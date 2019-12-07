package com.olbius.product.product;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.olbius.product.category.CategoryUtils;

import javolution.util.FastList;

public class ProductServices {

    public final static String module = ProductServices.class.getName();
    public static final String resource = "BaseEcommerceUiLabels";

    public static Map<String, Object> synchronizeProduct(DispatchContext dpc, Map<String, Object> context) throws GenericServiceException, GenericEntityException{
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpc.getDelegator();
		LocalDispatcher dispatcher = dpc.getDispatcher();
		String productId = (String) context.get("productId");
		
		EntityListIterator products = null;
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"));
			conds.add(EntityUtil.getFilterByDateExpr());
			
			Set<String> listSelectFields = UtilMisc.toSet("productId", "productCode", "productName", "productCategoryId", "description", "originalImageUrl");
						listSelectFields.add("mediumImageUrl");
						listSelectFields.add("largeImageUrl");
						listSelectFields.add("smallImageUrl");
						listSelectFields.add("brandName");
						listSelectFields.add("originGeoId");
			
			products = delegator.find("ProductCategoryMemberAndProduct", EntityCondition.makeCondition(conds), null, listSelectFields, null, null);
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),  true);
			if (products != null) {
				GenericValue product = null;
				while((product = products.next()) != null){
					ProductUtils.processBeforeUpdateProduct(delegator, dispatcher, product, system);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Fatal error calling jqGetListSalesOrder service: " + e.toString(), module);
		} finally {
			if (products != null) {
				products.close();
			}
		}

		return res;
    }
    
    public static Map<String, Object> synchronizeProducts(DispatchContext dpc, Map<String, Object> context) throws GenericServiceException{
	Map<String, Object> res = ServiceUtil.returnSuccess();
	Delegator delegator = dpc.getDelegator();
	LocalDispatcher dispatcher = dpc.getDispatcher();
	EntityListIterator products = null;
	try {
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
			List<GenericValue> productStores = delegator.findList("ProductStoreCatalogDetail", EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_ECOMMERCE"), UtilMisc.toSet("prodCatalogId"), null, options, true);
			List<String> catalogs = FastList.newInstance();
			for(GenericValue e : productStores){
				catalogs.add(e.getString("prodCatalogId"));
			}
			List<GenericValue> prodCatalogCate = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(
							UtilMisc.toList(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, catalogs),
							EntityUtil.getFilterByDateExpr())),
					UtilMisc.toSet("productCategoryId"), null, options, true);
			List<String> categories = FastList.newInstance();
			for(GenericValue e : prodCatalogCate){
				String productCategoryId = e.getString("productCategoryId");
				categories.add(productCategoryId);
				CategoryUtils.flatternCategories(delegator, categories, productCategoryId);
			}
			Set<String> fields = UtilMisc.toSet("productId", "productCode", "productName", "productCategoryId", "description", "originalImageUrl");
			fields.add("mediumImageUrl");
			fields.add("largeImageUrl");
			fields.add("smallImageUrl");
			fields.add("brandName");
			fields.add("originGeoId");

			products = delegator.find("ProductCategoryMemberAndProduct",
					EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categories),
								EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"),
								EntityUtil.getFilterByDateExpr())
							), null, fields, null, options);
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),  true);
			GenericValue product = null;
			while((product = products.next()) != null){
				ProductUtils.processBeforeUpdateProduct(delegator, dispatcher, product, system);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		} finally {
			if(products != null){
				try {
					products.close();
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
			}
		}
	return res;
    }
    public static Map<String, Object> deleteProductCacheData(DispatchContext dpc, Map<String, Object> context){
	Delegator delegator = dpc.getDelegator();
	Map<String, Object> res = ServiceUtil.returnSuccess();
	String productId = (String) context.get("productId");
	String productStoreId = (String) context.get("productStoreId");
	String partyId = (String) context.get("partyId");
	if(UtilValidate.isEmpty(partyId)){
		partyId = "_NA_";
	}
	try {
			GenericValue us = delegator.findOne("ProductCacheData", UtilMisc.toMap("productId", productId, "productStoreId", productStoreId, "partyId", partyId), false);
			if(UtilValidate.isNotEmpty(us)){
				us.remove();
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
	return res;
    }
    public static Map<String, Object> deleteListProductCacheData(DispatchContext dpc, Map<String, Object> context){
	Delegator delegator = dpc.getDelegator();
	Map<String, Object> res = ServiceUtil.returnSuccess();
	String productId = (String) context.get("productId");
	EntityListIterator list = null;
	try {
		list = delegator.find("ProductCacheData", EntityCondition.makeCondition("productId", productId), null, null, null, null);
		GenericValue e = null;
		while((e = list.next()) != null){
			e.remove();
		}
	} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		} finally {
			if(list != null){
				try {
					list.close();
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
			}
		}
	return res;
    }
    public static Map<String, Object> synchronizeProductPrice(DispatchContext dpc, Map<String, Object> context) throws GenericServiceException, GenericEntityException{
	Map<String, Object> res = ServiceUtil.returnSuccess();
	Delegator delegator = dpc.getDelegator();
	LocalDispatcher dispatcher = dpc.getDispatcher();
	String productId = (String) context.get("productId");
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),  true);
			ProductUtils.updateProductPrice(delegator, dispatcher, system, productId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}

	return res;
    }
    public static Map<String, Object> synchronizeProductReview(DispatchContext dpc, Map<String, Object> context) throws GenericServiceException, GenericEntityException{
	Map<String, Object> res = ServiceUtil.returnSuccess();
	Delegator delegator = dpc.getDelegator();
	String productId = (String) context.get("productId");
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),  true);
			ProductUtils.updateProductReview(delegator,system, productId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}

	return res;
    }
    public static Map<String, Object> synchronizeProductKeywords(DispatchContext dpc, Map<String, Object> context) throws GenericServiceException, GenericEntityException{
	Map<String, Object> res = ServiceUtil.returnSuccess();
	Delegator delegator = dpc.getDelegator();
	LocalDispatcher dispatcher = dpc.getDispatcher();
	String productId = (String) context.get("productId");
		try {
			GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"),  true);
			ProductUtils.updateProductKeywords(delegator, dispatcher, system, productId);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}

	return res;
    }
}
