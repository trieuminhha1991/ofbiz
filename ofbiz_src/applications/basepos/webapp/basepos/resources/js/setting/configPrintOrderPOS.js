$(document).ready(function() {
	ConfigPrintOrder.init();
});
if (typeof (ConfigPrintOrder) == "undefined") {
	var ConfigPrintOrder = (function() {
		var mainGrid, contextMenu;
		var initJqxElements = function() {
			contextMenu.jqxMenu({ width: 150, autoOpenPopup: false, mode: "popup", theme: theme });
		};
		var handleEvents = function() {
			contextMenu.on("itemclick", function (event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				switch (itemId) {
				case "mnuRefresh":
					mainGrid.jqxGrid("updatebounddata");
					break;
				case "mnuPrint":
					AddConfigPrintOrder.setValue(rowData);
					ConfigPrintOrder.print();
					break;
				case "mnuEdit":
					AddConfigPrintOrder.setValue(rowData);
					$("#alterpopupWindow").jqxWindow("open");
					break;
				case "mnuDelete":
					ConfigPrintOrder.deleteRow(rowData);
					break;
				default:
					break;
				}
			});
		};
		var addRow = function(data) {
			if (!_.isEmpty(data)) {
				mainGrid.jqxGrid("addRow", null, data, "first");
			}
		};
		var updateRow = function(data) {
			if (!_.isEmpty(data)) {
				mainGrid.jqxGrid("updaterow", data.uid, data);
			}
		};
		var deleteRow = function(data) {
			if (!_.isEmpty(data)) {
				bootbox.dialog(multiLang.BPOSAreYouSureDeleteThisConfigPrintOrder, [{
					"label": multiLang.CommonCancel,
					"icon" : "fa fa-remove",
					"class": "btn  btn-danger form-action-button pull-right",
					"callback": function() {
						bootbox.hideAll();
					}
				}, {
					"label": multiLang.CommonSubmit,
					"icon" : "fa-check",
					"class": "btn btn-primary form-action-button pull-right",
					"callback": function() {
						mainGrid.jqxGrid("deleterow", data.uid);
					}
				}]);
			}
		};
		var print = function() {
			var tmpWin = $("#PrintOrder").printArea().win;
			if(tmpWin.matchMedia){
				tmpWin.matchMedia("print");
			};
		};
		return {
			init: function() {
				mainGrid = $("#jqxgridConfigPrintOrder");
				contextMenu = $("#contextMenu");
				initJqxElements();
				handleEvents();
				AddConfigPrintOrder.init();
			},
			addRow: addRow,
			updateRow: updateRow,
			deleteRow: deleteRow,
			print: print
		};
	})();
}