$(function(){
	OlbLoyaltyNewInfo.init();
});
var OlbLoyaltyNewInfo = (function(){
	var validatorVAL;
	
	var init = function(){
		initElement();
		initElementComplex();
		initValidateForm();
	};
	var initElement = function(){
		jOlbUtil.input.create("#loyaltyId", {maxLength:20});
		jOlbUtil.input.create("#loyaltyName", {maxLength:100});
		jOlbUtil.dateTimeInput.create("#fromDate", {width: '100%', allowNullDate: true, value: null});
		jOlbUtil.dateTimeInput.create("#thruDate", {width: '100%', allowNullDate: true, value: null});
		
		/*
		jOlbUtil.numberInput.create("#useLimitPerOrder", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		jOlbUtil.numberInput.create("#useLimitPerCustomer", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		jOlbUtil.numberInput.create("#useLimitPerPromotion", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		 */
		
		$('#loyaltyName').jqxInput('focus');
		/*
		if (typeof(dataLoyaltyNew.useLimitPerOrder) != "undefined") jOlbUtil.numberInput.val("#useLimitPerOrder", dataLoyaltyNew.useLimitPerOrder);
		else jOlbUtil.numberInput.clear("#useLimitPerOrder");
		if (typeof(dataLoyaltyNew.useLimitPerCustomer) != "undefined") jOlbUtil.numberInput.val("#useLimitPerCustomer", dataLoyaltyNew.useLimitPerCustomer);
		else jOlbUtil.numberInput.clear("#useLimitPerCustomer");
		if (typeof(dataLoyaltyNew.useLimitPerPromotion) != "undefined") jOlbUtil.numberInput.val("#useLimitPerPromotion", dataLoyaltyNew.useLimitPerPromotion);
		else jOlbUtil.numberInput.clear("#useLimitPerPromotion");
		 */
		
		if (typeof(dataLoyaltyNew.loyaltyId) != "undefined") {
			$('#loyaltyId').jqxInput('val', dataLoyaltyNew.loyaltyId);
			$('#loyaltyId').jqxInput({disabled: true});
			if (typeof(dataLoyaltyNew.loyaltyName) != "undefined") {
				$('#loyaltyName').jqxInput('val', dataLoyaltyNew.loyaltyName);
			}
			if (typeof(dataLoyaltyNew.loyaltyText) != "undefined") {
				$('#loyaltyText').val(dataLoyaltyNew.loyaltyText);
			}
		}
		if (typeof(dataLoyaltyNew.fromDate) != "undefined") $('#fromDate').jqxDateTimeInput('setDate', dataLoyaltyNew.fromDate);
		if (typeof(dataLoyaltyNew.thruDate) != "undefined") $('#thruDate').jqxDateTimeInput('setDate', dataLoyaltyNew.thruDate);
	};
	var initElementComplex = function(){
		var configProductStore = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductStorePriceRule',
			key: 'productStoreId',
			value: 'storeName',
			autoDropDownHeight: true,
			multiSelect: true,
		}
		new OlbComboBox($("#productStoreIds"), null, configProductStore, listProductStoreSelected);
		
		var configRoleType = {
			width: '100%',
			key: 'roleTypeId',
			value: 'descriptionSearch',
			placeHolder: uiLabelMap.BSClickToChoose,
			dropDownHeight: 200,
			autoDropDownHeight: false,
			displayDetail: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			multiSelect: true,
		};
		new OlbComboBox($("#roleTypeIds"), roleTypeData, configRoleType, listRoleTypeSelected);
		
		var configLoyaltyType = {
			width: "99%",
    		key: "enumId",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		};
		new OlbDropDownList($('#loyaltyTypeId'), loyaltyTypeData, configLoyaltyType, [dataLoyaltyNew.loyaltyTypeId]);
		
		/*var configShowToCustomer = {
			width: "100%",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		}
		new OlbDropDownList($('#showToCustomer'), dataYesNoChoose, configShowToCustomer, [dataLoyaltyNew.showToCustomer]);
		var configRequireCode = {
			width: "100%",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		}
		new OlbDropDownList($('#requireCode'), dataYesNoChoose, configRequireCode, [dataLoyaltyNew.requireCode]);*/
	};
	var initValidateForm = function(){
		var mapRules = [
                {input: '#loyaltyId', type: 'validCannotSpecialCharactor'},
				{input: '#loyaltyName', type: 'validInputNotNull'},
				{input: '#loyaltyTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#productStoreIds', type: 'validObjectNotNull', objType: 'comboBoxMulti'},
				{input: '#fromDate', type: 'validDateTimeInputNotNull'},
				{input: '#fromDate', type: 'validDateCompareToday'},
				{input: '#fromDate, #thruDate', type: 'validCompareTwoDate', paramId1 : "fromDate", paramId2 : "thruDate"},
			];
		var extendRules = [];
		validatorVAL = new OlbValidator($('#initLoyaltyEntry'), mapRules, extendRules, {position: 'bottom', scroll: true});
	};
	var clearNumberInput = function(element){
		jOlbUtil.numberInput.clear(element);
	};
	var getObj = function(){
		return {
			"validatorVAL": validatorVAL
		};
	}
	return {
		init: init,
		clearNumberInput: clearNumberInput,
		getObj: getObj,
	};
}());