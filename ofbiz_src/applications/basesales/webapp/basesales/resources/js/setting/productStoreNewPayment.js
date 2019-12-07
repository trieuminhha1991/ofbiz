$(function(){
	OlbSettingProductStoreNewPayment.init();
});

var OlbSettingProductStoreNewPayment = (function(){
	var paymentMethodTypeDDL;
	var paymentServiceTypeDDL;
	var applyToAllProductsDDL;
	var validatorVAL;
	
	var init = (function(){
		initWindow();
		initElementComplex();
		initEvent();
		initValidateForm();
	});
	
	var initWindow = (function(){
		jOlbUtil.windowPopup.create($("#alterpopupWindowPaymentMethodNew"), {width: 640, height: 250, cancelButton: $("#wn_alterCancel1")});
	});
	
	var initElementComplex = function(){
		var configPaymentMethodType = {
			width: '96%',
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: false,
			key: 'paymentMethodTypeId',
			value: 'description',
			displayDetail: true,
			autoDropDownHeight: true
		}
		paymentMethodTypeDDL = new OlbDropDownList($("#wn_paymentMethodTypeId"), paymentMethodTypeList, configPaymentMethodType, []);
		
		var configPaymentServiceType = {
				width: '96%',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				key: 'enumId',
				value: 'description',
				autoDropDownHeight: true
		}
		paymentServiceTypeDDL = new OlbDropDownList($("#wn_paymentServiceTypeEnumId"), paymentServiceTypeList, configPaymentServiceType, []);
		
		var configApplyToAllProducts = {
				width: '96%',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				key: 'value',
				value: 'text',
				autoDropDownHeight: true
		}
		applyToAllProductsDDL = new OlbDropDownList($("#wn_applyToAllProducts"), answerList, configApplyToAllProducts, ["Y"]);
	};
	
	var initEvent = function(){
		$('#wn_alterSave1').click(function(){
			if (!validatorVAL.validate()) {
				return false;
			}
			
			var row = {
				productStoreId : productStoreId,
				paymentMethodTypeId : paymentMethodTypeDDL.getValue(),
				paymentServiceTypeEnumId : paymentServiceTypeDDL.getValue(),
				paymentService : "",
				paymentCustomMethodId : "",
				paymentGatewayConfigId : "",
				paymentPropertiesPath : "",
				applyToAllProducts : applyToAllProductsDDL.getValue(),
			};
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			$("#jqxgrid").jqxGrid('clearSelection');
			$("#jqxgrid").jqxGrid('selectRow', 0);
			$("#alterpopupWindowPaymentMethodNew").jqxWindow('close');
		});
		
		$('#alterpopupWindowPaymentMethodNew').on('close',function(){
			$("#jqxgrid").jqxGrid('updatebounddata');
			paymentMethodTypeDDL.clearAll();
			paymentServiceTypeDDL.clearAll();
			applyToAllProductsDDL.clearAll();
			/*
			$('#paymentCustomMethodIdAdd').jqxDropDownList('clearSelection');
			$('#paymentGatewayConfigIdAdd').jqxDropDownList('clearSelection');
			$('#paymentServiceAdd').val(null);
			$('#paymentPropertiesPathAdd').val(null);
			*/
		});
	};
	
	var initValidateForm = function(){
		var mapRules = [
            	{input: '#wn_paymentMethodTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
            	{input: '#wn_paymentServiceTypeEnumId', type: 'validObjectNotNull', objType: 'dropDownList'},
            ];
		var extendRules = [];
		validatorVAL = new OlbValidator($('#alterpopupWindowPaymentMethodNew'), mapRules, extendRules, {position: 'bottom'});
	};
	
	return {
		init: init,
	}
}());
