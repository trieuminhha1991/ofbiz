$(function() {
	supReturnEditTmp.init();
});

var supReturnEditTmp = (function() {
	var btnClick = false;
	var init = function() {
		initElement();
		initEvent();
		initElementComplex();
		initValidateForm();
	};

	var initElement = function() {
	};

	var initElementComplex = function() {
	};

	var initEvent = function() {

		$("#fuelux-wizard").ace_wizard().on("change", function(e, info) {
			if (info.step == 1 && (info.direction == "next")) {
				// check form valid
				$("#containerNotify").empty();
				var resultValidate = !supReturnInfo.getValidator().validate();
				if (resultValidate)
					return false;
				if (listProductSelected.length <= 0) {
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on("finished", function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureUpdate, function() {
				if (!btnClick) {
					Loading.show("loadingMacro");
					setTimeout(function() {
						finishCreateSupReturn(listProductSelected);
						Loading.hide("loadingMacro");
					}, 500);
					btnClick = true;
				}
			}, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
	        	btnClick = false;
	        });
		}).on("stepclick", function(e) {
			// prevent clicking on steps
		});
	};

	var finishCreateSupReturn = function(listProductSelected) {
		var supplier = partyId;
		var currencyUomId = currencyUomId;
		var description = $("#description").jqxInput("val").split("\n").join(" ")
		updateNewReturnSupplier(supplier, currencyUomId, description, listProductSelected);
	};

	function showConfirmPage() {
		var supSelectedId = $("#toPartyId").val();
		$("#supplierIdDT").text(toPartyName);
		$("#currencyUomIdDT").text(currencyUomId);
		if (facilitySelected){
			if (facilitySelected.facilityCode){
				$("#facilityIdDT").text("[" + facilitySelected.facilityCode + "] " + facilitySelected.facilityName);
			} else {
				$("#facilityIdDT").text("[" + facilitySelected.facilityId + "] " + facilitySelected.facilityName);
			}
		}
		if ($("#description").val()) {
			$("#descriptionDT").text($("#description").val());
		}
		$("#entryDateDT").text(DatetimeUtilObj.formatToMinutes(new Date(entryDate)));
		
		if ($("#tableProduct").length > 0){
			var totalValue = 0;
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
			for (var i in listProductSelected){
				var data = listProductSelected[i];
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				var newText = document.createTextNode((parseInt(i) + 1).toString());
				newCell0.appendChild(newText);
				
				var newCell1 = newRow.insertCell(1);
				newText = document.createTextNode(data.productCode);
				newCell1.appendChild(newText);
				
				var newCell2 = newRow.insertCell(2);
				var element = document.createElement("div");
				element.innerHTML = data.productName;
				newCell2.appendChild(element);
				
				var newCell3 = newRow.insertCell(3);
				element = document.createElement("div");
				
				if (data.returnReasonId){
					for(var i in returnReasonData){
						if(data.returnReasonId == returnReasonData[i].returnReasonId){
							element.innerHTML = returnReasonData[i].description;
						}
					}
				} else {
					element.innerHTML = "";
				}
				newCell3.appendChild(element);
				
				var newCell4 = newRow.insertCell(4);
				newCell4.className = 'align-right';
				if (data.requireAmount && data.requireAmount == 'Y' && data.amountUomTypeId == 'WEIGHT_MEASURE') {
					newText = document.createTextNode(getUomDescription(data.weightUomId));
				} else {
					newText = document.createTextNode(getUomDescription(data.quantityUomId));
				}
				newCell4.appendChild(newText);
				
				var newCell5 = newRow.insertCell(5);
				newCell5.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.quantity));
				newCell5.appendChild(newText);
				
				var newCell6 = newRow.insertCell(6);
				newCell6.className = 'align-right';
				var u = data.returnPriceTmp;
//				if (locale && locale === 'vi' && typeof(u) === 'string'){
//					u = data.returnPriceTmp.toString().replace('.', '');
//					u = u.replace(',', '.');
//				}
				newText = document.createTextNode(formatnumber(u));
				newCell6.appendChild(newText);
				
				var newCell7 = newRow.insertCell(7);
				newCell7.className = 'align-right';
				if (data.returnPriceTmp >= 0 && data.quantity >= 0){
					var u = data.returnPriceTmp;
//					if (locale && locale === 'vi' && typeof(u) === 'string'){
//						u = data.returnPriceTmp.toString().replace('.', '');
//						u = u.replace(',', '.');
//					}
					var lastPrice = parseFloat(u.toString());
					var v = data.quantity;
//					if (locale && locale === 'vi' && typeof(v) === 'string'){
//						v = v.toString().replace('.', '');
//						v = v.replace(',', '.');
//					}
					var quantity = parseFloat(v.toString());
					
					value = lastPrice*parseFloat(quantity);
					totalValue = totalValue + value;
					if (value >= 0) {
						newText = document.createTextNode(formatnumber(value));
					} 
					newCell7.appendChild(newText);
				}
			}
			if (totalValue >= 0){
				var newRowTotal = tableRef.insertRow(tableRef.rows.length);
				var newCellTotal0 = newRowTotal.insertCell(0);
				newCellTotal0.colSpan = 7;
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

	var updateNewReturnSupplier = function(supplier, currencyUomId, description, listProductSelected) {
		var listItemAdds = [];
		var listItemUpdate = [];
		var listItemRemove = [];
		for (var x in listProductSelected){
			var data = listProductSelected[x];returnReasonId
			var productId = data.productId;
			var quantityStr = data.quantity.toString();
			var returnReasonId = data.returnReasonId;
			var returnPriceStr = data.returnPriceTmp.toString();
			var quantityUomId = data.quantityUomId;
			var obj = {
					productId: productId,	
					quantity: quantityStr, 
					returnPrice: returnPriceStr, 
					quantityUomId: quantityUomId,
					returnId: returnId,
					returnReasonId: returnReasonId,
			};
			var returnItemSeqId = data.returnItemSeqId;
			if (returnItemSeqId){
				obj.returnItemSeqId = returnItemSeqId;
				listItemUpdate.push(obj);
			} else {
				listItemAdds.push(obj);
			}
		}
		for (var x in returnItemInitData) {
			var data1 = returnItemInitData[x];
			var check = false;
			for (var y in listProductSelected){
				var data2 = listProductSelected[y];
				if (data2.productId == data1.productId){
					check = true;
					break;
				}
			}
			if (!check) {
				var obj = {
					returnItemSeqId: data1.returnItemSeqId,	
					returnId: data1.returnId, 
				};
				listItemRemove.push(obj);
			}
		}
		var data = {};
		if (listItemAdds.length > 0){
			listItemAdds = JSON.stringify(listItemAdds);
			data.listReturnItemAdds = listItemAdds;
		}
		if (listItemUpdate.length > 0){
			listItemUpdate = JSON.stringify(listItemUpdate);
			data.listReturnItemUpdate = listItemUpdate;
		}
		if (listItemRemove.length > 0){
			listItemRemove = JSON.stringify(listItemRemove);
			data.listReturnItemRemove = listItemRemove;
		}
		data.returnId = returnId;
		if (facilitySelected){
			data.destinationFacilityId = facilitySelected.facilityId;
		}
		if (description){
			data.description = description;
		}
		$.ajax({
			url : "updateReturnSupplierTotal",
			type : "POST",
			data : data,
			dataType : "json",
			success : function(data) {
				window.location.href = "viewGeneralReturnSupplier?returnId=" + returnId;
			}
			}).done(function(data) {
		});
	};

	var initValidateForm = function() {
		var mapRules = [];
		var extendRules = [];
	};

	return {
		init : init,
	};
}());