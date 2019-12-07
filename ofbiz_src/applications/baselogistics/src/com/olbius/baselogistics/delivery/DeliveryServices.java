package com.olbius.baselogistics.delivery;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.config.model.DelegatorElement;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.*;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.baselogistics.util.GeoUtil;
import com.olbius.baselogistics.util.JsonUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.InventoryUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;

import javax.swing.text.html.parser.Entity;

public class DeliveryServices {
	
	public static final String module = DeliveryServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceCommonEntity = "CommonEntityLabels";
	public static final String OrderEntityLabels = "OrderEntityLabels";
    public static final String resourceError = "BaseLogisticsErrorUiLabels";
    public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
	public static enum DeliveryTypeEnum {
		DELIVERY_SALES,
		DELIVERY_PURCHASE,
		DELIVERY_TRANSFER
	}

	public static enum ShippingTripPackStatusEnum {
		SHIP_PACK_CREATED,
		SHIP_PACK_COMPLETED,
		SHIP_PACK_CANCELLED,
		SHIP_CANCELLED
	}

	public static enum DeliveryStatusEnum {
		DLV_APPROVED,
		DLV_CANCELLED,
		DLV_COMPLETED,
		DLV_CONFIRMED,
		DLV_CREATED,
		DLV_DELIVERED,
		DLV_EXPORTED,
		DLV_PROPOSED
	}

	public static enum PackStatusEnum {
		PACK_APPROVED,
		PACK_BEING_DLIED,
		PACK_CANCELLED,
		PACK_CREATED,
		PACK_DELIVERED,
		PACK_OUT_TRIP
	}
	public static enum TripStatusEnum {
		TRIP_CANCELLED,
		TRIP_COMPLETED,
		TRIP_CONFIRMED,
		TRIP_CREATED,
		TRIP_EXPORTED
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<Map<String, Object>> listDeliveries = new ArrayList<>();
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String deliveryTypeId = null;
    	String statusId = null;
    	if (parameters.get("statusId") != null && parameters.get("statusId").length > 0){
    		statusId = (String)parameters.get("statusId")[0];
    	}
    	if (statusId != null && !"".equals(statusId) && "DLV_EXPORTED".equals(statusId)){
    		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
    		tmpListCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DLV_EXPORTED"));
    		tmpListCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DLV_DELIVERED"));
    		EntityCondition statusCondition = EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.OR);
        	listAllConditions.add(statusCondition);
    	}
    	if (SalesPartyUtil.isDistributor(delegator, (String) userLogin.get("userLoginId"))){
			EntityCondition statusCondition = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DLV_APPROVED");	
			listAllConditions.add(statusCondition);	
		};
    	mapCondition = new HashMap<String, String>();
    	if (parameters.get("deliveryTypeId") != null && parameters.get("deliveryTypeId").length > 0){
    		deliveryTypeId = (String)parameters.get("deliveryTypeId")[0];
    	}
    	if (deliveryTypeId != null && !"".equals(deliveryTypeId)){
    		mapCondition.put("deliveryTypeId", deliveryTypeId);
    	}
    	String partyIdTo = null;
    	if (parameters.get("partyIdTo") != null && parameters.get("partyIdTo").length > 0){
    		partyIdTo = (String)parameters.get("partyIdTo")[0];
    	}
    	if (UtilValidate.isNotEmpty(partyIdTo)){
    		mapCondition.put("partyIdTo", partyIdTo);
    	}
    	
    	String fromOrderId = null;
		if (parameters.containsKey("fromOrderId") && parameters.get("fromOrderId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("fromOrderId"))) {
				fromOrderId = parameters.get("fromOrderId")[0];
			}
		}
		if (UtilValidate.isNotEmpty(fromOrderId)) {
			mapCondition.put("orderId", fromOrderId);
		}
    	
    	EntityCondition typeConditon = EntityCondition.makeCondition(mapCondition);
    	
    	listAllConditions.add(typeConditon);
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String products = null;
		if (parameters.containsKey("products") && parameters.get("products").length > 0) {
			products = parameters.get("products")[0];
		}
		List<String> listProductIds = new ArrayList<String>();
		if (UtilValidate.isNotEmpty(products)) {
			List<Map<String, Object>> listProducts;
			try {
				listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", products);
				if (!listProducts.isEmpty()){
					for (Map<String, Object> map : listProducts) {
						if (!listProductIds.contains((String)map.get("productId"))){
							listProductIds.add((String)map.get("productId"));
						}
					}
				}
			} catch (ParseException e) {
				return ServiceUtil.returnError("OLBIUS: convert list json to list object error! " + e.toString());
			}
		}
		if (!listProductIds.isEmpty()){
			EntityCondition cond1 = EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds);
			EntityCondition cond2 = EntityCondition.makeCondition("deliveryTypeId", EntityOperator.EQUALS, deliveryTypeId);
			EntityCondition cond3 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED");
			List<EntityCondition> conds = new ArrayList<EntityCondition>();
			conds.add(cond1);
			conds.add(cond2);
			conds.add(cond3);
			
			List<GenericValue> listDlvItems = new ArrayList<GenericValue>();
			try {
				listDlvItems = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: get DeliveryAndItems error! " + e.toString());
			}
			if (!listDlvItems.isEmpty()) {
				List<String> dlvIds = EntityUtil.getFieldListFromEntityList(listDlvItems, "deliveryId", true);
				listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.IN, dlvIds));
			} else {
				successResult.put("listIterator", listDeliveries);
				return successResult;
			}
		}
		List<GenericValue> listDeliveryTmps = new ArrayList<GenericValue>();
    	try {
    		if (listSortFields.isEmpty()){
    			listSortFields.add("-deliveryId");
    		}
    		listDeliveryTmps = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "DeliveryDetail", 
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    		if (UtilValidate.isNotEmpty(listDeliveryTmps)) {
    			if (!listDeliveryTmps.isEmpty()){
    				for (GenericValue item : listDeliveryTmps) {
    		    		Map<String, Object> row = new HashMap<String, Object>();
    		    		List<GenericValue> listRowDetails = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", item.getString("deliveryId"))), null, null, null, false);
    		    		row.putAll(item);
    					List<Map<String, Object>> listToResult = FastList.newInstance();
    					for (GenericValue dlvItem : listRowDetails){
    						Map<String, Object> mapTmp = FastMap.newInstance();
    						mapTmp.put("deliveryId", dlvItem.getString("deliveryId"));
    						mapTmp.put("deliveryItemSeqId", dlvItem.getString("deliveryItemSeqId"));
    						mapTmp.put("fromOrderItemSeqId", dlvItem.getString("fromOrderItemSeqId"));
    						mapTmp.put("fromOrderId", dlvItem.getString("fromOrderId"));
    						mapTmp.put("productId", dlvItem.getString("productId"));
    						mapTmp.put("productCode", dlvItem.getString("productCode"));
    						mapTmp.put("productName", dlvItem.getString("productName"));
    						mapTmp.put("quantityUomId", dlvItem.getString("quantityUomId"));
    						mapTmp.put("actualExportedQuantity", dlvItem.getBigDecimal("actualExportedQuantity"));
    						mapTmp.put("actualDeliveredQuantity", dlvItem.getBigDecimal("actualDeliveredQuantity"));
    						mapTmp.put("statusId", dlvItem.getString("statusId"));
    						mapTmp.put("isPromo", dlvItem.getString("isPromo"));
    						mapTmp.put("batch", dlvItem.getString("batch"));
    						mapTmp.put("quantity", dlvItem.getBigDecimal("quantity"));
    						
    						mapTmp.put("actualExpireDate", dlvItem.getTimestamp("actualExpireDate"));
    						mapTmp.put("actualManufacturedDate", dlvItem.getTimestamp("actualManufacturedDate"));
    						mapTmp.put("expireDate", dlvItem.getTimestamp("expireDate"));
    						mapTmp.put("inventoryItemId", dlvItem.getString("inventoryItemId"));
    						mapTmp.put("deliveryStatusId", dlvItem.getString("deliveryStatusId"));
    						mapTmp.put("invoiceId", dlvItem.get("invoiceId"));
    						listToResult.add(mapTmp);
    					}
    					row.put("rowDetail", listToResult);
    					listDeliveries.add(row);
    				}
    			}
			}
	    	successResult.put("listIterator", listDeliveries);
    	} catch (GenericEntityException e){
    		return ServiceUtil.returnError("OLBIUS: error get list delivery");
    	}
    	return successResult;
    }
	
	public static Map<String, Object> loadGeoAssocListByGeoId(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String geoId = (String)context.get("geoId");
		Delegator delegator = ctx.getDelegator();
		List<Map<String, String>> listGeoAssocMap = GeoUtil.loadGeoAssocListByGeoId(delegator, geoId);
		result.put("listGeoAssocMap", listGeoAssocMap);
		return result;
	}
	
	public static boolean numberOrNot(String input){
        try {
            Long.parseLong(input);
        } catch(NumberFormatException ex){
            return false;
        }
        return true;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String,Object> updateDeliveryItemList(DispatchContext ctx, Map<String, Object> context){
	    Locale locale = (Locale)context.get("locale");
	    List<Map<String, Object>> listDeliveryItems = null;
	    String pathScanFile = (String)context.get("pathScanFile");
	    String pathScanFileExpt = (String)context.get("pathScanFileExpt");
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
				return ServiceUtil.returnError("Delivery not found!");
			}
	    }
	    String orderId = delivery.getString("orderId");
	    if (UtilValidate.isNotEmpty(pathScanFile)){
	    	delivery.put("pathScanFile", pathScanFile);
	    }
	    if (UtilValidate.isNotEmpty(pathScanFileExpt)){
	    	delivery.put("pathScanFileExpt", pathScanFileExpt);
	    }
	    if (actualStartDate != null && deliveryId != null){
    		delivery.put("actualStartDate", new Timestamp(actualStartDate));
	    }
	    if (actualArrivalDate != null && deliveryId != null){
	    	delivery.put("actualArrivalDate", new Timestamp(actualArrivalDate));
	    }
	    try {
			delegator.store(delivery);
			if (delivery.getString("shipmentId") != null && delivery.get("actualStartDate") != null){
				GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", delivery.getString("shipmentId")));
				shipment.put("estimatedShipDate", delivery.get("actualStartDate"));
				delegator.store(shipment);
			}
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError("Store delivery error!");
		}
	    
	    for (Map<String, Object> item : listDeliveryItems) {
			String deliveryItemSeqId = null;
	    	if (UtilValidate.isEmpty(item.get("deliveryItemSeqId"))) {
				GenericValue newDeliveryItem = delegator.makeValue("DeliveryItem"); 
				newDeliveryItem.put("deliveryId", deliveryId);
				delegator.setNextSubSeqId(newDeliveryItem, "deliveryItemSeqId", 5, 1);
				newDeliveryItem.put("fromOrderId", orderId);
				newDeliveryItem.put("fromTransferId", delivery.get("transferId"));
				newDeliveryItem.put("fromOrderItemSeqId", item.get("fromOrderItemSeqId"));
				newDeliveryItem.put("fromTransferItemSeqId", item.get("fromTransferItemSeqId"));
				newDeliveryItem.set("inventoryItemId", item.get("inventoryItemId"));
				newDeliveryItem.put("statusId", "DELI_ITEM_APPROVED");
				newDeliveryItem.put("actualExportedQuantity", BigDecimal.ZERO);
				
				GenericValue oneOfOldDlvItem = null;
				if ("DELIVERY_SALES".equals(delivery.getString("deliveryTypeId"))){
					List<GenericValue> listTmps = FastList.newInstance();
					try {
						listTmps = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", orderId, "fromOrderItemSeqId", item.get("fromOrderItemSeqId"))), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listTmps.isEmpty()) oneOfOldDlvItem = listTmps.get(0);
				} else if ("DELIVERY_TRANSFER".equals(delivery.getString("deliveryTypeId"))){
					List<GenericValue> listTmps = FastList.newInstance();
					try {
						listTmps = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", delivery.get("transferId"), "fromTransferItemSeqId", item.get("fromTransferItemSeqId"))), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listTmps.isEmpty()) oneOfOldDlvItem = listTmps.get(0);
				}
				if (UtilValidate.isNotEmpty(oneOfOldDlvItem)) {
					newDeliveryItem.put("quantity", oneOfOldDlvItem.getBigDecimal("quantity"));
					newDeliveryItem.put("amount", oneOfOldDlvItem.getBigDecimal("amount"));
					try {
						delegator.createOrStore(newDeliveryItem);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when runSync updateDeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					deliveryItemSeqId = newDeliveryItem.getString("deliveryItemSeqId");
				}
			} else {
				deliveryItemSeqId = (String)item.get("deliveryItemSeqId");
			}
	    	item.put("deliveryItemSeqId", deliveryItemSeqId);
	    }
    	Map<String, Object> tmpMap;
 	    for (Map<String, Object> item : listDeliveryItems) {
	    	if (UtilValidate.isNotEmpty(item.get("deliveryItemSeqId"))) {
	    		String deliveryItemSeqId = (String)item.get("deliveryItemSeqId");
		        tmpMap = new HashMap<String, Object>();
		        tmpMap.put("fromOrderId", item.get("fromOrderId"));
		        tmpMap.put("fromOrderItemSeqId", item.get("fromOrderItemSeqId"));
		        tmpMap.put("fromTransferId", item.get("fromTransferId"));
		        tmpMap.put("fromTransferItemSeqId", item.get("fromTransferItemSeqId"));
		        tmpMap.put("inventoryItemId", item.get("inventoryItemId"));
		        tmpMap.put("deliveryId", item.get("deliveryId"));
		        tmpMap.put("deliveryItemSeqId", deliveryItemSeqId);
		        if (UtilValidate.isNotEmpty(item.get("actualExportedQuantity")) && !"null".equals(item.get("actualExportedQuantity").toString())){
		        	BigDecimal tmpBD = new BigDecimal(item.get("actualExportedQuantity").toString());
		        	tmpMap.put("actualExportedQuantity", tmpBD);
		        } 
		        if (UtilValidate.isNotEmpty(item.get("actualDeliveredQuantity")) && !"null".equals(item.get("actualDeliveredQuantity").toString())){
		        	BigDecimal tmpBD = new BigDecimal(item.get("actualDeliveredQuantity").toString());
			        tmpMap.put("actualDeliveredQuantity", tmpBD);
		        } 
		        
		        tmpMap.put("userLogin", (GenericValue)context.get("userLogin"));
		        LocalDispatcher dispatcher = ctx.getDispatcher();
		        try {
					Map<String, Object> mapReturn = dispatcher.runSync("updateDeliveryItem", tmpMap);
					if (ServiceUtil.isError(mapReturn)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
					}
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when runSync updateDeliveryItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
	    	}
	    }
	    Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "UpdateSuccessfully", locale));
	    if (UtilValidate.isNotEmpty(orderId)){
	    	String statusId = delivery.getString("statusId");
	    	if ("DLV_APPROVED".equals(statusId)){
		    	String roles = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.receiveMesg.customer");
		    	String[] arrRoles;
		    	arrRoles = roles.split(";");
		    	List<String> listRoles = Arrays.asList(arrRoles); 
		    	if (!listRoles.isEmpty()){
		    		List<GenericValue> listPartyTos = FastList.newInstance();
		    		List<GenericValue> listPartyFroms = FastList.newInstance();
		    		List<GenericValue> listPartyRelations = FastList.newInstance();
		    		try {
			    	listPartyTos = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
			    	listPartyFroms = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
			    	listPartyRelations = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", listPartyFroms.get(0).getString("partyId"), "partyIdFrom", listPartyTos.get(0).getString("partyId"), "roleTypeIdTo", "INTERNAL_ORGANIZATIO")), EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, listRoles))), null, null, null, false);
		    		} catch (GenericEntityException e){
		    			String errMsg = "OLBIUS: Fatal error when runSync updateDeliveryItemList: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
		    		}
			    	if (!listPartyRelations.isEmpty()){
			    		LocalDispatcher dispatcher = ctx.getDispatcher();
			    		String partyId = listPartyTos.get(0).getString("partyId");
						String header = UtilProperties.getMessage(resource, "HasNewShipmentWillBeTransferring", (Locale)context.get("locale")) + ". " + UtilProperties.getMessage(resource, "OrderId", (Locale)context.get("locale")) + ": " + orderId;
						Map<String, Object> mapContext = new HashMap<String, Object>();
			    		mapContext.put("partyId", partyId);
			    		mapContext.put("action", "viewOrder?orderId="+orderId+"&activeTab=stockIn-tab");
			    		mapContext.put("targetLink", "");
			    		mapContext.put("header", header);
			    		mapContext.put("ntfType", "ONE");
			    		mapContext.put("sendToGroup", "Y");
			    		mapContext.put("userLogin",(GenericValue)context.get("userLogin"));
			    		mapContext.put("openTime", UtilDateTime.nowTimestamp());
			    		try {
							Map<String, Object> mapReturn = dispatcher.runSync("createNotification", mapContext);
							if (ServiceUtil.isError(mapReturn)) {
								return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
							}
						} catch (GenericServiceException e) {
							String errMsg = "OLBIUS: Fatal error when runSync createNotification: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
			    	}
		    	}
	    	}
	    }
	    result.put("deliveryId", deliveryId);
	    return result;
	}

	public static Map<String, Object> checkRoleByDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String deliveryId = (String)context.get("deliveryId");
    	Boolean isStorekeeperFrom = false;
    	Boolean isStorekeeperTo = false;
    	Boolean isSpecialist = false;
    	Boolean isAdmin = false;
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Delegator delegator = ctx.getDelegator(); 
	    try {	
	    	GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
	    	
	    	List<GenericValue> listStorekeeperFrom = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("originFacilityId"), "partyId", userLogin.getString("partyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"))), null, null, null, false);
	    	listStorekeeperFrom = EntityUtil.filterByDate(listStorekeeperFrom);
			if (!listStorekeeperFrom.isEmpty()){
				isStorekeeperFrom = true;
			}
			
			List<GenericValue> listStorekeeperTo = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("destFacilityId"), "partyId", userLogin.getString("partyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"))), null, null, null, false);
			listStorekeeperTo = EntityUtil.filterByDate(listStorekeeperTo);
			if (!listStorekeeperTo.isEmpty()){
				isStorekeeperTo  = true;
			}
			
			List<GenericValue> listSpecialist = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("originFacilityId"), "partyId", userLogin.getString("partyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.specialist"))), null, null, null, false);
			listSpecialist = EntityUtil.filterByDate(listSpecialist);
			if (!listSpecialist.isEmpty()){
				isSpecialist = true;
			}
			
			List<GenericValue> listAdmins = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", delivery.getString("originFacilityId"), "partyId", userLogin.getString("partyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.admin"))), null, null, null, false);
			listAdmins = EntityUtil.filterByDate(listAdmins);
			if (!listAdmins.isEmpty()){
				isAdmin = true;
			}
	    }
    	catch (GenericEntityException e){
    		return ServiceUtil.returnError("checkRoleByDelivery error" + e.toString());
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("isStorekeeperFrom", isStorekeeperFrom);
    	mapReturn.put("isStorekeeperTo", isStorekeeperTo);
    	mapReturn.put("isSpecialist", isSpecialist);
    	mapReturn.put("isAdmin", isAdmin);
    	
    	return mapReturn;
	}
	
	public static Map<String,Object> getDeliveryById(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		//Get parameters
		String deliveryId = (String)context.get("deliveryId");
		Delegator delegator = ctx.getDelegator();
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("DeliveryDetail", UtilMisc.toMap("deliveryId", deliveryId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError("Service getDeliveryById:" + e.toString());
		}
		
		List<GenericValue> orderRoleTos = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", delivery.getString("orderId"), "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
		List<GenericValue> orderRoleFroms = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", delivery.getString("orderId"), "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
		
		String partyFromName = null;
		String partyToName = null;
		if (!orderRoleTos.isEmpty()){
			partyToName = PartyUtil.getPartyName(delegator, orderRoleTos.get(0).getString("partyId"));
		}
		if (!orderRoleFroms.isEmpty()){
			partyFromName = PartyUtil.getPartyName(delegator, orderRoleFroms.get(0).getString("partyId"));
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("deliveryId", delivery.get("deliveryId"));
		result.put("statusId", delivery.get("statusId"));
		result.put("orderId", delivery.get("orderId"));
		result.put("transferId", delivery.get("transferId"));
		result.put("requirementId", delivery.get("requirementId"));
		result.put("originFacilityId", delivery.get("originFacilityId"));
		result.put("destFacilityId", delivery.get("destFacilityId"));
		result.put("createDate", delivery.get("createDate"));
		result.put("partyIdTo", delivery.get("partyIdTo"));
		result.put("destContactMechId", delivery.get("destContactMechId"));
		result.put("partyIdFrom", delivery.get("partyIdFrom"));
		result.put("deliveryDate", delivery.get("deliveryDate"));
		result.put("originContactMechId", delivery.get("originContactMechId"));
		result.put("deliveryDate", delivery.get("deliveryDate"));
		result.put("shipmentId", delivery.get("shipmentId"));
		result.put("pathScanFile", delivery.get("pathScanFile"));
		result.put("pathScanFileExpt", delivery.get("pathScanFileExpt"));
		result.put("estimatedStartDate", delivery.get("estimatedStartDate"));
		result.put("estimatedArrivalDate", delivery.get("estimatedArrivalDate"));
		result.put("actualStartDate", delivery.get("actualStartDate"));
		result.put("actualArrivalDate", delivery.get("actualArrivalDate"));
		result.put("no", delivery.get("no"));
		result.put("originFacilityName", delivery.get("originFacilityName"));
		result.put("destFacilityName", delivery.get("destFacilityName"));
		result.put("originAddress", delivery.get("originAddress"));
		result.put("destAddress", delivery.get("destAddress"));
		result.put("deliveryTypeId", delivery.get("deliveryTypeId"));
		result.put("deliveryTypeDesc", delivery.get("deliveryTypeDesc"));
		result.put("partyFromName", partyFromName);
		result.put("partyToName", partyToName);
		
		return result;
	}
	
	public static Map<String,Object> getINVByOrderAndDlv(DispatchContext ctx, Map<String, Object> context){
	    Delegator delegator = ctx.getDelegator();
	    String deliveryId = (String)context.get("deliveryId");
	    String orderId = (String)context.get("orderId");
	    String facilityId = (String)context.get("facilityId");
	    String checkLabel = (String)context.get("checkLabel");
	    
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
	    List<String> listOrderBy = new ArrayList<String>();
	    listOrderBy.add("-expireDate");
	    try{
	        listData = delegator.findList("DeliveryINVOrderItem", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, listOrderBy, null, false);
	        List<GenericValue> listFilter = new ArrayList<GenericValue>();
	        if (!listData.isEmpty()){
	        	for (GenericValue item : listData){
	        		if (listFilter.isEmpty()){
	        			if (UtilValidate.isNotEmpty(checkLabel) && "Y".equals(checkLabel)){
        					String invId = item.getString("inventoryItemId");
        					GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", invId));
        					String productId = inv.getString("productId");
        					List<GenericValue> listConfigLabels = delegator.findList("ConfigLabel", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
        					if (!listConfigLabels.isEmpty()){
        						Boolean checkHasLabel = true;
        						for (GenericValue label : listConfigLabels) {
									String inventoryItemLabelId = label.getString("inventoryItemLabelId");
									List<GenericValue> listLabelAppl = delegator.findList("InventoryItemLabelAppl", EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelId, "inventoryItemId", invId)), null, null, null, false);
									if (listLabelAppl.isEmpty()){
										checkHasLabel = false;
										break;
									}
								}
        						if (checkHasLabel){
        							listFilter.add(item);
        						}
        					} else {
//        						listFilter.add(item);
        					}
        				} else {
        					listFilter.add(item);
        				}
	        		} else {
	        			Boolean check = false;
	        			for (GenericValue existed : listFilter){
	        				if (existed.getString("inventoryItemId").equals(item.getString("inventoryItemId"))){
	        					check = true;
	        				}
	        			}
	        			if (!check){
	        				if (UtilValidate.isNotEmpty(checkLabel) && "Y".equals(checkLabel)){
	        					String invId = item.getString("inventoryItemId");
	        					GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", invId));
	        					String productId = inv.getString("productId");
	        					List<GenericValue> listConfigLabels = delegator.findList("ConfigLabel", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
	        					if (!listConfigLabels.isEmpty()){
	        						Boolean checkHasLabel = true;
	        						for (GenericValue label : listConfigLabels) {
										String inventoryItemLabelId = label.getString("inventoryItemLabelId");
										List<GenericValue> listLabelAppl = delegator.findList("InventoryItemLabelAppl", EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelId, "inventoryItemId", invId)), null, null, null, false);
										if (listLabelAppl.isEmpty()){
											checkHasLabel = false;
											break;
										}
									}
	        						if (checkHasLabel){
	        							listFilter.add(item);
	        						}
	        					} else {
//	        						listFilter.add(item);
	        					}
	        				} else {
	        					listFilter.add(item);
	        				}
	        			}
	        		}
	        	}
	        }
	        listData = new ArrayList<GenericValue>();
	        listData.addAll(listFilter);
	    } catch (GenericEntityException e) {
            Debug.log(e.getStackTrace().toString(), module);
            return ServiceUtil.returnError("Service getINVByOrderAndDlv:" + e.toString());
        }
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    result.put("listData", listData);
	    return result;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> getListDeliveryItem(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	EntityListIterator listIterator = null;
       	String deliveryId = null;
       	if(!UtilValidate.isEmpty(parameters.get("deliveryId"))){
       		deliveryId = parameters.get("deliveryId")[0];
       	}
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("deliveryId", deliveryId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    		if (UtilValidate.isNotEmpty((delivery.getString("orderId")))){
    			listIterator = delegator.find("DeliveryItemView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		} else if (UtilValidate.isNotEmpty((delivery.getString("transferId")))){
    			listIterator = delegator.find("DeliveryItemTransferView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListDeliveryItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getDeliveryItemDetail(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String deliveryId = (String)context.get("deliveryId");
        String deliveryItemSeqId = (String)context.get("deliveryItemSeqId");
        String productId = null;
        String quantityUomId = null;
        Timestamp expireDate = null;
        try {
			GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			GenericValue deliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
			if (deliveryItem != null){
				String orderId = (String)deliveryItem.get("fromOrderId");
				if (orderId != null){
					String orderItemSeqId = (String)deliveryItem.get("fromOrderItemSeqId");
					GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
					productId = (String)orderItem.get("productId");
					quantityUomId = (String)orderItem.get("quantityUomId");
					if (quantityUomId == null){
						GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						quantityUomId = (String)product.get("quantityUomId");
					}
					if (orderItem.getTimestamp("expireDate") == null){
						expireDate = deliveryItem.getTimestamp("expireDate");
					} else {
						expireDate = orderItem.getTimestamp("expireDate");
					}
				} 
				String defaultWeightUomId = delivery.getString("defaultWeightUomId");
				result.put("productId", productId);
				result.put("quantityUomId", quantityUomId);
				result.put("expireDate", expireDate);
				result.put("defaultWeightUomId", defaultWeightUomId);
				
			} else {
				return ServiceUtil.returnError("Problem when get DeliveryItem records");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
        return result;
	}
	
	public static Map<String, Object> getProductDeliveryWeight(DispatchContext ctx, Map<String, ?> context){
		String deliveryId = (String)context.get("deliveryId");
		String deliveryItemSeqId = (String)context.get("deliveryItemSeqId");
		Delegator delegator = ctx.getDelegator();
		GenericValue deliveyItem = null;
		BigDecimal weight = BigDecimal.ZERO;
		BigDecimal productWeight = BigDecimal.ZERO;
		String defaultWeightUomId = null;
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			deliveyItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
			BigDecimal actualExportedQty = deliveyItem.getBigDecimal("actualExportedQuantity");
			BigDecimal quantity = deliveyItem.getBigDecimal("quantity");
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
			if (product != null && (product.getBigDecimal("weight") != null || product.getBigDecimal("productWeight") != null)){
				if (deliveyItem.get("statusId").equals("DELI_ITEM_APPROVED") || deliveyItem.get("statusId").equals("DELI_ITEM_CREATED")){
					if (product.getBigDecimal("weight") != null){
						weight = quantity.multiply(product.getBigDecimal("weight"));
					} else if (product.getBigDecimal("productWeight") != null){
						productWeight = quantity.multiply(product.getBigDecimal("productWeight"));
					}
				} else if (!"DELI_ITEM_CANCELLED".equals(deliveyItem.getString("statusId"))){
					if (product.getBigDecimal("weight") != null){
						weight = actualExportedQty.multiply(product.getBigDecimal("weight"));
					} else if (product.getBigDecimal("productWeight") != null){
						productWeight = actualExportedQty.multiply(product.getBigDecimal("productWeight"));
					}
				}
			}
			defaultWeightUomId = delivery.getString("defaultWeightUomId");
			if (defaultWeightUomId == null){
				defaultWeightUomId = product.getString("weightUomId");
			} else {
				if (product.getString("weightUomId") == null){
					GenericValue kgUom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", "WT_kg"));
					if (kgUom != null){
                       defaultWeightUomId = "WT_kg";
                       mapResult.put("weightUomId", defaultWeightUomId);
                       mapResult.put("totalWeight", BigDecimal.ZERO);
                       return mapResult;
					} else {
						return ServiceUtil.returnError("Product not have weight uom!");
					}
				}
				if (!product.getString("weightUomId").equals(defaultWeightUomId)) {
					GenericValue convert = null;
					convert = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", defaultWeightUomId, "uomIdTo", product.getString("weightUomId")));
					if (convert != null){
						if (weight.compareTo(BigDecimal.ZERO) > 0){
							weight = weight.divide(convert.getBigDecimal("conversionFactor"), decimals, rounding);
						} else {
							if (productWeight.compareTo(BigDecimal.ZERO) > 0){
								productWeight = productWeight.divide(convert.getBigDecimal("conversionFactor"), decimals, rounding);
							}
						}
					} else {
						convert = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", defaultWeightUomId, "uomIdTo", product.getString("weightUomId")));
						if (convert != null){
							if (weight.compareTo(BigDecimal.ZERO) > 0){
								weight = weight.multiply(convert.getBigDecimal("conversionFactor"));
							} else {
								if (productWeight.compareTo(BigDecimal.ZERO) > 0){
									productWeight = productWeight.multiply(convert.getBigDecimal("conversionFactor"));
								}
							}
							
						} else {
							return ServiceUtil.returnError("Cannot found the conversion between two weight uom: " + defaultWeightUomId + " and " + product.getString("weightUomId"));
						}
					}
				}
			}
		} catch (GenericEntityException e){
			return ServiceUtil.returnError(e.getMessage());
		}
		if (weight.compareTo(BigDecimal.ZERO) > 0){
			mapResult.put("totalWeight", weight);
		} else {
			if (productWeight.compareTo(BigDecimal.ZERO) > 0){
				mapResult.put("totalWeight", productWeight);
			} else {
				mapResult.put("totalWeight", BigDecimal.ZERO);
			}
		}
		mapResult.put("weightUomId", defaultWeightUomId);
		return mapResult;
	}
	
   	@SuppressWarnings("unchecked")
   		public static Map<String, Object> getListOrderItemDelivery(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException, GenericEntityException{
       	Delegator delegator = ctx.getDelegator();
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String orderId = parameters.get("orderId")[0];
       	String facilityId = null;
       	
       	if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
       		facilityId = parameters.get("facilityId")[0];
       	}
       	List<GenericValue> orderItems = LogisticsProductUtil.getOrderItemRemains(delegator, orderId, "WAREHOUSE");
       	List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
   		for(GenericValue item: orderItems){
       	    HashMap<String, Object> tmpMap = new HashMap<String, Object>();
       	    GenericValue objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", item.getString("orderItemSeqId")));
       	    tmpMap.put("orderItemSeqId", item.getString("orderItemSeqId"));
       	    tmpMap.put("orderId", item.getString("orderId"));
   	    	tmpMap.put("expireDate", item.getString("expireDate"));
       	    String baseQuantityUomId = null;
       	    String baseWeightUomId = null;
       	    BigDecimal weight = BigDecimal.ZERO;
       	    String productName = null;
       	    String productCode = null;
       	    String requireAmount = null;
       	    try {
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.getString("productId")));
				baseQuantityUomId = product.getString("quantityUomId");
				baseWeightUomId = product.getString("weightUomId");
				productName = product.getString("productName");
				productCode = product.getString("productCode");
				requireAmount = product.getString("requireAmount");
				if (UtilValidate.isNotEmpty(product.getBigDecimal("productWeight"))){
					weight = product.getBigDecimal("productWeight");	
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("findOne product error!"+ e.toString());
			}
       	    BigDecimal convertNumber = BigDecimal.ONE;
       	    String productId = item.getString("productId");
       	    if (baseQuantityUomId != null && item.getString("quantityUomId") != null){
       	    	convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, item.getString("quantityUomId"), baseQuantityUomId);
       	    	if (UtilValidate.isNotEmpty(weight)) weight = weight.multiply(convertNumber);
       	    }
       	    tmpMap.put("purchaseUomId", item.getString("quantityUomId"));
       	    tmpMap.put("convertNumber", convertNumber);
       	    tmpMap.put("productId", productId);
       	    tmpMap.put("productCode", productCode);
       	    tmpMap.put("productName", productName);
       	    tmpMap.put("quantityUomId", item.getString("quantityUomId"));
       	    tmpMap.put("isPromo", objOrderItem.getString("isPromo"));
       	    tmpMap.put("weightUomId", item.getString("weightUomId"));
       	    tmpMap.put("requireAmount", requireAmount);
       	    tmpMap.put("selectedAmount", item.getString("selectedAmount"));
       	    tmpMap.put("createdQuantity", new BigDecimal(0));
       	    tmpMap.put("baseQuantityUomId", baseQuantityUomId);
       	    tmpMap.put("baseWeightUomId", baseWeightUomId);
       	    tmpMap.put("weight", weight);
       	    
   	    	if(UtilValidate.isNotEmpty(objOrderItem.getBigDecimal("cancelQuantity"))){
   	    		BigDecimal orderQty = objOrderItem.getBigDecimal("alternativeQuantity");
   	    		BigDecimal remainQty = item.getBigDecimal("quantity");
   	    		BigDecimal cancelQty = objOrderItem.getBigDecimal("cancelQuantity").divide(convertNumber);
       	        tmpMap.put("requiredQuantity", orderQty.subtract(cancelQty));
       	        tmpMap.put("requiredQuantityTmp", remainQty);
       	        tmpMap.put("createdQuantity", objOrderItem.getBigDecimal("alternativeQuantity").subtract(orderQty.add(cancelQty)));
       	    } else{
				BigDecimal orderQty = objOrderItem.getBigDecimal("alternativeQuantity");
				BigDecimal remainQty = item.getBigDecimal("quantity");
				if (UtilValidate.isNotEmpty(requireAmount) && requireAmount.equals("Y")){
					BigDecimal selectedAmount = objOrderItem.getBigDecimal("selectedAmount");
					orderQty = orderQty.multiply(selectedAmount);
					tmpMap.put("createdQuantity", orderQty.subtract(remainQty.multiply(selectedAmount)));
				}
				else
					tmpMap.put("createdQuantity", orderQty.subtract(remainQty));
       	        tmpMap.put("requiredQuantity", orderQty);
       	        tmpMap.put("requiredQuantityTmp", remainQty);
       	    }
   	    	List<Map<String, Object>> listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
   	    	tmpMap.put("quantityUomIds", listQtyUoms);
   	    	tmpMap.put("quantityUomIds", JsonUtil.convertListMapToJSON(listQtyUoms));
       	 
       	    if (UtilValidate.isNotEmpty(facilityId)){
       	    	Map<String, Object> qtyMap = FastMap.newInstance();
           	    qtyMap.put("productId", productId);
           	    qtyMap.put("expireDate", item.getString("expireDate"));
           	    qtyMap.put("originFacilityId", facilityId);
           	    qtyMap.put("userLogin", (GenericValue)context.get("userLogin"));
           	    
           	    LocalDispatcher dispatcher = ctx.getDispatcher();
           	    Map<String, Object> tmpResult = dispatcher.runSync("getDetailQuantityInventory", qtyMap);
           	    tmpMap.put("availableToPromiseTotal", (BigDecimal)tmpResult.get("availableToPromiseTotal"));
           	    tmpMap.put("quantityOnHandTotal", (BigDecimal)tmpResult.get("quantityOnHandTotal"));
           	    tmpMap.put("amountOnHandTotal", (BigDecimal)tmpResult.get("amountOnHandTotal"));
       	    } else {
       	    	tmpMap.put("availableToPromiseTotal", BigDecimal.ZERO);
           	    tmpMap.put("quantityOnHandTotal", BigDecimal.ZERO);
           	    tmpMap.put("amountOnHandTotal", BigDecimal.ZERO);
       	    }
       	    listData.add(tmpMap);
        }
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	successResult.put("listIterator", listData);
       	return successResult;
    }
   	@SuppressWarnings({ "unchecked" })
	public static Map<String,Object> createDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	Security security = ctx.getSecurity();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	//Get Parameters
    	String deliveryId = (String)context.get("deliveryId");
    	if (deliveryId != null && !"".equals(deliveryId)){
    		GenericValue deliveryTest = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    		if (deliveryTest != null){
    			return ServiceUtil.returnError("DELIVERY_ID_EXISTED");
    		}
    	} else {
    		deliveryId = delegator.getNextSeqId("Delivery");
    	}
    	String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
    	String carrierPartyId = (String)context.get("carrierPartyId");
    	String partyIdTo = (String)context.get("partyIdTo");
    	String partyIdFrom = (String)context.get("partyIdFrom");
    	String orderId = (String)context.get("orderId");
    	String statusId = (String)context.get("statusId");
    	String destContactMechId = (String)context.get("destContactMechId");
    	String originContactMechId = (String)context.get("originContactMechId");
    	String originFacilityId = (String)context.get("originFacilityId");
    	String destFacilityId = (String)context.get("destFacilityId");
    	String defaultWeightUomId = (String)context.get("defaultWeightUomId");
    	String noNumber = (String)context.get("no");
    	String deliveryTypeId = (String)context.get("deliveryTypeId");
    	String picklistBinId = (String)context.get("picklistBinId");
    	String conversionFactorStr = (String) context.get("conversionFactor");
    	
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
				if (item.containsKey("quantityUomId")){
					mapItems.put("quantityUomId", item.getString("quantityUomId"));
				}
				if (item.containsKey("actualDeliveredAmount")){
					mapItems.put("actualDeliveredAmount", item.getString("actualDeliveredAmount"));
				}
				if (item.containsKey("actualExportedAmount")){
					mapItems.put("actualExportedAmount", item.getString("actualExportedAmount"));
				}
				if (item.containsKey("amount")){
					mapItems.put("amount", item.getString("amount"));
				}
				if (item.containsKey("statusId")){
					mapItems.put("statusId", item.getString("statusId"));
				}
				
				if (item.containsKey("inventoryItemId")){
					mapItems.put("inventoryItemId", item.getString("inventoryItemId"));
				}
				if (item.containsKey("locationId")){
					mapItems.put("locationId", item.getString("locationId"));
				}
				listOrderItems.add(mapItems);
			}
    	} else {
    		listOrderItems = (List<Map<String, String>>)context.get("listOrderItems");
    	}
    	
    	if (UtilValidate.isNotEmpty(shipmentMethodTypeId) && UtilValidate.isNotEmpty(carrierPartyId)){
    		List<GenericValue> listShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
    		for (GenericValue item : listShipGroups) {
    			if (UtilValidate.isEmpty(item.getString("shipmentMethodTypeId")) && UtilValidate.isEmpty(item.getString("carrierPartyId"))){
    				item.put("shipmentMethodTypeId", shipmentMethodTypeId);
        			item.put("carrierPartyId", carrierPartyId);
        			item.store();
    			}
			}
    	}
    	
    	//Make Delivery
    	GenericValue delivery = delegator.makeValue("Delivery");
    	Timestamp deliveryDate = (Timestamp)(context.get("deliveryDate"));
    	Timestamp estimatedStartDate = (Timestamp)(context.get("estimatedStartDate"));
    	Timestamp estimatedArrivalDate = (Timestamp)(context.get("estimatedArrivalDate"));
    	delivery.put("deliveryId", deliveryId);
    	delivery.put("deliveryDate", deliveryDate);
    	delivery.put("estimatedStartDate", estimatedStartDate);
    	delivery.put("estimatedArrivalDate", estimatedArrivalDate);
    	Timestamp createDate = UtilDateTime.nowTimestamp();
    	delivery.put("createDate", createDate);
    	delivery.put("partyIdFrom", partyIdFrom);
    	delivery.put("deliveryTypeId", deliveryTypeId);
    	delivery.put("picklistBinId", picklistBinId);//Huyendt remove comment for picklistBin in ACC service create Delivery
    	delivery.put("originContactMechId", originContactMechId);
    	delivery.put("originFacilityId", originFacilityId);
    	delivery.put("destFacilityId", destFacilityId);
    	delivery.put("deliveryId", deliveryId);
    	delivery.put("partyIdTo", partyIdTo);
    	delivery.put("orderId", orderId);
    	delivery.put("statusId", statusId);
    	delivery.put("no", noNumber);
    	delivery.put("defaultWeightUomId", defaultWeightUomId);
    	delivery.put("destContactMechId", destContactMechId);
    	if(UtilValidate.isNotEmpty(conversionFactorStr))
        	delivery.put("conversionFactor", new BigDecimal(conversionFactorStr));
    	delivery.create();
    	
    	//Make Delivery Item
    	if (!listOrderItems.isEmpty()){
    		for (Map<String, String> item : listOrderItems){
    			GenericValue deliveryItem = delegator.makeValue("DeliveryItem");
    			deliveryItem.put("deliveryId", deliveryId);
		        delegator.setNextSubSeqId(deliveryItem, "deliveryItemSeqId", 5, 1);
    			deliveryItem.put("fromOrderItemSeqId", item.get("orderItemSeqId"));
    			deliveryItem.put("fromOrderId", item.get("orderId"));
    			GenericValue objOrderItem = delegator.findOne("OrderItem", false,
						UtilMisc.toMap("orderId", orderId, "orderItemSeqId", item.get("orderItemSeqId")));
    			String productId = objOrderItem.getString("productId");
    			GenericValue objProduct = delegator.findOne("Product", false,
						UtilMisc.toMap("productId", productId));
    			BigDecimal quantity = BigDecimal.valueOf(Double.parseDouble(item.get("quantity")));
    			if (UtilValidate.isNotEmpty(item.get("quantityUomId")) && UtilValidate.isNotEmpty(objProduct.getString("quantityUomId")) && !objProduct.getString("quantityUomId").equals(item.get("quantityUomId"))){
    				String quantityUomId = item.get("quantityUomId");
    				BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, objProduct.getString("quantityUomId"));
    				quantity = quantity.multiply(convert);
    			}
    			deliveryItem.put("quantity", quantity);
    			
    			if (UtilValidate.isNotEmpty(item.get("amount")) && ProductUtil.isWeightProduct(delegator, productId)){
					deliveryItem.put("amount", new BigDecimal(item.get("amount")));
					deliveryItem.put("quantity", BigDecimal.ONE);
    			} else {
    				deliveryItem.put("amount", BigDecimal.ZERO);
    			}
    			
    			if (UtilValidate.isNotEmpty(item.get("actualExportedQuantity"))){
    				deliveryItem.put("actualExportedQuantity", new BigDecimal(item.get("actualExportedQuantity")));
    			} else {
    				deliveryItem.put("actualExportedQuantity", BigDecimal.ZERO);
    			}
    			if (UtilValidate.isNotEmpty(item.get("actualDeliveredQuantity"))){
    				deliveryItem.put("actualDeliveredQuantity", new BigDecimal(item.get("actualDeliveredQuantity")));
    			} else {
    				deliveryItem.put("actualDeliveredQuantity", BigDecimal.ZERO);
    			}
    			if (UtilValidate.isNotEmpty(item.get("actualExportedAmount"))){
    				deliveryItem.put("actualExportedAmount", new BigDecimal(item.get("actualExportedAmount")));
    			} else {
    				deliveryItem.put("actualExportedQuantity", BigDecimal.ZERO);
    			}
    			if (UtilValidate.isNotEmpty(item.get("actualDeliveredAmount"))){
    				deliveryItem.put("actualDeliveredAmount", new BigDecimal(item.get("actualDeliveredAmount")));
    			} else {
    				deliveryItem.put("actualDeliveredAmount", BigDecimal.ZERO);
    			}
    			if (UtilValidate.isNotEmpty(item.get("statusId"))){
    				deliveryItem.put("statusId", item.get("statusId"));
    			} else {
    				deliveryItem.put("statusId", "DELI_ITEM_CREATED");
    			}
    			if (item.containsKey("actualExpireDate") && UtilValidate.isNotEmpty(item.get("actualExpireDate"))){
    				deliveryItem.put("actualExpireDate", Timestamp.valueOf(item.get("actualExpireDate")));
    			} 
    			if (item.containsKey("actualManufacturedDate") && UtilValidate.isNotEmpty(item.get("actualManufacturedDate"))){
    				deliveryItem.put("actualManufacturedDate", Timestamp.valueOf(item.get("actualManufacturedDate")));
    			}
    			if (item.containsKey("expireDate") && UtilValidate.isNotEmpty(item.get("expireDate"))){
    				deliveryItem.put("expireDate", new Timestamp(new Long(item.get("expireDate"))));
    			}
    			if (item.containsKey("inventoryItemId") && UtilValidate.isNotEmpty(item.get("inventoryItemId"))){
    				deliveryItem.put("inventoryItemId", item.get("inventoryItemId"));
    			}
    			if (item.containsKey("locationId") && UtilValidate.isNotEmpty(item.get("locationId"))){
    				deliveryItem.put("locationId", item.get("locationId"));
    			}
    			deliveryItem.create();
    		}
    	}
    	
    	//Create DeliveryStatus
		String userLoginId = (String)userLogin.get("userLoginId");
		GenericValue deliveryStatus = delegator.makeValue("DeliveryStatus");
		deliveryStatus.put("deliveryStatusId", delegator.getNextSeqId("DeliveryStatus"));
		deliveryStatus.put("deliveryId", deliveryId);
		deliveryStatus.put("statusId", statusId);
		deliveryStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
		deliveryStatus.put("statusUserLogin", userLoginId);
		delegator.createOrStore(deliveryStatus);
		
		if (security.hasPermission("DELIVERY_ADMIN", userLogin) || security.hasPermission("SALESORDER_ACTION_QUICKSHIP", userLogin)) {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			try {
				dispatcher.runSync("updateDeliveryStatus", UtilMisc.toMap("deliveryId", deliveryId, "newStatusId", "DLV_APPROVED", "setItemStatus", "Y", "newItemStatus", "DELI_ITEM_APPROVED", "userLogin", (GenericValue)context.get("userLogin")));
			} catch (GenericServiceException e){
				Debug.log(module, e.toString());
				return ServiceUtil.returnError("OLBIUS: updateDeliveryStatus after create delivery error");
			}
		}
		result.put("deliveryTypeId", deliveryTypeId);
    	result.put("deliveryId", deliveryId);
    	return result;
	}
	
