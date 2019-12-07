package com.olbius.elasticsearch.client;

public interface ElasticSearchClientFactory {

	OlbiusElasticSearchClient client(String version);
	
	OlbiusElasticSearchClient client();
	
}
