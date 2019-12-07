import com.olbius.util.PartyUtil;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;

partyId = userLogin.get("partyId");
internalOrgId = PartyUtil.getOrgByManager(partyId, delegator);
internalOrg = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId", internalOrgId),false);
if(UtilValidate.isNotEmpty(internalOrg)){
	internalOrgName = internalOrg.get("groupName");
}else{
	internalOrgName = "";
}


context.internalOrgName = internalOrgName
context.internalOrgId = internalOrgId;