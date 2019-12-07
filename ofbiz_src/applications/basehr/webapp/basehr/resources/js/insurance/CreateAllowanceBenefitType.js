var createAllowanceBenefitTypeObj = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxCheckBox();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("spinnerCreateBenefitType"));
	};
	var initJqxInput = function(){
		$("#allowanceBenefitTypeDesc").jqxInput({width: '96%', height: 20});
		//$("#allowanceBenefitTypeCode").jqxNumberInput({width: '98%', height: 25, min: 1,  spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#popupAddrow"), 500, 320);
		$("#popupAddrow").on('open', function(event){
			
		});
		$("#popupAddrow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.insBenefitTypeFreqArr, $("#insBenefitTypeFreq"), "frequenceId", "description", 25, '98%');
		createJqxDropDownList(globalVar.insAllowanceBenefitClassTypeArr, $("#benefitClassTypeId"), "benefitClassTypeId", "description", 25, '98%');
	};
	var initJqxCheckBox = function(){
		$("#isIncAnnualLeaveBenefitType").jqxCheckBox({width: '99%', height: 25});
		$("#InsuranceAccumulated").jqxCheckBox({width: '99%', height: 25});
	};
	var initJqxValidator = function(){
		$("#popupAddrow").jqxValidator({
			rules: [
				{input : '#allowanceBenefitTypeDesc', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#insBenefitTypeFreq', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#benefitClassTypeId', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(input.val() < 1){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var initEvent = function(){
		$("#cancelAllowanceBenefitType").click(function(event){
			$("#popupAddrow").jqxWindow('close');
		});
		$("#saveAllowanceBenefitType").click(function(event){
			var valid = $("#popupAddrow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateAllowanceBenefitTypeConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createAllowanceBenefitType();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			//$("#popupAddrow").jqxWindow('close');
		});
	};
	var getData = function(){
		var data = {};
		data.description = $("#allowanceBenefitTypeDesc").val();
		//data.benefitTypeCode = $("#allowanceBenefitTypeCode").val();
		data.frequenceId = $("#insBenefitTypeFreq").val();
		data.benefitClassTypeId = $("#benefitClassTypeId").val();
		var isAccumulated = $("#InsuranceAccumulated").jqxCheckBox('checked');
		if(isAccumulated){
			data.isAccumulated = "Y";
		}else{
			data.isAccumulated = "N";
		}
		var isIncAnnualLeave = $("#isIncAnnualLeaveBenefitType").jqxCheckBox('checked');
		if(isIncAnnualLeave){
			data.isIncAnnualLeave = "Y";
		}else{
			data.isIncAnnualLeave = "N";
		}
		return data;
	};
	var createAllowanceBenefitType = function(){
		$("#loadingCreateBenefitType").show();
		$("#cancelAllowanceBenefitType").attr("disabled", "disabled");
		$("#saveAllowanceBenefitType").attr("disabled", "disabled");
		var dataSubmit = getData();
		$.ajax({
			url: 'createInsuranceAllowanceBenefitType',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#popupAddrow").jqxWindow('close');
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
						);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingCreateBenefitType").hide();
				$("#cancelAllowanceBenefitType").removeAttr("disabled");
				$("#saveAllowanceBenefitType").removeAttr("disabled");
			}
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	createAllowanceBenefitTypeObj.init();
});