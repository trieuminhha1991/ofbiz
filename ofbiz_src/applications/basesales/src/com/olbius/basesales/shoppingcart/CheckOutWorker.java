package com.olbius.basesales.shoppingcart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
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
import javolution.util.FastMap;

public class CheckOutWorker {
	public static final String module = CheckOutWorker.class.getName();
	public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
	
	public static Map<String, Object> setPartialCheckOutOptions(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, 
    		String billingAccountId, String billingAccountAmount, String shippingMethod, String shippingContactMechId, 
    		String taxAuthPartyGeoIds, String partyTaxId, String isExempt, String shippingInstructions, String orderAdditionalEmails, 
    		String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate, 
    		List<String> paymentMethods, String issuerId, Map<String, Object> securityCodeMap, Map<String, Object> amountMap, 
    		String addGiftCard, String giftCardNumber, String giftCardPin, String giftCardAmount, String partyId, String singleUseGiftCard, Boolean runPromo, Boolean runCalcTax) {
        // FIXME response need to be checked ?
        // String resp = setCheckOutOptions(request, response);
        setCheckOutOptions(delegator, dispatcher, cart, billingAccountId, billingAccountAmount, shippingMethod, shippingContactMechId, 
        		taxAuthPartyGeoIds, partyTaxId, isExempt, shippingInstructions, orderAdditionalEmails, maySplit, 
        		giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate, paymentMethods, issuerId, securityCodeMap, amountMap, 
        		addGiftCard, giftCardNumber, giftCardPin, giftCardAmount, partyId, singleUseGiftCard, runPromo, runCalcTax);
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
        Map<String, Map<String, Object>> selectedPaymentMethods = getSelectedPaymentMethods(cart, errorMessageList, paymentMethods, issuerId, securityCodeMap, amountMap);

        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

        // get the billing account and amount
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            BigDecimal billingAccountAmt = null;
            billingAccountAmt = determineBillingAccountAmount(billingAccountId, billingAccountAmount, dispatcher);
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
        }

        Map<String, Object> optResult = checkOutHelper.setCheckOutOptions(shippingMethod, shippingContactMechId, selectedPaymentMethods,
                singleUsePayments, billingAccountId, shippingInstructions,
                orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate, runPromo, runCalcTax);

        if (ServiceUtil.isError(optResult)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(optResult));
        }

        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Map<String, Object>> getSelectedPaymentMethods(ShoppingCart cart, List<String> errorMessageList, 
    		List<String> paymentMethods, String issuerId, Map<String, Object> securityCodeMap, Map<String, Object> amountMap) {
        Locale locale = cart != null ? cart.getLocale() : Locale.getDefault();
    	Map<String, Map<String, Object>> selectedPaymentMethods = new HashMap<String, Map<String, Object>>();
        //if (UtilValidate.isNotEmpty(request.getParameter("issuerId"))) {
        //    request.setAttribute("issuerId", request.getParameter("issuerId"));
        //}
    	if (securityCodeMap == null) securityCodeMap = FastMap.newInstance();
    	if (amountMap == null) amountMap = FastMap.newInstance();
        if (paymentMethods != null) {
            for (int i = 0; i < paymentMethods.size(); i++) {
                Map<String, Object> paymentMethodInfo = FastMap.newInstance();

                // String securityCode = request.getParameter("securityCode_" + paymentMethods[i]);
                String securityCode = (String) securityCodeMap.get("securityCode_" + paymentMethods.get(i));
                if (UtilValidate.isNotEmpty(securityCode)) {
                    paymentMethodInfo.put("securityCode", securityCode);
                }
                // String amountStr = request.getParameter("amount_" + paymentMethods[i]);
                String amountStr = (String) amountMap.get("amount_" + paymentMethods.get(i));
                BigDecimal amount = null;
                if (UtilValidate.isNotEmpty(amountStr) && !"REMAINING".equals(amountStr)) {
                    try {
                        amount = new BigDecimal(amountStr);
                    } catch (NumberFormatException e) {
                        Debug.logError(e, module);
                        errorMessageList.add(UtilProperties.getMessage(resource_error, "checkevents.invalid_amount_set_for_payment_method", locale));
                        return null;
                    }
                }
                paymentMethodInfo.put("amount", amount);
                selectedPaymentMethods.put(paymentMethods.get(i), paymentMethodInfo);
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
}
