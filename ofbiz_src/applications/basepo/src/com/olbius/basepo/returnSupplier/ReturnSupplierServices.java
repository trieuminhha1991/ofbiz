package com.olbius.basepo.returnSupplier;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

public class ReturnSupplierServices {
	public static final String module = ReturnSupplierServices.class.getName();

	public static Map<String, Object> createNewReturnSupplier(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String userLoginId = userLogin.getString("userLoginId");
		String fromPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		String returnHeaderTypeId = (String) context.get("returnHeaderTypeId");
		String statusId = (String) context.get("statusId");
		String toPartyId = (String) context.get("toPartyId");
		String currencyUomId = (String) context.get("currencyUomId");
		String description = (String) context.get("description");
		String needsInventoryReceive = (String) context.get("needsInventoryReceive");
		String destinationFacilityId = (String) context.get("destinationFacilityId");
		Timestamp entryDate = UtilDateTime.nowTimestamp();
		Map<String, Object> map = FastMap.newInstance();
		map.put("returnHeaderTypeId", returnHeaderTypeId);
		map.put("fromPartyId", fromPartyId);
		map.put("statusId", statusId);
		map.put("toPartyId", toPartyId);
		map.put("currencyUomId", currencyUomId);
		map.put("description", description);
		map.put("needsInventoryReceive", needsInventoryReceive);
		map.put("destinationFacilityId", destinationFacilityId);
		map.put("entryDate", entryDate);
		map.put("userLogin", userLogin);

		try {
			Map<String, Object> mapResult = dispatcher.runSync("createReturnHeader", map);
			result.put("returnId", (String) mapResult.get("returnId"));
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(e.getMessage());
		}

		return result;
	}

