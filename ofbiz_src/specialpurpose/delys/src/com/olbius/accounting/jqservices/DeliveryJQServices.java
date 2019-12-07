package com.olbius.accounting.jqservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.ProductUtil;
import com.olbius.util.SecurityUtil;

public class DeliveryJQServices {
	
	public static final String module = CostsJQServices.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listDeliveries = new ArrayList<GenericValue>();
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String orderId = null;
    	String deliveryTypeId = null;
    	String deliveryId = null;
    	if (parameters.get("deliveryId") != null && parameters.get("deliveryId").length > 0){
    		deliveryId = (String)parameters.get("deliveryId")[0];
    	}
    	if (parameters.get("orderId") != null && parameters.get("orderId").length > 0){
    		orderId = (String)parameters.get("orderId")[0];
    	}
    	if (orderId != null && !"".equals(orderId)){
    		mapCondition.put("orderId", orderId);
    	}
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	mapCondition = new HashMap<String, String>();
    	if (parameters.get("deliveryTypeId") != null && parameters.get("deliveryTypeId").length > 0){
    		deliveryTypeId = (String)parameters.get("deliveryTypeId")[0];
    	}
    	if (deliveryTypeId != null && !"".equals(deliveryTypeId)){
    		mapCondition.put("deliveryTypeId", deliveryTypeId);
    	}
    	EntityCondition typeConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(typeConditon);
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listFilter = new ArrayList<GenericValue>();
    	try {
    		listDeliveries = delegator.findList("DeliveryDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		GenericValue userLogin = (GenericValue)context.get("userLogin");
    		String partyId = userLogin.getString("partyId");
    		List<GenericValue> listFacilityParties = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "roleTypeId", "LOG_SPECIALIST")), null, null, null, false);
    		listFacilityParties = EntityUtil.filterByDate(listFacilityParties);
    		if (listFacilityParties.isEmpty()){
    			listFacilityParties = new ArrayList<GenericValue>();
    			listFacilityParties = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "roleTypeId", "LOG_STOREKEEPER")), null, null, null, false);
        		listFacilityParties = EntityUtil.filterByDate(listFacilityParties);
        		if (!listFacilityParties.isEmpty()){
        			for (GenericValue dlv : listDeliveries){
        				String originFacility = dlv.getString("originFacilityId");
        				String destFacility = dlv.getString("destFacilityId");
        				for (GenericValue faParty : listFacilityParties){
        					String facilityId = faParty.getString("facilityId");
        					if (originFacility.equals(facilityId) || destFacility.equals(facilityId)){
        						listFilter.add(dlv);
        						break;
        					}
        				}
        			}
        		} else {
        			listFilter.addAll(listDeliveries);
        		}
    		}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getPurchaseDeliverys service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	if (!listFilter.isEmpty()){
    		listDeliveries = new ArrayList<GenericValue>();
    		listDeliveries.addAll(listFilter);
		}
    	if (deliveryId != null && !"".equals(deliveryId)){
    		GenericValue delivery = null;
    		try {
    			delivery = delegator.findOne("DeliveryDetail", false, UtilMisc.toMap("deliveryId", deliveryId));
			} catch (GenericEntityException e) {
				ServiceUtil.returnError("Delivery not found!");
			}
    		listDeliveries.set(0, delivery);
    	}
    	successResult.put("listIterator", listDeliveries);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
   	public static Map<String, Object> getListOrderItemDelivery(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
       	String orderId = parameters.get("orderId")[0];
       	
       	String facilityId = null;
       	
       	if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
       		facilityId = parameters.get("facilityId")[0];
       	}
       	// 1. Get list of orderItem
       	List<GenericValue> orderItems = null;
       	try {
       	    orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
        } catch (GenericEntityException e1) {
            String errMsg = "Fatal error calling getListOrderItemDelivery service: " + e1.toString();
            Debug.logError(e1, errMsg, module);
            return ServiceUtil.returnError(e1.getStackTrace().toString());
        } 
       	// TODO use View Entity
       	// 2. Get number of deliveryItems already created.
       	List<EntityCondition> condList = new ArrayList<EntityCondition>();
       	List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
       	for(GenericValue item: orderItems){
       	    EntityCondition cond1 = EntityCondition.makeCondition("fromOrderId", EntityComparisonOperator.EQUALS, item.getString("orderId"));
       	    EntityCondition cond2 = EntityCondition.makeCondition("fromOrderItemSeqId", EntityComparisonOperator.EQUALS, item.getString("orderItemSeqId"));
       	    condList.add(EntityCondition.makeCondition(EntityJoinOperator.AND, cond1, cond2));
       	    HashMap<String, Object> tmpMap = new HashMap<String, Object>();
       	    tmpMap.put("orderItemSeqId", item.getString("orderItemSeqId"));
       	    tmpMap.put("orderId", item.getString("orderId"));
   	    	tmpMap.put("expireDate", item.getString("expireDate"));
       	    String baseQuantityUomId = null;
       	    String baseWeightUomId = null;
       	    BigDecimal weight = BigDecimal.ZERO;
       	    String productName = null;
       	    try {
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.getString("productId")));
				baseQuantityUomId = product.getString("quantityUomId");
				baseWeightUomId = product.getString("weightUomId");
				productName = product.getString("productName");
				weight = product.getBigDecimal("weight");
			} catch (GenericEntityException e) {
				ServiceUtil.returnError("findOne product error!"+ e.toString());
			}
       	    String productId = item.getString("productId");
       	    if (baseQuantityUomId != null && item.getString("quantityUomId") != null){
       	    	BigDecimal convertNumber = BigDecimal.ONE;
       	    	convertNumber = ProductUtil.getConvertNumber(delegator, convertNumber, productId, item.getString("quantityUomId"), baseQuantityUomId);
       	    	weight = weight.multiply(convertNumber);
       	    }
       	    tmpMap.put("productId", productId);
       	    tmpMap.put("productName", productName);
       	    tmpMap.put("quantityUomId", item.getString("quantityUomId"));
       	    tmpMap.put("createdQuantity", new BigDecimal(0));
       	    tmpMap.put("baseQuantityUomId", baseQuantityUomId);
       	    tmpMap.put("baseWeightUomId", baseWeightUomId);
       	    tmpMap.put("weight", weight);
       	 
       	    if(item.getBigDecimal("cancelQuantity") != null){
       	        tmpMap.put("requiredQuantity", item.getBigDecimal("alternativeQuantity").subtract(item.getBigDecimal("cancelQuantity")));
       	        tmpMap.put("requiredQuantityTmp", item.getBigDecimal("alternativeQuantity").subtract(item.getBigDecimal("cancelQuantity")));
       	    }else{
       	        tmpMap.put("requiredQuantity", item.getBigDecimal("alternativeQuantity"));
       	        tmpMap.put("requiredQuantityTmp", item.getBigDecimal("alternativeQuantity"));
       	    }
       	    listData.add(tmpMap);
        }
       	List<GenericValue> deliveryItems = null;
       	try {
            deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(condList, EntityJoinOperator.OR), null, null, null, false);
        } catch (GenericEntityException e1) {
            String errMsg = "Fatal error calling getListOrderItemDelivery service: " + e1.toString();
            Debug.logError(e1, errMsg, module);
            return ServiceUtil.returnError(e1.getStackTrace().toString());
        } 
       	// 3. Create return data Map 
       	if(deliveryItems != null){
       	    String strOrderItemSeqId = "";
           	for(GenericValue item: deliveryItems){
           	    strOrderItemSeqId = item.getString("fromOrderItemSeqId");
           	    // FIXME Convert with UOM
           	    for(int i = 0; i < listData.size(); i++){
           	        if(listData.get(i).get("orderItemSeqId").equals(strOrderItemSeqId)){
           	            BigDecimal tmpBD = (BigDecimal)listData.get(i).get("createdQuantity");
           	            tmpBD = tmpBD.add(item.getBigDecimal("quantity"));
           	            BigDecimal tmpRequired = (BigDecimal)listData.get(i).get("requiredQuantity");
           	            if(tmpRequired.compareTo(tmpBD) == 0){
           	                listData.remove(i);
           	                break;
           	            }
           	            listData.get(i).put("createdQuantity", tmpBD);
           	            break;
           	        }
           	    }
           	}
       	}
       	if (!listData.isEmpty()){
       		for (HashMap<String, Object> item : listData){
		        if (item.containsKey("expireDate") && item.get("expireDate") == null && facilityId != null){
		        	if (facilityId != null){
		   	    		List<String> orderBy = new ArrayList<String>();
		   	    		orderBy.add("-expireDate");
		   	    		try {
							List<GenericValue> listInvs = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("productId", item.get("productId"), "facilityId", facilityId)), null, orderBy, null, false);
							if (!listInvs.isEmpty()){
								for (GenericValue inv : listInvs){
									if (inv.getBigDecimal("availableToPromiseTotal").compareTo((BigDecimal)item.get("requiredQuantity")) != -1){
										item.put("expireDate", inv.getString("expireDate"));
										break;
									}
								}
							}
						} catch (GenericEntityException e) {
							ServiceUtil.returnError("get InventoryItem error!");
						}
		   	    	}
		        }
       		}
       	}
       	/* Commented for new purpose: We can change Facility, not only show reserved products.
       	try {
       		deliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition("fromOrderId", orderId), null, null, null, false);
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling getListOrderItemDelivery service: " + e1.toString();
            Debug.logError(e1, errMsg, module);
            return ServiceUtil.returnError(e1.getStackTrace().toString());
		} 
       	Set<String> orderItemSeqIdSet = FastSet.newInstance();
       	for(GenericValue item: deliveryItems){
       		orderItemSeqIdSet.add(item.getString("fromOrderItemSeqId"));
       	}
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
       		String facilityId = parameters.get("facilityId")[0];
       		mapCondition.put("facilityId", facilityId);
       	}
       	mapCondition.put("orderId", orderId);
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
       	if(!UtilValidate.isEmpty(orderItemSeqIdSet)){
       		EntityCondition orderItemSeqCond = EntityCondition.makeCondition("orderItemSeqId", EntityComparisonOperator.NOT_IN, orderItemSeqIdSet);
       		listAllConditions.add(orderItemSeqCond);
       	}
       	listAllConditions.add(tmpConditon);
       	List<GenericValue> listOrderItems = new ArrayList<GenericValue>();
       	try {
       		listOrderItems = delegator.findList("OrderItemShipGroupResFacilityView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND),null, listSortFields, opts, false);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling getListOrderItemDelivery service: " + e.toString();
   			Debug.logError(e, errMsg, module);
   			return ServiceUtil.returnError(e.getStackTrace().toString());
   		}
   		*/
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	successResult.put("listIterator", listData);
       	return successResult;
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
    		listIterator = delegator.find("DeliveryItemView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListDeliveryItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listIterator);
    	return successResult;
       }
}
