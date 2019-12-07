package com.olbius.baselogistics.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.SecurityUtil;
import com.olbius.baselogistics.LogisticsServices;
import com.olbius.baselogistics.util.LogisticsOrderUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;

public class OrderServices {
	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsUiLabels";
	public static final String resourceCommonEntity = "CommonEntityLabels";
	public static final String OrderEntityLabels = "OrderEntityLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	public static Map<String, Object> updateOrderHoldingReason(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	String enumId = (String)context.get("enumId");
    	String enumCode = (String)context.get("enumCode");
    	String enumTypeId = (String)context.get("enumTypeId");
    	String description = (String)context.get("description");
    	if (enumId != null){
    		GenericValue enumObj = delegator.findOne("Enumeration", false, UtilMisc.toMap("enumId", enumId));
    		if (enumObj != null){
    			enumObj.put("enumCode", enumCode);
    			enumObj.put("enumTypeId", enumTypeId);
    			enumObj.put("description", description);
    			delegator.store(enumObj);
    		} else {
    			enumObj = delegator.makeValue("Enumeration");
    			enumObj.put("enumId", enumId);
    			enumObj.put("enumCode", enumCode);
    			enumObj.put("enumTypeId", enumTypeId);
    			enumObj.put("description", description);
    			delegator.store(enumObj);
    		}
    	} else {
    		GenericValue enumObj = delegator.makeValue("Enumeration");
			enumObj.setNextSeqId();
			enumObj.put("enumCode", enumCode);
			enumObj.put("enumTypeId", enumTypeId);
			enumObj.put("description", description);
			delegator.create(enumObj);
			enumId = enumObj.getString("enumId");
    	}
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("enumId", enumId);
    	return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOrderHoldingReasons(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listReasons = new ArrayList<GenericValue>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String enumTypeId = null;
    	if (parameters.get("enumTypeId") != null && parameters.get("enumTypeId").length > 0){
    		enumTypeId = (String)parameters.get("enumTypeId")[0];
    	}
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	if (enumTypeId != null && !"".equals(enumTypeId)){
    		mapCondition.put("enumTypeId", enumTypeId);
    	}
    	
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	listReasons = delegator.findList("Enumeration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listReasons);
    	return successResult;
	}
	
	public static Map<String, Object> getOrderParty(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator();
    	List<Map<String, String>> listOParty = LogisticsOrderUtil.getOrderParty(delegator, orderId);
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("listOParty", listOParty);
    	return mapReturn;
	}
	
	public static Map<String, Object> checkOrderExported(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator();
    	Boolean isExported = LogisticsOrderUtil.checkOrderExported(delegator, orderId);
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("isExported", isExported);
    	return mapReturn;
	}
	
	public static Map<String, Object> checkOrderReceived(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		Boolean isReceived = LogisticsOrderUtil.checkOrderReceived(delegator, orderId);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("isReceived", isReceived);
		return mapReturn;
	}
	
	public static Map<String, Object> getOrderPartyNameView(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator(); 
    	List<GenericValue> listOrderParties = new ArrayList<GenericValue>();
    	listOrderParties = LogisticsOrderUtil.getOrderPartyNameView(delegator, orderId);
    	Map<String, Object> mapReturn = FastMap.newInstance();
    	mapReturn.put("listOrderParties", listOrderParties);
    	return mapReturn;
	}
	
	public static Map<String, Object> changeOrderStatusToHeld(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String)context.get("orderId");
		String statusId = (String)context.get("statusId");
		String noteInfo = (String)context.get("noteInfo");
		String changeReason = (String)context.get("changeReason");
		
		Map<String, Object> mapChange = FastMap.newInstance();
		mapChange.put("orderId", orderId);
		mapChange.put("statusId", statusId);
		mapChange.put("changeReason", changeReason);
		mapChange.put("userLogin", (GenericValue)context.get("userLogin"));
		
		try {
			dispatcher.runSync("changeOrderStatus", mapChange);
			
			Map<String, Object> mapNote = FastMap.newInstance();
			mapNote.put("orderId", orderId);
			mapNote.put("note", noteInfo);
			mapNote.put("internalNote", "Y");
			mapNote.put("noteName", "");
			mapNote.put("userLogin", (GenericValue)context.get("userLogin"));
			
			dispatcher.runSync("createOrderNote", mapNote);
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("changeOrderStatus Error!");
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("orderId", orderId);
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> sendNotifyAfterChangedOrderStatus(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String)context.get("orderId");
		String orderStatus = "ORDER_HOLD";
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId)); 
		String productStoreId = orderHeader.getString("productStoreId");
		Map<String, Object> mapTmp = FastMap.newInstance();
		mapTmp.put("productStoreId", productStoreId);
		mapTmp.put("statusId", orderStatus);
		mapTmp.put("userLogin", userLogin);
		Map<String, Object> map = FastMap.newInstance();
		try {
			map = dispatcher.runSync("getPartiesSalesReceiveNotifyOrder", mapTmp);
			List<String> listManagers = (List<String>)map.get("partyIds");
			
			List<String> listAcc1 = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, "ACC_PAYMENT_EMP", delegator);
			List<String> listAcc2 = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, "ACC_SALES_EMP", delegator);
			if (listAcc1 != null && !listAcc1.isEmpty()){
				listManagers.addAll(listAcc1);
			}
			if (listAcc2 != null && !listAcc2.isEmpty()){
				listManagers.addAll(listAcc2);
			}
			if (!listManagers.isEmpty()){
				String header = UtilProperties.getMessage(resource, "HasAnOrderChangeToHeld", (Locale)context.get("locale")) +", "+ UtilProperties.getMessage(resource, "OrderId", (Locale)context.get("locale")) +": [" +orderId+"]";
				for (String partyId : listManagers){
//					String targetLink = "orderId="+orderId;
					Map<String, Object> mapContext = new HashMap<String, Object>();
					mapContext.put("partyId", partyId);
					mapContext.put("action", "viewOrder?orderId="+orderId);
					mapContext.put("targetLink", "");
					mapContext.put("header", header);
					mapContext.put("ntfType", "ONE");
					mapContext.put("userLogin", userLogin);
					dispatcher.runSync("createNotification", mapContext);
				}
			}
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("sendNotifyAfterChangedOrderStatus Error!");
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("orderId", orderId);
		return mapReturn;
	}
	
	public static Map<String, Object> getAvailableFacilityToExport(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException{
        Delegator delegator = ctx.getDelegator();
        String orderId = (String)context.get("orderId");
        // 1. get  list of Facility of current Org
        List<GenericValue> orderRoleFroms = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false);
    	String orgId = orderRoleFroms.get(0).getString("partyId");
        List<GenericValue> listFacility = null;
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        String partyId = userLogin.getString("partyId");
        GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
        String productStoreId = order.getString("productStoreId");
        try {
            listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", orgId, "facilityTypeId", "WAREHOUSE")), null, null, null, false);
            
            List<GenericValue> listTmp = new ArrayList<GenericValue>();
            for (GenericValue fac : listFacility){
            	String storeId = fac.getString("productStoreId");
            	if (storeId == null || !storeId.equals(productStoreId)){
            		List<GenericValue> listStoreFac = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", fac.getString("facilityId"), "productStoreId", productStoreId)), null, null, null, false);
            		listStoreFac = EntityUtil.filterByDate(listStoreFac);
            		if (listStoreFac.isEmpty()){
            			listTmp.add(fac);
            			continue;
            		}
            	}
            	String facilityId = fac.getString("facilityId");
            	List<GenericValue> listFacilityParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", partyId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"))), null, null, null, false);
            	listFacilityParty = EntityUtil.filterByDate(listFacilityParty);
            	if (listFacilityParty.isEmpty()){
            		listTmp.add(fac);
        			continue;
            	}
            }
            listFacility.removeAll(listTmp);
        } catch (GenericEntityException e) {
            Debug.log(e.getStackTrace().toString(), module);
            return ServiceUtil.returnError("Service getAvailableFacilityToExport:" + e.toString());
        }
        // 2. get list of orderItem
        List<GenericValue> listOrderItem = null;
        
        listOrderItem = LogisticsProductUtil.getOrderItemRemains(delegator, orderId, "WAREHOUSE");
        List<GenericValue> listData = new ArrayList<GenericValue>();
        List<GenericValue> listTmpData = new ArrayList<GenericValue>();
        List<EntityCondition> listCond = new ArrayList<EntityCondition>();
        L1: for(int i = 0; i < listFacility.size(); i++){
            L2: for(int j = 0; j < listOrderItem.size();j++){
                listCond = new ArrayList<EntityCondition>();
                listCond.add(EntityCondition.makeCondition("productId", listOrderItem.get(j).getString("productId")));
                listCond.add(EntityCondition.makeCondition("facilityId", listFacility.get(i).getString("facilityId")));
                try {
                    listTmpData = delegator.findList("SumATPByProductAndEXP", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, null, false);
                } catch (GenericEntityException e) {
                    Debug.log(e.getStackTrace().toString(), module);
                    return ServiceUtil.returnError("Service getAvailableINV:" + e.toString());
                }
                for(int k = 0; k < listTmpData.size(); k++){
                    if(listTmpData.get(k).getBigDecimal("qoh").compareTo(listOrderItem.get(j).getBigDecimal("quantity")) >= 0){
                        continue L2;
                    }
                }
                continue L1;
            }
            listData.add(listFacility.get(i));
        }
        Map<String, Object> result = new FastMap<String, Object>();
        result.put("listFacilities", listData);
        return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListPurchaseOrderToAgent(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition); 
    	listAllConditions.add(tmpConditon);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String orderTypeId = null;
    	if (parameters.get("orderTypeId") != null && parameters.get("orderTypeId").length > 0){
    		orderTypeId = (String)parameters.get("orderTypeId")[0];
    	}
    	if (orderTypeId != null && !"".equals(orderTypeId)){
    		Map<String, String> mapTypeCondition = new HashMap<String, String>();
    		mapTypeCondition.put("orderTypeId", orderTypeId);
    		EntityCondition tmpTypeConditon = EntityCondition.makeCondition(mapTypeCondition);
        	listAllConditions.add(tmpTypeConditon);
    	}
    	
    	EntityListIterator listIterator = null;
		listIterator = delegator.find("OrderAndShipAndContactAndRequirement", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		List<GenericValue> listOrders = listIterator.getCompleteList();
		listIterator.close();
    	Map<String, Object> returnMap = FastMap.newInstance();
    	returnMap.put("listIterator", listOrders);
    	return returnMap;
	}
	
	public static Map<String, Object> receivePOToServiceFacility(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		List<GenericValue> listOrderItemShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		String facilityId = null;
		String shipGroupSeqId = null;
		Timestamp shipBydate = null;
		if (!listOrderItemShipGroups.isEmpty()){
			facilityId = listOrderItemShipGroups.get(0).getString("facilityId");
			shipBydate = listOrderItemShipGroups.get(0).getTimestamp("shipByDate");
			shipGroupSeqId = listOrderItemShipGroups.get(0).getString("shipGroupSeqId");
		}
		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		if (!listOrderItems.isEmpty() && facilityId != null){
			GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			for (GenericValue item : listOrderItems){
				String productId = item.getString("productId");
				String uomFromId = LogisticsProductUtil.getQuantityUomBySupplier(delegator, item.getString("productId"), orderId);
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				String uomToId = product.getString("quantityUomId");
				BigDecimal convertNumber = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, uomFromId, uomToId);
				BigDecimal quantityToReceive = item.getBigDecimal("quantity").multiply(convertNumber);
				GenericValue inventoryItem = delegator.makeValue("InventoryItem");
				inventoryItem.put("ownerPartyId", facility.getString("ownerPartyId"));
				inventoryItem.put("quantityOnHandTotal", BigDecimal.ZERO);
				inventoryItem.put("availableToPromiseTotal", BigDecimal.ZERO);
				inventoryItem.put("facilityId", facilityId);
				inventoryItem.put("ownerPartyId", facility.getString("ownerPartyId"));
				inventoryItem.put("datetimeReceived", shipBydate);
				inventoryItem.put("productId", productId);
				inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				inventoryItem.put("inventoryItemId", delegator.getNextSeqId("InventoryItem"));
				delegator.create(inventoryItem);
				
				GenericValue  tmpInvDetail = delegator.makeValue("InventoryItemDetail");
                tmpInvDetail.set("inventoryItemId", delegator.getNextSeqId("InventoryItem"));
                tmpInvDetail.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
                tmpInvDetail.set("effectiveDate", shipBydate);
                tmpInvDetail.set("quantityOnHandDiff", quantityToReceive);
                tmpInvDetail.set("availableToPromiseDiff", quantityToReceive);
                tmpInvDetail.set("accountingQuantityDiff", BigDecimal.ZERO);
                tmpInvDetail.set("orderId", orderId);
                tmpInvDetail.set("orderItemSeqId", item.get("orderItemSeqId"));
                tmpInvDetail.set("shipGroupSeqId", shipGroupSeqId);
                tmpInvDetail.create();
			}
		}
		order.put("statusId", "ORDER_COMPLETED");
		delegator.store(order);
		
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("orderId", orderId);
		return mapReturn;
	}
	
	public static Map<String,Object> updateOrderNote(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator();
    	String listItemTmp = (String)context.get("listNoteItems");
    	List<Map<String, Object>> listNoteItems = new ArrayList<Map<String,Object>>();
		JSONArray lists = JSONArray.fromObject(listItemTmp);
		for (int i = 0; i < lists.size(); i++){
			HashMap<String, Object> mapItems = new HashMap<String, Object>();
			JSONObject item = lists.getJSONObject(i);
			if (item.containsKey("quantity")){
				mapItems.put("quantity", new BigDecimal(item.getString("quantity")));
			}
			if (item.containsKey("productId")){
				mapItems.put("productId", item.getString("productId"));
			}
			if (item.containsKey("returnReasonId")){
				mapItems.put("returnReasonId", item.getString("returnReasonId"));
			}
			if (item.containsKey("inventoryItemStatusId")){
				mapItems.put("inventoryItemStatusId", item.getString("inventoryItemStatusId"));
			}
			if (item.containsKey("isReceiveProduct")){
				mapItems.put("isReceiveProduct", item.getString("isReceiveProduct"));
			}
			if (item.containsKey("quantityUomId")){
				mapItems.put("quantityUomId", item.getString("quantityUomId"));
			}
			listNoteItems.add(mapItems);
		}
		Locale locale = (Locale)context.get("locale");
    	if (!listNoteItems.isEmpty()){
    		for (Map<String, Object> item : listNoteItems){
    			GenericValue note = delegator.makeValue("NoteData");
    			GenericValue uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", item.get("quantityUomId")));
    			GenericValue reason = delegator.findOne("ReturnReason", false, UtilMisc.toMap("returnReasonId", item.get("returnReasonId")));
				GenericValue invStatus = delegator.findOne("StatusItem", false, UtilMisc.toMap("statusId", item.get("inventoryItemStatusId")));
				if (invStatus != null){
    				if ("Y".equals(item.get("isReceiveProduct"))){
        				note.put("noteInfo", UtilProperties.getMessage(resource, "ProductId", locale) +": " + item.get("productId") + ". " + UtilProperties.getMessage(resource, "DeliveryMissing", locale) + ": " +  item.get("quantity").toString() + " ("+ uom.get("description", resourceCommonEntity, locale) +"). " + UtilProperties.getMessage(resource, "Reason", locale) + ": " +reason.get("description", OrderEntityLabels, locale) + ". " + UtilProperties.getMessage(resource, "ProductStatus", locale) + ": " + invStatus.get("description", resourceCommonEntity, locale) + ". " + UtilProperties.getMessage(resource, "ReturnReceived", locale) + ".");
        			} else {
        				note.put("noteInfo", UtilProperties.getMessage(resource, "ProductId", locale) +": " + item.get("productId") + ". " + UtilProperties.getMessage(resource, "DeliveryMissing", locale) + ": " +  item.get("quantity").toString() + " ("+ uom.get("description", resourceCommonEntity, locale) +"). " + UtilProperties.getMessage(resource, "Reason", locale) + ": " +reason.get("description", OrderEntityLabels, locale) + ". " + UtilProperties.getMessage(resource, "ProductStatus", locale) + ": " + invStatus.get("description", resourceCommonEntity, locale) + ".");
        			}
    			} else {
    				if ("Y".equals(item.get("isReceiveProduct"))){
        				note.put("noteInfo", UtilProperties.getMessage(resource, "ProductId", locale) +": " + item.get("productId") + ". " + UtilProperties.getMessage(resource, "DeliveryMissing", locale) + ": " +  item.get("quantity").toString() + " ("+ uom.get("description", resourceCommonEntity, locale) +"). " + UtilProperties.getMessage(resource, "Reason", locale) + ": " +reason.get("description", OrderEntityLabels, locale) + ". " + UtilProperties.getMessage(resource, "ProductStatus", locale) + ": " + UtilProperties.getMessage(resource, "InventoryGood", locale) + ". " + UtilProperties.getMessage(resource, "ReturnReceived", locale) + ".");
        			} else {
        				note.put("noteInfo", UtilProperties.getMessage(resource, "ProductId", locale) +": " + item.get("productId") + ". " + UtilProperties.getMessage(resource, "DeliveryMissing", locale) + ": " +  item.get("quantity").toString() + " ("+ uom.get("description", resourceCommonEntity, locale) +"). " + UtilProperties.getMessage(resource, "Reason", locale) + ": " +reason.get("description", OrderEntityLabels, locale) + ". " + UtilProperties.getMessage(resource, "ProductStatus", locale) + ": " + UtilProperties.getMessage(resource, "InventoryGood", locale) + ".");
        			}
    			}
    			
    			String noteId = delegator.getNextSeqId("NoteData");
    			note.put("noteId", noteId);
    			delegator.create(note);
    			GenericValue orderNote = delegator.makeValue("OrderHeaderNote");
    			
    			orderNote.put("orderId", orderId);
    			orderNote.put("noteId", noteId);
    			orderNote.put("internalNote", "Y");
    			delegator.create(orderNote);
    		}
    	}
    	return result;
   	}
	
	public static Map<String,Object> getOrderRoleAndParty(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> listOrderParties = delegator.findList("OrderRolePartyNameView", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
    	Map<String, Object> result = new FastMap<String, Object>();
    	result.put("listParties", listOrderParties);
    	result.put("orderId", orderId);
    	return result;
	}
	
	public static Map<String,Object> sendNotifyToLogStorekeeperNewOrder(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator();
    	String facilityId = null;
    	GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    	String messages = "";
    	String action = "";
    	if ("SALES_ORDER".equals(orderHeader.getString("orderTypeId"))){
    		facilityId = orderHeader.getString("originFacilityId");
    		messages = "NeedsToPrepareProductToExport";
    		action = "viewOrder?orderId="+orderId;
    	} else if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))){
    		List<GenericValue> listOrderShipGroups = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);    	
        	if (!listOrderShipGroups.isEmpty()){
        		facilityId = listOrderShipGroups.get(0).getString("facilityId");
        	}
        	messages = "NeedsToPrepareWarehouseToReceive";
        	action = "viewDetailPO?orderId="+orderId;
    	}
    	GenericValue orderType = delegator.findOne("OrderType", false, UtilMisc.toMap("orderTypeId", orderHeader.getString("orderTypeId")));
    	if (UtilValidate.isNotEmpty(facilityId) && "ORDER_APPROVED".equals(orderHeader.getString("statusId"))){
    		List<GenericValue> listStorekeepers = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"))), null, null, null, false);
    		listStorekeepers = EntityUtil.filterByDate(listStorekeepers);
    		if (!listStorekeepers.isEmpty()){
    			LocalDispatcher dispatcher = ctx.getDispatcher();
    			for (GenericValue party : listStorekeepers) {
    				Map<String, Object> mapContext = new HashMap<String, Object>();
    				String header = UtilProperties.getMessage(resource, "Has", (Locale)context.get("locale"))+ " " + StringUtil.wrapString((String)orderType.get("description", (Locale)context.get("locale"))).toString().toLowerCase() + " " + StringUtil.wrapString(UtilProperties.getMessage(resource, messages, (Locale)context.get("locale"))).toString().toLowerCase() + ", "+ UtilProperties.getMessage(resource, "OrderId", (Locale)context.get("locale")) +": [" +orderId+"]";
//    				String target = "orderId="+orderId;
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
    	}
    	return ServiceUtil.returnSuccess();
	}
	
	public static Map<String,Object> updateReservesToOriginFacilitySalesOrder(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String orderId = (String)context.get("orderId");
    	Delegator delegator = ctx.getDelegator();
    	GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    	String facilityId = orderHeader.getString("originFacilityId");
    	if (UtilValidate.isNotEmpty(facilityId)){
    		// Cancel all old reserves
    		LocalDispatcher dispatcher = ctx.getDispatcher();
    		try {
				dispatcher.runSync("cancelOrderInventoryReservation", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "orderId", orderId));
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: runsync cancelOrderInventoryReservation service error!");
			}
    		// Create new reserves for new facility
    		try {
				dispatcher.runSync("quickReserveInventoryForOrderAndFacility", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "orderId", orderId, "facilityId", facilityId));
			} catch (GenericServiceException e){
				return ServiceUtil.returnError("OLBIUS: runsync quickReserveInventoryForOrderAndFacility service error!");
			}
    	}
    	Map<String, Object> result = new FastMap<String, Object>();
    	result.put("orderId", orderId);
    	return result;
	}
    
	public static Map<String,Object> quickShipSalesOrderFromOriginFacility(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	String orderId = (String)context.get("orderId");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			dispatcher.runSync("updateReservesToOriginFacilitySalesOrder", UtilMisc.toMap("userLogin", (GenericValue)context.get("userLogin"), "orderId", orderId));
		} catch (GenericServiceException e){
			return ServiceUtil.returnError("OLBIUS: runsync updateReservesToOriginFacilitySalesOrder service error!");
		}
		try {
			// get system user 
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			dispatcher.runSync("quickShipEntireOrder", UtilMisc.toMap("userLogin", system, "orderId", orderId));
		} catch (GenericServiceException e){
			return ServiceUtil.returnError("OLBIUS: runsync quickShipEntireOrder service error!");
		}
    	Map<String, Object> result = new FastMap<String, Object>();
    	result.put("orderId", orderId);
    	return result;
	}


	/**
	 * Shipping MT
	 */

	public static Map<String, Object> JQGetListOrderInArea(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("customerId");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listItems = null;
		try {
			if (parameters.get("shipperId") != null) {
				if ("Y".equals(parameters.get("isIn")[0])) {
					String shipperId = parameters.get("shipperId")[0];
					List<GenericValue> listGV = delegator.findList("DeliveryCluster", EntityCondition.makeCondition(UtilMisc.toMap("executorId", shipperId)), null, null, null, false);
					List<String> clusters = new ArrayList<>();
					for (GenericValue gv : listGV) {
						clusters.add((String) gv.get("deliveryClusterId"));
					}
					listAllConditions.add(EntityCondition.makeCondition("deliveryClusterId", EntityOperator.IN, clusters));
				} else {
					String shipperId = parameters.get("shipperId")[0];
					List<GenericValue> listGV = delegator.findList("DeliveryCluster", EntityCondition.makeCondition("executorId", shipperId), null, null, null, false);
					List<String> clusters = new ArrayList<>();
					for (GenericValue gv : listGV) {
						clusters.add((String) gv.get("deliveryClusterId"));
					}
					listAllConditions.add(EntityCondition.makeCondition("deliveryClusterId", EntityOperator.NOT_IN, clusters));
				}
			}

			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("deliveryId",null,"deliveryStatusId","DLV_CANCELLED"),EntityOperator.OR));
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("statusId","ORDER_APPROVED","deliveryClusterStatusId","DELIVERY_CLUSTER_ENABLED")));
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("shipmentMethodTypeId","GROUND_HOME")));
			listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "OrderHeaderAndDeliveryClusterCustomerView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListPack service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}

		successResult.put("listIterator", listItems);
		return successResult;
	}

	public static Map<String, Object> JQGetListOrderWithPackDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");

		for (int i = 0; i < listAllConditions.size(); i++) {
			if (listAllConditions.get(i) instanceof EntityExpr) {
				boolean xd = false;
				EntityExpr e = (EntityExpr) listAllConditions.get(i);

				if("=".equals(e.getOperator().getCode()))
				if (((EntityFieldValue) e.getLhs()).getFieldName().equals("statusId")
						&& ( e.getRhs().equals("NULL"))) {
					EntityCondition ee = EntityCondition.makeCondition("statusId", null);
					listAllConditions.set(i, ee);
				}
			} else {
				EntityConditionList e = (EntityConditionList) listAllConditions.get(i);
				boolean xd = false;
				for (int j = 0; j < e.getConditionListSize(); j++) {
					if("=".equals(((EntityExpr) e.getCondition(j)).getOperator().getCode()))
					if (((EntityFieldValue) ((EntityExpr) e.getCondition(j)).getLhs()).getFieldName().equals("statusId")
							&& ((EntityExpr) e.getCondition(j)).getRhs().equals("NULL")) {
						xd = true;
						break;
					}
				}
				List<EntityCondition> leNew = new ArrayList<>();
				if (xd == true) {
					for (int j = 0; j < e.getConditionListSize(); j++)
						if("=".equals(((EntityExpr) e.getCondition(j)).getOperator().getCode()))
						if (((EntityFieldValue) ((EntityExpr) e.getCondition(j)).getLhs()).getFieldName().equals("statusId")
								&& ((EntityExpr) e.getCondition(j)).getRhs().equals("NULL")) {
							EntityCondition ee = EntityCondition.makeCondition("statusId", null);
							leNew.add(ee);
						} else {
							leNew.add(e.getCondition(j));
						}
					listAllConditions.set(i, EntityCondition.makeCondition(leNew, EntityOperator.OR));
				}
			}
		}

		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("orderDate DESC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listItems = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			if (organizationId != null) {
					listAllConditions.add(EntityCondition.makeCondition("sellerId", organizationId));
			}
			listItems = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "OrderHeaderAndPackDeliveryClusterCustomerView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListOrderWithPackDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listItems);
		return successResult;
	}

}