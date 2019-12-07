package com.olbius.accounting.ledger;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 7/11/18.
 */
public class AcctgTransWorkers {
    private static final int DECIMALS = UtilNumber.getBigDecimalScale("ledger.decimals");
    private static final int ROUNDING = UtilNumber.getBigDecimalRoundingMode("ledger.rounding");

    static Boolean isPaymentAdvance(Delegator delegator, GenericValue payment) {
        Boolean isPaymentAdvance = false;
        if(UtilValidate.isNotEmpty(payment.get("paymentPreferenceId"))) return true;
        return false;
    }
    public static void createPaidAdvanceIfNotExists(Delegator delegator, GenericValue shipment) throws GenericEntityException {
        List<GenericValue> paidAdvances = delegator.findList("PaidAdvanceShipmentRelation", EntityCondition.makeCondition("shipmentId", shipment.get("shipmentId")), null, null, null, false);
        if(UtilValidate.isNotEmpty(paidAdvances)) return;
        String orderId = shipment.getString("primaryOrderId");
        List<GenericValue> orderPaymentPreference = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
        for(GenericValue pre : orderPaymentPreference) {
            List<GenericValue> payments = delegator.findList("Payment", EntityCondition.makeCondition("paymentPreferenceId", pre.get("orderPaymentPreferenceId")), null, null, null, false);
            for(GenericValue payment : payments) {
                    GenericValue paidAdvance = delegator.makeValue("PaidAdvanceShipmentRelation");
                    paidAdvance.set("relationId", delegator.getNextSeqId("PaidAdvanceShipmentRelation"));
                    paidAdvance.set("shipmentId", shipment.get("shipmentId"));
                    paidAdvance.set("paymentId", payment.get("paymentId"));
                    paidAdvance.set("unpaidAmount", payment.get("amount"));
                    paidAdvance.set("currencyUomId", payment.get("currencyUomId"));
                    paidAdvance.set("isPaid", false);
                    paidAdvance.create();
            }
        }
    }
    public static BigDecimal getAmountOfPaymentAdvance(Delegator delegator, String acctgTransId) throws GenericEntityException {
        GenericValue acctgTrans = delegator.findOne("AcctgTrans", UtilMisc.toMap("acctgTransId", acctgTransId), false);
        BigDecimal amount = BigDecimal.ZERO;
        if(UtilValidate.isEmpty(acctgTrans.get("shipmentId"))) return BigDecimal.ZERO;
        String shipmentId = acctgTrans.getString("shipmentId");
        GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
        String orderId = shipment.getString("primaryOrderId");
        List<GenericValue> orderPaymentPreference = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
        for(GenericValue pre : orderPaymentPreference) {
            List<GenericValue> payments = delegator.findList("Payment", EntityCondition.makeCondition("paymentPreferenceId", pre.get("orderPaymentPreferenceId")), null, null, null, false);
            for(GenericValue payment : payments) {
                amount = amount.add(payment.getBigDecimal("amount"));
            }
        }
        return amount;
    }
    public static Map<BigDecimal, BigDecimal> getExchangedRateFromOrder(DispatchContext ctx, String orderId) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<BigDecimal, BigDecimal> exChanged = FastMap.newInstance();
        if(UtilValidate.isEmpty(orderId)) return null;
        List<GenericValue> orderPaymentPreference = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
        for(GenericValue pre : orderPaymentPreference) {
            List<GenericValue> payments = delegator.findList("Payment", EntityCondition.makeCondition("paymentPreferenceId", pre.get("orderPaymentPreferenceId")), null, null, null, false);
            for(GenericValue payment : payments) {
                GenericValue acctgTrans = EntityUtil.getFirst(delegator.findList("AcctgTrans", EntityCondition.makeCondition("paymentId", payment.get("paymentId")), null, null, null, false));
                GenericValue paidAdvance = EntityUtil.getFirst(delegator.findList("PaidAdvanceShipmentRelation", EntityCondition.makeCondition("paymentId", payment.get("paymentId")), null, null, null, false));
                if(UtilValidate.isNotEmpty(acctgTrans)) {
                    BigDecimal conversionFactor = getConversionFactor(dispatcher, payment.getString("paymentId"), "PAYMENT");
                    if(paidAdvance != null && (UtilValidate.isEmpty(paidAdvance.get("isPaid")) || !paidAdvance.getBoolean("isPaid")))
                        exChanged.put(paidAdvance.getBigDecimal("unpaidAmount"), conversionFactor);
                }
            }
        }
        return exChanged;
    }

    public static List<String> getAcctgTransOfAdvancePayment(Delegator delegator, String orderId) throws GenericEntityException {
        if(UtilValidate.isEmpty(orderId)) return null;
        List<GenericValue> orderPaymentPreference = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
        List<String> listAcctgTransId = FastList.newInstance();
        for(GenericValue pre : orderPaymentPreference) {
            List<GenericValue> payments = delegator.findList("Payment", EntityCondition.makeCondition("paymentPreferenceId", pre.get("orderPaymentPreferenceId")), null, null, null, false);
            for(GenericValue payment : payments) {
                GenericValue acctgTrans = EntityUtil.getFirst(delegator.findList("AcctgTrans", EntityCondition.makeCondition("paymentId", payment.get("paymentId")), null, null, null, false));
                if(UtilValidate.isNotEmpty(acctgTrans)) listAcctgTransId.add(acctgTrans.getString("acctgTransId"));
            }
        }
        return listAcctgTransId;
    }

    public static BigDecimal calculateAmountFromCurrency(DispatchContext ctx, GenericValue acctgTransEntry, Map<BigDecimal, BigDecimal> advancePayment, BigDecimal totalUnitCost, String shipmentId) throws GenericServiceException, GenericEntityException {
        if(acctgTransEntry.get("origCurrencyUomId").equals(acctgTransEntry.get("currencyUomId"))) {
            return totalUnitCost;
        }
        BigDecimal unpaidAmount = totalUnitCost;
        LocalDispatcher dispatcher = ctx.getDispatcher();
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;
        for(BigDecimal paidAmount : advancePayment.keySet()) {
            if(unpaidAmount.compareTo(paidAmount) < 0) {
                BigDecimal conversionFactor = advancePayment.get(paidAmount);
                amount = amount.add(unpaidAmount.multiply(conversionFactor));
                paid = paid.add(unpaidAmount);
                unpaidAmount = BigDecimal.ZERO;
                break;
            }
            else {
                paid = paid.add(paidAmount);
                unpaidAmount = unpaidAmount.subtract(paidAmount);
                BigDecimal conversionFactor = advancePayment.get(paidAmount);
                amount = amount.add(paidAmount.multiply(conversionFactor));
            }
        }

        updatePaidAdvanceShipmentRelation(ctx.getDelegator(), paid, shipmentId);
        GenericValue acctgTrans = ctx.getDelegator().findOne("AcctgTrans", UtilMisc.toMap("acctgTransId", acctgTransEntry.get("acctgTransId")), false);
        Map<String, Object> convertUomInMap = FastMap.newInstance();
        convertUomInMap.put("originalValue", unpaidAmount);
        convertUomInMap.put("uomId", acctgTransEntry.get("origCurrencyUomId"));
        convertUomInMap.put("uomIdTo", acctgTransEntry.get("currencyUomId"));
        if(UtilValidate.isNotEmpty(acctgTrans))
            convertUomInMap.put("asOfDate", acctgTrans.get("transactionDate"));
        Map<String, Object> result = dispatcher.runSync("convertUomMoney", convertUomInMap);
        BigDecimal convertedValue = ((BigDecimal) result.get("convertedValue")).setScale(DECIMALS, ROUNDING);
        amount = amount.add(convertedValue);
        return amount;
    }

    public static BigDecimal getConversionFactor(LocalDispatcher dispatcher, String documentId, String documentTypeId) throws GenericServiceException, GenericEntityException {
        BigDecimal conversionFactor = BigDecimal.ONE;
        Delegator delegator = dispatcher.getDelegator();
        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("documentId", documentId));
        conds.add(EntityCondition.makeCondition("documentTypeId", documentTypeId));
        GenericValue x = EntityUtil.getFirst(delegator.findList("ExchangedRateHistory", EntityCondition.makeCondition(conds), null, null, null, false));
        if(UtilValidate.isNotEmpty(x) && UtilValidate.isNotEmpty(x.get("conversionFactor")))
            conversionFactor = x.getBigDecimal("conversionFactor");
        return conversionFactor;
    }

    public static BigDecimal getConversionFactorForInvoice(Delegator delegator, String invoiceId) throws GenericEntityException {
        BigDecimal conversionFactor = BigDecimal.ONE;
        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("documentId", invoiceId));
        conds.add(EntityCondition.makeCondition("documentTypeId", "INVOICE"));
        GenericValue x = EntityUtil.getFirst(delegator.findList("ExchangedRateHistory", EntityCondition.makeCondition(conds), null, null, null, false));
        if(UtilValidate.isNotEmpty(x)) conversionFactor  = x.getBigDecimal("conversionFactor");
        return conversionFactor;
    }

    public static BigDecimal getExchangedRateFromShipmentReceipt(Delegator delegator, String invoiceId) throws GenericEntityException {
        BigDecimal conversionFactor = BigDecimal.ONE;
        List<GenericValue> orderItemBillings = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false);
        for(GenericValue orderItemBilling : orderItemBillings) {
            String orderId = orderItemBilling.getString("orderId");
            List<GenericValue> listShipment = delegator.findList("Shipment", EntityCondition.makeCondition("primaryOrderId", orderId), null, null, null, false);
            for(GenericValue shipment : listShipment) {
                GenericValue delivery = EntityUtil.getFirst(delegator.findList("Delivery", EntityCondition.makeCondition("shipmentId", shipment.get("shipmentId")), null, null, null, false));
                List<EntityCondition> conds = FastList.newInstance();
                conds.add(EntityCondition.makeCondition("documentId", delivery.get("deliveryId")));
                conds.add(EntityCondition.makeCondition("documentTypeId", "DELIVERY"));
                GenericValue exchangedRate = EntityUtil.getFirst(delegator.findList("ExchangedRateHistory", EntityCondition.makeCondition(conds), null, null, null, false));
                if(UtilValidate.isNotEmpty(exchangedRate.get("conversionFactor")))
                    conversionFactor = exchangedRate.getBigDecimal("conversionFactor");            }
        }
        return conversionFactor;
    }

    public static String getBaseCurrencyUomId(LocalDispatcher dispatcher, String organizationPartyId, GenericValue userLogin) throws GenericServiceException {
        Map<String, Object> result = dispatcher.runSync("getPartyAccountingPreferences", UtilMisc.toMap("organizationPartyId", organizationPartyId, "userLogin", userLogin));
        GenericValue partyAccountingPreference = (GenericValue) result.get("partyAccountingPreference");
        return partyAccountingPreference.getString("baseCurrencyUomId");
    }

    public static void updatePaidAdvanceShipmentRelation(Delegator delegator, BigDecimal paidAmount, String shipmentId) throws GenericEntityException {
        GenericValue paidAdvanceShipmentRelation = EntityUtil.getFirst(delegator.findList("PaidAdvanceShipmentRelation", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false));
        BigDecimal unpaidAmount = paidAdvanceShipmentRelation.getBigDecimal("unpaidAmount");
        unpaidAmount = unpaidAmount.subtract(paidAmount);
        if(unpaidAmount.compareTo(BigDecimal.ZERO) <= 0) paidAdvanceShipmentRelation.set("isPaid", true);
        paidAdvanceShipmentRelation.set("unpaidAmount", unpaidAmount);
        paidAdvanceShipmentRelation.store();
    }

    public static String getGlAccountIdFromGlAccountType(Delegator delegator, String glAccountTypeId, String orgId) throws GenericEntityException {
        GenericValue glAccountTypeDefault = delegator.findOne("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId", glAccountTypeId, "organizationPartyId", orgId), false);
        if(UtilValidate.isNotEmpty(glAccountTypeDefault.get("glAccountId"))) return glAccountTypeDefault.getString("glAccountId");
        return null;
    };

    public static String getGlAccountTypeByCostAccounting(Delegator delegator, String costAccountingId) throws GenericEntityException {
        GenericValue costAccounting = delegator.findOne("CostAccounting", UtilMisc.toMap("costAccountingId", costAccountingId), false);
        String costAccBaseId = costAccounting.getString("costAccBaseId");
        GenericValue costAccBase = delegator.findOne("CostAccBase", UtilMisc.toMap("costAccBaseId", costAccBaseId), false);
        String invoiceItemTypeId = costAccBase.getString("invoiceItemTypeId");
        /**
         * PINV_IMPTAX_ITEM - thuế nhập khẩu
         * PINV_IMPCOST_ITEM - chi phí khác
         * PITM_SPEC_TAX - thuế tiêu thụ đặc biệt
         */
        if("PINV_IMPTAX_ITEM".equals(invoiceItemTypeId)) {
            return "PINV_IMPTAX_ITEM";
        }
        else if("PITM_SPEC_TAX".equals(invoiceItemTypeId)) {
            return "PITM_SPEC_TAX";
        }
        else {
            return "PINV_IMPCOST_ITEM";
        }
    }
}
