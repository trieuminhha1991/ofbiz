package com.olbius.basepo.plan;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basepos.cart.PosCheckOutEvents;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PlanEvent {
	public static String module = PlanEvent.class.getName();
	public static String resource = "WebPosSettingUiLabels";
	public static int scale = 2;
	protected static final int PLAN_ITEM_SEQUENCE_ID_DIGITS = 5;

	@SuppressWarnings("unchecked")
	public static String createTransferPlan(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		boolean beganTx = TransactionUtil.begin();
		try {
			String facilityIdFrom = request.getParameter("facilityIdFrom");
			/*
			 * EntityCondition facilityCond =
			 * EntityCondition.makeCondition("facilityTypeId",
			 * EntityOperator.EQUALS, "RETAIL_STORE");
			 */
			EntityCondition facilityCond = null;
			List<GenericValue> facilityList = delegator.findList("Facility", facilityCond, null,
					UtilMisc.toList("facilityId"), null, false);

			String productListParam = request.getParameter("productList");
			BigDecimal grandTotal = BigDecimal.ZERO;

			List<Map<String, Object>> itemPlanList = FastList.newInstance();
			Map<String, Object> itemFacility = FastMap.newInstance();
			if (UtilValidate.isNotEmpty(productListParam)) {
				Map<String, Object> mapReturn = calculatePlan(facilityList, productListParam);
				itemFacility = (Map<String, Object>) mapReturn.get("itemFacility");
				itemPlanList = (List<Map<String, Object>>) mapReturn.get("itemPlanList");
				grandTotal = (BigDecimal) mapReturn.get("grandTotal");
			}
			// create plan header
			String planTypeId = "PLAN_TRANSFER";
			String planId = createPlanHeader(request, planTypeId, facilityIdFrom, null, grandTotal);
			// create plan item
			createPlanItem(request, planId, itemPlanList);
			if (UtilValidate.isNotEmpty(itemFacility)) {
				createShipmentAndShipmentItem(request, facilityList, itemFacility, facilityIdFrom, planId);
			}
			request.setAttribute("planId", planId);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return "error";
		}
		TransactionUtil.commit(beganTx);
		return "success";
	}

	@SuppressWarnings("unchecked")
	public static void createShipmentAndShipmentItem(HttpServletRequest request, List<GenericValue> facilityList,
			Map<String, Object> itemFacility, String facilityIdFrom, String planId)
			throws GenericServiceException, GenericEntityException {
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue facilityFrom = null;
		String currencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "VND");
		facilityFrom = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityIdFrom), false);
		for (GenericValue facility : facilityList) {
			String facilityId = facility.getString("facilityId");
			if (!facilityId.equals(facilityIdFrom)) {
				String partyIdFrom = facilityFrom.getString("ownerPartyId");
				String partyIdTo = facility.getString("ownerPartyId");

				Map<String, Object> createShipmentMap = FastMap.newInstance();
				createShipmentMap.put("userLogin", userLogin);
				createShipmentMap.put("shipmentTypeId", "TRANSFER");
				createShipmentMap.put("statusId", "SHIPMENT_INPUT");
				createShipmentMap.put("originFacilityId", facilityIdFrom);
				createShipmentMap.put("destinationFacilityId", facilityId);
				createShipmentMap.put("partyIdFrom", partyIdFrom);
				createShipmentMap.put("partyIdTo", partyIdTo);
				createShipmentMap.put("currencyUomId", currencyUomId);
				createShipmentMap.put("planId", planId);
				Map<String, Object> createShipment = FastMap.newInstance();
				createShipment = dispatcher.runSync("createShipment", createShipmentMap);
				String shipmentId = (String) createShipment.get("shipmentId");
				List<Map<String, Object>> rowDetail = (List<Map<String, Object>>) itemFacility.get(facilityId);
				if (UtilValidate.isNotEmpty(rowDetail)) {
					int shipmentItemSeqNum = 0;
					for (Map<String, Object> row : rowDetail) {
						shipmentItemSeqNum++;
						String shipmentItemSeqId = UtilFormatOut.formatPaddedNumber(shipmentItemSeqNum,
								PLAN_ITEM_SEQUENCE_ID_DIGITS);
						Map<String, Object> createShipmentItemMap = FastMap.newInstance();
						String productId = (String) row.get("productId");
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId),
								false);
						String quantityUomId = product.getString("quantityUomId");
						BigDecimal quantity = (BigDecimal) row.get("quantity");
						BigDecimal unitCost = (BigDecimal) row.get("unitCost");
						if (UtilValidate.isEmpty(quantity)) {
							quantity = BigDecimal.ZERO;
						}
						createShipmentItemMap.put("userLogin", userLogin);
						createShipmentItemMap.put("shipmentId", shipmentId);
						createShipmentItemMap.put("shipmentItemSeqId", shipmentItemSeqId);
						createShipmentItemMap.put("productId", productId);
						if (UtilValidate.isNotEmpty(quantityUomId)) {
							createShipmentItemMap.put("quantityUomId", quantityUomId);
						}
						createShipmentItemMap.put("quantity", quantity);
						createShipmentItemMap.put("unitCost", unitCost);
						dispatcher.runSync("createShipmentItem", createShipmentItemMap);
					}
				}
			}
		}
	}

	public static void createPlanItem(HttpServletRequest request, String planId, List<Map<String, Object>> itemList)
			throws GenericEntityException, GenericServiceException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		int planItemSeqNum = 0;
		for (Map<String, Object> item : itemList) {
			Map<String, Object> createPlanItemMap = FastMap.newInstance();

			planItemSeqNum++;
			String planItemSeqId = UtilFormatOut.formatPaddedNumber(planItemSeqNum, PLAN_ITEM_SEQUENCE_ID_DIGITS);
			String productId = (String) item.get("productId");
			String facilityId = (String) item.get("facilityId");
			BigDecimal quantity = (BigDecimal) item.get("quantity");
			BigDecimal unitCost = (BigDecimal) item.get("unitCost");
			GenericValue product = null;
			product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			GenericValue productFacility = null;
			productFacility = delegator.findOne("ProductFacility",
					UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
			if (UtilValidate.isEmpty(productFacility)) {
				// create product facility
				Map<String, Object> createProductFacility = FastMap.newInstance();
				createProductFacility.put("productId", productId);
				createProductFacility.put("facilityId", facilityId);
				productFacility = delegator.makeValue("ProductFacility", createProductFacility);
				productFacility.create();
			}
			BigDecimal qoh = productFacility.getBigDecimal("lastInventoryCount");
			BigDecimal qoo = productFacility.getBigDecimal("qoo");
			BigDecimal qpdL = productFacility.getBigDecimal("qpdL");
			BigDecimal qpdS = productFacility.getBigDecimal("qpdS");
			/*
			 * BigDecimal lidL = null; BigDecimal lidS = null;
			 */
			if (UtilValidate.isEmpty(qoh)) {
				qoh = BigDecimal.ZERO;
			}
			if (UtilValidate.isEmpty(qoo)) {
				qoo = BigDecimal.ZERO;
			}
			/*
			 * BigDecimal totalQuantity = qoh.add(qoo); totalQuantity =
			 * totalQuantity.add(quantity); if(UtilValidate.isNotEmpty(qpdL) &&
			 * qpdL.compareTo(BigDecimal.ZERO) != 0){ lidL =
			 * totalQuantity.divide(qpdL, scale, RoundingMode.HALF_UP); }
			 * 
			 * if(UtilValidate.isNotEmpty(qpdS) &&
			 * qpdS.compareTo(BigDecimal.ZERO) != 0){ lidS =
			 * totalQuantity.divide(qpdS, scale, RoundingMode.HALF_UP); }
			 */
			String quantityUomId = null;
			if (UtilValidate.isNotEmpty(product)) {
				quantityUomId = product.getString("quantityUomId");
			}
			createPlanItemMap.put("userLogin", userLogin);
			createPlanItemMap.put("planId", planId);
			createPlanItemMap.put("planItemSeqId", planItemSeqId);

			createPlanItemMap.put("productId", productId);
			createPlanItemMap.put("facilityId", facilityId);
			createPlanItemMap.put("quantity", quantity);
			createPlanItemMap.put("quantityUomId", quantityUomId);
			createPlanItemMap.put("unitCost", unitCost);
			createPlanItemMap.put("qoh", qoh);
			createPlanItemMap.put("qoo", qoo);
			createPlanItemMap.put("qpdL", qpdL);
			createPlanItemMap.put("qpdS", qpdS);
			/*
			 * createPlanItemMap.put("lidL", lidL);
			 * createPlanItemMap.put("lidS", lidS);
			 */
			dispatcher.runSync("createPlanItem", createPlanItemMap);
		}
	}

	public static String createPlanHeader(HttpServletRequest request, String planTypeId, String facilityId,
			String partyId, BigDecimal grandTotal) throws GenericServiceException {
		String result = null;
		Locale locale = UtilHttp.getLocale(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String currencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "VND");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		String errorMessage = null;
		if (planTypeId.equals("PLAN_ORDER")) {
			errorMessage = UtilProperties.getMessage(resource, "SettingCanNotCreatePlanPurchase", locale);
		} else if (planTypeId.equals("PLAN_TRANSFER")) {
			errorMessage = UtilProperties.getMessage(resource, "SettingCanNotCreatePlanTransfer", locale);
		}
		Map<String, Object> createPlanHeaderMap = FastMap.newInstance();
		createPlanHeaderMap.put("userLogin", userLogin);
		createPlanHeaderMap.put("planTypeId", planTypeId);
		createPlanHeaderMap.put("facilityId", facilityId);
		if (UtilValidate.isNotEmpty(partyId)) {
			createPlanHeaderMap.put("partyId", partyId);
		}
		createPlanHeaderMap.put("grandTotal", grandTotal);
		createPlanHeaderMap.put("currencyUomId", currencyUomId);
		createPlanHeaderMap.put("createdDate", nowTimestamp);

		Map<String, Object> createPlanHeader = dispatcher.runSync("createPlanHeader", createPlanHeaderMap);
		if (!ServiceUtil.isSuccess(createPlanHeader)) {
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		result = (String) createPlanHeader.get("planId");
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> calculatePlan(List<GenericValue> facilityList, String productListParam) {
		Map<String, Object> mapReturn = FastMap.newInstance();
		List<Map<String, Object>> itemPlanList = FastList.newInstance();
		Map<String, Object> itemFacility = FastMap.newInstance();
		BigDecimal grandTotal = BigDecimal.ZERO;
		JSONArray productListJson = JSONArray.fromObject(productListParam);
		for (GenericValue facility : facilityList) {
			String facilityId = facility.getString("facilityId");
			List<Map<String, Object>> productFacility = FastList.newInstance();
			if (UtilValidate.isNotEmpty(productListParam)) {
				for (int index = 0; index < productListJson.size(); index++) {
					Map<String, Object> itemPlan = FastMap.newInstance();
					JSONObject productJson = productListJson.getJSONObject(index);
					String productId = productJson.getString("productId");
					List<Map<String, Object>> rowDetail = (List<Map<String, Object>>) productJson.get("rowDetail");
					if (UtilValidate.isNotEmpty(rowDetail)) {
						for (Map<String, Object> row : rowDetail) {
							String facilityIdRow = (String) row.get("facilityId");
							if (facilityId.equals(facilityIdRow)) {
								BigDecimal quantity = null;
								BigDecimal unitCost = null;
								if (UtilValidate.isNotEmpty(row.get("quantity"))) {
									quantity = new BigDecimal(row.get("quantity").toString());
								} else {
									quantity = BigDecimal.ZERO;
								}
								if (UtilValidate.isNotEmpty(row.get("unitCost"))) {
									unitCost = new BigDecimal(row.get("unitCost").toString());
								} else {
									unitCost = BigDecimal.ZERO;
								}
								Map<String, Object> productFacilityTmp = FastMap.newInstance();
								productFacilityTmp.put("facilityId", facilityId);
								productFacilityTmp.put("quantity", quantity);
								productFacilityTmp.put("unitCost", unitCost);
								productFacilityTmp.put("productId", productId);
								productFacility.add(productFacilityTmp);
								BigDecimal itemTotal = quantity.multiply(unitCost);
								grandTotal = grandTotal.add(itemTotal);
								itemPlan.put("productId", productId);
								itemPlan.put("facilityId", facilityIdRow);
								itemPlan.put("quantity", quantity);
								itemPlan.put("unitCost", unitCost);
								itemPlan.put("uomId", unitCost);
								itemPlanList.add(itemPlan);
							}
						}
					}
				}
			}
			itemFacility.put(facilityId, productFacility);
		}
		mapReturn.put("grandTotal", grandTotal);
		mapReturn.put("itemFacility", itemFacility);
		mapReturn.put("itemPlanList", itemPlanList);
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> prepareCaculatedPO(JSONArray itemList, List<String> facilityList) {
		Map<String, Object> mapReturn = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(facilityList)) {
			for (String facilityId : facilityList) {
				List<Map<String, Object>> productFacility = FastList.newInstance();
				for (int index = 0; index < itemList.size(); index++) {
					JSONObject productJson = itemList.getJSONObject(index);
					List<Map<String, Object>> rowDetails = (List<Map<String, Object>>) productJson.get("rowDetail");
					if (UtilValidate.isNotEmpty(rowDetails)) {
						for (Map<String, Object> rowDetail : rowDetails) {
							String facilityIdRowDetail = (String) rowDetail.get("facilityId");
							if (facilityId.equals(facilityIdRowDetail)) {
								String productId = (String) rowDetail.get("productId");
								Map<String, Object> productFacilityTmp = FastMap.newInstance();
								productFacilityTmp.put("productId", productId);
								BigDecimal quantity = BigDecimal.ZERO;
								BigDecimal unitCost = BigDecimal.ZERO;
								quantity = (BigDecimal) rowDetail.get("quantity");
								unitCost = (BigDecimal) rowDetail.get("unitCost");
								productFacilityTmp.put("quantity", quantity);
								productFacilityTmp.put("unitCost", unitCost);
								productFacilityTmp.put("facilityId", facilityId);
								productFacility.add(productFacilityTmp);
							}
						}
					}
				}
				mapReturn.put(facilityId, productFacility);
			}
		}
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static String createPurchasePlan(HttpServletRequest request, HttpServletResponse response)
			throws GenericTransactionException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		boolean beganTx = TransactionUtil.begin(7200);
		try {
			String supplierPartyId = request.getParameter("supplierPartyId");
			String hasMutilPO = (String) request.getParameter("hasMutilPO");
			String productListParam = request.getParameter("productList");
			String mainFacilityId = (String) request.getParameter("mainFacilityId");
			
			if (UtilValidate.isNotEmpty(request.getParameter("shipBeforeDate"))){
				Long shipBeforeDate = new Long ((String)request.getParameter("shipBeforeDate"));
				Timestamp x = new Timestamp(shipBeforeDate);
				
				Date before = new Date(x.getTime()); 
				Calendar cal = Calendar.getInstance();
				int year = 0;
				int month = 0;
				int day = 0;
				if (before != null){
					cal.setTime(before);
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
				    day = cal.get(Calendar.DAY_OF_MONTH);
				}
				String beforeStr = String.valueOf(year);
				if (month < 10){
					String m = "0" + String.valueOf(month);
					beforeStr = beforeStr + "-" + m;
				} else {
					beforeStr = beforeStr + "-" + String.valueOf(month);
				}
				if (day < 10){
					String d = "0" + String.valueOf(day);
					beforeStr = beforeStr + "-" + d;
				} else {
					beforeStr = beforeStr + "-" + String.valueOf(day);
				} 
				request.setAttribute("shipBeforeDate", beforeStr);
			}
			if (UtilValidate.isNotEmpty(request.getParameter("shipAfterDate"))){
				Long shipAfterDate = new Long ((String)request.getParameter("shipAfterDate"));
				Timestamp y = new Timestamp(shipAfterDate);
				Date after = new Date(y.getTime()); 
				Calendar cal = Calendar.getInstance();
				int year = 0;
				int month = 0;
				int day = 0;
				if (after != null){
					cal.setTime(after);
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
				    day = cal.get(Calendar.DAY_OF_MONTH);
				}
				String afterStr = String.valueOf(year);
				if (month < 10){
					String m = "0" + String.valueOf(month);
					afterStr = afterStr + "-" + m;
				} else {
					afterStr = afterStr + "-" + String.valueOf(month);
				}
				if (day < 10){
					String d = "0" + String.valueOf(day);
					afterStr = afterStr + "-" + d;
				} else {
					afterStr = afterStr + "-" + String.valueOf(day);
				}
				
				request.setAttribute("shipAfterDate", afterStr);
			}
			EntityCondition facilityCond = null;
			List<GenericValue> facilityList = delegator.findList("Facility", facilityCond, null,
					UtilMisc.toList("facilityId"), null, false);

			BigDecimal grandTotal = BigDecimal.ZERO;

			List<Map<String, Object>> itemPlanList = FastList.newInstance();
			Map<String, Object> itemFacility = FastMap.newInstance();
			if (UtilValidate.isNotEmpty(productListParam)) {
				Map<String, Object> mapReturn = calculatePlan(facilityList, productListParam);
				itemFacility = (Map<String, Object>) mapReturn.get("itemFacility");
				itemPlanList = (List<Map<String, Object>>) mapReturn.get("itemPlanList");
				grandTotal = (BigDecimal) mapReturn.get("grandTotal");
			}
			// create plan header
			String planTypeId = "PLAN_ORDER";
			String planId = createPlanHeader(request, planTypeId, null, supplierPartyId, grandTotal);
			// create plan item
			createPlanItem(request, planId, itemPlanList);
			String currencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "VND");
			if (UtilValidate.isNotEmpty(hasMutilPO)) {
				if (hasMutilPO.equals("true")) {
					for (GenericValue facility : facilityList) {
						String facilityId = facility.getString("facilityId");
						String onwerPartyId = facility.getString("ownerPartyId");
						List<Map<String, Object>> productFacilityList = (List<Map<String, Object>>) itemFacility
								.get(facilityId);
						if (!productFacilityList.isEmpty()){
							createPurchaseOrder(request, response, facilityId, supplierPartyId, onwerPartyId, currencyUomId,
									planId, productFacilityList);
							ShoppingCartEvents.destroyCart(request, response);
						}
					}
				} else {
					GenericValue mainFacility = delegator.findOne("Facility",
							UtilMisc.toMap("facilityId", mainFacilityId), false);
					if (UtilValidate.isNotEmpty(mainFacility)) {
						String ownerPartyId = mainFacility.getString("ownerPartyId");
						createPurchaseOrder(request, response, mainFacilityId, supplierPartyId, ownerPartyId,
								currencyUomId, planId, itemPlanList);
						ShoppingCartEvents.destroyCart(request, response);
					}
				}
			}
			request.setAttribute("planId", planId);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return "error";
		}
		TransactionUtil.commit(beganTx);
		return "success";
	}

	public static void createPurchaseOrder(HttpServletRequest request, HttpServletResponse response, String facilityId,
			String supplierPartyId, String company, String currencyUomId, String planId,
			List<Map<String, Object>> listProductsSelectedCaculatedPO) throws Exception {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		String productStoreIdTmp = (String) request.getAttribute("productStoreId");
		String productStoreId = null;
		if (UtilValidate.isEmpty(productStoreIdTmp)) {
			// get productStoreId according to facilityId
			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			if (UtilValidate.isNotEmpty(facility)) {
				if (UtilValidate.isNotEmpty(facility.getString("productStoreId"))){
					productStoreId = facility.getString("productStoreId");
				} else {
					List<GenericValue> list = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);
					list = EntityUtil.filterByDate(list);
					if (!list.isEmpty()){
						productStoreId = list.get(0).getString("productStoreId");
					}
				}
			}
		} else {
			productStoreId = productStoreIdTmp;
		}
		
		session.setAttribute("productStoreId", productStoreId);
		request.setAttribute("productStoreId", productStoreId);
		
		ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
		
		// set shipping
		String contactMechId = null;
		List<Map<String, Object>> facilityContactMechValueMaps = ContactMechWorker
				.getFacilityContactMechValueMaps(delegator, facilityId, false, null);
		if (UtilValidate.isNotEmpty(facilityContactMechValueMaps)) {
			Map<String, Object> facilityContactMechValueMap = facilityContactMechValueMaps.get(0);
			GenericValue postalAddress = (GenericValue) facilityContactMechValueMap.get("postalAddress");
			if (UtilValidate.isNotEmpty(postalAddress)) {
				contactMechId = postalAddress.getString("contactMechId");
				if (UtilValidate.isNotEmpty(contactMechId)) {
					cart.setShippingContactMechId(0, contactMechId);
				}
			}
		}
		String channelId = null;
		GenericValue store = delegator.findOne("ProductStore", false, UtilMisc.toMap("productStoreId", productStoreId));
		if (UtilValidate.isNotEmpty(store)){
			channelId = store.getString("defaultSalesChannelEnumId");
			if (UtilValidate.isEmpty(channelId)){
				channelId = "UNKNWN_SALES_CHANNEL";
			}
		}
		String orderId = delegator.getNextSeqId("OrderHeader");
		cart.setOrderType("PURCHASE_ORDER");
		cart.setAttribute("originOrderId", "PURCHASE_ORDER");
		cart.setSupplierPartyId(0, supplierPartyId);
		cart.setOrderPartyId(supplierPartyId);
		cart.setChannelType(channelId);
		cart.setBillToCustomerPartyId(company);
		cart.setShipmentMethodTypeId(0, "NO_SHIPPING");
		cart.setCarrierPartyId(0, "_NA_");
		cart.setMaySplit(0, Boolean.FALSE);
		cart.setIsGift(0, Boolean.FALSE);
		cart.setAttribute("addpty", "Y");
		cart.setAttribute("supplierPartyId", supplierPartyId);
		cart.setBillFromVendorPartyId(supplierPartyId);
		cart.setShipGroupFacilityId(0, facilityId);
		cart.setFacilityId(facilityId);
		if (UtilValidate.isNotEmpty(productStoreId)) {
			cart.setProductStoreId(productStoreId);
		}
		cart.setOrderId(orderId);
		/* cart.setAllShippingContactMechId(contactMechId); */
		for (Map<String, Object> productCaculated : listProductsSelectedCaculatedPO) {
			String productId = (String) productCaculated.get("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			String quantityUomId = null;
			if (UtilValidate.isNotEmpty(product)) {
				quantityUomId = product.getString("quantityUomId");
			}
			request.setAttribute("add_product_id", productId);
			BigDecimal unitCost = (BigDecimal) productCaculated.get("unitCost");
			BigDecimal quantity = (BigDecimal) productCaculated.get("quantity");
			String comments = (String) productCaculated.get("comments");
			if (UtilValidate.isNotEmpty(unitCost)) {
				request.setAttribute("price", unitCost.toString());
			} else {
				request.setAttribute("price", "0");
			}
			if (UtilValidate.isNotEmpty(quantity)) {
				request.setAttribute("quantity", quantity.toString());
			} else {
				request.setAttribute("quantity", "0");
			}
			request.setAttribute("itemComment", comments);
			request.setAttribute("uomId", quantityUomId);
			String addResult = ShoppingCartEvents.addToCart(request, response);
			if ("error".equals(addResult)) {
				throw new Exception((String) request.getAttribute("_ERROR_MESSAGE_"));
			}
		}
		/*
		 * CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher,
		 * delegator, cart);
		 */
		ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
		cartHelper.setCurrency(currencyUomId);
		String addResult = PosCheckOutEvents.createOrder(request, response);
		if ("error".equals(addResult)) {
			throw new Exception((String) request.getAttribute("_ERROR_MESSAGE_"));
		}
		// set planId for the order is just created
		GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		if (UtilValidate.isNotEmpty(orderHeader)) {
			orderHeader.set("planId", planId);
			orderHeader.store();
			dispatcher.runSync("resetGrandTotal", UtilMisc.toMap("userLogin", system, "orderId", orderId));
		}
	}
}
