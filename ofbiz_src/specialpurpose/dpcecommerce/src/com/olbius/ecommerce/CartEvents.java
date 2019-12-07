package com.olbius.ecommerce;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CartEvents {

	public final static String module = CommonServices.class.getName();
	public static final String resource = "CommonUiLabels";

	public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
		String productId = null;
		Map<String, Object> paramMap = UtilHttp.getCombinedMap(request);
		if (paramMap.containsKey("ADD_PRODUCT_ID")) {
			productId = (String) paramMap.remove("ADD_PRODUCT_ID");
		} else if (paramMap.containsKey("add_product_id")) {
			Object object = paramMap.remove("add_product_id");
			try {
				productId = (String) object;
			} catch (ClassCastException e) {
				List<String> productList = UtilGenerics.checkList(object);
				productId = productList.get(0);
			}
		}
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			GenericValue e = ProductWorker.findProduct(delegator, productId);
			if (UtilValidate.isNotEmpty(e)) {
				request.setAttribute("quantityUomId", e.getString("quantityUomId"));
				request.setAttribute("alternativeQuantity", new BigDecimal(1));
				request.setAttribute("alternativeUnitPrice", new BigDecimal(0));
				String res = ShoppingCartEvents.addToCart(request, response);
				return res;
			} else {
				return "error";
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	public static String setCheckOutPages(HttpServletRequest request, HttpServletResponse response) {
		if ("error".equals(CheckOutEvents.cartNotEmpty(request, response)) == true) {
			return "error";
		}
		HttpSession session = request.getSession();
		String curPage = request.getParameter("checkoutpage");
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		GenericValue userLogin = cart.getUserLogin();
		if (userLogin == null)
			userLogin = (GenericValue) session.getAttribute("userLogin");
		if (curPage == null) {
			try {
				cart.createDropShipGroups(dispatcher);
			} catch (CartItemModifyException e) {
				Debug.logError(e, module);
			}
		} else {
			// remove empty ship group
			cart.cleanUpShipGroups();
		}
		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

		if ("confirm".equals(curPage) == true) {
			// Set the shipping address options
			String partyId = userLogin.getString("partyId");
			String shippingContactMechId = null;
			try {
				List<GenericValue> tmpAddr = delegator.findList("PartyContactMechPurpose",EntityCondition.makeCondition(
													UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
															EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"))), null, UtilMisc.toList("-contactMechId"), null, false);
				if(UtilValidate.isNotEmpty(tmpAddr)){
					GenericValue e = tmpAddr.get(0);
					shippingContactMechId = e.getString("contactMechId");
				}else{
					curPage = "error";
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			}
			Map<String, ? extends Object> callResult0 = checkOutHelper.setCheckOutShippingAddress(shippingContactMechId);
			ServiceUtil.getMessages(request, callResult0, null);
			// Set the general shipping options
			String shippingMethod = "NO_SHIPPING";
			String shippingInstructions = request.getParameter("shipping_instructions");
			Map<String, ? extends Object> callResult = ServiceUtil.returnSuccess();

			for (int shipGroupIndex = 0; shipGroupIndex < cart.getShipGroupSize(); shipGroupIndex++) {
				callResult = checkOutHelper.finalizeOrderEntryOptions(shipGroupIndex, shippingMethod,
						shippingInstructions, null, null, "false", null, null,
						null, null);
				ServiceUtil.getMessages(request, callResult, null);
			}

			Map<String, Map<String, Object>> selectedPaymentMethods = CheckOutEvents.getSelectedPaymentMethods(request);
			List<String> singleUsePayments = new ArrayList<String>();
			Map<String, Object> callResult2 = checkOutHelper.setCheckOutPayment(selectedPaymentMethods,
					singleUsePayments, null);
			ServiceUtil.getMessages(request, callResult, null);
			if (!(callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
					&& !(callResult2.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) && !(ServiceUtil.isError(callResult0))) {
				// No errors so push the user onto the next page
				String status = CheckOutEvents.createOrder(request, response);
				if(status.equals("error")){
					return "error";
				}
				String orderId = cart.getOrderId();
				request.setAttribute("checkOutPaymentId", request.getParameter("checkOutPaymentId"));
				request.setAttribute("orderId", orderId);
				request.setAttribute("shipBeforeDate", request.getParameter("shipBeforeDate"));
				ShoppingCartEvents.clearCart(request, response);
				curPage = "success";
			}
		} else {
			curPage = "confirm";
		}

		return curPage;
	}
}
