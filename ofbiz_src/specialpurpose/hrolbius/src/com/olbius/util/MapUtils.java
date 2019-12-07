package com.olbius.util;

import java.util.Iterator;
import java.util.Map;

import javolution.util.FastMap;

public class MapUtils{
	public static Map<String, Object> copy(Map<String, Object> obj) {
		Map<String, Object> result = FastMap.newInstance();
		for (Iterator<String> iterator = obj.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			result.put(key, obj.get(key));
		}
		return result;
	}
}
