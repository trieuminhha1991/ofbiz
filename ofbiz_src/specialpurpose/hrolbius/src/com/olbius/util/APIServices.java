package com.olbius.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class APIServices {
	
	//Const
	public static final String MODULE = APIServices.class.getName();
	public static final String RESOURCE_NOTI = "NotificationUiLabels";
	
	public static Map<String, Object> getPartiesByRootAndRole (DispatchContext dispCtx, Map<String, ?extends Object> context){
		//Parameters
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		Locale locale = (Locale)context.get("locale");
		
		Delegator delegator = dispCtx.getDelegator();
		List<String> partyList = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();
		try {
			partyList = PartyUtil.getPartyByRootAndRole(delegator, partyId, roleTypeId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					RESOURCE_NOTI, "findError",
					new Object[] { e.getMessage() }, locale));
		}
		
		//Return
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
				RESOURCE_NOTI, "findSuccessfully", locale));
		result.put("parties", partyList);
		return result;
	}
	
	public static Map<String, Object> getPeopleByRootAndRole (DispatchContext dispCtx, Map<String, ?extends Object> context){
		//Parameters
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		Locale locale = (Locale)context.get("locale");
		
		Delegator delegator = dispCtx.getDelegator();
		List<String> partyList = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();
		try {
			partyList = PartyUtil.getPeopleByRootAndRole(delegator, partyId, roleTypeId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(
					RESOURCE_NOTI, "findError",
					new Object[] { e.getMessage() }, locale));
		}
		
		//Return
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
				RESOURCE_NOTI, "findSuccessfully", locale));
		result.put("parties", partyList);
		return result;
	}
	public static Map<String, Object> getPartiesByRootAndRoles (DispatchContext dispCtx, Map<String, ?extends Object> context){
		//Parameters
		String partyId = (String)context.get("partyId");
		@SuppressWarnings("unchecked")
		List<String> roleTypeIds = (List<String>)context.get("roleTypeIds");
		Locale locale = (Locale)context.get("locale");
		
		Delegator delegator = dispCtx.getDelegator();
		List<String> partyList = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();
		try {
			partyList = PartyUtil.getPartyByRootAndRoles(delegator, partyId, roleTypeIds);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE_NOTI, "findError", new Object[] { e.getMessage() }, locale));
		}
		
		//Return
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
				RESOURCE_NOTI, "findSuccessfully", locale));
		result.put("parties", partyList);
		return result;
	}
	
	public static Map<String, Object> getAllRolePartyInOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		//conditions.add(EntityCondition.makeCondition("PartyRelationshipTypeId", "EMPLOYMENT"));
		conditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
		
		List<EntityCondition> conditions2 = FastList.newInstance();
		conditions2.add(EntityUtil.getFilterByDateExpr());
		//conditions2.add(EntityCondition.makeCondition("PartyRelationshipTypeId", "EMPLOYMENT"));
		conditions2.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		List<Map<String, Object>> retList = FastList.newInstance();
		Map<String, Object> retMap = FastMap.newInstance();
		Set<String> roleTypeSet = FastSet.newInstance();
		try {
			List<GenericValue> partyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<GenericValue> partyRelList2 = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions2), null, null, null, false);
			for(GenericValue tempGv: partyRelList){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyGroupId", tempGv.getString("partyIdFrom"));
				tempMap.put("partyGroupRoleTypeId", tempGv.getString("roleTypeIdFrom"));
				tempMap.put("emplRoleTypeId", tempGv.getString("roleTypeIdTo"));
				roleTypeSet.add(tempGv.getString("roleTypeIdTo"));
				retList.add(tempMap);
			}
			for(GenericValue tempGv: partyRelList2){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("partyGroupId", tempGv.getString("partyIdTo"));
				tempMap.put("partyGroupRoleTypeId", tempGv.getString("roleTypeIdTo"));
				tempMap.put("emplRoleTypeId", tempGv.getString("roleTypeIdFrom"));
				roleTypeSet.add(tempGv.getString("roleTypeIdFrom"));
				retList.add(tempMap);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> list = FastList.newInstance();
		list.addAll(roleTypeSet);
		retMap.put("allRoleTypeEmplInRelationship", retList);
		retMap.put("allRoleEmplList", list);
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListPartyIdToPartyRel(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyIdFrom = (String)context.get("partyIdFrom");
		String roleTypeIdFrom = (String)context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String)context.get("roleTypeIdTo");
		String partyRelationshipTypeId = (String)context.get("partyRelationshipTypeId");
		List<String> orderBy = (List<String>)context.get("orderBy");
		Map<String,Object> retMap = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		if(partyRelationshipTypeId != null){
			conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
		}
		try {
			List<String> listPartyIdTo = FastList.newInstance();
			retMap.put("listPartyIdTo", listPartyIdTo);
			List<GenericValue> partyRel = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, orderBy, null, false);
			if(UtilValidate.isNotEmpty(partyRel)){
				listPartyIdTo = EntityUtil.getFieldListFromEntityList(partyRel, "partyIdTo", true);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retMap;
	}
	
	public static Map<String,Object> getListMgrPartyGroup(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyIdFrom = (String)context.get("partyIdFrom");
		String roleTypeIdFrom = (String)context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String)context.get("roleTypeIdTo");
		Map<String,Object> retMap = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		Set<String> mgrSet = FastSet.newInstance();
		List<String> retList = FastList.newInstance();
		retMap.put("listMgrId", retList);
		try {
			List<GenericValue> partyRel = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyRel)){
				for(GenericValue tempGv: partyRel){
					String partGroupId = tempGv.getString("partyIdTo");
					String managerOfPartyGroup = PartyUtil.getManagerbyOrg(partGroupId, delegator);
					if(managerOfPartyGroup != null){
						mgrSet.add(managerOfPartyGroup);
					}
				}
			}
			retList.addAll(mgrSet);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retMap;
	}
}
