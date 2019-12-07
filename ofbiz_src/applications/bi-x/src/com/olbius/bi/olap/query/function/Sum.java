package com.olbius.bi.olap.query.function;

import com.olbius.bi.olap.query.AbstractFunction;

public class Sum extends AbstractFunction{
	
	public Sum(String s) {
		set(s);
	}
	
	public Sum(String s, String s1, String s2) {
		
		String tmp = "CASE WHEN " + s + " THEN " + s1 + " ELSE " + s2 + " END";
		
		set(tmp);
	}

	@Override
	protected String func() {
		return "SUM";
	}
	
}
