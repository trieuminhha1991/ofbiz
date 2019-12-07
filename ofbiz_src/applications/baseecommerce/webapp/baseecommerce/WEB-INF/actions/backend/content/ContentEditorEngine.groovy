def selectedSubMenuItem = "";
if("PRODUCT".equals(parameters.type)) {
	selectedSubMenuItem = "ListProductContent";
} else if ("TOPIC".equals(parameters.type)) {
	selectedSubMenuItem = "ListTopicContent";
}
context.selectedSubMenuItem = selectedSubMenuItem;