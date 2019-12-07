$(function () {
	TransferDlvDetailOpenObj.init();
});
var TransferDlvDetailOpenObj = (function () {
	var btnClick = false;
	var init = function () {
	}
	
	var prepareExportTransferDelivery = function (deliveryId){
		window.location.href = "prepareExportTransferDelivery?deliveryId="+ deliveryId;
	}
	
	var prepareReceiveTransferDelivery = function (deliveryId){
		window.location.href = "prepareReceiveTransferDelivery?deliveryId="+ deliveryId;
	}
	
	var prepareExportAndReceiveTransferDelivery = function (deliveryId){
		window.location.href = "prepareExportAndReceiveTransferDelivery?deliveryId="+ deliveryId;
	}
	
	var exportPDFTransferDocument = function (deliveryId){
		window.open("exportPDFTransferDeliveryDocument?deliveryId="+ deliveryId, "_blank");
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
		prepareExportTransferDelivery: prepareExportTransferDelivery,
		prepareReceiveTransferDelivery: prepareReceiveTransferDelivery,
		prepareExportAndReceiveTransferDelivery: prepareExportAndReceiveTransferDelivery,
		cancelDelivery: cancelDelivery,
		exportPDFTransferDocument: exportPDFTransferDocument,
		approveDelivery: approveDelivery,
	};
}());