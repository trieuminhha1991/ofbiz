var fixedAssetDecrReasonTypeObj = (function(){
	var init = function(){
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#fixedAssetDecrReasonTypeId").jqxInput({width: '96%', height: 22});
		$("#descDecreaseReasonType").jqxInput({width: '96%', height: 22});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addDecreaseReasonTypeWindow"), 380, 190);
	};
	var initEvent = function(){
		$("#addDecreaseReasonTypeWindow").on('close', function(e){
			Grid.clearForm($(this));
			$("#addDecreaseReasonTypeWindow").jqxValidator('hide');
		});
		$("#cancelAddDecreaseReasonType").click(function(e){
			$("#addDecreaseReasonTypeWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddDecreaseReasonType").click(function(e){
			var valid = $("#addDecreaseReasonTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.BACCCreateNewConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createDecreaseReasonType(false);
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#saveAddDecreaseReasonType").click(function(e){
			var valid = $("#addDecreaseReasonTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.BACCCreateNewConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createDecreaseReasonType(true);
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
	};
	
	var createDecreaseReasonType = function(isCloseWindow){
		Loading.show('loadingMacro');
		var data = {};
		if($("#fixedAssetDecrReasonTypeId").val()){
			data.decreaseReasonTypeId = $("#fixedAssetDecrReasonTypeId").val();
		}
		data.description = $("#descDecreaseReasonType").val();
		$.ajax({
			url: 'createFixedAssetDecrReasonType',
			type: "POST",
			data: data,
			success: function(response) {
				  if(response._ERROR_MESSAGE_){
					  bootbox.dialog(response._ERROR_MESSAGE_,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  if(isCloseWindow){
						  $("#addDecreaseReasonTypeWindow").jqxWindow('close');
					  }else{
						  Grid.clearForm($("#addDecreaseReasonTypeWindow"));
					  }
					  Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
					  updateDecreaseReasonType(response.decreaseReasonTypeId);
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var updateDecreaseReasonType = function(decreaseReasonTypeId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getFixedAssetDecrReasonType',
			type: "POST",
			success: function(response) {
				if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  globalVar.decreaseReasonTypeArr = response.fixedAssetDecrReasonTypeList;
					  accutils.updateSourceDropdownlist($("#decreaseReasonTypeId"), globalVar.decreaseReasonTypeArr);
					  $("#decreaseReasonTypeId").val(decreaseReasonTypeId);
				  }
			},
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var initValidator = function(){
		$("#addDecreaseReasonTypeWindow").jqxValidator({
			rules: [
				{ input: '#descDecreaseReasonType', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val()){
							return true;
						}
						return false;
					}
				},
			]
		});
	};
	var openWindow = function(){
		accutils.openJqxWindow($("#addDecreaseReasonTypeWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	fixedAssetDecrReasonTypeObj.init();
});