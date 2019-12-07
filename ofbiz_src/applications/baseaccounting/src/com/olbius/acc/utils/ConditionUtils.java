package com.olbius.acc.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

public class ConditionUtils {
	public static EntityCondition filterByThruDate(String dateField, Date thruDate) {
		EntityCondition thruDateNull = EntityCondition.makeCondition(dateField, EntityJoinOperator.EQUALS, null);
		EntityCondition lessThanThru = EntityCondition.makeCondition(dateField, EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate);
		EntityCondition condition = EntityCondition.makeCondition(EntityJoinOperator.OR, thruDateNull, lessThanThru);
		return condition;
	}
	
	public static EntityCondition filterByDate(String dateField, Timestamp fromDate, Timestamp thruDate) {
		EntityCondition fromDateCond = EntityCondition.makeCondition(dateField, EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate);
		EntityCondition thruDateCond = EntityCondition.makeCondition(dateField, EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate);
		List<EntityCondition> condition = new ArrayList<EntityCondition>();
		condition.add(thruDateCond);
		condition.add(fromDateCond);
		return EntityCondition.makeCondition(condition, EntityJoinOperator.AND);
	}
	
	public static EntityCondition filterByDate(String dateField, Timestamp fromDate, Timestamp thruDate, Boolean isDate) {
		if(isDate) {
			EntityCondition fromDateCond = EntityCondition.makeCondition(dateField, EntityJoinOperator.GREATER_THAN_EQUAL_TO, new Date(fromDate.getTime()));
			EntityCondition thruDateCond = EntityCondition.makeCondition(dateField, EntityJoinOperator.LESS_THAN_EQUAL_TO, new Date(thruDate.getTime()));
			List<EntityCondition> condition = new ArrayList<EntityCondition>();
			condition.add(thruDateCond);
			condition.add(fromDateCond);
			return EntityCondition.makeCondition(condition, EntityJoinOperator.AND);
		}else {
			return filterByDate(dateField, fromDate, thruDate);
		}
	}
}
