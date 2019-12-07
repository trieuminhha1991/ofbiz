package com.olbius.bi.olap.cache.dimension;

public class FacilityDimension extends OlapCache {

	public static final OlapCache D = new FacilityDimension();
	
	@Override
	public String getDimension() {
		return "FacilityDimension";
	}

	@Override
	public String getKey() {
		return "facilityId";
	}

}
