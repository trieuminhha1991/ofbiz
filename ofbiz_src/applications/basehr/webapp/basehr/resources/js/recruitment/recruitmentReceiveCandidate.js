var recruitmentReceiveCandidateObj = (function(){
	var _data = null;
	var initContentWindow = function(){
		initJqxInput();
		initJqxDateTimeInput();
		initJqxNumberInput();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerAjaxReceiveCandidate"));
	};
	var init = function(){
		initJqxTreeBtn();
		initJqxDropDownList();
		createJqxWindow($("#recruitmentReceiveCandidateWindow"), 800, 320, initContentWindow);
		$("#recruitmentReceiveCandidateWindow").on('close', function(event){
			_data = null;
			clearDropDownContent($("#jqxTreeReceiveCandidate"), $("#dropDownButtonReceiveCandidate"));
			Grid.clearForm($(this));
			createCandidateUserLoginObj.resetUserLoginData()
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#emplPositionTypeIdReceiveCandidate"), "emplPositionTypeId", "description", 25, "98%");
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeReceiveCandidate"), "periodTypeId", "description", 25, "97%");
	};
	
	var initJqxInput = function(){
		$("#partyCodeReceiveCandidate").jqxInput({width: '96%', height: 20});
		$("#fullNameReceiveCandidate").jqxInput({width: '96%', height: 20, disabled: true});
	};
	
	var initJqxTreeBtn = function(){
		var expandTreeCompleteFunc = function(){
			if(_data.hasOwnProperty("partyGroupId")){
				$("#jqxTreeReceiveCandidate").jqxTree('selectItem', $("#" + _data.partyGroupId + "_treeReceiveCandidate")[0]);
			}
		};
		var config = {dropDownBtnWidth: 215, treeWidth: 215, expandCompleteFunc: expandTreeCompleteFunc};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeReceiveCandidate"), $("#dropDownButtonReceiveCandidate"), globalVar.rootPartyArr, "treeReceiveCandidate", "treeChildReceiveCandidate", config);
		$("#jqxTreeReceiveCandidate").on('select', function(event){
			setDropdownContent(args.element, $("#jqxTreeReceiveCandidate"), $("#dropDownButtonReceiveCandidate"));
			if(_data.hasOwnProperty("partyGroupId") && _data.hasOwnProperty("month") && _data.hasOwnProperty("year")){
				refreshEmplPositionTypeDropDown(_data.partyGroupId, _data.month, _data.year);
			}
		});
	};
	
	var refreshEmplPositionTypeDropDown = function(partyId, month, year){
		if(typeof(partyId) != 'undefined' && partyId.length > 0 && typeof(month) != 'undefined' && typeof(year) != 'undefined'){
			$("#emplPositionTypeIdReceiveCandidate").jqxDropDownList({disabled: true});
			$.ajax({
				url: 'getListAllEmplPositionTypeOfParty',
				data: {partyId: partyId, month: month, year: year},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						updateSourceDropdownlist($("#emplPositionTypeIdReceiveCandidate"), response.listReturn);
						if(_data.hasOwnProperty("emplPositionTypeId")){
							$("#emplPositionTypeIdReceiveCandidate").val(_data.emplPositionTypeId);
						}
					}
				},
				complete: function(jqXHR, textStatus){
					$("#emplPositionTypeIdReceiveCandidate").jqxDropDownList({disabled: false});
				}
			});
		}
	};
	var initJqxValidator = function(){
		$("#recruitmentReceiveCandidateWindow").jqxValidator({
			rules:[
				{
					input: '#dateJoinCompanyReceiveCandidate',
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{
					input: '#periodTypeReceiveCandidate',
					message: uiLabelMap.FieldRequired,
					action: 'blur',
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
	var initEvent = function(){
		$("#cancelReceiveCandidate").click(function(event){
			$("#recruitmentReceiveCandidateWindow").jqxWindow('close');
		});
		$("#saveReceiveCandidate").click(function(event){
			var valid = $("#recruitmentReceiveCandidateWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmReceiveCandidateBecomeEmpl,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		    	receiveCandidateBecomeEmployee();
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
		});
	};
	
	var receiveCandidateBecomeEmployee = function(){
		var dataCandidate = getData();
		var userLoginData = createCandidateUserLoginObj.getUserLoginData();
		var dataSubmit = $.extend({}, dataCandidate, userLoginData);
		$("#ajaxLoadingReceiveCandidate").show();
		$("#cancelReceiveCandidate").attr("disabled", "disabled");
		$("#saveReceiveCandidate").attr("disabled", "disabled");
		$.ajax({
			url: 'recruitmentReceiveCandidate',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					$("#recruitmentReceiveCandidateWindow").jqxWindow('close');
					$("#ntfRecruitRoundCandidateGrid").jqxNotification('closeLast');
					$("#ntfTextRecruitRoundCandidateGrid").text(response.successMessage);
					$("#ntfRecruitRoundCandidateGrid").jqxNotification({ template: 'info' });
					$("#ntfRecruitRoundCandidateGrid").jqxNotification('open');
					$("#recruitRoundCandidateGrid").jqxGrid('updatebounddata');
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
				$("#ajaxLoadingReceiveCandidate").hide();
				$("#cancelReceiveCandidate").removeAttr("disabled");
				$("#saveReceiveCandidate").removeAttr("disabled");
			}
		});
	};
	
	var getData = function(){
		var retData = {};
		var partyIdFromSelected = $("#jqxTreeReceiveCandidate").jqxTree("getSelectedItem");
		retData.partyIdTo = _data.partyId;
		retData.partyIdFrom = partyIdFromSelected.value;
		retData.partyCodeTo = $("#partyCodeReceiveCandidate").val();
		retData.emplPositionTypeId = $("#emplPositionTypeIdReceiveCandidate").val();
		retData.dateJoinCompany = $("#dateJoinCompanyReceiveCandidate").jqxDateTimeInput('val', 'date').getTime();
		retData.salaryBaseFlat = $("#salaryBaseFlatReceiveCandidate").val();
		retData.recruitmentPlanId = _data.recruitmentPlanId;
		retData.periodTypeId = $("#periodTypeReceiveCandidate").val();
		var probationaryDeadLine = $("#probationaryDeadLineReceiveCandidate").jqxDateTimeInput('val', 'date');
		if(probationaryDeadLine){
			retData.probationaryDeadLine = probationaryDeadLine.getTime();
		}
		return retData;
	};
	
	var initJqxDateTimeInput = function(){
		$("#dateJoinCompanyReceiveCandidate").jqxDateTimeInput({width: '98%', height: '25px'});
		$("#probationaryDeadLineReceiveCandidate").jqxDateTimeInput({width: '98%', height: '25px'});
		$("#probationaryDeadLineReceiveCandidate").val(null);
	};
	
	var initJqxNumberInput = function(){
		$("#salaryBaseFlatReceiveCandidate").jqxNumberInput({width: '98%', height: '23px', spinButtons: true, decimalDigits: 0, 
			digits: 9, max: 999999999, theme: 'olbius', min: 0});
	};
	
	var setData = function(data){
		_data = data;
		$("#partyCodeReceiveCandidate").val(_data.candidateId);
		$("#fullNameReceiveCandidate").val(data.fullName);
		$("#salaryBaseFlatReceiveCandidate").val(data.salaryAmount);
		$.ajax({
			url: 'getAncestorTreeOfPartyGroup',
			data: {partyId: _data.partyGroupId},
			type: 'POST',
			success: function(response){
				if(response.ancestorTree && response.ancestorTree.length > 0){
					for(var i = 0; i < response.ancestorTree.length; i++){
						$("#jqxTreeReceiveCandidate").jqxTree('expandItem', $("#" + response.ancestorTree[i] + "_treeReceiveCandidate")[0]);
					}
				}
			},
			complete: function(jqXHR, textStatus){
				if(_data.hasOwnProperty("partyGroupId")){
					$("#jqxTreeReceiveCandidate").jqxTree('selectItem', $("#" + _data.partyGroupId + "_treeReceiveCandidate")[0]);
				}
			}
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#recruitmentReceiveCandidateWindow"));
	};
	
	var showCandidateSetting = function(type){
		var rowindex = $("#recruitRoundCandidateGrid").jqxGrid('getselectedrowindex');
        var dataRecord = $("#recruitRoundCandidateGrid").jqxGrid('getrowdata', rowindex);
		if(type == "information"){
			editCandidateInfoObj.openWindow();//editCandidateInfoObj is denfined in RecruitmenEditCandidateInfo.js
        	editCandidateInfoObj.setData(dataRecord);
        	editCandidateInfoObj.setFunctionAfterUpdateCandidate(functionAfterUpdateCandidate);
		}else if(type == "contact"){
			editCandidateContactMechs.openWindow();//editCandidateContactMechs is denfined in RecruitmenEditCandidateInfo.js
        	editCandidateContactMechs.setData(dataRecord);
		}else if(type == "userLogin"){
			createCandidateUserLoginObj.openWindow();
			createCandidateUserLoginObj.setData(dataRecord);
		}
	};
	
	var functionAfterUpdateCandidate = function(message){
		$("#recruitRoundCandidateGrid").jqxGrid('updatebounddata');
	};
	
	return{
		init: init,
		openWindow: openWindow,
		setData: setData,
		showCandidateSetting: showCandidateSetting
	}
}());

var createCandidateUserLoginObj = (function(){
	var _userLoginData = {};
	var _partyId;
	var init = function(){
		initJqxInput();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerAjaxUserLogin"));
	};
	var initJqxInput = function(){
		$("#fullNameCandidateCreateUserLogin").jqxInput({width: '95%', height: 20, disabled: true});
		$("#userLoginIdCanidate").jqxInput({width: '95%', height: 20});
		$("#passwordCandidate").jqxPasswordInput({width: '95%', height: '20px', showStrength: false, showPasswordIcon: false});
		$("#confirmPasswordCandidate").jqxPasswordInput({width: '95%', height: '20px', showStrength: false, showPasswordIcon: false});
	};
	var openWindow = function(){
		openJqxWindow($("#createUserLoginCanidateWindow"));
	};
	var initJqxWindow = function(){
		createJqxWindow($("#createUserLoginCanidateWindow"), 480, 260);
		$("#createUserLoginCanidateWindow").on('close', function(event){
			_partyId = null;
			Grid.clearForm($(this));
		});
		$("#createUserLoginCanidateWindow").on('open', function(event){
			var userLoginData = createCandidateUserLoginObj.getUserLoginData();
			if(userLoginData.hasOwnProperty("userLoginId")){
				$("#userLoginIdCanidate").val(userLoginData.userLoginId);
				$("#passwordCandidate").val(userLoginData.password);
			}
		});
	};
	var initJqxValidator = function(){
		$("#createUserLoginCanidateWindow").jqxValidator({
			rules: [
			        {
			        	input: '#userLoginIdCanidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#userLoginIdCanidate',
			        	message : uiLabelMap.MustntHaveSpaceChar,
			        	action : 'blur',
			        	rule : function(input,commit){
			        		var value = input.val();
			        		var space = ' ';
			        		if(value && value.indexOf(space) > -1){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#passwordCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },     	
			        {
			        	input: '#confirmPasswordCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#confirmPasswordCandidate',
			        	message: uiLabelMap.password_did_not_match_verify_password,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if (input.val() === $('#passwordCandidate').val()) {
			        			return true;
			        		}
			        		return false;
			        	}
			        },
			        ]
		});
	};
	var initEvent = function(){
		$("#cancelCreateuserLoginCandidate").click(function(event){
			$("#createUserLoginCanidateWindow").jqxWindow('close');
		});
		$("#saveCreateuserLoginCandidate").click(function(event){
			var valid = $("#createUserLoginCanidateWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateNewUserLoginConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		createNewUserLogin();   	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
		});
	};
	var createNewUserLogin = function(){
		var dataSubmit = {};
		dataSubmit.partyId = _partyId;
		dataSubmit.userLoginId = $("#userLoginIdCanidate").val();
		dataSubmit.password = $("#passwordCandidate").val();
		$("#ajaxLoadingUserLoginCandidate").show();
		$("#cancelCreateuserLoginCandidate").attr("disabled", "disabled");
		$("#saveCreateuserLoginCandidate").attr("disabled", "disabled");
		$.ajax({
			url: 'checkUserLoginExists',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				var message = "";
				if(response.responseMessage == 'success'){
					$("#createUserLoginCanidateWindow").jqxWindow('close');
					_userLoginData = dataSubmit;
				}else{
					message = response.errorMessage;
					bootbox.dialog(message,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",    			    		    
							}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoadingUserLoginCandidate").hide();
				$("#cancelCreateuserLoginCandidate").removeAttr("disabled");
				$("#saveCreateuserLoginCandidate").removeAttr("disabled");
			}
		});
	};
	var setData = function(data){
		_partyId = data.partyId;
		$("#fullNameCandidateCreateUserLogin").val(data.fullName);
	};
	var getUserLoginData = function(){
		return _userLoginData;
	};
	var resetUserLoginData = function(){
		_userLoginData = {};
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData,
		getUserLoginData: getUserLoginData,
		resetUserLoginData: resetUserLoginData
	}
}());

$(document).ready(function(){
	recruitmentReceiveCandidateObj.init();
	createCandidateUserLoginObj.init();
});