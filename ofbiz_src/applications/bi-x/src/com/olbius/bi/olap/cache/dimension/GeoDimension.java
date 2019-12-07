package com.olbius.bi.olap.cache.dimension;

public class GeoDimension extends OlapCache {

	public static final OlapCache D = new GeoDimension();
	
	@Override
	public String getDimension() {
		return "GeoDimension";
	}

	@Override
	public String getKey() {
		return "geoId";
	}

}
