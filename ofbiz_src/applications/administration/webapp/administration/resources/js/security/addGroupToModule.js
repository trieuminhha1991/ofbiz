if (typeof (AddGroupToModule) == "undefined") {
	var AddGroupToModule = (function() {
		var jqxwindow, mainGrid, subGrid, selectedGroup = new Array();
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: theme, width: 600, height: 300, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddGroup"), modalOpacity: 0.7
			});
			
			var initEmployeeDrDGrid = function(dropdown, grid, width){
				var datafields = [
								{ name: "partyId", type: "string" },
								{ name: "partyCode", type: "string" },
								{ name: "description", type: "string" },
								{ name: "partyTypeId", type: "string" }];
			var columns = [
					{text: multiLang.ADUserGroupId, datafield: "partyCode", width: 200},
					{text: multiLang.BSDescription, datafield: "description"}
			];
			GridUtils.initDropDownButton({url: "JQGetListGroupNotInApplication&moduleId=" + moduleId, autorowheight: true, filterable: true, showfilterrow: true,
				width: width ? width : 600, source: {pagesize: 5}, selectionmode: "checkbox", closeOnSelect: "N",
					handlekeyboardnavigation: function (event) {
						var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
						if (key == 70 && event.ctrlKey) {
							subGrid.jqxGrid("clearfilters");
							return true;
						}
				}, clearOnClose: false, dropdown: {dropDownHorizontalAlignment: "right", width: 218}}, datafields, columns, null, grid, dropdown, "partyId",
		 			function(row){
						return subGrid.jqxGrid("getselectedrowindexes").length + " " + multiLang.ADUserGroup;
				});
			};
			initEmployeeDrDGrid($("#txtAddGroupEmployeeId"),subGrid, 600);
			
			$("#txtAddGroupPermissionId").jqxComboBox({ source: AdministrationConfig.SecurityPermission.array, multiSelect: true,
				width: 218, height: 25, theme: theme });
			
			$("#txtAddGroupFromDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtAddGroupThruDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtAddGroupThruDate").jqxDateTimeInput("setDate", null);
		};
		var handleEvents = function() {
			jqxwindow.on("open", function () {
				$("#txtAddGroupPermissionId").jqxComboBox("clearSelection");
				$("#txtAddGroupThruDate").jqxDateTimeInput("setDate", null);
				selectedGroup = new Array();
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
				selectedGroup.push(rowData.partyId);
			});
			subGrid.on("rowunselect", function (event) {
				var args = event.args;
				var rowData = args.row;
				selectedGroup = _.reject(selectedGroup, function(v){ return v == rowData.partyId });
				Grid.initTooltipDropdown($("#txtAddGroupEmployeeId"), subGrid.jqxGrid("getselectedrowindexes").length + " " + multiLang.CommonEmployee);
			});
			
			$("#saveAddGroup").click(function () {
				if (jqxwindow.jqxValidator("validate")) {
					DataAccess.executeAsync({
						url: "addUsersToModule",
						data: AddGroupToModule.getValue()
						}, AddGroupToModule.notify);
					
					jqxwindow.jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules: [{ input: "#txtAddGroupPermissionId", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtAddGroupFromDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtAddGroupEmployeeId", message: multiLang.fieldRequired, action: "close", 
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
				applicationId: $("#txtAddGroupApplicationId").text(),
				parties: _.uniq(selectedGroup),
				permissionIds: _.pluck($("#txtAddGroupPermissionId").jqxComboBox("getSelectedItems"), "value"),
				fromDate: $("#txtAddGroupFromDate").jqxDateTimeInput("getDate").toSQLTimeStamp(),
				allow: true
			};
			if ($("#txtAddGroupThruDate").jqxDateTimeInput("getDate")) {
				value.thruDate = $("#txtAddGroupThruDate").jqxDateTimeInput("getDate").toSQLTimeStamp();
			}
			return value;
		};
		var notify = function(res) {
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				Grid.renderMessage("jqxgridGroupInModule", multiLang.updateError, {
					autoClose : true,
					template : "error"
				});
			}else {
				Grid.renderMessage("jqxgridGroupInModule", multiLang.updateSuccess, {
					autoClose : true,
					template : "info"
				});
				mainGrid.jqxGrid("updatebounddata");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowAddGroup");
				mainGrid = $("#jqxgridGroupInModule");
				subGrid = $("#jqxgridAddGroupEmployeeId");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			notify: notify
		};
	})();
}