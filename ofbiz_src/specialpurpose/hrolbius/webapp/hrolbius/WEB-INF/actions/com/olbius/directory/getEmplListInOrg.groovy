import org.ofbiz.entity.GenericValue;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

if(internalOrgId){
	Organization org = PartyUtil.buildOrg(delegator, internalOrgId);
	List<GenericValue> emplList = org.getEmployeeInOrg(delegator);
	context.emplList = emplList;
}