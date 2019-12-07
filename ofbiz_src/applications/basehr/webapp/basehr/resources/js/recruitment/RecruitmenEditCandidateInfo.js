var editCandidateInfoObj = (function(){
	var _functionAfterUpdateCandidate = null;
	var _partyId = null;
	var _data = null;
	var init = function(){
		initJqxDropDownList();
		initJqxInput();
		initJqxDateTimeInput();
		initJqxNumberInput();
		initJqxWindow();
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxValidator();
			initEvent();
			create_spinner($("#spinnerAjaxEditCandidateInfo"));
		};
		createJqxWindow($("#generalInfoCandidateEditWindow"), 750, 470, initContent);
		$("#generalInfoCandidateEditWindow").on('close', function(event){
			Grid.clearForm($("#generalInfoCandidateEditWindow"));
			$("#ethnicOriginDropdownCandidateEdit").jqxDropDownList('clearSelection');
			$("#maritalStatusDropdownCandidateEdit").jqxDropDownList('clearSelection');
			$("#religionDropdownCandidateEdit").jqxDropDownList('clearSelection');
			$("#nationalityDropdownCandidateEdit").jqxDropDownList('clearSelection');
			$("#majorCandidateEdit").jqxDropDownList('clearSelection');
			$("#nationalityDropdownCandidateEdit").jqxDropDownList('clearSelection');
			$("#classificationTypeCandidateEdit").jqxDropDownList('clearSelection');
			$("#educationSystemTypeCandidateEdit").jqxDropDownList('clearSelection');
			$("#genderCandidateEdit").jqxDropDownList('clearSelection');
			$("#idIssuePlaceDropDownCandidateEdit").jqxDropDownList('clearSelection');
			_data = {};
			_partyId = null;
			_functionAfterUpdateCandidate = null;
		});
	};
	
	var initJqxInput = function(){		
		$('#candidateIdEdit').jqxInput({width : '95%',height : '20px'});
		$('#lastNameCandidateEdit').jqxInput({width : '95%',height : '20px'});
		$('#middleNameCandidateEdit').jqxInput({width : '95%',height : '20px'});
		$('#firstNameCandidateEdit').jqxInput({width : '95%',height : '20px'});
		$('#nativeLandInputCandidateEdit').jqxInput({width : '95%',height : '20px'});
		$("#editIdNumberCandidateEdit").jqxInput({ width: '95%', height: 19});
	};
	
	var initJqxDateTimeInput = function(){
		$("#birthDateCandidateEdit").jqxDateTimeInput({width: '97%', height: '25px'});
		$("#idIssueDateTimeCandidateEdit").jqxDateTimeInput({width: '97%', height: '25px'});
		$("#idIssueDateTimeCandidateEdit").val(null);
		$("#birthDateCandidateEdit").val(null);
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.genderArr, $("#genderCandidateEdit"), 'genderId', 'description', 25, '97%');
		createJqxDropDownList([], $("#idIssuePlaceDropDownCandidateEdit"), "geoId", "geoName", 25, "97%");
		$("#idIssuePlaceDropDownCandidateEdit").jqxDropDownList({dropDownHeight: 180});
		createJqxDropDownList([], $("#ethnicOriginDropdownCandidateEdit"), "ethnicOriginId", "description", 25, "97%");
		createJqxDropDownList([], $("#maritalStatusDropdownCandidateEdit"), "ethnicOriginId", "description", 25, "97%");
		createJqxDropDownList([], $("#religionDropdownCandidateEdit"), "religionId", "description", 25, "97%");
		createJqxDropDownList([], $("#nationalityDropdownCandidateEdit"), "nationalityId", "description", 25, "97%");
		createJqxDropDownList([], $("#majorCandidateEdit"), "majorId", "description", 25, "97%");
		createJqxDropDownList([], $("#classificationTypeCandidateEdit"), "classificationTypeId", "description", 25, "97%");
		createJqxDropDownList([], $("#educationSystemTypeCandidateEdit"), "educationSystemTypeId", "description", 25, "97%");
	};
	
	var initJqxNumberInput = function(){
		$("#graduationYearCandidateEdit").jqxNumberInput({ width: '97%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 0});
	};
	
	var initJqxValidator = function(){
		$("#generalInfoCandidateEditWindow").jqxValidator({
			rules: [
			        {
			        	input: '#candidateIdEdit',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input: '#candidateIdEdit',
			        	message: uiLabelMap.OnlyContainInvalidChar,
			        	action: 'blur',
			        	rule: function (input, commit){
			    		   var value = input.val();
			    		   if(value){
			    			   if(/^[a-zA-Z0-9-_]*$/.test(value) == false) {
			    				    return false;
			    				}
			    		   }
			    		   return true;
			    	    }
			        },
			        {
			        	input: '#firstNameCandidateEdit',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input: '#lastNameCandidateEdit',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input : '#lastNameCandidateEdit',
			        	message : uiLabelMap.IllegalCharactersAndSpace,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		var value = $(input).val();
			        		var space = " ";
			        		if(isContainSpecialCharAndNumb(value) || value.trim().indexOf(space) > -1){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#firstNameCandidateEdit',
			        	message : uiLabelMap.IllegalCharactersAndSpace,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		var value = $(input).val();
			        		var space = " ";
			        		if(isContainSpecialCharAndNumb(value) || value.trim().indexOf(space) > -1){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#middleNameCandidateEdit',
			        	message : uiLabelMap.IllegalCharacters,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		var value = $(input).val();
			        		var specialCharacter = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-= 1234567890";
			        		for(var i=0 ; i < specialCharacter.length ; i++){
			        			if(value.trim().indexOf(specialCharacter[i]) > -1){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#editIdNumberCandidateEdit',
			        	message : uiLabelMap.OnlyInputNumberGreaterThanZero,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		if($(input).val() < 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#idIssueDateTimeCandidateEdit',
			        	message : uiLabelMap.IdentifyDayGreaterBirthDate,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		if($('#birthDateCandidateEdit').jqxDateTimeInput('val', 'date')){
			        			if($('#birthDateCandidateEdit').jqxDateTimeInput('val', 'date') > $(input).jqxDateTimeInput('val','date')){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#birthDateCandidateEdit',
			        	message : uiLabelMap.BirthDateBefIdentifyCardDay,
			        	action :'blur',
			        	rule : function(input, commit){
			        		if($('#idIssueDateTimeCandidateEdit').jqxDateTimeInput('val', 'date')){
			        			if($('#idIssueDateTimeCandidateEdit').jqxDateTimeInput('val', 'date') < $(input).jqxDateTimeInput('val', 'date')){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#birthDateCandidateEdit',
			        	message : uiLabelMap.BirthDateBeforeToDay,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		var now = new Date();
			        		if($(input).jqxDateTimeInput('val', 'date') >= now){
			        			return false;
			        		}
			        		return true;
			        	}
			        }
			]
		});
	};
	
	var initEvent = function(){
		$("#cancelEditCandidateInfo").click(function(event){
			$("#generalInfoCandidateEditWindow").jqxWindow('close');
		});
		if(globalVar.hasPermissionAdmin){
			$("#saveEditCandidateInfo").click(function(event){
				if(!validate()){
					return false
				}
				var dataSubmit = getData();
				$("#ajaxLoadingEditCandidateInfo").show();
				$("#saveEditCandidateInfo").attr("disabled", "disabled");
				$("#cancelEditCandidateInfo").attr("disabled", "disabled");
				$.ajax({
					url: 'updateCandidateInfo',
					data: dataSubmit,
					type: 'POST',
					success: function(response){
						if(response.responseMessage == 'success'){
							if(typeof(_functionAfterUpdateCandidate) == "function"){
								_functionAfterUpdateCandidate(response.successMessage);
							}
							$("#generalInfoCandidateEditWindow").jqxWindow('close');
							//recruitmentCandidateListObj.renderMessage(response.successMessage);//recruitmentCandidateListObj defined in RecruitmentCreateCandidate.js
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
						$("#ajaxLoadingEditCandidateInfo").hide();
						$("#saveEditCandidateInfo").removeAttr("disabled");
						$("#cancelEditCandidateInfo").removeAttr("disabled");
					}
				});
			});
		}
	};
	
	var validate = function(){
		return $("#generalInfoCandidateEditWindow").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#generalInfoCandidateEditWindow").jqxValidator('hide');
	};
	
	var resetData = function(){
		Grid.clearForm($("#generalInfoCandidateEdit"));
	};
	
	var getData = function(){
		var data = {
				partyId: _partyId,
				candidateId: $('#candidateIdEdit').val(),
				lastName: $('#lastNameCandidateEdit').val(),
				middleName: $('#middleNameCandidateEdit').val(),
				firstName: $('#firstNameCandidateEdit').val(),
				nativeLand: $('#nativeLandInputCandidateEdit').val(),
				idNumber: $("#editIdNumberCandidateEdit").val(),
				gender: $("#genderCandidateEdit").val(),
				idIssuePlace: $("#idIssuePlaceDropDownCandidateEdit").val(),
				ethnicOrigin: $("#ethnicOriginDropdownCandidateEdit").val(),
				religion: $("#religionDropdownCandidateEdit").val(),
				nationality: $("#nationalityDropdownCandidateEdit").val(),
				maritalStatusId: $("#maritalStatusDropdownCandidateEdit").val(),
				birthPlace: $("#birthPlaceCandidateEdit").val(),
				graduationYear: $("#graduationYearCandidateEdit").val(),
				majorId: $("#majorCandidateEdit").val(),
				classificationTypeId: $("#classificationTypeCandidateEdit").val(),
				educationSystemTypeId: $("#educationSystemTypeCandidateEdit").val()
		};
		if($("#birthDateCandidateEdit").jqxDateTimeInput('val', 'date')){
			data.birthDate = $("#birthDateCandidateEdit").jqxDateTimeInput('val', 'date').getTime();
		}
		if($("#idIssueDateTimeCandidateEdit").jqxDateTimeInput('val', 'date')){
			data.idIssueDate = $("#idIssueDateTimeCandidateEdit").jqxDateTimeInput('val', 'date').getTime();
		}
		return data;
	};
	
	var setData = function(data){
		_partyId = data.partyId;
		var candidateInfo = {};
		$.ajax({
			url: 'getCandidateGeneralInfo',
			data: {partyId: _partyId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					candidateInfo = response.personInfo;
				}
			},
			complete: function(jqXHR, textStatus){
				_data = $.extend({}, data, candidateInfo);
				fillData(_data);
			}
		});
	};
	
	var fillData = function(data){
		$("#candidateIdEdit").val(data.recruitCandidateId);
		$("#lastNameCandidateEdit").val(data.lastName);
		$("#middleNameCandidateEdit").val(data.middleName);
		$("#firstNameCandidateEdit").val(data.firstName);
		$("#editIdNumberCandidateEdit").val(data.idNumber);
		$("#editIdNumberCandidateEdit").val(data.idNumber);
		$("#genderCandidateEdit").val(data.gender);
		$("#nativeLandInputCandidateEdit").val(data.nativeLand);
		$("#graduationYearCandidateEdit").val(data.graduationYear);
		if(data.idIssueDate){
			$("#idIssueDateTimeCandidateEdit").val(new Date(data.idIssueDate));
		}
		if(data.birthDate){
			$("#birthDateCandidateEdit").val(new Date(data.birthDate));
		}
		//wizardEditCandidate is defined in RecruitmentCreateCandidate.js
		var condition = [{fieldName: "geoIdFrom", fieldValue: globalVar.defaultCountry}];
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "geoStateProvinceArr", $("#idIssuePlaceDropDownCandidateEdit"), 
				"getListStateProvinceGeo", JSON.stringify(condition), data.idIssuePlace);
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "ethnicOriginArr", $("#ethnicOriginDropdownCandidateEdit"), 
				"getEthnicOriginList", null, data.ethnicOrigin);
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "religionArr", $("#religionDropdownCandidateEdit"), "getReligionList", null, data.religion);
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "nationalityArr", $("#nationalityDropdownCandidateEdit"), 
				"getNationalityList", null, data.nationality);
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "majorArr", $("#majorCandidateEdit"), "getMajorList", null, data.majorId);
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "degreeClassTypeArr", $("#classificationTypeCandidateEdit"), 
				"getDegreeClassificationTypeList", null, data.classificationTypeId);
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "educationSystemTypeArr", $("#educationSystemTypeCandidateEdit"), 
				"getEducationSystemTypeList", null, data.educationSystemTypeId);
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "maritalStatusArr", $("#maritalStatusDropdownCandidateEdit"), 
				"getMaritalStatusList", null, data.maritalStatusId);
	};
	
	var openWindow = function(){
		openJqxWindow($("#generalInfoCandidateEditWindow"));
	};
	
	var setFunctionAfterUpdateCandidate = function(value){
		_functionAfterUpdateCandidate = value;
	}
	
	return{
		init: init,
		openWindow: openWindow,
		validate: validate,
		getData: getData,
		setData: setData,
		hideValidate: hideValidate,
		setFunctionAfterUpdateCandidate: setFunctionAfterUpdateCandidate,
		//getDataOnWindowOpen: getDataOnWindowOpen,
		resetData: resetData
	}
}());

var editPermanentResInfoCandidate = (function(){
	var _permanentResAddr = {};
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initJqxValidator();
		/*if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdPermResCandidateEdit").jqxDropDownList('selectItem', globalVar.defaultCountry);
		}*/
	};
	
	var initJqxInput = function(){
		$("#paddress1CandidateEdit").jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
			createJqxDropDownList([], $("#countryGeoIdPermResCandidateEdit"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#stateGeoIdPermResCandidateEdit"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#countyGeoIdPermResCandidateEdit"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#wardGeoIdPermResCandidateEdit"), "geoId", "geoName", 25, "97%");
	};
	
	var getData = function(){
			var data = {
					address1: $("#paddress1CandidateEdit").val(),
					countryGeoId: $("#countryGeoIdPermResCandidateEdit").val(),
					stateProvinceGeoId: $("#stateGeoIdPermResCandidateEdit").val(),
					districtGeoId: $("#countyGeoIdPermResCandidateEdit").val(),
					wardGeoId: $("#wardGeoIdPermResCandidateEdit").val()
			};
			return data;
	};
	
	var resetData = function(){
		$("#paddress1CandidateEdit").val("");
		$("#countryGeoIdPermResCandidateEdit").jqxDropDownList('clearSelection');
		$("#stateGeoIdPermResCandidateEdit").jqxDropDownList('clearSelection');
		$("#countyGeoIdPermResCandidateEdit").jqxDropDownList('clearSelection');
		$("#wardGeoIdPermResCandidateEdit").jqxDropDownList('clearSelection');
		_permanentResAddr = {};
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdPermResCandidateEdit').on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($("#stateGeoIdPermResCandidateEdit"), data, url, _permanentResAddr.stateProvinceGeoId);
			}
		});
		
		$("#stateGeoIdPermResCandidateEdit").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($("#countyGeoIdPermResCandidateEdit"), data, url, _permanentResAddr.districtGeoId);
			}
		});
		$("#countyGeoIdPermResCandidateEdit").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($("#wardGeoIdPermResCandidateEdit"), data, url, _permanentResAddr.wardGeoId);
			}
		});
	};
	
	var hideValidate = function(){
		$("#permanentResidenceCandidateEdit").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#permanentResidenceCandidateEdit").jqxValidator({
			rules: [
			        {
			        	input: '#countryGeoIdPermResCandidateEdit',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1CandidateEdit").val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        			return true;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#stateGeoIdPermResCandidateEdit',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1CandidateEdit").val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        			return true;
			        		}
			        		return true;
			        	}
			        }			        
			]
		});
	};
	
	var validate = function(){
			return $("#permanentResidenceCandidateEdit").jqxValidator('validate');
	};
	
	var setData = function(data){
		if(data && data.hasOwnProperty("contactMechId")){
			_permanentResAddr = data;
			candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "countryGeoIdArr", $("#countryGeoIdPermResCandidateEdit"), 
					"getCountryGeoList", null, _permanentResAddr.countryGeoId);
			$("#paddress1CandidateEdit").val(_permanentResAddr.address1);
		}else{
			candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "countryGeoIdArr", $("#countryGeoIdPermResCandidateEdit"), 
					"getCountryGeoList", null, globalVar.defaultCountry);
		}
	};
	
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		setData: setData,
		resetData: resetData
	}
}());

