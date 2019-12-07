$(function(){
	OlbOrderFacilityConsign.init();
});
var OlbOrderFacilityConsign = (function(){
	var facilityConsignDDB;
	var favorDistributorPartyDDB;
	var validatorVAL;
	
	var init = function(){
		initElement();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initElement = function(){
		jOlbUtil.checkBox.create("#shipFromFacilityConsign");
		if (isShipFromFacilityConsign) $("#shipFromFacilityConsign").jqxCheckBox('check');
	};
	var initElementComplex = function(){
		var configDistributorDelivery = {
			widthButton: '100%',
			filterable: true,
			pageable: true,
			showfilterrow: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSDistributorId, datafield: 'partyCode', width: '40%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			useUrl: true,
			root: 'results',
			url: 'JQGetPartiesDistributor',
			useUtilFunc: true,
			key: 'partyId',
			keyCode: 'partyCode',
			description: 'fullName',
		};
		favorDistributorPartyDDB = new OlbDropDownButton($("#favorDistributorPartyId"), $("#distributorPartyGrid"), null, configDistributorDelivery, []);
		
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
			url: '',
			
			selectedIndex: 0,
			key: 'facilityId',
			description: ['facilityName'],
		};
		facilityConsignDDB = new OlbDropDownButton($("#facilityConsignId"), $("#facilityConsignGrid"), null, configFacilityDelivery, []);
	}
	var initEvents = function(){
		favorDistributorPartyDDB.getGrid().rowSelectListener(function(rowData){
	    	var __customerId = rowData['partyId'];
	    	facilityConsignDDB.updateSource("jqxGeneralServicer?sname=JQGetListFacilityByAdmin&partyId=" + __customerId, null, function(){
	    		facilityConsignDDB.selectItem(null, 0);
	    	});
	    });
		facilityConsignDDB.getGrid().bindingCompleteListener(function() {
			facilityConsignDDB.selectItem(null, 0);
		});
		$('#shipFromFacilityConsign').on('change', function (event) {
			var checked = event.args.checked;
			if (checked) {
				$(".facility-consign-container").show(600);
			} else {
				$(".facility-consign-container").hide(600);
			}
		});
	};
	var initValidateForm = function(){
		var extendRules = [
				{input: '#facilityConsignId', message: uiLabelMap.BSRequired, action: 'close', rule: 
					function (input, commit) {
						var isShipFromFacilityConsign = $('#shipFromFacilityConsign').val();
						var value = $(input).val();
						if (isShipFromFacilityConsign && !OlbCore.isNotEmpty(value)) {
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
	return {
		init: init,
	};
}());