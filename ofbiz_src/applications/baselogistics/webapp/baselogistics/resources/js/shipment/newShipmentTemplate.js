$(function(){
	ShipmentTemplateObj.init();
});
var ShipmentTemplateObj = (function() {
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
				var resultValidate = !ShipmentInfoObj.getValidator().validate();
				if(resultValidate) return false;
				if (listInvChanged.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		finishCreateShipment();
	            	Loading.hide('loadingMacro');
            	}, 500);
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(){
		for (var i = 0; i < facilityData.length; i ++){
			if ($("#originFacilityId").val() == facilityData[i].facilityId){
				$("#originFacilityIdDT").text(facilityData[i].description);
			}
			if ($("#destinationFacilityId").val() == facilityData[i].facilityId){
				$("#destinationFacilityIdDT").text(facilityData[i].description);
			}
		}
		var originContactSeleted = $("#originContactMechId").jqxDropDownList('getSelectedItem'); 
		var destContactSeleted = $("#destinationContactMechId").jqxDropDownList('getSelectedItem'); 
		$("#originContactMechIdDT").text(unescapeHTML(originContactSeleted.label));
		$("#destinationContactMechIdDT").text(unescapeHTML(destContactSeleted.label));
		$("#estimatedShipDateDT").text($("#estimatedShipDate").val());
		$("#estimatedArrivalDateDT").text($("#estimatedArrivalDate").val());
		$("#estimatedShipCostDT").text($("#estimatedShipCost").val().toLocaleString(localeStr));
		$("#currencyUomIdDT").text($("#currencyUomId").val());
		
		var tmpSource = $("#jqxgridInvSelected").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listInvChanged;
			$("#jqxgridInvSelected").jqxGrid('source', tmpSource);
		}
	}
	
	function finishCreateShipment(){
		var listProducts = [];
		if (listInvChanged != undefined && listInvChanged.length > 0){
			for (var i = 0; i < listInvChanged.length; i ++){
				var data = listInvChanged[i];
				var map = {};
				map['listInventoryItemIds'] = JSON.stringify(data.listInventoryItemIds);
		   		map['productId'] = data.productId;
		   		map['quantity'] = data.quantity;
		   		map['uomId'] = data.uomId;
		        map['facilityId'] = data.facilityId;
		        listProducts.push(map);
			}
		}
		var listProducts = JSON.stringify(listProducts);
		$.ajax({
			type: 'POST',
			url: 'createTransferShipment',
			async: false,
			data: {
				originFacilityId: $("#originFacilityId").val(),
				destinationFacilityId: $("#destinationFacilityId").val(),
				originContactMechId: $("#originContactMechId").val(),
				destinationContactMechId: $("#destinationContactMechId").val(),
				estimatedShipCost: $("#estimatedShipCost").val(),
				estimatedShipDate: $("#estimatedShipDate").jqxDateTimeInput('getDate').getTime(),
				estimatedArrivalDate: $("#estimatedArrivalDate").jqxDateTimeInput('getDate').getTime(),
				currencyUomId: $("#currencyUomId").val(),
				shipmentTypeId: $("#shipmentTypeId").val(),
				listProducts: listProducts,
			},
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(data){
				viewShipmentDetail(data.shipmentId);
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
	
	function viewShipmentDetail(shipmentId){
		window.location.href = 'viewShipmentDetail?shipmentId=' + shipmentId;
	}
	var initValidateForm = function(){
		
	};
	return {
		init: init,
	}
	return {
		init: init,
		olbPageInfo: olbPageInfo,
	}
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
}());