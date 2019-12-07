package com.olbius.basepos.lean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class InvoiceWorker {
	private static final int DECIMALS = UtilNumber.getBigDecimalScale("invoice.decimals");
	private static final int ROUNDING = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
    private static final int TAX_DECIMALS = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    private static final int TAX_ROUNDING = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int INVOICE_ITEM_SEQUENCE_ID_DIGITS = 5; // this is the number of digits used for invoiceItemSeqId: 00001, 00002...
    public static final String resource = "AccountingUiLabels";
    
    private AccountingWorker accountingWorker;
	private DataWorker dataWorker;
	
	public InvoiceWorker(DataWorker dataWorker, AccountingWorker accountingWorker){
		this.dataWorker = dataWorker;
		this.accountingWorker = accountingWorker;
	}
	
	public void createInvoice() throws Exception {
        Delegator delegator = dataWorker.delegator;
        GenericValue userLogin = dataWorker.posSession.getUserLogin();
        GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", dataWorker.cart.getOrderId()), false);
        if(orderHeader == null){
        	return;
        }
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        List<GenericValue> billItems = orh.getOrderItems();
        if (DECIMALS == -1 || ROUNDING == -1) {
            throw new Exception(UtilProperties.getMessage(resource,"AccountingAritmeticPropertiesNotConfigured", dataWorker.posSession.getLocale()));
        }
        // figure out the invoice type
        String invoiceType = "SALES_INVOICE";

        // Set the precision depending on the type of invoice
        int invoiceTypeDecimals = UtilNumber.getBigDecimalScale("invoice." + invoiceType + ".decimals");
        if (invoiceTypeDecimals == -1) invoiceTypeDecimals = DECIMALS;

        // get the product store
        GenericValue productStore = orh.getProductStore();

        // get the shipping adjustment mode (Y = Pro-Rate; N = First-Invoice)
        String prorateShipping = productStore != null ? productStore.getString("prorateShipping") : "Y";
        if (prorateShipping == null) {
            prorateShipping = "Y";
        }

        // get the billing parties
        String billToCustomerPartyId = orh.getBillToParty().getString("partyId");
        String billFromVendorPartyId = orh.getBillFromParty().getString("partyId");

        // get some price totals
        BigDecimal shippableAmount = orh.getShippableTotal(null);
        BigDecimal orderSubTotal = orh.getOrderItemsSubTotal();

        // these variables are for pro-rating order amounts across invoices, so they should not be rounded off for maximum accuracy
        BigDecimal invoiceShipProRateAmount = ZERO;
        BigDecimal invoiceSubTotal = ZERO;
        BigDecimal invoiceQuantity = ZERO;

        Timestamp invoiceDate = UtilDateTime.nowTimestamp();

        String invoiceId = delegator.getNextSeqId("Invoice");
        // Create invoice directly
        GenericValue invoice = delegator.makeValue("Invoice");
        invoice.set("invoiceId", invoiceId);
        invoice.set("invoiceDate", invoiceDate);
        invoice.set("partyId", billToCustomerPartyId);
        invoice.set("partyIdFrom", billFromVendorPartyId);
        invoice.set("invoiceTypeId", invoiceType);
        invoice.set("statusId", "INVOICE_IN_PROCESS");
        invoice.set("currencyUomId", orderHeader.getString("currencyUom"));
        invoice.create();
        // Create InvoiceStatus
        GenericValue invoiceStatus = delegator.makeValue("InvoiceStatus");
        invoiceStatus.set("invoiceId", invoiceId);
        invoiceStatus.set("statusId", "INVOICE_IN_PROCESS");
        invoiceStatus.set("statusDate", invoiceDate);
        invoiceStatus.create();

        // order roles to invoice roles
        List<GenericValue> orderRoles = orderHeader.getRelated("OrderRole", null, null, false);
        List<GenericValue> toBeStored = new ArrayList<GenericValue>();
        for (GenericValue orderRole : orderRoles) {
            java.util.Date date= new java.util.Date();
            Map<String, Object> changeFields = new HashMap<String, Object>();
            changeFields.put("invoiceId", invoiceId);
            changeFields.put("partyId", orderRole.getString("partyId"));
            changeFields.put("roleTypeId", orderRole.getString("roleTypeId"));
            changeFields.put("datetimePerformed", new Timestamp(date.getTime()));
            toBeStored.add(delegator.makeValue("InvoiceRole", changeFields));
        }
        delegator.storeAll(toBeStored);
        // sequence for items - all OrderItems or InventoryReservations + all Adjustments
        int invoiceItemSeqNum = 1;
        String invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);

        // create the item records
        for (GenericValue currentValue : billItems) {
            GenericValue orderItem = null;
            orderItem = currentValue;

            GenericValue product = null;
            if (orderItem.get("productId") != null) {
                product = orderItem.getRelatedOne("Product", false);
            }

            // get some quantities
            BigDecimal billingQuantity = null;
            BigDecimal orderedQuantity = OrderReadHelper.getOrderItemQuantity(orderItem);
            BigDecimal invoicedQuantity = OrderReadHelper.getOrderItemInvoicedQuantity(orderItem);
            billingQuantity = orderedQuantity.subtract(invoicedQuantity);
            if (billingQuantity.compareTo(ZERO) < 0) {
                billingQuantity = ZERO;
            }
            if (billingQuantity == null) billingQuantity = ZERO;

            // check if shipping applies to this item.  Shipping is calculated for sales invoices, not purchase invoices.
            boolean shippingApplies = false;
        	if ((product != null) && (ProductWorker.shippingApplies(product))) {
                shippingApplies = true;
            }
            
            BigDecimal billingAmount = orderItem.getBigDecimal("unitPrice").setScale(invoiceTypeDecimals, ROUNDING);
            
            if (UtilValidate.isNotEmpty(orderItem.get("alternativeUnitPrice"))) {
                //Calculate precise unit price of the order item (unit price stored in order item is rounded and the invoice amount may not be equal to order if this one is used)
                BigDecimal totalPrice = orderItem.getBigDecimal("alternativeUnitPrice");
                BigDecimal alternativeQuantity = orderItem.getBigDecimal("alternativeQuantity");
                if (UtilValidate.isNotEmpty(alternativeQuantity)) {
                    totalPrice = totalPrice.multiply(alternativeQuantity);
                }
                orderedQuantity = OrderReadHelper.getOrderItemQuantity(orderItem);
                billingAmount = totalPrice.divide(orderedQuantity, 100, ROUNDING);
            }
            
            Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
            createInvoiceItemContext.put("invoiceId", invoiceId);
            createInvoiceItemContext.put("invoiceItemSeqId", invoiceItemSeqId);
            String strIsPromo = orderItem.getString("isPromo");
            if (strIsPromo.equals("Y"))                	
            	createInvoiceItemContext.put("invoiceItemTypeId", getInvoiceItemType(delegator, (orderItem.getString("orderItemTypeId")), "FININTER_GOOD", invoiceType, "INV_FPROD_ITEM"));
            else
            	createInvoiceItemContext.put("invoiceItemTypeId", getInvoiceItemType(delegator, (orderItem.getString("orderItemTypeId")), (product == null ? null : product.getString("productTypeId")), invoiceType, "INV_FPROD_ITEM"));
            createInvoiceItemContext.put("description", orderItem.get("itemDescription"));
            createInvoiceItemContext.put("quantity", billingQuantity);
            createInvoiceItemContext.put("amount", billingAmount);
            createInvoiceItemContext.put("productId", orderItem.get("productId"));
            createInvoiceItemContext.put("productFeatureId", orderItem.get("productFeatureId"));
            createInvoiceItemContext.put("overrideGlAccountId", orderItem.get("overrideGlAccountId"));
            createInvoiceItemContext.put("userLogin", userLogin);
            String itemIssuanceId = null;
            // similarly, tax only for purchase invoices
            if (product != null) {
                createInvoiceItemContext.put("taxableFlag", product.get("taxable"));
            }

            dataWorker.dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);

            // this item total
            BigDecimal thisAmount = billingAmount.multiply(billingQuantity).setScale(invoiceTypeDecimals, ROUNDING);

            // add to the ship amount only if it applies to this item
            if (shippingApplies) {
                invoiceShipProRateAmount = invoiceShipProRateAmount.add(thisAmount).setScale(invoiceTypeDecimals, ROUNDING);
            }

            // increment the invoice subtotal
            invoiceSubTotal = invoiceSubTotal.add(thisAmount).setScale(100, ROUNDING);

            // increment the invoice quantity
            invoiceQuantity = invoiceQuantity.add(billingQuantity).setScale(invoiceTypeDecimals, ROUNDING);

            // create the OrderItemBilling record
            Map<String, Object> createOrderItemBillingContext = FastMap.newInstance();
            createOrderItemBillingContext.put("invoiceId", invoiceId);
            createOrderItemBillingContext.put("invoiceItemSeqId", invoiceItemSeqId);
            createOrderItemBillingContext.put("orderId", orderItem.get("orderId"));
            createOrderItemBillingContext.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            createOrderItemBillingContext.put("itemIssuanceId", itemIssuanceId);
            createOrderItemBillingContext.put("quantity", billingQuantity);
            createOrderItemBillingContext.put("amount", billingAmount);
            createOrderItemBillingContext.put("userLogin", userLogin);

            dataWorker.dispatcher.runSync("createOrderItemBilling", createOrderItemBillingContext);

            String parentInvoiceItemSeqId = invoiceItemSeqId;
            // increment the counter
            invoiceItemSeqNum++;
            invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);

            // Get the original order item from the DB, in case the quantity has been overridden
            GenericValue originalOrderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", dataWorker.cart.getOrderId(), "orderItemSeqId", orderItem.getString("orderItemSeqId")), false);

            // create the item adjustment as line items
            List<GenericValue> itemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orh.getAdjustments());
            for (GenericValue adj : itemAdjustments) {

                // Check against OrderAdjustmentBilling to see how much of this adjustment has already been invoiced
                BigDecimal adjAlreadyInvoicedAmount = null;
                Map<String, Object> checkResult = dataWorker.dispatcher.runSync("calculateInvoicedAdjustmentTotal", UtilMisc.toMap("orderAdjustment", adj));
                adjAlreadyInvoicedAmount = (BigDecimal) checkResult.get("invoicedTotal");

                // If the absolute invoiced amount >= the abs of the adjustment amount, the full amount has already been invoiced,
                //  so skip this adjustment
                if (adj.get("amount") == null) { // JLR 17/4/7 : fix a bug coming from POS in case of use of a discount (on item(s) or sale, item(s) here) and a cash amount higher than total (hence issuing change)
                    continue;
                }
                if (adjAlreadyInvoicedAmount.abs().compareTo(adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING).abs()) > 0) {
                    continue;
                }

                BigDecimal originalOrderItemQuantity = OrderReadHelper.getOrderItemQuantity(originalOrderItem);
                BigDecimal amount = ZERO;
                if (originalOrderItemQuantity.signum() != 0) {
                    if (adj.get("amount") != null) {
                        // pro-rate the amount
                        // set decimals = 100 means we don't round this intermediate value, which is very important
                        amount = adj.getBigDecimal("amount").divide(originalOrderItemQuantity, 100, ROUNDING);
                        amount = amount.multiply(billingQuantity);
                        // Tax needs to be rounded differently from other order adjustments
                        if (adj.getString("orderAdjustmentTypeId").equals("SALES_TAX")) {
                            amount = amount.setScale(TAX_DECIMALS, TAX_ROUNDING);
                        } else {
                            amount = amount.setScale(invoiceTypeDecimals, ROUNDING);
                        }
                    } else if (adj.get("sourcePercentage") != null) {
                        // pro-rate the amount
                        // set decimals = 100 means we don't round this intermediate value, which is very important
                        BigDecimal percent = adj.getBigDecimal("sourcePercentage");
                        percent = percent.divide(new BigDecimal(100), 100, ROUNDING);
                        amount = billingAmount.multiply(percent);
                        amount = amount.divide(originalOrderItemQuantity, 100, ROUNDING);
                        amount = amount.multiply(billingQuantity);
                        amount = amount.setScale(invoiceTypeDecimals, ROUNDING);
                    }
                }
                if (amount.signum() != 0) {
                    Map<String, Object> createInvoiceItemAdjContext = FastMap.newInstance();
                    createInvoiceItemAdjContext.put("invoiceId", invoiceId);
                    createInvoiceItemAdjContext.put("invoiceItemSeqId", invoiceItemSeqId);
                    createInvoiceItemAdjContext.put("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), null, invoiceType, "INVOICE_ITM_ADJ"));
                    createInvoiceItemAdjContext.put("quantity", BigDecimal.ONE);
                    createInvoiceItemAdjContext.put("amount", amount);
                    createInvoiceItemAdjContext.put("productId", orderItem.get("productId"));
                    createInvoiceItemAdjContext.put("productFeatureId", orderItem.get("productFeatureId"));
                    createInvoiceItemAdjContext.put("overrideGlAccountId", adj.get("overrideGlAccountId"));
                    createInvoiceItemAdjContext.put("parentInvoiceId", invoiceId);
                    createInvoiceItemAdjContext.put("parentInvoiceItemSeqId", parentInvoiceItemSeqId);
                    createInvoiceItemAdjContext.put("userLogin", userLogin);
                    createInvoiceItemAdjContext.put("taxAuthPartyId", adj.get("taxAuthPartyId"));
                    createInvoiceItemAdjContext.put("taxAuthGeoId", adj.get("taxAuthGeoId"));
                    createInvoiceItemAdjContext.put("taxAuthorityRateSeqId", adj.get("taxAuthorityRateSeqId"));

                    // some adjustments fill out the comments field instead
                    String description = (UtilValidate.isEmpty(adj.getString("description")) ? adj.getString("comments") : adj.getString("description"));
                    createInvoiceItemAdjContext.put("description", description);

                    // invoice items for sales tax are not taxable themselves
                    // TODO: This is not an ideal solution. Instead, we need to use OrderAdjustment.includeInTax when it is implemented
                    if (!(adj.getString("orderAdjustmentTypeId").equals("SALES_TAX"))) {
                        createInvoiceItemAdjContext.put("taxableFlag", product.get("taxable"));
                    }

                    // If the OrderAdjustment is associated to a ProductPromo,
                    // and the field ProductPromo.overrideOrgPartyId is set,
                    // copy the value to InvoiceItem.overrideOrgPartyId: this
                    // represent an organization override for the payToPartyId
                    if (UtilValidate.isNotEmpty(adj.getString("productPromoId"))) {
                        GenericValue productPromo = adj.getRelatedOne("ProductPromo", false);
                        if (UtilValidate.isNotEmpty(productPromo.getString("overrideOrgPartyId"))) {
                            createInvoiceItemAdjContext.put("overrideOrgPartyId", productPromo.getString("overrideOrgPartyId"));
                        }
                    }

                    dataWorker.dispatcher.runSync("createInvoiceItem", createInvoiceItemAdjContext);

                    // Create the OrderAdjustmentBilling record
                    Map<String, Object> createOrderAdjustmentBillingContext = FastMap.newInstance();
                    createOrderAdjustmentBillingContext.put("orderAdjustmentId", adj.getString("orderAdjustmentId"));
                    createOrderAdjustmentBillingContext.put("invoiceId", invoiceId);
                    createOrderAdjustmentBillingContext.put("invoiceItemSeqId", invoiceItemSeqId);
                    createOrderAdjustmentBillingContext.put("amount", amount);
                    createOrderAdjustmentBillingContext.put("userLogin", userLogin);

                    dataWorker.dispatcher.runSync("createOrderAdjustmentBilling", createOrderAdjustmentBillingContext);

                    // this adjustment amount
                    BigDecimal thisAdjAmount = amount;

                    // adjustments only apply to totals when they are not tax or shipping adjustments
                    if (!"SALES_TAX".equals(adj.getString("orderAdjustmentTypeId")) &&
                            !"SHIPPING_ADJUSTMENT".equals(adj.getString("orderAdjustmentTypeId"))) {
                        // increment the invoice subtotal
                        invoiceSubTotal = invoiceSubTotal.add(thisAdjAmount).setScale(100, ROUNDING);

                        // add to the ship amount only if it applies to this item
                        if (shippingApplies) {
                            invoiceShipProRateAmount = invoiceShipProRateAmount.add(thisAdjAmount).setScale(invoiceTypeDecimals, ROUNDING);
                        }
                    }

                    // increment the counter
                    invoiceItemSeqNum++;
                    invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
                }
            }
        }

        // create header adjustments as line items -- always to tax/shipping last
        Map<GenericValue, BigDecimal> shipAdjustments = FastMap.newInstance();
        Map<GenericValue, BigDecimal> taxAdjustments = FastMap.newInstance();

        List<GenericValue> headerAdjustments = orh.getOrderHeaderAdjustments();
        for (GenericValue adj : headerAdjustments) {

            // Check against OrderAdjustmentBilling to see how much of this adjustment has already been invoiced
            BigDecimal adjAlreadyInvoicedAmount = null;
            Map<String, Object> checkResult = dataWorker.dispatcher.runSync("calculateInvoicedAdjustmentTotal", UtilMisc.toMap("orderAdjustment", adj));
            adjAlreadyInvoicedAmount = ((BigDecimal) checkResult.get("invoicedTotal")).setScale(invoiceTypeDecimals, ROUNDING);

            // If the absolute invoiced amount >= the abs of the adjustment amount, the full amount has already been invoiced,
            //  so skip this adjustment
            if (null == adj.get("amount")) { // JLR 17/4/7 : fix a bug coming from POS in case of use of a discount (on item(s) or sale, sale here) and a cash amount higher than total (hence issuing change)
                continue;
            }
            if (adjAlreadyInvoicedAmount.abs().compareTo(adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING).abs()) > 0) {
                continue;
            }

            if ("SHIPPING_CHARGES".equals(adj.getString("orderAdjustmentTypeId"))) {
                shipAdjustments.put(adj, adjAlreadyInvoicedAmount);
            } else if ("SALES_TAX".equals(adj.getString("orderAdjustmentTypeId"))) {
                taxAdjustments.put(adj, adjAlreadyInvoicedAmount);
            } else {
                // these will effect the shipping pro-rate (unless commented)
                // other adjustment type
            	// FIXME improve the following function
                calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, orderSubTotal, invoiceSubTotal,
                        adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING), invoiceTypeDecimals, ROUNDING, userLogin, dataWorker.dispatcher);
                // invoiceShipProRateAmount += adjAmount;
                // do adjustments compound or are they based off subtotal? Here we will (unless commented)
                // invoiceSubTotal += adjAmount;

                // increment the counter
                invoiceItemSeqNum++;
                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
            }
        }

        // next do the shipping adjustments.  Note that we do not want to add these to the invoiceSubTotal or orderSubTotal for pro-rating tax later, as that would cause
        // numerator/denominator problems when the shipping is not pro-rated but rather charged all on the first invoice
        for (GenericValue adj : shipAdjustments.keySet()) {
            BigDecimal adjAlreadyInvoicedAmount = shipAdjustments.get(adj);

            if ("N".equalsIgnoreCase(prorateShipping)) {

                // Set the divisor and multiplier to 1 to avoid prorating
                BigDecimal divisor = BigDecimal.ONE;
                BigDecimal multiplier = BigDecimal.ONE;

                // The base amount in this case is the adjustment amount minus the total already invoiced for that adjustment, since
                //  it won't be prorated
                BigDecimal baseAmount = adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING).subtract(adjAlreadyInvoicedAmount);
                calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, divisor, multiplier, baseAmount, 
                        invoiceTypeDecimals, ROUNDING, userLogin, dataWorker.dispatcher);
            } else {

                // Pro-rate the shipping amount based on shippable information
                BigDecimal divisor = shippableAmount;
                BigDecimal multiplier = invoiceShipProRateAmount;

                // The base amount in this case is the adjustment amount, since we want to prorate based on the full amount
                BigDecimal baseAmount = adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING);
                calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, divisor, multiplier, 
                        baseAmount, invoiceTypeDecimals, ROUNDING, userLogin, dataWorker.dispatcher);
            }

            // Increment the counter
            invoiceItemSeqNum++;
            invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
        }

        // last do the tax adjustments
        String prorateTaxes = productStore != null ? productStore.getString("prorateTaxes") : "Y";
        if (prorateTaxes == null) {
            prorateTaxes = "Y";
        }
        for (Map.Entry<GenericValue, BigDecimal> entry : taxAdjustments.entrySet()) {
            GenericValue adj = entry.getKey();
            BigDecimal adjAlreadyInvoicedAmount = entry.getValue();
            BigDecimal adjAmount = null;

            if ("N".equalsIgnoreCase(prorateTaxes)) {

                // Set the divisor and multiplier to 1 to avoid prorating
                BigDecimal divisor = BigDecimal.ONE;
                BigDecimal multiplier = BigDecimal.ONE;

                // The base amount in this case is the adjustment amount minus the total already invoiced for that adjustment, since
                //  it won't be prorated
                BigDecimal baseAmount = adj.getBigDecimal("amount").setScale(TAX_DECIMALS, TAX_ROUNDING).subtract(adjAlreadyInvoicedAmount);
                adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId,
                         divisor, multiplier, baseAmount, TAX_DECIMALS, TAX_ROUNDING, userLogin, dataWorker.dispatcher);
            } else {

                // Pro-rate the tax amount based on shippable information
                BigDecimal divisor = orderSubTotal;
                BigDecimal multiplier = invoiceSubTotal;

                // The base amount in this case is the adjustment amount, since we want to prorate based on the full amount
                BigDecimal baseAmount = adj.getBigDecimal("amount");
                adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId,
                        divisor, multiplier, baseAmount, TAX_DECIMALS, TAX_ROUNDING, userLogin, dataWorker.dispatcher);
            }
            invoiceSubTotal = invoiceSubTotal.add(adjAmount).setScale(invoiceTypeDecimals, ROUNDING);

            // Increment the counter
            invoiceItemSeqNum++;
            invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
        }

        // check for previous order payments
        List<EntityExpr> paymentPrefConds = UtilMisc.toList(
                EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, dataWorker.cart.getOrderId()),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PMNT_VOID"),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
        List<GenericValue> orderPaymentPrefs = delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition(paymentPrefConds, EntityOperator.AND), null, null, null, false);
        List<GenericValue> currentPayments = FastList.newInstance();
        for (GenericValue paymentPref : orderPaymentPrefs) {
            List<GenericValue> payments = paymentPref.getRelated("Payment", null, null, false);
            currentPayments.addAll(payments);
        }
        // apply these payments to the invoice if they have any remaining amount to apply
        for (GenericValue payment : currentPayments) {
            BigDecimal notApplied = PaymentWorker.getPaymentNotApplied(payment);
            if (notApplied.signum() > 0) {
                Map<String, Object> appl = FastMap.newInstance();
                appl.put("paymentId", payment.get("paymentId"));
                appl.put("invoiceId", invoiceId);
                //appl.put("billingAccountId", billingAccountId);
                appl.put("amountApplied", notApplied);
                appl.put("userLogin", userLogin);
                dataWorker.dispatcher.runSync("createPaymentApplicationPos", appl);
            }
        }
        //change status of invoice into ready
        /*Map<String, Object> setStatusInvoicePosMap = FastMap.newInstance();
        setStatusInvoicePosMap.put("invoiceId", invoiceId);
        setStatusInvoicePosMap.put("statusId", "INVOICE_READY");
        setStatusInvoicePosMap.put("userLogin", userLogin);
        dataWorker.dispatcher.runSync("setInvoiceStatusPos", setStatusInvoicePosMap);*/
        // Create Acc transaction
        invoice.set("statusId", "INVOICE_READY");
        invoice.store();
        /*invoiceStatus.set("statusId", "INVOICE_READY"); // This code can be removed
        invoiceStatus.set("statusDate", invoiceDate);
        invoiceStatus.create();*/
        Map<String, Object> setStatusInvoicePosMap = FastMap.newInstance();
        setStatusInvoicePosMap.put("invoiceId", invoiceId);
        setStatusInvoicePosMap.put("userLogin", userLogin);
        //dataWorker.dispatcher.runSync("createAcctgTransForSalesInvoice", setStatusInvoicePosMap);
        accountingWorker.createAcctgTransForSalesInvoice(invoiceId);
        // recording accounting for invoice
        //change status of invoice into paid
        /*Map<String, Object> setStatusInvoicePosPaidMap = FastMap.newInstance();
        setStatusInvoicePosPaidMap.put("invoiceId", invoiceId);
        setStatusInvoicePosPaidMap.put("statusId", "INVOICE_PAID");
        setStatusInvoicePosPaidMap.put("userLogin", userLogin);
        dataWorker.dispatcher.runSync("setInvoiceStatusPos", setStatusInvoicePosPaidMap);*/
        invoice.set("statusId", "INVOICE_PAID");
        invoice.store();
        invoiceStatus.set("statusId", "INVOICE_PAID");
        invoiceStatus.set("statusDate", invoiceDate);
        invoiceStatus.create();
    }
	private String getInvoiceItemType(Delegator delegator, String key1, String key2, String invoiceTypeId, String defaultValue) throws GenericEntityException {
        GenericValue itemMap = null;
        if (UtilValidate.isNotEmpty(key1)) {
            itemMap = delegator.findOne("InvoiceItemTypeMap", UtilMisc.toMap("invoiceItemMapKey", key1, "invoiceTypeId", invoiceTypeId), true);
        }
        if (itemMap == null && UtilValidate.isNotEmpty(key2)) {
            itemMap = delegator.findOne("InvoiceItemTypeMap", UtilMisc.toMap("invoiceItemMapKey", key2, "invoiceTypeId", invoiceTypeId), true);
        }
        if (itemMap != null) {
            return itemMap.getString("invoiceItemTypeId");
        } else {
	            return defaultValue;
        }
    }
	private BigDecimal calcHeaderAdj(Delegator delegator, GenericValue adj, String invoiceTypeId, String invoiceId, String invoiceItemSeqId,
            BigDecimal divisor, BigDecimal multiplier, BigDecimal baseAmount, int decimals, int rounding, GenericValue userLogin, LocalDispatcher dispatcher) throws GenericEntityException {
        BigDecimal adjAmount = ZERO;
        if (adj.get("amount") != null) {

            // pro-rate the amount
            BigDecimal amount = ZERO;
            // make sure the divisor is not 0 to avoid NaN problems; just leave the amount as 0 and skip it in essense
            if (divisor.signum() != 0) {
                // multiply first then divide to avoid rounding errors
                amount = baseAmount.multiply(multiplier).divide(divisor, decimals, rounding);
            }
            if (amount.signum() != 0) {
                Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
                createInvoiceItemContext.put("invoiceId", invoiceId);
                createInvoiceItemContext.put("invoiceItemSeqId", invoiceItemSeqId);
                createInvoiceItemContext.put("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), null, invoiceTypeId, "INVOICE_ADJ"));
                createInvoiceItemContext.put("description", adj.get("description"));
                createInvoiceItemContext.put("quantity", BigDecimal.ONE);
                createInvoiceItemContext.put("amount", amount);
                createInvoiceItemContext.put("overrideGlAccountId", adj.get("overrideGlAccountId"));
                //createInvoiceItemContext.put("productId", orderItem.get("productId"));
                //createInvoiceItemContext.put("productFeatureId", orderItem.get("productFeatureId"));
                //createInvoiceItemContext.put("uomId", "");
                //createInvoiceItemContext.put("taxableFlag", product.get("taxable"));
                createInvoiceItemContext.put("taxAuthPartyId", adj.get("taxAuthPartyId"));
                createInvoiceItemContext.put("taxAuthGeoId", adj.get("taxAuthGeoId"));
                createInvoiceItemContext.put("taxAuthorityRateSeqId", adj.get("taxAuthorityRateSeqId"));
                createInvoiceItemContext.put("userLogin", userLogin);

                Map<String, Object> createInvoiceItemResult = null;
                try {
                    createInvoiceItemResult = dataWorker.dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
                } catch (GenericServiceException e) {
                    return adjAmount;
                }
                if (ServiceUtil.isError(createInvoiceItemResult)) {
                    return adjAmount;
                }

                // Create the OrderAdjustmentBilling record
                Map<String, Object> createOrderAdjustmentBillingContext = FastMap.newInstance();
                createOrderAdjustmentBillingContext.put("orderAdjustmentId", adj.getString("orderAdjustmentId"));
                createOrderAdjustmentBillingContext.put("invoiceId", invoiceId);
                createOrderAdjustmentBillingContext.put("invoiceItemSeqId", invoiceItemSeqId);
                createOrderAdjustmentBillingContext.put("amount", amount);
                createOrderAdjustmentBillingContext.put("userLogin", userLogin);

                try {
                    dataWorker.dispatcher.runSync("createOrderAdjustmentBilling", createOrderAdjustmentBillingContext);
                } catch (GenericServiceException e) {
                    return adjAmount;                }

            }
            amount = amount.setScale(decimals, rounding);
            adjAmount = amount;
        }
        else if (adj.get("sourcePercentage") != null) {
            // pro-rate the amount
            BigDecimal percent = adj.getBigDecimal("sourcePercentage");
            percent = percent.divide(new BigDecimal(100), 100, rounding);
            BigDecimal amount = ZERO;
            // make sure the divisor is not 0 to avoid NaN problems; just leave the amount as 0 and skip it in essense
            if (divisor.signum() != 0) {
                // multiply first then divide to avoid rounding errors
                amount = percent.multiply(divisor);
            }
            if (amount.signum() != 0) {
                Map<String, Object> createInvoiceItemContext = FastMap.newInstance();
                createInvoiceItemContext.put("invoiceId", invoiceId);
                createInvoiceItemContext.put("invoiceItemSeqId", invoiceItemSeqId);
                createInvoiceItemContext.put("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), null, invoiceTypeId, "INVOICE_ADJ"));
                createInvoiceItemContext.put("description", adj.get("description"));
                createInvoiceItemContext.put("quantity", BigDecimal.ONE);
                createInvoiceItemContext.put("amount", amount);
                createInvoiceItemContext.put("overrideGlAccountId", adj.get("overrideGlAccountId"));
                //createInvoiceItemContext.put("productId", orderItem.get("productId"));
                //createInvoiceItemContext.put("productFeatureId", orderItem.get("productFeatureId"));
                //createInvoiceItemContext.put("uomId", "");
                //createInvoiceItemContext.put("taxableFlag", product.get("taxable"));
                createInvoiceItemContext.put("taxAuthPartyId", adj.get("taxAuthPartyId"));
                createInvoiceItemContext.put("taxAuthGeoId", adj.get("taxAuthGeoId"));
                createInvoiceItemContext.put("taxAuthorityRateSeqId", adj.get("taxAuthorityRateSeqId"));
                createInvoiceItemContext.put("userLogin", userLogin);

                Map<String, Object> createInvoiceItemResult = null;
                try {
                    createInvoiceItemResult = dataWorker.dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
                } catch (GenericServiceException e) {
                    return adjAmount;
                }
                if (ServiceUtil.isError(createInvoiceItemResult)) {
                    return adjAmount;
                }

                // Create the OrderAdjustmentBilling record
                Map<String, Object> createOrderAdjustmentBillingContext = FastMap.newInstance();
                createOrderAdjustmentBillingContext.put("orderAdjustmentId", adj.getString("orderAdjustmentId"));
                createOrderAdjustmentBillingContext.put("invoiceId", invoiceId);
                createOrderAdjustmentBillingContext.put("invoiceItemSeqId", invoiceItemSeqId);
                createOrderAdjustmentBillingContext.put("amount", amount);
                createOrderAdjustmentBillingContext.put("userLogin", userLogin);

                try {
                    dataWorker.dispatcher.runSync("createOrderAdjustmentBilling", createOrderAdjustmentBillingContext);
                } catch (GenericServiceException e) {
                    return adjAmount;
                }

            }
            amount = amount.setScale(decimals, rounding);
            adjAmount = amount;
        }
        return adjAmount;
    }
}
