import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import com.olbius.util.SecurityUtil;
import org.ofbiz.base.util.UtilHttp;

if(userLogin == null){
    return;
}
listRoles = SecurityUtil.getCurrentRoles(userLogin.partyId, delegator);
exprList = [];
context.portalPageId = "defaultPortalPage";
if(!listRoles.isEmpty()){
	for(int i = 0; i < listRoles.size();i++){
		exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, listRoles.get(i)));
	}
	listPortalPages = delegator.findList("PortalPage", EntityCondition.makeCondition(exprList, EntityOperator.OR), null, null, null, false);
	context.listPortalPages = listPortalPages;
	if(listPortalPages != null && listPortalPages.size() > 0){
		for(GenericValue item : listPortalPages){
			module = UtilHttp.getModule(request);
			if(module.equals(item.moduleId)){
				context.portalPageId = item.portalPageId;
				break;
			}
		}
	}
}