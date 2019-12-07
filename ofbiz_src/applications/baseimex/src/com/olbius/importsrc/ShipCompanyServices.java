package com.olbius.importsrc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;


public class ShipCompanyServices {
	public static final String MODULE = ShipCompanyServices.class.getName();
	public static Map<String, Object> addShipCompany(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue partyGroup = delegator.makeValue("PartyGroup");
		GenericValue party = delegator.makeValue("Party");
		GenericValue partyRole = delegator.makeValue("PartyRole");
		String companyName = (String) context.get("companyName");
		String companyId = (String) context.get("companyId");
		String description = (String) context.get("description");
		String partySeqId = delegator.getNextSeqId("Party");
		
		party.set("partyId", partySeqId);
		party.set("partyTypeId", "PARTY_GROUP");
		party.set("partyCode", companyId);
		party.set("statusId", "PARTY_ENABLED");
		party.set("description", description);
		partyRole.set("partyId", partySeqId);
		partyRole.set("roleTypeId", "SHIPPING_LINE");
		partyGroup.set("partyId", partySeqId);
		partyGroup.set("groupName", companyName);
		
		try {
			if (UtilValidate.isNotEmpty(delegator.findList("Party",  EntityCondition.makeCondition("partyCode", companyId), null, null, null, Boolean.FALSE)))
				return ServiceUtil.returnError("COMPANYID_DUPLICATED");
			delegator.create(party);
			delegator.create(partyGroup);
			delegator.create(partyRole);
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
			return ServiceUtil.returnError(e1.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateShipCompany(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue partyGroup ;
		GenericValue party ;
		String companyName = (String) context.get("companyName");
		String companyEdittingId = (String) context.get("companyEdittingId");
		String companyId = (String) context.get("companyId");
		String description = (String) context.get("description");
		String partyId;
		try {
			// check if companyId duplicate, if duplicate return error "companyId duplicate"
			if ( !companyId.equals(companyEdittingId) ) {
				if (UtilValidate.isNotEmpty(delegator.findList("Party",  EntityCondition.makeCondition("partyCode", companyId), null, null, null, Boolean.FALSE)))
					return ServiceUtil.returnError("COMPANYID_DUPLICATED");
			}
			party = EntityUtil.getFirst(delegator.findList("Party",  EntityCondition.makeCondition("partyCode", companyEdittingId), null, null, null, Boolean.FALSE));
			partyId = (String) party.get("partyId");
			partyGroup = EntityUtil.getFirst(delegator.findList("PartyGroup",  EntityCondition.makeCondition("partyId", partyId), null, null, null, Boolean.FALSE));
			party.set("partyCode", companyId);
			party.set("description", description);
			partyGroup.set("groupName", companyName);
			
        	delegator.store(party);
			delegator.store(partyGroup);	
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> removeShipCompany(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String companyId = (String) context.get("companyId");
		GenericValue party;
        try {
        	party = EntityUtil.getFirst(delegator.findList("Party",  EntityCondition.makeCondition("partyCode", companyId), null, null, null, Boolean.FALSE));
			party.set("statusId", "PARTY_DISABLED");
        	delegator.store(party);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> listShipCompany(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	listAllConditions.add( EntityCondition.makeCondition("roleTypeId", "SHIPPING_LINE"));
    	listAllConditions.add( EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,  null));
    	listAllConditions.add( EntityCondition.makeCondition("partyTypeId",  "PARTY_GROUP"));
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {    		
    		listIterator = delegator.find("PartyRoleAndPartyDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
		
	}
	
}
