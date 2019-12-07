package com.olbius.bi.olap.query.option;

import java.util.Map;

import com.olbius.bi.olap.query.condition.Condition;

public class GreaterFilter implements FilterCondition{

	@Override
	public void registry(Map<String, FilterCondition> map) {
		map.put(GREATER_THAN, this);
	}

	@Override
	public Condition makeCondition(String name, Object value) {
		return Condition.make(name, Condition.GREATER, value);
	}

}
