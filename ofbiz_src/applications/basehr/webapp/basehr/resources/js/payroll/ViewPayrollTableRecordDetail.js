var payrollTableRecordDetailObj = (function(){
	var init = function(){
		initDropDownList();
		initJqxWindow();
		create_spinner($("#spinnerRounding"));
		initEvent();
	};
	var calculatePayrollTable = function(){
		if(globalVar.statusId == 'PYRLL_TABLE_CREATED'){
			calculatePayrollRecord();
		}else if(globalVar.statusId == 'PYRLL_TABLE_CALC'){
			bootbox.dialog(uiLabelMap.PayrollCalculated_Recalculated,
				[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "icon-ok btn btn-small btn-primary open-sans",
					    "callback": function() {
					    	calculatePayrollRecord();	
					    }
					},
					{
						  "label" : uiLabelMap.CommonCancel,
			    		   "class" : "btn-danger icon-remove btn-small open-sans",
					}
				]		
			);
		}
	};
	var calculatePayrollRecord = function(){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'calcPayroll',
			data: {payrollTableId: globalVar.payrollTableId},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					/*Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});*/
					//$("#jqxgrid").jqxGrid('updatebounddata');
					location.reload();
				}else{
					Grid.renderMessage('jqxgrid', response._ERROR_MESSAGE_, {autoClose: true,
						template : 'error', appendContainer: "#containerjqxgrid", opacity : 0.9});
					Loading.hide('loadingMacro');
				}
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	var sendRequestApproval = function(){
		Loading.show('loadingMacro');
		$.ajax({
    		url: 'sendReqApprPayrollTable',
    		data: {payrollTableId: globalVar.payrollTableId},
    		type: 'POST',
    		success: function(response){
				if(response._EVENT_MESSAGE_){
					location.reload();
				}else{
					Grid.renderMessage('jqxgrid', response._ERROR_MESSAGE_, {autoClose: true,
						template : 'error', appendContainer: "#containerjqxgrid", opacity : 0.9});
					Loading.hide('loadingMacro');
				}
			},
			complete: function(jqXHR, textStatus){
			}
    	});
	};
	var roundingNumber = function(){
		openJqxWindow($("#roundingAmountWindow"));
	};
	var initDropDownList = function(){
		 var localdata = [
		                  {name: uiLabelMap.RoundingThousand, value: 3},
		                  {name: uiLabelMap.RoundingHundreds, value: 2},
		                  {name: uiLabelMap.RoundingTens, value: 1},
		                  {name: uiLabelMap.RoundingUnit, value: 0},
		                  ];
		 createJqxDropDownList(localdata, $("#roundingAmountNbr"), "value", "name", 25, '97%');
	};
	var initJqxWindow = function(){
		createJqxWindow($("#roundingAmountWindow"), 350, 150);
	};
    var exportExcelPayrollDetail = function(payrollTableId) {
        var payrollTableId = payrollTableId;
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", "exportExcelPayrollDetail");
        form.setAttribute("target", "_blank");
        var hiddenField0 = document.createElement("input");
        hiddenField0.setAttribute("type", "hidden");
        hiddenField0.setAttribute("name", "payrollTableId");
        hiddenField0.setAttribute("value", payrollTableId);
        form.appendChild(hiddenField0);
        if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
            $.each(filterObjData.data, function (key, value) {
                var hiddenField1 = document.createElement("input");
                hiddenField1.setAttribute("type", "hidden");
                hiddenField1.setAttribute("name", key);
                hiddenField1.setAttribute("value", value);
                form.appendChild(hiddenField1);
            });
        }
        document.body.appendChild(form);
        form.submit();
    };
	var initEvent = function(){
		$("#roundingAmountWindow").on('open', function(event){
			$("#roundingAmountNbr").jqxDropDownList({selectedIndex: 0});
		});
		$("#saveRounding").click(function(event){
			$("#saveRounding").attr("disabled", "disabled");
			$("#cancelRounding").attr("disabled", "disabled");
			$("#loadingRounding").show();
			$.ajax({
				url: 'roundingAmountPayrollTableRecord',
				data: {payrollTableId: globalVar.payrollTableId, roundingNumber: $("#roundingAmountNbr").val()},
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
    					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
    						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
    					$("#jqxgrid").jqxGrid('updatebounddata');
    					$("#roundingAmountWindow").jqxWindow('close');
    				}else{
    					bootbox.dialog(response._ERROR_MESSAGE_,
    							[
    								{
    									  "label" : uiLabelMap.CommonCancel,
    						    		   "class" : "btn-danger icon-remove btn-small open-sans",
    								}
    							]		
    						);
    				}
				},
				complete: function(jqXHR, textStatus){
					$("#saveRounding").removeAttr("disabled");
					$("#cancelRounding").removeAttr("disabled");
					$("#loadingRounding").hide();
				}
			});
		});
		$("#cancelRounding").click(function(event){
			$("#roundingAmountWindow").jqxWindow('close');
		});
	};
	return{
		calculatePayrollTable: calculatePayrollTable,
		sendRequestApproval: sendRequestApproval,
		roundingNumber: roundingNumber,
        exportExcelPayrollDetail: exportExcelPayrollDetail,
		init: init
	}
}());

var contextMenuObj = (function(){
	var init = function(){
		createJqxMenu("contextMenu", 30, 140);
		initEvent();
	};
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var payrollTableId = dataRecord.payrollTableId;
            if($(args).attr("action") == 'detail'){
            	payrollTablePtyDetailObj.openWindow();
            	payrollTablePtyDetailObj.setData(dataRecord);
            }
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function() {
	contextMenuObj.init();
	payrollTableRecordDetailObj.init();
});