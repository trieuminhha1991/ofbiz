$(function() {
	SupReturnProductPromoObj.init();
});
var SupReturnProductPromoObj = (function() {
    var validatorVAL;
    var init = function() {
		if (listProductPromoSelected === undefined) {
			listProductPromoSelected = [];
		}
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};

    var updateGridProductPromo = function(listOrderIds) {
        var listObj = [];
        for (var i = 0; i < listOrderIds.length; i++) {
            var row = {};
            row["orderId"] = listOrderIds[i];
            listObj.push(row);
        }
        listObj = JSON.stringify(listObj);
        var listOrderItems = [];
        $.ajax({
            type : "POST",
            url : "getOrderPromoItemsByOrdersToReturn",
            data : {
                "listOrderIds" : listObj
            },
            dataType : "json",
            async : false,
            success : function(response) {
                listOrderItems = response.listOrderItems;
            },
            error : function(response) {
                alert("Error:" + response);
            }
        }).done(function() {
            loadProductPromo(listOrderItems);
        });
    };

	var initInputs = function() {
		initProductPromoGrid();
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#jqxgridProductPromo").on("bindingcomplete", function (event) {
			if (!$("#jqxgridProductPromo").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).hasClass("hide")) {
				$("#jqxgridProductPromo").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).addClass("hide");
			}   
		});
		$("#jqxgridProductPromo").on("rowunselect", function(event) {
			var args = event.args;
			if (args.rowindex instanceof Array || args.rowindex < 0) {
				listProductPromoSelected = [];
			} else {
				var rowData = args.row;
				if (rowData) {
					var orderId = rowData.orderId;
					var productPromoId = rowData.productPromoId;
					var productPromoActionSeqId = rowData.productPromoActionSeqId;
					var productPromoRuleId = rowData.productPromoRuleId;
					$.each(listProductPromoSelected, function(i) {
						var olb = listProductPromoSelected[i];
						if (olb.orderId == orderId && olb.productPromoId == productPromoId && olb.productPromoActionSeqId == productPromoActionSeqId
                        && olb.productPromoRuleId == productPromoRuleId) {
							listProductPromoSelected.splice(i, 1);
						}
					});
				}
			}
		});

		$("#jqxgridProductPromo").on("rowselect", function(event) {
			var args = event.args;
			if (args.rowindex instanceof Array) {
				listProductPromoSelected = [];
				for (var i = 0; i < args.rowindex.length; i++) {
					var allItems = $("#jqxgridProductPromo").jqxGrid("getrows");
					for (var j = 0; j < allItems.length; j++) {
						var rowData = allItems[j];
						if (rowData && rowData != window) {
							listProductPromoSelected.push(rowData);
						}
					}
				}
			} else {
				var rowData = args.row;
				if (rowData) {
					listProductPromoSelected.push(rowData);
				}
			}
		});
	};
	var initValidateForm = function() {
		var extendRules = [];
		var mapRules = [];
	};

    var getValidator = function() {
        return validatorVAL;
    };

	var datafieldprs = [ { name : "orderId", type : "string" },
                         { name : "orderAdjustmentId", type : "string" },
                         { name : "orderAdjustmentTypeId", type : "string" },
                         { name : "productPromoId", type : "string" },
                         { name : "productPromoRuleId", type : "string" },
                         { name : "productPromoActionSeqId", type : "string" },
	                     { name : "productPromoName", type : "string" },
                         { name : "orderedPromoAmount", type : "number" },
                         { name : "returnableAmount", type : "number" },
                         { name : "amount", type : "number" },
                         { name : "returnReasonId", type : "string" }
    ];
	var columnprs = [
			{ text : uiLabelMap.SequenceId, sortable : false, filterable : false, editable : false, pinned : true, groupable : false, draggable : false, resizable : false, datafield : "", columntype : "number", width : 50,
				cellsrenderer : function(row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},
			{ text : uiLabelMap.OrderId, datafield : "orderId", width : 100, editable : false, hidden : false, pinned : true
			},
			{ text : uiLabelMap.ProductPromoId, datafield : "productPromoId", width : 120, editable : false, pinned : true
			},
			{ text : uiLabelMap.ProductPromoName, datafield : "productPromoName", minwidth : 150, editable : false, pinned : true
			},
			{ text : uiLabelMap.orderedPromoAmount, dataField : "orderedPromoAmount", minwidth: 70, editable : false,
			},
            { text : uiLabelMap.returnableAmount, dataField : "returnableAmount", minwidth : 70, editable : false,
            },
            { text : uiLabelMap.returnAmount, datafield : "amount", width : "150", editable : true, filterable : false, cellsalign : "right", columntype : "numberinput",
				cellsrenderer : function(row, column, value) {
					if (value) {
						return '<span class="cell-right-focus">' + formatnumber(value) + '</span>';
					}
				},
				initeditor : function(row, cellvalue, editor) {
					var data = $("#jqxgridProductPromo").jqxGrid("getrowdata", row);
					editor.jqxNumberInput({ decimalDigits: 0, inputMode : "simple", spinMode : "simple", groupSeparator : ".", min : 0, max : data.returnableAmount});
					if (data.amount) {
						var u = data.amount;
						if ('vi' == locale) {
							u = u.toString().replace('.', ',');
						}
						editor.jqxNumberInput('val', u);
					}
				},
				cellendedit : function(row, datafield, columntype, oldvalue, newvalue) {
					var rowdata = $("#jqxgridProductPromo").jqxGrid("getrowdata", row);
					if (newvalue != oldvalue && newvalue <= rowdata.returnableAmount) {
						$("#jqxgridProductPromo").jqxGrid("selectrow", row);
					}
					if (newvalue > rowdata.returnableAmount || newvalue < 0) return false;
				},
				validation : function(cell, value) {
					var data = $("#jqxgridProductPromo").jqxGrid("getrowdata", cell.row);
					if (value > data.returnableAmount) {
						return { result : false, message : uiLabelMap.BPOAmountMustBeSmallerThanReturnableAmount };
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
	var rendertoolbar = function(toolbar){
        toolbar.html("");
        var id = "jqxgridProductPromo";
        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4 style='max-width: 60%; overflow: hidden'>"+uiLabelMap.listReturnProductPromoByOrder+"</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
        toolbar.append(jqxheader);
    };
	var initProductPromoGrid = function() {
		var grid = $("#jqxgridProductPromo");
		var datafield = datafieldprs;
		var columns = columnprs;
		var config = {
			width : "100%",
			virtualmode : false,
            rendertoolbar: rendertoolbar,
			filterable : false,
			showtoolbar : true,
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
			if (!$("#jqxgridProductPromo").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).hasClass("hide")) {
				$("#jqxgridProductPromo").find($('div[role="columnheader"]')).find($('div[role="checkbox"]')).addClass("hide");
			}  
		}, 300);
	};
	var loadProductPromo = function loadProductPromo(valueDataSource) {
		var tmpS = $("#jqxgridProductPromo").jqxGrid("source");
		tmpS._source.localdata = valueDataSource;
		$("#jqxgridProductPromo").jqxGrid("source", tmpS);
	};
	return {
		init : init,
		loadProductPromo : loadProductPromo,
        updateGridProductPromo: updateGridProductPromo
	}
}());