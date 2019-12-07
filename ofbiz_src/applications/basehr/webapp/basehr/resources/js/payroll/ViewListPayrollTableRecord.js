var contextMenuObj = (function(){
	var init = function(){
		createJqxMenu("contextMenu", 30, 180);
		initEvent();
	};
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var payrollTableId = dataRecord.payrollTableId;
            var action = $(args).attr("action"); 
            if(action == 'calculatePayrollTable'){
            	calculatePayrollTableConfirm(rowindex);
            }else if(action == "sendRequestAppr"){
            	$("#jqxgrid").jqxGrid({disabled: true});
            	$("#jqxgrid").jqxGrid('showloadelement');
            	$.ajax({
            		url: 'sendReqApprPayrollTable',
            		data: {payrollTableId: dataRecord.payrollTableId},
            		type: 'POST',
            		success: function(response){
        				if(response._EVENT_MESSAGE_){
        					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
        						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
        					$("#jqxgrid").jqxGrid('updatebounddata');
        				}else{
        					Grid.renderMessage('jqxgrid', response._ERROR_MESSAGE_, {autoClose: true,
        						template : 'error', appendContainer: "#containerjqxgrid", opacity : 0.9});
        				}
        			},
        			complete: function(jqXHR, textStatus){
        				$("#jqxgrid").jqxGrid({disabled: false});
                    	$("#jqxgrid").jqxGrid('hideloadelement');
        			}
            	});
            }else if(action == "approve"){
            	location.href = "ViewPayrollTableRecordDetail?payrollTableId=" + dataRecord.payrollTableId;
            }
		});
		$("#contextMenu").on('shown', function (event) {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			var statusId = dataRecord.statusId;
			if(statusId == "PYRLL_TABLE_CREATED"){
				$(this).jqxMenu('disable', "calculatePayroll", false);
				$(this).jqxMenu('disable', "sendRequestAppr", true);
			}else if(statusId == "PYRLL_TABLE_CALC" || statusId == "PYRLL_TABLE_REJECT"){
				$(this).jqxMenu('disable', "calculatePayroll", false);
				$(this).jqxMenu('disable', "sendRequestAppr", false);
			}else{
				$(this).jqxMenu('disable', "calculatePayroll", true);
				$(this).jqxMenu('disable', "sendRequestAppr", true);
			}
		});
	};
	var calculatePayrollTableConfirm = function(rowindex){
		var data = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		var statusId = data.statusId;
		var payrollTableId = data.payrollTableId;
		if(statusId == 'PYRLL_TABLE_CREATED'){
			calculatePayrollRecord(payrollTableId);
			$('#jqxgrid').jqxGrid({ disabled: true});
		}else if(statusId == 'PYRLL_TABLE_CALC'){
			bootbox.dialog(uiLabelMap.PayrollCalculated_Recalculated,
				[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "icon-ok btn btn-small btn-primary open-sans",
					    "callback": function() {
					    	calculatePayrollRecord(payrollTableId);	
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
	var calculatePayrollRecord = function(payrollTableId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'calcPayroll',
			data: {payrollTableId: payrollTableId},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					window.location.href = 'ViewPayrollTableRecordDetail?payrollTableId=' + payrollTableId;
				}else{
					Grid.renderMessage('jqxgrid', response._ERROR_MESSAGE_, {autoClose: true,
						template : 'error', appendContainer: "#containerjqxgrid", opacity : 0.9});
					Loading.hide('loadingMacro');
				}
			},
			complete: function(jqXHR, textStatus){
				$('#jqxgrid').jqxGrid({ disabled: false});
				$('#jqxgrid').jqxGrid('hideloadelement');
			}
		});
	};
	return{
		init: init
	}
}());

var listPayrollExcelObj = (function () {
    var exportExcel = function () {
        var isExistData = false;
        var rows = $("#jqxgrid").jqxGrid("getrows");
        if(OlbCore.isNotEmpty(rows) && rows.length > 0){
            isExistData = true;
        }

        if (!isExistData) {
            OlbCore.alert.error("${uiLabelMap.HRPNoDataToExport}");
            return false;
        }
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", "exportListPayrollTable");

        if(OlbCore.isNotEmpty(currencyUomId)){
            var hiddenField0 = document.createElement("input");
            hiddenField0.setAttribute("type", "hidden");
            hiddenField0.setAttribute("name", "currencyUomId");
            hiddenField0.setAttribute("value", currencyUomId);
            form.appendChild(hiddenField0);
        }

        if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
            $.each(filterObjData.data, function(key, value) {
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
    return{
        exportExcel: exportExcel
    }
}());

$(document).ready(function() {
	contextMenuObj.init();
});