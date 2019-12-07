$(function () {
	SalesDlvDetailObj.init();
});
var SalesDlvDetailObj = (function () {
	var init = function () {
	}
	
	var viewDetailOrder = function (orderId){
		window.open("viewOrder?orderId="+ orderId, "_blank");
	}
	
	var viewShipmentPurchaseDis = function (shipmentId){
		window.open("viewDetailShipmentPurchDis?shipmentId="+ shipmentId, "_blank");
	}
	
	return {
		init: init,
		viewDetailOrder: viewDetailOrder,
	};
}());