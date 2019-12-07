$(function(){
	DlvEntryTemplateObj.init();
});
var DlvEntryTemplateObj = (function() {
	var listShipmentItemFinish = new Array(); 
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				$('#containerNotify').empty();
				var resultValidate = !DlvEntryInfoObj.getValidator().validate();
				if(resultValidate) return false;
				var selectedIndexs = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
				var listItems = new Array();
            	var listShipments = new Array();
            	listShipmentItemSelected = [];
	    		for(var i = 0; i < selectedIndexs.length; i++){
	    			if ($('#jqxgridDetail'+ selectedIndexs[i]).length > 0){
	    				var childIndexs = $('#jqxgridDetail'+ selectedIndexs[i]).jqxGrid('getrows');
		    			for(var j = 0; j < childIndexs.length; j ++){
		    				var data = childIndexs[j];
		    				if (data.quantityCreate > 0){
		    					var map = {};
		    					map['shipmentId'] = data.shipmentId;
		    					map['deliveryId'] = data.deliveryId;
		    					map['orderId'] = data.primaryOrderId;
			    				map['productId'] = data.productId;
			    				map['productCode'] = data.productCode;
				    			map['productName'] = data.productName;
				    			map['shipmentItemSeqId'] = data.shipmentItemSeqId;
				    			map['quantity'] = data.quantityCreate;
				    			map['quantityUomId'] = data.quantityUomId;
				    			listItems.push(map);
				    			listShipmentItemSelected.push(map);
		    				}
		    			}	
	    			} else {
	    				var data = $('#jqxgridfilterGrid').jqxGrid('getrowdata', selectedIndexs[i]);
	    				var listItemTmp = data.rowDetail;
	    				for (var n = 0; n < listItemTmp.length; n ++){
	    					if (listItemTmp[n].quantity > 0){
		    					var map = {};
		    					map['shipmentId'] = listItemTmp[n].shipmentId;
		    					map['deliveryId'] = data.deliveryId;
		    					map['orderId'] = data.primaryOrderId;
			    				map['productId'] = listItemTmp[n].productId;
			    				map['productCode'] = listItemTmp[n].productCode;
				    			map['productName'] = listItemTmp[n].productName;
				    			map['shipmentItemSeqId'] = listItemTmp[n].shipmentItemSeqId;
				    			map['quantity'] = listItemTmp[n].quantity;
				    			map['quantityUomId'] = listItemTmp[n].quantityUomId;
				    			listItems.push(map);
				    			listShipmentItemSelected.push(map);
		    				}
	    				}
	    			}
	    		}
				if (listItems.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
					return false;
				}
				showConfirmPage(listItems);
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		finishCreateDeliveryEntry();
	            	Loading.hide('loadingMacro');
            	}, 500);
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(listData){
		for (var i = 0; i < faciData.length; i ++){
			if ($("#facilityIdAdd").jqxDropDownList('val') == faciData[i].facilityId){
				$("#facilityIdDT").text(faciData[i].description);
			}
		}
		
		var contactMechDescription = $("#contactMechId").jqxDropDownList('val');
		$.ajax({
			type: 'POST',
			url: 'getDetailPostalAddress',
			async: false,
			data: {
        		contactMechId:$('#contactMechId').jqxDropDownList('val'),
			},
			success: function(data){
				contactMechDescription = data.fullName;
				$("#contactMechIdDT").text(contactMechDescription);
			},
		});
		
		for (var i=0; i < statusDataDE.length; i++){
			if (statusDataDE[i].statusId == $("#statusDEId").jqxDropDownList('val')){
				$("#statusDEIdDT").text(statusDataDE[i].description);
			}
		}
		
		for (var i=0; i < shipmentTypeData.length; i++){
			if (shipmentTypeData[i].shipmentTypeId == $("#shipmentTypeId").jqxDropDownList('val')){
				$("#shipmentTypeIdDT").text(shipmentTypeData[i].description);
			}
		}
		
		$("#fromDateDT").text($("#fromDateAdd").val());
		$("#thruDateDT").text($("#thruDateAdd").val());
		$("#shipCostDT").text(formatnumber(parseFloat($("#shipCost").val())));
		$("#descriptionDT").text($("#description").val());
		
		var carrierData = $("#carrierPartyId").jqxDropDownList('getItems');
		var carrierName = null;
		if (carrierData.length > 0){
			for (var j = 0; j < carrierData.length; j ++){
				var data = carrierData[j].originalItem;
				if (data.partyId == $("#carrierPartyId").jqxDropDownList('val')){
					$("#carrierPartyIdDT").text(unescapeHTML(data.description));
					carrierName = unescapeHTML(data.description)
					break;
				}
			}
		}
		if ($("#fixedAssetId").jqxDropDownList('disabled') == true){
			$("#fixedAssetIdDT").text(carrierName);
		} else { 
			for (var i=0; i < vehicleData.length; i++){
				if (vehicleData[i].fixedAssetId == $("#fixedAssetId").jqxDropDownList('val')){
					$("#fixedAssetIdDT").text(unescapeHTML(vehicleData[i].description));
				}
			}
		}
		
		if ($("#driverPartyId").jqxDropDownList('disabled') == true){
			$("#driverPartyIdDT").text(carrierName);
		} else {
			for (var i=0; i < driverPartyData.length; i++){
				if (driverPartyData[i].partyId == $("#driverPartyId").jqxDropDownList('val')){
					$("#driverPartyIdDT").text(unescapeHTML(driverPartyData[i].description));
				}
			}
		}
		if ($("#delivererPartyId").jqxDropDownList('disabled') == true){
			$("#delivererPartyIdDT").text(carrierName);
		} else {
			for (var i=0; i < delivererPartyData.length; i++){
				if (delivererPartyData[i].partyId == $("#delivererPartyId").jqxDropDownList('val')){
					$("#delivererPartyIdDT").text(unescapeHTML(driverPartyData[i].description));
				}
			}
		}
//		$("#totalWeightDT").text($("#totalWeight").jqxDropDownList('val').toLocaleString(localeStr));
//		
//		for (var i=0; i < weightUomData.length; i++){
//			if (weightUomData[i].uomId == $("#weightUomIdAdd").val()){
//				$("#weightUomIdDT").text(weightUomData[i].description);
//			}
//		}
		
		var tmpSource = $("#jqxgridShipmentSelected").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listData;
			$("#jqxgridShipmentSelected").jqxGrid('source', tmpSource);
		}
	}
	
	function finishCreateDeliveryEntry(){
		var listShipmentItemTmp = [];
		for(var j = 0; j < listShipmentItemSelected.length; j ++){
			var data = listShipmentItemSelected[j];
			var map = {};
			map['shipmentId'] = data.shipmentId;
			map['shipmentItemSeqId'] = data.shipmentItemSeqId;
			map['quantity'] = data.quantity;
			map['quantityUomId'] = data.quantityUomId;
			listShipmentItemTmp.push(map);
		}
		listShipmentItemTmp = JSON.stringify(listShipmentItemTmp);
		var dataMap = {};
		dataMap = {
    		facilityId:$('#facilityIdAdd').jqxDropDownList('val'),
    		contactMechId:$('#contactMechId').jqxDropDownList('val'),
    		fromDate: $('#fromDateAdd').jqxDateTimeInput('getDate').getTime(),
    		thruDate: $('#thruDateAdd').jqxDateTimeInput('getDate').getTime(),
    		shipmentItems: listShipmentItemTmp
		};
		if ($('#delivererPartyId').jqxDropDownList('val') && $('#delivererPartyId').jqxDropDownList('val') != ""){
			dataMap.delivererPartyId = $('#delivererPartyId').jqxDropDownList('val');
		}
		if ($('#driverPartyId').jqxDropDownList('val') && $('#driverPartyId').jqxDropDownList('val') != ""){
			dataMap.driverPartyId = $('#driverPartyId').jqxDropDownList('val');
		}
		if ($('#fixedAssetId').jqxDropDownList('val') && $('#fixedAssetId').jqxDropDownList('val') != ""){
			dataMap.fixedAssetId = $('#fixedAssetId').jqxDropDownList('val');
		}
		if ($('#carrierPartyId').jqxDropDownList('val') && $('#carrierPartyId').jqxDropDownList('val') != ""){
			dataMap.carrierPartyId = $('#carrierPartyId').jqxDropDownList('val');
		}
		if ($('#shipCost').jqxNumberInput('val')){
			dataMap.shipCost = $('#shipCost').jqxNumberInput('val');
		}
		if ($('#description').jqxInput('val')){
			dataMap.description = $('#description').jqxInput('val');
		}
		$.ajax({
			type: 'POST',
			url: 'createNewDeliveryEntry',
			async: false,
			data: dataMap,
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(data){
				if (data._ERROR_MESSAGE_) {
					jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
					return;
				}
				viewDeliveryEntryDetail(data.deliveryEntryId);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
				$("#btnPrevWizard").removeClass("disabled");
				$("#btnNextWizard").removeClass("disabled");
			},
		});
	}
	
	function viewDeliveryEntryDetail(deliveryEntryId){
		window.location.href = 'deliveryEntryDetail?deliveryEntryId=' + deliveryEntryId;
	}
	var initValidateForm = function(){
		
	};
	 var entityMap = {
	    "&": "&amp;",
	    "<": "&lt;",
	    ">": "&gt;",
	    '"': '&quot;',
	    "'": '&#39;',
	    "/": '&#x2F;'
	 };

	 function escapeHtml(string) {
	    return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	 }
	 function unescapeHTML(escapedStr) {
	     var div = document.createElement('div');
	     div.innerHTML = escapedStr;
	     var child = div.childNodes[0];
	     return child ? child.nodeValue : '';
	 };
	return {
		init: init,
	}
}());