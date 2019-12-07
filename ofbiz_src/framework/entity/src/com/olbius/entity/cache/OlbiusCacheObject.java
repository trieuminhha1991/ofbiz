package com.olbius.entity.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.olbius.entity.cache.OlbiusCache.OlbiusCacheLoader;

public abstract class OlbiusCacheObject<T, V> {

	private Map<String, LoadingCache<T, V>> map = new HashMap<String, LoadingCache<T, V>>();

	private static final long timeCache;
	private static final long maximumSize;
	static {
        timeCache = UtilProperties.getPropertyAsLong("olbiusCache", "timeCache", 60);
        maximumSize = UtilProperties.getPropertyAsLong("olbiusCache", "maximumSize", 1000);
    }

	public LoadingCache<T, V> getLoadingCache(Delegator delegator) {
		LoadingCache<T, V> cache = map.get(delegator.getDelegatorName());
		if (cache == null) {
			cache = CacheBuilder.newBuilder().maximumSize(maximumSize)
					.expireAfterAccess(timeCache, TimeUnit.MINUTES)
					.build(new OlbiusCacheLoader<T, V>(delegator) {
						@Override
						public V load(T key) throws Exception {
							return loadCache(delegator, key);
						}
					});
			map.put(delegator.getDelegatorName(), cache);
		}
		return cache;
	}

	public abstract V loadCache(Delegator delegator, T key) throws Exception;

	public V get(Delegator delegator, T key) {
		try {
			return getLoadingCache(delegator).get(key);
		} catch (ExecutionException e) {
			Debug.logError(e, this.getClass().getName());
			return null;
		}
	}

	public void clean(Delegator delegator) {
		getLoadingCache(delegator).invalidateAll();
	}

}
