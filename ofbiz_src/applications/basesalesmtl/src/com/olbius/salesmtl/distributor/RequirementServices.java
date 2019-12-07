package com.olbius.salesmtl.distributor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.NotificationWorker;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RequirementServices {
	public static final String module = RequirementServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";
	
	public static Map<String, Object> distributorReceiveReturn(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			Object destinationFacilityId = context.get("destinationFacilityId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue party = userLogin.getRelatedOne("Party", false);
			
			//	create ReturnHeader
			result = dispatcher.runSync("createReturnHeader",
					UtilMisc.toMap("returnHeaderTypeId", "CUSTOMER_RETURN", "statusId", "RETURN_REQUESTED", "createdBy", userLogin.get("userLoginId"),
							"fromPartyId", "_NA_", "toPartyId", party.get("partyId"), "entryDate", new Timestamp(System.currentTimeMillis()),
							"destinationFacilityId", destinationFacilityId, "needsInventoryReceive", "Y", "currencyUomId", party.get("preferredCurrencyUomId"),
							"userLogin", userLogin));
			Object returnId = result.get("returnId");
			JSONArray items = JSONArray.fromObject(context.get("items"));
			for (Object o : items) {
				JSONObject item = JSONObject.fromObject(o);
				BigDecimal returnPrice = null;
				if (UtilValidate.isNotEmpty(item.get("returnPrice"))){
					returnPrice = new BigDecimal( String.valueOf(item.get("returnPrice")));
				}
				String returnReasonId = "";
				if (UtilValidate.isNotEmpty(item.get("returnReasonId"))){
					returnReasonId = String.valueOf(item.get("returnReasonId"));
				}
				result = dispatcher.runSync("createReturnItemWithoutOrder",
						UtilMisc.toMap("returnId", returnId, "returnReasonId", returnReasonId, "returnTypeId", "RTN_REFUND", "returnItemTypeId",
								"RET_FPROD_ITEM", "productId", item.get("productId"), "description", item.get("description"), "statusId", "RETURN_REQUESTED",
								"returnPrice", returnPrice,
								"expectedItemStatus", "INV_RETURNED", "returnQuantity", new BigDecimal(item.getInt(("returnQuantity"))), "quantityUomId", item.get("quantityUomId"),
								"userLogin", userLogin));
				Object returnItemSeqId = result.get("returnItemSeqId");
				//	createInventoryItem
				GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				result = dispatcher.runSync("createInventoryItem",
						UtilMisc.toMap("inventoryItemTypeId", "NON_SERIAL_INV_ITEM", "productId", item.get("productId"), "ownerPartyId",
								party.get("partyId"), "datetimeReceived", new Timestamp(System.currentTimeMillis()), "facilityId", destinationFacilityId,
								"returnPrice", returnPrice,
								"currencyUomId", party.get("preferredCurrencyUomId"), "isReturned", "Y", "userLogin", system));
				Object inventoryItemId = result.get("inventoryItemId");
				//	createInventoryItemDetail
				result = dispatcher.runSync("createInventoryItemDetail",
						UtilMisc.toMap("inventoryItemId", inventoryItemId,
								"quantityOnHandDiff", new BigDecimal(item.getInt(("returnQuantity"))), "availableToPromiseDiff",
								new BigDecimal(item.getInt(("returnQuantity"))), "returnId", returnId, "returnItemSeqId", returnItemSeqId,
								"unitCost", returnPrice,
								"userLogin", userLogin));
				if (ServiceUtil.isSuccess(result)) {
					delegator.storeByCondition("ReturnItem", UtilMisc.toMap("statusId", "RETURN_COMPLETED"),
							EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId)));
				} else {
					throw new Exception();
				}
			}
			delegator.storeByCondition("ReturnHeader", UtilMisc.toMap("statusId", "RETURN_COMPLETED"),
					EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)));
			result.clear();
			result.put("returnId", (String) returnId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> createReturnItemWithoutOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue returnItem = delegator.makeValidValue("ReturnItem", context);
			if (UtilValidate.isEmpty(returnItem.get("returnItemSeqId"))) {
				delegator.setNextSubSeqId(returnItem, "returnItemSeqId", 5, 1);
			}
			returnItem.create();
			result.put("returnItemSeqId", returnItem.get("returnItemSeqId"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> createRequirementItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue requirementItem = delegator.makeValidValue("RequirementItem", context);
			if (UtilValidate.isEmpty(requirementItem.get("reqItemSeqId"))) {
				delegator.setNextSubSeqId(requirementItem, "reqItemSeqId", 5, 1);
			}
			requirementItem.create();
			result.put("reqItemSeqId", requirementItem.get("reqItemSeqId"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> createReturnRequirementCommitment(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue returnRequirementCommitment = delegator.makeValidValue("ReturnRequirementCommitment", context);
			returnRequirementCommitment.create();
			result.put("returnId", returnRequirementCommitment.get("returnId"));
			result.put("returnItemSeqId", returnRequirementCommitment.get("returnItemSeqId"));
			result.put("requirementId", returnRequirementCommitment.get("requirementId"));
			result.put("reqItemSeqId", returnRequirementCommitment.get("reqItemSeqId"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> updateReturnRequirementCommitment(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue returnRequirementCommitment = delegator.makeValidValue("ReturnRequirementCommitment", context);
			returnRequirementCommitment.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> createRequirementItemAssoc(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue requirementItemAssoc = delegator.makeValidValue("RequirementItemAssoc", context);
			requirementItemAssoc.create();
			result.put("requirementId", requirementItemAssoc.get("requirementId"));
			result.put("reqItemSeqId", requirementItemAssoc.get("reqItemSeqId"));
			result.put("toRequirementId", requirementItemAssoc.get("toRequirementId"));
			result.put("toReqItemSeqId", requirementItemAssoc.get("toReqItemSeqId"));
			result.put("reqItemAssocTypeId", requirementItemAssoc.get("reqItemAssocTypeId"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> updateRequirementItemAssoc(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue requirementItemAssoc = delegator.makeValidValue("RequirementItemAssoc", context);
			requirementItemAssoc.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> createNotifyBorrowRequirement(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String requirementId = (String) context.get("requirementId");
    	try {
    		GenericValue requirement = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
    		if (requirement == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundRequirementHasIdIs", UtilMisc.toList(requirementId), locale));
        	
    		NotificationWorker.sendNotifiWhenCreateRequirement(delegator, dispatcher, locale, requirement, userLogin);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling sendNotifyWhenCreateOrder service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	return successResult;
    }
}
