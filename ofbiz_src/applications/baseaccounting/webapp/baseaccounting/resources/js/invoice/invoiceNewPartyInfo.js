var invoicePartyInfo = (function(){
	var _data = {};
	var init = function(){
		initInput();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#infoInvoiceAddress").jqxInput({width: '95%', height: 20});
		$("#infoInvoiceTaxCode").jqxInput({width: '95%', height: 20});
		$("#infoInvoicePartyName").jqxInput({width: '95%', height: 20});
		$("#infoInvoicePhoneNbr").jqxInput({width: '95%', height: 20});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#infoInvoiceCountry"), globalVar.countryGeoArr, {valueMember: 'geoId', displayMember: 'geoName', width: '97%', height: 25});
		accutils.createJqxDropDownList($("#infoInvoiceState"), [], {valueMember: 'geoId', displayMember: 'geoName', width: '97%', height: 25});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#AddInfoInvoiceWindow"), 420, 340);
	};
	var initEvent = function(){
		$("#infoInvoiceCountry").on('select', function (event){
			var item = args.item;
			var geoId = item.value;
			$.ajax({
				url: 'jqGetAssociatedStateOtherListGeo',
				data: {geoId: geoId, pagesize: 0},
				type: 'POST',
				success: function(response){
					var listGeo = response.results? response.results: [];
					accutils.updateSourceDropdownlist($("#infoInvoiceState"), listGeo);
				},
				complete: function(){
					if(_data.hasOwnProperty('stateGeoId')){
						$("#infoInvoiceState").val(_data.stateGeoId);
					}
				}
			});
		});
		$("#addInvoiceInfoBtn").click(function(){
			accutils.openJqxWindow($("#AddInfoInvoiceWindow"));
		});
		$("#saveAddInfoInvoice").click(function(){
			var valid = $("#AddInfoInvoiceWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var countryGeo = $("#infoInvoiceCountry").jqxDropDownList("getSelectedItem");
			var stateGeo = $("#infoInvoiceState").jqxDropDownList("getSelectedItem");
			_data.partyName = $("#infoInvoicePartyName").val();
			_data.taxCode = $("#infoInvoiceTaxCode").val();
			_data.address = $("#infoInvoiceAddress").val();
			_data.countryGeoId = countryGeo.value;
			_data.countryGeoName = countryGeo.label;
			_data.stateGeoId = stateGeo.value;
			_data.stateGeoName = stateGeo.label;
			if($("#infoInvoicePhoneNbr").val()){
				_data.phoneNbr = $("#infoInvoicePhoneNbr").val();
			}
			$("#AddInfoInvoiceWindow").jqxWindow('close');
		});
		
		$("#clearAddInfoInvoice").click(function(){
			Grid.clearForm($("#AddInfoInvoiceWindow"));
			accutils.updateSourceDropdownlist($("#infoInvoiceState"), []);
			$("#AddInfoInvoiceWindow").jqxValidator('hide');
			_data = {};
			if(globalVar.defaultCountryGeoId){
				$("#infoInvoiceCountry").val(globalVar.defaultCountryGeoId);
			}
		});
		
		$("#cancelAddInfoInvoice").click(function(){
			$("#AddInfoInvoiceWindow").jqxWindow('close');
		});
		$("#AddInfoInvoiceWindow").on('open', function(event){
			if(_data.countryGeoId){
				$("#infoInvoiceCountry").val(_data.countryGeoId);
			}else if(globalVar.defaultCountryGeoId){
				$("#infoInvoiceCountry").val(globalVar.defaultCountryGeoId);
			}
			if(_data.partyName){
				$("#infoInvoicePartyName").val(_data.partyName);
			}
			if(_data.taxCode){
				$("#infoInvoiceTaxCode").val(_data.taxCode);
			}
			if(_data.address){
				$("#infoInvoiceAddress").val(_data.address);
			}
			if(_data.phoneNbr){
				$("#infoInvoicePhoneNbr").val(_data.phoneNbr);
			}
		});
		$("#AddInfoInvoiceWindow").on('close', function(event){
			Grid.clearForm($("#AddInfoInvoiceWindow"));
			accutils.updateSourceDropdownlist($("#infoInvoiceState"), []);
			$("#AddInfoInvoiceWindow").jqxValidator('hide');
		});
	};
	var initValidator = function(){
		$("#AddInfoInvoiceWindow").jqxValidator({
			rules: [
				{
					input: '#infoInvoicePhoneNbr', message: uiLabelMap.PhoneNumberMustInOto9, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(value && !(/^[0-9]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
				{
       				input: '#infoInvoicePhoneNbr', message: uiLabelMap.PhoneNumberMustBeContain10or11character, action: 'keyup, blur', 
       	            rule: function (input) {	
       	         	   	var tmp = $('#infoInvoicePhoneNbr').val();
       	         	   	if (tmp.length > 0 && (tmp.length > 11 || tmp.length < 10)){
       	         	   		return false;
       	         	   	}
       	         	   	return true;
       	            }
       			},
				{
					input: '#infoInvoiceAddress', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoInvoiceTaxCode', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoInvoicePartyName', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoInvoiceCountry', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
					rule: function (input) {	
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				},
				{
					input: '#infoInvoiceState', message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
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
		accutils.openJqxWindow($("#AddInfoInvoiceWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		getSubmitData: getSubmitData 
	}
}());
$(document).ready(function(){
	invoicePartyInfo.init();
});