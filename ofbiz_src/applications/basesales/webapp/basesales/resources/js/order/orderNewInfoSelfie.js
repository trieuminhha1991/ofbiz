var isFirstLoad = true;

var customerDDB;
var agreementDDB;
var productStoreDDL;

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
		jOlbUtil.input.create("#customerCode", {width: '98%', disabled: true});
		$("#customerId").val(defaultPartyId);
		$("#customerCode").jqxInput("val", defaultPartyCodeId);
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
			useUrl: false,
			url: 'jqxGeneralServicer?sname=JQGetListProductStoreBySeller&ckorg=Y',
			key: 'productStoreId',
			value: 'storeName',
			autoDropDownHeight: true,
		}
		productStoreDDL = new OlbDropDownList($("#productStoreId"), productStoreData, configProductStore, [defaultProductStoreId]);
		
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
		};
		agreementDDB = new OlbDropDownButton($("#agreementId"), $("#agreementGrid"), null, configAgreement, [currentAgreementId]);
	};
	var initEvents = function() {
		$("#productStoreId").on('select', function(event){
			var args = event.args;
			var item = args.item;
			if(item){
				var productStoreId = item.value;
				
				customerIdMain = $("#customerId").val();
				OlbOrderCheckout.updateCheckoutOptions(customerIdMain, productStoreId);
				OlbOrderCheckout.reloadSalesExecutiveGrid(customerIdMain);
				
				// load list product
				OlbGridUtil.updateSource($('#jqxgridSO'), "jqxGeneralServicer?sname=JQGetListProductByStoreOrCatalog&productStoreId=" + productStoreId + "&hasrequest=Y");
			}
		});
		
		productStoreDDL.selectListener(function(itemData){
			var productStoreId = itemData.value;
			
			/*customerDDB.updateSource("jqxGeneralServicer?sname=JQGetCustomersByProductStore&productStoreId="+productStoreId);*/
			customerIdMain = $("#customerId").val();
			OlbOrderCheckout.updateCheckoutOptions(customerIdMain, productStoreId);
			OlbOrderCheckout.reloadSalesExecutiveGrid(customerIdMain);
			
			if (typeof(OlbOrderDropShip) != 'undefined') {
				// load list supplier
				shipGroupFacilityDDB.updateSource("jqxGeneralServicer?sname=JQGetFacilitiesByProductStore&productStoreId="+productStoreId);
			}
			// load list product
			OlbGridUtil.updateSource($('#jqxgridSO'), "jqxGeneralServicer?sname=JQGetListProductByStoreOrCatalog&productStoreId=" + productStoreId + "&hasrequest=Y");
		});
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
				{input: '#customerCode', type: 'validInputNotNull'},
				{input: '#desiredDeliveryDate', type: 'validDateTimeCompareToday'},
				{input: '#shipAfterDate', type: 'validDateTimeCompareToday'},
				{input: '#shipBeforeDate', type: 'validDateTimeCompareToday'},
            ];
		validatorVAL = new OlbValidator($('#initOrderEntry'), mapRules, extendRules, {position: 'bottom', scroll: true});
	};
	var getValidator = function(){
		return validatorVAL;
	}
	return {
		init: init,
		getValidator: getValidator,
	}
}());