package com.olbius.accounting.appr;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.DelysServices;
import com.olbius.services.JqxWidgetSevices;
import com.olbius.util.ProductUtil;
import com.olbius.util.SecurityUtil;

public class DeliveryServices {
	
	public enum DeliveryStatus{
		DLV_CREATED, DLV_EXPORTED, DLV_DELIVERED, DLV_APPROVED, DLV_COMPLETED, DLV_CANCELED;
	}
	
	public static final String module = DeliveryServices.class.getName();
	public static final String ApprUiLabels = "DelysAccApprUiLabels";
	public static final String resource = "DelysUiLabels";
	public static final int DELIVERY_ITEM_SEQUENCE_ID_DIGITS = 5;
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String,Object> createDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	
    	//Get Parameters
    	String deliveryId = delegator.getNextSeqId("Delivery");
    	String partyIdTo = (String)context.get("partyIdTo");
    	String partyIdFrom = (String)context.get("partyIdFrom");
    	String currencyUomId = (String)context.get("currencyUomId");
    	String originProductStoreId = (String)context.get("originProductStoreId");
    	String orderId = (String)context.get("orderId");
    	String statusId = (String)context.get("statusId");
    	String destContactMechId = (String)context.get("destContactMechId");
    	String originContactMechId = (String)context.get("originContactMechId");
    	String originFacilityId = (String)context.get("originFacilityId");
    	String destFacilityId = (String)context.get("destFacilityId");
    	String defaultWeightUomId = (String)context.get("defaultWeightUomId");
    	String noNumber = (String)context.get("no");
    	String deliveryTypeId = (String)context.get("deliveryTypeId");
    	List<Object> listItemTmp = (List<Object>)context.get("listOrderItems");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, String>> listOrderItems = new ArrayList<Map<String,String>>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("orderId")){
					mapItems.put("orderId", item.getString("orderId"));
				}
				if (item.containsKey("orderItemSeqId")){
					mapItems.put("orderItemSeqId", item.getString("orderItemSeqId"));
				}
				if (item.containsKey("expireDate")){
					mapItems.put("expireDate", item.getString("expireDate"));
				}
				if (item.containsKey("actualExpireDate")){
					mapItems.put("actualExpireDate", item.getString("actualExpireDate"));
				}
				if (item.containsKey("actualManufacturedDate")){
					mapItems.put("actualManufacturedDate", item.getString("actualManufacturedDate"));
				}
				if (item.containsKey("actualDeliveredQuantity")){
					mapItems.put("actualDeliveredQuantity", item.getString("actualDeliveredQuantity"));
				}
				if (item.containsKey("actualExportedQuantity")){
					mapItems.put("actualExportedQuantity", item.getString("actualExportedQuantity"));
				}
				if (item.containsKey("quantity")){
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("statusId")){
					mapItems.put("statusId", item.getString("statusId"));
				}
				listOrderItems.add(mapItems);
			}
    	} else {
    		listOrderItems = (List<Map<String, String>>)context.get("listOrderItems");
    	}
    	//Make Delivery
    	GenericValue delivery = delegator.makeValue("Delivery");
    	Timestamp deliveryDate = (Timestamp)(context.get("deliveryDate"));
    	Timestamp estimatedStartDate = (Timestamp)(context.get("estimatedStartDate"));
    	Timestamp estimatedArrivalDate = (Timestamp)(context.get("estimatedArrivalDate"));
    	delivery.put("deliveryDate", deliveryDate);
    	delivery.put("estimatedStartDate", estimatedStartDate);
    	delivery.put("estimatedArrivalDate", estimatedArrivalDate);
    	Timestamp createDate = UtilDateTime.nowTimestamp();
    	delivery.put("createDate", createDate);
    	delivery.put("partyIdFrom", partyIdFrom);
    	delivery.put("deliveryTypeId", deliveryTypeId);
    	delivery.put("currencyUomId", currencyUomId);
    	delivery.put("originContactMechId", originContactMechId);
    	delivery.put("originFacilityId", originFacilityId);
    	delivery.put("destFacilityId", destFacilityId);
    	delivery.put("deliveryId", deliveryId);
    	delivery.put("partyIdTo", partyIdTo);
    	delivery.put("originProductStoreId", originProductStoreId);
    	delivery.put("orderId", orderId);
    	delivery.put("statusId", statusId);
    	delivery.put("no", noNumber);
    	delivery.put("defaultWeightUomId", defaultWeightUomId);
    	delivery.put("destContactMechId", destContactMechId);
    	GenericValue OrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    	OrderHeader.put("productStoreId", originProductStoreId);
    	delivery.put("totalAmount", BigDecimal.ZERO);
    	delivery.create();
    	
    	//Make Delivery Item
    	if (!listOrderItems.isEmpty()){
    		for (Map<String, String> item : listOrderItems){
    			GenericValue deliveryItem = delegator.makeValue("DeliveryItem");
    			deliveryItem.put("deliveryId", deliveryId);
				//Set Seq for delivery Item
		        deliveryItem.put("deliveryItemSeqId", Integer.toString(listOrderItems.indexOf(item) + 1));
    			deliveryItem.put("fromOrderItemSeqId", item.get("orderItemSeqId"));
    			deliveryItem.put("fromOrderId", item.get("orderId"));
    			deliveryItem.put("quantity", BigDecimal.valueOf(Double.parseDouble(item.get("quantity"))));
    			if (item.get("actualExportedQuantity") != null){
    				deliveryItem.put("actualExportedQuantity", new BigDecimal(item.get("actualExportedQuantity")));
    			} else {
    				deliveryItem.put("actualExportedQuantity", BigDecimal.ZERO);
    			}
    			if (item.get("actualDeliveredQuantity") != null){
    				deliveryItem.put("actualDeliveredQuantity", new BigDecimal(item.get("actualDeliveredQuantity")));
    			} else {
    				deliveryItem.put("actualDeliveredQuantity", BigDecimal.ZERO);
    			}
    			if (item.get("statusId") != null){
    				deliveryItem.put("statusId", item.get("statusId"));
    			} else {
    				deliveryItem.put("statusId", "DELI_ITEM_CREATED");
    			}
    			if (item.containsKey("actualExpireDate")){
    				deliveryItem.put("actualExpireDate", Timestamp.valueOf(item.get("actualExpireDate")));
    			} 
    			if (item.containsKey("actualManufacturedDate")){
    				deliveryItem.put("actualManufacturedDate", Timestamp.valueOf(item.get("actualManufacturedDate")));
    			}
    			if (item.containsKey("expireDate")){
    				deliveryItem.put("expireDate", new Timestamp(new Long(item.get("expireDate"))));
    			}
    			deliveryItem.create();
    		}
    	}
    	
    	//Create DeliveryStatus
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		GenericValue deliveryStatus = delegator.makeValue("DeliveryStatus");
		deliveryStatus.put("deliveryStatusId", delegator.getNextSeqId("DeliveryStatus"));
		deliveryStatus.put("deliveryId", deliveryId);
		deliveryStatus.put("statusId", statusId);
		deliveryStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
		deliveryStatus.put("statusUserLogin", userLoginId);
		delegator.createOrStore(deliveryStatus);
		
		// send notification for storekeeper and deliveryadmin 
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<String> listStoreKeepers = new ArrayList<String>();
		List<String> listPartyGroups = SecurityUtil.getPartiesByRoles("LOG_STOREKEEPER", delegator);
		if (!listPartyGroups.isEmpty()){
			for (String group : listPartyGroups){
				try {
					List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", "LOG_STOREKEEPER")), null, null, null, false);
					listManagers = EntityUtil.filterByDate(listManagers);
					if (!listManagers.isEmpty()){
						for (GenericValue manager : listManagers){
							String partyId = manager.getString("partyIdFrom");
							List<GenericValue> listStorekeeperByFacility = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("originFacilityId"), "partyId", partyId, "roleTypeId", "LOG_STOREKEEPER")), null, null, null, false);
							listStorekeeperByFacility = EntityUtil.filterByDate(listStorekeeperByFacility);
							if (!listStorekeeperByFacility.isEmpty()){
								listStoreKeepers.add(partyId);	
							}
						}
					}
				} catch (GenericEntityException e) {
					ServiceUtil.returnError("get Party relationship error!");
				}
			}
		}
		if(!listStoreKeepers.isEmpty()){
			String header = UtilProperties.getMessage(resource, "NewDeliveryRequest", (Locale)context.get("locale")) +", "+ UtilProperties.getMessage(resource, "DeliveryIdAbb", (Locale)context.get("locale")) +": [" +deliveryId+"]";
			for (String managerParty : listStoreKeepers){
				String targetLink = "deliveryTypeId=DELIVERY_SALES;deliveryId="+deliveryId;
				String sendToPartyId = managerParty;
				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("partyId", sendToPartyId);
				// FIXME notify must be direct to detail of delivery in List Delivery
				mapContext.put("action", "getSalesDeliveries");
				mapContext.put("targetLink", targetLink);
				mapContext.put("header", header);
				mapContext.put("userLogin", userLogin);
				try {
					dispatcher.runSync("createNotification", mapContext);
				} catch (GenericServiceException e) {
					ServiceUtil.returnError("createNotification error!");
				}
			}
		}
		
		
		List<String> listDeliveryAdmins = new ArrayList<String>();
		List<String> listPartyGroupDlvAdmins = SecurityUtil.getPartiesByRoles("LOG_DELIVERY", delegator);
		if (!listPartyGroupDlvAdmins.isEmpty()){
			for (String group : listPartyGroupDlvAdmins){
				try {
					List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", "LOG_DELIVERY")), null, null, null, false);
					listManagers = EntityUtil.filterByDate(listManagers);
					if (!listManagers.isEmpty()){
						for (GenericValue manager : listManagers){
							String partyId = manager.getString("partyIdFrom");
							// FIXME if deliveryadmin have relation with specific facility we must filter by origin facility
							listDeliveryAdmins.add(partyId);	
						}
					}
				} catch (GenericEntityException e) {
					ServiceUtil.returnError("get Party relationship error!");
				}
			}
		}
		if(!listDeliveryAdmins.isEmpty()){
			String header = UtilProperties.getMessage(resource, "NewDeliveryRequest", (Locale)context.get("locale")) +", "+ UtilProperties.getMessage(resource, "DeliveryIdAbb", (Locale)context.get("locale")) +": [" +deliveryId+"]";
			for (String managerParty : listDeliveryAdmins){
				String targetLink = "deliveryTypeId=DELIVERY_SALES;deliveryId="+deliveryId;
				String sendToPartyId = managerParty;
				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("partyId", sendToPartyId);
				// FIXME notify must be direct to detail of delivery in List Delivery
				mapContext.put("action", "getSalesDeliveries");
				mapContext.put("targetLink", targetLink);
				mapContext.put("header", header);
				mapContext.put("userLogin", userLogin);
				try {
					dispatcher.runSync("createNotification", mapContext);
				} catch (GenericServiceException e) {
					ServiceUtil.returnError("createNotification error!");
				}
			}
		}
		result.put("deliveryTypeId", deliveryTypeId);
    	result.put("deliveryId", deliveryId);
    	return result;
	}
	
	public static Map<String,Object> createShipmentFromDelivery(DispatchContext ctx, Map<String, Object> context){
		String deliveryId = (String)context.get("deliveryId");
		Locale locale = (Locale)context.get("locale");
		
		//Create Shipment
		Map<String, Object> shipmentPara = FastMap.newInstance();
		shipmentPara.put("deliveryId", deliveryId);
		shipmentPara.put("ctx", ctx);
		shipmentPara.put("locale", locale);
		DeliveryHelper.createShipmentFromDelivery(shipmentPara);
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(ApprUiLabels, "createShipmentFromDeliverySuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,Object> updateDeliveryItemList(DispatchContext ctx, Map<String, Object> context){
	    Locale locale = (Locale)context.get("locale");
	    List<Map<String, Object>> listDeliveryItems = null;
	    String pathScanFile = (String)context.get("pathScanFile");
	    String deliveryId = (String)context.get("deliveryId");
	    String strList = (String)context.get("listDeliveryItems");
	    Long actualStartDate = (Long)context.get("actualStartDate");
	    Long actualArrivalDate = (Long)context.get("actualArrivalDate");
	    try {
            listDeliveryItems = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", strList);
        } catch (ParseException e1) {
            return ServiceUtil.returnError(e1.toString());
        }
	    // update path file scan delivery
	    Delegator delegator = ctx.getDelegator();
	    GenericValue delivery = null;
	    if (deliveryId != null){
	    	try {
				delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			} catch (GenericEntityException e) {
				ServiceUtil.returnError("Delivery not found!");
			}
	    }
	    if (pathScanFile != null && deliveryId != null){
	    	delivery.put("pathScanFile", pathScanFile);
	    }
	    if (actualStartDate != null && deliveryId != null){
	    	delivery.put("actualStartDate", new Timestamp(actualStartDate));
	    }
	    if (actualArrivalDate != null && deliveryId != null){
	    	delivery.put("actualArrivalDate", new Timestamp(actualArrivalDate));
	    }
	    try {
			delegator.store(delivery);
		} catch (GenericEntityException e1) {
			ServiceUtil.returnError("Store delivery error!");
		}
	    
	    Map<String, Object> tmpMap;
	    for(int i = 0; i < listDeliveryItems.size();i++){
	        tmpMap = new HashMap<String, Object>();
	        tmpMap.put("fromOrderId", listDeliveryItems.get(i).get("fromOrderId"));
	        tmpMap.put("fromOrderItemSeqId", listDeliveryItems.get(i).get("fromOrderItemSeqId"));
	        tmpMap.put("fromTransferId", listDeliveryItems.get(i).get("fromTransferId"));
	        tmpMap.put("fromTransferItemSeqId", listDeliveryItems.get(i).get("fromTransferItemSeqId"));
	        tmpMap.put("inventoryItemId", listDeliveryItems.get(i).get("inventoryItemId"));
	        tmpMap.put("deliveryId", listDeliveryItems.get(i).get("deliveryId"));
	        tmpMap.put("deliveryItemSeqId", listDeliveryItems.get(i).get("deliveryItemSeqId"));
	        BigDecimal tmpBD = new BigDecimal(listDeliveryItems.get(i).get("actualExportedQuantity").toString());
	        tmpMap.put("actualExportedQuantity", tmpBD);
	        tmpBD = new BigDecimal(listDeliveryItems.get(i).get("actualDeliveredQuantity").toString());
	        tmpMap.put("actualDeliveredQuantity", tmpBD);
	        tmpMap.put("userLogin", (GenericValue)context.get("userLogin"));
	        LocalDispatcher dispatcher = ctx.getDispatcher();
	        try {
                dispatcher.runSync("updateDeliveryItem", tmpMap);
            } catch (GenericServiceException e) {
                return ServiceUtil.returnError(e.toString());
            }
	    }
	    Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(ApprUiLabels, "updateDeliverySuccessfully", locale));
	    return result;
	}
    public static Map<String,Object> updateDeliveryItem(DispatchContext ctx, Map<String, Object> context){
		//Get parameters
		String deliveryId = (String)context.get("deliveryId");
		String orderId = (String)context.get("fromOrderId");
		String transferId = (String)context.get("fromTransferId");
		String orderItemSeqId = (String)context.get("fromOrderItemSeqId");
		String transferItemSeqId = (String)context.get("fromTransferItemSeqId");
		String strInventoryItemId = (String)context.get("inventoryItemId");
		String deliveryItemSeqId = (String)context.get("deliveryItemSeqId");
		BigDecimal actualExportedQuantity = (BigDecimal)context.get("actualExportedQuantity"); // The number to reserve
		BigDecimal actualDeliveredQuantity = (BigDecimal)context.get("actualDeliveredQuantity");
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// FIXME check for DeliveryItem has exported
		// I. Update Delivery
		//Create Subject, Observer and Attach Observer to Subject 
		Observer o = new DeliveryObserver();
		ItemSubject is = new DeliveryItemSubject();
		is.attach(o);
		
		//Set Map
		Map<String, Object> parameters = FastMap.newInstance();
		parameters.put("deliveryId", deliveryId);
		parameters.put("deliveryItemSeqId", deliveryItemSeqId);
		parameters.put("delegator", delegator);
		String deliveryTypeId = null;
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			if(orderId == null || "".equals(orderId)){
			    orderId = delivery.getString("orderId");
			}
		} catch (GenericEntityException e) {
		    return ServiceUtil.returnError(e.toString());
		}
		if (delivery == null) {
            Debug.logError("delivery is null", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "DeliveryCannotBeFound", (Locale)context.get("locale")));
        }
		deliveryTypeId = (String)delivery.get("deliveryTypeId");
		if(actualDeliveredQuantity.intValue() == 0){
//		    String strOldInventoryItemId = "";
		    GenericValue newINV = null;
		    // Update actualExpireDate for DeliveryItem
	        try {
	            newINV = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", strInventoryItemId));
	            GenericValue tmpDelItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
	            if(tmpDelItem != null && newINV != null){
//	                strOldInventoryItemId = tmpDelItem.getString("inventoryItemId");
	                tmpDelItem.set("actualExpireDate", newINV.get("expireDate"));
	                tmpDelItem.set("inventoryItemId", strInventoryItemId);
	                tmpDelItem.store();
	            }
	        } catch (GenericEntityException e) {
	            return ServiceUtil.returnError(e.toString());
	        }
		    
			//Issue to Delivery Note
			Map<String, Object> issuePara = FastMap.newInstance();
			issuePara.put("deliveryId", deliveryId);
			issuePara.put("deliveryItemSeqId", deliveryItemSeqId);
			issuePara.put("context", ctx);
			issuePara.put("locale", locale);
			BigDecimal quantityToIssuse = actualExportedQuantity;
			try {
				GenericValue deliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
				String quantityUomId = null;
				String baseUomId = null;
				GenericValue product = null;
				if ("DELIVERY_SALES".equals(deliveryTypeId)){
				    // TODO Optimize the following code
		            // I. Update reservation and InventoryItemDetail, balanceINV and Insert new record to OrderItemShipGrpRes
		            List<GenericValue> listResevedItem = null;
		            try {
		                //  1. Update reservation and InventoryItemDetail, balanceINV
		                List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		                listAllConditions.add(EntityCondition.makeCondition("orderId", orderId));
		                listAllConditions.add(EntityCondition.makeCondition("orderItemSeqId", orderItemSeqId));
		                // get OrderItem to get quantityUomId
		                GenericValue tmpOI = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
		                GenericValue tmpProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", tmpOI.get("productId")));
		                Map<String, Object> convertUomContext = FastMap.newInstance();
		                convertUomContext.put("originalValue", actualExportedQuantity);
		                convertUomContext.put("userLogin", userLogin);
		                convertUomContext.put("uomId", tmpOI.get("quantityUomId"));
		                convertUomContext.put("uomIdTo", tmpProduct.get("quantityUomId"));
		                BigDecimal bdConverted = ProductUtil.getConvertPackingNumber(delegator, tmpOI.getString("productId"), tmpOI.getString("quantityUomId"), tmpProduct.getString("quantityUomId"));
		                // TODO If one orderItem can be split into multi-delivery in the same facility, the following code must be changed 
		                listResevedItem = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), UtilMisc.toSet("inventoryItemId", "quantity", "shipGroupSeqId"), null, null, false);
		               
		                BigDecimal tmpNotReserve = new BigDecimal(bdConverted.multiply(actualExportedQuantity).toString());
		                for(int i = 0; i < listResevedItem.size();i++){
		                    BigDecimal tmpBD = listResevedItem.get(i).getBigDecimal("quantity");
		                    BigDecimal tmpInsertBD = new BigDecimal(tmpBD.toString());
		                    GenericValue tmpRes = delegator.findOne("OrderItemShipGrpInvRes", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", listResevedItem.get(i).getString("shipGroupSeqId"), "inventoryItemId", listResevedItem.get(i).getString("inventoryItemId")));
		                    if(actualExportedQuantity.equals(tmpBD)){
		                        // delete this reservation
		                        delegator.removeValue(tmpRes);
		                        tmpNotReserve = BigDecimal.ZERO;
		                    }else{
		                        if(tmpNotReserve.compareTo(tmpBD) < 0){
		                            tmpInsertBD = new BigDecimal(tmpNotReserve.toString());
		                            tmpRes.set("quantity", tmpBD.subtract(tmpNotReserve));
		                            tmpRes.store();
		                        }else{
		                            delegator.removeValue(tmpRes);
		                        }
		                        tmpNotReserve = tmpNotReserve.subtract(tmpBD);
		                    }
		                    // FIXME Check for QOH and ATP of input inventoryItem is less than required quantity. 
		                    // update InventoryItemDetail
		                    GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		                    tmpInvDetail.set("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId"));
		                    tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		                    java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
		                    tmpInvDetail.set("effectiveDate", tmpDate);
		                    tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
		                    tmpInvDetail.set("availableToPromiseDiff", tmpInsertBD);
		                    tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
		                    tmpInvDetail.create();
		                    // balanceInventoryItems
		                    GenericValue tmpINV = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId")));
		                    if(tmpINV.get("expireDate") != null){
		                        Map<String, Object> tmpMap = new HashMap<String, Object>();
		                        GenericValue userSystemLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		                        tmpMap.put("userLogin", userSystemLogin);
		                        tmpMap.put("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId"));
		                        dispatcher.runSync("balanceInventoryItems", tmpMap);
		                    }
		                    // check to break this loop
		                    if(tmpNotReserve.compareTo(BigDecimal.ZERO) <= 0){
		                        break;
		                    }
		                }
		                //    2. Insert new record to OrderItemShipGrpRes
		                // FIXME find out another method. 
                        // set shipGroupID transfer_id, ship_group_seq_id, transfer_item_seq_id, inventory_item_id
                        String tmpSGSI = "00001";
                        for(int tmpG = 1; tmpG < 100; tmpG++){
                            if(tmpG < 10){
                                tmpSGSI = "0000" + Integer.toString(tmpG);
                            }else if(tmpG < 100){
                                tmpSGSI = "000" + Integer.toString(tmpG);
                            }
                            Map<String, Object> tmpMap = new HashMap<String, Object>();
                            tmpMap.put("transferId", transferId);
                            tmpMap.put("transferItemSeqId", transferItemSeqId);
                            tmpMap.put("inventoryItemId", strInventoryItemId);
                            tmpMap.put("shipGroupSeqId", tmpSGSI);
                            GenericValue tmpGSGSI = delegator.findOne("TransferItemShipGrpInvRes", tmpMap, false);
                            if(tmpGSGSI == null){
                                break;
                            }
                        }
		                GenericValue  tmpOISG = delegator.makeValue("OrderItemShipGrpInvRes");
		                tmpOISG.set("orderId", orderId);
		                tmpOISG.set("orderItemSeqId", orderItemSeqId);
		                tmpOISG.set("shipGroupSeqId", tmpSGSI);
		                tmpOISG.set("inventoryItemId", strInventoryItemId);
		                tmpOISG.set("reserveOrderEnumId", "INVRO_FIFO_REC");
		                tmpOISG.set("quantity", actualExportedQuantity.multiply(bdConverted));
		                tmpOISG.set("quantityNotAvailable", BigDecimal.ZERO);
		                java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
		                tmpOISG.set("reservedDatetime", tmpDate);
		                tmpOISG.set("createdDatetime", tmpDate);
		                tmpOISG.set("promisedDatetime", tmpDate);
		                tmpOISG.create();
		                // update InventoryItemDetail
		                GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		                tmpInvDetail.set("inventoryItemId", strInventoryItemId);
		                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		                tmpInvDetail.set("effectiveDate", tmpDate);
		                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.multiply(bdConverted).negate());
		                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("orderId", orderId);
		                tmpInvDetail.set("orderItemSeqId", orderItemSeqId);
		                tmpInvDetail.set("shipGroupSeqId", tmpSGSI);
		                tmpInvDetail.create();
		            } catch (Exception e) {
		                return ServiceUtil.returnError(e.toString());
		            }
		            
					GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", deliveryItem.getString("fromOrderId"), "orderItemSeqId", deliveryItem.get("fromOrderItemSeqId")));
					if (orderItem == null){
						ServiceUtil.returnError("OrderItem not found for DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
					}
					product = delegator.findOne("Product", false, UtilMisc.toMap("productId", orderItem.getString("productId")));
					if (product == null){
						ServiceUtil.returnError("Product not found for DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
					}
					baseUomId = product.getString("quantityUomId"); 
					if (baseUomId == null){
						ServiceUtil.returnError("Quantity Uom not found for product:" + product.getString("productId") + " of DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
					}
					quantityUomId = orderItem.getString("quantityUomId");
					BigDecimal convertNumber = BigDecimal.ONE;
					convertNumber = DelysServices.getConvertNumber(delegator, convertNumber, product.getString("productId"), quantityUomId, baseUomId);
					if (convertNumber.compareTo(BigDecimal.ONE) == 1){
						quantityToIssuse = actualExportedQuantity.multiply(convertNumber);
					}
				} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
				    // FIXME check for new INV is adapt quantity
				    // TODO Optimize the following code 
                    // I. Update reservation and InventoryItemDetail, balanceINV and Insert new record to TransferItemShipGrpRes
                    List<GenericValue> listResevedItem = null;
                    try {
                        //  1. Update reservation and InventoryItemDetail, balanceINV
                        List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
                        listAllConditions.add(EntityCondition.makeCondition("transferId", transferId));
                        listAllConditions.add(EntityCondition.makeCondition("transferItemSeqId", transferItemSeqId));
                        // get TransferItem to get quantityUomId
                        GenericValue tmpOI = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
                        GenericValue tmpProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", tmpOI.get("productId")));
                        Map<String, Object> convertUomContext = FastMap.newInstance();
                        convertUomContext.put("originalValue", actualExportedQuantity);
                        convertUomContext.put("userLogin", userLogin);
                        convertUomContext.put("uomId", tmpOI.get("quantityUomId"));
                        convertUomContext.put("uomIdTo", tmpProduct.get("quantityUomId"));
                        BigDecimal bdConverted = ProductUtil.getConvertPackingNumber(delegator, tmpOI.getString("productId"), tmpOI.getString("quantityUomId"), tmpProduct.getString("quantityUomId"));
                        // TODO If one transferItem can be split into multi-delivery in the same facility, the following code must be changed 
                        listResevedItem = delegator.findList("TransferItemShipGrpInvRes", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), UtilMisc.toSet("inventoryItemId", "quantity", "shipGroupSeqId"), null, null, false);
                       
                        BigDecimal tmpNotReserve = new BigDecimal(bdConverted.multiply(actualExportedQuantity).toString());
                        for(int i = 0; i < listResevedItem.size();i++){
                            BigDecimal tmpBD = listResevedItem.get(i).getBigDecimal("quantity");
                            BigDecimal tmpInsertBD = new BigDecimal(tmpBD.toString());
                            GenericValue tmpRes = delegator.findOne("TransferItemShipGrpInvRes", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId, "shipGroupSeqId", listResevedItem.get(i).getString("shipGroupSeqId"), "inventoryItemId", listResevedItem.get(i).getString("inventoryItemId")));
                            if(actualExportedQuantity.equals(tmpBD)){
                                // delete this reservation
                                delegator.removeValue(tmpRes);
                                tmpNotReserve = BigDecimal.ZERO;
                            }else{
                                if(tmpNotReserve.compareTo(tmpBD) < 0){
                                	// subtract quantity reserve and store GrpInvRes
                                    tmpInsertBD = new BigDecimal(tmpNotReserve.toString());
                                    tmpRes.set("quantity", tmpBD.subtract(tmpNotReserve));
                                    tmpRes.store();
                                }else{
                                    delegator.removeValue(tmpRes);
                                }
                                tmpNotReserve = tmpNotReserve.subtract(tmpBD);
                            }
                            // FIXME Check for QOH and ATP of input inventoryItem is less than required quantity. 
                            // update InventoryItemDetail
                            GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
                            tmpInvDetail.set("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId"));
                            tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
                            java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
                            tmpInvDetail.set("effectiveDate", tmpDate);
                            tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
                            tmpInvDetail.set("availableToPromiseDiff", tmpInsertBD);
                            tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
                            tmpInvDetail.create();
                            // balanceInventoryItems
                            GenericValue tmpINV = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId")));
                            if(tmpINV.get("expireDate") != null){
                                Map<String, Object> tmpMap = new HashMap<String, Object>();
                                GenericValue userSystemLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
                                tmpMap.put("userLogin", userSystemLogin);
                                tmpMap.put("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId"));
                                dispatcher.runSync("balanceInventoryItems", tmpMap);
                            }
                            // check to break this loop
                            if(tmpNotReserve.compareTo(BigDecimal.ZERO) <= 0){
                                break;
                            }
                        }
                        //    2. Insert new record to TransferItemShipGrpRes
                        // FIXME find out another method. 
