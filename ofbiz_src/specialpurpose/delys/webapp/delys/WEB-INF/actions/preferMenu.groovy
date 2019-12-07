import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

if(userLogin != null){
	listFuncs = delegator.findList("UserLoginFunction", EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.partyId), null, ["sequenceNumber"], null, false);
	context.listFuncs = listFuncs;
}
context.listProducts = delegator.findList("Product", null, null, null, null, true);
