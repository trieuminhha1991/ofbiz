package com.olbius.basesales.returnorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ReturnServices {
	public static final String module = ReturnServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListReturnItem(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	//List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	
    	String returnId = null;
    	try {
    		// get list distributor
        	if (parameters.containsKey("returnId") && UtilValidate.isNotEmpty(parameters.get("returnId"))) {
        		returnId = parameters.get("returnId")[0];
        	}
        	if (returnId != null) {
        		listAllConditions.add(EntityCondition.makeCondition("returnId", returnId));
        		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
        		listIterator = delegator.find("ReturnItemDetail", tmpConditon, null, null, listSortFields, opts);
        	}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListReturnItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createReturnHeaderItemAdjustmentCustomer(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Security security = ctx.getSecurity();
        OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        if (!(securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "RETURN_ORDER_NEW") || securityOlb.olbiusHasPermission(userLogin, "CREATE", "ENTITY", "DIS_RETURNORDER")|| securityOlb.olbiusHasPermission(userLogin, "CREATE", "ENTITY", "SALESMAN_RETURNORDER"))) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
        }
		
		// process data
    	List<Object> productListParam = (List<Object>) context.get("listProduct");
    	boolean isJson = false;
    	if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
    		if (productListParam.get(0) instanceof String) isJson = true;
    	}
		List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
    	if (isJson){
			String productListStr = "[" + (String) productListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject prodItem = jsonArray.getJSONObject(i);
					Map<String, Object> productItem = FastMap.newInstance();
					if (prodItem.containsKey("returnItemTypeId")) productItem.put("returnItemTypeId", prodItem.getString("returnItemTypeId"));
					if (prodItem.containsKey("orderId")) productItem.put("orderId", prodItem.getString("orderId"));
					if (prodItem.containsKey("orderItemSeqId")) productItem.put("orderItemSeqId", prodItem.getString("orderItemSeqId"));
					if (prodItem.containsKey("description")) productItem.put("description", prodItem.getString("description"));
					if (prodItem.containsKey("productId")) productItem.put("productId", prodItem.getString("productId"));
					if (prodItem.containsKey("returnQuantity")) productItem.put("returnQuantity", prodItem.getString("returnQuantity"));
					
					if (productItem.containsKey("orderId") && UtilValidate.isNotEmpty(productItem.get("orderId"))) { // Accounting process fail!!!
					try {
						GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", prodItem.getString("orderId"), "orderItemSeqId", prodItem.getString("orderItemSeqId")));
						productItem.put("returnAmount", orderItem.getBigDecimal("selectedAmount"));
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					
					if (prodItem.containsKey("returnPrice")) productItem.put("returnPrice", prodItem.getString("returnPrice"));
					if (prodItem.containsKey("returnReasonId")) productItem.put("returnReasonId", prodItem.getString("returnReasonId"));
					if (prodItem.containsKey("returnTypeId")) productItem.put("returnTypeId", prodItem.getString("returnTypeId"));
					if (prodItem.containsKey("expectedItemStatus")) productItem.put("expectedItemStatus", prodItem.getString("expectedItemStatus"));
					
					if (prodItem.containsKey("returnAdjustmentTypeId")) productItem.put("returnAdjustmentTypeId", prodItem.getString("returnAdjustmentTypeId"));
					if (prodItem.containsKey("returnItemSeqId")) productItem.put("returnItemSeqId", prodItem.getString("returnItemSeqId"));
					if (prodItem.containsKey("orderAdjustmentId")) productItem.put("orderAdjustmentId", prodItem.getString("orderAdjustmentId"));
					if (prodItem.containsKey("comments")) productItem.put("comments", prodItem.getString("comments"));
					if (prodItem.containsKey("amount")) productItem.put("amount", prodItem.getString("amount"));
					
					listProduct.add(productItem);
				}
			}
    	} else {
    		listProduct = (List<Map<String, Object>>) context.get("productList");
    	}
    	if (UtilValidate.isEmpty(listProduct)) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuantityMustBeGreaterThanZero", locale));
		
    	// Get parameter information general
        String orderId = (String) context.get("orderId");
        String returnId = (String) context.get("returnId");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        List<String> errMsgList = new ArrayList<String>();
        
        String returnIdSuccess = returnId;
        try {
        	List<Map<String, Object>> tobeStored = new LinkedList<Map<String, Object>>();
            for (Map<String, Object> productItem : listProduct) {
            	// process return item
    	        BigDecimal amount = null;
    	        String lotId = null;
    	        String comments = null;
    	        String correspondingProductId = null;
    	        String customerReferenceId = null;
    	        Timestamp datetimeManufactured = null;
    	        String description = null;
    	        BigDecimal exemptAmount = null;
    	        String expectedItemStatus = null;
    	        Timestamp expiredDate = null;
    	        String includeInShipping = null;
    	        String includeInTax = null;
    	        Timestamp createdDate = nowTimestamp;
    	        String createdByUserLogin = userLogin.getString("userLoginId");
    	        Timestamp lastModifiedDate = nowTimestamp;
    	        String lastModifiedByUserLogin = userLogin.getString("userLoginId");
    	        // locale
    	        // userLogin
    	        String orderAdjustmentId = null;
    	        // orderId
    	        String orderItemSeqId = null;
    	        String overrideGlAccountId = null;
    	        String primaryGeoId = null;
    	        String productFeatureId = null;
    	        String productId = null;
    	        String quantityUomId = null;
    	        String productPromoId, productPromoRuleId, productPromoActionSeqId;
    	        BigDecimal receivedQuantity = null;
    	        String returnAdjustmentId, returnAdjustmentTypeId;
    	        // returnId
    	        String returnItemResponseId = null;
    	        String returnItemSeqId = null;
    	        String returnItemTypeId = null;
    	        BigDecimal returnPrice = null;
    	        BigDecimal returnQuantity = null;
    	        String returnReasonId = null;
    	        String returnTypeId = null;
    	        String secondaryGeoId = null;
    	        String shipGroupSeqId = null;
    	        BigDecimal sourcePercentage = null;
    	        BigDecimal returnAmount = null;
    	        String sourceReferenceId = null;
    	        String statusId = null;
    	        String taxAuthGeoId, taxAuthPartyId, taxAuthorityRateSeqId;
    	        
    	        productPromoId = productPromoRuleId = productPromoActionSeqId = null;
    	        returnAdjustmentId = returnAdjustmentTypeId = null;
    	        taxAuthGeoId = taxAuthPartyId = taxAuthorityRateSeqId = null;
    	        
    	        if (productItem.containsKey("amount")) {
    	        	String amountStr = (String) productItem.get("amount");
    	        	if (UtilValidate.isNotEmpty(amountStr)) {
	                	// parse the quantity
	                    try {
	                    	amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
	                    } catch (Exception e) {
	                        Debug.logWarning(e, "Problems parsing quantity string: " + amountStr, module);
	                        amount = BigDecimal.ZERO;
	                    }
	                }
                }
    	        if (productItem.containsKey("returnAmount")) returnAmount = (BigDecimal) productItem.get("returnAmount");
    	        if (productItem.containsKey("lotId")) lotId = (String) productItem.get("lotId");
    	        if (productItem.containsKey("description")) description = (String) productItem.get("description");
    	        if (productItem.containsKey("comments")) comments = (String) productItem.get("comments");
    	        if (productItem.containsKey("correspondingProductId")) correspondingProductId = (String) productItem.get("correspondingProductId");
    	        if (productItem.containsKey("customerReferenceId")) customerReferenceId = (String) productItem.get("customerReferenceId");
    	        if (productItem.containsKey("datetimeManufactured")) {
    	        	String datetimeManufacturedStr = (String) productItem.get("datetimeManufactured");
    	        	datetimeManufactured = new Timestamp(Long.parseLong(datetimeManufacturedStr));
    	        }
    	        //if (productItem.containsKey("description")) description = (String) productItem.get("description");
    	        if (productItem.containsKey("exemptAmount")) {
    	        	String exemptAmountStr = (String) productItem.get("exemptAmount");
    	        	if (UtilValidate.isNotEmpty(exemptAmountStr)) {
	                	// parse the quantity
	                    try {
	                    	exemptAmount = (BigDecimal) ObjectType.simpleTypeConvert(exemptAmountStr, "BigDecimal", null, locale);
	                    } catch (Exception e) {
	                        Debug.logWarning(e, "Problems parsing quantity string: " + exemptAmountStr, module);
	                        exemptAmount = BigDecimal.ZERO;
	                    }
	                }
                }
    	        if (productItem.containsKey("expectedItemStatus")) expectedItemStatus = (String) productItem.get("expectedItemStatus");
    	        if (productItem.containsKey("expiredDate")) {
    	        	String expiredDateStr = (String) productItem.get("expiredDate");
    	        	expiredDate = new Timestamp(Long.parseLong(expiredDateStr));
    	        }
    	        if (productItem.containsKey("includeInShipping")) includeInShipping = (String) productItem.get("includeInShipping");
    	        if (productItem.containsKey("includeInTax")) includeInTax = (String) productItem.get("includeInTax");
    	        if (productItem.containsKey("orderAdjustmentId")) orderAdjustmentId = (String) productItem.get("orderAdjustmentId");
    	        if (productItem.containsKey("orderItemSeqId")) orderItemSeqId = (String) productItem.get("orderItemSeqId");
    	        if (productItem.containsKey("overrideGlAccountId")) overrideGlAccountId = (String) productItem.get("overrideGlAccountId");
    	        if (productItem.containsKey("productFeatureId")) productFeatureId = (String) productItem.get("productFeatureId");
    	        if (productItem.containsKey("productId")) productId = (String) productItem.get("productId");
    	        if (productItem.containsKey("productPromoActionSeqId")) productPromoActionSeqId = (String) productItem.get("productPromoActionSeqId");
    	        if (productItem.containsKey("productPromoId")) productPromoId = (String) productItem.get("productPromoId");
    	        if (productItem.containsKey("productPromoRuleId")) productPromoRuleId = (String) productItem.get("productPromoRuleId");
    	        if (productItem.containsKey("quantityUomId")) quantityUomId = (String) productItem.get("quantityUomId");
    	        if (UtilValidate.isEmpty(quantityUomId)) {
    	        	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    	        	if (product != null) quantityUomId = product.getString("quantityUomId");
    	        }
    	        if (productItem.containsKey("receivedQuantity")) {
    	        	String receivedQuantityStr = (String) productItem.get("receivedQuantity");
    	        	if (UtilValidate.isNotEmpty(receivedQuantityStr)) {
	                	// parse the quantity
	                    try {
	                    	receivedQuantity = (BigDecimal) ObjectType.simpleTypeConvert(receivedQuantityStr, "BigDecimal", null, locale);
	                    } catch (Exception e) {
	                        Debug.logWarning(e, "Problems parsing quantity string: " + receivedQuantityStr, module);
	                        receivedQuantity = BigDecimal.ZERO;
	                    }
	                }
                }
    	        if (productItem.containsKey("returnAdjustmentId")) returnAdjustmentId = (String) productItem.get("returnAdjustmentId");
    	        if (productItem.containsKey("returnAdjustmentTypeId")) returnAdjustmentTypeId = (String) productItem.get("returnAdjustmentTypeId");
    	        if (productItem.containsKey("returnItemResponseId")) returnItemResponseId = (String) productItem.get("returnItemResponseId");
    	        if (productItem.containsKey("returnItemSeqId")) returnItemSeqId = (String) productItem.get("returnItemSeqId");
    	        if (productItem.containsKey("returnItemTypeId")) returnItemTypeId = (String) productItem.get("returnItemTypeId");
    	        if (productItem.containsKey("returnPrice")) {
    	        	String returnPriceStr = (String) productItem.get("returnPrice");
    	        	if (UtilValidate.isNotEmpty(returnPriceStr)) {
	                	// parse the quantity
	                    try {
//	                    	returnPrice = (BigDecimal) ObjectType.simpleTypeConvert(returnPriceStr, "BigDecimal", null, locale);
							returnPrice = new BigDecimal(returnPriceStr);
	                    } catch (Exception e) {
	                        Debug.logWarning(e, "Problems parsing quantity string: " + returnPriceStr, module);
	                        returnPrice = BigDecimal.ZERO;
	                    }
	                }
                }
    	        if (productItem.containsKey("returnQuantity")) {
    	        	String returnQuantityStr = (String) productItem.get("returnQuantity");
    	        	if (UtilValidate.isNotEmpty(returnQuantityStr)) {
    	        		// parse the quantity
    	        		try {
    	        			returnQuantity = (BigDecimal) ObjectType.simpleTypeConvert(returnQuantityStr, "BigDecimal", null, locale);
    	        		} catch (Exception e) {
    	        			Debug.logWarning(e, "Problems parsing quantity string: " + returnQuantityStr, module);
    	        			returnQuantity = BigDecimal.ZERO;
    	        		}
    	        	}
    	        }
    	        if (productItem.containsKey("returnReasonId")) returnReasonId = (String) productItem.get("returnReasonId");
    	        if (productItem.containsKey("returnTypeId")) returnTypeId = (String) productItem.get("returnTypeId");
    	        if (productItem.containsKey("secondaryGeoId")) secondaryGeoId = (String) productItem.get("secondaryGeoId");
    	        if (productItem.containsKey("shipGroupSeqId")) shipGroupSeqId = (String) productItem.get("shipGroupSeqId");
    	        if (productItem.containsKey("sourcePercentage")) {
    	        	String sourcePercentageStr = (String) productItem.get("sourcePercentage");
    	        	if (UtilValidate.isNotEmpty(sourcePercentageStr)) {
    	        		// parse the quantity
    	        		try {
    	        			sourcePercentage = (BigDecimal) ObjectType.simpleTypeConvert(sourcePercentageStr, "BigDecimal", null, locale);
    	        		} catch (Exception e) {
    	        			Debug.logWarning(e, "Problems parsing quantity string: " + sourcePercentageStr, module);
    	        			sourcePercentage = BigDecimal.ZERO;
    	        		}
    	        	}
    	        }
    	        if (productItem.containsKey("sourceReferenceId")) sourceReferenceId = (String) productItem.get("sourceReferenceId");
    	        if (productItem.containsKey("statusId")) statusId = (String) productItem.get("statusId");
    	        if (productItem.containsKey("taxAuthGeoId")) taxAuthGeoId = (String) productItem.get("taxAuthGeoId");
    	        if (productItem.containsKey("taxAuthPartyId")) taxAuthPartyId = (String) productItem.get("taxAuthPartyId");
    	        if (productItem.containsKey("taxAuthorityRateSeqId")) taxAuthorityRateSeqId = (String) productItem.get("taxAuthorityRateSeqId");
    	        
    	        if (orderId != null && orderItemSeqId != null && returnQuantity != null && BigDecimal.ZERO.compareTo(returnQuantity) < 0) {
    	        	GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
    	        	if (orderItem != null) {
    	        		description = orderItem.getString("itemDescription");
    	        		
    	        		Map<String, Object> contextMapReturnItem = UtilMisc.<String, Object>toMap(
				    	        		"amount", amount, "lotId", lotId, "comments", comments, "correspondingProductId", correspondingProductId,
				    	        		"createdByUserLogin", createdByUserLogin, "createdDate", createdDate,
				    	        		"customerReferenceId", customerReferenceId, "datetimeManufactured", datetimeManufactured,
				    	        		"description", description, "exemptAmount", exemptAmount,
				    	        		"expectedItemStatus", expectedItemStatus, "expiredDate", expiredDate,
				    	        		"includeInShipping", includeInShipping, "includeInTax", includeInTax,
				    	        		"lastModifiedByUserLogin", lastModifiedByUserLogin, "lastModifiedDate", lastModifiedDate, 
				    	        		"locale", locale, "userLogin", userLogin,
				    	        		"orderAdjustmentId", orderAdjustmentId, "orderId", orderId,
				    	        		"orderItemSeqId", orderItemSeqId, "overrideGlAccountId", overrideGlAccountId,
				    	        		"primaryGeoId", primaryGeoId, "productFeatureId", productFeatureId,
				    	        		"productId", productId, "productPromoActionSeqId", productPromoActionSeqId,
				    	        		"productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId,
				    	        		"quantityUomId", quantityUomId, "receivedQuantity", receivedQuantity,
				    	        		"returnAdjustmentId", returnAdjustmentId, "returnAdjustmentTypeId", returnAdjustmentTypeId,
				    	        		"returnId", returnIdSuccess, "returnItemResponseId", returnItemResponseId,
				    	        		"returnItemSeqId", returnItemSeqId, "returnItemTypeId", returnItemTypeId,
				    	        		"returnPrice", returnPrice, "returnQuantity", returnQuantity,
				    	        		"returnReasonId", returnReasonId, "returnTypeId", returnTypeId,
				    	        		"secondaryGeoId", secondaryGeoId, "shipGroupSeqId", shipGroupSeqId,
				    	        		"sourcePercentage", sourcePercentage, "sourceReferenceId", sourceReferenceId,
				    	        		"returnAmount", returnAmount,
				    	        		"statusId", statusId, "taxAuthGeoId", taxAuthGeoId, "taxAuthPartyId", taxAuthPartyId, "taxAuthorityRateSeqId", taxAuthorityRateSeqId
		    	        		);
		    	        tobeStored.add(contextMapReturnItem);
		    	        
		    	        // Order item adjustments
                        List<GenericValue> itemAdjustments = null;
                        try {
                            itemAdjustments = orderItem.getRelated("OrderAdjustment", null, null, false);
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderErrorUnableToGetOrderAdjustmentsFromItem", locale));
                        }
                        List<GenericValue> returnItemTypeMap = delegator.findByAnd("ReturnItemTypeMap", UtilMisc.toMap("returnHeaderTypeId", "CUSTOMER_RETURN"), null, false);
                        if (UtilValidate.isNotEmpty(itemAdjustments)) {
                            for (GenericValue itemAdjustment : itemAdjustments) {
                            	/*Map<String, Object> returnInfo = FastMap.newInstance();
                                returnInfo.put("returnableQuantity", BigDecimal.ONE);
                                returnInfo.put("returnablePrice", itemAdjustment.get("amount"));
                                returnInfo.put("itemTypeKey", itemTypeKey);*/
                                GenericValue returnItemTypeItem = EntityUtil.getFirst(EntityUtil.filterByAnd(returnItemTypeMap, UtilMisc.toMap("returnItemMapKey", itemAdjustment.get("orderAdjustmentTypeId"))));
                                returnAdjustmentTypeId = returnItemTypeItem.getString("returnItemTypeId");
                                orderAdjustmentId = itemAdjustment.getString("orderAdjustmentId");
                                returnTypeId = "RTN_REFUND";
                                amount = itemAdjustment.getBigDecimal("amount");
                                
                                description = null;
                                GenericValue adjustmentType = itemAdjustment.getRelatedOne("OrderAdjustmentType", false);
                                if (UtilValidate.isNotEmpty(itemAdjustment.get("description"))) {
                                	description = itemAdjustment.getString("description");
                                } else if (adjustmentType != null) {
                                	description = (String) adjustmentType.get("description", locale);
                                }
                                
                                contextMapReturnItem = UtilMisc.<String, Object>toMap(
						    	        		"amount", amount, 
						    	        		"description", description, 
						    	        		"createdByUserLogin", createdByUserLogin, "createdDate", createdDate,
						    	        		"lastModifiedByUserLogin", lastModifiedByUserLogin, "lastModifiedDate", lastModifiedDate, 
						    	        		"locale", locale, "userLogin", userLogin,
						    	        		"orderAdjustmentId", orderAdjustmentId, 
						    	        		"returnAdjustmentTypeId", returnAdjustmentTypeId,
						    	        		"returnId", returnIdSuccess, "returnTypeId", returnTypeId
				    	        		);
				    	        tobeStored.add(contextMapReturnItem);
                            }
                        }
    	        	}
    	        } else if (UtilValidate.isEmpty(orderItemSeqId)) {
    	        	Map<String, Object> contextMapReturn = UtilMisc.<String, Object>toMap(
    	        					"returnItemTypeId", returnItemTypeId,
    	        					"returnItemSeqId", returnItemSeqId,
    	        					"returnTypeId", returnTypeId,
    	        					"amount", amount,
    	        					"description", description
    	        			);
    	        	tobeStored.add(contextMapReturn);
    	        }
            }
            
            // finish prepare process data, create return header and return items
            if (UtilValidate.isNotEmpty(tobeStored)) {
            	if (UtilValidate.isNotEmpty(orderId)) {
            		if (UtilValidate.isEmpty(returnId)) {
            			GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
            			if (returnHeader == null) {
            				// create return header
            				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            				List<GenericValue> orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId), null, false);
                    		String fromPartyId = null;
                    		String toPartyId = null;
                    		String originFacilityId = null;
                    		if (UtilValidate.isNotEmpty(orderRoles)) {
                    			List<GenericValue> listFromPartyId = EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId", "PLACING_CUSTOMER"));
                    			if (UtilValidate.isNotEmpty(listFromPartyId)) {
                    				fromPartyId = listFromPartyId.get(0).getString("partyId");
                    			}
                    			List<GenericValue> listToPartyId = EntityUtil.filterByAnd(orderRoles, UtilMisc.toMap("roleTypeId", "BILL_FROM_VENDOR"));
                    			if (UtilValidate.isNotEmpty(listToPartyId)) {
                    				toPartyId = listToPartyId.get(0).getString("partyId");
                    			}
                    		}
                    		if (orderHeader != null) {
                    			originFacilityId = orderHeader.getString("originFacilityId");
                    		}
                    		if (toPartyId == null || fromPartyId == null) {
                    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"From party and to party must not empty", locale));
                    		}
                    		Map<String, Object> contextMap =  UtilMisc.<String, Object>toMap(
                    						"returnHeaderTypeId", "CUSTOMER_RETURN", "statusId", "RETURN_REQUESTED",
                    						"fromPartyId", fromPartyId, "toPartyId", toPartyId,
                    						"paymentMethodId", null, "finAccountId", null,
                    						"billingAccountId", null, "entryDate", nowTimestamp,
                    						"originContactMechId", null, "destinationFacilityId", originFacilityId,
                    						"needsInventoryReceive", "Y", "currencyUomId", SalesUtil.getCurrentCurrencyUom(delegator),
                    						"supplierRmaId", null, "userLogin", userLogin, "locale", locale
                    				);
                        	Map<String, Object> result0 = dispatcher.runSync("createReturnHeader", contextMap);
                        	
                        	if (ServiceUtil.isError(result0)) {
                        		// no values for price and paramMap (a context for adding attributes)
                        		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result0));
                        	}
                            
                            returnIdSuccess = (String) result0.get("returnId");
            			}
            		}
            	}
            
            	String returnAdjustmentId = null;
            	//String returnItemSeqId = null;
            	for (Map<String, Object> contextMapReturnItem : tobeStored) {
            		String returnItemSeqId = null;
            		contextMapReturnItem.put("returnId", returnIdSuccess);
            		contextMapReturnItem.put("returnAdjustmentId", returnAdjustmentId);
            		contextMapReturnItem.put("returnItemSeqId", returnItemSeqId);
            		Map<String, Object> result0 = dispatcher.runSync("createReturnItemOrAdjustment", contextMapReturnItem);
            		if (ServiceUtil.isError(result0)) {
                		// no values for price and paramMap (a context for adding attributes)
                		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result0));
                	}
            		
                    returnAdjustmentId = (String) result0.get("returnAdjustmentId");
                    returnItemSeqId = (String) result0.get("returnItemSeqId");
            	}
	        } else {
	        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
	        }
        } catch (Exception e) {
        	Debug.logError(e, module);
            errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
        } catch (Throwable t) {
            Debug.logError(t, module);
            errMsgList.add(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
        } finally {
        	if (UtilValidate.isNotEmpty(errMsgList)) {
        		return ServiceUtil.returnError(errMsgList);
            }
        /*	
        	try {
				NotificationWorker.sendNotifyWhenCreateReturnOrder(delegator, dispatcher, locale, orderId, returnIdSuccess, userLogin);
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error when create notification: " + e.toString();
				Debug.logWarning(e, errMsg, module);
			} catch (GenericServiceException e) {
				String errMsg = "Fatal error when create notification: " + e.toString();
				Debug.logWarning(e, errMsg, module);
			}
		*/
        }
    	
        successResult.put("returnId", returnIdSuccess);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createReturnWithoutOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Security security = ctx.getSecurity();
        OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        if (!(securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "RETURN_ORDER_NEW"))) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
        }

		// process data
    	List<Object> productListParam = (List<Object>) context.get("listProduct");
    	boolean isJson = false;
    	if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
    		if (productListParam.get(0) instanceof String) isJson = true;
    	}
		List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
    	if (isJson){
			String productListStr = "[" + (String) productListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject prodItem = jsonArray.getJSONObject(i);
					Map<String, Object> productItem = FastMap.newInstance();
					if (prodItem.containsKey("productId")) productItem.put("productId", prodItem.getString("productId"));
					if (prodItem.containsKey("quantityUomId")) productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
					if (prodItem.containsKey("description")) productItem.put("description", prodItem.getString("description"));
					if (prodItem.containsKey("returnQuantity")) productItem.put("returnQuantity", prodItem.getString("returnQuantity"));
					if (prodItem.containsKey("returnPrice")) productItem.put("returnPrice", prodItem.getString("returnPrice"));
					if (prodItem.containsKey("returnReasonId")) productItem.put("returnReasonId", prodItem.getString("returnReasonId"));
					//if (prodItem.containsKey("amount")) productItem.put("amount", prodItem.getString("amount"));
					// productItem.put("returnAmount", orderItem.getBigDecimal("selectedAmount"));
					
					listProduct.add(productItem);
				}
			}
    	} else {
    		listProduct = (List<Map<String, Object>>) context.get("listProduct");
    	}
    	if (UtilValidate.isEmpty(listProduct)) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuantityMustBeGreaterThanZero", locale));
		
    	// Get parameter information general
        String returnId = (String) context.get("returnId");
        
        if (UtilValidate.isEmpty(returnId)) {
        	returnId = delegator.getNextSeqId("ReturnHeader");
        }
        
        try {
        	// create return header
    		String partyId = (String) context.get("customerId");
    		String description = (String) context.get("description");
    		String originContactMechId = (String) context.get("originContactMechId");
    		String destinationFacilityId = (String) context.get("destinationFacilityId");
    		String currencyUomId = (String) context.get("currencyUomId");
    		
    		Map<String, Object> createReturnHeaderMap = FastMap.newInstance();
    		createReturnHeaderMap.put("userLogin", userLogin);
    		createReturnHeaderMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
    		createReturnHeaderMap.put("statusId", "RETURN_REQUESTED");
    		createReturnHeaderMap.put("fromPartyId", partyId);
    		createReturnHeaderMap.put("originContactMechId", originContactMechId);
    		createReturnHeaderMap.put("destinationFacilityId", destinationFacilityId);
    		createReturnHeaderMap.put("needsInventoryReceive", "Y");
    		createReturnHeaderMap.put("currencyUomId", currencyUomId);
    		createReturnHeaderMap.put("description", description);
    		//createReturnHeaderMap.put("grandTotal", cart.getGranTotalReturn());
    		//createReturnHeaderMap.put("taxTotal", cart.getTotalSalesTax());
    		Map<String, Object> createReturnHeaderResult = dispatcher.runSync("createReturnHeader", createReturnHeaderMap);
    		if (ServiceUtil.isError(createReturnHeaderMap)) {
    			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createReturnHeaderResult));
    		}
    		returnId = (String) createReturnHeaderResult.get("returnId");
        	
    		// create return item
    		String returnTypeId = "RTN_REFUND";
    		String returnItemTypeId = "RET_FPROD_ITEM";
    		
            for (Map<String, Object> productItem : listProduct) {
            	String productId = (String) productItem.get("productId");
            	String quantityUomId = (String) productItem.get("quantityUomId");
            	String itemDescription = (String) productItem.get("description");
            	String returnReasonId = (String) productItem.get("returnReasonId");
            	BigDecimal returnPrice = null;
    	        BigDecimal returnQuantity = null;
    	        
    	        if (productItem.containsKey("returnPrice")) {
    	        	String returnPriceStr = (String) productItem.get("returnPrice");
    	        	if (UtilValidate.isNotEmpty(returnPriceStr)) {
	                	// parse the quantity
	                    try {
	                    	returnPrice = new BigDecimal(returnPriceStr); //(BigDecimal) ObjectType.simpleTypeConvert(returnPriceStr, "BigDecimal", null, locale);
	                    } catch (Exception e) {
	                        Debug.logWarning(e, "Problems parsing return price string: " + returnPriceStr, module);
	                        returnPrice = BigDecimal.ZERO;
	                    }
	                }
                }
    	        if (productItem.containsKey("returnQuantity")) {
    	        	String returnQuantityStr = (String) productItem.get("returnQuantity");
    	        	if (UtilValidate.isNotEmpty(returnQuantityStr)) {
    	        		// parse the quantity
    	        		try {
    	        			returnQuantity = new BigDecimal(returnQuantityStr); //(BigDecimal) ObjectType.simpleTypeConvert(returnQuantityStr, "BigDecimal", null, locale);
    	        		} catch (Exception e) {
    	        			Debug.logWarning(e, "Problems parsing return quantity string: " + returnQuantityStr, module);
    	        			returnQuantity = BigDecimal.ZERO;
    	        		}
    	        	}
    	        }
            	
            	Map<String, Object> createCartItemMap = FastMap.newInstance();
    			createCartItemMap.put("userLogin", userLogin);
    			createCartItemMap.put("returnId", returnId);
    			createCartItemMap.put("returnTypeId", returnTypeId);
    			createCartItemMap.put("returnItemTypeId", returnItemTypeId);
    			createCartItemMap.put("productId", productId);
				createCartItemMap.put("quantityUomId", quantityUomId);
    			createCartItemMap.put("returnQuantity", returnQuantity);
    			createCartItemMap.put("returnPrice", returnPrice);
    			createCartItemMap.put("returnReasonId", returnReasonId);
    			createCartItemMap.put("description", itemDescription);
    			
    			Map<String, Object> createReturnItemDirectly = dispatcher.runSync("createReturnItemDirectly", createCartItemMap);
    			if (ServiceUtil.isError(createReturnItemDirectly)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createReturnItemDirectly));
				}
    			
    			//create return adjustment
    			List<GenericValue> productTax = FastList.newInstance();
    			productTax = delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", productId), null, false);
    			
    			if (UtilValidate.isNotEmpty(productTax)){
    				BigDecimal taxPercent = productTax.get(0).getBigDecimal("taxPercentage");
    				if (taxPercent.compareTo(BigDecimal.ZERO) > 0){
    					Map<String, Object> createCartItemAdjMap = FastMap.newInstance(); 
    					createCartItemAdjMap.put("returnId", returnId);
    					createCartItemAdjMap.put("returnItemSeqId", createReturnItemDirectly.get("returnItemSeqId"));
    					createCartItemAdjMap.put("returnTypeId", returnTypeId);
    					createCartItemAdjMap.put("returnAdjustmentTypeId", "RET_SALES_TAX_ADJ");
    					createCartItemAdjMap.put("description", UtilProperties.getMessage("BasePosUiLabels", "BSReturnSalesTaxNoOrder", locale));
    					createCartItemAdjMap.put("shipGroupSeqId", "_NA_");
    					createCartItemAdjMap.put("createdDate", UtilDateTime.nowTimestamp());
    					createCartItemAdjMap.put("createdByUserLogin", userLogin.getString("userLoginId"));
    					createCartItemAdjMap.put("taxAuthorityRateSeqId", productTax.get(0).getString("taxAuthorityRateSeqId"));
    					createCartItemAdjMap.put("sourcePercentage", taxPercent);
    					createCartItemAdjMap.put("primaryGeoId", productTax.get(0).getString("originGeoId")); 
    					createCartItemAdjMap.put("taxAuthGeoId", productTax.get(0).getString("taxAuthGeoId"));
    					createCartItemAdjMap.put("taxAuthPartyId", productTax.get(0).getString("taxAuthPartyId"));
    					
    					BigDecimal taxAmount = returnQuantity.multiply(returnPrice.multiply(taxPercent).divide(new BigDecimal(100)));
    					createCartItemAdjMap.put("amount", taxAmount);
    					createCartItemAdjMap.put("userLogin", userLogin);
    					
    					Map<String, Object> createReturnAdjustmentResult = dispatcher.runSync("createReturnAdjustment", createCartItemAdjMap);
    					if (ServiceUtil.isError(createReturnAdjustmentResult)) {
    						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createReturnAdjustmentResult));
    					}
    				}
    			}
            }
        } catch (Exception e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
        }
    	
        successResult.put("returnId", returnId);
		return successResult;
	}
	public static Map<String, Object> createReturnWithoutOrderBySalesman(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		if (!(securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "SALESMAN_RETURNORDER_NEW"))) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission", locale));
		}

		// process data
		List<Object> productListParam = (List<Object>) context.get("listProduct");
		boolean isJson = false;
		if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
			if (productListParam.get(0) instanceof String) isJson = true;
		}
		List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
		if (isJson){
			String productListStr = "[" + (String) productListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject prodItem = jsonArray.getJSONObject(i);
					Map<String, Object> productItem = FastMap.newInstance();
					if (prodItem.containsKey("productId")) productItem.put("productId", prodItem.getString("productId"));
					if (prodItem.containsKey("quantityUomId")) productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
					if (prodItem.containsKey("description")) productItem.put("description", prodItem.getString("description"));
					if (prodItem.containsKey("returnQuantity")) productItem.put("returnQuantity", prodItem.getString("returnQuantity"));
					if (prodItem.containsKey("returnPrice")) productItem.put("returnPrice", prodItem.getString("returnPrice"));
					//if (prodItem.containsKey("returnReasonId")) productItem.put("returnReasonId", prodItem.getString("returnReasonId"));
					//if (prodItem.containsKey("amount")) productItem.put("amount", prodItem.getString("amount"));
					// productItem.put("returnAmount", orderItem.getBigDecimal("selectedAmount"));

					listProduct.add(productItem);
				}
			}
		} else {
			listProduct = (List<Map<String, Object>>) context.get("listProduct");
		}
		if (UtilValidate.isEmpty(listProduct)) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuantityMustBeGreaterThanZero", locale));

		// Get parameter information general
		String returnId = (String) context.get("returnId");

		if (UtilValidate.isEmpty(returnId)) {
			returnId = delegator.getNextSeqId("ReturnHeader");
		}

		try {
			// create return header
			String partyId = (String) context.get("customerId");
			String description = (String) context.get("description");
			String originContactMechId = (String) context.get("originContactMechId");
			String destinationDistributorId = (String) context.get("destinationDistributorId");
			String currencyUomId = (String) context.get("currencyUomId");
			String destinationFacilityId = null;
			List<String> destinationFacilities = EntityUtil.getFieldListFromEntityList(
					delegator.findList("FacilityAll", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", destinationDistributorId)), null, null, null, false), "facilityId", false);
			if (UtilValidate.isNotEmpty(destinationFacilities)) {
				destinationFacilityId = destinationFacilities.get(0);
			}
			Map<String, Object> createReturnHeaderMap = FastMap.newInstance();
			createReturnHeaderMap.put("userLogin", userLogin);
			createReturnHeaderMap.put("returnHeaderTypeId", "CUSTOMER_RETURN");
			createReturnHeaderMap.put("statusId", "RETURN_REQUESTED");
			createReturnHeaderMap.put("fromPartyId", partyId);
			createReturnHeaderMap.put("toPartyId", destinationDistributorId);
			createReturnHeaderMap.put("originContactMechId", originContactMechId);
			if(UtilValidate.isNotEmpty(destinationFacilityId)){
				createReturnHeaderMap.put("destinationFacilityId", destinationFacilityId);
			}
			createReturnHeaderMap.put("needsInventoryReceive", "Y");
			createReturnHeaderMap.put("currencyUomId", currencyUomId);
			createReturnHeaderMap.put("description", description);
			//createReturnHeaderMap.put("grandTotal", cart.getGranTotalReturn());
			//createReturnHeaderMap.put("taxTotal", cart.getTotalSalesTax());
			Map<String, Object> createReturnHeaderResult = dispatcher.runSync("createReturnHeader", createReturnHeaderMap);
			if (ServiceUtil.isError(createReturnHeaderMap)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createReturnHeaderResult));
			}
			returnId = (String) createReturnHeaderResult.get("returnId");

			// create return item
			String returnTypeId = "RTN_REFUND";
			String returnItemTypeId = "RET_FPROD_ITEM";

			for (Map<String, Object> productItem : listProduct) {
				String productId = (String) productItem.get("productId");
				String quantityUomId = (String) productItem.get("quantityUomId");
				String itemDescription = (String) productItem.get("description");
				BigDecimal returnPrice = null;
				BigDecimal returnQuantity = null;

				if (productItem.containsKey("returnPrice")) {
					String returnPriceStr = (String) productItem.get("returnPrice");
					if (UtilValidate.isNotEmpty(returnPriceStr)) {
						// parse the quantity
						try {
							returnPrice = new BigDecimal(returnPriceStr); //(BigDecimal) ObjectType.simpleTypeConvert(returnPriceStr, "BigDecimal", null, locale);
						} catch (Exception e) {
							Debug.logWarning(e, "Problems parsing return price string: " + returnPriceStr, module);
							returnPrice = BigDecimal.ZERO;
						}
					}
				}
				if (productItem.containsKey("returnQuantity")) {
					String returnQuantityStr = (String) productItem.get("returnQuantity");
					if (UtilValidate.isNotEmpty(returnQuantityStr)) {
						// parse the quantity
						try {
							returnQuantity = new BigDecimal(returnQuantityStr); //(BigDecimal) ObjectType.simpleTypeConvert(returnQuantityStr, "BigDecimal", null, locale);
						} catch (Exception e) {
							Debug.logWarning(e, "Problems parsing return quantity string: " + returnQuantityStr, module);
							returnQuantity = BigDecimal.ZERO;
						}
					}
				}

				Map<String, Object> createCartItemMap = FastMap.newInstance();
				createCartItemMap.put("userLogin", userLogin);
				createCartItemMap.put("returnId", returnId);
				createCartItemMap.put("returnTypeId", returnTypeId);
				createCartItemMap.put("returnItemTypeId", returnItemTypeId);
				createCartItemMap.put("productId", productId);
				createCartItemMap.put("quantityUomId", quantityUomId);
				createCartItemMap.put("returnQuantity", returnQuantity);
				createCartItemMap.put("returnPrice", returnPrice);
				createCartItemMap.put("description", itemDescription);

				Map<String, Object> createReturnItemDirectly = dispatcher.runSync("createReturnItemDirectlyBySalesman", createCartItemMap);
				if (ServiceUtil.isError(createReturnItemDirectly)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createReturnItemDirectly));
				}

				//create return adjustment
				List<GenericValue> productTax = FastList.newInstance();
				productTax = delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", productId), null, false);

				if (UtilValidate.isNotEmpty(productTax)){
					BigDecimal taxPercent = productTax.get(0).getBigDecimal("taxPercentage");
					if (taxPercent.compareTo(BigDecimal.ZERO) > 0){
						Map<String, Object> createCartItemAdjMap = FastMap.newInstance();
						createCartItemAdjMap.put("returnId", returnId);
						createCartItemAdjMap.put("returnItemSeqId", createReturnItemDirectly.get("returnItemSeqId"));
						createCartItemAdjMap.put("returnTypeId", returnTypeId);
						createCartItemAdjMap.put("returnAdjustmentTypeId", "RET_SALES_TAX_ADJ");
						createCartItemAdjMap.put("description", UtilProperties.getMessage("BasePosUiLabels", "BSReturnSalesTaxNoOrder", locale));
						createCartItemAdjMap.put("shipGroupSeqId", "_NA_");
						createCartItemAdjMap.put("createdDate", UtilDateTime.nowTimestamp());
						createCartItemAdjMap.put("createdByUserLogin", userLogin.getString("userLoginId"));
						createCartItemAdjMap.put("taxAuthorityRateSeqId", productTax.get(0).getString("taxAuthorityRateSeqId"));
						createCartItemAdjMap.put("sourcePercentage", taxPercent);
						createCartItemAdjMap.put("primaryGeoId", productTax.get(0).getString("originGeoId"));
						createCartItemAdjMap.put("taxAuthGeoId", productTax.get(0).getString("taxAuthGeoId"));
						createCartItemAdjMap.put("taxAuthPartyId", productTax.get(0).getString("taxAuthPartyId"));

						BigDecimal taxAmount = returnQuantity.multiply(returnPrice.multiply(taxPercent).divide(new BigDecimal(100)));
						createCartItemAdjMap.put("amount", taxAmount);
						createCartItemAdjMap.put("userLogin", userLogin);

						Map<String, Object> createReturnAdjustmentResult = dispatcher.runSync("createReturnAdjustment", createCartItemAdjMap);
						if (ServiceUtil.isError(createReturnAdjustmentResult)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createReturnAdjustmentResult));
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}

		successResult.put("returnId", returnId);
		return successResult;
	}
}
