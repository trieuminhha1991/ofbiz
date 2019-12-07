import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import com.olbius.basehr.util.MultiOrganizationUtil;

def ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
context.ownerPartyId = ownerPartyId;

def conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, userLogin.get("partyId")));
List<GenericValue> dummy = delegator.findList("FacilityParty",
		EntityCondition.makeCondition(conditions), null, null, null, false);

conditions.clear();
conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.IN,
		EntityUtil.getFieldListFromEntityList(dummy, "facilityId", true)));
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", ownerPartyId)));
dummy = delegator.findList("Facility",
		EntityCondition.makeCondition(conditions), null, null, null, false);
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