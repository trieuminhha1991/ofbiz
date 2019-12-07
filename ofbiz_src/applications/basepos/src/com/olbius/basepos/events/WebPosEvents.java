package com.olbius.basepos.events;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;

import com.olbius.basepos.order.PosOrderChangeHelper;

import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;

import com.olbius.basesales.order.CheckOutHelper;

import org.ofbiz.order.OrderManagerEvents;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.securityext.login.LoginEvents;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;

import com.olbius.basepos.invoice.InvoiceEvents;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;
import com.olbius.basehr.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class WebPosEvents {
	public static String module = WebPosEvents.class.getName();
	public static String resource = "BasePosUiLabels";
	public static String resource_error = "BasePosErrorUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

	public static String posLogin(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		// get the posTerminalId
		String posTerminalId = request.getParameter("posTerminalId");

//		try {
//			checkPosTerminal(delegator, UtilHttp.getLocale(request), posTerminalId, request.getParameter("USERNAME"));
//		} catch (Exception e) {
//			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
//			return "error";
//		}

		session.setAttribute("posTerminalId", posTerminalId);

		session.removeAttribute("shoppingCart");
		session.removeAttribute("webPosSession");
		String responseString = LoginEvents.storeLogin(request, response);
		GenericValue userLoginNew = (GenericValue) session.getAttribute("userLogin");
		if (UtilValidate.isNotEmpty(userLoginNew) && UtilValidate.isNotEmpty(posTerminalId)) {
			try {
				GenericValue posTerminal = delegator.findOne("PosTerminal",
						UtilMisc.toMap("posTerminalId", posTerminalId), false);
				String facilityId = posTerminal.getString("facilityId");
				String partyId = userLoginNew.getString("partyId");
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				conditions.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> facilityParties = delegator.findList("FacilityParty",
						EntityCondition.makeCondition(conditions, EntityOperator.AND),
						UtilMisc.toSet("roleTypeId", "facilityId", "partyId", "fromDate", "thruDate"),
						UtilMisc.toList("-fromDate"), null, false);
				if (UtilValidate.isNotEmpty(facilityParties)) {
					if (UtilValidate.isNotEmpty(facilityId)) {
						String productStoreId = null;
						/*List<EntityCondition> productStoreConds = FastList.newInstance();
						productStoreConds.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
						productStoreConds.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "POS_SALES_CHANNEL"));
						productStoreConds.add(EntityCondition.makeCondition("salesMethodChannelEnumId", EntityOperator.EQUALS, "SMCHANNEL_POS"));
						EntityCondition productStoreCond = EntityCondition.makeCondition(productStoreConds, EntityOperator.AND);
						List<GenericValue> productStores = delegator.findList("ProductStoreFacilityAndStore", productStoreCond, null, null, null, false);*/
						List<EntityCondition> productStoreConds = FastList.newInstance();
						productStoreConds.add(EntityCondition.makeCondition("inventoryFacilityId", facilityId));
						productStoreConds.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", "POS_SALES_CHANNEL"));
						productStoreConds.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_POS"));
						List<GenericValue> productStores = delegator.findList("ProductStore", EntityCondition.makeCondition(productStoreConds, EntityOperator.AND), null, null, null, false);
						if (UtilValidate.isNotEmpty(productStores)) {
							GenericValue productStore = productStores.get(0);
							if (UtilValidate.isNotEmpty(productStore)) {
								productStoreId = productStore.getString("productStoreId");
							}
						} else {
							String errMsg = UtilProperties.getMessage(resource_error, "BPOSCannotFindValidStore", UtilHttp.getLocale(request));
							request.setAttribute("_ERROR_MESSAGE_", errMsg);
							responseString = "error";
						}
						session.setAttribute("productStoreId", productStoreId);
						WebPosSession webPosSession = WebPosEvents.getWebPosSession(request, posTerminalId);
						// check state of terminal
						// hoandv
						webPosSession.setUserLogin(userLoginNew);
						WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
						if (UtilValidate.isNotEmpty(webposTransaction)) {
							if (!webposTransaction.isOpen()) {
								request.getSession().setAttribute("_PREVIOUS_REQUEST_", "showOpenTerminal");
								return "showOpenTerminal";
							}
						}
					} else {
						String errMsg = UtilProperties.getMessage(resource_error,
								"BPOSCannotFindFacilityForThisTerminal", UtilHttp.getLocale(request));
						request.setAttribute("_ERROR_MESSAGE_", errMsg);
						responseString = "error";
					}
				} else {
					String errMsg = UtilProperties.getMessage(resource_error, "BPOSUserLoginCannotIncludedInStore",
							UtilMisc.toMap("userLoginId", userLoginNew), UtilHttp.getLocale(request));
					request.setAttribute("_ERROR_MESSAGE_", errMsg);
					responseString = "error";
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}

		if ("error".equals(responseString)) {
			session.removeAttribute("userLogin");
			session.removeAttribute("autoUserLogin");
		}

		return responseString;
	}

	public static String existsWebPosSession(HttpServletRequest request, HttpServletResponse response) {
		String responseString = "success";
		HttpSession session = request.getSession(true);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");

		if (UtilValidate.isEmpty(webPosSession)) {
			responseString = "error";
		}
		return responseString;
	}

	public static WebPosSession getWebPosSession(HttpServletRequest request, String posTerminalId) {
		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String orderMode = (String) request.getParameter("orderMode");
		String orderModeAttribute = (String) session.getAttribute("orderMode");
		String facilityId = null;
		String productStoreId = ProductStoreWorker.getProductStoreId(request);
		if (UtilValidate.isNotEmpty(posTerminalId)) {
			GenericValue posTerminal = null;
			try {
				posTerminal = delegator.findOne("PosTerminal", UtilMisc.toMap("posTerminalId", posTerminalId), false);
			} catch (GenericEntityException e) {
				Debug.logError("PosTerminalId is empty cannot create a webPosSession", module);
			}
			if (UtilValidate.isNotEmpty(posTerminal)) {
				if (UtilValidate.isNotEmpty(posTerminal.getString("facilityId"))) {
					facilityId = posTerminal.getString("facilityId");
				}
			}
		} else {
			facilityId = (String) session.getAttribute("facilityId");
		}

		if (UtilValidate.isNotEmpty(orderModeAttribute)) {
			orderMode = orderModeAttribute;
		}
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		if (UtilValidate.isEmpty(webPosSession)) {
			GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
			String currencyUomId = request.getParameter("currencyUomId");
			if (UtilValidate.isNotEmpty(productStore)) {
				if (UtilValidate.isEmpty(currencyUomId)) {
					currencyUomId = productStore.getString("defaultCurrencyUomId");
				}
			}

			if (UtilValidate.isEmpty(cart)) {
				cart = new ShoppingCart(delegator, productStoreId, request.getLocale(), currencyUomId);
				session.setAttribute("shoppingCart", cart);

			} else if (UtilValidate.isEmpty(cart.getProductStoreId())) {
				cart.setProductStoreId(productStoreId);
				// TODOCHANGE add new attribute: "salesMethodChannelEnumId",
				// "salesExecutiveId", "requestFavorDelivery"
				String salesMethodChannelEnumId = null;
				if (productStore != null && productStore.containsKey("salesMethodChannelEnumId"))
					salesMethodChannelEnumId = productStore.getString("salesMethodChannelEnumId");
				if (salesMethodChannelEnumId != null) {
					cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);
				}
				cart.setAttribute("isFavorDelivery", "N");
			}
			if (UtilValidate.isNotEmpty(facilityId)) {
				cart.setFacilityId(facilityId);
			}
			cart.setChannelType("POS_SALES_CHANNEL");
			if (UtilValidate.isNotEmpty(orderMode) && orderMode.equalsIgnoreCase("PURCHASE_ORDER")) {
				cart.setOrderType("PURCHASE_ORDER");
				session.setAttribute("orderMode", "PURCHASE_ORDER");
			}
			if (UtilValidate.isNotEmpty(userLogin)) {
				session.setAttribute("userLogin", userLogin);
				try {
					cart.setUserLogin(userLogin, dispatcher);
				} catch (CartItemModifyException e) {
					e.printStackTrace();
				}
			}
			if (UtilValidate.isNotEmpty(posTerminalId)) {
				Locale locale = UtilHttp.getLocale(request);
				webPosSession = new WebPosSession(posTerminalId, null, userLogin, locale, productStoreId,
						facilityId, currencyUomId, delegator, dispatcher, cart);
				session.setAttribute("webPosSession", webPosSession);
			} else {
				Debug.logError("PosTerminalId is empty cannot create a webPosSession", module);
			}
		} else {
			if (!orderMode.equals("PURCHASE_ORDER")) {
				facilityId = webPosSession.getFacilityId();
			}
			cart.setFacilityId(facilityId);
		}

		// set party info
		cart.setOrderPartyId("_NA_");

		String salesExecutiveId = userLogin.getString("partyId");
		if (salesExecutiveId != null) {
			cart.setAttribute("salesExecutiveId", salesExecutiveId);
		}
		cart.setProductStoreId(productStoreId);
		webPosSession.setFacilityId(facilityId);
		return webPosSession;
	}

	public static void removeWebPosSession(HttpServletRequest request, String posTerminalId) {
		HttpSession session = request.getSession(true);
		session.removeAttribute("shoppingCart");
		session.setAttribute("orderMode", "SALES_ORDER");
		session.removeAttribute("webPosSession");
		getWebPosSession(request, posTerminalId);
	}

	public static String completeSale(HttpServletRequest request, HttpServletResponse response)
			throws GeneralException {
		HttpSession session = request.getSession(true);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		if (UtilValidate.isNotEmpty(webPosSession)) {
			webPosSession.getCurrentTransaction().processSale(request, response);
			emptyCartAndClearAutoSaveList(request, response);
		}
		return "success";
	}

	public static String createOrderToLog(HttpServletRequest request, HttpServletResponse response)
			throws GeneralException {
		HttpSession session = request.getSession(true);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		GenericValue userLogin = webPosSession.getUserLogin();
		ShoppingCart cart = webPosSession.getCart();
		String productStoreId = request.getParameter("productStoreId");
		GenericValue productStore = null;
		if (UtilValidate.isNotEmpty(productStoreId)) {
			productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
		}

		String salesMethodChannelEnumId = null;
		if (productStore != null && productStore.containsKey("salesMethodChannelEnumId"))
			salesMethodChannelEnumId = productStore.getString("salesMethodChannelEnumId");
		if (salesMethodChannelEnumId != null) {
			cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);
		}

		request.setAttribute("may_split", "false");
		request.setAttribute("is_gift", "false");

		// add shpping method, payment method to cart
		String updateCheckoutOptionsResult = CheckOutEvents.setPartialCheckOutOptions(request, response);
		if ("error".equals(updateCheckoutOptionsResult))
			return "error";
		String checkoutResult = CheckOutEvents.setQuickCheckOutOptions(request, response);
		if ("error".equals(checkoutResult))
			return "error";
		String calcShippingResult = org.ofbiz.order.shoppingcart.shipping.ShippingEvents.getShipEstimate(request,
				response);
		if ("error".equals(calcShippingResult))
			return "error";
		String calcTax = org.ofbiz.order.shoppingcart.CheckOutEvents.calcTax(request, response);
		if ("error".equals(calcTax))
			return "error";

		// set delivery date to cart
		String desiredDeliveryDateStr = request.getParameter("desiredDeliveryDate");
		Timestamp desiredDeliveryDate = null;
		if (UtilValidate.isNotEmpty(desiredDeliveryDateStr)) {
			Long desiredDeliveryDateL = Long.parseLong(desiredDeliveryDateStr);
			desiredDeliveryDate = new Timestamp(desiredDeliveryDateL);
		}
		List<ShoppingCartItem> listItems = cart.items();
		for (int i = 0; i < listItems.size(); i++) {
			listItems.get(i).setDesiredDeliveryDate(desiredDeliveryDate);
		}

		// create oder
		CheckOutHelper ch = new CheckOutHelper(dispatcher, delegator, cart);
		Map<String, ? extends Object> orderRes = UtilGenerics.cast(ch.createOrder(userLogin));
		String orderId = "";
		if (orderRes != null && ServiceUtil.isError(orderRes)) {
			String errorMessage = ServiceUtil.getErrorMessage(orderRes);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} else {
			orderId = (String) orderRes.get("orderId");
		}

		/*
		 * create orderItemShipGrpInvRes
		 */
		Map<String, Object> mapReserves = UtilMisc.toMap("orderId", orderId, "userLogin", userLogin);
		dispatcher.runSync("quickReserveInventoryForOrder", mapReserves);

		/*
		 * update needs inventory issuance
		 */
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		orderHeader.put("needsInventoryIssuance", "N");
		orderHeader.store();

		// approve order
		boolean holdOrder = cart.getHoldOrder();
		Map<String, Object> callResult = ch.processPayment(productStore, userLogin, false, holdOrder);
		if (ServiceUtil.isError(callResult)) {
			// clear out the rejected payment methods (if any) from the cart, so
			// they don't get re-authorized
			cart.clearDeclinedPaymentMethods(delegator);
			// null out the orderId for next pass
			cart.setOrderId(null);
		}
		// generate any messages required
		ServiceUtil.getMessages(request, callResult, null);
		// check for customer message(s)
		List<String> messages = UtilGenerics.checkList(callResult.get("authResultMsgs"));
		if (UtilValidate.isNotEmpty(messages)) {
			request.setAttribute("_EVENT_MESSAGE_LIST_", messages);
		}

		// payment
		String amountParam = request.getParameter("CASH_amount");
		BigDecimal amount = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(amountParam)) {
			amount = new BigDecimal(amountParam);
		}

		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			request.setAttribute("orderId", orderId);
			List<GenericValue> orderRoles = delegator.findByAnd("OrderRole",
					UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"), null, false);
			String partyId = "";
			if (UtilValidate.isNotEmpty(orderRoles)) {
				partyId = orderRoles.get(0).getString("partyId");
			}
			request.setAttribute("partyId", partyId);
			String resultPayment = OrderManagerEvents.receiveOfflinePayment(request, response);
			if (!resultPayment.equals("success")) {
				request.setAttribute("_ERROR_MESSAGE_",
						UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale));
				return "error";
			}
		}

		// create notification to log
		String header = UtilProperties.getMessage(resource, "BPOSNotificationCreateOrderToLog", locale) + " [" + orderId
				+ "]";
		List<String> listParties = SecurityUtil.getPartiesByRoles(
				EntityUtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.specialist", delegator),
				delegator);
		for (String party : listParties) {
			String targetLink = "orderId=" + orderId;
			Map<String, Object> mapContext = new HashMap<String, Object>();
			mapContext.put("partyId", party);
			mapContext.put("action", "viewOrder");
			mapContext.put("targetLink", targetLink);
			mapContext.put("header", header);
			mapContext.put("ntfType", "ONE");
			mapContext.put("userLogin", userLogin);
			try {
				dispatcher.runSync("createNotification", mapContext);
			} catch (GenericServiceException e) {
				ServiceUtil.returnError("createNotification error!");
			}
		}

		// clear cart after create order
		emptyCartAndClearAutoSaveList(request, response);

		return "success";
	}

	public static String emptyCartAndClearAutoSaveList(HttpServletRequest request, HttpServletResponse response)
			throws GeneralException {
		HttpSession session = request.getSession(true);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		ShoppingCartEvents.clearCart(request, response);
		if (UtilValidate.isNotEmpty(webPosSession)) {
			String posTerminalId = webPosSession.getId();
			removeWebPosSession(request, posTerminalId);
		}
		return "success";
	}

	@SuppressWarnings("unchecked")
	public static String quickReturnPos(HttpServletRequest request, HttpServletResponse response) {
		WebPosSession webposSession = getWebPosSession(request, null);
		GenericValue userLogin = webposSession.getUserLogin();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String orderId = request.getParameter("orderId");
		String facilityId = webposSession.getFacilityId();
		String returnHeaderTypeId = "CUSTOMER_RETURN";
		String paramQuantitys = request.getParameter("returnQuantitys");
		String paramAdjusts = request.getParameter("returnAdjustments");
		String returnAdjustTotal = request.getParameter("returnAdjustTotal");
		String[] returnQuantitys = paramQuantitys.split(",");
		String[] returnAdjustments = paramAdjusts.split(",");
		String customer = null;
		OrderReadHelper orderHelper = null;
		GenericValue orderHeader = null;
		String returnId = null;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			orderHelper = new OrderReadHelper(orderHeader);
			GenericValue customerEntity = orderHelper.getPlacingParty();
			if (UtilValidate.isNotEmpty(customerEntity)) {
				customer = customerEntity.getString("partyId");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String, String> typeMap = FastMap.newInstance();
		List<GenericValue> returnItemTypeMap = null;
		try {
			returnItemTypeMap = delegator.findByAnd("ReturnItemTypeMap",
					UtilMisc.toMap("returnHeaderTypeId", returnHeaderTypeId), null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(returnItemTypeMap)) {
			for (GenericValue value : returnItemTypeMap) {
				String returnItemTypeId = value.getString("returnItemTypeId");
				String returnItemMapKey = value.getString("returnItemMapKey");
				typeMap.put(returnItemMapKey, returnItemTypeId);
			}
		}

		String company = UtilProperties.getPropertyValue("general", "ORGANIZATION_PARTY");

		Map<String, Object> returnRes = null;
		Map<GenericValue, Map<String, Object>> returnableItems = null;
		// return items
		try {
			returnRes = dispatcher.runSync("getReturnableItems", UtilMisc.toMap("orderId", orderId));
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(returnRes)) {
			returnableItems = (Map<GenericValue, Map<String, Object>>) returnRes.get("returnableItems");
		}
		int index = 0;
		if (UtilValidate.isNotEmpty(returnableItems)) {
			Set<GenericValue> listReturns = returnableItems.keySet();
			for (GenericValue orderItem : listReturns) {
				Map<String, Object> svcCtx = FastMap.newInstance();
				// common
				svcCtx.put("orderId", orderId);
				svcCtx.put("fromPartyId", customer);
				svcCtx.put("toPartyId", company);
				svcCtx.put("needsInventoryReceive", "Y");
				svcCtx.put("destinationFacilityId", facilityId);
				svcCtx.put("returnHeaderTypeId", returnHeaderTypeId);
				svcCtx.put("currencyUomId", orderHeader.getString("currencyUom"));
				svcCtx.put("userLogin", userLogin);
				if (returnId != null) {
					svcCtx.put("returnId", returnId);
				}
				// end common
				String returnItemType = typeMap.get(returnableItems.get(orderItem).get("itemTypeKey"));
				svcCtx.put("returnItemTypeId", returnItemType);
				svcCtx.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
				svcCtx.put("description", orderItem.get("description"));
				svcCtx.put("productId", orderItem.get("productId"));
				BigDecimal returnQuantity = new BigDecimal(returnQuantitys[index]);
				BigDecimal returnAdjustment = new BigDecimal(returnAdjustments[index]);
				BigDecimal orderItemPrice = (BigDecimal) returnableItems.get(orderItem).get("returnablePrice");
				MathContext mc = new MathContext(2);
				BigDecimal returnPrice = orderItemPrice.subtract(returnAdjustment, mc);
				svcCtx.put("returnQuantity", returnQuantity);
				svcCtx.put("returnPrice", returnPrice);
				svcCtx.put("returnReasonId", "RTN_NOT_WANT");
				svcCtx.put("returnTypeId", "RTN_REFUND");
				svcCtx.put("expectedItemStatus", "INV_RETURNED");
				index = index + 1;
				try {
					Map<String, Object> returnItemAdjustment = dispatcher.runSync("createReturnAndItemOrAdjustment",
							svcCtx);
					if (returnId == null) {
						returnId = (String) returnItemAdjustment.get("returnId");
					}
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
			}
		}

		// return adjustment
		List<GenericValue> orderHeaderAdjustments = FastList.newInstance();
		if (UtilValidate.isNotEmpty(orderHelper)) {
			orderHeaderAdjustments = orderHelper.getAvailableOrderHeaderAdjustments();
			if (UtilValidate.isNotEmpty(orderHeaderAdjustments)) {
				for (GenericValue adj : orderHeaderAdjustments) {
					String returnAdjustmentType = typeMap.get(adj.get("orderAdjustmentTypeId"));
					GenericValue adjustmentType = null;
					try {
						adjustmentType = adj.getRelatedOne("OrderAdjustmentType", false);
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					String description = adjustmentType.getString("description");
					Map<String, Object> svcCtx = FastMap.newInstance();
					// common
					svcCtx.put("orderId", orderId);
					svcCtx.put("fromPartyId", customer);
					svcCtx.put("toPartyId", company);
					svcCtx.put("needsInventoryReceive", "Y");
					svcCtx.put("destinationFacilityId", facilityId);
					svcCtx.put("returnHeaderTypeId", returnHeaderTypeId);
					svcCtx.put("currencyUomId", orderHeader.getString("currencyUom"));
					svcCtx.put("userLogin", userLogin);
					if (returnId != null) {
						svcCtx.put("returnId", returnId);
					}
					// end common
					svcCtx.put("returnAdjustmentTypeId", returnAdjustmentType);
					svcCtx.put("orderAdjustmentId", adj.getString("orderAdjustmentId"));
					svcCtx.put("returnItemSeqId", "_NA_");
					svcCtx.put("description", description);
					BigDecimal adjustTotal = new BigDecimal(returnAdjustTotal);

					svcCtx.put("amount", adjustTotal.negate());
					svcCtx.put("returnTypeId", "RTN_REFUND");
					try {
						Map<String, Object> returnItemAdjustment = dispatcher.runSync("createReturnAndItemOrAdjustment",
								svcCtx);
						if (returnId == null) {
							returnId = (String) returnItemAdjustment.get("returnId");
						}
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// update return
		if (UtilValidate.isNotEmpty(returnId)) {
			Map<String, Object> svcCtxUpdate = FastMap.newInstance();
			svcCtxUpdate.put("returnId", returnId);
			svcCtxUpdate.put("statusId", "RETURN_ACCEPTED");
			svcCtxUpdate.put("needsInventoryReceive", "Y");
			svcCtxUpdate.put("userLogin", userLogin);
			try {
				dispatcher.runSync("updateReturnHeader", svcCtxUpdate);
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		return "success";
	}

	public static String processReturnFromCart(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
		Locale locale = UtilHttp.getLocale(request);

		if (UtilValidate.isNotEmpty(webPosSession)) {
			ShoppingCart cart = webPosSession.getCart();
			String orderId = webposTransaction.getOrderId();
			String facilityId = cart.getFacilityId();
			GenericValue facility = null;
			try {
				facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			// create a shipment
			String shipmentId = creatShipmentPurchase(request, cart);
			if (shipmentId.equals("error")) {
				shipmentId = null;
			}
			GenericValue orderHeader = null;
			try {
				orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
			}
			Timestamp orderDate = orderHeader.getTimestamp("orderDate");
			EntityCondition mainCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
			List<GenericValue> listOrderItems = FastList.newInstance();
			try {
				listOrderItems = delegator.findList("OrderItem", mainCond, null, null, null, false);
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
			}
			for (GenericValue orderItem : listOrderItems) {
				String productId = orderItem.getString("productId");
				Map<String, Object> receiveInventoryProductMap = FastMap.newInstance();
				receiveInventoryProductMap.put("facilityId", facilityId);
				receiveInventoryProductMap.put("userLogin", userLogin);
				receiveInventoryProductMap.put("productId", productId);
				receiveInventoryProductMap.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				receiveInventoryProductMap.put("quantityAccepted", orderItem.getBigDecimal("quantity"));
				Map<String, Object> getProductAverageCostMap = FastMap.newInstance();
				getProductAverageCostMap.put("ownerPartyId", facility.getString("ownerPartyId"));
				getProductAverageCostMap.put("facilityId", facilityId);
				getProductAverageCostMap.put("productId", productId);
				getProductAverageCostMap.put("userLogin", userLogin);
				Map<String, Object> getProductAverageCost = FastMap.newInstance();
				try {
					getProductAverageCost = dispatcher.runSync("getProductAverageCostSimple", getProductAverageCostMap);
				} catch (GenericServiceException e1) {
					e1.printStackTrace();
				}
				receiveInventoryProductMap.put("unitCost", getProductAverageCost.get("unitCost"));
				receiveInventoryProductMap.put("quantityRejected", BigDecimal.ZERO);
				receiveInventoryProductMap.put("quantityQualityAssurance", BigDecimal.ZERO);
				receiveInventoryProductMap.put("ownerPartyId", facility.getString("ownerPartyId"));
				receiveInventoryProductMap.put("userLogin", userLogin);
				receiveInventoryProductMap.put("datetimeReceived", orderDate);
				receiveInventoryProductMap.put("shipmentId", shipmentId);

				// map for shipment item
				Map<String, Object> shipmentItemMap = FastMap.newInstance();
				shipmentItemMap.put("shipmentId", shipmentId);
				shipmentItemMap.put("shipmentItemSeqId", orderItem.getString("orderItemSeqId"));
				shipmentItemMap.put("productId", productId);
				shipmentItemMap.put("quantity", orderItem.getBigDecimal("quantity"));
				shipmentItemMap.put("quantityUomId", orderItem.getString("quantityUomId"));
				shipmentItemMap.put("userLogin", userLogin);
				try {
					dispatcher.runSync("receiveInventoryProduct", receiveInventoryProductMap);
					dispatcher.runSync("createShipmentItem", shipmentItemMap);
				} catch (GenericServiceException e) {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotReceiveProduct",
							UtilHttp.getLocale(request));
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}

			String createInvoiceId = createInvoiceReceiveInventory(request, response);
			if (createInvoiceId.equals("error")) {
				return createInvoiceId;
			}

			// create invoice item
			createInvoiceItemReturnFromCart(request, response, createInvoiceId);

			// create payment prference
			Map<String, Object> createOrderPaymentPreferenceMap = FastMap.newInstance();
			createOrderPaymentPreferenceMap.put("userLogin", userLogin);
			createOrderPaymentPreferenceMap.put("orderId", orderId);
			createOrderPaymentPreferenceMap.put("paymentMethodTypeId", "CASH");
			createOrderPaymentPreferenceMap.put("statusId", "PAYMENT_RECEIVED");
			createOrderPaymentPreferenceMap.put("maxAmount", orderHeader.getBigDecimal("grandTotal"));
			Map<String, Object> createOrderPaymentPreference = FastMap.newInstance();
			try {
				createOrderPaymentPreference = dispatcher.runSync("createOrderPaymentPreference",
						createOrderPaymentPreferenceMap);
			} catch (GenericServiceException e2) {
				e2.printStackTrace();
			}
			if (ServiceUtil.isError(createOrderPaymentPreference)) {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotLinkPaymentWithOrder",
						locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}

			// create a payment
			String paymentId = createPaymentReturn(request, cart,
					(String) createOrderPaymentPreference.get("orderPaymentPreferenceId"));
			if (paymentId.equals("error")) {
				return paymentId;
			}
			GenericValue payment = null;
			if (UtilValidate.isNotEmpty(paymentId)) {
				try {
					payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}

			// link payment with invoice
			if (UtilValidate.isNotEmpty(payment)) {
				Map<String, Object> createPaymentApp = FastMap.newInstance();
				createPaymentApp.put("userLogin", userLogin);
				createPaymentApp.put("paymentId", paymentId);
				createPaymentApp.put("invoiceId", createInvoiceId);
				Map<String, Object> createPaymentAppResult = FastMap.newInstance();
				try {
					createPaymentAppResult = dispatcher.runSync("createPaymentApplication", createPaymentApp);
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}

				if (ServiceUtil.isError(createPaymentAppResult)) {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotLinkPaymentWithInvoice",
							locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}

				// approve order when there is orderId
				if (UtilValidate.isNotEmpty(orderId)) {
					if (UtilValidate.isNotEmpty(orderHeader)) {
						PosOrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
						try {
							PosOrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, "ORDER_COMPLETED",
									"ITEM_APPROVED", "ITEM_COMPLETED", null);
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
					}
				}

				// change status of invoice
				String returnChangeStatusInvoice = InvoiceEvents.changeInvoiceStatus(request, createInvoiceId);
				if (returnChangeStatusInvoice.equals("error")) {
					return "error";
				}
				try {
					cart.setOrderId(null);
					cart.setOrderId(null);
					cart.setSupplierAgentPartyId(null);
					cart.setOrderPartyId(null);
					emptyCartAndClearAutoSaveList(request, response);
					String posTerminalId = webPosSession.getId();
					session.setAttribute("orderMode", "SALES_ORDER");
					removeWebPosSession(request, posTerminalId);
				} catch (GeneralException e) {
					e.printStackTrace();
				}

				return "success";
			} else {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreatePayment", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		}

		return "success";
	}

	public static String createInvoiceItemReturnFromCart(HttpServletRequest request, HttpServletResponse response,
			String invoiceId) {
		HttpSession session = request.getSession(true);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
		String orderId = webposTransaction.getOrderId();
		String facilityId = webPosSession.getFacilityId();
		String orgnazationId = UtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String invoiceType = "PURCHASE_INVOICE";
		List<GenericValue> listOrderItems = FastList.newInstance();
		if (UtilValidate.isNotEmpty(orderId)) {
			try {
				EntityCondition orderItemCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,
						orderId);
				listOrderItems = delegator.findList("OrderItem", orderItemCond, null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			GenericValue invoice = null;
			try {
				invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			} catch (GenericEntityException e1) {
				e1.printStackTrace();
			}
			if (UtilValidate.isNotEmpty(invoice) && UtilValidate.isNotEmpty(listOrderItems)) {
				for (GenericValue orderItem : listOrderItems) {
					String productId = orderItem.getString("productId");
					GenericValue product = null;
					String orderItemSeq = orderItem.getString("orderItemSeqId");
					try {
						product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					} catch (GenericEntityException e1) {
						e1.printStackTrace();
					}
					// get average cost of productId
					Map<String, Object> averageCostMap = FastMap.newInstance();
					averageCostMap.put("productId", productId);
					averageCostMap.put("facilityId", facilityId);
					averageCostMap.put("ownerPartyId", orgnazationId);
					averageCostMap.put("userLogin", userLogin);
					BigDecimal averageCost = BigDecimal.ZERO;
					Map<String, Object> averageCostService = FastMap.newInstance();
					try {
						averageCostService = dispatcher.runSync("getProductAverageCostSimple", averageCostMap);
						averageCost = (BigDecimal) averageCostService.get("unitCost");
					} catch (GenericServiceException e1) {
						e1.printStackTrace();
					}

					BigDecimal quantity = orderItem.getBigDecimal("quantity");
					BigDecimal quantityStandard = quantity;
					BigDecimal unitPrice = orderItem.getBigDecimal("unitPrice");
					Map<String, Object> createInvoiceItemMap = FastMap.newInstance();
					BigDecimal alternativeQuantity = orderItem.getBigDecimal("alternativeQuantity");
					BigDecimal alternativeUnitPrice = orderItem.getBigDecimal("alternativeUnitPrice");
					if (UtilValidate.isNotEmpty(alternativeQuantity)) {
						quantity = alternativeQuantity;
						unitPrice = alternativeUnitPrice;
						String uomIdFrom = orderItem.getString("quantityUomId");
						String uomIdTo = product.getString("quantityUomId");
						quantityStandard = UomWorker.customConvertUom(productId, uomIdFrom, uomIdTo, quantity,
								"convert", delegator);
					}
					BigDecimal grandTotalItemStandard = quantityStandard.multiply(averageCost);
					BigDecimal grandTotalItem = quantity.multiply(unitPrice);
					createInvoiceItemMap.put("quantity", quantity);
					createInvoiceItemMap.put("amount", averageCost);
					createInvoiceItemMap.put("userLogin", userLogin);
					createInvoiceItemMap.put("invoiceId", invoiceId);
					createInvoiceItemMap.put("invoiceItemTypeId",
							getInvoiceItemType(delegator, (orderItem.getString("orderItemTypeId")),
									(product == null ? null : product.getString("productTypeId")), invoiceType,
									"INV_FPROD_ITEM"));
					createInvoiceItemMap.put("taxableFlag", "N");
					String invoiceItemSeqIdReturn = null;

					// add into order_item_billing
					try {
						Map<String, Object> createInvoiceItem = dispatcher.runSync("createInvoiceItem",
								createInvoiceItemMap);
						invoiceItemSeqIdReturn = (String) createInvoiceItem.get("invoiceItemSeqId");
					} catch (GenericServiceException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateInvoiceItem",
								UtilHttp.getLocale(request));
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					GenericValue orderItemBilling = delegator.makeValue("OrderItemBilling");

					orderItemBilling.put("orderId", orderId);
					orderItemBilling.put("orderItemSeqId", orderItemSeq);
					orderItemBilling.put("invoiceId", invoiceId);
					orderItemBilling.put("invoiceItemSeqId", invoiceItemSeqIdReturn);
					orderItemBilling.put("quantity", quantity);
					orderItemBilling.put("amount", averageCost);
					try {
						orderItemBilling.create();
					} catch (GenericEntityException e) {
						String errorMessage = UtilProperties.getMessage(resource_error,
								"BPOSCanNotLinkOrderWithInvoice", UtilHttp.getLocale(request));
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					// check amount return different
					float grandItemCost = grandTotalItemStandard.floatValue();
					float grandTotalSale = grandTotalItem.floatValue();
					float different = grandTotalSale - grandItemCost;
					if (UtilValidate.isNotEmpty(different) && different != 0) {
						BigDecimal amountDifferent = BigDecimal.valueOf(different);
						Map<String, Object> createInvoiceItemDifferentMap = FastMap.newInstance();
						createInvoiceItemDifferentMap.put("userLogin", userLogin);
						createInvoiceItemDifferentMap.put("invoiceId", invoiceId);
						createInvoiceItemDifferentMap.put("productId", product.getString("productId"));
						createInvoiceItemDifferentMap.put("invoiceItemTypeId", "PINV_SRT_DFF_ITEM");
						createInvoiceItemDifferentMap.put("amount", amountDifferent);
						try {
							dispatcher.runSync("createInvoiceItem", createInvoiceItemDifferentMap);
						} catch (GenericServiceException e) {
							String errorMessage = UtilProperties.getMessage(resource_error,
									"BPOSCanNotCreateInvoiceItem", UtilHttp.getLocale(request));
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
					}
				}
			}
		}

		return "success";
	}

	public static String createInvoiceReceiveInventory(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		ShoppingCart cart = webPosSession.getCart();
		Map<String, Object> createInvoiceMap = FastMap.newInstance();
		createInvoiceMap.put("userLogin", userLogin);
		String currencyUomId = cart.getCurrency();
		String partyIdFrom = cart.getOrderPartyId();
		String partyId = cart.getBillFromVendorPartyId();
		createInvoiceMap.put("statusId", "INVOICE_IN_PROCESS");
		createInvoiceMap.put("invoiceTypeId", "PURCHASE_INVOICE");
		createInvoiceMap.put("currencyUomId", currencyUomId);
		String company = UtilProperties.getPropertyValue("general", "ORGANIZATION_PARTY");
		if (cart.getOrderType().equals("SALES_ORDER")) {
			if (cart.isCartContainedReturn() || cart.isWholeCartIsReturn()) {
				partyIdFrom = cart.getPartyId();
				partyId = company;
			}
		} else {
			if (cart.isCartContainedReturn() || cart.isWholeCartIsReturn()) {
				partyIdFrom = cart.getPartyId();
				partyId = company;
			}
		}

		createInvoiceMap.put("partyId", partyId);
		createInvoiceMap.put("partyIdFrom", partyIdFrom);
		Map<String, Object> createInvoiceReturn = FastMap.newInstance();
		try {
			createInvoiceReturn = dispatcher.runSync("createInvoice", createInvoiceMap);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		if (ServiceUtil.isError(createInvoiceReturn)) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateInvoice", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} else {
			String invoiceId = (String) createInvoiceReturn.get("invoiceId");
			return invoiceId;
		}
	}

	public static String createPaymentPos(HttpServletRequest request, ShoppingCart cart) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = cart.getUserLogin();
		String partyIdTo = cart.getOrderPartyId();
		String partyIdFrom = cart.getBillFromVendorPartyId();
		String amountCash = request.getParameter("amountCash");
		BigDecimal amount = new BigDecimal(amountCash);
		String currencyUomId = cart.getCurrency();
		if (UtilValidate.isEmpty(amountCash)) {
			amount = BigDecimal.ZERO;
		}
		Map<String, Object> createPaymentMap = FastMap.newInstance();
		createPaymentMap.put("partyIdTo", partyIdTo);
		createPaymentMap.put("partyIdFrom", partyIdFrom);
		createPaymentMap.put("amount", amount);
		String comments = UtilProperties.getMessage(resource, "BPOSPaymentDescription", locale);
		if (cart.getOrderType().equals("SALES_ORDER")) {
			createPaymentMap.put("paymentTypeId", "CUSTOMER_REFUND");
			if (cart.wholeCartIsReturned()) {
				comments = UtilProperties.getMessage(resource, "BPOSCustomerRefund", locale);
			}
		} else {
			createPaymentMap.put("paymentTypeId", "VENDOR_PAYMENT");
		}
		createPaymentMap.put("comments", comments);
		createPaymentMap.put("userLogin", userLogin);
		createPaymentMap.put("currencyUomId", currencyUomId);
		createPaymentMap.put("paymentMethodTypeId", "CASH");
		createPaymentMap.put("statusId", "PMNT_SENT");
		Map<String, Object> createPayment = FastMap.newInstance();
		try {
			createPayment = dispatcher.runSync("createPayment", createPaymentMap);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		if (ServiceUtil.isError(createPayment)) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreatePayment", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} else {
			String paymentId = (String) createPayment.get("paymentId");
			return paymentId;
		}
	}

	public static String createPaymentReturn(HttpServletRequest request, ShoppingCart cart,
			String orderPaymentPreferenceId) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = cart.getUserLogin();
		String partyIdTo = null, partyIdFrom = null;
		HttpSession session = request.getSession();
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
		if (UtilValidate.isNotEmpty(webposTransaction)) {
			String company = UtilProperties.getPropertyValue("general", "ORGANIZATION_PARTY");
			if (cart.getOrderType().equals("SALES_ORDER")) {
				if (cart.isCartContainedReturn() || cart.isWholeCartIsReturn()) {
					partyIdFrom = company;
					partyIdTo = cart.getPartyId();
				}
			} else {
				if (cart.isCartContainedReturn() || cart.isWholeCartIsReturn()) {
					partyIdFrom = company;
					partyIdTo = cart.getPartyId();
				} else {
					partyIdFrom = cart.getPartyId();
					partyIdTo = company;
				}
			}

			BigDecimal amount = BigDecimal.ZERO;
			String currencyUomId = cart.getCurrency();
			if (cart.isCartContainedReturn()) {
				amount = cart.getGranTotalReturn();
			}
			if (cart.isWholeCartIsReturn()) {
				amount = cart.getGranTotalReturn();
			}
			Map<String, Object> createPaymentMap = FastMap.newInstance();
			createPaymentMap.put("partyIdTo", partyIdTo);
			createPaymentMap.put("partyIdFrom", partyIdFrom);
			createPaymentMap.put("amount", amount);
			String comments = "";
			if (cart.getOrderType().equals("SALES_ORDER")) {
				createPaymentMap.put("paymentTypeId", "VENDOR_PAYMENT");
				comments = UtilProperties.getMessage(resource, "BPOSVenderPayment", locale);
			} else {
				createPaymentMap.put("paymentTypeId", "CUSTNORD_REFUND");
				comments = UtilProperties.getMessage(resource, "BPOSCustomerRefund", locale);
			}
			createPaymentMap.put("comments", comments);
			createPaymentMap.put("userLogin", userLogin);
			createPaymentMap.put("currencyUomId", currencyUomId);
			createPaymentMap.put("paymentMethodTypeId", "CASH");
			createPaymentMap.put("statusId", "PMNT_SENT");
			createPaymentMap.put("paymentPreferenceId", orderPaymentPreferenceId);
			Map<String, Object> createPayment = FastMap.newInstance();
			try {
				createPayment = dispatcher.runSync("createPayment", createPaymentMap);
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
			if (ServiceUtil.isError(createPayment)) {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreatePayment", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			} else {
				String paymentId = (String) createPayment.get("paymentId");
				return paymentId;
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreatePayment", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
	}

	public static String creatShipmentPurchase(HttpServletRequest request, ShoppingCart cart) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		String facilityid = cart.getFacilityId();
		GenericValue userLogin = cart.getUserLogin();
		Map<String, Object> createShipmentMap = FastMap.newInstance();
		createShipmentMap.put("statusId", "PURCH_SHIP_CREATED");
		createShipmentMap.put("shipmentTypeId", "PURCHASE_SHIPMENT");
		createShipmentMap.put("destinationFacilityId", facilityid);
		createShipmentMap.put("userLogin", userLogin);
		Map<String, Object> returnShipmentPurchase = FastMap.newInstance();
		try {
			returnShipmentPurchase = dispatcher.runSync("createShipment", createShipmentMap);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		if (ServiceUtil.isError(returnShipmentPurchase)) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateShipment", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} else {
			String shipmentId = (String) returnShipmentPurchase.get("shipmentId");
			return shipmentId;
		}
	}

	public static String getCartInfo(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		List<Map<String, Object>> listCartItems = FastList.newInstance();
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");

		if (UtilValidate.isNotEmpty(webposSession)) {
			ShoppingCart cart = webposSession.getCart();
			int index = 0;
			if (UtilValidate.isNotEmpty(cart)) {
				List<ShoppingCartItem> cartItems = cart.items();
				String productStoreId = cart.getProductStoreId();
				GenericValue productStore = null;
				try {
					productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId),
							false);
				} catch (GenericEntityException e2) {
					e2.printStackTrace();
				}

				if (UtilValidate.isEmpty(productStore)) {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSWebPosSessionNotFound",
							locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}

				String showPricesWithVatTax = productStore.getString("showPricesWithVatTax");

				if (UtilValidate.isNotEmpty(cartItems)) {
					for (ShoppingCartItem cartItem : cartItems) {
						Map<String, Object> mapCartItem = FastMap.newInstance();
						index += 1;
						mapCartItem.put("count", index);
						int cartLineIndex = cart.getItemIndex(cartItem);
						mapCartItem.put("cartLineIndex", cartLineIndex);
						GenericValue product = cartItem.getProduct();
						String productId = product.getString("productId");
						String productCode = product.getString("productCode");
						// get attribute of product
						Map<String, String> sizeAttributeMap = FastMap.newInstance();
						sizeAttributeMap.put("productId", productId);
						sizeAttributeMap.put("attrName", "SIZE");
						GenericValue sizeAttribute = null;
						try {
							sizeAttribute = delegator.findOne("ProductAttribute", sizeAttributeMap, false);
						} catch (GenericEntityException e1) {
							e1.printStackTrace();
						}
						if (UtilValidate.isNotEmpty(sizeAttribute)) {
							mapCartItem.put("sizeAttributeName", "SIZE");
							mapCartItem.put("sizeAttributeValue", sizeAttribute.getString("attrValue"));
						} else {
							mapCartItem.put("sizeAttributeName", "SIZE");
							mapCartItem.put("sizeAttributeValue", "");
						}
						// get colour attribute of product
						Map<String, String> colourAttributeMap = FastMap.newInstance();
						colourAttributeMap.put("productId", productId);
						colourAttributeMap.put("attrName", "COLOUR");
						GenericValue colourAttribute = null;
						try {
							colourAttribute = delegator.findOne("ProductAttribute", colourAttributeMap, false);
						} catch (GenericEntityException e1) {
							e1.printStackTrace();
						}
						if (UtilValidate.isNotEmpty(colourAttribute)) {
							mapCartItem.put("colourAttributeName", "COLOUR");
							mapCartItem.put("colourAttributeValue", colourAttribute.getString("attrValue"));
						} else {
							mapCartItem.put("colourAttributeName", "COLOUR");
							mapCartItem.put("colourAttributeValue", "");
						}
						mapCartItem.put("productId", productId);
						mapCartItem.put("productCode", productCode);
						String internalName = product.getString("internalName");
						String largeImageUrl = product.getString("largeImageUrl");
						//String productName = product.getString("productName");
						String productName = cartItem.getName();
						mapCartItem.put("productName", productName);
						mapCartItem.put("internalName", internalName);
						mapCartItem.put("largeImageUrl", largeImageUrl);
						String quantityUomId = (String) cartItem.getAttribute("quantityUomId");
						mapCartItem.put("uomId", quantityUomId);

						BigDecimal quantityProduct = BigDecimal.ZERO;
						BigDecimal price = BigDecimal.ZERO;
						BigDecimal amount = BigDecimal.ZERO;
						if (showPricesWithVatTax.equals("Y")) {
							if (UtilValidate.isNotEmpty(quantityUomId)) {
								quantityProduct = cartItem.getAlternativeQuantity();
							} else {
								quantityProduct = (BigDecimal) cartItem.getQuantity();
							}
							price = (BigDecimal) cartItem.getAlterPriceWithTax(cart, dispatcher);
							amount = (BigDecimal) cartItem.getItemSubTotalAndTax(cart);
						} else {
							if (UtilValidate.isNotEmpty(quantityUomId)) {
								quantityProduct = cartItem.getAlternativeQuantity();
								price = cartItem.getAlternativeUnitPrice();
							} else {
								quantityProduct = (BigDecimal) cartItem.getQuantity();
								price = (BigDecimal) cartItem.getBasePrice();
							}
							amount = (BigDecimal) cartItem.getItemSubTotal();
						}

						mapCartItem.put("quantityProduct", quantityProduct);
						mapCartItem.put("price", price);
						mapCartItem.put("amount", amount);

						GenericValue barCodeEntity = null;
						try {
							barCodeEntity = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId",
									productId, "goodIdentificationTypeId", "SKU", "uomId", quantityUomId), null, false));
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						if (UtilValidate.isNotEmpty(barCodeEntity)) {
							mapCartItem.put("barcode", barCodeEntity.getString("idValue"));
						}
						String currency = cart.getCurrency();
						List<Map<String, String>> listUom = FastList.newInstance();
						GenericValue uomOrigin = null;
						try {
							uomOrigin = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						if (UtilValidate.isNotEmpty(uomOrigin)) {
							Map<String, String> uom = FastMap.newInstance();
							uom.put("quantityUomId", uomOrigin.getString("uomId"));
							uom.put("description", uomOrigin.getString("description"));
							listUom.add(uom);
						}
						mapCartItem.put("currency", currency);
						List<GenericValue> listQuantityUomId = cartItem.getListQuantityUom();
						if (UtilValidate.isNotEmpty(listQuantityUomId)) {
							for (GenericValue uomItem : listQuantityUomId) {
								Map<String, String> uom = FastMap.newInstance();
								uom.put("quantityUomId", uomItem.getString("uomId"));
								uom.put("description", uomItem.getString("description"));
								listUom.add(uom);
							}
						}
						mapCartItem.put("uomList", listUom);

						try {
//							mapCartItem.put("quanATP", (BigDecimal) cartItem.getATPInfo(quantityUomId,
//									product.getString("quantityUomId"), dispatcher, cart));
							GenericValue productFacility = delegator.findOne("ProductFacility",
									UtilMisc.toMap("productId", productId, "facilityId", cart.getFacilityId()), false);
							if (UtilValidate.isNotEmpty(productFacility)) {
								mapCartItem.put("quanATP", productFacility.get("lastInventoryCount"));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						BigDecimal discount = cartItem.getAdjustment();
						BigDecimal subTotalAndTax = quantityProduct.multiply(price);
						mapCartItem.put("subTotal", subTotalAndTax);
						mapCartItem.put("discount", discount);
						float percent = 0;
						if (UtilValidate.isNotEmpty(subTotalAndTax) && subTotalAndTax.compareTo(BigDecimal.ZERO) != 0) {
							percent = discount.floatValue() / subTotalAndTax.floatValue();
						}

						percent = percent * 100;
						if (percent == 0) {
							mapCartItem.put("discountPercent", '0');
						} else {
							mapCartItem.put("discountPercent", String.format("%.2f", percent));
						}

						mapCartItem.put("isPromo", cartItem.getIsPromo() ? "Y" : "N");

						listCartItems.add(mapCartItem);
					}
				}
			}
		}
		request.setAttribute("listCartItems", listCartItems);
		return "success";
	}

	public static String getCartItemSelectedInfo(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		String cartLineIndex = request.getParameter("cartLineIndex");
		int index = Integer.parseInt(cartLineIndex);
		Map<String, Object> mapItem = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(webposSession)) {
			ShoppingCart cart = webposSession.getCart();
			if (UtilValidate.isNotEmpty(cart)) {
				ShoppingCartItem cartItem = cart.findCartItem(index);
				if (UtilValidate.isNotEmpty(cartItem)) {
					String productId = cartItem.getProductId();
					mapItem.put("cartLineIndex", index);
					mapItem.put("productId", productId);
					mapItem.put("uomList", cartItem.getListQuantityUom());
					GenericValue product = cartItem.getProduct();
					mapItem.put("productName", product.getString("internalName"));
					mapItem.put("currencyUom", cart.getCurrency());
					String quantityUomId = (String) cartItem.getAttribute("quantityUomId");
					mapItem.put("quantityUomId", cartItem.getAttribute("quan"));
					if (UtilValidate.isNotEmpty(quantityUomId)) {
						mapItem.put("quantity", cartItem.getAlternativeQuantity());
						mapItem.put("unitPrice", cartItem.getAlternativeUnitPrice());
						mapItem.put("quantityATP", cartItem.getAttribute("alternatvieAtpQuantity"));
					} else {
						mapItem.put("quantity", cartItem.getQuantity());
						mapItem.put("unitPrice", cartItem.getBasePrice());
						mapItem.put("quantityATP", cartItem.getAttribute("atpQuantity"));
					}
					BigDecimal subTotalNotAdj = cartItem.getItemSubTotalNotAdj();
					BigDecimal discount = cartItem.getAdjustment();
					mapItem.put("subTotal", subTotalNotAdj);
					mapItem.put("discount", discount);
					float percent = 0;
					if (UtilValidate.isNotEmpty(subTotalNotAdj) && subTotalNotAdj.compareTo(BigDecimal.ZERO) != 0) {
						percent = discount.floatValue() / subTotalNotAdj.floatValue();
					}

					percent = percent * 100;
					if (percent == 0) {
						mapItem.put("discountPercent", '0');
					} else {
						mapItem.put("discountPercent", String.format("%.2f", percent));
					}
				} else {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSYouCanNotGetInfoCartItem",
							locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSWebPosSessionNotFound", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		request.setAttribute("cartItem", mapItem);
		return "success";
	}

	public static String getCartHeaderInfo(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		Map<String, Object> cartHeader = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(webposSession)) {
			String transactionId = webposSession.getCurrentTransaction().getTransactionId();
			if (UtilValidate.isEmpty(transactionId)) {
				transactionId = "_NA_";
			}
			cartHeader.put("transactionId", transactionId);
			ShoppingCart cart = webposSession.getCart();
			if (UtilValidate.isNotEmpty(cart)) {
				String productStoreId = cart.getProductStoreId();
				GenericValue productStore = null;
				try {
					productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId),
							false);
				} catch (GenericEntityException e2) {
					e2.printStackTrace();
				}

				if (UtilValidate.isEmpty(productStore)) {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSWebPosSessionNotFound",
							locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}

				String showPricesWithVatTax = productStore.getString("showPricesWithVatTax");

				BigDecimal tax = cart.getTotalSalesTax();
				cartHeader.put("tax", tax);
				BigDecimal totalDue = webposSession.getCurrentTransaction().getTotalDue();
				BigDecimal grandTotalCart = BigDecimal.ZERO;
				if (showPricesWithVatTax.equals("Y")) {
					grandTotalCart = cart.getSubTotalAndTax();
				} else {
					grandTotalCart = cart.getGrandTotalNotAdjusment();
				}

				BigDecimal discount = BigDecimal.ZERO;
				discount = cart.getOrderOtherAdjustmentTotal();
				discount = discount.negate();
				BigDecimal discountPercentAmount = webposSession.getCurrentTransaction().getPercentAmount();
				if (!grandTotalCart.equals(BigDecimal.ZERO)) {
					if (grandTotalCart.compareTo(BigDecimal.ZERO) > 0) {
						discountPercentAmount = discount.divide(grandTotalCart, 4, RoundingMode.UP)
								.multiply(new BigDecimal(100));
					}
				}
				String currency = cart.getCurrency();
				// get loyalty point
				LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
				try {
					Map<String, Object> tmpResult = dispatcher.runSync("getLoyaltyPointCustomer", UtilMisc.toMap("cart", cart, "userLogin", userLogin));
					BigDecimal totalPointOrder = (BigDecimal) tmpResult.get("totalPointOrder");
					cartHeader.put("loyaltyPoint", totalPointOrder);
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
				cartHeader.put("amountDiscount", discount);
				discountPercentAmount = discountPercentAmount.setScale(0, BigDecimal.ROUND_HALF_UP);
				cartHeader.put("amountPercent", discountPercentAmount);
				cartHeader.put("totalDue", totalDue);
				cartHeader.put("grandTotalCart", grandTotalCart);
				cartHeader.put("currency", currency);
			} else {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCartNotFound", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSWebPosSessionNotFound", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		request.setAttribute("cartHeader", cartHeader);
		return "success";
	}

	public static String openTerminal(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession(true);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		String amount = request.getParameter("startingDrawerAmount");
		try {
			BigDecimal startingDrawerAmount = new BigDecimal(amount);
			if (UtilValidate.isNotEmpty("webPosSession")) {
				String userLoginId = webPosSession.getUserLoginId();
				WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
				if (UtilValidate.isNotEmpty(webposTransaction)) {
					if (!webposTransaction.isOpen()) {
						if (UtilValidate.isNotEmpty(startingDrawerAmount)) {
							GenericValue terminalState = delegator.makeValue("PosTerminalState");
							String posTerminalStateId = delegator.getNextSeqId("PosTerminalState");
							String posTerminalId = webPosSession.getId();
							Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
							String transactionId = webposTransaction.getTransactionId();
							terminalState.put("posTerminalStateId", posTerminalStateId);
							terminalState.put("posTerminalId", posTerminalId);
							terminalState.put("openedDate", nowTimestamp);
							terminalState.put("openedByUserLoginId", userLoginId);
							terminalState.put("startingTxId", transactionId);
							terminalState.put("startingDrawerAmount", startingDrawerAmount);
							terminalState.put("currency", webPosSession.getCurrencyUomId());
							try {
								terminalState.create();
								request.setAttribute("posTerminalStateId", posTerminalStateId);
								// Update first PosTerminalLog record
						        List<EntityCondition> condition = FastList.newInstance();
						        condition.add(EntityCondition.makeCondition("posTerminalId", EntityOperator.EQUALS, webPosSession.getId()));
						        condition.add(EntityCondition.makeCondition("posTerminalStateId", EntityOperator.EQUALS, null));
						        List<GenericValue> posTerminalState = delegator.findList("PosTerminalLog", EntityCondition.makeCondition(condition, EntityJoinOperator.AND), null, null, null, false);
						        for (GenericValue genericValue : posTerminalState) {
						         genericValue.set("posTerminalStateId", posTerminalStateId);
						         genericValue.store();
						        }
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_error,
										"BPOSCanNotCreateAPosTerminalState", locale);
								request.setAttribute("_ERROR_MESSAGE_", errorMessage);
								return "error";
							}
						} else {
							String errorMessage = UtilProperties.getMessage(resource_error,
									"BPOSManagerOpenTerminalDrawingAmountNotValid", locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
					} else {
						List<EntityCondition> condition = FastList.newInstance();
						condition.add(EntityCondition.makeCondition("posTerminalId", EntityOperator.EQUALS,
								webPosSession.getId()));
						condition.add(EntityCondition.makeCondition("closedDate", EntityOperator.EQUALS, null));
						condition.add(EntityCondition.makeCondition("openedByUserLoginId", EntityOperator.EQUALS,
								userLoginId));
						List<GenericValue> posTerminalState = delegator.findList("PosTerminalState",
								EntityCondition.makeCondition(condition, EntityJoinOperator.AND), null, null, null,
								false);
						String posTerminalStateId = "";
						if (UtilValidate.isNotEmpty(posTerminalState)) {
							posTerminalStateId = posTerminalState.get(0).getString("posTerminalStateId");
						}
						request.setAttribute("posTerminalStateId", posTerminalStateId);
						String errorMessage = UtilProperties.getMessage(resource_error,
								"BPOSManagerTerminalAlreadyOpened", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				}
			} else {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		} catch (Exception e) {
			String errorMessage = UtilProperties.getMessage(resource_error,
					"BPOSManagerOpenTerminalDrawingAmountNotValid", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}

	public static String closeTerminal(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		String amount = request.getParameter("endingDrawerCashAmount");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			BigDecimal endingDrawerCashAmount = new BigDecimal(amount);
			if (UtilValidate.isNotEmpty(webPosSession)) {
				WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
				if (UtilValidate.isNotEmpty(webposTransaction)) {
					GenericValue terminalState = webposTransaction.getTerminalState();
					if (UtilValidate.isNotEmpty(terminalState)) {
						String userLoginId = webPosSession.getUserLoginId();
						Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
						String transactionId = webposTransaction.getTransactionId();
						terminalState.set("closedByUserLoginId", userLoginId);
						terminalState.set("actualEndingCash", endingDrawerCashAmount);
						terminalState.set("closedDate", nowTimestamp);
						terminalState.set("endingTxId", transactionId);
						terminalState.store();
						// Update first PosTerminalLog record
				        List<EntityCondition> condition = FastList.newInstance();
				        condition.add(EntityCondition.makeCondition("posTerminalId", EntityOperator.EQUALS, webPosSession.getId()));
				        condition.add(EntityCondition.makeCondition("posTerminalStateId", EntityOperator.EQUALS, null));
				        List<GenericValue> posTerminalState = delegator.findList("PosTerminalLog", EntityCondition.makeCondition(condition, EntityJoinOperator.AND), null, null, null, false);
				        for (GenericValue genericValue : posTerminalState) {
				         genericValue.set("posTerminalStateId", terminalState.get("posTerminalStateId"));
				         genericValue.store();
				        }
					}
				}
				LoginWorker.logout(request, response);
			} else {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		} catch (Exception e) {
			String errorMessage = UtilProperties.getMessage(resource_error,
					"BPOSManagerOpenTerminalDrawingAmountNotValid", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}

		return "success";
	}

	public static String processPromotionCode(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		String productPromoCodeId = request.getParameter("productPromoCodeId");
		String promoName = "";
		String promoText = "";
		if (UtilValidate.isNotEmpty(productPromoCodeId)) {
			GenericValue promoCode = delegator.findOne("ProductPromoCode",
					UtilMisc.toMap("productPromoCodeId", productPromoCodeId), true);
			if (UtilValidate.isNotEmpty(promoCode)) {
				GenericValue promotion = delegator.findOne("ProductPromo",
						UtilMisc.toMap("productPromoId", promoCode.getString("productPromoId")), true);
				if (UtilValidate.isNotEmpty(promotion)) {
					promoName = promotion.getString("promoName");
					promoText = promotion.getString("promoText");
				}
			}
		}

		String message = UtilProperties.getMessage(resource_error, "BPOSMessagePromotion",
				UtilMisc.toMap("promoName", promoName, "promoText", promoText), locale);
		if (!promoName.equals("")) {
			request.setAttribute("messageSuccess", message);
		}

		if (UtilValidate.isNotEmpty(webPosSession)) {
			String resultOfAddPromo = ShoppingCartEvents.addProductPromoCode(request, response);
			WebPosTransaction webposTransaction = webPosSession.getCurrentTransaction();
			webposTransaction.calcTax();
			if (resultOfAddPromo.equals("success")) {
				return resultOfAddPromo;
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}

		return "success";
	}

	private static String getInvoiceItemType(Delegator delegator, String key1, String key2, String invoiceTypeId,
			String defaultValue) {
		GenericValue itemMap = null;
		try {
			if (UtilValidate.isNotEmpty(key1)) {
				itemMap = delegator.findOne("InvoiceItemTypeMap",
						UtilMisc.toMap("invoiceItemMapKey", key1, "invoiceTypeId", invoiceTypeId), true);
			}
			if (itemMap == null && UtilValidate.isNotEmpty(key2)) {
				itemMap = delegator.findOne("InvoiceItemTypeMap",
						UtilMisc.toMap("invoiceItemMapKey", key2, "invoiceTypeId", invoiceTypeId), true);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Trouble getting InvoiceItemTypeMap entity record", module);
			return defaultValue;
		}
		if (itemMap != null) {
			return itemMap.getString("invoiceItemTypeId");
		} else {
			return defaultValue;
		}
	}

	private static void checkPosTerminal(Delegator delegator, Locale locale, String posTerminalId, String userLoginId)
			throws Exception {
		boolean beganTx = TransactionUtil.begin(7200);
		List<EntityCondition> conditions = FastList.newInstance();
		EntityListIterator visit = null;
		try {
			conditions.add(EntityCondition.makeCondition("posTerminalId", EntityJoinOperator.EQUALS, posTerminalId));
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			visit = delegator.find("Visit", EntityCondition.makeCondition(conditions), null, null, null, findOptions);
			if (visit.getResultsTotalSize() != 0) {
				String errMsg = UtilProperties.getMessage(resource_error, "BPOSHadEmployeesSignInAtThisPOS", locale);
				throw new Exception(errMsg);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (visit != null) {
				visit.close();
			}
		}
		EntityListIterator userLogin = null;
		try {
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("userLoginId", EntityJoinOperator.EQUALS, userLoginId));
			conditions.add(EntityCondition.makeCondition("hasLoggedOut", EntityJoinOperator.EQUALS, "N"));
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			userLogin = delegator.find("UserLogin", EntityCondition.makeCondition(conditions), null, null, null,
					findOptions);
			if (userLogin.getResultsTotalSize() != 0) {
				String errMsg = UtilProperties.getMessage(resource_error, "BPOSAccountIsLoggedInTheSystem", locale);
				throw new Exception(errMsg);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (userLogin != null) {
				userLogin.close();
			}
		}
		TransactionUtil.commit(beganTx);
	}
	
	public static void posSessionLogout(HttpSession session) {
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		if (webPosSession != null) {
			webPosSession.logout();
		}
	}
	
}