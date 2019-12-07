$(function(){
	TripEditObjTotal.init();
});
var TripEditObjTotal = (function() {
	var btnClick = false;
	var confirmClick = false;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
	};
	var initInputs = function() {
		var listProductRelate = [];
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			
			if(info.step == 1 && (info.direction == "next")) {
				
				var validate = TripEditInfoObj.getValidator().validate();
				if(!validate) return false;
          		var listOrderSelected = TripEditObj.getListOrderSelected();
				if (listOrderSelected.length <= 0 ){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseOrder);
				return false;
			   }
				showConfirmPage(listOrderSelected);	
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureEdit, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishEditTrip(TripEditObj.getListOrderSelected());
		            	Loading.hide('loadingMacro');
	            	}, 300);
	            	btnClick = true;
				} 
            }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
            	btnClick = false;
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(listData){
		$("#facilityIdDT").text(facility[0].facilityName);
		var addressSrc = $("#contactMechId").jqxDropDownList('source');
		$("#contactMechIdDT").text(addressSrc[0].description);
		$("#shipCostDT").text($("#shipCost").val());
		$("#shipReturnCostDT").text($("#shipReturnCost").val());
		$("#fromDateDT").text($("#fromDate").val());
		$("#thruDateDT").text($("#thruDate").val());
		$("#driverPartyIdDT").text(shipperPartyData['shipperName']);
		$("#descriptionDT").text($("#description").val());
		
		if ($("#tableOrder").length > 0) {
            var totalValue = 0;
            $('#tableOrder tbody').empty();
            var tableRef = document.getElementById('tableOrder').getElementsByTagName('tbody')[0];
			var sequenceNumber = 0;
            for (var i in listData) {
				sequenceNumber++;
                var data = listData[i];
                var newRow = tableRef.insertRow(tableRef.rows.length);
                var newCell0 = newRow.insertCell(0);
                var newText = document.createTextNode(sequenceNumber);
                newCell0.appendChild(newText);

                var newCell1 = newRow.insertCell(1);
                newText = document.createTextNode(data.orderId);
                newCell1.appendChild(newText);

                var newCell2 = newRow.insertCell(2);
                newText = document.createTextNode(data.customerId);
                newCell2.appendChild(newText);

                var newCell3 = newRow.insertCell(3);
                newText = document.createTextNode(data.deliveryClusterId);
                newCell3.appendChild(newText);
                var newCell4 = newRow.insertCell(4);
                newText = document.createTextNode(data.partyName);
                newCell4.appendChild(newText);
                var newCell5 = newRow.insertCell(5);
                newText = document.createTextNode(data.postalAddressName);
                newCell5.appendChild(newText);

                var newCell6 = newRow.insertCell(6);
                newText = document.createTextNode(formatnumber(data.totalGrandAmount));
                newCell6.appendChild(newText);
                totalValue += data.totalGrandAmount;
            }
            if (totalValue) {
                var newRowTotal = tableRef.insertRow(tableRef.rows.length);
                var newCellTotal0 = newRowTotal.insertCell(0);
                newCellTotal0.colSpan = 6;
                newCellTotal0.className = 'align-right';
                newCellTotal0.style.fontWeight = "bold";
                newCellTotal0.style.background = "#f2f2f2";
                var str = uiLabelMap.OrderItemsSubTotal.toUpperCase();
                var newTextTotal = document.createTextNode(str);
                newCellTotal0.appendChild(newTextTotal);

                var newCellTotal8 = newRowTotal.insertCell(1);
                newCellTotal8.className = 'align-right';
                newCellTotal8.style.background = "#f2f2f2";
                var newTextTotal = document.createTextNode(formatnumber(totalValue));
                newCellTotal8.appendChild(newTextTotal);
            }
        }
	}
	
	function finishEditTrip(listData){
		var listOrders = [];
		var listOrdersID = [];
        for (var j = 0; j < listData.length; j++) {
            var data = listData[j];
            var map = {};
            map['orderId'] = data.orderId;
			if(typeof data.packId === 'undefined' ){
				map['packId'] = null;
			}else {
				map['packId'] = data.packId;
			}
			map['statusId'] = 'CREATED';
            listOrders.push(map);
			listOrdersID.push(data.orderId);
        }
		for (var k = 0; k < listOrderOld.length; k++) {
            var data = listOrderOld[k];
            var map = {};
            map['orderId'] = data.orderId;
			map['packId'] = data.packId;
			if(!(listOrdersID.includes(map['orderId']))){
				map['statusId'] = 'CANCELLED';
				listOrders.push(map);
			}
        }
        listOrders = JSON.stringify(listOrders);
        var dataMap = {};
        dataMap = {
            shippingTripId : shippingTripId,
			tripCost: $("#shipCost").val(),
            costCustomerPaid: $("#shipReturnCost").val(),
            startDateTime: $('#fromDate').jqxDateTimeInput('getDate').getTime(),
            finishedDateTime: $('#thruDate').jqxDateTimeInput('getDate').getTime(),
            listOrders: listOrders
        };

        if ($('#driverPartyId').jqxDropDownList('val') && $('#driverPartyId').jqxDropDownList('val') != "") {
            dataMap.driverPartyId = $('#driverPartyId').jqxDropDownList('val');
        }
        // if ($('#shipReturnCost').jqxNumberInput('val')) {
        //     dataMap.tripReturnCost = $('#shipReturnCost').jqxNumberInput('val');
        // }
        // if ($('#shipCost').jqxNumberInput('val')) {
        //     dataMap.tripCost = $('#shipCost').jqxNumberInput('val');
        // }
        if ($('#description').jqxInput('val')) {
            dataMap.description = $('#description').jqxInput('val');
        }
		
        $.ajax({
            type: 'POST',
            url: 'editShippingTripByOrder',
            async: false,
            data: dataMap,
            beforeSend: function () {
                $("#btnPrevWizard").addClass("disabled");
                $("#btnNextWizard").addClass("disabled");
                $("#loader_page_common").show();
            },
            success: function (data) {
                if (data._ERROR_MESSAGE_) {
                    jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
                    return;
                }
                viewShippingTripDetail(data.shippingTripId);
            },
            error: function (data) {
                alert("Send request is error");
            },
            complete: function (data) {
                $("#loader_page_common").hide();
                $("#btnPrevWizard").removeClass("disabled");
                $("#btnNextWizard").removeClass("disabled");
            },
        });
	}
	
	function viewShippingTripDetail(tripId){
		window.location.href = 'shippingTripDetail?shippingTripId=' + tripId;
	}
	
	return {
		init: init,
	}
}());