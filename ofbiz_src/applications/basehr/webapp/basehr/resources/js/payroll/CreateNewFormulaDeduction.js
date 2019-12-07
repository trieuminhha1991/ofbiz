var createNewFormulaDeductionObject = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropdownlist();
		initJqxCheckBox();
		initJqxValidator();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerAjax"));
	};
	var initEvent = function(){
		$("#valueFormulaBtn").click(function(event){
			createNewFormulaObject.setFunctionAfterBuildFormula(function(value){
				$("#functionValue").val(value);
			});//createNewFormulaObject is defined in CreateNewFormula.js
			openJqxWindow($("#settingFormula"));//$("#settingFormula") is defined in CreateNewFormula.ftl
		});
		$("#maxValueFormulaBtn").click(function(event){
			createNewFormulaObject.setFunctionAfterBuildFormula(function(value){
				$("#maxValue").val(value);
			});//createNewFormulaObject is defined in CreateNewFormula.js
			openJqxWindow($("#settingFormula"));
		});
		
		$("#cancelCreateFormula").click(function(event){
			$("#createDeductionFormulaWindow").jqxWindow('close');
		});
		$("#saveCreateFormula").click(function(event){
			if(!validate()){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreateNewFormulaConfrim,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createNewFormula();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			
		});
	};
	var createNewFormula = function(){
		var dataSubmit = {};
		dataSubmit.code = $("#newFormulaCode").val();
		dataSubmit.name = $("#newFormulaName").val();
		dataSubmit["function"] = $("#functionValue").val(); 
		dataSubmit.maxValue = $("#maxValue").val();
		dataSubmit.payrollCharacteristicId = $("#newPayrollCharacId").val();
		var isExempted = $("#newExempted").jqxCheckBox('checked');
		if(isExempted){
			dataSubmit.exempted = "Y";
		}
		$("#ajaxLoading").show();
		$("#cancelCreateFormula").attr("disabled", 'disabled');
		$("#saveCreateFormula").attr("disabled", 'disabled');
		$.ajax({
			url: 'createFormula',
			data: dataSubmit,
			type: 'POST',
			success: function(data){
				$("#createFormulaNtf").jqxNotification('closeLast');
				if(data._EVENT_MESSAGE_){
					$("#notificationText").text(data._EVENT_MESSAGE_);
					$("#createFormulaNtf").jqxNotification({ template: 'info' });
					$("#createFormulaNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#createDeductionFormulaWindow").jqxWindow('close');
				}else{
					bootbox.dialog(data._ERROR_MESSAGE_,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete: function (jqXHR, textStatus){
				$("#ajaxLoading").hide();
				$("#cancelCreateFormula").removeAttr("disabled");
				$("#saveCreateFormula").removeAttr("disabled");
			}
		});
	};
	var initJqxDropdownlist = function(){
		createJqxDropDownList(characDeductionArr, $('#newPayrollCharacId'), "payrollCharacteristicId", "description", 25, '96%');
	};
	
	var initJqxInput = function(){
		$("#newFormulaCode").jqxInput({height: 20, width: "95%", theme: 'olbius'});
		$("#newFormulaName").jqxInput({height: 20, width: "95%", theme: 'olbius'});
		$("#maxValue").jqxInput({height: 20, width: '86%', theme: 'olbius'});
		$("#functionValue").jqxInput({height: 20, width: '86%', theme: 'olbius'});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#createDeductionFormulaWindow"), 550, 360);
		$("#createDeductionFormulaWindow").on('close', function(event){
			$('#createNewFormulaForm').jqxValidator('hide');
			Grid.clearForm($(this));
		});
	};
	var initJqxCheckBox = function(){
		$("#newExempted").jqxCheckBox({width: 50, height: 25, checked: false, theme: 'olbius'});
	};
	
	var initJqxValidator = function(){
		$('#createNewFormulaForm').jqxValidator({
			rules:[
			       {input: '#newFormulaCode', message: uiLabelMap.CommonRequired, action: 'blur',  rule: 'required'},
			       {
			    	   input: '#newFormulaCode', 
			    	   message: uiLabelMap.OnlyContainInvalidChar, 
			    	   action: 'blur',  
			    	   rule: function (input, commit){
			    		   	var value = input.val();
			    		   	if(value){
			    			   if(/^[a-zA-Z0-9-_]*$/.test(value) == false) {
			    				    return false;
			    				}
			    		   	}
			    		   	return true;
			    	   }
			       },
			       {input: '#newFormulaName', message: uiLabelMap.CommonRequired, action: 'blur',  rule: 'required'},
			       {
			    	   input: '#newPayrollCharacId', 
			    	   message: uiLabelMap.CommonRequired, 
			    	   action: 'blur',  
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input: '#functionValue', 
			    	   message: uiLabelMap.CommonRequired, 
			    	   action: 'blur',  
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			],
			position: 'bottom'
		});
	};
	
	var getData = function(){
		var data = new Array();
		data.push({name: "payrollCharacteristicId", value: $("#newPayrollCharacId").val()});
		var checked = $("#newExempted").jqxCheckBox('checked');
		var exempted = "N";
		if(checked){
			exempted = "Y";
		}
		data.push({name: "exempted", value: exempted});
		return data;
	};
	
	var hideValidate = function(){
		$('#createNewFormulaForm').jqxValidator('hide');
	};
	
	var validate = function(){
		return $('#createNewFormulaForm').jqxValidator('validate');
	};
	
	return{
		init: init,
		validate: validate,
		hideValidate: hideValidate,
		getData: getData
	}
}());

$(document).ready(function() {
	createNewFormulaDeductionObject.init();
});