$(function() {  
	OlbReqProduct.init();
});

var OlbReqProduct = (function() {
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
				var items = listProductMap[rowData.productId];
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
			var items = listProductMap[rowData.productId];
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
						var requiredQuantity = rowData.requiredQuantity;
						if (value >= 0 && value <= qoh && value <= requiredQuantity){
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
						var items = listProductMap[rowData.productId];
						if (items){
							var item = null;
							$.each(items, function(i){
				   				var olb = items[i];
				   				if (compareObj(olb, rowData)){
				   					olb.quantity = value;
				   					return false;
				   				}
				   			});
							listProductMap[rowData.productId] = items;
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
					}
				}
			}
		});
	}
	
	var initContent = function () {  
    	initJqxSplitter();
    };
    
	var initJqxSplitter =  function(){
		$("#splitterProduct").jqxSplitter({  width: '100%', height: 485, panels: [{ size: '55%'}, {size: '45%'}],showSplitBar: false });
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
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: '10%', minwidth: 100, editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', width: '15%', minwidth: 120, editable:false,},
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
			{ text: uiLabelMap.QOH, datafield: 'qoh', sortable: false, width: '7%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
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
					if (data.requiredQuantity < value){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber };
					}
					if (data.qoh < value){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber };
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'unitCost', hidden: hidePrice, sortable: false,  width: '15%', editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						if (locale && locale === 'vi' && typeof(value) === 'string'){
							value = data.unitCost.toString().replace('.', '');
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
				{ name: 'requirementId', type: 'string'},
				{ name: 'reqItemSeqId', type: 'string'},
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
				{ name: 'unitCost', type: 'number' },
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
				{ name: 'requirementId', type: 'string'},
				{ name: 'reqItemSeqId', type: 'string'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'amountUomTypeId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'lotId', type: 'string' },
				{ name: 'quantity', type: 'number' },
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
			{ text: uiLabelMap.QOH, dataField: 'quantityOnHandTotal', width: '12%', editable: false,
				cellsrenderer: function(row, column, value) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if (rowData.requireAmount && rowData.requireAmount == 'Y' && rowData.amountUomTypeId && rowData.amountUomTypeId == 'Y'){
						value = rowData.amountOnHandTotal;
					}
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}
			},
			{ text: uiLabelMap.ExpiredDateSum, dataField: 'expireDate', width: '20%', editable: false, cellsformat:'dd/MM/yyyy', filtertype: 'range',
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.ManufacturedDateSum, dataField: 'datetimeManufactured', width: '20%', editable: false, cellsformat:'dd/MM/yyyy', filtertype: 'range',
				cellsrenderer: function(row, column, value) {
				}
			},
			{ text: uiLabelMap.BatchSum, dataField: 'lotId', minwidth: 50, editable: false,},
			{ text: uiLabelMap.Quantity, datafield: 'quantity', sortable: false,  width: '20%', editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = gridInfo.jqxGrid('getrowdata', row);
				    if (typeof value === 'string') {
				    	value = value.replace(',', '.');
				    	value = parseFloat(value, 3, null);
				    }
				    var items = listProductMap[rowData.productId];
				    if (items.length > 0){
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
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
					var rowData = gridInfo.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						var items = listProductMap[rowData.productId];
					    if (items.length > 0){
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
					if (rowData.requireAmount && rowData.requireAmount == 'Y' && rowData.amountUomTypeId && rowData.amountUomTypeId == 'Y'){
						partQoh = rowData.amountOnHandTotal;
					}
					if (partQoh < value){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(value) + " > " + formatnumber(partQoh)};
					}
					
					
					var total = value;
					var items = listProductMap[rowData.productId];
					if (items.length > 0){
						for (var j in items){
							var y = items[j];
							if (!compareObj(y, rowData)){
								if (y.quantity > 0){
									total = total + y.quantity;
								}
							}
						}
					}
					
					var requiredQuantity = 0;
					var qoh = 0;
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId){
		   					requiredQuantity = olb.requiredQuantity;
		   					qoh = olb.qoh;
		   					return false;
		   				}
		   			});
					if (typeof (requiredQuantity) == 'string'){
						requiredQuantity = parseFloat(requiredQuantity);
					}
					if (typeof (qoh) == 'string'){
						qoh = parseFloat(qoh);
					}
					if (requiredQuantity < total){
						return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ": " + formatnumber(total) + " > " + formatnumber(requiredQuantity)};
					}
					if (qoh < total){
						return { result: false, message: uiLabelMap.CannotGreaterQOHNumber + ": " + formatnumber(total) + " > " + formatnumber(qoh)};
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
		var reqItemSeqId = originData.reqItemSeqId;
		var productId = originData.productId;
		var listItems = [];
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
					newItem["quantityOnHandTotal"] = item.quantityOnHandTotal;
					newItem["amountOnHandTotal"] = item.amountOnHandTotal;
					newItem["requireAmount"] = item.requireAmount;
					newItem["amountUomTypeId"] = item.amountUomTypeId;
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
						newItem["quantityOnHandTotal"] = item.quantityOnHandTotal;
						newItem["amountOnHandTotal"] = item.amountOnHandTotal;
						newItem["requireAmount"] = item.requireAmount;
						newItem["amountUomTypeId"] = item.amountUomTypeId;
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
						newItem["quantityOnHandTotal"] = item.quantityOnHandTotal;
						newItem["amountOnHandTotal"] = item.amountOnHandTotal;
						newItem["requireAmount"] = item.requireAmount;
						newItem["amountUomTypeId"] = item.amountUomTypeId;
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
    	if (column == 'quantity') {
			return 'background-prepare';
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
