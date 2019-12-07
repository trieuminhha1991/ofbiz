package com.olbius.salesmtl.common;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.DispatchContext;

import com.olbius.elasticsearch.SearchClientFactory;
import com.olbius.elasticsearch.loader.ESVersion;
import com.olbius.elasticsearch.loader.ElasticSearchContainer;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import javolution.util.FastMap;

public class SearchServices {
	public static String module = SearchServices.class.getName();
	public static Map<String, Object> elasticSearchCustomer(DispatchContext dpc, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		try {
			String query = (String) context.get("query");
			String channel = (String) context.get("channel");
			Debug.log(module + "::ElasticSearch  START");
			
			result.put("data", SearchEngine.search(query, channel, "venues"));
			Debug.log(module + "::ElasticSearch  END");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> elasticSearchProduct(DispatchContext dpc, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		try {
			String query = (String) context.get("query");
			result.put("data", SearchEngine.search(query, "product", "venues"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static class SearchEngine {
		@SuppressWarnings({ "rawtypes", "deprecation" })
		public static Map search(String query, String channel, String type) {
			SearchResult result = ElasticSearchContainer.FACTORY.client(ESVersion.ES_1_4).query(channel, type, query);
			if (result != null) {
				return result.getJsonMap();
			} else {
				return new HashMap<>();
			}
		}
	}
}
