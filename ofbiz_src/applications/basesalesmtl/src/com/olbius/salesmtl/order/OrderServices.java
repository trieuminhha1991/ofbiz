package com.olbius.salesmtl.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.order.OrderWorker;
import com.olbius.basesales.product.ProductStoreWorker;
import com.olbius.basesales.util.ProcessConditionUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

public class OrderServices {
	public static final String module = OrderServices.class.getName();
	
	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> jqGetListOrderNeedDelivery(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//EntityListIterator listIterator = null;
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
    		boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, "VIEW", "ENTITY", "SALESORDER");
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            //return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			String userLoginPartyId = userLogin.getString("partyId");
			
			List<String> productStoreIdsSeller = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreSell(delegator, userLogin, userLoginPartyId, true), "productStoreId", true);
			
			if (UtilValidate.isNotEmpty(productStoreIdsSeller)) {
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIdsSeller));
				listAllConditions.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("statusId", "ORDER_SADAPPROVED"), EntityOperator.OR, 
						EntityCondition.makeCondition("statusId", "ORDER_APPROVED")));
				listAllConditions.add(EntityCondition.makeCondition
						(EntityCondition.makeCondition("requirementStatusId", null), EntityOperator.OR,
						EntityCondition.makeCondition("requirementStatusId", "REQ_CANCELLED")));
				
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-orderDate");
					listSortFields.add("priority");
				}
				
				listAllConditions = ProcessConditionUtil.processOrderCondition(listAllConditions);
				listSortFields = ProcessConditionUtil.processOrderSort(listSortFields);
				opts.setDistinct(true);
				List<GenericValue> listOrder = delegator.findList("OrderHeaderNeedDelivery", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
				if (UtilValidate.isNotEmpty(listOrder)) {
					for (GenericValue order : listOrder) {
						Map<String, Object> orderMap = order.getAllFields();
						BigDecimal totalWeight = OrderWorker.getTotalWeightProduct(delegator, dispatcher, order.getString("orderId"));
						orderMap.put("totalWeight", totalWeight);
						listIterator.add(orderMap);
					}
				}
			}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderNeedDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	

	@SuppressWarnings({ "unchecked", "unused" })
    public static Map<String, Object> getListOrderNeedDelivery(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
    	
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		
    	try {
    		//check permission for each order type
    		boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, "VIEW", "ENTITY", "SALESORDER");
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            //return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			String userLoginPartyId = userLogin.getString("partyId");
			
			List<String> productStoreIdsSeller = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreSell(delegator, userLogin, userLoginPartyId, true), "productStoreId", true);
			
			if (UtilValidate.isNotEmpty(productStoreIdsSeller)) {
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIdsSeller));
				listAllConditions.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("statusId", "ORDER_SADAPPROVED"), EntityOperator.OR, 
						EntityCondition.makeCondition("statusId", "ORDER_APPROVED")));
				listAllConditions.add(EntityCondition.makeCondition
						(EntityCondition.makeCondition("requirementStatusId", null), EntityOperator.OR,
						EntityCondition.makeCondition("requirementStatusId", "REQ_CANCELLED")));
				
				listAllConditions = ProcessConditionUtil.processOrderCondition(listAllConditions);

				List<GenericValue> listOrder = delegator.findList("OrderHeaderDelivery", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isNotEmpty(listOrder)) {
					for (GenericValue order : listOrder) {
						Map<String, Object> orderMap = order.getAllFields();
						BigDecimal totalWeight = OrderWorker.getTotalWeightProduct(delegator, dispatcher, order.getString("orderId"));
						orderMap.put("totalWeight", totalWeight);
						listIterator.add(orderMap);
					}
				}
			}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderNeedDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
