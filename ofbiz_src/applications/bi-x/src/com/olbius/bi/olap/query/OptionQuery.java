package com.olbius.bi.olap.query;

import java.util.Map;

public interface OptionQuery {

	void setQuery(OlapQuery query);
	
	void setParameters(Map<String, Object> parameters);
	
	void setParam(String param);
	
	boolean checkParam();
	
}
