import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil

import javolution.util.FastList;
import javolution.util.FastMap;

Map<String, Object> organization = FastMap.newInstance();
GenericValue partyOrg = delegator.findOne("Party", ["partyId": "company"], true);
if (partyOrg) {
	String fixOrganizationId = "company";
	organization.partyId = partyOrg.partyId;
	organization.partyCode = partyOrg.partyCode;
	organization.preferredCurrencyUomId = partyOrg.preferredCurrencyUomId;
	
	GenericValue partyGroupOrg = delegator.findOne("PartyGroup", ["partyId": fixOrganizationId], true);
	if (partyGroupOrg) {
		organization.groupName = partyGroupOrg.groupName;
	}
	
	// get telecom number
	List<EntityCondition> conds = FastList.newInstance();
	conds.add(EntityUtil.getFilterByDateExpr());
	conds.add(EntityCondition.makeCondition("partyId", fixOrganizationId));
	conds.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_PHONE"));
	GenericValue pcmpPhone = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conds), null, null, null, false));
	if (pcmpPhone) {
		GenericValue telecomPhone = delegator.findOne("TelecomNumber", ["contactMechId": pcmpPhone.contactMechId], false);
		if (telecomPhone) organization.phoneNumber = telecomPhone.contactNumber;
	}
}
context.organization = organization;

GenericValue isHaveSubsidiary = delegator.findOne("SystemConfig", ["systemConfigId": "isHaveSubsidiary"], true);
context.isHaveSubsidiary = isHaveSubsidiary;

// check user human resource manager
boolean isExistHrManager = false;
List<EntityCondition> conds = new ArrayList<EntityCondition>();
conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "HR_MANAGER"));
conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
conds.add(EntityUtil.getFilterByDateExpr());
GenericValue hrManagerRel = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), UtilMisc.toSet("partyIdFrom", "partyIdTo"), null, null, true));
if (hrManagerRel != null) {
	isExistHrManager = true;
}
context.isExistHrManager = isExistHrManager;

// check user olbius admin
boolean isExistOlbiusAdmin = false;
List<EntityCondition> conds2 = new ArrayList<EntityCondition>();
conds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "SYS_ADMINISTRATOR"));
conds2.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
conds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
conds2.add(EntityUtil.getFilterByDateExpr());
GenericValue olbiusAdminRel = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds2), UtilMisc.toSet("partyIdFrom", "partyIdTo"), null, null, true));
if (olbiusAdminRel != null) {
	isExistOlbiusAdmin = true;
}
context.isExistOlbiusAdmin = isExistOlbiusAdmin;

