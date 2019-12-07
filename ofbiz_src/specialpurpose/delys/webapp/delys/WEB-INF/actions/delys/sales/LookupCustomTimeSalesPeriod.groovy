import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

conditionList = [];
orConditionList = [];
orConditionList2 = [];
mainConditionList = [];
// or 1
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_MONTH"));
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_QUARTER"));
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_YEAR"));
orConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_WEEK"));
orConditions = EntityCondition.makeCondition(orConditionList, EntityOperator.OR);

// or 2
orConditionList2.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, null));
conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.NOT_EQUAL, null));
conditionList.add(EntityCondition.makeCondition("isClosed", EntityOperator.NOT_EQUAL, "N"));
conditions = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orConditionList2.add(conditions);
orConditions2 = EntityCondition.makeCondition(orConditionList2, EntityOperator.OR);

mainConditionList.add(orConditions);
mainConditionList.add(orConditions2);
mainConditions = EntityCondition.makeCondition(mainConditionList, EntityOperator.AND);
context.andCondition = orConditions;

//listPeriodTypeString = ["SALES_MONTH", "SALES_QUARTER", "SALES_YEAR", "SALES_WEEK"];
//context.listPeriodTypeString = listPeriodTypeString;
//
//listPeriodType = delegator.findList("PeriodType", orConditions, null, null, null, false);
//context.listPeriodType = listPeriodType;