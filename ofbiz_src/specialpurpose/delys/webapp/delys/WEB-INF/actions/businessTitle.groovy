import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.widget.menu.MenuFactory;
import org.ofbiz.base.util.UtilHttp;

if (userLogin != null){
String partyId = userLogin.getString("partyId");
List<GenericValue> roleTypeList = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId), null, false);
Set<String> businessMenus = new HashSet<String>();
for (roleType in roleTypeList) {
	if (context.businessTitle ==null){
		String roleTypeId = roleType.getString("roleTypeId");
		GenericValue menuRoleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessMenu"), false);
		if (menuRoleTypeAttr != null && menuRoleTypeAttr.getString("attrValue") != null){
			applicationMenuLocation = "component://delys/widget/DelysMenus.xml";
			appModelMenu = MenuFactory.getMenuFromLocation(applicationMenuLocation,menuRoleTypeAttr.getString("attrValue"),delegator,dispatcher)
			module = UtilHttp.getModule(request)
			if(appModelMenu.getModule().equals(module)){
				GenericValue titleRoleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessTitle"), false);
				context.businessTitle = titleRoleTypeAttr.getString("attrValue");
			}
		}
	}
}
}