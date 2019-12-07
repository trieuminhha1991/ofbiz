package com.olbius.baselogistics.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.util.InventoryUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.baselogistics.util.LogisticsStringUtil;
import com.olbius.baselogistics.util.LogisticsUtil;
import com.olbius.common.util.EntityMiscUtil;

public class ProductServices {
	
	public static final String module = ProductServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceCommonEntity = "CommonEntityLabels";
	public static final String OrderEntityLabels = "OrderEntityLabels";
    public static final String resourceError = "BaseLogisticsErrorUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> JQGetListProductByOrganiztion(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
        List<Map<String, Object>> listProducts= new ArrayList<Map<String, Object>>();
        String currentOrg = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String productTypeId = null;
		if (parameters.get("productTypeId") != null && parameters.get("productTypeId").length > 0) {
			productTypeId = parameters.get("productTypeId")[0];
		}
		if (UtilValidate.isNotEmpty(productTypeId)) {
			listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, productTypeId));
		} else {
			listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.IN, UtilMisc.toList("FINISHED_GOOD", "AGGREGATED")));
		}
		
		if (listSortFields.isEmpty()) {
			listSortFields.add("productCode");
		}
		
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	try {
    		mapCondition = new HashMap<String, String>();
        	String orgPartyId = null;
        	if (parameters.get("orgPartyId") != null && parameters.get("orgPartyId").length > 0){
        		orgPartyId = (String)parameters.get("orgPartyId")[0];
        	} else {
        		orgPartyId = currentOrg;
        	}
        	
        	if (UtilValidate.isNotEmpty(orgPartyId)){
        		mapCondition.put("pay_to_party_id", orgPartyId);
        	}
        	
        	String facilityId = null;
        	if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0){
        		facilityId = (String)parameters.get("facilityId")[0];
        	}
        	
        	String inventoryInfo = null;
        	if (parameters.get("inventoryInfo") != null && parameters.get("inventoryInfo").length > 0){
        		inventoryInfo = (String)parameters.get("inventoryInfo")[0];
        	}
        	
        	String costType = null;
			if (parameters.get("costType") != null && parameters.get("costType").length > 0) {
				costType = parameters.get("costType")[0];
			}
			
        	mapCondition = new HashMap<String, String>();
        	String productStoreId = null;
        	if (parameters.get("productStoreId") != null && parameters.get("productStoreId").length > 0){
        		productStoreId = (String)parameters.get("productStoreId")[0];
        	}
        	if (UtilValidate.isNotEmpty(productStoreId)){
        		mapCondition.put("productStoreId", productStoreId);
        	}
        	
        	opts.setDistinct(true);
        	
 			EntityCondition variantConditon1 = EntityCondition.makeCondition("isVariant", "Y");
 			
 			EntityCondition conditon1 = EntityCondition.makeCondition("isVariant", "N");
 			EntityCondition conditon2 = EntityCondition.makeCondition("isVirtual", "N");
 			
 			EntityCondition andCond = EntityCondition.makeCondition(UtilMisc.toList(conditon1, conditon2), EntityOperator.AND);
 			EntityCondition orCond = EntityCondition.makeCondition(UtilMisc.toList(variantConditon1, andCond), EntityOperator.OR);
 			
 			listAllConditions.add(orCond);
 			EntityCondition rivalCond1 = EntityCondition.makeCondition("numRival", EntityOperator.EQUALS, null);
 			EntityCondition rivalCond2 = EntityCondition.makeCondition("numRival", EntityOperator.NOT_EQUAL, "1");
 			List<EntityCondition> listCondRivals = UtilMisc.toList(rivalCond1, rivalCond2);
 			EntityCondition rivalCond = EntityCondition.makeCondition(listCondRivals, EntityOperator.OR); 
 					
 			listAllConditions.add(rivalCond);
 			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
 			
 			List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
 			listProductTmps = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "Product", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
 			for (GenericValue item : listProductTmps) {
 				String productId = item.getString("productId");
				List<String> listQuantityUoms = LogisticsProductUtil.getProductPackingUoms(delegator, item.getString("productId"));
				Map<String, Object> itemTmp = FastMap.newInstance();
				itemTmp.putAll(item);
				itemTmp.put("packingUomIds", listQuantityUoms);
				itemTmp.put("quantityUomIds", listQuantityUoms);
				List<GenericValue> weightUoms = delegator.findList("Uom", EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "WEIGHT_MEASURE"), null, null, null, false);
				List<String> listWeightUomIds = new ArrayList<String>();	
				listWeightUomIds = EntityUtil.getFieldListFromEntityList(weightUoms, "uomId", true);
				itemTmp.put("weightUomIds", listWeightUomIds);
				
				if (UtilValidate.isNotEmpty(inventoryInfo) && "Y".equals(inventoryInfo)) {
					Map<String, Object> attributes = FastMap.newInstance();
	           	    attributes.put("facilityId", facilityId);
	           	    attributes.put("productId", productId);
	           	    
	        	    Map<String, Object> tmpResult = InventoryUtil.getDetailQuantityInventory(delegator, attributes);
	        	    itemTmp.put("availableToPromiseTotal", (BigDecimal)tmpResult.get("availableToPromiseTotal"));
	        	    itemTmp.put("quantityOnHandTotal", (BigDecimal)tmpResult.get("quantityOnHandTotal"));
	        	    itemTmp.put("amountOnHandTotal", (BigDecimal)tmpResult.get("amountOnHandTotal"));
				}
           	    
           	    if (UtilValidate.isNotEmpty(item.getString("requireAmount")) && "Y".equals(item.getString("requireAmount"))) {
           	    	itemTmp.put("uomId", item.getString("weightUomId"));
				} else {
					itemTmp.put("uomId", item.getString("quantityUomId"));
				}
				
				// price
				BigDecimal price = BigDecimal.ZERO;
				LocalDispatcher dispatcher = ctx.getDispatcher();
				if ("SALES_PRICE".equals(costType)){
					try {
						 Map<String, Object> map = dispatcher.runSync("calculateProductPriceCustom", UtilMisc.toMap("productId", productId, "userLogin", userLogin));
						 price = (BigDecimal) map.get("price");
						 itemTmp.put("unitCost", price);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: calculateProductPriceCustom error! " + e.toString());
					}
				} else {
					// default using average cost
					List<EntityCondition> listCondCosts = new ArrayList<EntityCondition>(); 
					EntityCondition priceTypeCond = EntityCondition.makeCondition("productAverageCostTypeId", EntityOperator.EQUALS, "SIMPLE_AVG_COST");
	 				EntityCondition orgCond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, company);
	 				EntityCondition productCond = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
	 				if (UtilValidate.isNotEmpty(facilityId)) {
	 					EntityCondition faCond = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
	 					listCondCosts.add(faCond);
					}
	 				listCondCosts.add(priceTypeCond);
	 				listCondCosts.add(orgCond);
	 				listCondCosts.add(productCond);
					List<GenericValue> listAverages = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(listCondCosts), null, null, null, false);
					listAverages = EntityUtil.filterByDate(listAverages);
					if (!listAverages.isEmpty()) {
						price = listAverages.get(0).getBigDecimal("averageCost");
					}
				}
				itemTmp.put("unitCost", price);
				listProducts.add(itemTmp);
			}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling  service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	successResult.put("listIterator", listProducts);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQGetListProductByIdentification(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		EntityListIterator listIterator = null;
		String orgPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<GenericValue> listProducts = new ArrayList<GenericValue>();
		Map<String, Object> successResult = FastMap.newInstance();
		try {
			mapCondition = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(orgPartyId)){
				mapCondition.put("pay_to_party_id", orgPartyId);
			}
			
			if (listSortFields.isEmpty()){
				listSortFields.add("productCode");
			}
			
			opts.setDistinct(true);
			Set<String> selectFields = FastSet.newInstance();
			selectFields.add("productId");
			selectFields.add("productName");
			selectFields.add("internalName");
			selectFields.add("quantityUomId");
			selectFields.add("productCode");
			selectFields.add("weight");
			selectFields.add("weightUomId");
			selectFields.add("productTypeId");
			
			EntityCondition variantConditon1 = EntityCondition.makeCondition("isVariant", "Y");
			
			EntityCondition conditon1 = EntityCondition.makeCondition("isVariant", "N");
			EntityCondition conditon2 = EntityCondition.makeCondition("isVirtual", "N");
			
			EntityCondition andCond = EntityCondition.makeCondition(UtilMisc.toList(conditon1, conditon2), EntityOperator.AND);
			EntityCondition orCond = EntityCondition.makeCondition(UtilMisc.toList(variantConditon1, andCond), EntityOperator.OR);
			
			listAllConditions.add(orCond);
			EntityCondition rivalCond1 = EntityCondition.makeCondition("numRival", EntityOperator.EQUALS, null);
			EntityCondition rivalCond2 = EntityCondition.makeCondition("numRival", EntityOperator.NOT_EQUAL, "1");
			List<EntityCondition> listCondRivals = UtilMisc.toList(rivalCond1, rivalCond2);
			EntityCondition rivalCond = EntityCondition.makeCondition(listCondRivals, EntityOperator.OR); 
			
			EntityCondition cond3 = EntityCondition.makeCondition("goodIdentificationTypeId", EntityOperator.EQUALS, "SKU");
			listAllConditions.add(rivalCond);
			listAllConditions.add(cond3);
			
			listIterator = delegator.find("GoodIdentificationAndProduct", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			listProducts = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling JQGetListProductByIdentification service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listProducts);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getProductRelateds(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Object> listProductTmps = (List<Object>)context.get("listProducts");
    	Boolean isJson = false;
    	if (!listProductTmps.isEmpty()){
    		if (listProductTmps.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, String>> listProducts = new ArrayList<Map<String, String>>();
    	if (isJson){
    		String stringJson = "["+(String)listProductTmps.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")){
					mapItem.put("productId", item.getString("productId"));
				}
				listProducts.add(mapItem);
			}
    	} else {
    		listProducts = (List<Map<String, String>>)context.get("listProducts");
    	}
    	List<Map<String, Object>> listProductRelateds = new ArrayList<Map<String, Object>>();
    	try {
	    	if (!listProducts.isEmpty()){
	    		List<String> listProductToIds = new ArrayList<String>();
	    		List<String> listProductFromIds = new ArrayList<String>();
	    		for (Map<String, String> item : listProducts) {
	    			String productId = item.get("productId");
	    			listProductFromIds.add(productId);
	    			List<GenericValue> listRelations = delegator.findList("ProductRelationship", EntityCondition.makeCondition(UtilMisc.toMap("productIdFrom", productId)), null, null, null, false);
	    			listRelations = EntityUtil.filterByDate(listRelations);
	    			for (GenericValue relation : listRelations) {
	    				Boolean check = true;
	    				for (String id : listProductToIds) {
							if (relation.getString("productIdTo").equals(id)) check = false; break;
						}
	    				if (check) listProductToIds.add(relation.getString("productIdTo"));
					}
				}
	    		if (!listProductToIds.isEmpty()){
	    			for (String productId : listProductToIds) {
	    				List<GenericValue> productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId", "LIST_PRICE", "productPricePurposeId", "PURCHASE")), null, null, null, false);
						productPrices = EntityUtil.filterByDate(productPrices);
						GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						Map<String, Object> map = FastMap.newInstance();
						map.put("productId", productId);
						map.put("productName", product.getString("productName"));
						map.put("internalName", product.getString("internalName"));
						map.put("quantityUomId", product.getString("quantityUomId"));
						map.put("productCode", product.getString("productCode"));
						if (!productPrices.isEmpty()){
							map.put("unitCost", productPrices.get(0).getBigDecimal("price"));
						} else {
							map.put("unitCost", BigDecimal.ZERO);
						}
						GenericValue userLogin = (GenericValue)context.get("userLogin");
						String facilityId = (String)context.get("facilityId");
						String orgPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
						BigDecimal qoh = BigDecimal.ZERO;
						if (UtilValidate.isNotEmpty(facilityId)){
							qoh = InventoryUtil.getQuantityOnHandTotal(delegator, productId, facilityId);
						} else {
							qoh = InventoryUtil.getQuantityOnHandTotalByOwner(delegator, productId, orgPartyId);
						}
						map.put("quantityOnHandTotal", qoh);
						EntityCondition Cond1 = EntityCondition.makeCondition("productIdFrom", EntityOperator.IN, listProductFromIds);
						EntityCondition Cond2 = EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId);
						List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
						EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
						List<GenericValue> list = delegator.findList("ProductRelationship", allConds, null, null, null, false);
						List<String> listProductFroms = new ArrayList<String>();
						for (GenericValue item : list) {
							Boolean check = true;
							for (String pr : listProductFroms) {
								if (pr.equals(item.getString("productIdFrom"))){
									check = false;
									break;
								}
							}
							if (check) listProductFroms.add(item.getString("productIdFrom"));
						}
						map.put("listOriginProductIds", listProductFroms);
						listProductRelateds.add(map);
					}
	    		}
	    	}
    	} catch (GenericEntityException e){
    		ServiceUtil.returnError("OLBIUS: error");
    	}
    	successResult.put("listProductRelateds", listProductRelateds);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getProductConfigRelateds(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Object> listProductTmps = (List<Object>)context.get("listProducts");
		Boolean isJson = false;
		if (!listProductTmps.isEmpty()){
			if (listProductTmps.get(0) instanceof String){
				isJson = true;
			}
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String)context.get("facilityId");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<Map<String, String>> listProducts = new ArrayList<Map<String, String>>();
		if (isJson){
			String stringJson = "["+(String)listProductTmps.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")){
					mapItem.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")){
					mapItem.put("quantity", item.getString("quantity"));
				}
				listProducts.add(mapItem);
			}
		} else {
			listProducts = (List<Map<String, String>>)context.get("listProducts");
		}
		List<Map<String, Object>> listProductRelateds = new ArrayList<Map<String, Object>>();
		Boolean existedNotConfig = false;
		try {
			if (!listProducts.isEmpty()){
				for (Map<String,String> item : listProducts) {
					String productId = (String)item.get("productId");
					GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					String requireAmount = objProduct.getString("requireAmount");
					BigDecimal quantity = new BigDecimal((String) item.get("quantity"));
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						quantity = BigDecimal.ONE;
					}
					List<GenericValue> listProductChilds = LogisticsProductUtil.getListProdConfigItemProduct(delegator, productId);
					if (!listProductChilds.isEmpty()){
						if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
							BigDecimal totalQuantity = new BigDecimal((String) item.get("quantity")); // like amount in amount case tracing
							BigDecimal unitAmount = LogisticsProductUtil.getProductConfigAmount(delegator, productId);
							if (unitAmount.compareTo(BigDecimal.ZERO) > 0) {
								quantity = totalQuantity.divide(unitAmount, RoundingMode.HALF_UP); 
							}
						}
						for (GenericValue pr : listProductChilds) {
							Map<String, Object> map = FastMap.newInstance();
							String requireAmontChild = pr.getString("requireAmount");
							map.put("productId", pr.getString("productId"));
							map.put("requireAmount", requireAmontChild);
							map.put("productCode", pr.getString("productCode"));
							map.put("productName", pr.getString("productName"));
							if (UtilValidate.isNotEmpty(pr.getBigDecimal("quantity"))) {
								map.put("quantity", quantity.multiply(pr.getBigDecimal("quantity")).toString());
								if (UtilValidate.isNotEmpty(pr.getBigDecimal("amount")) && UtilValidate.isNotEmpty(requireAmontChild) && "Y".equals(requireAmontChild)) {
									map.put("weight", quantity.multiply(pr.getBigDecimal("amount").multiply(pr.getBigDecimal("quantity"))).toString());
								}
							}
							map.put("quantityUomId", pr.getString("quantityUomId"));
							map.put("weightUomId", pr.getString("weightUomId"));
							List<GenericValue> listAverageCost = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(UtilMisc.toMap("productId", pr.getString("productId"),  "facilityId", facilityId, "organizationPartyId", company, "productAverageCostTypeId", "SIMPLE_AVG_COST")), null, null, null, false);
							listAverageCost = EntityUtil.filterByDate(listAverageCost);
							if (!listAverageCost.isEmpty()){
								map.put("unitCost", listAverageCost.get(0).getBigDecimal("averageCost").toString());
							} else {
								map.put("unitCost", BigDecimal.ZERO.toString());
							}
							Map<String, Object> mapInv = FastMap.newInstance();
							if (UtilValidate.isNotEmpty(facilityId)){
								Map<String, Object> mapAttr = FastMap.newInstance();
								mapAttr.put("facilityId", facilityId);
								mapAttr.put("productId", productId);
								mapInv = InventoryUtil.getDetailQuantityInventory(delegator, mapAttr);
							} else {
								Map<String, Object> mapAttr = FastMap.newInstance();
								mapAttr.put("productId", productId);
								mapInv = InventoryUtil.getDetailQuantityInventory(delegator, mapAttr);
							}
							map.put("quantityOnHandTotal", mapInv.get("quantityOnHandTotal"));
							map.put("amountOnHandTotal", mapInv.get("amountOnHandTotal"));
							map.put("availableToPromiseTotal", mapInv.get("availableToPromiseTotal"));
							listProductRelateds.add(map);
						}
					} else {
						existedNotConfig = true;
					}
				}
			}
		} catch (GenericEntityException e){
			ServiceUtil.returnError("OLBIUS: error");
		}
		successResult.put("listProductRelateds", listProductRelateds);
		successResult.put("existedNotConfig", existedNotConfig);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getProductRequiredAttributes(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			EntityCondition cond1 = EntityCondition.makeCondition("expRequired", EntityOperator.NOT_EQUAL, null);
			EntityCondition cond2 = EntityCondition.makeCondition("mnfRequired", EntityOperator.NOT_EQUAL, null);
			EntityCondition cond3 = EntityCondition.makeCondition("lotRequired", EntityOperator.NOT_EQUAL, null);
			EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2, cond3), EntityOperator.OR);
			listAllConditions.add(cond);
			listIterator = delegator.find("ProductFacilityAndProduct", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: getConfigRequiredDates error!" + e.toString();
			ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateProductRequiredAttributes(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		List<Object> mapProducts = (List<Object>) context.get("listProductIds");
		List<Object> mapFacilitys = (List<Object>) context.get("listFacilityIds");
		String expRequired = (String)context.get("expRequired");
		String mnfRequired = (String)context.get("mnfRequired");
		String lotRequired = (String)context.get("lotRequired");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			listProducts = LogisticsStringUtil.convertListJsonObjectToListMap(mapProducts, UtilMisc.toList("productId"));
		} else {
			listProducts = (List<Map<String, Object>>) context.get("listProductIds");
		}
		
		isJson = false;
		if (!mapFacilitys.isEmpty()) {
			if (mapFacilitys.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listFacilitys = new ArrayList<Map<String, Object>>();
		if (isJson) {
			listFacilitys = LogisticsStringUtil.convertListJsonObjectToListMap(mapFacilitys, UtilMisc.toList("facilityId"));
		} else {
			listFacilitys = (List<Map<String, Object>>) context.get("listFacilityIds");
		}
		
		if (!listProducts.isEmpty() && (UtilValidate.isNotEmpty(expRequired) || UtilValidate.isNotEmpty(mnfRequired) || UtilValidate.isNotEmpty(lotRequired))){
			LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> pr : listProducts) {
				for (Map<String, Object> fa : listFacilitys) {
					String productId = (String)pr.get("productId");
					String facilityId = (String)fa.get("facilityId");
					try {
						dispatcher.runSync("updateProductRequiredAttribute", UtilMisc.toMap("productId", productId, "facilityId", facilityId, "expRequired", expRequired, "mnfRequired", mnfRequired, "lotRequired", lotRequired, "userLogin", (GenericValue)context.get("userLogin")));
					} catch (GenericServiceException e){
						return ServiceUtil.returnError("OLBIUS: updateProductRequiredAttribute error!" + e.toString());
					}
				}
			}
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateProductRequiredAttribute(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator(); 
		String productId = (String)context.get("productId");
		String facilityId = (String)context.get("facilityId");
		String exp = (String)context.get("expRequired");
		String mnf = (String)context.get("mnfRequired");
		String lot = (String)context.get("lotRequired");
		
		GenericValue productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
		if (UtilValidate.isEmpty(productFacility)){
			LocalDispatcher dispatcher = ctx.getDispatcher();
			try {
				dispatcher.runSync("createProductFacility", UtilMisc.toMap("productId", productId, "facilityId", facilityId, "lastInventoryCount", BigDecimal.ZERO, "userLogin", (GenericValue)context.get("userLogin")));
				productFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createProductFacility error" + e.toString());
			}
		}
		productFacility.put("expRequired", exp);
		productFacility.put("mnfRequired", mnf);
		productFacility.put("lotRequired", lot);
		delegator.store(productFacility);
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getProductFacilitys(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		List<GenericValue> listProductFacilitys = new ArrayList<GenericValue>();
		List<Object> mapProducts = (List<Object>) context.get("listProductIds");
		List<Object> mapFacilitys = (List<Object>) context.get("listFacilityIds");
		
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			listProducts = LogisticsStringUtil.convertListJsonObjectToListMap(mapProducts, UtilMisc.toList("productId"));
		} else {
			listProducts = (List<Map<String, Object>>) context.get("listProductIds");
		}
		
		isJson = false;
		if (!mapFacilitys.isEmpty()) {
			if (mapFacilitys.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listFacilitys = new ArrayList<Map<String, Object>>();
		if (isJson) {
			listFacilitys = LogisticsStringUtil.convertListJsonObjectToListMap(mapFacilitys, UtilMisc.toList("facilityId"));
		} else {
			listFacilitys = (List<Map<String, Object>>) context.get("listFacilityIds");
		}
		
		List<String> listProductIds = new ArrayList<String>();
		List<String> listFacilityIds = new ArrayList<String>();
		
		if (!listProducts.isEmpty()){
			for (Map<String, Object> map : listProducts) {
				if (map.containsKey("productId")){
					listProductIds.add((String)map.get("productId"));
				}
			}
		}
		if (!listFacilitys.isEmpty()){
			for (Map<String, Object> map : listFacilitys) {
				if (map.containsKey("facilityId")){
					listFacilityIds.add((String)map.get("facilityId"));
				}
			}
		}
		
		try {
			EntityCondition cond1 = EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds);
			EntityCondition cond2 = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds);
			EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2), EntityOperator.AND);
			listAllConditions.add(cond);
			listProductFacilitys = delegator.findList("ProductFacilityAndProduct", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: getProductFacilitys error!" + e.toString();
			ServiceUtil.returnError(errMsg);
		}
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("listProductFacilitys", listProductFacilitys);
		return successResult;
	}
	
	//create by thanhdt
	@SuppressWarnings("unchecked")
    
    public static Map<String, Object> JQGetListRequirementItem(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
        List<Map<String, Object>> listProducts= new ArrayList<Map<String, Object>>();
        @SuppressWarnings("unused")
		String currentOrg = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String facilityId = parameters.get("facilityId")[0];
        String requirementId = parameters.get("requirementId")[0];
        
        try {
        	EntityCondition idConds = EntityCondition.makeCondition("requirementId", requirementId);
        	EntityCondition statusConds = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("REQ_CANCELLED", "REQ_REJECTED"));
        	listAllConditions.add(idConds);
        	listAllConditions.add(statusConds);
			List<GenericValue> requirementItems = delegator.findList("RequirementItemDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND) , null, listSortFields, opts, false);
			
			for(GenericValue requirementItem : requirementItems){
				HashMap<String, Object> item = new HashMap<String, Object>();
				String productId = requirementItem.getString("productId");
				item.put("productId", productId);
				item.put("sequenceId", requirementItem.getString("reqItemSeqId"));
				item.put("uomId", requirementItem.getString("quantityUomId"));
				item.put("quantity", requirementItem.getString("quantity"));
				item.put("comment", requirementItem.getString("description"));
				item.put("quantityUomId", requirementItem.getString("quantityUomId"));
				item.put("unitCost", requirementItem.getString("unitCost"));
				item.put("productCode", requirementItem.getString("productCode"));
				item.put("productName", requirementItem.getString("productName"));
				item.put("statusId", requirementItem.getString("statusId"));
				
				Map<String, Object> attributes = FastMap.newInstance();
				attributes.put("facilityId", facilityId);
				attributes.put("productId", productId);
				
				Map<String, Object> tmpResult = InventoryUtil.getDetailQuantityInventory(delegator, attributes);
				item.put("availableToPromiseTotal", (BigDecimal)tmpResult.get("availableToPromiseTotal"));
				item.put("quantityOnHandTotal", (BigDecimal)tmpResult.get("quantityOnHandTotal"));
				item.put("amountOnHandTotal", (BigDecimal)tmpResult.get("amountOnHandTotal"));
				listProducts.add(item);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        successResult.put("listIterator", listProducts);
        return successResult;
    }
}
