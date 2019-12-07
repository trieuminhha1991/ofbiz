package com.olbius.elasticsearch.client;

import io.searchbox.core.Index;

public interface IndexExecutor {
	
	void commit();

	void addData(Index index);
	
}
