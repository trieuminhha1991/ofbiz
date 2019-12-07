package com.olbius.elasticsearch;

import java.io.IOException;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;

import com.olbius.elasticsearch.loader.ElasticSearchContainer;
import com.olbius.elasticsearch.loader.ESVersion;
import com.olbius.entity.tenant.OlbiusTenant;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

/**
 * @author Nguyen Ha
 *
 */
public class ElasticIndexImpl implements ElasticIndex {

	private String index;
	private String type;
	private int shards;
	private int replicas;

	private int commitSize;

	private int batch;

	private Builder builder;

	//private String version = ESVersion.ES_5;
	private String version = ESVersion.ES_1_4;
	
	public ElasticIndexImpl() {
		this.shards = 3;
		this.replicas = 0;
		this.commitSize = 100;
		this.batch = 0;
	}

	@Override
	public void createIndex() {

		if (index == null) {
			Debug.logError("Index name is null", module);
			return;
		}

		JestClient jestClient = ElasticSearchContainer.FACTORY.client(version).jest();
		try {
			jestClient.execute(new CreateIndex.Builder(index).build());
		} catch (IOException e) {
			Debug.logError(e, module);
		}
	}

	@Override
	public void deleteIndex() {

		if (index == null) {
			Debug.logError("Index name is null", module);
			return;
		}

		JestClient jestClient = ElasticSearchContainer.FACTORY.client(version).jest();
		DeleteIndex deleteIndex = new DeleteIndex.Builder(index).build();
		try {
			jestClient.execute(deleteIndex);
		} catch (IOException e) {
			Debug.logError(e, module);
		}
	}

	@Override
	public JestResult execute() {
		return null;
	}

	@Override
	public int getCommitSize() {
		return commitSize;
	}

	@Override
	public String getIndex() {
		return index;
	}

	private Index getIndexData(Object data) {
		if (index == null) {
			Debug.logError("Index name is null", module);
			return null;
		}

		if (type == null) {
			Debug.logError("Type name is null", module);
			return null;
		}
		Index tmp = new Index.Builder(data).index(index).type(type).build();

		return tmp;
	}

	@Override
	public int getReplicas() {
		return replicas;
	}

	@Override
	public int getShards() {
		return shards;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void indexData(Object data) {
		indexDatas(data);
		commit();
	}

	@Override
	public void indexDatas(List<Object> data) {

		for (Object object : data) {
			indexDatas(object);
		}
		commit();
	}

	@Override
	public void setCommitSize(int commitSize) {
		this.commitSize = commitSize;
	}

	@Override
	public void setIndex(Delegator delegator, String index) {
		this.index = OlbiusTenant.getTenantId(delegator) + "_" + index.toLowerCase();
	}

	@Override
	public void setReplicas(int replicas) {
		this.replicas = replicas;
	}

	@Override
	public void setShards(int shards) {
		this.shards = shards;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void indexDatas(Object data) {
		if (builder == null || batch == 0) {
			builder = new Bulk.Builder();
		}

		Index tmp = getIndexData(data);
		if (tmp != null) {
			builder.addAction(tmp);
			batch++;
		}

		if (batch >= commitSize) {
			commit();
		}
	}

	@Override
	public void commit() {
		Debug.log(module + "::commit, batch = " + batch);
		if (batch > 0) {
			Debug.log(module + "::commit, batch = " + batch + ", init jestClient");
			
			JestClient jestClient = ElasticSearchContainer.FACTORY.client(version).jest();
			jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
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

	@Override
	public void setElasticVeriosn(String version) {
		this.version = version;
	}

}
