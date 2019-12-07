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
List<GenericValue> listSalesman = new ArrayList<GenericValue>();
if (UtilValidate.isNotEmpty(partyId)) {
	listSalesman = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationshipAndDetail", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "DELYS_DISTRIBUTOR", "roleTypeIdTo", "DELYS_SALESMAN_GT"), null, false));
}
context.listSalesman = listSalesman;