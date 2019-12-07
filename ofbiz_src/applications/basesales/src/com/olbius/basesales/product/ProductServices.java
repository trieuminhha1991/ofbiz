package com.olbius.basesales.product;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.shoppingcart.ShoppingCartWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;

public class ProductServices {
	public static final String module = ProductServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCategoryByCatalog(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
    		String prodCatalogId = SalesUtil.getParameter(parameters, "prodCatalogId");
			String showAllStr = SalesUtil.getParameter(parameters, "showAll");
			boolean showAll = false;
			if ("Y".equals(showAllStr)) showAll = true;
			String searchKey = SalesUtil.getParameter(parameters, "searchKey");
			if (searchKey != null) {
				List<EntityCondition> condsOr = new ArrayList<EntityCondition>();
				condsOr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("categoryName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + searchKey + "%")));
				condsOr.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCategoryId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + searchKey + "%")));
				listAllConditions.add(EntityCondition.makeCondition(condsOr, EntityOperator.OR));
			}
			
    		List<String> productCategoryIds = FastList.newInstance();
    		List<String> prodCatalogIds = FastList.newInstance();
    		if (showAll) {
    			List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProdCatalog", null, null, false), "prodCatalogId", true);
				if (UtilValidate.isNotEmpty(prodCatalogIdsTmp)) prodCatalogIds.addAll(prodCatalogIdsTmp);
    		} else {
    			if (UtilValidate.isNotEmpty(productStoreId)) {
        			prodCatalogIds = EntityUtil.getFieldListFromEntityList(CatalogWorker.getStoreCatalogs(delegator, productStoreId), "prodCatalogId", true);
        		} else if (UtilValidate.isNotEmpty(prodCatalogId)) {
        			prodCatalogIds.add(prodCatalogId);
        		}
    		}
    		String productCategoryTypeId = "CATALOG_CATEGORY";
    		if (prodCatalogIds != null) {
				for (String prodCatalogIdItem : prodCatalogIds) {
					List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
					if (listCategory != null) {
						for (GenericValue categoryItem : listCategory) {
							productCategoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), productCategoryTypeId));
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(productCategoryIds)) {
				listAllConditions.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
				listAllConditions.add(EntityCondition.makeCondition("productCategoryTypeId", productCategoryTypeId));
				listIterator = delegator.find("ProductCategory", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByCatalogAndStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    /* Use entity temporary "ProductAndCatalogTempData" for get product from catalogs */
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductByStoreOrCatalog(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Locale locale = (Locale) context.get("locale");
    	HttpServletRequest request = (HttpServletRequest) context.get("request");
    	try {
    		boolean hasVirtualProd = false;
    		boolean showAll = false;
    		String[] productStoreId = null;
    		String[] prodCatalogId = null;
    		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
    			productStoreId = parameters.get("productStoreId");
    		}
    		if (parameters.containsKey("prodCatalogId") && parameters.get("prodCatalogId").length > 0) {
    			prodCatalogId = parameters.get("prodCatalogId");
    		}
    		
    		String showAllStr = SalesUtil.getParameter(parameters, "showAll");
			if ("Y".equals(showAllStr)) showAll = true;
			
			String hasVirtualProdStr = SalesUtil.getParameter(parameters, "hasVirtualProd");
			if ("Y".equals(hasVirtualProdStr)) hasVirtualProd = true;
			
			String salesOnlyStr = SalesUtil.getParameter(parameters, "salesOnly");
			boolean salesOnly = "N".equals(salesOnlyStr) ? false : true;
			
    		List<String> prodCatalogIds = FastList.newInstance();
    		if (showAll) {
    			List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProdCatalog", null, null, false), "prodCatalogId", true);
				if (UtilValidate.isNotEmpty(prodCatalogIdsTmp)) prodCatalogIds.addAll(prodCatalogIdsTmp);
    		} else {
    			if (UtilValidate.isNotEmpty(productStoreId)) {
        			for (int i = 0; i < productStoreId.length; i++) {
        				List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(CatalogWorker.getStoreCatalogs(delegator, productStoreId[i]), "prodCatalogId", true);
        				if (prodCatalogIdsTmp != null) prodCatalogIds.addAll(prodCatalogIdsTmp);
        			}
        		} else if (UtilValidate.isNotEmpty(prodCatalogId)) {
        			for (int i = 0; i < prodCatalogId.length; i++) {
        				prodCatalogIds.add(prodCatalogId[i]);
    				}
        		}
    		}
    		if (prodCatalogIds != null) {
				List<EntityCondition> mainCondList = FastList.newInstance();
                mainCondList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
                if (!hasVirtualProd) mainCondList.add(EntityCondition.makeCondition("isVirtual", "N"));
                
                boolean activeOnly = true;
                Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
                Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
                Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                
                if (activeOnly) {
                    mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                }
                if (introductionDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                }
                if (releaseDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                }
                
                // check product has is_virtual = "N" or NULL
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVirtual", "N"), EntityOperator.OR, EntityCondition.makeCondition("isVirtual", null)));
                
                if (salesOnly) {
                	mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                }
                
                EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);

                /* OLD
                // do not include configurable products
				conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGREGATED"));
				conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGREGATED_SERVICE"));
				conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGR_DIGSERV"));
                */
                
                opts.setDistinct(true);
                Set<String> selectFields = FastSet.newInstance();
                selectFields.add("productId");
                selectFields.add("productName");
                selectFields.add("internalName");
                selectFields.add("quantityUomId");
                selectFields.add("salesUomId");
                selectFields.add("isVirtual");
                selectFields.add("isVariant");
                selectFields.add("productCode");
                //selectFields.add("sequenceNum");
                
                listAllConditions.add(mainCond);
                listSortFields.add("productCode");
                List<GenericValue> listProduct = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductAndCatalogTempDataDetail", 
                		EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
                if (UtilValidate.isNotEmpty(listProduct)) {
                	for (GenericValue itemProd : listProduct) {
                		GenericValue itemProduct = delegator.findOne("Product", UtilMisc.toMap("productId", itemProd.getString("productId")), false);
                		if ("Y".equals(itemProduct.getString("isVariant"))) {
                			GenericValue parentProduct = ProductWorker.getParentProduct(itemProd.getString("productId"), delegator, null);
                			String colorCode = null;
                			if (parentProduct != null) {
                    			String attrName = EntityUtilProperties.getPropertyValue("basesales.properties", "productAttrName.displayColor", delegator);
                    			if (UtilValidate.isNotEmpty(attrName)) {
                    				List<GenericValue> productAttrs = parentProduct.getRelated("ProductAttribute", UtilMisc.toMap("attrName", attrName), null, true);
                        			if (productAttrs != null && productAttrs.size() > 0) {
                        				GenericValue productAttr = productAttrs.get(0);
                        				if (productAttr != null) colorCode = productAttr.getString("attrValue");
                        			}
                    			}
                			}
                			//if (hasVirtualProd) {
                			//	Map<String, Object> tmp = ProductWorker.processGeneralProd(delegator, locale, parentProduct, null, null, true, true);
                			//	if (UtilValidate.isNotEmpty(tmp.get("salesUomId"))) tmp.put("quantityUomId", tmp.get("salesUomId"));
                			//	listIterator.add(tmp);
                			//}
                			Map<String, Object> tmp2 = ProductWorker.processGeneralProd(delegator, locale, itemProduct, parentProduct.getString("productId"), colorCode, true, true);
            				if (UtilValidate.isNotEmpty(tmp2.get("salesUomId"))) tmp2.put("quantityUomId", tmp2.get("salesUomId"));
                			listIterator.add(tmp2);
                		} else {
            				Map<String, Object> tmp2 = ProductWorker.processGeneralProd(delegator, locale, itemProduct, null, null, true, true);
            				if (UtilValidate.isNotEmpty(tmp2.get("salesUomId"))) tmp2.put("quantityUomId", tmp2.get("salesUomId"));
                			listIterator.add(tmp2);
                		}
					}
                }
			}
    		
    		if (request != null) {
    			HttpSession session = request.getSession();
				ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
				if (cart != null) {
	    			if (UtilValidate.isNotEmpty(listIterator)) {
	    				Map<String, Object> productQuantitiesMap = FastMap.newInstance();
	            		Map<String, Object> productUomIdsMap = FastMap.newInstance();
    					List<GenericValue> orderItems = cart.makeOrderItems();
    					if (orderItems != null && !orderItems.isEmpty()) {
    						for (GenericValue orderItem : orderItems) {
    							if (orderItem.get("productId") != null && (orderItem.get("isPromo") == null || "N".equals(orderItem.get("isPromo")))) {
    								productQuantitiesMap.put(orderItem.getString("productId"), orderItem.get("quantity"));
    								productUomIdsMap.put(orderItem.getString("productId"), orderItem.get("quantityUomId"));
    							}
    						}
    						if (productQuantitiesMap.size() > 0) {
    							for (Map<String, Object> item : listIterator) {
    								if (productQuantitiesMap.containsKey(item.get("productId"))) {
    									item.put("quantity", productQuantitiesMap.get(item.get("productId")));
    								}
    								if (productUomIdsMap.containsKey(item.get("productId"))) {
    									item.put("quantityUomId", productUomIdsMap.get(item.get("productId")));
    								}
    							}
    						}
    					}
    				} else {
    					// screen product search split
    					List<Map<String, Object>> orderItems = ShoppingCartWorker.getOrderItemsInfo(cart);
    					if (orderItems != null && !orderItems.isEmpty()) {
    						for (Map<String, Object> orderItem : orderItems) {
    							GenericValue itemProduct = delegator.findOne("Product", UtilMisc.toMap("productId", orderItem.get("productId")), false);
    							if (itemProduct != null) {
    								Map<String, Object> tmp = ProductWorker.processGeneralProd(delegator, locale, itemProduct, null, null, true, true);
                    				tmp.put("quantityUomId", orderItem.get("quantityUomId"));
                    				tmp.put("quantity", orderItem.get("quantity"));
                    				//tmp.put("idUPCA", orderItem.get("idUPCA"));
                    				tmp.put("idEAN", orderItem.get("idEAN"));
                        			listIterator.add(tmp);
    							}
    						}
    						int totalRows = listIterator.size(); 
    						successResult.put("TotalRows", String.valueOf(totalRows));
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByCatalogAndStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }

    /* Use entity activity for get product from catalogs */
    /*@Deprecated
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductByStoreOrCatalogOld(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Locale locale = (Locale) context.get("locale");
    	HttpServletRequest request = (HttpServletRequest) context.get("request");
    	try {
    		boolean hasVirtualProd = false;
    		String hasVirtualProdStr = "";
    		String[] productStoreId = null;
    		String[] prodCatalogId = null;
    		boolean showAll = false;
    		if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
    			productStoreId = parameters.get("productStoreId");
    		}
    		if (parameters.containsKey("prodCatalogId") && parameters.get("prodCatalogId").length > 0) {
    			prodCatalogId = parameters.get("prodCatalogId");
    		}
    		if (parameters.containsKey("showAll") && parameters.get("showAll").length > 0) {
    			String showAllStr = parameters.get("showAll")[0];
    			if ("Y".equals(showAllStr)) showAll = true;
    		}
    		if (parameters.containsKey("hasVirtualProd") && parameters.get("hasVirtualProd").length > 0) {
    			hasVirtualProdStr = parameters.get("hasVirtualProd")[0];
    			if ("Y".equals(hasVirtualProdStr)) hasVirtualProd = true;
    		}
    		List<String> productCategoryIds = FastList.newInstance();
    		List<String> prodCatalogIds = FastList.newInstance();
    		if (showAll) {
    			List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProdCatalog", null, null, false), "prodCatalogId", true);
				if (UtilValidate.isNotEmpty(prodCatalogIdsTmp)) prodCatalogIds.addAll(prodCatalogIdsTmp);
    		} else {
    			if (UtilValidate.isNotEmpty(productStoreId)) {
        			for (int i = 0; i < productStoreId.length; i++) {
        				List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(CatalogWorker.getStoreCatalogs(delegator, productStoreId[i]), "prodCatalogId", true);
        				if (prodCatalogIdsTmp != null) prodCatalogIds.addAll(prodCatalogIdsTmp);
        			}
        		} else if (UtilValidate.isNotEmpty(prodCatalogId)) {
        			for (int i = 0; i < prodCatalogId.length; i++) {
        				prodCatalogIds.add(prodCatalogId[i]);
    				}
        		}
    		}
    		if (prodCatalogIds != null) {
				for (String prodCatalogIdItem : prodCatalogIds) {
					List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
					if (listCategory != null) {
						for (GenericValue categoryItem : listCategory) {
							productCategoryIds.addAll(SalesUtil.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId")));
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(productCategoryIds)) {
				List<EntityCondition> mainCondList = FastList.newInstance();
                mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
                boolean activeOnly = true;
                Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
                Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
                Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                
                if (activeOnly) {
                    mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                }
                if (introductionDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                }
                if (releaseDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                }
                
                // check product has is_variant = "N" or NULL
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVariant", "N"), EntityOperator.OR, EntityCondition.makeCondition("isVariant", null)));
                
                EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);

                // set distinct on
                //EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
                //findOpts.setMaxRows(highIndex);
                
                // using list iterator
                //listAllConditions.add(mainCond);
                //EntityListIterator pli = delegator.find("ProductAndCategoryMember", mainCond, null, null, listSortFields, opts);
                //List<GenericValue> listProduct = SalesUtil.processIterator(pli, parameters, successResult);
                
                 OLD
                // do not include configurable products
				conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGREGATED"));
				conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGREGATED_SERVICE"));
				conditionList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, "AGGR_DIGSERV"));
                
                
                // List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
                // Cach 1: List Iterator result
                listAllConditions.add(mainCond);
                EntityListIterator pli = delegator.find("ProductAndCategoryMember", mainCond, null, null, listSortFields, opts);
                List<GenericValue> listProduct = SalesUtil.processIterator(pli, parameters, successResult);
                
                 Cach 2: List Map result 
                opts.setDistinct(true);
                Set<String> selectFields = FastSet.newInstance();
                selectFields.add("productId");
                selectFields.add("productName");
                selectFields.add("internalName");
                selectFields.add("quantityUomId");
                selectFields.add("isVirtual");
                selectFields.add("isVariant");
                selectFields.add("productCode");
                selectFields.add("sequenceNum");
                
                List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, selectFields, UtilMisc.toList("sequenceNum"), opts, false);
                if (listProduct != null) {
                	for (GenericValue itemProd : listProduct) {
                		GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
                		if ("Y".equals(itemProduct.getString("isVirtual"))) {
                			String colorCode = null;
                			String attrName = EntityUtilProperties.getPropertyValue("basesales.properties", "productAttrName.displayColor", delegator);
                			if (UtilValidate.isNotEmpty(attrName)) {
                				List<GenericValue> productAttrs = itemProduct.getRelated("ProductAttribute", UtilMisc.toMap("attrName", attrName), null, true);
                    			if (productAttrs != null && productAttrs.size() > 0) {
                    				GenericValue productAttr = productAttrs.get(0);
                    				if (productAttr != null) colorCode = productAttr.getString("attrValue");
                    			}
                			}
                			if (hasVirtualProd) listIterator.add(ProductWorker.processGeneralProd(delegator, locale, itemProduct, null, null, true, true));
                			List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
                			if (listVariantProductAssoc != null){
                				for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
                					GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
                					listIterator.add(ProductWorker.processGeneralProd(delegator, locale, itemVariantProduct, itemProduct.getString("productId"), colorCode, true, true));
                				}
                			}
                		} else {
                			listIterator.add(ProductWorker.processGeneralProd(delegator, locale, itemProduct, null, null, true, true));
                		}
					}
                }
                listIterator = EntityMiscUtil.filterMap(listIterator, listAllConditions);
        		listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
        		
        		Map<String, Object> productQuantitiesMap = FastMap.newInstance();
        		Map<String, Object> productUomIdsMap = FastMap.newInstance();
        		if (UtilValidate.isNotEmpty(listIterator) && request != null) {
    				HttpSession session = request.getSession();
					ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
					if (cart != null) {
						List<GenericValue> orderItems = cart.makeOrderItems();
						if (orderItems != null) {
							for (GenericValue orderItem : orderItems) {
								if (orderItem.get("productId") != null && (orderItem.get("isPromo") == null || "N".equals(orderItem.get("isPromo")))) {
									productQuantitiesMap.put(orderItem.getString("productId"), orderItem.get("quantity"));
									productUomIdsMap.put(orderItem.getString("productId"), orderItem.get("quantityUomId"));
								}
							}
							if (productQuantitiesMap.size() > 0) {
								for (Map<String, Object> item : listIterator) {
    								if (productQuantitiesMap.containsKey(item.get("productId"))) {
    									item.put("quantity", productQuantitiesMap.get(item.get("productId")));
    								}
    								if (productUomIdsMap.containsKey(item.get("productId"))) {
    									item.put("quantityUomId", productUomIdsMap.get(item.get("productId")));
    								}
								}
							}
						}
					}
        		}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByCatalogAndStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }*/
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductSellAll(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
    	String TotalRows = null;
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
            selectFields.add("productNameSearch");
            
            listAllConditions.add(EntityUtil.getFilterByDateExpr());
            
            //listSortFields.add("sequenceNum");
            listSortFields.add("productCode");
            listIterator = delegator.findList("ProductAndCatalogTempDataDetailSearch", EntityCondition.makeCondition(listAllConditions), selectFields, listSortFields, opts, false);
            TotalRows = String.valueOf(listIterator.size());
            
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductSellAll service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
		successResult.put("TotalRows", TotalRows);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductOfCompany(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String hasVirtualProdStr = SalesUtil.getParameter(parameters, "hasVirtualProd");
    		boolean hasVirtualProd = false;
    		if ("Y".equals(hasVirtualProdStr)) hasVirtualProd = true;
    		String productTypeId = SalesUtil.getParameter(parameters, "productTypeId");
    		if (UtilValidate.isNotEmpty(productTypeId)) listAllConditions.add(EntityCondition.makeCondition("productTypeId", productTypeId)); 
    		
    		String entityName = "Product";
    		String searchKey = SalesUtil.getParameter(parameters, "searchKey");
			if (searchKey != null) {
				entityName = "ProductDetailSearch";
				listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productNameSearch"), EntityOperator.LIKE, EntityFunction.UPPER("%" + searchKey + "%")));
			}
    		
            //opts.setDistinct(true);
            if (!hasVirtualProd) {
            	listAllConditions.add(EntityCondition.makeCondition("isVirtual", "N"));
            }
            listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("numRival", EntityOperator.EQUALS, null), 
            		EntityOperator.OR, EntityCondition.makeCondition("numRival", EntityOperator.NOT_EQUAL, "1")));
            //listIterator = delegator.find(entityName, EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, entityName, EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductOfCompany service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductAddCateMember(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String hasVirtualProdStr = SalesUtil.getParameter(parameters, "hasVirtualProd");
			boolean hasVirtualProd = false;
			if ("Y".equals(hasVirtualProdStr)) hasVirtualProd = true;
			String productTypeId = SalesUtil.getParameter(parameters, "productTypeId");
			if (UtilValidate.isNotEmpty(productTypeId)) listAllConditions.add(EntityCondition.makeCondition("productTypeId", productTypeId)); 
			
			String entityName = "Product";
			String searchKey = SalesUtil.getParameter(parameters, "searchKey");
			if (searchKey != null) {
				entityName = "ProductDetailSearch";
				listAllConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productNameSearch"), EntityOperator.LIKE, EntityFunction.UPPER("%" + searchKey + "%")));
			}
			
			String currentCategoryId = SalesUtil.getParameter(parameters, "currentCategoryId");
			if (currentCategoryId != null) {
				List<String> productCategoryIds = ProductWorker.getAllCategoryTree(delegator, currentCategoryId, "CATALOG_CATEGORY");
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("primaryProductCategoryId", null), 
										EntityOperator.OR, EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_IN, productCategoryIds)));
			}
			
			//opts.setDistinct(true);
			if (!hasVirtualProd) {
				listAllConditions.add(EntityCondition.makeCondition("isVirtual", "N"));
			}
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("numRival", EntityOperator.EQUALS, null), 
										EntityOperator.OR, EntityCondition.makeCondition("numRival", EntityOperator.NOT_EQUAL, "1")));
			listIterator = delegator.find(entityName, EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductAddCateMember service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductAll(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String isVirtual = SalesUtil.getParameter(parameters, "isVirtual");
			String isVariant = SalesUtil.getParameter(parameters, "isVariant");
			
			if (isVirtual != null) {
				listAllConditions.add(EntityCondition.makeCondition("isVirtual", isVirtual));
			}
			if (isVariant != null) {
				listAllConditions.add(EntityCondition.makeCondition("isVariant", isVariant));
			}
			
			Set<String> selectFields = FastSet.newInstance();
			selectFields.add("productId");
			selectFields.add("productCode");
			selectFields.add("internalName");
			selectFields.add("productName");
			selectFields.add("quantityUomId");
			selectFields.add("isVirtual");
			selectFields.add("isVariant");
			selectFields.add("primaryProductCategoryId");
			
			listIterator = delegator.find("Product", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductAll service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> searchProducts(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
			List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> listSortFields = FastList.newInstance();
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Locale locale = (Locale) context.get("locale");
		String query = (String) context.get("query");
		if(UtilValidate.isNotEmpty(query)){
			query = "%" + query + "%";
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productName", EntityOperator.LIKE, query),
												  EntityCondition.makeCondition("productId", EntityOperator.LIKE, query)), EntityOperator.OR));
		}
		try {
			boolean hasVirtualProd = false;
			List<String> productCategoryIds = FastList.newInstance();
			List<String> prodCatalogIds = FastList.newInstance();
			List<GenericValue> tmpCatalog = delegator.findList("ProdCatalog", null, null, null, null, false);
			for(GenericValue e : tmpCatalog){
				String ct = e.getString("prodCatalogId");
				prodCatalogIds.add(ct);
			}
			if (prodCatalogIds != null) {
					for (String prodCatalogIdItem : prodCatalogIds) {
						List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
						if (listCategory != null) {
							for (GenericValue categoryItem : listCategory) {
								productCategoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
							}
						}
					}
				}
				if (UtilValidate.isNotEmpty(productCategoryIds)) {
					List<EntityCondition> mainCondList = FastList.newInstance();
	                mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
	                boolean activeOnly = true;
	                Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
	                Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
	                Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	
	                if (activeOnly) {
	                    mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
	                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
	                }
	                if (introductionDateLimit != null) {
	                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
	                }
	                if (releaseDateLimit != null) {
	                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
	                }
	                EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
	
	                List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, null, null, opts, false);
	                if (listProduct != null) {
				for (GenericValue itemProd : listProduct) {
					GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
					if ("Y".equals(itemProduct.getString("isVirtual"))) {
						String colorCode = null;
						String attrName = EntityUtilProperties.getPropertyValue("basesales.properties", "productAttrName.displayColor", delegator);
						if (UtilValidate.isNotEmpty(attrName)) {
							List<GenericValue> productAttrs = itemProduct.getRelated("ProductAttribute", UtilMisc.toMap("attrName", attrName), null, true);
						if (productAttrs != null && productAttrs.size() > 0) {
							GenericValue productAttr = productAttrs.get(0);
							if (productAttr != null) colorCode = productAttr.getString("attrValue");
						}
						}
						if (hasVirtualProd) listIterator.add(ProductWorker.processGeneralProd(delegator, locale, itemProduct, null, null, true, true));
						List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
						if (listVariantProductAssoc != null){
							for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
								GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
								listIterator.add(ProductWorker.processGeneralProd(delegator, locale, itemVariantProduct, itemProduct.getString("productId"), colorCode, true, true));
							}
						}
					} else {
						listIterator.add(ProductWorker.processGeneralProd(delegator, locale, itemProduct, null, null, true, true));
					}
						}
	                }
	                listIterator = EntityMiscUtil.filterMap(listIterator, listAllConditions);
				listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		successResult.put("results", listIterator);
		return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductAndTaxByCatalog(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		listAllConditions.add(EntityCondition.makeCondition("salesDiscontinuationDate" , EntityOperator.EQUALS, null));
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listIterator = null;
    	try {
    		List<String> prodCatalogIds = FastList.newInstance();
    		String prodCatalogId = null;
    		if (parameters.containsKey("prodCatalogId") && parameters.get("prodCatalogId").length > 0) {
    			prodCatalogId = parameters.get("prodCatalogId")[0];
    			if (UtilValidate.isNotEmpty(prodCatalogIds)) prodCatalogIds.add(prodCatalogId);
    		}
    		if (parameters.containsKey("showAll") && parameters.get("showAll").length > 0) {
    			String showAll = parameters.get("showAll")[0];
    			if ("Y".equals(showAll)) {
    				List<String> prodCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProdCatalog", null, null, false), "prodCatalogId", true);
    				if (UtilValidate.isNotEmpty(prodCatalogIdsTmp)) prodCatalogIds.addAll(prodCatalogIdsTmp);
    			}
    		}
    		if (parameters.containsKey("channelEnumId") && parameters.get("channelEnumId").length > 0) {
    			String channelEnumId = parameters.get("channelEnumId")[0];
    			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductStore", UtilMisc.toMap("salesMethodChannelEnumId", channelEnumId), null, false), "productStoreId", true);
    			if (UtilValidate.isNotEmpty(productStoreIds)) {
    				List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds), null, null, null, false), "prodCatalogId", true);
    				if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
    					prodCatalogIds.addAll(productCatalogIdsTmp);
    				}
    			}
    		}
    		if (parameters.containsKey("productStoreGroupIds") && parameters.get("productStoreGroupIds").length > 0) {
    			String[] productStoreGroupIdsStr = parameters.get("productStoreGroupIds");
    			List<String> productStoreGroupIds = Arrays.asList(productStoreGroupIdsStr);
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
    		
    		String isSelectAllProductStore = SalesUtil.getParameter(parameters, "isSelectAllProductStore");
    		if ("Y".equals(isSelectAllProductStore)) {
    			List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityUtil.getFilterByDateExpr(), null, null, null, false), "prodCatalogId", true);
				if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
					prodCatalogIds.addAll(productCatalogIdsTmp);
				}
    		} else {
    			if (parameters.containsKey("productStoreIds") && parameters.get("productStoreIds").length > 0) {
        			String[] productStoreIdsStr = parameters.get("productStoreIds");
        			List<String> productStoreIds = Arrays.asList(productStoreIdsStr);
        			if (UtilValidate.isNotEmpty(productStoreIds)) {
        				List<EntityCondition> mainConds = FastList.newInstance();
            			mainConds.add(EntityUtil.getFilterByDateExpr());
            			mainConds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
        				List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(mainConds), null, null, null, false), "prodCatalogId", true);
        				if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
        					prodCatalogIds.addAll(productCatalogIdsTmp);
        				}
        			}
        		}
    		}
    		
    		if (UtilValidate.isNotEmpty(prodCatalogIds)) {
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("productCode");
    			}
    			
				Map<String, Object> productResult = ProductWorker.getListProductAndTaxByCatalogAndPeriod(delegator, locale, prodCatalogIds, true, null, null, null, parameters, listSortFields, listAllConditions, dispatcher);
				if (productResult != null) {
					successResult.put("TotalRows", productResult.get("TotalRows"));
					listIterator = (List<Map<String, Object>>) productResult.get("listIterator");
				}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductAndTaxByCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListAllProductAndTaxAndPrices(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		//LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		try {
			boolean activeIsNowTimestamp = true;
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("productCode");
			}
			
			List<EntityCondition> mainCondList = FastList.newInstance();
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			}
			
			/*
			 else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
				}
			}
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			 */
			
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("currencyUomId");
            //selectFields.add("taxAuthPartyId");
            //selectFields.add("taxAuthGeoId");
            selectFields.add("unitUomId");
            selectFields.add("unitPrice");
            selectFields.add("unitDefaultPrice");
            selectFields.add("unitListPrice");
            //selectFields.add("salesUomId");
            //selectFields.add("salesPrice");
            //selectFields.add("taxPercentage");
            //selectFields.add("taxAuthPartyId");
            //selectFields.add("taxAuthGeoId");
            
            //EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            EntityListIterator iterator = delegator.find("ProductAndCatalogTempDataMore", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, successResult);
            
            /*if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			//itemMap.put("packingUomIds", getListQuantityUomIds(itemProd.getString("productId"), itemProd.getString("quantityUomId"), delegator, dispatcher));
        			listIterator.add(itemMap);
				}
            }*/
            if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			
        			/*Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", itemProd.get("productId"), "productStoreId", null,
    	        			"quantityUomId", itemProd.get("quantityUomId"));
    	        	Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
        			if (ServiceUtil.isSuccess(resultValue)) {
        				//BigDecimal taxPercentage = itemProd.getBigDecimal("taxPercentage");
        				BigDecimal basePrice = (BigDecimal) resultValue.get("basePrice");
        				BigDecimal listPrice = (BigDecimal) resultValue.get("listPrice");
        				
        				itemMap.put("price", basePrice);
        				itemMap.put("listPrice", listPrice);
        			}*/
        			
        			listIterator.add(itemMap);
				}
            }
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAllProductAndTaxAndPrices service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	/** Function "Bao gia"
	 * Copy from jqFindProductPriceAndTax method
	 * @param ctx
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqFindProductPriceQuotes(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listIterator = null;
		try {
			List<String> prodCatalogIds = FastList.newInstance();
			
			//String roleTypeIds = SalesUtil.getParameter(parameters, "roleTypeIds");
			//String salesMethodChannelEnumId = SalesUtil.getParameter(parameters, "salesMethodChannelEnumId");
			
			String partyId = SalesUtil.getParameter(parameters, "partyId");
			boolean hasVirtualProd = "Y".equals(SalesUtil.getParameter(parameters, "hasVirtualProd")) ? true : false;
			
			String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
			if (UtilValidate.isNotEmpty(productStoreId)) {
				List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("productStoreId", productStoreId), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
				if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
					prodCatalogIds.addAll(productCatalogIdsTmp);
				}
			}
			
			if (UtilValidate.isNotEmpty(prodCatalogIds)) {
				Map<String, Object> productResult = ProductWorker.findProductPriceQuotation(delegator, dispatcher, locale, listSortFields, listAllConditions, parameters, prodCatalogIds, 
						true, null, null, null, productStoreId, partyId, hasVirtualProd);
				if (productResult != null) {
					successResult.put("TotalRows", productResult.get("TotalRows"));
					listIterator = (List<Map<String, Object>>) productResult.get("listIterator");
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqFindProductPriceQuotes service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> jqFindProductPriceAndTax(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listIterator = null;
		try {
			//String roleTypeIdsStr = null;
			//if (parameters.containsKey("roleTypeIds") && parameters.get("roleTypeIds").length > 0) {
			//	roleTypeIdsStr = parameters.get("roleTypeIds")[0];
			//}
			//
			//List<String> roleTypeIds = new ArrayList<String>();
			//if (roleTypeIdsStr != null) {
			//	JSONArray jsonArray = new JSONArray();
			//	if (UtilValidate.isNotEmpty(roleTypeIdsStr)) {
			//		jsonArray = JSONArray.fromObject(roleTypeIdsStr);
			//	}
			//	if (jsonArray != null && jsonArray.size() > 0) {
			//		for (int i = 0; i < jsonArray.size(); i++) {
			//			roleTypeIds.add(jsonArray.getString(i));
			//		}
			//	}
			//}
			
			String roleTypeId = null;
			if (parameters.containsKey("roleTypeId") && parameters.get("roleTypeId").length > 0) {
				roleTypeId = parameters.get("roleTypeId")[0];
			}
			
			String partyId = null;
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				partyId = parameters.get("partyId")[0];
			}
			boolean hasVirtualProd = false;
			if (parameters.containsKey("hasVirtualProd") && parameters.get("hasVirtualProd").length > 0) {
    			String hasVirtualProdStr = parameters.get("hasVirtualProd")[0];
    			if ("Y".equals(hasVirtualProdStr)) hasVirtualProd = true;
    		}
			
			List<String> prodCatalogIds = FastList.newInstance();
			String prodCatalogId = null;
			if (parameters.containsKey("prodCatalogId") && parameters.get("prodCatalogId").length > 0) {
				prodCatalogId = parameters.get("prodCatalogId")[0];
				if (UtilValidate.isNotEmpty(prodCatalogIds)) prodCatalogIds.add(prodCatalogId);
			}
			
			List<String> productStoreIds = null;
			
			String productStoreId = null;
			if (parameters.containsKey("productStoreId") && parameters.get("productStoreId").length > 0) {
				productStoreId = parameters.get("productStoreId")[0];
			}
			if (productStoreId != null) {
				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
				if (productStore != null) {
					productStoreIds = new ArrayList<String>();
					prodCatalogIds = new ArrayList<String>();
					
					productStoreIds.add(productStore.getString("productStoreId"));
					if (UtilValidate.isNotEmpty(productStoreIds)) {
						List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds), null, null, null, false), "prodCatalogId", true);
						if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
							prodCatalogIds.addAll(productCatalogIdsTmp);
						}
					}
				}
			} else {
				if (parameters.containsKey("salesMethodChannelEnumId") && parameters.get("salesMethodChannelEnumId").length > 0) {
					String salesMethodChannelEnumId = parameters.get("salesMethodChannelEnumId")[0];
					productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductStore", UtilMisc.toMap("salesMethodChannelEnumId", salesMethodChannelEnumId), null, false), "productStoreId", true);
					if (UtilValidate.isNotEmpty(productStoreIds)) {
						List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds), null, null, null, false), "prodCatalogId", true);
						if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
							prodCatalogIds.addAll(productCatalogIdsTmp);
						}
					}
				}
			}
			
			if (UtilValidate.isNotEmpty(prodCatalogIds)) {
				//listIterator = ProductWorker.findProductPriceAndTax(delegator, dispatcher, locale, prodCatalogIds, true, null, null, null, productStoreIds, partyId, hasVirtualProd);
				//listIterator = EntityMiscUtil.filterMap(listIterator, listAllConditions);
				//listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
				
				Map<String, Object> productResult = ProductWorker.findProductPriceAndTax(delegator, dispatcher, locale, prodCatalogIds, 
						true, null, null, null, productStoreIds, partyId, hasVirtualProd, parameters, listSortFields, listAllConditions);
				if (productResult != null) {
					successResult.put("TotalRows", productResult.get("TotalRows"));
					listIterator = (List<Map<String, Object>>) productResult.get("listIterator");
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqFindProductPriceAndTax service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}*/
	
	
	@SuppressWarnings("unused")
	public static Map<String, Object> getListCatalogAndCategory(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = FastList.newInstance();
    	List<Map<String, Object>> listTreeResult = new ArrayList<Map<String,Object>>();
    	Set<String> listSortFields = FastSet.newInstance();
    	try {
    		EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			
    		List<Map<String, Object>> listLoading = new ArrayList<Map<String,Object>>();
    		Map<String, Object> itemLoading = FastMap.newInstance();
    		itemLoading.put("label", UtilProperties.getMessage(resource, "BSLoading", locale) + "...");
    		listLoading.add(itemLoading);
    		List<GenericValue> listCatalog = delegator.findByAnd("ProdCatalog", null, null, false);
    		if (listCatalog != null) {
    			boolean hasChild;
    			for (GenericValue catalogItem : listCatalog) {
    				hasChild = false;
    				String prodCatalogId = catalogItem.getString("prodCatalogId");
    				String treeId = "CLOG_" + prodCatalogId;
					Map<String, Object> treeItem = FastMap.newInstance();
					treeItem.put("id", treeId);
					treeItem.put("parentId", "-1");
					treeItem.put("dataType", "CATALOG");
					treeItem.put("label", catalogItem.getString("catalogName"));
					treeItem.put("value", prodCatalogId);
					treeItem.put("expanded", true);
					
					List<String> listCategoryId = EntityUtil.getFieldListFromEntityList(CatalogWorker.getProdCatalogCategories(delegator, prodCatalogId, "PCCT_BROWSE_ROOT"), "productCategoryId", true);
					if (UtilValidate.isNotEmpty(listCategoryId)) {
						List<GenericValue> listCategoryRollup = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.IN, listCategoryId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, opts, false);
						if (UtilValidate.isNotEmpty(listCategoryRollup)) {
							hasChild = true;
							treeItem.put("totalChild", listCategoryRollup.size());
							listTreeResult.add(treeItem);
							for (GenericValue categoryRollup : listCategoryRollup) {
								getNbrCategoryChildren(delegator, listTreeResult, categoryRollup.getString("productCategoryId"), treeId);
							}
						}
					}
					if (!hasChild) {
						treeItem.put("totalChild", null);
						listTreeResult.add(treeItem);
					}
				}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListCatalogAndCategory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("results", listTreeResult);
    	return successResult;
    }
	
	public static void getNbrCategoryChildren(Delegator delegator, List<Map<String, Object>> list, String productCategoryId, String parentId) throws GenericEntityException {
		GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
		if (productCategory != null) {
			Map<String, Object> cateMap = FastMap.newInstance();
			String id = "CATE_" + productCategoryId;
			cateMap.put("id", id);
			cateMap.put("parentId", parentId);
			cateMap.put("dataType", "CATEGORY");
			cateMap.put("label", productCategory.getString("categoryName"));
			cateMap.put("value", productCategoryId);
			cateMap.put("expanded", false);
			
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			List<GenericValue> categoryChildren = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(EntityCondition.makeCondition("parentProductCategoryId", productCategoryId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, opts, false);
			if (UtilValidate.isNotEmpty(categoryChildren)) {
				cateMap.put("totalChild", categoryChildren.size());
				list.add(cateMap);
				for (GenericValue cateItem : categoryChildren) {
					getNbrCategoryChildren(delegator, list, cateItem.getString("productCategoryId"), id);
				}
			} else {
				cateMap.put("totalChild", null);
				list.add(cateMap);
			}
		}
		
		return;
	}
	
    public static Map<String, Object> getListProductIdByProductStoreId(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<String> listIterator = FastList.newInstance();
    	String productStoreId = (String) context.get("productStoreId");
    	String hasVirtualProdStr = (String) context.get("hasVirtualProd");
    	try {
    		List<String> prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(EntityCondition.makeCondition("productStoreId", productStoreId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
    		if (UtilValidate.isNotEmpty(prodCatalogIds)) {
    			boolean hasVirtualProd = false;
    			if ("Y".equals(hasVirtualProdStr)) {
    				hasVirtualProd = true;
    			}
    			
    			List<String> catalogIds = new ArrayList<String>();
    			List<String> categoryIds = new ArrayList<String>();
        		if (catalogIds != null) {
    				for (String prodCatalogIdItem : prodCatalogIds) {
    					List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
    					if (listCategory != null) {
    						for (GenericValue categoryItem : listCategory) {
    							categoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
    						}
    					}
    				}
    			}
    			if (UtilValidate.isNotEmpty(categoryIds)) {
    				List<EntityCondition> mainCondList = FastList.newInstance();
                    mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIds));
                    boolean activeOnly = true;
                    Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
                    Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
                    
                    if (activeOnly) {
                        mainCondList.add(EntityUtil.getFilterByDateExpr());
                    }
                    if (introductionDateLimit != null) {
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                    }
                    if (releaseDateLimit != null) {
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                    }
                    
                    // check product has is_variant = "N" or NULL
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVariant", "N"), EntityOperator.OR, EntityCondition.makeCondition("isVariant", null)));
                    
                    EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);

                    /* Cach 2: List Map result */
                    EntityFindOptions opts = new EntityFindOptions();
                    opts.setDistinct(true);
                    Set<String> selectFields = FastSet.newInstance();
                    selectFields.add("productId");
                    selectFields.add("productName");
                    selectFields.add("internalName");
                    selectFields.add("quantityUomId");
                    selectFields.add("isVirtual");
                    selectFields.add("isVariant");
                    selectFields.add("productCode");
                    
                    List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, selectFields, UtilMisc.toList("productCode"), opts, false);
                    if (listProduct != null) {
                    	for (GenericValue itemProd : listProduct) {
                    		GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
                    		if ("Y".equals(itemProduct.getString("isVirtual"))) {
                    			if (hasVirtualProd) listIterator.add(itemProduct.getString("productId"));
                    			List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
                    			if (listVariantProductAssoc != null){
                    				for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
                    					GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
                    					listIterator.add(itemVariantProduct.getString("productId"));
                    				}
                    			}
                    		} else {
                    			listIterator.add(itemProduct.getString("productId"));
                    		}
    					}
                    }
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductIdByCatalogOrCategory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listProductIds", listIterator);
    	return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProductIdByCatalogOrCategory(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<String> listIterator = FastList.newInstance();
		List<String> catalogIdsParam = (List<String>) context.get("catalogIds");
		List<String> categoryIdsParam = (List<String>) context.get("categoryIds");
    		
    	boolean isJsonRole = false;
    	if (UtilValidate.isNotEmpty(catalogIdsParam) && catalogIdsParam.size() > 0){
    		if (catalogIdsParam.get(0) instanceof String) isJsonRole = true;
    	}
    	List<String> catalogIds = new ArrayList<String>();
    	if (isJsonRole){
			String catalogIdsStr = "[" + (String) catalogIdsParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(catalogIdsStr)) {
				jsonArray = JSONArray.fromObject(catalogIdsStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					catalogIds.add(jsonArray.getString(i));
				}
			}
    	} else {
    		catalogIds = (List<String>) context.get("catalogIds");
    	}
    	
    	boolean isJsonCategory = false;
    	if (UtilValidate.isNotEmpty(catalogIdsParam) && categoryIdsParam.size() > 0){
    		if (categoryIdsParam.get(0) instanceof String) isJsonCategory = true;
    	}
    	List<String> categoryIds = new ArrayList<String>();
    	if (isJsonCategory){
			String categoryIdsStr = "[" + (String) categoryIdsParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(categoryIdsStr)) {
				jsonArray = JSONArray.fromObject(categoryIdsStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					categoryIds.add(jsonArray.getString(i));
				}
			}
    	} else {
    		categoryIds = (List<String>) context.get("categoryIds");
    	}
    	
    	try {
    		boolean hasVirtualProd = false;
    		
    		if (catalogIds != null) {
				for (String prodCatalogIdItem : catalogIds) {
					List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
					if (listCategory != null) {
						for (GenericValue categoryItem : listCategory) {
							categoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(categoryIds)) {
				List<EntityCondition> mainCondList = FastList.newInstance();
                mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIds));
                boolean activeOnly = true;
                Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
                Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
                
                if (activeOnly) {
                    mainCondList.add(EntityUtil.getFilterByDateExpr());
                }
                if (introductionDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                }
                if (releaseDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                }
                
                // check product has is_variant = "N" or NULL
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVariant", "N"), EntityOperator.OR, EntityCondition.makeCondition("isVariant", null)));
                
                EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);

                /* Cach 2: List Map result */
                EntityFindOptions opts = new EntityFindOptions();
                opts.setDistinct(true);
                Set<String> selectFields = FastSet.newInstance();
                selectFields.add("productId");
                selectFields.add("productName");
                selectFields.add("internalName");
                selectFields.add("quantityUomId");
                selectFields.add("isVirtual");
                selectFields.add("isVariant");
                selectFields.add("productCode");
                
                List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, selectFields, UtilMisc.toList("productCode"), opts, false);
                if (listProduct != null) {
                	for (GenericValue itemProd : listProduct) {
                		GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
                		if ("Y".equals(itemProduct.getString("isVirtual"))) {
                			if (hasVirtualProd) listIterator.add(itemProduct.getString("productId"));
                			List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
                			if (listVariantProductAssoc != null){
                				for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
                					GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
                					listIterator.add(itemVariantProduct.getString("productId"));
                				}
                			}
                		} else {
                			listIterator.add(itemProduct.getString("productId"));
                		}
					}
                }
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductIdByCatalogOrCategory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listProductIds", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProductIdAdvanceByCatalogOrCategory(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<String> catalogIdsParam = (List<String>) context.get("catalogIds");
		List<String> categoryIdsParam = (List<String>) context.get("categoryIds");
		
		boolean isJsonRole = false;
		if (UtilValidate.isNotEmpty(catalogIdsParam) && catalogIdsParam.size() > 0){
			if (catalogIdsParam.get(0) instanceof String) isJsonRole = true;
		}
		List<String> catalogIds = new ArrayList<String>();
		if (isJsonRole){
			String catalogIdsStr = "[" + (String) catalogIdsParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(catalogIdsStr)) {
				jsonArray = JSONArray.fromObject(catalogIdsStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					catalogIds.add(jsonArray.getString(i));
				}
			}
		} else {
			catalogIds = (List<String>) context.get("catalogIds");
		}
		
		boolean isJsonCategory = false;
		if (UtilValidate.isNotEmpty(catalogIdsParam) && categoryIdsParam.size() > 0){
			if (categoryIdsParam.get(0) instanceof String) isJsonCategory = true;
		}
		List<String> categoryIds = new ArrayList<String>();
		if (isJsonCategory){
			String categoryIdsStr = "[" + (String) categoryIdsParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(categoryIdsStr)) {
				jsonArray = JSONArray.fromObject(categoryIdsStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					categoryIds.add(jsonArray.getString(i));
				}
			}
		} else {
			categoryIds = (List<String>) context.get("categoryIds");
		}
		
		try {
			boolean hasVirtualProd = false;
			
			if (catalogIds != null) {
				for (String prodCatalogIdItem : catalogIds) {
					List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
					if (listCategory != null) {
						for (GenericValue categoryItem : listCategory) {
							categoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(categoryIds)) {
				List<EntityCondition> mainCondList = FastList.newInstance();
				mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIds));
				boolean activeOnly = true;
				Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
				Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
				
				if (activeOnly) {
					mainCondList.add(EntityUtil.getFilterByDateExpr());
				}
				if (introductionDateLimit != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
				}
				if (releaseDateLimit != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
				}
				
				// check product has is_variant = "N" or NULL
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVariant", "N"), EntityOperator.OR, EntityCondition.makeCondition("isVariant", null)));
				
				EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
				
				/* Cach 2: List Map result */
				EntityFindOptions opts = new EntityFindOptions();
				opts.setDistinct(true);
				Set<String> selectFields = FastSet.newInstance();
				selectFields.add("productId");
				selectFields.add("productName");
				selectFields.add("internalName");
				selectFields.add("quantityUomId");
				selectFields.add("isVirtual");
				selectFields.add("isVariant");
				selectFields.add("productCode");
				selectFields.add("sequenceNum");
				
				List<String> productIdCheckExisted = FastList.newInstance();
				List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, selectFields, UtilMisc.toList("productCode", "sequenceNum"), opts, false);
				List<GenericValue> listUomPacking = delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING"), null, false);
				if (listProduct != null) {
					for (GenericValue itemProd : listProduct) {
						GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
						if ("Y".equals(itemProduct.getString("isVirtual"))) {
							if (hasVirtualProd) {
								String productId = itemProduct.getString("productId");
								if (!productIdCheckExisted.contains(productId)) {
									GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(listUomPacking, UtilMisc.toMap("uomId", itemProduct.getString("quantityUomId"))));
									String quantityUomIdDesc = quantityUom != null ? quantityUom.getString("description") : itemProduct.getString("quantityUomId");
									listIterator.add(UtilMisc.<String, Object>toMap(
											"productId", productId, 
											"productCode", itemProduct.getString("productCode"), 
											"quantityUomId", itemProduct.getString("quantityUomId"), 
											"quantityUomIdDesc", quantityUomIdDesc));
									productIdCheckExisted.add(productId);
								}
							}
							List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
							if (listVariantProductAssoc != null){
								for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
									GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
									String productId = itemVariantProduct.getString("productId");
									if (!productIdCheckExisted.contains(productId)) {
										GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(listUomPacking, UtilMisc.toMap("uomId", itemVariantProduct.getString("quantityUomId"))));
										String quantityUomIdDesc = quantityUom != null ? quantityUom.getString("description") : itemVariantProduct.getString("quantityUomId");
										listIterator.add(UtilMisc.<String, Object>toMap(
														"productId", productId, 
														"productCode", itemVariantProduct.getString("productCode"), 
														"quantityUomId", itemVariantProduct.getString("quantityUomId"),
														"quantityUomIdDesc", quantityUomIdDesc));
										productIdCheckExisted.add(productId);
									}
								}
							}
						} else {
							String productId = itemProduct.getString("productId");
							if (!productIdCheckExisted.contains(productId)) {
								GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(listUomPacking, UtilMisc.toMap("uomId", itemProduct.getString("quantityUomId"))));
								String quantityUomIdDesc = quantityUom != null ? quantityUom.getString("description") : itemProduct.getString("quantityUomId");
								listIterator.add(UtilMisc.<String, Object>toMap(
										"productId", productId, 
										"productCode", itemProduct.getString("productCode"), 
										"quantityUomId", itemProduct.getString("quantityUomId"),
										"quantityUomIdDesc", quantityUomIdDesc));
								productIdCheckExisted.add(productId);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListProductIdAdvanceByCatalogOrCategory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listProductIds", listIterator);
		return successResult;
	}
	// ------------------- End Sales Main -------------------------
	
	// ------------------- Other Sales ----------------------------
    //getListProductIdAdvanceByCatalogOrCategory with productName
    public static Map<String, Object> getListProductIdNameAdvanceByCatalogOrCategory(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<Map<String, Object>> listIterator = FastList.newInstance();
        List<String> catalogIdsParam = (List<String>) context.get("catalogIds");
        List<String> categoryIdsParam = (List<String>) context.get("categoryIds");

        boolean isJsonRole = false;
        if (UtilValidate.isNotEmpty(catalogIdsParam) && catalogIdsParam.size() > 0){
            if (catalogIdsParam.get(0) instanceof String) isJsonRole = true;
        }
        List<String> catalogIds = new ArrayList<String>();
        if (isJsonRole){
            String catalogIdsStr = "[" + (String) catalogIdsParam.get(0) + "]";
            JSONArray jsonArray = new JSONArray();
            if (UtilValidate.isNotEmpty(catalogIdsStr)) {
                jsonArray = JSONArray.fromObject(catalogIdsStr);
            }
            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    catalogIds.add(jsonArray.getString(i));
                }
            }
        } else {
            catalogIds = (List<String>) context.get("catalogIds");
        }

        boolean isJsonCategory = false;
        if (UtilValidate.isNotEmpty(catalogIdsParam) && categoryIdsParam.size() > 0){
            if (categoryIdsParam.get(0) instanceof String) isJsonCategory = true;
        }
        List<String> categoryIds = new ArrayList<String>();
        if (isJsonCategory){
            String categoryIdsStr = "[" + (String) categoryIdsParam.get(0) + "]";
            JSONArray jsonArray = new JSONArray();
            if (UtilValidate.isNotEmpty(categoryIdsStr)) {
                jsonArray = JSONArray.fromObject(categoryIdsStr);
            }
            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    categoryIds.add(jsonArray.getString(i));
                }
            }
        } else {
            categoryIds = (List<String>) context.get("categoryIds");
        }

        try {
            boolean hasVirtualProd = false;

            if (catalogIds != null) {
                for (String prodCatalogIdItem : catalogIds) {
                    List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
                    if (listCategory != null) {
                        for (GenericValue categoryItem : listCategory) {
                            categoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
                        }
                    }
                }
            }
            if (UtilValidate.isNotEmpty(categoryIds)) {
                List<EntityCondition> mainCondList = FastList.newInstance();
                mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIds));
                boolean activeOnly = true;
                Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
                Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");

                if (activeOnly) {
                    mainCondList.add(EntityUtil.getFilterByDateExpr());
                }
                if (introductionDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                }
                if (releaseDateLimit != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                }

                // check product has is_variant = "N" or NULL
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVariant", "N"), EntityOperator.OR, EntityCondition.makeCondition("isVariant", null)));

                EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);

                /* Cach 2: List Map result */
                EntityFindOptions opts = new EntityFindOptions();
                opts.setDistinct(true);
                Set<String> selectFields = FastSet.newInstance();
                selectFields.add("productId");
                selectFields.add("productName");
                selectFields.add("internalName");
                selectFields.add("quantityUomId");
                selectFields.add("isVirtual");
                selectFields.add("isVariant");
                selectFields.add("productCode");
                selectFields.add("sequenceNum");

                List<String> productIdCheckExisted = FastList.newInstance();
                List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, selectFields, UtilMisc.toList("sequenceNum", "productCode"), opts, false);
                List<GenericValue> listUomPacking = delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING"), null, false);
                if (listProduct != null) {
                    for (GenericValue itemProd : listProduct) {
                        GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
                        if ("Y".equals(itemProduct.getString("isVirtual"))) {
                            if (hasVirtualProd) {
                                String productId = itemProduct.getString("productId");
                                if (!productIdCheckExisted.contains(productId)) {
                                    GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(listUomPacking, UtilMisc.toMap("uomId", itemProduct.getString("quantityUomId"))));
                                    String quantityUomIdDesc = quantityUom != null ? quantityUom.getString("description") : itemProduct.getString("quantityUomId");
                                    listIterator.add(UtilMisc.<String, Object>toMap(
                                            "productId", productId,
                                            "productCode", itemProduct.getString("productCode"),
                                            "quantityUomId", itemProduct.getString("quantityUomId"),
                                            "quantityUomIdDesc", quantityUomIdDesc, "productName", itemProduct.getString("productName")));
                                    productIdCheckExisted.add(productId);
                                }
                            }
                            List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
                            if (listVariantProductAssoc != null){
                                for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
                                    GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
                                    String productId = itemVariantProduct.getString("productId");
                                    if (!productIdCheckExisted.contains(productId)) {
                                        GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(listUomPacking, UtilMisc.toMap("uomId", itemVariantProduct.getString("quantityUomId"))));
                                        String quantityUomIdDesc = quantityUom != null ? quantityUom.getString("description") : itemVariantProduct.getString("quantityUomId");
                                        listIterator.add(UtilMisc.<String, Object>toMap(
                                                "productId", productId,
                                                "productCode", itemVariantProduct.getString("productCode"),
                                                "quantityUomId", itemVariantProduct.getString("quantityUomId"),
                                                "quantityUomIdDesc", quantityUomIdDesc, "productName", itemProduct.getString("productName")));
                                        productIdCheckExisted.add(productId);
                                    }
                                }
                            }
                        } else {
                            String productId = itemProduct.getString("productId");
                            if (!productIdCheckExisted.contains(productId)) {
                                GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(listUomPacking, UtilMisc.toMap("uomId", itemProduct.getString("quantityUomId"))));
                                String quantityUomIdDesc = quantityUom != null ? quantityUom.getString("description") : itemProduct.getString("quantityUomId");
                                listIterator.add(UtilMisc.<String, Object>toMap(
                                        "productId", productId,
                                        "productCode", itemProduct.getString("productCode"),
                                        "quantityUomId", itemProduct.getString("quantityUomId"),
                                        "quantityUomIdDesc", quantityUomIdDesc, "productName", itemProduct.getString("productName")));
                                productIdCheckExisted.add(productId);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListProductIdNameAdvanceByCatalogOrCategory service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }

        successResult.put("listProductIds", listIterator);
        return successResult;
    }
	//product
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProdCatalog(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("ProdCatalog",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listProdCatalog service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getListProductStoreAvalibleInCatalog(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String prodCatalogId = (String) context.get("prodCatalogId");
		List<GenericValue> listProductStoreCatalog = delegator.findList("ProductStoreCatalog",
				EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", prodCatalogId)), null, null, null, false);
		List<String> listProductStoreId = EntityUtil.getFieldListFromEntityList(listProductStoreCatalog, "productStoreId", true);
		EntityCondition condition = null;
		if (UtilValidate.isNotEmpty(listProductStoreId)) {
			condition = EntityCondition.makeCondition("productStoreId", EntityJoinOperator.NOT_IN, listProductStoreId);
		}
		List<GenericValue> listProductStoreAvalible = delegator.findList("ProductStore", condition, UtilMisc.toSet("productStoreId", "storeName"), null, null, false);
		for (GenericValue x : listProductStoreAvalible) {
			x.set("storeName", x.getString("storeName") + " [" + x.getString("productStoreId") + "]");
		}
		result.put("listProductStoreAvalible", listProductStoreAvalible);
		return result;
	}
	
	public static Map<String, Object> getStoreListByProdCatalogId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String prodCatalogId = (String) context.get("prodCatalogId");
		List<GenericValue> listStoreList = delegator.findList("ProductStoreCatalogDetail",
				EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", prodCatalogId)), null, null, null, false);
		result.put("listStoreList", listStoreList);
		return result;
	}
	
	public static Map<String, Object> getCategoryByProdCatalogId (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String prodCatalogId = (String) context.get("prodCatalogId");
		List<GenericValue> listProdCatalogCategory = delegator.findList("ProdCatalogCategory",
				EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", prodCatalogId)), null,
				UtilMisc.toList("productCategoryId"), null, false);
		result.put("listProdCatalogCategory", listProdCatalogCategory);
		return result;
	}
	
	public static Map<String, Object> getListCategoryAvalibleInCatalog(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String prodCatalogId = (String) context.get("prodCatalogId");
		List<GenericValue> listProdCatalogCategory = delegator.findList("ProdCatalogCategory",
				EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", prodCatalogId)), null,
				UtilMisc.toList("productCategoryId"), null, false);
		List<String> listProductCategoryId = EntityUtil.getFieldListFromEntityList(listProdCatalogCategory, "productCategoryId", true);
		List<EntityCondition> conditions = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listProductCategoryId)) {
			conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_IN, listProductCategoryId));
		}
		conditions.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityJoinOperator.EQUALS, null));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY")));
		List<GenericValue> listCategoryAvalible = delegator.findList("ProductCategory",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		result.put("listCategoryAvalible", listCategoryAvalible);
		return result;
	}
	
	public static Map<String, Object> getListProductCategory (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("categoryName");
		List<GenericValue> listProductCategory = delegator.findList("ProductCategory",
				EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY")), null, orderBy, null, false);
		result.put("listProductCategory", listProductCategory);
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getProductByProductCategoryId (DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String productCategoryId = (String) context.get("productCategoryId");
		List<EntityCondition> listCondition = FastList.newInstance();
		listCondition.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)));
		listCondition.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(listCondition), null, UtilMisc.toList("sequenceNum", "productId"), null, false);
		List<Map> listProductByProductCategoryId = FastList.newInstance();
		
		for (GenericValue x : listProductCategoryMember) {
			String productId = x.getString("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			Map<String, Object> mapProduct = FastMap.newInstance();
			mapProduct.putAll(product);
			mapProduct.putAll(x);
			listProductByProductCategoryId.add(mapProduct);
		}
		result.put("listProductByProductCategoryId", listProductByProductCategoryId);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductNotInCategory(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	if (parameters.containsKey("productCategoryId")) {
    			String productCategoryId = parameters.get("productCategoryId")[0];
    			List<String> productIds = EntityUtil.getFieldListFromEntityList(
    					delegator.findList("ProductCategoryMember",
    							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)), UtilMisc.toSet("productId"), UtilMisc.toList("productCategoryId"), null, false)
    					, "productId", true);
    			if (UtilValidate.isNotEmpty(productIds)) {
    				listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.NOT_IN, productIds));
				}
    		}
	    	EntityListIterator listIterator = delegator.find("Product", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	    	result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> updateProductCategoryAndRollup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			dispatcher.runSync("updateProductCategory", context);
			String primaryParentCategoryId = (String) context.get("primaryParentCategoryId");
			String productCategoryId = (String) context.get("productCategoryId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			if (UtilValidate.isNotEmpty(primaryParentCategoryId)) {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId",
						primaryParentCategoryId)));
				List<GenericValue> listProductCategoryRollup = delegator.findList("ProductCategoryRollup",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(listProductCategoryRollup)) {
					delegator.create("ProductCategoryRollup",
							UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", primaryParentCategoryId,
									"fromDate", new Timestamp(System.currentTimeMillis())));
				}
			}else {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)));
				List<GenericValue> listProductCategoryRollup = delegator.findList("ProductCategoryRollup",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				for (GenericValue x : listProductCategoryRollup) {
					x.set("thruDate", new Timestamp(System.currentTimeMillis()));
					x.store();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listProductsPrices(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("-createdStamp");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.IN, listProductSalable(delegator)));
			listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityJoinOperator.EQUALS, "FINISHED_GOOD"));
			List<GenericValue> products = delegator.findList("Product",
					EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			List<Map<String, Object>> listProducts = FastList.newInstance();
			for (GenericValue x : products) {
				Map<String, Object> product = FastMap.newInstance();
				product.putAll(x);
				product.put("taxCatalogs", getTaxCatalogs(delegator, x.getString("productId")));
				listProducts.add(product);
			}
			result.put("listIterator", listProducts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static List<String> listProductSalable(Delegator delegator) throws GenericEntityException {
		List<GenericValue> products = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()), UtilMisc.toSet("productId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(products, "productId", true);
	}
	public static String getTaxCatalogs(Delegator delegator, String productId) throws GenericEntityException {
		String taxCatalogs = "";
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.clear();
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "TAX_CATEGORY")));
		List<GenericValue> listProductCategory = delegator.findList("ProductCategory",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		List<String> listProductCategoryId = EntityUtil.getFieldListFromEntityList(listProductCategory, "productCategoryId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, listProductCategoryId));
		conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
		List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(listProductCategoryMember)) {
			GenericValue productCategoryMember = EntityUtil.getFirst(listProductCategoryMember);
			GenericValue productCategory = delegator.findOne("ProductCategory",
					UtilMisc.toMap("productCategoryId", productCategoryMember.getString("productCategoryId")), false);
			taxCatalogs = productCategory.getString("categoryName");
		}
		return taxCatalogs;
	}
	
	public static Map<String, Object> listPricesOfProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, context.get("productId")));
			List<GenericValue> listPrices = delegator.findList("ProductPrice",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-thruDate", "termUomId"), null, false);
			result.put("listPrices", listPrices);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProdPriceByProduct(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String productId = SalesUtil.getParameter(parameters, "productId");
			if (UtilValidate.isNotEmpty(productId)) {
    			listAllConditions.add(EntityCondition.makeCondition("productId", productId));
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("thruDate");
    				listSortFields.add("termUomId");
    			}
    			listIterator = delegator.find("ProductPriceAndTax", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProdPriceByProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> checkProductInProductStore(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	//List<GenericPK> listResult = FastList.newInstance();
    	String productId = (String) context.get("productId");
    	String productStoreId = (String) context.get("productStoreId");
    	Boolean available = false;
    	try {
    		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    		if (product != null) {
    			List<String> listProductId = FastList.newInstance();
    			listProductId.add(product.getString("productId"));
    			
    			GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator, nowTimestamp);
    			if (parentProduct != null) listProductId.add(parentProduct.getString("productId"));
    			
    			List<String> listProductCategoryId = FastList.newInstance();
    			List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMember", 
    					EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
    			if (UtilValidate.isNotEmpty(productCategoryIds)) {
    				List<String> listCategoryTemp = FastList.newInstance();
    				for (String productCategoryId : productCategoryIds) {
    					listCategoryTemp = ProductWorker.getAllCategoryParentTree(delegator, productCategoryId, nowTimestamp);
    					if (listCategoryTemp != null) {
    						listProductCategoryId.addAll(listCategoryTemp);
    						listCategoryTemp.clear();
    					}
    				}
    			}
    			
    			// find product category in product catalog category
    			if (UtilValidate.isNotEmpty(productCategoryIds)) {
    				List<String> prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProdCatalogCategory", 
        					EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, listProductCategoryId), 
        							EntityOperator.AND, EntityUtil.getFilterByDateExpr()), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
    				
    				if (UtilValidate.isNotEmpty(prodCatalogIds)) {
    					List<EntityCondition> conds = FastList.newInstance();
    					conds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
    					conds.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
    					conds.add(EntityUtil.getFilterByDateExpr());
    					List<GenericValue> listProductStoreCatalog = delegator.findList("ProductStoreCatalog", 
            					EntityCondition.makeCondition(conds, EntityOperator.AND), null, null, null, false);
    					if (UtilValidate.isNotEmpty(listProductStoreCatalog)) {
    						available = true;
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling checkProductInProductStore service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("available", available);
    	return successResult;
    }
	
	public static Map<String, Object> createProductPriceCustom(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			if (context.containsKey("fromDate")) {
				long fromDateL = (Long) context.get("fromDate");
				if (UtilValidate.isNotEmpty(fromDateL)) {
					context.put("fromDate", new Timestamp(fromDateL));
				}
			}
			if (context.containsKey("thruDate")) {
				long thruDateL = (Long) context.get("thruDate");
				if (UtilValidate.isNotEmpty(thruDateL)) {
					context.put("thruDate", new Timestamp(thruDateL));
				}
			}
			dispatcher.runSync("createProductPrice", context);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> updateProductPriceCustom(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		try {
			if (context.containsKey("fromDate")) {
				long fromDateL = (Long) context.get("fromDate");
				if (UtilValidate.isNotEmpty(fromDateL)) {
					context.put("fromDate", new Timestamp(fromDateL));
				}
			}
			if (context.containsKey("thruDate")) {
				long thruDateL = (Long) context.get("thruDate");
				if (UtilValidate.isNotEmpty(thruDateL)) {
					context.put("thruDate", new Timestamp(thruDateL));
				}
			}
			Map<String, Object> prodPriceCtx = ServiceUtil.setServiceFields(dispatcher, "updateProductPrice", context, userLogin, null, locale);
			dispatcher.runSync("updateProductPrice", prodPriceCtx);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductChildren(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Locale locale = (Locale) context.get("locale");
    	
    	try {
    		String productId = SalesUtil.getParameter(parameters, "productId");
			if (UtilValidate.isNotEmpty(productId)) {
				List<GenericValue> listProduct = ProductWorker.getChildrenProduct(productId, delegator, null);
				if (UtilValidate.isNotEmpty(listProduct)) {
					if (UtilValidate.isNotEmpty(listAllConditions)) {
						listProduct = EntityUtil.filterByCondition(listProduct, EntityCondition.makeCondition(listAllConditions));
					}
					if (UtilValidate.isNotEmpty(listSortFields)) {
						listProduct = EntityUtil.orderBy(listProduct, listSortFields);
					}
					if (UtilValidate.isNotEmpty(listProduct)) {
						for (GenericValue product : listProduct) {
							Map<String, Object> item = product.getAllFields();
							item.put("feature", ProductWorker.getFeatureProduct(delegator, product, locale));
							listIterator.add(item);
						}
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductChildren service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProdConfigItemProduct(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		try {
			String productId = SalesUtil.getParameter(parameters, "productId");
			if (UtilValidate.isNotEmpty(productId)) {
				EntityCondition condDateTime = EntityUtil.getFilterByDateExpr();
	    		List<EntityCondition> condAll = new ArrayList<EntityCondition>();
	    		condAll.add(condDateTime);
	    		condAll.add(EntityCondition.makeCondition("productId", productId));
	    		List<String> configItemIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductConfig", EntityCondition.makeCondition(condAll, EntityOperator.AND), UtilMisc.toSet("configItemId"), UtilMisc.toList("-fromDate", "sequenceNum"), null, true), "configItemId", true);
	    		if (UtilValidate.isNotEmpty(configItemIds)) {
	    			List<String> configOptionIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductConfigOption", EntityCondition.makeCondition("configItemId", EntityOperator.IN, configItemIds), UtilMisc.toSet("configOptionId"), null, null, true), "configOptionId", true);
	    			if (UtilValidate.isNotEmpty(configOptionIds)) {
	    				listAllConditions.add(EntityCondition.makeCondition("configItemId", EntityOperator.IN, configItemIds));
	    				listAllConditions.add(EntityCondition.makeCondition("configOptionId", EntityOperator.IN, configOptionIds));
	    				if (UtilValidate.isEmpty(listSortFields)) {
	    					listSortFields.add("sequenceNum");
	    				}
	    				listIterator = delegator.find("ProductConfigProductAndProduct", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	    			}
	    		}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProdConfigItemProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> calcProductAndCatalogTempData(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	try {
    		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    		Set<String> prodCatalogIds = FastSet.newInstance();
    		
    		boolean hasProcessChildProduct = true;
    		boolean isRunning = true;
    		int index = 0;
    		int size = 100;
    		
    		List<EntityCondition> listConditions = FastList.newInstance();
    		listConditions.add(EntityUtil.getFilterByDateExpr());
    		EntityCondition mainCond = EntityCondition.makeCondition(listConditions);
    		
    		EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
    		List<String> sortByFields = FastList.newInstance();
    		sortByFields.add("-fromDate");
    		while (isRunning) {
    			int iIndex = index * size + 1;
    			/*EntityFindOptions opts = new EntityFindOptions();
    			opts.setOffset(lowIndex);
    			opts.setLimit(size);*/
    			//opts.setMaxRows(size);
    			
    			List<GenericValue> listCategoryMembers = null;
    			
    			EntityListIterator iterator = null;
    			try {
    				iterator = delegator.find("ProductCategoryMember", mainCond, null, null, null, opts);
	    			listCategoryMembers = iterator.getPartialList(iIndex, size);
    			} catch(Exception e) {
    				Debug.logWarning("Error when select", module);
    			} finally {
					if (iterator != null) {
						iterator.close();
					}
				}
    			if (UtilValidate.isEmpty(listCategoryMembers)) {
    				isRunning = false;
    				continue;
    			}
    			
    			List<EntityCondition> itemConds = new ArrayList<EntityCondition>();
    			
    			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
    			for (GenericValue categoryMember : listCategoryMembers) {
    				String productId = categoryMember.getString("productId");
					List<String> parentCategoryIds = ProductWorker.getAllCategoryParentTree(delegator, categoryMember.getString("productCategoryId"), null);
					if (UtilValidate.isNotEmpty(parentCategoryIds)) {
						itemConds.clear();
						itemConds.add(EntityUtil.getFilterByDateExpr());
						itemConds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, parentCategoryIds));
						List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(itemConds), null, null, null, false);
						if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
							for (GenericValue catalogCategory : prodCatalogCategories) {
								String prodCatalogId = catalogCategory.getString("prodCatalogId");
								/*itemConds.clear();
								itemConds.add(EntityCondition.makeCondition("productId", productId));
								itemConds.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
								itemConds.add(EntityUtil.getFilterByDateExpr());
								GenericValue productTmp = EntityUtil.getFirst(delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(itemConds), null, null, null, false));
			    				if (productTmp == null) {
			    					// create new
			    					productTmp = delegator.makeValue("ProductAndCatalogTempData");
			    					productTmp.put("prodCatalogId", prodCatalogId);
			    					productTmp.put("productId", productId);
			    					productTmp.put("fromDate", nowTimestamp);
									tobeStored.add(productTmp);
			    				}*/
								Long sequenceNum = calculateSequenceNumTemp(catalogCategory.getLong("sequenceNum"), parentCategoryIds.size(), categoryMember.getLong("sequenceNum"));
								List<GenericValue> tobeStoredTmp = processUpdateProductAndCatalogTempData(delegator, productId, prodCatalogId, nowTimestamp, null, hasProcessChildProduct, sequenceNum);
								if (UtilValidate.isNotEmpty(tobeStoredTmp)) tobeStored.addAll(tobeStoredTmp);
			    				
			    				prodCatalogIds.add(prodCatalogId);
							}
						}
					}
    			}
    			delegator.storeAll(tobeStored);
    			index++;
    		}
    		
    		// thru date records has expired, product and product catalog don't have relationship
    		List<EntityCondition> deleteConds = new ArrayList<EntityCondition>();
    		deleteConds.add(EntityUtil.getFilterByDateExpr());
    		if (UtilValidate.isNotEmpty(prodCatalogIds)) deleteConds.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.NOT_IN, prodCatalogIds));
    		List<GenericValue> deleteList = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(deleteConds), null, null, null, false);
    		if (UtilValidate.isNotEmpty(deleteList)) {
    			for (GenericValue item : deleteList) {
    				item.set("thruDate", nowTimestamp);
    			}
    			delegator.storeAll(deleteList);
    		}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling calcProductAndCatalogTempData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
	
	private static Long calculateSequenceNumTemp(Long sequenceNumCategory, Integer levelDeep, Long sequenceNumProd) {
		if (sequenceNumCategory == null) sequenceNumCategory = new Long(0);
		if (sequenceNumProd == null) sequenceNumProd = new Long(0);
		if (levelDeep == null) levelDeep = new Integer(0);
		Long sequenceNum = new Long(0);
		int distanceVal = 100;
		sequenceNum = sequenceNumCategory * distanceVal * levelDeep + sequenceNumProd;
		return sequenceNum;
	}
	
	public static Map<String, Object> updateProductAndCatalogTempData(DispatchContext ctx, Map<String, Object> context){
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	try {
    		String infoUpdate = (String) context.get("infoUpdate");
    		String productCategoryId = (String) context.get("productCategoryId");
    		String prodCatalogId = (String) context.get("prodCatalogId");
    		String productId = (String) context.get("productId");
    		Long sequenceNum = (Long) context.get("sequenceNum");
    		Timestamp fromDate = (Timestamp) context.get("fromDate");
    		Timestamp thruDate = (Timestamp) context.get("thruDate");
    		
    		//Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    		List<EntityCondition> itemConds = new ArrayList<EntityCondition>();
    		List<GenericValue> tobeStored = new LinkedList<GenericValue>();
    		//Set<String> prodCatalogIds = FastSet.newInstance();
    		
    		boolean hasProcessChildProduct = true;
    		
    		GenericValue productCategoryCheck = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
    		if (productCategoryCheck != null) {
    			if ("MMS_CATAGORY_REF".equals(productCategoryCheck.getString("productCategoryTypeId"))) {
    				return successResult;
    			}
    		}
    		
    		if ("ProductCategoryMember".equals(infoUpdate)) {
    			// find all product category of product
    			List<EntityCondition> conds = new ArrayList<EntityCondition>();
				conds.add(EntityUtil.getFilterByDateExpr());
				conds.add(EntityCondition.makeCondition("productId", productId));
				conds.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
				List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition(conds), null, null, null, false), "productCategoryId", true);
				if (UtilValidate.isNotEmpty(productCategoryIds)) {
					// find all category parents
					List<String> parentCategoryIds = FastList.newInstance();
					for (String categoryId : productCategoryIds) {
						List<String> categoryIdsParentTmp = ProductWorker.getAllCategoryParentTree(delegator, categoryId, null);
						if (UtilValidate.isNotEmpty(categoryIdsParentTmp)) parentCategoryIds.addAll(categoryIdsParentTmp);
					}
					if (parentCategoryIds.size() > 0) {
						// check category rel catalog
						conds.clear();
						conds.add(EntityUtil.getFilterByDateExpr());
						conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, parentCategoryIds));
						List<String> catalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(conds), null, null, null, false), "prodCatalogId", true);
						
						conds.clear();
						conds.add(EntityCondition.makeCondition("productId", productId));
						conds.add(EntityUtil.getFilterByDateExpr());
						List<GenericValue> productAndCatalogs = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(conds), null, null, null, false);
						
						if (UtilValidate.isNotEmpty(catalogIds)) {
							// check, update or create new data
							Set<String> catalogIdNew = FastSet.newInstance();
							catalogIdNew.addAll(catalogIds);
							if (UtilValidate.isNotEmpty(productAndCatalogs)) {
								for (GenericValue item : productAndCatalogs) {
									if (catalogIds.contains(item.getString("prodCatalogId"))) {
										catalogIdNew.remove(item.getString("prodCatalogId"));
									} else {
										// thru date
										List<GenericValue> tmpStored = processThruDateProductAndCatalogTempData(delegator, item, thruDate, hasProcessChildProduct);
										if (tmpStored.size() > 0) {
											tobeStored.addAll(tmpStored);
										}
									}
								}
							} else {
								// create for all catalog
								catalogIdNew.addAll(catalogIds);
							}
							if (UtilValidate.isNotEmpty(catalogIdNew)) {
								// create for all catalog
								for (String itemCatalogId : catalogIdNew) {
									// create new data
									List<GenericValue> tmpStored = processCreateProductAndCatalogTempData(delegator, itemCatalogId, productId, fromDate, thruDate, sequenceNum, hasProcessChildProduct);
									if (tmpStored.size() > 0) {
										tobeStored.addAll(tmpStored);
									}
								}
							}
						} else {
							// thru date all if product not in any catalog
							if (UtilValidate.isNotEmpty(productAndCatalogs)) {
								for (GenericValue item : productAndCatalogs) {
									List<GenericValue> tmpStored = processThruDateProductAndCatalogTempData(delegator, item, thruDate, hasProcessChildProduct);
									if (tmpStored.size() > 0) {
										tobeStored.addAll(tmpStored);
									}
								}
							}
						}
					}
				} else {
					// thru date all if product not in any category
					conds.clear();
					conds.add(EntityCondition.makeCondition("productId", productId));
					conds.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> productAndCatalogs = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(conds), null, null, null, false);
					if (UtilValidate.isNotEmpty(productAndCatalogs)) {
						for (GenericValue item : productAndCatalogs) {
							List<GenericValue> tmpStored = processThruDateProductAndCatalogTempData(delegator, item, thruDate, hasProcessChildProduct);
							if (tmpStored.size() > 0) {
								tobeStored.addAll(tmpStored);
							}
						}
						tobeStored.addAll(productAndCatalogs);
					}
				}
				
				// TODOCHANGE delete code
				// find all parent category
    			/*List<String> parentCategoryIds = ProductWorker.getAllCategoryParentTree(delegator, productCategoryId, null);
    			if (UtilValidate.isNotEmpty(parentCategoryIds)) {
    				List<EntityCondition> conds = new ArrayList<EntityCondition>();
					conds.add(EntityUtil.getFilterByDateExpr());
					conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, parentCategoryIds));
					List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(conds), null, null, null, false);
					if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
						for (GenericValue catalogCategory : prodCatalogCategories) {
							String itemCatalogId = catalogCategory.getString("prodCatalogId");
							Long sequenceNumTmp = calculateSequenceNumTemp(catalogCategory.getLong("sequenceNum"), parentCategoryIds.size(), sequenceNum);
							List<GenericValue> tobeStoreTmp = processUpdateProductAndCatalogTempData(delegator, productId, itemCatalogId, fromDate, thruDate, hasProcessChildProduct, sequenceNumTmp);
							if (UtilValidate.isNotEmpty(tobeStoreTmp)) tobeStored.addAll(tobeStoreTmp);
						}
					}
    			}*/
    		} else if ("ProdCatalogCategory".equals(infoUpdate)) {
    			List<String> productCategoryIds = ProductWorker.getAllCategoryTree(delegator, productCategoryId, "CATALOG_CATEGORY");
    			if (UtilValidate.isNotEmpty(productCategoryIds)) {
    				itemConds.clear();
    				itemConds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
    				itemConds.add(EntityUtil.getFilterByDateExpr());
    				List<GenericValue> productMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(itemConds), null, null, null, false);
    				if (UtilValidate.isNotEmpty(productMembers)) {
    					for (GenericValue itemProduct : productMembers) {
    						// check sequence
    						List<String> parentCategoryIds = ProductWorker.getAllCategoryParentTree(delegator, itemProduct.getString("productCategoryId"), null);
    						Long sequenceNumTmp = calculateSequenceNumTemp(sequenceNum, parentCategoryIds.size(), itemProduct.getLong("sequenceNum"));
							// create
    						List<GenericValue> tobeStoredTmp = processUpdateProductAndCatalogTempData(delegator, itemProduct.getString("productId"), prodCatalogId, fromDate, thruDate, hasProcessChildProduct, sequenceNumTmp);
							if (UtilValidate.isNotEmpty(tobeStoredTmp)) tobeStored.addAll(tobeStoredTmp);
    					}
    				}
    			}
    		} else if ("ProductCategoryRollup".equals(infoUpdate)) {
    			String parentProductCategoryId = (String) context.get("parentProductCategoryId");
    			GenericValue parentProductCategoryCheck = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", parentProductCategoryId), false);
    			if (parentProductCategoryCheck != null) {
        			if ("MMS_CATAGORY_REF".equals(parentProductCategoryCheck.getString("productCategoryTypeId"))) {
        				return successResult;
        			}
        		}
    			
    			// find all parent category
    			List<String> parentCategoryIds0 = ProductWorker.getAllCategoryParentTree(delegator, parentProductCategoryId, null);
    			if (UtilValidate.isNotEmpty(parentCategoryIds0)) {
    				List<EntityCondition> conds = new ArrayList<EntityCondition>();
					conds.add(EntityUtil.getFilterByDateExpr());
					conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, parentCategoryIds0));
					List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(conds), null, null, null, false);
					if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
						List<String> productCategoryIds = ProductWorker.getAllCategoryTree(delegator, productCategoryId, "CATALOG_CATEGORY");
		    			if (UtilValidate.isNotEmpty(productCategoryIds)) {
		    				itemConds.clear();
		    				itemConds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
		    				itemConds.add(EntityUtil.getFilterByDateExpr());
		    				List<GenericValue> productMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(itemConds), null, null, null, false);
		    				if (UtilValidate.isNotEmpty(productMembers)) {
		    					for (GenericValue catalogCategory : prodCatalogCategories) {
		    						for (GenericValue itemProduct : productMembers) {
		    							String itemCatalogId = catalogCategory.getString("prodCatalogId");
			    						// check sequence
			    						List<String> parentCategoryIds = ProductWorker.getAllCategoryParentTree(delegator, itemProduct.getString("productCategoryId"), null);
			    						Long sequenceNumTmp = calculateSequenceNumTemp(catalogCategory.getLong("sequenceNum"), parentCategoryIds.size(), itemProduct.getLong("sequenceNum"));
										// create
			    						List<GenericValue> tobeStoredTmp = processUpdateProductAndCatalogTempData(delegator, itemProduct.getString("productId"), itemCatalogId, fromDate, thruDate, hasProcessChildProduct, sequenceNumTmp);
										if (UtilValidate.isNotEmpty(tobeStoredTmp)) tobeStored.addAll(tobeStoredTmp);
			    					}
								}
		    				}
		    			}
					}
    			}
    		}
    		
    		delegator.storeAll(tobeStored);
    		
    		// thru date records has expired, product and product catalog don't have relationship
    		/*List<EntityCondition> deleteConds = new ArrayList<EntityCondition>();
    		deleteConds.add(EntityUtil.getFilterByDateExpr());
    		if (UtilValidate.isNotEmpty(prodCatalogIds)) deleteConds.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.NOT_IN, prodCatalogIds));
    		List<GenericValue> deleteList = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(deleteConds), null, null, null, false);
    		if (UtilValidate.isNotEmpty(deleteList)) {
    			for (GenericValue item : deleteList) {
    				item.set("thruDate", nowTimestamp);
    			}
    			delegator.storeAll(deleteList);
    		}*/
    	} catch (Exception e) {
			String errMsg = "Fatal error calling updateProductAndCatalogTempData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
	
	public static Map<String, Object> calcProductTempData(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	try {
    		String productIdsStr = (String) context.get("productIds");
    		if (UtilValidate.isNotEmpty(productIdsStr)) {
    			String[] productIdsArr = productIdsStr.split(",");
    			if (productIdsArr != null && productIdsArr.length > 0) {
    				List<GenericValue> listProducts = FastList.newInstance();
    				for (int i = 0; i < productIdsArr.length; i++) {
    					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productIdsArr[i]), false);
    					if (product != null) {
    						listProducts.add(product);
    					}
    				}
    				if (listProducts.size() > 0) {
    					processProductTempData(delegator, listProducts);
    				}
    			}
    		} else {
    			// process all of product
    			
    			boolean isRunning = true;
        		int index = 0;
        		int size = 100;
        		
        		//List<EntityCondition> listConditions = FastList.newInstance();
        		//listConditions.add(EntityUtil.getFilterByDateExpr());
        		//EntityCondition mainCond = EntityCondition.makeCondition(listConditions);
        		
        		EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
        		List<String> sortByFields = FastList.newInstance();
        		sortByFields.add("-fromDate");
        		while (isRunning) {
        			int iIndex = index * size + 1;
        			/*EntityFindOptions opts = new EntityFindOptions();
        			opts.setOffset(lowIndex);
        			opts.setLimit(size);*/
        			//opts.setMaxRows(size);
        			
        			List<GenericValue> listProducts = null;
        			
        			EntityListIterator iterator = null;
        			try {
        				iterator = delegator.find("Product", null, null, null, null, opts);
    	    			listProducts = iterator.getPartialList(iIndex, size);
        			} catch(Exception e) {
        				Debug.logWarning("Error when select:" + e, module);
        			} finally {
    					if (iterator != null) {
    						iterator.close();
    					}
    				}
        			if (UtilValidate.isEmpty(listProducts)) {
        				isRunning = false;
        				continue;
        			}
        			
        			processProductTempData(delegator, listProducts);
        			
        			index++;
        		}
    		}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling calcProductTempData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
	
	private static void processProductTempData(Delegator delegator, List<GenericValue> listProducts) throws GenericEntityException {
		List<EntityCondition> itemConds = new ArrayList<EntityCondition>();
		List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		for (GenericValue product : listProducts) {
			String productId = product.getString("productId");
			
			// category tax
			String taxCategoryId = null;
			BigDecimal taxPercentage = null;
			String taxAuthPartyId = null;
			String taxAuthGeoId = null;
			
			itemConds.clear();
			itemConds.add(EntityCondition.makeCondition("productId", productId));
			itemConds.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
			itemConds.add(EntityUtil.getFilterByDateExpr());
			GenericValue taxCategory = EntityUtil.getFirst(delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition(itemConds), null, null, null, false));
			if (taxCategory != null) {
				taxCategoryId = taxCategory.getString("productCategoryId");
				GenericValue taxAuth = EntityUtil.getFirst(delegator.findByAnd("TaxAuthorityRateProduct", UtilMisc.toMap("productCategoryId", taxCategoryId), null, false));
				if (taxAuth != null) {
					taxPercentage = taxAuth.getBigDecimal("taxPercentage");
					taxAuthPartyId = taxAuth.getString("taxAuthPartyId");
					taxAuthGeoId = taxAuth.getString("taxAuthGeoId");
				}
			}
			
			String unitUomId = product.getString("quantityUomId");
			String salesUomId = product.getString("salesUomId");
			BigDecimal unitDefaultPrice = null;
			BigDecimal unitListPrice = null;
			BigDecimal salesDefaultPrice = null;
			BigDecimal salesListPrice = null;
			String currencyUomId = null;
			
			// price per sales uom id
			if (UtilValidate.isNotEmpty(salesUomId)) {
				itemConds.clear();
				itemConds.add(EntityCondition.makeCondition("productId", productId));
				itemConds.add(EntityCondition.makeCondition("termUomId", salesUomId));
				itemConds.add(EntityCondition.makeCondition("productPriceTypeId", "DEFAULT_PRICE"));
				itemConds.add(EntityUtil.getFilterByDateExpr());
				GenericValue priceDFSales = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(itemConds), null, null, null, false));
				if (priceDFSales != null) {
					salesDefaultPrice = priceDFSales.getBigDecimal("price");
					currencyUomId = priceDFSales.getString("currencyUomId");
				}
				itemConds.clear();
				itemConds.add(EntityCondition.makeCondition("productId", productId));
				itemConds.add(EntityCondition.makeCondition("termUomId", salesUomId));
				itemConds.add(EntityCondition.makeCondition("productPriceTypeId", "LIST_PRICE"));
				itemConds.add(EntityUtil.getFilterByDateExpr());
				GenericValue priceLFSales = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(itemConds), null, null, null, false));
				if (priceLFSales != null) {
					salesListPrice = priceLFSales.getBigDecimal("price");
				}
			}
			
			// price per unit uom id
			if (UtilValidate.isNotEmpty(unitUomId)) {
				if (unitUomId.equals(salesUomId)) {
					unitDefaultPrice = salesDefaultPrice;
					unitListPrice = salesListPrice;
				} else {
					itemConds.clear();
					itemConds.add(EntityCondition.makeCondition("productId", productId));
    				itemConds.add(EntityCondition.makeCondition("termUomId", unitUomId));
    				itemConds.add(EntityCondition.makeCondition("productPriceTypeId", "DEFAULT_PRICE"));
    				itemConds.add(EntityUtil.getFilterByDateExpr());
    				GenericValue priceDFUnit = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(itemConds), null, null, null, false));
    				if (priceDFUnit != null) {
    					unitDefaultPrice = priceDFUnit.getBigDecimal("price");
    					currencyUomId = priceDFUnit.getString("currencyUomId");
    				}
    				itemConds.clear();
    				itemConds.add(EntityCondition.makeCondition("productId", productId));
    				itemConds.add(EntityCondition.makeCondition("termUomId", unitUomId));
    				itemConds.add(EntityCondition.makeCondition("productPriceTypeId", "LIST_PRICE"));
    				itemConds.add(EntityUtil.getFilterByDateExpr());
    				GenericValue priceLFUnit = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(itemConds), null, null, null, false));
    				if (priceLFUnit != null) {
    					unitListPrice = priceLFUnit.getBigDecimal("price");
    				}
    				
    				if (salesDefaultPrice == null || salesListPrice == null) {
    					BigDecimal convertQuantity = BigDecimal.ZERO;
						if (UtilValidate.isNotEmpty(unitUomId) && UtilValidate.isNotEmpty(salesUomId)) {
							if (unitUomId.equals(salesUomId)) {
								convertQuantity = BigDecimal.ONE;
							} else {
								convertQuantity = UomWorker.customConvertUom(productId, salesUomId, unitUomId, BigDecimal.ONE, delegator);
								if (convertQuantity == null) convertQuantity = BigDecimal.ZERO;
							}
						}
						
						if (unitDefaultPrice != null && salesDefaultPrice == null) {
							salesDefaultPrice = unitDefaultPrice.multiply(convertQuantity);
						}
						if (salesDefaultPrice != null && salesListPrice == null) {
							salesListPrice = unitListPrice.multiply(convertQuantity);
						}
    				}
				}
			}
			
			// make product temp data
			product = delegator.makeValue("ProductTempData");
			product.set("productId", productId);
			product.set("unitUomId", unitUomId);
			product.set("salesUomId", salesUomId);
			product.set("unitPrice", unitDefaultPrice);
			product.set("unitDefaultPrice", unitDefaultPrice);
			product.set("unitListPrice", unitListPrice);
			product.set("salesPrice", salesDefaultPrice);
			product.set("salesDefaultPrice", salesDefaultPrice);
			product.set("salesListPrice", salesListPrice);
			product.set("currencyUomId", currencyUomId);
			product.set("taxCategoryId", taxCategoryId);
			product.set("taxPercentage", taxPercentage);
			product.set("taxAuthPartyId", taxAuthPartyId);
			product.set("taxAuthGeoId", taxAuthGeoId);
			tobeStored.add(product);
			
		}
		if (!tobeStored.isEmpty()) delegator.storeAll(tobeStored);
	}
	
	public static Map<String, Object> updateProductTempData(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String infoUpdate = (String) context.get("infoUpdate");
			String productId = (String) context.get("productId");
    		
			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
			
			if ("Product".equals(infoUpdate)) {
				GenericValue product = delegator.findOne("ProductTempData", UtilMisc.toMap("productId", productId), false);
				if (product == null) {
					String quantityUomId = (String) context.get("quantityUomId");
					String salesUomId = (String) context.get("salesUomId");
					product = delegator.makeValue("ProductTempData", UtilMisc.toMap("productId", productId, "unitUomId", quantityUomId, "salesUomId", salesUomId));
					tobeStored.add(product);
				}
			} else if ("ProductCategoryMember".equals(infoUpdate)) {
				// create or store relationship between Category tax and Product
				String productCategoryId = (String) context.get("productCategoryId");
				GenericValue category = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
				if (category != null && "TAX_CATEGORY".equals(category.getString("productCategoryTypeId"))) {
					GenericValue product = delegator.findOne("ProductTempData", UtilMisc.toMap("productId", productId), false);
					if (product != null) {
						Timestamp thruDate = (Timestamp) context.get("thruDate");
						if (thruDate != null) {
							if (productCategoryId.equals(product.getString("taxCategoryId"))) {
								product.set("taxCategoryId", null);
								product.set("taxPercentage", null);
								product.set("taxAuthPartyId", null);
								product.set("taxAuthGeoId", null);
								tobeStored.add(product);
							}
						} else {
							product.set("taxCategoryId", productCategoryId);
							GenericValue taxAuth = EntityUtil.getFirst(delegator.findByAnd("TaxAuthorityRateProduct", UtilMisc.toMap("productCategoryId", productCategoryId), null, false));
							if (taxAuth != null) {
								product.set("taxPercentage", taxAuth.get("taxPercentage"));
								product.set("taxAuthPartyId", taxAuth.get("taxAuthPartyId"));
								product.set("taxAuthGeoId", taxAuth.get("taxAuthGeoId"));
							}
							tobeStored.add(product);
						}
					}
				}
			} else if ("TaxAuthorityRateProduct".equals(infoUpdate)) {
				String productCategoryId = (String) context.get("productCategoryId");
				BigDecimal taxPercentage = (BigDecimal) context.get("taxPercentage");
				String taxAuthPartyId = (String) context.get("taxAuthPartyId");
				String taxAuthGeoId = (String) context.get("taxAuthGeoId");
				List<GenericValue> productList = delegator.findByAnd("ProductTempData", UtilMisc.toMap("taxCategoryId", productCategoryId), null, false);
				if (productList != null && !productList.isEmpty()) {
					for (GenericValue prod : productList) {
						prod.set("taxPercentage", taxPercentage);
						prod.set("taxAuthPartyId", taxAuthPartyId);
						prod.set("taxAuthGeoId", taxAuthGeoId);
					}
					tobeStored.addAll(productList);
				}
			} else if ("ProductPrice".equals(infoUpdate)) {
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				GenericValue productTemp = delegator.findOne("ProductTempData", UtilMisc.toMap("productId", productId), false);
				if (product != null && productTemp != null) {
					String termUomId = (String) context.get("termUomId");
					String termUomIdUnit = termUomId != null ? termUomId : productTemp.getString("unitUomId");
					String termUomIdSales = termUomId != null ? termUomId : productTemp.getString("salesUomId");
					String productPriceTypeId = (String) context.get("productPriceTypeId");
					String currencyUomId = context.get("currencyUomId") != null ? (String) context.get("currencyUomId") : productTemp.getString("currencyUomId");
					BigDecimal price = (BigDecimal) context.get("price");
					//if (price == null) return successResult;
					
					String unitUomId = product.getString("quantityUomId");
					String salesUomId = product.getString("salesUomId");
					
					Timestamp thruDate = (Timestamp) context.get("thruDate");
					if (thruDate != null) {
						if (termUomIdUnit != null && termUomIdUnit.equals(unitUomId)) {
							if ("DEFAULT_PRICE".equals(productPriceTypeId)) {
								productTemp.set("unitPrice", null);
								productTemp.set("unitDefaultPrice", null);
								
								List<GenericValue> priceSales = delegator.findList("ProductPrice", 
																			EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "termUomId", salesUomId, "productPriceTypeId", "DEFAULT_PRICE")), 
																					EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false);
								if (UtilValidate.isEmpty(priceSales)) {
									productTemp.set("salesPrice", null);
									productTemp.set("salesDefaultPrice", null);
								}
							} else if ("LIST_PRICE".equals(productPriceTypeId)) {
								productTemp.set("unitListPrice", price);
								
								List<GenericValue> priceSales = delegator.findList("ProductPrice", 
																			EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "termUomId", salesUomId, "productPriceTypeId", "LIST_PRICE")), 
																					EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false);
								if (UtilValidate.isEmpty(priceSales)) {
									productTemp.set("salesListPrice", null);
								}
							}
						}
						if (termUomIdSales != null && termUomIdSales.equals(salesUomId)) {
							if ("DEFAULT_PRICE".equals(productPriceTypeId)) {
								productTemp.set("salesPrice", null);
								productTemp.set("salesDefaultPrice", null);
							} else if ("LIST_PRICE".equals(productPriceTypeId)) {
								productTemp.set("salesListPrice", null);
							}
						}
						tobeStored.add(productTemp);
					} else {
						if (termUomIdUnit != null && termUomIdUnit.equals(unitUomId)) {
							if ("DEFAULT_PRICE".equals(productPriceTypeId)) {
								productTemp.set("unitPrice", price);
								productTemp.set("unitDefaultPrice", price);
								productTemp.set("currencyUomId", currencyUomId);
								
								List<GenericValue> priceSales = delegator.findList("ProductPrice", 
																			EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "termUomId", salesUomId, "productPriceTypeId", "DEFAULT_PRICE")), 
																					EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false);
								if (UtilValidate.isEmpty(priceSales)) {
									BigDecimal convertQuantity = BigDecimal.ZERO;
									if (UtilValidate.isNotEmpty(unitUomId) && UtilValidate.isNotEmpty(salesUomId)) {
										if (unitUomId.equals(salesUomId)) {
											convertQuantity = BigDecimal.ONE;
										} else {
											convertQuantity = UomWorker.customConvertUom(productId, salesUomId, unitUomId, BigDecimal.ONE, delegator);
											if (convertQuantity == null) convertQuantity = BigDecimal.ZERO;
										}
									}
									if (convertQuantity.compareTo(BigDecimal.ZERO) > 0) {
										productTemp.set("salesPrice", price.multiply(convertQuantity));
										productTemp.set("salesDefaultPrice", price.multiply(convertQuantity));
									}
								}
							} else if ("LIST_PRICE".equals(productPriceTypeId)) {
								productTemp.set("unitListPrice", price);
								productTemp.set("currencyUomId", currencyUomId);
								
								List<GenericValue> priceSales = delegator.findList("ProductPrice", 
																			EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "termUomId", salesUomId, "productPriceTypeId", "LIST_PRICE")), 
																					EntityOperator.AND, EntityUtil.getFilterByDateExpr()), null, null, null, false);
									if (UtilValidate.isEmpty(priceSales)) {
									BigDecimal convertQuantity = BigDecimal.ZERO;
									if (UtilValidate.isNotEmpty(unitUomId) && UtilValidate.isNotEmpty(salesUomId)) {
										if (unitUomId.equals(salesUomId)) {
											convertQuantity = BigDecimal.ONE;
										} else {
											convertQuantity = UomWorker.customConvertUom(productId, salesUomId, unitUomId, BigDecimal.ONE, delegator);
											if (convertQuantity == null) convertQuantity = BigDecimal.ZERO;
										}
									}
									if (convertQuantity.compareTo(BigDecimal.ZERO) > 0) {
										productTemp.set("salesListPrice", price.multiply(convertQuantity));
									}
								}
							}
						}
						if (termUomIdSales != null && termUomIdSales.equals(salesUomId)) {
							if ("DEFAULT_PRICE".equals(productPriceTypeId)) {
								productTemp.set("salesPrice", price);
								productTemp.set("salesDefaultPrice", price);
							} else if ("LIST_PRICE".equals(productPriceTypeId)) {
								productTemp.set("salesListPrice", price);
							}
						}
						tobeStored.add(productTemp);
					}
				}
			}
			
			if (!tobeStored.isEmpty()) {
				delegator.storeAll(tobeStored);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateProductTempData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> calcProductPackagingUom(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	try {
    		boolean isRunning = true;
    		int index = 0;
    		int size = 100;
    		
    		//List<EntityCondition> listConditions = FastList.newInstance();
    		//listConditions.add(EntityUtil.getFilterByDateExpr());
    		//EntityCondition mainCond = EntityCondition.makeCondition(listConditions);
    		
    		EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
    		List<String> sortByFields = FastList.newInstance();
    		sortByFields.add("-fromDate");
    		while (isRunning) {
    			int iIndex = index * size + 1;
    			/*EntityFindOptions opts = new EntityFindOptions();
    			opts.setOffset(lowIndex);
    			opts.setLimit(size);*/
    			//opts.setMaxRows(size);
    			
    			List<GenericValue> listProducts = null;
    			
    			EntityListIterator iterator = null;
    			try {
    				iterator = delegator.find("Product", null, null, null, null, opts);
	    			listProducts = iterator.getPartialList(iIndex, size);
    			} catch(Exception e) {
    				Debug.logWarning("Error when select:" + e, module);
    			} finally {
					if (iterator != null) {
						iterator.close();
					}
				}
    			if (UtilValidate.isEmpty(listProducts)) {
    				isRunning = false;
    				continue;
    			}
    			
    			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
    			for (GenericValue product : listProducts) {
    				String productId = product.getString("productId");
					
					List<GenericValue> productUomsRemove = delegator.findByAnd("ProductPackagingUom", UtilMisc.toMap("productId", productId), null, false);
					
					List<String> uomIds = getQuantityUomIds(productId, delegator);
					if (UtilValidate.isNotEmpty(uomIds)) {
						List<String> uomIdsExisted = EntityUtil.getFieldListFromEntityList(productUomsRemove, "uomId", true);
						for (String uomId : uomIds) {
							if (uomIdsExisted.contains(uomId)) {
								List<GenericValue> tmpList = EntityUtil.filterByCondition(productUomsRemove, EntityCondition.makeCondition("uomId", uomId));
								if (UtilValidate.isNotEmpty(tmpList)) {
									productUomsRemove.removeAll(tmpList);
								}
								continue;
							} else {
								GenericValue productUom = delegator.makeValue("ProductPackagingUom", UtilMisc.toMap("productId", productId, "uomId", uomId));
								tobeStored.add(productUom);
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(productUomsRemove)) {
						delegator.removeAll(productUomsRemove);
					}
    			}
    			if (!tobeStored.isEmpty()) delegator.storeAll(tobeStored);
    			index++;
    		}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling calcProductPackagingUom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
	
	public static Map<String, Object> updateProductPackagingUom(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String infoUpdate = (String) context.get("infoUpdate");
			String productId = (String) context.get("productId");
			
			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
			
			if ("Product".equals(infoUpdate)) {
				String quantityUomId = (String) context.get("quantityUomId");
				if (UtilValidate.isNotEmpty(quantityUomId)) {
					GenericValue product = delegator.findOne("ProductPackagingUom", UtilMisc.toMap("productId", productId, "uomId", quantityUomId), false);
					if (product == null) {
						product = delegator.makeValue("ProductPackagingUom", UtilMisc.toMap("productId", productId, "uomId", quantityUomId));
						tobeStored.add(product);
					}
				} else {
					List<GenericValue> productUomsRemove = delegator.findByAnd("ProductPackagingUom", UtilMisc.toMap("productId", productId), null, false);
					
					List<String> uomIds = getQuantityUomIds(productId, delegator);
					if (UtilValidate.isNotEmpty(uomIds)) {
						List<String> uomIdsExisted = EntityUtil.getFieldListFromEntityList(productUomsRemove, "uomId", true);
						for (String uomId : uomIds) {
							if (uomIdsExisted.contains(uomId)) {
								List<GenericValue> tmpList = EntityUtil.filterByCondition(productUomsRemove, EntityCondition.makeCondition("uomId", uomId));
								if (UtilValidate.isNotEmpty(tmpList)) {
									productUomsRemove.removeAll(tmpList);
								}
								continue;
							} else {
								GenericValue productUom = delegator.makeValue("ProductPackagingUom", UtilMisc.toMap("productId", productId, "uomId", uomId));
								tobeStored.add(productUom);
							}
						}
					}
					
					if (UtilValidate.isNotEmpty(productUomsRemove)) {
						delegator.removeAll(productUomsRemove);
					}
				}
			} else if ("ConfigPacking".equals(infoUpdate)) {
				List<GenericValue> productUomsRemove = delegator.findByAnd("ProductPackagingUom", UtilMisc.toMap("productId", productId), null, false);
				
				List<String> uomIds = getQuantityUomIds(productId, delegator);
				if (UtilValidate.isNotEmpty(uomIds)) {
					List<String> uomIdsExisted = EntityUtil.getFieldListFromEntityList(productUomsRemove, "uomId", true);
					for (String uomId : uomIds) {
						if (uomIdsExisted.contains(uomId)) {
							List<GenericValue> tmpList = EntityUtil.filterByCondition(productUomsRemove, EntityCondition.makeCondition("uomId", uomId));
							if (UtilValidate.isNotEmpty(tmpList)) {
								productUomsRemove.removeAll(tmpList);
							}
							continue;
						} else {
							GenericValue productUom = delegator.makeValue("ProductPackagingUom", UtilMisc.toMap("productId", productId, "uomId", uomId));
							tobeStored.add(productUom);
						}
					}
				}
				
				if (UtilValidate.isNotEmpty(productUomsRemove)) {
					delegator.removeAll(productUomsRemove);
				}
			}
			
			if (!tobeStored.isEmpty()) {
				delegator.storeAll(tobeStored);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateProductPackagingUom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	private static List<String> getQuantityUomIds(String productId, Delegator delegator) throws GenericEntityException {
		List<String> resultValue = FastList.newInstance();
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		if (product == null) {
			return resultValue;
		}
		
		Set<String> uomIds = FastSet.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("productId", productId));
		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listConfigPacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conds), null, null, null, false);
		for (GenericValue conPackItem : listConfigPacking) {
			String uomFromId = conPackItem.getString("uomFromId");
			String uomToId = conPackItem.getString("uomToId");
			if (!uomIds.contains(uomFromId)) {
				uomIds.add(uomFromId);
			}
			if (!uomIds.contains(uomToId)) {
				uomIds.add(uomToId);
			}
		}
		String quantityUomId = product.getString("quantityUomId");
		if (UtilValidate.isNotEmpty(quantityUomId) && !uomIds.contains(quantityUomId)) {
			uomIds.add(quantityUomId);
		}
		resultValue.addAll(uomIds);
		return resultValue;
	}
	
	/**
	 * Create new data ProductAndCatalogTempData
	 * @param delegator
	 * @param catalogId
	 * @param productId
	 * @param fromDate
	 * @param thruDate
	 * @param sequenceNum
	 * @param hasProcessChildProduct
	 * @return
	 */
	private static List<GenericValue> processCreateProductAndCatalogTempData(Delegator delegator, String catalogId, String productId, 
			Timestamp fromDate, Timestamp thruDate, Long sequenceNum, Boolean hasProcessChildProduct) {
		List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		
		GenericValue productTmp = delegator.makeValue("ProductAndCatalogTempData");
		productTmp.put("prodCatalogId", catalogId);
		productTmp.put("productId", productId);
		productTmp.put("fromDate", fromDate);
		productTmp.put("sequenceNum", sequenceNum);
		if (thruDate != null) productTmp.put("thruDate", thruDate);
		tobeStored.add(productTmp);
		
		if (hasProcessChildProduct) {
			// add all children product
			List<GenericValue> childrenProducts = ProductWorker.getChildrenAssocProduct(productId, delegator, null);
			if (UtilValidate.isNotEmpty(childrenProducts)) {
				for (GenericValue childProduct : childrenProducts) {
					GenericValue childProductTmp = delegator.makeValue("ProductAndCatalogTempData");
					childProductTmp.put("prodCatalogId", catalogId);
					childProductTmp.put("productId", childProduct.getString("productIdTo"));
					childProductTmp.put("fromDate", fromDate);
					childProductTmp.put("sequenceNum", sequenceNum);
					if (thruDate != null) childProductTmp.put("thruDate", thruDate);
					tobeStored.add(childProductTmp);
				}
			}
		}
		return tobeStored;
	}
	
	private static List<GenericValue> processThruDateProductAndCatalogTempData(Delegator delegator, GenericValue productAndCatalogTempData, Timestamp thruDate, Boolean hasProcessChildProduct) throws GenericEntityException {
		List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		productAndCatalogTempData.set("thruDate", thruDate);
		
		if (hasProcessChildProduct) {
			// thru date all children product
			List<GenericValue> childrenProducts = ProductWorker.getChildrenAssocProduct(productAndCatalogTempData.getString("productId"), delegator, null);
			if (UtilValidate.isNotEmpty(childrenProducts)) {
				List<String> childrenProductIds = EntityUtil.getFieldListFromEntityList(childrenProducts, "productIdTo", true);
				if (UtilValidate.isNotEmpty(childrenProductIds)) {
					List<EntityCondition> itemConds = new ArrayList<EntityCondition>();
					itemConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, childrenProductIds));
					itemConds.add(EntityCondition.makeCondition("prodCatalogId", productAndCatalogTempData.getString("prodCatalogId")));
					itemConds.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> childrenProductTmps = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(itemConds), null, null, null, false);
    				if (UtilValidate.isNotEmpty(childrenProductTmps)) {
    					for (GenericValue childProduct : childrenProductTmps) {
    						childProduct.put("thruDate", thruDate);
    						//childProduct.put("sequenceNum", sequenceNum);
						}
    					tobeStored.addAll(childrenProductTmps);
    				}
				}
			}
		}
		tobeStored.add(productAndCatalogTempData);
		return tobeStored;
	}
	
	private static List<GenericValue> processUpdateProductAndCatalogTempData(Delegator delegator, String productId, String prodCatalogId, 
			Timestamp fromDate, Timestamp thruDate, Boolean hasProcessChildProduct, Long sequenceNum) throws GenericEntityException {
		List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		List<EntityCondition> itemConds = new ArrayList<EntityCondition>();
		itemConds.clear();
		itemConds.add(EntityCondition.makeCondition("productId", productId));
		itemConds.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
		itemConds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> productTmps = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(itemConds), null, null, null, false);
		if (UtilValidate.isEmpty(productTmps)) {
			// create new
			GenericValue productTmp = delegator.makeValue("ProductAndCatalogTempData");
			productTmp.put("prodCatalogId", prodCatalogId);
			productTmp.put("productId", productId);
			productTmp.put("fromDate", fromDate);
			productTmp.put("sequenceNum", sequenceNum);
			if (thruDate != null) productTmp.put("thruDate", thruDate);
			tobeStored.add(productTmp);
			
			if (hasProcessChildProduct) {
				// add all children product
				List<GenericValue> childrenProducts = ProductWorker.getChildrenAssocProduct(productId, delegator, null);
				if (UtilValidate.isNotEmpty(childrenProducts)) {
					for (GenericValue childProduct : childrenProducts) {
						GenericValue childProductTmp = delegator.makeValue("ProductAndCatalogTempData");
    					childProductTmp.put("prodCatalogId", prodCatalogId);
    					childProductTmp.put("productId", childProduct.getString("productIdTo"));
    					childProductTmp.put("fromDate", fromDate);
    					childProductTmp.put("sequenceNum", sequenceNum);
    					if (thruDate != null) childProductTmp.put("thruDate", thruDate);
						tobeStored.add(childProductTmp);
					}
				}
			}
		} else {
			if (thruDate != null) {
				for (GenericValue productTmp : productTmps) {
					productTmp.set("thruDate", thruDate);
					productTmp.set("sequenceNum", sequenceNum);
					
					if (hasProcessChildProduct) {
						// thru date all children product
						List<GenericValue> childrenProducts = ProductWorker.getChildrenAssocProduct(productTmp.getString("productId"), delegator, null);
						if (UtilValidate.isNotEmpty(childrenProducts)) {
							List<String> childrenProductIds = EntityUtil.getFieldListFromEntityList(childrenProducts, "productIdTo", true);
							if (UtilValidate.isNotEmpty(childrenProductIds)) {
								itemConds.clear();
								itemConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, childrenProductIds));
								itemConds.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
								itemConds.add(EntityUtil.getFilterByDateExpr());
								List<GenericValue> childrenProductTmps = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(itemConds), null, null, null, false);
			    				if (UtilValidate.isNotEmpty(childrenProductTmps)) {
			    					for (GenericValue childProduct : childrenProductTmps) {
			    						childProduct.put("thruDate", thruDate);
			    						childProduct.put("sequenceNum", sequenceNum);
									}
			    					tobeStored.addAll(childrenProductTmps);
			    				}
							}
						}
					}
				}
				tobeStored.addAll(productTmps);
			} else {
				for (GenericValue productTmp : productTmps) {
					productTmp.set("sequenceNum", sequenceNum);
					
					if (hasProcessChildProduct) {
						// thru date all children product
						List<GenericValue> childrenProducts = ProductWorker.getChildrenAssocProduct(productTmp.getString("productId"), delegator, null);
						if (UtilValidate.isNotEmpty(childrenProducts)) {
							List<String> childrenProductIds = EntityUtil.getFieldListFromEntityList(childrenProducts, "productIdTo", true);
							if (UtilValidate.isNotEmpty(childrenProductIds)) {
								itemConds.clear();
								itemConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, childrenProductIds));
								itemConds.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
								itemConds.add(EntityUtil.getFilterByDateExpr());
								List<GenericValue> childrenProductTmps = delegator.findList("ProductAndCatalogTempData", EntityCondition.makeCondition(itemConds), null, null, null, false);
			    				if (UtilValidate.isNotEmpty(childrenProductTmps)) {
			    					for (GenericValue childProduct : childrenProductTmps) {
			    						childProduct.put("sequenceNum", sequenceNum);
									}
			    					tobeStored.addAll(childrenProductTmps);
			    				}
							}
						}
					}
				}
				tobeStored.addAll(productTmps);
			}
		}
		
		//prodCatalogIds.add(itemCatalogId);
		return tobeStored;
	}
	
	public static Map<String, Object> getProductIdAndAmountByUPCA(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String idUPCA = (String) context.get("idUPCA");
			boolean checkCode = ProductUtils.isValidUpc(idUPCA);
			if (!checkCode) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSInvalidCode", locale));
			}
			String pluCode = ProductUtils.getPluCodeInUpcId(idUPCA);
			GenericValue productPlu = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("goodIdentificationTypeId", "PLU", "idValue", pluCode), null, false));
			if (productPlu != null) {
				GenericValue product = productPlu.getRelatedOne("Product", false);
				if (product != null) {
					successResult.put("productId", product.get("productId"));
					
					BigDecimal price = ProductWorker.getPriceProductUpc(product, idUPCA, delegator, locale);
					if (price == null) {
						BigDecimal amount = ProductWorker.getAmountProductUpc(product, idUPCA, delegator, dispatcher, locale);
						successResult.put("amount", amount);
					}
					successResult.put("price", price);
				}
			}
			successResult.put("idPLU", pluCode);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getProductIdAndAmountByUPCA service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	public static Map<String, Object> getProductIdAndAmountByEAN(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		//LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String idEAN = (String) context.get("idEAN");
			boolean checkCode = ProductUtils.isValidEan(idEAN);
			if (!checkCode) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSInvalidCode", locale));
			}
			String pluCode = ProductUtils.getPluCodeInEanId(idEAN);
			GenericValue productPlu = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("goodIdentificationTypeId", "PLU", "idValue", pluCode), null, false));
			if (productPlu != null) {
				GenericValue product = productPlu.getRelatedOne("Product", false);
				if (product != null) {
					successResult.put("productId", product.get("productId"));
					
					BigDecimal amount = ProductWorker.getAmountProductEan(idEAN, delegator, locale);
					successResult.put("amount", amount);
				}
			}
			successResult.put("idPLU", pluCode);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getProductIdAndAmountByEAN service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProdExportPrice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String inlucdeProductPrice = SalesUtil.getParameter(parameters, "includePriceRule");
    		GenericValue productStore = null;
			if ("Y".equals(inlucdeProductPrice)) {
    			String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
    			productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
    			if (productStore == null) {
    				Debug.logInfo("Error: Print product price with option 'inlucdeProductPrice' = Y, but product store is null", module);
    				successResult.put("TotalRows", "0");
    				successResult.put("listIterator", listIterator);
    				return successResult;
    			}
			}
    		
    		String filterType = SalesUtil.getParameter(parameters, "filterType");
    		if (UtilValidate.isEmpty(listSortFields)) listSortFields.add("productCode");
    		
    		Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("primaryProductCategoryId");
            selectFields.add("quantityUomId");
            selectFields.add("currencyUomId");
            selectFields.add("supplierCode");
            selectFields.add("idSKU");
            selectFields.add("iupprm");
            selectFields.add("uomId");
    		
    		EntityListIterator listIteratorI = null;
    		if ("ALLPROD".equals(filterType)) {
    			listIteratorI = delegator.find("ProductTempDataDetailAndSupplierRef", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
    		} else if ("BYCATALOG".equals(filterType)) {
    			String prodCatalogId = SalesUtil.getParameter(parameters, "prodCatalogId");
    			List<String> productCategoryIds = FastList.newInstance();
    			if (UtilValidate.isNotEmpty(prodCatalogId)) {
    				List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogId, "PCCT_BROWSE_ROOT");
    				if (listCategory != null) {
    					for (GenericValue categoryItem : listCategory) {
    						productCategoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId"), "CATALOG_CATEGORY"));
    					}
    				}
    			}
    			if (UtilValidate.isNotEmpty(productCategoryIds)) {
    				listAllConditions.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN, productCategoryIds));
    				listIteratorI = delegator.find("ProductTempDataDetailAndSupplierRef", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, selectFields, listSortFields, opts);
    			}
    		} else if ("BYCATEGORY".equals(filterType)) {
    			String productCategoryId = SalesUtil.getParameter(parameters, "productCategoryId");
    			if (UtilValidate.isNotEmpty(productCategoryId)) {
    				List<String> productCategoryIds = FastList.newInstance();
    				productCategoryIds.addAll(ProductWorker.getAllCategoryTree(delegator, productCategoryId, "CATALOG_CATEGORY"));
    				listAllConditions.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN, productCategoryIds));
    				listIteratorI = delegator.find("ProductTempDataDetailAndSupplierRef", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, selectFields, listSortFields, opts);
    			}
    		}
    		
    		List<GenericValue> listTmp = SalesUtil.processIterator(listIteratorI, parameters, successResult);
    		if (UtilValidate.isNotEmpty(listTmp)) {
    			if (productStore != null) {
    				Map<String, Object> productIdPrice = FastMap.newInstance();
    				for (GenericValue item : listTmp) {
    					Map<String, Object> itemMap = item.getAllFields();
    					String productId = item.getString("productId");
    					String uomId = item.getString("uomId");

    					// get info tax category
    					String taxCategoryId = null;
    					BigDecimal taxPercentage = null;
    					Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
    					if (productTax != null) {
    						taxCategoryId = (String) productTax.get("taxCategoryId");
    						taxPercentage = (BigDecimal) productTax.get("taxPercentage");
    					}
    					itemMap.put("taxCategoryId", taxCategoryId);
    					itemMap.put("taxPercentage", taxPercentage);
    					
    					BigDecimal unitPrice = null;
    					BigDecimal unitListPrice = null;
    					String productIdKey = uomId != null ? productId + "@" + uomId : productId + "@_NA_";
    					if (!productIdPrice.containsKey(productIdKey)) {
    						Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap(
            	        			"productId", productId, "productStoreId", productStore.getString("productStoreId"),
            	        			"quantityUomId", uomId);
            	        	Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
            	        	if (ServiceUtil.isError(resultCalPrice)) {
            	        		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCalPrice));
            	        	}
            	        	unitPrice = (BigDecimal) resultCalPrice.get("basePrice");
            	        	unitListPrice = (BigDecimal) resultCalPrice.get("listPrice");
            	        	
            	        	productIdPrice.put(productIdKey, UtilMisc.toMap("basePrice", unitPrice, "listPrice", unitListPrice));
    					} else {
    						Map<String, Object> pricesInfo = (Map<String, Object>) productIdPrice.get(productIdKey);
    						unitPrice = (BigDecimal) pricesInfo.get("basePrice");
    						unitListPrice = (BigDecimal) pricesInfo.get("listPrice");
    					}
    					BigDecimal unitPriceVAT = ProductWorker.calculatePriceAfterTax(unitPrice, taxPercentage);
        	        	BigDecimal unitListPriceVAT = ProductWorker.calculatePriceAfterTax(unitListPrice, taxPercentage);
        	        	itemMap.put("unitPrice", unitPrice);
        	        	itemMap.put("unitListPrice", unitListPrice);
        	        	itemMap.put("unitPriceVAT", unitPriceVAT);
        	        	itemMap.put("unitListPriceVAT", unitListPriceVAT);
        	        	listIterator.add(itemMap);
        			}
    			} else {
    				Map<String, Object> productIdPrice = FastMap.newInstance();
    				for (GenericValue item : listTmp) {
    					Map<String, Object> itemMap = item.getAllFields();
    					String productId = item.getString("productId");
    					//String quantityUomId = item.getString("quantityUomId");
    					String uomId = item.getString("uomId");
    					
    					// get info tax category
    					String taxCategoryId = null;
    					BigDecimal taxPercentage = null;
    					Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
    					if (productTax != null) {
    						taxCategoryId = (String) productTax.get("taxCategoryId");
    						taxPercentage = (BigDecimal) productTax.get("taxPercentage");
    					}
    					itemMap.put("taxCategoryId", taxCategoryId);
    					itemMap.put("taxPercentage", taxPercentage);
    					
    					BigDecimal unitPrice = null;
    					BigDecimal unitListPrice = null;
    					//if (quantityUomId != null && quantityUomId.equals(uomId)) {
    						String productIdKey = uomId != null ? productId + "@" + uomId : productId + "@_NA_";
        					if (!productIdPrice.containsKey(productIdKey)) {
        						Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, "productStoreId", null,
                	        			"quantityUomId", uomId);
                	        	Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
                	        	if (ServiceUtil.isError(resultCalPrice)) {
                	        		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCalPrice));
                	        	}
                	        	unitPrice = (BigDecimal) resultCalPrice.get("basePrice");
                	        	unitListPrice = (BigDecimal) resultCalPrice.get("listPrice");
                	        	
                	        	productIdPrice.put(productIdKey, UtilMisc.toMap("basePrice", unitPrice, "listPrice", unitListPrice));
        					} else {
        						Map<String, Object> pricesInfo = (Map<String, Object>) productIdPrice.get(productIdKey);
        						unitPrice = (BigDecimal) pricesInfo.get("basePrice");
        						unitListPrice = (BigDecimal) pricesInfo.get("listPrice");
        					}
    					//}
    					BigDecimal unitPriceVAT = ProductWorker.calculatePriceAfterTax(unitPrice, taxPercentage);
        	        	BigDecimal unitListPriceVAT = ProductWorker.calculatePriceAfterTax(unitListPrice, taxPercentage);
        	        	itemMap.put("unitPrice", unitPrice);
        	        	itemMap.put("unitListPrice", unitListPrice);
        	        	itemMap.put("unitPriceVAT", unitPriceVAT);
        	        	itemMap.put("unitListPriceVAT", unitListPriceVAT);
        	        	listIterator.add(itemMap);
        			}
    			}
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProdExportPrice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> getInfoProductAddToQuot(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
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
			
			GenericValue productTax = EntityUtil.getFirst(delegator.findByAnd("ProductAndTaxAuthorityRateSimple", UtilMisc.toMap("productId", productId), null, false));
			if (productTax != null) {
				productInfo.put("taxPercentage", productTax.get("taxPercentage"));
			}
			
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
		} catch (Exception e) {
			String errMsg = "Fatal error calling getInfoProductAddToQuot service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("productInfo", productInfo);
		return successResult;
	}
	
	public static Map<String, Object> getInfoProductAddToExportPrice(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, Object> productInfo = FastMap.newInstance();
		try {
			String productId = null;
			String uomId = null;
			String productStoreId = (String) context.get("productStoreId");
			String idSKU = (String) context.get("idSKU");
			GenericValue goodIdPrimary = null;
			
			GenericValue goodIdentification = null;
			if (UtilValidate.isNotEmpty(idSKU)) {
				goodIdentification = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("idValue", idSKU), null, false));
				if (goodIdentification != null) {
					productId = goodIdentification.getString("productId");
					uomId = goodIdentification.getString("uomId");
					Long iupprm = goodIdentification.getLong("iupprm");
					if (iupprm != null && iupprm == 1) {
						goodIdPrimary = goodIdentification;
					}
				}
			}
			if (UtilValidate.isEmpty(productId)) {
				productId = (String) context.get("productId");
				if (UtilValidate.isEmpty(productId)) {
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
				}
				uomId = (String) context.get("quantityUomId");
			}
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (product == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
			}
			
			if (goodIdPrimary == null) {
				// get idSKU primary iupprm = 1
				goodIdPrimary = EntityUtil.getFirst(delegator.findByAnd("ProductIdAndGoodIdSKUPrimary", UtilMisc.toMap("productId", productId, "uomId", uomId), null, false));
			}
			if (goodIdPrimary == null) {
				// if primary idSKU is null then get default idSKU input
				goodIdPrimary = goodIdentification;
			}
			String idSKUPrimary = null;
			String iupprm = null;
			if (goodIdPrimary != null) {
				idSKUPrimary = goodIdPrimary.getString("idValue");
				iupprm = goodIdPrimary.getString("iupprm");
			}
			productInfo.put("idSKU", idSKUPrimary);
			productInfo.put("iupprm", iupprm);
			
			// Miss: parentProductId, parentProductCode, features, colorCode
			productInfo.put("productId", product.get("productId"));
			productInfo.put("primaryProductCategoryId", product.get("primaryProductCategoryId"));
			productInfo.put("productCode", product.get("productCode"));
			productInfo.put("productName", product.get("productName"));
			productInfo.put("quantityUomId", product.getString("quantityUomId"));
			productInfo.put("uomId", uomId);
			
			BigDecimal taxPercentage = null;
			Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
			if (productTax != null) {
				taxPercentage = (BigDecimal) productTax.get("taxPercentage");
			}
			productInfo.put("taxPercentage", taxPercentage);
			
			GenericValue productTempData = EntityUtil.getFirst(delegator.findByAnd("ProductTempData", UtilMisc.toMap("productId", productId), null, false));
			if (productTempData != null) {
				productInfo.put("currencyUomId", productTempData.get("currencyUomId"));
				//productInfo.put("unitPrice", productTempData.get("unitPrice"));
			}
			
			
			BigDecimal unitPrice = null;
			BigDecimal unitListPrice = null;
			BigDecimal unitPriceVAT = null;
			BigDecimal unitListPriceVAT = null;
			try {
				Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, "productStoreId", productStoreId, "quantityUomId", uomId);
				Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx, 300, true);
	        	if (!ServiceUtil.isError(resultCalPrice)) {
	        		unitPrice = (BigDecimal) resultCalPrice.get("basePrice");
	        		unitListPrice = (BigDecimal) resultCalPrice.get("listPrice");
	        		unitPriceVAT = ProductWorker.calculatePriceAfterTax(unitPrice, taxPercentage);
	        		unitListPriceVAT = ProductWorker.calculatePriceAfterTax(unitListPrice, taxPercentage);
	        	}
			} catch (Exception e) {
				Debug.logWarning("Error when calculate price of uom: " + uomId, module);
			}
			productInfo.put("unitPrice", unitPrice);
    		productInfo.put("unitListPrice", unitListPrice);
    		productInfo.put("unitPriceVAT", unitPriceVAT);
    		productInfo.put("unitListPriceVAT", unitListPriceVAT);
			
			// get quantity convert from UOM id to product quantity UOM id
			BigDecimal quantityConvert = UomWorker.customConvertUom(productId, uomId, product.getString("quantityUomId"), BigDecimal.ONE, delegator);
			if (quantityConvert != null && quantityConvert.compareTo(BigDecimal.ZERO) < 0) quantityConvert = BigDecimal.ZERO;
			productInfo.put("quantityConvert", quantityConvert);
			
			// get info suppliers reference 80_SGC_SUPPL
			String supplierCode = null;
			GenericValue supplierRef = EntityUtil.getFirst(delegator.findByAnd("SupplierProductPartyCodeActiveRef", UtilMisc.toMap("productId", productId), null, false));
			if (supplierRef != null) {
				supplierCode = supplierRef.getString("partyCode");
			}
			productInfo.put("supplierCode", supplierCode);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getInfoProductAddToExportPrice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("productInfo", productInfo);
		return successResult;
	}

	public static Map<String, Object> getInfoProductAddToPlanSalePrice(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
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
			
			//List<Map<String, Object>> packingUomIds = ProductWorker.getListQuantityUomIds(product.getString("productId"), product.getString("quantityUomId"), delegator, dispatcher);
			//productInfo.put("packingUomIds", packingUomIds);
			
			Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
			if (productTax != null) {
				productInfo.put("taxPercentage", productTax.get("taxPercentage"));
			}
			
			String currencyUomId = null;
			GenericValue productTempData = EntityUtil.getFirst(delegator.findByAnd("ProductTempData", UtilMisc.toMap("productId", productId), null, false));
			if (productTempData != null) {
				currencyUomId = productTempData.getString("currencyUomId");
				productInfo.put("currencyUomId", currencyUomId);
				productInfo.put("unitPrice", productTempData.get("unitPrice"));
			}
			
			/*if (UtilValidate.isNotEmpty(packingUomIds)) {
				for (Map<String, Object> uomItem : packingUomIds) {
					if (quantityUomId != null && quantityUomId.equals((String) uomItem.get("uomId"))) {
						productInfo.put("quantityConvert", uomItem.get("quantityConvert"));
						productInfo.put("unitPrice", uomItem.get("unitPriceConvert"));
					}
				}
			}*/
			
			Map<String, Object> calcPriceResult = dispatcher.runSync("calculateProductPriceWithTax", UtilMisc.toMap("productId", productId, "quantityUomId", quantityUomId, "currencyUomId", currencyUomId));
			if (ServiceUtil.isSuccess(calcPriceResult)) {
				productInfo.put("defaultPrice", calcPriceResult.get("defaultPrice"));
				productInfo.put("listPrice", calcPriceResult.get("listPrice"));
				productInfo.put("defaultPriceVAT", calcPriceResult.get("defaultPriceTax"));
				productInfo.put("listPriceVAT", calcPriceResult.get("listPriceTax"));
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getInfoProductAddToPlanSalePrice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("productInfo", productInfo);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqExportProductInSalesCategory(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		try {
			boolean activeIsNowTimestamp = true;
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("productCode");
			}
			
			List<EntityCondition> mainCondList = FastList.newInstance();
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			}
			
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            //selectFields.add("currencyUomId");
            selectFields.add("idSKU");
            selectFields.add("iupprm");
            selectFields.add("primaryProductCategoryId");
            selectFields.add("purchaseUomId");
            selectFields.add("salesUomId");
            selectFields.add("salesDiscontinuationDate");
            selectFields.add("purchaseDiscontinuationDate");
            selectFields.add("partyId");
            selectFields.add("partyCode");
            
            List<String> prodCatalogIds = FastList.newInstance();
			String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
			if (UtilValidate.isNotEmpty(productStoreId)) {
				List<EntityCondition> condsStore = FastList.newInstance();
				condsStore.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				condsStore.add(EntityUtil.getFilterByDateExpr());
				prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(condsStore), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
			}
            
			if (UtilValidate.isNotEmpty(prodCatalogIds)) {
				if (prodCatalogIds.size() == 1) listAllConditions.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogIds.get(0)));
				else listAllConditions.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
				
				// filter product is selling
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), 
            			EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
				
				EntityListIterator iterator = delegator.find("ProductAndGoodIdsAndCatalogAndSupplier", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
	            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, successResult);
	            
	            if (listProduct != null) {
	            	List<EntityCondition> condsCurrency = FastList.newInstance();
	            	for (GenericValue itemProd : listProduct) {
	        			Map<String, Object> itemMap = itemProd.getAllFields();
	        			String productId = itemProd.getString("productId");
	        			
	        			// get quantity convert from purchase UOM id to quantity UOM id
	        			BigDecimal quantityConvert = UomWorker.customConvertUom(productId, itemProd.getString("purchaseUomId"), itemProd.getString("quantityUomId"), BigDecimal.ONE, delegator);
	        			if (quantityConvert != null && quantityConvert.compareTo(BigDecimal.ZERO) < 0) quantityConvert = BigDecimal.ZERO;
	        			itemMap.put("quantityConvert", quantityConvert);
	        			
	        			// get currency UOM
	        			String currencyUomId = null;
	        			condsCurrency.clear();
	        			condsCurrency.add(EntityCondition.makeCondition("productId", productId));
	        			condsCurrency.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.NOT_EQUAL, null));
	        			condsCurrency.add(EntityUtil.getFilterByDateExpr());
	        			EntityFindOptions optsCurrency = new EntityFindOptions();
	        			optsCurrency.setMaxRows(1);
	        			GenericValue productPrice = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(condsCurrency), null, null, optsCurrency, false));
	        			if (productPrice != null) {
	        				currencyUomId = productPrice.getString("currencyUomId");
	        			}
	        			itemMap.put("currencyUomId", currencyUomId);
	        			
	        			// get info tax category
	        			String taxCategoryId = null;
	        			BigDecimal taxPercentage = null;
	        			List<EntityCondition> condsTax = FastList.newInstance();
	    				condsTax.add(EntityCondition.makeCondition("productId", productId));
	    				condsTax.add(EntityUtil.getFilterByDateExpr());
	    				GenericValue taxCategoryGV = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRateSimple", EntityCondition.makeCondition(condsTax), null, null, null, false));
	    				if (taxCategoryGV != null) {
	    					taxCategoryId = taxCategoryGV.getString("taxCategoryId");
	    					taxPercentage = taxCategoryGV.getBigDecimal("taxPercentage");
	    				}
		    			itemMap.put("taxCategoryId", taxCategoryId);
		    			itemMap.put("taxPercentage", taxPercentage);
	        			
	        			// get info supplier
	        			String supplierId = itemProd.getString("partyId");
	        			String supplierCode = itemProd.getString("partyCode");
	        			BigDecimal purchasePriceVAT = null;
	        			if (UtilValidate.isNotEmpty(supplierId)) {
        					if (UtilValidate.isNotEmpty(productId) 
        							&& UtilValidate.isNotEmpty(currencyUomId) 
        							&& UtilValidate.isNotEmpty(supplierId)
        							&& UtilValidate.isNotEmpty(itemProd.getString("purchaseUomId"))) {
        						try {
	        						Map<String, Object> purchasePriceCtx = UtilMisc.toMap("productId", productId, 
	        								"partyId", supplierId, "currencyUomId", currencyUomId, 
	        								"uomId", itemProd.getString("purchaseUomId"),
	        								"quantity", "1", "userLogin", userLogin);
	        						Map<String, Object> purchasePriceResult = dispatcher.runSync("getLastPriceBySupplierProductAndQuantity", purchasePriceCtx, 300, true);
	        						if (ServiceUtil.isSuccess(purchasePriceResult)) {
	        							if (UtilValidate.isNotEmpty(purchasePriceResult.get("lastPrice"))) {
		        							purchasePriceVAT = (BigDecimal) purchasePriceResult.get("lastPrice");
	        								purchasePriceVAT = ProductWorker.calculatePriceAfterTax(purchasePriceVAT, taxPercentage);
		        						}
	        						} else {
        								Debug.logWarning("Error when get purchase price of product in jqExportProductInSalesCategory service, productId = " + productId, module);
        							}
	        					} catch (Exception e1) {
	        						Debug.logWarning("Error when get purchase price of product in jqExportProductInSalesCategory service, productId = " + productId, module);
	        					}
    						}
	        			}
	        			itemMap.put("supplierId", supplierId);
	        			itemMap.put("supplierCode", supplierCode);
	        			itemMap.put("purchasePriceVAT", purchasePriceVAT);
	        			
	        			// get info sales price and quotation id
	        			BigDecimal salesPriceVAT = null;
	        			String quotationId = null;
	        			Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, "productStoreId", productStoreId,
	    	        			"quantityUomId", itemProd.get("quantityUomId"));
	    	        	Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
	        			if (ServiceUtil.isSuccess(resultValue)) {
	        				//BigDecimal listPrice = (BigDecimal) resultValue.get("listPrice");
	        				
	        				salesPriceVAT = (BigDecimal) resultValue.get("basePrice");
	        				salesPriceVAT = ProductWorker.calculatePriceAfterTax(salesPriceVAT, taxPercentage);
	        				
	        				// get info quotation
	        				List<GenericValue> orderItemPriceInfos = (List<GenericValue>) resultValue.get("orderItemPriceInfos");
	        				if (UtilValidate.isNotEmpty(orderItemPriceInfos)) {
	        					for (GenericValue itemPrice : orderItemPriceInfos) {
	        						if ("OrderItemPriceInfo".equals(itemPrice.getEntityName())) {
	        							String productPriceRuleId = itemPrice.getString("productPriceRuleId");
	        							GenericValue priceRule = delegator.findOne("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), false);
	        							if (priceRule != null) {
	        								if (UtilValidate.isNotEmpty(priceRule.get("productQuotationId"))) {
	        									if (quotationId == null) quotationId = priceRule.getString("productQuotationId");
	        									else quotationId += ", " + priceRule.getString("productQuotationId");
	        								}
	        							}
	        						}
	        					}
	        				}
	        			}
	        			itemMap.put("salesPriceVAT", salesPriceVAT);
	        			itemMap.put("quotationId", quotationId);
	        			
	        			listIterator.add(itemMap);
					}
	            }
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqExportProductInSalesCategory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
