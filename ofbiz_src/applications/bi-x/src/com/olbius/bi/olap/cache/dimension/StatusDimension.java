package com.olbius.bi.olap.cache.dimension;

public class StatusDimension extends OlapCache {

	public static final OlapCache D = new StatusDimension();

	@Override
	public String getDimension() {
		return "StatusDimension";
	}

	@Override
	public String getKey() {
		return "statusId";
	}

}