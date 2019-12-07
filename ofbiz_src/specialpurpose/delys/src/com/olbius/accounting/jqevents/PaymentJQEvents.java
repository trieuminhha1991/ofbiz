package com.olbius.accounting.jqevents;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

public class PaymentJQEvents {
//	public static String getInvoices(HttpServletRequest request, HttpServletResponse response) {
//        String paymentId = request.getParameter("paymentId");
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        try {
//			GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
//			
//			//Set party condition
//			Map<String, Object> partyExprMap = FastMap.newInstance();
//			partyExprMap.put("partyId", payment.get("partyIdFrom"));
//			partyExprMap.put("partyIdFrom", payment.get("partyIdTo"));
//			EntityCondition partyCond = EntityCondition.makeCondition(partyExprMap);
//			
//			//Set status condition
//			EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toSet("INVOICE_APPROVED", "INVOICE_SENT", "INVOICE_READY", "INVOICE_RECEIVED"));
//			
//			//Set currency Condition
//			EntityCondition currCond = EntityCondition.makeCondition("currencyUomId", payment.get("currencyUomId"));
//			EntityCondition actualCurrCond = EntityCondition.makeCondition("currencyUomId", payment.get("actualCurrencyUomId"));
//			
//			//List condition
//			EntityCondition topCond = EntityCondition.makeCondition(UtilMisc.toList(partyCond, statusCond, currCond), EntityJoinOperator.AND);
//			EntityCondition topCondActual = EntityCondition.makeCondition(UtilMisc.toList(partyCond, statusCond, actualCurrCond), EntityJoinOperator.AND);
//			
//			Set<String> fields = UtilMisc.toSet("invoiceId", "invoiceTypeId", "currencyUomId", "description", "invoiceDate");
//			//retrieve invoices for the related parties which have not been (fully) applied yet and which have the same currency as the payment
//			List<GenericValue> invoices = delegator.findList("Invoice", topCond, fields, UtilMisc.toList("invoiceDate"), null, false);
//			List<Map<String, Object>> listInvoices = getInvoices(invoices, payment, false);
//			request.setAttribute("listInvoices", listInvoices);
//			
//			invoices = delegator.findList("Invoice", topCondActual, fields, UtilMisc.toList("invoiceDate"), null, false);
//			List<Map<String, Object>> invoicesOtherCurrency = getInvoices(invoices, payment, true);
//			request.setAttribute("invoicesOtherCurrency", invoicesOtherCurrency);
//        } catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//			return "error";
//		}
//        return "success";
//    }
//	
//	public static String getPayments(HttpServletRequest request, HttpServletResponse response) {
//        String paymentId = request.getParameter("paymentId");
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        try {
//			GenericValue basePayment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
//			
//			//Set party condition
//			Map<String, Object> partyExprMap = FastMap.newInstance();
//			partyExprMap.put("partyIdTo", basePayment.get("partyIdFrom"));
//			partyExprMap.put("partyIdFrom", basePayment.get("partyIdTo"));
//			EntityCondition partyCond = EntityCondition.makeCondition(partyExprMap);
//			
//			//Set payment condition
//			EntityCondition paymentCond = EntityCondition.makeCondition("paymentId", EntityJoinOperator.NOT_EQUAL, paymentId);
//			
//			//Set status condition
//			EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityJoinOperator.IN, UtilMisc.toSet("PMNT_RECEIVED", "PMNT_SENT"));
//			
//			//List condition
//			EntityCondition topCond = EntityCondition.makeCondition(UtilMisc.toList(partyCond, statusCond, paymentCond), EntityJoinOperator.AND);
//			
//			List<GenericValue> payments = delegator.findList("Payment", topCond, null, UtilMisc.toList("effectiveDate"), null, false);
//			BigDecimal basePaymentApplied = PaymentWorker.getPaymentApplied(basePayment);
//			BigDecimal basePaymentAmount = basePayment.getBigDecimal("amount");
//			BigDecimal basePaymentToApply = basePaymentAmount.subtract(basePaymentApplied);
//		    List<Map<String, Object>> paymentsMapList = FastList.newInstance();
//			for(GenericValue payment : payments) {
//		    	if (PaymentWorker.getPaymentNotApplied(payment).signum() == 1) {  // positiv not applied amount?
//			           // yes, put in the map
//			           Map<String, Object> paymentMap = FastMap.newInstance();
//			           paymentMap.put("basePaymentId", paymentId);
//			           paymentMap.put("toPaymentId", payment.get("paymentId"));
//			           paymentMap.put("currencyUomId", payment.get("currencyUomId"));
//			           paymentMap.put("effectiveDate", payment.get("effectiveDate")); // list as YYYY-MM-DD
//			           paymentMap.put("amount", payment.getBigDecimal("amount"));
//			           paymentMap.put("amountApplied", PaymentWorker.getPaymentApplied(payment));
//			           BigDecimal paymentToApply = PaymentWorker.getPaymentNotApplied(payment);
//			           if (paymentToApply.compareTo(basePaymentToApply) < 0 ) {
//			                paymentMap.put("amountToApply", paymentToApply);
//			           } else {
//			                paymentMap.put("amountToApply", basePaymentToApply);
//			           }
//			           paymentsMapList.add(paymentMap);
//			        }
//		    }
//		        
//			request.setAttribute("payments", paymentsMapList);
//        } catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//			return "error";
//		}
//        return "success";
//    }
//	
//	public static String getPayApplInv(HttpServletRequest request, HttpServletResponse response) {
//        String paymentId = request.getParameter("paymentId");
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        try {
//        	EntityCondition conditionInv = EntityCondition.makeCondition("invoiceId", EntityJoinOperator.NOT_EQUAL, null);
//        	EntityCondition conditionPay = EntityCondition.makeCondition("paymentId", EntityJoinOperator.EQUALS, paymentId);
//			List<GenericValue> paymentApplicationsInv = delegator.findList("PaymentApplication", EntityCondition.makeCondition(UtilMisc.toList(conditionInv, conditionPay), EntityJoinOperator.AND), null, UtilMisc.toList("invoiceId", "invoiceItemSeqId"), null, false);
//			request.setAttribute("paymentApplicationsInv", paymentApplicationsInv);
//        } catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//			return "error";
//		}
//        return "success";
//    }
//	
//	public static String getPayApplPay(HttpServletRequest request, HttpServletResponse response) {
//        String paymentId = request.getParameter("paymentId");
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        try {
//        	EntityCondition conditionToPay = EntityCondition.makeCondition("toPaymentId", EntityJoinOperator.NOT_EQUAL, null);
//        	EntityCondition conditionPay = EntityCondition.makeCondition("paymentId", EntityJoinOperator.EQUALS, paymentId);
//			List<GenericValue> paymentApplicationsPay = delegator.findList("PaymentApplication", EntityCondition.makeCondition(UtilMisc.toList(conditionToPay, conditionPay), EntityJoinOperator.AND), null, UtilMisc.toList("toPaymentId", "amountApplied"), null, false);
//			request.setAttribute("paymentApplicationsPay", paymentApplicationsPay);
//        } catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//			return "error";
//		}
//        return "success";
//    }
//	
//	public static String getPayApplBil(HttpServletRequest request, HttpServletResponse response) {
//        String paymentId = request.getParameter("paymentId");
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        try {
//        	EntityCondition conditionBil = EntityCondition.makeCondition("billingAccountId", EntityJoinOperator.NOT_EQUAL, null);
//        	EntityCondition conditionPay = EntityCondition.makeCondition("paymentId", EntityJoinOperator.EQUALS, paymentId);
//			List<GenericValue> paymentApplicationsBil = delegator.findList("PaymentApplication", EntityCondition.makeCondition(UtilMisc.toList(conditionBil, conditionPay), EntityJoinOperator.AND), null, UtilMisc.toList("billingAccountId", "amountApplied"), null, false);
//			request.setAttribute("paymentApplicationsBil", paymentApplicationsBil);
//        } catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//			return "error";
//		}
//        return "success";
//    }
//	
//	public static String getPayApplTax(HttpServletRequest request, HttpServletResponse response) {
//        String paymentId = request.getParameter("paymentId");
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        try {
//        	EntityCondition conditionTax = EntityCondition.makeCondition("taxAuthGeoId", EntityJoinOperator.NOT_EQUAL, null);
//        	EntityCondition conditionPay = EntityCondition.makeCondition("paymentId", EntityJoinOperator.EQUALS, paymentId);
//			List<GenericValue> paymentApplicationsTax = delegator.findList("PaymentApplication", EntityCondition.makeCondition(UtilMisc.toList(conditionTax, conditionPay), EntityJoinOperator.AND), null, UtilMisc.toList("taxAuthGeoId", "amountApplied"), null, false);
//			request.setAttribute("paymentApplicationsTax", paymentApplicationsTax);
//        } catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//			return "error";
//		}
//        return "success";
//    }
//	
//	private static List<Map<String, Object>> getInvoices(List<GenericValue> invoices, GenericValue payment,  boolean actual) {
//		List<Map<String, Object>> invoicesList = new ArrayList<Map<String, Object>>();  // to pass back to the screeen list of unapplied invoices
//		if (invoices != null) {
//	        BigDecimal paymentApplied = PaymentWorker.getPaymentApplied(payment);
//	        int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
//			int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
//			BigDecimal paymentToApply = payment.getBigDecimal("amount").setScale(decimals,rounding).subtract(paymentApplied);
//	        if (actual && payment.get("actualCurrencyAmount") != null) {
//	            paymentToApply = payment.getBigDecimal("actualCurrencyAmount").setScale(decimals,rounding).subtract(paymentApplied);
//	        }
//	        for(GenericValue invoice : invoices ) {
//	            BigDecimal invoiceAmount = InvoiceWorker.getInvoiceTotal(invoice).setScale(decimals,rounding);
//	            BigDecimal invoiceApplied = InvoiceWorker.getInvoiceApplied(invoice).setScale(decimals,rounding);
//	            BigDecimal invoiceToApply = invoiceAmount.subtract(invoiceApplied);
//	            if (invoiceToApply.signum() == 1) {
//	                Map<String, Object> invoiceMap = FastMap.newInstance();
//	                invoiceMap.putAll(invoice);
//	                invoiceMap.put("amount", invoiceAmount);
//	                invoiceMap.put("amountApplied", invoiceApplied);
//	                if (paymentToApply.compareTo(invoiceToApply) < 0 ) {
//	                    invoiceMap.put("amountToApply", paymentToApply);
//	                } else {
//	                    invoiceMap.put("amountToApply", invoiceToApply);
//	                }
//	                invoicesList.add(invoiceMap);
//	            }
//	        }
//	    }
//		return invoicesList;
//	}
//	
//	public static String getAppliedAmountAndNotAppliedAmount(HttpServletRequest request, HttpServletResponse response) {
//        String paymentId = request.getParameter("paymentId");
//        Delegator delegator = (Delegator) request.getAttribute("delegator");
//        try {
//        	GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
//			BigDecimal appliedAmount = PaymentWorker.getPaymentApplied(payment);
//			request.setAttribute("appliedAmount", appliedAmount);
//			BigDecimal notAppliedAmount = PaymentWorker.getPaymentNotApplied(payment);
//			request.setAttribute("notAppliedAmount", notAppliedAmount);
//        } catch (GenericEntityException e) {
//			Debug.log(e.getMessage());
//			return "error";
//		}
//        return "success";
//    }

}
