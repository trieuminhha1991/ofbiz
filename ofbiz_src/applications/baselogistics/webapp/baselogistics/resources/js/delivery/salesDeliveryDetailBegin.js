$(function () {
	SalesDlvDetailOpenObj.init();
});
var SalesDlvDetailOpenObj = (function () {
	var btnClick = false;
	var init = function () {
	}
	
	var prepareReceivePurchaseDistributor = function (deliveryId){
		window.location.href = "prepareCreatePurchDisShipment?deliveryId="+ deliveryId;
	}
	
	var prepareUpdateExportedSalesDelivery = function (deliveryId){
		window.location.href = "prepareUpdateExportedSalesDelivery?deliveryId="+ deliveryId;
	}
	
	var prepareUpdateDeliveredSalesDelivery = function (deliveryId){
		window.location.href = "prepareUpdateDeliveredSalesDelivery?deliveryId="+ deliveryId;
	}
	
	var exportPDFDeliveryDocument = function (deliveryId){
		window.open("deliveryAndExport.pdf?deliveryId="+ deliveryId, "_blank");
	}
	
	var exportPDFStockOutDocument = function (deliveryId){
		window.open("deliveryUnitPrice.pdf?deliveryId="+ deliveryId, "_blank");
	}
	
	var cancelDelivery = function (deliveryId){
		jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCancel, function() {
			if (!btnClick){
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		$.ajax({
            			url: "changeDeliveryStatus",
            			type: "POST",
            			data: {
            				deliveryId: deliveryId,
            				statusId: "DLV_CANCELLED",
            			},
            			async: false,
            			success: function (res) {
            				if (!res._ERROR_MESSAGE_) {
            					window.location.reload();
            				} else {
            					jOlbUtil.alert.error(uiLabelMap.UpdateError + ": " + res._ERROR_MESSAGE_);
            					return false;
            				}
            			}
            		});
	            	Loading.hide('loadingMacro');
            	}, 300);
            	btnClick = true;
			} 
        }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
        	btnClick = false;
        });
	}
	
	var approveDelivery = function (deliveryId){
		jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureApprove, function() {
			if (!btnClick){
				Loading.show('loadingMacro');
				setTimeout(function(){
					$.ajax({
						url: "changeDeliveryStatus",
						type: "POST",
						data: {
							deliveryId: deliveryId,
							statusId: "DLV_APPROVED",
						},
						async: false,
						success: function (res) {
							if (!res._ERROR_MESSAGE_) {
								window.location.reload();
							} else {
								jOlbUtil.alert.error(uiLabelMap.UpdateError + ": " + res._ERROR_MESSAGE_);
								return false;
							}
						}
					});
					Loading.hide('loadingMacro');
				}, 300);
				btnClick = true;
			} 
		}, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
			btnClick = false;
		});
	}
	
	return {
		init: init,
		prepareUpdateExportedSalesDelivery: prepareUpdateExportedSalesDelivery,
		cancelDelivery: cancelDelivery,
		approveDelivery: approveDelivery,
		exportPDFStockOutDocument: exportPDFStockOutDocument,
		exportPDFDeliveryDocument: exportPDFDeliveryDocument,
		prepareUpdateDeliveredSalesDelivery: prepareUpdateDeliveredSalesDelivery,
		prepareUpdateExportedSalesDelivery: prepareUpdateExportedSalesDelivery,
		prepareReceivePurchaseDistributor: prepareReceivePurchaseDistributor,
	};
}());