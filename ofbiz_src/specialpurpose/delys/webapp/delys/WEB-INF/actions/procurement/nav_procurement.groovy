import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.util.SalesPartyUtil;

Set<String> businessMenus = SalesPartyUtil.getBusinessMenusCurrently(delegator, userLogin);
if (businessMenus) {
	String selectedSubMenuItem = "";
	String selectedMenuItem = "";
	String roleTypeId = "";
	Iterator<String> businessMenusIterator = businessMenus.iterator();
	while (businessMenusIterator.hasNext()) {
		String menuName = businessMenusIterator.next();
		if ("DELYS_PROCURMENT".equals(menuName)) {
			selectedMenuItem = "listShoppingPoroposal";
			roleTypeId = "DELYS_PROCURMENT";
			break;
		} else if ("DELYS_REQUEST".equals(menuName)) {
			selectedMenuItem = "request";
			roleTypeId = "DELYS_REQUEST";
			break;
		} else if ("DELYS_CEO".equals(menuName)) {
			roleTypeId = "DELYS_CEO";
			selectedMenuItem = "procurement";
			selectedSubMenuItem  = "listShoppingPoroposal";
			break;
		}
	}
	context.selectedSubMenuItem =selectedSubMenuItem ;
	context.selectedMenuItem = selectedMenuItem;
	context.roleTypeId = roleTypeId;
}
