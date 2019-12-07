$(function(){
	transferEditObjTotal.init();
});
var transferEditObjTotal = (function() {
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
				var validate = TransferEditInfoObj.getValidator();
				if(!validate) return false;
					// check form valid
					$('#containerNotify').empty();
					var checkAllItemQuantity = isAllItemQuantityNull();
					if (listProductSelected.length <= 0 || checkAllItemQuantity){
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
	            		finishEditTransfer();
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
		if (transfer.transferType){
			$("#transferTypeDT").text($("#transferTypeId").text());
		}
		if (originFacilityData){
			if (originFacilityData.facilityCode){
				$("#originFacilityDT").text("[" + originFacilityData.facilityCode + "] " + originFacilityData.facilityName);
			} else {
				$("#originFacilityDT").text("[" + originFacilityData.facilityId + "] " + originFacilityData.facilityName);
			}
		}
		if (destFacilityData.facilityCode){
			if (destFacilityData.facilityName){
				$("#destFacilityDT").text("[" + destFacilityData.facilityCode + "] " + destFacilityData.facilityName);
			} else {
				$("#destFacilityDT").text("[" + destFacilityData.facilityId + "] " + destFacilityData.facilityName);
			}
		}
		
		$("#descriptionDT").text($("#description").jqxInput('val'));
		$("#shipAfterDateDT").text($("#shipAfterDate").jqxDateTimeInput('val'));
		$("#shipBeforeDateDT").text($("#shipBeforeDate").jqxDateTimeInput('val'));
		
		if ($("#tableProduct").length > 0){
			var totalValue = 0;
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
			
			var finalListProductSelected = [];
			
			for(var i in listProductSelected){
				if((listProductSelected[i].statusId != "TRANS_ITEM_CANCELLED") && (listProductSelected[i].statusId != "TRANS_ITEM_REJECTED")){
					finalListProductSelected.push(listProductSelected[i]);
				}
			}
			
			for (var i in finalListProductSelected){
				var data = finalListProductSelected[i];
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
				newText = document.createTextNode(getUomDescription(data.quantityUomId));
				newCell3.appendChild(newText);
				
				var newCell4 = newRow.insertCell(4);
				newCell4.className = 'align-right';
				if(data.requireAmount && data.requireAmount == 'Y'){
					newText = document.createTextNode(formatnumber(data.amount));
				}else{
					newText = document.createTextNode(formatnumber(data.quantity));
				}
				newCell4.appendChild(newText);
			}
		}
	}
	
	function finishEditTransfer(){
		var listProducts = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var data = listProductSelected[i];
				var map = {};
				map['statusId'] = data.statusId;
				map['sequenceId'] = data.transferItemSeqId;
		   		map['productId'] = data.productId;
		   		map['quantity'] = data.quantity;
		   		map['weightUomId'] = data.weightUomId;
		   		map['uomId'] = data.quantityUomId;
		   		map['amount'] = data.amount;
		        listProducts.push(map);
			}
		}
		var listProducts = JSON.stringify(listProducts);
		var dataMap = {
			transferId : transferId,
			listProducts: listProducts,
			shipAfterDate : $("#shipAfterDate").jqxDateTimeInput('getDate').getTime(),
			shipBeforeDate : $("#shipBeforeDate").jqxDateTimeInput('getDate').getTime(),
			description: $("#description").jqxInput('val').split('\n').join(' '),
		};
		$.ajax({
			type: 'POST',
			url: 'updateTransferAndItem',
			async: false,
			data: dataMap,
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
	}
	
	function viewTransferDetail(transferId){
		window.location.href = 'viewDetailTransfer?transferId=' + transferId;
	}
	
	var isAllItemQuantityNull = function(){
		var count = 0;
		for(var i = 0; i < listProductSelected.length; i++){
			if((listProductSelected[i].quantity == 0) || (listProductSelected[i].statusId == "TRANS_ITEM_CANCELLED")){
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