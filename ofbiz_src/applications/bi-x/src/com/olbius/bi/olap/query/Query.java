package com.olbius.bi.olap.query;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;

public class Query implements OlapQuery{

	private SQLProcessor processor;
	
	private Map<String, String> select;
	private String from;
	private String as;
	private List<Join> joins;
	private List<String> conditions;
	private List<Object> conditionValues;
	private List<String> groupBy;
	private List<String> orderBy;
	private List<String> distinct;
	private long limit;
	private long offset;
	
	private boolean executeFlag;
	
	public Query() {
		init();
	}
	
	@Override
	public List<Object> getConditionValues() {
		return this.conditionValues;
	}
	
	public Query(SQLProcessor processor) {
		init();
		this.processor = processor;
	}
	
	private void init() {
		select = new HashMap<String, String>();
		distinct = new ArrayList<String>();
		joins = new ArrayList<Join>();
		conditions = new ArrayList<String>();
		conditionValues = new ArrayList<Object>();
		groupBy = new ArrayList<String>();
		orderBy = new ArrayList<String>();
		limit = -1;
		offset = -1;
		executeFlag = false;
	}
	
	public void addDistinct(String d, boolean flag) {
		if(flag) {
			distinct.add(d);
		}
	}
	
	public void addDistinct(String d) {
		addDistinct(d, true);
	}
	
	@Override
	public OlapQuery limit(long limit) {
		this.limit = limit;
		return this;
	}
	
	public void setFrom(String from, String as) {
		this.from = from;
		this.as = as;
	}
	
	public void setFrom(String from) {
		setFrom(from, null);
	}

	public void setFrom(OlapQuery query, String as) {
		String sql = query.toString();
		setFrom("("+sql+")", as);
		conditionValues.addAll(0, query.getConditionValues());
	}
	
	public void addSelect(String name, String as, boolean flag) {
		if(flag)
			select.put(name, as);
	}
	
	public void addSelect(String name, String as, boolean groupBy, boolean flag) {
		if(flag) {
			addSelect(name, as, flag);
			if(groupBy) {
				addGroupBy(as == null ? name : as, flag);
			}
		}
	}
	
	public void addSelect(String name, boolean groupBy, boolean flag) {
		addSelect(name, null, groupBy, flag);
	}
	
	public void addSelect(String name, boolean flag) {
		addSelect(name, null, flag);
	}
	
	public void addSelect(String name, String as) {
		addSelect(name, as, true);
	}
	
	public void addSelect(String name) {
		addSelect(name, null, true);
	}
	
	public void addSelect(String name1, String name2, String as, boolean flag) {
		if(flag)
			select.put(name1, as);
		else 
			select.put(name2, as);
	}
	
	public void addJoin(Join join, boolean flag) {
		if(flag) this.joins.add(join);
	}
	
	public void addInnerJoin(String table, String as, String condition, boolean flag) {
		if(flag) {
			InnerJoin join = new InnerJoin();
			join.setTable(table, as);
			join.addCondition(condition, true);
			this.joins.add(join);
		}
	}
	
	public void addInnerJoin(String table, String as, String condition) {
		addInnerJoin(table, as, condition, true);
	}
	
	public void addInnerJoin(String table, String condition) {
		addInnerJoin(table, null, condition, true);
	}
	
	public void addInnerJoin(String table, String condition, boolean flag) {
		addInnerJoin(table, null, condition, flag);
	}
	
	public void addCondition(String s, boolean flag) {
		if(flag)
			conditions.add(s);
	}
	
	public void addCondition(String s, Object v, boolean flag) {
		if(flag) {
			conditions.add(s);
			conditionValues.add(v);
		}
	}
	
	public void addConditionEQ(String s, boolean flag) {
		addCondition(s + " = ?", flag);
	}
	
	public void addConditionEQ(String s, Object v, boolean flag) {
		addCondition(s + " = ?", v, flag);
	}
	
	public void addConditionEQ(String s) {
		addCondition(s + " = ?", true);
	}
	
	public void addConditionEQ(String s, Object v) {
		addCondition(s + " = ?", v, true);
	}
	
	public void addCondition(String s) {
		addCondition(s, true);
	}
	
	public void addCondition(String s, Object v) {
		addCondition(s, v, true);
	}
	
	public void addConditionIn(String s, List<String> objects, boolean flag) {
		if(flag && objects != null && !objects.isEmpty()) {
			String tmp = s + " IN (";
			for(int i = 0; i < objects.size(); i++) {
				if(objects.get(i)!=null && !objects.get(i).isEmpty()) {
					tmp += "\'" + objects.get(i) +"\'";
					if(i < objects.size() - 1) {
						tmp += ",";
					} else {
						tmp += ")";
					}
				}
			}
			if(!tmp.equals(s + " IN (")) {
				conditions.add(tmp);
			}
		}
			
	}
	
