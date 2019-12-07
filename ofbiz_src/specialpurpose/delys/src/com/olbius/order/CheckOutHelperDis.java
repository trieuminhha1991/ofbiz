package com.olbius.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.order.finaccount.FinAccountHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CheckOutHelperDis {
	public static final String module = CheckOutHelperDis.class.getName();
    public static final String resource = "DelysAdminUiLabels";
    public static final String resource_error = "DelysAdminErrorUiLabels";

    public static final int scale = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

    protected LocalDispatcher dispatcher = null;
    protected Delegator delegator = null;
    protected ShoppingCart cart = null;

    public CheckOutHelperDis(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart) {
        this.delegator = delegator;
        this.dispatcher = dispatcher;
        this.cart = cart;
    }
    
    public Map<String, Object> checkGiftCard(Map<String, Object> params, Map<String, Map<String, Object>> selectedPaymentMethods) {
        List<String> errorMessages = new ArrayList<String>();
        Map<String, Object> errorMaps = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        String errMsg = null;
        // handle gift card payment
        if (params.get("addGiftCard") != null) {
            String gcNum = (String) params.get("giftCardNumber");
            String gcPin = (String) params.get("giftCardPin");
            String gcAmt = (String) params.get("giftCardAmount");
            BigDecimal gcAmount = BigDecimal.ONE.negate();

            boolean gcFieldsOkay = true;
            if (UtilValidate.isEmpty(gcNum)) {
                errMsg = UtilProperties.getMessage(resource_error,"checkhelper.enter_gift_card_number", (cart != null ? cart.getLocale() : Locale.getDefault()));
                errorMessages.add(errMsg);
                gcFieldsOkay = false;
            }
            if (cart.isPinRequiredForGC(delegator)) {
                //  if a PIN is required, make sure the PIN is valid
                if (UtilValidate.isEmpty(gcPin)) {
                    errMsg = UtilProperties.getMessage(resource_error,"checkhelper.enter_gift_card_pin_number", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                    gcFieldsOkay = false;
                }
            }
            // See if we should validate gift card code against FinAccount's accountCode
            if (cart.isValidateGCFinAccount(delegator)) {
                try {
                    // No PIN required - validate gift card number against account code
                    if (!cart.isPinRequiredForGC(delegator)) {
                        GenericValue finAccount = FinAccountHelper.getFinAccountFromCode(gcNum, delegator);
                        if (finAccount == null) {
                            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.gift_card_does_not_exist", (cart != null ? cart.getLocale() : Locale.getDefault()));
                            errorMessages.add(errMsg);
                            gcFieldsOkay = false;
                        } else if ((finAccount.getBigDecimal("availableBalance") == null) ||
                                !((finAccount.getBigDecimal("availableBalance")).compareTo(FinAccountHelper.ZERO) > 0)) {
                            // if account's available balance (including authorizations) is not greater than zero, then return an error
                            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.gift_card_has_no_value", (cart != null ? cart.getLocale() : Locale.getDefault()));
                            errorMessages.add(errMsg);
                            gcFieldsOkay = false;
                        }
                    }
                    // TODO: else case when pin is required - we should validate gcNum and gcPin
                } catch (GenericEntityException ex) {
                    errorMessages.add(ex.getMessage());
                    gcFieldsOkay = false;
                }
            }

            if (UtilValidate.isNotEmpty(selectedPaymentMethods)) {
                if (UtilValidate.isEmpty(gcAmt)) {
                    errMsg = UtilProperties.getMessage(resource_error,"checkhelper.enter_amount_to_place_on_gift_card", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                    gcFieldsOkay = false;
                }
            }
            if (UtilValidate.isNotEmpty(gcAmt)) {
                try {
                    gcAmount = new BigDecimal(gcAmt);
                } catch (NumberFormatException e) {
                    Debug.logError(e, module);
                    errMsg = UtilProperties.getMessage(resource_error,"checkhelper.invalid_amount_for_gift_card", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                    gcFieldsOkay = false;
                }
            }

            if (gcFieldsOkay) {
                // store the gift card
                Map<String, Object> gcCtx = new HashMap<String, Object>();
                gcCtx.put("partyId", params.get("partyId"));
                gcCtx.put("cardNumber", gcNum);
                if (cart.isPinRequiredForGC(delegator)) {
                    gcCtx.put("pinNumber", gcPin);
                }
                gcCtx.put("userLogin", cart.getUserLogin());
                Map<String, Object> gcResult = null;
                try {
                    gcResult = dispatcher.runSync("createGiftCard", gcCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    errorMessages.add(e.getMessage());
                }
                if (gcResult != null) {
                    ServiceUtil.addErrors(errorMessages, errorMaps, gcResult);

                    if (errorMessages.size() == 0 && errorMaps.size() == 0) {
                        // set the GC payment method
                        BigDecimal giftCardAmount = null;
                        if (gcAmount.compareTo(BigDecimal.ZERO) > 0) {
                            giftCardAmount = gcAmount;
                        }
                        String gcPaymentMethodId = (String) gcResult.get("paymentMethodId");
                        result = ServiceUtil.returnSuccess();
                        result.put("paymentMethodId", gcPaymentMethodId);
                        result.put("amount", giftCardAmount);
                    }
                } else {
                    errMsg = UtilProperties.getMessage(resource_error,"checkhelper.problem_with_gift_card_information", (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                }
            }
        } else {
            result = ServiceUtil.returnSuccess();
        }

        // see whether we need to return an error or not
        if (errorMessages.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessages);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        if (errorMaps.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_MAP, errorMaps);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }

        return result;
    }
    
    public Map<String, Object> setCheckOutOptionsAjax(String shippingMethod, String shippingContactMechId, Map<String, Map<String, Object>> selectedPaymentMethods,
            List<String> singleUsePayments, String billingAccountId, String shippingInstructions,
            String orderAdditionalEmails, String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate) {
        List<String> errorMessages = new ArrayList<String>();
        Map<String, Object> result = null;
        String errMsg = null;

        if (UtilValidate.isNotEmpty(this.cart)) {
            // set the general shipping options and method
        	//DONENOACCTRANS: deleted
            /*errorMessages.addAll(setCheckOutShippingOptionsInternal(shippingMethod, shippingInstructions,
                    orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate));*/

            // set the shipping address
            errorMessages.addAll(setCheckOutShippingAddressInternal(shippingContactMechId));

            // Recalc shipping costs before setting payment
            Map<String, Object> shipEstimateMap = ShippingEvents.getShipGroupEstimate(dispatcher, delegator, cart, 0);
            BigDecimal shippingTotal = (BigDecimal) shipEstimateMap.get("shippingTotal");
            if (shippingTotal == null) {
                shippingTotal = BigDecimal.ZERO;
            }
            cart.setItemShipGroupEstimate(shippingTotal, 0);
            ProductPromoWorker.doPromotions(cart, dispatcher);

            //Recalc tax before setting payment
            try {
                this.calcAndAddTax();
            } catch (GeneralException e) {
                Debug.logError(e, module);
            }
            // set the payment method(s) option
            //DONENOACCTRANS: deleted
            //errorMessages.addAll(setCheckOutPaymentInternal(selectedPaymentMethods, singleUsePayments, billingAccountId));

        } else {
            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.no_items_in_cart", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        if (errorMessages.size() == 1) {
            result = ServiceUtil.returnError(errorMessages.get(0).toString());
        } else if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        } else {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }
    
    /**
     * Custom to minimize, no shipping method, no payment method, no gift option, no send email.
     * Version native: orderAdditionalEmails (String), isGift (String)
     * @param shippingMethod
     * @param shippingContactMechId
     * @param selectedPaymentMethods
     * @param singleUsePayments
     * @param billingAccountId
     * @param shippingInstructions
     * @param maySplit
     * @param giftMessage
     * @param internalCode
     * @param shipBeforeDate
     * @param shipAfterDate
     * @return
     */
    public Map<String, Object> setCheckOutOptions(String shippingMethod, String shippingContactMechId, Map<String, Map<String, Object>> selectedPaymentMethods,
            List<String> singleUsePayments, String billingAccountId, String shippingInstructions, 
            String maySplit, String giftMessage, String internalCode, String shipBeforeDate, String shipAfterDate) {
        List<String> errorMessages = new ArrayList<String>();
        Map<String, Object> result = null;
        String errMsg = null;

        if (UtilValidate.isNotEmpty(this.cart)) {
            // set the general shipping options and method
        	//DONENOACCTRANS: deleted
            /*errorMessages.addAll(setCheckOutShippingOptionsInternal(shippingMethod, shippingInstructions,
                    orderAdditionalEmails, maySplit, giftMessage, isGift, internalCode, shipBeforeDate, shipAfterDate));*/
        	// set the shipping instructions
        	//DONENOACCTRANS: add, get from setCheckOutShippingOptionsInternal in CheckOutHelper.java
            this.cart.setAllShippingInstructions(shippingInstructions);
            
            // set the shipping address
            errorMessages.addAll(setCheckOutShippingAddressInternal(shippingContactMechId));

            // Recalc shipping costs before setting payment
            Map<String, Object> shipEstimateMap = ShippingEvents.getShipGroupEstimate(dispatcher, delegator, cart, 0);
            BigDecimal shippingTotal = (BigDecimal) shipEstimateMap.get("shippingTotal");
            if (shippingTotal == null) {
                shippingTotal = BigDecimal.ZERO;
            }
            cart.setItemShipGroupEstimate(shippingTotal, 0);
            ProductPromoWorker.doPromotions(cart, dispatcher);

            //Recalc tax before setting payment
            try {
                this.calcAndAddTax();
            } catch (GeneralException e) {
                Debug.logError(e, module);
            }
            // set the payment method(s) option
            //DONENOACCTRANS: deleted
            //errorMessages.addAll(setCheckOutPaymentInternal(selectedPaymentMethods, singleUsePayments, billingAccountId));

        } else {
            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.no_items_in_cart", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        if (errorMessages.size() == 1) {
            result = ServiceUtil.returnError(errorMessages.get(0).toString());
        } else if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        } else {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }
    
    //DONENOACCTRANS: deleted
    /*private List<String> setCheckOutShippingOptionsInternal(String shippingMethod, String shippingInstructions, String orderAdditionalEmails,
            String maySplit, String giftMessage, String isGift, String internalCode, String shipBeforeDate, String shipAfterDate) {
        List<String> errorMessages = new ArrayList<String>();
        String errMsg = null;

        // set the general shipping options
        if (UtilValidate.isNotEmpty(shippingMethod)) {
            int delimiterPos = shippingMethod.indexOf('@');
            String shipmentMethodTypeId = null;
            String carrierPartyId = null;

            if (delimiterPos > 0) {
                shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                carrierPartyId = shippingMethod.substring(delimiterPos + 1);
            }

            this.cart.setAllShipmentMethodTypeId(shipmentMethodTypeId);
            this.cart.setAllCarrierPartyId(carrierPartyId);
        } else if (cart.shippingApplies()) {
            // only return an error if shipping is required for this purchase
            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.select_shipping_method", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        // set the shipping instructions
        this.cart.setAllShippingInstructions(shippingInstructions);

        if (UtilValidate.isNotEmpty(maySplit)) {
            cart.setAllMaySplit(Boolean.valueOf(maySplit));
        } else {
            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.select_splitting_preference", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        // set the gift message
        this.cart.setAllGiftMessage(giftMessage);

        if (UtilValidate.isNotEmpty(isGift)) {
            cart.setAllIsGift(Boolean.valueOf(isGift));
        } else {
            errMsg = UtilProperties.getMessage(resource_error, "checkhelper.specify_if_order_is_gift", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        // interal code
        this.cart.setInternalCode(internalCode);

        if (UtilValidate.isNotEmpty(shipBeforeDate)) {
            if (UtilValidate.isDate(shipBeforeDate)) {
                cart.setShipBeforeDate(UtilDateTime.toTimestamp(shipBeforeDate));
            } else {
                errMsg = UtilProperties.getMessage(resource_error, "checkhelper.specify_if_shipBeforeDate_is_date", (cart != null ? cart.getLocale() : Locale.getDefault()));
                errorMessages.add(errMsg);
            }
        }

        if (UtilValidate.isNotEmpty(shipAfterDate)) {
            if (UtilValidate.isDate(shipAfterDate)) {
                cart.setShipAfterDate(UtilDateTime.toTimestamp(shipAfterDate));
            } else {
                errMsg = UtilProperties.getMessage(resource_error, "checkhelper.specify_if_shipAfterDate_is_date", (cart != null ? cart.getLocale() : Locale.getDefault()));
                errorMessages.add(errMsg);
            }
        }

        // set any additional notification emails
        this.cart.setOrderAdditionalEmails(orderAdditionalEmails);

        return errorMessages;
    }*/
    
    private List<String> setCheckOutShippingAddressInternal(String shippingContactMechId) {
        List<String> errorMessages = new ArrayList<String>();
        String errMsg = null;

        // set the shipping address
        if (UtilValidate.isNotEmpty(shippingContactMechId)) {
            this.cart.setAllShippingContactMechId(shippingContactMechId);
        } else if (cart.shippingApplies()) {
            // only return an error if shipping is required for this purchase
            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.select_shipping_destination", (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        return errorMessages;
    }
    
    public void calcAndAddTax() throws GeneralException {
        calcAndAddTax(null, false);
    }
    
    public void calcAndAddTax(GenericValue shipAddress, boolean skipEmptyAddresses) throws GeneralException {
        if (UtilValidate.isEmpty(cart.getShippingContactMechId()) && cart.getBillingAddress() == null && shipAddress == null) {
            return;
        }

        int shipGroups = this.cart.getShipGroupSize();
        for (int i = 0; i < shipGroups; i++) {
            ShoppingCart.CartShipInfo csi = cart.getShipInfo(i);
            Map<Integer, ShoppingCartItem> shoppingCartItemIndexMap = new HashMap<Integer, ShoppingCartItem>();
            Map<String, Object> serviceContext = this.makeTaxContext(i, shipAddress, shoppingCartItemIndexMap, cart.getFacilityId(), skipEmptyAddresses);
            if (skipEmptyAddresses && serviceContext == null) {
                csi.clearAllTaxInfo();
                continue;
            }
            List<List<? extends Object>> taxReturn = this.getTaxAdjustments(dispatcher, "calcTax", serviceContext);

            if (Debug.verboseOn()) Debug.logVerbose("ReturnList: " + taxReturn, module);
            List<GenericValue> orderAdj = UtilGenerics.checkList(taxReturn.get(0));
            List<List<GenericValue>> itemAdj = UtilGenerics.checkList(taxReturn.get(1));

            // set the item adjustments
            if (itemAdj != null) {
                for (int x = 0; x < itemAdj.size(); x++) {
                    List<GenericValue> adjs = itemAdj.get(x);
                    ShoppingCartItem item = shoppingCartItemIndexMap.get(Integer.valueOf(x));
                    if (adjs == null) {
                        adjs = new LinkedList<GenericValue>();
                    }
                    csi.setItemInfo(item, adjs);
                    if (Debug.verboseOn()) Debug.logVerbose("Added item adjustments to ship group [" + i + " / " + x + "] - " + adjs, module);
                }
            }

            // need to manually clear the order adjustments
            csi.shipTaxAdj.clear();
            csi.shipTaxAdj.addAll(orderAdj);
        }
    }
    
    public List<String> setCheckOutPaymentInternal(Map<String, Map<String, Object>> selectedPaymentMethods, List<String> singleUsePayments, String billingAccountId) {
        List<String> errorMessages = new ArrayList<String>();
        String errMsg = null;

        if (singleUsePayments == null) {
            singleUsePayments = new ArrayList<String>();
        }

        // set the payment method option
        if (UtilValidate.isNotEmpty(selectedPaymentMethods)) {
            // clear out the old payments
            cart.clearPayments();

            if (UtilValidate.isNotEmpty(billingAccountId)) {
                Map<String, Object> billingAccountMap = selectedPaymentMethods.get("EXT_BILLACT");
                BigDecimal billingAccountAmt = (BigDecimal)billingAccountMap.get("amount");
                // set cart billing account data and generate a payment method containing the amount we will be charging
                cart.setBillingAccount(billingAccountId, (billingAccountAmt != null ? billingAccountAmt: BigDecimal.ZERO));
                // copy the billing account terms as order terms
                try {
                    List<GenericValue> billingAccountTerms = delegator.findByAnd("BillingAccountTerm", UtilMisc.toMap("billingAccountId", billingAccountId), null, false);
                    if (UtilValidate.isNotEmpty(billingAccountTerms)) {
                        for (GenericValue billingAccountTerm : billingAccountTerms) {
                            // the term is not copied if in the cart a term of the same type is already set
                            if (!cart.hasOrderTerm(billingAccountTerm.getString("termTypeId"))) {
                                cart.addOrderTerm(billingAccountTerm.getString("termTypeId"), billingAccountTerm.getBigDecimal("termValue"), billingAccountTerm.getLong("termDays"));
                            }
                        }
                    }
                } catch (GenericEntityException gee) {
                    Debug.logWarning("Error copying billing account terms to order terms: " + gee.getMessage(), module);
                }
            } else {
                // remove the billing account from the cart
                cart.setBillingAccount(null, BigDecimal.ZERO);
            }

            // if checkoutPaymentId == EXT_BILLACT, then we have billing account only, so make sure we have enough credit
            if (selectedPaymentMethods.containsKey("EXT_BILLACT") && selectedPaymentMethods.size() == 1) {
                BigDecimal accountCredit = this.availableAccountBalance(cart.getBillingAccountId());
                BigDecimal amountToUse = cart.getBillingAccountAmount();

                // if an amount was entered, check that it doesn't exceed available amount
                if (amountToUse.compareTo(BigDecimal.ZERO) > 0 && amountToUse.compareTo(accountCredit) > 0) {
                    errMsg = UtilProperties.getMessage(resource_error,"checkhelper.insufficient_credit_available_on_account",
                            (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                } else {
                    // otherwise use the available account credit (The user might enter 10.00 for an order worth 20.00 from an account with 30.00. This makes sure that the 30.00 is used)
                    amountToUse = accountCredit;
                }

                // check that the amount to use is enough to fulfill the order
                BigDecimal grandTotal = cart.getGrandTotal();
                if (grandTotal.compareTo(amountToUse) > 0) {
                    cart.setBillingAccount(null, BigDecimal.ZERO); // erase existing billing account data
                    errMsg = UtilProperties.getMessage(resource_error,"checkhelper.insufficient_credit_available_on_account",
                            (cart != null ? cart.getLocale() : Locale.getDefault()));
                    errorMessages.add(errMsg);
                } else {
                    // since this is the only selected payment method, let's make this amount the grand total for convenience
                    amountToUse = grandTotal;
                }

                // associate the cart billing account amount and EXT_BILLACT selected payment method with whatever amount we have now
                // XXX: Note that this step is critical for the billing account to be charged correctly
                if (amountToUse.compareTo(BigDecimal.ZERO) > 0) {
                    cart.setBillingAccount(billingAccountId, amountToUse);
                    selectedPaymentMethods.put("EXT_BILLACT", UtilMisc.<String, Object>toMap("amount", amountToUse, "securityCode", null));
                }
            }

            for (String checkOutPaymentId : selectedPaymentMethods.keySet()) {
                String finAccountId = null;

                if (checkOutPaymentId.indexOf("|") > -1) {
                    // split type -- ID|Actual
                    String[] splitStr = checkOutPaymentId.split("\\|");
                    checkOutPaymentId = splitStr[0];
                    if ("FIN_ACCOUNT".equals(checkOutPaymentId)) {
                        finAccountId = splitStr[1];
                    }
                    if (Debug.verboseOn()) Debug.logVerbose("Split checkOutPaymentId: " + splitStr[0] + " / " + splitStr[1], module);
                }

                // get the selected amount to use
                BigDecimal paymentAmount = null;
                String securityCode = null;
                if (selectedPaymentMethods.get(checkOutPaymentId) != null) {
                    Map<String, Object> checkOutPaymentInfo = selectedPaymentMethods.get(checkOutPaymentId);
                    paymentAmount = (BigDecimal) checkOutPaymentInfo.get("amount");
                    securityCode = (String) checkOutPaymentInfo.get("securityCode");
                }

                boolean singleUse = singleUsePayments.contains(checkOutPaymentId);
                ShoppingCart.CartPaymentInfo inf = cart.addPaymentAmount(checkOutPaymentId, paymentAmount, singleUse);
                if (finAccountId != null) {
                    inf.finAccountId = finAccountId;
                }
                if (securityCode != null) {
                    inf.securityCode = securityCode;
                }
            }
        } else if (cart.getGrandTotal().compareTo(BigDecimal.ZERO) != 0) {
            // only return an error if the order total is not 0.00
            errMsg = UtilProperties.getMessage(resource_error,"checkhelper.select_method_of_payment",
                    (cart != null ? cart.getLocale() : Locale.getDefault()));
            errorMessages.add(errMsg);
        }

        return errorMessages;
    }
    
    private Map<String, Object> makeTaxContext(int shipGroup, GenericValue shipAddress, Map<Integer, ShoppingCartItem> shoppingCartItemIndexMap, String originFacilityId, boolean skipEmptyAddresses) {
        ShoppingCart.CartShipInfo csi = cart.getShipInfo(shipGroup);
        int totalItems = csi.shipItemInfo.size();

        List<GenericValue> product = new ArrayList<GenericValue>(totalItems);
        List<BigDecimal> amount = new ArrayList<BigDecimal>(totalItems);
        List<BigDecimal> price = new ArrayList<BigDecimal>(totalItems);
        List<BigDecimal> quantity = new ArrayList<BigDecimal>(totalItems);
        List<BigDecimal> shipAmt = new ArrayList<BigDecimal>(totalItems);

        // Debug.logInfo("====== makeTaxContext passed in shipAddress=" + shipAddress, module);

        Iterator<ShoppingCartItem> it = csi.shipItemInfo.keySet().iterator();
        for (int i = 0; i < totalItems; i++) {
            ShoppingCartItem cartItem = it.next();
            ShoppingCart.CartShipInfo.CartShipItemInfo itemInfo = csi.getShipItemInfo(cartItem);

            //Debug.logInfo("In makeTaxContext for item [" + i + "] in ship group [" + shipGroup + "] got cartItem: " + cartItem, module);
            //Debug.logInfo("In makeTaxContext for item [" + i + "] in ship group [" + shipGroup + "] got itemInfo: " + itemInfo, module);

            product.add(i, cartItem.getProduct());
            amount.add(i, cartItem.getItemSubTotal(itemInfo.quantity));
            price.add(i, cartItem.getBasePrice());
            quantity.add(i, cartItem.getQuantity());
            shipAmt.add(i, BigDecimal.ZERO); // no per item shipping yet
            shoppingCartItemIndexMap.put(Integer.valueOf(i), cartItem);
        }

        //add promotion adjustments
        List<GenericValue> allAdjustments = cart.getAdjustments();
        BigDecimal orderPromoAmt = OrderReadHelper.calcOrderPromoAdjustmentsBd(allAdjustments);

        BigDecimal shipAmount = csi.shipEstimate;
        if (shipAddress == null) {
            shipAddress = cart.getShippingAddress(shipGroup);
            // Debug.logInfo("====== makeTaxContext set shipAddress to cart.getShippingAddress(shipGroup): " + shipAddress, module);
        }

        if (shipAddress == null && skipEmptyAddresses) {
            return null;
        }

        // no shipping address; try the billing address
        if (shipAddress == null) {
            for (int i = 0; i < cart.selectedPayments(); i++) {
                ShoppingCart.CartPaymentInfo cpi = cart.getPaymentInfo(i);
                GenericValue billAddr = cpi.getBillingAddress(delegator);
                if (billAddr != null) {
                    shipAddress = billAddr;
                    Debug.logInfo("In makeTaxContext no shipping address, but found address with ID [" + shipAddress.get("contactMechId") + "] from payment method.", module);
                    break;
                }
            }
        }

        if (shipAddress == null) {
            // face-to-face order; use the facility address
            if (originFacilityId != null) {
                GenericValue facilityContactMech = ContactMechWorker.getFacilityContactMechByPurpose(delegator, originFacilityId, UtilMisc.toList("SHIP_ORIG_LOCATION", "PRIMARY_LOCATION"));
                if (facilityContactMech != null) {
                    try {
                        shipAddress = delegator.findOne("PostalAddress",
                                UtilMisc.toMap("contactMechId", facilityContactMech.getString("contactMechId")), false);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                    }
                }
            }
        }

        // if shippingAddress is still null then don't calculate tax; it may be an situation where no tax is applicable, or the data is bad and we don't have a way to find an address to check tax for
        if (shipAddress == null) {
            Debug.logWarning("Not calculating tax for new order because there is no shipping address, no billing address, and no address on the origin facility [" + originFacilityId + "]", module);
        }
        
        Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("productStoreId", cart.getProductStoreId());
        serviceContext.put("payToPartyId", cart.getBillFromVendorPartyId());
        serviceContext.put("billToPartyId", cart.getBillToCustomerPartyId());
        serviceContext.put("itemProductList", product);
        serviceContext.put("itemAmountList", amount);
        serviceContext.put("itemPriceList", price);
        serviceContext.put("itemQuantityList", quantity);
        serviceContext.put("itemShippingList", shipAmt);
        serviceContext.put("orderShippingAmount", shipAmount);
        serviceContext.put("shippingAddress", shipAddress);
        serviceContext.put("orderPromotionsAmount", orderPromoAmt);

        return serviceContext;
    }
    
    // Calc the tax adjustments.
    private List<List<? extends Object>> getTaxAdjustments(LocalDispatcher dispatcher, String taxService, Map<String, Object> serviceContext) throws GeneralException {
        Map<String, Object> serviceResult = null;

        try {
            serviceResult = dispatcher.runSync(taxService, serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException("Problem occurred in tax service (" + e.getMessage() + ")", e);
        }

        if (ServiceUtil.isError(serviceResult)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(serviceResult));
        }

        // the adjustments (returned in order) from taxware.
        List<GenericValue> orderAdj = UtilGenerics.checkList(serviceResult.get("orderAdjustments"));
        List<List<GenericValue>> itemAdj = UtilGenerics.checkList(serviceResult.get("itemAdjustments"));

        return UtilMisc.toList(orderAdj, itemAdj);
    }
    public static BigDecimal availableAccountBalance(String billingAccountId, LocalDispatcher dispatcher) {
        if (billingAccountId == null) return BigDecimal.ZERO;
        try {
            Map<String, Object> res = dispatcher.runSync("calcBillingAccountBalance", UtilMisc.toMap("billingAccountId", billingAccountId));
            BigDecimal availableBalance = (BigDecimal) res.get("accountBalance");
            if (availableBalance != null) {
                return availableBalance;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        return BigDecimal.ZERO;
    }
    public BigDecimal availableAccountBalance(String billingAccountId) {
        return availableAccountBalance(billingAccountId, dispatcher);
    }
    
    // TODOCHANGE copy form createOrder method in com.olbius.order.CheckOutHelper.createOrder(GenericValue, String, String, List<GenericValue>, boolean, String, String)
    public Map<String, Object> createOrder(GenericValue userLogin, String distributorId, String affiliateId,
            List<GenericValue> trackingCodeOrders, boolean areOrderItemsExploded, String visitId, String webSiteId) {
        if (this.cart == null) {
            return null;
        }
        String orderId = this.cart.getOrderId();
        String supplierPartyId = (String) this.cart.getAttribute("supplierPartyId");
        String originOrderId = (String) this.cart.getAttribute("originOrderId");

        this.cart.clearAllItemStatus();

        BigDecimal grandTotal = this.cart.getGrandTotal();

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
        context.put("locale", cart.getLocale());
        
        // need the partyId; don't use userLogin in case of an order via order mgr
        String partyId = this.cart.getPartyId();
        String productStoreId = cart.getProductStoreId();

        // store the order - invoke the service
        Map<String, Object> storeResult = null;

        try {
            storeResult = dispatcher.runSync("storeOrderDis", context);
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
            String errMsg = UtilProperties.getMessage(resource_error, "checkhelper.could_not_create_order_invoking_service", messageMap, (cart != null ? cart.getLocale() : Locale.getDefault()));
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // check for error message(s)
        if (ServiceUtil.isError(storeResult)) {
            String errMsg = UtilProperties.getMessage(resource_error, "checkhelper.did_not_complete_order_following_occurred", (cart != null ? cart.getLocale() : Locale.getDefault()));
            List<String> resErrorMessages = new LinkedList<String>();
            resErrorMessages.add(errMsg);
            resErrorMessages.add(ServiceUtil.getErrorMessage(storeResult));
            return ServiceUtil.returnError(resErrorMessages);
        }

        // ----------
        // If needed, the production runs are created and linked to the order lines.
        //
        List<GenericValue> orderItems = UtilGenerics.checkList(context.get("orderItems"));
        int counter = 0;
        for (GenericValue orderItem : orderItems) {
            String productId = orderItem.getString("productId");
            if (productId != null) {
                try {
                    // do something tricky here: run as the "system" user
                    // that can actually create and run a production run
                    GenericValue permUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
                    GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
                    GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                    if ("AGGR_DIGSERV_CONF".equals(product.getString("productTypeId")) && // don't for digital service 
                    		EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "AGGREGATED")) {
                        org.ofbiz.product.config.ProductConfigWrapper config = this.cart.findCartItem(counter).getConfigWrapper();
                        Map<String, Object> inputMap = new HashMap<String, Object>();
                        inputMap.put("config", config);
                        inputMap.put("facilityId", productStore.getString("inventoryFacilityId"));
                        inputMap.put("orderId", orderId);
                        inputMap.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                        inputMap.put("quantity", orderItem.getBigDecimal("quantity"));
                        inputMap.put("userLogin", permUserLogin);

                        Map<String, Object> prunResult = dispatcher.runSync("createProductionRunFromConfiguration", inputMap);
                        if (ServiceUtil.isError(prunResult)) {
                            Debug.logError(ServiceUtil.getErrorMessage(prunResult) + " for input:" + inputMap, module);
                        }
                    }
                } catch (Exception e) {
                    String service = e.getMessage();
                    Map<String, String> messageMap = UtilMisc.toMap("service", service);
                    String errMsg = UtilProperties.getMessage(resource_error, "checkhelper.could_not_create_order_invoking_service", messageMap, (cart != null ? cart.getLocale() : Locale.getDefault()));
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
            counter++;
        }
        // ----------

        // ----------
        // The status of the requirement associated to the shopping cart lines is set to "ordered".
        //
        for (ShoppingCartItem shoppingCartItem : this.cart.items()) {
            String requirementId = shoppingCartItem.getRequirementId();
            if (requirementId != null) {
                try {
                    Map<String, Object> inputMap = UtilMisc.<String, Object>toMap("requirementId", requirementId, "statusId", "REQ_ORDERED");
                    inputMap.put("userLogin", userLogin);
                    // TODO: check service result for an error return
                    dispatcher.runSync("updateRequirement", inputMap);
                } catch (Exception e) {
                    String service = e.getMessage();
                    Map<String, String> messageMap = UtilMisc.toMap("service", service);
                    String errMsg = UtilProperties.getMessage(resource_error, "checkhelper.could_not_create_order_invoking_service", messageMap, (cart != null ? cart.getLocale() : Locale.getDefault()));
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }
        // ----------

        // set the orderId for use by chained events
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("orderId", orderId);
        result.put("orderAdditionalEmails", this.cart.getOrderAdditionalEmails());

        // save the emails to the order
        List<GenericValue> toBeStored = new LinkedList<GenericValue>();

        GenericValue party = null;
        try {
            party = this.delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderProblemsGettingPartyRecord", cart.getLocale()), module);
        }

        // create order contact mechs for the email address(s)
        if (party != null) {
            Iterator<GenericValue> emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = emailIter.next();
                GenericValue orderContactMech = this.delegator.makeValue("OrderContactMech",
                        UtilMisc.toMap("orderId", orderId, "contactMechId", email.getString("contactMechId"), "contactMechPurposeTypeId", "ORDER_EMAIL"));
                toBeStored.add(orderContactMech);
                if (UtilValidate.isEmpty(ContactHelper.getContactMechByPurpose(party, "ORDER_EMAIL", false))) {
                    GenericValue partyContactMechPurpose = this.delegator.makeValue("PartyContactMechPurpose",
                            UtilMisc.toMap("partyId", party.getString("partyId"), "contactMechId", email.getString("contactMechId"), "contactMechPurposeTypeId", "ORDER_EMAIL", "fromDate", UtilDateTime.nowTimestamp()));
                    toBeStored.add(partyContactMechPurpose);
                }
            }
        }

        // create dummy contact mechs and order contact mechs for the additional emails
        String additionalEmails = this.cart.getOrderAdditionalEmails();
        List<String> emailList = StringUtil.split(additionalEmails, ",");
        if (emailList == null) emailList = new ArrayList<String>();
        for (String email : emailList) {
            String contactMechId = this.delegator.getNextSeqId("ContactMech");
            GenericValue contactMech = this.delegator.makeValue("ContactMech",
                    UtilMisc.toMap("contactMechId", contactMechId, "contactMechTypeId", "EMAIL_ADDRESS", "infoString", email));

            GenericValue orderContactMech = this.delegator.makeValue("OrderContactMech",
                    UtilMisc.toMap("orderId", orderId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "ORDER_EMAIL"));
            toBeStored.add(contactMech);
            toBeStored.add(orderContactMech);
        }

        if (toBeStored.size() > 0) {
            try {
                if (Debug.verboseOn()) Debug.logVerbose("To Be Stored: " + toBeStored, module);
                this.delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                // not a fatal error; so just print a message
                Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderProblemsStoringOrderEmailContactInformation", cart.getLocale()), module);
            }
        }

        return result;
    }
}
