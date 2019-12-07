var editInvVoucherDiffValueObj = (function(){
	var init = function(){
		initInput();
		initWindow();
		initEvent();
		$("#jqxNotificationjqxgrid").jqxNotification({ width: "100%", appendContainer: "#containerjqxgrid", opacity: 1, autoClose: true, template: "success" });
	};
	var initInput = function(){
		$("#editInvVoucherDiffValue").jqxNumberInput({width: '92%', height: 25, spinButtons: true, decimalDigits: 2});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editInvVoucherDiffValueWindow"), 400, 140);
	};
	var initEvent = function(){
		$("#editInvVoucherDiffValueWindow").on('open', function(e){
			if(globalVar.hasOwnProperty("invVoucherDiffValue")){
				$("#editInvVoucherDiffValue").val(globalVar.invVoucherDiffValue);
			}
		});
		$("#editInvVoucherDiffValueWindow").on('close', function(e){
			$("#editInvVoucherDiffValue").val(0);
		});
		$("#editInvVoucherDiffValueBtn").click(function(e){
			accutils.openJqxWindow($("#editInvVoucherDiffValueWindow"));
		});
		$("#editInvVoucherDiffValueWindow").jqxValidator({
			rules: [
				{input: '#editInvVoucherDiffValue', message: uiLabelMap.BACCValueMustGreaterOrEqualThanZero, action: 'keyup, change', 
					rule: function (input, commit) {
						var value = input.val();
						if(value < 0){
							return false;
						}
						return true;
					}
				},
			]
		});
		$("#cancelEditInVoicherDiffValue").click(function(e){
			$("#editInvVoucherDiffValueWindow").jqxWindow('close');
		});
		$("#saveEditInVoicherDiffValue").click(function(e){
			var valid = $("#editInvVoucherDiffValueWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.BSAreYouSureYouWantToUpdate,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							updateVoucherInvoiceDiffValueConfig();
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
	var updateVoucherInvoiceDiffValueConfig = function(){
		Loading.show('loadingMacro');
		var amount = $("#editInvVoucherDiffValue").val();
		$.ajax({
			url: 'updateVoucherInvoiceDiffValue',
			type: 'POST',
			data: {systemValue: amount},
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
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#editInvVoucherDiffValueWindow").jqxWindow('close');
				globalVar.invVoucherDiffValue = amount;
				var systemValue = "";
				if(amount > 0){
					systemValue = "&le; ";
				}
				systemValue += formatcurrency(amount);
				$("#editInvVoucherDiffValueDiv").html(systemValue);
			},
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	editInvVoucherDiffValueObj.init();
});
