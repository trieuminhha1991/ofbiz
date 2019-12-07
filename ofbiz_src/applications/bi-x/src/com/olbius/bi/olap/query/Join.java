package com.olbius.bi.olap.query;

public interface Join {

	void setTable(String table, String as);
	
	void addCondition(String condition, boolean flag);
	
	void addConditionBetween(String name, String s, String s1, boolean flag);
	
}
