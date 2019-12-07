import org.ofbiz.base.util.UtilHttp;

String module = UtilHttp.getModule(request);
if (module) {
	if ("ACC".equals(module)) {
		parameters.selectedMenuItem = "accApprovement";
		parameters.selectedSubMenuItem = "accRequirementList";
	} else if ("SALES".equals(module)) {
		parameters.selectedMenuItem = "requirement";
		parameters.selectedSubMenuItem = "reqSalesTransferList";
	} else {
	//	parameters.selectedMenuItem = "LogRequirement";
	//	parameters.selectedSubMenuItem = "ListRequirement";
	}
}
