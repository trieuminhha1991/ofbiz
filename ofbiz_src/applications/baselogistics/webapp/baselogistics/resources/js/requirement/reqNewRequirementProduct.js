$(function() {
	OlbReqProduct.init();
});

var OlbReqProduct = (function() {
	var grid = $('#jqxGridProduct');
	var product = null;
	var init = function() {
		initInput();
		initElementComplex();
		initEvents();
	};
	
	var initInput = function() {
	}
	
	var initElementComplex = function() {
		initGridProduct(grid);
	}
	var getColumns = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable:false,},
			{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', sortable: true, width: 120, editable: false, filterable: false, cellsalign: 'right', cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.Quantity, datafield: 'quantity', sortable: false,  width: 120, editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
					if (value > 0) {
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
					if (cellvalue) {
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
				}, validation: function (cell, value) {
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'unitCost', hidden: hidePrice, sortable: false, width: 120, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput', cellclassname: productGridCellclass,
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
						return '<span class="align-right">' + formatnumber(value, null, 3) +'</span>';
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
				}
			},
			{ text: uiLabelMap.BPOTotal, datafield: 'totalValue', hidden: hidePrice, sortable: false, width: 120, editable: false, filterable: false, cellsalign: 'right', sortable: false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value){
					var rowdata = $('#jqxGridProduct').jqxGrid('getrowdata', row);
					var quantity = rowdata.quantity;
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowdata.productId ){
			   					value = olb.unitCost;
			   					quantity = olb.quantity;
			   					return false;
			   				}
			   			});
				    }
					if (value){
						var lastPrice = parseFloat(value.toString().replace(',', '.'));
						value = lastPrice*parseFloat(quantity);
						if (value) {
							return '<span class="align-right">' + formatnumber(value) +'</span>';
						} else {
							return '<span class="align-right"></span>';
						}
					}
					return '<span class="align-right"></span>';
				},
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
        ];
		return columns;
	};
	
	var getDataField = function(){
		var datafield = [{ name: 'productId', type: 'string' },
							{ name: 'supplierProductId', type: 'string' },
							{ name: 'productCode', type: 'string' },
							{ name: 'productName', type: 'string' },
							{ name: 'quantity', type: 'number' },
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
		var configGrid = {
			datafields: getDataField(),
			columns: getColumns(grid),
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
		product = new OlbGrid(grid, null, configGrid, []);
	};
	
	var initEvents = function() {
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
					if (value != 0){
						var item = $.extend({}, rowData);
						item.quantity = value;
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
	    	if (listProductSelected.length > 0){
	    		for (var x in rows){
	    			var check = false;
	    			for (var y in listProductSelected){
	    				if (rows[x].productId === listProductSelected[y].productId) {
	    					check = true;
	    					break;
	    				}
		    		}
	    			var index = $('#jqxGridProduct').jqxGrid('getrowboundindexbyid',rows[x].uid);
	    			if (check){
    					$("#jqxGridProduct").jqxGrid('selectrow', index);
    				} else {
						$("#jqxGridProduct").jqxGrid('unselectrow', index);
					}
	    		}
	    	}
			
	    });
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
	
	var updateGridProductSource = function (source){
		grid.jqxGrid("source")._source.url = source;
		grid.jqxGrid("updatebounddata");
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
	return {
		init : init,
		updateGridProductSource: updateGridProductSource,
	}
}());