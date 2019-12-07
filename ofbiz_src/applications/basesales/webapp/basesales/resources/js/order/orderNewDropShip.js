$(function(){
	OlbOrderDropShip.init();
});
var OlbOrderDropShip = (function(){
	var validatorVAL;
	
	var init = function(){
		initElement();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initElement = function(){
		jOlbUtil.checkBox.create("#requestFavorDelivery");
		if (isFavorDelivery) $("#requestFavorDelivery").jqxCheckBox('check');
	};
	var initElementComplex = function(){
		var configSupplierDelivery = {
			widthButton: '100%',
			filterable: false,
			pageable: true,
			showfilterrow: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSSupplierId, datafield: 'partyCode', width: '40%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			useUrl: true,
			root: 'results',
			url: defaultProductStoreId != null ? 'JQGetPartiesSupplier' : '',
			useUtilFunc: true,
			selectedIndex: 0,
			key: 'partyId',
			keyCode: 'partyCode',
			description: 'fullName',
		};
		favorSupplierPartyDDB = new OlbDropDownButton($("#favorSupplierPartyId"), $("#supplierPartyGrid"), null, configSupplierDelivery, []);
		
		var configFacilityDelivery = {
			widthButton: '100%',
			filterable: false,
			pageable: true,
			showfilterrow: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'facilityId', type: 'string'}, {name: 'facilityName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSFacilityId, datafield: 'facilityId', width: '40%'},
				{text: uiLabelMap.BSFacilityName, datafield: 'facilityName'}
			],
			useUtilFunc: true,
			useUrl: true,
			root: 'results',
			url: defaultProductStoreId != null ? 'JQGetFacilitiesByProductStore&productStoreId=' + defaultProductStoreId : '',
			
			selectedIndex: 0,
			key: 'facilityId',
			description: ['facilityName'],
		};
		shipGroupFacilityDDB = new OlbDropDownButton($("#shipGroupFacilityId"), $("#facilityGrid"), null, configFacilityDelivery, []);
	}
	var initEvents = function(){
		favorSupplierPartyDDB.getGrid().bindingCompleteListener(function(){
			if (favorSupplierPartyId != null) {
	    		//favorSupplierPartyDDB.putConfig("defaultValue", favorSupplierPartyId);
	    		//favorSupplierPartyDDB.putConfig("defaultLabel", favorSupplierPartyFullName);
		    	favorSupplierPartyDDB.selectItem([favorSupplierPartyId]);
			}
		}, true);
		shipGroupFacilityDDB.getGrid().bindingCompleteListener(function() {
			shipGroupFacilityDDB.selectItem([shipGroupFacilityId]);
		});
		$('#requestFavorDelivery').on('change', function (event) {
			var checked = event.args.checked;
			if (checked) {
				$(".favor-delivery-container").show(600);
			} else {
				$(".favor-delivery-container").hide(600);
			}
		});
	};
	var initValidateForm = function(){
		var extendRules = [
				{input: '#favorSupplierPartyId', message: uiLabelMap.BSRequired, action: 'close', rule: 
					function (input, commit) {
						var isFavorDelivery = $('#requestFavorDelivery').val();
						var value = $(input).val();
						if (isFavorDelivery && !OlbCore.isNotEmpty(value)) {
							return false;
						}
						return true;
					}
				},
				{input: '#shipGroupFacilityId', message: uiLabelMap.BSRequired, action: 'close', rule: 
					function (input, commit) {
						var isFavorDelivery = $('#requestFavorDelivery').val();
						var value = $(input).val();
						if (isFavorDelivery && !OlbCore.isNotEmpty(value)) {
							return false;
						}
						return true;
					}
				},
			];
		// #salesExecutiveId - validInputNotNull
		var mapRules = [];
		validatorVAL = new OlbValidator($('#checkoutOrderEntry'), mapRules, extendRules, {scroll: true});
	};
	var getValidator = function() {
		return validatorVAL;
	};
	return {
		init: init,
		getValidator: getValidator,
	};
}());