$(function(){
	OlbProductStoreNewShipment.init();
});

var OlbProductStoreNewShipment = (function(){
	var shipmentMethodTypeDDL;
	var validatorVAL;
	
	var init = (function(){
		initWindow();
		initInput();
		initElementComplex();
		initEvent();
		initValidateForm();
	});
	
	var initWindow = (function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindowShipmentMethodNew"), {width: 640, height: 200, cancelButton: $("#wn_alterCancel1")});
	});
	
	var initInput = (function(){
		jOlbUtil.numberInput.create($('#wn_sequenceNumber'), {width: '96%', spinButtons: true, decimalDigits: 0, inputMode: 'simple'});
	});
	
	var initElementComplex = (function(){
		var configShipmentMethodType = {
			width: '96%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: false,
			key: 'shipmentMethodTypeId',
			value: 'description',
			displayDetail: true,
			autoDropDownHeight: true,
			renderer: function (index, label, value) {
		        var valueStr = shipmentMethodTypeList[index].description + " [" + shipmentMethodTypeList[index].partyId + "/" + shipmentMethodTypeList[index].roleTypeId + "]";
		        return valueStr;
		    },
		}
		shipmentMethodTypeDDL = new OlbDropDownList($("#wn_shipmentMethodTypeId"), shipmentMethodTypeList, configShipmentMethodType, []);
	});
	
	var initEvent = function(){
		$('#wn_alterSave1').click(function(){
			if (!validatorVAL.validate()) {
				return false;
			}
			
			var index = $("#wn_shipmentMethodTypeId").jqxDropDownList('getSelectedIndex'); 
			var row = {};
			row = {
				productStoreId : productStoreId2,
				shipmentMethodTypeId : $('#wn_shipmentMethodTypeId').val(),
				partyId: shipmentMethodTypeList[index].partyId,
				roleTypeId: shipmentMethodTypeList[index].roleTypeId,
				sequenceNumber : $('#wn_sequenceNumber').val(),
			};
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			$("#jqxgrid").jqxGrid('clearSelection');                        
			$("#jqxgrid").jqxGrid('selectRow', 0);  
			$("#alterpopupWindowShipmentMethodNew").jqxWindow('close');
		});
		
		$('#alterpopupWindowShipmentMethodNew').on('close',function(){
			$('#jqxgrid').jqxGrid('updatebounddata');
			shipmentMethodTypeDDL.clearAll();
		});
	};
	
	var initValidateForm = function(){
		var mapRules = [
            	{input: '#wn_shipmentMethodTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
            ];
		var extendRules = [];
		validatorVAL = new OlbValidator($('#alterpopupWindowShipmentMethodNew'), mapRules, extendRules, {position: 'bottom'});
	};

	return{
		init: init,
	}
}());
