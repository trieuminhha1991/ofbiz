$(function(){
	packEditObjTotal.init();
});
var packEditObjTotal = (function() {
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
				var validate = PackEditInfoObj.getValidator();
				if(!validate) return false;
					// check form valid
					$('#containerNotify').empty();
                	listProductSelected=PackEditObj.getProductGrid().getGridObj().jqxGrid('getrows');
					if (listProductSelected.length <= 0 ){
						jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
						return false;
					}
					showConfirmPage();
				
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureEdit, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishEditPack();
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
	function showConfirmPage(){
		if ($("#description").val()) {
            $("#descriptionDT").text($("#description").val());
        }
        var customerDt = $("#customerId").jqxDropDownButton('getContent');
        if (customerDt) {
            $('#customerIdDT').text(customerDt.text());
        }

        var customerDt = $("#destContactMechId").jqxDropDownButton('getContent');
        if (customerDt) {
            $('#destContactMechDT').text(customerDt.text());
        }

        if ($("#shipBeforeDate") != undefined && $("#shipBeforeDate").val() != '' && $("#shipBeforeDate") != null) {
            $("#shipBeforeDateDT").text(DatetimeUtilObj.formatFullDate($("#shipBeforeDate").jqxDateTimeInput('getDate')));
        }
        if ($("#shipAfterDate") != undefined && $("#shipAfterDate").val() != '' && $("#shipAfterDate") != null) {
            $("#shipAfterDateDT").text(DatetimeUtilObj.formatFullDate($("#shipAfterDate").jqxDateTimeInput('getDate')));
        } else {
            $("#shipAfterDateDT").text('');
        }

        if ($("#tableProduct").length > 0) {
            var totalValue = 0;
            $('#tableProduct tbody').empty();
            var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
            var finalListProductSelected=[]
            for(var i in listProductSelected){
                if((listProductSelected[i].statusId != "PACK_ITEM_CANCELLED") && (listProductSelected[i].statusId != "PACK_ITEM_REJECTED")){
                    finalListProductSelected.push(listProductSelected[i]);
                }
            }
            for (var i in finalListProductSelected) {
                var data = finalListProductSelected[i];
                var newRow = tableRef.insertRow(tableRef.rows.length);
                var newCell0 = newRow.insertCell(0);
                var newText = document.createTextNode(i);
                newCell0.appendChild(newText);

                var newCell1 = newRow.insertCell(1);
                newText = document.createTextNode(data.productCode);
                newCell1.appendChild(newText);

                var newCell2 = newRow.insertCell(2);
                newText = document.createTextNode(data.deliveryId);
                newCell2.appendChild(newText);

                var newCell2 = newRow.insertCell(3);
                newText = document.createTextNode(data.productName);
                newCell2.appendChild(newText);

                var newCell3 = newRow.insertCell(4);
                if (data.description) {
                    newText = document.createTextNode(data.description);
                } else {
                    newText = document.createTextNode("");
                }
                newCell3.appendChild(newText);

                var newCell4 = newRow.insertCell(5);
                newCell4.className = 'align-right';
                newText = document.createTextNode(data.quantity + " (" + getUomDescription(data.quantityUomId) + ")");
                newCell4.appendChild(newText);

                var newCell5 = newRow.insertCell(6);
                newText = document.createTextNode(getUomDescription(data.quantityUomId));
                newCell5.appendChild(newText);

            }
            if (totalValue) {
                var newRowTotal = tableRef.insertRow(tableRef.rows.length);
                var newCellTotal0 = newRowTotal.insertCell(0);
                newCellTotal0.colSpan = 8;
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
	
	function finishEditPack(){
		var listProducts = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var data = listProductSelected[i];
				var map = {};
				map['statusId'] = data.statusId;
				map['sequenceId'] = data.packItemSeqId;
		   		map['productId'] = data.productId;
		   		map['quantity'] = data.quantity;
		   		map['weightUomId'] = data.weightUomId;
		   		map['uomId'] = data.quantityUomId;
		   		map['amount'] = data.amount;
		   		map['deliveryId']=data.deliveryId;
		   		map['deliveryItemSeqId']=data.deliveryItemSeqId;
		        listProducts.push(map);
			}
		}
		var listProducts = JSON.stringify(listProducts);
		var dataMap = {
			packId : packId,
			listProducts: listProducts,
			shipAfterDate : $("#shipAfterDate").jqxDateTimeInput('getDate').getTime(),
			shipBeforeDate : $("#shipBeforeDate").jqxDateTimeInput('getDate').getTime(),
			/*description: $("#description").jqxInput('val').split('\n').join(' '),*/
		};
		$.ajax({
			type: 'POST',
			url: 'updatePackAndItem',
			async: false,
			data: dataMap,
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(data){
				viewPackDetail(data.packId);
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
	
	function viewPackDetail(packId){
		window.location.href = 'viewDetailPack?packId=' + packId;
	}
	
	var isAllItemQuantityNull = function(){
		var count = 0;
		for(var i = 0; i < listProductSelected.length; i++){
			if((listProductSelected[i].quantity == 0) || (listProductSelected[i].statusId == "PACK_ITEM_CANCELLED")){
				count++;
			}
		}
		if(count == listProductSelected.length){
			return true;
		}
		return false;
	}
	
	return {
		init: init,
	}
}());