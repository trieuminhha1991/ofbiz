package com.olbius.employment.services;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.ChineseCalendar;
import com.olbius.employment.helper.TimeManagerHelper;
import com.olbius.util.CommonServices;
import com.olbius.util.DateUtil;
import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class TimeManagerServices {
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListHolidayInYear (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String year = request.getParameter("year");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		TimeZone timeZone = TimeZone.getDefault();
		Locale locale = (Locale) context.get("locale");
		try {
			int yearTmp;
			Calendar cal = Calendar.getInstance();
			if(UtilValidate.isEmpty(year)){
				Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
				cal.setTime(nowtimestamp);
				yearTmp = cal.get(Calendar.YEAR);
			}else{
				yearTmp = Integer.parseInt(year);
			}
			cal.set(Calendar.YEAR, yearTmp);
			Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
			Timestamp fromDate = UtilDateTime.getYearStart(timestamp);
			Timestamp thruDate = UtilDateTime.getYearEnd(timestamp, timeZone, locale);
			Date fDate = new Date(fromDate.getTime());
			Date tDate = new Date(thruDate.getTime());
			listAllConditions.add(EntityCondition.makeCondition("dateHoliday", EntityOperator.LESS_THAN_EQUAL_TO, tDate));
			listAllConditions.add(EntityCondition.makeCondition("dateHoliday", EntityOperator.GREATER_THAN_EQUAL_TO, fDate));
			listSortFields.add("dateHoliday");
			listIterator = delegator.find("HolidayAndEmplTimekeepingSign", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List Holiday In Year  cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> createOrUpdateHolidayInYear(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Integer year = (Integer)context.get("year");
		Calendar cal = Calendar.getInstance();
		int currYear = cal.get(Calendar.YEAR);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		ChineseCalendar lunarCal = new ChineseCalendar();
		lunarCal.set(Calendar.HOUR, 0);
		lunarCal.set(Calendar.MINUTE, 0);
		lunarCal.set(Calendar.SECOND, 0);
		lunarCal.set(Calendar.MILLISECOND, 0);
		Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> holidayTypeList = delegator.findByAnd("HolidayConfig", null, null, false);
			for(GenericValue tempGv: holidayTypeList){							
				String description = (String)tempGv.get("description", locale);
				String calendarType = tempGv.getString("calendarType");
				Integer dateOfMonth = tempGv.getLong("dateOfMonth").intValue();
				Integer month = tempGv.getLong("month").intValue();
				if(dateOfMonth != null && month != null){
					if("LUNAR_CALENDAR".equals(calendarType)){
						int yearLunar = CommonServices.convertSolarYearToLunarYear(year, currYear);
						lunarCal.set(Calendar.MONTH, month - 1);
						lunarCal.set(Calendar.YEAR, yearLunar);
						int tempDateOfMonth = dateOfMonth;
						if(dateOfMonth > lunarCal.getActualMaximum(Calendar.DATE)){
							tempDateOfMonth = lunarCal.getActualMaximum(Calendar.DATE); 
						}
						cal.setTimeInMillis(lunarCal.getTimeInMillis());
						if(cal.get(Calendar.YEAR) > year){
							lunarCal.set(Calendar.YEAR, yearLunar - 1);
							if(dateOfMonth > lunarCal.getActualMaximum(Calendar.DATE)){
								tempDateOfMonth = lunarCal.getActualMaximum(Calendar.DATE); 
							}else{
								tempDateOfMonth = dateOfMonth;
							}
						}
						dateOfMonth = tempDateOfMonth;
						lunarCal.set(Calendar.DATE, dateOfMonth);
						cal.setTimeInMillis(lunarCal.getTimeInMillis());
					}else{					
						cal.set(Calendar.MONTH, month - 1);
						cal.set(Calendar.YEAR, year);
						if(dateOfMonth > cal.getActualMaximum(Calendar.DATE)){
							dateOfMonth = cal.getActualMaximum(Calendar.DATE);
						}
						cal.set(Calendar.DATE, dateOfMonth);
					}
					Map<String, Object> ctxMap = FastMap.newInstance();
					ctxMap.put("dateHoliday", new Date(cal.getTimeInMillis()));	
					ctxMap.put("emplTimekeepingSignId", tempGv.getString("emplTimekeepingSignId"));
					ctxMap.put("userLogin", context.get("userLogin"));
					if(description != null && description.indexOf("${date}") > -1){
						description = description.replace("${date}", String.valueOf(dateOfMonth));
					}
					ctxMap.put("holidayName", description);
					Map<String, Object> resultService = dispatcher.runSync("createHolidayInYear", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError((String)resultService.get(ModelService.ERROR_MESSAGE));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "AutoUpdateHolidaySuccess", locale));
	}
	
	public static Map<String, Object> createHolidayYear(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Date dateHoliday = (Date)context.get("dateHoliday");
		try {
			GenericValue holidays = delegator.findOne("Holiday", UtilMisc.toMap("dateHoliday", dateHoliday), false);
			if(holidays != null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateHoliday);
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "HolidayInDateCreated", 
						UtilMisc.toMap("dateHoliday", DateUtil.getDateMonthYearDesc(cal), "holidayName", holidays.getString("holidayName")), locale));
			}
			holidays = delegator.makeValue("Holiday");
			holidays.setAllFields(context, false, null, null);
			holidays.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListHolidayConfig(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
    	int page = Integer.parseInt(parameters.get("pagenum")[0]);
    	int start = size * page;
		int end = start + size;
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		Delegator delegator = dctx.getDelegator();
		listSortFields.add("description");
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		try {
			List<GenericValue> listHolidayConfig = delegator.findList("HolidayConfig", tmpCond, null, listSortFields, null, false);
			totalRows = listHolidayConfig.size();
			if(end > totalRows){
				end = totalRows;
			}
			listHolidayConfig = listHolidayConfig.subList(start, end);
			for(GenericValue tempGv: listHolidayConfig){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				tempMap.put("holidayConfigId", tempGv.get("holidayConfigId"));
				tempMap.put("emplTimekeepingSignId", tempGv.get("emplTimekeepingSignId"));
				tempMap.put("description", tempGv.get("description"));
				String calendarType = tempGv.getString("calendarType");
				if("LUNAR_CALENDAR".equals(calendarType)){
					tempMap.put("calendarType", true);
				}else{
					tempMap.put("calendarType", false);
				}
				Long dateOfMonth = tempGv.getLong("dateOfMonth");
				Long month = tempGv.getLong("month");
				tempMap.put("month", month);
				tempMap.put("dateOfMonth", dateOfMonth);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> createHolidayConfig(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue holidayConfig = delegator.makeValue("HolidayConfig");
		holidayConfig.setAllFields(context, false, null, null);
		String holidayConfigId = delegator.getNextSeqId("HolidayConfig");
		holidayConfig.set("holidayConfigId", holidayConfigId);
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			holidayConfig.create();
			retMap.put("holidayConfigId", holidayConfigId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}		
		return retMap;
	}
	
	public static Map<String, Object> deleteHolidayConfig(DispatchContext dctx, Map<String, Object> context){
		String holidayConfigId = (String)context.get("holidayConfigId");
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue holidayConfig = delegator.findOne("HolidayConfig", UtilMisc.toMap("holidayConfigId", holidayConfigId), false);
			if(holidayConfig != null){
				holidayConfig.remove();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public Map<String, Object> createHolidayInYear(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();		
		Locale locale = (Locale)context.get("locale");
		Date dateHoliday = (Date)context.get("dateHoliday");		
		try {
			GenericValue holidayCheck = delegator.findOne("Holiday", UtilMisc.toMap("dateHoliday", dateHoliday), false);
			if(holidayCheck != null){
				holidayCheck.setAllFields(context, false, null, null);
				holidayCheck.store();
			}else{
				GenericValue holiday = delegator.makeValue("Holiday");
				holiday.setAllFields(context, false, null, null);
				holiday.create();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		return retMap;
	}
	
	public static Map<String, Object> updateHolidayInYear(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String holidayId = (String)context.get("holidayId");
		Locale locale = (Locale)context.get("locale");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			GenericValue holiday = delegator.findOne("Holidays", UtilMisc.toMap("holidayId", holidayId), false);
			if(UtilValidate.isEmpty(holiday)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordHoliday", locale));
			}
			if(holiday.getTimestamp("fromDate").before(nowTimestamp)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "HolidayIsbeforeNow", locale));
			}
			holiday.setNonPKFields(context);
			holiday.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> deleteHolidayInYear(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Date dateHoliday = (Date)context.get("dateHoliday");
		Locale locale = (Locale)context.get("locale");
		//Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			GenericValue holiday = delegator.findOne("Holiday", UtilMisc.toMap("dateHoliday", dateHoliday), false);
			if(UtilValidate.isEmpty(holiday)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordHoliday", locale));
			}
			holiday.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListWorkingShift (DispatchContext dpct,Map<String,Object> context){
		Delegator delegator  =  (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		List<GenericValue> listIterator = null;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		result.put("listIterator", listReturn);
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
    	int page = Integer.parseInt(parameters.get("pagenum")[0]);
    	int start = size * page;
		int end = start + size;
		int totalRows = 0;
		listSortFields.add("-createdStamp");
		try {
			listIterator = delegator.findList("WorkingShift", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
			totalRows = listIterator.size();
			result.put("TotalRows", String.valueOf(totalRows));
			if(end > totalRows){
				end = totalRows;
			}
			listIterator = listIterator.subList(start, end);
			for(GenericValue tempGv: listIterator){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				tempMap.put("workingShiftId", tempGv.get("workingShiftId"));
				tempMap.put("workingShiftName", tempGv.get("workingShiftName"));
				tempMap.put("shiftStartTime", tempGv.get("shiftStartTime"));
				tempMap.put("shiftBreakStart", tempGv.get("shiftBreakStart"));
				tempMap.put("shiftBreakEnd", tempGv.get("shiftBreakEnd"));
				tempMap.put("shiftEndTime", tempGv.get("shiftEndTime"));
				tempMap.put("startOverTimeAfterShift", tempGv.get("startOverTimeAfterShift"));
				tempMap.put("endOverTimeAfterShift", tempGv.get("endOverTimeAfterShift"));
				tempMap.put("minMinuteOvertime", tempGv.get("minMinuteOvertime"));				
				tempMap.put("allowLateMinute", tempGv.get("allowLateMinute"));	
				tempMap.put("isAllowOTAfterShift", tempGv.get("isAllowOTAfterShift"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get List WorkingShift  cause : " + e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> getWorkingShiftDayWeek(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String workingShiftId = request.getParameter("workingShiftId");
		retMap.put("listIterator", listReturn);
		Delegator delegator = dctx.getDelegator();
		int totalRows = 0;
		if(workingShiftId != null){
			try {
				List<GenericValue> workingShiftDayWeek = delegator.findByAnd("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId), null, false);
				Map<String, Object> map = FastMap.newInstance();				
				map.put("workingShiftId", workingShiftId);
				for(GenericValue tempGv: workingShiftDayWeek){
					map.put(tempGv.getString("dayOfWeek"), tempGv.get("workTypeId"));
				}
				if(map.size() > 0){
					listReturn.add(map);
				}
				totalRows = listReturn.size();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> createWorkingShift(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String workingShiftId = (String)context.get("workingShiftId");
		if(workingShiftId == null){
			workingShiftId = delegator.getNextSeqId("WorkingShift");
		}
		Locale locale = (Locale)context.get("locale");
		String isAllowOTAfterShift = (String)context.get("isAllowOTAfterShift");
		if("Y".equals(isAllowOTAfterShift)){
			Time startOverTimeAfterShift = (Time)context.get("startOverTimeAfterShift");
			Time endOverTimeAfterShift = (Time)context.get("startOverTimeAfterShift");
			Long minMinuteOvertime = (Long)context.get("minMinuteOvertime");
			if(startOverTimeAfterShift == null || endOverTimeAfterShift == null || minMinuteOvertime == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "WorkOverTimeLackOfInfo", locale));
			}
		}		
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
			if(workingShift != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotCreateWorkingShiftExists", UtilMisc.toMap("workingShiftId", workingShiftId), locale));
			}
			workingShift = delegator.makeValue("WorkingShift");
			workingShift.setNonPKFields(context);
			workingShift.set("workingShiftId", workingShiftId);
			workingShift.create();
			retMap.put("workingShiftId", workingShiftId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateWorkingShift(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String workingShiftId = (String)context.get("workingShiftId");
		String isAllowOTAfterShift = (String)context.get("isAllowOTAfterShift");
		Locale locale = (Locale)context.get("locale");
		if("Y".equals(isAllowOTAfterShift)){
			Time startOverTimeAfterShift = (Time)context.get("startOverTimeAfterShift");
			Time endOverTimeAfterShift = (Time)context.get("startOverTimeAfterShift");
			Long minMinuteOvertime = (Long)context.get("minMinuteOvertime");
			if(startOverTimeAfterShift == null || endOverTimeAfterShift == null || minMinuteOvertime == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "WorkOverTimeLackOfInfo", locale));
			}
		}
		try {
			GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
			if(workingShift != null){
				workingShift.setNonPKFields(context);
				workingShift.store();
			}else{
				return ServiceUtil.returnError("cannot find working shift have workingShiftId: " + workingShiftId);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} 
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createWorkingShiftDayWeek(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String workingShiftId = (String)context.get("workingShiftId");
		String dayOfWeek = (String)context.get("dayOfWeek");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", 
					UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), false);
			if(workingShiftDayWeek != null){
				dispatcher.runSync("updateWorkingShiftDayWeek", context);
			}else{
				workingShiftDayWeek = delegator.makeValue("WorkingShiftDayWeek");
				workingShiftDayWeek.setAllFields(context, false, null, null);
				workingShiftDayWeek.create();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateWorkingShiftDayWeek(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String workingShiftId = (String)context.get("workingShiftId");
		String dayOfWeek = (String)context.get("dayOfWeek");
		try {
			GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", 
					UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), false);
			if(workingShiftDayWeek != null){
				workingShiftDayWeek.setNonPKFields(context);
				workingShiftDayWeek.store();
			}else{
				return ServiceUtil.returnError("cannot find workingShiftDayWeek have id: " + workingShiftId);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getPartyWorkingShift(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String rootPartyId = (String)context.get("partyId");
		retMap.put("listReturn", listReturn);
		if(rootPartyId == null){
			rootPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, rootPartyId, false, false);
			Map<String, Object> map = FastMap.newInstance();
			map.put("partyId", rootPartyId);
			map.put("partyIdFrom", "-1");
			map.put("partyName", PartyHelper.getPartyName(delegator, rootPartyId, false));
			map.put("expanded", true);
			List<GenericValue> workingShiftParty = delegator.findByAnd("WorkingShiftConfigAndParty", UtilMisc.toMap("partyId", rootPartyId), null, false);
			if(UtilValidate.isNotEmpty(workingShiftParty)){
				map.put("workingShiftId", workingShiftParty.get(0).getString("workingShiftId"));
				map.put("workingShiftName", workingShiftParty.get(0).getString("workingShiftName"));
			}
			listReturn.add(map);
			TimeManagerHelper.getPartyHierarchyWorkingShift(delegator, buildOrg, listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> editWorkingShiftPartyConfig(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		try {
			GenericValue workingShiftPartyConfig = delegator.findOne("WorkingShiftPartyConfig", UtilMisc.toMap("partyId", partyId), false);
			if(workingShiftPartyConfig == null){
				workingShiftPartyConfig = delegator.makeValue("WorkingShiftPartyConfig");
				workingShiftPartyConfig.set("partyId", partyId);
			}
			workingShiftPartyConfig.setNonPKFields(context);
			delegator.createOrStore(workingShiftPartyConfig);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWorkingShiftEmployee(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		Delegator delegator = dctx.getDelegator();
		if(partyGroupId == null){
			partyGroupId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			totalRows = emplList.size();
			if(end > totalRows){
				end = totalRows;
			}
			emplList = emplList.subList(start, end);
			Calendar cal = Calendar.getInstance();
			for(GenericValue tempGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				String partyId = tempGv.getString("partyId");
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));				
				Timestamp tempTimestamp = fromDate;
				List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
				List<String> emplPosType = EntityUtil.getFieldListFromEntityList(emplPosList, "description", true);
				tempMap.put("emplPositionTypeId", StringUtils.join(emplPosType, ", "));
				List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				List<String> departmentName = FastList.newInstance();
				for(String departmentId: departmentList){
					GenericValue department = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", departmentId), false);
					departmentName.add(department.getString("groupName"));
				}
				tempMap.put("orgId", StringUtils.join(departmentName, ", "));
				while(tempTimestamp.before(thruDate)){
					Date tempDate = new Date(tempTimestamp.getTime());
					cal.setTime(tempDate);
					String dataField = String.valueOf(cal.get(Calendar.DATE)) + String.valueOf(cal.get(Calendar.MONTH) + 1) + String.valueOf(cal.get(Calendar.YEAR));
					tempMap.put("date_" + dataField, tempDate.getTime());
					Timestamp fromDateInOrg = tempGv.getTimestamp("fromDate");
					Timestamp thruDateInOrg = tempGv.getTimestamp("thruDate");
					if(DateUtil.checkTimestampInPeriod(tempTimestamp, fromDateInOrg, thruDateInOrg)){
						GenericValue workingShiftEmplPos = delegator.findOne("WorkingShiftEmployee", 
								UtilMisc.toMap("partyId", partyId, "dateWork", tempDate), false);
						if(workingShiftEmplPos != null){
							tempMap.put("ws_" + dataField, workingShiftEmplPos.getString("workingShiftId"));
						}
					}else{
						tempMap.put("ws_" + dataField, "EXPIRE");
					}
					tempTimestamp = UtilDateTime.getNextDayStart(tempTimestamp);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> editWorkingShiftEmployeeInPeriod(DispatchContext dctx, Map<String, Object> context){
		Date fromDate = (Date)context.get("fromDate");
		Date thruDate = (Date)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		String workingShiftId = (String)context.get("workingShiftId");
		Delegator delegator = dctx.getDelegator();
		Calendar cal = Calendar.getInstance();
		while(fromDate.before(thruDate)){
			cal.setTime(fromDate);
			try {
				GenericValue workingShiftEmployee = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", fromDate), false);
				if(workingShiftEmployee == null){
					workingShiftEmployee = delegator.makeValue("WorkingShiftEmployee");
					workingShiftEmployee.set("partyId", partyId);
					workingShiftEmployee.set("dateWork", fromDate);
				}
				workingShiftEmployee.set("workingShiftId", workingShiftId);
				delegator.createOrStore(workingShiftEmployee);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			cal.add(Calendar.DATE, 1);
			fromDate = new Date(cal.getTimeInMillis());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateWorkingShiftEmployee(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		Date dateWork = (Date)context.get("dateWork");
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue workingShiftEmployee = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", dateWork), false);
			if(workingShiftEmployee == null){
				workingShiftEmployee = delegator.makeValue("WorkingShiftEmployee");
				workingShiftEmployee.set("partyId", partyId);
				workingShiftEmployee.set("dateWork", dateWork);
			}
			workingShiftEmployee.setNonPKFields(context);
			delegator.createOrStore(workingShiftEmployee);
		} catch (GenericEntityException e) {			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
}
