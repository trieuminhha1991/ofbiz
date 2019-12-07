var orderReqVATInvObj = (function() {
	var orderIdSelected;
	
	var init = function() {
		initContextMenu();
		initInput();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	
	var initInput = function() {
		$("#orderIdVATEdit").jqxInput({width: '93%', height: 22, disabled: true});
		$("#customerNameVATEdit").jqxInput({width: '93%', height: 22});
		$("#companyNameVATEdit").jqxInput({width: '93%', height: 22});
		$("#taxInfoIdVATEdit").jqxInput({width: '93%', height: 22});
		$("#bankIdVATEdit").jqxInput({width: '93%', height: 22});
		
		var voucherF = new Array("01GTKT", "02GTTT", "06HDXK", "07KPTQ", "03XKNB","04HGDL", "01BLP", "02BLP2", "HĐ-BACHHOA", "HDBACHHOA", "TNDN", "PLHD", "THUEMB", "C1-02/NS");
		$("#voucherForm").jqxInput({width: '93%', height: 22, source: voucherF, theme:'energyblue'});
		
		var voucherS = new Array("AB/17P", "SC/17P", "AA/16P", "AB/16P", "BK/01","AA/17E", "TT/16P", "ND/16P", "AB/18P", "AB/19P", "SC/18P", "SC/19P", "AA/17P", "AA/18P", "AA/19P");
		$("#voucherSerial").jqxInput({width: '93%', height: 22, source: voucherS, theme:'energyblue'});
		
		$("#voucherNumber").jqxFormattedInput({width: '93%', height: 22, value: ''});
		$("#issuedDate").jqxDateTimeInput({width: '95%', height: 25});
	};
	
	var initDropDown = function() {
		accutils.createJqxDropDownList($("#enumPaymentMethodVATEdit"), globalVar.enumPaymentMethodArr, {valueMember: 'enumId', displayMember: 'description', width: '95%', height: 25});
	};
	
	var initWindow = function() {
		accutils.createJqxWindow($("#editOrderInvoiceNoteInfoWindow"), 470, 440);
		accutils.createJqxWindow($("#createdInvoiceVATWindow"), 480, 255);
	};
	
	var openWindowEdit = function(data) {
		accutils.openJqxWindow($("#editOrderInvoiceNoteInfoWindow"));
		$("#orderIdVATEdit").val(data.orderId);
		$("#customerNameVATEdit").val(data.customerName);
		$("#companyNameVATEdit").val(data.companyName);
		$("#taxInfoIdVATEdit").val(data.taxInfoId);
		$("#bankIdVATEdit").val(data.bankId);
		$("#addressVAREdit").val(data.address);
		$("#enumPaymentMethodVATEdit").val(data.paymentMethod);
	};
	
	var openWindowVoucher = function() {
		accutils.openJqxWindow($("#createdInvoiceVATWindow"));
		$("#voucherForm").val("");
		$("#voucherSerial").val("");
		$("#voucherNumber").val("");
		$("#issuedDate").val(new Date());
	};
	
	var initEvent = function(){
		$("#enumPaymentMethodVATEdit").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item;
				var value = item.value;
				if (value == "POSNoteTienGuiNganHang") {
					$("#bankIdVATEdit").jqxInput({disabled: false});
				} else if (value == "POSNoteTienMat") {
					$("#bankIdVATEdit").jqxInput({disabled: true});
				}
			}
		});
		
		$("#editOrderInvoiceNoteInfoWindow").on('close', function(event){
			Grid.clearForm($("#editOrderInvoiceNoteInfoWindow"));
		});
		
		$("#createdInvoiceVATWindow").on('close', function(event){
			Grid.clearForm($("#createdInvoiceVATWindow"));
		});
		
		$("#cancelCreateOrderInvoiceNoteEdit").click(function(e){
			$("#editOrderInvoiceNoteInfoWindow").jqxWindow('close');
		});
		
		$("#cancelCreatedInvoiceVAT").click(function(e){
			$("#createdInvoiceVATWindow").jqxWindow('close');
		});
		
		$("#saveCreateOrderInvoiceNoteEdit").click(function(e){
			var valid = $("#editOrderInvoiceNoteInfoWindow").jqxValidator('validate');
			if (!valid) {
				return;
			}
			bootbox.dialog(uiLabelMap.BACCUpdateConfirm,
				[
				 	{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							updateOrderInvoiceNote();
						}
					},
					{
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					}
				]		
			);
		});
		
		$("#saveCreatedInvoiceVAT").click(function(e){
			var valid = $("#createdInvoiceVATWindow").jqxValidator('validate');
			if (!valid) {
				return;
			}
			bootbox.dialog(uiLabelMap.SetOrderIsCreatedVATInvoiceConfirm,
				[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							updateOrderIsCreatedVATInvoice(orderIdSelected);
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
	
	var updateOrderInvoiceNote = function(){
		Loading.show('loadingMacro');
		var data = getData(); 
		$.ajax({
			url: 'updateOrderInvoiceNote',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }]);
					return;
				}
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#editOrderInvoiceNoteInfoWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
				$("#jqxgrid").jqxGrid('clearselection');
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var getData = function(){
		var data = {};
		data.orderId = $("#orderIdVATEdit").val();
		data.customerName = $("#customerNameVATEdit").val();
		data.companyName = $("#companyNameVATEdit").val();
		if($("#taxInfoIdVATEdit").val()){
			data.taxInfoId = $("#taxInfoIdVATEdit").val();
		}
		data.address = $("#addressVAREdit").val();
		data.paymentMethod = $("#enumPaymentMethodVATEdit").val();
		if($("#bankIdVATEdit").val()){
			data.bankId = $("#bankIdVATEdit").val();
		}
		return data;
	};
	
	var initValidator = function(){
		$("#editOrderInvoiceNoteInfoWindow").jqxValidator({
			rules: [
				{ input: '#customerNameVATEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#companyNameVATEdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#addressVAREdit', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
			]
		});
		
		$("#createdInvoiceVATWindow").jqxValidator({
			rules: [
				{ input: '#voucherForm', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#voucherSerial', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#voucherNumber', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#issuedDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				} 
			]
		});
	};
	
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 200);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var action = $(args).attr("action");
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			if (action == "setCreatedVATInv") {
				orderIdSelected = dataRecord.orderId;
				openWindowVoucher();
			} else if (action == "editVATInv") {
				openWindowEdit(dataRecord);
			}
		});
		
		$("#contextMenu").on('shown', function (event) {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			var isCreatedVatInv = dataRecord.isCreatedVatInv;
			if (isCreatedVatInv) {
				$(this).jqxMenu('disable', "setCreatedVATInv", true);
			} else {
				$(this).jqxMenu('disable', "setCreatedVATInv", false);
			}
		});
	};
	
	var updateOrderIsCreatedVATInvoice = function(orderId){
		$("#jqxgrid").jqxGrid({disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'updateOrderIsCreatedVATInvoice',
			data: {
				orderId: orderId,
				voucherForm: $("#voucherForm").val(),
				voucherNumber: $("#voucherNumber").val(),
				voucherSerial: $("#voucherSerial").val(),
				issuedDate: $("#issuedDate").jqxDateTimeInput('val', 'date').getTime()
			},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }]);
					return;
				}
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#createdInvoiceVATWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
			},
			complete: function(){
				$("#jqxgrid").jqxGrid({disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
			}
		});
	};
	
	return{
		init: init
	}
}());

$(document).ready(function(){
	orderReqVATInvObj.init();
});