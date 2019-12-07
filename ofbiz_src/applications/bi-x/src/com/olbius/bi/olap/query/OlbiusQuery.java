package com.olbius.bi.olap.query;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.AbstractJoin;
import com.olbius.bi.olap.query.join.Join;

public class OlbiusQuery implements OlbiusQueryInterface{

	public static final String ASC = "ASC";
	
	public static final String DESC = "DESC";
	
	private String table;
	
	private String as;
	
	private List<Join> joins;
	
	private Condition conditions;
	
	private Map<Object, String> select;
	
	private List<String> groupBy;
	
	private List<String> orderBy;
	
	private List<String> distinct;
	
	private long limit;
	
	private long offset;
	
	private OlapQuery query;
	
	private SQLProcessor processor;
	
	private boolean executeFlag;
	
	private boolean selectFlag;
	
	private boolean distinctFlag;
	
	private Map<String, OlapQuery> queryExtend;
	
	public OlbiusQuery() {
		select = new HashMap<Object, String>();
		groupBy = new ArrayList<String>();
		orderBy = new ArrayList<String>();
		distinct = new ArrayList<String>();
		joins = new ArrayList<Join>();
		queryExtend = new HashMap<String, OlapQuery>();
		limit = 0;
		offset = -1;
	}
	
	public OlbiusQuery(SQLProcessor processor) {
		this();
		this.processor = processor;
	}
	
	public static OlbiusQuery make() {
		return new OlbiusQuery();
	}
	
