package com.olbius.elasticsearch.query;

import io.searchbox.core.SearchResult;

public interface ElasticSearchQuery {

	SearchResult query(String index, String type, String query);
	
}
