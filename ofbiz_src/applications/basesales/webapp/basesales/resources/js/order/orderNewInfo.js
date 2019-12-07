var isFirstLoad = true;

var customerDDB;
var agreementDDB;
var productStoreDDL;
var orderPriorityDDL;

$(function(){
	OlbOrderInfo.init();
});
var OlbOrderInfo = (function() {
	var validatorVAL;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		jOlbUtil.input.create("#orderId");
		jOlbUtil.input.create("#externalId");
		jOlbUtil.dateTimeInput.create("#desiredDeliveryDate", {width: '100%', allowNullDate: true, value: null, showFooter: true});
		jOlbUtil.dateTimeInput.create("#shipAfterDate", {width: '100%', allowNullDate: true, value: null, showFooter: true});
		jOlbUtil.dateTimeInput.create("#shipBeforeDate", {width: '100%', allowNullDate: true, value: null, showFooter: true});
		
		if (desiredDeliveryDate != null) $("#desiredDeliveryDate").jqxDateTimeInput('setDate', desiredDeliveryDate);
		if (defaultShipAfterDate != null) $("#shipAfterDate").jqxDateTimeInput('setDate', defaultShipAfterDate);
		if (defaultShipBeforeDate != null) $("#shipBeforeDate").jqxDateTimeInput('setDate', defaultShipBeforeDate);
	};
	var initElementComplex = function() {
		var configProductStore = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductStoreBySeller&ckorg=Y',
			key: 'productStoreId',
			value: 'storeName',
			autoDropDownHeight: true
		}
		productStoreDDL = new OlbDropDownList($("#productStoreId"), null, configProductStore, [defaultProductStoreId]);
		
		var configOrderPriority = {
				width: '100%',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true
		}
		orderPriorityDDL = new OlbDropDownList($("#orderPriorityId"), orderPriorityData, configOrderPriority, ["PRIO_MEDIUM"]);
		
		var configCustomer = {
			useUrl: true,
			root: 'results',
			widthButton: '100%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'partyName', type: 'string'}, 
			             {name: 'telecomId', type: 'string'}, {name: 'telecomName', type: 'string'}, {name: 'postalAddressName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSCustomerId, datafield: 'partyCode', width: '24%'},
				{text: uiLabelMap.BSFullName, datafield: 'partyName', width: '30%'},
				{text: uiLabelMap.BSPhone, datafield: 'telecomName', width: '20%'},
				{text: uiLabelMap.BSAddress, datafield: 'postalAddressName', width: '60%'},
			],
			url: 'JQGetCustomersByProductStore&productStoreId=' + defaultProductStoreId,
			useUtilFunc: true,
			
			key: 'partyId',
			keyCode: 'partyCode',
			description: ['partyName'],
			autoCloseDropDown: true,
			filterable: true,
			sortable: true,
		};
		customerDDB = new OlbDropDownButton($("#customerId"), $("#customerGrid"), null, configCustomer, []);
		
		var urlAgreement = 'JQGetAgreementsByCustomer';
		if (defaultPartyId != null) urlAgreement += '&partyId=' + defaultPartyId;
		var configAgreement = {
			useUrl: true,
			root: 'results',
			widthButton: '100%',
			datafields: [
				{name: 'agreementId', type: 'string'}, {name: 'agreementCode', type: 'string'}, 
				{name: 'partyIdFrom', type: 'string'}, {name: 'partyIdTo', type: 'string'},
				{name: 'fromDate', type: 'date', other: 'Timestamp'}, {name: 'thruDate', type: 'date', other: 'Timestamp'}
			],
			columns: [
				{text: uiLabelMap.BSAgreementId, datafield: 'agreementId', width: '17%'},
				{text: uiLabelMap.BSAgreementCode, datafield: 'agreementCode', width: '24%'},
				{text: uiLabelMap.BSCustomerId, datafield: 'partyIdFrom', width: '25%'},
				{text: uiLabelMap.BSFromDate, datafield: 'fromDate', width: '17%', cellsformat: 'dd/MM/yyyy'},
				{text: uiLabelMap.BSThruDate, datafield: 'thruDate', width: '17%', cellsformat: 'dd/MM/yyyy'},
			],
			useUtilFunc: true,
			url: urlAgreement,
			
			selectedIndex: 0,
			key: 'agreementId',
			description: function(rowData){
				if (rowData) {
					var descriptionValue = rowData['agreementCode'];
					return descriptionValue;
				}
			},
			autoCloseDropDown: true,
			filterable: true,
			sortable: true,
		};
		agreementDDB = new OlbDropDownButton($("#agreementId"), $("#agreementGrid"), null, configAgreement, [currentAgreementId]);
	};
	var initEvents = function() {
		productStoreDDL.selectListener(function(itemData){
			var productStoreId = itemData.value;
			
			customerDDB.updateSource("jqxGeneralServicer?sname=JQGetCustomersByProductStore&productStoreId="+productStoreId);
			customerIdMain = "";
			customerRowDataMain = {};
			
			if (typeof(OlbOrderDropShip) != 'undefined') {
				// load list supplier
				shipGroupFacilityDDB.updateSource("jqxGeneralServicer?sname=JQGetFacilitiesByProductStore&productStoreId="+productStoreId);
			}
		});
	    customerDDB.getGrid().bindingCompleteListener(function() {
	    	if (defaultPartyId) {
	    		//customerDDB.putConfig("defaultValue", defaultPartyId);
	    		//customerDDB.putConfig("defaultLabel", defaultPartyFullName);
	    		customerDDB.selectItem([defaultPartyId]);
	    	}
	    }, true);
	};
	var initValidateForm = function(){
		var extendRules = [
				{input: '#desiredDeliveryDate, #shipAfterDate, #shipBeforeDate', message: validFieldRequire, action: 'valueChanged', 
					rule: function(input, commit){
						return OlbValidatorUtil.validElement(input, commit, 'validDeliveryDateNotNull');
					}
				},
				{input: '#desiredDeliveryDate', message: uiLabelMap.BSDesiredDeliveryDateMustBetweenStartDateAndFinishDate, action: 'valueChanged', 
					rule: function(input, commit){
						return OlbValidatorUtil.validElement(input, commit, 'validCompareDeliveryDateBetweenStartDateAndFinishDate');
					}
				},
				{input: '#shipAfterDate, #shipBeforeDate', message: uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate, action: 'valueChanged', 
					rule: function(input, commit){
						return OlbValidatorUtil.validElement(input, commit, 'validCompareStartDateAndFinishDate');
					}
				}
           ];
		var mapRules = [
	            {input: '#orderId', type: 'validCannotSpecialCharactor'},
				{input: '#customerId', type: 'validObjectNotNull', objType: 'dropDownButton'},
				{input: '#desiredDeliveryDate', type: 'validDateTimeCompareToday'},
				{input: '#shipAfterDate', type: 'validDateTimeCompareToday'},
				{input: '#shipBeforeDate', type: 'validDateTimeCompareToday'},
            ];
		validatorVAL = new OlbValidator($('#initOrderEntry'), mapRules, extendRules, {position: 'bottom', scroll: true});
	};
	var getValidator = function() {
		return validatorVAL;
	};
	return {
		init: init,
		getValidator: getValidator,
	}
}());