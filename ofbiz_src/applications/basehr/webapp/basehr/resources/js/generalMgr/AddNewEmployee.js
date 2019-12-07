var initJqxNotification = function(){
	$("#updateNotification").jqxNotification({
        width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotification",
        autoOpen: false, autoClose: true
    });
};

var initWizard = (function(){
	var init = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	return addNewEmplGeneralInfo.validate();
	        }else if (info.step == 2 && (info.direction == "next")) {
	        	return (permanentResInfo.validate() && currResInfo.validate() && emailContactObj.validate() && phoneNumberContactObj.validate()) ;
	        }else if(info.step == 3 && (info.direction == "next")){
	        	if(!emplWorkingInfo.validate()){
	        		return false;
	        	}
	        	$("#userLoginId" + globalVar.defaultSuffix).val(addNewEmplGeneralInfo.getPartyCode());//addNewEmplGeneralInfo is defined in AddNewEmployeeProfileInfo.js 
	        }
	        if(info.direction == "previous"){
	        	addNewEmplGeneralInfo.hideValidate();
	        	permanentResInfo.hideValidate();
	        	emplWorkingInfo.hideValidate();
	        	userLoginInfo.hideValidate();
	        	emailContactObj.hideValidate();
                phoneNumberContactObj.hideValidate();
	        }
	    }).on('finished', function(e) {
	    	var valid = userLoginInfo.validate();
	    	if(!valid){
	    		return;
	    	}
	    	bootbox.dialog(uiLabelMap.CreateNewEmployeeConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		createNewEmployee();   	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
	    }).on('stepclick', function(e){
	        //return false;//prevent clicking on steps
	    });
	};
	
	var resetStep = function(){
		$('#fuelux-wizard').wizard('previous');
		$('#fuelux-wizard').wizard('previous');
		$('#fuelux-wizard').wizard('previous');
	};
	
	var createNewEmployee = function(){
			var dataSubmit = {};
	    	var generalInfoData = addNewEmplGeneralInfo.getData();
	    	var permanentResInfoData = permanentResInfo.getData();
	    	var currResInfoData = currResInfo.getData();
	    	var emplWorkingInfoData = emplWorkingInfo.getData();
	    	var userLoginInfoData = userLoginInfo.getData();
	    	$.extend(dataSubmit, generalInfoData);
	    	$.extend(dataSubmit, userLoginInfoData);
	    	$.extend(dataSubmit, emplWorkingInfoData);
	    	dataSubmit.permanentRes = JSON.stringify(permanentResInfoData);
	    	dataSubmit.currRes = JSON.stringify(currResInfoData);
	    	var formData = new FormData();
	    	var imageAvatar = addNewEmplGeneralInfo.getImageAvatar();
	    	if(imageAvatar){
	    		formData.append(imageAvatar.name, imageAvatar);
	    	}
	    	for(var key in dataSubmit){
	    		if(typeof(dataSubmit[key]) != "undefined"){
	    			formData.append(key, dataSubmit[key]);
	    		}
			}
	    	$("#ajaxLoading").show();
	    	$("#btnNext").attr("disabled", "disabled");
	    	$("#btnPrev").attr("disabled", "disabled");
	    	userLoginInfo.disabled();
	    	$.ajax({
	    		url: 'createNewEmployee',
	    		data: formData,
				type: 'POST',
				cache : false,
				contentType : false,
				processData : false,
	    		success: function(response){
	    			if(response.responseMessage == 'success'){
	    				$("#addNewEmployeeWindow").jqxWindow('close');
	    				$("#updateNotification").jqxNotification('closeLast');
	    				$("#notificationText").text(response.successMessage);
						$("#updateNotification").jqxNotification({ template: 'info' });
						$("#updateNotification").jqxNotification('open');
						$("#jqxgrid").jqxGrid('updatebounddata');
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
	    			$("#ajaxLoading").hide();
	    			$("#btnNext").removeAttr("disabled");
	    	    	$("#btnPrev").removeAttr("disabled");
	    	    	userLoginInfo.enabled();
	    		}
	    	});
	};
	return {
		init: init,
		resetStep: resetStep
	}
}());

