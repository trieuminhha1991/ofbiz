var createARPaymentObj = (function(){
	var _orderIds = [];
	var init = function(){
		initDropDown();
		initDropDownBtn();
		initInvoiceGrid();
		initInput();
		initWindow();
		initWizard();
		initEvent();
		initValidator();
		initContextMenu();
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#paymentTypeId"), [], {valueMember: 'paymentTypeId', displayMember: 'description', width: '96%', height: 25, 
			placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#paymentMethodId"), globalVar.paymentMethodArr, {valueMember: 'paymentMethodId', displayMember: 'description', 
			width: '96%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#payCurrencyUomId"), globalVar.uomArr, {valueMember: 'uomId', displayMember: 'abbreviation', width: '91%', height: 25, disabled: true});
		accutils.createJqxDropDownList($("#partyIdTo"), globalVar.partyIdToArr, {valueMember: 'partyId', displayMember: 'fullName', width: '96%', height: 25, placeHolder: uiLabelMap.filterchoosestring, disabled: true, selectedIndex: 0});
	};
	var initDropDownBtn = function(){
		var configPartyFrom = {
			useUrl: true,
			root: 'results',
			widthButton: '96%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'partyId', type: 'string'}, {name: 'groupName', type: 'string'},{name: 'partyCode', type: 'string'}],
			columns: [
				{ text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: 150, pinned: true },
				{ text: uiLabelMap.POSupplierName, datafield: 'groupName', minwidth: 250 }
			],
			url: 'jqGetListPartySupplier',
			useUtilFunc: true,
			key: 'partyId',
			pagesize: 10,
			description: ['groupName']
		};
		accutils.initDropDownButton($("#partyIdFromDropDownBtn"), $("#partyIdFromGrid"), null, configPartyFrom, []);
	};
	var initInvoiceGrid = function(){
		var grid = $("#invoiceListGrid");
		var datafield = [{ name: 'orderId', type: 'string'},
		                 { name: 'orderDate', type: 'date', other:'Timestamp'},
		                 { name: 'receiptId', type: 'string'},
		                 { name: 'total', type: 'number'},
		                 { name: 'amountApplied', type: 'number'},
		                 { name: 'paymentAmount', type: 'number'},
		                 { name: 'paymentId', type: 'string'},
		                 { name: 'bankName', type: 'string'}
		                 ];
		var columns = [ {text: uiLabelMap.BACCOrderId, dataField: 'orderId', width: '15%', editable: false},
		                {text: uiLabelMap.BSOrderDate, dataField: 'orderDate', width: '20%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range', editable: false, hidden: true},
		                {text: uiLabelMap.BPOSReceiptId, dataField: 'receiptId', width: '15%', editable: false},
		                {text: uiLabelMap.BACCBankName, dataField: 'bankName', width: '22%', editable: false},
						{text: uiLabelMap.BACCTotal, dataField: 'total', width: '16%', filterable: false, editable: false,
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(data && typeof(value) == 'number'){
		                			return '<span class=align-right>'+ formatcurrency(value)+'</span>';
		                		}
		                	}
		                },
		                {text: uiLabelMap.BACCAmountApplied, dataField: 'amountApplied', width: '16%', filterable: false, editable: false,
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(data && typeof(value) == 'number'){
		                			return '<span class=align-right>'+ formatcurrency(value)+'</span>';
		                		}
		                	}
		                },
		                {text: uiLabelMap.BACCPaymentAmount, dataField: 'paymentAmount', filterable: false, cellclassname: cellclassnameAdd, width: '16%',
		                	cellsalign: 'right', columntype: 'numberinput',
		                	cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
								if (!value) {
									if(data){
										var paymentAmount = data.total - data.amountApplied;
			                			return '<span class=align-right>'+ formatcurrency(paymentAmount)+'</span>';
			                		}
								} else {
									return '<span class=align-right>'+ formatcurrency(value)+'</span>';
								}
		                	},
		                	initeditor: function (row, cellvalue, editor) {
		    					editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 2 });
		    					if (!cellvalue) {
		    						var data = grid.jqxGrid('getrowdata', row);
		    						if (data) {
		    							var paymentAmount = data.total - data.amountApplied;
		    							grid.jqxGrid('setcellvalue', row, "paymentAmount", paymentAmount);
		    						}
		    					}
		    				}, 
		    				validation: function (cell, value) {
		    					var data = grid.jqxGrid('getrowdata', cell.row);
		    					var paymentAmount = data.total - data.amountApplied;
		    					if (value < 0 || value > paymentAmount) {
		    						return { result: false, message: uiLabelMap.BACCCheckPaymentAmount};
		    					}
		    					return true;
		    				}
		                },
		                {text: uiLabelMap.BACCPaymentId, dataField: 'paymentId', width: '24%', editable: false, hidden: true},
		              ];
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "invoiceListGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BACCListOrder + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};	
		
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				pageable: true,
				sortable: true,
				source: {
					pagesize: 10,
					localdata: [],
				}
		};
	    Grid.initGrid(config, datafield, columns, null, grid);
	    Grid.createContextMenu(grid, $("#invoiceCtxMenu"), false);
	};
	
	var initInput = function(){
		$("#amount").jqxNumberInput({ width: '100%',  max : 999999999999999, digits: 13, decimalDigits:2, spinButtons: true, min: 0, disabled: true});
		$("#effectiveDate").jqxDateTimeInput({width: '96%', height: 25});
		
		$("#organizationName").jqxInput({width: '94%', height: 22});
		$("#identifyCard").jqxInput({width: '94%', height: 22});
		$("#issuedPlace").jqxInput({width: '94%', height: 22});
		$("#issuedDate").jqxDateTimeInput({width: '96%', height: 25, showFooter: true});
		$('#issuedDate').val(null);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#createPaymentWindow"), 900, 530);
	};
	var initWizard = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.direction == "next") {
				var valid = $("#step1").jqxValidator('validate');
				if(!valid){
					return false;
				}
			}else if(info.direction == "previous"){
				
			}
		}).on('finished', function(e) {
			var rows = $("#invoiceListGrid").jqxGrid('getrows');
			if(rows.length == 0){
				bootbox.dialog(uiLabelMap.BACCListInvoiceIsEmpty,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					}]		
				);
				return;
			}
			bootbox.dialog(uiLabelMap.BACCCreatePaymentForVoucherInvoiceConfirm,
				[{
					 "label" : uiLabelMap.CommonSubmit,
					 "class" : "btn-primary btn-small icon-ok open-sans",
					 "callback": function() {
						createPayment();
					 }
				 },
				 {
					 "label" : uiLabelMap.CommonCancel,
					 "class" : "btn-danger btn-small icon-remove open-sans",
				 }]		
			);
		}).on('stepclick', function(e){
 			//return false;//prevent clicking on steps
 		});
	};
	var initEvent = function(){
		$('#createPaymentWindow').on('open', function(event){
			$("#effectiveDate").val(new Date());
			if(globalVar.preferenceCurrencyUom){
				$("#payCurrencyUomId").val(globalVar.preferenceCurrencyUom);
			}
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getInvoiceInfoByBank',
				type: "POST",
				data: {orderIds: JSON.stringify(_orderIds)},
				success: function(response) {
					  if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
									[{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);		
					  } else {
						  updateGridLocalData($("#invoiceListGrid"), response.invoiceList);
						  $('#amount').val(response.totalAmount);
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		
		$('#createPaymentWindow').on('close', function(e){
			$('#paymentTypeId').jqxDropDownList('clearSelection');
			$('#paymentMethodId').jqxDropDownList('clearSelection');
			$('#amount').val('');
			$('#comments').val("");
			$('#organizationName').val("");
			$('#identifyCard').val("");
			$('#issuedPlace').val("");
			$('#issuedDate').val(null);
			$('#step1').jqxValidator('hide');
			_orderIds = [];
			updateGridLocalData($("#invoiceListGrid"), []);
			$('#fuelux-wizard').wizard('previous');
			$("#partyIdFromHidden").val("");
		});
		$('#invoiceListGrid').on('cellendedit', function (event){
		    var args = event.args;
		    var dataField = event.args.datafield;
		    var rowBoundIndex = event.args.rowindex;
		    var value = args.value;
		    var oldvalue = args.oldvalue;
		    var rowData = args.row;
		    if (dataField == 'paymentAmount') {
		    	var allData = $('#invoiceListGrid').jqxGrid('getrows');
		    	var total = 0;
		    	for (var i = 0; i < allData.length; i++) {
		    		if (allData[i].paymentAmount) {
		    			total += allData[i].paymentAmount;
		    		} else {
		    			total += allData[i].total - allData[i].amountApplied;
		    		}
		    	}
		    	$('#amount').val(total);
		    }
		});
	};
	
	var initValidator = function(){
		$("#step1").jqxValidator({
			rules: [
				{input: '#partyIdTo', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
				{input: '#paymentTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
				{input: '#paymentMethodId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
				{input: '#amount', message: uiLabelMap.ValueMustBeGreateThanZero , action: 'keyup, change', 
					rule: function (input, commit) {
				       if(input.val() >= 0){
				    	   return true;
				       }
				       return false;
					}
				},
				{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var identifyCard = $('#identifyCard').val();
						if(identifyCard.length > 0){
							if(!$(input).val()){
								return false;
							}
						}
						return true;
					}
				},
				{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var issuedDate = $('#issuedDate').val();
						if(issuedDate.length > 0){
							if(!$(input).val()){
								return false;
							}
						}
						return true;
					}
				},
				{input: '#organizationName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var issuedPlace = $('#issuedPlace').val();
						if(issuedPlace.length > 0){
							if(!$(input).val()){
								return false;
							}
						}
						return true;
					}
				},
				{input: '#effectiveDate', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
			]
		});
	};
	
	var initContextMenu = function(){
		accutils.createJqxMenu("invoiceCtxMenu", 30, 130, {popupZIndex: 22000});
		var getInvoiceTotalAmount = function(){
			var rows = $("#invoiceListGrid").jqxGrid('getrows');
			var total = 0;
			rows.forEach(function(row){
				total += row.total;
			});
			return total;
		};
		$("#invoiceCtxMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#invoiceListGrid").jqxGrid('getselectedrowindex');
			var data = $("#invoiceListGrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == 'delete'){
				bootbox.dialog(uiLabelMap.BACCUpdatePaymentAmountAfterDelete,
					[{
						 "label": uiLabelMap.CommonYes,
						 "class": "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							 $("#invoiceListGrid").jqxGrid('deleterow', data.uid);
							 var totalAmount = getInvoiceTotalAmount();
							 $("#amount").val(totalAmount);
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonNo,
						 "class" : "btn-yellow- btn-small icon-fire open-sans",
						 "callback": function() {
							 $("#invoiceListGrid").jqxGrid('deleterow', data.uid);
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 },
					 ]		
				);
			}
		});
	};
	
	var getData = function(){
		var data = {};
		data.partyIdFrom = $("#partyIdFromHidden").val();
		data.partyIdTo = $('#partyIdTo').val();
		data.paymentTypeId = $('#paymentTypeId').val();
		data.paymentMethodId = $('#paymentMethodId').val();
		data.amount = $('#amount').val();
		data.currencyUomId = $('#payCurrencyUomId').val();
		data.statusId = 'PMNT_NOT_PAID';
		data.comments = $('#comments').val();
		var effectiveDate = $("#effectiveDate").jqxDateTimeInput('val', 'date');
		data.effectiveDate = effectiveDate.getTime();
		var paymentTypeId = $("#paymentTypeId").val();
		if($("#organizationName").val()){
			data.organizationName = $('#organizationName').val();
			if($("#identifyCard").val()){
				data.identifyCard = $('#identifyCard').val();
			}
			if($("#issuedPlace").val()){
				data.issuedPlace = $('#issuedPlace').val();
			}
			if($("#issuedDate").jqxDateTimeInput('val', 'date')){
				data.issuedDate = $("#issuedDate").jqxDateTimeInput('val', 'date').getTime();
			}
		}
		var rows = $("#invoiceListGrid").jqxGrid('getrows');
		var receiptIds = [];
		rows.forEach(function(row){
			var item = {};
			item.receiptId = row.receiptId;
			if (row.paymentAmount) {
				item.amount = row.paymentAmount;
			} else {
				item.amount = row.total - row.amountApplied;
			}
			receiptIds.push(item);
		});
		data.receiptIds = JSON.stringify(receiptIds);
		return data;
	};
	
	var createPayment = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: "createPaymentForBank",
			type: "POST",
			data: data,
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					  );		
				  } else {
					  OrderReceiptNote.reset();
					  $("#createPaymentWindow").jqxWindow('close');
					  Grid.renderMessage('jqxgridListOrderReceiptNote', response.successMessage, {template : 'success', appendContainer : '#containerjqxgridListOrderReceiptNote'});
					  $("#jqxgridListOrderReceiptNote").jqxGrid('clearselection');
					  $("#jqxgridListOrderReceiptNote").jqxGrid('updatebounddata');
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
	  	});
	};
	
	var openWindow = function(data){
		$("#payerPayeeInfo").html(uiLabelMap.BACCPayers);
		$("#createPaymentWindow").jqxWindow('setTitle', uiLabelMap.BACCCreateARPayment);
		accutils.updateSourceDropdownlist($("#paymentTypeId"), globalVar.paymentARTypeArr);
		accutils.setValueDropDownButtonOnly($("#partyIdFromDropDownBtn"), data.bankId, data.bankName);
		$("#partyIdFromHidden").val(data.bankId);
		$("#partyIdFromDropDownBtn").jqxDropDownButton('disabled', true);
		$("#paymentTypeId").val("POS_PAIDIN_BANK");
		_orderIds = data.orderIds;
		accutils.openJqxWindow($("#createPaymentWindow"));
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var cellclassnameAdd = function (row, column, value, data) {
    	if (column == 'paymentAmount') {
			return 'background-prepare';
    	} 
	}
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	createARPaymentObj.init();
});