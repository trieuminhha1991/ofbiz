import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

partyId = parameters.parm0;
if(partyId){
	Organization org =	PartyUtil.buildOrg(delegator, partyId);	
	List<GenericValue> emplList = org.getEmployeeInOrg(delegator);
	List<String> emplListString = FastList.newInstance();
	emplListString = EntityUtil.getFieldListFromEntityList(emplList, "partyId", false);
	System.out.println('emplll' + emplListString);
	if(UtilValidate.isEmpty(emplListString)){
		emplListString.add("");
	}
	parameters.partyId_fld0_op = "in";	
	parameters.partyId_fld0_value= emplListString;
}