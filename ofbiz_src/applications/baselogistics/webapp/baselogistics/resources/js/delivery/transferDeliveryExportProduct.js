$(function() {  
	DlvProduct.init();
});

var DlvProduct = (function() {
	var grid = $("#jqxGridProduct");  
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
		if (listProductSelected.length > 0){
			updateProductData(listProductSelected);
		}
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
			{editable: false, text: uiLabelMap.Unit, sortable: false, dataField: 'uomId', width: 150, columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.QOH, datafield: 'qoh', sortable: false,  width: 150, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.RequiredNumber, datafield: 'createdQuantity', sortable: false,  width: 150, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.ActualExportedQuantity, datafield: 'quantity', sortable: false,  width: 150, editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
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
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
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
					if (cellvalue) {
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
				}, validation: function (cell, value) {
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					var data = grid.jqxGrid('getrowdata', cell.row);
					var total = value;
					if (data.createdQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(data.createdQuantity)};
					}
					if (data.qoh < total){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(total) + " > " + formatnumber(data.qoh)};
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.BLQuantityEATotal, datafield: 'quantityTotal', sortable: false,  width: 150, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					var total = 0;
					if (data.quantity > 0) {
						total = total + data.quantity;
					}
					return '<span class="align-right">' + formatnumber(total) +'</span>';
				}, 
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'unitPrice', sortable: false,  width: 150, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						if (locale && locale === 'vi' && typeof(value) === 'string'){
							value = data.unitPrice.toString().replace('.', '');
							value = value.replace(',', '.');
						}
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.Note, datafield: 'description', sortable: false,  width: 150, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
			},
        ];
		return columns; 
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'deliveryId', type: 'string'},
				{ name: 'deliveryItemSeqId', type: 'string'},
				{ name: 'transferId', type: 'string'},
				{ name: 'transferItemSeqId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'statusId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'currencyUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createdQuantity', type: 'number' },
				{ name: 'initQuantity', type: 'number' },
				{ name: 'qoh', type: 'number' },
				{ name: 'unitPrice', type: 'number' },
				{ name: 'expRequired', type: 'String'},
				{ name: 'mnfRequired', type: 'String'},
				{ name: 'lotRequired', type: 'String'},
				{ name: 'description', type: 'String'},
				{ name: 'requireAmount', type: 'String'}]
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
			useUrl: false,
			url: '',
			groupable: false,
			showgroupsheader: false,
			showaggregates: false,
			showstatusbar: false,
			virtualmode:false,
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
		grid.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantity'){
					var qoh = rowData.qoh;
					if (value > rowData.initQuantity) return false;
					var createdQuantity = rowData.createdQuantity;
					var total = value;
					if (total >= 0 && total <= qoh && total <= createdQuantity){
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
					
						var item = $.extend({}, rowData);
						item.quantity = value;
						listProductSelected.push(item);
					} 
				} 
			}
		});
	    
	    grid.on("bindingcomplete", function(event) {
	    	var args = event.args;
	    	var rows = grid.jqxGrid('getrows');
	    	if (listProductSelected.length > 0){
	    		for (var x in rows){
	    			var check = false;
	    			for (var y in listProductSelected){
	    				if (rows[x].productId === listProductSelected[y].productId) {
	    					check = true;
	    					break;
	    				}
		    		}
	    			var index = grid.jqxGrid('getrowboundindexbyid',rows[x].uid);
	    			if (check){
    					grid.jqxGrid('selectrow', index);
    				} else {
						grid.jqxGrid('unselectrow', index);
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
	    	if (column == 'quantity') {
    			if (data.qoh <= 0){
    	    		return 'background-warning';
    	    	} else {
    	    		return 'background-prepare';
    	    	}
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
	 var updateProductData = function (data){
		 var tmpS = grid.jqxGrid("source");
		tmpS._source.localdata = data;
		grid.jqxGrid("source", tmpS);
		grid.jqxGrid("updatebounddata");
	 }
	return {
		init : init,
		updateGridProductSource: updateGridProductSource,
	}
}());