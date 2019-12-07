import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.ServiceUtil

import com.olbius.basesales.util.SalesPartyUtil;;

String partyId = parameters.partyId;
if (SalesPartyUtil.isDistributor(delegator, userLogin.partyId)) {
	partyId = userLogin.partyId;
}
if (partyId) {
	Map<String, Object> resultValueLoadDis = dispatcher.runSync("loadDistributorInfo", UtilMisc.toMap("partyId", partyId, "detail", "Y"));
	if (ServiceUtil.isSuccess(resultValueLoadDis)) {
		Map<String, Object> distributorInfo = resultValueLoadDis.distributorInfo;
		context.distributorInfo = distributorInfo;
	}
}
context.breadcrumbCustomName = UtilProperties.getMessage("CommonUiLabels", "CommonProfile", locale);