var createNewFormulaIncomeObject = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropdownlist();
		initJqxNumberInput();
		initJqxValidator();
		initEvent();
		initJqxWindow();
		create_spinner($("#spinnerAjax"));
	};
	var initEvent = function(){
		$("#valueFormulaBtn").click(function(event){
			//createNewFormulaObject is defined in CreateNewFormula.js
			createNewFormulaObject.setFunctionAfterBuildFormula(function(value){
				$("#functionValue").val(value);
			});
			openJqxWindow($("#settingFormula"));//$("#settingFormula") is defined in CreateNewFormula.ftl
		});
		$("#maxValueFormulaBtn").click(function(event){
			//createNewFormulaObject is defined in CreateNewFormula.js
			createNewFormulaObject.setFunctionAfterBuildFormula(function(value){
				$("#maxValue").val(value);
			});
			openJqxWindow($("#settingFormula"));
		});
		$("#cancelCreateFormula").click(function(event){
			$("#createIncomeFormulaWindow").jqxWindow('close');
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
	var initJqxWindow = function(){
		createJqxWindow($("#createIncomeFormulaWindow"), 600, 380);
		$("#createIncomeFormulaWindow").on('close', function(event){
			$('#createNewFormulaForm').jqxValidator('hide');
			Grid.clearForm($(this));
			$("#incomeTaxRate").css("display", "none");
	    	$("#createIncomeFormulaWindow").jqxWindow({height: 380});
		});
	};
	var initJqxValidator = function(){
		$('#createNewFormulaForm').jqxValidator({
			rules:[
			       {
			    	   input: '#newFormulaCode', 
			    	   message: uiLabelMap.CommonRequired, 
			    	   action: 'blur',  
			    	   rule: 'required'
			    		   
			       },
			       {
				    	 input : '#newFormulaCode', message : uiLabelMap.OnlyContainInvalidChar, action : 'blur',
				    	 rule : function(input, commit){
				    		 var value = input.val();
				    		 if(value){
				    			 if(/^[a-zA-Z0-9-_]*$/.test(value) == false){
				    				 return false;
				    			 }
				    		 }
				    		 return true;
				    	 }
				       },
			       {input: '#newFormulaName', message: uiLabelMap.CommonRequired, action: 'blur',  rule: 'required'},
			       {
			    	   input: '#newFormulaName', 
			    	   message: uiLabelMap.InvalidChar, 
			    	   action: 'blur',  
			    	   rule: function (input, commit){
			    		   	var value = input.val();
			    		   	if(value){
			    		   		if(validationNameWithoutHtml(value)){
									return false;
								}
			    		   	}
			    		   	return true;
			    	   }
			       },
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
			       {
			    	   input : '#functionValue',
			    	   message : uiLabelMap.CommonRequired,
			    	   action : 'blur',
			    	   rule : 'required'
			       }
			],
			position: 'bottom'
		});
	};
	
	var createNewFormula = function(){
		var dataSubmit = {};
		dataSubmit.code = $("#newFormulaCode").val();
		dataSubmit.name = $("#newFormulaName").val();
		dataSubmit["function"] = $("#functionValue").val(); 
		dataSubmit.maxValue = $("#maxValue").val();
		dataSubmit.payrollCharacteristicId = $("#newPayrollCharacId").val();
		dataSubmit.payrollItemTypeId = $("#payrollItemType").val();
		var taxableTypeId = $("#taxableType").val();
		dataSubmit.taxableTypeId = taxableTypeId;
		if(taxableTypeId == "WHOLE_TARIFF"){
			dataSubmit.taxRate = $("#taxRate").val();
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
					$("#createIncomeFormulaWindow").jqxWindow('close');
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
	
	var validate = function(){
		return $('#createNewFormulaForm').jqxValidator('validate');
	};
	
	var initJqxNumberInput = function (){
		$("#taxRate").jqxNumberInput({ width: '96%', height: '25px', digits: 3, symbolPosition: 'right', symbol: '%',  spinButtons: false , theme: 'olbius'});
	};
	
	var initJqxInput = function(){
		$("#newFormulaCode").jqxInput({height: 20, width: '95%', theme: 'olbius'});
		$("#newFormulaName").jqxInput({height: 20, width: '95%', theme: 'olbius'});
		$("#maxValue").jqxInput({height: 20, width: '86%', theme: 'olbius'});
		$("#functionValue").jqxInput({height: 20, width: '86%', theme: 'olbius'});
	};
	
	var initJqxDropdownlist = function(){
		createJqxDropDownList(characIncomeArr, $('#newPayrollCharacId'), "payrollCharacteristicId", "description", 25, '96%');
		createJqxDropDownList(taxableTypeArr, $('#taxableType'), "taxableTypeId", "description", 25, '96%');
		createJqxDropDownList(payrollItemTypeArr, $('#payrollItemType'), "payrollItemTypeId", "description", 25, '96%');
		$("#taxableType").on('select', function (event){
    		var args = event.args;
    	    if (args) {
        	    // index represents the item's index.                
        	    var index = args.index;
        	    var item = args.item;
        	    // get item's label and value.
        	    var value = item.value;
        	    if(value == "WHOLE_TARIFF"){
        	    	$("#incomeTaxRate").css("display", "block");
        	    	$("#createIncomeFormulaWindow").jqxWindow({height: 415});
        	    }else{
        	    	$("#incomeTaxRate").css("display", "none");
        	    	$("#createIncomeFormulaWindow").jqxWindow({height: 380});
        	    }
    		}
    	});
	};
	
	var getData = function(){
		var data = new Array();
		data.push({name: "payrollCharacteristicId", value: $("#newPayrollCharacId").val()});
		data.push({name: "payrollItemTypeId", value: $("#payrollItemType").val()});
		data.push({name: "taxableTypeId", value: $("#taxableType").val()});
		if($("#incomeTaxRate").val()){
			data.push({name: "taxRate", value: $("#incomeTaxRate").val()});
		}
		return data;
	};
	
	var hideValidate = function(){
		$('#createNewFormulaForm').jqxValidator('hide');
	};
	
	return{
		init: init,
		getData: getData,
		validate: validate,
		hideValidate: hideValidate
	}
}());

$(document).ready(function() {
	createNewFormulaIncomeObject.init();
});