	$(document).ready(function() {
		var grid = $("#ListContactBusiness");
		$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
		if(getCookie().checkContainValue("newContact")){
			deleteCookie("newContact");
			$("#jqxNotificationNested").jqxNotification("closeLast");
			$("#jqxNotificationNested").jqxNotification({ template: "info"});
	        $("#notificationContentNested").text(multiLang.addSuccess);
	        $("#jqxNotificationNested").jqxNotification("open");
		 }
		if(getCookie().checkContainValue("updateContact")){
			deleteCookie("updateContact");
			$("#jqxNotificationNested").jqxNotification("closeLast");
			$("#jqxNotificationNested").jqxNotification({ template: "info"});
			$("#notificationContentNested").text(multiLang.updateSuccess);
			$("#jqxNotificationNested").jqxNotification("open");
		}
	});