import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.util.PartyUtil;

if(emplTerminationProposal){
	statusId = emplTerminationProposal.statusId;
	String headOfHR = PartyUtil.getHrmAdmin(delegator);
	String ceoId = PartyUtil.getCEO(delegator);
	context.statusEdit = false;
	if(statusId.equalsIgnoreCase("TER_PPSL_CREATED")){
		context.statusIdList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "SACKING_HOHR_APPL"), null, false);
		if(userLogin.partyId.equalsIgnoreCase(headOfHR)){
			context.statusEdit = true;
		}
	}else if(statusId.equalsIgnoreCase("SACKING_HOHR_A")){
		context.statusIdList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "SACKING_CEO_APPL"), null, false);	
		if(userLogin.partyId.equalsIgnoreCase(ceoId)){
			context.statusEdit = true;
		}
	}else{
		context.statusIdList = FastList.newInstance();
	}
	context.currStatusId = statusId;
}