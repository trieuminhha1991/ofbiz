package com.olbius.bi.olap.query.function;

import com.olbius.bi.olap.query.AbstractFunction;
import com.olbius.bi.olap.query.Function;

public class Count extends AbstractFunction{

	private boolean distinct;
	private String column;
	
	public Count(String s) {
		this.column = s;
		set(s);
	}
	
	@Override
	protected String func() {
		if(distinct) {
			set("DISTINCT " + column);
		}
		return "COUNT";
	}

	public Function distinct() {
		this.distinct = true;
		return this;
	}
	
}
