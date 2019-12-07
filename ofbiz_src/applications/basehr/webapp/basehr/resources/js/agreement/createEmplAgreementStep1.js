var createEmplAgrObjectstep1 = (function(){
	var emplPositionPartyArr = [];
	var _partyIdTo = null;
	var init = function(){
		initJqxInput();
		initJqxDateTimeInput();
		initJqxDropDownList();
		initJqxNumberInput();
		initBtnEvent();
		initJqxValidator();
		initDropDownListEvent();
		initJqxDateTimeInputEvent();
	};
	
	var initJqxDateTimeInputEvent = function(){
		$("#fromDate" + globalVar.suffix).on('valueChanged', function(event){
			var periodTypeSelected = $("#agreementDuration" + globalVar.suffix).jqxDropDownList('getSelectedItem');
			if(periodTypeSelected){
				var originalItem = periodTypeSelected.originalItem;
				var fromDate = event.args.date;
				setThruDateValue(fromDate, originalItem.periodLength, originalItem.uomId);
			}
		});
	};
	var setThruDateValue = function(fromDate, periodLength, uomId){
		if(fromDate && periodLength && uomId){
			var year = fromDate.getFullYear();
			var month = fromDate.getMonth();
			var day = fromDate.getDate();
			if(uomId == "TF_yr"){
				year += periodLength;
			}else if(uomId == "TF_mon"){
				month += periodLength;
			}else if(uomId == "TF_wk"){
				day += periodLength * 7;
			}else if(uomId == "TF_day"){
				day += periodLength;
			}
			var date = new Date(year, month, day - 1);
			$("#thruDate" + globalVar.suffix).val(date);
		}
	};
	var initDropDownListEvent = function(){
		$("#agreementDuration" + globalVar.suffix).on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var originalItem = item.originalItem;
				var periodLength = originalItem.periodLength;
				var uomId = originalItem.uomId;
				var fromDate = $("#fromDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
				setThruDateValue(fromDate, periodLength, uomId);
			}
		});
		/*$("#emplPosition" + globalVar.suffix).on('select', function(event){
			var index = args.index;
			var partyGroupName = getEmplPositionPartyValue(index, "groupName");
			$("#organization" + globalVar.suffix).val(partyGroupName);
			var fulltimeFlag = getEmplPositionPartyValue(index, "fulltimeFlag");
			if(fulltimeFlag){
				if(fulltimeFlag == "Y"){
					$("#workTypeWorkWeek" + globalVar.suffix).jqxDropDownList('selectItem', "FULLTIME");
				}else{
					$("#workTypeWorkWeek" + globalVar.suffix).jqxDropDownList('selectItem', "PARTTIME")
				}
			}
		});*/
		$("#agreementTypeId" + globalVar.suffix).on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if("UNLIMITED_TIME_AGR" == value){
					$("#agreementDuration" + globalVar.suffix).jqxDropDownList({disabled: true});
					$("#thruDate" + globalVar.suffix).jqxDateTimeInput({disabled: true});
				}else{
					$("#agreementDuration" + globalVar.suffix).jqxDropDownList({disabled: false});
					$("#thruDate" + globalVar.suffix).jqxDateTimeInput({disabled: false});
				}
			}
		});
	};
	
	var initBtnEvent = function(){
		$("#getDateJoinCompany").click(function(event){
			getDateJoinCompanyAction();
		});
		$("#searchEmpl").click(function(event){
			openJqxWindow($("#popupWindowEmplList"));
			initWizard.setFunctionAfterChooseEmpl(functionAfterChooseEmpl);
		});
		$("#newAgrDur").click(function(event){
			openJqxWindow($("#newAgrDurationWindow"));
		});
	};
	
	var initJqxInput = function(){
		
		$("#agreementCode" + globalVar.suffix).jqxInput({width: '96%', height: 20});
		$("#agreementDesc" + globalVar.suffix).jqxInput({width: '96%', height: 20});
		$("#organization" + globalVar.suffix).jqxInput({width: '96%', height: 20});
		$("#employeeName" + globalVar.suffix).jqxInput({width: '86%', height: 20, disabled: true});
		$("#partyIdTo" + globalVar.suffix).jqxInput({width: '96%', height: 20, disabled: true});
		$("#partyIdFrom" + globalVar.suffix).jqxInput({
		     placeHolder: "",
		     displayMember: "groupName",
		     valueMember: "partyId",
		     width: '96%',
		     height: 20,
		     disabled: true
		 });
	};
	
	var initJqxDateTimeInput = function(){
		$("#fromDate" + globalVar.suffix).jqxDateTimeInput({ width: '100%', height: '25px', value : null});
		$("#thruDate" + globalVar.suffix).jqxDateTimeInput({ width: '98%', height: '25px'});
		$("#agreementDate" + globalVar.suffix).jqxDateTimeInput({ width: '98%', height: '25px', value : null});
		//restrictFomDateThruDate($("#fromDate" + globalVar.suffix), $("#thruDate" + globalVar.suffix));
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.agreementTypeArr, $("#agreementTypeId" + globalVar.suffix), "agreementTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#emplPosition" + globalVar.suffix), "emplPositionTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.workTypeArr, $("#workTypeWorkWeek" + globalVar.suffix), "workTypeId", "description", 25, '98%');
		createJqxDropDownListBinding($("#agreementDuration" + globalVar.suffix), [{name: 'periodTypeId'}, {name: 'description'}, {name: "uomId"}, {name: "periodLength"}], 
				'getAgrDurationList', "listReturn", "periodTypeId", "description", '100%', 25);
	};
	
	var initJqxNumberInput = function(){
		$("#basicSalary" + globalVar.suffix).jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, decimalDigits: 0, digits: 12, max: 999999999999});
		$("#insuranceSalary" + globalVar.suffix).jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, decimalDigits: 0, digits: 12, max: 999999999999});
		$("#payRate" + globalVar.suffix).jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, decimalDigits: 0, digits: 3, max: 100, inputMode: 'simple', symbol: '%', symbolPosition:'right'});
		$("#payRate" + globalVar.suffix).val(100);
	};
	
	var getData = function(){
		var retData = {};
		retData.partyIdTo = _partyIdTo;
		retData.partyIdFrom = $("#partyIdFrom" + globalVar.suffix).val().value;
		retData.agreementCode = $("#agreementCode" + globalVar.suffix).val();
		retData.description = $("#agreementDesc" + globalVar.suffix).val();
		retData.agreementDate = $("#agreementDate" + globalVar.suffix).jqxDateTimeInput('val', 'date').getTime();
		retData.fromDate = $("#fromDate" + globalVar.suffix).jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#thruDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
		if(thruDate){
			retData.thruDate = thruDate.getTime();
		}
		retData.agreementTypeId = $("#agreementTypeId" + globalVar.suffix).val();
		retData.agreementDuration = $("#agreementDuration" + globalVar.suffix).val();
		retData.basicSalary = $("#basicSalary" + globalVar.suffix).val();
		retData.insuranceSalary = $("#insuranceSalary" + globalVar.suffix).val();
		retData.payRate = $("#payRate" + globalVar.suffix).val();
		retData.emplPositionTypeId = $("#emplPosition" + globalVar.suffix).val();
		retData.workPlace = $("#organization" + globalVar.suffix).val();
		return retData;
	};
	
	var getAgrDurData = function(quantity,unit){
		var data = {};
		if(unit == "TF_yr"){
			data.agrDurId = quantity + "_" + "YEAR";
			data.description = quantity + " " + uiLabelMap.HolidayYear;
			data.lengthDate = quantity * 12 * 30;
		}
		if(unit == "TF_mon"){
			data.agrDurId = quantity + "_" + "MONTH";
			data.description = quantity + " " + uiLabelMap.HRCommonMonth;
			data.lengthDate = quantity * 30;
		}
		return data;
	};
	
	var fillData = function(data){
		_partyIdTo = data.partyIdTo;
		$("#partyIdTo" + globalVar.suffix).val(data.partyCode);
		$("#employeeName" + globalVar.suffix).val(data.fullName);
		$("#agreementCode" + globalVar.suffix).val(data.agreementCode);
		$("#agreementDesc" + globalVar.suffix).val(data.description);
		$("#partyIdFrom" + globalVar.suffix).jqxInput('val', {label: data.groupName, value: data.partyIdFrom});
		$("#agreementDate" + globalVar.suffix).val(data.agreementDate);
		$("#agreementTypeId" + globalVar.suffix).val(data.agreementTypeId);
		$("#agreementDuration" + globalVar.suffix).val(data.agreementPeriod);
		$("#basicSalary" + globalVar.suffix).val(data.basicSalary);
		$("#insuranceSalary" + globalVar.suffix).val(data.insuranceSalary);
		$("#payRate" + globalVar.suffix).val(data.payRate);
		$("#emplPosition" + globalVar.suffix).val(data.emplPositionTypeId);
		$("#organization" + globalVar.suffix).val(data.workPlace);
        $("#fromDate" + globalVar.suffix).val(data.fromDate);
        if(data.thruDate){
            $("#thruDate" + globalVar.suffix).val(data.thruDate);
        }else{
            $("#thruDate" + globalVar.suffix).val(null);
        }

		/*var dataSubmit = {partyId: data.partyIdTo};
		dataSubmit.fromDate = data.fromDate.getTime();
		if(data.thruDate){
			dataSubmit.thruDate = data.thruDate.getTime();
		}
		var callback = function(listSource){
			$("#emplPosition" + globalVar.suffix).jqxDropDownList('clearSelection');
			if(listSource.length > 0){
				$("#emplPosition" + globalVar.suffix).val(listSource[0].emplPositionTypeId);
				$("#organization" + globalVar.suffix).val(listSource[0].groupName);
			}
		}
		actionObject.getPositionOfEmplDropDownList(dataSubmit, callback);*/
		
	};
	
	var initJqxValidator = function(){
		$("#newAgrDurationWindow").jqxValidator({
			rules : [
	         ]
		});
		$("#createAgreementStep1").jqxValidator({
			rules: [
				{
					input: "#agreementCode" + globalVar.suffix,
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: 'required'
				},
				{
					input: "#partyIdTo" + globalVar.suffix,
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: 'required'
				},
				{
					input: "#agreementDate" + globalVar.suffix,
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{
					input: "#getDateJoinCompany",
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: function(input, commit){
						if(!$("#fromDate" + globalVar.suffix).val()){
							return false;
						}
						return true;
					}
				},
				{
					input: "#getDateJoinCompany",
					message: uiLabelMap.ValueIsInvalid,
					action: 'blur',
					rule: function(input, commit){
						var thruDate = $("#thruDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
						var agreementTypeId = $("#agreementTypeId" + globalVar.suffix).val();
						if(!agreementTypeId){
							return true;
						}
						var fromDate = $("#fromDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
						if(thruDate && fromDate){
							if("UNLIMITED_TIME_AGR" != agreementTypeId && thruDate.getTime() <= fromDate.getTime()){
								return false;
							}
						}
						return true;
					}
				},
				{
					input: "#agreementTypeId" + globalVar.suffix,
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{
					input: "#thruDate" + globalVar.suffix,
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: function(input, commit){
						var agreementTypeId = $("#agreementTypeId" + globalVar.suffix).val();
						if(!agreementTypeId){
							return true;
						}
						if("UNLIMITED_TIME_AGR" != agreementTypeId){
							if(!input.val()){
								return false;
							}
						}
						return true;
					}
				},
				{
					input: "#thruDate" + globalVar.suffix,
					message: uiLabelMap.HRRequiredValueGreatherToDay,
					action: 'blur',
					position : 'bottom',
					rule: function(input, commit){
						var val = input.jqxDateTimeInput('val', 'date');
						var agreementTypeId = $("#agreementTypeId" + globalVar.suffix).val();
						if(val && "UNLIMITED_TIME_AGR" != agreementTypeId){
							var nowDate = new Date(globalVar.nowTimestamp);
							if(val.getTime() < nowDate.getTime()){
								return false;
							}
							var fromDate = $("#fromDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
							if(val.getTime() <= fromDate.getTime()){
								return false;
							}
						}
						return true;
					}
				},
				{
					input: "#thruDate" + globalVar.suffix,
					message: uiLabelMap.ValueMustGreaterAgreementDate,
					action: 'blur',
					rule: function(input, commit){
						var val = input.jqxDateTimeInput('val', 'date');
						var agreementTypeId = $("#agreementTypeId" + globalVar.suffix).val();
						if(val && "UNLIMITED_TIME_AGR" != agreementTypeId){
							var agreementDate = $("#agreementDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
							if(agreementDate && val.getTime() <= agreementDate.getTime()){
								return false;
							}
						}
						return true;
					}
				},
				{
					input : '#basicSalary' + globalVar.suffix,
					message : uiLabelMap.OnlyInputNumberGreaterThanZero,
					action : 'blur',
					rule : function(input,commit){
						if(input.val()< 0){
							return false;
						}
						return true;
					}
				},
				{
					input : '#insuranceSalary' + globalVar.suffix,
					message : uiLabelMap.OnlyInputNumberGreaterThanZero,
					action : 'blur',
					rule : function(input,commit){
						if(input.val()< 0){
							return false;
						}
						return true;
					}
				},
				{
					input : '#payRate' + globalVar.suffix,
					message : uiLabelMap.OnlyInputNumberGreaterThanZero,
					action : 'blur',
					rule : function(input,commit){
						if(input.val()< 0){
							return false;
						}
						return true;
					}
				},
				{
					input : "#getDateJoinCompany",
					message : uiLabelMap.EffectiveDayEqualOrGreaterThanContractingDay,
					action : 'blur',
					rule : function(input, commit){
						if($("#agreementDate" + globalVar.suffix).jqxDateTimeInput('getDate')){
							if($("#fromDate" + globalVar.suffix).jqxDateTimeInput('getDate') < $("#agreementDate" + globalVar.suffix).jqxDateTimeInput('getDate')){
								return false;
							}
						}
						return true;
					}
				},
				{
					input : "#agreementDate" + globalVar.suffix,
					position : 'bottom',
					message : uiLabelMap.FieldRequired,
					action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				}
			]
		});
	};
	
	var validate = function(){
		return $("#createAgreementStep1").jqxValidator('validate');
	};
	
	var getDateJoinCompanyAction = function(){
		var partyId = _partyIdTo;
		if(!partyId){
			bootbox.dialog(uiLabelMap.NoPartyChoose,
				[{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
			return;
		}
		$("#fromDate" + globalVar.suffix).jqxDateTimeInput({disabled: true});
		$.ajax({
			url: 'getDateJoinCompany',
			data: {partyId: partyId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					$("#fromDate" + globalVar.suffix).val(new Date(response.dateJoinCompany));
				}else{
					bootbox.dialog(response.errorMessage,
							[{
				    		    "label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",
				    		}]		
						);
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				$("#fromDate" + globalVar.suffix).jqxDateTimeInput({disabled: false});
			}
		});
	};
	
	var getPositionOfParty = function(partyId){
		var fromDate = $("#fromDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
		var thruDate = $("#thruDate" + globalVar.suffix).jqxDateTimeInput('val', 'date');
		var dataSubmit = {partyId: partyId};
		if(fromDate){
			dataSubmit.fromDate = fromDate.getTime();
		}
		if(thruDate){
			dataSubmit.thruDate = thruDate.getTime();
		}
		var callback = function(listSource){
			$("#emplPosition" + globalVar.suffix).jqxDropDownList('clearSelection');
			if(listSource.length > 0){
				$("#emplPosition" + globalVar.suffix).val(listSource[0].emplPositionTypeId);
				$("#organization" + globalVar.suffix).val(listSource[0].groupName)
			}
		}
		actionObject.getPositionOfEmplDropDownList(dataSubmit, callback);
	};
	
	/*var setEmplPositionPartyArr = function(newArray){
		emplPositionPartyArr = newArray;
	};*/
	
	var getEmplPositionPartyValue = function(index, key){
		if(emplPositionPartyArr[index]){
			return emplPositionPartyArr[index][key];
		}
	};
	
	var functionAfterChooseEmpl = function(data){
		$("#employeeName" + globalVar.suffix).val(data.fullName);
	    $("#partyIdTo" + globalVar.suffix).val(data.partyCode);
	    _partyIdTo = data.partyId;
	    getPositionOfParty(data.partyId);
	};
	
	var resetData = function(){
		Grid.clearForm($("#createAgreementStep1"));
		$("#emplPosition" + globalVar.suffix).jqxDropDownList('clearSelection');
		_partyIdTo = null;
		//updateSourceDropdownlist($("#emplPosition" + globalVar.suffix), []);
	};
	
	return{
		init: init,
		//setEmplPositionPartyArr: setEmplPositionPartyArr,
		validate: validate,
		getData: getData,
		resetData: resetData,
		fillData: fillData
	}
}());