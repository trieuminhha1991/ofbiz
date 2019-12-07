import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.base.util.UtilProperties;

if (parameters.userLogin != null)
{
	String userLoginId = parameters.userLogin.userLoginId;
	
	userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
	
	currentOrg = UtilProperties.getPropertyValue("general", "ORGANIZATION_PARTY"); 
	
	if(userLogin != null) {
	currentOrg = userLogin.getString("lastOrg") };
	
	context.currentOrg = currentOrg;
}
