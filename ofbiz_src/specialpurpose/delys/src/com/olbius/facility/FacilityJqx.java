package com.olbius.facility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.pseudotag.EntityField;

import com.olbius.services.DelysServices;

public class FacilityJqx {
	public static final String module = DelysServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listFacilityJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	Security security = ctx.getSecurity();
    	String strEntity = "FacilityAll";
    	// security check
        if (!security.hasPermission("LOGISTICS_ADMIN", userLogin)) {
        	if (!security.hasPermission("LOGISTICS_VIEW", userLogin)) {
        		if (!security.hasPermission("FACILITY_ADMIN", userLogin)) {
        			if (!security.hasPermission("FACILITY_VIEW", userLogin)) {
        				if (security.hasPermission("FACILITY_ROLE_VIEW", userLogin)) {
        					strEntity = "FacilityFullPartyFacility";
            	    		listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.get("partyId")));
            	    		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
            	    		tmpListCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER"));
            	    		tmpListCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "LOG_STOREKEEPER"));
            	    		listAllConditions.add(EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.OR));
            	    		// FIXME check fromDate and thruDate, may be done in View
        				} else {
            				return null; // has no permission.
            			}
        			}
        		}
        	}
        }
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition); 
    	listAllConditions.add(tmpConditon);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityTypeId = (String)parameters.get("facilityTypeId")[0];
    	if (facilityTypeId != null && !"".equals(facilityTypeId)){
    		Map<String, String> mapFacTypeCondition = new HashMap<String, String>();
    		mapFacTypeCondition.put("facilityTypeId", facilityTypeId);
    		EntityCondition tmpFacTypeConditon = EntityCondition.makeCondition(mapFacTypeCondition);
        	listAllConditions.add(tmpFacTypeConditon);
    	}
    	try {
    		listIterator = delegator.find(strEntity, EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listFacilityJqx service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	public static Map<String, Object> createFacilityJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue facility = delegator.makeValue("Facility");
		if(!"".equals((String)context.get("facilityId"))){
			facility.set("facilityId", context.get("facilityId"));
		}else{
			facility.set("facilityId", delegator.getNextSeqId("Facility"));
		}
		facility.set("facilityName", context.get("facilityName")); 
		facility.set("facilityTypeId", context.get("facilityTypeId"));
		if(!"".equals((String)context.get("ownerPartyId"))){
			facility.set("ownerPartyId", context.get("ownerPartyId"));
		}
		facility.set("primaryFacilityGroupId", context.get("primaryFacilityGroupId"));
		facility.set("facilitySize", context.get("facilitySize"));
		facility.set("facilitySizeUomId", context.get("facilitySizeUomId"));
		String strFacilityId = null;
		try{
			strFacilityId = facility.getString("facilityId");
			// check exist
			Map<String, String> tmpMap = new HashMap<String, String>();
			tmpMap.put("facilityId", strFacilityId);
			GenericValue tmpGV = delegator.findOne("Facility", tmpMap, false);
			if(tmpGV != null){
				Locale locale = (Locale) context.get("locale");
				successResult = ServiceUtil.returnError(UtilProperties.getMessage("DelysLogisticsUiLabels", "facilityExisted", locale));
				return successResult;
			}
			// create Facility
			facility.create();
			
			if(!"".equals((String)context.get("managerPartyId"))){
				// create Manager relationShip
				GenericValue facilityParty = delegator.makeValue("FacilityParty");
				facilityParty.set("facilityId", strFacilityId);
				facilityParty.set("roleTypeId", "MANAGER");
				facilityParty.set("fromDate", context.get("fromDateManager"));
				facilityParty.set("thruDate", context.get("thruDateManager"));
				facilityParty.set("partyId", context.get("managerPartyId"));
				facilityParty.create();
			}
		}catch (Exception e) {
			String errMsg = "Fatal error calling createFacilityJqx service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("facilityId", strFacilityId);
		return successResult;
	}
	public static Map<String, Object> facilityOwnerableList(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String strCurrentOrganization = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator);
		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
		String strKeySearch = (String)context.get("searchKey");
		tmpListCond.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strCurrentOrganization));
		List<EntityCondition> tmpRoleCond = new ArrayList<EntityCondition>();
		tmpRoleCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "DELYS_DISTRIBUTOR"));
		tmpRoleCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "RENT_FACILITY"));
		tmpListCond.add(EntityCondition.makeCondition(tmpRoleCond,EntityJoinOperator.OR));
		if(!"".equals(strKeySearch)){
			List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
		}
		List<GenericValue> listData;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			listData = delegator.findList("PartyRelationShipWithPartyGroup", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
			GenericValue company = delegator.findOne("PartyGroup", false, UtilMisc.toMap("partyId", strCurrentOrganization));
			listData.add(company);
			successResult.put("listParties", listData);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling facilityOwnerableList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
	}
	public static Map<String, Object> facilityManagerableList(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String strCurrentOrganization = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator);
		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
		String strKeySearch = (String)context.get("searchKey");
		tmpListCond.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strCurrentOrganization));
		tmpListCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE")); // FIXME update role to Logistics department role
		if(!"".equals(strKeySearch)){
			List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstMiddle"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstLast"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleLast"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
		}
		List<GenericValue> listData;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			listData = delegator.findList("PartyRelationShipWithPerson", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
			// FIXME check thruDate
			successResult.put("listParties", listData);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling facilityManagerableList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	public static Map<String, Object> facilityPartyDetailList(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listData;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String, String[]> tmpParams = (Map<String, String[]>)context.get("parameters");
		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, ((String[])tmpParams.get("facilityId"))[0]));
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true); 
		try {
			listData = delegator.find("FacilityPartyWithDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			successResult.put("listIterator", listData);
		} catch (Exception e) {
			String errMsg = "Fatal error calling facilityPartyDetailList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	public static Map<String, Object> createFacilityPartyDetail(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String strPartyId = (String)context.get("partyId");
		String strFacilityId = (String)context.get("facilityId");
		String strRoleTypeId = (String)context.get("roleTypeId");
		Timestamp stFromDate = (Timestamp)context.get("fromDate");
		Timestamp stThruDate = (Timestamp)context.get("thruDate");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			tmpMap.put("facilityId", strFacilityId);
			tmpMap.put("partyId", strPartyId);
			tmpMap.put("roleTypeId", strRoleTypeId);
			tmpMap.put("fromDate", stFromDate);
			//tmpMap.put("thruDate", null);
			GenericValue tmpGV = delegator.findOne("FacilityParty", tmpMap, false);
			if(tmpGV != null){
				successResult.put("responseMessage", "error");
				successResult.put("errorMessage", "Bản ghi đã tồn tại");
				return successResult;
			}
			// check PartyRole
			tmpMap = new HashMap<String, Object>();
			tmpMap.put("partyId", strPartyId);
			tmpMap.put("roleTypeId", strRoleTypeId);
			tmpGV = delegator.findOne("PartyRole", tmpMap, false);
			if(tmpGV == null){
				GenericValue tmpPartyRole = delegator.makeValue("PartyRole");
				tmpPartyRole.put("partyId", strPartyId);
				tmpPartyRole.put("roleTypeId", strRoleTypeId);
				tmpPartyRole.create();
			}
			GenericValue tmpPartyRole = delegator.makeValue("FacilityParty");
			tmpPartyRole.put("partyId", strPartyId);
			tmpPartyRole.put("roleTypeId", strRoleTypeId);
			tmpPartyRole.put("fromDate", stFromDate);
			tmpPartyRole.put("thruDate", stThruDate);
			tmpPartyRole.put("facilityId", strFacilityId);
			// FIXME check partyId for valid role.
			tmpPartyRole.create();
		} catch (Exception e) {
			String errMsg = "Fatal error calling createFacilityPartyDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	public static Map<String, Object> updateFacilityPartyDetail(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String strPartyId = (String)context.get("partyId");
		Map<String, String[]> tmpParams = (Map<String, String[]>)context.get("parameters");
		String strFacilityId = tmpParams.get("facilityId")[0];
		String strRoleTypeId = (String)context.get("roleTypeId");
		Timestamp stFromDate = (Timestamp)context.get("fromDate");
		Timestamp stThruDate = (Timestamp)context.get("thruDate");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			GenericValue tmpPartyRole = delegator.makeValue("FacilityParty");
			tmpPartyRole.put("partyId", strPartyId);
			tmpPartyRole.put("roleTypeId", strRoleTypeId);
			tmpPartyRole.put("fromDate", stFromDate);
			tmpPartyRole.put("thruDate", stThruDate);
			tmpPartyRole.put("facilityId", strFacilityId);
			tmpPartyRole.store();
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateFacilityPartyDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
}
