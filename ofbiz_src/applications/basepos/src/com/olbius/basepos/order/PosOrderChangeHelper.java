package com.olbius.basepos.order;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class PosOrderChangeHelper extends OrderChangeHelper{
	public static String module = PosOrderChangeHelper.class.getName();
	public static final String resource_order = "OrderUiLabels";
	
	public static boolean completeOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId){
		try {
			PosOrderChangeHelper.createReceivedPayments(dispatcher, userLogin, orderId);
			PosOrderChangeHelper.createOrderInvoice(dispatcher, userLogin, orderId);
			PosOrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, "ORDER_COMPLETED", "ITEM_APPROVED", "ITEM_COMPLETED", null);
		} catch (GenericEntityException e) {
            Debug.logError(e, module);
            return false;
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return false;
        }
		return true;
	}
	
	public static void createReceivedPayments(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) throws GenericEntityException, GenericServiceException {
        GenericValue orderHeader = null;
        try {
            orderHeader = dispatcher.getDelegator().findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            GenericValue btparty = orh.getBillToParty();
            String partyId = "_NA_";
            if (btparty != null) {
                partyId = btparty.getString("partyId");
            }

            List<GenericValue> opps = orh.getPaymentPreferences();
            for (GenericValue opp : opps) {
                if ("PAYMENT_RECEIVED".equals(opp.getString("statusId"))) {
                    List<GenericValue> payments = orh.getOrderPayments(opp);
                    if (UtilValidate.isEmpty(payments)) {
                        // only do this one time; if we have payment already for this pref ignore.
                        Map<String, Object> results = dispatcher.runSync("createPaymentFromPreferencePos",
                                UtilMisc.<String, Object>toMap("userLogin", userLogin, "orderPaymentPreferenceId", opp.getString("orderPaymentPreferenceId"),
                                "paymentRefNum",  UtilDateTime.nowTimestamp().toString(), "paymentFromId", partyId));
                        if (results.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
                            Debug.logError((String) results.get(ModelService.ERROR_MESSAGE), module);
                        }
                    }
                }
            }
        }
    }
	
	public static void createOrderInvoice(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) throws GenericServiceException {
        GenericValue orderHeader = null;
        try {
            orderHeader = dispatcher.getDelegator().findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            List<GenericValue> items = orh.getOrderItems();

            Map<String, Object> serviceParam = UtilMisc.<String, Object>toMap("orderId", orderId, "billItems", items, "userLogin", userLogin);
            Map<String, Object> serviceRes = dispatcher.runSync("createInvoiceForOrderPos", serviceParam);
            if (ServiceUtil.isError(serviceRes)) {
                throw new GenericServiceException(ServiceUtil.getErrorMessage(serviceRes));
            }
        }
    }
	
	public static void orderStatusChanges(LocalDispatcher dispatcher, GenericValue userLogin, String orderId, String orderStatus, String fromItemStatus, String toItemStatus, String digitalItemStatus) throws GenericServiceException {
        // set the status on the order header
        Map<String, Object> statusFields = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", orderStatus, "userLogin", userLogin);
        Map<String, Object> statusResult = dispatcher.runSync("changeOrderStatusPos", statusFields);
        if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
            Debug.logError("Problems adjusting order header status for order #" + orderId, module);
        }

        // set the status on the order item(s)
        Map<String, Object> itemStatusFields = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", toItemStatus, "userLogin", userLogin);
        if (fromItemStatus != null) {
            itemStatusFields.put("fromStatusId", fromItemStatus);
        }
        Map<String, Object> itemStatusResult = dispatcher.runSync("changeOrderItemStatusPos", itemStatusFields);
        if (itemStatusResult.containsKey(ModelService.ERROR_MESSAGE)) {
            Debug.logError("Problems adjusting order item status for order #" + orderId, module);
        }
    }
	
	public static boolean approveOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId, boolean holdOrder) {
        GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(dispatcher.getDelegator(), orderId);
        if (productStore == null) {
            throw new IllegalArgumentException("Could not find ProductStore for orderId [" + orderId + "], cannot approve order.");
        }
        
        // interal status for held orders
        String HEADER_STATUS = "ORDER_PROCESSING";
        String ITEM_STATUS = "ITEM_CREATED";
        String DIGITAL_ITEM_STATUS = "ITEM_APPROVED";

        if (!holdOrder) {
            if (productStore.get("headerApprovedStatus") != null) {
                HEADER_STATUS = productStore.getString("headerApprovedStatus");
            }
            if (productStore.get("itemApprovedStatus") != null) {
                ITEM_STATUS = productStore.getString("itemApprovedStatus");
            }
            if (productStore.get("digitalItemApprovedStatus") != null) {
                DIGITAL_ITEM_STATUS = productStore.getString("digitalItemApprovedStatus");
            }
        }

        try {
            PosOrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, HEADER_STATUS, "ITEM_CREATED", ITEM_STATUS, DIGITAL_ITEM_STATUS);
           
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return false;
        }

        return true;
    }
	
}
