package com.olbius.basehr.timekeeping.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
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
import org.ofbiz.service.calendar.RecurrenceRule;

import com.olbius.basehr.employee.services.EmployeeLeaveServices;
import com.olbius.basehr.importExport.ImportExcelConfig;
import com.olbius.basehr.importExport.ImportExportExcel;
import com.olbius.basehr.importExport.ImportExportFile;
import com.olbius.basehr.importExport.ImportExportFileFactory;
import com.olbius.basehr.timekeeping.helper.TimekeepingHelper;
import com.olbius.basehr.timekeeping.utils.TimekeepingUtils;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class TimekeepingServices {
	public static Map<String, Object> getPartyAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String dateKeeping = (String)context.get("dateKeeping");
		java.sql.Date date = new java.sql.Date(Long.parseLong(dateKeeping));
		String retValue = "";
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> emplDateAttendance = delegator.findByAnd("EmplAttendanceTracker", 
					UtilMisc.toMap("partyId", partyId, "dateAttendance", date, "orgId", orgId), UtilMisc.toList("startTime"), false);
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
    	String partyGroupId = request.getParameter("partyGroupId");
    	//Map<String, Object> resultService = FastMap.newInstance();
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.DATE, 1);
    	cal.set(Calendar.YEAR, year);
    	cal.set(Calendar.MONTH, month);
    	Timestamp timestamp = new Timestamp(cal.getTimeInMillis()); 
    	Timestamp fromDate = UtilDateTime.getMonthStart(timestamp);
    	Timestamp thruDate = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition(listAllConditions));
			if(UtilValidate.isEmpty(listSortFields)){
				emplList = EntityUtil.orderBy(emplList, UtilMisc.toList("firstName"));
			}else{
				emplList = EntityUtil.orderBy(emplList, listSortFields);
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
				tempMap.put("partyCode", empl.get("partyCode"));
				tempMap.put("partyName", empl.get("fullName"));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		
		retMap.put("listIterator", listReturn);
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTimesheetAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		List<Map<String, Object>> listReturn = FastList.newInstance();
    	Map<String, Object> retMap = FastMap.newInstance();
    	retMap.put("listIterator", listReturn);
    	String partyGroupId = request.getParameter("partyGroupId");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int totalRows = 0;
    	if(partyGroupId == null){
    		retMap.put("TotalRows", String.valueOf(totalRows));
    		return retMap;
    	}
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String emplTimesheetId = request.getParameter("emplTimesheetId");
    	if(emplTimesheetId != null){
    		try {
    			List<String> orgListMgr = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
				if(UtilValidate.isEmpty(orgListMgr)){
					return ServiceUtil.returnError("PartyId is not manage any organization");
				}
				//List<GenericValue> emplTimesheetAttList = delegator.findByAnd("EmplTimesheetAttendance", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), UtilMisc.toList("dateAttendance"),  false);
    			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
				String customTimePeriodId = emplTimesheets.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
				boolean isFullPerms = PartyUtil.isFullPermissionView(delegator, userLogin.getString("userLoginId"));
				if(!orgListMgr.contains(partyGroupId) && !isFullPerms){
					Set<String> allEmplOfUserLogin = FastSet.newInstance();
					for(String orgId: orgListMgr){
						Organization tempBuildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
						List<GenericValue> tempEmplList = tempBuildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
						if(tempEmplList != null){
							List<String> childListStr = EntityUtil.getFieldListFromEntityList(tempEmplList, "partyId", true);
							allEmplOfUserLogin.addAll(childListStr);
						}
					}
					emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allEmplOfUserLogin));
				}
				if(end > emplList.size()){
    				end = emplList.size();
    			}
    			totalRows = emplList.size();
    			emplList = emplList.subList(start, end);
				Calendar cal = Calendar.getInstance();
				
				for(GenericValue employee: emplList){
					Map<String, Object> tempMap = FastMap.newInstance();
					listReturn.add(tempMap);
					tempMap.put("emplTimesheetId", emplTimesheetId);
					tempMap.put("partyId", employee.getString("partyId"));
					tempMap.put("partyCode", employee.getString("partyCode"));
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
				return ServiceUtil.returnError(e.getLocalizedMessage());
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	
	/*public static Map<String, Object> updateEmplTimesheetAttendance(DispatchContext dctx, Map<String, Object> context){
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
			//check combine temporary pending
			Map<String, Object> mapValidCombine = TimekeepingUtils.checkCombineValidEmplTimekeepingSign(delegator, emplTimekeepingSignIdSubmit);
			Boolean isValid = (Boolean)mapValidCombine.get("isValid");
			if(!isValid){
				String emplTimekeepingSignId = (String)mapValidCombine.get("emplTimekeepingSignId");
				String emplTimekeepingSignIdTo = (String)mapValidCombine.get("emplTimekeepingSignIdTo");
				GenericValue emplTimekeepingSign = delegator.findOne("EmplTimekeepingSign", UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSignId), false);
				GenericValue emplTimekeepingSignTo = delegator.findOne("EmplTimekeepingSign", UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSignIdTo), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplTimekeepingSignListCannotCombine", 
						UtilMisc.toMap("emplTimekeepingSignId", emplTimekeepingSign.get("description") + " - (" + emplTimekeepingSign.get("sign") + ")",
								"emplTimekeepingSignIdTo", emplTimekeepingSignTo.get("description") + " - (" + emplTimekeepingSignTo.get("sign") + ")"), locale));
			}
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
						List<GenericValue> emplTimesheetAttendanceWorkingShift = delegator.findByAnd("EmplTimesheetAttendanceWorkingShift", UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "dateAttendance", dateAttendance, "emplTimekeepingSignId", tempDeleteGv.getString("emplTimekeepingSignId")), null, false);
						for(GenericValue temp: emplTimesheetAttendanceWorkingShift){
							temp.remove();
						}
						tempDeleteGv.remove();
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}*/
	
	public static Map<String, Object> updateEmplTimesheetAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		String emplTimekeepingSignId = (String)context.get("emplTimekeepingSignId");
		Date dateAttendance = (Date)context.get("dateAttendance");
		try {
			GenericValue emplTimesheetAttendance = delegator.findOne("EmplTimesheetAttendance", 
					UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "emplTimekeepingSignId", emplTimekeepingSignId, "dateAttendance", dateAttendance), false);
			if(emplTimesheetAttendance != null){
				emplTimesheetAttendance.setNonPKFields(context);
				emplTimesheetAttendance.store();
			}else{
				dispatcher.runSync("createEmplTimesheetAttendance", context);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteEmplTimesheetAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		String emplTimekeepingSignId = (String)context.get("emplTimekeepingSignId");
		Date dateAttendance = (Date)context.get("dateAttendance");
		try {
			GenericValue emplTimesheetAttendance = delegator.findOne("EmplTimesheetAttendance", 
					UtilMisc.toMap("partyId", partyId, "emplTimesheetId", emplTimesheetId, "emplTimekeepingSignId", emplTimekeepingSignId, "dateAttendance", dateAttendance), false);
			if(emplTimesheetAttendance != null){
				emplTimesheetAttendance.remove();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
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
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		try {
			List<String> orgListMgr = PartyUtil.getListOrgManagedByParty(delegator, userLoginId);
			List<String> allListOrg = FastList.newInstance();
			allListOrg.addAll(orgListMgr);
			for(String orgId: orgListMgr){
				Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
				List<GenericValue> tempChildList = buildOrg.getChildList();				
				if(tempChildList != null){
					for(GenericValue child: tempChildList){
						allListOrg.add(child.getString("partyId"));
					}
				}
				List<String> parentOrgList = PartyUtil.getAncestorOfParty(delegator, orgId);
				allListOrg.addAll(parentOrgList);
			}
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "EMPL_TS_DELETED"));
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allListOrg));
			EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
			listIterator = delegator.find("EmplTSAndCustomTimePeriodAndPartyGroup", tmpCond, null, null, listSortFields, opts);
		}catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> createEmplTimesheets(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
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
//			String emplTimesheetNoWhiteSpace = CommonUtil.removeWhiteSpace(emplTimesheetName);
//			if(!CommonUtil.containsValidCharacter(emplTimesheetNoWhiteSpace)){
//				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "TimesheetNameContainNotValidChar", locale)); 
//			}
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			String fromDateStr = DateUtil.getDateMonthYearDesc(cal);
			cal.setTime(thruDate);
			String thruDateStr = DateUtil.getDateMonthYearDesc(cal);
			List<GenericValue> allChild = buildOrg.getAllDepartmentList(delegator);
			if(allChild != null){
				for(GenericValue tempChild: allChild){
					String tempPartyId = tempChild.getString("partyId");
					List<GenericValue> checkEmplTimesheet = delegator.findByAnd("EmplTimesheets", UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "partyId", tempPartyId), null, false);
					if(UtilValidate.isNotEmpty(checkEmplTimesheet)){
						String groupName = PartyHelper.getPartyName(delegator, partyId, false);
						String childGroupName = PartyHelper.getPartyName(delegator, tempPartyId, false);
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplTimesheetIsExists", 
								UtilMisc.toMap("fromDate", fromDateStr, "thruDate", thruDateStr, "groupName", groupName, "parentGroupName", childGroupName), locale));
					}
				}
			}
			Map<String, Object> checkMap = TimekeepingUtils.checkEmplTimesheetValid(delegator, customTimePeriodId, partyId, userLogin.getString("userLoginId"));
			Boolean isValid = (Boolean)checkMap.get("isValid");
			if(isValid != null && !isValid){
				String parentPartyId = (String)checkMap.get("partyId");
				String groupName = PartyHelper.getPartyName(delegator, partyId, false);
				String parentGroupName = PartyHelper.getPartyName(delegator, parentPartyId, false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplTimesheetIsExists", 
						UtilMisc.toMap("fromDate", fromDateStr, "thruDate", thruDateStr, "groupName", groupName, "parentGroupName", parentGroupName), locale));
			}
			GenericValue emplTimesheets = delegator.makeValue("EmplTimesheets");
			emplTimesheets.setAllFields(context, false, null, false);
			String emplTimesheetId = delegator.getNextSeqId("EmplTimesheets");
			emplTimesheets.set("emplTimesheetId", emplTimesheetId);
			emplTimesheets.set("statusId", statusId);
			
			emplTimesheets.create();
			retMap.put("emplTimesheetId", emplTimesheetId);
			if("Y".equals(importDataTimeRecord)){
				Map<String, Object> contextTmp = FastMap.newInstance();
				contextTmp.put("emplTimesheetId", emplTimesheetId);
				contextTmp.put("userLogin", userLogin);
				contextTmp.put("partyGroupId", partyId);
				contextTmp.put("timeZone", timeZone);
				contextTmp.put("locale", locale);
				dispatcher.schedule("pool", "importTimesheetDataFromTimeRecord", contextTmp, UtilDateTime.nowTimestamp().getTime(), RecurrenceRule.DAILY, 1, 1, -1, 0);
				//dispatcher.runSync("importTimesheetDataFromTimeRecord", UtilMisc.toMap("emplTimesheetId", emplTimesheetId, "userLogin", userLogin, "timeZone", timeZone));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
			if(emplTimesheetName == null || emplTimesheetName.trim().length() <= 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplTimesheetNameEmptyError", locale));
			}
			GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheet == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));	
			}
			emplTimesheet.setNonPKFields(context);
			emplTimesheet.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotDeleteTimesheetCalculating", locale));
			}
			String partyId = emplTimesheets.getString("partyId");
			if(!PartyUtil.isFullPermissionView(delegator, userLogin.getString("userLoginId"))){
				String customTimePeriodId = emplTimesheets.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				List<String> orgListMgr = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("partyId"), fromDateTs, thruDateTs);
				boolean isDelPerms = false;
				if(orgListMgr.contains(partyId)){
					isDelPerms = true;
				}else{
					mainloop:
					for(String orgId: orgListMgr){
						Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
						List<GenericValue> tempOrgList = buildOrg.getChildList();
						if(tempOrgList != null){
							for(GenericValue tempOrg: tempOrgList){
								if(partyId.equals(tempOrg.getString("partyId"))){
									isDelPerms = true;
									break mainloop;
								}
							}
						}
					}
					if(!isDelPerms){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotDeleteTimesheet", locale));
					}
				}
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
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
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
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotFindEmplTimesheet", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), locale));
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
			ntfCtx.put("header", UtilProperties.getMessage("BaseHRPayrollUiLabels", "ApprovalTimesheet", UtilMisc.toMap("emplTimesheetName", emplTimesheet.getString("emplTimesheetName"), "emplTimesheetId", emplTimesheetId), locale));
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("sendToSender", "Y");
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "SendProposalSuccessful", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotFindEmplTimesheet", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), locale));
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
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
			if(UtilValidate.isEmpty(wotNotAppr)){
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDate);
				String fDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				cal.setTime(thruDate);
				String tDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "WorkOvertimeEmplAppr", UtilMisc.toMap("fromDate", fDate, "thruDate", tDate), locale));
			}
			for(GenericValue tempGv: wotNotAppr){
				String partyId = tempGv.getString("partyId");
				partyMgrIdSet.addAll(PartyUtil.getManagerOfEmpl(delegator, partyId, fromDateTs, thruDateTs, userLogin.getString("userLoginId")));
			}
			List<String> partyMgrIdList = FastList.newInstance();
			partyMgrIdList.addAll(partyMgrIdSet);
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("partiesList", partyMgrIdList);
			ntfCtx.put("targetLink", "fromDate="+ fromDate.getTime() + ";thruDate=" + thruDate.getTime());
			ntfCtx.put("header", UtilProperties.getMessage("BaseHRPayrollUiLabels", "ApprovalWorkingOvertimeEmpl", locale));
			ntfCtx.put("action", "ViewEmplWorkingOvertimeList");
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
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "SentRequestApproval", locale));
	}
	
	public static Map<String, Object> reCalcEmplTimesheet(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		try {
			GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheet == null){
				return ServiceUtil.returnError("cannot find emplTimesheet have id is: " + emplTimesheetId);
			}
			String partyGroupId = emplTimesheet.getString("partyId");
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
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
			List<String> emplListId = null;
			if(partyGroupId != null){
				String customTimePeriodId = (String)emplTimesheet.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> empList = buildOrg.getEmplInOrgAtPeriod(delegator, new Timestamp(fromDate.getTime()), UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime())));
				if(!PartyUtil.isFullPermissionView(delegator, userLogin.getString("userLoginId"))){
					List<String> tempEmplListId = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), new Timestamp(fromDate.getTime()), UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime())));
					empList = EntityUtil.filterByCondition(empList, EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, tempEmplListId));
				}
				emplListId = EntityUtil.getFieldListFromEntityList(empList, "partyId", true);
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListId));
			}
			List<GenericValue> emplTimesheetAtt = delegator.findList("EmplTimesheetAttendance", EntityCondition.makeCondition(conditions), null, null, null, false);
			for(GenericValue tempGv: emplTimesheetAtt){
				tempGv.remove();
			}
			dispatcher.runSync("importTimesheetDataFromTimeRecord", UtilMisc.toMap("emplTimesheetId", context.get("emplTimesheetId"), "partyGroupId", partyGroupId,
					"userLogin", userLogin, "timeZone", context.get("timeZone"), "emplListId", emplListId, "locale", context.get("locale")));
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
		List<String> emplListId = (List<String>)context.get("emplListId");
		try {
			GenericValue emplTimesheets = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
			if(emplTimesheets == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));
			}
			String orgId = PartyUtil.getSubsidiaryOfPartyGroup(delegator, emplTimesheets.getString("partyId"));
			String customTimePeriodId = emplTimesheets.getString("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false); 
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
			Timestamp nowTimestamp = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			if(thruDateTs.after(nowTimestamp)){
				thruDateTs = nowTimestamp;
			}
			if(fromDateTs.after(thruDateTs)){
				emplTimesheets.set("statusId", "EMPL_TS_CREATED");
			    emplTimesheets.store();
			    return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
			}
			String timesheetPartyId = null;
			if(emplListId == null){
				if(partyGroupId != null){
					timesheetPartyId = partyGroupId;
				}else{
					timesheetPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				}
				Organization buildOrg = PartyUtil.buildOrg(delegator, timesheetPartyId, true, false);
				List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
				
				if(emplList == null){
					emplList = FastList.newInstance();
				}
				emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDateTs);
			Map<String, Object> resultService = FastMap.newInstance();
			if(emplListId != null){
				for(String partyId: emplListId){
					Timestamp timestamp = new Timestamp(fromDateTs.getTime());
					resultService = dispatcher.runSync("updateActualEmplOvertimeWorking", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, 
							"fromDate", fromDateTs, "thruDate", thruDateTs, "emplTimesheetId", emplTimesheetId, "orgId", orgId,
							"timeZone", timeZone, "locale", locale));
					resultService = dispatcher.runSync("updateActualEmplWrokingLate", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
							"fromDate", fromDateTs, "thruDate", thruDateTs, "orgId", orgId,
							"emplTimesheetId", emplTimesheetId,
							"timeZone", timeZone, "locale", locale));
					
					while (timestamp.before(thruDateTs)) {
						Date tempDate = new Date(timestamp.getTime());
						resultService = dispatcher.runSync("getEmplTimekeepingSignInDate", 
								UtilMisc.toMap("partyId", partyId, "dateAttendance", tempDate, "userLogin", userLogin, "orgId", orgId));
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
										Set<String> keys = ((Map<String, Object>)value).keySet();
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
		    ntfCtx.put("header", UtilProperties.getMessage("BaseHRPayrollUiLabels", "EmplTimesheetCalculated", UtilMisc.toMap("fromDate", fromDateDes, "thruDate", thruDateDes), locale));
		    ntfCtx.put("state", "open");
		    ntfCtx.put("targetLink", "emplTimesheetId=" + emplTimesheetId);
		    ntfCtx.put("action", "ViewEmplTimesheetList");
		    ntfCtx.put("sendToSender", "Y");
		    ntfCtx.put("userLogin", userLogin);
		    dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateEmplAttendanceTracker(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Date dateAttendance = (Date)context.get("dateAttendance");
		String partyId = (String)context.get("partyId");
		Time startTime = (Time)context.get("startTime");
		Time endTime = (Time)context.get("endTime");
		Delegator delegator = dctx.getDelegator();
		String orgId = (String)context.get("orgId");
		//Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> emplAttendanceTracker = delegator.findByAnd("EmplAttendanceTracker", 
					UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance, "orgId", orgId), null, false);
			if(UtilValidate.isEmpty(emplAttendanceTracker)){
				GenericValue tempEmplAttendance = delegator.makeValue("EmplAttendanceTracker");
				tempEmplAttendance.set("partyId", partyId);
				tempEmplAttendance.set("dateAttendance", dateAttendance);
				tempEmplAttendance.set("startTime", startTime);
				tempEmplAttendance.set("endTime", endTime);
				tempEmplAttendance.set("orgId", orgId);
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
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "TimeEnterNotValid", locale));
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
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplTimesheetGeneral(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Map<String, Object> retMap = FastMap.newInstance();
		String emplTimesheetId = request.getParameter("emplTimesheetId");
		String partyGroupId = request.getParameter("partyGroupId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		
    	int totalRows = 0;
		if(emplTimesheetId != null){
			try {
				if(partyGroupId == null){
					partyGroupId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				}
				List<String> listFieldInEntity = FastList.newInstance();
				listFieldInEntity.add("partyId");
				listFieldInEntity.add("partyName");
				List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
				List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
				EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
				
				List<String> sortedFieldInEntity = FastList.newInstance();
				List<String> sortedFieldNotInEntity = FastList.newInstance();
				if(UtilValidate.isNotEmpty(listSortFields)){
					EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
				}
				
				List<String> orgListMgr = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
				if(UtilValidate.isEmpty(orgListMgr)){
					return ServiceUtil.returnError("PartyId is not manage any organization");
				}
				GenericValue emplTimesheet = delegator.findOne("EmplTimesheets", UtilMisc.toMap("emplTimesheetId", emplTimesheetId), false);
				//List<GenericValue> emplTimesheetAtt = delegator.findList("EmplTimesheetAttendance", EntityCondition.makeCondition("emplTimesheetId", emplTimesheetId), null, UtilMisc.toList("partyId"), null, false);
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				
				String customTimePeriodId = emplTimesheet.getString("customTimePeriodId");
				//String partyIdTimesheet = emplTimesheet.getString("partyId");
				//String orgId = PartyUtil.getSubsidiaryOfPartyGroup(delegator, partyIdTimesheet);
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDate = customTimePeriod.getDate("fromDate");
				Date thruDate = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
				Map<String, Object> mapEmpDayLeave = FastMap.newInstance();
				List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
				Map<String, Object> resultService = FastMap.newInstance();
				DecimalFormat df = new DecimalFormat("#.#");
				boolean isFullPerms = PartyUtil.isFullPermissionView(delegator, userLogin.getString("userLoginId"));
				if(!orgListMgr.contains(partyGroupId) && !isFullPerms){
					Set<String> allEmplOfUserLogin = FastSet.newInstance();
					for(String orgMgrId: orgListMgr){
						Organization tempBuildOrg = PartyUtil.buildOrg(delegator, orgMgrId, true, false);
						List<GenericValue> tempEmplList = tempBuildOrg.getEmplInOrgAtPeriod(delegator, fromDateTs, thruDateTs);
						if(tempEmplList != null){
							List<String> childListStr = EntityUtil.getFieldListFromEntityList(tempEmplList, "partyId", true);
							allEmplOfUserLogin.addAll(childListStr);
						}
					}
					emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, allEmplOfUserLogin));
				}
				if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
					emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition(condsForFieldInEntity));
				}
				if(UtilValidate.isEmpty(listSortFields)){
					sortedFieldInEntity.add("firstName");
					emplList = EntityUtil.orderBy(emplList, sortedFieldInEntity);
				}
				
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
				EntityCondition dateWorkingLateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDateTs),
																					 EntityJoinOperator.AND,
																					 EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDateTs));
				for(GenericValue employee: emplList){
					Map<String, Object> tempMap = FastMap.newInstance();
					String partyId = employee.getString("partyId");
					List<GenericValue> emplPositionTypes = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
					String emplPositionTypeId = "";
					if(UtilValidate.isNotEmpty(emplPositionTypes)){
						emplPositionTypeId = emplPositionTypes.get(0).getString("emplPositionTypeId");
					}
					Float totalDayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDateTs, thruDateTs, locale, timeZone);
					mapEmpDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, emplTimesheetId, fromDateTs, thruDateTs, partyId);
					Float totalDayLeave = (Float)mapEmpDayLeave.get("totalDayLeave");
					
					//Float totalDayLeaveApprove = (Float)mapEmpDayLeave.get("leavePaid") + (Float)mapEmpDayLeave.get("leaveUnPaid");
					Float totalDayLeavePaidApproved = (Float)mapEmpDayLeave.get("leavePaid");
					
					resultService = dispatcher.runSync("getNbrHourWorkOvertime", UtilMisc.toMap("partyId", partyId, "fromDate", fromDateTs, "thruDate", thruDateTs, "userLogin", userLogin));
					Float hoursActualWorkOvertime = (Float)resultService.get("hoursActualWorkOvertime");
					List<GenericValue> emplWorkingLateList = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(dateWorkingLateConds,
																									EntityJoinOperator.AND,
																									EntityCondition.makeCondition("partyId", partyId)), null, null, null, false);
					Long totalMinutesWorkLate = 0l;
					for(GenericValue workingLate: emplWorkingLateList){
						Long tempDelayTime = workingLate.getLong("delayTime");
						if(tempDelayTime != null){
							totalMinutesWorkLate += tempDelayTime;
						}
					}
					//tempMap.put("dayLeaveApprove", totalDayLeaveApprove );
					tempMap.put("totalDayLeave", totalDayLeave);
					tempMap.put("totalDayLeavePaidApproved", totalDayLeavePaidApproved);
					tempMap.put("overtimeActual", df.format(hoursActualWorkOvertime));
					tempMap.put("totalWorkingLateHour", df.format(totalMinutesWorkLate));
					tempMap.put("partyId", partyId);
					tempMap.put("partyCode", employee.getString("partyCode"));
					tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
					tempMap.put("emplPositionTypeId", emplPositionTypeId);
					tempMap.put("totalDayWork", totalDayWork);
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
					if(end > totalRows){
						end = totalRows;
					}
					listReturn = listReturn.subList(start, end);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			} catch (GenericServiceException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		retMap.put("listIterator", listReturn);
		return retMap;
	}
	
	/*public static Map<String, Object> getEmplWorkingLateHours(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if(thruDate.after(nowTimestamp)){
			thruDate = nowTimestamp;
		}
		Date startDate = new Date(fromDate.getTime());
		Date endDate = new Date(thruDate.getTime());
		String orgId = (String)context.get("orgId");
		float totalHoursLate = 0f;
		
		try {
			Calendar cal = Calendar.getInstance();
			List<GenericValue> emplAttendanceTrackers;
			while (startDate.before(endDate)|| startDate.equals(endDate)) {
				cal.setTime(startDate);
				emplAttendanceTrackers = delegator.findByAnd("EmplAttendanceTracker", 
						UtilMisc.toMap("partyId", partyId, "dateAttendance", startDate, "orgId", orgId), UtilMisc.toList("startTime"), false);
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put("totalHoursLate", totalHoursLate);
		return retMap;
	}*/
	
	/*public static Map<String, Object> updateEmplWorkingLateInPeriod(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		String orgId = (String)context.get("orgId");
		EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
		Map<String, Object> resultService;
		try {
			while(fromDate.before(thruDate)){
				Timestamp tempThruDate = UtilDateTime.getDayEnd(fromDate);
				resultService = dispatcher.runSync("getEmplWorkingLateHours", 
						UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", tempThruDate, "orgId", orgId));
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
					}
				}
				fromDate = UtilDateTime.getDayStart(fromDate, 1);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		return retMap;
	}*/
	
	public static Map<String, Object> updateActualEmplWrokingLate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String) context.get("partyId");
		String orgId = (String)context.get("orgId");
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
				resultService = dispatcher.runSync("getEmplWorkingLateHours", 
						UtilMisc.toMap("partyId", partyId, "fromDate", timestampFrom, "thruDate", tempThruDate, "orgId", orgId));
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
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		return retMap;
	}
	
	public static Map<String, Object> updateEmplWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		Delegator delegator = dctx.getDelegator();
		String emplWorkingLateId = (String)context.get("emplWorkingLateId");
		try {
			GenericValue emplWorkingLate = delegator.findOne("EmplWorkingLate", UtilMisc.toMap("emplWorkingLateId", emplWorkingLateId), false);
			if(emplWorkingLate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale));
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
//		String partyId = userLogin.getString("partyId");
		try {
			List<String> emplList = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), fromDate, thruDate);
			if(UtilValidate.isEmpty(emplList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "NotEmplWorkingLate", locale));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplList));
			conditions.add(EntityCondition.makeCondition("statusId", "EMPL_LATE_CREATED"));
			List<GenericValue> emplListWorkingLateUpdate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isEmpty(emplListWorkingLateUpdate)){
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDate);
				String fromDateLabel = DateUtil.getDateMonthYearDesc(cal);
				cal.setTime(thruDate);
				String thruDateLabel = DateUtil.getDateMonthYearDesc(cal);
				Map<String, Object> errorMap = ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmployeeUiLabels", "AllEmplWorkingLateApproval", UtilMisc.toMap("fromDate", fromDateLabel, "thruDate", thruDateLabel), locale));
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
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
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
		String orgId = (String)context.get("orgId");
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
			
			resultService = dispatcher.runSync("getEmplWorkingLateHours", 
					UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, "partyId", partyId, "userLogin", userLogin, "orgId", orgId));
			Float totalHoursLate = (Float)resultService.get("totalHoursLate");
			if(totalHoursLate > 0){
				attendanceSignAndHour.put("DI_MUON", UtilMisc.toMap("hours", (double)totalHoursLate));
			}
			//List<GenericValue> workingShiftList = TimekeepingUtils.getAllWorkingShift(delegator, UtilMisc.toList("startTime"));
			List<GenericValue> dateEmplAttendanceTracker = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("partyId", partyId, "dateAttendance", dateAttendance), UtilMisc.toList("startTime"), false);
			
			Map<String, Object> mapWsDayOfParty = TimekeepingUtils.getWorkingShiftDayOfParty(dctx, context, dateAttendance);
			Boolean isDayLeave = (Boolean)mapWsDayOfParty.get("isDayLeave");
			String workingShiftId = null;
			workingShiftId = (String)mapWsDayOfParty.get("workingShiftId");
			if(!isDayLeave){
				Time startTime = null;
				Time endTime = null;
				
				Double workdayInDate = 0d;
				String workTypeId = null;
				if(UtilValidate.isNotEmpty(dateEmplAttendanceTracker)){
					startTime = dateEmplAttendanceTracker.get(0).getTime("startTime");
					endTime = dateEmplAttendanceTracker.get(dateEmplAttendanceTracker.size() - 1).getTime("endTime");				
					if(startTime != null && endTime != null){
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
								/*DecimalFormat df = new DecimalFormat("#.#");
								df.setRoundingMode(RoundingMode.CEILING);*/
								Long nbrHourWorked = DateUtil.calculateHoursBetweenTimes(startTime, endTime);
								Long nbrHoursMustWork = DateUtil.calculateHoursBetweenTimes(shiftStartTime, shiftEndTime);
								if(nbrHoursMustWork != 0){
									float tempWorkDay = (float)nbrHourWorked/nbrHoursMustWork;
									if("SECOND_HALF_SHIFT".equals(workTypeId) || "FIRST_HALF_SHIFT".equals(workTypeId)){
										tempWorkDay /= 2;
									}
									Map<String, Double> tempMap = FastMap.newInstance();
									String emplTimekeepingSignId = "DI_LAM";
									workdayInDate = Math.round(tempWorkDay * 2)/2d;
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
							attendanceSignAndHour.put(entry.getKey(), UtilMisc.toMap("workday", entry.getValue()));
						}
					}
				}
				
			}else if(dateEmplAttendanceTracker.size() == 0){
				Boolean isHoliday = (Boolean)mapWsDayOfParty.get("isHoliday");
				Double workday = null;
				if(mapWsDayOfParty.get("workTypeId") != null){
					String workTypeId = (String)mapWsDayOfParty.get("workTypeId");
					if("ALL_SHIFT".equals(workTypeId)){
						workday = 1d;
					}else if("SECOND_HALF_SHIFT".equals(workTypeId) || "FIRST_HALF_SHIFT".equals(workTypeId)){
						workday = 1d/2;
					}
				}
				if(isHoliday != null && isHoliday){
					Map<String, Object> tempMap = FastMap.newInstance();
					tempMap.put("workday", workday);
					attendanceSignAndHour.put("NGHI_LE", tempMap);
				}else{
					attendanceSignAndHour.put("_NA_", null);
				}
			}
		} catch (GenericEntityException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
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
		commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("statusId", "LEAVE_APPROVED"));
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
					String emplLeaveReasonTypeId = tempGv.getString("emplLeaveReasonTypeId");
					Timestamp fromDateLeave = tempGv.getTimestamp("fromDate");
					Timestamp thruDateLeave = tempGv.getTimestamp("thruDate");
					String fromDateLeaveTypeId = tempGv.getString("fromDateLeaveTypeId");
					String thruDateLeaveTypeId = tempGv.getString("thruDateLeaveTypeId");
					GenericValue emplLeaveReasonType = delegator.findOne("EmplLeaveReasonType", UtilMisc.toMap("emplLeaveReasonTypeId", emplLeaveReasonTypeId), false);
					String emplTimekeepingSignId = emplLeaveReasonType.getString("emplTimekeepingSignId");
					if(workdayInDate == 0d){
						//so ngay cong cua nhan vien la 0 => nhan vien nghi ca ngay
						if(dayStart.equals(fromDateLeave) && EmployeeLeaveServices.SECOND_HALF_DAY.equals(fromDateLeaveTypeId)||
								dayEnd.equals(thruDateLeave) && EmployeeLeaveServices.FIRST_HALF_DAY.equals(thruDateLeaveTypeId)){
							//nhan vien nghi ca ngay nhung don xin phep chi nghi nua ngay => nua ngay nghi "NGHI_KHONG_LY_DO"
							tempMap.put(emplTimekeepingSignId, 0.5);
							if(dayWorkKeeping - 0.5 > 0){
								tempMap.put("NGHI_KHONG_LY_DO", dayWorkKeeping - 0.5);
							}
						}else{
							tempMap.put(emplTimekeepingSignId, dayWorkKeeping);
						}
					}else if(workdayInDate <= 0.5){
						//nhan vien lam viec nua ngay, nghi nua ngay
						tempMap.put(emplTimekeepingSignId, 1 - workdayInDate);
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
					tempMap.put("NGHI_KHONG_LY_DO", workdayLeaveNbr);
					leaveTimekeepingSign.add(tempMap);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	 public static Map<String, Object> getTimeKeepingEmplInPeriod(DispatchContext dctx, Map<String, Object> context){
	    	//Delegator delegator = dctx.getDelegator();
	    	String partyId = (String)context.get("partyId");
	    	String emplTimesheetId = (String)context.get("emplTimesheetId");
	    	//List<String> attendanceTypeIds = (List<String>) context.get("attendanceTypeIds"); 
	    	Timestamp fromDate = (Timestamp)context.get("fromDate");
	    	Timestamp thruDate = (Timestamp)context.get("thruDate");
	    	//List<EntityCondition> conditions = FastList.newInstance();
	    	Map<String, Object> retMap = FastMap.newInstance();
	    	Locale locale = (Locale)context.get("locale");
	    	TimeZone timeZone = (TimeZone)context.get("timeZone");
	    	float countTimekeeping = 0;
	    	
	    	try {
	    		
	    		float totalDayWork = TimekeepingUtils.getDayWorkOfPartyInPeriod(dctx, partyId, fromDate, thruDate, locale, timeZone);
	    		Map<String, Object> mapEmplDayLeave = TimekeepingUtils.getEmplDayLeaveByTimekeeper(dctx, emplTimesheetId, fromDate, thruDate, partyId); 
	    		float totalDayLeave = (Float)mapEmplDayLeave.get("totalDayLeave");
	    		float leavePaid = (Float)mapEmplDayLeave.get("leavePaid");
	    		countTimekeeping = totalDayWork - (totalDayLeave - leavePaid);
	    			
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
		cal.set(Calendar.DATE, 1);
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
		//String partyId = userLogin.getString("partyId");
		if(fromDateStr != null && thruDateStr != null){
			try {
				Date startDate = new Date(Long.parseLong(fromDateStr));
				Date endDate = new Date(Long.parseLong(thruDateStr));
				Timestamp fromDateTs = new Timestamp(startDate.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
				List<String> emplList = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), fromDateTs, thruDateTs);
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplList));
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
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> getEmplWorkingLateInPeriod(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		//String partyId = userLogin.getString("partyId");
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
			List<String> emplList = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), fromDate, thruDate);			
			EntityCondition dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateWorkingLate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
																	 EntityOperator.AND,
																	 EntityCondition.makeCondition("dateWorkingLate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
			EntityCondition partyConds = EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplList);
			List<GenericValue> listEmplWorkingLate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, partyConds), null, UtilMisc.toList("dateWorkingLate", "partyId"), null, false);

			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("emplWorkingLateId");
			listFieldInEntity.add("reason");
			listFieldInEntity.add("statusId");
			listFieldInEntity.add("delayTime");
			listFieldInEntity.add("partyId");
			
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				listEmplWorkingLate = EntityConditionUtils.doFilterGenericValue(listEmplWorkingLate, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("emplWorkingLateId");
			}
			listEmplWorkingLate = EntityUtil.orderBy(listEmplWorkingLate, sortedFieldInEntity);
			boolean isFilterAdvance = false;
			
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = listEmplWorkingLate.size();
				if(end > listEmplWorkingLate.size()){
					end = listEmplWorkingLate.size();
				}
			}else{
				isFilterAdvance = true;
			}
			if(end > listEmplWorkingLate.size()){
				end = listEmplWorkingLate.size();
			}
