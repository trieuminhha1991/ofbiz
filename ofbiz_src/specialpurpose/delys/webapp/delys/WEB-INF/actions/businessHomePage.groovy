import java.util.*;
import org.ofbiz.base.util.UtilMisc;
String partyId = userLogin.getString("partyId");
List<GenericValue> roleTypeList = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId), null, false);
Set<String> businessMenus = new HashSet<String>();
for (roleType in roleTypeList) {
	if (context.businessHomePage ==null){
		String roleTypeId = roleType.getString("roleTypeId");
		GenericValue homePageRoleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessHomePage"), false);
		if (homePageRoleTypeAttr != null){
			context.businessHomePage =homePageRoleTypeAttr.getString("attrValue");
		}
	}
}