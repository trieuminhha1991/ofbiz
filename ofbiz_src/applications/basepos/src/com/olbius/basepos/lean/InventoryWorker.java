package com.olbius.basepos.lean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

import javolution.util.FastList;

public class InventoryWorker {
	private AccountingWorker accountingWorker;
	private DataWorker dataWorker;
	
	public InventoryWorker(DataWorker dataWorker, AccountingWorker accountingWorker){
		this.dataWorker = dataWorker;
		this.accountingWorker = accountingWorker;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void immediatelyFulfilledOrder() throws Exception{
		Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", dataWorker.cart.getOrderId());
        fields.put("statusId", "ITEM_COMPLETED");
        List<GenericValue> orderItems = dataWorker.dispatcher.getDelegator().findByAnd("OrderItem", fields, null, true);
        GenericValue productStore = dataWorker.dispatcher.getDelegator().findOne("ProductStore", UtilMisc.toMap("productStoreId", dataWorker.posSession.getProductStoreId()), true);
        List<String> orderList = new ArrayList<String>();
        if(productStore.getString("reserveOrderEnumId").equals("INVRO_FIFO_EXP")){
    		orderList.add("expireDate");
    	}else if(productStore.getString("reserveOrderEnumId").equals("INVRO_LIFO_EXP")){
    		orderList.add("-expireDate");
    	}else if(productStore.getString("reserveOrderEnumId").equals("INVRO_LIFO_REC")){
    		orderList.add("-datetimeReceived");
    	}else{
    		orderList.add("datetimeReceived");
    	}
        for (GenericValue orderItem : orderItems) {
			// 1. Find list of inventoryItem to Fulfill 
        	List<EntityExpr> exprs = FastList.newInstance();
            exprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, orderItem.get("productId")));
            exprs.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, dataWorker.cart.getFacilityId()));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INV_DEBT_SUPPLIER"));
            exprs.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, new BigDecimal(0)));
        	List<GenericValue> invList = dataWorker.dispatcher.getDelegator().findList("InventoryItem", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderList, null, true);
