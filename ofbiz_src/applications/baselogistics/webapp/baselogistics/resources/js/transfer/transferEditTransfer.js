$(function () {
	TransferEditObj.init();
});
var TransferEditObj = (function () {

	var listProductGrid;
	var productAddOLBG = null;
	var listProductAdd = [];
	var init = function () {
		if (noteValidate === undefined) var noteValidate;
		initInputs();
		initElementComplex();
		initEvents();
	};
	var initInputs = function () {
		if (transferDate != null && (shipBeforeDate == null || shipAfterDate == null)) {
			$("#transferDateDT").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)));
		} else if (transferDate != null && shipBeforeDate != null && shipAfterDate != null) {
			$("#transferDateDT").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)) + " (" + DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) - DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)) + ")");
		} else if (transferDate == null && (shipBeforeDate != null && shipAfterDate != null)) {
			$("#transferDateDT").text(DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) + ' - ' + DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)));
		}
		if (statusDatetime != null && statusDatetime != undefined && statusDatetime != '') {
			$("#completedDateDT").text(DatetimeUtilObj.formatFullDate(new Date(statusDatetime)));
		} else {
			$("#completedDateDT").text('');
		}

		if (shipBeforeDate) {
			$("#shipBeforeDateDT").text(DatetimeUtilObj.formatToMinutes(new Date(shipBeforeDate)));
		}
		if (shipAfterDate) {
			$("#shipAfterDateDT").text(DatetimeUtilObj.formatToMinutes(new Date(shipAfterDate)));
		}
		
		$("#addProductPopup").jqxWindow({
	 	    maxWidth: 1500, minWidth: 500, width: 1000, modalZIndex: 10000, zIndex:10000, minHeight: 200, height: 470, maxHeight: 670, resizable: false, cancelButton: $("#addProductCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
	 	});
	};
	var initElementComplex = function () {
		initGrid();
		initProductGridAdd();
	};
	var initEvents = function () {
		$("#jqxgridProductDetail").on("rowselect", function (event) {
			var args = event.args;
			var rowData = args.row;
			if (rowData) {
				productSelected = $.extend({}, rowData);
			}
		});

		$("#jqxgridProductDetail").on("cellendedit", function (event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData) {
				if (dataField == 'quantity') {
					var item = $.extend({}, rowData);
					if (value == 0 && value != undefined) {
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					item.statusId = "TRANS_ITEM_CANCELLED";
			   					return false;
			   				}
			   			});
					}
					if (value > 0 && value != undefined) {
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						if (rowData.requireAmount && rowData.requireAmount == 'Y'){
							item.amount = value;
						} else {
							item.quantity = value;
						}
						item.statusId = "TRANS_ITEM_CREATED";
						listProductSelected.push(item);
					}
				}
			}
		});

		$("#jqxgridProductAdd").on("cellendedit", function(event) {
				var args = event.args;
				var dataField = event.args.datafield;
				var rowBoundIndex = event.args.rowindex;
				var value = args.value;
				var oldvalue = args.oldvalue;
				var rowData = args.row;
				if (rowData){
					var productId = rowData.productId;
					var item = null;
					for (var c in listProductAdd){
						var b = listProductAdd[c];
						if (b.productId == productId){
							item = b;
							break;
						}
					}
					if (dataField == "quantity") {
				    	if (value !== undefined && value !== null && value != '' && value > 0){
				    		if (item){
				    			if(item.requireAmount && item.requireAmount == 'Y'){
				    				item.amount = value;
				    			}
				    			else{
				    				item.quantity = value;
				    			}
				    		} else {
				    			var s = $.extend({}, rowData);
				    			if(s.requireAmount && s.requireAmount == 'Y'){
				    				s.amount = value;
				    			}else{
				    				s.quantity = value;
				    			}
				    			listProductAdd.push(s);
				    		}
				    	}
				    	if (value == 0){
				    		if (item != undefined){
				    			for (var c in listProductAdd){
									var b = listProductAdd[c];
									if (b.productId == productId){
										listProductAdd.splice(c, 1);
										break;
									}
								}
				    		}
				    	}
					}
				}
			});
			
			$("#addProductSave").on("click", function (event) {
				var rowPosition = "first";
				if (listProductAdd.length > 0){
					for (var x in listProductAdd){
						var data = listProductAdd[x];
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
							if(item.statusId == "TRANS_ITEM_CANCELLED"){
								item.statusId = "TRANS_ITEM_CREATED";
								if(item.requireAmount && item.requireAmount == 'Y'){
									item.amount = data.amount;
								}
								else{
									item.quantity = data.quantity;
								}
							} else {
								var x = item.quantity;
								if(item.requireAmount && item.requireAmount == 'Y'){
									x = item.amount;
								}
								if (typeof x === 'string') {
									x.replace(',', '.');
									x = parseFloat(x, 3, null);
								}
								if(item.requireAmount && item.requireAmount == 'Y'){
									item.amount = x+ data.amount;
								}
								item.quantity = x + data.quantity;
							}
							var curRows = $("#jqxgridProductDetail").jqxGrid('getrows');
							for (var z in curRows){	
								var t = curRows[z];
								if (t.productId == productId){
									if(t.requireAmount && t.requireAmount == 'Y'){
										$("#jqxgridProductDetail").jqxGrid('setcellvaluebyid', t.uid, "quantity", item.amount);
									}else{
										$("#jqxgridProductDetail").jqxGrid('setcellvaluebyid', t.uid, "quantity", item.quantity);
									}
								}
							}
						} else {
							var x = $.extend({}, data);
							x.statusId = "TRANS_ITEM_CREATED";
							listProductSelected.push(x);
						}
					}
				}
				var listProductSelectedFinal = [];
				for(var i=0; i<listProductSelected.length; i++){
					if(listProductSelected[i].statusId != "TRANS_ITEM_CANCELLED" && listProductSelected[i].quantity != 0){
						listProductSelectedFinal.push(listProductSelected[i]);
					}
				}
				
				OlbGridUtil.updateSource($("#jqxgridProductDetail"), null, listProductSelectedFinal, false);
				$("#jqxgridProductDetail").jqxGrid('updatebounddata');
				$("#addProductPopup").jqxWindow('close');
			});
			
			$("#addProductPopup").on("close", function (event) {
				listProductAdd = [];
			});
	};


	var initGrid = function () {
		initListProductSelected();

		var grid = $("#jqxgridProductDetail");

		var rendertoolbar = function (toolbar) {
			toolbar.html("");
			var container = $("<div id='toolbarcontainerGridProduct' class='widget-header' style='height:33px !important;'><div id='jqxProductSearch' class='pull-right' style='margin-left: -10px !important; margin-top: 4px; padding: 0px !important'></div></div>");
			toolbar.append(container);
			container.append('<div class="margin-top10">');
			container.append('<a href="javascript:TransferEditObj.deleteRow()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="red fa fa-times"></i></a>');
			container.append('<a href="javascript:TransferEditObj.addWithGrid()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="fa fa-plus"></i></a>');
			container.append('</div>');

			var paramInput = {
				facilityId: transfer.originalFacilityId,
			};
			ProductSearch.init($("#jqxProductSearch"), $("#jqxgridProductDetail"), "quantity", "findProductByOrganization", "listProducts", paramInput, listProductSelected, uiLabelMap.BPSearchProductToAdd, uiLabelMap.BPProductNotFound);
		}
		
		
		var columns = [
			{
				text: uiLabelMap.SequenceId, sortable: false, filterable: false, pinned: true, editable: false, groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<span>' + (value + 1) + '</span>';	
				}
			},
			{
				text: uiLabelMap.ProductProductId, dataField: 'productCode', width: 200, pinned: true, editable: false, filterable: true, sortable: true,
			},
			{
				text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 200, editable: false, filterable: true, sortable: true
			},
			{
				text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 120, editable: false, filterable: false,
				cellsrenderer: function (row, column, value) {
					var data = $('#jqxgridProductDetail').jqxGrid("getrowdata", row);
					var requireAmount = data.requireAmount;
					if (requireAmount && requireAmount == 'Y') {
						value = data.weightUomId;
					}
					if (value) {
						return '<span style=\"text-align: right !important\">' + getUomDescription(value) + '</span>';
					}
				},
			},
			{
				text: uiLabelMap.RequiredNumberSum, dataField: 'quantity', width: 150, editable: true, filterable: false, sortable: true, cellclassname: productGridCellclass, cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function (row, column, value) {
					var data = $('#jqxgridProductDetail').jqxGrid('getrowdata', row);
					var requireAmount = data.requireAmount;
					if (requireAmount && requireAmount == 'Y') {
						for( var x of listProductSelected){
							if (x.productId == data.productId){
								value = x.amount;
							}
						}
					}
					else{
						value = data.quantity;
					}
					if (value) {
						return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
					} else {
						return '<span></span>';
					}
				},
				initeditor: function (row, cellvalue, editor) {
					var rowData = $('#jqxgridProductDetail').jqxGrid('getrowdata', row);
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min: 0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min: 0 });
					}
					var rowData = $('#jqxgridProductDetail').jqxGrid('getrowdata', row);
					if (cellvalue) {
						if (listProductSelected.length > 0) {
							$.each(listProductSelected, function (i) {
								var olb = listProductSelected[i];
								if (olb.productId == rowData.productId) {
									if(rowData.requireAmount && rowData.requireAmount == 'Y'){
										cellvalue = olb.amount;
									}else{
										cellvalue = olb.quantity;
									}
									return false;
								}
							});
						}
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
					else if (cellvalue == 0) {
						var rowindex = $("#jqxgridProductDetail").jqxGrid('getselectedrowindex');
						var data = $("#jqxgridProductDetail").jqxGrid('getrowdata', rowindex);
						if (data) {
							if (data.quantity > 0) {
								$.each(listProductSelected, function (m) {
									var olb = listProductSelected[m];
									if (olb) {
										if (olb.productId == data.productId) {
											listProductSelected[m].statusId = "TRANS_ITEM_CANCELLED";
											return false;
										}
									}
								});
							}
						}
					}
				}, validation: function (cell, value) {
					if (value < 0) {
						return { result: false, message: uiLabelMap.BLSGCMustNotInputNegativeValue }
					}
					return true;
				}
			},
		];

		var datafield = [{ name: 'productId', type: 'string' },
		{ name: 'productName', type: 'string' },
		{ name: 'productCode', type: 'string' },
		{ name: 'quantityUomId', type: 'string' },
		{ name: 'amount', type: 'number' },
		{ name: 'weightUomId', type: 'string' },
		{ name: 'productPacking', type: 'string' },
		{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
		{ name: 'fromExpiredDate', type: 'date', other: 'Timestamp' },
		{ name: 'toExpiredDate', type: 'date', other: 'Timestamp' },
		{ name: 'expiredDateRequired', type: 'string' },
		{ name: 'quantity', type: 'number' },
		{ name: 'requireAmount', type: 'number' },
		{ name: 'quantityDelivered', type: 'number' },
		{ name: 'quantityRemain', type: 'number' },
		{ name: 'quantityShipping', type: 'number' },
		{ name: 'quantityScheduled', type: 'number' },
		{ name: 'baseQuantityUomId', type: 'string' },
		{ name: 'convertNumber', type: 'string' },
		{ name: 'transferId', type: 'string' },
		{ name: 'transferItemSeqId', type: 'string' },
		{ name: 'description', type: 'string' },
		{ name: 'statusId', type: 'string' },
		];
		var config = {
			columns: columns,
			datafields: datafield,
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
			formData: "filterObjData",
			selectionmode: "singlerow",
			bindresize: true,
			pagesize: 10,
			rendertoolbar: rendertoolbar,
		};
		listProductGrid = new OlbGrid(grid, null, config, []);
		initGridData();
		
	}
	
	var dataFieldAdd = [
	                	{name: 'productId', type: 'string'},
	        			{name: 'productCode', type: 'string'},
	               		{name: 'productName', type: 'string'},
	               		{name: 'description', type: 'string'},
	               		{name: 'amount', type: 'number'},
	               		{name: 'quantityUomId', type: 'string'},
	               		{name: 'weightUomId', type: 'string'},
	               		{name: 'quantityOnHandTotal', type: 'number' },
	               		{name: 'unitCost', type: 'number'},
	               		{name: 'quantity', type: 'number'},
	               		{ name: 'requireAmount', type: 'string' },
	               		{ name: 'uomId', type: 'string' },
	                    ];
	var columnAdd = [
				{text: uiLabelMap.ProductProductId, dataField: 'productCode', width: 140, editable:false},
				{text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 120, editable:false},
				{text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 120, editable:false, filterable: false,
					cellsrenderer : function(row, column, value) {
						var data = $("#jqxgridProductAdd").jqxGrid("getrowdata", row);
						var requireAmount = data.requireAmount;
						if (requireAmount && requireAmount == 'Y') {
							value = data.weightUomId;
						}
						if (value) {
							return '<span class="align-right">' + getUomDescription(value) + '</span>';
						}
					},
				},
				{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', sortable: false, width: 120, editable: false, filterable: false, cellsalign: 'right', cellclassname: productGridCellclass,
					cellsrenderer: function(row, column, value) {
						var data = $("#jqxgridProductAdd").jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') {
							value = data.amountOnHandTotal;
						} 
						var description = formatnumber(value);
						if (data.requireAmount && data.requireAmount == 'Y') {
							description = formatnumber(value) + ' (' + getUomDescription(data.weightUomId) +')';	
						} else {
							description = formatnumber(value) + ' (' + getUomDescription(data.quantityUomId)+')';
						}
						return '<span class="align-right">' + description +'</span>';
					}, 
				},
				{ text : uiLabelMap.RequiredNumberSum, datafield : "quantity", width : "120", editable : true,sortable: false, filterable : false, cellsalign : "right", columntype : "numberinput", cellClassName: 'background-prepare',
					cellsrenderer : function(row, column, value) {
						var rowData = $("#jqxgridProductAdd").jqxGrid('getrowdata', row);
						var requireAmount = rowData.requireAmount;
						if (listProductAdd.length > 0){
					    	$.each(listProductAdd, function(i){
				   				var olb = listProductAdd[i];
				   				if (olb.productId == rowData.productId ){
				   					if(requireAmount && requireAmount == 'Y'){
				   						value = olb.amount; 
				   					}
				   					else{
				   						value = olb.quantity;
				   					}
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
						var rowData = $("#jqxgridProductAdd").jqxGrid('getrowdata', row);
						if ('Y' == rowData.requireAmount) {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
						} else {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
						}
						if (cellvalue) {
							if (listProductAdd.length > 0){
						    	$.each(listProductAdd, function(i){
					   				var olb = listProductAdd[i];
					   				if (olb.productId == rowData.productId ){
					   					if(rowData.requireAmount && rowData.requireAmount == 'Y'){
											cellvalue = olb.amount;
										}else{
											cellvalue = olb.quantity;
										}
					   					return false;
					   				}
					   			});
						    }
							var u = cellvalue.toString().replace('.', ',');
							editor.jqxNumberInput('val', u);
						}
					},
					validation : function(cell, value) {
						if (value < 0) {
							return { result : false, message : uiLabel.ValueMustBeGreaterThanZero };
						}
						return true;
					}
				},
				
             ];
	
	var initProductGridAdd = function() {
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
			productAddOLBG = new OlbGrid($("#jqxgridProductAdd") , null, configProductAdd, []);
			
	};
	

	function initGridData() {
		OlbGridUtil.updateSource($('#jqxgridProductDetail'), null, listProductSelected, false);
		$('#jqxgridProductDetail').jqxGrid('updatebounddata');
	}

	var initListProductSelected = function () {
		$.ajax({
			type: 'POST',
			url: 'jqxGeneralServicer?sname=JQGetListTransferItem',
			async: false,
			data: {
				transferId: transferId,
			},
			success: function (data) {
				list = data.results;
			}
		});
		for (var i = 0; i < list.length; i++) {
			listProductSelected[i] = list[i];
		}
	}

	var isListProductHasNull = function(){
		count = 0;
		for(var i=0; i< listProductSelected.length; i++){
			if(listProductSelected[i].statusId == "TRANS_ITEM_CANCELLED") count++;
		}

		if(count == listProductSelected.length){
			return true;
		}
		return false;
	}
	

	var productGridCellclass = function (row, column, value, data) {
		var data = $("#jqxgridProductDetail").jqxGrid('getrowdata', row);
		if (data['purchaseDiscontinuationDate'] != undefined && data['purchaseDiscontinuationDate'] != null) {
			var now = new Date();
			var ex = new Date(data['purchaseDiscontinuationDate']);
			if (ex <= now) {
				return 'background-cancel';
			}
		} else {
			if (column == 'quantity' || column == 'unitCost' || column == 'description') {
				return 'background-prepare';
			}
		}
	}

	var deleteRow = function () {
		if (productSelected == null) {
			jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
			return false;
		}
		var rowindex = $("#jqxgridProductDetail").jqxGrid('getselectedrowindex');
		$("#jqxgridProductDetail").jqxGrid('endcelledit', rowindex, "quantity", false);
		var data = $("#jqxgridProductDetail").jqxGrid('getrowdata', rowindex);
		if (data) {
			if (data.quantity > 0 || data.amount > 0) {
				$.each(listProductSelected, function (m) {
					var olb = listProductSelected[m];
					if (olb) {
						if (olb.productId == data.productId) {
							listProductSelected[m].statusId = "TRANS_ITEM_CANCELLED";
							return false;
						}
					}
				});
			}
			var rowid = data.uid;
			$("#jqxgridProductDetail").jqxGrid('deleterow', rowid);
		}
	}
	
	var addWithGrid = function (){
		if (OlbCore.isNotEmpty(transfer.originalFacilityId)){
			productAddOLBG.updateSource("jqxGeneralServicer?sname=JQGetListProductByOrganiztion&inventoryInfo=Y&facilityId="+transfer.originalFacilityId+"&requirementTypeId=RECEIVE_REQUIREMENT");
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
		if (column == 'quantity') {
			return 'background-prepare';
		}
	}
	
	var checkSpecialCharacters = function(value) {
		if (OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/.test(value))) {
			return true;
		}
			return false;
	}
	
	function unescapeHTML(escapedStr) {
		 var div = document.createElement('div');
		     div.innerHTML = escapedStr;
		     var child = div.childNodes[0];
		     return child ? child.nodeValue : '';
	};
	
	return {
		init: init,
		deleteRow: deleteRow,
		addWithGrid : addWithGrid,
	}
}());