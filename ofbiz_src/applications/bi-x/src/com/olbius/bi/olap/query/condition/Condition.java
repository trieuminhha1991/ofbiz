package com.olbius.bi.olap.query.condition;

import java.util.ArrayList;
import java.util.List;

public class Condition {

	public static final String AND = "AND";

	public static final String OR = "OR";

	public static final String EQ = "=";

	public static final String NOT_EQ = "!=";

	public static final String LESS = "<";

	public static final String LESS_EQ = "<=";

	public static final String GREATER = ">";

	public static final String GREATER_EQ = ">=";

	private List<Object> conditions;

	private List<String> operator;

	private List<Object> conditionValues;

	public Condition() {
		conditions = new ArrayList<Object>();
		conditionValues = new ArrayList<Object>();
		operator = new ArrayList<String>();
	}

	public static Condition andCondition(Condition condition, Condition condition2) {
		return andCondition(condition, condition2, true);
	}

	public static Condition andCondition(Condition condition, Condition condition2, boolean flag) {
		Condition cond = new Condition();
		if (flag) {
			cond.and(condition).and(condition2);
		}
		return cond;
	}

	public static Condition make(String cond) {
		return make(cond, true);
	}

	public static Condition make(String cond, boolean flag) {
		Condition condition = new Condition();
		return condition.and(cond, flag);
	}

	public static Condition make(String cond, Object v) {
		return make(cond, v, true);
	}

	public static Condition make(String cond, Object v, boolean flag) {
		Condition condition = new Condition();
		return condition.and(cond, v, flag);
	}

	public static Condition make(String s, String operator, Object v) {
		return make(s, operator, v, true);
	}

	public static Condition make(String s, String operator, Object v, boolean flag) {
		Condition condition = new Condition();
		return condition.and(s, operator, v, flag);
	}

	public static Condition makeBetween(String s, Object v, Object v2) {
		return makeBetween(s, v, v2, true);
	}

	public static Condition makeBetween(String s, Object v, Object v2, boolean flag) {
		Condition condition = new Condition();
		return condition.andBetween(s, v, v2, flag);
	}
	
	public static Condition makeEQ(String cond, Object v) {
		return makeEQ(cond, v, true);
	}

	public static Condition makeEQ(String cond, Object v, boolean flag) {
		Condition condition = new Condition();
		return condition.andEQ(cond, v, flag);
	}
	
	public static Condition makeIn(String s, List<Object> v) {
		return makeIn(s, v, true);
	}

	public static Condition makeIn(String s, List<Object> v, boolean flag) {
		Condition condition = new Condition();
		return condition.andIn(s, v, flag);
	}

    public static Condition makeFuncField(String s, String operator, String func, Object v) {
        return makeFuncField(s, operator, func, v, true);
    }

    public static Condition makeFuncField(String s, String operator, String func, Object v, boolean flag) {
        Condition condition = new Condition();
        return condition.andFuncField(s, operator, func, v, flag);
    }

	public static Condition orCondition(Condition condition, Condition condition2) {
		return orCondition(condition, condition2, true);
	}

	public static Condition orCondition(Condition condition, Condition condition2, boolean flag) {
		Condition cond = new Condition();
		if (flag) {
			cond.and(condition).or(condition2);
		}
		return cond;
	}

	private void add(Condition condition, String op) {
		Object object = condition;
		add(object, op);
		conditionValues.add(condition);
	}

	private void add(Object condition, String op) {
		conditions.add(condition);
		if (conditions.size() > 1 && op != null) {
			operator.add(op);
		}
	}

	@SuppressWarnings("unchecked")
	private void add(String condition, String op, Object... conditionValue) {
		add(condition, op);
		for (Object obj : conditionValue) {
			if(obj instanceof List) {
				conditionValues.addAll((List<Object>)obj);
			} else {
				conditionValues.add(obj);
			}
		}
	}

	private void addIn(String s, String operator, List<Object> v) {
		String tmp = s + " IN (";
		for(int i = 0; i < v.size(); i++) {
			tmp += "?";
			if(i < v.size() - 1) {
				tmp += ",";
			}
		}
		tmp += ")";
		add(tmp, operator, v);
	}

	public Condition and(Condition cond) {
		return and(cond, true);
	}

	public Condition and(Condition cond, boolean flag) {
		if (flag) {
			add(cond, AND);
		}
		return this;
	}

	public Condition and(String condition) {
		return and(condition, true);
	}

