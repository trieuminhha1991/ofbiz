var contactCommonInfoObj = (function(){
	var updateSourceJqxDropdownList = function (dropdownlistEle, data, url, selectItem){
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
    };
    return{
    	updateSourceJqxDropdownList: updateSourceJqxDropdownList
    }
}());

var permanentResInfo = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initJqxValidator();
		if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdPermRes" + globalVar.defaultSuffix).jqxDropDownList('selectItem', globalVar.defaultCountry);
		}
		
	};
	
	var initJqxInput = function(){
		$("#paddress1" + globalVar.defaultSuffix).jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
			createJqxDropDownList(globalVar.geoCountryList, $("#countryGeoIdPermRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#stateGeoIdPermRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#countyGeoIdPermRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#wardGeoIdPermRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
	};
	
	var getData = function(){
		var address1 = $("#paddress1" + globalVar.defaultSuffix).val();
		var data = {};
		if(address1 && address1.length > 0){
			data = {
					address1: $("#paddress1" + globalVar.defaultSuffix).val(),
					countryGeoId: $("#countryGeoIdPermRes" + globalVar.defaultSuffix).val(),
					stateProvinceGeoId: $("#stateGeoIdPermRes" + globalVar.defaultSuffix).val(),
					districtGeoId: $("#countyGeoIdPermRes" + globalVar.defaultSuffix).val(),
					wardGeoId: $("#wardGeoIdPermRes" + globalVar.defaultSuffix).val()
			};
		}
		return data;
	};
	
	var resetData = function(){
		$("#paddress1" + globalVar.defaultSuffix).val("");
		if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdPermRes" + globalVar.defaultSuffix).jqxDropDownList('selectItem', globalVar.defaultCountry);
		}
		$("#stateGeoIdPermRes" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#countyGeoIdPermRes" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#wardGeoIdPermRes" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
	};
	
	var initJqxDropDownListEvent = function(){
			$('#countryGeoIdPermRes' + globalVar.defaultSuffix).on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {countryGeoId: value};
					var url = 'getAssociatedStateListHR';
					contactCommonInfoObj.updateSourceJqxDropdownList($("#stateGeoIdPermRes" + globalVar.defaultSuffix), data, url);
					updateSourceDropdownlist($("#countyGeoIdPermRes" + globalVar.defaultSuffix), []);
					updateSourceDropdownlist($("#wardGeoIdPermRes" + globalVar.defaultSuffix), []);
				}
			});
			
			$("#stateGeoIdPermRes" + globalVar.defaultSuffix).on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {stateGeoId: value};
					var url = 'getAssociatedCountyListHR';
					contactCommonInfoObj.updateSourceJqxDropdownList($("#countyGeoIdPermRes" + globalVar.defaultSuffix), data, url);
					updateSourceDropdownlist($("#wardGeoIdPermRes" + globalVar.defaultSuffix), []);
				}
			});
			$("#countyGeoIdPermRes" + globalVar.defaultSuffix).on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {districtGeoId: value};
					var url = 'getAssociatedWardListHR';
					contactCommonInfoObj.updateSourceJqxDropdownList($("#wardGeoIdPermRes" + globalVar.defaultSuffix), data, url);
				}
			});
	};
	
	var hideValidate = function(){
		$("#permanentResidence").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#permanentResidence").jqxValidator({
			scroll: false,
			rules: [
			        {
			        	input: '#countryGeoIdPermRes' + globalVar.defaultSuffix,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1" + globalVar.defaultSuffix).val();
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
			        	input: '#stateGeoIdPermRes' + globalVar.defaultSuffix,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1" + globalVar.defaultSuffix).val();
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
                        input: '#paddress1' + globalVar.defaultSuffix,
                        message: uiLabelMap.HRCharacterIsNotValid,
                        action: 'keyup, blur',
                        rule : function(input, commit){
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(),uiLabelMap.HRCheckAddress);
                        }
                    },
			]
		});
	};
	
	var validate = function(){
			return $("#permanentResidence").jqxValidator('validate');
	};
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		resetData: resetData
	}
}());

