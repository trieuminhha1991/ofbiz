import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.salesmtl.DistributorServices;


Delegator dlg = delegator;

def parties = FastList.newInstance();

if ("DISTRIBUTOR".equals(reportType)) {
	if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))) {
		parties = UtilMisc.toList(userLogin.getString("partyId"));
	} else {
		parties = DistributorServices.distributorOfSupervisor(delegator, userLogin);
	}
} else {
	parties = UtilMisc.toList(MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId")));
}

def dummy = delegator.findList("Facility",
		EntityCondition.makeCondition(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.IN, parties)), null, null, null, false);
def facilities = "[";
def originalFacilities = "[";
def flag = false;
for(value in dummy) {
    if(flag) {
    	facilities += ",";
    	originalFacilities += ",";
    }
    facilities += "{ facilityId: " + "\'" + value.get("facilityId") + "\'" + ", facilityName: " + "\'" + value.get("facilityName") + "\'" + " }";
    originalFacilities += "\'" + value.get("facilityId") + "\'";
    flag = true;
}
facilities += "]";
originalFacilities += "]";
context.facilities = facilities;
context.originalFacilities = originalFacilities;