package com.olbius.basehr.timekeeping.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.employee.services.EmployeeLeaveServices;
import com.olbius.basehr.importExport.ImportExportWorker;
import com.olbius.basehr.util.DateUtil;

public class TimekeepingHelper {
	public static int importTimekeepingDetailParty(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Locale locale,
			String timekeepingDetailId, ByteBuffer uploadedFile, Map<Integer, Object> columnExcelMap, Integer sheetIndex, int startLine) throws IOException {
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		context.put("timekeepingDetailId", timekeepingDetailId);
		ByteArrayInputStream bais = new ByteArrayInputStream(uploadedFile.array());
		Workbook wb = new XSSFWorkbook(bais);
		Sheet sheetImport = wb.getSheetAt(sheetIndex);
		int rows = sheetImport.getLastRowNum();
		int cols = 0; // No of columns
		Map<String, Object> resultService = null;
		int totalRowImportSuccess = 0;
		for(int i = startLine; i <= rows; i++){
			Row row = sheetImport.getRow(i);
			if(!ImportExportWorker.isEmptyRow(row)){
				cols = row.getLastCellNum();
				List<GenericValue> timekeepingDetailPartyList = FastList.newInstance();
				for (int c = 0; c < cols; c++) {
					if(columnExcelMap.get(c) != null){
						Object fieldValue = columnExcelMap.get(c);
						Cell cell = row.getCell(c);
						Object cellValue = ImportExportWorker.getCellValue(cell);
						if(!("partyCode".equals(fieldValue)) &&
                                ((cell==null && cellValue==null)||
                                ("".equals(cell.toString()) && cellValue==null) ||
                                (cell==null && "".equals(cellValue.toString())) ||
                                ("".equals(cell.toString()) && "".equals(cellValue.toString())))
                                ){
                            GenericValue timekeepingDetailParty = delegator.makeValue("TimekeepingDetailPartyAndParty");
                            Double workdayActual = Double.valueOf(0);
                            timekeepingDetailParty.put("dateTimekeeping", fieldValue);
                            timekeepingDetailParty.put("workdayActual", workdayActual);
                            timekeepingDetailPartyList.add(timekeepingDetailParty);
						}else if(cell != null && cellValue != null){
                            if("partyCode".equals(fieldValue) && cellValue instanceof String){
                                context.put("partyCode", cellValue);
                            }else if(cellValue instanceof Double){
                                GenericValue timekeepingDetailParty = delegator.makeValue("TimekeepingDetailPartyAndParty");
                                Double workdayActual = (Double)cellValue;
                                timekeepingDetailParty.put("dateTimekeeping", fieldValue);
                                timekeepingDetailParty.put("workdayActual", workdayActual);
                                timekeepingDetailPartyList.add(timekeepingDetailParty);
                            }
                        }
					}
				}
				context.put("timekeepingDetailPartyList", timekeepingDetailPartyList);
				try {
					resultService = dispatcher.runSync("createTimekeepingDetailPartyInPeriod", context);
					if(ServiceUtil.isSuccess(resultService)){
						totalRowImportSuccess++;
					}
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
			}
		}
		return totalRowImportSuccess;
	}

	public static Map<String, Boolean> checkDayIsDayLeaveOfParty(Delegator delegator, String partyId, String defaultWorkingShiftId, Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		boolean isDayLeave = false;
		Map<String, Boolean> retMap = FastMap.newInstance();
		String workingShiftId = null;
		try {
			//check whether dateCheck in holiday or not
			GenericValue dateCheckInHoliday = delegator.findOne("Holiday", UtilMisc.toMap("dateHoliday", date), true);
			GenericValue workingShiftEmployee = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", date), false);
			if(workingShiftEmployee != null){
				workingShiftId = workingShiftEmployee.getString("workingShiftId");
			}else{
				workingShiftId = defaultWorkingShiftId;
			}
			if(workingShiftId != null){
				String dayOfWeek = DateUtil.getDayName(cal.get(Calendar.DAY_OF_WEEK));
				GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), false);
				if(workingShiftDayWeek != null){
					String workTypeId = workingShiftDayWeek.getString("workTypeId");
					if("DAY_OFF".equals(workTypeId)){
						isDayLeave = true;
					}
				}
			}
			if(UtilValidate.isNotEmpty(dateCheckInHoliday)){
				retMap.put("isDayLeave", true);
				retMap.put("isHoliday", true);
				return retMap;
			}
			retMap.put("isDayLeave", isDayLeave);			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Double getTotalWorkdayWhenEmplLeave(Delegator delegator, String partyId, Date date) throws GenericEntityException {
		Timestamp timestamp = new Timestamp(date.getTime());
		return getTotalWorkdayWhenEmplLeave(delegator, partyId, timestamp);
	}
	
	public static Double getTotalWorkdayWhenEmplLeave(Delegator delegator, String partyId, Timestamp moment) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, moment));
		conds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, moment));
		conds.add(EntityCondition.makeCondition("statusId", "LEAVE_APPROVED"));
		conds.add(EntityCondition.makeCondition("emplLeaveReasonTypeId", EntityJoinOperator.IN, UtilMisc.toList("NGHI_PHEP", "NGHI_CUOI", "NGHI_HN_HT", "NGHI_BU")));
		List<GenericValue> listEmplLeave = delegator.findList("EmplLeave", EntityCondition.makeCondition(conds), null, null, null, false);
		if(UtilValidate.isEmpty(listEmplLeave)){
			return null;
		}
		GenericValue emplLeave = listEmplLeave.get(0);
		String workingShiftId = emplLeave.getString("workingShiftId");
		Timestamp thruDateLeave = emplLeave.getTimestamp("thruDate");
		Timestamp fromDateLeave = emplLeave.getTimestamp("fromDate");
		Timestamp endDateMoment = UtilDateTime.getDayEnd(moment);
		Timestamp startDateMoment = UtilDateTime.getDayStart(moment);
		String fromDateLeaveTypeId = emplLeave.getString("fromDateLeaveTypeId");
		String thruDateLeaveTypeId = emplLeave.getString("thruDateLeaveTypeId");
		Date date = new Date(moment.getTime());
		String dayOfWeek = DateUtil.getDayName(date);
		GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), false);
		if(workingShiftDayWeek == null){
			return null;
		}
		String workTypeId = workingShiftDayWeek.getString("workTypeId");
		Double workdayLeavePaid = null;
		if("SECOND_HALF_SHIFT".equals(workTypeId)){
			return 0.5d;
		}else if("FIRST_HALF_SHIFT".equals(workTypeId)){
			return 0.5d;
		}else if("ALL_SHIFT".equals(workTypeId)){
			if(fromDateLeave.compareTo(startDateMoment) == 0){
				if(EmployeeLeaveServices.SECOND_HALF_DAY.equals(fromDateLeaveTypeId)){
					workdayLeavePaid = 0.5d;
				}else{
					if(thruDateLeave.compareTo(endDateMoment) == 0 && EmployeeLeaveServices.FIRST_HALF_DAY.equals(thruDateLeaveTypeId)){
						workdayLeavePaid = 0.5d;
					}else{
						workdayLeavePaid = 1d;
					}
				}
			}else if(thruDateLeave.compareTo(endDateMoment) == 0){
				if(EmployeeLeaveServices.FIRST_HALF_DAY.equals(thruDateLeaveTypeId)){
					workdayLeavePaid = 0.5d;
				}else if(EmployeeLeaveServices.SECOND_HALF_DAY.equals(thruDateLeaveTypeId)){
					workdayLeavePaid = 1d;
				}
			}else{
				workdayLeavePaid = 1d;
			}
		}
		return workdayLeavePaid;
	}
	
	public static List<Date> getListHolidayInMonth(Delegator delegator, int month, int year) throws GenericEntityException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DATE, 1);
		Date startDate = new Date(cal.getTimeInMillis());
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = new Date(cal.getTimeInMillis());
		List<GenericValue> listHoliday = delegator.findList("Holiday", EntityCondition.makeCondition(
				EntityCondition.makeCondition("dateHoliday", EntityJoinOperator.LESS_THAN_EQUAL_TO, endDate),
				EntityJoinOperator.AND,
				EntityCondition.makeCondition("dateHoliday", EntityJoinOperator.GREATER_THAN_EQUAL_TO, startDate)), null, null, null, false);
		if(UtilValidate.isNotEmpty(listHoliday)){
			List<Date> holidayDate = EntityUtil.getFieldListFromEntityList(listHoliday, "dateHoliday", true);
			return holidayDate;
		}
		return null;
	}
	public static Map<String, Object> createTimekeepingSummaryParty(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String timekeepingSummaryId, 
			Long month, Long year, List<GenericValue> listTimekeepingDetailOfParty, String partyId, Map<String, String> workingShiftWorkType, 
			String defaultWorkingShiftId, List<Date> holidayList, Locale locale, TimeZone timeZone) throws GenericServiceException, GenericEntityException {
		Double workdayActual = 0d, workdayLeavePaid = 0d, workdayStandard = 0d, 
				overtimeHoursNormal = 0d, overtimeHoursWeekend = 0d, overtimeHoursHoliday = 0d;
		Long totalWorkLate = 0l, totalMinuteLate = 0l;
		Map<String, Object> resultService = ServiceUtil.returnSuccess();
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("workingShiftId", defaultWorkingShiftId);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month.intValue());
		cal.set(Calendar.YEAR, year.intValue());
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp startMonth = UtilDateTime.getMonthStart(timestamp);
		Timestamp endMonth = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
		while(startMonth.compareTo(endMonth) <= 0){
			Date tempDate = new Date(startMonth.getTime());
			if(holidayList == null || !holidayList.contains(tempDate)){
				String dayOfWeek = DateUtil.getDayName(tempDate);
				GenericValue workingShiftEmpl = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", tempDate), false);
				String workTypeId = null;
				if(workingShiftEmpl == null || workingShiftEmpl.getString("workingShiftId").equals(defaultWorkingShiftId)){
					workTypeId = workingShiftWorkType.get(dayOfWeek);
				}else{
					String workingShiftId = workingShiftEmpl.getString("workingShiftId");
					GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), false);
					if(workingShiftDayWeek != null){
						workTypeId = workingShiftDayWeek.getString("workTypeId");
					}
				}
				if("FIRST_HALF_SHIFT".equals(workTypeId) || "SECOND_HALF_SHIFT".equals(workTypeId)){
					workdayStandard += 0.5d;
				}else if("ALL_SHIFT".equals(workTypeId)){
					workdayStandard += 1d;
				}
			}
			startMonth = UtilDateTime.getDayStart(startMonth, 1);
		}
		for(GenericValue tempGv: listTimekeepingDetailOfParty){
			//Date dateTimekeeping = tempGv.getDate("dateTimekeeping");
			Double tempWorkdayLeavePaid = tempGv.getDouble("workdayLeavePaid");
			Double tempWorkdayActual = tempGv.getDouble("workdayActual");
			Double overtimeHours = tempGv.getDouble("overtimeHours");
			Long workingLateMinutes = tempGv.getLong("workingLateMinutes");
			if(workingLateMinutes != null && workingLateMinutes > 0){
				totalMinuteLate += workingLateMinutes;
				totalWorkLate++;
			}
			if(tempWorkdayActual != null){
				workdayActual += tempWorkdayActual;
			}
			if(tempWorkdayLeavePaid != null){
				workdayLeavePaid += tempWorkdayLeavePaid;
			}
			if(overtimeHours != null && overtimeHours > 0){
				String overtimeEnumId = tempGv.getString("overtimeEnumId");
				if("OT_NORMAL".equals(overtimeEnumId)){
					overtimeHoursNormal += overtimeHours;
				}else if("OT_WEEKEND".equals(overtimeEnumId)){
					overtimeHoursWeekend += overtimeHours;
				}else if("OT_HOLIDAY".equals(overtimeEnumId)){
					overtimeHoursHoliday += overtimeHours;
				}
			}
		}
		
		GenericValue timekeepingSummaryParty = delegator.makeValue("TimekeepingSummaryParty");
		timekeepingSummaryParty.put("timekeepingSummaryId", timekeepingSummaryId);
		timekeepingSummaryParty.put("partyId", partyId);
		timekeepingSummaryParty.put("workdayActual", workdayActual);
		timekeepingSummaryParty.put("workdayLeavePaid", workdayLeavePaid);
		timekeepingSummaryParty.put("workdayStandard", workdayStandard);
		timekeepingSummaryParty.put("overtimeHoursNormal", overtimeHoursNormal);
		timekeepingSummaryParty.put("overtimeHoursWeekend", overtimeHoursWeekend);
		timekeepingSummaryParty.put("overtimeHoursHoliday", overtimeHoursHoliday);
		timekeepingSummaryParty.put("totalMinuteLate", totalMinuteLate);
		timekeepingSummaryParty.put("totalWorkLate", totalWorkLate);
		delegator.createOrStore(timekeepingSummaryParty);
		return resultService;
	}
	public static Double getTotalHoursWorkOvertime(
			GenericValue timekeepingSummaryParty, String type) {
		if(type == null){
			return null;
		}
		switch (type) {
			case "LAM_THEM_NGAY_THUONG":
				return timekeepingSummaryParty.getDouble("overtimeHoursNormal");
			case "LAM_THEM_NGAY_NGHI":
				return timekeepingSummaryParty.getDouble("overtimeHoursWeekend");
			case "LAM_THEM_NGAY_LE":
				return timekeepingSummaryParty.getDouble("overtimeHoursHoliday");
			default:
				break;
		}
		return null;
	}
}
