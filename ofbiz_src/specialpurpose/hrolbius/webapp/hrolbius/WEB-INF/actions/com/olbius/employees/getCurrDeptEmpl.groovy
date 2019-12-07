import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;

import com.olbius.util.PartyUtil;

/*if(parameters.partyId){
	partyId = parameters.partyId;
}*/
if(emplId){
	partyId = emplId;
	GenericValue currDept = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
	if(currDept){
		context.currDept = PartyHelper.getPartyName(delegator, currDept.getString("partyIdFrom"), false);
	}else{
	context.currDept = "";
	}
	
}

