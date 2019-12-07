package com.olbius.baselogistics.returns;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
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
import org.ofbiz.order.order.OrderReturnServices;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.LogisticsServices;
import com.olbius.baselogistics.util.JsonUtil;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.basesales.util.NotificationWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.product.util.FacilityUtil;
import com.olbius.product.util.InventoryUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;

public class ReturnServices {
	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
    public static final int decimals = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveReturnItems(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<Object> listJson = (List<Object>)context.get("listReturnItems");
		Timestamp datetimeReceived = new Timestamp((Long)context.get("datetimeReceived"));
		String facilityId = (String)context.get("facilityId");
		String orderId = (String)context.get("orderId");
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		String currencyUomId = orderHeader.getString("currencyUom");
		String stringJson = "["+(String)listJson.get(0)+"]";
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<GenericValue> orderRoleTo = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
		List<GenericValue> orderRoleFrom = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
		
		String fromPartyId = null;
		if (!orderRoleTo.isEmpty()){
			fromPartyId = orderRoleTo.get(0).getString("partyId");
		}
		String toPartyId = null;
		if (!orderRoleFrom.isEmpty()){
			toPartyId = orderRoleFrom.get(0).getString("partyId");
		}
		JSONArray lists = JSONArray.fromObject(stringJson);
		List<Map<String, Object>> listItemReceives = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < lists.size(); i++){
			JSONObject item = lists.getJSONObject(i);
			Map<String, Object> map = FastMap.newInstance();
			GenericValue product = null;
			String uomFromId = null;
			if (item.containsKey("productId")){
				if (item.getString("productId") != null && !"".equals(item.getString("productId"))){
					map.put("productId", item.getString("productId"));
					product = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.getString("productId")));
					map.put("description", product.getString("productName"));
				}
			}
			if (item.containsKey("quantityUomId")){
				if (item.getString("quantityUomId") != null && !"".equals(item.getString("quantityUomId"))){
					uomFromId = item.getString("quantityUomId");
				}
			}
			if (item.containsKey("quantity")){
				if (item.getString("quantity") != null && !"".equals(item.getString("quantity"))){
					BigDecimal convert = LogisticsProductUtil.getConvertPackingNumber(delegator, product.getString("productId"), uomFromId, product.getString("quantityUomId"));
					map.put("returnQuantity", new BigDecimal(item.getString("quantity")).multiply(convert));
				}
			}
			if (item.containsKey("manufacturedDate")){
				if (UtilValidate.isNotEmpty(item.getString("manufacturedDate"))){
					map.put("datetimeManufactured", new Timestamp((new Long(item.getString("manufacturedDate")))));
				}
			}
			if (item.containsKey("expiredDate")){
				if (UtilValidate.isNotEmpty(item.getString("expiredDate"))){
					map.put("expiredDate", new Timestamp((new Long(item.getString("expiredDate")))));
				}
			}
			if (item.containsKey("inventoryItemStatusId")){
				if (item.getString("inventoryItemStatusId") != null && !"".equals(item.getString("inventoryItemStatusId")) && !"null".equals(item.getString("inventoryItemStatusId"))){
					map.put("expectedItemStatus", item.getString("inventoryItemStatusId"));
					map.put("isGood", false);
				} else {
					map.put("isGood", true);
					map.put("expectedItemStatus", "INV_NS_RETURNED");
				}
			} 
			if (item.containsKey("returnReasonId")){
				if (item.getString("returnReasonId") != null && !"".equals(item.getString("returnReasonId"))){
					map.put("returnReasonId", item.getString("returnReasonId"));
				}
			}
			if (item.containsKey("inventoryItemId")){
				if (item.getString("inventoryItemId") != null && !"".equals(item.getString("inventoryItemId"))){
					GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", item.getString("inventoryItemId")));
					map.put("lotId", inv.getString("lotId"));
				}
			}
			String deliveryId = null;
			String deliveryItemSeqId = null;
			if (item.containsKey("deliveryId")){
				if (item.getString("deliveryId") != null && !"".equals(item.getString("deliveryId"))){
					deliveryId = item.getString("deliveryId");
				}
			}
			if (item.containsKey("deliveryItemSeqId")){
				if (item.getString("deliveryItemSeqId") != null && !"".equals(item.getString("deliveryItemSeqId"))){
					deliveryItemSeqId = item.getString("deliveryItemSeqId");
				}
			}
			
			GenericValue deliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
			String orderItemSeqId = deliveryItem.getString("fromOrderItemSeqId");
			map.put("returnTypeId", "RTN_REFUND");
			map.put("returnItemTypeId", "RET_FPROD_ITEM");
			map.put("orderId", orderId);
			map.put("orderItemSeqId", orderItemSeqId);
			map.put("statusId", "RETURN_REQUESTED");
			map.put("includeAdjustments", "Y");
			GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
			map.put("returnPrice", orderItem.getBigDecimal("unitPrice"));
			listItemReceives.add(map);
		}
		String returnId = null;
		if (!listItemReceives.isEmpty()){
			// Create return header
			Map<String, Object> mapReturnNeedsReceive = FastMap.newInstance();
			mapReturnNeedsReceive.put("returnHeaderTypeId", "CUSTOMER_RETURN");
			mapReturnNeedsReceive.put("statusId", "RETURN_REQUESTED");
			mapReturnNeedsReceive.put("createdBy", userLogin.getString("partyId"));
			mapReturnNeedsReceive.put("fromPartyId", fromPartyId);
			mapReturnNeedsReceive.put("toPartyId", toPartyId);
			mapReturnNeedsReceive.put("entryDate", datetimeReceived);
			mapReturnNeedsReceive.put("destinationFacilityId", facilityId);
			mapReturnNeedsReceive.put("needsInventoryReceive", "Y");
			mapReturnNeedsReceive.put("currencyUomId", currencyUomId);
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			mapReturnNeedsReceive.put("userLogin", system);
			
			Map<String, Object> mapTmp = dispatcher.runSync("createReturnHeader", mapReturnNeedsReceive);
			returnId = (String)mapTmp.get("returnId");
			
			// Create return item
			Map<String, Object> mapItemStatus = FastMap.newInstance();
			for (Map<String, Object> item : listItemReceives){
				item.put("returnId", returnId);
				item.put("userLogin", system);
				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.get("orderId"), "orderItemSeqId", item.get("orderItemSeqId")));
				item.put("returnAmount", orderItem.getBigDecimal("selectedAmount"));
				
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.get("productId")));
				String requireAmount = product.getString("requireAmount");
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)){
					BigDecimal returnPrice = (BigDecimal) item.get("returnPrice");
					returnPrice = returnPrice.divide(orderItem.getBigDecimal("selectedAmount"), decimals,rounding);
					item.put("returnPrice", returnPrice);
				}
				//item.put("unitPruct", value)
				Boolean isGood = false;
				if ((Boolean)item.get("isGood")){
					isGood = true;
				}
				item.remove("isGood");
				Map<String, Object> mapItemTmp = dispatcher.runSync("createReturnItem", item);
				String returnItemSeqId = (String)mapItemTmp.get("returnItemSeqId");
				if (isGood){
					mapItemStatus.put(returnItemSeqId, true);
				} else {
					mapItemStatus.put(returnItemSeqId, false);
				}
			}
			
			// Receive inventory item 
			Map<String, Object> mapReceive = FastMap.newInstance();
			mapReceive.put("returnId", returnId);
			mapReceive.put("userLogin", system);
			mapReceive.put("needsInventoryReceive", "N");
			mapReceive.put("statusId", "RETURN_ACCEPTED");
			dispatcher.runSync("updateReturnHeader", mapReceive);
			
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> returnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
			for (GenericValue returnItem : returnItems) {
				Map<String, Object> item = FastMap.newInstance(); 
				String productId = returnItem.getString("productId");
				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", returnItem.getString("orderItemSeqId")));
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
				String requireAmount = product.getString("requireAmount");
				
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount) && UtilValidate.isNotEmpty(selectedAmount) && selectedAmount.compareTo(BigDecimal.ZERO) > 0) {
					item.put("quantityAccepted", BigDecimal.ONE);
					item.put("amountAccepted", returnItem.getBigDecimal("returnQuantity").multiply(selectedAmount));
				} else {
					item.put("quantityAccepted", returnItem.getBigDecimal("returnQuantity"));
				}
				
				item.put("returnItemSeqId", returnItem.getString("returnItemSeqId"));
				item.put("returnId", returnId);
				item.put("quantityExcess", BigDecimal.ZERO);
				item.put("quantityRejected", BigDecimal.ZERO);
				item.put("quantityQualityAssurance", BigDecimal.ZERO);
				item.put("expireDate", returnItem.getTimestamp("expiredDate"));
				item.put("ownerPartyId", company);
				item.put("datetimeReceived", datetimeReceived);
				item.put("datetimeManufactured", returnItem.getTimestamp("datetimeManufactured"));
				item.put("lotId", returnItem.getString("lotId"));
				item.put("statusId", returnItem.getString("expectedItemStatus"));
				item.put("userLogin", system);
				
				item.put("productId", productId);
				item.put("facilityId", facilityId);
				item.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				List<GenericValue> orderItemIsuances = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", returnItem.getString("orderItemSeqId"))), null, null, null, false);
				List<GenericValue> listItemRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
				if (!listItemRoles.isEmpty()){
					String partyId = listItemRoles.get(0).getString("partyId");
					GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", partyId));
					if (UtilValidate.isNotEmpty(partyAcctgPreference) && "COGS_AVG_COST".equals(partyAcctgPreference.getString("cogsMethodId"))){
						if (!orderItemIsuances.isEmpty()){
							BigDecimal unitCost = BigDecimal.ZERO;
							BigDecimal totalInvUnitPrice = BigDecimal.ZERO;
							BigDecimal purCost = BigDecimal.ZERO;
							BigDecimal totalInvPurCost= BigDecimal.ZERO;
							for (GenericValue itemIssuance : orderItemIsuances) {
								Timestamp issuedDatetime = itemIssuance.getTimestamp("issuedDateTime");
								GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", itemIssuance.getString("inventoryItemId")));
								String facilityExptId = inv.getString("facilityId");
								String averageCostType = "SIMPLE_AVG_COST";
								List<GenericValue> listAverageCost = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(UtilMisc.toMap("productAverageCostTypeId", averageCostType, "organizationPartyId", partyId, "productId", productId, "facilityId", facilityExptId)), null, null, null, false);
								BigDecimal averageCost = BigDecimal.ZERO;
								BigDecimal averagePurCost = BigDecimal.ZERO;
								for (GenericValue avr : listAverageCost) {
									if ((avr.getTimestamp("fromDate").before(issuedDatetime) && UtilValidate.isEmpty(avr.getTimestamp("thruDate"))) || (avr.getTimestamp("fromDate").before(issuedDatetime) && UtilValidate.isNotEmpty(avr.getTimestamp("thruDate")) && avr.getTimestamp("thruDate").after(issuedDatetime))){
										averageCost = avr.getBigDecimal("averageCost"); 
										averagePurCost = avr.getBigDecimal("averagePurCost"); 
										break;
									}
								}
								totalInvUnitPrice = totalInvUnitPrice.add(averageCost);
								totalInvPurCost = totalInvPurCost.add(averagePurCost);
							}
							unitCost = totalInvUnitPrice.divide(new BigDecimal(orderItemIsuances.size()), 3, RoundingMode.HALF_UP);
							purCost = totalInvPurCost.divide(new BigDecimal(orderItemIsuances.size()), 3, RoundingMode.HALF_UP);
							item.put("unitCost", unitCost);
							item.put("purCost", purCost);
						} else {
							item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
							item.put("purCost", orderItem.getBigDecimal("unitPrice"));
						}
					} else {
						item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
						item.put("purCost", orderItem.getBigDecimal("unitPrice"));
					}
				} else {
					item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
					item.put("purCost", orderItem.getBigDecimal("unitPrice"));
				}
				
				List<GenericValue> returnItemShipments = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItem.getString("returnItemSeqId"))), null, null, null, false);
				item.put("shipmentId", returnItemShipments.get(0).getString("shipmentId"));
				try {
					dispatcher.runSync("receiveInventoryProduct", item);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error " + e.toString());
				}
			}
			
			mapReceive = FastMap.newInstance();
			mapReceive.put("returnId", returnId);
			mapReceive.put("userLogin", system);
			mapReceive.put("statusId", "RETURN_COMPLETED");
			mapReceive.put("needsInventoryReceive", "N");
			dispatcher.runSync("updateReturnHeader", mapReceive);
			
			List<GenericValue> listInvItemDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
			if (!listInvItemDetail.isEmpty()){
				for (GenericValue invDetail : listInvItemDetail){
					String returnItemSeqId = invDetail.getString("returnItemSeqId");
					Boolean isGood= false;
					if (mapItemStatus.containsKey(returnItemSeqId)){
						isGood = (Boolean)mapItemStatus.get(returnItemSeqId);
					}
					if (isGood){
						GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", invDetail.getString("inventoryItemId")));
						if (UtilValidate.isNotEmpty(inv)){
							if (UtilValidate.isNotEmpty(inv.get("statusId"))){
								inv.set("statusId", null);
								inv.store(); 
							}
						}
					}
				}
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("returnId", returnId);
		return result;
	}
	
	public static Map<String, Object> returnSalesOrder(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String orderId = (String)context.get("orderId");
		String returnReasonId = (String)context.get("returnReasonId");
		String facilityId = (String)context.get("facilityId");
		String expectedItemStatus = (String)context.get("inventoryStatusId");
		Boolean isGood = false;
		if (expectedItemStatus == null || "".equals(expectedItemStatus)){
			isGood = true;
			expectedItemStatus = "INV_NS_RETURNED";
		}
		Timestamp datetimeReceived = new Timestamp((Long)context.get("datetimeReceived"));
		Timestamp datetimeDelivered = new Timestamp((Long)context.get("datetimeDelivered"));
		GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", orderId, "statusId", "DELI_ITEM_EXPORTED")), null, null, null, false);
		List<Map<String, Object>> listReturnItems = new ArrayList<Map<String, Object>>();
		List<GenericValue> orderRoleTo = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
		List<GenericValue> orderRoleFrom = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
		String returnId = null;
		if (!listDeliveryItems.isEmpty()){
			Map<String, Object> mapReturnHeader= FastMap.newInstance();
			mapReturnHeader.put("returnHeaderTypeId", "CUSTOMER_RETURN");
			mapReturnHeader.put("statusId", "RETURN_REQUESTED");
			mapReturnHeader.put("createdBy", userLogin.getString("partyId"));
			mapReturnHeader.put("fromPartyId", orderRoleTo.get(0).getString("partyId"));
			mapReturnHeader.put("toPartyId", orderRoleFrom.get(0).getString("partyId"));
			mapReturnHeader.put("entryDate", datetimeReceived);
			mapReturnHeader.put("destinationFacilityId", facilityId);
			mapReturnHeader.put("needsInventoryReceive", "Y");
			mapReturnHeader.put("currencyUomId", order.getString("currencyUom"));
			mapReturnHeader.put("userLogin", userLogin);
			
			Map<String, Object> mapTmp = dispatcher.runSync("createReturnHeader", mapReturnHeader);
			returnId = (String)mapTmp.get("returnId");
			
			for (GenericValue item : listDeliveryItems){
				Map<String, Object> map = FastMap.newInstance();
				map.put("productId", item.getString("productId"));
				map.put("description", item.getString("productName"));
				map.put("returnQuantity", item.getBigDecimal("actualExportedQuantity"));
				GenericValue invItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", item.getString("inventoryItemId")));	
				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", item.getString("fromOrderItemSeqId")));
				map.put("returnPrice", orderItem.getBigDecimal("unitPrice"));
				map.put("datetimeManufactured", invItem.getTimestamp("datetimeManufactured"));
				map.put("expiredDate", invItem.getTimestamp("expireDate"));
				map.put("lotId", invItem.getString("lotId"));
				map.put("expectedItemStatus", expectedItemStatus);
				map.put("returnReasonId", returnReasonId);
				map.put("returnTypeId", "RTN_REFUND");
				map.put("returnItemTypeId", "RET_FPROD_ITEM");
				map.put("orderId", orderId);
				map.put("orderItemSeqId", item.getString("fromOrderItemSeqId"));
				map.put("returnAmount", item.getBigDecimal("selectedAmount"));
				map.put("statusId", "RETURN_REQUESTED");
				map.put("includeAdjustments", "Y");
				
				listReturnItems.add(map);
			}
			
			for (Map<String, Object> item : listReturnItems){
				item.put("returnId", returnId);
				item.put("userLogin", userLogin);
				dispatcher.runSync("createReturnItem", item);
			}
			
			// Receive inventory item 
			Map<String, Object> mapReceive = FastMap.newInstance();
			mapReceive.put("returnId", returnId);
			mapReceive.put("userLogin", userLogin);
			mapReceive.put("needsInventoryReceive", "N");
			mapReceive.put("statusId", "RETURN_ACCEPTED");
			dispatcher.runSync("updateReturnHeader", mapReceive);
			
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> returnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			for (GenericValue returnItem : returnItems) {
				Map<String, Object> item = FastMap.newInstance(); 
				item.put("returnItemSeqId", returnItem.getString("returnItemSeqId"));
				item.put("returnId", returnId);
				item.put("quantityAccepted", returnItem.getBigDecimal("returnQuantity"));
				
				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", returnItem.getString("orderItemSeqId")));
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", orderItem.getString("productId")));
				
				if (UtilValidate.isNotEmpty(product.get("requireAmount")) && "Y".equals((String)product.get("requireAmount")) && UtilValidate.isNotEmpty(orderItem.get("selectedAmount")) && orderItem.getBigDecimal("selectedAmount").compareTo(BigDecimal.ZERO) > 0) {
					item.put("amountAccepted", returnItem.getBigDecimal("returnQuantity").multiply(orderItem.getBigDecimal("selectedAmount")));
				}
				
				item.put("quantityExcess", BigDecimal.ZERO);
				item.put("quantityRejected", BigDecimal.ZERO);
				item.put("quantityQualityAssurance", BigDecimal.ZERO);
				item.put("expireDate", returnItem.getTimestamp("expiredDate"));
				item.put("ownerPartyId", company);
				item.put("datetimeReceived", datetimeReceived);
				item.put("datetimeManufactured", returnItem.getTimestamp("datetimeManufactured"));
				item.put("lotId", returnItem.getString("lotId"));
				item.put("statusId", returnItem.getString("expectedItemStatus"));
				item.put("userLogin", system);
				String productId = returnItem.getString("productId");
				item.put("productId", productId);
				item.put("facilityId", facilityId);
				item.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				List<GenericValue> orderItemIsuances = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", returnItem.getString("orderItemSeqId"))), null, null, null, false);
				List<GenericValue> listItemRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
				if (!listItemRoles.isEmpty()){
					String partyId = listItemRoles.get(0).getString("partyId");
					GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", partyId));
					if (UtilValidate.isNotEmpty(partyAcctgPreference) && "COGS_AVG_COST".equals(partyAcctgPreference.getString("cogsMethodId"))){
						if (!orderItemIsuances.isEmpty()){
							BigDecimal unitCost = BigDecimal.ZERO;
							BigDecimal totalInvUnitPrice = BigDecimal.ZERO;
							BigDecimal purCost = BigDecimal.ZERO;
							BigDecimal totalInvPurCost= BigDecimal.ZERO;
							for (GenericValue itemIssuance : orderItemIsuances) {
								Timestamp issuedDatetime = itemIssuance.getTimestamp("issuedDateTime");
								GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", itemIssuance.getString("inventoryItemId")));
								String facilityExptId = inv.getString("facilityId");
								String averageCostType = "SIMPLE_AVG_COST";
								List<GenericValue> listAverageCost = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(UtilMisc.toMap("productAverageCostTypeId", averageCostType, "organizationPartyId", partyId, "productId", productId, "facilityId", facilityExptId)), null, null, null, false);
								BigDecimal averageCost = BigDecimal.ZERO;
								BigDecimal averagePurCost = BigDecimal.ZERO;
								for (GenericValue avr : listAverageCost) {
									if ((avr.getTimestamp("fromDate").before(issuedDatetime) && UtilValidate.isEmpty(avr.getTimestamp("thruDate"))) || (avr.getTimestamp("fromDate").before(issuedDatetime) && UtilValidate.isNotEmpty(avr.getTimestamp("thruDate")) && avr.getTimestamp("thruDate").after(issuedDatetime))){
										averageCost = avr.getBigDecimal("averageCost"); 
										averagePurCost = avr.getBigDecimal("averagePurCost"); 
										break;
									}
								}
								totalInvUnitPrice = totalInvUnitPrice.add(averageCost);
								totalInvPurCost = totalInvPurCost.add(averagePurCost);
							}
							unitCost = totalInvUnitPrice.divide(new BigDecimal(orderItemIsuances.size()), 3, RoundingMode.HALF_UP);
							purCost = totalInvPurCost.divide(new BigDecimal(orderItemIsuances.size()), 3, RoundingMode.HALF_UP);
							item.put("unitCost", unitCost);
							item.put("purCost", purCost);
						} else {
							item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
							item.put("purCost", orderItem.getBigDecimal("unitPrice"));
						}
					} else {
						item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
						item.put("purCost", orderItem.getBigDecimal("unitPrice"));
					}
				} else {
					item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
					item.put("purCost", orderItem.getBigDecimal("unitPrice"));
				}
				
				List<GenericValue> returnItemShipmentTmps = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItem.getString("returnItemSeqId"))), null, null, null, false);
				if (!returnItemShipmentTmps.isEmpty()){
					item.put("shipmentId", returnItemShipmentTmps.get(0).getString("shipmentId"));
				}
				try {
					dispatcher.runSync("receiveInventoryProduct", item);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error " + e.toString());
				}
			}
			
			Map<String, Object> mapCompleted = FastMap.newInstance();
			mapCompleted.put("returnId", returnId);
			mapCompleted.put("userLogin", userLogin);
			mapReceive.put("needsInventoryReceive", "N");
			mapCompleted.put("statusId", "RETURN_COMPLETED");
			dispatcher.runSync("updateReturnHeader", mapCompleted);
			if (isGood){
				List<GenericValue> listInvItemReturns = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
				if (!listInvItemReturns.isEmpty()){
					for (GenericValue invDetail : listInvItemReturns){
						GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", invDetail.getString("inventoryItemId")));
						if (UtilValidate.isNotEmpty(inv)){
							if (UtilValidate.isNotEmpty(inv.get("statusId"))){
								inv.set("statusId", null);
								inv.store(); 
							}
						}
					}
				}
			}

			// update delivery
			List<GenericValue> listDeliveries = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "statusId", "DLV_EXPORTED")), null, null, null, false);
			for (GenericValue dlv : listDeliveries){
				List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", dlv.getString("deliveryId"))), null, null, null, false);
				List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
				if (!listDlvItems.isEmpty()){
		       		for (GenericValue item : listDlvItems){
		       			Map<String, Object> map = FastMap.newInstance();
		                map.put("fromOrderId", orderId);
		        		map.put("fromOrderItemSeqId", item.getString("fromOrderItemSeqId"));
		        		map.put("deliveryId", dlv.getString("deliveryId"));
		        		map.put("actualDeliveredQuantity", BigDecimal.ZERO);
		        		map.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
		        		map.put("inventoryItemId", item.getString("inventoryItemId"));
		        		listItems.add(map);
		       		}
		       	} else {
		       		return ServiceUtil.returnError("Delivery Item Not Found!");
		       	}
		       	String listDlvItemJSON = JsonUtil.convertListMapToJSON(listItems);
		    	Map<String, Object> mapUpdateDelivery = FastMap.newInstance();
		       	mapUpdateDelivery.put("deliveryId", dlv.getString("deliveryId"));
		       	mapUpdateDelivery.put("actualArrivalDate", datetimeDelivered.getTime());
		       	mapUpdateDelivery.put("listDeliveryItems", listDlvItemJSON);
		       	mapUpdateDelivery.put("userLogin", userLogin);
		       	dispatcher.runSync("updateDeliveryItemList", mapUpdateDelivery);
			}
		} else {
			return ServiceUtil.returnError("No Delivery Item found!");
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("returnId", returnId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductReturn(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	GenericValue userLogin = (GenericValue)context.get("userLogin");
       	String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
       	if (parameters.get("returnHeaderTypeId") != null && parameters.get("returnHeaderTypeId").length > 0){
       		String returnHeaderTypeId = (String)parameters.get("returnHeaderTypeId")[0];
           	mapCondition.put("returnHeaderTypeId", returnHeaderTypeId);
           	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
        	EntityCondition orgCond = null;
        	if ("CUSTOMER_RETURN".equals(returnHeaderTypeId)){
        		orgCond = EntityCondition.makeCondition("toPartyId", EntityOperator.EQUALS, company);
        	} else if ("VENDOR_RETURN".equals(returnHeaderTypeId)){
        		orgCond = EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, company);
        	}
        	if (UtilValidate.isNotEmpty(orgCond)){
        		listAllConditions.add(orgCond);
        	}
       	} else {
       		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
        	EntityCondition orgCond1 = EntityCondition.makeCondition("toPartyId", EntityOperator.EQUALS, company);
        	EntityCondition orgCond2 = EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, company);
        	List<EntityCondition> listOrgCond = UtilMisc.toList(orgCond1, orgCond2);
        	EntityCondition orgCondTotal = EntityCondition.makeCondition(listOrgCond, EntityOperator.OR);
        	if (UtilValidate.isNotEmpty(orgCondTotal)){
        		listAllConditions.add(orgCondTotal);
        	}
       	}
       	
       	if (parameters.get("statusId") != null && parameters.get("statusId").length > 0){
       		String statusId = (String)parameters.get("statusId")[0];
       		if (UtilValidate.isNotEmpty(statusId)){
	       		EntityCondition sttCond = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
	    		listAllConditions.add(sttCond);
       		}
       	}
       	
       	/*List<String> listFacilityIds = new ArrayList<String>();
       	Security security = ctx.getSecurity();
       	String admin = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "permission.admin");
		if (security.hasPermission(admin, userLogin)) {
			try {
				List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", company)), null, null, null, false);
				listFacilityIds = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", true);
			} catch (Exception e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: get FacilityParty error");
			}
		} else {
	    	try {
	    		listFacilityIds = FacilityUtil.getFacilityIdByRole(delegator, userLogin.getString("partyId"), UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
	    	} catch (GenericEntityException e){
	    		Debug.logError(e.toString(), module);
	    		return ServiceUtil.returnError("OLBIUS: get list facility party error");
	    	}
		}
    	EntityCondition idCond1 = EntityCondition.makeCondition("destinationFacilityId", EntityOperator.IN, listFacilityIds);
    	EntityCondition idCond2 = EntityCondition.makeCondition("destinationFacilityId", EntityOperator.EQUALS, null);
    	EntityCondition idCondAll = EntityCondition.makeCondition(UtilMisc.toList(idCond1, idCond2), EntityOperator.OR);
    	listAllConditions.add(idCondAll);
    	*/
       	if (listSortFields.isEmpty()){
       		listSortFields.add("-returnId");
       	}
    	try {
    		listIterator = delegator.find("ReturnHeaderDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProductReturn service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getReturnItemDetail(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
       	Delegator delegator = ctx.getDelegator();
       	String returnId = (String)context.get("returnId");
       	String facilityId = (String)context.get("facilityId");
    	GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
    	String returnHeaderTypeId = returnHeader.getString("returnHeaderTypeId");
    	List<GenericValue> listReturnItems = new ArrayList<GenericValue>();
    	EntityCondition Cond1 = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
    	List<Map<String, Object>> listReturns = new ArrayList<Map<String, Object>>();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	try {
    		if ("CUSTOMER_RETURN".equals(returnHeaderTypeId)){
    			EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "RETURN_CANCELLED");
    	    	List<EntityCondition> listAllConditions = UtilMisc.toList(Cond1, Cond2);
    			listReturnItems = delegator.findList("CustomerReturnItemDetail", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
    			for (GenericValue item : listReturnItems) {
					BigDecimal returnPrice = OrderReturnServices.getReturnItemInitialCost(delegator, returnId, item.getString("returnItemSeqId"));
					item.put("returnPrice", returnPrice);
				}
    		} else if ("VENDOR_RETURN".equals(returnHeaderTypeId)){
    			EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SUP_RETURN_CANCELLED");
    	    	List<EntityCondition> listAllConditions = UtilMisc.toList(Cond1, Cond2);
    			listReturnItems = delegator.findList("CustomerReturnItemDetail", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
    			for (GenericValue item : listReturnItems) {
					BigDecimal returnPrice = OrderReturnServices.getReturnItemInitialCost(delegator, returnId, item.getString("returnItemSeqId"));
					item.put("returnPrice", returnPrice);
				}
    			for (GenericValue item : listReturnItems) {
    				if (UtilValidate.isEmpty(item.get("returnPrice"))){
	    				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("orderId"), "orderItemSeqId", item.getString("orderItemSeqId")));
						BigDecimal returnPrice = orderItem.getBigDecimal("unitPrice");
						item.put("returnPrice", returnPrice);
    				}
				}
    		} 
			for (GenericValue item : listReturnItems) {
				BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
				BigDecimal actualExportedQuantity = BigDecimal.ZERO;
				List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", item.getString("orderId"), "fromOrderItemSeqId", item.getString("orderItemSeqId"))), null, null, null, false);
				for (GenericValue dlvItem : listDeliveryItems) {
					if (UtilValidate.isNotEmpty(dlvItem.getBigDecimal("actualDeliveredQuantity"))) actualDeliveredQuantity = actualDeliveredQuantity.add(dlvItem.getBigDecimal("actualDeliveredQuantity"));
					if (UtilValidate.isNotEmpty(dlvItem.getBigDecimal("actualExportedQuantity"))) actualExportedQuantity = actualExportedQuantity.add(dlvItem.getBigDecimal("actualExportedQuantity"));
				}
				Map<String, Object> map = FastMap.newInstance();
        		map.putAll(item);
        		map.put("requiredQuantity", item.getBigDecimal("returnQuantity"));
        		map.put("actualExportedQuantity", actualExportedQuantity);
        		map.put("actualDeliveredQuantity", actualDeliveredQuantity);
        		if (UtilValidate.isNotEmpty(facilityId)){
	        		GenericValue productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", item.getString("productId"), "facilityId", facilityId));
	    			if (UtilValidate.isNotEmpty(productFacility)){
	        			map.put("expRequired", productFacility.getString("expRequired"));
	            		map.put("mnfRequired", productFacility.getString("mnfRequired"));
	            		map.put("lotRequired", productFacility.getString("lotRequired"));
	        		}
        		}
    			listReturns.add(map);
				
			}
			successResult.put("listReturnItems", listReturns);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getReturnItemDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListReturnDetail(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	String returnId = (String)parameters.get("returnId")[0];
       	mapCondition.put("returnId", returnId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
    	String returnHeaderTypeId = returnHeader.getString("returnHeaderTypeId");
    	String statusId = returnHeader.getString("statusId");
    	List<GenericValue> listReturnItems = new ArrayList<GenericValue>();
    	try {
    		if ("CUSTOMER_RETURN".equals(returnHeaderTypeId) && ("RETURN_ACCEPTED".equals(statusId) || "RETURN_RECEIVED".equals(statusId) || "RETURN_COMPLETED".equals(statusId))){
    			listIterator = delegator.find("CustomerReturnItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
        		listReturnItems = listIterator.getCompleteList();
        		listIterator.close();
    			for (GenericValue item : listReturnItems) {
					BigDecimal returnPrice = OrderReturnServices.getReturnItemInitialCost(delegator, returnId, item.getString("returnItemSeqId"));
					item.put("returnPrice", returnPrice);
				}
    		} else if ("VENDOR_RETURN".equals(returnHeaderTypeId) && ("SUP_RETURN_SHIPPED".equals(statusId) || "SUP_RETURN_COMPLETED".equals(statusId))){
    			listIterator = delegator.find("VendorReturnItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
        		listReturnItems = listIterator.getCompleteList();
        		listIterator.close();
    			for (GenericValue item : listReturnItems) {
    				if (UtilValidate.isEmpty(item.get("returnPrice"))){
	    				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("orderId"), "orderItemSeqId", item.getString("orderItemSeqId")));
						BigDecimal returnPrice = orderItem.getBigDecimal("unitPrice");
						item.put("returnPrice", returnPrice);
    				}
				}
    		} else {
    			listIterator = delegator.find("ReturnItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
        		listReturnItems = listIterator.getCompleteList();
        		listIterator.close();
    			for (GenericValue item : listReturnItems) {
    				if (UtilValidate.isEmpty(item.get("returnPrice"))){
	    				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("orderId"), "orderItemSeqId", item.getString("orderItemSeqId")));
	    				if (UtilValidate.isNotEmpty(orderItem)) {
	    					BigDecimal returnPrice = orderItem.getBigDecimal("unitPrice");
							item.put("returnPrice", returnPrice);
						}
    				}
				}
    		}
    		
			for (GenericValue item : listReturnItems) {
				String orderId = item.getString("orderId");
                String orderItemSeqId = item.getString("orderItemSeqId");
                if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(orderItemSeqId)) {
					BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
					BigDecimal actualExportedQuantity = BigDecimal.ZERO;
					BigDecimal actualDeliveredAmount = BigDecimal.ZERO;
					BigDecimal actualExportedAmount = BigDecimal.ZERO;
					//List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", item.getString("orderId"), "fromOrderItemSeqId", item.getString("orderItemSeqId"))), null, null, null, false);
					List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", orderId, "fromOrderItemSeqId", orderItemSeqId)), null, null, null, false);
					for (GenericValue dlvItem : listDeliveryItems) {
						if (UtilValidate.isNotEmpty(dlvItem.get("actualDeliveredQuantity"))) actualDeliveredQuantity = actualDeliveredQuantity.add(dlvItem.getBigDecimal("actualDeliveredQuantity"));
						if (UtilValidate.isNotEmpty(dlvItem.get("actualExportedQuantity"))) actualExportedQuantity = actualExportedQuantity.add(dlvItem.getBigDecimal("actualExportedQuantity"));
						if (UtilValidate.isNotEmpty(dlvItem.get("actualDeliveredAmount"))) actualDeliveredAmount = actualDeliveredAmount.add(dlvItem.getBigDecimal("actualDeliveredAmount"));
						if (UtilValidate.isNotEmpty(dlvItem.get("actualExportedAmount"))) actualExportedAmount = actualExportedAmount.add(dlvItem.getBigDecimal("actualExportedAmount"));
					}
                }
			}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProductReturn service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listReturnItems);
    	return successResult;
	}
	
	public static Map<String, Object> updateVendorReturn(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		String returnId = (String)context.get("returnId");
		String statusId = (String)context.get("statusId");
		GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
		String currencyUomId = returnHeader.getString("currencyUomId");
		List<GenericValue> listReturnItem = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
		if (!listReturnItem.isEmpty()){
			// get facility has inventory has been received
			List<GenericValue> listInvItemDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(UtilMisc.toMap(UtilMisc.toMap("orderId", listReturnItem.get(0).getString("orderId"), "orderItemSeqId", listReturnItem.get(0).getString("orderItemSeqId")))), null, null, null, false);
			if (!listInvItemDetail.isEmpty()){
				String shipmentIdTmp = listInvItemDetail.get(0).getString("shipmentId");
				GenericValue shipmentReceive = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentIdTmp));
				String facilityId = shipmentReceive.getString("destinationFacilityId");
				returnHeader.put("destinationFacilityId", facilityId);
				delegator.store(returnHeader);
				String contactMechId = shipmentReceive.getString("destinationContactMechId");
				if ("SUP_RETURN_SHIPPED".equals(statusId)){
					// create shipment for return
					Map<String, Object> mapCreateShipment = FastMap.newInstance();
					mapCreateShipment.put("primaryReturnId", returnId);
					mapCreateShipment.put("shipmentTypeId", "PURCHASE_RETURN");
					mapCreateShipment.put("statusId", "SHIPMENT_INPUT");
					mapCreateShipment.put("currencyUomId", currencyUomId);
					mapCreateShipment.put("defaultWeightUomId", "WT_kg");
					mapCreateShipment.put("originFacilityId", facilityId);
					mapCreateShipment.put("originContactMechId", contactMechId);
					mapCreateShipment.put("partyIdTo", returnHeader.getString("toPartyId"));
					mapCreateShipment.put("partyIdFrom", returnHeader.getString("fromPartyId"));
					mapCreateShipment.put("userLogin", system);
					Map<String, Object> mapReturn = dispatcher.runSync("createShipmentAndItemsForVendorReturn", mapCreateShipment);
					if (ServiceUtil.isError(mapReturn)){
						String errorMsg = ServiceUtil.getErrorMessage(mapReturn);
						return ServiceUtil.returnError(errorMsg);
					}
					String shipmentId = (String)mapReturn.get("shipmentId");
					
					Map<String, Object> mapCreateItemIssuance = FastMap.newInstance();
					mapCreateItemIssuance.put("shipmentId", shipmentId);
					mapCreateItemIssuance.put("userLogin", system);
					dispatcher.runSync("createItemIssuanceFromShipment", mapCreateItemIssuance);
					
					// update shipment to packed and shipped
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("shipmentId", shipmentId);
					mapTmp.put("statusId", "SHIPMENT_SCHEDULED");
					mapTmp.put("userLogin", system);
					dispatcher.runSync("updateShipment", mapTmp);
					
					mapTmp = FastMap.newInstance();
					mapTmp.put("shipmentId", shipmentId);
					mapTmp.put("statusId", "SHIPMENT_PACKED");
					mapTmp.put("userLogin", system);
					dispatcher.runSync("updateShipment", mapTmp);
					
					mapTmp = FastMap.newInstance();
					mapTmp.put("shipmentId", shipmentId);
					mapTmp.put("statusId", "SHIPMENT_SHIPPED");
					mapTmp.put("userLogin", system);
					dispatcher.runSync("updateShipment", mapTmp);
					
					// get list item issuance and issue them
					List<GenericValue> listItemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
					if (UtilValidate.isNotEmpty(listItemIssuance)){
						for (GenericValue issuance : listItemIssuance){
							mapTmp = FastMap.newInstance();
							List<GenericValue> listShipmentReturns = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", issuance.getString("shipmentItemSeqId"))), null, null, null, false);
							if (!listShipmentReturns.isEmpty()){
								mapTmp.put("returnId", listShipmentReturns.get(0).getString("returnId"));
								mapTmp.put("returnItemSeqId", listShipmentReturns.get(0).getString("returnItemSeqId"));
								mapTmp.put("shipmentItemSeqId", issuance.getString("shipmentItemSeqId"));
								mapTmp.put("shipmentId", shipmentId);
								mapTmp.put("itemIssuanceId", issuance.getString("itemIssuanceId"));
								mapTmp.put("inventoryItemId", issuance.getString("inventoryItemId"));
								if (UtilValidate.isNotEmpty(issuance.getBigDecimal("weight"))) {
									mapTmp.put("amountOnHandDiff", issuance.getBigDecimal("weight").negate());
								}
								mapTmp.put("quantityOnHandDiff", issuance.getBigDecimal("quantity").negate());
								mapTmp.put("availableToPromiseDiff", issuance.getBigDecimal("quantity").negate());
								mapTmp.put("userLogin", system);
								try {
									dispatcher.runSync("createInventoryItemDetail", mapTmp);
								} catch (GenericServiceException e){
									return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail for return shipment error!");
								}
							} else {
								return ServiceUtil.returnError("OLBIUS: Mapping between Return item and shipment item not found!");
							}
						}
						// update return to shipped
						mapTmp = FastMap.newInstance();
						mapTmp.put("returnId", returnId);
						mapTmp.put("statusId", "SUP_RETURN_SHIPPED");
						mapTmp.put("userLogin", system);
						dispatcher.runSync("updateReturnHeader", mapTmp);
					} else {
						return ServiceUtil.returnError("OLBIUS: ItemIssuance not found for shipment "+shipmentId);
					}
				} else {
					// other status
				}
			} else {
				return ServiceUtil.returnError("OLBIUS: inventory item has been received from order not found!");
			}
		} else {
			return ServiceUtil.returnError("OLBIUS: Return item not found!");
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("returnId", returnId);
    	return successResult;
	}
   	
	public static Map<String, Object> checkInventoryItemReservedFromReturn(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String returnId = (String)context.get("returnId");
    	Boolean hasReserved = false;
    	Boolean hasExported = false;
    	
    	List<GenericValue> listReturnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition("returnId", returnId) , null, null, null, false);
    	if (!listReturnItems.isEmpty()){
    		for (GenericValue item : listReturnItems) {
				String orderId = item.getString("orderId");
				String orderItemSeqId = item.getString("orderItemSeqId");
				BigDecimal returnQuantity = item.getBigDecimal("returnQuantity");
				EntityCondition orderCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
				EntityCondition orderItemCond = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
				EntityCondition QOHCond = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
				EntityCondition ATPCond = EntityCondition.makeCondition("availableToPromiseDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
				List<EntityCondition> listAllDetailCond = UtilMisc.toList(orderCond, orderItemCond, QOHCond, ATPCond);
				List<GenericValue> listInvDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(listAllDetailCond, EntityOperator.AND), null, null, null, false);
				if (!listInvDetail.isEmpty()){
					BigDecimal totalQuantityATP = BigDecimal.ZERO;
					for (GenericValue inv : listInvDetail){
						String inventoryItemId = inv.getString("inventoryItemId");
						BigDecimal quantityDetail = inv.getBigDecimal("quantityOnHandDiff");
						if (quantityDetail.compareTo(BigDecimal.ZERO) > 0){
							GenericValue inventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
							BigDecimal availableToPromiseTotal = inventoryItem.getBigDecimal("availableToPromiseTotal");
							totalQuantityATP = totalQuantityATP.add(availableToPromiseTotal);
							BigDecimal quantityOnHandTotal = inventoryItem.getBigDecimal("quantityOnHandTotal");
							if (quantityOnHandTotal.compareTo(quantityDetail) < 0){
								hasExported = true;
							}
						}
					}
					if (totalQuantityATP.compareTo(returnQuantity) >= 0){
						hasReserved = false;
					} else {
						hasReserved = true;
						break;
					}
				} else {
					return ServiceUtil.returnError("Can not trace inventory item has recieved from order " + orderId +" orderItemSeqId " + orderItemSeqId);
				}
				if (hasReserved){
					break;
				}
			}
    	} else {
    		return ServiceUtil.returnError("OLBIUS: Return item not found for return " + returnId);
    	}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("hasReserved", hasReserved);
    	successResult.put("hasExported", hasExported);
    	successResult.put("returnId", returnId);
    	return successResult;
	}
	
	public static Map<String, Object> updateVendorReturnWithChangeReserves(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String returnId = (String)context.get("returnId");
    	String newStatusId = (String)context.get("statusId");
    	GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
    	String statusId = returnHeader.getString("statusId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	if ("SUP_RETURN_CANCELLED".equals(statusId)){
    		return ServiceUtil.returnError("OLBIUS: Cannot update return because it cancelled");
    	}
    	if ("SUP_RETURN_ACCEPTED".equals(statusId) && newStatusId != null && "SUP_RETURN_SHIPPED".equals(newStatusId)){
    		// 1. Check inventory item reserved again
    		Map<String, Object> mapCheckInvReserved = FastMap.newInstance();
    		mapCheckInvReserved.put("returnId", returnId);
    		mapCheckInvReserved.put("userLogin", userLogin);
    		try {
    			Map<String, Object> mapCheckReturn = dispatcher.runSync("checkInventoryItemReservedFromReturn", mapCheckInvReserved);
    			Boolean hasReserved = (Boolean)mapCheckReturn.get("hasReserved");
    			if (hasReserved){
    				// 2. Get all inventory item
    				EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "RETURN_CANCELLED");
    				EntityCondition returnCond = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
    				List<EntityCondition> listAllCond = UtilMisc.toList(statusCond, returnCond);
    				List<GenericValue> listReturnItem = delegator.findList("ReturnItem", EntityCondition.makeCondition(listAllCond, EntityOperator.AND), null, null, null, false);
    				for (GenericValue item : listReturnItem) {
						String orderId = item.getString("orderId");
						String orderItemSeqId = item.getString("orderItemSeqId");
						
						EntityCondition orderCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
						EntityCondition orderItemCond = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
	    				List<EntityCondition> listAllDetailCond = UtilMisc.toList(orderCond, orderItemCond);
	    				
						List<GenericValue> listInvItemDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(listAllDetailCond, EntityOperator.AND), null, null, null, false);
						if (!listInvItemDetail.isEmpty()){
							// 3. Delete old reserved
							for (GenericValue itemDetail : listInvItemDetail) {
								String inventoryItemId = itemDetail.getString("inventoryItemId");
								List<GenericValue> listShipGroupSeqIds = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemId", inventoryItemId)), null, null, null, false);
								for (GenericValue oldRes : listShipGroupSeqIds) {
									String shipGroupSeqId = oldRes.getString("shipGroupSeqId");
									Map<String, Object> tmpMap = FastMap.newInstance();
									tmpMap.put("userLogin", userLogin);
									tmpMap.put("orderId", oldRes.getString("orderId"));
									tmpMap.put("orderItemSeqId", oldRes.getString("orderItemSeqId"));
									tmpMap.put("inventoryItemId", inventoryItemId);
									tmpMap.put("cancelQuantity", oldRes.getBigDecimal("quantity"));
									tmpMap.put("shipGroupSeqId", shipGroupSeqId);
									dispatcher.runSync("cancelOrderItemShipGrpInvRes", tmpMap);
									
									// 4. Create new reserves
									Map<String, Object> mapCreateReserves = FastMap.newInstance();
									mapCreateReserves.put("orderId", oldRes.getString("orderId"));
									mapCreateReserves.put("orderItemSeqId", oldRes.getString("orderItemSeqId"));
									mapCreateReserves.put("quantity", oldRes.getBigDecimal("quantity"));
									mapCreateReserves.put("userLogin", userLogin);
									try {
										dispatcher.runSync("reserveInventoryFromOrderItem", mapCreateReserves);
									} catch (Exception e) {
										return ServiceUtil.returnError("OLBIUS: call services reserveInventoryFromOrderItem error");
									}
								}
							}
						} else {
							return ServiceUtil.returnError("OLBIUS: Inventory item detail not found" + orderId + " " + orderItemSeqId);
						}
					}
    			}
    		} catch (Exception e2) {
    			return ServiceUtil.returnError("OLBIUS: call services checkInventoryItemReservedFromReturn error");
			}
    		// 5. update return header
    		Map<String, Object> mapUpdateReturn = FastMap.newInstance();
    		mapUpdateReturn.put("returnId", returnId);
    		mapUpdateReturn.put("statusId", newStatusId);
    		mapUpdateReturn.put("userLogin", userLogin);
    		try {
				dispatcher.runSync("updateVendorReturn", mapUpdateReturn);
			} catch (Exception e) {
				return ServiceUtil.returnError("OLBIUS: call services updateVendorReturn error");
			}
    	} else {
    		
    	}
    	Map<String, Object> result = FastMap.newInstance();
    	result.put("returnId", returnId);
    	return result;
	}
	
	public static Map<String, Object> logisticsCreateInvoiceFromCustomerReturn(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String returnId = (String)context.get("returnId");
    	List<GenericValue> billItems = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
    	if (!billItems.isEmpty()){
    		Map<String, Object> mapCreateInvoice = UtilMisc.toMap("returnId", returnId, "billItems", billItems, "userLogin", (GenericValue)context.get("userLogin"));
    		try {
				dispatcher.runSync("createInvoiceFromReturn", mapCreateInvoice);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createInvoiceFromReturn fail with return " + returnId);
			}
    	} else {
    		return ServiceUtil.returnError("OLBIUS: ShipmentReceipt not found for return " + returnId);
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("returnId", returnId);
		return mapReturn;
	}
	
	public static Map<String, Object> logisticsReceiveReturn(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String returnId = (String)context.get("returnId");
    	String shipmentId = (String)context.get("shipmentId");
    	String facilityId = (String)context.get("facilityId");
		String listItems = (String)context.get("listReturnItems");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
		returnHeader.put("destinationFacilityId", facilityId); 
		delegator.store(returnHeader);
		
		if (UtilValidate.isEmpty(shipmentId)) {
			List<GenericValue> returnShipments = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("primaryReturnId", returnId)), null, null, null, false);
			if (!returnShipments.isEmpty()){
				shipmentId = returnShipments.get(0).getString("shipmentId");
			}
		}
		
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		JSONArray listItemTmp = JSONArray.fromObject(listItems);
		for (int j = 0; j < listItemTmp.size(); j++){
			Map<String, Object> item = FastMap.newInstance();
			JSONObject returnItem = listItemTmp.getJSONObject(j);
			String returnItemSeqId = returnItem.getString("returnItemSeqId");
			GenericValue returnItemDB = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
			BigDecimal returnQuantity = new BigDecimal(returnItem.getString("returnQuantity"));
			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", returnItemDB.getString("productId")));
			String requireAmount = product.getString("requireAmount");
			BigDecimal selectedAmount = returnQuantity;
			
			Map<String, Object> mapShipmentItem = FastMap.newInstance();
			mapShipmentItem.put("shipmentId", shipmentId);
			mapShipmentItem.put("productId", returnItemDB.getString("productId"));
			mapShipmentItem.put("quantity", returnQuantity);
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount) && UtilValidate.isNotEmpty(selectedAmount) && selectedAmount.compareTo(BigDecimal.ZERO) > 0) {
				mapShipmentItem.put("weight", returnQuantity.multiply(selectedAmount));
			}
			
			String locationCode = null;
			String locationId = null;
			if (returnItem.containsKey("locationCode") && UtilValidate.isNotEmpty(returnItem.getString("locationCode"))){
				locationCode = (String)returnItem.get("locationCode");
			}
			if (UtilValidate.isNotEmpty(locationCode)) {
				List<GenericValue> listLocByCode = delegator.findList("LocationFacility",
						EntityCondition.makeCondition("locationCode", EntityOperator.EQUALS, locationCode),
						null, null, null, false);
				if (!listLocByCode.isEmpty()){
					locationId = listLocByCode.get(0).getString("locationId");
					item.put("locationId", locationId);
				}
			}
			
			mapShipmentItem.put("userLogin", userLogin);
			mapShipmentItem.put("locationId", locationId);
			String shipmentItemSeqId = null;
			try {
				Map<String, Object> mapShipmentItemResult = dispatcher.runSync("createShipmentItem", mapShipmentItem);
				shipmentItemSeqId = (String)mapShipmentItemResult.get("shipmentItemSeqId");
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS - Create shipment item error");
			}
			// Create mapping
			GenericValue requirementItemShipment = delegator.makeValue("ReturnItemShipment");
			requirementItemShipment.put("shipmentId", shipmentId);
			requirementItemShipment.put("shipmentItemSeqId", shipmentItemSeqId);
			requirementItemShipment.put("returnId", returnId);
			requirementItemShipment.put("returnItemSeqId", returnItemDB.getString("returnItemSeqId"));
			requirementItemShipment.put("quantity", returnQuantity);
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount) && UtilValidate.isNotEmpty(selectedAmount) && selectedAmount.compareTo(BigDecimal.ZERO) > 0) {
				requirementItemShipment.put("amount", returnQuantity.multiply(selectedAmount));
			}
			delegator.create(requirementItemShipment);
		}
		
		for (int j = 0; j < listItemTmp.size(); j++){
			Map<String, Object> item = FastMap.newInstance();
			JSONObject returnItem = listItemTmp.getJSONObject(j);
			String returnItemSeqId = returnItem.getString("returnItemSeqId");
			GenericValue returnItemDB = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
			BigDecimal returnQuantity = new BigDecimal(returnItem.getString("returnQuantity"));
			if (returnQuantity.compareTo(BigDecimal.ZERO) > 0 && returnQuantity.compareTo(returnItemDB.getBigDecimal("returnQuantity")) <= 0){
				String returnReasonId = returnItem.getString("returnReasonId");
				String lotId = null;
				if (returnItem.containsKey("lotId") && UtilValidate.isNotEmpty(returnItem.getString("lotId"))){
					lotId = returnItem.getString("lotId").toUpperCase();
					GenericValue lot = delegator.findOne("Lot", false, UtilMisc.toMap("lotId", lotId));
					if (UtilValidate.isEmpty(lot)){
						lot = delegator.makeValue("Lot");
						lot.put("lotId", lotId);
						lot.put("creationDate", UtilDateTime.nowTimestamp());
						delegator.create(lot); 
					}
				}
				String statusId = returnItem.getString("inventoryStatusId");
				Long expireDate = null;
				if (returnItem.containsKey("expireDate") && UtilValidate.isNotEmpty(returnItem.getString("expireDate"))){
					expireDate = new Long (returnItem.getString("expireDate"));
				}
				
				Long datetimeReceived = null;
				if (returnItem.containsKey("datetimeReceived") && UtilValidate.isNotEmpty(returnItem.getString("datetimeReceived"))){
					datetimeReceived = new Long (returnItem.getString("datetimeReceived"));
				}
				
				Long datetimeManufactured = null;
				if (returnItem.containsKey("datetimeManufactured") && UtilValidate.isNotEmpty(returnItem.getString("datetimeManufactured"))){
					datetimeManufactured = new Long (returnItem.getString("datetimeManufactured"));
				}
				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", returnItemDB.getString("orderId"), "orderItemSeqId", returnItemDB.getString("orderItemSeqId")));
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", returnItemDB.getString("productId")));
				String requireAmount = product.getString("requireAmount");
				BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
				
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount) && UtilValidate.isNotEmpty(selectedAmount) && selectedAmount.compareTo(BigDecimal.ZERO) > 0) {
					item.put("amountAccepted", returnQuantity.multiply(selectedAmount));
				}
				item.put("quantityAccepted", returnQuantity);
				item.put("returnItemSeqId", returnItemSeqId);
				item.put("returnId", returnId);
				item.put("returnReasonId", returnReasonId);
				
				item.put("quantityExcess", BigDecimal.ZERO);
				item.put("quantityRejected", BigDecimal.ZERO);
				item.put("quantityQualityAssurance", BigDecimal.ZERO);
				item.put("expireDate", expireDate);
				item.put("ownerPartyId", company);
				item.put("datetimeReceived", datetimeReceived);
				item.put("datetimeManufactured", datetimeManufactured);
				item.put("lotId", lotId);
				item.put("statusId", statusId);
				GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
				item.put("userLogin", system);
				String productId = returnItem.getString("productId");
				item.put("productId", productId);
				item.put("facilityId", facilityId);
				item.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				if (UtilValidate.isNotEmpty(returnItemDB.getString("orderId")) && UtilValidate.isNotEmpty(returnItemDB.getString("orderItemSeqId"))){
					List<GenericValue> orderItemIsuances = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("orderId", returnItemDB.getString("orderId"), "orderItemSeqId", returnItemDB.getString("orderItemSeqId"))), null, null, null, false);
					List<GenericValue> listItemRoles = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", returnItemDB.getString("orderId"), "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
					if (!listItemRoles.isEmpty()){
						String partyId = listItemRoles.get(0).getString("partyId");
						GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", partyId));
						if (UtilValidate.isNotEmpty(partyAcctgPreference) && "COGS_AVG_COST".equals(partyAcctgPreference.getString("cogsMethodId"))){
							if (!orderItemIsuances.isEmpty()){
								BigDecimal unitCost = BigDecimal.ZERO;
								BigDecimal totalInvUnitPrice = BigDecimal.ZERO;
								BigDecimal purCost = BigDecimal.ZERO;
								BigDecimal totalInvPurCost= BigDecimal.ZERO;
								for (GenericValue itemIssuance : orderItemIsuances) {
									Timestamp issuedDatetime = itemIssuance.getTimestamp("issuedDateTime");
									GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", itemIssuance.getString("inventoryItemId")));
									String facilityExptId = inv.getString("facilityId");
									String averageCostType = "SIMPLE_AVG_COST";
									List<GenericValue> listAverageCost = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(UtilMisc.toMap("productAverageCostTypeId", averageCostType, "organizationPartyId", partyId, "productId", productId, "facilityId", facilityExptId)), null, null, null, false);
									BigDecimal averageCost = BigDecimal.ZERO;
									BigDecimal averagePurCost = BigDecimal.ZERO;
									for (GenericValue avr : listAverageCost) {
										if ((avr.getTimestamp("fromDate").before(issuedDatetime) && UtilValidate.isEmpty(avr.getTimestamp("thruDate"))) || (avr.getTimestamp("fromDate").before(issuedDatetime) && UtilValidate.isNotEmpty(avr.getTimestamp("thruDate")) && avr.getTimestamp("thruDate").after(issuedDatetime))){
											averageCost = avr.getBigDecimal("averageCost"); 
											averagePurCost = avr.getBigDecimal("averagePurCost"); 
											break;
										}
									}
									totalInvUnitPrice = totalInvUnitPrice.add(averageCost);
									totalInvPurCost = totalInvPurCost.add(averagePurCost);
								}
								unitCost = totalInvUnitPrice.divide(new BigDecimal(orderItemIsuances.size()), 3, RoundingMode.HALF_UP);
								purCost = totalInvPurCost.divide(new BigDecimal(orderItemIsuances.size()), 3, RoundingMode.HALF_UP);
								item.put("unitCost", unitCost);
								item.put("purCost", purCost);
							} else {
								item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
								item.put("purCost", orderItem.getBigDecimal("unitPrice"));
							}
						} else {
							item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
							item.put("purCost", orderItem.getBigDecimal("unitPrice"));
						}
					} else {
						item.put("unitCost", orderItem.getBigDecimal("unitPrice"));
						item.put("purCost", orderItem.getBigDecimal("unitPrice"));
					}
				} else {
					item.put("unitCost", returnItemDB.getBigDecimal("unitCost"));
					item.put("purCost", returnItemDB.getBigDecimal("unitCost"));
				}
				if (shipmentId != null && !"".equals(shipmentId)){
					item.put("shipmentId", shipmentId);
				}
				try {
					dispatcher.runSync("receiveInventoryProduct", item);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error " + e.toString());
				}
			} else {
				if (returnQuantity.compareTo(returnItemDB.getBigDecimal("returnQuantity")) > 0){
					return ServiceUtil.returnError("OLBIUS: Cannot receive return with quantity greater than quantity has been required by sales! ");
				} else {
					Map<String, Object> mapReturnItemUpdate = FastMap.newInstance();
					returnItemDB.put("receivedQuantity", BigDecimal.ZERO);
					mapReturnItemUpdate.put("returnId", returnId); 
					mapReturnItemUpdate.put("returnItemSeqId", returnItemSeqId); 
					mapReturnItemUpdate.put("receivedQuantity", BigDecimal.ZERO); 
					mapReturnItemUpdate.put("statusId", "RETURN_RECEIVED");
					mapReturnItemUpdate.put("userLogin", (GenericValue)context.get("userLogin"));
					try {
						dispatcher.runSync("updateReturnItem", mapReturnItemUpdate);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: updateReturnItem error " + e.toString());
					}
				}
			}
		}
		Boolean checkDistributor = SalesPartyUtil.isDistributor(delegator, returnHeader.getString("fromPartyId"));
		List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", returnHeader.getString("fromPartyId"))), null, null, null, false);
		Map<String, Object> mapReturn = FastMap.newInstance();
		if (!listFacility.isEmpty() && checkDistributor){
			try {
				//	autoExportReturn
				mapReturn.clear();
				mapReturn.put("returnId", returnId);
				mapReturn.put("userLogin", context.get("userLogin"));
				dispatcher.runSync("autoExportReturn", mapReturn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mapReturn.clear();
		mapReturn.put("returnId", returnId);
		return mapReturn;
	}
	
	public static Map<String, Object> logisticsExportReturn(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String returnId = (String)context.get("returnId");
		String listItems = (String)context.get("listReturnItems");
		GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
		String facilityId = null;
		JSONArray listItemTmp = JSONArray.fromObject(listItems);
		List<Map<String, Object>> listReturnItems = new ArrayList<Map<String, Object>>();
		for (int j = 0; j < listItemTmp.size(); j++){
			Map<String, Object> item = FastMap.newInstance();
			JSONObject returnItem = listItemTmp.getJSONObject(j);
			String returnItemSeqId = returnItem.getString("returnItemSeqId");
			GenericValue returnItemDB = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
			String productId = returnItemDB.getString("productId");
			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			Boolean requireAmount = false;
			if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.get("requireAmount"))) {
				requireAmount = true;
			}
			BigDecimal returnQuantity = new BigDecimal(returnItem.getString("returnQuantity"));
			item.put("inventoryItemId", returnItem.getString("inventoryItemId"));
			if (requireAmount) {
				item.put("returnQuantity", BigDecimal.ONE);
				item.put("returnAmount", returnQuantity);
			} else {
				item.put("returnQuantity", returnQuantity);
			}
			
			item.put("productId", productId);
			item.put("returnItemSeqId", returnItemSeqId);
			listReturnItems.add(item);
			if (returnQuantity.compareTo(BigDecimal.ZERO) > 0){
				String inventoryItemId = returnItem.getString("inventoryItemId");
				GenericValue inventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
				if (requireAmount) {
					if (UtilValidate.isNotEmpty(returnItemDB.get("receivedAmount"))){
						returnItemDB.put("receivedAmount", returnQuantity.add(returnItemDB.getBigDecimal("receivedAmount")));
					} else {
						returnItemDB.put("receivedAmount", returnQuantity);
					}
					returnItemDB.put("receivedQuantity", BigDecimal.ONE);
				} else {
					if (UtilValidate.isNotEmpty(returnItemDB.get("receivedQuantity"))){
						returnItemDB.put("receivedQuantity", returnQuantity.add(returnItemDB.getBigDecimal("receivedQuantity")));
					} else {
						returnItemDB.put("receivedQuantity", returnQuantity);
					}
				}
				returnItemDB.store();
				facilityId = inventoryItem.getString("facilityId");
			} else {
				Map<String, Object> mapReturnItemUpdate = FastMap.newInstance();
				if (UtilValidate.isEmpty(returnItemDB.get("receivedQuantity"))){
					mapReturnItemUpdate.put("receivedQuantity", returnItemDB.getBigDecimal("receivedQuantity"));
				} else {
					mapReturnItemUpdate.put("receivedQuantity", BigDecimal.ZERO);
				}
				if (UtilValidate.isEmpty(returnItemDB.get("receivedQuantity"))){
					mapReturnItemUpdate.put("receivedQuantity", returnItemDB.getBigDecimal("receivedQuantity"));
				} else {
					mapReturnItemUpdate.put("receivedQuantity", BigDecimal.ZERO);
				}
				mapReturnItemUpdate.put("returnId", returnId); 
				mapReturnItemUpdate.put("returnItemSeqId", returnItemSeqId); 
				mapReturnItemUpdate.put("receivedQuantity", BigDecimal.ZERO); 
				mapReturnItemUpdate.put("statusId", "RETURN_RECEIVED");
				mapReturnItemUpdate.put("userLogin", (GenericValue)context.get("userLogin"));
				try {
					dispatcher.runSync("updateReturnItem", mapReturnItemUpdate);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error " + e.toString());
				}
			}
		}
		returnHeader.put("destinationFacilityId", facilityId);
		returnHeader.store();
		List<GenericValue> listContactMechs = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION")), null, null, null, false);
		String currencyUomId = returnHeader.getString("currencyUomId");
		// create shipment for return
		Map<String, Object> mapCreateShipment = FastMap.newInstance();
		mapCreateShipment.put("primaryReturnId", returnId);
		mapCreateShipment.put("shipmentTypeId", "PURCHASE_RETURN");
		mapCreateShipment.put("statusId", "SHIPMENT_INPUT");
		mapCreateShipment.put("currencyUomId", currencyUomId);
		mapCreateShipment.put("defaultWeightUomId", "WT_kg");
		mapCreateShipment.put("originFacilityId", facilityId);
		if (!listContactMechs.isEmpty()){
			mapCreateShipment.put("originContactMechId", listContactMechs.get(0).getString("contactMechId"));
		}
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		mapCreateShipment.put("partyIdTo", returnHeader.getString("toPartyId"));
		mapCreateShipment.put("partyIdFrom", returnHeader.getString("fromPartyId"));
		mapCreateShipment.put("userLogin", userLogin);
		Map<String, Object> mapReturn = dispatcher.runSync("createShipment", mapCreateShipment);
		if (ServiceUtil.isError(mapReturn)){
			String errorMsg = ServiceUtil.getErrorMessage(mapReturn);
			return ServiceUtil.returnError(errorMsg);
		}
		String shipmentId = (String)mapReturn.get("shipmentId");
		
		for (Map<String, Object> inv : listReturnItems){
			String productId = (String)inv.get("productId");
			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			String requireAmount = objProduct.getString("requireAmount");
			
			String returnItemSeqId = (String)inv.get("returnItemSeqId");
			// create shipment item
			Map<String, Object> mapShipmentItem = FastMap.newInstance();
			mapShipmentItem.put("shipmentId", shipmentId);
			mapShipmentItem.put("productId", productId);
			mapShipmentItem.put("quantity", (BigDecimal)inv.get("returnQuantity"));
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
				mapShipmentItem.put("quantity", BigDecimal.ONE);
				mapShipmentItem.put("weight", (BigDecimal)inv.get("returnAmount"));
			}
			
			mapShipmentItem.put("userLogin", userLogin);
			String shipmentItemSeqId = null;
			try {
				Map<String, Object> mapShipmentItemResult = dispatcher.runSync("createShipmentItem", mapShipmentItem);
				shipmentItemSeqId = (String)mapShipmentItemResult.get("shipmentItemSeqId");
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS - Create shipment item error");
			}
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			// create return item and shipment
			Map<String, Object> returnSmt = FastMap.newInstance();
			returnSmt.put("returnId", returnId);
			returnSmt.put("returnItemSeqId", returnItemSeqId);
			returnSmt.put("shipmentId", shipmentId);
			returnSmt.put("shipmentItemSeqId", shipmentItemSeqId);
			returnSmt.put("quantity", (BigDecimal)inv.get("returnQuantity"));
			returnSmt.put("amount", (BigDecimal)inv.get("returnAmount"));
			returnSmt.put("userLogin", system);
			try {
				dispatcher.runSync("createReturnItemShipment", returnSmt);
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: runsync service createReturnItemShipment error!");
			}
			
			String inventoryItemId = (String)inv.get("inventoryItemId");
			BigDecimal returnQuantity = (BigDecimal)inv.get("returnQuantity");
			if (returnQuantity.compareTo(BigDecimal.ZERO) > 0){
				Map<String, Object> mapIssuance = FastMap.newInstance();
    			mapIssuance.put("shipmentItemSeqId", shipmentItemSeqId);
    			mapIssuance.put("shipmentId", shipmentId);
				mapIssuance.put("inventoryItemId", inventoryItemId);
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					mapIssuance.put("quantity", BigDecimal.ONE);
					mapIssuance.put("weight", (BigDecimal)inv.get("returnAmount"));
				} else {
					mapIssuance.put("quantity", returnQuantity);
				}
    			
    			mapIssuance.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
    			mapIssuance.put("userLogin", userLogin);
    			try {
    				dispatcher.runSync("createItemIssuance", mapIssuance);
    			} catch (GenericServiceException e) {
    				return ServiceUtil.returnError("OLBIUS - Create item issuance error");
    			}
			} else {
				return ServiceUtil.returnError("OLBIUS: Order item not received");
			}
		}
		
		Map<String, Object> mapTmp = FastMap.newInstance();
		mapTmp.put("shipmentId", shipmentId);
		mapTmp.put("statusId", "SHIPMENT_SCHEDULED");
		mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
		dispatcher.runSync("updateShipment", mapTmp);
		
		mapTmp = FastMap.newInstance();
		mapTmp.put("shipmentId", shipmentId);
		mapTmp.put("statusId", "SHIPMENT_PACKED");
		mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
		dispatcher.runSync("updateShipment", mapTmp);
		
		mapTmp = FastMap.newInstance();
		mapTmp.put("shipmentId", shipmentId);
		mapTmp.put("statusId", "SHIPMENT_SHIPPED");
		mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
		dispatcher.runSync("updateShipment", mapTmp);
		
		// get list item issuance and issue them
		List<GenericValue> listItemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
		if (UtilValidate.isNotEmpty(listItemIssuance)){
			for (GenericValue issuance : listItemIssuance){
				List<GenericValue> listShipmentReturns = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", issuance.getString("shipmentItemSeqId"))), null, null, null, false);
				if (!listShipmentReturns.isEmpty()){
					mapTmp.put("returnId", listShipmentReturns.get(0).getString("returnId"));
					mapTmp.put("returnItemSeqId", listShipmentReturns.get(0).getString("returnItemSeqId"));
					mapTmp = FastMap.newInstance();
					mapTmp.put("shipmentItemSeqId", issuance.getString("shipmentItemSeqId"));
					mapTmp.put("shipmentId", shipmentId);
					mapTmp.put("itemIssuanceId", issuance.getString("itemIssuanceId"));
					mapTmp.put("inventoryItemId", issuance.getString("inventoryItemId"));
					mapTmp.put("quantityOnHandDiff", issuance.getBigDecimal("quantity").negate());
					// TODO CHANGE weight -> amount
					if (UtilValidate.isNotEmpty(issuance.getBigDecimal("weight"))) {
						mapTmp.put("amountOnHandDiff", issuance.getBigDecimal("weight").negate());
					}
					mapTmp.put("availableToPromiseDiff", issuance.getBigDecimal("quantity").negate());
					mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
					try {
						dispatcher.runSync("createInventoryItemDetail", mapTmp);
					} catch (GenericServiceException e){
						return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail for return shipment error!");
					}
				} else {
					return ServiceUtil.returnError("OLBIUS: Mapping between Return item and shipment item not found!");
				}
			}
			// update return to shipped
			mapTmp = FastMap.newInstance();
			mapTmp.put("returnId", returnId);
			mapTmp.put("statusId", "SUP_RETURN_SHIPPED");
			mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
			dispatcher.runSync("updateReturnHeader", mapTmp);
		} else {
			return ServiceUtil.returnError("OLBIUS: ItemIssuance not found for shipment "+shipmentId);
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("returnId", returnId);
		result.put("returnHeaderTypeId", returnHeader.getString("returnHeaderTypeId"));
		
		return result;
	}
	
	public static Map<String,Object> sendNotifyToLogStorekeeperNewReturn(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String returnId = (String)context.get("returnId");
    	Delegator delegator = ctx.getDelegator();
    	String facilityId = null;
    	GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
    	String messages = "HasBeenApproved";
    	String action = "";
    	facilityId = returnHeader.getString("destinationFacilityId");
		List<GenericValue> listFacilityParties = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"))), null, null, null, false);
		listFacilityParties = EntityUtil.filterByDate(listFacilityParties);
    	GenericValue returnType = delegator.findOne("ReturnHeaderType", false, UtilMisc.toMap("returnHeaderTypeId", returnHeader.getString("returnHeaderTypeId")));
//    	String target = "returnId="+returnId;
    	if (!listFacilityParties.isEmpty() && ("RETURN_ACCEPTED".equals(returnHeader.getString("statusId")) || "SUP_RETURN_ACCEPTED".equals(returnHeader.getString("statusId")))){
			LocalDispatcher dispatcher = ctx.getDispatcher();
			if ("CUSTOMER_RETURN".equals(returnHeader.getString("returnHeaderTypeId"))){
				action = "viewReturnOrder?returnId="+returnId;
			} else if ("VENDOR_RETURN".equals(returnHeader.getString("returnHeaderTypeId"))){
				action = "getDetailVendorReturn?returnId="+returnId;
			} else {
				return ServiceUtil.returnSuccess();
			}
			for (GenericValue party : listFacilityParties) {
				Map<String, Object> mapContext = new HashMap<String, Object>();
				String header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + UtilProperties.getMessage(resource, "Shipment", (Locale)context.get("locale")).toString().toLowerCase()+ " " + StringUtil.wrapString((String)returnType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "ReturnId", (Locale)context.get("locale")) +": [" +returnId+"]";
        		mapContext.put("partyId", party.getString("partyId"));
        		mapContext.put("action", action);
        		mapContext.put("targetLink", "");
        		mapContext.put("header", header);
        		mapContext.put("ntfType", "ONE");
        		mapContext.put("userLogin", (GenericValue)context.get("userLogin"));
        		mapContext.put("openTime", UtilDateTime.nowTimestamp());
        		try {
        			dispatcher.runSync("createNotification", mapContext);
        		} catch (GenericServiceException e) {
        			e.printStackTrace();
        		}
			}
    	}
    	return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> autoExportReturn(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	String returnId = (String)context.get("returnId");
    	String ownerPartyId = (String)context.get("partyId");
    	String facilityId = (String)context.get("facilityId");
    	GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
    	if (UtilValidate.isEmpty(returnHeader)) return ServiceUtil.returnError("OLBIUS: return not found");
    	String fromPartyId = returnHeader.getString("fromPartyId");
    	if (UtilValidate.isEmpty(fromPartyId) && UtilValidate.isEmpty(ownerPartyId)) return ServiceUtil.returnError("OLBIUS: party from or owner party not found");
    	List<GenericValue> listReturnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
    	for (GenericValue item : listReturnItems) {
    		String productId = item.getString("productId");
    		if (UtilValidate.isEmpty(productId)) return ServiceUtil.returnError("OLBIUS: Cannot auto export return with empty product id");
    		BigDecimal quantity = item.getBigDecimal("returnQuantity");
    		EntityCondition productCond = EntityCondition.makeCondition("productId", productId);
    		EntityCondition ownerPartyCond = null;
    		if (UtilValidate.isNotEmpty(ownerPartyId)) {
    			ownerPartyCond = EntityCondition.makeCondition("ownerPartyId", ownerPartyId);
    		} else if(UtilValidate.isNotEmpty(fromPartyId)){
    			ownerPartyId = fromPartyId;
    			ownerPartyCond = EntityCondition.makeCondition("ownerPartyId", fromPartyId);
    		}
    		EntityCondition facilityCond = null;
    		if (UtilValidate.isNotEmpty(facilityId)) {
    			facilityCond = EntityCondition.makeCondition("facilityId", facilityId);
    		} else {
    			List<String> listFaIds = new ArrayList<String>();
    			List<GenericValue> listFacilities = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", ownerPartyId)), null, null, null, false);
    			for (GenericValue fa : listFacilities) {
    				listFaIds.add(fa.getString("facilityId"));
				}
    			if (!listFaIds.isEmpty()) facilityCond = EntityCondition.makeCondition("facilityId", facilityId);
    		}
    		if (UtilValidate.isEmpty(facilityCond)) return ServiceUtil.returnError("OLBIUS: Cannot get facility to export");
    		EntityCondition qohCond = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity);
    		List<EntityCondition> listConds = UtilMisc.toList(ownerPartyCond, productCond);
    		if (UtilValidate.isNotEmpty(facilityCond)) listConds.add(facilityCond);
    		listConds.add(qohCond);
    		EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
    		List<GenericValue> listInvItems = delegator.findList("InventoryItem", EntityCondition.makeCondition(allConds), null, null, null, false);
    		if (!listInvItems.isEmpty()){
    			GenericValue inventoryItem = listInvItems.get(0);
    			BigDecimal curQoh = inventoryItem.getBigDecimal("quantityOnHandTotal");
    			BigDecimal curAtp = inventoryItem.getBigDecimal("availableToPromiseTotal");
    			inventoryItem.set("quantityOnHandTotal", curQoh.subtract(quantity));
    			inventoryItem.set("availableToPromiseTotal", curAtp.subtract(quantity));
    			delegator.store(inventoryItem);
    		} else {
    			List<EntityCondition> listConds2 = UtilMisc.toList(ownerPartyCond, productCond);
        		if (UtilValidate.isNotEmpty(facilityCond)) listConds2.add(facilityCond);
        		EntityCondition allConds2 = EntityCondition.makeCondition(listConds2, EntityOperator.AND);
        		List<GenericValue> listInvItems2 = delegator.findList("InventoryItem", EntityCondition.makeCondition(allConds2), null, null, null, false);
        		if (!listInvItems2.isEmpty()){
        			BigDecimal remainQty = quantity;
        			for (GenericValue item2 : listInvItems2) {
						if (item2.getBigDecimal("quantityOnHandTotal").compareTo(remainQty) < 0){
							item2.put("quantityOnHandTotal", BigDecimal.ZERO);
							item2.put("availableToPromiseTotal", BigDecimal.ZERO);
							delegator.store(item2);
							remainQty = quantity.subtract(item2.getBigDecimal("quantityOnHandTotal"));
						} else {
							item2.put("quantityOnHandTotal", item2.getBigDecimal("quantityOnHandTotal").subtract(quantity));
							item2.put("availableToPromiseTotal", item2.getBigDecimal("availableToPromiseTotal").subtract(quantity));
							remainQty = BigDecimal.ZERO;
							break;
						}
					}
        			if (remainQty.compareTo(BigDecimal.ZERO) > 0){
        				GenericValue invNag = listInvItems2.get(0);
        				invNag.put("quantityOnHandTotal", invNag.getBigDecimal("quantityOnHandTotal").subtract(remainQty));
        				invNag.put("availableToPromiseTotal", invNag.getBigDecimal("availableToPromiseTotal").subtract(remainQty));
        				delegator.store(invNag);
        			}
        		} else {
        			Debug.log("OLBIUS: inventory item not found for product id " + productId);
        		}
    		}
		}
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
    	Map<String, Object> mapReceive = FastMap.newInstance();
		mapReceive.put("returnId", returnId);
		mapReceive.put("userLogin", system);
		mapReceive.put("statusId", "RETURN_COMPLETED");
		mapReceive.put("needsInventoryReceive", "N");
		dispatcher.runSync("updateReturnHeader", mapReceive);
		
		Map<String, Object> mapResult = FastMap.newInstance();
		mapResult.put("returnId", returnId);
		return mapResult;
	}
	
	public static Map<String, Object> updateReturnItemStatusByReturnId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	String returnId = (String)context.get("returnId");
    	GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId",returnId));
    	String statusId = returnHeader.getString("statusId");
    	List<GenericValue> list = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
    	if (!list.isEmpty()){
    		LocalDispatcher dispatcher = ctx.getDispatcher();
    		for (GenericValue item : list) {
    			Map<String, Object> input = FastMap.newInstance();
    			input.put("returnId", item.getString("returnId"));
    			input.put("returnItemSeqId", item.getString("returnItemSeqId"));
    			input.put("statusId", statusId);
    			input.put("userLogin", (GenericValue)context.get("userLogin"));
    			
				try {
					dispatcher.runSync("updateReturnItem", input);
				} catch (GenericServiceException e){
					return ServiceUtil.returnError("OLBIUS: runsync service updateReturnItem error!");
				}
			}
    	}
    	Map<String, Object> mapResult = FastMap.newInstance();
		mapResult.put("returnId", returnId);
		return mapResult;
	}
	
	public static Map<String, Object> getReturnItems(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	String returnId = (String)context.get("returnId");
    	List<EntityCondition> listConds = new ArrayList<EntityCondition>();
    	EntityCondition idConds = EntityCondition.makeCondition("returnId", returnId);
    	listConds.add(idConds);
    	Map<String, Object> successResult = FastMap.newInstance();
    	List<GenericValue> listReturnItems = new ArrayList<GenericValue>();
    	listReturnItems = delegator.findList("ReturnItemDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
    	successResult.put("listReturnItems", listReturnItems);
    	return successResult;
	}
	
	public static Map<String, Object> sendNotificationBackToReturnCreatedParty(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String returnId = (String)context.get("returnId");
    	GenericValue returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
    	String returnHeaderTypeId = returnHeader.getString("returnHeaderTypeId");
    	GenericValue returnHeaderType = delegator.findOne("ReturnHeaderType", false, UtilMisc.toMap("returnHeaderTypeId", returnHeaderTypeId));
    	String header = ""; 
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
//		String targetLink = "returnId="+returnId;
		Map<String, Object> mapContext = new HashMap<String, Object>();
		GenericValue userLoginCreated = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", returnHeader.getString("createdBy")));
		String statusId = returnHeader.getString("statusId");
		String mss = "";
		if ("SUP_RETURN_REQUESTED".equals(statusId)){
			mss = "HasBeenRequested";
		} else if ("SUP_RETURN_ACCEPTED".equals(statusId)){
			mss = "HasBeenAccepted";
		} else if ("SUP_RETURN_SHIPPED".equals(statusId)){
			mss = "HasBeenShipped";
		} else if ("SUP_RETURN_COMPLETED".equals(statusId)){
			mss = "HasBeenCompleted";
		} else if ("SUP_RETURN_CANCELLED".equals(statusId)){
			mss = "HasBeenCancelled";
		} 
		header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)returnHeaderType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, mss, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "ReturnId", (Locale)context.get("locale")) +": [" +returnId+"]";
		mapContext.put("partyId", userLoginCreated.getString("partyId"));
    	mapContext.put("action", "viewGeneralReturnSupplier?returnId="+returnId);
		mapContext.put("targetLink", "");
		mapContext.put("header", header);
		mapContext.put("ntfType", "ONE");
		mapContext.put("userLogin", userLogin);
		try {
			dispatcher.runSync("createNotification", mapContext);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: sendNotificationBackToReturnCreatedParty error! " + e.toString());
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("returnId", returnId);
		return mapReturn;
	}
	
	public static Map<String, Object> createNotifyCustomerReturn(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String returnId = (String)context.get("returnId");
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	
    	List<GenericValue> listReturnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId)), null, null, null, false);
    	// one return one order
    	String orderId = listReturnItems.get(0).getString("orderId");
    	try {
			NotificationWorker.sendNotifyWhenCreateReturnOrder(delegator, dispatcher, locale, orderId, returnId, userLogin);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when create notification: " + e.toString();
			Debug.logWarning(e, errMsg, module);
		} catch (GenericServiceException e) {
			String errMsg = "Fatal error when create notification: " + e.toString();
			Debug.logWarning(e, errMsg, module);
		}
    	
    	Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("returnId", returnId);
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> exportProductFromVendorReturn(DispatchContext ctx, Map<String, Object> context){
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		String returnId = (String)context.get("returnId");
		String listItems = (String)context.get("listReturnItems");
		
		GenericValue returnHeader = null;
		try {
			returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when findOne ReturnHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String facilityId = returnHeader.getString("destinationFacilityId");
		
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: exportProductFromVendorReturn - JqxWidgetSevices.convert error!");
		}
		

		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: exportProductFromVendorReturn - JqxWidgetSevices.convert error!");
			}
		}
		
		List<Map<String, Object>> listReturnItems = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapAttributes = FastMap.newInstance();
		for (Map<String, Object> returnItem : listProducts){
			Map<String, Object> item = FastMap.newInstance();
			String returnItemSeqId = (String)returnItem.get("returnItemSeqId");
			GenericValue returnItemDB = null;
			try {
				returnItemDB = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne ReturnItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			String productId = returnItemDB.getString("productId");
			GenericValue objProduct = null;
			try {
				objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			if (!listProductAttrs.isEmpty()) {
				List<Map<String, Object>> listAttributes = FastList.newInstance();
				for (Map<String, Object> map : listProductAttrs) {
					if (map.containsKey("productId")){
						String prId = (String)map.get("productId");
						if (UtilValidate.isNotEmpty(prId) && productId.equals(prId)) {
							listAttributes.add(map);
						}
					}
				}
				mapAttributes.put(productId, listAttributes);
			}
			
			Boolean requireAmount = false;
			if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.get("requireAmount"))) {
				requireAmount = true;
			}
			BigDecimal quantity = new BigDecimal((String)returnItem.get("quantity"));
			if (requireAmount) {
				item.put("returnQuantity", BigDecimal.ONE);
				item.put("returnAmount", quantity);
			} else {
				item.put("returnQuantity", quantity);
			}
			
			item.put("productId", productId);
			item.put("returnItemSeqId", returnItemSeqId);
			listReturnItems.add(item);
			if (quantity.compareTo(BigDecimal.ZERO) > 0){
				if (requireAmount) {
					if (UtilValidate.isNotEmpty(returnItemDB.get("receivedAmount"))){
						returnItemDB.put("receivedAmount", quantity.add(returnItemDB.getBigDecimal("receivedAmount")));
					} else {
						returnItemDB.put("receivedAmount", quantity);
					}
					returnItemDB.put("receivedQuantity", BigDecimal.ONE);
				} else {
					if (UtilValidate.isNotEmpty(returnItemDB.get("receivedQuantity"))){
						returnItemDB.put("receivedQuantity", quantity.add(returnItemDB.getBigDecimal("receivedQuantity")));
					} else {
						returnItemDB.put("receivedQuantity", quantity);
					}
				}
				try {
					returnItemDB.store();
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when Store ReturnItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			} else {
				Map<String, Object> mapReturnItemUpdate = FastMap.newInstance();
				if (UtilValidate.isEmpty(returnItemDB.get("receivedQuantity"))){
					mapReturnItemUpdate.put("receivedQuantity", returnItemDB.getBigDecimal("receivedQuantity"));
				} else {
					mapReturnItemUpdate.put("receivedQuantity", BigDecimal.ZERO);
				}
				if (UtilValidate.isEmpty(returnItemDB.get("receivedQuantity"))){
					mapReturnItemUpdate.put("receivedQuantity", returnItemDB.getBigDecimal("receivedQuantity"));
				} else {
					mapReturnItemUpdate.put("receivedQuantity", BigDecimal.ZERO);
				}
				mapReturnItemUpdate.put("returnId", returnId); 
				mapReturnItemUpdate.put("returnItemSeqId", returnItemSeqId); 
				mapReturnItemUpdate.put("receivedQuantity", BigDecimal.ZERO); 
				mapReturnItemUpdate.put("statusId", "RETURN_RECEIVED");
				mapReturnItemUpdate.put("userLogin", userLogin);
				try {
					dispatcher.runSync("updateReturnItem", mapReturnItemUpdate);
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when runSync updateReturnItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);

				}
			}
		}
		Map<String, Object> mapCreateShipment = FastMap.newInstance();
		mapCreateShipment.put("returnId", returnId);
		mapCreateShipment.put("shipmentTypeId", "PURCHASE_RETURN");
		mapCreateShipment.put("userLogin", userLogin);
		Map<String, Object> mapReturn = FastMap.newInstance();
		try {
			mapReturn = dispatcher.runSync("createShipmentFromVendorReturn", mapCreateShipment);
			if (ServiceUtil.isError(mapReturn)){
				String errorMsg = ServiceUtil.getErrorMessage(mapReturn);
				return ServiceUtil.returnError(errorMsg);
			}
		} catch (GenericServiceException e) {
			String errMsg = "OLBIUS: Fatal error when runSync createShipment: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String shipmentId = (String)mapReturn.get("shipmentId");
		List<GenericValue> listShipmentItems = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
		EntityCondition cond2 = EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(cond1);
		conds.add(cond2);
		try {
			listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList ShipmentItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		for (GenericValue item : listShipmentItems){
			String shipmentItemSeqId = item.getString("shipmentItemSeqId");
			String returnItemSeqId = null;
			
			List<GenericValue> listReturnShipment = FastList.newInstance();
			try {
				EntityCondition condsm = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
				EntityCondition condsmit = EntityCondition.makeCondition("shipmentItemSeqId", EntityOperator.EQUALS, shipmentItemSeqId);
				List<EntityCondition> condsmt = FastList.newInstance();
				condsmt.add(condsm);
				condsmt.add(condsmit);
				listReturnShipment = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(condsmt), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList ReturnItemShipment error!");
			}
			if (!listReturnShipment.isEmpty()) returnItemSeqId = listReturnShipment.get(0).getString("returnItemSeqId");
			if (UtilValidate.isEmpty(returnItemSeqId)) {
				return ServiceUtil.returnError("OLBIUS: ReturnItemShipment not found! returnId= " + returnId + "; shipmentId =" + shipmentId);
			} 
			
			GenericValue objReturnItem = null;
			try {
				objReturnItem = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne ReturnItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			String productId = objReturnItem.getString("productId");
			BigDecimal quantity = item.getBigDecimal("quantity");
			if (ProductUtil.isWeightProduct(delegator, productId)) {
				quantity = item.getBigDecimal("weight");
			}
			
			List<Map<String, Object>> listAttribues = FastList.newInstance();
			if (mapAttributes.containsKey(productId)){
				listAttribues = (List<Map<String, Object>>)mapAttributes.get(productId);
			}
			
			if (quantity.compareTo(BigDecimal.ZERO) > 0){
				List<Map<String, Object>> listInvs = FastList.newInstance();
				if (!listAttribues.isEmpty()){
					for (Map<String, Object> map : listAttribues) {
						Map<String, Object> attributes = FastMap.newInstance();
						attributes.put("productId", productId);
						attributes.put("facilityId", facilityId);
						attributes.put("ownerPartyId", company);
						
						String qtyStr = null;
						if (map.containsKey("quantity") && map.containsKey("productId")){
							qtyStr = (String)map.get("quantity");
							if (UtilValidate.isNotEmpty(qtyStr)) {
								BigDecimal quantityP = new BigDecimal(qtyStr);
								if (quantityP.compareTo(BigDecimal.ZERO) > 0){
									for (String key : map.keySet()) {
										if ("expireDate".equals(key)){
											if (UtilValidate.isNotEmpty(map.get("expireDate"))) {
												String expStr = (String)map.get("expireDate");
												if (UtilValidate.isNotEmpty(expStr)) {
													Long expL = new Long (expStr);
													if (UtilValidate.isNotEmpty(expL)) {
														Timestamp exp = new Timestamp(expL);
														if (UtilValidate.isNotEmpty(exp)) {
															attributes.put(key, exp);
														}
													}
												}
											}
										} else if ("datetimeManufactured".equals(key)){
											if (UtilValidate.isNotEmpty(map.get("datetimeManufactured"))) {
												String expStr = (String)map.get("datetimeManufactured");
												if (UtilValidate.isNotEmpty(expStr)) {
													Long expL = new Long (expStr);
													if (UtilValidate.isNotEmpty(expL)) {
														Timestamp exp = new Timestamp(expL);
														if (UtilValidate.isNotEmpty(exp)) {
															attributes.put(key, exp);
														}
													}
												}
											}
										} else {
											attributes.put(key, map.get(key));
										}
									}
									List<Map<String, Object>> listInvTmps = FastList.newInstance();
									try {
										listInvTmps = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantityP);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when InventoryUtil.getInventoryItemsForQuantity: " + e.toString();
										Debug.logError(e, errMsg, module);
										return ServiceUtil.returnError(errMsg);
									}
									if (!listInvTmps.isEmpty()){
										listInvs.addAll(listInvTmps);
									}
								}
							}
						}
					}
				} else {
					Map<String, Object> attributes = FastMap.newInstance();
					attributes.put("productId", productId);
					attributes.put("facilityId", facilityId);
					attributes.put("ownerPartyId", company);
					List<String> orderBy = FastList.newInstance();
					orderBy.add("expireDate");
					try {
						listInvs = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantity, orderBy);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when InventoryUtil.getInventoryItemsForQuantity: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}
				
				if (listInvs.isEmpty()) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				BigDecimal qohTotal = BigDecimal.ZERO;
				for (Map<String, Object> map : listInvs) {
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					qohTotal = qohTotal.add(qoh);
				}
				if (qohTotal.compareTo(quantity) < 0) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				
				for (Map<String, Object> map : listInvs) {
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					String inventoryItemId = (String)map.get("inventoryItemId");
					Map<String, Object> mapIssuance = FastMap.newInstance();
					mapIssuance.put("shipmentItemSeqId", shipmentItemSeqId);
					mapIssuance.put("shipmentId", shipmentId);
					mapIssuance.put("inventoryItemId", inventoryItemId);
					if (ProductUtil.isWeightProduct(delegator, productId)) {
						mapIssuance.put("quantity", BigDecimal.ONE);
						mapIssuance.put("weight", qoh);
					} else {
						mapIssuance.put("quantity", qoh);
					}
					mapIssuance.put("returnId", returnId);
					mapIssuance.put("returnItemSeqId", returnItemSeqId);
					mapIssuance.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
					mapIssuance.put("userLogin", userLogin);
					mapIssuance.put("affectAccounting", true);
					try {
						dispatcher.runSync("createItemIssuance", mapIssuance);
					} catch (GenericServiceException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS - Create item issuance error!");
					}
				}
			}
		}
		
		Map<String, Object> mapTmp = FastMap.newInstance();
		mapTmp = FastMap.newInstance();
		mapTmp.put("shipmentId", shipmentId);
		mapTmp.put("statusId", "SHIPMENT_PACKED");
		mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
		try {
			dispatcher.runSync("updateShipment", mapTmp);
		} catch (GenericServiceException e) {
			String errMsg = "OLBIUS: Fatal error when runSync updateShipment: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		mapTmp = FastMap.newInstance();
		mapTmp.put("shipmentId", shipmentId);
		mapTmp.put("statusId", "SHIPMENT_SHIPPED");
		mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
		try {
			dispatcher.runSync("updateShipment", mapTmp);
		} catch (GenericServiceException e) {
			String errMsg = "OLBIUS: Fatal error when runSync updateShipment: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		if (UtilValidate.isNotEmpty(listShipmentItems)){
			for (GenericValue shipmentItem : listShipmentItems){
				String shipmentItemSeqId = shipmentItem.getString("shipmentItemSeqId");
				List<GenericValue> listItemIssuance = FastList.newInstance();
				try {
					listItemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", shipmentItemSeqId)), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList ItemIssuance: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				
				if (!listItemIssuance.isEmpty()){
					for (GenericValue issuance : listItemIssuance) {
						List<GenericValue> listShipmentReturns = FastList.newInstance();
						try {
							listShipmentReturns = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", shipmentItemSeqId)), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList ReturnItemShipment: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listShipmentReturns.isEmpty()){
							Map<String, Object> mapDetail = FastMap.newInstance();
							String returnReasonId = null;
							try {
								GenericValue returnItem = listShipmentReturns.get(0).getRelatedOne("ReturnItem", false);
								if (UtilValidate.isNotEmpty(returnItem)) {
									returnReasonId = returnItem.getString("returnReasonId");
								}
							} catch (GenericEntityException e1) {
								String errMsg = "OLBIUS: Fatal error when getRelatedOne ReturnItem: " + e1.toString();
								Debug.logError(e1, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
							
							mapDetail.put("returnId", listShipmentReturns.get(0).getString("returnId"));
							mapDetail.put("returnItemSeqId", listShipmentReturns.get(0).getString("returnItemSeqId"));
							mapDetail.put("shipmentItemSeqId", issuance.getString("shipmentItemSeqId"));
							mapDetail.put("shipmentId", shipmentId);
							mapDetail.put("returnReasonId", returnReasonId);
							mapDetail.put("itemIssuanceId", issuance.getString("itemIssuanceId"));
							mapDetail.put("inventoryItemId", issuance.getString("inventoryItemId"));
							
							String productId = shipmentItem.getString("productId");
							if (ProductUtil.isWeightProduct(delegator, productId)) {
								mapDetail.put("amountOnHandDiff", issuance.getBigDecimal("weight").negate());
								mapDetail.put("quantityOnHandDiff", new BigDecimal(-1));
								mapDetail.put("availableToPromiseDiff", new BigDecimal(-1));
							} else {
								mapDetail.put("quantityOnHandDiff", new BigDecimal(issuance.getString("quantity")).negate());
								mapDetail.put("availableToPromiseDiff", new BigDecimal(issuance.getString("quantity")).negate());
							}
							mapDetail.put("userLogin", userLogin);
							try {
								dispatcher.runSync("createInventoryItemDetail", mapDetail);
							} catch (GenericServiceException e){
								String errMsg = "OLBIUS: Fatal error when runSync createInventoryItemDetail: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						} 
					}
				}
			}
			// update return to shipped
			mapTmp = FastMap.newInstance();
			mapTmp.put("returnId", returnId);
			mapTmp.put("statusId", "SUP_RETURN_COMPLETED");
			mapTmp.put("userLogin", context.get("userLogin"));
			try {
				dispatcher.runSync("updateReturnHeader", mapTmp);
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when runSync updateReturnHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		} else {
			return ServiceUtil.returnError("OLBIUS: ItemIssuance not found for shipment "+shipmentId);
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("returnId", returnId);
		return result;
	}
	
	public static Map<String, Object> createShipmentFromVendorReturn(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String returnId = (String)context.get("returnId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String shipmentId = null;
    	GenericValue objReturnHeader = null;
		try {
			objReturnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ReturnHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	String currencyUomId = objReturnHeader.getString("currencyUomId");
    	String facilityId = objReturnHeader.getString("destinationFacilityId");
    	
		// create shipment for return
		Map<String, Object> mapCreateShipment = FastMap.newInstance();
		mapCreateShipment.put("primaryReturnId", returnId);
		mapCreateShipment.put("shipmentTypeId", "PURCHASE_RETURN");
		mapCreateShipment.put("statusId", "SHIPMENT_INPUT");
		mapCreateShipment.put("currencyUomId", currencyUomId);
		mapCreateShipment.put("defaultWeightUomId", "WT_kg");
		mapCreateShipment.put("originFacilityId", facilityId);
		List<GenericValue> listContactMechs;
		try {
			listContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "SHIP_ORIG_LOCATION");
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when getFacilityContactMechs: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listContactMechs.isEmpty()){
			mapCreateShipment.put("originContactMechId", listContactMechs.get(0).getString("contactMechId"));
		}
		mapCreateShipment.put("partyIdTo", objReturnHeader.getString("toPartyId"));
		mapCreateShipment.put("partyIdFrom", objReturnHeader.getString("fromPartyId"));
		mapCreateShipment.put("userLogin", userLogin);
		Map<String, Object> mapReturn = FastMap.newInstance();
		try {
			mapReturn = dispatcher.runSync("createShipment", mapCreateShipment);
			if (ServiceUtil.isError(mapReturn)){
				String errorMsg = ServiceUtil.getErrorMessage(mapReturn);
				return ServiceUtil.returnError(errorMsg);
			}
			shipmentId = (String)mapReturn.get("shipmentId");
		} catch (GenericServiceException e) {
			String errMsg = "OLBIUS: Fatal error when runSync createShipment: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	
		List<GenericValue> listReturnItems = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
		EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SUP_RETURN_ACCEPTED");
		EntityCondition cond3 = EntityCondition.makeCondition("receivedQuantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(cond1);
		conds.add(cond2);
		conds.add(cond3);
		try {
			listReturnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList ReturnItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		for (GenericValue returnItem : listReturnItems){
			String productId = returnItem.getString("productId");
			GenericValue objProduct;
			try {
				objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			String requireAmount = objProduct.getString("requireAmount");
			
			String returnItemSeqId = returnItem.getString("returnItemSeqId");
			// create shipment item
			Map<String, Object> mapShipmentItem = FastMap.newInstance();
			mapShipmentItem.put("shipmentId", shipmentId);
			mapShipmentItem.put("productId", productId);
			
			BigDecimal quantity = returnItem.getBigDecimal("returnQuantity");
			if (UtilValidate.isNotEmpty(returnItem.get("receivedQuantity"))) {
				quantity = returnItem.getBigDecimal("receivedQuantity");
			} 
			BigDecimal weight = returnItem.getBigDecimal("returnAmount");
			if (UtilValidate.isNotEmpty(returnItem.get("receivedAmount"))) {
				weight = returnItem.getBigDecimal("receivedAmount");
			} 
			
			mapShipmentItem.put("quantity", quantity);
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
				mapShipmentItem.put("quantity", BigDecimal.ONE);
				mapShipmentItem.put("weight", weight);
			}
			
			mapShipmentItem.put("userLogin", userLogin);
			String shipmentItemSeqId = null;
			try {
				Map<String, Object> mapShipmentItemResult = dispatcher.runSync("createShipmentItem", mapShipmentItem);
				shipmentItemSeqId = (String)mapShipmentItemResult.get("shipmentItemSeqId");
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when runSync createShipmentItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			GenericValue system = null;
			try {
				system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne findOne: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			// create return item and shipment
			Map<String, Object> returnSmt = FastMap.newInstance();
			returnSmt.put("returnId", returnId);
			returnSmt.put("returnItemSeqId", returnItemSeqId);
			returnSmt.put("shipmentId", shipmentId);
			returnSmt.put("shipmentItemSeqId", shipmentItemSeqId);
			returnSmt.put("quantity", quantity);
			returnSmt.put("amount", weight);
			returnSmt.put("userLogin", system);
			try {
				dispatcher.runSync("createReturnItemShipment", returnSmt);
			} catch (GenericServiceException e){
				String errMsg = "OLBIUS: Fatal error when runSync createReturnItemShipment: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		}
	
    	Map<String, Object> result = FastMap.newInstance();
		result.put("shipmentId", shipmentId);
		result.put("returnId", returnId);
    	return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveProductFromCustomerReturn(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue system = null;
		try {
			system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne UserLogin: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String returnId = (String)context.get("returnId");
		String listItems = (String)context.get("listReturnItems");
		Locale locale = (Locale)context.get("locale");
		GenericValue returnHeader = null;
		try {
			returnHeader = delegator.findOne("ReturnHeader", false, UtilMisc.toMap("returnId", returnId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ReturnHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		String facilityId = null;
		if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
			facilityId = (String)context.get("facilityId");
		}
		if (UtilValidate.isEmpty(facilityId)) {
			facilityId = returnHeader.getString("destinationFacilityId");
		}
		
		if (UtilValidate.isEmpty(facilityId)) {
			String errMsg = "OLBIUS: Fatal error when receiveProductFromCustomerReturn: Missing facilityId info";
			return ServiceUtil.returnError(errMsg);
		}
		String shipmentId = null;
		List<GenericValue> returnShipments = FastList.newInstance();
		try {
			returnShipments = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("primaryReturnId", returnId)), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList Shipment: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!returnShipments.isEmpty()){
			shipmentId = returnShipments.get(0).getString("shipmentId");
		} else {
			String errMsg = "OLBIUS: Shipment not found with return: " + returnId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - JqxWidgetSevices.convert error!");
		}
		
		String listPrAttrs = null;
		if (UtilValidate.isNotEmpty(context.get("listProductAttributes"))) {
			listPrAttrs = (String)context.get("listProductAttributes");
		}
		
		List<Map<String, Object>> listProductAttrs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listPrAttrs)) {
			try {
				listProductAttrs = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listPrAttrs);
			} catch (ParseException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - JqxWidgetSevices.convert error!");
			}
		}
		
		
		Map<String, Object> mapAttributes = FastMap.newInstance();
		for (Map<String, Object> returnItem : listProducts){
			String returnItemSeqId = null;
			if (returnItem.containsKey("returnItemSeqId")){
				returnItemSeqId = (String)returnItem.get("returnItemSeqId");
			}
			String productId = null;
			if (returnItem.containsKey("productId")){
				productId = (String)returnItem.get("productId");
			}
			BigDecimal quantity = BigDecimal.ZERO;
			String quantityStr = null;
			if (returnItem.containsKey("quantity")){
				quantityStr = (String)returnItem.get("quantity");
				quantity = new BigDecimal(quantityStr);
			}
			
			if (!listProductAttrs.isEmpty()) {
				List<Map<String, Object>> listAttributes = FastList.newInstance();
				for (Map<String, Object> map : listProductAttrs) {
					if (map.containsKey("productId")){
						String prId = (String)map.get("productId");
						if (UtilValidate.isNotEmpty(prId) && productId.equals(prId)) {
							listAttributes.add(map);
						}
					}
				}
				mapAttributes.put(productId, listAttributes);
			}
			
			if (UtilValidate.isNotEmpty(productId)) {
				if (UtilValidate.isNotEmpty(returnItemSeqId)) {
					// update
					BigDecimal quantityShip = BigDecimal.ZERO;
					BigDecimal weightShip = BigDecimal.ZERO;
					GenericValue returnItemDB = null;
					try {
						returnItemDB = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne ReturnItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					BigDecimal receivedQuantity = BigDecimal.ZERO;
					BigDecimal receivedAmount = BigDecimal.ZERO;
					BigDecimal returnQuantity = returnItemDB.getBigDecimal("returnQuantity");
					BigDecimal returnAmount = BigDecimal.ZERO;
					if (UtilValidate.isNotEmpty(returnItemDB.get("returnAmount"))) {
						returnAmount = returnItemDB.getBigDecimal("returnAmount");
					}
					if (ProductUtil.isWeightProduct(delegator, productId)){
						receivedQuantity = BigDecimal.ONE;
						quantityShip = BigDecimal.ONE;
						receivedAmount = quantity;
						weightShip = quantity;
					} else {
						receivedQuantity = quantity;
						quantityShip = quantity;
					}
					
					if (UtilValidate.isNotEmpty(returnItemDB.get("receivedQuantity"))){
						receivedQuantity = receivedQuantity.add(returnItemDB.getBigDecimal("receivedQuantity"));
						if (receivedQuantity.compareTo(returnQuantity) > 0){
							return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLCannotReceiveGreaterThanCreatedQuantity", locale));
						}
					} 
					if (UtilValidate.isNotEmpty(returnItemDB.get("receivedAmount"))){
						receivedAmount = receivedAmount.add(returnItemDB.getBigDecimal("receivedAmount"));
						if (receivedAmount.compareTo(returnAmount) > 0){
							return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BLCannotReceiveGreaterThanCreatedQuantity", locale));
						}
					} 
					returnItemDB.put("receivedQuantity", receivedQuantity);
					returnItemDB.put("receivedAmount", receivedAmount);
					try {
						delegator.store(returnItemDB);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when store ReturnItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					
					List<GenericValue> listReqShipments = FastList.newInstance();
					EntityCondition cond1 = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
					EntityCondition cond2 = EntityCondition.makeCondition("returnItemSeqId", EntityOperator.EQUALS, returnItemSeqId);
					EntityCondition cond3 = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(cond1);
					conds.add(cond2);
					conds.add(cond3);
					try {
						listReqShipments = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						Debug.logError(e.toString(), module);
						return ServiceUtil.returnError("OLBIUS: findList ReturnItemShipment error!");
					}
					String shipmentItemSeqId = null;
					if (!listReqShipments.isEmpty()){
						listReqShipments.get(0).getString("shipmentItemSeqId");
					} else {
						String errMsg = "OLBIUS: Fatal error: Shipment item not found for returnItem: " + returnId + "-" + returnItemSeqId;
						return ServiceUtil.returnError(errMsg);
					}
				
					List<Map<String, Object>> listAttribues = FastList.newInstance();
					if (mapAttributes.containsKey(productId)){
						listAttribues = (List<Map<String, Object>>)mapAttributes.get(productId);
					}
					if (!listAttribues.isEmpty()){
	
						// xu ly nhap sp co HSD, NSX, Lo SX
						BigDecimal quantityFree = quantityShip;
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							quantityFree = weightShip;
						} 
						for (Map<String, Object> mapAttr : listAttribues) {
							Map<String, Object> attributes = FastMap.newInstance();
							String qtyStr = null;
							if (mapAttr.containsKey("quantity") && mapAttr.containsKey("productId")){
								qtyStr = (String)mapAttr.get("quantity");
								if (UtilValidate.isNotEmpty(qtyStr)) {
									BigDecimal quantityP = new BigDecimal(qtyStr); // quantity tuong ung se la can nang voi san pham can nang
									quantityFree = quantityFree.subtract(quantityP);
									if (quantityP.compareTo(BigDecimal.ZERO) > 0){
										for (String key : mapAttr.keySet()) {
											if ("expireDate".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("expireDate"))) {
													String expStr = (String)mapAttr.get("expireDate");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("datetimeManufactured".equals(key)){
												if (UtilValidate.isNotEmpty(mapAttr.get("datetimeManufactured"))) {
													String expStr = (String)mapAttr.get("datetimeManufactured");
													if (UtilValidate.isNotEmpty(expStr)) {
														Long expL = new Long (expStr);
														if (UtilValidate.isNotEmpty(expL)) {
															Timestamp exp = new Timestamp(expL);
															if (UtilValidate.isNotEmpty(exp)) {
																attributes.put(key, exp);
															}
														}
													}
												}
											} else if ("lotId".equals(key)){ 
												String lotId = (String)mapAttr.get(key);
												GenericValue objLot = null;
												try {
													objLot = delegator.findOne("Lot", false,
															UtilMisc.toMap("lotId", lotId));
												} catch (GenericEntityException e) {
													Debug.logError(e.toString(), module);
													return ServiceUtil.returnError("OLBIUS: findOne Lot error!");
												}
												if (UtilValidate.isEmpty(objLot)) {
													// create new lot
													objLot = delegator.makeValue("Lot");
													objLot.put("lotId", lotId);
													objLot.put("creationDate", UtilDateTime.nowTimestamp());
													try {
														delegator.create(objLot);
													} catch (GenericEntityException e) {
														String errMsg = "OLBIUS: Fatal error when create Lot: " + e.toString();
														Debug.logError(e, errMsg, module);
														return ServiceUtil.returnError(errMsg);
													} 
												}
												attributes.put(key, lotId);
											} else {
												attributes.put(key, mapAttr.get(key));
											}
										}
										Map<String, Object> map = FastMap.newInstance();
										map.put("returnItemSeqId", returnItemSeqId);
										map.put("returnId", returnId);
										map.put("productId", productId);
										map.put("shipmentItemSeqId", shipmentItemSeqId);
										map.put("quantityAccepted", quantityP);
										if (ProductUtil.isWeightProduct(delegator, productId)) {
											map.put("amountAccepted", quantityP);
											map.put("quantityAccepted", BigDecimal.ONE);
										} 
										map.put("quantityExcess", BigDecimal.ZERO);
										map.put("quantityRejected", BigDecimal.ZERO);
										map.put("quantityQualityAssurance", BigDecimal.ZERO);
										map.put("ownerPartyId", company);
										map.put("statusId", null);
										map.put("userLogin", userLogin);
										map.put("facilityId", facilityId);
										map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
										map.put("unitCost", returnItemDB.getBigDecimal("returnPrice"));
										map.put("purCost", BigDecimal.ZERO);
										map.put("shipmentId", shipmentId);
										map.put("returnReasonId", returnItemDB.getString("returnReasonId"));
										
										map.putAll(attributes);
										try {
											dispatcher.runSync("receiveInventoryProduct", map);
										} catch (GenericServiceException e) {
											Debug.logError(e.toString(), module);
											return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
										}
									}
								}
							}
						}
						if (quantityFree.compareTo(BigDecimal.ZERO) < 0){
							return ServiceUtil.returnError("OLBIUS: receiveProductFromRequirement - quantity not true!");
						} else if (quantityFree.compareTo(BigDecimal.ZERO) > 0){
							Map<String, Object> map = FastMap.newInstance();
							map.put("returnItemSeqId", returnItemSeqId);
							map.put("returnId", returnId);
							map.put("productId", productId);
							map.put("quantityAccepted", quantityFree);
							if (ProductUtil.isWeightProduct(delegator, productId)) {
								map.put("amountAccepted", quantityFree);
								map.put("quantityAccepted", BigDecimal.ONE);
							} 
							map.put("shipmentItemSeqId", shipmentItemSeqId);
							map.put("quantityExcess", BigDecimal.ZERO);
							map.put("quantityRejected", BigDecimal.ZERO);
							map.put("quantityQualityAssurance", BigDecimal.ZERO);
							map.put("ownerPartyId", company);
							map.put("statusId", null);
							map.put("userLogin", userLogin);
							map.put("facilityId", facilityId);
							map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
							map.put("unitCost", returnItemDB.getBigDecimal("returnPrice"));
							map.put("purCost", BigDecimal.ZERO);
							map.put("shipmentId", shipmentId);
							map.put("returnReasonId", returnItemDB.getString("returnReasonId"));
							try {
								dispatcher.runSync("receiveInventoryProduct", map);
							} catch (GenericServiceException e) {
								Debug.logError(e.toString(), module);
								return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
							}
						}
					} else {
						Map<String, Object> map = FastMap.newInstance();
						map.put("returnItemSeqId", returnItemSeqId);
						map.put("returnId", returnId);
						map.put("productId", productId);
						map.put("quantityAccepted", quantityShip);
						if (ProductUtil.isWeightProduct(delegator, productId)) {
							map.put("amountAccepted", weightShip);
							map.put("quantityAccepted", BigDecimal.ONE);
						} 
						map.put("shipmentItemSeqId", shipmentItemSeqId);
						
						map.put("quantityExcess", BigDecimal.ZERO);
						map.put("quantityRejected", BigDecimal.ZERO);
						map.put("quantityQualityAssurance", BigDecimal.ZERO);
						map.put("ownerPartyId", company);
						map.put("statusId", null);
						map.put("userLogin", userLogin);
						map.put("facilityId", facilityId);
						map.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						map.put("unitCost", returnItemDB.getBigDecimal("returnPrice"));
						map.put("purCost", BigDecimal.ZERO);
						map.put("shipmentId", shipmentId);
						map.put("returnReasonId", returnItemDB.getString("returnReasonId"));
						try {
							dispatcher.runSync("receiveInventoryProduct", map);
						} catch (GenericServiceException e) {
							Debug.logError(e.toString(), module);
							return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
						}
					}
				}
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
/*	Tu dong tru kho NPP	
 * Boolean checkDistributor = SalesPartyUtil.isDistributor(delegator, returnHeader.getString("fromPartyId"));
		List<GenericValue> listFacility = FastList.newInstance();
		try {
			listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", returnHeader.getString("fromPartyId"))), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: findList Facility error!");
		}
		if (!listFacility.isEmpty() && checkDistributor){
			try {
				//	autoExportReturn
				mapReturn.clear();
				mapReturn.put("returnId", returnId);
				mapReturn.put("userLogin", context.get("userLogin"));
				dispatcher.runSync("autoExportReturn", mapReturn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		mapReturn.clear();
		mapReturn.put("returnId", returnId);
		return mapReturn;
	}
}