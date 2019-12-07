package com.olbius.ecommerce.catalog;

import java.util.Map;

import org.ofbiz.entity.Delegator;

public class ProductUtils {

	public final static String module = ProductUtils.class.getName();
	public static final String resource = "CommonUiLabels";

	public static Map<String, Object> getRelatedProduct(
			Delegator delegator, String productCategoryId, String productId, int viewIndex,
			int pagesize) {
		Map<String, Object> res = CategoryUtils.getProductInCategoryExceptOne(delegator, productCategoryId, productId, viewIndex, pagesize);
		return res;
	}
}
