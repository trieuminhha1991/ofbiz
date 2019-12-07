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
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: '8%', editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', columngroup: 'qoh', sortable: false,  width: '5%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if (rowData.requireAmount && 'Y' == rowData.requireAmount) {
						if (rowData.weightUomId) {
							value = rowData.weightUomId;
						}
					} else { 
						if (rowData.quantityUomId) {
							value = rowData.quantityUomId;
						}
					}
					if (value) {
						var desc = getUomDesc(value);
						return '<span class="align-right">' + desc +'</span>';
					} 
					return value;
				}, 
			},
			{ text: uiLabelMap.Quantity, datafield: 'qoh', columngroup: 'qoh', sortable: false,  width: '6%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{editable: false, text: uiLabelMap.Unit, columngroup: 'require', sortable: false, dataField: 'uomId', width: '5%', columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.BLPackingForm, columngroup: 'require', datafield: 'convertNumber', sortable: false,  width: '5%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.BLQuantityByQCUom, columngroup: 'require', datafield: 'createdQuantityQC', sortable: false,  width: '6%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						var rowData = grid.jqxGrid('getrowdata', row);
						if ('Y' == rowData.requireAmount) {
							value = rowData.createdQuantity/rowData.selectedAmount;
						} else if (rowData.createdQuantity > 0 && rowData.convertNumber > 0){
							value = rowData.createdQuantity/rowData.convertNumber;
						}
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					}
					return '<span class="align-right"></span>';
				}, 
			},
			{ text: uiLabelMap.BLQuantityEATotal, columngroup: 'require', datafield: 'createdQuantity', sortable: false,  width: '6%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					value = rowData.createdQuantity;
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.BLPromoQty, columngroup: 'require', datafield: 'initPromoQuantity', sortable: false,  width: '6%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.Quantity, columngroup: 'quantity', datafield: 'quantity', sortable: false,  width: '8%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
			   				if (olb.orderItemSeqId == rowData.orderItemSeqId ){
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
					if (rowData.quantity < 0){
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
				   				if (olb.orderItemSeqId == rowData.orderItemSeqId ){
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
					if (data.promoQuantity > 0){
						total = total + data.promoQuantity;
					}
					if (data.createdQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(data.createdQuantity)};
					}
					if (data.qoh < total){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(total) + " > " + formatnumber(data.qoh)};
					}
					if (data.initQuantity < value){
						return { result: false, message: uiLabelMap.CannotGreaterCreatedNotPromoNumber + ": " + formatnumber(value) + " > " + formatnumber(data.initQuantity)};
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.BLPromoQty, columngroup: 'quantity', datafield: 'promoQuantity', sortable: false,  width: '8%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
							if (olb.orderItemSeqId == rowData.orderItemSeqId ){
								value = olb.promoQuantity;
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
					if ('Y' != rowData.hasPromo){
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
								if (olb.orderItemSeqId == rowData.orderItemSeqId ){
									cellvalue = olb.promoQuantity;
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
					if (data.quantity > 0){
						total = total + data.quantity;
					}
					if (data.createdQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(data.createdQuantity)};
					}
					if (data.qoh < total){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(total) + " > " + formatnumber(data.qoh)};
					}
					if (data.initPromoQuantity < value){
						return { result: false, message: uiLabelMap.CannotGreaterPromoNumber + ": " + formatnumber(value) + " > " + formatnumber(data.initPromoQuantity)};
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.Total, datafield: 'quantityTotal', sortable: false,  width: '8%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					var total = 0;
					if (data.quantity > 0) {
						total = total + data.quantity;
					}
					if (data.promoQuantity > 0) {
						total = total + data.promoQuantity;
					}
					return '<span class="align-right">' + formatnumber(total) +'</span>';
				}, 
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'unitPrice', sortable: false,  width: '8%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						var data = grid.jqxGrid('getrowdata', row);	
						if (locale && locale === 'vi' && typeof(value) === 'string'){
							value = data.unitPrice.toString().replace('.', '');
							value = value.replace(',', '.');
						}
						if (data.onlyPromo && data.onlyPromo == "Y") {
							return '<span class="align-right"><strike>' + formatnumber(value) +'</strike></span>';
						}
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
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
			{ text: uiLabelMap.QOH, align: 'center', name: 'qoh'},
			{ text: uiLabelMap.RequiredNumber, align: 'center', name: 'require'},
			{ text: uiLabelMap.ActualDeliveryQuantitySum, align: 'center', name: 'quantity'}
		];
		return columns;
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'deliveryId', type: 'string'},
				{ name: 'deliveryItemSeqId', type: 'string'},
				{ name: 'orderId', type: 'string'},
				{ name: 'orderItemSeqId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'isPromo', type: 'string' },
				{ name: 'hasPromo', type: 'string' },
				{ name: 'onlyPromo', type: 'string' },
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'statusId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'orderQuantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'currencyUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createdQuantity', type: 'number' },
				{ name: 'createdQuantityQC', type: 'number' },
				{ name: 'promoQuantity', type: 'number' },
				{ name: 'initPromoQuantity', type: 'number' },
				{ name: 'initQuantity', type: 'number' },
				{ name: 'selectedAmount', type: 'number' },
				{ name: 'qoh', type: 'number' },
				{ name: 'convertNumber', type: 'number' },
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
					if (rowData.promoQuantity > 0){
						total = total + rowData.promoQuantity; 
					}
					if (total >= 0 && total <= qoh && total <= createdQuantity){
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.orderItemSeqId == rowData.orderItemSeqId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
					
						var item = $.extend({}, rowData);
						item.quantity = value;
						listProductSelected.push(item);
					} 
				} 
				if (dataField == 'promoQuantity'){
					if (value > rowData.initPromoQuantity) return false;
					var qoh = rowData.qoh;
					var createdQuantity = rowData.createdQuantity;
					var total = value;
					if (rowData.quantity > 0){
						total = total + rowData.quantity; 
					}
					if (total >= 0 && total <= qoh && total <= createdQuantity){
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.orderItemSeqId == rowData.orderItemSeqId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						
						var item = $.extend({}, rowData);
						item.promoQuantity = value;
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
	    			var index = $('#jqxGridProduct').jqxGrid('getrowboundindexbyid',rows[x].uid);
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
	    	if ((column == 'quantity') || (column == 'promoQuantity' && data.hasPromo == 'Y')) {
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