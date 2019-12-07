package com.olbius.jackrabbit.api;

import java.util.List;

import javax.jcr.RepositoryException;

public interface Search {

	public static final String DESC = "DESC";
	public static final String ASC = "ASC";
	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String NOT = "NOT";
	public static final String LIKE = "LIKE";
	public static final String ISCHILDNODE = "ISCHILDNODE";
	public static final String ISDESCENDANTNODE = "ISDESCENDANTNODE";
	public static final String CONTAINS = "CONTAINS ";
	public static final String LENGTH = "LENGTH";
	public static final String NAME = "NAME ";
	public static final String LOCALNAME = "LOCALNAME ";
	public static final String SCORE = "SCORE";
	public static final String LOWER = "LOWER";
	public static final String UPPER = "UPPER";

	public void setLimit(int i);
	
	public List<String> search(String sql) throws RepositoryException;

	public String[] column() throws RepositoryException;

	public String getQuery(String select, String from, String where, String order);

	public String getCondition(String dynamicOperand, String operand, Object staticOperand);

	public String getConstraint(String s1, String s2, String s3, String s4);

	public String getConstraint(String s1, String s2, String s3);
	
	public String getConstraint(boolean not, String s); 
	
	public String getConstraint(String s1, String s2, String s3, boolean group);

	public String CONTAINS(String selectorName, String propertyName, String fulltextSearchExpression);

	public String ISCHILDNODE(String selectorName, String path);

	public String ISDESCENDANTNODE(String selectorName, String path);

	public String NAME(String selectorName);

	public String LOCALNAME(String selectorName);

	public String SCORE(String selectorName);

	public String LOWER(String dynamicOperand);

	public String UPPER(String dynamicOperand);

	public String DynamicOperand(String s1, String s2);

	public String DynamicOperand(String s1, String s2, String s3);

	public String LENGTH(String selectorName, String propertyName);
}
