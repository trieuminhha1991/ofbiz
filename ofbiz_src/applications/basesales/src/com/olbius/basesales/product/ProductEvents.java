package com.olbius.basesales.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.common.util.VNCharacterUtils;

import javolution.util.FastList;
import net.sf.json.JSONArray;

public class ProductEvents {
	public static final String module = ProductEvents.class.getName();
	
	public static String findProducts(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Locale locale = UtilHttp.getLocale(request);
    	
    	String productToSearch = request.getParameter("productToSearch");
    	
    	List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();
    	if (UtilValidate.isNotEmpty(productToSearch)) {
        	List<EntityCondition> orConds = FastList.newInstance();
        	//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
        	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
        	//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
        	orConds.add(EntityCondition.makeCondition("productNameSimple", EntityOperator.LIKE, "%" + VNCharacterUtils.removeAccent(productToSearch).toUpperCase() +"%"));
        	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
        	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
        	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idSKU"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
        	
        	boolean isIdEan = false;
        	String idPLU = null;
			BigDecimal amount = null;
        	if (productToSearch.length() == 13) {
        		idPLU = ProductUtils.getPluCodeInEanId(productToSearch);
        		if (idPLU != null) { // EAN13 code
        			orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
        			isIdEan = true;
        		}
        	}/* else if (productToSearch.length() == 12) {
        		// BigDecimal modifyPrice = null; TODO allow UPCA code with price embed
        		idPLU = ProductUtils.getPluCodeInUpcId(productToSearch);
        		if (idPLU != null) {
        			// UPC-A code
        			orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
        			isIdUpc = true;
        		}
        	}*/
        	
        	EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
        	
        	List<EntityCondition> mainConds = FastList.newInstance();
        	mainConds.add(orCond);
        	mainConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), 
        			EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
        	//mainConds.add(EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"));
        	mainConds.add(EntityCondition.makeCondition("isVirtual", "N"));
        	EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
        	findOptions.setDistinct(true);
			findOptions.setMaxRows(20);
        	EntityCondition mainCond = EntityCondition.makeCondition(mainConds, EntityOperator.AND);
        	try {
        		Set<String> listSelectFields = new HashSet<String>();
        		/*listSelectFields.add("productId");
        		listSelectFields.add("productName");
        		listSelectFields.add("quantityUomId");
        		listSelectFields.add("salesUomId");
        		listSelectFields.add("isVirtual");
        		listSelectFields.add("isVariant");
        		listSelectFields.add("productCode");
        		listSelectFields.add("idSKU");
        		listSelectFields.add("idPLU");
        		listSelectFields.add("requireAmount");
        		
        		listSelectFields.add("productId");
				listSelectFields.add("productCode");
				listSelectFields.add("productName");
				listSelectFields.add("quantityUomId");
				listSelectFields.add("salesUomId");
				listSelectFields.add("uomId");
				listSelectFields.add("isVirtual");
				listSelectFields.add("isVariant");
        		*/
        		listSelectFields.add("productId");
    			listSelectFields.add("productCode");
    			listSelectFields.add("primaryProductCategoryId");
    			listSelectFields.add("productName");
    			listSelectFields.add("quantityUomId");
    			listSelectFields.add("longDescription");
    			listSelectFields.add("isVirtual");
    			listSelectFields.add("categoryName");
    			listSelectFields.add("salesDiscontinuationDate");
    			listSelectFields.add("purchaseDiscontinuationDate");
    			listSelectFields.add("idSKU");
    			listSelectFields.add("uomId");
        		List<GenericValue> productListTmp = delegator.findList("ProductAndUomAndCatePriAndGoodIdsSKU", mainCond, listSelectFields, UtilMisc.toList("productCode"), findOptions, false);
        		if (UtilValidate.isNotEmpty(productListTmp)) {
        			if (isIdEan) {
        				amount = ProductWorker.getAmountProductEan(productToSearch, delegator, locale);
						
        				/*try {
							Map<String, Object> infoAndAmountResult = dispatcher.runSync("getProductIdAndAmountByUPCA", UtilMisc.toMap("idUPCA", productToSearch));
							if (ServiceUtil.isSuccess(infoAndAmountResult)) {
		            			// modifyPrice = (BigDecimal) infoAndAmountResult.get("price");
		            			amount = (BigDecimal) infoAndAmountResult.get("amount");
		            		}
						} catch (GenericServiceException e) {
							Debug.logWarning("Error: don't get info and amount of product by UPCA code", module);
						}*/
					}
        			
        			for (GenericValue product : productListTmp) {
        				Map<String, Object> itemMap = product.getAllFields();
        				if ("Y".equals(product.getString("requireAmount")) && isIdEan) {
							//itemMap.put("amount", amount);
        					//itemMap.put("amountPrice", null);
        					itemMap.put("amountWeight", amount);
							//itemMap.put("idUPCA", productToSearch);
							itemMap.put("idEAN", productToSearch);
						}
        				//itemMap.put("packingUomIds", ProductWorker.getListQuantityUomIds(product.getString("productId"), product.getString("quantityUomId"), delegator));
        				productList.add(itemMap);
        			}
        		}
        	} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(), module);
			}
    	}
    	
		request.setAttribute("productsList", productList);
    	return "success";
    }
	
	public static String findProductsSales(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	//LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Locale locale = UtilHttp.getLocale(request);
    	
    	String productStoreId = request.getParameter("productStoreId");
    	String productToSearch = request.getParameter("productToSearch");
    	
    	List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();
    	if (UtilValidate.isNotEmpty(productStoreId) && UtilValidate.isNotEmpty(productToSearch)) {
    		if(UtilValidate.isNotEmpty(productToSearch)){
            	List<EntityCondition> orConds = FastList.newInstance();
            	//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
            	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
            	//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
            	orConds.add(EntityCondition.makeCondition("productNameSimple", EntityOperator.LIKE, "%" + VNCharacterUtils.removeAccent(productToSearch).toUpperCase() +"%"));
            	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
            	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
            	orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idSKU"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
            	
            	boolean isIdEan = false;
            	String idPLU = null;
				BigDecimal amount = null;
            	if (productToSearch.length() == 13) {
            		idPLU = ProductUtils.getPluCodeInEanId(productToSearch);
            		if (idPLU != null) { // EAN13 code
            			orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
            			isIdEan = true;
            		}
            	}/* else if (productToSearch.length() == 12) {
            		// BigDecimal modifyPrice = null; TODO allow UPCA code with price embed
            		idPLU = ProductUtils.getPluCodeInUpcId(productToSearch);
            		if (idPLU != null) {
            			// UPC-A code
            			orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
            			isIdUpc = true;
            		}
            	}*/
            	
            	EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
            	
            	List<EntityCondition> mainConds = FastList.newInstance();
            	mainConds.add(orCond);
            	mainConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), 
            			EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
            	//mainConds.add(EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"));
            	mainConds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
            	mainConds.add(EntityCondition.makeCondition("isVirtual", "N"));
            	EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            	findOptions.setDistinct(true);
				findOptions.setMaxRows(20);
            	EntityCondition mainCond = EntityCondition.makeCondition(mainConds, EntityOperator.AND);
            	try {
            		//Set<String> listSelectFields = UtilMisc.toSet("qoh", "currencyUomId", "termUomId", "price", "productId", "productName", "goodIdentificationTypeId", "productCode");
            		Set<String> listSelectFields = new HashSet<String>();
            		listSelectFields.add("productId");
            		listSelectFields.add("productName");
            		listSelectFields.add("quantityUomId");
            		listSelectFields.add("salesUomId");
            		listSelectFields.add("isVirtual");
            		listSelectFields.add("isVariant");
            		listSelectFields.add("productCode");
            		listSelectFields.add("idSKU");
            		listSelectFields.add("idPLU");
            		listSelectFields.add("requireAmount");
            		List<GenericValue> productListTmp = delegator.findList("ProductAndGoodIdsAndProductStore", mainCond, listSelectFields, UtilMisc.toList("productCode"), findOptions, false);
            		if (UtilValidate.isNotEmpty(productListTmp)) {
            			if (isIdEan) {
            				amount = ProductWorker.getAmountProductEan(productToSearch, delegator, locale);
							
            				/*try {
								Map<String, Object> infoAndAmountResult = dispatcher.runSync("getProductIdAndAmountByUPCA", UtilMisc.toMap("idUPCA", productToSearch));
								if (ServiceUtil.isSuccess(infoAndAmountResult)) {
			            			// modifyPrice = (BigDecimal) infoAndAmountResult.get("price");
			            			amount = (BigDecimal) infoAndAmountResult.get("amount");
			            		}
							} catch (GenericServiceException e) {
								Debug.logWarning("Error: don't get info and amount of product by UPCA code", module);
							}*/
						}
            			
            			for (GenericValue product : productListTmp) {
            				Map<String, Object> itemMap = product.getAllFields();
            				if ("Y".equals(product.getString("requireAmount")) && isIdEan) {
    							//itemMap.put("amount", amount);
            					//itemMap.put("amountPrice", null);
            					itemMap.put("amountWeight", amount);
    							//itemMap.put("idUPCA", productToSearch);
    							itemMap.put("idEAN", productToSearch);
							}
            				itemMap.put("packingUomIds", ProductWorker.getListQuantityUomIds(product.getString("productId"), product.getString("quantityUomId"), delegator));
            				productList.add(itemMap);
            			}
            		}
            	} catch (GenericEntityException e) {
    				Debug.logError(e.getMessage(), module);
    			}
        	}
    	}
    	
		request.setAttribute("productsList", productList);
    	return "success";
    }
	
	public static String findProductsAddToQuot(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> productList = new ArrayList<GenericValue>();
		
		try {
			List<String> prodCatalogIds = FastList.newInstance();
			
			String productStoreGroupIdsParam = request.getParameter("productStoreGroupIds");
			if (UtilValidate.isNotEmpty(productStoreGroupIdsParam) && !"null".equals(productStoreGroupIdsParam)) {
				List<String> productStoreGroupIds = FastList.newInstance();
				JSONArray jsonArray = new JSONArray();
				if(UtilValidate.isNotEmpty(productStoreGroupIdsParam)) {
					jsonArray = JSONArray.fromObject(productStoreGroupIdsParam);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						productStoreGroupIds.add(jsonArray.getString(i));
					}
				}
    			if (UtilValidate.isNotEmpty(productStoreGroupIds)) {
    				List<EntityCondition> mainConds = FastList.newInstance();
        			mainConds.add(EntityUtil.getFilterByDateExpr());
        			mainConds.add(EntityCondition.makeCondition("productStoreGroupId", EntityOperator.IN, productStoreGroupIds));
        			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreGroupMember", EntityCondition.makeCondition(mainConds), null, null, null, false), "productStoreId", true);
        			if (UtilValidate.isNotEmpty(productStoreIds)) {
        				mainConds.clear();
        				mainConds.add(EntityUtil.getFilterByDateExpr());
        				mainConds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
        				List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(mainConds), null, null, null, false), "prodCatalogId", true);
        				if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
        					prodCatalogIds.addAll(productCatalogIdsTmp);
        				}
        			}
    			}
    		}
    		
			String isSelectAllProductStore = request.getParameter("isSelectAllProductStore");
			if ("Y".equals(isSelectAllProductStore)) {
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityUtil.getFilterByDateExpr());
				prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(conds), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
			} else {
				String productStoreIdsParam = request.getParameter("productStoreIds");
				if (UtilValidate.isNotEmpty(productStoreIdsParam) && !"null".equals(productStoreIdsParam)) {
					List<String> productStoreIds = FastList.newInstance();
					JSONArray jsonArray = new JSONArray();
					if(UtilValidate.isNotEmpty(productStoreIdsParam)) {
						jsonArray = JSONArray.fromObject(productStoreIdsParam);
					}
					if (jsonArray != null && jsonArray.size() > 0) {
						for (int i = 0; i < jsonArray.size(); i++) {
							productStoreIds.add(jsonArray.getString(i));
						}
					}
					
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
					conds.add(EntityUtil.getFilterByDateExpr());
					prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(conds), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
				}
			}
			
			String productToSearch = request.getParameter("productToSearch");
			
			if (UtilValidate.isNotEmpty(prodCatalogIds) && UtilValidate.isNotEmpty(productToSearch)) {
				List<EntityCondition> orConds = FastList.newInstance();
				//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition("productNameSimple", EntityOperator.LIKE, "%" + VNCharacterUtils.removeAccent(productToSearch).toUpperCase() +"%"));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idSKU"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				
				String idPLU = null;
				if (productToSearch.length() == 13) {
					idPLU = ProductUtils.getPluCodeInEanId(productToSearch);
					if (idPLU != null) { // EAN13 code
						orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
					}
				}
				
				EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
				
				List<EntityCondition> mainConds = FastList.newInstance();
				mainConds.add(orCond);
				mainConds.add(EntityCondition.makeCondition("isVirtual", "N"));
				mainConds.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
				mainConds.add(EntityUtil.getFilterByDateExpr());
				mainConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), 
						EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
				
				EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
				findOptions.setMaxRows(20);
				EntityCondition mainCond = EntityCondition.makeCondition(mainConds, EntityOperator.AND);
					
				Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productId");
				listSelectFields.add("productCode");
				listSelectFields.add("productName");
				listSelectFields.add("quantityUomId");
				listSelectFields.add("salesUomId");
				listSelectFields.add("uomId");
				listSelectFields.add("isVirtual");
				listSelectFields.add("isVariant");
				//listSelectFields.add("idSKU");
				//listSelectFields.add("idPLU");
				productList = delegator.findList("ProductAndUomAndGoodIdsAndCatalog", mainCond, listSelectFields, UtilMisc.toList("productCode"), findOptions, false);
				if (UtilValidate.isEmpty(productList)) {
					productList = new ArrayList<GenericValue>();
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling findProductsAddToQuot event: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		request.setAttribute("productsList", productList);
		return "success";
	}
	
	public static String findProductsAddToReturn(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> productList = new ArrayList<GenericValue>();
		
		try {
			String productToSearch = request.getParameter("productToSearch");
			if (UtilValidate.isNotEmpty(productToSearch)) {
				List<EntityCondition> orConds = FastList.newInstance();
				//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition("productNameSimple", EntityOperator.LIKE, "%" + VNCharacterUtils.removeAccent(productToSearch).toUpperCase() +"%"));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idSKU"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				
				String idPLU = null;
				if (productToSearch.length() == 13) {
					idPLU = ProductUtils.getPluCodeInEanId(productToSearch);
					if (idPLU != null) { // EAN13 code
						orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
					}
				}
				
				EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
				
				List<EntityCondition> mainConds = FastList.newInstance();
				mainConds.add(orCond);
				mainConds.add(EntityCondition.makeCondition("isVirtual", "N"));
				//mainConds.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
				//mainConds.add(EntityUtil.getFilterByDateExpr());
				mainConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), 
						EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
				
				EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
				findOptions.setMaxRows(20);
				EntityCondition mainCond = EntityCondition.makeCondition(mainConds, EntityOperator.AND);
					
				Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productId");
				listSelectFields.add("productCode");
				listSelectFields.add("productName");
				listSelectFields.add("quantityUomId");
				listSelectFields.add("salesUomId");
				listSelectFields.add("uomId");
				listSelectFields.add("isVirtual");
				listSelectFields.add("isVariant");
				listSelectFields.add("taxPercentage");
				listSelectFields.add("currencyUomId");
				//listSelectFields.add("idSKU");
				//listSelectFields.add("idPLU");
				productList = delegator.findList("ProductAndGoodIds", mainCond, listSelectFields, UtilMisc.toList("productCode"), findOptions, false);
				if (UtilValidate.isEmpty(productList)) {
					productList = new ArrayList<GenericValue>();
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling findProductsAddToQuot event: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		request.setAttribute("productsList", productList);
		return "success";
	}
	
	public static String findProductsAddToExportPrice(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> productList = new ArrayList<GenericValue>();
		
		try {
			List<String> prodCatalogIds = FastList.newInstance();
			
			String productStoreId = request.getParameter("productStoreId");
			if (UtilValidate.isNotEmpty(productStoreId)) {
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				conds.add(EntityUtil.getFilterByDateExpr());
				prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(conds), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
			}
			
			String productToSearch = request.getParameter("productToSearch");
			
			if (UtilValidate.isNotEmpty(prodCatalogIds) && UtilValidate.isNotEmpty(productToSearch)) {
				List<EntityCondition> orConds = FastList.newInstance();
				//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition("productNameSimple", EntityOperator.LIKE, "%" + VNCharacterUtils.removeAccent(productToSearch).toUpperCase() +"%"));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idSKU"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				
				String idPLU = null;
				if (productToSearch.length() == 13) {
					idPLU = ProductUtils.getPluCodeInEanId(productToSearch);
					if (idPLU != null) { // EAN13 code
						orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
					}
				}
				
				EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
				
				List<EntityCondition> mainConds = FastList.newInstance();
				mainConds.add(orCond);
				mainConds.add(EntityCondition.makeCondition("isVirtual", "N"));
				mainConds.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
				mainConds.add(EntityUtil.getFilterByDateExpr());
				mainConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), 
						EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
				mainConds.add(EntityCondition.makeCondition("idSKU", EntityOperator.NOT_EQUAL, null));
				
				EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
				findOptions.setMaxRows(20);
				EntityCondition mainCond = EntityCondition.makeCondition(mainConds, EntityOperator.AND);
				
				Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productId");
				listSelectFields.add("productCode");
				listSelectFields.add("productName");
				listSelectFields.add("internalName");
				listSelectFields.add("quantityUomId");
				listSelectFields.add("salesUomId");
				listSelectFields.add("uomId");
				listSelectFields.add("isVirtual");
				listSelectFields.add("isVariant");
				listSelectFields.add("idSKU");
				listSelectFields.add("iupprm");
				//listSelectFields.add("idPLU");
				productList = delegator.findList("ProductAndUomAndGoodIdsAndCatalog", mainCond, listSelectFields, UtilMisc.toList("productCode"), findOptions, false);
				if (UtilValidate.isEmpty(productList)) {
					productList = new ArrayList<GenericValue>();
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling findProductsAddToQuot event: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		request.setAttribute("productsList", productList);
		return "success";
	}
}
