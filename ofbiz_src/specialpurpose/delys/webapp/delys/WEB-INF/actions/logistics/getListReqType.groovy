import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

List<String> listRequirementTypeId= new ArrayList<String>();
if (!listReqTypes.isEmpty()){
	for (GenericValue item : listReqTypes) {
		listRequirementTypeId.add(item.get("requirementTypeId"));
	}
} else {
	context.NoPermission = "Y";
}

if (!listRequirementTypeId.isEmpty()){
	context.listRequirementTypeId = listRequirementTypeId;
}