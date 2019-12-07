package com.olbius.basesales.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CheckOutEvents extends CheckOutHelper{

	public CheckOutEvents(LocalDispatcher dispatcher, Delegator delegator,
			ShoppingCart cart) {
		super(dispatcher, delegator, cart);
		// TODO Auto-generated constructor stub
	}
	
	// Event wrapper for processPayment.
    public static String processPayment(HttpServletRequest request, HttpServletResponse response) {
        // run the process payment process + approve order when complete; may also run sync fulfillments
        int failureCode = 0;
        try {
            if (!processPayment(request)) {
                failureCode = 1;
            }
        } catch (GeneralException e) {
            Debug.logError(e, module);
            ServiceUtil.setMessages(request, e.getMessage(), null, null);
            failureCode = 2;
        } catch (GeneralRuntimeException e) {
            Debug.logError(e, module);
            ServiceUtil.setMessages(request, e.getMessage(), null, null);
        }

        // event return based on failureCode
        switch (failureCode) {
            case 0:
                return "success";
            case 1:
                return "fail";
            default:
                return "error";
        }
    }
    
    private static boolean processPayment(HttpServletRequest request) throws GeneralException {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

        // TODOCHANGE
        Map<String, Object> callResult = null;
        try {
        	String orderId = cart.getOrderId();
	        Map<String, Object> resultCheckProcessPayment = dispatcher.runSync("checkProcessPayment", 
	        		UtilMisc.<String, Object>toMap("orderId", orderId, "userLogin", userLogin));
	        if (ServiceUtil.isSuccess(resultCheckProcessPayment)) {
	        	boolean toProcess = (Boolean) resultCheckProcessPayment.get("toProcess");
	        	if (!toProcess) {
	        		// send notify to seller
		        	// TODOCHANGE send notify to partyIds
		            Map<String, Object> notiCtx = new HashMap<String, Object>();
		            notiCtx.put("orderId", orderId);
		            notiCtx.put("userLogin", userLogin);
		            Map<String, Object> notiResp = dispatcher.runSync("sendNotiChangeOrderStatus", notiCtx);
		            if (notiResp != null && ServiceUtil.isError(notiResp)) {
		                Debug.logWarning(ServiceUtil.getErrorMessage(notiResp), module);
		            }
		            // jump over this step: process payment
		        	callResult = ServiceUtil.returnSuccess();
		        	return (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS));
	        	}
	        }
        } catch (Exception e) {
        	Debug.log("Warning: Call checkProcessPayment is fail");
        	
        	// jump over this step: process payment
        	callResult = ServiceUtil.returnSuccess();
        	return (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS));
        }
        
        // check if the order is to be held (processing)
        boolean holdOrder = cart.getHoldOrder();

        // load the ProductStore settings
        GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
        callResult = checkOutHelper.processPayment(productStore, userLogin, false, holdOrder);

        if (ServiceUtil.isError(callResult)) {
            // clear out the rejected payment methods (if any) from the cart, so they don't get re-authorized
            cart.clearDeclinedPaymentMethods(delegator);
            // null out the orderId for next pass
            cart.setOrderId(null);
        }
        
        // TODOCHANGE send notify to partyIds
        Map<String, Object> notiCtx = new HashMap<String, Object>();
        notiCtx.put("orderId", cart.getOrderId());
        notiCtx.put("userLogin", userLogin);
        Map<String, Object> notiResp = dispatcher.runSync("sendNotiChangeOrderStatus", notiCtx);
        if (notiResp != null && ServiceUtil.isError(notiResp)) {
            Debug.logWarning(ServiceUtil.getErrorMessage(notiResp), module);
        }

        // generate any messages required
        ServiceUtil.getMessages(request, callResult, null);

        // check for customer message(s)
        List<String> messages = UtilGenerics.checkList(callResult.get("authResultMsgs"));
        if (UtilValidate.isNotEmpty(messages)) {
            request.setAttribute("_EVENT_MESSAGE_LIST_", messages);
        }

        // determine whether it was a success or failure
        return (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS));
    }
}
