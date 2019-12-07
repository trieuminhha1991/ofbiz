var paymentPartyInfo = (function(){
	var _data = {};
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#infoPaymentAddress").jqxInput({width: '95%', height: 20});
		$("#infoPaymentTaxCode").jqxInput({width: '95%', height: 20});
		$("#infoPaymentPartyName").jqxInput({width: '95%', height: 20});
		$("#infoPaymentPhoneNbr").jqxInput({width: '95%', height: 20});
		$("#infoPaymentBankCode").jqxInput({width: '95%', height: 20});
		$("#infoPaymentAtTheBank").jqxInput({width: '95%', height: 20});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#infoPaymentCountry"), globalVar.countryGeoArr, {valueMember: 'geoId', displayMember: 'geoName', width: '97%', height: 25});
		accutils.createJqxDropDownList($("#infoPaymentState"), [], {valueMember: 'geoId', displayMember: 'geoName', width: '97%', height: 25});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#AddInfoPaymentWindow"), 520, 440);
	};
	var initEvent = function(){
		$("#infoPaymentCountry").on('select', function (event){
			var item = args.item;
			var geoId = item.value;
			$.ajax({
				url: 'jqGetAssociatedStateOtherListGeo',
				data: {geoId: geoId, pagesize: 0},
				type: 'POST',
				success: function(response){
					var listGeo = response.results? response.results: [];
					accutils.updateSourceDropdownlist($("#infoPaymentState"), listGeo);
				},
				complete: function(){
					if(_data.hasOwnProperty('stateGeoId')){
						$("#infoPaymentState").val(_data.stateGeoId);
					}
				}
			});
		});
		$("#addPaymentInfoBtn").click(function(){
			accutils.openJqxWindow($("#AddInfoPaymentWindow"));
		});
		$("#saveAddInfoPayment").click(function(){
			var valid = $("#AddInfoPaymentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var countryGeo = $("#infoPaymentCountry").jqxDropDownList("getSelectedItem");
			var stateGeo = $("#infoPaymentState").jqxDropDownList("getSelectedItem");
			_data.partyName = $("#infoPaymentPartyName").val();
			_data.taxCode = $("#infoPaymentTaxCode").val();
			_data.address = $("#infoPaymentAddress").val();
			_data.finAccountCode = $("#infoPaymentBankCode").val();
			_data.finAccountName = $("#infoPaymentAtTheBank").val();
			_data.countryGeoId = countryGeo.value;
			_data.countryGeoName = countryGeo.label;
			_data.stateGeoId = stateGeo.value;
			_data.stateGeoName = stateGeo.label;
			if($("#infoPaymentPhoneNbr").val()){
				_data.phoneNbr = $("#infoPaymentPhoneNbr").val();
			}
			$("#AddInfoPaymentWindow").jqxWindow('close');
		});
		
		$("#clearAddInfoPayment").click(function(){
			Grid.clearForm($("#AddInfoPaymentWindow"));
			accutils.updateSourceDropdownlist($("#infoPaymentState"), []);
			$("#AddInfoPaymentWindow").jqxValidator('hide');
			_data = {};
			if(globalVar.defaultCountryGeoId){
				$("#infoPaymentCountry").val(globalVar.defaultCountryGeoId);
			}
		});
		
		$("#cancelAddInfoPayment").click(function(){
			$("#AddInfoPaymentWindow").jqxWindow('close');
		});
		$("#AddInfoPaymentWindow").on('open', function(event){
			if(_data.countryGeoId){
				$("#infoPaymentCountry").val(_data.countryGeoId);
			}else if(globalVar.defaultCountryGeoId){
				$("#infoPaymentCountry").val(globalVar.defaultCountryGeoId);
			}
			if(_data.partyName){
				$("#infoPaymentPartyName").val(_data.partyName);
			}
			if(_data.taxCode){
				$("#infoPaymentTaxCode").val(_data.taxCode);
			}
			if(_data.address){
				$("#infoPaymentAddress").val(_data.address);
			}
			if(_data.phoneNbr){
				$("#infoPaymentPhoneNbr").val(_data.phoneNbr);
			}
			if(_data.finAccountCode){
				$("#infoPaymentBankCode").val(_data.finAccountCode);
			}
			if(_data.finAccountName){
				$("#infoPaymentAtTheBank").val(_data.finAccountName);
			}			
		});
		$("#AddInfoPaymentWindow").on('close', function(event){
			Grid.clearForm($("#AddInfoPaymentWindow"));
			accutils.updateSourceDropdownlist($("#infoPaymentState"), []);
			$("#AddInfoPaymentWindow").jqxValidator('hide');
		});
	};
	var initValidator = function(){
		$("#AddInfoPaymentWindow").jqxValidator({
			rules: [
				{
					input: '#infoPaymentPhoneNbr', message: uiLabelMap.PhoneNumberMustInOto9, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(value && !(/^[0-9]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
				{
       				input: '#infoPaymentPhoneNbr', message: uiLabelMap.PhoneNumberMustBeContain10or11character, action: 'keyup, blur', 
       	            rule: function (input) {	
       	         	   	var tmp = $('#infoPaymentPhoneNbr').val();
       	         	   	if (tmp.length > 0 && (tmp.length > 11 || tmp.length < 10)){
       	         	   		return false;
       	         	   	}
       	         	   	return true;
       	            }
       			},
				{
					input: '#infoPaymentAddress', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoPaymentTaxCode', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoPaymentBankCode', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoPaymentAtTheBank', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},				
				{
					input: '#infoPaymentPartyName', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoPaymentCountry', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoPaymentState', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				
			]
		});
	};
	var getSubmitData = function(){
		return _data;
	};
	var openWindow = function(){
		accutils.openJqxWindow($("#AddInfoPaymentWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		getSubmitData: getSubmitData 
	}
}());
$(document).ready(function(){
	paymentPartyInfo.init();
});