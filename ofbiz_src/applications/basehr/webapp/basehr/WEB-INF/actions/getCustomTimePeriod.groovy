import java.sql.Timestamp;
import java.sql.Date;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
context.nowTimestamp = nowTimestamp;
Date nowDate = new Date(nowTimestamp.getTime());
List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("periodTypeId", "YEARLY"));
conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, nowDate));
conditions.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, nowDate));
List<GenericValue> customTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
if(UtilValidate.isNotEmpty(customTimePeriod)){
	context.selectYearCustomTimePeriodId = customTimePeriod.get(0).getString("customTimePeriodId");
}