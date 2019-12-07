package com.olbius.entity.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class OlbiusCache<V> {

    private long timeCacheDefault;
    private long maximumSizeDefault;

	public OlbiusCache(Map<String, Long> config) {
        if(config != null && !config.isEmpty()) {
            if(config.get("timeCache") != null)
                timeCacheDefault = config.get("timeCache");
            if(config.get("maximumSize") != null)
                maximumSizeDefault = config.get("maximumSize");
        }
    }

    public OlbiusCache() {
        timeCacheDefault = UtilProperties.getPropertyAsLong("olbiusCache", "timeCache", 60);
        maximumSizeDefault = UtilProperties.getPropertyAsLong("olbiusCache", "maximumSize", 1000);
    }

	private Map<String, LoadingCache<String, V>> map = new HashMap<String, LoadingCache<String, V>>();

	private LoadingCache<String, V> getLoadingCache(Delegator delegator) {
		LoadingCache<String, V> cache = map.get(delegator.getDelegatorName());
		if (cache == null) {
			cache = CacheBuilder.newBuilder().maximumSize(maximumSizeDefault).expireAfterAccess(timeCacheDefault, TimeUnit.MINUTES)
					.build(new OlbiusCacheLoader<String, V>(delegator) {
						@Override
						public V load(String key) throws Exception {
							return loadCache(delegator, key);
						}

					});
			map.put(delegator.getDelegatorName(), cache);
		}
		return cache;
	}

	public abstract V loadCache(Delegator delegator, String key) throws Exception;

	public V get(Delegator delegator, String key) {
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

	public static abstract class OlbiusCacheLoader<K, V> extends CacheLoader<K, V> {

		protected Delegator delegator;

		public OlbiusCacheLoader(Delegator delegator) {
			this.delegator = delegator;
		}
	}

}
