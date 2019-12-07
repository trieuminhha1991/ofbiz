/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import javolution.util.FastList;
import javolution.util.FastMap;



invoiceId = parameters.get("invoiceId");
invoice = delegator.findOne("Invoice", [invoiceId : invoiceId], false);
context.invoice = invoice;
/*
listVoucher = delegator.findByAnd("InvoiceVoucherView", [invoiceId : invoiceId],null,false);
if(listVoucher != null && listVoucher.size() > 0){
	context.voucher = listVoucher.get(0);
}
*/

listOrderBillingInvoice = delegator.findByAnd("OrderBillingInvoice", [invoiceId : invoiceId],null,false);
if(listOrderBillingInvoice != null && listOrderBillingInvoice.size() > 0){
	orderBillingInvoice = listOrderBillingInvoice.get(0);
	listOrderContactMech = delegator.findByAnd("OrderContactMech", [orderId : orderBillingInvoice.orderId, contactMechPurposeTypeId : "SHIPPING_LOCATION" ],null,false);
	if(listOrderContactMech != null && listOrderContactMech.size() > 0){
		orderContactMech = listOrderContactMech.get(0);
		listFacilityPartyExchange = delegator.findByAnd("FacilityPartyExchange", [postalAddressId : orderContactMech.contactMechId],null,false);
		if(listFacilityPartyExchange != null && listFacilityPartyExchange.size() > 0){
			context.customerName = listFacilityPartyExchange.get(0).productStoreNameDtm;
		}
	}

	orderHeader = delegator.findOne("OrderHeader", [orderId : orderBillingInvoice.orderId], false);
	productStoreId = orderHeader.productStoreId;

	productStore = delegator.findOne("ProductStore", [productStoreId : productStoreId], false);
	context.productStoreName = productStore.storeName;

	listOrderInvoiceNote = delegator.findByAnd("OrderInvoiceNote", [orderId : orderBillingInvoice.orderId],null,false);
	if(listOrderInvoiceNote != null && listOrderInvoiceNote.size() > 0){
		context.orderInvoiceNote = listOrderInvoiceNote.get(0);
	}
}

listOrderItemIssuanceShipmentDelivery = delegator.findByAnd("OrderItemIssuanceShipmentDelivery", [invoiceId : invoiceId],null,false);
if(listOrderItemIssuanceShipmentDelivery != null && listOrderItemIssuanceShipmentDelivery.size() > 0){
	context.deliveryId = listOrderItemIssuanceShipmentDelivery.get(0).deliveryId;
	originFacilityId =listOrderItemIssuanceShipmentDelivery.get(0).originFacilityId;
	facility = delegator.findOne("Facility", [facilityId : originFacilityId], false);
	context.facilityName = facility.facilityName;
}

currency = parameters.currency;        // allow the display of the invoice in the original currency, the default is to display the invoice in the default currency
BigDecimal conversionRate = new BigDecimal("1");
ZERO = BigDecimal.ZERO;
decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