public static Map<String,Object> updateDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	
    	Delegator delegator = ctx.getDelegator();
    	
    	//Get Parameters
    	String deliveryId = (String)context.get("deliveryId");
    	String statusId = (String)context.get("statusId");
    	String invoiceId = (String)context.get("invoiceId");
    	Boolean needReceiveProduct = false;
    	Long actualStartDate = (Long)context.get("actualStartDate");
    	Long actualArrivalDateTmp = (Long)context.get("actualArrivalDate");
		String pathScanFile = (String)context.get("pathScanFile");
    	if (UtilValidate.isNotEmpty(deliveryId)){
    		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    		String deliveryTypeId = delivery.getString("deliveryTypeId");
    		if (UtilValidate.isNotEmpty(delivery) && UtilValidate.isNotEmpty(statusId)){
				if ("DLV_DELIVERED".equals(statusId) && "DELIVERY_PURCHASE".equals(deliveryTypeId)){
    				String listItems = (String)context.get("listDeliveryItems");
    				JSONArray listItemTmp = JSONArray.fromObject(listItems);
    				for (int j = 0; j < listItemTmp.size(); j++){
    					JSONObject dlvItem = listItemTmp.getJSONObject(j);
    					String deliveryItemSeqId = dlvItem.getString("deliveryItemSeqId");
    					String statusItem = dlvItem.getString("statusId");
    					String batch = "";
    					if(dlvItem.containsKey("batch")){
    						batch = dlvItem.getString("batch");
    					}
    					String locationId = null;
    					if(dlvItem.containsKey("locationId")){
    						locationId = dlvItem.getString("locationId");
    					}
    					GenericValue item = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
    					String fromOrderId = null;
						if (dlvItem.containsKey("fromOrderId")){
							fromOrderId = dlvItem.getString("fromOrderId");
						}
						String fromOrderItemSeqId = null;
						if (dlvItem.containsKey("fromOrderItemSeqId")){
							fromOrderItemSeqId = dlvItem.getString("fromOrderItemSeqId");
						}
						
						BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
						if (dlvItem.containsKey("actualDeliveredQuantity") && UtilValidate.isNotEmpty(dlvItem.getString("actualDeliveredQuantity"))) {
							actualDeliveredQuantity = new BigDecimal(dlvItem.getString("actualDeliveredQuantity"));
						}
						BigDecimal actualDeliveredAmount = BigDecimal.ZERO;
						if (dlvItem.containsKey("actualDeliveredAmount") && UtilValidate.isNotEmpty(dlvItem.getString("actualDeliveredAmount"))) {
							actualDeliveredAmount = new BigDecimal(dlvItem.getString("actualDeliveredAmount"));
						}
						
						GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", fromOrderId, "orderItemSeqId", fromOrderItemSeqId));
    					if (UtilValidate.isNotEmpty(item)){
							if (actualDeliveredQuantity.compareTo(BigDecimal.ZERO) > 0 || actualDeliveredAmount.compareTo(BigDecimal.ZERO) > 0){
								if (UtilValidate.isNotEmpty(item.get("actualDeliveredQuantity"))) {
									if (item.getBigDecimal("actualDeliveredQuantity").compareTo(BigDecimal.ZERO) > 0){
										return ServiceUtil.returnError("OLBIUS: delivery has been received");
									}
								}
								needReceiveProduct = true;
								Timestamp actualExpireDate = null;
								if (UtilValidate.isNotEmpty(dlvItem.get("actualExpireDate"))){
									actualExpireDate = new Timestamp((Long)dlvItem.get("actualExpireDate"));
								}
								Timestamp actualManufacturedDate = null;
								if (UtilValidate.isNotEmpty(dlvItem.get("actualManufacturedDate"))){
									actualManufacturedDate = new Timestamp((Long)dlvItem.get("actualManufacturedDate"));
								}
	        					item.put("actualDeliveredQuantity", actualDeliveredQuantity);
	        					item.put("actualDeliveredAmount", actualDeliveredAmount);
	        					item.put("actualExpireDate", actualExpireDate);
	        					item.put("actualManufacturedDate", actualManufacturedDate);
	        					item.put("statusId", statusItem);
	        					item.put("batch", batch);
	        					if (UtilValidate.isNotEmpty(locationId)) {
        							item.put("locationId", locationId);
								}
	        					delegator.store(item);
							} else {
	        					item.put("actualDeliveredQuantity", BigDecimal.ZERO);
	        					item.put("actualDeliveredAmount", BigDecimal.ZERO);
	        					item.put("actualExpireDate", null);
	        					item.put("actualManufacturedDate", null);
	        					item.put("statusId", statusItem);
	        					item.put("batch", null);
	        					delegator.store(item);
							}
    					} else {
    						// create new item
    						if (orderItem != null){
    							String productId = orderItem.getString("productId");
    							if (productId != null){
    								List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", fromOrderId, "fromOrderItemSeqId", fromOrderItemSeqId, "deliveryId", deliveryId)), null, null, null, false);
    								if (!listDlvItems.isEmpty() && (actualDeliveredQuantity.compareTo(BigDecimal.ZERO) > 0 || actualDeliveredAmount.compareTo(BigDecimal.ZERO) > 0)){
    									Timestamp actualExpireDate = null;
    									if (UtilValidate.isNotEmpty(dlvItem.get("actualExpireDate"))){
    										actualExpireDate = new Timestamp((Long)dlvItem.get("actualExpireDate"));
    									}
    									Timestamp actualManufacturedDate = null;
    									if (UtilValidate.isNotEmpty(dlvItem.get("actualManufacturedDate"))){
    										actualManufacturedDate = new Timestamp((Long)dlvItem.get("actualManufacturedDate"));
    									}
    									GenericValue deliveryItem = listDlvItems.get(0);
    									GenericValue newDeliveryItem = delegator.makeValue("DeliveryItem");
    									newDeliveryItem.putAll(deliveryItem);
    									newDeliveryItem.put("deliveryItemSeqId", null);
    									delegator.setNextSubSeqId(newDeliveryItem, "deliveryItemSeqId", 5, 1);
    									newDeliveryItem.put("actualDeliveredQuantity", actualDeliveredQuantity);
    									newDeliveryItem.put("actualDeliveredAmount", actualDeliveredAmount);
    									newDeliveryItem.put("actualExpireDate", actualExpireDate);
    									newDeliveryItem.put("actualManufacturedDate", actualManufacturedDate);
    									newDeliveryItem.put("statusId", statusItem);
    									newDeliveryItem.put("batch", batch);
    									
    									if (UtilValidate.isNotEmpty(locationId)) {
		        							newDeliveryItem.put("locationId", locationId);
    									}
    									
    									delegator.create(newDeliveryItem);
    								}
    							}
    						}
    					}
    				}
    				// Remove if this item has been replaced by another item
    				List<GenericValue> listUpdatedItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
    				List<GenericValue> listWillBeRemove = new ArrayList<GenericValue>();
    				for (GenericValue item : listUpdatedItems){
    					String orderId = item.getString("fromOrderId");
    					String orderItemSeqId = item.getString("fromOrderItemSeqId");
    					if (item.getBigDecimal("actualDeliveredQuantity").compareTo(BigDecimal.ZERO) == 0){
    						Boolean isCheck = false;
    						for (GenericValue item2 : listUpdatedItems){
    							if (item2.getString("fromOrderId").equals(orderId) && item2.getString("fromOrderItemSeqId").equals(orderItemSeqId)){
    								if (item.getBigDecimal("actualDeliveredQuantity").compareTo(BigDecimal.ZERO) > 0){
    									isCheck = true; 
    									break;
    								}
    							}
    						}
    						if (isCheck){
    							listWillBeRemove.add(item);
    						}
    					}
    				}
					delegator.removeAll(listWillBeRemove);
				}
    		} else {
    			return ServiceUtil.returnError("OLBIUS: delivery not found");
    		}
    		
    		// update delivery status
    		LocalDispatcher dispatcher = ctx.getDispatcher();
    		try {
    			String statusItemId = null;
    			if ("DLV_CREATED".equals(statusId)){
    				statusItemId = "DELI_ITEM_CREATED";
    			} else if ("DLV_PROPOSED".equals(statusId)){
    				statusItemId = "DELI_ITEM_PROPOSED";
    			} else if ("DLV_APPROVED".equals(statusId)){
    				statusItemId = "DELI_ITEM_APPROVED";
    			} else if ("DLV_EXPORTED".equals(statusId)){
    				statusItemId = "DELI_ITEM_EXPORTED";
     			} else if ("DLV_DELIVERED".equals(statusId)){
     				statusItemId = "DELI_ITEM_DELIVERED";
    			}
    			dispatcher.runSync("updateDeliveryStatus", UtilMisc.toMap("deliveryId", deliveryId, "newStatusId", statusId, "setItemStatus", "Y", "newItemStatus", statusItemId, "userLogin", (GenericValue)context.get("userLogin")));
    		} catch (GenericServiceException e) {
    			return ServiceUtil.returnError("OLBIUS: update delivery status error");
    		}
    		delivery.refresh();
    		// update information related
    		if (UtilValidate.isNotEmpty(actualStartDate)){
    			delivery.put("actualStartDate", new Timestamp(actualStartDate));
    		}
    		if (UtilValidate.isNotEmpty(actualArrivalDateTmp)){
    			Timestamp actualArrivalDate = new Timestamp(actualArrivalDateTmp);
    			delivery.put("actualArrivalDate", actualArrivalDate);
    		}
    		if (UtilValidate.isNotEmpty(pathScanFile)){
    			delivery.put("pathScanFile", pathScanFile);
    		}
    		if (UtilValidate.isNotEmpty(invoiceId)){
    			delivery.put("invoiceId", invoiceId);
    		}
    		delegator.store(delivery);
    		// update delivery item with purchase delivery when vendor confirmed delivery
    		if ("DLV_EXPORTED".equals(statusId) && "DELIVERY_PURCHASE".equals(deliveryTypeId)){
    			List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
    			for (GenericValue item : listDlvItems) {
    				item.put("actualExportedQuantity", item.getBigDecimal("quantity"));
    				item.put("actualExportedAmount", item.getBigDecimal("amount"));
    				item.store();
				}
    		}
    		if ("DLV_DELIVERED".equals(statusId) && "DELIVERY_PURCHASE".equals(deliveryTypeId)){
    			List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
	    		// update debit quantity
	    		if (UtilValidate.isNotEmpty(context.get("listProductDebits"))) {
	    			String listDebits = (String)context.get("listProductDebits");
	    			JSONArray listDebitItem = JSONArray.fromObject(listDebits);
	    			List<Map<String, String>> listMapDebit = new ArrayList<Map<String, String>>();
	    			for (int j = 0; j < listDebitItem.size(); j++){
	    				Map<String, String> map = FastMap.newInstance();
	    				JSONObject debitItem = listDebitItem.getJSONObject(j);
	    				if(debitItem.containsKey("productId")){
	    					String productId = debitItem.getString("productId");
	    					map.put("productId", productId);
	    				}
	    				if(debitItem.containsKey("debitQuantity")){
	    					String quantity = debitItem.getString("debitQuantity");
	    					map.put("debitQuantity", quantity);
	    				}
	    				listMapDebit.add(map);
	    			}
	    			
	    			if (!listMapDebit.isEmpty() && !listDlvItems.isEmpty()){
	    				for (Map<String, String> debit : listMapDebit) {
	    					for (GenericValue item : listDlvItems) {
	        					GenericValue objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")));
	        					String productId = objOrderItem.getString("productId");
	        					if (productId.equals(debit.get("productId"))) {
	    							if (UtilValidate.isNotEmpty(debit.get("debitQuantity"))) {
	    								BigDecimal debitQuantity = new BigDecimal(debit.get("debitQuantity"));
	    								item.put("debitQuantity", debitQuantity);
	    								if (ProductUtil.isWeightProduct(delegator, productId)) {
	    									item.put("debitAmount", debitQuantity);
										}
	    								delegator.store(item);
	    								break;
									}
	    						}
	        				}
						}
	    			}
				}
    		}
    		
    		if (needReceiveProduct){
    			Map<String, Object> input = FastMap.newInstance();
    			input.put("deliveryId", deliveryId);
    			input.put("userLogin", (GenericValue)context.get("userLogin"));
    			try {
					Map<String, Object> mapReturn = dispatcher.runSync("receiveInventoryFromDelivery", input);
					if (ServiceUtil.isError(mapReturn)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(mapReturn));
					}
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when runSync receiveInventoryFromDelivery: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
    		}
    	}
    	Map<String, Object> result = new FastMap<String, Object>();
    	result.put("deliveryId", deliveryId);
    	return result;
	}
	
	public static Map<String,Object> updateDeliveryItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		//Get parameters
		String deliveryId = (String)context.get("deliveryId");
		String orderId = (String)context.get("fromOrderId");
		String orderItemSeqId = (String)context.get("fromOrderItemSeqId");
		String strInventoryItemId = (String)context.get("inventoryItemId");
		String deliveryItemSeqId = (String)context.get("deliveryItemSeqId");
		BigDecimal actualExportedQuantity = (BigDecimal)context.get("actualExportedQuantity"); // The number to reserve
		BigDecimal actualDeliveredQuantity = (BigDecimal)context.get("actualDeliveredQuantity");
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// FIXME check for DeliveryItem has exported
		// I. Update Delivery
		//Create Subject, Observer and Attach Observer to Subject 
		Observer o = new DeliveryObserver();
		ItemSubject is = new DeliveryItemSubject();
		is.attach(o);
		GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		Map<String, Object> parameters = FastMap.newInstance();
		parameters.put("deliveryId", deliveryId);
		parameters.put("deliveryItemSeqId", deliveryItemSeqId);
		parameters.put("delegator", delegator);
		String deliveryTypeId = null;
		GenericValue delivery = null;
		delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		deliveryTypeId = (String)delivery.get("deliveryTypeId");
		if ("DELIVERY_SALES".equals(deliveryTypeId) || "DELIVERY_PURCHASE".equals(deliveryTypeId)){
			if(UtilValidate.isEmpty(orderId)){
			    orderId = delivery.getString("orderId");
			}
	        GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));

	        BigDecimal selectedAmount = orderItem.getBigDecimal("selectedAmount");
			if("DLV_APPROVED".equals(delivery.getString("statusId"))){
				if (actualExportedQuantity.compareTo(BigDecimal.ZERO) > 0 && UtilValidate.isNotEmpty(orderItem) && "ITEM_APPROVED".equals(orderItem.getString("statusId"))){
					GenericValue newINV = null;
		            newINV = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", strInventoryItemId));
					// check requireAmount
					if (ProductUtil.isWeightProduct(delegator, orderItem.getString("productId")) && UtilValidate.isNotEmpty(selectedAmount) && selectedAmount.compareTo(BigDecimal.ZERO) > 0) {
						if (newINV.getBigDecimal("amountOnHandTotal").compareTo(actualExportedQuantity.multiply(selectedAmount)) < 0){
			            	return ServiceUtil.returnError("QUANTITY_NOT_ENOUGH");
			            }
					} else {
						if (newINV.getBigDecimal("quantityOnHandTotal").compareTo(actualExportedQuantity) < 0){
			            	return ServiceUtil.returnError("QUANTITY_NOT_ENOUGH");
			            }
					}
		            GenericValue tmpDelItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
	            	if(UtilValidate.isNotEmpty(newINV)){
		                tmpDelItem.set("actualExpireDate", newINV.get("expireDate"));
		                tmpDelItem.set("actualManufacturedDate", newINV.get("datetimeManufactured"));
		                tmpDelItem.set("inventoryItemId", strInventoryItemId);
		                tmpDelItem.store();
	            	}
					// Issue
					Map<String, Object> issuePara = FastMap.newInstance();
					issuePara.put("deliveryId", deliveryId);
					issuePara.put("deliveryItemSeqId", deliveryItemSeqId);
					issuePara.put("context", ctx);
					issuePara.put("locale", locale);
					BigDecimal quantityToIssuse = BigDecimal.ZERO;
					try {
						if ("DELIVERY_SALES".equals(deliveryTypeId)){
						    // TODO Optimize the following code
				            // I. Update reservation and InventoryItemDetail, balanceINV and Insert new record to OrderItemShipGrpRes
				            List<GenericValue> listResevedItem = null;
				            BigDecimal remainQty = BigDecimal.ZERO;
				            try {
				                //  1. Update reservation and InventoryItemDetail, balanceINV
				                List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
				                listAllConditions.add(EntityCondition.makeCondition("orderId", orderId));
				                listAllConditions.add(EntityCondition.makeCondition("orderItemSeqId", orderItemSeqId));
				                // TODO If one orderItem can be split into multi-delivery in the same facility, the following code must be changed 
				                
				                while(true){
					                listResevedItem = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), UtilMisc.toSet("inventoryItemId", "quantity", "shipGroupSeqId"), null, null, false);
					                // TODO This loop should have a counter(for instance: 100) to stop, it will prevent program does not hang when the below logic is false.
					                if(listResevedItem == null || listResevedItem.isEmpty()){
				                		break;
				                	}
					                BigDecimal tmpNotReserve = new BigDecimal(actualExportedQuantity.toString());
					                Boolean checkEnough = false;
					                GenericValue itemEnough = null;
					                for (GenericValue item : listResevedItem) {
										if (item.getBigDecimal("quantity").compareTo(tmpNotReserve) == 0){
											checkEnough = true;
											itemEnough = item;
											break;
										}
									}
					                if (!checkEnough){
					                	itemEnough = listResevedItem.get(0);
					                }
					                	
					                BigDecimal tmpBD = itemEnough.getBigDecimal("quantity");
				                    BigDecimal tmpInsertBD = new BigDecimal(tmpBD.toString());
				                    GenericValue tmpRes = delegator.findOne("OrderItemShipGrpInvRes", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", itemEnough.getString("shipGroupSeqId"), "inventoryItemId", itemEnough.getString("inventoryItemId")));
				                    
				                    if(tmpNotReserve.compareTo(tmpBD) == 0){
				                        // delete this reservation
				                        delegator.removeValue(tmpRes);
				                        tmpNotReserve = BigDecimal.ZERO;
				                    }else{
				                        if(tmpNotReserve.compareTo(tmpBD) < 0){
				                            tmpInsertBD = new BigDecimal(tmpNotReserve.toString());
				                            tmpRes.set("quantity", tmpBD.subtract(tmpNotReserve));
				                            tmpRes.store();
				                        } else{
				                            delegator.removeValue(tmpRes);
				                        }
				                        tmpNotReserve = tmpNotReserve.subtract(tmpBD);
				                    }
				                    // FIXME Check for QOH and ATP of input inventoryItem is less than required quantity. 
				                    // update InventoryItemDetail
				                    GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
				                    tmpInvDetail.set("inventoryItemId", itemEnough.getString("inventoryItemId"));
				                    tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
				                    java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
				                    tmpInvDetail.set("effectiveDate", tmpDate);
				                    tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
				                    tmpInvDetail.set("availableToPromiseDiff", tmpInsertBD);
				                    tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
				                    tmpInvDetail.create();
				                    // balanceInventoryItems
				                    GenericValue tmpINV = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", itemEnough.getString("inventoryItemId")));
				                    if(tmpINV.get("expireDate") != null){
				                        Map<String, Object> tmpMap = new HashMap<String, Object>();
				                        GenericValue userSystemLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				                        tmpMap.put("userLogin", userSystemLogin);
				                        tmpMap.put("inventoryItemId", itemEnough.getString("inventoryItemId"));
				                        dispatcher.runSync("balanceInventoryItems", tmpMap);
				                    }
				                    // check to break this loop
				                    if(tmpNotReserve.compareTo(BigDecimal.ZERO) <= 0){
				                        break;
				                    }
				                }
				                //    2. Insert new record to OrderItemShipGrpRes
				                // FIXME find out another method. 
				                DelegatorElement tmp = EntityConfigUtil.getDelegator(delegator.getDelegatorBaseName());
            					String idPrefix = tmp.getSequencedIdPrefix();
		                        String tmpSGSI = "00001";
		                        for(int tmpG = 1; tmpG < 100; tmpG++){
		                            if(tmpG < 10){
		                                tmpSGSI = "0000" + Integer.toString(tmpG);
		                            }else if(tmpG < 100){
		                                tmpSGSI = "000" + Integer.toString(tmpG);
		                            }
		                            if (UtilValidate.isNotEmpty(idPrefix)){
		                            	tmpSGSI = idPrefix.concat(tmpSGSI);
		                            }
		                            Map<String, Object> tmpMap = new HashMap<String, Object>();
		                            tmpMap.put("orderId", orderId);
		                            tmpMap.put("orderItemSeqId", orderItemSeqId);
		                            tmpMap.put("inventoryItemId", strInventoryItemId);
		                            tmpMap.put("shipGroupSeqId", tmpSGSI);
		                            GenericValue tmpGSGSI = delegator.findOne("OrderItemShipGrpInvRes", tmpMap, false);
		                            if(UtilValidate.isEmpty(tmpGSGSI)){
		                                break;
		                            }
		                        }
		                        GenericValue  tmpOISG = delegator.makeValue("OrderItemShipGrpInvRes");
				                tmpOISG.set("orderId", orderId);
				                tmpOISG.set("orderItemSeqId", orderItemSeqId);
				                tmpOISG.set("shipGroupSeqId", tmpSGSI);
				                tmpOISG.set("inventoryItemId", strInventoryItemId);
				                tmpOISG.set("reserveOrderEnumId", "INVRO_FIFO_REC");
				                tmpOISG.set("quantity", actualExportedQuantity);
				                tmpOISG.set("quantityNotAvailable", BigDecimal.ZERO);
				                java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
				                tmpOISG.set("reservedDatetime", tmpDate);
				                tmpOISG.set("createdDatetime", tmpDate);
				                tmpOISG.set("promisedDatetime", tmpDate);
				                tmpOISG.create();
				                remainQty = actualExportedQuantity;
				                issuePara.put("shipGroupSeqId", tmpSGSI);
				                
				                // update InventoryItemDetail
				                GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
				                tmpInvDetail.set("inventoryItemId", strInventoryItemId);
				                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
				                tmpInvDetail.set("effectiveDate", tmpDate);
				                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.negate());
				                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("orderId", orderId);
				                tmpInvDetail.set("orderItemSeqId", orderItemSeqId);
				                tmpInvDetail.set("shipGroupSeqId", tmpSGSI);
				                tmpInvDetail.create();
				                
				            } catch (Exception e) {
				                return ServiceUtil.returnError(e.toString());
				            }
							quantityToIssuse = remainQty;
						}
						issuePara.put("quantity", quantityToIssuse);
						if (ProductUtil.isWeightProduct(delegator, orderItem.getString("productId")) && UtilValidate.isNotEmpty(selectedAmount)) {
							issuePara.put("weight", tmpDelItem.getBigDecimal("amount"));
						}
						try {
							DeliveryHelper.issuseDelivery(issuePara);
						} catch (Exception e) {
						    Debug.log(e.getStackTrace().toString(), module);
						    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
		    			}
		    			parameters.put("actualExportedQuantity", actualExportedQuantity);
		    			try {
		    				is.updateExportedQuantity(parameters);
		    			} catch (Exception e) {
		    				Debug.log(e.toString(), module);
		    				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
		    			}
					} catch (Exception e) {
		                Debug.log(e.getStackTrace().toString(), module);
		                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
		            }
				} else if (actualExportedQuantity.compareTo(BigDecimal.ZERO) <= 0){
					Map<String, Object> updateItem = FastMap.newInstance();
					updateItem.put("deliveryId", deliveryId);
					updateItem.put("deliveryItemSeqId", deliveryItemSeqId);
					updateItem.put("actualExportedQuantity", BigDecimal.ZERO);
					updateItem.put("delegator", delegator);
					is.updateExportedQuantity(updateItem);
				} else if (UtilValidate.isEmpty(orderItem)){
					return ServiceUtil.returnError("OLBIUS: Order item not found!");
				} else if ("ITEM_APPROVED".equals(orderItem.getString("statusId"))){
					return ServiceUtil.returnError("OLBIUS: Cannot issue inventory item for Order item with status: " + orderItem.getString("statusId"));
				}
			} else{
				if ("DELIVERY_SALES".equals(deliveryTypeId)){
					parameters.put("actualDeliveredQuantity", actualDeliveredQuantity);
					try {
						is.updateDeliveredQuantity(parameters);
					} catch (GenericEntityException e) {
						Debug.log(e.getStackTrace().toString(), module);
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
					}
				}
			}
		} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
			BigDecimal quantityToIssuse = actualExportedQuantity;
			String transferId = (String)context.get("fromTransferId");
			String transferItemSeqId = (String)context.get("fromTransferItemSeqId");
			GenericValue deliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
			
			GenericValue newINV = null;
            newINV = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", strInventoryItemId));
            
            GenericValue tmpTI = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
            String productId = tmpTI.getString("productId");
            GenericValue tmpProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
            Boolean reqAmount = ProductUtil.isWeightProduct(delegator, productId);
            
            String baseUomId = tmpProduct.getString("quantityUomId");
			if (baseUomId == null){
				return ServiceUtil.returnError("Quantity Uom not found for product:" + tmpProduct.getString("productId") + " of DeliveryItem: deliveryId" + deliveryId + "deliveryItemSeqId:" + deliveryItemSeqId);
			}
            
			if ("DLV_APPROVED".equals(delivery.getString("statusId"))){
				if (UtilValidate.isNotEmpty(deliveryItem)){
		            if(UtilValidate.isNotEmpty(newINV)){
		            	deliveryItem.set("actualExpireDate", newINV.get("expireDate"));
		                deliveryItem.set("actualManufacturedDate", newINV.get("datetimeManufactured"));
		                deliveryItem.set("inventoryItemId", strInventoryItemId);
		                deliveryItem.store();
		            }
		            List<GenericValue> listResevedItem = null;
		            try {
		                //  1. Update reservation and InventoryItemDetail, balanceINV
		                List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		                listAllConditions.add(EntityCondition.makeCondition("transferId", transferId));
		                listAllConditions.add(EntityCondition.makeCondition("transferItemSeqId", transferItemSeqId));
		                // get TransferItem to get quantityUomId
		                
		                Map<String, Object> convertUomContext = FastMap.newInstance();
		                convertUomContext.put("originalValue", actualExportedQuantity);
		                convertUomContext.put("userLogin", (GenericValue)context.get("userLogin"));
		                convertUomContext.put("uomId", tmpTI.get("quantityUomId"));
		                convertUomContext.put("uomIdTo", tmpProduct.get("quantityUomId"));
		                // TODO If one transferItem can be split into multi-delivery in the same facility, this code must be changed 
		                listResevedItem = delegator.findList("TransferItemShipGrpInvRes", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), UtilMisc.toSet("inventoryItemId", "quantity", "shipGroupSeqId", "amount"), null, null, false);
		               
		                BigDecimal tmpNotReserve = actualExportedQuantity;
		                for (int i = 0; i < listResevedItem.size();i++){
		                    BigDecimal tmpBD = listResevedItem.get(i).getBigDecimal("quantity");
		                    if (reqAmount) tmpBD = listResevedItem.get(i).getBigDecimal("amount");
		                    BigDecimal tmpInsertBD = new BigDecimal(tmpBD.toString());
		                    GenericValue tmpRes = delegator.findOne("TransferItemShipGrpInvRes", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId, "shipGroupSeqId", listResevedItem.get(i).getString("shipGroupSeqId"), "inventoryItemId", listResevedItem.get(i).getString("inventoryItemId")));
		                    if (actualExportedQuantity.compareTo(tmpBD) == 0){
		                        // delete this reservation
		                        delegator.removeValue(tmpRes);
		                        tmpNotReserve = BigDecimal.ZERO;
		                    } else {
		                        if (tmpNotReserve.compareTo(tmpBD) < 0){
		                        	// subtract quantity reserve and store GrpInvRes
		                            tmpInsertBD = new BigDecimal(tmpNotReserve.toString());
		                            if (reqAmount) {
		                            	tmpRes.set("amount", tmpBD.subtract(tmpNotReserve));
		                            } else {
		                            	tmpRes.set("quantity", tmpBD.subtract(tmpNotReserve));
		                            }
		                            tmpRes.store();
		                        }else{
		                            delegator.removeValue(tmpRes);
		                        }
		                        tmpNotReserve = tmpNotReserve.subtract(tmpBD);
		                    }
		                    // FIXME Check for QOH and ATP of input inventoryItem is less than required quantity. 
		                    // update InventoryItemDetail
		                    if (reqAmount) {
		                    	if(tmpNotReserve.compareTo(BigDecimal.ZERO) <= 0){
		                    		GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
				                    tmpInvDetail.set("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId"));
				                    tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
				                    java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
				                    tmpInvDetail.set("effectiveDate", tmpDate);
				                    tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
				                    tmpInvDetail.set("availableToPromiseDiff", BigDecimal.ONE);
				                    tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
				                    tmpInvDetail.create();
		                    	}
		                    } else {
		                    	GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
			                    tmpInvDetail.set("inventoryItemId", listResevedItem.get(i).getString("inventoryItemId"));
			                    tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
			                    java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
			                    tmpInvDetail.set("effectiveDate", tmpDate);
			                    tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
			                    tmpInvDetail.set("availableToPromiseDiff", tmpInsertBD);
			                    tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
			                    tmpInvDetail.create();
		                    }
		                    
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
		                DelegatorElement tmp = EntityConfigUtil.getDelegator(delegator.getDelegatorBaseName());
    					String idPrefix = tmp.getSequencedIdPrefix();
		                String tmpSGSI = "00001";
		                for(int tmpG = 1; tmpG < 100; tmpG++){
		                    if(tmpG < 10){
		                        tmpSGSI = "0000" + Integer.toString(tmpG);
		                    }else if(tmpG < 100){
		                        tmpSGSI = "000" + Integer.toString(tmpG);
		                    }
		                    if (UtilValidate.isNotEmpty(idPrefix)){
		                    	tmpSGSI = idPrefix.concat(tmpSGSI);
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
		                if (reqAmount) {
		                	tmpOISG.set("quantity", BigDecimal.ONE);
		                	tmpOISG.set("amount", actualExportedQuantity);
		                } else {
		                	tmpOISG.set("quantity", actualExportedQuantity);
		                }
		                
		                tmpOISG.set("quantityNotAvailable", BigDecimal.ZERO);
		                java.sql.Timestamp tmpDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
		                tmpOISG.set("reservedDatetime", tmpDate);
		                tmpOISG.set("createdDatetime", tmpDate);
		                tmpOISG.set("promisedDatetime", tmpDate);
		                tmpOISG.create();
		                // 	update InventoryItemDetail
		                GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		                tmpInvDetail.set("inventoryItemId", strInventoryItemId);
		                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		                tmpInvDetail.set("effectiveDate", tmpDate);
		                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
		                if (reqAmount) {
		                	tmpInvDetail.set("availableToPromiseDiff", new BigDecimal(-1));
		                } else {
		                	tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.negate());
		                }
		                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("transferId", transferId);
		                tmpInvDetail.set("transferItemSeqId", transferItemSeqId);
		                tmpInvDetail.set("shipGroupSeqId", tmpSGSI);
		                tmpInvDetail.create();
		            } catch (Exception e) {
		                return ServiceUtil.returnError(e.toString());
		            }
				    
					Map<String, Object> issuePara = FastMap.newInstance();
					issuePara.put("deliveryId", deliveryId);
					issuePara.put("deliveryItemSeqId", deliveryItemSeqId);
					issuePara.put("context", ctx);
					issuePara.put("locale", locale);
					if (reqAmount) {
						issuePara.put("quantity", BigDecimal.ONE);
						issuePara.put("weight", quantityToIssuse);
					} else {
						issuePara.put("quantity", quantityToIssuse);
					}
					try {
						DeliveryHelper.issuseDelivery(issuePara);
					} catch (Exception e) {
					    Debug.log(e.getStackTrace().toString(), module);
					    return ServiceUtil.returnError("OLBIUS: issuseDelivery error");
					}
					if (reqAmount) {
						parameters.put("actualExportedQuantity", BigDecimal.ONE);
						is.updateExportedQuantity(parameters);
						parameters.put("actualExportedAmount", actualExportedQuantity);
						is.updateExportedAmount(parameters);
					} else {
						parameters.put("actualExportedQuantity", actualExportedQuantity);
						is.updateExportedQuantity(parameters);
					}
					
					List<GenericValue> deliveryByTransferItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", deliveryItem.get("fromTransferId"), "fromTransferItemSeqId", deliveryItem.get("fromTransferItemSeqId"))), null, null, null, false);
					BigDecimal totalItemQuantity = BigDecimal.ZERO;
					String field = "actualExportedQuantity";
					String fieldInit = "quantity";
					if (reqAmount) {
						field = "actualExportedAmount";
						fieldInit = "amount";
					}
					if (!deliveryByTransferItem.isEmpty()){
						for (GenericValue dlvItem : deliveryByTransferItem){
							if (UtilValidate.isNotEmpty(dlvItem.getBigDecimal(field))) {
								totalItemQuantity = totalItemQuantity.add(dlvItem.getBigDecimal(field));
							}
						}
						if (totalItemQuantity.compareTo(tmpTI.getBigDecimal(fieldInit)) == 0){
							Map<String, Object> mapChangeStatus = FastMap.newInstance();
							mapChangeStatus.put("transferId", deliveryItem.get("fromTransferId"));
							mapChangeStatus.put("transferItemSeqId", deliveryItem.get("fromTransferItemSeqId"));
							mapChangeStatus.put("fromStatusId", tmpTI.getString("statusId"));
							mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
							mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
							try {
								mapChangeStatus.put("statusId", "TRANS_ITEM_EXPORTED");
								dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("Change trasfer item status Error!");
							}
						}
					}
				}
			} else if ("DELI_ITEM_EXPORTED".equals(deliveryItem.getString("statusId"))){
				try {
					if (reqAmount) {
						parameters.put("actualDeliveredQuantity", BigDecimal.ONE);
						is.updateDeliveredQuantity(parameters);
						parameters.put("actualDeliveredAmount", actualDeliveredQuantity);
						is.updateDeliveredAmount(parameters);
					} else {
						parameters.put("actualDeliveredQuantity", actualDeliveredQuantity);
						is.updateDeliveredQuantity(parameters);
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
							BigDecimal quantityExported = actualExportedQuantity;
							BigDecimal quantityDelivered = actualDeliveredQuantity;
							String inventoryItemIdIssuanced = deliveryItem.getString("inventoryItemId");
							List<GenericValue> listItemIssuances = delegator.findList("TransferItemIssuance", EntityCondition.makeCondition(UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId, "shipmentId", delivery.get("shipmentId"), "inventoryItemId", inventoryItemIdIssuanced)), null, null, null, false);
							String shipmentItemSeqId = null;
							if (!listItemIssuances.isEmpty()){
								shipmentItemSeqId = listItemIssuances.get(0).getString("shipmentItemSeqId");
							}
							mapInv.put("productId", productId);
							mapInv.put("facilityId", (String)delivery.get("destFacilityId"));
							mapInv.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
							mapInv.put("userLogin", system);
							mapInv.put("expireDate", invItemFrom.get("expireDate"));
							mapInv.put("datetimeReceived", invItemFrom.get("datetimeReceived"));
							mapInv.put("datetimeManufactured", invItemFrom.get("datetimeManufactured"));
							mapInv.put("unitCost", invItemFrom.get("unitCost"));
							mapInv.put("purCost", invItemFrom.get("purCost"));
							if (reqAmount) {
								mapInv.put("quantityAccepted", BigDecimal.ONE);
								mapInv.put("amountAccepted", quantityDelivered);
							} else {
								mapInv.put("quantityAccepted", quantityDelivered);
							}
							mapInv.put("shipmentId", delivery.get("shipmentId"));
							mapInv.put("shipmentItemSeqId", shipmentItemSeqId);
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
								return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error!");
							}
							// up date delivery item status
							List<GenericValue> deliveryByTransferItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("fromTransferId", deliveryItem.get("fromTransferId"), "fromTransferItemSeqId", deliveryItem.get("fromTransferItemSeqId"))), null, null, null, false);
							BigDecimal quantityExportedTmp = BigDecimal.ZERO;
							BigDecimal quantityApprovedTmp = BigDecimal.ZERO;
							BigDecimal quantityCreatedTmp = BigDecimal.ZERO;
							BigDecimal quantityDeliveredTmp = BigDecimal.ZERO;
							if (!deliveryByTransferItem.isEmpty()){
								for (GenericValue dlvItem : deliveryByTransferItem){
									if ("DELI_ITEM_CREATED".equals(dlvItem.getString("statusId"))){
										if (reqAmount) {
											quantityCreatedTmp = quantityCreatedTmp.add(dlvItem.getBigDecimal("amount"));
										} else {
											quantityCreatedTmp = quantityCreatedTmp.add(dlvItem.getBigDecimal("quantity"));
										}
									} else if ("DELI_ITEM_APPROVED".equals(dlvItem.getString("statusId"))){
										if (reqAmount) {
											quantityApprovedTmp = quantityApprovedTmp.add(dlvItem.getBigDecimal("amount"));
										} else {
											quantityApprovedTmp = quantityApprovedTmp.add(dlvItem.getBigDecimal("quantity"));
										}
									} else if ("DELI_ITEM_EXPORTED".equals(dlvItem.getString("statusId"))){	
										if (reqAmount) {
											quantityExportedTmp = quantityExportedTmp.add(dlvItem.getBigDecimal("actualExportedAmount"));
										} else {
											quantityExportedTmp = quantityExportedTmp.add(dlvItem.getBigDecimal("actualExportedQuantity"));
										}
									} else if ("DELI_ITEM_DELIVERED".equals(dlvItem.getString("statusId"))){
										if (reqAmount) {
											quantityDeliveredTmp = quantityDeliveredTmp.add(dlvItem.getBigDecimal("actualDeliveredAmount"));
										} else {
											quantityDeliveredTmp = quantityDeliveredTmp.add(dlvItem.getBigDecimal("actualDeliveredQuantity"));
										}
									}
								}
							}
							BigDecimal initQty = tmpTI.getBigDecimal("quantity");
							if (reqAmount) initQty = tmpTI.getBigDecimal("amount");
							if (quantityDeliveredTmp.compareTo(initQty) == 0){
								if (quantityCreatedTmp.compareTo(BigDecimal.ZERO) == 0 && quantityApprovedTmp.compareTo(BigDecimal.ZERO) == 0 && quantityExportedTmp.compareTo(BigDecimal.ZERO) == 0){
									Map<String, Object> mapChangeStatus = FastMap.newInstance();
									mapChangeStatus.put("transferId", transferId);
									mapChangeStatus.put("transferItemSeqId", transferItemSeqId);
									mapChangeStatus.put("fromStatusId", tmpTI.getString("statusId"));
									mapChangeStatus.put("statusDateTime", UtilDateTime.nowTimestamp());
									mapChangeStatus.put("userLogin", (GenericValue)context.get("userLogin"));
									mapChangeStatus.put("statusId", "TRANS_ITEM_DELIVERED");
									try {
										dispatcher.runSync("changeTransferItemStatus", mapChangeStatus);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("Change trasfer item status Error!");
									}
								}
							}
						} else {
							Debug.logError("TransferItemIssuance not found", module);
				            return ServiceUtil.returnError("OLBIUS: issuance not found");
						}
					} else {
						Debug.logError("TransferItemShipGroup not found", module);
			            return ServiceUtil.returnError("OLBIUS: item ship group not found");
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		Map<String, Object> result = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "updateDeliverySuccessfully", locale));
		delivery.refresh();
		String deliveryStatusId = (String)delivery.get("statusId");
		result.put("deliveryTypeId", deliveryTypeId);
		result.put("deliveryStatusId", deliveryStatusId);
		result.put("transferId", delivery.getString("transferId"));
		result.put("deliveryId", deliveryId);
		result.put("deliveryItemSeqId", deliveryItemSeqId);
		return result;
	}
	
	public static Map<String, Object> getDeliveryTotalWeight(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String deliveryId = (String)context.get("deliveryId");
        GenericValue delivery = null;
        BigDecimal totalWeight = BigDecimal.ZERO;
        String defaultWeightUomId = null;
        try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			if (delivery == null){
				return ServiceUtil.returnError("Delivery not found");
			}
			List<GenericValue> listItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
			defaultWeightUomId = delivery.getString("defaultWeightUomId");
			if (!listItems.isEmpty()){
				if (defaultWeightUomId == null){
					for (GenericValue item : listItems){
						GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")));
						defaultWeightUomId = orderItem.getString("weightUomId");
						break;
					}
				}
				for (GenericValue item : listItems){
					BigDecimal weightTmp = BigDecimal.ZERO;
					GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")));
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", orderItem.getString("productId"))); 
					String baseUom = product.getString("quantityUomId");
					String orderUom = orderItem.getString("quantityUomId");
					BigDecimal convertBase = BigDecimal.ONE;
					convertBase = LogisticsProductUtil.getConvertPackingNumber(delegator, product.getString("productId"), orderUom, baseUom);
					BigDecimal weight = BigDecimal.ZERO;
					weight = product.getBigDecimal("weight");
					if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0){
						weight = product.getBigDecimal("productWeight");
					}
					if (weight != null && weight.compareTo(BigDecimal.ZERO) > 0){
						weightTmp = weightTmp.add(weight.multiply(convertBase));
					}
					String weightUomBase = product.getString("weightUomId");
					GenericValue uomConversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", defaultWeightUomId, "uomIdTo", weightUomBase));
					if (uomConversion != null){
						weightTmp = weightTmp.divide(uomConversion.getBigDecimal("conversionFactor"), decimals, rounding);
					} else {
						uomConversion = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", weightUomBase, "uomIdTo", defaultWeightUomId));
						if (uomConversion != null){
							weightTmp = weightTmp.multiply(uomConversion.getBigDecimal("conversionFactor"));
						}
					}
					if (item.getBigDecimal("actualExportedQuantity") != null){
						weightTmp = weightTmp.multiply(item.getBigDecimal("actualExportedQuantity"));
					} else {
						weightTmp = weightTmp.multiply(orderItem.getBigDecimal("quantity"));
					}
					totalWeight = totalWeight.add(weightTmp);
				}
			} 
        }catch(GenericEntityException e){ 
	        	e.printStackTrace();
        }
        result.put("weightUomId", defaultWeightUomId);
        result.put("totalWeight", totalWeight);
		return result;
	}
	public static Map<String, Object> quickCreateDelivery(DispatchContext ctx, Map<String, ? extends Object> context)  throws GenericEntityException, GenericServiceException{
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String orderId = (String)context.get("orderId");
        GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
        String orderTypeId = order.getString("orderTypeId");
        Locale locale = (Locale)context.get("locale");
        String facilityId = (String)context.get("facilityId");
        String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
        String carrierPartyId = (String)context.get("carrierPartyId");
        if (UtilValidate.isEmpty(facilityId))
            facilityId = order.getString("originFacilityId");
        GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
        String facilityTypeId = facility.getString("facilityTypeId");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        if ("SALES_ORDER".equals(orderTypeId)){
        	List<GenericValue> listShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
        	if (UtilValidate.isNotEmpty(shipmentMethodTypeId) && UtilValidate.isNotEmpty(carrierPartyId)){
        		for (GenericValue item : listShipGroups) {
        			if (UtilValidate.isEmpty(item.getString("shipmentMethodTypeId")) && UtilValidate.isEmpty(item.getString("carrierPartyId"))){
        				item.put("shipmentMethodTypeId", shipmentMethodTypeId);
            			item.put("carrierPartyId", carrierPartyId);
            			item.store();
        			}
    			}
        	}
        	GenericValue orderShipGroup = listShipGroups.get(0);
            String contactMech = orderShipGroup.getString("contactMechId");
            List<GenericValue> customers = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "SHIP_TO_CUSTOMER")), null, null, null, false);
            String partyIdTo = null;
            Timestamp estimatedStartDateTmp = null;
            Timestamp estimatedArrivalDateTmp = null;
            
            String weightUomDefault = null;
            if (!customers.isEmpty()){
            	partyIdTo = customers.get(0).getString("partyId");
            }
            
            List<GenericValue> pratys = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
        	String partyIdFrom = pratys.get(0).getString("partyId");
        	
            List<GenericValue> listOrderItems = LogisticsProductUtil.getOrderItemRemains(delegator, orderId, facilityTypeId);
            List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();

            if (!listOrderItems.isEmpty()){
            	weightUomDefault = "WT_kg";
            	for (GenericValue orderItem : listOrderItems){
            		Map<String, String> mapOrderItem = FastMap.newInstance();
            		mapOrderItem.put("orderId", orderId);
            		mapOrderItem.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
            		GenericValue objOrderItem = null;
					try {
						objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId")));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne OrderItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					String productId = objOrderItem.getString("productId");
					if (ProductUtil.isWeightProduct(delegator, productId)){
						mapOrderItem.put("quantity", BigDecimal.ONE.toString());
						BigDecimal amount = orderItem.getBigDecimal("quantity").multiply(objOrderItem.getBigDecimal("selectedAmount"));
						mapOrderItem.put("amount", amount.toString());
					} else {
						mapOrderItem.put("quantity", orderItem.getBigDecimal("quantity").toString());
						mapOrderItem.put("amount", BigDecimal.ZERO.toString());
					}
            		
            		listItems.add(mapOrderItem);
            		if (orderItem.getTimestamp("estimatedDeliveryDate") != null && estimatedStartDateTmp == null){
            			estimatedStartDateTmp = orderItem.getTimestamp("estimatedDeliveryDate");
            		} else if (estimatedStartDateTmp != null){
            			orderItem.put("estimatedDeliveryDate", estimatedStartDateTmp);
            			GenericValue updateItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId")));
            			updateItem.put("estimatedDeliveryDate", estimatedStartDateTmp);
            			updateItem.store();
            		} else if (orderItem.getTimestamp("shipAfterDate") != null){
            			estimatedStartDateTmp = orderItem.getTimestamp("shipAfterDate");
            			if (orderItem.getTimestamp("shipBeforeDate") != null && estimatedArrivalDateTmp == null){
            				estimatedArrivalDateTmp = orderItem.getTimestamp("shipBeforeDate");
            			}
            		}
            	}
            }
            if (estimatedStartDateTmp == null){
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DeliveryDateNotFound", locale));
            }
            if (estimatedArrivalDateTmp == null){
            	estimatedArrivalDateTmp = estimatedStartDateTmp; 
            }
            // get default facility
            LocalDispatcher dispathcher = ctx.getDispatcher();
            Map<String, Object> mapFac = FastMap.newInstance();
    		List<GenericValue> listAllConditions = new ArrayList<GenericValue>();
    		List<GenericValue> listSortFields = new ArrayList<GenericValue>();
    		EntityFindOptions opts = new EntityFindOptions();
    		Map<String, String[]> parameters = FastMap.newInstance();
    		String[] listOrderId = new String[1];
    		listOrderId[0] = orderId;
    		parameters.put("orderId", listOrderId);
    		mapFac.put("listAllConditions", listAllConditions);
    		mapFac.put("listSortFields", listSortFields);
    		mapFac.put("opts", opts);
    		mapFac.put("parameters", parameters);
    		mapFac.put("userLogin", (GenericValue)context.get("userLogin"));
    		String contactMechFac = (String)context.get("contactMechId");
    		if (UtilValidate.isEmpty(contactMechFac)) {
    			List<GenericValue> listFacilityContactMechs = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION")), null, null, null, false);
    			listFacilityContactMechs = EntityUtil.filterByDate(listFacilityContactMechs);
    			if (!listFacilityContactMechs.isEmpty()){
    				contactMechFac = listFacilityContactMechs.get(0).getString("contactMechId");
    			}
    		}
    		Map<String, Object> mapDlv = FastMap.newInstance();
            if (!listOrderItems.isEmpty()){
            	Map<String, Object> mapDelivery = FastMap.newInstance();
            	mapDelivery.put("orderId", orderId);
            	mapDelivery.put("deliveryTypeId", "DELIVERY_SALES");
            	mapDelivery.put("currencyUomId", order.getString("currencyUom"));
            	mapDelivery.put("statusId", "DLV_CREATED");
            	mapDelivery.put("originContactMechId", contactMechFac);
            	mapDelivery.put("partyIdFrom", partyIdFrom);
            	mapDelivery.put("partyIdTo", partyIdTo);
            	mapDelivery.put("destContactMechId", contactMech);
            	mapDelivery.put("originFacilityId", facilityId);
            	mapDelivery.put("deliveryDate", estimatedStartDateTmp);
            	mapDelivery.put("estimatedStartDate", estimatedStartDateTmp);
            	mapDelivery.put("estimatedArrivalDate", estimatedArrivalDateTmp);
            	mapDelivery.put("no", null);
            	mapDelivery.put("defaultWeightUomId", weightUomDefault);
            	mapDelivery.put("listOrderItems", listItems);
            	mapDelivery.put("conversionFactor", context.get("conversionFactor"));
            	mapDelivery.put("userLogin", context.get("userLogin"));
            	try {
            		mapDlv = dispathcher.runSync("createDelivery", mapDelivery);
//                    if(!"VND".equals(order.getString("currencyUom"))) {
//                        Map<String, Object> mapExchangedRateHistory = FastMap.newInstance();
//                        mapExchangedRateHistory.put("conversionFactor", conversionFactor);
//                        mapExchangedRateHistory.put("documentTypeId", "DELIVERY");
//                        mapExchangedRateHistory.put("currencyUomId", order.getString("currencyUom"));
//                        mapExchangedRateHistory.put("currencyUomIdTo", "VND"); //TODO fix me
//                        mapExchangedRateHistory.put("documentId", mapDlv.get("deliveryId"));
//                        mapExchangedRateHistory.put("userLogin", userLogin);
//                        dispathcher.runSync("createExchangedRateHistory", mapExchangedRateHistory);
//                    }
    			} catch (GenericServiceException e) {
    				return ServiceUtil.returnError("createDelivery error!");
    			}
            } else {
            	return ServiceUtil.returnError("Olbius - Order Item Not Found!");
            }
            String deliveryId = (String)mapDlv.get("deliveryId");
            result.put("deliveryId", deliveryId);
            result.put("orderId", orderId);
        } else if ("PURCHASE_ORDER".equals(orderTypeId)) {
            List<GenericValue> partyFroms = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
            List<GenericValue> partyVendors = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
            String partyIdTo = null;
            String partyIdFrom = null;
            String weightUomDefault = null;
            if (!partyFroms.isEmpty()){
            	partyIdTo = partyFroms.get(0).getString("partyId");
            }
            if (!partyVendors.isEmpty()){
            	partyIdFrom = partyVendors.get(0).getString("partyId");
            }
            List<GenericValue> listOrderItemShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
            Timestamp dateReceive = null;
            if (!listOrderItemShipGroups.isEmpty()){
            	for (GenericValue shipGr : listOrderItemShipGroups){
            		if (shipGr.getTimestamp("shipByDate") != null){
            			dateReceive = shipGr.getTimestamp("shipByDate");
            		}
            	}
            }
            if (dateReceive == null){
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DeliveryDateNotFound", locale));
            }
            List<GenericValue> listOrderItems = LogisticsProductUtil.getOrderItemRemains(delegator, orderId, null);
            List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
            if (!listOrderItems.isEmpty()){
            	weightUomDefault = "WT_kg";
            	for (GenericValue orderItem : listOrderItems){
            		GenericValue productTmp = delegator.findOne("Product", false, UtilMisc.toMap("productId", orderItem.getString("productId")));
            		Map<String, String> mapOrderItem = FastMap.newInstance();
            		mapOrderItem.put("orderId", orderId);
            		mapOrderItem.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
            		mapOrderItem.put("quantityUomId", productTmp.getString("quantityUomId"));
            		mapOrderItem.put("quantity", orderItem.getBigDecimal("quantity").toString());
            		mapOrderItem.put("amount", orderItem.getBigDecimal("selectedAmount").toString());
            		listItems.add(mapOrderItem);
            	}
            }
            
            // get default facility
            LocalDispatcher dispathcher = ctx.getDispatcher();
    		String contactMechId = (String)context.get("contactMechId");
    		if (UtilValidate.isEmpty(contactMechId)){
    			Map<String, Object> mapFindContactMech = FastMap.newInstance();
    			mapFindContactMech.put("facilityId", facilityId);
    			mapFindContactMech.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
    			mapFindContactMech.put("userLogin", userLogin);
    			Map<String, Object> mapTmp = dispathcher.runSync("getFacilityContactMechs", mapFindContactMech);
    			@SuppressWarnings({ "unchecked" })
				List<GenericValue> listFacilityContactMechs = (List<GenericValue>)mapTmp.get("listFacilityContactMechs");
    			if (!listFacilityContactMechs.isEmpty()){
    				contactMechId = listFacilityContactMechs.get(0).getString("contactMechId");
    			}
    		}

    		Map<String, Object> mapDlv = FastMap.newInstance();
            if (!listOrderItems.isEmpty()){
            	Map<String, Object> mapDelivery = FastMap.newInstance();
            	mapDelivery.put("orderId", orderId);
            	mapDelivery.put("deliveryTypeId", "DELIVERY_PURCHASE");
            	mapDelivery.put("currencyUomId", order.getString("currencyUom"));
            	mapDelivery.put("statusId", "DLV_CREATED");
            	mapDelivery.put("destContactMechId", contactMechId);
            	mapDelivery.put("partyIdFrom", partyIdFrom);
            	mapDelivery.put("partyIdTo", partyIdTo);
            	mapDelivery.put("destFacilityId", facilityId);
            	mapDelivery.put("deliveryDate", dateReceive);
            	mapDelivery.put("estimatedStartDate", dateReceive);
            	mapDelivery.put("estimatedArrivalDate", dateReceive);
            	mapDelivery.put("no", null);
            	mapDelivery.put("defaultWeightUomId", weightUomDefault);
            	mapDelivery.put("listOrderItems", listItems);
            	mapDelivery.put("conversionFactor", context.get("conversionFactor"));
            	mapDelivery.put("userLogin", userLogin);
            	try {
            		mapDlv = dispathcher.runSync("createDelivery", mapDelivery);

//            		if(!"VND".equals(order.getString("currencyUom"))) {
//            		    Map<String, Object> mapExchangedRateHistory = FastMap.newInstance();
//            		    mapExchangedRateHistory.put("conversionFactor", conversionFactor);
//            		    mapExchangedRateHistory.put("documentTypeId", "DELIVERY");
//            		    mapExchangedRateHistory.put("currencyUomId", order.getString("currencyUom"));
//            		    mapExchangedRateHistory.put("currencyUomIdTo", "VND"); //TODO fix me
//                        mapExchangedRateHistory.put("documentId", mapDlv.get("deliveryId"));
//                        mapExchangedRateHistory.put("userLogin", userLogin);
//                        dispathcher.runSync("createExchangedRateHistory", mapExchangedRateHistory);
//                    }
    			} catch (GenericServiceException e) {
    				return ServiceUtil.returnError("createDelivery error!");
    			}
            } else {
            	return ServiceUtil.returnError("Olbius - Order Item Not Found!");
            }
            String deliveryId = (String)mapDlv.get("deliveryId");
            result.put("deliveryId", deliveryId);
            result.put("orderId", orderId);
        }
        
        return result;
	}
	
	public static Map<String, Object> quickCreateTransferDelivery(DispatchContext ctx, Map<String, ? extends Object> context)  throws GenericEntityException, GenericServiceException{
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String transferId = (String)context.get("transferId");
        GenericValue transfer = delegator.findOne("TransferHeader", false, UtilMisc.toMap("transferId", transferId));
        Locale locale = (Locale)context.get("locale");
        String facilityId = transfer.getString("originFacilityId");
        
        List<GenericValue> listShipGroups = delegator.findList("TransferItemShipGroup", EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId), null, null, null, false);
        if (listShipGroups.isEmpty()) return ServiceUtil.returnError("OLBIUS: cannot found shipgroup for transfer " + transferId);
        
        GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
        String facilityTypeId = facility.getString("facilityTypeId");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
    	GenericValue orderShipGroup = listShipGroups.get(0);
    	
        String destContactMechId = orderShipGroup.getString("contactMechId");
        String partyIdTo = null;
        String partyIdFrom = null;
        if (UtilValidate.isNotEmpty(transfer.getString("transferTypeId")) && "TRANS_INTERNAL".equals(transfer.getString("transferTypeId"))){
        	String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        	partyIdFrom = company;
        	partyIdTo = company;
        }
        Timestamp estimatedStartDateTmp = null;
        if (UtilValidate.isNotEmpty(transfer.getTimestamp("transferDate"))){
        	estimatedStartDateTmp = transfer.getTimestamp("transferDate");
        } else if (UtilValidate.isNotEmpty(transfer.getTimestamp("shipAfterDate"))){
        	estimatedStartDateTmp = transfer.getTimestamp("shipAfterDate");
        }
        
        Timestamp estimatedArrivalDateTmp = null;
        if (UtilValidate.isNotEmpty(transfer.getTimestamp("transferDate"))){
        	estimatedArrivalDateTmp = transfer.getTimestamp("transferDate");
        } else if (UtilValidate.isNotEmpty(transfer.getTimestamp("shipBeforeDate"))){
        	estimatedArrivalDateTmp = transfer.getTimestamp("shipBeforeDate");
        }
        
        String weightUomDefault = null;
    	
        List<GenericValue> listTransferItems = LogisticsProductUtil.getTransferItemRemains(delegator, transferId, facilityTypeId, partyIdFrom);
        List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();

        if (!listTransferItems.isEmpty()){
        	GenericValue productTmp = delegator.findOne("Product", false, UtilMisc.toMap("productId", listTransferItems.get(0).getString("productId")));
        	weightUomDefault = productTmp.getString("weightUomId");
        	if (weightUomDefault == null){
        		weightUomDefault = "WT_kg";
        	} 
        	for (GenericValue transferItem : listTransferItems){
        		Map<String, String> mapTransferItem = FastMap.newInstance();
        		mapTransferItem.put("transferId", transferId);
        		mapTransferItem.put("transferItemSeqId", transferItem.getString("transferItemSeqId"));
        		mapTransferItem.put("quantity", transferItem.getBigDecimal("quantity").toString());
        		if (UtilValidate.isNotEmpty(transferItem.get("amount"))) {
        			mapTransferItem.put("quantity", transferItem.getBigDecimal("amount").toString());
				}
        		listItems.add(mapTransferItem);
        	}
        }
        if (estimatedStartDateTmp == null){
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DeliveryDateNotFound", locale));
        }
        if (estimatedArrivalDateTmp == null){
        	estimatedArrivalDateTmp = estimatedStartDateTmp; 
        }
        String originContactMechId = null;
		List<GenericValue> listFacilityContactMechs = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION")), null, null, null, false);
		listFacilityContactMechs = EntityUtil.filterByDate(listFacilityContactMechs);
		if (!listFacilityContactMechs.isEmpty()){
			originContactMechId = listFacilityContactMechs.get(0).getString("contactMechId");
		}
		Map<String, Object> mapDlv = FastMap.newInstance();
        if (!listTransferItems.isEmpty()){
        	LocalDispatcher dispathcher = ctx.getDispatcher();
        	Map<String, Object> mapDelivery = FastMap.newInstance();
        	mapDelivery.put("transferId", transferId);
        	mapDelivery.put("deliveryTypeId", "DELIVERY_TRANSFER");
        	mapDelivery.put("statusId", "DLV_CREATED");
        	mapDelivery.put("originContactMechId", originContactMechId);
        	mapDelivery.put("partyIdFrom", partyIdFrom);
        	mapDelivery.put("partyIdTo", partyIdTo);
        	mapDelivery.put("destContactMechId", destContactMechId);
        	mapDelivery.put("originFacilityId", facilityId);
        	mapDelivery.put("deliveryDate", estimatedStartDateTmp);
        	mapDelivery.put("estimatedStartDate", estimatedStartDateTmp);
        	mapDelivery.put("estimatedArrivalDate", estimatedArrivalDateTmp);
        	mapDelivery.put("no", null);
        	mapDelivery.put("defaultWeightUomId", weightUomDefault);
        	mapDelivery.put("listTransferItems", listItems);
        	mapDelivery.put("userLogin", (GenericValue)context.get("userLogin"));
        	try {
        		mapDlv = dispathcher.runSync("createTransferDelivery", mapDelivery);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("createTransferDelivery error! " + e.toString());
			}
        } else {
        	return ServiceUtil.returnError("Olbius - Transfer Item Not Found!");
        }
        String deliveryId = (String)mapDlv.get("deliveryId");
        result.put("deliveryId", deliveryId);
        result.put("transferId", transferId);
        return result; 
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getDeliveryEntry(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		EntityCondition condOwner = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId);
		listAllConditions.add(condOwner);
		EntityCondition condStatus = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ENTRY_CANCELLED");
		listAllConditions.add(condStatus);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("DeliveryEntryDetailAndOrder", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getDeliveryEntry service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> updateDeliveryEntry(DispatchContext ctx, Map<String, Object> context){
		
		//Parameters
		String deliveryEntryId = (String) context.get("deliveryEntryId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		
		//When DE is shipped then all shipment in it is shipped
		List<GenericValue> listShipment = null;
		if("DELI_ENTRY_SHIPPING".equals(statusId)){
			try {
				listShipment = delegator.findList("Shipment", EntityCondition.makeCondition("deliveryEntryId", deliveryEntryId), null, null, null, false);
				for(GenericValue item : listShipment){
					item.put("statusId", "SHIPMENT_SHIPPED");
					item.store();
				}
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "UpdateDeliveryEntryFail", locale));
			}
			
		}
		
		//Update status for Delivery Entry
		GenericValue deliveryEntry = null;
		try {
			deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "UpdateDeliveryEntryFail", locale));
		}
		deliveryEntry.put("statusId", statusId);
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			deliveryEntry.put("fromDate", new Timestamp((Long)context.get("fromDate")));
		}
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			deliveryEntry.put("thruDate", new Timestamp((Long)context.get("thruDate")));
		}
		if (UtilValidate.isNotEmpty(context.get("shipCost"))) {
			deliveryEntry.put("shipCost", new BigDecimal((String)context.get("shipCost")));
		}
		try {
			deliveryEntry.store();
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "UpdateDeliveryEntryFail", locale));
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "UpdateSuccessfully", locale));
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
			return ServiceUtil.returnError(errMsg);
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
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DeliveryEntryNotFound", locale));
		}
		if (listShipmentItems.isEmpty()){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "NoShipmentItemFound", locale));
		}
		List<String> listShipmentIds = new ArrayList<String>();
		for (Map<String, String> item : listShipmentItems){
			if (!listShipmentIds.contains(item.get("shipmentId")) && item.get("shipmentId") != null){
				listShipmentIds.add(item.get("shipmentId"));
			}
		}
		String deliveryWeightUomId = deliveryEntry.getString("weightUomId");
		if (deliveryWeightUomId == null) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DeliveryEntryWeightUomNotFound", locale));
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
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BoxTypeNotFound", locale));
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
	
	public static Map<String, Object> checkDeliveryExisted(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String deliveryId = (String)context.get("deliveryId");
    	Boolean existed = false; 
    	if (deliveryId != null){
    		GenericValue deliveryTest = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    		if (deliveryTest != null){
    			existed = true;
    			return ServiceUtil.returnError("DELIVERY_ID_EXISTED");
    		}
    	} else {
    		existed = false;
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("existed", existed);
    	return mapReturn;
	}
	
	public static Map<String, Object> deleteDeliveryEntry(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	String deliveryEntryId = (String)context.get("deliveryEntryId");
    	List<GenericValue> listShipment = delegator.findList("Shipment", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId)), null, null, null, false);
    	GenericValue deliveryEntry = delegator.findOne("DeliveryEntry", UtilMisc.toMap("deliveryEntryId", deliveryEntryId), false);
    	// Remove in shipment
    	if(!listShipment.isEmpty()){
    		for (GenericValue shipment : listShipment) {
    			Map<String, Object> mapDeleteDlvEntryShipment = FastMap.newInstance();
    			mapDeleteDlvEntryShipment.put("deliveryEntryId", deliveryEntryId);
    			mapDeleteDlvEntryShipment.put("shipmentId", shipment.getString("shipmentId"));
    			mapDeleteDlvEntryShipment.put("userLogin", (GenericValue)context.get("userLogin"));
    			try {
					dispatcher.runSync("removeDeliveryEntryShipment", mapDeleteDlvEntryShipment);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS-LOGISTICS: run removeDeliveryEntryShipment error!");
				}
    		}
    	}
    	
    	List<GenericValue> listDlvEntryRoles = delegator.findList("DeliveryEntryRole", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId)), null, null, null, false);
    	for (GenericValue item : listDlvEntryRoles) {
    		Map<String, Object> mapDeleteDlvEntryRole = FastMap.newInstance();
    		mapDeleteDlvEntryRole.put("deliveryEntryId", deliveryEntryId);
    		mapDeleteDlvEntryRole.put("roleTypeId", item.getString("roleTypeId"));
    		mapDeleteDlvEntryRole.put("userLogin", (GenericValue)context.get("userLogin"));
    		try {
				dispatcher.runSync("removeDeliveryEntryRole", mapDeleteDlvEntryRole);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS-LOGISTICS: run removeDeliveryEntryRole error!");
			}
		}
		
		List<GenericValue> listVehicles = delegator.findList("DeliveryEntryFixedAsset", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId)), null, null, null, false);
		if (!listVehicles.isEmpty()){
			for (GenericValue vehicle : listVehicles) {
				Map<String, Object> mapDeleteDlvEntryVehicle = FastMap.newInstance();
				mapDeleteDlvEntryVehicle.put("deliveryEntryId", deliveryEntryId);
				mapDeleteDlvEntryVehicle.put("fixedAssetId", vehicle.getString("fixedAssetId"));
				mapDeleteDlvEntryVehicle.put("userLogin", (GenericValue)context.get("userLogin"));
				try {
					dispatcher.runSync("removeDeliveryEntryFixedAsset", mapDeleteDlvEntryVehicle);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS-LOGISTICS: run removeDeliveryEntryFixedAsset error!");
				}
			}
		}
		deliveryEntry.put("statusId", "DELI_ENTRY_CANCELLED");
    	delegator.store(deliveryEntry);
    	return mapReturn;
	}
	
	public static Map<String, Object> receiveInventoryFromDelivery(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String deliveryId = (String)context.get("deliveryId");
		String invStatusId = (String)context.get("inventoryStatusId");
		GenericValue delivery = null;
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("Delivery not Found");
		}
		String deliveryTypeId = null;
		
		if (delivery != null){
			deliveryTypeId = (String)delivery.get("deliveryTypeId");
			List<GenericValue> listItem = new ArrayList<GenericValue>();
			// Create shipment from delivery
			Map<String, Object> shipmentParam = FastMap.newInstance();
			shipmentParam.put("deliveryId", deliveryId);
			shipmentParam.put("userLogin", userLogin);
			Map<String, Object> mapShipment = FastMap.newInstance();
			try {
				mapShipment = dispatcher.runSync("createShipmentForPurchaseDelivery", shipmentParam);
			} catch (GenericServiceException e1) {
				return ServiceUtil.returnError("createShipmentForPurchaseDelivery Error!");
			}
			String shipmentId = (String)mapShipment.get("shipmentId");
			try {
				GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
				delivery.put("shipmentId", shipmentId);
				delivery.store();
				listItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
				// Edit by VietTB
 				if (!listItem.isEmpty()){
 					// edit split debit quantity
 					List<Map<String, Object>> listDlvItems = new ArrayList<Map<String, Object>>();
 					for (GenericValue item : listItem){
 						if (UtilValidate.isNotEmpty(item.get("debitQuantity"))) {
 							Map<String, Object> map1 = FastMap.newInstance();
 							map1.putAll(item);
 							map1.put("actualDeliveredQuantity", item.getBigDecimal("actualDeliveredQuantity").subtract(item.getBigDecimal("debitQuantity")));
 							listDlvItems.add(map1);
 							Map<String, Object> map2 = FastMap.newInstance();
 							map2.putAll(item);
 							map2.put("actualDeliveredQuantity", item.getBigDecimal("debitQuantity"));
 							map2.put("inventoryItemStatusId", "INV_DEBT_SUPPLIER");
 							listDlvItems.add(map2);
						} else if (UtilValidate.isNotEmpty(item.get("debitAmount"))) {
							Map<String, Object> map1 = FastMap.newInstance();
 							map1.putAll(item);
 							map1.put("actualDeliveredQuantity", item.getBigDecimal("actualDeliveredQuantity").subtract(item.getBigDecimal("debitQuantity")));
 							listDlvItems.add(map1);
 							Map<String, Object> map2 = FastMap.newInstance();
 							map2.putAll(item);
 							map2.put("actualDeliveredQuantity", BigDecimal.ONE);
 							map2.put("actualDeliveredAmount", item.getBigDecimal("debitAmount"));
 							map2.put("inventoryItemStatusId", "INV_DEBT_SUPPLIER");
 							listDlvItems.add(map2);
						} else {
							listDlvItems.add(item);
						}
 					}
 					
					for (Map<String, Object> item : listDlvItems){
						if (item.get("actualDeliveredQuantity") != null  && ((BigDecimal)item.get("actualDeliveredQuantity")).compareTo(BigDecimal.ZERO) > 0){
							GenericValue orderItem = delegator.findOne("OrderItem", false,
									UtilMisc.toMap("orderId", item.get("fromOrderId"), "orderItemSeqId", item.get("fromOrderItemSeqId")));
							String orderItemSeqId = (String)orderItem.get("orderItemSeqId");
							// receive inventory
							String lotId = null;
							if (UtilValidate.isNotEmpty(item.get("batch"))){
								lotId = (String)item.get("batch");
								GenericValue lot = delegator.findOne("Lot", false, UtilMisc.toMap("lotId", lotId));
								if (lot == null){
									lot = delegator.makeValue("Lot");
									lot.put("lotId", lotId);
									lot.put("creationDate", UtilDateTime.nowTimestamp());
									delegator.create(lot); 
								}
							}
							String locationId = null;
							if (UtilValidate.isNotEmpty(item.get("locationId"))) {
								locationId = (String)item.get("locationId");
							}
							String productId = (String)orderItem.get("productId");
							Map<String, Object> mapInv = new FastMap<String, Object>();
							mapInv.put("productId", productId);
							mapInv.put("locationId", locationId);
							mapInv.put("orderId", (String)orderItem.get("orderId"));
							mapInv.put("orderItemSeqId", orderItemSeqId);
							mapInv.put("lotId", lotId);
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
							mapInv.put("datetimeReceived", delivery.get("actualArrivalDate"));
							mapInv.put("quantityAccepted", (BigDecimal)item.get("actualDeliveredQuantity")); 
							mapInv.put("amountAccepted", (BigDecimal)item.get("actualDeliveredAmount"));
							mapInv.put("quantityExcess", BigDecimal.ZERO);
							mapInv.put("quantityRejected", BigDecimal.ZERO);
							mapInv.put("quantityQualityAssurance", BigDecimal.ZERO);
							
							mapInv.put("statusId", (String)item.get("inventoryItemStatusId"));
							mapInv.put("userLogin", system);
							
							List<GenericValue> orderRoleTos = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", delivery.getString("orderId"), "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
							String company = orderRoleTos.get(0).getString("partyId");
							mapInv.put("ownerPartyId", company);
							
							// Process exchange rate by VietTB
							GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", delivery.getString("orderId")));
							String currencyUom = orderHeader.getString("currencyUom");
							GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", company));
							String baseCurrencyUomId = partyAcctgPreference.getString("baseCurrencyUomId");
							// Calculate price by amount
							BigDecimal unitPrice = orderItem.getBigDecimal("unitPrice");
							if (ProductUtil.isWeightProduct(delegator, productId)) {
								BigDecimal amount = orderItem.getBigDecimal("selectedAmount");
								BigDecimal unitPriceInit = orderItem.getBigDecimal("unitPrice");
								unitPrice = unitPriceInit.divide(amount, decimals, rounding);
							}
							if (baseCurrencyUomId.equals(currencyUom)){
								mapInv.put("unitCost", unitPrice);
							} else {	
								BigDecimal totalPaymentApplied = BigDecimal.ZERO;
								BigDecimal totalDeliveredItem = BigDecimal.ZERO;
								BigDecimal totalDeliveryItem = BigDecimal.ZERO;
								BigDecimal avgExchangeRate = BigDecimal.ZERO;
								BigDecimal avgUnitPrice = BigDecimal.ZERO;
								List<GenericValue> listPayment = null;
						        try {
						        	List<String> paymentStatusId = new FastList<String>();
						        	paymentStatusId.add("PMNT_SENT");
						        	paymentStatusId.add("PMNT_CONFIRMED");
						        	EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
					                     EntityCondition.makeCondition("statusId", EntityOperator.IN, paymentStatusId),
						                     EntityCondition.makeCondition("paymentPreferenceId", EntityOperator.IN, com.olbius.accounting.invoice.InvoiceWorker.getOrderPaymentPreferenceIds(delegator, delivery.getString("orderId")))),
						                     EntityOperator.AND);
						        	listPayment = delegator.findList("PaymentAcctgTrans", condition, null, UtilMisc.toList("transactionDate"), null, false);
						        	
						        	if (listPayment != null)
						        	{
							        	for (GenericValue payment : listPayment)
							        	{
							        		totalPaymentApplied = totalPaymentApplied.add(payment.getBigDecimal("amount"));
							        	}
						        	}
						        	
						        	EntityConditionList<EntityExpr> conditionDelivery = EntityCondition.makeCondition(UtilMisc.toList(
						                     EntityCondition.makeCondition("fromOrderId", EntityOperator.EQUALS, delivery.getString("orderId")),
						                     EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_DELIVERED"),
							                     EntityCondition.makeCondition("deliveryId", EntityOperator.NOT_EQUAL, deliveryId )),
							                     EntityOperator.AND);
						        	List<GenericValue> listDeliveredItem = delegator.findList("DeliveryItemView", conditionDelivery, null, null, null, false);
						        	
						        	if (listDeliveredItem != null && !listDeliveredItem.isEmpty())
						        	{
							        	for (GenericValue deliveredItem : listDeliveredItem)
							        	{
							        		String productDiId = (String)deliveredItem.get("productId");
											BigDecimal unitDiPrice = deliveredItem.getBigDecimal("unitPrice");
											if (ProductUtil.isWeightProduct(delegator, productDiId)) {
												BigDecimal amountDi = deliveredItem.getBigDecimal("selectedAmount");
												BigDecimal unitDiPriceInit = deliveredItem.getBigDecimal("unitPrice");
												unitDiPrice = unitDiPriceInit.divide(amountDi, decimals, rounding);
											}			
											
							        		BigDecimal amount = unitDiPrice.multiply(deliveredItem.getBigDecimal("actualDeliveredQuantity"));
							        		totalDeliveredItem = totalDeliveredItem.add(amount);
							        	}
						        	}	
						        	
						        	List<GenericValue> listItemDelivery = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
						        	
						        	for (GenericValue deliveryItem : listItemDelivery)
						        	{
						        								        		
						        		String productDiId = (String)deliveryItem.get("productId");
										
										BigDecimal unitDiPrice = deliveryItem.getBigDecimal("unitPrice");
										if (ProductUtil.isWeightProduct(delegator, productDiId)) {
											BigDecimal amountDi = deliveryItem.getBigDecimal("selectedAmount");
											BigDecimal unitDiPriceInit = deliveryItem.getBigDecimal("unitPrice");
											unitDiPrice = unitDiPriceInit.divide(amountDi, decimals, rounding);
										}
										
						        		BigDecimal amount = unitDiPrice.multiply(deliveryItem.getBigDecimal("actualDeliveredQuantity"));
						        		totalDeliveryItem = totalDeliveryItem.add(amount);
						        	}
						        		BigDecimal deliveredItemDis = totalDeliveredItem; 
						        		BigDecimal deliveryItemDis = totalDeliveryItem; 
						        		BigDecimal totalAmount = BigDecimal.ZERO;
						        		String strBankId = "";
						        		Timestamp dtTransactionDate ;
						        		int i = 0;
						        		if (listPayment != null && !listPayment.isEmpty())
						        		{
						        			GenericValue payment = listPayment.get(i);
						        			BigDecimal paymentAmount = payment.getBigDecimal("amount");
							        		while (deliveredItemDis.compareTo(paymentAmount) > 0) 
							        		{
							        			deliveredItemDis = deliveredItemDis.subtract(paymentAmount);
							        			i++;
							        			payment = listPayment.get(i);
							        			paymentAmount = payment.getBigDecimal("amount");
							        		}
							        		BigDecimal  paymentAmountDis = paymentAmount.subtract(deliveredItemDis);
							        		BigDecimal convertValued = BigDecimal.ZERO;
							        		while (deliveryItemDis.compareTo(BigDecimal.ZERO) > 0)
							        		{
							        			deliveryItemDis = deliveryItemDis.subtract(paymentAmountDis);
							        			String strPaymentMethodID = payment.getString("paymentMethodId");
							        			dtTransactionDate = payment.getTimestamp("transactionDate");
							        			if (strPaymentMethodID != null)
							        			{
							        				GenericValue paymentMethod = delegator.findOne("PaymentMethod", false, UtilMisc.toMap("paymentMethodId", strPaymentMethodID));
							        				if (paymentMethod != null && !paymentMethod.isEmpty())
							        				{
							        					String strFinAccountId = paymentMethod.getString("finAccountId");
							        					GenericValue finAccount = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", strFinAccountId));
							        					if (finAccount != null && !finAccount.isEmpty())
							        					{
							        						strBankId = finAccount.getString("bankId");
							        					}
							        				}
							        			}
							        			if (deliveryItemDis.compareTo(BigDecimal.ZERO) < 0 )
							        			{
							        				convertValued = paymentAmountDis.add(deliveryItemDis);
							        			} else convertValued = paymentAmountDis;
							        			
							        			Map<String, Object> priceResults = FastMap.newInstance();
					                            try {
					                                priceResults = dispatcher.runSync("convertUomMoney", UtilMisc.<String, Object>toMap("uomId", currencyUom, "uomIdTo", baseCurrencyUomId, "originalValue", convertValued , "bankId", strBankId, "purposeEnumId", "EXTERNAL_CONVERSION", "asOfDate", dtTransactionDate, "defaultDecimalScale" , Long.valueOf(3) , "defaultRoundingMode" , "ROUND_HALF_UP"));
					                                if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
					                                    Debug.logWarning("Unable to convert " + currencyUom + " for product  " + baseCurrencyUomId , module);
					                                } 
					                            } catch (GenericServiceException e) {
					                                Debug.logError(e, module);
					                            }
						                            
							        			totalAmount = totalAmount.add( (BigDecimal)priceResults.get("convertedValue"));
							        			i++;
							        			if (i==listPayment.size())
							        			{
							        				if (deliveryItemDis.compareTo(BigDecimal.ZERO) > 0)
							        				{
							        					dtTransactionDate = null;
							                            try {
							                                priceResults = dispatcher.runSync("convertUomMoney", UtilMisc.<String, Object>toMap("uomId", currencyUom, "uomIdTo", baseCurrencyUomId, "originalValue", deliveryItemDis , "bankId", strBankId, "purposeEnumId", "EXTERNAL_CONVERSION", "asOfDate", dtTransactionDate, "defaultDecimalScale" , Long.valueOf(3) , "defaultRoundingMode" , "ROUND_HALF_UP"));
							                                if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
							                                    Debug.logWarning("Unable to convert " + currencyUom + " for product  " + baseCurrencyUomId , module);
							                                } 
							                            } catch (GenericServiceException e) {
							                                Debug.logError(e, module);
							                            }
							                            totalAmount = totalAmount.add( (BigDecimal)priceResults.get("convertedValue"));
							        				}
							        				break;
							        			}
							        			payment = listPayment.get(i);
							        			paymentAmountDis = payment.getBigDecimal("amount");
							        		}
							        		avgExchangeRate = totalAmount.divide(totalDeliveryItem, decimals, rounding );
							        		avgUnitPrice = unitPrice.multiply(avgExchangeRate).setScale(decimals, rounding);
						        		}
						        		else
						        		{
						        			List<GenericValue> listAllPayment = delegator.findList("Payment", EntityCondition.makeCondition("paymentPreferenceId", EntityOperator.IN, com.olbius.accounting.invoice.InvoiceWorker.getOrderPaymentPreferenceIds(delegator, delivery.getString("orderId"))), null, UtilMisc.toList("effectiveDate"), null, false);
						        			if (listAllPayment!= null && !listAllPayment.isEmpty())
						        			{
						        				GenericValue paymentAll = listAllPayment.get(0);
						        				dtTransactionDate = null;
						        				String strPaymentMethodID = paymentAll.getString("paymentMethodId");
						        				if (strPaymentMethodID != null)
							        			{
							        				GenericValue paymentMethod = delegator.findOne("PaymentMethod", false, UtilMisc.toMap("paymentMethodId", strPaymentMethodID));
							        				if (paymentMethod != null && !paymentMethod.isEmpty())
							        				{
							        					String strFinAccountId = paymentMethod.getString("finAccountId");
							        					GenericValue finAccount = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", strFinAccountId));
							        					if (finAccount != null && !finAccount.isEmpty())
							        					{
							        						strBankId = finAccount.getString("bankId");
							        					}
							        				}
							        			}
						        				Map<String, Object> priceResults = FastMap.newInstance();
					                            try {
					                                priceResults = dispatcher.runSync("convertUomMoney", UtilMisc.<String, Object>toMap("uomId", currencyUom, "uomIdTo", baseCurrencyUomId, "originalValue", unitPrice , "bankId", strBankId, "purposeEnumId", "EXTERNAL_CONVERSION", "asOfDate", dtTransactionDate, "defaultDecimalScale" , Long.valueOf(3) , "defaultRoundingMode" , "ROUND_HALF_UP"));
					                                if (ServiceUtil.isError(priceResults) || (priceResults.get("convertedValue") == null)) {
					                                    Debug.logWarning("Unable to convert " + currencyUom + " for product  " + baseCurrencyUomId , module);
					                                } 
					                            } catch (GenericServiceException e) {
					                                Debug.logError(e, module);
					                            }
					                            avgUnitPrice = (BigDecimal)priceResults.get("convertedValue");
						        			}
						        		}
						        } catch (GenericEntityException e) {
						            Debug.logError(e, "Trouble getting Payment list", module);
						        }				
						        
								mapInv.put("unitCost", avgUnitPrice);
								mapInv.put("orderCurrencyUnitPrice", unitPrice);
							}
							// End exchange rate
							String inventoryItemId = null;
							try {
								Map<String, Object> rs = dispatcher.runSync("receiveInventoryProduct", mapInv);
								if (rs.containsKey("inventoryItemId")) inventoryItemId = (String)rs.get("inventoryItemId");
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("Service receiveInventoryProduct error!");
							}
							if (UtilValidate.isNotEmpty(inventoryItemId) && UtilValidate.isNotEmpty(locationId)) {
								Map<String, Object> map = FastMap.newInstance();
								map.put("inventoryItemId", inventoryItemId);
								map.put("locationId", locationId);
								map.put("userLogin", userLogin);
								try {
									dispatcher.runSync("setLocationForInventoryItem", map);
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: setLocationForInventoryItem error! " + e.toString());
								}
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("No Item found with delivery" + deliveryId);
			}
		}
		List<String> listAccAdminGroup = SecurityUtil.getPartiesByRolesWithCurrentOrg((GenericValue)context.get("userLogin"), "ACC_PAYMENT_EMP", delegator);
		for (String partyId : listAccAdminGroup){
			Map<String, Object> mapNotify = new HashMap<String, Object>();
			String header = UtilProperties.getMessage(resource, "NewOrderHadBeenDelivered", (Locale)context.get("locale"));
			mapNotify.put("partyId", partyId);
			mapNotify.put("action", "viewDetailPO?orderId="+delivery.getString("orderId"));
			mapNotify.put("targetLink", "");
			mapNotify.put("header", header);
			mapNotify.put("ntfType", "ONE");
			mapNotify.put("userLogin", (GenericValue)context.get("userLogin"));
			try {
				dispatcher.runSync("createNotification", mapNotify);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CreateNotificationError", (Locale)context.get("locale")));
			}
		}
		result.put("deliveryTypeId", deliveryTypeId);
		result.put("deliveryId", deliveryId);
		return result;
	}
	
	public static Map<String, Object> getDeliveryItemByDeliveryId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String deliveryId = (String) context.get("deliveryId");
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		String deliveryTypeId = delivery.getString("deliveryTypeId");
		String entityView = "DeliveryItem";
		if ("DELIVERY_SALES".equals(deliveryTypeId) || "DELIVERY_PURCHASE".equals(deliveryTypeId)){
			entityView = "DeliveryItemView";
		} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
			entityView = "DeliveryItemTransferView";
		} else if (UtilValidate.isNotEmpty(delivery.getString("requirementId"))){
			entityView = "DeliveryItemRequirementView";
		}
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("deliveryId", deliveryId));
		if (!"DLV_CANCELLED".equals(delivery.getString("statusId"))){
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED"));
		}
		List<GenericValue> listItems = delegator.findList(entityView, EntityCondition.makeCondition(conds), null, null, null, false);
    	List<Map<String, Object>> listDeliveryItems = FastList.newInstance();
    	String facilityId = delivery.getString("destFacilityId");
    	
    	if (!listItems.isEmpty()){
    		for(GenericValue item : listItems){
    			Map<String, Object> map = FastMap.newInstance();
        		map.put("deliveryId", item.getString("deliveryId"));
        		map.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
        		map.put("fromOrderItemSeqId", item.getString("fromOrderItemSeqId"));
        		map.put("fromOrderId", item.getString("fromOrderId"));
        		map.put("fromTransferItemSeqId", item.getString("fromTransferItemSeqId"));
        		map.put("fromTransferId", item.getString("fromTransferId"));
        		map.put("fromReqItemSeqId", item.getString("fromReqItemSeqId"));
        		map.put("fromRequirementId", item.getString("fromRequirementId"));
        		map.put("productId", item.getString("productId"));
        		map.put("productCode", item.getString("productCode"));
        		map.put("productName", item.getString("productName"));
        		map.put("quantityUomId", item.getString("quantityUomId"));
        		map.put("orderQuantityUomId", item.getString("orderQuantityUomId"));
        		map.put("weightUomId", item.getString("weightUomId"));
        		map.put("orderWeightUomId", item.getString("orderWeightUomId"));
        		map.put("alternativeQuantity", item.getString("alternativeQuantity"));
        		map.put("comment", item.getString("comment"));
        		map.put("actualExportedQuantity", item.getBigDecimal("actualExportedQuantity"));
        		map.put("actualExportedAmount", item.getBigDecimal("actualExportedAmount"));
        		map.put("statusId", item.getString("statusId"));
        		map.put("isPromo", item.getString("isPromo"));
        		map.put("quantity", item.getBigDecimal("quantity"));
        		map.put("amount", item.getBigDecimal("amount"));
        		map.put("DeliveryItemTransferView", item.getBigDecimal("DeliveryItemTransferView"));
        		map.put("batch", item.getString("batch"));
        		map.put("inventoryItemId", item.getString("inventoryItemId"));
        		map.put("actualExpireDate", item.getTimestamp("actualExpireDate"));
        		map.put("actualManufacturedDate", item.getTimestamp("actualManufacturedDate")); 
        		map.put("expireDate", item.getTimestamp("expireDate"));
        		map.put("deliveryStatusId", item.getString("deliveryStatusId"));
        		map.put("weight", item.getBigDecimal("weight"));
        		map.put("productWeight", item.getBigDecimal("productWeight"));
        		map.put("weightUomId", item.getString("weightUomId"));
        		map.put("defaultWeightUomId", item.getString("defaultWeightUomId"));
        		map.put("purchaseUomId", item.getString("purchaseUomId"));
        		map.put("salesUomId", item.getString("salesUomId"));
        		map.put("requireAmount", item.getString("requireAmount"));
        		map.put("selectedAmount", item.getBigDecimal("selectedAmount"));
				map.put("unitPrice", item.getBigDecimal("unitPrice"));
				map.put("comments", item.getString("comments"));
        		
        		BigDecimal convertNumber = BigDecimal.ONE;
        		if (UtilValidate.isNotEmpty(item.getString("orderQuantityUomId"))) {
        			convertNumber = ProductUtil.getConvertPackingNumber(delegator, item.getString("productId"), item.getString("orderQuantityUomId"), item.getString("quantityUomId"));
				}
        		if (UtilValidate.isNotEmpty(item.getString("transferQuantityUomId"))) {
        			convertNumber = ProductUtil.getConvertPackingNumber(delegator, item.getString("productId"), item.getString("transferQuantityUomId"), item.getString("quantityUomId"));
        		}
        		map.put("convertNumber", convertNumber);
        		
        		List<Map<String, Object>> listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, item.getString("productId"));
        		map.put("quantityUomIds", JsonUtil.convertListMapToJSON(listQtyUoms));
        		
        		List<Map<String, Object>> listWeUoms = ProductUtil.getProductWeightUomWithConvertNumbers(delegator, item.getString("productId"));
        		map.put("weightUomIds", JsonUtil.convertListMapToJSON(listWeUoms));
        		
        		GenericValue productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", item.getString("productId"), "facilityId", facilityId));
        		if (UtilValidate.isNotEmpty(productFacility)){
        			map.put("expRequired", productFacility.getString("expRequired"));
            		map.put("mnfRequired", productFacility.getString("mnfRequired"));
            		map.put("lotRequired", productFacility.getString("lotRequired"));
        		}
        		
        		if(item.getBigDecimal("actualDeliveredQuantity") != null && item.getBigDecimal("actualDeliveredQuantity").compareTo(BigDecimal.ZERO) > 0){
        			map.put("actualDeliveredQuantity", item.getBigDecimal("actualDeliveredQuantity"));
        		} else {
        			map.put("actualDeliveredQuantity", BigDecimal.ZERO);
        		}
        		if(item.getBigDecimal("actualDeliveredAmount") != null && item.getBigDecimal("actualDeliveredAmount").compareTo(BigDecimal.ZERO) > 0){
        			map.put("actualDeliveredAmount", item.getBigDecimal("actualDeliveredAmount"));
        		} else {
        			map.put("actualDeliveredAmount", BigDecimal.ZERO);
        		}
        		listDeliveryItems.add(map);
    		}
    	}
    		
		result.put("listDeliveryItems", listDeliveryItems);
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> getOrderItemDelivery(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String) context.get("orderId");
		String facilityId = (String) context.get("facilityId");
		LocalDispatcher dispathcher = ctx.getDispatcher();
	    Map<String, Object> mapFac = FastMap.newInstance();
		List<GenericValue> listAllConditions = new ArrayList<GenericValue>();
		List<GenericValue> listSortFields = new ArrayList<GenericValue>();
		EntityFindOptions opts = new EntityFindOptions();
		Map<String, String[]> parameters = FastMap.newInstance();
		String[] listOrderId = new String[1];
		listOrderId[0] = orderId;
		String[] listFacilityId = new String[1];
		listFacilityId[0] = facilityId;
		parameters.put("orderId", listOrderId);
		parameters.put("facilityId", listFacilityId);
		mapFac.put("listAllConditions", listAllConditions);
		mapFac.put("listSortFields", listSortFields);
		mapFac.put("opts", opts);
		mapFac.put("parameters", parameters);
		mapFac.put("userLogin", (GenericValue)context.get("userLogin"));
		List<GenericValue> listOrderItems = new ArrayList<GenericValue>();
		Map<String, Object> map = FastMap.newInstance();
		try {
			map = dispathcher.runSync("getListOrderItemDelivery", mapFac);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		listOrderItems = (List)map.get("listIterator");
		result.put("listOrderItems", listOrderItems); 
		return result;
	}
	
   	public static Map<String, Object> getListDeliveryItemByOrder(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
       	Delegator delegator = ctx.getDelegator();
		List<GenericValue> listDeliveryItems = new ArrayList<GenericValue>();
		String orderId = (String)context.get("orderId");
    	listDeliveryItems = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(UtilMisc.toMap("fromOrderId", orderId)), null, null, null, false);
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listDeliveryItems", listDeliveryItems);
    	return successResult;
	}
   	
   	public static Map<String, Object> quickReceivePurchaseOrder(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException{
       	Delegator delegator = ctx.getDelegator();
       	String orderId = (String)context.get("orderId");
       	String deliveryId = null;
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String contactMechId = (String)context.get("contactMechId");
       	// 1. auto create receive Delivery for order
       	Map<String, Object> mapCreateDelivery = FastMap.newInstance();
       	
       	List<GenericValue> orderRoleTos = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
       	
    	String curOrg = orderRoleTos.get(0).getString("partyId");
    	List<GenericValue> listPOShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
       	Timestamp shipByDate = null;
       	String facilityId = (String)context.get("facilityId");
       	if (facilityId == null){
       		if (!listPOShipGroups.isEmpty()){
           		shipByDate = listPOShipGroups.get(0).getTimestamp("shipByDate");
           		// get Order Sales relate to PO order
           		List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("toOrderId", orderId)), null, null, null, false);
           		if (!listOrderItemAssoc.isEmpty()){
           			String soOrderId = listOrderItemAssoc.get(0).getString("orderId");
           			List<GenericValue> listSOShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", soOrderId)), null, null, null, false);
           			if (!listSOShipGroups.isEmpty()){
           				if (listSOShipGroups.get(0).getString("facilityId") != null){
           					GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", listSOShipGroups.get(0).getString("facilityId")));
                       		if ("VIRTUAL_WAREHOUSE".equals(facility.getString("facilityTypeId")) &&  facility.getString("ownerPartyId").equals(curOrg)){
                       			facilityId = listSOShipGroups.get(0).getString("facilityId");
                       		}
           				}
           			}
           		}
           	}
           	if (facilityId == null){
           		List<GenericValue> listVirtualFacilities = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "VIRTUAL_WAREHOUSE", "ownerPartyId", curOrg)), null, null, null, false);
               	if (!listVirtualFacilities.isEmpty()){
               		facilityId = listVirtualFacilities.get(0).getString("facilityId");
               	} else {
               		return ServiceUtil.returnError("No virtual facility had been setup for current organization!");
               	}
           	}
       	}
       	if (shipByDate == null){
       		shipByDate = UtilDateTime.nowTimestamp();
       	}
       	mapCreateDelivery.put("orderId", orderId);
       	mapCreateDelivery.put("facilityId", facilityId);
       	mapCreateDelivery.put("approveNow", true);
       	if (contactMechId != null){
       		mapCreateDelivery.put("contactMechId", contactMechId);
       	}
       	mapCreateDelivery.put("userLogin", userLogin);
       	
       	LocalDispatcher dispatcher = ctx.getDispatcher(); 
       	Map<String, Object> mapDelivery = dispatcher.runSync("quickCreateDelivery", mapCreateDelivery);
       	
       	deliveryId = (String)mapDelivery.get("deliveryId");
       	
       	// 2. update delivery item to receive 
       	List<Map<String, Object>> listDeliveryItems = new ArrayList<Map<String, Object>>();
       	
       	List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
       	
       	if (!listDlvItems.isEmpty()){
       		for (GenericValue item : listDlvItems){
       			Map<String, Object> map = FastMap.newInstance();
                map.put("fromOrderId", orderId);
        		map.put("fromOrderItemSeqId", item.getString("fromOrderItemSeqId"));
        		GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", item.getString("fromOrderItemSeqId")));
        		String productId = orderItem.getString("productId");
        		// get date available to receive (N + x)
        		Timestamp expiredDate = LogisticsProductUtil.getProductExpireDate(delegator, facilityId, productId);
        		// get manufacturedDate from expire date
				Map<String, Object> mapDate = LogisticsProductUtil.getProductAttributeDate(delegator, facilityId, productId, expiredDate);
        		Timestamp manufacturedDate = (Timestamp)mapDate.get("manufacturedDate");
        		map.put("actualExpireDate", expiredDate.getTime());
        		map.put("actualManufacturedDate", manufacturedDate.getTime());
        		map.put("deliveryId", deliveryId);
        		map.put("actualExportedQuantity", item.getBigDecimal("quantity"));
        		map.put("actualDeliveredQuantity", item.getBigDecimal("quantity"));
        		map.put("quantity", item.getBigDecimal("quantity"));
        		map.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
        		map.put("statusId", "DELI_ITEM_DELIVERED");
        		
        		listDeliveryItems.add(map);
       		}
       	} else {
       		return ServiceUtil.returnError("Order Item Not Found!");
       	}
       	String listDlvItemJSON = JsonUtil.convertListMapToJSON(listDeliveryItems);
    	Map<String, Object> mapUpdateDelivery = FastMap.newInstance();
       	mapUpdateDelivery.put("deliveryId", deliveryId);
       	if (shipByDate.compareTo(UtilDateTime.nowTimestamp()) > 0){
       		mapUpdateDelivery.put("actualStartDate", UtilDateTime.nowTimestamp().getTime());
           	mapUpdateDelivery.put("actualArrivalDate", UtilDateTime.nowTimestamp().getTime());
       	} else {
       		mapUpdateDelivery.put("actualStartDate", shipByDate.getTime());
           	mapUpdateDelivery.put("actualArrivalDate", shipByDate.getTime());
       	}
       	mapUpdateDelivery.put("listDeliveryItems", listDlvItemJSON);
       	mapUpdateDelivery.put("statusId", "DLV_DELIVERED");
       	mapUpdateDelivery.put("userLogin", userLogin);
       	dispatcher.runSync("updateDelivery", mapUpdateDelivery);
       	
       	// 3. Create export delivery
       	List<GenericValue> listItemAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("toOrderId", orderId)), null, null, null, false); 
		List<Map<String, Object>> listSalesOrderItems = new ArrayList<Map<String, Object>>();
		String salesOrderId = null;
       	if (!listDeliveryItems.isEmpty()){
			for (GenericValue assoc : listItemAssoc){
				String soId = assoc.getString("orderId");
				salesOrderId = soId;
				String soOrderItemSeqId = assoc.getString("orderItemSeqId");
				BigDecimal quantity = assoc.getBigDecimal("quantity");
				
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("orderItemSeqId", soOrderItemSeqId);
				mapTmp.put("orderId", soId);
				mapTmp.put("quantity", quantity.toString());
				GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", soId, "orderItemSeqId", soOrderItemSeqId));
				if ("ITEM_APPROVED".equals(orderItem.getString("statusId"))){
					listSalesOrderItems.add(mapTmp);
				}
			}
		}
       	GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
       	
       	List<GenericValue> customers = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", salesOrderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
        String partyIdTo = null;
        if (!customers.isEmpty()){
        	partyIdTo = customers.get(0).getString("partyId");
        }
        
    	String partyIdFrom = curOrg;
        List<GenericValue> listOrderItemShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", salesOrderId)), null, null, null, false);
        String destinationContactMech = listOrderItemShipGroups.get(0).getString("contactMechId");
       	Map<String, Object> mapCreateExportDelivery = FastMap.newInstance();
       	mapCreateExportDelivery.put("listOrderItems", listSalesOrderItems);
       	mapCreateExportDelivery.put("orderId", salesOrderId);
       	mapCreateExportDelivery.put("currencyUomId", orderHeader.getString("currencyUom"));
       	mapCreateExportDelivery.put("statusId", "DLV_CREATED");
       	mapCreateExportDelivery.put("originContactMechId", contactMechId);
