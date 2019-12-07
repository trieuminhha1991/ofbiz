$(function(){
	BillOfLading.init();
});
var BillOfLading = (function() {
	
	var validatorBILL;
	var gridShipping = $("#jqxGridPartyShipping");
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	
	var initInputs = function (){
		$("#txtdepartureDate").jqxDateTimeInput({width: 300, height: 25, theme: theme});
		$("#txtarrivalDate").jqxDateTimeInput({width: 300, height: 25, theme: theme});
		$("#txtBillNumber").jqxInput({width: 300, height: 25, theme: theme});
		$("#txtdepartureDate").jqxDateTimeInput("clear");
		$("#txtarrivalDate").jqxDateTimeInput("clear");
		
		$("#billDescription").jqxInput({ width: 300, height: 100});
		
		$("#shippingParty").jqxDropDownButton({width: 300, theme: theme}); 
		$('#shippingParty').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
	}
	
	var initElementComplex = function (){
		initShippingGrid(gridShipping);
	}
	
	var initEvents = function (){
		
		gridShipping.on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = gridShipping.jqxGrid('getrowdata', rowBoundIndex);
	        shippingSelected = {};
	        shippingSelected = $.extend({}, rowData);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.fullName +'</div>';
	        $('#shippingParty').jqxDropDownButton('setContent', dropDownContent);
	        $("#shippingParty").jqxDropDownButton('close');
		});
		
		gridShipping.on('bindingcomplete', function (event) {
			if (shippingSelected != null){
				var rows = gridShipping.jqxGrid('getrows');
				if (rows && rows.length > 0){
					for (var i in rows){
						if (rows[i].partyId == shippingSelected.partyId){
							var index = gridShipping.jqxGrid('getrowboundindexbyid', rows[i].uid);
							shippingSelected.jqxGrid('selectrow', index);
							break;
						}
					}
				}
			}
		});
	}
	
	var initShippingGrid = function(grid){
		var url = "jqGetPartyByRole&roleTypeId=SHIPPING_LINE";
		var datafield =  [
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'fullName', type: 'string'},
			];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				}
			},
			{text: uiLabelMap.CommonId, datafield: 'partyCode', width: '150',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			{text: uiLabelMap.CommonName, datafield: 'fullName', minwidth: '200',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			];
		
		var config = {
				width: 450, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				useUrl: true,
				url: url,                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initValidateForm = function (){
		var extendRules = [
//			{
//				input: '#containerTypeId', 
//				message: uiLabelMap.FieldRequired, 
//				action: 'blur', 
//				position: 'right',
//				rule: function (input) {
//					var x = $("#containerTypeId").jqxDropDownList('val');
//					if (x === undefined || x === null || x === ""){
//						return false;
//					}
//					return true;
//				}
//			},
		];
		var mapRules = [
				{input: '#txtBillNumber', type: 'validInputNotNull'},
				{input: '#txtdepartureDate', type: 'validInputNotNull'},
				{input: '#txtarrivalDate', type: 'validInputNotNull'},
				{input: '#txtarrivalDate', type: 'validDateTimeCompareToday'},
				{input: '#txtdepartureDate, #txtarrivalDate', type: 'validCompareTwoDate', paramId1 : "txtdepartureDate", paramId2 : "txtarrivalDate"},
		];
		validatorBILL = new OlbValidator($('#BillingForm'), mapRules, extendRules, {position: 'right'});
	}
	
	var getValidate = function (){
		if (validatorBILL){
			return validatorBILL.validate();
		}
		return true;
	}
	return {
		init: init,
		getValidate: getValidate,
	}
}());