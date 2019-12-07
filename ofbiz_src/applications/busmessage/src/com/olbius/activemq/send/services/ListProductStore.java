package com.olbius.activemq.send.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.service.OlbiusService;

public class ListProductStore implements OlbiusService {

	@Override
	public Map<String, Object> run(DispatchContext dctx, Map<String, Object> context) throws Exception {

		Map<String, Object> result = ServiceUtil.returnSuccess();

		List<GenericValue> values = dctx.getDelegator().findByAnd("ProductStore", null, null, true);

		List<Map<String, String>> data = new ArrayList<>();

		for (GenericValue value : values) {
			Map<String, String> map = new HashMap<>();
			map.put("productStoreId", value.getString("productStoreId"));
			map.put("productStoreName", value.getString("storeName") != null ? value.getString("storeName")
					: value.getString("productStoreId"));
			data.add(map);
		}

		result.put("data", data);
		
		return result;
	}

}
