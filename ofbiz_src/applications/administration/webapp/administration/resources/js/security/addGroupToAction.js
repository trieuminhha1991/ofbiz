if (typeof (AddGroupToAction) == "undefined") {
	var AddGroupToAction = (function() {
		var jqxwindow, mainGrid, subGrid, selectedGroup = new Array();
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: theme, width: 600, height: 300, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddGroupToAction"), modalOpacity: 0.7
			});
			
			var initEmployeeDrDGrid = function(dropdown, grid, width){
				var datafields =
				[
					{ name: "partyId", type: "string" },
					{ name: "partyCode", type: "string" },
					{ name: "description", type: "string" },
					{ name: "partyTypeId", type: "string" }
				];
				var columns =
				[
					{ text: multiLang.ADUserGroupId, datafield: "partyCode", width: 200 },
					{ text: multiLang.BSDescription, datafield: "description" }
				];
				GridUtils.initDropDownButton({url: "", autorowheight: true, filterable: true, showfilterrow: true,
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
			initEmployeeDrDGrid($("#txtAddGroupToActionEmployeeId"),subGrid, 600);
			
			$("#txtAddGroupToActionPermissionId").jqxComboBox({ source: AdministrationConfig.SecurityPermission.array, multiSelect: true,
				width: 218, height: 25, theme: theme });
			
			$("#txtAddGroupToActionFromDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtAddGroupToActionThruDate").jqxDateTimeInput({ theme: theme, width: 218, height: 25 });
			$("#txtAddGroupToActionThruDate").jqxDateTimeInput("setDate", null);
		};
		var handleEvents = function() {
			jqxwindow.on("open", function () {
				$("#txtAddGroupToActionPermissionId").jqxComboBox("clearSelection");
				$("#txtAddGroupToActionThruDate").jqxDateTimeInput("setDate", null);
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
				Grid.initTooltipDropdown($("#txtAddGroupToActionEmployeeId"), subGrid.jqxGrid("getselectedrowindexes").length + " " + multiLang.CommonEmployee);
			});
			
			$("#saveAddGroupToAction").click(function () {
				if (jqxwindow.jqxValidator("validate")) {
					DataAccess.executeAsync({
						url: "addUsersToModule",
						data: AddGroupToAction.getValue()
						}, AddGroupToAction.notify);
					jqxwindow.jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules:
				[
				 	{ input: "#txtAddGroupToActionPermissionId", message: multiLang.fieldRequired, action: "change",
				 		rule: function (input, commit) {
				 			if (input.val()) {
				 				return true;
				 			}
				 			return false;
				 		}
					},
					{ input: "#txtAddGroupToActionFromDate", message: multiLang.fieldRequired, action: "change",
						rule: function (input, commit) {
							if (input.val()) {
								return true;
							}
							return false;
						}
					},
					{ input: "#txtAddGroupToActionEmployeeId", message: multiLang.fieldRequired, action: "close", 
						rule: function (input, commit) {
							var value = Grid.getDropDownValue(input).toString();
							if (value.trim()) {
								return true;
							}
							return false;
						}
					}
				]
			});
		};
		var getValue = function() {
			var value = {
				applicationId: $("#txtAddGroupToActionApplicationId").text(),
				parties: _.uniq(selectedGroup),
				permissionIds: _.pluck($("#txtAddGroupToActionPermissionId").jqxComboBox("getSelectedItems"), "value"),
				fromDate: $("#txtAddGroupToActionFromDate").jqxDateTimeInput("getDate").toSQLTimeStamp(),
				allow: true
			};
			if ($("#txtAddGroupToActionThruDate").jqxDateTimeInput("getDate")) {
				value.thruDate = $("#txtAddGroupToActionThruDate").jqxDateTimeInput("getDate").toSQLTimeStamp();
			}
			return value;
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtAddGroupToActionApplicationId").text(data.applicationId);
			}
		};
		var open = function(grid, applicationId) {
			if (applicationId) {
				mainGrid = grid;
				
				AddGroupToAction.setValue({applicationId: applicationId});
				
				var adapter = subGrid.jqxGrid("source");
				if(adapter){
					adapter.url = "jqxGeneralServicer?sname=JQGetListGroupNotInApplication&moduleId=" + applicationId;
					adapter._source.url = adapter.url;
					subGrid.jqxGrid("source", adapter);
				}
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
			}
		};
		var notify = function(res) {
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				Grid.renderMessage(mainGrid.attr("id"), multiLang.updateError, {
					autoClose : true,
					template : "error",
					appendContainer : "#container" + mainGrid.attr("id"),
					opacity : 0.9
				});
			} else {
				Grid.renderMessage(mainGrid.attr("id"), multiLang.updateSuccess, {
					autoClose : true,
					template : "info",
					appendContainer : "#container" + mainGrid.attr("id"),
					opacity : 0.9
				});
				mainGrid.jqxGrid("updatebounddata");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowAddGroupToAction");
				subGrid = $("#jqxgridAddGroupToActionEmployeeId");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			setValue: setValue,
			open: open,
			notify: notify
		};
	})();
}