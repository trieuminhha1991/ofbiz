$(function() { 
	PurDlvProduct.init();
});

var PurDlvProduct = (function() {
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
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: '8%', editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: '12%', editable:false,},
			{editable: false, text: uiLabelMap.Unit, columngroup: 'RequiredQuantity', sortable: false, dataField: 'uomId', width: '5%', columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					
					if (!value) {
						if (rowData.requireAmount && 'Y' == rowData.requireAmount) {
							if (rowData.weightUomId) {
								value = rowData.weightUomId;
							}
						} else { 
							if (rowData.orderQuantityUomId) {
								value = rowData.orderQuantityUomId;
							} else if (rowData.quantityUomId) {
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
			{ text: uiLabelMap.BLPackingForm, datafield: 'convertNumber', columngroup: 'RequiredQuantity', sortable: false,  width: '6%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, 
			},
			{ text: uiLabelMap.BLQuantityByQCUom, datafield: 'requiredQuantityQC', columngroup: 'RequiredQuantity', sortable: false,  width: '6%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						var rowData = grid.jqxGrid('getrowdata', row);
						if (rowData.requiredQuantity > 0 && rowData.convertNumber > 0){
							value = rowData.requiredQuantity/rowData.convertNumber;
						}
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					}
					return '<span class="align-right"></span>';
				}, 
			},
			{ text: uiLabelMap.BLQuantityEATotal, datafield: 'requiredQuantity', columngroup: 'RequiredQuantity', sortable: false,  width: '6%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, 
			},
			{ text: uiLabelMap.BLQuantityByQCUom, datafield: 'quantityQC', columngroup: 'ReceivedQuantity', sortable: false,  width: '8%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
			   					value = olb.quantityQC;
			   					return false;
			   				}
			   			});
				    }
					if (value >= 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if (rowData.orderQuantityUomId == rowData.quantityUomId){
						editor.jqxNumberInput({disabled: true});
					} else {
						editor.jqxNumberInput({disabled: false});
					}
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
				   					cellvalue = olb.quantityQC;
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
					var total = value*data.convertNumber + data.quantityEA;
					if (data.requiredQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(data.requiredQuantity)};
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.BLQuantityByEAUom, datafield: 'quantityEA', columngroup: 'ReceivedQuantity', sortable: false,  width: '8%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
								value = olb.quantityEA;
								return false;
							}
						});
					}
					if (value >= 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
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
									cellvalue = olb.quantityEA;
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
					var total = data.quantityQC*data.convertNumber + value;
					if (data.requiredQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(data.requiredQuantity)};
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.BLQuantityEATotal, datafield: 'quantity', columngroup: 'ReceivedQuantity', sortable: false,  width: '8%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
						return '<span class="align-right"></span>';
					}
				}, 
			},
			{ text: uiLabelMap.UnitPrice, hidden: hidePrice, datafield: 'unitPrice', sortable: false,  width: '8%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						if (locale && locale === 'vi' && typeof(value) === 'string'){
							value = data.unitPrice.toString().replace('.', '');
							value = value.replace(',', '.');
						}
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, 
			},
			{ text: uiLabelMap.Note, datafield: 'description', sortable: false,  width: '8%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
			},
        ];
		return columns; 
	};
	
	var getColumnGroups = function(grid){
		var columns = [
			{ text: uiLabelMap.RequiredNumber, align: 'center', name: 'RequiredQuantity'},
			{ text: uiLabelMap.ActualReceivedQuantity, align: 'center', name: 'ReceivedQuantity'}
		];
		return columns;
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'deliveryId', type: 'string'},
				{ name: 'deliveryItemSeqId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'internalName', type: 'string' },
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'statusId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'orderQuantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'currencyUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'quantityQC', type: 'number' },
				{ name: 'quantityEA', type: 'number' },
				{ name: 'requiredQuantityQC', type: 'number' },
				{ name: 'requiredQuantityEA', type: 'number' },
				{ name: 'requiredQuantity', type: 'number' },
				{ name: 'convertNumber', type: 'number' },
				{ name: 'weight', type: 'number' },
				{ name: 'receiveQuantity', type: 'number' },
				{ name: 'actualExecutedQuantity', type: 'number' },
				{ name: 'actualExecutedWeight', type: 'number' },
				{ name: 'unitPrice', type: 'number' },
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
				{ name: 'fromExpiredDate', type: 'date', other: 'Timestamp'},
				{ name: 'toExpiredDate', type: 'date', other: 'Timestamp'},
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
			columngroups: getColumnGroups(),
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
		$("#jqxGridProduct").on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantityQC'){
					var requiredQuantity = rowData.requiredQuantity;
					var total = value*rowData.convertNumber + rowData.quantityEA;
					if (value >= 0 && total <= requiredQuantity){
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
						var item = $.extend({}, rowData);
						item.quantityQC = value;
						item.quantity = total;
						listProductSelected.push(item);
					} 
				} 
				if (dataField == 'quantityEA'){
					var requiredQuantity = rowData.requiredQuantity;
					var total = rowData.quantityQC*rowData.convertNumber + value;
					if (value >= 0 && value <= requiredQuantity){
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.quantityEA = value;
						item.quantity = total;
						listProductSelected.push(item);
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
	    	if (column == 'quantityEA' || (column == 'quantityQC' && data.convertNumber > 1)) {
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
		 var tmpS = $("#jqxGridProduct").jqxGrid("source");
		tmpS._source.localdata = data;
		$("#jqxGridProduct").jqxGrid("source", tmpS);
		$("#jqxGridProduct").jqxGrid("updatebounddata");
	 }
	return {
		init : init,
		updateGridProductSource: updateGridProductSource,
	}
}());