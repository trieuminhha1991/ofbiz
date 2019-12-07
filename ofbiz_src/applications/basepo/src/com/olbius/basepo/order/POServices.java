package com.olbius.basepo.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityListIterator;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.utils.POUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.security.util.SecurityUtil;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.condition.EntityOperator;

public class POServices {
	public static final String module = POServices.class.getName();
	public static final String resource = "BasePOUiLabels";
	public static final String resource_error = "BasePOErrorUiLabels";

	public static Map<String, Object> getProductBySupplier(DispatchContext dpx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String supplier = (String) context.get("supplier");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String productPlanId = (String) context.get("productPlanId");
		String orderId = (String) context.get("orderId");
		result = getProductBySupplierAndPlan(delegator, supplier, customTimePeriodId, productPlanId, orderId);
		return result;
	}
	
	public static Map<String, Object> getSupplierCurrencyUom(DispatchContext dpx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		List<GenericValue> list = delegator.findList("SupplierProductGroup", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false);
		List<String> listUomIds = new ArrayList<String>();
		listUomIds = EntityUtil.getFieldListFromEntityList(list, "currencyUomId", true);
		List<GenericValue> listUoms = delegator.findList("Uom",
				EntityCondition.makeCondition("uomId", EntityOperator.IN, listUomIds), null, null, null, false);
		result.put("listCurrencyUoms", listUoms);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetSupplierCurrencyUom(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String partyId = SalesUtil.getParameter(parameters, "partyId");
			if (UtilValidate.isNotEmpty(partyId)) {
				List<String> listUomIds = EntityUtil.getFieldListFromEntityList(delegator.findList("SupplierProductGroup", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false), "currencyUomId", true);
				
				listAllConditions.add(EntityCondition.makeCondition("uomId", EntityOperator.IN, listUomIds));
				listIterator = delegator.findList("Uom", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
				successResult.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetSupplierCurrencyUom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> getLastPriceBySupplierProductAndQuantity(DispatchContext dpx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String) context.get("productId");
		String partyId = (String) context.get("partyId");
		String currencyUomId = (String) context.get("currencyUomId");
		String uomId = (String) context.get("uomId");
		String quantityTmp = (String) context.get("quantity");
		BigDecimal quantity = new BigDecimal(quantityTmp);
		BigDecimal lastPrice = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, partyId, currencyUomId, uomId, quantity);
		result.put("lastPrice", lastPrice);
		return result;
	}
	

	public static Map<String, Object> changeOrderStatusCompletePOCustom(DispatchContext dpx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		result.put("userLogin", userLogin);
		return result;
	}

	public static Map<String, Object> checkCreateDropShipPurchaseOrdersPO(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		Security security = ctx.getSecurity();
		// TODO (use the "system" user)
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		Locale locale = (Locale) context.get("locale");
		OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
		// TODO: skip this if there is already a purchase order associated with
		// the sales order (ship group)

		try {
			// if sales order
			if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
				// get the order's ship groups
				for (GenericValue shipGroup : orh.getOrderItemShipGroups()) {
					GenericValue orderHeaderSO = orh.getOrderHeader();
					result.put("statusId", orderHeaderSO.getString("statusId"));
					if (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId"))) {
						// This ship group is a drop shipment: we create a
						// purchase order for it
						String supplierPartyId = shipGroup.getString("supplierPartyId");

						List<GenericValue> listOrderNote = delegator.findList("OrderHeaderNote",
								EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "internalNote", "N")),
								null, null, null, false);
						String orderNote = null;
						if (!UtilValidate.isEmpty(listOrderNote)) {
							String noteId = listOrderNote.get(0).getString("noteId");
							GenericValue noteData = delegator.findOne("NoteData", UtilMisc.toMap("noteId", noteId),
									false);
							orderNote = noteData.getString("noteInfo");
						}

						String currencyUom = orderHeaderSO.getString("currencyUom");
						String salesMethodChannelEnumId = orderHeaderSO.getString("salesMethodChannelEnumId");
						// create the cart
						ShoppingCart cart = new ShoppingCart(delegator, orh.getProductStoreId(), null,
								orh.getCurrency());
						cart.setOrderType("PURCHASE_ORDER");
						cart.setBillToCustomerPartyId(cart.getBillFromVendorPartyId()); // Company
						cart.setBillFromVendorPartyId(supplierPartyId);
						cart.setOrderPartyId(supplierPartyId);
						cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);
						if (orderNote != null) {
							cart.addOrderNote(orderNote);
						}
						// Get the items associated to items and create po
						List<GenericValue> items = orh.getValidOrderItems(shipGroup.getString("shipGroupSeqId"));
						Map<String, Object> contextMap = FastMap.newInstance();
						if (!UtilValidate.isEmpty(items)) {
							int size = items.size();
							int i = 0;
							for (GenericValue item : items) {
								String priceStr = "0";
								String quantityStr = "0";
								try {
									// modify by datnv
									List<GenericValue> listSupp = delegator
											.findList("SupplierProduct",
													EntityCondition.makeCondition(UtilMisc.toMap("partyId",
															supplierPartyId, "currencyUomId", currencyUom, "productId",
															item.getString("productId"), "supplierPrefOrderId",
															"10_MAIN_SUPPL")),
													null, null, null, false);
									listSupp = EntityUtil.filterByCondition(listSupp,
											EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
									if (!UtilValidate.isEmpty(listSupp)) {
										delegator.store(orderHeaderSO);
										GenericValue supp = EntityUtil.getFirst(listSupp);
										BigDecimal lastPrice = supp.getBigDecimal("lastPrice");
										priceStr = (String) ObjectType.simpleTypeConvert(lastPrice, "String", null,
												locale);
										quantityStr = (String) ObjectType.simpleTypeConvert(
												item.getBigDecimal("quantity"), "String", null, locale);
									}
									Timestamp shipByDate = null;
									if (item.get("shipBeforeDate") == null)
										shipByDate = item.getTimestamp("estimatedDeliveryDate");
									else
										shipByDate = item.getTimestamp("shipBeforeDate");
									Timestamp shipAfterDate = null;
									if (item.get("shipAfterDate") == null)
										shipAfterDate = item.getTimestamp("estimatedDeliveryDate");
									else
										shipAfterDate = item.getTimestamp("shipAfterDate");
									// end

									int itemIndex = cart.addOrIncreaseItem(item.getString("productId"), null, // amount
											item.getBigDecimal("quantity"), null, null, null, // reserv
											shipByDate, shipAfterDate, null, null, null, null, null, null, null,
											dispatcher);
									ShoppingCartItem sci = cart.findCartItem(itemIndex);
									sci.setAssociatedOrderId(orderId);
									sci.setAssociatedOrderItemSeqId(item.getString("orderItemSeqId"));
									sci.setOrderItemAssocTypeId("DROP_SHIPMENT");

									// cancel Order inventory reservation
									Map<String, Object> cancelCtx = UtilMisc.<String, Object> toMap("userLogin",
											userLogin, "orderId", orderId);
									cancelCtx.put("orderItemSeqId", item.getString("orderItemSeqId"));
									cancelCtx.put("shipGroupSeqId", shipGroup.getString("shipGroupSeqId"));

									Map<String, Object> cancelResp = null;
									try {
										cancelResp = dispatcher.runSync("cancelOrderInventoryReservation", cancelCtx);
									} catch (GenericServiceException e) {
										Debug.logError(e, module);
										throw new GeneralException(e.getMessage());
									}
									if (ServiceUtil.isError(cancelResp)) {
										throw new GeneralException(ServiceUtil.getErrorMessage(cancelResp));
									}

								} catch (Exception e) {
									return ServiceUtil.returnError(UtilProperties.getMessage(resource,
											"OrderOrderCreatingDropShipmentsError",
											UtilMisc.toMap("orderId", orderId, "errorString", e.getMessage()), locale));
								}

								contextMap.put("update_" + (size - i - 1), quantityStr);
								contextMap.put("price_" + (size - i - 1), priceStr);
								contextMap.put("itemType_" + (size - i - 1), "PRODUCT_ORDER_ITEM");
								i++;
							}
						}

						// If there are indeed items to drop ship, then create
						// the purchase order
						if (!UtilValidate.isEmpty(cart.items())) {
							// set checkout options
							cart.setDefaultCheckoutOptions(dispatcher);
							// the shipping address is the one of the customer
							cart.setAllShippingContactMechId(shipGroup.getString("contactMechId"));
							// associate ship groups of sales and purchase
							// orders
							ShoppingCart.CartShipInfo cartShipInfo = cart.getShipGroups().get(0);
							cartShipInfo.setAssociatedShipGroupSeqId(shipGroup.getString("shipGroupSeqId"));
							// create the order
							CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
							// modify datnv

							contextMap.put("finalizeReqAdditionalParty", false);
							contextMap.put("finalizeReqOptions", false);
							contextMap.put("removeSelected", false);
							contextMap.put("finalizeReqPayInfo", false);
							ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
							helper.modifyCart(security, userLogin, contextMap, false, null, locale);

							coh.calcAndAddTax();
							// end
							Map<String, Object> callResult = coh.createOrderChangeSuppPrice(userLogin, "false");
							String orderIdPO = (String) callResult.get("orderId");
							result.put("orderIdPO", orderIdPO);
						} else {
							// if there are no items to drop ship, then clear
							// out the supplier partyId
							Debug.logWarning(
									"No drop ship items found for order [" + shipGroup.getString("orderId")
											+ "] and ship group [" + shipGroup.getString("shipGroupSeqId")
											+ "] and supplier party [" + shipGroup.getString("supplierPartyId")
											+ "].  Supplier party information will be cleared for this ship group",
									module);
							shipGroup.set("supplierPartyId", null);
							shipGroup.store();
						}
					}
				}
			}
		} catch (Exception exc) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderOrderCreatingDropShipmentsError",
					UtilMisc.toMap("orderId", orderId, "errorString", exc.getMessage()), locale));
		}

		return result;
	}

	public static Map<String, Object> getProductBySupplierAndPlan(Delegator delegator, String supplier,
			String customTimePeriodId, String productPlanId, String orderId) {
		List<GenericValue> listOrderItem = FastList.newInstance();
		try {
			listOrderItem = delegator.findList("OrderItem",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			listOrderItem = EntityUtil.filterByCondition(listOrderItem,
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
		} catch (GenericEntityException e2) {
			Debug.logWarning(e2.getMessage(), "module");
		}
		List<String> listProductId = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listOrderItem)) {
			listProductId = EntityUtil.getFieldListFromEntityList(listOrderItem, "productId", true);
		}

		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listSupplierProduct = new ArrayList<GenericValue>();
		try {
			listSupplierProduct = delegator.findList("SupplierProductAndProductAndUom",
					EntityCondition
							.makeCondition(UtilMisc.toMap("partyId", supplier, "supplierPrefOrderId", "10_MAIN_SUPPL")),
					null, null, null, false);
			listSupplierProduct = EntityUtil.filterByCondition(listSupplierProduct,
					EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
			listSupplierProduct = EntityUtil.filterByCondition(listSupplierProduct,
					EntityCondition.makeCondition("productId", EntityOperator.NOT_IN, listProductId));

		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), "module");
		}
		List<Map<String, Object>> listReturn = FastList.newInstance();
		GenericValue productPlanWeek = null;
		try {
			GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader",
					UtilMisc.toMap("productPlanId", productPlanId), true);
			if (productPlanHeader != null) {
				List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader",
						EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "IMPORT_PLAN",
								"customTimePeriodId", customTimePeriodId, "organizationPartyId",
								productPlanHeader.getString("organizationPartyId"))),
						null, null, null, true);
				if (UtilValidate.isNotEmpty(listProductPlanHeader)) {
					productPlanWeek = EntityUtil.getFirst(listProductPlanHeader);
				}
			}
		} catch (GenericEntityException e1) {
			Debug.logWarning(e1.getMessage(), "module");
		}

		if ("".equals(customTimePeriodId) || customTimePeriodId == null || "".equals(productPlanId)
				|| productPlanId == null) {
			for (GenericValue supp : listSupplierProduct) {
				Map<String, Object> map = FastMap.newInstance();
				map.put("productId", supp.getString("productId"));
				map.put("partyId", supp.getString("partyId"));
				map.put("minimumOrderQuantity", supp.getBigDecimal("minimumOrderQuantity"));
				map.put("quantityUomId", supp.getString("quantityUomId"));
				map.put("lastPrice", supp.getBigDecimal("lastPrice"));
				map.put("currencyUomId", supp.getString("currencyUomId"));
				map.put("productName", supp.getString("productName"));
				map.put("description", supp.getString("description"));
				map.put("quantity", new BigDecimal(0));
				map.put("productCode", supp.getString("productCode"));
				listReturn.add(map);
			}
		} else {
			List<Map<String, Object>> listRe = FastList.newInstance();
			for (GenericValue supp : listSupplierProduct) {
				Map<String, Object> map = FastMap.newInstance();
				map.put("productPlanId", productPlanId);
				map.put("customTimePeriodId", customTimePeriodId);
				map.put("productId", supp.getString("productId"));
				map.put("productCode", supp.getString("productCode"));
				map.put("partyId", supp.getString("partyId"));
				map.put("minimumOrderQuantity", supp.getBigDecimal("minimumOrderQuantity"));
				map.put("quantityUomId", supp.getString("quantityUomId"));
				map.put("lastPrice", supp.getBigDecimal("lastPrice"));
				map.put("currencyUomId", supp.getString("currencyUomId"));
				map.put("productName", supp.getString("productName"));
				map.put("description", supp.getString("description"));
				BigDecimal quantity = new BigDecimal(0);
				BigDecimal planQuantity = new BigDecimal(0);
				BigDecimal orderedQuantity = new BigDecimal(0);
				try {
					if (productPlanWeek != null) {
						GenericValue productPlanItem = delegator.findOne("ProductPlanItem",
								UtilMisc.toMap("productPlanId", productPlanWeek.getString("productPlanId"), "productId",
										supp.getString("productId"), "customTimePeriodId", customTimePeriodId),
								false);
						if (productPlanItem != null) {
							planQuantity = productPlanItem.getBigDecimal("planQuantity");
							if (productPlanItem.get("orderedQuantity") != null) {
								orderedQuantity = productPlanItem.getBigDecimal("orderedQuantity");
							}
							if (planQuantity.compareTo(orderedQuantity) > 0) {
								quantity = planQuantity.subtract(orderedQuantity);
							}
						}
					}
				} catch (GenericEntityException e) {
					Debug.logWarning(e.getMessage(), "module");
				}
				map.put("orderedQuantity", orderedQuantity);
				map.put("planQuantity", planQuantity);
				map.put("quantity", quantity);
				listRe.add(map);
			}
			List<Map<String, Object>> listQ = FastList.newInstance();
			List<Map<String, Object>> listNoQ = FastList.newInstance();
			for (Map<String, Object> list : listRe) {
				BigDecimal quantity = (BigDecimal) list.get("quantity");
				if (quantity.compareTo(new BigDecimal(0)) == 1) {
					listQ.add(list);
				} else {
					listNoQ.add(list);
				}
			}
			if (UtilValidate.isNotEmpty(listQ)) {
				listReturn.addAll(listQ);
			}
			if (UtilValidate.isNotEmpty(listNoQ)) {
				listReturn.addAll(listNoQ);
			}
		}

		GenericValue party = null;
		try {
			party = delegator.findOne("Party", UtilMisc.toMap("partyId", supplier), false);
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), "module");
		}
		String currencyUomId = party.getString("preferredCurrencyUomId");
		result.put("listProductBySupplier", listReturn);
		result.put("currencyUomId", currencyUomId);
		return result;
	}

	public static Map<String, Object> createNewPurchaseOrder(DispatchContext dpx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Security security = dpx.getSecurity();
		String currencyUomId = (String) context.get("currencyUomId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdFrom = (String) context.get("partyIdFrom");
		String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
		String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		String contactMechId = (String) context.get("contactMechId");
		String listOrderItems = (String) context.get("orderItems");
		String originFacilityId = (String) context.get("originFacilityId");
		String shipBeforeDateStr = (String) context.get("shipBeforeDate");
		String shipAfterDateStr = (String) context.get("shipAfterDate");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String productPlanId = (String) context.get("productPlanId");
		long shipBeforeDateLong = Long.parseLong(shipBeforeDateStr);
		long shipAfterDateLong = Long.parseLong(shipAfterDateStr);
		Timestamp shipBeforeDate = new Timestamp(shipBeforeDateLong);
		Timestamp shipAfterDate = new Timestamp(shipAfterDateLong);
		JSONArray arrOrderItems = JSONArray.fromObject(listOrderItems);
		ShoppingCart cart = new ShoppingCart(delegator, null, null, locale, currencyUomId, null, null);

		try {
			cart.setOrderType("PURCHASE_ORDER");
			cart.setShipBeforeDate(shipBeforeDate);
			cart.setShipAfterDate(shipAfterDate);
			cart.setCurrency(dispatcher, currencyUomId);
			cart.setUserLogin(userLogin, dispatcher);
			cart.setLocale(locale);
			cart.setPlacingCustomerPartyId(partyIdTo);
			cart.setBillToCustomerPartyId(partyIdTo);
			cart.setAllShippingContactMechId(contactMechId);
			if (originFacilityId != null && !originFacilityId.equals("")) {
				cart.setFacilityId(originFacilityId);
				cart.setShipGroupFacilityId(0, originFacilityId);
			}
			cart.setBillFromVendorPartyId(partyIdFrom);
			cart.setAllShipmentMethodTypeId("NO_SHIPPING");
			cart.setShipFromVendorPartyId(partyIdFrom);
			cart.setShipToCustomerPartyId(partyIdTo);
			if ((String) cart.getAttribute("supplierPartyId") == null) {
				cart.setAttribute("supplierPartyId", partyIdFrom);
			}
			cart.setOrderPartyId(partyIdFrom);
			cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);

		} catch (CartItemModifyException e) {

		}
		ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
		int size = arrOrderItems.size();
		Map<String, Object> contextMap = FastMap.newInstance();
		for (int i = 0; i < size; i++) {
			JSONObject orderItem = arrOrderItems.getJSONObject(i);
			String productId = (String) orderItem.get("productId");
			Integer quantityInt = (Integer) orderItem.get("quantity");
			String priceStr = orderItem.getString("lastPrice");
			BigDecimal price = new BigDecimal(0);
			BigDecimal amount = new BigDecimal(0);
			price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
			amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal quantity = new BigDecimal(quantityInt);
			if (!"".equals(customTimePeriodId) && !"".equals(productPlanId) && productId != null
					&& customTimePeriodId != null) {
				GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader",
						UtilMisc.toMap("productPlanId", productPlanId), false);
				List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader",
						EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "IMPORT_PLAN",
								"customTimePeriodId", customTimePeriodId, "organizationPartyId",
								productPlanHeader.getString("organizationPartyId"))),
						null, null, null, true);
				if (UtilValidate.isNotEmpty(listProductPlanHeader)) {
					GenericValue productPlanHeaderGe = EntityUtil.getFirst(listProductPlanHeader);
					GenericValue productPlanItem = delegator.findOne("ProductPlanItem",
							UtilMisc.toMap("productPlanId", productPlanHeaderGe.getString("productPlanId"), "productId",
									productId, "customTimePeriodId", customTimePeriodId),
							false);
					if (productPlanItem != null) {
						BigDecimal orderedQuantity = new BigDecimal(0);
						if (productPlanItem.get("orderedQuantity") != null) {
							orderedQuantity = productPlanItem.getBigDecimal("orderedQuantity");
						}
						productPlanItem.put("orderedQuantity", quantity.add(orderedQuantity));
						delegator.store(productPlanItem);
					}
				}
			}

			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			String productCategoryId = null;
			String catalogId = null;
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("-fromDate");
			if ((String) product.get("primaryProductCategoryId") != null) {
				productCategoryId = (String) product.get("primaryProductCategoryId");
			} else {
				List<GenericValue> listCategoryByProducts = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, orderBy, null,
						false);
				listCategoryByProducts = EntityUtil.filterByDate(listCategoryByProducts);
				if (!listCategoryByProducts.isEmpty()) {
					productCategoryId = (String) EntityUtil.getFirst(listCategoryByProducts).get("productCategoryId");
				}
			}
			List<GenericValue> listCatalogCategorys = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)), null,
					orderBy, null, false);
			listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
			if (!listCatalogCategorys.isEmpty()) {
				catalogId = (String) EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
			} else {
				String productCategoryIdTmp = productCategoryId;
				while (listCatalogCategorys.isEmpty()) {
					listCatalogCategorys = new ArrayList<GenericValue>();
					List<GenericValue> listCategoryParents = delegator.findList("ProductCategoryRollup",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)),
							null, orderBy, null, false);
					listCategoryParents = EntityUtil.filterByDate(listCategoryParents);
					if (!listCategoryParents.isEmpty()) {
						productCategoryIdTmp = (String) EntityUtil.getFirst(listCategoryParents)
								.get("parentProductCategoryId");
						listCatalogCategorys = delegator.findList("ProdCatalogCategory",
								EntityCondition
										.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)),
								null, orderBy, null, false);
						listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
					} else {
						break;
					}
				}
			}
			if (!listCatalogCategorys.isEmpty()) {
				catalogId = (String) EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
			}
			helper.addToCart(catalogId, null, null, productId, productCategoryId, "PRODUCT_ORDER_ITEM", null, null,
					null, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context,
					null);

			contextMap.put("update_" + (size - i - 1), quantity.toString());
			contextMap.put("price_" + (size - i - 1), priceStr);
			contextMap.put("itemType_" + (size - i - 1), "PRODUCT_ORDER_ITEM");
		}
		contextMap.put("finalizeReqAdditionalParty", false);
		contextMap.put("finalizeReqOptions", false);
		contextMap.put("removeSelected", false);
		contextMap.put("finalizeReqPayInfo", false);
		helper.modifyCart(security, userLogin, contextMap, false, null, locale);

		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
		checkOutHelper.finalizeOrderEntryShip(0, contactMechId, partyIdFrom);
		checkOutHelper.finalizeOrderEntryOptions(0, "NO_SHIPPING@_NA_", null, "false", null, "false", null, null, null,
				null, null);
		try {
			checkOutHelper.calcAndAddTax();
		} catch (GeneralException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);

		Map<String, Object> callResult = checkOutHelper.createOrder(userLogin, null, null, null, areOrderItemsExploded,
				null, null);
		String orderId = (String) callResult.get("orderId");
		Map<String, Object> returnResult = ServiceUtil.returnSuccess();
		returnResult.put("orderId", orderId);
		cart.clear();
		return returnResult;
	}

	public static Map<String, Object> initPurchaseOrderService(DispatchContext dpx,
			Map<String, ? extends Object> context) throws GeneralException {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> returnResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
//		Security security = dpx.getSecurity();
		String currencyUomId = (String) context.get("currencyUomId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdFrom = (String) context.get("partyIdFrom");
		String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
		String partyIdTo = null;
		if (UtilValidate.isNotEmpty(context.get("partyIdTo"))) {
			partyIdTo = (String)context.get("partyIdTo");
		} else {
			partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		}
		String contactMechId = (String) context.get("contactMechId");
		String listOrderItems = (String) context.get("orderItems");
		String originFacilityId = (String) context.get("originFacilityId");
		String shipBeforeDateStr = (String) context.get("shipBeforeDate");
		String shipAfterDateStr = (String) context.get("shipAfterDate");
//		String customTimePeriodId = (String) context.get("customTimePeriodId");
//		String productPlanId = (String) context.get("productPlanId");
		long shipBeforeDateLong = Long.parseLong(shipBeforeDateStr);
		long shipAfterDateLong = Long.parseLong(shipAfterDateStr);
		Timestamp shipBeforeDate = new Timestamp(shipBeforeDateLong);
		Timestamp shipAfterDate = new Timestamp(shipAfterDateLong);
		JSONArray arrOrderItems = JSONArray.fromObject(listOrderItems);
		ShoppingCart cart = new ShoppingCart(delegator, null, null, locale, currencyUomId, null, null);

		try {
			cart.setOrderType("PURCHASE_ORDER");
			cart.setShipBeforeDate(shipBeforeDate);
			cart.setShipAfterDate(shipAfterDate);
			cart.setDefaultShipBeforeDate(shipBeforeDate);
			cart.setDefaultShipAfterDate(shipAfterDate);
			cart.setCurrency(dispatcher, currencyUomId);
			cart.setLocale(locale);
			cart.setPlacingCustomerPartyId(partyIdTo);
			cart.setBillToCustomerPartyId(partyIdTo);
			cart.setAllShippingContactMechId(contactMechId);
			if (originFacilityId != null && !originFacilityId.equals("")) {
				cart.setFacilityId(originFacilityId);
				cart.setShipGroupFacilityId(0, originFacilityId);
			}
			cart.setBillFromVendorPartyId(partyIdFrom);
			cart.setAllShipmentMethodTypeId("NO_SHIPPING");
			cart.setShipFromVendorPartyId(partyIdFrom);
			cart.setShipToCustomerPartyId(partyIdTo);
			if ((String) cart.getAttribute("supplierPartyId") == null) {
				cart.setAttribute("supplierPartyId", partyIdFrom);
			}
			cart.setOrderPartyId(partyIdFrom);
			cart.setAttribute("salesMethodChannelEnumId", salesMethodChannelEnumId);

		} catch (CartItemModifyException e) {

		}

		ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
		int size = arrOrderItems.size();
//		Map<String, Object> contextMap = FastMap.newInstance();
		for (int i = 0; i < size; i++) {
			JSONObject orderItem = arrOrderItems.getJSONObject(i);
			String productId = (String) orderItem.get("productId");
			BigDecimal quantity = BigDecimal.ZERO;
			if (orderItem.get("quantity") instanceof String) {
				String quantityStr = (String) orderItem.get("quantity");
				quantity = new BigDecimal(quantityStr);
			} else if (orderItem.get("quantity") instanceof Integer) {
				Integer quantityStr = (Integer) orderItem.get("quantity");
				quantity = new BigDecimal(quantityStr);
			} else if (orderItem.get("quantity") instanceof BigDecimal) {
				quantity = (BigDecimal)orderItem.get("quantity");
			}
			
			String priceStr = orderItem.getString("lastPrice");
			String quantityUomId = null;
			String weightUomId = null;
			if (orderItem.containsKey("quantityUomId")){
				quantityUomId = orderItem.getString("quantityUomId");
			}
			if (orderItem.containsKey("weightUomId")){
				weightUomId = orderItem.getString("weightUomId");
			}
			// item comment
			String itemComment = null;
			if (orderItem.containsKey("itemComment")){
				itemComment = orderItem.getString("itemComment");
			}
			Map<String, Object> paramMap = FastMap.newInstance();
			paramMap.putAll(context);
			if (UtilValidate.isNotEmpty(quantityUomId)){
				paramMap.put("quantityUomId", quantityUomId);
			}
			if (UtilValidate.isNotEmpty(weightUomId)){
				paramMap.put("weightUomId", weightUomId);
			}
			if (UtilValidate.isNotEmpty(itemComment)){
				paramMap.put("itemComment", itemComment);
			}
			/*if (!"".equals(customTimePeriodId) && !"".equals(productPlanId) && productId != null
					&& customTimePeriodId != null) {
				GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader",
						UtilMisc.toMap("productPlanId", productPlanId), false);
				List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader",
						EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "IMPORT_PLAN",
								"customTimePeriodId", customTimePeriodId, "organizationPartyId",
								productPlanHeader.getString("organizationPartyId"))),
						null, null, null, true);
				if (UtilValidate.isNotEmpty(listProductPlanHeader)) {
					GenericValue productPlanHeaderGe = EntityUtil.getFirst(listProductPlanHeader);
					GenericValue productPlanItem = delegator.findOne("ProductPlanItem",
							UtilMisc.toMap("productPlanId", productPlanHeaderGe.getString("productPlanId"), "productId",
									productId, "customTimePeriodId", customTimePeriodId),
							false);
					if (productPlanItem != null) {
						BigDecimal orderedQuantity = new BigDecimal(0);
						if (productPlanItem.get("orderedQuantity") != null) {
							orderedQuantity = productPlanItem.getBigDecimal("orderedQuantity");
						}
						productPlanItem.put("orderedQuantity", quantity.add(orderedQuantity));
						delegator.store(productPlanItem);
					}
				}
			}*/

			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			String productCategoryId = null;
			String catalogId = null;
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("-fromDate");
			if ((String) product.get("primaryProductCategoryId") != null) {
				productCategoryId = (String) product.get("primaryProductCategoryId");
			} else {
				List<GenericValue> listCategoryByProducts = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, orderBy, null,
						false);
				listCategoryByProducts = EntityUtil.filterByDate(listCategoryByProducts);
				if (!listCategoryByProducts.isEmpty()) {
					productCategoryId = (String) EntityUtil.getFirst(listCategoryByProducts).get("productCategoryId");
				}
			}

			List<GenericValue> listCatalogCategorys = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)), null,
					orderBy, null, false);
			listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
			if (!listCatalogCategorys.isEmpty()) {
				catalogId = (String) EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
			} else {
				String productCategoryIdTmp = productCategoryId;
				while (listCatalogCategorys.isEmpty()) {
					listCatalogCategorys = new ArrayList<GenericValue>();
					List<GenericValue> listCategoryParents = delegator.findList("ProductCategoryRollup",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)),
							null, orderBy, null, false);
					listCategoryParents = EntityUtil.filterByDate(listCategoryParents);
					if (!listCategoryParents.isEmpty()) {
						productCategoryIdTmp = (String) EntityUtil.getFirst(listCategoryParents)
								.get("parentProductCategoryId");
						listCatalogCategorys = delegator.findList("ProdCatalogCategory",
								EntityCondition
										.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)),
								null, orderBy, null, false);
						listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
					} else {
						break;
					}
				}
			}

			if (!listCatalogCategorys.isEmpty()) {
				catalogId = (String) EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
			}
	        ProductConfigWrapper configWrapper = null;
