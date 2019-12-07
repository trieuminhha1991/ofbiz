$(document).ready(function() {
	ChangeEmployee.init();
});
var txtEmployeeDDB;
if (typeof (ChangeEmployee) == "undefined") {
	var ChangeEmployee = (function() {
		var jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ theme : theme, width : 550, height : 150, resizable : false, isModal : true, autoOpen : false, cancelButton : $("#btnCancel"), modalOpacity : 0.7 });
			
			var configEmployee =
			{
				useUrl: true,
				root: "results",
				widthButton: 300,
				heightButton: "28px",
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: "left",
				datafields:
				[
					{ name: "partyId", type: "string" },
					{ name: "partyCode", type: "string" },
					{ name: "partyName", type: "string" }
				],
				columns:
				[
					{ text: uiLabelMap.EmployeeId, datafield: "partyCode", width: 150 },
					{ text: uiLabelMap.EmployeeName, datafield: "partyName" }
				],
				url: "JQGetListPartiesInOrganization",
				useUtilFunc: true,
				autoCloseDropDown: true,
				key: "partyId",
				keyCode: 'partyCode',
				description: ["partyName"],
				pagesize: 5
			};
			txtEmployeeDDB = new OlbDropDownButton($("#txtEmployee"), $("#txtEmployeeGrid"), null, configEmployee, []);
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if (jqxwindow.jqxValidator("validate") && $("#btnSave").data("eligible")) {
					$("#btnSave").data("eligible", false);
					DataAccess.execute({
						url: "assignPartyToPicklistBin",
						data: {
							partyId: txtEmployeeDDB.getValue(),
							picklistBinId: jqxwindow.data("picklistBinId"),
							roleTypeId: jqxwindow.data("roleTypeId")
						}
					}, ChangeEmployee.notify);
				}
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#btnSave").data("eligible", true);
				txtEmployeeDDB.clearAll();
				jqxwindow.data("picklistBinId", null);
				jqxwindow.data("roleTypeId", null);
			});
			jqxwindow.on("open", function() {
				$("#btnSave").data("eligible", true);
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules :
				[
					{ input : "#txtEmployee", message : uiLabelMap.fieldRequired, action : "close",
						rule : function(input, commit) {
							return !!txtEmployeeDDB.getValue();
						}
					}
				],
				scroll : false
			});
		};
		var notify = function(res) {
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				Grid.renderMessage("jqxgridPicklistBin", uiLabelMap.updateError, {
					autoClose : true,
					template : "error",
					appendContainer : "#container" + "jqxgridPicklistBin",
					opacity : 0.9
				});
				$("#btnSave").data("eligible", true);
			} else {
				Grid.renderMessage("jqxgridPicklistBin", uiLabelMap.updateSuccess, {
					autoClose : true,
					template : "info",
					appendContainer : "#container" + "jqxgridPicklistBin",
					opacity : 0.9
				});
				$("#jqxgridPicklistBin").jqxGrid("updatebounddata");
				jqxwindow.jqxWindow("close");
			}
		};
		var open = function(grid, picklistBinId, roleTypeId, partyId) {
			if (grid && picklistBinId && roleTypeId) {
				if (partyId) {
					txtEmployeeDDB.selectItem(new Array(partyId));
				}
				jqxwindow.data("picklistBinId", picklistBinId);
				jqxwindow.data("roleTypeId", roleTypeId);

				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
			}
		};
		return {
			init : function() {
				jqxwindow = $("#jqwWindowChangeEmployee");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			open: open,
			notify: notify
		};
	})();
}