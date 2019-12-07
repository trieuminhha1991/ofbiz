package com.olbius.elasticsearch;

public class ElasticIndexFactory {

	public static ElasticIndex getInstance() {
		return new ElasticIndexImpl();
	}
	
}
