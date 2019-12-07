package com.olbius.basepos.invoice;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

public class InvoiceEvents {
	public static String resource = "BasePosUiLabels";
	public static String resource_error = "BasePosErrorUiLabels";
	
	public static String addPayCashToInvoice(HttpServletRequest request, HttpServletResponse response){
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	HttpSession session = request.getSession(true);
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Locale locale = UtilHttp.getLocale(request);
    	String invoiceId = (String) request.getParameter("invoiceId");
    	String amountCash = (String) request.getParameter("amountCash");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	Security security = (Security) request.getAttribute("security");
    	boolean hasPermission = security.hasEntityPermission("RECEIVING", "_PAIDMONEY", userLogin);
    	if (hasPermission) {
    		if(UtilValidate.isNotEmpty(invoiceId)){
        		GenericValue invoice = null;
        		 try {
    				invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    			} catch (GenericEntityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        		 if(UtilValidate.isNotEmpty(invoice)){
        			 String partyIdTo = invoice.getString("partyId");
        			 String partyIdFrom = invoice.getString("partyIdFrom");
        			 BigDecimal amount = new BigDecimal(amountCash);
        			 String currencyUomId = invoice.getString("currencyUomId");
        			 if(UtilValidate.isEmpty(amount)){
        				 amount = BigDecimal.ZERO;
        			 }
        			 //
        			 try {
    					TransactionUtil.begin();
    				} catch (GenericTransactionException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
        			 Map<String, Object> createPaymentMap = FastMap.newInstance();
        	    		createPaymentMap.put("partyIdTo",partyIdFrom );
        	    		createPaymentMap.put("partyIdFrom",partyIdTo);
        	    		createPaymentMap.put("amount", amount);
        	    		createPaymentMap.put("paymentTypeId", "VENDOR_PAYMENT");
        	    		String comments = UtilProperties.getMessage(resource, "BPOSPaymentDescription", locale);
        	    		createPaymentMap.put("comments", comments);
        	    		createPaymentMap.put("userLogin", userLogin);
        	    		createPaymentMap.put("currencyUomId", currencyUomId);
        	    		createPaymentMap.put("isDepositWithDrawPayment", "Y");
        	    		createPaymentMap.put("finAccountTransTypeId", "WITHDRAWAL");
        	    		createPaymentMap.put("paymentMethodTypeId", "CASH");
        	    		createPaymentMap.put("statusId", "PMNT_RECEIVED");
        	    		Map<String, Object> createPayment = FastMap.newInstance();
        	    		try {
        	    			createPayment = dispatcher.runSync("createPaymentAndFinAccountTrans", createPaymentMap);
        				
        				} catch (GenericServiceException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        	    		if(ServiceUtil.isError(createPayment)){
        	    			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreatePayment", locale);
        	        		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
        	        		return "error";
        	    		}else{
        	    			String paymentId = (String) createPayment.get("paymentId");
        	    			Map<String, Object> createPaymentApp = FastMap.newInstance();
        	    			createPaymentApp.put("userLogin", userLogin);
        	    			createPaymentApp.put("paymentId", paymentId);
        	    			createPaymentApp.put("invoiceId", invoiceId);
        	    			Map<String, Object> createPaymentAppResult =  FastMap.newInstance();
        	    			try {
        						createPaymentAppResult = dispatcher.runSync("createPaymentApplication", createPaymentApp);
        						
        					} catch (GenericServiceException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        	    		
        	    			if(ServiceUtil.isError(createPaymentAppResult)){
        	    				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotLinkPaymentWithInvoice", locale);
        	            		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
        	            		return "error";
        	    			}
        	    				//changeInvoiceStatus
        	    			String returnChangedStatus = changeInvoiceStatus(request, invoiceId);
        	    			
        	    			try {
    							TransactionUtil.commit();
    						} catch (GenericTransactionException e1) {
    							 String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotAddPayCashIntoInvoice", locale);
    			    			 request.setAttribute("_ERROR_MESSAGE_", errorMessage);
    			    	 		 return "error";
    						}
        	    			return returnChangedStatus;
        	    			
        	    		}
        		 }else{
        			 String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotAddPayCashIntoInvoice", locale);
        			 request.setAttribute("_ERROR_MESSAGE_", errorMessage);
        	 		 return "error";
        		 }
        	}else{
        		String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotAddPayCashIntoInvoice", locale);
        		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
     			return "error";
        	}
    	} else {
    		String errorMessage = UtilProperties.getMessage(resource_error, "BasePosViewPermissionError", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
    	}
    }
	
	public static String changeInvoiceStatus(HttpServletRequest request, String invoiceId){
    	Locale locale = UtilHttp.getLocale(request);
    	HttpSession session = request.getSession(true);
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	GenericValue invoice = null;
    	try {
			invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(UtilValidate.isNotEmpty(invoice)){
    		BigDecimal amountNotApplied = InvoiceWorker.getInvoiceNotApplied(invoice);
    		if(UtilValidate.isNotEmpty(amountNotApplied) && amountNotApplied.compareTo(BigDecimal.ZERO) == 0){
    			Map<String, Object> approvedInvoiceMapo = FastMap.newInstance();
    			approvedInvoiceMapo.put("userLogin", userLogin);
    			approvedInvoiceMapo.put("statusId", "INVOICE_READY");
    			approvedInvoiceMapo.put("invoiceId", invoiceId);
    			try {
					dispatcher.runSync("setInvoiceStatus", approvedInvoiceMapo);
				} catch (GenericServiceException e) {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotChangeStatusOfInvoice", locale);
		    		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
		 			return "error";
				}
    			Map<String, Object> paidInvoiceMapo = FastMap.newInstance();
    			paidInvoiceMapo.put("userLogin", userLogin);
    			paidInvoiceMapo.put("statusId", "INVOICE_PAID");
    			paidInvoiceMapo.put("invoiceId", invoiceId);
    			try {
					dispatcher.runSync("setInvoiceStatus", paidInvoiceMapo);
				} catch (GenericServiceException e) {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotChangeStatusOfInvoice", locale);
		    		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
		 			return "error";
				}
    		}
    	}else{
    		String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotFindInvoice", locale);
    		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
 			return "error";
    	}
    	return "success";
    }
}
