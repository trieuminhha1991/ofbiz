import javolution.util.FastList;
import com.olbius.security.util.SecurityUtil;
import com.olbius.basehr.util.PartyUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilMisc;

List<String> listDepart1s = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), UtilDateTime.nowTimestamp());
String userLoginId = requirement.getString("createdByUserLogin");
GenericValue createdUserLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", userLoginId));
List<String> listDepart2s = PartyUtil.getDepartmentOfEmployee(delegator, createdUserLogin.getString("partyId"), UtilDateTime.nowTimestamp());
Boolean checkInternal = false;
if (!listDepart1s.isEmpty() && !listDepart2s.isEmpty()){
	for(String a : listDepart2s){
		for(String b : listDepart1s){
			if(a.equals(b)){
				checkInternal = true;
				break;
			}
		}
	}
}
Boolean checkPermission = false;
if (!checkInternal){
	if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOGISTICS")){
		checkPermission = true;
	}
} else {
	checkPermission = true;
}
List<EntityCondition> listAllConditions = FastList.newInstance();
listAllConditions.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId));
listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("REQ_CANCELLED", "REQ_REJECTED")));

List<GenericValue> listReqItems = delegator.findList("RequirementItemDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);

context.checkPermission = checkPermission;
context.checkInternal = checkInternal;
context.listItems = listReqItems;