import java.util.*;
import java.util.Map.Entry;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.basesales.util.SalesUtil;

Set<String> businessMenus = SalesUtil.getCurrentBusinessMenus(delegator, userLogin);
if (businessMenus) {
	Iterator<String> businessMenusIterator = businessMenus.iterator(); 
	while (businessMenusIterator.hasNext()) {
		String menuName = businessMenusIterator.next();
		if ("DISTRIBUTOR".equals(menuName)) {
			context.selectedMenuItem = "distributorBalance";
			context.selectedSubMenuItem = "";
			context.titleProperty = "DistributorLiabilityView";
			break;
		} else if ("ACCOUNTANTS_MANAGER".equals(menuName)) {
			context.selectedMenuItem = "";
			context.selectedSubMenuItem = "";
			break;
		}
	}
}
