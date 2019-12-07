var addKeyPerfIndChildOrgObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerAddAllocateForChildOrg"));
	};
	var initDropDown = function(){
		createJqxDropDownListExt($("#partyGroupAllocateList"), globalVar.directChildDeptArr, 
				{displayMember: 'groupName', valueMember: 'partyId', width: '98%', height: 25});
		createJqxDropDownListExt($("#keyPerfIndAllocateOrg"), globalVar.keyPerfIndPartyTargetItemList, 
				{displayMember: 'keyPerfIndicatorName', valueMember: 'keyPerfIndicatorId', width: '98%', height: 25});
		createJqxDropDownListExt($("#uomChildOrg"), globalVar.uomArr, {displayMember: 'abbreviation', valueMember: 'uomId', width: '98%', height: 25, disabled: true});
	};
	var initInput = function(){
		$("#keyPerfIndicatorNameOrgAdd").jqxInput({width: '96%', height: 20});
		$("#weightChildOrg").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 3, decimalDigits: 1, symbolPosition: 'right', symbol: '%'});
		$("#targetChildOrg").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 12, decimalDigits: 1, max: 999999999999});
		$("#targetParentOrg").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 12, decimalDigits: 1, max: 999999999999, disabled: true});
	};
	var initWindow = function(){
		createJqxWindow($("#AddAllocateForOrgWindow"), 430, 380);
	};
	var initEvent = function(){
		$("#addAllocateChildOrgBtn").click(function(){
			openJqxWindow($("#AddAllocateForOrgWindow"));
		});
		$("#AddAllocateForOrgWindow").on('open', function(event){
			
		});
		$("#AddAllocateForOrgWindow").on('close', function(event){
			Grid.clearForm($("#AddAllocateForOrgWindow"));
		});
		$("#cancelAddAllocateChildOrg").click(function(event){
			$("#AddAllocateForOrgWindow").jqxWindow('close');
		});
		$("#saveAddAllocateChildOrg").click(function(event){
			var valid = $("#AddAllocateForOrgWindow").jqxValidator('validate');
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
		$("#AddAllocateForOrgWindow").jqxValidator({
			rules: [
				{input : '#keyPerfIndicatorNameOrgAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#partyGroupAllocateList', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#weightChildOrg', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#targetChildOrg', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#keyPerfIndAllocateOrg', message : uiLabelMap.FieldRequired, action : 'blur',
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
		data.partyTargetName = $("#keyPerfIndicatorNameOrgAdd").val();
		data.partyId = $("#partyGroupAllocateList").val();
		data.keyPerfIndicatorId = $("#keyPerfIndAllocateOrg").val();
		data.target = $("#targetChildOrg").val();
		data.weight = $("#weightChildOrg").val();
		data.parentPartyTargetId = globalVar.partyTargetId;
		$("#cancelAddAllocateChildOrg").attr("disabled","disabled");
		$("#saveAddAllocateChildOrg").attr("disabled","disabled");
		$("#loadingAddAllocateForChildOrg").show();
		$.ajax({
			type : 'POST',
			url : 'createKPIAllocateTarget',
			data : data,
			success : function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer : "#containerjqxgrid", opacity : 0.9});
					$("#AddAllocateForOrgWindow").jqxWindow('close');
					$("#childOrg" + data.keyPerfIndicatorId).jqxGrid('updatebounddata');
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
				$("#cancelAddAllocateChildOrg").removeAttr("disabled");
				$("#saveAddAllocateChildOrg").removeAttr("disabled");
				$("#loadingAddAllocateForChildOrg").hide();
			}
		});
	};
	var openWindow = function(keyPerfIndicatorId){
		prepareData(keyPerfIndicatorId);
		openJqxWindow($("#AddAllocateForOrgWindow"));
	};
	var prepareData = function(keyPerfIndicatorId){
		$("#keyPerfIndAllocateOrg").val(keyPerfIndicatorId);
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
				$("#uomChildOrg").val(response.uomId);
				$("#targetParentOrg").val(response.target);
			},
			complete: function(jqXHR, textStatus){
				enableAll();	
			}
		});
	};
	var disableAll = function(){
		$("#loadingAddAllocateForChildOrg").show();
		$("#cancelAddAllocateChildOrg").attr("disabled", "disabled");
		$("#saveAddAllocateChildOrg").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingAddAllocateForChildOrg").hide();
		$("#cancelAddAllocateChildOrg").removeAttr("disabled");
		$("#saveAddAllocateChildOrg").removeAttr("disabled");
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	addKeyPerfIndChildOrgObj.init();
})