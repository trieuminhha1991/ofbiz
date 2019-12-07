package com.olbius.basepo.product;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.*;

import com.olbius.administration.util.CrabEntity;
import com.olbius.administration.util.UniqueUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.utils.POUtil;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.InventoryUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProductServices {
	public static final String module = ProductServices.class.getName();
	
	public static Map<String, Object> jqGetListQuotaProductBySupplier(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Locale locale = (Locale) context.get("locale");
		List<GenericValue> listProducts = null;
		String supplierId = null;
		if (parameters.containsKey("supplierId") && parameters.get("supplierId").length > 0) {
			supplierId = parameters.get("supplierId")[0];
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, supplierId));
		}
		String currencyUomId = null;
		if (parameters.containsKey("currencyUomId") && parameters.get("currencyUomId").length > 0) {
			currencyUomId = parameters.get("currencyUomId")[0];
			listAllConditions.add(EntityCondition.makeCondition("currencyUomId", EntityJoinOperator.EQUALS, currencyUomId));
		}
		if (UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("productCode");
		}
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate")));
		listAllConditions.add(EntityCondition.makeCondition(
				EntityCondition.makeCondition("purchaseDiscontinuationDate", EntityOperator.EQUALS, null),
				EntityOperator.OR, EntityCondition.makeCondition("purchaseDiscontinuationDate",
						EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis()))));
		try {
			listProducts = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "SupplierProductAndQuotaAndTax", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), 
					null, null, listSortFields, opts);
			
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListQuotaProductBySupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listProducts);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		//Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		EntityListIterator productVirtualInterator = null;
		try {
			listAllConditions = processProductCondition(listAllConditions, delegator);
			
			//listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.IN, com.olbius.basesales.product.ProductServices.listProductSalable(delegator)));
			listAllConditions.add(EntityCondition.makeCondition("isVariant", "N"));
			List<String> productTypeIdsEnable = SalesUtil.getPropertyProcessedMultiKey(delegator, "product.type.enable");
			listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.IN, productTypeIdsEnable));
			List<EntityCondition> condsOr = FastList.newInstance();
			condsOr.add(EntityCondition.makeCondition("numRival", null));
			condsOr.add(EntityCondition.makeCondition("numRival", "0"));
			condsOr.add(EntityCondition.makeCondition("numRival", "2"));
			listAllConditions.add(EntityCondition.makeCondition(condsOr, EntityOperator.OR));
			//List<GenericValue> listProductVirtual = delegator.findList("Product", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}
			
			Set<String> listSelectFields = FastSet.newInstance();
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
			// internalName, brandName, productWeight, weightUomId
			productVirtualInterator = delegator.find("ProductAndCategoryPrimary", EntityCondition.makeCondition(listAllConditions), null, listSelectFields, listSortFields, opts);
			
			List<GenericValue> listProductVirtual = SalesUtil.processIterator(productVirtualInterator, parameters, successResult);
			String getQuota = null;
			if (parameters.containsKey("getQuota") && parameters.get("getQuota").length > 0) {
				if (UtilValidate.isNotEmpty(parameters.get("getQuota"))){
					getQuota = parameters.get("getQuota")[0];
				}
			}
			List<Map<String, Object>> listProducts = FastList.newInstance();
			if (UtilValidate.isNotEmpty(listProductVirtual)) {
				for (GenericValue x : listProductVirtual) {
					Map<String, Object> mapProduct = x.getAllFields();
					// mapProduct.put("taxCatalogs", com.olbius.basesales.product.ProductServices.getTaxCatalogs(delegator, x.getString("productId")));
					
					List<GenericValue> listProductAssoc = ProductWorker.getChildrenAssocProduct(x.getString("productId"), delegator, null);
					if (UtilValidate.isNotEmpty(listProductAssoc)) {
						List<Map<String, Object>> listProductVariant = FastList.newInstance();
						for (GenericValue z : listProductAssoc) {
							GenericValue productVariant = EntityUtil.getFirst(delegator.findList("ProductAndCategoryPrimary", EntityCondition.makeCondition("productId", z.getString("productIdTo")), listSelectFields, null, null, false));
							if (productVariant != null) {
								Map<String, Object> mapProductVariant = FastMap.newInstance();
								mapProductVariant.put("productId", productVariant.getString("productId"));
								mapProductVariant.put("productCode", productVariant.getString("productCode"));
								mapProductVariant.put("primaryProductCategoryId", productVariant.getString("primaryProductCategoryId"));
								mapProductVariant.put("productName", productVariant.getString("productName"));
								mapProductVariant.put("quantityUomId", productVariant.getString("quantityUomId"));
								mapProductVariant.put("longDescription", productVariant.getString("longDescription"));
								mapProductVariant.put("isVirtual", productVariant.getString("isVirtual"));
								mapProductVariant.put("categoryName", productVariant.getString("categoryName"));
								//mapProductVariant.put("internalName", productVariant.getString("internalName"));
								//mapProductVariant.put("brandName", productVariant.getString("brandName"));
								//mapProductVariant.put("productWeight", productVariant.getBigDecimal("productWeight"));
								//mapProductVariant.put("weightUomId", productVariant.getString("weightUomId"));
								//mapProductVariant.put("taxCatalogs", com.olbius.basesales.product.ProductServices.getTaxCatalogs(delegator, productVariant.getString("productId")));
								//mapProductVariant.putAll(getProductFeature(delegator, locale, productVariant.get("productId"), "STANDARD_FEATURE"));
								
								listProductVariant.add(mapProductVariant);
							}
						}
						mapProduct.put("rowDetail", listProductVariant);
						mapProduct.put("numChild", listProductVariant.size());
					}
					if (UtilValidate.isNotEmpty(getQuota) && "Y".equals(getQuota)) {
						BigDecimal quantityQuota = BigDecimal.ZERO;
						List<GenericValue> listQuotas = FastList.newInstance();
						List<EntityCondition> conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("productId", x.getString("productId")));
						try {
							listQuotas = delegator.findList("QuotaItemAvailableGroupByProduct", EntityCondition.makeCondition(conds), null, null,
									null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList QuotaItemAvailableGroupByProduct: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listQuotas.isEmpty()){
							for (GenericValue item : listQuotas) {
								quantityQuota = quantityQuota.add(item.getBigDecimal("quotaQuantity"));
							}
						}
						mapProduct.put("quantityQuota", quantityQuota);
					}
					listProducts.add(mapProduct);
				}
			}
			successResult.put("listIterator", listProducts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProducts service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return successResult;
	}

	public static Map<String, Object> listDataSampleProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setLimit(10);
		opts.setMaxRows(10);
		EntityListIterator productVirtualInterator = null;
		try {
			listAllConditions = processProductCondition(listAllConditions, delegator);
			listAllConditions.add(EntityCondition.makeCondition("isVariant", "N"));
			List<String> productTypeIdsEnable = SalesUtil.getPropertyProcessedMultiKey(delegator, "product.type.enable");
			listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.IN, productTypeIdsEnable));
			List<EntityCondition> condsOr = FastList.newInstance();
			condsOr.add(EntityCondition.makeCondition("numRival", null));
			condsOr.add(EntityCondition.makeCondition("numRival", "0"));
			condsOr.add(EntityCondition.makeCondition("numRival", "2"));
			listAllConditions.add(EntityCondition.makeCondition(condsOr, EntityOperator.OR));
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}

			Set<String> listSelectFields = FastSet.newInstance();
			listSelectFields.add("productId");
			listSelectFields.add("productCode");
			listSelectFields.add("primaryProductCategoryId");
			listSelectFields.add("productName");
			listSelectFields.add("descriptionUom");
			listSelectFields.add("longDescription");
			listSelectFields.add("isVirtual");
			listSelectFields.add("productWeight");
			listSelectFields.add("weight");
			listSelectFields.add("weightUomName");
			listSelectFields.add("productDefaultPrice");
			listSelectFields.add("productListPrice");
			listSelectFields.add("currencyUomId");
			productVirtualInterator = delegator.find("ProductAndCategoryPrimaryAndPriceAndUom", EntityCondition.makeCondition(listAllConditions), null, listSelectFields, listSortFields, opts);

			List<GenericValue> listProductVirtual = SalesUtil.processIterator(productVirtualInterator, parameters, successResult);
			String getQuota = null;
			if (parameters.containsKey("getQuota") && parameters.get("getQuota").length > 0) {
				if (UtilValidate.isNotEmpty(parameters.get("getQuota"))){
					getQuota = parameters.get("getQuota")[0];
				}
			}
			List<Map<String, Object>> listProducts = FastList.newInstance();
			if (UtilValidate.isNotEmpty(listProductVirtual)) {
				for (GenericValue x : listProductVirtual) {
					Map<String, Object> mapProduct = x.getAllFields();
					List<GenericValue> listProductAssoc = ProductWorker.getChildrenAssocProduct(x.getString("productId"), delegator, null);
					if (UtilValidate.isNotEmpty(listProductAssoc)) {
						List<Map<String, Object>> listProductVariant = FastList.newInstance();
						for (GenericValue z : listProductAssoc) {
							GenericValue productVariant = EntityUtil.getFirst(delegator.findList("ProductAndCategoryPrimaryAndPriceAndUom", EntityCondition.makeCondition("productId", z.getString("productIdTo")), listSelectFields, null, null, false));
							if (productVariant != null) {
								Map<String, Object> mapProductVariant = FastMap.newInstance();
								mapProductVariant.put("productId", productVariant.getString("productId"));
								mapProductVariant.put("productCode", productVariant.getString("productCode"));
								mapProductVariant.put("primaryProductCategoryId", productVariant.getString("primaryProductCategoryId"));
								mapProductVariant.put("productName", productVariant.getString("productName"));
								mapProductVariant.put("descriptionUom", productVariant.getString("descriptionUom"));
								mapProductVariant.put("longDescription", productVariant.getString("longDescription"));
								mapProductVariant.put("isVirtual", productVariant.getString("isVirtual"));
								mapProductVariant.put("categoryName", productVariant.getString("categoryName"));
								mapProductVariant.put("productWeight", productVariant.getString("productWeight"));
								mapProductVariant.put("weight", productVariant.getString("weight"));
								mapProductVariant.put("weightUomName", productVariant.getString("weightUomName"));
								mapProductVariant.put("productDefaultPrice", productVariant.getString("productDefaultPrice"));
								mapProductVariant.put("productListPrice", productVariant.getString("productListPrice"));
								mapProductVariant.put("currencyUomId", productVariant.getString("currencyUomId"));
								listProductVariant.add(mapProductVariant);
							}
						}
						mapProduct.put("rowDetail", listProductVariant);
						mapProduct.put("numChild", listProductVariant.size());
					}
					if (UtilValidate.isNotEmpty(getQuota) && "Y".equals(getQuota)) {
						BigDecimal quantityQuota = BigDecimal.ZERO;
						List<GenericValue> listQuotas = FastList.newInstance();
						List<EntityCondition> conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("productId", x.getString("productId")));
						try {
							listQuotas = delegator.findList("QuotaItemAvailableGroupByProduct", EntityCondition.makeCondition(conds), null, null,
									null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList QuotaItemAvailableGroupByProduct: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listQuotas.isEmpty()){
							for (GenericValue item : listQuotas) {
								quantityQuota = quantityQuota.add(item.getBigDecimal("quotaQuantity"));
							}
						}
						mapProduct.put("quantityQuota", quantityQuota);
					}
					listProducts.add(mapProduct);
				}
			}
			successResult.put("listIterator", listProducts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProducts service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}

		return successResult;
	}
	
	public static List<EntityCondition> processProductCondition(List<EntityCondition> listAllConditions, Delegator delegator) throws GenericEntityException{
    	List<EntityCondition> listAllConditionsResult = new ArrayList<EntityCondition>();
		for (EntityCondition condition : listAllConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				if (UtilValidate.isEmpty(conditionSplit)) listAllConditionsResult.add(condition);
				
				String fieldName = conditionSplit.length > 0 ? (String) conditionSplit[0] : null;
				String operator = conditionSplit.length > 1 ? (String) conditionSplit[1] : null;
				String value = conditionSplit.length > 2 ? (String) conditionSplit[2].trim() : null;
				String valueFrom = null;
				String valueTo = null;
				if (conditionSplit.length > 4) {
					if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
						if ("AND".equals(conditionSplit[4].trim())) {
							operator = "RANGE";
							valueFrom = (String) conditionSplit[2].trim();
							valueTo = (String) conditionSplit[7].trim();
							valueFrom = EntityMiscUtil.cleanValue(valueFrom);
							valueTo = EntityMiscUtil.cleanValue(valueTo);
						}
					}
				}
				fieldName = EntityMiscUtil.cleanFieldName(fieldName);
				value = EntityMiscUtil.cleanValue(value);
				
				if ("productCode".equals(fieldName)) {
					if ("LIKE".equals(operator)) {
						listAllConditionsResult.add(EntityCondition.makeCondition("productId", EntityJoinOperator.IN, ProductUtils.getByProductCodeOfProductAndChild(delegator, value)));
					}
				} else if ("productState".equals(fieldName)) {
					if ("LIKE".equals(operator) || "=".equals(operator)) {
						Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
						if ("DISCONTINUE_SALES".equals(value)) {
							listAllConditionsResult.add(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.LESS_THAN, nowTimestamp));
						} else if ("DISCONTINUE_PURCHASE".equals(value)) {
							listAllConditionsResult.add(EntityCondition.makeCondition("purchaseDiscontinuationDate", EntityOperator.LESS_THAN, nowTimestamp));
						} else if ("NORMAL".equals(value)) {
							List<EntityCondition> tempConds = FastList.newInstance();
							tempConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, nowTimestamp)));
							tempConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("purchaseDiscontinuationDate", null), EntityOperator.OR, EntityCondition.makeCondition("purchaseDiscontinuationDate", EntityOperator.GREATER_THAN, nowTimestamp)));
							listAllConditionsResult.add(EntityCondition.makeCondition(tempConds));
						}
					}
				} else {
					listAllConditionsResult.add(condition);
				}
			}
		}
    	return listAllConditionsResult;
	}
	
	/*private static Map<String, Object> getProductFeature(Delegator delegator, Locale locale, Object productId, Object productFeatureApplTypeId) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		List<EntityCondition> listEntityConditions = FastList.newInstance();
		listEntityConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", productFeatureApplTypeId)));
		listEntityConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		List<GenericValue> listProductFeatureAppl = delegator.findList("ProductFeatureAppl",
				EntityCondition.makeCondition(listEntityConditions), null, null, null, false);
		for (GenericValue c : listProductFeatureAppl) {
			GenericValue productFeature = delegator.findOne("ProductFeature", UtilMisc.toMap("productFeatureId", c.get("productFeatureId")), false);
			String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
			result.put(productFeatureTypeId, productFeature.get("description", locale));
		}
		return result;
	}*/
	
	public static Map<String, Object> getProductFeature(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			Object productId = context.get("productId");
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(productId)) {
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE")));
				List<GenericValue> productFeatureAppls = delegator.findList("ProductFeatureAppl",
						EntityCondition.makeCondition(conditions), UtilMisc.toSet("productFeatureId"), null, null, false);
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("productFeatureId", EntityJoinOperator.IN, EntityUtil.getFieldListFromEntityList(productFeatureAppls, "productFeatureId", true)));
			}
			conditions.add(EntityCondition.makeCondition("productFeatureTypeId", EntityJoinOperator.EQUALS, context.get("productFeatureTypeId")));
			List<GenericValue> productFeatures = delegator.findList("ProductFeature",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : productFeatures) {
				x.set("description", x.get("description", locale));
			}
			result.put("productFeatures", productFeatures);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> createOrUpdateProductAddImages(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String productId = (String) context.get("productId");
		
		// update ADDITIONAL_IMAGE_1 of product
		createOrUpdateProductAddImage((String) context.get("ADDITIONAL_IMAGE_1Id"), (String) context.get("ADDITIONAL_IMAGE_1"), productId, "ADDITIONAL_IMAGE_1", "XTRA_IMG_1_LARGE", delegator, dispatcher, userLogin);
		
		// update ADDITIONAL_IMAGE_2 of product
		createOrUpdateProductAddImage((String) context.get("ADDITIONAL_IMAGE_2Id"), (String) context.get("ADDITIONAL_IMAGE_2"), productId, "ADDITIONAL_IMAGE_2", "XTRA_IMG_2_LARGE", delegator, dispatcher, userLogin);
		
		// update ADDITIONAL_IMAGE_3 of product
		createOrUpdateProductAddImage((String) context.get("ADDITIONAL_IMAGE_3Id"), (String) context.get("ADDITIONAL_IMAGE_3"), productId, "ADDITIONAL_IMAGE_3", "XTRA_IMG_3_LARGE", delegator, dispatcher, userLogin);
		
		// update ADDITIONAL_IMAGE_4 of product
		createOrUpdateProductAddImage((String) context.get("ADDITIONAL_IMAGE_4Id"), (String) context.get("ADDITIONAL_IMAGE_4"), productId, "ADDITIONAL_IMAGE_4", "XTRA_IMG_4_LARGE", delegator, dispatcher, userLogin);
		
		return successResult;
	}
	
	private static void createOrUpdateProductAddImage(String dataResourceId, String imageUrl, String productId, String productContentTypeId, String productContentTypeId2, 
			Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		
		if (UtilValidate.isNotEmpty(dataResourceId)) {
			// update data source of product image
			dispatcher.runSync("updateDataResource", UtilMisc.toMap("dataResourceId", dataResourceId, "objectInfo", imageUrl, "userLogin", userLogin));
		} else {
			// create new data source contain product image
			createOrThruProductAdditionalImage(imageUrl, productId, productContentTypeId, productContentTypeId2, delegator, dispatcher, userLogin);
		}
	}
	
	private static void createOrThruProductAdditionalImage(String imageUrl, String productId, String productContentTypeId, String productContentTypeId2, 
			Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(imageUrl)) {
			// thru old image content and create new content with "productContentTypeId"
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productContentTypeId", productContentTypeId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> prodContentExists = delegator.findList("ProductContent", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(prodContentExists)) {
				for (GenericValue prodCntItem : prodContentExists) {
					prodCntItem.put("thruDate", nowTimestamp);
				}
				delegator.storeAll(prodContentExists);
			}
			
			// thru old image content and create new content with "productContentTypeId2"
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productContentTypeId", productContentTypeId2));
			conds.add(EntityUtil.getFilterByDateExpr());
			prodContentExists = delegator.findList("ProductContent", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(prodContentExists)) {
				for (GenericValue prodCntItem : prodContentExists) {
					prodCntItem.put("thruDate", nowTimestamp);
				}
				delegator.storeAll(prodContentExists);
			}
		} else {
			// create new content image
			Map<String, Object> createDataResourceResult = dispatcher.runSync("createDataResource", UtilMisc.toMap("localeString", "vi", 
							"dataResourceTypeId", "LINK", "objectInfo", imageUrl, "dataTemplateTypeId", "NONE", "statusId", "CTNT_PUBLISHED", 
							"dataResourceName", "Product image", "mimeTypeId", "text/xml", "isPublic", "Y", "userLogin", userLogin));
			if (ServiceUtil.isError(createDataResourceResult)) {
				Debug.logError(ServiceUtil.getErrorMessage(createDataResourceResult), module);
				return;
			}
			
			String dataResourceId = (String) createDataResourceResult.get("dataResourceId");
			String contentId = "PRIM" + delegator.getNextSeqId("Content");
			
			ProductContentUtils.createContentWithWebSite(dispatcher, delegator, userLogin, UtilMisc.toMap("contentId", contentId, "contentTypeId", "DOCUMENT", 
							"dataResourceId", dataResourceId, "statusId", "CTNT_PUBLISHED", "createdDate", UtilDateTime.nowTimestamp()));
			
			// thru old image content and create new content with "productContentTypeId"
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productContentTypeId", productContentTypeId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> prodContentExists = delegator.findList("ProductContent", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(prodContentExists)) {
				for (GenericValue prodCntItem : prodContentExists) {
					prodCntItem.put("thruDate", nowTimestamp);
				}
				delegator.storeAll(prodContentExists);
			}
			GenericValue productContent = delegator.makeValidValue("ProductContent", UtilMisc.toMap("productId", productId, 
							"contentId", contentId, "productContentTypeId", productContentTypeId, "fromDate", UtilDateTime.nowTimestamp()));
			delegator.create(productContent);
			
			// thru old image content and create new content with "productContentTypeId2"
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productContentTypeId", productContentTypeId2));
			conds.add(EntityUtil.getFilterByDateExpr());
			prodContentExists = delegator.findList("ProductContent", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(prodContentExists)) {
				for (GenericValue prodCntItem : prodContentExists) {
					prodCntItem.put("thruDate", nowTimestamp);
				}
				delegator.storeAll(prodContentExists);
			}
			GenericValue productContent2 = delegator.makeValidValue("ProductContent", UtilMisc.toMap("productId", productId, 
							"contentId", contentId, "productContentTypeId", productContentTypeId2, "fromDate", UtilDateTime.nowTimestamp()));
			delegator.create(productContent2);
		}
	}

	public static Map<String, Object> createOrUpdateProductImages(Delegator delegator, LocalDispatcher dispatcher, Map<String, ? extends Object> context, GenericValue userLogin, String productId) throws GenericEntityException, GenericServiceException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		if (product == null) {
			return successResult;
		}
		
		// create images product, store file into JCR
		String largeImageUrl = (String) context.get("largeImageUrl");
		String smallImageUrl = (String) context.get("smallImageUrl");
		String additionalImage1Url = (String) context.get("additionalImage1Url");
		String additionalImage2Url = (String) context.get("additionalImage2Url");
		String additionalImage3Url = (String) context.get("additionalImage3Url");
		String additionalImage4Url = (String) context.get("additionalImage4Url");
		try {
			Map<String, Object> contentCtx = new HashMap<String, Object>();
	        Map<String, Object> fileResult = new HashMap<String, Object>();
	        
			// large image
	        if (UtilValidate.isEmpty(largeImageUrl)) {
	        	ByteBuffer largeImage = (ByteBuffer) context.get("largeImage");
				String _largeImage_fileName = (String) context.get("_largeImage_fileName");
				String _largeImage_contentType = (String) context.get("_largeImage_contentType");
		        if (largeImage != null) {
		        	contentCtx.put("userLogin", userLogin);
			        contentCtx.put("uploadedFile", largeImage);
			        contentCtx.put("_uploadedFile_fileName", _largeImage_fileName);
			        contentCtx.put("_uploadedFile_contentType", _largeImage_contentType);
			        contentCtx.put("public", "Y");
			        contentCtx.put("folder", "/product");
			        fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
		            if (ServiceUtil.isError(fileResult)) {
		            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
		            }
		            largeImageUrl = (String) fileResult.get("path");
		        }
	        }
            
            // small image
	        if (UtilValidate.isEmpty(smallImageUrl)) {
	        	ByteBuffer smallImage = (ByteBuffer) context.get("smallImage");
	            String _smallImage_fileName = (String) context.get("_smallImage_fileName");
	            String _smallImage_contentType = (String) context.get("_smallImage_contentType");
	            if (smallImage != null) {
	            	fileResult.clear();
	            	contentCtx.clear();
	            	contentCtx.put("userLogin", userLogin);
	            	contentCtx.put("uploadedFile", smallImage);
	            	contentCtx.put("_uploadedFile_fileName", _smallImage_fileName);
	            	contentCtx.put("_uploadedFile_contentType", _smallImage_contentType);
	            	contentCtx.put("public", "Y");
	            	contentCtx.put("folder", "/product");
	            	fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
		            if (ServiceUtil.isError(fileResult)) {
		            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
		            }
		            smallImageUrl = (String) fileResult.get("path");
	            }
	        }
            
            // additional image 1
	        if (UtilValidate.isEmpty(additionalImage1Url)) {
	        	ByteBuffer additionalImage1 = (ByteBuffer) context.get("additionalImage1");
	            String _additionalImage1_fileName = (String) context.get("_additionalImage1_fileName");
	            String _additionalImage1_contentType = (String) context.get("_additionalImage1_contentType");
	            if (additionalImage1 != null) {
	            	fileResult.clear();
	            	contentCtx.clear();
	            	contentCtx.put("userLogin", userLogin);
	            	contentCtx.put("uploadedFile", additionalImage1);
	            	contentCtx.put("_uploadedFile_fileName", _additionalImage1_fileName);
	            	contentCtx.put("_uploadedFile_contentType", _additionalImage1_contentType);
	            	contentCtx.put("public", "Y");
	            	contentCtx.put("folder", "/product");
	            	fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
	            	if (ServiceUtil.isError(fileResult)) {
	            		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
	            	}
	            	additionalImage1Url = (String) fileResult.get("path");
	            }
	        }
            
            // additional image 2
	        if (UtilValidate.isEmpty(additionalImage2Url)) {
	        	ByteBuffer additionalImage2 = (ByteBuffer) context.get("additionalImage2");
	            String _additionalImage2_fileName = (String) context.get("_additionalImage2_fileName");
	            String _additionalImage2_contentType = (String) context.get("_additionalImage2_contentType");
	            if (additionalImage2 != null) {
	            	fileResult.clear();
	            	contentCtx.clear();
	            	contentCtx.put("userLogin", userLogin);
	            	contentCtx.put("uploadedFile", additionalImage2);
	            	contentCtx.put("_uploadedFile_fileName", _additionalImage2_fileName);
	            	contentCtx.put("_uploadedFile_contentType", _additionalImage2_contentType);
	            	contentCtx.put("public", "Y");
	            	contentCtx.put("folder", "/product");
	            	fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
	            	if (ServiceUtil.isError(fileResult)) {
	            		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
	            	}
	            	additionalImage2Url = (String) fileResult.get("path");
	            }
	        }
            
            // additional image 3
	        if (UtilValidate.isEmpty(additionalImage3Url)) {
	        	ByteBuffer additionalImage3 = (ByteBuffer) context.get("additionalImage3");
	            String _additionalImage3_fileName = (String) context.get("_additionalImage3_fileName");
	            String _additionalImage3_contentType = (String) context.get("_additionalImage3_contentType");
	            if (additionalImage3 != null) {
	            	fileResult.clear();
	            	contentCtx.clear();
	            	contentCtx.put("userLogin", userLogin);
	            	contentCtx.put("uploadedFile", additionalImage3);
	            	contentCtx.put("_uploadedFile_fileName", _additionalImage3_fileName);
	            	contentCtx.put("_uploadedFile_contentType", _additionalImage3_contentType);
	            	contentCtx.put("public", "Y");
	            	contentCtx.put("folder", "/product");
	            	fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
	            	if (ServiceUtil.isError(fileResult)) {
	            		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
	            	}
	            	additionalImage3Url = (String) fileResult.get("path");
	            }
	        }
            
            // additional image 4
	        if (UtilValidate.isEmpty(additionalImage4Url)) {
	        	ByteBuffer additionalImage4 = (ByteBuffer) context.get("additionalImage4");
	            String _additionalImage4_fileName = (String) context.get("_additionalImage4_fileName");
	            String _additionalImage4_contentType = (String) context.get("_additionalImage4_contentType");
	            if (additionalImage4 != null) {
	            	fileResult.clear();
	            	contentCtx.clear();
	            	contentCtx.put("userLogin", userLogin);
	            	contentCtx.put("uploadedFile", additionalImage4);
	            	contentCtx.put("_uploadedFile_fileName", _additionalImage4_fileName);
	            	contentCtx.put("_uploadedFile_contentType", _additionalImage4_contentType);
	            	contentCtx.put("public", "Y");
	            	contentCtx.put("folder", "/product");
	            	fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
	            	if (ServiceUtil.isError(fileResult)) {
	            		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
	            	}
	            	additionalImage4Url = (String) fileResult.get("path");
	            }
	        }
        } catch (GenericServiceException e) {
            Debug.logError("Upload images product error", module);
            Debug.logError(e, module);
        }
		
		// store all image paths into product info
		if (((largeImageUrl == null && product.get("largeImageUrl") != null) || (largeImageUrl != null && !largeImageUrl.equals(product.get("largeImageUrl"))))
				|| (smallImageUrl == null && product.get("smallImageUrl") != null) || (smallImageUrl != null && !smallImageUrl.equals(product.get("smallImageUrl")))) {
			product.set("largeImageUrl", largeImageUrl);
			product.set("smallImageUrl", smallImageUrl);
			delegator.store(product);
		}
		
		Map<String, Object> contentCtx2 = new HashMap<String, Object>();
		contentCtx2.put("productId", productId);
		contentCtx2.put("ADDITIONAL_IMAGE_1", additionalImage1Url);
		contentCtx2.put("ADDITIONAL_IMAGE_2", additionalImage2Url);
		contentCtx2.put("ADDITIONAL_IMAGE_3", additionalImage3Url);
		contentCtx2.put("ADDITIONAL_IMAGE_4", additionalImage4Url);
		contentCtx2.put("userLogin", userLogin);
		Map<String, Object> fileResult = dispatcher.runSync("createOrUpdateProductAddImages", contentCtx2);
    	if (ServiceUtil.isError(fileResult)) {
    		return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
    	}
		
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> createProductAdvance(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODUCTPO_NEW");
		if (!hasPermission){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}
		
		String productId = (String) context.get("productId");
		try {
			String productCode = (String) context.get("productCode");
			if (UtilValidate.isNotEmpty(productCode)) {
				// check productCode is available or no
				try {
					UniqueUtil.checkProductCode(delegator, context.get("productCode"), context.get("productId"));
				} catch (Exception e) {
					Debug.logWarning("Product id is exists", module);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductIdIsAlreadyExists", locale));
				}
			}
			List<EntityCondition> conds = new ArrayList<EntityCondition>();
			// check barcode
			String barcode = (String) context.get("barcode");
			if (UtilValidate.isNotEmpty(barcode)) {
				List<String> barcodes = ProductUtils.splitBarcodeArrayString(barcode);
				// check barcode's existence
				conds.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
				conds.add(EntityCondition.makeCondition("idValue", EntityOperator.IN, barcodes));
				List<GenericValue> barcodeExisted = delegator.findList("GoodIdentification", EntityCondition.makeCondition(conds), null, null, null, false);
				if (UtilValidate.isNotEmpty(barcodeExisted)) {
					Debug.logWarning("Barcode " + barcode + " is exists", module);
					List<String> barcodeIdExisted = EntityUtil.getFieldListFromEntityList(barcodeExisted, "idValue", true);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSBarcodeHasBeenUsedParam", UtilMisc.toMap("barcode", barcodeIdExisted.toString()), locale));
				}
			}
			// check PLU code
			String idPLUCode = (String) context.get("idPLUCode");
			if (UtilValidate.isNotEmpty(idPLUCode)) {
				int idPLUCodeLength = idPLUCode.length();
				if (idPLUCodeLength < 5) {
					idPLUCode = formatPaddedString(idPLUCode, 5);
				} else if (idPLUCodeLength > 5) {
					idPLUCode = idPLUCode.substring(idPLUCodeLength - 5, idPLUCodeLength);
				}
				// check barcode's existence
				List<GenericValue> barcodeExisted = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("idValue", idPLUCode), null, false);
				if (UtilValidate.isNotEmpty(barcodeExisted)) {
					Debug.logWarning("PLU code " + idPLUCode + " is exists", module);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSPLUCodeHasBeenUsedParam", UtilMisc.toMap("idPLUCode", idPLUCode), locale));
				}
			}
			
			String productTypeId = (String) context.get("productTypeId");
			String isVirtual = "N";
			String isVariant = "N";
			String productConfigItem = null;
			if ("AGGREGATED".equals(productTypeId)) {
				productConfigItem = (String) context.get("productConfigItem");
			} else {
				isVirtual = (String) context.get("isVirtual");
				isVariant = (String) context.get("isVariant");
				if (UtilValidate.isEmpty(isVirtual)) isVirtual = "N";
				if (UtilValidate.isEmpty(isVariant)) isVariant = "N";
				if ("Y".equals(isVirtual) && "Y".equals(isVariant)) {
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductCanNotBothVirtualAndVariant", locale));
				}
			}
			
			if (UtilValidate.isEmpty(productId)) {
				productId = "OLB" + delegator.getNextSeqId("Product");
			}
			
			Timestamp salesDiscontinuationDate = null;
			Timestamp purchaseDiscontinuationDate = null;
			try {
				Long salesDiscontinuationDateStr = (Long) context.get("salesDiscontinuationDate");
				Long purchaseDiscontinuationDateStr = (Long) context.get("purchaseDiscontinuationDate");
				if (UtilValidate.isNotEmpty(salesDiscontinuationDateStr)) {
					context.remove("salesDiscontinuationDate");
					salesDiscontinuationDate = new Timestamp(salesDiscontinuationDateStr);
				}
				if (UtilValidate.isNotEmpty(purchaseDiscontinuationDateStr)) {
					context.remove("purchaseDiscontinuationDate");
					purchaseDiscontinuationDate = new Timestamp(purchaseDiscontinuationDateStr);
				}
			} catch (Exception e) {
	        	Debug.logWarning("Error: format sales discountinuation date and purchase discountinuation date", module);
	        }
			
			String quantityUomId = (String) context.get("quantityUomId");
			String salesUomId = (String) context.get("salesUomId");
			String purchaseUomId = (String) context.get("purchaseUomId");
			if (UtilValidate.isEmpty(salesUomId)) salesUomId = quantityUomId;
			if (UtilValidate.isEmpty(purchaseUomId)) purchaseUomId = quantityUomId;
			
			Map<String, Object> productCtx = CrabEntity.fastMaking(delegator, "Product", context);
			productCtx.put("productId", productId);
			productCtx.put("isVirtual", isVirtual);
			productCtx.put("isVariant", isVariant);
			productCtx.put("salesUomId", salesUomId);
			productCtx.put("purchaseUomId", purchaseUomId);

			String weightStr = (String) context.get("weight");
			String productWeightStr = (String) context.get("productWeight");
			BigDecimal weight = null;
			BigDecimal productWeight = null;
			if (UtilValidate.isNotEmpty(weightStr)) weight = new BigDecimal(weightStr);
			if (UtilValidate.isNotEmpty(productWeightStr)) productWeight = new BigDecimal(productWeightStr);
			productCtx.put("weight", weight);
			productCtx.put("productWeight", productWeight);
			
			if (UtilValidate.isEmpty(productCode)) productCode = productId;
			productCtx.put("productCode", productCode);
			
			// put info images
			if (UtilValidate.isNotEmpty(context.get("largeImageUrl"))) {
				productCtx.put("largeImageUrl", context.get("largeImageUrl"));
			}
			if (UtilValidate.isNotEmpty(context.get("smallImageUrl"))) {
				productCtx.put("smallImageUrl", context.get("smallImageUrl"));
			}
			String internalName = (String) context.get("internalName");
			if (UtilValidate.isEmpty(internalName)) {
				internalName = "";
			}
			productCtx.put("internalName", internalName);
			
			// sales and purchase discountinuation date
			productCtx.put("salesDiscontinuationDate", salesDiscontinuationDate);
			productCtx.put("purchaseDiscontinuationDate", purchaseDiscontinuationDate);
			
			String requireAmount = null;
			String useOnlyManualPrice = null;
			if (UtilValidate.isNotEmpty(productCtx.get("amountUomTypeId"))) {
				requireAmount = "Y";
				if ("AGGREGATED".equals(productTypeId)) {
					useOnlyManualPrice = "Y";
				}
			}
			productCtx.put("requireAmount", requireAmount);
			productCtx.put("useOnlyManualPrice", useOnlyManualPrice);
			
			// create product
			Map<String, Object> createProdResult = dispatcher.runSync("createProduct", productCtx);
			if (ServiceUtil.isError(createProdResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createProdResult));
			}
			
			// create barcode
			if (UtilValidate.isNotEmpty(barcode)) {
				/*Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId,
						"goodIdentificationTypeId", "SKU", 
						"uomId", quantityUomId, "idValue", barcode,
						"userLogin", userLogin);
				Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
				}*/
				Boolean isSyncThread = "Y".equals((String) context.get("isSyncThread")) ? true : false;
				Map<String, Object> createGoodIdenResult = ProductUtils.createOrStoreBarcodeProduct(delegator, dispatcher, locale, userLogin, productId, quantityUomId, barcode, isSyncThread);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
				}
			}
			
			// create PLU code
			if (UtilValidate.isNotEmpty(idPLUCode)) {
				/*Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId,
						"goodIdentificationTypeId", "PLU", 
						"uomId", quantityUomId, "idValue", idPLUCode,
						"userLogin", userLogin);
				Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
				}*/
				Map<String, Object> createGoodIdenResult = ProductUtils.createOrStorePLUCodeProduct(delegator, dispatcher, locale, userLogin, productId, quantityUomId, idPLUCode);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
				}
			}
			
			// add feature type in product attributes, store feature types of virtual product
			List<String> featureTypeIds = (List<String>) context.get("featureTypeIds");
			if (UtilValidate.isNotEmpty(featureTypeIds)) {
				String featureTypeIdsStr = StringUtils.join(featureTypeIds, ";");
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "featureTypes");
				productCtx.put("attrValue", featureTypeIdsStr);
				productCtx.put("userLogin", userLogin);
				dispatcher.runSync("createProductAttribute", productCtx);
			}
			
			// add product to category
			Set<String> productCategoryIds = FastSet.newInstance();
			// check primary category
			String primaryProductCategoryId = (String) context.get("primaryProductCategoryId");
			if (UtilValidate.isNotEmpty(primaryProductCategoryId)) {
				productCategoryIds.add(primaryProductCategoryId);
			}
			// check list other category
			List<String> otherProductCategoryIds = (List<String>) context.get("productCategoryIds");
			if (UtilValidate.isNotEmpty(otherProductCategoryIds)) {
				productCategoryIds.addAll(otherProductCategoryIds);
			}
			if (productCategoryIds.size() > 0) {
				int sequenceNum = 1;
				for (String categoryId : productCategoryIds) {
					productCtx.clear();
					productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
					productCtx.put("productId", productId);
					productCtx.put("productCategoryId", categoryId);
					productCtx.put("sequenceNum", sequenceNum);
					productCtx.put("userLogin", userLogin);
					Map<String, Object> addToCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
					if (ServiceUtil.isError(addToCategoryResult)) {
						Debug.logError("Error occur when add product id = " + productId + " to product category = " + categoryId, module);
						continue;
					}
					sequenceNum++;
				}
				/* (extend) Check for update product
					List<EntityCondition> conds = new ArrayList<EntityCondition>();
					conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
					conds.add(EntityCondition.makeCondition("productId", productId));
					conds.add(EntityUtil.getFilterByDateExpr());
					List<String> listProductCategoryIdExists = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conds), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
					
					int sequenceNum = 1;
					for (String categoryId : productCategoryIds) {
						if (!listProductCategoryIdExists.contains(categoryId)) {
							productCtx.clear();
							productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
							productCtx.put("productId", productId);
							productCtx.put("productCategoryId", categoryId);
							productCtx.put("sequenceNum", sequenceNum);
							productCtx.put("userLogin", userLogin);
							Map<String, Object> addToCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
							if (ServiceUtil.isError(addToCategoryResult)) {
								Debug.logError("Error occur when add product id = " + productId + " to product category = " + categoryId, module);
								continue;
							}
							sequenceNum++;
						}
					}
				 */
			}
			
			// add product to TAX category
			String taxProductCategoryId = (String) context.get("taxProductCategoryId");
			if (UtilValidate.isNotEmpty(taxProductCategoryId)) {
				productCtx.clear();
				productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
				productCtx.put("productId", productId);
				productCtx.put("productCategoryId", taxProductCategoryId);
				productCtx.put("sequenceNum", 1);
				productCtx.put("userLogin", userLogin);
				Map<String, Object> addToTaxCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
				if (ServiceUtil.isError(addToTaxCategoryResult)) {
					Debug.logError("Error occur when add product id = " + productId + " to product category = " + taxProductCategoryId, module);
				}
				/* (extend) Check for update product
					List<GenericValue> listProductCategoryMemberTax = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", context.get("taxCatalogs"), "productId", productId)),
						null, null, null, false);
					if (UtilValidate.isEmpty(listProductCategoryMemberTax)) {
					}
				*/
			}
			
			// add features to product
			if (!isVirtual.equals(isVariant)) {
				String productFeatureApplTypeId = "STANDARD_FEATURE";
				if (isVirtual.equals("Y")) {
					productFeatureApplTypeId = "SELECTABLE_FEATURE";
					
					if (UtilValidate.isNotEmpty(context.get("displayColor"))) {
						// create product attribute 'display color'
						String attrName = EntityUtilProperties.getPropertyValue("po.properties", "productAttrName.displayColor", delegator);
						if (UtilValidate.isNotEmpty(attrName)) {
							productCtx.clear();
							productCtx.put("productId", productId);
							productCtx.put("attrName", attrName);
							productCtx.put("attrValue", context.get("displayColor"));
							productCtx.put("userLogin", userLogin);
							dispatcher.runSync("createProductAttribute", productCtx);
						} else {
							Debug.logError("Not found attrName of attribute 'displayColor'", module);
						}
					}
				}
				String parentProductId = (String) context.get("parentProductId");
				if (isVariant.equals("Y")) {
					if (UtilValidate.isNotEmpty(parentProductId)) {
						productCtx.clear();
						productCtx.put("productIdTo", productId);
						productCtx.put("productId", parentProductId);
						productCtx.put("productAssocTypeId", "PRODUCT_VARIANT");
						productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
						productCtx.put("userLogin", userLogin);
						dispatcher.runSync("createProductAssoc", productCtx);
					} else {
						throw new Exception();
					}
				}
				
				/*String feature = (String) context.get("feature");
				if (UtilValidate.isNotEmpty(feature)) {
					try {
						applyFeatureToProduct(delegator, dispatcher, feature, productId, productFeatureApplTypeId, userLogin);
					} catch (Exception e) {
						Debug.logError("Error occur when add feature = " + feature + " to product = " + productId, module);
					}
				}*/
				List<String> featureIds = (List<String>) context.get("featureIds");
				if (UtilValidate.isNotEmpty(featureIds)) {
					for (String featureId : featureIds) {
						productCtx.clear();
						productCtx.put("productId", productId);
						productCtx.put("productFeatureId", featureId);
						productCtx.put("productFeatureApplTypeId", productFeatureApplTypeId);
						productCtx.put("userLogin", userLogin);
						dispatcher.runSync("applyFeatureToProduct", productCtx);
						
						if (UtilValidate.isNotEmpty(parentProductId)) {
							// add feature to parent product
							List<EntityCondition> condsPF = new ArrayList<EntityCondition>();
							condsPF.add(EntityCondition.makeCondition("productId", parentProductId));
							condsPF.add(EntityCondition.makeCondition("productFeatureId", featureId));
							condsPF.add(EntityUtil.getFilterByDateExpr());
							List<GenericValue> parentProductFeature = delegator.findList("ProductFeatureAppl", EntityCondition.makeCondition(condsPF), null, null, null, false);
							if (UtilValidate.isEmpty(parentProductFeature)) {
								productCtx.clear();
								productCtx.put("productId", parentProductId);
								productCtx.put("productFeatureId", featureId);
								productCtx.put("productFeatureApplTypeId", "SELECTABLE_FEATURE");
								productCtx.put("userLogin", userLogin);
								dispatcher.runSync("applyFeatureToProduct", productCtx);
							}
						}
					}
				}
			}
			
			String taxInPrice = (String) context.get("taxInPrice");
			if (UtilValidate.isEmpty(taxInPrice)) taxInPrice = "N";
			
			String currencyUomId = (String) context.get("currencyUomId");
			boolean isPriceIncludedVat = "Y".equals((String) context.get("isPriceIncludedVat")) ? true : false;
			BigDecimal taxPercentage = null;
			if (isPriceIncludedVat) {
				List<EntityCondition> condsTax = FastList.newInstance();
				condsTax.add(EntityCondition.makeCondition("productId", productId));
				condsTax.add(EntityUtil.getFilterByDateExpr());
				GenericValue taxCategoryGV = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRateSimple", EntityCondition.makeCondition(condsTax), UtilMisc.toSet("taxPercentage"), null, null, false));
				if (taxCategoryGV != null) taxPercentage = taxCategoryGV.getBigDecimal("taxPercentage");
			}
			
			// create product price with type is "DEFAULT_PRICE"
			//BigDecimal productDefaultPrice = (BigDecimal) context.get("productDefaultPrice");
			String productDefaultPriceStr = (String) context.get("productDefaultPrice");
			BigDecimal productDefaultPrice = null;
			if (UtilValidate.isNotEmpty(productDefaultPriceStr)) productDefaultPrice = new BigDecimal(productDefaultPriceStr);
			BigDecimal taxAmountDefaultPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountDefaultPrice"))) taxAmountDefaultPrice = new BigDecimal((String) context.get("taxAmountDefaultPrice"));
			
			// create product price with type is "LIST_PRICE"
			//BigDecimal productListPrice = (BigDecimal) context.get("productListPrice");
			String productListPriceStr = (String) context.get("productListPrice");
			BigDecimal productListPrice = null;
			if (UtilValidate.isNotEmpty(productListPriceStr)) productListPrice = new BigDecimal(productListPriceStr);
			BigDecimal taxAmountListPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountListPrice"))) taxAmountListPrice = new BigDecimal((String) context.get("taxAmountListPrice"));
			
			// create product price
			Map<String, Object> resultProductPrice = ProductUtils.createOrStoreProductPrice(productId, currencyUomId, quantityUomId, taxInPrice, productDefaultPrice, productListPrice, taxAmountDefaultPrice, taxAmountListPrice, isPriceIncludedVat, taxPercentage, delegator, dispatcher, userLogin);
			if (ServiceUtil.isError(resultProductPrice)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultProductPrice));
			}
			
			// create dayN
			if (UtilValidate.isNotEmpty(context.get("dayN"))) {
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "DAYN");
				productCtx.put("attrValue", context.get("dayN"));
				productCtx.put("userLogin", userLogin);
				dispatcher.runSync("createProductAttribute", productCtx);
			}
			
			// create shelf life of product
			if (UtilValidate.isNotEmpty(context.get("shelflife"))) {
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "SHELFLIFE");
				productCtx.put("attrValue", context.get("shelflife"));
				productCtx.put("userLogin", userLogin);
				dispatcher.runSync("createProductAttribute", productCtx);
			}
			
			// insert into price product for supplier
			if (UtilValidate.isNotEmpty(context.get("supplierProduct"))) {
				String supplierProduct = (String) context.get("supplierProduct");
				String checkSupplierReferenceStr = (String) context.get("checkSupplierReference");
				Boolean checkSupplierReference = "Y".equals(checkSupplierReferenceStr) ? true : false;
				Map<String, Object> resultSupplierProd = ProductUtils.createOrStoreSupplierProduct(delegator, dispatcher, userLogin, productId, supplierProduct, quantityUomId, checkSupplierReference, locale);
				if (ServiceUtil.isError(resultSupplierProd)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultSupplierProd));
				}
			}
			
			// insert alternative UOM in product config packing
			if (UtilValidate.isNotEmpty(context.get("alterUomData"))) {
				String alterUomData = (String) context.get("alterUomData");
				Map<String, Object> resultAlterUom = ProductUtils.createOrStoreProductAlterUom(delegator, dispatcher, userLogin, locale, productId, quantityUomId, alterUomData, currencyUomId, isPriceIncludedVat, taxPercentage);
				if (ServiceUtil.isError(resultAlterUom)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultAlterUom));
				}
			}
			
			// insert product configuration item
			if (UtilValidate.isNotEmpty(productConfigItem)) {
				Map<String, Object> resultProdConfigItem = ProductUtils.createOrStoreProductConfigItem(delegator, dispatcher, userLogin, locale, productId, productConfigItem);
				if (ServiceUtil.isError(resultProdConfigItem)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultProdConfigItem));
				}
			}
			
			try {
				Map<String, Object> createImageResult = createOrUpdateProductImages(delegator, dispatcher, context, userLogin, productId);
				if (ServiceUtil.isError(createImageResult)) {
					Debug.logError(ServiceUtil.getErrorMessage(createImageResult), module);
				}
			} catch (Exception e) {
				Debug.logError(e, module);
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		result.put("productId", productId);
		return result;
	}
	
	public static Long getNextSequeceCategoryMember(Delegator delegator, String categoryId) throws GenericEntityException {
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("productCategoryId", categoryId));
		conditions.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.NOT_EQUAL, null));
		GenericValue sequenceNumberGV = EntityUtil.getFirst(delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-sequenceNum"), null, false));
		Long sequenceNumber = new Long(1);
		if (sequenceNumberGV != null) {
			sequenceNumber = sequenceNumberGV.getLong("sequenceNum");
		}
		return ++sequenceNumber;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> updateProductAdvance(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODUCTPO_EDIT");
		if (!hasPermission){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}
		String productId = (String) context.get("productId");
		
		try {
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (product == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSRecordHasIdIsNotFound", locale));
			}
			
			// check productCode
			try {
				UniqueUtil.checkProductCode(delegator, context.get("productCode"), context.get("productId"));
			} catch (Exception e) {
				Debug.logError("Product id is exists", module);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductIdIsAlreadyExists", locale));
			}
			
			// backup old product category id list
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
			conds.add(EntityUtil.getFilterByDateExpr());
//			List<String> oldProductCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
			//result.put("oldProductCategoryIds", oldProductCategoryIds);
			
			//String quantityUomId = (String) context.get("quantityUomId");
			//if (UtilValidate.isEmpty(quantityUomId)) {
			String quantityUomId = product.getString("quantityUomId");
			//}
			
			// check barcode
			String barcode = (String) context.get("barcode");
			Boolean isSyncThread = "Y".equals((String) context.get("isSyncThread")) ? true : false;
			Map<String, Object> resultBarcode = ProductUtils.createOrStoreBarcodeProduct(delegator, dispatcher, locale, userLogin, productId, quantityUomId, barcode, isSyncThread);
			if (ServiceUtil.isError(resultBarcode)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultBarcode));
			}
			
			// check PLU code
			String idPLUCode = (String) context.get("idPLUCode");
			if (UtilValidate.isNotEmpty(idPLUCode)) {
				int idPLUCodeLength = idPLUCode.length();
				if (idPLUCodeLength < 5) {
					idPLUCode = formatPaddedString(idPLUCode, 5);
				} else if (idPLUCodeLength > 5) {
					idPLUCode = idPLUCode.substring(idPLUCodeLength - 5, idPLUCodeLength);
				}
			}
			Map<String, Object> resultPLUCode = ProductUtils.createOrStorePLUCodeProduct(delegator, dispatcher, locale, userLogin, productId, quantityUomId, idPLUCode);
			if (ServiceUtil.isError(resultPLUCode)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultPLUCode));
			}
			
			List<EntityCondition> conditions = FastList.newInstance();
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			String productTypeId = product.getString("productTypeId");
			String productConfigItem = null;
			if ("AGGREGATED".equals(productTypeId)) {
				productConfigItem = (String) context.get("productConfigItem");
			}
			
			Timestamp salesDiscontinuationDate = null;
			Timestamp purchaseDiscontinuationDate = null;
			try {
				Long salesDiscontinuationDateStr = (Long) context.get("salesDiscontinuationDate");
				Long purchaseDiscontinuationDateStr = (Long) context.get("purchaseDiscontinuationDate");
				if (UtilValidate.isNotEmpty(salesDiscontinuationDateStr)) {
					context.remove("salesDiscontinuationDate");
					salesDiscontinuationDate = new Timestamp(salesDiscontinuationDateStr);
				}
				if (UtilValidate.isNotEmpty(purchaseDiscontinuationDateStr)) {
					context.remove("purchaseDiscontinuationDate");
					purchaseDiscontinuationDate = new Timestamp(purchaseDiscontinuationDateStr);
				}
			} catch (Exception e) {
	        	Debug.logWarning("Error: format sales discountinuation date and purchase discountinuation date", module);
	        }
			
			String salesUomId = (String) context.get("salesUomId");
			String purchaseUomId = (String) context.get("purchaseUomId");
			if (UtilValidate.isEmpty(salesUomId)) salesUomId = quantityUomId;
			if (UtilValidate.isEmpty(purchaseUomId)) purchaseUomId = quantityUomId;
			
			Map<String, Object> productCtx = CrabEntity.fastMaking(delegator, "Product", context);
			productCtx.put("salesUomId", salesUomId);
			productCtx.put("purchaseUomId", purchaseUomId);

			String weightStr = (String) context.get("weight");
			String productWeightStr = (String) context.get("productWeight");
			BigDecimal weight = null;
			BigDecimal productWeight = null;
			if (UtilValidate.isNotEmpty(weightStr)) weight = new BigDecimal(weightStr);
			if (UtilValidate.isNotEmpty(productWeightStr)) productWeight = new BigDecimal(productWeightStr);
			productCtx.put("weight", weight);
			productCtx.put("productWeight", productWeight);
			
			productCtx.remove("productTypeId");
			// put info images
			if (UtilValidate.isNotEmpty(context.get("largeImageUrl"))) {
				productCtx.put("largeImageUrl", context.get("largeImageUrl"));
			}
			if (UtilValidate.isNotEmpty(context.get("smallImageUrl"))) {
				productCtx.put("smallImageUrl", context.get("smallImageUrl"));
			}
			
			// sales and purchase discountinuation date
			productCtx.put("salesDiscontinuationDate", salesDiscontinuationDate);
			productCtx.put("purchaseDiscontinuationDate", purchaseDiscontinuationDate);
			
			String requireAmount = null;
			if (UtilValidate.isNotEmpty(productCtx.get("amountUomTypeId"))) requireAmount = "Y";
			productCtx.put("requireAmount", requireAmount);
			if (UtilValidate.isEmpty(productCtx.get("internalName"))) productCtx.put("internalName", productCtx.get("productName"));
			
			// update product
			Map<String, Object> prodUpdateResult = dispatcher.runSync("updateProduct", productCtx);
			if (ServiceUtil.isError(prodUpdateResult)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(prodUpdateResult));
			}
			
			// add feature type in product attributes, store feature types of virtual product
			List<String> featureTypeIds = (List<String>) context.get("featureTypeIds");
			List<GenericValue> productFeatureTypeIds = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "featureTypes"), null, false);
			String serviceNameFeatureTypeId = null;
			if (UtilValidate.isNotEmpty(productFeatureTypeIds)) {
				serviceNameFeatureTypeId = "updateProductAttribute";
			} else if (UtilValidate.isNotEmpty(featureTypeIds)) {
				serviceNameFeatureTypeId = "createProductAttribute";
			}
			if (serviceNameFeatureTypeId != null) {
				String featureTypeIdsStr = StringUtils.join(featureTypeIds, ";");
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "featureTypes");
				productCtx.put("attrValue", featureTypeIdsStr);
				productCtx.put("userLogin", userLogin);
				Map<String, Object> featureTypeResult = dispatcher.runSync(serviceNameFeatureTypeId, productCtx);
				if (ServiceUtil.isError(featureTypeResult)) {
					Debug.logError("Error occur when update feature type attribute", module);
				}
			}
			
			
			String isVirtual = product.getString("isVirtual");
			String isVariant = product.getString("isVariant");
			
			
			// add features to product
			if (!isVirtual.equals(isVariant)) {
				String productFeatureApplTypeId = "STANDARD_FEATURE";
				if (isVirtual.equals("Y")) {
					productFeatureApplTypeId = "SELECTABLE_FEATURE";
					
					// create product attribute 'display color'
					String attrName = EntityUtilProperties.getPropertyValue("po.properties", "productAttrName.displayColor", delegator);
					if (UtilValidate.isNotEmpty(attrName)) {
						List<GenericValue> productAttributes = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", attrName), null, false);
						String serviceName = "updateProductAttribute";
						if (UtilValidate.isEmpty(productAttributes)) {
							serviceName = "createProductAttribute";
						}
						
						productCtx.clear();
						productCtx.put("productId", productId);
						productCtx.put("attrName", attrName);
						productCtx.put("attrValue", context.get("displayColor"));
						productCtx.put("userLogin", userLogin);
						dispatcher.runSync(serviceName, productCtx);
					} else {
						Debug.logError("Not found attrName of attribute 'displayColor'", module);
					}
				}
				
				// applyFeatureToProduct(delegator, dispatcher, context.get("feature"), productId, productFeatureApplTypeId, userLogin);
				List<String> featureIds = (List<String>) context.get("featureIds");
				if (UtilValidate.isNotEmpty(featureIds)) {
					applyFeatureToProduct(delegator, dispatcher, featureIds, productId, productFeatureApplTypeId, userLogin);
				}
			}
			
			// add product to category
			Set<String> productCategoryIds = FastSet.newInstance();
			// check primary category
			String primaryProductCategoryId = (String) context.get("primaryProductCategoryId");
			if (UtilValidate.isNotEmpty(primaryProductCategoryId)) {
				productCategoryIds.add(primaryProductCategoryId);
			}
			// check list other category
			List<String> otherProductCategoryIds = (List<String>) context.get("productCategoryIds");
			if (UtilValidate.isNotEmpty(otherProductCategoryIds)) {
				productCategoryIds.addAll(otherProductCategoryIds);
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("productId", productId));
			conditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listProductCategoryIdExists = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(productCategoryIds)) {
				// create product category member
				for (String categoryId : productCategoryIds) {
					conditions.clear();
					conditions.add(EntityUtil.getFilterByDateExpr());
					conditions.add(EntityCondition.makeCondition("productCategoryId", categoryId));
					conditions.add(EntityCondition.makeCondition("productId", productId));
					List<GenericValue> listProductCategoryMember = EntityUtil.filterByCondition(listProductCategoryIdExists, EntityCondition.makeCondition(conditions));
					
					if (UtilValidate.isEmpty(listProductCategoryMember)) {
						// find next sequence number
						Long sequenceNumber = getNextSequeceCategoryMember(delegator, categoryId);
						
						productCtx.clear();
						productCtx.put("fromDate", nowTimestamp);
						productCtx.put("productId", productId);
						productCtx.put("productCategoryId", categoryId);
						productCtx.put("sequenceNum", sequenceNumber);
						productCtx.put("userLogin", userLogin);
						Map<String, Object> addToCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
						if (ServiceUtil.isError(addToCategoryResult)) {
							Debug.logError("Error occur when add product id = " + productId + " to product category = " + categoryId, module);
							continue;
						}
					}
				}
			}
			// thru date other category of product
			conditions.clear();
			if (UtilValidate.isNotEmpty(productCategoryIds)) conditions.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_IN, UtilMisc.toList(productCategoryIds)));
			conditions.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
			conditions.add(EntityCondition.makeCondition("productId", productId));
			conditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listProductCategory = delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> listCategoryIds = EntityUtil.getFieldListFromEntityList(listProductCategory, "productCategoryId", false);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, listCategoryIds));
			List<GenericValue> listProductCategoryMemberOther = EntityUtil.filterByCondition(listProductCategoryIdExists, EntityCondition.makeCondition(conditions));
			if (UtilValidate.isNotEmpty(listProductCategoryMemberOther)) {
				for (GenericValue categoryItem : listProductCategoryMemberOther) {
					categoryItem.set("thruDate", nowTimestamp);
				}
				delegator.storeAll(listProductCategoryMemberOther);
			}
			
			
			// add product to TAX category
			String taxProductCategoryId = (String) context.get("taxProductCategoryId");
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
			conditions.add(EntityCondition.makeCondition("productId", productId));
			conditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listCategoryMemberTax = delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
			if (UtilValidate.isNotEmpty(listCategoryMemberTax)) {
//				String oldProductCategoryIdTax = listCategoryMemberTax.get(0).getString("productCategoryId");
				//result.put("oldProductCategoryIdTax", oldProductCategoryIdTax);
			}
			if (UtilValidate.isNotEmpty(taxProductCategoryId)) {
				GenericValue categoryTaxExists = EntityUtil.getFirst(EntityUtil.filterByCondition(listCategoryMemberTax, EntityCondition.makeCondition("productCategoryId", taxProductCategoryId)));
				if (categoryTaxExists == null) {
					// create product category member tax
					// find next sequence number
					Long sequenceNumber = getNextSequeceCategoryMember(delegator, taxProductCategoryId);
					
					productCtx.clear();
					productCtx.put("fromDate", nowTimestamp);
					productCtx.put("productId", productId);
					productCtx.put("productCategoryId", taxProductCategoryId);
					productCtx.put("sequenceNum", sequenceNumber);
					productCtx.put("userLogin", userLogin);
					Map<String, Object> addToTaxCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
					if (ServiceUtil.isError(addToTaxCategoryResult)) {
						Debug.logError("Error occur when add product id = " + productId + " to product category = " + taxProductCategoryId, module);
					}
				} else {
					listCategoryMemberTax.remove(categoryTaxExists);
				}
			}
			// thru date other TAX category of product
			if (UtilValidate.isNotEmpty(listCategoryMemberTax)) {
				for (GenericValue memberTax : listCategoryMemberTax) {
					GenericValue categoryMemberTax = delegator.findOne("ProductCategoryMember", 
							UtilMisc.toMap("productId", memberTax.get("productId"), "productCategoryId", memberTax.get("productCategoryId"), "fromDate", memberTax.get("fromDate")), false);
					if (categoryMemberTax != null) {
						categoryMemberTax.set("thruDate", nowTimestamp);
						delegator.store(categoryMemberTax);
					}
				}
			}
			
			
			// add price to product
			String taxInPrice = (String) context.get("taxInPrice");
			if (UtilValidate.isEmpty(taxInPrice)) taxInPrice = "N";
			
			String currencyUomId = (String) context.get("currencyUomId");
			boolean isPriceIncludedVat = "Y".equals((String) context.get("isPriceIncludedVat")) ? true : false;
			BigDecimal taxPercentage = null;
			if (isPriceIncludedVat) {
				List<EntityCondition> condsTax = FastList.newInstance();
				condsTax.add(EntityCondition.makeCondition("productId", productId));
				condsTax.add(EntityUtil.getFilterByDateExpr());
				GenericValue taxCategoryGV = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRateSimple", EntityCondition.makeCondition(condsTax), UtilMisc.toSet("taxPercentage"), null, null, false));
				if (taxCategoryGV != null) taxPercentage = taxCategoryGV.getBigDecimal("taxPercentage");
			}
			
			// update product price with type is "DEFAULT_PRICE"
			//BigDecimal productDefaultPrice = (BigDecimal) context.get("productDefaultPrice");
			String productDefaultPriceStr = (String) context.get("productDefaultPrice");
			BigDecimal productDefaultPrice = null;
			if (UtilValidate.isNotEmpty(productDefaultPriceStr)) productDefaultPrice = new BigDecimal(productDefaultPriceStr);
			BigDecimal taxAmountDefaultPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountDefaultPrice"))) taxAmountDefaultPrice = new BigDecimal((String) context.get("taxAmountDefaultPrice"));
			
			// update product price with type is "LIST_PRICE"
			//BigDecimal productListPrice = (BigDecimal) context.get("productListPrice");
			String productListPriceStr = (String) context.get("productListPrice");
			BigDecimal productListPrice = null;
			if (UtilValidate.isNotEmpty(productListPriceStr)) productListPrice = new BigDecimal(productListPriceStr);
			BigDecimal taxAmountListPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountListPrice"))) taxAmountListPrice = new BigDecimal((String) context.get("taxAmountListPrice"));
			
			// create or store product price
			Map<String, Object> resultProductPrice = ProductUtils.createOrStoreProductPrice(productId, currencyUomId, quantityUomId, taxInPrice, productDefaultPrice, productListPrice, taxAmountDefaultPrice, taxAmountListPrice, isPriceIncludedVat, taxPercentage, delegator, dispatcher, userLogin);
			if (ServiceUtil.isError(resultProductPrice)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultProductPrice));
			}
			
			// update DayN
			String dayN = (String) context.get("dayN");
			List<GenericValue> productAttributes = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "DAYN"), null, false);
			String serviceNameDayN = null;
			if (UtilValidate.isNotEmpty(productAttributes)) {
				serviceNameDayN = "updateProductAttribute";
			} else if (UtilValidate.isNotEmpty(dayN)) {
				serviceNameDayN = "createProductAttribute";
			}
			if (serviceNameDayN != null) {
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "DAYN");
				productCtx.put("attrValue", context.get("dayN"));
				productCtx.put("userLogin", userLogin);
				Map<String, Object> dayNResult = dispatcher.runSync(serviceNameDayN, productCtx);
				if (ServiceUtil.isError(dayNResult)) {
					Debug.logError("Error occur when update dayN", module);
				}
			}
			
			// update shelfLife
			String shelflife = (String) context.get("shelflife");
			List<GenericValue> productAttributes2 = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "SHELFLIFE"), null, false);
			String serviceNameSelfLife = null;
			if (UtilValidate.isNotEmpty(productAttributes2)) {
				serviceNameSelfLife = "updateProductAttribute";
			} else if (UtilValidate.isNotEmpty(shelflife)) {
				serviceNameSelfLife = "createProductAttribute";
			}
			if (serviceNameSelfLife != null) {
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "SHELFLIFE");
				productCtx.put("attrValue", shelflife);
				productCtx.put("userLogin", userLogin);
				Map<String, Object> shelfLifeResult = dispatcher.runSync(serviceNameSelfLife, productCtx);
				if (ServiceUtil.isError(shelfLifeResult)) {
					Debug.logError("Error occur when update self life", module);
				}
			}
			
			// insert into price product for supplier
			if (UtilValidate.isNotEmpty(context.get("supplierProduct"))) {
				String supplierProduct = (String) context.get("supplierProduct");
				String checkSupplierReferenceStr = (String) context.get("checkSupplierReference");
				Boolean checkSupplierReference = "Y".equals(checkSupplierReferenceStr) ? true : false;
				Map<String, Object> resultSupplierProd = ProductUtils.createOrStoreSupplierProduct(delegator, dispatcher, userLogin, productId, supplierProduct, quantityUomId, checkSupplierReference, locale);
				if (ServiceUtil.isError(resultSupplierProd)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultSupplierProd));
				}
			}
			
			// insert alternative UOM in product config packing
			if (UtilValidate.isNotEmpty(context.get("alterUomData"))) {
				String alterUomData = (String) context.get("alterUomData");
				Map<String, Object> resultAlterUom = ProductUtils.createOrStoreProductAlterUom(delegator, dispatcher, userLogin, locale, productId, quantityUomId, alterUomData, currencyUomId, isPriceIncludedVat, taxPercentage);
				if (ServiceUtil.isError(resultAlterUom)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultAlterUom));
				}
			}
			
			// insert product configuration item
			if (UtilValidate.isNotEmpty(productConfigItem)) {
				Map<String, Object> resultProdConfigItem = ProductUtils.createOrStoreProductConfigItem(delegator, dispatcher, userLogin, locale, productId, productConfigItem);
				if (ServiceUtil.isError(resultProdConfigItem)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultProdConfigItem));
				}
			}
			
			try {
				Map<String, Object> createImageResult = createOrUpdateProductImages(delegator, dispatcher, context, userLogin, productId);
				if (ServiceUtil.isError(createImageResult)) {
					Debug.logError(ServiceUtil.getErrorMessage(createImageResult), module);
				}
			} catch (Exception e) {
				Debug.logError(e, module);
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		result.put("productId", productId);
		return result;
	}
	
	// TODOCHANGE deleted
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> createProductDms(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODUCTPO_NEW");
		if (!hasPermission){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}
		
		try {
			//check productCode
			UniqueUtil.checkProductCode(delegator, context.get("productCode"), context.get("productId"));
			List<String> productCategoryId = (List<String>) context.get("productCategoryId[]");
			String isVirtual = (String) context.get("isVirtual");
			String isVariant = (String) context.get("isVariant");
			String productId = "OLB" + delegator.getNextSeqId("Product");
			Map<String, Object> product = CrabEntity.fastMaking(delegator, "Product", context);
			if (UtilValidate.isNotEmpty(context.get("largeImageUrl"))) {
				product.put("largeImageUrl", context.get("largeImageUrl"));
			}
			if (UtilValidate.isNotEmpty(context.get("smallImageUrl"))) {
				product.put("smallImageUrl", context.get("smallImageUrl"));
			}
			product.put("productId", productId);
			result = dispatcher.runSync("createProduct", product);
			//create SupplierProduct
			ProductUtils.createOrStoreSupplierProduct(delegator, dispatcher, userLogin, productId, (String)context.get("supplierProduct"));
			//addProductToCategory
			if (UtilValidate.isNotEmpty(productCategoryId)) {
				for (String s : productCategoryId) {
					if (UtilValidate.isNotEmpty(s)) {
						List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember",
								EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", s, "productId", productId)),
								null, null, null, false);
						if (UtilValidate.isEmpty(listProductCategoryMember)) {
							product.clear();
							product.put("fromDate", new Timestamp(System.currentTimeMillis()));
							product.put("productId", productId);
							product.put("productCategoryId", s);
							product.put("sequenceNum", 1);
							product.put("userLogin", userLogin);
							dispatcher.runSync("addProductToCategory", product);
						}
					}
				}
			}
			//	create tax
			product.clear();
			List<GenericValue> listProductCategoryMemberTax = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", context.get("taxCatalogs"), "productId", productId)),
					null, null, null, false);
			if (UtilValidate.isEmpty(listProductCategoryMemberTax)) {
				product.put("fromDate", new Timestamp(System.currentTimeMillis()));
				product.put("productId", productId);
				product.put("productCategoryId", context.get("taxCatalogs"));
				product.put("sequenceNum", 1);
				product.put("userLogin", userLogin);
				dispatcher.runSync("addProductToCategory", product);
			}
			if (isVirtual.equals("Y") && isVariant.equals("Y")) {
				throw new Exception();
			}
			if (!isVirtual.equals(isVariant)) {
				String productFeatureApplTypeId = "STANDARD_FEATURE";
				if (isVirtual.equals("Y")) {
					productFeatureApplTypeId = "SELECTABLE_FEATURE";
					if (UtilValidate.isNotEmpty(productId)) {
						//	createProductAttribute
						String attrName = EntityUtilProperties.getPropertyValue("po.properties", "productAttrName.displayColor", delegator);
						if (UtilValidate.isNotEmpty(attrName)) {
							product.clear();
							product.put("productId", productId);
							product.put("attrName", attrName);
							product.put("attrValue", context.get("displayColor"));
							product.put("userLogin", userLogin);
							dispatcher.runSync("createProductAttribute", product);
						} else {
							throw new Exception();
						}
					} else {
						throw new Exception();
					}
				}
				if (isVariant.equals("Y")) {
					if (UtilValidate.isNotEmpty(context.get("productIdTo"))) {
						product.clear();
						product.put("productIdTo", productId);
						product.put("productId", context.get("productIdTo"));
						product.put("productAssocTypeId", "PRODUCT_VARIANT");
						product.put("fromDate", new Timestamp(System.currentTimeMillis()));
						product.put("userLogin", userLogin);
						dispatcher.runSync("createProductAssoc", product);
					} else {
						throw new Exception();
					}
				}
				applyFeatureToProduct(delegator, dispatcher, context.get("feature"), productId, productFeatureApplTypeId, userLogin);
			}
			//createProductPrice DEFAULT_PRICE
			product.clear();
			product.put("productId", productId);
			product.put("productPriceTypeId", "DEFAULT_PRICE");
			product.put("productPricePurposeId", "PURCHASE");
			product.put("currencyUomId", context.get("currencyUomId"));
			product.put("termUomId", context.get("quantityUomId"));
			product.put("productStoreGroupId", "_NA_");
			product.put("price", context.get("productDefaultPrice"));
			product.put("taxInPrice", context.get("taxInPrice"));
			product.put("userLogin", userLogin);
			dispatcher.runSync("createProductPrice", product);
			//createProductPrice LIST_PRICE
			product.clear();
			product.put("productId", productId);
			product.put("productPriceTypeId", "LIST_PRICE");
			product.put("productPricePurposeId", "PURCHASE");
			product.put("currencyUomId", context.get("currencyUomId"));
			product.put("termUomId", context.get("quantityUomId"));
			product.put("productStoreGroupId", "_NA_");
			product.put("price", context.get("productListPrice"));
			product.put("taxInPrice", context.get("taxInPrice"));
			product.put("userLogin", userLogin);
			dispatcher.runSync("createProductPrice", product);
			//create dayN
			product.clear();
			product.put("productId", productId);
			product.put("attrName", "DAYN");
			product.put("attrValue", context.get("dayN"));
			product.put("userLogin", userLogin);
			dispatcher.runSync("createProductAttribute", product);
			//create shelflife
			product.clear();
			product.put("productId", productId);
			product.put("attrName", "SHELFLIFE");
			product.put("attrValue", context.get("shelflife"));
			product.put("userLogin", userLogin);
			dispatcher.runSync("createProductAttribute", product);
			//add config packing
			if(context.get("uomFromId") != null){
				String uomFromId = (String)context.get("uomFromId");
				Timestamp fromDateConfigPacking = new Timestamp((Long)context.get("fromDateConfigPacking"));
				Timestamp thruDateConfigPacking = null;
				if(context.get("thruDateConfigPacking") !=null){
					thruDateConfigPacking = new Timestamp((Long)context.get("thruDateConfigPacking"));
				}
				BigDecimal quantityConvert = new BigDecimal(Integer.parseInt((String)context.get("quantityConvert")));
				product.clear();
				product.put("uomFromId", uomFromId);
				product.put("uomToId", (String)context.get("quantityUomId"));
				product.put("productId", productId);
				product.put("fromDate", fromDateConfigPacking);
				product.put("thruDate", thruDateConfigPacking);
				product.put("quantityConvert", quantityConvert);
				product.put("userLogin", userLogin);
				dispatcher.runSync("UpdateProductConfigPacking", product);
			}
			result.put("productId", productId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static void applyFeatureToProduct(Delegator delegator, LocalDispatcher dispatcher, Object feature, Object productId, Object productFeatureApplTypeId, Object userLogin) throws GenericServiceException, GenericEntityException {
		List<Object> features = new ArrayList<Object>();
		if (feature != null && feature instanceof List) {
			features = (List<Object>) feature;
		} else {
			JSONObject objFeature = JSONObject.fromObject(feature);
			Set<String> keyFeatures = objFeature.keySet();
			for (String s : keyFeatures) {
				JSONArray listFeature = objFeature.getJSONArray(s);
				for (Object f : listFeature) {
					features.add(f);
				}
			}
		}
		List<String> listCurrentFeature = FastList.newInstance();
		Map<String, Object> productCtx = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		
		for (Object f : features) {
			listCurrentFeature.add(f.toString());
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productFeatureId", f, "productFeatureApplTypeId", productFeatureApplTypeId)));
			List<GenericValue> productFeatureAppls = delegator.findList("ProductFeatureAppl",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(productFeatureAppls)) {
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("productFeatureId", f);
				productCtx.put("productFeatureApplTypeId", productFeatureApplTypeId);
				productCtx.put("userLogin", userLogin);
				dispatcher.runSync("applyFeatureToProduct", productCtx);
			}
		}
		if (UtilValidate.isNotEmpty(listCurrentFeature)) {
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)));
			conditions.add(EntityCondition.makeCondition("productFeatureId", EntityJoinOperator.NOT_IN, listCurrentFeature));
			List<GenericValue> productFeatureAppls = delegator.findList("ProductFeatureAppl",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : productFeatureAppls) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
		}
	}
	
	// TODOCHANGE deleted
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> updateProductDms(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODUCTPO_EDIT");
		if (!hasPermission){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}
		try {
			//	check productCode
			UniqueUtil.checkProductCode(delegator, context.get("productCode"), context.get("productId"));
			String productId = (String) context.get("productId");
			List<String> productCategoryId = (List<String>) context.get("productCategoryId[]");
			//	create or store SupplierProduct
			ProductUtils.createOrStoreSupplierProduct(delegator, dispatcher, userLogin, productId, (String)context.get("supplierProduct"));
			Map<String, Object> product = FastMap.newInstance();
			String isVirtual = (String) context.get("isVirtual");
			String isVariant = (String) context.get("isVariant");
			if (isVirtual.equals("Y") && isVariant.equals("Y")) {
				throw new Exception();
			}
			List<EntityCondition> conditions = FastList.newInstance();
			if (!isVirtual.equals(isVariant)) {
				String productFeatureApplTypeId = "STANDARD_FEATURE";
				if (isVirtual.equals("Y")) {
					productFeatureApplTypeId = "SELECTABLE_FEATURE";
					if (UtilValidate.isNotEmpty(productId)) {
						String attrName = EntityUtilProperties.getPropertyValue("po.properties", "productAttrName.displayColor", delegator);
						//	checkProductAttribute
						List<GenericValue> productAttributes = delegator.findList("ProductAttribute",
								EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "attrName", attrName)), null, null, null, false);
						String url = "updateProductAttribute";
						if (UtilValidate.isEmpty(productAttributes)) {
							url = "createProductAttribute";
						}
						//	updateProductAttribute
						if (UtilValidate.isNotEmpty(attrName)) {
							product.clear();
							product.put("productId", productId);
							product.put("attrName", attrName);
							product.put("attrValue", context.get("displayColor"));
							product.put("userLogin", userLogin);
							
							dispatcher.runSync(url, product);
						} else {
							throw new Exception();
						}
					} else {
						throw new Exception();
					}
				}
				applyFeatureToProduct(delegator, dispatcher, context.get("feature"), productId, productFeatureApplTypeId, userLogin);
			}
			if (UtilValidate.isNotEmpty(productCategoryId)) {
				//	create ProductCategoryMember
				for (String s : productCategoryId) {
					conditions.clear();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", s, "productId", productId)));
					List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					if (UtilValidate.isEmpty(listProductCategoryMember)) {
						product.clear();
						product.put("fromDate", new Timestamp(System.currentTimeMillis()));
						product.put("productId", productId);
						product.put("productCategoryId", s);
						product.put("sequenceNum", 1);
						product.put("userLogin", userLogin);
						dispatcher.runSync("addProductToCategory", product);
					}
				}
				//	thrudate other category of product
				conditions.clear();
				if (UtilValidate.isNotEmpty(productCategoryId)) {
					conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_IN, productCategoryId));
				}
				conditions.add(EntityCondition.makeCondition("productCategoryTypeId", EntityJoinOperator.EQUALS, "CATALOG_CATEGORY"));
				List<GenericValue> listProductCategory = delegator.findList("ProductCategory",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				List<String> listProductCategoryId = EntityUtil.getFieldListFromEntityList(listProductCategory, "productCategoryId", true);
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, listProductCategoryId));
				conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
				List<GenericValue> listProductCategoryMemberOther = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				for (GenericValue x : listProductCategoryMemberOther) {
					x.set("thruDate", new Timestamp(System.currentTimeMillis()));
					x.store();
				}
			}
			//	create tax
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", context.get("taxCatalogs"), "productId", productId)));
			List<GenericValue> listProductCategoryMemberTax = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(listProductCategoryMemberTax)) {
				product.clear();
				product.put("fromDate", new Timestamp(System.currentTimeMillis()));
				product.put("productId", productId);
				product.put("productCategoryId", context.get("taxCatalogs"));
				product.put("sequenceNum", 1);
				product.put("userLogin", userLogin);
				dispatcher.runSync("addProductToCategory", product);
			}
			//	thrudate other tax of product
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_EQUAL, context.get("taxCatalogs")));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "TAX_CATEGORY")));
			List<GenericValue> listProductCategoryTax = delegator.findList("ProductCategory",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> listProductCategoryIdTax = EntityUtil.getFieldListFromEntityList(listProductCategoryTax, "productCategoryId", true);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, listProductCategoryIdTax));
			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
			List<GenericValue> listProductCategoryMemberOtherTax = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listProductCategoryMemberOtherTax) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
			//	create tax end
			product.clear();
			product = CrabEntity.fastMaking(delegator, "Product", context);
			if (UtilValidate.isNotEmpty(context.get("largeImageUrl"))) {
				product.put("largeImageUrl", context.get("largeImageUrl"));
			}
			if (UtilValidate.isNotEmpty(context.get("smallImageUrl"))) {
				product.put("smallImageUrl", context.get("smallImageUrl"));
			}
			result = dispatcher.runSync("updateProduct", product);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId", "DEFAULT_PRICE",
					"productPricePurposeId", "PURCHASE", "currencyUomId", context.get("currencyUomId"), "termUomId", context.get("quantityUomId"),
					"productStoreGroupId", "_NA_", "taxInPrice", context.get("taxInPrice"))));
			List<GenericValue> listProductPrice = delegator.findList("ProductPrice",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(listProductPrice)) {
				//	createProductPrice DEFAULT_PRICE
				product.clear();
				product.put("productId", productId);
				product.put("productPriceTypeId", "DEFAULT_PRICE");
				product.put("productPricePurposeId", "PURCHASE");
				product.put("currencyUomId", context.get("currencyUomId"));
				product.put("termUomId", context.get("quantityUomId"));
				product.put("productStoreGroupId", "_NA_");
				product.put("price", context.get("productDefaultPrice"));
				product.put("taxInPrice", context.get("taxInPrice"));
				product.put("userLogin", userLogin);
				dispatcher.runSync("createProductPrice", product);
			} else {
				for (GenericValue x : listProductPrice) {
					x.set("price", context.get("productDefaultPrice"));
					x.store();
				}
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId", "LIST_PRICE",
					"productPricePurposeId", "PURCHASE", "currencyUomId", context.get("currencyUomId"), "termUomId", context.get("quantityUomId"),
					"productStoreGroupId", "_NA_", "taxInPrice", context.get("taxInPrice"))));
			listProductPrice = delegator.findList("ProductPrice",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(listProductPrice)) {
				//	createProductPrice LIST_PRICE
				product.clear();
				product.put("productId", productId);
				product.put("productPriceTypeId", "LIST_PRICE");
				product.put("productPricePurposeId", "PURCHASE");
				product.put("currencyUomId", context.get("currencyUomId"));
				product.put("termUomId", context.get("quantityUomId"));
				product.put("productStoreGroupId", "_NA_");
				product.put("price", context.get("productListPrice"));
				product.put("taxInPrice", context.get("taxInPrice"));
				product.put("userLogin", userLogin);
				dispatcher.runSync("createProductPrice", product);
			} else {
				for (GenericValue x : listProductPrice) {
					x.set("price", context.get("productListPrice"));
					delegator.store(x);
				}
			}
			//	update DayN
			//  checkProductAttribute
			List<GenericValue> productAttributes = delegator.findList("ProductAttribute",
							EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "attrName", "DAYN")), null, null, null, false);
			String url = "updateProductAttribute";
			if (UtilValidate.isEmpty(productAttributes)) {
				url = "createProductAttribute";
			}
			//  updateProductAttribute
			product.clear();
			product.put("productId", productId);
			product.put("attrName", "DAYN");
			product.put("attrValue", context.get("dayN"));
			product.put("userLogin", userLogin);
			dispatcher.runSync(url, product);
			//  update shelflife
			//  checkProductAttribute
			productAttributes = delegator.findList("ProductAttribute",
						EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "attrName", "SHELFLIFE")), null, null, null, false);
			url = "updateProductAttribute";
			if (UtilValidate.isEmpty(productAttributes)) {
				url = "createProductAttribute";
			}
			//  updateProductAttribute
			product.clear();
			product.put("productId", productId);
			product.put("attrName", "SHELFLIFE");
			product.put("attrValue", context.get("shelflife"));
			product.put("userLogin", userLogin);
			dispatcher.runSync(url, product);
			result.put("productId", productId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> checkProductCode(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			UniqueUtil.checkProductCode(delegator, context.get("productCode"), context.get("productId"));
			result.put("check", "true");
		} catch (Exception e) {
			result.put("check", "false");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadProductInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> product = FastMap.newInstance();
			String productId = (String) context.get("productId");
			GenericValue thisProduct = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			String quantityUomId = "";
			if (UtilValidate.isNotEmpty(thisProduct)) {
				quantityUomId = thisProduct.getString("quantityUomId");
				product.putAll(thisProduct);
				String attrName = EntityUtilProperties.getPropertyValue("po.properties", "productAttrName.displayColor", delegator);
				GenericValue productAttribute = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", attrName), false);
				if (UtilValidate.isNotEmpty(productAttribute)) {
					product.put("displayColor", productAttribute.getString("attrValue"));
				}
				productAttribute = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "DAYN"), false);
				if (UtilValidate.isNotEmpty(productAttribute)) {
					product.put("dayN", productAttribute.getString("attrValue"));
				}
				productAttribute = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "SHELFLIFE"), false);
				if (UtilValidate.isNotEmpty(productAttribute)) {
					product.put("shelflife", productAttribute.getString("attrValue"));
				}
				Object productFeatureApplTypeId = "";
				if ("Y".equals(thisProduct.getString("isVirtual"))) {
					productFeatureApplTypeId = "SELECTABLE_FEATURE";
					
				} else if ("Y".equals(thisProduct.getString("isVariant"))) {
					List<GenericValue> listProductAssoc = delegator.findList("ProductAssoc", EntityCondition.makeCondition(UtilMisc.toMap("productIdTo", productId, "productAssocTypeId", "PRODUCT_VARIANT")), null, null, null, false);
					if (UtilValidate.isNotEmpty(listProductAssoc)) {
						GenericValue productAssoc = EntityUtil.getFirst(listProductAssoc);
						product.put("productIdTo", productAssoc.getString("productId"));
					}
					productFeatureApplTypeId = "STANDARD_FEATURE";
				}
				List<EntityCondition> conditions = FastList.newInstance();
				Map<String, Object> feature = FastMap.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition("productId", productId));
				conditions.add(EntityCondition.makeCondition("productFeatureApplTypeId", productFeatureApplTypeId));
				List<GenericValue> productVirtualFeatures = delegator.findList("ProductFeatureAppl", EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isNotEmpty(productVirtualFeatures)) {
					for (GenericValue x : productVirtualFeatures) {
						GenericValue productFeature = delegator.findOne("ProductFeature", UtilMisc.toMap("productFeatureId", x.get("productFeatureId")), false);
						String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
						List<Object> features = FastList.newInstance();
						if (UtilValidate.isNotEmpty(feature.get(productFeatureTypeId))) {
							features = (List<Object>) feature.get(productFeatureTypeId);
						}
						features.add(productFeature.get("productFeatureId"));
						feature.put(productFeatureTypeId, features);
					}
					product.put("feature", feature);
				}
				
				// get list price
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, 
													"productPriceTypeId", "LIST_PRICE", "productPricePurposeId", "PURCHASE", 
													"productStoreGroupId", "_NA_", "termUomId", quantityUomId)));
				List<GenericValue> listProductListPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isNotEmpty(listProductListPrice)) {
					product.put("productListPrice", EntityUtil.getFirst(listProductListPrice));
				}
				
				// get default price
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, 
													"productPriceTypeId", "DEFAULT_PRICE", "productPricePurposeId", "PURCHASE", 
													"productStoreGroupId", "_NA_", "termUomId", quantityUomId)));
				List<GenericValue> listProductDefaultPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isNotEmpty(listProductDefaultPrice)) {
					product.put("productDefaultPrice", EntityUtil.getFirst(listProductDefaultPrice));
				}
				
				// get category TAX
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
				conditions.add(EntityCondition.makeCondition("productId", productId));
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isNotEmpty(listProductCategoryMember)) {
					GenericValue productCategoryMember = EntityUtil.getFirst(listProductCategoryMember);
					product.put("productCategoryTaxId", productCategoryMember.getString("productCategoryId"));
				}
				
				// get supplier product
				List<GenericValue> listSupplierProduct = delegator.findList("SupplierProduct", EntityCondition.makeCondition("productId", productId), UtilMisc.toSet("productId", "partyId", "availableFromDate", "minimumOrderQuantity", "currencyUomId"), null, null, false);
				if (UtilValidate.isNotEmpty(listSupplierProduct)) {
					List<String> supplierIdsInSP = EntityUtil.getFieldListFromEntityList(listSupplierProduct, "partyId", true);
					List<GenericValue> suppliers = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, supplierIdsInSP), UtilMisc.toSet("partyId", "groupName"), null, null, false);
					product.put("supplierId", suppliers);
					
					Map<String, Object> extendSupplierProductId = FastMap.newInstance();
					for (GenericValue x : listSupplierProduct) {
						extendSupplierProductId.put(x.getString("partyId"), x);
					}
					product.put("extendSupplierProductId", extendSupplierProductId);
				}
				
				// get list product category exclude primary category
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("productCategoryTypeId", EntityJoinOperator.NOT_EQUAL, "TAX_CATEGORY"));
				conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				List<String> productCategories = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
				product.put("productCategories", productCategories);
			}
			result.put("product", product);
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		return result;
	}
	
	public static Map<String, Object> loadSupplierProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Long availableFromDateL = (Long) context.get("availableFromDate");
			Timestamp availableFromDate = null;
			if (UtilValidate.isNotEmpty(availableFromDateL)) {
				availableFromDate = new Timestamp(availableFromDateL);
			}
			GenericValue supplierProduct = delegator.findOne("SupplierProduct",
					UtilMisc.toMap("productId", context.get("productId"), "partyId", context.get("partyId"), "availableFromDate", availableFromDate,
							"currencyUomId", context.get("currencyUomId"), "minimumOrderQuantity", context.get("minimumOrderQuantity")), false);
			result.put("supplierProduct", supplierProduct);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> productCategories(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			result = dispatcher.runSync("loadCategoriesOfWebSite",
					UtilMisc.toMap("isEc", "false", "prodCatalogId", context.get("prodCatalogId"), "userLogin", context.get("userLogin")));
			List<String> categoryTypes = UtilMisc.toList("CATALOG_CATEGORY");
			List<Map<String, Object>> categories = (List<Map<String, Object>>) result.get("categories");
			List<Map<String, Object>> productCategories = FastList.newInstance();
			for (Map<String, Object> x : categories) {
				String productCategoryTypeId = (String) x.get("productCategoryTypeId");
				if (categoryTypes.contains(productCategoryTypeId)) {
					Map<String, Object> category = FastMap.newInstance();
					category.putAll(x);
					productCategories.add(category);
				}
			}
			result.clear();
			result.put("productCategories", productCategories);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> applyFeatureToProductCustom(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String productId = (String) context.get("productId");
			String productFeatureApplTypeId = (String) context.get("productFeatureApplTypeId");
			List<String> arrayProductFeatureId = (List<String>) context.get("arrayProductFeatureId[]");
			for (String s : arrayProductFeatureId) {
				List<GenericValue> listProductFeatureAppl = delegator.findList("ProductFeatureAppl",
						EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productFeatureId", s)), null, null, null, false);
				if (UtilValidate.isEmpty(listProductFeatureAppl)) {
					Map<String, Object> mapApplyFeatureToProduct = FastMap.newInstance();
					mapApplyFeatureToProduct.put("productId", productId);
					mapApplyFeatureToProduct.put("productFeatureId", s);
					mapApplyFeatureToProduct.put("productFeatureApplTypeId", productFeatureApplTypeId);
					mapApplyFeatureToProduct.put("userLogin", userLogin);
					dispatcher.runSync("applyFeatureToProduct", mapApplyFeatureToProduct);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> loadCurrencyUomIdBySupplier(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
       	Delegator delegator = ctx.getDelegator();
       	try {
       		String partyId = (String) context.get("partyId");
    		List<GenericValue> listProductCurrencyUomId = new ArrayList<GenericValue>(); 
    		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
    		listProductCurrencyUomId.add(party);
    		result.put("listProductCurrencyUomId", listProductCurrencyUomId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> loadProductPrice(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> productPrice = FastMap.newInstance();
			String productId = (String) context.get("productId");
			String currencyUomId = (String) context.get("currencyUomId");
			String termUomId = (String) context.get("termUomId");
			String taxInPrice = (String) context.get("taxInPrice");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId", "LIST_PRICE", "productPricePurposeId",
					"PURCHASE", "productStoreGroupId", "_NA_", "currencyUomId", currencyUomId, "termUomId", termUomId, "taxInPrice", taxInPrice)));
			List<GenericValue> listProductListPrice = delegator.findList("ProductPrice",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			BigDecimal productListPrice = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(listProductListPrice)) {
				productListPrice = EntityUtil.getFirst(listProductListPrice).getBigDecimal("price");
			}
			productPrice.put("productListPrice", productListPrice);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPriceTypeId", "DEFAULT_PRICE", "productPricePurposeId",
					"PURCHASE", "productStoreGroupId", "_NA_", "currencyUomId", currencyUomId, "termUomId", termUomId, "taxInPrice", taxInPrice)));
			List<GenericValue> listProductDefaultPrice = delegator.findList("ProductPrice",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			BigDecimal productDefaultPrice = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(listProductDefaultPrice)) {
				productDefaultPrice = EntityUtil.getFirst(listProductDefaultPrice).getBigDecimal("price");
			}
			productPrice.put("productDefaultPrice", productDefaultPrice);
			result.put("productPrice", productPrice);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductBrands(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("groupName");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("partyTypeId", "BRANDGROUP"));
			EntityListIterator listIterator = delegator.find("PartyAndGroup",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e){
			e.printStackTrace();
		}
    	return result;
    }
	
	public static Map<String, Object> createProductBrand(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "CONFIG_PRODPACK_NEW");
    	if (!hasPermission){
    		return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
    	}
    	
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String partyCode = (String) context.get("partyCode");
			context.remove("partyCode");
			Map<String, Object> resultCreateBrand = dispatcher.runSync("createPartyGroup", context);
			String partyId = (String) resultCreateBrand.get("partyId");
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode!=null?partyCode:partyId)),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
		} catch (Exception e){
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> getListProductBrands(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<GenericValue> brands = delegator.findList("PartyAndGroup",
					EntityCondition.makeCondition(UtilMisc.toMap("partyTypeId", "BRANDGROUP", "statusId", "PARTY_ENABLED")),
					UtilMisc.toSet("partyId", "partyCode", "groupName"), UtilMisc.toList("groupName"), null, false);
			result.put("brands", brands);
		} catch (Exception e){
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> listSupplierOfProduct(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
		try {
			List<GenericValue> listSuppliers = FastList.newInstance();
			if (UtilValidate.isNotEmpty(context.get("productId"))) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, context.get("productId")));
				String checkActive = (String) context.get("checkActive");
				if ("Y".equals(checkActive)) {
					conditions.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
				}
				conditions.add(EntityCondition.makeCondition("supplierPrefOrderId", EntityOperator.NOT_EQUAL, "80_SGC_SUPPL"));
				listSuppliers = delegator.findList("SuppProAndProdConfAndParty",
						EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-availableThruDate", "lastPrice"), null, false);
			}
			result.put("listSuppliers", listSuppliers);
		} catch (Exception e){
			e.printStackTrace();
		}
    	return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListSuppliersRefByProduct(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		try {
			String productId = SalesUtil.getParameter(parameters, "productId");
			if (productId != null) {
				listAllConditions.add(EntityCondition.makeCondition("productId", productId));
				listAllConditions.add(EntityCondition.makeCondition("supplierPrefOrderId", "80_SGC_SUPPL"));
				listAllConditions.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-availableThruDate");
					listSortFields.add("lastPrice");
				}
				listIterator = delegator.find("SuppProAndProdConfAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSuppliersRefByProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSupplierProduct(DispatchContext ctx, Map<String, Object> context) {
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
				String hasFutureStr = SalesUtil.getParameter(parameters, "hasFuture");
	    		String isActiveStr = SalesUtil.getParameter(parameters, "isActive");
				boolean hasFuture = false;
				boolean isActive = false;
				if ("Y".equals(hasFutureStr)) hasFuture = true;
				if ("Y".equals(isActiveStr)) isActive = true;
				
				listAllConditions.add(EntityCondition.makeCondition("productId", productId));
				if (isActive && !hasFuture) {
					listAllConditions.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
				} else if (isActive && hasFuture) {
					listAllConditions.add(EntityCondition.makeCondition(
				                EntityCondition.makeCondition("availableThruDate", EntityOperator.EQUALS, null),
				                EntityOperator.OR,
				                EntityCondition.makeCondition("availableThruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())
				           ));
				}
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-availableThruDate");
					listSortFields.add("lastPrice");
				}
				
				listIterator = delegator.find("SuppProAndProdConfAndParty", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSupplierProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductBySupplier(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		String supplierId = null;
		if (parameters.containsKey("supplierId") && parameters.get("supplierId").length > 0) {
			supplierId = parameters.get("supplierId")[0];
		}
		
		String currencyUomId = null;
		if (parameters.containsKey("currencyUomId") && parameters.get("currencyUomId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("currencyUomId"))){
				currencyUomId = parameters.get("currencyUomId")[0];
			}
		}
		
		String facilityId = null;
		if (parameters.containsKey("facilityId") && parameters.get("facilityId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("facilityId"))){
				facilityId = parameters.get("facilityId")[0];
			}
		}
		List<String> productIds = new ArrayList<String>();
		List<EntityCondition> mainCondList = FastList.newInstance();
		if (parameters.containsKey("orderId") && parameters.get("orderId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("orderId"))){
				String orderId = parameters.get("orderId")[0];
				
				List<GenericValue> listOrderItems = FastList.newInstance();
				try {
					listOrderItems = POUtil.getOrderItemEditable(delegator, orderId);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				if (!listOrderItems.isEmpty()){
					for (GenericValue item : listOrderItems) {
						productIds.add(item.getString("productId"));
					}
					if (!productIds.isEmpty()) {
						mainCondList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
					}
				}
			}
		}
		
		EntityListIterator listIterator = null;
		List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		
		mainCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
		mainCondList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId));
		EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
		listAllConditions.add(mainCond);
		
		if (!productIds.isEmpty()) {
			try {
				listIterator = delegator.find("SupplierProductGroupAndProduct", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				if (listIterator.hasNext()){
					listProductTmps = listIterator.getCompleteList();
					listIterator.close();
				}
			} catch (GenericEntityException e) {
				String mess = e.toString();
				Debug.logError(e, module);
				return ServiceUtil.returnError(mess);
			}
		} else {
			try {
				listProductTmps = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "SupplierProductGroupAndProduct", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			} catch (GenericEntityException e) {
				String mess = e.toString();
				Debug.logError(e, module);
				return ServiceUtil.returnError(mess);
			}
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		String getQuota = null;
		if (parameters.containsKey("getQuota") && parameters.get("getQuota").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("getQuota"))){
				getQuota = parameters.get("getQuota")[0];
			}
		}
		
		if (UtilValidate.isNotEmpty(listProductTmps)){
            for (GenericValue itemProd : listProductTmps) {
            	Map<String, Object> proMap = FastMap.newInstance();
            	proMap.putAll(itemProd);
            	
            	String productId = (String)itemProd.get("productId");
            	Map<String, Object> attributes = FastMap.newInstance();
            	attributes.put("productId", productId);
            	attributes.put("ownerPartyId", company);
            	attributes.put("facilityId", facilityId);
            	Map<String, Object> mapInv;
				try {
					mapInv = InventoryUtil.getDetailQuantityInventory(delegator, attributes);
				} catch (GenericEntityException e) {
					String mess = e.toString();
					Debug.logError(e, module);
					return ServiceUtil.returnError(mess);
				}
            	
            	BigDecimal qoh = (BigDecimal)mapInv.get("quantityOnHandTotal");
            	BigDecimal atp = (BigDecimal)mapInv.get("availableToPromiseTotal");
            	BigDecimal aoh = (BigDecimal)mapInv.get("amountOnHandTotal");
            	
            	proMap.put("quantityOnHandTotal", qoh);
            	proMap.put("availableToPromiseTotal", atp);
            	proMap.put("amountOnHandTotal", aoh);
            	
            	String purchaseUomId = (String)itemProd.get("purchaseUomId");
            	List<Map<String, Object>> listQtyUoms = new ArrayList<Map<String, Object>>();
            	List<Map<String, Object>> listWeUoms = new ArrayList<Map<String, Object>>();
            	String quantityUomId = itemProd.getString("quantityUomId");
            	BigDecimal minimumQty = BigDecimal.ONE;
				
            	if (ProductUtil.isWeightProduct(delegator, productId)){
            		try {
						listWeUoms = ProductUtil.getProductWeightUomWithConvertNumbers(delegator, productId);
						for (Map<String,Object> map : listWeUoms) {
							if (((String)map.get("uomId")).equals(purchaseUomId)){
								minimumQty = (BigDecimal)map.get("convertNumber");
								break;
							}
						}
					} catch (GenericEntityException e) {
						String mess = e.toString();
						Debug.logError(e, module);
						return ServiceUtil.returnError(mess);
					}
            		String weightUomId = itemProd.getString("weightUomId");
            		purchaseUomId = weightUomId;
            	} else {
            		try {
						listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
						for (Map<String,Object> map : listQtyUoms) {
							if (((String)map.get("quantityUomId")).equals(purchaseUomId)){
								minimumQty = (BigDecimal)map.get("convertNumber");
								break;
							}
						}
					} catch (GenericEntityException e) {
						String mess = e.toString();
						Debug.logError(e, module);
						return ServiceUtil.returnError(mess);
					}
					if (UtilValidate.isEmpty(purchaseUomId)) {
						purchaseUomId = quantityUomId;
					}
            	}
            	proMap.put("uomId", purchaseUomId);
				proMap.put("weightUomIds", listWeUoms);
				proMap.put("quantityUomIds", listQtyUoms);
				
				BigDecimal minimumOrderQuantity = BigDecimal.ONE;
				if (UtilValidate.isNotEmpty(itemProd.get("minimumOrderQuantity"))) {
					minimumOrderQuantity = itemProd.getBigDecimal("minimumOrderQuantity");
					if (minimumOrderQuantity.compareTo(minimumQty) > 0){
						minimumQty = minimumOrderQuantity;
					}
				}
				
				if (UtilValidate.isNotEmpty(getQuota) && "Y".equals(getQuota)) {
					BigDecimal quantityQuota = BigDecimal.ZERO;
					List<GenericValue> listQuotas = FastList.newInstance();
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("productId", productId));
//					conds.add(EntityCondition.makeCondition("supplierPartyId", supplierId));
//					conds.add(EntityCondition.makeCondition("currencyUomId", currencyUomId));
					try {
						listQuotas = delegator.findList("QuotaItemAvailableGroupByProduct", EntityCondition.makeCondition(conds), null, null,
								null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList QuotaItemAvailableGroupByProduct: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listQuotas.isEmpty()){
						for (GenericValue item : listQuotas) {
							quantityQuota = quantityQuota.add(item.getBigDecimal("quotaQuantity"));
						}
					}
					proMap.put("quantityQuota", quantityQuota);
				}
				
				proMap.put("minimumOrderQuantity", minimumQty);

				BigDecimal price = null;
				Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, 
						"currencyUomId", currencyUomId, "partyId", supplierId, "quantity", minimumQty, "amount", null, "quantityUomId", purchaseUomId);
				Map<String, Object> resultCalPrice;
				try {
					resultCalPrice = dispatcher.runSync("calculatePurchasePrice", calPriceCtx);
					if (!ServiceUtil.isError(resultCalPrice)) {
		        		price = (BigDecimal) resultCalPrice.get("price");
		        	}
				} catch (GenericServiceException e) {
					String mess = e.toString();
					Debug.logError(e, module);
					return ServiceUtil.returnError(mess);
				}
	        	
				proMap.put("lastPrice", price);
				proMap.put("uomId", purchaseUomId);
            	listProducts.add(proMap);
            }
		}
		
		successResult.put("listIterator", listProducts);
		return successResult;
	}
	
	public static Map<String, Object> getListProductByAgreement(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		String agreementId = (String) context.get("agreementId");
		
		
		String getQuota = "Y";

		try {
			listProductTmps = delegator.findList("SupplierProductGroupDetailAndAgreementDetail", EntityCondition.makeCondition("agreementId", agreementId), null, null, null, Boolean.FALSE);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (UtilValidate.isNotEmpty(listProductTmps)){
            for (GenericValue itemProd : listProductTmps) {
            	Map<String, Object> proMap = FastMap.newInstance();
            	proMap.putAll(itemProd);
            	
            	String productId = (String)itemProd.get("productId");
            	Map<String, Object> attributes = FastMap.newInstance();
            	attributes.put("productId", productId);
            	
            	String purchaseUomId = (String)itemProd.get("purchaseUomId");
            	List<Map<String, Object>> listQtyUoms = new ArrayList<Map<String, Object>>();
            	List<Map<String, Object>> listWeUoms = new ArrayList<Map<String, Object>>();
            	String quantityUomId = itemProd.getString("quantityUomId");
            	String currencyUomId = itemProd.getString("currencyUomId");
            	BigDecimal minimumQty = BigDecimal.ONE;
				
            	if (ProductUtil.isWeightProduct(delegator, productId)){
            		try {
						listWeUoms = ProductUtil.getProductWeightUomWithConvertNumbers(delegator, productId);
						for (Map<String,Object> map : listWeUoms) {
							if (((String)map.get("uomId")).equals(purchaseUomId)){
								minimumQty = (BigDecimal)map.get("convertNumber");
								break;
							}
						}
					} catch (GenericEntityException e) {
						String mess = e.toString();
						Debug.logError(e, module);
						return ServiceUtil.returnError(mess);
					}
            		String weightUomId = itemProd.getString("weightUomId");
            		purchaseUomId = weightUomId;
            	} else {
            		try {
						listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
						for (Map<String,Object> map : listQtyUoms) {
							if (((String)map.get("quantityUomId")).equals(purchaseUomId)){
								minimumQty = (BigDecimal)map.get("convertNumber");
								break;
							}
						}
					} catch (GenericEntityException e) {
						String mess = e.toString();
						Debug.logError(e, module);
						return ServiceUtil.returnError(mess);
					}
					if (UtilValidate.isEmpty(purchaseUomId)) {
						purchaseUomId = quantityUomId;
					}
            	}
            	proMap.put("uomId", purchaseUomId);
				proMap.put("weightUomIds", listWeUoms);
				proMap.put("quantityUomIds", listQtyUoms);
				
				BigDecimal minimumOrderQuantity = BigDecimal.ONE;
				if (UtilValidate.isNotEmpty(itemProd.get("minimumOrderQuantity"))) {
					minimumOrderQuantity = itemProd.getBigDecimal("minimumOrderQuantity");
					if (minimumOrderQuantity.compareTo(minimumQty) > 0){
						minimumQty = minimumOrderQuantity;
					}
				}
				
				if (UtilValidate.isNotEmpty(getQuota) && "Y".equals(getQuota)) {
					BigDecimal quantityQuota = BigDecimal.ZERO;
					List<GenericValue> listQuotas = FastList.newInstance();
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("productId", productId));
					try {
						listQuotas = delegator.findList("QuotaItemAvailableGroupByProduct", EntityCondition.makeCondition(conds), null, null,
								null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList QuotaItemAvailableGroupByProduct: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listQuotas.isEmpty()){
						for (GenericValue item : listQuotas) {
							quantityQuota = quantityQuota.add(item.getBigDecimal("quotaQuantity"));
						}
					}
					proMap.put("quantityQuota", quantityQuota);
				}
				
				proMap.put("minimumOrderQuantity", minimumQty);

				BigDecimal price = null;
				if (UtilValidate.isNotEmpty(itemProd.get("price"))) {
					price = itemProd.getBigDecimal("price");
				}
				proMap.put("currencyUomId", currencyUomId);
				proMap.put("lastPrice", price);
				proMap.put("uomId", purchaseUomId);
            	listProducts.add(proMap);
            }
		}
		
		
		successResult.put("listProducts", listProducts);
		return successResult;
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetListProductByOrder(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String orderId = null;
		if (parameters.containsKey("orderId") && parameters.get("orderId").length > 0) {
			orderId = parameters.get("orderId")[0];
		}
		
		List<GenericValue> listOrderItems = FastList.newInstance();
		
		try {
			listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null , null, null, true);
			for(GenericValue orderItem: listOrderItems){
				try {
					Map<String, Object> serviceResult = dispatcher.runSync("getReturnableQuantity", UtilMisc.toMap("orderItem", orderItem, "userLogin", userLogin));
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", orderItem.getString("productId")), false);
					Map<String, Object> map = FastMap.newInstance();
					map.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
					map.put("orderId", orderId);
					map.put("productId", orderItem.getString("productId"));
					map.put("productCode", product.getString("productCode"));
					map.put("orderedQuantity", orderItem.getBigDecimal("quantity"));
					map.put("unitPrice", orderItem.getBigDecimal("unitPrice"));
					map.put("itemDescription", orderItem.getString("itemDescription"));
					map.put("returnableQuantity", (BigDecimal)serviceResult.get("returnableQuantity"));
					map.put("quantity", (BigDecimal)serviceResult.get("returnableQuantity"));
					listIterator.add(map);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					Debug.logError(e.getMessage(), "module");
				}
			}
			listIterator = EntityMiscUtil.filterMap(listIterator, listAllConditions);
			listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductByOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductCategory(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String entityName = null;
    		boolean showChildren = true;
    		String showChildrenStr = SalesUtil.getParameter(parameters, "showChildren");
    		if ("N".equals(showChildrenStr)) showChildren = false;
    		
    		String prodCatalogId = SalesUtil.getParameter(parameters, "prodCatalogId");
    		if (prodCatalogId != null) {
    			listAllConditions.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
    			listAllConditions.add(EntityUtil.getFilterByDateExpr());
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("sequenceNum");
    			}
    			entityName = "ProdCatalogCategoryAndProductCategory";
    			showChildren = false;
    		} else {
    			entityName = "ProductCategory";
    			//listAllConditions.add(EntityCondition.makeCondition("primaryParentCategoryId", null));
    		}
    		String productCategoryTypeId = SalesUtil.getParameter(parameters, "productCategoryTypeId");
    		if (productCategoryTypeId != null) {
    			listAllConditions.add(EntityCondition.makeCondition("productCategoryTypeId", productCategoryTypeId));
    		}
    		String rootCategoryId = SalesUtil.getParameter(parameters, "rootCategoryId");
    		if (rootCategoryId != null) {
    			listAllConditions.add(EntityCondition.makeCondition("productCategoryId", rootCategoryId));
    		}
    		List<GenericValue> listProductCategory = delegator.findList(entityName, EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
    		if (showChildren) {
    			if (UtilValidate.isNotEmpty(listProductCategory)) {
    				for (GenericValue productCategory : listProductCategory) {
        				String parentCategoryId = productCategory.getString("productCategoryId");
        				List<Map<String, Object>> productCategoryChildren = ProductWorker.getAllCategoryTreeMap(delegator, parentCategoryId, null, productCategoryTypeId);
        				if (UtilValidate.isNotEmpty(productCategoryChildren)) {
        					listIterator.addAll(productCategoryChildren);
        				}
        			}
    			}
    		} else {
    			listIterator.addAll(listProductCategory);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListProductCategory service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductCategoryRoot(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			listAllConditions.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
			listAllConditions.add(EntityCondition.makeCondition("primaryParentCategoryId", null));
			listIterator = delegator.find("ProductCategory", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductCategoryRoot service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> changeRootCategory(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String prodCatalogId = (String) context.get("prodCatalogId");
			String productCategoryIdFrom = (String) context.get("productCategoryIdFrom");
			String productCategoryIdTo = (String) context.get("productCategoryIdTo");
			if (UtilValidate.isNotEmpty(prodCatalogId)) {
				boolean changeable = false;
				GenericValue productCategoryRootFrom = EntityUtil.getFirst(CatalogWorker.getProdCatalogCategories(delegator, prodCatalogId, "PCCT_BROWSE_ROOT"));
				if (productCategoryIdTo != null) {
					if (productCategoryIdFrom != null) {
						String productCategoryRootIdFrom = productCategoryRootFrom.getString("productCategoryId");
						if (!productCategoryIdTo.equals(productCategoryRootIdFrom)) {
							changeable = true;
						}
					} else {
						changeable = true;
					}
				}
				
				if (changeable) {
					// update all categories in catalog
					Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
					
					// thru date all relationship
					List<EntityCondition> conds = new ArrayList<EntityCondition>();
					conds.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
					conds.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> listProdCatalogCategory = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(conds), null, null, null, false);
					if (UtilValidate.isNotEmpty(listProdCatalogCategory)) {
						for (GenericValue item : listProdCatalogCategory) {
							item.set("thruDate", nowTimestamp);
						}
						delegator.storeAll(listProdCatalogCategory);
					}
					
					// add new relationship with new category
					Map<String, Object> catalogCategoryCtx = new HashMap<String, Object>();
					// 1. product catalog category type is "PCCT_BROWSE_ROOT"
					catalogCategoryCtx.put("prodCatalogId", prodCatalogId);
					catalogCategoryCtx.put("productCategoryId", productCategoryIdTo);
					catalogCategoryCtx.put("prodCatalogCategoryTypeId", "PCCT_BROWSE_ROOT");
					catalogCategoryCtx.put("fromDate", nowTimestamp);
					catalogCategoryCtx.put("sequenceNum", new Long(1));
					catalogCategoryCtx.put("userLogin", userLogin);
					Map<String, Object> rootCategoryResult = dispatcher.runSync("addProductCategoryToProdCatalog", catalogCategoryCtx);
					if (ServiceUtil.isError(rootCategoryResult)) {
						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rootCategoryResult));
					}
					/*// 2. product catalog category type is "PCCT_BEST_SELL"
					String categoryId2 = productCategoryIdTo + "_BSL";
					GenericValue category2 = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId2), false);
					if (category2 != null) {
						catalogCategoryCtx.clear();
						catalogCategoryCtx.put("prodCatalogId", prodCatalogId);
						catalogCategoryCtx.put("productCategoryId", categoryId2);
						catalogCategoryCtx.put("prodCatalogCategoryTypeId", "PCCT_BEST_SELL");
						catalogCategoryCtx.put("fromDate", nowTimestamp);
						catalogCategoryCtx.put("sequenceNum", new Long(2));
						catalogCategoryCtx.put("userLogin", userLogin);
						Map<String, Object> categoryResult2 = dispatcher.runSync("addProductCategoryToProdCatalog", catalogCategoryCtx);
						if (ServiceUtil.isError(categoryResult2)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(categoryResult2));
						}
					}
					// 3. product catalog category type is "PCCT_PROMOTIONS"
					String categoryId3 = productCategoryIdTo + "_PROMOS";
					GenericValue category3 = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId3), false);
					if (category3 != null) {
						catalogCategoryCtx.clear();
						catalogCategoryCtx.put("prodCatalogId", prodCatalogId);
						catalogCategoryCtx.put("productCategoryId", categoryId3);
						catalogCategoryCtx.put("prodCatalogCategoryTypeId", "PCCT_PROMOTIONS");
						catalogCategoryCtx.put("fromDate", nowTimestamp);
						catalogCategoryCtx.put("sequenceNum", new Long(3));
						catalogCategoryCtx.put("userLogin", userLogin);
						Map<String, Object> categoryResult2 = dispatcher.runSync("addProductCategoryToProdCatalog", catalogCategoryCtx);
						if (ServiceUtil.isError(categoryResult2)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(categoryResult2));
						}
					}
					// 4. product catalog category type is "PCCT_WHATS_NEW"
					String categoryId4 = productCategoryIdTo + "_NEW";
					GenericValue category4 = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId4), false);
					if (category4 != null) {
						catalogCategoryCtx.clear();
						catalogCategoryCtx.put("prodCatalogId", prodCatalogId);
						catalogCategoryCtx.put("productCategoryId", categoryId4);
						catalogCategoryCtx.put("prodCatalogCategoryTypeId", "PCCT_WHATS_NEW");
						catalogCategoryCtx.put("fromDate", nowTimestamp);
						catalogCategoryCtx.put("sequenceNum", new Long(4));
						catalogCategoryCtx.put("userLogin", userLogin);
						Map<String, Object> categoryResult2 = dispatcher.runSync("addProductCategoryToProdCatalog", catalogCategoryCtx);
						if (ServiceUtil.isError(categoryResult2)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(categoryResult2));
						}
					}
					// 5. product catalog category type is "PCCT_MOST_POPULAR"
					String categoryId5 = productCategoryIdTo + "_FEATURED";
					GenericValue category5 = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId5), false);
					if (category5 != null) {
						catalogCategoryCtx.clear();
						catalogCategoryCtx.put("prodCatalogId", prodCatalogId);
						catalogCategoryCtx.put("productCategoryId", categoryId5);
						catalogCategoryCtx.put("prodCatalogCategoryTypeId", "PCCT_MOST_POPULAR");
						catalogCategoryCtx.put("fromDate", nowTimestamp);
						catalogCategoryCtx.put("sequenceNum", new Long(5));
						catalogCategoryCtx.put("userLogin", userLogin);
						Map<String, Object> categoryResult2 = dispatcher.runSync("addProductCategoryToProdCatalog", catalogCategoryCtx);
						if (ServiceUtil.isError(categoryResult2)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(categoryResult2));
						}
					}*/
				}
			}
		} catch (Exception e) {
    		String errMsg = "Fatal error calling changeRootCategory service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
		return successResult;
	}
	
	private static String isCheckInfoProductAddToCategory(String productId, Delegator delegator) throws GenericEntityException {
		String reponseMessage = null;
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		if (product != null) {
			String quantityUomId = product.getString("quantityUomId");
			// check product price
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productPriceTypeId", "DEFAULT_PRICE"));
			conds.add(EntityCondition.makeCondition("termUomId", quantityUomId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isEmpty(productPrices)) {
				reponseMessage = "San pham " + productId + " thieu Gia ban";
			}
			
			// check supplier product
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("supplierPrefOrderId", "10_MAIN_SUPPL"));
			conds.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
			List<GenericValue> supplierProducts = delegator.findList("SupplierProduct", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isEmpty(supplierProducts)) {
				reponseMessage = reponseMessage == null ? "San pham " + productId + " thieu Gia mua" : reponseMessage + ", thieu Gia mua";
			}
			
		}
		return reponseMessage;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addProductsToCategory(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String productCategoryId = (String) context.get("productCategoryId");
			List<String> listProductId = (List<String>) context.get("productIds[]");
			List<String> listProductId2 = (List<String>) context.get("productIds");
			if (UtilValidate.isEmpty(listProductId) && UtilValidate.isNotEmpty(listProductId2)) {
				listProductId = listProductId2;
			}
			//GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
			if (UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isNotEmpty(listProductId)) {
				Map<String, Object> productCtx = FastMap.newInstance();
				Long sequenceNum = getNextSequeceCategoryMember(delegator, productCategoryId);
				List<EntityCondition> listConds = FastList.newInstance();
				EntityFindOptions opts = new EntityFindOptions();
				opts.setMaxRows(1);
				
				List<String> responseMessages = FastList.newInstance();
				List<String> productIdsSuccess = FastList.newInstance();
				List<String> productIdsNotSuccess = FastList.newInstance();
				for (String productId : listProductId) {
					String msgCheck = isCheckInfoProductAddToCategory(productId, delegator);
					if (msgCheck != null) {
						productIdsNotSuccess.add(productId);
						responseMessages.add(msgCheck);
						continue;
					}
					//GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					//if (product == null) {
					//	continue;
					//}
					listConds.clear();
					listConds.add(EntityCondition.makeCondition("productCategoryId", productCategoryId));
					listConds.add(EntityCondition.makeCondition("productId", productId));
					listConds.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> productMemberExists = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(listConds), UtilMisc.toSet("productId"), null, opts, false);
					if (UtilValidate.isNotEmpty(productMemberExists)) {
						continue;
					}
					
					productCtx.clear();
					productCtx.put("productCategoryId", productCategoryId);
					productCtx.put("productId", productId);
					productCtx.put("sequenceNum", sequenceNum);
					productCtx.put("userLogin", userLogin);
					Map<String, Object> addResult = dispatcher.runSync("addProductToCategory", productCtx);
					if (ServiceUtil.isError(addResult)) {
						Debug.logWarning(ServiceUtil.getErrorMessage(addResult), module);
					} else {
						sequenceNum++;
						/*if ("CATALOG_CATEGORY".equals(productCategory.getString("productCategoryTypeId")) 
								&& UtilValidate.isEmpty(product.get("primaryProductCategoryId"))) {
							product.set("primaryProductCategoryId", productCategoryId);
							delegator.store(product);
						}*/
					}
					productIdsSuccess.add(productId);
				}
				if (responseMessages.size() > 0) {
					responseMessages.add(0, "Ket qua: " + productIdsSuccess.size() + " san pham thanh cong. " + productIdsNotSuccess.size() + " san pham loi");
					successResult = ServiceUtil.returnSuccess(responseMessages);
					successResult.put("productIdsNotSuccess", productIdsNotSuccess);
					Debug.logWarning("Missing sales or puchase price. List product don't add to category: " + productIdsNotSuccess.toString(), module);
				}
				successResult.put("productIdsSuccess", productIdsSuccess);
			}
		} catch (Exception e) {
    		String errMsg = "Fatal error calling addProductsToCategory service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
		return successResult;
	}
	
	public static Map<String, Object> removeProductCategoryMember(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String productCategoryId = (String) context.get("productCategoryId");
			String productId = (String) context.get("productId");
			if (UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isNotEmpty(productId)) {
				
				List<EntityCondition> listConds = FastList.newInstance();
				List<EntityCondition> listCondsOr = FastList.newInstance();
				listCondsOr.add(EntityCondition.makeCondition("productCategoryId", productCategoryId));
				listCondsOr.add(EntityCondition.makeCondition("productCategoryId", productCategoryId + "_BSL"));
				listCondsOr.add(EntityCondition.makeCondition("productCategoryId", productCategoryId + "_PROMOS"));
				listCondsOr.add(EntityCondition.makeCondition("productCategoryId", productCategoryId + "_NEW"));
				listCondsOr.add(EntityCondition.makeCondition("productCategoryId", productCategoryId + "_FEATURED"));
				listConds.add(EntityCondition.makeCondition(listCondsOr, EntityOperator.OR));
				listConds.add(EntityCondition.makeCondition("productId", productId));
				listConds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> productMemberExists = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(listConds), null, null, null, false);
				if (UtilValidate.isNotEmpty(productMemberExists)) {
					Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
					for (GenericValue productMember : productMemberExists) {
						productMember.set("thruDate", nowTimestamp);
					}
					delegator.storeAll(productMemberExists);
				}
				
				// check and remove primaryProductCategoryId field in product
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				if (product != null && productCategoryId.equals(product.getString("primaryProductCategoryId"))) {
					listConds.clear();
					listConds.add(EntityCondition.makeCondition("productId", productId));
					listConds.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
					listConds.add(EntityUtil.getFilterByDateExpr());
					GenericValue productCategoryMemberOldest = EntityUtil.getFirst(delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(listConds), null, UtilMisc.toList("fromDate"), null, false));
					if (productCategoryMemberOldest != null) {
						String productCategoryIdOldest = productCategoryMemberOldest.getString("productCategoryId");
						product.set("primaryProductCategoryId", productCategoryIdOldest);
					} else {
						product.set("primaryProductCategoryId", null);
					}
					product.store();
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling removeProductCategoryMember service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductUom(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("uomTypeId", "PRODUCT_PACKING"));
    		listIterator = delegator.find("Uom", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListProductUom service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> createProductUom(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSCreateSuccessful", locale));
		try {
			String uomId = (String) context.get("uomId");
			String abbreviation = (String) context.get("abbreviation");
			String description = (String) context.get("description");
			String uomTypeId = "PRODUCT_PACKING";
			
			if (UtilValidate.isNotEmpty(uomId)) {
				GenericValue prodUomExisted = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
				if (prodUomExisted != null) {
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSIdExisted", locale));
				}
			} else {
				uomId = "PP_" + delegator.getNextSeqId("Uom");
			}
			GenericValue productUom = delegator.makeValue("Uom", UtilMisc.toMap(
					"uomId", uomId, "uomTypeId", uomTypeId, "abbreviation", abbreviation, "description", description));
			delegator.create(productUom);
			
			successResult.put("uomId", uomId);
		} catch (Exception e) {
    		String errMsg = "Fatal error calling createProductUom service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
		return successResult;
	}
	
	public static Map<String, Object> deleteProductUom(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String uomId = (String) context.get("uomId");
			GenericValue prodUomExisted = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
			if (prodUomExisted == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSRecordIsNotFound", locale));
			}
			try {
				delegator.removeValue(prodUomExisted);
			} catch (GenericEntityException ex) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSCannotDeleteTheRecordHasBeenUsed", locale));
			}
		} catch (Exception e) {
    		String errMsg = "Fatal error calling deleteProductUom service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
		return successResult;
	}
	
	public static Map<String, Object> updateProductUom(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseSalesUiLabels", "BSCreateSuccessful", locale));
		try {
			String uomId = (String) context.get("uomId");
			GenericValue prodUomExisted = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
			if (prodUomExisted == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSRecordIsNotFound", locale));
			}
			String abbreviation = (String) context.get("abbreviation");
			String description = (String) context.get("description");
			prodUomExisted.set("abbreviation", abbreviation);
			prodUomExisted.set("description", description);
			delegator.store(prodUomExisted);
		} catch (Exception e) {
			String errMsg = "Fatal error calling updateProductUom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> changeProductStateList(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			List<String> productIds = (List<String>) context.get("productIds[]");
			String action = (String) context.get("action");
			if (UtilValidate.isNotEmpty(action) && UtilValidate.isNotEmpty(productIds)) {
				Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
				List<GenericValue> tobeStored = new LinkedList<GenericValue>();
				
				switch (action) {
				case "discontSales":
					for (String productId : productIds) {
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						if (product != null) {
							Timestamp salesDiscontinuationDate = product.getTimestamp("salesDiscontinuationDate");
							if (salesDiscontinuationDate == null || nowTimestamp.compareTo(salesDiscontinuationDate) < 0) {
								product.set("salesDiscontinuationDate", nowTimestamp);
								tobeStored.add(product);
							}
						}
					}
					break;
				case "discontPurchase":
					for (String productId : productIds) {
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						if (product != null) {
							Timestamp purchaseDiscontinuationDate = product.getTimestamp("purchaseDiscontinuationDate");
							if (purchaseDiscontinuationDate == null || nowTimestamp.compareTo(purchaseDiscontinuationDate) < 0) {
								product.set("purchaseDiscontinuationDate", nowTimestamp);
								tobeStored.add(product);
							}
						}
					}
					break;
				case "contSales":
					for (String productId : productIds) {
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						if (product != null) {
							Timestamp salesDiscontinuationDate = product.getTimestamp("salesDiscontinuationDate");
							if (salesDiscontinuationDate != null && nowTimestamp.compareTo(salesDiscontinuationDate) >= 0) {
								product.set("salesDiscontinuationDate", null);
								tobeStored.add(product);
							}
						}
					}
					break;
				case "contPurchase":
					for (String productId : productIds) {
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						if (product != null) {
							Timestamp purchaseDiscontinuationDate = product.getTimestamp("purchaseDiscontinuationDate");
							if (purchaseDiscontinuationDate != null && nowTimestamp.compareTo(purchaseDiscontinuationDate) >= 0) {
								product.set("purchaseDiscontinuationDate", null);
								tobeStored.add(product);
							}
						}
					}
					break;
				default:
					break;
				}
				if (UtilValidate.isNotEmpty(tobeStored)) {
					delegator.storeAll(tobeStored);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling changeProductStateList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductAlterUom(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String productId = SalesUtil.getParameter(parameters, "productId");
			if (UtilValidate.isNotEmpty(productId)) {
				listAllConditions.add(EntityCondition.makeCondition("productId", productId));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				//listAllConditions.add(EntityCondition.makeCondition("quantityConvert", EntityOperator.NOT_EQUAL, BigDecimal.ONE));
				List<GenericValue> configPackingAppls = delegator.findList("ConfigPacking", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
				List<EntityCondition> conds2 = new ArrayList<EntityCondition>();
				for (GenericValue item : configPackingAppls) {
					Map<String, Object> itemNew = item.getAllFields();
					// price
					conds2.clear();
					conds2.add(EntityCondition.makeCondition("productId", productId));
					conds2.add(EntityCondition.makeCondition("productPriceTypeId", "DEFAULT_PRICE"));
					conds2.add(EntityUtil.getFilterByDateExpr());
					conds2.add(EntityCondition.makeCondition("termUomId", item.get("uomFromId")));
					GenericValue productPriceDF = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(conds2), null, null, null, false));
					if (productPriceDF != null) {
						BigDecimal priceValue = null;
						if (productPriceDF.get("price") != null) {
							priceValue = productPriceDF.getBigDecimal("price");
							if (productPriceDF.get("taxAmount") != null) {
								priceValue = priceValue.add(productPriceDF.getBigDecimal("taxAmount"));
							}
						}
						itemNew.put("priceTotal", priceValue);
						itemNew.put("price", productPriceDF.get("price"));
						itemNew.put("taxAmount", productPriceDF.get("taxAmount"));
					}
					// barcode
					List<GenericValue> barcode2 = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "SKU", "uomId", item.get("uomFromId")), null, false);
					if (UtilValidate.isNotEmpty(barcode2)) {
						List<String> barcodeId2 = EntityUtil.getFieldListFromEntityList(barcode2, "idValue", true);
						itemNew.put("barcode", StringUtils.join(barcodeId2, ", "));
					}
					listIterator.add(itemNew);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductAlterUom service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductBuyAll(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
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
			
			List<String> supplierIds = new ArrayList<String>();
			if (parameters.containsKey("supplierIds") && parameters.get("supplierIds").length > 0) {
    			String[] supplierIdsStr = parameters.get("supplierIds");
    			supplierIds = Arrays.asList(supplierIdsStr);
			}
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, supplierIds));
			
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
            
            //listSortFields.add("sequenceNum");
            listSortFields.add("productCode");
            //listIterator = delegator.find("SupplierProductAndProductFind", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
            listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "SupplierProductAndProductFind", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductSellAll service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductInCategoryInclude(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator prodMemberIter = null;
		try {
			String productCategoryId = SalesUtil.getParameter(parameters, "productCategoryId");
			if (UtilValidate.isNotEmpty(productCategoryId)) {
				List<String> productCategoryIds = ProductWorker.getAllCategoryTree(delegator, productCategoryId, "CATALOG_CATEGORY");
				listAllConditions.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("productCategoryId");
					listSortFields.add("sequenceNum");
				}
				Set<String> fieldsToSelect = FastSet.newInstance();
				fieldsToSelect.add("productCategoryId");
				fieldsToSelect.add("sequenceNum");
				fieldsToSelect.add("productId");
				fieldsToSelect.add("productCode");
				fieldsToSelect.add("productName");
				prodMemberIter = delegator.find("ProductCategoryAndProductMember", EntityCondition.makeCondition(listAllConditions), null, fieldsToSelect, listSortFields, opts);
				
				List<GenericValue> listProducts = SalesUtil.processIterator(prodMemberIter, parameters, successResult);
				if (UtilValidate.isNotEmpty(listProducts)) {
					for (GenericValue productMember : listProducts) {
						Map<String, Object> item = productMember.getAllFields();
						item.putAll(isBestSell(delegator, productMember.getString("productId"), productMember.getString("productCategoryId")));
						listIterator.add(item);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductInCategoryInclude service: " + e.toString();
			Debug.logError(e, errMsg, module);
		} finally {
			if (prodMemberIter != null) {
				try {
					prodMemberIter.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Error when close entity list iterator", module);
				}
			}
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductAndPurchasePrice(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String supplierId = SalesUtil.getParameter(parameters, "supplierId");
			GenericValue supplier = delegator.findOne("Party", UtilMisc.toMap("partyId", supplierId), false);
			if (supplier != null) {
				String currencyUomId = supplier.getString("preferredCurrencyUomId");
				
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("productId");
				}
				EntityListIterator listProductIter = delegator.find("Product", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				List<GenericValue> listProducts = SalesUtil.processIterator(listProductIter, parameters, successResult);
				if (UtilValidate.isNotEmpty(listProducts)) {
					for (GenericValue productMember : listProducts) {
						Map<String, Object> item = productMember.getAllFields();
						
						// get purchase price
						BigDecimal purchasePrice = null;
						Map<String, Object> purchasePriceCtx = UtilMisc.toMap("productId", productMember.get("productId"), 
								"partyId", supplierId, "currencyUomId", currencyUomId, 
								"uomId", productMember.getString("quantityUomId"), "quantity", "1", "userLogin", userLogin, "locale", locale);
						Map<String, Object> purchasePriceResult = dispatcher.runSync("getLastPriceBySupplierProductAndQuantity", purchasePriceCtx, 300, true);
						if (ServiceUtil.isSuccess(purchasePriceResult)) {
							if (UtilValidate.isNotEmpty(purchasePriceResult.get("lastPrice"))) {
								purchasePrice = (BigDecimal) purchasePriceResult.get("lastPrice");
							}
						} else {
							Debug.logWarning("Error when get purchase price of product in jqGetListProductAndPurchasePrice service, productId = " + productMember.getString("productId"), module);
						}
						item.put("purchasePrice", purchasePrice);
						item.put("currencyUomId", currencyUomId);
						
						listIterator.add(item);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductAndPurchasePrice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> isBestSell(Delegator delegator, String productId, String productCategoryId) throws GenericEntityException {
		Map<String, Object> bestSell = FastMap.newInstance();
		String productCategoryId_BSL = productCategoryId.toUpperCase() + "_BSL";
		String productCategoryId_PROMOS = productCategoryId.toUpperCase() + "_PROMOS";
		String productCategoryId_NEW = productCategoryId.toUpperCase() + "_NEW";
		String productCategoryId_FEATURED = productCategoryId.toUpperCase() + "_FEATURED";
		
		List<EntityCondition> conditions = FastList.newInstance();
		
		// check category best selling
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("productId", productId));
		conditions.add(EntityCondition.makeCondition("productCategoryId", productCategoryId_BSL));
		List<GenericValue> productCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isBestSell", true);
			bestSell.put("bestSellFromDate", EntityUtil.getFirst(productCategoryMembers).getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isBestSell", false);
		}
		
		// check category promotions
		conditions.clear();
		productCategoryMembers.clear();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("productId", productId));
		conditions.add(EntityCondition.makeCondition("productCategoryId", productCategoryId_PROMOS));
		productCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isPromos", true);
			bestSell.put("promosFromDate", EntityUtil.getFirst(productCategoryMembers).getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isPromos", false);
		}
		
		// check category what's new
		conditions.clear();
		productCategoryMembers.clear();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("productId", productId));
		conditions.add(EntityCondition.makeCondition("productCategoryId", productCategoryId_NEW));
		productCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isNew", true);
			bestSell.put("newFromDate", EntityUtil.getFirst(productCategoryMembers).getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isNew", false);
		}
		
		// check category featured
		conditions.clear();
		productCategoryMembers.clear();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("productId", productId));
		conditions.add(EntityCondition.makeCondition("productCategoryId", productCategoryId_FEATURED));
		productCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isFeatured", true);
			bestSell.put("featuredFromDate", EntityUtil.getFirst(productCategoryMembers).getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isFeatured", false);
		}
		return bestSell;
	}
	
	public static Map<String, Object> createSupplierProductHoanm(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String password = (String) context.get("password");
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(password.getBytes(Charset.forName("UTF8")));
			byte[] resultByte = messageDigest.digest();
			String result = new String(Hex.encodeHex(resultByte));
			if ("3b4192c03f0c21650ddb43f5a5bc6873".equals(result)) {
				Delegator delegator = ctx.getDelegator();
				LocalDispatcher dispatcher = ctx.getDispatcher();
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "OLBPOM"), false);
				EntityListIterator listIterator = null;
				try {
					List<EntityCondition> conditions = FastList.newInstance();
					List<GenericValue> supplierProducts = delegator.findList("SupplierProduct", null, null, null, null, false);
					conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.NOT_IN, EntityUtil.getFieldListFromEntityList(supplierProducts, "productId", true)));
					listIterator = delegator.find("Product", EntityCondition.makeCondition(conditions), null, null, null, null);
					GenericValue product = null;
					while ((product = listIterator.next()) != null) {
						boolean beganTx = TransactionUtil.begin(7200);
						Map<String, Object> prodCtx = FastMap.newInstance();
						prodCtx.put("productId", product.get("productId"));
						prodCtx.put("partyId", "NB010286999");
						prodCtx.put("availableFromDate", String.valueOf(System.currentTimeMillis()));
						prodCtx.put("minimumOrderQuantity", "1");
						prodCtx.put("lastPrice", getRandom(10000, 8000000));
						prodCtx.put("shippingPrice", getRandom(1000, 800000));
						prodCtx.put("currencyUomId", "VND");
						prodCtx.put("supplierProductId", product.get("productId") + "NB010286999");
						prodCtx.put("canDropShip", "N");
						prodCtx.put("comments", product.get("productName"));
						prodCtx.put("userLogin", userLogin);
						try {
							dispatcher.runSync("addNewSupplierForProductId", prodCtx);
						} catch (Exception e) {
							TransactionUtil.rollback(beganTx, e.getMessage(), e);
						}
						TransactionUtil.commit(beganTx);
					}
				} catch (Exception e) {
					String errMsg = "Fatal error calling jqGetListProductSellAll service: " + e.toString();
					Debug.logError(e, errMsg, module);
				} finally {
					if (listIterator != null) {
						listIterator.close();
					}
				}
			} else {
				Locale locale = (Locale) context.get("locale");
				successResult = ServiceUtil.returnError(UtilProperties.getMessage("SecurityUiLabels", "FormFieldTitle_newPasswordVerify", locale));
			}
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		return successResult;
	}
	private static String getRandom(int min, int max) {
		Random rand = new Random();
		int value = rand.nextInt(max) + min;
		return String.valueOf(value);
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListProductUPCCodes(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		//Locale locale = (Locale) context.get("locale");
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		try {
			String productId = SalesUtil.getParameter(parameters, "productId");
			if (UtilValidate.isNotEmpty(productId)) {
				listAllConditions.add(EntityCondition.makeCondition("productId", productId));
				
				Set<String> listSelectFields = UtilMisc.toSet("productId", "productCode", "measureUomId", "measureValue", "idValue", "goodIdentificationTypeId");
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("idValue");
				}
				listIterator = delegator.find("GoodIdentificationMeasureAndProduct", EntityCondition.makeCondition(listAllConditions), null, listSelectFields, listSortFields, opts);
				successResult.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductUPCCodes service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return successResult;
	}
	
	public static Map<String, Object> createProductUPCCode(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			String productId = (String) context.get("productId");
			String measureUomId = (String) context.get("measureUomId");
			BigDecimal measureValue = (BigDecimal) context.get("measureValue");
			String goodIdentificationTypeId = (String) context.get("goodIdentificationTypeId");
			
			GenericValue goodIdExisted = delegator.findOne("GoodIdentificationMeasure", UtilMisc.toMap("goodIdentificationTypeId", goodIdentificationTypeId, "productId", productId, "measureUomId", measureUomId, "measureValue", measureValue), false);
			if (goodIdExisted != null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSIdExisted", locale));
			}
			
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (product == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
			}
			
			// PLU code
			GenericValue prodPLUCode = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId, "goodIdentificationTypeId", "PLU", "uomId", product.get("quantityUomId")), null, false));
			if (prodPLUCode == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSIsNotFound", locale) + "PLU code");
			}
			
			if ("UPCA".equals(goodIdentificationTypeId)) {
				GenericValue measureUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", measureUomId), false);
				
				// make UPCA code
				String idValue = com.olbius.basesales.product.ProductUtils.makeUpcId(prodPLUCode.getString("idValue"), measureUom.getString("uomTypeId"), measureValue);
				if (UtilValidate.isEmpty(idValue)) {
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
				}
				
				goodIdExisted = delegator.makeValue("GoodIdentificationMeasure", UtilMisc.toMap(
						"goodIdentificationTypeId", goodIdentificationTypeId, 
						"productId", productId, 
						"measureUomId", measureUomId, 
						"measureValue", measureValue,
						"idValue", idValue));
				delegator.create(goodIdExisted);
			} else if ("EAN".equals(goodIdentificationTypeId)) {
				// is EAN13
				
				// get PrefixWeightBarcode
				String prefixWeightBarcode = null;
				GenericValue prefixWeightBarcodeGV = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "PrefixWeightBarcode"), false);
				if (prefixWeightBarcodeGV != null) {
					prefixWeightBarcode = prefixWeightBarcodeGV.getString("systemValue");
				}
				
				// get PatternWeightBarcode
				String patternWeightBarcode = null;
				GenericValue patternWeightBarcodeGV = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "PatternWeightBarcode"), false);
				if (patternWeightBarcodeGV != null) {
					patternWeightBarcode = patternWeightBarcodeGV.getString("systemValue");
				}
				
				// get DecimalsInWeight
				Integer decimalsInWeight = null;
				GenericValue decimalsInWeightGV = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "DecimalsInWeight"), false);
				if (decimalsInWeightGV != null) {
					String decimalsInWeightStr = decimalsInWeightGV.getString("systemValue");
					decimalsInWeight = Integer.parseInt(decimalsInWeightStr);
				}
				
				if (prefixWeightBarcode == null || patternWeightBarcode == null || decimalsInWeight == null) {
					return ServiceUtil.returnError("Missing parameter config Weight barcode");
				}
				
				// make EAN13 code
				String idValue = com.olbius.basesales.product.ProductUtils.makeEanId(prodPLUCode.getString("idValue"), measureValue, prefixWeightBarcode, patternWeightBarcode, decimalsInWeight);
				if (UtilValidate.isEmpty(idValue)) {
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
				}
				
				goodIdExisted = delegator.makeValue("GoodIdentificationMeasure", UtilMisc.toMap(
						"goodIdentificationTypeId", goodIdentificationTypeId, 
						"productId", productId, 
						"measureUomId", measureUomId, 
						"measureValue", measureValue,
						"idValue", idValue));
				delegator.create(goodIdExisted);
				
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling createProductUPCCode service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	public static Map<String, Object> deleteProductUPCCode(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			String productId = (String) context.get("productId");
			String measureUomId = (String) context.get("measureUomId");
			BigDecimal measureValue = (BigDecimal) context.get("measureValue");
			String goodIdentificationTypeId = (String) context.get("goodIdentificationTypeId");
			
			GenericValue goodIdExisted = delegator.findOne("GoodIdentificationMeasure", UtilMisc.toMap(
					"goodIdentificationTypeId", goodIdentificationTypeId, 
					"productId", productId, 
					"measureUomId", measureUomId, 
					"measureValue", measureValue), false);
			if (goodIdExisted == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSIsNotFound", locale));
			}
			
			delegator.removeValue(goodIdExisted);
		} catch (Exception e) {
			String errMsg = "Fatal error calling deleteProductUPCCode service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListUomByType(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		//Locale locale = (Locale) context.get("locale");
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		try {
			String uomTypeId = SalesUtil.getParameter(parameters, "uomTypeId");
			if (UtilValidate.isNotEmpty(uomTypeId)) {
				listAllConditions.add(EntityCondition.makeCondition("uomTypeId", uomTypeId));
				
				Set<String> listSelectFields = UtilMisc.toSet("uomId", "uomTypeId", "abbreviation", "description");
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("uomId");
				}
				listIterator = delegator.find("Uom", EntityCondition.makeCondition(listAllConditions), null, listSelectFields, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListUomByType service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static String formatPaddedString(String idValue, int numericPadding) {
        StringBuilder outStrBfr = new StringBuilder(idValue);
        while (numericPadding > outStrBfr.length()) {
            outStrBfr.insert(0, '0');
        }
        return outStrBfr.toString();
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListProductMainUpc(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		String productId = SalesUtil.getParameter(parameters, "productId");
    		if (UtilValidate.isNotEmpty(productId)) {
    			listAllConditions.add(EntityCondition.makeCondition("productId", productId));
    			
    			if (UtilValidate.isEmpty(listSortFields)) {
    				listSortFields.add("uomId");
    			}
        		listIterator = delegator.find("GoodIdentification", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListProductMainUpc service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> updateProductMainUPC(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        try {
        	String productId = (String) context.get("productId");
        	String upcListStr = (String) context.get("upcList");
        	
        	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
        	if (product == null) {
        		return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductNotFound", locale));
        	}
        	
        	List<Map<String, Object>> listUPC = new ArrayList<Map<String,Object>>();
        	JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(upcListStr)) {
				jsonArray = JSONArray.fromObject(upcListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject upcItem = jsonArray.getJSONObject(i);
					Map<String, Object> upcMapItem = FastMap.newInstance();
					if (upcItem.containsKey("productId")) upcMapItem.put("productId", upcItem.getString("productId"));
					if (upcItem.containsKey("idValue")) upcMapItem.put("idValue", upcItem.getString("idValue"));
					if (upcItem.containsKey("uomId")) upcMapItem.put("uomId", upcItem.getString("uomId"));
					if (upcItem.containsKey("iupprm")) upcMapItem.put("iupprm", upcItem.getString("iupprm"));
					listUPC.add(upcMapItem);
				}
			}
			
			if (UtilValidate.isNotEmpty(listUPC)) {
    			List<Map<String, Object>> upcListResult = FastList.newInstance();
    			List<GenericValue> tobeStored = FastList.newInstance();
				for (Map<String, Object> upcItem : listUPC) {
	    			String idValue = (String) upcItem.get("idValue");
	    			String uomId = (String) upcItem.get("uomId");
	    			String iupprmStr = (String) upcItem.get("iupprm");
	    			Long iupprm = null;
	    			if (UtilValidate.isNotEmpty(iupprmStr) && !"null".equals(iupprmStr)) {
	    				try {
	    					iupprm = new Long(iupprmStr);
	    				} catch (Exception e1) {
	    					Debug.logWarning("Convert String to Long is error, iupprm = " + iupprmStr, module);
	    				}
	    			}
	    			
	    			List<EntityCondition> conds = FastList.newInstance();
	    			conds.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
	    			conds.add(EntityCondition.makeCondition("productId", productId));
	    			conds.add(EntityCondition.makeCondition("idValue", idValue));
	    			conds.add(EntityCondition.makeCondition("uomId", uomId));
	    			GenericValue goodIdentificationSku = EntityUtil.getFirst(delegator.findList("GoodIdentification", EntityCondition.makeCondition(conds), null, null, null, false));
	    			if (goodIdentificationSku != null) {
	    				goodIdentificationSku.put("iupprm", iupprm);
	    				tobeStored.add(goodIdentificationSku);
	    			}
	    			
	    			Map<String, Object> upcMapItem = FastMap.newInstance();
	    			upcMapItem.put("productId", productId);
	    			upcMapItem.put("idValue", idValue);
	    			upcMapItem.put("uomId", uomId);
	    			upcMapItem.put("iupprm", iupprm);
	    			upcListResult.add(upcMapItem);
				}
				if (tobeStored.size() > 0) {
					delegator.storeAll(tobeStored);
				}
				successResult.put("upcListResult", upcListResult);
			}
			successResult.put("productId", productId);
        } catch (GenericEntityException e) {
        	String errMsg = "Fatal error calling updateProductMainUPC service: " + e.toString();
			Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
        }
        return successResult;
    }
	
	public static Map<String, Object> updateSalesProductPrice(DispatchContext ctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		try {
			String taxInPrice = "N";
				
			boolean isPriceIncludedVat = "Y".equals((String) context.get("isPriceIncludedVat")) ? true : false;
			
			String productId = (String) context.get("productId");
			String currencyUomId = (String) context.get("currencyUomId");
			BigDecimal productDefaultPrice = (BigDecimal) context.get("defaultPrice");
			BigDecimal productListPrice = (BigDecimal) context.get("listPrice");
			String quantityUomId = (String) context.get("quantityUomId");
			
			BigDecimal taxPercentage = null;
			Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
			if (productTax != null) {
				taxPercentage = (BigDecimal) productTax.get("taxPercentage");
			}
			
			BigDecimal taxAmountDefaultPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountDefaultPrice"))) taxAmountDefaultPrice = new BigDecimal((String) context.get("taxAmountDefaultPrice"));
			
			BigDecimal taxAmountListPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountListPrice"))) taxAmountListPrice = new BigDecimal((String) context.get("taxAmountListPrice"));
			
			// create product price with type is "DEFAULT_PRICE"
			List<EntityCondition> conditions = FastList.newInstance();
			if (productDefaultPrice == null || productDefaultPrice.compareTo(BigDecimal.ZERO) <= 0) {
				conditions.clear();
				conditions.add(EntityUtil.getFilterByDateExpr());
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, 
						"productPriceTypeId", "DEFAULT_PRICE", "productPricePurposeId", "PURCHASE", 
						"currencyUomId", currencyUomId, "termUomId", quantityUomId, "productStoreGroupId", "_NA_"))); //"taxInPrice", context.get("taxInPrice")
				GenericValue defaultPriceGV = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false));
				if (defaultPriceGV != null) {
					productDefaultPrice = defaultPriceGV.getBigDecimal("price");
				}
			}
			
			// create product price with type is "LIST_PRICE"
			if (productListPrice == null || productListPrice.compareTo(BigDecimal.ZERO) <= 0) {
				conditions.clear();
				conditions.add(EntityUtil.getFilterByDateExpr());
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, 
						"productPriceTypeId", "LIST_PRICE", "productPricePurposeId", "PURCHASE", 
						"currencyUomId", currencyUomId, "termUomId", quantityUomId, "productStoreGroupId", "_NA_"))); //"taxInPrice", context.get("taxInPrice")
				GenericValue listPriceGV = EntityUtil.getFirst(delegator.findList("ProductPrice", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false));
				if (listPriceGV != null) {
					productListPrice = listPriceGV.getBigDecimal("price");
				}
			}
			if (taxPercentage != null) {
				if (productDefaultPrice != null) {
					taxAmountDefaultPrice = productDefaultPrice.multiply(taxPercentage).divide(ProductUtils.PERCENT_SCALE, ProductUtils.salestaxCalcDecimals, ProductUtils.salestaxRounding);
				}
				if (productListPrice != null) {
					taxAmountListPrice = productListPrice.multiply(taxPercentage).divide(ProductUtils.PERCENT_SCALE, ProductUtils.salestaxCalcDecimals, ProductUtils.salestaxRounding);
				}
			}
			if (isPriceIncludedVat) {
				if (UtilValidate.isEmpty(context.get("defaultPrice"))) productDefaultPrice = productDefaultPrice.add(taxAmountDefaultPrice);
				if (UtilValidate.isEmpty(context.get("listPrice"))) productListPrice = productListPrice.add(taxAmountListPrice);
			}
			
			// create product price
			Map<String, Object> resultProductPrice = ProductUtils.createOrStoreProductPrice(productId, currencyUomId, quantityUomId, taxInPrice, productDefaultPrice, productListPrice, taxAmountDefaultPrice, taxAmountListPrice, isPriceIncludedVat, taxPercentage, delegator, dispatcher, userLogin);
			if (ServiceUtil.isError(resultProductPrice)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultProductPrice));
			}
				
			successResult.put("productId", productId);
		} catch (GenericEntityException | GenericServiceException e) {
			String errMsg = "Fatal error calling updateSalesProductPrice service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		
		return successResult;
	}
	
	public static Map<String, Object> loadQuantityUomIdByProduct(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			result.put("uoms", delegator.findList("ProductPackagingUomAndUom", EntityCondition.makeCondition(UtilMisc.toMap("productId", context.get("productId"), "uomTypeId", "PRODUCT_PACKING")), null, null, null, false));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> getInfoProductAddToQuotPO(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, Object> productInfo = FastMap.newInstance();
		try {
			String productId = (String) context.get("productId");
			String currencyUomId = (String) context.get("currencyUomId");
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
			
			List<Map<String, Object>> packingUomIds = com.olbius.basepo.product.ProductWorker.getListQuantityUomIdsPO(product, product.getString("quantityUomId"), currencyUomId, delegator, dispatcher);
			productInfo.put("packingUomIds", packingUomIds);
			
			Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
			if (productTax != null) {
				productInfo.put("taxPercentage", productTax.get("taxPercentage"));
			}
			
			/*GenericValue productTempData = EntityUtil.getFirst(delegator.findByAnd("ProductTempData", UtilMisc.toMap("productId", productId), null, false));
			if (productTempData != null) {
				productInfo.put("currencyUomId", productTempData.get("currencyUomId"));
				productInfo.put("unitPrice", null);
			}*/
			productInfo.put("currencyUomId", currencyUomId);
			productInfo.put("unitPrice", null);
			
			if (UtilValidate.isNotEmpty(packingUomIds)) {
				for (Map<String, Object> uomItem : packingUomIds) {
					if (quantityUomId != null && quantityUomId.equals((String) uomItem.get("uomId"))) {
						productInfo.put("quantityConvert", uomItem.get("quantityConvert"));
						productInfo.put("unitPrice", uomItem.get("unitPriceConvert"));
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getInfoProductAddToQuotPO service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("productInfo", productInfo);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductQuotationPO(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
    	try {
    		if (UtilValidate.isEmpty(listSortFields)) {
    			listSortFields.add("-createDate");
    		}
    		boolean isViewAllStatus = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATIONPO_NEW") || securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATIONPO_APPROVE");
    		if (!isViewAllStatus) {
    			listAllConditions.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("productQuotationModuleTypeId", "PURCHASE_QUOTATION"));
	    	listIterator = delegator.find("ProductQuotationDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductQuotationPO service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> getOrderItemToUpdate(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		
		String supplierId = null;
		if (context.containsKey("supplierId")) {
			supplierId = (String)context.get("supplierId");
		}
		
		String currencyUomId = null;
		if (context.containsKey("currencyUomId")) {
			currencyUomId = (String)context.get("currencyUomId");
		}
		
		String facilityId = null;
		if (context.containsKey("facilityId")) {
			facilityId = (String)context.get("facilityId");
		}
		
		String orderId = null;
		if (context.containsKey("orderId")) {
			orderId = (String)context.get("orderId");
		}
		
		String entity = "SupplierProductGroupAndProductAll";
		
		List<EntityCondition> mainCondList = FastList.newInstance();
		if (UtilValidate.isNotEmpty(orderId)){
			
			List<String> listStatusCanBeEdit = FastList.newInstance();
			listStatusCanBeEdit.add("ITEM_CREATED");
			listStatusCanBeEdit.add("ITEM_APPROVED");
			listStatusCanBeEdit.add("ITEM_ESTIMATED");
			
			List<EntityCondition> listConds = FastList.newInstance();
			
			listConds.add(EntityCondition.makeCondition("orderId", orderId));
			listConds.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, listStatusCanBeEdit));
			
			EntityCondition condPromo1 = EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, "N");
			EntityCondition condPromo2 = EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, null);
			List<EntityCondition> condOrs = FastList.newInstance();
			condOrs.add(condPromo1);
			condOrs.add(condPromo2);
			EntityCondition condPromo = EntityCondition.makeCondition(condOrs, EntityOperator.OR);
			listConds.add(condPromo);
			
			List<GenericValue> listOrderItems = FastList.newInstance();
			try {
				listOrderItems = delegator.findList("OrderItemAndProductDetail", EntityCondition.makeCondition(listConds), null, null, null, false);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: findList OrderItemAndProductDetail error! " + e.toString());
			}
			List<String> productIds = FastList.newInstance();
			for (GenericValue item : listOrderItems) {
				productIds.add(item.getString("productId"));
			}
			if (!productIds.isEmpty()) {
				mainCondList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
			}
		}
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		try {
			mainCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
			mainCondList.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId));
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			listProductTmps = delegator.findList(entity, EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(listProductTmps)){
                for (GenericValue itemProd : listProductTmps) {
                	String productId = (String)itemProd.get("productId");
                	Map<String, Object> proMap = FastMap.newInstance();
                	proMap.putAll(itemProd);
                	BigDecimal qoh = BigDecimal.ZERO;
                	BigDecimal atp = BigDecimal.ZERO;
                	BigDecimal aoh = BigDecimal.ZERO;
            		Map<String, Object> attributes = FastMap.newInstance();
                	attributes.put("productId", productId);
                	attributes.put("facilityId", facilityId);
                	GenericValue objProductFacility = null;
					try {
						objProductFacility = delegator.findOne("ProductFacility", false, attributes);
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError("OLBIUS: findOne ProductFacility error! " + e.toString());
					}
					if (UtilValidate.isNotEmpty(objProductFacility)) {
						if (UtilValidate.isNotEmpty(objProductFacility.getBigDecimal("lastInventoryCount"))) {
							qoh = qoh.add(objProductFacility.getBigDecimal("lastInventoryCount"));
		                	aoh = aoh.add(objProductFacility.getBigDecimal("lastInventoryCount"));
						}
					}
                	proMap.put("quantityOnHandTotal", qoh);
                	proMap.put("availableToPromiseTotal", atp);
                	proMap.put("amountOnHandTotal", aoh);
                	
                	String purchaseUomId = (String)itemProd.get("purchaseUomId");
                	String requireAmount = (String)itemProd.get("requireAmount");
                	List<Map<String, Object>> listQtyUoms = new ArrayList<Map<String, Object>>();
                	List<Map<String, Object>> listWeUoms = new ArrayList<Map<String, Object>>();
                	String quantityUomId = itemProd.getString("quantityUomId");
                	if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)){
                		listWeUoms = ProductUtil.getProductWeightUomWithConvertNumbers(delegator, productId);
                		String weightUomId = itemProd.getString("weightUomId");
    					if (UtilValidate.isEmpty(purchaseUomId)) {
    						purchaseUomId = weightUomId;
    						proMap.put("uomId", purchaseUomId);
    					} else {
    						GenericValue uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", purchaseUomId));
    						if (!"WEIGHT_MEASURE".equals(uom.getString("uomTypeId"))) {
    							purchaseUomId = weightUomId;
        						proMap.put("uomId", purchaseUomId);
    						}
    					}
                	} else {
    					if (UtilValidate.isEmpty(purchaseUomId)) {
    						purchaseUomId = quantityUomId;
    						proMap.put("uomId", purchaseUomId);
    					}
                	}
            		listQtyUoms = ProductUtil.getProductPackingUomWithConvertNumbers(delegator, productId);
					proMap.put("weightUomIds", listWeUoms);
					proMap.put("quantityUomIds", listQtyUoms);
					
					Boolean check = false;
					for (Map<String, Object> map : listQtyUoms) {
						String uomId = (String)map.get("quantityUomId");
						if (uomId.equals(purchaseUomId)){
							BigDecimal price = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, supplierId, currencyUomId, purchaseUomId, BigDecimal.ONE);
							proMap.put("lastPrice", price);
							proMap.put("uomId", purchaseUomId);
							check = true;
							break;
						}
					}
					if (!check){
						for (Map<String, Object> map : listWeUoms) {
							String uomId = (String)map.get("uomId");
							if (uomId.equals(purchaseUomId)){
								BigDecimal price = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, supplierId, currencyUomId, purchaseUomId, BigDecimal.ONE);
								proMap.put("lastPrice", price);
								proMap.put("weightUomId", purchaseUomId);
								proMap.put("uomId", purchaseUomId);
								check = true;
								break;
							}
						}
					}
					if (!check && (UtilValidate.isEmpty(requireAmount) || !"Y".equals(requireAmount))){
						BigDecimal price = ProductUtil.getLastPriceBySupplierProductAndQuantity(delegator, productId, supplierId, currencyUomId, quantityUomId, BigDecimal.ONE);
						proMap.put("lastPrice", price);
						proMap.put("uomId", quantityUomId);
					}
                	listProducts.add(proMap);
                }
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductBySupplier service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("listProducts", listProducts);
		return successResult;
	}

	public static Map<String, Object> createDataSampleProducts(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
				String sizeProducts = parameters.get("sizeProducts")[0];
				int len = Integer.parseInt(sizeProducts);
				for (int i=0; i < len; i++){
					String productCode = parameters.get("products["+i+"][productCode]")[0];
					String productName = parameters.get("products["+i+"][productName]")[0];
					String quantityUomId = parameters.get("products["+i+"][quantityUomId]")[0];
					String productDefaultPriceStr = parameters.get("products["+i+"][productDefaultPrice]")[0];
					String productListPriceStr = parameters.get("products["+i+"][productListPrice]")[0];
					String currencyUomId = parameters.get("products["+i+"][currencyUomId]")[0];
					String weightUomId = parameters.get("products["+i+"][weightUomId]")[0];
					String sequence = parameters.get("products["+i+"][sequence]")[0];
					String productWeightStr = parameters.get("products["+i+"][productWeight]")[0];
					String weightStr = parameters.get("products["+i+"][weight]")[0];
					BigDecimal productDefaultPrice = null;
					if (checkValidInputData(productDefaultPriceStr).get("isValidStatus").equals("valid")) productDefaultPrice = new BigDecimal(productDefaultPriceStr);
					BigDecimal productListPrice = null;
					if (checkValidInputData(productListPriceStr).get("isValidStatus").equals("valid")) productListPrice = new BigDecimal(productListPriceStr);
					BigDecimal productWeight = null;
					if (checkValidInputData(productWeightStr).get("isValidStatus").equals("valid")) productWeight = new BigDecimal(productWeightStr);
					BigDecimal weight = null;
					if (checkValidInputData(weightStr).get("isValidStatus").equals("valid")) weight = new BigDecimal(weightStr);

					Map<String, Object> productCtx = FastMap.newInstance();
						productCtx.put("productCode", productCode);
						productCtx.put("productName", productName);
						productCtx.put("productTypeId", "FINISHED_GOOD");
						productCtx.put("quantityUomId", quantityUomId);
						productCtx.put("productDefaultPrice", productDefaultPrice!=null?productDefaultPrice.toString():productDefaultPriceStr);
						productCtx.put("productListPrice", productListPrice!=null?productListPrice.toString():productListPriceStr);
						productCtx.put("currencyUomId", currencyUomId);
						productCtx.put("productWeight", productWeight);
						productCtx.put("weight", weight);
						productCtx.put("weightUomId", weightUomId);
						productCtx.put("userLogin", userLogin);
					Map<String, String> validate = checkValidateProductFromExcel(productCtx, locale, delegator, productDefaultPriceStr, productListPriceStr, productWeightStr, weightStr);
					if (validate.get("statusValidate") == "false"){
						productCtx.put("statusImport","error");
						productCtx.put("message",validate.get("message"));
						productCtx.put("sequence",sequence);
						listIterator.add(productCtx);
						continue;
					}
					Map<String, Object>	result = dispatcher.runSync("createProductFromFileExcel", productCtx);
						productCtx.put("sequence",sequence);
					if(result.containsKey("productId")){
						productCtx.put("statusImport","success");
						productCtx.put("message", UtilProperties.getMessage("BaseSalesUiLabels", "BSCreateSuccessful", locale));
					}else{
						productCtx.put("statusImport","error");
						productCtx.put("message",result.get("message"));
					}
					listIterator.add(productCtx);
				}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductQuotationPO service aa: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, String> checkValidInputData(String str){
		Map<String, String> results = FastMap.newInstance();
		if (UtilValidate.isEmpty(str)){
			results.put("isValidStatus", "empty");
		}else{
			if (str.equals("_NA_")){
				results.put("isValidStatus", "notValid");
			}else{
				results.put("isValidStatus", "valid");
			}
		}
		return results;
	}

	public static Map<String, String> checkValidateProductFromExcel(Map<String, Object> dataCtx, Locale locale, Delegator delegator, String productDefaultPrice,
			String productListPrice, String productWeight, String weight){
		Map<String, String> results = FastMap.newInstance();
		String message = "";
		if (!UtilValidate.isEmpty((String) dataCtx.get("productCode"))){
			if(dataCtx.get("productCode").equals("_NA_")){
				message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSProductCodeIsNotValid", locale) + "\n";
			}else{
				try {
					UniqueUtil.checkProductCode(delegator, dataCtx.get("productCode"), null);
				} catch (Exception e) {
					message += UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductIdIsAlreadyExists", locale) + "\n";
				}
			}
		}
		if (UtilValidate.isEmpty((String) dataCtx.get("productName"))){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSProductNameNotYetAvailable", locale) + "\n";
		}
		if (checkValidInputData((String) dataCtx.get("quantityUomId")).get("isValidStatus").equals("empty")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSQuantityUomNotYetAvailable", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("quantityUomId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSQuantityUomIsNotValid", locale) + "\n";
		}
		if (checkValidInputData((String) dataCtx.get("currencyUomId")).get("isValidStatus").equals("empty")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSCurrencyUomNotYetAvailable", locale) + "\n";
		}else if(checkValidInputData((String) dataCtx.get("currencyUomId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSCurrencyUomIsNotValid", locale) + "\n";
		}
		if (checkValidInputData((String) dataCtx.get("weightUomId")).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSWeightUomIsNotValid", locale) + "\n";
		}
		if (checkValidInputData(productListPrice).get("isValidStatus").equals("empty")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSProductListPriceNotYetAvailable", locale) + "\n";
		}else if(checkValidInputData(productListPrice).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSProductListPriceIsNotValid", locale) + "\n";
		}
		if (checkValidInputData(productDefaultPrice).get("isValidStatus").equals("empty")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSProductDefaultPriceNotYetAvailable", locale) + "\n";
		}else if(checkValidInputData(productDefaultPrice).get("isValidStatus").equals("notValid")){
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSProductDefaultPriceIsNotValid", locale) + "\n";
		}
		if (checkValidInputData(productWeight).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSProductWeightIsNotValid", locale) + "\n";
		}
		if (checkValidInputData(weight).get("isValidStatus").equals("notValid")) {
			message += UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSWeightIsNotValid", locale) + "\n";
		}
		if (message.length()>0){
			results.put("statusValidate", "false");
		}else{
			results.put("statusValidate", "true");
		}
		results.put("message", message);
		return results;
	}

	public static Map<String, Object> createProductFromFileExcel(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> messageError = FastMap.newInstance();

		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SYS_IMPORT_DATA_PRODUCT");
		if (!hasPermission){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}

		String productId = (String) context.get("productId");
		try {
			String productCode = (String) context.get("productCode")!=""? (String) context.get("productCode"):null;
			if (UtilValidate.isNotEmpty(productCode)) {
				// check productCode is available or no
				try {
					UniqueUtil.checkProductCode(delegator, context.get("productCode"), context.get("productId"));
				} catch (Exception e) {
					Debug.logWarning("Product id is exists", module);
					messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductIdIsAlreadyExists", locale));
					return messageError;
				}
			}
			List<EntityCondition> conds = new ArrayList<EntityCondition>();
			// check barcode
			String barcode = (String) context.get("barcode");
			if (UtilValidate.isNotEmpty(barcode)) {
				List<String> barcodes = ProductUtils.splitBarcodeArrayString(barcode);
				// check barcode's existence
				conds.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
				conds.add(EntityCondition.makeCondition("idValue", EntityOperator.IN, barcodes));
				List<GenericValue> barcodeExisted = delegator.findList("GoodIdentification", EntityCondition.makeCondition(conds), null, null, null, false);
				if (UtilValidate.isNotEmpty(barcodeExisted)) {
					Debug.logWarning("Barcode " + barcode + " is exists", module);
					List<String> barcodeIdExisted = EntityUtil.getFieldListFromEntityList(barcodeExisted, "idValue", true);
					messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSBarcodeHasBeenUsedParam", UtilMisc.toMap("barcode", barcodeIdExisted.toString()), locale));
					return messageError;
				}
			}
			// check PLU code
			String idPLUCode = (String) context.get("idPLUCode");
			if (UtilValidate.isNotEmpty(idPLUCode)) {
				int idPLUCodeLength = idPLUCode.length();
				if (idPLUCodeLength < 5) {
					idPLUCode = formatPaddedString(idPLUCode, 5);
				} else if (idPLUCodeLength > 5) {
					idPLUCode = idPLUCode.substring(idPLUCodeLength - 5, idPLUCodeLength);
				}
				// check barcode's existence
				List<GenericValue> barcodeExisted = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("idValue", idPLUCode), null, false);
				if (UtilValidate.isNotEmpty(barcodeExisted)) {
					Debug.logWarning("PLU code " + idPLUCode + " is exists", module);
					messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSPLUCodeHasBeenUsedParam", UtilMisc.toMap("idPLUCode", idPLUCode), locale));
					return messageError;
				}
			}

			String productTypeId = (String) context.get("productTypeId");
			String isVirtual = "N";
			String isVariant = "N";
			String productConfigItem = null;
			if ("AGGREGATED".equals(productTypeId)) {
				productConfigItem = (String) context.get("productConfigItem");
			} else {
				isVirtual = (String) context.get("isVirtual");
				isVariant = (String) context.get("isVariant");
				if (UtilValidate.isEmpty(isVirtual)) isVirtual = "N";
				if (UtilValidate.isEmpty(isVariant)) isVariant = "N";
				if ("Y".equals(isVirtual) && "Y".equals(isVariant)) {
					messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSProductCanNotBothVirtualAndVariant", locale));
					return messageError;
				}
			}

			if (UtilValidate.isEmpty(productId)) {
				productId = "OLB" + delegator.getNextSeqId("Product");
			}

			Timestamp salesDiscontinuationDate = null;
			Timestamp purchaseDiscontinuationDate = null;
			try {
				Long salesDiscontinuationDateStr = (Long) context.get("salesDiscontinuationDate");
				Long purchaseDiscontinuationDateStr = (Long) context.get("purchaseDiscontinuationDate");
				if (UtilValidate.isNotEmpty(salesDiscontinuationDateStr)) {
					context.remove("salesDiscontinuationDate");
					salesDiscontinuationDate = new Timestamp(salesDiscontinuationDateStr);
				}
				if (UtilValidate.isNotEmpty(purchaseDiscontinuationDateStr)) {
					context.remove("purchaseDiscontinuationDate");
					purchaseDiscontinuationDate = new Timestamp(purchaseDiscontinuationDateStr);
				}
			} catch (Exception e) {
				Debug.logWarning("Error: format sales discountinuation date and purchase discountinuation date", module);
			}

			String quantityUomId = (String) context.get("quantityUomId");
			String salesUomId = (String) context.get("salesUomId");
			String purchaseUomId = (String) context.get("purchaseUomId");
			if (UtilValidate.isEmpty(salesUomId)) salesUomId = quantityUomId;
			if (UtilValidate.isEmpty(purchaseUomId)) purchaseUomId = quantityUomId;

			Map<String, Object> productCtx = CrabEntity.fastMaking(delegator, "Product", context);
			productCtx.put("productId", productId);
			productCtx.put("isVirtual", isVirtual);
			productCtx.put("isVariant", isVariant);
			productCtx.put("salesUomId", salesUomId);
			productCtx.put("purchaseUomId", purchaseUomId);

			if (UtilValidate.isEmpty(productCode)) productCode = productId;
			productCtx.put("productCode", productCode);

			// put info images
			if (UtilValidate.isNotEmpty(context.get("largeImageUrl"))) {
				productCtx.put("largeImageUrl", context.get("largeImageUrl"));
			}
			if (UtilValidate.isNotEmpty(context.get("smallImageUrl"))) {
				productCtx.put("smallImageUrl", context.get("smallImageUrl"));
			}
			String internalName = (String) context.get("internalName");
			if (UtilValidate.isEmpty(internalName)) {
				internalName = "";
			}
			productCtx.put("internalName", internalName);

			// sales and purchase discountinuation date
			productCtx.put("salesDiscontinuationDate", salesDiscontinuationDate);
			productCtx.put("purchaseDiscontinuationDate", purchaseDiscontinuationDate);

			String requireAmount = null;
			String useOnlyManualPrice = null;
			if (UtilValidate.isNotEmpty(productCtx.get("amountUomTypeId"))) {
				requireAmount = "Y";
				if ("AGGREGATED".equals(productTypeId)) {
					useOnlyManualPrice = "Y";
				}
			}
			productCtx.put("requireAmount", requireAmount);
			productCtx.put("useOnlyManualPrice", useOnlyManualPrice);

			// create product
			Map<String, Object> createProdResult = dispatcher.runSync("createProduct", productCtx);
			if (ServiceUtil.isError(createProdResult)) {
				messageError.put("message", ServiceUtil.getErrorMessage(createProdResult));
				return messageError;
			}

			// create barcode
			if (UtilValidate.isNotEmpty(barcode)) {
				/*Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId,
						"goodIdentificationTypeId", "SKU",
						"uomId", quantityUomId, "idValue", barcode,
						"userLogin", userLogin);
				Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
				}*/
				Boolean isSyncThread = "Y".equals((String) context.get("isSyncThread")) ? true : false;
				Map<String, Object> createGoodIdenResult = ProductUtils.createOrStoreBarcodeProduct(delegator, dispatcher, locale, userLogin, productId, quantityUomId, barcode, isSyncThread);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					messageError.put("message", ServiceUtil.getErrorMessage(createGoodIdenResult));
					return messageError;
				}
			}

			// create PLU code
			if (UtilValidate.isNotEmpty(idPLUCode)) {
				/*Map<String, Object> goodIdentificationCtx = UtilMisc.toMap("productId", productId,
						"goodIdentificationTypeId", "PLU",
						"uomId", quantityUomId, "idValue", idPLUCode,
						"userLogin", userLogin);
				Map<String, Object> createGoodIdenResult = dispatcher.runSync("createGoodIdentification", goodIdentificationCtx);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createGoodIdenResult));
				}*/
				Map<String, Object> createGoodIdenResult = ProductUtils.createOrStorePLUCodeProduct(delegator, dispatcher, locale, userLogin, productId, quantityUomId, idPLUCode);
				if (ServiceUtil.isError(createGoodIdenResult)) {
					messageError.put("message",ServiceUtil.getErrorMessage(createGoodIdenResult));
					return messageError;
				}
			}

			// add feature type in product attributes, store feature types of virtual product
			List<String> featureTypeIds = (List<String>) context.get("featureTypeIds");
			if (UtilValidate.isNotEmpty(featureTypeIds)) {
				String featureTypeIdsStr = StringUtils.join(featureTypeIds, ";");
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "featureTypes");
				productCtx.put("attrValue", featureTypeIdsStr);
				productCtx.put("userLogin", userLogin);
				dispatcher.runSync("createProductAttribute", productCtx);
			}

			// add product to category
			Set<String> productCategoryIds = FastSet.newInstance();
			// check primary category
			String primaryProductCategoryId = (String) context.get("primaryProductCategoryId");
			if (UtilValidate.isNotEmpty(primaryProductCategoryId)) {
				productCategoryIds.add(primaryProductCategoryId);
			}
			// check list other category
			List<String> otherProductCategoryIds = (List<String>) context.get("productCategoryIds");
			if (UtilValidate.isNotEmpty(otherProductCategoryIds)) {
				productCategoryIds.addAll(otherProductCategoryIds);
			}
			if (productCategoryIds.size() > 0) {
				int sequenceNum = 1;
				for (String categoryId : productCategoryIds) {
					productCtx.clear();
					productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
					productCtx.put("productId", productId);
					productCtx.put("productCategoryId", categoryId);
					productCtx.put("sequenceNum", sequenceNum);
					productCtx.put("userLogin", userLogin);
					Map<String, Object> addToCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
					if (ServiceUtil.isError(addToCategoryResult)) {
						Debug.logError("Error occur when add product id = " + productId + " to product category = " + categoryId, module);
						continue;
					}
					sequenceNum++;
				}
				/* (extend) Check for update product
					List<EntityCondition> conds = new ArrayList<EntityCondition>();
					conds.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
					conds.add(EntityCondition.makeCondition("productId", productId));
					conds.add(EntityUtil.getFilterByDateExpr());
					List<String> listProductCategoryIdExists = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conds), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);

					int sequenceNum = 1;
					for (String categoryId : productCategoryIds) {
						if (!listProductCategoryIdExists.contains(categoryId)) {
							productCtx.clear();
							productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
							productCtx.put("productId", productId);
							productCtx.put("productCategoryId", categoryId);
							productCtx.put("sequenceNum", sequenceNum);
							productCtx.put("userLogin", userLogin);
							Map<String, Object> addToCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
							if (ServiceUtil.isError(addToCategoryResult)) {
								Debug.logError("Error occur when add product id = " + productId + " to product category = " + categoryId, module);
								continue;
							}
							sequenceNum++;
						}
					}
				 */
			}

			// add product to TAX category
			String taxProductCategoryId = (String) context.get("taxProductCategoryId");
			if (UtilValidate.isNotEmpty(taxProductCategoryId)) {
				productCtx.clear();
				productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
				productCtx.put("productId", productId);
				productCtx.put("productCategoryId", taxProductCategoryId);
				productCtx.put("sequenceNum", 1);
				productCtx.put("userLogin", userLogin);
				Map<String, Object> addToTaxCategoryResult = dispatcher.runSync("addProductToCategory", productCtx);
				if (ServiceUtil.isError(addToTaxCategoryResult)) {
					Debug.logError("Error occur when add product id = " + productId + " to product category = " + taxProductCategoryId, module);
				}
				/* (extend) Check for update product
					List<GenericValue> listProductCategoryMemberTax = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", context.get("taxCatalogs"), "productId", productId)),
						null, null, null, false);
					if (UtilValidate.isEmpty(listProductCategoryMemberTax)) {
					}
				*/
			}

			// add features to product
			if (!isVirtual.equals(isVariant)) {
				String productFeatureApplTypeId = "STANDARD_FEATURE";
				if (isVirtual.equals("Y")) {
					productFeatureApplTypeId = "SELECTABLE_FEATURE";

					if (UtilValidate.isNotEmpty(context.get("displayColor"))) {
						// create product attribute 'display color'
						String attrName = EntityUtilProperties.getPropertyValue("po.properties", "productAttrName.displayColor", delegator);
						if (UtilValidate.isNotEmpty(attrName)) {
							productCtx.clear();
							productCtx.put("productId", productId);
							productCtx.put("attrName", attrName);
							productCtx.put("attrValue", context.get("displayColor"));
							productCtx.put("userLogin", userLogin);
							dispatcher.runSync("createProductAttribute", productCtx);
						} else {
							Debug.logError("Not found attrName of attribute 'displayColor'", module);
						}
					}
				}
				String parentProductId = (String) context.get("parentProductId");
				if (isVariant.equals("Y")) {
					if (UtilValidate.isNotEmpty(parentProductId)) {
						productCtx.clear();
						productCtx.put("productIdTo", productId);
						productCtx.put("productId", parentProductId);
						productCtx.put("productAssocTypeId", "PRODUCT_VARIANT");
						productCtx.put("fromDate", new Timestamp(System.currentTimeMillis()));
						productCtx.put("userLogin", userLogin);
						dispatcher.runSync("createProductAssoc", productCtx);
					} else {
						throw new Exception();
					}
				}

				/*String feature = (String) context.get("feature");
				if (UtilValidate.isNotEmpty(feature)) {
					try {
						applyFeatureToProduct(delegator, dispatcher, feature, productId, productFeatureApplTypeId, userLogin);
					} catch (Exception e) {
						Debug.logError("Error occur when add feature = " + feature + " to product = " + productId, module);
					}
				}*/
				List<String> featureIds = (List<String>) context.get("featureIds");
				if (UtilValidate.isNotEmpty(featureIds)) {
					for (String featureId : featureIds) {
						productCtx.clear();
						productCtx.put("productId", productId);
						productCtx.put("productFeatureId", featureId);
						productCtx.put("productFeatureApplTypeId", productFeatureApplTypeId);
						productCtx.put("userLogin", userLogin);
						dispatcher.runSync("applyFeatureToProduct", productCtx);

						if (UtilValidate.isNotEmpty(parentProductId)) {
							// add feature to parent product
							List<EntityCondition> condsPF = new ArrayList<EntityCondition>();
							condsPF.add(EntityCondition.makeCondition("productId", parentProductId));
							condsPF.add(EntityCondition.makeCondition("productFeatureId", featureId));
							condsPF.add(EntityUtil.getFilterByDateExpr());
							List<GenericValue> parentProductFeature = delegator.findList("ProductFeatureAppl", EntityCondition.makeCondition(condsPF), null, null, null, false);
							if (UtilValidate.isEmpty(parentProductFeature)) {
								productCtx.clear();
								productCtx.put("productId", parentProductId);
								productCtx.put("productFeatureId", featureId);
								productCtx.put("productFeatureApplTypeId", "SELECTABLE_FEATURE");
								productCtx.put("userLogin", userLogin);
								dispatcher.runSync("applyFeatureToProduct", productCtx);
							}
						}
					}
				}
			}

			String taxInPrice = (String) context.get("taxInPrice");
			if (UtilValidate.isEmpty(taxInPrice)) taxInPrice = "N";

			String currencyUomId = (String) context.get("currencyUomId");
			boolean isPriceIncludedVat = "Y".equals((String) context.get("isPriceIncludedVat")) ? true : false;
			BigDecimal taxPercentage = null;
			if (isPriceIncludedVat) {
				/*List<EntityCondition> condsTax = FastList.newInstance();
				condsTax.add(EntityCondition.makeCondition("productId", productId));
				condsTax.add(EntityUtil.getFilterByDateExpr());
				GenericValue taxCategoryGV = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRateSimple", EntityCondition.makeCondition(condsTax), UtilMisc.toSet("taxPercentage"), null, null, false));
				if (taxCategoryGV != null) taxPercentage = taxCategoryGV.getBigDecimal("taxPercentage");*/
				Map<String, Object> productTax = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
				if (productTax != null) taxPercentage = (BigDecimal) productTax.get("taxPercentage");
			}

			// create product price with type is "DEFAULT_PRICE"
			//BigDecimal productDefaultPrice = (BigDecimal) context.get("productDefaultPrice");
			String productDefaultPriceStr = (String) context.get("productDefaultPrice");
			BigDecimal productDefaultPrice = null;
			if (UtilValidate.isNotEmpty(productDefaultPriceStr)) productDefaultPrice = new BigDecimal(productDefaultPriceStr);
			BigDecimal taxAmountDefaultPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountDefaultPrice"))) taxAmountDefaultPrice = new BigDecimal((String) context.get("taxAmountDefaultPrice"));

			// create product price with type is "LIST_PRICE"
			//BigDecimal productListPrice = (BigDecimal) context.get("productListPrice");
			String productListPriceStr = (String) context.get("productListPrice");
			BigDecimal productListPrice = null;
			if (UtilValidate.isNotEmpty(productListPriceStr)) productListPrice = new BigDecimal(productListPriceStr);
			BigDecimal taxAmountListPrice = null;
			if (UtilValidate.isNotEmpty(context.get("taxAmountListPrice"))) taxAmountListPrice = new BigDecimal((String) context.get("taxAmountListPrice"));

			// create product price
			Map<String, Object> resultProductPrice = ProductUtils.createOrStoreProductPrice(productId, currencyUomId, quantityUomId, taxInPrice, productDefaultPrice, productListPrice, taxAmountDefaultPrice, taxAmountListPrice, isPriceIncludedVat, taxPercentage, delegator, dispatcher, userLogin);
			if (ServiceUtil.isError(resultProductPrice)) {
				messageError.put("message",ServiceUtil.getErrorMessage(resultProductPrice));
				return messageError;
			}

			// create dayN
			if (UtilValidate.isNotEmpty(context.get("dayN"))) {
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "DAYN");
				productCtx.put("attrValue", context.get("dayN"));
				productCtx.put("userLogin", userLogin);
				dispatcher.runSync("createProductAttribute", productCtx);
			}

			// create shelf life of product
			if (UtilValidate.isNotEmpty(context.get("shelflife"))) {
				productCtx.clear();
				productCtx.put("productId", productId);
				productCtx.put("attrName", "SHELFLIFE");
				productCtx.put("attrValue", context.get("shelflife"));
				productCtx.put("userLogin", userLogin);
				dispatcher.runSync("createProductAttribute", productCtx);
			}

			// insert into price product for supplier
			if (UtilValidate.isNotEmpty(context.get("supplierProduct"))) {
				String supplierProduct = (String) context.get("supplierProduct");
				String checkSupplierReferenceStr = (String) context.get("checkSupplierReference");
				Boolean checkSupplierReference = "Y".equals(checkSupplierReferenceStr) ? true : false;
				Map<String, Object> resultSupplierProd = ProductUtils.createOrStoreSupplierProduct(delegator, dispatcher, userLogin, productId, supplierProduct, quantityUomId, checkSupplierReference, locale);
				if (ServiceUtil.isError(resultSupplierProd)) {
					messageError.put("message", ServiceUtil.getErrorMessage(resultSupplierProd));
					return messageError;
				}
			}

			// insert alternative UOM in product config packing
			if (UtilValidate.isNotEmpty(context.get("alterUomData"))) {
				String alterUomData = (String) context.get("alterUomData");
				Map<String, Object> resultAlterUom = ProductUtils.createOrStoreProductAlterUom(delegator, dispatcher, userLogin, locale, productId, quantityUomId, alterUomData, currencyUomId, isPriceIncludedVat, taxPercentage);
				if (ServiceUtil.isError(resultAlterUom)) {
					messageError.put("message", ServiceUtil.getErrorMessage(resultAlterUom));
					return messageError;
				}
			}

			// insert product configuration item
			if (UtilValidate.isNotEmpty(productConfigItem)) {
				Map<String, Object> resultProdConfigItem = ProductUtils.createOrStoreProductConfigItem(delegator, dispatcher, userLogin, locale, productId, productConfigItem);
				if (ServiceUtil.isError(resultProdConfigItem)) {
					messageError.put("message", ServiceUtil.getErrorMessage(resultProdConfigItem));
					return messageError;
				}
			}

			try {
				Map<String, Object> createImageResult = createOrUpdateProductImages(delegator, dispatcher, context, userLogin, productId);
				if (ServiceUtil.isError(createImageResult)) {
					Debug.logError(ServiceUtil.getErrorMessage(createImageResult), module);
				}
			} catch (Exception e) {
				Debug.logError(e, module);
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			messageError.put("message", UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
			return messageError;
		}
		result.put("productId", productId);
		return result;
	}
}
