import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

String partyId = userLogin.getString("partyId");
println("ss" + partyId);
List<GenericValue> listCustomer = new ArrayList<GenericValue>();
println("trunganh1111");
if (UtilValidate.isNotEmpty(partyId)) {
	try {
		List<GenericValue> listRouter = new ArrayList<GenericValue>();
		//get list salesman of Distributor. Is salesman = partyIdTo
		List<GenericValue> listSM = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "DELYS_DISTRIBUTOR", "roleTypeIdTo", "DELYS_SALESMAN_GT"), null, false));
		if (listSM != null) {
			for (GenericValue smItem : listSM) {
				//get list router of each salesman. Is router = partyIdFrom
				List<GenericValue> listRouterTemp = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", smItem.getString("partyIdTo"), "roleTypeIdFrom", "DELYS_ROUTE", "roleTypeIdTo", "DELYS_SALESMAN_GT"), null, false));
				println("asshole"+ smItem.getString("partyIdTo"));
				if (listRouterTemp != null) {
					listRouter.addAll(listRouterTemp);
				}
			}
		}
		for (GenericValue routerItem : listRouter) {
			// get list customer of each route. Is customer = partyIdTo
			List<GenericValue> listCustTemp = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", UtilMisc.toMap("partyIdFrom", routerItem.getString("partyIdFrom"), "roleTypeIdFrom", "DELYS_ROUTE", "roleTypeIdTo", "DELYS_CUSTOMER_GT"), null, false));
			if (listCustTemp != null) {
				listCustomer.addAll(listCustTemp);
			}
		}
	} catch (Exception e) {
		String errMsg = "Fatal error calling jqGetListCDARInvoice service: " + e.toString();
		Debug.logError(e, errMsg, module);
	}
}
context.listCustomer = listCustomer;