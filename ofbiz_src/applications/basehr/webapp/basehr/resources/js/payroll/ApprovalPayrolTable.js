var approvalPayrollObj = (function(){
	var init = function(){
		initSimpleInput();
		initDropDown();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerApproval"));
	};
	var initSimpleInput = function(){
		$("#payrollTableName").jqxInput({width: '96%', height: 20, disabled: true});
		$("#totalOrgPaidAmount").jqxNumberInput({ width: '97%', height: '25px', spinButtons: true, decimalDigits: 0, digits: 12, max: 99999999999, disabled: true});
		$("#totalAcutalReceiptAmount").jqxNumberInput({ width: '97%', height: '25px', spinButtons: true, decimalDigits: 0, digits: 12, max: 99999999999, disabled: true});
	};
	var initDropDown = function(){
		var data = [{type: 'ACCEPT', description: uiLabelMap.CommonApprove},
		            {type: 'REJECT', description: uiLabelMap.CommonReject},];
		createJqxDropDownList(data, $("#approvalDropDown"), "type", "description", 25, "97%");
	};
	var initJqxEditor = function(){
		$("#changeReasonAppr").jqxEditor({ 
    		width: '97%',
            theme: 'olbiuseditor',
            tools: '',
            height: 120,
        });
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		};
		createJqxWindow($("#payrollTableApprWindow"), 500, 390, initContent);
	};
	var openWindow = function(){
		openJqxWindow($("#payrollTableApprWindow"));
	};
	var initEvent = function(){
		$("#payrollTableApprWindow").on('open', function(event){
			$("#approvalDropDown").jqxDropDownList({selectedIndex: 0});
			$("#payrollTableName").val(globalVar.payrollTableName);
			$("#totalOrgPaidAmount").val(globalVar.totalOrgPaid);
			$("#totalAcutalReceiptAmount").val(globalVar.totalAcutalReceipt);
		});
		$("#payrollTableApprWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#cancelAppr").click(function(event){
			$("#payrollTableApprWindow").jqxWindow('close');
		});
		$("#saveAppr").click(function(event){
			bootbox.dialog(uiLabelMap.AreYouSureWantToApproval,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							approvalPayrollTable();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	var approvalPayrollTable = function(){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'approvalPayrollTable',
			data: {approvalType: $("#approvalDropDown").val(), payrollTableId: globalVar.payrollTableId, changeReason: $("#changeReasonAppr").jqxEditor('val')},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					$("#payrollTableApprWindow").jqxWindow('close');
					location.reload();
				}else{
					Loading.hide('loadingMacro');
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete:  function(jqXHR, textStatus){
				//$("#loadingApproval").hide();
			}
		});
	};
	var openWindow = function(){
		openJqxWindow($("#payrollTableApprWindow"));
	};
	return{
		openWindow: openWindow,
		init: init
	};
}());
$(document).ready(function(){
	approvalPayrollObj.init();
});