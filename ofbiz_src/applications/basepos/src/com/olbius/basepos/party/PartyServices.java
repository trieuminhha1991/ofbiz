package com.olbius.basepos.party;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

public class PartyServices {
	public static String module = PartyServices.class.getName();
	public static Map<String, Object> jqListCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	List<EntityCondition> conditions = FastList.newInstance();
    	conditions.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
        conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
        conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
        conditions.add(EntityCondition.makeCondition(mapCondition));
        conditions.add(EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND));
        if(UtilValidate.isEmpty(listSortFields)){
        	listSortFields.add("partyId DESC");
        }
    	try {
    		listIterator = delegator.find("PartyNameAndBillingShippingAddressAndMobileAndEmailAndRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqListCustomer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static Map<String, Object> jqListSupplier(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	List<EntityCondition> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition(mapCondition));
        conditions.add(EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND));
    	try {
    		listIterator = delegator.find("PartyAndSupplierRelationshipInformation", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqListSupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	public static Map<String, Object> jqListEmployee(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityCondition typeCondition = EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "POS_ROLETYPE");
		listAllConditions.add(typeCondition);
		if(UtilValidate.isEmpty(listSortFields)){
        	listSortFields.add("partyId DESC");
        }
		try {
			listIterator = delegator.find("PartyNameAndPrimaryAddressAndMobileAndEmailAndUserLoginAndRoleAndFacility", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListEmployee service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
