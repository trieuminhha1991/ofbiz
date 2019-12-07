import java.util.*;
import java.util.Map.Entry;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

String module = UtilHttp.getModule(request);
if (module) {
	if ("ACC".equals(module)) {
		context.selectedMenuItem = "AP";
		context.selectedSubMenuItem = "purchaseOrderList";
	} 
}