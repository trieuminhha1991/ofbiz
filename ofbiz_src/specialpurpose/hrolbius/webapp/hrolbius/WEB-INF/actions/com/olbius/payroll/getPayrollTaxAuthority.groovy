import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

EntityExpr condition = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "PAYROLL_TAX_AUTH");
listParty = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
List listCondition = new ArrayList();
for(int i = 0;i < listParty.size();i++){
	EntityExpr tmpCondition = EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, listParty.get(i).get("partyId"));
	listCondition.add(tmpCondition);
}
if((listCondition == null) ||(listCondition.size() == 0)){
	context.taxAuthorityList = null;
}else{
	context.taxAuthorityList = delegator.findList("TaxAuthority", EntityCondition.makeCondition(listCondition,EntityJoinOperator.OR), 
															UtilMisc.toSet("taxAuthGeoId","taxAuthPartyId"), null, null, false);
}															