package com.olbius.bi.olap.query.option;

import java.util.Map;

import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OptionQuery;

public abstract class AbstractOptionQuery implements OptionQuery {

	protected OlapQuery query;
	protected Map<String, Object> parameters;
	protected String param;

	@Override
	public void setQuery(OlapQuery query) {
		this.query = query;
	}

	@Override
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public void setParam(String param) {
		this.param = param;
	}
	
}
