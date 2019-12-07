import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import javolution.util.FastList;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;

context.organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

def dummy = delegator.findList("ProductStore", null, null, null, null, false);
def productStores = "[";
def flag = false;
for(value in dummy) {
    if(flag) {
    	productStores += ",";
    }
    productStores += "{ productStoreId: " + "\'" + value.get("productStoreId") + "\'" + ", storeName: " + "\'" + value.get("storeName") + "\'" + " }";
    flag = true;
}
productStores += "]";
context.productStores = productStores;

def conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("termTypeId", "CUSTOMER_TERM_TEXT", "attrName", "defaultValue")));
dummy = delegator.findList("TermTypeAttr",
		EntityCondition.makeCondition(conditions), null, null, null, false);
def agreementTerm = "";
for(value in dummy) {
    agreementTerm += value.attrValue;
}
context.agreementTerm = agreementTerm;


List<String> listPartyFromId = SecurityUtil.getPartiesByRoles("CUSTOMER", delegator);
List<GenericValue> listPartyFromPartyGroup = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyFromId), null, null, null, false);
List<GenericValue> listPartyFromPerson = delegator.findList("Person", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyFromId), null, null, null, false);
if (UtilValidate.isNotEmpty(listPartyFromPerson)) {
	listPartyFromPartyGroup.addAll(listPartyFromPerson);
}
context.listPartyFrom = listPartyFromPartyGroup;

List<String> listPartyToId = SecurityUtil.getPartiesByRoles("INTERNAL_ORGANIZATIO", delegator);
List<GenericValue> listPartyTo = delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listPartyToId), null, null, null, false);
context.listPartyTo = listPartyTo;