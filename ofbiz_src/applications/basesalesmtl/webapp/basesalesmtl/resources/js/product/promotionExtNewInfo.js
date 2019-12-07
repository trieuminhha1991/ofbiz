$(function(){
	OlbPromoNewInfo.init();
});
var OlbPromoNewInfo = (function(){
	var validatorVAL;
	
	var init = function(){
		initElement();
		initElementComplex();
		initValidateForm();
	};
	var initElement = function(){
		jOlbUtil.input.create("#productPromoId", {maxLength:20});
		jOlbUtil.input.create("#promoName", {maxLength:100});
		jOlbUtil.input.create("#promoName", {maxLength:100});
		jOlbUtil.dateTimeInput.create("#fromDate", {width: '100%', allowNullDate: true, value: null});
		jOlbUtil.dateTimeInput.create("#thruDate", {width: '100%', allowNullDate: true, value: null});
		jOlbUtil.numberInput.create("#useLimitPerPromotion", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		
		$('#promoName').jqxInput('focus');
		if (typeof(dataPromoNew.useLimitPerPromotion) != "undefined") jOlbUtil.numberInput.val("#useLimitPerPromotion", dataPromoNew.useLimitPerPromotion);
		else jOlbUtil.numberInput.clear("#useLimitPerPromotion");
		
		if (typeof(dataPromoNew.productPromoId) != "undefined") {
			$('#productPromoId').jqxInput('val', dataPromoNew.productPromoId);
			$('#productPromoId').jqxInput({disabled: true});
			if (typeof(dataPromoNew.productPromoName) != "undefined") {
				$('#promoName').jqxInput('val', dataPromoNew.productPromoName);
			}
		}
		if (typeof(dataPromoNew.fromDate) != "undefined") $('#fromDate').jqxDateTimeInput('setDate', dataPromoNew.fromDate);
		if (typeof(dataPromoNew.thruDate) != "undefined") $('#thruDate').jqxDateTimeInput('setDate', dataPromoNew.thruDate);
	};
	var initElementComplex = function(){
		var configProductStore = {
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductStorePriceRule&pagesize=0',
			key: 'productStoreId',
			value: 'storeName',
			autoDropDownHeight: false,
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
		
		var configShowToCustomer = {
			width: "100%",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		}
		new OlbDropDownList($('#showToCustomer'), dataYesNoChoose, configShowToCustomer, [dataPromoNew.showToCustomer]);
		
		var configProductPromoType = {
			width: "100%",
			key: "productPromoTypeId",
			value: "description",
			autoDropDownHeight: true,
			displayDetail: false,
			placeHolder: uiLabelMap.BSClickToChoose,
		}
		new OlbDropDownList($('#productPromoTypeId'), promoExtTypeData, configProductPromoType, [dataPromoNew.productPromoTypeId]);
	};
	var initValidateForm = function(){
		var mapRules = [
                {input: '#productPromoId', type: 'validCannotSpecialCharactor'},
				{input: '#promoName', type: 'validInputNotNull'},
				{input: '#productStoreIds', type: 'validObjectNotNull', objType: 'comboBoxMulti'},
				{input: '#fromDate', type: 'validDateTimeInputNotNull'},
				{input: '#fromDate', type: 'validDateCompareToday'},
				{input: '#fromDate, #thruDate', type: 'validCompareTwoDate', paramId1 : "fromDate", paramId2 : "thruDate"},
			];
		validatorVAL = new OlbValidator($('#initPromotionEntry'), mapRules, null, {scroll: true});
	};
	var clearNumberInput = function(element){
		jOlbUtil.numberInput.clear(element);
	};
	var getValidator = function(){
		return validatorVAL;
	}
	return {
		init: init,
		clearNumberInput: clearNumberInput,
		getValidator: getValidator,
	};
}());