if (invoice) {
    // each invoice of course has two billing addresses, but the one that is relevant for purchase invoices is the PAYMENT_LOCATION of the invoice
    // (ie Accounts Payable address for the supplier), while the right one for sales invoices is the BILLING_LOCATION (ie Accounts Receivable or
    // home of the customer.)
    if ("PURCHASE_INVOICE".equals(invoice.invoiceTypeId)) {
        billingAddress = InvoiceWorker.getSendFromAddress(invoice);
    } else {
        billingAddress = InvoiceWorker.getBillToAddress(invoice);
    }
    if (billingAddress) {
        context.billingAddress = billingAddress;
    }

    billingParty = InvoiceWorker.getBillToParty(invoice);
    context.billingParty = billingParty;
    sendingParty = InvoiceWorker.getSendFromParty(invoice);
    context.sendingParty = sendingParty;

    if (currency && !invoice.getString("currencyUomId").equals(currency)) {
        conversionRate = InvoiceWorker.getInvoiceCurrencyConversionRate(invoice);
        invoice.currencyUomId = currency;
        invoice.invoiceMessage = " converted from original with a rate of: " + conversionRate.setScale(8, rounding);
    }

    List<Map<String, Object>> invoiceItemMap = FastList.newInstance();

    invoiceItemPdfs = FastList.newInstance();
    if ("SALES_INVOICE_TOTAL".equals(invoice.invoiceTypeId))
    	invoiceItemPdfs = delegator.findByAnd("GroupInvoiceItemInvoiceSalesTotalPdf", UtilMisc.toMap("invoiceId", invoiceId), null, false);
    else
    	invoiceItemPdfs = delegator.findByAnd("InvoiceItemInvoicePdfSum", UtilMisc.toMap("invoiceId", invoiceId), null, false);
    List<GenericValue> discount = delegator.findByAnd("InvoiceItemAndDiscountGroupBy", UtilMisc.toMap("invoiceId", invoiceId), null, false)
    invoiceItemPdfSums = FastList.newInstance();
    vatTaxesByType = FastMap.newInstance();
    vatAmountByType = FastMap.newInstance();
    if ("SALES_INVOICE_TOTAL".equals(invoice.invoiceTypeId))
    	invoiceItemPdfSums = delegator.findByAnd("SumInvoiceItemInvoiceSalesTotalPdf", [invoiceId : invoiceId], null, false);
    else
        invoiceItemPdfSums = delegator.findByAnd("InvoiceItemAndTaxSum", [invoiceId : invoiceId], null, false);
  	invoiceItemPdfSums.each { invoiceItemPdfSum ->
    	if (invoiceItemPdfSum.taxPercentage == null || invoiceItemPdfSum.taxPercentage == 0)
    	{
    		 vatAmountByTypeAmount = vatAmountByType["VAT0"];
    		 if (!vatAmountByTypeAmount) {
                vatAmountByTypeAmount = 0.0;
             }
    		 vatAmountByType.put("VAT0",  vatAmountByTypeAmount + invoiceItemPdfSum.amountNotTax);
    		 vatTaxesByType.put("VAT0", null);
    	}
    	else  if (invoiceItemPdfSum.taxPercentage == 5)
    	{
    		 vatAmountByType.put("VAT5", invoiceItemPdfSum.amountNotTax);
    		 vatTaxesByType.put("VAT5", invoiceItemPdfSum.totalTaxAmount);
    	}  else  if (invoiceItemPdfSum.taxPercentage == 10)
    	{
    		 vatAmountByType.put("VAT10", invoiceItemPdfSum.amountNotTax);
    		 vatTaxesByType.put("VAT10", invoiceItemPdfSum.totalTaxAmount);
    	}
    }

    context.vatTaxesByType = vatTaxesByType;
    context.vatAmountByType = vatAmountByType
    context.invoiceTypeId = "SALES_INVOICE"
    if(invoiceItemPdfs.size() + discount.size() > 15) {
        context.invoiceTypeId = "SALES_INVOICE_TOTAL"
        List<GenericValue> temps = delegator.findByAnd("InvoiceItemAndTaxSum", UtilMisc.toMap("invoiceId", invoiceId), null, false);
        for(GenericValue temp : temps) {
            Map<String, Object> item = FastMap.newInstance();
            item.put("quantity", 1);
            item.put("amount", temp.get("amountNotTax"));
            item.put("invoiceId", invoiceId);
            item.put("quantityTax", 1);
            item.put("amountTax", temp.get("totalTaxAmount"));
            GenericValue taxAuthorityRateProduct = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId", temp.get("taxAuthorityRateSeqId")), false);
            GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", taxAuthorityRateProduct.get("productCategoryId")), false);
            item.put("taxPercentage", taxAuthorityRateProduct.get("taxPercentage"))
            item.put("productName", productCategory.get("categoryName"))
            item.put("productCode", productCategory.get("productCategoryId"))
            item.put("totalAmount", temp.get("amountNotTax"))
            item.put("totalAmountTax", temp.get("totalTaxAmount"))
            invoiceItemMap.add(item)
        }
        invoiceItemPdfs = FastList.newInstance()
    }

    for(GenericValue x : invoiceItemPdfs) {
        Map<String, Object> item = FastMap.newInstance();
        item.put("quantity", x.get("quantity"))
        item.put("amount", x.get("amount"))
        item.put("invoiceId", x.get("invoiceId"))
        item.put("quantityTax", x.get("quantityTax"))
        item.put("amountTax", x.get("amountTax"))
        item.put("taxPercentage", x.get("taxPercentage"))
        item.put("productName", x.get("productName"))
        item.put("productCode", x.get("productCode"))
        item.put("totalAmount", x.get("totalAmount"))
        item.put("totalAmountTax", x.get("totalAmountTax"))
        invoiceItemMap.add(item)
    }
    List<Map<String, Object>> discountMap = FastList.newInstance();
    for(GenericValue x : discount) {
        Map<String, Object> item = FastMap.newInstance()
        item.put("quantity", x.get("quantity"))
        item.put("amount", x.get("amount"))
        item.put("invoiceId", x.get("invoiceId"))
        item.put("quantityTax", BigDecimal.ZERO)
        item.put("amountTax", BigDecimal.ZERO)
        item.put("taxPercentage", BigDecimal.ZERO)
        item.put("productName", x.getString("description"))
        item.put("productCode", "")
        item.put("totalAmount", x.get("subTotalAmount"))
        item.put("totalAmountTax", BigDecimal.ZERO)
        invoiceItemMap.add(item)
        discountMap.add(item)
    }
    context.invoiceItemPdfs = invoiceItemMap
    context.discountMap = discountMap

    invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
    invoiceNoTaxTotal = InvoiceWorker.getInvoiceNoTaxTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
    invoiceTaxTotal = InvoiceWorker.getInvoiceTaxTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
    context.invoiceTotal = invoiceTotal;
    context.invoiceTaxTotal = invoiceTaxTotal;
    context.invoiceNoTaxTotal = invoiceNoTaxTotal;

                //*________________this snippet was added for adding Tax ID in invoice header if needed _________________

               sendingTaxInfos = sendingParty.getRelated("PartyTaxAuthInfo", null, null, false);
               billingTaxInfos = billingParty.getRelated("PartyTaxAuthInfo", null, null, false);
               sendingPartyTaxId = null;
               billingPartyTaxId = null;

               if (billingAddress) {
                   sendingTaxInfos.eachWithIndex { sendingTaxInfo, i ->
                       if (sendingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
                            sendingPartyTaxId = sendingTaxInfos[i-1].partyTaxId;
                       }
                   }
                   billingTaxInfos.eachWithIndex { billingTaxInfo, i ->
                       if (billingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
                            billingPartyTaxId = billingTaxInfos[i-1].partyTaxId;
                       }
                   }
               }
               if (sendingPartyTaxId) {
                   context.sendingPartyTaxId = sendingPartyTaxId;
               }
               if (billingPartyTaxId && !context.billingPartyTaxId) {
                   context.billingPartyTaxId = billingPartyTaxId;
               }
               //________________this snippet was added for adding Tax ID in invoice header if needed _________________*/

    terms = invoice.getRelated("InvoiceTerm", null, null, false);
    context.terms = terms;

    paymentAppls = delegator.findByAnd("PaymentApplication", [invoiceId : invoiceId], null, false);
    context.payments = paymentAppls;

    orderItemBillings = delegator.findByAnd("OrderItemBilling", [invoiceId : invoiceId], ['orderId'], false);
    orders = new LinkedHashSet();
    orderItemBillings.each { orderIb ->
        orders.add(orderIb.orderId);
    }
    context.orders = orders;

    invoiceStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", invoice.get("statusId")), false)
    context.invoiceStatus = invoiceStatus;

    edit = parameters.editInvoice;
    if ("true".equalsIgnoreCase(edit)) {
        invoiceItemTypes = delegator.findList("InvoiceItemType", null, null, null, null, false);
        context.invoiceItemTypes = invoiceItemTypes;
        context.editInvoice = true;
    }

    // format the date
    if (invoice.invoiceDate) {
        invoiceDate = DateFormat.getDateInstance(DateFormat.LONG).format(invoice.invoiceDate);
        context.invoiceDate = invoiceDate;
    } else {
        context.invoiceDate = "N/A";
    }
}