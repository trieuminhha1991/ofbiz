var addInvToPaymentObj = (function(){
	var _decimalseparator = ",";
    var _thousandsseparator = ".";
    var _currencysymbol = "đ";
	var init = function(){
		initDropDownGrid();
		initInput();
		initEvent();
		initWindow();
		initValidator();
		
	};
	var initDropDownGrid = function(){
		$("#dropDownInvGridBtn").jqxDropDownButton({width: '98%', height: 25});
		var gridEle = $("#jqxGridInvoice");
		var datafield = [{ name: 'paymentApplicationId', type: 'string' },
				     	 { name: 'paymentId', type: 'string' },
				         { name: 'invoiceId', type: 'string' },
				         { name: 'invoiceItemSeqId', type: 'string' },
				         { name: 'partyFromName', type: 'string'},
			 			 { name: 'partyName', type: 'string'},
			 			 { name: 'invoiceDate', type: 'date'},
				         { name: 'amountApplied', type: 'number' },
				         { name: 'amount', type: 'number' },
				         { name: 'amountNotApply', type: 'number' },
				         { name: 'currencyUomId', type: 'string' },];
		var fullNameField = "";
		var fullNameLabel = "";
		if(globalVar.businessType == "AP"){
			fullNameField = "partyFromName";
			fullNameLabel = uiLabelMap.BACCInvoiceFromParty;
		}else if(globalVar.businessType == "AR"){
			fullNameField = "partyName";
			fullNameLabel = uiLabelMap.BACCInvoiceToParty;
		}
		var columns = [{text: uiLabelMap.BACCInvoiceId, datafield: 'invoiceId', width: '13%',},
					   {text: fullNameLabel, datafield: fullNameField, width: '27%'},
					   {text: uiLabelMap.BACCInvoiceDate, datafield: 'invoiceDate', width: '27%', 
						 cellsformat:'dd/MM/yyyy HH:mm:ss', columntype: 'datetimeinput', filtertype: 'range'},
					   {text: uiLabelMap.BACCAmountApplied, datafield: 'amountApplied', columntype: 'numberinput', width: '21%',
						 cellsrenderer: function(row, column, value){
								if(typeof(value) == 'number'){
									var data = gridEle.jqxGrid('getrowdata', row);
									return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '<span>';
								}
							}	 
					   },
					   {text: uiLabelMap.BACCInvoiceTotal, datafield: 'amount', columntype: 'numberinput', width: '22%',
						 cellsrenderer: function(row, column, value){
								if(typeof(value) == 'number'){
									var data = gridEle.jqxGrid('getrowdata', row);
									return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '<span>';
								}
							}	 
					   }];
		
		var config = {
		   		width: 650, 
		   		rowsheight: 25,
		   		virtualmode: true,
		   		showfilterrow: false,
		   		pageable: true,
		   		sortable: true,
		        filterable: true,
		        editable: false,
		        url: '',    
	   			showtoolbar: false,
	        	source: {pagesize: 5, id: 'paymentApplicationId'}
		 };
		Grid.initGrid(config, datafield, columns, null, gridEle);
	};
	var initInput = function(){
		$("#invoiceTotalAmount").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, decimalDigits: 2, max: 9999999999, digits: 10, min: 0, disabled: true});
		$("#invoiceNotApplydAmount").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, decimalDigits: 2, max: 9999999999, digits: 10, min: 0, disabled: true});
		$("#invoiceAmountApply").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, decimalDigits: 2, max: 9999999999, digits: 10, min: 0});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addInvToPaymentWindow"), 450, 280);
	};
	var initEvent = function(){
		$("#jqxGridInvoice").on('rowclick', function (event) {
	        var args = event.args;
	        var row = $("#jqxGridInvoice").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['invoiceId'] + '</div>';
	        $("#dropDownInvGridBtn").jqxDropDownButton('setContent', dropDownContent);
	        $("#dropDownInvGridBtn").jqxDropDownButton('close');
	    });
		$("#jqxGridInvoice").on('rowselect', function (event) {
			var row = $("#jqxGridInvoice").jqxGrid('getrowdata', args.rowindex);
			setCurrencyUomId(row.currencyUomId);
			$("#invoiceTotalAmount").jqxNumberInput({groupSeparator: _thousandsseparator, decimalSeparator: _decimalseparator, symbol: _currencysymbol + ' '});
			$("#invoiceNotApplydAmount").jqxNumberInput({groupSeparator: _thousandsseparator, decimalSeparator: _decimalseparator, symbol: _currencysymbol + ' '});
			$("#invoiceAmountApply").jqxNumberInput({groupSeparator: _thousandsseparator, decimalSeparator: _decimalseparator, symbol: _currencysymbol + ' '});
			console.log(row.amount.toString().replace(".", ","));
			$("#invoiceTotalAmount").val(row.amount.toString().replace(".", ","));
			$("#invoiceNotApplydAmount").val(row.amountNotApply.toString().replace(".", ","));
		});
		$("#addInvToPaymentWindow").on('open', function(event){
			initOpen();
		});
		$("#addInvToPaymentWindow").on('close', function(event){
			resetData();
		});
		$("#cancelAddInvToPayment").click(function(event){
			$("#addInvToPaymentWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddInvToPayment").click(function(event){
			var valid = $("#addInvToPaymentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreatePaymentApplicationConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createPaymentApplication(false)
						}	
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		$("#saveAddInvToPayment").click(function(event){
			var valid = $("#addInvToPaymentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreatePaymentApplicationConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createPaymentApplication(true)
						}	
					},
					{
						"label" : uiLabelMap.CommonCancel,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	var createPaymentApplication = function(isCloseWindow){
		Loading.show('loadingMacro');
		var data = {paymentId: globalVar.paymentId};
		var rowIndexInvoice = $("#jqxGridInvoice").jqxGrid('getselectedrowindex');
		invoiceData = $("#jqxGridInvoice").jqxGrid('getrowdata', rowIndexInvoice);
		data.invoiceId = invoiceData.invoiceId;
		data.amountApplied = $("#invoiceAmountApply").val();
		$.ajax({
			url: 'createPaymentApplicationOlbius',
			type:'POST',
			data: data,
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgridPaymentInv',response.successMessage, {template : 'success', appendContainer : '#containerjqxgridPaymentInv'});
					if(isCloseWindow){
						$("#addInvToPaymentWindow").jqxWindow('close');
					}else{
						resetData();
						initOpen();
					}
					$("#jqxgridPaymentInv").jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');
			}
		});
	};
	var setCurrencyUomId = function(currencyUomId){
		if(typeof(currencyUomId) == "undefined" || currencyUomId == "VND"){
			_decimalseparator = ",";
		    _thousandsseparator = ".";
		    _currencysymbol = "đ";
		    return;
		}
		if(currencyUomId == "USD"){
	        _currencysymbol = "$";
	        _decimalseparator = ".";
	        _thousandsseparator = ",";
	    }else if(currencyUomId == "EUR"){
	        _currencysymbol = "€";
	        _decimalseparator = ".";
	        _thousandsseparator = ",";
	    }
	};
	var initValidator = function(){
		$("#addInvToPaymentWindow").jqxValidator({
			rules: [
			        { input: '#dropDownInvGridBtn', message: uiLabelMap.FieldRequired, action: 'change',
						rule: function (input, commit){
							var rowIndex = $("#jqxGridInvoice").jqxGrid('getselectedrowindex');
							if(rowIndex < 0){
								return false;
							}
							return true;
						}
					},
                    {input: '#invoiceAmountApply', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
                        rule: function (input, commit) {
                            if(input.val() <= 0){
                                return false;
                            }
                            return true;
                        }
                    },
            ]
		});
	};
	var initOpen = function(){
		setCurrencyUomId();
		var source = $("#jqxGridInvoice").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListInvoiceNotCompleteApply&paymentId=" + globalVar.paymentId  + "&businessType=" + globalVar.businessType;
		$("#jqxGridInvoice").jqxGrid('source', source);
	};
	var resetData = function(){
		Grid.clearForm($("#addInvToPaymentWindow"));
		var source = $("#jqxGridInvoice").jqxGrid('source');
		source._source.url = "";
		$("#jqxGridInvoice").jqxGrid('source', source);
		$("#jqxGridInvoice").jqxGrid('clearselection');
		$("#dropDownInvGridBtn").jqxDropDownButton('setContent', "");
		$("#addInvToPaymentWindow").jqxValidator('hide');
	};
	var openWindow = function(){
		accutils.openJqxWindow($("#addInvToPaymentWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	addInvToPaymentObj.init();
});
