package com.olbius.accounting.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class OrderJQServices {
	public static final String module = OrderJQServices.class.getName();
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListOrders(DispatchContext ctx, Map<String, ? extends Object> context) {
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
    		listIterator = delegator.find("OrderHeader", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListOrders service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    /** Method to get the list party_Id where party role type is RoleTypeId List Variable .  */
    public static List<String> getListOrderWithOrganizationParty(Delegator delegator, String organizationPartyId, String RoleTypeId, String OrderTypeId) throws GenericEntityException {
        List<String> listOrderIds = FastList.newInstance();
		EntityCondition tmpConditon = 
                EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, organizationPartyId),
                        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, RoleTypeId),
                        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, OrderTypeId)
                ), EntityJoinOperator.AND);			 			
        List<GenericValue> listPartyIdWithRoleTypesList = delegator.findList("OrderHeaderAndRoles", tmpConditon, null, null, null, true); 
        for (GenericValue listPartyIdWithRoleType : listPartyIdWithRoleTypesList) {
        	listOrderIds.add(listPartyIdWithRoleType.getString("orderId"));
        }
        return listOrderIds;
    }		
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListSaleOrders (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	String organizationParty = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", delegator);
    	
    	if (organizationParty != null && !"".equals(organizationParty)) {
    		listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, getListOrderWithOrganizationParty(delegator,organizationParty,"BILL_FROM_VENDOR","SALES_ORDER")));
    		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
    	}
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("OrderHeaderAndRoles", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListOrders service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqListPurchaseOrders (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	String organizationParty = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", delegator);
    	if (organizationParty != null && !"".equals(organizationParty)) {
    		listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, getListOrderWithOrganizationParty(delegator,organizationParty,"BILL_TO_CUSTOMER","PURCHASE_ORDER")));
    		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR"));
    	}
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("OrderHeaderAndRolesDetail", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListOrders service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
