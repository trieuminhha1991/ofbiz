import org.ofbiz.base.util.*;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.condition.*;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.webapp.control.*;
import org.ofbiz.entity.DelegatorFactory;

context.autoUserLogin = session.getAttribute("autoUserLogin");
context.autoLogoutUrl = LoginWorker.makeLoginUrl(request, "autoLogout");

previousParams = session.getAttribute("_PREVIOUS_PARAMS_");
if (previousParams) {
    previousParams = UtilHttp.stripNamedParamsFromQueryString(previousParams, ['USERNAME', 'PASSWORD']);
    previousParams = "?" + previousParams;
} else {
    previousParams = "";
}
context.previousParams = previousParams;


def productStores = delegator.findByAnd("ProductStore", UtilMisc.toMap("salesMethodChannelEnumId", "SMCHANNEL_POS"), null, true);
def mapPosTerminal = "{";
def flag = false;
for (productStore in productStores) {
	if (flag) {
		mapPosTerminal += ",";
	}
	def facilityId = productStore.get("inventoryFacilityId");
	def posTerminals = delegator.findByAnd("PosTerminal", UtilMisc.toMap("facilityId", facilityId), null, true);
	
	def terminals = "[";
	def flag2 = false;
	for (posTerminal in posTerminals) {
		if (flag2) {
			terminals += ",";
		}
		terminals += "{ posTerminalId: " + "\'" + posTerminal.get("posTerminalId") + "\'" + ", terminalName: " + "\'" + posTerminal.get("terminalName") + "\'" + " }";
		flag2 = true;
	}
	mapPosTerminal += facilityId + ": " + terminals + "]";
	flag = true;
}

context.productStores = productStores;
context.mapPosTerminal = mapPosTerminal + "}";