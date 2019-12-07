package com.olbius.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.recruitment.helper.RoleTyle;

public class CommonServices implements RoleTyle {
	
	public static final String module = CommonServices.class.getName(); 
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListPeople(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("Person", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling getListPeople service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListEmailTemplates(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters =(Map<String, String[]>) context.get("parameters");
		String workEffortTypeId = parameters.get("workEffortTypeId")[0];
		Map<String, String> mapCondition = new HashMap<String, String>();
		if(workEffortTypeId.equals("ROUND_INTERVIEW")) {
			EntityCondition emailTemplateSettingIdCon = EntityCondition.makeCondition("emailTemplateSettingId", EntityJoinOperator.IN, UtilMisc.toSet("FIRST_INT_INVIT", "SECOND_INT_INVIT", "INTERVIEW_NOTI"));
			listAllConditions.add(emailTemplateSettingIdCon);
		}
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("EmailTemplateSetting", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling getListEmailTemplates service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListJobRequest(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("statusId", "JR_ACCEPTED");
		mapCondition.put("actorRoleTypeId", CEO_ROLE);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("JobRequest", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling getListJobRequest service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListPartyGroups(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("PartyGroup", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling getListPartyGroups service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListEmplPositionTypes(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("DeptPositionTypeDetail", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling getListEmplPositionTypes service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListSaleEmplPositionTypes(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters =(Map<String, String[]>) context.get("parameters");
		EntityCondition emplPosCon = EntityCondition.makeCondition("deptId", parameters.get("partyId")[0]);
		listAllConditions.add(emplPosCon);
		try {
			EntityCondition condition = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("DeptPositionTypeDetail", condition, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling getListEmplPositionTypes service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> getGeoAssoc(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String geoId = (String)context.get("geoId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listGeoAssoc", listReturn);
		
		try {
			List<GenericValue> geoListAssoc = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoId", geoId, "geoAssocTypeId", "REGIONS"), null, false);
			for(GenericValue tempGv: geoListAssoc){
				Map<String, Object> tempMap = FastMap.newInstance();
				String geoToId = tempGv.getString("geoIdTo");
				GenericValue geoTo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoToId), false);
				tempMap.put("id", geoToId);
				tempMap.put("label", geoTo.getString("geoName"));
				tempMap.put("value", geoToId);
				//check if geoToId have children
				List<GenericValue> geoListAssocChild = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoId", geoToId, "geoAssocTypeId", "REGIONS"), null, false);
				if(UtilValidate.isNotEmpty(geoListAssocChild)){
					Map<String, Object> childs = FastMap.newInstance();
					childs.put("label", "Loading...");
					childs.put("value", "getGeoAssoc");
					List<Map<String, Object>> listChilds = FastList.newInstance();
					listChilds.add(childs);
					tempMap.put("items", listChilds);
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, "success");
		return retMap;
	}
	
	public static Map<String, Object> createCustomTimePeriodInsurance(DispatchContext dctx, Map<String, Object> context){
		Timestamp startTimestamp = (Timestamp)context.get("startTimestamp");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		if(startTimestamp == null){
			startTimestamp = UtilDateTime.nowTimestamp();
		}
		startTimestamp = UtilDateTime.getYearStart(startTimestamp);
		Timestamp endTimestamp = UtilDateTime.getYearEnd(startTimestamp, timeZone, locale);
		Date startDate = new Date(startTimestamp.getTime());
		Date endDate = new Date(endTimestamp.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("userLogin", system);
			ctxMap.put("periodTypeId", "YEARLY");
			ctxMap.put("fromDate", startDate);
			ctxMap.put("thruDate", endDate);
			ctxMap.put("isClosed", "N");
			ctxMap.put("periodName", "Năm " + cal.get(Calendar.YEAR));
			ctxMap.put("organizationPartyId", MultiOrganizationUtil.getCurrentOrganization(delegator));
			Map<String, Object> resultService = dispatcher.runSync("createCustomTimePeriod", ctxMap);
			String parentCustomTimePeriodId = (String)resultService.get("customTimePeriodId");
			Timestamp tmpTimestamp = startTimestamp;
			ctxMap.put("periodTypeId", "MONTHLY");
			ctxMap.put("parentPeriodId", parentCustomTimePeriodId);
			while (tmpTimestamp.before(endTimestamp)) {
				Date tempStartDate = new Date(tmpTimestamp.getTime());
				Timestamp tmpEndTimestamp = UtilDateTime.getMonthEnd(tmpTimestamp, timeZone, locale);
				cal.setTime(tempStartDate);
				Date tmpEndDate = new Date(tmpEndTimestamp.getTime());
				ctxMap.put("fromDate", tempStartDate);
				ctxMap.put("thruDate", tmpEndDate);
				ctxMap.put("isClosed", "N");
				ctxMap.put("periodName", "Tháng " + (cal.get(Calendar.MONTH) + 1));
				dispatcher.runSync("createCustomTimePeriod", ctxMap);
				tmpTimestamp = UtilDateTime.getMonthStart(tmpTimestamp, 0, 1);
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}

	public static int convertSolarYearToLunarYear(Integer year, Integer currYear) {
		int startYearCycle = ((currYear - 3)/60) * 60 + 3;
		return year - startYearCycle;
	}
}
