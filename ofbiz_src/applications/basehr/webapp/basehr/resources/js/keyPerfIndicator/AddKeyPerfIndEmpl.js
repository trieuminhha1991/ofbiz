var addKeyPerfIndEmplObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerAddAllocateForEmpl"));
	};
	var initDropDown = function(){
		createJqxDropDownListExt($("#employeeDirectList"), globalVar.directEmplArr, 
				{displayMember: 'fullName', valueMember: 'partyId', width: '98%', height: 25});
		createJqxDropDownListExt($("#keyPerfIndAllocateEmpl"), globalVar.keyPerfIndPartyTargetItemList, 
				{displayMember: 'keyPerfIndicatorName', valueMember: 'keyPerfIndicatorId', width: '98%', height: 25});
		createJqxDropDownListExt($("#uomEmpl"), globalVar.uomArr, {displayMember: 'abbreviation', valueMember: 'uomId', width: '98%', height: 25, disabled: true});
	};
	var initInput = function(){
		$("#keyPerfIndicatorNameEmplAdd").jqxInput({width: '96%', height: 20});
		$("#weightEmpl").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 3, decimalDigits: 1, symbolPosition: 'right', symbol: '%'});
		$("#targetEmpl").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 12, decimalDigits: 1, max: 999999999999});
		$("#targetParentEmpl").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 12, decimalDigits: 1, max: 999999999999, disabled: true});
	};
	var initWindow = function(){
		createJqxWindow($("#AddAllocateForEmplWindow"), 430, 380);
	};
	var initEvent = function(){
		$("#addAllocateEmplBtn").click(function(){
			openJqxWindow($("#AddAllocateForEmplWindow"));
		});
		$("#AddAllocateForEmplWindow").on('open', function(event){
			
		});
		$("#AddAllocateForEmplWindow").on('close', function(event){
			Grid.clearForm($("#AddAllocateForEmplWindow"));
		});
		$("#cancelAddAllocateEmpl").click(function(event){
			$("#AddAllocateForEmplWindow").jqxWindow('close');
		});
		$("#saveAddAllocateEmpl").click(function(event){
			var valid = $("#AddAllocateForEmplWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateKPIAllocateTargetConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		    	createKPIAllocateTarget();
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
		});
	};
	var initValidator = function(){
		$("#AddAllocateForEmplWindow").jqxValidator({
			rules: [
				{input : '#keyPerfIndicatorNameEmplAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#employeeDirectList', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#weightEmpl', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#targetEmpl', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#keyPerfIndAllocateEmpl', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
			]
		});
	};
	var createKPIAllocateTarget = function(){
		var data = {};
		data.partyTargetName = $("#keyPerfIndicatorNameEmplAdd").val();
		data.partyId = $("#employeeDirectList").val();
		data.keyPerfIndicatorId = $("#keyPerfIndAllocateEmpl").val();
		data.target = $("#targetEmpl").val();
		data.weight = $("#weightEmpl").val();
		data.parentPartyTargetId = globalVar.partyTargetId;
		$("#cancelAddAllocateEmpl").attr("disabled","disabled");
		$("#saveAddAllocateEmpl").attr("disabled","disabled");
		$("#loadingAddAllocateForEmpl").show();
		$.ajax({
			type : 'POST',
			url : 'createKPIAllocateTarget',
			data : data,
			success : function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer : "#containerjqxgrid", opacity : 0.9});
					$("#AddAllocateForEmplWindow").jqxWindow('close');
					$("#employee" + data.keyPerfIndicatorId).jqxGrid('updatebounddata');
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
				$("#cancelAddAllocateEmpl").removeAttr("disabled");
				$("#saveAddAllocateEmpl").removeAttr("disabled");
				$("#loadingAddAllocateForEmpl").hide();
			}
		});
	};
	var openWindow = function(keyPerfIndicatorId){
		prepareData(keyPerfIndicatorId);
		openJqxWindow($("#AddAllocateForEmplWindow"));
	};
	var prepareData = function(keyPerfIndicatorId){
		$("#keyPerfIndAllocateEmpl").val(keyPerfIndicatorId);
		disableAll();
		$.ajax({
			url: 'getKeyPerfIndPartyTargetItem',
			data: {keyPerfIndicatorId: keyPerfIndicatorId, partyTargetId: globalVar.partyTargetId},
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
						);
					return;
				}
				$("#uomEmpl").val(response.uomId);
				$("#targetParentEmpl").val(response.target);
			},
			complete: function(jqXHR, textStatus){
				enableAll();	
			}
		});
	};
	var disableAll = function(){
		$("#loadingAddAllocateForEmpl").show();
		$("#cancelAddAllocateEmpl").attr("disabled", "disabled");
		$("#saveAddAllocateEmpl").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingAddAllocateForEmpl").hide();
		$("#cancelAddAllocateEmpl").removeAttr("disabled");
		$("#saveAddAllocateEmpl").removeAttr("disabled");
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	addKeyPerfIndEmplObj.init();
})