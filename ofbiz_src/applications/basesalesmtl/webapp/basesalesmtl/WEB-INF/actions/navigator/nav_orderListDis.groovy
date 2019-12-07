import java.util.*;
import java.util.Map.Entry;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;

String module = UtilHttp.getModule(request);
if (module) {
	if ("ACC".equals(module)) {
		context.selectedMenuItem = "AR";
		context.selectedSubMenuItem = "salesOrderList";
	} else if ("DISTRIBUTOR".equals(module)) {
		context.selectedMenuItem = "salesOrderDis";
		context.selectedSubMenuItem = "listSalesOrderDis";
	} else {
		context.selectedMenuItem = "salesOrderDis";
		context.selectedSubMenuItem = "listSalesOrderDis";
	}
}