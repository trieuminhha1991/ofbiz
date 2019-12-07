import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.PartyUtil;

partyId = parameters.partyId;
if(!partyId){
	emplList = PartyUtil.getListEmployeeOfManager(delegator, userLogin.partyId);
	parameters.partyId_op = "in";
	parameters.partyId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", false);
}
	

results = dispatcher.runSync("performFind", UtilMisc.toMap("entityName", "PartyPunishmentRemindAndWarning",
												 "inputFields", parameters,
												 "noConditionFind", "Y",
												 "viewSize", parameters.VIEW_SIZE,
												 "viewIndex", parameters.VIEW_INDEX));

if(ServiceUtil.isSuccess(results)){
	context.emplList = results.get("listIt");
}else{
	context.emplList = FastList.newInstance();
}