var editCurrResInfoCandidate = (function(){
	var _currResAddrData = {};
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initBtnEvent();
		initJqxValidator();
		/*if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdCurrResCandidateEdit").jqxDropDownList('selectItem', globalVar.defaultCountry);
		}*/
	};
	
	var initJqxInput = function(){
		$("#address1CurrResCandidateEdit").jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#countryGeoIdCurrResCandidateEdit"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#stateGeoIdCurrResCandidateEdit"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#countyGeoIdCurrResCandidateEdit"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#wardGeoIdCurrResCandidateEdit"), "geoId", "geoName", 25, "97%");
	};
	
	var getData = function(){
		var data = {
				address1: $("#address1CurrResCandidateEdit").val(),
				countryGeoId: $("#countryGeoIdCurrResCandidateEdit").val(),
				stateProvinceGeoId: $("#stateGeoIdCurrResCandidateEdit").val(),
				districtGeoId: $("#countyGeoIdCurrResCandidateEdit").val(),
				wardGeoId: $("#wardGeoIdCurrResCandidateEdit").val()
		};
		return data;
	};
	
	var resetData = function(){
		$("#address1CurrResCandidateEdit").val("");
		$("#countryGeoIdCurrResCandidateEdit").jqxDropDownList('clearSelection');
		$("#stateGeoIdCurrResCandidateEdit").jqxDropDownList('clearSelection');
		$("#countyGeoIdCurrResCandidateEdit").jqxDropDownList('clearSelection');
		$("#wardGeoIdCurrResCandidateEdit").jqxDropDownList('clearSelection');
		_currResAddrData = {};
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdCurrResCandidateEdit').on('select', function(event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($('#stateGeoIdCurrResCandidateEdit'), data, url, _currResAddrData.stateProvinceGeoId);
			}
		});
		
		$("#stateGeoIdCurrResCandidateEdit").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($('#countyGeoIdCurrResCandidateEdit'), data, url, _currResAddrData.districtGeoId);
			}
		});
		
		$("#countyGeoIdCurrResCandidateEdit").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($('#wardGeoIdCurrResCandidateEdit'), data, url, _currResAddrData.wardGeoId);
			}
		});
	};
	
	var hideValidate = function(){
		$("#currentResidenceCandidateEdit").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#currentResidenceCandidateEdit").jqxValidator({
			rules: [
			        {
			        	input: '#countryGeoIdCurrResCandidateEdit',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrResCandidateEdit").val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#stateGeoIdCurrResCandidateEdit',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrResCandidateEdit").val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        }			        
			]
		});
	};
	var validate = function(){
		return $("#currentResidenceCandidateEdit").jqxValidator('validate');
	};
	
	var setData = function(data){
		if(data && data.hasOwnProperty("contactMechId")){
			_currResAddrData = data;
			candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "countryGeoIdArr", $("#countryGeoIdCurrResCandidateEdit"), 
					"getCountryGeoList", null, _currResAddrData.countryGeoId);
			$("#address1CurrResCandidateEdit").val(_currResAddrData.address1);
		}else{
			candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "countryGeoIdArr", $("#countryGeoIdCurrResCandidateEdit"), 
					"getCountryGeoList", null, globalVar.defaultCountry);
		}
	};
	
	var initBtnEvent = function(){
		$("#copyPermResEdit").click(function(){
			_currResAddrData = editPermanentResInfoCandidate.getData();
			$("#address1CurrResCandidateEdit").val(_currResAddrData.address1);
			$('#countryGeoIdCurrResCandidateEdit').jqxDropDownList('clearSelection');						
			$('#countryGeoIdCurrResCandidateEdit').jqxDropDownList('selectItem', _currResAddrData.countryGeoId);
		});
	};
	
	return {
		init: init,
		validate: validate,
		getData: getData,
		setData: setData,
		hideValidate: hideValidate,
		resetData: resetData
	}
}());

