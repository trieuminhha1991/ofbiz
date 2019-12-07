package org.ofbiz.mobileservices;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.Mobile;
import org.ofbiz.ProcessMobileApps;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrderEvents implements Mobile {
	public static final String module = OrderEvents.class.getName();
	private static ProcessMobileApps process = new ProcessMobileApps();
	public static String result = "";
	public static String currencyUom = null;
	public static final String resource = "BaseSalesErrorUiLabels";

	public static String createCart(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		HttpSession session = request.getSession();
		Boolean isCustomerApp = dispatcher.getName().equals("mobilecustomer");
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");

		if(cart != null){
			ShoppingCartEvents.destroyCart(request,response);
			/*request.setAttribute(Mobile.ERROR_MESSAGE, "Cart is exist");*/
//			request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSCartIsExist", locale));
//			return "modifycart";
		}
		// cart = ShoppingCartEvents.getCartObject(request);
		
		try {
			String productStoreId = request.getParameter("productStoreId");
			String partyId = request.getParameter("customerId");
			String salesExecutiveId = userLogin.getString("partyId");
			
			List<EntityCondition> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyId", salesExecutiveId));
			if(isCustomerApp){
				exprs.add(EntityCondition.makeCondition("roleTypeId", EntityUtilProperties.getPropertyValue("basesales.properties", "role.customer.in.store", "CUSTOMER", delegator)));
			}else
				exprs.add(EntityCondition.makeCondition("roleTypeId", EntityUtilProperties.getPropertyValue("basesales.properties", "role.sell.in.store", "SELLER", delegator)));
			if (UtilValidate.isNotEmpty(productStoreId)) exprs.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			exprs.add(EntityUtil.getFilterByDateExpr());
	
			List<GenericValue> listProductStore = null;
			try {
				listProductStore = delegator.findList("ProductStorePartyView", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
	
			if (UtilValidate.isEmpty(listProductStore)) {
				/*request.setAttribute(Mobile.ERROR_MESSAGE, "You don't have permission to sell product in product store");*/
				request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSNotHavePermissionSellProductInStore", locale));
				return "error";
			}
	
			GenericValue productStore = listProductStore.get(0);
			productStoreId = productStore.getString("productStoreId");
	
			Map<String, Object> inp = FastMap.newInstance();
			inp.put("userLogin", userLogin);
			inp.put("partyId", partyId);
			inp.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
			Map<String, Object> outp = dispatcher.runSync("getPartyPostalAddress", inp);
			String contactMechId = (String) outp.get("contactMechId");
			
			String placingCustomerId = null;
			
			// Check party parent payment order of this customer
			EntityFindOptions optsLimitOne = new EntityFindOptions();
			optsLimitOne.setLimit(1);
			exprs.clear();
			exprs.add(EntityCondition.makeCondition("partyIdTo", partyId));
			exprs.add(EntityCondition.makeCondition("roleTypeIdFrom", "CUSTOMER"));
			exprs.add(EntityCondition.makeCondition("roleTypeIdTo", "CHILD_MEMBER"));
			exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", "OWNER"));
			exprs.add(EntityUtil.getFilterByDateExpr());
			GenericValue listCustomerMember = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs), null, null, optsLimitOne, false));
			if (UtilValidate.isEmpty(listCustomerMember)) {
				placingCustomerId = partyId;
			} else {
				placingCustomerId = listCustomerMember.getString("partyIdFrom");
			}
			// check role placing customer is customer of product store
			exprs.clear();
			if (!partyId.equals(placingCustomerId)) {
				exprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId), 
						EntityOperator.OR, EntityCondition.makeCondition("partyId", placingCustomerId)));
			} else {
				exprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId)));
			}
			exprs.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			exprs.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			exprs.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listPlacingCustomer = null;
			try {
				List<GenericValue> listProductStoreCustomer = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(exprs), null, null, optsLimitOne, false);
				listPlacingCustomer = delegator.findList("PartyFullNameDetailSimple", EntityCondition.makeCondition("partyId", placingCustomerId), null, null, null, false);
				if (UtilValidate.isEmpty(listProductStoreCustomer)) {
					/*request.setAttribute(Mobile.ERROR_MESSAGE, "This customer isn't customer of this product store");*/
					if (listPlacingCustomer.size()>0){
						request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSCustomerIsNotExistInStore", UtilMisc.toMap("customerId", listPlacingCustomer.get(0).getString("partyCode")), locale));
					}else {
						request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSCustomerIsNotExistInStore", UtilMisc.toMap("customerId", placingCustomerId), locale));
					}
					return "error";
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				/*request.setAttribute(Mobile.ERROR_MESSAGE, "Error when check role customer " + placingCustomerId + " in product store");*/
				request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSErrorCheckRoleCustomerInStore", UtilMisc.toMap("customerId", placingCustomerId), locale));
				return "error";
			}
			
			String orderMode = "SALES_ORDER";
	    	//ShoppingCart cart = null; // ShoppingCartEvents.getCartObject(request);
	    	
	    	Map<String, Object> paramMapCtx = UtilHttp.getCombinedMap(request);
	    	if (paramMapCtx == null) paramMapCtx = FastMap.newInstance();
	    	paramMapCtx.put("shoppingCart", cart);
	    	paramMapCtx.put("userLogin", userLogin);
	    	paramMapCtx.put("locale", locale);
	    	
	    	paramMapCtx.put("productStoreId", productStoreId);
	    	paramMapCtx.put("partyId", partyId);
	    	paramMapCtx.put("shipToCustomerPartyId", partyId);
	    	paramMapCtx.put("salesExecutiveId", salesExecutiveId);
	    	paramMapCtx.put("salesChannelEnumId", "MOBILE_SALES_CHANNEL");
	    	paramMapCtx.put("shipping_contact_mech_id", contactMechId);
	    	paramMapCtx.put("shipping_method", "NO_SHIPPING@_NA_");
	    	paramMapCtx.put("listProd", request.getParameter("products"));
	    	// paramMapCtx.put("shipping_instructions", "");
	    	paramMapCtx.put("may_split", "false");
	    	paramMapCtx.put("is_gift", "false");
	    	Long desiredDeliveryDate = UtilDateTime.nowTimestamp().getTime() + 300000;
	    	paramMapCtx.put("desiredDeliveryDate", desiredDeliveryDate.toString());
	    	
	    	session.setAttribute("shipping_contact_mech_id", contactMechId);
	    	session.setAttribute("shipping_method", "NO_SHIPPING@_NA_");
	    	session.setAttribute("checkOutPaymentId", "EXT_COD");
	    	session.setAttribute("may_split", "false");
	    	session.setAttribute("is_gift", "false");
	    	
	    	String[] checkOutPaymentIdArr = request.getParameterValues("checkOutPaymentId");
	    	List<String> checkOutPaymentId = null;
	    	if (checkOutPaymentIdArr != null) checkOutPaymentId = Arrays.asList(checkOutPaymentIdArr);
	    	if (UtilValidate.isEmpty(checkOutPaymentId)) checkOutPaymentId = UtilMisc.toList("EXT_COD");
	    	paramMapCtx.put("checkOutPaymentId", checkOutPaymentId);
	    	
	    	// currency uom
	    	String iso = UtilHttp.getCurrencyUom(session, null);
	    	paramMapCtx.put("currencyUom", iso);
	    	
	    	Map<String, Object> shoppingCartCtx = ServiceUtil.setServiceFields(dispatcher, "initializeSalesOrderEntry", paramMapCtx, userLogin, null, locale);
	    	
	    	Map<String, Object> resultValue = dispatcher.runSync("initializeSalesOrderEntry", shoppingCartCtx);
	    	if (ServiceUtil.isError(resultValue)) {
	    		request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultValue));
				org.ofbiz.order.shoppingcart.ShoppingCartEvents.destroyCart(request, response);
				session.removeAttribute("orderMode");
				return "error";
	    	}
	    	cart = (ShoppingCart) resultValue.get("shoppingCart");
	        session.setAttribute("shoppingCart", cart);
	        
	        if (cart != null) {
	        	session.setAttribute("orderMode", orderMode);
	        	session.setAttribute("productStoreId", cart.getProductStoreId());
	        } else {
	        	session.removeAttribute("orderMode");
	        }
	        
	        if (UtilValidate.isNotEmpty(request.getParameter("issuerId"))) {
	            request.setAttribute("issuerId", request.getParameter("issuerId"));
	        }
		} catch (Exception e) {
			Debug.logError(e, module);
			/*request.setAttribute("_ERROR_MESSAGE_", "Have a error when create cart");*/
			request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSErrorCreateCart", locale));
            return "error";
		}
		
		return "success";
	}

	public static String modifyCart(HttpServletRequest request, HttpServletResponse response){
		String productsStr = new String(request.getParameter("products"));
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = request.getLocale();
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		if(cart == null){
			/*request.setAttribute(Mobile.ERROR_MESSAGE, "Cart is empty");*/
			request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSCartIsEmpty", locale));
			return "error";
		}
		if(UtilValidate.isNotEmpty(productsStr)){
			List<ShoppingCartItem> items = cart.items();
			List<ShoppingCartItem> removed = FastList.newInstance();
			JSONArray products = JSONArray.fromObject(productsStr);
			JSONArray productsAdd = JSONArray.fromObject(productsStr);
			int deleted = 0;
			int i = 0;
			int index = 0;
			for(ShoppingCartItem item : items){
				StringBuilder productTmp = null;
				boolean isFinded = false;
//				deleted = 0;
				for (i = 0; i < products.size(); i++) {
					JSONObject product = products.getJSONObject(i);
					productTmp = new StringBuilder(product.getString("productId"));
					if(productTmp.toString().equals(item.getProductId())){
						isFinded = true;
						index = i < deleted ? i : (i - deleted);
						if(index < productsAdd.size()){
							productsAdd.remove(index);
						}else{
							removeProduct(productsAdd, productTmp.toString());
						}
						deleted++;
						try {
							item.setQuantity(BigDecimal.valueOf(product.getDouble("quantity")), dispatcher, cart);
						} catch (CartItemModifyException e) {
							Debug.log(e.getMessage());
						}
						break;
					}
				}
				if(!isFinded){
					removed.add(item);
				}
			}
			if(UtilValidate.isNotEmpty(removed)){
				for(ShoppingCartItem item : removed){
					try {
		                cart.removeCartItem(cart.getItemIndex(item), dispatcher);
		            } catch (CartItemModifyException e) {
				Debug.log(e.getMessage());
		            }
				}
			}
			if(productsAdd.size() != 0){
				addProductsToCart(productsAdd, cart, dispatcher);
			}
		}
		return "success";
	}
	public static void removeProduct(JSONArray arr, String productId){
		for(int i = 0; i < arr.size(); i++){
			JSONObject o = arr.getJSONObject(i);
			if(o.getString("productId").equals(productId)){
				arr.remove(i);
				break;
			}
		}
	}
	public static String submitOrder(HttpServletRequest request, HttpServletResponse response) {
		try {
			Delegator delegator = (Delegator) request.getAttribute("delegator");
	        Locale locale = UtilHttp.getLocale(request);
			String productStoreId = request.getParameter("productStoreId");
			String customerId = request.getParameter("customerId");
        	// check productStore active
        	Map<String, Object> checkProductStore = MobileUtils.checkProductStoreActive(delegator, locale, productStoreId);
			if (checkProductStore.get("responseMessage").equals("error")){
				request.setAttribute(Mobile.ERROR_MESSAGE, checkProductStore.get("errorMessage").toString());
				return "error";
			}
			// check customer active
			Map<String, Object> checkCustomer = MobileUtils.checkCustomerActive(delegator, locale, customerId);
			if (checkCustomer.get("responseMessage").equals("error")){
				request.setAttribute(Mobile.ERROR_MESSAGE, checkCustomer.get("errorMessage").toString());
				return "error";
			}
			process.createOrders(request, response, module);
		} catch (Exception e) {
			Debug.logError("error cause : " + e.getMessage(), module);
			return "error";
		}
		getCartInfo(request, response);
		return "sucess";
	}
	public static String addProductsToOrder(HttpServletRequest request, HttpServletResponse response){
		String productsStr = new String(request.getParameter("products"));
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = request.getLocale();
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		if(cart == null){
			/*request.setAttribute(Mobile.ERROR_MESSAGE, "Cart is empty");*/
			request.setAttribute(Mobile.ERROR_MESSAGE, UtilProperties.getMessage(resource, "BSCartIsEmpty", locale));
			return "error";
		}
		if(UtilValidate.isNotEmpty(productsStr)){
			JSONArray products = JSONArray.fromObject(productsStr);
			addProductsToCart(products, cart, dispatcher);
		}

		return "success";
	}
	public static String getCartInfo(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		if(cart == null)
			return "error";
		List<Map<String, Object>> listPromotions = process.getPromotions(cart);
		BigDecimal discountAmount = cart.getOrderOtherAdjustmentTotal();
		BigDecimal totalAmount = cart.getSubTotalNotAdj();
		BigDecimal subTotal = cart.getSubTotal();
		discountAmount = discountAmount.subtract(totalAmount).add(subTotal);
		Map<String, Object> res = FastMap.newInstance();
		res.put("totalAmount", totalAmount);
		res.put("subTotal", subTotal);
		res.put("grandTotal", cart.getGrandTotal());
		res.put("taxAmount", cart.getTotalSalesTax());
		res.put("discountAmount", discountAmount);
		res.put("promotions", listPromotions);
		request.setAttribute("order", res);
		return "success";
	}
	public static void addProductsToCart(JSONArray products, ShoppingCart cart, LocalDispatcher dispatcher){
		StringBuilder productTmp = null;
		for (int i = 0; i < products.size(); i++) {
			JSONObject product = products.getJSONObject(i);
			productTmp = new StringBuilder(product.getString("productId"));
			try {
				cart.addOrIncreaseItem(productTmp.toString(), null, BigDecimal.valueOf(Integer.valueOf(product.getInt("quantity"))), null,
						null, null, null, null, null, null, null /* catalogId */, null, null/* itemType */, null/* itemGroupNumber */, null, dispatcher);
			} catch (Exception exc) {
				Debug.logWarning("Error adding product with id " + productTmp.toString() + " to the cart: " + exc.getMessage(), module);
			}
		}
	}
}
