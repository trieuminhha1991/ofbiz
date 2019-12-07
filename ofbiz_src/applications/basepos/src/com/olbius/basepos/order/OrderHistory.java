package com.olbius.basepos.order;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;


public class OrderHistory {
	public static final String module = OrderHistory.class.getName();
	public static String resource = "WebPosUiLabels";
	public static String resourceSetting = "WebPosSettingUiLabels";
	public static Map<String, Object> jqGetSaleAndReturnOrderHistory(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("invoiceDate DESC");
    	}
    	//customize condition
    	List<String> invoiceTypeList = FastList.newInstance();
    	invoiceTypeList.add("SALES_INVOICE");
    	invoiceTypeList.add("CUST_RTN_INVOICE");
    	EntityCondition invoiceTypeCond = EntityCondition.makeCondition("invoiceTypeId", EntityOperator.IN, invoiceTypeList);
    	listAllConditions.add(invoiceTypeCond);
    	try {
    		opts.setDistinct(true);
    		listIterator = delegator.find("InvoiceAndSaleOrderAndReturnAndCustomer", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetOrderHistory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	public static Map<String, Object> jqPurchaseOrderHistory(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("orderDate DESC");
		}
		try {
			listIterator = delegator.find("OrderHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqPurchaseOrderHistory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> jqPurchaseOrderForReceiving(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String[] facilityIds = parameters.get("facilityId");
		String facilityId = null;
		if(UtilValidate.isNotEmpty(facilityIds)){
			facilityId = facilityIds[0];
		}
		/*List<EntityCondition> orConditionList = FastList.newInstance();
		orConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_COMPLETED"));
		orConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		orConditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
		
		EntityCondition orCondition = EntityCondition.makeCondition(orConditionList, EntityOperator.AND);
		listAllConditions.add(orCondition);*/
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_COMPLETED"));
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
		if(UtilValidate.isNotEmpty(facilityId)){
			listAllConditions.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		}
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("orderDate DESC");
		}
		try {
			listIterator = delegator.find("OrderHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqPurchaseOrderForReceiving service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static String getInforFromInvoice(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String invoiceId = request.getParameter("invoiceId");
		List<Map<String, Object>> itemList = FastList.newInstance();
		Map<String, Object> partyInfor = FastMap.newInstance();
		if(UtilValidate.isEmpty(userLogin)){
			String errorMessage = UtilProperties.getMessage(resource, "WebPosNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";	
		}
		if(UtilValidate.isEmpty(invoiceId)){
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingMissingInvoice", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		
		EntityCondition invoiceCondition = EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId);
		List<GenericValue> invoiceSaleReturnList = FastList.newInstance();
		try {
			invoiceSaleReturnList = delegator.findList("InvoiceAndSaleOrderAndReturnAndCustomer", invoiceCondition, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GenericValue invoiceSaleReturn = null;
		if(UtilValidate.isNotEmpty(invoiceSaleReturnList)){
			invoiceSaleReturn = EntityUtil.getFirst(invoiceSaleReturnList);
		}
		if(UtilValidate.isEmpty(invoiceSaleReturn)){
			Debug.logError("Can not find invoiceSaleReturn by invoiceId:" + invoiceId, module);
			String errorMessage = UtilProperties.getMessage(resource, "SettingCanNotGetInformationOfInvoice", UtilMisc.toMap("invoiceId", invoiceId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		String currencyUomId = invoiceSaleReturn.getString("currencyUomId");
		String invoiceTypeId = invoiceSaleReturn.getString("invoiceTypeId");
		Timestamp invoiceDate = invoiceSaleReturn.getTimestamp("invoiceDate");
		if(invoiceTypeId.equals("SALES_INVOICE")){
			Map<String, Object> orderHeaderInfo = FastMap.newInstance();
			
			String orderId = invoiceSaleReturn.getString("orderId");
			GenericValue orderHeader = null;
			try {
				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			} catch (GenericEntityException e) {
				Debug.logError("Can not find order: "+ orderId + " in the system", module);
				String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotFindOrder", UtilMisc.toMap("orderId", orderId), locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
			OrderReadHelper orh = new OrderReadHelper(orderHeader);
			//get item list
			String getItemListFromOrder = getItemListFromOrder(request, orh, itemList, currencyUomId);
			if(getItemListFromOrder.equals("error")){
				return "error";
			}
			orderHeaderInfo.put("invoiceId", invoiceId);
			orderHeaderInfo.put("invoiceDate", invoiceDate);
			orderHeaderInfo.put("currencyUomId", currencyUomId);
			getOrderHeaderFromOrder(request, orh, orderHeaderInfo);
			request.setAttribute("orderHeaderInfo", orderHeaderInfo);
			//customer info
			getPartyInfoInSaleAndReturnOrder(invoiceSaleReturn, partyInfor);
			request.setAttribute("partyInfo", partyInfor);
			
			//check the order whether is returned or no
			String getInfoReturnOfOrder = getInfoReturnOfOrder(request, orderHeader);
			if(getInfoReturnOfOrder.equals("error")){
				return "error";
			}
			if(UtilValidate.isNotEmpty(request.getAttribute("orderIsReturned"))){
				orderHeaderInfo.put("orderIsReturned", request.getAttribute("orderIsReturned"));
			}
			
		}else if(invoiceTypeId.equals("CUST_RTN_INVOICE")){
			Map<String, Object> orderHeaderInfo = FastMap.newInstance();
			String returnId = invoiceSaleReturn.getString("returnId");
			GenericValue returnHeader = null;
			try {
				returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
			} catch (GenericEntityException e) {
				Debug.logError("Can not find return: "+ returnId + " in the system", module);
				String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotFindReturn", UtilMisc.toMap("returnId", returnId), locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
			String getItemListFromReturn = getItemListFromReturn(request, returnHeader, itemList, currencyUomId);
			if(getItemListFromReturn.equals("error")){
				return "error";
			}
			
			BigDecimal grandTotal = invoiceSaleReturn.getBigDecimal("amount");
			BigDecimal amountTotal = BigDecimal.ZERO;
			for (Map<String, Object> returnItem : itemList) {
				BigDecimal itemTotal = (BigDecimal) returnItem.get("itemTotal");
				if(UtilValidate.isNotEmpty(itemTotal)){
					amountTotal = amountTotal.add(itemTotal);
				}
			}
			orderHeaderInfo.put("invoiceId", invoiceId);
			orderHeaderInfo.put("invoiceDate", invoiceDate);
			orderHeaderInfo.put("grandTotal", grandTotal);
			orderHeaderInfo.put("amountTotal", amountTotal);
			orderHeaderInfo.put("currencyUomId", currencyUomId);
			getReturnHeaderFromReturn(request, returnHeader, orderHeaderInfo);
			request.setAttribute("orderHeaderInfo", orderHeaderInfo);
			request.setAttribute("partyInfo", partyInfor);
			
		}
		request.setAttribute("itemList", itemList);
		
		return "success";
	}
	public static String getInfoReturnOfOrder(HttpServletRequest request, GenericValue orderHeader){
		//check the order whether is returned or no
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String orderId = orderHeader.getString("orderId");
		Map<String, Object> returnRes = null;
		Map<GenericValue, Map<String, Object>> returnableItems = null;
		//return items
		if (UtilValidate.isNotEmpty(orderHeader)){
			String orderTypeId = orderHeader.getString("orderTypeId");
			if (orderTypeId.equals("SALES_ORDER")){
				try {
					returnRes = dispatcher.runSync("getReturnableItems", UtilMisc.toMap("orderId", orderId));
				} catch (GenericServiceException e) {
					Debug.log("Can not getReturnableItems of order:" + orderId);
					String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotGetOrderInfo", UtilMisc.toMap("orderId", orderId), locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
		}
		if(UtilValidate.isNotEmpty(returnRes)){
			returnableItems  = (Map<GenericValue, Map<String, Object>>) returnRes.get("returnableItems");	
		}
		if(UtilValidate.isEmpty(returnableItems)){
			request.setAttribute("orderIsReturned", true);
		}
		return "success";
	}
	
	public static void getPartyInfoInSaleAndReturnOrder(GenericValue invoiceSaleReturn, Map<String, Object> partyInfor){
		String partyId = invoiceSaleReturn.getString("partyId");
		if(UtilValidate.isNotEmpty(partyId) && !partyId.equals("_NA_")){
			partyInfor.put("partyId", partyId);
			String fullName = null;
			String firstName = invoiceSaleReturn.getString("firstName");
			String middleName = invoiceSaleReturn.getString("middleName");
			String lastName = invoiceSaleReturn.getString("lastName");
			if(UtilValidate.isNotEmpty(firstName)){
				fullName = firstName;
			}
			if(UtilValidate.isNotEmpty(middleName)){
				if(UtilValidate.isNotEmpty(fullName)){
					fullName += " " + middleName;
				}else{
					fullName = middleName;
				}
				
			}
			if(UtilValidate.isNotEmpty(lastName)){
				if(UtilValidate.isNotEmpty(fullName)){
					fullName += " " + lastName;
				}else{
					fullName = lastName;
				}
				
			}
			partyInfor.put("partyName", fullName);
			
			String partyAddress = null;
			String billingAddress = invoiceSaleReturn.getString("addressBilling1");
			String cityBilling = invoiceSaleReturn.getString("cityBilling");
			if(UtilValidate.isNotEmpty(billingAddress)){
				partyAddress = billingAddress;
			}
			if(UtilValidate.isNotEmpty(cityBilling)){
				if(UtilValidate.isNotEmpty(partyAddress)){
					partyAddress += " " + cityBilling;
				}else{
					partyAddress = cityBilling;
				}
			}
			if(UtilValidate.isEmpty(partyAddress)){
				String shippingAddress = invoiceSaleReturn.getString("addressShipping1");
				String cityShipping = invoiceSaleReturn.getString("cityShipping");
				if(UtilValidate.isNotEmpty(shippingAddress)){
					partyAddress = shippingAddress;
				}
				if(UtilValidate.isNotEmpty(cityShipping)){
					if(UtilValidate.isNotEmpty(partyAddress)){
						partyAddress += " " + cityShipping;
					}else{
						partyAddress = cityShipping;
					}
				}
			}
			if(UtilValidate.isNotEmpty(partyAddress)){
				partyInfor.put("partyAddress", partyAddress);
			}
			String partyPhone = null;
			String phoneMobile = invoiceSaleReturn.getString("phoneMobile");
			String phoneWork = invoiceSaleReturn.getString("phoneWork");
			String phoneHome = invoiceSaleReturn.getString("phoneHome");
			if(UtilValidate.isNotEmpty(phoneMobile)){
				partyPhone = phoneMobile;
			}
			if(UtilValidate.isEmpty(partyPhone)){
				if(UtilValidate.isNotEmpty(phoneHome)){
					partyPhone =  phoneHome;
				}
			}
			if(UtilValidate.isEmpty(partyPhone)){
				if(UtilValidate.isNotEmpty(phoneWork)){
					partyPhone = phoneWork;
				}
			}
			if(UtilValidate.isNotEmpty(partyPhone)){
				partyInfor.put("partyPhone", partyPhone);
			}
		}
		
		
	}
	public static String getItemListFromReturn(HttpServletRequest request, GenericValue returnHeader, List<Map<String, Object>> itemList, String currencyUomId){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		String returnId = returnHeader.getString("returnId");
		List<GenericValue> returnItemList = FastList.newInstance();
		EntityCondition returnCondition = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
		try {
			returnItemList = delegator.findList("ReturnItem", returnCondition, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError("Can not get return item of return:" + returnId, module);
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotGetInformationOfReturn",UtilMisc.toMap("returnId", returnId) ,locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		if(UtilValidate.isNotEmpty(returnItemList)){
			for (GenericValue returnItem : returnItemList) {
				Map<String, Object> returnItemTmp = FastMap.newInstance();
				String productId = returnItem.getString("productId");
				GenericValue product = null;
				try {
					product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				} catch (GenericEntityException e) {
					Debug.logError("Can not find product: "+ productId + " in the system", module);
					String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotFindProduct", UtilMisc.toMap("productId", productId), locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
				String quantityUomId = returnItem.getString("quantityUomId");
				String returnItemSeqId = returnItem.getString("returnItemSeqId");
				Map<String, String> returnItemAdjustmentMap = FastMap.newInstance();
				returnItemAdjustmentMap.put("returnId", returnId);
				returnItemAdjustmentMap.put("returnItemSeqId", returnItemSeqId);
				EntityCondition returnItemAdjCond = EntityCondition.makeCondition(returnItemAdjustmentMap, EntityOperator.AND);
				BigDecimal adjustment = BigDecimal.ZERO;
				List<GenericValue> returnItemAdjustmentList = FastList.newInstance();
				
				try {
					returnItemAdjustmentList = delegator.findList("ReturnAdjustment", returnItemAdjCond, null, null, null, false);
					
				} catch (GenericEntityException e) {
					Debug.logError("Can not get adjustment of returnItemSeqId: " + returnItemSeqId, module);
					String errorMessage = UtilProperties.getMessage(resource,"SettingCanNotGetAdjustmentOfReturnItem", UtilMisc.toMap("productId", productId) , locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
				if(UtilValidate.isNotEmpty(returnItemAdjustmentList)){
					for (GenericValue returnItemAdjustment : returnItemAdjustmentList) {
						BigDecimal adjustmentTmp = returnItemAdjustment.getBigDecimal("amount");
						adjustment = adjustment.add(adjustmentTmp);
					}
				}
				String idValue = null;
				if(UtilValidate.isNotEmpty(quantityUomId)){
					GenericValue barcode = null;
					try {
						barcode = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "SKU", "uomId", quantityUomId), null, false));
					} catch (GenericEntityException e) {
						Debug.logError("Can not get barcode for productId: " + productId + "in the system", module);
						String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotGetInfoOfProduct", UtilMisc.toMap("productId", productId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					if (UtilValidate.isNotEmpty(barcode)){
						idValue = barcode.getString("idValue");
					}
				}
				if(UtilValidate.isNotEmpty(idValue)){
					returnItemTmp.put("idValue", idValue);
				}
				
				returnItemTmp.put("currencyUomId", currencyUomId);
				returnItemTmp.put("productId", productId);
				returnItemTmp.put("internalName", product.getString("internalName"));
				if(UtilValidate.isNotEmpty(quantityUomId)){
					returnItemTmp.put("quantityUomId", quantityUomId);
				}
				BigDecimal quantity = returnItem.getBigDecimal("receivedQuantity");
				BigDecimal unitPrice = returnItem.getBigDecimal("returnPrice");
				returnItemTmp.put("quantity", quantity);
				returnItemTmp.put("unitPrice", unitPrice);
				if(UtilValidate.isNotEmpty(adjustment)){
					returnItemTmp.put("adjustment", adjustment.negate());
				}
				
				BigDecimal itemTotal = quantity.multiply(unitPrice).add(adjustment);
				returnItemTmp.put("itemTotal", itemTotal);
				itemList.add(returnItemTmp);
			}
		}
		return "success";
	}
	public static String getOrderHeaderFromOrder(HttpServletRequest request, OrderReadHelper orh, Map<String, Object> orderHeaderInfo){
		BigDecimal orderTotal = orh.getOrderItemsTotal();
		BigDecimal grandTotal = orh.getOrderGrandTotal();
		BigDecimal orderAdjustment = orh.getOrderAdjustmentsTotal();
		orderHeaderInfo.put("amountTotal", orderTotal);
		orderHeaderInfo.put("grandTotal", grandTotal);
		orderHeaderInfo.put("adjustment", orderAdjustment.negate());
		return "success";
	}
	public static String getReturnHeaderFromReturn(HttpServletRequest request, GenericValue returnHeader, Map<String, Object> returnHeaderInfo){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		String returnId = returnHeader.getString("returnId");
		Map<String, String> returnAdjustmentMap = FastMap.newInstance();
		returnAdjustmentMap.put("returnId", returnId);
		returnAdjustmentMap.put("returnItemSeqId", "_NA_");
		EntityCondition returnAdjustmentCon = EntityCondition.makeCondition(returnAdjustmentMap, EntityOperator.AND);
		List<GenericValue> returnAdjustmentList = FastList.newInstance();
		try {
			returnAdjustmentList = delegator.findList("ReturnAdjustment", returnAdjustmentCon, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError("Can not get adjustment of return: " + returnId, module);
			String errorMessage = UtilProperties.getMessage(resource, "SettingCanNotGetAdjustmentOfReturn", UtilMisc.toMap("returnId", returnId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		BigDecimal adjustment = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(returnAdjustmentList)){
			for (GenericValue returnAdjustment : returnAdjustmentList) {
				BigDecimal adjustmentTmp = returnAdjustment.getBigDecimal("amount");
				adjustment = adjustment.add(adjustmentTmp);
			}
			if(UtilValidate.isNotEmpty(adjustment)){
				returnHeaderInfo.put("adjustment", adjustment.negate());
			}
		}
		
		return "success";
	}
	public static String getItemListFromOrder(HttpServletRequest request, OrderReadHelper orh, List<Map<String, Object>> itemList, String currencyUomId){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		List<GenericValue> orderItems = FastList.newInstance();
		orderItems = orh.getOrderItems();
		if(UtilValidate.isNotEmpty(orderItems)){
			for (GenericValue orderItem : orderItems) {
				Map<String, Object> orderItemTmp = FastMap.newInstance();
				String productId = orderItem.getString("productId");
				GenericValue product = null;
				try {
					product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				} catch (GenericEntityException e) {
					Debug.logError("Can not find product: "+ productId + " in the system", module);
					String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotFindProduct", UtilMisc.toMap("productId", productId), locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
				orderItemTmp.put("currencyUomId", currencyUomId);
				orderItemTmp.put("productId", productId);
				orderItemTmp.put("internalName", product.getString("internalName"));
				String quantityUomId = orderItem.getString("quantityUomId");
				String idValue = null;
				if(UtilValidate.isNotEmpty(quantityUomId)){
					orderItemTmp.put("quantityUomId", quantityUomId);
					orderItemTmp.put("quantity", orderItem.getBigDecimal("alternativeQuantity"));
					orderItemTmp.put("unitPrice", orderItem.getBigDecimal("alternativeUnitPrice"));
					GenericValue barcode = null;
					try {
						barcode = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "SKU", "uomId", quantityUomId), null, false));
					} catch (GenericEntityException e) {
						Debug.logError("Can not get barcode for productId: " + productId + "in the system", module);
						String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotGetInfoOfProduct", UtilMisc.toMap("productId", productId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					if (UtilValidate.isNotEmpty(barcode)){
						idValue = barcode.getString("idValue");
					}
				}else{
					orderItemTmp.put("quantity", orderItem.getBigDecimal("quantity"));
					orderItemTmp.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
				}
				if(UtilValidate.isNotEmpty(idValue)){
					orderItemTmp.put("idValue", idValue);
				}
				BigDecimal adjustment = orh.getOrderItemAdjustmentsTotal(orderItem);
				if(UtilValidate.isNotEmpty(adjustment)){
					orderItemTmp.put("adjustment", adjustment.negate());
				}
				
				BigDecimal itemTotal = orh.getOrderItemSubTotal(orderItem);
				if(UtilValidate.isNotEmpty(itemTotal)){
					orderItemTmp.put("itemTotal", itemTotal);
				}
				itemList.add(orderItemTmp);
			}
		}
		return "success";
		
	}
	public static String getInfoFromPurchase(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin)){
			String errorMessage = UtilProperties.getMessage(resource, "WebPosNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";	
		}
		Security security = (Security) request.getAttribute("security");
		if(!security.hasEntityPermission("CALCULATE_PO", "_VIEW", userLogin)){
			String errorMessage = UtilProperties.getMessage(resource, "SettingDoNotHavePermissionToDo", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		String orderId = request.getParameter("orderId");
		GenericValue orderHeader = null ;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resource, "SettingCanNotFindOrder", UtilMisc.toMap("orderId", orderId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		List<Map<String, Object>> itemList = FastList.newInstance();
		String currencyUomId = orderHeader.getString("currencyUom");
		Map<String, Object> partyInfor = FastMap.newInstance();
		Map<String, Object> orderHeaderInfo = FastMap.newInstance();
		OrderReadHelper orh = new OrderReadHelper(orderHeader);
		//get item list
		String getItemListFromOrder = getItemListFromOrder(request, orh, itemList, currencyUomId);
		if(getItemListFromOrder.equals("error")){
			return "error";
		}
		request.setAttribute("itemList", itemList);
		Timestamp orderDate = orderHeader.getTimestamp("orderDate");
		orderHeaderInfo.put("orderId", orderId);
		orderHeaderInfo.put("statusId", orderHeader.getString("statusId"));
		orderHeaderInfo.put("facilityId", orderHeader.getString("originFacilityId"));
		orderHeaderInfo.put("orderDate", orderDate);
		orderHeaderInfo.put("currencyUomId", currencyUomId);
		getOrderHeaderFromOrder(request, orh, orderHeaderInfo);
		request.setAttribute("orderHeaderInfo", orderHeaderInfo);
		//customer info
		try {
			getPartyInfoFromPurchase(request, orderHeader, partyInfor);
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotGetInfoOfOrder", UtilMisc.toMap("orderId", orderId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		request.setAttribute("partyInfo", partyInfor);
		return "success";
	}
	public static void setSupplierInfo(Delegator delegator, String partyId, Map<String, Object> partyInfor) throws GenericEntityException{
		partyInfor.put("partyId", partyId);
		String partyAddress = null;
		String partyPhone = null;
		String groupName = null;
		EntityCondition partyCond = EntityCondition.makeCondition("partyGroupId", EntityOperator.EQUALS, partyId);
		List<GenericValue> partyList = delegator.findList("PartyAndSupplierRelationshipInformation", partyCond, null, null, null, false);
		if(UtilValidate.isNotEmpty(partyList)){
			GenericValue party = EntityUtil.getFirst(partyList);
			groupName = party.getString("groupName");
			String companyAddress = party.getString("companyAddress");
			String companyCity = party.getString("companyCity");
			String companyPhone = party.getString("companyPhone");
			if(UtilValidate.isNotEmpty(companyAddress)){
				partyAddress = companyAddress;
			}
			if(UtilValidate.isNotEmpty(companyCity)){
				partyAddress += " - " + companyCity;
			}
			if(UtilValidate.isNotEmpty(companyPhone)){
				partyPhone = companyPhone;
			}
		}
		partyInfor.put("partyName", groupName);
		partyInfor.put("partyAddress", partyAddress);
		partyInfor.put("partyPhone", partyPhone);
	}
	public static void getPartyInfoFromPurchase(HttpServletRequest request, GenericValue orderHeader, Map<String, Object> partyInfor) throws GenericEntityException{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		OrderReadHelper orh = new OrderReadHelper(orderHeader);
		GenericValue supplier = orh.getBillFromParty();
		String partyId = supplier.getString("partyId");
		setSupplierInfo(delegator, partyId, partyInfor);
	}
	public static String getInfoFromShipment(HttpServletRequest request, HttpServletResponse response){
    	HttpSession session = request.getSession();
    	Locale locale = UtilHttp.getLocale(request);
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    	if(UtilValidate.isEmpty(userLogin)){
    		String errorMessage = UtilProperties.getMessage(resource, "WebPosNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
    	}
    	Security security = (Security) request.getAttribute("security");
		if(!security.hasEntityPermission("REC", "_VIEW", userLogin)){
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingDoNotHavePermissionToDo", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		String shipmentId = request.getParameter("shipmentId");
		if(UtilValidate.isEmpty(shipmentId)){
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingMissingShipmentIdToDisplay", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		
		EntityCondition shipmentInvoiceCond = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
		GenericValue shipmentInvoice = null;
		try {
			List<GenericValue> shipmentInvoiceList = FastList.newInstance();
			
			shipmentInvoiceList = delegator.findList("ShipmentAndInvoiceForReceiving", shipmentInvoiceCond, null, null, null, false);
			if(UtilValidate.isNotEmpty(shipmentInvoiceList)){
				shipmentInvoice = shipmentInvoiceList.get(0);
			}
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotFindShipment", UtilMisc.toMap("shipmentId", shipmentId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} 
		List<Map<String, Object>> itemList = FastList.newInstance();
		String currencyUomId = shipmentInvoice.getString("currencyUomId");
		Map<String, Object> partyInfo = FastMap.newInstance();
		Map<String, Object> receivingHeaderInfo = FastMap.newInstance(); 
		try {
			getItemListFromShipment(request, shipmentInvoice, itemList);
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotGetInfoOfShipment",UtilMisc.toMap("shipmentId", shipmentId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		request.setAttribute("itemList", itemList);
		try {
			getReceivingHeaderFromShipment(request, shipmentInvoice, receivingHeaderInfo);
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotGetInfoOfShipment",UtilMisc.toMap("shipmentId", shipmentId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		receivingHeaderInfo.put("currencyUomId", currencyUomId);
		request.setAttribute("receivingHeaderInfo", receivingHeaderInfo);
		String partyId = shipmentInvoice.getString("partyIdFrom");
		try {
			setSupplierInfo(delegator, partyId, partyInfo);
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotGetInfoOfShipment",UtilMisc.toMap("shipmentId", shipmentId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		request.setAttribute("partyInfo", partyInfo);
    	return "success";
    }
	public static void getItemListFromShipment(HttpServletRequest request, GenericValue shipmentInvoice, List<Map<String, Object>> itemList) throws GenericEntityException{
		String shipmentId = shipmentInvoice.getString("shipmentId");
		String currencyUomId = shipmentInvoice.getString("currencyUomId");
		String invoiceId = shipmentInvoice.getString("invoiceId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> shipmentItemList = FastList.newInstance();
		EntityCondition shipmentItemCond = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
		shipmentItemList = delegator.findList("ShipmentItem", shipmentItemCond, null, null, null, false);
		for (GenericValue shipmentItem : shipmentItemList) {
			Map<String, Object> item = FastMap.newInstance();
			String productId = shipmentItem.getString("productId");
			String quantityUomId = shipmentItem.getString("quantityUomId");
			GenericValue barcode = null;
			String idValue = null;
			if(UtilValidate.isNotEmpty(quantityUomId)){
				barcode = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "SKU", "uomId", quantityUomId), null, false));
				if (UtilValidate.isNotEmpty(barcode)){
					idValue = barcode.getString("idValue");
				}
			}
			
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			BigDecimal quantity = shipmentItem.getBigDecimal("quantity");
			BigDecimal unitCost = shipmentItem.getBigDecimal("unitCost");
			BigDecimal adjustment = BigDecimal.ZERO;
			if(UtilValidate.isEmpty(unitCost)){
				unitCost = BigDecimal.ZERO;
			}
			Map<String, Object> invoiceItemMap = FastMap.newInstance();
			invoiceItemMap.put("productId", productId);
			invoiceItemMap.put("invoiceId", invoiceId);
			/*if(UtilValidate.isNotEmpty(quantityUomId)){
				invoiceItemMap.put("quantityUomId", quantityUomId);
			}*/
			invoiceItemMap.put("invoiceItemTypeId", "PITM_DISCOUNT_ADJ");
			EntityCondition invoiceItemCond = EntityCondition.makeCondition(invoiceItemMap, EntityOperator.AND);
			List<GenericValue> adjustmentList  = delegator.findList("InvoiceItem", invoiceItemCond, null, null, null, false);
			
			if(UtilValidate.isNotEmpty(adjustmentList)){
				GenericValue adjustmentEntity = adjustmentList.get(0);
				if(UtilValidate.isNotEmpty(adjustmentEntity)){
					 adjustment =  adjustmentEntity.getBigDecimal("amount");
				}
			}
			
			item.put("productId", productId);
			item.put("internalName", product.getString("internalName"));
			item.put("quantityUomId", quantityUomId);
			item.put("idValue", idValue);
			item.put("quantity", quantity);
			item.put("unitPrice", unitCost);
			item.put("adjustment", adjustment);
			item.put("currencyUomId", currencyUomId);
			BigDecimal itemTotal = quantity.multiply(unitCost);
			itemTotal = itemTotal.subtract(adjustment);
			item.put("itemTotal", itemTotal);
			itemList.add(item);
			
		}
	}
	public static void getReceivingHeaderFromShipment(HttpServletRequest request, GenericValue shipmentInvoice, Map<String, Object> receivingHeaderInfo) throws GenericEntityException{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String invoiceId = shipmentInvoice.getString("invoiceId");
		String shipmentId = shipmentInvoice.getString("shipmentId");
		/*List<GenericValue> invoiceItemList = FastList.newInstance();*/
		BigDecimal amount = shipmentInvoice.getBigDecimal("amount");
		BigDecimal amountPaid = shipmentInvoice.getBigDecimal("amountApplied");
		BigDecimal amountNotApply = shipmentInvoice.getBigDecimal("amountNotApply");
		BigDecimal adjustment = BigDecimal.ZERO;
		Map<String, String> invoiceItemCondList = FastMap.newInstance();
		invoiceItemCondList.put("invoiceId", invoiceId);
		invoiceItemCondList.put("invoiceItemSeqId", "_NA_");
		/*EntityCondition invoiceItemCond = EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId);*/
		GenericValue invoiceItem = delegator.findOne("InvoiceItem", invoiceItemCondList, false);
		if (UtilValidate.isNotEmpty(invoiceItem)) {
			adjustment = invoiceItem.getBigDecimal("amount");
		}
		BigDecimal amountTotal = amount.subtract(adjustment);
		receivingHeaderInfo.put("shipmentId", shipmentId);
		receivingHeaderInfo.put("invoiceId", invoiceId);
		receivingHeaderInfo.put("amountTotal", amountTotal);
		receivingHeaderInfo.put("grandTotal", amount);
		receivingHeaderInfo.put("adjustment", adjustment.negate());
		receivingHeaderInfo.put("amountPaid", amountPaid);
		receivingHeaderInfo.put("amountNotApply", amountNotApply);
		
	
	}
	public static String getReceivingHeaderInfo(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String shipmentId = request.getParameter("shipmentId");
		EntityCondition shipmentInvoiceCond = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
		List<GenericValue> shipmentInvoiceList = FastList.newInstance();
		Map<String, Object> receivingHeaderInfo = FastMap.newInstance();
		try {
			shipmentInvoiceList = delegator.findList("ShipmentAndInvoiceForReceiving", shipmentInvoiceCond, null, null, null, false);
			if(UtilValidate.isNotEmpty(shipmentInvoiceList)){
				GenericValue shipmentInvoice = shipmentInvoiceList.get(0);
				String currencyUomId = shipmentInvoice.getString("currencyUomId");
				getReceivingHeaderFromShipment(request, shipmentInvoice, receivingHeaderInfo);
				receivingHeaderInfo.put("currencyUomId", currencyUomId);
				request.setAttribute("receivingHeaderInfo", receivingHeaderInfo);
			}
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resourceSetting, "SettingCanNotFindShipment", UtilMisc.toMap("shipmentId", shipmentId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}
}
