package com.olbius.bi.olap.cache;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.bi.olap.cache.dimension.Dimension;
import com.olbius.entity.cache.OlbiusCacheObject;

public class DimensionDataCache extends OlbiusCacheObject<DimensionId, Map<String, Object>> {

	public static final DimensionDataCache cache = new DimensionDataCache();
	
	@Override
	public Map<String, Object> loadCache(Delegator delegator, DimensionId key) throws Exception {
		GenericValue value = EntityUtil.getFirst(
				delegator.findByAnd(key.getDimension(), UtilMisc.toMap("dimensionId", key.getId()), null, false));
		return value != null ? value : new HashMap<String, Object>();
	}

	public static Map<String, Object> get(Delegator delegator, String dimension, long id) {
		return cache.get(delegator, DimensionId.newInstance(dimension, id));
	}
	
	public static Map<String, Object> get(Delegator delegator, Dimension dimension, long id) {
		return cache.get(delegator, DimensionId.newInstance(dimension.getDimension(), id));
	}
	
}
