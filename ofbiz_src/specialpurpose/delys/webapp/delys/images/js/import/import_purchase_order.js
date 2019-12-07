$(document).ready(function(){
	$("form[name='quickaddform']").attr("action", "addItemToCart");
	$("form[name='bulkworkaddform']").attr("action", "addItemToCart");
	$("form[name='cartform']").attr("action", "modifyCurrentCart");
	$("form[name='checkoutsetupform']").attr("action", "finalizePurchaseOrder");
	$("form[name='termform']").attr("action", "addPurchaseOrderTerm");
	$("form[name='OrderCancel']").attr("action", "changePOStatus");
	$("form[name='OrderApproveOrder']").attr("action", "changePOStatus");
	
	$("#finalize").attr("href", "/delys/control/finalizePurchaseOrder?finalizeMode=purchase&amp;finalizeReqCustInfo=false&amp;finalizeReqShipInfo=false&amp;finalizeReqOptions=false&amp;finalizeReqPayInfo=false");
	$("#emptyCart").attr("href", "/delys/control/emptyCurrentCart");
	$("#createOrder").attr("href", "/delys/control/processPurchaseOrder");
	
});