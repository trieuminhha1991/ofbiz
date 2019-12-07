import java.util.*;
import java.util.Map.Entry;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

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
	if ("DISTRIBUTOR".equals(module)) {
		context.selectedMenuItem = "findProductStoreDis";
		context.selectedSubMenuItem = "";
	} else {
		context.selectedMenuItem = "settingSales";
		context.selectedSubMenuItem = "listProductStores";
	}
}
