$(function(){
	OlbProductStoreNewCatalog.init();
});

var OlbProductStoreNewCatalog = (function(){
	var prodCatalogDDL;
	var validatorVAL;
	
	var init = (function(){
		initElement();
		initDropDownList();
		initValidateForm();
		initEvent();
	});
	
	var initElement = (function(){
		jOlbUtil.dateTimeInput.create("#wn_pscata_fromDate", {width: '99%', showFooter: true, height:28});
		jOlbUtil.dateTimeInput.create("#wn_pscata_thruDate", {width: '99%', showFooter: true, height:28, allowNullDate: true});
		jOlbUtil.numberInput.create($("#wn_pscata_sequenceNum"), {width: '99%', spinButtons: false, digits: 3, inputMode: 'simple', decimalDigits: 0, min: 0, allowNull: true});
		
		$('#wn_pscata_fromDate').val(new Date());
		$('#wn_pscata_thruDate').val(null);
		$('#wn_pscata_sequenceNum').val(null);
		
		jOlbUtil.windowPopup.create($('#alterpopupWindowNewProdCatalog'), {width: 500, height: 260, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#wn_pscata_alterCancel")});
	});
	
	var initDropDownList = (function(){
		var configProdCatalog = {
			width: '98%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProdCatalog',
			key: 'prodCatalogId',
			value: 'catalogName',
			autoDropDownHeight: true
		}
		prodCatalogDDL = new OlbDropDownList($("#wn_pscata_prodCatalogId"), null, configProdCatalog, []);
	});
	
	var initValidateForm = function(){
		var extendRules = [];
   		var mapRules = [
	                {input: '#wn_pscata_prodCatalogId', type: 'validObjectNotNull', objType: 'dropDownList'},
	                {input: '#wn_pscata_fromDate', type: 'validDateTimeInputNotNull'},
	                {input: '#wn_pscata_fromDate', type: 'validDateCompareToday'},
	                {input: '#wn_pscata_fromDate, #wn_pscata_thruDate', type: 'validCompareTwoDate', paramId1 : "wn_pscata_fromDate", paramId2 : "wn_pscata_thruDate"},
                ];
   		validatorVAL = new OlbValidator($('#alterpopupWindowNewProdCatalog'), mapRules, extendRules, {position: 'bottom'});
	}
	
	var initEvent = function(){
		$('#wn_pscata_alterSave').click(function(){
			if (!validatorVAL.validate()) {
				return false;
			}
			var row = {
				productStoreId: productStoreId,
				prodCatalogId: $('#wn_pscata_prodCatalogId').val(),
				fromDate: $('#wn_pscata_fromDate').jqxDateTimeInput('val', 'date'),
				thruDate: $('#wn_pscata_thruDate').jqxDateTimeInput('val', 'date'),
				sequenceNum: $('#wn_pscata_sequenceNum').val()
			};
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			$("#jqxgrid").jqxGrid('clearSelection');                        
			//$("#jqxgrid").jqxGrid('selectRow', 0);  
			$("#alterpopupWindowNewProdCatalog").jqxWindow('close');
		});
		
		$('#alterpopupWindowNewProdCatalog').on('open', function(){
			$('#wn_pscata_fromDate').val(new Date());
		});
		
		$('#alterpopupWindowNewProdCatalog').on('close', function(){
			$('#jqxgrid').jqxGrid('refresh');
			prodCatalogDDL.clearAll();
			$('#wn_pscata_thruDate').val(null);
			$('#wn_pscata_sequenceNum').val(null);
			
			setTimeout(function(){validatorVAL.hide();}, 100);
		});
	};
	
	return {
		init: init,
	}
}());
