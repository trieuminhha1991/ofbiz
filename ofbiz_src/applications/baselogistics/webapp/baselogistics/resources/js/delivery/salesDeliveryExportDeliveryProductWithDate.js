$(function() {  
	SaleDlvExp.init();
});

var SaleDlvExp = (function() {
	var rowSelectedId = null;
	var grid = $('#jqxGridProduct');
	var gridInfo = $('#jqxGridProductInfo');
	var product = null;
	var productInfo = null;
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
				var items = listProductMap[rowData.orderItemSeqId];
				if (items){
					updateGridProductInfoLocalData(items);
				} else {
					getProductInfoData(rowData, facilityId);
				}
			}
		});
		
		grid.on('rowdoubleclick', function (event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = grid.jqxGrid('getrowdata', boundIndex);
			rowSelectedId = rowData.uid;
			var items = listProductMap[rowData.orderItemSeqId];
			if (items){
				updateGridProductInfoLocalData(items);
			} else {
				getProductInfoData(rowData, facilityId);
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
						var qoh = rowData.qoh;
						var createdQuantity = rowData.createdQuantity;
						if (value >= 0 && value <= qoh && value <= createdQuantity){
							$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.orderItemSeqId == rowData.orderItemSeqId){
				   					listProductSelected.splice(i,1);
				   					return false;
				   				}
				   			});
							var item = $.extend({}, rowData);
							item.quantity = value;
							listProductSelected.push(item);
						} 
						getProductInfoData(rowData, facilityId);
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
				if (dataField == 'quantity'){
					if (value != oldvalue){
						var items = listProductMap[rowData.orderItemSeqId];
						if (items){
							var item = null;
							$.each(items, function(i){
				   				var olb = items[i];
				   				if (compareObj(olb, rowData)){
				   					olb.quantity = value;
				   					return false;
				   				}
				   			});
							listProductMap[rowData.orderItemSeqId] = items;
							var total = 0;
							for (var j in items){
								total = total + items[j].quantity; 
								if (items[j].promoQuantity > 0){
									total = total + items[j].promoQuantity;
								}
							}
							$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.orderItemSeqId == rowData.orderItemSeqId){
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
					}
				}
				if (dataField == 'promoQuantity'){
					if (value != oldvalue){
						var items = listProductMap[rowData.orderItemSeqId];
						if (items){
							var item = null;
							$.each(items, function(i){
								var olb = items[i];
								if (compareObj(olb, rowData)){
									olb.promoQuantity = value;
									return false;
								}
							});
							listProductMap[rowData.orderItemSeqId] = items;
							var total = 0;
							for (var j in items){
								total = total + items[j].quantity;
								if (items[j].promoQuantity > 0){
									total = total + items[j].promoQuantity;
								}
							}
							$.each(listProductSelected, function(i){
								var olb = listProductSelected[i];
								if (olb.orderItemSeqId == rowData.orderItemSeqId){
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
					}
				}
			}
		});
	}
	
	var initContent = function () {  
    	initJqxSplitter();
    };
    
	var initJqxSplitter =  function(){
		$("#splitterProduct").jqxSplitter({  width: '100%', height: 485, panels: [{ size: '50%'}, {size: '50%'}],showSplitBar: false });
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
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: '10%', minwidth: 100, editable: false, pinned: true, cellclassname: productGridCellclass,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', width: '15%', minwidth: 120, editable:false, cellclassname: productGridCellclass,},
			{ text: uiLabelMap.QOH, datafield: 'qoh', sortable: false, width: '8%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{editable: false, text: uiLabelMap.UnitSum, sortable: false, dataField: 'uomId', width: '8%', columntype: 'dropdownlist', filterable:false, cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.RequiredNumberSum, datafield: 'createdQuantity', sortable: false,  width: '12%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, 
			},
			{ text: uiLabelMap.ActualDeliveryQuantitySum, datafield: 'quantity', sortable: false,  width: '15%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
			   				if (olb.orderItemSeqId == rowData.orderItemSeqId){
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
				   				if (olb.orderItemSeqId == rowData.orderItemSeqId){
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
					if (data.createdQuantity < value){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber };
					}
					
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.orderItemSeqId != data.orderItemSeqId && olb.productId == data.productId){
			   					value += olb.quantity;
			   					return false;
			   				}
			   			});
				    }
					
					if (data.qoh < value){
						return { result: false, message: uiLabelMap.BLTotalExportQuantityCannotGreaterThanQOH + ". " + formatnumber(value) + " > " + formatnumber(data.qoh) };
					}
