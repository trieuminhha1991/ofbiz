package com.olbius.basepos.lean;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;

public class PaymentWorker {
	private AccountingWorker accountingWorker;
	private DataWorker dataWorker;
	
	public PaymentWorker(DataWorker dataWorker, AccountingWorker accountingWorker){
		this.dataWorker = dataWorker;
		this.accountingWorker = accountingWorker;
	}
	public boolean validateAmount(String strCashAmount, String strCreditCardAmount){
		// Validate input amount
		if(UtilValidate.isEmpty(strCashAmount) && UtilValidate.isEmpty(strCreditCardAmount) && dataWorker.cart.getGranTotalReturn().signum() != 0){
			return false;
		}
		// Validate Cash amount
		if(UtilValidate.isNotEmpty(strCashAmount)){
			BigDecimal cashAmount = new BigDecimal(strCashAmount);
			if(UtilValidate.isNotEmpty(cashAmount) && cashAmount.compareTo(BigDecimal.ZERO) >0){
				dataWorker.webPosTransaction.addPayment("CASH", cashAmount, "", "");
			}else{
				return false;
			}
		}
		// Validate Credit amount
		if(UtilValidate.isNotEmpty(strCreditCardAmount)){
			BigDecimal creditCardAmount = new BigDecimal(strCreditCardAmount);
			if(UtilValidate.isNotEmpty(creditCardAmount) && creditCardAmount.compareTo(BigDecimal.ZERO) >= 0){
				dataWorker.webPosTransaction.addPayment("COMPANY_CHECK", creditCardAmount);
			}else{
				return false;
			}
		}
		return true;
	}
	public void createPayment() throws GenericEntityException{
		createPaymentPreference();
	}
	private void createPaymentPreference() throws GenericEntityException{
		BigDecimal prefTotal = BigDecimal.ZERO;
		BigDecimal cartTotal = dataWorker.cart.getGrandTotalNoReturn();
		List<GenericValue> allPaymentPrefs = dataWorker.delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", dataWorker.cart.getOrderId()), null, false);
        if (allPaymentPrefs != null) {
            for (GenericValue pref : allPaymentPrefs) {
                BigDecimal maxAmount = pref.getBigDecimal("maxAmount");
                if (maxAmount == null) maxAmount = BigDecimal.ZERO;
                prefTotal = prefTotal.add(maxAmount);
            }
        }
        
        if (prefTotal.compareTo(cartTotal) > 0) {
            BigDecimal change = prefTotal.subtract(cartTotal).negate();
            GenericValue newPref = dataWorker.delegator.makeValue("OrderPaymentPreference");
            newPref.set("orderId", dataWorker.cart.getOrderId());
            newPref.set("paymentMethodTypeId", "CASH");
            newPref.set("statusId", "PAYMENT_RECEIVED");
            newPref.set("maxAmount", change);
            newPref.set("createdDate", UtilDateTime.nowTimestamp());
            if (dataWorker.posSession.getUserLogin() != null) {
                newPref.set("createdByUserLogin", dataWorker.posSession.getUserLoginId());
            }
            dataWorker.delegator.createSetNextSeqId(newPref);
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
        // FIXME Test COMPANY_CHECK method
	}
	public void createReceivedPayments() throws Exception {
        GenericValue orderHeader = null;
        orderHeader = dataWorker.delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", dataWorker.cart.getOrderId()), false);
        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            GenericValue btparty = orh.getBillToParty();
            String partyId = "_NA_";
            if (btparty != null) {
                partyId = btparty.getString("partyId");
            }
            List<GenericValue> opps = orh.getPaymentPreferences();
            // TODO Improve the following code
            for (GenericValue opp : opps) {
                if ("PAYMENT_RECEIVED".equals(opp.getString("statusId"))) {
                    List<GenericValue> payments = orh.getOrderPayments(opp);
                    if (UtilValidate.isEmpty(payments)) {
                    	GenericValue orderPaymentPreference = dataWorker.delegator.findOne("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", opp.get("orderPaymentPreferenceId")), false);
                    	GenericValue productStore = orderHeader.getRelatedOne("ProductStore", true);
                        // Create Payment
                        GenericValue payment = dataWorker.delegator.makeValue("Payment");
                        payment.set("paymentId", dataWorker.delegator.getNextSeqId("Payment"));
                        payment.set("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId"));
                        payment.set("paymentMethodId", orderPaymentPreference.getString("paymentMethodId"));
                        payment.set("amount", orderPaymentPreference.getBigDecimal("maxAmount"));
                        payment.set("paymentTypeId", "CUSTOMER_PAYMENT");
                        payment.set("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
                        payment.set("partyIdTo", productStore.getString("payToPartyId"));
                        payment.set("partyIdFrom", partyId);
                        payment.set("statusId", "PMNT_RECEIVED");
                        payment.set("effectiveDate", UtilDateTime.nowTimestamp());
                        payment.set("currencyUomId", productStore.getString("defaultCurrencyUomId"));
                        payment.create();
                        // FIXME Improve following function
                        /*dispatcher.runSync("createAcctgTransAndEntriesForIncomingPayment",UtilMisc.<String, Object>toMap("userLogin", this.webPosSession.getUserLogin(),
                        		"paymentId", payment.get("paymentId")));*/
                        accountingWorker.createAcctgTransAndEntriesForIncomingPayment(payment.getString("paymentId"));
                        payment.set("statusId","PMNT_CONFIRMED");
                        payment.store();
                    }
                }
            }
        }
    }
}
