package com.olbius.elasticsearch;

import java.util.List;

import org.ofbiz.entity.Delegator;

import io.searchbox.client.JestResult;

/**
 * @author Nguyen Ha
 *
 */
public interface ElasticIndex {

	public static final String module = ElasticIndex.class.getName();

	void setIndex(Delegator delegator, String index);

	String getIndex();

	void setType(String type);

	String getType();

	void setShards(int shards);

	int getShards();

	void setReplicas(int replicas);

	int getReplicas();

	void createIndex();

	void deleteIndex();

	void indexData(Object data);
	
	void indexDatas(List<Object> data);
	
	void indexDatas(Object data);

	void setCommitSize(int commitSize);

	int getCommitSize();
	
	JestResult execute();
	
	void commit();
	
	void setElasticVeriosn(String version);

}
