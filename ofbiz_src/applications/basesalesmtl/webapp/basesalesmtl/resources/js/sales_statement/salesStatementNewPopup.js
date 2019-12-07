var organizationPartyDDB;
//var internalPartyDDB;
var salesStatementTypeDDL;

$(function(){
	OlbSalesStatementNew.init();
});
var OlbSalesStatementNew = (function(){
	var customTimePeriodDDB;
	var salesForecastDDB;
	var currencyUomIdCBB;
	var validatorVAL;

	var init = function(){
		initElement();
		initElementComplex();
		initEvent();
		initValidateForm();
	};
	var initElement = function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindowSalesStatementNew"), {maxWidth: 960, width: 960, height: 300, cancelButton: $("#wn_alterCancel")});
		jOlbUtil.input.create("#wn_salesStatementId");
		jOlbUtil.input.create("#wn_salesStatementName");
	};
	var initElementComplex = function(){
		var configOrganizationParty = {
			widthButton: '100%',
			filterable: true,
			pageable: true,
			showfilterrow: false,
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'groupName', type: 'string'}, {name: 'baseCurrencyUomId', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSOrganizationId, datafield: 'partyId', width: '20%'},
				{text: uiLabelMap.BSFullName, datafield: 'groupName'},
				{text: uiLabelMap.BSCurrencyUomId, datafield: 'baseCurrencyUomId', width: '18%'}
			],
			useUrl: true,
			root: 'results',
			url: 'JQListOrganizationPartyAcctg',
			useUtilFunc: true,
			
			key: 'partyId',
			description: ['groupName'],
			autoCloseDropDown: true,
			disabled: true,
		};
		organizationPartyDDB = new OlbDropDownButton($("#wn_organizationPartyId"), $("#wn_organizationPartyGrid"), null, configOrganizationParty, [defaultDataMap.currentOrganizationPartyId]);
		
		/*var configInternalParty = {
			widthButton: '100%',
			filterable: true,
			pageable: true,
			showfilterrow: false,
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSOrganizationId, datafield: 'partyCode', width: '20%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'},
			],
			useUrl: true,
			root: 'results',
			url: 'JQListPartyFullName',
			useUtilFunc: true,
			
			key: 'partyId',
			description: ['fullName'],
			autoCloseDropDown: true
		};
		internalPartyDDB = new OlbDropDownButton($("#wn_internalPartyId"), $("#wn_internalPartyGrid"), null, configInternalParty, [defaultDataMap.currentOrganizationPartyId]);
		*/
		var configCustomTimePeriod = {
			widthButton: '98%',
			width: '800px',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'right',
			datafields: [
				{name: 'customTimePeriodId', type: 'string'}, 
				{name: 'parentPeriodId', type: 'string'},
				{name: 'periodNum', type: 'string'},
				{name: 'periodName', type: 'string'},
				{name: 'fromDate', type: 'date', other: 'Timestamp'},
				{name: 'thruDate', type: 'date', other: 'Timestamp'},
			],
			columns: [
				{text: uiLabelMap.BSCustomTimePeriodId, datafield: 'customTimePeriodId', width: '25%'},
				{text: uiLabelMap.BSParentPeriodId, datafield: 'parentPeriodId', width: '18%'},
				{text: uiLabelMap.BSPeriodName, datafield: 'periodName'},
				{text: uiLabelMap.BSFromDate, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '18%',
					cellsrenderer: function(row, colum, value) {
						return '<span>' + jOlbUtil.dateTime.formatDate(value) + '</span>';
					}
				},
				{text: uiLabelMap.BSThruDate, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '18%',
					cellsrenderer: function(row, colum, value) {
						return '<span>' + jOlbUtil.dateTime.formatDate(value) + '</span>';
					}
				},
			],
			useUrl: true,
			root: 'results',
			useUtilFunc: true,
			url: 'JQListCustomTimePeriodSales&periodTypeId=SALES_MONTH',
			key: 'customTimePeriodId',
			description: ['periodName'],
			pageable: true,
			autoCloseDropDown: true,
			filterable: true,
			//url: 'jqxGeneralServicer?sname=JQListCustomTimePeriodSales&periodTypeId=SALES_MONTH',
			//parentKeyId: 'parentPeriodId',
			//gridType: 'jqxTreeGrid',
		};
		customTimePeriodDDB = new OlbDropDownButton($("#wn_customTimePeriodId"), $("#wn_customTimePeriodGrid"), null, configCustomTimePeriod, []);
		
		var configCurrencyUom = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: 'uomId',
			value: 'descriptionSearch',
			width: '98%',
			dropDownHeight: 200,
			autoDropDownHeight: false,
			displayDetail: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
		};
		currencyUomIdCBB = new OlbComboBox($("#wn_currencyUomId"), currencyUomData, configCurrencyUom, [defaultDataMap.currentCurrencyUomId]);
		
		var configSalesStatementType = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQListSalesStatementType',
			key: 'salesStatementTypeId',
			value: 'description',
			autoDropDownHeight: true,
			disabled: true
		}
		salesStatementTypeDDL = new OlbDropDownList($("#wn_salesStatementTypeId"), null, configSalesStatementType, [defaultDataMap.salesStatementTypeId]);
		
		var configSalesForecast = {
			widthButton: '98%',
			width: '600px',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			filterable: true,
			pageable: true,
			showfilterrow: true,
			searchId: 'salesForecastId',
			dropDownHorizontalAlignment: 'right',
			datafields: [
				{name: 'salesForecastId', type: 'string'},
				{name: 'parentSalesForecastId', type: 'string'},
				{name: 'organizationPartyId', type: 'string'},
				{name: 'internalPartyId', type: 'string'},
				{name: 'customTimePeriodId', type: 'string'},
				{name: 'periodName', type: 'string'},
			],
			columns: [
				{text: uiLabelMap.BSSalesForecastId, datafield: 'salesForecastId', width: '22%'},
				{text: uiLabelMap.BSOrganizationId, datafield: 'organizationPartyId', width: '22%'},
				{text: uiLabelMap.BSSalesCustomTimePeriodId, datafield: 'customTimePeriodId'},
				{text: uiLabelMap.BSSalesPeriodName, datafield: 'periodName'},
			],
			useUrl: true,
			root: 'results',
			url: 'JQListSalesForecast&pagesize=0',
			useUtilFunc: true,
			key: 'salesForecastId',
			description: ['organizationPartyId'],
			autoCloseDropDown: true,
		};
		salesForecastDDB = new OlbDropDownButton($("#wn_salesForecastId"), $("#wn_salesForecastGrid"), null, configSalesForecast, []);
	};
	var initEvent = function(){
	    $("#wn_alterSave").on('click', function(){
	    	if(!$('#alterpopupWindowSalesStatementNew').jqxValidator('validate')) return false;
	    	var wn_parentSalesStatementId = jOlbUtil.getAttrDataValue("wn_parentSalesStatementId");
	    	var wn_organizationPartyId = organizationPartyDDB.getValue();
	    	//var wn_internalPartyId = internalPartyDDB.getValue();
	    	var wn_customTimePeriodId = customTimePeriodDDB.getValue();
	    	var wn_salesStatementTypeId = salesStatementTypeDDL.getValue();
	    	var wn_salesForecastId = salesForecastDDB.getValue();
	    	var dataMap = {
	    		salesStatementId: $('#wn_salesStatementId').val(),
	    		salesStatementName: $('#wn_salesStatementName').val(),
	    		parentSalesStatementId: typeof(wn_parentSalesStatementId) != 'undefined' ? wn_parentSalesStatementId : '',
	    		organizationPartyId: typeof(wn_organizationPartyId) != 'undefined' ? wn_organizationPartyId : '',
	    		//internalPartyId: typeof(wn_internalPartyId) != 'undefined' ? wn_internalPartyId : '',
	    		customTimePeriodId: typeof(wn_customTimePeriodId) != 'undefined' ? wn_customTimePeriodId : '',
	    		currencyUomId: currencyUomIdCBB.getValue(),
	    		salesStatementTypeId: typeof(wn_salesStatementTypeId) != 'undefined' ? wn_salesStatementTypeId : '',
	    		salesForecastId: typeof(wn_salesForecastId) != 'undefined' ? wn_salesForecastId : '',
	    	};
	    	
	    	$.ajax({
				type: 'POST',
				url: 'createSalesStatementAjax',
				data: dataMap,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
								$("#btnPrevWizard").removeClass("disabled");
								$("#btnNextWizard").removeClass("disabled");
								
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'error'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	$("#alterpopupWindowSalesStatementNew").jqxWindow('close');
					        	
					        	resetWindowPopupCreate();
					        	return true;
							}, function(){
								resetWindowPopupCreate();
				        		return true;
							}
					);
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
	    });
	    
	    customTimePeriodDDB.getGrid().rowSelectListener(function(rowData){
	    	if (rowData) {
	    		var customTimePeriodId = rowData.customTimePeriodId;
	    		salesForecastDDB.updateSource("jqxGeneralServicer?sname=JQListSalesForecast&customTimePeriodId="+customTimePeriodId, null, function(){
	    			salesForecastDDB.selectItem(null, 0);
	    		});
	    	}
	    });
	};
	var resetWindowPopupCreate = function(){
		$("#wn_salesStatementId").jqxInput("val", null);
		$("#wn_salesStatementName").jqxInput("val", null);
		
		//organizationPartyDDB.clearAll(false);
		//organizationPartyDDB.selectItem(defaultDataMap.currentOrganizationPartyId);
		
		//internalPartyDDB.clearAll(true);
		//internalPartyDDB.selectItem(defaultDataMap.currentOrganizationPartyId);
	    
		customTimePeriodDDB.clearAll();
		currencyUomIdCBB.selectItem([defaultDataMap.currentCurrencyUomId]);
		
		$("#jqxSalesStatementList").jqxGrid('updateBoundData');
		
		salesForecastDDB.clearAll(false);
		salesForecastDDB.updateSource("jqxGeneralServicer?sname=JQListSalesForecast");
	};
	var initValidateForm = function(){
		var extendRules = [];
		var mapRules = [
	            {input: '#wn_salesStatementId', type: 'validCannotSpecialCharactor'},
				{input: '#wn_salesStatementTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#wn_organizationPartyId', type: 'validObjectNotNull', objType: 'dropDownButton'},
				//{input: '#wn_internalPartyId', type: 'validInputNotNull'},
				{input: '#wn_customTimePeriodId', type: 'validObjectNotNull', objType: 'dropDownButton'},
            ];
		validatorVAL = new OlbValidator($('#alterpopupWindowSalesStatementNew'), mapRules, extendRules, {position: 'bottom'});
	};
	return {
		init: init,
	};
}());