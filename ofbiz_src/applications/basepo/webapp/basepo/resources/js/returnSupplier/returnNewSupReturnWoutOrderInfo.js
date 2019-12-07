$(function(){
		OlbReturnWithoutOrderInfo.init();
	});
	var OlbReturnWithoutOrderInfo = (function(){
		var supplierDDB;
		var currencyUomDDB;
		var destinationFacilityDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			
		};
		var initElementComplex = function(){
			var configSupplier = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'groupName', type: 'string'}],
				columns: [
					{text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: '30%'},
					{text: uiLabelMap.POSupplierName, datafield: 'groupName', width: '70%'}
				],
				url: 'jqGetListPartySupplier',
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['groupName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
				dropDownHorizontalAlignment: 'left'
			};
			supplierDDB = new OlbDropDownButton($("#supplierId"), $("#supplierGrid"), null, configSupplier, null);
			
			var configCurrencyUom = {
				//widthButton: '100%',
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'uomId', type: 'string'}, 
					{name: 'description', type: 'string'}, 
				],
				columns: [
					{text: uiLabelMap.BSId, datafield: 'uomId', width: '30%'},
					{text: uiLabelMap.Description, datafield: 'description', width: '70%'},
				],
				useUrl: true,
				root: 'results',
				url: '', //JQGetSupplierCurrencyUom
				useUtilFunc: true,
				selectedIndex: 0,
				key: 'uomId', 
				description: ['description'], 
				autoCloseDropDown: false,
				filterable: true,
				sortable: true,
			};
			currencyUomDDB = new OlbDropDownButton($("#currencyUomId"), $("#currencyUomGrid"), null, configCurrencyUom, null);
			
			var configDestinationFacility = {
				widthButton: '100%',
				filterable: true,
				pageable: true,
				showfilterrow: false,
				dropDownHorizontalAlignment: 'left',
				datafields: [
					{name: 'facilityId', type: 'string'}, 
					{name: 'facilityCode', type: 'string'}, 
					{name: 'facilityName', type: 'string'}
					],
				columns: [
					{text: uiLabelMap.FacilityId, datafield: 'facilityCode', width: '40%'},
					{text: uiLabelMap.FacilityName, datafield: 'facilityName'}
				],
				useUtilFunc: true,
				useUrl: true,
				root: 'results',
				url: 'JQGetListFacilityByOrg',
				
				//selectedIndex: 0,
				key: 'facilityId',
				description: ['facilityName'],
			};
			destinationFacilityDDB = new OlbDropDownButton($("#destinationFacilityId"), $("#destinationFacilityGrid"), null, configDestinationFacility, []);
		};
		var initEvent = function(){
			supplierDDB.getGrid().rowSelectListener(function(rowData){
		    	var supplierId = rowData['partyId'];
				currencyUomDDB.updateSource("jqxGeneralServicer?sname=JQGetSupplierCurrencyUom&partyId="+supplierId, null, function(){
					currencyUomDDB.selectItem(null, 0);
				});
		    });
			destinationFacilityDDB.getGrid().rowSelectListener(function(rowData){
		    	var facilityId = rowData['facilityId'];
		    	ReturnProductWoutOrderObj.reloadListProduct(facilityId);
		    });

            currencyUomDDB.getGrid().rowSelectListener(function(rowData) {
                var currencyUomId = rowData['currencyUomId'];
                ReturnProductWoutOrderObj.reloadListProduct(currencyUomId);
            });
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#supplierId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#destinationFacilityId', type: 'validObjectNotNull', objType: 'dropDownButton'},
	            ];
			validatorVAL = new OlbValidator($('#newReturnSupplierWoutOrder'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		var getValidator = function() {
			return validatorVAL;
		};
		var getValue = function(){
			return {
				supplierId: supplierDDB.getValue(),
				//originContactMechId: null,
				destinationFacilityId: destinationFacilityDDB.getValue(),
				description: $("#description").val(),
				currencyUomId: currencyUomDDB.getValue(),
			}
		};
		var getObj = function(){
			return {
				destinationFacilityDDB: destinationFacilityDDB,
                currencyUomDDB: currencyUomDDB,
                supplierDDB: supplierDDB
			}
		};
		return {
			init: init,
			getValidator: getValidator,
			getValue: getValue,
			getObj: getObj,
		};
	}());