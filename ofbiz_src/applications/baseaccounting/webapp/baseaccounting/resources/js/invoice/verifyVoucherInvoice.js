var verifyInvoiceObj = (function(){
	var _voucherTotalAmount = 0;
	var _voucherTaxAmount = 0;
	var _diffTotalAmount = 0;
	var _diffTaxAmount = 0;
	var	_decimalseparator = ",";
	var _thousandsseparator = ".";
	var _currencysymbol = "đ";
	var init = function(){
		initInvoiceItemNotTaxGrid();
		initInvoiceItemTaxGrid();
		initWindow();
		initEvent();
		if(globalVar.currencyUomId == "USD"){
	        _currencysymbol = "$";
	        _decimalseparator = ".";
	        _thousandsseparator = ",";
	    }else if(globalVar.currencyUomId == "EUR"){
	        _currencysymbol = "€";
	        _decimalseparator = ".";
	        _thousandsseparator = ",";
	    }
	};
	var initInvoiceItemNotTaxGrid = function(){
		var grid = $("#invoiceItemNotTaxGrid");
		var datafield = [{name: 'invoiceItemSeqId', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'productId', type: 'string'},
		                 {name: 'productName', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'totalAmount', type: 'number'},
		                 ];
		var columns = [{text: uiLabelMap.BACCSeqId, filterable: false, editable: false, columntype : 'number', width : '5%', sortable: false,
							cellsrenderer : function(row,column,value){
								return '<span>' + (value + 1) + '</span>'; 
	                  		}
						},
						{text: uiLabelMap.BACCInvoiceItemType, datafield: 'description',  width: '23%', editable: false},
						{text: uiLabelMap.BACCProduct, datafield: 'productName',  width: '26%', editable: false},
						{text: uiLabelMap.BACCQuantity, datafield: 'quantity', columntype: 'numberinput', width: '9%',
							cellsrenderer: function(row, colum, value){
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + value + '</span>';
								}
							},
							validation: function (cell, value) {
						        if(value < 0){
						        	return {result: false, message: uiLabelMap.ValueMustBeGreateThanZero};
						        }
						        return true;
						    },
						    cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						    	var rowData = grid.jqxGrid('getrowdata', row);
						    	updateGridCellData(grid, row, "totalAmount", rowData.amount * newvalue);
						    },
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 0});
							},
							cellclassname: function (row, columnfield, value) {
								if(globalVar.businessType == "AR"){
									return 'background-prepare';
								}
							}
						},
						{text: uiLabelMap.BACCUnitPrice, datafield: 'amount', width: '17%', columntype: 'numberinput',
							cellsrenderer: function(row, colum, value){
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + formatcurrency(value, globalVar.currencyUomId) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, max: 999999999999, digits: 12, 
									groupSeparator: _thousandsseparator, decimalSeparator: _decimalseparator, inputMode: 'advanced'});
							},
						    cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						    	var rowData = grid.jqxGrid('getrowdata', row);
						    	updateGridCellData(grid, row, "totalAmount", rowData.quantity * newvalue);
						    },
						    cellclassname: function (row, columnfield, value) {
						    	if(globalVar.businessType == "AR"){
									return 'background-prepare';
								}
							},
							aggregatesrenderer: function (aggregates, column, element) {
								var renderstring = '<div style="position: relative; margin: 6px; text-align: right; overflow: hidden;"><b>' 
													+ uiLabelMap.BACCAmountTotal + '</b></div>';
								return renderstring;
	                        }
						},
						{text: uiLabelMap.BACCTotal, datafield: 'totalAmount', width: '20%', editable: false, aggregates: ['sum'], columntype: 'numberinput',
							cellsrenderer: function(row, colum, value){
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + formatcurrency(value, globalVar.currencyUomId) + '</span>';
								}
							},
							aggregatesrenderer: function (aggregates, column, element) {
								return '<div style="color: red; position: relative; margin: 6px; text-align: right; overflow: hidden; font-size: 14px"><b>' 
										+ formatcurrency(aggregates.sum, globalVar.currencyUomId) + '<b></div>'
							}
						},
						
	    ];
		var editable = true;
		if(globalVar.businessType == "AP" && globalVar.isInvoiceHaveBilling){
			editable = false;
		}
		var config = {
		   		width: '100%', 
		   		virtualmode: false,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: editable,
		        url: '',    
		        selectionmode: 'singlecell',
		        editmode: 'click',
	   			showtoolbar: false,
	   			showstatusbar: true,
                statusbarheight: 28,
                showaggregates: true,
	        	source: {
	        		pagesize: 10,
	        	}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initInvoiceItemTaxGrid = function(){
		var datafield = [{name: 'invoiceItemSeqId', type: 'string'},
		                 {name: 'productName', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'productId', type: 'string'},
		                 {name: 'amount', type: 'number'},];
		var columns = [{text: uiLabelMap.BACCSeqId, filterable: false, editable: false, columntype : 'number', width : '5%', sortable: false,
							cellsrenderer : function(row,column,value){
								return '<span>' + (value + 1) + '</span>'; 
				      		}
						},
						{text: uiLabelMap.BACCProduct, datafield: 'productName',  width: '37%', editable: false},
						{text: uiLabelMap.accTaxAuthorityRateTypeId, datafield: 'description',  width: '38%', editable: false,
							aggregatesrenderer: function (aggregates, column, element) {
		                          var renderstring = '<div style="position: relative; margin: 6px; text-align: right; overflow: hidden; "><b>' 
		                        	  				+ uiLabelMap.BACCAmountTotal + '</b></div>';
		                          return renderstring;
		                      }
						},
						{text: uiLabelMap.BACCTotal, datafield: 'amount', width: '20%', aggregates: ['sum'], columntype: 'numberinput',
							cellsrenderer: function(row, colum, value){
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + formatcurrency(value, globalVar.currencyUomId) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, 
									max: 999999999999, digits: 12, 
									groupSeparator: _thousandsseparator, decimalSeparator: _decimalseparator, inputMode: 'advanced'});
							},
							cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
								updateDifferenceAmount();
							},
							
						    cellclassname: function (row, columnfield, value) {
								return 'background-prepare';
							},
							aggregatesrenderer: function (aggregates, column, element) {
								return '<div style="color: red; position: relative; margin: 6px; text-align: right; overflow: hidden; font-size: 14px"><b>' 
										+ formatcurrency(aggregates.sum, globalVar.currencyUomId) + '<b></div>'
							}
						},
		];
		var config = {
		   		width: '100%', 
		   		virtualmode: false,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: true,
		        url: '',    
		        selectionmode: 'singlecell',
		        editmode: 'click',
	   			showtoolbar: false,
	   			showstatusbar: true,
                statusbarheight: 28,
                showaggregates: true,
	        	source: {
	        		pagesize: 10,
	        	}
		 };
		Grid.initGrid(config, datafield, columns, null, $("#invoiceItemTaxGrid"));
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#verifyVoucherInvWindow"), 850, 580);
		$("#loadingVerifyVoucher").jqxLoader({ width: 100, height: 60, imagePosition: 'top'});
	};
	
	var initEvent = function(){
		$("#editVoucherInvoiceVerify").click(function(event){
			accutils.openJqxWindow($("#verifyVoucherInvWindow"));
		});
		$("#verifyVoucherInvWindow").on('open', function(event){
			initOpen();
		});
		$("#cancelVerifyVoucher").click(function(){
			$("#verifyVoucherInvWindow").jqxWindow('close');
		});
		$("#resetVerifyVoucher").click(function(){
			initOpen();
		});
		$("#saveVerifyVoucher").click(function(event){
			bootbox.dialog(uiLabelMap.BACCUpdateConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							updateInvoiceItem();	
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#verifyVerifyVoucher").click(function(event){
			if(_diffTaxAmount != 0){
				bootbox.dialog(uiLabelMap.InvoiceAmountTaxActualAndSystemDiff,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
				return;
			}
			if(Math.abs(_diffTotalAmount) > globalVar.invVoucherDiffValue){
				bootbox.dialog(uiLabelMap.InvoiceAmountActualAndSystemDiffNotExceed + ' ' + formatcurrency(globalVar.invVoucherDiffValue),
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
				return;
			}
			bootbox.dialog(uiLabelMap.VerifyInvoiceConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							verifyInvoiceVoucher();	
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
	};
	
	var verifyInvoiceVoucher = function(){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'verifyInvoiceVoucher',
			type: 'POST',
			data: {invoiceId: globalVar.invoiceId},
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					Loading.hide('loadingMacro');
					return;
				}
				if(globalVar.businessType == "AR"){
					window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				}else{
					window.location.href = 'ViewAPInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				}
			},
			complete:  function(jqXHR, textStatus){}
		});
	};
	
	var initOpen = function(){
		disableAll();
		$.when(
			getInvoiceVoucherAmount(),
			getInvoiceItemNotTax(),
			getInvoiceItemTax()
		).done(function(){
			enableAll();
			updateDifferenceAmount();
		});
	};
	
	var getInvoiceVoucherAmount = function(){
		return $.ajax({
			url: 'getInvoiceVoucherAmount',
			data: {invoiceId: globalVar.invoiceId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				_voucherTaxAmount = response.taxAmount;
				_voucherTotalAmount = response.amount;
				$("#amountNotTaxActualEdit").html(formatcurrency(_voucherTotalAmount, globalVar.currencyUomId));
				$("#amountTaxActualEdit").html(formatcurrency(_voucherTaxAmount, globalVar.currencyUomId));
			},
			complete:  function(jqXHR, textStatus){
			}
		});
	};
	
	var getInvoiceItemNotTax = function(){
		return $.ajax({
			url: 'getInvoiceItemExcludeTax',
			data: {invoiceId: globalVar.invoiceId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				updateLocalDataGrid($("#invoiceItemNotTaxGrid"), response.listReturn);
			}
		});
	};
	
	var getInvoiceItemTax = function(){
		return $.ajax({
			url: 'getInvoiceItemTax',
			data: {invoiceId: globalVar.invoiceId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							 {
								 "label" : uiLabelMap.CommonClose,
								 "class" : "btn-danger btn-small icon-remove open-sans",
							 }]		
					);
					return;
				}
				updateLocalDataGrid($("#invoiceItemTaxGrid"), response.listReturn);
			}
		});
	};
	
	var updateLocalDataGrid = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	
	var updateGridCellData = function(grid, rowindex, datafield, value){
		grid.jqxGrid('setcellvalue', rowindex, datafield, value);
		updateDifferenceAmount();
	};
	
	var updateDifferenceAmount = function(){
		setTimeout(function() {
			var invoiceNotTaxData = $("#invoiceItemNotTaxGrid").jqxGrid('getrows');
			var invoiceTaxData = $("#invoiceItemTaxGrid").jqxGrid('getrows');
			var totalNotTaxAmount = 0;
			var totalTaxAmount = 0;
			invoiceNotTaxData.forEach(function(rowData){
				totalNotTaxAmount += rowData.totalAmount;
			});
			invoiceTaxData.forEach(function(rowData){
				totalTaxAmount += rowData.amount;
			});
			_diffTotalAmount = Math.round((totalNotTaxAmount - _voucherTotalAmount) * 100)/100;
			_diffTaxAmount = Math.round((totalTaxAmount - _voucherTaxAmount) * 100)/100;
			$("#diffAmountNotTaxEdit").html(formatcurrency(_diffTotalAmount, globalVar.currencyUomId));
			$("#diffAmountTaxEdit").html(formatcurrency(_diffTaxAmount, globalVar.currencyUomId));
		}, 0);
	};
	
	var getData = function(){
		var data = {};
		data.invoiceId = globalVar.invoiceId;
		var invoiceNotTaxData = $("#invoiceItemNotTaxGrid").jqxGrid('getrows');
		var invoiceTaxData = $("#invoiceItemTaxGrid").jqxGrid('getrows');
		var invoiceItems = [];
		invoiceNotTaxData.forEach(function(rowData){
			invoiceItems.push({invoiceItemSeqId: rowData.invoiceItemSeqId, quantity: rowData.quantity, amount: rowData.amount});
		});
		invoiceTaxData.forEach(function(rowData){
			invoiceItems.push({invoiceItemSeqId: rowData.invoiceItemSeqId, amount: rowData.amount});
		});
		data.invoiceItems = JSON.stringify(invoiceItems);
		return data;
	};
	
	var updateInvoiceItem = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'updateInvoiceItemBaseAcc',
			data: data,
			type: 'POST', 
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					Loading.hide('loadingMacro');
					return;
				}
				if(globalVar.businessType == "AR"){
					window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				}else{
					window.location.href = 'ViewAPInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				}
			},
			complete: function(jqXHR, textStatus){}
		});
	};
	
	var disableAll = function(){
		$("#loadingVerifyVoucher").jqxLoader('open');
		$("#saveVerifyVoucher").attr("disabled", "disabled");
		$("#cancelVerifyVoucher").attr("disabled", "disabled");
		$("#verifyVoucherInvWindow .form-window-container").addClass("disabledArea");
	};
	
	var enableAll = function(){
		$("#loadingVerifyVoucher").jqxLoader('close');
		$("#saveVerifyVoucher").removeAttr("disabled");
		$("#cancelVerifyVoucher").removeAttr("disabled");
		$("#verifyVoucherInvWindow .form-window-container").removeClass("disabledArea");
	};
	var resetData = function(){
		_diffTaxAmount = 0;
		_diffTotalAmount = 0;
		_voucherTaxAmount = 0;
		_voucherTotalAmount = 0;
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	verifyInvoiceObj.init();
});