//       	mapCreateExportDelivery.put("originProductStoreId", orderHeader.getString("productStoreId"));
       	
       	mapCreateExportDelivery.put("partyIdTo", partyIdTo);
       	mapCreateExportDelivery.put("partyIdFrom", partyIdFrom);
       	mapCreateExportDelivery.put("destContactMechId", destinationContactMech);
       	mapCreateExportDelivery.put("originFacilityId", facilityId);
       	mapCreateExportDelivery.put("deliveryDate", shipByDate);
       	mapCreateExportDelivery.put("estimatedStartDate", shipByDate);
       	mapCreateExportDelivery.put("estimatedArrivalDate", shipByDate);
       	mapCreateExportDelivery.put("defaultWeightUomId", "WT_kg");
       	mapCreateExportDelivery.put("deliveryTypeId", "DELIVERY_SALES");
       	mapCreateExportDelivery.put("userLogin", userLogin);
       	
    	Map<String, Object> mapExportDelivery = dispatcher.runSync("createDelivery", mapCreateExportDelivery);
    	String expDeliveryId = (String)mapExportDelivery.get("deliveryId");
    	// 4. Approve export delivery
    	
    	Map<String, Object> mapAppr = FastMap.newInstance();
    	mapAppr.put("deliveryId", expDeliveryId);
    	mapAppr.put("statusId", "DLV_APPROVED");
    	mapAppr.put("userLogin", (GenericValue)context.get("userLogin"));
    	try {
    		dispatcher.runSync("updateDelivery", mapAppr);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("Approve delivery error!");
		}
    	
    	// 5. Export delivery has been created
    	
    	List<GenericValue> listSalesDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", expDeliveryId)), null, null, null, false);
    	List<Map<String, Object>> listUpdateSalesDeliveryItems = new ArrayList<Map<String, Object>>();
    	List<GenericValue> listInvDetails = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
    	for (GenericValue dlvItem : listSalesDeliveryItems){
    		Map<String, Object> mapTmp = FastMap.newInstance();
    		mapTmp.put("fromOrderId", dlvItem.getString("fromOrderId"));
    		mapTmp.put("fromOrderItemSeqId", dlvItem.getString("fromOrderItemSeqId"));
    		GenericValue soOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", dlvItem.getString("fromOrderId"), "orderItemSeqId", dlvItem.getString("fromOrderItemSeqId")));
    		String invItemId = null;
    		for (GenericValue detail : listInvDetails){
    			GenericValue poOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", detail.getString("orderItemSeqId")));
    			if (soOrderItem.getString("productId").equals(poOrderItem.getString("productId"))){
    				invItemId = detail.getString("inventoryItemId");
    			}
    		}
    		if (invItemId != null){
    			mapTmp.put("inventoryItemId", invItemId);
        		mapTmp.put("deliveryId", expDeliveryId);
        		mapTmp.put("deliveryItemSeqId", dlvItem.getString("deliveryItemSeqId"));
        		mapTmp.put("actualExportedQuantity", dlvItem.getBigDecimal("quantity"));
        		mapTmp.put("actualDeliveredQuantity", BigDecimal.ZERO);
        		listUpdateSalesDeliveryItems.add(mapTmp);
    		}
    	}
    	String listSalesDlvItemJSON = JsonUtil.convertListMapToJSON(listUpdateSalesDeliveryItems);
    	Map<String, Object> mapUpdateExportDlv = FastMap.newInstance();
    	mapUpdateExportDlv.put("listDeliveryItems", listSalesDlvItemJSON);
    	mapUpdateExportDlv.put("deliveryId", expDeliveryId);
    	if (shipByDate.compareTo(UtilDateTime.nowTimestamp()) < 0){
    		mapUpdateExportDlv.put("actualStartDate", shipByDate.getTime());
        	mapUpdateExportDlv.put("actualArrivalDate", shipByDate.getTime());
    	} else {
    		mapUpdateExportDlv.put("actualStartDate", UtilDateTime.nowTimestamp().getTime());
        	mapUpdateExportDlv.put("actualArrivalDate", UtilDateTime.nowTimestamp().getTime());
    	}
    	mapUpdateExportDlv.put("userLogin", userLogin);
    	dispatcher.runSync("updateDeliveryItemList", mapUpdateExportDlv);
    	
    	// 6. update delivered quantity to completed sales order
    	
    	GenericValue salesOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", salesOrderId));
    	String isFavor = salesOrderHeader.getString("isFavorDelivery");
    	if ("Y".equals(isFavor)){
    		// complete order if it is favor delivery
    		listSalesDeliveryItems.clear();
        	listSalesDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", expDeliveryId)), null, null, null, false);
        	listUpdateSalesDeliveryItems.clear();
        	listUpdateSalesDeliveryItems = new ArrayList<Map<String, Object>>();
        	for (GenericValue dlvItem : listSalesDeliveryItems){
        		Map<String, Object> mapTmp = FastMap.newInstance();
        		mapTmp.put("fromOrderId", dlvItem.getString("fromOrderId"));
        		mapTmp.put("fromOrderItemSeqId", dlvItem.getString("fromOrderItemSeqId"));
        		mapTmp.put("deliveryId", expDeliveryId);
        		mapTmp.put("inventoryItemId", dlvItem.getString("inventoryItemId"));
        		mapTmp.put("actualExportedQuantity", dlvItem.getBigDecimal("actualExportedQuantity"));
        		mapTmp.put("deliveryItemSeqId", dlvItem.getString("deliveryItemSeqId"));
        		mapTmp.put("actualDeliveredQuantity", dlvItem.getBigDecimal("actualExportedQuantity"));
        		
        		listUpdateSalesDeliveryItems.add(mapTmp);
        	}
        	listSalesDlvItemJSON = JsonUtil.convertListMapToJSON(listUpdateSalesDeliveryItems);
        	Map<String, Object> mapUpdateDeliveredDlv = FastMap.newInstance();
        	mapUpdateDeliveredDlv.put("listDeliveryItems", listSalesDlvItemJSON);
        	mapUpdateDeliveredDlv.put("deliveryId", expDeliveryId);
        	mapUpdateDeliveredDlv.put("actualArrivalDate", UtilDateTime.nowTimestamp().getTime());
        	mapUpdateDeliveredDlv.put("userLogin", userLogin);
        	dispatcher.runSync("updateDeliveryItemList", mapUpdateDeliveredDlv);
    	}
    	
       	Map<String, Object> result = FastMap.newInstance();
       	result.put("orderId", orderId);
    	return result;
	}
   	
   	public static Map<String,Object> autoExportDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
    	String deliveryId = (String)context.get("deliveryId");
    	Delegator delegator = ctx.getDelegator();
    	GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    	Map<String, Object> mapInit = FastMap.newInstance();
    	mapInit.put("pathScanFile", null);
    	mapInit.put("deliveryId", deliveryId);
    	mapInit.put("actualStartDate", delivery.getTimestamp("estimatedStartDate").getTime());
    	mapInit.put("actualArrivalDate", null);
    	
    	List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
    	List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> map = FastMap.newInstance();
    	for (GenericValue item : listDlvItems){
    		GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")));
			String productId = orderItem.getString("productId");	
			BigDecimal actualQty = orderItem.getBigDecimal("quantity");
			String inventoryItemId = null;
			List<GenericValue> listShipGrps = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(UtilMisc.toMap("orderId", item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId"))), null, null, null, false);
			if (!listShipGrps.isEmpty()){
				GenericValue inventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", listShipGrps.get(0).getString("inventoryItemId")));
				if(inventoryItem != null && inventoryItem.getBigDecimal("quantityOnHandTotal").compareTo(actualQty) > 0){
					inventoryItemId = listShipGrps.get(0).getString("inventoryItemId");
				}
			}
			Locale locale = (Locale)context.get("locale");
			List<GenericValue> orderRoleFroms = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", item.getString("fromOrderId"), "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
			if (!orderRoleFroms.isEmpty()){
				String billFromParty = orderRoleFroms.get(0).getString("partyId");
				if (inventoryItemId == null){
					EntityCondition statusCond1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
					EntityCondition statusCond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INV_AVAILABLE");
					EntityCondition statusConds = EntityCondition.makeCondition(UtilMisc.toList(statusCond1, statusCond2), EntityOperator.OR);
					List<EntityCondition> listCondFindInvs = new ArrayList<EntityCondition>();
					listCondFindInvs.add(statusConds);
					listCondFindInvs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					listCondFindInvs.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, billFromParty));
					List<GenericValue> listInvs = delegator.findList("InventoryItem", EntityCondition.makeCondition(listCondFindInvs), null, null, null, false);
					if (!listInvs.isEmpty()){
						for (GenericValue inv : listInvs){
							if (inv.getBigDecimal("quantityOnHandTotal").compareTo(actualQty) > 0){
								inventoryItemId = inv.getString("inventoryItemId");
							}
						}
						if (inventoryItemId == null){
							return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "NotEnoughProduct", locale));
						}
					}
				}
			} else {
				return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "PartyBillFromOfOrderNotFound", locale));
			}
			item.put("inventoryItemId", inventoryItemId);
			delegator.store(item);
			map.put("fromOrderId", item.getString("fromOrderId"));
		    map.put("fromOrderItemSeqId", item.getString("fromOrderItemSeqId"));
		    map.put("inventoryItemId", inventoryItemId);
		    map.put("deliveryId", item.getString("deliveryId"));
		    map.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
		    map.put("actualExportedQuantity", actualQty.toString());
		    map.put("actualDeliveredQuantity", null);
		    listItems.add(map);
		    
		    String listJsonItems = JsonUtil.convertListMapToJSON(listItems);
		    mapInit.put("listDeliveryItems", listJsonItems);
		    mapInit.put("userLogin", (GenericValue)context.get("userLogin"));
		    
		    dispatcher.runSync("updateDeliveryItemList", mapInit);
    	}
    	Map<String, Object> mapInitCompleted = FastMap.newInstance();
	    mapInitCompleted.put("deliveryId", deliveryId);
	    mapInitCompleted.put("userLogin", (GenericValue)context.get("userLogin"));
	    dispatcher.runSync("autoCompleteDelivery", mapInitCompleted);
    	Locale locale = (Locale)context.get("locale");
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "UpdateSuccessfully", locale));
	}
	
	public static Map<String,Object> autoCompleteDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
    	String deliveryId = (String)context.get("deliveryId");
    	Delegator delegator = ctx.getDelegator();
    	GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    	Map<String, Object> mapInit = FastMap.newInstance();
    	mapInit.put("pathScanFile", null);
    	mapInit.put("deliveryId", deliveryId);
    	mapInit.put("actualStartDate", delivery.getTimestamp("actualStartDate").getTime());
    	mapInit.put("actualArrivalDate", delivery.getTimestamp("estimatedArrivalDate").getTime());
    	
    	List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
    	List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    	
    	Map<String, Object> map = FastMap.newInstance();
    	for (GenericValue item : listDlvItems){
    		
			map.put("fromOrderId", item.getString("fromOrderId"));
		    map.put("fromOrderItemSeqId", item.getString("fromOrderItemSeqId"));
		    map.put("inventoryItemId", item.getString("inventoryItemId"));
		    map.put("deliveryId", item.getString("deliveryId"));
		    map.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
		    map.put("actualExportedQuantity", item.getString("actualExportedQuantity"));
		    map.put("actualDeliveredQuantity", item.getString("actualExportedQuantity"));
		    listItems.add(map);
		    
		    String listJsonItems = JsonUtil.convertListMapToJSON(listItems);
		    mapInit.put("listDeliveryItems", listJsonItems);
		    mapInit.put("userLogin", (GenericValue)context.get("userLogin"));
		    LocalDispatcher dispatcher = ctx.getDispatcher();
		    dispatcher.runSync("updateDeliveryItemList", mapInit);
    	}
    	Locale locale = (Locale)context.get("locale");
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "UpdateSuccessfully", locale));
	}
	
	public static Map<String, Object> checkDeliveryHasCreatedForOrder(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String orderId = (String)context.get("orderId");
    	Boolean hasExisted = DeliveryHelper.checkDeliveryHasCreatedForOrder(delegator, orderId);
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("hasExisted", hasExisted);
		successResult.put("orderId", orderId);
		return successResult;
	} 
	
	public static Map<String, Object> changeDeliveryItemStatus(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String deliveryId = (String)context.get("deliveryId");
    	String deliveryItemSeqId = (String)context.get("deliveryItemSeqId");
    	String statusId = (String)context.get("statusId");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue deliveryItem = null;
    	String oldStatusId = null;
    	try {
    		deliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId));
    		oldStatusId = deliveryItem.getString("statusId");
    		if (UtilValidate.isNotEmpty(deliveryItem)){
    			deliveryItem.put("statusId", statusId);
    			if ("DELI_ITEM_CANCELLED".equals(statusId)){
        			deliveryItem.put("actualExportedQuantity", BigDecimal.ZERO);
        			deliveryItem.put("actualDeliveredQuantity", BigDecimal.ZERO);
    			}
    			deliveryItem.store();
    		} else {
    			return ServiceUtil.returnError("OLBIUS: changeDeliveryItemStatus Error");
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling changeDeliveryItemStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	successResult.put("deliveryId", deliveryId);
    	successResult.put("deliveryItemSeqId", deliveryItemSeqId);
    	successResult.put("oldStatusId", oldStatusId);
    	successResult.put("statusId", statusId);
    	return successResult;
    }
	 
	public static Map<String, Object> changeAllDeliveryItemStatus(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String deliveryId = (String)context.get("deliveryId");
    	String statusId = (String)context.get("statusId");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	try {
    		List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition("deliveryId", deliveryId), null, null, null, false); 
    		if (!listDeliveryItems.isEmpty()){
    			for (GenericValue item : listDeliveryItems){
    				Map<String, Object> mapChange = FastMap.newInstance();
    				mapChange.put("deliveryId", deliveryId);
    				mapChange.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
    				mapChange.put("statusId", statusId);
    				mapChange.put("userLogin", (GenericValue)context.get("userLogin"));
    				dispatcher.runSync("changeDeliveryItemStatus", mapChange);
    			}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling changeAllDeliveryItemStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	successResult.put("deliveryId", deliveryId);
    	return successResult;
    }
	
	public static Map<String,Object> updateDeliveryStatus(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String deliveryId = (String)context.get("deliveryId");
		String newStatusId = (String)context.get("newStatusId");
		String setItemStatus = (String)context.get("setItemStatus");
		String newItemStatus = (String)context.get("newItemStatus");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		try {
			GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			if (newStatusId != null && delivery != null){
				delivery.put("statusId", newStatusId);
				delegator.store(delivery);
			}
			GenericValue deliveryStatus = delegator.makeValue("DeliveryStatus");
			String deliveryStatusId = delegator.getNextSeqId("DeliveryStatus");
			deliveryStatus.put("deliveryStatusId", deliveryStatusId);
			deliveryStatus.put("deliveryId", deliveryId);
			deliveryStatus.put("statusId", newStatusId);
			deliveryStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
			deliveryStatus.put("statusUserLogin", userLoginId);
			delegator.create(deliveryStatus);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: get delivery or create delivery status error!");
		}
		if (UtilValidate.isNotEmpty(setItemStatus) && UtilValidate.isNotEmpty(newItemStatus) && "Y".equals(setItemStatus)){
			List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
			for (GenericValue item : listDlvItems) {
				GenericValue statusValid = delegator.findOne("StatusValidChange", false, UtilMisc.toMap("statusId", item.getString("statusId"), "statusIdTo", newItemStatus));
				if (!item.getString("statusId").equals(newItemStatus) && UtilValidate.isNotEmpty(statusValid)){
					item.put("statusId", newItemStatus);
					item.store();
				}
			}
		}
		Security security = ctx.getSecurity();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String roleType = null;
		if (!security.hasPermission("DELIVERY_ADMIN", userLogin)) {
			roleType = "role.manager.specialist";
		} else {
			roleType = "role.storekeeper";
		}
		try {
			String messages = null;
			if ("DLV_CREATED".equals(newStatusId)){
				messages = "HasBeenCreated";
			} else if ("DLV_PROPOSED".equals(newStatusId)){
				messages = "HasBeenProposed";
			} else if ("DLV_APPROVED".equals(newStatusId)){
				messages = "HasBeenApproved";
			} else if ("DLV_EXPORTED".equals(newStatusId)){
				messages = "HasBeenExported";
			} else if ("DLV_DELIVERED".equals(newStatusId)){
				messages = "HasBeenDelivered";
			}
			dispatcher.runSync("createNotifyDelivery", UtilMisc.toMap("deliveryId", deliveryId, "messages", messages, "roleTypeProperties", roleType, "userLogin", userLogin));
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: createNotifyDelivery error! " + e.toString());
		}
		return result;
	}
	
	public static Map<String,Object> createNotifyDelivery(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String deliveryId = (String)context.get("deliveryId");
		result.put("deliveryId", deliveryId);
		String messages = (String)context.get("messages");
		String roleTypeProperties = (String)context.get("roleTypeProperties");
		GenericValue roleType = delegator.findOne("RoleType", false, UtilMisc.toMap("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, roleTypeProperties)));
		if (UtilValidate.isEmpty(roleType)) return result;
		String header = "";
		String action = "";
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<String> listPartyTos = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, roleTypeProperties), delegator);
		if (listPartyTos.isEmpty()){
	    	return result;
	    }
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		GenericValue deliveryType = delegator.findOne("DeliveryType", false, UtilMisc.toMap("deliveryTypeId", delivery.getString("deliveryTypeId")));
		if ("DELIVERY_SALES".equals(delivery.getString("deliveryTypeId"))){
			header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)deliveryType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "CommonDeliveryId", (Locale)context.get("locale")) +": [" +deliveryId+"]";
			String orderId = delivery.getString("orderId");
			action = "viewOrder?orderId="+orderId+ "&activeTab=deliveries-tab";
		} else if ("DELIVERY_PURCHASE".equals(delivery.getString("deliveryTypeId"))){
			header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)deliveryType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "CommonDeliveryId", (Locale)context.get("locale")) +": [" +deliveryId+"]";
			String orderId = delivery.getString("orderId");
			action = "viewDetailPO?orderId="+orderId+ "&activeTab=deliveries-tab";
		} else if ("DELIVERY_TRANSFER".equals(delivery.getString("deliveryTypeId"))){
			header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)deliveryType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "CommonDeliveryId", (Locale)context.get("locale")) +": [" +deliveryId+"]";
			String transferId = delivery.getString("transferId");
			action = "viewDetailTransfer?transferId="+transferId+ "&activeTab=deliveries-tab";
		} 
		LocalDispatcher dispatcher = ctx.getDispatcher();
		for (String partyId : listPartyTos) {
    		Map<String, Object> mapContext = new HashMap<String, Object>();
    		mapContext.put("partyId", partyId);
    		mapContext.put("action", action);
    		mapContext.put("targetLink", "");
    		mapContext.put("header", header);
    		mapContext.put("ntfType", "ONE");
    		mapContext.put("userLogin", userLogin);
    		mapContext.put("openTime", UtilDateTime.nowTimestamp());
    		try {
    			dispatcher.runSync("createNotification", mapContext);
    		} catch (GenericServiceException e) {
    			return ServiceUtil.returnError("OLBIUS: createNotification error! " + e.toString());
    		}
		}
		
		return result;
	}
	
	public static Map<String,Object> checkDeliveryStatus(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String deliveryId = (String)context.get("deliveryId");
		List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
		Boolean allCreated = true;
		Boolean allProposed = true;
		Boolean allApproved = true;
		Boolean allExported = true;
		Boolean allDelivered = true;
		Boolean allCancelled = true;
		String newDlvStatusId = null;
		if (!listDlvItems.isEmpty()){
			for (GenericValue item : listDlvItems) {
				if (!"DELI_ITEM_CREATED".equals(item.getString("statusId"))){
					allCreated = false;
					if (!"DELI_ITEM_PROPOSED".equals(item.getString("statusId"))){
						allProposed = false;
						if (!"DELI_ITEM_APPROVED".equals(item.getString("statusId"))){
							allApproved = false;
							if (!"DELI_ITEM_EXPORTED".equals(item.getString("statusId"))){
								allExported = false;
								if (!"DELI_ITEM_DELIVERED".equals(item.getString("statusId"))){
									allDelivered = false;
									if (!"DELI_ITEM_CANCELLED".equals(item.getString("statusId"))){
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
		if (allCreated){
			newDlvStatusId = "DLV_CREATED";
		} else if (allProposed){
			newDlvStatusId ="DLV_PROPOSED";
		} else if (allApproved){
			newDlvStatusId ="DLV_APPROVED";
		} else if (allExported){
			newDlvStatusId ="DLV_EXPORTED";
		} else if (allDelivered){
			newDlvStatusId ="DLV_DELIVERED";
		} else if (allCancelled){
			newDlvStatusId ="DLV_CANCELLED";
		}
		try {
			dispatcher.runSync("updateDeliveryStatus", UtilMisc.toMap("deliveryId", deliveryId, "newStatusId", newDlvStatusId, "setItemStatus", "N", "userLogin", (GenericValue)context.get("userLogin")));
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: update delivery status error");
		}
		result.put("deliveryId", deliveryId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createNewDeliveryEntry(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
		List<Object> listItemTmp = (List<Object>)context.get("shipmentItems");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, String>> listShipmentItems = new ArrayList<Map<String,String>>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("shipmentId")){
					mapItems.put("shipmentId", item.getString("shipmentId"));
				}
				if (item.containsKey("shipmentItemSeqId")){
					mapItems.put("shipmentItemSeqId", item.getString("shipmentItemSeqId"));
				}
				if (item.containsKey("quantity")){
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("quantityUomId")){
					mapItems.put("quantityUomId", item.getString("quantityUomId"));
				}
				listShipmentItems.add(mapItems);
			}
    	} else {
    		listShipmentItems = (List<Map<String, String>>)context.get("listShipmentItems");
    	}
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Delegator delegator = ctx.getDelegator();
    	String partyCarrier = (String)context.get("carrierPartyId");
    	String ancestorId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	Boolean isInternalOrg = PartyUtil.checkAncestorOfParty(delegator, ancestorId, partyCarrier, userLogin);
    	Map<String, Object> initMap = FastMap.newInstance();
    	if (isInternalOrg == true){
    		if (UtilValidate.isNotEmpty(context.get("delivererPartyId"))) {
        		initMap.put("delivererPartyId", context.get("delivererPartyId"));
			}
    		if (UtilValidate.isNotEmpty(context.get("driverPartyId"))) {
    			initMap.put("driverPartyId", context.get("driverPartyId"));
			}
    	}
    	String statusId = "DELI_ENTRY_CREATED";
    	if (UtilValidate.isNotEmpty(context.get("statusId"))) {
    		statusId = (String)context.get("statusId");
		}
    	if (UtilValidate.isNotEmpty(context.get("shipCost"))) {
    		initMap.put("shipCost", new BigDecimal((String)context.get("shipCost")));
    	}
    	initMap.put("weightUomId", context.get("weightUomId"));
    	initMap.put("facilityId", context.get("facilityId"));
    	initMap.put("contactMechId", context.get("contactMechId"));
    	initMap.put("carrierPartyId", partyCarrier);
    	initMap.put("fixedAssetId", context.get("fixedAssetId"));
     	initMap.put("statusId", statusId);
     	initMap.put("fromDate", context.get("fromDate"));
     	initMap.put("thruDate", context.get("thruDate"));
     	initMap.put("description", context.get("description"));
    	initMap.put("userLogin", (GenericValue)context.get("userLogin"));
    	initMap.put("listShipmentItems", listShipmentItems);
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> result;
    	Map<String, Object> successResult = FastMap.newInstance();
		result = dispatcher.runSync("createDeliveryEntry", initMap);
		successResult.put("deliveryEntryId", result.get("deliveryEntryId"));
		return successResult;
	}
	
	public static Map<String,Object> checkAllDeliveryInSpecificStatus(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		Boolean check = true;
		String orderId = (String)context.get("orderId");
		String statusId = (String)context.get("statusId");
		List<GenericValue> list = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		if (UtilValidate.isNotEmpty(statusId) && !list.isEmpty()){
			for (GenericValue item : list) {
				if (!statusId.equals(item.getString("statusId"))) check = false; break;
			}
		}
		result.put("check", check);
		return result;
	}
	
	public static Map<String, Object> autoCreateDeliveryForSalesOrder(DispatchContext ctx, Map<String, ? extends Object> context)  throws GenericEntityException, GenericServiceException{
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String)context.get("orderId");
        try {
			Map<String, Object> map = dispatcher.runSync("quickCreateDelivery", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "orderId", orderId, "approveNow", true));
			String deliveryId = (String)map.get("deliveryId");
			List<GenericValue> list = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
			if (!list.isEmpty()){
				for (GenericValue item : list) {
    				item.put("statusId", "DELI_ITEM_APPROVED");
    				delegator.store(item);
				}
			}
			GenericValue dlv = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			dlv.put("statusId", "DLV_APPROVED");
			delegator.store(dlv);
		} catch (GenericServiceException e){
			return ServiceUtil.returnError("OLBIUS: runsync service quickCreateDelivery error!");
		}
        result.put("orderId", orderId);
		return result;
	}
	
	public static Map<String, Object> autoExportDeliveryForSalesOrder(DispatchContext ctx, Map<String, ? extends Object> context)  throws GenericEntityException, GenericServiceException{
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = FastMap.newInstance();
        String orderId = (String)context.get("orderId");
		List<GenericValue> listDlvs = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		for (GenericValue dlv : listDlvs) {
			List<GenericValue> list = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", dlv.getString("deliveryId"))), null, null, null, false);
			if (!list.isEmpty()){
				Observer o = new DeliveryObserver();
				ItemSubject is = new DeliveryItemSubject();
				is.attach(o);
				for (GenericValue item : list) {
					Map<String, Object> updateItem = FastMap.newInstance();
    				updateItem.put("deliveryId", dlv.getString("deliveryId"));
    				updateItem.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
    				updateItem.put("actualExportedQuantity", item.getBigDecimal("quantity"));
    				updateItem.put("delegator", delegator);
    				is.updateExportedQuantity(updateItem);
				}
			}
		}
        result.put("orderId", orderId);
		return result;
	}
	
	public static Map<String, Object> updateInventoryItemToDeliveryItemByOrder(DispatchContext ctx, Map<String, ? extends Object> context)  throws GenericEntityException, GenericServiceException{
		 Delegator delegator = ctx.getDelegator();
	        Map<String, Object> result = FastMap.newInstance();
	        String orderId = (String)context.get("orderId");
			List<GenericValue> listDlvs = delegator.findList("Delivery", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			for (GenericValue dlv : listDlvs) {
				List<GenericValue> list = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", dlv.getString("deliveryId"))), null, null, null, false);
				if (!list.isEmpty()){
					for (GenericValue item : list) {
						String orderItemSeqId = item.getString("fromOrderItemSeqId");
						List<GenericValue> listDetail = delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId)), null, null, null, false);
						BigDecimal quantity = item.getBigDecimal("quantity");
						String inventoryItemId = null;
						for (GenericValue inv : listDetail) {
							if (inv.getBigDecimal("quantityOnHandDiff").compareTo(BigDecimal.ZERO) <0 && (inv.getBigDecimal("quantityOnHandDiff").abs()).compareTo(quantity) >= 0){
								inventoryItemId = inv.getString("inventoryItemId"); break;
							}
						}
						item.put("inventoryItemId", inventoryItemId);
						delegator.store(item);
					}
				}
				dlv.put("actualStartDate", UtilDateTime.nowTimestamp());
				dlv.store();
			}
	        result.put("orderId", orderId);
		return result;
	}
	
	public static Map<String, Object> removeDeliveryEntryRole(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	String deliveryEntryId = (String)context.get("deliveryEntryId");
    	String roleTypeId = (String)context.get("roleTypeId");
    	List<GenericValue> listDlvEntryRoles = delegator.findList("DeliveryEntryRole", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "roleTypeId", roleTypeId)), null, null, null, false);
    	if(!listDlvEntryRoles.isEmpty()){
    		for (GenericValue item : listDlvEntryRoles) {
    			item.put("thruDate", UtilDateTime.nowTimestamp());
    			delegator.store(item);
    		}
    	}
    	mapReturn.put("deliveryEntryId", deliveryEntryId);
    	return mapReturn;
	}
	
	public static Map<String, Object> removeDeliveryEntryShipment(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	String deliveryEntryId = (String)context.get("deliveryEntryId");
    	String shipmentId = (String)context.get("shipmentId");
    	GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
		shipment.put("deliveryEntryId", null);
		delegator.store(shipment);
		List<GenericValue> listDeliveryEntryShipment = delegator.findList("DeliveryEntryShipment", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "shipmentId", shipmentId)), null, null, null, false);
		if (!listDeliveryEntryShipment.isEmpty()){
			delegator.removeAll(listDeliveryEntryShipment);
		}
    	mapReturn.put("deliveryEntryId", deliveryEntryId);
    	mapReturn.put("shipmentId", shipmentId);
    	return mapReturn;
	}
	
	public static Map<String, Object> removeDeliveryEntryFixedAsset(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	String deliveryEntryId = (String)context.get("deliveryEntryId");
    	String fixedAssetId = (String)context.get("fixedAssetId");
		List<GenericValue> listDeliveryEntryFixedAsset= delegator.findList("DeliveryEntryFixedAsset", EntityCondition.makeCondition(UtilMisc.toMap("deliveryEntryId", deliveryEntryId, "fixedAssetId", fixedAssetId)), null, null, null, false);
		if (!listDeliveryEntryFixedAsset.isEmpty()){
			delegator.removeAll(listDeliveryEntryFixedAsset);
		}
    	mapReturn.put("deliveryEntryId", deliveryEntryId);
    	mapReturn.put("fixedAssetId", fixedAssetId);
    	return mapReturn;
	}
	
	public static Map<String, Object> changeDeliveryStatus(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String deliveryId = (String)context.get("deliveryId");
    	String statusId = (String)context.get("statusId");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue delivery = null;
    	String oldStatusId = null;
    	String deliveryTypeId = null;
    	try {
    		delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    		oldStatusId = delivery.getString("statusId");
    		deliveryTypeId = delivery.getString("deliveryTypeId");
    		if (UtilValidate.isNotEmpty(delivery)){
    			delivery.put("statusId", statusId);
    			if ("DELIVERY_PURCHASE".equals(deliveryTypeId) && "DLV_EXPORTED".equals(statusId)){
    				delivery.put("actualStartDate", UtilDateTime.nowTimestamp());
    			}
    			if ("DELIVERY_PURCHASE".equals(deliveryTypeId) && "DLV_DELIVERED".equals(statusId)){
    				delivery.put("actualArrivalDate", UtilDateTime.nowTimestamp());
    			}
    			if ("DELIVERY_TRANSFER".equals(deliveryTypeId) && "DLV_DELIVERED".equals(statusId)){
    				delivery.put("actualArrivalDate", UtilDateTime.nowTimestamp());
    			}
    			delivery.store();
    		} else {
    			return ServiceUtil.returnError("OLBIUS: changeDeliveryStatus Error");
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling changeDeliveryStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	successResult.put("deliveryId", deliveryId);
    	successResult.put("oldStatusId", oldStatusId);
    	successResult.put("statusId", statusId);
    	successResult.put("deliveryTypeId", deliveryTypeId);
    	return successResult;
    }
	
	public static Map<String, Object> changeShipmentStatusFromDelivery(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException{
    	Delegator delegator = ctx.getDelegator();
    	String deliveryId = (String)context.get("deliveryId");
    	String shipmentStatusId = (String)context.get("shipmentStatusId");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue delivery = null;
    	String shipmentId = null;
    	try {
    		delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
    		shipmentId = delivery.getString("shipmentId");
    		if (UtilValidate.isNotEmpty(delivery) && shipmentId != null){
    			GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
    			if ("SHIPMENT_INPUT".equals(shipment.getString("statusId"))){
    				GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
    				Map<String, Object> mapShipment = FastMap.newInstance();
    				mapShipment.put("userLogin", userLogin);
    				mapShipment.put("shipmentId", shipmentId);
    				mapShipment.put("statusId", shipmentStatusId);
    				dispatcher.runSync("updateShipment", mapShipment);
    			} else {
    				return ServiceUtil.returnError("OLBIUS: Can not changed shipment status from "+ shipment.getString("statusId") + "to " + shipmentStatusId);
    			}
    		} else {
    			return ServiceUtil.returnError("OLBIUS: changeShipmentStatusFromDelivery Error");
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling changeShipmentStatusFromDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	successResult.put("shipmentId", shipmentId);
    	successResult.put("deliveryId", deliveryId);
    	return successResult;
    }
	
	public static Map<String, Object> updateDeliveryTotal(DispatchContext ctx, Map<String, Object> context){
		String deliveryId = (String)context.get("deliveryId");
		String pathScanFile = (String)context.get("pathScanFile");
		String pathScanFileExpt = (String)context.get("pathScanFileExpt");
		Long actualArrivalDate = (Long)context.get("actualArrivalDate");
		Long actualStartDate = (Long)context.get("actualStartDate");
		String orderId = (String)context.get("orderId");
		String facilityId = (String)context.get("facilityId");
		Long datetimeReceived = (Long)context.get("datetimeReceived");
		String listDeliveryItems = (String)context.get("listDeliveryItems");
		Object listReturnItems = (Object)context.get("listReturnItems");
		String listNoteItems = (String)context.get("listNoteItems");
		Map<String, Object> updateDlvItemList = FastMap.newInstance();
		Map<String, Object> updateOrderNotes = FastMap.newInstance();
		Map<String, Object> updateOrderReturn = FastMap.newInstance();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		updateDlvItemList.put("listDeliveryItems", listDeliveryItems);
		updateDlvItemList.put("pathScanFile", pathScanFile);
		updateDlvItemList.put("pathScanFileExpt", pathScanFileExpt);
		updateDlvItemList.put("deliveryId", deliveryId);
		updateDlvItemList.put("actualStartDate", actualStartDate);
		updateDlvItemList.put("actualArrivalDate", actualArrivalDate);
		updateDlvItemList.put("userLogin", userLogin);
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			dispatcher.runSync("updateDeliveryItemList", updateDlvItemList);
		} catch (GenericServiceException e){
			return ServiceUtil.returnError("OLBIUS: updateDeliveryItemList error !" + e.toString());
		}
		
		if (UtilValidate.isNotEmpty(listReturnItems)){
			updateOrderNotes.put("orderId", orderId);
			updateOrderNotes.put("listNoteItems", listNoteItems);
			updateOrderNotes.put("userLogin", userLogin);
			
			try {
				dispatcher.runSync("updateOrderNote", updateOrderNotes);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: updateOrderNotes error! " + e.toString());
			}
		}
		
		if (UtilValidate.isNotEmpty(listReturnItems)) {
			updateOrderReturn.put("orderId", orderId);
			updateOrderReturn.put("facilityId", facilityId);
			updateOrderReturn.put("datetimeReceived", datetimeReceived);
			updateOrderReturn.put("listReturnItems", listReturnItems);
			updateOrderReturn.put("userLogin", userLogin);
			
			try {
				dispatcher.runSync("receiveReturnItems", updateOrderReturn);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: receiveReturnItems error! " + e.toString());
			}
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> changeAllDeliveryStatusByOrder(DispatchContext ctx, Map<String, Object> context){
		String deliveryStatusId = (String)context.get("deliveryStatusId");
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
			if (UtilValidate.isNotEmpty(objOrderHeader)) {
				String orderTypeId = objOrderHeader.getString("orderTypeId");
				EntityCondition orderCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
				EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("DLV_APPROVED", "DLV_CREATED"));
				List<EntityCondition> listConds = new ArrayList<EntityCondition>();
				listConds.add(orderCond);
				listConds.add(statusCond);
				List<GenericValue> listDlvs = delegator.findList("Delivery", EntityCondition.makeCondition(listConds), null, null, null, false);
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				if ("PURCHASE_ORDER".equals(orderTypeId)){
					for (GenericValue dlv : listDlvs) {
						Map<String, Object> map = FastMap.newInstance();
						map.put("userLogin", userLogin);
						map.put("statusId", deliveryStatusId);
						map.put("deliveryId", dlv.getString("deliveryId"));
						try {
							dispatcher.runSync("changeDeliveryStatus", map);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: changeDeliveryStatus error! " + e.toString());
						}
					}
				} else {
					// another case
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		return successResult;
	}
	
	public static Map<String, Object> loadDeliveryItemToEdit(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String deliveryId = (String) context.get("deliveryId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		String deliveryTypeId = objDelivery.getString("deliveryTypeId");
		EntityCondition cond1 = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
		List<String> listStt = new ArrayList<String>();
		listStt = UtilMisc.toList("DELI_ITEM_APPROVED", "DELI_ITEM_CREATED");
		
		if ("DELIVERY_PURCHASE".equals(deliveryTypeId)) {
			listStt.add("DELI_ITEM_EXPORTED");
		}
		EntityCondition cond2 = EntityCondition.makeCondition("statusId", EntityOperator.IN, listStt);
		List<EntityCondition> conds = new ArrayList<EntityCondition>();
		conds.add(cond2);
		conds.add(cond1);
		List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
		List<HashMap<String, Object>> listOrderItems = new ArrayList<HashMap<String, Object>>();
		if ("DELIVERY_PURCHASE".equals(deliveryTypeId) || "DELIVERY_SALES".equals(deliveryTypeId)){
			for (GenericValue dlvItem : listDeliveryItems) {
				String orderId = dlvItem.getString("fromOrderId");
				String orderItemSeqId = dlvItem.getString("fromOrderItemSeqId");
				List<GenericValue> orderItems = FastList.newInstance();
				try {
					orderItems = LogisticsProductUtil.getOrderItemRemains(delegator, orderId, "WAREHOUSE", deliveryId, orderItemSeqId);
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when getOrderItemRemains: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
	   			BigDecimal remainQty = BigDecimal.ZERO;
	   			for (GenericValue item : orderItems) {
					remainQty = remainQty.add(item.getBigDecimal("quantity"));
				}
				GenericValue objOrderItem = delegator.findOne("OrderItem", false,
						UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
				String productId = objOrderItem.getString("productId");
				HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	   			tmpMap.put("orderItemSeqId", orderItemSeqId);
	   			tmpMap.put("orderId", orderId);
	   			tmpMap.put("deliveryItemSeqId", dlvItem.getString("deliveryItemSeqId"));
	   			tmpMap.put("deliveryId", deliveryId);
	   			String baseQuantityUomId = null;
	   			String baseWeightUomId = null;
	   			String productName = null;
	   			String productCode = null;
	   			boolean isKg = false;
	   			try {
	   				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
	   				baseQuantityUomId = product.getString("quantityUomId");
	   				baseWeightUomId = product.getString("weightUomId");
	   				productName = product.getString("productName");
	   				productCode = product.getString("productCode");
	   				isKg = ProductUtil.isWeightProduct(delegator, productId);
	   			} catch (GenericEntityException e) {
	   				return ServiceUtil.returnError("findOne product error!"+ e.toString());
	   			}
	   			BigDecimal convertNumber = BigDecimal.ONE;
	   			if (baseQuantityUomId != null && objOrderItem.getString("quantityUomId") != null){
	   				convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, objOrderItem.getString("quantityUomId"), baseQuantityUomId);
	   			}
	   			tmpMap.put("productId", productId);
	   			tmpMap.put("isPromo", objOrderItem.getString("isPromo"));
	   			tmpMap.put("productCode", productCode);
	   			tmpMap.put("productName", productName);
	   			tmpMap.put("quantityUomId", objOrderItem.getString("quantityUomId"));
	   			tmpMap.put("weightUomId", objOrderItem.getString("weightUomId"));
	   			tmpMap.put("isKg", isKg);
	   			tmpMap.put("selectedAmount", objOrderItem.getString("selectedAmount"));
	   			tmpMap.put("fromOrderId", objOrderItem.getString("orderId"));
	   			tmpMap.put("fromOrderItemSeqId", objOrderItem.getString("orderItemSeqId"));
	   			tmpMap.put("createdQuantity", new BigDecimal(0));
	   			tmpMap.put("baseQuantityUomId", baseQuantityUomId);
	   			tmpMap.put("baseWeightUomId", baseWeightUomId);
	   			tmpMap.put("convertNumber", convertNumber);
	   			
   				BigDecimal orderQty = objOrderItem.getBigDecimal("alternativeQuantity");
   				tmpMap.put("requiredQuantity", orderQty);
   				tmpMap.put("quantity", remainQty);
	   			List<Map<String, Object>> listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
	   			tmpMap.put("quantityUomIds", listQtyUoms);
	   			tmpMap.put("quantityUomIds", JsonUtil.convertListMapToJSON(listQtyUoms));
	   			if (isKg){
	   				tmpMap.put("createdQuantity", dlvItem.getBigDecimal("amount"));
	   			} else {
	   				tmpMap.put("createdQuantity", dlvItem.getBigDecimal("quantity"));
	   			}
	   			
	   			listOrderItems.add(tmpMap);
	   		}
		} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			for (GenericValue dlvItem : listDeliveryItems) {
				String transferId = dlvItem.getString("fromTransferId");
				String transferItemSeqId = dlvItem.getString("fromTransferItemSeqId");
				List<GenericValue> transferItems = FastList.newInstance();
				try {
					transferItems = LogisticsProductUtil.getTransferItemRemains(delegator, transferId, "WAREHOUSE", company, deliveryId, transferItemSeqId);
				} catch (GenericServiceException e) {
					String errMsg = "OLBIUS: Fatal error when getOrderItemRemains: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
	   			BigDecimal remainQty = BigDecimal.ZERO;
	   			for (GenericValue item : transferItems) {
					remainQty = remainQty.add(item.getBigDecimal("quantity"));
				}
				GenericValue objTransferItem = delegator.findOne("TransferItem", false,
						UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
				String productId = objTransferItem.getString("productId");
				HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	   			tmpMap.put("transferItemSeqId", transferItemSeqId);
	   			tmpMap.put("transferId", transferId);
	   			tmpMap.put("deliveryItemSeqId", dlvItem.getString("deliveryItemSeqId"));
	   			tmpMap.put("deliveryId", deliveryId);
	   			String requireAmount = null;
	   			BigDecimal convertNumber = BigDecimal.ONE;
	   			if (UtilValidate.isNotEmpty(objTransferItem.getString("quantityUomId"))){
	   				convertNumber = LogisticsProductUtil.getConvertPackingToBaseUom(delegator, productId, objTransferItem.getString("quantityUomId"));
	   			}
	   			GenericValue objProduct = null;
				try {
					objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
	   			tmpMap.put("productId", productId);
	   			tmpMap.put("isPromo", objTransferItem.getString("isPromo"));
	   			tmpMap.put("productCode", objProduct.getString("productCode"));
	   			tmpMap.put("productName", objProduct.getString("productName"));
	   			tmpMap.put("quantityUomId", objTransferItem.getString("quantityUomId"));
	   			tmpMap.put("weightUomId", objTransferItem.getString("weightUomId"));
	   			tmpMap.put("requireAmount", requireAmount);
	   			tmpMap.put("selectedAmount", objTransferItem.getString("selectedAmount"));
	   			tmpMap.put("fromTransferId", objTransferItem.getString("transferId"));
	   			tmpMap.put("fromTransferItemSeqId", objTransferItem.getString("transferItemSeqId"));
	   			tmpMap.put("createdQuantity", new BigDecimal(0));
	   			tmpMap.put("baseQuantityUomId", objProduct.getString("quantityUomId"));
	   			tmpMap.put("baseWeightUomId", objProduct.getString("weightUomId"));
	   			tmpMap.put("convertNumber", convertNumber);
	   			
   				BigDecimal orderQty = objTransferItem.getBigDecimal("quantity");
   				tmpMap.put("requiredQuantity", orderQty);
   				tmpMap.put("quantity", remainQty);
	   			List<Map<String, Object>> listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
	   			tmpMap.put("quantityUomIds", listQtyUoms);
	   			tmpMap.put("quantityUomIds", JsonUtil.convertListMapToJSON(listQtyUoms));
	   			
	   			tmpMap.put("createdQuantity", dlvItem.getBigDecimal("quantity"));
	   			listOrderItems.add(tmpMap);
	   		}
		}
		result.put("listDeliveryItems", listOrderItems); 
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,Object> updateDeliveryItemInfos(DispatchContext ctx, Map<String, Object> context){
	    List<Map<String, Object>> listDeliveryItems = null;
	    Delegator delegator = ctx.getDelegator();
	    String strList = (String)context.get("listDeliveryItems");
	    try {
            listDeliveryItems = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", strList);
        } catch (ParseException e1) {
            return ServiceUtil.returnError(e1.toString());
        }
	    for (Map<String, Object> item : listDeliveryItems) {
    		String quantityStr = (String)item.get("quantity");
    		if (UtilValidate.isNotEmpty(quantityStr)) {
    			BigDecimal quantity = new BigDecimal(quantityStr);
    			if (UtilValidate.isNotEmpty(quantity)) {
    				if (quantity.compareTo(BigDecimal.ZERO) > 0) {
    					String dlvId = (String)item.get("deliveryId");
	    				String dlvItemSeqId = (String)item.get("deliveryItemSeqId");
	    				GenericValue objDelivery;
						try {
							objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", dlvId));
						} catch (GenericEntityException e) {
							String errMsg = "Fatal error when findOne Delivery: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
	    				GenericValue objDeliveryItem;
						try {
							objDeliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", dlvId, "deliveryItemSeqId", dlvItemSeqId));
						} catch (GenericEntityException e) {
							String errMsg = "Fatal error when findOne DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						String productId = null;
						String deliveryTypeId = objDelivery.getString("deliveryTypeId");
						String orderId = null;
						String orderItemSeqId = null;
						String transferId = null;
						String transferItemSeqId = null;
						if ("DELIVERY_SALES".equals(deliveryTypeId) || "DELIVERY_PURCHASE".equals(deliveryTypeId)){
							orderId = (String)item.get("fromOrderId");
							orderItemSeqId = (String)item.get("fromOrderItemSeqId");
							GenericValue objOrderItem;
							try {
								objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
							} catch (GenericEntityException e) {
								String errMsg = "Fatal error when findOne OrderItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
							if (UtilValidate.isNotEmpty(objOrderItem)) {
								productId = objOrderItem.getString("productId");
							}
						} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
							transferId = (String)item.get("fromTransferId");
							transferItemSeqId = (String)item.get("fromTransferItemSeqId");
							GenericValue objTransferItem = null;
							try {
								objTransferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
							} catch (GenericEntityException e) {
								String errMsg = "Fatal error when findOne OrderItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
							if (UtilValidate.isNotEmpty(objTransferItem)) {
								productId = objTransferItem.getString("productId");
							}
						}
	    				if (UtilValidate.isEmpty(productId)) {
	    					String errMsg = "Fatal error when updateDeliveryItemInfos: productId not found!";
							Debug.logError(errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						Boolean reqAm = false;
		    			if (ProductUtil.isWeightProduct(delegator, productId)) {
		    				reqAm = true;
		    			}
						
	    				String deliveryStatusId = objDelivery.getString("statusId");
	    				if (UtilValidate.isNotEmpty(objDeliveryItem)) {
	    					if (reqAm) {
	    						objDeliveryItem.put("amount", quantity);
	    					} else {
	    						objDeliveryItem.put("quantity", quantity);
	    					}
	    					
	    					if ("DELIVERY_PURCHASE".equals(deliveryTypeId) && "DLV_EXPORTED".equals(deliveryStatusId)){
	    						if (reqAm) {
	    							objDeliveryItem.put("actualExportedAmount", quantity);
	    						} else {
	    							objDeliveryItem.put("actualExportedQuantity", quantity);
	    						}
	    					}
	    					try {
								delegator.store(objDeliveryItem);
							} catch (GenericEntityException e) {
								String errMsg = "Fatal error when Store DeliveryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						} else {
							// create new
							
							if (UtilValidate.isNotEmpty(objDelivery) && (("DLV_CREATED".equals(deliveryStatusId) || "DLV_APPROVED".equals(deliveryStatusId)) || ("DLV_EXPORTED".equals(deliveryStatusId) && "DELIVERY_PURCHASE".equals(deliveryTypeId)))) {
								GenericValue userLogin = (GenericValue) context.get("userLogin");
								LocalDispatcher dispatcher = ctx.getDispatcher();
								
								Map<String, Object> map = FastMap.newInstance();
								map.put("userLogin", userLogin);
								map.put("fromOrderId", orderId);
								map.put("fromTransferId", transferId);
								map.put("deliveryId", dlvId);
								map.put("fromTransferItemSeqId", transferItemSeqId);
								map.put("fromOrderItemSeqId", orderItemSeqId);
								if (reqAm){
									map.put("quantity", BigDecimal.ONE);
									map.put("amount", quantity);
								} else {
									map.put("quantity", quantity);
								}
								try {
									Map<String, Object> mapResult = dispatcher.runSync("createDeliveryItem", map);
									dlvItemSeqId = (String)mapResult.get("deliveryItemSeqId");
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: createDeliveryItem error! " + e.toString());
								}
								if ("DLV_APPROVED".equals(objDelivery.getString("statusId"))){
									map = FastMap.newInstance();
			    					map.put("statusId", "DELI_ITEM_APPROVED");
			    					map.put("deliveryId", dlvId);
			    					map.put("deliveryItemSeqId", dlvItemSeqId);
			    					map.put("userLogin", userLogin);
			    					try {
										dispatcher.runSync("changeDeliveryItemStatus", map);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: changeDeliveryItemStatus error! " + e.toString());
									}
								}
								if ("DLV_EXPORTED".equals(deliveryStatusId) && "DELIVERY_PURCHASE".equals(deliveryTypeId)){
									GenericValue objNewDeliveryItem = null;
									try {
										objNewDeliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", dlvId, "deliveryItemSeqId", dlvItemSeqId));
									} catch (GenericEntityException e) {
										String errMsg = "Fatal error when findOne DeliveryItem: " + e.toString();
										Debug.logError(e, errMsg, module);
										return ServiceUtil.returnError(errMsg);
									}
									
									objNewDeliveryItem.put("actualExportedQuantity", quantity);
									try {
										delegator.store(objNewDeliveryItem);
									} catch (GenericEntityException e) {
										String errMsg = "Fatal error when Store DeliveryItem: " + e.toString();
										Debug.logError(e, errMsg, module);
										return ServiceUtil.returnError(errMsg);
									}
									
									map = FastMap.newInstance();
			    					map.put("statusId", "DELI_ITEM_EXPORTED");
			    					map.put("deliveryId", dlvId);
			    					map.put("deliveryItemSeqId", dlvItemSeqId);
			    					map.put("userLogin", userLogin);
			    					try {
										dispatcher.runSync("changeDeliveryItemStatus", map);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: changeDeliveryItemStatus error! " + e.toString());
									}
								}
							}
						}
    				} else {
    					String dlvId = (String)item.get("deliveryId");
	    				String dlvItemSeqId = (String)item.get("deliveryItemSeqId");
	    				GenericValue objDeliveryItem;
						try {
							objDeliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", dlvId, "deliveryItemSeqId", dlvItemSeqId));
						} catch (GenericEntityException e) {
							String errMsg = "Fatal error when findOne DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
	    				GenericValue userLogin = (GenericValue) context.get("userLogin");
	    				if (UtilValidate.isNotEmpty(objDeliveryItem)) {
	    					Map<String, Object> map = FastMap.newInstance();
	    					map.put("statusId", "DELI_ITEM_CANCELLED");
	    					map.put("deliveryId", dlvId);
	    					map.put("deliveryItemSeqId", dlvItemSeqId);
	    					map.put("userLogin", userLogin);
	    					LocalDispatcher dispatcher = ctx.getDispatcher();
	    					try {
								dispatcher.runSync("changeDeliveryItemStatus", map);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: changeDeliveryItemStatus error! " + e.toString());
							}
						}
    				}
				}
			}
	    }
	    Map<String, Object> result = new FastMap<String, Object>();
		return result;
	}
	
	public static Map<String, Object> autoCancelDeliveryByOrderItem(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		String changeReason = null;
		if (UtilValidate.isNotEmpty(context.get("changeReason"))) {
			changeReason = (String)context.get("changeReason");
		}
		EntityCondition cond1 = EntityCondition.makeCondition("fromOrderId", EntityOperator.EQUALS, orderId);
		EntityCondition cond2 = EntityCondition.makeCondition("fromOrderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
		EntityCondition cond3 = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("DELI_ITEM_CREATED", "DELI_ITEM_APPROVED"));
		GenericValue objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		if (UtilValidate.isNotEmpty(objOrderHeader)) {
			String orderTypeId = objOrderHeader.getString("orderTypeId");
			if ("PURCHASE_ORDER".equals(orderTypeId)){
				cond3 = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("DELI_ITEM_CREATED", "DELI_ITEM_APPROVED", "DELI_ITEM_EXPORTED"));
			}
			List<EntityCondition> conds = new ArrayList<EntityCondition>();
			conds.add(cond1);
			conds.add(cond2);
			conds.add(cond3);
			List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
			List<String> listDeliveryIds = new ArrayList<String>();
			listDeliveryIds = EntityUtil.getFieldListFromEntityList(listDeliveryItems, "deliveryId", true); 
			if (!listDeliveryIds.isEmpty()){
				for (String deliveryId : listDeliveryIds) {
					GenericValue userLogin = (GenericValue) context.get("userLogin");
					LocalDispatcher dispatcher = ctx.getDispatcher();
					Map<String, Object> map = FastMap.newInstance();
					map.put("deliveryId", deliveryId);
					map.put("newStatusId", "DLV_CANCELLED");
					map.put("setItemStatus", "Y");
					map.put("newItemStatus", "DELI_ITEM_CANCELLED");
					map.put("changeReason", changeReason);
					map.put("userLogin", userLogin);
					try {
						dispatcher.runSync("updateDeliveryStatus", map);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: updateDeliveryStatus error! " + e.toString());
					}
				}
			}
		}
		return result;
	}
	
	public static Map<String, Object> getProductNotExportedYet(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String deliveryId = (String) context.get("deliveryId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		GenericValue objDelivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		String deliveryTypeId = objDelivery.getString("deliveryTypeId");
		if (UtilValidate.isNotEmpty(objDelivery)) {
			if ("DELIVERY_SALES".equals(deliveryTypeId) || "DELIVERY_PURCHASE".equals(deliveryTypeId)){
				String orderId = objDelivery.getString("orderId");
				if (UtilValidate.isNotEmpty(orderId)) {
					try {
						listProductTmps = LogisticsProductUtil.getOrderItemRemains(delegator, orderId, "WAREHOUSE", deliveryId);
						List<EntityCondition> conds = new ArrayList<EntityCondition>();
						conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED"));
						conds.add(EntityCondition.makeCondition("fromOrderId", orderId));
						List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
						if (!listProductTmps.isEmpty()){
							for (GenericValue item : listProductTmps) {
								String orderItemSeqId = item.getString("orderItemSeqId");
								HashMap<String, Object> tmpMap = new HashMap<String, Object>();
					   			tmpMap.put("orderItemSeqId", orderItemSeqId);
					   			tmpMap.put("orderId", orderId);
					   			
					   			GenericValue objOrderItem = delegator.findOne("OrderItem", false,
										UtilMisc.toMap("orderId", orderId, "orderItemSeqId", item.getString("orderItemSeqId")));
					   			String productId = objOrderItem.getString("productId");
					   			GenericValue objProduct = delegator.findOne("Product", false,
										UtilMisc.toMap("productId", productId));
					   			
					   			Boolean reqAm = ProductUtil.isWeightProduct(delegator, productId);
					   			
					   			tmpMap.put("productId", productId);
					   			tmpMap.put("productCode", objProduct.getString("productCode"));
					   			tmpMap.put("productName", objProduct.getString("productName"));
					   			tmpMap.put("quantityUomId", objOrderItem.getString("quantityUomId"));
					   			tmpMap.put("weightUomId", objOrderItem.getString("weightUomId"));
					   			tmpMap.put("isPromo", objOrderItem.getString("isPromo"));
					   			tmpMap.put("requireAmount", objProduct.getString("requireAmount"));
					   			tmpMap.put("selectedAmount", objOrderItem.getString("selectedAmount"));
					   			tmpMap.put("fromOrderId", objOrderItem.getString("orderId"));
					   			tmpMap.put("fromOrderItemSeqId", objOrderItem.getString("orderItemSeqId"));
					   			tmpMap.put("createdQuantity", new BigDecimal(0));
					   			tmpMap.put("baseQuantityUomId", objProduct.getString("quantityUomId"));
					   			tmpMap.put("baseWeightUomId", objProduct.getString("weightUomId"));
					   			
					   			BigDecimal convertNumber = BigDecimal.ONE;
					   			if (objProduct.getString("quantityUomId") != null && objOrderItem.getString("quantityUomId") != null){
					   				convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, objOrderItem.getString("quantityUomId"), objProduct.getString("quantityUomId"));
					   			}
					   			
					   			tmpMap.put("convertNumber", convertNumber);
					   			BigDecimal createdQuantity = BigDecimal.ZERO;
				   				for (GenericValue itemDlv : listDlvItems) {
									if (itemDlv.getString("fromOrderId").equals(orderId) && itemDlv.getString("fromOrderItemSeqId").equals(orderItemSeqId)) {
										String itemStatus = itemDlv.getString("statusId");
										if ("DELI_ITEM_CREATED".equals(itemStatus) || "DELI_ITEM_APPROVED".equals(itemStatus)){
											if (reqAm){
												createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("amount"));
											} else {
												createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("quantity"));
											}
										}
										if ("DELI_ITEM_EXPORTED".equals(itemStatus)){
											if (reqAm){
												createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualExportedAmount"));
											} else {
												createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualExportedQuantity"));
											}
										}
										if ("DELI_ITEM_DELIVERED".equals(itemStatus)){
											if ("DELIVERY_PURCHASE".equals(deliveryTypeId)){
												if (reqAm){
													createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualDeliveredAmount"));
												} else {
													createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualDeliveredQuantity"));
												}
											} else {
												if (reqAm){
													createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualExportedAmount"));
												} else {
													createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualExportedQuantity"));
												}
											}
										}
									}
								}
				   				
				   				BigDecimal orderQty = BigDecimal.ZERO;
				   				if (reqAm){
				   					orderQty = objOrderItem.getBigDecimal("quantity").multiply(objOrderItem.getBigDecimal("selectedAmount"));
				   				} else {
				   					orderQty = objOrderItem.getBigDecimal("quantity");
				   				}
				   				tmpMap.put("requiredQuantity", orderQty);
				   				BigDecimal quantity = orderQty.subtract(createdQuantity);
				   				if (quantity.compareTo(BigDecimal.ZERO) > 0){
				   					tmpMap.put("quantity", quantity);
					   				
					   				tmpMap.put("createdQuantity", createdQuantity);
					   				
						   			List<Map<String, Object>> listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
						   			tmpMap.put("quantityUomIds", listQtyUoms);
						   			tmpMap.put("quantityUomIds", JsonUtil.convertListMapToJSON(listQtyUoms));
						   			
						   			listProducts.add(tmpMap);
				   				}
							}
						}
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: getOrderItemRemains error! " + e.toString());
					}
				}
			} else if ("DELIVERY_TRANSFER".equals(deliveryTypeId)){
				String transferId = objDelivery.getString("transferId");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				if (UtilValidate.isNotEmpty(transferId)) {
					String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
					try {
						listProductTmps = LogisticsProductUtil.getTransferItemRemains(delegator, transferId, "WAREHOUSE", company, deliveryId);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: getTransferItemRemains error! " + e.toString());
					}
					List<EntityCondition> conds = new ArrayList<EntityCondition>();
					conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED"));
					conds.add(EntityCondition.makeCondition("fromTransferId", transferId));
					List<GenericValue> listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
					if (!listProductTmps.isEmpty()){
						for (GenericValue item : listProductTmps) {
							String transferItemSeqId = item.getString("transferItemSeqId");
							HashMap<String, Object> tmpMap = new HashMap<String, Object>();
				   			tmpMap.put("transferItemSeqId", transferItemSeqId);
				   			tmpMap.put("transferId", transferId);
				   			
				   			GenericValue objTransferItem = delegator.findOne("TransferItem", false, UtilMisc.toMap("transferId", transferId, "transferItemSeqId", transferItemSeqId));
				   			String productId = objTransferItem.getString("productId");
				   			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				   			
				   			Boolean reqAm = ProductUtil.isWeightProduct(delegator, productId);
				   			
				   			tmpMap.put("productId", productId);
				   			tmpMap.put("productCode", objProduct.getString("productCode"));
				   			tmpMap.put("productName", objProduct.getString("productName"));
				   			tmpMap.put("quantityUomId", objTransferItem.getString("quantityUomId"));
				   			tmpMap.put("weightUomId", objTransferItem.getString("weightUomId"));
				   			tmpMap.put("requireAmount", objProduct.getString("requireAmount"));
				   			tmpMap.put("selectedAmount", objTransferItem.getString("selectedAmount"));
				   			tmpMap.put("fromTransferId", transferId);
				   			tmpMap.put("fromTransferItemSeqId", transferItemSeqId);
				   			tmpMap.put("baseQuantityUomId", objProduct.getString("quantityUomId"));
				   			tmpMap.put("baseWeightUomId", objProduct.getString("weightUomId"));
				   			
				   			BigDecimal convertNumber = BigDecimal.ONE;
				   			if (UtilValidate.isNotEmpty(objTransferItem.getString("quantityUomId"))){
				   				convertNumber = LogisticsProductUtil.getConvertPackingToBaseUom(delegator, productId, objTransferItem.getString("quantityUomId"));
				   			}
				   			
				   			tmpMap.put("convertNumber", convertNumber);
				   			BigDecimal createdQuantity = BigDecimal.ZERO;
			   				for (GenericValue itemDlv : listDlvItems) {
								if (itemDlv.getString("fromTransferId").equals(transferId) && itemDlv.getString("fromTransferItemSeqId").equals(transferItemSeqId)) {
									String itemStatus = itemDlv.getString("statusId");
									if ("DELI_ITEM_CREATED".equals(itemStatus) || "DELI_ITEM_APPROVED".equals(itemStatus)){
										if (reqAm){
											createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("amount"));
										} else {
											createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("quantity"));
										}
									}
									if ("DELI_ITEM_EXPORTED".equals(itemStatus)){
										if (reqAm){
											createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualExportedAmount"));
										} else {
											createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualExportedQuantity"));
										}
									}
									if ("DELI_ITEM_DELIVERED".equals(itemStatus)){
										if (reqAm){
											createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualDeliveredAmount"));
										} else {
											createdQuantity = createdQuantity.add(itemDlv.getBigDecimal("actualDeliveredQuantity"));
										}
									}
								}
							}
			   				
			   				BigDecimal orderQty = BigDecimal.ZERO;
			   				if (reqAm){
			   					orderQty = objTransferItem.getBigDecimal("quantity").multiply(objTransferItem.getBigDecimal("selectedAmount"));
			   				} else {
			   					orderQty = objTransferItem.getBigDecimal("quantity");
			   				}
			   				tmpMap.put("requiredQuantity", orderQty);
			   				BigDecimal quantity = orderQty.subtract(createdQuantity);
			   				if (quantity.compareTo(BigDecimal.ZERO) > 0){
			   					tmpMap.put("quantity", quantity);
				   				
				   				tmpMap.put("createdQuantity", createdQuantity);
				   				
					   			List<Map<String, Object>> listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
					   			tmpMap.put("quantityUomIds", listQtyUoms);
					   			tmpMap.put("quantityUomIds", JsonUtil.convertListMapToJSON(listQtyUoms));
					   			
					   			listProducts.add(tmpMap);
			   				}
						}
					}
				}
			}
		}
		result.put("listProducts", listProducts);
		return result;
	}
	
	public static Map<String, Object> createDeliveryItem(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		BigDecimal quantity = (BigDecimal)context.get("quantity");
		Delegator delegator = ctx.getDelegator();
		String deliveryItemSeqId = null;
		if (UtilValidate.isNotEmpty(quantity) && quantity.compareTo(BigDecimal.ZERO) > 0) {
			GenericValue newItem = delegator.makeValue("DeliveryItem");
			newItem.setNonPKFields(context);
			newItem.put("deliveryId", (String)context.get("deliveryId"));
			delegator.setNextSubSeqId(newItem, "deliveryItemSeqId", 5, 1);
			newItem.put("statusId", "DELI_ITEM_CREATED");
			delegator.create(newItem);
			deliveryItemSeqId = newItem.getString("deliveryItemSeqId");
		}
		result.put("deliveryItemSeqId", deliveryItemSeqId);
		return result;
	}
	public static Map<String, Object> createDeliveryFromPicklist(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String picklistId = (String)context.get("picklistId");
		GenericValue objPicklist = delegator.findOne("Picklist", false, UtilMisc.toMap("picklistId", picklistId));
		if (UtilValidate.isNotEmpty(objPicklist)) {
			List<GenericValue> listBins = delegator.findList("PicklistBin",
					EntityCondition.makeCondition("picklistId", EntityOperator.EQUALS, picklistId), null, null, null, false);
			if (!listBins.isEmpty()){
				LocalDispatcher dispatcher = ctx.getDispatcher();
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				for (GenericValue bin : listBins) {
					String picklistBinId = bin.getString("picklistBinId");
					Map<String, Object> map = FastMap.newInstance();
					map.put("userLogin", userLogin);
					map.put("picklistBinId", picklistBinId);
					try {
						dispatcher.runSync("createDeliveryFromPicklistBin", map);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: createDeliveryFromPicklistBin error! " + e.toString());
					}
				}
			}
		}
		return result;
	}
	public static Map<String, Object> createDeliveryFromPicklistBin(DispatchContext ctx, Map<String,?extends Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String picklistBinId = (String)context.get("picklistBinId");
		GenericValue objPicklistBin = delegator.findOne("PicklistBin", false, UtilMisc.toMap("picklistBinId", picklistBinId));
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		if (UtilValidate.isNotEmpty(objPicklistBin)) {
			// check qoh enough to export 
			Boolean checkEnough = true;
			Map<String, Object> mapCheck = FastMap.newInstance();
			mapCheck.put("picklistBinId", picklistBinId);
			mapCheck.put("userLogin", userLogin);
			try {
				Map<String, Object> x = dispatcher.runSync("checkQuantityOnHandEnoughToPick", mapCheck);
				if (UtilValidate.isNotEmpty(x.get("checkEnough"))) {
					checkEnough = (Boolean)x.get("checkEnough");
				} 
			} catch (GenericServiceException e) {
				Debug.log(e.toString());
				return ServiceUtil.returnError("OLBIUS: checkQuantityOnHandEnoughToPick error!");
			}
			if (!checkEnough){
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLNotEnoughInventoryToExport", locale));
			}
			// refresh picklistItem - get new inventoryItem enough quantity
			Map<String, Object> mapRf = FastMap.newInstance();
			mapRf.put("picklistBinId", picklistBinId);
			mapRf.put("userLogin", userLogin);
			try {
				dispatcher.runSync("refreshInventoryItemForPicklistItem", mapRf);
			} catch (GenericServiceException e) {
				Debug.log(e.toString());
				return ServiceUtil.returnError("OLBIUS: refreshInventoryItemForPicklistItem error!");
			}
			
			String orderId = objPicklistBin.getString("primaryOrderId");
			String picklistId = objPicklistBin.getString("picklistId");
			if (UtilValidate.isNotEmpty(orderId)) {
				GenericValue objOrderHeader = null;
				try {
					objOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
				} catch (GenericEntityException e) {
					return ServiceUtil.returnError("OLBIUS: findOne OrderHeader error! " + e.toString());
				}
				if (UtilValidate.isEmpty(objOrderHeader)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "OrderNotFound", locale));
				}
				if (!"ORDER_APPROVED".equals(objOrderHeader.getString("statusId"))){
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "OrderStatusChangedCannotCreateDelivery", locale));
				}
				List<GenericValue> orderItems = FastList.newInstance();
				try {
				orderItems = LogisticsProductUtil.getOrderItemRemains(delegator, orderId, "WAREHOUSE");
					
				} catch (GenericServiceException e1) {
					e1.printStackTrace();
					return ServiceUtil.returnError("OLBIUS: getOrderItemRemains error! - orderId:" + orderId + e1.toString());
				}
				if (orderItems.isEmpty()){
					Debug.log("OLBIUS: createDeliveryFromPicklistBin error! - orderId:" + orderId);
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLOrderHasBeenExported", locale));
				}
				GenericValue objPicklist = delegator.findOne("Picklist", false, UtilMisc.toMap("picklistId", picklistId));
				EntityCondition cond1 = EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS, picklistBinId);
				EntityCondition cond2 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
				EntityCondition cond3 = EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "PICKITEM_APPROVED");
				List<EntityCondition> conds = new ArrayList<EntityCondition>();
				conds.add(cond1);
				conds.add(cond2);
				conds.add(cond3);
				List<GenericValue> listPicklistItems = delegator.findList("PicklistItem", EntityCondition.makeCondition(conds), null, null, null, false);
				if (!listPicklistItems.isEmpty()){
					List<GenericValue> listShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
					GenericValue orderShipGroup = listShipGroups.get(0);
		            String contactMech = orderShipGroup.getString("contactMechId");
		            List<GenericValue> customers = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);
		            String partyIdTo = null;
		            String weightUomDefault = "WT_kg";
		            if (!customers.isEmpty()){
		            	partyIdTo = customers.get(0).getString("partyId");
		            }
		            
		            List<GenericValue> pratys = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
		        	String partyIdFrom = pratys.get(0).getString("partyId");
		        	
		        	String facilityId = objPicklist.getString("facilityId");
		        	String contactMechFac = null;
	    			List<GenericValue> listFacilityContactMechs = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION")), null, null, null, false);
	    			listFacilityContactMechs = EntityUtil.filterByDate(listFacilityContactMechs);
	    			if (!listFacilityContactMechs.isEmpty()){
	    				contactMechFac = listFacilityContactMechs.get(0).getString("contactMechId");
	    			}
	    			
	    			List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
	            	for (GenericValue orderItem : listPicklistItems){
	            		Map<String, Object> map = FastMap.newInstance();
	            		map.put("orderId", orderId);
	            		map.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
	            		GenericValue objOrderItemX = null;
						try {
							objOrderItemX = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId")));
						} catch (GenericEntityException e) {
							return ServiceUtil.returnError("OLBIUS: findOne OrderItem error! " + e.toString());
						}
	            		map.put("inventoryItemId", orderItem.getString("inventoryItemId"));
	            		map.put("shipGroupSeqId", orderItem.getString("shipGroupSeqId"));
	            		map.put("picklistBinId", orderItem.getString("picklistBinId"));
	            		BigDecimal quantityNeed = orderItem.getBigDecimal("quantity");
	            		BigDecimal quantityRemain = BigDecimal.ZERO;
	            		
	            		for (GenericValue item : orderItems) {
	            			if (item.getString("orderItemSeqId").equals(orderItem.getString("orderItemSeqId"))){
	            				if (ProductUtil.isWeightProduct(delegator, objOrderItemX.getString("productId"))) {
	            					quantityRemain = quantityRemain.add(item.getBigDecimal("selectedAmount"));
								} else {
									quantityRemain = quantityRemain.add(item.getBigDecimal("quantity"));
								}
	            			}
						}
	            		if (quantityNeed.compareTo(quantityRemain) > 0){
	            			Debug.log("OLBIUS: createDeliveryFromPicklistBin error! - orderId:" + orderId);
	    					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLProductInOrderHasBeenExportOrEdited", locale) + ": " + objOrderItemX.getString("productId"));
	            		}
	            		
	            		List<GenericValue> listPicklistItemLocations = delegator.findList("PicklistItemLocation", EntityCondition.makeCondition(map), null, null, null, false);
	            		if (!listPicklistItemLocations.isEmpty()){
	            			for (GenericValue loc : listPicklistItemLocations) {
	            				Map<String, String> mapOrderItem = FastMap.newInstance();
			            		mapOrderItem.put("orderId", orderId);
			            		mapOrderItem.put("orderItemSeqId", loc.getString("orderItemSeqId"));
			            		GenericValue objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", loc.getString("orderItemSeqId")));
			            		GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", objOrderItem.getString("productId"))); 
			            		mapOrderItem.put("quantityUomId", objProduct.getString("quantityUomId"));
			            		
		            			BigDecimal quantity = loc.getBigDecimal("quantity");
			            		mapOrderItem.put("quantity", quantity.toString());
			            		if (ProductUtil.isWeightProduct(delegator, objOrderItem.getString("productId"))) {
			            			mapOrderItem.put("amount", loc.getBigDecimal("amount").toString());
								}
			            		mapOrderItem.put("inventoryItemId", loc.getString("inventoryItemId"));
			            		mapOrderItem.put("locationId", loc.getString("locationId"));
		            			listItems.add(mapOrderItem);
							}
	            		} else {
	            			Map<String, String> mapOrderItem = FastMap.newInstance();
		            		mapOrderItem.put("orderId", orderId);
		            		mapOrderItem.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
		            		GenericValue objOrderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId")));
		            		GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", objOrderItem.getString("productId"))); 
		            		mapOrderItem.put("quantityUomId", objProduct.getString("quantityUomId"));
		            		
	            			BigDecimal quantity = orderItem.getBigDecimal("quantity");
		            		mapOrderItem.put("quantity", quantity.toString());
		            		if (ProductUtil.isWeightProduct(delegator, objOrderItem.getString("productId"))) {
		            			mapOrderItem.put("amount", quantity.toString());
		            			mapOrderItem.put("quantity", BigDecimal.ONE.toString());
							}
		            		mapOrderItem.put("inventoryItemId", orderItem.getString("inventoryItemId"));
	            			listItems.add(mapOrderItem);
	            		}
	            	}
	            	
					Map<String, Object> mapDelivery = FastMap.newInstance();
	            	mapDelivery.put("orderId", orderId);
	            	mapDelivery.put("deliveryTypeId", "DELIVERY_SALES");
	            	mapDelivery.put("currencyUomId", objOrderHeader.getString("currencyUom"));
	            	mapDelivery.put("statusId", "DLV_CREATED");
	            	mapDelivery.put("originContactMechId", contactMechFac);
	            	mapDelivery.put("partyIdFrom", partyIdFrom);
	            	mapDelivery.put("partyIdTo", partyIdTo);
	            	mapDelivery.put("destContactMechId", contactMech);
	            	mapDelivery.put("originFacilityId", facilityId);
	            	mapDelivery.put("deliveryDate", objOrderHeader.getTimestamp("orderDate"));
	            	if (UtilValidate.isNotEmpty(objOrderHeader.getTimestamp("estimatedDeliveryDate"))) {
	            		mapDelivery.put("estimatedStartDate", objOrderHeader.getTimestamp("estimatedDeliveryDate"));
		            	mapDelivery.put("estimatedArrivalDate", objOrderHeader.getTimestamp("estimatedDeliveryDate"));
					} else {
						mapDelivery.put("estimatedStartDate", orderShipGroup.getTimestamp("shipAfterDate"));
		            	mapDelivery.put("estimatedArrivalDate", orderShipGroup.getTimestamp("shipByDate"));
					}
	            	mapDelivery.put("no", null);
	            	mapDelivery.put("defaultWeightUomId", weightUomDefault);
	            	mapDelivery.put("listOrderItems", listItems);
	            	mapDelivery.put("picklistBinId", picklistBinId);
	            	mapDelivery.put("userLogin", userLogin);
	            	String deliveryId = null;
	            	try {
	            		Map<String, Object> mapTmp = dispatcher.runSync("createDelivery", mapDelivery);
	            		deliveryId = (String)mapTmp.get("deliveryId");
	    			} catch (GenericServiceException e) {
	    				return ServiceUtil.returnError("createDelivery error!");
	    			}
	            	if (UtilValidate.isNotEmpty(deliveryId)) {
	            		result.put("deliveryId", deliveryId);
					}
	            	
	            	Map<String, Object> map = FastMap.newInstance();
	            	map.put("userLogin", userLogin);
	            	map.put("picklistBinId", picklistBinId);
	            	map.put("statusId", "PICKBIN_DLV_CREATED");
	            	try {
						dispatcher.runSync("changePicklistBinStatus", map);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: changePicklistBinStatus error! " + e.toString());
					}
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> exportProductFromSalesDelivery(DispatchContext ctx, Map<String, Object> context){
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		String deliveryId = (String)context.get("deliveryId");
		String listItems = (String)context.get("listProducts");
		
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when findOne Delivery: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		delivery.set("actualStartDate", UtilDateTime.nowTimestamp());
		try {
			delegator.store(delivery);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when Store Delivery: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		String facilityId = delivery.getString("originFacilityId");
		String orderId = delivery.getString("orderId");
		String shipmentId = delivery.getString("shipmentId");
		
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: exportProductFromSalesDelivery - JqxWidgetSevices.convert error!");
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
				return ServiceUtil.returnError("OLBIUS: exportProductFromSalesDelivery - JqxWidgetSevices.convert error!");
			}
		}
		
		Map<String, Object> mapAttributes = FastMap.newInstance();
		for (Map<String, Object> item : listProducts){
			String orderItemSeqId = (String)item.get("orderItemSeqId");
			if (!listProductAttrs.isEmpty()) {
				List<Map<String, Object>> listAttributes = FastList.newInstance();
				for (Map<String, Object> map : listProductAttrs) {
					if (map.containsKey("orderItemSeqId")){
						String seqId = (String)map.get("orderItemSeqId");
						if (UtilValidate.isNotEmpty(seqId) && orderItemSeqId.equals(seqId)) {
							listAttributes.add(map);
						}
					}
				}
				mapAttributes.put(orderItemSeqId, listAttributes);
			}
		}
		EntityCondition condDlv = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
		EntityCondition condOh = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
		Observer o = new DeliveryObserver();
		ItemSubject is = new DeliveryItemSubject();
		is.attach(o);
		for (Map<String, Object> item : listProducts){
			List<String> listOrderItemSeqIds = FastList.newInstance();
			String orderItemSeqId = (String)item.get("orderItemSeqId");
			if (!listOrderItemSeqIds.contains(orderItemSeqId)) listOrderItemSeqIds.add(orderItemSeqId);
			String productId = (String)item.get("productId");
			Boolean isWeight = false;
			if (ProductUtil.isWeightProduct(delegator, productId)){
				isWeight = true;
			}
			EntityCondition condPr = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			
			String deliveryItemSeqId = null;
			EntityCondition cond2 = EntityCondition.makeCondition("fromOrderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(condDlv);
			conds.add(cond2);
			List<GenericValue> listDlvItems = FastList.newInstance();
			GenericValue deliveryItem = null;
			try {
				listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
				if (!listDlvItems.isEmpty()) {
					deliveryItemSeqId = listDlvItems.get(0).getString("deliveryItemSeqId");
					deliveryItem = EntityUtil.getFirst(listDlvItems);
				}
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			BigDecimal quantity = BigDecimal.ZERO;
			BigDecimal promoQuantity = BigDecimal.ZERO;
			if (item.containsKey("quantity")){
				String quantityStr = (String)item.get("quantity");
				quantity = new BigDecimal(quantityStr);
			}
			if (item.containsKey("promoQuantity")){
				String quantityStr = (String)item.get("promoQuantity");
				promoQuantity = new BigDecimal(quantityStr);
			}
			if (quantity.compareTo(BigDecimal.ZERO) > 0 || promoQuantity.compareTo(BigDecimal.ZERO) > 0){
				
				List<Map<String, Object>> listAttribues = FastList.newInstance();
				if (mapAttributes.containsKey(orderItemSeqId)){
					listAttribues = (List<Map<String, Object>>)mapAttributes.get(orderItemSeqId);
				}
			
				List<Map<String, Object>> listInvs = FastList.newInstance();
				List<EntityCondition> condOis = FastList.newInstance();
				EntityCondition condPromo = EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, "Y");
				EntityCondition condStt = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_APPROVED");
				condOis.add(condPr);
				condOis.add(condPromo);
				condOis.add(condStt);
				condOis.add(condDlv);
				
				List<GenericValue> listItemPromos = FastList.newInstance();
				try {
					listItemPromos = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(condOis), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList OrderItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				List<Map<String, Object>> listInvPromos = FastList.newInstance();
				if (!listAttribues.isEmpty()){
					for (Map<String, Object> map : listAttribues) {
						Map<String, Object> attributes = FastMap.newInstance();
						attributes.put("productId", productId);
						attributes.put("facilityId", facilityId);
						attributes.put("ownerPartyId", company);
						
						String qtyStr = null;
						String promoQtyStr = null;
						if ((map.containsKey("quantity") || map.containsKey("promoQuantity")) && map.containsKey("productId")){
							qtyStr = (String)map.get("quantity");
							if (map.containsKey("promoQuantity")){
								promoQtyStr = (String)map.get("promoQuantity");
							}
							if (UtilValidate.isNotEmpty(qtyStr)) {
								BigDecimal quantityP = new BigDecimal(qtyStr);
								if (quantityP.compareTo(BigDecimal.ZERO) > 0){
									Boolean hasExp = false;
									Boolean hasMnf = false;
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
															hasExp = true;
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
															hasMnf = true;
														}
													}
												}
											}
										} else {
											attributes.put(key, map.get(key));
										}
									}
									if (!hasExp){
										attributes.put("expireDate", null);
									}
									if (!hasMnf){
										attributes.put("datetimeManufactured", null);
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
										for (Map<String, Object> inv : listInvTmps){
											inv.put("orderItemSeqId", orderItemSeqId);
											listInvs.add(inv);
										}
									}
								}
							}
							if (UtilValidate.isNotEmpty(promoQtyStr)) {
								BigDecimal quantityP = new BigDecimal(promoQtyStr);
								if (quantityP.compareTo(BigDecimal.ZERO) > 0){
									Boolean hasExp = false;
									Boolean hasMnf = false;
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
															hasExp = true;
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
															hasMnf = true;
														}
													}
												}
											}
										} else {
											attributes.put(key, map.get(key));
										}
									}
									if (!hasExp){
										attributes.put("expireDate", null);
									}
									if (!hasMnf){
										attributes.put("datetimeManufactured", null);
									}
									try {
										listInvPromos = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantityP);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when InventoryUtil.getInventoryItemsForQuantity: " + e.toString();
										Debug.logError(e, errMsg, module);
										return ServiceUtil.returnError(errMsg);
									}
								}
							}
						}
					}
				} else {
					if (UtilValidate.isNotEmpty(deliveryItem)) {
						Map<String, Object> attributes = FastMap.newInstance();
						attributes.put("productId", productId);
						attributes.put("facilityId", facilityId);
						attributes.put("ownerPartyId", company);
						List<String> orderBy = FastList.newInstance();
						orderBy.add("expireDate");
						if (quantity.compareTo(BigDecimal.ZERO) > 0){
							List<Map<String, Object>> listInvBasics = FastList.newInstance();
							try {
								listInvBasics = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantity, orderBy);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when InventoryUtil.getInventoryItemsForQuantity: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
							if (!listInvBasics.isEmpty()) {
								for (Map<String, Object> map : listInvBasics) {
									map.put("orderItemSeqId", orderItemSeqId);
									listInvs.add(map);
								}
							}
						}
						if (promoQuantity.compareTo(BigDecimal.ZERO) > 0){
							try {
								listInvPromos = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, promoQuantity, orderBy);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when InventoryUtil.getInventoryItemsForQuantity: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						} 
					}
				}
				
				List<String> itemPromoSeqNotExports = FastList.newInstance();
				if (!listInvPromos.isEmpty()){
					// tim 1 orderItem la item Promo phu hop (ko co uu tien giua 2 item promo cho cung 1 SP)
					List<String> allItemPromoSeqs = FastList.newInstance();
					if (!listItemPromos.isEmpty()){
						allItemPromoSeqs = EntityUtil.getFieldListFromEntityList(listItemPromos, "fromOrderItemSeqId", true);
						for (GenericValue oi : listItemPromos) {
							String orderItemSeqIdTmp = oi.getString("fromOrderItemSeqId");
							BigDecimal quantityTmp = oi.getBigDecimal("quantity");
							if (isWeight){
								quantityTmp = oi.getBigDecimal("amount");
							}
							if (quantityTmp.compareTo(BigDecimal.ZERO) > 0){
								BigDecimal qtyRemain = quantityTmp;
								List<Map<String, Object>> listRms = FastList.newInstance();
								for (Map<String, Object> inv : listInvPromos) {
									BigDecimal invQoh = (BigDecimal)inv.get("quantity");
									if (invQoh.compareTo(BigDecimal.ZERO) > 0){
										if (qtyRemain.compareTo(invQoh) > 0){
											qtyRemain = qtyRemain.subtract(invQoh);
											
											Map<String, Object> mapInv = FastMap.newInstance();
											mapInv.putAll(inv);
											mapInv.put("orderItemSeqId", orderItemSeqIdTmp);
											mapInv.put("quantity", invQoh);
											listInvs.add(mapInv);
											if (!listOrderItemSeqIds.contains(orderItemSeqIdTmp)) listOrderItemSeqIds.add(orderItemSeqIdTmp);
											listRms.add(inv);
										} else {
											Map<String, Object> mapInv = FastMap.newInstance();
											mapInv.putAll(inv);
											mapInv.put("orderItemSeqId", orderItemSeqIdTmp);
											mapInv.put("quantity", qtyRemain);
											listInvs.add(mapInv);
											if (!listOrderItemSeqIds.contains(orderItemSeqIdTmp)) listOrderItemSeqIds.add(orderItemSeqIdTmp);
											inv.put("quantity", invQoh.subtract(qtyRemain));
											qtyRemain = BigDecimal.ZERO;
										}
										if (qtyRemain.compareTo(BigDecimal.ZERO) <= 0) break;
									}
								}
								if (!listRms.isEmpty()){
									listInvPromos.removeAll(listRms);
								}
								if (listInvPromos.isEmpty()) break;
							}
						}
						if (!allItemPromoSeqs.isEmpty() && !listOrderItemSeqIds.isEmpty()) {
							allItemPromoSeqs.removeAll(listOrderItemSeqIds);
						}
						if (!allItemPromoSeqs.isEmpty()) {
							itemPromoSeqNotExports.addAll(allItemPromoSeqs);
						}
					}
				} else {
					List<String> allItemPromoSeqs = FastList.newInstance();
					if (!listItemPromos.isEmpty()){
						allItemPromoSeqs = EntityUtil.getFieldListFromEntityList(listItemPromos, "fromOrderItemSeqId", true);
					}
					itemPromoSeqNotExports.addAll(allItemPromoSeqs);
				}
				
				if (listInvs.isEmpty()) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				BigDecimal qohTotal = BigDecimal.ZERO;
				for (Map<String, Object> map : listInvs) {
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					qohTotal = qohTotal.add(qoh);
				}
				if (qohTotal.compareTo(quantity) < 0) return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale) + ": " + productId + " - QOH = 0 < " + quantity.toString());
				
				List<GenericValue> listReservers = FastList.newInstance();
				EntityCondition condOiSeq = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.IN, listOrderItemSeqIds);
				List<EntityCondition> cond2s = FastList.newInstance();
				cond2s.add(condOh);
				cond2s.add(condOiSeq);
				try {
					listReservers = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(cond2s), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList OrderItemShipGrpInvRes: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listReservers.isEmpty()){
					// update old reserves
					for (String orSeqId : listOrderItemSeqIds) {
						BigDecimal remainQuantity = BigDecimal.ZERO;
						for (Map<String, Object> inv : listInvs) {
							String orSeqIdTmp = (String)inv.get("orderItemSeqId");
							if (orSeqId.equals(orSeqIdTmp) && UtilValidate.isNotEmpty(inv.get("quantity"))){
								remainQuantity = remainQuantity.add((BigDecimal)inv.get("quantity"));
							}
						}
						List<GenericValue> listTmps = FastList.newInstance();
						for (GenericValue res : listReservers) {
							if (res.getString("orderItemSeqId").equals(orSeqId)) {
								listTmps.add(res);
							}
						}
						if (listTmps.isEmpty()) continue;
						for (GenericValue res : listTmps) {
							BigDecimal quantityRes = res.getBigDecimal("quantity");
							if (quantityRes.compareTo(remainQuantity) <= 0){
								
								// tao detail bu ATP
								GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
				                tmpInvDetail.set("inventoryItemId", res.getString("inventoryItemId"));
				                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
				                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
				                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("availableToPromiseDiff", res.getBigDecimal("quantity"));
				                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("orderId", orderId);
				                tmpInvDetail.set("orderItemSeqId", orderItemSeqId);
				                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
				                try {
									tmpInvDetail.create();
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
				                
				                try {
									delegator.removeValue(res);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when Create OrderItemShipGrpInvRes: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
				                
								remainQuantity = remainQuantity.subtract(quantityRes);
							} else {
								res.put("quantity", quantityRes.subtract(remainQuantity));
								BigDecimal quantityResNotAvai = res.getBigDecimal("quantityNotAvailable");
								if (UtilValidate.isNotEmpty(quantityResNotAvai)) {
									BigDecimal notAvai = quantityResNotAvai.subtract(remainQuantity);
									if (notAvai.compareTo(BigDecimal.ZERO) <= 0){
										notAvai = BigDecimal.ZERO;
									}
									res.put("quantityNotAvailable", notAvai);
								}
								
								GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
				                tmpInvDetail.set("inventoryItemId", res.getString("inventoryItemId"));
				                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
				                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
				                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("availableToPromiseDiff", remainQuantity);
				                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
				                tmpInvDetail.set("orderId", orderId);
				                tmpInvDetail.set("orderItemSeqId", orderItemSeqId);
				                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
				                try {
									tmpInvDetail.create();
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
				                
								try {
									delegator.store(res);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when Store OrderItemShipGrpInvRes: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
								remainQuantity = BigDecimal.ZERO;
							}
							if (remainQuantity.compareTo(BigDecimal.ZERO) < 0){
								break;
							}
						}
					}
				}
				
				BigDecimal createdQuantity = BigDecimal.ZERO;
				List<GenericValue> dlvInitItems = FastList.newInstance();
				if (!listOrderItemSeqIds.isEmpty()){
					EntityCondition condFromOi = EntityCondition.makeCondition("fromOrderItemSeqId", EntityOperator.IN, listOrderItemSeqIds);
					List<EntityCondition> cond5s = FastList.newInstance();
					cond5s.add(condDlv);
					cond5s.add(condFromOi);
					cond5s.add(condStt);
					try {
						dlvInitItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(cond5s), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!dlvInitItems.isEmpty()){
						for (GenericValue x : dlvInitItems) {
							if (isWeight){
								createdQuantity = x.getBigDecimal("amount");
							} else {
								createdQuantity = x.getBigDecimal("quantity");
							}
						}
					}
				}
				
				BigDecimal remainQuantity = createdQuantity;
				List<String> listDlvItemIds = FastList.newInstance();
				for (Map<String, Object> map : listInvs) {
					String inventoryItemId = (String)map.get("inventoryItemId");
					String orderItemSeqIdTmp = (String)map.get("orderItemSeqId");
					BigDecimal qoh = (BigDecimal)map.get("quantity");
					BigDecimal actualExportedQuantity = BigDecimal.ZERO;
					BigDecimal actualExportedAmount = BigDecimal.ZERO;
					actualExportedQuantity = qoh;
					if (isWeight){
						actualExportedAmount = actualExportedQuantity;
						actualExportedQuantity = BigDecimal.ONE;
					}
					// create reserves
					listReservers = FastList.newInstance();
					EntityCondition condinv = EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, inventoryItemId);
					EntityCondition condSeq = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqIdTmp);
					List<EntityCondition> cond3s = FastList.newInstance();
					cond3s.add(condOh);
					cond3s.add(condSeq);
					cond3s.add(condinv);
					try {
						listReservers = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(cond3s), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList OrderItemShipGrpInvRes: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (listReservers.isEmpty()){
						// create new
						GenericValue  tmpOISG = delegator.makeValue("OrderItemShipGrpInvRes");
		                tmpOISG.set("orderId", orderId);
		                tmpOISG.set("orderItemSeqId", orderItemSeqIdTmp);
		                tmpOISG.set("shipGroupSeqId", "00001"); // hard code
		                tmpOISG.set("inventoryItemId", inventoryItemId);
		                tmpOISG.set("reserveOrderEnumId", "INVRO_FIFO_REC");
		                tmpOISG.set("quantity", actualExportedQuantity);
		                tmpOISG.set("quantityNotAvailable", BigDecimal.ZERO);
		                tmpOISG.set("reservedDatetime", UtilDateTime.nowTimestamp());
		                tmpOISG.set("createdDatetime", UtilDateTime.nowTimestamp());
		                tmpOISG.set("promisedDatetime", UtilDateTime.nowTimestamp());
		                try {
							tmpOISG.create();
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Store OrderItemShipGrpInvRes: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
		                
		                GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		                tmpInvDetail.set("inventoryItemId", inventoryItemId);
		                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
		                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.negate());
		                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("orderId", orderId);
		                tmpInvDetail.set("orderItemSeqId", orderItemSeqId);
		                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
		                try {
							tmpInvDetail.create();
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
		                
					} else {
						// update
						GenericValue res = listReservers.get(0);
						BigDecimal resQuantity = res.getBigDecimal("quantity");
						res.put("quantity", resQuantity.add(actualExportedQuantity));
						try {
							delegator.store(res);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Store OrderItemShipGrpInvRes: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
		                tmpInvDetail.set("inventoryItemId", res.getString("inventoryItemId"));
		                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
		                tmpInvDetail.set("effectiveDate", UtilDateTime.nowTimestamp());
		                tmpInvDetail.set("quantityOnHandDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("availableToPromiseDiff", actualExportedQuantity.negate());
		                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
		                tmpInvDetail.set("orderId", orderId);
		                tmpInvDetail.set("orderItemSeqId", orderItemSeqId);
		                tmpInvDetail.set("shipGroupSeqId", "00001"); // hard code
		                try {
							tmpInvDetail.create();
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when Create InventoryItemDetail: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					}
					
					remainQuantity = remainQuantity.subtract(qoh);
					
					List<EntityCondition> condDlvIts = FastList.newInstance();
					condDlvIts.add(EntityCondition.makeCondition("fromOrderItemSeqId", EntityOperator.EQUALS, orderItemSeqIdTmp));
					condDlvIts.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_APPROVED"));
					if (!listDlvItemIds.isEmpty()){
						condDlvIts.add(EntityCondition.makeCondition("deliveryItemSeqId", EntityOperator.NOT_IN, listDlvItemIds));
					}
					condDlvIts.add(condDlv);
					List<GenericValue> items = FastList.newInstance();
					String dlvItemSeqId = null;
					try {
						items = delegator.findList("DeliveryItem", EntityCondition.makeCondition(condDlvIts), null, null, null, false);
						if (!items.isEmpty()){
							dlvItemSeqId = items.get(0).getString("deliveryItemSeqId"); 
						}
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					
					GenericValue objDeliveryItem = null;
					if (!listDlvItemIds.contains(dlvItemSeqId) && UtilValidate.isNotEmpty(dlvItemSeqId)){
						listDlvItemIds.add(dlvItemSeqId);
						try {
							objDeliveryItem = delegator.findOne("DeliveryItem", false, UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", dlvItemSeqId));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (UtilValidate.isNotEmpty(objDeliveryItem)) {
							objDeliveryItem.put("inventoryItemId", inventoryItemId);
							try {
								delegator.store(objDeliveryItem);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when store DeliveryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						}
					} else {
						condDlvIts = FastList.newInstance();
						condDlvIts.add(EntityCondition.makeCondition("fromOrderItemSeqId", EntityOperator.EQUALS, orderItemSeqIdTmp));
						condDlvIts.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "DELI_ITEM_CANCELLED"));
						condDlvIts.add(condDlv);
						items = FastList.newInstance();
						try {
							items = delegator.findList("DeliveryItem", EntityCondition.makeCondition(condDlvIts), null, null, null, false);
							if (!items.isEmpty()){
								objDeliveryItem = items.get(0); 
							}
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						// create new delivery
						if (UtilValidate.isNotEmpty(objDeliveryItem)) {
							GenericValue newDeliveryItem = delegator.makeValue("DeliveryItem"); 
	        				newDeliveryItem.put("deliveryId", deliveryId);
	        				delegator.setNextSubSeqId(newDeliveryItem, "deliveryItemSeqId", 5, 1);
	        				newDeliveryItem.put("fromOrderId", orderId);
	        				newDeliveryItem.put("fromOrderItemSeqId", orderItemSeqIdTmp);
	        				newDeliveryItem.set("inventoryItemId", inventoryItemId);
	        				newDeliveryItem.put("statusId", "DELI_ITEM_APPROVED");
	        				newDeliveryItem.put("actualExportedQuantity", BigDecimal.ZERO);
	        				newDeliveryItem.put("quantity", objDeliveryItem.getBigDecimal("quantity"));
	        				newDeliveryItem.put("amount", objDeliveryItem.getBigDecimal("amount"));
	        				try {
								delegator.createOrStore(newDeliveryItem);
								dlvItemSeqId = newDeliveryItem.getString("deliveryItemSeqId");
								if (!listDlvItemIds.contains(dlvItemSeqId)) {
									listDlvItemIds.add(dlvItemSeqId);
								}
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when create DeliveryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						}
					}
					
					Map<String, Object> updateItem = FastMap.newInstance();
    				updateItem.put("deliveryId", deliveryId);
    				updateItem.put("deliveryItemSeqId", dlvItemSeqId);
    				updateItem.put("actualExportedQuantity", actualExportedQuantity);
    				updateItem.put("delegator", delegator);
    				try {
						is.updateExportedQuantity(updateItem);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when updateExportedQuantity DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
    				
    				if (actualExportedAmount.compareTo(BigDecimal.ZERO) > 0){
	    				updateItem = FastMap.newInstance();
	    				updateItem.put("deliveryId", deliveryId);
	    				updateItem.put("deliveryItemSeqId", dlvItemSeqId);
	    				updateItem.put("actualExportedAmount", actualExportedAmount);
	    				updateItem.put("delegator", delegator);
	    				try {
							is.updateExportedAmount(updateItem);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when updateExportedAmount DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
    				}
    				
    				BigDecimal weight = BigDecimal.ZERO;
    				BigDecimal qty = qoh;
					if (isWeight){
						weight = qoh;
						qty = BigDecimal.ONE;
					}
					
					Map<String, Object> issueContext = FastMap.newInstance();
					issueContext.put("inventoryItemId", inventoryItemId);
					issueContext.put("locale", locale);
					issueContext.put("orderId", orderId);
					issueContext.put("orderItemSeqId", orderItemSeqIdTmp);
					issueContext.put("shipGroupSeqId", "00001"); // hard code
					issueContext.put("shipmentId", shipmentId);
					issueContext.put("quantity", qty);
					issueContext.put("weight", weight);
					issueContext.put("userLogin", userLogin);
					try {
						Map<String, Object> resultTmp = dispatcher.runSync("issueOrderItemShipGrpInvResToShipment", issueContext);
						if (ServiceUtil.isError(resultTmp)){
							String errMsg = "OLBIUS: Fatal error when runSync issueOrderItemShipGrpInvResToShipment: " + ServiceUtil.getErrorMessage(resultTmp);
							Debug.logError(errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					} catch (GenericServiceException e) {
						String errMsg = "OLBIUS: Fatal error when runSync issueOrderItemShipGrpInvResToShipment: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}
				if (!itemPromoSeqNotExports.isEmpty()){
					for (String orderItemSeqIdTmp : itemPromoSeqNotExports) {
						List<GenericValue> listDlvItemTmps = FastList.newInstance();
						try {
							listDlvItemTmps = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId, "fromOrderItemSeqId", orderItemSeqIdTmp, "statusId", "DELI_ITEM_APPROVED")), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listDlvItemTmps.isEmpty()){
							for (GenericValue it : listDlvItemTmps) {
								Map<String, Object> updateItem = FastMap.newInstance();
								updateItem.put("deliveryId", deliveryId);
								updateItem.put("deliveryItemSeqId", it.getString("deliveryItemSeqId"));
								updateItem.put("actualExportedQuantity", BigDecimal.ZERO);
								updateItem.put("delegator", delegator);
								try {
									is.updateExportedQuantity(updateItem);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when updateExportedQuantity DeliveryItem: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
								if (isWeight){
									updateItem = FastMap.newInstance();
				    				updateItem.put("deliveryId", deliveryId);
				    				updateItem.put("deliveryItemSeqId", it.getString("deliveryItemSeqId"));
				    				updateItem.put("actualExportedAmount", BigDecimal.ZERO);
				    				updateItem.put("delegator", delegator);
				    				try {
										is.updateExportedAmount(updateItem);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when updateExportedAmount DeliveryItem: " + e.toString();
										Debug.logError(e, errMsg, module);
										return ServiceUtil.returnError(errMsg);
									}
								}
							}
						}
					}
				}
			} else {
				Map<String, Object> updateItem = FastMap.newInstance();
				updateItem.put("deliveryId", deliveryId);
				updateItem.put("deliveryItemSeqId", deliveryItemSeqId);
				updateItem.put("actualExportedQuantity", BigDecimal.ZERO);
				updateItem.put("delegator", delegator);
				try {
					is.updateExportedQuantity(updateItem);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when updateExportedQuantity DeliveryItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (isWeight){
					updateItem = FastMap.newInstance();
    				updateItem.put("deliveryId", deliveryId);
    				updateItem.put("deliveryItemSeqId", deliveryItemSeqId);
    				updateItem.put("actualExportedAmount", BigDecimal.ZERO);
    				updateItem.put("delegator", delegator);
    				try {
						is.updateExportedAmount(updateItem);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when updateExportedAmount DeliveryItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("deliveryId", deliveryId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateDeliveredSalesDelivery(DispatchContext ctx, Map<String, Object> context){
    	Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String deliveryId = (String)context.get("deliveryId");
		String listItems = (String)context.get("listProducts");
		
		GenericValue delivery = null;
		try {
			delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when findOne Delivery: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String statusId = delivery.getString("statusId");
		if (!"DLV_EXPORTED".equals(statusId)) {
			String errMsg = UtilProperties.getMessage(resourceError, "CannotUpdateDeliveryWithStatus", locale) + " " + statusId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		delivery.set("actualArrivalDate", UtilDateTime.nowTimestamp());
		try {
			delegator.store(delivery);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when Store Delivery: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			listProducts = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItems);
		} catch (ParseException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: exportProductFromSalesDelivery - JqxWidgetSevices.convert error!");
		}
		EntityCondition condDlv = EntityCondition.makeCondition("deliveryId", EntityOperator.EQUALS, deliveryId);
		EntityCondition condStt = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "DELI_ITEM_EXPORTED");
		List<String> listItemIds = FastList.newInstance();
		Observer o = new DeliveryObserver();
		ItemSubject is = new DeliveryItemSubject();
		is.attach(o);
		for (Map<String, Object> item : listProducts){
			String orderItemSeqId = (String)item.get("orderItemSeqId");
			String productId = (String)item.get("productId");
			Boolean isWeight = ProductUtil.isWeightProduct(delegator, productId);
			
			EntityCondition cond2 = EntityCondition.makeCondition("fromOrderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(condDlv);
			conds.add(condStt);
			conds.add(cond2);
			List<GenericValue> listDlvItems = FastList.newInstance();
			try {
				listDlvItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			BigDecimal quantity = BigDecimal.ZERO;
			
			if (item.containsKey("quantity")){
				String quantityStr = (String)item.get("quantity");
				quantity = new BigDecimal(quantityStr);
			}
			BigDecimal promoQuantity = BigDecimal.ZERO;
			
			if (item.containsKey("promoQuantity")){
				String quantityStr = (String)item.get("promoQuantity");
				promoQuantity = new BigDecimal(quantityStr);
			}
			if (quantity.compareTo(BigDecimal.ZERO) > 0){
				BigDecimal remainQuantity = quantity;
				Map<String, Object> parameters = FastMap.newInstance();
				parameters.put("deliveryId", deliveryId);
				parameters.put("delegator", delegator);
				for (GenericValue dlvItem : listDlvItems) {
					String deliveryItemSeqId = dlvItem.getString("deliveryItemSeqId");
					if (!listItemIds.contains(deliveryItemSeqId)) listItemIds.add(deliveryItemSeqId);
					BigDecimal actualExportedQuantity = dlvItem.getBigDecimal("actualExportedQuantity");
					if (isWeight){
						actualExportedQuantity = dlvItem.getBigDecimal("actualExportedAmount");
					}
					BigDecimal actualDeliveredQuantity = BigDecimal.ZERO;
					BigDecimal actualDeliveredAmount = BigDecimal.ZERO;
					if (remainQuantity.compareTo(BigDecimal.ZERO) <= 0) {
						actualDeliveredQuantity = BigDecimal.ZERO;
					} else if (remainQuantity.compareTo(actualExportedQuantity) <= 0){
						actualDeliveredQuantity = remainQuantity;
						remainQuantity = BigDecimal.ZERO;
					} else if (remainQuantity.compareTo(actualExportedQuantity) > 0){
						remainQuantity = remainQuantity.subtract(actualExportedQuantity);
						actualDeliveredQuantity = actualExportedQuantity;
					}
					
					if (isWeight){
						actualDeliveredAmount = actualDeliveredQuantity;
						actualDeliveredQuantity = BigDecimal.ONE;
					}
					
					parameters.put("deliveryItemSeqId", deliveryItemSeqId);
					parameters.put("actualDeliveredQuantity", actualDeliveredQuantity);
					parameters.put("actualDeliveredAmount", actualDeliveredAmount);
					try {
						is.updateDeliveredQuantity(parameters);
					} catch (GenericEntityException e) {
						Debug.log(e.getStackTrace().toString(), module);
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
					}
					if (isWeight){
						try {
							is.updateDeliveredAmount(parameters);
						} catch (GenericEntityException e) {
							Debug.log(e.getStackTrace().toString(), module);
							return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
						}
					}
				}
			}
			if (promoQuantity.compareTo(BigDecimal.ZERO) > 0){
				conds = FastList.newInstance();
				conds.add(condDlv);
				conds.add(condStt);
				conds.add(EntityCondition.makeCondition("productId", productId));
				conds.add(EntityCondition.makeCondition("isPromo", "Y"));
				conds.add(EntityCondition.makeCondition("actualExportedQuantity",EntityOperator.GREATER_THAN, BigDecimal.ZERO));
				listDlvItems = FastList.newInstance();
				try {
					listDlvItems = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listDlvItems.isEmpty()){
					BigDecimal remainQty = promoQuantity;
					List<Map<String, Object>> listUpdates = FastList.newInstance();
					for (GenericValue obj : listDlvItems) {
						if (remainQty.compareTo(BigDecimal.ZERO) <= 0) break;
						BigDecimal actQty = obj.getBigDecimal("actualExportedQuantity");
						String deliveryItemSeqId = obj.getString("deliveryItemSeqId");
						if (UtilValidate.isNotEmpty(actQty)) {
							Map<String, Object> map = FastMap.newInstance();
							if (remainQty.compareTo(actQty) >= 0){
								remainQty = remainQty.subtract(actQty);
								map.put("deliveryItemSeqId", deliveryItemSeqId);
								map.put("actualDeliveredQuantity", actQty);
							} else {
								map.put("deliveryItemSeqId", deliveryItemSeqId);
								map.put("actualDeliveredQuantity", remainQty);
								remainQty = BigDecimal.ZERO;
							}
							listUpdates.add(map);
						}
					}
					if (!listUpdates.isEmpty()){
						Map<String, Object> parameters = FastMap.newInstance();
						parameters.put("deliveryId", deliveryId);
						parameters.put("delegator", delegator);
						
						for (Map<String, Object> map : listUpdates) {
							BigDecimal actualDeliveredQuantity = (BigDecimal)map.get("actualDeliveredQuantity");
							String deliveryItemSeqId = (String)map.get("deliveryItemSeqId");
							BigDecimal actualDeliveredAmount = BigDecimal.ZERO;
							if (isWeight){
								actualDeliveredAmount = actualDeliveredQuantity;
								actualDeliveredQuantity = BigDecimal.ONE;
							}
							
							parameters.put("deliveryItemSeqId", deliveryItemSeqId);
							parameters.put("actualDeliveredQuantity", actualDeliveredQuantity);
							parameters.put("actualDeliveredAmount", actualDeliveredAmount);
							try {
								is.updateDeliveredQuantity(parameters);
							} catch (GenericEntityException e) {
								Debug.log(e.getStackTrace().toString(), module);
								return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
							}
							if (isWeight){
								try {
									is.updateDeliveredAmount(parameters);
								} catch (GenericEntityException e) {
									Debug.log(e.getStackTrace().toString(), module);
									return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
								}
							}
						}
					}
				}
			}
		}
		List<GenericValue> listAllItems = FastList.newInstance();
		try {
			EntityCondition condIds = EntityCondition.makeCondition("deliveryItemSeqId", EntityOperator.NOT_IN, listItemIds);
			List<EntityCondition> cond2s = FastList.newInstance();
			cond2s.add(condIds);
			cond2s.add(condDlv);
			cond2s.add(condStt);
			listAllItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(cond2s), null, null, null, false);
			if (!listAllItems.isEmpty()){
				Map<String, Object> parameters = FastMap.newInstance();
				parameters.put("deliveryId", deliveryId);
				parameters.put("delegator", delegator);
				
				for (GenericValue item : listAllItems) {
					parameters.put("deliveryItemSeqId", item.getString("deliveryItemSeqId"));
					parameters.put("actualDeliveredQuantity", BigDecimal.ZERO);
					parameters.put("actualDeliveredAmount", BigDecimal.ZERO);
					try {
						is.updateDeliveredQuantity(parameters);
					} catch (GenericEntityException e) {
						Debug.log(e.getStackTrace().toString(), module);
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
					}
					try {
						is.updateDeliveredAmount(parameters);
					} catch (GenericEntityException e) {
						Debug.log(e.getStackTrace().toString(), module);
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryItemError", locale));
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList DeliveryItem: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("deliveryId", deliveryId);
		return result;
	}
	
	
	public static Map<String, Object> updateInvoiceForPurchaseDelivery(DispatchContext ctx, Map<String, Object> context){
    	Delegator delegator = ctx.getDelegator();
		String shipmentId = (String)context.get("shipmentId");
		if (UtilValidate.isNotEmpty(shipmentId) && context.containsKey("invoicesCreated")) {
			GenericValue objShipment = null;
			try {
				objShipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Shipment: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (("PURCH_SHIP_RECEIVED".equals(objShipment.getString("statusId")) && "PURCHASE_SHIPMENT".equals(objShipment.getString("shipmentTypeId")))
					|| (("SHIPMENT_SHIPPED".equals(objShipment.getString("statusId")) || "SHIPMENT_PACKED".equals(objShipment.getString("statusId"))) && "SALES_SHIPMENT".equals(objShipment.getString("shipmentTypeId")))){
				@SuppressWarnings("unchecked")
				List<String> invoices = (List<String>)context.get("invoicesCreated");
				if (!invoices.isEmpty()){
					String invoiceId = invoices.get(0);
					if (UtilValidate.isNotEmpty(invoiceId)) {
						List<GenericValue> listDelivery = FastList.newInstance();
						try {
							listDelivery = delegator.findList("Delivery", EntityCondition.makeCondition("shipmentId", shipmentId), null,
									null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList Delivery: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listDelivery.isEmpty()){
							GenericValue delivery = EntityUtil.getFirst(listDelivery);
							delivery.set("invoiceId", invoiceId);
							try {
								delegator.store(delivery);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when updateInvoiceForPurchaseDelivery: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						}
					}
				}
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}

    @SuppressWarnings("unchecked")
	public static Map<String, Object> updateConversionFactorForDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String deliveryId = (String)context.get("deliveryId");
        String conversionFactorStr = (String) context.get("conversionFactor");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
            GenericValue delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
            if (UtilValidate.isNotEmpty(conversionFactorStr)) {
                delivery.put("conversionFactor", new BigDecimal(conversionFactorStr));
            }
            delegator.store(delivery);
            result.put("deliveryId", deliveryId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

	/**
	 * Shipping MT
	 */


	public static Map<String, Object> JQGetListShippingTrip(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("createdDate DESC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listItems = null;
		String[] shipperId = parameters.get("shipperId");
		if (shipperId != null) {
			listAllConditions.add(EntityCondition.makeCondition("shipperId", shipperId[0]));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "TRIP_EXPORTED"));
			listAllConditions.add(EntityCondition.makeCondition("invoiceId", null));
		}
		try {
			if (shipperId != null) {
				listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ShippingTripUnpaidInvoice", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			} else {
				listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ShippingTripView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListShippingTrip service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}

		successResult.put("listIterator", listItems);

		return successResult;
	}

	public static Map<String, Object> JQGetListOrderItemDetails(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listItems = new ArrayList<GenericValue>();
		String[] shippingTripId = parameters.get("shippingTripId");
		if (shippingTripId != null) {
			listAllConditions.add(EntityCondition.makeCondition("shippingTripId", shippingTripId[0]));
		}
		try {
			listSortFields.add("productId ASC");
			listItems = delegator.findList("ShippingTripAndPackAndOrder", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListOrderItemDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}

		successResult.put("listIterator", listItems);
		return successResult;
	}

	public static Map<String, Object> createOptimalRouteShippingTrip(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String shippingTripId = (String) context.get("shippingTripId");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listPack = null;
		ArrayList<Map<String, Object>> listPointDetail = new ArrayList<>();
        List<Map<String, Object>> listPoint = FastList.newInstance();
		EntityCondition filterByTripId = EntityCondition.makeCondition("shippingTripId", EntityOperator.LIKE, shippingTripId);
		listAllConditions.add(filterByTripId);
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, DeliveryServices.ShippingTripPackStatusEnum.SHIP_PACK_CANCELLED.name()));
		try {
			listPack = delegator.findList("ShippingTripPackOrderDeliveryPackSummaryView", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			GenericValue shippingTrip = delegator.findOne("ShippingTrip", false, UtilMisc.toMap("shippingTripId", shippingTripId));
			String facilityId = shippingTrip.getString("facilityId");
			GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			List<GenericValue> facilityContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "PRIMARY_LOCATION");
			String facilityGeoPointId = null;
			if (facilityContactMechs.isEmpty()){
				EntityCondition shipOrgLocation = EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION");
				EntityCondition shippingLocation = EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION");
				List<EntityCondition> listFacCond = UtilMisc.toList(shipOrgLocation, shippingLocation);
				EntityCondition facCond = EntityCondition.makeCondition(listFacCond, EntityOperator.OR);
				facilityContactMechs = delegator.findList("FacilityContactMechPurpose", facCond, null, null, null, false);
			}
			if (!facilityContactMechs.isEmpty()){
				GenericValue contactMech = facilityContactMechs.get(0);
				GenericValue postalAddress = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId")));
				facilityGeoPointId = postalAddress.getString("geoPointId");
			}
//			String facilityGeoPointId = facility.getString("geoPointId");
            GenericValue facilityGeoPoint = delegator.findOne("GeoPoint", false, UtilMisc.toMap("geoPointId", facilityGeoPointId));
            Map< String, Object> startPoint = FastMap.newInstance();
			String latFaciTemp = null ;
			String longFaciTemp = null;
            if( facilityGeoPoint != null){
				 latFaciTemp = facilityGeoPoint.getString("latitude");
				 longFaciTemp = facilityGeoPoint.getString("longitude");
			}

			if( (latFaciTemp == null) || (longFaciTemp == null)){
				return ServiceUtil.returnError(  facilityId + " ( " + facility.getString("facilityName") + " )");
			}
            startPoint.put("latitude", latFaciTemp);
            startPoint.put("longitude", longFaciTemp);
            startPoint.put("id", 0);
            listPointDetail.add(0, startPoint );
			int sequenceId = 1;
            listPoint.add(startPoint);
			for ( GenericValue pack: listPack
				 ) {
			    Map< String, Object> tempPoint = FastMap.newInstance();
			    tempPoint.put("customerId", pack.getString("customerId"));
			    tempPoint.put("packId", pack.getString("packId"));
				String latTemp = pack.getString("latitude");
				String longTemp = pack.getString("longitude");
			    if( (latTemp == null) || (longTemp == null)){
					return ServiceUtil.returnError(  pack.getString("customerId") + " ( "+ pack.getString("partyName")+")");
				}
				tempPoint.put("latitude", latTemp );
				tempPoint.put("longitude", longTemp);
				listPointDetail.add(sequenceId, tempPoint);
				sequenceId++;
                listPoint.add(tempPoint);
			}
            Map< String, Object> initDataToRoute = new HashMap<>();
            Map<String, Object> initMap = FastMap.newInstance();
            initMap.put("customers", listPoint);
            initMap.put("start", startPoint);

			initMap.put("timeLimit", 15);
            try {
                initDataToRoute = ServiceUtil.setServiceFields(dispatcher,"computeOptimalSequencePoints",initMap,userLogin,null,null);
                Map<String, Object> optimalRoute = dispatcher.runSync("computeOptimalSequencePoints", initDataToRoute);
				GenericValue routeTrip = delegator.makeValue("ShippingTripRoute");
				String routeTripId = delegator.getNextSeqId("ShippingTripRoute");
				routeTrip.put("routeTripId", routeTripId);
				routeTrip.put("shippingTripId", shippingTripId);
				routeTrip.put("shipperId", shippingTrip.getString("shipperId"));
				routeTrip.put("startPointLatitude", facilityGeoPoint.getString("latitude"));
				routeTrip.put("startPointLongitude", facilityGeoPoint.getString("longitude"));
				routeTrip.create();
				shippingTrip.put("isHasOptimalRoute",routeTripId);
				shippingTrip.store();
                ArrayList<Integer> routeTripCustomers = (ArrayList<Integer>) optimalRoute.get("route");
                int sequenceNumCustomer = 1;
				for (int point: routeTripCustomers
					 ) {
					if ( point != 0){
						GenericValue routeTripCustomer = delegator.makeValue("ShippingTripRouteCustomer");
						Map<String, Object> customer = listPointDetail.get(point);
						routeTripCustomer.put("routeTripId", routeTripId);
						routeTripCustomer.put("customerId",  customer.get("customerId"));
						routeTripCustomer.put("packId",  customer.get("packId"));
						routeTripCustomer.put("sequenceNum", Integer.toString(sequenceNumCustomer));
						routeTripCustomer.put("latitude", customer.get("latitude"));
						routeTripCustomer.put("longitude", customer.get("longitude"));
						routeTripCustomer.create();
						sequenceNumCustomer++;
					}
				}
				successResult.put("message", "OK");
				successResult.put("routeTripId", routeTripId);

            } catch (GeneralServiceException e) {
                e.printStackTrace();
				return ServiceUtil.returnError(e.getStackTrace().toString());
            } catch (GenericServiceException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getStackTrace().toString());
			}

		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListPack service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		return successResult;
	}
	
	public static Map<String, Object> getOptimalRouteShippingTrip(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String routeTripId = (String) context.get("routeTripId");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> optimalRouteShippingTrip = new ArrayList<Map<String, Object>>();
		try {
			GenericValue routeTrip = delegator.findOne("ShippingTripRoute", false, UtilMisc.toMap("routeTripId",routeTripId));
			String shippingTripId = routeTrip.getString("shippingTripId");
			GenericValue shippingTrip = delegator.findOne("ShippingTrip", false, UtilMisc.toMap("shippingTripId", shippingTripId));
			String facilityId = shippingTrip.getString("facilityId");
			List<GenericValue> facilityTemp = delegator.findList("FacilityAndPostalAddressAndTelecomNumber",  EntityCondition.makeCondition("facilityId", EntityOperator.LIKE, facilityId),null, null, null, false);
		    GenericValue facility = EntityUtil.getFirst(facilityTemp);
			String facilityGeoPointId = null;
			List<GenericValue> facilityContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "PRIMARY_LOCATION");
			if (facilityContactMechs.isEmpty()){
				EntityCondition shipOrgLocation = EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION");
				EntityCondition shippingLocation = EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION");
				List<EntityCondition> listFacCond = UtilMisc.toList(shipOrgLocation, shippingLocation);
				EntityCondition facCond = EntityCondition.makeCondition(listFacCond, EntityOperator.OR);
				facilityContactMechs = delegator.findList("FacilityContactMechPurpose", facCond, null, null, null, false);
			}
			if (!facilityContactMechs.isEmpty()){
				GenericValue contactMech = facilityContactMechs.get(0);
				GenericValue postalAddress = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId")));
				facilityGeoPointId = postalAddress.getString("geoPointId");
			}
			GenericValue facilityGeoPoint = delegator.findOne("GeoPoint", false, UtilMisc.toMap("geoPointId", facilityGeoPointId));
			Map< String, Object> startPoint = FastMap.newInstance();
			startPoint.put("facilityId", facility.getString("facilityId"));
			startPoint.put("facilityName", facility.getString("facilityName"));
			startPoint.put("latitude", facilityGeoPoint.getString("latitude"));
			startPoint.put("longitude", facilityGeoPoint.getString("longitude"));
			startPoint.put("address", facility.getString("address1"));
			optimalRouteShippingTrip.add(startPoint);
			List<GenericValue> listCustomer = delegator.findList("ShippingTripRouteCustomer",EntityCondition.makeCondition("routeTripId", EntityOperator.LIKE, routeTripId), null, UtilMisc.toList("sequenceNum ASC"), null, false);
			for ( GenericValue customer : listCustomer
				 ) {
//				List orderBy = UtilMisc.toList("sequenceNum");
				Map< String, Object> tempPoint = FastMap.newInstance();
//				GenericValue point = delegator.findList("ShippingTripPackOrderDeliveryPackSummaryView",  UtilMisc.toMap("shippingTripId", shippingTripId, "packId", customer.getString("packId") ));
				List<EntityCondition> listCondition = new ArrayList<EntityCondition>();
				EntityCondition tripFilter = EntityCondition.makeCondition("shippingTripId", EntityOperator.LIKE, shippingTripId);
				EntityCondition packFilter = EntityCondition.makeCondition("packId", EntityOperator.LIKE, customer.getString("packId"));
				EntityCondition customerFilter = EntityCondition.makeCondition("customerId", EntityOperator.LIKE, customer.getString("customerId"));
				listCondition.add(tripFilter);
				listCondition.add(packFilter);
				listCondition.add(customerFilter);
				List<GenericValue> pointTemp = delegator.findList("ShippingTripPackOrderDeliveryPackSummaryView",EntityCondition.makeCondition(listCondition), null, null, null, false );
				GenericValue point = EntityUtil.getFirst(pointTemp);
				tempPoint.put("customerId", customer.getString("customerId"));
				tempPoint.put("packId", customer.getString("packId"));
				tempPoint.put("sequenceNum", customer.getString("sequenceNum"));
				tempPoint.put("latitude", customer.getString("latitude"));
				tempPoint.put("longitude", customer.getString("longitude"));
				tempPoint.put("customerName", point.getString("partyName"));
				String contactMechId = point.getString("contactMechId");
				if ( contactMechId != null){
					try {
						GenericValue contactMech = delegator.findOne("TelecomNumber", false, UtilMisc.toMap("contactMechId", contactMechId));
						if (contactMech != null){
							tempPoint.put("phoneNumber", contactMech.getString("contactNumber"));
						}else {
							tempPoint.put("phoneNumber", null);
						}
					}catch (GenericEntityException e){
						String errMsg = "Cannot get ContactMech for Customer: " + e.toString();
						Debug.logError(e, errMsg, module);
					}
				}else{
					tempPoint.put("phoneNumber", null);
				}
				tempPoint.put("address", point.getString("postalAddressName"));
				optimalRouteShippingTrip.add(tempPoint);
			}
			successResult.put("optimalRouteShippingTrip", optimalRouteShippingTrip);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getOptimalRouteShippingTrip service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}

	public static Map<String, Object> updateFromDateToPrimary(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> list = null;
		try {
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			listSortFields.add("partyId ASC");
			list = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			sdf.format(date);
			long m = date.getTime();
			for (int i = 0; i < list.size(); i++) {
				m += 1000;
				Timestamp fromDate = new Timestamp(m);
				GenericValue gvCurrent = list.get(i);
				GenericValue gv = delegator.findOne("ProductStoreRole", UtilMisc.toMap("partyId", gvCurrent.get("partyId"), "roleTypeId", "CUSTOMER", "productStoreId", gvCurrent.get("productStoreId")), false);
				gv.put("fromDate", fromDate);
				delegator.store(gv);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListShippingTrip service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}

	public static Map<String, Object> updateProductStoreId(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String[] productStoreIds = {"CH00001", "CH00002", "CH00003", "CH00004"};
		String productStoreId;
		int index = 0;
		int loop;
		List<GenericValue> list = null;
		try {
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			listSortFields.add("partyId ASC");
			list = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			loop = index;
			int len = list.size();
			for (int i = 1; i < list.size(); i++) {
				index = (i < len / 4) ? 0 : ((i < len / 2) ? 1 : ((i < len * 3 / 4) ? 2 : 3));
				GenericValue gvCurrent = list.get(i);
				GenericValue gvPrev = list.get(i - 1);
				if ((gvCurrent.get("partyId")).equals(gvPrev.get("partyId"))) {
					loop++;//productStoreRole[loop]
					System.out.println(loop - 1);
					System.out.println(loop);
				} else {
					loop = index;//productStoreRole[index]
				}
				GenericValue gv = delegator.findOne("ProductStoreRole", UtilMisc.toMap("partyId", gvCurrent.get("partyId"), "roleTypeId", "CUSTOMER", "fromDate", gvCurrent.get("fromDate")), false);
				gv.put("productStoreId", productStoreIds[loop]);
				delegator.store(gv);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListShippingTrip service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}

	public static Map<String, Object> exportProductInShippingTrip(DispatchContext ctx, Map<String, ? extends Object> context) {
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String shippingTripId = (String) context.get("shippingTripId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		List<GenericValue> tripPacks = new ArrayList<GenericValue>();
		try {
			tripPacks = delegator.findList("ShippingTripPack", EntityCondition.makeCondition(UtilMisc.toMap("shippingTripId", shippingTripId, "statusId", "SHIP_PACK_CREATED")), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		for (GenericValue pack : tripPacks) {
			String packId = (String) pack.get("packId");
			List<GenericValue> packItems = new ArrayList<GenericValue>();
			try {
				packItems = delegator.findList("OrderDeliveryPackItemSummaryView", EntityCondition.makeCondition("packId", packId), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			List<Map<String, Object>> listMap = new ArrayList<>();
			String deliveryId = null;
			for (GenericValue packItem : packItems)
				if("N".equals(packItem.getString("isPromo"))){
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("productId", packItem.get("productId"));
					tmp.put("quantity", packItem.get("orderQuantity"));
					tmp.put("deliveryId", packItem.get("deliveryId"));
					deliveryId = (String) packItem.get("deliveryId");
					tmp.put("orderItemSeqId", packItem.get("orderItemSeqId"));
					tmp.put("orderId", packItem.get("orderId"));
					listMap.add(tmp);
				}

			for (GenericValue packItem : packItems)
				if("Y".equals(packItem.getString("isPromo"))){
					for(Map<String,Object> map:listMap)
						if(packItem.get("productId").equals(map.get("productId"))) {
							map.put("promoQuantity",packItem.get("orderQuantity"));
							break;
						}
				}
			String listProduct = JsonUtil.convertListMapToJSON(listMap);
			Map<String, Object> exportMapProduct = new HashMap<>();
			exportMapProduct.put("listProducts", listProduct);
			exportMapProduct.put("deliveryId", deliveryId);
			exportMapProduct.put("listProductAttributes", JsonUtil.convertListMapToJSON(new ArrayList<Map<String, Object>>()));

			exportMapProduct.put("userLogin", userLogin);
			try {
				GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
				String statusId= (String) delivery.get("statusId");
				if(DeliveryStatusEnum.DLV_CANCELLED.name().equals(statusId)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateDeliveryError", locale));
				}
				if(DeliveryStatusEnum.DLV_APPROVED.name().equals(statusId)) {
					try {
						dispatcher.runSync("exportProductFromSalesDelivery", exportMapProduct);
					} catch (GenericServiceException e) {
						String errMsg = "OLBIUS: Fatal error when run Service exportProductFromSalesDelivery: " + e.toString();
						Debug.logError(e, errMsg, module);
						e.printStackTrace();
						return ServiceUtil.returnError(errMsg);
					}
				}
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when run Service exportProductFromSalesDelivery: " + e.toString();
				Debug.logError(e, errMsg, module);
				e.printStackTrace();
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "DeliveryCannotBeFound", locale));
			}

			try {
				GenericValue gv = delegator.findOne("ShippingTrip", false, UtilMisc.toMap("shippingTripId", shippingTripId));
				gv.set("statusId", TripStatusEnum.TRIP_EXPORTED.name());
				gv.store();
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when store ShippingTrip: " + e.toString();
				Debug.logError(e, errMsg, module);
				e.printStackTrace();
				return ServiceUtil.returnError(errMsg);
			}
		}
		return successResult;
	}


	public static Map<String, Object> quickApproveAndExportShippingTrip(DispatchContext ctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String shippingTripId = (String) context.get("shippingTripId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		GenericValue shippingTrip = null;
		try {
			shippingTrip = delegator.findOne("ShippingTrip", UtilMisc.toMap("shippingTripId", shippingTripId), false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		if (TripStatusEnum.TRIP_CREATED.name().equals((String) shippingTrip.get("statusId"))) {

			Map<String, Object> changeStatus = new HashMap<>();
			changeStatus.put("shippingTripId", shippingTripId);
			changeStatus.put("statusId", shippingTrip.get("statusId"));
			changeStatus.put("userLogin", userLogin);
			try {
				dispatcher.runSync("changeShippingTripStatus", changeStatus);
				DeliveryHelper.updatePackStatus(delegator, shippingTripId, null, "PACK_BEING_DLIED");
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run Service changeShippingTripStatus: " + e.toString();
				Debug.logError(e, errMsg, module);
				e.printStackTrace();
				return ServiceUtil.returnError(errMsg);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			Map<String, Object> exportShippingTrip = new HashMap<>();
			exportShippingTrip.put("shippingTripId", shippingTripId);
			exportShippingTrip.put("userLogin", userLogin);
			try {
				Map<String,Object> suc=dispatcher.runSync("exportProductInShippingTrip", exportShippingTrip);
				if(ServiceUtil.isError(suc)){
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(suc));
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run Service exportProductInShippingTrip: " + e.toString();
				Debug.logError(e, errMsg, module);
				e.printStackTrace();
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		return successResult;
	}

	public static Map<String, Object> updateShippingTripByOrder(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> returnResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String shippingTripId = (String) context.get("shippingTripId");
		Long startDateTime = Long.parseLong((String) context.get("startDateTime"));
		Long finishedDateTime = Long.parseLong((String) context.get("finishedDateTime"));
		String description = (String) context.get("description");
		String deliverierId = (String) context.get("deliverierId");
		BigDecimal tripCost = new BigDecimal((String) context.get("tripCost"));
		BigDecimal costCustomerPaid = new BigDecimal((String) context.get("costCustomerPaid"));
		JSONArray listOrders = JSONArray.fromObject(context.get("listOrders"));

		try {
			Map<String, Object> shippingTripInfoMap = context;
			shippingTripInfoMap.remove("listOrders");
			shippingTripInfoMap.put("userLogin", userLogin);
			dispatcher.runSync("updateShippingTripInfo", shippingTripInfoMap);
		} catch (GenericServiceException e) {
			String errMsg = "OLBIUS: Fatal error when run Service updateShippingTripInfo: " + e.toString();
			Debug.logError(e, errMsg, module);
			e.printStackTrace();
			return ServiceUtil.returnError(errMsg);
		}

		List<Map<String, Object>> orders = FastList.newInstance();
		for (int i = 0; i < listOrders.size(); i++) {
			JSONObject objectJSON = JSONObject.fromObject(listOrders.get(i));
			Map<String, Object> order = new HashMap<String, Object>();
			order.put("userLogin", userLogin);
			order.put("orderId", objectJSON.getString("orderId"));
			order.put("statusId", objectJSON.getString("statusId"));
			order.put("packId", objectJSON.getString("packId"));
			order.put("shippingTripId", shippingTripId);
			orders.add(order);
		}
		try {
			List<GenericValue> listShippingTripPackItems = delegator.findList("ShippingTripPackOrderDeliveryPackSummaryView", EntityCondition.makeCondition("shippingTripId", shippingTripId), null, null, null, false);
			HashMap<String, GenericValue> mapOrdersOld = new HashMap<>();
			for (int i = 0; i < listShippingTripPackItems.size(); i++) {
				mapOrdersOld.put((String) listShippingTripPackItems.get(i).get("orderId"), listShippingTripPackItems.get(i));
			}

			HashSet<String> setPackItemsNew = new HashSet<>();
			for (int i = 0; i < orders.size(); i++) {
				setPackItemsNew.add((String) orders.get(i).get("orderId"));
			}
			for (Map<String, Object> order : orders) {
				if (!mapOrdersOld.containsKey((String) order.get("orderId"))) {
					//order.put("statusId",PackServices.PACK_ITEM_STATUS.PACK_ITEM_CREATED.name());
					Map<String, Object> dataSer = ServiceUtil.setServiceFields(dispatcher, "addShippingTripPack", order, userLogin, null, null);
					dispatcher.runSync("addShippingTripPack", dataSer);
				} else {
					if ("CANCELLED".equals((String) order.get("statusId"))) {
						Map<String, Object> map = new HashMap<>();
						map.put("packId", mapOrdersOld.get((String) order.get("orderId")).get("packId"));
						map.put("statusId", PackStatusEnum.PACK_CANCELLED.name());
						map.put("userLogin", userLogin);
						dispatcher.runSync("changePackStatus", map);
						map = new HashMap<>();
						map.put("deliveryId", mapOrdersOld.get((String) order.get("orderId")).get("deliveryId"));
						map.put("statusId", DeliveryStatusEnum.DLV_CANCELLED.name());
						map.put("userLogin", userLogin);
						dispatcher.runSync("changeDeliveryStatus", map);
						GenericValue tripPack = delegator.findOne("ShippingTripPack", false, UtilMisc.toMap("packId", mapOrdersOld.get((String) order.get("orderId")).get("packId"), "shippingTripId", shippingTripId));
						tripPack.set("statusId", ShippingTripPackStatusEnum.SHIP_PACK_CANCELLED.name());
						tripPack.store();
						GenericValue orderRole = delegator.findOne("OrderRole", false, UtilMisc.toMap("orderId", order.get("orderId"), "partyId", deliverierId, "roleTypeId", "LOG_DELIVERER"));
						if (orderRole != null)
							orderRole.remove();
					}
					if ("CREATED".equals((String) order.get("statusId")) && order.get("packId").equals("null")) {
						Map<String, Object> dataSer = ServiceUtil.setServiceFields(dispatcher, "addShippingTripPack", order, userLogin, null, null);
						dispatcher.runSync("addShippingTripPack", dataSer);
					}
				}
			}


		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		returnResult.put("shippingTripId", shippingTripId);

		return returnResult;
	}


	public static Map<String, Object> updateShippingTripInfo(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> returnResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String shippingTripId = (String) context.get("shippingTripId");
		Long startDateTime = Long.parseLong((String) context.get("startDateTime"));
		Long finishedDateTime = Long.parseLong((String) context.get("finishedDateTime"));
		String description = (String) context.get("description");
		String deliverierId = (String) context.get("deliverierId");
		BigDecimal tripCost = new BigDecimal((String) context.get("tripCost"));
		BigDecimal costCustomerPaid = new BigDecimal((String) context.get("costCustomerPaid"));
		if (shippingTripId != null) {
			try {
				GenericValue shippingTrip = delegator.findOne("ShippingTrip", false, UtilMisc.toMap("shippingTripId", shippingTripId));
				if (UtilValidate.isNotEmpty(shippingTrip)) {
					shippingTrip.set("startDateTime", new Timestamp(startDateTime));
					shippingTrip.set("finishedDateTime", new Timestamp(finishedDateTime));
					//shippingTrip.set("deliverierId", deliverierId);
					shippingTrip.set("tripCost", tripCost);
					shippingTrip.set("costCustomerPaid", costCustomerPaid);
					if (UtilValidate.isNotEmpty(deliverierId))
						shippingTrip.set("shipperId", deliverierId);
					if (UtilValidate.isNotEmpty(description))
						shippingTrip.set("description", description);
					delegator.store(shippingTrip);
				}
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when run Service updateshippingTripInfo: " + e.toString();
				Debug.logError(e, errMsg, module);
				e.printStackTrace();
				return ServiceUtil.returnError(errMsg);
			}
			returnResult.put("shippingTripId", shippingTripId);
			return returnResult;
		}
		String errMsg = "OLBIUS: Fatal error when run Service updateshippingTripInfo: ";
		return ServiceUtil.returnError(errMsg);
	}

	public static Map<String, Object> addShippingTripPack(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> returnResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = FastMap.newInstance();
		@SuppressWarnings("unused")
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			Map<String, Object> initMap = FastMap.newInstance();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> result;
			Map<String, Object> initData = new HashMap<>();
			// create delivery
			String shippingTripId = (String) context.get("shippingTripId");
			GenericValue shippingTrip = delegator.findOne("ShippingTrip", false, UtilMisc.toMap("shippingTripId", shippingTripId));
			initData.put("facilityId", shippingTrip.get("facilityId"));
			initData.put("orderId", context.get("orderId"));
			initData.put("contactMechId", shippingTrip.get("originContactMechId"));
			initData.put("userLogin", (GenericValue) context.get("userLogin"));
			result = dispatcher.runSync("quickCreateDelivery", initData);
			String deliveryId = (String) result.get("deliveryId");
			// create pack
			List<Map<String, Object>> packs = new ArrayList<>();
			List<GenericValue> listDeliveryItem = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
			List<EntityCondition> listCondsRole = new ArrayList<>();
			listCondsRole.add(EntityCondition.makeCondition(UtilMisc.toMap("orderId", listDeliveryItem.get(0).get("fromOrderId"))));
			listCondsRole.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", "END_USER_CUSTOMER")));
			List<GenericValue> ordersRoles = delegator.findList("OrderHeaderAndRoleSummary", EntityCondition.makeCondition(listCondsRole), null, null, null, false);
			List<EntityCondition> listCondsContact = new ArrayList<>();
			listCondsContact.add(EntityCondition.makeCondition(UtilMisc.toMap("orderId", listDeliveryItem.get(0).get("fromOrderId"))));
			listCondsContact.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION")));

			List<GenericValue> orderContactMech = delegator.findList("OrderContactMech", EntityCondition.makeCondition(listCondsContact), null, null, null, false);
			List<Map<String, Object>> listProduct = new ArrayList<>();
			for (GenericValue gv : listDeliveryItem) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("productId", gv.get("productId"));
				item.put("quantity", gv.get("quantity"));
				item.put("quantityUomId", gv.get("quantityUomId"));
				item.put("weightUomId", gv.get("weightUomId"));
				item.put("deliveryId", gv.get("deliveryId"));
				item.put("deliveryItemSeqId", gv.get("deliveryItemSeqId"));
				listProduct.add(item);
			}
			initData = new HashMap<>();
			initData.put("shipAfterDate", ((Timestamp) shippingTrip.get("startDateTime")).getTime() + "");
			initData.put("shipBeforeDate", ((Timestamp) shippingTrip.get("finishedDateTime")).getTime() + "");
			initData.put("customerId", ordersRoles.get(0).get("partyId"));
			initData.put("userLogin", (GenericValue) context.get("userLogin"));
			//initMap.put("orderId", listDeliveryItem.get(0).get("fromOrderId"));
			initData.put("destContactMechId", orderContactMech.get(0).get("contactMechId"));
			initData.put("listProducts", listProduct);
			result = dispatcher.runSync("createPack", initData);

			// create shipping trip pack
			String packId = (String) result.get("packId");
			GenericValue tripPack = delegator.makeValue("ShippingTripPack");
			tripPack.put("shippingTripId", context.get("shippingTripId"));
			tripPack.put("packId", packId);
			tripPack.put("statusId", "SHIP_PACK_CREATED");
			try {
				delegator.create(tripPack);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: Create Pack item error");
			}
			// Update status of Pack
//			GenericValue packGV = delegator.findOne("Pack", false, UtilMisc.toMap("packId", packId));
//			String statusPack = "PACK_BEING_DLIED";
//			packGV.set("statusId", statusPack);
//			try {
//				packGV.store();
//			} catch (GenericEntityException e) {
//				return ServiceUtil.returnError("OLBIUS: Update Pack's status error");
//			}
			// create order role
			GenericValue orderRole = delegator.makeValue("OrderRole");
			orderRole.put("partyId", shippingTrip.get("shipperId"));
			orderRole.put("orderId", context.get("orderId"));
			orderRole.put("roleTypeId", "LOG_DELIVERER");
			orderRole.store();

		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createNewShippingTripByOrder(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		List<Object> listItemTmp = (List<Object>) context.get("listOrders");
		Boolean isJson = false;
		if (!listItemTmp.isEmpty()) {
			if (listItemTmp.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listOrders = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "[" + (String) listItemTmp.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("orderId")) {
					mapItems.put("orderId", item.getString("orderId"));
				}
				listOrders.add(mapItems);
			}
		} else {
			listOrders = (List<Map<String, String>>) context.get("listOrders");
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = ctx.getDelegator();
		String ancestorId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Map<String, Object> initMap = FastMap.newInstance();

		if (UtilValidate.isNotEmpty(context.get("driverPartyId"))) {
			initMap.put("shipperId", context.get("driverPartyId"));
		}
		String statusId = "TRIP_CREATED";

		if (UtilValidate.isNotEmpty(context.get("tripCost"))) {
			initMap.put("tripCost", new BigDecimal((String) context.get("tripCost")));
		}
		if (UtilValidate.isNotEmpty(context.get("tripReturnCost"))) {
			initMap.put("costCustomerPaid", new BigDecimal((String) context.get("tripReturnCost")));
		}
		initMap.put("facilityId", context.get("facilityId"));
		initMap.put("contactMechId", context.get("contactMechId"));
		initMap.put("statusId", statusId);
		initMap.put("fromDate", context.get("fromDate"));
		initMap.put("thruDate", context.get("thruDate"));
		initMap.put("description", context.get("description"));
		initMap.put("userLogin", (GenericValue) context.get("userLogin"));

		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> result;
		Map<String, Object> successResult = FastMap.newInstance();
		List<String> deliveryIds = new ArrayList<>();
		for (Map<String, String> order : listOrders) {
			Map<String, Object> initData = new HashMap<>();
			initData.put("facilityId", initMap.get("facilityId"));
			initData.put("orderId", order.get("orderId"));
			initData.put("contactMechId", initMap.get("contactMechId"));
			initData.put("userLogin", (GenericValue) context.get("userLogin"));
			result = dispatcher.runSync("quickCreateDelivery", initData);
			deliveryIds.add((String) result.get("deliveryId"));
		}
		List<Map<String, Object>> packs = new ArrayList<>();
		for (String deliveryId : deliveryIds) {
			GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			List<GenericValue> listDeliveryItem = delegator.findList("DeliveryItemView", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
//			List<EntityCondition> listCondsRole = new ArrayList<>();
//			listCondsRole.add(EntityCondition.makeCondition(UtilMisc.toMap("orderId", listDeliveryItem.get(0).get("fromOrderId"))));
//			listCondsRole.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", "END_USER_CUSTOMER")));
//			List<GenericValue> ordersRoles = delegator.findList("OrderHeaderAndRoleSummary", EntityCondition.makeCondition(listCondsRole), null, null, null, false);
			List<EntityCondition> listCondsContact = new ArrayList<>();
			listCondsContact.add(EntityCondition.makeCondition(UtilMisc.toMap("orderId", listDeliveryItem.get(0).get("fromOrderId"))));
			listCondsContact.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION")));

			List<GenericValue> orderContactMech = delegator.findList("OrderContactMech", EntityCondition.makeCondition(listCondsContact), null, null, null, false);
			List<Map<String, Object>> listProduct = new ArrayList<>();
			for (GenericValue gv : listDeliveryItem) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("productId", gv.get("productId"));
				item.put("quantity", gv.get("quantity"));
				item.put("quantityUomId", gv.get("quantityUomId"));
				item.put("weightUomId", gv.get("weightUomId"));
				item.put("deliveryId", gv.get("deliveryId"));
				item.put("deliveryItemSeqId", gv.get("deliveryItemSeqId"));
				listProduct.add(item);
			}
			Map<String, Object> initData = new HashMap<>();
			initData.put("shipAfterDate", context.get("fromDate"));
			initData.put("shipBeforeDate", context.get("thruDate"));
			initData.put("customerId", delivery.get("partyIdTo"));
			initData.put("userLogin", (GenericValue) context.get("userLogin"));
			//initMap.put("orderId", listDeliveryItem.get(0).get("fromOrderId"));
			initData.put("destContactMechId", orderContactMech.get(0).get("contactMechId"));
			initData.put("listProducts", listProduct);
			result = dispatcher.runSync("createPack", initData);
			Map<String, Object> pack = FastMap.newInstance();
			pack.put("packId", (String) result.get("packId"));
			packs.add(pack);
		}
		Map<String, Object> initData = new HashMap<>();
		initMap.put("shipAfterDate", context.get("fromDate"));
		initMap.put("shipBeforeDate", context.get("thruDate"));
		initMap.put("originContactMechId", context.get("contactMechId"));
		initMap.put("packItems", packs);
		try {
			initData = ServiceUtil.setServiceFields(dispatcher, "createShippingTrip", initMap, userLogin, null, null);
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		}
		result = dispatcher.runSync("createShippingTrip", initData);
		for (Map<String, String> order : listOrders) {
			GenericValue orderRole = delegator.makeValue("OrderRole");
			orderRole.put("partyId", context.get("driverPartyId"));
			orderRole.put("orderId", order.get("orderId"));
			orderRole.put("roleTypeId", "LOG_DELIVERER");
			orderRole.store();
		}

		successResult.put("tripId", result.get("tripId"));
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQGetListDeliveryItemAndProductId(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		//EntityListIterator listIterator = null;
		List<GenericValue> listIterator = null;

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("deliveryTypeId", DeliveryTypeEnum.DELIVERY_SALES.name());
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		String customerId = null;
		if (!UtilValidate.isEmpty(parameters.get("customerId"))) {
			customerId = parameters.get("customerId")[0];
			mapCondition.put("partyIdTo", customerId);
			tmpConditon = EntityCondition.makeCondition(mapCondition);
			listAllConditions.add(tmpConditon);
		}
		List<String> deliveryIds = null;
		if (!UtilValidate.isEmpty(parameters.get("deliveryId[]"))) {
			deliveryIds = Arrays.asList(parameters.get("deliveryId[]"));
			listAllConditions.add(EntityCondition.makeCondition("deliveryId", EntityOperator.IN, deliveryIds));
		}
		//listAllConditions.add(EntityCondition.makeCondition("quantityOnHand",EntityOperator.GREATER_THAN,new BigDecimal(0.0)));
		try {

			listIterator = EntityMiscUtil.processIteratorToList(parameters,
					successResult, delegator, "DeliveryItemAndProductIdView",
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND),
					null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListDeliveryItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}


		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> JQGetListDeliveryByCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		//EntityListIterator listIterator = null;
		List<GenericValue> listIterator = null;
		String customerId = null;
		if (!UtilValidate.isEmpty(parameters.get("customerId"))) {
			customerId = parameters.get("customerId")[0];
		}
		List<String> deliveryIds = null;
		if (!UtilValidate.isEmpty(parameters.get("selectedDelivery[]"))) {
			deliveryIds = Arrays.asList(parameters.get("selectedDelivery[]"));
		}
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("deliveryTypeId", DeliveryTypeEnum.DELIVERY_SALES.name());
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		mapCondition.put("partyIdTo", customerId);
		tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isPacked", null),
				EntityOperator.OR,
				EntityCondition.makeCondition("isPacked", "N")));
		tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			listIterator = EntityMiscUtil.processIteratorToList(parameters,
					successResult, delegator, "Delivery",
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND),
					null, null, listSortFields, opts);
			if (deliveryIds != null && deliveryIds.size() > 0)
				for (String deliveryId : deliveryIds) {
					GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
					listIterator.add(0, delivery);
				}

		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListDeliveryItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		successResult.put("TotalRows", listIterator.size() + "");
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> JQGetListDeliveryByPack(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		//EntityListIterator listIterator = null;
		List<GenericValue> listIterator = null;
		String packId = null;
		if (!UtilValidate.isEmpty(parameters.get("packId"))) {
			packId = parameters.get("packId")[0];
		}

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, String> mapCondition = new HashMap<String, String>();

		EntityCondition tmpConditon = null;

		mapCondition.put("packId", packId);
		tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);

		tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {

			listIterator = EntityMiscUtil.processIteratorToList(parameters,
					successResult, delegator, "PackItemAndDeliveryView",
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND),
					null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListDeliveryItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}