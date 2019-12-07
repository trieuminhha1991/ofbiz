$(function(){
	TransferTemplateObj.init();
});
var TransferTemplateObj = (function() {
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
				var resultValidate = !TransferInfoObj.getValidator().validate();
				if(resultValidate) return false;
				if (listProductSelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
    		finishCreateTransfer();
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(){
		if (originFacility){
			if (originFacility.facilityCode){
				$('#originFacilityIdDT').text("[" + originFacility.facilityCode + "] " + originFacility.facilityName);
			} else {
				$('#originFacilityIdDT').text("[" + originFacility.facilityId + "] " + originFacility.facilityName);
			}
		}
		if (destFacility){
			if (destFacility.facilityCode){
				$('#destFacilityIdDT').text("[" + destFacility.facilityCode + "] " + destFacility.facilityName);
			} else {
				$('#destFacilityIdDT').text("[" + destFacility.facilityId + "] " + destFacility.facilityName);
			}
		}
		
		for (var i = 0; i < transferTypeData.length; i ++){
			if (transferTypeData[i].transferTypeId == $('#transferTypeId').val()){
				$('#transferTypeIdDT').text(transferTypeData[i].description);
				break;
			}
		}
		
		for (var i = 0; i < shipmentMethodData.length; i ++){
			if (shipmentMethodData[i].shipmentMethodTypeId == $('#shipmentMethodTypeId').val()){
				$('#shipmentMethodTypeIdDT').text(shipmentMethodData[i].description);
				break;
			}
		}
		
		if ($("#description").val()){
			$("#descriptionDT").text($("#description").val());
		}
		
		var originAddress = $("#originContactMechId").jqxDropDownList('getSelectedItem'); 
		var destAddress = $("#destContactMechId").jqxDropDownList('getSelectedItem'); 
		if (originAddress){
			$('#originContactMechDT').text(originAddress.label);
		}
		if (destAddress){
			$('#destContactMechDT').text(destAddress.label);
		}
		
		var partySelected = $("#carrierPartyId").jqxDropDownList('getSelectedItem'); 
		$('#carrierPartyIdDT').text(partySelected.label);
		
		if ($("#shipBeforeDate") != undefined && $("#shipBeforeDate").val() != '' && $("#shipBeforeDate") != null) {
			$("#shipBeforeDateDT").text(DatetimeUtilObj.formatFullDate($("#shipBeforeDate").jqxDateTimeInput('getDate')));
		} 
		if ($("#shipAfterDate") != undefined && $("#shipAfterDate").val() != '' && $("#shipAfterDate") != null) {
			$("#shipAfterDateDT").text(DatetimeUtilObj.formatFullDate($("#shipAfterDate").jqxDateTimeInput('getDate')));
		} else {
			$("#shipAfterDateDT").text('');
		}
		
		if ($("#tableProduct").length > 0){
			var totalValue = 0;
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
			for (var i in listProductSelected){
				var data = listProductSelected[i];
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				var seq = parseInt(i)+1;
				var newText = document.createTextNode(seq);
				newCell0.appendChild(newText);
				
				var newCell1 = newRow.insertCell(1);
				newText = document.createTextNode(data.productCode);
				newCell1.appendChild(newText);
				
				var newCell2 = newRow.insertCell(2);
				newText = document.createTextNode(data.productName);
				newCell2.appendChild(newText);
				
				var newCell3 = newRow.insertCell(3);
				if (data.description){
					newText = document.createTextNode(data.description);
				} else {
					newText = document.createTextNode("");
				}
				newCell3.appendChild(newText);
				
				var newCell4 = newRow.insertCell(4);
				newCell4.className = 'align-right';
				if (data.requireAmount && data.requireAmount == 'Y' && data.amountUomTypeId == 'WEIGHT_MEASURE') {
					newText = document.createTextNode(data.amountOnHandTotal + " (" + getUomDesc(data.weightUomId) + ")");
				} else {
					newText = document.createTextNode(data.quantityOnHandTotal + " (" + getUomDesc(data.quantityUomId) + ")");
				}
				newCell4.appendChild(newText);
				
				var newCell5 = newRow.insertCell(5);
				newText = document.createTextNode(getUomDesc(data.uomId));
				newCell5.appendChild(newText);
				
				var newCell6 = newRow.insertCell(6);
				newCell6.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.quantity));
				newCell6.appendChild(newText);
				
				var newCell7 = newRow.insertCell(7);
				newCell7.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.unitCost));
				newCell7.appendChild(newText);
				
				var newCell8 = newRow.insertCell(8);
				newCell8.className = 'align-right';
				if (data.unitCost && data.quantity){
					var lastPrice = parseFloat(data.unitCost.toString().replace(',', '.'));
					var quantity = parseFloat(data.quantity);
					value = lastPrice*parseFloat(quantity);
					totalValue = totalValue + value;
					if (value) {
						newText = document.createTextNode(formatnumber(value));
					} 
					newCell8.appendChild(newText);
				}
			}
			if (totalValue){
				var newRowTotal = tableRef.insertRow(tableRef.rows.length);
				var newCellTotal0 = newRowTotal.insertCell(0);
				newCellTotal0.colSpan = 8;
				newCellTotal0.className = 'align-right';
				newCellTotal0.style.fontWeight="bold";
				newCellTotal0.style.background="#f2f2f2";
				var str = uiLabelMap.OrderItemsSubTotal.toUpperCase();
				var newTextTotal = document.createTextNode(str);
				newCellTotal0.appendChild(newTextTotal);
				
				var newCellTotal8 = newRowTotal.insertCell(1);
				newCellTotal8.className = 'align-right';
				newCellTotal8.style.background="#f2f2f2";
				var newTextTotal = document.createTextNode(formatnumber(totalValue));
				newCellTotal8.appendChild(newTextTotal);
			}
		}
	}
	
	function finishCreateTransfer(){
		var listProducts = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var map = {};
				var data = listProductSelected[i];
				var key = 'row' + data.productId;
				var stringDate = null;
				var map = {};
		   		map['productId'] = data.productId;
		   		if (data.expiredDate){
		   			map['expireDate'] = data.expiredDate.getTime();
		   		}
		   		map['quantity'] = data.quantity;
		   		var requireAmount = data.requireAmount;
		   		if (requireAmount && requireAmount == 'Y') {
		   			map['weightUomId'] = data.uomId;
		   			map['quantityUomId'] = data.quantityUomId;
		   		} else {
		   			map['quantityUomId'] = data.uomId;
		   			map['weightUomId'] = data.weightUomId;
		   		}
		        listProducts.push(map);
			}
		}
		// check enough to warning
		var listProducts = JSON.stringify(listProducts);
		var mess = uiLabelMap.AreYouSureCreate;
		var shipBeforeDateTmp = null;
		var shipAfterDateTmp = null;
		if ($("#shipBeforeDate").val()){
			shipBeforeDateTmp = $("#shipBeforeDate").jqxDateTimeInput('getDate').getTime();
		}
		if ($("#shipAfterDate").val()){
			shipAfterDateTmp = $("#shipAfterDate").jqxDateTimeInput('getDate').getTime();
		}
		jOlbUtil.confirm.dialog(mess, function() {
			if (!btnClick){
				Loading.show('loadingMacro');
	        	setTimeout(function(){
				$.ajax({
					type: 'POST',
					url: 'createTransfer',
					async: false,
					data: {
						originFacilityId: originFacility.facilityId,
						destFacilityId: destFacility.facilityId,
						originContactMechId: $("#originContactMechId").val(),
						destContactMechId: $("#destContactMechId").val(),
						transferTypeId: $("#transferTypeId").val(),
						shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
						carrierPartyId: $("#carrierPartyId").val(),
						shipBeforeDate: shipBeforeDateTmp,
						shipAfterDate: shipAfterDateTmp,
						description: $("#description").jqxInput('val').split('\n').join(' '),
						listProducts: listProducts,
					},
					beforeSend: function(){
						$("#btnPrevWizard").addClass("disabled");
						$("#btnNextWizard").addClass("disabled");
						$("#loader_page_common").show();
					},
					success: function(data){
						viewTransferDetail(data.transferId);
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
				Loading.hide('loadingMacro');
	        	}, 500);
				btnClick = true;
			}
        }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
        	btnClick = false;
        });
	}
	
	function viewTransferDetail(transferId){
		if(acc){
			window.location.href = 'accViewDetailTransfer?transferId=' + transferId;
		}
		else window.location.href = 'viewDetailTransfer?transferId=' + transferId;
	}
	var initValidateForm = function(){
		
	};
	var reloadPages = function(){
		window.location.reload();
	};
	return {
		init: init,
		viewTransferDetail: viewTransferDetail,
		reloadPages: reloadPages,
	}
}());