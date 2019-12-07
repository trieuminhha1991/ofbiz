$(function(){
	ReceiveReturnTotalObj.init();
});
var ReceiveReturnTotalObj = (function() {
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
				if (listProductSelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
					return false;
				}
				if (facilitySelected == null){
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseFacility);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureYouWantToImport, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishReceiveReturn();
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
		if (facilitySelected) {
			if (facilitySelected.facilityCode){
				$("#facilityDT").text('['+facilitySelected.facilityCode +'] ' + facilitySelected.facilityName);
			} else {
				$("#facilityDT").text('['+facilitySelected.facilityId +'] ' + facilitySelected.facilityName);
			}
		}
		if ($("#tableProduct").length > 0){
			var totalValue = 0;
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
			for (var i in listProductSelected){
				var data = listProductSelected[i];
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				var newText = document.createTextNode(parseInt(i) + 1);
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
					newText = document.createTextNode(getUomDesc(data.weightUomId));
				} else {
					newText = document.createTextNode(getUomDesc(data.quantityUomId));
				}
				newCell4.appendChild(newText);
				
				var newCell5 = newRow.insertCell(5);
				newCell5.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.returnQuantity));
				newCell5.appendChild(newText);
				
				var newCell6 = newRow.insertCell(6);
				newCell6.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.quantity));
				newCell6.appendChild(newText);
				
				if (!hidePrice){
					var newCell7 = newRow.insertCell(7);
					newCell7.className = 'align-right';
					var u = data.returnPrice;
					if (locale && locale === 'vi' && typeof(u) === 'string'){
						u = data.returnPrice.toString().replace('.', '');
						u = u.replace(',', '.');
					}
					newText = document.createTextNode(formatnumber(u));
					newCell7.appendChild(newText);
					
					var newCell8 = newRow.insertCell(8);
					newCell8.className = 'align-right';
					if (data.returnPrice && data.quantity >= 0){
						var u = data.returnPrice;
						if (locale && locale === 'vi' && typeof(u) === 'string'){
							u = data.returnPrice.toString().replace('.', '');
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
				
			}
			if (!hidePrice){
				if (totalValue >= 0){
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
		if ($("#tableProductDetail").length > 0){
			var totalValue = 0;
			$('#tableProductDetail tbody').empty();
			var tableRef = document.getElementById('tableProductDetail').getElementsByTagName('tbody')[0];
			
			if ($.isEmptyObject(listProductMap)){
				var newRow = tableRef.insertRow(tableRef.rows.length);
				var newCell0 = newRow.insertCell(0);
				newCell0.colSpan = 7
				newCell0.className = 'align-center';
				newText = document.createTextNode(uiLabelMap.NoDataToDisplay);
				newCell0.appendChild(newText);
				
			}
			for (var x in listProductMap){
				var listData = listProductMap[x];
				for (var i in listData){
					var data = listData[i];
					if (data.quantity && data.quantity > 0){
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
						newText = document.createTextNode(formatnumber(data.quantity));
						newCell3.appendChild(newText);
						
						var exp = data.expireDate;
						if (exp != null && exp != undefined){
							var newCell4 = newRow.insertCell(4);
							newCell4.className = 'align-right';
							newText = document.createTextNode(DatetimeUtilObj.getFormattedDate(new Date(exp)));
							newCell4.appendChild(newText);
						} else {
							var newCell4 = newRow.insertCell(4);
							newText = document.createTextNode("");
							newCell4.appendChild(newText);
						}
						
						var mnf = data.datetimeManufactured;
						if (mnf != null && mnf != undefined){
							var newCell5 = newRow.insertCell(5);
							newCell5.className = 'align-right';
							newText = document.createTextNode(DatetimeUtilObj.getFormattedDate(new Date(mnf)));
							newCell5.appendChild(newText);
						} else {
							var newCell5 = newRow.insertCell(5);
							newText = document.createTextNode("");
							newCell5.appendChild(newText);
						}
						var newCell6 = newRow.insertCell(6);
						if (data.lotId){
							newText = document.createTextNode(data.lotId);
							newCell6.appendChild(newText);
						}
					}
				}
			}
		}
	}
	
	function finishReceiveReturn(){
		var listProducts = [];
		var listProductAttributes = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var data = listProductSelected[i];
				var map = {};
				var items = listProductMap[data.productId];
				if (items){
					if (items.length > 0){
						for (var x in items){
							var myObj = items[x];
							for(var keys in myObj){
								if (myObj[keys] === null || myObj[keys] === "" || myObj[keys] === "null" || myObj[keys] === undefined){
									delete myObj[keys]
								}
							}
							if (myObj.quantity > 0){
								listProductAttributes.push(myObj);
							}
						}
					}
				}
		   		map['productId'] = data.productId;
		   		map['quantity'] = data.quantity;
		   		map['returnId'] = data.returnId;
		   		map['returnItemSeqId'] = data.returnItemSeqId;
		        listProducts.push(map);
			}
		}
		listProducts = JSON.stringify(listProducts);
		listProductAttributes = JSON.stringify(listProductAttributes);
    	var url = "receiveProductFromCustomerReturn";
    	$.ajax({	
			 type: "POST",
			 url: url,
			 data: {
				 returnId: returnId,
				 facilityId: facilitySelected.facilityId,
				 listReturnItems: listProducts,
				 listProductAttributes: listProductAttributes,
			 },
			 dataType: "json",
			 async: false,
			 success: function(data){
				window.location.href = "viewReturnOrder?returnId="+returnId;
			 },
			 error: function(response){
				window.location.href = "viewReturnOrder?returnId="+returnId;
			 }
	 		}).done(function(data) {
  		});
	}
	
	function viewReturnDetail(returnId){
		window.location.href = 'viewReturnOrder?returnId=' + returnId;
	}
	var initValidateForm = function(){
		
	};
	
	return {
		init: init,
	}
}());