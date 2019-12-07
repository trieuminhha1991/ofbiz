$(function() {  
	PurDlvProduct.init();
});

var PurDlvProduct = (function() {
	var rowSelectedId = null;
	var grid = $('#jqxGridProduct');
	var gridInfo = $('#jqxGridProductInfo');
	var product = null;
	var productInfo = null;
	var rowSelectedId = null;
	var listItems = [];
	var productSelected = null;
	var init = function() {
		initInput();
		initElementComplex();
		initEvents();
	};
	
	var initInput = function() { 
		
	}
	var initElementComplex = function() {
		initGridProduct(grid);
		initGridProductInfo(gridInfo);
		initContent();
		if (listProductSelected.length > 0){
			
			updateGridProductLocalData(listProductSelected);
		}
		updateGridProductInfoData([]);
	}
	
	var initEvents = function() { 
		
		grid.on('rowselect', function (event){
			var args = event.args;
			var rowData = args.row;
			rowSelectedId = rowData.uid;
			if (rowData){
				productSelected = $.extend({}, rowData);
				var items = listProductMap[rowData.productId];
				if (items){
					updateGridProductInfoLocalData(items);
				} else {
					gridInfo.jqxGrid('clear');
				}
				var rowInfs = gridInfo.jqxGrid('getrows');
				if (rowInfs.length <= 0){
					addRow();
				}
			}
		});
		
		grid.on('rowdoubleclick', function (event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = grid.jqxGrid('getrowdata', boundIndex);
			rowSelectedId = rowData.uid;
			var items = listProductMap[rowData.productId];
			if (items){
				updateGridProductInfoLocalData(items);
			}
			setTimeout(function(){
				grid.jqxGrid('begincelledit', rowSelectedId, "quantity");
			}, 100);
		});
		
		grid.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantity'){
					if (value != oldvalue){
						var requiredQuantity = rowData.requiredQuantity;
						if (value >= 0 && value <= requiredQuantity){
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
						
						var rowInfs = gridInfo.jqxGrid('getrows');
						if (rowInfs.length >= 0){
							var items = listProductMap[rowData.productId];
							if (items && items.length > 0){
								var total = 0;
								for (var i in items){
									total = total + items[i].quantity;
								}
								if (total > value){
									setTimeout(function(){
										gridInfo.jqxGrid('begincelledit', 0, "quantity");
									}, 50);
								}
							}
						}
					}
				}
			}
		});
		
		gridInfo.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				var items = listProductMap[rowData.productId];
				if (dataField == 'quantity'){
					if (value != oldvalue){
						if (items && items.length > 0){
							var check = false;
							var item = null;
							var quantityTotal = 0;
							$.each(items, function(i){
				   				var olb = items[i];
				   				if (compareObj(olb, rowData)){
				   					quantityTotal = quantityTotal + olb.quantity;
				   					check = true;
				   				}
				   			});
							if (typeof(oldvalue) == 'number'){
								quantityTotal = quantityTotal - oldvalue;
							} 
							if (typeof(oldvalue) == 'string'){
								quantityTotal = quantityTotal - parseFloat(oldvalue);
							}
							quantityTotal = quantityTotal + value;
							
							if (value <= 0 && quantityTotal <= 0){
								$.each(items, function(i){
					   				var olb = items[i];
					   				if (compareObj(olb, rowData)){
					   					items.splice(i,1);
					   					return;
					   				}
								});
								check = true;
							} else {
								$.each(items, function(i){
					   				var olb = items[i];
					   				if (compareObj(olb, rowData)){
					   					olb.quantity = quantityTotal;
					   					return;
					   				}
								});
							}
							
							if (!check){
								var newItem = {};
								if (productSelected){
									newItem["productCode"] = productSelected.productCode;
									newItem["productName"] = productSelected.productName;
								}
								newItem["transferId"] = rowData.transferId;
								newItem["transferItemSeqId"] = rowData.transferItemSeqId;
								newItem["expireDate"] = rowData.expireDate;
								newItem["datetimeManufactured"] = rowData.datetimeManufactured;
								newItem["lotId"] = rowData.lotId;
								newItem["quantity"] = value;
								items.push(newItem);
							}
							listProductMap[rowData.productId] = items;
						} else {
							if (value > 0){
								items = [];
								var newItem = {};
								newItem["productId"] = rowData.productId;
								if (productSelected){
									newItem["productCode"] = productSelected.productCode;
									newItem["productName"] = productSelected.productName;
								}
								newItem["transferItemSeqId"] = rowData.transferItemSeqId;
								newItem["transferId"] = rowData.transferId;
								newItem["expireDate"] = rowData.expireDate;
								newItem["datetimeManufactured"] = rowData.datetimeManufactured;
								newItem["lotId"] = rowData.lotId;
								newItem["quantity"] = value;
								items.push(newItem);
								listProductMap[rowData.productId] = items;
							}
						}
						var total = 0;
						for (var j in items){
							total = total + items[j].quantity; 
						}
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId){
			   					listProductSelected[i].quantity = total;
			   					if (rowSelectedId != null){
			   						var data1 = grid.jqxGrid('getrowdatabyid', rowSelectedId);
			   						data1.quantity = total;
				   					grid.jqxGrid('updaterow', rowSelectedId, data1);
			   					}
			   					return false;
			   				}
			   			});
					}
				} else {
					if (rowData.quantity > 0){
						var items = listProductMap[rowData.productId];
						if (items && items.length > 0){
							var x = $.extend({}, rowData);
							x[dataField] = oldvalue;
							$.each(items, function(i){
				   				var olb = items[i];
				   				if (compareObj(olb, x)){
				   					items.splice(i,1);
				   					return;
				   				}
							});
							var y = $.extend({}, rowData);
							y[dataField] = value;
							
							items.push(y);
							listProductMap[rowData.productId] = groupAndSum(items);
						}
					}
				}
			}
		});
	}
	
	var initContent = function () {  
    	initJqxSplitter();
    };
    
	var initJqxSplitter =  function(){
		$("#splitterProduct").jqxSplitter({  width: '100%', height: 522, panels: [{ size: '55%'}, {size: '45%'}], showSplitBar: false});
	};
	
	var getColumns = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: '12%', minwidth: 100, editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', width: '25%', minwidth: 120, editable:false,},
			{editable: false, text: uiLabelMap.Unit, sortable: false, dataField: 'uomId', width: '10%', columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.RequiredNumberSum, datafield: 'requiredQuantity', sortable: false,  width: '15%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, 
			},
			{ text: uiLabelMap.ActualReceivedQuantitySum, datafield: 'quantity', sortable: false,  width: '15%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
					var rowData = grid.jqxGrid('getrowdata', cell.row);
					if (rowData.requiredQuantity < value){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber };
					}
					if (rowData.qoh < value){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber };
					}
					
					var total = 0;
					var items = listProductMap[rowData.productId];
				    if (items && items.length > 0){
				    	$.each(items, function(i){
			   				var olb = items[i];
			   				if (!compareObj(olb, rowData)){
								if (olb.quantity > 0){
									total = total + olb.quantity;
								}
							}
			   			});
				    }
   					if (value < total){
   						return { result: false, message: uiLabelMap.BLCannotLessThanTotalQuantityDetail + ": " + formatnumber(value) + " < " + formatnumber(total)};
   					}
					
					return true;
				}, 
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'unitPrice', sortable: false,  width: '15%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.Note, datafield: 'description', sortable: false,  width: '20%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
			},
        ];
		return columns; 
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'deliveryId', type: 'string'},
				{ name: 'transferItemSeqId', type: 'string'},
				{ name: 'transferId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'internalName', type: 'string' },
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'statusId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'currencyUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'requiredQuantity', type: 'number' },
				{ name: 'weight', type: 'number' },
				{ name: 'receiveQuantity', type: 'number' },
				{ name: 'actualExecutedQuantity', type: 'number' },
				{ name: 'actualExecutedWeight', type: 'number' },
				{ name: 'qoh', type: 'number' },
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
			width: '100%',
			height: 522,
			autoheight: false,
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
			showtoolbar:true,
			rendertoolbar: rendertoolbarProduct,
			columnsresize: true,
			isSaveFormData: true,
			formData: "filterObjData",
			selectionmode: "singlerow",
			bindresize: true,
			pagesize: 15,
		};
		product = new OlbGrid(grid, null, configGrid, []);
	};
	
	var rendertoolbarProduct = function (toolbar){
		toolbar.html("");
		var id = "jqxGridProduct";
		var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ListProduct + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
		toolbar.append(jqxheader);
     	var container = $('#toolbarButtonContainer' + id);
        var maincontainer = $("#toolbarcontainer" + id);
	}
	
	var getDataFieldInfo = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
             	{ name: 'productCode', type: 'string'},
             	{ name: 'productName', type: 'string'},
				{ name: 'deliveryId', type: 'string'},
				{ name: 'transferItemSeqId', type: 'string'},
				{ name: 'transferId', type: 'string'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'inventoryId', type: 'string' },
				{ name: 'lotId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				]
		return datafield;
	};
	
	var getColumnsInfo = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ManufacturedDateSum, dataField: 'datetimeManufactured', width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					var items = listProductMap[rowData.productId];
				    if (items && items.length > 0){
				    	$.each(items, function(i){
			   				var olb = items[i];
			   				if (compareObj(olb, rowData)){
			   					value = olb.datetimeManufactured;
			   					return false;
			   				}
			   			});
				    }
				    
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = gridInfo.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		var now = new Date();
			 		if (value) {
				        if (value > now) {
				            return { result: false, message: uiLabelMap.ManufactureDateMustBeBeforeNow};
				        }
				        var data = gridInfo.jqxGrid('getrowdata', cell.row);
				        if (data.expireDate){
				        	var exp = new Date(data.expireDate);
				        	if (exp < new Date(value)){
					        	return { result: false, message: uiLabelMap.ManufactureDateMustBeBeforeExpireDate};
					        }
				        }
			        } 
			        return true;
				 },
			},
			{ text: uiLabelMap.ExpiredDateSum, dataField: 'expireDate', width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					var items = listProductMap[rowData.productId];
				    if (items && items.length > 0){
				    	$.each(items, function(i){
			   				var olb = items[i];
			   				if (compareObj(olb, rowData)){
			   					value = olb.expireDate;
			   					return false;
			   				}
			   			});
				    }
				    
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = gridInfo.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		if (value) {
				        var data = gridInfo.jqxGrid('getrowdata', cell.row);
				        if (data.datetimeManufactured){
				        	var mnf = new Date(data.datetimeManufactured);
				        	if (mnf > new Date(value)){
					        	return { result: false, message: uiLabelMap.ExpireDateMustBeBeforeManufactureDate};
					        }
				        }
			        } 
			        return true;
				 },
			},
			{ text: uiLabelMap.BatchSum, dataField: 'lotId', minwidth: 80, editable: true, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value){
					 var rowData = gridInfo.jqxGrid('getrowdata', row);
					 var items = listProductMap[rowData.productId];
					 if (items && items.length > 0){
						 $.each(items, function(i){
			   					var olb = items[i];
			   					if (compareObj(olb, rowData)){
			   						value = olb.lotId;
			   						return false;
			   					}
						 });
					 }
					 if (value){
						 return '<span class="align-right">' + value + '</span>';
					 } else {
						 return '<span class="align-right"></span>';
					 }
				 },
				 validation: function (cell, value) {
	               	 if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
	               		 return { result: false, message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter};
	               	 }
	               	 return true;
				 },
			},
			{ text: uiLabelMap.Quantity, datafield: 'quantity', sortable: false,  width: '20%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
				    if (typeof value === 'string') {
				    	value = value.replace(',', '.');
				    	value = parseFloat(value, 3, null);
				    }
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if (cellvalue) {
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
				}, validation: function (cell, value) {
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					var rowData = gridInfo.jqxGrid('getrowdata', cell.row);
					var oldValue = rowData.quantity;
					
					var total = 0;
					
					var items = listProductMap[rowData.productId];
				    if (items && items.length > 0){
				    	$.each(items, function(i){
			   				var olb = items[i];
							if (olb.quantity > 0){
								total = total + olb.quantity;
							}
			   			});
				    }
				    
					if (typeof(oldValue) == 'number'){
						total = total - oldValue;
					} 
					if (typeof(oldValue) == 'string'){
						total = total - parseFloat(oldValue);
					}
					total = total + value;
					
					var requiredQuantity = 0;
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId){
		   					requiredQuantity = olb.requiredQuantity;
		   					return false;
		   				}
		   			});
					if (typeof (requiredQuantity) == 'string'){
						requiredQuantity = parseFloat(requiredQuantity);
					}
					if (requiredQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(requiredQuantity)};
					}
					return true;
				}, 
			},
			];
		return columns;
	}
	
	var initGridProductInfo = function(jqxGrid){
		var configGrid = {
			datafields: getDataFieldInfo(),
			columns: getColumnsInfo(grid),
			width: '100%',
			height: 522,
			autoheight: false,
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
			virtualmode:false,
			showdefaultloadelement:true,
			autoshowloadelement:true,
			showtoolbar:true,
			rendertoolbar: rendertoolbarInfo,
			columnsresize: true,
			selectionmode: "singlerow",
			bindresize: true,
			pagesize: 15,
			rowsheight: 20,
		};
		productInfo = new OlbGrid(jqxGrid, null, configGrid, []);
	};
	
	var rendertoolbarInfo = function (toolbar){
		var me = this;
        var container = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.DetailInfo + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
        toolbar.append(container);
        container.append('<div class="margin-top10">');
        container.append('<a href="javascript:PurDlvProduct.deleteRow()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="red fa fa-times"></i></a>');
        container.append('<a href="javascript:PurDlvProduct.addRow()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="fa fa-plus"></i></a>');
        container.append('<a href="javascript:PurDlvProduct.addMultiRow()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="fa fa-plus-square"></i></a>');
        container.append('</div>');
	}
	
	var updateGridProductSource = function (source){
		grid.jqxGrid("source")._source.url = source;
		grid.jqxGrid("updatebounddata");
	}
	
	var updateGridProductLocalData = function (data){
		var tmpS = grid.jqxGrid("source");
		tmpS._source.localdata = data;
		grid.jqxGrid("source", tmpS);
		grid.jqxGrid("updatebounddata");
	}
	
	var updateGridProductInfoLocalData = function (data){
		var tmpS = gridInfo.jqxGrid("source");
		tmpS._source.localdata = data;
		gridInfo.jqxGrid("source", tmpS);
		gridInfo.jqxGrid("updatebounddata");
	}
	var updateGridProductInfoData = function (originData, listInv){
		var expQuantity = originData.quantity;
		var transferItemSeqId = originData.transferItemSeqId;
		var productId = originData.productId;
		listItems = [];
		if (listInv && originData && listInv.length > 0 && expQuantity > 0) {
			var remainQty = expQuantity;
			for (var i in listInv) {
				var item = listInv[i];
				if (remainQty <= 0) {
					var newItem = {};
					newItem["productId"] = productId;
					newItem["productCode"] = item.productCode;
					newItem["productName"] = item.productName;
					newItem["facilityId"] = facilityId;
					newItem["expireDate"] = item.expireDate;
					newItem["datetimeManufactured"] = item.datetimeManufactured;
					newItem["lotId"] = item.lotId;
					newItem["quantity"] = 0;
					listItems.push(newItem);
				} else {
					var qoh = listInv[i].quantityOnHandTotal;
					if (item.requireAmount && item.requireAmount == 'Y'){
						qoh = listInv[i].amountOnHandTotal;
					}
					if (qoh < 1) {
						continue;
					}
					if (qoh <= remainQty) {
						var newItem = {};
						newItem["productId"] = productId;
						newItem["productCode"] = item.productCode;
						newItem["productName"] = item.productName;
						newItem["facilityId"] = facilityId;
						newItem["expireDate"] = item.expireDate;
						newItem["datetimeManufactured"] = item.datetimeManufactured;
						newItem["lotId"] = item.lotId;
						newItem["quantity"] = qoh;
						listItems.push(newItem);
						remainQty = remainQty - qoh;
					} else {
						var newItem = {};
						newItem["productId"] = productId;
						newItem["productCode"] = item.productCode;
						newItem["productName"] = item.productName;
						newItem["facilityId"] = facilityId;
						newItem["expireDate"] = item.expireDate;
						newItem["datetimeManufactured"] = item.datetimeManufactured;
						newItem["lotId"] = item.lotId;
						newItem["quantity"] = remainQty;
						listItems.push(newItem);
						
						remainQty = 0;
					}
				}
			}
		}
		if (productId){
			listProductMap[productId] = listItems;
			var tmpS = gridInfo.jqxGrid("source");
			tmpS._source.localdata = listItems;
			gridInfo.jqxGrid("source", tmpS);
			gridInfo.jqxGrid("updatebounddata");
		}
	}
	
	var updateGridProductInfoUrl = function (productId, facilityId){
		var tmpS = gridInfo.jqxGrid("source");
		var newUrl = "jqxGeneralServicer?sname=jqGetListInventoryGroupByDate&facilityId="+facilityId+"&productId="+productId;
		tmpS._source.url = newUrl;
		gridInfo.jqxGrid("source", tmpS);
		gridInfo.jqxGrid("updatebounddata");
	}
	
	var productGridCellclass = function (row, column, value, data) {
    	if (column == 'quantity' || column == 'expireDate' || column == 'datetimeManufactured' || column == 'lotId') {
			return 'background-prepare';
    	}
	}
	
	var generaterow = function (){
		  var row = {};
		  if (productSelected){
			  row['productId'] = productSelected.productId;
			  row['transferItemSeqId'] = productSelected.transferItemSeqId;
			  row['transferId'] = productSelected.transferId;
			  row['expireDate'] = null;
			  row['datetimeManufactured'] = null;
			  row['lotId'] = null;
			  row['quantity'] = null;
	          return row;
		  }
		  return;
	}
	
	var addRow = function (){
		if (!productSelected) {
			jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
			return;
		}
		var datarow = generaterow();
        var commit = gridInfo.jqxGrid('addrow', null, datarow);
	}
	
	var addMultiRow = function (){
		if (!productSelected) {
			jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
			return;
		}
		for (var i = 0; i < 15; i++) {
			addRow();
		}
	}
	
	var deleteRow = function (){
		var rowindex = gridInfo.jqxGrid('getselectedrowindex');
		var data = gridInfo.jqxGrid('getrowdata', rowindex);
		if (data){
			if (data.quantity > 0){
				if (productSelected){
					var items = listProductMap[productSelected.productId];
					if (items){
						$.each(items, function(i){
			   				var olb = items[i];
			   				if (compareObj(olb, data)){
			   					olb.quantity = olb.quantity - data.quantity;
			   					if (olb.quantity <= 0){
			   						items.splice(i,1);
			   					}
			   					return false;
			   				}
			   			});
						if (items.length > 0){
							listProductMap[productSelected.productId] = items;
						} else {
							delete listProductMap[productSelected.productId];
						}
					}
				}
			}
			var rowid = data.uid;
			gridInfo.jqxGrid('deleterow', rowid);
		}
	}
	
	var compareObj = function (x, y){
		if (x && y){
			if (x.expireDate != null){
				if (typeof (x.expireDate) == 'string'){
					x.expireDate = parseFloat(x.expireDate);
				}
				if (typeof (x.expireDate.getMonth) == 'function'){
					x.expireDate = x.expireDate.getTime();
				}
				if (typeof (x.expireDate) != 'number'){
					return false;
				}
			}
			if (y.expireDate){
				if (typeof (y.expireDate) == 'string'){
					y.expireDate = parseFloat(y.expireDate);
				}
				if (typeof (y.expireDate.getMonth) == 'function'){
					y.expireDate = y.expireDate.getTime();
				}
				if (typeof (y.expireDate) != 'number'){
					return false;
				}
			}
			if (x.datetimeManufactured != null){
				if (typeof (x.datetimeManufactured) == 'string'){
					x.datetimeManufactured = parseFloat(x.datetimeManufactured);
				}
				if (typeof (x.datetimeManufactured.getMonth) == 'function'){
					x.datetimeManufactured = x.datetimeManufactured.getTime();
				}
				if (typeof (x.datetimeManufactured) != 'number'){
					return false;
				}
			}
			if (y.datetimeManufactured){
				if (typeof (y.datetimeManufactured) == 'string'){
					y.datetimeManufactured = parseFloat(y.datetimeManufactured);
				}
				if (typeof (y.datetimeManufactured.getMonth) == 'function'){
					y.datetimeManufactured = y.datetimeManufactured.getTime();
				}
				if (typeof (y.datetimeManufactured) != 'number'){
					return false;
				}
			}
			
			if (x.expireDate == y.expireDate && x.datetimeManufactured == y.datetimeManufactured && x.lotId == y.lotId){
				return true;
			}
		}
		return false;
	}
	var groupAndSum = function(list){
		var listDist = [];
		var listReturns = [];
		if (list && list.length > 0){
			for (var i in list){
				var item = list[i];
				if (listDist.length <= 0){
					listDist.push(item);
				} else {
					var check = false;
					for (var j in listDist){
						var tmp = listDist[j];
						if (compareObj(item, tmp)){
							check = true;
						}
					}
					if (!check){
						listDist.push(item);
					}
				}
			}
			if (listDist.length > 0){
				for (var i in listDist){
					var item = listDist[i];
					var quantity = 0;
					for (var j in list) {
						var tmp = list[j];
						if (compareObj(item, tmp)){
							quantity = quantity + tmp.quantity;
						}
					}
					item.quantity = quantity;
					listReturns.push(item);
				}
			}
		}
		return listReturns;
	}
	return {
		init : init,
		updateGridProductSource: updateGridProductSource,
		addRow:addRow,
		addMultiRow:addMultiRow,
		deleteRow:deleteRow
	}
}());
