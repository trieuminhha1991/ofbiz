package com.olbius.logistics.receive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class ReceiveServices {
	
	public static final String module = ReceiveServices.class.getName();
	public static final String resource = "DelysLogisticsUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getPurchaseDeliverys(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listReceipts = new ArrayList<GenericValue>();
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String orderId = null;
    	String deliveryTypeId = null;
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
    	
    	try {
    		listReceipts = delegator.findList("Delivery", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getPurchaseDeliverys service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listReceipts);
    	return successResult;
    }
	
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getJQPurchaseOrders(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listOrders = new ArrayList<GenericValue>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	
    	String orderStatusId = null;
    	if (parameters.get("orderStatusId") != null && parameters.get("orderStatusId").length > 0){
    		orderStatusId = (String)parameters.get("orderStatusId")[0];
    	}
    	if (orderStatusId != null && !("").equals(orderStatusId)){
    		Map<String, String> mapCondition = new HashMap<String, String>();
    		mapCondition.put("orderStatusId", orderStatusId);
    		EntityCondition geoCondition = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(geoCondition);
    	}
    	
    	String reqStatusId = null;
    	if (parameters.get("reqStatusId") != null && parameters.get("reqStatusId").length > 0){
    		reqStatusId = (String)parameters.get("reqStatusId")[0];
    	}
    	
    	String countryGeoId = null;
    	if (parameters.get("countryGeoId") != null && parameters.get("countryGeoId").length > 0){
    		countryGeoId = (String)parameters.get("countryGeoId")[0];
    	}
    	if (countryGeoId != null && !("").equals(countryGeoId)){
    		Map<String, String> mapCondition = new HashMap<String, String>();
    		mapCondition.put("countryGeoId", countryGeoId);
    		EntityCondition geoCondition = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(geoCondition);
    	} else {
    		EntityCondition geoCondition = EntityCondition.makeCondition("countryGeoId",EntityJoinOperator.NOT_IN, UtilMisc.toList("VNM"));
    		listAllConditions.add(geoCondition);
    	}
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("orderTypeId", "PURCHASE_ORDER");
    	mapCondition.put("roleTypeId", "BILL_FROM_VENDOR");
    	mapCondition.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	
    	try {
    		listIterator = delegator.find("PurchaseOrderDetail", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listOrders = listIterator.getCompleteList();
    		listIterator.close();
    		List<GenericValue> listPO = new ArrayList<GenericValue>();
    		if (countryGeoId != null && !("").equals(countryGeoId)){
    			listPO = delegator.findList("PartyOrderAddressPurpose", EntityCondition.makeCondition(UtilMisc.toMap("orderTypeId", "PURCHASE_ORDER", "roleTypeId", "BILL_FROM_VENDOR", "contactMechPurposeTypeId", "PRIMARY_LOCATION", "countryGeoId", countryGeoId)), null, null, null, false);
    		} else {
    			List<EntityCondition> listCond = new ArrayList<EntityCondition>();
				EntityCondition geoCondition = EntityCondition.makeCondition("countryGeoId",EntityJoinOperator.NOT_IN, UtilMisc.toList("VNM"));
				listCond.add(geoCondition);
				mapCondition = new HashMap<String, String>();
		    	mapCondition.put("orderTypeId", "PURCHASE_ORDER");
		    	mapCondition.put("roleTypeId", "BILL_FROM_VENDOR");
		    	mapCondition.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
		    	EntityCondition conditon = EntityCondition.makeCondition(mapCondition);
		    	listCond.add(conditon);
		    	listPO = delegator.findList("PartyOrderAddressPurpose", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, null, false);
    		}
    		List<GenericValue> listTmp = new ArrayList<GenericValue>();
    		List<GenericValue> listAllOrders = delegator.findList("PurchaseOrderDetail", null, null, null, null, false);
    		if (listOrders.isEmpty()){
    			if (listAllOrders.isEmpty()){
    				if (!listPO.isEmpty()){
        				listTmp.addAll(listPO);
        			}
    			}
    		} else {
	    		if (!listPO.isEmpty() && !listAllOrders.isEmpty()){
	    			for (GenericValue order1 : listPO){
	    				Boolean test = true;
	    				for (GenericValue order2 : listAllOrders){
	    					if (((String)order1.get("orderId")).equals((String)order2.get("orderId"))){
	    						test = false;
	    						break;
	    					}
	    				}
	    				if (test){
	    					listTmp.add(order1);
	    				}
	    			}
	    		}
    		}
    		if (!listTmp.isEmpty()){
    			for (GenericValue orderTmp : listTmp){
    				GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", (String)orderTmp.get("orderId")));
    				if (order != null){
    					listOrders.add(order);
    				}
    			}
    		}
    		if (reqStatusId != null && !("").equals(reqStatusId)){
    			List<GenericValue> listOrderTemps = new ArrayList<GenericValue>();
    			for (GenericValue orderTmp : listOrders){
					List<GenericValue> orderReq = delegator.findList("OrderRequirement", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)orderTmp.get("orderId"), "statusId", reqStatusId)), null, null, null, false);
					if (!orderReq.isEmpty()){
						listOrderTemps.add(orderTmp);
					}
    			}
    			listOrders = new ArrayList<GenericValue>();
    			listOrders.addAll(listOrderTemps);
        	}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getPurchaseImportOrders service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listOrders);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getOrderDetailToReceive(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listReceipts = new ArrayList<GenericValue>();
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String orderId = null;
    	if (parameters.get("orderId") != null && parameters.get("orderId").length > 0){
    		orderId = (String)parameters.get("orderId")[0];
    	}
    	if (orderId != null && !"".equals(orderId)){
    		mapCondition.put("orderId", orderId);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
        	try {
        		listReceipts = delegator.findList("OrderItemAndProduct", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		} catch (GenericEntityException e) {
    			return ServiceUtil.returnError("OrderItem not found");
    		}
    	} else {
    		return ServiceUtil.returnError("OrderId not found!");
    	}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listReceipts);
    	return successResult;
    }
}