//					if (data.selectedAmount > 0){
//						if (value%data.selectedAmount > 0){
//							return { result: false, message: uiLabelMap.BLQuantityMustBeMultiplesOfWeight + ". " + uiLabelMap.Weight + " = " + formatnumber(data.selectedAmount)};
//						}
//					}
					return true;
				}, 
			},
			{ text: uiLabelMap.BLSLKM, datafield: 'promoQuantity', sortable: false,  width: '12%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.Weight, datafield: 'selectedAmount', sortable: false,  width: '13%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
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
						return '<span class="align-right">0</span>';
					}
				}, 
			},
        ];
		return columns; 
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'isPromo', type: 'string'},
				{ name: 'deliveryId', type: 'string'},
				{ name: 'orderId', type: 'string'},
				{ name: 'orderItemSeqId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'internalName', type: 'string' },
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'statusId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'currencyUomId', type: 'string' },
				{ name: 'selectedAmount', type: 'number' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createdQuantity', type: 'number' },
				{ name: 'promoQuantity', type: 'number' },
				{ name: 'initPromoQuantity', type: 'number' },
				{ name: 'actualExportedQuantity', type: 'number' },
				{ name: 'actualDeliveredQuantity', type: 'number' },
				{ name: 'qoh', type: 'number' },
				{ name: 'unitPrice', type: 'number' },
				{ name: 'hasPromo', type: 'String'},
				{ name: 'expRequired', type: 'String'},
				{ name: 'mnfRequired', type: 'String'},
				{ name: 'lotRequired', type: 'String'},
				{ name: 'description', type: 'String'},]
		return datafield;
	};
	
	var initGridProduct = function(grid){
		var configGrid = {
			datafields: getDataField(),
			columns: getColumns(grid),
			width: '100%',
			height: 485,
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
			showtoolbar:false,
			columnsresize: true,
			isSaveFormData: true,
			formData: "filterObjData",
			selectionmode: "singlerow",
			bindresize: true,
			pagesize: 15,
		};
		product = new OlbGrid(grid, null, configGrid, []);
	};
	
	var getDataFieldInfo = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
             	{ name: 'productCode', type: 'string'},
             	{ name: 'productName', type: 'string'},
				{ name: 'deliveryId', type: 'string'},
				{ name: 'orderId', type: 'string'},
				{ name: 'orderItemSeqId', type: 'string'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'amountUomTypeId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'lotId', type: 'string' },
				{ name: 'hasPromo', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'promoQuantity', type: 'number' },
				{ name: 'selectedAmount', type: 'number' },
				{ name: 'quantityOnHandTotal', type: 'number' },
				{ name: 'amountOnHandTotal', type: 'number' },
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
			{ text: uiLabelMap.QOH, dataField: 'quantityOnHandTotal', width: '9%', editable: false,
				cellsrenderer: function(row, column, value) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if (rowData.requireAmount && rowData.requireAmount == 'Y' && rowData.amountUomTypeId && rowData.amountUomTypeId == 'WEIGHT_MEASURE'){
						value = rowData.amountOnHandTotal;
					}
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}
			},
			{ text: uiLabelMap.ExpiredDateSum, dataField: 'expireDate', width: '15%', editable: false, cellsformat:'dd/MM/yyyy', filtertype: 'range',
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.ManufacturedDateSum, dataField: 'datetimeManufactured', width: '15%', editable: false, cellsformat:'dd/MM/yyyy', filtertype: 'range',
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.BatchSum, dataField: 'lotId', minwidth: 50, editable: false,},
			{ text: uiLabelMap.Weight, datafield: 'selectedAmount', sortable: false,  width: '13%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, 
			},
			{ text: uiLabelMap.Quantity, datafield: 'quantity', sortable: false,  width: '13%', editable: true, filterable: false, sortable: false, cellclassname: productGridInfoCellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
				    if (typeof value === 'string') {
				    	value = value.replace(',', '.');
				    	value = parseFloat(value, 3, null);
				    }
				    var items = listProductMap[rowData.orderItemSeqId];
				    if (items && items.length > 0){
				    	$.each(items, function(i){
			   				var olb = items[i];
			   				if (compareObj(olb, rowData)){
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
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
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
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						var items = listProductMap[rowData.orderItemSeqId];
					    if (items && items.length > 0){
					    	$.each(items, function(i){
				   				var olb = items[i];
				   				if (compareObj(olb, rowData)){
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
					var rowData = gridInfo.jqxGrid('getrowdata', cell.row);
					var partQoh = rowData.quantityOnHandTotal;
					if (rowData.requireAmount && rowData.requireAmount == 'Y' && rowData.amountUomTypeId && rowData.amountUomTypeId == 'WEIGHT_MEASURE'){
						partQoh = rowData.amountOnHandTotal;
					}
					var totalLeft = value;
					var qty = rowData.promoQuantity;
					if (qty > 0){
						totalLeft = totalLeft + qty;
					}
					
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.orderItemSeqId != rowData.orderItemSeqId && olb.productId == rowData.productId){
							var items = listProductMap[olb.orderItemSeqId];
							if (items && items.length > 0){
								$.each(items, function(i){
									var olb2 = items[i];
									if (compareObj(olb2, rowData)){
										totalLeft += olb2.quantity;
										if (olb2.promoQuantity > 0){
											totalLeft += olb2.promoQuantity;
										}
									}
								});
							}
						}
					});
					
					if (partQoh < totalLeft){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(totalLeft) + " > " + formatnumber(partQoh)};
					}
					
					var total = value;
					if (qty > 0){
						total = total + qty; 
					}
					var items = listProductMap[rowData.orderItemSeqId];
					if (items && items.length > 0){
						for (var j in items){
							var y = items[j];
							if (!compareObj(y, rowData)){
								if (y.quantity > 0){
									total = total + y.quantity;
									if (y.promoQuantity > 0){
										total = total + y.promoQuantity;
									}
								}
							}
						}
					}
					
					var createdQuantity = 0;
					var qoh = 0;
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.orderItemSeqId == rowData.orderItemSeqId){
							createdQuantity = olb.createdQuantity;
							qoh = olb.qoh;
							return false;
						}
					});
					if (typeof (createdQuantity) == 'string'){
						createdQuantity = parseFloat(createdQuantity);
					}
					if (typeof (qoh) == 'string'){
						qoh = parseFloat(qoh);
					}
					if (createdQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(createdQuantity)};
					}
					if (qoh < total){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(total) + " > " + formatnumber(qoh)};
					}
