package com.olbius.basepos.invoice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.accounting.invoice.InvoiceServices;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PosInvoiceServices extends InvoiceServices {
	private static String resource_error = "BasePosErrorUiLabels";
	public static String module = PosInvoiceServices.class.getName();
	
    /* Service to create an invoice for an order */
    public static Map<String, Object> createInvoiceForOrder(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        if (DECIMALS == -1 || ROUNDING == -1) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "AccountingAritmeticPropertiesNotConfigured", locale));
        }

        String orderId = (String) context.get("orderId");
        List<GenericValue> billItems = UtilGenerics.checkList(context.get("billItems"));
        String invoiceId = (String) context.get("invoiceId");

        if (UtilValidate.isEmpty(billItems)) {
            Debug.logVerbose("No order items to invoice; not creating invoice; returning success", module);
            return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, 
                    "AccountingNoOrderItemsToInvoice", locale));
        }

        try {
            GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "AccountingNoOrderHeader", locale));
            }

            // figure out the invoice type
            String invoiceType = null;
            String orderType = orderHeader.getString("orderTypeId");
            if (orderType.equals("SALES_ORDER")) {
                invoiceType = "SALES_INVOICE";
            } else if (orderType.equals("PURCHASE_ORDER")) {
                invoiceType = "PURCHASE_INVOICE";
            }

            // Set the precision depending on the type of invoice
            int invoiceTypeDecimals = UtilNumber.getBigDecimalScale("invoice." + invoiceType + ".decimals");
            if (invoiceTypeDecimals == -1) invoiceTypeDecimals = DECIMALS;

            // Make an order read helper from the order
            OrderReadHelper orh = new OrderReadHelper(orderHeader);

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

            GenericValue billingAccount = orderHeader.getRelatedOne("BillingAccount", false);
            String billingAccountId = billingAccount != null ? billingAccount.getString("billingAccountId") : null;

            Timestamp invoiceDate = (Timestamp)context.get("eventDate");
            if (UtilValidate.isEmpty(invoiceDate)) {
                // TODO: ideally this should be the same time as when a shipment is sent and be passed in as a parameter
                invoiceDate = UtilDateTime.nowTimestamp();
            }
            // TODO: perhaps consider billing account net days term as well?
            Long orderTermNetDays = orh.getOrderTermNetDays();
            Timestamp dueDate = null;
            if (orderTermNetDays != null) {
                dueDate = UtilDateTime.getDayEnd(invoiceDate, orderTermNetDays);
            }

            // create the invoice record
            if (UtilValidate.isEmpty(invoiceId)) {
            	//create new invoice id to createInvoice service don't call getNextInvoiceId service
            	invoiceId = delegator.getNextSeqId("Invoice");
                Map<String, Object> createInvoiceContext = FastMap.newInstance();
                createInvoiceContext.put("partyId", billToCustomerPartyId);
                createInvoiceContext.put("partyIdFrom", billFromVendorPartyId);
                createInvoiceContext.put("billingAccountId", billingAccountId);
                createInvoiceContext.put("invoiceDate", invoiceDate);
                createInvoiceContext.put("dueDate", dueDate);
                createInvoiceContext.put("invoiceTypeId", invoiceType);
                // start with INVOICE_IN_PROCESS, in the INVOICE_READY we can't change the invoice (or shouldn't be able to...)
                createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
                createInvoiceContext.put("currencyUomId", orderHeader.getString("currencyUom"));
                createInvoiceContext.put("userLogin", userLogin);

                // store the invoice first
                Map<String, Object> createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
                if (ServiceUtil.isError(createInvoiceResult)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "AccountingErrorCreatingInvoiceFromOrder", locale), null, null, createInvoiceResult);
                }

                // call service, not direct entity op: delegator.create(invoice);
                invoiceId = (String) createInvoiceResult.get("invoiceId");
            }

            // order roles to invoice roles
            List<GenericValue> orderRoles = orderHeader.getRelated("OrderRole", null, null, false);
            Map<String, Object> createInvoiceRoleContext = FastMap.newInstance();
            createInvoiceRoleContext.put("invoiceId", invoiceId);
            for (GenericValue orderRole : orderRoles) {
                createInvoiceRoleContext.put("partyId", orderRole.getString("partyId"));
                createInvoiceRoleContext.put("roleTypeId", orderRole.getString("roleTypeId"));
                java.util.Date date= new java.util.Date();
                createInvoiceRoleContext.put("datetimePerformed", new Timestamp(date.getTime()));
                GenericValue tmpInvoiceRole = delegator.create("InvoiceRole", createInvoiceRoleContext);
                tmpInvoiceRole.store();
            }

            // billing accounts
            // List billingAccountTerms = null;
            // for billing accounts we will use related information
            if (billingAccount != null) {
                List<GenericValue> billToRoles = billingAccount.getRelated("BillingAccountRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER"), null, false);
                for (GenericValue billToRole : billToRoles) {
                    if (!(billToRole.getString("partyId").equals(billToCustomerPartyId))) {
                        createInvoiceRoleContext = UtilMisc.toMap("invoiceId", invoiceId, "partyId", billToRole.get("partyId"),
                                                                           "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin);
                        Map<String, Object> createInvoiceRoleResult = dispatcher.runSync("createInvoiceRole", createInvoiceRoleContext);
                        if (ServiceUtil.isError(createInvoiceRoleResult)) {
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                    "AccountingErrorCreatingInvoiceRoleFromOrder", locale), null, null, createInvoiceRoleResult);
                        }
                    }
                }

                // set the bill-to contact mech as the contact mech of the billing account
                if (UtilValidate.isNotEmpty(billingAccount.getString("contactMechId"))) {
                    Map<String, Object> createBillToContactMechContext = UtilMisc.toMap("invoiceId", invoiceId, "contactMechId", billingAccount.getString("contactMechId"),
                                                                       "contactMechPurposeTypeId", "BILLING_LOCATION", "userLogin", userLogin);
                    Map<String, Object> createBillToContactMechResult = dispatcher.runSync("createInvoiceContactMech", createBillToContactMechContext);
                    if (ServiceUtil.isError(createBillToContactMechResult)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                "AccountingErrorCreatingInvoiceContactMechFromOrder", locale), null, null, createBillToContactMechResult);
                    }
                }
            } else {
                List<GenericValue> billingLocations = orh.getBillingLocations();
                if (UtilValidate.isNotEmpty(billingLocations)) {
                    for (GenericValue ocm : billingLocations) {
                        Map<String, Object> createBillToContactMechContext = UtilMisc.toMap("invoiceId", invoiceId, "contactMechId", ocm.getString("contactMechId"),
                                                                           "contactMechPurposeTypeId", "BILLING_LOCATION", "userLogin", userLogin);
                        Map<String, Object> createBillToContactMechResult = dispatcher.runSync("createInvoiceContactMech", createBillToContactMechContext);
                        if (ServiceUtil.isError(createBillToContactMechResult)) {
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                    "AccountingErrorCreatingInvoiceContactMechFromOrder", locale), null, null, createBillToContactMechResult);
                        }
                    }
                } else {
                    Debug.logWarning("No billing locations found for order [" + orderId +"] and none were created for Invoice [" + invoiceId + "]", module);
                }
            }

            GenericValue payToAddress = null;
            if (invoiceType.equals("PURCHASE_INVOICE")) {
                // for purchase orders, the pay to address is the BILLING_LOCATION of the vendor
                GenericValue billFromVendor = orh.getPartyFromRole("BILL_FROM_VENDOR");
                if (billFromVendor != null) {
                    List<GenericValue> billingContactMechs = billFromVendor.getRelatedOne("Party", false).getRelated("PartyContactMechPurpose", UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"), null, false);
                    if (UtilValidate.isNotEmpty(billingContactMechs)) {
                        payToAddress = EntityUtil.getFirst(billingContactMechs);
                    }
                }
            } else {
                // for sales orders, it is the payment address on file for the store
                payToAddress = PaymentWorker.getPaymentAddress(delegator, productStore.getString("payToPartyId"));
            }
            if (payToAddress != null) {
                Map<String, Object> createPayToContactMechContext = UtilMisc.toMap("invoiceId", invoiceId, "contactMechId", payToAddress.getString("contactMechId"),
                                                                   "contactMechPurposeTypeId", "PAYMENT_LOCATION", "userLogin", userLogin);
                Map<String, Object> createPayToContactMechResult = dispatcher.runSync("createInvoiceContactMech", createPayToContactMechContext);
                if (ServiceUtil.isError(createPayToContactMechResult)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "AccountingErrorCreatingInvoiceContactMechFromOrder", locale), null, null, createPayToContactMechResult);
                }
            }

            // sequence for items - all OrderItems or InventoryReservations + all Adjustments
            int invoiceItemSeqNum = 1;
            String invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);

            // create the item records
            for (GenericValue currentValue : billItems) {
                GenericValue itemIssuance = null;
                GenericValue orderItem = null;
                GenericValue shipmentReceipt = null;
                if ("ItemIssuance".equals(currentValue.getEntityName())) {
                    itemIssuance = currentValue;
                } else if ("OrderItem".equals(currentValue.getEntityName())) {
                    orderItem = currentValue;
                } else if ("ShipmentReceipt".equals(currentValue.getEntityName())) {
                    shipmentReceipt = currentValue;
                } else {
                    Debug.logError("Unexpected entity " + currentValue + " of type " + currentValue.getEntityName(), module);
                }

                if (orderItem == null && itemIssuance != null) {
                    orderItem = itemIssuance.getRelatedOne("OrderItem", false);
                } else if ((orderItem == null) && (shipmentReceipt != null)) {
                    orderItem = shipmentReceipt.getRelatedOne("OrderItem", false);
                } else if ((orderItem == null) && (itemIssuance == null) && (shipmentReceipt == null)) {
                    Debug.logError("Cannot create invoice when orderItem, itemIssuance, and shipmentReceipt are all null", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "AccountingIllegalValuesPassedToCreateInvoiceService", locale));
                }
                GenericValue product = null;
                if (orderItem.get("productId") != null) {
                    product = orderItem.getRelatedOne("Product", false);
                }

                // get some quantities
                BigDecimal billingQuantity = null;
                if (itemIssuance != null) {
                    billingQuantity = itemIssuance.getBigDecimal("quantity");
                    BigDecimal cancelQty = itemIssuance.getBigDecimal("cancelQuantity");
                    if (cancelQty == null) {
                        cancelQty = ZERO;
                    }
                    billingQuantity = billingQuantity.subtract(cancelQty).setScale(DECIMALS, ROUNDING);
                } else if (shipmentReceipt != null) {
                    billingQuantity = shipmentReceipt.getBigDecimal("quantityAccepted");
                } else {
                    BigDecimal orderedQuantity = OrderReadHelper.getOrderItemQuantity(orderItem);
                    BigDecimal invoicedQuantity = OrderReadHelper.getOrderItemInvoicedQuantity(orderItem);
                    billingQuantity = orderedQuantity.subtract(invoicedQuantity);
                    if (billingQuantity.compareTo(ZERO) < 0) {
                        billingQuantity = ZERO;
                    }
                }
                if (billingQuantity == null) billingQuantity = ZERO;

                // check if shipping applies to this item.  Shipping is calculated for sales invoices, not purchase invoices.
                boolean shippingApplies = false;
            	if ((product != null) && (ProductWorker.shippingApplies(product)) && (invoiceType.equals("SALES_INVOICE"))) {
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
                    BigDecimal orderedQuantity = OrderReadHelper.getOrderItemQuantity(orderItem);
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
                if (itemIssuance != null && itemIssuance.get("inventoryItemId") != null) {
                    itemIssuanceId = itemIssuance.getString("itemIssuanceId");
                    createInvoiceItemContext.put("inventoryItemId", itemIssuance.get("inventoryItemId"));
                }
                // similarly, tax only for purchase invoices
                if ((product != null) && (invoiceType.equals("SALES_INVOICE"))) {
                    createInvoiceItemContext.put("taxableFlag", product.get("taxable"));
                }

                Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
                if (ServiceUtil.isError(createInvoiceItemResult)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "AccountingErrorCreatingInvoiceItemFromOrder", locale), null, null, createInvoiceItemResult);
                }

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
                if ((shipmentReceipt != null) && (shipmentReceipt.getString("receiptId") != null)) {
                    createOrderItemBillingContext.put("shipmentReceiptId", shipmentReceipt.getString("receiptId"));
                }

                Map<String, Object> createOrderItemBillingResult = dispatcher.runSync("createOrderItemBilling", createOrderItemBillingContext);
                if (ServiceUtil.isError(createOrderItemBillingResult)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "AccountingErrorCreatingOrderItemBillingFromOrder", locale), null, null, createOrderItemBillingResult);
                }

                if ("ItemIssuance".equals(currentValue.getEntityName())) {
                    List<GenericValue> shipmentItemBillings = delegator.findByAnd("ShipmentItemBilling", UtilMisc.toMap("shipmentId", currentValue.get("shipmentId")), null, false);
                    if (UtilValidate.isEmpty(shipmentItemBillings)) {
                        // create the ShipmentItemBilling record
                        GenericValue shipmentItemBilling = delegator.makeValue("ShipmentItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
                        shipmentItemBilling.put("shipmentId", currentValue.get("shipmentId"));
                        shipmentItemBilling.put("shipmentItemSeqId", currentValue.get("shipmentItemSeqId"));
                        shipmentItemBilling.create();
                    }
                }

                String parentInvoiceItemSeqId = invoiceItemSeqId;
                // increment the counter
                invoiceItemSeqNum++;
                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);

                // Get the original order item from the DB, in case the quantity has been overridden
                GenericValue originalOrderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId")), false);

                // create the item adjustment as line items
                List<GenericValue> itemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orh.getAdjustments());
                for (GenericValue adj : itemAdjustments) {

                    // Check against OrderAdjustmentBilling to see how much of this adjustment has already been invoiced
                    BigDecimal adjAlreadyInvoicedAmount = null;
                    try {
                        Map<String, Object> checkResult = dispatcher.runSync("calculateInvoicedAdjustmentTotal", UtilMisc.toMap("orderAdjustment", adj));
                        adjAlreadyInvoicedAmount = (BigDecimal) checkResult.get("invoicedTotal");
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Accounting trouble calling calculateInvoicedAdjustmentTotal service", module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                "AccountingTroubleCallingCalculateInvoicedAdjustmentTotalService", locale));
                    }

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
                            try {
                                GenericValue productPromo = adj.getRelatedOne("ProductPromo", false);
                                if (UtilValidate.isNotEmpty(productPromo.getString("overrideOrgPartyId"))) {
                                    createInvoiceItemAdjContext.put("overrideOrgPartyId", productPromo.getString("overrideOrgPartyId"));
                                }
                            } catch (GenericEntityException e) {
                                Debug.logError(e, "Error looking up ProductPromo with id [" + adj.getString("productPromoId") + "]", module);
                            }
                        }

                        Map<String, Object> createInvoiceItemAdjResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemAdjContext);
                        if (ServiceUtil.isError(createInvoiceItemAdjResult)) {
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                    "AccountingErrorCreatingInvoiceItemFromOrder", locale), null, null, createInvoiceItemAdjResult);
                        }

                        // Create the OrderAdjustmentBilling record
                        Map<String, Object> createOrderAdjustmentBillingContext = FastMap.newInstance();
                        createOrderAdjustmentBillingContext.put("orderAdjustmentId", adj.getString("orderAdjustmentId"));
                        createOrderAdjustmentBillingContext.put("invoiceId", invoiceId);
                        createOrderAdjustmentBillingContext.put("invoiceItemSeqId", invoiceItemSeqId);
                        createOrderAdjustmentBillingContext.put("amount", amount);
                        createOrderAdjustmentBillingContext.put("userLogin", userLogin);

                        Map<String, Object> createOrderAdjustmentBillingResult = dispatcher.runSync("createOrderAdjustmentBilling", createOrderAdjustmentBillingContext);
                        if (ServiceUtil.isError(createOrderAdjustmentBillingResult)) {
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                    "AccountingErrorCreatingOrderAdjustmentBillingFromOrder", locale), null, null, createOrderAdjustmentBillingContext);
                        }

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
                try {
                    Map<String, Object> checkResult = dispatcher.runSync("calculateInvoicedAdjustmentTotal", UtilMisc.toMap("orderAdjustment", adj));
                    adjAlreadyInvoicedAmount = ((BigDecimal) checkResult.get("invoicedTotal")).setScale(invoiceTypeDecimals, ROUNDING);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Accounting trouble calling calculateInvoicedAdjustmentTotal service", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "AccountingTroubleCallingCalculateInvoicedAdjustmentTotalService", locale));                    
                }

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
                    calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, orderSubTotal, invoiceSubTotal,
                            adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING), invoiceTypeDecimals, ROUNDING, userLogin, dispatcher, locale);
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
                            invoiceTypeDecimals, ROUNDING, userLogin, dispatcher, locale);
                } else {

                    // Pro-rate the shipping amount based on shippable information
                    BigDecimal divisor = shippableAmount;
                    BigDecimal multiplier = invoiceShipProRateAmount;

                    // The base amount in this case is the adjustment amount, since we want to prorate based on the full amount
                    BigDecimal baseAmount = adj.getBigDecimal("amount").setScale(invoiceTypeDecimals, ROUNDING);
                    calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, divisor, multiplier, 
                            baseAmount, invoiceTypeDecimals, ROUNDING, userLogin, dispatcher, locale);
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
                             divisor, multiplier, baseAmount, TAX_DECIMALS, TAX_ROUNDING, userLogin, dispatcher, locale);
                } else {

                    // Pro-rate the tax amount based on shippable information
                    BigDecimal divisor = orderSubTotal;
                    BigDecimal multiplier = invoiceSubTotal;

                    // The base amount in this case is the adjustment amount, since we want to prorate based on the full amount
                    BigDecimal baseAmount = adj.getBigDecimal("amount");
                    adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId,
                            divisor, multiplier, baseAmount, TAX_DECIMALS, TAX_ROUNDING, userLogin, dispatcher, locale);
                }
                invoiceSubTotal = invoiceSubTotal.add(adjAmount).setScale(invoiceTypeDecimals, ROUNDING);

                // Increment the counter
                invoiceItemSeqNum++;
                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
            }

            // check for previous order payments
            List<EntityExpr> paymentPrefConds = UtilMisc.toList(
                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
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
                    appl.put("billingAccountId", billingAccountId);
                    appl.put("amountApplied", notApplied);
                    appl.put("userLogin", userLogin);
                    Map<String, Object> createPayApplResult = dispatcher.runSync("createPaymentApplicationPos", appl);
                    if (ServiceUtil.isError(createPayApplResult)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                "AccountingErrorCreatingInvoiceFromOrder", locale), null, null, createPayApplResult);
                    }
                }
            }
            	
            //String nextStatusId = "PURCHASE_INVOICE".equals(invoiceType) ? "INVOICE_IN_PROCESS" : "INVOICE_READY";
            //change status of invoice into ready
            Map<String, Object> setStatusInvoicePosMap = FastMap.newInstance();
            setStatusInvoicePosMap.put("invoiceId", invoiceId);
            setStatusInvoicePosMap.put("statusId", "INVOICE_READY");
            setStatusInvoicePosMap.put("userLogin", userLogin);
            Map<String, Object> setInvoiceStatusPos = dispatcher.runSync("setInvoiceStatusPos", setStatusInvoicePosMap);
            if(ServiceUtil.isError(setInvoiceStatusPos)){
            	Debug.logError("Can not change status of invoice: " + invoiceId, module);
            	String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotChangeStatusOfInvoice", UtilMisc.toMap("invoiceId", invoiceId) ,locale);
            	return ServiceUtil.returnError(errorMessage);
            }
            // recording accounting for invoice
            //change status of invoice into paid
            Map<String, Object> setStatusInvoicePosPaidMap = FastMap.newInstance();
            setStatusInvoicePosPaidMap.put("invoiceId", invoiceId);
            setStatusInvoicePosPaidMap.put("statusId", "INVOICE_PAID");
            setStatusInvoicePosPaidMap.put("userLogin", userLogin);
            Map<String, Object> setInvoiceStatusPosPaid = dispatcher.runSync("setInvoiceStatusPos", setStatusInvoicePosPaidMap);
            if(ServiceUtil.isError(setInvoiceStatusPosPaid)){
            	Debug.logError("Can not change status of invoice: " + invoiceId, module);
            	String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotChangeStatusOfInvoice", UtilMisc.toMap("invoiceId", invoiceId) ,locale);
            	return ServiceUtil.returnError(errorMessage);
            }
            // recording accounting for invoice
            if(invoiceType.equals("SALES_INVOICE")){
            	Map<String, Object> createAcctgTransForSalesInvoiceMap = FastMap.newInstance();
            	createAcctgTransForSalesInvoiceMap.put("userLogin", userLogin);
            	createAcctgTransForSalesInvoiceMap.put("invoiceId", invoiceId);
            }
            
            Map<String, Object> resp = ServiceUtil.returnSuccess();
            resp.put("invoiceId", invoiceId);
            resp.put("invoiceTypeId", invoiceType);
            return resp;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity/data problem creating invoice from order items: " + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "AccountingEntityDataProblemCreatingInvoiceFromOrderItems",
                    UtilMisc.toMap("reason", e.toString()), locale));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service/other problem creating invoice from order items: " + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "AccountingServiceOtherProblemCreatingInvoiceFromOrderItems",
                    UtilMisc.toMap("reason", e.toString()), locale));
        }
    }
}
