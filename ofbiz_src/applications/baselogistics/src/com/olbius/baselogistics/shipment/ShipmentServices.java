package com.olbius.baselogistics.shipment;

import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.LogisticsServices;
import com.olbius.baselogistics.delivery.DeliveryHelper;
import com.olbius.baselogistics.transfer.TransferReadHepler;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.product.util.ProductUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

public class ShipmentServices {
	
	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	
	public static Map<String, Object> checkOrderAndShipmentStatus(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = new FastMap<String, Object>();
		String shipmentId = (String)context.get("shipmentId"); 
		GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
		String statusId = shipment.getString("statusId");		
		if ("SHIPMENT_DELIVERED".equals(statusId)){
			String orderId = shipment.getString("primaryOrderId");
			EntityCondition Cond1 = EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId);
			EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED");
			List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
			EntityCondition allConds = EntityCondition.makeCondition(listConds,
					EntityOperator.AND);
			List<GenericValue> listShipmentByOrder = delegator.findList("Shipment", allConds, null, null, null, false);
			if (!listShipmentByOrder.isEmpty()){
				Boolean allDelivered = true;
				for (GenericValue shmp : listShipmentByOrder){
					if (!"SHIPMENT_DELIVERED".equals(shmp.getString("statusId"))){
						allDelivered = false;
					}
				}
				if (allDelivered){
					GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
					if ("ORDER_IN_TRANSIT".equals(order.getString("statusId"))){
						Map<String, Object> mapSetStatusOrder = FastMap.newInstance();
						mapSetStatusOrder.put("userLogin", (GenericValue)context.get("userLogin"));
						mapSetStatusOrder.put("orderId", orderId);
						mapSetStatusOrder.put("statusId", "ORDER_COMPLETED");
						mapSetStatusOrder.put("setItemStatus", "Y");
						dispatcher.runSync("changeOrderStatus", mapSetStatusOrder);
					}
				}
			}
		}
		result.put("shipmentId", shipmentId);
		return result;
	}
	
	public static Map<String, Object> checkDeliveryEntrytatus(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = new FastMap<String, Object>();
		String shipmentId = (String)context.get("shipmentId"); 
		GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
		String deliveryEntryId = shipment.getString("deliveryEntryId");
		
		Boolean allPacked = true;
		Boolean allPicked = true;
		Boolean allShipped = true;
		Boolean allDelivered = true;
		Boolean allScheduled = true;
		Boolean allCancelled = true;
		
		String newDEStatusId = null;
		if (deliveryEntryId != null){
			List<GenericValue> listShipmentByDE = delegator.findList("Shipment", EntityCondition.makeCondition("deliveryEntryId", deliveryEntryId), null, null, null, false);
			if (!listShipmentByDE.isEmpty()){
				for (GenericValue shmp : listShipmentByDE){
					if (!"SHIPMENT_PACKED".equals(shmp.getString("statusId"))){
						allPacked = false;
						if (!"SHIPMENT_PICKED".equals(shmp.getString("statusId"))){
							allPicked = false;
							if (!"SHIPMENT_SCHEDULED".equals(shmp.getString("statusId"))){
								allScheduled = false;
								if (!"SHIPMENT_SHIPPED".equals(shmp.getString("statusId"))){
									allShipped = false;
									if (!"SHIPMENT_DELIVERED".equals(shmp.getString("statusId"))){
										allDelivered = false;
										if (!"SHIPMENT_CANCELLED".equals(shmp.getString("statusId"))){
											allCancelled = false;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
			if (allPacked){
				newDEStatusId = "DELI_ENTRY_SCHEDULED";
			} else if (allPicked){
				newDEStatusId ="DELI_ENTRY_SCHEDULED";
			} else if (allScheduled){
				newDEStatusId ="DELI_ENTRY_SCHEDULED";
			} else if (allShipped){
				newDEStatusId ="DELI_ENTRY_SHIPPING";
			} else if (allDelivered){
				newDEStatusId ="DELI_ENTRY_DELIVERED";
			} else if (allCancelled){
				newDEStatusId ="DELI_ENTRY_CANCELED";
			}
			
			if (newDEStatusId != null){
				GenericValue deliveryEntry = delegator.findOne("DeliveryEntry", false, UtilMisc.toMap("deliveryEntryId", deliveryEntryId));
				Map<String, Object> mapSetStatusDE = FastMap.newInstance();
				mapSetStatusDE.put("userLogin", (GenericValue)context.get("userLogin"));
				mapSetStatusDE.put("deliveryEntryId", deliveryEntryId);
				mapSetStatusDE.put("statusId", newDEStatusId);
				mapSetStatusDE.put("oldStatusId", deliveryEntry.getString("statusId"));
				mapSetStatusDE.put("setItemStatus", "N");
				
				dispatcher.runSync("changeDeliveryEntryStatus", mapSetStatusDE);
			}
		}
		
		result.put("shipmentId", shipmentId);
		return result;
	}
	
	public static Map<String, Object> changeDeliveryEntryStatus(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result = new FastMap<String, Object>();
		String deliveryEntryId = (String)context.get("deliveryEntryId"); 
		GenericValue deliveryEntry = delegator.findOne("DeliveryEntry", false, UtilMisc.toMap("deliveryEntryId", deliveryEntryId));
		String setItemStatus = (String)context.get("setItemStatus");
		String oldStatusId = (String)context.get("oldStatusId");
		String statusId = (String)context.get("statusId");
		
		if (!oldStatusId.equals(statusId)){
			deliveryEntry.put("statusId", statusId);
			delegator.store(deliveryEntry);
			if ("Y".equals(setItemStatus)){
				String shipmentStatusId = null;
				if ("DELI_ENTRY_SHIPPING".equals(statusId)){
					shipmentStatusId ="SHIPMENT_SHIPPED";
				} else if ("DELI_ENTRY_DELIVERED".equals(statusId)){
					shipmentStatusId ="SHIPMENT_DELIVERED";
				}
				List<GenericValue> listShipments = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId)), null, null, null, false);
				if (!listShipments.isEmpty()){
					for (GenericValue shipment : listShipments){
						Map<String, Object> mapTmp = FastMap.newInstance();
						mapTmp.put("shipmentId", shipment.getString("shipmentId"));
						mapTmp.put("statusId", shipmentStatusId);
						mapTmp.put("userLogin", context.get("userLogin"));
    					dispatcher.runSync("updateShipment", mapTmp);
					}
				}
			}
		}
		result.put("deliveryEntryId", deliveryEntryId);
		return result;
	}
	
	public static Map<String,Object> createShipmentFromDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		String deliveryId = (String)context.get("deliveryId");
		Locale locale = (Locale)context.get("locale");
		
		//Create Shipment
		Map<String, Object> shipmentPara = FastMap.newInstance();
		shipmentPara.put("deliveryId", deliveryId);
		shipmentPara.put("ctx", ctx);
		shipmentPara.put("locale", locale);
		DeliveryHelper.createShipmentFromDelivery(shipmentPara);
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "CreateSuccessfully", locale));
	}
	
	public static Map<String, Object> updateShipmentByDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        String deliveryId = (String)context.get("deliveryId");
        GenericValue delivery = null;
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			if (delivery == null){
				return ServiceUtil.returnError("Delivery not found");
			}
			String deliveryTypeId = delivery.getString("deliveryTypeId");
			if ("DELIVERY_SALES".equals(deliveryTypeId)){
				String shipmentId = (String)delivery.get("shipmentId");
				if (UtilValidate.isNotEmpty(shipmentId)) {
					GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
					String deliveryStatusId = (String)delivery.get("statusId");
					String shipmentStatusId = null;
					if ("DLV_EXPORTED".equals(deliveryStatusId)){
						shipmentStatusId = "SHIPMENT_PACKED";
					} else if ("DLV_DELIVERED".equals(deliveryStatusId)){
						shipmentStatusId = "SHIPMENT_DELIVERED";
					}
					if (shipmentStatusId != null){
						Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", shipment.getString("statusId"), "statusIdTo", shipmentStatusId);
		                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
		                if (statusChange != null){
		    				try {
		    					//GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		    					Map<String, Object> mapShipment = FastMap.newInstance();
		        				mapShipment.put("userLogin", userLogin);
		        				mapShipment.put("shipmentId", shipmentId);
		        				mapShipment.put("statusId", shipmentStatusId);
		    					dispatcher.runSync("updateShipment", mapShipment);
		    					if ("DLV_EXPORTED".equals(deliveryStatusId) && (shipmentStatusId.equals("SHIPMENT_PACKED") || shipmentStatusId.equals("SHIPMENT_PICKED"))){
		    						// FIXME: update shipment to shipped after delivery exported. To exactly, must update delivery entry to shipping and after that update shipment status
		    						mapShipment = FastMap.newInstance();
		            				mapShipment.put("userLogin", userLogin);
		            				mapShipment.put("shipmentId", shipmentId);
		            				mapShipment.put("statusId", "SHIPMENT_SHIPPED");
		        					dispatcher.runSync("updateShipment", mapShipment);
		    					}
		    				} catch (GenericServiceException e) {
		    					return ServiceUtil.returnError("OLBIUS: updateShipment error" + e.toString());
		    				}
		                }
					}
				}
			}
        } catch(GenericEntityException e){
        	Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "HasErrorWhenProcessing", locale));
        }
        return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getFilterShipment(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = new ArrayList<GenericValue>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	String deliveryEntryId = null;
    	String originFacilityId = null; 
    	String shipmentTypeId = null; 
    	if (parameters.get("deliveryEntryId") != null && parameters.get("deliveryEntryId").length > 0){
    		deliveryEntryId = parameters.get("deliveryEntryId")[0];
    	}
		if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
			originFacilityId = parameters.get("facilityId")[0];
    	}
		if (parameters.get("shipmentTypeId") != null && parameters.get("shipmentTypeId").length > 0){
			shipmentTypeId = parameters.get("shipmentTypeId")[0];
		}
    	if (UtilValidate.isNotEmpty(deliveryEntryId)){
    		GenericValue deliveryEntry = null;
			deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
			originFacilityId = deliveryEntry.getString("facilityId");
    	} 
    	
    	if (UtilValidate.isNotEmpty(originFacilityId)) {
        	EntityCondition facCond = EntityCondition.makeCondition("originFacilityId", originFacilityId);
        	listAllConditions.add(facCond);
    	} 
    	
    	EntityCondition deliveryEntryCon = EntityCondition.makeCondition("deliveryEntryId", EntityComparisonOperator.EQUALS , null);
    	EntityCondition shipmentTypeCon = EntityCondition.makeCondition("shipmentTypeId", EntityComparisonOperator.EQUALS , shipmentTypeId);
    	
    	listAllConditions.add(shipmentTypeCon);
    	listAllConditions.add(deliveryEntryCon);
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("PURCH_SHIP_RECEIVED", "PURCH_SHIP_SHIPPED", "SHIPMENT_DELIVERED", "SHIPMENT_SHIPPED")));
		listIterator = delegator.findList("DeliveryAndShipmentDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, opts, false);
		
    	List<Map<String, Object>> listShipments = new ArrayList<Map<String, Object>>();
    	if (!listIterator.isEmpty()){
    		for (GenericValue shipment : listIterator){
//    			Boolean createdDone = false;
//				createdDone = LogisticsProductUtil.checkShipmentCreatedDoneForDeliveryEntry(delegator, shipment.getString("shipmentId"));
//    			if (!createdDone){
    				Map<String, Object> mapReturn = FastMap.newInstance();
        			String shipmentId = shipment.getString("shipmentId");
        			mapReturn.put("shipmentId", shipment.get("shipmentId"));
        			mapReturn.put("shipmentTypeId", shipment.get("shipmentTypeId"));
        			mapReturn.put("statusId", shipment.get("statusId"));
        			mapReturn.put("primaryOrderId", shipment.get("primaryOrderId"));
        			mapReturn.put("primaryTransferId", shipment.get("primaryTransferId"));
        			mapReturn.put("estimatedReadyDate", shipment.get("estimatedReadyDate"));
        			mapReturn.put("estimatedShipDate", shipment.get("estimatedShipDate"));
        			mapReturn.put("estimatedArrivalDate", shipment.get("estimatedArrivalDate"));
        			mapReturn.put("estimatedShipCost", shipment.get("estimatedShipCost"));
        			mapReturn.put("currencyUomId", shipment.get("currencyUomId"));
        			mapReturn.put("originFacilityId", shipment.get("originFacilityId"));
        			mapReturn.put("originContactMechId", shipment.get("originContactMechId"));
        			mapReturn.put("destinationFacilityId", shipment.get("destinationFacilityId"));
        			mapReturn.put("destinationContactMechId", shipment.get("destinationContactMechId"));
        			mapReturn.put("destFacilityName", shipment.get("destFacilityName"));
        			mapReturn.put("originFacilityName", shipment.get("originFacilityName"));
        			mapReturn.put("destAddress", shipment.get("destAddress"));
        			mapReturn.put("originAddress", shipment.get("originAddress"));
        			mapReturn.put("defaultWeightUomId", shipment.get("defaultWeightUomId"));
        			mapReturn.put("deliveryId", shipment.get("deliveryId"));
        			BigDecimal totalWeight = BigDecimal.ZERO;
        			
        			Map<String, Object> mapTmp = FastMap.newInstance();
        			mapTmp.put("shipmentId", shipmentId);
        			mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
        			
        			LocalDispatcher dispatcher = ctx.getDispatcher();
        			try {
    					Map<String, Object> result = dispatcher.runSync("getTotalShipmentItem", mapTmp);
    					totalWeight = (BigDecimal)result.get("totalWeight");
    				} catch (GenericServiceException e) {
    					ServiceUtil.returnError("getTotalShipmentItem error");
    				}
        			mapReturn.put("totalWeight", totalWeight);
        			
        			// detail 
        			List<GenericValue> listShipmentItems = delegator.findList("ShipmentItemDetail", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
        			List<Map<String, Object>> rowDetail = new ArrayList<Map<String,Object>>();
    				if(!UtilValidate.isEmpty(listShipmentItems)){
    					for(GenericValue item : listShipmentItems){
    						List<GenericValue> listShipmentItemInDe = delegator.findList("DeliveryEntryShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
    						BigDecimal quantity = item.getBigDecimal("quantity");
    						if (!listShipmentItemInDe.isEmpty()){
    							for (GenericValue itemInDe : listShipmentItemInDe){
    								quantity = quantity.subtract(itemInDe.getBigDecimal("quantity"));
    							}
    						}
    						if (quantity.compareTo(BigDecimal.ZERO) > 0){
    							Map<String, Object> childDetail = new HashMap<String, Object>(); 
        						childDetail.put("shipmentId", shipmentId);
        						childDetail.put("shipmentItemSeqId", item.getString("shipmentItemSeqId"));
        						childDetail.put("productId", item.getString("productId"));
        						childDetail.put("productCode", item.getString("productCode"));
        						childDetail.put("productName", item.getString("productName"));
        						childDetail.put("quantity", quantity);
        						childDetail.put("quantityCreate", item.getBigDecimal("quantity"));
        						childDetail.put("weight", item.getBigDecimal("weight"));
        						childDetail.put("weightUomId", item.getString("weightUomId"));
        						childDetail.put("quantityUomId", item.getString("quantityUomId"));
        						childDetail.put("deliveryId", item.getString("deliveryId"));
        						rowDetail.add(childDetail);
    						}
    					}
    				}
    				mapReturn.put("rowDetail", rowDetail);
        			listShipments.add(mapReturn);
    			}
//    		}
    	}
    			
    	successResult.put("listIterator", listShipments);
    	return successResult;
    }
	
	public static Map<String, Object> getTotalShipmentItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
		String shipmentId = (String)context.get("shipmentId");
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		GenericValue shipment;
		shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
		BigDecimal totalWeight = BigDecimal.ZERO;
		String weightUomDefault = (String)shipment.get("defaultWeightUomId");
		if (weightUomDefault == null){
			shipment.put("defaultWeightUomId", "WT_kg");
			delegator.store(shipment);
		}
		List<GenericValue> listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipment.get("shipmentId"))), null, null, null, false);
		if (!listShipmentItems.isEmpty()){
			for (GenericValue item : listShipmentItems){
				BigDecimal quantity = BigDecimal.ZERO;
				BigDecimal weight = BigDecimal.ZERO;
				GenericValue product = item.getRelatedOne("Product", false);
				if (item.getBigDecimal("quantity") != null) {
					quantity = item.getBigDecimal("quantity");
				}
				if (product.getBigDecimal("weight") != null) {
					weight = product.getBigDecimal("weight").multiply(quantity);
				} else {
					if (product.getBigDecimal("productWeight") != null) {
						weight = product.getBigDecimal("productWeight").multiply(quantity);
					}
				}
				String weightUomId = product.getString("weightUomId");
				GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", weightUomDefault, "uomIdTo", weightUomId));
				BigDecimal conversionFactor = BigDecimal.ONE;
				if (UtilValidate.isNotEmpty(conversion)){
					conversionFactor = conversion.getBigDecimal("conversionFactor");
					if (conversionFactor.compareTo(BigDecimal.ONE) >= 1){
						conversionFactor = (BigDecimal.ONE).divide(conversionFactor, 4, RoundingMode.HALF_UP);
					}
				}
				BigDecimal itemWeight = weight.multiply(conversionFactor);
				totalWeight = totalWeight.add(itemWeight);
			}
		}
		result.put("totalWeight", totalWeight);
		return result;
	}
	
