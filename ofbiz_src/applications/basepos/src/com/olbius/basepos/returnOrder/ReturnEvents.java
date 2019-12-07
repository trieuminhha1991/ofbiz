package com.olbius.basepos.returnOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ReturnEvents {
	public static String module = ReturnEvents.class.getName();
	public static String resource = "WebPosUiLabels";
	public static String returnDirectly(HttpServletRequest request, HttpServletResponse response){
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin)){
			String errorMessage = UtilProperties.getMessage(resource, "WebPosNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		if(UtilValidate.isNotEmpty(webPosSession)){
			try {
				TransactionUtil.begin(7200);
			} catch (GenericTransactionException e1) {
				Debug.logError("Can not create a transaction", module);
				String errorMessage = UtilProperties.getMessage(resource, "WebPosNotReceiveProduct",UtilHttp.getLocale(request));
    			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			}
			ShoppingCart cart = webPosSession.getCart();
			String facilityId = cart.getFacilityId();
			GenericValue facility = null;
			try {
				facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			} catch (GenericEntityException e) {
				Debug.logError("facilityId: "+ facilityId  +"has not found in system", module);
				String errorMessage = UtilProperties.getMessage(resource, "WebPosNotReceiveProduct",UtilHttp.getLocale(request));
    			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			}
			if(UtilValidate.isEmpty(facility)){
				Debug.logError("facilityId: "+ facilityId  +"has not found in system", module);
				String errorMessage = UtilProperties.getMessage(resource, "WebPosNotReceiveProduct",UtilHttp.getLocale(request));
    			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			}
			List<ShoppingCartItem> cartItemList = getCartItemReturnList(cart);
			String returnId = createReturnHeader(request, cart);
			
			if(returnId.equals("error") && UtilValidate.isEmpty(returnId)){
				return "error";
			}
			session.setAttribute("returnId", returnId);
			
			GenericValue returnHeader = null;
	    	try {
	    		returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
			} catch (GenericEntityException e) {
				Debug.logError("Can not find returnId: " + returnId + "in system", module);
				String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotFindReturnInSystem", UtilMisc.toMap("returnId", returnId) , locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
			//create Return Item
			String createReturnItem = createReturnItem(request, returnId, cartItemList);
        	if(createReturnItem.equals("error")){
        		return "error";
        	}
        	//update Return header
        	String returnHeaderTypeId = returnHeader.getString("returnHeaderTypeId");
        	String updateReturnHeader = null;
        	if(returnHeaderTypeId.equals("CUSTOMER_RETURN")){
        		updateReturnHeader = updateReturnHeader(request, returnHeader, "RETURN_ACCEPTED");
        	}else{
        		updateReturnHeader = updateReturnHeader(request, returnHeader, "SUP_RETURN_ACCEPTED");
        	}
        	if (updateReturnHeader.equals("error")) {
				return "error";
			}
		}
		return "success";
	}
	public static String updateReturnHeader(HttpServletRequest request, GenericValue returnHeader, String statusId){
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String returnId = returnHeader.getString("returnId");
		
    	Map<String, Object> updateReturnHeaderMap = FastMap.newInstance();
    	updateReturnHeaderMap.put("userLogin", userLogin);
    	updateReturnHeaderMap.put("returnId", returnId);
    	updateReturnHeaderMap.put("statusId", statusId);
    	updateReturnHeaderMap.put("needsInventoryReceive", "Y");
    	
    	Map<String, Object> updateReturnHeader = FastMap.newInstance();
    	try {
			updateReturnHeader = dispatcher.runSync("updateReturnHeaderDirectly", updateReturnHeaderMap);
		} catch (GenericServiceException e) {
			String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotUpdateReturn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
    	if(!ServiceUtil.isSuccess(updateReturnHeader)){
    		String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotUpdateReturn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
    	}
    	return "success";
	}
	public static String createReturnHeader(HttpServletRequest request, ShoppingCart cart){
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String facilityId = cart.getFacilityId();
		String currencyUomId = cart.getCurrency();
		String partyId = cart.getBillToCustomerPartyId();
		Map<String, Object> createReturnHeaderMap = FastMap.newInstance();
		createReturnHeaderMap.put("userLogin", userLogin);
		createReturnHeaderMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
		createReturnHeaderMap.put("statusId", "RETURN_REQUESTED");
		createReturnHeaderMap.put("fromPartyId", partyId);
		createReturnHeaderMap.put("destinationFacilityId", facilityId);
		createReturnHeaderMap.put("needsInventoryReceive", "Y");
		createReturnHeaderMap.put("currencyUomId", currencyUomId);
		Map<String, Object> createReturnHeader = FastMap.newInstance();
		try {
			createReturnHeader = dispatcher.runSync("createReturnHeader", createReturnHeaderMap);
		} catch (GenericServiceException e) {
			String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotCreateReturn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		if(!ServiceUtil.isSuccess(createReturnHeader)){
			String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotCreateReturn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		String returnId = (String) createReturnHeader.get("returnId");
		return returnId;
	}
	public static String createReturnItem (HttpServletRequest request, String returnId, List<ShoppingCartItem> cartItemList){
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String returnTypeId = "RTN_REFUND_IMMEDIATE";
		String returnItemTypeId = "RET_FPROD_ITEM";
		for (ShoppingCartItem cartItem : cartItemList) {
			Map<String, Object> createCartItemMap = FastMap.newInstance();
			GenericValue product = cartItem.getProduct();
			String productId = product.getString("productId");
			createCartItemMap.put("userLogin", userLogin);
			createCartItemMap.put("returnId", returnId);
			createCartItemMap.put("returnTypeId", returnTypeId);
			createCartItemMap.put("returnItemTypeId", returnItemTypeId);
			createCartItemMap.put("productId", cartItem.getProductId());
			String quantityUomId = (String) cartItem.getAttribute("quantityUomId");
			BigDecimal returnPrice = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(quantityUomId)){
				BigDecimal alternativeQuantity = cartItem.getAlternativeQuantity();
				returnPrice = cartItem.getAlternativeUnitPrice();
				createCartItemMap.put("returnQuantity", alternativeQuantity.negate());
				createCartItemMap.put("quantityUomId", quantityUomId);
			}else{
				createCartItemMap.put("returnQuantity", cartItem.getQuantity().negate());
				returnPrice = (BigDecimal) cartItem.getBasePrice();
			}
			createCartItemMap.put("returnPrice", returnPrice);
			Map<String, Object> createReturnItemDirectly = FastMap.newInstance();
			try {
				createReturnItemDirectly = dispatcher.runSync("createReturnItemDirectly", createCartItemMap);
			} catch (GenericServiceException e) {
				Debug.logVerbose("Can not create return item directly", module);
				String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnProduct", UtilMisc.toMap("productId", productId), locale);
				request.setAttribute("_ERROR_MESSAGE", errorMessage);
				return "error";
			}
			if(!ServiceUtil.isSuccess(createReturnItemDirectly)){
				Debug.logVerbose("Can not create return item directly", module);
				String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnProduct", UtilMisc.toMap("productId", productId), locale);
				request.setAttribute("_ERROR_MESSAGE", errorMessage);
				return "error";
			}
			
			//create return adjustment
			List<GenericValue> productTax = FastList.newInstance();
			try {
				productTax = delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", productId), null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			if (UtilValidate.isNotEmpty(productTax)){
				BigDecimal taxPercent = productTax.get(0).getBigDecimal("taxPercentage");
				if (taxPercent.compareTo(BigDecimal.ZERO) > 0){
					Map<String, Object> createCartItemAdjMap = FastMap.newInstance(); 
					createCartItemAdjMap.put("returnId", returnId);
					createCartItemAdjMap.put("returnItemSeqId", createReturnItemDirectly.get("returnItemSeqId"));
					createCartItemAdjMap.put("returnTypeId", returnTypeId);
					createCartItemAdjMap.put("returnAdjustmentTypeId", "RET_SALES_TAX_ADJ");
					createCartItemAdjMap.put("description", UtilProperties.getMessage("BasePosUiLabels", "BSReturnSalesTaxNoOrder", locale));
					createCartItemAdjMap.put("shipGroupSeqId", "_NA_");
					createCartItemAdjMap.put("createdDate", UtilDateTime.nowTimestamp());
					createCartItemAdjMap.put("createdByUserLogin", userLogin.getString("partyId"));
					createCartItemAdjMap.put("taxAuthorityRateSeqId", productTax.get(0).getString("taxAuthorityRateSeqId"));
					createCartItemAdjMap.put("sourcePercentage", taxPercent);
					createCartItemAdjMap.put("primaryGeoId", productTax.get(0).getString("originGeoId")); 
					createCartItemAdjMap.put("taxAuthGeoId", productTax.get(0).getString("taxAuthGeoId"));
					createCartItemAdjMap.put("taxAuthPartyId", productTax.get(0).getString("taxAuthPartyId"));
					
					BigDecimal taxAmount = returnPrice.multiply(taxPercent).divide(new BigDecimal(100));
					createCartItemAdjMap.put("amount", taxAmount);
					
					Map<String, Object> createReturnItemAdj = FastMap.newInstance();
					try {
						createReturnItemAdj = dispatcher.runSync("createReturnAdjustment", createCartItemAdjMap);
					} catch (GenericServiceException e) {
						Debug.logVerbose("Can not create return item directly", module);
						String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnProduct", UtilMisc.toMap("productId", productId), locale);
						request.setAttribute("_ERROR_MESSAGE", errorMessage);
						return "error";
					}
					
					if(!ServiceUtil.isSuccess(createReturnItemAdj)){
						Debug.logVerbose("Can not create return item directly", module);
						String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnProduct", UtilMisc.toMap("productId", productId), locale);
						request.setAttribute("_ERROR_MESSAGE", errorMessage);
						return "error";
					}
				}
			}
		}
		return "success";
	}
	public static String quickReturnSalesOrder(HttpServletRequest request, HttpServletResponse response){
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
						String errorMessage = UtilProperties.getMessage(resource, "WebPosNotCreateATransaction", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					
					Map<String, Object> quickReturnMap = FastMap.newInstance();
					quickReturnMap.put("userLogin", userLogin);
					quickReturnMap.put("orderId", orderId);
					quickReturnMap.put("facilityId", facilityId);
					quickReturnMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
					Map<String, Object> returnSalesOrder = FastMap.newInstance();
					try {
						returnSalesOrder = dispatcher.runSync("quickReturnOrder", quickReturnMap);
					} catch (GenericServiceException e) {
						String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
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
							String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						terminalLog.put("statusId", "POSTX_RETURNED");
						terminalLog.put("returnId", returnId);
						terminalLog.set("logEndDateTime", UtilDateTime.nowTimestamp());
						try {
							terminalLog.store();
						} catch (GenericEntityException e) {
							Debug.logError("Can not change Terminal Log", module);
							String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						transaction.createPosTerminalLog(delegator, webposSession);
					}else{
						Debug.logError("webposSession don't exist", module);
						String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotReturnOrder", UtilMisc.toMap("orderId", orderId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					try {
						TransactionUtil.commit();
					} catch (GenericTransactionException e) {
						String errorMessage = UtilProperties.getMessage(resource, "WebPosCanNotCommitThisTransaction", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					
				}else{
					String errorMessage = UtilProperties.getMessage(resource, "WebPosTheOrderIsReturned", UtilMisc.toMap("orderId", orderId), locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
				
			}
		}else{
			Debug.logError("You didt not login", module);
			String errorMessage = UtilProperties.getMessage(resource, "WebPosNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		
		
		
		return "success";
	}
	public static List<ShoppingCartItem> getCartItemReturnList(ShoppingCart cart){
		List<ShoppingCartItem> cartItemReturnList = FastList.newInstance();
		List<ShoppingCartItem> cartItemList = cart.items();
		if(UtilValidate.isNotEmpty(cartItemList)){
			for (ShoppingCartItem cartItem : cartItemList) {
				BigDecimal quantity = cartItem.getQuantity();
				if(quantity.compareTo(BigDecimal.ZERO)<0){
					
					cartItemReturnList.add(cartItem);
				}
			}
		}
		return cartItemReturnList;
	}
}
