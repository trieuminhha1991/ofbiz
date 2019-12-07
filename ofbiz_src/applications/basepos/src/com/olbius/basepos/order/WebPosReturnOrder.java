package com.olbius.basepos.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.print.ServiceUI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WebPosReturnOrder {
	public static String resource_error = "BasePosErrorUiLabels";
	private static String module = WebPosReturnOrder.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String quickReturnSalesOrder(HttpServletRequest request, HttpServletResponse response) throws GeneralException{
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);
		GenericValue userLogin =(GenericValue) session.getAttribute("userLogin");
		String orderId = request.getParameter("orderId");
		String facilityId = request.getParameter("facilityId");
		if(UtilValidate.isNotEmpty(userLogin)){
			//begin transaction
			if(UtilValidate.isNotEmpty(orderId)){
				Map<String, Object> returnRes = null;
				Map<GenericValue, Map<String, Object>> returnableItems = null;
				//return items
				try {
					returnRes = dispatcher.runSync("getReturnableItems", UtilMisc.toMap("orderId", orderId));
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(UtilValidate.isNotEmpty(returnRes)){
					returnableItems  = (Map<GenericValue, Map<String, Object>>) returnRes.get("returnableItems");	
				}
				if(UtilValidate.isNotEmpty(returnableItems)){
					try {
						TransactionUtil.begin();
					} catch (GenericTransactionException e) {
						// TODO Auto-generated catch block
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotCreateATransaction", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					// Add tax and grand total value
					GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
					OrderReadHelper orh = new OrderReadHelper(orderHeader);
					List<GenericValue> listAdjustment = orh.getAdjustments();
					BigDecimal adjustmentAmount = new BigDecimal(0);
					for (GenericValue adjustment : listAdjustment) {
						if(adjustment.getString("orderAdjustmentTypeId").equals("SALES_TAX")){
							adjustmentAmount = adjustmentAmount.add(adjustment.getBigDecimal("amount"));
						}
					}
					
					Map<String, Object> quickReturnMap = FastMap.newInstance();
					quickReturnMap.put("userLogin", userLogin);
					quickReturnMap.put("orderId", orderId);
					quickReturnMap.put("facilityId", facilityId);
					quickReturnMap.put("grandTotal", orderHeader.get("grandTotal"));
					quickReturnMap.put("taxTotal", adjustmentAmount);
					quickReturnMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
					Map<String, Object> returnSalesOrder = FastMap.newInstance();
					try {
						returnSalesOrder = dispatcher.runSync("quickReturnOrderPos", quickReturnMap);
					} catch (GenericServiceException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					String returnId = (String) returnSalesOrder.get("returnId");
					WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
					if(UtilValidate.isNotEmpty(webposSession)){
						WebPosTransaction transaction = webposSession.getCurrentTransaction();
						String transactionId = transaction.getTerminalLogId();
						GenericValue terminalLog  = null;
						try {
							terminalLog = delegator.findOne("PosTerminalLog", UtilMisc.toMap("posTerminalLogId", transactionId),false);
						} catch (GenericEntityException e) {
							Debug.logError("Can not find Terminal Log", module);
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						terminalLog.put("statusId", "POSTX_RETURNED");
						terminalLog.put("returnId", returnId);
						terminalLog.set("logEndDateTime", UtilDateTime.nowTimestamp());
						// Append orderId for returnHeader
				        List<GenericValue> tmpList = delegator.findByAnd("ReturnItem", UtilMisc.toMap("returnId", returnId));
				        if(tmpList != null && !tmpList.isEmpty()){
				        	terminalLog.set("orderId", tmpList.get(0).get("orderId"));
				        }
						try {
							terminalLog.store();
						} catch (GenericEntityException e) {
							Debug.logError("Can not change Terminal Log", module);
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						transaction.createPosTerminalLog(delegator, webposSession);
						
						GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
						Map<String, Object> createPosHeader = FastMap.newInstance();
						createPosHeader.put("partyId", returnHeader.get("fromPartyId"));
						createPosHeader.put("orderId", orderId);
						createPosHeader.put("posTerminalLogId", transaction.getTerminalLogId());
						createPosHeader.put("userLogin", userLogin);
			        	createPosHeader.put("returnId", returnId);
			        	createPosHeader.put("returnCreatedBy", userLogin.getString("partyId"));
			        	createPosHeader.put("returnGrandTotal", orderHeader.get("grandTotal"));
			        	createPosHeader.put("returnDate", UtilDateTime.nowTimestamp());
			        	dispatcher.runSync("createPosHistoryReturnRecord", createPosHeader); 
			        	// Get next TerminalLogId & create WebPosTransaction
			        	WebPosTransaction tmpTransaction = new WebPosTransaction(webposSession); 
			        	webposSession.setCurrentTransaction(tmpTransaction);
			        	request.setAttribute("posTerminalLogId", transaction.getTerminalLogId());
			        	request.setAttribute("returnId", returnId);
					} else{
						Debug.logError("webposSession don't exist", module);
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					try {
						TransactionUtil.commit();
					} catch (GenericTransactionException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCommitThisTransaction", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				}else{
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSTheOrderIsReturned", UtilMisc.toMap("orderId", orderId), locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
		}else{
			Debug.logError("You didt not login", module);
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParReturnAbleItems(DispatchContext dctx, Map<String, ? extends Object> context) {
		String orderId = (String)context.get("orderId");
		Map<String, Object> returnMap = ServiceUtil.returnSuccess();
		try {
			Map<String, Object> returnParamsMap = new HashMap<String, Object>();
			returnParamsMap.put("orderId", orderId);
			returnParamsMap.put("userLogin", context.get("userLogin"));
			Map<String, Object> returnRes = dctx.getDispatcher().runSync("getReturnableItems", UtilMisc.toMap("orderId", orderId));
			Map<GenericValue, Map<String, Object>> returnable = (Map<GenericValue, Map<String, Object>>) returnRes.get("returnableItems");
			List<GenericValue> tmpList = new ArrayList<>(returnable.keySet());
			List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
			for (GenericValue genericValue : tmpList) {
				listMap.add(returnable.get(genericValue));
			}
			// Get all returned adjustment Tax
			List<GenericValue> listReturn = dctx.getDelegator().findList("ReturnAdjustmentOrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			if(listReturn != null && !listReturn.isEmpty()){
				List<Map<String, Object>> listAdjMap = new ArrayList<Map<String, Object>>();
				for (GenericValue genericValue : listReturn) {
					Map<String, Object> tmpAdjMap = new HashMap<String, Object>();
					tmpAdjMap.put("orderItemSeqId", genericValue.get("orderItemSeqId"));
					tmpAdjMap.put("amount", genericValue.get("amount"));
					listAdjMap.add(tmpAdjMap);
				}
				returnMap.put("returnAdjustmentMaps", listAdjMap);
			}
			returnMap.put("returnItems", tmpList);
			returnMap.put("returnItemMaps", listMap);
		} catch (Exception e) {
			returnMap = ServiceUtil.returnError("Error in getParReturnAbleItems service(" + module + "):" + e.toString());
		}
		return returnMap;
	}
	
	public static Map<String, Object> getOrderAdjustment(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> returnMap = ServiceUtil.returnSuccess();
		JSONArray jsonOrderItems = JSONArray.fromObject(context.get("orderItems"));
		List<String> orderItemSeqId =  new ArrayList<>();
		if(jsonOrderItems == null || jsonOrderItems.size() < 1){
			returnMap.put("listOrderItemsPromo", new ArrayList<>());
			returnMap.put("listOrderAdjustmentsPromo", new ArrayList<>());
			return returnMap;
		}
		for(int i = 0; i < jsonOrderItems.size();i++){
			orderItemSeqId.add(jsonOrderItems.getJSONObject(i).get("orderItemSeqId").toString());
		}
		List<EntityCondition> listCond = new ArrayList<>();
		EntityCondition ec1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, jsonOrderItems.getJSONObject(0).get("orderId").toString());
		EntityCondition ec2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.IN, orderItemSeqId);
		listCond.add(ec1);
		listCond.add(ec2);
		List<GenericValue> orderItems;
		try {
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, null, false);
			Map<String, Object> returnParamsMap = new HashMap<String, Object>();
			returnParamsMap.put("listOrderItems", orderItems);
			returnParamsMap.put("userLogin", context.get("userLogin"));
			Map<String, Object> returnRes = dctx.getDispatcher().runSync("getListOrderItemsPromoReturn", returnParamsMap);
			List<Map<String, Object>> tmpListOrderItemsPromo = (List<Map<String, Object>>) returnRes.get("listOrderItemsPromo");
			List<Map<String, Object>> tmpListOrderAdjustmentsPromo = (List<Map<String, Object>>) returnRes.get("listOrderAdjustmentsPromo");
			// Filter returned adjustment
			List<String> returnedAdjustment = getReturnedAdjustment(dctx, jsonOrderItems.getJSONObject(0).get("orderId").toString());
			List<Map<String, Object>> listOrderItemsPromo = new ArrayList<>();
			List<Map<String, Object>> listOrderAdjustmentsPromo = new ArrayList<>();
			if(!returnedAdjustment.isEmpty()){
				if(tmpListOrderItemsPromo != null){
					for(int i = 0; i < tmpListOrderItemsPromo.size();i++){
						boolean tmpBl = true;
						for(int j = 0; j < returnedAdjustment.size();j++){
							if(returnedAdjustment.get(j).equals(tmpListOrderItemsPromo.get(i).get("orderAdjustmentId"))){
								tmpBl = false;
								break;
							}
						}
						if(tmpBl){
							listOrderItemsPromo.add(tmpListOrderItemsPromo.get(i));
						}
					}
				}
				if(tmpListOrderAdjustmentsPromo != null){
					for(int i = 0; i < tmpListOrderAdjustmentsPromo.size();i++){
						boolean tmpBl = true;
						for(int j = 0; j < returnedAdjustment.size();j++){
							if(returnedAdjustment.get(j).equals(tmpListOrderAdjustmentsPromo.get(i).get("orderAdjustmentId"))){
								tmpBl = false;
								break;
							}
						}
						if(tmpBl){
							listOrderAdjustmentsPromo.add(tmpListOrderAdjustmentsPromo.get(i));
						}
					}
				}
			}else{
				if(tmpListOrderItemsPromo != null){
					listOrderItemsPromo = tmpListOrderItemsPromo;
				}else{
					listOrderItemsPromo = new ArrayList<>();
				}
				if(tmpListOrderAdjustmentsPromo!= null){
					listOrderAdjustmentsPromo = tmpListOrderAdjustmentsPromo;
				}else{
					listOrderAdjustmentsPromo = new ArrayList<>();
				}
			}
			returnMap.put("listOrderItemsPromo", listOrderItemsPromo);
			returnMap.put("listOrderAdjustmentsPromo", listOrderAdjustmentsPromo);
		} catch (Exception e) {
			returnMap = ServiceUtil.returnError("Error in getOrderAdjustment service(" + module + "):" + e.toString());
		}
		return returnMap;
	}
	
	private static List<String> getReturnedAdjustment(DispatchContext dctx, String orderId) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		List<String> tmpList = new ArrayList<>();
		List<GenericValue> listAdjustment = delegator.findList("OrderAdjustmentReturnAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		if(listAdjustment != null && !listAdjustment.isEmpty()){
			for (GenericValue genericValue : listAdjustment) {
				tmpList.add(genericValue.getString("orderAdjustmentId"));
			}
		}
		return tmpList;
	}
	
	public static String returnPartialOrderEvent(HttpServletRequest request, HttpServletResponse response) throws GeneralException{
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);
		GenericValue userLogin =(GenericValue) session.getAttribute("userLogin");
		String returnItems = request.getParameter("returnItems");
		String orderId = request.getParameter("orderId");
		String returnAdjustment = request.getParameter("returnAdjustment");
		BigDecimal grandTotal = new BigDecimal(request.getParameter("grandTotal"));
		Map<String, Object> quickReturnMap = FastMap.newInstance();
		quickReturnMap.put("userLogin", userLogin);
		quickReturnMap.put("returnItems", returnItems);
		quickReturnMap.put("returnAdjustment", returnAdjustment);
		quickReturnMap.put("grandTotal", grandTotal);
		quickReturnMap.put("orderId", orderId);
		Map<String, Object> returnSalesOrder = FastMap.newInstance();
		try {
			returnSalesOrder = dispatcher.runSync("returnPartialOrder", quickReturnMap);
			request.setAttribute("result", returnSalesOrder.get("result"));
			// Create POSHistory record
			WebPosSession webposSession = (WebPosSession)session.getAttribute("webPosSession");
			List<EntityCondition> listCond = new ArrayList<>();
			EntityCondition ec1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
			EntityCondition ec2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER");
			listCond.add(ec1);
			listCond.add(ec2);
			List<GenericValue> tmpList = delegator.findList("OrderRole", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, null, false);
			Map<String, Object> createPosHeader = FastMap.newInstance();
			if(tmpList != null && !tmpList.isEmpty()){
				createPosHeader.put("partyId", tmpList.get(0).getString("partyId"));
			}else{
				createPosHeader.put("partyId", "_NA_");
			}
			createPosHeader.put("posTerminalLogId", webposSession.getCurrentTransaction().getTerminalLogId());
			createPosHeader.put("userLogin", userLogin);
			createPosHeader.put("returnId", returnSalesOrder.get("result"));
			createPosHeader.put("orderId", orderId);
        	createPosHeader.put("returnCreatedBy", userLogin.getString("partyId"));
        	createPosHeader.put("returnGrandTotal", grandTotal);
        	createPosHeader.put("returnDate", UtilDateTime.nowTimestamp());
        	dispatcher.runSync("createPosHistoryReturnRecord", createPosHeader); 
		} catch (GenericServiceException e) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}
	
	public static Map<String, Object> returnPartialOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> returnMap = ServiceUtil.returnSuccess();
		JSONArray settingsArr = JSONArray.fromObject(context.get("returnItems"));
		BigDecimal grandTotal = (BigDecimal) context.get("grandTotal");
		/*
        <attribute name="taxTotal" type="BigDecimal" mode="IN" optional="true"/>
        <attribute name="returnId" type="String" mode="OUT" optional="false"/>
		 * */
		List<Map<String, Object>> returnItemsList = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < settingsArr.size();i++){
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			BigDecimal quantity = new BigDecimal(settingsArr.getJSONObject(i).get("quantity").toString());
			tmpMap.put("productId", settingsArr.getJSONObject(i).get("productId").toString());
			tmpMap.put("quantity", quantity);
			tmpMap.put("orderItemSeqId", settingsArr.getJSONObject(i).get("orderItemSeqId").toString());
			returnItemsList.add(tmpMap);
			//grandTotal = grandTotal.add(quantity.multiply(new BigDecimal(settingsArr.getJSONObject(i).get("alternativeUnitPrice").toString())));
		}
		String orderId = (String)context.get("orderId");
		Map<String, Object> returnParamsMap = new HashMap<String, Object>();
		returnParamsMap.put("orderId", orderId);
		returnParamsMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
		returnParamsMap.put("receiveReturn", true);
		returnParamsMap.put("userLogin", context.get("userLogin"));
		// Set order adjustment
		JSONArray jsonReturnAdjustment = JSONArray.fromObject(context.get("returnAdjustment"));
		List<String> listReturnAdjustment = new ArrayList<String>();
		if(jsonReturnAdjustment != null && jsonReturnAdjustment.size() > 0){
			for(int i = 0; i < jsonReturnAdjustment.size();i++){
				String tmpAdjustmentId = jsonReturnAdjustment.getJSONObject(i).get("orderAdjustmentId").toString();
				try {
					List<GenericValue> orderItems = dctx.getDelegator().findList("ProductPromoReturnItem", EntityCondition.makeCondition("orderAdjustmentId", EntityOperator.EQUALS, tmpAdjustmentId), null, null, null, false);
					if(orderItems != null && !orderItems.isEmpty()){
						Map<String, Object> tmpMap = new HashMap<String, Object>();
						BigDecimal quantity = orderItems.get(0).getBigDecimal("alternativeQuantity");
						tmpMap.put("productId", orderItems.get(0).getString("productId"));
						tmpMap.put("quantity", quantity);
						tmpMap.put("orderItemSeqId", orderItems.get(0).getString("orderItemSeqId"));
						returnItemsList.add(tmpMap);
						//grandTotal = grandTotal.add(quantity.multiply(orderItems.get(0).getBigDecimal("alternativeUnitPrice")));
					}else{
						GenericValue tmpAdjustment = dctx.getDelegator().findOne("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", tmpAdjustmentId), false);
//						grandTotal = grandTotal.subtract(tmpAdjustment.getBigDecimal("amount"));
					}
				} catch (GenericEntityException e) {
					returnMap = ServiceUtil.returnError("Error in getParReturnAbleItems service(" + module + "):" + e.toString());
				}
				listReturnAdjustment.add(jsonReturnAdjustment.getJSONObject(i).get("orderAdjustmentId").toString());
			}
		}
		returnParamsMap.put("grandTotal", grandTotal);
		returnParamsMap.put("returnOrderItems", returnItemsList);
		returnParamsMap.put("returnedAdjustment", listReturnAdjustment);
		try {
			Map<String, Object> returnRes = dctx.getDispatcher().runSync("partialReturnFromOrder", returnParamsMap);
			returnMap.put("result", returnRes.get("returnId"));
		} catch (GenericServiceException e) {
			returnMap = ServiceUtil.returnError("Error in getParReturnAbleItems service(" + module + "):" + e.toString());
		}
		return returnMap;
	}
}
