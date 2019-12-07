$(function(){
	ReqEditInfoObj.init();
});
var ReqEditInfoObj = (function() {
	var validatorVAL;
	var init = function() {
		initInputs();
		initDateTimePicker();
		initEvents();
		initValidateForm();
	}
	
	var initInputs = function(){
		$("#description").jqxInput({ width: 300, height: 90});
		$("#description").text(requirement.reason);
		if (requirement.requirementTypeId){
 			for (var i = 0; i < requirementTypeData.length; i++){
 				if (requirement.requirementTypeId == requirementTypeData[i].requirementTypeId){
 					$('#requirementTypeId').text(requirementTypeData[i].description);
 				}
 			}
		};
		if (requirement.reasonEnumId){
 			for (var i = 0; i < reasonEnumData.length; i++){
 				if (requirement.reasonEnumId == reasonEnumData[i].enumId){
 					$('#reasonEnumId').text(reasonEnumData[i].description);
 				}
 			}
		};
		
		for(var i =0; i< originFacilityData.length; i++ ){
			if(requirement.facilityId == originFacilityData[i].facilityId){
				originFacilitySelected = $.extend({}, originFacilityData[i]);
			}
			if(requirement.destFacilityId == originFacilityData[i].facilityId){
				destFacilitySelected = $.extend({}, originFacilityData[i]);
			}
		}
		if (originFacilitySelected) {
        	if (originFacilitySelected.facilityCode != null){
        		description = '['+ originFacilitySelected.facilityCode +'] ' + originFacilitySelected.facilityName;
        	} else {
        		description = '['+ originFacilitySelected.facilityId +'] ' + originFacilitySelected.facilityName;
        	}
	        $('#originFacility').text(description);
        }
	}
	

	var initDateTimePicker = function(){
		$("#requirementStartDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#requirementStartDate").jqxDateTimeInput('val', requirement.requirementStartDate);
	}
	
	
	var initEvents = function(){
			
	}
	
	
	var initValidateForm = function(){
		var extendRules = [
				{input: '#requirementStartDate', message: uiLabelMap.CannotBeforeNow , action: 'change', position: 'topcenter',
					rule: function(input, commit){
						var value = $('#requirementStartDate').jqxDateTimeInput('getDate');
						var nowDate = new Date();
						if(value < nowDate){
							return false;
						}
						return true;
					}
				},
              ];
   		var mapRules = [
   				{input: '#requirementStartDate', type: 'validInputNotNull'},
               ];
   		validatorVAL = new OlbValidator($('#editRequirement'), mapRules, extendRules, {position: 'right'});
	};
	
	var getValidator = function(){
		return validatorVAL;
	}
	
	
	return {
		init: init,
		getValidator : getValidator
	}
}());