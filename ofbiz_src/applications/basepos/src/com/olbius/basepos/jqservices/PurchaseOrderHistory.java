package com.olbius.basepos.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class PurchaseOrderHistory {
	public static final String module = PurchaseOrderHistory.class.getName();
			
	public static Map<String, Object> jqGetPurchaseOrderHistory(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	String[] facilityIds = parameters.get("facilityId");
    	String[] statusIds = parameters.get("statusId");
    	String facilityId = null;
    	String statusId = null;

    	if(UtilValidate.isNotEmpty(facilityIds)){
    		facilityId = facilityIds[0];
    	}
    	if(UtilValidate.isNotEmpty(facilityId)){
    		mapCondition.put("originFacilityId", facilityId);
    	}
    	if(UtilValidate.isNotEmpty(statusIds)){
    		statusId = statusIds[0];
    	}
    	if(UtilValidate.isNotEmpty(statusId)){
    		mapCondition.put("statusId", statusId);
    	}
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = FastList.newInstance();
    		listSortFields.add("orderDate DESC");
    	}
    	EntityCondition delCondition = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER");
    	EntityCondition poCondition = EntityCondition.makeCondition("transactionId", EntityOperator.EQUALS, null);
    	EntityCondition poConditionPlan = EntityCondition.makeCondition("planPOId", EntityOperator.NOT_EQUAL, null);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	EntityCondition listAllConditionsSearch = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.OR);
    	EntityCondition listCondSearch = EntityCondition.makeCondition(EntityJoinOperator.AND, listAllConditionsSearch, delCondition, tmpConditon, poCondition, poConditionPlan);
    	
    	try {
    		listIterator = delegator.find("OrderHeaderAndParty", listCondSearch, null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListPurchaseOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> jqGetPurchaseOrderHistoryDetail(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	EntityCondition delCondition = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.get("orderId")[0]);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	EntityCondition listAllConditionsSearch = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.OR);
    	EntityCondition listCondSearch = EntityCondition.makeCondition(EntityJoinOperator.AND, listAllConditionsSearch, delCondition, tmpConditon);
    	
    	try {
    		listIterator = delegator.find("OrderItem", listCondSearch, null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetPurchaseOrderHistoryDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
}