	public Condition and(String condition, boolean flag) {
		if (flag) {
			add(condition, AND);
		}
		return this;
	}

	public Condition and(String condition, Object v) {
		return and(condition, v, true);
	}

	public Condition and(String condition, Object v, boolean flag) {
		if (flag) {
			add(condition, AND, v);
		}
		return this;
	}

	public Condition and(String s, String operator, Object v) {
		return and(s, operator, v, true);
	}

	public Condition and(String s, String operator, Object v, boolean flag) {
		if (flag) {
			and(s + " " + operator + " ?", v, flag);
		}
		return this;
	}

	public Condition andBetween(String s, Object v, Object v2) {
		return andBetween(s, v, v2, true);
	}

	public Condition andBetween(String s, Object v, Object v2, boolean flag) {
		if (flag) {
			add("(" + s + " BETWEEN ? AND ?)", AND, v, v2);
		}
		return this;
	}

	public Condition andEQ(String s, Object v) {
		return andEQ(s, v, true);
	}
	
	public Condition andEQ(String s, Object v, boolean flag) {
		return and(s, EQ, v, flag);
	}
	
	public Condition andNotEQ(String s, Object v) {
		return andNotEQ(s, v, true);
	}
	
	public Condition andNotEQ(String s, Object v, boolean flag) {
		return and(s, NOT_EQ, v, flag);
	}
	
	public Condition andIn(String s, List<Object> v) {
		return andIn(s, v, true);
	}
	
	public Condition andIn(String s, List<Object> v, boolean flag) {
		if(flag) {
			if (v == null) v = new ArrayList<Object>();
			addIn(s, AND, v);
		}
		return this;
	}

	public Condition andFuncField(String s, String operator, String func, Object v, boolean flag) {
		if(flag) {
            add(s + operator + " " + func + "(?)", AND, v);
		}
		return this;
	}
	
	public List<Object> getConditions() {
		return conditions;
	}
	
	public List<Object> getConditionValues() {
		List<Object> tmp = new ArrayList<Object>();
		for (Object obj : conditionValues) {
			if (obj instanceof Condition) {
				tmp.addAll(((Condition) obj).getConditionValues());
			} else {
				tmp.add(obj);
			}
		}
		return tmp;
	}

	public Condition or(Condition cond) {
		return or(cond, true);
	}

	public Condition or(Condition cond, boolean flag) {
		if (flag) {
			add(cond, OR);
		}
		return this;
	}

	public Condition or(String condition) {
		return or(condition, true);
	}

	public Condition or(String condition, boolean flag) {
		if (flag) {
			add(condition, OR);
		}
		return this;
	}

	public Condition or(String condition, Object v) {
		return or(condition, v, true);
	}

	public Condition or(String condition, Object v, boolean flag) {
		if (flag) {
			add(condition, OR, v);
		}
		return this;
	}

	public Condition or(String s, String operator, Object v) {
		return or(s, operator, v);
	}

	public Condition or(String s, String operator, Object v, boolean flag) {
		if (flag) {
			or(s + operator + "?", v, flag);
		}
		return this;
	}

	public Condition orBetween(String s, Object v, Object v2) {
		return orBetween(s, v, v2, true);
	}

	public Condition orBetween(String s, Object v, Object v2, boolean flag) {
		if (flag) {
			add("(" + s + " BETWEEN ? AND ?)", OR, v, v2);
		}
		return this;
	}

	public Condition orEQ(String s, Object v) {
		return orEQ(s, v, true);
	}

	public Condition orEQ(String s, Object v, boolean flag) {
		return or(s, EQ, v, flag);
	}

	public Condition orIn(String s, List<Object> v) {
		return orIn(s, v, true);
	}

	public Condition orIn(String s, List<Object> v, boolean flag) {
		if(flag && v != null && !v.isEmpty()) {
			addIn(s, AND, v);
		}
		return this;
	}

	@Override
	public String toString() {
		String s = "";

		for (int i = 0; i < conditions.size(); i++) {
			boolean flag = false;
			if (conditions.get(i) instanceof Condition) {
				String tmp = conditions.get(i).toString();
				if(!tmp.isEmpty()) {
					s += "(" + tmp + ")";
					flag = true;
				}
			} else {
				s += conditions.get(i).toString();
				flag = true;
			}
			if (i < conditions.size() - 1 && flag) {
				s += " " + operator.get(i) + " ";
			}
		}

		return s;
	}
}
