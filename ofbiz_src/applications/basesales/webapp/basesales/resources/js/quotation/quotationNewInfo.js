$(function(){
	OlbQuotationInfo.init();
});
var OlbQuotationInfo = (function(){
	var productStoreGroupCBB;
	var productStoreCBB;
	var validatorVAL;
	var currencyUomIdCBB;
	var productQuotationTypeDDL;
	var partyDDB;
	var disableAll = false;
	
	var init = function(){
		if (updateMode && !copyMode && isQuotationEditSpecial) {
			disableAll = true;
		}
		
		initElement();
		initElementComplex();
		initEvent();
		initValidateForm();
		
		if (updateMode) {
			//partyDDB.selectItem(null, null, {"defaultValue": quotationSelected.partyIdApplyDefaultValue, "defaultCode": quotationSelected.partyIdApplyDefaultCode, "defaultLabel": quotationSelected.partyIdApplyDefaultLabel});
			partyDDB.selectItem([quotationSelected.partyIdApplyDefaultValue]);
		}
		if (disableAll) {
			$('#quotationName').jqxInput({disabled: true});
			$('#fromDate').jqxDateTimeInput({disabled: true});
			//$('#thruDate').jqxDateTimeInput({disabled: true});
			productQuotationTypeDDL.getListObj().jqxDropDownList({disabled: true});
		}
	};
	var initElement = function(){
		jOlbUtil.input.create("#productQuotationId", {maxLength: 20});
		jOlbUtil.input.create("#quotationName", {maxLength: 100});
		jOlbUtil.dateTimeInput.create("#fromDate", {width: '100%', allowNullDate: true, value: null});
		jOlbUtil.dateTimeInput.create("#thruDate", {width: '100%', allowNullDate: true, value: null});
		jOlbUtil.checkBox.create($("#isSelectAllProductStore"), {});
		
		if (updateMode) {
			$('#quotationName').jqxInput('val', quotationSelected.quotationName);
			if (!copyMode) {
				$('#productQuotationId').jqxInput('val', quotationSelected.productQuotationId);
				$('#productQuotationId').jqxInput({disabled: true});
				if (quotationSelected.fromDate){
					$('#fromDate').jqxDateTimeInput('setDate', quotationSelected.fromDate);
				}
				if (quotationSelected.thruDate){
					$('#thruDate').jqxDateTimeInput('setDate', quotationSelected.thruDate);
				}
			}
		}
	};
	var initElementComplex = function(){
		var configQuotationType = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: false,
			key: 'typeId',
			value: 'description',
			autoDropDownHeight: true
		}
		productQuotationTypeDDL = new OlbDropDownList($("#productQuotationTypeId"), productQuotationTypeData, configQuotationType, []);
		
		/*var configSalesChannel = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'enumId',
			value: 'description',
			autoDropDownHeight: true,
		}
		new OlbDropDownList($("#salesMethodChannelEnumId"), salesMethodChannelEnumData, configSalesChannel, [quotationSelected.salesMethodChannelEnumId]);
		*/
		var configProductStoreGroup = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductStoreGroup&pagesize=0',
			key: 'productStoreGroupId',
			value: 'productStoreGroupName',
			//autoDropDownHeight: true,
			dropDownHeight: 200,
			multiSelect: true,
		}
		if (disableAll) configProductStoreGroup.disabled = true;
		productStoreGroupCBB = new OlbComboBox($("#productStoreGroupIds"), null, configProductStoreGroup, listProductStoreGroupSelected);
		
		var configProductStore = {
				width: '100%',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProductStorePriceRule&pagesize=0',
				key: 'productStoreId',
				value: 'storeName',
				//autoDropDownHeight: true,
				dropDownHeight: 200,
				multiSelect: true,
		}
		if (disableAll) configProductStore.disabled = true;
		productStoreCBB = new OlbComboBox($("#productStoreIds"), null, configProductStore, listProductStoreSelected);
		
		var configCurrencyUom = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'uomId',
			value: 'descriptionSearch',
			width: '100%',
			dropDownHeight: 200,
			autoDropDownHeight: false,
			displayDetail: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
		};
		if (disableAll) configCurrencyUom.disabled = true;
		currencyUomIdCBB = new OlbComboBox($("#currencyUomId"), currencyUomData, configCurrencyUom, [quotationSelected.currencyUomId]);
		
		/*var configRoleType = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'roleTypeId',
			value: 'descriptionSearch',
			width: '100%',
			dropDownHeight: 200,
			autoDropDownHeight: false,
			displayDetail: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			multiSelect: true,
		};
		new OlbComboBox($("#roleTypeId"), roleTypeData, configRoleType, listRoleTypeSelected);*/
		
		var configParty = {
			useUrl: true,
			widthButton: '100%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSId, datafield: 'partyCode', width: '30%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			url: 'JQGetListPartyAndGroupCustomer',
			useUtilFunc: true,
			
			key: 'partyId',
			keyCode: 'partyCode',
			description: ['fullName'],
			autoCloseDropDown: true,
			filterable: true,
			dropDownHorizontalAlignment: 'right',
		};
		if (updateMode && !copyMode) {
			configParty.showClearButton = false;
			configParty.disabled = true;
		} else {
			configParty.showClearButton = true;
			configParty.disabled = false;
		}
		partyDDB = new OlbDropDownButton($("#partyId"), $("#partyGrid"), null, configParty, []);
	};
	var initEvent = function(){
		$("#isSelectAllProductStore").on("checked", function(){
			productStoreCBB.disable(true);
			validatorVAL.hide();
		});
		$("#isSelectAllProductStore").on("unchecked", function(){
			productStoreCBB.disable(false);
		});
		
		/* $("#salesMethodChannelEnumId").on("select", function(event){
			var args = event.args;
			var item = args.item;
			if(item){
				// load list product
				OlbGridUtil.updateSource($('#jqxgridProd'), "jqxGeneralServicer?sname=JQListProductAndTaxByCatalog&channelEnumId=" + item.value);
			}
		}); 
		OlbGridUtil.updateSource($('#jqxgridProd'), "jqxGeneralServicer?sname=JQListProductAndTaxByCatalog&channelEnumId=" + item.value); */
		/*$("#productStoreIds").on("change", function(event){
			productPricesMap = {};
			var quotationTypeId = productQuotationTypeDDL.getValue();
			if (quotationTypeId == "PROD_PRICE_FLAT") {
				updateGridProdItems();
			}
		});*/
		
		/*productQuotationTypeDDL.selectListener(function(itemData, index){
			var quotationTypeId = productQuotationTypeDDL.getValue();
			if (quotationTypeId == "PROD_PRICE_FOL") { // product price modify
				$("#qni-case1").show();
				$("#qni-case2").hide();
			} else if (quotationTypeId == "PROD_CAT_PRICE_FOD") { // category price modify
				$("#qni-case1").hide();
				$("#qni-case2").show();
			} else {
				// default is PROD_PRICE_FLAT product price override
				$("#qni-case1").show();
				$("#qni-case2").hide();
				updateGridProdItems();
			}
		});*/
		if (quotationSelected.productQuotationTypeId) {
			productQuotationTypeDDL.selectItem([quotationSelected.productQuotationTypeId]);
		}
	};
	/*var updateGridProdItems = function(){
		var items = productStoreCBB.getValue();
		var itemsStr = "";
		if (items) {
			for (var i = 0; i < items.length; i++) {
				itemsStr += "&productStoreIds=" + items[i];
			}
		}
		// load list product
		OlbGridUtil.updateSource($('#jqxgridProd'), "jqxGeneralServicer?sname=" + newUrlUpdateGridItems + itemsStr);
	};*/
	var initValidateForm = function(){
		var extendRules = [
				{input: '#productStoreIds, #productStoreGroupIds', message: uiLabelMap.validFieldRequire, action: 'close', 
					rule: function(input, commit){
						var storeGroupSelected = productStoreGroupCBB.getValue();
						if (storeGroupSelected != null && storeGroupSelected.length > 0) {
							return true;
						}
						if ($("#isSelectAllProductStore").val()) {
							return true;
						} else {
							var storeSelected = productStoreCBB.getValue();
							if (storeSelected != null && storeSelected.length > 0) {
								return true;
							}
						}
						return false;
					}
				},
        	];
		var mapRules = [
                {input: '#productQuotationId', type: 'validCannotSpecialCharactor'},
                {input: '#productQuotationTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#quotationName', type: 'validInputNotNull'},
				{input: '#currencyUomId', type: 'validObjectNotNull', objType: 'comboBox'},
				{input: '#fromDate', type: 'validDateTimeInputNotNull'},
				{input: '#fromDate, #thruDate', type: 'validCompareTwoDate', paramId1 : "fromDate", paramId2 : "thruDate"},
			];
		if (!updateMode || copyMode) {
			mapRules.push({input: '#fromDate', type: 'validDateCompareToday'});
		}
		validatorVAL = new OlbValidator($('#initQuotationEntry'), mapRules, extendRules, {scroll: true});
	};
	var getObj = function(){
		return {
			productStoreCBB: productStoreCBB,
			productStoreGroupCBB: productStoreGroupCBB,
			productQuotationTypeDDL: productQuotationTypeDDL,
			partyDDB: partyDDB,
		}
	};
	var getValidator = function(){
		return validatorVAL;
	};
	var getValues = function(){
		/*
		var roleTypeIds = [];
		var listRoleTypeData = $("#roleTypeId").jqxComboBox('getSelectedItems');
		if (OlbCore.isNotEmpty(listRoleTypeData)) {
			for (var i = 0; i < listRoleTypeData.length; i++) {
				var roleTypeItem = listRoleTypeData[i];
				roleTypeIds.push(roleTypeItem.value);
			}
		}
		*/
		
		var fromDate = $("#fromDate").jqxDateTimeInput('getDate') != null ? $("#fromDate").jqxDateTimeInput('getDate').getTime() : "";
		var thruDate = $("#thruDate").jqxDateTimeInput('getDate') != null ? $("#thruDate").jqxDateTimeInput('getDate').getTime() : "";
		//var salesMethodChannelEnumIdSelected = $("#salesMethodChannelEnumId").jqxDropDownList('getSelectedItem');
		var currencyUomIdSelected = $("#currencyUomId").jqxComboBox('getSelectedItem');
		var salesMethodChannelEnumId = "";
		var currencyUomId = "";
		//if (salesMethodChannelEnumIdSelected != null) salesMethodChannelEnumId = salesMethodChannelEnumIdSelected.value; 
		if (currencyUomIdSelected != null) currencyUomId = currencyUomIdSelected.value; 
		
		var dataSelectedFinal = [];
		var quotationTypeId = OlbQuotationInfo.getObj().productQuotationTypeDDL.getValue();
		if (quotationTypeId == "PROD_CAT_PRICE_FOD") {
			if (dataSelectedCate) {
				for (var i = 0; i < dataSelectedCate.length; i++) {
					var itemFinal = dataSelectedCate[i];
					var rowFinal = {
						productCategoryId: itemFinal.productCategoryId, 
						amount: itemFinal.amount, 
					};
					dataSelectedFinal.push(rowFinal);
				}
			}
		} else {
			if (dataSelected) {
				for (var i = 0; i < dataSelected.length; i++) {
					var itemFinal = dataSelected[i];
					var rowFinal = {
						productId: itemFinal.productId, 
						productCode: itemFinal.productCode,
						listPrice: itemFinal.listPrice, 
						listPriceVAT: itemFinal.listPriceVAT, 
						taxPercentage: itemFinal.taxPercentage, 
						quantityUomId: itemFinal.quantityUomId
					};
					dataSelectedFinal.push(rowFinal);
				}
			}
		}
		var isSelectAllProductStore = "N";
		if ($("#isSelectAllProductStore").val()) isSelectAllProductStore = "Y";
		
		var dataMap = {};
		dataMap.productList = JSON.stringify(dataSelectedFinal);
		//dataMap.roleTypeIds = JSON.stringify(roleTypeIds);
		dataMap.fromDate = fromDate;
		dataMap.thruDate = thruDate;
		dataMap.productQuotationId = $("#productQuotationId").val();
		dataMap.productQuotationTypeId = quotationTypeId;
		dataMap.quotationName = $("#quotationName").val();
		dataMap.salesMethodChannelEnumId = salesMethodChannelEnumId;
		dataMap.currencyUomId = currencyUomId;
		dataMap.description = $("#description").val();
		dataMap.productStoreIds = OlbQuotationInfo.getObj().productStoreCBB.getValue();
		dataMap.productStoreGroupIds = OlbQuotationInfo.getObj().productStoreGroupCBB.getValue();
		dataMap.isSelectAllProductStore = isSelectAllProductStore;
		dataMap.partyId = OlbQuotationInfo.getObj().partyDDB.getValue();
		return dataMap;
	};
	return {
		init: init,
		getObj: getObj,
		getValidator: getValidator,
		getValues: getValues,
	};
}());