
import org.ofbiz.base.util.UtilValidate;

import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;


List<String> salesDept = SecurityUtil.getPartiesByRoles("SALES_DEPARTMENT", delegator);
if(UtilValidate.isNotEmpty(salesDept)){
	String partyId = salesDept.get(0);
	Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
	context.org = buildOrg;
}
