import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;

List<GenericValue> listRoleType = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", EntityJoinOperator.IN, UtilMisc.toList("FAMILY_MEMBER", "PARENT")), null, UtilMisc.toList("roleTypeId", "description"), null, false);
context.listRoleType = listRoleType;

GenericValue member = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);

context.familyId = parameters.familyId;

if (UtilValidate.isNotEmpty(member)) {
	context.member = member;

	Map<String, Object> getContactMechParty = dispatcher.runSync("getContactMechOfParty", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
	context.infoContactMechParty = getContactMechParty.get("infoContactMechParty");

	List<EntityCondition> listConditions = FastList.newInstance();
//	get PRIMARY_PHONE
	listConditions.clear();
	listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE")));
	List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
			EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
		GenericValue partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
		String contactMechId = partyContactMechPurpose.getString("contactMechId");
		listConditions.clear();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId)));
		listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND), null, null, null, false);
		if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
			partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
			String contactMechPurposeTypeId = partyContactMechPurpose.getString("contactMechPurposeTypeId");
			context.primaryPhone = contactMechPurposeTypeId;
		}
	}
//	get PHONE_SHIPPING
	listConditions.clear();
	listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_SHIPPING")));
	listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
			EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
		partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
		String contactMechId = partyContactMechPurpose.getString("contactMechId");
		listConditions.clear();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId)));
		listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND), null, null, null, false);
		if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
			partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
			String contactMechPurposeTypeId = partyContactMechPurpose.getString("contactMechPurposeTypeId");
			context.phoneShipping = contactMechPurposeTypeId;
		}
	}
}