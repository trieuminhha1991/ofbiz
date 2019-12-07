package com.olbius.basesales.shoppingcart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;

import com.olbius.security.util.SecurityUtil;

public class CheckOutWithoutAccTransEvents extends CheckOutEvents {
	
	/**
     * Use for quickcheckout submit.  It calculates the tax before setting the payment options.
     * Shipment option should already be set by the quickcheckout form.
     */
    public static String setQuickCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        String result = calcTax(request, response);
        if ("error".equals(result)) return "error";
        return setCheckOutOptions(request, response);
    }
	
    // this servlet is used by quick checkout
    public static String setCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        // Set the payment options
        Map<String, Map<String, Object>> selectedPaymentMethods = getSelectedPaymentMethods(request); // DONENOACCTRANS: deleted

        CheckOutWithoutAccTransHelper checkOutHelperWithout = new CheckOutWithoutAccTransHelper(dispatcher, delegator, cart);

        // get the billing account and amount
        /* DONENOACCTRANS: deleted */
        String billingAccountId = request.getParameter("billingAccountId");
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            BigDecimal billingAccountAmt = null;
            billingAccountAmt = determineBillingAccountAmount(billingAccountId, request.getParameter("billingAccountAmount"), dispatcher);
            if (billingAccountAmt == null) {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderInvalidAmountSetForBillingAccount", UtilMisc.toMap("billingAccountId",billingAccountId), (cart != null ? cart.getLocale() : Locale.getDefault())));
                return "error";
            }
            selectedPaymentMethods.put("EXT_BILLACT", UtilMisc.<String, Object>toMap("amount", billingAccountAmt, "securityCode", null));
        }

        if (selectedPaymentMethods == null) {
            return "error";
        }
        
        /* DONENOACCTRANS: deleted
        String shippingMethod = request.getParameter("shipping_method");
        if (shippingMethod == null){
        	shippingMethod = (String)request.getAttribute("shipping_method");
        }*/
        String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
        if (shippingContactMechId == null){
        	shippingContactMechId = (String)request.getAttribute("shipping_contact_mech_id");
        }
        String taxAuthPartyGeoIds = request.getParameter("taxAuthPartyGeoIds");
        String partyTaxId = request.getParameter("partyTaxId");
        String isExempt = request.getParameter("isExempt");

        String shippingInstructions = request.getParameter("shipping_instructions");
        // String orderAdditionalEmails = request.getParameter("order_additional_emails"); //DONENOACCTRANS: deleted
        String maySplit = request.getParameter("may_split");
        if (maySplit == null){
        	maySplit = (String)request.getAttribute("may_split");
        }
        String giftMessage = request.getParameter("gift_message");
        // DONENOACCTRANS: deleted
        /*String isGift = request.getParameter("is_gift");
        if (isGift == null){
        	isGift = (String)request.getAttribute("is_gift");
        }*/
        String internalCode = request.getParameter("internalCode");
        String shipBeforeDate = request.getParameter("shipBeforeDate");
        String shipAfterDate = request.getParameter("shipAfterDate");

        List<String> singleUsePayments = new ArrayList<String>();

        // get a request map of parameters
        //Map<String, Object> params = UtilHttp.getParameterMap(request);

        // if taxAuthPartyGeoIds is not empty drop that into the database
        if (UtilValidate.isNotEmpty(taxAuthPartyGeoIds)) {
            try {
                Map<String, Object> createCustomerTaxAuthInfoResult = dispatcher.runSync("createCustomerTaxAuthInfo",
                        UtilMisc.toMap("partyId", cart.getPartyId(), "taxAuthPartyGeoIds", taxAuthPartyGeoIds, "partyTaxId", partyTaxId, "isExempt", isExempt));
                ServiceUtil.getMessages(request, createCustomerTaxAuthInfoResult, null);
                if (ServiceUtil.isError(createCustomerTaxAuthInfoResult)) {
                    return "error";
                }
            } catch (GenericServiceException e) {
                String errMsg = "Error setting customer tax info: " + e.toString();
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        }

        // check for gift card not on file
        /* DONENOACCTRANS: deleted
        Map<String, Object> gcResult = checkOutHelper.checkGiftCard(params, selectedPaymentMethods);
        ServiceUtil.getMessages(request, gcResult, null);
        if (ServiceUtil.isError(gcResult)) {
            return "error";
        }

        String gcPaymentMethodId = (String) gcResult.get("paymentMethodId");
        BigDecimal gcAmount = (BigDecimal) gcResult.get("amount");
        if (gcPaymentMethodId != null) {
            selectedPaymentMethods.put(gcPaymentMethodId, UtilMisc.<String, Object>toMap("amount", gcAmount, "securityCode", null));
            if ("Y".equalsIgnoreCase(request.getParameter("singleUseGiftCard"))) {
                singleUsePayments.add(gcPaymentMethodId);
            }
        }*/

        Map<String, Object> optResult = checkOutHelperWithout.setCheckOutOptions(null, shippingContactMechId, selectedPaymentMethods,
                singleUsePayments, null, shippingInstructions, maySplit, giftMessage, internalCode, shipBeforeDate, shipAfterDate);
        /* DONENOACCTRANS: deleted
        Map<String, Object> optResult = checkOutHelper.setCheckOutOptions(shippingMethod, shippingContactMechId, selectedPaymentMethods,
                singleUsePayments, billingAccountId, shippingInstructions,
                orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate);
         */

        ServiceUtil.getMessages(request, optResult, null);
        if (ServiceUtil.isError(optResult)) {
            return "error";
        }

        return "success";
    }
    
    // Create order event - uses createOrder service for processing
    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutWithoutAccTransHelper checkOutHelper = new CheckOutWithoutAccTransHelper(dispatcher, delegator, cart);
        Map<String, Object> callResult;

        if (UtilValidate.isEmpty(userLogin)) {
            userLogin = cart.getUserLogin();
            session.setAttribute("userLogin", userLogin);
        }
        // remove this whenever creating an order so quick reorder cache will refresh/recalc
        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

        boolean areOrderItemsExploded = explodeOrderItems(delegator, cart);

        //get the TrackingCodeOrder List
        List<GenericValue> trackingCodeOrders = null; // TODOCHANGE without acc: TrackingCodeEvents.makeTrackingCodeOrders(request);
        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        String visitId = VisitHandler.getVisitId(session);
        String webSiteId = WebSiteWorker.getWebSiteId(request);

        callResult = checkOutHelper.createOrder(userLogin, distributorId, affiliateId, trackingCodeOrders, areOrderItemsExploded, visitId, webSiteId);
        if (callResult != null) {
            ServiceUtil.getMessages(request, callResult, null);
            if (ServiceUtil.isError(callResult)) {
                // messages already setup with the getMessages call, just return the error response code
                return "error";
            }
            if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                // set the orderId for use by chained events
                String orderId = cart.getOrderId();
                request.setAttribute("orderId", orderId);
                request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
            }
        }
        
        String issuerId = request.getParameter("issuerId");
        if (UtilValidate.isNotEmpty(issuerId)) {
            request.setAttribute("issuerId", issuerId);
        }
        
        if ("sales_order".equals(cart.getOrderType().toLowerCase())) {
        	/* Sequence process after process sales order
			 * checkBlackListSalesAjax
			 * processPaymentSalesAjax
			 * clearCartSalesAjax
        	 */
        	Security security = (Security) request.getAttribute("security");
        	if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "DIS_SALESORDER")) {
        		String resultProcessPayment = com.olbius.basesales.order.CheckOutEvents.processPayment(request, response);
        		if ("error".equals(resultProcessPayment)) return resultProcessPayment;
        	}
        	
        	String resultClearCart = org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
        	if ("error".equals(resultClearCart)) return resultClearCart;
        	
        	session.setAttribute("shipping_contact_mech_id", null);
        	session.setAttribute("shipping_method", null);
        	session.setAttribute("checkOutPaymentId", null);
        	session.setAttribute("may_split", null);
        	session.setAttribute("is_gift", null);
        }

        return "success";
    }
}
