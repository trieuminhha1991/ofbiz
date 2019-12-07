package com.olbius.acc.report.logistics;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;

@SuppressWarnings({ "unchecked" })
public class InventoryAverageCostsServices {
	public static final String module = InventoryAverageCostsServices.class.getName();
	public static final String resource = "widgetUiLabels";
	public static final String resourceError = "widgetErrorUiLabels";
	 
	@SuppressWarnings("unused")
	public static Map<String,Object> getListInventoryAverageCost(DispatchContext dpct, Map<String,Object> context) {
	 	Delegator delegator = dpct.getDelegator();
	 	LocalDispatcher dispatcher = dpct.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<EntityCondition> extraCondition = FastList.newInstance();
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	List<String> extraSort = FastList.newInstance();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int size = parameters.get("pagesize") !=null? Integer.parseInt(parameters.get("pagesize")[0]) : 15;
		int page = parameters.get("pagenum") != null? Integer.parseInt(parameters.get("pagenum")[0]) : 0;
		int start = size * page;
		int end = start + size;
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));   
    	for (EntityCondition cond : listAllConditions) {
    		String condStr = cond.toString();
    		if(condStr.contains("totalQuantityOnHand") || condStr.contains("productAverageCost") || condStr.contains("totalInventoryCost")) {
    			extraCondition.add(cond);
    		}
    	}
    	listAllConditions.removeAll(extraCondition);
    	
    	for (String item : listSortFields) {
    		if(item.contains("totalQuantityOnHand") || item.contains("productAverageCost") || item.contains("totalInventoryCost")) {
    			extraSort.add(item);
    		}
    	}
    	listSortFields.removeAll(extraSort);
    	
    	String facilityId = (parameters.containsKey("facilityId")) ? parameters.get("facilityId")[0] : null;
    	try {
    		GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", organizationPartyId), false);
    		String cogsMethodId = partyAcctgPreference != null? partyAcctgPreference.getString("cogsMethodId") : null;
    		List<EntityCondition> productAverageCostCondList = FastList.newInstance();
    		productAverageCostCondList.add(EntityUtil.getFilterByDateExpr());
    		productAverageCostCondList.add(EntityCondition.makeCondition("productAverageCostTypeId", "SIMPLE_AVG_COST"));
    		productAverageCostCondList.add(EntityCondition.makeCondition("facilityId", facilityId));
    		productAverageCostCondList.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
    		EntityCondition productAverageCostCond = EntityCondition.makeCondition(productAverageCostCondList); 
    		
    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId),
                            EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, organizationPartyId)
                            ), EntityJoinOperator.AND);
    		listAllConditions.add(tmpConditon);
    		
    		if (UtilValidate.isEmpty(listSortFields) && UtilValidate.isEmpty(extraSort)) {
    			listSortFields.add("productId");
    		}
	    	List<GenericValue> inventoryItems = delegator.findList("InventoryItemAndProductGroupBy", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
	    	boolean extraFilter = false;
	    	int totalRow = 0;
	    	if(UtilValidate.isNotEmpty(extraCondition)) {
	    		extraFilter = true;
	    	} else {
	    		totalRow = inventoryItems.size();
	    		if (end > totalRow) {
	    			end = totalRow;
	    		}
	    		if (size > 0) {
	    			inventoryItems = inventoryItems.subList(start, end);
	    		}
	    	}
	    	List<Map<String,Object>> inventoryAverageCosts = FastList.newInstance();
	    	String serviceName = null;
	    	for (GenericValue inventoryItem: inventoryItems) {
	    		String productId = inventoryItem.getString("productId");
	    		BigDecimal productAverageCost = null;
	    		if ("COGS_AVG_COST".equals(cogsMethodId)) {
	    			List<GenericValue> productAverageCostList = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(productAverageCostCond, 
	    					EntityJoinOperator.AND, EntityCondition.makeCondition("productId", productId)), null, UtilMisc.toList("-fromDate"), null, false);
	    			if (UtilValidate.isNotEmpty(productAverageCostList)) {
	    				productAverageCost = productAverageCostList.get(0).getBigDecimal("averageCost");
	    			}
	    		}
	    		if ("Y".equals(inventoryItem.get("requireAmount")) && "WEIGHT_MEASURE".equals(inventoryItem.get("amountUomTypeId"))) {
	    			serviceName = "calcWeightProductAverageCost";
	    		} else {
	    			serviceName = "calculateProductAverageCost";
	    		}
	    		Map<String,Object> result = dpct.getDispatcher().runSync(serviceName, UtilMisc.toMap("productId", productId, "facilityId", facilityId, "ownerPartyId", organizationPartyId, "userLogin", userLogin));
	    		BigDecimal totalQuantityOnHand = result.get("totalQuantityOnHand") != null? (BigDecimal)result.get("totalQuantityOnHand") : (BigDecimal)result.get("totalAmountOnHandTotal");
    	        if (productAverageCost == null) {
    	        	productAverageCost = (BigDecimal) result.get("productAverageCost");
    	        }
    	        BigDecimal totalInventoryCost = BigDecimal.ZERO;
	    		if ("COGS_AVG_COST".equals(cogsMethodId)) {
	    			totalInventoryCost = totalQuantityOnHand.multiply(productAverageCost);
	    		} else {
	    			totalInventoryCost = (BigDecimal) result.get("totalInventoryCost");
	    		}
    	        String currencyUomId = (String) result.get("currencyUomId");
	        	Map<String,Object> mapTmp = inventoryItem.getAllFields();
	        	mapTmp.put("totalQuantityOnHand", totalQuantityOnHand);
	        	mapTmp.put("productAverageCost", productAverageCost);
	        	mapTmp.put("totalInventoryCost", totalInventoryCost);
	        	mapTmp.put("currencyUomId", currencyUomId);
	        	inventoryAverageCosts.add(mapTmp);
	    	}
	    	
	    	if (extraFilter) {
	    		inventoryAverageCosts = EntityMiscUtil.filterMap(inventoryAverageCosts, extraCondition);
	    		totalRow = inventoryAverageCosts.size();
	    	}
	    	
	    	if (UtilValidate.isNotEmpty(extraSort)) {
	    		inventoryAverageCosts = EntityMiscUtil.sortList(inventoryAverageCosts, extraSort);
	    	}
	    	
    		successResult.put("listIterator", inventoryAverageCosts);
    		successResult.put("TotalRows", String.valueOf(totalRow));
		} catch (Exception e) {
			Debug.log("error call services getListInventoryAverageCost" + e.getMessage());
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	 }
	
	public static Map<String,Object> jqGetListFacilities(DispatchContext dpct,Map<String,Object> context){
	 	Delegator delegator = dpct.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		if (parameters.containsKey("organizationPartyId") && UtilValidate.isNotEmpty(parameters.get("organizationPartyId"))) {
			String organizationPartyId = parameters.get("organizationPartyId")[0];
            listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", organizationPartyId));
        }
		if(parameters.containsKey("facilityGroupId") && UtilValidate.isNotEmpty(parameters.get("facilityGroupId"))){
            String facilityGroupId  = parameters.get("facilityGroupId")[0];
            listAllConditions.add(EntityCondition.makeCondition("primaryFacilityGroupId", facilityGroupId));
        }
    	try {
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "Facility",
    				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		successResult.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.log("error call services jqGetListFacilities" + e.getMessage());
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return successResult;
	 }
}