	public void addGroupBy(String s, boolean flag) {
		if(flag)
			groupBy.add(s);
	}
	
	public void addGroupBy(String s) {
		addGroupBy(s, true);
	}
	
	public void addOrderBy(String s, boolean flag) {
		if(flag)
			orderBy.add(s);
	}
	
	public void addOrderBy(String s) {
		addOrderBy(s, true);
	}
	
	public void addOrderBy(String s, boolean sort, boolean flag) {
		if(sort)
			addOrderBy(s, flag);
		else if(flag)
			orderBy.add(s + " DESC");
	}

	public void addConditionBetween(String name, String s, String s1, boolean flag) {
		if(flag)
			conditions.add("(" + name + " BETWEEN " + s + " AND " + s1 + ")");
	}
	
	public void addConditionBetweenObj(String name, Object v, Object v1 ,boolean flag) {
		if(flag) {
			conditions.add("(" + name + " BETWEEN ? AND ?)");
			conditionValues.add(v);
			conditionValues.add(v1);
		}
	}
	
	public void addConditionBetween(String name, String s, String s1) {
		addConditionBetween(name, s, s1, true);
	}
	
	public void addConditionBetweenObj(String name, Object v, Object v1) {
		addConditionBetweenObj(name, v, v1, true);
	}
	
	@Override
	public String toString() {
		if(select.isEmpty() || from == null || from.isEmpty()) {
			return null;
		}
		String s = "SELECT ";
		
		int i = 0;
		int size = 0;
		if(!distinct.isEmpty()) {
			size = distinct.size()-1;
			s += "DISTINCT ON (";
			for(String d : distinct) {
				s += d;
				if(i < size) {
					s += ", ";
				} else {
					s += ") ";
				}
				i++;
			}
		}
		i = 0;
		size = select.size()-1;
		for(String name : select.keySet()) {
			s += name;
			if(select.get(name) != null && !select.get(name).isEmpty()) {
				s += " AS " + select.get(name);
			}
			if(i < size) {
				s += ", ";
			} else {
				s += " ";
			}
			i++;
		}
		s += "FROM " + from + " ";
		if(as !=null && !as.isEmpty()) {
			s += "AS " + as + " ";
		}
		for(i = 0; i < joins.size(); i++) {
			String tmp = joins.get(i).toString();
			if(tmp != null) s += joins.get(i).toString() + " ";
		}
		if(!conditions.isEmpty()) {
			s += "WHERE ";
			for(i = 0; i < conditions.size(); i++) {
				s += conditions.get(i);
				if(i < conditions.size()-1) {
					s += " AND ";
				} else {
					s += " ";
				}
			}
		}
		if(!groupBy.isEmpty()) {
			s += "GROUP BY ";
			for(i = 0; i < groupBy.size(); i++) {
				s += groupBy.get(i);
				if(i < groupBy.size()-1) {
					s += ", ";
				} else {
					s += " ";
				}
			}
		}
		if(!orderBy.isEmpty()) {
			s += "ORDER BY ";
			for(i = 0; i < orderBy.size(); i++) {
				s += orderBy.get(i);
				if(i < orderBy.size()-1) {
					s += ", ";
				}
			}
		}
		if(limit > 0) {
			s += " LIMIT " + limit;
		}
		if(offset > -1) {
			s += " OFFSET " + offset;
		}
		return s;
	}
	
	private void getStatement() throws GenericDataSourceException, GenericEntityException, SQLException {
		processor.prepareStatement(this.toString());
		for(Object obj : conditionValues) {
			Method method;
						try {
							method = processor.getClass().getMethod("setValue", obj.getClass());
							method.invoke(processor, obj);
						} catch (Exception e) {
							Debug.logError(e.getMessage(), OlbiusQuery.class.getName());
						}
		}
	}
	
	@Override
	public void execute() throws GenericDataSourceException, GenericEntityException, SQLException {
		getStatement();
		processor.executeQuery();
		executeFlag = true;
	}
	
	@Override
	public ResultSet getResultSet() throws GenericDataSourceException, GenericEntityException, SQLException {
		if(!executeFlag) {
			execute();
		}
		executeFlag = false;
		return processor.getResultSet();
	}
	
	@Override
	public OlapQuery offset(long offset) {
		this.offset = offset;
		return this;
	}
	
	@Override
	public void close() throws GenericDataSourceException {
		if(processor != null) {
			processor.close();
		}
	}
	
	@Override
	public OlapQuery limit(int limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public OlapQuery offset(int offset) {
		this.offset = offset;
		return this;
	}

	@Override
	public SQLProcessor getSQLProcessor() {
		return this.processor;
	}
}
