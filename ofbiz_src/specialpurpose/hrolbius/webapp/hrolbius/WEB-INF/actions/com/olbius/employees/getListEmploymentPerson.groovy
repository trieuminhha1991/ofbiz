import java.util.List;
import java.util.Properties;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

partyIdFrom = parameters.partyIdFrom;
parameters.partyIdFrom = null;
//context.partyIdFromSearch = partyIdFrom;
userLogin = session.getAttribute("userLogin");

if(partyIdFrom){
	rootPartyId = partyIdFrom;
}else{
	Properties generalProp = UtilProperties.getProperties("general");
	rootPartyId = (String)generalProp.get("ORGANIZATION_PARTY");
}

Organization org = PartyUtil.buildOrg(delegator, rootPartyId);
List<GenericValue> allListEmpl = org.getEmployeeInOrg(delegator);

List<String> allListEmplId = EntityUtil.getFieldListFromEntityList(allListEmpl, "partyIdTo", true);
parameters.partyIdTo_fld1_value = allListEmplId;
parameters.partyIdTo_fld1_op = "in";

Map<String, Object> results = dispatcher.runSync("performFind", 
														UtilMisc.toMap("entityName", "EmploymentAndPerson",
																		"inputFields", parameters,
																		"noConditionFind", "N", 
																		"userLogin", userLogin));
																	
EntityListIterator listIt = (EntityListIterator)results.get("listIt");
//println("listIt: " + listIt);
if (listIt != null){
	context.listIt = listIt.getCompleteList();
	listIt.close();
}else{
	context.listIt = FastList.newInstance();
}




