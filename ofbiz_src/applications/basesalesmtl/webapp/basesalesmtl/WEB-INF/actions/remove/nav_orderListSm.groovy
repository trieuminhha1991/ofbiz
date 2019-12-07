import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.salesmtl.SalesmtlUtil;

Set<String> businessMenus = SalesmtlUtil.getBusinessMenusCurrently(delegator, userLogin);
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
