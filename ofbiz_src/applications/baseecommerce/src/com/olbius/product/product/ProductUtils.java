package com.olbius.product.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.baseecommerce.backend.ConfigProductServices;
import com.olbius.product.category.CategoryUtils;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductUtils {
	
	public static Map<String, Object> getRelatedProduct( Delegator delegator, String productCategoryId, String productId, int viewIndex,
			int pagesize) {
		Map<String, Object> res = CategoryUtils.getProductInCategoryExceptOne(delegator, productCategoryId, productId, viewIndex, pagesize);
		return res;
	}
	
	public static List<GenericValue> getCatalogTopCategories(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;
        List<GenericValue> prodCatalogCategories = getProdCatalogCategories(request, prodCatalogId);
        return prodCatalogCategories;
    }
	public static List<GenericValue> getProdCatalogCategories(ServletRequest request, String prodCatalogId) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        return getProdCatalogCategories(delegator, prodCatalogId);
    }
	public static List<GenericValue> getProdCatalogCategories(Delegator delegator, String prodCatalogId) {
        try {
        	String rootCategoryId = "";
        	List<EntityCondition> conditions = FastList.newInstance();
        	conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("prodCatalogId", EntityJoinOperator.EQUALS, prodCatalogId));
			conditions.add(EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityJoinOperator.EQUALS,
					"PCCT_BROWSE_ROOT"));
			List<GenericValue> prodCatalogCategories = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId"), UtilMisc.toList("sequenceNum"), null, false);
			if (UtilValidate.isNotEmpty(prodCatalogCategories)) {
				rootCategoryId = EntityUtil.getFirst(prodCatalogCategories).getString("productCategoryId");
			}
			if (UtilValidate.isNotEmpty(rootCategoryId)) {
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition("parentProductCategoryId", EntityJoinOperator.EQUALS,
						rootCategoryId));
	            List<GenericValue> categories = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(conditions),
	    				UtilMisc.toSet("productCategoryId", "sequenceNum"), UtilMisc.toList("sequenceNum"), null, false);
	            List<GenericValue> productCategories = FastList.newInstance();
	            for (GenericValue x : categories) {
					GenericValue productCategory = delegator.findOne("ProductCategory",
							UtilMisc.toMap("productCategoryId", x.get("productCategoryId")), false);
					if (UtilValidate.isNotEmpty(productCategory)) {
						if ("CATALOG_CATEGORY".equals(productCategory.get("productCategoryTypeId"))) {
							productCategories.add(x);
						}
					}
				}
	            return productCategories;
			}
        } catch (GenericEntityException e) {
        	e.printStackTrace();
        }
        return null;
    }
	@SuppressWarnings("unchecked")
	public static void processBeforeUpdateProduct(Delegator delegator, LocalDispatcher dispatcher, GenericValue e, GenericValue userLogin) throws GenericEntityException, GenericServiceException{
		List<String> stores = null;
		Map<String, Object> cacheStores = FastMap.newInstance();
		String productCategoryId = e.getString("productCategoryId");
		if(cacheStores.containsKey(productCategoryId)){
			stores = (List<String>) cacheStores.get(productCategoryId);
		}else{
			stores = CategoryUtils.getProductStoreByCategory(delegator, productCategoryId);
			cacheStores.put(productCategoryId, stores);
		}
		String productId = e.getString("productId");
		String keywords = getProductKeywords(delegator, productId);
		Map<String, Object> cout = null;
		for(String s : stores){
			Map<String, Object> o = FastMap.newInstance();
			o.putAll(e);
			cout = getProductPrice(delegator, dispatcher, userLogin, productId,  s);
			BigDecimal listPrice = (BigDecimal) cout.get("listPrice");
			BigDecimal defaultPrice = (BigDecimal) cout.get("defaultPrice");
			BigDecimal price = (BigDecimal) cout.get("price");
			o.put("listPrice", listPrice);
			o.put("defaultPrice", defaultPrice);
			o.put("price", price);
			o.put("currencyUsed", cout.get("currencyUsed"));
			o.put("productStoreId", s);
			Map<String, Object> reviews = getTotalMarkRating(delegator, userLogin, productId, s);
			o.putAll(reviews);
			o.put("metaKeywords", keywords);
			updateProductData(delegator, o);
		}
	}
	public static void updateProductData(Delegator delegator, Map<String, Object> product) throws GenericEntityException{
		String productStoreId = (String) product.get("productStoreId");
		String productId = (String) product.get("productId");
		String partyId = (String) product.get("partyId");
		if(UtilValidate.isEmpty(partyId)){
			partyId = "_NA_";
			product.put("partyId", partyId);
		}
		GenericValue pa = delegator.findOne("ProductCacheData", UtilMisc.toMap("productStoreId", productStoreId, "productId", productId, "partyId", partyId), false);
		if(UtilValidate.isNotEmpty(pa)){
			pa.setNonPKFields(product);
			pa.store();
		}else{
			pa = delegator.makeValidValue("ProductCacheData", product);
			pa.create();
		}
	}
	public static void updateProductReview(Delegator delegator, GenericValue userLogin, String productId) throws GenericEntityException{
		EntityListIterator list = null;
		try{
			list = delegator.find("ProductCacheData", EntityCondition.makeCondition("productId", productId), null, null, null, null);
			GenericValue e = null;
			Map<String, Object> rating = null;
			StringBuilder tmp = new StringBuilder();
			while((e = list.next()) != null){
				String cur = productId + "-" + e.getString("productStoreId");
				if(!tmp.toString().equals(cur)){
					tmp = new StringBuilder(cur);
					rating = getTotalMarkRating(delegator, userLogin, productId, e.getString("productStoreId"));
					e.set("rating", (BigDecimal) rating.get("rating"));
					e.set("totalReview", (BigDecimal) rating.get("totalReview"));
					e.store();
				}
			}
		}catch(Exception e){
			Debug.log(e.getMessage());
		}finally {
			if(list != null){
				list.close();
			}
		}
	}
	public static void updateProductPrice(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String productId) throws GenericEntityException{
		EntityListIterator list = null;
		try{
			list = delegator.find("ProductCacheData", EntityCondition.makeCondition("productId", productId), null, null, null, null);
			GenericValue e = null;
			Map<String, Object> cout = null;
			StringBuilder tmp = null;
			while((e = list.next()) != null){
				String cur = productId + "-" + e.getString("productStoreId");
				if(!tmp.toString().equals(cur)){
					tmp = new StringBuilder(cur);
					cout = getProductPrice(delegator, dispatcher, userLogin, productId,  e.getString("productStoreId"));
					BigDecimal listPrice = (BigDecimal) cout.get("listPrice");
					BigDecimal defaultPrice = (BigDecimal) cout.get("listPrice");
					BigDecimal price = (BigDecimal) cout.get("listPrice");
					e.set("listPrice", listPrice);
					e.set("defaultPrice", defaultPrice);
					e.set("price", price);
					e.set("currencyUsed", cout.get("currencyUsed"));
					e.store();
				}
			}
		}catch(Exception e){
			Debug.log(e.getMessage());
		}finally {
			if(list != null){
				list.close();
			}
		}
	}

	public static void updateProductKeywords(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String productId) throws GenericEntityException{
		EntityListIterator list = null;
		try{
			GenericValue e = null;
			list = delegator.find("ProductCacheData", EntityCondition.makeCondition("productId", productId), null, null, null, null);
			String keywords = getProductKeywords(delegator, productId);
			while((e = list.next()) != null){
				e.setString("metaKeywords", keywords);
				e.store();
			}
		}catch(Exception e){
			Debug.log(e.getMessage());
		}finally {
			if(list != null){
				list.close();
			}
		}
	}

	public static GenericValue getProductData(Delegator delegator, String productStoreId, String productId){
		String partyId = "_NA_";
		return getProductData(delegator, productStoreId, productId, partyId);
	}
	public static Map<String, Object> getProductPrice(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String productId, String productStoreId) throws GenericServiceException{
		Map<String, Object> c = FastMap.newInstance();
		Map<String, Object> cout = FastMap.newInstance();
		try {
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
			c.put("userLogin", userLogin);
			c.put("productStoreId", productStoreId);
			c.put("product", product);
			cout = dispatcher.runSync("calculateProductPriceCustom", c);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return cout;
	}
	public static GenericValue getProductData(Delegator delegator, String productStoreId, String productId, String partyId){
		if(UtilValidate.isEmpty(partyId)){
			partyId = "_NA_";
		}
		GenericValue rp = null;
		try {
			rp = delegator.findOne("ProductCacheDataDetail", UtilMisc.toMap("productStoreId", productStoreId, "productId", productId, "partyId", partyId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
		}
		return rp;
	}
	public static List<String> getProductStoreRelated(Delegator delegator, String productStoreId) throws GenericEntityException{
		List<String> visualThemes = getVisualThemeRelated(delegator, productStoreId);
		List<GenericValue> stores = delegator.findList("ProductStore", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("visualThemeId", EntityOperator.IN, visualThemes), EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_EQUAL, productStoreId))), UtilMisc.toSet("productStoreId"), null, null, true);
		List<String> storeIds = FastList.newInstance();
		for(GenericValue e : stores){
			storeIds.add(e.getString("productStoreId"));
		}
		return storeIds;
	}

	public static List<String> getVisualThemeRelated(Delegator delegator, String productStoreId) throws GenericEntityException{
		List<GenericValue> visuals = delegator.findList("ProductStore", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productStoreId", productStoreId))), UtilMisc.toSet("visualThemeId"), null, null, true);
		List<String> visualThemes = FastList.newInstance();
		for(GenericValue e : visuals){
			visualThemes.add(e.getString("visualThemeId"));
		}
		return visualThemes;
	}

	public static Map<String, Object> getTotalMarkRating(Delegator delegator, GenericValue userLogin, String productId, String productStoreId) throws GenericEntityException {
		Map<String, Object> res = FastMap.newInstance();
		BigDecimal totalRating = new BigDecimal(0);
		BigDecimal totalReview = new BigDecimal(0);
		EntityListIterator productReview = null;
		try {
			List<String> stores = getProductStoreRelated(delegator, productStoreId);
			stores.add(productStoreId);
			productReview = delegator.find("ProductReviewSum",
						EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, stores),
										EntityCondition.makeCondition("productId", productId))),
						null, null, null, null);
			GenericValue e = null;
			while((e = productReview.next()) != null){
				totalRating = totalRating.add(e.getBigDecimal("totalRating"));
				Long tm = e.getLong("totalReview");
				BigDecimal tmd = new BigDecimal(tm);
				totalReview = totalReview.add(tmd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			productReview.close();
		}
		res.put("rating", totalRating);
		res.put("totalReview", totalReview);
		return res;
	}
	public static String getProductKeywords(Delegator delegator, String productId) throws GenericEntityException{
		EntityListIterator list = null;
		StringBuilder tmp = new StringBuilder();
		try {
			list = delegator.find("ProductKeyword", EntityCondition.makeCondition("productId", productId), null, null, null, null);
			GenericValue e = null;
			while((e = list.next()) != null){
				tmp.append(e.getString("keyword")).append(". ");
			}
		} catch (GenericEntityException e1) {
			Debug.log(e1.getMessage());
		} finally {
			list.close();
		}
		String keywords = tmp.toString();
		return keywords;
	}
	
	public static boolean isSaleableProduct(Delegator delegator, String prodCatalogId, String productId) {
		boolean saleable = false;
		try {
			List<GenericValue> categoryRollups = ConfigProductServices.listProductCategoryRollups(delegator, prodCatalogId);
			List<String> categoryTypes = UtilMisc.toList("CATALOG_CATEGORY");
			List<Object> categories = FastList.newInstance();
			for (GenericValue x : categoryRollups) {
				String productCategoryId = x.getString("productCategoryId");
				GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
				String productCategoryTypeId = productCategory.getString("productCategoryTypeId");
				if (categoryTypes.contains(productCategoryTypeId)) {
					categories.add(x.get("productCategoryId"));
				}
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("productCategoryId", EntityComparisonOperator.IN, categories));
			conditions.add(EntityCondition.makeCondition("productId", EntityComparisonOperator.EQUALS, productId));
			List<GenericValue> productCategoryMembers = delegator.findList("ProductCategoryMember",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productCategoryId", "productId", "fromDate"), null, null, false);
			if (UtilValidate.isNotEmpty(productCategoryMembers)) {
				saleable = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saleable;
	}
}