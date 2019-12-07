import java.util.*;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.widget.menu.MenuFactory;
import org.ofbiz.base.util.UtilHttp;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;

import org.ofbiz.widget.menu.ModelMenu;

if (userLogin != null){
	List<String> businessMenus = SecurityUtil.getBussinessRoles(userLogin.partyId, delegator);
	for (bMenu in businessMenus) {
		String applicationMenuLocation = "component://" + componentNameSc + "/widget/" + componentResourceName + "Menus.xml";
		ModelMenu appModelMenu = MenuFactory.getMenuFromLocation(applicationMenuLocation,bMenu,delegator,dispatcher);
		String module = UtilHttp.getModule(request);
		if (appModelMenu.getModule().equals(module)) {
			/*GenericValue titleRoleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessTitle"), false);
			context.businessTitle = titleRoleTypeAttr.getString("attrValue");*/
			context.businessTitle = appModelMenu.getModuleName();
		}
	}
	
	List<String> listSubsidiaries = SecurityUtil.getPartiesByRoles("SUBSIDIARY", delegator);
	for (subsidiary in listSubsidiaries) {
		String groupName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, subsidiary, false);
		String orgId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		if (subsidiary == orgId) {
			context.organizationName = groupName;
		}
	}
	
}