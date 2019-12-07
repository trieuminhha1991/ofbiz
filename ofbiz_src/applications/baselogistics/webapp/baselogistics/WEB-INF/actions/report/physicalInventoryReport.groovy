import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;

org = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.userLoginId);

def dummy = delegator.findByAnd("Facility", UtilMisc.toMap("ownerPartyId", org), null, false);
def facilities = "[";
def flag = false;
for(value in dummy) {
    if(flag) {
    	facilities += ",";
    }
    facilities += "{ facilityId: " + "\'" + value.get("facilityId") + "\'" + ", facilityName: " + "\'" + value.get("facilityName") + "\'" + " }";
    flag = true;
}
facilities += "]";
context.facilities = facilities;

dummy = delegator.findByAnd("VarianceReason", null, null, false);
def reasons = "[";
flag = false;
for(value in dummy) {
    if(flag) {
        reasons += ",";
    }
    reasons += "{ varianceReasonId: " + "\'" + value.get("varianceReasonId") + "\'" + ", description: " + "\'" + value.get("description") + "\'" + " }";
    flag = true;
}
reasons += "]";
context.reasons = reasons;