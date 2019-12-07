$(function () {
	dlvEditObj.init();
});
var dlvEditObj = (function () {
	var popupMain = $("#editPopupWindow");
	var popupAddProduct = $("#editAddProductWindow");
	var gridEdit = $("#editGridProduct");
	var gridAddProduct = $("#editAddProductGrid");
	var listProductSelected = [];
	var listProductAdd = [];
	var validatorVAL;
	
	var init = function (){
		initElement();
        initValidator();
		initEvent();
	};
	
	var initElement = function (){
		popupMain.jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1200, height: 700, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme
		});
		popupAddProduct.jqxWindow({
			maxWidth: 1300, minWidth: 500, width: 1200, height: 590, minHeight: 100, maxHeight: 700, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#editAddProductCancel"), modalOpacity: 0.7, theme:theme           
		});
        $("#conversionFactorPopup").jqxNumberInput({ width: '96%',  max : 999999999999, digits: 12, decimalDigits: 2, spinButtons: true, min: 0});
    };
	
	var initEvent = function (){
		popupAddProduct.on("close", function (){
			listProductAdd = [];
		});
		
		$("#editSave").click(function () {
            if(!getValidator().validate()) return false;
			bootbox.dialog(uiLabelMap.AreYouSureUpdate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function () {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function () {
						Loading.show("loadingMacro");
						setTimeout(function () {
							saveEditDelivery();
							Loading.hide("loadingMacro");	
						}, 300);
					}
				}]);
			listProductToAdd = [];
		});
		
		gridAddProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'newQuantity'){
					if (value != oldvalue){
						$.each(listProductAdd, function(i){
			   				var olb = listProductAdd[i];
			   				if (olb.fromOrderItemSeqId && olb.fromOrderItemSeqId == rowData.fromOrderItemSeqId){
			   					listProductAdd.splice(i,1);
			   					return false;
			   				} else if (olb.fromTransferItemSeqId && olb.fromTransferItemSeqId == rowData.fromTransferItemSeqId){
			   					listProductAdd.splice(i,1);
			   					return false;
			   				}
			   			});
						$.each(listProductToAdd, function(i){
							var olb = listProductToAdd[i];
							if (olb.fromOrderItemSeqId && olb.fromOrderItemSeqId == rowData.fromOrderItemSeqId){
								listProductToAdd.splice(i,1);
								return false;
							} else if (olb.fromTransferItemSeqId && olb.fromTransferItemSeqId == rowData.fromTransferItemSeqId){
								listProductToAdd.splice(i,1);
								return false;
							}
						});
						
						var item = $.extend({}, rowData);
						item.newQuantity = value;
						if (value > 0){
							listProductAdd.push(item);
						}
						listProductToAdd.push(item);
					} 
				} 
			}
		});
		gridEdit.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'newQuantity'){
					if (value != oldvalue){
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.fromOrderItemSeqId && olb.fromOrderItemSeqId == rowData.fromOrderItemSeqId ){
								listProductSelected.splice(i,1);
								return false;
							} else if (olb.fromTransferItemSeqId && olb.fromTransferItemSeqId == rowData.fromTransferItemSeqId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						
						var item = $.extend({}, rowData);
						item.newQuantity = value;
						listProductSelected.push(item);
					} 
				} 
			}
		});
		
		$("#editAddProductSave").click(function () {
			if (listProductAdd.length <= 0) {
				jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
				return false;
			}
			var newDataList = [];
			for (var x in listProductAdd){
				var data1 = listProductAdd[x];
				var obj = $.extend({}, data1);
				$.each(listProductToAdd, function(i){
					var olb = listProductToAdd[i];
					if (olb.fromOrderItemSeqId && olb.fromOrderItemSeqId == obj.fromOrderItemSeqId){
						listProductToAdd.splice(i,1);
						return false;
					} else if (olb.fromTransferItemSeqId && olb.fromTransferItemSeqId == obj.fromTransferItemSeqId){
						listProductToAdd.splice(i,1);
						return false;
					}
				});
				var check = false;
				for (var y in listProductSelected){
					var data2 = listProductSelected[y];
					if (data1.productId == data2.productId){
						check = true;
						data2.newQuantity += data1.newQuantity;
					}
				}
				if (!check){
					listProductSelected.push(obj);
				}
			}
			loadEditGrid(listProductSelected);
			popupAddProduct.jqxWindow('close');
		});
	}
	
	var saveEditDelivery = function (){
		var listDeliveryItemTmps = [];
		for (var i in listProductSelected) {
			var data = listProductSelected[i];
			var item = {};
			item.quantity = data.newQuantity;
			item.deliveryId = glDeliveryId;
			if (data.deliveryItemSeqId != undefined){
				item.deliveryItemSeqId = data.deliveryItemSeqId;
			}
			item.fromOrderId = data.fromOrderId;
			item.fromOrderItemSeqId = data.fromOrderItemSeqId;
			item.fromTransferId = data.fromTransferId;
			item.fromTransferItemSeqId = data.fromTransferItemSeqId;
			listDeliveryItemTmps.push(item);
		}
		
		listDeliveryItemTmps = JSON.stringify(listDeliveryItemTmps);
		$.ajax({
			type: "POST",
			url: "updateDeliveryItemInfos",
			data: {
				listDeliveryItems: listDeliveryItemTmps,
			},
			async: false,
			success: function (res) {
				if (!res._ERROR_MESSAGE_) {
					window.location.reload();
				} else {
					jOlbUtil.alert.error(uiLabelMap.UpdateError + ": " + res._ERROR_MESSAGE_);
					return false;
				}
				popupMain.jqxWindow('close');
			}
		});

        $.ajax({
            type: "POST",
            url: "updateConversionFactorForDelivery",
            data: {
                deliveryId: glDeliveryId,
                conversionFactor: $("#conversionFactorPopup").val()
            },
            async: false,
            success: function (res) {
            }
        });
	};
	
	var editDelivery = function (deliveryId){
		$.ajax({
			url: "loadDeliveryItemToEdit",
			type: "POST",
			data: {
				deliveryId: deliveryId,
			},
			async: false,
			success: function (res) {
				var listOrderItemTmps = res.listDeliveryItems;
				for (var x in listOrderItemTmps) {
					listOrderItemTmps[x].newQuantity = listOrderItemTmps[x].createdQuantity;
					var obj = $.extend({}, listOrderItemTmps[x]);
					listProductSelected.push(obj);
				}
                if(globalVar && globalVar.currencyUomId !== 'VND') {
                    $('#conversionFactorPopup').jqxNumberInput('val', conversionFactor.replace(',', '.'));
                    $('#conversionFactorDiv').removeClass('hide');
                }
                loadEditGrid(listOrderItemTmps);
				popupMain.jqxWindow('open');
			}
		});
	}
	
	function loadEditGrid(valueDataSoure) {
		var sourceOrderItem =
		{
			datafields:
			[
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'fromOrderId', type: 'string' },
				{ name: 'fromOrderItemSeqId', type: 'string' },
				{ name: 'fromTransferId', type: 'string' },
				{ name: 'fromTransferItemSeqId', type: 'string' },
				{ name: 'isPromo', type: 'string' },
				{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryItemSeqId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'baseQuantityUomId', type: 'string' },
				{ name: 'baseWeightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createdQuantity', type: 'number' },
				{ name: 'newQuantity', type: 'number' },
				{ name: 'convertNumber', type: 'number' }
			],
			localdata: valueDataSoure,
			datatype: "array"
		};
		var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
		
		gridEdit.jqxGrid({
			source: dataAdapterOrderItem,
			filterable: true,
			showfilterrow: true,
			theme: theme,
			rowsheight: 26,
			width: '100%',
			height: 525,
			enabletooltips: true,
			autoheight: false,
			pageable: true,
			pagesize: 15,
			editable: true,
			localization: getLocalization(),
			columns:
			[
				{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<span style=margin:4px;>' + (value + 1) + '</span>';
					}
				},
				{ text: uiLabelMap.ProductId, dataField: 'productCode', width: '10%', editable: false, pinned: true, cellclassname: gridCellclass,},
				{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 120, editable: false, pinned: true, cellclassname: gridCellclass,},
				{ text: uiLabelMap.IsPromo, filterable: false, dataField: 'isPromo', width: '10%', editable: false,  cellclassname: gridCellclass,
					cellsrenderer: function (row, column, value) {
						if (value == 'Y'){
							return '<span>' + uiLabelMap.LogYes + '</span>';
						} else {
							return '<span>' + uiLabelMap.LogNO + '</span>';
						}
					}
				},
				{ text: uiLabelMap.BLQuantityAvailable, filterable: false, dataField: 'quantity', width: '12%', editable: false,  cellclassname: gridCellclass,
					cellsrenderer: function (row, column, value) {
						return '<span class="align-right">' + formatnumber(value) + '</span>';
					}
				},
				{ text: uiLabelMap.Unit, filterable: false, dataField: 'quantityUomId', width: '7%', editable: false, cellclassname: gridCellclass,
					cellsrenderer: function (row, column, value) {
						var data = gridEdit.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.baseWeightUomId;
						return '<span class="align-right">' + getUomDesc(value) +'</span>';
					}
				},
				{ text: uiLabelMap.BLQuantityCurrent, filterable: false, columntype: 'numberinput', cellsalign: 'right', dataField: 'createdQuantity', width: '9%', editable: false, cellclassname: gridCellclass, 
					cellsrenderer: function (row, column, value) {
						return '<span class="align-right">'+ formatnumber(value)+ '</span>';
					},
				},
				{ text: uiLabelMap.BLQuantityWant, filterable: false, columntype: 'numberinput',  cellsalign: 'right', dataField: 'newQuantity', width: '12%', editable: true, cellclassname: gridCellclass,
					cellsrenderer: function (row, column, value) {
						return '<span class="align-right">'+ formatnumber(value)+ '</span>';
					},
					initeditor: function (row, value, editor) {
						var data = gridEdit.jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && 'Y' == requireAmount) {
							editor.jqxNumberInput({ decimalDigits: 2});
						} else {
							editor.jqxNumberInput({ decimalDigits: 0});
						} 
					},
					validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: uiLabelMap.NumberGTOEZ };
						}
						var data = gridEdit.jqxGrid('getrowdata', cell.row);
						if (data.quantity < value) {
							return { result: false, message: uiLabelMap.BLQuantityGreateThanQuantityAvailable + ' ' + value + ' > ' + data.quantity};
						}
						return true;
					}
				}
			]
		});
	}
	
	function loadEditAddProductGrid(valueDataSoure) {
		var sourceOrderItem =
		{
			datafields:
			[
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'fromOrderId', type: 'string' },
				{ name: 'fromOrderItemSeqId', type: 'string' },
				{ name: 'fromTransferId', type: 'string' },
				{ name: 'fromTransferItemSeqId', type: 'string' },
				{ name: 'deliveryId', type: 'string' },
				{ name: 'isPromo', type: 'string' },
				{ name: 'deliveryItemSeqId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'baseQuantityUomId', type: 'string' },
				{ name: 'baseWeightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createdQuantity', type: 'number' },
				{ name: 'newQuantity', type: 'number' },
				{ name: 'convertNumber', type: 'number' }
			],
			localdata: valueDataSoure,
			datatype: "array"
		};
		var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
		gridAddProduct.jqxGrid({
			source: dataAdapterOrderItem,
			filterable: true,
			showfilterrow: true,
			theme: theme,
			rowsheight: 26,
			width: '100%',
			height: 480,
			enabletooltips: true,
			autoheight: false,
			pageable: true,
			pagesize: 15,
			editable: true,
			selectionmode: 'singlerow',
			localization: getLocalization(),
			columns:
			[
				{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<span style=margin:4px;>' + (value + 1) + '</span>';
					}
				},
				{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 120, editable: false, pinned: true, cellclassname: gridCellclass,},
				{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 120, editable: false, pinned: true, cellclassname: gridCellclass,},
				{ text: uiLabelMap.IsPromo, filterable: false, dataField: 'isPromo', width: '10%', editable: false,  cellclassname: gridCellclass,
					cellsrenderer: function (row, column, value) {
						if (value == 'Y'){
							return '<span>' + uiLabelMap.LogYes + '</span>';
						} else {
							return '<span>' + uiLabelMap.LogNO + '</span>';
						}
					}
				},
				{ text: uiLabelMap.BLQuantityAvailable, dataField: 'quantity', width: 150, editable: false, cellclassname: gridCellclass, 
					cellsrenderer: function (row, column, value) {
						return '<span class="align-right">' + formatnumber(value) + '</span>';
					}
				},
				{ text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 150, editable: false, cellclassname: gridCellclass,
					cellsrenderer: function (row, column, value) {
						var data = gridAddProduct.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.baseWeightUomId;
						return '<span class="align-right">' + getUomDesc(value) +'</span>';
					}
				},
				{ text: uiLabelMap.Quantity,columntype: 'numberinput',  cellsalign: 'right', dataField: 'newQuantity', width: 150, editable: true, cellclassname: gridCellclass,
					cellsrenderer: function (row, column, value) {
						return '<span class="align-right">'+ formatnumber(value)+ '</span>';
					},
					initeditor: function (row, value, editor) {
						var data = gridAddProduct.jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && 'Y' == requireAmount) {
							editor.jqxNumberInput({ decimalDigits: 2});
						} else {
							editor.jqxNumberInput({ decimalDigits: 0});
						} 
					},
					validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: uiLabelMap.NumberGTOEZ };
						}
						var data = gridAddProduct.jqxGrid('getrowdata', cell.row);
						if (data.quantity < value) {
							return { result: false, message: uiLabelMap.BLQuantityGreateThanQuantityAvailable + ' ' + value + ' > ' + data.quantity};
						}
						return true;
					}
				}
			]
		});
	}

	var initValidator = function () {
        var extendRules = [
            {
                input: '#conversionFactorPopup',
                message: uiLabelMap.ValueMustBeGreaterThanZero,
                action: 'blur',
                position: 'right',
                rule: function (input) {
                    if(globalVar.currencyUomId === 'VND') return true;
                    var value = input.val();
                    return value > 0;
                }
            }
        ];
        var mapRules = [
        ];
        validatorVAL = new OlbValidator($("#editPopupWindow"), mapRules, extendRules, {position: 'right'});
    };

	var getValidator = function() {
	    return validatorVAL;
    }
	
	var loadProductNotExportedYet = function (){
		if (listProductToAdd.length <= 0) {
			var listProducts = [];
			$.ajax({
				type: "POST",
				url: "getProductNotExportedYet",
				data: {
					deliveryId: glDeliveryId,
				},
				async: false,
				success: function (res) {
					listProducts = res.listProducts;
					var listData = [];
					for (var x in listProducts) {
						var obj = $.extend({}, listProducts[x]);
						var check = false;
						for (var y in listProductSelected){
							var tmp = listProductSelected[y];
							if (tmp.fromOrderItemSeqId && tmp.fromOrderItemSeqId == obj.fromOrderItemSeqId){
								check = true;
								break;
							} else if (tmp.fromTransferItemSeqId && tmp.fromTransferItemSeqId == obj.fromTransferItemSeqId){
								check = true;
								break;
							}
						}
						if (!check){
							obj.quantity = listProducts[x].quantity;
							obj.newQuantity = 0;
							obj.createdQuantity = listProducts[x].createdQuantity;
							listProductToAdd.push(obj);
						}
					}
					loadEditAddProductGrid(listProductToAdd);
				}
			});
		} else {
			var listProducts = [];
			for (var x in listProductToAdd){
				if (listProductToAdd[x].quantity > 0){
					listProducts.push(listProductToAdd[x]);
				}
			}
			loadEditAddProductGrid(listProducts);
		}
	};
	
	
	var gridCellclass = function (row, column, value, data) {
    	if (column == 'newQuantity') {
    		return 'background-prepare';
    	}
    	if (data.isPromo == 'Y'){
    		return 'background-promo';
    	}
	}
	
	var editAddNewProduct = function (){
		loadProductNotExportedYet();
		popupAddProduct.jqxWindow('open');
	};
	
	return {
		init: init,
		editDelivery: editDelivery,
		editAddNewProduct: editAddNewProduct,
	};
}());
