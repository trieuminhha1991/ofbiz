package com.olbius.baselogistics.delivery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;

import java.util.ArrayList;

public class DeliveryHelper {
	public static final String module = DeliveryHelper.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsErrorUiLabels";
	
	public static Map<String, Object> createShipmentFromDelivery(Map<String, Object> parameters) throws GenericEntityException{
		
		//Get parameters
		String deliveryId = (String)parameters.get("deliveryId"); 
		Locale locale = (Locale)parameters.get("locale");
		DispatchContext ctx = (DispatchContext)parameters.get("ctx");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = null;
		Map<String, Object> result = FastMap.newInstance();
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e2) {
			Debug.log(e2.getStackTrace().toString(), module);
		}
		
		//Create Shipment
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
		}
		String no = delivery.getString("no");
		String deliveryTypeId = (String)delivery.get("deliveryTypeId");
		if (deliveryTypeId == null){
			return ServiceUtil.returnError("deliveryTypeId not found!");
		} else {
			List<GenericValue> shipGroups = null;
			Map<String, Object> shipmetCtx = FastMap.newInstance();
			
			if ("DELIVERY_SALES".equals(deliveryTypeId)){
				try {
					shipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition("orderId", delivery.get("orderId")), null, null, null, false);
				} catch (GenericEntityException e1) {
					Debug.log(e1.getStackTrace().toString(), module);
				}
				if (UtilValidate.isNotEmpty(no)){
					GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", no));
					if (UtilValidate.isNotEmpty(shipment)){
						return ServiceUtil.returnError("SHIPMENT_ID_EXISTED");
					} else {
						shipmetCtx.put("shipmentId", no);
					}
				}
				shipmetCtx.put("currencyUomId", delivery.getString("currencyUomId"));
				shipmetCtx.put("primaryOrderId", delivery.getString("orderId"));
				shipmetCtx.put("shipmentTypeId", "SALES_SHIPMENT");
				shipmetCtx.put("statusId", "SHIPMENT_INPUT");
			}
			if ("DELIVERY_PURCHASE".equals(deliveryTypeId)){
				try {
					shipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition("orderId", delivery.get("orderId")), null, null, null, false);
					GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", delivery.get("orderId")));
					shipmetCtx.put("currencyUomId", order.getString("currencyUom"));
				} catch (GenericEntityException e1) {
					Debug.log(e1.getStackTrace().toString(), module);
				}
				shipmetCtx.put("primaryOrderId", delivery.getString("orderId"));
				shipmetCtx.put("shipmentTypeId", "PURCHASE_SHIPMENT");
				shipmetCtx.put("statusId", "PURCH_SHIP_CREATED");
			}
			if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
				try {
					shipGroups = delegator.findList("TransferItemShipGroup", EntityCondition.makeCondition("transferId", delivery.get("transferId")), null, null, null, false);
				} catch (GenericEntityException e1) {
					Debug.log(e1.getStackTrace().toString(), module);
				}
				shipmetCtx.put("primaryTransferId", delivery.getString("transferId"));
				shipmetCtx.put("shipmentTypeId", "TRANSFER");
				shipmetCtx.put("statusId", "SHIPMENT_INPUT");
			} 
			
			GenericValue shipGroup = shipGroups.get(0);
			
			shipmetCtx.put("destinationContactMechId", delivery.getString("destContactMechId"));
			shipmetCtx.put("destinationFacilityId", delivery.getString("destFacilityId"));
			shipmetCtx.put("estimatedArrivalDate", delivery.getTimestamp("estimatedArrivalDate"));
			shipmetCtx.put("estimatedShipDate", delivery.getTimestamp("estimatedStartDate"));
			shipmetCtx.put("locale", locale);
			shipmetCtx.put("originContactMechId", delivery.getString("originContactMechId"));
			shipmetCtx.put("originFacilityId", delivery.getString("originFacilityId"));
			shipmetCtx.put("partyIdFrom", delivery.getString("partyIdFrom"));
			shipmetCtx.put("partyIdTo", delivery.getString("partyIdTo"));
			shipmetCtx.put("defaultWeightUomId", delivery.getString("defaultWeightUomId"));
			shipmetCtx.put("primaryShipGroupSeqId", shipGroup.get("shipGroupSeqId"));
			shipmetCtx.put("userLogin", userLogin);
			LocalDispatcher localDis = ctx.getDispatcher();
			try {
				 result = localDis.runSync("createShipment", shipmetCtx);
			} catch (ServiceAuthException e) {
				Debug.log(e.getStackTrace().toString(), module);
			} catch (ServiceValidationException e) {
				Debug.log(e.getStackTrace().toString(), module);
			} catch (GenericServiceException e) {
				Debug.log(e.getStackTrace().toString(), module);
			}
			
			delivery.put("shipmentId", result.get("shipmentId"));
			try {
				delivery.store();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
			}
		}
		
		return result;
	}
	
	public static  Map<String, Object> createShipmentItemFromDelivery(Map<String, Object> parameters){
		
		//Get parameters
		String productId = (String)parameters.get("productId");
		BigDecimal quantity = (BigDecimal)parameters.get("quantity");
		String shipmentId = (String)parameters.get("shipmentId");
		Locale locale = (Locale)parameters.get("locale");
		DispatchContext ctx = (DispatchContext)parameters.get("ctx");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), false);
		}
		//Create shipment item
		Map<String, Object> shipmetItemCtx = FastMap.newInstance();
		shipmetItemCtx.put("productId", productId);
		shipmetItemCtx.put("quantity", quantity);
		shipmetItemCtx.put("shipmentId", shipmentId);
		shipmetItemCtx.put("locale", locale);
		shipmetItemCtx.put("userLogin", userLogin);
		LocalDispatcher localDis = ctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		try {
			result = localDis.runSync("createShipmentItem", shipmetItemCtx);
		} catch (ServiceAuthException e) {
			Debug.log(e.getStackTrace().toString(), module);
		} catch (ServiceValidationException e) {
			Debug.log(e.getStackTrace().toString(), module);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return result;
	}
	
	public static Map<String, Object> createShipGroupFromDelivery(Map<String, Object> parameters){
		
		//Get parameters
		String deliveryId = (String)parameters.get("deliveryId"); 
		Locale locale = (Locale)parameters.get("locale");
		DispatchContext ctx = (DispatchContext)parameters.get("ctx");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e2) {
			Debug.log(e2.getStackTrace().toString(), module);
		}
		//Create OrderItemShipGroup
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
		}
		List<GenericValue> shipGroups = null;
		try {
			shipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition("orderId", delivery.get("orderId")), null, null, null, false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
		}
		GenericValue shipGroup = shipGroups.get(0);
		Map<String, String> mapCondition = FastMap.newInstance();
		mapCondition.put("orderId", delivery.getString("orderId"));
		mapCondition.put("shipGroupSeqId", shipGroup.getString("shipGroupSeqId"));
		List<GenericValue> listDelivery = null;
		try {
			listDelivery = delegator.findList("Delivery", EntityCondition.makeCondition(mapCondition, EntityJoinOperator.AND), null, null, null, false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
		}
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> shipGroupCtx = FastMap.newInstance();
		if(UtilValidate.isEmpty(listDelivery)){
			shipGroupCtx.put("orderId", delivery.getString("orderId"));
			shipGroupCtx.put("contactMechId", delivery.getString("destContactMechId"));
			shipGroupCtx.put("shipGroupSeqId", shipGroup.get("shipGroupSeqId"));
			shipGroupCtx.put("locale", locale);
			shipGroupCtx.put("userLogin", userLogin);
			LocalDispatcher localDis = ctx.getDispatcher();
			try {
				localDis.runSync("updateOrderItemShipGroup", shipGroupCtx);
			} catch (GenericServiceException e) {
				Debug.log(e.getStackTrace().toString(), module);
			}
			result.put("shipGroupSeqId", shipGroup.get("shipGroupSeqId"));
		}else{
			shipGroupCtx.put("orderId", delivery.getString("orderId"));
			shipGroupCtx.put("contactMechId", delivery.getString("destContactMechId"));
			shipGroupCtx.put("locale", locale);
			shipGroupCtx.put("userLogin", userLogin);
			LocalDispatcher localDis = ctx.getDispatcher();
			try {
				result = localDis.runSync("createOrderItemShipGroup", shipGroupCtx);
			} catch (GenericServiceException e) {
				Debug.log(e.getStackTrace().toString(), module);
			}
		}
		
		delivery.put("shipGroupSeqId", result.get("shipGroupSeqId"));
		try {
			delivery.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return result;
	}
	
	public static void createShipGroupAssFromDelivery(Map<String, Object> parameters){
		
		//Get parameters
		DispatchContext ctx = (DispatchContext)parameters.get("ctx");
		String orderId = (String)parameters.get("orderId"); 
		String orderItemSeqId = (String)parameters.get("orderItemSeqId");
		String shipGroupSeqId = (String)parameters.get("shipGroupSeqId");
		BigDecimal quantity = (BigDecimal)parameters.get("quantity");
		Delegator delegator = ctx.getDelegator();
		GenericValue shipGroupAssoc = null;
		try {
			shipGroupAssoc = delegator.findOne("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", shipGroupSeqId), false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
		}
		if(!UtilValidate.isEmpty(shipGroupAssoc)){
			shipGroupAssoc.put("quantity", quantity);
			try {
				shipGroupAssoc.store();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
			}
		}else{
			//Create OrderItemShipGroup
			GenericValue shipGroupAss = delegator.makeValue("OrderItemShipGroupAssoc");
			shipGroupAss.put("orderId", orderId);
			shipGroupAss.put("orderItemSeqId", orderItemSeqId);
			shipGroupAss.put("shipGroupSeqId", shipGroupSeqId);
			shipGroupAss.put("quantity", quantity);
			try {
				shipGroupAss.create();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
			}
		}
		
	}
	
	public static Map<String, Object> createOrderShipment(Map<String, Object> parameters){
		//Get Parameters
		String orderId = (String) parameters.get("orderId");
		String orderItemSeqId = (String)parameters.get("orderItemSeqId");
		String shipGroupSeqId = (String)parameters.get("shipGroupSeqId");
		String shipmentId = (String)parameters.get("shipmentId");
		String shipmentItemSeqId = (String)parameters.get("shipmentItemSeqId");
		BigDecimal quantity = (BigDecimal)parameters.get("quantity");
		Locale locale = (Locale)parameters.get("locale");
		DispatchContext ctx = (DispatchContext)parameters.get("ctx");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e2) {
			Debug.log(e2.getStackTrace().toString(), module);
		}
		//Create OrderShipment
		Map<String, Object> orderShipmentCtx = FastMap.newInstance();
		orderShipmentCtx.put("orderId", orderId);
		orderShipmentCtx.put("orderItemSeqId", orderItemSeqId);
		orderShipmentCtx.put("shipGroupSeqId", shipGroupSeqId);
		orderShipmentCtx.put("shipmentId", shipmentId);
		orderShipmentCtx.put("shipmentItemSeqId", shipmentItemSeqId);
		orderShipmentCtx.put("quantity", quantity);
		orderShipmentCtx.put("userLogin", userLogin);
		orderShipmentCtx.put("locale", locale);
		LocalDispatcher localDis = ctx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		try {
			result = localDis.runSync("createOrderShipment", orderShipmentCtx);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return result;
	}
	
	public static void delOrderItemShipmentGroup(Map<String, Object> parameters){
		//Get Parameters
		String orderId = (String) parameters.get("orderId");
		String shipGroupSeqId = (String)parameters.get("shipGroupSeqId");
		Map<String, String> mapCon = FastMap.newInstance();
		mapCon.put("orderId", orderId);
		mapCon.put("shipGroupSeqId", shipGroupSeqId);
		
		Delegator delegator = (Delegator)parameters.get("delegator");
		
		//Delete OrderItemShipmentGroupAssoc
		try {
			List<GenericValue> orderItemShipmentGrpAssoc = delegator.findList("OrderItemShipmentGroupAssoc", EntityCondition.makeCondition(mapCon),null, null, null, false);
			for(GenericValue item : orderItemShipmentGrpAssoc){
				item.remove();
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		
		//Delete OrderItemShipmentGroup
		try {
			GenericValue orderItemShipmentGrp = delegator.findOne("OrderItemShipmentGroup", UtilMisc.toMap("orderId", orderId, "shipGroupSeqId", shipGroupSeqId), false);
			orderItemShipmentGrp.remove();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
	}
	
	public static void issuseDelivery(Map<String, Object> parameters) throws Exception{
		//get parameters
		String deliveryId = (String) parameters.get("deliveryId");
		String deliveryItemSeqId = (String) parameters.get("deliveryItemSeqId");
		DispatchContext context = (DispatchContext) parameters.get("context");
		Delegator delegator = context.getDelegator();
		Locale locale = (Locale) parameters.get("locale");
		BigDecimal quantity = (BigDecimal) parameters.get("quantity");
		BigDecimal weight = (BigDecimal) parameters.get("weight");
		String shipGroupSeqId = (String)parameters.get("shipGroupSeqId");	
		//Get Delivery And DeliveryItem
		GenericValue delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		GenericValue deliveryItem = delegator.findOne("DeliveryItem", UtilMisc.toMap("deliveryItemSeqId", deliveryItemSeqId, "deliveryId", deliveryId), false);
		
		//Get Shipment
		String shipmentId = delivery.getString("shipmentId");
		Map<String, Object> mapConditionShipment = FastMap.newInstance();
		mapConditionShipment.put("shipmentId", shipmentId);
		GenericValue shipment = delegator.findOne("Shipment", mapConditionShipment, false);
		
		String deliveryTypeId = (String)delivery.get("deliveryTypeId");
		if (deliveryTypeId != null){
			if ("DELIVERY_SALES".equals(deliveryTypeId)){
				//Get orderItemShipGrpInvRes
				Map<String, Object> mapCondition = FastMap.newInstance();
				mapCondition.put("orderId", deliveryItem.get("fromOrderId"));
				mapCondition.put("orderItemSeqId", deliveryItem.get("fromOrderItemSeqId"));
				mapCondition.put("shipGroupSeqId", shipGroupSeqId);
				mapCondition.put("inventoryItemId", deliveryItem.get("inventoryItemId"));
				List<GenericValue> orderItemShipGrpInvReses = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(mapCondition), null, null, null, false);
				GenericValue orderItemShipGrpInvRes = null;
				for (GenericValue item : orderItemShipGrpInvReses) {
					if (item.getBigDecimal("quantity").compareTo(quantity) >= 0){
						orderItemShipGrpInvRes = item;
					}
				}
				if (orderItemShipGrpInvRes == null){
					ServiceUtil.returnError("OLBIUS: Can not get OrderItemShipGrpReserves");
				}
				//Get userLogin system
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				
				//Call issueOrderItemShipGrpInvResToShipment service
				Map<String, Object> issueContext = FastMap.newInstance();
				issueContext.put("inventoryItemId", orderItemShipGrpInvRes.getString("inventoryItemId"));
				issueContext.put("locale", locale);
				issueContext.put("orderId", orderItemShipGrpInvRes.getString("orderId"));
				issueContext.put("orderItemSeqId", orderItemShipGrpInvRes.getString("orderItemSeqId"));
				issueContext.put("shipGroupSeqId", shipGroupSeqId);
				issueContext.put("shipmentId", shipment.getString("shipmentId"));
				issueContext.put("quantity", quantity);
				issueContext.put("weight", weight);
				issueContext.put("userLogin", userLogin);
				LocalDispatcher dispatcher = context.getDispatcher();
				dispatcher.runSync("issueOrderItemShipGrpInvResToShipment", issueContext);
			} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)) {
				//Get transferItemShipGrpInvRes
				Map<String, Object> mapCondition = FastMap.newInstance();
				mapCondition.put("transferId", deliveryItem.get("fromTransferId"));
				mapCondition.put("transferItemSeqId", deliveryItem.get("fromTransferItemSeqId"));
				mapCondition.put("inventoryItemId", deliveryItem.getString("inventoryItemId"));
//				mapCondition.put("shipGroupSeqId", shipment.getString("primaryShipGroupSeqId"));
				List<GenericValue> transferItemShipGrpInvReses = delegator.findList("TransferItemShipGrpInvRes", EntityCondition.makeCondition(mapCondition), null, null, null, false);
				GenericValue transferItemShipGrpInvRes = null;
				// find in list reserve inventory have expire date and product id exactly with item exported
				if (!transferItemShipGrpInvReses.isEmpty()){
					for (GenericValue res : transferItemShipGrpInvReses){
						if (res.getBigDecimal("quantity").compareTo(quantity) >= 0){
							transferItemShipGrpInvRes = res;
						}
					}
				} else {
					throw new NullPointerException("TransferItemShipGrpInvRes is Null");
				}
				
				//Get userLogin system
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				
				//Call issueTransferItemShipGrpInvResToShipment service
				Map<String, Object> issueContext = FastMap.newInstance();
				issueContext.put("inventoryItemId", transferItemShipGrpInvRes.getString("inventoryItemId"));
				issueContext.put("locale", locale);
				issueContext.put("transferId", transferItemShipGrpInvRes.getString("transferId"));
				issueContext.put("transferItemSeqId", transferItemShipGrpInvRes.getString("transferItemSeqId"));
				issueContext.put("shipGroupSeqId", transferItemShipGrpInvRes.getString("shipGroupSeqId"));
				issueContext.put("shipmentId", shipment.getString("shipmentId"));
				issueContext.put("quantity", quantity);
				issueContext.put("weight", weight);
				issueContext.put("userLogin", userLogin);
				LocalDispatcher dispatcher = context.getDispatcher();
				dispatcher.runSync("issueTransferItemShipGrpInvResToShipment", issueContext);
			}
		} 
	}
	
	public static Boolean checkDeliveryHasCreatedForOrder(Delegator delegator, String orderId) throws GenericEntityException{
		Boolean hasExisted = false;
		if (UtilValidate.isNotEmpty(orderId)){
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DLV_CANCELLED");
			EntityCondition orderCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
			listConds.add(statusCond);
			listConds.add(orderCond);
			List<GenericValue> listDeliveryByOrder = delegator.findList("Delivery", EntityCondition.makeCondition(listConds, EntityOperator.AND),  null, null, null, false);
			if (listDeliveryByOrder.isEmpty()){
				hasExisted = false;
			} else {
				hasExisted = true;
			}
		}
		return hasExisted;
	}
	
	public static Boolean checkShipmentCreatedDoneForDeliveryEntry(Delegator delegator, String shipmentId) throws GenericEntityException, GenericServiceException{
		Boolean createdDone = true;
   	    List<GenericValue> listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
   	    List<GenericValue> listShipmentItemInDEs = delegator.findList("DeliveryEntryShipment", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
   	    if (!listShipmentItems.isEmpty() && !listShipmentItemInDEs.isEmpty()){
   	    	for (GenericValue item : listShipmentItems){
   	    		BigDecimal shipmentQty = item.getBigDecimal("quantity");
   	    		BigDecimal createdQty = BigDecimal.ZERO;
   	    		for (GenericValue itemInDe : listShipmentItemInDEs){
   	    			if (itemInDe.getString("shipmentId").equals(item.getString("shipmentId")) && itemInDe.getString("shipmentItemSeqId").equals(item.getString("shipmentItemSeqId"))){
   	    				createdQty = createdQty.add(itemInDe.getBigDecimal("quantity"));
   	    			}
   	    		}
   	    		if (shipmentQty.compareTo(createdQty) > 0){
   	    			createdDone = false;
   	    			break;
   	    		}
   	    	}
   	    } else {
   	    	if (listShipmentItemInDEs.isEmpty() && !listShipmentItems.isEmpty()){
   	    		createdDone = false;
   	    	} else {
   	    		createdDone = true;
   	    	}
   	    }
   	    return createdDone;
	}

	public static void updatePackStatus(Delegator delegator, String shippingTripId, String packId, String statusId) throws GenericEntityException, GenericServiceException{
//		if(packId != null){
//			try {
//				GenericValue pack = delegator.findOne("Pack", false, UtilMisc.toMap("packId", packId));
//				pack.put("statusId", statusId);
//				pack.store();
//			}catch ( Exception e){
//				e.printStackTrace();
//			}
//		}else{
		List<EntityCondition> condsToFindPack = new ArrayList<EntityCondition>();
		condsToFindPack.add(EntityCondition.makeCondition("shippingTripId", EntityOperator.LIKE, shippingTripId));
		condsToFindPack.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_LIKE, "SHIP_PACK_CANCELLED"));
		List<GenericValue> listTripPacks = delegator.findList("ShippingTripPack", EntityCondition.makeCondition(condsToFindPack), null, null, null, false);
		for ( GenericValue item : listTripPacks
		) {
			String id = item.getString("packId");
			try {
				GenericValue pack = delegator.findOne("Pack", false, UtilMisc.toMap("packId", id));
				pack.put("statusId", statusId);
				pack.store();
			}catch ( Exception e){
				e.printStackTrace();
			}
//			}
		}
	}
}
	