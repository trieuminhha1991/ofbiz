$(document).ready(function() {
	RequirementApprove.init();
});
if (typeof (RequirementApprove) == "undefined") {
	var RequirementApprove = (function() {
		var initJqxElements = function() {
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var setRequirementStatus = function(requirementId, statusId) {
			DataAccess.execute({
			url: "setRequirementStatus",
			data: {
				requirementId: requirementId,
				statusId: statusId}
			}, this.notify);
		};
		var processRequirement = function(action) {
			switch (action) {
			case "Destroyed":
				this.Destroyed.showOption();
				break;
			case "Transfer":
				this.Transfer.execute();
				break;
			default:
				break;
			}
		};
		var Transfer = (function() {
			var execute = function() {
				DataAccess.execute({
					url: "executeWithdrawalRequirement",
					data: Transfer.getValue()
					}, RequirementApprove.notify);
			};
			var getValue = function() {
				var value = {
					distributorId: $("#distributorId").val(),
					requirementId: $("#requirementId").text().trim(),
					type: "Transfer"
				};
				return value;
			};
			return {
				getValue: getValue,
				execute: execute
			}
		})();
		var Destroyed = (function() {
			var jqxwindow;
			var initJqxElements = function() {
				jqxwindow.jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, height: 150, resizable: false, isModal: true, autoOpen: false,
					cancelButton: $("#cancelDestroyedOption"), modalOpacity: 0.7
				});
				
				var initDepositWarehouseDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "facilityId", type: "string" },
					                  { name: "facilityName", type: "string" }];
					var columns = [{text: multiLang.FacilityId, datafield: "facilityId", width: 150},
					               {text: multiLang.FacilityName, datafield: "facilityName"}];
					GridUtils.initDropDownButton({
						url: urlGetListFacility, autorowheight: true, filterable: true, showfilterrow: true,
						width: width ? width : 600, source: {id: "facilityId", pagesize: 5},
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									$("#jqxgridDepositWarehouse").jqxGrid("clearfilters");
									return true;
								}
							}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
					}, datafields, columns, null, grid, dropdown, "facilityId", "facilityName");
				};
				initDepositWarehouseDrDGrid($("#divDepositWarehouse"),$("#jqxgridDepositWarehouse"), 600);
				
			};
			var handleEvents = function() {
				$("#saveDestroyedOption").click(function() {
					if (jqxwindow.jqxValidator("validate")) {
						DataAccess.execute({
							url: "executeWithdrawalRequirement",
							data: Destroyed.getValue()
							}, RequirementApprove.notify);
						jqxwindow.jqxWindow("close");
					}
				});
				jqxwindow.on("close", function() {
					jqxwindow.jqxValidator("hide");
					Grid.cleanDropDownValue($("#divDepositWarehouse"));
				});
			};
			var initValidator = function() {
				jqxwindow.jqxValidator({
				    rules: [{ input: "#divDepositWarehouse", message: multiLang.fieldRequired, action: "close", 
					           	rule: function (input, commit) {
					           		if (Grid.getDropDownValue(input).trim()) {
					           			return true;
					           		}
					           		return false;
					           	}
					        }]
				});
			};
			var getValue = function() {
				var value = {
						distributorId: $("#distributorId").val(),
						requirementId: $("#requirementId").text().trim(),
						destinationFacilityId: Grid.getDropDownValue($("#divDepositWarehouse")).trim(),
						type: "Destroyed"
				};
				return value;
			};
			var showOption = function() {
				var wtmp = window;
		    	var tmpwidth = jqxwindow.jqxWindow("width");
		        jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
		    	jqxwindow.jqxWindow("open");
			};
			return {
				init: function() {
					jqxwindow = $("#jqxwindowDestroyedOption");
					initJqxElements();
					handleEvents();
					initValidator();
				},
				showOption: showOption,
				getValue: getValue
			}
		})();
		var notify = function(res) {
			$("#jqxNotification").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotification").jqxNotification({ template: "error"});
				$("#notificationContent").text(errormes);
				$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#notificationContent").text(multiLang.updateSuccess);
				$("#jqxNotification").jqxNotification("open");
				location.reload();
			}
		};
		return {
			init: function() {
				initJqxElements();
				Destroyed.init();
			},
			setRequirementStatus: setRequirementStatus,
			processRequirement: processRequirement,
			Destroyed: Destroyed,
			Transfer: Transfer,
			notify: notify
		}
	})();
}