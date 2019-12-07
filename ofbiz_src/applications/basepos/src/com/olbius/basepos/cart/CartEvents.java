package com.olbius.basepos.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basepos.events.WebPosEvents;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.transaction.WebPosTransaction;

import javolution.util.FastList;
import javolution.util.FastMap;

public class CartEvents {
	public static String resource_error = "BasePosErrorUiLabels";
	public static String module = CartEvents.class.getName();

	public static String setPartyToCart(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException, CartItemModifyException {
		Locale locale = request.getLocale();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		String partyId = request.getParameter("partyId");
		String contactMechPurposeTypeId = request.getParameter("contactMechPurposeTypeId");
		if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
			contactMechPurposeTypeId = "PRIMARY_LOCATION";
		}
		String contactMechId = null;
		if (UtilValidate.isNotEmpty(webposSession)) {
			WebPosTransaction webPosTransaction = webposSession.getCurrentTransaction();
			ShoppingCart cart = webposSession.getCart();
			if (UtilValidate.isNotEmpty(cart)) {
				List<EntityCondition> mainConditions = FastList.newInstance();
				mainConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				mainConditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,
						contactMechPurposeTypeId));
				EntityCondition mainCond = EntityCondition.makeCondition(mainConditions, EntityOperator.AND);
				List<GenericValue> partyContacMechPurposes = delegator.findList("PartyContactMechPurpose", mainCond,
						null, UtilMisc.toList("fromDate"), null, false);
				if (UtilValidate.isNotEmpty(partyContacMechPurposes)) {
					partyContacMechPurposes = EntityUtil.filterByDate(partyContacMechPurposes);
				}
				if (UtilValidate.isNotEmpty(partyContacMechPurposes)) {
					GenericValue partyContactMechFirst = EntityUtil.getFirst(partyContacMechPurposes);
					if (UtilValidate.isNotEmpty(partyContactMechFirst)) {
						contactMechId = partyContactMechFirst.getString("contactMechId");
					}
				}
				if (UtilValidate.isEmpty(contactMechId)) {
					String errorMessage = UtilProperties.getMessage(resource_error,
							"BPOSBillingOrShippingLocationMissing", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				} else {
					cart.setUserLogin(userLogin, dispatcher);
					cart.addContactMech(contactMechPurposeTypeId, contactMechId);
					if (cart.getOrderType().equals("SALES_ORDER")) {
						/*
						 * cart.setBillToCustomerPartyId(partyId);
						 * cart.setPlacingCustomerPartyId(partyId);
						 */
						cart.setOrderPartyId(partyId);
						webPosTransaction.calcTax();
						ProductPromoWorker.doPromotions(cart, dispatcher);
					} else {
						webPosTransaction.setPartyId(partyId);
						cart.setOrderPartyId(partyId);
						cart.setAttribute("supplierPartyId", partyId);
					}
				}
			}
		} else {
			Debug.log("You must login");
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}

