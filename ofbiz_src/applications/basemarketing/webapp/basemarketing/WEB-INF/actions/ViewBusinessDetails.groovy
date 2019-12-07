import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import com.olbius.dms.util.PartyHelper;

GenericValue group = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
if (UtilValidate.isNotEmpty(group)) {
	context.group = group;

	Map<String, Object> getContactMechParty = dispatcher.runSync("getContactMechOfParty", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
	Map<String, Object> infoContactMechParty = getContactMechParty.get("infoContactMechParty");
	context.infoContactMechParty = infoContactMechParty;
	String primaryLocation = "";
	String shippingLocation = "";
	if (UtilValidate.isNotEmpty(infoContactMechParty)) {
		List<Map<String, Object>> listPrimaryLocation = (List<Map<String, Object>>) infoContactMechParty.get("listPrimaryLocation");
		for (Map<String, Object> x : listPrimaryLocation) {
			primaryLocation += (String) x.get("primaryLocation") + "<br>";
		}
		List<Map<String, Object>> listShippingLocation = (List<Map<String, Object>>) infoContactMechParty.get("listShippingLocation");
		for (Map<String, Object> x : listShippingLocation) {
			shippingLocation += (String) x.get("shippingLocation") + "<br>";
		}
	}
	context.primaryLocation = primaryLocation;
	context.shippingLocation = shippingLocation;
//	find REPRESENTATIVE
	List<EntityCondition> listConditions = FastList.newInstance();
	listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdFrom", "REPRESENTATIVE")));
	List<GenericValue> listPartyRepresentative = delegator.findList("PartyRelationship",
			EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listPartyRepresentative)) {
		GenericValue partyRepresentative = EntityUtil.getFirst(listPartyRepresentative);
		def partyIdFrom = partyRepresentative.getString("partyIdFrom");
		GenericValue member = delegator.findOne("Person", UtilMisc.toMap("partyId", partyIdFrom), false);
		if (UtilValidate.isNotEmpty(member)) {
			String idIssuePlace = member.getString("idIssuePlace");
			GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", idIssuePlace), false);
			if (UtilValidate.isNotEmpty(geo)) {
				String geoName = geo.getString("geoName");
				member.set("idIssuePlace", geoName);
			}
			context.member = member;
			String memberName = PartyHelper.getPartyName(delegator, member.getString("partyId"), true, true);
			context.memberName = memberName;

			Map<String, Object> getContactMechMember = dispatcher.runSync("getContactMechOfParty", UtilMisc.toMap("partyId", partyIdFrom, "userLogin", userLogin));
			context.infoContactMechMember = getContactMechMember.get("infoContactMechParty");
		}
	}
}