$(function() {
	ReturnProductWoutOrderObj.init();
});
var ReturnProductWoutOrderObj = (function() {
	var productGRID = null;
	var	mapReasonEdit = {};
	var init = function() {
		if (listProductSelected === undefined) {
			listProductSelected = [];
		}
		if (mapProductSelected === undefined) {
			mapProductSelected = {};
		}
		if (mapPriceEdit === undefined) {
			mapPriceEdit = {};
		}
		if (mapDescriptionEdit === undefined) {
			mapDescriptionEdit = {};
		}
		
		
		
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		initProductGrid();
	};
	var initElementComplex = function() {
		
	};
	var initEvents = function() {
		$("#jqxgridProductWoutOrder").on("pagechange", function (event) {    
			   
		});
		$("#jqxgridProductWoutOrder").on("rowunselect", function(event) {
			
		});

		$("#jqxgridProductWoutOrder").on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'returnQuantity'){
					if (value >0){
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						mapProductSelected[rowData.productId]=value;
						item.returnQuantity = value;
						listProductSelected.push(item);	
					}
					
				} else if(dataField == 'returnPrice'){
					if (rowData.returnQuantity > 0){
						var item = $.extend({}, rowData);
						item.returnPrice = value;
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
						listProductSelected.push(item);
					}
					mapPriceEdit[rowData.productId]=value;
				}  else if (dataField == 'description'){
					if (rowData.returnQuantity > 0 && value != '' && value != null && value != undefined && value != 'null'){
						var item = $.extend({}, rowData);
						item.description = value;
						if (listProductSelected.length > 0){
							$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					listProductSelected.splice(i,1);
				   					return false;
				   				}
				   			});
						}
						listProductSelected.push(item);
					}
					mapDescriptionEdit[rowData.productId]=value;
				} else if (dataField == 'returnReasonId'){
						if (rowData.returnQuantity > 0 && value != '' && value != null && value != undefined && value != 'null'){
							var item = $.extend({}, rowData);
							item.returnReasonId = value;
							if (listProductSelected.length > 0){
								$.each(listProductSelected, function(i){
					   				var olb = listProductSelected[i];
					   				if (olb.productId == rowData.productId ){
					   					listProductSelected.splice(i,1);
					   					return false;
					   				}
					   			});
							}
							listProductSelected.push(item);
						}
						mapReasonEdit[rowData.productId]=value;
				}
			}
		});
	};
	
	var initValidateForm = function() {
		var extendRules = [];
		var mapRules = [];
	};

	var datafieldprs = [ 
	                    {name: 'productId', type: 'string'},
	         			{name: 'productCode', type: 'string'},
	               		{name: 'internalName', type: 'string'},
	               		{name: 'productName', type: 'string'},
	               		{name: 'quantityUomId', type: 'string'},
	               		{name: 'returnPrice', type: 'number', formatter: 'float'}, 
	               		{name: 'returnQuantity', type: 'number'},
	               		{name: 'requireAmount', type: 'number'},
	               		{name :'returnReasonId', type : 'string' },
                     ];
	var columnprs = [
	                 	{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
	                 		groupable: false, draggable: false, resizable: false,
	                 		datafield: '', columntype: 'number', width: 50,
	                 		cellsrenderer: function (row, column, value) {
	                 			return '<div style=margin:4px;>' + (value + 1) + '</div>';
	                 		}
	                 	},	                 	
						{text: uiLabelMap.ProductId, dataField: 'productCode', width: 120, editable:false},
						{text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 120, editable:false},
						{ text : uiLabelMap.Unit, datafield : "quantityUomId", width : "120", editable : false, filterable : false, cellsalign : "right", columntype : "numberinput",
							cellsrenderer : function(row, column, value) {
								var data = $("#jqxgridProductWoutOrder").jqxGrid("getrowdata", row);
								var requireAmount = data.requireAmount;
								if (requireAmount && requireAmount == 'Y') {
									value = data.weightUomId;
								}
								if (value) {
									return '<span class="align-right">' + getUomDescription(value) + '</span>';
								}
							},
						},
						{ text: uiLabelMap.BSReturnPrice, datafield: 'returnPrice', sortable: false, width: 130, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput',cellclassname: productGridCellclass,
							cellsrenderer: function(row, column, value){
								var rowData = $('#jqxgridProductWoutOrder').jqxGrid('getrowdata', row);
								var item = mapPriceEdit[rowData.productId];
								if (item) {
									value = item;
								}
								if (value) {
									return '<span style=\"text-align: right\" title=\"'+formatnumber(value, null, 3)+'\">' + formatnumber(value, null, 3) +'</span>';
								}
							},
							initeditor: function (row, cellvalue, editor) {
								editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 3 });
							}
						},
						
						{ text : uiLabelMap.BSReturnQty, datafield : "returnQuantity", width : "120", editable : true, filterable : false, sortable: false, cellsalign : "right", columntype : "numberinput", cellclassname: productGridCellclass, 
							cellsrenderer: function(row, column, value){
								var rowData = $('#jqxgridProductWoutOrder').jqxGrid('getrowdata', row);
								var item = mapProductSelected[rowData.productId];
								if (item) {
									value = item;
								} 
								if (value) {
									return '<span style=\"text-align: right\">' + value +'</span>';
								} 
								return value;
							},
							createeditor : function(row, cellvalue, editor) {
								var data = $("#jqxgridProductWoutOrder").jqxGrid("getrowdata", row);
								var requireAmount = data.requireAmount;
								if (requireAmount && requireAmount == 'Y') {
									editor.jqxNumberInput({ decimalDigits: 2, inputMode : "simple"});
								} else {
									editor.jqxNumberInput({ decimalDigits: 0, inputMode : "simple"});
								}
							},
							validation : function(cell, value) {
								var data = $("#jqxgridProductWoutOrder").jqxGrid("getrowdata", cell.row);
								if (value < 0) {
									return { result : false, message : uiLabelMap.BSQuantityMustBeGreaterThanZero };
								}
								return true;
							}
						},
						{text: uiLabelMap.Description, dataField: 'description', width: 140, editable: true, filterable: false, cellClassName: 'background-prepare',
							cellsrenderer: function(row, column, value){
								var rowData = $('#jqxgridProductWoutOrder').jqxGrid('getrowdata', row);
								var item = mapDescriptionEdit[rowData.productId];
								
								if (item) {
									value = item;
								}
								if (value) {
									return '<span style=\"text-align: right\">' +value+'</span>';
								}
							},
							initeditor: function (row, cellvalue, editor) {
								editor.jqxInput();
								if (!cellvalue) {
									var rowData = $('#jqxgridProductWoutOrder').jqxGrid('getrowdata', row);
									var item = mapDescriptionEdit[rowData.productId];
									if (item) {
										if (item.description) {
											cellvalue = item.description;
										}
									} 
								}
								editor.jqxInput('val', unescapeHTML(cellvalue));
							},
						
						},
						{ text : uiLabelMap.Reason, datafield : "returnReasonId", width : 200, editable : true, filterable : false, columntype : "dropdownlist", 
							cellsrenderer : function(row, column, value) {
								var rowData = $('#jqxgridProductWoutOrder').jqxGrid('getrowdata', row);
								var item = mapReasonEdit[rowData.productId];
								if (item) {
									value = item;
								}
								if (value) {
									for (var i = 0; i < returnReasonData.length; i++) {
										if (value == returnReasonData[i].returnReasonId) {
											return '<span class="cell-left-focus">' + returnReasonData[i].description + '</span>';
										}
									}
								} else {
									return '<span class="cell-left-focus"></span>';
								}
							},
							createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxDropDownList({
									placeHolder : uiLabelMap.PleaseSelectTitle,
									source : returnReasonData,
									valueMember : "returnReasonId",
									displayMember : "description"
								});
							}
						}, 
						
					];
	var initProductGrid = function() {
		var grid = $("#jqxgridProductWoutOrder");
		var datafield = datafieldprs;
		var columns = columnprs;
		var config = {
			datafield : datafieldprs,
			columns : columnprs,
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
			selectionmode: "singlecell",
			editmode: "click",
			bindresize: true,
			virtualmode: true,
			pagesize: 15,
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		setTimeout(function() {
			if (!$("#jqxgridProductWoutOrder").find($('div[role="row"]')).find($('div[role="gridcell"]')).hasClass("hide")) { 
				$("#jqxgridProductWoutOrder").find($('div[role="row"]')).find($('div[role="gridcell"]')).addClass("hide");    
			}  
		}, 300);
	};
	var reloadListProduct = function(){
		var facilityId = OlbReturnWithoutOrderInfo.getObj().destinationFacilityDDB.getValue();
		var currencyUomId = OlbReturnWithoutOrderInfo.getObj().currencyUomDDB.getValue();
		var supplierId = OlbReturnWithoutOrderInfo.getObj().supplierDDB.getValue();
		OlbGridUtil.updateSource($('#jqxgridProductWoutOrder'), "jqxGeneralServicer?sname=JQGetListProductToReturnSupplier&facilityId=" + facilityId + "&currencyUomId=" + currencyUomId + "&supplierId=" + supplierId);
	};
	function productGridCellclass (row, column, value, data) {
    	if (column == 'returnQuantity' || column == 'returnPrice') {
			return 'background-prepare';
    	}
	}
	function unescapeHTML(escapedStr) {
     	var div = document.createElement('div');
     	div.innerHTML = escapedStr;
     	var child = div.childNodes[0];
     	return child ? child.nodeValue : '';
 	};
	return {
		init : init,
		reloadListProduct : reloadListProduct,
	}
}());