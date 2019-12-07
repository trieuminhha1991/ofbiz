import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.util.SalesPartyUtil;

Set<String> businessMenus = SalesPartyUtil.getBusinessMenusCurrently(delegator, userLogin);
if (businessMenus) {
	Iterator<String> businessMenusIterator = businessMenus.iterator();
	while (businessMenusIterator.hasNext()) {
		String menuName = businessMenusIterator.next();
		if ("DELYS_SALESADMIN_GT".equals(menuName) || "SALESADMIN_MANAGER".equals(menuName) || "DELYS_NBD".equals(menuName)) {
			context.selectedMenuItem = "otherSales";
			context.selectedSubMenuItem = "orderListSm";
			break;
		} else if ("DELYS_ACCOUNTANTS".equals(menuName)) {
			context.selectedMenuItem = "accApprovement";
			context.selectedSubMenuItem = "accSaleOrderList";
			break;
		} else if ("DELYS_DISTRIBUTOR".equals(menuName)) {
			context.selectedMenuItem = "salesOrderDis";
			context.selectedSubMenuItem = "distributorListSO";
			/*if ("purcharseOrderListDis".equals(requestNameScreen)) {
				context.selectedMenuItem = "distributorListPO";
				context.selectedSubMenuItem = "";
			} else if ("salesOrderListDis".equals(requestNameScreen)) {
				context.selectedMenuItem = "distributorListSO";
				context.selectedSubMenuItem = "";
			}*/
			break;
		} else if ("DELYS_SALESSUP_GT".equals(menuName)) {
			context.selectedMenuItem = "order";
			context.selectedSubMenuItem = "orderListSm";
			break;
		} else if ("DELYS_LOGISTICS_GT".equals(menuName)) {
			context.selectedMenuItem = "deliverySub";
			context.selectedSubMenuItem = "orderList";
			break;
		}
	}
}
