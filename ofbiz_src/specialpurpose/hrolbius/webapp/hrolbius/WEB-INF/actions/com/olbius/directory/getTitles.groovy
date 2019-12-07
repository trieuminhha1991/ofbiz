import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
titles = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId","MANAGER"), null, null ,null, false);
parentOrgId= parameters.parentOrgId;
println ("parentOrgId: " + parentOrgId);
roleType=null;
if(parentOrgId !=null){
	//System.out.println("abcd"+parentOrgId);
	roleType=delegator.findList("PartyRoleDetailAndPartyDetail", EntityCondition.makeCondition("partyId",parentOrgId),null, null ,null, false);
}
context.titles = titles;
context.roleType=roleType;