var editTrainingInfoObj = (function(){
	var init = function(){
		initInput();
		initWindow();
		initEvent();
		create_spinner($("#spinnerTrainingCourseInfo"));
	};
	var initInput = function(){
		$("#actualFromDate").jqxDateTimeInput({width: '98%', height: 25});
		$("#actualThruDate").jqxDateTimeInput({width: '98%', height: 25});
	};
	var initWindow = function(){
		createJqxWindow($("#editTrainingInfoWindow"), 400, 200);
	};
	var initEvent = function(){
		$("#editTrainingInfoWindow").on('open', function(){
			$("#actualFromDate").val(globalVar.actualFromDate);
			$("#actualThruDate").val(globalVar.actualThruDate);
		});
		$("#editTrainingCourseInfoBtn").click(function(){
			openJqxWindow($("#editTrainingInfoWindow"));
		});
		$("#cancelEditTrainingCourseInfo").click(function(event){
			$("#editTrainingInfoWindow").jqxWindow('close');
		});
		$("#saveEditTrainingCourseInfo").click(function(event){
			$("#loadingTrainingCourseInfo").show();
			$("#cancelEditTrainingCourseInfo").attr("disabled", "disabled");
			$("#saveEditTrainingCourseInfo").attr("disabled", "disabled");
			var actualFromDate = $("#actualFromDate").jqxDateTimeInput('val', 'date');
			var actualThruDate = $("#actualThruDate").jqxDateTimeInput('val', 'date');
			$.ajax({
				url: 'updateTrainingCourseSummarized',
				data: {trainingCourseId: globalVar.trainingCourseId, actualFromDate: actualFromDate.getTime(), actualThruDate: actualThruDate.getTime()},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						Grid.renderMessage('jqxgrid', response.successMessage, 
								{autoClose : true, template : 'info',appendContainer : "#containerjqxgrid",opacity : 0.9});
						globalVar.actualFromDate = actualFromDate;
						globalVar.actualThruDate = actualThruDate;
						$("#actualFromDateView").html(getDate(actualFromDate) + "/" + getMonth(actualFromDate) + "/" + actualFromDate.getFullYear());
						$("#actualThruDateView").html(getDate(actualThruDate) + "/" + getMonth(actualThruDate) + "/" + actualThruDate.getFullYear());
						$("#editTrainingInfoWindow").jqxWindow('close');
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
					$("#loadingTrainingCourseInfo").hide();
					$("#cancelEditTrainingCourseInfo").removeAttr("disabled");
					$("#saveEditTrainingCourseInfo").removeAttr("disabled");
				}
			});
		});
	};
	return{
		init: init
	}
}());
var editTrainingCostObj = (function(){
	var init = function(){
		initInput();
		initWindow();
		initEvent();
		create_spinner($("#spinnerTrainingCost"));
	};
	var initInput = function(){
		$("#actualEmplPaid").jqxNumberInput({ width: '98%', height: 25, min: 0, spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999});
		$("#actualAmountCompanySup").jqxNumberInput({ width: '98%', height: 25, min: 0, spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999});
	};
	var initWindow = function(){
		createJqxWindow($("#editTrainingCostWindow"), 400, 200);
	};
	var initEvent = function(){
		$("#AddNewPartyAttWindow").on('addPartyToTrainingSuccess', function(event){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getTotalEmplAttendanceTraining',
				data: {trainingCourseId: globalVar.trainingCourseId},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						globalVar.totalActualAtt = response.totalActualAtt;
						$("#actualNumberView").html(globalVar.totalActualAtt);
						var totalCost = (globalVar.actualEmplPaid + globalVar.actualAmountCompanySup) * globalVar.totalActualAtt;
						$("#totalActualCostView").html(formatcurrency(totalCost));
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
		$("#editTrainingCostWindow").on('open', function(event){
			$("#actualEmplPaid").val(globalVar.actualEmplPaid);
			$("#actualAmountCompanySup").val(globalVar.actualAmountCompanySup);
		});
		$("#cancelEditTrainingCost").click(function(event){
			$("#editTrainingCostWindow").jqxWindow('close');
		});
		$("#saveEditTrainingCost").click(function(event){
			$("#loadingTrainingCost").show();
			$("#cancelEditTrainingCost").attr("disabled", "disabled");
			$("#saveEditTrainingCost").attr("disabled", "disabled");
			var actualEmplPaid = $("#actualEmplPaid").val();
			var actualAmountCompanySup = $("#actualAmountCompanySup").val();
			$.ajax({
				url: 'updateTrainingCourseSummarized',
				data: {trainingCourseId: globalVar.trainingCourseId, actualEmplPaid: actualEmplPaid, actualAmountCompanySup: actualAmountCompanySup},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						Grid.renderMessage('jqxgrid', response.successMessage, 
								{autoClose : true, template : 'info',appendContainer : "#containerjqxgrid",opacity : 0.9});
						globalVar.actualEmplPaid = actualEmplPaid;
						globalVar.actualAmountCompanySup = actualAmountCompanySup;
						var totalCost = (globalVar.actualEmplPaid + globalVar.actualAmountCompanySup) * globalVar.totalActualAtt;
						$("#actualAmountCompanySupView").html(formatcurrency(globalVar.actualAmountCompanySup));
						$("#actualEmplPaidView").html(formatcurrency(globalVar.actualEmplPaid));
						$("#totalActualCostView").html(formatcurrency(totalCost));
						$("#editTrainingCostWindow").jqxWindow('close');
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
					$("#loadingTrainingCost").hide();
					$("#cancelEditTrainingCost").removeAttr("disabled");
					$("#saveEditTrainingCost").removeAttr("disabled");
				}
			});
		});
		$("#editTrainingCourseCostBtn").click(function(){
			openJqxWindow($("#editTrainingCostWindow"));
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	$.jqx.theme = 'olbius';
	$("#jqxNotificationjqxgrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, 
		template: "info", appendContainer: "#containerjqxgrid"});
	editTrainingInfoObj.init();
	editTrainingCostObj.init();
	$("#completeTrainingCourse").click(function(event){
		bootbox.dialog(uiLabelMap.CompleteTrainingCourseConfirm,
				[
				 {
					 "label" : uiLabelMap.CommonSubmit,
					 "class" : "btn-primary btn-small icon-ok open-sans",
					 "callback": function() {
						 Loading.show('loadingMacro');
							$.ajax({
								url: 'completeTrainingCourse',
								data: {trainingCourseId: globalVar.trainingCourseId},
								type: 'POST',
								success: function(response){
									if(response._EVENT_MESSAGE_){
										location.reload();
									}else{
										bootbox.dialog(response._ERROR_MESSAGE_,
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
					 }
				 },
				 {
					 "label" : uiLabelMap.CommonCancel,
					 "class" : "btn-danger icon-remove btn-small open-sans",
				 }
				 ]
			);
	});
});