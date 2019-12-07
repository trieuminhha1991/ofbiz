var apprTrainingCourseObj = (function(){
	var init = function(){
		initJqxRadioBtn();
		initWindow();
		initEvent();
		initValidator();
	};
	var initJqxRadioBtn = function(){
		$("#acceptTrainingPpsl").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#rejectTrainingPpsl").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#cancelTrainingPpsl").jqxRadioButton({ width: '94%', height: 25, checked: false});
	};
	var initEditor = function(){
		$('#changeReason').jqxEditor({ 
    		width: '97%',
            theme: 'olbiuseditor',
            tools: '',
            disabled: false,
            height: 100,
        });	
		$('#changeReason').val("");
	};
	var initWindow = function(){
		createJqxWindow($("#apprTrainCourseWindow"), 500, 250, initEditor);
	};
	var initEvent = function(){
		$("#apprTrainingCourse").click(function(event){
			openJqxWindow($("#apprTrainCourseWindow"));
		});
		$("#apprTrainCourseWindow").on('close', function(event){
			Grid.clearForm($(this));
			$('#changeReason').val("");
		});
		$("#cancelAppr").click(function(event){
			$("#apprTrainCourseWindow").jqxWindow('close');
		});
		$("#saveAppr").click(function(event){
			var valid = $("#apprTrainCourseWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.HRApprovalConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							approvalTrainingCourse();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		
	};
	var approvalTrainingCourse = function(){
		var dataSubmit = {trainingCourseId: globalVar.trainingCourseId};
		if($("#changeReason").jqxEditor('val').length > 0){
			dataSubmit.changeReason = $("#changeReason").jqxEditor('val');
		}
		var statusId = "";
		var accept = $("#acceptTrainingPpsl").jqxRadioButton('checked');
		var reject = $("#rejectTrainingPpsl").jqxRadioButton('checked');
		var cancel = $("#cancelTrainingPpsl").jqxRadioButton('checked');
		if(accept){
			dataSubmit.statusId = "TRAINING_PLANNED_ACC";
		}else if(reject){
			dataSubmit.statusId = "TRAINING_PLANNED_REJ";
		}else if(cancel){
			dataSubmit.statusId = "TRAINING_PLANNED_CANCEL";
		}
		Loading.show('loadingMacro');
		$.ajax({
			url: 'approvalTrainingCourse',
			data: dataSubmit,
			type: 'POST', 
			success: function(response){
				if(response._EVENT_MESSAGE_){
					location.reload();
				}else{
					Grid.renderMessage('EditTraining', response._ERROR_MESSAGE_, {autoClose : true,
						template : 'error', appendContainer: "#containerEditTraining", opacity : 0.9});
					Loading.hide('loadingMacro');
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				Loading.hide('loadingMacro');
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	var initValidator = function(){
		$("#apprTrainCourseWindow").jqxValidator({
			rules: [
		        {
		        	input : '#cancelTrainingPpsl', message : uiLabelMap.PleaseSelectOption, action: 'blur', 
					rule : function(input, commit){
						var acceptTrainingPpsl = $("#acceptTrainingPpsl").jqxRadioButton('checked');
						var rejectTrainingPpsl = $("#rejectTrainingPpsl").jqxRadioButton('checked');
						var cancelTrainingPpsl = $("#cancelTrainingPpsl").jqxRadioButton('checked');
						if(!acceptTrainingPpsl && !rejectTrainingPpsl && !cancelTrainingPpsl){
							return false;
						}
						return true;
					}
				
		        }
			]
		});
	};
	return{
		init: init
	};
}());
$(document).ready(function(){
	apprTrainingCourseObj.init();
});