import java.util.*;
import java.util.Map.Entry;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;

String module = UtilHttp.getModule(request);
if (module) {
	if ("LOG".equals(module)) {
		context.selectedMenuItem = "DeliveryEntry";
		context.selectedSubMenuItem = "reqDeliveryOrderList";
	} else {
		context.selectedMenuItem = "reqDelivery";
		context.selectedSubMenuItem = "reqDeliveryOrderList";
	}
}