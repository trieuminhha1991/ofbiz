import org.ofbiz.entity.GenericValue;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

GenericValue userLogin = (GenericValue)context.get("userLogin");
String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

String orderId = parameters.orderId;
List<GenericValue> listOrderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")), null, null, null, false);

String partyId = "";
if(UtilValidate.isNotEmpty(listOrderRole)){
	GenericValue orderRole = listOrderRole.get(0);
	if(orderRole != null){
		partyId = orderRole.getString("partyId");
	}
}

String org = "N";
if(companyStr.equals(partyId)){
	org = "Y";
}

if (security.hasEntityPermission("LOGISTICS", "_VIEW", session)) {
	selectedMenuItem = "StockIn";
	context.selectedMenuItem = selectedMenuItem;
}

context.org = org;