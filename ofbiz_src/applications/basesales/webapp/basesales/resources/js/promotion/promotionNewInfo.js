$(function(){
	OlbPromoNewInfo.init();
});
var OlbPromoNewInfo = (function(){
	var productStoreCBB;
	var roleTypeCBB;
	var productStoreGRID;
	var roleTypeGRID;
	var showToCustomerDDL;
	var requireCodeDDL;
	var productStoreDataArr = [];
	var roleTypeDataArr = [];
	var validatorVAL;
	
	var init = function(){
		initElement();
		initElementComplex();
		initValidateForm();
		initEvent();
		initEventRoleType();
	};
	var initElement = function(){
		jOlbUtil.input.create("#productPromoId", {maxLength:20});
		jOlbUtil.input.create("#promoName", {maxLength:100});
		jOlbUtil.dateTimeInput.create("#fromDate", {width: '100%', allowNullDate: true, value: null});
		jOlbUtil.dateTimeInput.create("#thruDate", {width: '100%', allowNullDate: true, value: null});
		jOlbUtil.numberInput.create("#useLimitPerOrder", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		jOlbUtil.numberInput.create("#useLimitPerCustomer", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		jOlbUtil.numberInput.create("#useLimitPerPromotion", {width: '100%', spinButtons:true, decimalDigits: 0, min: 0});
		
		$('#promoName').jqxInput('focus');
		if (typeof(dataPromoNew.useLimitPerOrder) != "undefined") jOlbUtil.numberInput.val("#useLimitPerOrder", dataPromoNew.useLimitPerOrder);
		else jOlbUtil.numberInput.clear("#useLimitPerOrder");
		if (typeof(dataPromoNew.useLimitPerCustomer) != "undefined") jOlbUtil.numberInput.val("#useLimitPerCustomer", dataPromoNew.useLimitPerCustomer);
		else jOlbUtil.numberInput.clear("#useLimitPerCustomer");
		if (typeof(dataPromoNew.useLimitPerPromotion) != "undefined") jOlbUtil.numberInput.val("#useLimitPerPromotion", dataPromoNew.useLimitPerPromotion);
		else jOlbUtil.numberInput.clear("#useLimitPerPromotion");
		
		if (typeof(dataPromoNew.productPromoId) != "undefined") {
			$('#productPromoId').jqxInput('val', dataPromoNew.productPromoId);
			$('#productPromoId').jqxInput({disabled: true});
		}
		if (typeof(dataPromoNew.productPromoName) != "undefined") {
			$('#promoName').jqxInput('val', dataPromoNew.productPromoName);
		}
		if (typeof(dataPromoNew.fromDate) != "undefined") $('#fromDate').jqxDateTimeInput('setDate', dataPromoNew.fromDate);
		if (typeof(dataPromoNew.thruDate) != "undefined") $('#thruDate').jqxDateTimeInput('setDate', dataPromoNew.thruDate);
		
		jOlbUtil.windowPopup.create($("#alterpopupWindowProductStore"), {width: 960, height: 450, cancelButton: $("#wn_ps_alterCancel")});
		jOlbUtil.windowPopup.create($("#alterpopupWindowRoleType"), {width: 960, height: 450, cancelButton: $("#wn_rt_alterCancel")});
	};
	var initElementComplex = function(){
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
		productStoreCBB = new OlbComboBox($("#productStoreIds"), null, configProductStore, listProductStoreSelected);
		
		var configGridProductStore = {
			datafields: [
				{name: 'productStoreId', type: 'string'}, 
				{name: 'storeName', type: 'string'},
				{name: 'title', type: 'string'},
				{name: 'subtitle', type: 'string'},
				{name: 'payToPartyId', type: 'string'},
				{name: 'defaultCurrencyUomId', type: 'string'},
				{name: 'salesMethodChannelEnumId', type: 'string'},
				{name: 'storeCreditAccountEnumId', type: 'string'},
			],
			columns: [
				{text: uiLabelMap.BSProductStoreId, dataField: 'productStoreId', width: '20%', editable: false,
					cellsrenderer: function(row, colum, value) {
				    	return "<span><a href='showProductStore?productStoreId=" + value + "' target='_blank'>" + value + "</a></span>";
				    }
				}, 
				{text: uiLabelMap.BSStoreName, dataField: 'storeName'},
				{text: uiLabelMap.BSPayToParty, dataField: 'payToPartyId', width: '16%'},
				{text: uiLabelMap.BSDefaultCurrencyUomId, dataField: 'defaultCurrencyUomId', width: '12%'},
			],
			width: '100%',
			height: 'auto',
			sortable: true,
			filterable: true,
			pageable: true,
			pagesize: 10,
			showfilterrow: true,
			useUtilFunc: false,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductStorePriceRule&pagesize=0',
			groupable: true,
			showdefaultloadelement:true,
			autoshowloadelement:true,
			selectionmode:'checkbox',
			virtualmode: false,
		};
		productStoreGRID = new OlbGrid($("#jqxgridProductStore"), null, configGridProductStore, []);
		
		var configRoleType = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: false,
			key: 'roleTypeId',
			value: 'descriptionSearch',
			dropDownHeight: 200,
			//autoDropDownHeight: true,
			displayDetail: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			multiSelect: true,
		}
		roleTypeCBB = new OlbComboBox($("#roleTypeIds"), roleTypeData, configRoleType, listRoleTypeSelected);
		var configGridRoleType = {
			datafields: [
				{name: 'roleTypeId', type: 'string'}, 
				{name: 'description', type: 'string'},
			],
			columns: [
				{text: uiLabelMap.BSRoleTypeId, dataField: 'roleTypeId', width: '25%'},
				{text: uiLabelMap.BSDescription, dataField: 'description'},
			],
			width: '100%',
			height: 'auto',
			sortable: true,
			filterable: true,
			pageable: true,
			pagesize: 10,
			showfilterrow: true,
			useUtilFunc: false,
			useUrl: false,
			groupable: true,
			showdefaultloadelement:true,
			autoshowloadelement:true,
			selectionmode:'checkbox',
			virtualmode: false,
		};
		roleTypeGRID = new OlbGrid($("#jqxgridRoleType"), roleTypeData, configGridRoleType, []);
		
		var configShowToCustomer = {
			width: "100%",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		}
		showToCustomerDDL = new OlbDropDownList($('#showToCustomer'), dataYesNoChoose, configShowToCustomer, [dataPromoNew.showToCustomer]);
		
		var configRequireCode = {
			width: "100%",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		placeHolder: uiLabelMap.BSClickToChoose,
		}
		requireCodeDDL = new OlbDropDownList($('#requireCode'), dataYesNoChoose, configRequireCode, [dataPromoNew.requireCode]);
	};
	var initEvent = function(){
		$("#btnShowProductStoreList").on("click", function(){
			$("#alterpopupWindowProductStore").jqxWindow("open");
		});
		productStoreGRID.on("bindingComplete", function(){
			productStoreDataArr = [];
		});
		productStoreGRID.on("rowselect", function(event){
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
				for (var i = 0; i < rowBoundIndex.length; i++) {
					processDataRowSelect(rowBoundIndex[i]);
				}
			} else {
				processDataRowSelect(rowBoundIndex);
			}
		});
		productStoreGRID.on("rowunselect", function(event){
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			if (rowBoundIndex == -9999) {
				productStoreGRID.clearSelection();
				productStoreDataArr = [];
			} else {
				var data = $("#jqxgridProductStore").jqxGrid("getrowdata", rowBoundIndex);
				if (data) {
					var index = productStoreDataArr.indexOf(data.productStoreId);
					if (index > -1) {
						productStoreDataArr.splice(index, 1);
					}
				}
			}
		});
		
		var processDataRowSelect = function(rowIndex){
			var rowData = $("#jqxgridProductStore").jqxGrid("getrowdata", rowIndex);
			if (rowData) {
				var idStr = rowData.productStoreId;
				var index = productStoreDataArr.indexOf(idStr);
				if (index < 0) {
					productStoreDataArr.push(idStr);
				}
			}
		};
		
		$("#wn_ps_alterSave").on("click", function(){
			if (productStoreCBB){
				productStoreCBB.clearAll();
				productStoreCBB.selectItem(productStoreDataArr);
			}
			$("#alterpopupWindowProductStore").jqxWindow("close");
		});
	};
	var initEventRoleType = function(){
		$("#btnShowRoleTypeList").on("click", function(){
			$("#alterpopupWindowRoleType").jqxWindow("open");
		});
		roleTypeGRID.on("bindingComplete", function(){
			roleTypeDataArr = [];
		});
		roleTypeGRID.on("rowselect", function(event){
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
				for (var i = 0; i < rowBoundIndex.length; i++) {
					processDataRowSelect(rowBoundIndex[i]);
				}
			} else {
				processDataRowSelect(rowBoundIndex);
			}
		});
		roleTypeGRID.on("rowunselect", function(event){
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			if (rowBoundIndex == -9999) {
				roleTypeGRID.clearSelection();
				roleTypeDataArr = [];
			} else {
				var data = $("#jqxgridRoleType").jqxGrid("getrowdata", rowBoundIndex);
				if (data) {
					var index = roleTypeDataArr.indexOf(data.roleTypeId);
					if (index > -1) {
						roleTypeDataArr.splice(index, 1);
					}
				}
			}
		});
		
		var processDataRowSelect = function(rowIndex){
			var rowData = $("#jqxgridRoleType").jqxGrid("getrowdata", rowIndex);
			if (rowData) {
				var idStr = rowData.roleTypeId;
				var index = roleTypeDataArr.indexOf(idStr);
				if (index < 0) {
					roleTypeDataArr.push(idStr);
				}
			}
		};
		
		$("#wn_rt_alterSave").on("click", function(){
			if (roleTypeCBB){
				roleTypeCBB.clearAll();
				roleTypeCBB.selectItem(roleTypeDataArr);
			}
			$("#alterpopupWindowRoleType").jqxWindow("close");
		});
	};
	var initValidateForm = function(){
		var mapRules = [
                {input: '#productPromoId', type: 'validCannotSpecialCharactor'},
				{input: '#promoName', type: 'validInputNotNull'},
				{input: '#productStoreIds', type: 'validObjectNotNull', objType: 'comboBoxMulti'},
				{input: '#fromDate', type: 'validDateTimeInputNotNull'},
				{input: '#fromDate', type: 'validDateCompareToday'},
				{input: '#fromDate, #thruDate', type: 'validCompareTwoDate', paramId1 : "fromDate", paramId2 : "thruDate"},
			]
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