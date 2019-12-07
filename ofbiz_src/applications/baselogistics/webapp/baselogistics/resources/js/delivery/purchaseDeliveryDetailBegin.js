$(function () {
	PurchaseDlvDetailOpenObj.init();
});
var PurchaseDlvDetailOpenObj = (function () {
	var btnClick = false;
	var init = function () {
	}
	
	var prepareReceivePurchaseDelivery = function (deliveryId){
		window.location.href = "prepareReceivePurchaseDelivery?deliveryId="+ deliveryId;
	}
	
	var exportPDFReceiptDocument = function (deliveryId){
		window.open("exportPDFReceiptDocument?deliveryId="+ deliveryId, "_blank");
	}
	
	var exportPDFStockInDocument = function (deliveryId){
		window.open("exportPDFStockInDocument?deliveryId="+ deliveryId, "_blank");
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
	
	var updateExportedPurchaseDelivery = function (deliveryId){
		jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureUpdate, function() {
			if (!btnClick){
				Loading.show('loadingMacro');
				setTimeout(function(){
					$.ajax({
						url: "changeDeliveryStatus",
						type: "POST",
						data: {
							deliveryId: deliveryId,
							statusId: "DLV_EXPORTED",
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
		prepareReceivePurchaseDelivery: prepareReceivePurchaseDelivery,
		cancelDelivery: cancelDelivery,
		exportPDFReceiptDocument: exportPDFReceiptDocument,
		exportPDFStockInDocument: exportPDFStockInDocument,
		updateExportedPurchaseDelivery: updateExportedPurchaseDelivery,
		approveDelivery: approveDelivery,
	};
}());