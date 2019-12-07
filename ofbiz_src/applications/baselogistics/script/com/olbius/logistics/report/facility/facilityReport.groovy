import javolution.util.FastList;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.salesmtl.DistributorServices;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.security.util.SecurityUtil;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;

Delegator dlg = delegator;
def facis = LogisticsFacilityUtil.getFacilityAllowedView(delegator, userLogin);
def dummy = null;
if (!facis.isEmpty()){
    dummy = delegator.findList("Facility",
		EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityJoinOperator.IN, facis)), null, null, null, false);
}
def facilities = null;
def originalFacilities = null;
if (dummy != null){ 
	facilities = "[";
	originalFacilities = "[";
	def flag = false;
	for(value in dummy) {
	    if(flag) {
	    	facilities += ",";
	    	originalFacilities += ",";
	    }
	    facilities += "{ facilityId: " + "\'" + value.get("facilityId") + "\'" + ", facilityCode: " + "\'" + value.get("facilityCode") + "\'" + ", facilityName: " + "\'" + value.get("facilityName") + "\'" + " }";
	    originalFacilities += "\'" + value.get("facilityId") + "\'";
	    flag = true;
	}
	facilities += "]";
	originalFacilities += "]";
}

context.facilities = facilities;
context.originalFacilities = originalFacilities;