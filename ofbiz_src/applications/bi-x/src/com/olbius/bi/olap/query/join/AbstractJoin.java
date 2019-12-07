package com.olbius.bi.olap.query.join;

import java.util.ArrayList;
import java.util.List;

import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;

public abstract class AbstractJoin implements Join{

	private String table;
	
	private String as;
	
	private Condition conditions;
	
	private OlapQuery query;
	
	public static Join make(String join) {
		if(INNER_JOIN.equals(join)) {
			return new InnerJoin();
		}
		if(LEFT_OUTER_JOIN.equals(join)) {
			return new LeftJoin();
		}
		if(RIGHT_OUTER_JOIN.equals(join)) {
			return new RightJoin();
		}
		if(FULL_OUTER_JOIN.equals(join)) {
			return new FullJoin();
		}
		return null;
	}
	
	public abstract String getJoinName();
	
	@Override
	public Join table(String tb) {
		return table(tb, null);
	}
	
	@Override
	public Join table(String tb, String as) {
		this.table = tb;
		this.as = as;
		this.query = null;
		return this;
	}

	@Override
	public Join table(OlapQuery query, String as) {
		if(as == null || as.isEmpty()) {
			return null;
		}
		this.table = null;
		this.as = as;
		this.query = query;
		return this;
	}
	
	@Override
	public Condition on(Condition condition) {
		this.conditions = condition;
		return this.conditions;
	}
	
	@Override
	public List<Object> getConditionValues() {
		List<Object> tmp = new ArrayList<Object>();
		if(this.query != null) {
			tmp.addAll(this.query.getConditionValues());
		}
		tmp.addAll(this.conditions.getConditionValues());
		return tmp;
	}
	
	@Override
	public String toString() {
		if(conditions == null) {
			return null;
		}
		String s = getJoinName()+ " ";
		
		if(query != null) {
			s += "(" + query.toString()  + ")";
		} else if (table !=null && !table.isEmpty()) {
			s += table;
		} else {
			return null;
		}
		
		if(as != null && !as.isEmpty()) {
			s += " AS " + as;
		}
		s += " ON ";
		s += conditions.toString();
		return s;
	}
	
	@Override
	public String getTable() {
		if(as != null && !as.isEmpty()) {
			return as;
		}
		return table;
	}
}
