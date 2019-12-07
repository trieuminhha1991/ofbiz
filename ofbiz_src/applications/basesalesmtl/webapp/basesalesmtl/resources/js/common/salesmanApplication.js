$(document).ready(function() {
	SalesmanApplicationConfig.init();
});
if (typeof (SalesmanApplicationConfig) == "undefined") {
	var SalesmanApplicationConfig = (function() {
		var initJqxElements = function() {
			$("#txtRequiresGPS").jqxCheckBox({ width: 20, height: 20 });
			$("#txtTheMinimumDistance").jqxNumberInput({ width: 220, height: 30, theme: theme, inputMode: "simple", decimalDigits: 0, spinButtons: true });
			
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				DataAccess.execute({
					url: "updateApplicationSettings",
					data: { settings: SalesmanApplicationConfig.getValue() }
					}, SalesmanApplicationConfig.notify);
			});
		};
		var load = function(applicationSettingId, applicationSettingEnumId) {
			var value = DataAccess.getData({
				url: "getValueApplicationSetting",
				data: { applicationSettingId: applicationSettingId, applicationSettingTypeId: "SALESMAN_APPLICATION", applicationSettingEnumId: applicationSettingEnumId },
				source: "value"});
			return value;
		};
		var setValue = function() {
			$("#txtRequiresGPS").jqxCheckBox({ checked: SalesmanApplicationConfig.load("SALESMANAPP_RequireGPS", "REQUIRE_GPS")=="true"?true:false });
			$("#txtTheMinimumDistance").jqxNumberInput("setDecimal", SalesmanApplicationConfig.load("SALESMANAPP_TheMinimumDistance", "MINIMUM_DISTANCE"));
		};
		var getValue = function() {
			var value = [
			    { applicationSettingId: "SALESMANAPP_RequireGPS", applicationSettingTypeId: "SALESMAN_APPLICATION", applicationSettingEnumId: "REQUIRE_GPS", value: $("#txtRequiresGPS").jqxCheckBox("val").toString() },
			    { applicationSettingId: "SALESMANAPP_TheMinimumDistance", applicationSettingTypeId: "SALESMAN_APPLICATION", applicationSettingEnumId: "MINIMUM_DISTANCE", value: $("#txtTheMinimumDistance").jqxNumberInput("getDecimal").toString() },
            ];
			return JSON.stringify(value);
		};
		var notify = function(res) {
			$("#jqxNotification").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotification").jqxNotification({ template: "error"});
				$("#notificationContent").text(multiLang.updateError);
				$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#notificationContent").text(multiLang.updateSuccess);
				$("#jqxNotification").jqxNotification("open");
				setTimeout(function() {
					location.reload();
				}, 1000);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				SalesmanApplicationConfig.setValue();
			},
			load: load,
			setValue: setValue,
			getValue: getValue,
			notify: notify
		}
	})();
}