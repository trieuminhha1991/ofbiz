if (typeof (AddPermission) == "undefined") {
	var AddPermission = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: theme, width: 600, height: 300, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddPermission"), modalOpacity: 0.7
			});

			$("#txtPermissionId").jqxDropDownList({ theme: theme, width: 218, height: 25, source: [], placeHolder: multiLang.filterchoosestring});
			
			$("#txtFromDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtThruDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtThruDate").jqxDateTimeInput("setDate", null);
		};
		var handleEvents = function() {
			jqxwindow.on("open", function () {
				$("#txtPermissionId").jqxDropDownList("clearSelection");
				$("#txtThruDate").jqxDateTimeInput("setDate", null);
			});
			jqxwindow.on("close", function () {
				jqxwindow.jqxValidator("hide");
			});

			$("#saveAddPermission").click(function () {
				if (jqxwindow.jqxValidator("validate")) {
					mainGrid.jqxGrid("addrow", null, AddPermission.getValue(), "first");
					jqxwindow.jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
			    rules: [{ input: "#txtPermissionId", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtFromDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						}]
			});
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtApplicationId").text(data.applicationId);
				$("#txtEmployeeId").text(data.partyId);
			}
		};
		var getValue = function() {
			var value = {
				applicationId: $("#txtApplicationId").text(),
				partyId: $("#txtEmployeeId").text(),
				permissionId: $("#txtPermissionId").jqxDropDownList("val"),
				fromDate: $("#txtFromDate").jqxDateTimeInput("getDate").toSQLTimeStamp(),
				allow: true
			};
			if ($("#txtThruDate").jqxDateTimeInput("getDate")) {
				value.thruDate = $("#txtThruDate").jqxDateTimeInput("getDate").toSQLTimeStamp();
			}
			return value;
		};
		var reloadAddablePermission = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtPermissionId").jqxDropDownList({ source: _.difference(AdministrationConfig.SecurityPermission.array, data.permissions) });
			}
		};
		var open = function(grid, applicationId, partyId, isGroup) {
			if (applicationId && partyId) {
				mainGrid = grid;
				if (isGroup) {
					$("#lblEmployeeId").text(multiLang.ADUserGroupId);
				} else {
					$("#lblEmployeeId").text(multiLang.EmployeeId);
				}
				DataAccess.executeAsync({
				url: "loadPermissionOfPartyInApplication",
				data: {partyId: partyId, applicationId: applicationId}
				}, AddPermission.reloadAddablePermission);
				
				AddPermission.setValue({partyId: partyId, applicationId: applicationId});
				
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowAddPermission");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			setValue: setValue,
			getValue: getValue,
			reloadAddablePermission: reloadAddablePermission,
			open: open
		};
	})();
}