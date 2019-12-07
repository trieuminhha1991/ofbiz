var jqxNotificationObject = (function(){
	var initJqxNotification = function(){
		$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	};
	return{
		initJqxNotification:initJqxNotification
	}
}());
$(document).ready(function(){
	jqxNotificationObject.initJqxNotification();
});