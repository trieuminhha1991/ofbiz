package com.olbius.basehr.kpiperfreview.services;

import java.util.List;
import java.util.Map;

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
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.PartyUtil;


public class KeyPerfIndicatorServices {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListKeyPerfIndicatorJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("keyPerfIndicatorName");
    		}
    		listIterator = delegator.find("KeyPerfIndicatorAndDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListKeyPerfIndPartyTargetJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-fromDate");
				listSortFields.add("groupName");
			}
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.NOT_EQUAL, "PERSON"));
			if(!PartyUtil.isFullPermissionView(delegator, userLogin.getString("userLoginId"))){
				List<String> listParty = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"), UtilDateTime.nowTimestamp(), null);
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listParty));
			}
			listIterator = delegator.find("KeyPerfIndPartyTargetAndParty", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListKeyPerfIndPartyTargetItemJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyTargetId = parameters.get("partyTargetId") != null? parameters.get("partyTargetId")[0] : null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("keyPerfIndicatorName");
			}
			listAllConditions.add(EntityCondition.makeCondition("partyTargetId", partyTargetId));
			listIterator = delegator.find("KeyPerfIndPartyTargetItemAndKPI", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getKeyPerfIndPartyTargetOfOrgChildJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String parentPartyTargetId = parameters.get("parentPartyTargetId") != null? parameters.get("parentPartyTargetId")[0] : null;
		String keyPerfIndicatorId = parameters.get("keyPerfIndicatorId") != null? parameters.get("keyPerfIndicatorId")[0] : null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-target");
			}
			listAllConditions.add(EntityCondition.makeCondition("parentPartyTargetId", parentPartyTargetId));
			listAllConditions.add(EntityCondition.makeCondition("keyPerfIndicatorId", keyPerfIndicatorId));
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.NOT_EQUAL, "PERSON"));
			listIterator = delegator.find("KeyPerfIndPartyTargetAndItemAndPty", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getKeyPerfIndPartyTargetOfEmplJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String parentPartyTargetId = parameters.get("parentPartyTargetId") != null? parameters.get("parentPartyTargetId")[0] : null;
		String keyPerfIndicatorId = parameters.get("keyPerfIndicatorId") != null? parameters.get("keyPerfIndicatorId")[0] : null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-target");
			}
			listAllConditions.add(EntityCondition.makeCondition("parentPartyTargetId", parentPartyTargetId));
			listAllConditions.add(EntityCondition.makeCondition("keyPerfIndicatorId", keyPerfIndicatorId));
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", "PERSON"));
			listIterator = delegator.find("KeyPerfIndPartyTargetAndItemAndPty", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getListKeyPerfIndPartyTargetItem(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyTargetId = (String)context.get("partyTargetId");
		Delegator delegator = dctx.getDelegator();
		try {
			List<GenericValue> listReturn = delegator.findByAnd("KeyPerfIndPartyTargetItemAndKPI", UtilMisc.toMap("partyTargetId", partyTargetId), UtilMisc.toList("keyPerfIndicatorName"), false);
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createKeyPerfIndicator(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue keyPerfIndicator = delegator.makeValue("KeyPerfIndicator");
		keyPerfIndicator.setNonPKFields(context);
		String keyPerfIndicatorId = delegator.getNextSeqId("KeyPerfIndicator");
		keyPerfIndicator.set("keyPerfIndicatorId", keyPerfIndicatorId);
		try {
			delegator.create(keyPerfIndicator);
			retMap.put("keyPerfIndicatorId", keyPerfIndicatorId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createKeyPerfIndPartyAppl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue keyPerfIndPartyAppl = delegator.makeValue("KeyPerfIndPartyAppl");
		keyPerfIndPartyAppl.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(keyPerfIndPartyAppl);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createKeyPerfIndPositionTypeAppl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue keyPerfIndPositionTypeAppl = delegator.makeValue("KeyPerfIndPositionTypeAppl");
		keyPerfIndPositionTypeAppl.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(keyPerfIndPositionTypeAppl);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createKeyPerfIndPartyTarget(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue keyPerfIndPartyTarget = delegator.makeValue("KeyPerfIndPartyTarget");
		keyPerfIndPartyTarget.setNonPKFields(context);
		String partyTargetId = delegator.getNextSeqId("KeyPerfIndPartyTarget");
		keyPerfIndPartyTarget.set("partyTargetId", partyTargetId);
		try {
			delegator.create(keyPerfIndPartyTarget);
			retMap.put("partyTargetId", partyTargetId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createKeyPerfIndPartyTargetItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue keyPerfIndPartyTargetItem = delegator.makeValue("KeyPerfIndPartyTargetItem");
		keyPerfIndPartyTargetItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(keyPerfIndPartyTargetItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> getKeyPerfIndPartyTargetItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyTargetId = (String)context.get("partyTargetId");
		String keyPerfIndicatorId = (String)context.get("keyPerfIndicatorId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			GenericValue keyPerfIndPartyTargetItem = delegator.findOne("KeyPerfIndPartyTargetItem", UtilMisc.toMap("partyTargetId", partyTargetId, "keyPerfIndicatorId", keyPerfIndicatorId), false);
			if(keyPerfIndPartyTargetItem != null){
				retMap.putAll(keyPerfIndPartyTargetItem.getFields(UtilMisc.toList("target", "weight", "uomId")));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
}
