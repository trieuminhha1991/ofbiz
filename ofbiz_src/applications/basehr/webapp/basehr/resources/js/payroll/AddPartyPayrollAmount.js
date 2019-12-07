var addEmplIncomeObj = (function(){
	var init = function(){
		initDropDown();
		initSimpleInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerIncomeCreateNew"));
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.formulaIncomeArr, $("#incomeFormulaNew"), "code", "name", 25, "98%");
	};
	var	initSimpleInput = function(){
		$("#incomeAmountNew").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, decimalDigits: 0, max: 999999999, digits: 9, min: 0});
		$("#taxableTypeNew").jqxInput({width: '96%', height: 20, disabled: true});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#addEmplIncomeWindow"), 400, 220);
	};
	var openWindow = function(){
		openJqxWindow($("#addEmplIncomeWindow"));
	};
	var initJqxValidator = function(){
		$("#addEmplIncomeWindow").jqxValidator({
			rules: [
				{
					input: '#incomeFormulaNew', 
					message: uiLabelMap.FieldRequired, action: 'blur', 
					rule: function (input, commit) {
						if(!input.val()){
							return false;
						}
						return true;
					}
				},    
				{
					input: '#incomeAmountNew', 
					message: uiLabelMap.ValueMustBeGreateThanZero, action: 'blur', 
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
	var initEvent = function(){
		$("#addEmplIncomeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#cancelAddIncome").click(function(event){
			$("#addEmplIncomeWindow").jqxWindow('close');
		});
		$("#saveAndContinueIncome").click(function(event){
			addEmplIncome(false);
		});
		$("#saveAddIncome").click(function(event){
			addEmplIncome(true);
		});
		$("#incomeFormulaNew").on('select', function(event){
			var args = event.args;
			$("#taxableTypeNew").val("");
			if (args) {
				var index = args.index;
				var data = globalVar.formulaIncomeArr[index];
				var taxableTypeId = data.taxableTypeId
				for(var i = 0; i < globalVar.taxableTypeArr.length; i++){
					if(taxableTypeId == globalVar.taxableTypeArr[i].taxableTypeId){
						$("#taxableTypeNew").val(globalVar.taxableTypeArr[i].description);
						break;
					}
				}
			}
		});
	};
	var addEmplIncome = function(isCloseWindow){
		var valid = $("#addEmplIncomeWindow").jqxValidator('validate');
		if(!valid){
			return;
		}
		bootbox.dialog(uiLabelMap.HrCreateNewConfirm,
			[
				{
				    "label" : uiLabelMap.CommonSubmit,
				    "class" : "icon-ok btn btn-small btn-primary open-sans",
				    "callback": function() {
				    	addPayrollTableRecordPartyAmount(isCloseWindow);	
				    }
				},
				{
					  "label" : uiLabelMap.CommonCancel,
		    		   "class" : "btn-danger icon-remove btn-small open-sans",
				}
			]		
		);
	};
	var addPayrollTableRecordPartyAmount = function(isCloseWindow){
		var index = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var data = $("#jqxgrid").jqxGrid('getrowdata', index);
		var dataSubmit = {partyId: data.partyId, payrollTableId: globalVar.payrollTableId, code: $("#incomeFormulaNew").val(), amount: $("#incomeAmountNew").val()};
		$("#loadingIncomeCreateNew").show();
		$("#cancelAddIncome").attr("disabled", "disabled");
		$("#saveAddIncome").attr("disabled", "disabled");
		$("#saveAndContinueIncome").attr("disabled", "disabled");
		$.ajax({
			url: 'createPayrollTableRecordPartyAmount',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								  "label" : uiLabelMap.CommonCancel,
					    		   "class" : "btn-danger icon-remove btn-small open-sans",
							}
							]		
						);
				}else{
					if(isCloseWindow){
						$("#addEmplIncomeWindow").jqxWindow('close');
					}
					$("#gridIncome").jqxGrid('updatebounddata');
					payrollTablePtyDetailObj.setUpdateGridData(true);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingIncomeCreateNew").hide();
				$("#cancelAddIncome").removeAttr("disabled");
				$("#saveAddIncome").removeAttr("disabled");
				$("#saveAndContinueIncome").removeAttr("disabled");
			} 
		});
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

var addEmplDeductionObj = (function(){
	var init = function(){
		initDropDown();
		initSimpleInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerDeductionCreateNew"));
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.formulaDeductionArr, $("#deductionFormulaNew"), "code", "name", 25, "98%");
	};
	var	initSimpleInput = function(){
		$("#deductionAmountNew").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, decimalDigits: 0, max: 999999999, digits: 9, min: 0});
		$("#isExemptedTaxNew").jqxCheckBox({width: "98%", height: 25, disabled: true});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#addEmplDeductionWindow"), 400, 220);
	};
	var openWindow = function(){
		openJqxWindow($("#addEmplDeductionWindow"));
	};
	var initEvent = function(){
		$("#addEmplDeductionWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#cancelAddDeduction").click(function(event){
			$("#addEmplDeductionWindow").jqxWindow('close');
		});
		$("#saveAndContinueDeduction").click(function(event){
			addEmplDeduction(false);
		});
		$("#saveAddDeduction").click(function(event){
			addEmplDeduction(true);
		});
		$("#deductionFormulaNew").on('select', function(event){
			var args = event.args;
			$("#isExemptedTaxNew").jqxCheckBox({checked: false});
			if (args) {
				var index = args.index;
				var data = globalVar.formulaDeductionArr[index];
				var exempted = data.exempted;
				if("Y" == exempted){
					$("#isExemptedTaxNew").jqxCheckBox({checked: true});
				}
			}
		});
	};
	var addEmplDeduction = function(isCloseWindow){
		var valid = $("#addEmplDeductionWindow").jqxValidator('validate');
		if(!valid){
			return;
		}
		bootbox.dialog(uiLabelMap.HrCreateNewConfirm,
			[
				{
				    "label" : uiLabelMap.CommonSubmit,
				    "class" : "icon-ok btn btn-small btn-primary open-sans",
				    "callback": function() {
				    	addPayrollTableRecordPartyAmount(isCloseWindow);	
				    }
				},
				{
					  "label" : uiLabelMap.CommonCancel,
		    		   "class" : "btn-danger icon-remove btn-small open-sans",
				}
			]		
		);
	};
	var addPayrollTableRecordPartyAmount = function(isCloseWindow){
		var index = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var data = $("#jqxgrid").jqxGrid('getrowdata', index);
		var dataSubmit = {partyId: data.partyId, payrollTableId: globalVar.payrollTableId, code: $("#deductionFormulaNew").val(), amount: $("#deductionAmountNew").val()};
		$("#loadingDeductionCreateNew").show();
		$("#cancelAddDeduction").attr("disabled", "disabled");
		$("#saveAddDeduction").attr("disabled", "disabled");
		$("#saveAndContinueDeduction").attr("disabled", "disabled");
		$.ajax({
			url: 'createPayrollTableRecordPartyAmount',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								  "label" : uiLabelMap.CommonCancel,
					    		   "class" : "btn-danger icon-remove btn-small open-sans",
							}
							]		
						);
				}else{
					if(isCloseWindow){
						$("#addEmplDeductionWindow").jqxWindow('close');
					}
					$("#gridDeduction").jqxGrid('updatebounddata');	 
					payrollTablePtyDetailObj.setUpdateGridData(true);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingDeductionCreateNew").hide();
				$("#cancelAddDeduction").removeAttr("disabled");
				$("#saveAddDeduction").removeAttr("disabled");
				$("#saveAndContinueDeduction").removeAttr("disabled");
			} 
		});
	};
	var initJqxValidator = function(){
		$("#addEmplDeductionWindow").jqxValidator({
			rules: [
				{
					input: '#deductionFormulaNew', 
					message: uiLabelMap.FieldRequired, action: 'blur', 
					rule: function (input, commit) {
						if(!input.val()){
							return false;
						}
						return true;
					}
				},    
				{
					input: '#deductionAmountNew', 
					message: uiLabelMap.ValueMustBeGreateThanZero, action: 'blur', 
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
	return{
		init: init,
		openWindow: openWindow
	}
}());

var contextMenuPartyPayrollDetail = (function(){
	var init = function(){
		initContextMenu();
		initEvent();
	};
	var initEvent = function(){
		$("#contextMenuDeduction").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#gridDeduction").jqxGrid('getselectedrowindex');
			var rowid = $("#gridIncome").jqxGrid('getrowid', rowindex);
            if($(args).attr("action") == 'delete'){
            	deleteGridRow($("#gridDeduction"), rowid);
            }
		});
		$("#contextMenuIncome").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#gridIncome").jqxGrid('getselectedrowindex');
			var rowid = $("#gridIncome").jqxGrid('getrowid', rowindex);
			if($(args).attr("action") == 'delete'){
				deleteGridRow($("#gridIncome"), rowid);
			}
		});
	};
	var deleteGridRow = function(grid, rowid){
		bootbox.dialog(uiLabelMap.NotifyDelete,
			[
				{
				    "label" : uiLabelMap.CommonSubmit,
				    "class" : "icon-ok btn btn-small btn-primary open-sans",
				    "callback": function() {
				    	grid.jqxGrid('deleterow', rowid);	
				    }
				},
				{
					  "label" : uiLabelMap.CommonCancel,
		    		   "class" : "btn-danger icon-remove btn-small open-sans",
				}
			]		
		);
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenuDeduction", 30, 120, {popupZIndex: 22000});
		createJqxMenu("contextMenuIncome", 30, 120, {popupZIndex: 22000});
	};
	return{
		init: init
	}
}());

$(document).ready(function() {
	addEmplIncomeObj.init();
	addEmplDeductionObj.init();
	contextMenuPartyPayrollDetail.init();
});