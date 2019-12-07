package com.olbius.bi.olap.cache.dimension;

public class ProductDimension extends OlapCache {

	public static final OlapCache D = new ProductDimension();
	
	@Override
	public String getDimension() {
		return "ProductDimension";
	}

	@Override
	public String getKey() {
		return "productId";
	}

}