//        	orderList.clear();
        	// 2. Create issuance
        	BigDecimal quantityNotIssued = orderItem.getBigDecimal("quantity");
        	BigDecimal amountNotIssued = orderItem.getBigDecimal("selectedAmount").multiply(quantityNotIssued); // quantity may be greater then zero
        	BigDecimal deductAmount = null;
        	if(amountNotIssued != null && amountNotIssued.signum() > 0){
        		for (GenericValue inventoryItem : invList) {
        			if(inventoryItem.getBigDecimal("amountOnHandTotal").compareTo(amountNotIssued) > 0){
        				deductAmount = amountNotIssued;
        			}else{
        				deductAmount = inventoryItem.getBigDecimal("amountOnHandTotal");
        			}
        			amountNotIssued = amountNotIssued.subtract(deductAmount);
        			// 2.1 Create issuance
	        		Map<String, Object> context = new HashMap();
	        		// FIXME missing ship_group_seq_id
	        		GenericValue itemIssuance = dataWorker.delegator.makeValue("ItemIssuance");
	                itemIssuance.set("itemIssuanceId", dataWorker.delegator.getNextSeqId("ItemIssuance"));
	                itemIssuance.set("orderId", dataWorker.cart.getOrderId());
	                itemIssuance.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
	                itemIssuance.set("quantity", BigDecimal.ONE);
	                itemIssuance.set("issuedDateTime", UtilDateTime.nowTimestamp());
	                itemIssuance.set("inventoryItemId", inventoryItem.getString("inventoryItemId"));
	                itemIssuance.set("weight", deductAmount);
	                itemIssuance.create();
	                context.put("userLogin", dataWorker.posSession.getUserLogin());
	                context.put("itemIssuanceId", itemIssuance.get("itemIssuanceId"));
	                //dispatcher.runSync("createAcctgTransForSalesShipmentIssuance", context); 
	                accountingWorker.createAcctgTransForSalesShipmentIssuance(itemIssuance, inventoryItem);
	                // 2.2 Create InventoryItemDetail
	                GenericValue invDetail = dataWorker.delegator.makeValue("InventoryItemDetail");
	                invDetail.set("inventoryItemId", inventoryItem.getString("inventoryItemId"));
	                invDetail.set("inventoryItemDetailSeqId", dataWorker.delegator.getNextSeqId("InventoryItemDetail"));
	                invDetail.set("orderId", dataWorker.cart.getOrderId());
	                invDetail.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
	                invDetail.set("itemIssuanceId", itemIssuance.get("itemIssuanceId"));
	                invDetail.set("availableToPromiseDiff", BigDecimal.ONE.negate());
	                invDetail.set("quantityOnHandDiff", BigDecimal.ONE.negate());
	                invDetail.set("effectiveDate", itemIssuance.get("issuedDateTime"));
	                invDetail.set("amountOnHandDiff", deductAmount.negate());
	                invDetail.create();
	        		// 2.3 check to exit the loop
	        		if(amountNotIssued.compareTo(new BigDecimal(0)) == 0){
	        			break;
	        		}
        		}
        	}else{
	        	for (GenericValue inventoryItem : invList) {
	        		if(inventoryItem.getBigDecimal("availableToPromiseTotal").compareTo(quantityNotIssued) > 0){
	        			deductAmount = quantityNotIssued;
	        		}else{
	        			deductAmount = inventoryItem.getBigDecimal("availableToPromiseTotal");
	        		}
	        		quantityNotIssued = quantityNotIssued.subtract(deductAmount);
	        		// 2.1 Create issuance
	        		Map<String, Object> context = new HashMap();
	                // FIXME missing ship_group_seq_id
	                GenericValue itemIssuance = dataWorker.delegator.makeValue("ItemIssuance");
	                itemIssuance.set("itemIssuanceId", dataWorker.delegator.getNextSeqId("ItemIssuance"));
	                itemIssuance.set("orderId", dataWorker.cart.getOrderId());
	                itemIssuance.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
	                itemIssuance.set("quantity", deductAmount);
	                itemIssuance.set("issuedDateTime", UtilDateTime.nowTimestamp());
	                itemIssuance.set("inventoryItemId", inventoryItem.getString("inventoryItemId"));
	                itemIssuance.create();
	                context = new HashMap();
	                context.put("userLogin", dataWorker.posSession.getUserLogin());
	                context.put("itemIssuanceId", itemIssuance.get("itemIssuanceId"));
	                //dispatcher.runSync("createAcctgTransForSalesShipmentIssuance", context); 
	                accountingWorker.createAcctgTransForSalesShipmentIssuance(itemIssuance, inventoryItem);
	        		// 2.2 Create InventoryItemDetail
	                GenericValue invDetail = dataWorker.delegator.makeValue("InventoryItemDetail");
	                invDetail.set("inventoryItemId", inventoryItem.getString("inventoryItemId"));
	                invDetail.set("inventoryItemDetailSeqId", dataWorker.delegator.getNextSeqId("InventoryItemDetail"));
	                invDetail.set("orderId", dataWorker.cart.getOrderId());
	                invDetail.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
	                invDetail.set("itemIssuanceId", itemIssuance.get("itemIssuanceId"));
	                invDetail.set("availableToPromiseDiff", deductAmount.negate());
	                invDetail.set("quantityOnHandDiff", deductAmount.negate());
	                invDetail.set("effectiveDate", itemIssuance.get("issuedDateTime"));
	                invDetail.create();
	        		// 2.3 check to exit the loop
	        		if(quantityNotIssued.compareTo(new BigDecimal(0)) == 0){
	        			break;
	        		}
	        	}
        	}
        	// FIXME if there is one inventoryItem does not exist in system, but it can be picked by employee, so it can cause error here. 
        	// Prevent when adding this product to cart
		}
        
	}
}
