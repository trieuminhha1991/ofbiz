import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basehr.util.PartyUtil;

Map<String, Object> our = FastMap.newInstance();

def organizationId = PartyUtil.getRootOrganization(delegator, null);
our.putAll(delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true));

List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
List<GenericValue> dummy = delegator.findList("PartyContactMechPurpose",
		EntityCondition.makeCondition(conditions), null, null, null, true);
if (UtilValidate.isNotEmpty(dummy)) {
	def fullName = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false).getString("fullName");
	if (UtilValidate.isNotEmpty(fullName)) {
		fullName = fullName.replaceAll(", __", "");
	}
	our.companyAddress = fullName;
}

conditions.clear();
conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_PHONE")));
dummy = delegator.findList("PartyContactMechPurpose",
		EntityCondition.makeCondition(conditions), null, null, null, true);
if (UtilValidate.isNotEmpty(dummy)) {
	our.contactNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), true).getString("contactNumber");
}

conditions.clear();
conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", organizationId)));
dummy = delegator.findList("PartyTaxAuthInfo", EntityCondition.makeCondition(conditions), null, null, null, true);
if (UtilValidate.isNotEmpty(dummy)) {
	our.taxIdCompany = EntityUtil.getFirst(dummy).getString("partyTaxId");
}

if (UtilValidate.isNotEmpty(orderHeader)) {
	conditions.clear();
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderHeader.orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
	dummy = delegator.findList("OrderContactMech",
			EntityCondition.makeCondition(conditions), null, null, null, false);
	if (UtilValidate.isNotEmpty(dummy)) {
		conditions.clear();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId"), "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
		dummy = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(conditions), null, null, null, true);
		if (UtilValidate.isNotEmpty(dummy)) {
			GenericValue fa = delegator.findOne("Facility", UtilMisc.toMap("facilityId", EntityUtil.getFirst(dummy).get("facilityId")), true);
			def facilityId = EntityUtil.getFirst(dummy).get("facilityId");
			our.facilityName = fa.getString("facilityName");
			our.facilityId = facilityId;
			our.facilityCode = fa.getString("facilityCode");;
			
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "PRIMARY_PHONE")));
			dummy = delegator.findList("FacilityContactMechPurpose",
					EntityCondition.makeCondition(conditions), null, null, null, true);
			if (UtilValidate.isNotEmpty(dummy)) {
				our.facilityContactNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), true).getString("contactNumber");
			}
		}
	}
}

if (UtilValidate.isNotEmpty(shippingAddressList)) {
	for (Object x : shippingAddressList) {
		our.facilityAddress = x;
		break;
	}
}

if (UtilValidate.isNotEmpty(displayParty)) {
	conditions.clear();
	conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", displayParty.partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
	dummy = delegator.findList("PartyContactMechPurpose",
			EntityCondition.makeCondition(conditions), null, null, null, false);
	if (UtilValidate.isNotEmpty(dummy)) {
		def fullName = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false).getString("fullName");
		if (UtilValidate.isNotEmpty(fullName)) {
			fullName = fullName.replaceAll(", __", "");
		}
		our.supplierAddress = fullName;
	}

	conditions.clear();
	conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", displayParty.partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE")));
	dummy = delegator.findList("PartyContactMechPurpose",
			EntityCondition.makeCondition(conditions), null, null, null, true);
	if (UtilValidate.isNotEmpty(dummy)) {
		our.supplierContactNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), true).getString("contactNumber");
	}
}

context.our = our;