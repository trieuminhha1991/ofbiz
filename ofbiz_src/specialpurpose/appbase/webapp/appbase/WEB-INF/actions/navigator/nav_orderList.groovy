import java.util.*;
import java.util.Map.Entry;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.security.util.SecurityUtil;

/*
import com.olbius.basesales.util.SalesUtil;
Set<String> businessMenus = SalesUtil.getCurrentBusinessMenus(delegator, userLogin);
if (businessMenus) {
	Iterator<String> businessMenusIterator = businessMenus.iterator();
	while (businessMenusIterator.hasNext()) {
		String menuName = businessMenusIterator.next();
		if ("ACCOUNTANTS_MANAGER".equals(menuName)) {
			context.selectedMenuItem = "AR";
			context.selectedSubMenuItem = "salesOrderList";
			break;
		} else {
			context.selectedMenuItem = "order";
			context.selectedSubMenuItem = "salesOrderList";
			break;
		}
	}
}*/

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
