package com.olbius.baselogistics.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.order.OrderReadHelper;
import com.olbius.product.util.ProductUtil;

public class LogisticsOrderUtil {
	public static final String module = LogisticsOrderUtil.class.getName();
	
	public static Boolean checkOrderExported(Delegator delegator, String orderId) throws GenericEntityException{
		Boolean isExported = false;
		EntityCondition condOrderId = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		EntityCondition condDiff = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, BigDecimal.ZERO);
		List<EntityCondition> listConds = new ArrayList<EntityCondition>();
		listConds.add(condOrderId);
		listConds.add(condDiff);
		List<GenericValue> listItemDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(listConds), null, null, null, false);
		if (!listItemDetails.isEmpty()){
			isExported = true;
		} else {
			isExported = false;
		}
		return isExported;
	}
	
	public static Boolean checkOrderReceived(Delegator delegator, String orderId) throws GenericEntityException{
		Boolean isReceived = false;
		EntityCondition condOrderId = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		EntityCondition condDiff = EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		List<EntityCondition> listConds = new ArrayList<EntityCondition>();
		listConds.add(condOrderId);
		listConds.add(condDiff);
		List<GenericValue> listItemDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(listConds), null, null, null, false);
		if (!listItemDetails.isEmpty()){
			isReceived = true;
		} else {
			isReceived = false;
		}
		return isReceived;
	}
	
	public static List<GenericValue> getOrderPartyNameView(Delegator delegator, String orderId) {
    	List<GenericValue> listOrderParties = new ArrayList<GenericValue>();
	    try {	
	    	List<GenericValue> listOrderRole = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId), null, false);
	    	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	    	for(int i = 0; i < listOrderRole.size(); i++){
	    	    listCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, listOrderRole.get(i).get("partyId")));
	    	}
	    	listOrderParties = delegator.findList("PartyNameView", EntityCondition.makeCondition(listCond, EntityOperator.OR), null, null, null, false);
	    }
    	catch (GenericEntityException e){
    		ServiceUtil.returnError("getOrderPartyNameView error" + e.toString());
    	}
    	return listOrderParties;
	}
	
	public static List<Map<String, String>> getOrderParty(Delegator delegator, String orderId) throws GenericEntityException{
    	List<GenericValue> listOrderRole = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId), null, false);
    	List<EntityCondition> listCond = new ArrayList<EntityCondition>();
    	for(int i = 0; i < listOrderRole.size(); i++){
    	    listCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, listOrderRole.get(i).get("partyId")));
    	}
    	List<GenericValue> listTmps = delegator.findList("PartyNameView", EntityCondition.makeCondition(listCond, EntityOperator.OR), null, null, null, false);
    	List<Map<String, String>> listOParty = new ArrayList<Map<String, String>>();
    	if (!listTmps.isEmpty()){
    		for (GenericValue item : listTmps){
    			Map<String, String> mapTmp = FastMap.newInstance();
    			mapTmp.put("partyId", item.getString("partyId"));
    			String tmp = "";
    			if (item.getString("lastName") != null){
    				tmp = tmp + item.getString("lastName");
    			}
    			if (item.getString("middleName") != null){
    				tmp = tmp + " " + item.getString("middleName");
    			}
    			if (item.getString("firstName") != null){
    				tmp = tmp + " " + item.getString("firstName");
    			} 
    			if (item.getString("groupName") != null){
    				tmp = tmp + " " + item.getString("groupName");
    			}
    			mapTmp.put("description", tmp);
    			listOParty.add(mapTmp);
    		}
    	}
    	return listOParty;
	}
	
	public static BigDecimal calcOrderAdjustmentInDelivery(Delegator delegator, GenericValue orderHeaderAdjustment, String deliveryId) throws GenericEntityException{
		BigDecimal adjustmentAmountInDelivery = BigDecimal.ZERO;
		BigDecimal adj = orderHeaderAdjustment.getBigDecimal("amount");
		BigDecimal ratioItem = LogisticsOrderUtil.calcRotationPriceOfDeliveryAndOrder(delegator, deliveryId);
		adjustmentAmountInDelivery = adj.multiply(ratioItem);
		
		return adjustmentAmountInDelivery;
	}
	
	public static BigDecimal calcOrderItemAdjustmentInDelivery(Delegator delegator, GenericValue orderHeaderAdjustment, String deliveryId) throws GenericEntityException{
		BigDecimal adjustmentAmountInDelivery = BigDecimal.ZERO;
		String orderId = orderHeaderAdjustment.getString("orderId");
		String orderItemSeqId = orderHeaderAdjustment.getString("orderItemSeqId");
		GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		List<GenericValue> orderItems = orderHeader.getRelated("OrderItem", null, null, false);
		List<GenericValue> adjustments = orderHeader.getRelated("OrderAdjustment", null, null, false);
		BigDecimal orderSubTotal = OrderReadHelper.getNewOrderItemsSubTotal(orderItems, adjustments);
		BigDecimal adjustmentAmount = OrderReadHelper.calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal);
		if (UtilValidate.isNotEmpty(orderItem)){
			String productId = orderItem.getString("productId");
			boolean isKg = ProductUtil.isWeightProduct(delegator, productId);
			BigDecimal orderQuantity = BigDecimal.ZERO;
			if (isKg){
				orderQuantity = orderItem.getBigDecimal("selectedAmount"); 
			} else {
				orderQuantity = orderItem.getBigDecimal("quantity"); 
				if (UtilValidate.isNotEmpty(orderItem.get("cancelQuantity"))) {
					orderQuantity = orderQuantity.subtract(orderItem.getBigDecimal("cancelQuantity"));
				}
			}
			
			BigDecimal dlvQuantity = BigDecimal.ZERO;
			List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId, "fromOrderId", orderId, "fromOrderItemSeqId", orderItemSeqId)), null, null, null, false);
			if (!listDlvItems.isEmpty()){
				for (GenericValue dlvItem : listDlvItems){
					String statusId = dlvItem.getString("statusId");
					if ("DELI_ITEM_APPROVED".equals(statusId) || "DELI_ITEM_CREATED".equals(statusId)){
						if (isKg){
							dlvQuantity = dlvQuantity.add(dlvItem.getBigDecimal("amount"));
						} else {
							dlvQuantity = dlvQuantity.add(dlvItem.getBigDecimal("quantity"));
						}
					}
					if ("DELI_ITEM_EXPORTED".equals(statusId)){
						if (isKg){
							dlvQuantity = dlvQuantity.add(dlvItem.getBigDecimal("actualExportedAmount"));
						} else {
							dlvQuantity = dlvQuantity.add(dlvItem.getBigDecimal("actualExportedQuantity"));
						}
					}
					if ("DELI_ITEM_DELIVERED".equals(statusId)){
						if (isKg){
							dlvQuantity = dlvQuantity.add(dlvItem.getBigDecimal("actualDeliveredAmount"));
						} else {
							dlvQuantity = dlvQuantity.add(dlvItem.getBigDecimal("actualDeliveredQuantity"));
						}
					}
				}
			}
			if (orderQuantity.compareTo(BigDecimal.ZERO) > 0 ){
				adjustmentAmountInDelivery  = adjustmentAmount.multiply(dlvQuantity.divide(orderQuantity));
			}
		} 
		return adjustmentAmountInDelivery;
	}
	
	public static List<Map<String, Object>> getListTaxForDelivery(Delegator delegator, List<Map<String, Object>> listTaxTotal, String deliveryId) throws GenericEntityException{
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId",deliveryId));
		String orderId = delivery.getString("orderId");
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		List<GenericValue> adjustmentTmps = orderHeader.getRelated("OrderAdjustment", null, null, false);
		for (Map<String, Object> tax : listTaxTotal){
			BigDecimal percent = (BigDecimal)tax.get("sourcePercentage");
			BigDecimal totalAmount = BigDecimal.ZERO;
			if (!adjustmentTmps.isEmpty()){
				for (GenericValue adj : adjustmentTmps){
					BigDecimal amount = adj.getBigDecimal("amount");
					if ("SALES_TAX".equals(adj.getString("orderAdjustmentTypeId")) && UtilValidate.isNotEmpty(adj.getBigDecimal("sourcePercentage")) && adj.getBigDecimal("sourcePercentage").compareTo(percent) == 0){
						String orderItemSeqId = adj.getString("orderItemSeqId");
						GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
						if (UtilValidate.isNotEmpty(orderItem) && orderItem.getString("isPromo").equals("N")){
							String productId = orderItem.getString("productId");
							boolean isKg = ProductUtil.isWeightProduct(delegator, productId);
							BigDecimal orderQuantity = BigDecimal.ZERO;
							if (isKg){
								orderQuantity = orderItem.getBigDecimal("selectedAmount").multiply(orderItem.getBigDecimal("quantity"));
							} else {
								orderQuantity = orderItem.getBigDecimal("quantity"); 
								if (UtilValidate.isNotEmpty(orderItem.get("cancelQuantity"))) {
									orderQuantity = orderQuantity.subtract(orderItem.getBigDecimal("cancelQuantity"));
								}
							}
							List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId, "fromOrderId", orderId, "fromOrderItemSeqId", orderItemSeqId)), null, null, null, false);
							BigDecimal dlvItemQuantity = BigDecimal.ZERO;
							for (GenericValue dlvItem : listDlvItems){
								String statusId = dlvItem.getString("statusId");
								if ("DELI_ITEM_APPROVED".equals(statusId) || "DELI_ITEM_CREATED".equals(statusId)){
									if (isKg){
										dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("amount"));
									} else {
										dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("quantity"));
									}
								}
								if ("DELI_ITEM_EXPORTED".equals(statusId)){
									if (isKg){
										dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualExportedAmount"));
									} else {
										dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualExportedQuantity"));
									}
								}
								if ("DELI_ITEM_DELIVERED".equals(statusId)){
									if (isKg){
										dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualDeliveredAmount"));
									} else {
										dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualDeliveredQuantity"));
									}
								}
							}
							amount = amount.multiply(dlvItemQuantity.divide(orderQuantity, 10, BigDecimal.ROUND_HALF_UP));
							totalAmount = totalAmount.add(amount);
						}
					}
				}
			}
			tax.put("amount", totalAmount);
		}
		return listTaxTotal;
	}
	
	public static List<GenericValue> getListPromotionForDelivery(Delegator delegator, String deliveryId) throws GenericEntityException{
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId",deliveryId));
		String orderId = delivery.getString("orderId");
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		List<GenericValue> adjustmentTmps = orderHeader.getRelated("OrderAdjustment", null, null, false);
		if (!adjustmentTmps.isEmpty()){
			for (GenericValue adj : adjustmentTmps){
				BigDecimal totalAmount = BigDecimal.ZERO;
				BigDecimal amount = adj.getBigDecimal("amount");
				if (!"SALES_TAX".equals(adj.getString("orderAdjustmentTypeId")) && !"SHIPPING_CHARGES".equals(adj.getString("orderAdjustmentTypeId")) && !"VAT_PRICE_CORRECT".equals(adj.getString("orderAdjustmentTypeId")) && !"VAT_TAX".equals(adj.getString("orderAdjustmentTypeId"))){
					String orderItemSeqId = adj.getString("orderItemSeqId");
					GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
					if (UtilValidate.isNotEmpty(orderItem)){
						String productId = orderItem.getString("productId");
						boolean isKg = ProductUtil.isWeightProduct(delegator, productId);
						
						BigDecimal orderQuantity = BigDecimal.ZERO;
						if (isKg){
							orderQuantity = orderItem.getBigDecimal("selectedAmount");
						} else {
							orderQuantity = orderItem.getBigDecimal("quantity");
							if (UtilValidate.isNotEmpty(orderItem.get("cancelQuantity"))) {
								orderQuantity = orderQuantity.subtract(orderItem.getBigDecimal("cancelQuantity"));
							}
						}
						
						List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId, "fromOrderId", orderId, "fromOrderItemSeqId", orderItemSeqId)), null, null, null, false);
						BigDecimal dlvItemQuantity = BigDecimal.ZERO;
						for (GenericValue dlvItem : listDlvItems){
							String statusId = dlvItem.getString("statusId");
							if ("DELI_ITEM_APPROVED".equals(statusId) || "DELI_ITEM_CREATED".equals(statusId)){
								if (isKg){
									dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("amount"));
								} else {
									dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("quantity"));
								}
							}
							if ("DELI_ITEM_EXPORTED".equals(statusId)){
								if (isKg){
									dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualExportedAmount"));
								} else {
									dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualExportedQuantity"));
								}
							}
							if ("DELI_ITEM_DELIVERED".equals(statusId)){
								if (isKg){
									dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualDeliveredAmount"));
								} else {
									dlvItemQuantity = dlvItemQuantity.add(dlvItem.getBigDecimal("actualDeliveredQuantity"));
								}
							}
						}
						amount = amount.multiply(dlvItemQuantity.divide(orderQuantity, 10, BigDecimal.ROUND_HALF_UP));
						totalAmount = totalAmount.add(amount);
					} else {
						BigDecimal rotation = LogisticsOrderUtil.calcRotationPriceOfDeliveryAndOrder(delegator, deliveryId);
						amount = amount.multiply(rotation);
						totalAmount = totalAmount.add(amount);
					}
				}
				adj.put("amount", totalAmount);
			}
		}
		return adjustmentTmps;
	}
	
	public static BigDecimal calcRotationPriceOfDeliveryAndOrder(Delegator delegator, String deliveryId) throws GenericEntityException{
		BigDecimal rotation = BigDecimal.ZERO;
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId",deliveryId));
		String orderId = delivery.getString("orderId");
		List<GenericValue> deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
		List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "isPromo", "N")), null, null, null, false);
		BigDecimal grandOrderNotTax = OrderReadHelper.getOrderItemsSubTotal(orderItems, null);
		if (!deliveryItems.isEmpty()){
			BigDecimal grandDeliveryNotTax = BigDecimal.ZERO;
			for (GenericValue item : deliveryItems) {
				String statusId = item.getString("statusId");
				GenericValue objOrderItem = null;
				try {
					objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", item.getString("fromOrderItemSeqId")));
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findOne OrderItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return rotation;
				}
				if (UtilValidate.isNotEmpty(objOrderItem)) {
					String productId = objOrderItem.getString("productId");
					boolean isKg = ProductUtil.isWeightProduct(delegator, productId);
					BigDecimal dlvItemQuantity = BigDecimal.ZERO;
					if ("DELI_ITEM_APPROVED".equals(statusId) || "DELI_ITEM_CREATED".equals(statusId)){
						if (isKg){
							dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("amount"));
						} else {
							dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("quantity"));
						}
					}
					if ("DELI_ITEM_EXPORTED".equals(statusId)){
						if (isKg){
							dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("actualExportedAmount"));
						} else {
							dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("actualExportedQuantity"));
						}
					}
					if ("DELI_ITEM_DELIVERED".equals(statusId)){
						if (isKg){
							dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("actualDeliveredAmount"));
						} else {
							dlvItemQuantity = dlvItemQuantity.add(item.getBigDecimal("actualDeliveredQuantity"));
						}
					}
					if ("N".equals(objOrderItem.getString("isPromo"))){
						String orderQuantityUomId = objOrderItem.getString("quantityUomId");
						GenericValue objProduct = null;
						try {
							objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
							Debug.logError(e, errMsg, module);
						}
						String quantityUomId = objProduct.getString("quantityUomId");
						BigDecimal convertNumber = BigDecimal.ONE;
						if (!quantityUomId.equals(orderQuantityUomId)){
							convertNumber = ProductUtil.getConvertPackingNumber(delegator, productId, orderQuantityUomId, quantityUomId);
						}
						BigDecimal unitPrice = objOrderItem.getBigDecimal("alternativeUnitPrice").divide(convertNumber, 3, RoundingMode.HALF_UP);
						if (isKg){
							unitPrice = objOrderItem.getBigDecimal("alternativeUnitPrice").divide(objOrderItem.getBigDecimal("selectedAmount"));
						}
						grandDeliveryNotTax = grandDeliveryNotTax.add(dlvItemQuantity.multiply(unitPrice));
					} else {
						continue;
					}
				}
			}
			if (grandOrderNotTax.compareTo(BigDecimal.ZERO) > 0){
				rotation = grandDeliveryNotTax.divide(grandOrderNotTax, 10, RoundingMode.HALF_UP);
			}
		}
		return rotation;
	}
	
	public static BigDecimal getProductQuantityOnPurchaseOrder(Delegator delegator, String productId, String facilityId)
			throws GenericEntityException {
		List<GenericValue> purchaseOrderList = FastList.newInstance();
		BigDecimal quantityOnOrder = BigDecimal.ZERO;
		Map<String, String> mapConditions = FastMap.newInstance();
		mapConditions.put("productId", productId);
		mapConditions.put("facilityId", facilityId);
		EntityCondition condition = EntityCondition.makeCondition(mapConditions);
		purchaseOrderList = delegator.findList("PurchaseOnOrder", condition, null, null, null, false);
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		String requireAmount = product.getString("requireAmount");
		
		if (UtilValidate.isNotEmpty(purchaseOrderList)) {
			for (GenericValue purchaseOrder : purchaseOrderList) {
				BigDecimal quantity = purchaseOrder.getBigDecimal("quantity");
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					quantity = purchaseOrder.getBigDecimal("selectedAmount");
				} 
				if (UtilValidate.isNotEmpty(quantity)) {
					quantityOnOrder = quantityOnOrder.add(quantity);
				}
			}
		}
		return quantityOnOrder;
	}
	
	public static BigDecimal getQuantitySalesInTimePeriod(Delegator delegator, String productId, String facilityId,
			BigDecimal numberDay) throws GenericEntityException {
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		int periodTmp = numberDay.intValue();
		Timestamp startTime = UtilDateTime.getDayStart(nowTimestamp, periodTmp * (-1));
		// sale
		BigDecimal totalSale = BigDecimal.ZERO;
		BigDecimal totalReturn = BigDecimal.ZERO;
		List<GenericValue> saleList = FastList.newInstance();
		List<EntityCondition> saleConditionList = FastList.newInstance();
		saleConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		saleConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		saleConditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, startTime));
		saleConditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
		EntityCondition saleCond = EntityCondition.makeCondition(saleConditionList);
		saleList = delegator.findList("SaleOrderItemCompleted", saleCond, null, null, null, false);
		
		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		String requireAmount = product.getString("requireAmount");
		
		if (UtilValidate.isNotEmpty(saleList)) {
			for (GenericValue saleOrder : saleList) {
				BigDecimal quantity = saleOrder.getBigDecimal("quantity");
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					quantity = saleOrder.getBigDecimal("selectedAmount");
				} 
				if (UtilValidate.isNotEmpty(quantity)) {
					totalSale = totalSale.add(quantity);
				}
			}
		}
		// return
		// TODO
	/*	List<GenericValue> returnList = FastList.newInstance();
		List<EntityCondition> returnConditionList = FastList.newInstance();
		returnConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		returnConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		returnConditionList
				.add(EntityCondition.makeCondition("returnDate", EntityOperator.GREATER_THAN_EQUAL_TO, startTime));
		returnConditionList
				.add(EntityCondition.makeCondition("returnDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
		EntityCondition returnCond = EntityCondition.makeCondition(returnConditionList);
		returnList = delegator.findList("ReturnHeaderAndShipmentReceipt", returnCond, null, null, null, false);
		if (UtilValidate.isNotEmpty(returnList)) {
			for (GenericValue returnOrder : returnList) {
				BigDecimal quantity = returnOrder.getBigDecimal("quantity");
				if (UtilValidate.isNotEmpty(quantity)) {
					totalReturn = totalReturn.add(quantity);
				}
			}
		}
	*/
		BigDecimal quantity = totalSale.subtract(totalReturn);
		return quantity;

	}
}
