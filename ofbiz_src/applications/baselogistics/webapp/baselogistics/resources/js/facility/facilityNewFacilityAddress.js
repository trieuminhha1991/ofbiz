$(function(){
	FacilityAddressObj.init();
});
var FacilityAddressObj = (function() {
	var validatorVAL;
	
	var provinceData = new Array();
	var countyData = new Array();
	var wardData = new Array();
	var init = function() {
		initElementComplex();
		initInputs();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#phoneNumber").jqxInput({width: 300, height: 24,});
		$("#address").jqxInput({width: 305, height: 102,});
		
		update({
			geoId: $("#countryGeoId").val(),
			geoAssocTypeId: "REGIONS",
			geoTypeId: "PROVINCE",
			}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'provinceGeoId');
		
		update({
			geoId: $("#provinceGeoId").val(),
			geoAssocTypeId: "REGIONS",
			geoTypeId: "DISTRICT",
			}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'districtGeoId');
		
		update({
			geoId: $("#districtGeoId").val(),
			geoAssocTypeId: "REGIONS",
			geoTypeId: "WARD",
			}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'wardGeoId');
	};
	
	var initElementComplex = function() {
		var countryGeoDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '300',
			dropDownHeight: '300',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#countryGeoId"), countryData, countryGeoDataConfig, ["VNM"]);
		
		var provinceDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '300',
			dropDownHeight: '300',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#provinceGeoId"), provinceData, provinceDataConfig, []);
			
		var countyGeoDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '300',
			dropDownHeight: '300',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#districtGeoId"), countyData, countyGeoDataConfig, []);
		
		var wardGeoDataConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'geoId',
			value: 'description',
			height: '25',
			width: '300',
			dropDownHeight: '300',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#wardGeoId"), wardData, wardGeoDataConfig, []);
	};
	
	var initEvents = function() {
		$("#countryGeoId").on('change', function(event){
			update({
				geoId: $("#countryGeoId").val(),
				geoAssocTypeId: "REGIONS",
				geoTypeId: "PROVINCE",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'provinceGeoId');
		});
		$("#provinceGeoId").on('change', function(event){
			update({
				geoId: $("#provinceGeoId").val(),
				geoAssocTypeId: "REGIONS",
				geoTypeId: "DISTRICT",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'districtGeoId');
		});
		
		$("#districtGeoId").on('change', function(event){
			update({
				geoId: $("#districtGeoId").val(),
				geoAssocTypeId: "REGIONS",
				geoTypeId: "WARD",
				}, 'getGeoAssocs' , 'listGeos', 'geoId', 'geoName', 'wardGeoId');
		});
	};
	var initValidateForm = function(){
		var extendRules = [
				{
					input: '#phoneNumber',
					message: uiLabelMap.OnlyNumberInput,
					action: 'valueChanged',
					rule: function(input, commit){
						var regular = /^\d+$/;
						var value = $(input).val();
						return regular.test(value);
					}
				},
				{
					input: '#address',
					message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter,
					action: 'valueChanged',
					rule: function(input, commit){
						var regular = /[~`!#@$%\^&*+=\\[\]\\';{}|\\":<>\?]/;
						var value = $(input).val();
						if(value && regular.test(value)){
							return false;
						}
						return true;
					}
				}
              ];
   		var mapRules = [
				{input: '#countryGeoId', type: 'validInputNotNull'},
				{input: '#provinceGeoId', type: 'validInputNotNull'}, 
				{input: '#districtGeoId', type: 'validInputNotNull'},
				{input: '#wardGeoId', type: 'validInputNotNull'},          
				{input: '#phoneNumber', type: 'validInputNotNull'},
				{input: '#address', type: 'validInputNotNull'},          
               ];
   		validatorVAL = new OlbValidator($('#initFacilityAddress'), mapRules, extendRules, {position: 'right'});
	};
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
    var getValidator = function(){
    	return validatorVAL;
    }
	return {
		init: init,
		getValidator: getValidator,
	}
}());