	public static String getPartyInfoInCart(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession sesson = request.getSession();
		WebPosSession webposSession = (WebPosSession) sesson.getAttribute("webPosSession");
		Map<String, Object> partyInfo = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(webposSession)) {
			GenericValue userLogin = webposSession.getUserLogin();
			String partyOfUserLogin = userLogin.getString("partyId");
			ShoppingCart shoppingCart = webposSession.getCart();
			if (UtilValidate.isNotEmpty(shoppingCart)) {
				String partyId = "_NA_";
				String partyName = "";
				if (shoppingCart.getOrderType().equals("SALES_ORDER")) {
					partyId = shoppingCart.getBillToCustomerPartyId();
				} else {
					partyId = shoppingCart.getBillToCustomerPartyId();
				}
				if (UtilValidate.isNotEmpty(partyId) && !partyId.equalsIgnoreCase("_NA_")
						&& !partyId.equalsIgnoreCase(partyOfUserLogin)) {
					partyInfo.put("partyId", partyId);
					if (shoppingCart.getOrderType().equalsIgnoreCase("SALES_ORDER")) {
						GenericValue party = null;
						try {
							party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
						} catch (GenericEntityException e) {
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetPartyInfo",
									locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						if (UtilValidate.isNotEmpty(party)) {
							String lastName = party.getString("lastName");
							if (UtilValidate.isNotEmpty(lastName)) {
								partyName = partyName + lastName;
							}
							String middleName = party.getString("middleName");
							if (UtilValidate.isNotEmpty(middleName)) {
								partyName = partyName + " " + middleName;
							}
							String firstName = party.getString("firstName");
							if (UtilValidate.isNotEmpty(firstName)) {
								partyName = partyName + " " + firstName;
							}
						}
					} else {
						GenericValue party = null;
						try {
							party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
						} catch (GenericEntityException e) {
							String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetPartyInfo",
									locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
						if (UtilValidate.isNotEmpty(party)) {
							partyName = party.getString("groupName");
						} else {
							try {
								party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
							} catch (GenericEntityException e) {
								String errorMessage = UtilProperties.getMessage(resource_error,
										"BPOSCanNotGetPartyInfo", locale);
								request.setAttribute("_ERROR_MESSAGE_", errorMessage);
								return "error";
							}
							if (UtilValidate.isNotEmpty(party)) {
								String lastName = party.getString("lastName");
								if (UtilValidate.isNotEmpty(lastName)) {
									partyName = partyName + lastName;
								}
								String middleName = party.getString("middleName");
								if (UtilValidate.isNotEmpty(middleName)) {
									partyName = partyName + " " + middleName;
								}
								String firstName = party.getString("firstName");
								if (UtilValidate.isNotEmpty(firstName)) {
									partyName = partyName + " " + firstName;
								}
							}
						}
					}
					partyInfo.put("partyName", partyName);
					// get party address
					Map<String, Object> partyAddressMap = FastMap.newInstance();
					partyAddressMap.put("partyId", partyId);
					partyAddressMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
					partyAddressMap.put("userLogin", userLogin);
					Map<String, Object> partyAddressInfo = FastMap.newInstance();
					try {
						partyAddressInfo = dispatcher.runSync("getPartyPostalAddressAddState", partyAddressMap);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
					if (ServiceUtil.isError(partyAddressInfo)) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetPartyInfo",
								locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					} else {
						String partyAddress = "";
						String address1 = (String) partyAddressInfo.get("address1");
						String city = (String) partyAddressInfo.get("city");
						if (UtilValidate.isNotEmpty(address1)) {
							partyAddress = partyAddress + address1;
						}
						if (UtilValidate.isNotEmpty(city)) {
							partyAddress = partyAddress + " - " + city;
						}
						partyInfo.put("partyAddress", partyAddress);
					}
					// get party telephone
					Map<String, Object> partyTelephoneMap = FastMap.newInstance();
					partyTelephoneMap.put("partyId", partyId);
					partyTelephoneMap.put("userLogin", userLogin);
					partyTelephoneMap.put("contactMechPurposeTypeId", "PHONE_MOBILE");
					Map<String, Object> partyTelephone = FastMap.newInstance();
					try {
						partyTelephone = dispatcher.runSync("getPartyTelephone", partyTelephoneMap);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
					if (ServiceUtil.isError(partyTelephone)) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotGetPartyInfo",
								locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					} else {
						String contactNumber = (String) partyTelephone.get("contactNumber");
						if (UtilValidate.isNotEmpty(contactNumber)) {
							partyInfo.put("partyTelephone", contactNumber);
						}
					}
					// get party loyalty
					try {
						String productStoreId = ProductStoreWorker.getProductStoreId(request);
						List<EntityExpr> condition = FastList.newInstance();
						condition.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						condition.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
						condition.add(
								EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
						GenericValue loyaltyCustomer = EntityUtil.getFirst(delegator.findList("LoyaltyCustomer",
								EntityCondition.makeCondition(condition, EntityOperator.AND), null, null, null, false));
						if (loyaltyCustomer != null) {
							String loyaltyName = loyaltyCustomer.getString("ratingName");
							if (UtilValidate.isNotEmpty(loyaltyName)) {
								partyInfo.put("partyLoyalty", loyaltyName);
							}
						}
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		request.setAttribute("partyInfo", partyInfo);
		return "success";
	}

	public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);  
		HttpSession session = request.getSession();
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		if (UtilValidate.isNotEmpty(webposSession)) {
			String resultOfAddToCart = ShoppingCartEvents.addToCart(request, response);
			webposSession.getCurrentTransaction().calcTax();
			if (resultOfAddToCart.equals("success")) {
				return resultOfAddToCart;
			} else {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotAddToCart", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
	}

	public static String updateCartItem(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		String cartLineIndex = request.getParameter("cartLineIndex");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		if (UtilValidate.isNotEmpty(webposSession)) {
			ShoppingCart cart = webposSession.getCart();
			int index = Integer.parseInt(cartLineIndex);
			ShoppingCartItem cartItem = cart.findCartItem(index);
			String quantityParam = request.getParameter("quantity");
			BigDecimal quantity = BigDecimal.ZERO;
			try {
				quantity = new BigDecimal(quantityParam);
			} catch (Exception e) {
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSQuantityItemNotValid", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
			if (UtilValidate.isNotEmpty(cartItem)) {
				boolean triggerExternalOps = true;
				boolean resetShipGroup = true;
				boolean updateProductPrice = true;
				try {
					cartItem.setQuantity(quantity, dispatcher, cart, triggerExternalOps, resetShipGroup,
							updateProductPrice);
				} catch (CartItemModifyException e) {
					String errors = e.toString();
					String[] error = errors.split(":");
					request.setAttribute("_ERROR_MESSAGE_", error[1]);
					return "error";
				}
				WebPosTransaction webposTransaction = webposSession.getCurrentTransaction();
				webposTransaction.calcTax();
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}

	public static String updateUom(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		String cartLineIndex = request.getParameter("cartLineIndex");
		String uomFromId = request.getParameter("uomFromId");
		String uomToId = request.getParameter("uomToId");
		int index = Integer.parseInt(cartLineIndex);
		Map<String, Object> mapItem = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(webposSession)) {
			ShoppingCart cart = webposSession.getCart();
			WebPosTransaction webposTransaction = webposSession.getCurrentTransaction();
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
				ShoppingCartItem cartItem = cart.findCartItem(index);
				if (UtilValidate.isNotEmpty(cartItem) && UtilValidate.isNotEmpty(uomFromId)
						&& UtilValidate.isNotEmpty(uomToId)) {
					try {
						cartItem.updateUom(uomFromId, uomToId, dispatcher, cart);
						webposTransaction.calcTax();
						String productId = cartItem.getProductId();
						mapItem.put("cartLineIndex", index);
						mapItem.put("productId", productId);
						mapItem.put("uomList", cartItem.getListQuantityUom());
						GenericValue product = cartItem.getProduct();
						mapItem.put("productName", product.getString("internalName"));
						mapItem.put("currencyUom", cart.getCurrency());
						String quantityUomId = (String) cartItem.getAttribute("quantityUomId");
						mapItem.put("quantityUomId", quantityUomId);
						if (showPricesWithVatTax.equals("Y")) {
							if (UtilValidate.isNotEmpty(quantityUomId)) {
								mapItem.put("quantity", cartItem.getAlternativeQuantity());
								mapItem.put("quantityATP", cartItem.getAttribute("alternativeAtpQuantity"));
							} else {
								mapItem.put("quantity", cartItem.getQuantity());
								mapItem.put("quantityATP", cartItem.getAttribute("atpQuantity"));
							}
							mapItem.put("unitPrice", cartItem.getAlterPriceWithTax(cart, dispatcher));
							mapItem.put("subTotal", cartItem.getItemSubTotalAndTax(cart));
						} else {
							if (UtilValidate.isNotEmpty(quantityUomId)) {
								mapItem.put("quantity", cartItem.getAlternativeQuantity());
								mapItem.put("unitPrice", cartItem.getAlternativeUnitPrice());
								mapItem.put("quantityATP", cartItem.getAttribute("alternativeAtpQuantity"));
							} else {
								mapItem.put("quantity", cartItem.getQuantity());
								mapItem.put("unitPrice", cartItem.getBasePrice());
								mapItem.put("quantityATP", cartItem.getAttribute("atpQuantity"));
							}
							mapItem.put("subTotal", cartItem.getItemSubTotalNotAdj());
						}
						mapItem.put("discount", cartItem.getAdjustment());
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
						List<GenericValue> listQuantityUomId = cartItem.getListQuantityUom();
						if (UtilValidate.isNotEmpty(listQuantityUomId)) {
							for (GenericValue uomItem : listQuantityUomId) {
								Map<String, String> uom = FastMap.newInstance();
								uom.put("quantityUomId", uomItem.getString("uomId"));
								uom.put("description", uomItem.getString("description"));
								listUom.add(uom);
							}
						}
						mapItem.put("uomList", listUom);
						GenericValue barCodeEntity = null;
						try {
							barCodeEntity = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId",
									productId, "goodIdentificationTypeId", "SKU", "uomId", quantityUomId), null, false));
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						if (UtilValidate.isNotEmpty(barCodeEntity)) {
							mapItem.put("barcode", barCodeEntity.getString("idValue"));
						}
					} catch (CartItemModifyException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSYouCanNotUpdateUom",
								locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				} else {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSYouCanNotUpdateUom", locale);
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

	public static String saleDiscount(HttpServletRequest request, HttpServletResponse response)
			throws GeneralException {
		HttpSession session = request.getSession(true);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		Locale locale = UtilHttp.getLocale(request);
		WebPosTransaction trans = webPosSession.getCurrentTransaction();
		boolean flagDiscount = trans.isFlagDiscount();
		String value = request.getParameter("amountDiscount");
		String percent = request.getParameter("percent");
		boolean discountPercent = Boolean.parseBoolean(percent);
		if (UtilValidate.isNotEmpty(value)) {
			ShoppingCart cart = webPosSession.getCart();
			BigDecimal amount = BigDecimal.ZERO;
			// if cart was discounted, cart must reserve amount that discounted
			// before
			if (flagDiscount) {
				List<GenericValue> listAdjustment = webPosSession.getCart().getAdjustments();
				if (UtilValidate.isNotEmpty(listAdjustment)) {
					for (GenericValue adjustment : listAdjustment) {
						int index = listAdjustment.indexOf(adjustment);
						cart.removeAdjustment(index);
					}
				}
			}
			BigDecimal grandTotal = webPosSession.getCurrentTransaction().getGrandTotal();
			String totalPercent = "100";
			BigDecimal totalPecent = new BigDecimal(totalPercent);
			try {
				amount = new BigDecimal(value);
				if (!discountPercent) {
					amount = amount.setScale(0, BigDecimal.ROUND_HALF_UP);
				}
			} catch (NumberFormatException e) {

			}
			if (discountPercent) {
				if ((amount.compareTo(BigDecimal.ZERO) != -1) && (amount.compareTo(totalPecent) == -1)) {
					if (discountPercent) {
						amount = amount.movePointLeft(2);
						amount = grandTotal.multiply(amount);
						amount = amount.setScale(0, BigDecimal.ROUND_HALF_UP);
					}
					amount = amount.negate();
					trans.addDiscount(amount, discountPercent);
					trans.calcTax();
				} else {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSPercentDiscountNotValid",
							locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			} else {
				if (cart.haveContainReturnItem()) {
					amount = amount.negate();
					trans.addDiscount(amount, discountPercent);
					trans.calcTax();
				} else {
					if ((amount.compareTo(BigDecimal.ZERO) != -1) && (amount.compareTo(grandTotal) == -1)) {
						amount = amount.negate();
						trans.addDiscount(amount, discountPercent);
						trans.calcTax();
					} else {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSMoneyDiscountNotValid",
								locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				}
			}
		}
		return "success";
	}

	public static String itemDiscount(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		WebPosTransaction trans = webposSession.getCurrentTransaction();
		String amountItemDiscount = request.getParameter("amountItemDiscount");
		String percent = request.getParameter("percentItemDiscount");
		String cartLineIdx = request.getParameter("cartLineIdx");
		int cartLine = Integer.parseInt(cartLineIdx);
		ShoppingCartItem cartItem = webposSession.getCart().findCartItem(cartLine);
		if (UtilValidate.isNotEmpty(cartItem)) {
			BigDecimal totalItemNoAdj = cartItem.getDisplayItemSubTotalNoAdj();
			boolean discountPercent = Boolean.parseBoolean(percent);
			if (UtilValidate.isNotEmpty(amountItemDiscount) && UtilValidate.isNotEmpty(cartLineIdx)) {
				BigDecimal amount = BigDecimal.ZERO;
				// if cart was discounted, cart must reserve amount that discounted
				// before
				String totalPercent = "100";
				BigDecimal totalPecent = new BigDecimal(totalPercent);
				try {
					amount = new BigDecimal(amountItemDiscount);
					if (!discountPercent) {
						amount = amount.setScale(0, BigDecimal.ROUND_HALF_UP);
					}
				} catch (NumberFormatException e) {
				}
				if ((amount.compareTo(BigDecimal.ZERO) >= 0 && amount.compareTo(totalItemNoAdj) == -1 && !discountPercent)
						|| (discountPercent && amount.compareTo(BigDecimal.ZERO) >= 0
								&& amount.compareTo(totalPecent) == -1)) {
					if (discountPercent) {
						if (amount.compareTo(BigDecimal.ZERO) == 1) {
							amount = amount.movePointLeft(2);
							amount = amount.multiply(totalItemNoAdj);
							amount = amount.setScale(0, BigDecimal.ROUND_HALF_UP);
						}
					}
					amount = amount.negate();
					trans.addDiscountItem(cartLine, amount, false);
					request.setAttribute("itemSelectedDiscount", cartLineIdx);
					trans.calcTax();
				}
			}
		}
		return "success";
	}

	public static String deleteCartItem(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		String cartLineIndex = request.getParameter("cartLineIndex");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		if (UtilValidate.isNotEmpty(webposSession)) {
			ShoppingCart cart = webposSession.getCart();
			if (UtilValidate.isNotEmpty(cart)) {
				int index = Integer.parseInt(cartLineIndex);
				if (UtilValidate.isNotEmpty(index)) {
					ShoppingCartItem cartItem = cart.findCartItem(index);
					String productId = cartItem.getProductId();
					try {
						cart.deleteCartItem(index, dispatcher);
					} catch (CartItemModifyException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotRemoveCartItem",
								UtilMisc.toMap("productId", productId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					// check to remove adjustment with this product
					WebPosTransaction webposTransaction = webposSession.getCurrentTransaction();
					Map<String, Integer> itemDiscounts = webposTransaction.getItemDiscounts();
					if (UtilValidate.isNotEmpty(itemDiscounts)) {
						Integer itemAdj = itemDiscounts.remove(productId);
						if (UtilValidate.isNotEmpty(itemAdj)) {
							cartItem.removeAdjustment(itemAdj.intValue());
						}
					}
					webposTransaction.calcTax();
				}
			}
		} else {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}

	public static String returnCartItem(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = request.getLocale();
		HttpSession session = request.getSession();
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		if (UtilValidate.isNotEmpty(webposSession)) {
			ShoppingCart cart = webposSession.getCart();
			WebPosTransaction webposTransaction = webposSession.getCurrentTransaction();
			if (UtilValidate.isNotEmpty(cart)) {
				String cartLineIndex = request.getParameter("cartLineIndex");
				int cartIndex = Integer.parseInt(cartLineIndex);
				ShoppingCartItem cartItem = null;
				if (UtilValidate.isNotEmpty(cartIndex)) {
					cartItem = cart.findCartItem(cartIndex);
				}
				if (UtilValidate.isNotEmpty(cartItem)) {
					BigDecimal quantity = cartItem.getQuantity();
					quantity = quantity.negate();
					LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
					boolean triggerExternalOps = true;
					boolean resetShipGroup = true;
					boolean updateProductPrice = true;
					try {
						cartItem.setQuantity(quantity, dispatcher, cart, triggerExternalOps, resetShipGroup,
								updateProductPrice);
						webposTransaction.calcTax();
					} catch (CartItemModifyException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotReturnThisItem",
								locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
				} else {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSThisItemDoNotExistInCart",
							locale);
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

	@SuppressWarnings("unchecked")
	public static String holdCart(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		WebPosSession webPosSession = (WebPosSession) session.getAttribute("webPosSession");
		List<WebPosSession> listHoldCart = (List<WebPosSession>) session.getAttribute("webPosSessionHolderList");
		if (UtilValidate.isEmpty(listHoldCart)) {
			listHoldCart = FastList.newInstance();
			session.setAttribute("webPosSessionHolderList", listHoldCart);
		}
		List<ShoppingCartItem> itemsCart = webPosSession.getCart().items();
		if (itemsCart.size() > 0) {
			listHoldCart.add(webPosSession);
		}
		session.setAttribute("webPosSession", null);
		session.setAttribute("shoppingCart", null);
		WebPosEvents.getWebPosSession(request, webPosSession.getId());
		return "success";
	}

	@SuppressWarnings("unchecked")
	public static String loadCart(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		List<WebPosSession> listSessiontHold = (List<WebPosSession>) session.getAttribute("webPosSessionHolderList");
		String transactionId = request.getParameter("transactionId");
		for (WebPosSession sessionHold : listSessiontHold) {
			ShoppingCart cartHold = sessionHold.getCart();
			if (cartHold.getTransactionId().equals(transactionId)) {
				session.setAttribute("webPosSession", sessionHold);
				session.setAttribute("shoppingCart", cartHold);
				listSessiontHold.remove(sessionHold);
			}
		}
		return "success";
	}

	@SuppressWarnings("unchecked")
	public static String removeHoldedCart(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		List<WebPosSession> listSessiontHold = (List<WebPosSession>) session.getAttribute("webPosSessionHolderList");
		String transactionId = request.getParameter("transactionId");
		for (WebPosSession sessionHold : listSessiontHold) {
			ShoppingCart cartHold = sessionHold.getCart();
			if (cartHold.getTransactionId().equals(transactionId)) {
				listSessiontHold.remove(sessionHold);
			}
		}
		return "success";
	}
}
