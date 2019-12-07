package com.olbius.bi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import javolution.util.FastMap;

public class OlapServices {
	public final static String module = OlapServices.class.getName();
	
	public static Map<String, Object> getProductId(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();

		List<GenericValue> values = null;

		try {
			values = delegator.findList("Product", null, null, UtilMisc.toList("productId"), null, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		List<Map<String,String>> productId = new ArrayList<Map<String,String>>();
		
		for (GenericValue value : values) {
			Map<String,String> _map = new HashMap<String, String>();
			_map.put("text", "[" + value.getString("productId") + "] " +  value.getString("internalName"));
			_map.put("value", value.getString("productId"));
			productId.add(_map);
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("product", productId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> getCategory(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();

		List<GenericValue> values = null;

		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		conditions.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "CATALOG_CATEGORY"));
		
		try {
			values = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions, EntityOperator.OR),
					null, UtilMisc.toList("productCategoryId"), null, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		List<Map<String,String>> category = new ArrayList<Map<String,String>>();
		
		for (GenericValue value : values) {
			Map<String,String> _map = new HashMap<String, String>();
			_map.put("text", "[" + value.getString("productCategoryId") + "] " +  value.getString("categoryName"));
			_map.put("value", value.getString("productCategoryId"));
			category.add(_map);
		}
		
		Map<String, Object> result = FastMap.newInstance();
		result.put("category", category);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
