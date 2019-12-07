package com.olbius.bi.olap.query.option;

import java.util.Map;

import com.olbius.bi.olap.query.condition.Condition;

public class NotEqualFilter implements FilterCondition{

	@Override
	public void registry(Map<String, FilterCondition> map) {
		map.put(NOT_EQUAL, this);
	}

	@Override
	public Condition makeCondition(String name, Object value) {
		return Condition.make(name, Condition.NOT_EQ, value);
	}

}
