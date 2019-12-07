var generalInfoCandidateObj = (function(){
	var init = function(){
		initJqxInput();
		initJqxDateTimeInput();
		initJqxNumberInput();
		initJqxDropDownList();
		initJqxValidator();
	};
	
	var initJqxInput = function(){		
		$('#candidateId').jqxInput({width : '95%',height : '20px'});
		$('#lastNameCandidate').jqxInput({width : '95%',height : '20px'});
		$('#middleNameCandidate').jqxInput({width : '95%',height : '20px'});
		$('#firstNameCandidate').jqxInput({width : '95%',height : '20px'});
		$('#nativeLandInputCandidate').jqxInput({width : '95%',height : '20px'});
		$("#editIdNumberCandidate").jqxInput({ width: '95%', height: 19});
		//$("#graduationYearCandidate").jqxInput({ width : '95%', height : '20px'});
	};
	
	var initJqxDateTimeInput = function(){
		$("#birthDateCandidate").jqxDateTimeInput({width: '97%', height: '25px'});
		$("#idIssueDateTimeCandidate").jqxDateTimeInput({width: '97%', height: '25px'});
		$("#idIssueDateTimeCandidate").val(null);
		$("#birthDateCandidate").val(null);
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.genderArr, $("#genderCandidate"), 'genderId', 'description', 25, '97%');
		$("#genderCandidate").jqxDropDownList({selectedIndex: 0});
		createJqxDropDownList([], $("#idIssuePlaceDropDownCandidate"), "geoId", "geoName", 25, "97%");
		$("#idIssuePlaceDropDownCandidate").jqxDropDownList({dropDownHeight: 180});
		createJqxDropDownList([], $("#ethnicOriginDropdownCandidate"), "ethnicOriginId", "description", 25, "97%");
		createJqxDropDownList([], $("#maritalStatusDropdownCandidate"), "ethnicOriginId", "description", 25, "97%");
		createJqxDropDownList([], $("#religionDropdownCandidate"), "religionId", "description", 25, "97%");
		createJqxDropDownList([], $("#nationalityDropdownCandidate"), "nationalityId", "description", 25, "97%");
		createJqxDropDownList([], $("#majorCandidate"), "majorId", "description", 25, "97%");
		createJqxDropDownList([], $("#classificationTypeCandidate"), "classificationTypeId", "description", 25, "97%");
		createJqxDropDownList([], $("#educationSystemTypeCandidate"), "educationSystemTypeId", "description", 25, "97%");
	};
	
	var initJqxNumberInput = function(){
		$("#graduationYearCandidate").jqxNumberInput({ width: '97%', digits: 4, height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 0});
	};
	
	var getDataOnWindowOpen = function(){
		var condition = [{fieldName: "geoIdFrom", fieldValue: globalVar.defaultCountry}];
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "geoStateProvinceArr", $("#idIssuePlaceDropDownCandidate"), 
				"getListStateProvinceGeo", JSON.stringify(condition));
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "ethnicOriginArr", $("#ethnicOriginDropdownCandidate"), "getEthnicOriginList");
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "religionArr", $("#religionDropdownCandidate"), "getReligionList");
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "nationalityArr", $("#nationalityDropdownCandidate"), "getNationalityList");
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "majorArr", $("#majorCandidate"), "getMajorList");
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "degreeClassTypeArr", $("#classificationTypeCandidate"), "getDegreeClassificationTypeList");
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "educationSystemTypeArr", $("#educationSystemTypeCandidate"), "getEducationSystemTypeList");
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "maritalStatusArr", $("#maritalStatusDropdownCandidate"), "getMaritalStatusList");
		var date = new Date();
		$("#graduationYearCandidate").val(date.getFullYear());
	};
	
	var initJqxValidator = function(){
		$("#generalInfoCandidate").jqxValidator({
			rules: [
			        {
			        	input: '#candidateId',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input: '#candidateId',
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
			        	input: '#firstNameCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input: '#lastNameCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input : '#lastNameCandidate',
			        	message : uiLabelMap.IllegalCharactersAndSpace,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		var space = " ";
			        		if(isContainSpecialChar($(input).val().trim()) || $(input).val().trim().indexOf(space) > -1){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#firstNameCandidate',
			        	message : uiLabelMap.IllegalCharactersAndSpace,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		var space = " ";
			        		if(isContainSpecialChar($(input).val().trim()) || $(input).val().trim().indexOf(space) > -1){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#middleNameCandidate',
			        	message : uiLabelMap.IllegalCharacters,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		if(isContainSpecialChar($(input).val().trim())){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#editIdNumberCandidate',
			        	message : uiLabelMap.OnlyInputNumberGreaterThanZero,
			        	action : 'blur',
			        	rule : function(input,commit){
			        		if($(input).val() < 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#idIssueDateTimeCandidate',
			        	message : uiLabelMap.IdentifyDayGreaterBirthDate,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		if($('#birthDateCandidate').val() != "" && $(input).val() != ""){
			        			if($(input).jqxDateTimeInput('getDate') < $('#birthDateCandidate').jqxDateTimeInput('getDate')){
			        				return false;
			        			}
			        			return true;
			        		}else{
			        			return true;
			        		}
			        	}
			        },
			        {
			        	input : '#graduationYearCandidate',
			        	message : uiLabelMap.OnlyInputNumberGreaterThanZero,
			        	action : 'blur',
			        	rule : function(input, commit){
			        		if(input.val() < 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        }
			]
		});
	};
	
	var validate = function(){
		return $("#generalInfoCandidate").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#generalInfoCandidate").jqxValidator('hide');
	};
	
	var resetData = function(){
		Grid.clearForm($("#generalInfoCandidate"));
	};
	
	var getData = function(){
		var graduationYear;
		if($("#graduationYearCandidate").val() != ""){
			graduationYear =parseInt($("#graduationYearCandidate").val(),10); 
		}else{
			graduationYear = null;
		}
		var data = {
				candidateId: $('#candidateId').val(),
				lastName: $('#lastNameCandidate').val(),
				middleName: $('#middleNameCandidate').val(),
				firstName: $('#firstNameCandidate').val(),
				nativeLand: $('#nativeLandInputCandidate').val(),
				idNumber: $("#editIdNumberCandidate").val(),
				gender: $("#genderCandidate").val(),
				idIssuePlace: $("#idIssuePlaceDropDownCandidate").val(),
				ethnicOrigin: $("#ethnicOriginDropdownCandidate").val(),
				religion: $("#religionDropdownCandidate").val(),
				nationality: $("#nationalityDropdownCandidate").val(),
				maritalStatusId: $("#maritalStatusDropdownCandidate").val(),
				birthPlace: $("#birthPlaceCandidate").val(),
				graduationYear: graduationYear,
				majorId: $("#majorCandidate").val(),
				classificationTypeId: $("#classificationTypeCandidate").val(),
				educationSystemTypeId: $("#educationSystemTypeCandidate").val()
		};
		if($("#birthDateCandidate").jqxDateTimeInput('val', 'date')){
			data.birthDate = $("#birthDateCandidate").jqxDateTimeInput('val', 'date').getTime();
		}
		if($("#idIssueDateTimeCandidate").jqxDateTimeInput('val', 'date')){
			data.idIssueDate = $("#idIssueDateTimeCandidate").jqxDateTimeInput('val', 'date').getTime();
		}
		return data;
	};
	
	return{
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		getDataOnWindowOpen: getDataOnWindowOpen,
		resetData: resetData
	}
}());

var permanentResInfoCandidate = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initJqxValidator();
		/*if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdPermResCandidate").jqxDropDownList('selectItem', globalVar.defaultCountry);
		}*/
	};
	
	var initJqxInput = function(){
		$("#paddress1Candidate").jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
			createJqxDropDownList([], $("#countryGeoIdPermResCandidate"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#stateGeoIdPermResCandidate"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#countyGeoIdPermResCandidate"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#wardGeoIdPermResCandidate"), "geoId", "geoName", 25, "97%");
	};
	
	var getDataOnWindowOpen = function(){
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "countryGeoIdArr", $("#countryGeoIdPermResCandidate"), 
				"getCountryGeoList", null,globalVar.defaultCountry);
	};
	
	var getData = function(){
			var data = {
					address1: $("#paddress1Candidate").val(),
					countryGeoId: $("#countryGeoIdPermResCandidate").val(),
					stateProvinceGeoId: $("#stateGeoIdPermResCandidate").val(),
					districtGeoId: $("#countyGeoIdPermResCandidate").val(),
					wardGeoId: $("#wardGeoIdPermResCandidate").val()
			};
			return data;
	};
	
	var resetData = function(){
		$("#paddress1Candidate").val("");
		if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdPermResCandidate").jqxDropDownList('selectItem', globalVar.defaultCountry);
		}
		$("#stateGeoIdPermResCandidate").jqxDropDownList('clearSelection');
		$("#countyGeoIdPermResCandidate").jqxDropDownList('clearSelection');
		$("#wardGeoIdPermResCandidate").jqxDropDownList('clearSelection');
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdPermResCandidate').on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($("#stateGeoIdPermResCandidate"), data, url);
			}
		});
		
		$("#stateGeoIdPermResCandidate").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($("#countyGeoIdPermResCandidate"), data, url);
			}
		});
		$("#countyGeoIdPermResCandidate").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($("#wardGeoIdPermResCandidate"), data, url);
			}
		});
	};
	
	var hideValidate = function(){
		$("#permanentResidenceCandidate").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#permanentResidenceCandidate").jqxValidator({
			rules: [
			        {
			        	input: '#countryGeoIdPermResCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1Candidate").val();
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
			        	input: '#stateGeoIdPermResCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1Candidate").val();
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
			return $("#permanentResidenceCandidate").jqxValidator('validate');
	};
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		getDataOnWindowOpen: getDataOnWindowOpen,
		resetData: resetData
	}
}());

