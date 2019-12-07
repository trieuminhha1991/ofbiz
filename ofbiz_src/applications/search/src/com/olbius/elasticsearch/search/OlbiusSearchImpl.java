package com.olbius.elasticsearch.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;

import com.olbius.elasticsearch.SearchClientFactory;
import com.olbius.elasticsearch.services.ProductIndex;
import com.olbius.entity.tenant.OlbiusTenant;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

public class OlbiusSearchImpl implements OlbiusSearch {

	private String index;
	private String type;

	private long total;
	
	private int offset;
	
	private int limit;
	
	@Override
	public void setIndex(Delegator delegator, String index) {
		this.index = OlbiusTenant.getTenantId(delegator) + "_" + index.toLowerCase();
	}

	@Override
	public String getIndex() {
		return index;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public long getTotal() {
		return total;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public long getLimit() {
		return limit;
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public List<?> search(Map<String, String> searchParams, Map<String, String> filter) {

		if (index == null) {
			Debug.logError("Index name is null", module);
			return null;
		}

		if (type == null) {
			Debug.logError("Type name is null", module);
			return null;
		}

		JestClient jestClient = SearchClientFactory.getInstance().getJestClient();

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.from(offset);
		
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		
		for(String s: searchParams.keySet()) {
			boolQueryBuilder.must(QueryBuilders.matchQuery(s, searchParams.get(s)));
		}
		
		if(filter !=null && !filter.isEmpty()) {
			for(String s: filter.keySet()) {
				boolQueryBuilder.must(QueryBuilders.matchQuery(s, filter.get(s)));
			}
		}
		
		searchSourceBuilder.query(boolQueryBuilder);
		
		if(limit != 0) {
			searchSourceBuilder.size(limit);
		}
		
		JestResult result = null;
		try {
			Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type).build();
			result = jestClient.execute(search);
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		List<ProductIndex> notes = null;
		if(result != null) {
			notes = result.getSourceAsObjectList(ProductIndex.class);
			total = result.getJsonObject().get("hits").getAsJsonObject().get("total").getAsLong();
		} else {
			notes = new ArrayList<ProductIndex>();
			total = 0;
		}
				
		return notes;
	}

}
