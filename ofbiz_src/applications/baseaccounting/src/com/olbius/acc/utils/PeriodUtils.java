package com.olbius.acc.utils;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PeriodUtils {
	
	public static final String PERIOD_TYPE_ID = "FISCAL_MONTH";
	
	public static String getCustomTimePeriod(Date calDate, Delegator delegator) throws GenericEntityException{
		List<EntityCondition> listConds = new ArrayList<>();
		listConds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO ,calDate));
		listConds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN ,calDate));
		listConds.add(EntityCondition.makeCondition("periodTypeId", PERIOD_TYPE_ID));
		List<GenericValue> periodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, null, false);
		GenericValue period = EntityUtil.getFirst(periodList);
		if(period == null) return null;
		return period.getString("customTimePeriodId");
	}

    public static String getCustomTimePeriod(String dateStr, Delegator delegator) throws GenericEntityException, ParseException {
        SimpleDateFormat format = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        format.applyPattern("dd/MM/yyyy");
        Date date = new Date(format.parse(dateStr).getTime());
        return getCustomTimePeriod(date, delegator);
    }
	
	public static GenericValue getGenCustomTimePeriod(Date calDate, Delegator delegator) throws GenericEntityException{
		List<EntityCondition> listConds = new ArrayList<>();
		listConds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO ,calDate));
		listConds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN ,calDate));
		listConds.add(EntityCondition.makeCondition("periodTypeId", PERIOD_TYPE_ID));
		List<GenericValue> periodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, null, false);
		GenericValue period = EntityUtil.getFirst(periodList);
		return period;
	}
}
