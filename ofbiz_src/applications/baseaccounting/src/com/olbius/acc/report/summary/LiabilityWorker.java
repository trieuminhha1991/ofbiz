package com.olbius.acc.report.summary;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by user on 10/18/18.
 */
public class LiabilityWorker {

    public static BigDecimal getTotalAmountPaid(Delegator delegator, Timestamp fromDate, Timestamp thruDate, String partyIdFrom, String partyIdTo) throws GenericEntityException {
        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
        conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
        conds.add(EntityCondition.makeCondition("partyId", partyIdTo));
        conds.add(EntityCondition.makeCondition("paymentStatusId", EntityOperator.IN, UtilMisc.toList("PMNT_CONFIRMED", "PMNT_RECEIVED", "PMNT_SENT")));
        EntityCondition statusConds = EntityCondition.makeCondition("invoiceStatusId", EntityJoinOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF"));
        BigDecimal amountPaid = BigDecimal.ZERO;
        List<GenericValue> listPaymentPaid = delegator.findList("InvoiceAndTotalAndPaymentView", EntityCondition.makeCondition(EntityCondition.makeCondition(conds),
                EntityJoinOperator.AND, statusConds), null, null, null, false);
        for(GenericValue item: listPaymentPaid){
            BigDecimal amount = item.getBigDecimal("amountApplied");
            amountPaid = amountPaid.add(amount);
        }
        return amountPaid;
    }

    public static BigDecimal getTotalAmount(Delegator delegator, Timestamp fromDate, Timestamp thruDate, String partyIdFrom, String partyIdTo) throws GenericEntityException {
        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
        conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
        conds.add(EntityCondition.makeCondition("partyId", partyIdTo));
        EntityCondition statusConds = EntityCondition.makeCondition("invoiceStatusId", EntityJoinOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF"));
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<GenericValue> listInvoice = delegator.findList("InvoiceAndTotalAmountView", EntityCondition.makeCondition(EntityCondition.makeCondition(conds),
                EntityJoinOperator.AND, statusConds), null, null, null, false);
        for(GenericValue item: listInvoice){
            BigDecimal amount = item.getBigDecimal("totalAmount");
            totalAmount = totalAmount.add(amount);
        }
        return totalAmount;
    }

    public static BigDecimal getTotalAmountUnpaid(Delegator delegator, Timestamp fromDate, Timestamp thruDate, String partyIdFrom, String partyIdTo) throws GenericEntityException {
        BigDecimal totalAmount = getTotalAmount(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);
        BigDecimal paidAmount = getTotalAmountPaid(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);
        return totalAmount.subtract(paidAmount);
    }

    public static BigDecimal getTotalAmountOther(Delegator delegator, Timestamp fromDate, Timestamp thruDate, String partyIdFrom, String partyIdTo) throws GenericEntityException {
        List<String> productType = UtilMisc.toList("INV_FPRODEXT_ITEM",
                "INV_SPROD_ITEM","INV_FPRODINT_ITEM","PINV_FPROD_ITEM","PINV_FPRODMEDI_ITEM","PINV_SPROD_ITEM");
        productType.add("PINV_FPRODPRICE_ITEM");
        productType.add("PINV_COUPON_ITEM");
        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
        conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
        conds.add(EntityCondition.makeCondition("partyId", partyIdTo));
        conds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF")));
        List<EntityCondition> otherConds = FastList.newInstance();
        EntityCondition otherCond1 = EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, productType);
        otherConds.add(otherCond1);
        List<GenericValue> listInvoiceItemOther = delegator.findList("InvoiceItemView", EntityCondition.makeCondition(EntityCondition.makeCondition(conds), EntityOperator.AND, EntityCondition.makeCondition(otherConds)), null, null, null, false);
        BigDecimal totalAmountOther = BigDecimal.ZERO;
        for(GenericValue item: listInvoiceItemOther){
            BigDecimal tempQuantity = item.get("quantity") != null? item.getBigDecimal("quantity") : BigDecimal.ONE;
            BigDecimal tempAmount = item.get("amount") != null? item.getBigDecimal("amount") : BigDecimal.ZERO;
            totalAmountOther = totalAmountOther.add(tempQuantity.multiply(tempAmount));
        }
        return totalAmountOther;
    }

    public static BigDecimal getReturnSupplierAmount(Delegator delegator, Timestamp fromDate, Timestamp thruDate, String partyIdFrom, String partyIdTo, String invoiceTypeId) throws GenericEntityException {
        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
        conds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
        conds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
        conds.add(EntityCondition.makeCondition("partyId", partyIdTo));
        conds.add(EntityCondition.makeCondition("invoiceTypeId", invoiceTypeId));
        EntityCondition statusConds = EntityCondition.makeCondition("invoiceStatusId", EntityJoinOperator.NOT_IN, UtilMisc.toList("INVOICE_CANCELLED", "INVOICE_WRITEOFF"));
        BigDecimal returnSupplierAmount = BigDecimal.ZERO;
        List<GenericValue> listInvoice = delegator.findList("InvoiceAndTotalAmountView", EntityCondition.makeCondition(EntityCondition.makeCondition(conds),
                EntityJoinOperator.AND, statusConds), null, null, null, false);
        for(GenericValue item: listInvoice){
            BigDecimal amount = item.getBigDecimal("totalAmount");
            returnSupplierAmount = returnSupplierAmount.add(amount);
        }
        return returnSupplierAmount;
    }

}