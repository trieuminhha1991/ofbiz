package com.olbius.bi.olap.query.function;

import java.util.HashMap;
import java.util.Map;

import com.olbius.bi.olap.query.AbstractFunction;

public class Case extends AbstractFunction{

	private Map<Object, Object> map;
	private Object els;
	
	public Case() {
		map = new HashMap<Object, Object>();
	}
	
	public Case when(Object conditon, Object value) {
		map.put(conditon, value);
		return this;
	}
	
	public Case els(Object value) {
		els = value;
		return this;
	}
	
	@Override
	protected String func() {
		return "CASE";
	}

	@Override
	public String toString() {
		if(!map.isEmpty()) {
			String tmp = func();
			for(Object key : map.keySet()) {
				
				tmp += " WHEN " + key.toString();
				tmp += " THEN " + map.get(key).toString();
				
			}
			
			if(els != null) {
				tmp += " ELSE " + els.toString();
			}
			
			tmp += " END";
			
			return tmp;
		} else {
			return "";
		}
	}
	
}
