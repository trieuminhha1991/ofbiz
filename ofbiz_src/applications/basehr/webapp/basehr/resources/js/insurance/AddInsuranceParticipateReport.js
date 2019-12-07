var addReportObj = (function(){
	var init = function(){
		initDateTime();
		initInput();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerAddReport"));
	};
	var initInput = function(){
		$("#reportName").jqxInput({width: '96%', height: 20});
	};
	var initWindow = function(){
		createJqxWindow($("#addInsuranceReportWindow"), 400, 180);
	};
	var initDateTime = function(){
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthData, $("#monthReport"), "month", "description", 25, 90);
		$("#yearReport").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initEvent = function(){
		$("#addInsuranceReportWindow").on('open', function(event){
			initData();
		});
		$("#addInsuranceReportWindow").on('close', function(event){
			resetData();
		});
		$("#cancelAddReport").click(function(event){
			$("#addInsuranceReportWindow").jqxWindow('close');
		});
		$("#saveAddReport").click(function(event){
			var valid = $("#addInsuranceReportWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateInsuranceReportConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createInsuranceReport();
						}
					},
					{
						"label": uiLabelMap.CommonCancel,
						"class": "btn-danger icon-remove btn-small open-sans",
					}]
				);
		});
	};
	var initValidator = function(){
		$("#addInsuranceReportWindow").jqxValidator({
			rules: [
				{ input: '#reportName', message: uiLabelMap.FieldRequired, action: 'blur',
					rule : function(input, commit){
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},       
			]
		});
	};
	var createInsuranceReport = function(){
		$("#loadingAddReport").show();
		$("#cancelAddReport").attr("disabled", "disabled");
		$("#saveAddReport").attr("disabled", "disabled");
		var data = {month: $("#monthReport").val(), year: $("#yearReport").val(), reportName: $("#reportName").val()};
		$.ajax({
			url: 'createInsuranceParticipateReport',
			data: data,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#addInsuranceReportWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}	
			},
			complete: function(jqXHR, textStatus){
				$("#loadingAddReport").hide();
				$("#cancelAddReport").removeAttr("disabled");
				$("#saveAddReport").removeAttr("disabled");
			}
		});
	};
	var resetData = function(){
		Grid.clearForm($("#addInsuranceReportWindow"));
	};
	var initData = function(){
		var date = new Date();
		var month = date.getMonth();
		var year = date.getFullYear();
		$("#monthReport").val(month);
		$("#yearReport").val(year);
	};
	return {
		init: init
	};
}());
$(document).ready(function () {
	addReportObj.init();
});