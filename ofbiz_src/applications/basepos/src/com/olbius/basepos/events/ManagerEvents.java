package com.olbius.basepos.events;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;

import com.olbius.basepos.util.PosUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ManagerEvents {
	public static String resource_error = "BasePosErrorUiLabels";
	public static final String POS_PARTY = "POS_PARTY";
	
	public static String paidOut(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String invoiceItemTypeId = request.getParameter("invoiceItemTypeId");
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String amountOutParam = request.getParameter("amountOut");
		String reasonCommentOut = request.getParameter("reasonCommentOut");
		String reasonOut = request.getParameter("reasonOut");
		String type= "OUT";
		if(UtilValidate.isNotEmpty(webposSession)){
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			WebPosTransaction webposTransaction = webposSession.getCurrentTransaction();
			ShoppingCart cart = webposTransaction.getCart();
			if(UtilValidate.isNotEmpty(webposTransaction)){
				boolean isOpen = webposTransaction.isOpen();
				if(isOpen){
					try {
						TransactionUtil.begin();
					} catch (GenericTransactionException e1) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotRecordThisExpense", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					BigDecimal amountOut = null;
					try {
						amountOut = new BigDecimal(amountOutParam);
					} catch (Exception e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSAmountIsNotValid", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					if(UtilValidate.isNotEmpty(amountOut)){
						String getTerminalLogId = webposTransaction.getTerminalLogId();
						GenericValue internTx = delegator.makeValue("PosTerminalInternTx");
						internTx.set("posTerminalLogId",getTerminalLogId);
						internTx.set("paidAmount",amountOut);
						internTx.set("reasonComment",reasonCommentOut);
						internTx.set("invoiceItemTypeId", invoiceItemTypeId);
						if(UtilValidate.isNotEmpty(reasonOut)){
							internTx.set("reasonEnumId", reasonOut);
						}
						try {
							internTx.create();
						} catch (GenericEntityException e) {
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotCreatePosTerminalLog", locale);
							request.setAttribute("_ERROR__MESSAGE_", errorMessage);
							return "error";
						}
						webposTransaction.paidInOut(type);
						try {
							String invoiceTypeId = "PURCHASE_INVOICE";
							String partyId = company;
							String partyIdFrom = POS_PARTY;
							List<Map<String, Object>> invoiceItemList = FastList.newInstance();
							String description = UtilProperties.getMessage(resource_error, "BPOSDiffExpenseInSale", locale);
							Map<String, Object> invoiceItem = FastMap.newInstance();
							invoiceItem.put("invoiceItemSeqId", "00001");
							invoiceItem.put("amount", amountOut);
							invoiceItem.put("quantity", BigDecimal.ONE);
							invoiceItem.put("invoiceItemTypeId", invoiceItemTypeId);
							invoiceItem.put("description", description);
							invoiceItemList.add(invoiceItem);
							String currencyUomId = cart.getCurrency();
							String paymentType = "VENDOR_PAYMENT";
							PosUtil.recordAccounting(invoiceTypeId, partyIdFrom, partyId, amountOut, currencyUomId, description, invoiceItemList, paymentType, userLogin, dispatcher, resource_error, locale);
							
						} catch (Exception e) {
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotRecordThisExpense", locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
					}
					try {
						TransactionUtil.commit();
					} catch (GenericTransactionException e1) {
						Debug.log("Can not commit this transaction");
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotRecordThisExpense", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				}else{
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSWorkShiftIsClosed", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
		}else{
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}
	
	public static String paidIn(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		String amountInParam = request.getParameter("amountIn");
		String reasonCommentIn = request.getParameter("reasonCommentIn");
		String reasonIn = request.getParameter("reasonIn");
		String type= "IN";
		String invoiceItemTypeId = request.getParameter("invoiceItemTypeId");
		if(UtilValidate.isNotEmpty(webposSession)){
			ShoppingCart cart = webposSession.getCart();
			WebPosTransaction webposTransaction = webposSession.getCurrentTransaction();
			if(UtilValidate.isNotEmpty(webposTransaction)){
				boolean isOpen = webposTransaction.isOpen();
				if(isOpen){
					try {
						TransactionUtil.begin();
					} catch (GenericTransactionException e1) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotRecordThisTurnOver", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					BigDecimal amountIn = null;
					try {
						amountIn = new BigDecimal(amountInParam);
					} catch (Exception e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSAmountIsNotValid", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					if(UtilValidate.isNotEmpty(amountIn)){
						String getTerminalLogId = webposTransaction.getTerminalLogId();
						GenericValue internTx = delegator.makeValue("PosTerminalInternTx");
						internTx.set("posTerminalLogId",getTerminalLogId);
						internTx.set("paidAmount",amountIn);
						internTx.set("reasonComment",reasonCommentIn);
						internTx.set("invoiceItemTypeId", invoiceItemTypeId);
						if(UtilValidate.isNotEmpty(reasonIn)){
							internTx.set("reasonEnumId", reasonIn);
						}
						try {
							internTx.create();
						} catch (GenericEntityException e) {
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotCreatePosTerminalLog", locale);
							request.setAttribute("_ERROR__MESSAGE_", errorMessage);
							return "error";
						}
						webposTransaction.paidInOut(type);
						try {
							String invoiceTypeId = "SALES_INVOICE";
							String partyId = POS_PARTY;
							String partyIdFrom = company;
							List<Map<String, Object>> invoiceItemList = FastList.newInstance();
							String description = UtilProperties.getMessage(resource_error, "BPOSDiffTurnOverInSale", locale);
							Map<String, Object> invoiceItem = FastMap.newInstance();
							invoiceItem.put("invoiceItemSeqId", "00001");
							invoiceItem.put("amount", amountIn);
							invoiceItem.put("quantity", BigDecimal.ONE);
							invoiceItem.put("invoiceItemTypeId", invoiceItemTypeId);
							invoiceItem.put("description", description);
							invoiceItemList.add(invoiceItem);
							String currencyUomId = cart.getCurrency();
							String paymentType = "CUSTOMER_PAYMENT";
							PosUtil.recordAccounting(invoiceTypeId, partyIdFrom, partyId, amountIn, currencyUomId, description, invoiceItemList, paymentType, userLogin, dispatcher, resource_error, locale);
						} catch (Exception e) {
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotRecordThisTurnOver", locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						try {
							TransactionUtil.commit();
						} catch (GenericTransactionException e) {
							Debug.log("Can not commit this transaction");
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotRecordThisTurnOver", locale);
				    		request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				 			return "error";
						}
					}
				}else{
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSWorkShiftIsClosed", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
		}else{
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}
}
