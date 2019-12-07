var providerTrainAndCostObj = (function(){
	var init = function(){
		initJqxDropDownList();
		initJqxInput();
		initJqxNumberInput();
		initJqxRadioButton();
		initJqxCheckBox();
		initEvents();
	};
	
	var initJqxNumberInput = function(){
		$("#amountEmplPaid" + globalVar.createNewSuffix).jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 9, max: 999999999 });
		$("#totalCostEstimated" + globalVar.createNewSuffix).jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 9, max: 999999999, disabled: true});
		$("#amountCompanyPaid" + globalVar.createNewSuffix).jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 9, max: 999999999 });
		$("#nbrEmplEstimated" + globalVar.createNewSuffix).jqxNumberInput({ width: '97%', height: 25, min: 0,  spinButtons: true,decimalDigits: 0, digits: 8});
		$("#nbrDayBeforeStart" + globalVar.createNewSuffix).jqxNumberInput({ width: 50, height: 25, min: 0,  spinButtons: true, decimalDigits: 0, digits: 3, inputMode: 'simple'});
		$("#nbrDayBeforeStart" + globalVar.createNewSuffix).val(1);
		
		$("#amountEmplPaid" + globalVar.createNewSuffix).on('valueChanged', function(event){
			calcTotalCostEstimated();
		});
		$("#amountCompanyPaid" + globalVar.createNewSuffix).on('valueChanged', function(event){
			calcTotalCostEstimated();
		});
		$("#nbrEmplEstimated" + globalVar.createNewSuffix).on('valueChanged', function(event){
			calcTotalCostEstimated();
		});
	};
	var initJqxInput = function(){
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(partyProvider, $("#trainingProvider" + globalVar.createNewSuffix), "partyId", "partyName", 25, '97%');
		createJqxDropDownList([], $("#providerContact" + globalVar.createNewSuffix), "contactMechId", "description", 25, '97%');
	};
	
	var initJqxRadioButton = function(){
		$("#isPublic" + globalVar.createNewSuffix).jqxRadioButton({ width: '98%', height: 25});
		$("#isNotPublic" + globalVar.createNewSuffix).jqxRadioButton({ width: '98%', height: 25, checked: true});
	};
	var initJqxCheckBox = function(){
		$("#allowCancelRegister" + globalVar.createNewSuffix).jqxCheckBox({ width: 320, height: 25, checked: true});
		$("#allowCancelRegister" + globalVar.createNewSuffix).on('change', function (event){
			$("#nbrDayBeforeStart" + globalVar.createNewSuffix).jqxNumberInput({disabled: !event.args.checked});
		});
	};
	
	var initEvents = function(){
		$("#trainingProvider" + globalVar.createNewSuffix).on('change', function(event){
			var idToUpdate = "providerContact" + globalVar.createNewSuffix;
			update({
				partyId: $("#trainingProvider" + globalVar.createNewSuffix).jqxDropDownList('val'),
				contactMechPurposeTypeId: "PRIMARY_LOCATION",
				}, 'getPartyPostalAddressByPurpose' , 'listPartyPostalAddress', 'contactMechId', 'fullName', idToUpdate);
		});
	};

	var calcTotalCostEstimated = function(){
		var totalEmpl = $("#nbrEmplEstimated" + globalVar.createNewSuffix).val();
		var costPerEmpl = $("#amountEmplPaid" + globalVar.createNewSuffix).val();
		var amountCompanyPaid = $("#amountCompanyPaid" + globalVar.createNewSuffix).val();
		var totalCost = (costPerEmpl + amountCompanyPaid) * totalEmpl;
		$("#totalCostEstimated" + globalVar.createNewSuffix).val(totalCost);
	};
	
	var getData = function(){
		var data = {};
		data.estimatedEmplPaid = $("#amountEmplPaid" + globalVar.createNewSuffix).val();
		data.amountCompanySupport = $("#amountCompanyPaid" + globalVar.createNewSuffix).val();
		data.estimatedNumber = $("#nbrEmplEstimated" + globalVar.createNewSuffix).val();
		if($("#isPublic" + globalVar.createNewSuffix).jqxRadioButton('checked')){
			data.isPublic = "Y";
		}
		if($("#isNotPublic" + globalVar.createNewSuffix).jqxRadioButton('checked')){
			data.isPublic = "N";
		}
		if($("#allowCancelRegister" + globalVar.createNewSuffix).jqxCheckBox('checked')){
			data.isCancelRegister = "Y";
			data.cancelBeforeDay = $("#nbrDayBeforeStart" + globalVar.createNewSuffix).val();
		}else{
			data.isCancelRegister = "N";
		}
		var providerId = $("#trainingProvider" + globalVar.createNewSuffix).val();
		if(providerId){
			data.providerId = providerId; 
		}
		return data;
	};
	
	var reset = function(){
		Grid.clearForm($("#trainingCourseProviderAndCost"));
	};
	var onWindowOpen = function(){
		$("#allowCancelRegister" + globalVar.createNewSuffix).jqxCheckBox({checked: true});
		$("#nbrDayBeforeStart" + globalVar.createNewSuffix).val(1);
		$("#isPublic" + globalVar.createNewSuffix).jqxRadioButton({checked: true});
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
    
	return{
		init: init,
		onWindowOpen: onWindowOpen,
		getData: getData,
		reset: reset
	}
}());