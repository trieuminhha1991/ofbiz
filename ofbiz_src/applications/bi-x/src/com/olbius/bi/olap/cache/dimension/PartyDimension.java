package com.olbius.bi.olap.cache.dimension;

public class PartyDimension extends OlapCache {

	public static final OlapCache D = new PartyDimension();
	
	@Override
	public String getDimension() {
		return "PartyDimension";
	}

	@Override
	public String getKey() {
		return "partyId";
	}

}
