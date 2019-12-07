package com.olbius.bi.olap.cache.dimension;

import java.util.Map;

import org.ofbiz.entity.Delegator;

import com.olbius.bi.olap.cache.DimensionDataCache;
import com.olbius.bi.olap.cache.DimensionKeyCache;

public abstract class OlapCache implements Dimension {

	public long getId(Delegator delegator, String key) {
		return DimensionKeyCache.get(delegator, this, key);
	}
	
	public Map<String, Object> getData(Delegator delegator, long id) {
		return DimensionDataCache.get(delegator, this, id);
	}
	
}