var currResInfoCandidate = (function(){
	var defaultData = {}
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initBtnEvent();
		initJqxValidator();
		/*if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdCurrResCandidate").jqxDropDownList('selectItem', globalVar.defaultCountry);
		}*/
	};
	
	var initJqxInput = function(){
		$("#address1CurrResCandidate").jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#countryGeoIdCurrResCandidate"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#stateGeoIdCurrResCandidate"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#countyGeoIdCurrResCandidate"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#wardGeoIdCurrResCandidate"), "geoId", "geoName", 25, "97%");
	};
	
	var getDataOnWindowOpen = function(){
		candidateRecUtilObj.getDataTypeForDropDownList(globalVar, "countryGeoIdArr", $("#countryGeoIdCurrResCandidate"), 
				"getCountryGeoList", null, globalVar.defaultCountry);
	};
	
	var getData = function(){
		var data = {
				address1: $("#address1CurrResCandidate").val(),
				countryGeoId: $("#countryGeoIdCandidate").val(),
				stateProvinceGeoId: $("#stateGeoIdCurrResCandidate").val(),
				districtGeoId: $("#countyGeoIdCurrResCandidate").val(),
				wardGeoId: $("#wardGeoIdCurrResCandidate").val()
		};
		return data;
	};
	
	var resetData = function(){
		$("#address1CurrResCandidate").val("");
		if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdCurrResCandidate").jqxDropDownList('selectItem', globalVar.defaultCountry);
		}
		$("#stateGeoIdCurrResCandidate").jqxDropDownList('clearSelection');
		$("#countyGeoIdCurrResCandidate").jqxDropDownList('clearSelection');
		$("#wardGeoIdCurrResCandidate").jqxDropDownList('clearSelection');
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdCurrResCandidate').on('select', function(event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($('#stateGeoIdCurrResCandidate'), data, url, defaultData.stateProvinceGeoId);
			}
		});
		
		$("#stateGeoIdCurrResCandidate").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($('#countyGeoIdCurrResCandidate'), data, url, defaultData.districtGeoId);
			}
		});
		
		$("#countyGeoIdCurrResCandidate").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				candidateRecUtilObj.updateSourceJqxDropdownList($('#wardGeoIdCurrResCandidate'), data, url, defaultData.wardGeoId);
			}
		});
	};
	
	var hideValidate = function(){
		$("#currentResidenceCandidate").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#currentResidenceCandidate").jqxValidator({
			rules: [
			        {
			        	input: '#countryGeoIdCurrResCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrResCandidate").val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#stateGeoIdCurrResCandidate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrResCandidate").val();
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
		return $("#currentResidenceCandidate").jqxValidator('validate');
	};
	var initBtnEvent = function(){
		$("#copyPermRes").click(function(){
			defaultData = permanentResInfoCandidate.getData();
			$("#address1CurrResCandidate").val(defaultData.address1);
			$('#countryGeoIdCurrResCandidate').jqxDropDownList('clearSelection');						
			$('#countryGeoIdCurrResCandidate').jqxDropDownList('selectItem', defaultData.countryGeoId);
		});
	};
	
	return {
		init: init,
		validate: validate,
		getData: getData,
		getDataOnWindowOpen: getDataOnWindowOpen,
		hideValidate: hideValidate,
		resetData: resetData
	}
}());

