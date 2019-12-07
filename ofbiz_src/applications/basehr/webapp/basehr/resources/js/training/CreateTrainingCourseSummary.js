var createTrainingSumObj = (function(){
	var init = function(){
		initSimpleInput();
		$("#jqxNotificationjqxgrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
			autoClose: true, template: "info", appendContainer: "#containerjqxgrid"});
		calcTotalCostEstimated($("#estimatedEmplPaid"), $("#amountCompanySupport"), $("#estimatedNumber"), $("#totalAmountEstimated"));
		calcTotalCostEstimated($("#actualEmplPaid"), $("#actualAmountCompanySup"), $("#actualNumber"), $("#totalAmountActual"));
		initEvent();
	};
	var initSimpleInput = function(){
		$("#trainingCodeView").jqxInput({width: '96%', height: 25, disabled: true});
		$("#trainingCourseNameView").jqxInput({width: '96%', height: 25, disabled: true});
		$("#actualStartDate").jqxDateTimeInput({width: '100%', height: 25, value: globalVar.estimatedFromDate});
		$("#actualEndDate").jqxDateTimeInput({width: '100%', height: 25, value: globalVar.estimatedThruDate});
		$("#estimatedStartDate").jqxDateTimeInput({width: '94%', height: 25, readonly: true, disabled: true, value: globalVar.estimatedFromDate});
		$("#estimatedEndDate").jqxDateTimeInput({width: '94%', height: 25, readonly: true, disabled: true, value: globalVar.estimatedThruDate});
		
		$("#actualEmplPaid").jqxNumberInput({ width: '100%', height: 25, min: 0, spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999, decimal: globalVar.estimatedEmplPaid});
		$("#actualAmountCompanySup").jqxNumberInput({ width: '100%', height: 25, min: 0, spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999, 
			 decimal: globalVar.amountCompanySupport});
		$("#amountCompanySupport").jqxNumberInput({ width: '94%', height: 25, min: 0,  spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999,
			decimal: globalVar.amountCompanySupport, readOnly: true, disabled: true,});
		$("#estimatedEmplPaid").jqxNumberInput({ width: '94%', height: 25, min: 0, spinButtons: true, decimalDigits: 0, digits: 9, 
			max: 999999999, readOnly: true, disabled: true, decimal: globalVar.estimatedEmplPaid});
		$("#totalAmountEstimated").jqxNumberInput({ width: '94%', height: 25, min: 0, spinButtons: true, decimalDigits: 0, digits: 9, 
			max: 999999999, readOnly: true, disabled: true});
		$("#totalAmountActual").jqxNumberInput({ width: '100%', height: 25, min: 0, spinButtons: true, decimalDigits: 0, digits: 9, 
			max: 999999999, readOnly: true, disabled: true});
		
		$("#actualNumber").jqxNumberInput({ width: '100%', height: 25, min: 0,  spinButtons: true, decimalDigits: 0, readOnly: true, disabled: true,
			decimal: globalVar.totalActualAtt});
		$("#estimatedNumber").jqxNumberInput({ width: '94%', height: 25, min: 0,  spinButtons: true, decimalDigits: 0, readOnly: true, disabled: 
			true, decimal: globalVar.estimatedNumber});
	};
	var calcTotalCostEstimated = function(amountEle, companySupEle, nbrEmplEle, totalEle){
		var totalEmpl = nbrEmplEle.val();
		var costPerEmpl = amountEle.val();
		var amountCompanyPaid = companySupEle.val();
		var totalCost = (costPerEmpl + amountCompanyPaid) * totalEmpl;
		totalEle.val(totalCost);
	};
	var initEvent = function(){
		$("#actualAmountCompanySup").on('valueChanged', function(event){
			calcTotalCostEstimated($("#actualEmplPaid"), $("#actualAmountCompanySup"), $("#actualNumber"), $("#totalAmountActual"));
		});
		$("#actualEmplPaid").on('valueChanged', function(event){
			calcTotalCostEstimated($("#actualEmplPaid"), $("#actualAmountCompanySup"), $("#actualNumber"), $("#totalAmountActual"));
		});
		
		$("#AddNewPartyAttWindow").on('addPartyToTrainingSuccess', function(event){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getTotalEmplAttendanceTraining',
				data: {trainingCourseId: globalVar.trainingCourseId},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						$("#actualNumber").val(response.totalActualAtt);
						calcTotalCostEstimated($("#actualEmplPaid"), $("#actualAmountCompanySup"), $("#actualNumber"), $("#totalAmountActual"));
					}else{
						bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
					}
				},
				complete: function(jqXHR, textStatus){
					Loading.hide('loadingMacro');
				}
			});
		});
		$("#saveSummaryTraining").click(function(event){
			Loading.show('loadingMacro');
			var data = getData();
			$.ajax({
				url: 'summaryTrainingCourse',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						location.reload();
					}else{
						bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
						Loading.hide('loadingMacro');
					}
				},
				complete: function(jqXHR, textStatus){
					
				}
			});
		});
	};
	var getData = function(){
		var data = {};
		data.actualFromDate = $("#actualStartDate").jqxDateTimeInput('val', 'date').getTime();
		data.actualThruDate = $("#actualEndDate").jqxDateTimeInput('val', 'date').getTime();
		data.actualEmplPaid = $("#actualEmplPaid").val();
		data.actualAmountCompanySup = $("#amountCompanySupport").val();
		data.trainingCourseId = globalVar.trainingCourseId;
		return data;
	};
	return{
		init: init,
	}
}());
$(document).ready(function(){
	$.jqx.theme = 'olbius';
	createTrainingSumObj.init();
	$("#mainContainer").show();
});