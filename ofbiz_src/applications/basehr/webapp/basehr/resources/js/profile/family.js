var familyObject = (function(){
	var _partyId = "";
	var init = function(){
		initJqxDropDownList();
		initJqxWindow();
		initJqxInput();
		initJqxDateTimeInput();
		initJqxNumberInput();
		initEvent();
		initJqxCheckBox();
		initJqxValidator();
		create_spinner($("#spinner" + globalVar.addNewWindowId));
	};
	var initJqxDropDownList = function(){
		$("#relationship" + globalVar.addNewWindowId).jqxDropDownList({ source: partyRelationshipType, width: '98%', height: '25px', 
			selectedIndex: -1, displayMember: "partyRelationshipName", valueMember : "partyRelationshipTypeId", placeHolder: uiLabelMap.PleaseChooseAcc});
		$("#relationship" + globalVar.addNewWindowId).jqxDropDownList({autoDropDownHeight: true});
		
	};
	var initJqxWindow = function(){
		createJqxWindow($('#' + globalVar.addNewWindowId), 760, 350);
	};
	
	var initJqxInput = function(){
		$('#lastName' + globalVar.addNewWindowId).jqxInput({width : '96%',height : '20px'});
		$('#middleName' + globalVar.addNewWindowId).jqxInput({width : '96%',height : '20px'});
		$('#firstName' + globalVar.addNewWindowId).jqxInput({width : '96%',height : '20px'});
		$('#occupation' + globalVar.addNewWindowId).jqxInput({width : '96%',height : '20px'});
		$('#placeWork' + globalVar.addNewWindowId).jqxInput({width : '96%',height : '20px'});
	};
	
	var initJqxDateTimeInput = function(){
		$("#familyBirthDate" + globalVar.addNewWindowId).jqxDateTimeInput({width: '98%', height: '25px'});
		$("#dependentStartDate" + globalVar.addNewWindowId).jqxDateTimeInput({width: '98%', height: '25px', disabled: true});
		$("#dependentStartDate" + globalVar.addNewWindowId).val(null);
		$("#dependentEndDate" + globalVar.addNewWindowId).jqxDateTimeInput({width: '98%', height: '25px', disabled: true});
		$("#dependentEndDate" + globalVar.addNewWindowId).val(null);
	};
	
	var initEvent = function(){
		$('#save'  + globalVar.addNewWindowId).click(function(){
			var valid = $('#' + globalVar.addNewWindowId).jqxValidator('validate');
			if(!valid){
				return;
			}
			addNewFamilyBackground(true);
		});
		$('#cancel'  + globalVar.addNewWindowId).click(function(){
			$('#' + globalVar.addNewWindowId).jqxWindow('close');
		});
		$("#saveAndContinue"  + globalVar.addNewWindowId).click(function(event){
			var valid = $('#' + globalVar.addNewWindowId).jqxValidator('validate');
			if(!valid){
				return;
			}
			addNewFamilyBackground(false);
		});
		$('#' + globalVar.addNewWindowId).on('close',function(){
			Grid.clearForm($(this));
			$("#phoneNumber" + globalVar.addNewWindowId).val(null);
		});
		$('#dependentRegister' + globalVar.addNewWindowId).on('change', function(event){
			$("#dependentStartDate" + globalVar.addNewWindowId).jqxDateTimeInput({disabled: !event.args.checked});
			$("#dependentEndDate" + globalVar.addNewWindowId).jqxDateTimeInput({disabled: !event.args.checked});
		});
	};
	
	var disableBtn = function(){
		$("#save" + globalVar.addNewWindowId).attr("disabled", "disabled");
		$("#saveAndContinue" + globalVar.addNewWindowId).attr("disabled", "disabled");
		$("#cancel" + globalVar.addNewWindowId).attr("disabled", "disabled");
	};
	var enableBtn = function(){
		$("#save" + globalVar.addNewWindowId).removeAttr("disabled");
		$("#saveAndContinue" + globalVar.addNewWindowId).removeAttr("disabled");
		$("#cancel" + globalVar.addNewWindowId).removeAttr("disabled");
	};
	
	var initJqxValidator = function(){
		$('#' + globalVar.addNewWindowId).jqxValidator({
			rules : [
					{input : '#lastName' + globalVar.addNewWindowId,message : uiLabelMap.FieldRequired , action : 'keyup, blur',rule : 'required'},
					{input : '#firstName' + globalVar.addNewWindowId,message : uiLabelMap.FieldRequired, action : 'keyup, blur',rule : 'required'},
					{
						input : '#relationship' + globalVar.addNewWindowId, message : uiLabelMap.FieldRequired, 
						action : 'blur',
						rule : function (input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
			        {
			        	 input : '#dependentStartDate' + globalVar.addNewWindowId,
			        	 message : uiLabelMap.FieldRequired, 
			        	 action : 'blur',
			        	 rule : function (input, commit){
			        		 var check = $('#dependentRegister' + globalVar.addNewWindowId).jqxCheckBox('checked');
			        		 if(check && !input.val()){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			        },
			        {
			        	input : '#dependentEndDate' + globalVar.addNewWindowId,
			        	message : uiLabelMap.ThruDateMustGreaterThanFromDate, 
			        	action : 'blur',
			        	rule : function (input, commit){
			        		var startDate = $('#dependentStartDate' + globalVar.addNewWindowId).jqxDateTimeInput('val', 'date');
			        		var endDate = $(input).jqxDateTimeInput('val', 'date');
			        		if(startDate && endDate && startDate >= endDate){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#phoneNumber' + globalVar.addNewWindowId,message : uiLabelMap.HRPhoneIsNotValid,
			        	action : 'keyup, blur',
			        	rule : function (input, commit){
			        		var value = input.val();
                            var regular = /^\d+$/;;
                            if(!regular.test(value)){
                            	return false;
                            }
                            return true
			        	}
			        },
			        {
			        	input : '#firstName' + globalVar.addNewWindowId, message : uiLabelMap.HRCharacterIsNotValid,
			        	action : 'keyup, blur',
			        	rule : function(input, commit){
			        		var value = input.val();
                            var regular = /[~`!#@$%\^&*+=_\/\[\]\-\()';,.{}|\\":<>\?\s\d+]/;
                            if(regular.test(value)){
                            	return false;
                            }
                            return true
			        	}
			        },
			        {
			        	input  : '#lastName' + globalVar.addNewWindowId, message : uiLabelMap.HRCharacterIsNotValid,
			        	action : 'keyup, change',
			        	rule : function(input, commit){
                            var value = input.val();
                            var regular = /[~`!#@$%\^&*+=_\/\[\]\-\()';,.{}|\\":<>\?\s\d+]/;
                            if(regular.test(value)){
                            	return false;
                            }
                            return true
			        	}
			        },
			        {
			        	input : '#middleName' + globalVar.addNewWindowId, message : uiLabelMap.HRCharacterIsNotValid,
			        	action : 'keyup, blur',
			        	rule : function(input, commit){
			        		var value = input.val();
                            var regular = /[~`!#@$%\^&*+=_\/\[\]\-\()';,.{}|\\":<>\?\s\d+]/;
                            if(regular.test(value)){
                            	return false;
                            }
                            return true
			        	}
			        },
			        {
			        	input : '#familyBirthDate' + globalVar.addNewWindowId, message : uiLabelMap.FieldRequired,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		if(!$(input).val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#familyBirthDate' + globalVar.addNewWindowId, message : uiLabelMap.HRBirthDateBeforeToDay,
			        	action : 'change, blur',
			        	rule : function(input, commit){
			        		var now = new Date();
			        		if($(input).jqxDateTimeInput('val', 'date') >= now){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
                    {
                        input : '#occupation' + globalVar.addNewWindowId, message : uiLabelMap.HRCharacterIsNotValid,
                        action : 'keyup, blur',
                        rule : function(input, commit){
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(), uiLabelMap.HRCheckSpecialCharacter);
                        }
                    },
                    {
                        input : '#placeWork' + globalVar.addNewWindowId, message : uiLabelMap.HRCharacterIsNotValid,
                        action : 'keyup, blur',
                        rule : function(input, commit){
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(), uiLabelMap.HRCheckSpecialCharacter);
                        }
                    },
	         ]
		});
	};
	
	var initJqxCheckBox = function(){
		$('#dependentRegister' + globalVar.addNewWindowId).jqxCheckBox({ width: '20px', height: '25px', checked: false});
	};
	
	var initJqxNumberInput = function(){
		$("#phoneNumber" + globalVar.addNewWindowId).jqxInput({ width: '96%', height: 20});
		$("#phoneNumber" + globalVar.addNewWindowId).val(null);
	};
	var getData = function(){
		var row = {};
		row = {
			firstName : $('#firstName' + globalVar.addNewWindowId).val(),
			middleName : $('#middleName' + globalVar.addNewWindowId).val(),
			lastName : $('#lastName' + globalVar.addNewWindowId).val(),
			partyRelationshipTypeId : $('#relationship' + globalVar.addNewWindowId).val(),
			occupation : $('#occupation' + globalVar.addNewWindowId).val(),
			placeWork : $("#placeWork" + globalVar.addNewWindowId).val(),
			phoneNumber : $('#phoneNumber' + globalVar.addNewWindowId).val(),
			
		};
		var birthDate = $('#familyBirthDate' + globalVar.addNewWindowId).jqxDateTimeInput('val', 'date');
		if(birthDate){
			row.birthDate = birthDate.getTime();
		}
		if(_partyId && _partyId.length > 0){
			row.partyId = _partyId;
		}
		if($('#dependentRegister' + globalVar.addNewWindowId).jqxCheckBox('checked')){
			row.isDependent = "Y";
			var dependentStartDate = $('#dependentStartDate' + globalVar.addNewWindowId).jqxDateTimeInput('val', 'date');
			if(dependentStartDate){
				row.dependentStartDate = dependentStartDate.getTime(); 
			}
			var dependentEndDate = $('#dependentEndDate' + globalVar.addNewWindowId).jqxDateTimeInput('val', 'date');
			if(dependentEndDate){
				row.dependentEndDate = dependentEndDate.getTime(); 
			}
		}else{
			row.isDependent = "N";
		}
		return row;
	};
	var addNewFamilyBackground = function(isCloseWindow){
		disableBtn();
		$("#loading" + globalVar.addNewWindowId).show();
		var data = getData();
		$.ajax({
			url: 'createPersonFamilyBackground',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					if(isCloseWindow){
						$('#' + globalVar.addNewWindowId).jqxWindow('close');
					}else{
						Grid.clearForm($('#' + globalVar.addNewWindowId));
						$("#phoneNumber" + globalVar.addNewWindowId).val(null);
					}
					$('#' + globalVar.addNewWindowId).trigger('createPersonFamilySuccess');
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
				enableBtn();
				$("#loading" + globalVar.addNewWindowId).hide();
			}
		});
	};
	var setData = function(data){
		_partyId = data.partyId;
	};
	return{
		init: init,
		setData: setData
	}
})();
	
$(document).ready(function(){
	familyObject.init();
});