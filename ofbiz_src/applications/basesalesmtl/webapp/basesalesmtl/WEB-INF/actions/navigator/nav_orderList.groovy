import java.util.*;
import java.util.Map.Entry;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.security.util.SecurityUtil;

/*
import com.olbius.salesmtl.SalesmtlUtil;

Set<String> businessMenus = SalesmtlUtil.getBusinessMenusCurrently(delegator, userLogin);
if (businessMenus) {
	Iterator<String> businessMenusIterator = businessMenus.iterator();
	while (businessMenusIterator.hasNext()) {
		String menuName = businessMenusIterator.next();
		if ("SALESADMIN_GT".equals(menuName) || "SALESADMIN_MT".equals(menuName) || "SALESADMIN_MANAGER".equals(menuName) || "NBD".equals(menuName)) {
			context.selectedMenuItem = "order";
			context.selectedSubMenuItem = "orderList";
			break;
		} else if ("ACCOUNTANTS".equals(menuName)) {
			context.selectedMenuItem = "accApprovement";
			context.selectedSubMenuItem = "accOrderApprovement";
			break;
		} else if ("DISTRIBUTOR".equals(menuName)) {
			context.selectedMenuItem = "purchaseOrderDis";
			context.selectedSubMenuItem = "distributorListPO";
			//if ("purcharseOrderListDis".equals(requestNameScreen)) {
			//	context.selectedMenuItem = "distributorListPO";
			//	context.selectedSubMenuItem = "";
			//} else if ("salesOrderListDis".equals(requestNameScreen)) {
			//	context.selectedMenuItem = "distributorListSO";
			//	context.selectedSubMenuItem = "";
			//}
			break;
		} else if ("SALESSUP_GT".equals(menuName) || "SALESSUP_MT".equals(menuName)) {
			context.selectedMenuItem = "order";
			break;
		} else if ("LOGISTICS_GT".equals(menuName)) {
			context.selectedMenuItem = "deliverySub";
			context.selectedSubMenuItem = "orderList";
			break;
		}
	}
}
*/

String module = UtilHttp.getModule(request);
if (module) {
	if ("ACC".equals(module)) {
		context.selectedMenuItem = "AR";
		context.selectedSubMenuItem = "salesOrderList";
	} else if ("LOG".equals(module)){
		if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "LOG_SALES_ORDER")){
			context.selectedMenuItem = "order";
			context.selectedSubMenuItem = "salesOrderList";
		} else {
			context.selectedMenuItem = "StockOut";
			context.selectedSubMenuItem = "salesOrderList";
		}
	} else if ("DISTRIBUTOR".equals(module)) {
		context.selectedMenuItem = "purchaseOrderDis";
		context.selectedSubMenuItem = "listPurchOrderDis";
	} else {
		context.selectedMenuItem = "order";
		context.selectedSubMenuItem = "salesOrderList";
	}
}