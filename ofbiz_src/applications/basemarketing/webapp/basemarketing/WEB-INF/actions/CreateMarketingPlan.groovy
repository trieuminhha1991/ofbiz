import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

marketingPlanId = parameters.id;
List<EntityCondition> cond1 = FastList.newInstance();
cond1.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond1.add(EntityCondition.makeCondition("marketingContentTypeId", "PLAN_VISION"));
List<GenericValue> visions = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond1, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(visions)){
	GenericValue vision = visions.get(0);
	context.vision = vision;
}
List<EntityCondition> cond2 = FastList.newInstance();
cond2.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond2.add(EntityCondition.makeCondition("marketingContentTypeId", "PLAN_MISSION"));
List<GenericValue> missions = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond2, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(missions)){
	GenericValue mission = missions.get(0);
	context.mission = mission;
}
List<EntityCondition> cond3 = FastList.newInstance();
cond3.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond3.add(EntityCondition.makeCondition("marketingContentTypeId", "PLAN_MESSAGE"));
List<GenericValue> messagings = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond3, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(messagings)){
	GenericValue messaging = messagings.get(0);
	context.messaging = messaging;
}
List<EntityCondition> cond4 = FastList.newInstance();
cond4.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond4.add(EntityCondition.makeCondition("marketingContentTypeId", "PLAN_SWOT"));

List<GenericValue> swots = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond4, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(swots)){
	GenericValue swot = swots.get(0);
	context.swot = swot;
}
List<EntityCondition> cond5 = FastList.newInstance();
cond5.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond5.add(EntityCondition.makeCondition("marketingContentTypeId", "PLAN_PEST"));

List<GenericValue> pests = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond5, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(pests)){
	GenericValue pest = pests.get(0);
	context.pest = pest;
}

List<EntityCondition> cond6 = FastList.newInstance();
cond6.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond6.add(EntityCondition.makeCondition("marketingContentTypeId", "MARKET_INSIGHT"));

List<GenericValue> insights = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond6, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(insights)){
	GenericValue insight = insights.get(0);
	context.insight = insight;
}

List<EntityCondition> cond7 = FastList.newInstance();
cond7.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond7.add(EntityCondition.makeCondition("marketingContentTypeId", "COMP_COMPARISON"));

List<GenericValue> comparisons = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond7, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(comparisons)){
	GenericValue comparison = comparisons.get(0);
	context.comparison = comparison;
}

List<EntityCondition> cond8 = FastList.newInstance();
cond8.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond8.add(EntityCondition.makeCondition("marketingContentTypeId", "COMP_COMPARISON"));

List<GenericValue> customers = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond8, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(customers)){
	GenericValue customer = customers.get(0);
	context.customer = customer;
}

List<EntityCondition> cond9 = FastList.newInstance();
cond9.add(EntityCondition.makeCondition("marketingPlanId", marketingPlanId));
cond9.add(EntityCondition.makeCondition("marketingContentTypeId", "PLAN_OBJECTIVE"));

List<GenericValue> objs = delegator.findList("MarketingPlanContentDetail",
	EntityCondition.makeCondition(cond9, EntityOperator.AND), null, null, null, false);
if(!UtilValidate.isEmpty(customers)){
	GenericValue obj = objs.get(0);
	context.objective = obj;
}