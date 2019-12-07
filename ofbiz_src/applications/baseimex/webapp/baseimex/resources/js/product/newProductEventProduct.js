$(function() {  
	EventProduct.init();
});

var EventProduct = (function() {
	var grid = $("#jqxGridProduct");  
	var gridAgreement = $("#jqxGridAgreement");  
	var gridPackingList = $("#jqxGridPackingList");  
	var product = null;
	var agreement = null;
	var packingList = null;
	var validatorVAL = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();
	};
	
	var initInput = function() { 
		
		$("#eventCode").jqxInput({width: 300, height: 24, theme: theme}); 
		$("#eventName").jqxInput({width: 300, height: 24, theme: theme}); 
		$("#description").jqxInput({width: 300, theme: theme}); 
		
		$("#agreement").jqxDropDownButton({width: 300, theme: theme}); 
		$('#agreement').jqxDropDownButton('setContent', '<div class="dropdown-button">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#packingList").jqxDropDownButton({width: 300, theme: theme}); 
		$('#packingList').jqxDropDownButton('setContent', '<div class="dropdown-button">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$('#productEventType').jqxDropDownList({ source: productEventTypeData, selectedIndex: 0, width: 300,theme: theme, valueMember: 'eventTypeId', displayMember: 'description', placeHolder : uiLabelMap.PleaseSelectTitle})
		
		$("#executedDate").jqxDateTimeInput({width: 300, theme: theme}); 
		$("#completedDate").jqxDateTimeInput({width: 300, theme: theme}); 
		$("#executedDate").jqxDateTimeInput('clear');
		$("#completedDate").jqxDateTimeInput('clear');
		
	}
	
	var initValidateForm = function(){
		var extendRules = [
		                   	{
		                   		input: '#eventCode', 
		                   		message: uiLabelMap.WrongFormat + "0-9, a-z, A-Z, _, -", 
		                   		action: 'blur', 
		                   		position: 'right',
		                   		rule: function (input) {
		                   			if (input.length > 0 ){
		                   				var patt = /[^0-9a-zA-Z\_\-]/gm;
		                   				var result = input.val().match(patt);
		                   				if (result) return false
		                   				else return true;
		                   			}
		                   			return true;
		                   		}
		                   	},           
		                   	{input: '#agreement', message: uiLabelMap.FieldRequired, action: 'close', position: 'right',
		                   		rule: function(input, commit){
		                   			if (!agreementSelected){
		                   				return false;
		                   			}
		                   			return true;
		                   		}
		                   	},
		                   	{input: '#packingList', message: uiLabelMap.FieldRequired, action: 'close', position: 'right',
		                   		rule: function(input, commit){
		                   			if (!packingListSelected){
		                   				return false;
		                   			}
		                   			return true;
		                   		}
		                   	},
		                   	];
   		var mapRules = [
   				{input: '#executedDate', type: 'validInputNotNull'},
   				{input: '#productEventType', type: 'validObjectNotNull', objType: 'dropDownList'},
               ];
   		validatorVAL = new OlbValidator($('#infoForm'), mapRules, extendRules, {position: 'right'});
	};
	
	 var getValidator = function(){
    	return validatorVAL;
    };
    
	var initElementComplex = function() {
		initGridProduct(grid);
		initGridAgreement(gridAgreement);
		initGridPackingList(gridPackingList);
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
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', sortable: false,  width: 120, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.BLQuantityRegistered, datafield: 'quantity', sortable: false,  width: 150, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}
			},
			{ text: uiLabelMap.BLQuantityUse, datafield: 'createQuantity', sortable: false,  width: 150, editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
			   					value = olb.createQuantity;
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
				   					cellvalue = olb.createQuantity;
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
					var rowData = grid.jqxGrid('getrowdata', cell.row);
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					if (value > rowData.quantity) {
						return { result: false, message: uiLabelMap.BLQuantityGreateThanQuantityAvailable + " " + value + " > " + rowData.quantity};
					}
					return true;
				}, 
			},
        ];
		return columns; 
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createQuantity', type: 'number' },
				{ name: 'requireAmount', type: 'String'}]
		return datafield;
	};
	
	var initGridProduct = function(grid){
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				editmode: 'click',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: true,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: '',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, getDataField(), getColumns(grid), null, grid);
	};
	
	var initGridAgreement = function(grid){
		var datafield =  [
			{ name: 'agreementId', type: 'string'},
			{ name: 'attrValue', type: 'string'},
			{ name: 'statusId', type: 'string'},
		                  ];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.AgreementId, dataField: 'agreementId', width: 120, editable: false, pinned: true,},
			{ text: uiLabelMap.AgreementName, dataField: 'attrValue', minwidth: 100, editable:false,},
			{ text: uiLabelMap.Status, dataField: 'statusId', width: 130, editable:false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value) {
					return '<span>' + getStatusDesc(value) +'</span>';
				}, 
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(agreeStatusData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
						renderer: function(index, label, value){
							return '<span>' + getStatusDesc (value) + '</span>';
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			];
		var config = {
				width: 500, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'JQGetListPurchaseAgreements&statusId=AGREEMENT_COMPLETED&statusId=AGREEMENT_PROCESSING',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initGridPackingList = function(grid){
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.BIEPackingListId, dataField: 'packingListNumber', width: 120,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span>' + value  + '</span>';
				}
			},
			{ text: uiLabelMap.BIEBillId, dataField: 'billNumber', width: 120,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span>' + value  + '</span>';
				}
			},
			{ text: uiLabelMap.BIEContainerId, dataField: 'containerNumber', width: 120,
				cellsrenderer: function(row, column, value) {
					var data = grid.jqxGrid('getrowdata', row);
					return '<span>' + value  + '</span>';
				}
			},
			{ text: uiLabelMap.OrderPO, dataField: 'purchaseOrderId', minwidth: 120,
				cellsrenderer: function(row, column, value) {
					return '<span>' + value  + '</span>';
				}
			},
        ];
		
		var datafield = [
         	{ name: 'packingListId', type: 'string'},
         	{ name: 'packingListNumber', type: 'string'},
         	{ name: 'externalInvoiceNumber', type: 'string'},
         	{ name: 'externalOrderNumber', type: 'string'},
			{ name: 'externalOrderTypeId', type: 'string'},
			{ name: 'description', type: 'string'},
			{ name: 'containerId', type: 'string'},
			{ name: 'sealNumber', type: 'string'},
			{ name: 'billId', type: 'string'},
			{ name: 'billNumber', type: 'string'},
			{ name: 'containerNumber', type: 'string'},
			{ name: 'purchaseOrderId', type: 'string'},
			{ name: 'agreementId', type: 'string'},
			{ name: 'netWeightTotal', type: 'number'},
			{ name: 'grossWeightTotal', type: 'number'},
			{ name: 'packingListDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'externalInvoiceDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'departureDate', type: 'date', other: 'Timestamp'},
		 	{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
	 	]
		
		var url = "";
		
		var config = {
			width: 600, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 15}
	  	};

		Grid.initGrid(config, datafield, columnlist, null, grid);
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
				if (dataField == 'createQuantity'){
					if (value >= 0){
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
						if (value > 0 && value <= rowData.quantity){
							var item = $.extend({}, rowData);
							item.createQuantity = value;
							listProductSelected.push(item);
						}
					} 
				} 
			}
		});
		
		gridAgreement.on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = gridAgreement.jqxGrid('getrowdata', rowBoundIndex);
	        if (rowData){
	        	if (!agreementSelected || (agreementSelected && agreementSelected.agreementId != rowData.agreementId)) {
		        	agreementSelected = $.extend({}, rowData);
			        var dropDownContent = '<div class="dropdown-button">'+ rowData.agreementId +'</div>';
			        $('#agreement').jqxDropDownButton('setContent', dropDownContent);
			        gridAgreement.jqxGrid('selectrow', rowBoundIndex);
			        gridPackingList.jqxGrid('clear');
			        gridPackingList.jqxGrid('clearselection');
			        $('#packingList').jqxDropDownButton('setContent', '<div class="dropdown-button">'+uiLabelMap.PleaseSelectTitle+'</div>');
			        
			        updatePackingListGridData(rowData.agreementId);
			        grid.jqxGrid('clear');
			        $("#agreement").jqxDropDownButton('close');
			        listProductSelected = [];
	        	}
	        } else {
	        	gridAgreement.jqxGrid('clearselection');
	        }
	    });
		
		gridPackingList.on('rowclick', function (event) {
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			var rowData = gridPackingList.jqxGrid('getrowdata', rowBoundIndex);
			if (rowData){
				packingListSelected = $.extend({}, rowData);
				var dropDownContent = '<div class="dropdown-button">'+ rowData.packingListId +'</div>';
				$('#packingList').jqxDropDownButton('setContent', dropDownContent);
				gridPackingList.jqxGrid('selectrow', rowBoundIndex);
				
				updateProductGridData(rowData.packingListId);
				
				$("#packingList").jqxDropDownButton('close');
			} else {
				gridPackingList.jqxGrid('clearselection');
	        }
		});
	 
	}
	
	var productGridCellclass = function (row, column, value, data) {
		var data = grid.jqxGrid('getrowdata',row);
    	if (column == 'createQuantity') {
    		return 'background-prepare';
    	}
	}
	
	var updateProductGridData = function (packingListId){
		var url = "jqxGeneralServicer?sname=jqGetPackingListItems&packingListId=" + packingListId;
		grid.jqxGrid("source")._source.url = url;
		grid.jqxGrid("updatebounddata");
	}
	
	var updatePackingListGridData = function (agreementId){
		var url = "jqxGeneralServicer?sname=jqGetPackingLists&agreementId=" + agreementId;
		gridPackingList.jqxGrid("source")._source.url = url;
		gridPackingList.jqxGrid("updatebounddata");
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
	 
	 var updateProductData = function (data){
		 var tmpS = grid.jqxGrid("source");
		tmpS._source.localdata = data;
		grid.jqxGrid("source", tmpS);
		grid.jqxGrid("updatebounddata");
	 }
	 
	return {
		init : init,
		getValidator: getValidator, 
	}
}());