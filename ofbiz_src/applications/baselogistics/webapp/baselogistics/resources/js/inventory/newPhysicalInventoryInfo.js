$(function(){
	PhysicalInvInfoObj.init();
});
var PhysicalInvInfoObj = (function() {
	var validatorVAL;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$('#facilityId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source: facilityData,selectedIndex: 0, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
		$('#partyId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source: partyData,selectedIndex: 0, theme: theme, displayMember: 'description', valueMember: 'partyId',
		});
		$("#physicalInventoryDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false, theme: theme});
		$("#physicalInventoryDate").jqxDateTimeInput('clear');
		$("#generalComments").jqxInput({ width: 300, height: 105});
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#facilityId').on('change', function(event){
			PhysicalInvObj.getData();
			listInvToUpdates = [];
			if ($('#jqxgridInventoryItemUpdate').length > 0){
				$('#jqxgridInventoryItemUpdate').jqxGrid('clear');
			}
		});
	};
	var initValidateForm = function(){
		var extendRules = [
               {input: '#physicalInventoryDate', message: uiLabelMap.CannotAfterNow, action: 'valueChanged', 
					rule: function(input, commit){
						var physicalInventoryDate = $('#physicalInventoryDate').jqxDateTimeInput('getDate');
						var now = new Date();
						if (physicalInventoryDate > now) {
					   		return false;
					   	}
						return true;
					}
				},
              ];
   		var mapRules = [
   	            {input: '#facilityId', type: 'validInputNotNull'},
   				{input: '#partyId', type: 'validInputNotNull'},
   				{input: '#physicalInventoryDate', type: 'validInputNotNull'},
               ];
   		validatorVAL = new OlbValidator($('#initPhyscialInv'), mapRules, extendRules, {position: 'right'});
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