var editRecruitCandidateInfoObj = (function(){
	var defaultData = {};
	var init = function(){
		initJqxDateTimeInput();
		initJqxDropDownList();
		initEvent();
	};

	var initJqxDateTimeInput = function(){
		$("#dateReceivingAppl").jqxDateTimeInput({width: '96%', height: 25});
		$("#dateReceivingAppl").val(null);
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownListBinding($("#recruitmentSourceCandidate"), [{name: 'recruitSourceTypeId'}, {name: 'recruitSourceName'}], 
				"getRecruitmentSourceType", "listReturn", "recruitSourceTypeId", "recruitSourceName", "99%", 25);
		createJqxDropDownListBinding($("#recruitmentChannelCandidate"), [{name: 'recruitChannelTypeId'}, {name: 'recruitChannelName'}], 
				"getRecruitmentChannelType", "listReturn", "recruitChannelTypeId", "recruitChannelName", "99%", 25);
		$("#recruitmentChannelCandidate").on('bindingComplete', function(event){
			if(recruitChannelTypeObj.getRecruitChannelTypeAddNew()){
				$("#recruitmentChannelCandidate").val(recruitChannelTypeObj.getRecruitChannelTypeAddNew());
			}
		});
		$("#recruitmentSourceCandidate").on('bindingComplete', function(event){
			if(recruitSourceTypeObj.getRecruitSourceTypeIdNew()){
				$("#recruitmentSourceCandidate").val(recruitSourceTypeObj.getRecruitSourceTypeIdNew());
			}
		});
	};
	
	var initJqxEditor = function(){
		$("#commentCandidate").jqxEditor({ 
    		width: '97%',
            theme: 'olbiuseditor',
            tools: '',
            height: 150,
        });
		if(defaultData.hasOwnProperty("comment")){
			$("#requirementDesc").val(defaultData.comment);
		}
	};
	
	var resetData = function(){
		Grid.clearForm($("#recruitmentCandidateInfo"));
	};
	
	var getData = function(){
		var retData = {};
		retData.recruitChannelTypeId = $("#recruitmentChannelCandidate").val();
		retData.recruitSourceTypeId = $("#recruitmentSourceCandidate").val();
		var dateReceivingAppl = $("#dateReceivingAppl").jqxDateTimeInput('val', 'date');
		if(dateReceivingAppl){
			retData.dateReceiveApply = dateReceivingAppl.getTime(); 
		}
		return retData;
	};
	var initEvent = function(){
		$("#addNewRecruitmentChannel").click(function(event){
			recruitChannelTypeObj.openWindow();
		});
		$("#addNewRecruitmentSource").click(function(event){
			recruitSourceTypeObj.openWindow();
		});
	};
	
	return{
		init: init,
		getData: getData,
		initOnWindowCreated: initJqxEditor,
		resetData: resetData
	}
}());

