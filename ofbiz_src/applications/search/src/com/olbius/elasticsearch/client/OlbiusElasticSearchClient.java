package com.olbius.elasticsearch.client;

import com.olbius.elasticsearch.query.ElasticSearchQuery;

import io.searchbox.client.JestClient;

public interface OlbiusElasticSearchClient extends ElasticSearchQuery, OlbiusElasticSearchIndex {

	JestClient jest();
	
}
