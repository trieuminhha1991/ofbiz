package com.olbius.elasticsearch.client;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;

import com.olbius.elasticsearch.loader.ESVersion;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class ElasticSearchClientFactoryImpl implements ElasticSearchClientFactory {

	public static final String module = ElasticSearchClientFactoryImpl.class.getName();
	
	private final Map<String, OlbiusElasticSearchClient> clientFactories = new HashMap<String, OlbiusElasticSearchClient>();

	private final String resources;
	
	private final String defaultVersion = ESVersion.ES_5;
	
	public ElasticSearchClientFactoryImpl(String resources) {
		this.resources = resources;
	}

	@Override
	public OlbiusElasticSearchClient client(String version) {
		try {
			return getClient(version);
		} catch (Exception e) {
			Debug.logError(e, module);
			return null;
		}
	}
	
	protected OlbiusElasticSearchClient getClient (String version) throws Exception {
		OlbiusElasticSearchClient clientFactory = clientFactories.get(version);
		if (clientFactory == null) {
			String host = UtilProperties.getPropertyValue(resources, version, null);
			
			if (host == null) {
				throw new Exception("ElasticSearch " + version + " client config not found!");
			}
			Debug.log(module + "::getClient START");
			HttpClientConfig clientConfig = new HttpClientConfig.Builder(host).multiThreaded(true).build();
			Debug.log(module + "::getClient END");
			
			JestClientFactory jestClientFactory = new JestClientFactory();
			jestClientFactory.setHttpClientConfig(clientConfig);
			clientFactory = new OlbiusElasticSearchClientImpl(jestClientFactory);
			clientFactories.put(version, clientFactory);
		}
		return clientFactory;
	}

	@Override
	public OlbiusElasticSearchClient client() {
		return client(defaultVersion);
	}

}
