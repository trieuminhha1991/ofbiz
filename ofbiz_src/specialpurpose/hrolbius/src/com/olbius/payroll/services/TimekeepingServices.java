package com.olbius.payroll.services;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
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
import org.ofbiz.service.calendar.RecurrenceRule;

import com.olbius.payroll.util.TimekeepingUtils;
import com.olbius.util.CommonUtil;
import com.olbius.util.DateUtil;
import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class TimekeepingServices {
	public static Map<String, Object> getPartyAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String dateKeeping = (String)context.get("dateKeeping");
		java.sql.Date date = new java.sql.Date(Long.parseLong(dateKeeping));
		String retValue = "";
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			List<GenericValue> emplDateAttendance = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("partyId", partyId, "dateAttendance", date), UtilMisc.toList("startTime"), false);
			Calendar cal = Calendar.getInstance();
			
			if(UtilValidate.isNotEmpty(emplDateAttendance)){
				GenericValue empldateAtt = EntityUtil.getFirst(emplDateAttendance);
				GenericValue emplDateAttLast = emplDateAttendance.get(emplDateAttendance.size() - 1);
				Time startTime = empldateAtt.getTime("startTime");
				Time endTime = emplDateAttLast.getTime("endTime");
				if(startTime != null){
					cal.setTime(startTime);
					retValue += cal.get(Calendar.HOUR) + ":";
					retValue += cal.get(Calendar.MINUTE) + "-";
					retMap.put("startTime", startTime.toString());
				}
				if(endTime != null){
					cal.setTime(endTime);
					retValue += cal.get(Calendar.HOUR) + ":";
					retValue += cal.get(Calendar.MINUTE);
					retMap.put("endTime", endTime.toString());
				}
			}else{
				retValue += "00:00-00:00";
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("not found record");
		}
		
		retMap.put(ModelService.SUCCESS_MESSAGE, ModelService.RESPOND_SUCCESS);
		retMap.put("partyDateKeepingInfo", retValue);
		
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplListTimekeeping(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		//LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	Map<String, Object> retMap = FastMap.newInstance();
    	int month = Integer.parseInt((String)parameters.get("month")[0]);
    	int year = Integer.parseInt((String)parameters.get("year")[0]);
    	String partyIdParam = (String[])parameters.get("partyId") != null? ((String[])parameters.get("partyId"))[0] : null;
    	String partyNameParam = (String[])parameters.get("partyName") != null? ((String[])parameters.get("partyName"))[0]: null;
    	String partyGroupId = request.getParameter("partyGroupId");
    	//Map<String, Object> resultService = FastMap.newInstance();
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, year);
    	cal.set(Calendar.MONTH, month);
    	Timestamp timestamp = new Timestamp(cal.getTimeInMillis()); 
    	Timestamp fromDate = UtilDateTime.getMonthStart(timestamp);
    	Timestamp thruDate = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
    	
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator);
			
			if(partyIdParam != null){
				emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyIdParam + "%")));
			}
			if(partyNameParam != null){
				partyNameParam = partyNameParam.replaceAll("\\s", "");
				List<EntityCondition> tempConds = FastList.newInstance();
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameFirstNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam.toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameLastNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastNameFirstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstNameLastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				emplList = EntityUtil.filterByOr(emplList, tempConds);
			}
			if(end > emplList.size()){
				end = emplList.size();
			}
			totalRows = emplList.size();
			emplList = emplList.subList(start, end);
			//List<GenericValue> workingShift = TimekeepingUtils.getAllWorkingShift(delegator);
			//Map<String, Object> mapEmplDayLeave = FastMap.newInstance();
			
			for(GenericValue empl: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = empl.getString("partyId");
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
				Timestamp tempTimestamp = fromDate;
				while(tempTimestamp.before(thruDate)){
					Date tempDate = new Date(tempTimestamp.getTime());
					List<GenericValue> emplAttendanceTracker = delegator.findList("EmplAttendanceTracker", 
							EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, EntityCondition.makeCondition("dateAttendance", tempDate)), 
							null, UtilMisc.toList("startTime"), null, false);
					cal.setTime(tempDate);
					String dataFieldGroup = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
					tempMap.put("date_" + dataFieldGroup, tempDate.getTime());
					if(UtilValidate.isNotEmpty(emplAttendanceTracker)){
						Time startTime = emplAttendanceTracker.get(0).getTime("startTime");
						Time endTime = emplAttendanceTracker.get(emplAttendanceTracker.size() - 1).getTime("endTime");
						if(startTime != null){
							tempMap.put("startTime_" + dataFieldGroup, startTime.getTime());
						}
						if(endTime != null){
							tempMap.put("endTime_" + dataFieldGroup, endTime.getTime());
						}
					}
						
					tempTimestamp = UtilDateTime.getNextDayStart(tempTimestamp);
				}
				listReturn.add(tempMap);
			}
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} 
		
		retMap.put("listIterator", listReturn);
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	
	public static Map<String, Object> getEmplTimesheetAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		//LocalDispatcher dispatcher = dctx.getDispatcher();
		//Locale locale = (Locale)context.get("locale");
		
		//TimeZone timeZone = UtilHttp.getTimeZone(request);
		List<Map<String, Object>> listReturn = FastList.newInstance();
    	Map<String, Object> retMap = FastMap.newInstance();
    	retMap.put("listIterator", listReturn);
    	String partyGroupId = (String)context.get("partyGroupId");
    	int totalRows = 0;
    	if(partyGroupId == null){
    		//partyGroupId = MultiOrganizationUtil.getCurrentOrganization(delegator);
    		retMap.put("TotalRows", String.valueOf(totalRows));
    		return retMap;
    	}
    	
    	int size = Integer.parseInt((String)context.get("pagesize"));
		int page = Integer.parseInt((String)context.get("pagenum"));
		int start = size * page;
		int end = start + size;
		
    	String emplTimesheetId = (String)context.get("emplTimesheetId");
    	if(emplTimesheetId != null){
    		try {
				//List<GenericValue> emplTimesheetAttList = delegator.findByAnd("EmplTimesheetAttendance", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), UtilMisc.toList("dateAttendance"),  false);
    			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
    			List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator);
    			if(end > emplList.size()){
    				end = emplList.size();
    			}
    			totalRows = emplList.size();
    			emplList = emplList.subList(start, end);
				GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
				String customTimePeriodId = emplTimesheets.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				Calendar cal = Calendar.getInstance();
				
				for(GenericValue employee: emplList){
					Map<String, Object> tempMap = FastMap.newInstance();
					listReturn.add(tempMap);
					tempMap.put("emplTimesheetId", emplTimesheetId);
					tempMap.put("partyId", employee.getString("partyId"));
					tempMap.put("partyName", PartyUtil.getPersonName(delegator, employee.getString("partyId")));
					Timestamp tempFromDate = fromDateTs;
					while(tempFromDate.before(thruDateTs)){
						Date tempDate = new Date(tempFromDate.getTime());
						cal.setTime(tempDate);
						List<GenericValue> emplTimesheetInDate = delegator.findByAnd("EmplTimesheetAttendance", UtilMisc.toMap("partyId", employee.getString("partyId"), "dateAttendance", tempDate, "emplTimesheetId", emplTimesheetId), null, false);
						List<String> emplTimekeepingSignList = EntityUtil.getFieldListFromEntityList(emplTimesheetInDate, "emplTimekeepingSignId", true);
						//String timekeepingSign = CommonUtil.joinListStringByEntityField(delegator, emplTimekeepingSignList, "emplTimekeepingSignId", "EmplTimekeepingSign", "sign");
						String dateText = cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR); 
						tempMap.put(dateText, emplTimekeepingSignList);
						tempFromDate = UtilDateTime.getDayStart(tempFromDate, 1);
					}
				}
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} 
    	}
    	retMap.put("TotalRows", String.valueOf(totalRows));
    	return retMap;
	}
	
	public static Map<String, Object> createEmplTimesheetAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue emplTimesheetAttendance = delegator.makeValue("EmplTimesheetAttendance");
		emplTimesheetAttendance.setAllFields(context, false, null, null);
		try {
			emplTimesheetAttendance.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateEmplTimesheetAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String dateAttendanceStr = (String)context.get("dateAttendance");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		String emplTimekeepingSignList = (String)context.get("emplTimekeepingSignList");
		Locale locale = (Locale)context.get("locale");
		JSONArray emplTimekeepingSignJson = JSONArray.fromObject(emplTimekeepingSignList);
		Date dateAttendance = new Date(Long.parseLong(dateAttendanceStr));
		try {
			List<GenericValue> emplTimesheetAttendanceList = delegator.findByAnd("EmplTimesheetAttendance", 
					UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "dateAttendance", dateAttendance), null, false);
			List<String> emplTimekeepingSignIdList = EntityUtil.getFieldListFromEntityList(emplTimesheetAttendanceList, "emplTimekeepingSignId", true);
			List<String> emplTimekeepingSignIdSubmit = FastList.newInstance();
			for(int i = 0; i < emplTimekeepingSignJson.size(); i++){
				String emplTimekeepingSignId = emplTimekeepingSignJson.getJSONObject(i).getString("emplTimekeepingSignId");
				emplTimekeepingSignIdSubmit.add(emplTimekeepingSignId);
			}
			/*Map<String, Object> mapValidCombine = TimekeepingUtils.checkCombineValidEmplTimekeepingSign(delegator, emplTimekeepingSignIdSubmit);
			Boolean isValid = (Boolean)mapValidCombine.get("isValid");
			if(!isValid){
				String emplTimekeepingSignId = (String)mapValidCombine.get("emplTimekeepingSignId");
				String emplTimekeepingSignIdTo = (String)mapValidCombine.get("emplTimekeepingSignIdTo");
				GenericValue emplTimekeepingSign = delegator.findOne("EmplTimekeepingSign", UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSignId), false);
				GenericValue emplTimekeepingSignTo = delegator.findOne("EmplTimekeepingSign", UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSignIdTo), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "EmplTimekeepingSignListCannotCombine", 
						UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSign.get("description") + " - (" + emplTimekeepingSign.get("sign") + ")",
								"emplTimekeepingSignIdTo", emplTimekeepingSignTo.get("description") + " - (" + emplTimekeepingSignTo.get("sign") + ")"), locale));
			}*/
			for(int i = 0; i < emplTimekeepingSignJson.size(); i++){
				String emplTimekeepingSignId = emplTimekeepingSignJson.getJSONObject(i).getString("emplTimekeepingSignId");
				String hours = null;
				String workday = null;
				if(emplTimekeepingSignJson.getJSONObject(i).has("hours")){
					hours = emplTimekeepingSignJson.getJSONObject(i).getString("hours");
				}
				if(emplTimekeepingSignJson.getJSONObject(i).has("workday")){
					workday = emplTimekeepingSignJson.getJSONObject(i).getString("workday");
				}
				if(emplTimekeepingSignIdList.contains(emplTimekeepingSignId)){
					//update emplTimekeepingSignId exists
					List<GenericValue> updateEntityList = EntityUtil.filterByCondition(emplTimesheetAttendanceList, 
							EntityCondition.makeCondition("emplTimekeepingSignId", emplTimekeepingSignId));
					GenericValue updateEntity = updateEntityList.get(0);
					if(hours != null){
						updateEntity.set("hours", Double.parseDouble(hours));
						updateEntity.store();
					}
					if(workday != null){
						updateEntity.set("workday", Double.parseDouble(workday));
						updateEntity.store();
					}
				}else{
					//else create emplTimekeepingSignId not exists
					GenericValue newEntity = delegator.makeValue("EmplTimesheetAttendance");
					newEntity.set("emplTimesheetId", emplTimesheetId);
					newEntity.set("partyId", partyId);
					newEntity.set("dateAttendance", dateAttendance);
					newEntity.set("emplTimekeepingSignId", emplTimekeepingSignId);
					if(hours != null){
						newEntity.set("hours", Double.parseDouble(hours));
					}
					if(workday != null){
						newEntity.set("workday", Double.parseDouble(workday));
					}
					newEntity.create();
				}
			}
			//delete emplTimekeepingSignId not submit that exists in database
			for(String emplTimekeepingSignId: emplTimekeepingSignIdList){
				if(!emplTimekeepingSignIdSubmit.contains(emplTimekeepingSignId)){
					List<GenericValue> deleteEntityList = EntityUtil.filterByCondition(emplTimesheetAttendanceList, EntityCondition.makeCondition("emplTimekeepingSignId", emplTimekeepingSignId));
					for(GenericValue tempDeleteGv: deleteEntityList){
						/*List<GenericValue> emplTimesheetAttendanceWorkingShift = delegator.findByAnd("EmplTimesheetAttendanceWorkingShift", UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "dateAttendance", dateAttendance, "emplTimekeepingSignId", tempDeleteGv.getString("emplTimekeepingSignId")), null, false);
						for(GenericValue temp: emplTimesheetAttendanceWorkingShift){
							temp.remove();
						}*/
						tempDeleteGv.remove();
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> checkTimesheetInDateIsEdit(DispatchContext dctx, Map<String, Object> context){
		Timestamp date = (Timestamp)context.get("date");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		EntityCondition dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, date),
																	EntityJoinOperator.AND,
																	EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, date));
		boolean isEdit = true;
		try {
			List<GenericValue> emplTimesheetsList = delegator.findList("EmplTimesheets", dateConds, null, null, null, false);
			for(GenericValue tempGv: emplTimesheetsList){
				String statusId = tempGv.getString("statusId");
				if(!"EMPL_TS_CREATED".equals(statusId) && !"EMPL_TS_CALC".equals(statusId)){
					retMap.put("emplTimesheetId", tempGv.getString("emplTimesheetId"));
					isEdit = false;
					break;
				}
			}
			retMap.put("isEdit", isEdit);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
 	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTimesheets(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("-fromDate");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "EMPL_TS_DELETED"));
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		
		try {
			listIterator = delegator.find("EmplTimesheetsAndCustomTimePeriod", tmpCond, null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createEmplTimesheets(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String statusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String importDataTimeRecord = (String)context.get("importDataTimeRecord");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		String emplTimesheetName = (String)context.get("emplTimesheetName");
		if(statusId == null){
			statusId = "EMPL_TS_CALC";
		}
		Locale locale = (Locale)context.get("locale");
		
		try {
			String emplTimesheetNoWhiteSpace = CommonUtil.removeWhiteSpace(emplTimesheetName);
			if(!CommonUtil.containsValidCharacter(emplTimesheetNoWhiteSpace)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "TimesheetNameContainNotValidChar", locale)); 
			}
			List<GenericValue> checkEmplTimesheet = delegator.findList("EmplTimesheets", EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId), null, null, null, false);
			if(UtilValidate.isNotEmpty(checkEmplTimesheet)){
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDate);
				String fromDateStr = DateUtil.getDateMonthYearDesc(cal);
				cal.setTime(thruDate);
				String thruDateStr = DateUtil.getDateMonthYearDesc(cal);
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "EmplTimesheetIsExists", UtilMisc.toMap("fromDate", fromDateStr, "thruDate", thruDateStr), locale));
			}
			GenericValue emplTimesheets = delegator.makeValue("EmplTimesheets");
			emplTimesheets.setAllFields(context, false, null, false);
			String emplTimesheetId = delegator.getNextSeqId("EmplTimesheets");
			emplTimesheets.set("emplTimesheetId", emplTimesheetId);
			emplTimesheets.set("statusId", statusId);
		
			emplTimesheets.create();
			if("Y".equals(importDataTimeRecord)){
				Map<String, Object> contextTmp = FastMap.newInstance();
				contextTmp.put("emplTimesheetId", emplTimesheetId);
				contextTmp.put("userLogin", userLogin);
				contextTmp.put("timeZone", timeZone);
				contextTmp.put("locale", locale);
				dispatcher.schedule("pool", "importTimesheetDataFromTimeRecord", contextTmp, UtilDateTime.nowTimestamp().getTime(), RecurrenceRule.DAILY, 1, 1, -1, 0);
				//dispatcher.runSync("importTimesheetDataFromTimeRecord", UtilMisc.toMap("emplTimesheetId", emplTimesheetId, "userLogin", userLogin, "timeZone", timeZone));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "EmplTimesheetCreateAndCalc", locale));
		return retMap;
	}
	
	public static Map<String, Object> updateEmplTimesheets(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		try {
			String emplTimesheetName = (String)context.get("emplTimesheetName");
			String emplTimesheetNoWhiteSpace = CommonUtil.removeWhiteSpace(emplTimesheetName);
			if(!CommonUtil.containsValidCharacter(emplTimesheetNoWhiteSpace)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "TimesheetNameContainNotValidChar", locale)); 
			}
			GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheet == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale));	
			}
			emplTimesheet.setNonPKFields(context);
			emplTimesheet.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> deleteEmplTimesheet(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheets == null){
				return ServiceUtil.returnError("cannot found emplTimesheet");
			}
			String statusId = emplTimesheets.getString("statusId");
			if("EMPL_TS_CALC".equals(statusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotDeleteTimesheetCalculating", locale));
			}
			emplTimesheets.set("statusId", "EMPL_TS_DELETED");
			emplTimesheets.store();
			Map<String, Object> contextTmp = FastMap.newInstance();
			contextTmp.put("emplTimesheetId", emplTimesheetId);
			contextTmp.put("userLogin", userLogin);
			contextTmp.put("timeZone", context.get("timeZone"));
			contextTmp.put("locale", context.get("locale"));
			dispatcher.schedule("pool", "executeDeleteEmplTimesheet", contextTmp, UtilDateTime.nowTimestamp().getTime(), RecurrenceRule.DAILY, 1, 1, -1, 0);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> executeDeleteEmplTimesheet(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		Locale locale = (Locale)context.get("locale");
		try {
			//List<GenericValue> emplTimesheetAttWS = delegator.findByAnd("EmplTimesheetAttendanceWorkingShift", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), null, false);
			List<GenericValue> emplTimesheetAtt = delegator.findByAnd("EmplTimesheetAttendance", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), null, false);
			GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			
			for(GenericValue tempGv: emplTimesheetAtt){
				tempGv.remove();
			}
			emplTimesheets.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> proposalApprovalTimesheets(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		Locale locale = (Locale)context.get("locale");
		GenericValue emplTimesheet;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheet == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotFindEmplTimesheet", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), locale));
			}
			emplTimesheet.set("statusId", "EMPL_TS_PROPOSAL");
			emplTimesheet.store();
			String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("partyId", hrmAdmin);
			ntfCtx.put("userLogin", userLogin);
			ntfCtx.put("targetLink", "emplTimesheetId=" + emplTimesheetId);
			ntfCtx.put("action", "ProposalTimesheet");
			ntfCtx.put("state", "open");
			ntfCtx.put("header", UtilProperties.getMessage("PayrollUiLabels", "ApprovalTimesheet", UtilMisc.toMap("emplTimesheetName", emplTimesheet.getString("emplTimesheetName"), "emplTimesheetId", emplTimesheetId), locale));
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("sendToSender", "Y");
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "SendProposalSuccessful", locale));
	}
	
	public static Map<String, Object> approvalTimesheet(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String statusId = (String)context.get("statusId");
		String ntfId = (String)context.get("ntfId");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		try {
			GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheets == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotFindEmplTimesheet", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), locale));
			}
			emplTimesheets.set("statusId", statusId);
			emplTimesheets.store();
			if(ntfId != null){
				dispatcher.runSync("updateNotification", UtilMisc.toMap("ntfId", ntfId, "userLogin", context.get("userLogin")));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "approveSuccessfully", locale));
	}
	
	public static Map<String, Object> requestUpdateWorkOvertimeStt(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		Date fromDate = new Date(Long.parseLong(fromDateStr));
		Date thruDate = new Date(Long.parseLong(thruDateStr));
		Set<String> partyMgrIdSet = FastSet.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("dateRegistration", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition("dateRegistration", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			conds.add(EntityCondition.makeCondition("statusId", "WOTR_CREATED"));
			List<GenericValue> wotNotAppr = delegator.findList("WorkOvertimeRegistration", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isEmpty(wotNotAppr)){
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDate);
				String fDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				cal.setTime(thruDate);
				String tDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "WorkOvertimeEmplAppr", UtilMisc.toMap("fromDate", fDate, "thruDate", tDate), locale));
			}
			for(GenericValue tempGv: wotNotAppr){
				String partyId = tempGv.getString("partyId");
				partyMgrIdSet.add(PartyUtil.getManagerOfEmpl(delegator, partyId));
			}
			List<String> partyMgrIdList = FastList.newInstance();
			partyMgrIdList.addAll(partyMgrIdSet);
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("partiesList", partyMgrIdList);
			ntfCtx.put("targetLink", "fromDate="+ fromDate.getTime() + ";thruDate=" + thruDate.getTime());
			ntfCtx.put("header", UtilProperties.getMessage("PayrollUiLabels", "ApprovalWorkingOvertimeEmpl", locale));
			ntfCtx.put("action", "ApprovalWorkingOvertimeEmpl");
			ntfCtx.put("userLogin", userLogin);
			ntfCtx.put("ntfType", "ONE");
			ntfCtx.put("state", "open");
			ntfCtx.put("sendToSender", "Y");
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "SentRequestApproval", locale));
	}
	
	public static Map<String, Object> reCalcEmplTimesheet(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyGroupId = (String)context.get("partyGroupId");
		try {
			GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheet == null){
				return ServiceUtil.returnError("cannot find emplTimesheet have id is: " + emplTimesheetId);
			}
			emplTimesheet.set("statusId", "EMPL_TS_CALC");
			emplTimesheet.store();
			Map<String, Object> contextTmp = FastMap.newInstance();
			contextTmp.put("emplTimesheetId", emplTimesheetId);
			contextTmp.put("partyGroupId", partyGroupId);
			contextTmp.put("userLogin", userLogin);
			contextTmp.put("timeZone", context.get("timeZone"));
			contextTmp.put("locale", locale);
			dispatcher.schedule("pool", "executeReCalcEmplTimesheet", contextTmp, UtilDateTime.nowTimestamp().getTime(), RecurrenceRule.DAILY, 1, 1, -1, 0);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> executeReCalcEmplTimesheet(DispatchContext dctx, Map<String, Object> context){		
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		String partyGroupId = (String)context.get("partyGroupId");
		
		try {
			GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("emplTimesheetId", emplTimesheetId));
			if(partyGroupId != null){
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				String customTimePeriodId = (String)emplTimesheet.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, new Timestamp(fromDate.getTime()), UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime())));
				List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListId));
			}
			List<GenericValue> emplTimesheetAtt = delegator.findList("EmplTimesheetAttendance", EntityCondition.makeCondition(conditions), null, null, null, false);
			for(GenericValue tempGv: emplTimesheetAtt){
				tempGv.remove();
			}
			dispatcher.runSync("importTimesheetDataFromTimeRecord", UtilMisc.toMap("emplTimesheetId", context.get("emplTimesheetId"), "partyGroupId", partyGroupId,
					"userLogin", userLogin, "timeZone", context.get("timeZone"), "locale", context.get("locale")));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> importTimesheetDataFromTimeRecord(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		String partyIdCreateTimesheet = userLogin.getString("partyId");
		String partyGroupId = (String)context.get("partyGroupId");
		try {
			GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheets == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale));
			}
			String customTimePeriodId = emplTimesheets.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false); 
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
			String orgId = null;
			if(partyGroupId != null){
				orgId = partyGroupId;
			}else{
				orgId = MultiOrganizationUtil.getCurrentOrganization(delegator);
			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
			if(emplList == null){
				emplList = FastList.newInstance();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDateTs);
			Map<String, Object> resultService = FastMap.newInstance();
			
		    for(GenericValue employee: emplList){
		    	Timestamp timestamp = new Timestamp(fromDateTs.getTime());
				String partyId = employee.getString("partyId");
				resultService = dispatcher.runSync("updateActualEmplOvertimeWorking", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, 
															"fromDate", fromDateTs, "thruDate", thruDateTs, 
															"timeZone", timeZone, "locale", locale));
				resultService = dispatcher.runSync("updateActualEmplWrokingLate", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
																								"fromDate", fromDateTs, "thruDate", thruDateTs, 																								
																								"timeZone", timeZone, "locale", locale));
				
				while (timestamp.before(thruDateTs)) {
					Date tempDate = new Date(timestamp.getTime());
					resultService = dispatcher.runSync("getEmplTimekeepingSignInDate", UtilMisc.toMap("partyId", partyId, "dateAttendance", tempDate, "userLogin", userLogin));
					if(ServiceUtil.isSuccess(resultService)){
						Map<String, Object> attendanceSignAndHour = (Map<String, Object>)resultService.get("attendanceSignAndHour");
						for(Map.Entry<String, Object> entry: attendanceSignAndHour.entrySet()){
							GenericValue checkedEntity = delegator.findOne("EmplTimesheetAttendance", UtilMisc.toMap("emplTimesheetId", emplTimesheetId, "partyId", partyId, "dateAttendance", tempDate, "emplTimekeepingSignId", entry.getKey()), false);
							Map<String, Object> value = (Map<String, Object>)entry.getValue();
							if(checkedEntity == null){
								GenericValue emplTimesheetAttendance = delegator.makeValue("EmplTimesheetAttendance");					
								emplTimesheetAttendance.set("partyId", partyId);
								emplTimesheetAttendance.set("emplTimesheetId", emplTimesheetId);
								emplTimesheetAttendance.set("dateAttendance", tempDate);
								emplTimesheetAttendance.set("emplTimekeepingSignId", entry.getKey());
								if(value != null){
									Set<String> keys = value.keySet();
									for(String key: keys){
										emplTimesheetAttendance.set(key, value.get(key));
									}
								}
								emplTimesheetAttendance.create();	
							}else{
								if(value != null){
									Set<String> keys = value.keySet();
									for(String key: keys){
										checkedEntity.set(key, value.get(key));
									}
								}
								checkedEntity.store();
							}
						}
					}
					timestamp = UtilDateTime.getDayStart(timestamp, 1);
				}
			}
		    //update emplTimesheet status
		    emplTimesheets.set("statusId", "EMPL_TS_CREATED");
		    emplTimesheets.store();
		    
		    //notify to person that create timesheet
		    Map<String, Object> ntfCtx = FastMap.newInstance();
		    String fromDateDes = DateUtil.getDateMonthYearDesc(fromDateTs);
		    String thruDateDes = DateUtil.getDateMonthYearDesc(thruDateTs);
		    ntfCtx.put("partyId", partyIdCreateTimesheet);
		    ntfCtx.put("ntfType", "ONE");
		    ntfCtx.put("header", UtilProperties.getMessage("PayrollUiLabels", "EmplTimesheetCalculated", UtilMisc.toMap("fromDate", fromDateDes, "thruDate", thruDateDes), locale));
		    ntfCtx.put("state", "open");
		    ntfCtx.put("targetLink", "emplTimesheetId=" + emplTimesheetId);
		    ntfCtx.put("action", "EmplTimesheetList");
		    ntfCtx.put("sendToSender", "Y");
		    ntfCtx.put("userLogin", userLogin);
		    dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateEmplAttendanceTracker(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Date dateAttendance = (Date)context.get("dateAttendance");
		String partyId = (String)context.get("partyId");
		Time startTime = (Time)context.get("startTime");
		Time endTime = (Time)context.get("endTime");
		Delegator delegator = dctx.getDelegator();
		//Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> emplAttendanceTracker = delegator.findByAnd("EmplAttendanceTracker", 
					UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance), null, false);
			if(UtilValidate.isEmpty(emplAttendanceTracker)){
				GenericValue tempEmplAttendance = delegator.makeValue("EmplAttendanceTracker");
				tempEmplAttendance.set("partyId", partyId);
				tempEmplAttendance.set("dateAttendance", dateAttendance);
				tempEmplAttendance.set("startTime", startTime);
				tempEmplAttendance.set("endTime", endTime);
				tempEmplAttendance.create();
			}else{
				GenericValue emplTimeTracker = emplAttendanceTracker.get(0);
				if(startTime == null){
					startTime = emplTimeTracker.getTime("startTime");
				}
				if(endTime == null){
					endTime = emplTimeTracker.getTime("endTime");
				}
				Calendar cal = Calendar.getInstance();
				startTime = DateUtil.convertTime(cal, startTime);
				endTime = DateUtil.convertTime(cal, endTime);
				/*if(startTime != null && endTime != null && !startTime.before(endTime)){
					return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "TimeEnterNotValid", locale));
				}*/
				emplTimeTracker.set("startTime", startTime);
				emplTimeTracker.set("endTime", endTime);
				emplTimeTracker.store();
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}
	
	/*====== replace by getEmplTimesheetGeneral, maybe delete============*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTimekeepingGeneral(DispatchContext dctx, Map<String, Object> context){
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		String partyGroupId = request.getParameter("partyGroupId");
		int month = Integer.parseInt((String)parameters.get("month")[0]);
    	int year = Integer.parseInt((String)parameters.get("year")[0]);
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, year);
    	cal.set(Calendar.MONTH, month);
    	Timestamp timestamp = new Timestamp(cal.getTimeInMillis()); 
    	Timestamp fromDate = UtilDateTime.getMonthStart(timestamp);
    	Timestamp thruDate = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
    	Timestamp monthEnd = thruDate;
    	Timestamp todayEnd = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
    	if(thruDate.after(todayEnd)){
    		thruDate = todayEnd;
    	}
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		int totalRows = 0;
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put("listIterator", listReturn);
		Map<String, Object> resultService = FastMap.newInstance(); 
		try {
			//Map<EmplPositionTypeWorkDay, Float> emplPositionTypeWorkWeek = FastMap.newInstance();
			Organization orgParty = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = orgParty.getEmployeeInOrg(delegator);
			if(end > emplList.size()){
				end = emplList.size();
			}
			totalRows = emplList.size();
			emplList = emplList.subList(start, end);
			retMap.put("TotalRows", String.valueOf(totalRows));
			Map<String, Object> mapEmpDayLeave = FastMap.newInstance();
			DecimalFormat df = new DecimalFormat("#.#");
			for(GenericValue tempGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempPartyId = tempGv.getString("partyId");
				List<GenericValue> emplPositionTypes = PartyUtil.getCurrPositionTypeOfEmpl(delegator, tempPartyId);
				String emplPositionTypeId = "";
				if(UtilValidate.isNotEmpty(emplPositionTypes)){
					emplPositionTypeId = emplPositionTypes.get(0).getString("emplPositionTypeId");					
				}
				Float totalDayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, tempPartyId, fromDate, monthEnd, locale, timeZone);
				//resultService = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", tempPartyId, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
				//Float totalDayLeaveApprove = (Float)resultService.get("nbrDayLeave");
				mapEmpDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, fromDate, thruDate, tempPartyId);
				Float totalDayLeave = (Float)mapEmpDayLeave.get("totalDayLeave");
				Float totalDayLeaveApprove = (Float)mapEmpDayLeave.get("leavePaid") + (Float)mapEmpDayLeave.get("leaveUnPaid");
				Float totalDayLeavePaidApproved = (Float)mapEmpDayLeave.get("leavePaid");
				resultService = dispatcher.runSync("getNbrHourWorkOvertime", UtilMisc.toMap("partyId", tempPartyId, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
				Float hoursActualWorkOvertime = (Float)resultService.get("hoursActualWorkOvertime");
				Float hoursRegisWorkOvertime = (Float)resultService.get("hoursRegisWorkOvertime");
				resultService = dispatcher.runSync("getEmplWorkingLateHours", UtilMisc.toMap("partyId", tempPartyId, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
				Float totalHoursWorkLate = (Float)resultService.get("totalHoursLate");
				tempMap.put("dayLeaveApprove", totalDayLeaveApprove );
				tempMap.put("totalDayLeave", totalDayLeave);
				tempMap.put("totalDayLeavePaidApproved", totalDayLeavePaidApproved);
				tempMap.put("overtimeRegister", df.format(hoursRegisWorkOvertime));
				tempMap.put("overtimeActual", df.format(hoursActualWorkOvertime));
				tempMap.put("totalWorkingLateHour", df.format(totalHoursWorkLate));
				tempMap.put("partyId", tempPartyId);
				tempMap.put("partyName", PartyHelper.getPartyName(delegator, tempPartyId, false));
				tempMap.put("emplPositionTypeId", emplPositionTypeId);
				tempMap.put("totalDayWork", totalDayWork);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getEmplTimesheetGeneral(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> retMap = FastMap.newInstance();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		String partyGroupId = (String)context.get("partyGroupId");
		if(partyGroupId == null){
			partyGroupId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		int size, page;
		try{
			size = Integer.parseInt((String)context.get("pagesize"));
		}catch(Exception e){
			size = 0;
		}
		try{
			page = Integer.parseInt((String)context.get("pagenum"));
		}catch(Exception e){
			page = 0;
		}
		int start = size * page;
		int end = start + size;
		
    	int totalRows = 0;
		if(emplTimesheetId != null){
			try {
				GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
				//List<GenericValue> emplTimesheetAtt = delegator.findList("EmplTimesheetAttendance", EntityCondition.makeCondition("emplTimesheetId", emplTimesheetId), null, UtilMisc.toList("partyId"), null, false);
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				
				List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator);
				String customTimePeriodId = emplTimesheet.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				Map<String, Object> mapEmpDayLeave = FastMap.newInstance();
				
				Map<String, Object> resultService = FastMap.newInstance();
				DecimalFormat df = new DecimalFormat("#.#");
				if(end > emplList.size()){
    				end = emplList.size();
    			}
    			totalRows = emplList.size();
    			emplList = emplList.subList(start, end);
				for(GenericValue employee: emplList){
					Map<String, Object> tempMap = FastMap.newInstance();
					String partyId = employee.getString("partyId");
					List<GenericValue> emplPositionTypes = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
					String emplPositionTypeId = "";
					if(UtilValidate.isNotEmpty(emplPositionTypes)){
						emplPositionTypeId = emplPositionTypes.get(0).getString("emplPositionTypeId");
					}
					Float totalDayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDateTs, thruDateTs, locale, timeZone);
					mapEmpDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, fromDateTs, thruDateTs, partyId);
					Float totalDayLeave = (Float)mapEmpDayLeave.get("totalDayLeave");
					
					//Float totalDayLeaveApprove = (Float)mapEmpDayLeave.get("leavePaid") + (Float)mapEmpDayLeave.get("leaveUnPaid");
					Float totalDayLeavePaidApproved = (Float)mapEmpDayLeave.get("leavePaid");
					
					resultService = dispatcher.runSync("getNbrHourWorkOvertime", UtilMisc.toMap("partyId", partyId, "fromDate", fromDateTs, "thruDate", thruDateTs, "userLogin", userLogin));
					Float hoursActualWorkOvertime = (Float)resultService.get("hoursActualWorkOvertime");
					resultService = dispatcher.runSync("getEmplWorkingLateHours", UtilMisc.toMap("partyId", partyId, "fromDate", fromDateTs, "thruDate", thruDateTs, "userLogin", userLogin));
					Float totalHoursWorkLate = (Float)resultService.get("totalHoursLate");
					//tempMap.put("dayLeaveApprove", totalDayLeaveApprove );
					tempMap.put("totalDayLeave", totalDayLeave);
					tempMap.put("totalDayLeavePaidApproved", totalDayLeavePaidApproved);
					tempMap.put("overtimeActual", df.format(hoursActualWorkOvertime));
					tempMap.put("totalWorkingLateHour", df.format(totalHoursWorkLate));
					tempMap.put("partyId", partyId);
					tempMap.put("partyName", PartyHelper.getPartyName(delegator, partyId, false));
					tempMap.put("emplPositionTypeId", emplPositionTypeId);
					tempMap.put("totalDayWork", totalDayWork);
					listReturn.add(tempMap);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> getEmplWorkingLateHours(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Date startDate = new Date(fromDate.getTime());
		Date endDate = new Date(thruDate.getTime());
		float totalHoursLate = 0f;
		try {
			Calendar cal = Calendar.getInstance();
			List<GenericValue> emplAttendanceTrackers;
			while (startDate.before(endDate)|| startDate.equals(endDate)) {
				cal.setTime(startDate);
				emplAttendanceTrackers = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("partyId", partyId, "dateAttendance", startDate), UtilMisc.toList("startTime"), false);
				if(UtilValidate.isNotEmpty(emplAttendanceTrackers)){
					Map<String, Object> workingShiftRegulation = TimekeepingUtils.getWorkingShiftDayOfParty(dctx, context, startDate);
					Boolean isDayLeave = (Boolean)workingShiftRegulation.get("isDayLeave");
					String tempWorkingShiftId = (String)workingShiftRegulation.get("workingShiftId");
					GenericValue tempGv = emplAttendanceTrackers.get(0);
					//get time that party go to work
					Time startTime = tempGv.getTime("startTime");
					if(startTime != null){
						//get list working shift of party in day
						GenericValue tempWorkingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", tempWorkingShiftId), false);
						if(UtilValidate.isNotEmpty(tempWorkingShift)){
							//get time of first working shift that party attendance
							Time startTimeWorkingShift = tempWorkingShift.getTime("shiftStartTime");
							Time shiftBreakEnd = tempWorkingShift.getTime("shiftBreakEnd");
							Time shiftBreakStart = tempWorkingShift.getTime("shiftBreakStart");
							Time shiftEndTime = tempWorkingShift.getTime("shiftEndTime");
							Long allowLateMinute = tempWorkingShift.getLong("allowLateMinute");
							Time timeStartCalLate = startTimeWorkingShift; 
							if(allowLateMinute != null){
								timeStartCalLate = new Time(startTimeWorkingShift.getTime() + allowLateMinute * 60 * 1000);
							}else{
								allowLateMinute = 0l;
							}
							if(!isDayLeave){
								if(shiftBreakStart == null){
									shiftBreakStart = DateUtil.getMiddleTimeBetweenTwoTime(startTimeWorkingShift, shiftEndTime);
								}
								if(shiftBreakEnd == null){
									shiftBreakEnd = shiftBreakStart;
								}
								if(startTime.after(timeStartCalLate) && startTime.before(shiftBreakStart)){
									totalHoursLate += (float)(startTime.getTime() - timeStartCalLate.getTime())/(1000 * 3600);
								}else{
									//employee go to work after end of time FIRST_HALF_WORKING_SHIFT, so calculate working late start from SECOND_HALF_WORKING_SHIFT 
									timeStartCalLate = new Time(shiftBreakEnd.getTime() + allowLateMinute * 60 * 1000);
									if(startTime.after(timeStartCalLate) && startTime.before(shiftEndTime)){
										totalHoursLate += (float)(startTime.getTime() - timeStartCalLate.getTime())/(1000 * 3600);
									}
								}
							}
						}
					}
				}
				cal.add(Calendar.DATE, 1);
				startDate = new Date(cal.getTimeInMillis());
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		retMap.put("totalHoursLate", totalHoursLate);
		return retMap;
	}
	
	public static Map<String, Object> updateEmplWorkingLateInPeriod(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
		Map<String, Object> resultService;
		try {
			while(fromDate.before(thruDate)){
				Timestamp tempThruDate = UtilDateTime.getDayEnd(fromDate);
				resultService = dispatcher.runSync("getEmplWorkingLateHours", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", tempThruDate));
				if(ServiceUtil.isSuccess(resultService)){
					EntityCondition dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateWorkingLate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
																				EntityOperator.AND,
																				EntityCondition.makeCondition("dateWorkingLate", EntityOperator.LESS_THAN_EQUAL_TO, tempThruDate));
					List<GenericValue> listEmplWorkingLate;
					
						listEmplWorkingLate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(commonConds, EntityOperator.AND, dateConds), null, 
																					UtilMisc.toList("dateWorkingLate"), null, false);
					
					Float totalHoursLate = (Float)resultService.get("totalHoursLate");
					
					if(totalHoursLate > 0){
						Float totalMinuteLate = totalHoursLate * 60;
						if(UtilValidate.isNotEmpty(listEmplWorkingLate)){
							for(GenericValue tempGv: listEmplWorkingLate){
								tempGv.set("delayTime", totalMinuteLate.longValue());
								tempGv.store();
							}
						}else{
							GenericValue emplWorkingLate = delegator.makeValue("EmplWorkingLate");
							emplWorkingLate.set("partyId", partyId);
							emplWorkingLate.set("dateWorkingLate", fromDate);
							emplWorkingLate.set("delayTime", totalMinuteLate.longValue());
							String emplWorkingLateId = delegator.getNextSeqId("EmplWorkingLate");
							emplWorkingLate.set("emplWorkingLateId", emplWorkingLateId);
							emplWorkingLate.set("statusId", "EMPL_LATE_CREATED");
							emplWorkingLate.create();
						}
					}else{
						if(UtilValidate.isNotEmpty(listEmplWorkingLate)){
							for(GenericValue tempGv: listEmplWorkingLate){
								tempGv.remove();
							}
						}
					}
				}
				fromDate = UtilDateTime.getDayStart(fromDate, 1);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
		return retMap;
	}
	
	public static Map<String, Object> updateActualEmplWrokingLate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String) context.get("partyId");
		
		Timestamp timestampFrom = UtilDateTime.getDayStart((Timestamp)context.get("fromDate"));
		Timestamp timestampThru = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"), timeZone, locale);
		Timestamp toDay = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		if(timestampThru.after(toDay)){
			timestampThru = toDay;
		}
		
		EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
		Map<String, Object> resultService = FastMap.newInstance();
		
		try {
			while(timestampFrom.before(timestampThru)){
				Timestamp tempThruDate = UtilDateTime.getDayEnd(timestampFrom);
				resultService = dispatcher.runSync("getEmplWorkingLateHours", UtilMisc.toMap("partyId", partyId, "fromDate", timestampFrom, "thruDate", tempThruDate));
				if(ServiceUtil.isSuccess(resultService)){
					EntityCondition dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateWorkingLate", EntityOperator.GREATER_THAN_EQUAL_TO, timestampFrom),
																				EntityOperator.AND,
																				EntityCondition.makeCondition("dateWorkingLate", EntityOperator.LESS_THAN_EQUAL_TO, tempThruDate));
					List<GenericValue> listEmplWorkingLate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(commonConds, EntityOperator.AND, dateConds), null, 
																				UtilMisc.toList("dateWorkingLate"), null, false);
					Float totalHoursLate = (Float)resultService.get("totalHoursLate");
					
					if(totalHoursLate > 0){
						Float totalMinuteLate = totalHoursLate * 60;
						if(UtilValidate.isNotEmpty(listEmplWorkingLate)){
							for(GenericValue tempGv: listEmplWorkingLate){
								tempGv.set("delayTime", totalMinuteLate.longValue());
								tempGv.store();
							}
						}else{
							GenericValue emplWorkingLate = delegator.makeValue("EmplWorkingLate");
							emplWorkingLate.set("partyId", partyId);
							emplWorkingLate.set("dateWorkingLate", timestampFrom);
							emplWorkingLate.set("delayTime", totalMinuteLate.longValue());
							String emplWorkingLateId = delegator.getNextSeqId("EmplWorkingLate");
							emplWorkingLate.set("emplWorkingLateId", emplWorkingLateId);
							emplWorkingLate.set("statusId", "EMPL_LATE_CREATED");
							emplWorkingLate.create();
						}
					}else{
						if(UtilValidate.isNotEmpty(listEmplWorkingLate)){
							for(GenericValue tempGv: listEmplWorkingLate){
								tempGv.remove();
							}
						}
					}
				}
				timestampFrom = UtilDateTime.getDayStart(timestampFrom, 1);
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
		return retMap;
	}
	
	public static Map<String, Object> updateEmplWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
		Delegator delegator = dctx.getDelegator();
		String emplWorkingLateId = (String)context.get("emplWorkingLateId");
		try {
			GenericValue emplWorkingLate = delegator.findOne("EmplWorkingLate", UtilMisc.toMap("emplWorkingLateId", emplWorkingLateId), false);
			if(emplWorkingLate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale));
			}
			/*String partyId = emplWorkingLate.getString("partyId");*/
			emplWorkingLate.setNonPKFields(context);
			emplWorkingLate.store();
			/*List<GenericValue> emplTimesheetAttLate = delegator.findByAnd("EmplTimesheetAttendance", 
					UtilMisc.toMap("partyId", partyId, "dateAttendance", emplWorkingLate.getDate("dateWorkingLate"), "emplTimekeepingSignId", "DI_MUON"), null, false);*/
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> approvalAllEmplWorkingLateInPeriod(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		String statusId = (String)context.get("statusId");
		Timestamp fromDate  = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate  = new Timestamp(Long.parseLong(thruDateStr));
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		try {
			List<GenericValue> emplList = PartyUtil.getListEmplDirectMgr(delegator, partyId);
			List<String> emplListStr = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListStr));
			conditions.add(EntityCondition.makeCondition("statusId", "EMPL_LATE_CREATED"));
			List<GenericValue> emplListWorkingLateUpdate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isEmpty(emplListWorkingLateUpdate)){
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDate);
				String fromDateLabel = DateUtil.getDateMonthYearDesc(cal);
				cal.setTime(thruDate);
				String thruDateLabel = DateUtil.getDateMonthYearDesc(cal);
				Map<String, Object> errorMap = ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "AllEmplWorkingLateApproval", UtilMisc.toMap("fromDate", fromDateLabel, "thruDate", thruDateLabel), locale));
				errorMap.put(ModelService.RESPONSE_MESSAGE, "error");
				return errorMap;
			}
			for(GenericValue updateEtt: emplListWorkingLateUpdate){
				dispatcher.runSync("updateEmplWorkingLate", UtilMisc.toMap("emplWorkingLateId", updateEtt.getString("emplWorkingLateId"), "statusId", statusId));
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	/**
	 * check whether date is dayoff of position type
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> checkDayIsDayLeaveOfParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		Timestamp dateCheck = (Timestamp)context.get("dateCheck");
		Date date = new Date(dateCheck.getTime());
		String partyId = (String)context.get("partyId");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateCheck.getTime());
		//Timestamp dayEnd = UtilDateTime.getDayEnd(dateCheck);
		boolean isDayLeave = false;
		//List<String> workingShiftList = FastList.newInstance();
		try {
			//check whether dateCheck in holiday or not
			GenericValue dateCheckInHoliday = delegator.findOne("Holiday", UtilMisc.toMap("dateHoliday", date), false);
			
			if(UtilValidate.isNotEmpty(dateCheckInHoliday)){
				retMap.put("isDayLeave", true);
				retMap.put("isHoliday", true);
				return retMap;
			}
			GenericValue workingShiftEmployee = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", date), false);
			if(workingShiftEmployee != null){
				String workingShiftId = workingShiftEmployee.getString("workingShiftId");
				if(workingShiftId != null){
					String dayOfWeek = DateUtil.getDayName(cal.get(Calendar.DAY_OF_WEEK));
					GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), false);
					if(workingShiftDayWeek != null){
						String workTypeId = workingShiftDayWeek.getString("workTypeId");
						retMap.put("workTypeId", workTypeId);
						if("DAY_OFF".equals(workTypeId)){
							isDayLeave = true;
						}
					}
				}
			}
			retMap.put("isDayLeave", isDayLeave);			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTimekeepingSignInDate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		Map<String, Object> attendanceSignAndHour = FastMap.newInstance();
		//listAttendanceSignAndHour.add(attendanceSignAndHour);
		retMap.put("attendanceSignAndHour", attendanceSignAndHour);
		Date dateAttendance = (Date)context.get("dateAttendance");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			Timestamp timestamp = new Timestamp(dateAttendance.getTime());
			Timestamp fromDate = UtilDateTime.getDayStart(timestamp);
			Timestamp thruDate = UtilDateTime.getDayEnd(timestamp);
			Map<String, Object> resultService = dispatcher.runSync("getNbrHourWorkOvertime", 
					UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "partyId", partyId, "statusId", "WOTR_ACCEPTED", "userLogin", userLogin));
			Float hoursConfirmWorkOvertime = (Float)resultService.get("hoursActualWorkOvertime");
			if(hoursConfirmWorkOvertime > 0){
				attendanceSignAndHour.put("LAM_THEM", UtilMisc.toMap("hours", (double)hoursConfirmWorkOvertime));
			}
			
			resultService = dispatcher.runSync("getNbrHourWorkOvertime", 
					UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "partyId", partyId, "statusId", "WOTR_CREATED", "userLogin", userLogin));
			Float hoursNotConfirmWorkOvertime = (Float)resultService.get("hoursActualWorkOvertime");
			if(hoursNotConfirmWorkOvertime > 0){
				attendanceSignAndHour.put("LAM_THEM_CHUA_XN", UtilMisc.toMap("hours", (double)hoursNotConfirmWorkOvertime));
			}
			
			resultService = dispatcher.runSync("getEmplWorkingLateHours", UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "partyId", partyId, "userLogin", userLogin));
			Float totalHoursLate = (Float)resultService.get("totalHoursLate");
			if(totalHoursLate > 0){
				attendanceSignAndHour.put("DI_MUON", UtilMisc.toMap("hours", (double)totalHoursLate));
			}
			//List<GenericValue> workingShiftList = TimekeepingUtils.getAllWorkingShift(delegator, UtilMisc.toList("startTime"));
			List<GenericValue> dateEmplAttendanceTracker = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance), UtilMisc.toList("startTime"), false);
			
			Map<String, Object> mapWsDayOfParty = TimekeepingUtils.getWorkingShiftDayOfParty(dctx, context, dateAttendance);
			Boolean isDayLeave = (Boolean)mapWsDayOfParty.get("isDayLeave");
			
			if(!isDayLeave){
				Time startTime = null;
				Time endTime = null;
				String workingShiftId = null;
				Double workdayInDate = 0d;
				String workTypeId = null;
				if(UtilValidate.isNotEmpty(dateEmplAttendanceTracker)){
					startTime = dateEmplAttendanceTracker.get(0).getTime("startTime");
					endTime = dateEmplAttendanceTracker.get(dateEmplAttendanceTracker.size() - 1).getTime("endTime");				
					if(startTime != null && endTime != null){
						workingShiftId = (String)mapWsDayOfParty.get("workingShiftId");
						if(workingShiftId != null){
							GenericValue workingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", workingShiftId), false);
							workTypeId = (String)mapWsDayOfParty.get("workTypeId");
							if(endTime.before(startTime)){
								endTime = new Time(endTime.getTime() + DateUtil.ONE_DAY_MILLIS);
							}
							Time shiftStartTime = workingShift.getTime("shiftStartTime");
							Time shiftEndTime = workingShift.getTime("shiftEndTime");
							Time shiftBreakStart = workingShift.getTime("shiftBreakStart");
							Time shiftBreakEnd = workingShift.getTime("shiftBreakEnd");
							if(shiftBreakStart != null && shiftBreakEnd != null && shiftBreakEnd.before(shiftBreakStart)){
								shiftBreakEnd = new Time(shiftBreakEnd.getTime() + DateUtil.ONE_DAY_MILLIS);
							}
							if(shiftEndTime.before(shiftStartTime)){
								shiftEndTime = new Time(shiftEndTime.getTime() + DateUtil.ONE_DAY_MILLIS);
							}
							if("FIRST_HALF_SHIFT".equals(workTypeId)){
								if(shiftBreakStart != null){
									shiftEndTime = shiftBreakStart;
								}else{
									shiftEndTime = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);	
								}
							}
							if("SECOND_HALF_SHIFT".equals(workTypeId)){
								if(shiftBreakEnd != null){
									shiftStartTime = shiftBreakEnd;
								}else{
									shiftStartTime = DateUtil.getMiddleTimeBetweenTwoTime(shiftStartTime, shiftEndTime);
								}
							}
							if(startTime.before(shiftEndTime) && endTime.after(shiftStartTime)){
								if(startTime.before(shiftStartTime)){
									startTime = shiftStartTime;
								}
								//TODO need check case: shiftStartTime is 20:00:00 and shiftEndTime is 4:00:00
								if(endTime.after(shiftEndTime)){
									endTime = shiftEndTime;
								}
								DecimalFormat df = new DecimalFormat("#.#");
								Long nbrHourWorked = DateUtil.calculateHoursBetweenTimes(startTime, endTime);
								Long nbrHoursMustWork = DateUtil.calculateHoursBetweenTimes(shiftStartTime, shiftEndTime);
								if(nbrHoursMustWork != 0){
									float tempWorkDay = (float)nbrHourWorked/nbrHoursMustWork;
									Map<String, Double> tempMap = FastMap.newInstance();
									String emplTimekeepingSignId = "DI_LAM";
									workdayInDate = Double.valueOf(df.format(tempWorkDay));
									tempMap.put("workday", workdayInDate);
									attendanceSignAndHour.put(emplTimekeepingSignId, tempMap);
								}
							}
						}
					}
				}
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("partyId", partyId);
				ctxMap.put("dateAttendance", dateAttendance);
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("workdayInDate", workdayInDate);
				ctxMap.put("workTypeId", workTypeId);
				
				resultService = dispatcher.runSync("getEmplLeaveTimekeepingSignInDate", ctxMap);
				if(ServiceUtil.isSuccess(resultService)){
					List<Map<String, Object>> leaveTimekeepingSign = (List<Map<String, Object>>)resultService.get("leaveTimekeepingSign");
					for(Map<String, Object> tempEmplTimekeepingSignMap: leaveTimekeepingSign){
						for(Entry<String, Object> entry: tempEmplTimekeepingSignMap.entrySet()){
							attendanceSignAndHour.put(entry.getKey(), entry.getValue());
						}
					}
				}
				
			}else if(dateEmplAttendanceTracker.size() == 0){
				Boolean isHoliday = (Boolean)mapWsDayOfParty.get("isHoliday");
				if(isHoliday != null && isHoliday){
					attendanceSignAndHour.put("NGHI_LE", null);
				}else{
					attendanceSignAndHour.put("_NA_", null);
				}
			}
		} catch (GenericEntityException e){
			e.printStackTrace();
		} catch (GenericServiceException e){
			e.printStackTrace();
		}
		return retMap;
	}
	
	// service to get information about timekeepingSign of employee is "NGHI_PHEP", "NGHI_KHONG_LUONG" or "NGHI_KHONG_PHEP"
	public static Map<String, Object> getEmplLeaveTimekeepingSignInDate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		Timestamp dayStart, dayEnd;
		//so ngay cong cua nhan vien
		Double workdayInDate = (Double)context.get("workdayInDate");
		if(workdayInDate == null){
			workdayInDate = 0d;
		}
		String workTypeId = (String)context.get("workTypeId");
		String partyId = (String)context.get("partyId");
		Date dateAttendance = (Date)context.get("dateAttendance");
		
		dayStart = UtilDateTime.getDayStart(new Timestamp(dateAttendance.getTime()));
		dayEnd = UtilDateTime.getDayEnd(new Timestamp(dateAttendance.getTime()));
		EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
		commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("leaveStatus", "LEAVE_APPROVED"));
		List<EntityCondition> conditions = FastList.newInstance();
		List<Map<String, Object>> leaveTimekeepingSign = FastList.newInstance();
		retMap.put("leaveTimekeepingSign", leaveTimekeepingSign);
		try {		
			if(workTypeId == null){
				Map<String, Object> map = TimekeepingUtils.getWorkingShiftDayOfParty(dctx, partyId, dateAttendance);
				workTypeId = (String)map.get("workTypeId");
			}
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayStart));
			conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd));
			List<GenericValue> emplLeave = delegator.findList("EmplLeave", EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition(conditions)), null, null, null, false);
			Double workdayLeaveNbr = null;
			double dayWorkKeeping = 0d;// so ngay cong trong 1 ngay
			if("SECOND_HALF_SHIFT".equals(workTypeId) || "FIRST_HALF_SHIFT".equals(workTypeId)){
				dayWorkKeeping = 0.5;
			}else if("ALL_SHIFT".equals(workTypeId)){
				dayWorkKeeping = 1d;
			}
			if(UtilValidate.isNotEmpty(emplLeave)){
				for(GenericValue tempGv: emplLeave){
					Map<String, Object> tempMap = FastMap.newInstance();
					String leaveUnpaid = tempGv.getString("leaveUnpaid");
					String leaveTypeId = tempGv.getString("leaveTypeId");
					String leaveType;
					if(!"Y".equals(leaveUnpaid)){
						leaveType = "NGHI_KHONG_LUONG";
					}else if("COMPENSATE".equals(leaveTypeId)){
						leaveType = "NGHI_BU";	
					}else{
						//TODO need separate leave case: NGHI_PHEP_NAM, NGHI_NUA_NGAY_PHEP, NGHI_BU, NGHI_OM_75
						leaveType = "NGHI_PHEP_NAM";
					}
					if(workdayInDate == 0d){
						//so ngay cong cua nhan vien la 0 => nhan vien nghi ca ngay
						if("FIRST_HAFT_DAY".equals(leaveTypeId) || "SECOND_HAFT_DAY".equals(leaveTypeId)){
							//nhan vien nghi ca ngay nhung don xin phep chi nghi nua ngay => nua ngay nghi "NGHI_KHONG_LY_DO"
							tempMap.put(leaveType, 0.5);
							if(dayWorkKeeping - 0.5 > 0){
								tempMap.put("NGHI_KHONG_LY_DO", dayWorkKeeping - 0.5);
							}
						}else{
							tempMap.put(leaveType, dayWorkKeeping);	
						}
					}else if(workdayInDate <= 0.5){
						//nhan vien lam viec nua ngay, nghi nua ngay
						tempMap.put(leaveType, 1 - workdayInDate);
					}
					leaveTimekeepingSign.add(tempMap);
				}
			}else{
				Map<String, Object> tempMap = FastMap.newInstance();
				if(workdayInDate == 0d){
					workdayLeaveNbr = dayWorkKeeping;
				}else if(workdayInDate <= 0.5 && dayWorkKeeping - workdayInDate > 0){
					workdayLeaveNbr = dayWorkKeeping - workdayInDate;
				}
				if(workdayLeaveNbr != null){
					tempMap.put("NGHI_KHONG_LY_DO", UtilMisc.toMap("workday", workdayLeaveNbr));
					leaveTimekeepingSign.add(tempMap);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	 public static Map<String, Object> getTimeKeepingEmplInPeriod(DispatchContext dctx, Map<String, Object> context){
	    	//Delegator delegator = dctx.getDelegator();
	    	String partyId = (String)context.get("partyId");
	    	//List<String> attendanceTypeIds = (List<String>) context.get("attendanceTypeIds"); 
	    	Timestamp fromDate = (Timestamp)context.get("fromDate");
	    	Timestamp thruDate = (Timestamp)context.get("thruDate");
	    	//List<EntityCondition> conditions = FastList.newInstance();
	    	Map<String, Object> retMap = FastMap.newInstance();
	    	Locale locale = (Locale)context.get("locale");
	    	TimeZone timeZone = (TimeZone)context.get("timeZone");
	    	float countTimekeeping = 0;
	    	/*conditions.add(EntityCondition.makeCondition("dateAttendance", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(fromDate.getTime())));
	    	conditions.add(EntityCondition.makeCondition("dateAttendance", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(thruDate.getTime())));
	    	conditions.add(EntityCondition.makeCondition("partyId", partyId));*/
	    	
	    	try {
	    		
	    		float totalDayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
	    		Map<String, Object> mapEmplDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, fromDate, thruDate, partyId); 
	    		float totalDayLeave = (Float)mapEmplDayLeave.get("totalDayLeave");
	    		countTimekeeping = totalDayWork - totalDayLeave;
	    			
				/*List<GenericValue> emplAttTrack = delegator.findList("EmplAttendanceTracker", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
				float timekeepingPerShift = 1.0f/(workingShift.size());
				for(GenericValue tempEmplAttTrack: emplAttTrack){
					Time startTime = tempEmplAttTrack.getTime("startTime");
					Time endTime = tempEmplAttTrack.getTime("endTime");
					for(GenericValue tempShift: workingShift){
						Time startShift = tempShift.getTime("startTime");
						Time endShift = tempShift.getTime("endTime");
						if(startTime.before(endShift) && endTime.after(startShift)){
							countTimekeeping += timekeepingPerShift;
						}
					}
				}*/
				retMap.put("countTimekeeping", countTimekeeping);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
	    	return retMap;
	    }
	 
	 /*====== get  work overtime registration of partyid ==========*/
	public static Map<String, Object> getWorkOvertimeRegistration(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String) context.get("partyId");
		String monthStr = (String)context.get("month");
		String yearStr = (String)context.get("year");
		Calendar cal = Calendar.getInstance();
		int month = Integer.parseInt(monthStr);
		int year = Integer.parseInt(yearStr);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp timestampFrom = UtilDateTime.getMonthStart(timestamp);
		Date startMonthDate = new Date(timestampFrom.getTime());
		Timestamp timestampThru = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
		Timestamp toDay = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		if(timestampThru.after(toDay)){
			timestampThru = toDay;
		}
		Date endMonthDate = new Date(timestampThru.getTime());
		Map<String, Object> res = FastMap.newInstance();
		try {
			EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
			EntityCondition dateWorkRegisConds;
			List<Map<String, Object>> listReturn = FastList.newInstance();
			
			dateWorkRegisConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateRegistration", EntityOperator.LESS_THAN_EQUAL_TO, endMonthDate),
																EntityOperator.AND,
																EntityCondition.makeCondition("dateRegistration", EntityOperator.GREATER_THAN_EQUAL_TO, startMonthDate));
			
			List<GenericValue> emplWorkOvertimeRegis = delegator.findList("WorkOvertimeRegistration", EntityCondition.makeCondition(commonConds, 
																				EntityOperator.AND, dateWorkRegisConds), 
																			null, UtilMisc.toList("overTimeFromDate"), null, false);
			
			for(GenericValue tempGv: emplWorkOvertimeRegis){
				Map<String, Object> tempMap = FastMap.newInstance();
				Time overTimeFromDate = tempGv.getTime("overTimeFromDate");
				Time overTimeThruDate = tempGv.getTime("overTimeThruDate");
				Date dateRegistration = tempGv.getDate("dateRegistration");
				Time actualStartTime = tempGv.getTime("actualStartTime");
				Time actualEndTime = tempGv.getTime("actualEndTime");
				tempMap.put("workOvertimeRegisId", tempGv.getString("workOvertimeRegisId"));
				tempMap.put("dateRegistration", dateRegistration != null? dateRegistration.getTime(): null);
				tempMap.put("overTimeFromDate", overTimeFromDate != null? overTimeFromDate.getTime(): null);
				tempMap.put("overTimeThruDate", overTimeThruDate != null? overTimeThruDate.getTime(): null);
				tempMap.put("actualStartTime", actualStartTime != null? actualStartTime.getTime(): null);
				tempMap.put("actualEndTime", actualEndTime != null? actualEndTime.getTime(): null);
				tempMap.put("statusId", tempGv.getString("statusId"));
				listReturn.add(tempMap);
			}
			res.put("data", listReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/*=========get working overtime of all employee in  period==========*/
	public static Map<String, Object> getWorkOvertimeInPeriod(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		if(fromDateStr != null && thruDateStr != null){
			try {
				String orgMgrId = PartyUtil.getOrgByManager(partyId, delegator);
				Organization buildOrg = PartyUtil.buildOrg(delegator, orgMgrId, false, false);
				List<GenericValue> emplList = buildOrg.getDirectEmployee(delegator);
				List<String> emplListStr = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
				Date startDate = new Date(Long.parseLong(fromDateStr));
				Date endDate = new Date(Long.parseLong(thruDateStr));
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListStr));
				conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dateRegistration", EntityOperator.LESS_THAN_EQUAL_TO, endDate),
					EntityOperator.AND,
					EntityCondition.makeCondition("dateRegistration", EntityOperator.GREATER_THAN_EQUAL_TO, startDate)));
			
				List<GenericValue> emplWorkOvertimeRegis = delegator.findList("WorkOvertimeRegistration", EntityCondition.makeCondition(conditions), 
																				null, UtilMisc.toList("dateRegistration", "partyId"), null, false);
				for(GenericValue tempGv: emplWorkOvertimeRegis){
					Map<String, Object> tempMap = FastMap.newInstance();
					Time overTimeFromDate = tempGv.getTime("overTimeFromDate");
					Time overTimeThruDate = tempGv.getTime("overTimeThruDate");
					Date dateRegistration = tempGv.getDate("dateRegistration");
					Time actualStartTime = tempGv.getTime("actualStartTime");
					Time actualEndTime = tempGv.getTime("actualEndTime");
					tempMap.put("partyId", tempGv.getString("partyId"));
					tempMap.put("partyName", PartyUtil.getPersonName(delegator, tempGv.getString("partyId")));
					tempMap.put("workOvertimeRegisId", tempGv.getString("workOvertimeRegisId"));
					tempMap.put("dateRegistration", dateRegistration != null? dateRegistration.getTime(): null);
					tempMap.put("overTimeFromDate", overTimeFromDate != null? overTimeFromDate.getTime(): null);
					tempMap.put("overTimeThruDate", overTimeThruDate != null? overTimeThruDate.getTime(): null);
					tempMap.put("actualStartTime", actualStartTime != null? actualStartTime.getTime(): null);
					tempMap.put("actualEndTime", actualEndTime != null? actualEndTime.getTime(): null);
					tempMap.put("statusId", tempGv.getString("statusId"));
					listReturn.add(tempMap);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} 
		}
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put("listReturn", listReturn);
		return retMap;
	}
	
	/*======================get all employee working late in emplTimesheet========================*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTimesheetWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		try {
			if(emplTimesheetId != null){
				GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
				String customTimePeriodId = emplTimesheets.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				Map<String, String[]> parameters = FastMap.newInstance();
				parameters.put("fromDate", new String[]{String.valueOf(fromDateTs.getTime())});
				parameters.put("thruDate", new String[]{String.valueOf(thruDateTs.getTime())});
				List<EntityCondition> conds = FastList.newInstance();
				List<String> sortField = FastList.newInstance();
				EntityFindOptions opt = new EntityFindOptions();
				Map<String, Object> resultService = dispatcher.runSync("getEmplWorkingLateInPeriod", 
						UtilMisc.toMap("parameters", parameters, "listAllConditions", conds, "listSortFields",sortField, "opts", opt, "userLogin", userLogin));
				if(ServiceUtil.isSuccess(resultService)){
					listReturn = (List<Map<String, Object>>)resultService.get("listIterator");
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		retMap.put("listReturn", listReturn);
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplWorkingLateInPeriod(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String fromDateStr = parameters.get("fromDate")[0];
		String thruDateStr = parameters.get("thruDate")[0];
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		int totalRows = 0;
		int size, page = -1;
		try{
			size = Integer.parseInt(parameters.get("pagesize")[0]);
		}catch(Exception e){
			size = -1;
		}
    	try{
    		page = Integer.parseInt(parameters.get("pagenum")[0]);
    	}catch(Exception e){
    		page = -1;
    	}
		
		int start = size * page;
		int end = start + size;
		try {
			EntityCondition dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateWorkingLate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
																	 EntityOperator.AND,
																	 EntityCondition.makeCondition("dateWorkingLate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			List<GenericValue> listEmplWorkingLate = delegator.findList("EmplWorkingLate", dateConds, null, UtilMisc.toList("dateWorkingLate", "partyId"), null, false);
			totalRows = listEmplWorkingLate.size();
			retMap.put("TotalRows", String.valueOf(totalRows));
			if(end > listEmplWorkingLate.size()){
				end = listEmplWorkingLate.size();
			}
			if(size != -1 && page != -1){
				listEmplWorkingLate = listEmplWorkingLate.subList(start, end);
			}
			for(GenericValue tempGv: listEmplWorkingLate){
				Map<String, Object> tempMap = FastMap.newInstance();
				Timestamp dateWorkingLate = tempGv.getTimestamp("dateWorkingLate");
				Date tempDate = new Date(dateWorkingLate.getTime());
				String partyId = tempGv.getString("partyId");
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("emplWorkingLateId", tempGv.get("emplWorkingLateId"));
				tempMap.put("dateWorkingLate", tempDate.getTime());
				tempMap.put("reason", tempGv.getString("reason"));
				tempMap.put("statusId", tempGv.getString("statusId"));
				tempMap.put("delayTime", tempGv.get("delayTime"));
				List<GenericValue> emplAttandanceTracker = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("dateAttendance", tempDate, "partyId", partyId), UtilMisc.toList("startTime"), false);
				if(UtilValidate.isNotEmpty(emplAttandanceTracker)){
					tempMap.put("arrivalTime", emplAttandanceTracker.get(0).getTime("startTime") != null? emplAttandanceTracker.get(0).getTime("startTime").getTime() : null);
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}

	public static Map<String, Object> getEmplWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String) context.get("partyId");
		String monthStr = (String)context.get("month");
		String yearStr = (String)context.get("year");
		Calendar cal = Calendar.getInstance();
		int month = Integer.parseInt(monthStr);
		int year = Integer.parseInt(yearStr);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Map<String, Object> res = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		res.put("data", listReturn);
		Timestamp timestampFrom = UtilDateTime.getMonthStart(timestamp);
		Timestamp timestampThru = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
		Timestamp toDay = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		if(timestampThru.after(toDay)){
			timestampThru = toDay;
		}
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dateWorkingLate", EntityOperator.GREATER_THAN_EQUAL_TO, timestampFrom),
													EntityOperator.AND,
													EntityCondition.makeCondition("dateWorkingLate", EntityOperator.LESS_THAN_EQUAL_TO, timestampThru)));
		try {
			List<GenericValue> emplListWorkingLate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(conditions), null, 
																		UtilMisc.toList("-dateWorkingLate"), null, false);
			for(GenericValue tempGv: emplListWorkingLate){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				Timestamp dateWorkingLate = tempGv.getTimestamp("dateWorkingLate");
				Date tempDate = new Date(dateWorkingLate.getTime());
				tempMap.put("emplWorkingLateId", tempGv.get("emplWorkingLateId"));
				tempMap.put("dateWorkingLate", dateWorkingLate.getTime());
				tempMap.put("reason", tempGv.getString("reason"));
				tempMap.put("statusId", tempGv.getString("statusId"));
				tempMap.put("delayTime", tempGv.get("delayTime"));
				List<GenericValue> emplAttandanceTracker = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("dateAttendance", tempDate, "partyId", partyId), UtilMisc.toList("startTime"), false);
				if(UtilValidate.isNotEmpty(emplAttandanceTracker)){
					tempMap.put("arrivalTime", emplAttandanceTracker.get(0).getTime("startTime") != null? emplAttandanceTracker.get(0).getTime("startTime").getTime() : null);
				}
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return res;
	}
	
	public static Map<String, Object> updateActualEmplOvertimeWorking(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		//TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String) context.get("partyId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Calendar cal = Calendar.getInstance();
		
		Timestamp timestampFrom = (Timestamp)context.get("fromDate");
		Date startMonthDate = new Date(timestampFrom.getTime());
		Timestamp timestampThru = (Timestamp)context.get("thruDate");
		Timestamp toDay = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		
		if(timestampThru.after(toDay)){
			timestampThru = toDay;
		}
		Date endMonthDate = new Date(timestampThru.getTime());
		//Map<String, Object> res = FastMap.newInstance();
		EntityCondition workOvertimeRegis;
		try {
			Date tmpDate = startMonthDate;
			EntityCondition dateAttendanceConds;
			Map<String, Object> tempMap = FastMap.newInstance();
			EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
			while(tmpDate.before(endMonthDate)){
				dateAttendanceConds = EntityCondition.makeCondition("dateAttendance", tmpDate);					
				List<GenericValue> emplAttendance = delegator.findList("EmplAttendanceTracker", EntityCondition.makeCondition(commonConds, 
																								EntityOperator.AND, dateAttendanceConds), 
																								null, UtilMisc.toList("-endTime"), null, false);
				cal.setTime(tmpDate);
				boolean isWorkOvertime = false;
				
				Long minMinuteOvertime = 0l;//so phut toi thieu tinh lam them gio
				if(UtilValidate.isNotEmpty(emplAttendance)){
					tempMap = TimekeepingUtils.getWorkingShiftDayOfParty(dctx, context, tmpDate);
					Boolean tempIsDayLeave = (Boolean)tempMap.get("isDayLeave");
					Time startTime = emplAttendance.get(0).getTime("startTime");
					Time endTime = emplAttendance.get(0).getTime("endTime");
					if(endTime.before(startTime) || endTime.equals(startTime)){
						endTime = new Time(endTime.getTime() + DateUtil.ONE_DAY_MILLIS);
					}
					Time timeStartCalcOT = null;//thoi gian bat dau tinh lam them theo ca lam viec
					Time timeEndCalcOT = null;//thoi gian ket thuc tinh lam them theo ca lam viec
					
					String tempWorkShiftId = (String)tempMap.get("workingShiftId");
					if(tempWorkShiftId != null){
						GenericValue tempWorkingShift = delegator.findOne("WorkingShift", UtilMisc.toMap("workingShiftId", tempWorkShiftId), false);
						String isAllowOTAfterShift = tempWorkingShift.getString("isAllowOTAfterShift");
						Time shiftEndTime = tempWorkingShift.getTime("shiftEndTime");
						Time shiftStartTime = tempWorkingShift.getTime("shiftStartTime");
						if(shiftEndTime.before(shiftStartTime) || shiftEndTime.equals(shiftStartTime)){
							shiftEndTime = new Time(shiftEndTime.getTime() + DateUtil.ONE_DAY_MILLIS);
						}
						if("Y".equals(isAllowOTAfterShift)){
							minMinuteOvertime = tempWorkingShift.getLong("minMinuteOvertime");
							if(minMinuteOvertime == null){
								minMinuteOvertime = 0l;
							}
							timeStartCalcOT = TimekeepingUtils.getTimeStartCalcOTWS(delegator, tempWorkShiftId);
							timeEndCalcOT = TimekeepingUtils.getTimeEndCalcOTWS(delegator, tempWorkShiftId);							
							if(timeStartCalcOT.before(shiftEndTime)){
								timeStartCalcOT = new Time(timeStartCalcOT.getTime() + DateUtil.ONE_DAY_MILLIS);
							}
							if(timeEndCalcOT.before(timeStartCalcOT) || timeEndCalcOT.equals(timeStartCalcOT)){
								timeEndCalcOT = new Time(timeEndCalcOT.getTime() + DateUtil.ONE_DAY_MILLIS);
							}
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(tmpDate);
							if(tempIsDayLeave){
								isWorkOvertime = true;
								TimekeepingUtils.editWorkOverTimeRegis(delegator, dispatcher, startTime, endTime, userLogin, false, tmpDate, partyId);
							}else if(endTime.after(shiftEndTime)){
								isWorkOvertime = true;
								Time startTimeOT = timeStartCalcOT;
								Time endTimeOT = endTime;
								if(timeStartCalcOT.before(startTime)){
									startTimeOT = startTime;
								}
								if(endTime.after(timeEndCalcOT)){
									endTimeOT = timeEndCalcOT; 
								}
								TimekeepingUtils.editWorkOverTimeRegis(delegator, dispatcher, startTimeOT, endTimeOT, userLogin, false, tmpDate, partyId);
							}
						}
					}							
				}
				//if empl not working overtime, update working overtime registration
				if(!isWorkOvertime){
					workOvertimeRegis = EntityCondition.makeCondition("dateRegistration", tmpDate);
					List<GenericValue> workOverRegisList = delegator.findList("WorkOvertimeRegistration", 
							EntityCondition.makeCondition(workOvertimeRegis, EntityOperator.AND, commonConds), null, null, null, false);
					for(GenericValue removeGv: workOverRegisList){
						removeGv.remove();
					}
				}
				cal.add(Calendar.DATE, 1);
				tmpDate = new Date(cal.getTimeInMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	
	public static Map<String, Object> updateEmplWorkovertime(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String workOvertimeRegisId = (String)context.get("workOvertimeRegisId");
		String actualStartTime = (String)context.get("actualStartTime");
		String actualEndTime = (String)context.get("actualEndTime");
		Time startTime = null;
		Time endTime = null;
		Calendar cal = Calendar.getInstance();
		Calendar tempCal = Calendar.getInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Boolean checkBeforeUpdate = (Boolean)context.get("checkBeforeUpdate"); 
		try {
			GenericValue emplWorkOvertime = delegator.findOne("WorkOvertimeRegistration", UtilMisc.toMap("workOvertimeRegisId", workOvertimeRegisId), false);
			if(emplWorkOvertime == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale)); 
			}
			Date dateRegistration = emplWorkOvertime.getDate("dateRegistration");
			if(checkBeforeUpdate == null || checkBeforeUpdate){
				Map<String, Object> resultService = dispatcher.runSync("checkTimesheetInDateIsEdit", UtilMisc.toMap("date", new Timestamp(dateRegistration.getTime()), "userLogin", userLogin));
				if(ServiceUtil.isSuccess(resultService)){
					Boolean isEdit = (Boolean)resultService.get("isEdit");
					if(!isEdit){
						String emplTimesheetId = (String)resultService.get("emplTimesheetId");
						GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
						String customTimePeriodId = emplTimesheet.getString("customTimePeriodId");
						GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
						Calendar calMsg = Calendar.getInstance();
						calMsg.setTime(customTimePeriod.getDate("fromDate"));
						String fromDate = DateUtil.getDateMonthYearDesc(calMsg);
						calMsg.setTime(customTimePeriod.getDate("thruDate"));
						String thruDate = DateUtil.getDateMonthYearDesc(calMsg);
						calMsg.setTime(dateRegistration);
						String dateRegistrationStr = DateUtil.getDateMonthYearDesc(calMsg);
						String statusEmplTimekeepingId = emplTimesheet.getString("statusId");
						GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusEmplTimekeepingId), false);
						return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotUpdateWorkOverTime_EmplTimesheetStt", 
								UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "date", dateRegistrationStr, 
										"emplTimesheetName", emplTimesheet.getString("emplTimesheetName"), 
										"status", status.getString("description")), locale));
					}
				}
			}
			if(actualStartTime != null){
				tempCal.setTimeInMillis(Long.parseLong(actualStartTime));
				cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));
				cal.set(Calendar.SECOND, tempCal.get(Calendar.SECOND));
				startTime = new Time(cal.getTimeInMillis());
			}
			if(actualEndTime != null){
				tempCal.setTimeInMillis(Long.parseLong(actualEndTime));
				cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));
				cal.set(Calendar.SECOND, tempCal.get(Calendar.SECOND));
				endTime = new Time(cal.getTimeInMillis());
			}
			
			if(startTime != null && endTime != null){
				if(startTime.after(endTime)){
					return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "TimeEnterNotValid", locale));
				}
			}
			if(startTime != null){
				emplWorkOvertime.set("actualStartTime", startTime);
			}
			if(endTime != null){
				emplWorkOvertime.set("actualEndTime", endTime);
			}
			if(context.get("statusId") != null){
				emplWorkOvertime.set("statusId", context.get("statusId"));
			}
			emplWorkOvertime.store();
			
			//update emplTimekeepingSign in date
			dispatcher.runSync("updateWorkOvertimeInEmplTimesheetAtt", UtilMisc.toMap("partyId", emplWorkOvertime.getString("partyId"), "dateAttendance", dateRegistration, "userLogin", userLogin));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateWorkOvertimeInEmplTimesheetAtt(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Date dateAttendance = (Date)context.get("dateAttendance");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("dateAttendance", dateAttendance));
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		//conditions.add(EntityCondition.makeCondition("emplTimekeepingSignId", "LAM_THEM"));
		try {
			List<GenericValue> emplTimesheetAtt = delegator.findList("EmplTimesheetAttendance", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(emplTimesheetAtt)){
				//timesheet attendance in "dateAttendance" created for party, so update "LAM_THEM" emplTimekeepingSign
				Timestamp timestamp = new Timestamp(dateAttendance.getTime());
				Timestamp fromDate = UtilDateTime.getDayStart(timestamp);
				Timestamp thruDate = UtilDateTime.getDayEnd(timestamp);
				Map<String, Object> resultService = dispatcher.runSync("getNbrHourWorkOvertime", 
						UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate, "userLogin", userLogin));
				Float hoursActualWorkOvertime = (Float)resultService.get("hoursActualWorkOvertime");
				List<GenericValue> emplTimesheetAttWorkOvertime = EntityUtil.filterByCondition(emplTimesheetAtt, EntityCondition.makeCondition("emplTimekeepingSignId", "LAM_THEM"));
				List<GenericValue> emplTsAttWorkOverNotConfirm = EntityUtil.filterByCondition(emplTimesheetAtt, EntityCondition.makeCondition("emplTimekeepingSignId", "LAM_THEM_CHUA_XN"));
				//remove all working overtime not confirm
				for(GenericValue tempGv: emplTsAttWorkOverNotConfirm){
					tempGv.remove();
				}
				if(hoursActualWorkOvertime > 0){
					if(UtilValidate.isEmpty(emplTimesheetAttWorkOvertime)){
						String emplTimesheetId = emplTimesheetAtt.get(0).getString("emplTimesheetId");
						Map<String, Object> ctxMap = FastMap.newInstance();
						ctxMap.put("partyId", partyId);
						ctxMap.put("emplTimesheetId", emplTimesheetId);
						ctxMap.put("partyId", partyId);
						ctxMap.put("dateAttendance", dateAttendance);
						ctxMap.put("emplTimekeepingSignId", "LAM_THEM");
						ctxMap.put("hours", (double)hoursActualWorkOvertime);
						ctxMap.put("userLogin", userLogin);
						dispatcher.runSync("createEmplTimesheetAttendance", ctxMap);
					}else{
						for(GenericValue tempGv: emplTimesheetAttWorkOvertime){
							tempGv.set("hours", (double)hoursActualWorkOvertime);
							tempGv.store();
						}
					}
				}else{
					//hoursActualWorkOvertime <= 0, party not workOvertime, so remove all emplTimekeepingSign "LAM_THEM"
					if(UtilValidate.isNotEmpty(emplTimesheetAttWorkOvertime)){
						for(GenericValue tempGv: emplTimesheetAttWorkOvertime){
							tempGv.remove();
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> approvalAllWorkOvertimeInPeriod(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		Date fromDate = new Date(Long.parseLong(fromDateStr));
		Date thruDate = new Date(Long.parseLong(thruDateStr));
		String statusId = (String)context.get("statusId");
		try {
			String department = PartyUtil.getOrgByManager(partyId, delegator);
			Organization buildOrg = PartyUtil.buildOrg(delegator, department, false, false);
			List<GenericValue> emplMgrByPartyIdGv = buildOrg.getDirectEmployee(delegator);
			List<String> emplMgrByParty = EntityUtil.getFieldListFromEntityList(emplMgrByPartyIdGv, "partyId", true);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("dateRegistration", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conditions.add(EntityCondition.makeCondition("dateRegistration", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplMgrByParty));
			/*conditions.add(EntityCondition.makeCondition("statusId", "WOTR_CREATED"));*/
			List<GenericValue> listEmplWOT = delegator.findList("WorkOvertimeRegistration", EntityCondition.makeCondition(conditions), null, null, null, false);
			for(GenericValue tempGv: listEmplWOT){
				String workOvertimeRegisId = tempGv.getString("workOvertimeRegisId");
				dispatcher.runSync("updateEmplWorkovertime", UtilMisc.toMap("workOvertimeRegisId", workOvertimeRegisId, "statusId", statusId, "userLogin", userLogin));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
}
