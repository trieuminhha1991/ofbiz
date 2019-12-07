$(document).ready(function() {
	AddCostByDepartment.init();
});
if (typeof (AddCostByDepartment) == "undefined") {
	var AddCostByDepartment = (function() {
		var mainGrid, jqxwindow, jqxmenu;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ resizable: false, autoOpen: false, height: 300, width: 950, isModal: true,
				theme: theme, cancelButton: "#alterCancel" });
			
			$("#txtInvoiceItemTypeId").jqxDropDownList({ theme: theme, source: dataAdapterInvoiceItemType, width: 218, height: 30, displayMember: "invoiceItemTypeName", valueMember: "costAccMapDepId", dropDownHeight: 150, placeHolder: multiLang.filterchoosestring});
			
			$("#txtCurrencyUomId").jqxDropDownList({ theme: theme, source: currencyUoms, width: 218, height: 30, displayMember: "description", valueMember: "currencyUomId", dropDownHeight: 150});
			$("#txtCurrencyUomId").jqxDropDownList("val", "VND");
			$("#txtCostAccDate").jqxDateTimeInput({ theme: theme, width: 218, height: 30 });
			$("#txtCostPriceActual").jqxNumberInput({ theme: theme, width: 218, height: 30, decimalDigits:2, spinButtons: false, min: 0 });
			
			var initPartyDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "groupName", type: "string" }];
				var columns = [{ text: multiLang.CommonId, datafield: "partyCode", width: 150,
									cellsrenderer: function (row, column, value) {
										if (value){
											return '<span>' + value + '</span>';
										} else {
											var data = $('#jqxgridParty').jqxGrid('getrowdata', row);
											return '<span>' + data.partyId + '</span>';
										}
									},	
								},
				               { text: multiLang.BACCFullName, datafield: "groupName" }];
				GridUtils.initDropDownButton({
					url: urlGetParty, filterable: true, showfilterrow: true,
					width: 400, source: {id: "partyId", pagesize: 5},
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								$("#jqxgridParty").jqxGrid("clearfilters");
								return true;
							}
						}, dropdown: {width: 218, height: 30}, clearOnClose: "Y"
				}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var str = row.groupName;
					if (row.partyCode){
						str = str + " [" + row.partyCode + "]";
					} else if (row.partyId){
						str = str + " [" + row.partyId + "]";
					}
					return str;
				});
			};
			initPartyDrDGrid($("#divPartyId"),$("#jqxgridParty"), 600);
			
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			
			jqxmenu.jqxMenu({ theme: "olbius", width: 220, autoOpenPopup: false, mode: "popup"});
		};
		var handleEvents = function() {
			jqxwindow.on("close", function() {
				$("#txtInvoiceItemTypeId").jqxDropDownList("clearSelection");
				$("#txtCostPriceActual").jqxNumberInput("val", 0);
				$("#txtDescription").val("");
				Grid.cleanDropDownValue($("#divPartyId"));
				jqxwindow.data("action", null);
				jqxwindow.data("costAccDepId", null);
			});
			$("#btnSave").click(function() {
				if (jqxwindow.jqxValidator("validate")) {
					var data = AddCostByDepartment.getValue();
					if ($("#txtPathScanFile").prop("files")[0]) {
						var pathScanFile = DataAccess.uploadFile($("#txtPathScanFile").prop("files")[0]);
						data.pathScanFile = pathScanFile;
					}
					if (jqxwindow.data("action") == "update") {
						data.costAccDepId = jqxwindow.data("costAccDepId");
						DataAccess.execute({
							url: "updateCostAccDepartmentCustom",
							data: data },
						AddCostByDepartment.notify);
					} else {
						DataAccess.execute({
							url: "createCostAccDepartmentCustom",
							data: data },
						AddCostByDepartment.notify);
					}
					jqxwindow.jqxWindow("close");
				}
			});
			
			$("#contextMenu").on("itemclick", function (event) {
		        var args = event.args;
		        var itemId = $(args).attr("id");
		        switch (itemId) {
				case "mitemUpdate":
					var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
					var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
					if (rowData) {
						AddCostByDepartment.setValue(rowData);
						jqxwindow.data("action", "update");
						jqxwindow.data("costAccDepId", rowData.costAccDepId);
						AddCostByDepartment.open();
					}
					break;
				default:
					break;
				}
			});
			$("body").on("click", function() {
				$("#contextMenu").jqxMenu("close");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
			    rules: [{ input: "#txtInvoiceItemTypeId", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
		          		},
			            { input: "#txtCurrencyUomId", message: multiLang.fieldRequired, action: "change",
					    	rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
			            },
			            { input: "#txtCostAccDate", message: multiLang.fieldRequired, action: "change",
			            	rule: function (input, commit) {
			            		if (input.jqxDateTimeInput("getDate")) {
			            			return true;
			            		}
			            		return false;
			            	}
			            },
			            { input: "#txtCostPriceActual", message: multiLang.fieldRequired, action: "change",
			            	rule: function (input, commit) {
			            		if (input.jqxNumberInput("val")) {
			            			return true;
			            		}
			            		return false;
			            	}
			            },
						{ input: "#divPartyId", message: multiLang.fieldRequired, action: "close",
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
				costAccMapDepId: $("#txtInvoiceItemTypeId").jqxDropDownList("val"),
				costPriceActual: $("#txtCostPriceActual").jqxNumberInput("val"),
				currencyUomId: $("#txtCurrencyUomId").jqxDropDownList("val"),
				partyId: Grid.getDropDownValue($("#divPartyId")).trim(),
				description: $("#txtDescription").val(),
				costAccDate: $("#txtCostAccDate").jqxDateTimeInput("getDate")
			};
			value.costAccDate = value.costAccDate?value.costAccDate.getTime():value.costAccDate;
			return value;
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtInvoiceItemTypeId").jqxDropDownList("val", data.costAccMapDepId);
				$("#txtCostPriceActual").jqxNumberInput("val", data.costPriceActual);
				$("#txtCurrencyUomId").jqxDropDownList("val", data.currencyUomId);
				Grid.setDropDownValue($("#divPartyId"), data.partyId, data.partyName);
				$("#txtDescription").val(data.description);
				if (data.costAccDate) {
					$("#txtCostAccDate").jqxDateTimeInput("setDate", data.costAccDate);
				}
			}
		};
		var open = function() {
			var wtmp = window;
			var tmpwidth = jqxwindow.jqxWindow("width");
			jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
			jqxwindow.jqxWindow("open");
		};
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
				if (jqxwindow.data("action") == "update") {
					$("#notificationContent").text(multiLang.updateSuccess);
				} else {
					$("#notificationContent").text(multiLang.addSuccess);
				}
		      	$("#jqxNotification").jqxNotification("open");
		      	mainGrid.jqxGrid("updatebounddata");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#alterpopupWindow");
				mainGrid = $("#jqxgrid");
				jqxmenu =$("#contextMenu");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			open: open,
			getValue: getValue,
			setValue: setValue,
			notify: notify
		}
	})();
}