if (typeof (GridDetail) == "undefined") {
	var GridDetail = (function() {
		var selectedProducts = new Array();
		var initRowDetail = function(index, parentElement, gridElement, datarecord) {
			$(parentElement).css("overflow-y", "scroll");
			var grid = $($(parentElement).children()[0]);
			$(grid).attr("id", "gridDetail" + index);
			
			var datafield =
			[
				{ name : "productId", type : "string" },
				{ name : "facilityId", type : "string" },
				{ name : "facilityName", type : "string" },
				{ name : "qoh", type : "number" },
				{ name : "qoo", type : "number" },
				{ name : "qpdL", type : "number" },
				{ name : "qpdS", type : "number" },
				{ name : "lidL", type : "number" },
				{ name : "lidS", type : "number" },
				{ name : "quantity", type : "number" },
				{ name : "lastSold", type : "date" },
				{ name : "lastReceived", type : "date" },
				{ name : "status", type : "string" }
			];
			var columnlist =
			[
				{ text : uiLabelMap.SettingFacilityId, datafield : "facilityId", editable : false, width : 100 },
				{ text : uiLabelMap.SettingFacilityName, datafield : "facilityName", editable : false, minwidth : 180 },
				{ text : uiLabelMap.SettingSummaryQOH, datafield : "qoh", editable : false, width : 100,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text : uiLabelMap.SettingQOO, datafield : "qoo", editable : false, width : 100,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text : uiLabelMap.SettingQty, datafield : "quantity", columntype : "numberinput", width : 100,
					cellclassname : function(row, column, value, data) {
						return "editable";
					}, cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
					}, cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						GridDetail.commitNewQuantity(grid, index, row);
					}, validation : function (cell, value) {
						if (value < 0) {
							return {
								result : false,
								message : uiLabelMap.SettingQuantityIsMustGreaterZero
							};
						}
						return true;
					}
				},
				{ text : uiLabelMap.SettingQPDL, datafield : "qpdL", editable : false, width : 100,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text : uiLabelMap.SettingQPDS, datafield : "qpdS", editable : false, width : 100,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text : uiLabelMap.SettingLIDL, datafield : "lidL", editable : false, width : 100,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text : uiLabelMap.SettingLIDS, datafield : "lidS", editable : false, width : 100,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div style=\"margin:4px;text-align: right;\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text : uiLabelMap.SettingLastsold, datafield : "lastSold", editable : false, cellsformat : "dd/MM/yyyy", width : 100 },
				{ text : uiLabelMap.SettingLastReceived, datafield : "lastReceived", editable : false, cellsformat : "dd/MM/yyyy", width : 100 },
				{ text : uiLabelMap.SettingStatus, datafield : "status", editable : false, width : 120 }
			];
			var config =
			{
				url : "JQGetRowDetailForCalculate&productId=" + datarecord.productId,
				width : "98%",
				height : 240,
				showtoolbar : false,
				editable : true,
				editmode : "click",
				showheader : false,
				selectionmode : "singlecell",
				theme : theme,
				pageable : false,
				localization : getLocalization()
			};
			Grid.initGrid(config, datafield, columnlist, null, grid);
		};
		var handleEvents = function() {
			mainGrid.on("rowselect", function (event) {
				var args = event.args;
				var rowBoundIndex = args.rowindex;
				var rowData = args.row;
				if (rowData.quantity) {
					rowData.rowBoundIndex = rowBoundIndex;
					selectedProducts.push(rowData);
				} else {
					mainGrid.jqxGrid("unselectrow", rowBoundIndex);
					mainGrid.jqxGrid("showrowdetails", rowBoundIndex);
				}
			});
			mainGrid.on("rowunselect", function (event) {
				var args = event.args;
				var rowData = args.row;
				selectedProducts = _.reject(selectedProducts, function(v){ return v.productId == rowData.productId });
			});
			mainGrid.on("bindingcomplete", function (event) {
				if (!mainGrid.find($("div[role=\"columnheader\"]")).find($("div[role=\"checkbox\"]")).hasClass("hide")) {
					mainGrid.find($("div[role=\"columnheader\"]")).find($("div[role=\"checkbox\"]")).addClass("hide");
				}
			});
		};
		var validator = function() {
			return !_.isEmpty(mainGrid.jqxGrid("getselectedrowindexes"));
		};
		var commitNewQuantity = function(grid, index, row) {
			setTimeout(function() {
				var rowdata = grid.jqxGrid("getrowdata", row);
				var totalForecast = rowdata.qoh + rowdata.qoo + rowdata.quantity;
				if (rowdata.qpdL != 0) {
					grid.jqxGrid("setcellvaluebyid", rowdata.uid, "lidL", totalForecast/rowdata.qpdL);
				}
				if (rowdata.qpdS != 0) {
					grid.jqxGrid("setcellvaluebyid", rowdata.uid, "lidS", totalForecast/rowdata.qpdS);
				}
				var rowsdata = grid.jqxGrid("getrows");
				var totalQuantity = 0;
				for ( var x in rowsdata) {
					var quantity = rowsdata[x].quantity;
					totalQuantity += quantity?quantity:0;
				}
				mainGrid.jqxGrid("setcellvalue", index, "quantity", totalQuantity);
				
				var lastPrice = mainGrid.jqxGrid("getcellvalue", index, "lastPrice");
				if (lastPrice) {
					mainGrid.jqxGrid("setcellvalue", index, "grandTotal", totalQuantity*lastPrice);
					mainGrid.jqxGrid("selectrow", index);
				}
			}, 10);
		};
		var getValue = function() {
			selectedProducts = _.uniq(selectedProducts);
			for ( var x in selectedProducts) {
				var grid = $("#gridDetail" + selectedProducts[x].rowBoundIndex);
				var rowsdata = grid.jqxGrid("getrows");
				var rowDetails = new Array();
				for ( var y in rowsdata) {
					if (rowsdata[y].quantity > 0) {
						rowDetails.push(rowsdata[y]);
					}
				}
				selectedProducts[x].rowDetails = rowDetails;
			}
			return selectedProducts;
		};
		return {
			init: function() {
				handleEvents();
			},
			initRowDetail: initRowDetail,
			commitNewQuantity: commitNewQuantity,
			getValue: getValue,
			validator: validator
		};
	})();
}