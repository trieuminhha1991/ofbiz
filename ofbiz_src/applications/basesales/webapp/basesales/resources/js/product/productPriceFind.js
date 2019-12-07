$(function(){
	OlbProdPriceFind.init();
});
var OlbProdPriceFind = (function(){
	var salesMethodChannelEnumIdDDL;
	var partyDDB;
	var productStoreDDL;
	
	var init = function(){
		initComplexElement();
		initEvent();
	};
	var initComplexElement = function(){
		var configSalesChannel = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: false,
			key: 'enumId',
			value: 'description',
			autoDropDownHeight: true,
			disabled: true,
		}
		salesMethodChannelEnumIdDDL = new OlbDropDownList($("#salesMethodChannelEnumId"), salesMethodChannelEnumData, configSalesChannel, []);
		
		var configParty = {
			useUrl: true,
			widthButton: '100%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSPartyId, datafield: 'partyId', width: '30%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			url: 'JQListPartyFullName',
			useUtilFunc: true,
			
			key: 'partyId',
			keyCode: 'partyCode',
			description: ['fullName'],
			autoCloseDropDown: true,
			filterable: true
		};
		partyDDB = new OlbDropDownButton($("#partyId"), $("#partyGrid"), null, configParty, []);
		
		var configProductStore = {
			width: '100%',
			dropDownHeight: '200px',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductStorePriceRule&pagesize=0',
			key: 'productStoreId',
			value: 'storeName',
			autoDropDownHeight: false,
		}
		productStoreDDL = new OlbComboBox($("#productStoreId"), null, configProductStore, []);
	};
	var initEvent = function(){
		$("#btnFindProductPrice").on("click", function(){
			//var salesMethodChannelEnumId = salesMethodChannelEnumIdDDL.getValue();
			var partyId = partyDDB.getValue();
			var productStoreId = productStoreDDL.getValue();
			
			var otherParam = "";
			//if (OlbCore.isNotEmpty(salesMethodChannelEnumId)) otherParam += "&salesMethodChannelEnumId=" + salesMethodChannelEnumId;
			if (OlbCore.isNotEmpty(partyId)) otherParam += "&partyId=" + partyId;
			if (OlbCore.isNotEmpty(productStoreId)) otherParam += "&productStoreId=" + productStoreId;
			
			//if (salesMethodChannelEnumId != null) {
			productItemsOLBG.updateSource("jqxGeneralServicer?sname=" + urlSNameFindProdPrice + otherParam);
			//}
		});
	};
	var getObj = function() {
		return {
			partyDDB: partyDDB,
			productStoreDDL: productStoreDDL
		}
	};
	return {
		init: init,
		getObj: getObj
	};
}());