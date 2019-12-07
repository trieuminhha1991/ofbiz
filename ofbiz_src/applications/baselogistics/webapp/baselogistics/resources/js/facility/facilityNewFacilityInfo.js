$(function(){
	FacilityInfoObj.init();
});
var FacilityInfoObj = (function() {
	var validatorVAL;
	var pathScanFile = null;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		
		var sourceProdStoreData = {
			localdata : prodStoreData,
			datatype : "array",
			dataField : [
	             {name : 'description', type :'string'},
	             {name : 'productStoreId', type :'string'},
	         ]
		};
		var dataAdapterStore = new $.jqx.dataAdapter(sourceProdStoreData);
	    // Create a jqxComboBox
	    $("#productStoreId").jqxComboBox({placeHolder: uiLabelMap.PleaseSelectTitle, checkboxes: true, source: dataAdapterStore, displayMember: "description", valueMember: "productStoreId", width: 300, height: 25, dropDownHeight : 300});
			
		$("#facilitySizeAdd").jqxNumberInput({ width: '300px', height: '25px', inputMode: 'simple', spinButtons: true });
		$("#openedDate").jqxDateTimeInput({width: '300px', height: '25px'});
		$("#openedDate").jqxDateTimeInput("val",null);
		$("#facilityName").jqxInput({width: 295, height: 24,});
		$("#facilityCode").jqxInput({width: 295, height: 24,});
		$("#description").jqxInput({ width: 300, height: 75});
		$("#parentFacilityId").jqxDropDownList({ placeHolder : uiLabelMap.PleaseSelectTitle, source: facilityData, displayMember: 'facilityName', valueMember: 'facilityId', theme: theme, width: '300', height: '25'});
		if (parentFacilityId){
			$("#parentFacilityId").jqxDropDownList('val', parentFacilityId);
		}
		var requireLocData = [];
		requireLocData.push({
			text: uiLabelMap.LogYes,
			value: 'Y'
		});
		requireLocData.push({
			text: uiLabelMap.LogNO,
			value: 'N'
		});
		var requireDateData = [];
		requireDateData.push({
			text: uiLabelMap.LogYes,
			value: 'Y'
		});
		requireDateData.push({
			text: uiLabelMap.LogNO,
			value: 'N'
		});
		$("#requireLocation").jqxDropDownList({ placeHolder : uiLabelMap.PleaseSelectTitle, source: requireLocData, displayMember: 'text', valueMember: 'value', theme: theme, width: '300', height: '25'});
		$("#requireLocation").jqxDropDownList('val', 'N');
		$("#requireDate").jqxDropDownList({ placeHolder : uiLabelMap.PleaseSelectTitle, source: requireDateData, displayMember: 'text', valueMember: 'value', theme: theme, width: '300', height: '25'});
		$("#requireDate").jqxDropDownList('val', 'N');
		
		$(function() {
			$('#id-input-file-1 , #imagesPath').ace_file_input({
				no_file: uiLabelMap.NoFile + ' ...',
				btn_choose: uiLabelMap.Choose ,
				btn_change: uiLabelMap.LOGChange ,
				droppable: false,
				onchange: null,
				thumbnail: false
			});
		});
	};
	
	var initElementComplex = function() {
		var countryFaSizeConfig = {
			placeHolder: uiLabelMap.PleaseSelectTitle,
			key: 'key',
			value: 'value',
			height: '25',
			width: '300',
			dropDownHeight: '300',
			autoDropDownHeight: false,
			selectedIndex: 0,
			displayDetail: true,
			dropDownHorizontalAlignment: 'right',
			useUrl: false,
		};
		jLogCommon.initDropDownList($("#facilitySizeUomId"), sourceUom, countryFaSizeConfig, ["AREA_m2"]);
		
	};
	
	var initEvents = function() {
	};
	var initValidateForm = function(){
		var extendRules = [
			 {input: '#facilitySizeAdd', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'valueChanged', rule: 
                 function (input, commit) {
                  	var value = $(input).val();
          			if(value && value < 0){
          				return false;
          			}
          			return true;
             		}
             },
             {input: '#facilityName', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'valueChanged', rule: 
                 function (input, commit) {
            	 	var value = $(input).val();
            	 	if(value && FacilityInfoObj.checkSpecialChars(value)){
          				return false;
          			}
          				return true;
             		}
             },
             {input: '#facilityCode', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'valueChanged', rule: 
                 function (input, commit) {
                  	var value = $(input).val();
                  	if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
          				return false;
          			}
          			return true;
             		}
             },
		];
   		var mapRules = [
			{input: '#facilityName', type: 'validInputNotNull'},
			{input: '#facilityCode', type: 'validInputNotNull'},
			{input: '#productStoreId', type: 'validInputNotNull'},
        ];
   		validatorVAL = new OlbValidator($('#initFacilityInfo'), mapRules, extendRules, {position: 'right'});
	};
	
	var specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-="
	var checkSpecialChars = function(string){
	    for(i = 0; i < specialChars.length;i++){
	        if(string.indexOf(specialChars[i]) > -1){
	            return true
	        }
	    }
	    return false;
	}
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
    
    function removeFile(){
		pathScanFile = null;
		$('#idImagesPath').html("");
		$('#idImagesPath').append("<input type='file' id='imagesPath' name='uploadedFile' class='green-label'/>");
		$('#id-input-file-1 , #imagesPath').ace_file_input({
			no_file: uiLabelMap.NoFile + '...',
			btn_choose: uiLabelMap.Choose,
			btn_change: uiLabelMap.LOGChange,
			droppable:false,
			onchange:null,
			thumbnail:false
		});
	}
    var getValidator = function(){
    	return validatorVAL;
    }
	return {
		init: init,
		getValidator: getValidator,
		checkSpecialChars: checkSpecialChars,
	}
}());