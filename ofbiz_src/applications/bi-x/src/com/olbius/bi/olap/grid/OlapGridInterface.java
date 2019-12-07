package com.olbius.bi.olap.grid;

import java.util.List;
import java.util.Map;

public interface OlapGridInterface {

	List<String> getDataFields();
	
	List<Map<String, Object>> getData();
	
	String getId();
	
	void setId(String id);
}
