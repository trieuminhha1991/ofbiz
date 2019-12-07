$(function(){
	ImportDocObj.init();
});
var ImportDocObj = (function() {
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
				var resultValidate = !BillOfLading.getValidate();
				if(resultValidate) return false;
			}
			if(info.step == 2 && (info.direction == "next")) {
				// check form valid
				var resultValidate = !Container.getValidate();
				if(resultValidate) return false;
			}
			if(info.step == 3 && (info.direction == "next")) {
				// check form valid
				var resultValidate = !PackingListObj.getValidate();
				if(resultValidate) return false;
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishCreatDocument();
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
		$("#txtBillNumberDT").text($("#txtBillNumber").val());
		if (shippingSelected){
			$("#shippingPartyDT").text(shippingSelected.fullName);
		}
		if ($("#txtdepartureDate").jqxDateTimeInput('val')){
			var x = $("#txtdepartureDate").jqxDateTimeInput('val');
			$("#txtdepartureDateDT").text(x);
		}
		
		if ($("#txtarrivalDate").jqxDateTimeInput('val')){
			var x = $("#txtarrivalDate").jqxDateTimeInput('val');
			$("#txtarrivalDateDT").text(x);
		}
		
		$("#billDescriptionDT").text($("#billDescription").val());
		if ($("#containerTypeId").jqxDropDownList('getSelectedItem')){
			$("#containerTypeIdDT").text($("#containerTypeId").jqxDropDownList('getSelectedItem').label);
		}
		$("#containerNumberDT").text($("#containerNumber").val());
		$("#sealNumberDT").text($("#sealNumber").val());
		
		if (facilitySelected){
			$("#facilityContainerDT").text(facilitySelected.facilityName);
		}
		
		$("#contDescriptionDT").text($("#contDescription").val());
		$("#packingListNumberDT").text($("#packingListNumber").val());
		$("#invoiceNumberDT").text($("#invoiceNumber").val());
		$("#orderNumberSuppDT").text($("#orderNumberSupp").val());
		
		if (agreementSelected){
			$("#agreementDT").text(agreementSelected.agreementCode);
		}
		
		if ($("#packingListDate").jqxDateTimeInput('val')){
			var x = $("#packingListDate").jqxDateTimeInput('val');
			$("#packingListDateDT").text(x);
		}
		
		if ($("#invoiceDate").jqxDateTimeInput('val')){
			var x = $("#invoiceDate").jqxDateTimeInput('val');
			$("#invoiceDateDT").text(x);
		}
		
		$("#totalNetWeightDT").text(formatnumber($("#totalNetWeight").val()));
		$("#totalGrossWeightDT").text(formatnumber($("#totalGrossWeight").val()));

		if ($("#tableProduct").length > 0){
			var table = document.getElementById('tableProduct');
			table.deleteTHead();
			var header = table.createTHead();
			header.insertRow(0);
			var tr = document.getElementById('tableProduct').tHead.children[0];
			
			th1 = document.createElement('th');
			th1.innerHTML = uiLabelMap.SequenceId;
			tr.appendChild(th1);
			
			th2 = document.createElement('th');
			th2.innerHTML = uiLabelMap.ProductId;
			tr.appendChild(th2);
			
			th3 = document.createElement('th');
			th3.innerHTML = uiLabelMap.ProductName;
			tr.appendChild(th3);
			
			th4 = document.createElement('th');
			th4.innerHTML = uiLabelMap.originOrderUnit;
			tr.appendChild(th4);
			
			th5 = document.createElement('th');
			th5.innerHTML = uiLabelMap.packingUnits;
			tr.appendChild(th5);
			
			th6 = document.createElement('th');
			th6.innerHTML = uiLabelMap.orderUnits;
			tr.appendChild(th6);
			
			th7 = document.createElement('th');
			th7.innerHTML = uiLabelMap.dateOfManufacture;
			tr.appendChild(th7);
			
			th8 = document.createElement('th');
			th8.innerHTML = uiLabelMap.ProductExpireDate;
			tr.appendChild(th8);
			
			th9 = document.createElement('th');
			th9.innerHTML = uiLabelMap.batchNumber;
			tr.appendChild(th9);
			
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];

			for (var i in listProductSelected){
				var product = listProductSelected[i];
				
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				var u = parseInt(i) + 1;
				var newText = document.createTextNode(u);
				newCell0.appendChild(newText);
				
				var newCell1 = newRow.insertCell(1);
				newText = document.createTextNode(product.productCode);
				newCell1.appendChild(newText);
				
				var newCell2 = newRow.insertCell(2);
				newText = document.createTextNode(product.productName);
				newCell2.appendChild(newText);
				
				var newCell3 = newRow.insertCell(3);
				newText = document.createTextNode(formatnumber(product.originOrderUnit));
				newCell3.appendChild(newText);
				
				
				var newCell4 = newRow.insertCell(4);
				newText = document.createTextNode(formatnumber(product.packingUnit));
				newCell4.appendChild(newText);
				
				
				var newCell5 = newRow.insertCell(5);
				newText = document.createTextNode(formatnumber(product.orderUnit));
				newCell5.appendChild(newText);
				
				
				var newCell6 = newRow.insertCell(6);
				if (product.datetimeManufactured){
					newText = document.createTextNode(DatetimeUtilObj.getFormattedDate(new Date(product.datetimeManufactured)));
				} else {
					newText = document.createTextNode("");
				}
				newCell6.appendChild(newText);
				
				
				var newCell7 = newRow.insertCell(7);
				if (product.expireDate){
					newText = document.createTextNode(DatetimeUtilObj.getFormattedDate(new Date(product.expireDate)));
				} else {
					newText = document.createTextNode("");
				}
				newCell7.appendChild(newText);
				
				var newCell8 = newRow.insertCell(8);
				if (product.batchNumber){
					newText = document.createTextNode(product.batchNumber);
				} else {
					newText = document.createTextNode("");
				}
				newCell8.appendChild(newText);
			}
		}
	}
	
	function finishCreatDocument(){
		var data = {};
		// bill
		data.billNumber = $("#txtBillNumber").jqxInput('val');
		if (shippingSelected){
			data.partyIdFrom = shippingSelected.partyId;
		}
		
		var x = $("#txtdepartureDate").jqxDateTimeInput('getDate');
		if (x){
			data.departureDate = x.getTime();
		}
			
		var y = $("#txtarrivalDate").jqxDateTimeInput('getDate');
		if (y){
			data.arrivalDate = y.getTime();
		}
		data.description = $("#billDescription").jqxInput('val');
		
		// container
		data.containerNumber = $("#containerNumber").jqxInput('val');
		data.containerTypeId = $("#containerTypeId").jqxDropDownList('val');
		data.sealNumber = $("#sealNumber").jqxInput('val');
		data.contDescription = $("#contDescription").jqxInput('val');
		
		// packinglist
		var packingListNumber = $('#packingListNumber').jqxInput('val');
		var orderNumberSupp = $('#orderNumberSupp').jqxInput('val');
		var invoiceNumber = $('#invoiceNumber').jqxInput('val');
		var totalNetWeight = $('#totalNetWeight').jqxNumberInput('val');
		totalNetWeight = totalNetWeight.toString();
		var totalGrossWeight = $('#totalGrossWeight').jqxNumberInput('val');
		totalGrossWeight = totalGrossWeight.toString();
		
		var packingListDate = $('#packingListDate').jqxDateTimeInput('getDate')
				.getTime();
		var invoiceDate = $('#invoiceDate').jqxDateTimeInput('getDate')
				.getTime();
		var destFacilityId = null;
		if (facilitySelected) {
			destFacilityId = facilitySelected.facilityId;
		}
		
		data.agreementId = agreementSelected.agreementId;
		data.packingListNumber = packingListNumber;
		data.orderNumberSupp = orderNumberSupp;
		data.invoiceNumber = invoiceNumber;
		data.orderTypeSuppId = "ORIGINAL";	
		data.totalNetWeight = totalNetWeight;
		data.totalGrossWeight = totalGrossWeight;
		data.packingListDate = packingListDate;
		data.invoiceDate = invoiceDate;
		data.destFacilityId = destFacilityId;
		
		var listProducts = [];
		for (var i in listProductSelected){
			var map = listProductSelected[i];
			var obj = {};
			obj.orderId = map.orderId;
			obj.orderItemSeqId = map.orderItemSeqId;
			obj.productId = map.productId;
			obj.batchNumber = map.batchNumber;
			obj.orderUnit = map.orderUnit;
			obj.packingUnit = map.packingUnit;
			if (map.datetimeManufactured){
				var x = new Date(map.datetimeManufactured);
				obj.datetimeManufactured = x.getTime();
			}
			if (map.expireDate){
				var x = new Date(map.expireDate);
				obj.expireDate = x.getTime();
			}
			obj.originOrderUnit = map.originOrderUnit;
			listProducts.push(obj);
		}
		
		data.packingListDetail = JSON.stringify(listProducts);
		
    	var url = "createImportDocument";
    	$.ajax({	
			 type: "POST",
			 url: url,
			 data: data,
			 dataType: "json",
			 async: false,
			 success: function(res){
				 if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
					 jOlbUtil.alert.error(res._ERROR_MESSAGE_);
					 Loading.hide("loadingMacro");
					 btnClick = false;
					 return false;
				 }
				 if (res.billId){
					 window.location.href = "viewDetailBillOfLading?billId="+res.billId;
				 }
			 },
			 error: function(response){
			 }
	 		}).done(function(data) {
  		});
	}
	
	var initValidateForm = function(){
		
	};
	
	return {
		init: init,
	}
}());