var emplWorkingInfo = (function(){
	var init = function(){
		//var config = {dropDownBtnWidth: 222, treeWidth: 222};
		initJqxDropDownList();
		initJqxDropDownEvent();
		initJqxTreeBtn();
		initJqxTreeEvent();
		initJqxDateTimeInput();
		initJqxNumberInput();
		initEvent();
		initJqxInput();
		initDateTime();
		initJqxValidator();
	};
	
	var initJqxTreeBtn = function(){
		var config = {dropDownBtnWidth: '97%', treeWidth: 250};
		globalObjectAddNewEmpl.createJqxTreeDropDownBtn($("#jqxTree" + globalVar.defaultSuffix), $("#dropDownButton" + globalVar.defaultSuffix), globalVar.rootPartyArr, "treeNew", "treeChildNew", config);
	};
		
	var initJqxTreeEvent = function(){
		$("#jqxTree" + globalVar.defaultSuffix).on('select', function(event){
			var id = event.args.element.id;
		  	var item = $("#jqxTree" + globalVar.defaultSuffix).jqxTree('getItem', args.element);
			setDropdownContent(item, $(this), $("#dropDownButton" + globalVar.defaultSuffix));
			var value = $("#jqxTree" + globalVar.defaultSuffix).jqxTree('getItem', $("#"+id)[0]).value;
			getEmplPositionTypeInOrg(value);
		});
	};
	
	var initJqxDateTimeInput = function(){
		$("#dateJoinCompany" + globalVar.defaultSuffix).jqxDateTimeInput({width: '97%', height: 25});
		$("#insParticipateFrom" + globalVar.defaultSuffix).jqxDateTimeInput({width: '97%', height: 25});
		$("#insParticipateFrom" + globalVar.defaultSuffix).val(null);
		$("#resignDate" + globalVar.defaultSuffix).jqxDateTimeInput({width: '97%', height: 25, disabled: true});
		$("#resignDate" + globalVar.defaultSuffix).val(null);
	};
	var initDateTime = function(){
		var monthDataRequire = [];
		var monthDataNotRequire = [{month: -1, description: "-----------"}];
		for(var i = 0; i < 12; i++){
			monthDataRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
			monthDataNotRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		var date = new Date();
		var year = date.getFullYear();
		createJqxDropDownList(monthDataNotRequire, $("#monthFromHealthIns" + globalVar.defaultSuffix), "month", "description", 25, 100);
		$("#yearFromHealthIns" + globalVar.defaultSuffix).jqxNumberInput({ width: 80, height: 25, spinButtons: true, inputMode: 'simple', 
			decimalDigits: 0, disabled: true, decimal: year});
		createJqxDropDownList(monthDataNotRequire, $("#monthThruHealthIns" + globalVar.defaultSuffix), "month", "description", 25, 100);
		$("#yearThruHealthIns" + globalVar.defaultSuffix).jqxNumberInput({ width: 80, height: 25, spinButtons: true, 
			inputMode: 'simple', decimalDigits: 0, disabled: true, decimal: year});

		createJqxDropDownList(monthDataRequire, $("#monthFromParticipate" + globalVar.defaultSuffix), "month", "description", 25, 100);
		$("#yearFromParticipate" + globalVar.defaultSuffix).jqxNumberInput({ width: 80, height: 25, spinButtons: true, 
			inputMode: 'simple', decimalDigits: 0, disabled: true, decimal: year});
		
		$("#monthFromHealthIns" + globalVar.defaultSuffix).jqxDropDownList({selectedIndex: 0});
		$("#monthThruHealthIns" + globalVar.defaultSuffix).jqxDropDownList({selectedIndex: 0});
		$("#monthFromParticipate" + globalVar.defaultSuffix).val(date.getMonth());
	};
	var initJqxNumberInput = function(){
		$("#salaryBaseFlat" + globalVar.defaultSuffix).jqxNumberInput({width: '97%', height: 25, spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius'});
		$("#insuranceSalary" + globalVar.defaultSuffix).jqxNumberInput({width: '97%', height: 25, spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius'});
	};
	
	var initJqxInput = function(){
		$("#insuranceSocialNbr" + globalVar.defaultSuffix).jqxInput({width: '95%', height: 20});
		$("#insuranceHealth" + globalVar.defaultSuffix).jqxInput({width: '95%', height: 20});
		$("#healthCareProvider" + globalVar.defaultSuffix).jqxInput({width: '82%', height: 20, disabled: true, valueMember: 'hospitalId', displayMember: 'hospitalName'});
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			$("#insuranceType" + insuranceType.insuranceTypeId + globalVar.defaultSuffix).jqxCheckBox({ width: 60, height: 25, 
				checked: isCompulsory == "Y", disabled: isCompulsory == "Y"});
		});
		$("#isParticipateIns").jqxCheckBox({width: 180, height: 25, checked: false});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodType" + globalVar.defaultSuffix), "periodTypeId", "description", 25, "97%");
		createJqxDropDownList([], $("#emplPositionTypeId" + globalVar.defaultSuffix), "emplPositionTypeId", "description", 25, "83.5%");
		createJqxDropDownList(initSourceGeneral.statusWorkingArr, $("#statusId" + globalVar.defaultSuffix), "statusId", "description", 25, "97%");
		createJqxDropDownList(initSourceGeneral.terminationReasonArr, $("#reasonResign" + globalVar.defaultSuffix), "terminationReasonId", "description", 25, "97%");
		$("#reasonResign" + globalVar.defaultSuffix).jqxDropDownList({disabled: true});
	};
	
	var resetData = function(){
		var date = new Date();
		var year = date.getFullYear();
		$("#monthFromHealthIns" + globalVar.defaultSuffix).jqxDropDownList({selectedIndex: 0});
		$("#monthThruHealthIns" + globalVar.defaultSuffix).jqxDropDownList({selectedIndex: 0});
		$("#yearFromHealthIns" + globalVar.defaultSuffix).val(year);
		$("#yearThruHealthIns" + globalVar.defaultSuffix).val(year);
		$("#monthFromParticipate" + globalVar.defaultSuffix).val(date.getMonth());
		
		$("#jqxTree" + globalVar.defaultSuffix).jqxTree('selectItem', null);
		$("#dropDownButton" + globalVar.defaultSuffix).val("");
		updateSourceDropdownlist($("#emplPositionTypeId" + globalVar.defaultSuffix), []);		
		$("#dateJoinCompany" + globalVar.defaultSuffix).val(new Date(globalVar.nowTimestamp));
		$("#salaryBaseFlat" + globalVar.defaultSuffix).val(0);
		$("#insuranceSocialNbr" + globalVar.defaultSuffix).val("");
		$("#insParticipateFrom" + globalVar.defaultSuffix).val(null);
		$("#insuranceSalary" + globalVar.defaultSuffix).val(0);
		$("#healthCareProvider" + globalVar.defaultSuffix).val("");
		$("#healthCareProvider" + globalVar.defaultSuffix).attr("data-value", "");
		$("#insuranceHealth" + globalVar.defaultSuffix).val("");
		$("#resignDate" + globalVar.defaultSuffix).jqxDateTimeInput({disabled: true});
		$("#resignDate" + globalVar.defaultSuffix).val(null);
		$("#statusId" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#reasonResign" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#periodType" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#reasonResign" + globalVar.defaultSuffix).jqxDropDownList({disabled: true});
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			$("#insuranceType" + insuranceType.insuranceTypeId + globalVar.defaultSuffix).jqxCheckBox({checked: isCompulsory == "Y"});
		});
		$("#isParticipateIns").jqxCheckBox({checked: false});
	};
	
	var initJqxDropDownEvent = function(){
		$("#statusId" + globalVar.defaultSuffix).on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if(value == 'EMPL_WORKING'){
					$("#reasonResign" + globalVar.defaultSuffix).jqxDropDownList({disabled: true});
					$("#resignDate" + globalVar.defaultSuffix).jqxDateTimeInput({disabled: true});
					$("#reasonResign" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
					$("#resignDate" + globalVar.defaultSuffix).val(null);
				}else{
					$("#reasonResign" + globalVar.defaultSuffix).jqxDropDownList({disabled: false});
					$("#resignDate" + globalVar.defaultSuffix).jqxDateTimeInput({disabled: false});
				}
			}
		});
	};
	
	var getData = function(){
		var isParticipateIns = $("#isParticipateIns").jqxCheckBox('checked');
		var data = {
			salaryBaseFlat: $("#salaryBaseFlat" + globalVar.defaultSuffix).val(),
			dateJoinCompany: $("#dateJoinCompany" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date').getTime(),
			emplPositionTypeId: $("#emplPositionTypeId" + globalVar.defaultSuffix).val()
		};
		var item = $("#jqxTree" + globalVar.defaultSuffix).jqxTree('getSelectedItem');
		data.partyIdFrom = item.value;
		var thruDate = $("#resignDate" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		data.periodTypeId = $("#periodType" + globalVar.defaultSuffix).val();
		
		if(isParticipateIns){
			data.insuranceSocialNbr = $("#insuranceSocialNbr" + globalVar.defaultSuffix).val();
			data.insHealthCard = $("#insuranceHealth" + globalVar.defaultSuffix).val();
			data.insuranceSalary = $("#insuranceSalary" + globalVar.defaultSuffix).val();
			if($("#insParticipateFrom" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date')){
				data.dateParticipateIns = $("#insParticipateFrom" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date').getTime();
			}
			var month = $("#monthFromHealthIns" + globalVar.defaultSuffix).val();
			if(month > -1){
				var year = $("#yearFromHealthIns" + globalVar.defaultSuffix).val();
				var heathInsuranceFromDate = new Date(year, month, 1);
				data.heathInsuranceFromDate = heathInsuranceFromDate.getTime();
				month = $("#monthThruHealthIns" + globalVar.defaultSuffix).val();
				if(month > -1){
					year = $("#yearThruHealthIns" + globalVar.defaultSuffix).val();
					var heathInsuranceThruDate = new Date(year, month, 1);
					data.heathInsuranceThruDate = heathInsuranceThruDate.getTime();
				}
			}
			month = $("#monthFromParticipate" + globalVar.defaultSuffix).val();
			year = $("#yearFromParticipate" + globalVar.defaultSuffix).val();
			var participateFromDate = new Date(year, month, 1);
			data.participateFromDate = participateFromDate.getTime();
			
			var insuranceTypeArr = [];
			globalVar.insuranceTypeArr.forEach(function(insuranceType){
				var isCompulsory = insuranceType.isCompulsory;
				if("Y" != isCompulsory){
					if($("#insuranceType" + insuranceType.insuranceTypeId + globalVar.defaultSuffix).jqxCheckBox('checked')){
						insuranceTypeArr.push(insuranceType.insuranceTypeId);
					}
				}
			});
			if(insuranceTypeArr.length > 0){
				data.insuranceTypeNotCompulsory = JSON.stringify(insuranceTypeArr);
			}
			
			if($("#healthCareProvider" + globalVar.defaultSuffix).attr("data-value")){
				data.healthCareProvider = $("#healthCareProvider" + globalVar.defaultSuffix).attr("data-value");
			}
			data.isParticipateIns = "Y";
		}else{
			data.isParticipateIns = "N";
		}
		return data;
	};
	
	
	var getEmplPositionTypeInOrg = function(partyGroupId){
		$("#emplPositionTypeId" + globalVar.defaultSuffix).jqxDropDownList({disabled: true});
		$.ajax({
			url: 'getListEmplPositionTypeByParty',
			data: {partyId: partyGroupId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					updateSourceDropdownlist($("#emplPositionTypeId" + globalVar.defaultSuffix), response.listReturn);
				}
			},
			complete: function(jqXHR, status){
				$("#emplPositionTypeId" + globalVar.defaultSuffix).jqxDropDownList({disabled: false});
			}
		});
	};
	
	var hideValidate = function(){
		$("#employmentInfo").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#employmentInfo").jqxValidator({
			scroll: false,
			rules: [
			        {
			        	input: '#dropDownButton' + globalVar.defaultSuffix,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var item = $("#jqxTree" + globalVar.defaultSuffix).jqxTree('getSelectedItem');
			        		if(!item){
			        			return false;
			        		}
			        		return true;
			        	}
			        },			        	       
			        {
			        	input: '#addEmplPositionTypeBtnaddNew',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'none',
			        	rule: function (input, commit){
			        		var value = $('#emplPositionTypeId' + globalVar.defaultSuffix).val() 
			        		if(!value){
			        			return false;
			        		}
			        		return true;
			        	}
			        },			        	       
			        {
			        	input: '#periodType' + globalVar.defaultSuffix,
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
			        	input: '#dateJoinCompany' + globalVar.defaultSuffix,
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
			        	input: '#salaryBaseFlat' + globalVar.defaultSuffix,
			        	message: uiLabelMap.AmountValueGreaterThanZero,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(input.val() <= 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#resignDate' + globalVar.defaultSuffix,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var statusId = $("#statusId" + globalVar.defaultSuffix).val();				        		
			        		if(statusId && statusId != 'EMPL_WORKING'){
			        			if(!input.val()){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        { input: '#yearThruHealthIns' + globalVar.defaultSuffix, message: uiLabelMap.GTDateFieldRequired, action: 'blur',
						rule : function(input, commit){
							var monthThru = $("#monthThruHealthIns" + globalVar.defaultSuffix).val();
							var monthFrom = $("#monthFromHealthIns" + globalVar.defaultSuffix).val();
							if(monthThru > -1 && monthFrom > -1){
								var yearThru = $("#yearThruHealthIns" + globalVar.defaultSuffix).val();
								var yearFrom = $("#yearFromHealthIns" + globalVar.defaultSuffix).val();
								var fromDate = new Date(yearFrom, monthFrom, 1);
								var thruDate = new Date(yearThru, monthThru, 1);
								if(fromDate > thruDate){
									return false;
								}
							}
							return true;
						}
					}, 
			        {
			        	input : "#insuranceSocialNbr" + globalVar.defaultSuffix,
			        	message : uiLabelMap.OnlyInputNumberGreaterThanZero,
			        	action : 'blur',
			        	rule : function(input,commit){
			        		var result = parseInt(input.val());
			        		if(result < 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : "#insuranceHealth" + globalVar.defaultSuffix,
			        	message : uiLabelMap.OnlyInputNumberGreaterThanZero,
			        	action : 'blur',
			        	rule : function(input,commit){
			        		var result = parseInt(input.val());
			        		if(result < 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : "#insuranceSalary" + globalVar.defaultSuffix,
			        	message : uiLabelMap.OnlyInputNumberGreaterThanZero,
			        	action : 'blur',
			        	rule : function(input,commit){
			        		if(input.val() < 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : "#healthCareProvider" + globalVar.defaultSuffix, message : uiLabelMap.FieldRequired, action : 'blur',
			        	rule : function(input,commit){
			        		var isParticipateIns = $("#isParticipateIns").jqxCheckBox('checked');
			        		if(isParticipateIns){
			        			var insuranceHealth = $("#insuranceHealth" + globalVar.defaultSuffix).val();
			        			if(insuranceHealth.length > 0){
			        				var value = $(input).attr("data-value");
			        				if(!value){
			        					return false;
			        				}
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : "#yearFromHealthIns" + globalVar.defaultSuffix, message : uiLabelMap.FieldRequired, action : 'blur',
			        	rule : function(input,commit){
			        		var isParticipateIns = $("#isParticipateIns").jqxCheckBox('checked');
			        		if(isParticipateIns){
			        			var insuranceHealth = $("#insuranceHealth" + globalVar.defaultSuffix).val();
			        			if(insuranceHealth.length > 0){
			        				var value = $('#monthFromHealthIns' + globalVar.defaultSuffix).val();
			        				if(value < 0){
			        					return false;
			        				}
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : "#yearThruHealthIns" + globalVar.defaultSuffix, message : uiLabelMap.FieldRequired, action : 'blur',
			        	rule : function(input,commit){
			        		var isParticipateIns = $("#isParticipateIns").jqxCheckBox('checked');
			        		if(isParticipateIns){
			        			var insuranceHealth = $("#insuranceHealth" + globalVar.defaultSuffix).val();
			        			if(insuranceHealth.length > 0){
			        				var value = $('#monthThruHealthIns' + globalVar.defaultSuffix).val();
			        				if(value < 0){
			        					return false;
			        				}
			        			}
			        		}
			        		return true;
			        	}
			        },
                    {
                        input : "#insuranceSocialNbr" + globalVar.defaultSuffix, message : uiLabelMap.HRCharacterIsNotValid, action : 'blur',
                        rule : function(input,commit){
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(),uiLabelMap.HRCheckInsurance);
                        }
                    },
                    {
                        input : "#insuranceHealth" + globalVar.defaultSuffix, message : uiLabelMap.HRCharacterIsNotValid, action : 'blur',
                        rule : function(input,commit){
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(),uiLabelMap.HRCheckInsurance);
                        }
                    },
			]
		});
	};
	
	var initEvent = function(){
		/*$("#updateSalBasePosition").click(function(event){
			var positionTypeId = $("#emplPositionTypeId" + globalVar.defaultSuffix).val();
			if(!positionTypeId){
				bootbox.dialog(uiLabelMap.NotEmplPositionTypeChoose,
					[
						{
						    "label" : uiLabelMap.CommonClose,
						    "class" : "btn-danger btn-small icon-remove open-sans",
						    "callback": function() {
						    }
						}, 
					]
				);
				return;
			}
			$("#salaryBaseFlat" + globalVar.defaultSuffix).jqxNumberInput({disabled: true});
			var dateJoinCompany = $("#dateJoinCompany" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date');
			$("#salaryBaseFlat" + globalVar.defaultSuffix).val(0);
			$.ajax({
				url: 'getEmplPositionSalary',
				data: {emplPositionTypeId: positionTypeId, date: dateJoinCompany.getTime()},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						$("#salaryBaseFlat" + globalVar.defaultSuffix).val(response.rateAmount);
					}else{
						bootbox.dialog(response.errorMessage,
							[
								{
								    "label" : uiLabelMap.CommonClose,
								    "class" : "btn-danger btn-small icon-remove open-sans",
								    "callback": function() {
								    }
								}, 
							]
						);
					}
				},
				complete: function(jqXHR, status){
					$("#salaryBaseFlat" + globalVar.defaultSuffix).jqxNumberInput({disabled: false});
				}
			});
		});*/
		
		$("#searchHealthCareProvider" + globalVar.defaultSuffix).click(function(event){
			openJqxWindow($("#hospitalListWindow"));
		});
		
		$("#monthFromHealthIns" + globalVar.defaultSuffix).on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearFromHealthIns" + globalVar.defaultSuffix).jqxNumberInput({disabled: true});
				}else{
					$("#yearFromHealthIns" + globalVar.defaultSuffix).jqxNumberInput({disabled: false});
				}
			}
		});
		$("#monthThruHealthIns" + globalVar.defaultSuffix).on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearThruHealthIns" + globalVar.defaultSuffix).jqxNumberInput({disabled: true});
				}else{
					$("#yearThruHealthIns" + globalVar.defaultSuffix).jqxNumberInput({disabled: false});
				}
			}
		});
		$("#isParticipateIns").on('change', function(event){
			var checked = event.args.checked
			if(checked){
				$("#insuranceInfo" + globalVar.defaultSuffix).removeClass("disabledArea");
			}else{
				$("#insuranceInfo" + globalVar.defaultSuffix).addClass("disabledArea");
			}
		});
		
		$("#addNewEmployeeWindow").on('open', function(event){
			$("#password" + globalVar.defaultSuffix).jqxPasswordInput('val', '');
			$("#confirmPassword" + globalVar.defaultSuffix).jqxPasswordInput('val', '');
		});
		$("#addEmplPositionTypeBtn" + globalVar.defaultSuffix).click(function(){
			settingPositionTypeForOrgObj.openWindow();//settingPositionTypeForOrgObj is defined in AddNewEmployeeSetPosTypeForOrg.js
		});
	};
	
	var validate = function(){
		return $("#employmentInfo").jqxValidator('validate');
	};
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		resetData: resetData,
		getEmplPositionTypeInOrg: getEmplPositionTypeInOrg
	}
}());

var userLoginInfo = (function(){
		var init = function(){
			initJqxInput();
			initJqxValidator();
			$("#useDefaultPwd"+ globalVar.defaultSuffix).change(function(event){
				var checked = event.args.checked;
				if(checked){
					$("#password" + globalVar.defaultSuffix).jqxPasswordInput({disabled: true});
					$("#confirmPassword" + globalVar.defaultSuffix).jqxPasswordInput({disabled: true});
					if(OlbCore.isNotEmpty(defaultPwd)){
						$("#password" + globalVar.defaultSuffix).jqxPasswordInput("val", defaultPwd);
						$("#confirmPassword" + globalVar.defaultSuffix).jqxPasswordInput("val",defaultPwd);
					}
				}else{
					$("#password" + globalVar.defaultSuffix).jqxPasswordInput({disabled: false});
					$("#confirmPassword" + globalVar.defaultSuffix).jqxPasswordInput({disabled: false});
					$("#password" + globalVar.defaultSuffix).jqxPasswordInput("val", "");
					$("#confirmPassword" + globalVar.defaultSuffix).jqxPasswordInput("val", "");
				}
			});
		};
		
		var initJqxInput = function(){
			$("#userLoginId" + globalVar.defaultSuffix).jqxInput({width: '95%', height: 20});
			$("#password" + globalVar.defaultSuffix).jqxPasswordInput({width: '95%', height: '20px', showStrength: false, showPasswordIcon: false});
			$("#confirmPassword" + globalVar.defaultSuffix).jqxPasswordInput({width: '95%', height: '20px', showStrength: false, showPasswordIcon: false});
			$("#useDefaultPwd" + globalVar.defaultSuffix).jqxCheckBox({width: 13, height: 13, checked: false});
		};
		
		var getData = function(){
			var data = {
					userLoginId: $("#userLoginId" + globalVar.defaultSuffix).val(), 
					password: $("#password" + globalVar.defaultSuffix).val()
			};
			return data;
		};
		
		var resetData = function(){
			$("#userLoginId" + globalVar.defaultSuffix).val("");
			$("#password" + globalVar.defaultSuffix).jqxPasswordInput("val", "");
			$("#useDefaultPwd" + globalVar.defaultSuffix).jqxCheckBox('val', false);
		};
		
		var initJqxValidator = function(){
			$("#userLoginInfo").jqxValidator({
				scroll: false,
				rules: [
				        {
				        	input: '#userLoginId' + globalVar.defaultSuffix,
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
				        	input : '#userLoginId' + globalVar.defaultSuffix,
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
				        	input: '#password' + globalVar.defaultSuffix,
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
				        	input : "#password" + globalVar.defaultSuffix,
				        	message : uiLabelMap.PassLengthOverFive,
				        	action: 'blur',
				        	rule : function(input, commit){
				        		if($(input).val().length < 5){
				        			return false;
				        		}
				        		return true;
				        	}
				        },
				        {
				        	input : "#password" + globalVar.defaultSuffix,
				        	message : uiLabelMap.PasswordInvalid,
				        	action : 'blur',
				        	rule : function(input, commit){
				        		var space = " ";
				        		var value = $(input).val();
				        		if(value.indexOf(space) > -1){
				        			return false;
				        		}
				        		return true;
				        	}
				        },
				        {
				        	input: '#confirmPassword' + globalVar.defaultSuffix,
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
				        	input: '#confirmPassword' + globalVar.defaultSuffix,
				        	message: uiLabelMap.password_did_not_match_verify_password,
				        	action: 'blur',
				        	rule: function (input, commit){
				        		if (input.val() === $('#password' + globalVar.defaultSuffix).val()) {
				        			return true;
				        		}
				        		return false;
				        	}
				        },
				        ]
			});
		};
		
		var validate = function(){
			return $("#userLoginInfo").jqxValidator('validate');
		};
		
		var disabled = function(){
			$("#userLoginId" + globalVar.defaultSuffix).jqxInput({disabled: true});
			$("#password" + globalVar.defaultSuffix).jqxPasswordInput({disabled: true});
		};	
		var enabled = function(){
			$("#userLoginId" + globalVar.defaultSuffix).jqxInput({disabled: false});
			$("#password" + globalVar.defaultSuffix).jqxPasswordInput({disabled: false});
		};	
		
		var hideValidate = function(){
			$("#userLoginInfo").jqxValidator('hide');
		};
		return {
			init: init,
			validate: validate,
			getData: getData,
			disabled: disabled,
			enabled: enabled,
			hideValidate: hideValidate,
			resetData: resetData
		}
}());

var hosipitalObj = (function(){
		var init = function(){
			$("#hospitalListWindow").on('chooseDataHospital', function (){
			    var data = hospitalListObject.getSelectedHospitalData();
			    if(data){
			    	$("#healthCareProvider" + globalVar.defaultSuffix).jqxInput('val', {label: data.hospitalName, value: data.hospitalId});
			    	$("#healthCareProvider" + globalVar.defaultSuffix).attr("data-value", data.hospitalId);
			    }
			});
		};
		return {
			init: init
		}
}());

$(document).ready(function () {
	initJqxNotification();
	addNewEmplGeneralInfo.init();//addNewEmplGeneralInfo is defined in AddNewEmployeeProfileInfo.js
	permanentResInfo.init();//permanentResInfo is defined in AddNewEmployeeContactInfo.js
	currResInfo.init();//currResInfo is defined in AddNewEmployeeContactInfo.js
	emailContactObj.init();//emailContactObj is defined in AddNewEmployeeContactInfo.js
	phoneNumberContactObj.init();//phoneNumberContactObj is defined in AddNewEmployeeContactInfo.js
	emplWorkingInfo.init();
	userLoginInfo.init();
	hosipitalObj.init();
	initWizard.init();
	//createJqxWindow($("#hospitalListWindow"), 660, 550);
	create_spinner($("#spinner-ajax"));	
});

function resetData(){
	addNewEmplGeneralInfo.resetData();
	permanentResInfo.resetData();
	currResInfo.resetData();
	emplWorkingInfo.resetData();
	userLoginInfo.resetData();
	initWizard.resetStep();
}
function hideValidate(){
	addNewEmplGeneralInfo.hideValidate();
	permanentResInfo.hideValidate();
	emplWorkingInfo.hideValidate();
	userLoginInfo.hideValidate();
}