package com.olbius.product.category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil.StringWrapper;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.category.CategoryContentWrapper;

import com.olbius.product.catalog.NewCatalogWorker;

import javolution.util.FastList;
import javolution.util.FastMap;

public class CategoryUtils {

	public final static String module = CategoryUtils.class.getName();
	public static final String resource = "CommonUiLabels";

	public static Map<String, Object> getProductInCategoryExceptOne(
			Delegator delegator, String productCategoryId, String productId,
			int viewIndex, int pagesize) {
		Map<String, Object> res = FastMap.newInstance();
		EntityListIterator lstIte = null;
		try {
			EntityFindOptions findOptions = new EntityFindOptions(true,
					EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, true);
			EntityCondition cond = EntityCondition.makeCondition(UtilMisc
					.toList(EntityCondition.makeCondition("primaryProductCategoryId",
							productCategoryId), EntityCondition.makeCondition(
							"productId", EntityOperator.NOT_EQUAL, productId)));
			lstIte = delegator.find("Product", cond, null, null,
					UtilMisc.toList("-productId"), findOptions);
			int arraySize = lstIte.getResultsTotalSize();
			res.put("totalrows", arraySize);
			List<GenericValue> contents = FastList.newInstance();
			int start = viewIndex * pagesize + 1;
			int end = start + pagesize;
			if (arraySize < end) {
				contents = lstIte.getPartialList(start, arraySize);
			} else {
				contents = lstIte.getPartialList(start, pagesize);
			}
			res.put("products", contents);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("totalrows", 0);
			res.put("products", FastList.newInstance());
		} finally {
			if (lstIte != null) {
				try {
					lstIte.close();
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}
	public static void flatternCategories(Delegator delegator, List<String> categories, String parentProductCategoryId){
		try {
			List<GenericValue> tmp = delegator.findList("ProductCategoryRollup",
					EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("parentProductCategoryId", parentProductCategoryId),
							EntityUtil.getFilterByDateExpr())), UtilMisc.toSet("productCategoryId"),
					UtilMisc.toList("productCategoryId"), null, true);
			for(GenericValue e : tmp){
				String productCategoryId = e.getString("productCategoryId");
				if(categories.indexOf(productCategoryId) == -1){
					categories.add(productCategoryId);
					flatternCategories(delegator, categories, productCategoryId);
				}
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
	}
	public static List<String> getProductStoreByCategory(Delegator delegator, String productCategoryId) throws GenericEntityException{
		List<String> stores = FastList.newInstance();
		String topCategoryId = getTopParentCategory(delegator, productCategoryId);
		if(UtilValidate.isNotEmpty(topCategoryId)){
			List<GenericValue> catalogs = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productCategoryId", topCategoryId),
							EntityUtil.getFilterByDateExpr(), EntityCondition.makeCondition("prodCatalogCategoryTypeId", "PCCT_BROWSE_ROOT"))),
					UtilMisc.toSet("prodCatalogId"), UtilMisc.toList("-fromDate"), null, true);
			if(UtilValidate.isNotEmpty(catalogs)){
				GenericValue e = EntityUtil.getFirst(catalogs);
				String prodCatalogId = e.getString("prodCatalogId");
				List<GenericValue> dstores = delegator.findList("ProductStoreCatalog",
						EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("prodCatalogId", prodCatalogId),
								EntityUtil.getFilterByDateExpr())),
						UtilMisc.toSet("productStoreId"), UtilMisc.toList("-fromDate"), null, true);
				for(GenericValue s : dstores){
					stores.add(s.getString("productStoreId"));
				}
			}
		}
		return stores;
	}

	public static String getTopParentCategory(Delegator delegator, String productCategoryId) throws GenericEntityException{
		List<String> cats = getCategoryRollUp(delegator, null, productCategoryId);
		String topCategoryId = null;
		if(UtilValidate.isNotEmpty(cats)){
			topCategoryId = cats.get(cats.size() - 1);
		}
		return topCategoryId;
	}
	public static List<String> getCategoryRollUp(Delegator delegator, List<String> parents, String productCategoryId) throws GenericEntityException{
		if(parents == null){
			parents = FastList.newInstance();
			parents.add(productCategoryId);
		}
		if(UtilValidate.isEmpty(productCategoryId)){
			return null;
		}
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		List<GenericValue> cates = delegator.findList("ProductCategoryRollup",
				EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("productCategoryId", productCategoryId),
						EntityUtil.getFilterByDateExpr())), UtilMisc.toSet("parentProductCategoryId"),
				UtilMisc.toList("fromDate"), null, true);
		GenericValue e = EntityUtil.getFirst(cates);
		if(e != null){
			String parentProductCategoryId = e.getString("parentProductCategoryId");
			if(parents.indexOf(parentProductCategoryId) == -1){
				parents.add(parentProductCategoryId);
				getCategoryRollUp(delegator, parents, parentProductCategoryId);
			}
		}
		return parents;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getProductCategoryAndLimitedMembers(Delegator delegator, Map<String, Object> context) throws GenericEntityException {
		String productCategoryId = (String) context.get("productCategoryId");
		String brand = (String) context.get("brand");
		String lp = (String) context.get("lowPrice");
		String hp = (String) context.get("highPrice");
		List<String> productAppeared = (List<String>) context.get("productAppeared");
		int defaultViewSize = ((Integer) context.get("defaultViewSize")).intValue();
		List<String> orderByFields = UtilGenerics.checkList(context.get("orderByFields"));
		if (orderByFields == null) orderByFields = FastList.newInstance();
		String entityName = "ProductCategoryMember";
		boolean activeOnly = (context.get("activeOnly") == null || ((Boolean) context.get("activeOnly")).booleanValue());
		// checkViewAllow defaults to false, must be set to true and pass the prodCatalogId to enable
		int viewIndex = 0;
		try {
		    viewIndex = Integer.valueOf((String) context.get("viewIndexString")).intValue();
		} catch (Exception e) {
		    viewIndex = 0;
		}
		int viewSize = defaultViewSize;
		try {
		    viewSize = Integer.valueOf((String) context.get("viewSizeString")).intValue();
		} catch (Exception e) {
		    viewSize = defaultViewSize;
		}
		GenericValue productCategory = null;
		List<GenericValue> childProductCategory = null;
		try {
			productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), true);
			childProductCategory = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition("parentProductCategoryId", productCategoryId), null, null, null, false);
			childProductCategory = EntityUtil.filterByCondition(childProductCategory, EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
			productCategory = null;
		}
		int start = viewIndex * viewSize + 1;
		int listSize = 0;
		int end = start + viewSize;
		List<GenericValue> productCategoryMembers = FastList.newInstance();
		EntityListIterator list = null;
		if (productCategory != null) {
			try {
				List<EntityCondition> tmpCond = FastList.newInstance();
				if (UtilValidate.isNotEmpty(brand)) {
					tmpCond.add(filterProductByBrand(delegator, brand));
				}
				if (UtilValidate.isEmpty(childProductCategory)) {
					tmpCond.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
				} else {
					List<String> tmp = FastList.newInstance();
					for(GenericValue e : childProductCategory){
						tmp.add(e.getString("productCategoryId"));
					}
					tmpCond.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productCategoryId",
					EntityOperator.EQUALS, productCategoryId), EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, tmp)), EntityOperator.OR));
				}
				if (activeOnly) {
					tmpCond.add(EntityUtil.getFilterByDateExpr());
				}
				try{
					if(UtilValidate.isNotEmpty(lp) || UtilValidate.isNotEmpty(hp)){
						entityName = "ProductCategoryMemberAndPrice";
						tmpCond.add(EntityCondition.makeCondition(
								"priceProductPriceTypeId", EntityOperator.EQUALS,
								"LIST_PRICE"));
						if(UtilValidate.isNotEmpty(hp)){
							BigDecimal highPrice = new BigDecimal(hp);
							tmpCond.add(EntityCondition.makeCondition("pricePrice",
									EntityOperator.LESS_THAN, highPrice));
						}
						if(UtilValidate.isNotEmpty(lp)){
							BigDecimal lowPrice = new BigDecimal(lp);
							tmpCond.add(EntityCondition.makeCondition("pricePrice",
									EntityOperator.GREATER_THAN_EQUAL_TO, lowPrice));
						}
					}
				} catch (Exception e) {
					Debug.log(e.getMessage());
				}
				if (UtilValidate.isNotEmpty(productAppeared)) {
					tmpCond.add(EntityCondition.makeCondition("productId", EntityJoinOperator.NOT_IN, productAppeared));
				}
				EntityFindOptions opts = new EntityFindOptions();
				opts.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
				opts.setDistinct(true);
				list = delegator.find(entityName, EntityCondition.makeCondition(tmpCond), null, UtilMisc.toSet("productId", "sequenceNum"), orderByFields, opts);
				listSize = list.getResultsTotalSize();
				end = end < listSize ? listSize : listSize;
				productCategoryMembers = list.getPartialList(start, viewSize);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			} finally {
				if(list != null){
					list.close();
				}
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("viewIndex", Integer.valueOf(viewIndex));
		result.put("viewSize", Integer.valueOf(viewSize));
		result.put("lowIndex", Integer.valueOf(start + 1));
		result.put("highIndex", Integer.valueOf(end));
		result.put("listSize", Integer.valueOf(listSize));
		if (productCategory != null) result.put("productCategory", productCategory);
		if (productCategoryMembers != null) result.put("productCategoryMembers", productCategoryMembers);
		return result;
	}

	public static EntityCondition filterProductByBrand(Delegator delegator, String brandName) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		String otherBrandId = EntityUtilProperties.getPropertyValue("ecommerce.properties", "category.brand.other.default", "OTHER_BRAND", delegator);
		if (otherBrandId.equals(brandName)) {
			conditions.add(EntityCondition.makeCondition(
					UtilMisc.toList(
							EntityCondition.makeCondition("brandName", EntityJoinOperator.EQUALS, brandName),
							EntityCondition.makeCondition("brandName", EntityJoinOperator.EQUALS, null)
							), EntityJoinOperator.OR));
		} else {
			conditions.add(EntityCondition.makeCondition("brandName", EntityJoinOperator.EQUALS, brandName));
		}
		List<GenericValue> products = delegator.findList("Product",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("productId"), null, null, false);
		EntityCondition condition = EntityCondition.makeCondition("productId", EntityJoinOperator.IN,
				EntityUtil.getFieldListFromEntityList(products, "productId", true));
		return condition;
	}

	public static Map<String, Object> getCategoryData(Delegator delegator, HttpServletRequest request, String productCategoryId){
		Map<String, Object> cond = UtilMisc.toMap("productCategoryId", productCategoryId);
		Map<String, Object> res = FastMap.newInstance();
		try {
			GenericValue category = delegator.findOne("ProductCategory", cond, true);
			if (category != null) {
				CategoryContentWrapper categoryContentWrapper = new CategoryContentWrapper(category, request);

				String detailScreen = category.getString("detailScreen");
			    if (UtilValidate.isNotEmpty(detailScreen)) {
				res.put("detailScreen", detailScreen);
			    }
			    GenericValue pt = getContentInfo(delegator, productCategoryId, "PAGE_TITLE");

			    if (pt != null) {
				res.put("title", pt.getString("textData"));
			    }else{
				res.put("title", categoryContentWrapper.get("CATEGORY_NAME"));
			    }
			    GenericValue description = getContentInfo(delegator, productCategoryId, "META_DESCRIPTION");
			    String  metaDescription = null;
			    StringWrapper tm = null;
			    if (description != null) {
				metaDescription = description.getString("textData");
			    } else {
				tm = categoryContentWrapper.get("DESCRIPTION");
				if(tm != null){
					metaDescription = tm.toString();
				}
			    }
			    res.put("metaDescription", metaDescription);
			    GenericValue metaKeywords = getContentInfo(delegator, productCategoryId, "META_KEYWORD");

			    if (metaKeywords != null) {
				res.put("metaKeywords", metaKeywords.getString("textData"));
			    } else {
				String catalogName = NewCatalogWorker.getCatalogName(request);
			        if (UtilValidate.isNotEmpty(metaDescription)) {
					res.put("metaKeywords", metaDescription + ", " + catalogName);
			        } else {
					res.put("metaKeywords", catalogName);
			        }
			    }
			    res.put("productCategory", category);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return res;
	}
	public static GenericValue getContentInfo(Delegator delegator, String productCategoryId, String contentTypeId) throws GenericEntityException{
		List<GenericValue> categoryPageTitle = delegator.findByAnd("ProductCategoryContentAndInfo", UtilMisc.toMap("productCategoryId", productCategoryId, "prodCatContentTypeId", contentTypeId), null, true);
		GenericValue pageTitle = null;
	    if (UtilValidate.isNotEmpty(categoryPageTitle)) {
		pageTitle = delegator.findOne("ElectronicText", UtilMisc.toMap("dataResourceId", categoryPageTitle.get(0).getString("dataResourceId")), true);
	    }
	    return pageTitle;
	}
}