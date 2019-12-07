$(function(){
	TripTemplateObj.init();
});
// ShippingTripId == TripID
var TripTemplateObj = (function() {
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
				var resultValidate = !TripInfo.getValidator().validate();
				if(resultValidate) return false;
				var selectedIndexs = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
				var listItems = new Array();
            //	var listShipments = new Array();
        listPackItemSelected = [];
	    		for(var i = 0; i < selectedIndexs.length; i++){
	    				var data = $('#jqxgridfilterGrid').jqxGrid('getrowdata', selectedIndexs[i]);
		    					var map = {};
									map['packId'] = data.packId;
		    					map['statusId'] = data.statusId;
		    					map['originContactMechId'] = data.originContactMechId;
			    				map['shipBeforeDate'] = data.shipBeforeDate;
			    				map['shipAfterDate'] = data.shipAfterDate;
									map['partyIdTo'] = data.partyIdTo;
				    			listItems.push(map);
				    			listPackItemSelected.push(map);
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
            		finishTripEntry();
	            	Loading.hide('loadingMacro');
            	}, 500);
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(listData){
		// var selectedShipper = $("#shipperPartyId").jqxDropDownList('getSelectedItem');
		// $("#shipperPartyIdDT").text(selectedShipper.label); 
		var shipperData = $("#shipperPartyId").jqxDropDownList('getItems');
		var shipperName = null;
		if (shipperData.length > 0){
			for (var j = 0; j < shipperData.length; j ++){
				var data = shipperData[j].originalItem;
				if (data.partyId == $("#shipperPartyId").jqxDropDownList('val')){
					$("#shipperPartyIdDT").text(unescapeHTML(data.description));
					shipperName = unescapeHTML(data.description);
					break;
				}
			}
		}
		$("#estimatedTimeStartDT").text($("#estimatedTimeStart").val());
		$("#estimatedTimeEndDT").text($("#estimatedTimeEnd").val());
		$("#tripCostDT").text(formatnumber(parseFloat($("#tripCost").val())));
		$("#costCustomerPaidDT").text(formatnumber(parseFloat($("#costCustomerPaid").val())));
		$("#descriptionDT").text($("#description").val());

		var tmpSource = $("#jqxgridPackSelected").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listData;
			$("#jqxgridPackSelected").jqxGrid('source', tmpSource);
		}
	}

	function finishTripEntry(){
		var listPackItemTmp = [];
		for(var j = 0; j < listPackItemSelected.length; j ++){
			var data = listPackItemSelected[j];
			var map = {};
			map['packId'] = data.packId;
			listPackItemTmp.push(map);
		}
		listPackItemTmp = JSON.stringify(listPackItemTmp);
		var dataMap = {};
		dataMap = {
    		shipperId:$('#shipperPartyId').jqxDropDownList('val'),
				tripCost: formatnumber(parseFloat($("#tripCost").val())),
				costCustomerPaid: formatnumber(parseFloat($("#costCustomerPaid").val())),
				description: $("#description").val(),
    		shipBeforeDate: $('#estimatedTimeStart').jqxDateTimeInput('getDate').getTime(),
    		shipAfterDate: $('#estimatedTimeEnd').jqxDateTimeInput('getDate').getTime(),
    		packItems: listPackItemTmp,
		};
		$.ajax({
			type: 'POST',
			url: 'createShippingTrip',
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
				jOlbUtil.alert.info(uiLabelMap.SuccessfulWhenCreate);
				viewTripDetail(data.tripId);
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

	function viewTripDetail(tripId){
		window.location.href = 'shippingTripDetail?shippingTripId=' + tripId;
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
