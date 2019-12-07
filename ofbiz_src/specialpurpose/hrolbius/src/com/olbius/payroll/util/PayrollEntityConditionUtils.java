package com.olbius.payroll.util;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

public class PayrollEntityConditionUtils {
	public static EntityCondition makeDateConds(Timestamp fromDate, Timestamp thruDate){
		List<EntityCondition> conditions = FastList.newInstance();
		if(thruDate != null){
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		}
		if(fromDate != null){
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
		}
		return EntityCondition.makeCondition(conditions);
	}
}
