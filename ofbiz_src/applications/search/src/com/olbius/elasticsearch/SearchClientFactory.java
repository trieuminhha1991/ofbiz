package com.olbius.elasticsearch;

import org.ofbiz.base.util.UtilProperties;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

/**
 * @author Nguyen Ha
 *
 */
public class SearchClientFactory {

	private static SearchClientFactory client;
	
	private final JestClientFactory jestClientFactory;
	
	private final String host = UtilProperties.getPropertyValue("elasticSearch", "elastic.host", "http://localhost:49200");
	
	public SearchClientFactory() {
		 HttpClientConfig clientConfig = new HttpClientConfig
                 .Builder(host)
                 .multiThreaded(true)
                 .build();
		 jestClientFactory = new JestClientFactory();
		 jestClientFactory.setHttpClientConfig(clientConfig);
	}
	
	public static SearchClientFactory getInstance() {
		if(client == null) {
			client = new SearchClientFactory();
		}
		return client;
	}
	
	public JestClient getJestClient() {
		return jestClientFactory.getObject();
	}
}
