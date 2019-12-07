import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.olbius.dms.util.SecurityUtil;

List<String> listPartyFromId = SecurityUtil.getPartiesByRoles("CUSTOMER", delegator);
List<GenericValue> listPartyFromPartyGroup = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyFromId), null, null, null, false);
List<GenericValue> listPartyFromPerson = delegator.findList("Person", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyFromId), null, null, null, false);
if (UtilValidate.isNotEmpty(listPartyFromPerson)) {
	listPartyFromPartyGroup.addAll(listPartyFromPerson);
}
context.listPartyFrom = listPartyFromPartyGroup;

List<String> listPartyToId = SecurityUtil.getPartiesByRoles("INTERNAL_ORGANIZATIO", delegator);
List<GenericValue> listPartyTo = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyToId), null, null, null, false);
context.listPartyTo = listPartyTo;
