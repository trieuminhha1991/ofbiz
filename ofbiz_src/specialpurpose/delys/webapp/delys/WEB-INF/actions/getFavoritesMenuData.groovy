import com.olbius.util.SecurityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.base.util.UtilMisc;

partyId = userLogin.partyId;
currRoles = SecurityUtil.getCurrentRoles(partyId, delegator);
if(currRoles != null && currRoles.size() > 0){
	// get all functions associated to user's roles
	listCond = [];
	for(int i = 0; i < currRoles.size();i++){
		listCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, currRoles.get(i)));
	}
	osisFindOptions = new EntityFindOptions();
    osisFindOptions.setDistinct(true);
	listFuncs = delegator.findList("FunctionRoleDetail", EntityCondition.makeCondition(listCond, EntityOperator.OR), null, ["funcId"], osisFindOptions, false);
	// convert to json format(display on jqx)
	JSONArray jsonArray = JSONArray.fromObject(listFuncs);
	context.listFuncs = jsonArray.toString();
	
	// get all user's function
	listUserFuncs = delegator.findList("UserLoginFunctionDetail", EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.partyId), UtilMisc.toSet("funcId","name"), ["sequenceNumber"], null, false);
	jsonArray = JSONArray.fromObject(listUserFuncs);
	context.listUserFuncs = jsonArray.toString();
}
context.test = "test";