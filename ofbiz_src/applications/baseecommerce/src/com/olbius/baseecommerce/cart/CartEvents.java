package com.olbius.baseecommerce.cart;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;

import com.olbius.basehr.util.SecurityUtil;

public class CartEvents {

	public final static String module = CartEvents.class.getName();
	public static final String resource = "CommonUiLabels";
	public static final String resource_error = "OrderErrorUiLabels";

	public static String setCheckOutPages(HttpServletRequest request, HttpServletResponse response) {
		if ("error".equals(CheckOutEvents.cartNotEmpty(request, response)) == true) {
			return "error";
		}
		HttpSession session = request.getSession();
		String curPage = request.getParameter("checkoutpage");
		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = (String) request.getAttribute("partyId");
		String contactMechId = (String) request.getAttribute("contactMechId");
		String contactMechIdEmail = (String) request.getAttribute("contactMechIdEmail");
		String contactMechIdPhone = (String) request.getAttribute("contactMechIdPhone");
		GenericValue userLogin = cart.getUserLogin();
		if (userLogin == null){
//			request.setAttribute("phone", request.getParameter("phone"));
//			request.setAttribute("email", request.getParameter("email"));
			userLogin = (GenericValue) session.getAttribute("userLogin");
		}

		if (curPage == null) {
			curPage = (String) request.getAttribute("checkoutpage");
			if(curPage == null){
				try {
					cart.createDropShipGroups(dispatcher);
				} catch (CartItemModifyException e) {
					Debug.logError(e, module);
				}
			}else {
				// remove empty ship group
				cart.cleanUpShipGroups();
			}
		}
		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

		if ("confirm".equals(curPage) == true) {
			// Set the shipping address options
//			partyId = (String) request.getParameter("partyId");
			if(UtilValidate.isNotEmpty(userLogin)){
				partyId = userLogin.getString("partyId");
			}else{
				partyId = "_NA_";
			}
			cart.setOrderPartyId(partyId);


			Map<String, Map<String, Object>> selectedPaymentMethods = CheckOutEvents.getSelectedPaymentMethods(request);
			List<String> singleUsePayments = new ArrayList<String>();

			String billingAccountId = request.getParameter("billingAccountId");
            if (UtilValidate.isNotEmpty(billingAccountId)) {
                BigDecimal billingAccountAmt = null;
                billingAccountAmt = determineBillingAccountAmount(billingAccountId, request.getParameter("billingAccountAmount"), dispatcher);
                if ((billingAccountId != null) && !"_NA_".equals(billingAccountId) && (billingAccountAmt == null)) {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderInvalidAmountSetForBillingAccount", UtilMisc.toMap("billingAccountId",billingAccountId), cart.getLocale()));
                    return "error";
                }
                selectedPaymentMethods.put("EXT_BILLACT", UtilMisc.<String, Object>toMap("amount", billingAccountAmt, "securityCode", null));
            }

            if (UtilValidate.isEmpty(selectedPaymentMethods)) {
                return "error";
            }

            // check for gift card not on file
            Map<String, Object> params = UtilHttp.getParameterMap(request);
            Map<String, Object> gcResult = checkOutHelper.checkGiftCard(params, selectedPaymentMethods);
            ServiceUtil.getMessages(request, gcResult, null);
            if (gcResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
                return "error";
            } else {
                String gcPaymentMethodId = (String) gcResult.get("paymentMethodId");
                BigDecimal gcAmount = (BigDecimal) gcResult.get("amount");
                if (gcPaymentMethodId != null) {
                    selectedPaymentMethods.put(gcPaymentMethodId, UtilMisc.<String, Object>toMap("amount", gcAmount, "securityCode", null));
                    if ("Y".equalsIgnoreCase(request.getParameter("singleUseGiftCard"))) {
                        singleUsePayments.add(gcPaymentMethodId);
                    }
                }
            }

            Map<String, Object> callResult2 = checkOutHelper.setCheckOutPayment(selectedPaymentMethods,
					singleUsePayments, null);
            Map<String, ? extends Object> callResult = ServiceUtil.returnSuccess();
			ServiceUtil.getMessages(request, callResult, null);
			long shipBeTm = Long.parseLong((String) request
					.getParameter("shipBeforeDate"));
			updateShippingBeforeDate(request, shipBeTm);
			if (!(callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
					&& !(callResult2.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))) {
				// No errors so push the user onto the next page
				try {
					GenericValue system = delegator.findOne("UserLogin",
							UtilMisc.toMap("userLoginId", "system"), false);
					request.setAttribute("userLogin", system);
//					cart.setUserLogin(system, dispatcher);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String status = createOrder(request, response);
				if(status.equals("error")){
					return "error";
				}
				
				String orderId = cart.getOrderId();
//				ShoppingCartEvents.clearCart(request, response);
				request.setAttribute("checkOutPaymentId", request.getParameter("checkOutPaymentId"));
				request.setAttribute("orderId", orderId);
//				request.setAttribute("shipping_instructions", request.getAttribute("shippingInstructions"));
//				request.setAttribute("address", "");
				String date = (String) request.getAttribute("shipBeforeDate");
				Timestamp shippingDate = Timestamp.valueOf(date);
				request.setAttribute("shipBeforeDate", formatShippingDate(shippingDate, 2, "-"));
				curPage = "success";
			}
		}else if(UtilValidate.isNotEmpty(partyId) && userLogin == null && UtilValidate.isNotEmpty(contactMechId) && curPage.equals("shippinginfo")){
			//Check current cart state is choosing payment method
			//Set contact mech shipping
//			String shippingContactMechId = null;
//			if(UtilValidate.isEmpty(contactMechId)){
//				contactMechId = (String) request.getParameter("contactMechId");
//			}
//			if(UtilValidate.isNotEmpty(contactMechId)){
//				shippingContactMechId = contactMechId;
//			}else{
//				try {
//					List<GenericValue> tmpAddr = delegator.findList("PartyContactMechPurpose",EntityCondition.makeCondition(
//														UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
//																EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"))), null, UtilMisc.toList("-contactMechId"), null, false);
//					if(UtilValidate.isNotEmpty(tmpAddr)){
//						GenericValue e = tmpAddr.get(0);
//						shippingContactMechId = e.getString("contactMechId");
//					}else{
//						curPage = "error";
//					}
//				} catch (GenericEntityException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					return "error";
//				}
//			}
			curPage = setShippingInformation(request, cart, checkOutHelper, contactMechId, partyId);
		}else if(UtilValidate.isNotEmpty(userLogin)){
			if(UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(curPage) && curPage.equals("shippinginfo")){
				curPage = setShippingInformation(request, cart, checkOutHelper, contactMechId, partyId);
			}else{
				curPage = "shippinginfo";
			}
		}else if(UtilValidate.isEmpty(curPage)){
			curPage = "login";
		}
		if (UtilValidate.isNotEmpty(partyId) && userLogin == null) {
			if (UtilValidate.isNotEmpty(contactMechIdEmail)) cart.addContactMech("ORDER_EMAIL", contactMechIdEmail);
			if (UtilValidate.isNotEmpty(contactMechIdPhone)) cart.addContactMech("PHONE_SHIPPING", contactMechIdPhone);
		}
		keepAttributeInCart(request);
		request.setAttribute("currentpage", curPage);
		return curPage;
	}
	// Create order event - uses createOrder service for processing
    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
    	HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> callResult;
        if(UtilValidate.isEmpty(userLogin)){
        	userLogin = (GenericValue) request.getAttribute("userLogin");
        }
        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");
        boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);
        //get the TrackingCodeOrder List
        List<GenericValue> trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);
        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        String visitId = VisitHandler.getVisitId(session);
        String webSiteId = WebSiteWorker.getWebSiteId(request);
        callResult = checkOutHelper.createOrder(userLogin, distributorId, affiliateId, trackingCodeOrders, areOrderItemsExploded, visitId, webSiteId);
        if (callResult != null) {
            ServiceUtil.getMessages(request, callResult, null);
            if (ServiceUtil.isError(callResult)) {
                // messages already setup with the getMessages call, just return the error response code
                return "error";
            }
            if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                // set the orderId for use by chained events
                String orderId = cart.getOrderId();
                request.setAttribute("orderId", orderId);
                request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
                Map<String, Map<String, Object>> selectedPaymentMethods = CheckOutEvents.getSelectedPaymentMethods(request);
                if (selectedPaymentMethods.containsKey("EXT_OFFLINE")) {
                	try {
                		String header = UtilProperties.getMessage("EcommerceBackendUiLabels", "NotifyNewOrder", cart.getLocale());
                		header += " [" + orderId + "]";
						dispatcher.runSync("createNotification",
								UtilMisc.toMap("roleTypeId", "ACC_PAYMENT_EMP", "targetLink", "orderId=" + orderId,
										"action", "viewOrder", "header", header, "ntfType", "ONE", "userLogin", userLogin));
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
				}
                try {
            		String header = UtilProperties.getMessage("EcommerceBackendUiLabels", "NotifyNewOrder", cart.getLocale());
            		header += " [" + orderId + "]";
					String roleTypeId = EntityUtilProperties.getPropertyValue("basesales.properties", "roleTypeId.receiveMsg.returnorder.approved", delegator);
					List<String> parties = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, roleTypeId, delegator);
					if (!parties.isEmpty()){
						dispatcher.runSync("createNotification",
								UtilMisc.toMap("partiesList", parties, "targetLink", "orderId=" + orderId,
										"action", "viewOrder", "header", header, "ntfType", "ONE", "userLogin", userLogin));
					} else {
						try {
							GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
							String storeId = orderHeader.getString("productStoreId");
							List<GenericValue> listStoreRoles = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", storeId, "roleTypeId", "OWNER")), null, null, null, false);
							listStoreRoles = EntityUtil.filterByDate(listStoreRoles);
							if (!listStoreRoles.isEmpty()){
								String partyOrg = listStoreRoles.get(0).getString("partyId");
								parties = SecurityUtil.getPartiesByRolesWithOrg(partyOrg, roleTypeId, delegator);
								dispatcher.runSync("createNotification",
										UtilMisc.toMap("partiesList", parties, "targetLink", "orderId=" + orderId,
												"action", "viewOrder", "header", header, "ntfType", "ONE", "userLogin", userLogin));
							}
						} catch (GenericEntityException e){
							 return "error";
						}
					}
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
            }
        }

        String issuerId = request.getParameter("issuerId");
        if (UtilValidate.isNotEmpty(issuerId)) {
            request.setAttribute("issuerId", issuerId);
        }

        return cart.getOrderType().toLowerCase();
    }
    public static void updateShippingBeforeDate(HttpServletRequest request, long shipBeTm){
		Timestamp shipBeforeDate = new Timestamp(shipBeTm);
		request.setAttribute("shipBeforeDate", shipBeforeDate.toString());
		HttpSession session = request.getSession();
		ShoppingCart cart = (ShoppingCart) session
				.getAttribute("shoppingCart");
		for (ShoppingCartItem item : cart.items()) {
//			item.setShipBeforeDate(shipBeforeDate);
//			item.setEstimatedShipDate(shipBeforeDate);
			item.setDesiredDeliveryDate(shipBeforeDate);
		}
    }
    @SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getPromoItem(List<ShoppingCartItem> items){
		Map<String, Object> promos = FastMap.newInstance();
		BigDecimal quantity = new BigDecimal(0);
		BigDecimal tmpQuan = new BigDecimal(0);
		for(ShoppingCartItem item : items){
			if(item.getIsPromo()){
				String productId = item.getProductId();
				quantity = item.getQuantity();
				if(!promos.containsKey(productId)){
					Map<String, Object> o = FastMap.newInstance();
					o.put("productId", productId);
					o.put("lineOptionalFeatures", item.getOptionalProductFeatures());
					o.put("product", item.getProduct());
					o.put("productName", item.getName());
					o.put("amount", item.getDisplayPrice());
					o.put("quantity", item.getQuantity());
					promos.put(productId, o);
				}else{
					Map<String, Object> o = (Map<String, Object>) promos.get(productId);
					tmpQuan = (BigDecimal) o.get("quantity");
					quantity = tmpQuan.add(quantity);
					o.put("quantity", quantity);
					promos.put(productId, o);
				}
			}
		}
		List<String> keys = new ArrayList<String>(promos.keySet());
		List<Map<String, Object>> list = FastList.newInstance();
		for(String key : keys){
			list.add((Map<String, Object>) promos.get(key));
		}
		return list;
    }
    private static BigDecimal determineBillingAccountAmount(String billingAccountId, String billingAccountAmount, LocalDispatcher dispatcher) {
        BigDecimal billingAccountAmt = null;

        // set the billing account amount to the minimum of billing account available balance or amount input if less than balance
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            // parse the amount to a decimal
            if (UtilValidate.isNotEmpty(billingAccountAmount)) {
                try {
                    billingAccountAmt = new BigDecimal(billingAccountAmount);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (billingAccountAmt == null) {
                billingAccountAmt = BigDecimal.ZERO;
            }
            BigDecimal availableBalance = CheckOutHelper.availableAccountBalance(billingAccountId, dispatcher);

            // set amount to be charged to entered amount unless it exceeds the available balance
            BigDecimal chargeAmount = BigDecimal.ZERO;
            if (billingAccountAmt.compareTo(availableBalance) < 0) {
                chargeAmount = billingAccountAmt;
            } else {
                chargeAmount = availableBalance;
            }
            if (chargeAmount.compareTo(BigDecimal.ZERO) < 0.0) {
                chargeAmount = BigDecimal.ZERO;
            }

            return chargeAmount;
        } else {
            return null;
        }
    }
    private static String formatShippingDate(Timestamp time, int type, String slash){
	StringBuilder tmp = new StringBuilder();
		Date orderDateTmp = new Date(time.getTime());
		SimpleDateFormat simpleFormat = null;
		switch (type) {
			case 0: // DD:MM:YYYY HH:MM
				simpleFormat = new SimpleDateFormat("dd"+slash+"MM"+slash+"yyyy" + " HH:mm");
				tmp = new StringBuilder(simpleFormat.format(orderDateTmp));
				break;
			case 1: //YYYY:MM:DD HH:MM
				simpleFormat = new SimpleDateFormat("yyyy" + slash + "MM" + slash + "dd" + " HH:mm");
				tmp = new StringBuilder(simpleFormat.format(orderDateTmp));
				break;
			case 2:  //HH:MM DD:MM:YYYY
				simpleFormat = new SimpleDateFormat("HH:mm " + "dd" + slash + "MM" + slash + "yyyy");
				tmp = new StringBuilder(simpleFormat.format(orderDateTmp));
				break;
			default:
				simpleFormat = new SimpleDateFormat("yyyy" + slash + "MM" + slash + "dd" + " HH:mm");
				tmp = new StringBuilder(simpleFormat.format(orderDateTmp));
				break;
		}
		return tmp.toString();
    }
    private static String setShippingInformation(HttpServletRequest request, ShoppingCart cart, CheckOutHelper checkOutHelper, String contactMechId, String partyId){
	Map<String, ? extends Object> callResult0 = checkOutHelper.setCheckOutShippingAddress(contactMechId);
	if(UtilValidate.isNotEmpty(request.getParameter("shipBeforeDate"))){
		request.setAttribute("shipBeforeDate", request.getParameter("shipBeforeDate"));
	}
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
		//After set shipping -> choose payment method
		if(!callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)){
			return "confirm";
		}
		return "shippinginfo";
    }
    public static String checkOrderInformation(HttpServletRequest request, HttpServletResponse response){
		ShoppingCartEvents.clearCart(request, response);
		String orderId = (String) request.getAttribute("orderId");
		String checkOutPaymentId = (String) request.getAttribute("checkOutPaymentId");
		String shipBeforeDate = (String) request.getAttribute("shipBeforeDate");
		keepAttributeInCart(request);
		if(UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(checkOutPaymentId) && UtilValidate.isNotEmpty(shipBeforeDate)){
		return "success";
	}
	return "error";
    }

    public static String processPayment(HttpServletRequest request, HttpServletResponse response) {
        // run the process payment process + approve order when complete; may also run sync fulfillments
        int failureCode = 0;
        try {
            if (!processPayment(request)) {
                failureCode = 1;
            }
        } catch (GeneralException e) {
            Debug.logError(e, module);
            ServiceUtil.setMessages(request, e.getMessage(), null, null);
            failureCode = 2;
        } catch (GeneralRuntimeException e) {
            Debug.logError(e, module);
            ServiceUtil.setMessages(request, e.getMessage(), null, null);
        }

        // event return based on failureCode
        switch (failureCode) {
            case 0:
                return "success";
            case 1:
                return "fail";
            default:
                return "error";
        }
    }
    private static boolean processPayment(HttpServletRequest request) throws GeneralException {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if(userLogin == null){
		userLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
        }
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

        // check if the order is to be held (processing)
        boolean holdOrder = cart.getHoldOrder();

        // load the ProductStore settings
        GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
        Map<String, Object> callResult = checkOutHelper.processPayment(productStore, userLogin, false, holdOrder);

        if (ServiceUtil.isError(callResult)) {
            // clear out the rejected payment methods (if any) from the cart, so they don't get re-authorized
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
        keepAttributeInCart(request);
        // determine whether it was a success or failure
        return (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS));
    }
    private static void keepAttributeInCart(HttpServletRequest request){
	request.setAttribute("partyId", request.getAttribute("partyId"));
		request.setAttribute("fullName",  request.getAttribute("fullName"));
		request.setAttribute("phone",  request.getAttribute("phone"));
		request.setAttribute("email",  request.getAttribute("email"));
		request.setAttribute("contactMechId",  request.getAttribute("contactMechId"));
		request.setAttribute("orderId", request.getAttribute("orderId"));
		request.setAttribute("checkOutPaymentId", request.getAttribute("checkOutPaymentId"));
		request.setAttribute("shipping_instructions", request.getAttribute("shipping_instructions"));
		request.setAttribute("shipBeforeDate", request.getAttribute("shipBeforeDate"));
    }
}