public static Map<String, Object> assignShipmentToDE(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		
		//Get Parameters
		String deliveryEntryId = (String)context.get("deliveryEntryId");
		String shipmentId = (String)context.get("shipmentId");
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		//Update deliveryEntryId to shipment
		GenericValue shipment = null;
		try {
			shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "AssignShipmentToDEFail", locale));
		}
		if (UtilValidate.isEmpty(shipment.get("deliveryEntryId")) || !shipment.get("deliveryEntryId").equals(deliveryEntryId)){
			shipment.put("deliveryEntryId", deliveryEntryId);
			try {
				shipment.store();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "AssignShipmentToDEFail", locale));
			}
			
			//Update weight for DE
			GenericValue deliveryEntry = null;
			try {
				deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "AssignShipmentToDEFail", locale));
			}
			BigDecimal weight = deliveryEntry.getBigDecimal("weight");
			Map<String, Object> getTotalShipmentItemCtx = FastMap.newInstance();
			getTotalShipmentItemCtx.put("shipmentId", shipmentId);
			getTotalShipmentItemCtx.put("userLogin", userLogin);
			Map<String, Object> getTotalShipmentItemResult = null;
			try {
				getTotalShipmentItemResult = dispatcher.runSync("getTotalShipmentItem", getTotalShipmentItemCtx);
			} catch (GenericServiceException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "AssignShipmentToDEFail", locale));
			}
			
			GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", deliveryEntry.getString("weightUomId"), "uomIdTo", shipment.getString("defaultWeightUomId")));
			BigDecimal conversionFactor = BigDecimal.ONE;
			if (UtilValidate.isNotEmpty(conversion)){
				conversionFactor = conversion.getBigDecimal("conversionFactor");
				if (conversionFactor.compareTo(BigDecimal.ONE) >= 1){
					conversionFactor = (BigDecimal.ONE).divide(conversionFactor, 4, RoundingMode.HALF_UP);
				}
			}
			
			weight = weight.add(conversionFactor.multiply((BigDecimal)getTotalShipmentItemResult.get("totalWeight")));
			deliveryEntry.put("weight", weight);
			try {
				deliveryEntry.store();
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "AssignShipmentToDEFail", locale));
			}
			Timestamp thruDateShipment = shipment.getTimestamp("estimatedArrivalDate");
			deliveryEntry.put("thruDate", DateUtil.getBiggerDateTime(deliveryEntry.getTimestamp("thruDate"), thruDateShipment));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "UpdateSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getShipments(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
	   	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	   	EntityListIterator listIterator = null;
	   	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	   	List<String> listSortFields = (List<String>) context.get("listSortFields");
	   	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	   	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
	   	
	   	if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
	   		String facilityId = parameters.get("facilityId")[0];
		   	Map<String, String> mapCondition = new HashMap<String, String>();
		   	if (!facilityId.equals("")) {
		   		mapCondition.put("originFacilityId", facilityId);
		   		mapCondition.put("destinationFacilityId", facilityId);
		   		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition, EntityOperator.OR);
		       	listAllConditions.add(tmpConditon);
			}
	   	}
	   	
	   	if (parameters.get("shipmentTypeId") != null && parameters.get("shipmentTypeId").length > 0){
	   		String shipmentTypeId = parameters.get("shipmentTypeId")[0];
		   	if (!shipmentTypeId.equals("")) {
		   		EntityCondition tmpConditon = EntityCondition.makeCondition("shipmentTypeId", shipmentTypeId);
		       	listAllConditions.add(tmpConditon);
			}
	   	}
	   	
	   	GenericValue userLogin = (GenericValue)context.get("userLogin");
	   	String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	   	EntityCondition orgCond1 = EntityCondition.makeCondition("fromOwnerPartyId", EntityOperator.EQUALS, company);
	   	EntityCondition orgCond2 = EntityCondition.makeCondition("destOwnerPartyId", EntityOperator.EQUALS, company);
	   	List<EntityCondition> orgCondLists = UtilMisc.toList(orgCond1, orgCond2);
	   	EntityCondition orgCond = EntityCondition.makeCondition(orgCondLists, EntityOperator.OR);
	   	listAllConditions.add(orgCond);
	   	try {
	   		listIterator = delegator.find("ShipmentAndContactMechDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
	   	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getShipments service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
	   	successResult.put("listIterator", listIterator);
	   	return successResult;
	}
	
	public static Map<String,Object> getShipmentById(DispatchContext ctx, Map<String, Object> context){
		
		String shipmentId = (String)context.get("shipmentId");
		Delegator delegator = ctx.getDelegator();
		GenericValue shipment = null;
		try {
			shipment = delegator.findOne("ShipmentAndContactMechDetail", UtilMisc.toMap("shipmentId", shipmentId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		Map<String, Object> results = ServiceUtil.returnSuccess();
		results.put("shipment", shipment);
		
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListShipmentItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String shipmentId = (String)parameters.get("shipmentId")[0];
    	if (shipmentId != null && !"".equals(shipmentId)){
    		mapCondition.put("shipmentId", shipmentId);
    	}
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	List<GenericValue> listItems = new ArrayList<GenericValue>();
    	try {
    		GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
    		String entity = null;
    		if ("TRANSFER".equals(shipment.getString("shipmentTypeId"))){
    			entity = "ShipmentTransferItemIssuanceDetail";
    		} else {
    			entity = "ShipmentItemIssuanceDetail";
    		}
    		listItems = delegator.findList(entity, EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListShipmentItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listItems);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getShipmentPackage(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listPackages = new ArrayList<GenericValue>();
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String shipmentId = (String)parameters.get("shipmentId")[0];
    	if (shipmentId != null && !"".equals(shipmentId)){
    		mapCondition.put("shipmentId", shipmentId);
    	}
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		listPackages = delegator.findList("ShipmentPackage", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listPackages)){
    			for(GenericValue packageItem : listPackages){
    				Map<String, Object> row = new HashMap<String, Object>();
    				String shipmentIdTmp = (String)packageItem.get("shipmentId");
    				String shipmentPackageSeqId = (String)packageItem.get("shipmentPackageSeqId");
    				String shipmentBoxTypeId = (String)packageItem.get("shipmentBoxTypeId");
    				Timestamp dateCreated = packageItem.getTimestamp("dateCreated");
    				BigDecimal weight = packageItem.getBigDecimal("weight");
    				String weightUomId = (String)packageItem.get("weightUomId");
    				row.put("shipmentId", shipmentIdTmp);
    				row.put("shipmentPackageSeqId", shipmentPackageSeqId);
    				row.put("shipmentBoxTypeId", shipmentBoxTypeId);
    				row.put("dateCreated", dateCreated);
    				row.put("weight", weight);
    				row.put("weightUomId", weightUomId);
    				
    				List<GenericValue> listPackageContent = delegator.findList("ShipmentPackageContentDetail", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentPackageSeqId", shipmentPackageSeqId)), null, null, null, false);
    				List<Map<String, Object>> rowDetail = new ArrayList<Map<String,Object>>();
    				if(!UtilValidate.isEmpty(listPackageContent)){
    					for(GenericValue productItem : listPackageContent){
    						Map<String, Object> childDetail = new HashMap<String, Object>(); 
    						childDetail.put("shipmentId", shipmentIdTmp);
    						childDetail.put("shipmentPackageSeqId", shipmentPackageSeqId);
    						childDetail.put("shipmentItemSeqId", productItem.getString("shipmentItemSeqId"));
    						childDetail.put("productId", productItem.getString("productId"));
    						childDetail.put("productName", productItem.getString("productName"));
    						childDetail.put("quantity", productItem.get("quantity"));
    						childDetail.put("weight", productItem.get("weight"));
    						childDetail.put("weightUomId", (String)productItem.get("weightUomId"));
    						childDetail.put("quantityUomId", (String)productItem.get("quantityUomId"));
    						rowDetail.add(childDetail);
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
    		
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getShipmentPackage service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getShipmentInDE(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	String deliveryEntryId = parameters.get("deliveryEntryId")[0];
    	mapCondition.put("deliveryEntryId", deliveryEntryId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("ShipmentAndContactMechDetail", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getShipmentInDE service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	List<GenericValue> listTmp = listIterator.getCompleteList();
    	listIterator.close();
    	List<Map<String, Object>> listShipments = new ArrayList<Map<String, Object>>();
    	if (!listTmp.isEmpty()){
    		for (GenericValue shipment : listTmp){
    			Map<String, Object> mapReturn = FastMap.newInstance();
    			String shipmentId = shipment.getString("shipmentId");
    			mapReturn.put("shipmentId", shipment.get("shipmentId"));
    			mapReturn.put("deliveryId", shipment.get("deliveryId"));
    			mapReturn.put("shipmentTypeId", shipment.get("shipmentTypeId"));
    			mapReturn.put("statusId", shipment.get("statusId"));
    			mapReturn.put("primaryOrderId", shipment.get("primaryOrderId"));
    			mapReturn.put("primaryTransferId", shipment.get("primaryTransferId"));
    			mapReturn.put("estimatedReadyDate", shipment.get("estimatedReadyDate"));
    			mapReturn.put("estimatedShipDate", shipment.get("estimatedShipDate"));
    			mapReturn.put("estimatedArrivalDate", shipment.get("estimatedArrivalDate"));
    			mapReturn.put("estimatedShipCost", shipment.get("estimatedShipCost"));
    			mapReturn.put("currencyUomId", shipment.get("currencyUomId"));
    			mapReturn.put("originFacilityId", shipment.get("originFacilityId"));
    			mapReturn.put("originContactMechId", shipment.get("originContactMechId"));
    			mapReturn.put("destinationFacilityId", shipment.get("destinationFacilityId"));
    			mapReturn.put("destinationContactMechId", shipment.get("destinationContactMechId"));
    			mapReturn.put("destFacilityName", shipment.get("destFacilityName"));
    			mapReturn.put("originFacilityName", shipment.get("originFacilityName"));
    			mapReturn.put("destAddress", shipment.get("destAddress"));
    			mapReturn.put("originAddress", shipment.get("originAddress"));
    			mapReturn.put("defaultWeightUomId", shipment.get("defaultWeightUomId"));
    			BigDecimal totalWeight = BigDecimal.ZERO;
    			
    			Map<String, Object> mapTmp = FastMap.newInstance();
    			mapTmp.put("shipmentId", shipmentId);
    			mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
    			
    			LocalDispatcher dispatcher = ctx.getDispatcher();
    			try {
					Map<String, Object> result = dispatcher.runSync("getTotalShipmentItem", mapTmp);
					totalWeight = (BigDecimal)result.get("totalWeight");
				} catch (GenericServiceException e) {
					ServiceUtil.returnError("getTotalShipmentItem error");
				}
    			mapReturn.put("totalWeight", totalWeight);
    			
    			List<GenericValue> listShipmentInDE = delegator.findList("DeliveryEntryShipment", EntityCondition.makeCondition("deliveryEntryId", deliveryEntryId), null, null, null, false);
    			if (!listShipmentInDE.isEmpty()){
    				List<Map<String, Object>> rowDetail = new ArrayList<Map<String,Object>>();
    				for (GenericValue itemInDe : listShipmentInDE){
    					GenericValue item = delegator.findOne("ShipmentItemDetail", false, UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", itemInDe.getString("shipmentItemSeqId")));
            			if (UtilValidate.isNotEmpty(item)){
							Map<String, Object> childDetail = new HashMap<String, Object>(); 
							childDetail.put("shipmentId", shipmentId);
							childDetail.put("shipmentItemSeqId", itemInDe.getString("shipmentItemSeqId"));
							childDetail.put("productId", item.getString("productId"));
							childDetail.put("productCode", item.getString("productCode"));
							childDetail.put("productName", item.getString("productName"));
							childDetail.put("quantity", itemInDe.getBigDecimal("quantity"));
							childDetail.put("weight", item.getBigDecimal("weight"));
							childDetail.put("weightUomId", item.getString("weightUomId"));
							childDetail.put("quantityUomId", item.getString("quantityUomId"));
							rowDetail.add(childDetail);
            			}
    				}
    				mapReturn.put("rowDetail", rowDetail);
    			}
    			listShipments.add(mapReturn);
    		}
    	}
    			
    	successResult.put("listIterator", listShipments);
    	return successResult;
    }
	
	public static Map<String, Object> getShipmentInDeliveryEntry(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String deliveryEntryId = (String)context.get("deliveryEntryId");
    	List<GenericValue> listTmp = delegator.findList("ShipmentAndContactMechDetail", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId)), null, null, null, false);
    	List<Map<String, Object>> listShipments = new ArrayList<Map<String, Object>>();
    	if (!listTmp.isEmpty()){
    		for (GenericValue shipment : listTmp){
    			Map<String, Object> mapReturn = FastMap.newInstance();
    			String shipmentId = shipment.getString("shipmentId");
    			mapReturn.put("shipmentId", shipment.get("shipmentId"));
    			mapReturn.put("shipmentTypeId", shipment.get("shipmentTypeId"));
    			mapReturn.put("statusId", shipment.get("statusId"));
    			mapReturn.put("primaryOrderId", shipment.get("primaryOrderId"));
    			mapReturn.put("estimatedReadyDate", shipment.get("estimatedReadyDate"));
    			mapReturn.put("estimatedShipDate", shipment.get("estimatedShipDate"));
    			mapReturn.put("estimatedArrivalDate", shipment.get("estimatedArrivalDate"));
    			mapReturn.put("estimatedShipCost", shipment.get("estimatedShipCost"));
    			mapReturn.put("currencyUomId", shipment.get("currencyUomId"));
    			mapReturn.put("originFacilityId", shipment.get("originFacilityId"));
    			mapReturn.put("originContactMechId", shipment.get("originContactMechId"));
    			mapReturn.put("destinationFacilityId", shipment.get("destinationFacilityId"));
    			mapReturn.put("destinationContactMechId", shipment.get("destinationContactMechId"));
    			mapReturn.put("destFacilityName", shipment.get("destFacilityName"));
    			mapReturn.put("originFacilityName", shipment.get("originFacilityName"));
    			mapReturn.put("destAddress", shipment.get("destAddress"));
    			mapReturn.put("originAddress", shipment.get("originAddress"));
    			mapReturn.put("defaultWeightUomId", shipment.get("defaultWeightUomId"));
    			BigDecimal totalWeight = BigDecimal.ZERO;
    			
    			Map<String, Object> mapTmp = FastMap.newInstance();
    			mapTmp.put("shipmentId", shipmentId);
    			mapTmp.put("userLogin", (GenericValue)context.get("userLogin"));
    			
    			LocalDispatcher dispatcher = ctx.getDispatcher();
    			try {
					Map<String, Object> result = dispatcher.runSync("getTotalShipmentItem", mapTmp);
					totalWeight = (BigDecimal)result.get("totalWeight");
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("getTotalShipmentItem error");
				}
    			mapReturn.put("totalWeight", totalWeight);
    			listShipments.add(mapReturn);
    		}
    	}
    			
    	successResult.put("listShipments", listShipments);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getShipmentByDeliveryEntry(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String deliveryEntryId = (String)parameters.get("deliveryEntryId")[0];
    	if (deliveryEntryId != null && !"".equals(deliveryEntryId)){
    		mapCondition.put("deliveryEntryId", deliveryEntryId);
    	}
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	List<GenericValue> listShipments = new ArrayList<GenericValue>();
    	try {
    		listShipments = delegator.findList("ShipmentAndContactMechDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listShipments)){
    			for(GenericValue shipment : listShipments){
    				Map<String, Object> row = new HashMap<String, Object>();
    				String deliveryEntryIdTmp = (String)shipment.get("deliveryEntryId");
    				String shipmentId = (String)shipment.get("shipmentId");
    				String defaultWeightUomId = (String)shipment.get("defaultWeightUomId");
    				row.put("deliveryEntryId", deliveryEntryIdTmp);
    				row.put("shipmentId", shipmentId);
    				row.put("shipmentTypeId", (String)shipment.get("shipmentTypeId"));
    				row.put("originFacilityId", (String)shipment.get("originFacilityId"));
    				row.put("destinationFacilityId", (String)shipment.get("destinationFacilityId"));
    				row.put("originContactMechId", (String)shipment.get("originContactMechId"));
    				row.put("destinationContactMechId", (String)shipment.get("destinationContactMechId"));
    				row.put("defaultWeightUomId", defaultWeightUomId);
    				row.put("originFacilityName", (String)shipment.get("originFacilityName"));
    				row.put("destFacilityName", (String)shipment.get("destFacilityName"));
    				row.put("originAddress", (String)shipment.get("originAddress"));
    				row.put("destAddress", (String)shipment.get("destAddress"));
    				row.put("currencyUomId", (String)shipment.get("currencyUomId"));
    				
    				List<GenericValue> listShipmentItems = delegator.findList("ShipmentItemDetail", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
    				List<Map<String, Object>> rowDetail = new ArrayList<Map<String,Object>>();
    				if(!UtilValidate.isEmpty(listShipmentItems)){
    					for(GenericValue item : listShipmentItems){
    						Map<String, Object> childDetail = new HashMap<String, Object>(); 
    						childDetail.put("deliveryEntryId", deliveryEntryIdTmp);
    						childDetail.put("shipmentId", item.getString("shipmentId"));
    						childDetail.put("shipmentItemSeqId", item.getString("shipmentItemSeqId"));
    						childDetail.put("productId", item.getString("productId"));
    						childDetail.put("productName", item.getString("productName"));
    						childDetail.put("quantity", item.get("quantity"));
    						childDetail.put("weight", item.get("weight"));
    						childDetail.put("weightUomId", (String)item.get("weightUomId"));
    						childDetail.put("quantityUomId", (String)item.get("quantityUomId"));
    						
    						List<GenericValue> listPackagedItems = delegator.findList("DeliveryEntryPackageContent", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "shipmentId", shipmentId, "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
    						BigDecimal quantityPackaged = BigDecimal.ZERO;
    						if (!listPackagedItems.isEmpty()){
    							for (GenericValue itemPackaged : listPackagedItems){
    								quantityPackaged = quantityPackaged.add(itemPackaged.getBigDecimal("quantity"));
    							}
    						}
    						childDetail.put("quantityPackaged", quantityPackaged);
    						rowDetail.add(childDetail);
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
    		
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getShipmentByDeliveryEntry service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getShipmentCostEstimated(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	String payToPartyId = parameters.get("payToPartyId")[0];
    	mapCondition.put("payToPartyId", payToPartyId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	List<GenericValue> listShipCostEstimated = new ArrayList<GenericValue>();
    	
    	listShipCostEstimated = delegator.findList("ShipmentCostEstimateDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, opts, false);
    	
    	successResult.put("listIterator", listShipCostEstimated);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String,Object> createTransferShipment(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	List<Object> listItemTmp = (List<Object>)context.get("listProducts");
    	Boolean isJson = false;
    	String originFacilityId = (String)context.get("originFacilityId");
    	String destinationFacilityId = (String)context.get("destinationFacilityId");
    	String originContactMechId = (String)context.get("originContactMechId");
    	String destinationContactMechId = (String)context.get("destinationContactMechId");
    	String shipmentTypeId = "TRANSFER";
    	BigDecimal estimatedShipCost = (BigDecimal)context.get("estimatedShipCost");
    	Long estimatedShipDate = (Long)context.get("estimatedShipDate");
    	Long estimatedArrivalDate = (Long)context.get("estimatedArrivalDate");
    	String currencyUomId = (String)context.get("currencyUomId");
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, String>> listProducts = new ArrayList<Map<String,String>>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				Map<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("listInventoryItemIds")){
					mapItems.put("listInventoryItemIds", item.getString("listInventoryItemIds"));
				}
				if (item.containsKey("quantity")){
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("productId")){
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("uomId")){
					mapItems.put("uomId", item.getString("uomId"));
				}
				listProducts.add(mapItems);
			}
    	} else {
    		listProducts = (List<Map<String, String>>)context.get("listProducts");
    	}
    	List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String,Object>>();
    	if (!listProducts.isEmpty()){
    		for (Map<String, String> item : listProducts){
    			BigDecimal quantityTmp = new BigDecimal(item.get("quantity"));
    			String uomId = item.get("uomId");
    			String productId = item.get("productId");
    			BigDecimal convert = LogisticsProductUtil.getConvertPackingToBaseUom(delegator, productId, uomId);
    			BigDecimal quantity = quantityTmp.multiply(convert);
    			String listInvStr = item.get("listInventoryItemIds");
    			listInvStr = listInvStr.replace("[", "");
    			listInvStr = listInvStr.replace("]", "");
    			String[] arr = listInvStr.split(",");
    			List<String> listInventory = new ArrayList<String>();
    			for (int i = 0; i < arr.length; i ++){
    				listInventory.add(arr[i].replaceAll("\"", ""));
    			}
    			String invEnough = null;
    			for (String invId : listInventory){
    				GenericValue inventory = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", invId));
    				if (UtilValidate.isNotEmpty(inventory)){
    					if (inventory.getBigDecimal("quantityOnHandTotal").compareTo(quantity) == 1){
    						invEnough = invId; 
    						break;
    					}
    				}
    			}
    			if (UtilValidate.isNotEmpty(invEnough)){
    				Map<String, Object> mapItem = FastMap.newInstance();
    				mapItem.put("inventoryItemId", invEnough);
    				mapItem.put("quantity", quantity);
    				listInventoryItems.add(mapItem);
    			} else {
    				BigDecimal quantityCreated = BigDecimal.ZERO;
    				for (String invId : listInventory){
    					if (quantityCreated.compareTo(quantity) > 0){
    						break;
    					} else {
    						GenericValue inventory = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", invId));
            				if (UtilValidate.isNotEmpty(inventory)){
            					if (quantityCreated.add(inventory.getBigDecimal("quantityOnHandTotal")).compareTo(quantity) < 0){
            						Map<String, Object> mapItem = FastMap.newInstance();
            	    				mapItem.put("inventoryItemId", invId);
            	    				mapItem.put("quantity", inventory.getBigDecimal("quantityOnHandTotal"));
            	    				listInventoryItems.add(mapItem);
            	    				quantityCreated = quantityCreated.add(inventory.getBigDecimal("quantityOnHandTotal"));
            					} else {
            						BigDecimal itemQty = quantity.subtract(quantityCreated);
            						Map<String, Object> mapItem = FastMap.newInstance();
            	    				mapItem.put("inventoryItemId", invId);
            	    				mapItem.put("quantity", itemQty);
            	    				listInventoryItems.add(mapItem);
            	    				quantityCreated = quantityCreated.add(itemQty);
            					}
            				}
    					}
    				}
    			}
    		}
    	}
    	// create shipment
    	Map<String, Object> mapCreateShipment = FastMap.newInstance();
    	mapCreateShipment.put("originFacilityId", originFacilityId);
    	mapCreateShipment.put("originContactMechId", originContactMechId);
    	mapCreateShipment.put("destinationFacilityId", destinationFacilityId);
    	mapCreateShipment.put("destinationContactMechId", destinationContactMechId);
    	mapCreateShipment.put("shipmentTypeId", shipmentTypeId);
    	mapCreateShipment.put("statusId", "SHIPMENT_INPUT");
    	mapCreateShipment.put("estimatedShipDate", new Timestamp(estimatedShipDate));
    	mapCreateShipment.put("estimatedArrivalDate", new Timestamp(estimatedArrivalDate));
    	mapCreateShipment.put("estimatedShipCost", estimatedShipCost);
    	mapCreateShipment.put("currencyUomId", currencyUomId);
    	mapCreateShipment.put("defaultWeightUomId", "WT_kg");
    	mapCreateShipment.put("userLogin", userLogin);
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> mapShipment = FastMap.newInstance();
    	String shipmentId = null;
    	try {
    		mapShipment = dispatcher.runSync("createShipment", mapCreateShipment);
    		if (UtilValidate.isNotEmpty(mapShipment.get("shipmentId"))){
    			shipmentId = (String)mapShipment.get("shipmentId");
    		}
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS - Create shipment transfer error");
		}
    	
    	if (UtilValidate.isNotEmpty(shipmentId)){
    		List<String> listInvTransfers = new ArrayList<String>();
    		if (!listInventoryItems.isEmpty()){
    			for (Map<String, Object> inv : listInventoryItems){
    				// create inventory transfer 
    				Map<String, Object> mapInvTranfer = FastMap.newInstance();
    				mapInvTranfer.put("inventoryItemId", inv.get("inventoryItemId"));
    				mapInvTranfer.put("statusId", "IXF_REQUESTED");
    				mapInvTranfer.put("facilityId", originFacilityId);
    				mapInvTranfer.put("facilityIdTo", destinationFacilityId);
    				mapInvTranfer.put("xferQty", (BigDecimal)inv.get("quantity"));
    				mapInvTranfer.put("userLogin", userLogin);
    				String inventoryTransferId = null;
    				try {
    					Map<String, Object> invTranfer = dispatcher.runSync("createInventoryTransfer", mapInvTranfer);
    					if (UtilValidate.isNotEmpty(invTranfer.get("inventoryTransferId"))){
    						inventoryTransferId = (String)invTranfer.get("inventoryTransferId");
    					}
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS - Create inventory item transfer error");
					}
    				if (UtilValidate.isNotEmpty(inventoryTransferId)){
    					listInvTransfers.add(inventoryTransferId);
    				}
    				
    				//create shipment item
    				Map<String, Object> mapShipmentItem = FastMap.newInstance();
    				mapShipmentItem.put("shipmentId", shipmentId);
    				GenericValue inventory = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inv.get("inventoryItemId"))); 
    				mapShipmentItem.put("productId", inventory.getString("productId"));
    				mapShipmentItem.put("quantity", (BigDecimal)inv.get("quantity"));
    				mapShipmentItem.put("userLogin", userLogin);
    				String shipmentItemSeqId = null;
    				try {
    					Map<String, Object> mapShipmentItemResult = dispatcher.runSync("createShipmentItem", mapShipmentItem);
    					shipmentItemSeqId = (String)mapShipmentItemResult.get("shipmentItemSeqId");
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS - Create shipment item transfer error");
					}
    				
    				// Create item issuance
        			Map<String, Object> mapIssuance = FastMap.newInstance();
    				mapIssuance.put("shipmentItemSeqId", shipmentItemSeqId);
    				mapIssuance.put("shipmentId", shipmentId);
    				mapIssuance.put("inventoryItemId", inv.get("inventoryItemId"));
    				mapIssuance.put("quantity", (BigDecimal)inv.get("quantity"));
    				mapIssuance.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
    				mapIssuance.put("userLogin", userLogin);
    				try {
    					Map<String, Object> mapIssuanceResult = dispatcher.runSync("createItemIssuance", mapIssuance);
    					String itemIssuanceId = (String)mapIssuanceResult.get("itemIssuanceId");
    					// store issuance to inventory transfer
    					GenericValue inventoryTransfer = delegator.findOne("InventoryTransfer", false, UtilMisc.toMap("inventoryTransferId", inventoryTransferId));
    					inventoryTransfer.set("itemIssuanceId", itemIssuanceId);
    					inventoryTransfer.store();
    				} catch (GenericServiceException e) {
    					return ServiceUtil.returnError("OLBIUS - Create item issuance error");
    				}
    			}
    		}
    	}
    	result.put("shipmentId", shipmentId);
    	return result;
	}
	
    public static Map<String, Object> getShipmentDetail(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String shipmentId = (String)context.get("shipmentId");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue shipment = null;
    	try {
    		shipment = delegator.findOne("ShipmentAndContactMechDetail", false, UtilMisc.toMap("shipmentId", shipmentId));
		} catch (Exception e) {
			String errMsg = "Fatal error calling getShipmentDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("shipmentId", shipment.get("shipmentId"));
    	successResult.put("shipmentTypeId", shipment.get("shipmentTypeId"));
    	successResult.put("statusId", shipment.get("statusId"));
    	successResult.put("primaryOrderId", shipment.get("primaryOrderId"));
		successResult.put("estimatedReadyDate", shipment.get("estimatedReadyDate"));
		successResult.put("estimatedShipDate", shipment.get("estimatedShipDate"));
		successResult.put("estimatedArrivalDate", shipment.get("estimatedArrivalDate"));
		successResult.put("estimatedShipCost", shipment.get("estimatedShipCost"));
		successResult.put("currencyUomId", shipment.get("currencyUomId"));
		successResult.put("originFacilityId", shipment.get("originFacilityId"));
		successResult.put("originContactMechId", shipment.get("originContactMechId"));
		successResult.put("destinationFacilityId", shipment.get("destinationFacilityId"));
		successResult.put("destinationContactMechId", shipment.get("destinationContactMechId"));
		successResult.put("destFacilityName", shipment.get("destFacilityName"));
		successResult.put("originFacilityName", shipment.get("originFacilityName"));
		successResult.put("destAddress", shipment.get("destAddress"));
		successResult.put("originAddress", shipment.get("originAddress"));
		successResult.put("defaultWeightUomId", shipment.get("defaultWeightUomId"));
    	return successResult;
    }
    
    public static Map<String, Object> updateDatetimeShipmentTransfer(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String shipmentId = (String)context.get("shipmentId");
    	Long actualShipDateTmp = (Long)context.get("actualShipDate");
    	Long actualArrivalDateTmp = (Long)context.get("actualArrivalDate");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue shipment = null;
    	try {
    		shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
    		if (UtilValidate.isNotEmpty(shipment)){
    			shipment.put("actualShipDate", new Timestamp(actualShipDateTmp));
    			shipment.put("actualArrivalDate", new Timestamp(actualArrivalDateTmp));
    			shipment.store();
    		} else {
    			return ServiceUtil.returnError("OLBIUS: updateDatetimeShipmentTransfer Error");
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getShipmentDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("shipmentId", shipmentId);
    	return successResult;
    }
    
    public static Map<String, Object> createItemIssuanceFromShipment(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String shipmentId = (String)context.get("shipmentId");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	List<GenericValue> listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
    	List<GenericValue> listItemIssances = delegator.findList("ItemIssuance", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
    	if (!listShipmentItems.isEmpty() && listItemIssances.isEmpty()){
    		for (GenericValue item : listShipmentItems){
    			List<GenericValue> returnItemRelated = delegator.findList("ReturnItemShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
    			if (!returnItemRelated.isEmpty()){
    				for (GenericValue itemShipment : returnItemRelated){
    					String returnId = itemShipment.getString("returnId");
    					String returnItemSeqId = itemShipment.getString("returnItemSeqId");
    					GenericValue returnItem = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
    					if (UtilValidate.isNotEmpty(returnItem)){
    						String orderId = returnItem.getString("orderId");
    						String orderItemSeqId = returnItem.getString("orderItemSeqId");
    						List<GenericValue> listInvDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId)), null, null, null, false);
    						if (!listInvDetail.isEmpty()){
    							for (GenericValue inv : listInvDetail){
    								String inventoryItemId = inv.getString("inventoryItemId");
    								BigDecimal quantityDetail = inv.getBigDecimal("quantityOnHandDiff");
    								if (quantityDetail.compareTo(BigDecimal.ZERO) > 0){
    									GenericValue inventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
    									BigDecimal availableToPromiseTotal = inventoryItem.getBigDecimal("availableToPromiseTotal");
    									if (availableToPromiseTotal.compareTo(itemShipment.getBigDecimal("quantity")) >= 0){
    										Map<String, Object> mapIssuance = FastMap.newInstance();
    						    			mapIssuance.put("shipmentItemSeqId", item.getString("shipmentItemSeqId"));
    						    			mapIssuance.put("shipmentId", shipmentId);
    										mapIssuance.put("inventoryItemId", inventoryItemId);
    						    			mapIssuance.put("quantity", itemShipment.getBigDecimal("quantity"));
    						    			mapIssuance.put("weight", itemShipment.getBigDecimal("amount"));
    						    			mapIssuance.put("issuedByUserLoginId", userLogin.getString("userLoginId"));
    						    			mapIssuance.put("userLogin", userLogin);
    						    			try {
    						    				dispatcher.runSync("createItemIssuance", mapIssuance);
    						    			} catch (GenericServiceException e) {
    						    				return ServiceUtil.returnError("OLBIUS - Create item issuance error");
    						    			}
    						    			break;
    									} else {
    										return ServiceUtil.returnError("OLBIUS: Can not return item because Inventory item has been reservers for some thing else");
    									}
    								} else {
    									return ServiceUtil.returnError("OLBIUS: Order item not received");
    								}
    							}
    						} else {
    							return ServiceUtil.returnError("Can not trace inventory item has recieved from order " + orderId +" shipment " + shipmentId);
    						}
    					} else {
    						return ServiceUtil.returnError("OLBIUS: Return item not found for return " + returnId);
    					}
    				}
    			} else { 
    				return ServiceUtil.returnError("OLBIUS: Return item not found for shipment item");
    			}
    		}
    	} else {
    		return ServiceUtil.returnError("OLBIUS: Shipment item not found for shipment " + shipmentId);
    	}
    	successResult.put("shipmentId", shipmentId);
		return successResult;
    }
    
    public static Map<String, Object> checkShipmentExisted(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String shipmentId = (String)context.get("shipmentId"); 
		GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		if(UtilValidate.isNotEmpty(shipment)){
			result.put("hasExisted", true);
		} else{
			result.put("hasExisted", false);
		}
		return result;
	}
    
    public static Map<String, Object> getPartyCarrierByShipmentMethodAndStore(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productStoreId = (String)context.get("productStoreId");
		String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
		List<GenericValue> listProductStoreShipmentMeths = new ArrayList<GenericValue>();
		if (UtilValidate.isNotEmpty(productStoreId)){
			listProductStoreShipmentMeths = delegator.findList("ProductStoreShipmentMeth", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId",productStoreId, "shipmentMethodTypeId", shipmentMethodTypeId)), null, null, null, false);
		} else {
			GenericValue userLogin = (GenericValue)context.get("userLogin");
		   	String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		   	List<GenericValue> listProductStores = delegator.findList("ProductStore", EntityCondition.makeCondition(UtilMisc.toMap("payToPartyId", company)), null, null, null, false);
		   	if (!listProductStores.isEmpty()){
		   		for (GenericValue item : listProductStores) {
		   			List<GenericValue> listProductStoreTmp = delegator.findList("ProductStoreShipmentMeth", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", item.getString("productStoreId"), "shipmentMethodTypeId", shipmentMethodTypeId)), null, null, null, false);
		   			if (!listProductStoreTmp.isEmpty()){
		   				listProductStoreShipmentMeths.addAll(listProductStoreTmp);
		   			}
				}
		   	}
		}
		List<GenericValue> listParties = new ArrayList<GenericValue>();
		if (!listProductStoreShipmentMeths.isEmpty()){
			for (GenericValue item : listProductStoreShipmentMeths) {
				Boolean check = false;
				for (GenericValue party : listParties) {
					if (party.getString("partyId").equals(item.getString("partyId"))){
						check = true;
						break;
					}
				}
				if (!check){
					GenericValue party = delegator.findOne("PartyFullNameDetail", false, UtilMisc.toMap("partyId", item.getString("partyId")));
					if (party.getString("partyId").equals("_NA_")){
						party.put("fullName", "_NA_");
					}
					listParties.add(party);
				}
			}
		}
		result.put("listParties", listParties);
		return result;
	}
    
    public static Map<String, Object> createShipmentToReceiveForTransferDelivery(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		//Get parameters
		String deliveryId = (String)context.get("deliveryId"); 
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = null;
		String shipmentId = null;
		Map<String, Object> result = FastMap.newInstance();
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e2) {
			Debug.log(e2.getStackTrace().toString(), module);
			return ServiceUtil.returnError("createShipmentToReceiveForTransferDelivery error!");
		}
		
		//Create Shipment
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
			return ServiceUtil.returnError("findOne Delivery error!");
		}
		
		String deliveryTypeId = (String)delivery.get("deliveryTypeId");
		if (deliveryTypeId == null){
			return ServiceUtil.returnError("deliveryTypeId not found!");
		} else {
			List<GenericValue> shipGroups = null;
			Map<String, Object> shipmetCtx = FastMap.newInstance();
			
			try {
				shipGroups = delegator.findList("TransferItemShipGroup", EntityCondition.makeCondition("transferId", delivery.get("transferId")), null, null, null, false);
			} catch (GenericEntityException e1) {
				Debug.log(e1.getStackTrace().toString(), module);
				return ServiceUtil.returnError("findList TransferItemShipGroup error!");
			}
			shipmetCtx.put("primaryTransferId", delivery.getString("transferId"));
			shipmetCtx.put("shipmentTypeId", "TRANSFER");
			shipmetCtx.put("statusId", "SHIPMENT_INPUT");
			
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
				Map<String, Object> result2 = localDis.runSync("createShipment", shipmetCtx);
				shipmentId = (String)result2.get("shipmentId");
			} catch (ServiceAuthException e) {
				Debug.logError(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError("createShipment error!");
			} catch (ServiceValidationException e) {
				Debug.logError(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError("createShipment error!");
			} catch (GenericServiceException e) {
				Debug.logError(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError("createShipment error!");
			}
		}
        String resultF = createShipmentItemAndTransferShipment(ctx, delivery, shipmentId);
		if(!UtilValidate.isEmpty(resultF)) return ServiceUtil.returnError(resultF);
		result.put("shipmentId", shipmentId);
		return result;
    }

    public static String createShipmentItemAndTransferShipment(DispatchContext ctx, GenericValue delivery, String shipmentId) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        String deliveryId = delivery.getString("deliveryId");
        GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
        //  create transfer_shipment
        List<EntityCondition> conds = FastList.newInstance();
        EntityCondition cond1 = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
        EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_DELIVERED");
        EntityCondition cond3 = EntityCondition.makeCondition("actualDeliveredQuantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
        conds.add(cond1);
        conds.add(cond2);
        conds.add(cond3);
        List<GenericValue> listDeliveryItems;
        listDeliveryItems = delegator.findList("DeliveryItemTransferGroupBound", EntityCondition.makeCondition(conds), null, null, null, false);
        String originFacilityId = null;
        GenericValue originFacility = null;
        if (delivery.getString("originFacilityId") != null) {
            originFacilityId = delivery.getString("originFacilityId");
            originFacility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", originFacilityId));
        }
        for (GenericValue item : listDeliveryItems) {
            BigDecimal actualDeliveredQuantity = item.getBigDecimal("actualDeliveredQuantity");
            if (actualDeliveredQuantity.compareTo(BigDecimal.ZERO) > 0){
                try {
                    Map<String, Object> mapShipmentItemResult = createShipmentItem(ctx, item, shipmentId, delivery, originFacility, userLogin);
                    String shipmentItemSeqId = (String)mapShipmentItemResult.get("shipmentItemSeqId");
                    String errorCreateTransfer = createTransferShipment(ctx, item, shipmentItemSeqId, shipmentId, userLogin);
                    if(!UtilValidate.isEmpty(errorCreateTransfer)) return errorCreateTransfer;
                } catch (GenericServiceException e) {
                    return "createShipment error!";
                }
            }
        }
        return StringUtils.EMPTY;
    }
    public static Map<String, Object> createShipmentItem(DispatchContext ctx, GenericValue deliveryItem, String shipmentId, GenericValue delivery,
                                                         GenericValue originFacility, GenericValue userLogin) throws GenericServiceException {
        //create shipment item
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        boolean weight = false;
        if (ProductUtil.isWeightProduct(delegator, deliveryItem.getString("productId"))) weight = true;
        BigDecimal actualDeliveredQuantity = deliveryItem.getBigDecimal("actualDeliveredQuantity");
        String statusId = delivery.getString("statusId");
        String deliveryId = delivery.getString("deliveryId");
        String originFacilityId = delivery.getString("originFacilityId");
        String productAverageCostTypeId = "SIMPLE_AVG_COST";
        String organizationId = null;
        if(originFacility != null) organizationId = originFacility.getString("ownerPartyId");

        Map<String, Object> mapShipmentItem = FastMap.newInstance();
        mapShipmentItem.put("shipmentId", shipmentId);
        mapShipmentItem.put("productId", deliveryItem.getString("productId"));
        BigDecimal unitCost;
        if ("DLV_EXPORTED".equals(statusId) || "DLV_DELIVERED".equals(statusId)) {
            unitCost = TransferReadHepler.getAverageCostProductExportedByDelivery(delegator, deliveryId, deliveryItem.getString("productId"), originFacilityId, productAverageCostTypeId, organizationId);
        } else {
            unitCost = ProductUtil.getAverageCostByTime(delegator, deliveryItem.getString("productId"), originFacilityId, productAverageCostTypeId, organizationId, null);
        }
        mapShipmentItem.put("unitCost", unitCost);
        if (!weight){
            mapShipmentItem.put("quantity", actualDeliveredQuantity);
        } else {
            mapShipmentItem.put("quantity", BigDecimal.ONE);
            mapShipmentItem.put("weight", deliveryItem.getBigDecimal("actualDeliveredAmount"));
        }
        mapShipmentItem.put("userLogin", userLogin);
        return dispatcher.runSync("createShipmentItem", mapShipmentItem);
    }

    public static String createTransferShipment(DispatchContext ctx, GenericValue deliveryItem, String shipmentItemSeqId, String shipmentId, GenericValue userLogin)  {
        //  create mapping
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String transferId = deliveryItem.getString("fromTransferId");
        String transferItemSeqId = deliveryItem.getString("fromTransferItemSeqId");
        BigDecimal actualDeliveredQuantity = deliveryItem.getBigDecimal("actualDeliveredQuantity");

        boolean weight = false;
        if (ProductUtil.isWeightProduct(delegator, deliveryItem.getString("productId"))) weight = true;
        Map<String, Object> map = FastMap.newInstance();
        map.put("transferId", transferId);
        map.put("transferItemSeqId", transferItemSeqId);
        map.put("shipmentId", shipmentId);
        map.put("shipmentItemSeqId", shipmentItemSeqId);
        map.put("shipGroupSeqId", "00001");
        if (!weight){
            map.put("quantity", actualDeliveredQuantity);
        } else {
            map.put("quantity", BigDecimal.ONE);
            map.put("amount", deliveryItem.getBigDecimal("actualDeliveredAmount"));
        }
        map.put("userLogin", userLogin);
        try {
            Map<String, Object> mapReturn = dispatcher.runSync("createTransferShipment", map);
            if (ServiceUtil.isError(mapReturn)) {
                return ServiceUtil.getErrorMessage(mapReturn);
            }
        } catch (GenericServiceException e) {
            String errMsg = "OLBIUS: Fatal error when runSync createTransferShipment: " + e.toString();
            Debug.logError(e, errMsg, module);
            return errMsg;
        }
        return StringUtils.EMPTY;
    }
    
    @SuppressWarnings({"unchecked" })
	public static Map<String, Object> createShipmentForPurchaseDeliveryDistributor(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		//Get parameters
		String deliveryId = (String)context.get("deliveryId"); 
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = null;
		String shipmentId = null;
		Map<String, Object> result = FastMap.newInstance();
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		} catch (GenericEntityException e2) {
			Debug.log(e2.getStackTrace().toString(), module);
			return ServiceUtil.returnError("createShipmentToReceiveForTransferDelivery error!");
		}
		
		//Create Shipment
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e1) {
			Debug.log(e1.getStackTrace().toString(), module);
			return ServiceUtil.returnError("findOne Delivery error!");
		}
		List<Map<String, Object>> listProducts = (List<Map<String, Object>>)context.get("listProducts");
		List<GenericValue> shipGroups = null;
		Map<String, Object> shipmentCtx = FastMap.newInstance();
		if (!listProducts.isEmpty()){
			try {
				shipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition("orderId", delivery.get("orderId")), null, null, null, false);
			} catch (GenericEntityException e1) {
				Debug.log(e1.getStackTrace().toString(), module);
				return ServiceUtil.returnError("findList OrderItemShipGroup error!");
			}
			shipmentCtx.put("primaryOrderId", delivery.getString("orderId"));
			shipmentCtx.put("shipmentTypeId", "PURCH_DIS_SHIPMENT");
			shipmentCtx.put("statusId", "PURCH_SHIP_CREATED");
			
			GenericValue shipGroup = shipGroups.get(0);
			String contactMechId = (String)context.get("contactMechId");
			String facilityId = (String)context.get("facilityId");
			shipmentCtx.put("destinationContactMechId", contactMechId);
			shipmentCtx.put("destinationFacilityId", facilityId);
			shipmentCtx.put("estimatedArrivalDate", delivery.getTimestamp("estimatedArrivalDate"));
			shipmentCtx.put("estimatedShipDate", delivery.getTimestamp("estimatedStartDate"));
			shipmentCtx.put("locale", locale);
			shipmentCtx.put("originContactMechId", delivery.getString("originContactMechId"));
			shipmentCtx.put("originFacilityId", delivery.getString("originFacilityId"));
			shipmentCtx.put("partyIdFrom", delivery.getString("partyIdFrom"));
			shipmentCtx.put("partyIdTo", delivery.getString("partyIdTo"));
			shipmentCtx.put("defaultWeightUomId", delivery.getString("defaultWeightUomId"));
			shipmentCtx.put("primaryShipGroupSeqId", shipGroup.get("shipGroupSeqId"));
			shipmentCtx.put("userLogin", userLogin);
			LocalDispatcher dispatcher = ctx.getDispatcher();
			try {
				Map<String, Object> mapReturn = dispatcher.runSync("createShipment", shipmentCtx);
				if (ServiceUtil.isError(mapReturn)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
				}
				if (mapReturn.containsKey("shipmentId")) {
					shipmentId = (String)mapReturn.get("shipmentId");
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when runSync createShipment: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			GenericValue objShipment = null;
			try {
				objShipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Shipment: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			objShipment.put("deliveryId", deliveryId);
			delegator.store(objShipment);
			
			for (Map<String, Object> item : listProducts){
				//create shipment item
				String productId = (String)item.get("productId");
				Map<String, Object> mapShipmentItem = FastMap.newInstance();
				mapShipmentItem.put("shipmentId", shipmentId);
				mapShipmentItem.put("productId", productId);
				if (ProductUtil.isWeightProduct(delegator, productId)){
					mapShipmentItem.put("quantity", BigDecimal.ONE);
					mapShipmentItem.put("weight", (BigDecimal)item.get("quantity"));
				} else {
					mapShipmentItem.put("quantity", (BigDecimal)item.get("quantity"));
				}
				mapShipmentItem.put("userLogin", userLogin);
				String shipmentItemSeqId = null;
				try {
					Map<String, Object> mapReturn = dispatcher.runSync("createShipmentItem", mapShipmentItem);
					if (ServiceUtil.isError(mapReturn)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
					}
					shipmentItemSeqId = (String)mapReturn.get("shipmentItemSeqId");
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when runSync createShipmentItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (UtilValidate.isNotEmpty(shipmentItemSeqId)) {
					BigDecimal quantity = (BigDecimal)item.get("quantity");
					if (quantity.compareTo(BigDecimal.ZERO) > 0){
						GenericValue orSmt = delegator.makeValue("OrderShipment");
						orSmt.set("orderId", item.get("orderId"));
						orSmt.set("orderItemSeqId", item.get("orderItemSeqId"));
						orSmt.set("shipmentId", shipmentId);
						orSmt.set("shipmentItemSeqId", shipmentItemSeqId);
						orSmt.set("shipGroupSeqId", shipGroup.get("shipGroupSeqId"));
						
						if (ProductUtil.isWeightProduct(delegator, productId)){
							orSmt.set("quantity", BigDecimal.ONE);
							orSmt.set("weight", quantity);
						} else {
							orSmt.set("quantity", quantity);
						}
						delegator.createOrStore(orSmt);
					}
				}
			}
		}
		result.put("shipmentId", shipmentId);
		return result;
    }
}