//	        if ("AGGREGATED".equals(product.getString("productTypeId"))){
//	        	try {
//					configWrapper = new ProductConfigWrapper(delegator, dispatcher,
//					        productId, cart.getProductStoreId(), catalogId, cart.getWebSiteId(),
//					        currencyUomId, (Locale)context.get("locale"), userLogin);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//	        }
	        BigDecimal price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
	        BigDecimal amount = null;
	        
	        String weightUomIdBase = product.getString("weightUomId");
	        if (UtilValidate.isNotEmpty(weightUomId)) {
        		BigDecimal productWeight = BigDecimal.ONE;
	        	amount = productWeight.multiply(quantity);
	        	GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", weightUomId, "uomIdTo", weightUomIdBase));
	        	if (UtilValidate.isNotEmpty(conversion)) {
	        		amount = amount.multiply(conversion.getBigDecimal("conversionFactor")); 
				} else {
					conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", weightUomIdBase, "uomIdTo", weightUomId));
		        	if (UtilValidate.isNotEmpty(conversion)) {
		        		amount = amount.multiply(conversion.getBigDecimal("conversionFactor")); 
		        	}
				}
			}
			helper.addToCart(catalogId, null, null, productId, productCategoryId, "PRODUCT_ORDER_ITEM", itemComment, price,
					amount, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, configWrapper, null, paramMap,
					null, Boolean.FALSE);
			