var editCandidateContactMechs = (function(){
	var _partyId = null;
	var init = function(){
		editCurrResInfoCandidate.init();
		editPermanentResInfoCandidate.init();
		initEvent();
		initJqxWindow();
		create_spinner($("#spinnerAjaxEditCandidateContact"));
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#EditContactInfoCandidateWindow"), 750, 430);
		$("#EditContactInfoCandidateWindow").on('close', function(event){
			_partyId = null;
			editCurrResInfoCandidate.resetData();
			editPermanentResInfoCandidate.resetData();
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#EditContactInfoCandidateWindow"));
	};
	
	var setData = function(data){
		_partyId = data.partyId;
		$.ajax({
			url: 'getCandidateContactMechs',
			data: {partyId: _partyId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					editPermanentResInfoCandidate.setData(response.permanentResInfo);
					editCurrResInfoCandidate.setData(response.currResInfo);
				}
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	
	var initEvent = function(){
		$("#cancelEditCandidateContactMech").click(function(event){
			$("#EditContactInfoCandidateWindow").jqxWindow('close');
		});
		if(globalVar.hasPermissionAdmin){
			$("#saveEditCandidateContactMech").click(function(event){
				if(!editCurrResInfoCandidate.validate() || !editPermanentResInfoCandidate.validate()){
					return false;
				}
				$("#ajaxLoadingEditCandidateContact").show();
				$("#saveEditCandidateContactMech").attr("disabled", 'disabled');
				$("#cancelEditCandidateContactMech").attr("disabled", 'disabled');
				var dataSubmit = {};
				dataSubmit.permanentRes = JSON.stringify(editPermanentResInfoCandidate.getData());
				dataSubmit.currRes = JSON.stringify(editCurrResInfoCandidate.getData());
				dataSubmit.partyId = _partyId;
				$.ajax({
					url: 'updateCandidateContactMechs',
					data: dataSubmit,
					type: 'POST',
					success: function(response){
						if(response.responseMessage == 'success'){
							$("#EditContactInfoCandidateWindow").jqxWindow('close');
							recruitmentCandidateListObj.renderMessage(response.successMessage);//recruitmentCandidateListObj defined in RecruitmentCreateCandidate.js
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
						$("#ajaxLoadingEditCandidateContact").hide();
						$("#saveEditCandidateContactMech").removeAttr("disabled");
						$("#cancelEditCandidateContactMech").removeAttr("disabled");
					}
				});
			});
		}
	};
	
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	editCandidateInfoObj.init();
	editCandidateContactMechs.init();
});