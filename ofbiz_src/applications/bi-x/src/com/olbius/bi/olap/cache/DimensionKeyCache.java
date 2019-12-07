package com.olbius.bi.olap.cache;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.bi.olap.cache.dimension.Dimension;
import com.olbius.entity.cache.OlbiusCacheObject;

public class DimensionKeyCache extends OlbiusCacheObject<DimensionKey, Long> {

	public static final DimensionKeyCache cache = new DimensionKeyCache();
	
	@Override
	public Long loadCache(Delegator delegator, DimensionKey key) throws Exception {
		GenericValue value = EntityUtil.getFirst(
				delegator.findByAnd(key.getDimension(), UtilMisc.toMap(key.getKey(), key.getValue()), null, false));
		return value != null ? value.getLong("dimensionId") : -1;
	}

	public static long get(Delegator delegator, String dimension, String key, String value) {
		return cache.get(delegator, DimensionKey.newInstance(dimension, key, value));
	}
	
	public static long get(Delegator delegator, Dimension dimension, String value) {
		return cache.get(delegator, DimensionKey.newInstance(dimension.getDimension(), dimension.getKey(), value));
	}
	
}
