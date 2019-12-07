package com.olbius.basesales.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.thirdparty.paypal.ExpressCheckoutEvents;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.NotificationWorker;

public class CheckOutHelper extends org.ofbiz.order.shoppingcart.CheckOutHelper{

	public CheckOutHelper(LocalDispatcher dispatcher, Delegator delegator,
			ShoppingCart cart) {
		super(dispatcher, delegator, cart);
		// TODO Auto-generated constructor stub
	}
	
	public Map<String, Object> processPayment(GenericValue productStore, GenericValue userLogin, boolean faceToFace, boolean manualHold) throws GeneralException {
        return CheckOutHelper.processPayment(this.cart.getOrderId(), this.cart.getGrandTotal(), this.cart.getCurrency(), productStore, userLogin, faceToFace, manualHold, dispatcher, delegator, this.cart.getLocale());
    }
	
	public static Map<String, Object> processPayment(String orderId, BigDecimal orderTotal, String currencyUomId, GenericValue productStore, GenericValue userLogin, boolean faceToFace, boolean manualHold, LocalDispatcher dispatcher, Delegator delegator, Locale locale) throws GeneralException {
        // Get some payment related strings
        String DECLINE_MESSAGE = productStore.getString("authDeclinedMessage");
        String ERROR_MESSAGE = productStore.getString("authErrorMessage");
        String RETRY_ON_ERROR = productStore.getString("retryFailedAuths");
        if (RETRY_ON_ERROR == null) {
            RETRY_ON_ERROR = "Y";
        }

        List<GenericValue> allPaymentPreferences = null;
        try {
            allPaymentPreferences = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId), null, false);
        } catch (GenericEntityException e) {
            throw new GeneralException("Problems getting payment preferences", e);
        }

        // filter out cancelled preferences
        List<EntityExpr> canExpr = UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
        allPaymentPreferences = EntityUtil.filterByAnd(allPaymentPreferences, canExpr);

        // check for online payment methods or in-hand payment types with verbal or external refs
        List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("manualRefNum", EntityOperator.NOT_EQUAL, null));
        List<GenericValue> manualRefPaymentPrefs = EntityUtil.filterByAnd(allPaymentPreferences, exprs);
        if (UtilValidate.isNotEmpty(manualRefPaymentPrefs)) {
            for (GenericValue opp : manualRefPaymentPrefs) {
                Map<String, Object> authCtx = new HashMap<String, Object>();
                authCtx.put("orderPaymentPreference", opp);
                if (opp.get("paymentMethodId") == null) {
                    authCtx.put("serviceTypeEnum", "PRDS_PAY_EXTERNAL");
                }
                authCtx.put("processAmount", opp.getBigDecimal("maxAmount"));
                authCtx.put("authRefNum", opp.getString("manualRefNum"));
                authCtx.put("authResult", Boolean.TRUE);
                authCtx.put("userLogin", userLogin);
                authCtx.put("currencyUomId", currencyUomId);

                Map<String, Object> authResp = dispatcher.runSync("processAuthResult", authCtx);
                if (authResp != null && ServiceUtil.isError(authResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(authResp));
                }

                // approve the order
                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);

                if ("Y".equalsIgnoreCase(productStore.getString("manualAuthIsCapture"))) {
                    Map<String, Object> captCtx = new HashMap<String, Object>();
                    captCtx.put("orderPaymentPreference", opp);
                    if (opp.get("paymentMethodId") == null) {
                        captCtx.put("serviceTypeEnum", "PRDS_PAY_EXTERNAL");
                    }
                    captCtx.put("payToPartyId", productStore.get("payToPartyId"));
                    captCtx.put("captureResult", Boolean.TRUE);
                    captCtx.put("captureAmount", opp.getBigDecimal("maxAmount"));
                    captCtx.put("captureRefNum", opp.getString("manualRefNum"));
                    captCtx.put("userLogin", userLogin);
                    captCtx.put("currencyUomId", currencyUomId);

                    Map<String, Object> capResp = dispatcher.runSync("processCaptureResult", captCtx);
                    if (capResp != null && ServiceUtil.isError(capResp)) {
                        throw new GeneralException(ServiceUtil.getErrorMessage(capResp));
                    }
                }
            }
        }

        // check for a paypal express checkout needing completion
        List<EntityExpr> payPalExprs = UtilMisc.toList(
                EntityCondition.makeCondition("paymentMethodId", EntityOperator.NOT_EQUAL, null),
                EntityCondition.makeCondition("paymentMethodTypeId", "EXT_PAYPAL")
           );
        List<GenericValue> payPalPaymentPrefs = EntityUtil.filterByAnd(allPaymentPreferences, payPalExprs);
        if (UtilValidate.isNotEmpty(payPalPaymentPrefs)) {
            GenericValue payPalPaymentPref = EntityUtil.getFirst(payPalPaymentPrefs);
            ExpressCheckoutEvents.doExpressCheckout(productStore.getString("productStoreId"), orderId, payPalPaymentPref, userLogin, delegator, dispatcher);
        }

        // check for online payment methods needing authorization
        Map<String, Object> paymentFields = UtilMisc.<String, Object>toMap("statusId", "PAYMENT_NOT_AUTH");
        List<GenericValue> onlinePaymentPrefs = EntityUtil.filterByAnd(allPaymentPreferences, paymentFields);

        // Check the payment preferences; if we have ANY w/ status PAYMENT_NOT_AUTH invoke payment service.
        // Invoke payment processing.
        if (UtilValidate.isNotEmpty(onlinePaymentPrefs)) {
            boolean autoApproveOrder = UtilValidate.isEmpty(productStore.get("autoApproveOrder")) || "Y".equalsIgnoreCase(productStore.getString("autoApproveOrder"));
            if (orderTotal.compareTo(BigDecimal.ZERO) == 0 && autoApproveOrder) {
                // if there is nothing to authorize; don't bother
                boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);
                if (!ok) {
                    throw new GeneralException("Problem with order change; see above error");
                }
            }

            // now there should be something to authorize; go ahead
            Map<String, Object> paymentResult = null;
            try {
                // invoke the payment gateway service.
                paymentResult = dispatcher.runSync("authOrderPayments",
                        UtilMisc.<String, Object>toMap("orderId", orderId, "userLogin", userLogin), 180, false);
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
                throw new GeneralException("Error in authOrderPayments service: " + e.toString(), e.getNested());
            }
            if (Debug.verboseOn()) Debug.logVerbose("Finished w/ Payment Service", module);

            if (paymentResult != null && ServiceUtil.isError(paymentResult)) {
                throw new GeneralException(ServiceUtil.getErrorMessage(paymentResult));
            }


            if (paymentResult != null && paymentResult.containsKey("processResult")) {
                // grab the customer messages -- only passed back in the case of an error or failure
                List<String> messages = UtilGenerics.checkList(paymentResult.get("authResultMsgs"));

                String authResp = (String) paymentResult.get("processResult");

                if (authResp.equals("FAILED")) {
                    // order was NOT approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was NOT a success!", module);

                    boolean ok = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }
                    if (UtilValidate.isEmpty(messages)) {
                        return ServiceUtil.returnError(DECLINE_MESSAGE);
                    } else {
                        return ServiceUtil.returnError(messages);
                    }
                } else if (authResp.equals("APPROVED")) {
                    // order WAS approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was a success!", module);

                    // set the order and item status to approved
                    if (autoApproveOrder) {
                        List<GenericValue> productStorePaymentSettingList = delegator.findByAnd("ProductStorePaymentSetting", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"), "paymentMethodTypeId", "CREDIT_CARD", "paymentService", "cyberSourceCCAuth"), null, false);
                        if (productStorePaymentSettingList.size() > 0) {
                            String decision = (String) paymentResult.get("authCode");
                            if (UtilValidate.isNotEmpty(decision)) {
                                if ("ACCEPT".equalsIgnoreCase(decision)) {
                                    boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);
                                    if (!ok) {
                                        throw new GeneralException("Problem with order change; see above error");
                                    }
                                }
                            } else {
                                boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);
                                if (!ok) {
                                    throw new GeneralException("Problem with order change; see above error");
                                }
                            }
                        } else {
                            boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);
                            if (!ok) {
                                throw new GeneralException("Problem with order change; see above error");
                            }
                        }
                    }
                } else if (authResp.equals("ERROR")) {
                    // service failed
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);
                    if (!faceToFace && "Y".equalsIgnoreCase(RETRY_ON_ERROR)) {
                        // never do this for a face to face purchase regardless of store setting
                        return ServiceUtil.returnSuccess(ERROR_MESSAGE);
                    } else {
                        boolean ok = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
                        if (!ok) {
                            throw new GeneralException("Problem with order change; see above error");
                        }
                        if (UtilValidate.isEmpty(messages)) {
                            return ServiceUtil.returnError(ERROR_MESSAGE);
                        } else {
                            return ServiceUtil.returnError(messages);
                        }
                    }
                } else {
                    // should never happen
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderPleaseContactCustomerServicePaymentReturnCodeUnknown", Locale.getDefault()));
                }
            } else {
                // result returned null == service failed
                if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);
                if (!faceToFace && "Y".equalsIgnoreCase(RETRY_ON_ERROR)) {
                    // never do this for a face to face purchase regardless of store setting
                    return ServiceUtil.returnSuccess(ERROR_MESSAGE);
                } else {
                    boolean ok = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }
                    return ServiceUtil.returnError(ERROR_MESSAGE);
                }
            }
        } else {
            // Get the paymentMethodTypeIds - this will need to change when ecom supports multiple payments
            List<EntityExpr> cashCodPcBaExpr = UtilMisc.toList(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "CASH"),
                                           EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_COD"),
                                           EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "PERSONAL_CHECK"),
                                           EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_BILLACT"));
            List<GenericValue> cashCodPcBaPaymentPreferences = EntityUtil.filterByOr(allPaymentPreferences, cashCodPcBaExpr);

            if (UtilValidate.isNotEmpty(cashCodPcBaPaymentPreferences) &&
                    UtilValidate.isNotEmpty(allPaymentPreferences) &&
                    cashCodPcBaPaymentPreferences.size() == allPaymentPreferences.size()) {

                //if there are Check type, approve the order only if it is face to face
                List<GenericValue> checkPreferences = EntityUtil.filterByAnd(cashCodPcBaPaymentPreferences, UtilMisc.toMap("paymentMethodTypeId", "PERSONAL_CHECK"));
                if (UtilValidate.isNotEmpty(checkPreferences)) {
                    if (faceToFace) {
                        boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);
                        if (!ok) {
                            throw new GeneralException("Problem with order change; see above error");
                        }
                    }
                // approve this as long as there are only CASH, COD and Billing Account types
                } else {
                    boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }
                }

            } else {
                // There is nothing to do, we just treat this as a success
            }
        }

        // check to see if we should auto-invoice/bill
        if (faceToFace) {
            if (Debug.verboseOn()) Debug.logVerbose("Face-To-Face Sale - " + orderId, module);
            CheckOutHelper.adjustFaceToFacePayment(orderId, orderTotal, allPaymentPreferences, userLogin, delegator);
            boolean ok = OrderChangeHelper.completeOrder(dispatcher, userLogin, orderId);
            if (Debug.verboseOn()) Debug.logVerbose("Complete Order Result - " + ok, module);
            if (!ok) {
                throw new GeneralException("Problem with order change; see error logs");
            }
        }
        
        return ServiceUtil.returnSuccess();
    }
}
