$(function(){
	smtObj.init();
});
var smtObj = (function() {
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
		
	};
	
	function viewShipmentDetail(shipmentId){
		window.location.href = 'viewDetailShipmentPurchDis?shipmentId=' + shipmentId;
	}
	
	function viewDeliveryDetail(deliveryId){
		window.location.href = 'deliverySalesDeliveryDetailDis?deliveryId=' + deliveryId;
	}
	
	function viewOrderDetail(orderId){
		window.location.href = 'viewOrder?orderId=' + orderId;
	}
	
	function prepareCreatePurchDisShipment(orderId){
		window.location.href = 'prepareCreatePurchDisShipment';
	}
	
	var initValidateForm = function(){
		
	};
	
	return {
		init: init,
		viewShipmentDetail: viewShipmentDetail,
		viewDeliveryDetail: viewDeliveryDetail,
		viewOrderDetail: viewOrderDetail,
		prepareCreatePurchDisShipment: prepareCreatePurchDisShipment,
	}
}());