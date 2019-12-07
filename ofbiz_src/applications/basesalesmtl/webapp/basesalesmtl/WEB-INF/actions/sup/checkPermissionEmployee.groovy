import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import java.util.List;

partyId = userLogin.partyId;
if(UtilValidate.isNotEmpty(partyId)){
	List<GenericValue> relations = delegator.findList("PartyRelationship", EntityCondition.makeCondition(
		UtilMisc.toList(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"),
			EntityCondition.makeCondition("roleTypeIdFrom", "SALESMAN_EMPL"),
			EntityCondition.makeCondition("partyIdFrom", partyId),
			EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"),
			EntityUtil.getFilterByDateExpr())
	), null, null, null, false);
	if(UtilValidate.isNotEmpty(relations)){
		context.roleTypeId = "SALESMAN_EMPL";
		context.partyId = partyId;
	}
}
