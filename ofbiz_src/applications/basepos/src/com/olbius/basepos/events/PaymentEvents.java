package com.olbius.basepos.events;

import java.math.BigDecimal;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.transaction.GenericTransactionException;

import com.olbius.basepos.events.WebPosEvents;
import com.olbius.basepos.lean.POSWorker;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;

public class PaymentEvents {
	public static String resource_error = "BasePosErrorUiLabels";
	
	public static String payCashNew(HttpServletRequest request, HttpServletResponse response) throws Exception{
		POSWorker posWorker = new POSWorker(request, response);
		return posWorker.completeTransaction(request, response);
	}
	
	public static String payCash(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		String amountCashParam = request.getParameter("amountCash");
		String amountCreditCardParam = request.getParameter("amountCreditCard");
		HttpSession session = request.getSession();
		WebPosSession webposSession = (WebPosSession)session.getAttribute("webPosSession");
		if(UtilValidate.isNotEmpty(webposSession)){
			WebPosTransaction webposTransaction = webposSession.getCurrentTransaction();
			if(UtilValidate.isEmpty(amountCashParam) && UtilValidate.isEmpty(amountCreditCardParam)){
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSPayCashNotValidAmount", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}else{
				//create payment
				if(UtilValidate.isNotEmpty(amountCashParam)){
					BigDecimal amountCash = new BigDecimal(amountCashParam);
					if(UtilValidate.isNotEmpty(amountCash) && amountCash.compareTo(BigDecimal.ZERO) >0){
						webposTransaction.addPayment("CASH", amountCash, "", "");
					}else{
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSPayCashNotValidAmount", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				}
				if(UtilValidate.isNotEmpty(amountCreditCardParam)){
					BigDecimal amountCreditCard = new BigDecimal(amountCreditCardParam);
					if(UtilValidate.isNotEmpty(amountCreditCard) && amountCreditCard.compareTo(BigDecimal.ZERO) >0){
						webposTransaction.addPayment("COMPANY_CHECK", amountCreditCard);
					}else{
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSPayCashNotValidAmount", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				}
				//create order 
				String reusltOfCompleteSale = "false";
				try {
					reusltOfCompleteSale = WebPosEvents.completeSale(request, response);
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!reusltOfCompleteSale.equals("success")){
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}
}