$(function() {
	SupReturnProductObj.init();
});
var SupReturnProductObj = (function() {
	var init = function() {
		if (listProductSelected === undefined) {
			listProductSelected = [];
		}
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		initProductGrid();
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#jqxgridProduct").on("bindingcomplete", function (event) {    
			if (!$("#jqxgridProduct").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).hasClass("hide")) {     
				$("#jqxgridProduct").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).addClass("hide");    
			}   
		});
		$("#jqxgridProduct").on("rowunselect", function(event) {
			var args = event.args;
			if (args.rowindex instanceof Array || args.rowindex < 0) {
				listProductSelected = [];
			} else {
				var rowBoundIndex = args.rowindex;
				var rowData = args.row;
				if (rowData) {
					var orderId = rowData.orderId;
					var productId = rowData.productId;
					$.each(listProductSelected, function(i) {
						var olb = listProductSelected[i];
						if (olb.orderId == orderId && olb.productId == productId) {
							listProductSelected.splice(i, 1);
						}
					});
				}
			}
		});

		$("#jqxgridProduct").on("rowselect", function(event) {
			var args = event.args;
			if (args.rowindex instanceof Array) {
				listProductSelected = [];
				for (var i = 0; i < args.rowindex.length; i++) {
					var allItems = $("#jqxgridProduct").jqxGrid("getrows")
					for (var j = 0; j < allItems.length; j++) {
						var rowData = allItems[j];
						if (rowData && rowData != window) {
							listProductSelected.push(rowData);
						}
					}
				}
			} else {
				var rowBoundIndex = args.rowindex;
				var rowData = args.row;
				if (rowData) {
					listProductSelected.push(rowData);
				}
			}
		});
	};
	var initValidateForm = function() {
		var extendRules = [];
		var mapRules = [];
	};

	var datafieldprs = [ { name : "orderId", type : "string" }, 
	                     { name : "productId", type : "string" }, 
	                     { name : "productCode", type : "string" }, 
	                     { name : "itemDescription", type : "string" }, 
	                     { name : "quantity", type : "number" }, 
	                     { name : "amount", type : "number" }, 
	                     { name : "unitPrice", type : "number" }, 
	                     { name : "unitPriceTmp", type : "number" }, 
	                     { name : "orderItemSeqId", type : "string" }, 
	                     { name : "orderedQuantity", type : "number" }, 
	                     { name : "returnableQuantity", type : "number" }, 
	                     { name : "returnReasonId", type : "string" }, 
	                     { name : "quantityUomId", type : "string" }, 
	                     { name : "weightUomId", type : "string" }, 
	                     { name : "requireAmount", type : "string" }, 
                     ];
	var columnprs = [
			{ text : uiLabelMap.SequenceId, sortable : false, filterable : false, editable : false, pinned : true, groupable : false, draggable : false, resizable : false, datafield : "", columntype : "number", width : 40,
				cellsrenderer : function(row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},
			{ text : uiLabelMap.OrderId, datafield : "orderId", width : 100, editable : false, hidden : false, pinned : true
			},
			{ text : uiLabelMap.ProductId, datafield : "productId", width : 120, editable : false, hidden : true, pinned : true
			},
			{ text : uiLabelMap.ProductId, datafield : "productCode", width : 120, editable : false, pinned : true
			},
			{ text : uiLabelMap.ProductName, dataField : "itemDescription", minwidth : 130, editable : false,
			},
			{ text : uiLabelMap.OrderNumber, datafield : "orderedQuantity", width : 130,
				editable : false, filterable : false, cellsalign : "right", columntype : "numberinput", cellsrenderer : function(row, column, value) {
					return '<span class="align-right">' + formatnumber(value) + '</span>';
				}
			},
			{ text : uiLabelMap.POReturnQuantity, datafield : "returnableQuantity", width : "120", editable : false, filterable : false, cellsalign : "right", columntype : "numberinput"
			},
			{ text : uiLabelMap.Unit, datafield : "quantityUomId", width : "120", editable : false, filterable : false, cellsalign : "right", columntype : "numberinput",
				cellsrenderer : function(row, column, value) {
					var data = $("#jqxgridProduct").jqxGrid("getrowdata", row);
					var requireAmount = data.requireAmount;
					if (requireAmount && requireAmount == 'Y') {
						value = data.weightUomId;
					}
					if (value) {
						return '<span class="align-right">' + getUomDescription(value) + '</span>';
					}
				},
			},
			{ text : uiLabelMap.Quantity, datafield : "quantity", width : "120", editable : true, filterable : false, cellsalign : "right", columntype : "numberinput", 
				cellsrenderer : function(row, column, value) {
					if (value) {
						return '<span class="cell-right-focus">' + formatnumber(value) + '</span>';
					}
				},
				initeditor : function(row, cellvalue, editor) {
					var data = $("#jqxgridProduct").jqxGrid("getrowdata", row);
					var requireAmount = data.requireAmount;
					if (requireAmount && requireAmount == 'Y') {
						editor.jqxNumberInput({ decimalDigits: 2, inputMode : "simple", spinMode : "simple", groupSeparator : ".", min : 0, max : data.returnableQuantity });
					} else {
						editor.jqxNumberInput({ decimalDigits: 0, inputMode : "simple", spinMode : "simple", groupSeparator : ".", min : 0, max : data.returnableQuantity });
					}
					if (data.quantity) {
						var u = data.quantity;
						if ('vi' == locale) {
							u = u.toString().replace('.', ',');
						}
						editor.jqxNumberInput('val', u);
					}
				},
				cellendedit : function(row, datafield, columntype, oldvalue, newvalue) {
					var rowdata = $("#jqxgridProduct").jqxGrid("getrowdata", row);
					if (newvalue != oldvalue && newvalue <= rowdata.returnableQuantity) {
						$("#jqxgridProduct").jqxGrid("selectrow", row);
					}
					if (newvalue > rowdata.returnableQuantity || newvalue < 0) return false;
				},
				validation : function(cell, value) {
					var data = $("#jqxgridProduct").jqxGrid("getrowdata", cell.row);
					if (value > data.returnableQuantity) {
						return { result : false, message : uiLabelMap.BPOQuantiyMustBeSmallerThanReturnableQuantity };
					}
					return true;
				}
			},
			{ text : uiLabelMap.UnitPrice, datafield : "unitPrice",
				width : 100, editable : true, filterable : false, cellsalign : "right", columntype : "numberinput", 
				cellsrenderer : function(row, column, value) {
					if (value) {
						return '<span class="cell-right-focus">' + formatnumber(value) + '</span>';
					}
				},
				createeditor : function(row, cellvalue, editor) {
					editor.jqxNumberInput({decimalDigits: 3, inputMode : "simple", spinMode : "simple", groupSeparator : ".", min : 0
					});
				},
				validation : function(cell, value) {
					var data = $("#jqxgridProduct").jqxGrid("getrowdata", cell.row);
					if (value > data.unitPriceTmp) {
						return {
							result : false,
							message : uiLabelMap.ReturnPriceCannotExceedPurchasePrice + " [" + formatnumber(data.unitPriceTmp) + "]"
						};
					}
					return true;
				}
			},
			{ text : uiLabelMap.Reason, datafield : "returnReasonId", width : 200, editable : true, filterable : false, columntype : "dropdownlist", 
				cellsrenderer : function(row, column, value) {
					if (value) {
						for (var i = 0; i < returnReasonData.length; i++) {
							if (value == returnReasonData[i].returnReasonId) {
								return '<span class="cell-left-focus">' + returnReasonData[i].description + '</span>';
							}
						}
					} else {
						return '<span class="cell-left-focus"></span>';
					}
				},
				createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight) {
					editor.jqxDropDownList({
						placeHolder : uiLabelMap.PleaseSelectTitle,
						source : returnReasonData,
						valueMember : "returnReasonId",
						displayMember : "description"
					});
				}
			}, 
		];
	var initProductGrid = function() {
		var grid = $("#jqxgridProduct");
		var datafield = datafieldprs;
		var columns = columnprs;
		var config = {
			width : "100%",
			virtualmode : false,
			filterable : false,
			showtoolbar : false,
			selectionmode : "checkbox",
			editmode : "click",
			pageable : true,
			sortable : true,
			filterable : true,
			editable : true,
			rowsheight : 26,
			url : "",
			source : {
				pagesize : 10
			}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		setTimeout(function() {
			if (!$("#jqxgridProduct").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).hasClass("hide")) {     
				$("#jqxgridProduct").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).addClass("hide");    
			}  
		}, 300);
	};
	var loadProduct = function loadProduct(valueDataSoure) {
		for (var i = 0; i < valueDataSoure.length; i++) {
			valueDataSoure[i]["unitPriceTmp"] = valueDataSoure[i]["unitPrice"];
		}
		var tmpS = $("#jqxgridProduct").jqxGrid("source");
		tmpS._source.localdata = valueDataSoure;
		$("#jqxgridProduct").jqxGrid("source", tmpS);
	};
	return {
		init : init,
		loadProduct : loadProduct,
	}
}());