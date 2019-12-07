package com.olbius.elasticsearch.search;

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;

public interface OlbiusSearch {
	
	public static final String module = OlbiusSearch.class.getName();
	
	void setIndex(Delegator delegator, String index);
	
	String getIndex();
	
	void setType(String type);
	
	String getType();
	
	List<? extends Object> search(Map<String, String> search, Map<String, String> filter);

	long getTotal();

	long getOffset();

	void setOffset(int offset);

	long getLimit();

	void setLimit(int limit);
}
