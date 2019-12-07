import com.olbius.basehr.util.PartyHelper;

Map<String, Object> agreementInfo =  dispatcher.runSync("loadAgreementInfo", [agreementId: parameters.agreementId, userLogin: userLogin]);

Map<String, Object> agreement = agreementInfo.agreementInfo;

agreement.put("partyNameFrom", PartyHelper.getPartyName(delegator, agreement.partyIdFrom, true, true));
agreement.put("partyNameTo", PartyHelper.getPartyName(delegator, agreement.partyIdTo, true, true));

Map<String, Object> partyIdTo =  dispatcher.runSync("getPartyInformation", [partyId: agreement.partyIdTo, userLogin: userLogin]);
Map<String, Object> partyToInfo = partyIdTo.partyInfo;
def listAddress = partyToInfo.listAddress;
for (def x : listAddress) {
	if (x.contactMechPurposeType == "PRIMARY_LOCATION") {
		partyToInfo.put("address1", x.address1);
	}
}
partyToInfo.put("representativeName", PartyHelper.getPartyName(delegator, agreement.representativeId, true, true));
agreement.put("partyToInfo", partyToInfo);

Map<String, Object> partyFromInfo = null;
switch (agreement.partyTypeFrom) {
case "RETAIL_OUTLET":
	partyFromInfo = dispatcher.runSync("loadAgentInfo", [partyId: agreement.partyIdFrom, detail: "Y", userLogin: userLogin]).agentInfo;
	break;
case "PARTY_GROUP":
	partyFromInfo = dispatcher.runSync("loadDistributorInfo", [partyId: agreement.partyIdFrom, detail: "Y", userLogin: userLogin]).distributorInfo;
	break;
default:
	break;
}
agreement.put("partyFromInfo", partyFromInfo);
context.agreement = agreement;