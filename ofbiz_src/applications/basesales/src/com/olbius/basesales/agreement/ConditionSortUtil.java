package com.olbius.basesales.agreement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.common.util.EntityMiscUtil;

public class ConditionSortUtil {
	@SuppressWarnings("deprecation")
	public static List<EntityCondition> processAgreementListCondition(List<EntityCondition> listAllConditions){
    	List<EntityCondition> listAllConditionsResult = new ArrayList<EntityCondition>();
		for (EntityCondition condition : listAllConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				String fieldName = (String) conditionSplit[0];
				String operator = (String) conditionSplit[1];
				String value = (String) conditionSplit[2].trim();
				String valueFrom = null;
				String valueTo = null;
				if (conditionSplit.length > 4) {
					if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
						if ("AND".equals(conditionSplit[4].trim())) {
							operator = "RANGE";
							valueFrom = (String) conditionSplit[2].trim();
							valueTo = (String) conditionSplit[7].trim();
							valueFrom = EntityMiscUtil.cleanValue(valueFrom);
							valueTo = EntityMiscUtil.cleanValue(valueTo);
						}
					}
				}
				fieldName = EntityMiscUtil.cleanFieldName(fieldName);
				value = EntityMiscUtil.cleanValue(value);
				
				if ("remainDays".equals(fieldName)) {
					Timestamp thruDateCompare = UtilDateTime.nowTimestamp();
					Integer remainDaysInt = new Integer(value);
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(thruDateCompare);
					cal.add(Calendar.DAY_OF_WEEK, remainDaysInt);
					thruDateCompare.setTime(cal.getTime().getTime()); // or
					thruDateCompare = new Timestamp(cal.getTime().getTime());
					thruDateCompare.setHours(0);
					thruDateCompare.setMinutes(0);
					thruDateCompare.setSeconds(0);
					thruDateCompare.setNanos(0);
					
					// LIKE, NOT_LIKE, EQUAL, NOT_EQUAL, RANGE, NOT_LIKE, NOT_EQUAL, =, >=, <=, >, <, <>
					if (operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("EQUAL") || operator.equalsIgnoreCase("LIKE")) {
						Timestamp thruDateCompareTo = new Timestamp(thruDateCompare.getTime());
						thruDateCompareTo.setHours(23);
						thruDateCompareTo.setMinutes(59);
						thruDateCompareTo.setSeconds(59);
						thruDateCompareTo.setNanos(999);
						EntityCondition condDate = EntityCondition.makeCondition("thruDate", EntityOperator.BETWEEN, UtilMisc.toList(thruDateCompare, thruDateCompareTo));
						listAllConditionsResult.add(condDate);
					} else if(operator.equalsIgnoreCase(">=")) {
						EntityCondition condDate = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDateCompare);
						listAllConditionsResult.add(condDate);
					} else if(operator.equalsIgnoreCase("<=")) {
						EntityCondition condDate = EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateCompare);
						listAllConditionsResult.add(condDate);
					} else if(operator.equalsIgnoreCase(">")) {
						EntityCondition condDate = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, thruDateCompare);
						listAllConditionsResult.add(condDate);
					} else if(operator.equalsIgnoreCase("<")) {
						EntityCondition condDate = EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, thruDateCompare);
						listAllConditionsResult.add(condDate);
					} else if(operator.equalsIgnoreCase("<>") || operator.equalsIgnoreCase("NOT_EQUAL")) {
						EntityCondition condDate = EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, thruDateCompare);
						listAllConditionsResult.add(condDate);
					}
				} else {
					listAllConditionsResult.add(condition);
				}
			}
		}
    	return listAllConditionsResult;
	}
}
