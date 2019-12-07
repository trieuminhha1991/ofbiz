package com.olbius.accounting.ledger;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.rmi.CORBA.Util;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by user on 7/10/18.
 */
public class AcctgTransServices {

    private static final int DECIMALS = UtilNumber.getBigDecimalScale("ledger.decimals");
    private static final int ROUNDING = UtilNumber.getBigDecimalRoundingMode("ledger.rounding");
    private static final int  RECIPROCAL_ITEM_SEQ_ID = 5;
    @SuppressWarnings("unchecked")
    public static Map<String, Object> createAcctgTransForShipmentReceiptForImportTrade(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Map<String, Object> createAcctgTransAndEntriesInMap = FastMap.newInstance();
        try {
            String shipmentReceiptId = (String) context.get("receiptId");
            GenericValue shipmentReceipt = delegator.findOne("ShipmentReceipt", UtilMisc.toMap("receiptId", shipmentReceiptId), false);
            GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", shipmentReceipt.get("orderId"), "orderItemSeqId", shipmentReceipt.get("orderItemSeqId")), false);
            BigDecimal unitPrice = orderItem.getBigDecimal("unitPrice");
            GenericValue inventoryItem = shipmentReceipt.getRelatedOne("InventoryItem", false);
            GenericValue shipment = shipmentReceipt.getRelatedOne("Shipment", false);
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", shipment.get("primaryOrderId")), false);
            GenericValue delivery = EntityUtil.getFirst(delegator.findList("Delivery", EntityCondition.makeCondition("shipmentId", shipment.get("shipmentId")), null, null, null, false));
            BigDecimal conversionFactor = delivery.getBigDecimal("conversionFactor");
            String origCurrencyUomId = orderHeader.getString("currencyUom");
            GenericValue userLogin = (GenericValue) context.get("userLogin");
//            AcctgTransWorkers.createPaidAdvanceIfNotExists(delegator, shipment);
//            Map<BigDecimal, BigDecimal> paidAdvance = AcctgTransWorkers.getExchangedRateFromOrder(ctx, shipment.getString("primaryOrderId"));
            List<GenericValue> acctgTransEntries = FastList.newInstance();
            if(UtilValidate.isNotEmpty(shipmentReceipt.get("transferId"))) {
                Map<String, Object> result = dispatcher.runSync("getPartyAccountingPreferences", UtilMisc.toMap("organizationPartyId", inventoryItem.get("ownerPartyId"), "userLogin", userLogin));
                GenericValue partyAccountingPreference = (GenericValue) result.get("partyAccountingPreference");
                GenericValue shipmentItem = shipmentReceipt.getRelatedOne("ShipmentItem", false);
                Map<String, Object> createDetailMap = FastMap.newInstance();
                createDetailMap.put("inventoryItemId", inventoryItem.get("inventoryItemId"));
                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", inventoryItem.get("productId")), false);
                if(UtilValidate.isNotEmpty(product.get("requireAmount")) && product.getString("requireAmount").equals("Y")) {
                    createDetailMap.put("accountingQuantityDiff", shipmentReceipt.get("amountAccepted"));
                }
                else {
                    createDetailMap.put("accountingQuantityDiff", shipmentReceipt.get("quantityAccepted"));
                }
                dispatcher.runSync("createInventoryItemDetail", createDetailMap);
                BigDecimal accountingQuantityDiff = (BigDecimal) createDetailMap.get("accountingQuantityDiff");
                BigDecimal unitCost = shipmentItem.getBigDecimal("unitCost");
                BigDecimal purCost = shipmentItem.getBigDecimal("purCost");
                BigDecimal totalUnitCost = accountingQuantityDiff.multiply(unitCost).setScale(DECIMALS, ROUNDING);
                BigDecimal totalPurCost = accountingQuantityDiff.multiply(purCost).setScale(DECIMALS, ROUNDING);
                int reciprocalSeqId = 1;
                String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
                creditEntry.set("debitCreditFlag", "C");
                creditEntry.set("glAccountTypeId", "UNINVOICED_SHIP_RCPT");
                creditEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                creditEntry.set("partyId", shipment.get("partyIdFrom"));
                creditEntry.set("roleTypeId", "INVENTORY_TRANSFER");
                creditEntry.set("productId", inventoryItem.get("productId"));
                creditEntry.set("origAmount", totalUnitCost);
                creditEntry.set("origCurrencyUomId", origCurrencyUomId);
                creditEntry.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                creditEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                BigDecimal amount = totalUnitCost.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                creditEntry.set("amount", amount);

                acctgTransEntries.add(creditEntry);
                GenericValue debitEntry = delegator.makeValidValue("AcctgTransEntry");
                debitEntry.set("debitCreditFlag", "D");
                debitEntry.set("glAccountTypeId", "INVENTORY_ACCOUNT");
                debitEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                debitEntry.set("partyId", shipment.get("partyIdFrom"));
                debitEntry.set("roleTypeId", "INVENTORY_TRANSFER");
                debitEntry.set("productId", inventoryItem.get("productId"));
                debitEntry.set("origAmount", totalUnitCost);
                debitEntry.set("origCurrencyUomId", origCurrencyUomId);
                debitEntry.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                amount = totalUnitCost.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                debitEntry.set("amount", amount);
                acctgTransEntries.add(debitEntry);
                if(totalPurCost.compareTo(BigDecimal.ZERO) > 0) {
                    GenericValue creditEntry2 = delegator.makeValidValue("AcctgTransEntry");
                    reciprocalSeqId ++;
                    reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                    creditEntry2.set("debitCreditFlag", "C");
                    creditEntry2.set("glAccountTypeId", "UNINVOICED_SHIP_RCPT");
                    creditEntry2.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                    creditEntry2.set("partyId", shipment.get("partyIdFrom"));
                    creditEntry2.set("roleTypeId", "INVENTORY_TRANSFER");
                    creditEntry2.set("productId", inventoryItem.get("productId"));
                    creditEntry2.set("origAmount", totalPurCost);
                    creditEntry2.set("origCurrencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    creditEntry2.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    creditEntry2.set("amount", totalPurCost.multiply(conversionFactor));
                    creditEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(creditEntry2);

                    GenericValue debitEntry2 = delegator.makeValidValue("AcctgTransEntry");
                    debitEntry2.set("debitCreditFlag", "D");
                    debitEntry2.set("glAccountTypeId", "INV_PUR_ACCOUNT");
                    debitEntry2.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                    debitEntry2.set("partyId", shipment.get("partyIdFrom"));
                    debitEntry2.set("roleTypeId", "INVENTORY_TRANSFER");
                    debitEntry2.set("productId", inventoryItem.get("productId"));
                    debitEntry2.set("origAmount", totalPurCost);
                    debitEntry2.set("origCurrencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    debitEntry2.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    debitEntry.set("amount", totalPurCost.multiply(conversionFactor));
                    debitEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(debitEntry2);
                }

                createAcctgTransAndEntriesInMap.put("glFiscalTypeId", "ACTUAL");
                createAcctgTransAndEntriesInMap.put("acctgTransTypeId", "INVENTORY");
                createAcctgTransAndEntriesInMap.put("shipmentId", shipmentReceipt.get("shipmentId"));
                createAcctgTransAndEntriesInMap.put("partyId", shipment.get("partyIdFrom"));
                createAcctgTransAndEntriesInMap.put("acctgTransEntries", acctgTransEntries);
            }
            else {
                String creditAccountTypeId = "";
                BigDecimal unitCost = BigDecimal.ZERO;
                BigDecimal purCost = BigDecimal.ZERO;
                if(UtilValidate.isNotEmpty(shipmentReceipt.get("returnId"))) {
                    creditAccountTypeId = "COGS_ACCOUNT";
                } else creditAccountTypeId = "UNINVOICED_SHIP_RCPT";
                if("RECEIVE_STOCKEVENT".equals(shipment.get("shipmentTypeId"))) {
                    creditAccountTypeId = "OTHER_STOCKEVENT_RECEI";
                }
                Map<String, Object> result = dispatcher.runSync("getPartyAccountingPreferences", UtilMisc.toMap("organizationPartyId", inventoryItem.get("ownerPartyId"), "userLogin", userLogin));
                GenericValue partyAccountingPreference = (GenericValue) result.get("partyAccountingPreference");
                if(UtilValidate.isNotEmpty(shipmentReceipt.get("returnId"))) {
                        if("COGS_AVG_COST".equals(partyAccountingPreference.get("cogsMethodId"))) {
                            result = dispatcher.runSync("getProductAverageCost", UtilMisc.toMap("inventoryItem", inventoryItem));
                            unitCost = (BigDecimal) result.get("unitCost");
                            purCost = (BigDecimal) result.get("purCost");
                        }
                        if("COGS_INV_COST". equals(partyAccountingPreference.get("cogsMethodId"))) {
                            unitCost = unitPrice;
                            purCost = inventoryItem.getBigDecimal("purCost");
                        }
                }
                else {
                    unitCost = unitPrice;
                    purCost = inventoryItem.getBigDecimal("purCost");
                }
                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", inventoryItem.get("productId")), false);
                BigDecimal accountingQuantity = BigDecimal.ZERO;
                if("Y".equals(product.get("requireAmount"))) {
                    accountingQuantity = shipmentReceipt.getBigDecimal("amountAccepted");
                } else accountingQuantity = shipmentReceipt.getBigDecimal("quantityAccepted");
                BigDecimal origAmount = unitCost.multiply(accountingQuantity).setScale(DECIMALS, ROUNDING);
                BigDecimal origPurAmount = purCost.multiply(accountingQuantity).setScale(DECIMALS, ROUNDING);
                product = delegator.findOne("Product", UtilMisc.toMap("productId", shipmentReceipt.get("productId")), false);
                Map<String, Object> createDetailMap = FastMap.newInstance();
                createDetailMap.put("inventoryItemId", inventoryItem.get("inventoryItemId"));
                createDetailMap.put("accountingQuantityDiff", accountingQuantity);
                createDetailMap.put("userLogin", userLogin);
                dispatcher.runSync("createInventoryItemDetail", createDetailMap);

                int reciprocalSeqId = 1;
                String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
                creditEntry.set("debitCreditFlag", "C");
                creditEntry.set("glAccountTypeId", creditAccountTypeId);
                creditEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                creditEntry.set("partyId", shipment.get("partyIdFrom"));
                creditEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                creditEntry.set("productId", inventoryItem.get("productId"));
                creditEntry.set("origAmount", origAmount);
                creditEntry.set("origCurrencyUomId", origCurrencyUomId);
                creditEntry.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                creditEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                BigDecimal amount = origAmount.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                creditEntry.set("amount", amount);
                acctgTransEntries.add(creditEntry);

                GenericValue productType = delegator.findOne("ProductType", UtilMisc.toMap("productTypeId", product.get("productTypeId")), false);
                GenericValue debitEntry = delegator.makeValidValue("AcctgTransEntry");
                debitEntry.set("debitCreditFlag", "D");
                debitEntry.set("glAccountTypeId", productType.get("glAccountTypeId"));
                debitEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                debitEntry.set("partyId", shipment.get("partyIdFrom"));
                debitEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                debitEntry.set("productId", inventoryItem.get("productId"));
                debitEntry.set("origAmount", origAmount);
                debitEntry.set("origCurrencyUomId", origCurrencyUomId);
                debitEntry.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                amount = origAmount.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                debitEntry.set("amount", amount);
                acctgTransEntries.add(debitEntry);

                if(origPurAmount.compareTo(BigDecimal.ZERO) > 0) {
                    GenericValue creditEntry2 = delegator.makeValue("AcctgTransEntry");
                    reciprocalSeqId ++;
                    reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                    creditEntry2.set("debitCreditFlag", "C");
                    creditEntry2.set("glAccountTypeId", creditAccountTypeId);
                    creditEntry2.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                    creditEntry2.set("partyId", shipment.get("partyIdFrom"));
                    creditEntry2.set("roleTypeId", "BILL_FROM_VENDOR");
                    creditEntry2.set("productId", inventoryItem.get("productId"));
                    creditEntry2.set("origAmount", origPurAmount);
                    creditEntry2.set("origCurrencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    creditEntry2.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    creditEntry2.set("amount", origPurAmount.multiply(conversionFactor));
                    creditEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(creditEntry2);

                    GenericValue debitEntry2 = delegator.makeValue("AcctgTransEntry");
                    debitEntry2.set("debitCreditFlag", "D");
                    debitEntry2.set("glAccountTypeId", "INV_PUR_ACCOUNT");
                    debitEntry2.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
                    debitEntry2.set("partyId", shipment.get("partyIdFrom"));
                    debitEntry2.set("roleTypeId", "BILL_FROM_VENDOR");
                    debitEntry2.set("productId", inventoryItem.get("productId"));
                    debitEntry2.set("origAmount", origPurAmount);
                    debitEntry2.set("origCurrencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    debitEntry2.set("currencyUomId", partyAccountingPreference.get("baseCurrencyUomId"));
                    debitEntry2.set("amount", origPurAmount.multiply(conversionFactor));
                    debitEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(debitEntry2);
                }

                if(UtilValidate.isNotEmpty(shipmentReceipt.get("returnId"))) {
                    GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", shipmentReceipt.get("returnId")), false);
                    if("CUSTOMER_RETURN".equals(returnHeader.get("returnHeaderTypeId"))) {
                        createAcctgTransAndEntriesInMap.put("transactionDate", shipmentReceipt.get("datetimeReceived"));
                    }
                }
                createAcctgTransAndEntriesInMap.put("glFiscalTypeId", "ACTUAL");
                if(!"RECEIVE_EXCHANGED".equals(shipment.get("shipmentTypeId"))) {
                    if(!"RECEIVE_STOCKEVENT".equals(shipment.get("shipmentTypeId"))) {
                        createAcctgTransAndEntriesInMap.put("acctgTransTypeId", "SHIPMENT_RECEIPT");
                    }
                    else createAcctgTransAndEntriesInMap.put("acctgTransTypeId", "REC_STOKEVENT_SHIPMENT");
                }
                else {
                    createAcctgTransAndEntriesInMap.put("acctgTransTypeId", "REC_EXCHANGE_DATE");
                }
                createAcctgTransAndEntriesInMap.put("shipmentId", shipmentReceipt.get("shipmentId"));
                createAcctgTransAndEntriesInMap.put("partyId", shipment.get("partyIdFrom"));
                createAcctgTransAndEntriesInMap.put("acctgTransEntries", acctgTransEntries);
                createAcctgTransAndEntriesInMap.put("userLogin", userLogin);
            }

            Map<String, Object> result = dispatcher.runSync("createAcctgTransAndEntries", createAcctgTransAndEntriesInMap);
            successResult.put("acctgTransId", result.get("acctgTransId"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createAcctgTransForPurchaseInvoiceImportTrade(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String invoiceId = (String) context.get("invoiceId");
        Map<String, Object> createAcctgTransAndEntriesInMap = FastMap.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try {
            List<GenericValue> acctgTransEntries = FastList.newInstance();
            GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
            String glAccountTypeId = "ACCOUNTS_PAYABLE";
            List<String> typeTaxIds = InvoiceWorker.getTaxableInvoiceItemTypeIds(delegator);
            BigDecimal conversionFactorInvoice = invoice.getBigDecimal("conversionFactor");
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, typeTaxIds));
            conds.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INVOICE_ADJ"));
            conds.add(EntityCondition.makeCondition("invoiceId", invoiceId));
            List<GenericValue> invoiceItems = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conds), null, UtilMisc.toList("amount"), null, false);
            GenericValue invoiceType = invoice.getRelatedOne("InvoiceType", false);
            String removeInvoiceItemSeqId = "";
            long reciprocalSeqId = 0;
            for(GenericValue invoiceItem : invoiceItems) {
                BigDecimal conversionFactor = AcctgTransWorkers.getExchangedRateFromShipmentReceipt(delegator, invoiceId);
                if(BigDecimal.ONE.compareTo(conversionFactor) == 0) conversionFactor = conversionFactorInvoice;
                if(invoiceItem.getBigDecimal("amount").compareTo(BigDecimal.ZERO) < 0 && UtilValidate.isNotEmpty(invoiceItem.get("parentInvoiceItemSeqId"))) {
                    BigDecimal quantity= BigDecimal.ONE;
                    if(UtilValidate.isNotEmpty(invoiceItem.get("quantity "))) quantity = invoiceItem.getBigDecimal("quantity");
                    BigDecimal amountFromInvoice = quantity.multiply(invoiceItem.getBigDecimal("amount")).setScale(DECIMALS, ROUNDING);
                    reciprocalSeqId += 1;
                    String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                    GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                    debitEntry.set("debitCreditFlag", "D");
                    debitEntry.set("organizationPartyId", invoice.get("partyId"));
                    debitEntry.set("partyId", invoice.get("partyIdFrom"));
                    debitEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                    debitEntry.set("productId", invoiceItem.get("productId"));
                    debitEntry.set("glAccountTypeId", invoiceItem.get("invoiceItemTypeId"));
                    debitEntry.set("glAccountId", invoiceItem.get("overrideGlAccountId"));
                    debitEntry.set("origAmount", amountFromInvoice);
                    debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                    debitEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
                    debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                    BigDecimal amount = amountFromInvoice.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                    debitEntry.set("amount", amount);
                    acctgTransEntries.add(debitEntry);
                    List<EntityCondition> condsEntry = FastList.newInstance();
                    condsEntry.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, typeTaxIds));
                    condsEntry.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INVOICE_ADJ"));
                    condsEntry.add(EntityCondition.makeCondition("invoiceId", invoiceId));
                    condsEntry.add(EntityCondition.makeCondition("invoiceItemSeqId", invoiceItem.get("parentInvoiceItemSeqId")));
                    GenericValue invoiceItemEntry = EntityUtil.getFirst(delegator.findList("InvoiceItem", EntityCondition.makeCondition(condsEntry), null, UtilMisc.toList("amount"), null, false));
                    quantity = BigDecimal.ONE;
                    if(UtilValidate.isNotEmpty(invoiceItemEntry.get("quantity"))) {
                        quantity = invoiceItemEntry.getBigDecimal("quantity");
                    }
                    BigDecimal origAmountEntry = quantity.multiply(invoiceItemEntry.getBigDecimal("amount"));
                    BigDecimal absOrigAmountEntry = origAmountEntry;
                    if(origAmountEntry.compareTo(BigDecimal.ZERO) < 0)
                        absOrigAmountEntry = origAmountEntry.negate();
                    BigDecimal absAmountFromInvoice = amountFromInvoice;
                    if(amountFromInvoice.compareTo(BigDecimal.ZERO) < 0)
                        absAmountFromInvoice = amountFromInvoice.negate();
                    if(absOrigAmountEntry.compareTo(absAmountFromInvoice) == 0) {
                        removeInvoiceItemSeqId = removeInvoiceItemSeqId + ";" + invoiceItem.getString("parentInvoiceItemSeqId");
                        GenericValue debitEntry2 = delegator.makeValue("AcctgTransEntry");
                        debitEntry2.set("debitCreditFlag", "D");
                        debitEntry2.set("organizationPartyId", invoice.get("partyId"));
                        debitEntry2.set("partyId", invoice.get("partyIdFrom"));
                        debitEntry2.set("roleTypeId", "BILL_FROM_VENDOR");
                        debitEntry2.set("productId", invoiceItemEntry.get("productId"));
                        debitEntry2.set("glAccountTypeId", invoiceItemEntry.get("invoiceItemTypeId"));
                        debitEntry2.set("glAccountId", invoiceItemEntry.get("overrideGlAccountId"));
                        debitEntry2.set("origAmount", origAmountEntry);
                        debitEntry2.set("origCurrencyUomId", invoice.get("currencyUomId"));
                        debitEntry2.set("invoiceItemSeqId", invoiceItemEntry.get("invoiceItemSeqId"));
                        debitEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                        amount = origAmountEntry.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                        debitEntry.set("amount", amount);
                        acctgTransEntries.add(debitEntry);
                    }
                    else {
                        GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
                        creditEntry.set("debitCreditFlag", "C");
                        creditEntry.set("organizationPartyId", invoice.get("partyId"));
                        creditEntry.set("glAccountTypeId", glAccountTypeId);
                        creditEntry.set("origAmount", amountFromInvoice);
                        creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                        creditEntry.set("partyId", invoice.get("partyIdFrom"));
                        creditEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                        creditEntry.set("productId", invoiceItem.get("productId"));
                        creditEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
                        creditEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                        amount = amountFromInvoice.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                        creditEntry.set("amount", amount);
                        acctgTransEntries.add(creditEntry);
                    }
                }
                else {
                    if(!removeInvoiceItemSeqId.contains(invoiceItem.getString("invoiceItemSeqId"))) {
                        reciprocalSeqId = reciprocalSeqId + 1;
                        String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                        BigDecimal amountFromInvoice = BigDecimal.ZERO;
                        BigDecimal quantity = BigDecimal.ZERO;
                        if(UtilValidate.isNotEmpty(invoiceItem.get("quantity")))
                            quantity = invoiceItem.getBigDecimal("quantity");
                        amountFromInvoice = quantity.multiply(invoiceItem.getBigDecimal("amount")).setScale(DECIMALS,ROUNDING);
                        GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                        debitEntry.set("debitCreditFlag", "D");
                        debitEntry.set("organizationPartyId", invoice.get("partyId"));
                        debitEntry.set("partyId", invoice.get("partyIdFrom"));
                        debitEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                        debitEntry.set("productId", invoiceItem.get("productId"));
                        debitEntry.set("glAccountTypeId", invoiceItem.get("invoiceItemTypeId"));
                        debitEntry.set("glAccountId", invoiceItem.get("overrideGlAccountId"));
                        debitEntry.set("origAmount", amountFromInvoice);
                        debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                        debitEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
                        debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                        BigDecimal amount = amountFromInvoice.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                        debitEntry.set("amount", amount);
                        acctgTransEntries.add(debitEntry);

                        GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
                        creditEntry.set("debitCreditFlag", "C");
                        creditEntry.set("organizationPartyId", invoice.get("partyId"));
                        creditEntry.set("glAccountTypeId", glAccountTypeId);
                        creditEntry.set("origAmount", amountFromInvoice);
                        creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                        creditEntry.set("partyId", invoice.get("partyIdFrom"));
                        creditEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                        creditEntry.set("productId", invoiceItem.get("productId"));
                        creditEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
                        creditEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                        amount = amountFromInvoice.multiply(conversionFactor).setScale(DECIMALS, ROUNDING);
                        creditEntry.set("amount", amount);
                        acctgTransEntries.add(creditEntry);
                    }
                }
            }
            Map<String, Set<String>> taxAuthPartyAndGeos = InvoiceWorker.getInvoiceTaxAuthPartyAndGeos(invoice);
            for(String taxAuthPartyId : taxAuthPartyAndGeos.keySet()) {
                Set<String> taxAuthGeoIds = taxAuthPartyAndGeos.get(taxAuthPartyId);
                for(String taxAuthGeoId : taxAuthGeoIds) {
                    List<GenericValue> invoiceItemTaxAuthPartyAndGeos = com.olbius.accounting.invoice.InvoiceWorker.getInvoiceItemTaxAuthPartyAndGeos(invoice, taxAuthPartyId, taxAuthGeoId);
                    GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                    if(UtilValidate.isNotEmpty(invoiceItemTaxAuthPartyAndGeos)) {
                        for(GenericValue invoiceItemTax : invoiceItemTaxAuthPartyAndGeos) {
                            Boolean isFixedAsset = com.olbius.accounting.invoice.InvoiceWorker.isFixedAsset(invoiceItemTax);
                            reciprocalSeqId += 1;
                            String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                            debitEntry.set("debitCreditFlag", "D");
                            debitEntry.set("organizationPartyId", invoice.get("partyId"));
                            BigDecimal quantity = BigDecimal.ONE;
                            if(UtilValidate.isNotEmpty(invoiceItemTax.get("quantity"))) {
                                quantity = invoiceItemTax.getBigDecimal("quantity");
                            }
                            BigDecimal amount = BigDecimal.ZERO;
                            if(UtilValidate.isNotEmpty(invoiceItemTax.get("amount"))) amount = invoiceItemTax.getBigDecimal("amount");
                            BigDecimal taxAmount = quantity.multiply(amount).setScale(DECIMALS, ROUNDING);
                            debitEntry.set("origAmount", taxAmount);
                            debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                            debitEntry.set("partyId", taxAuthPartyId);
                            debitEntry.set("roleTypeId", "TAX_AUTHORITY");
                            GenericValue taxAuthorityRateType = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId", invoiceItemTax.get("taxAuthorityRateSeqId")), false);
                            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", invoiceItemTax.get("productId")), false);
                            GenericValue taxAuthorityRateTypeGlAccount = delegator.findOne("TaxAuthorityRateTypeGlAccount", UtilMisc.toMap("taxAuthorityRateTypeId", taxAuthorityRateType.get("taxAuthorityRateTypeId"),
                                    "organizationPartyId", debitEntry.get("organizationPartyId"),
                                    "invoiceTypeId", invoiceType.get("parentTypeId"),
                                    "productTypeId", product.get("productTypeId")), false);
                            if(UtilValidate.isNotEmpty(taxAuthorityRateTypeGlAccount)) {
                                debitEntry.set("glAccountId", taxAuthorityRateTypeGlAccount.get("glAccountId"));
                            }
                            else debitEntry.set("glAccountTypeId", "VAT_DED_ACCOUNT");
                            if(isFixedAsset) {
                                String typeFATax = "SRT_SALESFA_TAX";
                                GenericValue invoiceItemTypeFaTax = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", typeFATax), false);
                                if(UtilValidate.isNotEmpty(invoiceItemTypeFaTax) && UtilValidate.isNotEmpty(invoiceItemTypeFaTax.get("defaultGlAccountId"))) {
                                    debitEntry.set("glAccountId", invoiceItemTypeFaTax.get("defaultGlAccountId"));
                                }
                            }
                            debitEntry.set("productId", invoiceItemTax.get("productId"));
                            debitEntry.set("invoiceItemSeqId", invoiceItemTax.get("invoiceItemSeqId"));
                            debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                            BigDecimal amountS = taxAmount.multiply(conversionFactorInvoice).setScale(DECIMALS, ROUNDING);
                            debitEntry.set("amount", amountS);
                            acctgTransEntries.add(debitEntry);

                            GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
                            creditEntry.set("debitCreditFlag", "C");
                            creditEntry.set("organizationPartyId", invoice.get("partyId"));
                            creditEntry.set("glAccountTypeId", glAccountTypeId);
                            creditEntry.set("origAmount",taxAmount);
                            creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                            creditEntry.set("partyId", invoice.get("partyIdFrom"));
                            creditEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                            creditEntry.set("productId", invoiceItemTax.get("productId"));
                            creditEntry.set("invoiceItemSeqId", invoiceItemTax.get("invoiceItemSeqId"));
                            creditEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                            amountS = taxAmount.multiply(conversionFactorInvoice).setScale(DECIMALS, ROUNDING);
                            creditEntry.set("amount", amountS);
                            acctgTransEntries.add(creditEntry);
                        }
                    }
                }
            }
            BigDecimal taxAmount = InvoiceWorker.getInvoiceUnattributedTaxTotal(invoice);
            if(UtilValidate.isNotEmpty(taxAmount) && taxAmount.compareTo(BigDecimal.ZERO) > 0) {
                reciprocalSeqId += 1;
                String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                debitEntry.set("debitCreditFlag", "D");
                debitEntry.set("organizationPartyId", invoice.get("partyId"));
                debitEntry.set("origAmount", taxAmount);
                debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                debitEntry.set("glAccountTypeId", "TAX_ACCOUNT");
                debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                BigDecimal amount = taxAmount.multiply(conversionFactorInvoice).setScale(DECIMALS, ROUNDING);
                debitEntry.set("amount", amount);
                acctgTransEntries.add(debitEntry);
                GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
                creditEntry.set("debitCreditFlag", "C");
                creditEntry.set("organizationPartyId", invoice.get("partyId"));
                creditEntry.set("glAccountTypeId", glAccountTypeId);
                creditEntry.set("origAmount", taxAmount);
                creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
                creditEntry.set("partyId", invoice.get("partyIdFrom"));
                creditEntry.set("roleTypeId", "BILL_FROM_VENDOR");
                creditEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                amount = taxAmount.multiply(conversionFactorInvoice).setScale(DECIMALS, ROUNDING);
                creditEntry.set("amount", amount);
                acctgTransEntries.add(creditEntry);
            }
            createAcctgTransAndEntriesInMap.put("glFiscalTypeId", "ACTUAL");
            createAcctgTransAndEntriesInMap.put("acctgTransTypeId", "PURCHASE_INVOICE");
            createAcctgTransAndEntriesInMap.put("invoiceId", invoice.get("invoiceId"));
            createAcctgTransAndEntriesInMap.put("partyId", invoice.get("partyIdFrom"));
            createAcctgTransAndEntriesInMap.put("roleTypeId", "BILL_FROM_VENDOR");
            createAcctgTransAndEntriesInMap.put("acctgTransEntries", acctgTransEntries);
            createAcctgTransAndEntriesInMap.put("userLogin", userLogin);
            Map<String, Object> result = dispatcher.runSync("createAcctgTransAndEntries", createAcctgTransAndEntriesInMap);
            String acctgTransId = (String) result.get("acctgTransId");
            successResult.put("acctgTransId", acctgTransId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String, Object> createAcctgTransAndEntriesForOutgoingPaymentImportTrade(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Map<String, Object> createAcctgTransAndEntriesInMap = FastMap.newInstance();
        List<GenericValue> acctgTransEntries = FastList.newInstance();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String paymentId = (String) context.get("paymentId");
        int reciprocalSeqId = 1;
        BigDecimal amountAppliedTotal = BigDecimal.ZERO;
        try {
            GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
            String organizationPartyId = payment.getString("partyIdFrom");
            String partyId = payment.getString("partyIdTo");
            String roleTypeId = "BILL_FROM_VENDOR";
            GenericValue paymentGlAccountTypeMap = delegator.findOne("PaymentGlAccountTypeMap", UtilMisc.toMap("paymentTypeId", payment.get("paymentTypeId"), "organizationPartyId", organizationPartyId), false);
            String debitGlAccountTypeId = paymentGlAccountTypeMap.getString("glAccountTypeId");
            String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
            GenericValue paymentApplication = EntityUtil.getFirst(delegator.findList("PaymentApplication", EntityCondition.makeCondition("paymentId", paymentId), null, null, null, false));
            String invoiceId = "";
            Boolean isPaymentAdvance = AcctgTransWorkers.isPaymentAdvance(delegator, payment);
            BigDecimal conversionFactorPayment = payment.getBigDecimal("conversionFactor");
            if(UtilValidate.isNotEmpty(paymentApplication) && UtilValidate.isNotEmpty(paymentApplication.get("invoiceId")))
                invoiceId = paymentApplication.getString("invoiceId");

            GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
            creditEntry.set("debitCreditFlag", "C");
            BigDecimal amountFeeBank = BigDecimal.ZERO;
            if("FEE_TAX_BANK_PAYMENT".equals(payment.get("paymentTypeId"))) {
                amountFeeBank = payment.getBigDecimal("amount").subtract(payment.getBigDecimal("taxAmount")).setScale(DECIMALS, ROUNDING);
                creditEntry.set("origAmount", amountFeeBank);
            } else creditEntry.set("origAmount", payment.get("amount"));
            creditEntry.set("origCurrencyUomId", payment.get("currencyUomId"));
            creditEntry.set("organizationPartyId", payment.get("partyIdFrom"));
            creditEntry.set("partyId", payment.get("partyIdTo"));
            creditEntry.set("roleTypeId", "BILL_FROM_VENDOR");
            creditEntry.set("reciprocalSeqId", reciprocalItemSeqId);
            BigDecimal amount = creditEntry.getBigDecimal("origAmount").multiply(conversionFactorPayment);
            creditEntry.set("amount", amount);
            acctgTransEntries.add(creditEntry);
            if("FEE_TAX_BANK_PAYMENT".equals(payment.get("paymentTypeId"))) {
                amount = amountFeeBank.subtract(amountAppliedTotal).setScale(DECIMALS, ROUNDING);
            }
            else amount = payment.getBigDecimal("amount").subtract(amountAppliedTotal).setScale(DECIMALS, ROUNDING);
            if(amount.compareTo(BigDecimal.ZERO) > 0) {
                GenericValue debitEntryWithDiffAmount = delegator.makeValue("AcctgTransEntry");
                debitEntryWithDiffAmount.set("debitCreditFlag", "D");
                debitEntryWithDiffAmount.set("origAmount", amount);
                debitEntryWithDiffAmount.set("origCurrencyUomId", payment.get("currencyUomId"));
                debitEntryWithDiffAmount.set("glAccountId", payment.get("overrideGlAccountId"));
                debitEntryWithDiffAmount.set("glAccountTypeId", debitGlAccountTypeId);
                debitEntryWithDiffAmount.set("partyId", payment.get("partyIdTo"));
                debitEntryWithDiffAmount.set("organizationPartyId", organizationPartyId);
                debitEntryWithDiffAmount.set("reciprocalSeqId", reciprocalItemSeqId);
                BigDecimal amountS = amount.multiply(conversionFactorPayment);
                debitEntryWithDiffAmount.set("amount", amountS);
                acctgTransEntries.add(debitEntryWithDiffAmount);
            }
            if("FEE_TAX_BANK_PAYMENT".equals(payment.get("paymentTypeId"))) {
                reciprocalSeqId += 1;
                reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                GenericValue creditEntry2 = delegator.makeValue("AcctgTransEntry");
                creditEntry2.set("debitCreditFlag", "C");
                creditEntry2.set("origAmount", payment.get("taxAmount"));
                creditEntry2.set("organizationPartyId", payment.get("partyIdFrom"));
                creditEntry2.set("partyId", payment.get("partyIdTo"));
                creditEntry2.set("roleTypeId", "BILL_FROM_VENDOR");
                creditEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                BigDecimal amountS = creditEntry2.getBigDecimal("origAmount").multiply(conversionFactorPayment);
                creditEntry2.set("amount", amountS);
                acctgTransEntries.add(creditEntry2);
                GenericValue debitEntryWithDiffAmount = delegator.makeValue("AcctgTransEntry");
                debitEntryWithDiffAmount.set("debitCreditFlag", "D");
                debitEntryWithDiffAmount.set("origAmount", payment.get("taxAmount"));
                debitEntryWithDiffAmount.set("origCurrencyUomId", payment.get("currencyUomId"));
                debitEntryWithDiffAmount.set("glAccountId", payment.get("overrideGlAccountId"));
                debitEntryWithDiffAmount.set("glAccountTypeId", "TAX_BANK_PAYMENT");
                debitEntryWithDiffAmount.set("organizationPartyId", organizationPartyId);
                debitEntryWithDiffAmount.set("partyId", payment.get("partyIdTo"));
                debitEntryWithDiffAmount.set("reciprocalSeqId", reciprocalItemSeqId);
                amountS = debitEntryWithDiffAmount.getBigDecimal("origAmount").multiply(conversionFactorPayment);
                debitEntryWithDiffAmount.set("amount", amountS);
                acctgTransEntries.add(debitEntryWithDiffAmount);
            }

/*            if(conversionFactorShipmentReceipt.compareTo(conversionFactorPayment) > 0 && !isPaymentAdvance) {
                BigDecimal amountDiff = payment.getBigDecimal("amount").multiply(conversionFactorShipmentReceipt.subtract(conversionFactorPayment)).setScale(DECIMALS, ROUNDING); //Credit 515
                reciprocalSeqId++;
                reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                debitEntry.set("debitCreditFlag", "D");
                debitEntry.set("partyId", payment.get("partyIdTo"));
                debitEntry.set("organizationPartyId", payment.get("partyIdFrom"));
                debitEntry.set("glAccountTypeId", debitGlAccountTypeId);
                debitEntry.set("amount", amountDiff);
                debitEntry.set("origCurrencyUomId", "VND");
                debitEntry.set("origAmount", amountDiff);
                debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                acctgTransEntries.add(debitEntry);

                GenericValue creditEntry2 = delegator.makeValue("AcctgTransEntry");
                creditEntry2.set("debitCreditFlag", "C");
                creditEntry2.set("partyId", payment.get("partyIdTo"));
                creditEntry2.set("organizationPartyId", payment.get("partyIdFrom"));
                creditEntry2.set("glAccountTypeId", "FINCIAL_INCOME");
//                creditEntry.set("glAccountId", "515"); //demo hardcode
                creditEntry2.set("amount", amountDiff);
                creditEntry2.set("origCurrencyUomId", "VND");
                creditEntry2.set("origAmount", amountDiff);
                creditEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                acctgTransEntries.add(creditEntry2);
            }
            else if (conversionFactorShipmentReceipt.compareTo(conversionFactorPayment) < 0 && !isPaymentAdvance) {
                BigDecimal amountDiff = payment.getBigDecimal("amount").multiply(conversionFactorPayment.subtract(conversionFactorShipmentReceipt)).setScale(DECIMALS, ROUNDING); //Credit 515
                reciprocalSeqId++;
                reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                debitEntry.set("debitCreditFlag", "D");
                debitEntry.set("partyId", payment.get("partyIdTo"));
                debitEntry.set("organizationPartyId", payment.get("partyIdFrom"));
                debitEntry.set("glAccountTypeId", "FINACIAL_COST"); //635
                debitEntry.set("amount", amountDiff);
                debitEntry.set("origCurrencyUomId", "VND");
                debitEntry.set("origAmount", amountDiff);
                debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                acctgTransEntries.add(debitEntry);

                //112
                GenericValue creditEntryT = delegator.makeValue("AcctgTransEntry");
                creditEntryT.set("debitCreditFlag", "C");
                creditEntryT.set("partyId", payment.get("partyIdTo"));
                creditEntryT.set("organizationPartyId", payment.get("partyIdFrom"));
                creditEntryT.set("amount", amountDiff);
                creditEntryT.set("origCurrencyUomId", "VND");
                creditEntryT.set("origAmount", amountDiff);
                creditEntryT.set("roleTypeId", "BILL_FROM_VENDOR");
                creditEntryT.set("reciprocalSeqId", reciprocalItemSeqId);
                acctgTransEntries.add(creditEntryT);
            }*/

            createAcctgTransAndEntriesInMap.put("roleTypeId", roleTypeId);
            createAcctgTransAndEntriesInMap.put("glFiscalTypeId", "ACTUAL");
            createAcctgTransAndEntriesInMap.put("acctgTransTypeId", "OUTGOING_PAYMENT");
            createAcctgTransAndEntriesInMap.put("partyId", partyId);
            createAcctgTransAndEntriesInMap.put("paymentId", paymentId);
            createAcctgTransAndEntriesInMap.put("transactionDate", payment.get("paidDate"));
            createAcctgTransAndEntriesInMap.put("acctgTransEntries", acctgTransEntries);
            createAcctgTransAndEntriesInMap.put("userLogin", userLogin);
            Map<String, Object> result = dispatcher.runSync("createAcctgTransAndEntries", createAcctgTransAndEntriesInMap);
            String acctgTransId = (String) result.get("acctgTransId");
            successResult.put("acctgTransId", acctgTransId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String, Object> createExchangedRateFromDocument(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = dispatcher.getDelegator();
        String documentTypeId = (String) context.get("documentTypeId");
        String documentId = "";
        BigDecimal conversionFactor = BigDecimal.ONE;
        String currencyUomId = (String) context.get("currencyUomId");
        GenericValue doc = null;
        if(UtilValidate.isEmpty(context.get("currencyUomId"))) currencyUomId = "USD";
        if (documentTypeId.equals("INVOICE")) {
            documentId = (String) context.get("invoiceId");
            doc = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", documentId), false);
        }
        else if("DELIVERY".equals(documentTypeId)) {
            documentId = (String) context.get("deliveryId");
            doc = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", documentId), false);

        }
        else if("PAYMENT".equals(documentTypeId)) {
            documentId = (String) context.get("paymentId");
            doc = delegator.findOne("Payment", UtilMisc.toMap("paymentId", documentId), false);
        }
        if(UtilValidate.isNotEmpty(doc)) {
            conversionFactor = doc.getBigDecimal("conversionFactor");
        }
        Map<String, Object> exchangedRate = FastMap.newInstance();
        exchangedRate.put("documentId", documentId);
        exchangedRate.put("documentTypeId", documentTypeId);
        exchangedRate.put("currencyUomId", currencyUomId);
        exchangedRate.put("currencyUomIdTo", "VND");
        exchangedRate.put("conversionFactor", conversionFactor);
        exchangedRate.put("userLogin", context.get("userLogin"));
        try {
            dispatcher.runSync("createExchangedRateHistory", exchangedRate);
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> createExchangedRateHistory(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String documentId = (String) context.get("documentId");
            String documentTypeId = (String) context.get("documentTypeId");
            String currencyUomId = (String) context.get("currencyUomId");
            String currencyUomIdTo = (String) context.get("currencyUomIdTo");
            String userLoginId = userLogin.getString("userLoginId");
            BigDecimal conversionFactor = (BigDecimal) context.get("conversionFactor");
            GenericValue exchangedRate = delegator.makeValue("ExchangedRateHistory");
            String id = delegator.getNextSeqId("ExchangedRateHistory");
            exchangedRate.set("exchangedRateId", id);
            exchangedRate.set("documentId", documentId);
            exchangedRate.set("documentTypeId", documentTypeId);
            exchangedRate.set("conversionFactor", conversionFactor);
            exchangedRate.set("currencyUomId", currencyUomId);
            exchangedRate.set("currencyUomIdTo", currencyUomIdTo);
            exchangedRate.set("createdByUserLogin", userLoginId);
            exchangedRate.create();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String, Object> createAcctgTransAndEntriesForOutgoingPaymentDiff(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String paymentId = (String) context.get("paymentId");
        try {
            List<GenericValue> acctgTransEntries = FastList.newInstance();
            GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);

            String defaultCurrencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", delegator);
            if(defaultCurrencyUomId.equals(payment.getString("currencyUomId"))) return successResult;

            BigDecimal conversionFactorPayment = payment.getBigDecimal("conversionFactor");
            List<GenericValue> paymentAppls = delegator.findList("PaymentApplication", EntityCondition.makeCondition("paymentId", paymentId), null, null, null, false);
            String organizationPartyId = payment.getString("partyIdFrom");
            String partyId = payment.getString("partyIdTo");
            String roleTypeId = "BILL_FROM_VENDOR";
            GenericValue paymentGlAccountTypeMap = delegator.findOne("PaymentGlAccountTypeMap", UtilMisc.toMap("paymentTypeId", payment.get("paymentTypeId"), "organizationPartyId", organizationPartyId), false);
            String debitGlAccountTypeId = paymentGlAccountTypeMap.getString("glAccountTypeId");
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            int reciprocalSeqId = 0;
            String reciprocalItemSeqId;
            for (GenericValue appl : paymentAppls) {
                String invoiceId = appl.getString("invoiceId");
                GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
                BigDecimal conversionFactorInvoice = invoice.getBigDecimal("conversionFactor");
                BigDecimal amount = appl.getBigDecimal("amountApplied");
                reciprocalSeqId ++;
                reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
                if(conversionFactorInvoice.compareTo(conversionFactorPayment) > 0) {
                    BigDecimal amountDiff = amount.multiply(conversionFactorInvoice.subtract(conversionFactorPayment)).setScale(DECIMALS, ROUNDING); //Credit 515
                    GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                    debitEntry.set("debitCreditFlag", "D");
                    debitEntry.set("partyId", payment.get("partyIdTo"));
                    debitEntry.set("organizationPartyId", payment.get("partyIdFrom"));
                    debitEntry.set("glAccountTypeId", debitGlAccountTypeId);
                    debitEntry.set("amount", amountDiff);
                    debitEntry.set("origCurrencyUomId", "VND");
                    debitEntry.set("origAmount", amountDiff);
                    debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(debitEntry);

                    GenericValue creditEntry2 = delegator.makeValue("AcctgTransEntry");
                    creditEntry2.set("debitCreditFlag", "C");
                    creditEntry2.set("partyId", payment.get("partyIdTo"));
                    creditEntry2.set("organizationPartyId", payment.get("partyIdFrom"));
                    creditEntry2.set("glAccountTypeId", "FINCIAL_INCOME");
                    String glAccountId = AcctgTransWorkers.getGlAccountIdFromGlAccountType(delegator, "FINCIAL_INCOME", payment.getString("partyIdFrom"));
                    if( glAccountId != null)
                        creditEntry2.set("glAccountId", glAccountId);
                    creditEntry2.set("amount", amountDiff);
                    creditEntry2.set("origCurrencyUomId", "VND");
                    creditEntry2.set("origAmount", amountDiff);
                    creditEntry2.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(creditEntry2);
                }
                else if (conversionFactorInvoice.compareTo(conversionFactorPayment) < 0) {
                    BigDecimal amountDiff = payment.getBigDecimal("amount").multiply(conversionFactorPayment.subtract(conversionFactorInvoice)).setScale(DECIMALS, ROUNDING); //Credit 515
                    GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                    debitEntry.set("debitCreditFlag", "D");
                    debitEntry.set("partyId", payment.get("partyIdTo"));
                    debitEntry.set("organizationPartyId", payment.get("partyIdFrom"));
                    debitEntry.set("glAccountTypeId", "FINACIAL_COST"); //635
                    debitEntry.set("amount", amountDiff);
                    debitEntry.set("origCurrencyUomId", "VND");
                    debitEntry.set("origAmount", amountDiff);
                    debitEntry.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(debitEntry);
                    //112
                    GenericValue creditEntryT = delegator.makeValue("AcctgTransEntry");
                    creditEntryT.set("debitCreditFlag", "C");
                    creditEntryT.set("partyId", payment.get("partyIdTo"));
                    creditEntryT.set("organizationPartyId", payment.get("partyIdFrom"));
                    creditEntryT.set("amount", amountDiff);
                    creditEntryT.set("origCurrencyUomId", "VND");
                    creditEntryT.set("origAmount", amountDiff);
                    creditEntryT.set("roleTypeId", "BILL_FROM_VENDOR");
                    creditEntryT.set("reciprocalSeqId", reciprocalItemSeqId);
                    acctgTransEntries.add(creditEntryT);
                }
            }

            Map<String, Object> createAcctgTransAndEntriesInMap = FastMap.newInstance();
            createAcctgTransAndEntriesInMap.put("roleTypeId", roleTypeId);
            createAcctgTransAndEntriesInMap.put("glFiscalTypeId", "ACTUAL");
            createAcctgTransAndEntriesInMap.put("acctgTransTypeId", "EXCHANGE_RATE_DIFF");
            createAcctgTransAndEntriesInMap.put("partyId", partyId);
            createAcctgTransAndEntriesInMap.put("paymentId", paymentId);
            createAcctgTransAndEntriesInMap.put("transactionDate", payment.get("paidDate"));
            createAcctgTransAndEntriesInMap.put("acctgTransEntries", acctgTransEntries);
            createAcctgTransAndEntriesInMap.put("userLogin", userLogin);
            Map<String, Object> result = dispatcher.runSync("createAcctgTransAndEntries", createAcctgTransAndEntriesInMap);
            String acctgTransId = (String) result.get("acctgTransId");
            successResult.put("acctgTransId", acctgTransId);
        } catch (GenericEntityException | GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }

    public static Map<String, Object> prepareAcctgTransEntry(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        int reciprocalItemSeqDigit = 5;

        Delegator delegator = ctx.getDelegator();
        List<Map<String, Object>> prepareAcctgTransEntryList = FastList.newInstance();
        String orderId = (String) context.get("orderId");
        String productId = (String) context.get("productId");
        long reciprocalItemSeqId = (Long) context.get("reciprocalItemSeqId");
        String organizationPartyId = (String) context.get("organizationPartyId");
        String roleTypeId = (String) context.get("roleTypeId");
        String partyId = (String) context.get("partyId");

        List<EntityCondition> conds = FastList.newInstance();
        conds.add(EntityCondition.makeCondition("orderId", orderId));
        conds.add(EntityCondition.makeCondition("productId", productId));

        try {
            List<GenericValue> costProducts = delegator.findList("CostProductInOrder", EntityCondition.makeCondition(conds), null, null, null, false);
            for(GenericValue costProduct : costProducts) {
                BigDecimal costAmount = costProduct.getBigDecimal("totalCost");
                String currencyUomId = "VND";
                GenericValue creditEntry = delegator.makeValue("AcctgTransEntry");
                String costAccountingId = costProduct.getString("costAccountingId");
                String glAccountTypeId = AcctgTransWorkers.getGlAccountTypeByCostAccounting(delegator, costAccountingId);
                reciprocalItemSeqId = reciprocalItemSeqId + 1;
                String formatPadded = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
                creditEntry.set("debitCreditFlag", "C");
                creditEntry.set("glAccountTypeId", glAccountTypeId);
                creditEntry.set("organizationPartyId", organizationPartyId);
                creditEntry.set("partyId", partyId);
                creditEntry.set("roleTypeId", roleTypeId);
                creditEntry.set("productId", productId);
                creditEntry.set("origAmount", costAmount);
                creditEntry.set("origCurrencyUomId", currencyUomId);
                creditEntry.set("reciprocalSeqId", formatPadded);
                prepareAcctgTransEntryList.add(creditEntry);

                GenericValue debitEntry = delegator.makeValue("AcctgTransEntry");
                debitEntry.set("debitCreditFlag", "D");
                debitEntry.set("glAccountTypeId", "INV_PUR_ACCOUNT");
                debitEntry.set("organizationPartyId", organizationPartyId);
                debitEntry.set("partyId", partyId);
                debitEntry.set("roleTypeId", roleTypeId);
                debitEntry.set("productId", productId);
                debitEntry.set("origAmount", costAmount);
                debitEntry.set("origCurrencyUomId", currencyUomId);
                debitEntry.set("reciprocalSeqId", formatPadded);
                prepareAcctgTransEntryList.add(debitEntry);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        successResult.put("prepareAcctgTransEntryList", prepareAcctgTransEntryList);
        return successResult;
    }
}
