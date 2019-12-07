package com.olbius.security.core.cache;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.entity.cache.OlbiusCache;

public class PermissionCache extends  OlbiusCache<List<String>>{

	@Override
	public List<String> loadCache(Delegator delegator, String key) throws Exception {
		List<String> list = new ArrayList<String>();

		List<GenericValue> values = delegator.findByAnd("OlbiusPermission", UtilMisc.toMap("permissionId", key),
				null, false);

		for (GenericValue value : values) {
			list.add(value.getString("permissionIncludeId"));
		}

		return list;
	}

}