//					if (rowData.selectedAmount > 0){
//						if (total%rowData.selectedAmount > 0){
//							return { result: false, message: uiLabelMap.BLQuantityMustBeMultiplesOfWeight + ". " + uiLabelMap.Total + " = " + formatnumber(total) + " | " + uiLabelMap.Weight + " = " + formatnumber(rowData.selectedAmount)};
//						}
//					}
					// check 2 item has 2 selected amount
					var totalOther = 0;
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.orderItemSeqId != rowData.orderItemSeqId && olb.productId == rowData.productId){
							totalOther += olb.quantity;
						}
					});
					total += totalOther;
					if (qoh < total){
						return { result: false, message: "[" + rowData.productCode + "] " + uiLabelMap.BLTotalExportQuantityCannotGreaterThanQOH + ": " + formatnumber(total) + " > " + formatnumber(qoh)};
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.BLSLKM, datafield: 'promoQuantity', sortable: false,  width: '13%', editable: true, filterable: false, sortable: false, cellclassname: productGridInfoCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if (typeof value === 'string') {
						value = value.replace(',', '.');
						value = parseFloat(value, 3, null);
					}
					var items = listProductMap[rowData.orderIteSeqId];
					if (items && items.length > 0){
						$.each(items, function(i){
							var olb = items[i];
							if (compareObj(olb, rowData)){
								value = olb.promoQuantity;
								return false;
							}
						});
					}
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
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
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						var items = listProductMap[rowData.orderIteSeqId];
						if (items && items.length > 0){
							$.each(items, function(i){
								var olb = items[i];
								if (compareObj(olb, rowData)){
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
					var rowData = gridInfo.jqxGrid('getrowdata', cell.row);
					var partQoh = rowData.quantityOnHandTotal;
					if (rowData.requireAmount && rowData.requireAmount == 'Y' && rowData.amountUomTypeId && rowData.amountUomTypeId == 'WEIGHT_MEASURE'){
						partQoh = rowData.amountOnHandTotal;
					}
					var totalLeft = value;
					var totalPromo = value;
					var qty = rowData.quantity;
					if (qty > 0){
						totalLeft = totalLeft + qty;
					}
					
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.orderItemSeqId != rowData.orderItemSeqId && olb.productId == rowData.productId){
							var items = listProductMap[olb.orderItemSeqId];
							if (items && items.length > 0){
								$.each(items, function(i){
									var olb2 = items[i];
									if (compareObj(olb2, rowData)){
										totalLeft += olb2.quantity;
										if (olb2.promoQuantity > 0){
											totalLeft += olb2.promoQuantity;
										}
									}
								});
							}
						}
					});
					
					if (partQoh < totalLeft){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(totalLeft) + " > " + formatnumber(partQoh)};
					}
					
					var total = value;
					if (qty > 0){
						total = total + qty; 
					}
					var items = listProductMap[rowData.orderIteSeqId];
					if (items && items.length > 0){
						for (var j in items){
							var y = items[j];
							if (!compareObj(y, rowData)){
								if (y.quantity > 0){
									total = total + y.quantity;
									if (y.promoQuantity > 0){
										total = total + y.promoQuantity;
										totalPromo = totalPromo + y.promoQuantity;
									}
								}
							}
						}
					}
					
					var createdQuantity = 0;
					var qoh = 0;
					var promoQuantity = 0;
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.orderItemSeqId == rowData.orderItemSeqId){
							createdQuantity = olb.createdQuantity;
							promoQuantity = olb.promoQuantity;
							qoh = olb.qoh;
							return false;
						}
					});
					if (typeof (createdQuantity) == 'string'){
						createdQuantity = parseFloat(createdQuantity);
					}
					if (typeof (qoh) == 'string'){
						qoh = parseFloat(qoh);
					}
					if (createdQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(createdQuantity)};
					}
					if (qoh < total){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(total) + " > " + formatnumber(qoh)};
					}
					if (promoQuantity < totalPromo){
						return { result: false, message: uiLabelMap.CannotGreaterPromoNumber + ": " + formatnumber(totalPromo) + " > " + formatnumber(promoQuantity)};
					}
