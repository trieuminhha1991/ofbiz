package com.olbius.accounting.jqevents;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.payment.BillingAccountWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;

public class BillingAccountJQEvents {
	 public static String getBillingAccountBalance(HttpServletRequest request, HttpServletResponse response) {
	        String billingAccountId = request.getParameter("billingAccountId");
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        try {
				BigDecimal balance = BillingAccountWorker.getBillingAccountBalance(delegator, billingAccountId);
				request.setAttribute("availableBalance", balance);
	        } catch (GenericEntityException e) {
				Debug.log(e.getMessage());
				return "error";
			}
	        return "success";
	    }
	 
	 public static String getInvoiceTotal(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
	        String invoiceId = request.getParameter("invoiceId");
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        BigDecimal total = InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
			request.setAttribute("total", total);
	        return "success";
    }
	
	 public static String getInvoiceNotApplied(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
	        String invoiceId = request.getParameter("invoiceId");
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        BigDecimal total = InvoiceWorker.getInvoiceNotApplied(delegator, invoiceId);
			request.setAttribute("total", total);
	        return "success";
	 }
}
