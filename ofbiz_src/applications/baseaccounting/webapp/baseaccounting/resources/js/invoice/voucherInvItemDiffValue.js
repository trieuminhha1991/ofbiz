var addInvoiceItemDiffValue = (function(){
	var init = function(){
		initInput();
		initWindow();
		initEvent();
	};
	var initInput = function(){
		$("#amountIITDiffValue").jqxNumberInput({ width: '96%',  max: 999999999, digits: 9, decimalDigits:2, spinButtons: true});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addIIDiffValueWindow"), 400, 180);
	};
	var initEvent = function(){
		$("#addInvoiceItemDiffBtn").click(function(e){
			accutils.openJqxWindow($("#addIIDiffValueWindow"));
		});
		$("#addIIDiffValueWindow").on('open', function(event){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getInvoiceAndVoucherDiffAmount',
				data: {invoiceId: globalVar.invoiceId},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "error"){
						bootbox.dialog(response.errorMessage,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
						return;
					}
					$("#amountIITDiffValue").val(-response.diffInvoiceAmount)
				},
				complete: function(){
					Loading.hide('loadingMacro');	
				}
			});
		});
		$("#addIIDiffValueWindow").on('close', function(e){
			$("#amountIITDiffValue").val(0);
		});
		$("#cancelNewInvoiceItemDiffValue").click(function(e){
			$("#addIIDiffValueWindow").jqxWindow('close');
		});
		
		$("#saveNewInvoiceItemDiffValue").click(function(e){
			bootbox.dialog(uiLabelMap.BACCCreatePOInvItemAdjustmentConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createPOInvoiceItemTypeAdj();	
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
	var createPOInvoiceItemTypeAdj = function(){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'createPOInvoiceItemTypeAdj',
			data: {invoiceId: globalVar.invoiceId, amount: $("#amountIITDiffValue").val()},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					Loading.hide('loadingMacro');
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				if(globalVar.businessType == "AR"){
					window.location.href = 'ViewARInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				}else{
					window.location.href = 'ViewAPInvoice?invoiceId=' + globalVar.invoiceId + '&active=voucher-appl';
				}
			},
			complete: function(){
				
			}
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	addInvoiceItemDiffValue.init();
});