package com.olbius.basepos.cart;



import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
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
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import com.olbius.basepos.order.PosOrderChangeHelper;

public class PosCheckoutHelper extends CheckOutHelper {
	public PosCheckoutHelper(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart) {
		super(dispatcher, delegator, cart);
		// TODO Auto-generated constructor stub
	}
	public Map<String, Object> createOrder(GenericValue userLogin) {
        return createOrder(userLogin, null, null, null, false, null, cart.getWebSiteId());
    }

    // Create order event - uses createOrder service for processing
    public Map<String, Object> createOrder(GenericValue userLogin, String distributorId, String affiliateId,
            List<GenericValue> trackingCodeOrders, boolean areOrderItemsExploded, String visitId, String webSiteId) {
        if (this.cart == null) {
            return null;
        }
        Locale locale = this.cart.getLocale();
        String orderId = this.cart.getOrderId();
        String supplierPartyId = (String) this.cart.getAttribute("supplierPartyId");
        String originOrderId = (String) this.cart.getAttribute("originOrderId");

        this.cart.clearAllItemStatus();
        
        BigDecimal grandTotal = this.cart.getGrandTotal();
        if(this.cart.isCartContainedReturn()){
        	grandTotal = this.cart.getGranTotalReturn();
        }else{
        	grandTotal = this.cart.getGrandTotalNoReturn();
        }
        // store the order - build the context
        Map<String, Object> context = this.cart.makeCartMap(this.dispatcher, areOrderItemsExploded);

        //get the TrackingCodeOrder List
        context.put("trackingCodeOrders", trackingCodeOrders);

        if (distributorId != null) context.put("distributorId", distributorId);
        if (affiliateId != null) context.put("affiliateId", affiliateId);

        context.put("orderId", orderId);
        context.put("supplierPartyId", supplierPartyId);
        context.put("grandTotal", grandTotal);
        context.put("userLogin", userLogin);
        context.put("visitId", visitId);
        if (UtilValidate.isEmpty(webSiteId)) {
            webSiteId = cart.getWebSiteId();
        }
        context.put("webSiteId", webSiteId);
        context.put("originOrderId", originOrderId);

       

        // store the order - invoke the service
        Map<String, Object> storeResult = null;

        try {
            storeResult = dispatcher.runSync("storeOrderPos", context);
            orderId = (String) storeResult.get("orderId");
            if (UtilValidate.isNotEmpty(orderId)) {
                this.cart.setOrderId(orderId);
                if (this.cart.getFirstAttemptOrderId() == null) {
                    this.cart.setFirstAttemptOrderId(orderId);
                }
            }
        } catch (GenericServiceException e) {
            String service = e.getMessage();
            Map<String, Object> messageMap = UtilMisc.<String, Object>toMap("service", service);
            String errMsg = UtilProperties.getMessage(resource, "SettingCanNotCreateOrder",locale);
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // check for error message(s)
        if (ServiceUtil.isError(storeResult)) {
            String errMsg = UtilProperties.getMessage(resource, "SettingCanNotCreateOrder", locale);
            List<String> resErrorMessages = new LinkedList<String>();
            resErrorMessages.add(errMsg);
            resErrorMessages.add(ServiceUtil.getErrorMessage(storeResult));
            return ServiceUtil.returnError(resErrorMessages);
        }
        
        // set the orderId for use by chained events
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("orderId", orderId);
        

        return result;
    }
    public Map<String, Object> processPayment(GenericValue productStore, GenericValue userLogin, boolean faceToFace) throws GeneralException {
        /*return CheckOutHelper.processPayment(this.cart.getOrderId(), this.cart.getGrandTotal(), this.cart.getCurrency(), productStore, userLogin, faceToFace, false, dispatcher, delegator);*/
    	if(cart.isCartContainedReturn()){
    		return PosCheckoutHelper.processPayment(this.cart.getOrderId(), this.cart.getGranTotalReturn(), this.cart.getCurrency(), productStore, userLogin, faceToFace, false, dispatcher, delegator);
    	}else{
    		if(cart.isWholeCartIsReturn()){
    			return PosCheckoutHelper.processPayment(this.cart.getOrderId(), this.cart.getGrandTotalNoReturn(), this.cart.getCurrency(), productStore, userLogin, faceToFace, false, dispatcher, delegator);	
    		}else{
    			return PosCheckoutHelper.processPayment(this.cart.getOrderId(), this.cart.getGrandTotalNoReturn(), this.cart.getCurrency(), productStore, userLogin, faceToFace, false, dispatcher, delegator);
    		}
    		
    	}
        
    }

    public Map<String, Object> processPayment(GenericValue productStore, GenericValue userLogin, boolean faceToFace, boolean manualHold) throws GeneralException {
        return PosCheckoutHelper.processPayment(this.cart.getOrderId(), this.cart.getGrandTotal(), this.cart.getCurrency(), productStore, userLogin, faceToFace, manualHold, dispatcher, delegator);
    }

    public static Map<String, Object> processPayment(String orderId, BigDecimal orderTotal, String currencyUomId, GenericValue productStore, GenericValue userLogin, boolean faceToFace, boolean manualHold, LocalDispatcher dispatcher, Delegator delegator) throws GeneralException {
        List<GenericValue> allPaymentPreferences = null;
        try {
            allPaymentPreferences = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId), null, false);
        } catch (GenericEntityException e) {
            throw new GeneralException("Problems getting payment preferences", e);
        }
        //TODO set payment by credit card type is COMPANY_CHECK
        List<EntityExpr> paymentListCond = UtilMisc.toList(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "CASH"),
                                       EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "COMPANY_CHECK"));
        List<GenericValue> paymentList = EntityUtil.filterByOr(allPaymentPreferences, paymentListCond);
        if (UtilValidate.isNotEmpty(paymentList) &&
                UtilValidate.isNotEmpty(allPaymentPreferences) &&
                paymentList.size() == allPaymentPreferences.size()) {
                boolean ok = PosOrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, manualHold);
                if (!ok) {
                    throw new GeneralException("Problem with order change; see above error");
                }
        }

        // check to see if we should auto-invoice/bill
        if (faceToFace) {
            if (Debug.verboseOn()) Debug.logVerbose("Face-To-Face Sale - " + orderId, module);
            PosCheckoutHelper.adjustFaceToFacePayment(orderId, orderTotal, allPaymentPreferences, userLogin, delegator);
            boolean ok = PosOrderChangeHelper.completeOrder(dispatcher, userLogin, orderId);
            if (Debug.verboseOn()) Debug.logVerbose("Complete Order Result - " + ok, module);
            if (!ok) {
                throw new GeneralException("Problem with order change; see error logs");
            }
        }
        return ServiceUtil.returnSuccess();
    }
    public static void adjustFaceToFacePayment(String orderId, BigDecimal cartTotal, List<GenericValue> allPaymentPrefs, GenericValue userLogin, Delegator delegator) throws GeneralException {
        BigDecimal prefTotal = BigDecimal.ZERO;
        if (allPaymentPrefs != null) {
            for (GenericValue pref : allPaymentPrefs) {
                BigDecimal maxAmount = pref.getBigDecimal("maxAmount");
                if (maxAmount == null) maxAmount = BigDecimal.ZERO;
                prefTotal = prefTotal.add(maxAmount);
            }
        }
        
        if (prefTotal.compareTo(cartTotal) > 0) {
            BigDecimal change = prefTotal.subtract(cartTotal).negate();
            GenericValue newPref = delegator.makeValue("OrderPaymentPreference");
            newPref.set("orderId", orderId);
            newPref.set("paymentMethodTypeId", "CASH");
            newPref.set("statusId", "PAYMENT_RECEIVED");
            newPref.set("maxAmount", change);
            newPref.set("createdDate", UtilDateTime.nowTimestamp());
            if (userLogin != null) {
                newPref.set("createdByUserLogin", userLogin.getString("userLoginId"));
            }
            delegator.createSetNextSeqId(newPref);
        }
        if(prefTotal.compareTo(cartTotal) <0 ){
        	BigDecimal change = cartTotal.subtract(prefTotal);
        	for (GenericValue pref : allPaymentPrefs) {
        		if(pref.getString("paymentMethodTypeId").equals("CASH")){
        			 BigDecimal maxAmount = pref.getBigDecimal("maxAmount");
        			 maxAmount = maxAmount.add(change);
        			 pref.set("maxAmount", maxAmount);
        			 pref.store();
        			 break;
                    		
        		}
               
            }
        }
    }
}
