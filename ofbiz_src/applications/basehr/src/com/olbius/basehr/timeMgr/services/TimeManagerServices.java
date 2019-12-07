package com.olbius.basehr.timeMgr.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import org.ofbiz.entity.model.ModelEntity;
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
import com.olbius.basehr.timeMgr.helper.TimeManagerHelper;
import com.olbius.basehr.common.services.CommonServices;
import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.employee.services.EmployeeLeaveServices;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PersonHelper;

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
		lunarCal.set(com.ibm.icu.util.Calendar.HOUR, 0);
		lunarCal.set(com.ibm.icu.util.Calendar.MINUTE, 0);
		lunarCal.set(com.ibm.icu.util.Calendar.SECOND, 0);
		lunarCal.set(com.ibm.icu.util.Calendar.MILLISECOND, 0);
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
						lunarCal.set(com.ibm.icu.util.Calendar.MONTH, month - 1);
						lunarCal.set(com.ibm.icu.util.Calendar.YEAR, yearLunar);
						int tempDateOfMonth = dateOfMonth;
						if(dateOfMonth > lunarCal.getActualMaximum(com.ibm.icu.util.Calendar.DATE)){
							tempDateOfMonth = lunarCal.getActualMaximum(com.ibm.icu.util.Calendar.DATE); 
						}
						lunarCal.set(com.ibm.icu.util.Calendar.DATE, tempDateOfMonth);
						cal.setTimeInMillis(lunarCal.getTimeInMillis());
						if(cal.get(Calendar.YEAR) > year){
							yearLunar--;
							lunarCal.set(com.ibm.icu.util.Calendar.DATE, 1);
							lunarCal.set(com.ibm.icu.util.Calendar.YEAR, yearLunar);
							if(dateOfMonth > lunarCal.getActualMaximum(com.ibm.icu.util.Calendar.DATE)){
								tempDateOfMonth = lunarCal.getActualMaximum(com.ibm.icu.util.Calendar.DATE); 
							}else{
								tempDateOfMonth = dateOfMonth;
							}
							lunarCal.set(com.ibm.icu.util.Calendar.DATE, tempDateOfMonth);
						}
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "AutoUpdateHolidaySuccess", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "HolidayInDateCreated", 
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
		try {
			List<GenericValue> listHolidayConfig = delegator.findList("HolidayConfigAndEmplTimekeepingSign", tmpCond, null, listSortFields, null, false);
//			totalRows = listHolidayConfig.size();
//			if(end > totalRows){
//				end = totalRows;
//			}
//			listHolidayConfig = listHolidayConfig.subList(start, end);
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("holidayConfigId");
			listFieldInEntity.add("emplTimekeepingSignId");
			listFieldInEntity.add("description");
			listFieldInEntity.add("sign");
			listFieldInEntity.add("descriptionSign");
			listFieldInEntity.add("calendarType");
			listFieldInEntity.add("month");
			listFieldInEntity.add("dateOfMonth");
			
			
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				listHolidayConfig = EntityConditionUtils.doFilterGenericValue(listHolidayConfig, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("holidayConfigId");
			}
			listHolidayConfig = EntityUtil.orderBy(listHolidayConfig, sortedFieldInEntity);
			
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = listHolidayConfig.size();
				if(end > listHolidayConfig.size()){
					end = listHolidayConfig.size();
				}
				listHolidayConfig = listHolidayConfig.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			if(end > listHolidayConfig.size()){
				end  = listHolidayConfig.size();
			}
			
			for(GenericValue tempGv: listHolidayConfig){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("holidayConfigId", tempGv.get("holidayConfigId"));
				tempMap.put("emplTimekeepingSignId", tempGv.get("emplTimekeepingSignId"));
				tempMap.put("description", tempGv.get("description"));
				tempMap.put("sign", tempGv.get("sign"));
				tempMap.put("descriptionSign", tempGv.get("descriptionSign"));
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
				listReturn.add(tempMap);
			}
			if(isFilterAdvance){
				if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
					listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
				}
				if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
					listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
				}
				totalRows = listReturn.size();
				if(end > listReturn.size()){
					end = listReturn.size();
				}
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put("listIterator", listReturn);
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
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordHoliday", locale));
			}
			if(holiday.getTimestamp("fromDate").before(nowTimestamp)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "HolidayIsbeforeNow", locale));
			}
			holiday.setNonPKFields(context);
			holiday.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> deleteHolidayInYear(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Date dateHoliday = (Date)context.get("dateHoliday");
		Locale locale = (Locale)context.get("locale");
		//Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			GenericValue holiday = delegator.findOne("Holiday", UtilMisc.toMap("dateHoliday", dateHoliday), false);
			if(UtilValidate.isEmpty(holiday)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordHoliday", locale));
			}
			holiday.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
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
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWorkingShiftDayWeek(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String workingShiftId = request.getParameter("workingShiftId");
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<EntityCondition> listAllConditions= (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = page*size;
		int end = start + size;
		int totalRows = 0;
		if(workingShiftId != null){
			try {
				List<GenericValue> workingShiftDayWeek = delegator.findByAnd("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId), null, false);
				
				List<String> listFieldInEntity = FastList.newInstance();
				listFieldInEntity.add("workTypeId");
				
				
				List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
				List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
				EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
				
				List<String> sortedFieldInEntity = FastList.newInstance();
				List<String> sortedFieldNotInEntity = FastList.newInstance();
				if(listSortFields != null){
					EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
				}
				
				if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
					workingShiftDayWeek = EntityConditionUtils.doFilterGenericValue(workingShiftDayWeek, condsForFieldInEntity);
				}
				if(UtilValidate.isEmpty(sortedFieldInEntity)){
					sortedFieldInEntity.add("workTypeId");
				}
				workingShiftDayWeek = EntityUtil.orderBy(workingShiftDayWeek, sortedFieldInEntity);
				
				boolean isFilterAdvance = false;
				if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
					totalRows = workingShiftDayWeek.size();
					if(end > workingShiftDayWeek.size()){
						end = workingShiftDayWeek.size();
					}
					workingShiftDayWeek = workingShiftDayWeek.subList(start, end);
				}else{
					isFilterAdvance = true;
				}
				if(end > workingShiftDayWeek.size()){
					end  = workingShiftDayWeek.size();
				}
				Map<String, Object> map = FastMap.newInstance();				
				map.put("workingShiftId", workingShiftId);
				for(GenericValue tempGv: workingShiftDayWeek){
					map.put(tempGv.getString("dayOfWeek"), tempGv.get("workTypeId"));
				}
				if(map.size() > 0){
					listReturn.add(map);
				}
				if(isFilterAdvance){
					if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
						listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
					}
					if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
						listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
					}
					totalRows = listReturn.size();
					if(end > listReturn.size()){
						end = listReturn.size();
					}
					listReturn = listReturn.subList(start, end);
				}
				totalRows = listReturn.size();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		retMap.put("listIterator", listReturn);
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "WorkOverTimeLackOfInfo", locale));
			}
		}		
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
			if(workingShift != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "CannotCreateWorkingShiftExists", UtilMisc.toMap("workingShiftId", workingShiftId), locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "WorkOverTimeLackOfInfo", locale));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			if(rootPartyId == null){
				rootPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, rootPartyId, false, false);
			Map<String, Object> map = FastMap.newInstance();
			map.put("partyId", rootPartyId);
			map.put("partyCode", PartyUtil.getPartyCode(delegator, rootPartyId));
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
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWorkingShiftEmployee(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		Delegator delegator = dctx.getDelegator();
		
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
//		String partyIdParam = (String[])parameters.get("partyId") != null? ((String[])parameters.get("partyId"))[0] : null;
//    	String partyNameParam = (String[])parameters.get("partyName") != null? ((String[])parameters.get("partyName"))[0]: null;
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			if(partyGroupId == null){
				partyGroupId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("partyId");
			
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listSortFields)){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}else{
				sortedFieldInEntity.add("firstName");
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				emplList = EntityConditionUtils.doFilterGenericValue(emplList, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("partyId");
			}
			emplList = EntityUtil.orderBy(emplList, sortedFieldInEntity);
			
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = emplList.size();
				if(end > emplList.size()){
					end = emplList.size();
				}
				emplList = emplList.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			if(end > emplList.size()){
				end  = emplList.size();
			}
			
			Calendar cal = Calendar.getInstance();
			for(GenericValue tempGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				tempMap.put("partyId", partyId);
				tempMap.put("partyCode", tempGv.get("partyCode"));
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
					String dataField = DateUtil.getDateTowDigits(cal) + DateUtil.getMonthTowDigits(cal) + String.valueOf(cal.get(Calendar.YEAR));
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
				listReturn.add(tempMap);
			}
			if(isFilterAdvance){
				if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
					listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
				}
				if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
					listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
				}
				totalRows = listReturn.size();
				if(end > listReturn.size()){
					end = listReturn.size();
				}
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put("listIterator", listReturn);
		retMap.put("TotalRows", String.valueOf(totalRows));
		
		return retMap;
	}
	
	public static Map<String, Object> editWorkingShiftEmployeeInPeriod(DispatchContext dctx, Map<String, Object> context){
		Date fromDate = (Date)context.get("fromDate");
		Date thruDate = (Date)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		String workingShiftId = (String)context.get("workingShiftId");
		String isOverride = (String)context.get("isOverride");
		Delegator delegator = dctx.getDelegator();
		Calendar cal = Calendar.getInstance();
		try {
			PersonHelper.getDateEmplJoinOrg(delegator, partyId);
			while(fromDate.before(thruDate)){
				cal.setTime(fromDate);
					GenericValue workingShiftEmployee = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", fromDate), false);
					if(workingShiftEmployee == null){
						workingShiftEmployee = delegator.makeValue("WorkingShiftEmployee");
						workingShiftEmployee.set("partyId", partyId);
						workingShiftEmployee.set("dateWork", fromDate);
						workingShiftEmployee.set("workingShiftId", workingShiftId);
						delegator.create(workingShiftEmployee);
					}else if("Y".equals(isOverride)){
						workingShiftEmployee.set("workingShiftId", workingShiftId);
						delegator.store(workingShiftEmployee);
					}
				cal.add(Calendar.DATE, 1);
				fromDate = new Date(cal.getTimeInMillis());
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAnnualLeaveSheet(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//GenericValue userLogin = (GenericValue)context.get("userLogin");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		String yearStr = request.getParameter("year");
		String partyId = request.getParameter("partyId");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(yearStr));
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp yearStart = UtilDateTime.getYearStart(timestamp);
		Timestamp yearEnd = UtilDateTime.getYearEnd(yearStart, timeZone, locale);
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, yearStart, yearEnd);
			List<String> emplIdList = EntityUtil.getFieldListFromEntityList(emplList, "partyId", false);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplIdList));
			listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, yearStart));
			listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, yearEnd));
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			List<GenericValue> emplLeaveRegulationList = delegator.findList("EmplLeaveRegulationAndDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, null, false);
			totalRows = emplLeaveRegulationList.size();
			if(end > emplLeaveRegulationList.size()){
				end  = emplLeaveRegulationList.size();
			}
			
			for(GenericValue tempGv: emplLeaveRegulationList){
				Map<String, Object> tempMap = tempGv.getAllFields();
				BigDecimal totalDayLeave = tempGv.getBigDecimal("totalDayLeave");
				BigDecimal annualLeftDay = EmployeeHelper.getTotalDayLeftOfEmplInYear(delegator, tempGv.getString("partyId"), yearStart, yearEnd);
				tempMap.put("annualLeftDay", annualLeftDay);
				BigDecimal remainingDay = BigDecimal.ZERO;
				remainingDay = totalDayLeave.subtract(annualLeftDay);
				tempMap.put("remainDay", remainingDay);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplLeaveJQ(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator listIterator = null;
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		String yearStr = request != null ? request.getParameter("year") : (parameters.containsKey("year") ? parameters.get("year")[0] : null);
		String partyId = request != null ? request.getParameter("partyId") : (parameters.containsKey("partyId") ? parameters.get("partyId")[0] : null) ;
		
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Calendar cal = Calendar.getInstance();
		Integer year = null;
		try{
			year = Integer.parseInt(yearStr);
		}catch(NumberFormatException e){
			year = cal.get(Calendar.YEAR);
		}
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp yearStart = UtilDateTime.getYearStart(timestamp);
		Timestamp yearEnd = UtilDateTime.getYearEnd(yearStart, timeZone, locale);
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			List<GenericValue> allDeptList = buildOrg.getAllDepartmentList(delegator);
			List<String> allDeptId = null;
			if(UtilValidate.isNotEmpty(allDeptList)){
				allDeptId = EntityUtil.getFieldListFromEntityList(allDeptList, "partyId", true);
			}else{
				allDeptId = FastList.newInstance();
			}
			allDeptId.add(partyId);
			listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, yearStart));
			listAllConditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, yearEnd));
			if(!PartyUtil.isFullPermissionAction(delegator, userLogin.getString("userLoginId"))){
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_EQUAL, userLogin.get("partyId")));
			}
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("partyId");
			}
			ModelEntity emplLeaveModel = delegator.getModelEntity("EmplLeaveAndPartyRelPosGroupBy");
			List<String> emplLeaveFields = emplLeaveModel.getAllFieldNames();
			Set<String> selectedField = new HashSet<String>(emplLeaveFields);
			selectedField.remove("partyIdFrom");
			listIterator = delegator.find("EmplLeaveAndPartyRelPosGroupBy", EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.IN, allDeptId), EntityCondition.makeCondition(listAllConditions), selectedField, listSortFields, opts);
			List<GenericValue> emplLeaveList = listIterator.getCompleteList();
			listIterator.close();
			totalRows = emplLeaveList.size();
			if(end > totalRows){
				end = totalRows;
			}
			emplLeaveList = emplLeaveList.subList(start, end);
			for(GenericValue emplLeave: emplLeaveList){
				Map<String, Object> tempMap = emplLeave.getAllFields();
				String workingShiftId = emplLeave.getString("workingShiftId");
				Float totalDayLeave = EmployeeHelper.getNbrDayLeave(delegator, emplLeave);
				tempMap.put("nbrDayLeave", totalDayLeave);
				Timestamp fromDateLeave = EmployeeHelper.getTimeEmplLeave(delegator, workingShiftId, emplLeave.getTimestamp("fromDate"), 
						emplLeave.getString("fromDateLeaveTypeId"), EmployeeLeaveServices.START_LEAVE);
				Timestamp thruDateLeave = EmployeeHelper.getTimeEmplLeave(delegator, workingShiftId, emplLeave.getTimestamp("thruDate"), 
						emplLeave.getString("thruDateLeaveTypeId"), EmployeeLeaveServices.END_LEAVE);
				tempMap.put("fromDate", fromDateLeave.getTime());
				tempMap.put("thruDate", thruDateLeave.getTime());
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	public static Map<String, Object> createEmplLeaveRegulation(DispatchContext dctx, Map<String, Object> context){
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String yearStr = (String)context.get("year");
		String isTransferredAnnualLastYear = (String)context.get("isTransferredAnnualLastYear");
		Integer year = Integer.parseInt(yearStr);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp yearStart = UtilDateTime.getYearStart(timestamp);
		Timestamp yearEnd = UtilDateTime.getYearEnd(yearStart, timeZone, locale);
		Timestamp lastYearStart = UtilDateTime.getYearStart(timestamp, 0, -1);
		Timestamp lastYearEnd = UtilDateTime.getYearEnd(lastYearStart, timeZone, locale);
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, yearStart, yearEnd);
			for(GenericValue empl: emplList){
				String tempPartyId = empl.getString("partyId");
				List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, tempPartyId, yearStart, yearEnd);
				BigDecimal leaveDayYear = BigDecimal.ZERO;
				for(GenericValue posType: emplPosList){
					String emplPositionTypeId = posType.getString("emplPositionTypeId");
					GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
					Long dayLeaveRegulation = emplPositionType.getLong("dayLeaveRegulation");
					if(dayLeaveRegulation != null){
						BigDecimal tempLeaveDayYear = new BigDecimal(dayLeaveRegulation);
						if(leaveDayYear.compareTo(tempLeaveDayYear) < 0){
							leaveDayYear = tempLeaveDayYear;
						}
					}
				}
				BigDecimal lastYearLeft = BigDecimal.ZERO;
				BigDecimal lastYearTransferred = BigDecimal.ZERO;
				GenericValue emplLeaveRegulationLastYear = delegator.findOne("EmplLeaveRegulation", UtilMisc.toMap("partyId", tempPartyId, "fromDate", lastYearStart), false);
				if(emplLeaveRegulationLastYear != null){
					BigDecimal totalDayLeftOfEmplInLastYear = EmployeeHelper.getTotalDayLeftOfEmplInYear(delegator, tempPartyId, lastYearStart, lastYearEnd);
					BigDecimal totalAnnualDayLeaveInLastYear = BigDecimal.ZERO;
					totalAnnualDayLeaveInLastYear = EmployeeHelper.getTotalAnnualDayLeaveInYear(emplLeaveRegulationLastYear.getBigDecimal("lastYearTransferred"), 
							emplLeaveRegulationLastYear.getBigDecimal("leaveDayYear"), emplLeaveRegulationLastYear.getBigDecimal("grantedLeave"));
					lastYearLeft = totalAnnualDayLeaveInLastYear.subtract(totalDayLeftOfEmplInLastYear);
				}
				if("Y".equals(isTransferredAnnualLastYear)){
					lastYearTransferred = lastYearLeft;
				}
				GenericValue emplLeaveRegulation = delegator.makeValue("EmplLeaveRegulation");
				emplLeaveRegulation.set("partyId", tempPartyId);
				emplLeaveRegulation.set("fromDate", yearStart);
				emplLeaveRegulation.set("thruDate", yearEnd);
				emplLeaveRegulation.set("lastYearLeft", lastYearLeft);
				emplLeaveRegulation.set("lastYearTransferred", lastYearTransferred);
				emplLeaveRegulation.set("leaveDayYear", leaveDayYear);
				//FIXME need get granted Leave
				emplLeaveRegulation.set("grantedLeave", BigDecimal.ZERO);
				delegator.createOrStore(emplLeaveRegulation);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> updateEmplLeaveRegulation(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		try {
			GenericValue emplLeaveRegulation = delegator.findOne("EmplLeaveRegulation", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate), false);
			if(emplLeaveRegulation == null){
				return ServiceUtil.returnError("cannot find employee leave regulation");
			}
			emplLeaveRegulation.setNonPKFields(context);
			emplLeaveRegulation.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}
	
	public static Map<String, Object> approvalEmplLeave(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplLeaveId = (String)context.get("emplLeaveId");
		String statusId = (String)context.get("statusId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
			if(emplLeave == null){
				return ServiceUtil.returnError("cannot find emplLeave with id: " + emplLeaveId);
			}
			String partyId = emplLeave.getString("partyId");
			boolean isApprPerms = false;
			if(PartyUtil.isFullPermissionAction(delegator, userLogin.getString("userLoginId"))){
				isApprPerms = true;
			}else if(!userLogin.getString("partyId").equals(partyId)){
				List<String> listDeptId = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
				List<String> listOrgMgrByUserLogin = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"), UtilDateTime.nowTimestamp(), null);
				checkApprPers:
					for(String deptId: listDeptId){
						for(String orgMgrId: listOrgMgrByUserLogin){
							if(PartyUtil.checkAncestorOfParty(delegator, orgMgrId, deptId, userLogin)){
								isApprPerms = true;
								break checkApprPers;
							}else if(deptId.equals(orgMgrId)){
                                isApprPerms = true;
                                break checkApprPers;
                            }
						}
					}
			}
			Timestamp dateApplication = emplLeave.getTimestamp("dateApplication");
			//List<String> emplListId = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), UtilDateTime.nowTimestamp(), UtilDateTime.nowTimestamp());
			if(!isApprPerms){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "ManagerNotManageEmployee", 
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, partyId), "partyId", PartyUtil.getPartyCode(delegator, partyId)), locale));
			}
			if(!"LEAVE_CREATED".equals(emplLeave.get("statusId"))){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", emplLeave.get("statusId")), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "CannotApprEmplLeaveBecauseStatus", 
						UtilMisc.toMap("statusDesc", statusItem.get("description")), locale));
			}
			emplLeave.set("statusId", statusId);
			emplLeave.set("commentApproval", context.get("commentApproval"));
			emplLeave.store();
			String action = "ViewEmplApplicationLeave";
			String header = UtilProperties.getMessage("BaseHREmployeeUiLabels", "ApplicationLeaveApproved", 
					UtilMisc.toMap("dateApplication", DateUtil.getDateMonthYearDesc(dateApplication)), locale);
			CommonUtil.sendNotify(dispatcher, locale, partyId, userLogin, header, action, null);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale)); 
	}
	
	public static Map<String, Object> updateEmplLeaveFromTimeTracker(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		conds.add(EntityCondition.makeCondition("statusId", "LEAVE_APPROVED"));
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if(!PartyUtil.isFullPermissionView(delegator, userLogin.getString("userLoginId"))){
				List<String> emplMgrByParty = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), fromDate, thruDate);
				conds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplMgrByParty));
			}
			List<GenericValue> emplLeave = delegator.findList("EmplLeave", EntityCondition.makeCondition(conds), null, UtilMisc.toList("fromDate"), null, false);
			Calendar calFromDateLeave = Calendar.getInstance();
			Calendar calThruDateLeave = Calendar.getInstance();
			Calendar calAttendanceOut = Calendar.getInstance();
			Calendar calAttendanceIn = Calendar.getInstance();
			for(GenericValue tempGv: emplLeave){
				Timestamp fromDateLeave = tempGv.getTimestamp("fromDate");
				Timestamp thruDateLeave = tempGv.getTimestamp("thruDate");
				calFromDateLeave.setTime(fromDateLeave);
				calThruDateLeave.setTime(thruDateLeave);
				String workingShiftId = tempGv.getString("workingShiftId");
				String fromDateLeaveTypeId = tempGv.getString("fromDateLeaveTypeId");
				String thruDateLeaveTypeId = tempGv.getString("thruDateLeaveTypeId");
				GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
				Time shiftStartTime = workingShift.getTime("shiftStartTime");
				Time shiftEndTime = workingShift.getTime("shiftEndTime");
				Time shiftBreakStart = workingShift.getTime("shiftBreakStart");
				Time shiftBreakEnd = workingShift.getTime("shiftBreakEnd");
				Time startTimeLeave = null, endTimeLeave = null;
				String partyId = tempGv.getString("partyId");
				if(shiftBreakStart == null){
					shiftBreakStart = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);
				}
				if(shiftBreakEnd == null){
					shiftBreakEnd = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);
				}
				if("FIRST_HALF_DAY".equals(fromDateLeaveTypeId)){
					startTimeLeave = shiftStartTime;
				}else if("SECOND_HALF_DAY".equals(fromDateLeaveTypeId)){
					startTimeLeave = shiftBreakEnd;
				}
				if("FIRST_HALF_DAY".equals(thruDateLeaveTypeId)){
					endTimeLeave = shiftBreakStart;
				}else if("SECOND_HALF_DAY".equals(thruDateLeaveTypeId)){
					endTimeLeave = shiftEndTime;
				}
				DateUtil.setHourMiniteSecondForCal(calFromDateLeave, startTimeLeave);
				DateUtil.setHourMiniteSecondForCal(calThruDateLeave, endTimeLeave);
				Timestamp tempFromDateLeave = new Timestamp(fromDateLeave.getTime());
				boolean updateEmplLeave = false;
				while(tempFromDateLeave.compareTo(thruDateLeave) <= 0){
					Date date = new Date(tempFromDateLeave.getTime());
					GenericValue attendanceTracker = delegator.findOne("EmplAttendanceTracker", 
							UtilMisc.toMap("partyId", partyId, "dateAttendance", date, "orgId", orgId), false);
					if(attendanceTracker != null && attendanceTracker.getTime("endTime") != null && attendanceTracker.getTime("startTime") != null){
						// nhan vien di lam vao ngay ${date}, cap nhat lai ${thruDate} va ${thruDateLeaveTypeId} cua don xin nghi phep
						calAttendanceOut.setTime(date);
						calAttendanceIn.setTime(date);
						Time timeIn = attendanceTracker.getTime("startTime");
						Time timeOut = attendanceTracker.getTime("endTime");
						DateUtil.setHourMiniteSecondForCal(calAttendanceOut, timeOut);
						DateUtil.setHourMiniteSecondForCal(calAttendanceIn, timeIn);
						if(calAttendanceOut.compareTo(calFromDateLeave) > 0 && calAttendanceIn.compareTo(calThruDateLeave) < 0){
							updateEmplLeave = true;
							if(DateUtil.compareToTime(timeIn, shiftBreakStart) < 0){
								thruDateLeave = UtilDateTime.getDayEnd(new Timestamp(date.getTime()), -1L);
								thruDateLeaveTypeId = "SECOND_HALF_DAY";
							}else{
								thruDateLeave = UtilDateTime.getDayEnd(new Timestamp(date.getTime()));
								thruDateLeaveTypeId = "FIRST_HALF_DAY";
							}
							break;
						}
					}
					tempFromDateLeave = UtilDateTime.getDayStart(tempFromDateLeave, 1);
				}
				if(updateEmplLeave){
					if(fromDateLeave.compareTo(thruDateLeave) > 0 || 
							(fromDateLeave.compareTo(thruDateLeave) == 0 && "SECOND_HALF_DAY".equals(fromDateLeaveTypeId) && "FIRST_HALF_DAY".equals(thruDateLeaveTypeId))){
						tempGv.set("statusId", "LEAVE_CANCEL");
						tempGv.set("commentApproval", UtilProperties.getMessage("BaseHREmployeeUiLabels", "EmplProposalAbsenceButStillWorking", locale));
					}else{
						tempGv.set("thruDate", thruDateLeave);
						tempGv.set("thruDateLeaveTypeId", thruDateLeaveTypeId);
					}
					tempGv.store();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplLeaveListByParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		List<String> listPartyId = (List<String>)context.get("listPartyId");
		List<GenericValue> listReturn = FastList.newInstance();
		retMap.put("emplLeaveList", listReturn);
		if(UtilValidate.isEmpty(listPartyId)){
			return retMap;
		}
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if(fromDate == null){
			fromDate = UtilDateTime.getDayStart(nowTimestamp);
		}
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyId));
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		try {
			listReturn = delegator.findList("EmplLeave", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createWorkOvertimeRegis(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		if(thruDate.compareTo(fromDate) < 0){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "TimeBeginAfterTimeEnd", locale));
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue workOvertimeRegistration = delegator.makeValue("WorkOvertimeRegistration");
		workOvertimeRegistration.setNonPKFields(context);
		String workOvertimeRegisId = delegator.getNextSeqId("WorkOvertimeRegistration");
		workOvertimeRegistration.set("workOvertimeRegisId", workOvertimeRegisId);
		workOvertimeRegistration.set("statusId", "WOTR_CREATED");
		try {
			workOvertimeRegistration.create();
			retMap.put("workOvertimeRegisId", workOvertimeRegisId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap; 
	}
	public static Map<String, Object> createWorkingLateRegis(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		if(thruDate.compareTo(fromDate) < 0){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "TimeBeginAfterTimeEnd", locale));
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue workingLateRegister = delegator.makeValue("WorkingLateRegister");
		workingLateRegister.setNonPKFields(context);
		String workingLateRegisterId = delegator.getNextSeqId("WorkingLateRegister");
		workingLateRegister.set("workingLateRegisterId", workingLateRegisterId);
		workingLateRegister.set("statusId", "EMPL_LATE_CREATED");
		workingLateRegister.set("createdDate", UtilDateTime.nowTimestamp());
		try {
			workingLateRegister.create();
			retMap.put("workingLateRegisterId", workingLateRegisterId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap; 
	}
	public static Map<String, Object> createWorkOvertimeRegisEnum(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue workOvertimeRegistration = delegator.makeValue("WorkOvertimeRegisEnum");
		workOvertimeRegistration.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(workOvertimeRegistration);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createWorkingLateRegisEnum(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue workingLateRegisEnum = delegator.makeValue("WorkingLateRegisEnum");
		workingLateRegisEnum.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(workingLateRegisEnum);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> approvalWorkingOvertimeRegister(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue workOvertimeRegistration = delegator.makeValue("WorkOvertimeRegistration");
		workOvertimeRegistration.setAllFields(context, false, null, null);
		try {
			delegator.store(workOvertimeRegistration);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
	}
	public static Map<String, Object> approvalWorkingLateRegister(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue workingLateRegister = delegator.makeValue("WorkingLateRegister");
		workingLateRegister.setAllFields(context, false, null, null);
		try {
			delegator.store(workingLateRegister);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getWorkingLateRegisterJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-createdDate");
    		listSortFields.add("firstName");
    	}
    	try {
			listIterator = delegator.find("WorkingLateRegisterAndDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	}
}
