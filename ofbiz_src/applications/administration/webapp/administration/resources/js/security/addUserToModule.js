if (typeof (AddUserToModule) == "undefined") {
	var AddUserToModule = (function() {
		var jqxwindow, mainGrid, subGrid, selectedUser = new Array();
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: theme, width: 600, height: 300, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddUser"), modalOpacity: 0.7
			});
			
			var initEmployeeDrDGrid = function(dropdown, grid, width){
				var datafields =
				[
					{ name: "userLoginId", type: "string" },
					{ name: "partyId", type: "string" },
					{ name: "partyCode", type: "string" },
					{ name: "partyName", type: "string" },
					{ name: "partyTypeId", type: "string" }
				];
				var columns =
				[
					{ text: multiLang.userLoginId, datafield: "userLoginId", width: 200 },
					{ text: multiLang.EmployeeId, datafield: "partyCode", width: 200 },
					{ text: multiLang.EmployeeName, datafield: "partyName" }
				];
				GridUtils.initDropDownButton({url: "JQGetListUserLoginNotInApplication&moduleId=" + moduleId, autorowheight: true, filterable: true, showfilterrow: true,
					width: width ? width : 600, source: {pagesize: 5}, selectionmode: "checkbox", closeOnSelect: "N",
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								subGrid.jqxGrid("clearfilters");
								return true;
							}
						}, clearOnClose: false, dropdown: {dropDownHorizontalAlignment: "right", width: 218}}, datafields, columns, null, grid, dropdown, "partyId",
							function(row){
								return subGrid.jqxGrid("getselectedrowindexes").length + " " + multiLang.CommonEmployee;
							});
			};
			initEmployeeDrDGrid($("#txtAddUserEmployeeId"),subGrid, 600);
			
			$("#txtAddUserPermissionId").jqxComboBox({ source: AdministrationConfig.SecurityPermission.array, multiSelect: true,
				width: 218, height: 25, theme: theme });
			
			$("#txtAddUserFromDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtAddUserThruDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtAddUserThruDate").jqxDateTimeInput("setDate", null);
		};
		var handleEvents = function() {
			jqxwindow.on("open", function () {
				$("#txtAddUserPermissionId").jqxComboBox("clearSelection");
				$("#txtAddUserThruDate").jqxDateTimeInput("setDate", null);
				selectedUser = new Array();
			});
			jqxwindow.on("close", function () {
				jqxwindow.jqxValidator("hide");
			});
			
			subGrid.on("bindingcomplete", function (event) {
				if (!subGrid.find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).hasClass("hide")) {
					subGrid.find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).addClass("hide");
				}
			});
			
			subGrid.on("rowselect", function (event) {
				var args = event.args;
				var rowData = args.row;
				selectedUser.push(rowData.partyId);
			});
			subGrid.on("rowunselect", function (event) {
				var args = event.args;
				var rowData = args.row;
				selectedUser = _.reject(selectedUser, function(v){ return v == rowData.partyId });
				Grid.initTooltipDropdown($("#txtAddUserEmployeeId"), subGrid.jqxGrid("getselectedrowindexes").length + " " + multiLang.CommonEmployee);
			});
			
			$("#saveAddUser").click(function () {
				if (jqxwindow.jqxValidator("validate")) {
					DataAccess.executeAsync({
						url: "addUsersToModule",
						data: AddUserToModule.getValue()
						}, AddUserToModule.notify);
					
					jqxwindow.jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
			    rules: [{ input: "#txtAddUserPermissionId", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtAddUserFromDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtAddUserEmployeeId", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input).toString();
								if (value.trim()) {
									return true;
								}
								return false;
							}
						}]
			});
		};
		var getValue = function() {
			var value = {
				applicationId: $("#txtAddUserApplicationId").text(),
				parties: _.uniq(selectedUser),
				permissionIds: _.pluck($("#txtAddUserPermissionId").jqxComboBox("getSelectedItems"), "value"),
				fromDate: $("#txtAddUserFromDate").jqxDateTimeInput("getDate").toSQLTimeStamp(),
				allow: true
			};
			if ($("#txtAddUserThruDate").jqxDateTimeInput("getDate")) {
				value.thruDate = $("#txtAddUserThruDate").jqxDateTimeInput("getDate").toSQLTimeStamp();
			}
			return value;
		};
		var notify = function(res) {
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				Grid.renderMessage("jqxgridUserInModule", multiLang.updateError, {
					autoClose : true,
					template : "error"
				});
			}else {
				Grid.renderMessage("jqxgridUserInModule", multiLang.updateSuccess, {
					autoClose : true,
					template : "info"
				});
				mainGrid.jqxGrid("updatebounddata");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowAddUser");
				mainGrid = $("#jqxgridUserInModule");
				subGrid = $("#jqxgridAddUserEmployeeId");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			notify: notify
		};
	})();
}