//			contextMap.put("update_" + (size - i - 1), quantity.toString());
//			contextMap.put("price_" + (size - i - 1), priceStr);
//			contextMap.put("itemType_" + (size - i - 1), "PRODUCT_ORDER_ITEM");
		}
//		contextMap.put("finalizeReqAdditionalParty", false);
//		contextMap.put("finalizeReqOptions", false);
//		contextMap.put("removeSelected", false);
//		contextMap.put("finalizeReqPayInfo", false);
//		helper.modifyCart(security, userLogin, contextMap, false, null, locale, false);

		ProductPromoWorker.doPromotions(cart, dispatcher);
		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
		checkOutHelper.finalizeOrderEntryShip(0, contactMechId, partyIdFrom);
		checkOutHelper.finalizeOrderEntryOptions(0, "NO_SHIPPING@_NA_", null, "false", null, "false", null, null, null,
				null, null);
		try {
			checkOutHelper.calcAndAddTax();
		} catch (GeneralException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		returnResult.put("shoppingCart", cart);
		return returnResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPOOrder(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyRoleId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyRoleId", partyRoleId)));
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		String agreementId = null;
		if (parameters.containsKey("agreementId") && parameters.get("agreementId").length > 0) {
			agreementId = parameters.get("agreementId")[0];
		}
		
		String partySupplierId = null;
		if (parameters.containsKey("partySupplierId") && parameters.get("partySupplierId").length > 0) {
			partySupplierId = parameters.get("partySupplierId")[0];
			if (UtilValidate.isNotEmpty(partySupplierId)) {
				listAllConditions.add(EntityCondition.makeCondition("partySupplierId", partySupplierId));
			}
		}
		String filterStatusId = null;
		if (parameters.containsKey("filterStatusId") && parameters.get("filterStatusId").length > 0) {
			filterStatusId = parameters.get("filterStatusId")[0];
			if (UtilValidate.isNotEmpty(filterStatusId)) {
				listAllConditions.add(EntityCondition.makeCondition("statusId", filterStatusId));
			}
		}
		String currencyUomId = null;
		if (parameters.containsKey("currencyUomId") && parameters.get("currencyUomId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("currencyUomId"))) {
				currencyUomId = parameters.get("currencyUomId")[0];
				if (UtilValidate.isNotEmpty(currencyUomId)) {
					listAllConditions.add(EntityCondition.makeCondition("currencyUom", currencyUomId));
				}
			}
		}
		List<GenericValue> listOrders = FastList.newInstance();
		if (UtilValidate.isNotEmpty(agreementId)) {
			List<GenericValue> listAgreementAndOrder = FastList.newInstance();
			try {
				listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition("agreementId", agreementId), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList AgreementAndOrder: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			List<String> orderIds = EntityUtil.getFieldListFromEntityList(listAgreementAndOrder, "orderId", true);
			if (!orderIds.isEmpty()) {
				listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
			} else {
				successResult.put("listIterator", listOrders);
				return successResult;
			}
		}
		try {
			listSortFields.add("orderDate DESC");
			listAllConditions.add(EntityCondition.makeCondition("orderTypeId", "PURCHASE_ORDER"));
			listOrders = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "OrderAndSupplier", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listOrders);
		return successResult;
	}

	// service for jmeter by dunglv
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPOOrderAndReturn(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyRoleId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyRoleId", partyRoleId)));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("statusId", "ORDER_COMPLETED")));
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("returnId", null)));
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listSortFields.add("orderDate DESC");
			listIterator = delegator.find("OrderAndSupplierAndReturn", EntityCondition.makeCondition(listAllConditions),
					null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListPOOrderAndReturn service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}
	// end dunglv

	public static Map<String, Object> changeOrderStatusPOCustom(DispatchContext dpx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Security security = dpx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"PURCHASEORDER_EDIT");
		if (!hasPermission) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}
		Delegator delegator = dpx.getDelegator();
		String orderId = (String)context.get("orderId");
		String statusId = (String)context.get("statusId");
		
		if ("ORDER_CANCELLED".equals(statusId)){
			GenericValue objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
			if (UtilValidate.isNotEmpty(objOrderHeader)) {
				Boolean checkReceived = false;
				Map<String, Object> map = FastMap.newInstance();
				LocalDispatcher dispatcher = dpx.getDispatcher();
				map.put("orderId", orderId);
				map.put("userLogin", userLogin);
				try {
					Map<String, Object> mapCheck = dispatcher.runSync("checkOrderReceived", map);
					checkReceived = (Boolean)mapCheck.get("isReceived");
					if (checkReceived){
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BPSomeProductHasBeenRecived", locale));
					}
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: checkOrderReceived error! " + e.toString());
				}
			}
		}
		result.put("userLogin", userLogin);
		result.put("oldUserLogin", userLogin);
		return result;
	}

	public static Map<String, Object> updateOrderItemShipGroupPO(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String) context.get("orderId");
		Long shipByDateLong = (Long) context.get("shipByDate");
		Timestamp shipByDate = new Timestamp(shipByDateLong);
		Long shipAfterDateLong = (Long) context.get("shipAfterDate");
		Timestamp shipAfterDate = new Timestamp(shipAfterDateLong);
		String shipGroupSeqId = null;
		try {
			List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if (UtilValidate.isNotEmpty(listOrderItemShipGroup)) {
				GenericValue orderItemShipGroup = EntityUtil.getFirst(listOrderItemShipGroup);
				shipGroupSeqId = orderItemShipGroup.getString("shipGroupSeqId");
				try {
					dispatcher.runSync("updateOrderItemShipGroup",
							UtilMisc.<String, Object> toMap("orderId", orderId, "shipGroupSeqId", shipGroupSeqId,
									"userLogin", userLogin, "shipByDate", shipByDate, "shipAfterDate", shipAfterDate));
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError(e.getMessage());
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOrderItemByOrderId(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listItems = new ArrayList<GenericValue>();
		try {
			String orderId = null;
			if (parameters.containsKey("orderId") && parameters.get("orderId").length > 0) {
				orderId = parameters.get("orderId")[0];
			}
			listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			listAllConditions
					.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
			listSortFields.add("orderItemSeqId ASC");
			listIterator = delegator.find("OrderItemAndProduct", EntityCondition.makeCondition(listAllConditions), null,
					null, listSortFields, opts);
			listItems = listIterator.getCompleteList();
			listIterator.close();
			if (!listItems.isEmpty()) {
				for (GenericValue item : listItems) {
					String qtyUomId = getQuantityUomBySupplier(delegator, item.getString("productId"), orderId);
					item.put("quantityUomId", qtyUomId);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listItems);
		return result;
	}

	public static String getQuantityUomBySupplier(Delegator delegator, String productId, String orderId) {
		String quantityUomId = null;
		try {
			List<GenericValue> orderRole = delegator.findList("OrderRole",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")),
					null, null, null, false);
			if (!orderRole.isEmpty()) {
				String partyId = (String) orderRole.get(0).get("partyId");
				List<GenericValue> listSuppliers = delegator.findList("SupplierProduct",
						EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "productId", productId)), null,
						null, null, false);
				listSuppliers = EntityUtil.filterByDate(listSuppliers, null, "availableFromDate", "vailableThruDate",
						false);
				if (!listSuppliers.isEmpty()) {
					quantityUomId = (String) listSuppliers.get(0).get("quantityUomId");
				} else {
					ServiceUtil.returnError("Supplier not found!");
				}
			} else {
				ServiceUtil.returnError("OrderRole not found!");
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), "module");
		}

		return quantityUomId;
	}

	public static Map<String, Object> getOrderAndCTMAndShip(DispatchContext dpx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String orderId = (String) context.get("orderId");
		try {
			List<GenericValue> listOrderHeader = delegator.findList("OrderAndShipAndContact",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if (!UtilValidate.isEmpty(listOrderHeader)) {
				GenericValue order = EntityUtil.getFirst(listOrderHeader);
				result.put("mapOrder", order);
				String name = order.getString("toName") + "(" + order.getString("attnName") + ")";
				result.put("contact", name);
			}

		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListImagesByOrderId(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String orderId = (String) parameters.get("orderId")[0];
		listAllConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		try {
			listIterator = delegator.find("OrderPathFile", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPurchaseOrderService(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
		try {
			listIterator = delegator.find("OrderHeader", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> createMultiImagesForOrder(DispatchContext ctx, Map<String, ?> context) {
		Map<String, Object> result = FastMap.newInstance();
		ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
		String fileName = (String) context.get("_uploadedFile_fileName");
		String mimeType = (String) context.get("_uploadedFile_contentType");
		String folder = (String) context.get("folder");
		String orderId = (String) context.get("orderId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> map = UtilMisc.toMap("userLogin", userLogin, "uploadedFile", fileBytes, "folder", folder,
				"_uploadedFile_fileName", fileName, "_uploadedFile_contentType", mimeType);
		Map<String, Object> resultMap;
		try {
			resultMap = dispatcher.runSync("jackrabbitUploadFile", map);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		String path = (String) resultMap.get("path");
		result.put("path", path);
		Delegator dl = ctx.getDelegator();
		GenericValue orderFilePath = dl.makeValue("OrderPathFile");
		Timestamp cur = new Timestamp(System.currentTimeMillis());
		String orderPathFileId = dl.getNextSeqId("OrderPathFile");
		orderFilePath.put("orderPathFileId", orderPathFileId);
		orderFilePath.put("orderId", orderId);
		orderFilePath.put("path", path);
		orderFilePath.put("createByDate", cur);

		try {
			dl.create(orderFilePath);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPOBySupplier(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listOrders = FastList.newInstance();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		String supplierId = null;
		if (parameters.containsKey("supplierId") && parameters.get("supplierId").length > 0) {
			supplierId = parameters.get("supplierId")[0];
		}

		String returnId = null;
		if (parameters.containsKey("returnId") && parameters.get("returnId").length > 0) {
			returnId = parameters.get("returnId")[0];
		}

		String currencyUomId = null;
        if (parameters.containsKey("currencyUomId") && parameters.get("currencyUomId").length > 0) {
            currencyUomId = parameters.get("currencyUomId")[0];
        }

		GenericValue returnHeader = null;
		if (UtilValidate.isNotEmpty(returnId)) {
			returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
		}

		if (UtilValidate.isNotEmpty(supplierId)) {
			listAllConditions.add(EntityCondition.makeCondition("sellerId", supplierId));
		} else {
			Set<String> listSup = FastSet.newInstance();
			List<GenericValue> listSupplierParty = delegator.findList("ListPartySupplierByRole", null, null, null, null,
					false);
			for (GenericValue sup : listSupplierParty) {
				listSup.add(sup.getString("partyId"));
			}
			listAllConditions.add(EntityCondition.makeCondition("sellerId", EntityOperator.IN, listSup));
		}
		try {
			if (listSortFields.isEmpty()) {
				listSortFields = UtilMisc.toList("-orderDate");
			}
			String fromPartyId = null;
			if (UtilValidate.isNotEmpty(returnHeader)) {
				fromPartyId = returnHeader.getString("fromPartyId");
			} else {
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				fromPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			}
			 
			listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, fromPartyId));
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
			if(UtilValidate.isNotEmpty(currencyUomId))
			    listAllConditions.add(EntityCondition.makeCondition("currencyUom", currencyUomId));
			listIterator = delegator.find("PruchaseOrderHeaderFullView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			
			listOrders = POUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listOrders);
		return successResult;
	}

	public static Map<String, Object> getIncomingPurchaseOrders(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String supplierId = (String) context.get("supplierId");

		List<EntityCondition> listCondOrder = FastList.newInstance();
		listCondOrder.add(EntityCondition.makeCondition("roleTypeId", "BILL_FROM_VENDOR"));
		listCondOrder.add(EntityCondition.makeCondition("statusId", "ORDER_COMPLETED"));

		if (UtilValidate.isNotEmpty(supplierId)) {
			listCondOrder.add(EntityCondition.makeCondition("partyId", supplierId));
		} else {
			Set<String> listSup = FastSet.newInstance();
			List<GenericValue> listSupplierParty = delegator.findList("ListPartySupplierByRole", null, null, null, null,
					false);
			for (GenericValue sup : listSupplierParty) {
				listSup.add(sup.getString("partyId"));
			}
			listCondOrder.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listSup));
		}
		try {
			List<GenericValue> listOrder = delegator.findList("OrderHeaderAndRoles",
					EntityCondition.makeCondition(listCondOrder), null, UtilMisc.toList("-orderDate"), null, false);
			result.put("listOrders", listOrder);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getIncomingPurchaseOrders service: " + e.toString();
			return ServiceUtil.returnError(errMsg);
		}
		return result;
	}

	public static Map<String, Object> getOrderItemsByOrdersToReturn(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			String listOrderIdTmps = (String) context.get("listOrderIds");
			JSONArray listItemTmp = JSONArray.fromObject(listOrderIdTmps);
			List<String> listOrderIds = new ArrayList<String>();
			for (int j = 0; j < listItemTmp.size(); j++) {
				JSONObject obj = listItemTmp.getJSONObject(j);
				String orderId = obj.getString("orderId");
				listOrderIds.add(orderId);
			}
			List<Map<String, Object>> listOrderItems = new ArrayList<Map<String, Object>>();
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_COMPLETED"));
			conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, listOrderIds));
			List<GenericValue> listOrderItemTmps = delegator.findList("OrderItem", EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue orderItem : listOrderItemTmps) {
				String productId = orderItem.getString("productId");
				GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				String requireAmount = objProduct.getString("requireAmount");
				Map<String, Object> serviceResult = FastMap.newInstance();
				BigDecimal returnableQuantity = BigDecimal.ZERO;
				Map<String, Object> map = FastMap.newInstance();
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					serviceResult = dispatcher.runSync("getReturnableAmount", UtilMisc.toMap("orderItem", orderItem, "userLogin", (GenericValue) context.get("userLogin")));
					returnableQuantity = (BigDecimal) serviceResult.get("returnableAmount");
					map.put("unitPrice", (BigDecimal) serviceResult.get("returnablePrice"));
					map.put("orderedQuantity", orderItem.getBigDecimal("selectedAmount"));
				} else {
					serviceResult = dispatcher.runSync("getReturnableQuantity", UtilMisc.toMap("orderItem", orderItem, "userLogin", (GenericValue) context.get("userLogin")));
					returnableQuantity = (BigDecimal) serviceResult.get("returnableQuantity");
					map.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
					map.put("orderedQuantity", orderItem.getBigDecimal("quantity"));
				}
				map.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
				map.put("orderId", orderItem.getString("orderId"));
				map.put("quantityUomId", objProduct.getString("quantityUomId"));
				map.put("weightUomId", objProduct.getString("weightUomId"));
				map.put("productId", productId);
				map.put("requireAmount", requireAmount);
				map.put("productCode", objProduct.getString("productCode"));
				map.put("itemDescription", orderItem.getString("itemDescription"));
				if (UtilValidate.isEmpty(returnableQuantity) || returnableQuantity.compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				map.put("returnableQuantity", returnableQuantity);
				map.put("quantity", returnableQuantity);
				listOrderItems.add(map);
			}
			result.put("listOrderItems", listOrderItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    public static Map<String, Object> getOrderPromoItemsByOrdersToReturn(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            String listOrderIdTmps = (String) context.get("listOrderIds");
            JSONArray listItemTmp = JSONArray.fromObject(listOrderIdTmps);
            List<String> listOrderIds = new ArrayList<>();
            for (int j = 0; j < listItemTmp.size(); j++) {
                JSONObject obj = listItemTmp.getJSONObject(j);
                String orderId = obj.getString("orderId");
                listOrderIds.add(orderId);
            }
            List<Map<String, Object>> listOrderItems = FastList.newInstance();
            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, listOrderIds));
            conditions.add(EntityCondition.makeCondition("orderItemSeqId", "_NA_"));
            List<GenericValue> listOrderAdjustmentPromo = delegator.findList("OrderAdjustmentPromoView", EntityCondition.makeCondition(conditions), null, null, null, false);
            for (GenericValue orderItem : listOrderAdjustmentPromo) {
                String productPromoId = orderItem.getString("productPromoId");
                Map<String, Object> map = FastMap.newInstance();
                Map<String, Object> serviceResult = dispatcher.runSync("getReturnedAmountPromo", UtilMisc.toMap("promoItem", orderItem, "userLogin", context.get("userLogin")));
                BigDecimal returnedAmount = (BigDecimal) serviceResult.get("returnedAmount");
                BigDecimal orderedPromoAmount = orderItem.getBigDecimal("amount");
                BigDecimal returnableAmount = orderedPromoAmount.subtract(returnedAmount);
                map.put("orderId", orderItem.getString("orderId"));
                map.put("orderAdjustmentId", orderItem.getString("orderAdjustmentId"));
                map.put("orderAdjustmentTypeId", orderItem.getString("orderAdjustmentTypeId"));
                map.put("productPromoId", productPromoId);
                map.put("productPromoName", orderItem.getString("productPromoName"));
                map.put("productPromoRuleId", orderItem.getString("productPromoRuleId"));
                map.put("productPromoActionSeqId", orderItem.getString("productPromoActionSeqId"));
                if (UtilValidate.isEmpty(returnableAmount) || returnableAmount.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                map.put("orderedPromoAmount", orderedPromoAmount);
                map.put("returnableAmount", returnableAmount);
                map.put("amount", returnableAmount);
                listOrderItems.add(map);
            }
            result.put("listOrderItems", listOrderItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<GenericValue> getOrderItemsOrh(String shipGroupSeqId, OrderReadHelper orh) {
        if (shipGroupSeqId == null) return orh.getValidOrderItems();
        List<EntityExpr> exprs = UtilMisc.toList(
//                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"),
                EntityCondition.makeCondition("shipGroupSeqId", EntityOperator.EQUALS, shipGroupSeqId));
        return EntityUtil.filterByAnd(orh.getOrderItemAndShipGroupAssoc(), exprs);
    }
	public static Map<String, Object> checkUpdateDropShipPurchaseOrdersItemPO(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// TODO (use the "system" user)
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		Locale locale = (Locale) context.get("locale");
		String changeSO = (String) context.get("changeSO");
		OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
		String toOrderId = null;
		try {
			List<GenericValue> listOrderItemAss = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemAssocTypeId", "DROP_SHIPMENT")), null, null, null, false);
			List<GenericValue> listOrderItemAssPO = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("toOrderId", orderId, "orderItemAssocTypeId", "DROP_SHIPMENT")), null, null, null, false);
			if (UtilValidate.isNotEmpty(listOrderItemAss) || UtilValidate.isNotEmpty(listOrderItemAssPO)) {
				// notify
				if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
					GenericValue orderItemAss = EntityUtil.getFirst(listOrderItemAss);
					toOrderId = orderItemAss.getString("toOrderId");
					List<String> listParty = new ArrayList<String>();
					List<String> listPartyGroups = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator, "RECEIVE_MSG_FAVOR_DELIVERY", userLogin);
					if (!listPartyGroups.isEmpty()) {
						for (String group : listPartyGroups) {
							try {
								List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", group, "roleTypeIdFrom", "PO_MANAGER")), null, null, null, false);
								listManagers = EntityUtil.filterByDate(listManagers);
								if (!listManagers.isEmpty()) {
									for (GenericValue manager : listManagers) {
										listParty.add(manager.getString("partyIdFrom"));
									}
								}
							} catch (GenericEntityException e) {
								String errMsg = "Fatal error when get party relationship: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError("Get Party relationship error!");
							}
						}
					}
					if (!listParty.isEmpty()) {
						for (String managerParty : listParty) {
							String sendMessage = UtilProperties.getMessage("POUiLabels", "SOnotifyPOUpdate", locale) + " SO(" + orderId + ")" + " PO(" + toOrderId + ")";
							String targetLink = "orderId=" + toOrderId;
							String sendToPartyId = managerParty;
							Map<String, Object> mapContext = new HashMap<String, Object>();
							mapContext.put("partyId", sendToPartyId);
							mapContext.put("targetLink", targetLink);
							mapContext.put("action", "viewDetailPO");
							mapContext.put("header", sendMessage);
							mapContext.put("userLogin", userLogin);
							mapContext.put("ntfType", "ONE");
							try {
								dispatcher.runSync("createNotification", mapContext);
							} catch (GenericServiceException e) {
								String errMsg = "Fatal error when create notification: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError("sent notify error!");
							}
						}
					}
				}

				try {
					// if sales order
					if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
						// get the order's ship groups
						for (GenericValue shipGroup : orh.getOrderItemShipGroups()) {
							if (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId"))) {
								List<GenericValue> items = getOrderItemsOrh(shipGroup.getString("shipGroupSeqId"), orh);
								if (!UtilValidate.isEmpty(items)) {
									for (GenericValue item : items) {
										String orderItemSeqId = item.getString("orderItemSeqId");
										String shipGroupSeqId = item.getString("shipGroupSeqId");
										String productId = item.getString("productId");
										GenericValue productItem = null;
										try {
											productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
											if (productItem == null) {
												continue;
											}
										} catch (Exception e) {
											Debug.logWarning(e, "Problems [product not exists] get productId = " + productId, module);
										}

										/*// TODOCHANGE add sample product by item
										// in requirement assoc
										BigDecimal orderItemQuantityNew = item.getBigDecimal("orderItemQuantity");
										List<GenericValue> reqAssocItems = delegator.findByAnd("OrderReqAssocReqSampleAppl", UtilMisc.toMap("orderId", item.get("orderId"), "orderItemSeqId", item.get("orderItemSeqId")), null, false);
										if (UtilValidate.isNotEmpty(reqAssocItems)) {
											for (GenericValue reqAssocItem : reqAssocItems) {
												BigDecimal quantityItemNew = reqAssocItem.getBigDecimal("quantitySample");
												if (quantityItemNew != null) orderItemQuantityNew = orderItemQuantityNew.add(quantityItemNew);
											}
										}
										// end new
									 	*/
										BigDecimal orderItemQuantityNew = item.getBigDecimal("orderItemQuantity");
										String toShipGroupSeqId = shipGroupSeqId;
										List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc", 
												EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, 
														"shipGroupSeqId", shipGroupSeqId, "orderItemAssocTypeId", "DROP_SHIPMENT")), null, null, null, false);
										if (UtilValidate.isNotEmpty(listOrderItemAssoc)) {
											GenericValue orderItemAssoc = EntityUtil.getFirst(listOrderItemAssoc);
											toOrderId = orderItemAssoc.getString("toOrderId");
											String toOrderItemSeqId = orderItemAssoc.getString("toOrderItemSeqId");
											toShipGroupSeqId = orderItemAssoc.getString("toShipGroupSeqId");
											orderItemAssoc.set("quantity", orderItemQuantityNew);
											if (item.getString("statusId").equals("ITEM_CANCELLED")) {
												Map<String, Object> mapItems = FastMap.newInstance();
												mapItems.put("orderId", toOrderId);
												mapItems.put("orderItemSeqId", toOrderItemSeqId);
												mapItems.put("statusId", "ITEM_CANCELLED");
												mapItems.put("changeReason", "SO canceled PO");
												mapItems.put("userLogin", userLogin);
												dispatcher.runSync("changeOrderItemStatus", mapItems);

											} else {

												delegator.store(orderItemAssoc);
												// store po items

												Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
												Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
												Map<String, String> itemDescriptionMap = FastMap.newInstance();
												Map<String, String> itemReasonMap = FastMap.newInstance();
												Map<String, String> itemCommentMap = FastMap.newInstance();
												Map<String, String> itemAttributesMap = FastMap.newInstance();
												Map<String, String> itemEstimatedShipDateMap = FastMap.newInstance();
												Map<String, String> itemEstimatedDeliveryDateMap = FastMap.newInstance();
												Map<String, String> itemQtyMap = FastMap.newInstance();
												Map<String, String> itemPriceMap = FastMap.newInstance();
												Map<String, String> overridePriceMap = FastMap.newInstance();

												String qtyKey = toOrderItemSeqId + ":" + toShipGroupSeqId;
												String orderItemQuantityNewStr = "";
									    		try {
									    			orderItemQuantityNewStr = (String) ObjectType.simpleTypeConvert(orderItemQuantityNew, "String", null, locale);
									    		} catch (GeneralException e) {
									    			// TODO Auto-generated catch block
									    			e.printStackTrace();
									    		}
									    		
												itemQtyMap.put(qtyKey, orderItemQuantityNewStr);
												itemReasonMap.put(toOrderItemSeqId, "");
												itemCommentMap.put(toOrderItemSeqId, "");
												itemAlternativeQtyMap.put(qtyKey, orderItemQuantityNewStr);
												itemReasonMap.put(toOrderItemSeqId, "");
												itemCommentMap.put(toOrderItemSeqId, "");
												if (productItem != null) {
													itemQuantityUomIdMap.put(toOrderItemSeqId, productItem.getString("quantityUomId"));
												}
												Map<String, Object> contextTmp = new HashMap<String, Object>();
												contextTmp.put("orderId", toOrderId);
												contextTmp.put("orderTypeId", "PURCHASE_ORDER");
												contextTmp.put("itemQtyMap", itemQtyMap);
												contextTmp.put("itemPriceMap", itemPriceMap);
												contextTmp.put("itemDescriptionMap", itemDescriptionMap);
												contextTmp.put("itemReasonMap", itemReasonMap);
												contextTmp.put("itemCommentMap", itemCommentMap);
												contextTmp.put("itemAttributesMap", itemAttributesMap);
												contextTmp.put("itemShipDateMap", itemEstimatedShipDateMap);
												contextTmp.put("itemDeliveryDateMap", itemEstimatedDeliveryDateMap);
												contextTmp.put("overridePriceMap", overridePriceMap);
												contextTmp.put("userLogin", userLogin);
												contextTmp.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
												contextTmp.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
												dispatcher.runSync("updateOrderItemsNoActions", contextTmp);
											}
										} else {// nguuoc lai append new poitem
												// checkAppendDropShipPurchaseOrdersItemPO
											dispatcher.runSync("checkAppendDropShipPurchaseOrdersItemPO", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));

										}
									}
								}
							}
						}
					} else if ("PURCHASE_ORDER".equals(orh.getOrderTypeId())) {
						if (changeSO.equals("Y")) {
							GenericValue orderItemAssPO = EntityUtil.getFirst(listOrderItemAssPO);
							String orderIdSO = orderItemAssPO.getString("orderId");
							// String shipGroupSeqIdSO = orderItemAssPO.getString("shipGroupSeqId");
							// get the order's ship groups
							for (GenericValue shipGroup : orh.getOrderItemShipGroups()) {
								// if (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId"))) {
								List<GenericValue> items = orh.getValidOrderItems(shipGroup.getString("shipGroupSeqId"));
								if (!UtilValidate.isEmpty(items)) {
									for (GenericValue item : items) {
										// if(!item.getString("statusId").equals("ITEM_CANCELLED")){
										String orderItemSeqIdPO = item.getString("orderItemSeqId");
										String shipGroupSeqIdPO = item.getString("shipGroupSeqId");
										String productId = item.getString("productId");
										GenericValue productItem = null;
										try {
											productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
											if (productItem == null) {
												continue;
											}
										} catch (Exception e) {
											Debug.logWarning(e, "Problems [product not exists] get productId = " + productId, module);
										}

										BigDecimal orderItemQuantity = item.getBigDecimal("orderItemQuantity");

										List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc",
												EntityCondition.makeCondition(UtilMisc.toMap("toOrderId", orderId, "toOrderItemSeqId", orderItemSeqIdPO, 
														"toShipGroupSeqId", shipGroupSeqIdPO, "orderItemAssocTypeId", "DROP_SHIPMENT")), null, null, null, false);
										if (UtilValidate.isNotEmpty(listOrderItemAssoc)) {
											GenericValue orderItemAssoc = EntityUtil.getFirst(listOrderItemAssoc);
											// toOrderId =
											// orderItemAssoc.getString("toOrderId");
											String orderItemSeqIdSO = orderItemAssoc.getString("orderItemSeqId");
											String shipGroupSeqIdSO = orderItemAssoc.getString("shipGroupSeqId");
											// store assoc
											orderItemAssoc.set("quantity", orderItemQuantity);
											delegator.store(orderItemAssoc);
											// store so items
											Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
											Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
											Map<String, String> itemDescriptionMap = FastMap.newInstance();
											Map<String, String> itemReasonMap = FastMap.newInstance();
											Map<String, String> itemCommentMap = FastMap.newInstance();
											Map<String, String> itemAttributesMap = FastMap.newInstance();
											Map<String, String> itemEstimatedShipDateMap = FastMap.newInstance();
											Map<String, String> itemEstimatedDeliveryDateMap = FastMap.newInstance();
											Map<String, String> itemQtyMap = FastMap.newInstance();
											Map<String, String> itemPriceMap = FastMap.newInstance();
											Map<String, String> overridePriceMap = FastMap.newInstance();
											itemQtyMap.put(orderItemSeqIdSO + ":" + shipGroupSeqIdSO, orderItemQuantity.toString());
											itemAlternativeQtyMap.put(orderItemSeqIdSO + ":" + shipGroupSeqIdSO, orderItemQuantity.toString());
											itemReasonMap.put(orderItemSeqIdSO, "");
											itemCommentMap.put(orderItemSeqIdSO, "");
											if (productItem != null) {
												itemQuantityUomIdMap.put(orderItemSeqIdSO, productItem.getString("quantityUomId"));
											}
											Map<String, Object> contextTmp = new HashMap<String, Object>();
											contextTmp.put("orderId", orderIdSO);
											contextTmp.put("orderTypeId", "SALES_ORDER");
											contextTmp.put("itemQtyMap", itemQtyMap);
											contextTmp.put("itemPriceMap", itemPriceMap);
											contextTmp.put("itemDescriptionMap", itemDescriptionMap);
											contextTmp.put("itemReasonMap", itemReasonMap);
											contextTmp.put("itemCommentMap", itemCommentMap);
											contextTmp.put("itemAttributesMap", itemAttributesMap);
											contextTmp.put("itemShipDateMap", itemEstimatedShipDateMap);
											contextTmp.put("itemDeliveryDateMap", itemEstimatedDeliveryDateMap);
											contextTmp.put("overridePriceMap", overridePriceMap);
											contextTmp.put("userLogin", userLogin);
											contextTmp.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
											contextTmp.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
											dispatcher.runSync("updateOrderItemsNoActions", contextTmp);
										}
										// }
									}
								}
								// }
							}
						}
					}

				} catch (Exception exc) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderUpdateCreatingDropShipmentsItemPOError", UtilMisc.toMap("orderId", orderId, "errorString", exc.getMessage()), locale));
				}

			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> checkAppendDropShipPurchaseOrdersItemPO(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// TODO (use the "system" user)
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		Locale locale = (Locale) context.get("locale");
		OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
		String toOrderId = null;
		String toShipGroupSeqId = null;
		List<GenericValue> listOrderItemAssoc = FastList.newInstance();
		// List<GenericValue> listOrderItemAssocPO = FastList.newInstance();
		try {
			listOrderItemAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemAssocTypeId", "DROP_SHIPMENT")), null, null, null, false);
			delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("toOrderId", orderId, "orderItemAssocTypeId", "DROP_SHIPMENT")), null, null, null, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		if (UtilValidate.isNotEmpty(listOrderItemAssoc)) {
			GenericValue orderItemAssoc = EntityUtil.getFirst(listOrderItemAssoc);
			toShipGroupSeqId = orderItemAssoc.getString("toShipGroupSeqId");
			toOrderId = orderItemAssoc.getString("toOrderId");
			// notify
			// if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
			// GenericValue orderItemAssoc =
			// EntityUtil.getFirst(listOrderItemAssoc);
			//
			// toShipGroupSeqId = orderItemAssoc.getString("toShipGroupSeqId");
			// toOrderId = orderItemAssoc.getString("toOrderId");
			// List<String> listParty = new ArrayList<String>();
			// List<String> listPartyGroups =
			// SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator,
			// "RECEIVE_MSG_FAVOR_DELIVERY");
			// if (!listPartyGroups.isEmpty()){
			// for (String group : listPartyGroups){
			// try {
			// List<GenericValue> listManagers =
			// delegator.findList("PartyRelationship",
			// EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom",
			// group, "roleTypeIdFrom", "PO_MANAGER")), null, null, null,
			// false);
			// listManagers = EntityUtil.filterByDate(listManagers);
			// if (!listManagers.isEmpty()){
			// for (GenericValue manager : listManagers){
			// listParty.add(manager.getString("partyIdFrom"));
			// }
			// }
			// } catch (GenericEntityException e) {
			// String errMsg = "Fatal error when get party relationship: " +
			// e.toString();
			// Debug.logError(e, errMsg, module);
			// return ServiceUtil.returnError("Get Party relationship error!");
			// }
			// }
			// }
			// if (!listParty.isEmpty()){
			// for (String managerParty : listParty){
			// String sendMessage = UtilProperties.getMessage("POUiLabels",
			// "SOnotifyPOUpdate", locale) + " SO("+orderId+")"+"
			// PO("+toOrderId+")";
			// String targetLink = "orderId="+toOrderId;
			// String sendToPartyId = managerParty;
			// Map<String, Object> mapContext = new HashMap<String, Object>();
			// mapContext.put("partyId", sendToPartyId);
			// mapContext.put("targetLink", targetLink);
			// mapContext.put("action", "viewDetailPO");
			// mapContext.put("header", sendMessage);
			// mapContext.put("userLogin", userLogin);
			// mapContext.put("ntfType", "ONE");
			// try {
			// dispatcher.runSync("createNotification", mapContext);
			// } catch (GenericServiceException e) {
			// String errMsg = "Fatal error when create notification: " +
			// e.toString();
			// Debug.logError(e, errMsg, module);
			// return ServiceUtil.returnError("sent notify error!");
			// }
			// }
			// }
			// }

			try {
				// if sales order
				if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
					// get the order's ship groups
					for (GenericValue shipGroup : orh.getOrderItemShipGroups()) {
						if (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId"))) {
							List<GenericValue> items = orh.getValidOrderItems(shipGroup.getString("shipGroupSeqId"));
							if (!UtilValidate.isEmpty(items)) {
								for (GenericValue item : items) {
									if (!item.getString("statusId").equals("ITEM_CANCELLED")) {
										String orderItemSeqId = item.getString("orderItemSeqId");
										String shipGroupSeqId = item.getString("shipGroupSeqId");
										String productId = item.getString("productId");
										BigDecimal orderItemQuantity = item.getBigDecimal("orderItemQuantity");

										List<GenericValue> listOrderItemAssocCheck = delegator.findList("OrderItemAssoc",
												EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, 
														"shipGroupSeqId", shipGroupSeqId, "orderItemAssocTypeId", "DROP_SHIPMENT")), null, null, null, false);
										if (UtilValidate.isEmpty(listOrderItemAssocCheck)) {
											if (toOrderId != null) {
												// lay orderItem truoc khi append
												List<GenericValue> listOrderItemPast = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", toOrderId)), null, null, null, false);

												Map<String, Object> contextTmp = new HashMap<String, Object>();
												if (listOrderItemPast.get(0).getTimestamp("estimatedDeliveryDate") != null) {
													contextTmp.put("itemDesiredDeliveryDate", listOrderItemPast.get(0).getTimestamp("estimatedDeliveryDate"));
												}
												if (listOrderItemPast.get(0).getTimestamp("shipBeforeDate") != null) {
													contextTmp.put("shipBeforeDate", listOrderItemPast.get(0).getTimestamp("shipBeforeDate"));
												}
												if (listOrderItemPast.get(0).getTimestamp("shipAfterDate") != null) {
													contextTmp.put("shipAfterDate", listOrderItemPast.get(0).getTimestamp("shipAfterDate"));
												}

												contextTmp.put("orderId", toOrderId);
												contextTmp.put("shipGroupSeqId", toShipGroupSeqId);
												contextTmp.put("productId", productId);
												contextTmp.put("quantity", orderItemQuantity);
												contextTmp.put("userLogin", userLogin);
												dispatcher.runSync("appendOrderItem", contextTmp);

												List<GenericValue> listOrderItemCur = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", toOrderId)), null, null, null, false);
												for (GenericValue curOrderItem : listOrderItemCur) {
													String orderItemSeqIdCur = curOrderItem.getString("orderItemSeqId");
													boolean check = false;
													for (GenericValue orderItemPast : listOrderItemPast) {
														if (orderItemSeqIdCur.equals(orderItemPast.getString("orderItemSeqId"))) {
															check = true;
															break;
														}
													}
													if (!check) {
														GenericValue orderItemAssocUpdate = delegator.makeValue("OrderItemAssoc");
														orderItemAssocUpdate.put("orderId", orderId);
														orderItemAssocUpdate.put("orderItemSeqId", orderItemSeqId);
														orderItemAssocUpdate.put("shipGroupSeqId", shipGroupSeqId);
														orderItemAssocUpdate.put("toOrderId", toOrderId);
														orderItemAssocUpdate.put("toOrderItemSeqId", orderItemSeqIdCur);
														orderItemAssocUpdate.put("toShipGroupSeqId", toShipGroupSeqId);
														orderItemAssocUpdate.put("orderItemAssocTypeId", "DROP_SHIPMENT");
														orderItemAssocUpdate.put("quantity", orderItemQuantity);
														delegator.create(orderItemAssocUpdate);

														GenericValue orderHeaderPO = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", toOrderId), false);
														if (orderHeaderPO.getString("statusId").equals("ORDER_APPROVED")) {
															Map<String, Object> mapItems = FastMap.newInstance();
															mapItems.put("orderId", toOrderId);
															mapItems.put("orderItemSeqId", orderItemSeqIdCur);
															mapItems.put("statusId", "ITEM_APPROVED");
															mapItems.put("userLogin", userLogin);
															dispatcher.runSync("changeOrderItemStatus", mapItems);
														}

													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				// else if ("PURCHASE_ORDER".equals(orh.getOrderTypeId())) {
				// GenericValue orderItemAssocPO =
				// EntityUtil.getFirst(listOrderItemAssocPO);
				// String orderIdSO = orderItemAssocPO.getString("orderId");
				// String shipGroupSeqIdSO =
				// orderItemAssocPO.getString("shipGroupSeqId");
				// for (GenericValue shipGroup : orh.getOrderItemShipGroups()) {
				//// if
				// (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId")))
				// {
				// List<GenericValue> items =
				// orh.getValidOrderItems(shipGroup.getString("shipGroupSeqId"));
				// if (!UtilValidate.isEmpty(items)) {
				// for (GenericValue item : items) {
				// if(!item.getString("statusId").equals("ITEM_CANCELLED")){
				// String orderItemSeqIdPO = item.getString("orderItemSeqId");
				// String shipGroupSeqIdPO = item.getString("shipGroupSeqId");
				// String productId = item.getString("productId");
				// BigDecimal orderItemQuantity =
				// item.getBigDecimal("orderItemQuantity");
				//
				// List<GenericValue> listOrderItemAssocCheck =
				// delegator.findList("OrderItemAssoc",
				// EntityCondition.makeCondition(UtilMisc.toMap("toOrderId",
				// orderId, "toOrderItemSeqId", orderItemSeqIdPO,
				// "toShipGroupSeqId", shipGroupSeqIdPO, "orderItemAssocTypeId",
				// "DROP_SHIPMENT")), null, null, null, false);
				// if(UtilValidate.isEmpty(listOrderItemAssocCheck)){
				// if(orderIdSO != null){
				// //lay orderItem truoc khi append
				// List<GenericValue> listOrderItemPast =
				// delegator.findList("OrderItem",
				// EntityCondition.makeCondition(UtilMisc.toMap("orderId",
				// orderIdSO)), null, null, null, false);
				//
				// Map<String,Object> contextTmp = new HashMap<String,
				// Object>();
				// if(listOrderItemPast.get(0).getTimestamp("estimatedDeliveryDate")
				// != null){
				// contextTmp.put("itemDesiredDeliveryDate",
				// listOrderItemPast.get(0).getTimestamp("estimatedDeliveryDate"));
				// }
				// if(listOrderItemPast.get(0).getTimestamp("shipBeforeDate") !=
				// null){
				// contextTmp.put("shipBeforeDate",
				// listOrderItemPast.get(0).getTimestamp("shipBeforeDate"));
				// }
				// if(listOrderItemPast.get(0).getTimestamp("shipAfterDate") !=
				// null){
				// contextTmp.put("shipAfterDate",
				// listOrderItemPast.get(0).getTimestamp("shipAfterDate"));
				// }
				// contextTmp.put("orderId", orderIdSO);
				// contextTmp.put("shipGroupSeqId", shipGroupSeqIdSO);
				// contextTmp.put("productId", productId);
				// contextTmp.put("quantity", orderItemQuantity);
				// contextTmp.put("userLogin", userLogin);
				// dispatcher.runSync("appendOrderItem", contextTmp);
				//
				// List<GenericValue> listOrderItemCur =
				// delegator.findList("OrderItem",
				// EntityCondition.makeCondition(UtilMisc.toMap("orderId",
				// orderIdSO)), null, null, null, false);
				// for(GenericValue curOrderItem : listOrderItemCur){
				// String orderItemSeqIdCur =
				// curOrderItem.getString("orderItemSeqId");
				// boolean check = false;
				// for(GenericValue orderItemPast : listOrderItemPast){
				// if(orderItemSeqIdCur.equals(orderItemPast.getString("orderItemSeqId"))){
				// check = true;
				// break;
				// }
				// }
				// if(!check){
				// GenericValue orderItemAssocUpdate =
				// delegator.makeValue("OrderItemAssoc");
				// orderItemAssocUpdate.put("orderId", orderIdSO);
				// orderItemAssocUpdate.put("orderItemSeqId",
				// orderItemSeqIdCur);
				// orderItemAssocUpdate.put("shipGroupSeqId", shipGroupSeqIdSO);
				// orderItemAssocUpdate.put("toOrderId", orderId);
				// orderItemAssocUpdate.put("toOrderItemSeqId",
				// orderItemSeqIdPO);
				// orderItemAssocUpdate.put("toShipGroupSeqId",
				// shipGroupSeqIdPO);
				// orderItemAssocUpdate.put("orderItemAssocTypeId",
				// "DROP_SHIPMENT");
				// orderItemAssocUpdate.put("quantity", orderItemQuantity);
				// delegator.create(orderItemAssocUpdate);
				// }
				// }
				// }
				// }
				// }
				// }
				// }
				//// }
				// }
				// }
			} catch (Exception exc) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderAppendCreatingDropShipmentsItemPOError", UtilMisc.toMap("orderId", orderId, "errorString", exc.getMessage()), locale));
			}
		}

		return ServiceUtil.returnSuccess();
	}
	/*public static Map<String, Object> checkUpdateDropShipPurchaseOrdersItemPO(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// TODO (use the "system" user)
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");
		Locale locale = (Locale) context.get("locale");
		OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
		String toOrderId = null;
		try {
			List<GenericValue> listOrderItemAss = delegator.findList("OrderItemAssoc",
					EntityCondition
							.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemAssocTypeId", "DROP_SHIPMENT")),
					null, null, null, false);
			if (UtilValidate.isNotEmpty(listOrderItemAss)) {
				GenericValue orderItemAss = EntityUtil.getFirst(listOrderItemAss);
				toOrderId = orderItemAss.getString("toOrderId");

				List<String> listParty = new ArrayList<String>();
				List<String> listPartyGroups = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator,
						"RECEIVE_MSG_FAVOR_DELIVERY", userLogin);
				if (!listPartyGroups.isEmpty()) {
					for (String group : listPartyGroups) {
						try {
							List<GenericValue> listManagers = delegator.findList(
									"PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom",
											group, "partyIdTo", "DPO", "roleTypeIdFrom", "MANAGER")),
									null, null, null, false);
							listManagers = EntityUtil.filterByDate(listManagers);
							if (!listManagers.isEmpty()) {
								for (GenericValue manager : listManagers) {
									listParty.add(manager.getString("partyIdFrom"));
								}
							}
						} catch (GenericEntityException e) {
							String errMsg = "Fatal error when get party relationship: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError("Get Party relationship error!");
						}
					}
				}
				if (!listParty.isEmpty()) {
					for (String managerParty : listParty) {
						String sendMessage = UtilProperties.getMessage("POUiLabels", "SOnotifyPOUpdate", locale)
								+ " SO(" + orderId + ")" + " PO(" + toOrderId + ")";
						String targetLink = "orderId=" + toOrderId;
						String sendToPartyId = managerParty;
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("partyId", sendToPartyId);
						mapContext.put("targetLink", targetLink);
						mapContext.put("action", "viewDetailPO");
						mapContext.put("header", sendMessage);
						mapContext.put("userLogin", userLogin);
						mapContext.put("ntfType", "ONE");
						try {
							dispatcher.runSync("createNotification", mapContext);
						} catch (GenericServiceException e) {
							String errMsg = "Fatal error when create notification: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError("sent notify error!");
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		try {
			// if sales order
			if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
				// get the order's ship groups
				for (GenericValue shipGroup : orh.getOrderItemShipGroups()) {
					if (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId"))) {
						List<GenericValue> items = orh.getValidOrderItems(shipGroup.getString("shipGroupSeqId"));
						if (!UtilValidate.isEmpty(items)) {
							for (GenericValue item : items) {
								String orderItemSeqId = item.getString("orderItemSeqId");
								String shipGroupSeqId = item.getString("shipGroupSeqId");
								BigDecimal orderItemQuantity = item.getBigDecimal("orderItemQuantity");

								String toShipGroupSeqId = shipGroupSeqId;
								List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc",
										EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId,
												"orderItemSeqId", orderItemSeqId, "shipGroupSeqId", shipGroupSeqId,
												"orderItemAssocTypeId", "DROP_SHIPMENT")),
										null, null, null, false);
								if (UtilValidate.isNotEmpty(listOrderItemAssoc)) {
									GenericValue orderItemAssoc = EntityUtil.getFirst(listOrderItemAssoc);
									String toOrderItemSeqId = orderItemAssoc.getString("toOrderItemSeqId");
									toShipGroupSeqId = orderItemAssoc.getString("toShipGroupSeqId");
									// store assoc
									orderItemAssoc.set("quantity", orderItemQuantity);
									delegator.store(orderItemAssoc);
									// store po items
									Map<String, String> itemDescriptionMap = FastMap.newInstance();
									Map<String, String> itemReasonMap = FastMap.newInstance();
									Map<String, String> itemCommentMap = FastMap.newInstance();
									Map<String, String> itemAttributesMap = FastMap.newInstance();
									Map<String, String> itemEstimatedShipDateMap = FastMap.newInstance();
									Map<String, String> itemEstimatedDeliveryDateMap = FastMap.newInstance();
									Map<String, String> itemQtyMap = FastMap.newInstance();
									Map<String, String> itemPriceMap = FastMap.newInstance();
									Map<String, String> overridePriceMap = FastMap.newInstance();
									itemQtyMap.put(toOrderItemSeqId + ":" + toShipGroupSeqId,
											orderItemQuantity.toString());
									itemReasonMap.put(toOrderItemSeqId, "");
									itemCommentMap.put(toOrderItemSeqId, "");
									Map<String, Object> contextTmp = new HashMap<String, Object>();
									contextTmp.put("orderId", toOrderId);
									contextTmp.put("orderTypeId", "PURCHASE_ORDER");
									contextTmp.put("itemQtyMap", itemQtyMap);
									contextTmp.put("itemPriceMap", itemPriceMap);
									contextTmp.put("itemDescriptionMap", itemDescriptionMap);
									contextTmp.put("itemReasonMap", itemReasonMap);
									contextTmp.put("itemCommentMap", itemCommentMap);
									contextTmp.put("itemAttributesMap", itemAttributesMap);
									contextTmp.put("itemShipDateMap", itemEstimatedShipDateMap);
									contextTmp.put("itemDeliveryDateMap", itemEstimatedDeliveryDateMap);
									contextTmp.put("overridePriceMap", overridePriceMap);
									contextTmp.put("userLogin", userLogin);
									dispatcher.runSync("updateOrderItems", contextTmp);
								}
							}
						}
					}
				}
			}
		} catch (Exception exc) {
			return ServiceUtil
					.returnError(UtilProperties.getMessage(resource, "OrderUpdateCreatingDropShipmentsItemPOError",
							UtilMisc.toMap("orderId", orderId, "errorString", exc.getMessage()), locale));
		}

		return ServiceUtil.returnSuccess();
	}*/
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetOrderItemReceived(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator listIterator = null;
		String orderId = null;
		if (parameters.containsKey("orderId") && parameters.get("orderId").length > 0) {
			orderId = parameters.get("orderId")[0];
		}
		List<Map<String, Object>> listOrderItems = FastList.newInstance();
		List<GenericValue> listOrderItemTmps = FastList.newInstance();
		GenericValue objOrderHeader = null;
		try {
			objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		} catch (GenericEntityException e){
			return ServiceUtil.returnError("OLBIUS: get orderHeader error! " +e.toString());
		}
		if (UtilValidate.isNotEmpty(objOrderHeader)) {
			EntityCondition cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
			EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("ITEM_CANCELLED", "ITEM_CREATED"));
			listAllConditions.add(cond1);
			listAllConditions.add(cond2);
			try {
				listIterator = delegator.find("InventoryItemReceiveAndOrderItem", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				listOrderItemTmps = POUtil.getIteratorPartialList(listIterator, parameters, successResult);
			} catch (GenericEntityException e){
				return ServiceUtil.returnError("OLBIUS: get orderItemReceived error! " +e.toString());
			}
		}
		if (!listOrderItemTmps.isEmpty()){
			for (GenericValue item : listOrderItemTmps) {
				String productId = item.getString("productId");
				BigDecimal convertNumber = ProductUtil.getConvertPackingNumber(delegator, productId, item.getString("quantityUomId"), item.getString("quantityUomIdBase"));
				Map<String, Object> map = FastMap.newInstance();
				map.putAll(item);
				map.put("convertPacking", convertNumber);
				listOrderItems.add(map);
			}
		}
		successResult.put("listIterator", listOrderItems);
		return successResult;
	}
	
	public static Map<String, Object> updateOrderItemsTotal(DispatchContext dpx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		String orderId = (String) context.get("orderId");
		Locale locale = (Locale) context.get("locale");
		
		GenericValue objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		if (UtilValidate.isNotEmpty(objOrderHeader)) {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyIdFrom = (String) context.get("partyIdFrom");
			String contactMechId = (String) context.get("contactMechId");
			String currencyUom = (String) context.get("currencyUomId");
			String listOrderItemUpdate = (String) context.get("listOrderItemUpdate");
			String listOrderItemDelete = (String) context.get("listOrderItemDelete");
			String listOrderItemCreateNew = (String) context.get("listOrderItemCreateNew");
			String originFacilityId = (String) context.get("originFacilityId");
			String shipBeforeDateStr = (String) context.get("shipBeforeDate");
			String shipAfterDateStr = (String) context.get("shipAfterDate");
			long shipBeforeDateLong = Long.parseLong(shipBeforeDateStr);
			long shipAfterDateLong = Long.parseLong(shipAfterDateStr);
			Timestamp shipBeforeDate = new Timestamp(shipBeforeDateLong);
			Timestamp shipAfterDate = new Timestamp(shipAfterDateLong);
			JSONArray listOrderItemUpdateArr = JSONArray.fromObject(listOrderItemUpdate);
			JSONArray listOrderItemDeleteArr = JSONArray.fromObject(listOrderItemDelete);
			JSONArray listOrderItemCreateNewArr = JSONArray.fromObject(listOrderItemCreateNew);
			
			boolean hasChangeDeliveryDate = false;
			Timestamp oldShipBeforeDate = objOrderHeader.getTimestamp("shipBeforeDate");
			Timestamp oldShipAfterDate = objOrderHeader.getTimestamp("shipAfterDate");
			if ((oldShipBeforeDate != shipBeforeDate && oldShipBeforeDate.compareTo(shipBeforeDate) != 0)
					|| (oldShipAfterDate != shipAfterDate && oldShipAfterDate.compareTo(shipAfterDate) != 0)) {
				hasChangeDeliveryDate = true;
			}
			
			objOrderHeader.put("shipBeforeDate", shipBeforeDate);
			objOrderHeader.put("shipAfterDate", shipAfterDate);
			objOrderHeader.put("originFacilityId", originFacilityId);
			objOrderHeader.put("currencyUom", currencyUom);
			delegator.store(objOrderHeader);
			
			if (hasChangeDeliveryDate) {
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("orderId", objOrderHeader.getString("orderId")));
				conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
				conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
				delegator.storeByCondition("OrderItem", UtilMisc.toMap("shipBeforeDate", shipBeforeDate, "shipAfterDate", shipAfterDate), EntityCondition.makeCondition(conds));
			}
			
			Map<String, Object> mapUpdate = FastMap.newInstance();
			mapUpdate.put("userLogin", userLogin);
			mapUpdate.put("orderId", orderId);
			mapUpdate.put("supplierPartyId", partyIdFrom);
			String shipGroupSeqId = null;
			List<GenericValue> listOrderItemShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
			if (!listOrderItemShipGroups.isEmpty()) {
				shipGroupSeqId = listOrderItemShipGroups.get(0).getString("shipGroupSeqId");
				for (GenericValue grp : listOrderItemShipGroups) {
					if (contactMechId != null && contactMechId.equals(grp.getString("contactMechId"))
							&& originFacilityId != null && originFacilityId.equals(grp.getString("originFacilityId"))
							&& partyIdFrom != null && partyIdFrom.equals(grp.getString("supplierPartyId"))) {
						// info about postal address, facility and supplier is not change, stop update
						break;
					}
					grp.put("contactMechId", contactMechId);
					grp.put("facilityId", originFacilityId);
					grp.put("supplierPartyId", partyIdFrom);
					delegator.store(grp);
				}
			}
			List<Map<String, Object>> listOrderItemChanged = FastList.newInstance();
			
			if (UtilValidate.isNotEmpty(shipGroupSeqId)) {
				
				Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
				Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
				Map<String, String> itemDescriptionMap = FastMap.newInstance();
				Map<String, String> itemCommentMap = FastMap.newInstance();
				Map<String, String> itemQtyMap = FastMap.newInstance();
				Map<String, String> itemAmountMap = FastMap.newInstance();
				Map<String, String> itemPriceMap = FastMap.newInstance();
				Map<String, String> overridePriceMap = FastMap.newInstance();
				
				// delete
				// Xoa de lai 1 SP neu khong don hang se bi huy
				String orderItemSeqIdTmp = null;
				for (int i = 0; i < listOrderItemDeleteArr.size(); i++) {
					JSONObject orderItem = listOrderItemDeleteArr.getJSONObject(i);
					String orderItemSeqId = (String) orderItem.get("orderItemSeqId");
					if (UtilValidate.isNotEmpty(orderItemSeqId)) {
						if (orderItemSeqIdTmp == null){
							orderItemSeqIdTmp = orderItemSeqId;
							continue;
						}
						Map<String, Object> mapCancel = FastMap.newInstance();
						mapCancel.put("orderId", orderId);
						mapCancel.put("orderItemSeqId", orderItemSeqId);
						mapCancel.put("userLogin", userLogin);
						try {
							dispatcher.runSync("cancelOrderItem", mapCancel);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: cancelOrderItem error! " + e.toString());
						}
						Map<String, Object> map = FastMap.newInstance();
						map.put("orderId", orderId);
						map.put("orderItemSeqId", orderItemSeqId);
						listOrderItemChanged.add(map);
					}
				}
				
				// add item
				List<Map<String, Object>> productItemsMap = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < listOrderItemCreateNewArr.size(); i++) {
					JSONObject productJson = listOrderItemCreateNewArr.getJSONObject(i);
					String productId = (String) productJson.get("productId");
					String quantityStr = (String) productJson.get("quantityPurchase");
					String quantityUomId = (String) productJson.get("quantityUomId");
					String weightUomId = (String) productJson.get("weightUomId");
					String itemComment = (String) productJson.get("itemComment");
					BigDecimal quantity = new BigDecimal(quantityStr);
					String priceStr = productJson.getString("lastPrice");
					BigDecimal basePrice = new BigDecimal(priceStr);
					
					Map<String, Object> mapNew = FastMap.newInstance();
					if (ProductUtil.isWeightProduct(delegator, productId)){
						BigDecimal amount = quantity;
						quantity = BigDecimal.ONE;
						mapNew.put("amount", amount);
					}
					mapNew.put("userLogin", userLogin);
					mapNew.put("quantity", quantity);
					mapNew.put("productId", productId);
					mapNew.put("changeComments", itemComment);
					mapNew.put("itemComment", itemComment);
					mapNew.put("shipGroupSeqId", shipGroupSeqId);
					mapNew.put("orderId", orderId);
					mapNew.put("shipBeforeDate", shipBeforeDate);
					mapNew.put("shipAfterDate", shipAfterDate);
					mapNew.put("quantityUomId", quantityUomId);
					mapNew.put("weightUomId", weightUomId);
					mapNew.put("basePrice", basePrice);
					productItemsMap.add(mapNew);
				}
				try {
					Map<String, Object> addNewOrderItemCtx = UtilMisc.toMap("orderId", orderId, "productList", productItemsMap, "userLogin", userLogin, "locale", locale);
					Map<String, Object> addNewOrderItemResult = dispatcher.runSync("appendOrderItemCustomList", addNewOrderItemCtx);
					if (ServiceUtil.isError(addNewOrderItemResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(addNewOrderItemResult));
					}
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: appendOrderItemCustomList error! " + e.toString());
				}
				
				// update
				if (listOrderItemUpdateArr.size() > 0) {
					boolean hasUpdate = false;
					for (int i = 0; i < listOrderItemUpdateArr.size(); i++) {
						JSONObject orderItemJson = listOrderItemUpdateArr.getJSONObject(i);
						String orderItemSeqId = (String) orderItemJson.get("orderItemSeqId");
						
						GenericValue objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
						String productId = objOrderItem.getString("productId");
						
						// get info of product
						GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						boolean isRequireAmountProduct = false;
						if ("Y".equals(objProduct.get("requireAmount"))) isRequireAmountProduct = true;
						String quantityUomIdBase = objProduct.getString("quantityUomId");
						
						// process price
						BigDecimal oldPrice = objOrderItem.getBigDecimal("alternativeUnitPrice");
						String priceStr = orderItemJson.getString("lastPrice");
						BigDecimal newPrice = null;
						if (UtilValidate.isNotEmpty(priceStr)) {
							String tmp = priceStr.replace(".", "");
							tmp = tmp.replace(",", ".");
							newPrice = new BigDecimal(tmp);
						}
						Boolean checkPrice = !POWorker.equalObject(oldPrice, newPrice);
						
						// process quantity
						BigDecimal oldQuantity = objOrderItem.getBigDecimal("quantity");
						if (UtilValidate.isNotEmpty(objOrderItem.get("cancelQuantity"))) {
							oldQuantity = oldQuantity.subtract(objOrderItem.getBigDecimal("cancelQuantity"));
						}
						String newQuantityAdd = (String) orderItemJson.get("quantity");
						BigDecimal newQuantity = new BigDecimal(newQuantityAdd);
						
						// process itemComment
						String oldComment = objOrderItem.getString("comments");
						String newCommment = null;
						if (orderItemJson.containsKey("itemComment")){
							newCommment = orderItemJson.getString("itemComment");
						}
						Boolean checkComment = !POWorker.equalObject(oldComment, newCommment);
						
						// process quantity uom
						String oldQuantityUomId = objOrderItem.getString("quantityUomId");
						String newQuantityUomId = null;
						if (orderItemJson.containsKey("quantityUomId")){
							newQuantityUomId = orderItemJson.getString("quantityUomId");
						}
						Boolean checkUom = !POWorker.equalObject(oldQuantityUomId, newQuantityUomId);
						
						// chi cap nhat khi co 1 su thay doi nao do. check them cac dieu kien khac neu co 
						if (newQuantity.compareTo(oldQuantity) != 0 || checkPrice || checkComment || checkUom){
							
							if (isRequireAmountProduct) {
								String quantity = newQuantity.toString();
								itemQtyMap.put(orderItemSeqId+":"+shipGroupSeqId, BigDecimal.ONE.toString());
								itemAmountMap.put(orderItemSeqId, quantity);
							} else {
								String quantity = newQuantity.toBigInteger().toString();
								itemQtyMap.put(orderItemSeqId+":"+shipGroupSeqId, quantity);
							}
							
							itemPriceMap.put(orderItemSeqId, priceStr);
							overridePriceMap.put(orderItemSeqId, "Y");
							
							BigDecimal alterQuantity = null;
							if (UtilValidate.isNotEmpty(newQuantityUomId)){
								//objOrderItem.put("quantityUomId", newQuantityUomId);
								if (UtilValidate.isNotEmpty(quantityUomIdBase) && !newQuantityUomId.equals(quantityUomIdBase)) {
									BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, objOrderItem.getString("productId"), newQuantityUomId, quantityUomIdBase);
									alterQuantity = newQuantity.divide(convert, RoundingMode.HALF_UP);
									itemAlternativeQtyMap.put(orderItemSeqId+":"+shipGroupSeqId, alterQuantity.toString());
								}
								itemQuantityUomIdMap.put(orderItemSeqId, newQuantityUomId);
							}
							
							String weightUomId = null;
							if (orderItemJson.containsKey("weightUomId")){
								weightUomId = orderItemJson.getString("weightUomId");
								if (UtilValidate.isNotEmpty(weightUomId)) {
									objOrderItem.put("weightUomId", weightUomId);
								}
							}
							if (UtilValidate.isNotEmpty(newCommment) || "".equals(newCommment)) {
								if ("".equals(newCommment)){
									itemCommentMap.put(orderItemSeqId, null);
								} else {
									itemCommentMap.put(orderItemSeqId, newCommment);
								}
							} 
							/*objOrderItem.put("shipBeforeDate", shipBeforeDate);
							objOrderItem.put("shipAfterDate", shipAfterDate);
							delegator.store(objOrderItem);*/
							
							Map<String, Object> map = FastMap.newInstance();
							map.put("orderId", orderId);
							map.put("orderItemSeqId", orderItemSeqId);
							listOrderItemChanged.add(map);
							
							if (!hasUpdate) hasUpdate = true;
						}
					}
				
					if (hasUpdate) {
						// update order items
						mapUpdate.put("orderTypeId", "PURCHASE_ORDER");
						mapUpdate.put("itemQtyMap", itemQtyMap);
						mapUpdate.put("itemAmountMap", itemAmountMap);
						mapUpdate.put("itemPriceMap", itemPriceMap);
						mapUpdate.put("overridePriceMap", overridePriceMap);
						mapUpdate.put("itemCommentMap", itemCommentMap);
						mapUpdate.put("itemDescriptionMap", itemDescriptionMap);
						mapUpdate.put("userLogin", userLogin);
						mapUpdate.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
						mapUpdate.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
						
						try {
							dispatcher.runSync("updateOrderItemsCustom", mapUpdate);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: updateOrderItemsCustom error! " + e.toString());
						}
					}
				// end process update order items
				}
				
				// delete 
				// Xoa not item con lai
				if (UtilValidate.isNotEmpty(orderItemSeqIdTmp)) {
					Map<String, Object> mapCancel = FastMap.newInstance();
					mapCancel.put("orderId", orderId);
					mapCancel.put("orderItemSeqId", orderItemSeqIdTmp);
					mapCancel.put("userLogin", userLogin);
					try {
						dispatcher.runSync("cancelOrderItem", mapCancel);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: cancelOrderItem error! " + e.toString());
					}
					Map<String, Object> map = FastMap.newInstance();
					map.put("orderId", orderId);
					map.put("orderItemSeqId", orderItemSeqIdTmp);
					listOrderItemChanged.add(map);
				}
				
				/*
				* need cancel delivery related
				*/
				if (!listOrderItemChanged.isEmpty()){
					for (Map<String, Object> map : listOrderItemChanged) {
						map.put("userLogin", userLogin);
						String changeReason = UtilProperties.getMessage(resource, "BPCancelDeliveryBecauseChangeOrder", (Locale)context.get("locale"));
						map.put("changeReason", changeReason);
						try {
							dispatcher.runSync("autoCancelDeliveryByOrderItem", map);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: autoCancelDeliveryByOrderItem error! " + e.toString());
						}
					}
				}
			}
		}
		Map<String, Object> returnSuccess = FastMap.newInstance();
		returnSuccess.put("orderId", orderId);
		return returnSuccess;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> autoCancelOrderItemRemaining(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String) context.get("orderId");
		GenericValue objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		if (UtilValidate.isNotEmpty(objOrderHeader)) {
			
			// check promo, co promo thi khong cho phep thuc hien
			List<GenericValue> listPromoUse = FastList.newInstance();
			try {
				listPromoUse = delegator.findList("ProductPromoUse", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductPromoUse: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listPromoUse.isEmpty()){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "POHasPromotionCannotDoThis", (Locale)context.get("locale")));
			}
			String orderTypeId = objOrderHeader.getString("orderTypeId");
			String isRecalculatePrice = (String) context.get("isRecalculatePrice");
			try {
				Map<String, Object> map = FastMap.newInstance();
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				map.put("userLogin", userLogin);
				map.put("orderId", orderId);
				Map<String, Object> mapReturn = dispatcher.runSync("getOrderItemsEditable", map);
				List<Map<String, Object>> listOrderItems = (List<Map<String, Object>>)mapReturn.get("listOrderItems");
				if (!listOrderItems.isEmpty()){
					for (Map<String, Object> item : listOrderItems) {
						Map<String, Object> mapCancel = FastMap.newInstance();
						String orderItemSeqId = (String)item.get("orderItemSeqId");
						GenericValue objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
						if (!"ITEM_CANCELLED".equals(objOrderItem.getString("statusId")) && !"ITEM_COMPLETED".equals(objOrderItem.getString("statusId"))){
							String productId = objOrderItem.getString("productId");
							GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
							if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount"))) {
								BigDecimal selectedAmount = objOrderItem.getBigDecimal("selectedAmount");
								if (UtilValidate.isNotEmpty(objOrderItem.get("cancelAmount"))) {
									selectedAmount = selectedAmount.subtract(objOrderItem.getBigDecimal("cancelAmount"));
								}
								BigDecimal cancelAmount = (BigDecimal)item.get("selectedAmount");
								if (UtilValidate.isNotEmpty(cancelAmount) && UtilValidate.isNotEmpty(selectedAmount)) {
									if (cancelAmount.compareTo(BigDecimal.ZERO) > 0){
										if (cancelAmount.compareTo(selectedAmount) < 0){
											// update
											Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
											Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
											Map<String, String> itemDescriptionMap = FastMap.newInstance();
											Map<String, String> itemCommentMap = FastMap.newInstance();
											Map<String, String> itemQtyMap = FastMap.newInstance();
											Map<String, String> itemAmountMap = FastMap.newInstance();
											Map<String, String> itemPriceMap = FastMap.newInstance();
											Map<String, String> overridePriceMap = FastMap.newInstance();
											
											String shipGroupSeqId = null;
											List<GenericValue> listOrderItemShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
											if (!listOrderItemShipGroups.isEmpty()) {
												shipGroupSeqId = listOrderItemShipGroups.get(0).getString("shipGroupSeqId");
											}
											
											itemQtyMap.put(orderItemSeqId+":"+shipGroupSeqId, BigDecimal.ONE.toString());
											itemAmountMap.put(orderItemSeqId+":"+shipGroupSeqId, selectedAmount.subtract(cancelAmount).toString());
											
											overridePriceMap.put(orderItemSeqId, "N");
											
											Map<String, Object> mapUpdate = FastMap.newInstance();
											mapUpdate.put("userLogin", userLogin);
											mapUpdate.put("orderId", orderId);
											mapUpdate.put("orderTypeId", orderTypeId);
											mapUpdate.put("itemQtyMap", itemQtyMap);
											mapUpdate.put("itemAmountMap", itemAmountMap);
											mapUpdate.put("itemPriceMap", itemPriceMap);
											mapUpdate.put("overridePriceMap", overridePriceMap);
											mapUpdate.put("itemCommentMap", itemCommentMap);
											mapUpdate.put("itemDescriptionMap", itemDescriptionMap);
											mapUpdate.put("userLogin", userLogin);
											mapUpdate.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
											mapUpdate.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
											
											try {
												dispatcher.runSync("updateOrderItemsCustom", mapUpdate);
											} catch (GenericServiceException e) {
												return ServiceUtil.returnError("OLBIUS: updateOrderItemsCustom error! " + e.toString());
											}
											
											Map<String, Object> mapChangeStatus = FastMap.newInstance();
											mapChangeStatus.put("userLogin", userLogin);
											mapChangeStatus.put("orderId", orderId);
											mapChangeStatus.put("orderItemSeqId", orderItemSeqId);
											mapChangeStatus.put("statusId", "ITEM_COMPLETED");
											try {
												dispatcher.runSync("changeOrderItemStatus", mapChangeStatus);
											} catch (GenericServiceException e) {
												return ServiceUtil.returnError("OLBIUS: changeOrderItemStatus error! " + e.toString());
											}
										} else {
											Map<String, Object> mapChangeStatus = FastMap.newInstance();
											mapChangeStatus.put("userLogin", userLogin);
											mapChangeStatus.put("orderId", orderId);
											mapChangeStatus.put("orderItemSeqId", orderItemSeqId);
											mapChangeStatus.put("statusId", "ITEM_CANCELLED");
											try {
												dispatcher.runSync("changeOrderItemStatus", mapChangeStatus);
											} catch (GenericServiceException e) {
												return ServiceUtil.returnError("OLBIUS: changeOrderItemStatus error! " + e.toString());
											}
										}
									}
								}
							} else {
								BigDecimal quantity = objOrderItem.getBigDecimal("quantity");
								if (UtilValidate.isNotEmpty(objOrderItem.get("cancelQuantity"))) {
									quantity = quantity.subtract(objOrderItem.getBigDecimal("cancelQuantity"));
								}
								BigDecimal cancelQuantity = (BigDecimal)item.get("quantity");
								if (UtilValidate.isNotEmpty(cancelQuantity)) {
									mapCancel.put("userLogin", userLogin);
									mapCancel.put("orderId", orderId);
									mapCancel.put("orderItemSeqId", orderItemSeqId);
									mapCancel.put("cancelQuantity", cancelQuantity);
									mapCancel.put("isRecalculatePrice", isRecalculatePrice);
									try {
										dispatcher.runSync("cancelOrderItem", mapCancel);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: cancelOrderItem error! " + e.toString());
									}
									
									if (quantity.compareTo(cancelQuantity) > 0){
										Map<String, Object> mapChangeStatus = FastMap.newInstance();
										mapChangeStatus.put("userLogin", userLogin);
										mapChangeStatus.put("orderId", orderId);
										mapChangeStatus.put("orderItemSeqId", orderItemSeqId);
										mapChangeStatus.put("statusId", "ITEM_COMPLETED");
										try {
											dispatcher.runSync("changeOrderItemStatus", mapChangeStatus);
										} catch (GenericServiceException e) {
											return ServiceUtil.returnError("OLBIUS: changeOrderItemStatus error! " + e.toString());
										}
									}
								}
							}
						}
					}
				}
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: getOrderItemsEditable error! " + e.toString());
			}
		}
		Map<String, Object> returnSuccess = FastMap.newInstance();
		returnSuccess.put("orderId", orderId);
		return returnSuccess;
	}
	
	public static Map<String,Object> getOrderItemsEditable(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listOrderItems = new ArrayList<Map<String, Object>>();
		try {
			listOrderItems = ProductUtil.getOrderItemsEditable(delegator, orderId);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: getOrderItemsEditable error! " + e.toString());
		}
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("listOrderItems", listOrderItems);
		return result;
	}
	
	public static Map<String, Object> updateReturnSupplierTotal(DispatchContext dpx, Map<String, ? extends Object> context){
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		String returnId = (String) context.get("returnId");
		String destinationFacilityId = null;
		if (UtilValidate.isNotEmpty(context.get("destinationFacilityId"))) {
			destinationFacilityId = (String) context.get("destinationFacilityId");
		}
		GenericValue objReturnHeader = null;
		try {
			objReturnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
		} catch (GenericEntityException e3) {
			e3.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: findOne ReturnHeader! " + e3.toString());
		}
		if (UtilValidate.isNotEmpty(objReturnHeader)) {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String description = (String)context.get("description");
			objReturnHeader.put("description", description);
			objReturnHeader.put("destinationFacilityId", destinationFacilityId);
			try {
				delegator.store(objReturnHeader);
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
				return ServiceUtil.returnError("OLBIUS: store ReturnHeader! " + e2.toString());
			}
			
			// delete 
			if (UtilValidate.isNotEmpty(context.get("listReturnItemRemove"))) {
				String listReturnItemRemove = (String) context.get("listReturnItemRemove");
				JSONArray listItemDeleteArr = JSONArray.fromObject(listReturnItemRemove);
				for (int i = 0; i < listItemDeleteArr.size(); i++) {
					JSONObject returnItem = listItemDeleteArr.getJSONObject(i);
					String returnItemSeqId = (String) returnItem.get("returnItemSeqId");
					Map<String, Object> mapCancel = FastMap.newInstance();
					mapCancel.put("returnId", returnId);
					mapCancel.put("returnItemSeqId", returnItemSeqId);
					mapCancel.put("userLogin", userLogin);
					try {
						dispatcher.runSync("removeReturnItem", mapCancel);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: cancelOrderItem error! " + e.toString());
					}
				}
			}
				
			// update
			if (UtilValidate.isNotEmpty(context.get("listReturnItemUpdate"))) {
				String listReturnItemUpdate = (String) context.get("listReturnItemUpdate");
				JSONArray listItemUpdateArr = JSONArray.fromObject(listReturnItemUpdate);
				for (int i = 0; i < listItemUpdateArr.size(); i++) {
					JSONObject returnItem = listItemUpdateArr.getJSONObject(i);
					String returnItemSeqId = (String) returnItem.get("returnItemSeqId");
					
					String quantityTmp = (String) returnItem.get("quantity");
					String priceTmp = (String) returnItem.get("returnPrice");

					String returnReasonId = (String) returnItem.get("returnReasonId");
					BigDecimal returnQuantity = new BigDecimal(quantityTmp);
					BigDecimal returnPrice= new BigDecimal(priceTmp);
					Map<String, Object> mapUpdate = FastMap.newInstance();
					mapUpdate.put("returnId", returnId);
					mapUpdate.put("returnItemSeqId", returnItemSeqId);
					mapUpdate.put("returnQuantity", returnQuantity);
					mapUpdate.put("returnAmount", returnQuantity);
					mapUpdate.put("returnReasonId", returnReasonId);
					mapUpdate.put("returnPrice", returnPrice);
					mapUpdate.put("userLogin", userLogin);
					try {
						dispatcher.runSync("updateReturnItem", mapUpdate);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: updateReturnItem error! " + e.toString());
					}
				}
			}
			
			// create 
			if (UtilValidate.isNotEmpty(context.get("listReturnItemAdds"))) {
				String listReturnItemAdds = (String) context.get("listReturnItemAdds");
				JSONArray listItemCreateNewArr = JSONArray.fromObject(listReturnItemAdds);
				for (int i = 0; i < listItemCreateNewArr.size(); i++) {
					JSONObject product = listItemCreateNewArr.getJSONObject(i);
					String productId = (String) product.get("productId");
					String returnReasonId = (String) product.get("returnReasonId");
					String quantityStr = (String) product.get("quantity");
					String quantityUomId = (String) product.get("quantityUomId");
					BigDecimal quantity = new BigDecimal(quantityStr);
					String priceStr = product.getString("returnPrice");
					BigDecimal returnPrice = new BigDecimal(priceStr);
					Map<String, Object> mapNew = FastMap.newInstance();
					mapNew.put("userLogin", userLogin);
					mapNew.put("productId", productId);
					mapNew.put("returnId", returnId);
					mapNew.put("quantityUomId", quantityUomId);
					mapNew.put("returnReasonId", returnReasonId);
					mapNew.put("returnPrice", returnPrice);
					mapNew.put("returnTypeId", "RTN_REFUND");
					mapNew.put("returnItemTypeId", "RET_FPROD_ITEM");
					mapNew.put("returnQuantity", quantity);
					
	    			String returnItemSeqId = null;
					try {
						Map<String, Object> createReturnItemDirectly = dispatcher.runSync("createReturnItemDirectly", mapNew);
						returnItemSeqId = (String)createReturnItemDirectly.get("returnItemSeqId");
					} catch (GenericServiceException e1) {
						return ServiceUtil.returnError("OLBIUS: createReturnItemDirectly error! " + e1.toString());
					}
					if (UtilValidate.isNotEmpty(returnItemSeqId)) {
						//create return adjustment
		    			List<GenericValue> productTax = FastList.newInstance();
		    			try {
							productTax = delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", productId), null, false);
						} catch (GenericEntityException e1) {
							e1.printStackTrace();
							return ServiceUtil.returnError("OLBIUS: findByAnd ProductAndTaxAuthorityRate error! " + e1.toString());
						}
		    			
		    			if (UtilValidate.isNotEmpty(productTax)){
		    				BigDecimal taxPercent = productTax.get(0).getBigDecimal("taxPercentage");
		    				if (taxPercent.compareTo(BigDecimal.ZERO) > 0){
		    					Map<String, Object> createCartItemAdjMap = FastMap.newInstance(); 
		    					createCartItemAdjMap.put("returnId", returnId);
		    					createCartItemAdjMap.put("returnItemSeqId", returnItemSeqId);
		    					createCartItemAdjMap.put("returnTypeId", "RTN_REFUND");
		    					createCartItemAdjMap.put("returnAdjustmentTypeId", "RET_SALES_TAX_ADJ");
		    					createCartItemAdjMap.put("description", UtilProperties.getMessage("BasePosUiLabels", "BSReturnSalesTaxNoOrder", (Locale)context.get("locale")));
		    					createCartItemAdjMap.put("shipGroupSeqId", "_NA_");
		    					createCartItemAdjMap.put("createdDate", UtilDateTime.nowTimestamp());
		    					createCartItemAdjMap.put("createdByUserLogin", userLogin.getString("userLoginId"));
		    					createCartItemAdjMap.put("taxAuthorityRateSeqId", productTax.get(0).getString("taxAuthorityRateSeqId"));
		    					createCartItemAdjMap.put("sourcePercentage", taxPercent);
		    					createCartItemAdjMap.put("primaryGeoId", productTax.get(0).getString("originGeoId")); 
		    					createCartItemAdjMap.put("taxAuthGeoId", productTax.get(0).getString("taxAuthGeoId"));
		    					createCartItemAdjMap.put("taxAuthPartyId", productTax.get(0).getString("taxAuthPartyId"));
		    					
		    					BigDecimal taxAmount = quantity.multiply(returnPrice.multiply(taxPercent).divide(new BigDecimal(100)));
		    					createCartItemAdjMap.put("amount", taxAmount);
		    					createCartItemAdjMap.put("userLogin", userLogin);
		    					
								try {
									dispatcher.runSync("createReturnAdjustment", createCartItemAdjMap);
								} catch (GenericServiceException e) {
									e.printStackTrace();
									return ServiceUtil.returnError("OLBIUS: createReturnAdjustment error! " + e.toString());
								}
		    				}
		    			}
					}
					// auto approve if returnheader was accepted
					if ("SUP_RETURN_ACCEPTED".equals(objReturnHeader.getString("statusId"))) {
						List<GenericValue> listItemCreatedByProducts  = FastList.newInstance();
						try {
							listItemCreatedByProducts = delegator.findList("ReturnItem",
									EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId, "productId", productId, "statusId", "SUP_RETURN_REQUESTED")), null, null, null, false);
						} catch (GenericEntityException e1) {
							e1.printStackTrace();
							return ServiceUtil.returnError("OLBIUS: findList ReturnItem error! " + e1.toString());
						}
						if (!listItemCreatedByProducts.isEmpty()){
							for (GenericValue item : listItemCreatedByProducts) {
								try {
									dispatcher.runSync("updateReturnItemsStatus", UtilMisc.toMap("returnId", returnId, "returnItemSeqId", item.getString("returnItemSeqId"), "statusId", "SUP_RETURN_ACCEPTED", "userLogin", userLogin));
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: updateReturnItemsStatus error! " + e.toString());
								}
							}
						}
					}
				}
			}
		}
		Map<String, Object> returnSuccess = FastMap.newInstance();
		returnSuccess.put("returnId", returnId);
		return returnSuccess;
	}
	
	public static Map<String, Object> getProductReturnPrice(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		String productId = (String)context.get("productId");
		String organizationPartyId = (String)context.get("organizationPartyId");
		String facilityId = (String)context.get("facilityId");
		BigDecimal returnPrice = BigDecimal.ZERO;
		try {
			returnPrice = ProductUtil.getProductReturnPrice(delegator, productId, organizationPartyId, facilityId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: getProductReturnPrice error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("returnPrice", returnPrice);
		return successResult;
	}
	
	public static Map<String, Object> checkOrderEditable(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		String orderId = (String)context.get("orderId");
		String editable = "N";
		try {
			editable = POUtil.checkOrderEditable(delegator, orderId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: getProductReturnPrice error! " + e.toString());
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("editable", editable);
		return successResult;
	}
}