//                         set shipGroupID transfer_id, ship_group_seq_id, transfer_item_seq_id, inventory_item_id
                        String tmpSGSI = "00001";
                        for(int tmpG = 1; tmpG < 100; tmpG++){
                            if(tmpG < 10){
                                tmpSGSI = "0000" + Integer.toString(tmpG);
                            }else if(tmpG < 100){
                                tmpSGSI = "000" + Integer.toString(tmpG);
                            }
                            Map<String, Object> tmpMap = new HashMap<String, Object>();
                            tmpMap.put("transferId", transferId);
                            tmpMap.put("transferItemSeqId", transferItemSeqId);
                            tmpMap.put("inventoryItemId", strInventoryItemId);
                            tmpMap.put("shipGroupSeqId", tmpSGSI);
                            GenericValue tmpGSGSI = delegator.findOne("TransferItemShipGrpInvRes", tmpMap, false);
                            if(tmpGSGSI == null){
                                break;
                            }
                        }
                        GenericValue  tmpOISG = delegator.makeValue("TransferItemShipGrpInvRes");
                        tmpOISG.set("transferId", transferId);
                        tmpOISG.set("transferItemSeqId", transferItemSeqId);
                        tmpOISG.set("shipGroupSeqId", tmpSGSI);
                        tmpOISG.set("inventoryItemId", strInventoryItemId);
                        tmpOISG.set("reserveTransferEnumId", "INVRO_FIFO_REC");
                        tmpOISG.set("quantity", actualExportedQuantity.multiply(bdConverted));
                        tmpOISG.set("quantityNotAvailable", BigDecimal.ZERO);
                        java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
                        tmpOISG.set("reservedDatetime", tmpDate);
                        tmpOISG.set("createdDatetime", tmpDate);
                        tmpOISG.set("promisedDatetime", tmpDate);
                        tmpOISG.create();
