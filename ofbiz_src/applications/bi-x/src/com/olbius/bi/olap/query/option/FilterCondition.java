package com.olbius.bi.olap.query.option;

import java.util.Map;

import com.olbius.bi.olap.query.condition.Condition;

public interface FilterCondition {
	
	public final static String EMPTY = "EMPTY";
	public final static String NOT_EMPTY = "NOT_EMPTY";
	public final static String CONTAINS = "CONTAINS";
	public final static String CONTAINS_CASE_SENSITIVE = "CONTAINS_CASE_SENSITIVE";
	public final static String DOES_NOT_CONTAIN = "DOES_NOT_CONTAIN";
	public final static String DOES_NOT_CONTAIN_CASE_SENSITIVE = "DOES_NOT_CONTAIN_CASE_SENSITIVE";
	public final static String STARTS_WITH = "STARTS_WITH";
	public final static String STARTS_WITH_CASE_SENSITIVE = "STARTS_WITH_CASE_SENSITIVE";
	public final static String ENDS_WITH = "ENDS_WITH";
	public final static String ENDS_WITH_CASE_SENSITIVE = "ENDS_WITH_CASE_SENSITIVE";
	public final static String EQUAL = "EQUAL";
	public final static String EQUAL_CASE_SENSITIVE = "EQUAL_CASE_SENSITIVE";
	public final static String NULL = "NULL";
	public final static String NOT_NULL = "NOT_NULL";
	public final static String NOT_EQUAL = "NOT_EQUAL";
	public final static String LESS_THAN = "LESS_THAN"; 
	public final static String LESS_THAN_OR_EQUAL = "LESS_THAN_OR_EQUAL";
	public final static String GREATER_THAN = "GREATER_THAN";
	public final static String GREATER_THAN_OR_EQUAL = "GREATER_THAN_OR_EQUAL";
	
	void registry(Map<String, FilterCondition> map);
	
	Condition makeCondition(String name, Object value);
}
