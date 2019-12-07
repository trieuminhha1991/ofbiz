import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

List<GenericValue> listStatus = delegator.findList("StatusItem", EntityCondition.makeCondition(UtilMisc.toMap("statusTypeId", "AGREEMENT_STATUS")), null, null, null, false);
context.listStatus = listStatus;

List<GenericValue> listPartyShipping = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition(UtilMisc.toMap("roleTypeId", "SHIPPING_LINE")), null, null, null, false);;
context.listPartyShipping = listPartyShipping;
