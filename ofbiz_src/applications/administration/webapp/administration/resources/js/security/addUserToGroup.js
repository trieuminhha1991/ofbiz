if (typeof (AddUserToGroup) == "undefined") {
	var AddUserToGroup = (function() {
		var jqxwindow, subGrid, selectedUser = new Array();
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: theme, width: 600, height: 200, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddUserToGroup"), modalOpacity: 0.7
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
								return subGrid.jqxGrid("getselectedrowindexes").length + " " + multiLang.CommonEmployee;
							});
			};
			initEmployeeDrDGrid($("#txtAddUserToGroupEmployeeId"),subGrid, 600);
		};
		var handleEvents = function() {
			jqxwindow.on("open", function () {
				selectedUser = new Array();
				subGrid.jqxGrid("clearSelection");
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
				Grid.initTooltipDropdown($("#txtAddUserToGroupEmployeeId"), subGrid.jqxGrid("getselectedrowindexes").length + " " + multiLang.CommonEmployee);
			});
			
			$("#saveAddUserToGroup").click(function () {
				if (jqxwindow.jqxValidator("validate")) {
					DataAccess.executeAsync({
						url: "addUserToGroup",
						data: AddUserToGroup.getValue()
						}, UserInGroup.notify);
					
					jqxwindow.jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
			    rules: [{ input: "#txtAddUserToGroupEmployeeId", message: multiLang.fieldRequired, action: "close", 
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
				groupId: $("#txtGroupId").text(),
				parties: _.uniq(selectedUser)
			};
			return value;
		};
		var loadGrid = function(partyId) {
			var adapter = subGrid.jqxGrid("source");
			if (adapter) {
				adapter.url = "jqxGeneralServicer?sname=JQGetListUserNotInGroup&partyId=" + partyId;
				adapter._source.url = adapter.url;
				subGrid.jqxGrid("source", adapter);
			}
		};
		var open = function() {
			var partyId = UserInGroup.groupId();
			if (partyId) {
				loadGrid(partyId);
				$("#txtGroupId").text(partyId)
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowAddUserToGroup");
				subGrid = $("#jqxgridAddUserToGroupEmployeeId");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			open: open
		};
	})();
}