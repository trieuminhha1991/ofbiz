package com.olbius.bi.olap.query;

import java.util.ArrayList;
import java.util.List;

public class InnerJoin implements Join {

	private String table;
	
	private String as;
	
	private List<String> conditions;
	
	public InnerJoin() {
		conditions = new ArrayList<String>();
	}
	
	@Override
	public void setTable(String table, String as) {
		this.table = table;
		this.as = as;
	}

	@Override
	public void addCondition(String s, boolean flag) {
		if(flag)
			conditions.add(s);
	}

	@Override
	public void addConditionBetween(String name, String s, String s1, boolean flag) {
		if(flag)
			conditions.add("(" + name + " BETWEEN " + s + " AND " + s1 + ")");
	}
	
	@Override
	public String toString() {
		if(table == null || table.isEmpty() || conditions.isEmpty()) {
			return null;
		}
		String s = "INNER JOIN " + table;
		if(as != null && !as.isEmpty()) {
			s += " AS " + as;
		}
		s += " ON ";
		for(int i = 0; i < conditions.size(); i++) {
			s += conditions.get(i);
			if(i < conditions.size()-1) {
				s += " AND ";
			}
		}
		return s;
	}

}
