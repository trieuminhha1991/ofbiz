package com.olbius.baseecommerce.backend;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.baseecommerce.backend.content.ContentWithWebSite;
import com.olbius.basehr.util.PartyHelper;
import com.olbius.basepo.product.ProductContentUtils;
import com.olbius.basesales.product.ProductServices;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.product.ProductUtils;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ConfigProductServices {

	public static List<GenericValue> listProducts(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.IN,
				productsInWebSite(delegator, userLogin)));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productTypeId", "FINISHED_GOOD", "isVariant", "N")));
		List<GenericValue> products = delegator.findList("Product", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productId", "internalName"), UtilMisc.toList("internalName"), null, false);
		return products;
	}

	public static List<GenericValue> listCategories(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN,
				categoriesInWebSite(delegator, userLogin)));
		List<GenericValue> categories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("categoryName"), null, false);
		return categories;
	}
	public static List<GenericValue> listRootCategories(Delegator delegator, GenericValue userLogin, Object prodCatalogId, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<String> productCategoryIds = getRootCategoryIds(delegator, userLogin, prodCatalogId, isEcommerceAdmin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN,
				productCategoryIds));
		List<GenericValue> categories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("categoryName"), null, false);
		return categories;
	}
	public static List<GenericValue> listRootCategories(Delegator delegator, GenericValue userLogin, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<String> productCategoryIds = getRootCategoryIds(delegator, userLogin, isEcommerceAdmin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN,
				productCategoryIds));
		List<GenericValue> categories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("categoryName"), null, false);
		return categories;
	}
	public static List<String> getRootCategoryIds(Delegator delegator, GenericValue userLogin, Object prodCatalogId, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<String> productCategoryIds = FastList.newInstance();
		List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isEmpty(prodCatalogId)) {
			prodCatalogIds = prodCatalogIds(delegator, userLogin, isEcommerceAdmin);
		} else {
			prodCatalogIds = UtilMisc.toList((String)prodCatalogId);
		}
		List<EntityCondition> conditions = FastList.newInstance();
		for (String s : prodCatalogIds) {
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("prodCatalogId", EntityJoinOperator.EQUALS, s));
			conditions.add(EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityJoinOperator.EQUALS, "PCCT_BROWSE_ROOT"));
			List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), UtilMisc.toList("sequenceNum"), null, false);
			if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
				productCategoryIds.add(EntityUtil.getFirst(prodCatalogCategories).getString("productCategoryId"));
			}
		}
		return productCategoryIds;
	}
	
	public static List<GenericValue> listMainCategories(Delegator delegator, GenericValue userLogin, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<String> productCategoryIds = getRootCategoryIds(delegator, userLogin, isEcommerceAdmin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityJoinOperator.IN,
				productCategoryIds));
		List<GenericValue> categories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("categoryName"), null, false);
		return categories;
	}
	
	public static List<GenericValue> listMainCategories(Delegator delegator, GenericValue userLogin, Object prodCatalogId, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<String> productCategoryIds = getRootCategoryIds(delegator, userLogin, prodCatalogId, isEcommerceAdmin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityJoinOperator.IN,
				productCategoryIds));
		List<GenericValue> categories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("categoryName"), null, false);
		return categories;
	}
	
	public static List<GenericValue> usableCategories(Delegator delegator, GenericValue userLogin, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<String> productCategoryIds = getRootCategoryIds(delegator, userLogin, isEcommerceAdmin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityJoinOperator.IN,
				productCategoryIds));
		conditions.add(EntityCondition.makeCondition("productCategoryTypeId", EntityJoinOperator.EQUALS,
				"CATALOG_CATEGORY"));
		List<GenericValue> categories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("categoryName"), null, false);
		return categories;
	}
	
	
	public static Map<String, Object> categoriesOfWebSite(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> categories = FastList.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			boolean isEcommerceAdmin = "true".equals(context.get("isEc"));
			String getAll = (String) context.get("getAll");
			List<GenericValue> categoryRollups = new ArrayList<GenericValue>();
			if ("Y".equals(getAll)) {
				List<GenericValue> listRootCategories = delegator.findList("ProductCategory", EntityCondition.makeCondition("primaryParentCategoryId", null), null, null, null, false);
				if (UtilValidate.isNotEmpty(listRootCategories)) {
					categoryRollups.addAll(listRootCategories);
					for (GenericValue x : listRootCategories) {
						categoryRollups.addAll(categoriesTree(delegator, x.getString("productCategoryId")));
					}
				}
			} else {
				categoryRollups = listProductCategoryRollups(delegator, userLogin, context.get("prodCatalogId"), isEcommerceAdmin);
			}
			List<String> categoryTypes = UtilMisc.toList("CATALOG_CATEGORY", "RECYCLE_CATEGORY");
			for (GenericValue x : categoryRollups) {
				String productCategoryId = x.getString("productCategoryId");
				GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
				String productCategoryTypeId = productCategory.getString("productCategoryTypeId");
				if (categoryTypes.contains(productCategoryTypeId)) {
					Map<String, Object> category = FastMap.newInstance();
					category.putAll(x);
					category.put("productCategoryTypeId", productCategoryTypeId);
					category.put("categoryName", productCategory.getString("categoryName"));
					category.put("longDescription", productCategory.getString("longDescription"));
					if ("Y".equals(getAll)) {
						// get catalog
						List<String> prodCatalogIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProdCatalogCategory", 
								EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", productCategory.get("productCategoryId")), 
										EntityOperator.AND, EntityUtil.getFilterByDateExpr()), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
						if (UtilValidate.isNotEmpty(prodCatalogIds)) {
							category.put("prodCatalogId", StringUtils.join(prodCatalogIds, "<br/>"));
						}
					}
					categories.add(category);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("categories", categories);
		return result;
	}
	private static List<GenericValue> listProductCategoryRollups(Delegator delegator, GenericValue userLogin, Object prodCatalogId, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<GenericValue> categories = FastList.newInstance();
		List<GenericValue> listRootCategories = listRootCategories(delegator, userLogin, prodCatalogId, isEcommerceAdmin);
		categories.addAll(listRootCategories);
		for (GenericValue x : listRootCategories) {
			categories.addAll(categoriesTree(delegator, x.getString("productCategoryId")));
		}
		return categories;
	}
	private static List<GenericValue> categoriesTree(Delegator delegator, String parentProductCategoryId)
			throws GenericEntityException {
		List<GenericValue> categories = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("parentProductCategoryId", EntityJoinOperator.EQUALS, parentProductCategoryId));
		List<GenericValue> currentBough = delegator.findList("ProductCategoryRollup",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("sequenceNum"), null, false);
		categories.addAll(currentBough);
		for (GenericValue x : currentBough) {
			categories.addAll(categoriesTree(delegator, x.getString("productCategoryId")));
		}
		return categories;
	}

	public static Map<String, Object> createProductCategoryWithWebSite(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		dispatcher.runSync("createProductCategoryAndRollup", context);
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listProducts = FastList.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.IN, productsInWebSite(delegator, userLogin)));
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productTypeId", "FINISHED_GOOD", "isVariant", "N")));
			List<GenericValue> listProductVirtual = delegator.findList("Product",
					EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			for (GenericValue x : listProductVirtual) {
				Map<String, Object> mapProduct = FastMap.newInstance();
				mapProduct.put("productId", x.getString("productId"));
				mapProduct.put("productCode", x.getString("productCode"));
				mapProduct.put("primaryProductCategoryId", x.getString("primaryProductCategoryId"));
				mapProduct.put("internalName", x.getString("internalName"));
				mapProduct.put("productName", x.getString("productName"));
				mapProduct.put("brandName", x.getString("brandName"));
				mapProduct.put("productWeight", x.getBigDecimal("productWeight"));
				mapProduct.put("weightUomId", x.getString("weightUomId"));
				mapProduct.put("quantityUomId", x.getString("quantityUomId"));
				mapProduct.put("description", x.getString("description"));
				mapProduct.put("isVirtual", x.getString("isVirtual"));
				mapProduct.put("taxCatalogs", ProductServices.getTaxCatalogs(delegator, x.getString("productId")));
				listProducts.add(mapProduct);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listProducts);
		return result;
	}

	private static List<String> prodCatalogIds(Delegator delegator, GenericValue userLogin, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<GenericValue> productStoreCatalogs = FastList.newInstance();
		if (isEcommerceAdmin) {
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			List<GenericValue> productStores = delegator.findList("ProductStore",
					EntityCondition.makeCondition("visualThemeId", EntityJoinOperator.EQUALS, webSiteId), UtilMisc.toSet("productStoreId"), null, null, false);
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(productStores, "productStoreId", true);
			productStoreCatalogs = delegator.findList("ProductStoreCatalog",
					EntityCondition.makeCondition("productStoreId", EntityJoinOperator.IN, productStoreIds),
					UtilMisc.toSet("prodCatalogId"), null, null, false);
		} else {
			productStoreCatalogs = delegator.findList("ProdCatalog",
					null, UtilMisc.toSet("prodCatalogId"), null, null, false);
		}
		List<String> prodCatalogIds = EntityUtil.getFieldListFromEntityList(productStoreCatalogs, "prodCatalogId", true);
		return prodCatalogIds;
	}

	private static List<String> categoriesInWebSite(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		List<String> productCategoryIds = getRootCategoryIds(delegator, userLogin, true);
		List<String> productCategories = FastList.newInstance();
		productCategories.addAll(productCategoryIds);
		for (String s : productCategoryIds) {
			productCategories.addAll(productCategoryChild(delegator, s));
		}
		return productCategories;
	}
	public static List<String> getRootCategoryIds(Delegator delegator, GenericValue userLogin, boolean isEcommerceAdmin)
			throws GenericEntityException {
		List<String> productCategoryIds = FastList.newInstance();
		List<String> prodCatalogIds = prodCatalogIds(delegator, userLogin, isEcommerceAdmin);
		List<EntityCondition> conditions = FastList.newInstance();
		for (String s : prodCatalogIds) {
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("prodCatalogId", EntityJoinOperator.EQUALS, s));
			conditions.add(EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityJoinOperator.EQUALS,
					"PCCT_BROWSE_ROOT"));
			List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), UtilMisc.toList("sequenceNum"), null, false);
			if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
				productCategoryIds.add(EntityUtil.getFirst(prodCatalogCategories).getString("productCategoryId"));
			}
		}
		return productCategoryIds;
	}

	private static List<String> productsInWebSite(Delegator delegator, GenericValue userLogin)
			throws GenericEntityException {
		List<String> productCategories = categoriesInWebSite(delegator, userLogin);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, productCategories));
		List<GenericValue> productCategoryMembers = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("productId"), null, null, false);
		List<String> productIds = EntityUtil.getFieldListFromEntityList(productCategoryMembers, "productId", true);
		return productIds;
	}

	private static List<String> productCategoryChild(Delegator delegator, String parentProductCategoryId)
			throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("parentProductCategoryId", EntityJoinOperator.EQUALS,
				parentProductCategoryId));

		List<GenericValue> productCategoryRollups = delegator.findList("ProductCategoryRollup",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), null, null, false);
		List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategoryRollups, "productCategoryId", true);
		List<String> productCategories = FastList.newInstance();
		productCategories.addAll(productCategoryIds);
		for (String s : productCategoryIds) {
			productCategories.addAll(productCategoryChild(delegator, s));
		}
		return productCategories;
	}

	public static Map<String, Object> configOfProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> config = FastMap.newInstance();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String productId = (String) context.get("productId");
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (UtilValidate.isNotEmpty(product)) {
				config.put("productId", product.getString("productId"));
				config.put("productCode", product.getString("productCode"));
				config.put("internalName", product.getString("internalName"));
				config.put("smallImageUrl", product.getString("smallImageUrl"));
				config.put("mediumImageUrl", product.getString("mediumImageUrl"));
				config.put("largeImageUrl", product.getString("largeImageUrl"));
				config.put("detailImageUrl", product.getString("detailImageUrl"));
				config.put("originalImageUrl", product.getString("originalImageUrl"));
				config.putAll(ProductContentUtils.loadProductSpecifications(delegator, webSiteId, productId));
				//	get additional images
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("config", config);
		return result;
	}

	public static Map<String, Object> configProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			String productId = (String) context.get("productId");
			String largeImageUrl = (String) context.get("largeImageUrl");
			String smallImageUrl = (String) context.get("smallImageUrl");
			Map<String, Object> mapUpdateProduct = FastMap.newInstance();
			mapUpdateProduct.put("productId", productId);
			mapUpdateProduct.put("userLogin", context.get("userLogin"));
			if (UtilValidate.isNotEmpty(largeImageUrl)) {
				mapUpdateProduct.put("largeImageUrl", largeImageUrl);
			}
			if (UtilValidate.isNotEmpty(smallImageUrl)) {
				mapUpdateProduct.put("smallImageUrl", smallImageUrl);
			}
			dispatcher.runSync("updateProduct", mapUpdateProduct);
			updateProductSpecifications(ctx, context);
			updateProductAdditionalImage(ctx, context);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static void updateProductSpecifications(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		String productId = (String) context.get("productId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// update Effects of product
		updateProductContent(ctx, userLogin, (String) context.get("effects"),
				(String) context.get("effectsId"), productId, "EFFECTS");
		// update COMPOSITION of product
		updateProductContent(ctx, userLogin, (String) context.get("composition"),
				(String) context.get("compositionId"), productId, "COMPOSITION");
		// update SHELFLIFE of product
		updateProductContent(ctx, userLogin, (String) context.get("shelfLife"),
				(String) context.get("shelfLifeId"), productId, "SHELFLIFE");
		// update USERS of product
		updateProductContent(ctx, userLogin, (String) context.get("users"),
				(String) context.get("usersId"), productId, "USERS");
		// update INSTRUCTIONS of product
		updateProductContent(ctx, userLogin, (String) context.get("instructions"),
				(String) context.get("instructionsId"), productId, "INSTRUCTIONS");
		// update LICENSE of product
		updateProductContent(ctx, userLogin, (String) context.get("license"),
				(String) context.get("licenseId"), productId, "LICENSE");
		// update PACKING of product
		updateProductContent(ctx, userLogin, (String) context.get("packing"),
				(String) context.get("packingId"), productId, "PACKING");
		// update CONTRAINDICATIONS of product
		updateProductContent(ctx, userLogin, (String) context.get("contraindications"),
				(String) context.get("contraindicationsId"), productId, "CONTRAINDICATIONS");
	}
	public static void updateProductContent(DispatchContext ctx, GenericValue userLogin, String longDescription, String contentId, String productId, String productContentTypeId)
			throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isNotEmpty(contentId)) {
			Delegator delegator = ctx.getDelegator();
			GenericValue content = delegator.makeValidValue("Content",
					UtilMisc.toMap("contentId", contentId, "longDescription", longDescription));
			delegator.store(content);
		} else {
			createProductContent(ctx, userLogin, null, longDescription, productId, productContentTypeId);
		}
	}
	public static void createProductContent(DispatchContext ctx, GenericValue userLogin, String description, String longDescription, String productId, String productContentTypeId)
			throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String contentId = "PRC" + delegator.getNextSeqId("Content");
		ContentWithWebSite.create(dispatcher, delegator, userLogin,
				UtilMisc.toMap("contentId", contentId, "contentTypeId", "DOCUMENT", "longDescription", longDescription,
						"description", description, "statusId", "CTNT_PUBLISHED", "createdDate", new Timestamp(System.currentTimeMillis())));
		
		GenericValue productContent = delegator.makeValidValue("ProductContent",
				UtilMisc.toMap("productId", productId, "contentId", contentId, "productContentTypeId", productContentTypeId,
						"fromDate", new Timestamp(System.currentTimeMillis())));
		delegator.create(productContent);
	}
	
	public static Map<String, Object> updateProductAdditionalImage(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		String productId = (String) context.get("productId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// update ADDITIONAL_IMAGE_1 of product
		updateProductAdditionalImage(ctx, userLogin, (String) context.get("ADDITIONAL_IMAGE_1Id"),
				(String) context.get("ADDITIONAL_IMAGE_1"), productId, "ADDITIONAL_IMAGE_1", "XTRA_IMG_1_LARGE");
		// update ADDITIONAL_IMAGE_2 of product
		updateProductAdditionalImage(ctx, userLogin, (String) context.get("ADDITIONAL_IMAGE_2Id"),
				(String) context.get("ADDITIONAL_IMAGE_2"), productId, "ADDITIONAL_IMAGE_2", "XTRA_IMG_2_LARGE");
		// update ADDITIONAL_IMAGE_3 of product
		updateProductAdditionalImage(ctx, userLogin, (String) context.get("ADDITIONAL_IMAGE_3Id"),
				(String) context.get("ADDITIONAL_IMAGE_3"), productId, "ADDITIONAL_IMAGE_3", "XTRA_IMG_3_LARGE");
		// update ADDITIONAL_IMAGE_4 of product
		updateProductAdditionalImage(ctx, userLogin, (String) context.get("ADDITIONAL_IMAGE_4Id"),
				(String) context.get("ADDITIONAL_IMAGE_4"), productId, "ADDITIONAL_IMAGE_4", "XTRA_IMG_4_LARGE");
		
		return successResult;
	}
	public static void updateProductAdditionalImage(DispatchContext ctx, GenericValue userLogin, String dataResourceId, String objectInfo, String productId,
			String productContentTypeId, String productContentTypeId2)
					throws GenericServiceException, GenericEntityException {
		if (UtilValidate.isNotEmpty(dataResourceId)) {
			if (UtilValidate.isNotEmpty(objectInfo)) {
				LocalDispatcher dispatcher = ctx.getDispatcher();
				//	updateDataResource
				dispatcher.runSync("updateDataResource", UtilMisc.toMap("dataResourceId", dataResourceId, "objectInfo", objectInfo, "userLogin", userLogin));
			}
		} else {
			createProductAdditionalImage(ctx, userLogin, objectInfo, productId, productContentTypeId, productContentTypeId2);
		}
	}
	public static void createProductAdditionalImage(DispatchContext ctx, GenericValue userLogin, String objectInfo, String productId,
			String productContentTypeId, String productContentTypeId2)
					throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		//	createDataResource
		Map<String, Object> createDataResource = dispatcher.runSync("createDataResource",
				UtilMisc.toMap("localeString", "vi", "dataResourceTypeId", "LINK", "objectInfo", objectInfo, "dataTemplateTypeId", "NONE",
						"statusId", "CTNT_PUBLISHED", "dataResourceName", "Image product", "mimeTypeId", "text/xml", "isPublic", "Y",
						"userLogin", userLogin));
		String dataResourceId = (String) createDataResource.get("dataResourceId");
		String contentId = "PRIM" + delegator.getNextSeqId("Content");
		ContentWithWebSite.create(dispatcher, delegator, userLogin,
				UtilMisc.toMap("contentId", contentId, "contentTypeId", "DOCUMENT", "dataResourceId", dataResourceId, 
						"statusId", "CTNT_PUBLISHED", "createdDate", new Timestamp(System.currentTimeMillis())));
		GenericValue productContent = delegator.makeValidValue("ProductContent",
				UtilMisc.toMap("productId", productId, "contentId", contentId, "productContentTypeId", productContentTypeId,
						"fromDate", new Timestamp(System.currentTimeMillis())));
		delegator.create(productContent);
		GenericValue productContent2 = delegator.makeValidValue("ProductContent",
				UtilMisc.toMap("productId", productId, "contentId", contentId, "productContentTypeId", productContentTypeId2,
						"fromDate", new Timestamp(System.currentTimeMillis())));
		delegator.create(productContent2);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductContent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("productId")) {
				if (UtilValidate.isNotEmpty(parameters.get("productId")[0])) {
					listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", parameters.get("productId")[0])));
				}
			}
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			listAllConditions.add(EntityCondition.makeCondition("productContentTypeId", EntityJoinOperator.IN,
					UtilMisc.toList("RELATED_ARTICLE", "INTRODUCTION")));
			listAllConditions.add(EntityCondition.makeCondition("webSiteId", EntityJoinOperator.EQUALS, webSiteId));
			listIterator = delegator.find("ProductAndContent",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> saveContentProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			Locale locale = (Locale) context.get("locale");
			String productId = (String) context.get("productId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String productContentTypeId = (String) context.get("productContentTypeId");
			String contentName = (String) context.get("contentName");
			String description = (String) context.get("description");
			String longDescription = (String) context.get("editor");
			String contentId = "PR" + delegator.getNextSeqId("Content");
			if (UtilValidate.isEmpty(productContentTypeId)) {
				productContentTypeId = "RELATED_ARTICLE";
			}
			// createContent
			String originalImageUrl = ContentServices.uploadFile(dispatcher, context.get("userLogin"),
					context.get("titleImage"));
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "RELATED_ARTICLE", "contentName",
							contentName, "author", userLoginId, "description", description, "longDescription",
							longDescription, "statusId", "CTNT_DEACTIVATED", "createdDate",
							new Timestamp(System.currentTimeMillis()), "createdByUserLogin", userLoginId,
							"originalImageUrl", originalImageUrl, "productId", productId, "productContentTypeId",
							productContentTypeId, "userLogin", context.get("userLogin")));
			// createProductContent
			dispatcher.runSync("createProductContent", UtilMisc.toMap("productId", productId, "contentId", contentId,
					"productContentTypeId", productContentTypeId, "userLogin", context.get("userLogin")));
			result = ServiceUtil.returnSuccess(UtilProperties.getMessage("EcommerceBackendUiLabels", "BSCreateContentSuccess", locale));
			result.put("productId", productId);
			result.put("type", context.get("type"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> updateContentProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			Locale locale = (Locale) context.get("locale");
			String productId = (String) context.get("productId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			String contentId = (String) context.get("contentId");
			String contentName = (String) context.get("contentName");
			String description = (String) context.get("description");
			String longDescription = (String) context.get("editor");
			// updateContent
			GenericValue content = delegator.makeValidValue("Content",
					UtilMisc.toMap("contentId", contentId, "contentName", contentName, "author", userLoginId,
							"description", description, "longDescription", longDescription, "lastModifiedDate",
							new Timestamp(System.currentTimeMillis()), "lastModifiedByUserLogin", userLoginId));
			String originalImageUrl = ContentServices.uploadFile(dispatcher, context.get("userLogin"),
					context.get("titleImage"));
			if (UtilValidate.isNotEmpty(originalImageUrl)) {
				content.set("originalImageUrl", originalImageUrl);
			}
			delegator.store(content);
			result = ServiceUtil.returnSuccess(UtilProperties.getMessage("EcommerceBackendUiLabels", "BSUpdateContentSuccess", locale));
			result.put("productId", productId);
			result.put("type", context.get("type"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static List<Map<String, Object>> contentByProductId(Delegator delegator, Security security, Locale locale,
			String contentId, List<String> orderBy, boolean isComment)
					throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", contentId, "productContentTypeId", "COMMENT")));
		List<GenericValue> contentAssocs = delegator.findList("ProductContent",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId"), null, null, false);
		List<String> contentIds = EntityUtil.getFieldListFromEntityList(contentAssocs, "contentId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("contentId", EntityJoinOperator.IN, contentIds));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", "COMMENT")));
		List<GenericValue> listContent = delegator.findList("Content", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("contentId", "contentName", "longDescription", "statusId", "createdStamp", "author"),
				orderBy, null, false);
		List<Map<String, Object>> contents = FastList.newInstance();
		for (GenericValue x : listContent) {
			Map<String, Object> content = FastMap.newInstance();
			content.putAll(x);
			content.put("createdStamp", ContentUtils.getTimeAgo(locale, x.getTimestamp("createdStamp")));
			String author = x.getString("author");
			if (UtilValidate.isNotEmpty(author)) {
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", author), false);
				if (UtilValidate.isNotEmpty(userLogin)) {
					if (security.hasPermission("ECOMMERCE_ADMIN", userLogin)) {
						content.put("partyRole",
								UtilProperties.getMessage("DpcEcommerceBackendUiLabels", "BSAdmin", locale));
					}
				}
			}
			if (isComment) {
				content.put("numberOfReplies", ContentServices.numberOfReplies(delegator, x.getString("contentId")));
			}
			contents.add(content);
		}
		return contents;
	}

	public static List<Map<String, Object>> commentsByProductId(Delegator delegator, Security security, Locale locale, String contentId, List<String> orderBy)
					throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", contentId, "productContentTypeId", "COMMENT")));
		List<GenericValue> contentAssocs = delegator.findList("ProductContent",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId"), null, null, false);
		List<String> contentIds = EntityUtil.getFieldListFromEntityList(contentAssocs, "contentId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("contentId", EntityJoinOperator.IN, contentIds));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("statusId", "CTNT_PUBLISHED", "contentTypeId", "COMMENT")));
		List<GenericValue> contents = delegator.findList("Content", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("contentId", "contentName", "longDescription", "createdStamp", "author"), orderBy, null, false);
		List<Map<String, Object>> comments = FastList.newInstance();
		for (GenericValue x : contents) {
			Map<String, Object> comment = FastMap.newInstance();
			comment.putAll(x);
			comment.put("createdStamp", ContentUtils.getTimeAgo(locale, x.getTimestamp("createdStamp")));
			String author = x.getString("author");
			if (UtilValidate.isNotEmpty(author)) {
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", author), false);
				if (security.hasPermission("ECOMMERCE_ADMIN", userLogin)) {
					comment.put("partyRole",
							UtilProperties.getMessage("DpcEcommerceBackendUiLabels", "BSAdmin", locale));
					comment.put("author", PartyHelper.getPartyName(delegator, author, true, true));
				}
			}
			comments.add(comment);
		}
		return comments;
	}

	public static Map<String, Object> loadProductIntroduction(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue productContent = new GenericValue();
		try {
			String productId = (String) context.get("productId");
			String contentId = (String) context.get("contentId");
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isEmpty(contentId)) {
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productContentTypeId",
						"INTRODUCTION", "statusId", "CTNT_PUBLISHED")));
			} else {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "contentId", contentId,
						"productContentTypeId", "INTRODUCTION")));
			}
			List<GenericValue> productContents = delegator.findList("ProductAndContent",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(productContents)) {
				productContent = EntityUtil.getFirst(productContents);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("productIntroduction", productContent);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCategoriesOfProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("productId")) {
				if (UtilValidate.isNotEmpty(parameters.get("productId")[0])) {
					String productId = parameters.get("productId")[0];
					List<EntityCondition> conditions = FastList.newInstance();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition("productId", productId));
					List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
							EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), null, null,
							false);
					if (UtilValidate.isNotEmpty(productCategoryMember)) {
						List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategoryMember,
								"productCategoryId", true);
						listAllConditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN,
								productCategoryIds));
						listAllConditions.add(EntityCondition.makeCondition("productCategoryTypeId",
								EntityJoinOperator.EQUALS, "CATALOG_CATEGORY"));
						List<GenericValue> productCategories = delegator.findList("ProductCategory",
								EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
						for (GenericValue x : productCategories) {
							Map<String, Object> productCategory = FastMap.newInstance();
							productCategory.putAll(x);
							String productCategoryId = x.getString("productCategoryId");
							productCategory.put("productId", productId);
							productCategory.putAll(isBestSell(delegator, productId, productCategoryId));
							listIterator.add(productCategory);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> isBestSell(Delegator delegator, String productId, String productCategoryId)
			throws GenericEntityException {
		Map<String, Object> bestSell = FastMap.newInstance();
		String productCategoryId_BSL = productCategoryId.toUpperCase() + "_BSL";
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId_BSL)));
		List<GenericValue> productCategoryMembers = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isBestSell", true);
			GenericValue productCategoryMember = EntityUtil.getFirst(productCategoryMembers);
			bestSell.put("bestSellFromDate", productCategoryMember.getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isBestSell", false);
		}
		conditions.clear();
		String productCategoryId_PROMOS = productCategoryId.toUpperCase() + "_PROMOS";
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId_PROMOS)));
		productCategoryMembers = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isPromos", true);
			GenericValue productCategoryMember = EntityUtil.getFirst(productCategoryMembers);
			bestSell.put("promosFromDate", productCategoryMember.getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isPromos", false);
		}
		conditions.clear();
		String productCategoryId_NEW = productCategoryId.toUpperCase() + "_NEW";
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId_NEW)));
		productCategoryMembers = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isNew", true);
			GenericValue productCategoryMember = EntityUtil.getFirst(productCategoryMembers);
			bestSell.put("newFromDate", productCategoryMember.getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isNew", false);
		}
		conditions.clear();
		String productCategoryId_FEATURED = productCategoryId.toUpperCase() + "_FEATURED";
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId_FEATURED)));
		productCategoryMembers = delegator.findList("ProductCategoryMember",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("fromDate"), null, null, false);
		if (UtilValidate.isNotEmpty(productCategoryMembers)) {
			bestSell.put("isFeatured", true);
			GenericValue productCategoryMember = EntityUtil.getFirst(productCategoryMembers);
			bestSell.put("featuredFromDate", productCategoryMember.getTimestamp("fromDate").getTime());
		} else {
			bestSell.put("isFeatured", false);
		}
		return bestSell;
	}

	public static Map<String, Object> configCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String productId = (String) context.get("productId");
			String productCategoryId = (String) context.get("productCategoryId");
			String isBestSell = (String) context.get("isBestSell");
			String isPromos = (String) context.get("isPromos");
			String isNew = (String) context.get("isNew");
			String isFeatured = (String) context.get("isFeatured");
			Long bestSellFromDateL = (Long) context.get("bestSellFromDate");
			Long promosFromDateL = (Long) context.get("promosFromDate");
			Long newFromDateL = (Long) context.get("newFromDate");
			Long featuredFromDate = (Long) context.get("featuredFromDate");

			Timestamp bestSellFromDate = null;
			Timestamp promosFromDate = null;
			Timestamp newFromDate = null;
			if (UtilValidate.isNotEmpty(bestSellFromDateL)) {
				bestSellFromDate = new Timestamp(bestSellFromDateL);
			}
			if (UtilValidate.isNotEmpty(promosFromDateL)) {
				promosFromDate = new Timestamp(promosFromDateL);
			}
			if (UtilValidate.isNotEmpty(newFromDateL)) {
				newFromDate = new Timestamp(newFromDateL);
			}
			List<EntityCondition> conditions = FastList.newInstance();
			String productCategoryId_BSL = productCategoryId.toUpperCase() + "_BSL";
			if ("true".equals(isBestSell) && UtilValidate.isEmpty(bestSellFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_BSL, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_BSL, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isBestSell) && UtilValidate.isNotEmpty(bestSellFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_BSL,
									"productId", productId, "fromDate", bestSellFromDate)));
				}
			}
			String productCategoryId_PROMOS = productCategoryId.toUpperCase() + "_PROMOS";
			if ("true".equals(isPromos) && UtilValidate.isEmpty(promosFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_PROMOS, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_PROMOS, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isPromos) && UtilValidate.isNotEmpty(promosFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_PROMOS,
									"productId", productId, "fromDate", promosFromDate)));
				}
			}
			String productCategoryId_NEW = productCategoryId.toUpperCase() + "_NEW";
			if ("true".equals(isNew) && UtilValidate.isEmpty(newFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_NEW, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_NEW, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isNew) && UtilValidate.isNotEmpty(newFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_NEW,
									"productId", productId, "fromDate", newFromDate)));
				}
			}
			String productCategoryId_FEATURED = productCategoryId.toUpperCase() + "_FEATURED";
			if ("true".equals(isFeatured) && UtilValidate.isEmpty(featuredFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_FEATURED, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_FEATURED, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isFeatured) && UtilValidate.isNotEmpty(featuredFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_FEATURED,
									"productId", productId, "fromDate", featuredFromDate)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> fixCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<GenericValue> productCategories = delegator.findList("ProductCategory",
					EntityCondition.makeCondition("productCategoryTypeId", EntityJoinOperator.EQUALS,
							"CATALOG_CATEGORY"),
					UtilMisc.toSet("productCategoryId", "categoryName"), null, null, false);
			for (GenericValue x : productCategories) {
				checkProductCategoryExists(dispatcher, delegator, userLogin, x.getString("productCategoryId"),
						x.getString("categoryName"));
			}
			Security security = ctx.getSecurity();
			if (security.hasPermission("ECOMMERCE_ADMIN", userLogin)) {
				// fix TopicContent
				List<GenericValue> contentCategories = delegator.findList("ContentCategory",
						EntityCondition.makeCondition("contentCategoryTypeId", EntityJoinOperator.EQUALS, "ARTICLE"),
						UtilMisc.toSet("contentCategoryId", "categoryName"), null, null, false);
				for (GenericValue x : contentCategories) {
					checkContentTypeExists(dispatcher, delegator, userLogin, x.getString("contentCategoryId"),
							x.getString("categoryName"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	private static void checkContentTypeExists(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			String contentCategoryId, String categoryName)
					throws GenericEntityException, GenericServiceException {
		String contentCategoryId_HOT = contentCategoryId.toUpperCase() + "_HOT";
		GenericValue contentCategory_HOT = delegator.findOne("ContentCategory",
				UtilMisc.toMap("contentCategoryId", contentCategoryId_HOT, "contentCategoryTypeId", "HOT_ARTICLE"), false);
		if (UtilValidate.isEmpty(contentCategory_HOT)) {
			//	createContentType
			dispatcher.runSync("createContentCategory", UtilMisc.toMap("contentCategoryId", contentCategoryId_HOT,
					"contentCategoryTypeId", "HOT_ARTICLE", "categoryName", categoryName, "userLogin", userLogin));
		}
	}

	private static void checkProductCategoryExists(LocalDispatcher dispatcher, Delegator delegator,
			GenericValue userLogin, String productCategoryId, String categoryName)
					throws GenericEntityException, GenericServiceException {
		String productCategoryId_BSL = productCategoryId.toUpperCase() + "_BSL";
		GenericValue productCategory_BSL = delegator.findOne("ProductCategory",
				UtilMisc.toMap("productCategoryId", productCategoryId_BSL), false);
		if (UtilValidate.isEmpty(productCategory_BSL)) {
			// createProductCategory
			dispatcher.runSync("createProductCategory",
					UtilMisc.toMap("productCategoryId", productCategoryId_BSL, "productCategoryTypeId",
							"BEST_SELL_CATEGORY", "categoryName", categoryName, "userLogin", userLogin));
		}
		String productCategoryId_PROMOS = productCategoryId.toUpperCase() + "_PROMOS";
		GenericValue productCategory_PROMOS = delegator.findOne("ProductCategory",
				UtilMisc.toMap("productCategoryId", productCategoryId_PROMOS), false);
		if (UtilValidate.isEmpty(productCategory_PROMOS)) {
			// createProductCategory
			dispatcher.runSync("createProductCategory",
					UtilMisc.toMap("productCategoryId", productCategoryId_PROMOS, "productCategoryTypeId",
							"BEST_SELL_CATEGORY", "categoryName", categoryName, "userLogin", userLogin));
		}
		String productCategoryId_NEW = productCategoryId.toUpperCase() + "_NEW";
		GenericValue productCategory_NEW = delegator.findOne("ProductCategory",
				UtilMisc.toMap("productCategoryId", productCategoryId_NEW), false);
		if (UtilValidate.isEmpty(productCategory_NEW)) {
			// createProductCategory
			dispatcher.runSync("createProductCategory",
					UtilMisc.toMap("productCategoryId", productCategoryId_NEW, "productCategoryTypeId",
							"BEST_SELL_CATEGORY", "categoryName", categoryName, "userLogin", userLogin));
		}
		String productCategoryId_FEATURED = productCategoryId.toUpperCase() + "_FEATURED";
		GenericValue productCategory_FEATURED = delegator.findOne("ProductCategory",
				UtilMisc.toMap("productCategoryId", productCategoryId_FEATURED), false);
		if (UtilValidate.isEmpty(productCategory_FEATURED)) {
			// createProductCategory
			dispatcher.runSync("createProductCategory",
					UtilMisc.toMap("productCategoryId", productCategoryId_FEATURED, "productCategoryTypeId",
							"BEST_SELL_CATEGORY", "categoryName", categoryName, "userLogin", userLogin));
		}
	}

	public static Map<String, Object> slideOfProduct(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String productId = (String) context.get("productId");
		String type = (String) context.get("type");
		List<GenericValue> listSlide = FastList.newInstance();
		if ("preview".equals(type)) {
			listSlide = ContentUtils.slideOfProduct(delegator, productId, false);
		} else {
			listSlide = ContentUtils.slideOfProduct(delegator, productId, true);
		}
		result.put("listSlide", listSlide);
		return result;
	}

	public static Map<String, Object> addImageToSlideOfProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = ctx.getDelegator();
		try {
			String productId = (String) context.get("productId");
			String originalImageUrl = (String) context.get("originalImageUrl");
			String description = (String) context.get("description");
			createProductSlide(delegator, userLogin, description, originalImageUrl, productId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static void createProductSlide(Delegator delegator, GenericValue userLogin, String description, String originalImageUrl, String productId)
			throws GenericEntityException, GenericServiceException {
		String contentId = "CMT" + delegator.getNextSeqId("Content");
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
		ContentWithWebSite.create(delegator, webSiteId,
				UtilMisc.toMap("contentId", contentId, "contentTypeId", "SLIDE", "originalImageUrl", originalImageUrl,
						"description", description, "statusId", "CTNT_PUBLISHED", "createdDate", new Timestamp(System.currentTimeMillis())));
		
		GenericValue productContent = delegator.makeValidValue("ProductContent",
				UtilMisc.toMap("productId", productId, "contentId", contentId, "productContentTypeId", "SLIDE",
						"fromDate", new Timestamp(System.currentTimeMillis())));
		delegator.create(productContent);
	}

	public static Map<String, Object> getMainCategoriesByPrimaryParentCategoryId(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String productCategoryId = (String) context.get("productCategoryId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
		conditions.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityJoinOperator.EQUALS, productCategoryId));
		List<GenericValue> productCategories = delegator.findList("ProductCategory",
				EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName", "primaryParentCategoryId"),
				UtilMisc.toList("categoryName"), null, false);
		result.put("categories", productCategories);
		return result;
	}

	@SuppressWarnings("unused")
	private static List<GenericValue> productCategoryChild(Delegator delegator, GenericValue parentProductCategory)
			throws GenericEntityException {
		String parentProductCategoryId = parentProductCategory.getString("productCategoryId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("parentProductCategoryId", EntityJoinOperator.EQUALS,
				parentProductCategoryId));
		List<GenericValue> productCategoryRollups = delegator.findList("ProductCategoryRollup",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), null, null, false);
		List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategoryRollups, "productCategoryId", true);
		conditions.clear();
		conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, productCategoryIds));
		List<GenericValue> productCategories = delegator.findList("ProductCategory",
				EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName", "primaryParentCategoryId"),
				UtilMisc.toList("categoryName"), null, false);
		List<GenericValue> categories = FastList.newInstance();
		categories.addAll(productCategories);
		for (GenericValue x : productCategories) {
			categories.addAll(productCategoryChild(delegator, x));
		}
		return categories;
	}

	public static Map<String, Object> getCategoriesByProdCatalogId(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String prodCatalogId = (String) context.get("prodCatalogId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("prodCatalogId", prodCatalogId));
			List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("sequenceNum"), null, false);
			List<Map<String, Object>> categories = FastList.newInstance();
			for (GenericValue x : prodCatalogCategories) {
				Map<String, Object> categorie = FastMap.newInstance();
				categorie.putAll(x);
				GenericValue category = x.getRelatedOne("ProductCategory", false);
				categorie.put("categoryName", category.getString("categoryName"));
				categories.add(categorie);
			}
			result.put("categories", categories);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> removeProductCategoriesFromProdCatalog(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", context.get("prodCatalogId"),
					"productCategoryId", context.get("productCategoryId"), "prodCatalogCategoryTypeId",
					context.get("prodCatalogCategoryTypeId"))));
			List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : prodCatalogCategories) {
				x.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> getConfigCategory(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productCategoryId = (String) context.get("productCategoryId");
		String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);

		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		if (UtilValidate.isNotEmpty(productCategoryId)) {
			conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.EQUALS, productCategoryId));
		}
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("webSiteId", webSiteId, "prodCatContentTypeId", "CATEGORY_IMAGE")));
		List<GenericValue> listBanners = delegator.findList("ProductCategoryContentDetail",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		result.put("listBanners", listBanners);
		return result;
	}

	public static Map<String, Object> addBannerCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");

			String productCategoryId = (String) context.get("productCategoryId");
			String originalImageUrl = (String) context.get("originalImageUrl");
			String url = (String) context.get("url");
			String contentId = "CTGR_" + delegator.getNextSeqId("Content");
			// create Content
			ContentWithWebSite.create(dispatcher, delegator, userLogin,
					UtilMisc.toMap("contentId", contentId, "contentTypeId", "IMAGE_FRAME", "contentName",
							"Vertical Category Banner", "author", userLoginId, "url", url,
							"originalImageUrl", originalImageUrl, "statusId", "CTNT_PUBLISHED", "createdDate",
							new Timestamp(System.currentTimeMillis()), "createdByUserLogin", userLoginId));
			// createCategoryContent
			dispatcher.runSync("createCategoryContent", UtilMisc.toMap("productCategoryId", productCategoryId,
					"contentId", contentId, "prodCatContentTypeId", "CATEGORY_IMAGE", "userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> getProductByProductCategoryIdIncludeChild(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listProductByProductCategoryId = FastList.newInstance();
		try {
			String productCategoryId = (String) context.get("productCategoryId");
			List<String> productCategoryIds = childCategories(delegator, productCategoryId);
			productCategoryIds.add(productCategoryId);
			List<EntityCondition> listCondition = FastList.newInstance();
			listCondition.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, productCategoryIds));
			listCondition.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(listCondition), null, UtilMisc.toList("productCategoryId", "sequenceNum"), null, false);
			for (GenericValue x : listProductCategoryMember) {
				String productId = x.getString("productId");
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				Map<String, Object> mapProduct = FastMap.newInstance();
				mapProduct.put("productCategoryId", x.getString("productCategoryId"));
				mapProduct.put("productId", x.get("productId"));
				mapProduct.put("productCode", product.get("productCode"));
				mapProduct.put("fromDate", x.get("fromDate"));
				mapProduct.put("sequenceNum", x.get("sequenceNum"));
				mapProduct.put("internalName", product.get("internalName"));
				mapProduct.put("productName", product.get("productName"));
				mapProduct.put("primaryProductCategoryId", product.get("primaryProductCategoryId"));
				mapProduct.put("mainCategoryId", productCategoryId);
				mapProduct.putAll(ConfigProductServices.isBestSell(delegator, productId, productCategoryId));
				listProductByProductCategoryId.add(mapProduct);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		result.put("listProductByProductCategoryId", listProductByProductCategoryId);
		return result;
	}

	private static List<String> childCategories(Delegator delegator, String parentProductCategoryId)
			throws GenericEntityException {
		List<String> productCategoryIds = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("parentProductCategoryId", parentProductCategoryId)));
		List<GenericValue> productCategoryRollups = delegator.findList("ProductCategoryRollup",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"),
				UtilMisc.toList("sequenceNum", "productCategoryId"), null, false);
		List<String> dummy = EntityUtil.getFieldListFromEntityList(productCategoryRollups, "productCategoryId", true);
		productCategoryIds.addAll(dummy);
		for (GenericValue x : productCategoryRollups) {
			productCategoryIds.addAll(childCategories(delegator, x.getString("productCategoryId")));
		}
		return productCategoryIds;
	}

	public static Map<String, Object> configProductCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String productId = (String) context.get("productId");
			String mainCategoryId = (String) context.get("mainCategoryId");
			String productCategoryId = (String) context.get("productCategoryId");
			String isBestSell = (String) context.get("isBestSell");
			String isPromos = (String) context.get("isPromos");
			String isNew = (String) context.get("isNew");
			String isFeatured = (String) context.get("isFeatured");
			Long bestSellFromDateL = (Long) context.get("bestSellFromDate");
			Long promosFromDateL = (Long) context.get("promosFromDate");
			Long newFromDateL = (Long) context.get("newFromDate");
			Long featuredFromDateL = (Long) context.get("featuredFromDate");
			Long fromDateL = (Long) context.get("fromDate");
			Long sequenceNum = (Long) context.get("sequenceNum");
			Timestamp bestSellFromDate = null;
			Timestamp promosFromDate = null;
			Timestamp newFromDate = null;
			Timestamp featuredFromDate = null;
			Timestamp fromDate = null;
			if (UtilValidate.isNotEmpty(bestSellFromDateL)) {
				bestSellFromDate = new Timestamp(bestSellFromDateL);
			}
			if (UtilValidate.isNotEmpty(promosFromDateL)) {
				promosFromDate = new Timestamp(promosFromDateL);
			}
			if (UtilValidate.isNotEmpty(newFromDateL)) {
				newFromDate = new Timestamp(newFromDateL);
			}
			if (UtilValidate.isNotEmpty(featuredFromDateL)) {
				featuredFromDate = new Timestamp(featuredFromDateL);
			}
			if (UtilValidate.isNotEmpty(fromDateL)) {
				fromDate = new Timestamp(fromDateL);
			}
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(sequenceNum)) {
				// updateProductToCategory
				dispatcher.runSync("updateProductToCategory", UtilMisc.toMap("productCategoryId", productCategoryId,
						"fromDate", fromDate, "productId", productId, "sequenceNum", sequenceNum, "userLogin", userLogin));
			}
			String productCategoryId_BSL = mainCategoryId.toUpperCase() + "_BSL";
			if ("true".equals(isBestSell) && UtilValidate.isEmpty(bestSellFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_BSL, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_BSL, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isBestSell) && UtilValidate.isNotEmpty(bestSellFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_BSL,
									"productId", productId, "fromDate", bestSellFromDate)));
				}
			}
			String productCategoryId_PROMOS = mainCategoryId.toUpperCase() + "_PROMOS";
			if ("true".equals(isPromos) && UtilValidate.isEmpty(promosFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_PROMOS, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_PROMOS, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isPromos) && UtilValidate.isNotEmpty(promosFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_PROMOS,
									"productId", productId, "fromDate", promosFromDate)));
				}
			}
			String productCategoryId_NEW = mainCategoryId.toUpperCase() + "_NEW";
			if ("true".equals(isNew) && UtilValidate.isEmpty(newFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_NEW, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_NEW, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isNew) && UtilValidate.isNotEmpty(newFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_NEW,
									"productId", productId, "fromDate", newFromDate)));
				}
			}
			String productCategoryId_FEATURED = mainCategoryId.toUpperCase() + "_FEATURED";
			if ("true".equals(isFeatured) && UtilValidate.isEmpty(featuredFromDate)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(
						UtilMisc.toMap("productCategoryId", productCategoryId_FEATURED, "productId", productId)));
				List<GenericValue> productCategoryMember = delegator.findList("ProductCategoryMember",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(productCategoryMember)) {
					dispatcher.runSync("addProductToCategory", UtilMisc.toMap("productCategoryId",
							productCategoryId_FEATURED, "productId", productId, "userLogin", userLogin));
				}
			} else {
				if ("false".equals(isFeatured) && UtilValidate.isNotEmpty(featuredFromDate)) {
					delegator.removeByCondition("ProductCategoryMember",
							EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId_FEATURED,
									"productId", productId, "fromDate", featuredFromDate)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> moveProductCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			dispatcher.runSync("updateProductCategory", context);
			String primaryParentCategoryId = (String) context.get("primaryParentCategoryId");
			String productCategoryId = (String) context.get("productCategoryId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId,
								"parentProductCategoryId", primaryParentCategoryId)));
			List<GenericValue> listProductCategoryRollup = delegator.findList("ProductCategoryRollup",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isEmpty(listProductCategoryRollup)) {
				delegator.create("ProductCategoryRollup",
						UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", primaryParentCategoryId,
								"fromDate", new Timestamp(System.currentTimeMillis())));
			}
			conditions.clear();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)));
			conditions.add(EntityCondition.makeCondition("parentProductCategoryId", EntityJoinOperator.NOT_EQUAL, primaryParentCategoryId));
			listProductCategoryRollup = delegator.findList("ProductCategoryRollup",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listProductCategoryRollup) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				delegator.store(x);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> loadCategoryImage(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String productCategoryId = (String) context.get("productCategoryId");
		GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
		Map<String, Object> categoryConfig = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(productCategory)) {
			categoryConfig.put("categoryImageUrl", productCategory.getString("categoryImageUrl"));
			categoryConfig.put("icon", productCategory.getString("icon"));
			categoryConfig.put("url", productCategory.getString("url"));
		}
		result.put("categoryConfig", categoryConfig);
		return result;
	}
	
	@Deprecated
	public static Map<String, Object> addRootCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			result = dispatcher.runSync("createProductCategory", context);
			//	RECYCLE_CATEGORY
			String productCategoryId = (String) result.get("productCategoryId");
			String productCategoryId_recyle =  productCategoryId + "_recyle";
			String categoryName = (String) context.get("categoryName") + "_recyle";
			//	createProductCategoryAndRollup
			dispatcher.runSync("createProductCategoryAndRollup",
					UtilMisc.toMap("productCategoryId", productCategoryId_recyle, "categoryName", categoryName,
							"primaryParentCategoryId", productCategoryId, "productCategoryTypeId", "RECYCLE_CATEGORY",
							"userLogin", context.get("userLogin")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> fixRootCategory(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String,Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("productCategoryTypeId", EntityJoinOperator.EQUALS, "CATALOG_CATEGORY"));
			conditions.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityJoinOperator.EQUALS, null));
			List<GenericValue> categories = delegator.findList("ProductCategory",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			//	createProductCategoryAndRollup
			for (GenericValue x : categories) {
				String productCategoryId = x.getString("productCategoryId");
				String productCategoryId_recyle =  productCategoryId + "_recyle";
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.EQUALS, productCategoryId_recyle));
				List<GenericValue> check = delegator.findList("ProductCategory",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				if (UtilValidate.isEmpty(check)) {
					String categoryName = x.getString("categoryName") + "_recyle";
					dispatcher.runSync("createProductCategoryAndRollup",
							UtilMisc.toMap("productCategoryId", productCategoryId_recyle, "categoryName", categoryName,
									"primaryParentCategoryId", productCategoryId, "productCategoryTypeId", "RECYCLE_CATEGORY",
									"userLogin", context.get("userLogin")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductReviews(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("-postedDateTime");
			EntityFindOptions opts =(EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
			if (parameters.containsKey("productId")) {
				if (UtilValidate.isNotEmpty(parameters.get("productId")[0])) {
					listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", parameters.get("productId")[0])));
				}
			}
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			List<GenericValue> productStores = delegator.findList("ProductStore",
					EntityCondition.makeCondition("visualThemeId", webSiteId), UtilMisc.toSet("productStoreId"), null, null, false);
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(productStores, "productStoreId", true);
			listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.IN, productStoreIds));
			listIterator = delegator.find("ProductReviewDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> checkProductErasable(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			String productId = (String) context.get("productId");
			String productCategoryId = (String) context.get("productCategoryId");
			Map<String, Object> categoryTrail = dispatcher.runSync("getCategoryTrail", UtilMisc.toMap("productCategoryId", productCategoryId));
			List<String> trailElements = (List<String>) categoryTrail.get("trail");
			List<String> parentsCategories = FastList.newInstance();
			for (String s : trailElements) {
				parentsCategories.add(s);
				parentsCategories.add(s.toUpperCase() + "_BSL");
				parentsCategories.add(s.toUpperCase() + "_PROMOS");
				parentsCategories.add(s.toUpperCase() + "_NEW");
				parentsCategories.add(s.toUpperCase() + "_FEATURED");
			}
			parentsCategories.remove(productCategoryId);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, parentsCategories));
			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
			List<GenericValue> productCategoryMembers = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			String erasable = "N";
			if (UtilValidate.isEmpty(productCategoryMembers)) {
				erasable = "Y";
			}
			result.put("erasable", erasable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static List<Map<String, Object>> categoriesOfWebSite(ServletRequest request, Delegator delegator) throws GenericEntityException {
		List<Map<String, Object>> categories = FastList.newInstance();
		String prodCatalogId = EntityUtil.getFirst(NewCatalogWorker.getStoreCatalogs(request)).getString("prodCatalogId");
		List<GenericValue> categoryRollups = listProductCategoryRollups(delegator, prodCatalogId);
		List<String> categoryTypes = UtilMisc.toList("CATALOG_CATEGORY");
		for (GenericValue x : categoryRollups) {
			String productCategoryId = x.getString("productCategoryId");
			GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
			String productCategoryTypeId = productCategory.getString("productCategoryTypeId");
			if (categoryTypes.contains(productCategoryTypeId)) {
				Map<String, Object> category = FastMap.newInstance();
				category.putAll(x);
				category.put("productCategoryTypeId", productCategoryTypeId);
				category.put("categoryName", productCategory.getString("categoryName"));
				category.put("longDescription", productCategory.getString("longDescription"));
				categories.add(category);
			}
		}
		return categories;
	}
	public static List<GenericValue> listProductCategoryRollups(Delegator delegator, Object prodCatalogId)
			throws GenericEntityException {
		List<GenericValue> categories = FastList.newInstance();
		List<GenericValue> listRootCategories = listRootCategories(delegator, prodCatalogId);
		for (GenericValue x : listRootCategories) {
			categories.addAll(categoriesTree(delegator, x.getString("productCategoryId")));
		}
		return categories;
	}
	private static List<GenericValue> listRootCategories(Delegator delegator, Object prodCatalogId)
			throws GenericEntityException {
		List<String> productCategoryIds = getRootCategoryIds(delegator, prodCatalogId);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN,
				productCategoryIds));
		List<GenericValue> categories = delegator.findList("ProductCategory", EntityCondition.makeCondition(conditions),
				UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("categoryName"), null, false);
		return categories;
	}
	private static List<String> getRootCategoryIds(Delegator delegator, Object prodCatalogId)
			throws GenericEntityException {
		List<String> productCategoryIds = FastList.newInstance();
		List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			prodCatalogIds = UtilMisc.toList((String)prodCatalogId);
		}
		List<EntityCondition> conditions = FastList.newInstance();
		for (String s : prodCatalogIds) {
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("prodCatalogId", EntityJoinOperator.EQUALS, s));
			conditions.add(EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityJoinOperator.EQUALS, "PCCT_BROWSE_ROOT"));
			List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), UtilMisc.toList("sequenceNum"), null, false);
			if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
				productCategoryIds.add(EntityUtil.getFirst(prodCatalogCategories).getString("productCategoryId"));
			}
		}
		return productCategoryIds;
	}
	public static List<Map<String, Object>> productsOfWebSite(ServletRequest request, Delegator delegator) throws GenericEntityException {
		List<Map<String, Object>> products = FastList.newInstance();
		String prodCatalogId = EntityUtil.getFirst(NewCatalogWorker.getStoreCatalogs(request)).getString("prodCatalogId");
		List<String> rootCategoryIds = getRootCategoryIds(delegator, prodCatalogId);
		if (UtilValidate.isNotEmpty(rootCategoryIds)) {
			List<String> productCategoryIds = childCategories(delegator, rootCategoryIds.get(0));
			productCategoryIds.add(rootCategoryIds.get(0));
			List<EntityCondition> listCondition = FastList.newInstance();
			listCondition.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.IN, productCategoryIds));
			listCondition.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(listCondition), null, UtilMisc.toList("sequenceNum", "productId"), null, false);
			for (GenericValue x : listProductCategoryMember) {
				String productId = x.getString("productId");
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				Map<String, Object> mapProduct = FastMap.newInstance();
				mapProduct.put("productId", x.get("productId"));
				mapProduct.put("productCode", product.get("productCode"));
				mapProduct.put("internalName", product.get("internalName"));
				products.add(mapProduct);
			}
		}
		return products;
	}
	
	public static Map<String, Object> removeExuberancyProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.LIKE, "%_BSL%"),
					EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.LIKE, "%_NEW%"),
					EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.LIKE, "%_FEATURED%"),
					EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.LIKE, "%_PROMOS%")
					), EntityJoinOperator.OR));
			List<GenericValue> productCategoryMembers = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue z : productCategoryMembers) {
				z.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> checkProductSaleable(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String productId = (String) context.get("productId");
			boolean saleable = false;
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			List<GenericValue> productStoreCatalogs = delegator.findList("ProductStoreCatalog",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("prodCatalogId"), null, null, false);
			List<String> prodCatalogIds = EntityUtil.getFieldListFromEntityList(productStoreCatalogs, "prodCatalogId", true);
			for (String s : prodCatalogIds) {
				if (ProductUtils.isSaleableProduct(delegator, s, productId)) {
					saleable = true;
					break;
				}
			}
			result.put("saleable", saleable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}