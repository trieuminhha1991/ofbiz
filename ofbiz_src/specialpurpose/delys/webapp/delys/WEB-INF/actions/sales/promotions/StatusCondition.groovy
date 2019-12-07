import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

if(context.budgetTypeId){
	exprList = EntityCondition.makeCondition("budgetTypeId", EntityOperator.EQUALS, context.budgetTypeId);
	context.andCondition = exprList;
}
