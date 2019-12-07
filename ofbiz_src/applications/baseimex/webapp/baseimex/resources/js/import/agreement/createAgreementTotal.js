$(function(){
	AgreeTotalObj.init();
});
var AgreeTotalObj = (function() {
	var btnClick = false;
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
				
				var check = true;
				var validate = $('#agreementEditor').jqxValidator('validate');
				if(!validate){
					return false;
				}
				ObjAggrement.loadOrderData();
			} else if(info.step == 2 && (info.direction == "next")) {
				if( orderSelected == undefined || orderSelected == null){
					jOlbUtil.alert.error(uiLabelMap.BIEYouNotYetChooseOrder);
					return false;
				}
				$.each(listProductSelected, function(i){
	   				var olb = listProductSelected[i];
	   				if (olb.quantity > 0){
	   					check = false;
	   				}
	   			});
				if (check || listProductSelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureSave, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishCreateAgreement();
		            	Loading.hide('loadingMacro');
	            	}, 500);
	            	btnClick = true;
				} 
            }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
            	btnClick = false;
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(){
		$("#agreementNameDT").text($("#EditPurchaseAgreement2_agreementName").val());
		$("#agreementCodeDT").text($("#agreementCode").val());
		$("#exportPortDT").text($("#exportPort").val());
		$("#agreementDateDT").text(DateUtil.getFormattedDate(new Date($("#EditPurchaseAgreement2_agreementDate").jqxDateTimeInput('getDate').getTime())));
		$("#slideADT").text($("#EditPurchaseAgreement2_partyIdFrom").jqxDropDownList('getSelectedItem').label);
		$("#roleADT").text($("#EditPurchaseAgreement2_roleTypeIdFromText").text());
		$("#addressADT").text($("#EditPurchaseAgreement2_addressIdFrom").jqxDropDownList('getSelectedItem').label);
		$("#faxNumberDT").text($("#EditPurchaseAgreement2_faxNumberIdFrom").jqxDropDownList('getSelectedItem').label);
		$("#telephoneNumberDT").text($("#EditPurchaseAgreement2_telephoneIdFrom").jqxDropDownList('getSelectedItem').label);
		$("#fromDateDT").text(DateUtil.getFormattedDate(new Date($("#EditPurchaseAgreement2_fromDate").jqxDateTimeInput('getDate').getTime())));
		$("#thruDateDT").text(DateUtil.getFormattedDate(new Date($("#EditPurchaseAgreement2_thruDate").jqxDateTimeInput('getDate').getTime())));
		$("#slideBDT").text(partyIdToSelected.partyCode + ' - ' + partyIdToSelected.groupName);
		$("#roleBDT").text($("#EditPurchaseAgreement2_roleTypeIdToText").text());
		$("#addressBDT").text($("#EditPurchaseAgreement2_addressIdTo").jqxDropDownList('getSelectedItem').label);
		$("#emailBDT").text($("#EditPurchaseAgreement2_emailAddressIdTo").jqxDropDownList('getSelectedItem').label);
		$("#currencyUomDT").text($("#currencyUomId").jqxDropDownList('getSelectedItem').label);
		if (portSelected){
			$("#portOfDischargeDT").text(portSelected.facilityName);
		}
		if (facilitySelected){
			$("#facilityDT").text(facilitySelected.facilityName);
		}
		
		if ($("#listBank").jqxDropDownList('getSelectedItem')){
			$("#bankDT").text($("#listBank").jqxDropDownList('getSelectedItem').label);
		}
		if ($("#bankAccount").jqxDropDownList('getSelectedItem')){
			$("#bankAccountDT").text($("#bankAccount").jqxDropDownList('getSelectedItem').label);
		}
		
		if ($("#tableProduct").length > 0){
			var totalValue = 0;
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
			if (listProductSelected.length > 0){
				listProductSelected.sort(function(a, b) { 
				    return b.quantity - a.quantity;
				})
			}

			for (var i in listProductSelected){
				var data = listProductSelected[i];
				
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				var u = parseInt(i) + 1;
				var newText = document.createTextNode(u);
				newCell0.appendChild(newText);
				
				var newCell1 = newRow.insertCell(1);
				newText = document.createTextNode(data.productCode);
				newCell1.appendChild(newText);
				
				var newCell2 = newRow.insertCell(2);
				newText = document.createTextNode(data.productName);
				newCell2.appendChild(newText);
				
				var newCell3 = newRow.insertCell(3);
				
				newCell3.className = 'align-right';
				if (data.requireAmount && data.requireAmount == 'Y' && data.amountUomTypeId == 'WEIGHT_MEASURE') {
					newText = document.createTextNode(getUomDesc(data.weightUomId));
				} else {
					newText = document.createTextNode(getUomDesc(data.quantityUomId));
				}
				newCell3.appendChild(newText);
				
				var newCell4 = newRow.insertCell(4);
				if (!productPlanId){
					newCell4.className = 'hide';
				}
				
				if (data.planQuantity){
					newText = document.createTextNode(formatnumber(data.planQuantity));
					newCell4.appendChild(newText);
				} else {
					newText = document.createTextNode("0");
					newCell4.appendChild(newText);
				}
				
				var newCell5 = newRow.insertCell(5);
				if (!productPlanId){
					newCell5.className = 'hide';
				}
				
				if (data.orderedQuantity){
					newText = document.createTextNode(formatnumber(data.orderedQuantity));
					newCell5.appendChild(newText);
				} else {
					newText = document.createTextNode("0");
					newCell5.appendChild(newText);
				}
				
				var newCell6 = newRow.insertCell(6);
				newText = document.createTextNode(formatnumber(data.quantity));
				newCell6.appendChild(newText);
				
				var newCell7 = newRow.insertCell(7);
				newCell7.className = 'align-right';
				newCell7.className = 'align-right';
				var u = data.lastPrice; 
				if (locale && locale === 'vi' && typeof(u) === 'string'){
					u = data.lastPrice.toString().replace('.', '');
					u = u.replace(',', '.');
				}
				newText = document.createTextNode(formatnumber(u));
				newCell7.appendChild(newText);
				
				var newCell8 = newRow.insertCell(8);
				newCell8.className = 'align-right';
				newCell8.className = 'align-right';
				if (data.lastPrice){
					var u = data.lastPrice;
					if (locale && locale === 'vi' && typeof(u) === 'string'){
						u = data.lastPrice.toString().replace('.', '');
						u = u.replace(',', '.');
					}
					var lastPrice = parseFloat(u.toString());
					var v = data.quantity;
					if (locale && locale === 'vi' && typeof(v) === 'string'){
						v = v.toString().replace('.', '');
						v = v.replace(',', '.');
					}
					var quantity = parseFloat(v.toString());
					value = lastPrice*parseFloat(quantity);
					totalValue = totalValue + value;
					if (value >= 0) {
						newText = document.createTextNode(formatnumber(value));
					} 
					newCell8.appendChild(newText);
				}
			}
			if (totalValue >= 0){
				var newRowTotal = tableRef.insertRow(tableRef.rows.length);
				var newCellTotal0 = newRowTotal.insertCell(0);
				if (!productPlanId){
					newCellTotal0.colSpan = 6;
				} else {
					newCellTotal0.colSpan = 8;
				}
				newCellTotal0.className = 'align-right';
				newCellTotal0.style.fontWeight="bold";
				newCellTotal0.style.background="#f2f2f2";
				var str = uiLabelMap.OrderItemsSubTotal.toUpperCase();
				var newTextTotal = document.createTextNode(str);
				newCellTotal0.appendChild(newTextTotal);
				
				var newCellTotal9 = newRowTotal.insertCell(1);
				newCellTotal9.className = 'align-right';
				newCellTotal9.style.background="#f2f2f2";
				var newTextTotal = document.createTextNode(formatnumber(totalValue));
				newCellTotal9.appendChild(newTextTotal);
			}
		}
	}
	
	function finishCreateAgreement(){
		var orderItems = JSON.stringify(listProductSelected);

		var agreementId = $("#EditPurchaseAgreement2_agreementId").val();
		var agreementTypeId = $("input[name=agreementTypeId]").val();
		var agreementDate = $("#EditPurchaseAgreement2_agreementDate").jqxDateTimeInput('getDate').getTime();
		var agreementName = $("input[name=agreementName]").val();
		var agreementCode = $("input[name=agreementCode]").val();
		var exportPort = $("input[name=exportPort]").val();
		var fromDate = $("#EditPurchaseAgreement2_fromDate").jqxDateTimeInput('getDate').getTime();
		var thruDate = $("#EditPurchaseAgreement2_thruDate").jqxDateTimeInput('getDate').getTime();
		var weekETD = $("input[name=weekETD]").val();
		var partyIdFrom = "";
		var itemFrom = $('#EditPurchaseAgreement2_partyIdFrom').jqxDropDownList('getSelectedItem');
		if (itemFrom) {
			partyIdFrom = itemFrom.value;
		}
		var roleTypeIdFrom = $('#EditPurchaseAgreement2_roleTypeIdFrom').val();
		var representPartyIdFrom = '${userLoginId}';
		var addressIdFrom = "";
		var addressFrom = $('#EditPurchaseAgreement2_addressIdFrom').jqxDropDownList('getSelectedItem');
		if (addressFrom) {
			addressIdFrom = addressFrom.value;
		}
		var telephoneIdFrom = "";
		var telephoneFrom = $('#EditPurchaseAgreement2_telephoneIdFrom').jqxDropDownList('getSelectedItem');
		if (telephoneFrom) {
			telephoneIdFrom = telephoneFrom.value;
		}
		var faxNumberIdFrom = "";
		var faxNumberFrom = $('#EditPurchaseAgreement2_faxNumberIdFrom').jqxDropDownList('getSelectedItem');
		if (faxNumberFrom) {
			faxNumberIdFrom = faxNumberFrom.value;
		}
		var partyIdTo = partyIdToSelected.partyId;
		var roleTypeIdTo = $('#EditPurchaseAgreement2_roleTypeIdTo').val();
		var addressIdTo = "";
		var addressTo = $('#EditPurchaseAgreement2_addressIdTo').jqxDropDownList('getSelectedItem');
		if (addressTo) {
			addressIdTo = addressTo.value;
		}
		var emailAddressIdTo = "";
		var emailAddressTo = $('#EditPurchaseAgreement2_emailAddressIdTo')
				.jqxDropDownList('getSelectedItem');
		if (emailAddressTo) {
			emailAddressIdTo = emailAddressTo.value;
		}
		var productPlanId = $('#EditPurchaseAgreement2_productPlanId').val();
//		var portOfDischargeId = $('#EditPurchaseAgreement2_portOfDischargeId').val();
		var portOfDischargeId = null;
		if (portSelected){
			portOfDischargeId = portSelected.facilityName;
		}
		var destFacilityId = null;
		if (facilitySelected){
			destFacilityId = facilitySelected.facilityId;
		}
		var transshipment = "Y";
		var partialShipment = "Y";
    	var url = "createImExAgreement";
    	
    	var bankAccount = $("#bankAccount").jqxDropDownList('val');
    	var orderId = null;
    	if (orderSelected){
    		orderId = orderSelected.orderId;
    	}
    	$.ajax({	
			 type: "POST",
			 url: url,
			 data: {
				 agreementId: agreementId, 
				 destFacilityId: destFacilityId, 
				 finAccountId: bankAccount, 
				 agreementTypeId: agreementTypeId, 
				 agreementDate:agreementDate, 
				 fromDate: fromDate, 
				 thruDate: thruDate,
				 weekETD: weekETD, 
				 partyIdFrom: partyIdFrom, 
				 roleTypeIdFrom: roleTypeIdFrom, 
				 representPartyIdFrom: representPartyIdFrom, 
				 addressIdFrom: addressIdFrom,
				 telephoneIdFrom: telephoneIdFrom, 
				 faxNumberIdFrom: faxNumberIdFrom,
				 partyIdTo: partyIdTo, 
				 roleTypeIdTo: roleTypeIdTo,
				 addressIdTo: addressIdTo, 
				 currencyUomId: currencySelected.currencyUomId, 
				 emailAddressIdTo: emailAddressIdTo,
				 agreementName: agreementName, 
				 agreementCode: agreementCode, 
				 exportPort: exportPort, 
				 productPlanId: productPlanId, 
				 portOfDischargeId: portOfDischargeId, 
				 customTimePeriodId: customTimePeriodId,
				 transshipment: transshipment, 
				 partialShipment: partialShipment, 
				 statusId: "AGREEMENT_CREATED", 
				 orderItems: orderItems,
				 orderId: orderId,
			 },
			 dataType: "json",
			 async: false,
			 success: function(data){
				 if (data._ERROR_MESSAGE_ != undefined && data._ERROR_MESSAGE_ != null) {
					 jOlbUtil.alert.error(uiLabelMap.UpdateError + ". " + data._ERROR_MESSAGE_);
					 Loading.hide("loadingMacro");
					 return false;
				 }
				 var agreementId = data.agreementId;
				 window.location.href = "detailPurchaseAgreement?agreementId="+agreementId;
			 },
	 		}).done(function(data) {
  		});
	}
	
	function viewDeliveryDetail(deliveryId){
		window.location.href = 'deliverySalesDeliveryDetail?deliveryId=' + deliveryId;
	}
	var initValidateForm = function(){
		
	};
	
	return {
		init: init,
		viewDeliveryDetail: viewDeliveryDetail,
	}
}());