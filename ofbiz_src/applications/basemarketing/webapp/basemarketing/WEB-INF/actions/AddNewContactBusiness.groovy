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
	context.infoContactMechParty = getContactMechParty.get("infoContactMechParty");

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
			context.member = member;
			Map<String, Object> getContactMechMember = dispatcher.runSync("getContactMechOfParty", UtilMisc.toMap("partyId", partyIdFrom, "userLogin", userLogin));
			context.infoContactMechMember = getContactMechMember.get("infoContactMechParty");
		}
	}
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
//	get PRIMARY_LOCATION
	listConditions.clear();
	listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
	listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
			EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
		partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
		contactMechId = partyContactMechPurpose.getString("contactMechId");
		GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		if (UtilValidate.isNotEmpty(postalAddress)) {
			context.primaryLocation = postalAddress;
		}
	}
//	get SHIPPING_LOCATION
	listConditions.clear();
	String usePrimaryLocation = "false";
	listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
	listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
			EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND), null, null, null, false);
	if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
		partyContactMechPurpose = EntityUtil.getFirst(listPartyContactMechPurpose);
		if(contactMechId.equals(partyContactMechPurpose.getString("contactMechId"))){
			usePrimaryLocation = "true";
		}else {
			contactMechId = partyContactMechPurpose.getString("contactMechId");
		}
		postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		if (UtilValidate.isNotEmpty(postalAddress)) {
			context.shippingLocation = postalAddress;
		}
	}
	context.usePrimaryLocation = usePrimaryLocation;
}
context.listGeoCOUNTRY = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "COUNTRY")), UtilMisc.toSet("geoId", "geoName"), null, null, false);
context.listGeoPROVINCE = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "PROVINCE")), UtilMisc.toSet("geoId", "geoName"), null, null, false);
context.listGeoDISTRICT = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "DISTRICT")), UtilMisc.toSet("geoId", "geoName"), null, null, false);
context.listGeoWARD = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "WARD")), UtilMisc.toSet("geoId", "geoName"), null, null, false);