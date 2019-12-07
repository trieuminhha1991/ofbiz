package com.olbius.logistics.delivery;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.DateUtil;

public class JQDeliveryEntryServices {
	
	public static final String module = JQDeliveryEntryServices.class.getName();
	public static final String logisticsUiLabel = "DelysLogisticsUiLabels";
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqListDeliveryEntry(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("DeliveryEntry", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListDeliveryEntry service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListShipmentInDE(DispatchContext ctx, Map<String, Object> context) {
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
			String errMsg = "Fatal error calling jqListDeliveryEntry service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListFilterShipment(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = new ArrayList<GenericValue>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	Map<String, Object> mapCondition = new HashMap<String, Object>();
    	String deliveryEntryId = parameters.get("deliveryEntryId")[0];
    	String originFacilityId = parameters.get("facilityId")[0];
    	String estimatedShipDateTmp = parameters.get("fromDate")[0];
    	if (deliveryEntryId != null && !"".equals(deliveryEntryId)){
    		GenericValue deliveryEntry = null;
        	try {
    			deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
    		} catch (GenericEntityException e1) {
    			String errMsg = "Fatal error calling jqListDeliveryEntry service: " + e1.toString();
    			Debug.logError(e1, errMsg, module);
    		}
        	mapCondition.put("estimatedShipDate", deliveryEntry.getTimestamp("fromDate"));
        	mapCondition.put("originFacilityId", deliveryEntry.getString("facilityId"));
        	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	EntityCondition deliveryEntryCon = EntityCondition.makeCondition("deliveryEntryId", EntityComparisonOperator.EQUALS , null);
        	EntityCondition statusCondition = EntityCondition.makeCondition("statusId", EntityComparisonOperator.IN, UtilMisc.toSet("SHIPMENT_PICKED", "SHIPMENT_PACKED"));
        	listAllConditions.add(tmpConditon);
        	listAllConditions.add(deliveryEntryCon);
        	listAllConditions.add(statusCondition);
        	try {
    			listIterator = delegator.findList("ShipmentAndContactMechDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, opts, false);
    		} catch (GenericEntityException e) {
    			e.printStackTrace();
    		}
    	} else if ((estimatedShipDateTmp != null && !"".equals(estimatedShipDateTmp)) || (originFacilityId != null && !"".equals(originFacilityId))){
    		Timestamp estimatedShipDate = null;
    		EntityCondition tmpConditon = EntityCondition.makeCondition();
    		if (estimatedShipDateTmp != null && !"".equals(estimatedShipDateTmp)){
    			estimatedShipDate = new Timestamp(Long.valueOf(estimatedShipDateTmp));
    			mapCondition.put("estimatedShipDate", estimatedShipDate);
            	tmpConditon = EntityCondition.makeCondition(mapCondition);
    		}
    		if (originFacilityId != null && !"".equals(originFacilityId)){
    			mapCondition.put("originFacilityId", originFacilityId);
            	tmpConditon = EntityCondition.makeCondition(mapCondition);
    		}
    		listAllConditions.add(tmpConditon);
    		EntityCondition deliveryEntryCon = EntityCondition.makeCondition("deliveryEntryId", EntityComparisonOperator.EQUALS , null);
    		EntityCondition statusCondition = EntityCondition.makeCondition("statusId", EntityComparisonOperator.IN, UtilMisc.toSet("SHIPMENT_PICKED", "SHIPMENT_PACKED"));
        	listAllConditions.add(tmpConditon);
        	listAllConditions.add(deliveryEntryCon);
        	listAllConditions.add(statusCondition);
    		try {
    			listIterator = delegator.findList("ShipmentAndContactMechDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, opts, false);
    		} catch (GenericEntityException e) {
    			e.printStackTrace();
    		}
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> assignShipmentToDE(DispatchContext ctx, Map<String, Object> context){
		
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
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "AssignShipmentToDEFail", locale));
		}
		shipment.put("deliveryEntryId", deliveryEntryId);
		try {
			shipment.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "AssignShipmentToDEFail", locale));
		}
		
		//Update weight for DE
		GenericValue deliveryEntry = null;
		try {
			deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "AssignShipmentToDEFail", locale));
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
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "AssignShipmentToDEFail", locale));
		}
		
		Map<String, Object> convertUomContext = FastMap.newInstance();
		convertUomContext.put("originalValue", getTotalShipmentItemResult.get("totalWeight"));
		convertUomContext.put("userLogin", userLogin);
		convertUomContext.put("uomId", shipment.get("defaultWeightUomId"));
		convertUomContext.put("uomIdTo", deliveryEntry.get("weightUomId"));
		Map<String, Object> convertUomResult = null;
		try {
			convertUomResult = dispatcher.runSync("convertUom", convertUomContext);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "AssignShipmentToDEFail", locale));
		}
		weight = weight.add((BigDecimal)convertUomResult.get("convertedValue"));
		deliveryEntry.put("weight", weight);
		try {
			deliveryEntry.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "AssignShipmentToDEFail", locale));
		}
		Timestamp thruDateShipment = shipment.getTimestamp("estimatedArrivalDate");
		deliveryEntry.put("thruDate", DateUtil.getBiggerDateTime(deliveryEntry.getTimestamp("thruDate"), thruDateShipment));
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(logisticsUiLabel, "AssignShipmentToDESuccessfully", locale));
	}
	
	public static Map<String, Object> updateDeliveryEntry(DispatchContext ctx, Map<String, Object> context){
		
		//Parameters
		String deliveryEntryId = (String) context.get("deliveryEntryId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		
		//When DE is shipped then all shipment in it is shipped
		List<GenericValue> listShipment = null;
		if("DELI_ENTRY_SHIPED".equals(statusId)){
			try {
				listShipment = delegator.findList("Shipment", EntityCondition.makeCondition("deliveryEntryId", deliveryEntryId), null, null, null, false);
				for(GenericValue item : listShipment){
					item.put("statusId", "SHIPMENT_SHIPPED");
					item.store();
				}
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntryFail", locale));
			}
			
		}
		
		//Update status for Delivery Entry
		GenericValue deliveryEntry = null;
		try {
			deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntryFail", locale));
		}
		deliveryEntry.put("statusId", statusId);
		try {
			deliveryEntry.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntryFail", locale));
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntrySuccessfully", locale));
	}
	
	public static Map<String, Object> updateDeliveryByShipment(DispatchContext ctx, Map<String, Object> context){
		//Get parameters
		String shipmentId = (String)context.get("shipmentId");
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		
		//Update shipment
		GenericValue shipment = null;
		try {
			shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntryFail", locale));
		}
		
		String deliveryEntryId = shipment.getString("deliveryEntryId");
		if(deliveryEntryId != null){
			boolean isAllDelivered = true;
			List<GenericValue> shipmentList = null;
			try {
				 shipmentList = delegator.findList("Shipment", EntityCondition.makeCondition("deliveryEntryId", deliveryEntryId), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntryFail", locale));
			}
			
			for(GenericValue item: shipmentList){
				if(!"SHIPMENT_DELIVERED".equals(item.getString("statusId"))){
					isAllDelivered = false;
					break;
				}
			}
			if(isAllDelivered){
				GenericValue deliveryEntry = null;
				try {
					deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
					deliveryEntry.put("statusId", "DELI_ENTRY_COMPLETED");
					deliveryEntry.store();
				} catch (GenericEntityException e) {
					Debug.log(e.getStackTrace().toString(), module);
					ServiceUtil.returnError(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntryFail", locale));
				}
			}
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(logisticsUiLabel, "updateDeliveryEntrySuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDeliveryEntryVehicles(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
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
    	List<GenericValue> listItems = new ArrayList<GenericValue>();
    	try {
    		listItems = delegator.findList("DeliveryEntryVehicleDetail", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getDeliveryEntryVehicles service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listItems);
    	return successResult;
	}
	
	public static Map<String, Object> getDeliveryEntryVehicleRole(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String deliveryEntryId = (String)context.get("deliveryEntryId");
    	String vehicleId = (String)context.get("vehicleId");
    	String roleTypeId = (String)context.get("roleTypeId");
    	String partyId = null;
    	try {
			List<GenericValue> deVehicleRoles = delegator.findList("DeliveryEntryVehicleRole", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "vehicleId", vehicleId, "roleTypeId", roleTypeId)), null, null, null, false);
			if (deVehicleRoles.isEmpty()){
				return ServiceUtil.returnError("Delivery Entry Vehicle Role not found");
			}
			partyId = deVehicleRoles.get(0).getString("partyId");
    	} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	successResult.put("partyId", partyId);
    	return successResult;
	}
}
