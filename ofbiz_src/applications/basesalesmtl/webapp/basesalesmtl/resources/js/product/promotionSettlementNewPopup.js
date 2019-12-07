var productPromoDDB;
var productPromoExtDDB;
//var promoSettlementTypeDDL;

$(function(){
	OlbPromoSettlementNew.init();
});
var OlbPromoSettlementNew = (function(){
	var validatorVAL;

	var init = function(){
		initElement();
		initElementComplex();
		initEvent();
		initValidateForm();
	};
	var initElement = function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindowSalesStatementNew"), {maxWidth: 960, width: 960, height: 300, cancelButton: $("#wn_alterCancel")});
		jOlbUtil.input.create("#wn_promoSettlementId");
		jOlbUtil.input.create("#wn_promoSettlementName");
		jOlbUtil.dateTimeInput.create("#wn_fromDate");
		jOlbUtil.dateTimeInput.create("#wn_thruDate");
	};
	var initElementComplex = function(){
		/*var configPromoSettlementType = {
			width: '100%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: false,
			key: 'typeId',
			value: 'description',
			autoDropDownHeight: true,
			selectedIndex: 0
		}
		promoSettlementTypeDDL = new OlbDropDownList($("#wn_promoSettlementTypeId"), promoExtTypeData, configPromoSettlementType, []);
		*/
		var configCustomer = {
			root: 'results',
			widthButton: '100%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'left',
			datafields: [{name: 'productPromoId', type: 'string'}, {name: 'promoName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSProductPromoId, datafield: 'productPromoId', width: '30%'},
				{text: uiLabelMap.BSPromoName, datafield: 'promoName', width: '70%'}
			],
			useUrl: true,
			url: 'JQListProductPromo&_statusId=PROMO_ACCEPTED',
			useUtilFunc: true,
			
			key: 'productPromoId',
			description: ['productPromoId'],
			autoCloseDropDown: true,
			filterable: true
		};
		productPromoDDB = new OlbDropDownButton($("#wn_productPromoId"), $("#wn_productPromoGrid"), null, configCustomer, []);
		
		var configProductPromoExt = {
			root: 'results',
			widthButton: '100%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'left',
			datafields: [{name: 'productPromoId', type: 'string'}, {name: 'promoName', type: 'string'}],
			columns: [
			          {text: uiLabelMap.BSProductPromoId, datafield: 'productPromoId', width: '30%'},
			          {text: uiLabelMap.BSPromoName, datafield: 'promoName', width: '70%'}
	        ],
	        useUrl: true,
	        url: 'JQListProductPromoExt&_statusId=PROMO_ACCEPTED',
	        useUtilFunc: true,
	        
	        key: 'productPromoId',
	        description: ['productPromoId'],
	        autoCloseDropDown: true,
	        filterable: true
		};
		productPromoExtDDB = new OlbDropDownButton($("#wn_productPromoExtId"), $("#wn_productPromoExtGrid"), null, configProductPromoExt, []);
		
	};
	var initEvent = function(){
	    $("#wn_alterSave").on('click', function(){
	    	if(!$('#alterpopupWindowSalesStatementNew').jqxValidator('validate')) return false;
	    	var wn_promoSettlementId = $('#wn_promoSettlementId').val();
	    	var wn_promoSettlementName = $('#wn_promoSettlementName').val();
	    	//var wn_promoSettlementTypeId = promoSettlementTypeDDL.getValue();
	    	var wn_productPromoId = productPromoDDB.getValue();
	    	var wn_productPromoExtId = productPromoExtDDB.getValue();
	    	
	    	var dataMap = {
	    		promoSettlementId: $('#wn_promoSettlementId').val(),
	    		promoSettlementName: $('#wn_promoSettlementName').val(),
	    		//promoSettlementTypeId: typeof(wn_promoSettlementTypeId) != 'undefined' ? wn_promoSettlementTypeId : '',
	    		productPromoId: wn_productPromoId,
	    		productPromoExtId: wn_productPromoExtId,
	    	};
	    	
	    	if (typeof($('#wn_fromDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#wn_fromDate').jqxDateTimeInput('getDate') != null) {
				dataMap['fromDate'] = $('#wn_fromDate').jqxDateTimeInput('getDate').getTime();
			}
	    	if (typeof($('#wn_thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#wn_thruDate').jqxDateTimeInput('getDate') != null) {
	    		dataMap['thruDate'] = $('#wn_thruDate').jqxDateTimeInput('getDate').getTime();
	    	}
	    	
	    	$.ajax({
				type: 'POST',
				url: 'createPromoSettlementAjax',
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
				        	$("#jqxPromotion").jqxGrid("updatebounddata");
				        	resetWindowPopupCreate();
				        	return true;
						}, function(){
							$("#jqxPromotion").jqxGrid("updatebounddata");
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
					$("#alterpopupWindowSalesStatementNew").jqxWindow('close');
				},
			});
	    });
	};
	var resetWindowPopupCreate = function(){
		$("#wn_promoSettlementId").jqxInput("val", null);
		$("#wn_promoSettlementName").jqxInput("val", null);
	};
	var openNewPopup = function(){
		$("#alterpopupWindowSalesStatementNew").jqxWindow("open");
	};
	var initValidateForm = function(){
		var extendRules = [];
		var mapRules = [
	            {input: '#wn_promoSettlementId', type: 'validCannotSpecialCharactor'},
				{input: '#wn_promoSettlementId', type: 'validInputNotNull'},
				/*{input: '#wn_productPromoId', type: 'validInputNotNull'},*/
				{input: '#wn_fromDate', type: 'validDateTimeInputNotNull'},
				{input: '#wn_thruDate', type: 'validDateTimeInputNotNull'},
				{input: '#wn_fromDate, #thruDate', type: 'validCompareTwoDate', paramId1 : "wn_fromDate", paramId2 : "wn_thruDate"},
            ];
		validatorVAL = new OlbValidator($('#alterpopupWindowSalesStatementNew'), mapRules, extendRules, {position: 'bottom'});
	};
	return {
		init: init,
		openNewPopup: openNewPopup,
	};
}());