	public static OlbiusQuery make(Delegator delegator, String group) {
		return make(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo(group)));
	}
	
	public static OlbiusQuery make(SQLProcessor processor) {
		return new OlbiusQuery(processor);
	}
	
	@Override
	public void close() throws GenericDataSourceException{
		if(processor != null) {
			processor.close();
		}
	}

	@Override
	public OlbiusQueryInterface distinct() {
		distinctFlag = true;
		return this;
	}

	@Override
	public OlbiusQueryInterface distinctOn(String... name) {
		for(String s: name) {
			distinct.add(s);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface distinctOn(String name, boolean flag) {
		if(flag) {
			distinct.add(name);
		}
		return this;
	}

	@Override
	public void execute() throws GenericDataSourceException, GenericEntityException, SQLException {
		getStatement();
		processor.executeQuery();
		executeFlag = true;
	}
	
	@Override
	public OlbiusQueryInterface from(OlapQuery query, String as) {
		if(as == null || as.isEmpty()) {
			return null;
		}
		this.table = null;
		this.as = as;
		this.query = query;
		return this;
	}

	@Override
	public OlbiusQueryInterface from(String name) {
		return from(name, null);
	}

	@Override
	public OlbiusQueryInterface from(String name, String as) {
		this.table = name;
		this.as = as;
		this.query = null;
		return this;
	}

	@Override
	public List<Object> getConditionValues() {
		List<Object> tmp = new ArrayList<Object>();
		if(selectFlag) {
			for(Object s : select.keySet()) {
				if(s instanceof Condition) {
					tmp.addAll(((Condition) s).getConditionValues());
				}
			}
		}
		
		if(this.query != null) {
			tmp.addAll(this.query.getConditionValues());
		}
		for (String query : this.queryExtend.keySet()) {
			tmp.addAll(this.queryExtend.get(query).getConditionValues());
		}
		for(Join join : this.joins) {
			tmp.addAll(join.getConditionValues());
		}
		if(conditions != null) {
			tmp.addAll(this.conditions.getConditionValues());
		}
		return tmp;
	}

	@Override
	public String getFrom() {
		if(as != null && !as.isEmpty()) {
			return as;
		}
		return table;
	}

	@Override
	public ResultSet getResultSet() throws GenericDataSourceException, GenericEntityException, SQLException {
		if(!executeFlag) {
			execute();
		}
		executeFlag = false;
		return processor.getResultSet();
	}

	private void getStatement() throws GenericDataSourceException, GenericEntityException, SQLException {
		processor.prepareStatement(this.toString());
		for(Object obj : getConditionValues()) {
			Method method;
			try {
				method = processor.getClass().getMethod("setValue", obj.getClass());
				method.invoke(processor, obj);
			} catch (Exception e) {
				Debug.logError(e, OlbiusQuery.class.getName());
			}
		}
	}

	@Override
	public OlbiusQueryInterface groupBy(String... name) {
		for(String s : name) {
			this.groupBy.add(s);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface groupBy(String name, boolean flag) {
		if(flag) {
			this.groupBy.add(name);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface join(Join join) {
		return join(join, true);
	}

	@Override
	public OlbiusQueryInterface join(Join join, boolean flag) {
		if(flag) {
			this.joins.add(join);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, Condition condition) {
		return join(type, query, as, condition, true);
	}

	@Override
	public OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, Condition condition, boolean flag) {
		if(flag && as != null && !as.isEmpty()) {
			Join join = AbstractJoin.make(type);
			join.table(query, as).on(condition);
			this.join(join, flag);
		}
		return this;
	}
	
	@Override
	public OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, String condition) {
		return join(type, query, as, condition, true);
	}
	
	@Override
	public OlbiusQueryInterface join(String type, OlbiusQueryInterface query, String as, String condition, boolean flag) {
		return join(type, query, as, Condition.make(condition), true);
	}
	
	@Override
	public OlbiusQueryInterface join(String type, String table, Condition condition) {
		return join(type, table, condition, true);
	}
	
	@Override
	public OlbiusQueryInterface join(String type, String table, Condition condition, boolean flag) {
		return join(type, table, null, condition, flag);
	}
	
	@Override
	public OlbiusQueryInterface join(String type, String table, String condition) {
		return join(type, table, condition, true);
	}
	
	@Override
	public OlbiusQueryInterface join(String type, String table, String condition, boolean flag) {
		return join(type, table, null, condition, flag);
	}
	
	@Override
	public OlbiusQueryInterface join(String type, String table, String as, Condition condition) {
		return join(type, table, as, condition, true);
	}

	@Override
	public OlbiusQueryInterface join(String type, String table, String as, Condition condition, boolean flag) {
		if(flag) {
			Join join = AbstractJoin.make(type);
			join.table(table, as).on(condition);
			this.join(join, flag);
		}
		return this;
	}
	
	@Override
	public OlbiusQueryInterface join(String type, String table, String as, String condition) {
		return join(type, table, as, condition, true);
	}

	@Override
	public OlbiusQueryInterface join(String type, String table, String as, String condition, boolean flag) {
		return join(type, table, as, Condition.make(condition), flag);
	}

	@Override
	public OlapQuery limit(int limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public OlbiusQueryInterface limit(long limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public OlapQuery offset(int offset) {
		this.offset = offset;
		return this;
	}

	@Override
	public OlbiusQueryInterface offset(long offset) {
		this.offset = offset;
		return this;
	}
	
	@Override
	public OlbiusQueryInterface orderBy(String... name) {
		for(String s : name) {
			this.orderBy.add(s);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface orderBy(String name, boolean flag) {
		if(flag) {
			this.orderBy.add(name);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface orderBy(String name, String sort) {
		return orderBy(name, sort, true);
	}
	
	@Override
	public OlbiusQueryInterface orderBy(String name, String sort, boolean flag) {
		return orderBy(name + " " + sort, flag);
	}

	@Override
	public OlbiusQueryInterface select(Condition condition, String as) {
		return select(condition, as, true);
	}

	@Override
	public OlbiusQueryInterface select(Condition condition, String as, boolean flag) {
		if(flag) {
			this.select.put(condition, as);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface select(Function function, String as) {
		return select(function, as, true);
	}

	@Override
	public OlbiusQueryInterface select(Function function, String as, boolean flag) {
		if(flag) {
			this.select.put(function, as);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface select(String... name) {
		for(String s: name) {
			select(s, true);
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface select(String name, boolean flag) {
		return select(name, null, flag);
	}

	@Override
	public OlbiusQueryInterface select(String name, String as) {
		return select(name, as, true);
	}
	
	@Override
	public OlbiusQueryInterface select(String name, String as, boolean flag) {
		if(flag) {
			this.select.put(name, as);
		}
		return this;
	}

	@Override
	public String toString() {
		if(select.isEmpty()) {
			return null;
		}
		String s = "SELECT ";
		
		int i = 0;
		int size = 0;
		if(distinctFlag) {
			s += "DISTINCT ";
		} else if(!distinct.isEmpty()) {
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
		selectFlag = false;
		for(Object name : select.keySet()) {
			
			if(name instanceof Condition) {
				selectFlag = true;
			}
			
			s += name.toString();
			
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
		s += "FROM ";
		if(query != null) {
			s += "(" + query.toString()  + ") ";
		} else if (table !=null && !table.isEmpty()) {
			s += table + " ";
		} else {
			return null;
		}

		if(as !=null && !as.isEmpty()) {
			s += "AS " + as + " ";
		}
		
		boolean extend = false;
		
		for (String queryAs : this.queryExtend.keySet()) {
			String queryEx = this.queryExtend.get(queryAs).toString();
			if(query == null) {
				continue;
			} else {
				if(!extend) {
					s += ", (" + queryEx + ") AS " + queryAs + " ";
				}
			}
		}
		
		for(i = 0; i < joins.size(); i++) {
			String tmp = joins.get(i).toString();
			if(tmp != null) s += tmp + " ";
		}
		if(conditions != null) {
			String tmp = conditions.toString();
			if(tmp != null && !tmp.isEmpty()) {
				s += "WHERE " + tmp + " ";
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

	@Override
	public OlbiusQueryInterface where(Condition condition) {
		this.conditions = condition;
		return this;
	}

	@Override
	public Condition where() {
		if(this.conditions == null) {
			this.conditions = new Condition();
		}
		return this.conditions;
	}

	@Override
	public SQLProcessor getSQLProcessor() {
		return this.processor;
	}

	@Override
	public OlbiusQueryInterface distinct(boolean flag) {
		if(flag) {
			return distinct();
		}
		return this;
	}

	@Override
	public OlbiusQueryInterface extend(OlapQuery query, String as, boolean flag) {
		if(flag) {
			this.queryExtend.put(as, query);
		}
		return this;
	}
}