var currResInfo = (function(){
	var _defaultData = {}
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initBtnEvent();
		initJqxValidator();
		if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdCurrRes" + globalVar.defaultSuffix).jqxDropDownList('selectItem', globalVar.defaultCountry);
		}
	};
	
	var initJqxInput = function(){
		$("#address1CurrRes" + globalVar.defaultSuffix).jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.geoCountryList, $("#countryGeoIdCurrRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#stateGeoIdCurrRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#countyGeoIdCurrRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#wardGeoIdCurrRes" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
	};
	
	var getData = function(){
		var address1 = $("#address1CurrRes" + globalVar.defaultSuffix).val();
		var data = {};
		if(address1 && address1.length > 0){
			data = {
					address1: $("#address1CurrRes" + globalVar.defaultSuffix).val(),
					countryGeoId: $("#countryGeoIdCurrRes" + globalVar.defaultSuffix).val(),
					stateProvinceGeoId: $("#stateGeoIdCurrRes" + globalVar.defaultSuffix).val(),
					districtGeoId: $("#countyGeoIdCurrRes" + globalVar.defaultSuffix).val(),
					wardGeoId: $("#wardGeoIdCurrRes" + globalVar.defaultSuffix).val()
			};
		}
		return data;
	};
	
	var resetData = function(){
		$("#address1CurrRes" + globalVar.defaultSuffix).val("");
		if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdCurrRes" + globalVar.defaultSuffix).jqxDropDownList('selectItem', globalVar.defaultCountry);
		}
		$("#stateGeoIdCurrRes" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#countyGeoIdCurrRes" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#wardGeoIdCurrRes" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdCurrRes' + globalVar.defaultSuffix).on('select', function(event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				contactCommonInfoObj.updateSourceJqxDropdownList($('#stateGeoIdCurrRes' + globalVar.defaultSuffix), data, url, _defaultData.stateProvinceGeoId);
				updateSourceDropdownlist($("#countyGeoIdCurrRes" + globalVar.defaultSuffix), []);
				updateSourceDropdownlist($("#wardGeoIdCurrRes" + globalVar.defaultSuffix), []);
			}
		});
		
		$("#stateGeoIdCurrRes" + globalVar.defaultSuffix).on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				contactCommonInfoObj.updateSourceJqxDropdownList($('#countyGeoIdCurrRes' + globalVar.defaultSuffix), data, url, _defaultData.districtGeoId);
				updateSourceDropdownlist($("#wardGeoIdCurrRes" + globalVar.defaultSuffix), []);
			}
		});
		
		$("#countyGeoIdCurrRes" + globalVar.defaultSuffix).on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				contactCommonInfoObj.updateSourceJqxDropdownList($('#wardGeoIdCurrRes' + globalVar.defaultSuffix), data, url, _defaultData.wardGeoId);
			}
		});
	};
	
	var hideValidate = function(){
		$("#currentResidence").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#currentResidence").jqxValidator({
			rules: [
			        {
			        	input: '#countryGeoIdCurrRes' + globalVar.defaultSuffix,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrRes" + globalVar.defaultSuffix).val();
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
			        	input: '#stateGeoIdCurrRes' + globalVar.defaultSuffix,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrRes" + globalVar.defaultSuffix).val();
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
                        input: '#address1CurrRes' + globalVar.defaultSuffix,
                        message: uiLabelMap.HRCharacterIsNotValid,
                        action: 'keyup, blur',
                        rule : function(input, commit){
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRegex(input.val(),uiLabelMap.HRCheckAddress);
                        }
                    },
			]
		});
	};
	var validate = function(){
		return $("#currentResidence").jqxValidator('validate');
	};
	var initBtnEvent = function(){
		$("#copyPermRes").click(function(){
			_defaultData = permanentResInfo.getData();
			$("#address1CurrRes" + globalVar.defaultSuffix).val(_defaultData.address1);
			$('#countryGeoIdCurrRes' + globalVar.defaultSuffix).jqxDropDownList('clearSelection');						
			$('#countryGeoIdCurrRes' + globalVar.defaultSuffix).jqxDropDownList('selectItem', _defaultData.countryGeoId);
		});
	};
	
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		resetData: resetData
	}
}());

var phoneNumberContactObj = (function(){
	var init = function(){
		$("#phoneNumber" + globalVar.defaultSuffix).jqxInput({width: '95%', height: 20});
        initValidator();
	};
	var resetData = function(){
		$("#phoneNumber" + globalVar.defaultSuffix).val("");
        hideValidate();
	};
	var getData = function(){
		return {phoneNumber: $("#phoneNumber" + globalVar.defaultSuffix).val()};
	};
    var initValidator = function(){
        $("#phoneNumberContact" + globalVar.defaultSuffix).jqxValidator({
            rules:[
                {
                    input: "#phoneNumber"+ globalVar.defaultSuffix,
                    message: uiLabelMap.HRPhoneIsNotValid,
                    action: "keyup, blur",
                    rule:function(input, commit){
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRegex(input.val(),uiLabelMap.HRCheckPhone);
                    }
                }
            ]
        });
    };
    var validate = function(){
        return $("#phoneNumberContact" + globalVar.defaultSuffix).jqxValidator('validate');
    };
    var hideValidate = function(){
        return $("#phoneNumberContact" + globalVar.defaultSuffix).jqxValidator('hide');
    };
	return{
		init: init,
        validate: validate,
        hideValidate: hideValidate,
		getData: getData,
		resetData: resetData
	}
}());

var emailContactObj = (function(){
	var init = function(){
		$("#email" + globalVar.defaultSuffix).jqxInput({width: '95%', height: 20});
		initValidator();
	};
	var resetData = function(){
		$("#email" + globalVar.defaultSuffix).val("");
		hideValidate();
	};
	var initValidator = function(){
        $("#emailContact" + globalVar.defaultSuffix).jqxValidator({
            rules:[
                {
                    input: "#email"+ globalVar.defaultSuffix,
                    message: uiLabelMap.HREmailIsNotValid,
                    action: "keyup, blur",
                    rule:function(input, commit){
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRegex(input.val(),uiLabelMap.HRCheckEmail);
                    }
                }
            ]
        });
	};
	var getData = function(){
		return {emailAddress: $("#email" + globalVar.defaultSuffix).val()};
	};
	var validate = function(){
		return $("#emailContact" + globalVar.defaultSuffix).jqxValidator('validate');
	};
	var hideValidate = function(){
		return $("#emailContact" + globalVar.defaultSuffix).jqxValidator('hide');
	};
	return{
		init: init,
		resetData: resetData,
		getData: getData,
		validate: validate,
		hideValidate: hideValidate
	}
}());