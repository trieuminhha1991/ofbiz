package com.olbius.bi.olap.query.join;

import java.util.List;

import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;

public interface Join {

	public final static String INNER_JOIN = "INNER JOIN";
	public final static String LEFT_OUTER_JOIN = "LEFT OUTER JOIN";
	public final static String RIGHT_OUTER_JOIN = "RIGHT OUTER JOIN";
	public final static String FULL_OUTER_JOIN = "FULL OUTER JOIN";
	
	Join table(String tb, String as);
	
	Join table(String tb);
	
	Join table(OlapQuery query, String as);
	
	Condition on(Condition condition);
	
	List<Object> getConditionValues();
	
	String getTable();
	
}
