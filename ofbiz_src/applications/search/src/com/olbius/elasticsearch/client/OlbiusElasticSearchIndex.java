package com.olbius.elasticsearch.client;

import java.io.IOException;

import com.olbius.elasticsearch.object.Data;

public interface OlbiusElasticSearchIndex {

	String getIndexName(String s);
	
	void createIndex(String s) throws IOException;
	
	void putMapping(String index, String type, String mapping) throws IOException;
	
	IndexExecutor indexData(Data data);
}
