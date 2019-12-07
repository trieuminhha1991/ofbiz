var editTrainingCostObj = (function(){
	var init = function(){
		initJqxDropDownList();
		initJqxInput();
		initJqxNumberInput();
		initJqxRadioButton();
		initJqxCheckBox();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerTrainingCourseCost"));
	};
	
	var initJqxNumberInput = function(){
		$("#amountEmplPaid_Cost").jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 9, max: 999999999 });
		$("#totalCostEstimated_Cost").jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 9, max: 999999999, disabled: true});
		$("#amountCompanyPaid_Cost").jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 9, max: 999999999 });
		$("#nbrEmplEstimated_Cost").jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 8});
		$("#nbrDayBeforeStart_Cost").jqxNumberInput({ width: 50, height: 25, min: 0,  spinButtons: true, decimalDigits: 0, digits: 3, inputMode: 'simple'});
		
		$("#amountEmplPaid_Cost").on('valueChanged', function(event){
			calcTotalCostEstimated();
		});
		$("#amountCompanyPaid_Cost").on('valueChanged', function(event){
			calcTotalCostEstimated();
		});
		$("#nbrEmplEstimated_Cost").on('valueChanged', function(event){
			calcTotalCostEstimated();
		});
	};
	
	var initJqxInput = function(){
		$("#providerContact_Cost").jqxInput({width: '95%', height: 19, disabled: true});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#trainingProvider_Cost"), "partyId", "partyName", 25, '97%');
	};
	
	var initJqxRadioButton = function(){
		$("#isPublic_Cost").jqxRadioButton({ width: '98%', height: 25});
		$("#isNotPublic_Cost").jqxRadioButton({ width: '98%', height: 25});
	};
	var initJqxCheckBox = function(){
		$("#allowCancelRegister_Cost").jqxCheckBox({ width: 320, height: 25});
		$("#allowCancelRegister_Cost").on('change', function (event){
			$("#nbrDayBeforeStart_Cost").jqxNumberInput({disabled: !event.args.checked});
		});
	};
	
	var calcTotalCostEstimated = function(){
		var totalEmpl = $("#nbrEmplEstimated_Cost").val();
		var costPerEmpl = $("#amountEmplPaid_Cost").val();
		var amountCompanyPaid = $("#amountCompanyPaid_Cost").val();
		var totalCost = (costPerEmpl + amountCompanyPaid) * totalEmpl;
		$("#totalCostEstimated_Cost").val(totalCost);
	};
	
	var setData = function(data){
		$("#amountEmplPaid_Cost").val(data.estimatedEmplPaid);
		$("#amountCompanyPaid_Cost").val(data.amountCompanySupport);
		$("#nbrEmplEstimated_Cost").val(data.estimatedNumber);
		var isPublic = data.isPublic;
		if("Y" == isPublic){
			$("#isPublic_Cost").jqxRadioButton({checked: true});
		}else{
			$("#isNotPublic_Cost").jqxRadioButton({checked: true});
		}
		var isCancelRegister = data.isCancelRegister;
		if("Y" == isCancelRegister){
			$("#allowCancelRegister_Cost").jqxCheckBox({checked: true});
			$("#nbrDayBeforeStart_Cost").val(data.cancelBeforeDay);
		}else{
			$("#allowCancelRegister_Cost").jqxCheckBox({checked: false});
			$("#nbrDayBeforeStart_Cost").val(0);
		}
	};
	
	var getData = function(){
		var data = {};
		data.estimatedEmplPaid = $("#amountEmplPaid_Cost").val();
		data.amountCompanySupport = $("#amountCompanyPaid_Cost").val();
		data.estimatedNumber = $("#nbrEmplEstimated_Cost").val();
		if($("#isPublic_Cost").jqxRadioButton('checked')){
			data.isPublic = "Y";
		}
		if($("#isNotPublic_Cost").jqxRadioButton('checked')){
			data.isPublic = "N";
		}
		if($("#allowCancelRegister_Cost").jqxCheckBox('checked')){
			data.isCancelRegister = "Y";
			data.cancelBeforeDay = $("#nbrDayBeforeStart_Cost").val();
		}else{
			data.isCancelRegister = "N";
		}
		var providerId = $("#trainingProvider_Cost").val();
		if(providerId){
			data.providerId = providerId; 
		}
		return data;
	};
	
	var reset = function(){
		Grid.clearForm($("#editTrainingCostWindow"));
	};
	var initJqxWindow = function(){
		createJqxWindow($("#editTrainingCostWindow"), 800, 450)
	};
	var openWindow = function(){
		openJqxWindow($("#editTrainingCostWindow"));
	};
	var initEvent = function(){
		$("#editTrainingCourseCostBtn").click(function(event){
			editTrainingCostObj.openWindow();//editTrainingCostObj is defined in editTrainingCourseCost.js
		});
		$("#editTrainingCostWindow").on('open', function(event){
			loadData();
		});
		$("#editTrainingCostWindow").on('close', function(event){
			reset();
		});
		$("#cancelEditTrainingCourseCost").click(function(event){
			$("#editTrainingCostWindow").jqxWindow('close');
		});
		$("#saveEditTrainingCourseCost").click(function(event){
			var data = getData();
			data.trainingCourseId = globalVar.trainingCourseId;
			$("#loadingTrainingCourseCost").show();
			$("#cancelEditTrainingCourseCost").attr("disabled", "disabled");
			$("#saveEditTrainingCourseCost").attr("disabled", "disabled");
			$.ajax({
				url: 'updateTrainingCourse',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						Grid.renderMessage('EditTraining', response.successMessage, 
								{autoClose : true, template : 'info',appendContainer : "#containerEditTraining",opacity : 0.9});
						updateViewData();
						$("#editTrainingCostWindow").jqxWindow('close');
					}else{
						bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
							}]		
						);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#loadingTrainingCourseCost").hide();
	    			$("#cancelEditTrainingCourseCost").removeAttr("disabled");
	    			$("#saveEditTrainingCourseCost").removeAttr("disabled");
				}
			});
		});
	};
	var updateViewData = function(){
		$("#estimatedEmplPaidView").html(formatcurrency($("#amountEmplPaid_Cost").val()));
		$("#amountCompanySupportView").html(formatcurrency($("#amountCompanyPaid_Cost").val()));
		$("#totalEstimatedCostView").html(formatcurrency($("#totalCostEstimated_Cost").val()));
		$("#estimatedNumberView").html($("#nbrEmplEstimated_Cost").val());
		var provider = $("#trainingProvider_Cost").jqxDropDownList('getSelectedItem');
		if(provider){
			$("#providerNameView").html(provider.label);
		}else{
			$("#providerNameView").html(uiLabelMap.HRCommonNotSetting);
		}
		if($("#isPublic_Cost").jqxRadioButton('checked')){
			$("#trainingModeView").html(uiLabelMap.AllowAllEmployeeRegister);
		}
		if($("#isNotPublic_Cost").jqxRadioButton('checked')){
			$("#trainingModeView").html(uiLabelMap.OnlyEmplInRegisterList);
		}
		if($("#allowCancelRegister_Cost").jqxCheckBox('checked')){
			var cancelBeforeDay = $("#nbrDayBeforeStart_Cost").val();
			var desc = uiLabelMap.AllowCancelRegisterBefore + " " + cancelBeforeDay + " " + uiLabelMap.CommonDay;
			$("#trainingRegisterView").html(desc);
		}else{
			$("#trainingRegisterView").html(uiLabelMap.NotAllowCancelRegister);
		}
	};
	var loadData = function(){
		$("#loadingTrainingCourseCost").show();
		$("#cancelEditTrainingCourseCost").attr("disabled", "disabled");
		$("#saveEditTrainingCourseCost").attr("disabled", "disabled");
		$.ajax({
    		url: 'getTrainingCourseCost',
    		data: {trainingCourseId: globalVar.trainingCourseId},
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == 'success'){
    				var trainingCostData = response.trainingCostData;
    				setData(trainingCostData);
    			}else{
    				bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
    			}
    		},
    		error: function(jqXHR, textStatus, errorThrown){
    			bootbox.dialog(errorThrown,
					[{
						"label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
					}]		
				);
    		},
    		complete: function(jqXHR, textStatus){
    			$("#loadingTrainingCourseCost").hide();
    			$("#cancelEditTrainingCourseCost").removeAttr("disabled");
    			$("#saveEditTrainingCourseCost").removeAttr("disabled");
    		}
    	});
	};
	return{
		init: init,
		reset: reset,
		openWindow: openWindow 
	}
}());

$(document).ready(function(){
	editTrainingCostObj.init();
});