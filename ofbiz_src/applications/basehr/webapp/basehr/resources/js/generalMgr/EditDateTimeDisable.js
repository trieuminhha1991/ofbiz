var editDateTimeDisObj = (function(){
	var _data;
	var init = function(){
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#jqxInputeditDateTimeDisable").jqxDateTimeInput({width: '89%', height: 25, formatString: "dd/MM/yyyy HH:mm:ss"});
	};
	var initWindow = function(){
		createJqxWindow($("#editDateTimeDisable"), 400, 140);
	};
	var openWindow = function(data){
		_data = data;
		openJqxWindow($("#editDateTimeDisable"));
		var currentDate = new Date();
		$("#jqxInputeditDateTimeDisable").val(new Date(currentDate.getTime() + 1000));
	};
	var initEvent = function(){
		$("#cancelEditDateTimeDis").click(function(){
			$("#editDateTimeDisable").jqxWindow('close');
			$('#editDateTimeDisable').jqxValidator('hide');
		});
		$("#saveEditDateTimeDis").click(function(){
			var valid = $("#editDateTimeDisable").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.HRChangeInfoEmplAccount,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		 editDateTimeInfEmpl();
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
			$('#editDateTimeDisable').jqxValidator('hide');
		});
	};
	var initValidator = function(){
		$("#editDateTimeDisable").jqxValidator({
			scroll: false,
			position: 'bottom',
			rules: [
				{input: '#jqxInputeditDateTimeDisable', message: uiLabelMap.FieldRequired, action: 'change, blur',
					rule: function (input, commit){
						if(OlbCore.isNotEmpty(input.val())){
							return true;
						}
						return false;
					}
				},
				{input: '#jqxInputeditDateTimeDisable', message: uiLabelMap.HRAfterNowDate, action: 'change, blur',
					rule: function (input, commit){
						if(OlbCore.isNotEmpty(input.val()) && input.jqxDateTimeInput('val', 'date').getTime() > new Date()){
							return true;
						}
						return false;
					}
				},
			]
		});
	};
	var getData = function(){
 		var data = {};
 		data.userLoginId = $("#viewUserLoginId").val();
 		data.disabledDateTime = $("#jqxInputeditDateTimeDisable").jqxDateTimeInput('val', 'date').getTime().toString();
 		return data;
 	};
 	var editDateTimeInfEmpl = function(){
 		Loading.show('loadingMacro');
 		var data = getData();
 		$.ajax({
 			url: 'changingInfoEmplAccount',
 			data: data,
 			type: 'POST',
 			success: function(response){
 				if(response.responseMessage == "error"){
 					bootbox.dialog(response.errorMessage,
 							[{
 								"label" : uiLabelMap.CommonClose,
 								"class" : "btn-danger btn-small icon-remove open-sans",
 							}]		
 					);
 					return;
 				}
 				Grid.renderMessage('jqxgrid', response.results.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
 				$("#editDateTimeDisable").jqxWindow('close');
 				emplInfoAccountObj.updateViewEmlInf(response.results);
 			},
 			complete: function(){
 				Loading.hide('loadingMacro');
 			}
 		});
 	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	editDateTimeDisObj.init();
});