package com.olbius.basepo.product;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductCategoryServices {
	public static final String module = ProductCategoryServices.class.getName();
	public static final String resource = "BasePOUiLabels";
	
	public static Map<String, Object> checkProductCategoryId(Delegator delegator, String productCategoryId, Locale locale) throws Exception {
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCategoryId"), EntityJoinOperator.EQUALS, productCategoryId.toUpperCase()));
			List<GenericValue> product = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), null, findOptions, false);
			if (UtilValidate.isNotEmpty(product)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSProductCategoryIdAlreadyExists", locale));
			}
		} catch (Exception e) {
			throw e;
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> checkProductCategoryId(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			Map<String, Object> checkResult = checkProductCategoryId(delegator, (String) context.get("productCategoryId"), locale);
			if (ServiceUtil.isError(checkResult)) {
				result.put("check", "false");
			} else {
				result.put("check", "true");
			}
		} catch (Exception e) {
			result.put("check", "false");
		}
		return result;
	}
	
	public static Map<String, Object> createProductCategoryAndRollup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		try {
			String categoryName = (String) context.get("categoryName");
			
			Map<String, Object> checkResult = checkProductCategoryId(delegator, (String) context.get("productCategoryId"), locale);
			if (ServiceUtil.isError(checkResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(checkResult));
			}
			
			Object sequenceNum = context.get("sequenceNum");
			context.remove("sequenceNum");
			dispatcher.runSync("createProductCategory", context);
			String primaryParentCategoryId = (String) context.get("primaryParentCategoryId");
			String productCategoryId = (String) context.get("productCategoryId");
			
			if (UtilValidate.isNotEmpty(primaryParentCategoryId)) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityUtil.getFilterByDateExpr());
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", primaryParentCategoryId)));
				List<GenericValue> listProductCategoryRollup = delegator.findList("ProductCategoryRollup",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(listProductCategoryRollup)) {
					delegator.create("ProductCategoryRollup",
							UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", primaryParentCategoryId,
									"fromDate", new Timestamp(System.currentTimeMillis()), "sequenceNum", sequenceNum));
				}
			}
			//dispatcher.runSync("fixCategory", UtilMisc.toMap("userLogin", context.get("userLogin")));
			Map<String, Object> createCategoriesRelative = createOrUpdateCategoryAlternative(dispatcher, delegator, locale, userLogin, productCategoryId, categoryName);
			if (ServiceUtil.isError(createCategoriesRelative)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createCategoriesRelative));
			}
			result.put("productCategoryId", productCategoryId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	/**
	 * Create product categories as: best selling (_BSL), promotion (_PROMOS), new (_NEW), featured (_FEATURED)
	 * @param dispatcher
	 * @param delegator
	 * @param locale
	 * @param userLogin
	 * @param productCategoryId
	 * @param categoryName
	 * @return
	 * @throws GenericEntityException
	 * @throws GenericServiceException
	 */
	private static Map<String, Object> createOrUpdateCategoryAlternative(LocalDispatcher dispatcher, Delegator delegator, Locale locale, GenericValue userLogin, 
			String productCategoryId, String categoryName) throws GenericEntityException, GenericServiceException {
		// extend from "checkProductCategoryExists" method
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String prefixCategoryId = productCategoryId.toUpperCase();
		
		// process product category BESS SELLING
		String productCategoryId_BSL = prefixCategoryId + "_BSL";
		String categoryName_BSL = categoryName + " (" + UtilProperties.getMessage(resource, "BSPCBestSelling", locale) + ")";
		GenericValue productCategory_BSL = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId_BSL), false);
		if (UtilValidate.isEmpty(productCategory_BSL)) {
			Map<String, Object> createResult = dispatcher.runSync("createProductCategory", UtilMisc.toMap(
															"productCategoryId", productCategoryId_BSL, 
															"productCategoryTypeId", "BEST_SELL_CATEGORY", 
															"categoryName", categoryName_BSL, "userLogin", userLogin));
			if (ServiceUtil.isError(createResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
			}
		} else {
			if (!categoryName_BSL.equals(productCategory_BSL.getString("categoryName"))) {
				Map<String, Object> updateResult = dispatcher.runSync("updateProductCategory", UtilMisc.toMap(
																"productCategoryId", productCategoryId_BSL, 
																"categoryName", categoryName_BSL, "userLogin", userLogin));
				if (ServiceUtil.isError(updateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateResult));
				}
			}
		}
		// process product category PROMOS
		String productCategoryId_PROMOS = prefixCategoryId + "_PROMOS";
		String categoryName_PROMOS = categoryName + " (" + UtilProperties.getMessage(resource, "BSPCPromos", locale) + ")";
		GenericValue productCategory_PROMOS = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId_PROMOS), false);
		if (UtilValidate.isEmpty(productCategory_PROMOS)) {
			Map<String, Object> createResult = dispatcher.runSync("createProductCategory", UtilMisc.toMap(
															"productCategoryId", productCategoryId_PROMOS, 
															"productCategoryTypeId", "BEST_SELL_CATEGORY", 
															"categoryName", categoryName_PROMOS, "userLogin", userLogin));
			if (ServiceUtil.isError(createResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
			}
		} else {
			if (!categoryName_PROMOS.equals(productCategory_PROMOS.getString("categoryName"))) {
				Map<String, Object> updateResult = dispatcher.runSync("updateProductCategory", UtilMisc.toMap(
																"productCategoryId", productCategoryId_PROMOS, 
																"categoryName", categoryName_PROMOS, "userLogin", userLogin));
				if (ServiceUtil.isError(updateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateResult));
				}
			}
		}
		// process product category NEW
		String productCategoryId_NEW = prefixCategoryId + "_NEW";
		String categoryName_NEW = categoryName + " (" + UtilProperties.getMessage(resource, "BSPCNew", locale) + ")";
		GenericValue productCategory_NEW = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId_NEW), false);
		if (UtilValidate.isEmpty(productCategory_NEW)) {
			Map<String, Object> createResult = dispatcher.runSync("createProductCategory", UtilMisc.toMap(
															"productCategoryId", productCategoryId_NEW, 
															"productCategoryTypeId", "BEST_SELL_CATEGORY", 
															"categoryName", categoryName_NEW, "userLogin", userLogin));
			if (ServiceUtil.isError(createResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
			}
		} else {
			if (!categoryName_NEW.equals(productCategory_NEW.getString("categoryName"))) {
				Map<String, Object> updateResult = dispatcher.runSync("updateProductCategory", UtilMisc.toMap(
																"productCategoryId", productCategoryId_NEW, 
																"categoryName", categoryName_NEW, "userLogin", userLogin));
				if (ServiceUtil.isError(updateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateResult));
				}
			}
		}
		// process product category FEATURED
		String productCategoryId_FEATURED = prefixCategoryId + "_FEATURED";
		String categoryName_FEATURED = categoryName + " (" + UtilProperties.getMessage(resource, "BSPCFeatured", locale) + ")";
		GenericValue productCategory_FEATURED = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId_FEATURED), false);
		if (UtilValidate.isEmpty(productCategory_FEATURED)) {
			Map<String, Object> createResult = dispatcher.runSync("createProductCategory", UtilMisc.toMap(
															"productCategoryId", productCategoryId_FEATURED, 
															"productCategoryTypeId", "BEST_SELL_CATEGORY", 
															"categoryName", categoryName_FEATURED, "userLogin", userLogin));
			if (ServiceUtil.isError(createResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
			}
		} else {
			if (!categoryName_FEATURED.equals(productCategory_FEATURED.getString("categoryName"))) {
				Map<String, Object> updateResult = dispatcher.runSync("updateProductCategory", UtilMisc.toMap(
																"productCategoryId", productCategoryId_FEATURED, 
																"categoryName", categoryName_FEATURED, "userLogin", userLogin));
				if (ServiceUtil.isError(updateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateResult));
				}
			}
		}
		
		return successResult;
	}
	
	public static Map<String, Object> updateCategoryAndRollup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String productCategoryId = (String) context.get("productCategoryId");
			String categoryName = (String) context.get("categoryName");
			if (UtilValidate.isNotEmpty(productCategoryId)) {
				Long fromDateL = (Long) context.get("fromDate");
				Timestamp fromDate = null;
				if (UtilValidate.isNotEmpty(fromDateL)) {
					fromDate = new Timestamp(fromDateL);
				}
				Map<String, Object> updateResult = dispatcher.runSync("updateProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId, 
																"productCategoryTypeId", context.get("productCategoryTypeId"), 
																"primaryParentCategoryId", context.get("parentProductCategoryId"), 
																"categoryName", categoryName, 
																"description", context.get("description"), 
																"longDescription", context.get("longDescription"), "userLogin", context.get("userLogin")));
				if (ServiceUtil.isError(updateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateResult));
				}
				GenericValue productCategoryRollup = EntityUtil.getFirst(delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", context.get("parentProductCategoryId"), "fromDate", fromDate), null, false));
				if (productCategoryRollup != null) {
					productCategoryRollup.set("sequenceNum", context.get("sequenceNum"));
					productCategoryRollup.store();
				}
				
				// update product categories alternative
				Map<String, Object> updateAlterResult = createOrUpdateCategoryAlternative(dispatcher, delegator, locale, userLogin, productCategoryId, categoryName);
				if (ServiceUtil.isError(updateAlterResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateAlterResult));
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> addRootCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		try {
			String productCategoryId = (String) context.get("productCategoryId");
			String categoryName = (String) context.get("categoryName");
			Map<String, Object> checkResult = checkProductCategoryId(delegator, productCategoryId, locale);
			if (ServiceUtil.isError(checkResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(checkResult));
			}
			
			Map<String, Object> categoryNewResult = dispatcher.runSync("createProductCategory", context);
			if (ServiceUtil.isError(categoryNewResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(categoryNewResult));
			}
			
			// create product category has type is "RECYCLE_CATEGORY"
			String productCategoryId_recyle =  productCategoryId + "_recyle";
			String categoryName_recycle = categoryName + " Recyle";
			Map<String, Object> recycleCategoryCtx = FastMap.newInstance();
			recycleCategoryCtx.put("productCategoryId", productCategoryId_recyle);
			recycleCategoryCtx.put("categoryName", categoryName_recycle);
			recycleCategoryCtx.put("primaryParentCategoryId", productCategoryId);
			recycleCategoryCtx.put("productCategoryTypeId", "RECYCLE_CATEGORY");
			recycleCategoryCtx.put("userLogin", userLogin);
			Map<String, Object> recycleCategoryNewResult = dispatcher.runSync("createProductCategory", recycleCategoryCtx);
			if (ServiceUtil.isError(recycleCategoryNewResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(recycleCategoryNewResult));
			}
			
			// create product category roll up between recycle category and category new
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("productCategoryId", productCategoryId_recyle));
			conditions.add(EntityCondition.makeCondition("parentProductCategoryId", productCategoryId));
			conditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listProductCategoryRollup = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(listProductCategoryRollup)) {
				Map<String, Object> categoryRollupNewResult = delegator.create("ProductCategoryRollup", UtilMisc.toMap(
								"productCategoryId", productCategoryId_recyle, "parentProductCategoryId", productCategoryId,
								"fromDate", new Timestamp(System.currentTimeMillis()), "sequenceNum", new Long(1)));
				if (ServiceUtil.isError(categoryRollupNewResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(categoryRollupNewResult));
				}
			}
			
			//dispatcher.runSync("fixCategory", UtilMisc.toMap("userLogin", context.get("userLogin")));
			Map<String, Object> createCategoriesRelative = createOrUpdateCategoryAlternative(dispatcher, delegator, locale, userLogin, productCategoryId, categoryName);
			if (ServiceUtil.isError(createCategoriesRelative)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createCategoriesRelative));
			}
			successResult.put("productCategoryId", productCategoryId);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> getTreeProductCategories(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> categories = FastList.newInstance();
		try {
			List<String> categoryTypeIds = UtilMisc.toList("CATALOG_CATEGORY", "RECYCLE_CATEGORY");
			
			List<GenericValue> categoryRollups = new ArrayList<GenericValue>();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("primaryParentCategoryId", null));
			conds.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.IN, categoryTypeIds));
			List<GenericValue> listRootCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(listRootCategories)) {
				categoryRollups.addAll(listRootCategories);
				for (GenericValue x : listRootCategories) {
					categoryRollups.addAll(categoriesTree(delegator, x.getString("productCategoryId"), categoryTypeIds));
				}
			}
			
			for (GenericValue x : categoryRollups) {
				//String productCategoryId = x.getString("productCategoryId");
				//GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
				//String productCategoryTypeId = productCategory.getString("productCategoryTypeId");
				//if (categoryTypeIds.contains(productCategoryTypeId)) {
					Map<String, Object> category = FastMap.newInstance();
					//category.putAll(x);
					category.put("productCategoryId", x.getString("productCategoryId"));
					category.put("productCategoryTypeId", x.getString("productCategoryTypeId"));
					category.put("categoryName", x.getString("categoryName"));
					category.put("longDescription", x.getString("longDescription"));
					category.put("sequenceNum", x.getString("sequenceNum"));
					category.put("primaryParentCategoryId", x.getString("primaryParentCategoryId"));
					
					// get catalog
					List<String> prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProdCatalogCategory", 
							EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", x.get("productCategoryId")), 
									EntityOperator.AND, EntityUtil.getFilterByDateExpr()), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
					if (UtilValidate.isNotEmpty(prodCatalogIds)) {
						category.put("prodCatalogId", StringUtils.join(prodCatalogIds, "<br/>"));
					}
					
					categories.add(category);
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("categories", categories);
		return result;
	}
	private static List<GenericValue> categoriesTree(Delegator delegator, String parentProductCategoryId, List<String> categoryTypeIds)
			throws GenericEntityException {
		List<GenericValue> categories = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("parentProductCategoryId", EntityJoinOperator.EQUALS, parentProductCategoryId));
		conditions.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.IN, categoryTypeIds));
		List<GenericValue> currentBough = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("sequenceNum"), null, false);
		categories.addAll(currentBough);
		for (GenericValue x : currentBough) {
			categories.addAll(categoriesTree(delegator, x.getString("productCategoryId"), categoryTypeIds));
		}
		return categories;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductCategoryTax(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
    		listIterator = delegator.find("ProductCategory", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListProductCategoryTax service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	
}
