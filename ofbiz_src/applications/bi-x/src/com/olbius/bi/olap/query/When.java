package com.olbius.bi.olap.query;

import java.util.HashMap;
import java.util.Map;

public class When {
	
	private Map<String, String> when;
	private String _else;
	private String as;
	
	public When() {
		when = new HashMap<String, String>();
	}

	public void addWhen(String when, String then, boolean flag) {
		if(flag) {
			this.when.put(when, then);
		}
	}
	
	public void setElse(String s) {
		_else = s;
	}
	
	public void setAs(String s) {
		as = s;
	}
	
	@Override
	public String toString() {
		if(when.isEmpty() || as == null || as.isEmpty()) {
			return null;
		}
		String s = "CASE ";
		for(String w : when.keySet()) {
			s+= "WHEN " + w + " THEN " + when.get(w) + " ";
		}
		if(_else != null && !_else.isEmpty()) {
			s+= "ELSE " + _else + " ";
		}
		s+= "END AS" + as;
		return s;
	}
	
}
