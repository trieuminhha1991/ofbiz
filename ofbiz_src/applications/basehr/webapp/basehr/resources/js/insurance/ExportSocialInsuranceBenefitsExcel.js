var exportSocicalInsBenefitObj = (function(){
	var _data = {};
	var init = function(){
		initJqxDropDownList();
		initJqxWindow();
		initEvent();
		initJqxValidator();
	};
	var initEvent = function(){
		$("#cancelExcelExport").click(function(event){
			$("#ExportInsBenefitExcelWindow").jqxWindow('close');
		});
		$("#saveExcelExport").click(function(event){
			var valid = $("#ExportInsBenefitExcelWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var customTimePeriodId = $("#monthCustomTimeExcel").val();
			var sequenceNum = $("#sequenceNumInsDecl").val();
			var insuranceContentTypeId = $("#insuranceTypeReport").val();
			window.location.href = "exportInsuranceExcel?customTimePeriodId=" + customTimePeriodId + "&sequenceNum=" + sequenceNum + "&insuranceContentTypeId=" + insuranceContentTypeId ;
			$("#ExportInsBenefitExcelWindow").jqxWindow('close');
		});
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(yearCustomTimePeriod, $("#yearCustomTimeExcel"), "customTimePeriodId", "periodName", 25, 150);
		createJqxDropDownList([], $("#monthCustomTimeExcel"), "customTimePeriodId", "periodName", 25, 150);
		createJqxDropDownList(globalVar.insuranceContentTypeArr, $("#insuranceTypeReport"), "insuranceContentTypeId", "description", 25, '99%');
		createJqxDropDownList([], $("#sequenceNumInsDecl"), "sequenceNum", "sequenceNum", 25, 150);
		$("#monthCustomTimeExcel").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$.ajax({
					url: 'getInsAllowancePaymentDeclSeqNum',
					data: {customTimePeriodId: value},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							if(response.listReturn && response.listReturn.length > 0){
								updateSourceDropdownlist($("#sequenceNumInsDecl"), response.listReturn);
							}else{
								$("#sequenceNumInsDecl").jqxDropDownList({source: []});
							}
						}else{
							bootbox.dialog(uiLabelMap.ErrorWhenRetrieveData,
								[{
					    		    "label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",
					    		    "callback": function() {
					    		    }
					    		}]		
							);
						}
					}
				});				
			}
		});
		$("#yearCustomTimeExcel").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				actionObject.getCustomTimePeriodByParent($("#monthCustomTimeExcel"), value, _data.customTimePeriodId);
			}
		});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#ExportInsBenefitExcelWindow"), 500, 230);
		$("#ExportInsBenefitExcelWindow").on('close', function(event){
			_data = {};
			Grid.clearForm($(this));
		});
		$("#ExportInsBenefitExcelWindow").on('open', function(event){
			if(_data.hasOwnProperty("yearCustomTimePeriodId")){
				$("#yearCustomTimeExcel").val(_data.yearCustomTimePeriodId);
			}
		});
	};
	var initJqxValidator = function(){
		$("#ExportInsBenefitExcelWindow").jqxValidator({
			rules: [
				{
					input: "#insuranceTypeReport",
					message: uiLabelMap.FieldRequired,
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},   
				{
					input: "#monthCustomTimeExcel",
					message: uiLabelMap.FieldRequired,
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},   
				{
					input: "#yearCustomTimeExcel",
					message: uiLabelMap.FieldRequired,
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},   
				{
					input: "#sequenceNumInsDecl",
					message: uiLabelMap.FieldRequired,
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},   
			]
		});
	};
	var openWindow = function(){
		openJqxWindow($("#ExportInsBenefitExcelWindow"));
	};
	var setData = function(data){
		_data = data;
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	};
}());

$(document).ready(function () {
	exportSocicalInsBenefitObj.init();
});