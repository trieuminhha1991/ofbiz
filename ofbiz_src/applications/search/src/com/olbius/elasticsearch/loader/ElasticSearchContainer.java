package com.olbius.elasticsearch.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.json.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;

import com.olbius.elasticsearch.client.ElasticSearchClientFactory;
import com.olbius.elasticsearch.client.ElasticSearchClientFactoryImpl;
import com.olbius.elasticsearch.client.OlbiusElasticSearchClient;

public class ElasticSearchContainer implements Container {

	public static final String module = ElasticSearchContainer.class.getName();

	public static final ElasticSearchClientFactory FACTORY = new ElasticSearchClientFactoryImpl("elasticSearch");

	private final String indexFolder = "applications/search/index/";

	private String CONTAINER_NAME;

	@Override
	public void init(String[] args, String name, String configFile) throws ContainerException {
		CONTAINER_NAME = name;
		Debug.logInfo("Initializing " + CONTAINER_NAME, module);
	}

	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	@Override
	public boolean start() throws ContainerException {

		File dir = new File(indexFolder);

		String[] files = new String[] {};

		if (dir.isDirectory()) {
			files = dir.list();
		}

		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".json")) {
				String index = files[i].substring(0, files[i].length() - 5);

				try {
					String s = new Scanner(new File(indexFolder + files[i])).useDelimiter("\\Z").next();

					Map map = JSON.from(s).toObject(Map.class);

					for (Object key : map.keySet()) {
						if (key instanceof String) {
							
							Map type = (Map)map.get(key);
							
							List versions = (List) type.get("version");
							
							if (versions == null || versions.isEmpty()) {
								versions = new ArrayList<String>();
								versions.add(ESVersion.ES_5);
							}
							
							type.remove("version");
							
							String json = JSON.from(UtilMisc.toMap(key, type)).toString();
							
							for (Object version : versions) {
								if (version instanceof String) {
									OlbiusElasticSearchClient client = FACTORY.client((String) version);
									if (client == null) {
										continue;
									}
									client.putMapping(index, (String) key, json);
									
								}
							}
						}
					}

				} catch (IOException e) {
					Debug.logError(e, module);
				}

			}
		}

		return true;
	}

	@Override
	public void stop() throws ContainerException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		return CONTAINER_NAME;
	}

}
