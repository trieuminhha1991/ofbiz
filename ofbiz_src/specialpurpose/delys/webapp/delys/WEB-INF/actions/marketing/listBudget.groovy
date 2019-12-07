import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;

EntityCondition cond = EntityCondition.makeCondition("budgetTypeId", "MARKETING");
budgets = delegator.findList(
		"Budget", cond, null, null, null,
		false);
context.budgets = budgets;
