import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

hasViewPermission = false;
hasCreatePermission = false;
hasUpdatePermission = false;
hasDeletePermission = false;
roleLimit = false;
if(security.hasPermission("LOGISTICS_ADMIN", session) || security.hasPermission("FACILITY_ADMIN", session)) {
    hasViewPermission = true;
	hasCreatePermission = true;
	hasUpdatePermission = true;
	hasDeletePermission = true;
}else if(security.hasPermission("LOGISTICS_CREATE", session) || security.hasPermission("FACILITY_CREATE", session)){
	hasCreatePermission = true;
}else if(security.hasPermission("LOGISTICS_VIEW", session) || security.hasPermission("FACILITY_VIEW", session)){
	hasViewPermission = true;
}else if(security.hasPermission("LOGISTICS_UPDATE", session) || security.hasPermission("FACILITY_UPDATE", session)){
	hasUpdatePermission = true;
}else if(security.hasPermission("LOGISTICS_DELETE", session) || security.hasPermission("FACILITY_DELETE", session)){
	hasDeletePermission = true;
}

if(security.hasPermission("FACILITY_ROLE_VIEW", session) && hasViewPermission == false){
	hasViewPermission = checkFacilityRole();
}
if(security.hasPermission("FACILITY_ROLE_UPDATE", session) && hasUpdatePermission == false){
	hasUpdatePermission = checkFacilityRole();
}
context.hasViewPermission = hasViewPermission;
context.hasUpdatePermission = hasUpdatePermission;
context.hasCreatePermission = hasCreatePermission;
context.hasDeletePermission = hasDeletePermission;
context.roleLimit = roleLimit;

boolean checkFacilityRole(){
	userLogin = session.getAttribute("userLogin");
	partyId = userLogin.get('partyId');
	exprList = [];
	exprList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	exprList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, context.facilityId));
	listFacilityParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(exprList, EntityOperator.AND), null, ["partyId"], null, false);
	
	listFacilityParty = EntityUtil.filterByDate(listFacilityParty);
	
	if(listFacilityParty == null || listFacilityParty.size() < 1){
		return false;
	}else{
		return true;
	}
}