package com.olbius.basesales.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.common.util.EntityMiscUtil;

public class ProcessConditionUtil {
	
	public static List<String> processOrderSort(List<String> listSortFields){
    	List<String> listSortFieldsResult = new ArrayList<String>();
    	for (String item : listSortFields) {
    		String desc = "";
    		if (item.indexOf("-") > -1) {
    			desc = "-";
    			item = item.replace("-", "");
    		}
			if ("fullDeliveryDate".equals(item)) {
				listSortFieldsResult.add(desc + "estimatedDeliveryDate");
				listSortFieldsResult.add(desc + "shipAfterDate");
				listSortFieldsResult.add(desc + "shipBeforeDate");
			} else {
				listSortFieldsResult.add(desc + item);
			}
		}
    	
    	return listSortFieldsResult;
    }
	
	public static List<EntityCondition> processOrderCondition(List<EntityCondition> listAllConditions){
    	List<EntityCondition> listAllConditionsResult = new ArrayList<EntityCondition>();
		for (EntityCondition condition : listAllConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				if (UtilValidate.isEmpty(conditionSplit)) listAllConditionsResult.add(condition);
				
				String fieldName = conditionSplit.length > 0 ? (String) conditionSplit[0] : null;
				String operator = conditionSplit.length > 1 ? (String) conditionSplit[1] : null;
				String value = conditionSplit.length > 2 ? (String) conditionSplit[2].trim() : null;
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
				
				if ("fullDeliveryDate".equals(fieldName)) {
					if ("RANGE".equals(operator) && valueFrom != null && valueTo != null) {
						Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
						Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
						EntityCondition condEstimatedDeliveryDate = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.BETWEEN, UtilMisc.toList(valueFromTs, valueToTs));
						EntityCondition condShipAfterDate = EntityCondition.makeCondition("shipAfterDate", EntityOperator.GREATER_THAN_EQUAL_TO, valueFromTs);
						EntityCondition condShipBeforeDate = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, valueToTs);
						EntityCondition condAndRange = EntityCondition.makeCondition(condShipAfterDate, EntityOperator.AND, condShipBeforeDate);
						EntityCondition condOr = EntityCondition.makeCondition(condEstimatedDeliveryDate, EntityOperator.OR, condAndRange);
						listAllConditionsResult.add(condOr);
					}
				} else if ("isFavorDelivery".equals(fieldName)) {
					if ("_NA_".equals(value)) {
						listAllConditionsResult.add(EntityCondition.makeCondition("isFavorDelivery", EntityOperator.EQUALS, null));
					} else {
						listAllConditionsResult.add(condition);
					}
				} else {
					listAllConditionsResult.add(condition);
				}
			}
		}
    	return listAllConditionsResult;
	}
}