//			totalRows = listEmplWorkingLate.size();
//			retMap.put("TotalRows", String.valueOf(totalRows));
//			if(end > listEmplWorkingLate.size()){
//				end = listEmplWorkingLate.size();
//			}
//			if(size != -1 && page != -1){
//				listEmplWorkingLate = listEmplWorkingLate.subList(start, end);
//			}
			for(GenericValue tempGv: listEmplWorkingLate){
				Map<String, Object> tempMap = FastMap.newInstance();
				Timestamp dateWorkingLate = tempGv.getTimestamp("dateWorkingLate");
				Date tempDate = new Date(dateWorkingLate.getTime());
				String tempPartyId = tempGv.getString("partyId");
				tempMap.put("partyId", tempPartyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, tempPartyId));
				tempMap.put("emplWorkingLateId", tempGv.get("emplWorkingLateId"));
				tempMap.put("dateWorkingLate", tempDate.getTime());
				tempMap.put("reason", tempGv.getString("reason"));
				tempMap.put("statusId", tempGv.getString("statusId"));
				tempMap.put("delayTime", tempGv.get("delayTime"));
				List<GenericValue> emplAttandanceTracker = delegator.findByAnd("EmplAttendanceTracker", UtilMisc.toMap("dateAttendance", tempDate, "partyId", tempPartyId), UtilMisc.toList("startTime"), false);
				if(UtilValidate.isNotEmpty(emplAttandanceTracker)){
					tempMap.put("arrivalTime", emplAttandanceTracker.get(0).getTime("startTime") != null? emplAttandanceTracker.get(0).getTime("startTime").getTime() : null);
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
					end  = listReturn.size();
				}
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		retMap.put("listIterator", listReturn);
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
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		String orgId = (String)context.get("orgId");
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
			EntityCondition orgConds = EntityCondition.makeCondition("orgId", orgId);
			while(tmpDate.before(endMonthDate)){
				dateAttendanceConds = EntityCondition.makeCondition("dateAttendance", tmpDate);
				EntityCondition conds = EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, orgConds);
				List<GenericValue> emplAttendance = delegator.findList("EmplAttendanceTracker", EntityCondition.makeCondition(conds, 
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
					if(startTime != null && endTime != null){
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
								timeStartCalcOT = TimekeepingUtils.getTimeStartCalcOTWS(delegator, tempWorkShiftId, tmpDate);
								timeEndCalcOT = TimekeepingUtils.getTimeEndCalcOTWS(delegator, tempWorkShiftId, tmpDate);
								if(timeStartCalcOT.before(shiftStartTime)){
									timeStartCalcOT = new Time(timeStartCalcOT.getTime() + DateUtil.ONE_DAY_MILLIS);
								}
								if(timeEndCalcOT.before(timeStartCalcOT) || timeEndCalcOT.equals(timeStartCalcOT)){
									timeEndCalcOT = new Time(timeEndCalcOT.getTime() + DateUtil.ONE_DAY_MILLIS);
								}
								Calendar tempCal = Calendar.getInstance();
								tempCal.setTime(tmpDate);
								if(tempIsDayLeave){
									isWorkOvertime = true;
									TimekeepingUtils.editWorkOverTimeRegis(delegator, dispatcher, startTime, endTime, userLogin, false, tmpDate, partyId, emplTimesheetId);
								}else if(endTime.after(timeStartCalcOT)){//endTime.after(shiftEndTime)
									isWorkOvertime = true;
									Time startTimeOT = timeStartCalcOT;
									Time endTimeOT = endTime;
									if(timeStartCalcOT.before(startTime)){
										startTimeOT = startTime;
									}
									if(endTime.after(timeEndCalcOT)){
										endTimeOT = timeEndCalcOT; 
									}
									TimekeepingUtils.editWorkOverTimeRegis(delegator, dispatcher, startTimeOT, endTimeOT, userLogin, false, tmpDate, partyId, emplTimesheetId);
								}
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
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	
	public static Map<String, Object> updateEmplWorkovertime(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String workOvertimeRegisId = (String)context.get("workOvertimeRegisId");
		String actualStartTime = (String)context.get("actualStartTime");
		String actualEndTime = (String)context.get("actualEndTime");	
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		Time startTime = null;
		Time endTime = null;
		Calendar cal = Calendar.getInstance();
		Calendar tempCal = Calendar.getInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Boolean checkBeforeUpdate = (Boolean)context.get("checkBeforeUpdate"); 
		try {
			GenericValue emplWorkOvertime = delegator.findOne("WorkOvertimeRegistration", UtilMisc.toMap("workOvertimeRegisId", workOvertimeRegisId), false);
			if(emplWorkOvertime == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToUpdate", locale)); 
			}
			Date dateRegistration = emplWorkOvertime.getDate("dateRegistration");
			if(checkBeforeUpdate == null || checkBeforeUpdate){
				String emplId = emplWorkOvertime.getString("partyId");
				List<String> departmentList = PartyUtil.getOrgOfEmployee(delegator, emplId, new Timestamp(dateRegistration.getTime()));
				List<String> orgList = FastList.newInstance();
				for(String orgId: departmentList){
					orgList.addAll(PartyUtil.getAncestorOfParty(delegator, orgId));
				}
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, orgList));
				conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, dateRegistration));
				conds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, dateRegistration));
				List<GenericValue> emplTimesheetList = delegator.findList("EmplTSAndCustomTimePeriodAndPartyGroup", EntityCondition.makeCondition(conds), null, null, null, false);
				for(GenericValue emplTimesheet: emplTimesheetList){
					String statusId = emplTimesheet.getString("statusId"); 
					if(!"EMPL_TS_CREATED".equals(statusId) && !"EMPL_TS_CALC".equals(statusId)){
						String customTimePeriodId = emplTimesheet.getString("customTimePeriodId");
						GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
						Calendar calMsg = Calendar.getInstance();
						calMsg.setTime(customTimePeriod.getDate("fromDate"));
						String fromDate = DateUtil.getDateMonthYearDesc(calMsg);
						calMsg.setTime(customTimePeriod.getDate("thruDate"));
						String thruDate = DateUtil.getDateMonthYearDesc(calMsg);
						calMsg.setTime(dateRegistration);
						String dateRegistrationStr = DateUtil.getDateMonthYearDesc(calMsg);
						GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRPayrollUiLabels", "CannotUpdateWorkOverTime_EmplTimesheetStt", 
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
			
			/*if(startTime != null && endTime != null){
				if(startTime.after(endTime)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "TimeEnterNotValid", locale));
				}
			}*/
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
			if(emplTimesheetId != null){
				//update emplTimekeepingSign in date
				dispatcher.runSync("updateWorkOvertimeInEmplTimesheetAtt", UtilMisc.toMap("partyId", emplWorkOvertime.getString("partyId"), 
						"dateAttendance", dateRegistration, "emplTimesheetId", emplTimesheetId, "userLogin", userLogin));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateWorkOvertimeInEmplTimesheetAtt(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Date dateAttendance = (Date)context.get("dateAttendance");
		String emplTimesheetId = (String)context.get("emplTimesheetId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("dateAttendance", dateAttendance));
		conditions.add(EntityCondition.makeCondition("emplTimesheetId", emplTimesheetId));
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
		//String partyId = userLogin.getString("partyId");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		Date fromDate = new Date(Long.parseLong(fromDateStr));
		Date thruDate = new Date(Long.parseLong(thruDateStr));
		String statusId = (String)context.get("statusId");
		try {
			List<String> emplMgrByParty = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), fromDate, thruDate);
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> importExcelFileTimekeeping(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		//LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String contentType = (String)context.get("_uploadedFile_contentType");
		Locale locale = (Locale)context.get("locale");
		if(!ImportExportExcel.isExcelFile(contentType)){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "OnlyAccpetXLSXFile", locale));
		}
		InputStream is = new ByteArrayInputStream(documentFile.array());
		Integer partyIndex = context.get("partyId") != null? (Integer)context.get("partyId"): 1;
		Integer dateAttendanceIndex = context.get("dateAttendance") != null? (Integer)context.get("dateAttendance") : 2;
		Integer startTimeIndex = context.get("startTime") != null? (Integer)context.get("startTime") : 3;
		Integer endTimeIndex = context.get("endTime") != null? (Integer)context.get("endTime"): 4;
		Integer startLine = context.get("startLine") != null? (Integer)context.get("startLine"): 1;
		String dateTimePattern = (String)context.get("dateTimePattern");
		String overrideDataWay = (String)context.get("overrideDataWay");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		if(overrideDataWay == null){
			overrideDataWay = "onlyDeleteDataCoincide";
		}
		Date fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = new Date(Long.parseLong(fromDateStr));
		}
		if(thruDateStr != null){
			thruDate = new Date(Long.parseLong(thruDateStr));
		}
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, UtilDateTime.nowTimestamp(), UtilDateTime.nowTimestamp());
			List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			Workbook wb = WorkbookFactory.create(is);
			ImportExcelConfig config = new ImportExcelConfig(wb, "EmplAttendanceTracker");
			config.setStartLine(startLine);
			Map<Integer, String> fieldColumExcelMap = FastMap.newInstance();
			fieldColumExcelMap.put(partyIndex - 1, "partyId");
			fieldColumExcelMap.put(dateAttendanceIndex - 1, "dateAttendance");
			fieldColumExcelMap.put(startTimeIndex - 1, "startTime");
			fieldColumExcelMap.put(endTimeIndex - 1, "endTime");
			config.setFieldColumnExcelCorr(fieldColumExcelMap);
			Map<String, List<String>> partyBoundInConfig = FastMap.newInstance();
			partyBoundInConfig.put("partyId", emplListId);
			config.setFieldInListMap(partyBoundInConfig);
			config.setLocale(locale);
			config.setDateTimePattern(dateTimePattern);
			config.setFromDate(fromDate);
			config.setThruDate(thruDate);
			config.setOverrideDataWay(overrideDataWay);
			ImportExportFile importFile = ImportExportFileFactory.getImportExportFile(ImportExportFileFactory.EXCEL_FILE);
			importFile.importDataFromFile(delegator, userLogin, config);			
		} catch (IOException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "ImportExcelSuccess", locale));
	}
	
	/** ================================== Adding in 13/06/2016 ================================ **/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTimekeepingDetail(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-fromDate");
    		listSortFields.add("groupName");
    	}
    	try {
			listIterator = delegator.find("TimekeepingDetailAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	}
	
	public static Map<String, Object> createTimekeepingDetail(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue timekeepingDetail = delegator.makeValue("TimekeepingDetail");
		timekeepingDetail.setNonPKFields(context);
		String timekeepingDetailId = delegator.getNextSeqId("TimekeepingDetail");
		timekeepingDetail.put("timekeepingDetailId", timekeepingDetailId);
		timekeepingDetail.put("createdDate", UtilDateTime.nowTimestamp());
		try {
			delegator.create(timekeepingDetail);
			retMap.put("timekeepingDetailId", timekeepingDetailId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createTimekeepingDetailPartyInPeriod(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyCode = (String)context.get("partyCode");
		String timekeepingDetailId = (String)context.get("timekeepingDetailId");
		try {
			GenericValue timekeepingDetail = delegator.findOne("TimekeepingDetail", UtilMisc.toMap("timekeepingDetailId", timekeepingDetailId), false);
			if(timekeepingDetail == null){
				return ServiceUtil.returnError("cannot find timekeeping detail");
			}
			String timesheetDetailEnumId = timekeepingDetail.getString("timesheetDetailEnumId");
			Timestamp fromDate = timekeepingDetail.getTimestamp("fromDate");
			Timestamp thruDate = timekeepingDetail.getTimestamp("thruDate");
			String partyGroupId = timekeepingDetail.getString("partyId");
			String defaultWorkingShiftId = timekeepingDetail.getString("workingShiftId");
			List<GenericValue> partyList = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", partyCode), null, false);
			if(UtilValidate.isEmpty(partyList)){
				return ServiceUtil.returnError("cannot find party have code: " + partyCode);
			}
			String partyId = partyList.get(0).getString("partyId");
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp thruDateTs = new Timestamp(thruDate.getTime());
			Boolean isBelongOrg = PartyUtil.checkEmployeeInOrg(delegator, partyGroupId, partyId, fromDateTs, thruDateTs, userLogin);
			if(!isBelongOrg){
				return ServiceUtil.returnError("employee have code: " + partyCode + "is not belong organization have id: " + partyGroupId);
			}
			List<GenericValue> workingShiftDayWeekList = delegator.findByAnd("WorkingShiftDayWeek", 
					UtilMisc.toMap("workingShiftId", defaultWorkingShiftId), null, true);
			Map<String, String> workingShiftWorkType = FastMap.newInstance();
			for(GenericValue tempGv: workingShiftDayWeekList){
				workingShiftWorkType.put(tempGv.getString("dayOfWeek"), tempGv.getString("workTypeId"));
			}
			if("TS_ATTENDANCE_DAILY".equals(timesheetDetailEnumId)){
				List<GenericValue> timekeepingDetailPartyList = (List<GenericValue>)context.get("timekeepingDetailPartyList");
				for(GenericValue tempTimekeepingDetailParty: timekeepingDetailPartyList){
					Date dateTimekeeping = tempTimekeepingDetailParty.getDate("dateTimekeeping");
					if(dateTimekeeping != null && dateTimekeeping.compareTo(fromDate) >= 0 && dateTimekeeping.compareTo(thruDate) <= 0){
						GenericValue newEntity = delegator.makeValue("TimekeepingDetailParty");
						newEntity.put("partyId", partyId);
						newEntity.put("timekeepingDetailId", timekeepingDetailId);
						newEntity.put("dateTimekeeping", dateTimekeeping);
						Double workdayActual = tempTimekeepingDetailParty.getDouble("workdayActual");
						Double workdayStandard = 0d;
						Double overtimeHours = 0d;
						String dayOfWeek = DateUtil.getDayName(dateTimekeeping);
						if(workdayActual != null && workdayActual > 0){
							Map<String, Boolean> checkDayLeaveMap = TimekeepingHelper.checkDayIsDayLeaveOfParty(delegator, partyId, defaultWorkingShiftId, dateTimekeeping);
							Boolean isDayLeave = checkDayLeaveMap.get("isDayLeave");
							Boolean isHoliday = checkDayLeaveMap.get("isHoliday");
							GenericValue workingShiftEmpl = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", dateTimekeeping), true);
							String workTypeId = null;
							if(workingShiftEmpl == null || workingShiftEmpl.getString("workingShiftId").equals(defaultWorkingShiftId)){
								workTypeId = workingShiftWorkType.get(dayOfWeek);
							}else{
								String workingShiftId = workingShiftEmpl.getString("workingShiftId");
								GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), true);
								if(workingShiftDayWeek != null){
									workTypeId = workingShiftDayWeek.getString("workTypeId");
								}
							}
							if(!isDayLeave){
								if("FIRST_HALF_SHIFT".equals(workTypeId) || "SECOND_HALF_SHIFT".equals(workTypeId)){
									workdayStandard = 0.5d;
								}else if("ALL_SHIFT".equals(workTypeId)){
									workdayStandard = 1d;
								}
							}
							if(!isDayLeave){
								// ngay di lam binh thuong
								if(workdayActual > workdayStandard){
									//so cong thuc te > so cong chuan => lam them ngay thuong
									//overtimeHours = (workdayActual - workdayStandard) / 1.5 * 8;
									overtimeHours = (workdayActual - workdayStandard) * 8;
									overtimeHours = (Math.round(overtimeHours * 100)/100d);
									workdayActual = workdayStandard;
									newEntity.put("overtimeEnumId", "OT_NORMAL");
								}
							}else{
								//di lam ngay nghi cuoi tuan hoac ngay le
								if(isHoliday != null && isHoliday){
									newEntity.put("overtimeEnumId", "OT_HOLIDAY");
									//overtimeHours = workdayActual / 3 * 8;
									overtimeHours = workdayActual * 8;
								}else{
									newEntity.put("overtimeEnumId", "OT_WEEKEND");
									//overtimeHours = workdayActual / 2 * 8;
									overtimeHours = workdayActual * 8;
								}
								overtimeHours = (Math.round(overtimeHours * 100)/100d);
								workdayActual = 0d;
							}
						}
						newEntity.put("workdayActual", workdayActual);
						newEntity.put("workdayStandard", workdayStandard);
						newEntity.put("overtimeHours", overtimeHours);
						//newEntity.setAllFields(tempTimekeepingDetailParty, false, null, null);
						delegator.createOrStore(newEntity);
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTimekeepingDetailPartyJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	String timekeepingDetailId = (String[])parameters.get("timekeepingDetailId") != null? ((String[])parameters.get("timekeepingDetailId"))[0]: null;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("timekeepingDetailId", timekeepingDetailId));
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("firstName");
		}
		try {
			EntityListIterator timekeepingDetailPartyIt = delegator.find("TimekeepingDetailPtyAndPtyRelGroupBy", null, EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts);
			List<GenericValue> timekeepingDetailPartyList = timekeepingDetailPartyIt.getCompleteList();
			timekeepingDetailPartyIt.close();
			totalRows = timekeepingDetailPartyList.size();
			if(end > totalRows){
				end = totalRows;
			}
			timekeepingDetailPartyList = timekeepingDetailPartyList.subList(start, end);
			//Calendar cal = Calendar.getInstance();
			for(GenericValue timekeepingDetailParty: timekeepingDetailPartyList){
				Map<String, Object> tempMap = timekeepingDetailParty.getAllFields();
				String partyId = timekeepingDetailParty.getString("partyId");
				List<GenericValue> timekeepingDetailPartyDateKeeping = delegator.findByAnd("TimekeepingDetailPartyAndWorkdayPaid", 
						UtilMisc.toMap("partyId", partyId, "timekeepingDetailId", timekeepingDetailId), null, false);
				//Double totalWorkDay = 0d;
				//Double totalWorkDayPaid = 0d;
				for(GenericValue tempGv: timekeepingDetailPartyDateKeeping){
					Date dateTimekeeping = tempGv.getDate("dateTimekeeping");
					//cal.setTime(dateTimekeeping);
					String datafield = String.valueOf(dateTimekeeping.getTime());
					Double tempTotalWorkDayPaid = tempGv.getDouble("totalWorkdayPaid");
					Double overtimeHours = tempGv.getDouble("overtimeHours");
					if(overtimeHours != null){
						Double overtimeWorkday = 0d;
						String overtimeEnumId = tempGv.getString("overtimeEnumId");
						if("OT_NORMAL".equals(overtimeEnumId)){
							//overtimeWorkday = Math.floor(overtimeHours * 1.5) / 8;
							overtimeWorkday = Math.floor(overtimeHours) / 8;
						}else if("OT_WEEKEND".equals(overtimeEnumId)){
							//overtimeWorkday = Math.floor(overtimeHours * 2) / 8;
							overtimeWorkday = Math.floor(overtimeHours) / 8;
						}else if("OT_HOLIDAY".equals(overtimeEnumId)){
							//overtimeWorkday = Math.floor(overtimeHours * 3) / 8;
							overtimeWorkday = Math.floor(overtimeHours) / 8;
						}
						tempTotalWorkDayPaid += overtimeWorkday;
					}
					tempMap.put(datafield, tempTotalWorkDayPaid);
				}
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
	public static Map<String, Object> updateTimekeepingDetailParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Date dateTimekeeping = (Date)context.get("dateTimekeeping");
		String timekeepingDetailId = (String)context.get("timekeepingDetailId");
		Double overtimeHours = (Double)context.get("overtimeHours");
		Double workdayActual = (Double)context.get("workdayActual");
		Double workdayLeavePaid = (Double)context.get("workdayLeavePaid");
		try {
			GenericValue timekeepingDetail = delegator.findOne("TimekeepingDetail", UtilMisc.toMap("timekeepingDetailId", timekeepingDetailId), false);
			if(timekeepingDetail == null){
				return ServiceUtil.returnError("Cannot find timekeeping have code: " + timekeepingDetailId);
			}
			GenericValue workingShiftEmpl = delegator.findOne("WorkingShiftEmployee", UtilMisc.toMap("partyId", partyId, "dateWork", dateTimekeeping), false);
			String workingShiftId = null;
			if(workingShiftEmpl != null){
				workingShiftId = workingShiftEmpl.getString("workingShiftId");
			}else{
				workingShiftId = timekeepingDetail.getString("workingShiftId");
			}
			Map<String, Boolean> checkMapDayLeave = TimekeepingHelper.checkDayIsDayLeaveOfParty(delegator, partyId, workingShiftId, dateTimekeeping);
			Boolean isDayLeave = checkMapDayLeave.get("isDayLeave");
			Boolean isHoliday = checkMapDayLeave.get("isHoliday");
			GenericValue timekeepingDetailParty = delegator.makeValue("TimekeepingDetailParty");
			timekeepingDetailParty.setPKFields(context);
			Double workdayStandard = 0d;
			if(!isDayLeave){
				String dayOfWeek = DateUtil.getDayName(dateTimekeeping);
				GenericValue workingShiftDayWeek = delegator.findOne("WorkingShiftDayWeek", 
						UtilMisc.toMap("workingShiftId", workingShiftId, "dayOfWeek", dayOfWeek), true);
				String workTypeId = workingShiftDayWeek.getString("workTypeId");
				if("FIRST_HALF_SHIFT".equals(workTypeId) || "SECOND_HALF_SHIFT".equals(workTypeId)){
					workdayStandard = 0.5d;
				}else if("ALL_SHIFT".equals(workTypeId)){
					workdayStandard = 1d;
				}
				if(workdayActual != null && workdayActual > workdayStandard){
					workdayActual = workdayStandard;
				}
				if(workdayLeavePaid != null && workdayLeavePaid > workdayStandard){
					workdayLeavePaid = workdayActual != null? workdayStandard - workdayActual: workdayStandard;
				}
				timekeepingDetailParty.put("workdayActual", workdayActual);
				timekeepingDetailParty.put("workdayLeavePaid", workdayLeavePaid);
			}
			timekeepingDetailParty.put("workdayStandard", workdayStandard);
			String overtimeEnumId = null;
			if(overtimeHours != null && overtimeHours > 0){
				if(isHoliday != null && isHoliday){
					overtimeEnumId = "OT_HOLIDAY";
				}else if(isDayLeave){
					overtimeEnumId = "OT_WEEKEND";
				}else{
					overtimeEnumId = "OT_NORMAL";
				}
			}
			timekeepingDetailParty.put("overtimeEnumId", overtimeEnumId);
			timekeepingDetailParty.put("overtimeHours", overtimeHours);
			delegator.createOrStore(timekeepingDetailParty);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateTimekeepingDataRelated(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplLeaveUpdate = (String)context.get("emplLeaveUpdate");
		String holidayUpdate = (String)context.get("holidayUpdate");
		if(!"Y".equals(holidayUpdate) && !"Y".equals(emplLeaveUpdate)){
			return ServiceUtil.returnSuccess();
		}
		Timestamp fromDateTs = (Timestamp)context.get("fromDate");
		Timestamp thruDateTs = (Timestamp)context.get("thruDate");
		String timekeepingDetailId = (String)context.get("timekeepingDetailId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue timekeepingDetail = delegator.findOne("TimekeepingDetail", UtilMisc.toMap("timekeepingDetailId", timekeepingDetailId), false);
			if(timekeepingDetail == null){
				return ServiceUtil.returnError("cannot find timekeeping detail");
			}
			String workingShiftId = (String)timekeepingDetail.get("workingShiftId");
			Map<String, Object> resultService = null;
			Timestamp timekeepingFromDate = timekeepingDetail.getTimestamp("fromDate");
			Timestamp timekeepingThruDate = timekeepingDetail.getTimestamp("thruDate");
			if(fromDateTs.compareTo(timekeepingFromDate) < 0){
				fromDateTs = timekeepingFromDate;
			}
			if(thruDateTs.compareTo(timekeepingThruDate) > 0){
				thruDateTs = timekeepingThruDate;
			}
			Date fromDate = new Date(fromDateTs.getTime());
			Date thruDate = new Date(thruDateTs.getTime());
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("timekeepingDetailId", timekeepingDetailId));
			conds.add(EntityCondition.makeCondition("dateTimekeeping", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition("dateTimekeeping", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			List<GenericValue> timekeepingDetailPartyList = delegator.findList("TimekeepingDetailParty", EntityCondition.makeCondition(conds), null, null, null, false);
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("userLogin", userLogin);
			for(GenericValue timekeepingDetailParty: timekeepingDetailPartyList){
				String partyId = timekeepingDetailParty.getString("partyId");
				Date dateTimekeeping = timekeepingDetailParty.getDate("dateTimekeeping");
				Double totalWorkday = timekeepingDetailParty.getDouble("totalWorkday");//cong thuc te cua nhan vien trong ngay
				if(totalWorkday == null || totalWorkday <= 0){
					//nhan vien khong di lam => check xem nhan vien la nghi co phep hay nghi khong luong
					Double tempWorkdayLeavePaid = TimekeepingHelper.getTotalWorkdayWhenEmplLeave(delegator, partyId, dateTimekeeping);
					if(tempWorkdayLeavePaid != null && tempWorkdayLeavePaid > 0){
						timekeepingDetailParty.set("workdayLeavePaid", tempWorkdayLeavePaid);
					}
				}else{
					//nhan vien co di lam, kiem tra xem ngay di lam co phai la ngay nghi hoac ngay` le, tet khong
					ctxMap.put("partyId", partyId);
					ctxMap.put("workingShiftId", workingShiftId);
					ctxMap.put("dateCheck", new Timestamp(dateTimekeeping.getTime()));
					resultService = dispatcher.runSync("checkDayIsDayLeaveOfParty", ctxMap);
					if(ServiceUtil.isSuccess(resultService)){
						Boolean isDayLeave = (Boolean)resultService.get("isDayLeave");
						Boolean isHoliday = (Boolean)resultService.get("isHoliday");
						//Double tempTotalWorkdayPaid = null;
						if((isHoliday != null && isHoliday) || isDayLeave){
							timekeepingDetailParty.set("overtimeHours", totalWorkday * 8);
						}
					}
				}
				timekeepingDetailParty.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTimekeepingSummaryJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-fromDate");
    		listSortFields.add("groupName");
    	}
    	try {
			listIterator = delegator.find("TimekeepingSummaryAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	}
	public static Map<String, Object> createTimekeepingSummary(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue timekeepingSummary = delegator.makeValue("TimekeepingSummary");
		timekeepingSummary.setNonPKFields(context);
		String timekeepingSummaryId = delegator.getNextSeqId("TimekeepingSummary");
		timekeepingSummary.put("timekeepingSummaryId", timekeepingSummaryId);
		try {
			delegator.create(timekeepingSummary);
			retMap.put("timekeepingSummaryId", timekeepingSummaryId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createTimekeepingSummaryDetail(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue timekeepingSummaryDetail = delegator.makeValue("TimekeepingSummaryDetail");
		timekeepingSummaryDetail.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(timekeepingSummaryDetail);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateTimekeepingSummaryPartyFromTimekeepingDetail(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String timekeepingSummaryId = (String)context.get("timekeepingSummaryId");
		try {
			GenericValue timekeepingSummary = delegator.findOne("TimekeepingSummary", UtilMisc.toMap("timekeepingSummaryId", timekeepingSummaryId), false);
			if(timekeepingSummary == null){
				return ServiceUtil.returnError("cannot find time keeping summary");
			}
			List<GenericValue> timekeepingSummaryDetailList = delegator.findByAnd("TimekeepingSummaryDetailAndDetail", UtilMisc.toMap("timekeepingSummaryId", timekeepingSummaryId), 
					UtilMisc.toList("createdDate"), false);
			Timestamp fromDateTs = timekeepingSummary.getTimestamp("fromDate");
			Timestamp thruDateTs = timekeepingSummary.getTimestamp("thruDate");
			List<EntityCondition> conds = FastList.newInstance();
			Date fromDate = new Date(fromDateTs.getTime());
			Date thruDate = new Date(thruDateTs.getTime());
			Long month = timekeepingSummary.getLong("month");
			Long year = timekeepingSummary.getLong("year");
			List<Date> listDateHolidayInMonth = TimekeepingHelper.getListHolidayInMonth(delegator, month.intValue(), year.intValue());
			for(GenericValue tempGv: timekeepingSummaryDetailList){
				String defaultWorkingShiftId = tempGv.getString("workingShiftId");
				List<GenericValue> workingShiftDayWeekList = delegator.findByAnd("WorkingShiftDayWeek", UtilMisc.toMap("workingShiftId", defaultWorkingShiftId), null, true);
				Map<String, String> workingShiftWorkType = FastMap.newInstance();
				for(GenericValue workingShiftDayWeek: workingShiftDayWeekList){
					workingShiftWorkType.put(workingShiftDayWeek.getString("dayOfWeek"), workingShiftDayWeek.getString("workTypeId"));
				}
				String timekeepingDetailId = tempGv.getString("timekeepingDetailId");
				conds.add(EntityCondition.makeCondition("timekeepingDetailId", timekeepingDetailId));
				conds.add(EntityCondition.makeCondition("dateTimekeeping", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
				conds.add(EntityCondition.makeCondition("dateTimekeeping", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
				List<GenericValue> listTimekeepingDetailParty = delegator.findList("TimekeepingDetailParty", EntityCondition.makeCondition(conds), null, null, null, false);
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(listTimekeepingDetailParty, "partyId", true);
				Map<String, Object> resultService = null;
				for(String partyId: partyIdList){
					List<GenericValue> listTimekeepingDetailOfParty = EntityUtil.filterByCondition(listTimekeepingDetailParty, EntityCondition.makeCondition("partyId", partyId));
					resultService = TimekeepingHelper.createTimekeepingSummaryParty(delegator, dispatcher, userLogin, timekeepingSummaryId, month, year, listTimekeepingDetailOfParty, 
							partyId, workingShiftWorkType, defaultWorkingShiftId, listDateHolidayInMonth, locale, timeZone);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
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
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "RefreshDataSuccess", locale));
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTimekeepingSummaryParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("firstName");
    	}
    	String timekeepingSummaryId = parameters.get("timekeepingSummaryId") != null? parameters.get("timekeepingSummaryId")[0] : null;
    	listAllConditions.add(EntityCondition.makeCondition("timekeepingSummaryId", timekeepingSummaryId));
    	try {
			listIterator = delegator.find("TimekeepingSummaryPtyAndPtyRelGroupBy", null, EntityCondition.makeCondition(listAllConditions),  null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	}
	public static Map<String, Object> updateTimekeepingSummaryParty(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String timekeepingSummaryId = (String)context.get("timekeepingSummaryId");
        String workdayActualStr = (String)context.get("workdayActual");
        String workdayLeavePaidStr = (String)context.get("workdayLeavePaid");
        String workdayStandardStr = (String)context.get("workdayStandard");
        String overtimeHoursNormalStr = (String)context.get("overtimeHoursNormal");
        String overtimeHoursWeekendStr = (String)context.get("overtimeHoursWeekend");
        String overtimeHoursHolidayStr = (String)context.get("overtimeHoursHoliday");
        String totalMinuteLateStr = (String)context.get("totalMinuteLate");
        String totalWorkLateStr = (String)context.get("totalWorkLate");
		try {
			GenericValue timekeepingSummaryParty = delegator.findOne("TimekeepingSummaryParty",
					UtilMisc.toMap("timekeepingSummaryId", timekeepingSummaryId, "partyId", partyId), false);
			if(timekeepingSummaryParty == null){
				return ServiceUtil.returnError("cannot find timekeeping summary party to update");
			}
            if(UtilValidate.isNotEmpty(workdayActualStr)){
                timekeepingSummaryParty.set("workdayActual", Double.parseDouble(workdayActualStr));
            }
            if(UtilValidate.isNotEmpty(workdayLeavePaidStr)){
                timekeepingSummaryParty.set("workdayLeavePaid", Double.parseDouble(workdayLeavePaidStr));
            }
            if(UtilValidate.isNotEmpty(workdayStandardStr)){
                timekeepingSummaryParty.set("workdayStandard", Double.parseDouble(workdayStandardStr));
            }
            if(UtilValidate.isNotEmpty(overtimeHoursNormalStr)){
                timekeepingSummaryParty.set("overtimeHoursNormal", Double.parseDouble(overtimeHoursNormalStr));
            }
            if(UtilValidate.isNotEmpty(overtimeHoursWeekendStr)){
                timekeepingSummaryParty.set("overtimeHoursWeekend", Double.parseDouble(overtimeHoursWeekendStr));
            }
            if(UtilValidate.isNotEmpty(overtimeHoursHolidayStr)){
                timekeepingSummaryParty.set("overtimeHoursHoliday", Double.parseDouble(overtimeHoursHolidayStr));
            }
            if(UtilValidate.isNotEmpty(totalMinuteLateStr)){
                double totalMinuteLateD = Double.parseDouble(totalMinuteLateStr);
                timekeepingSummaryParty.set("totalMinuteLate", (long) totalMinuteLateD);
            }
            if(UtilValidate.isNotEmpty(totalWorkLateStr)){
                double totalWorkLateD = Double.parseDouble(totalWorkLateStr);
                timekeepingSummaryParty.set("totalWorkLate", (long)totalWorkLateD);
            }
			timekeepingSummaryParty.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	/** ========================================= ./end ======================================= **/
}
