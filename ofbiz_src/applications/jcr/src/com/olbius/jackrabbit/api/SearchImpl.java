package com.olbius.jackrabbit.api;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

public class SearchImpl implements Search {

	private Session session;
	
	private QueryManager manager;
	
//	private String select;
//	
//	private String from;
//	
//	private String where;
//	
//	private String order;
	
	private int limit;
	
	public SearchImpl(Session session) throws RepositoryException {
		this.session = session;
		this.manager = session.getWorkspace().getQueryManager();
	}

	@Override
	public List<String> search(String sql) throws RepositoryException {
		
		Query query = manager.createQuery(sql, Query.JCR_SQL2);

		if(limit > 0) {
			query.setLimit(limit);
		}
		
		QueryResult sqlResult = query.execute();

		NodeIterator nodes = sqlResult.getNodes();

		List<String> nodeMatches = new ArrayList<String>();

		while(nodes.hasNext()){
			Node node = nodes.nextNode();
			nodeMatches.add(node.getPath());
		}
		return nodeMatches;
	}
	
	@Override
	public String[] column() throws RepositoryException {
		
//		QueryManager manager = session.getWorkspace().getQueryManager();
//
//		String sqlQuery = "SELECT "+ select + " FROM " + from;
//		
//		Query query = manager.createQuery(sqlQuery, Query.JCR_SQL2);
//
//		QueryResult sqlResult = query.execute();
//
//		return sqlResult.getColumnNames();
		return null;
	}
	
	@Override
	public void setLimit(int i) {
		this.limit = i;
	}

	@Override
	public String getQuery(String select, String from, String where, String order) {
		String sql = null;
		if(select != null) {
			sql = "SELECT " + select + " FROM " + from;
		} else {
			sql = "SELECT * FROM " + from;
		}
		if(where != null) {
			sql += " WHERE " + where;
		}
		if(order != null && (order.equals(ASC) || order.equals(DESC))) {
			sql += " ORDER BY " + order;
		}
		return sql;
	}

	@Override
	public String NAME(String selectorName) {
		if(selectorName == null) {
			return "NAME()";
		}
		return "NAME("+selectorName+")";
	}

	@Override
	public String LOCALNAME(String selectorName) {
		if(selectorName == null) {
			return "LOCALNAME()";
		}
		return "LOCALNAME("+selectorName+")";
	}

	@Override
	public String SCORE(String selectorName) {
		if(selectorName == null) {
			return "SCORE()";
		}
		return "SCORE("+selectorName+")";
	}

	@Override
	public String LOWER(String dynamicOperand) {
		return "LOWER("+dynamicOperand+")";
	}

	@Override
	public String UPPER(String dynamicOperand) {
		return "UPPER("+dynamicOperand+")";
	}

	@Override
	public String DynamicOperand(String s1, String s2) {
		if(s1.equals(NAME)) {
			return NAME(s2);
		} else if(s1.equals(LOCALNAME)) {
			return LOCALNAME(s2);
		} else if(s1.equals(SCORE)) {
			return SCORE(s2);
		} else if(s1.equals(LOWER)) {
			return LOWER(s2);
		} else if(s1.equals(UPPER)) {
			return UPPER(s2);
		}
		return s1+"."+s2;
	}

	@Override
	public String DynamicOperand(String s1, String s2, String s3) {
		if(s1.equals(LENGTH)) {
			return LENGTH(s2, s3);
		}
		return "";
	}

	@Override
	public String LENGTH(String selectorName, String propertyName) {
		return "LENGTH("+selectorName+"."+propertyName+")";
	}

	@Override
	public String getCondition(String dynamicOperand, String operand, Object staticOperand) {
		if(staticOperand instanceof String) {
			return dynamicOperand + " " + operand + " '" + staticOperand + "'";
		}
		return dynamicOperand + " " + operand + " " + staticOperand;
	}

	@Override
	public String getConstraint(String s1, String s2, String s3, String s4) {
		if(s1.equals(CONTAINS)) {
			return CONTAINS(s2, s3, s4);
		}
		return null;
	}

	@Override
	public String getConstraint(String s1, String s2, String s3) {
		if(s1.equals(ISCHILDNODE)) {
			return ISCHILDNODE(s2, s3);
		} else if(s1.equals(ISDESCENDANTNODE)) {
			return ISDESCENDANTNODE(s2, s3);
		} else if(s1.equals(CONTAINS)) {
			return CONTAINS(s2, null, s3);
		}
		return "";
	}

	@Override
	public String CONTAINS(String selectorName, String propertyName, String fulltextSearchExpression) {
		if(propertyName != null) {
			return "CONTAINS("+selectorName+"."+propertyName+",'"+fulltextSearchExpression+"')";
		} else {
			return "CONTAINS("+selectorName+".*,'"+fulltextSearchExpression+"')";
		}
	}

	@Override
	public String ISCHILDNODE(String selectorName, String path) {
		return "ISCHILDNODE("+selectorName+",'"+path+"')";
	}

	@Override
	public String ISDESCENDANTNODE(String selectorName, String path) {
		return "ISDESCENDANTNODE("+selectorName+",'"+path+"')";
	}

	@Override
	public String getConstraint(String s1, String s2, String s3, boolean group) {
		String s = "";
		if(s1.equals(AND)) {
			s = s2 + " AND " + s3;
		} else if(s1.equals(OR)) {
			s = s2 + " OR " + s3;
		}
		if(group) {
			s = "("+s+")";
		}
		return s;
	}

	@Override
	public String getConstraint(boolean not ,String s) {
		if(not) {
			return "NOT " + s;
		} else {
			return s;
		}
	}
}
