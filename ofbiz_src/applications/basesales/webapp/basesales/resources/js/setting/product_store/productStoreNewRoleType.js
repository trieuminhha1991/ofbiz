$(function(){
	OlbSettingProductStoreNewRole.init();
});

var OlbSettingProductStoreNewRole = (function(){
	var partyIdAddDDB;
	var roleTypeIdAddCBB;
	var validatorVAL;
	
	var init = (function(){
		initElement();
		initElementComplex();
		initValidatorForm();
		initEvent();
	});
	
	var initElement = (function(){
		jOlbUtil.dateTimeInput.create("#wn_psrole_fromDate", {width: '100%', allowNullDate: false, showFooter: true});
		jOlbUtil.dateTimeInput.create("#wn_psrole_thruDate", {width: '100%', allowNullDate: true, value: null, showFooter: true});
		
		jOlbUtil.windowPopup.create($("#popupProdStoreNewRole"), {width: 500, height: 250, cancelButton: $("#alterCancel1")});
		
		$('#wn_psrole_fromDate').val(new Date());
	});
	
	var initElementComplex = (function(){
		var configa = {
			useUrl: true,
			root: 'results',
			widthButton: '100%',
			heightButton: '28px',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}, {name: 'groupName', type: 'string'}],
			columns: [
		          {text: uiLabelMap.BSPartyId, datafield: 'partyCode', width: '30%'},
		          {text: uiLabelMap.BSFullName, datafield: 'fullName', width: '70%'},
			],
			url: 'JQGetListParties',
			useUtilFunc: true,
			
			key: 'partyId',
			keyCode: 'partyCode',
			description: ['fullName'],
			autoCloseDropDown: true,
		};
		partyIdAddDDB = new OlbDropDownButton($("#wn_psrole_partyId"), $("#wn_psrole_partyGrid"), null, configa, []);
		
		var configRoleType = {
			width: "100%",
			height: 30,
			key: "roleTypeId",
			value: "description",
			displayDetail: true,
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: false,
			datafields: [
	            {name: 'roleTypeId'},
	            {name: 'description'}
	        ],
	        autoComplete: true,
			searchMode: 'containsignorecase',
		};
		roleTypeIdAddCBB = new OlbComboBox($("#wn_psrole_roleTypeId"), roleTypeData, configRoleType, []);
	});
	
	var initEvent = (function(){
		$('#alterSave1').click(function(){
			if (!validatorVAL.validate()) return false;
			
			var fromDateA = $('#wn_psrole_fromDate').jqxDateTimeInput('val', 'date');
			var thruDateA = $('#wn_psrole_thruDate').jqxDateTimeInput('val', 'date');
			var row = {};
			row = {
				"partyId": partyIdAddDDB.getValue(),
				"roleTypeId": roleTypeIdAddCBB.getValue(),
				"productStoreId": productStoreId,
				"fromDate": fromDateA,
				"thruDate": thruDateA,
			};
			$("#jqxProdStoreRole").jqxGrid('addRow', null, row, "first");
			$("#jqxProdStoreRole").jqxGrid('clearSelection');                        
			$("#jqxProdStoreRole").jqxGrid('selectRow', 0);  
			$("#popupProdStoreNewRole").jqxWindow('close');
		});
		
		$("#popupProdStoreNewRole").on('open', function(){
			$('#wn_psrole_fromDate').val(new Date());
		});
		$('#popupProdStoreNewRole').on('close',function(){
			$('#ProductStoreRoleForm').jqxValidator('hide');
			$('#jqxProdStoreRole').jqxGrid('refresh');
			partyIdAddDDB.clearAll();
			roleTypeIdAddCBB.clearAll();
			$('#wn_psrole_fromDate').jqxDateTimeInput({todayString: 'Today'});
			$('#wn_psrole_thruDate').val(null);
		});
	});
	
	var initValidatorForm = function(){
		var mapRules = [
            {input: '#wn_psrole_partyId', type: 'validObjectNotNull', objType: 'dropDownButton'},
            {input: '#wn_psrole_roleTypeId', type: 'validObjectNotNull', objType: 'comboBox'},
            {input: '#wn_psrole_fromDate, #wn_psrole_thruDate', type: 'validCompareTwoDate', paramId1 : "wn_psrole_fromDate", paramId2 : "wn_psrole_thruDate"},
        ];
		var extendRules = [];
		validatorVAL = new OlbValidator($('#ProductStoreRoleForm'), mapRules, extendRules);
	};
	
	return{
		init: init,
	}
}());