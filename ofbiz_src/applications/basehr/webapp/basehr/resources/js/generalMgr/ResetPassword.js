var resetPasswordObj = (function(){
	var _data;
	var init = function(){
		initWindow();
		initDropDownList();
		initEvent();
		initValidator();
	};
	var initWindow = function(){
		createJqxWindow($("#resetPasswordWindow"), 400, 140);
	}
	var openWindow = function(data){
		_data = data;
		openJqxWindow($("#resetPasswordWindow"));
	};
	var initDropDownList = function(){
		createJqxDropDownListExt($("#changePasswordUserLogin"), [], {valueMember: 'userLoginId', displayMember: 'userLoginId', width: '97%', height: 25});
	};
	var initEvent = function(){
		$("#resetPasswordWindow").on('open', function(event){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getListUserLoginByPartyId',
				type: 'POST',
				data: {partyId: _data.partyId},
				success: function(response){
					if(response.responseMessage == "error"){
						bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
						return;
					}
					updateSourceDropdownlist($("#changePasswordUserLogin"), response.listData);
					$("#changePasswordUserLogin").jqxDropDownList({selectedIndex: 0});
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
		$("#resetPasswordWindow").on('close', function(event){
			updateSourceDropdownlist($("#changePasswordUserLogin"), []);
		});
		$("#cancelResetPassword").click(function(){
			$("#resetPasswordWindow").jqxWindow('close');
		});
		$("#saveResetPassword").click(function(){
			var valid = $("#resetPasswordWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ResetPasswordConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		 resetPassword();
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
		$("#resetPasswordWindow").jqxValidator({
			rules: [
			        {input: '#changePasswordUserLogin', message: uiLabelMap.FieldRequired, action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			]
		});
	};
	var resetPassword = function(){
		Loading.show('loadingMacro');
		var userLoginId = $("#changePasswordUserLogin").val();
		$.ajax({
			url: 'resetPasswordUserLoginToDefault',
			type: 'POST',
			data: {userLoginId: userLoginId},
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#resetPasswordWindow").jqxWindow('close');
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
	resetPasswordObj.init();
});
