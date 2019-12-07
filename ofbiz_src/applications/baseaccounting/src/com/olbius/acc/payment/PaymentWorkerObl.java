package com.olbius.acc.payment;

public class PaymentWorkerObl {

	/*public static BigDecimal getPaymentApplied(Delegator delegator, GenericValue payment) throws GenericEntityException {
		BigDecimal paymentApplied = BigDecimal.ZERO;
		List<EntityExpr> cond = UtilMisc.toList(
                EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, payment.getString("paymentId")),
                EntityCondition.makeCondition("toPaymentId", EntityOperator.EQUALS, payment.getString("paymentId"))
               );
        EntityCondition partyCond = EntityCondition.makeCondition(cond, EntityOperator.OR);
        String defaultCurrency = AccountUtils.getDefaultCurrencyUom(delegator, null);
        List<GenericValue> paymentApplications = delegator.findList("PaymentApplication", partyCond, null, UtilMisc.toList("invoiceId", "billingAccountId"), null, false);
        for(GenericValue paymentApplication: paymentApplications){
        	BigDecimal amountApplied = paymentApplication.getBigDecimal("amountApplied");
        	GenericValue invoice = paymentApplication.getRelatedOne("Invoice", false);
        	String invoiceCurrencyUom = invoice.getString("currencyUomId");
        	amountApplied = AccountUtils.getAmountAppliedConversion(delegator, amountApplied, invoiceCurrencyUom, defaultCurrency, UtilDateTime.nowTimestamp());
        	paymentApplied = paymentApplied.add(amountApplied);
        }
		return paymentApplied;
	}*/
}
