package com.olbius.basepos.lean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepos.order.PosOrderChangeHelper;

import javolution.util.FastMap;

public class OrderWorker {

	public List<GenericValue> orderItems;
	private DataWorker dataWorker;
	
	public OrderWorker(DataWorker dataWorker){
		this.dataWorker = dataWorker;
	}
	
	public void createOrderAndComplete() throws Exception{
		//this.cart.setOrderPartyId(partyId);
		dataWorker.cart.setOrderId(dataWorker.posSession.getCurrentTransaction().getTransactionId());
		dataWorker.cart.setBillFromVendorPartyId(MultiOrganizationUtil.getCurrentOrganization(dataWorker.delegator));
		dataWorker.cart.setAllShipmentMethodTypeId("NO_SHIPPING");
		this.createOrder();
		this.approveOrder();
		this.completeOrder();
	}
	
	private void createOrder() throws Exception{
		if (dataWorker.cart == null) {
            throw new Exception("Cart is NULL");
        }
		dataWorker.cart.clearAllItemStatus();
		BigDecimal grandTotal = dataWorker.cart.getGrandTotal();
        if(dataWorker.cart.isCartContainedReturn()){
//        	grandTotal = this.cart.getGranTotalReturn();
//        }else{
        	grandTotal = dataWorker.cart.getGrandTotalNoReturn();
        }
        String orderId = dataWorker.cart.getOrderId();
        // store the order - build the context 
        Map<String, Object> context = dataWorker.cart.makeCartMap(dataWorker.dispatcher, false);
        context.put("orderId", orderId);
        context.put("grandTotal", grandTotal);
        context.put("userLogin", dataWorker.posSession.getUserLogin());
        context.put("originOrderId", (String) dataWorker.cart.getAttribute("originOrderId"));
        // Update Transaction orderId
        dataWorker.posSession.getCurrentTransaction().setOrderId(orderId);
        // TODO Make the following service more lighten or create new method
        Map<String, Object> storeResult = dataWorker.dispatcher.runSync("storeOrderPos", context);
        orderId = (String) storeResult.get("orderId");
        if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(context.get("billToCustomerPartyId"))) {
        	dataWorker.cart.setOrderId(orderId);
            if (dataWorker.cart.getFirstAttemptOrderId() == null) {
            	dataWorker.cart.setFirstAttemptOrderId(orderId);
            }
        }
	}
	
	private void approveOrder() throws Exception{
		GenericValue productStore = dataWorker.delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", dataWorker.posSession.getProductStoreId()), true);
        // interal status for held orders
        String HEADER_STATUS = "ORDER_PROCESSING";
        String ITEM_STATUS = "ITEM_CREATED";
        String DIGITAL_ITEM_STATUS = "ITEM_APPROVED";

        if (productStore.get("headerApprovedStatus") != null) {
            HEADER_STATUS = productStore.getString("headerApprovedStatus");
        }
        if (productStore.get("itemApprovedStatus") != null) {
            ITEM_STATUS = productStore.getString("itemApprovedStatus");
        }
        if (productStore.get("digitalItemApprovedStatus") != null) {
            DIGITAL_ITEM_STATUS = productStore.getString("digitalItemApprovedStatus");
        }
        // TODO Improve the following function 
        PosOrderChangeHelper.orderStatusChanges(dataWorker.dispatcher, dataWorker.posSession.getUserLogin(), dataWorker.cart.getOrderId(), HEADER_STATUS, "ITEM_CREATED", ITEM_STATUS, DIGITAL_ITEM_STATUS);
	}
	
	private void completeOrder() throws Exception {
		// 1. Change order status
		Delegator delegator = dataWorker.dispatcher.getDelegator();
        String orderId = dataWorker.cart.getOrderId();
        String statusId = "ORDER_COMPLETED";
        GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        // update the current status
        orderHeader.set("statusId", statusId);

        // now create a status change
        GenericValue orderStatus = delegator.makeValue("OrderStatus");
        orderStatus.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
        orderStatus.put("statusId", statusId);
        orderStatus.put("orderId", orderId);
        orderStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
        orderStatus.put("statusUserLogin", dataWorker.posSession.getUserLoginId());
        orderHeader.store();
        orderStatus.create();
        //2. Change order item status
        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", dataWorker.cart.getOrderId());
        fields.put("statusId", "ITEM_APPROVED");
        List<GenericValue> orderItems = null;
        orderItems = delegator.findByAnd("OrderItem", fields, null, true);
        List<GenericValue> toBeStored = new ArrayList<GenericValue>();
        for (GenericValue orderItem : orderItems) {
            orderItem.set("statusId", "ITEM_COMPLETED");
            toBeStored.add(orderItem);
            // now create a status change
            Map<String, Object> changeFields = new HashMap<String, Object>();
            changeFields.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
            changeFields.put("statusId", "ITEM_COMPLETED");
            changeFields.put("orderId", dataWorker.cart.getOrderId());
            changeFields.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
            changeFields.put("statusDatetime", UtilDateTime.nowTimestamp());
            changeFields.put("statusUserLogin", dataWorker.posSession.getUserLoginId());
            toBeStored.add(delegator.makeValue("OrderStatus", changeFields));
        }
        // store the changes
        delegator.storeAll(toBeStored);
        Map<String, Object> processLoyaltyPointMap = FastMap.newInstance();
        processLoyaltyPointMap.put("isReturnOrder", "N");
        processLoyaltyPointMap.put("orderId", orderId);
        processLoyaltyPointMap.put("userLogin", dataWorker.posSession.getUserLogin());
        dataWorker.dispatcher.runSync("processLoyaltyPoint", processLoyaltyPointMap); 
	}
	// Two following methods can be dropped
	public void setOrderStatus() throws Exception {
	        Delegator delegator = dataWorker.dispatcher.getDelegator();
	        GenericValue userLogin = dataWorker.posSession.getUserLogin();
	        String orderId = dataWorker.cart.getOrderId();
	        String statusId = "ORDER_COMPLETED";
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            // update the current status
            orderHeader.set("statusId", statusId);

            // now create a status change
            GenericValue orderStatus = delegator.makeValue("OrderStatus");
            orderStatus.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
            orderStatus.put("statusId", statusId);
            orderStatus.put("orderId", orderId);
            orderStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            orderStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
            orderHeader.store();
            orderStatus.create();
    }
//	public void setItemStatus() throws GenericEntityException {
//        Delegator delegator = dispatcher.getDelegator();
//        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", this.cart.getOrderId());
//        fields.put("statusId", "ITEM_APPROVED");
//        orderItems = delegator.findByAnd("OrderItem", fields, null, true);
//        List<GenericValue> toBeStored = new ArrayList<GenericValue>();
//        for (GenericValue orderItem : orderItems) {
//            orderItem.set("statusId", "ITEM_COMPLETED");
//            toBeStored.add(orderItem);
//            // now create a status change
//            Map<String, Object> changeFields = new HashMap<String, Object>();
//            changeFields.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
//            changeFields.put("statusId", "ITEM_COMPLETED");
//            changeFields.put("orderId", this.cart.getOrderId());
//            changeFields.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
//            changeFields.put("statusDatetime", UtilDateTime.nowTimestamp());
//            changeFields.put("statusUserLogin", posSession.getUserLoginId());
//            GenericValue orderStatus = delegator.makeValue("OrderStatus", changeFields);
//            toBeStored.add(orderStatus);
//        }
//        // store the changes
//        delegator.storeAll(toBeStored);
//    }
}
