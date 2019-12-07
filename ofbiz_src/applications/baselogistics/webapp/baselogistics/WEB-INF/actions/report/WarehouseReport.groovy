import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;

def partyId = userLogin.getString("partyId");

if (!SalesPartyUtil.isDistributor(delegator, partyId)) {
	partyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
}

def dummy = delegator.findByAnd("Facility", UtilMisc.toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", partyId), null, true);
def facilities = "[";
def flag = false;
for (value in dummy) {
	if (flag) {
		facilities += ",";
	}
	facilities += "{ facilityId: " + "\'" + value.get("facilityId") + "\'" + ", facilityName: " + "\'" + value.get("facilityName") + "\'" + " }";
	flag = true;
}
facilities += "]";
context.facilities = facilities;

def conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "CATALOG_CATEGORY")));
conditions.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_EQUAL, "BROWSE_ROOT"));
dummy = delegator.findList("ProductCategory",
		EntityCondition.makeCondition(conditions), null, null, null, true);
def categories = "[";
flag = false;
for (value in dummy) {
	if (flag) {
		categories += ",";
	}
	categories += "{ productCategoryId: " + "\'" + value.get("productCategoryId") + "\'" + ", categoryName: " + "\'" + value.get("categoryName") + "\'" + " }";
	flag = true;
}
categories += "]";
context.categories = categories;