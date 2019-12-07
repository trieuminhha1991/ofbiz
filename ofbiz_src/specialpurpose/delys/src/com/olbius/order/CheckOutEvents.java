/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;

/**
 * Events used for processing checkout and orders.
 */
public class CheckOutEvents extends org.ofbiz.order.shoppingcart.CheckOutEvents{

    // Create order event - uses createOrder service for processing
    // TODOCHANGE change createOrder for record promotion
    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> callResult;

        if (UtilValidate.isEmpty(userLogin)) {
            userLogin = cart.getUserLogin();
            session.setAttribute("userLogin", userLogin);
        }
        
        // create the order association
        List<ShoppingCartItem> cartLines = cart.items();
        for (ShoppingCartItem sci : cartLines) {
            int index = cart.getItemIndex(sci);
            try {
                Map<String, Object> orderItemMap = FastMap.newInstance();
                // orderItemMap.put("orderId", originalOrderId);
                orderItemMap.put("orderId", sci.getAssociatedOrderId());
                orderItemMap.put("orderItemSeqId", sci.getAssociatedOrderItemSeqId());
                orderItemMap.put("isPromo", sci.getIsPromo() ? "Y" : "N");
                orderItemMap.put("productId", sci.getProductId());
                orderItemMap.put("orderItemTypeId", sci.getItemType());
                GenericValue orderItem = EntityUtil.getFirst(delegator.findByAnd("OrderItem", orderItemMap, null, false));
                if (UtilValidate.isNotEmpty(orderItem)) {
                    sci.setAssociatedOrderId(orderItem.getString("orderId"));
                    sci.setAssociatedOrderItemSeqId(orderItem.getString("orderItemSeqId"));
                    sci.setOrderItemAssocTypeId("PAY_PROMO");
                    cart.addItem(index, sci);
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            } catch (CartItemModifyException e) {
                Debug.logError(e.getMessage(), module);
            }
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
    
    // TODOCHANGE copy from createOrder event
    public static String createOrderDis(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> callResult;

        if (UtilValidate.isEmpty(userLogin)) {
            userLogin = cart.getUserLogin();
            session.setAttribute("userLogin", userLogin);
        }
        
        // create the order association
        List<ShoppingCartItem> cartLines = cart.items();
        for (ShoppingCartItem sci : cartLines) {
            int index = cart.getItemIndex(sci);
            try {
                Map<String, Object> orderItemMap = FastMap.newInstance();
                // orderItemMap.put("orderId", originalOrderId);
                orderItemMap.put("orderId", sci.getAssociatedOrderId());
                orderItemMap.put("orderItemSeqId", sci.getAssociatedOrderItemSeqId());
                orderItemMap.put("isPromo", sci.getIsPromo() ? "Y" : "N");
                orderItemMap.put("productId", sci.getProductId());
                orderItemMap.put("orderItemTypeId", sci.getItemType());
                GenericValue orderItem = EntityUtil.getFirst(delegator.findByAnd("OrderItem", orderItemMap, null, false));
                if (UtilValidate.isNotEmpty(orderItem)) {
                    sci.setAssociatedOrderId(orderItem.getString("orderId"));
                    sci.setAssociatedOrderItemSeqId(orderItem.getString("orderItemSeqId"));
                    sci.setOrderItemAssocTypeId("PAY_PROMO");
                    cart.addItem(index, sci);
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            } catch (CartItemModifyException e) {
                Debug.logError(e.getMessage(), module);
            }
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

        callResult = checkOutHelper.createOrderDis(userLogin, distributorId, affiliateId, trackingCodeOrders, areOrderItemsExploded, visitId, webSiteId, true);
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
    
    /**
     * Use for quickcheckout submit.  It calculates the tax before setting the payment options.
     * Shipment option should already be set by the quickcheckout form.
     */
    public static String setQuickCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        String result = calcTax(request, response);
        if ("error".equals(result)) return "error";
        return setCheckOutOptions(request, response);
    }
    
    // TODOCHANGE
    // this servlet is used by quick checkout
    public static String setCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        // Set the payment options
        Map<String, Map<String, Object>> selectedPaymentMethods = getSelectedPaymentMethods(request);

        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

        // get the billing account and amount
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

        String shippingMethod = request.getParameter("shipping_method");
        String shippingContactMechId = request.getParameter("shipping_contact_mech_id");

        String taxAuthPartyGeoIds = request.getParameter("taxAuthPartyGeoIds");
        String partyTaxId = request.getParameter("partyTaxId");
        String isExempt = request.getParameter("isExempt");

        String shippingInstructions = request.getParameter("shipping_instructions");
        String orderAdditionalEmails = request.getParameter("order_additional_emails");
        String maySplit = request.getParameter("may_split");
        String giftMessage = request.getParameter("gift_message");
        String isGift = request.getParameter("is_gift");
        String internalCode = request.getParameter("internalCode");
        String shipBeforeDate = request.getParameter("shipBeforeDate");
        String shipAfterDate = request.getParameter("shipAfterDate");

        List<String> singleUsePayments = new ArrayList<String>();

        // get a request map of parameters
        Map<String, Object> params = UtilHttp.getParameterMap(request);

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
        }

        Map<String, Object> optResult = checkOutHelper.setCheckOutOptions(shippingMethod, shippingContactMechId, selectedPaymentMethods,
                singleUsePayments, billingAccountId, shippingInstructions,
                orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate);

        ServiceUtil.getMessages(request, optResult, null);
        if (ServiceUtil.isError(optResult)) {
            return "error";
        }

        return "success";
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
    protected static BigDecimal determineBillingAccountAmount(String billingAccountId, String billingAccountAmount, LocalDispatcher dispatcher) {
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
    }
    
    // TODOCHANGE add new method from "createReplacementOrder" method
    /** Create a pay promotion order from an existing order against a lost shipment etc. **/
    public static String createPayPromoOrder(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");

        Map<String, Object> context = cart.makeCartMap(dispatcher, false);
        String originalOrderId = request.getParameter("orderId");

        // create the replacement order adjustment
        List <GenericValue>orderAdjustments = UtilGenerics.checkList(context.get("orderAdjustments"));
        List <GenericValue>orderItems = UtilGenerics.checkList(context.get("orderItems"));
        OrderReadHelper orderReadHelper = new OrderReadHelper(orderAdjustments, orderItems);
        BigDecimal grandTotal = orderReadHelper.getOrderGrandTotal();
        if (grandTotal.compareTo(new BigDecimal(0)) != 0) {
            GenericValue adjustment = delegator.makeValue("OrderAdjustment");
            adjustment.set("orderAdjustmentTypeId", "REPLACE_ADJUSTMENT");
            adjustment.set("amount", grandTotal.negate());
            adjustment.set("comments", "ReShip Order for Order #" + originalOrderId);
            adjustment.set("createdDate", UtilDateTime.nowTimestamp());
            adjustment.set("createdByUserLogin", userLogin.getString("userLoginId"));
            cart.addAdjustment(adjustment);
        }
        /*
        // create the order association
        List<ShoppingCartItem> cartLines = cart.items();
        for (ShoppingCartItem sci : cartLines) {
            int index = cart.getItemIndex(sci);
            try {
                Map<String, Object> orderItemMap = FastMap.newInstance();
                // orderItemMap.put("orderId", originalOrderId);
                orderItemMap.put("orderId", sci.getAssociatedOrderId());
                orderItemMap.put("orderItemSeqId", sci.getAssociatedOrderItemSeqId());
                orderItemMap.put("isPromo", sci.getIsPromo() ? "Y" : "N");
                orderItemMap.put("productId", sci.getProductId());
                orderItemMap.put("orderItemTypeId", sci.getItemType());
                GenericValue orderItem = EntityUtil.getFirst(delegator.findByAnd("OrderItem", orderItemMap, null, false));
                if (UtilValidate.isNotEmpty(orderItem)) {
                    sci.setAssociatedOrderId(orderItem.getString("orderId"));
                    sci.setAssociatedOrderItemSeqId(orderItem.getString("orderItemSeqId"));
                    sci.setOrderItemAssocTypeId("PAY_PROMO");
                    cart.addItem(index, sci);
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            } catch (CartItemModifyException e) {
                Debug.logError(e.getMessage(), module);
            }
        }
         * */

        String result = createOrder(request, response);
        if ("error".equals(result)) {
            return "error";
        } else {
            return "success";
        }
    }
    
    public static String setPartialCheckOutOptionsSales(HttpServletRequest request, HttpServletResponse response) {
        // FIXME response need to be checked ?
        // String resp = setCheckOutOptions(request, response);
    	setCheckOutOptionsSales(request, response);
        request.setAttribute("_ERROR_MESSAGE_", null);
        return "success";
    }
    
    public static String setQuickCheckOutOptionsSales(HttpServletRequest request, HttpServletResponse response) {
        String result = calcTax(request, response);
        if ("error".equals(result)) return "error";
        return setCheckOutOptionsSales(request, response);
    }
    
    public static String setCheckOutOptionsSales(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        // Set the payment options
        Map<String, Map<String, Object>> selectedPaymentMethods = getSelectedPaymentMethodsSales(request);

        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

        // get the billing account and amount
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

        //String shippingMethod = request.getParameter("shipping_method"); //NO_SHIPPING@_NA_, NEXT_DAY@UPS, AIR@UPS, GROUND@UPS, EXPRESS@USPS, ...
        //if (shippingMethod == null){
        //	shippingMethod = (String)request.getAttribute("shipping_method");
        //}
        // DONEORDERSALESCTY
        String shippingMethod = "NO_SHIPPING@_NA_";
        
        String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
        if (shippingContactMechId == null){
        	shippingContactMechId = (String)request.getAttribute("shipping_contact_mech_id");
        }
        String taxAuthPartyGeoIds = request.getParameter("taxAuthPartyGeoIds");
        String partyTaxId = request.getParameter("partyTaxId");
        String isExempt = request.getParameter("isExempt");

        String shippingInstructions = request.getParameter("shipping_instructions");
        String orderAdditionalEmails = request.getParameter("order_additional_emails");
        //String maySplit = request.getParameter("may_split");
        //if (maySplit == null){
        //	maySplit = (String)request.getAttribute("may_split");
        //}
        // DONEORDERSALESCTY
        String maySplit = "false";
        
        String giftMessage = request.getParameter("gift_message");
        //String isGift = request.getParameter("is_gift");
        //if (isGift == null){
        //	isGift = (String)request.getAttribute("is_gift");
        //}
        // DONEORDERSALESCTY
        String isGift = "false";
        
        String internalCode = request.getParameter("internalCode");
        String shipBeforeDate = request.getParameter("shipBeforeDate");
        String shipAfterDate = request.getParameter("shipAfterDate");

        List<String> singleUsePayments = new ArrayList<String>();

        // get a request map of parameters
        Map<String, Object> params = UtilHttp.getParameterMap(request);

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
        }

        Map<String, Object> optResult = checkOutHelper.setCheckOutOptions(shippingMethod, shippingContactMechId, selectedPaymentMethods,
                singleUsePayments, billingAccountId, shippingInstructions,
                orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate);

        ServiceUtil.getMessages(request, optResult, null);
        if (ServiceUtil.isError(optResult)) {
            return "error";
        }

        return "success";
    }
    
    public static Map<String, Map<String, Object>> getSelectedPaymentMethodsSales(HttpServletRequest request) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        //Locale locale = UtilHttp.getLocale(request);
        Map<String, Map<String, Object>> selectedPaymentMethods = new HashMap<String, Map<String, Object>>();
        /*String[] paymentMethods = request.getParameterValues("checkOutPaymentId"); // EXT_OFFLINE, EXT_COD, EXT_WORLDPAY, EXT_PAYPAL
        if (paymentMethods == null || paymentMethods.length == 0 ){
        	paymentMethods = (String[])request.getAttribute("checkOutPaymentId");
        }*/
        // DONEORDERSALESCTY
        String[] paymentMethods = new String[]{"EXT_OFFLINE"};
        
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
                        errMsg = UtilProperties.getMessage(resource_error, "checkevents.invalid_amount_set_for_payment_method", (cart != null ? cart.getLocale() : Locale.getDefault()));
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
}
