import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.util.SalesPartyUtil;

Set<String> businessMenus = SalesPartyUtil.getBusinessMenusCurrently(delegator, userLogin);
if (businessMenus) {
	Iterator<String> businessMenusIterator = businessMenus.iterator();
	while (businessMenusIterator.hasNext()) {
		String menuName = businessMenusIterator.next();
		if ("DELYS_SALESADMIN_GT".equals(menuName)) {
			context.selectedMenuItem = "otherSales";
			context.selectedSubMenuItem = "orderList";
			break;
		} else if ("DELYS_ACCOUNTANTS".equals(menuName)) {
			context.selectedMenuItem = "accApprovement";
			context.selectedSubMenuItem = "accOrderList";
			break;
		} else if ("DELYS_DISTRIBUTOR".equals(menuName)) {
			if (context.containsKey("requestNameScreen")) {
				if ("purcharseOrderListDis".equals(requestNameScreen)) {
					context.selectedMenuItem = "salesOrderDis";
					context.selectedSubMenuItem = "distributorListSO";
				} else if ("salesOrderListDis".equals(requestNameScreen)) {
					context.selectedMenuItem = "salesOrderDis";
					context.selectedSubMenuItem = "distributorListSO";
				}
			}
			break;
		} else if ("DELYS_LOGISTICS_GT".equals(menuName)) {
			context.selectedMenuItem = "deliverySub";
			context.selectedSubMenuItem = "orderList";
			break;
		}
	}
}
