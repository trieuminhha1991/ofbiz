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
import java.math.BigDecimal;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import java.text.DateFormat;
import java.text.*;
import java.text.NumberFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.EntityConditionBuilder;
// @param GenericValue invoice - The Invoice entity to find payment applications for
if (!invoice) return;
invoiceApplications = [];  // to pass back to the screen with payment applications added
exprBldr =  new EntityConditionBuilder();
// retrieve related applications with null itemnumber
invoiceAppls = invoice.getRelated("PaymentApplication", [invoiceItemSeqId : null], null, false);
if("PAYROL_INVOICE".equals(invoice.invoiceTypeId)){
	invoiceAppls.each { invoiceAppl ->
		itemmap = [:];
		if(invoiceAppl.amountApplied == 0){
			payApplCond = exprBldr.AND() {
        		EQUALS(paymentId: invoiceAppl.paymentId)
        		NOT_EQUALS(taxAuthGeoId: null)
    		}
			listPayAppl = delegator.findList("PaymentApplication", payApplCond, null,null, null, false);
			invoiceAppl = listPayAppl.get(0);
			itemmap.invoiceId = invoiceAppl.invoiceId;
	    	itemmap.invoiceItemSeqId = invoiceAppl.invoiceItemSeqId;
	    	itemmap.total = InvoiceWorker.getInvoiceTotal(invoice);
	    	itemmap.paymentApplicationId = invoiceAppl.paymentApplicationId;
	    	itemmap.paymentId = invoiceAppl.paymentId;
	    	itemmap.billingAccountId = invoiceAppl.billingAccountId;
	    	itemmap.taxAuthGeoId = invoiceAppl.taxAuthGeoId;
	    	itemmap.amountToApply = invoiceAppl.amountApplied;
	    	itemmap.amountApplied = invoiceAppl.amountApplied;
	    	payment = delegator.findOne("Payment", [paymentId : invoiceAppl.paymentId], false);
	    	itemmap.paymentTypeId = payment.paymentTypeId;
	    	invoiceApplications.add(itemmap);
		}else{
	    	itemmap.invoiceId = invoiceAppl.invoiceId;
	    	itemmap.invoiceItemSeqId = invoiceAppl.invoiceItemSeqId;
	    	itemmap.total = InvoiceWorker.getInvoiceTotal(invoice);
	    	itemmap.paymentApplicationId = invoiceAppl.paymentApplicationId;
	    	itemmap.paymentId = invoiceAppl.paymentId;
	    	itemmap.billingAccountId = invoiceAppl.billingAccountId;
	    	itemmap.taxAuthGeoId = invoiceAppl.taxAuthGeoId;
	    	itemmap.amountToApply = invoiceAppl.amountApplied;
	    	itemmap.amountApplied = invoiceAppl.amountApplied;
	    	payment = delegator.findOne("Payment", [paymentId : invoiceAppl.paymentId], false);
	    	itemmap.paymentTypeId = payment.paymentTypeId;
	    	invoiceApplications.add(itemmap);		
		}
	}
}else{
	invoiceAppls.each { invoiceAppl ->
	    itemmap = [:];
	    itemmap.invoiceId = invoiceAppl.invoiceId;
	    itemmap.invoiceItemSeqId = invoiceAppl.invoiceItemSeqId;
	    itemmap.total = InvoiceWorker.getInvoiceTotal(invoice);
	    itemmap.paymentApplicationId = invoiceAppl.paymentApplicationId;
	    itemmap.paymentId = invoiceAppl.paymentId;
	    itemmap.billingAccountId = invoiceAppl.billingAccountId;
	    itemmap.taxAuthGeoId = invoiceAppl.taxAuthGeoId;
	    itemmap.amountToApply = invoiceAppl.amountApplied;
	    itemmap.amountApplied = invoiceAppl.amountApplied;
	    payment = delegator.findOne("Payment", [paymentId : invoiceAppl.paymentId], false);
    	itemmap.paymentTypeId = payment.paymentTypeId;
	    invoiceApplications.add(itemmap);
	}
}
// retrieve related applications with an existing itemnumber
invoice.getRelated("InvoiceItem", null, null, false).each { item ->
    BigDecimal itemTotal = null;
    if (item.amount != null) {
          if (!item.quantity) {
              itemTotal = item.getBigDecimal("amount");
          } else {
              itemTotal = item.getBigDecimal("amount").multiply(item.getBigDecimal("quantity"));
          }
    }
    // get relation payment applications for every item(can be more than 1 per item number)
    item.getRelated("PaymentApplication", null, null, false).each { paymentApplication ->
        itemmap = [:];
        itemmap.putAll(item);
        itemmap.total = NumberFormat.getInstance(locale).format(itemTotal);
        itemmap.paymentApplicationId = paymentApplication.paymentApplicationId;
        itemmap.paymentId = paymentApplication.paymentId;
        itemmap.toPaymentId = paymentApplication.toPaymentId;
        itemmap.amountApplied = paymentApplication.getBigDecimal("amountApplied");
        itemmap.amountToApply = paymentApplication.getBigDecimal("amountApplied");
        itemmap.billingAccountId = paymentApplication.billingAccountId;
        itemmap.taxAuthGeoId = paymentApplication.taxAuthGeoId;
        payment = delegator.findOne("Payment", [paymentId : invoiceAppl.paymentId], false);
    	itemmap.paymentTypeId = payment.paymentTypeId;
        invoiceApplications.add(itemmap);
    }
}
if (invoiceApplications) context.invoiceApplications = invoiceApplications;
result = ServiceUtil.returnSuccess();
result.invoiceApplications = invoiceApplications;
return result;
