package com.olbius.bi.olap.cache.dimension;

public class CategoryDimension extends OlapCache {

	public static final OlapCache D = new CategoryDimension();

	@Override
	public String getDimension() {
		return "CategoryDimension";
	}

	@Override
	public String getKey() {
		return "categoryId";
	}

}
