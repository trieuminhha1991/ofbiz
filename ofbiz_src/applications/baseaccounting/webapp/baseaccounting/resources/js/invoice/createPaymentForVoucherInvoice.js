var createPaymentObj = (function(){
	//var _normalWindowHeight = 400;
	//var _expandWindowHeight = 500;
	var _voucherIds = [];
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
		accutils.createJqxDropDownList($("#partyIdFrom"), [], {valueMember: 'partyId', displayMember: 'fullName', width: '96%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#partyIdTo"), [], {valueMember: 'partyId', displayMember: 'fullName', width: '96%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
	};
	var initDropDownBtn = function(){
		var configProduct = {
			useUrl: true,
			root: 'results',
			widthButton: '96%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'productId', type: 'string'}, {name: 'productName', type: 'string'},{name: 'productCode', type: 'string'}],
			columns: [
				{text: uiLabelMap.BACCProductId, datafield: 'productCode', width: '30%'},
				{text: uiLabelMap.BACCProductName, datafield: 'productName'}
			],
			url: 'JqxGetTaxProducts',
			useUtilFunc: true,
			key: 'productId',
			pagesize: 10,
			description: ['productName'],
		};
		accutils.initDropDownButton($("#productTaxDropDownBtn"), $("#productIdTaxGrid"), null, configProduct, []);
	};
	var initInvoiceGrid = function(){
		var grid = $("#invoiceListGrid");
		var datafield = [{name: 'invoiceId', type: 'string'},
		                 {name: 'invoiceTypeId', type: 'string'},
		                 {name: 'voucherNumber', type: 'string'},
		                 {name: 'invoiceDate', type: 'date', other:'Timestamp'},
		                 { name: 'newStatusId', type: 'string'},
		                 { name: 'total', type: 'number'},
		                 { name: 'amountApplied', type: 'number'},
		                 { name: 'paymentAmount', type: 'number'},
		                 { name: 'paymentId', type: 'string'},
						 { name: 'currencyUomId', type: 'string'},
		                 ];
		var columns = [{text: uiLabelMap.BACCInvoiceId, dataField: 'invoiceId', width: '12%', editable: false},
		               {text: uiLabelMap.BACCInvoiceTypeId, filtertype: 'checkedlist', dataField: 'invoiceTypeId', width: '18%', editable: false,
					      	  cellsrenderer: function(row, column, value){
					      		  for(var i = 0; i < globalVar.invoiceTypeArr.length; i++){
											if(value == globalVar.invoiceTypeArr[i].invoiceTypeId){
												return '<span title=' + value + '>' + globalVar.invoiceTypeArr[i].description + '</span>';
											}
					      		  }
					      		  return '<span>' + value + '</span>';
					      	  },
				 			  createfilterwidget: function (column, columnElement, widget) {
				 				 accutils.createJqxDropDownList(widget, globalVar.invoiceTypeArr, {valueMember: 'invoiceTypeId', displayMember: 'description'});
				 			  }
		               },
		               {text: uiLabelMap.VoucherNumber, datafield: 'voucherNumber', width: '20%', editable: false},
		               {text: uiLabelMap.BACCInvoiceDateSystem, dataField: 'invoiceDate', width: '20%', cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range', editable: false},
		               {text: uiLabelMap.CommonStatus, datafield: 'newStatusId', width: '14%', 
							filtertype: 'checkedlist', columntype: 'dropdownlist', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0 ; i < globalVar.statusArr.length ; i++ ){
				  					if(globalVar.statusArr[i].statusId == value){
				  						return '<span>' + globalVar.statusArr[i].description  + '</span>';
			  						}
			 					}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
								accutils.createJqxDropDownList(widget, globalVar.statusArr, {valueMember: 'statusId', displayMember: 'description'});
							}
						},
						{text: uiLabelMap.BACCTotal, dataField: 'total', width: '20%', filterable: false, editable: false,
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(data && typeof(value) == 'number'){
		                			return '<span class=align-right>'+ formatcurrency(value, data.currencyUomId)+'</span>';
		                		}
		                	}
		                },
		                {text: uiLabelMap.BACCAmountApplied, dataField: 'amountApplied', width: '20%', filterable: false, editable: false,
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(data && typeof(value) == 'number'){
		                			return '<span class=align-right>'+ formatcurrency(value, data.currencyUomId)+'</span>';
		                		}
		                	}
		                },
		                {text: uiLabelMap.BACCPaymentAmount, dataField: 'paymentAmount', width: '20%', filterable: false, cellclassname: cellclassnameAdd, 
		                	cellsalign: 'right', columntype: 'numberinput',
		                	cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
								if (!value) {
									if(data){
										var paymentAmount = data.total - data.amountApplied;
			                			return '<span class=align-right>'+ formatcurrency(paymentAmount, data.currencyUomId)+'</span>';
			                		}
								} else {
									return '<span class=align-right>'+ formatcurrency(value, data.currencyUomId)+'</span>';
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
		                {text: uiLabelMap.BACCPaymentId, dataField: 'paymentId', width: '15%', editable: false},
		               ];
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "invoiceListGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BACCListInvoices + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
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
		$("#amountNotTaxInc").jqxNumberInput({ width: '96%',  max : 999999999999999, digits: 15, decimalDigits:2, spinButtons: true, min: 0, disabled: true});
		$("#taxAmount").jqxNumberInput({ width: '96%',  max : 9999999999999, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
		$("#taxRate").jqxNumberInput({ width: '96%',  max : 100, digits: 3, decimalDigits: 1, spinButtons: true, min: 0, disabled: true,  symbolPosition: 'right', symbol: '%'});
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
						[
						 {
							 "label" : uiLabelMap.CommonClose,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
				return;
			}
			var listInvoiceHavePayment = getListInvoiceHavePayment();
			var confirmMess = "";
			if(listInvoiceHavePayment.length > 0){
				var invoiceStr = listInvoiceHavePayment.join(", ");
				confirmMess = uiLabelMap.BACCInvoice + ' ' + uiLabelMap.CommonIdCode + ' <b>' + invoiceStr + '</b> ' + uiLabelMap.BACCInvoiceIsCreatedPayment_PaymentCreateConfirm;
			}else{
				confirmMess = uiLabelMap.BACCCreatePaymentForVoucherInvoiceConfirm;
			}
			bootbox.dialog(confirmMess,
					[
					 {
						 "label" : uiLabelMap.CommonSubmit,
						 "class" : "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							createPayment();
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
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
				url: 'getInvoiceInfoByVoucher',
				type: "POST",
				data: {voucherIds: JSON.stringify(_voucherIds)},
				success: function(response) {
					  if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
									[{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);		
					  }else{
						  updateGridLocalData($("#invoiceListGrid"), response.invoiceList);
						  accutils.updateSourceDropdownlist($("#partyIdFrom"), response.partyPaidList);
						  accutils.updateSourceDropdownlist($("#partyIdTo"), response.partyReceiveList);
						  $('#amount').val(response.totalAmount);
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		$("#paymentTypeId").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if("FEE_TAX_BANK_PAYMENT" == value){
					showPaymentTaxInfo();
				}else{
					hidePaymentTaxInfo();
				}
			}
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
			hidePaymentTaxInfo();
			$('#step1').jqxValidator('hide');
			_voucherIds = [];
			updateGridLocalData($("#invoiceListGrid"), []);
			$('#fuelux-wizard').wizard('previous');
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
				{input: '#partyIdFrom', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
						rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
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
				{input: '#productTaxDropDownBtn', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var paymentTypeId = $("#paymentTypeId").val();
						if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
							var rowindex = $("#productIdTaxGrid").jqxGrid('getselectedrowindex');
							if(rowindex < 0){
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
						[
						 {
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
	
	var showPaymentTaxInfo = function(){
		$(".taxCodeContainer").show();
	};
	
	var hidePaymentTaxInfo = function(){
		$(".taxCodeContainer").hide();
		$("#taxAmount").val(0);
		$("#amountNotTax").val(0);
		$("#taxRate").val(0);
		$("#productIdTaxGrid").jqxGrid('clearselection');
		$("#productTaxDropDownBtn").jqxDropDownButton('setContent', "");
	};
	
	var getData = function(){
		var data = {};
		data.partyIdFrom = $('#partyIdFrom').val();
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
		if(paymentTypeId == "FEE_TAX_BANK_PAYMENT"){
			var productIndex = $("#productIdTaxGrid").jqxGrid('getselectedrowindex');
			var rowData = $("#productIdTaxGrid").jqxGrid('getrowdata', productIndex);
			data.productIdTaxCode = rowData.productId;
			data.taxAmount = $("#taxAmount").val();
		}
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
		var invoiceIds = [];
		rows.forEach(function(row){
			var item = {};
			item.invoiceId = row.invoiceId;
			if (row.paymentAmount) {
				item.amount = row.paymentAmount;
			} else {
				item.amount = row.total - row.amountApplied;
			}
			invoiceIds.push(item);
		});
		data.invoiceIds = JSON.stringify(invoiceIds);
		return data;
	};
	
	var createPayment = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: "createPaymentForVoucherInvoice",
			type: "POST",
			data: data,
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  } else {
					  viewListVoucherObj.reset();
					  $("#createPaymentWindow").jqxWindow('close');
					  $("#jqxgrid").jqxGrid('clearselection');
					  $("#jqxgrid").jqxGrid('updatebounddata');
					  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
	  	});
	};
	
	var openWindow = function(data){
		if(data.paymentType == "AP"){
			$("#payerPayeeInfo").html(uiLabelMap.BACCPayeeName);
			$("#createPaymentWindow").jqxWindow('setTitle', uiLabelMap.BACCCreateAPPayment);
			accutils.updateSourceDropdownlist($("#paymentTypeId"), globalVar.paymentAPTypeArr);
		}else if(data.paymentType == "AR"){
			$("#payerPayeeInfo").html(uiLabelMap.BACCPayers);
			$("#createPaymentWindow").jqxWindow('setTitle', uiLabelMap.BACCCreateARPayment);
			accutils.updateSourceDropdownlist($("#paymentTypeId"), globalVar.paymentARTypeArr);
		}
		_voucherIds = data.voucherIds;
		accutils.openJqxWindow($("#createPaymentWindow"));
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var getListInvoiceHavePayment = function(){
		var rows = $("#invoiceListGrid").jqxGrid('getrows');
		var invoiceIds = [];
		rows.forEach(function(row){
			if(row.paymentId && row.paymentId.length > 0){
				invoiceIds.push(row.invoiceId);
			}
		});
		return invoiceIds;
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
	createPaymentObj.init();
});