package com.olbius.elasticsearch.search;

public class OlbiusSearchFactory {

	public static OlbiusSearch getInstance() {
		return new OlbiusSearchImpl();
	}
	
}
