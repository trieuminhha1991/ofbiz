$(function(){
	TestTotalObj.init();
});
var TestTotalObj = (function() {
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
				var resultValidate = !EventProduct.getValidator().validate();
				if(resultValidate) return false;
				var check = true;
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
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishCreateEvent();
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
		if ($("#tableProduct").length > 0){
			var val = $("#productEventType").jqxDropDownList('val');
			var it = $("#productEventType").jqxDropDownList('getItemByValue', val);
			$("#productEventTypeDT").text(it.label);
			$("#eventCodeDT").text($("#eventCode").jqxInput('val'));
			$("#eventNameDT").text($("#eventName").jqxInput('val'));
			if (agreementSelected){
				$("#agreementDT").text(agreementSelected.agreementId);
			}
			if (packingListSelected){
				$("#packingListDT").text(packingListSelected.packingListNumber);
			}
			if ($("#executedDate").jqxDateTimeInput('val')){
				var x = $("#executedDate").jqxDateTimeInput('val');
				$("#executedDateDT").text(x);
			}
			if ($("#completedDate").jqxDateTimeInput('val')){
				var x = $("#completedDate").jqxDateTimeInput('val');
				$("#completedDateDT").text(x);
			}
			
			$("#descriptionDT").text($("#description").jqxInput('val'));
			
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
				newText = document.createTextNode(getUomDesc(data.quantityUomId));
				newCell3.appendChild(newText);
				
				var newCell4 = newRow.insertCell(4);
				newCell4.className = 'align-right';
				if (data.quantity){
					newText = document.createTextNode(formatnumber(data.quantity));
					newCell4.appendChild(newText);
				} else {
					newText = document.createTextNode("0");
					newCell4.appendChild(newText);
				}
				
				var newCell5 = newRow.insertCell(5);
				newCell5.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.createQuantity));
				newCell5.appendChild(newText);
			}
		}
	}
	
	function finishCreateEvent(){
		var listProducts = [];
		var listProductAttributes = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var data = listProductSelected[i];
				var map = {};
				var qty = data.createQuantity;
		   		map['productId'] = data.productId;
		   		map['quantity'] = qty;
		   		map['registeredQuantity'] = data.quantity;
		        listProducts.push(map);
			}
		}
		listProducts = JSON.stringify(listProducts);
		var data = {
			eventTypeId: $("#productEventType").jqxDropDownList('val'),
			eventCode: $("#eventCode").jqxInput('val'),
			eventName: $("#eventName").jqxInput('val'),
			description: $("#description").jqxInput('val'),
			agreementId: agreementSelected.agreementId,
			packingListId: packingListSelected.packingListId,
			listProducts: listProducts,
		};
		var x = $("#executedDate").jqxDateTimeInput('getDate');
		if (x){
			data.executedDate = x.getTime();
		}
			
		var y = $("#completedDate").jqxDateTimeInput('getDate');
		if (y){
			data.completedDate = y.getTime();
		}
			
    	var url = "createProductEvent";
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
					 return false;
				 }
				 if (res.eventId){
					 window.location.href = "getDetailQualityTestEvent?eventId="+res.eventId;
				 }
			 },
			 error: function(response){
			 }
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