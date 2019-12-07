$(function() {
	OlbEditReqProduct.init();
});

var OlbEditReqProduct = (function() {
	var grid = $('#jqxGridProduct');
	var gridProductAdd = $("#jqxgridProductAdd"); 
	var productAddOLBG = null;
	var listProductAdd = [];
	var init = function() {
		initListProductSelected();
		initInput();
		initElementComplex();
		initEvents();
		initGridData();
	};
	
	var initInput = function() {
		$("#addProductPopup").jqxWindow({
 		    maxWidth: 1500, minWidth: 500, width: 1000, modalZIndex: 10000, zIndex:10000, minHeight: 200, height: 470, maxHeight: 670, resizable: false, cancelButton: $("#addProductCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
 		});
	}
	
	var initElementComplex = function() {
		initGridProduct(grid);
		initProductGridAdd();
	}
	
	
	var getColumns = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value+1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, sortable: true, filterable: true, editable: false},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, sortable: true, filterable: true, editable:false,},
			{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', sortable: true, hidden : hideFacility , width: 120, editable: false, filterable: false, cellsalign: 'right', cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y') {
						value = data.amountOnHandTotal;
					} 
					var description = formatnumber(value);
					if (data.requireAmount && data.requireAmount == 'Y') {
						description = formatnumber(value) + ' (' + getUomDesc(data.weightUomId) +')';	
					} else {
						description = formatnumber(value) + ' (' + getUomDesc(data.quantityUomId)+')';
					}
					return '<span class="align-right">' + description +'</span>';
				}, 
			},
			{editable: false, text: uiLabelMap.Unit, sortable: false, dataField: 'uomId', width: 120, columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					
					if (!value) {
						if (rowData.requireAmount && 'Y' == rowData.requireAmount) {
							if (rowData.weightUomId) {
								value = rowData.weightUomId;
							}
						} else { 
							if (rowData.quantityUomId) {
								value = rowData.quantityUomId;
							}
						}
					} 
					if (value) {
						var desc = getUomDesc(value);
						return '<span class="align-right">' + desc +'</span>';
					} 
					return value;
				}, 
			},
			{ text: uiLabelMap.Quantity, datafield: 'quantity',  width: 120, editable: true, filterable: false, sortable: true, cellclassname: productGridCellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					var checkExpPurchase = false;
					var checkExpSales = false;
					if (rowData['purchaseDiscontinuationDate'] != undefined && rowData['purchaseDiscontinuationDate'] != null) {
						var now = new Date();
						var ex1 = new Date(rowData['purchaseDiscontinuationDate']);
						var ex2 = new Date(rowData['salesDiscontinuationDate']);
						if (ex1 <= now){
							checkExpPurchase = true;
				        }
						if (ex2 <= now){
							checkExpSales = true;
						}
				    }
				    if (typeof value === 'string') {
				    	value = value.replace(',', '.');
				    	value = parseFloat(value, 3, null);
				    }
				    if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.quantity;
			   					return false;
			   				}
			   			});
				    }
					if (value >= 0) {
						if (checkExpPurchase){
							return '<span class="align-right" title=\"'+ uiLabelMap.BSDiscountinuePurchase+'\">'+uiLabelMap.BSDiscountinuePurchase+'</span>';	
						} else if (checkExpSales){
							return '<span class="align-right" title=\"'+ uiLabelMap.BSDiscountinueSales+'\">'+uiLabelMap.BSDiscountinueSales+'</span>';	
						} else {
							return '<span class="align-right">' + formatnumber(value) +'</span>';
						}
					} else {
						if (checkExpPurchase){
							return '<span class="align-right" title=\"'+ uiLabelMap.BSDiscountinuePurchase + '\">' + uiLabelMap.BSDiscountinuePurchase +  '</span>'; 	
						} else if (checkExpSales){
							return '<span class="align-right" title=\"'+ uiLabelMap.BSDiscountinueSales+'\">'+uiLabelMap.BSDiscountinueSales+'</span>';	
						} else {
							return '<span class="align-right"></span>';
						}
					}
					return '<span class="align-right"></span>';
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
					var rowData = grid.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						if (listProductSelected.length > 0){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.quantity;
				   					return false;
				   				}
				   			});
					    }
					}
					if (cellvalue || cellvalue == 0) {
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
				}, validation: function(cell, value){
					if(value < 0){
						return {result: false, message: uiLabelMap.BLSGCMustNotInputNegativeValue }
					}
					return true;
				}
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'unitCost', sortable: false, width: 120, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput', cellclassname: productGridCellclass,
				cellbeginedit: function (row, datafield, columntype) {
				},	
				cellsrenderer: function(row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.unitCost;
			   					return false;
			   				}
			   			});
				    }
					if (value) {
						if (typeof value == 'number') {
							value = parseFloat(value);
						} else if (typeof value == 'string'){
							value = parseFloat(value.replace(',', '.'));
						}
					}
					if (value) {
						return '<span class="align-right">' + formatcurrency(value) +'</span>';
					}
					else {
						return '<span class="align-right">' + formatcurrency(0) +'</span>';
					}
				},
				initeditor: function (row, cellvalue, editor) {
					editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 2 });
					var rowData = grid.jqxGrid('getrowdata', row);
					if (!cellvalue){
						if (listProductSelected.length > 0){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.unitCost;
				   					return false;
				   				}
				   			});
					    }
					}
					if (cellvalue) {
						var price = cellvalue;
						if (locale == 'vi'){
							price = price.toString();
							price = price.replace(',','');
							price = price.replace('.',',');
						}
						editor.jqxNumberInput('val', price);
					} 
				},
				validation: function(cell, value){
					if(value < 0){
						return {result: false, message: uiLabelMap.BLSGCMustNotInputNegativeValue }
					}
					return true;
				}
			},
			{ text: uiLabelMap.Note, datafield: 'comment', sortable: false, width: 150, editable: true, filterable: false, cellsalign: 'left', sortable: false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if (value) {
						return '<span>' + value +'</span>';
					} 
					return value;
				}, 
				initeditor: function (row, cellvalue, editor) {
					editor.jqxInput();
					if (!cellvalue) {
						cellvalue = unescapeHTML(cellvalue);
					}
					editor.jqxInput('val', cellvalue);
				},
				validation: function (cell, value) {
					if (checkSpecialCharacters(value)){
						return { result: false, message: uiLabelMap.validContainSpecialCharacter };
					}
					return true;
				}
			},
			{ text: uiLabelMap.BPOTotal, datafield: 'totalValue', sortable: false, width: 120, editable: false, filterable: false, cellsalign: 'right', sortable: false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value){
					var rowdata = $('#jqxGridProduct').jqxGrid('getrowdata', row);
					var quantity = rowdata.quantity;
					var unitCost = rowdata.unitCost;
					if(quantity != null && unitCost != null){
						var lastPrice = parseFloat(unitCost.toString().replace(',', '.'));
						value = lastPrice*parseFloat(quantity);
						if (value) {
							return '<span class="align-right">' + formatcurrency(value) +'</span>';
						} else {
							return '<span class="align-right">' + formatcurrency(0) +'</span>';
						}
					}
				},
			},
        ];
		return columns;
	};
	
	var getDataField = function(){
		var datafield = [	{ name: '	', type: 'string' },
		                 	{ name: 'productId', type: 'string' },
		                 	{ name: 'statusId', type: 'string' },
							{ name: 'supplierProductId', type: 'string' },
							{ name: 'productCode', type: 'string' },
							{ name: 'productName', type: 'string' },
							{ name: 'quantity', type: 'number' },
							{ name: 'sequenceId', type: 'string'},
							{ name: 'quantityUomId', type: 'string' },
							{ name: 'quantityUomIds', type: 'string' },
							{ name: 'unitCost', type: 'number' },
							{ name: 'totalValue', type: 'number' },
							{ name: 'quantityOnHandTotal', type: 'number' },
							{ name: 'amountOnHandTotal', type: 'number' },
							{ name: 'description', type: 'string' },
							{ name: 'comment', type: 'string' },
							{ name: 'convertNumber', type: 'number' },
							{ name: 'weightUomId', type: 'string' },
							{ name: 'weightUomIds', type: 'string' },
							{ name: 'requireAmount', type: 'string' },
							{ name: 'amountUomTypeId', type: 'string' },
							{ name: 'uomId', type: 'string' },
							{ name: 'purchaseDiscontinuationDate', type: 'date', other: 'timestamp'},
							{ name: 'salesDiscontinuationDate', type: 'date', other: 'timestamp'}];
		return datafield;
	};
	
	var initGridProduct = function(grid){
		var rendertoolbarProduct = function (toolbar){
			toolbar.html("");
	        var container = $("<div id='toolbarcontainerGridProduct' class='widget-header' style='height:33px !important;'><div id='jqxProductSearch' class='pull-right' style='margin-left: -10px !important; margin-top: 4px'></div></div>");
	        toolbar.append(container);
	        container.append('<div class="margin-top10">');
	        container.append('<a href="javascript:OlbEditReqProduct.deleteRow()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="red fa fa-times"></i></a>');
	        container.append('<a href="javascript:OlbEditReqProduct.addWithGrid()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="fa fa-plus"></i></a>');
	        container.append('</div>');
	        var facilityToSearch = requirement.facilityId;
	        var paramInput = {
	        		facilityId: facilityToSearch,
			};
	        ProductSearch.init($("#jqxProductSearch"), $("#jqxGridProduct"), "quantity", "findProductByOrganization", "listProducts",  paramInput, listProductSelected, uiLabelMap.BPSearchProductToAdd, uiLabelMap.BPProductNotFound);
		}
		
		
		var configGrid = {
			datafields : getDataField(),
			columns : getColumns(grid),
			width: '100%',
			height: 'auto',
			sortable: true,
			editable: true,
			filterable: true,
			pageable: true,
			showfilterrow: true,
			useUtilFunc: false,
			useUrl: false,
			groupable: false,
			showgroupsheader: false,
			showaggregates: false,
			showstatusbar: false,
			virtualmode:false,
			showdefaultloadelement:true,
			autoshowloadelement:true,
			showtoolbar:true,
			columnsresize: true,
			isSaveFormData: true,
			toolbarheight: 38,
			rendertoolbar: rendertoolbarProduct,
			formData: "filterObjData",
			selectionmode: "singlerow",
			bindresize: true,
			pagesize: 10,
		};
		product = new OlbGrid(grid, null, configGrid, []);
	};
	
	var initEvents = function() {
		$("#jqxGridProduct").on("rowselect", function (event) {    
			var args = event.args;
			var rowData = args.row;
			if (rowData){
				productSelected = $.extend({}, rowData);
			}
		});
		
		$("#jqxGridProduct").on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantity'){
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
					var item = $.extend({}, rowData);
					if (value >=0 && value != undefined ){
						item.quantity = value;
						if(value == 0){
							item.statusId = 'REQ_CANCELLED';
						}
						listProductSelected.push(item);
					}
				} else if(dataField == 'unitCost'){
					if (rowData.quantity > 0){
						var item = $.extend({}, rowData);
						item.unitCost = value;
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
						listProductSelected.push(item);
					}
				}  else if (dataField == 'comment'){
					if (rowData.quantity > 0 && value != '' && value != null && value != undefined & value != 'null'){
						if (listProductSelected.length > 0){
							$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					listProductSelected[i].comment = value;
				   					return false;
				   				}
				   			});
						}
					}
				}
			}
		});
	    
	    $("#jqxGridProduct").on("bindingcomplete", function(event) {
	    	var args = event.args;
	    	var rows = $("#jqxGridProduct").jqxGrid('getrows');
	    	if(rows !=  null) {
	    	}
	    });
	   
	    gridProductAdd.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantity'){
					$.each(listProductAdd, function(i){
		   				var olb = listProductAdd[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductAdd.splice(i,1);
		   					return false;
		   				}
		   			});
					var item = $.extend({}, rowData);
					if (value >=0 && value != undefined ){
						item.quantity = value;
						if(value == 0){
							item.statusId = 'REQ_CANCELLED';
						}
						listProductAdd.push(item);
					}
				} else if(dataField == 'unitCost'){
					if (rowData.quantity > 0){
						var item = $.extend({}, rowData);
						item.unitCost = value;
						$.each(listProductAdd, function(i){
			   				var olb = listProductAdd[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductAdd.splice(i,1);
			   					return false;
			   				}
			   			});
						listProductAdd.push(item);
					}
				}  else if (dataField == 'comment'){
					if (rowData.quantity > 0 && value != '' && value != null && value != undefined & value != 'null'){
						if (listProductAdd.length > 0){
							$.each(listProductAdd, function(i){
				   				var olb = listProductAdd[i];
				   				if (olb.productId == rowData.productId ){
				   					listProductAdd[i].comment = value;
				   					return false;
				   				}
				   			});
						}
					}
				}
			}
		});
		
		$("#addProductSave").on("click", function (event) {
			var rowPosition = "first";
			if (listProductAdd.length > 0){
				for (var j in listProductAdd){
					var data = listProductAdd[j];
					var check = false;
					var productId = data.productId;
					var item = null;
					
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == data.productId ){
		   					item = olb;
		   					return false;
		   				}
		   			});
					
					if (item){
						if(item.statusId == "REQ_CANCELLED"){
							item.statusId = requirement.statusId;
							item.quantity = data.quantity;
						} else {
							var x = item.quantity;
							if (typeof x === 'string') {
								x.replace(',', '.');
								x = parseFloat(x, 3, null);
							}
							item.quantity = x + data.quantity;
						}
						item.unitCost = data.unitCost;
						var curRows = grid.jqxGrid('getrows');
						for (var z in curRows){
							var t = curRows[z];
							if (t.productId == productId){
								$("#jqxGridProduct").jqxGrid('setcellvaluebyid', t.uid, "quantity", item.quantity);
							}
						}
					} else {
						var x = $.extend({}, data);
						x.statusId = requirement.statusId;
						listProductSelected.push(x);
					}
				}
			}
			var listProductSelectedFinal = [];
			for(var i=0; i<listProductSelected.length; i++){
				if(listProductSelected[i].statusId != "REQ_CANCELLED" && listProductSelected[i].quantity != 0){
					listProductSelectedFinal.push(listProductSelected[i]);
				}
			}
			
			OlbGridUtil.updateSource(grid, null, listProductSelectedFinal, false);
 			grid.jqxGrid('updatebounddata');
			$("#addProductPopup").jqxWindow('close');
		});
		
		$("#addProductPopup").on("close", function (event) {
			listProductAdd = [];
		});
	};
	
	var dataFieldAdd = [
	                	{name: 'productId', type: 'string'},
	        			{name: 'productCode', type: 'string'},
	               		{name: 'productName', type: 'string'},
	               		{name: 'requireAmount', type: 'string'},
	               		{name: 'description', type: 'string'},
	               		{name: 'quantityUomId', type: 'string'},
	               		{name: 'quantityOnHandTotal', type: 'number' },
	               		{name: 'unitCost', type: 'number'},
	               		{name: 'quantity', type: 'number'},
	               		{ name: 'requireAmount', type: 'string' },
	               		{ name: 'uomId', type: 'string' },
	               		{ name: 'weightUomIds', type: 'string' },
	               		{ name: 'amountOnHandTotal', type: 'string' },
	               		
	                    ];
	var columnAdd = [
				{text: uiLabelMap.ProductId, dataField: 'productCode', width: 140, editable:false},
				{text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 120, editable:false},
				{text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 120, editable:false, filterable: false,
					cellsrenderer : function(row, column, value) {
						var data = gridProductAdd.jqxGrid("getrowdata", row);
						var requireAmount = data.requireAmount;
						if (requireAmount && requireAmount == 'Y') {
							value = data.uomId;
							
						}
						if (value) {
							return '<span class="align-right">' + getUomDesc(value) + '</span>';
						}
					},
				},
				{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal',filterable : false, hidden : hideFacility,  sortable: false, width: 120, editable: false, cellsalign: 'right', cellclassname: productGridCellclass,
					cellsrenderer: function(row, column, value) {
						var data = gridProductAdd.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') {
							value = data.amountOnHandTotal;
						} 
						var description = formatnumber(value);
						if (data.requireAmount && data.requireAmount == 'Y') {
							description = formatnumber(value) + ' (' + getUomDesc(data.uomId) +')';	
						} else {
							description = formatnumber(value) + ' (' + getUomDesc(data.quantityUomId)+')';
						}
						return '<span class="align-right">' + description +'</span>';
					}, 
				},
				{ text : uiLabelMap.Quantity, datafield : "quantity", width : "120", editable : true, filterable : false, sortable: false, cellsalign : "right", columntype : "numberinput", cellClassName: 'background-prepare',
					cellsrenderer : function(row, column, value) {
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if (listProductAdd.length > 0){
					    	$.each(listProductAdd, function(i){
				   				var olb = listProductAdd[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.quantity;
				   					return false;
				   				}
				   			});
					    }
						if (value > 0) {
							return '<span class="align-right">' + formatnumber(value) +'</span>';
						} else {
							return '<span class="align-right"></span>';
						}
					},
					initeditor: function (row, cellvalue, editor) {
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if ('Y' == rowData.requireAmount) {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
						} else {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
						}
						if (!cellvalue) {
							if (listProductAdd.length > 0){
						    	$.each(listProductAdd, function(i){
					   				var olb = listProductAdd[i];
					   				if (olb.productId == rowData.productId ){
					   					cellvalue = olb.quantity;
					   					return false;
					   				}
					   			});
						    }
						}
						if (cellvalue) {
							var u = cellvalue.toString().replace('.', ',');
							editor.jqxNumberInput('val', u);
						}
					},
					validation : function(cell, value) {
						var data = grid.jqxGrid("getrowdata", cell.row);
						if (value < 0) {
							return { result : false, message : uiLabel.ValueMustBeGreaterThanZero };
						}
						return true;
					}
				},
				{ text : uiLabelMap.UnitPrice, datafield : "unitCost",
					width : 100, editable : true, filterable : false, cellsalign : "right", columntype : "numberinput", cellClassName: 'background-prepare', 
					cellsrenderer: function(row, column, value){
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if (listProductAdd.length > 0){
					    	$.each(listProductAdd, function(i){
				   				var olb = listProductAdd[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.unitCost;
				   					return false;
				   				}
				   			});
					    }
						if (value) {
							if (typeof value == 'number') {
								value = parseFloat(value);
							} else if (typeof value == 'string'){
								value = parseFloat(value.replace(',', '.'));
							}
						}
						if (value) {
							return '<span class="align-right">' + formatcurrency(value) +'</span>';
						}
						else return '<span class="align-right">' + formatcurrency(0) +'</span>';
					},
					initeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 2 });
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if (!cellvalue){
							if (listProductAdd.length > 0){
						    	$.each(listProductAdd, function(i){
					   				var olb = listProductAdd[i];
					   				if (olb.productId == rowData.productId ){
					   					cellvalue = olb.unitCost;
					   					return false;
					   				}
					   			});
						    }
						}
						if (cellvalue) {
							var price = cellvalue;
							if (locale == 'vi'){
								price = price.toString();
								price = price.replace(',','');
								price = price.replace('.',',');
							}
							editor.jqxNumberInput('val', price);
						} 
					},
					validation: function(cell, value){
						if(value < 0){
							return {result: false, message: uiLabelMap.BLSGCMustNotInputNegativeValue }
						}
						return true;
					},
					
				},
				{ text: uiLabelMap.BPOTotal, datafield: 'totalValue', sortable: false, width: 120, editable: false, filterable: false, cellsalign: 'right', sortable: false, cellclassname: productGridCellclass,
					cellsrenderer: function(row, column, value){
						var rowdata = gridProductAdd.jqxGrid('getrowdata', row);
						var quantity = rowdata.quantity;
						var unitCost = rowdata.unitCost;
						if(quantity != null && unitCost != null){
							var lastPrice = parseFloat(unitCost.toString().replace(',', '.'));
							value = lastPrice*parseFloat(quantity);
							if (value) {
								return '<span class="align-right">' + formatcurrency(value) +'</span>';
							} else {
								return '<span class="align-right">' + formatcurrency(0) +'</span>';
							}
						}
					},
				},
             ];
	
	var initProductGridAdd = function() {
		var gridAdd = gridProductAdd;
		var datafield = dataFieldAdd;
		var columns = columnAdd;
		var configProductAdd = {
				datafields: datafield,
				columns: columns,
				width: '100%',
				height: 'auto',
				sortable: true,
				editable: true,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: '',
				groupable: false,
				showgroupsheader: false,
				showaggregates: false,
				showstatusbar: false,
				virtualmode:true,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				showtoolbar:false,
				columnsresize: true,
				isSaveFormData: true,
				formData: "filterObjData",
				selectionmode: "singlerow",
				bindresize: true,
				pagesize: 10,
			};
			productAddOLBG = new OlbGrid(gridAdd, null, configProductAdd, []);
			
	};
	
	
	var initListProductSelected = function(){
		$.ajax({
			type: 'POST',
			url: 'jqxGeneralServicer?sname=JQGetListRequirementItem',
			async: false,
			data: {
				facilityId: requirement.facilityId,
				requirementId: requirement.requirementId
			},
			success: function(data){
				list = data.results;
			}
		});
		for(var i=0; i < list.length; i++){
			listProductSelected[i] = list[i];
		}
	}
	
	var productGridCellclass = function (row, column, value, data) {
		var data = grid.jqxGrid('getrowdata',row);
		if (data['purchaseDiscontinuationDate'] != undefined && data['purchaseDiscontinuationDate'] != null) {
			var now = new Date();
			var ex = new Date(data['purchaseDiscontinuationDate']);
			if (ex <= now){
	        	return 'background-cancel';
	        }
	    } else {
	    	if (column == 'quantity' || column == 'unitCost' || column == 'comment') {
				return 'background-prepare';
	    	}
	    }
	}
	
	
	 function initGridData(){
		 OlbGridUtil.updateSource(grid, null, listProductSelected, false);
		 grid.jqxGrid('updatebounddata');
	 }
	 
	 function escapeHtml(string) {
		 return String(string).replace(/[&<>"'\/]/g, function (s) {
		      return entityMap[s];
		 });
	 }
		 
	 function unescapeHTML(escapedStr) {
		 var div = document.createElement('div');
		     div.innerHTML = escapedStr;
		     var child = div.childNodes[0];
		     return child ? child.nodeValue : '';
	};
		 
	var checkSpecialCharacters = function(value) {
		if (OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/.test(value))) {
			return true;
		}
			return false;
	}
	
	var deleteRow = function (){
		if (productSelected == null) {
			jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
			return false;
		}
		var rowindex = $("#jqxGridProduct").jqxGrid('getselectedrowindex');
		$("#jqxGridProduct").jqxGrid('endcelledit', rowindex, "quantity", false);
		var data = $("#jqxGridProduct").jqxGrid('getrowdata', rowindex);
		if (data){
			if (data.quantity > 0){
				$.each(listProductSelected, function(m){
			   		var olb = listProductSelected[m];
			   		if (olb) {
			   			if (olb.productId == data.productId) {
				   			listProductSelected[m].statusId = "REQ_CANCELLED";
				   			return false;
				   		}
			   		}
			   	});
			}
			var rowid = data.uid;
			$("#jqxGridProduct").jqxGrid('deleterow', rowid);
		}
	}
		 
	var addWithGrid = function (){
		if (OlbCore.isNotEmpty(requirement.facilityId)){
			productAddOLBG.updateSource("jqxGeneralServicer?sname=JQGetListProductByOrganiztion&inventoryInfo=Y&facilityId="+requirement.facilityId+"&requirementTypeId=RECEIVE_REQUIREMENT");
		} else {
			productAddOLBG.updateSource("jqxGeneralServicer?sname=JQGetListProductByOrganiztion");
		}
		$("#addProductPopup").jqxWindow('open');
		$("#jqxgridProductAdd").jqxGrid("updatebounddata");
	}
			
	var loadProduct = function loadProduct(valueDataSoure) {
		for (var i = 0; i < valueDataSoure.length; i++) {
			valueDataSoure[i]["unitPriceTmp"] = valueDataSoure[i]["unitPrice"];
		}
		var tmpS = grid.jqxGrid("source");
		tmpS._source.localdata = valueDataSoure;
		grid.jqxGrid("source", tmpS);
	};
			
	function productGridCellclass (row, column, value, data) {
		if (column == 'quantity' || column == 'unitCost' || column == 'returnReasonId') {
			return 'background-prepare';
		}
	}
			
	return {
		init : init,
		loadProduct : loadProduct,
		deleteRow : deleteRow,
		addWithGrid : addWithGrid,
	}
}());