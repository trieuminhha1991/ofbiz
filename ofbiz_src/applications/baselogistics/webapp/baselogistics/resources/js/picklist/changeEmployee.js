$(document).ready(function() {
	ChangeEmployee.init();
});
var checkEmployeeDDB;
var recheckEmployeeDDB;
if (typeof (ChangeEmployee) == "undefined") {
	var btnClick = false;
	var ChangeEmployee = (function() {
		var jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ theme : theme, width : 550, height : 200, resizable : false, isModal : true, autoOpen : false, cancelButton : $("#btnCancel"), modalOpacity : 0.7 });
			
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
					{ name: "employeePartyId", type: "string" },
					{ name: "employeePartyCode", type: "string" },
					{ name: "fullName", type: "string" }
				],
				columns:
				[
					{ text: uiLabelMap.EmployeeId, datafield: "employeePartyCode", width: 150 },
					{ text: uiLabelMap.EmployeeName, datafield: "fullName" }
				],
				url: "jqGetPartyAndPositionInCompany",
				useUtilFunc: true,
				autoCloseDropDown: true,
				key: "employeePartyId",
				keyCode: 'employeePartyCode',
				description: ["fullName"],
				pagesize: 5
			};
			
			recheckEmployeeDDB = new OlbDropDownButton($("#recheckEmployee"), $("#recheckEmployeeGrid"), null, configEmployee, []);
			checkEmployeeDDB = new OlbDropDownButton($("#checkEmployee"), $("#checkEmployeeGrid"), null, configEmployee, []);
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureSave, function() {
					if (!btnClick){
						Loading.show('loadingMacro');
			        	setTimeout(function(){
							if (jqxwindow.jqxValidator("validate") && $("#btnSave").data("eligible")) {
								$("#btnSave").data("eligible", false);
								$.ajax({
						    		url: "assignPartyToPicklistBin",
						    		type: "POST",
						    		async: false,
						    		data: {
						    			partyId: checkEmployeeDDB.getValue(),
										picklistBinId: jqxwindow.data("picklistBinId"),
										roleTypeId: "PICKING_PICKER"
						    		},
						    		success: function (res){
						    			notify(res);
						    		}
						    	});
								
								$.ajax({
									url: "assignPartyToPicklistBin",
									type: "POST",
									async: false,
									data: {
										partyId: recheckEmployeeDDB.getValue(),
										picklistBinId: jqxwindow.data("picklistBinId"),
										roleTypeId: "PICKING_CHECKER"
									},
									success: function (res){
										notify(res);
									}
								});
								$("#jqxgrid").jqxGrid("updatebounddata");
								jqxwindow.jqxWindow("close");
							}
						Loading.hide('loadingMacro');
			        	}, 500);
						btnClick = true;
					}
		        }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
		        	btnClick = false;
		        });
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#btnSave").data("eligible", true);
				checkEmployeeDDB.clearAll();
				recheckEmployeeDDB.clearAll();
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
					{ input : "#checkEmployee", message : uiLabelMap.fieldRequired, action : "close",
						rule : function(input, commit) {
							return !!checkEmployeeDDB.getValue();
						}
					},
					{ input : "#recheckEmployee", message : uiLabelMap.fieldRequired, action : "close",
						rule : function(input, commit) {
							return !!recheckEmployeeDDB.getValue();
						}
					},
				],
				scroll : false
			});
		};
		var notify = function(res) {
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				Grid.renderMessage("jqxgrid", uiLabelMap.updateError, {
					autoClose : true,
					template : "error",
					appendContainer : "#container" + "jqxgrid",
					opacity : 0.9
				});
				$("#btnSave").data("eligible", true);
			} else {
				Grid.renderMessage("jqxgrid", uiLabelMap.updateSuccess, {
					autoClose : true,
					template : "info",
					appendContainer : "#container" + "jqxgrid",
					opacity : 0.9
				});
			}
		};
		var open = function(grid, picklistBinId, partyPickId, partyCheckId) {
			if (grid && picklistBinId) {
				if (partyPickId) {
					checkEmployeeDDB.selectItem(new Array(partyPickId));
				}
				if (partyCheckId) {
					recheckEmployeeDDB.selectItem(new Array(partyCheckId));
				}
				jqxwindow.data("picklistBinId", picklistBinId);

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