//					if (rowData.selectedAmount > 0){
//						if (total%rowData.selectedAmount > 0){
//							return { result: true, message: uiLabelMap.BLQuantityMustBeMultiplesOfWeight + ". " + uiLabelMap.Total + " = " + formatnumber(total) + " | " + uiLabelMap.Weight + " = " + formatnumber(rowData.selectedAmount)};
//						}
//					}
					var totalOther = 0;
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.orderItemSeqId != rowData.orderItemSeqId && olb.productId == rowData.productId){
							totalOther += olb.quantity;
						}
					});
					total += totalOther;
					if (qoh < total){
						return { result: false, message: "[" + rowData.productCode + "] " + uiLabelMap.BLTotalExportQuantityCannotGreaterThanQOH + ": " + formatnumber(total) + " > " + formatnumber(qoh)};
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
			height: 485,
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
			showtoolbar:false,
			columnsresize: true,
			selectionmode: "singlerow",
			bindresize: true,
			pagesize: 15,
		};
		productInfo = new OlbGrid(jqxGrid, null, configGrid, []);
	};
	
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
	
	var getProductInfoData = function (originData, facilityId){
		var listInventory = [];
		var productId = originData.productId;
		$.ajax({
	        url: "getListInventoryGroupByDate",
	        type: "POST",
	        data: {
	        	productId: productId,
	        	facilityId: facilityId,
	        },
	        success: function(res) {
	        	listInventory = res["listInventory"];
	        	if (listInventory){
	    			updateGridProductInfoData(originData, listInventory);
	    		}
	        },
	    });
	}
	
	var updateGridProductInfoLocalData = function (data){
		var tmpS = gridInfo.jqxGrid("source");
		tmpS._source.localdata = data;
		gridInfo.jqxGrid("source", tmpS);
		gridInfo.jqxGrid("updatebounddata");
	}
	var updateGridProductInfoData = function (originData, listInv){
		var expQuantity = originData.quantity;
		var hasPromo = originData.hasPromo;
		var promoQuantity = originData.promoQuantity;
		var qty = expQuantity;
		if (promoQuantity > 0){
			if (expQuantity >= promoQuantity){
				qty = qty - promoQuantity;
			} else {
				promoQuantity = 0;
			}
		}
		
		var orderItemSeqId = originData.orderItemSeqId;
		var productId = originData.productId;
		var listItems = [];
		if (listInv && originData && listInv.length > 0 && expQuantity > 0) {
			var remainQty = qty;
			for (var i in listInv) {
				var item = listInv[i];
				var qoh = listInv[i].quantityOnHandTotal;
				if (item.requireAmount && item.requireAmount == 'Y'){
					qoh = listInv[i].amountOnHandTotal;
				}
				
				$.each(listProductSelected, function(i){
					var olb = listProductSelected[i];
					if (olb.orderItemSeqId != originData.orderItemSeqId && olb.productId == originData.productId){
						var items = listProductMap[olb.orderItemSeqId];
						if (items && items.length > 0){
							$.each(items, function(i){
								var olb2 = items[i];
								if (compareObj(olb2, item)){
									qoh = qoh - olb2.quantity;
									if (olb2.promoQuantity > 0){
										qoh = qoh - olb2.promoQuantity;
									}
								}
							});
						}
					}
				});
				
				var x = qoh;
				if (remainQty <= 0) {
					var newItem = {};
					newItem["productId"] = productId;
					newItem["productCode"] = originData.productCode;
					newItem["productName"] = originData.productName;
					newItem["orderItemSeqId"] = orderItemSeqId;
					newItem["facilityId"] = facilityId;
					newItem["expireDate"] = item.expireDate;
					newItem["datetimeManufactured"] = item.datetimeManufactured;
					newItem["lotId"] = item.lotId;
					newItem["quantityOnHandTotal"] = item.quantityOnHandTotal;
					newItem["amountOnHandTotal"] = item.amountOnHandTotal;
					newItem["requireAmount"] = item.requireAmount;
					newItem["selectedAmount"] = originData.selectedAmount;
					newItem["amountUomTypeId"] = item.amountUomTypeId;
					newItem["quantity"] = 0;
					newItem["hasPromo"] = hasPromo;
					
					if (x > 0 && promoQuantity > 0){
						if (x > promoQuantity){
							newItem["promoQuantity"] = promoQuantity;
							promoQuantity = 0;
						} else {
							newItem["promoQuantity"] = x;
							promoQuantity = x - promoQuantity;
						}
					}
					listItems.push(newItem);
				} else {
					if (qoh <= 0) {
						continue;
					}
					if (qoh <= remainQty) {
						var newItem = {};
						newItem["productId"] = productId;
						newItem["productCode"] = originData.productCode;
						newItem["productName"] = originData.productName;
						newItem["orderItemSeqId"] = orderItemSeqId;
						newItem["facilityId"] = facilityId;
						newItem["expireDate"] = item.expireDate;
						newItem["datetimeManufactured"] = item.datetimeManufactured;
						newItem["lotId"] = item.lotId;
						newItem["quantity"] = qoh;
						newItem["quantityOnHandTotal"] = item.quantityOnHandTotal;
						newItem["amountOnHandTotal"] = item.amountOnHandTotal;
						newItem["requireAmount"] = item.requireAmount;
						newItem["selectedAmount"] = originData.selectedAmount;
						newItem["amountUomTypeId"] = item.amountUomTypeId;
						newItem["hasPromo"] = hasPromo;
						listItems.push(newItem);
						
						remainQty = remainQty - qoh;
						x = 0;
					} else {
						var newItem = {};
						newItem["productId"] = productId;
						newItem["productCode"] = originData.productCode;
						newItem["productName"] = originData.productName;
						newItem["orderItemSeqId"] = orderItemSeqId;
						newItem["facilityId"] = facilityId;
						newItem["expireDate"] = item.expireDate;
						newItem["datetimeManufactured"] = item.datetimeManufactured;
						newItem["lotId"] = item.lotId;
						newItem["quantity"] = remainQty;
						newItem["quantityOnHandTotal"] = item.quantityOnHandTotal;
						newItem["amountOnHandTotal"] = item.amountOnHandTotal;
						newItem["requireAmount"] = item.requireAmount;
						newItem["selectedAmount"] = originData.selectedAmount;
						newItem["amountUomTypeId"] = item.amountUomTypeId;
						newItem["hasPromo"] = hasPromo;
						x = qoh - remainQty;
						if (x > 0 && promoQuantity > 0){
							if (x > promoQuantity){
								newItem["promoQuantity"] = promoQuantity;
								promoQuantity = 0;
							} else {
								newItem["promoQuantity"] = x;
								promoQuantity = x - promoQuantity;
							}
						}
						
						listItems.push(newItem);
						remainQty = 0;
					}
				}
			}
		}
		if (orderItemSeqId){
			listProductMap[orderItemSeqId] = listItems;
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
	
	var productGridInfoCellclass = function (row, column, value, data) {
    	if (column == 'quantity' || (column == 'promoQuantity' && data.hasPromo === 'Y')) {
			return 'background-prepare';
    	} 
    	
	}
	var productGridCellclass = function (row, column, value, data) {
		if (column == 'quantity') {
			if (data.qoh <= 0){
	    		return 'background-warning';
	    	} else {
	    		return 'background-prepare';
	    	}
		}
		if (data.hasPromo === 'Y'){
			if (data.qoh <= 0){
	    		return 'background-warning';
	    	} else {
	    		return 'background-promo';
	    	}
    	} else {
    		if (data.qoh <= 0){
        		return 'background-warning';
        	}
    	}
	}
	
	var compareObj = function (x, y){
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
		return false;
	}
	
	return {
		init : init,
		updateGridProductSource: updateGridProductSource,
	}
}());
