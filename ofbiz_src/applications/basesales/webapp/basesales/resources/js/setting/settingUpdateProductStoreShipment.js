$(function(){
	OlbSettingUpdateProductStoreCatalog.init();
});

var OlbSettingUpdateProductStoreCatalog = (function(){
	var initWindow  = (function(){
		$('#alterpopupWindowEdit').jqxWindow({width: 900, maxWidth: 1100, height : 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, title: updatePopup2 + " " + productIdd});
	});
	
	var initDropDownList = (function(){
		$("#productStoreShipMethIdEdit").jqxDropDownList({ source: shipmentMethodTypeList, width: '205px', height: '29px', displayMember: "description", valueMember : "shipmentMethodTypeId", dropDownWidth: 270, disabled: true, placeHolder: choose,
			renderer: function (index, label, value) {
	            var valueStr = shipmentMethodTypeList[index].description + " [" + shipmentMethodTypeList[index].partyId + "/" + shipmentMethodTypeList[index].roleTypeId + "]";
	            return valueStr;
	        },
		});
		$("#productStoreShipMethIdEdit").jqxDropDownList({dropDownHeight:150});
	});
	
	var initInput = (function(){
		jOlbUtil.input.create("#configPropsEdit", {width: '98%', height:24});
		$('#sequenceNumberEdit').jqxNumberInput({width: '100%', height: 28, spinButtons: false, decimalDigits: 0,inputMode: 'simple'});
		$('#minWeightEdit').jqxNumberInput({width: '100%', height: 28, spinButtons: false, decimalDigits: 3,inputMode: 'simple' });
		$('#maxWeightEdit').jqxNumberInput({width: '100%', height: 28, spinButtons: false, decimalDigits: 3,inputMode: 'simple' });
		$('#minSizeEdit').jqxNumberInput({width: '100%', height: 28, spinButtons: false, decimalDigits: 3,inputMode: 'simple' });
		$('#maxSizeEdit').jqxNumberInput({width: '100%', height: 28, spinButtons: false, decimalDigits: 3,inputMode: 'simple' });
		$('#minTotalEdit').jqxNumberInput({width: '100%', height: 28, spinButtons: false, decimalDigits: 3,inputMode: 'simple' });
		$('#maxTotalEdit').jqxNumberInput({width: '100%', height: 28, spinButtons: false, decimalDigits: 3,inputMode: 'simple' });
	});
	
	var eventMenu = (function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action"); 
            if (action == 'update') {
            	var wtmp = window;
        	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
        	   	var tmpwidth = $('#alterpopupWindowEdit').jqxWindow('width');
        	   	$('#alterpopupWindowEdit').jqxWindow('open');
    		   	if (rowindex >= 0) {
    		   		dataEdit();
    		   	}
            }
		});
		
		function dataEdit(){
			var indexSeleted = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid("getrowdata", indexSeleted);
			var a = data.productStoreShipMethId;
			if (data != null) {
				if (data.shipmentMethodTypeId != null) $("#productStoreShipMethIdEdit").jqxDropDownList('selectItem',data.shipmentMethodTypeId);
//				if (data.allowUspsAddr != null) $("#allowUspsAddrEdit").jqxDropDownList('selectItem',data.allowUspsAddr);
//				if (data.requireUspsAddr != null) $("#requireUspsAddrEdit").jqxDropDownList('selectItem',data.requireUspsAddr);
//				if (data.shipmentCustomMethodId != null) $("#shipmentCustomMethodIdEdit").jqxDropDownList('selectItem',data.shipmentCustomMethodId);
//				if (data.shipmentGatewayConfigId != null) $("#shipmentGatewayConfigIdEdit").jqxDropDownList('selectItem',data.shipmentGatewayConfigId);
				if (data.configProps != null) $("#configPropsEdit").val(data.configProps);
				if (data.minWeight != null) $("#minWeightEdit").jqxNumberInput('setDecimal', data.minWeight);
				if (data.maxWeight != null) $("#maxWeightEdit").jqxNumberInput('setDecimal', data.maxWeight);
				if (data.minSize != null) $("#minSizeEdit").jqxNumberInput('setDecimal', data.minSize);
				if (data.maxSize != null) $("#maxSizeEdit").jqxNumberInput('setDecimal', data.maxSize);
				if (data.minTotal != null) $("#minTotalEdit").jqxNumberInput('setDecimal', data.minTotal);
				if (data.maxTotal != null) $("#maxTotalEdit").jqxNumberInput('setDecimal', data.maxTotal);
				if (data.sequenceNumber != null) $("#sequenceNumberEdit").jqxNumberInput('setDecimal', data.sequenceNumber);
				$("#alterpopupWindowEdit").jqxWindow("open");
			}
		}
	});
	
	var eventUpdate = (function(){
		$('#alterSave2').click(function () {
		   	editShipment();
	       	$('#alterpopupWindowEdit').jqxWindow('hide');
	       	$('#alterpopupWindowEdit').jqxWindow('close');
	    });
		
		function editShipment(){
			var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
			var success = successK;
			var index2 = $("#productStoreShipMethIdEdit").jqxDropDownList('getSelectedIndex');
//			var shipmentCustomMethodId2 =  $('#shipmentCustomMethodIdEdit').val(); 
//			var shipmentGatewayConfigId2 =  $('#shipmentGatewayConfigIdEdit').val(); 
			var proId = productIdd;
			var aShipment = new Array();
				var data3 = $("#jqxgrid").jqxGrid('getrowdata', row);
				var map = {};
				map['productStoreId'] = proId;
				map['shipmentMethodTypeId'] = data3.shipmentMethodTypeId;
				map['productStoreShipMethId'] = data3.productStoreShipMethId;
				map['partyId'] = shipmentMethodTypeList[index2].partyId;
				map['roleTypeId'] = shipmentMethodTypeList[index2].roleTypeId;
//				map['allowUspsAddr'] = $('#allowUspsAddrEdit').val();
//				map['requireUspsAddr'] = $('#requireUspsAddrEdit').val();
				// if(shipmentCustomMethodId2){
//					map['shipmentCustomMethodId'] = shipmentCustomMethodId2;
				// }
				// if(shipmentCustomMethodId2){
//					map['shipmentGatewayConfigId'] = shipmentGatewayConfigId2;
				// }
				map['configProps'] = $('#configPropsEdit').val();
				map['minWeight'] = $('#minWeightEdit').val();
				map['maxWeight'] = $('#maxWeightEdit').val();
				map['minSize'] = $('#minSizeEdit').val();
				map['maxSize'] = $('#maxSizeEdit').val();
				map['minTotal'] = $('#minTotalEdit').val();
				map['maxTotal'] = $('#maxTotalEdit').val();
				map['sequenceNumber'] = $('#sequenceNumberEdit').val();
				aShipment = map;
			if (aShipment.length <= 0){
				return false;
			} else {
				aShipment = JSON.stringify(aShipment);
				jQuery.ajax({
			        url: 'updateProductStoreShipmentMethod',
			        type: 'POST',
			        async: true,
			        data: {
			        		'aShipment': aShipment,
		        		},
			        success: function(res) {
			        	var message = '';
						var template = '';
						if(res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_){
							if(res._ERROR_MESSAGE_LIST_){
								message += res._ERROR_MESSAGE_LIST_;
							}
							if(res._ERROR_MESSAGE_){
								message += res._ERROR_MESSAGE_;
							}
							template = 'error';
						}else{
							message = success;
							template = 'success';
							$("#jqxgrid").jqxGrid('updatebounddata');
			        		$("#jqxgrid").jqxGrid('clearselection');
					       	// $('#alterpopupWindowEdit').jqxWindow('hide');
					       	// $('#alterpopupWindowEdit').jqxWindow('close');
						}
						updateGridMessage('jqxgrid', template ,message);
			        },
			        error: function(e){
			        	console.log(e);
			        }
			    });
			}
		}
	});
	
	var init = (function(){
		initWindow();
		initDropDownList();
		initInput();
		eventMenu();
		eventUpdate()
	});
	
	return{
		init: init,
	}
}());	