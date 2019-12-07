import org.ofbiz.entity.condition.EntityCondition;

parollFormula = delegator.findList("PayrollFormula", null ,null, null,null, false);
context.parollFormula = parollFormula;