	public static Map<String, Object> getOrderItemByOrdeId(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String) context.get("orderId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Set<String> fieldSl = FastSet.newInstance();
		fieldSl.add("orderItemSeqId");
		fieldSl.add("orderId");
		fieldSl.add("productId");
		fieldSl.add("quantity");
		fieldSl.add("unitPrice");
		fieldSl.add("itemDescription");
		List<GenericValue> listOrderItems = FastList.newInstance();
		List<Map<String, Object>> listReturnItems = FastList.newInstance();
		try {
			listOrderItems = delegator.findList("OrderItem",
					EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, true);
			for (GenericValue orderItem : listOrderItems) {
				try {
					Map<String, Object> serviceResult = dispatcher.runSync("getReturnableQuantity",
							UtilMisc.toMap("orderItem", orderItem, "userLogin", userLogin));
					GenericValue product = delegator.findOne("Product",
							UtilMisc.toMap("productId", orderItem.getString("productId")), false);
					Map<String, Object> map = FastMap.newInstance();
					map.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
					map.put("orderId", orderId);
					map.put("productId", orderItem.getString("productId"));
					map.put("productCode", product.getString("productCode"));
					map.put("productName", product.getString("productName"));
					map.put("orderedQuantity", orderItem.getBigDecimal("quantity"));
					map.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
					map.put("itemDescription", orderItem.getString("itemDescription"));
					map.put("returnableQuantity", (BigDecimal) serviceResult.get("returnableQuantity"));
					map.put("quantity", (BigDecimal) serviceResult.get("returnableQuantity"));
					listReturnItems.add(map);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					Debug.logError(e.getMessage(), "module");
				}
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.logError(e.getMessage(), "module");
		}
		result.put("listOrderItems", listReturnItems);
		return result;
	}

	public static Map<String, Object> createReturnSupplierItems(DispatchContext dpx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Security security = dpx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> returnResult = ServiceUtil.returnSuccess();
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"RETURNPO_EDIT");
		if (hasPermission) {
			String returnId = (String) context.get("returnId");
			String listOrderItems = (String) context.get("orderItems");
            JSONArray arrOrderItems = JSONArray.fromObject(listOrderItems);
            String listPromoItems = (String) context.get("adjustmentPromoItems");
            JSONArray arrPromoItems = JSONArray.fromObject(listPromoItems);
            int size = arrOrderItems.size();
			for (int i = 0; i < size; i++) {
				JSONObject orderItem = arrOrderItems.getJSONObject(i);
				String productId = (String) orderItem.get("productId");

				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				String requireAmount = product.getString("requireAmount");
				String orderId = (String) orderItem.get("orderId");
				String orderItemSeqId = (String) orderItem.get("orderItemSeqId");
				String quantityInt = orderItem.getString("quantity");
				String unitPrice = orderItem.getString("unitPrice");
				String returnReasonId = (String) orderItem.get("returnReasonId");
				BigDecimal returnPrice = new BigDecimal(unitPrice);
				BigDecimal quantity = new BigDecimal(quantityInt);
				Map<String, Object> map = FastMap.newInstance();
				map.put("returnId", returnId);
				map.put("productId", productId);
				if(orderItem.containsKey("quantityUomId"))
    				map.put("quantityUomId", orderItem.getString("quantityUomId"));
				if(orderItem.containsKey("weightUomId"))
    				map.put("weightUomId", orderItem.get("weightUomId"));
				map.put("returnItemTypeId", "RET_FPROD_ITEM");
				map.put("description", product.getString("productName"));
				map.put("orderId", orderId);
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					map.put("returnQuantity", BigDecimal.ONE);
					map.put("returnAmount", quantity);
				} else {
					map.put("returnQuantity", quantity);
				}
				map.put("returnPrice", returnPrice);
				map.put("orderItemSeqId", orderItemSeqId);
				map.put("returnReasonId", returnReasonId);
				map.put("returnTypeId", "RTN_REFUND");
				map.put("expectedItemStatus", "INV_RETURNED");
				map.put("userLogin", userLogin);
				try {
					dispatcher.runSync("createReturnItemAndAdjustmentModify", map);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			int sizeP = arrPromoItems.size();
			for(int i = 0 ; i < sizeP ; i ++) {
                JSONObject promoItem = arrPromoItems.getJSONObject(i);
                String returnItemMapKey = promoItem.getString("orderAdjustmentTypeId");
                String returnHeaderTypeId = "VENDOR_RETURN";
                GenericValue returnItemTypeMap = delegator.findOne("ReturnItemTypeMap", UtilMisc.toMap("returnItemMapKey", returnItemMapKey, "returnHeaderTypeId", returnHeaderTypeId), false);
                String returnAdjustmentTypeId = returnItemTypeMap.getString("returnItemTypeId");
                String returnAdjustmentId = delegator.getNextSeqId("ReturnAdjustment");
                BigDecimal amount =BigDecimal.valueOf(Long.valueOf(promoItem.getString("amount")));
                GenericValue returnAdjustment = delegator.makeValidValue("ReturnAdjustment");
                returnAdjustment.put("returnAdjustmentId", returnAdjustmentId);
                returnAdjustment.put("returnAdjustmentTypeId", returnAdjustmentTypeId);
                returnAdjustment.put("returnTypeId", "RTN_REFUND");
                returnAdjustment.put("amount", amount);
                returnAdjustment.put("orderAdjustmentId", promoItem.get("orderAdjustmentId"));
                returnAdjustment.put("returnId", returnId);
                returnAdjustment.put("returnItemSeqId", "_NA_");
                returnAdjustment.put("shipGroupSeqId", "_NA_");
                returnAdjustment.put("description", promoItem.get("description"));
                returnAdjustment.put("productPromoId", promoItem.get("productPromoId"));
                returnAdjustment.put("productPromoRuleId", promoItem.get("productPromoRuleId"));
                returnAdjustment.put("productPromoActionSeqId", promoItem.get("productPromoActionSeqId"));
                returnAdjustment.put("createdByUserLogin", userLogin.get("partyId"));
                returnAdjustment.create();
            }
		} else {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}

		return returnResult;
	}

	public static Map<String, Object> JQListReturnSupplierSupplier(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		listAllConditions.add(EntityCondition
				.makeCondition(UtilMisc.toMap("returnHeaderTypeId", "VENDOR_RETURN", "fromPartyId", organizationId)));
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			listSortFields.add("returnId DESC");
			listIterator = delegator.find("ReturnHeaderAndParty",
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields,
					opts);
		} catch (Exception e) {

		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> jqxUpdateReturnItemsSupp(DispatchContext ctx, Map<String, Object> context) {
		Locale locale = (Locale) context.get("Locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String returnId = (String) context.get("returnId");
		String returnItemSeqId = (String) context.get("returnItemSeqId");
		try {
			BigDecimal returnQuantity = (BigDecimal) ObjectType
					.simpleTypeConvert((String) context.get("returnQuantity"), "BigDecimal", null, locale);
			Map<String, Object> map = FastMap.newInstance();
			map.put("returnId", returnId);
			map.put("returnItemSeqId", returnItemSeqId);
			map.put("returnQuantity", returnQuantity);
			map.put("userLogin", userLogin);
			dispatcher.runSync("updateReturnItem", map);
		} catch (GeneralException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> createReturnItemAndAdjustment(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Debug.logInfo("createReturnItemAndAdjustment's context:" + context, module);
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		Debug.logInfo("orderItemSeqId:" + orderItemSeqId + "#", module);
		LocalDispatcher dispatcher = dctx.getDispatcher();
		// if the request is to create returnItem, orderItemSeqId should not be
		// empty
		String serviceName = UtilValidate.isNotEmpty(orderItemSeqId) ? "createReturnItem" : "createReturnAdjustment";
		Debug.logInfo("serviceName:" + serviceName, module);
		try {
			Map<String, Object> inMap = dctx.makeValidContext(serviceName, "IN", context);
			if ("createReturnItem".equals(serviceName)) {
				// we don't want to automatically include the adjustments
				// when the return item is created because they are selectable
				// by the user
				inMap.put("includeAdjustments", "Y");
			}
			return dispatcher.runSync(serviceName, inMap);
		} catch (org.ofbiz.service.GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createReturnSupplierWithoutOrder(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Security security = ctx.getSecurity();
        OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        if (!(securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "RETURNPO_NEW_WOUTORD"))) {
        	return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
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
					if (prodItem.containsKey("weightUomId")) productItem.put("weightUomId", prodItem.getString("weightUomId"));
					if (prodItem.containsKey("description")) productItem.put("description", prodItem.getString("description"));
					if (prodItem.containsKey("returnReasonId")) productItem.put("returnReasonId", prodItem.getString("returnReasonId"));
					if (prodItem.containsKey("returnQuantity")){ 
						productItem.put("returnQuantity", prodItem.getString("returnQuantity"));
						productItem.put("returnAmount", prodItem.getString("returnQuantity"));
					}
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
    	if (UtilValidate.isEmpty(listProduct)) return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSQuantityMustBeGreaterThanZero", locale));
		
    	// Get parameter information general
        String returnId = (String) context.get("returnId");
        
        if (UtilValidate.isEmpty(returnId)) {
        	returnId = delegator.getNextSeqId("ReturnHeader");
        }
        
        try {
        	// create return header
    		String partyId = (String) context.get("supplierId");
    		String description = (String) context.get("description");
    		String originContactMechId = (String) context.get("originContactMechId");
    		String destinationFacilityId = (String) context.get("destinationFacilityId");
    		String currencyUomId = (String) context.get("currencyUomId");
    		
    		String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin); //PartyUtil.getRootOrganization(delegator);
    		
    		Map<String, Object> createReturnHeaderMap = FastMap.newInstance();
    		createReturnHeaderMap.put("userLogin", userLogin);
    		createReturnHeaderMap.put("returnHeaderTypeId", "VENDOR_RETURN");
    		createReturnHeaderMap.put("statusId", "SUP_RETURN_REQUESTED");
    		createReturnHeaderMap.put("fromPartyId", organizationId);
    		createReturnHeaderMap.put("toPartyId", partyId);
    		createReturnHeaderMap.put("originContactMechId", originContactMechId);
    		createReturnHeaderMap.put("destinationFacilityId", destinationFacilityId);
    		createReturnHeaderMap.put("needsInventoryReceive", "N");
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
    	        BigDecimal returnAmount = null;
    	        
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
    	        if (productItem.containsKey("returnAmount")) {
    	        	String returnAmountStr = (String) productItem.get("returnAmount");
    	        	if (UtilValidate.isNotEmpty(returnAmountStr)) {
    	        		// parse the quantity
    	        		try {
    	        			returnAmount = new BigDecimal(returnAmountStr); //(BigDecimal) ObjectType.simpleTypeConvert(returnAmountStr, "BigDecimal", null, locale);
    	        		} catch (Exception e) {
    	        			Debug.logWarning(e, "Problems parsing return quantity string: " + returnAmountStr, module);
    	        			returnAmount = BigDecimal.ZERO;
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
    			createCartItemMap.put("returnReasonId", returnReasonId);
    			createCartItemMap.put("returnAmount", returnAmount);
    			
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
            return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
        }
    	
        successResult.put("returnId", returnId);
		return successResult;
	}
	
	public static Map<String, Object> getInfoProductAddToReturnSupplier(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, Object> productInfo = FastMap.newInstance();
		try {
			String productId = (String) context.get("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (product == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
			}
			
			String quantityUomId = (String) context.get("quantityUomId");
			
			// Miss: parentProductId, parentProductCode, features, colorCode
			productInfo.put("productId", product.get("productId"));
			productInfo.put("productCode", product.get("productCode"));
			productInfo.put("productName", product.get("productName"));
			if (UtilValidate.isEmpty(quantityUomId)) quantityUomId = product.getString("quantityUomId");
			productInfo.put("quantityUomId", quantityUomId);
			
			List<Map<String, Object>> packingUomIds = ProductWorker.getListQuantityUomIds(product.getString("productId"), product.getString("quantityUomId"), delegator, dispatcher);
			productInfo.put("packingUomIds", packingUomIds);
			
			// get info tax category
			String taxCategoryId = null;
			BigDecimal taxPercentage = null;
			Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
			if (productTax != null) {
				taxCategoryId = (String) productTax.get("taxCategoryId");
				taxPercentage = (BigDecimal) productTax.get("taxPercentage");
			}
			productInfo.put("taxCategoryId", taxCategoryId);
			productInfo.put("taxPercentage", taxPercentage);
			
			GenericValue productTempData = EntityUtil.getFirst(delegator.findByAnd("ProductTempData", UtilMisc.toMap("productId", productId), null, false));
			if (productTempData != null) {
				productInfo.put("currencyUomId", productTempData.get("currencyUomId"));
				productInfo.put("unitPrice", productTempData.get("unitPrice"));
			}
			
			if (UtilValidate.isNotEmpty(packingUomIds)) {
				for (Map<String, Object> uomItem : packingUomIds) {
					if (quantityUomId != null && quantityUomId.equals((String) uomItem.get("uomId"))) {
						productInfo.put("quantityConvert", uomItem.get("quantityConvert"));
						productInfo.put("unitPrice", uomItem.get("unitPriceConvert"));
					}
				}
			}
			
			/*// get average cost
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("organizationPartyId", organizationId));
			conds.add(EntityCondition.makeCondition("facilityId", facilityId));
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityUtil.getFilterByDateExpr());
			GenericValue productAverageCost = EntityUtil.getFirst(delegator.findList("ProductAverageCost", EntityCondition.makeCondition(conds), null, null, null, false));
			if (productAverageCost != null) {
				productInfo.put("averageCost", productAverageCost.get("averageCost"));
			}*/
			
			// get return price suggest
			String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin);
			String facilityId = (String) context.get("facilityId");
			try {
				Map<String, Object> returnPriceResult = dispatcher.runSync("getProductReturnPrice", UtilMisc.toMap("productId", productId, "organizationPartyId", organizationId, "facilityId", facilityId, "userLogin", userLogin), 300, true);
				if (ServiceUtil.isError(returnPriceResult)) {
					Debug.logWarning("Error when run service 'getProductReturnPrice'. Error's message is " + ServiceUtil.getErrorMessage(returnPriceResult), module);
				}
				BigDecimal returnPrice = (BigDecimal) returnPriceResult.get("returnPrice");
				productInfo.put("returnPriceSug", returnPrice);
			} catch (Exception e) {
				Debug.logWarning("Error when run service 'getProductReturnPrice'. Error's message is " + e, module);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getInfoProductAddToReturnSupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("productInfo", productInfo);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductToReturnSupplier(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		boolean hasVirtualProd = false;
    		String hasVirtualProdStr = SalesUtil.getParameter(parameters, "hasVirtualProd");
			if ("Y".equals(hasVirtualProdStr)) hasVirtualProd = true;
			if (!hasVirtualProd) {
            	listAllConditions.add(EntityCondition.makeCondition("isVirtual", "N"));
            }
			
			String searchKey = SalesUtil.getParameter(parameters, "searchKey");
			if (searchKey != null) {
				listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productNameSearch"), EntityOperator.LIKE, EntityFunction.UPPER("%" + searchKey + "%")));
			}
			
            opts.setDistinct(true);
            Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            //selectFields.add("sequenceNum");
            selectFields.add("requireAmount");
            selectFields.add("productNameSearch");
            selectFields.add("currencyUomId");
            selectFields.add("partyId");

            String currencyUomId = SalesUtil.getParameter(parameters, "currencyUomId");
            String supplierId = SalesUtil.getParameter(parameters, "supplierId");
            listAllConditions.add(EntityUtil.getFilterByDateExpr());
            if(UtilValidate.isNotEmpty(currencyUomId)) {
                listAllConditions.add(EntityCondition.makeCondition("currencyUomId", currencyUomId));
            }
            if(UtilValidate.isNotEmpty(supplierId)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", supplierId));
            }
            //listSortFields.add("sequenceNum");
            listSortFields.add("productCode");
            EntityListIterator iterator = delegator.find("ProductAndCatalogTempDataDetailSearch", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, null);
            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, successResult);
            
            if (listProduct != null) {
            	// get average cost
            	String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin);
            	String facilityId = SalesUtil.getParameter(parameters, "facilityId");
            	for (GenericValue itemProd : listProduct) {
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			
        			List<EntityCondition> conds = FastList.newInstance();
        			conds.add(EntityCondition.makeCondition("organizationPartyId", organizationId));
        			conds.add(EntityCondition.makeCondition("facilityId", facilityId));
        			conds.add(EntityCondition.makeCondition("productId", itemProd.get("productId")));
        			conds.add(EntityUtil.getFilterByDateExpr());

        			GenericValue productAverageCost = EntityUtil.getFirst(delegator.findList("ProductAverageCost", EntityCondition.makeCondition(conds), null, null, null, false));
        			if (productAverageCost != null) {
        				itemMap.put("returnPrice", productAverageCost.get("averageCost"));
        			} else {
        				itemMap.put("returnPrice", null);
        			}
        			
        			listIterator.add(itemMap);
				}
            }
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductToReturnSupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
}