//                         update InventoryItemDetail
                        GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
                        tmpInvDetail.set("inventoryItemId", strInventoryItemId);
                        tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
                        tmpInvDetail.set("effectiveDate", tmpDate);
                        tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
                        tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.multiply(bdConverted).negate());
                        tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
                        tmpInvDetail.set("transferId", transferId);
                        tmpInvDetail.set("transferItemSeqId", transferItemSeqId);
                        tmpInvDetail.set("shipGroupSeqId", tmpSGSI);
                        tmpInvDetail.create();
                    } catch (Exception e) {
                        return ServiceUtil.returnError(e.toString());
                    }
				    
					GenericValue transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", deliveryItem.getString("fromTransferId"), "transferItemSeqId", deliveryItem.get("fromTransferItemSeqId")));
					if (transferItem == null){
						ServiceUtil.returnError("TransferItem not found for DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
					}
					product = delegator.findOne("Product", false, UtilMisc.toMap("productId", transferItem.getString("productId")));
					if (product == null){
						ServiceUtil.returnError("Product not found for DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
					}
					baseUomId = product.getString("quantityUomId");
					if (baseUomId == null){
						ServiceUtil.returnError("Quantity Uom not found for product:" + product.getString("productId") + " of DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
					}
					quantityUomId = transferItem.getString("quantityUomId");
					BigDecimal convertNumber = BigDecimal.ONE;
					convertNumber = DelysServices.getConvertNumber(delegator, convertNumber, product.getString("productId"), quantityUomId, baseUomId);
					if (convertNumber.compareTo(BigDecimal.ONE) == 1){
						quantityToIssuse = actualExportedQuantity.multiply(convertNumber);
					}
				}
				issuePara.put("quantity", quantityToIssuse);
				try {
					DeliveryHelper.issuseDelivery(issuePara);
				} catch (Exception e) {
				    Debug.log(e.getStackTrace().toString(), module);
                    ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryItemError", locale));
    			}
    			parameters.put("actualExportedQuantity", actualExportedQuantity);
    			try {
    				is.updateExportedQuantity(parameters);
    				// up date delivery item status
    				List<GenericValue> deliveryByTransferItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", deliveryItem.get("fromTransferId"), "fromTransferItemSeqId", deliveryItem.get("fromTransferItemSeqId"))), null, null, null, false);
    				Boolean checkExported = false;
    				BigDecimal totalItemQuantity = BigDecimal.ZERO;
    						
    				if (!deliveryByTransferItem.isEmpty()){
    					for (GenericValue dlvItem : deliveryByTransferItem){
    						if (dlvItem.getBigDecimal("actualExportedQuantity").compareTo(BigDecimal.ZERO) > 0){
    							checkExported = true;
    						} else {
    							checkExported = false;
    						}
    						totalItemQuantity = totalItemQuantity.add(dlvItem.getBigDecimal("quantity"));
    					}
    				}
    				GenericValue transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", deliveryItem.get("fromTransferId"), "transferItemSeqId", deliveryItem.get("fromTransferItemSeqId")));
    				Map<String, Object> mapChangeStatus = FastMap.newInstance();
    				mapChangeStatus.put("transferId", deliveryItem.get("fromTransferId"));
    				mapChangeStatus.put("transferItemSeqId", deliveryItem.get("fromTransferItemSeqId"));
    				mapChangeStatus.put("fromStatusId", transferItem.getString("statusId"));
    				mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
    				mapChangeStatus.put("userLogin", userLogin);
    				try {
    					if (checkExported && transferItem.getBigDecimal("quantity").compareTo(totalItemQuantity) == 0){
    						mapChangeStatus.put("statusId", "TRANS_ITEM_EXPORTED");
    						dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
    					} else if (actualExportedQuantity.compareTo(BigDecimal.ZERO) > 0) {
    						mapChangeStatus.put("statusId", "TRANS_ITEM_APART_EXP");
    						dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
    					}
    				} catch (GenericServiceException e) {
    					ServiceUtil.returnError("Change trasfer item status Error!");
    				}
    			} catch (Exception e) {
    				Debug.log(e.getStackTrace().toString(), module);
    				ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryItemError", locale));
    			}
			} catch (Exception e) {
                Debug.log(e.getStackTrace().toString(), module);
                ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryItemError", locale));
            }
		} else{
			if ("DELIVERY_SALES".equals(deliveryTypeId)){
				parameters.put("actualDeliveredQuantity", actualDeliveredQuantity);
				try {
					is.updateDeliveredQuantity(parameters);
				} catch (GenericEntityException e) {
					Debug.log(e.getStackTrace().toString(), module);
					ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryItemError", locale));
				}
			} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
				// update inventory item of destination facility
				try {
					GenericValue deliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
					if (deliveryItem == null) {
			            Debug.logError("delivery item is null", module);
			            return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "DeliveryItemCannotBeFound", (Locale)context.get("locale")));
			        }
					parameters.put("actualDeliveredQuantity", actualDeliveredQuantity);
					try {
						is.updateDeliveredQuantity(parameters);
					} catch (GenericEntityException e) {
						Debug.log(e.getStackTrace().toString(), module);
						ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryItemError", locale));
					}
					transferId = (String)deliveryItem.get("fromTransferId");
					transferItemSeqId = (String)deliveryItem.get("fromTransferItemSeqId");
					List<GenericValue> listTransferItemShipGroups = delegator.findList("TransferItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId)), null, null, null, false);
					if (!listTransferItemShipGroups.isEmpty()){
						GenericValue transferItemShipGroup = listTransferItemShipGroups.get(0);
						String shipGroupSeqId = (String)transferItemShipGroup.get("shipGroupSeqId");
						List<GenericValue> listInventoryItems = delegator.findList("TransferItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId, "shipGroupSeqId", shipGroupSeqId)), null, null, null, false);
						if (!listInventoryItems.isEmpty()){
							String inventoryItemId = (String)listInventoryItems.get(0).get("inventoryItemId");
							GenericValue invItemFrom = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
							Map<String, Object> mapInv = FastMap.newInstance();
							GenericValue transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
							GenericValue product = null;
							BigDecimal quantityExported = actualExportedQuantity;
							BigDecimal quantityDelivered = actualDeliveredQuantity;
							if (transferItem != null){
								
								product = delegator.findOne("Product", false, UtilMisc.toMap("productId", transferItem.getString("productId")));
								if (product == null){
									ServiceUtil.returnError("Product not found for DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
								}
								String baseUomId = product.getString("quantityUomId");
								if (baseUomId == null){
									ServiceUtil.returnError("Quantity Uom not found for product:" + product.getString("productId") + " of DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
								}
								String quantityUomId = transferItem.getString("quantityUomId");
								BigDecimal convertNumber = BigDecimal.ONE;
								convertNumber = DelysServices.getConvertNumber(delegator, convertNumber, product.getString("productId"), quantityUomId, baseUomId);
								if (convertNumber.compareTo(BigDecimal.ONE) == 1){
									quantityDelivered = actualDeliveredQuantity.multiply(convertNumber);
									quantityExported = actualExportedQuantity.multiply(convertNumber);
								}
								
								mapInv.put("productId", (String)transferItem.get("productId"));
								mapInv.put("facilityId", (String)delivery.get("destFacilityId"));
								mapInv.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
								mapInv.put("userLogin", userLogin);
								mapInv.put("expireDate", transferItem.get("expireDate"));
								mapInv.put("unitCost", invItemFrom.get("unitCost"));
								mapInv.put("purCost", invItemFrom.get("purCost"));
								mapInv.put("quantityAccepted", quantityDelivered);
								mapInv.put("shipmentId", delivery.get("shipmentId"));
								mapInv.put("transferId", transferId);
								mapInv.put("transferItemSeqId", transferItemSeqId);
								BigDecimal quantityRejected = quantityExported.subtract(quantityDelivered);
								if (quantityRejected.compareTo(BigDecimal.ZERO) == 1){
									mapInv.put("quantityRejected", quantityRejected);
									mapInv.put("quantityExcess", BigDecimal.ZERO);
								} else if (quantityRejected.compareTo(BigDecimal.ZERO) == -1){
									mapInv.put("quantityRejected", BigDecimal.ZERO);
									mapInv.put("quantityExcess", quantityRejected.negate());
								} else if (quantityRejected.compareTo(BigDecimal.ZERO) == 0){	
									mapInv.put("quantityExcess", BigDecimal.ZERO);
									mapInv.put("quantityRejected", BigDecimal.ZERO);
								}
								mapInv.put("quantityQualityAssurance", BigDecimal.ZERO);
								try {
									dispatcher.runSync("receiveInventoryProduct", mapInv);
								} catch (GenericServiceException e) {
									e.printStackTrace();
								}
							}
							// up date delivery item status
							List<GenericValue> deliveryByTransferItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", deliveryItem.get("fromTransferId"), "fromTransferItemSeqId", deliveryItem.get("fromTransferItemSeqId"))), null, null, null, false);
							Boolean checkDelivered = false;
							
							if (!deliveryByTransferItem.isEmpty()){
								for (GenericValue dlvItem : deliveryByTransferItem){
									if (dlvItem.getBigDecimal("actualDeliveredQuantity").compareTo(BigDecimal.ZERO) > 0){
										checkDelivered = true;
									} else {
										checkDelivered = false;
									}
								}
							}
							Map<String, Object> mapChangeStatus = FastMap.newInstance();
							mapChangeStatus.put("transferId", transferId);
							mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
							mapChangeStatus.put("fromStatusId", transferItem.getString("statusId"));
							mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
							mapChangeStatus.put("userLogin", userLogin);
							if (checkDelivered){
								mapChangeStatus.put("statusId", "TRANS_ITEM_DELIVERED");
							} else {
								mapChangeStatus.put("statusId", "TRANS_ITEM_APART_DLV");
							}
							try {
								dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
							} catch (GenericServiceException e) {
								ServiceUtil.returnError("Change trasfer item status Error!");
							}
						} else {
							Debug.logError("TransferItemIssuance not found", module);
				            return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "TransferItemIssuanceCannotBeFound", (Locale)context.get("locale")));
						}
					} else {
						Debug.logError("TransferItemShipGroup not found", module);
			            return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "TransferItemShipGroupCannotBeFound", (Locale)context.get("locale")));
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(ApprUiLabels, "updateDeliverySuccessfully", locale));
		try {
			delivery.refresh();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String deliveryStatusId = (String)delivery.get("statusId");
		transferId = (String)delivery.get("transferId");
		result.put("deliveryTypeId", deliveryTypeId);
		result.put("deliveryStatusId", deliveryStatusId);
		result.put("transferId", transferId);
		result.put("deliveryId", deliveryId);
		result.put("deliveryItemSeqId", deliveryItemSeqId);
		return result;
	}
	
	public static Map<String,Object> updateDelivery(DispatchContext ctx, Map<String, Object> context){
		
		//Get parameters
		String deliveryId = (String)context.get("deliveryId");
		String statusId = (String)context.get("statusId");
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = ctx.getDelegator();
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryError", locale));
		}
		delivery.put("statusId", statusId);
		try {
			delivery.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryError", locale));
		}
		
		/*//Update Delivery Item
		switch (DeliveryStatus.valueOf(statusId)) {
		case DLV_APPROVED:
			try {
				List<GenericValue> listDelivertItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition("deliveryId", deliveryId), null, null, null, false);
				for(GenericValue item: listDelivertItems){
					item.put("statusId", "DELI_ITEM_APPROVED");
					item.store();
				}
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryError", locale));
			}
			
			break;
		case DLV_CANCELED:
			try {
				List<GenericValue> listDelivertItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition("deliveryId", deliveryId), null, null, null, false);
				for(GenericValue item: listDelivertItems){
					item.put("statusId", "DELI_ITEM_CANCELED");
					item.store();
				}
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "updateDeliveryError", locale));
			}
			
			break;
		default:
			break;
		}*/
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(ApprUiLabels, "updateDeliverySuccessfully", locale));
	}
	
	public static Map<String,Object> getINVByOrderAndDlv(DispatchContext ctx, Map<String, Object> context){
	    Delegator delegator = ctx.getDelegator();
	    String deliveryId = (String)context.get("deliveryId");
	    String orderId = (String)context.get("orderId");
	    String facilityId = (String)context.get("facilityId");
	    List<EntityCondition> listCond = new ArrayList<EntityCondition>();
	    if(facilityId!= null && !"".equals(facilityId)){
	        listCond.add(EntityCondition.makeCondition("originFacilityId", facilityId));
	    }
	    if(deliveryId!= null && !"".equals(deliveryId)){
	        listCond.add(EntityCondition.makeCondition("deliveryId", deliveryId));
	    }
	    if(orderId != null && !"".equals(orderId)){
            listCond.add(EntityCondition.makeCondition("orderId", orderId));
        }
	    List<GenericValue> listData = null;
	    try{
	        listData = delegator.findList("DeliveryINVOrderItem", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), UtilMisc.toSet("facilityId", "productId", "inventoryItemId", "expireDate", "quantityOnHandTotal", "datetimeReceived"), null, null, false);
	    } catch (GenericEntityException e) {
            Debug.log(e.getStackTrace().toString(), module);
            return ServiceUtil.returnError("Service getINVByOrderAndDlv:" + e.toString());
        }
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    result.put("listData", listData);
	    return result;
	}
    public static Map<String,Object> getDeliveryById(DispatchContext ctx, Map<String, Object> context){
		//Get parameters
		String deliveryId = (String)context.get("deliveryId");
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("DeliveryDetail", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError("Service getDeliveryById:" + e.toString());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(ApprUiLabels, "getDeliverySuccessfully", locale));
		result.put("deliveryId", delivery.get("deliveryId"));
		result.put("statusId", delivery.get("statusId"));
		result.put("orderId", delivery.get("orderId"));
		result.put("transferId", delivery.get("transferId"));
		result.put("originFacilityId", delivery.get("originFacilityId"));
		result.put("destFacilityId", delivery.get("destFacilityId"));
		result.put("originProductStoreId", delivery.get("originProductStoreId"));
		result.put("createDate", delivery.get("createDate"));
		result.put("partyIdTo", delivery.get("partyIdTo"));
		result.put("destContactMechId", delivery.get("destContactMechId"));
		result.put("partyIdFrom", delivery.get("partyIdFrom"));
		result.put("deliveryDate", delivery.get("deliveryDate"));
		result.put("originContactMechId", delivery.get("originContactMechId"));
		result.put("deliveryDate", delivery.get("deliveryDate"));
		result.put("pathScanFile", delivery.get("pathScanFile"));
		result.put("estimatedStartDate", delivery.get("estimatedStartDate"));
		result.put("estimatedArrivalDate", delivery.get("estimatedArrivalDate"));
		result.put("actualStartDate", delivery.get("actualStartDate"));
		result.put("actualArrivalDate", delivery.get("actualArrivalDate"));
		result.put("no", delivery.get("no"));
		result.put("originFacilityName", delivery.get("originFacilityName"));
		result.put("destFacilityName", delivery.get("destFacilityName"));
		result.put("originAddress", delivery.get("originAddress"));
		result.put("destAddress", delivery.get("destAddress"));
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDeliveryEntryPackages(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listPackages = new ArrayList<GenericValue>();
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String deliveryEntryId = (String)parameters.get("deliveryEntryId")[0];
    	if (deliveryEntryId != null && !"".equals(deliveryEntryId)){
    		mapCondition.put("deliveryEntryId", deliveryEntryId);
    	}
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		listPackages = delegator.findList("DeliveryEntryPackage", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listPackages)){
    			for(GenericValue packageItem : listPackages){
    				Map<String, Object> row = new HashMap<String, Object>();
    				String deliveryEntryIdTmp = (String)packageItem.get("deliveryEntryId");
    				String deliveryPackageSeqId = (String)packageItem.get("deliveryPackageSeqId");
    				String deliveryBoxTypeId = (String)packageItem.get("deliveryBoxTypeId");
    				BigDecimal weight = packageItem.getBigDecimal("weight");
    				String weightUomId = (String)packageItem.get("weightUomId");
    				row.put("deliveryEntryId", deliveryEntryIdTmp);
    				row.put("deliveryPackageSeqId", deliveryPackageSeqId);
    				row.put("deliveryBoxTypeId", deliveryBoxTypeId);
    				row.put("weight", weight);
    				row.put("weightUomId", weightUomId);
    				
    				List<GenericValue> listPackageContent = delegator.findList("DeliveryEntryPackageContentDetail", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "deliveryPackageSeqId", deliveryPackageSeqId)), null, null, null, false);
    				List<Map<String, Object>> rowDetail = new ArrayList<Map<String,Object>>();
    				if(!UtilValidate.isEmpty(listPackageContent)){
    					for(GenericValue productItem : listPackageContent){
    						Map<String, Object> childDetail = new HashMap<String, Object>(); 
    						childDetail.put("deliveryEntryId", deliveryEntryIdTmp);
    						childDetail.put("deliveryPackageSeqId", deliveryPackageSeqId);
    						childDetail.put("shipmentId", productItem.getString("shipmentId"));
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
			String errMsg = "Fatal error calling getDeliveryEntryPackages service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
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
    		listShipments = delegator.findList("Shipment", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
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
	public static Map<String, Object> createDeliveryEntryPackage(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String deliveryEntryId = (String)context.get("deliveryEntryId");
		String deliveryBoxTypeId = (String)context.get("deliveryBoxTypeId");
		List<Map<String, String>> listShipmentItems = (List<Map<String, String>>)context.get("listShipmentItems");
		Locale locale = (Locale)context.get("locale");
		GenericValue deliveryEntry = null;
		try {
			deliveryEntry = delegator.findOne("DeliveryEntry", false, UtilMisc.toMap("deliveryEntryId", deliveryEntryId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (deliveryEntry == null) {
			return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "DeliveryEntryNotFound", locale));
		}
		if (listShipmentItems.isEmpty()){
			return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "NoShipmentItemFound", locale));
		}
		List<String> listShipmentIds = new ArrayList<String>();
		for (Map<String, String> item : listShipmentItems){
			if (!listShipmentIds.contains(item.get("shipmentId")) && item.get("shipmentId") != null){
				listShipmentIds.add(item.get("shipmentId"));
			}
		}
		String deliveryWeightUomId = deliveryEntry.getString("weightUomId");
		if (deliveryWeightUomId == null) {
			return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "DeliveryEntryWeightUomNotFound", locale));
		}
		BigDecimal boxWeight = BigDecimal.ZERO;
		String boxWeightUomId = null;
		GenericValue boxType = null;
		try {
			boxType = delegator.findOne("DeliveryBoxType", false, UtilMisc.toMap("deliveryBoxTypeId", deliveryBoxTypeId));
		} catch (GenericEntityException e2) {
			e2.printStackTrace();
		}
		if (boxType == null){
			return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "BoxTypeNotFound", locale));
		} else {
			try {
				if (boxType.getBigDecimal("boxWeight") != null){
					boxWeight = boxType.getBigDecimal("boxWeight");
					boxWeightUomId  = boxType.getString("weightUomId");
					if (!deliveryWeightUomId.equals(boxWeightUomId)){
						GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", boxWeightUomId, "uomIdTo", deliveryWeightUomId));
						boxWeight = boxWeight.multiply(conversion.getBigDecimal("conversionFactor"));
					}
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		if (!listShipmentIds.isEmpty()){
			for (String shipmentId : listShipmentIds){
				GenericValue newPackage = delegator.makeValue("DeliveryEntryPackage");
				delegator.setNextSubSeqId(newPackage, "deliveryPackageSeqId", 5, 1);
				newPackage.put("deliveryEntryId", deliveryEntryId);
				newPackage.put("deliveryBoxTypeId", deliveryBoxTypeId);
				newPackage.put("weightUomId", deliveryWeightUomId);
				
				try {
					delegator.createOrStore(newPackage);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				try {
					newPackage.refresh();
				} catch (GenericEntityException e1) {
					e1.printStackTrace();
				}
				BigDecimal productWeight = BigDecimal.ZERO;
				String productWeightUomId = null;
				String deliveryPackageSeqId = newPackage.getString("deliveryPackageSeqId");
				try {
			    	for(Map<String, String> item: listShipmentItems){
			    		if (shipmentId.equals(item.get("shipmentId"))){
				    		BigDecimal quantityToPacking = new BigDecimal(item.get("quantity"));
				    		BigDecimal weight = new BigDecimal(item.get("weight"));
				    		productWeightUomId = item.get("weightUomId");
				    		if (!deliveryWeightUomId.equals(productWeightUomId)){
				    			GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", productWeightUomId, "uomIdTo", deliveryWeightUomId));
					    		weight = weight.multiply(conversion.getBigDecimal("conversionFactor"));
				    		}
							productWeight = productWeight.add(quantityToPacking.multiply(weight));
							if (quantityToPacking.compareTo(BigDecimal.ZERO) == 1){
				    			GenericValue newPackageItem = delegator.makeValue("DeliveryEntryPackageContent");
				    			newPackageItem.put("deliveryEntryId", deliveryEntryId);
				    			newPackageItem.put("deliveryPackageSeqId", deliveryPackageSeqId);
				    			newPackageItem.put("shipmentId", item.get("shipmentId"));
				    			newPackageItem.put("shipmentItemSeqId", item.get("shipmentItemSeqId"));
				    			newPackageItem.put("quantity", quantityToPacking);
								delegator.createOrStore(newPackageItem);
				    		}
			    		}
			    	}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
		    	newPackage.put("weight", boxWeight.add(productWeight));
		    	try {
					delegator.store(newPackage);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
    	return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getShipmentItemByDeliveryEntry(DispatchContext ctx, Map<String, ? extends Object> context){
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
    		listShipments = delegator.findList("Shipment", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listShipments)){
    			for(GenericValue shipment : listShipments){
    				List<GenericValue> listShipmentItems = delegator.findList("ShipmentItemDetail", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipment.get("shipmentId"))), null, null, null, false);
    				if(!UtilValidate.isEmpty(listShipmentItems)){
    					for(GenericValue item : listShipmentItems){
    						Map<String, Object> row = new HashMap<String, Object>();
    						row.put("deliveryEntryId", deliveryEntryId);
    						row.put("shipmentId", item.getString("shipmentId"));
    						row.put("shipmentItemSeqId", item.getString("shipmentItemSeqId"));
    						row.put("productId", item.getString("productId"));
    						row.put("productName", item.getString("productName"));
    						row.put("quantity", item.get("quantity"));
    						row.put("weight", item.get("weight"));
    						row.put("weightUomId", (String)item.get("weightUomId"));
    						row.put("quantityUomId", (String)item.get("quantityUomId"));
    						
    						List<GenericValue> listItemHadInVehicles = delegator.findList("DeliveryEntryVehicleItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "shipmentId",  shipment.get("shipmentId"), "shipmentItemSeqId", item.getString("shipmentItemSeqId"))), null, null, null, false);
    						BigDecimal quantityTmp = BigDecimal.ZERO;
    						if (!listItemHadInVehicles.isEmpty()){
    							for (GenericValue itemTmp : listItemHadInVehicles){
    								quantityTmp = quantityTmp.add(itemTmp.getBigDecimal("quantity"));
    							}
    						}
    						row.put("quantityFree", item.getBigDecimal("quantity").subtract(quantityTmp));
    						listIterator.add(row);
    					}
    				}
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
	public static Map<String, Object> createDeliveryEntryVehicle(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String deliveryEntryId = (String)context.get("deliveryEntryId");
		String statusId = (String)context.get("statusId");
		String deliveryManId = (String)context.get("deliveryManId");
		String driverId = (String)context.get("driverId");
		String vehicleId = (String)context.get("vehicleId");
		List<Map<String, String>> listShipmentItems = (List<Map<String, String>>)context.get("listShipmentItems");
		Locale locale = (Locale)context.get("locale");
		GenericValue deliveryEntry = null;
		try {
			deliveryEntry = delegator.findOne("DeliveryEntry", false, UtilMisc.toMap("deliveryEntryId", deliveryEntryId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (deliveryEntry == null) {
			return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "DeliveryEntryNotFound", locale));
		}
		if (listShipmentItems.isEmpty()){
			return ServiceUtil.returnError(UtilProperties.getMessage(ApprUiLabels, "NoShipmentItemFound", locale));
		}
		String deliveryWeightUomId = deliveryEntry.getString("weightUomId");
		GenericValue deVehicle = delegator.makeValue("DeliveryEntryVehicle");
		deVehicle.put("deliveryEntryId", deliveryEntryId);
		deVehicle.put("vehicleId", vehicleId);
		deVehicle.put("statusId", statusId);
		deVehicle.put("fromDate", UtilDateTime.nowTimestamp());
		try {
			delegator.create(deVehicle);
			
			GenericValue deVehicleRoleDeMan = delegator.makeValue("DeliveryEntryVehicleRole");
			deVehicleRoleDeMan.put("deliveryEntryId", deliveryEntryId);
			deVehicleRoleDeMan.put("vehicleId", vehicleId);
			deVehicleRoleDeMan.put("partyId", deliveryManId);
			deVehicleRoleDeMan.put("roleTypeId", "DELIVERY_MAN");
			delegator.create(deVehicleRoleDeMan);
			
			GenericValue deVehicleRoleDriver = delegator.makeValue("DeliveryEntryVehicleRole");
			deVehicleRoleDriver.put("deliveryEntryId", deliveryEntryId);
			deVehicleRoleDriver.put("vehicleId", vehicleId);
			deVehicleRoleDriver.put("partyId", driverId);
			deVehicleRoleDriver.put("roleTypeId", "DRIVER");
			delegator.create(deVehicleRoleDriver);
			
			BigDecimal weight = BigDecimal.ZERO;
			for (Map<String, String> item : listShipmentItems){
				GenericValue vehicleItem = delegator.makeValue("DeliveryEntryVehicleItem");
				vehicleItem.put("deliveryEntryId", deliveryEntryId);
				vehicleItem.put("vehicleId", vehicleId);
				vehicleItem.put("shipmentId", item.get("shipmentId"));
				vehicleItem.put("shipmentItemSeqId", item.get("shipmentItemSeqId"));
				vehicleItem.put("quantity", new BigDecimal(item.get("quantity")));
				BigDecimal quantityItem = new BigDecimal(item.get("quantity"));
				delegator.create(vehicleItem);
				String productWeightUomId = item.get("weightUomId");
				BigDecimal weightItem = new BigDecimal(item.get("weight"));
				if (!deliveryWeightUomId.equals(productWeightUomId)){
	    			GenericValue conversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", productWeightUomId, "uomIdTo", deliveryWeightUomId));
	    			weightItem = weightItem.multiply(conversion.getBigDecimal("conversionFactor"));
	    		}
				weight = weight.add(weightItem.multiply(quantityItem));
			}
			deVehicle.put("weight", weight);
			deVehicle.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> createMultiDelivery(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String tmp = (String) context.get("listDeliverys");
		JSONArray listDeliverys = JSONArray.fromObject(tmp);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		for (int i = 0; i < listDeliverys.size(); i++) {
			JSONObject item = listDeliverys.getJSONObject(i);
			String listExportItems = item.getString("listExportItems");
			String deliveryTypeId = item.getString("deliveryTypeId");
			String partyIdFrom = item.getString("partyIdFrom");
			String orderId = item.getString("orderId");
			String statusId = item.getString("statusId");
			String deliveryDateString = item.getString("deliveryDate");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
			Timestamp deliveryDate = null;
		    try {
				java.util.Date parsedTimeStamp = dateFormat.parse(deliveryDateString);
				deliveryDate = new Timestamp(parsedTimeStamp.getTime());
			} catch (ParseException e1) {
				ServiceUtil.returnError(e1.toString());
			}
			String originFacilityId = item.getString("originFacilityId");
			String originContactMechId = item.getString("originContactMechId");
			String noNumber = item.getString("noNumber");
			
			JSONArray listItemTmp = JSONArray.fromObject(listExportItems);
			List<Map<String, String>> listOrderItems = new ArrayList<Map<String, String>>();
			for (int j = 0; j < listItemTmp.size(); j++){
				Map<String, String> mapItem = new HashMap<String, String>();
				JSONObject orderItem = listItemTmp.getJSONObject(j);
				String orderItemSeqId = orderItem.getString("orderItemSeqId");
				String actualExportedQuantity = null;
				if (orderItem.containsKey("actualExportedQuantity")){
					actualExportedQuantity = orderItem.getString("actualExportedQuantity");
				}
				String actualExpireDate = orderItem.getString("actualExpireDate");
				String actualManufacturedDate = orderItem.getString("actualManufacturedDate");
				String quantity = orderItem.getString("quantity");
				String itemStatusId = orderItem.getString("statusId");
				
				mapItem.put("orderId", orderId);
				mapItem.put("orderItemSeqId", orderItemSeqId);
				mapItem.put("actualExportedQuantity", actualExportedQuantity);
				mapItem.put("actualExpireDate", actualExpireDate);
				mapItem.put("actualManufacturedDate", actualManufacturedDate);
				mapItem.put("quantity", quantity);
				mapItem.put("statusId", itemStatusId);
				listOrderItems.add(mapItem);
			}
			Map<String, Object> mapContext = new HashMap<String, Object>();
			mapContext.put("listOrderItems", listOrderItems);
			mapContext.put("deliveryTypeId", deliveryTypeId);
			mapContext.put("partyIdFrom", partyIdFrom);
			mapContext.put("orderId", orderId);
			mapContext.put("statusId", statusId);
			mapContext.put("deliveryDate", deliveryDate);
			mapContext.put("originFacilityId", originFacilityId);
			mapContext.put("originContactMechId", originContactMechId);
			mapContext.put("no", noNumber);
			mapContext.put("userLogin", userLogin);
			try {
				dispatcher.runSync("createDelivery", mapContext);
			} catch (GenericServiceException e) {
				ServiceUtil.returnError("service createMultiDelivery error!");
			}
		}
		return result;
	}
	
	public static Map<String, Object> receiveInventoryFromDelivery(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String deliveryId = (String)context.get("deliveryId");
		String invStatusId = (String)context.get("inventoryStatusId");
		GenericValue delivery = null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("Delivery not Found");
		}
		String deliveryTypeId = null;
		
		if (delivery != null){
			deliveryTypeId = (String)delivery.get("deliveryTypeId");
			List<GenericValue> listItem = new ArrayList<GenericValue>();
			// Create shipment from delivery
			Map<String, Object> shipmentParam = FastMap.newInstance();
			shipmentParam.put("deliveryId", deliveryId);
			shipmentParam.put("userLogin", (GenericValue)context.get("userLogin"));
			Map<String, Object> mapShipment = FastMap.newInstance();
			try {
				mapShipment = dispatcher.runSync("createShipmentForPurchaseDelivery", shipmentParam);
			} catch (GenericServiceException e1) {
				ServiceUtil.returnError("createShipmentForPurchaseDelivery Error!");
			}
			String shipmentId = (String)mapShipment.get("shipmentId");
			try {
				List<GenericValue> listShipmentItem = delegator.findList("ShipmentItem", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
				delivery.put("shipmentId", shipmentId);
				delivery.store();
				listItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
				if (!listItem.isEmpty()){
					for (GenericValue item : listItem){
						GenericValue orderItem = delegator.getRelatedOne("OrderItem", item, false);
						String orderItemSeqId = (String)orderItem.get("orderItemSeqId");
						// receive inventory
						String inventoryItemId = null;
						Map<String, Object> mapInv = new FastMap<String, Object>();
						mapInv.put("productId", (String)orderItem.get("productId"));
						mapInv.put("orderId", (String)orderItem.get("orderId"));
						mapInv.put("orderItemSeqId", orderItemSeqId);
						mapInv.put("facilityId", (String)delivery.get("destFacilityId"));
						mapInv.put("shipmentId", shipmentId);
						mapInv.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						if (item.get("actualExpireDate") != null){
							mapInv.put("expireDate", item.get("actualExpireDate"));
						} else {
							mapInv.put("expireDate", orderItem.get("expireDate"));
						}
						if (item.get("actualManufacturedDate") != null){
							mapInv.put("datetimeManufactured", item.get("actualManufacturedDate"));
						} else {
							mapInv.put("datetimeManufactured", orderItem.get("datetimeManufactured"));
						}
						mapInv.put("statusId", invStatusId);
						mapInv.put("datetimeReceived", delivery.get("deliveryDate"));
						mapInv.put("unitCost", orderItem.get("unitPrice"));
						BigDecimal convertNumber = BigDecimal.ONE;
						try {
							Map<String, Object> mapTmp = dispatcher.runSync("getQuantityUomBySupplier", UtilMisc.toMap("orderId",(String)orderItem.get("orderId"), "productId", (String)orderItem.get("productId")));
							String quantityUomTemp = (String)mapTmp.get("quantityUomId");
							GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)orderItem.get("productId")));
							Map<String, Object> mapTmp2 = dispatcher.runSync("getProductConvertNumber", UtilMisc.toMap("productId",(String)orderItem.get("productId"), "quantityUomId", quantityUomTemp, "baseQuantityUomId", product.get("quantityUomId")));
							convertNumber = (BigDecimal)mapTmp2.get("convertNumber");
						} catch (GenericServiceException e1) {
							ServiceUtil.returnError("getQuantityUomBySupplier error!");
						}
						mapInv.put("quantityAccepted", item.getBigDecimal("actualDeliveredQuantity").multiply(convertNumber));
						mapInv.put("quantityExcess", BigDecimal.ZERO);
						mapInv.put("quantityRejected", BigDecimal.ZERO);
						mapInv.put("quantityQualityAssurance", BigDecimal.ZERO);
						mapInv.put("userLogin", userLogin);
						
						Timestamp expireDate = (Timestamp)orderItem.get("expireDate");
						BigDecimal quantityReceived = item.getBigDecimal("actualDeliveredQuantity");
						Date expDate = new Date(expireDate.getTime());
						Calendar cal = Calendar.getInstance();
					    cal.setTime(expDate);
					    int year = cal.get(Calendar.YEAR);
					    int month = cal.get(Calendar.MONTH);
					    int day = cal.get(Calendar.DAY_OF_MONTH);
						
						try {
							Map<String, Object> mapInvReturn = dispatcher.runSync("receiveInventoryProduct", mapInv);
							inventoryItemId = (String)mapInvReturn.get("inventoryItemId");
						} catch (GenericServiceException e) {
							ServiceUtil.returnError("Service receiveInventoryProduct error!");
						}
						if ("DELIVERY_PURCHASE".equals(deliveryTypeId)){
							String orderId = (String)orderItem.get("orderId");
							List<EntityCondition> listAllConditions  = new ArrayList<EntityCondition>();
							EntityCondition typeCondition = EntityCondition.makeCondition("deliveryTypeId", EntityJoinOperator.NOT_EQUAL, "DELIVERY_PURCHASE");
							listAllConditions.add(typeCondition);
							Map<String, String> mapCondition = new HashMap<String, String>();
					    	mapCondition.put("orderId", orderId);
					    	EntityCondition tmpConditonPre = EntityCondition.makeCondition(mapCondition);
					    	listAllConditions.add(tmpConditonPre);
					    	
					    	mapCondition = new HashMap<String, String>();
					    	mapCondition.put("statusId", "DLV_CREATED");
					    	EntityCondition statusCondition = EntityCondition.makeCondition(mapCondition);
					    	listAllConditions.add(statusCondition);
					    	
							List<GenericValue> listDeliveries = delegator.findList("Delivery", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, false);
							if (!listDeliveries.isEmpty()){
								for (GenericValue dlv : listDeliveries){
									List<GenericValue> listDlvItems = new ArrayList<GenericValue>();
									listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", (String)dlv.get("deliveryId"))), null, null, null, false);
									if (!listDlvItems.isEmpty()){
										for (GenericValue dlvItem : listDlvItems){
											BigDecimal quantityToExport = (dlvItem.getBigDecimal("quantity").subtract(dlvItem.getBigDecimal("actualExportedQuantity")));
											Timestamp actualExpireDate = (Timestamp)dlvItem.get("actualExpireDate");
											Date actualExpDate = new Date(actualExpireDate.getTime());
											Calendar calActual = Calendar.getInstance();
											calActual.setTime(actualExpDate);
										    int actualYear = calActual.get(Calendar.YEAR);
										    int actualMonth = calActual.get(Calendar.MONTH);
										    int actualDay = calActual.get(Calendar.DAY_OF_MONTH);
											if ("DELI_ITEM_CREATED".equals((String)dlvItem.get("statusId")) && orderItemSeqId.equals((String)dlvItem.get("fromOrderItemSeqId")) && year == actualYear && month == actualMonth && day == actualDay && quantityToExport.compareTo(BigDecimal.ZERO) == 1){
												if (quantityReceived.compareTo(quantityToExport) >= 0){
													Map<String, Object> mapPhysical = new HashMap<String, Object>();
													mapPhysical.put("inventoryItemId", inventoryItemId);
													mapPhysical.put("availableToPromiseVar", quantityToExport.negate());
													mapPhysical.put("quantityOnHandVar", quantityToExport.negate());
													mapPhysical.put("userLogin", userLogin);
													try {
														dispatcher.runSync("createPhysicalInventoryAndVariance", mapPhysical);
													} catch (GenericServiceException e) {
														ServiceUtil.returnError(e.getMessage());
													}
													dlvItem.put("actualExportedQuantity", quantityToExport);
													dlvItem.put("statusId", "DELI_ITEM_EXPORTED");
													delegator.store(dlvItem);
												} else {
													Map<String, Object> mapPhysical = new HashMap<String, Object>();
													mapPhysical.put("inventoryItemId", inventoryItemId);
													mapPhysical.put("availableToPromiseVar", quantityReceived.negate());
													mapPhysical.put("quantityOnHandVar", quantityReceived.negate());
													mapPhysical.put("userLogin", userLogin);
													try {
														dispatcher.runSync("createPhysicalInventoryAndVariance", mapPhysical);
													} catch (GenericServiceException e) {
														ServiceUtil.returnError(e.getMessage());
													}
													dlvItem.put("actualExportedQuantity", quantityReceived);
													delegator.store(dlvItem);
												}
											}
										}
									}
									listDlvItems = new ArrayList<GenericValue>(); 
									listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", (String)dlv.get("deliveryId"))), null, null, null, false);
									Boolean test = true;
									for (GenericValue dlvItem : listDlvItems){
										if (!"DELI_ITEM_EXPORTED".equals((String)dlvItem.get("statusId"))){
											test = false;
											break;
										}
									}
									if (test){
										dlv.put("statusId", "DLV_EXPORTED");
										delegator.store(dlv);
									}
								}
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				ServiceUtil.returnError("No Item found with delivery" + deliveryId);
			}
		}
		List<String> listAccAdminGroup = SecurityUtil.getPartiesByRoles("DELYS_ACCOUNTANTS", delegator);
		for (String group : listAccAdminGroup){
			try {
				List<GenericValue> listRelations = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", "DELYS_ACCOUNTANTS")), null, null, null, false);
				listRelations = EntityUtil.filterByDate(listRelations);
				List<String> listAccAdmins = new ArrayList<String>();
				if (!listRelations.isEmpty()){
					for (GenericValue relation : listRelations){
						listAccAdmins.add(relation.getString("partyIdFrom"));
					}
				}
				if (!listAccAdmins.isEmpty()){
					for (String partyId : listAccAdmins){
						Map<String, Object> mapNotify = new HashMap<String, Object>();
						String targetLink = "deliveryId="+deliveryId;
						String header = UtilProperties.getMessage(resource, "NewOrderHadBeenDelivered", (Locale)context.get("locale"));
						mapNotify.put("partyId", partyId);
						mapNotify.put("action", "getDetailPurchaseDelivery");
						mapNotify.put("targetLink", targetLink);
						mapNotify.put("header", header);
						mapNotify.put("userLogin", (GenericValue)context.get("userLogin"));
						try {
							dispatcher.runSync("createNotification", mapNotify);
						} catch (GenericServiceException e) {
							ServiceUtil.returnError(UtilProperties.getMessage(resource, "CreateNotificationError", (Locale)context.get("locale")));
						}
					}
				}
			} catch (GenericEntityException e) {
				ServiceUtil.returnError("get PartyRelationShip error!");
			}
		}
		result.put("deliveryTypeId", deliveryTypeId);
		result.put("deliveryId", deliveryId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDetailPurchaseDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String deliveryId = null;
		if (parameters.get("deliveryId") != null && parameters.get("deliveryId").length >= 0){
			 deliveryId = (String)parameters.get("deliveryId")[0];
		}
    	if (deliveryId != null && !"".equals(deliveryId)){
    		mapCondition.put("deliveryId", deliveryId);
    	} else {
    		ServiceUtil.returnError("DeliveryId not found!");
    	}
		mapCondition.put("deliveryId", deliveryId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
		List<GenericValue> listDeliveryItems = new ArrayList<GenericValue>();
    	try {
    		listDeliveryItems = delegator.findList("DeliveryItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    	} catch (GenericEntityException e) {
			ServiceUtil.returnError("Error when get list DeliveryItem!");
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listDeliveryItems);
    	return successResult;
	}
	public static Map<String, Object> getProductConvertNumber(DispatchContext ctx, Map<String, ?> context){
		Delegator delegator = ctx.getDelegator();
		String productId = (String)context.get("productId");
		String uomFromId = (String)context.get("quantityUomId");
		String uomToId = (String)context.get("baseQuantityUomId");
		BigDecimal convertNumber = BigDecimal.ONE;
		convertNumber = ProductUtil.getConvertPackingNumber(delegator, productId, uomFromId, uomToId);
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("convertNumber", convertNumber);
		return mapResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNoteOfPurchaseDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String deliveryId = null;
		if (parameters.get("deliveryId") != null && parameters.get("deliveryId").length >= 0){
			 deliveryId = (String)parameters.get("deliveryId")[0];
		}
    	if (deliveryId == null || "".equals(deliveryId)){
    		ServiceUtil.returnError("DeliveryId not found!");
    	}
    	GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e1) {
			ServiceUtil.returnError("Delivery Not Found!");
		}
		String orderId = (String)delivery.get("orderId");
		mapCondition.put("fromOrderId", orderId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	List<String> listTypes = new ArrayList<String>();
		listTypes.add("DELIVERY_QA");
		listTypes.add("DELIVERY_GIFT");
		EntityCondition typeCondition = EntityCondition.makeCondition("deliveryTypeId", EntityJoinOperator.IN, listTypes);
		listAllConditions.add(typeCondition);
		List<GenericValue> listDeliveryItems = new ArrayList<GenericValue>();
    	try {
    		listDeliveryItems = delegator.findList("DeliveryItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    	} catch (GenericEntityException e) {
			ServiceUtil.returnError("Error when get list DeliveryItem!");
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listDeliveryItems);
    	return successResult;
	}
	public static Map<String, Object> updateDeliveryPathScanFile(DispatchContext ctx, Map<String, ?> context){
		String deliveryId = (String)context.get("deliveryId");
		String pathScanFile = (String)context.get("pathScanFile");
		Delegator delegator = ctx.getDelegator();
		GenericValue delivey = null;
		try {
			delivey = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			delivey.put("pathScanFile", pathScanFile);
			delegator.store(delivey);
		} catch (GenericEntityException e){
			ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("deliveryId", deliveryId);
		return mapResult;
	}
	
	public static Map<String, Object> getProductDeliveryWeight(DispatchContext ctx, Map<String, ?> context){
		String deliveryId = (String)context.get("deliveryId");
		String deliveryItemSeqId = (String)context.get("deliveryItemSeqId");
		Delegator delegator = ctx.getDelegator();
		GenericValue deliveyItem = null;
		BigDecimal weight = BigDecimal.ZERO;
		String defaultWeightUomId = null;
		try {
			deliveyItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
			BigDecimal actualExportedQty = deliveyItem.getBigDecimal("actualExportedQuantity");
			GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			
			String deliveryTypeId = delivery.getString("deliveryTypeId");
			GenericValue product = null;
			if ("DELIVERY_SALES".equals(deliveryTypeId)){
				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", deliveyItem.getString("fromOrderId"), "orderItemSeqId", deliveyItem.getString("fromOrderItemSeqId")));
				product = delegator.findOne("Product", false, UtilMisc.toMap("productId", orderItem.getString("productId")));
			} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
				GenericValue transferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", deliveyItem.getString("fromTransferId"), "transferItemSeqId", deliveyItem.getString("fromTransferItemSeqId")));
				product = delegator.findOne("Product", false, UtilMisc.toMap("productId", transferItem.getString("productId")));
			}
			if (product != null && product.getBigDecimal("weight") != null){
				weight = actualExportedQty.multiply(product.getBigDecimal("weight"));
			}
			defaultWeightUomId = delivery.getString("defaultWeightUomId");
			if (defaultWeightUomId == null){
				defaultWeightUomId = product.getString("weightUomId");
			}
		} catch (GenericEntityException e){
			ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("weight", weight);
		mapResult.put("weightUomId", defaultWeightUomId);
		return mapResult;
	}
	
	public static Map<String, Object> updateRequirementFromDelivery(DispatchContext ctx, Map<String, ?> context){
		String deliveryId = (String)context.get("deliveryId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			String orderId = delivery.getString("orderId");
			List<GenericValue> listOrderReqs = delegator.findList("OrderRequirement", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if (!listOrderReqs.isEmpty()){
				for (GenericValue orderReq : listOrderReqs){
					String requirementId = orderReq.getString("requirementId");
					GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
					requirement.put("statusId", statusId);
					delegator.store(requirement);
				}
			}
		} catch (GenericEntityException e){
			ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> mapResult = new HashMap<String, Object>();
		return mapResult;
	}
	
}