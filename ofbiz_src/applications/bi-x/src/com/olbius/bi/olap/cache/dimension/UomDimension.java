package com.olbius.bi.olap.cache.dimension;

public class UomDimension extends OlapCache {

	public static final OlapCache D = new UomDimension();
	
	@Override
	public String getDimension() {
		return "CurrencyDimension";
	}

	@Override
	public String getKey() {
		return "currencyId";
	}

}
