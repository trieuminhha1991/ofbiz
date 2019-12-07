import org.ofbiz.base.util.UtilMisc;

import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceUtil;

facility = null;
if (facilityId) {
	facility = delegator.findOne("Facility", [facilityId : facilityId], false);
}

ownerAcctgPref = null;
if (facility) {
	owner = facility.getRelatedOne("OwnerParty", false);
	if (owner) {
		result = dispatcher.runSync("getPartyAccountingPreferences", [organizationPartyId : owner.partyId, userLogin : request.getAttribute("userLogin")]);
		if (!ServiceUtil.isError(result) && result.partyAccountingPreference) {
			ownerAcctgPref = result.partyAccountingPreference;
		}
	}
}

context.currencyUomId = ownerAcctgPref.baseCurrencyUomId;