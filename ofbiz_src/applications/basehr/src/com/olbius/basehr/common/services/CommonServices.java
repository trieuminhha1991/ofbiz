package com.olbius.basehr.common.services;

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

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;
import com.olbius.common.util.EntityMiscUtil;

public class CommonServices {
	
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
		if(workEffortTypeId.equals("ROUND_INTERVIEW")) {
			EntityCondition emailTemplateSettingIdCon = EntityCondition.makeCondition("emailTemplateSettingId", EntityJoinOperator.IN, UtilMisc.toSet("FIRST_INT_INVIT", "SECOND_INT_INVIT", "INTERVIEW_NOTI"));
			listAllConditions.add(emailTemplateSettingIdCon);
		}
		Map<String, String> mapCondition = new HashMap<String, String>();
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
		mapCondition.put("actorRoleTypeId", PropertiesUtil.DIRECTOR_ROLE);
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
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, "success");
		return retMap;
	}
	
	public static Map<String, Object> getSubsidiaryOfParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String partyIdFrom = (String)context.get("partyIdFrom");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		try {
			List<GenericValue> listSub = PartyUtil.getListSubsidiaryOfParty(delegator, partyIdFrom, customTimePeriodId);
			for(GenericValue tempGv: listSub){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyIdTo = tempGv.getString("partyIdTo");
				tempMap.put("id", partyIdTo);
				tempMap.put("label", tempGv.getString("groupName"));
				tempMap.put("value", partyIdTo);
				//check if geoToId have children
				List<GenericValue> listSubChild = PartyUtil.getListSubsidiaryOfParty(delegator, partyIdTo, customTimePeriodId);
				if(UtilValidate.isNotEmpty(listSubChild)){
					Map<String, Object> childs = FastMap.newInstance();
					childs.put("label", "Loading...");
					childs.put("value", "getSubsidiaryOfParty");
					List<Map<String, Object>> listChilds = FastList.newInstance();
					listChilds.add(childs);
					tempMap.put("items", listChilds);
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
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
			ctxMap.put("organizationPartyId", PartyUtil.getCurrentOrganization(delegator, null));
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
	
	public static Map<String, Object> getAssociatedStateList(DispatchContext dctx, Map<String, Object> context){
		List<Map<String, String>> listReturn = FastList.newInstance();
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put("listReturn", listReturn);
		String countryGeoId = (String)context.get("countryGeoId");
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> listState = CommonWorkers.getAssociatedStateList(delegator, countryGeoId);
		for(GenericValue state: listState){
			Map<String, String> tempMap = FastMap.newInstance();
			tempMap.put("geoId", state.getString("geoId"));
			tempMap.put("geoName", state.getString("geoName"));
			listReturn.add(tempMap);
		}
		return retMap;
	}
	
	public static Map<String, Object> getAssociatedCountyList(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, String>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		Delegator delegator = dctx.getDelegator();
		String stateGeoId = (String)context.get("stateGeoId");
		try {
			List<GenericValue> listGeo = delegator.findByAnd("GeoAssocAndGeoTo", UtilMisc.toMap("geoIdFrom", stateGeoId, 
																								"geoAssocTypeId", "REGIONS", 
																								"geoTypeId", "DISTRICT"), null, false);
			for(GenericValue county: listGeo){
				Map<String, String> tempMap = FastMap.newInstance();
				tempMap.put("geoId", county.getString("geoId"));
				tempMap.put("geoName", county.getString("geoName"));
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> getAssociatedWardList(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, String>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		Delegator delegator = dctx.getDelegator();
		String districtGeoId = (String)context.get("districtGeoId");
		try {
			List<GenericValue> listGeo = delegator.findByAnd("GeoAssocAndGeoTo", UtilMisc.toMap("geoIdFrom", districtGeoId, 
																								"geoAssocTypeId", "REGIONS", 
																								"geoTypeId", "WARD"), null, false);
			for(GenericValue county: listGeo){
				Map<String, String> tempMap = FastMap.newInstance();
				tempMap.put("geoId", county.getString("geoId"));
				tempMap.put("geoName", county.getString("geoName"));
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}

	public static int convertSolarYearToLunarYear(Integer year, Integer currYear) {
		int startYearCycle = ((currYear - 3)/60) * 60 + 3;
		return year - startYearCycle;
	}
	
	public static Map<String, Object> getPartyName(DispatchContext ctx, Map<String, ? extends Object> context) {
		String partyId = (String)context.get("partyId");
		String partyName = null;
		Delegator delegator = ctx.getDelegator();
		try {
			partyName = PartyUtil.getPartyName(delegator, partyId);
		} catch(GenericEntityException e){
			ServiceUtil.returnError(e.toString());
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("partyName", partyName);
		return result;
	}

	public static Map<String, Object> createPartyContactMech(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String partyId,
			String infoString, String contactMechPurposeTypeId, String contactMechTypeId) throws GenericEntityException, GenericServiceException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("contactMechTypeId", contactMechTypeId));
		conditions.add(EntityCondition.makeCondition("infoString", infoString));
		List<GenericValue> partyAndContactMechs = delegator.findList("PartyAndContactMech", EntityCondition.makeCondition(conditions), null, null, null, false);
		if(UtilValidate.isEmpty(partyAndContactMechs)){
			Map<String, Object> context = FastMap.newInstance();
			context.put("partyId", partyId);
			context.put("contactMechTypeId", contactMechTypeId);
			context.put("infoString", infoString);
			context.put("userLogin", userLogin);
			context.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			return dispatcher.runSync("createPartyContactMech", context);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListHRMAdminAuthorization(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		successResult.put("TotalRows", String.valueOf(totalRows));
		List<Map<String, Object>> listReturn = FastList.newInstance();
		successResult.put("listIterator", listReturn);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			String currOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> securityGroupPermission = delegator.findByAnd("SecurityGroupPermission", UtilMisc.toMap("permissionId", "HR_RECRUITMENT_ADMIN"), null, false);
			if(UtilValidate.isEmpty(securityGroupPermission)){
				return successResult;
			}
			List<String> groupId = EntityUtil.getFieldListFromEntityList(securityGroupPermission, "groupId", true);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("groupId", EntityJoinOperator.IN, groupId));
			if(currOrgId != null){
				conds.add(EntityCondition.makeCondition("organizationId", currOrgId));
			}
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> userLoginSecurityGroupList = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isEmpty(userLoginSecurityGroupList)){
				return successResult;
			}
			List<String> userLoginIdList = EntityUtil.getFieldListFromEntityList(userLoginSecurityGroupList, "userLoginId", true);
			List<GenericValue> userLoginList = delegator.findList("UserLogin", EntityCondition.makeCondition("userLoginId", EntityJoinOperator.IN, userLoginIdList), null, null, null, false);
			List<String> partyIdList = EntityUtil.getFieldListFromEntityList(userLoginList, "partyId", true);
			if(UtilValidate.isEmpty(partyIdList)){
				return successResult;
			}
			List<GenericValue> partyList = delegator.findList("Person", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIdList), null, UtilMisc.toList("firstName"), null, false);
			totalRows = partyIdList.size();
			if(end > totalRows){
				end = totalRows;
			}
			partyList = partyList.subList(start, end);
			for(GenericValue tempGv: partyList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				List<GenericValue> emplPositionTypeList = PartyUtil.getPositionTypeOfEmplAtTime(delegator, partyId, UtilDateTime.nowTimestamp());
				List<String> descriptionList = EntityUtil.getFieldListFromEntityList(emplPositionTypeList, "description", true);
				GenericValue party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
				tempMap.put("emplPositionType", StringUtils.join(descriptionList, ", "));
				tempMap.put("partyId", partyId);
				tempMap.put("partyCode", party.getString("partyCode"));
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult; 
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListStateProvinceGeo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		listAllCondition.add(EntityCondition.makeCondition("geoTypeId", "PROVINCE"));
		try {
			List<GenericValue> listReturn = delegator.findList("GeoAssocAndGeoTo", EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("geoId", "geoName"), UtilMisc.toList("geoName"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return successResult; 
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEthnicOriginList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			EntityCondition condition = null;
			if(listAllCondition != null){
				condition =  EntityCondition.makeCondition(listAllCondition);
			}
			listReturn = delegator.findList("EthnicOrigin", condition, UtilMisc.toSet("ethnicOriginId", "description"), UtilMisc.toList("description"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getReligionList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			EntityCondition condition = null;
			if(listAllCondition != null){
				condition =  EntityCondition.makeCondition(listAllCondition);
			}
			listReturn = delegator.findList("Religion", condition, UtilMisc.toSet("religionId", "description"), UtilMisc.toList("description"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNationalityList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			EntityCondition condition = null;
			if(listAllCondition != null){
				condition =  EntityCondition.makeCondition(listAllCondition);
			}
			listReturn = delegator.findList("Nationality", condition, UtilMisc.toSet("nationalityId", "description"), UtilMisc.toList("description"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMajorList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			listReturn = delegator.findList("Major", EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("majorId", "description"), UtilMisc.toList("description"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDegreeClassificationTypeList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			listReturn = delegator.findList("DegreeClassificationType", EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("classificationTypeId", "description"), UtilMisc.toList(""), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEducationSystemTypeList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			listReturn = delegator.findList("EducationSystemType", EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("educationSystemTypeId", "description"), UtilMisc.toList("description"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCountryGeoList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			listAllCondition.add(EntityCondition.makeCondition("geoTypeId", "COUNTRY"));
			listReturn = delegator.findList("Geo", EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("geoId", "geoName"), UtilMisc.toList("geoName"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentSourceTypeList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			listReturn = delegator.findList("RecruitmentSourceType", EntityCondition.makeCondition(listAllCondition), UtilMisc.toSet("recruitSourceTypeId", "recruitSourceName"), 
					UtilMisc.toList("recruitSourceName"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRecruitmentChannelTypeList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			listReturn = delegator.findList("RecruitmentChannelType", EntityCondition.makeCondition(listAllCondition), 
					UtilMisc.toSet("recruitChannelTypeId", "recruitChannelName"), UtilMisc.toList("recruitChannelName"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMaritalStatusList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllCondition = (List<EntityCondition>)context.get("listAllCondition");
		List<GenericValue> listReturn;
		try {
			listAllCondition.add(EntityCondition.makeCondition("statusTypeId", "MARITAL_STATUS"));
			listReturn = delegator.findList("StatusItem", EntityCondition.makeCondition(listAllCondition), 
					UtilMisc.toSet("statusId", "description"), UtilMisc.toList("description"), null, false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createNewSkillType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		try {
			String skillTypeId = (String)context.get("skillTypeId");
			if(skillTypeId != null){
				GenericValue skillType = delegator.findOne("SkillType", UtilMisc.toMap("skillTypeId", skillTypeId), false);
				if(skillType != null){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "SkillTypeIsExists", UtilMisc.toMap("skillTypeId", skillTypeId), locale));
				}
			}
			Map<String, Object> resultService = dispatcher.runSync("createSkillType", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
	}
	public static Map<String, Object> createUom(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String description = (String)context.get("description");
		String abbreviation = (String)context.get("abbreviation");
		String uomTypeId = (String)context.get("uomTypeId");
		if(abbreviation != null){
			abbreviation = abbreviation.trim();
		}
		if(description != null){
			description = description.trim();
		}
		EntityCondition cond1 = EntityCondition.makeCondition("description", description);
		EntityCondition cond2 = EntityCondition.makeCondition("abbreviation", abbreviation);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition(cond1, EntityJoinOperator.OR, cond2));
		conds.add(EntityCondition.makeCondition("uomTypeId", uomTypeId));
		try {
			List<GenericValue> kpiExists = delegator.findList("Uom", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(kpiExists)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "UomIsExisted", UtilMisc.toMap("description", description), locale));
			}
			GenericValue uom = delegator.makeValue("Uom");
			uom.setNonPKFields(context);
			String uomId = delegator.getNextSeqId("Uom");
			uom.set("uomId", uomId);
			delegator.create(uom);
			retMap.put("uomId", uomId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPartyGroups(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listOrders = FastList.newInstance();
		try {
			listOrders = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PartyGroupAndParty", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetPartyGroups service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listOrders);
		return successResult;
	}
}
