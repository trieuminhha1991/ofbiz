package com.olbius.salesmtl.product;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;

import javolution.util.FastSet;

public class ProductStoreServices {
	public static final String module = ProductStoreServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetProductStoreForRequestNewCustomer(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	//GenericValue userLogin = (GenericValue) context.get("userLogin");
    	try {
    		String salesMethodChannelEnumId = SalesUtil.getParameter(parameters, "salesMethodChannelEnumId");
    		if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
    			listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", salesMethodChannelEnumId));
    		}
    		
    		Set<String> listSelectFields = FastSet.newInstance();
    		listSelectFields.add("productStoreId");
    		listSelectFields.add("storeName");
    		listSelectFields.add("payToPartyId");
    		listSelectFields.add("salesMethodChannelEnumId");
    		listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductStore", EntityCondition.makeCondition(listAllConditions), null, listSelectFields, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductStoreForRequestNewCustomer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
}
