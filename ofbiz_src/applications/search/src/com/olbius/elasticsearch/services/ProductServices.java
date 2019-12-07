package com.olbius.elasticsearch.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.elasticsearch.ElasticIndex;
import com.olbius.elasticsearch.ElasticIndexFactory;
import com.olbius.elasticsearch.search.OlbiusSearch;
import com.olbius.elasticsearch.search.OlbiusSearchFactory;

import javolution.util.FastMap;

/**
 * @author Nguyen Ha
 *
 */
public class ProductServices {

	public final static String module = ProductServices.class.getName();

	public static Map<String, Object> deleteIndexProducts(DispatchContext ctx, Map<String, ? extends Object> context) {

		String productStore = (String) context.get("productStoreId");

		ElasticIndex index = ElasticIndexFactory.getInstance();
		index.setIndex(ctx.getDelegator(), productStore);

		index.deleteIndex();
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> indexProducts(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();

		String productStore = (String) context.get("productStoreId");

		List<GenericValue> catalog = new ArrayList<GenericValue>();
		try {
			catalog = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStore),
					null, UtilMisc.toList("prodCatalogId"), null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}

		ElasticIndex index = ElasticIndexFactory.getInstance();
		index.setIndex(delegator, productStore);
		index.setType("product");

		for (GenericValue value : catalog) {

			try {
				getProduct(delegator, index, productStore, value.getString("prodCatalogId"), null);
			} catch (GenericEntityException e) {
				Debug.logError(e, productStore);
			}

		}
		index.commit();

		return ServiceUtil.returnSuccess();
	}

	private static Set<String> getProduct(Delegator delegator, ElasticIndex index, String store, String catalog, String category)
			throws GenericEntityException {

		Set<String> products = new TreeSet<String>();

		List<GenericValue> values;

		if (category == null) {

			values = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, catalog), null,
					UtilMisc.toList("productCategoryId"), null, false);
			for (GenericValue value : values) {
				products.addAll(getProduct(delegator, index, store, catalog, value.getString("productCategoryId")));
			}
		} else {
			values = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition("parentProductCategoryId", category), null, null, null,
					false);
			for (GenericValue value : values) {
				products.addAll(getProduct(delegator, index, store, catalog, value.getString("productCategoryId")));
			}
			values = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId", category), null, null, null,
					false);
			for (GenericValue value : values) {
				products.add(value.getString("productId"));
			}
		}

		for (String member : products) {
			GenericValue product = null;
			try {
				product = delegator.findOne("Product", UtilMisc.toMap("productId", member), false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
			if (product != null) {
				ProductIndex productIndex = new ProductIndex();
				if(category != null) {
					productIndex.setKey(category + " " + product.getString("productId"));
				} else {
					productIndex.setKey(store + " " + product.getString("productId"));
				}
				
				productIndex.setProductStoreId(store);
				productIndex.setProductCatalogId(catalog);
				productIndex.setProductCategoryId(category);
				productIndex.setProductId(product.getString("productId"));
				productIndex.setProductName(product.getString("productName"));

				index.indexDatas(productIndex);
			}
		}

		return products;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> searchProducts(DispatchContext ctx, Map<String, ? extends Object> context) {

		String keyword = (String) context.get("keyword");

		String productStore = (String) context.get("productStoreId");

		String prodCatalog = (String) context.get("prodCatalog");

		Integer start = (Integer) context.get("start");

		Integer rows = (Integer) context.get("rows");

		List<String> products = new ArrayList<String>();

		OlbiusSearch search = OlbiusSearchFactory.getInstance();

		search.setIndex(ctx.getDelegator(), productStore);

		search.setType("product");

		search.setOffset(start);

		search.setLimit(rows);

		Map<String, String> params = new HashMap<String, String>();

		params.put("productName", keyword.toLowerCase());
		
		Map<String, String> filter = new HashMap<String, String>();

		if (prodCatalog != null && !prodCatalog.isEmpty()) {
			filter.put("key", prodCatalog.toLowerCase());
		} else {
			params.put("key", productStore.toLowerCase());
		}

		List<ProductIndex> list = (List<ProductIndex>) search.search(params, filter);

		long count = search.getTotal();

		for (ProductIndex p : list) {
			products.add(p.getProductId());
		}

		Map<String, Object> result = FastMap.newInstance();

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put("result", products);
		result.put("count", count);

		return result;

	}
}
