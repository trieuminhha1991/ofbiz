$.jqx.theme = 'olbius';
var theme = theme;
function updateSourceJqxDropdownList(dropdownlistEle, data, url, selectItem){
	$.ajax({
		url: url,
		data: data,
		type: 'POST',
		success: function(response){
			var listGeo = response.listReturn;
			if(listGeo && listGeo.length > -1){
				updateSourceDropdownlist(dropdownlistEle, listGeo);
				if(selectItem != 'undefinded'){
					dropdownlistEle.jqxDropDownList('selectItem', selectItem);
				}
			}
		}
	});
}

function functionAfterAddRow(){
	if(typeof(familyObject) != 'undefinded'){
		familyObject.enableBtn();
	}
	globalVar.updatePersonFamilyBackground = true;
}

var profileObject = (function(){
	var firstName;
	var lastName;
	var middleName;
	var init = function(){
		initBtnEvent();
		initJqxInput();
		initJqxPasswordInput();
		initJqxValidator();
		initJqxWindow();
	};
	
	var initJqxValidator = function(){
		$('#changeFullNameWindow').jqxValidator({
			rules : [
					{input : '#lastNameEdit',message : uiLabelMap.messageRequire , action : 'blur',rule : 'required'},
					{input : '#firstNameEdit',message : uiLabelMap.messageRequire, action : 'blur',rule : 'required'},
					{
			        	 input : '#lastNameEdit',
			        	 message : uiLabelMap.IllegalCharacters,
			        	 action : 'blur',
			        	 rule : function(input,commit){
			        		 var character = $(input).val();
			        		 var space = " ";
			        		 if(isContainSpecialCharAndNumb(character) || character.indexOf(space) > -1){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#middleNameEdit',
			        	 message : uiLabelMap.IllegalCharacters,
			        	 action : 'blur',
			        	 rule : function(input,commit){
			        		 var character = input.val();
			        		 if(isContainSpecialCharAndNumb(character)){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#firstNameEdit',
			        	 message : uiLabelMap.IllegalCharacters,
			        	 action : 'blur',
			        	 rule : function(input,commit){
			        		 var character = input.val();
			        		 var space = " ";
			        		 if(isContainSpecialCharAndNumb(character) || character.indexOf(space) > -1){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         }
					]
		});
		$('#changePasswordWindow').jqxValidator({
			rules : [
			         {input : '#currentPassword',message : uiLabelMap.messageRequire , action : 'blur',rule : 'required'},
			         {input : '#newPassword',message : uiLabelMap.messageRequire, action : 'blur',rule : 'required'},
			         { input: "#verifyNewPassword", message: uiLabelMap.messageRequire, action: 'keyup, blur', rule: 'required' },
                     {
                         input: "#verifyNewPassword", message: uiLabelMap.PasswordConfirmNotMatch, action: 'keyup, blur', 
                         rule: function (input, commit) {
                             var firstPassword = $("#newPassword").jqxPasswordInput('val');
                             var secondPassword = $("#verifyNewPassword").jqxPasswordInput('val');
                             return firstPassword == secondPassword;
                         }
                     }
			         ]
		});
	};
	
	var initJqxInput = function(){
		$("#firstNameEdit").jqxInput({width: '97%', height: 20});
		$("#middleNameEdit").jqxInput({width: '97%', height: 20});
		$("#lastNameEdit").jqxInput({width: '97%', height: 20});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#changeFullNameWindow"), 400, 220);
		createJqxWindow($("#changePasswordWindow"), 500, 220);
		$("#changeFullNameWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#changePasswordWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#changeFullNameWindow").on('open', function(event){
			$("#firstNameEdit").val(firstName);
			if(middleName){
				$("#middleNameEdit").val(middleName);
			}
			$("#lastNameEdit").val(lastName);
		});
	};
	
	var initJqxPasswordInput = function(){
		$("#currentPassword").jqxPasswordInput({width: '97%', height: 20, showStrength: false, showPasswordIcon: false});
		$("#newPassword").jqxPasswordInput({width: '97%', height: 20, showStrength: false, showPasswordIcon: false});
		$("#verifyNewPassword").jqxPasswordInput({width: '97%', height: 20, showStrength: false, showPasswordIcon: false});
	};
	
	var initBtnEvent = function(){
		$("#changeFullName").click(function(event){
			openJqxWindow($("#changeFullNameWindow"));
		});
		
		$("#changePassword").click(function(event){
			openJqxWindow($("#changePasswordWindow"));
		});
		
		$("#alterCancelEditFullName").click(function(event){
			$("#changeFullNameWindow").jqxWindow('close');
		});
		
		$("#cancelChangePw").click(function(event){
			$("#changePasswordWindow").jqxWindow('close');
		});
		$("#saveChangePw").click(function(event){
			var valid = $("#changePasswordWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			Loading.show('loadingMacro');
			var dataSubmit = {};
			dataSubmit.currentPassword = $("#currentPassword").val();
			dataSubmit.newPassword = $("#newPassword").val();
			dataSubmit.newPasswordVerify = $("#verifyNewPassword").val();
			disablePwWindow();
			$.ajax({
				url: 'updateEmplPassword',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						$("#changePasswordWindow").jqxWindow('close');
						var message = "<i class='fa-info-circle open-sans icon-modal-alert-info'></i><span class='message-content-alert-info'>";
						message += response.successMessage;
						message += "</span>";
						bootbox.dialog(message,
								 [{
					    		    "label" : uiLabelMap.CommonSubmit,
					    		    "class" : "btn-primary btn-small icon-ok open-sans",
					    		 }]
							 );		
					}else{
						bootbox.dialog(response.errorMessage,
								 [{
					    		    "label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger icon-remove btn-small",
					    		 }]
							 );	
					}
				},
				complete: function( jqXHR, textStatus){
					enablePwWindow();
					Loading.hide('loadingMacro');
				} 
			});
		});
		$("#alterSaveEditFullName").click(function(event){
			var valid = $("#changeFullNameWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			disableWindow();
			var dataSubmit = {};
			dataSubmit.firstName = $("#firstNameEdit").val();
			dataSubmit.lastName = $("#lastNameEdit").val();
			if($("#middleNameEdit").val()){
				dataSubmit.middleName = $("#middleNameEdit").val();
			}
			$.ajax({
				url: 'updatePersonName',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						$("#changeFullNameWindow").jqxWindow('close');
						bootbox.dialog(response.successMessage,
								 [{
					    		    "label" : uiLabelMap.CommonSubmit,
					    		    "class" : "btn-primary btn-small icon-ok open-sans",
					    		 }]
							 );		
						setPersonName(dataSubmit.firstName, dataSubmit.middleName, dataSubmit.lastName);
						$("#changeFullName").text(dataSubmit.lastName + " " + dataSubmit.middleName + " " + dataSubmit.firstName);
					}else{
						bootbox.dialog(response.errorMessage,
								 [{
					    		    "label" : uiLabelMap.CommonCancel,
					    		    "class" : "btn-danger icon-remove btn-small",
					    		 }]
							 );	
					}
				},
				complete: function( jqXHR, textStatus){
					enableWindow();
				}
			});
		});
	};
	
	var disablePwWindow = function(){
		$("#changePasswordWindow").jqxWindow({disabled: true});
		$("#currentPassword").jqxPasswordInput({disabled: true});
		$("#newPassword").jqxPasswordInput({disabled: true});
		$("#verifyNewPassword").jqxPasswordInput({disabled: true});
		$("#cancelChangePw").attr("disabled", "disabled");
		$("#saveChangePw").attr("disabled", "disabled");
	};
	var enablePwWindow = function(){
		$("#currentPassword").jqxPasswordInput({disabled: false});
		$("#newPassword").jqxPasswordInput({disabled: false});
		$("#verifyNewPassword").jqxPasswordInput({disabled: false});
		$("#changePasswordWindow").jqxWindow({disabled: false});
		$("#cancelChangePw").removeAttr("disabled");
		$("#saveChangePw").removeAttr("disabled");
	};
	
	var disableWindow = function(){
		$("#firstNameEdit").jqxInput({disabled: true});
		$("#middleNameEdit").jqxInput({disabled: true});
		$("#lastNameEdit").jqxInput({disabled: true});
		$("#alterCancelEditFullName").attr("disabled", "disabled");
		$("#alterSaveEditFullName").attr("disabled", "disabled");
		$("#changeFullNameWindow").jqxWindow({disabled:true});
	};
	
	var enableWindow = function(){
		$("#firstNameEdit").jqxInput({disabled: false});
		$("#middleNameEdit").jqxInput({disabled: false});
		$("#lastNameEdit").jqxInput({disabled: false});
		$("#alterCancelEditFullName").removeAttr("disabled");
		$("#alterSaveEditFullName").removeAttr("disabled");
		$("#changeFullNameWindow").jqxWindow({disabled:false});
	}
	
	var setPersonName = function(fName, mName, lName){
		firstName = fName;
		middleName = mName;
		lastName = lName;
	};
	return{
		init: init,
		setPersonName: setPersonName
	}
}());
$(document).ready(function(){
	profileObject.init();
	profileObject.setPersonName(globalVar.firstName, globalVar.middleName, globalVar.lastName);
});
