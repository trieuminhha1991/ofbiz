import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;


def organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
def dummy = delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("organizationPartyId", organizationId), null, true);
def mapTimePeriod = "{";
def flag = false;
for(value in dummy) {
	if(flag) {
		mapTimePeriod += ",";
	}
	mapTimePeriod += "\'" + value.getString("customTimePeriodId") + "\'" + ":" + "\'" + value.getDate("thruDate") + "\'";
	flag = true;
}
mapTimePeriod += "}";
context.mapTimePeriod = mapTimePeriod;