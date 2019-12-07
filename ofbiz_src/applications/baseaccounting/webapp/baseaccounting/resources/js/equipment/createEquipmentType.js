var createEquipmentTypeObj = (function(){
	var init = function(){
		initInput();
		initWindow();
		initValidator();
		initEvent();
	};
	var initInput = function(){
		$("#addEquimentTypeId").jqxInput({width: '96%', height: 22});
		$("#addEquimentTypeDesc").jqxInput({width: '96%', height: 22});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addEquipmentTypeWindow"), 380, 190);
	};
	var initEvent = function(){
		$("#addEquipmentTypeWindow").on('close', function(e){
			Grid.clearForm($(this));
			$("#addEquipmentTypeWindow").jqxValidator('hide');
		});
		$("#cancelAddEquipType").click(function(e){
			$("#addEquipmentTypeWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddEquipType").click(function(e){
			var valid = $("#addEquipmentTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.BACCCreateNewConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createEquipmentType(false);
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#saveAddEquipType").click(function(e){
			var valid = $("#addEquipmentTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.BACCCreateNewConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createEquipmentType(true);
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
	
	var createEquipmentType = function(isCloseWindow){
		Loading.show('loadingMacro');
		var data = {};
		if($("#addEquimentTypeId").val()){
			data.equimentTypeId = $("#addEquimentTypeId").val();
		}
		data.description = $("#addEquimentTypeDesc").val();
		$.ajax({
			url: 'createEquipmentType',
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
						  $("#addEquipmentTypeWindow").jqxWindow('close');
					  }else{
						  Grid.clearForm($("#addEquipmentTypeWindow"));
					  }
					  Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
					  updateListEquipmentType(response.equipmentTypeId);
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var initValidator = function(){
		$("#addEquipmentTypeWindow").jqxValidator({
			rules: [
				{ input: '#addEquimentTypeDesc', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
		accutils.openJqxWindow($("#addEquipmentTypeWindow"));
	};
	
	var updateListEquipmentType = function(equipmentTypeId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getListEquipmentType',
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
					  globalVar.equipmentTypeArr = response.equipmentTypeList;
					  accutils.updateSourceDropdownlist($("#equipmentTypeId"), globalVar.equipmentTypeArr);
					  $("#equipmentTypeId").val(equipmentTypeId);
				  }
			},
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	createEquipmentTypeObj.init();
});