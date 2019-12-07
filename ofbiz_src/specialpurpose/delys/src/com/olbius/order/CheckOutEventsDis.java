package com.olbius.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.order.CheckOutHelperDis;

import org.ofbiz.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;

public class CheckOutEventsDis {
	public static final String resource = "DelysAdminUiLabels";
	public static String module = CheckOutEventsDis.class.getName();
    
    public static Map<String, Map<String, Object>> getSelectedPaymentMethodsDis(HttpServletRequest request) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        //Locale locale = UtilHttp.getLocale(request);
        Map<String, Map<String, Object>> selectedPaymentMethods = new HashMap<String, Map<String, Object>>();
        String[] paymentMethods = request.getParameterValues("checkOutPaymentId");
        if (paymentMethods == null || paymentMethods.length == 0 ){
        	paymentMethods = (String[])request.getAttribute("checkOutPaymentId");
        }
        if (UtilValidate.isNotEmpty(request.getParameter("issuerId"))) {
            request.setAttribute("issuerId", request.getParameter("issuerId"));
        }

        String errMsg = null;

        if (paymentMethods != null) {
            for (int i = 0; i < paymentMethods.length; i++) {
                Map<String, Object> paymentMethodInfo = FastMap.newInstance();

                String securityCode = request.getParameter("securityCode_" + paymentMethods[i]);
                if (UtilValidate.isNotEmpty(securityCode)) {
                    paymentMethodInfo.put("securityCode", securityCode);
                }
                String amountStr = request.getParameter("amount_" + paymentMethods[i]);
                BigDecimal amount = null;
                if (UtilValidate.isNotEmpty(amountStr) && !"REMAINING".equals(amountStr)) {
                    try {
                        amount = new BigDecimal(amountStr);
                    } catch (NumberFormatException e) {
                        Debug.logError(e, module);
                        errMsg = UtilProperties.getMessage(resource, "checkevents.invalid_amount_set_for_payment_method", (cart != null ? cart.getLocale() : Locale.getDefault()));
                        request.setAttribute("_ERROR_MESSAGE_", errMsg);
                        return null;
                    }
                }
                paymentMethodInfo.put("amount", amount);
                selectedPaymentMethods.put(paymentMethods[i], paymentMethodInfo);
            }
        }
        Debug.logInfo("Selected Payment Methods : " + selectedPaymentMethods, module);
        return selectedPaymentMethods;
    }
    
    /**
     * Determine what billing account amount to use based on the form input.
     * This method returns the amount that will be charged to the billing account.
     *
     * An amount can be associated with the billingAccountId with a
     * parameter billingAccountAmount.  If no amount is specified, then
     * the entire available balance of the given billing account will be used.
     * If there is an error, a null will be returned.
     *
     * @return  Amount to charge billing account or null if there was an error
     */
    /*private static BigDecimal determineBillingAccountAmountDis (String billingAccountId, String billingAccountAmount, LocalDispatcher dispatcher) {
        BigDecimal billingAccountAmt = null;

        // set the billing account amount to the minimum of billing account available balance or amount input if less than balance
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            // parse the amount to a decimal
            if (UtilValidate.isNotEmpty(billingAccountAmount)) {
                try {
                    billingAccountAmt = new BigDecimal(billingAccountAmount);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (billingAccountAmt == null) {
                billingAccountAmt = BigDecimal.ZERO;
            }
            BigDecimal availableBalance = CheckOutHelper.availableAccountBalance(billingAccountId, dispatcher);

            // set amount to be charged to entered amount unless it exceeds the available balance
            BigDecimal chargeAmount = BigDecimal.ZERO;
            if (billingAccountAmt.compareTo(availableBalance) < 0) {
                chargeAmount = billingAccountAmt;
            } else {
                chargeAmount = availableBalance;
            }
            if (chargeAmount.compareTo(BigDecimal.ZERO) < 0.0) {
                chargeAmount = BigDecimal.ZERO;
            }

            return chargeAmount;
        } else {
            return null;
        }
    }*/
    
    /*
    <request-map uri="processorder">
        <security https="true" auth="true"/>
        <event type="java" path="org.ofbiz.order.shoppingcart.CheckOutEvents" invoke="createOrder"/>
        <response name="sales_order" type="request" value="checkBlackList"/>
        <response name="work_order" type="request" value="checkBlackList"/>
        <response name="purchase_order" type="request" value="clearpocart"/>
        <response name="error" type="view" value="confirm"/>
    </request-map>
     */
    // Create order event - uses createOrder service for processing
    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutHelperDis checkOutHelper = new CheckOutHelperDis(dispatcher, delegator, cart);
        Map<String, Object> callResult;

        if (UtilValidate.isEmpty(userLogin)) {
            userLogin = cart.getUserLogin();
            session.setAttribute("userLogin", userLogin);
        }
        // remove this whenever creating an order so quick reorder cache will refresh/recalc
        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

        boolean areOrderItemsExploded = explodeOrderItems(delegator, cart);

        //get the TrackingCodeOrder List
        List<GenericValue> trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);
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
        
        return cart.getOrderType().toLowerCase();
    }
    
    public static boolean explodeOrderItems(Delegator delegator, ShoppingCart cart) {
        if (cart == null) return false;
        GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
        if (productStore == null || productStore.get("explodeOrderItems") == null) {
            return false;
        }
        return productStore.getBoolean("explodeOrderItems").booleanValue();
    }
    
    /*
    <request-map uri="checkBlackList">
        <security direct-request="false"/>
        <event type="java" path="org.ofbiz.order.shoppingcart.CheckOutEvents" invoke="checkOrderBlacklist"/>
        <response name="success" type="request" value="processpayment"/>
        <response name="failed" type="request" value="failedBlacklist"/>
        <response name="error" type="view" value="confirm"/>
    </request-map>
     */
    public static String checkOrderBlacklist(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        CheckOutHelper checkOutHelper = new CheckOutHelper(null, delegator, cart);
        String result;

        Map<String, Object> callResult = checkOutHelper.checkOrderBlackList();
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            request.setAttribute("_ERROR_MESSAGE_", callResult.get(ModelService.ERROR_MESSAGE));
            result = "error";
        } else if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_FAIL)) {
            request.setAttribute("_ERROR_MESSAGE_", callResult.get(ModelService.ERROR_MESSAGE));
            result = "failed";
        } else {
            result = (String) callResult.get(ModelService.SUCCESS_MESSAGE);
        }

        return result;
    }
    
    /*
    <request-map uri="failedBlacklist">
        <security direct-request="false"/>
        <event type="java" path="org.ofbiz.order.shoppingcart.CheckOutEvents" invoke="failedBlacklistCheck"/>
        <response name="success" type="view" value="main"/>
        <response name="error" type="view" value="main"/>
    </request-map>
     */
    public static String failedBlacklistCheck(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String orderPartyId = cart.getOrderPartyId();
        GenericValue userLogin = PartyWorker.findPartyLatestUserLogin(orderPartyId, delegator);
        GenericValue currentUser = (GenericValue) session.getAttribute("userLogin");
        String result;

        // Load the properties store
        GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> callResult = checkOutHelper.failedBlacklistCheck(userLogin, productStore);

        //Generate any messages required
        ServiceUtil.getMessages(request, callResult, null);

        // wipe the session
        if (("anonymous".equals(currentUser.getString("userLoginId"))) || (currentUser.getString("userLoginId")).equals(userLogin.getString("userLoginId"))) {
            session.invalidate();
        }
        //Determine whether it was a success or not
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            result = (String) callResult.get(ModelService.ERROR_MESSAGE);
            request.setAttribute("_ERROR_MESSAGE_", result);
            result = "error";
        } else {
            result = (String) callResult.get(ModelService.ERROR_MESSAGE);
            request.setAttribute("_ERROR_MESSAGE_", result);
            result = "success";
        }
        return result;
    }
    
    /*
    <request-map uri="processpayment">
        <security direct-request="false"/>
        <event type="java" path="org.ofbiz.order.shoppingcart.CheckOutEvents" invoke="processPayment"/>
        <response name="success" type="request" value="clearcart"/>
        <response name="fail" type="view" value="confirm"/>
        <response name="error" type="view" value="confirm"/>
    </request-map>
     */
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

        // check if the order is to be held (processing)
        boolean holdOrder = cart.getHoldOrder();

        // load the ProductStore settings
        GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
        Map<String, Object> callResult = checkOutHelper.processPayment(productStore, userLogin, false, holdOrder);

        if (ServiceUtil.isError(callResult)) {
            // clear out the rejected payment methods (if any) from the cart, so they don't get re-authorized
            cart.clearDeclinedPaymentMethods(delegator);
            // null out the orderId for next pass
            cart.setOrderId(null);
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
    
    /*
    <request-map uri="clearcart">
        <security https="true" auth="true"/>
        <event type="java" path="org.ofbiz.order.shoppingcart.ShoppingCartEvents" invoke="destroyCart"/>
        <response name="success" type="request" value="emailorder"/>
        <response name="error" type="view" value="confirm"/>
    </request-map>
     */
    
    
    /*
    <request-map uri="emailorder">
        <security https="true" direct-request="false"/>
        <event type="service" path="async" invoke="sendOrderConfirmation"/>
        <response name="success" type="view" value="ordercomplete"/>
        <response name="error" type="view" value="ordercomplete"/>
    </request-map>
     */
    
    /**
     * Use for quickcheckout submit.  It calculates the tax before setting the payment options.
     * Shipment option should already be set by the quickcheckout form.
     */
    public static String setQuickCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        String result = org.ofbiz.order.shoppingcart.CheckOutEvents.calcTax(request, response);
        if ("error".equals(result)) return "error";
        return setCheckOutOptions(request, response);
    }
    
    // this servlet is used by quick checkout
    public static String setCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        // Set the payment options
        //DONENOACCTRANS: deleted
        //Map<String, Map<String, Object>> selectedPaymentMethods = getSelectedPaymentMethods(request);

        CheckOutHelperDis checkOutHelperDis = new CheckOutHelperDis(dispatcher, delegator, cart);

        // get the billing account and amount
        //DONENOACCTRANS: deleted
        /*String billingAccountId = request.getParameter("billingAccountId");
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
        }*/
        
        //DONENOACCTRANS: deleted
        /*String shippingMethod = request.getParameter("shipping_method");
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
        //DONENOACCTRANS: deleted
        //String orderAdditionalEmails = request.getParameter("order_additional_emails");
        String maySplit = request.getParameter("may_split");
        if (maySplit == null){
        	maySplit = (String)request.getAttribute("may_split");
        }
        String giftMessage = request.getParameter("gift_message");
        //DONENOACCTRANS: deleted
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
        //DONENOACCTRANS: deleted
        /* Map<String, Object> gcResult = checkOutHelper.checkGiftCard(params, selectedPaymentMethods);
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

        Map<String, Object> optResult = checkOutHelperDis.setCheckOutOptions(null, shippingContactMechId, null,
                singleUsePayments, null, shippingInstructions, maySplit, giftMessage, internalCode, shipBeforeDate, shipAfterDate);
        //DONENOACCTRANS: deleted
        /*
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
}
