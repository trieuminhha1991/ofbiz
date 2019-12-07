package com.olbius.bi.olap.query.function;

import com.olbius.bi.olap.query.AbstractFunction;

public class Min extends AbstractFunction {

	public Min(String s) {
		set(s);
	}
	
	@Override
	protected String func() {
		return "MIN";
	}

}
