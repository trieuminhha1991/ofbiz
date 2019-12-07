package com.olbius.basepos.workShift;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.MultiOrganizationUtil;
public class WorkShiftEvents {
	public static String resource_error = "BasePosErrorUiLabels";
	public static String module = WorkShiftEvents.class.getName();
	
	public static String takeAmountFromEmployee(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String posTerminalStateId = request.getParameter("posTerminalStateId");
		String actualReceivedAmountParam = request.getParameter("actualReceivedAmount");
		GenericValue posTerminalState = null;
		BigDecimal actualReceivedAmount = null;
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if(UtilValidate.isNotEmpty(userLogin)){
			if(UtilValidate.isNotEmpty(posTerminalStateId)){
				try {
					posTerminalState = delegator.findOne("PosTerminalState", UtilMisc.toMap("posTerminalStateId", posTerminalStateId), false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}

				try {
					actualReceivedAmount = new BigDecimal(actualReceivedAmountParam);
				} catch (Exception e) {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSAmountIsNotValid", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
				if(UtilValidate.isNotEmpty(posTerminalState)){
					try {
						TransactionUtil.begin();
					} catch (GenericTransactionException e1) {
						Debug.logError("Can not begin a transaction", module);
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetMoneyFromEmployee", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					if(UtilValidate.isNotEmpty(actualReceivedAmount)){
						posTerminalState.set("actualReceivedAmount", actualReceivedAmount);
					}
					EntityCondition workShiftCond = EntityCondition.makeCondition("posTerminalStateId", EntityOperator.EQUALS, posTerminalStateId );
					List<GenericValue> posWorkshiftList  = FastList.newInstance();
					try {
						posWorkshiftList = delegator.findList("PosWorkShift", workShiftCond, null, null, null, false);
					} catch (GenericEntityException e2) {
						Debug.logError("Can not get amount of system", module);
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetAmountOfSystem", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					BigDecimal systemAmount = BigDecimal.ZERO;
					GenericValue posWorkShift = EntityUtil.getFirst(posWorkshiftList);
					if(UtilValidate.isNotEmpty(posWorkShift)){
						//BigDecimal amountTotal = posWorkShift.getBigDecimal("amountTotal");
						BigDecimal amountCash = posWorkShift.getBigDecimal("amountCash");
						if(UtilValidate.isNotEmpty(amountCash)){
							systemAmount = systemAmount.add(amountCash);
						}
						//other Income
						BigDecimal otherIncome = posWorkShift.getBigDecimal("otherInCome");
						if(UtilValidate.isEmpty(otherIncome)){
							otherIncome  = BigDecimal.ZERO;
						}
						systemAmount = systemAmount.add(otherIncome);
						BigDecimal startDrawAmount = posTerminalState.getBigDecimal("startingDrawerAmount");
						if(UtilValidate.isEmpty(startDrawAmount)){
							startDrawAmount =BigDecimal.ZERO;
						}
						systemAmount = systemAmount.add(startDrawAmount);
						//otherCost Total
						BigDecimal otherCost = posWorkShift.getBigDecimal("otherCost");
						if(UtilValidate.isEmpty(otherCost)){
							otherCost = BigDecimal.ZERO;
						}
						systemAmount = systemAmount.subtract(otherCost);
					}
					
					try {
					} catch (Exception e) {
						Debug.logError("Can not get amount of system", module);
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetAmountOfSystem", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					
					if(UtilValidate.isNotEmpty(request.getAttribute("_ERROR_MESSAGE_"))){
						return "error";
					}
					BigDecimal differentAmount = actualReceivedAmount.subtract(systemAmount);
					posTerminalState.set("differenceAmount", differentAmount);
					
					try {
						posTerminalState.store();
					} catch (GenericEntityException e) {
						Debug.logError("Can not store posTerminalState", module);
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetMoneyFromEmployee", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					//record accounting
					String currencyUomId = posTerminalState.getString("currency");
					Map<String, Object> createAcctransForTakeMoneyMap = FastMap.newInstance();
					createAcctransForTakeMoneyMap.put("userLogin", userLogin);
					String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
					createAcctransForTakeMoneyMap.put("ownerPartyId", ownerPartyId);
					createAcctransForTakeMoneyMap.put("currencyUomId", currencyUomId);
					if(differentAmount.compareTo(BigDecimal.ZERO) > 0){
						createAcctransForTakeMoneyMap.put("glAccountTypeId", "RME_EXCESS");
						createAcctransForTakeMoneyMap.put("differentAmount", differentAmount);
					}else{
						createAcctransForTakeMoneyMap.put("glAccountTypeId", "RME_LOST");
						createAcctransForTakeMoneyMap.put("differentAmount", differentAmount);
					}
					try {
						dispatcher.runSync("createAcctgTransForReceiptMoneyFromEmployee", createAcctransForTakeMoneyMap);
					} catch (GenericServiceException e1) {
						Debug.logError("create acctrans for take money from employee is errored", module);
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetMoneyFromEmployee", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					
					try {
						TransactionUtil.commit();
					} catch (GenericTransactionException e) {
						Debug.logError("Can not commit this transaction", module);
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetMoneyFromEmployee", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				}else{
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotFindWorkShift", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
		} else {
			Debug.logError("You did not login", module);
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		
		return "success";
	}
}
