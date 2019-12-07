var excelTemplateObj = (function(){
	var _data = {};
	var init = function(){
		initJqxDropDownList();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerExcel"));
	};
	var initJqxValidator = function(){
		$("#excelExportWindow").jqxValidator({
			rules: [
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
	var initEvent = function(){
		$("#cancelExcelExport").click(function(event){
			$("#excelExportWindow").jqxWindow('close');
		});
		$("#saveExcelExport").click(function(event){
			var valid = $("#excelExportWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			/*$("#loadingExcel").show();
			$("#cancelExcelExport").attr("disabled", "disabled");
			$("#saveExcelExport").attr("disabled", "disabled");*/
			var customTimePeriodId = $("#monthCustomTimeExcel").val();
			var sequenceNum = $("#sequenceNumInsDecl").val();
			/*$.ajax({
				url: 'exportInsuranceExcel',
				type: 'POST',
				data: {customTimePeriodId: customTimePeriodId, sequenceNum: sequenceNum, insuranceContentTypeId: "D02-TS"},
				//cache: true,
				datatype: 'text',
				success: function(response){
					if(response.responseMessage == "error"){
						bootbox.dialog(response.errorMessage,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#loadingExcel").hide();
					$("#cancelExcelExport").removeAttr("disabled");
					$("#saveExcelExport").removeAttr("disabled");
				}
			});*/
			window.location.href = "exportInsuranceExcel?customTimePeriodId=" + customTimePeriodId + "&sequenceNum=" + sequenceNum + "&insuranceContentTypeId=D02-TS" ;
			$("#excelExportWindow").jqxWindow('close');
		});
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(yearCustomTimePeriod, $("#yearCustomTimeExcel"), "customTimePeriodId", "periodName", 25, 150);
		createJqxDropDownList([], $("#monthCustomTimeExcel"), "customTimePeriodId", "periodName", 25, 150);
		createJqxDropDownList([], $("#sequenceNumInsDecl"), "sequenceNum", "sequenceNum", 25, 150);
		$("#monthCustomTimeExcel").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$.ajax({
					url: 'getInsuranceDeclSequenceNum',
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
		createJqxWindow($("#excelExportWindow"), 500, 230);
		$("#excelExportWindow").on('close', function(event){
			_data = {};
			Grid.clearForm($(this));
		});
		$("#excelExportWindow").on('open', function(event){
			if(_data.hasOwnProperty("yearCustomTimePeriodId")){
				$("#yearCustomTimeExcel").val(_data.yearCustomTimePeriodId);
			}
		});
	};
	var openWindow = function(){
		openJqxWindow($("#excelExportWindow"));
	};
	var setData = function(data){
		_data = data;
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	excelTemplateObj.init();
});