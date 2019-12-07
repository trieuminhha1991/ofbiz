import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.util.SalesPartyUtil;

Set<String> businessMenus = SalesPartyUtil.getBusinessMenusCurrently(delegator, userLogin);
if (businessMenus) {
	Iterator<String> businessMenusIterator = businessMenus.iterator();
	while (businessMenusIterator.hasNext()) {
		String menuName = businessMenusIterator.next();
		if ("DELYS_SALESADMIN_GT".equals(menuName)) {
			context.selectedMenuItem = "promosProgramme";
			context.selectedSubMenuItem = "promosList";
			break;
		} else if ("DELYS_DISTRIBUTOR".equals(menuName)) {
			context.selectedMenuItem = "findPromoDis";
			context.selectedSubMenuItem = "";
			break;
		}
	}
}