var recruitSourceTypeObj = (function(){
	var _recruitSourceTypeId = null;
	var init = function(){
		initJqxInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerCreateSourceTypeRecruit"));
	};
	var initEvent = function(){
		$("#cancelNewRecruitmentSource").click(function(event){
			$("#AddNewRecruitmentSourceTypeWindow").jqxWindow('close');
		});
		$("#saveNewRecruitmentSource").click(function(event){
			var valid = $("#AddNewRecruitmentSourceTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var dataSubmit = {};
			dataSubmit.recruitSourceName = $("#recruitmentSourceNameNew").val();
			dataSubmit.comment = $("#commentRecruitmentSourceType").val();
			$("#cancelNewRecruitmentSource").attr("disabled", "disabled");
			$("#saveNewRecruitmentSource").attr("disabled", "disabled");
			$("#loadingCreateSourceTypeRecruit").show();
			$.ajax({
				url: 'createRecruitmentSourceType',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						_recruitSourceTypeId = response.recruitSourceTypeId;
						updateJqxDropDownListBinding($("#recruitmentSourceCandidate"), "getRecruitmentSourceType", response.recruitSourceTypeId);
						$("#AddNewRecruitmentSourceTypeWindow").jqxWindow('close');
					}else{
						_recruitSourceTypeId = null;
						bootbox.dialog(response._ERROR_MESSAGE_,
								[{
									"label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
								}]		
							);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#loadingCreateSourceTypeRecruit").hide();
					$("#cancelNewRecruitmentSource").removeAttr("disabled");
					$("#saveNewRecruitmentSource").removeAttr("disabled");
				}
			});
		});
	};
	var initJqxValidator = function(){
		$("#AddNewRecruitmentSourceTypeWindow").jqxValidator({
			rules: [
			        { input: '#recruitmentSourceNameNew', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			]
		});
	};
	var initJqxInput = function(){
		$("#recruitmentSourceNameNew").jqxInput({width: '96%', height: 20});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			$("#commentRecruitmentSourceType").jqxEditor({ 
	    		width: '98%',
	            theme: 'olbiuseditor',
	            tools: '',
	            height: 150,
	        });
		};
		createJqxWindow($("#AddNewRecruitmentSourceTypeWindow"), 500, 310, initContent);
		$("#AddNewRecruitmentSourceTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	var openWindow = function(){
		openJqxWindow($("#AddNewRecruitmentSourceTypeWindow"));
	};
	var getRecruitSourceTypeIdNew = function(){
		return _recruitSourceTypeId;
	};
	return{
		init: init,
		openWindow: openWindow,
		getRecruitSourceTypeIdNew: getRecruitSourceTypeIdNew
	}
}());

var recruitChannelTypeObj = (function(){
	var _recruitChannelTypeId = null;
	var init = function(){
		initJqxInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerCreateChannelTypeRecruit"));
	};
	var initJqxValidator = function(){
		$("#AddNewRecruitmentChannelTypeWindow").jqxValidator({
			rules: [
			        { input: '#recruitmentChannelNameNew', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			]
		});
	};
	var initEvent = function(){
		$("#cancelNewRecruitmentChannelType").click(function(event){
			$("#AddNewRecruitmentChannelTypeWindow").jqxWindow('close');
		});
		$("#saveNewRecruitmentChannelType").click(function(event){
			var valid = $("#AddNewRecruitmentChannelTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var dataSubmit = {};
			dataSubmit.recruitChannelName = $("#recruitmentChannelNameNew").val();
			dataSubmit.comment = $("#commentRecruitmentChannelType").val();
			$("#cancelNewRecruitmentChannelType").attr("disabled", "disabled");
			$("#saveNewRecruitmentChannelType").attr("disabled", "disabled");
			$("#loadingCreateChannelTypeRecruit").show();
			$.ajax({
				url: 'createRecruitmentChannelType',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						_recruitChannelTypeId = response.recruitChannelTypeId
						updateJqxDropDownListBinding($("#recruitmentChannelCandidate"), "getRecruitmentChannelType")
						$("#AddNewRecruitmentChannelTypeWindow").jqxWindow('close');
					}else{
						_recruitChannelTypeId = null;
						bootbox.dialog(response._ERROR_MESSAGE_,
								[{
									"label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
								}]		
							);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#loadingCreateChannelTypeRecruit").hide();
					$("#cancelNewRecruitmentChannelType").removeAttr("disabled");
					$("#saveNewRecruitmentChannelType").removeAttr("disabled");
				}
			});
		});
	};
	var initJqxInput = function(){
		$("#recruitmentChannelNameNew").jqxInput({width: '96%', height: 20});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			$("#commentRecruitmentChannelType").jqxEditor({ 
				width: '98%',
				theme: 'olbiuseditor',
				tools: '',
				height: 150,
			});
		};
		createJqxWindow($("#AddNewRecruitmentChannelTypeWindow"), 500, 310, initContent);
		$("#AddNewRecruitmentChannelTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	var openWindow = function(){
		openJqxWindow($("#AddNewRecruitmentChannelTypeWindow"));
	};
	var getRecruitChannelTypeAddNew = function(){
		return _recruitChannelTypeId;
	};
	return{
		init: init,
		getRecruitChannelTypeAddNew: getRecruitChannelTypeAddNew,
		openWindow: openWindow
	}
}());

var wizardEditCandidate = (function(){
	var _roundOrder = null;
	var _functionAfterCreateCandidate = null;
	var init = function(){
		initWizard();
		initJqxWindow();
		create_spinner($("#spinnerAjaxCandidate"));
	};
	
	var initWizard = function(){
		$('#fueluxWizardCandidate').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
	        	var valid = generalInfoCandidateObj.validate();
	        	if(!valid){
	        		return false;
	        	}
	        }else if (info.step == 2 && (info.direction == "next")) {
	        	return (permanentResInfoCandidate.validate() && currResInfoCandidate.validate()) ;
	        }else if(info.step == 3 && (info.direction == "next")){
	        	
	        }
	        if(info.direction == "previous"){
	        	
	        }
		}).on('finished', function(e) {
			bootbox.dialog(uiLabelMap.CreateNewCandidateConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		createNewCandidate();   	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
	    }).on('stepclick', function(e){
	        //return false;//prevent clicking on steps
	    });;
	};
	
	var createNewCandidate = function(){
		var recruitmentPlanId = recruitmentCandidateListObj.getRecruitmentPlanId();
		var generalInfo = generalInfoCandidateObj.getData();
		var permanentResInfoData = permanentResInfoCandidate.getData();
		var currResInfoData = currResInfoCandidate.getData();
		var recruitmenInfo = editRecruitCandidateInfoObj.getData();
		var dataSubmit = $.extend({}, dataSubmit, generalInfo, recruitmenInfo);
		dataSubmit.permanentRes = JSON.stringify(permanentResInfoData);
    	dataSubmit.currRes = JSON.stringify(currResInfoData);
    	dataSubmit.recruitmentPlanId = recruitmentPlanId;
    	if(_roundOrder != null){
    		dataSubmit.roundOrder = _roundOrder;
    	}
    	$("#ajaxLoadingCandidate").show();
		disableBtn();
		$.ajax({
			url: 'createNewRecruitmentCandidate',
    		data: dataSubmit,
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == 'success'){
    				if(_functionAfterCreateCandidate != null && typeof(_functionAfterCreateCandidate) == "function"){
    					_functionAfterCreateCandidate(response.successMessage);
    				}
    				$("#addRecruitCandidateWindow").jqxWindow('close');
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
    			$("#ajaxLoadingCandidate").hide();
				enableBtn();
    		}
		});
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			editRecruitCandidateInfoObj.initOnWindowCreated();
		};
		createJqxWindow($("#addRecruitCandidateWindow"), 850, 550, initContent);
		$("#addRecruitCandidateWindow").on('open', function(event){
			$("#ajaxLoadingCandidate").show();
			disableBtn();
			$.when(
				generalInfoCandidateObj.getDataOnWindowOpen(),
				permanentResInfoCandidate.getDataOnWindowOpen(),
				currResInfoCandidate.getDataOnWindowOpen()
			).done(function(){
				$("#ajaxLoadingCandidate").hide();
				enableBtn();
			});
		});
		
		$("#addRecruitCandidateWindow").on('close', function(event){
			generalInfoCandidateObj.resetData();
			generalInfoCandidateObj.hideValidate();
			permanentResInfoCandidate.resetData();
			permanentResInfoCandidate.hideValidate();
			currResInfoCandidate.resetData();
			currResInfoCandidate.hideValidate();
			editRecruitCandidateInfoObj.resetData();
			resetStep();
		});
	};
	
	var disableBtn = function(){
		$("#btnNextCandidate").attr("disabled", "disabled");
		$("#btnPrevCandidate").attr("disabled", "disabled");
	};
	
	var enableBtn = function(){
		$("#btnNextCandidate").removeAttr("disabled");
		$("#btnPrevCandidate").removeAttr("disabled");
	};
	
    var resetStep = function(){
    	$('#fueluxWizardCandidate').wizard('previous');
		$('#fueluxWizardCandidate').wizard('previous');
	};
	
	var setRoundOrder = function(roundOrder){
		_roundOrder = roundOrder;
	};
	
	var setFunctionAfterCreateCandidate = function(functionAfterCreateCandidate){
		_functionAfterCreateCandidate = functionAfterCreateCandidate;
	};
	
	return{
		init: init,
		setFunctionAfterCreateCandidate: setFunctionAfterCreateCandidate,
		setRoundOrder: setRoundOrder
	}
}());


$(document).ready(function(){
	permanentResInfoCandidate.init();
	currResInfoCandidate.init();
	generalInfoCandidateObj.init();
	editRecruitCandidateInfoObj.init();
	recruitSourceTypeObj.init();
	recruitChannelTypeObj.init();
	wizardEditCandidate.init();
});