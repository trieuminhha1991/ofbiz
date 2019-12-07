package com.olbius.accounting.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class VatTaxJQServices {
	public static final String module = VatTaxJQServices.class.getName();
	 	
		@SuppressWarnings("unchecked")
		public static Map<String, Object> getListTaxProductCategoryMember(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	listSortFields.add("productCategoryId");
	    	listSortFields.add("productId");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("productCategoryTypeId", "TAX_CATEGORY");
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("ProductCategoryAndMember", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling getListTaxProductCategoryMember service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 	 	
}
