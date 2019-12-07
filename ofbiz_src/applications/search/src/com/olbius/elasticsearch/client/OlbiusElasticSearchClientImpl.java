package com.olbius.elasticsearch.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.olbius.elasticsearch.object.Data;
import com.olbius.entity.tenant.OlbiusTenant;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

public class OlbiusElasticSearchClientImpl implements OlbiusElasticSearchClient {

	public static final String module = OlbiusElasticSearchClientImpl.class.getName();

	private final JestClientFactory clientFactory;

	private final Map<String, IndexAsync> executor = new HashMap<String, IndexAsync>();
	
	public OlbiusElasticSearchClientImpl(JestClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public JestClient jest() {
		return clientFactory.getObject();
	}

	@Override
	public SearchResult query(String index, String type, String query) {
		//index = "customer";
		Debug.log("SearchResult::query, index = " + index + ", type = " + type + ", query = " + query);
		Search search = new Search.Builder(query).addIndex(getIndexName(index)).addType(type).build();

		SearchResult result = null;

		try {
			result = jest().execute(search);
		} catch (IOException e) {
			Debug.logError(e, module);
		}
		return result;
	}

	@Override
	public String getIndexName(String s) {
		return OlbiusTenant.getTenantId(null) + "_" + s;
	}

	@Override
	public void createIndex(String s) throws IOException {
		jest().execute(new CreateIndex.Builder(getIndexName(s)).build());
	}

	@Override
	public void putMapping(String index, String type, String json) throws IOException {
		createIndex(index);
		PutMapping mapping = new PutMapping.Builder(getIndexName(index), type, json).build();
		jest().execute(mapping);
	}

	private IndexExecutor getExecutor(String key) {
		if (executor.get(key) == null) {
			executor.put(key, new IndexAsync(this));
		}
		return executor.get(key);
	}
	
	private String getKeyExecutor(String index, String type) {
		return getIndexName(index) + "_" + type;
	}
	
	@Override
	public IndexExecutor indexData(Data data) {
		Index tmp = new Index.Builder(data.getData()).index(getIndexName(data.getIndex())).type(data.getType()).build();
		IndexExecutor executor = getExecutor(getKeyExecutor(data.getIndex(), data.getType()));
		executor.addData(tmp);
		return executor;
	}
	
	public static class IndexAsync implements IndexExecutor {
		
		private Builder builder;
		private int batch = 0;
		private int commitSize = 100;
		private OlbiusElasticSearchClient client;
		
		public IndexAsync(OlbiusElasticSearchClient client) {
			this.client = client;
		}
		
		@Override
		public void addData(Index index) {
			if (builder == null || batch == 0) {
				builder = new Bulk.Builder();
			}
			if (index != null) {
				builder.addAction(index);
				batch++;
			}

			if (batch >= commitSize) {
				commit();
			}
		}
		
		@Override
		public void commit() {
			if (batch > 0) {
				client.jest().executeAsync(builder.build(), new JestResultHandler<JestResult>() {
					public void completed(JestResult result) {
						Debug.logInfo("Commit is completed", module);
					}

					public void failed(Exception ex) {
						Debug.logError(ex, module);
					}
				});
				batch = 0;
			}
		}
	}

}
