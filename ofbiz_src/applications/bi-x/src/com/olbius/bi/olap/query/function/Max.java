package com.olbius.bi.olap.query.function;

import com.olbius.bi.olap.query.AbstractFunction;

public class Max extends AbstractFunction{

	public Max(String s) {
		set(s);
	}
	
	@Override
	protected String func() {
		return "MAX";
	}

}
