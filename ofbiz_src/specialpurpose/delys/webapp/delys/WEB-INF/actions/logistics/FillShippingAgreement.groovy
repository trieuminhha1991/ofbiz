import java.util.List;

import javolution.util.FastList;

import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

//Parameters
agreementId = parameters.agreementId;

//Get partyFrom
shippingAgreement = delegator.findOne("Agreement", [agreementId : agreementId], false);
partyFromName = delegator.findOne("PartyNameView", [partyId : shippingAgreement.partyIdFrom], false);

//Get Representative From
finAgrRoleCond = EntityCondition.makeCondition([EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, agreementId),
              EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REPRESENT_PARTY_FROM")], EntityOperator.AND);
repFrom = delegator.findList("AgreementRole", finAgrRoleCond, null, null, null, false);
repFromName = delegator.findOne("PartyNameView", [partyId : repFrom.get(0).partyId], false);

//Get Empl Position Type From
emplPosTypeFrom = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "EMPL_POS_ID_FROM"), false);
if(UtilValidate.isNotEmpty(emplPosTypeFrom)){
	emplPosTypeFromDes = delegator.findOne("EmplPositionType",UtilMisc.toMap("emplPositionTypeId", emplPosTypeFrom.attrValue), false);
	context.emplPosTypeFromDes = emplPosTypeFromDes.description;
}

//Get Postal Address From
addressFromAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "ADDRESS_FROM"), false);
if(UtilValidate.isNotEmpty(addressFromAttr)){
	postAddressFrom = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", addressFromAttr.attrValue), false);
	context.postalAddressFrom = postAddressFrom;
}

//Get Phone Number From
phoneNumberFromAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "TELECOM_FROM"), false);
if(UtilValidate.isNotEmpty(phoneNumberFromAttr)){
	telecomNumberFrom = delegator.findOne("TelecomNumber", [contactMechId : phoneNumberFromAttr.attrValue], false);
	context.telecomNumberFrom = telecomNumberFrom;
}

//Get FaxNum From
faxNumberFromAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "FAX_FROM"), false);
if(UtilValidate.isNotEmpty(faxNumberFromAttr)){
	faxNumberFrom = delegator.findOne("FaxNumber", [contactMechId : faxNumberFromAttr.attrValue], false);
	context.faxNumberFrom = faxNumberFrom;
}

//Get partyTax From
taxFromAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "TAX_FROM"), false);
if(UtilValidate.isNotEmpty(taxFromAttr)){
	context.taxFrom = taxFromAttr.attrValue;
}

//Get partyTo
partyToName = delegator.findOne("PartyNameView", [partyId : shippingAgreement.partyIdTo], false);

//Get Representative To
finAgrRoleCond = EntityCondition.makeCondition([EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, agreementId),
			  EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REPRESENT_PARTY_TO")], EntityOperator.AND);
repTo = delegator.findList("AgreementRole", finAgrRoleCond, null, null, null, false);
repToName = delegator.findOne("PartyNameView", [partyId : repTo.get(0).partyId], false);

//Get Empl Position Type To
emplPosTypeTo = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "EMPL_POS_ID_TO"), false);
if(UtilValidate.isNotEmpty(emplPosTypeTo)){
	emplPosTypeToDes = delegator.findOne("EmplPositionType",UtilMisc.toMap("emplPositionTypeId", emplPosTypeTo.attrValue), false);
	context.emplPosTypeToDes = emplPosTypeToDes.description;
}

//Get Postal Address To
addressToAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "ADDRESS_TO"), false);
if(UtilValidate.isNotEmpty(addressToAttr)){
	postalAddressTo = delegator.findOne("PostalAddress", [contactMechId : addressToAttr.attrValue], false);
	context.postalAddressTo = postalAddressTo;
}

//Get Phone Number To
phoneNumberToAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "TELECOM_TO"), false);
if(UtilValidate.isNotEmpty(phoneNumberToAttr)){
	telecomNumberTo = delegator.findOne("TelecomNumber", [contactMechId : phoneNumberToAttr.attrValue], false);
	context.telecomNumberTo = telecomNumberTo;
}

//Get FaxNum To
faxNumberToAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "FAX_TO"), false);
if(UtilValidate.isNotEmpty(faxNumberToAttr)){
	faxNumberTo = delegator.findOne("FaxNumber", [contactMechId : faxNumberToAttr.attrValue], false);
	context.faxNumberTo = faxNumberTo;
}

//Get partyTax From
taxToAttr = delegator.findOne("AgreementAttribute",UtilMisc.toMap("agreementId", shippingAgreement.agreementId, "attrName", "TAX_TO"), false);
if(UtilValidate.isNotEmpty(taxToAttr)){
	context.taxTo = taxToAttr.attrValue;
}


//Set to context
context.repFromName = repFromName;
context.partyFromName = partyFromName;
context.partyToName = partyToName;
context.repToName = repToName;
