package com.olbius.basepos.history;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class PosHistoryServices {
	public static final String module = PosHistoryServices.class.getName();
    
	public static Map<String, Object> createPosHistoryOrderRecord(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Object orderId = context.get("orderId");
    	Object returnId = context.get("returnId");
    	Object partyId = context.get("partyId");
    	Object createdBy = context.get("createdBy");
    	Object orderDate = context.get("orderDate");
    	Object grandTotal = context.get("grandTotal");
    	Object posTerminalLogId = context.get("posTerminalLogId");
    	try {
    		// OrderHeader
			GenericValue posHistory = delegator.makeValue("PosHistory");
			posHistory.set("orderId", orderId);
			posHistory.set("returnId", returnId);
			posHistory.set("createdBy", createdBy);
			posHistory.set("orderDate", orderDate);
			posHistory.set("grandTotal", grandTotal);
			if(returnId != null){
				posHistory.set("transactionType", "ORDER_RETURN");
			}else{
				posHistory.set("transactionType", "ORDER");
			}
			// PosTerminalLog
			posHistory.set("posTerminalLogId", posTerminalLogId);
			posHistory.set("statusIdPTL", "POSTX_SOLD");
			// Person
			posHistory.set("partyId", partyId);
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			posHistory.set("firstName", person.get("firstName"));
			posHistory.set("middleName", person.get("middleName"));
			posHistory.set("lastName", person.get("lastName"));
			posHistory.create();
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling createPosHistoryOrderRecord service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	return ServiceUtil.returnSuccess();
    }
	
	public static Map<String, Object> createPosHistoryReturnRecord(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String returnId = (String) context.get("returnId");
		String orderId = (String) context.get("orderId");
		String returnCreatedBy = (String) context.get("returnCreatedBy");
		Object returnDate = context.get("returnDate");
		Object returnGrandTotal = context.get("returnGrandTotal");
		Object posTerminalLogId = context.get("posTerminalLogId");
		Object partyId = context.get("partyId");
		try {
			// OrderHeader
			GenericValue posHistory = delegator.makeValue("PosHistory");
			posHistory.set("returnId", returnId);
			posHistory.set("orderId", orderId);
			posHistory.set("returnCreatedBy", returnCreatedBy);
			posHistory.set("returnDate", returnDate);
			posHistory.set("returnGrandTotal", returnGrandTotal);
			posHistory.set("transactionType", "RETURN");
			// PosTerminalLog
			posHistory.set("posTerminalLogId", posTerminalLogId);
			posHistory.set("statusIdPTL", "POSTX_RETURNED");
			// Person
			posHistory.set("partyId", partyId);
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			posHistory.set("firstName", person.get("firstName"));
			posHistory.set("middleName", person.get("middleName"));
			posHistory.set("lastName", person.get("lastName"));
			posHistory.create();
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling createPosHistoryReturnRecord service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updatePosHistoryPersonInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String partyId = (String) context.get("partyId");
		try {
			EntityCondition cond1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			List<GenericValue> tmpList = delegator.findList("PosHistory", cond1, null, null, null, false);
			if(!tmpList.isEmpty()){
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				for (GenericValue posHistory : tmpList) {
					posHistory.set("firstName", person.get("firstName"));
					posHistory.set("middleName", person.get("middleName"));
					posHistory.set("lastName", person.get("lastName"));
					posHistory.store();
				}
			}
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling updatePosHistoryPersonInfo service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		return ServiceUtil.returnSuccess();
	}
}