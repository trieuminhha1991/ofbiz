import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.PartyUtil;


List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("isClosed", EntityJoinOperator.EQUALS, "N"));
conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
def dummy = delegator.findList("StockEvent", EntityCondition.makeCondition(conditions),
		null, null, null, false);

def company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
conditions.clear();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)));
conditions.add(
		EntityCondition.makeCondition("facilityId", EntityJoinOperator.IN, EntityUtil.getFieldListFromEntityList(dummy, "facilityId", true)));
dummy = delegator.findList("Facility", EntityCondition.makeCondition(conditions),
		null, null, null, false);
def facilityStocked = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		facilityStocked += ",";
	}
	facilityStocked += "\'" + value.get("facilityId") + "\'";
	flag = true;
}
facilityStocked += "]";
context.facilityStocked = facilityStocked;

conditions.clear();
if (PartyUtil.isDistributor(delegator, userLogin.getString("partyId"), company)){ 
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", userLogin.getString("partyId"))));
} else { 
	conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)));
}
dummy = delegator.findList("Facility", EntityCondition.makeCondition(conditions),
		null, null, null, false);
def facilities = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		facilities += ",";
	}
	facilities += "{ value: " + "\'" + value.get("facilityId") + "\'" + ", text: " + "\'" + value.get("facilityName") + "\'" + " }";
	flag = true;
}
facilities += "]";
context.facilities = facilities;