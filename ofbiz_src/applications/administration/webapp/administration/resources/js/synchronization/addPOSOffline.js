if (typeof (AddPOSOffline) == "undefined") {
	var AddPOSOffline = (function() {
		var jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ theme : theme, width : 500, height : 180, resizable : false, isModal : true, autoOpen : false, cancelButton : $("#btnCancel"), modalOpacity : 0.7 });
			
			var initProductStoreDrDGrid = function(dropdown, grid, width) {
				var datafields =
				[
					{ name : "productStoreId", type : "string" },
					{ name : "storeName", type : "string" }
				];
				var columns =
				[
					{ text : multiLang.BSProductStoreId, datafield : "productStoreId", width : 200 },
					{ text : multiLang.BSStoreName, datafield : "storeName", minwidth : 200 }
				];
				GridUtils.initDropDownButton({ url : "JQGetListProductStore", filterable : true,
					showfilterrow : true, width : width ? width : 600,
					source : {
						id : "productStoreId",
						pagesize : 5
					},
					handlekeyboardnavigation : function(event) {
						var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
						if (key == 70 && event.ctrlKey) {
							productGrid.jqxGrid("clearfilters");
							return true;
						}
					},
					dropdown : { width : 220 }
				}, datafields, columns, null, grid, dropdown, "productStoreId", "storeName");
			};
			initProductStoreDrDGrid($("#txtProductStore"), $("#jqxgridProductStore"), 600);
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if (jqxwindow.jqxValidator("validate")) {
					var row = {};
					row = {
						productStoreId : Grid.getDropDownValue($("#txtProductStore")).toString()
					};
					mainGrid.jqxGrid("addRow", null, row, "first");
					jqxwindow.jqxWindow("close");
				}
			});

			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				GridUtils.cleanDropDownValue($("#txtProductStore"))
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules :
				[
					{ input: "#txtProductStore", message: multiLang.fieldRequired, action: "close", 
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
		return {
			init : function() {
				jqxwindow = $("#addPOSOffline");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}