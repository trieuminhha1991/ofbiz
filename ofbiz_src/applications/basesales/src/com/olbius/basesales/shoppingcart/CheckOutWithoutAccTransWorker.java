package com.olbius.basesales.shoppingcart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

public class CheckOutWithoutAccTransWorker {
	public static final String module = CheckOutWithoutAccTransWorker.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    
    /**
     * Use for quickcheckout submit.  It calculates the tax before setting the payment options.
     * Shipment option should already be set by the quickcheckout form.
     */
    public static Map<String, Object> setQuickCheckOutOptions(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, 
    		String billingAccountId, String billingAccountAmount, String shippingMethod, String shippingContactMechId, 
    		String taxAuthPartyGeoIds, String partyTaxId, String isExempt, String shippingInstructions, String orderAdditionalEmails, 
    		String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate, 
    		List<String> paymentMethods, String issuerId, Map<String, Object> securityCodeMap, Map<String, Object> amountMap, 
    		String addGiftCard, String giftCardNumber, String giftCardPin, String giftCardAmount, String partyId, String singleUseGiftCard, Boolean runPromo, Boolean runCalcTax) {
    	
    	if (runCalcTax) {
    		Map<String, Object> result = calcTax(delegator, dispatcher, cart);
            if (ServiceUtil.isError(result)) return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));
    	}
        
        return setCheckOutOptions(delegator, dispatcher, cart, billingAccountId, billingAccountAmount, shippingMethod, shippingContactMechId, 
        		taxAuthPartyGeoIds, partyTaxId, isExempt, shippingInstructions, orderAdditionalEmails, 
        		maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate, 
        		paymentMethods, issuerId, securityCodeMap, amountMap, 
        		addGiftCard, giftCardNumber, giftCardPin, giftCardAmount, partyId, singleUseGiftCard, runPromo, runCalcTax);
    }
    
    // Event wrapper for the tax calc.
    public static Map<String, Object> calcTax(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart) {
        try {
        	// Invoke the taxCalc
        	CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

            //Calculate and add the tax adjustments
            checkOutHelper.calcAndAddTax();
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }
    
    // this servlet is used by quick checkout
    public static Map<String, Object> setCheckOutOptions(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, 
    		String billingAccountId, String billingAccountAmount, String shippingMethod, String shippingContactMechId, 
    		String taxAuthPartyGeoIds, String partyTaxId, String isExempt, String shippingInstructions, String orderAdditionalEmails, 
    		String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate, 
    		List<String> paymentMethods, String issuerId, Map<String, Object> securityCodeMap, Map<String, Object> amountMap, 
    		String addGiftCard, String giftCardNumber, String giftCardPin, String giftCardAmount, String partyId, String singleUseGiftCard, Boolean runPromo, Boolean runCalcTax) {
    	Locale locale = cart != null ? cart.getLocale() : Locale.getDefault();
    	List<String> errorMessageList = FastList.newInstance();
    	
        // Set the payment options
        Map<String, Map<String, Object>> selectedPaymentMethods = CheckOutWorker.getSelectedPaymentMethods(cart, errorMessageList, paymentMethods, issuerId, securityCodeMap, amountMap);

        CheckOutWithoutAccTransHelper checkOutHelperWithout = new CheckOutWithoutAccTransHelper(dispatcher, delegator, cart);

        // get the billing account and amount
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            BigDecimal billingAccountAmt = null;
            billingAccountAmt = CheckOutWorker.determineBillingAccountAmount(billingAccountId, billingAccountAmount, dispatcher);
            if (billingAccountAmt == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderInvalidAmountSetForBillingAccount", UtilMisc.toMap("billingAccountId", billingAccountId), locale));
            }
            selectedPaymentMethods.put("EXT_BILLACT", UtilMisc.<String, Object>toMap("amount", billingAccountAmt, "securityCode", null));
        }

        if (selectedPaymentMethods == null) {
            return ServiceUtil.returnError("Not select payment method yet");
        }

        List<String> singleUsePayments = new ArrayList<String>();

        // get a request map of parameters
        //Map<String, Object> params = UtilHttp.getParameterMap(request);

        // if taxAuthPartyGeoIds is not empty drop that into the database
        if (UtilValidate.isNotEmpty(taxAuthPartyGeoIds)) {
            try {
                Map<String, Object> createCustomerTaxAuthInfoResult = dispatcher.runSync("createCustomerTaxAuthInfo",
                        UtilMisc.toMap("partyId", cart.getPartyId(), "taxAuthPartyGeoIds", taxAuthPartyGeoIds, "partyTaxId", partyTaxId, "isExempt", isExempt));
                if (ServiceUtil.isError(createCustomerTaxAuthInfoResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createCustomerTaxAuthInfoResult));
                }
            } catch (GenericServiceException e) {
                return ServiceUtil.returnError("Error setting customer tax info: " + e.toString());
            }
        }

        // check for gift card not on file
        /* DONENOACCTRANS: deleted
        Map<String, Object> params = FastMap.newInstance();
        params.put("addGiftCard", addGiftCard);
        params.put("giftCardNumber", giftCardNumber);
        params.put("giftCardPin", giftCardPin);
        params.put("giftCardAmount", giftCardAmount);
        params.put("partyId", partyId);
        Map<String, Object> gcResult = checkOutHelper.checkGiftCard(params, selectedPaymentMethods);
        if (ServiceUtil.isError(gcResult)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(gcResult));
        }

        String gcPaymentMethodId = (String) gcResult.get("paymentMethodId");
        BigDecimal gcAmount = (BigDecimal) gcResult.get("amount");
        if (gcPaymentMethodId != null) {
            selectedPaymentMethods.put(gcPaymentMethodId, UtilMisc.<String, Object>toMap("amount", gcAmount, "securityCode", null));
            if ("Y".equalsIgnoreCase(singleUseGiftCard)) {
                singleUsePayments.add(gcPaymentMethodId);
            }
        }*/

        /* DONENOACCTRANS: deleted
         * shippingMethod
         * orderAdditionalEmails
         * isGift
         * params
         * gcResult
         * gcAmount
         */
        Map<String, Object> optResult = checkOutHelperWithout.setCheckOutOptions(null, shippingContactMechId, selectedPaymentMethods,
                singleUsePayments, null, shippingInstructions, maySplit, giftMessage, internalCode, shipBeforeDate, shipAfterDate, runPromo, runCalcTax);
        /* DONENOACCTRANS: deleted
        Map<String, Object> optResult = checkOutHelper.setCheckOutOptions(shippingMethod, shippingContactMechId, selectedPaymentMethods,
                singleUsePayments, billingAccountId, shippingInstructions,
                orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate);
         */

        if (ServiceUtil.isError(optResult)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(optResult));
        }

        return ServiceUtil.returnSuccess();
    }
	
}
