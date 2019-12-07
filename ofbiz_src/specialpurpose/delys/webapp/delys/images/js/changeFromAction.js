$(document).ready(function(){
	if ($("form[name='selectAllForm']")){
		$("form[name='selectAllForm']").attr("action", "addOrderItemToReturn");
	}
	if ($("form[name='orderToReturns']")){
		$("form[name='orderToReturns']").attr("action", "getListOrderItemsToReturn");
	}
	if ($("form[name='returnItems']")){
		$("form[name='returnItems']").attr("action", "addOrderItemToReturn");
	}
	if ($("form[name='acceptReturn']")){
		$("form[name='acceptReturn']").attr("action", "updateReturnStatus");
	}
	if ($("form[name='ListReturnItems']")){
		$("form[name='ListReturnItems']").attr("action", "updateReturnItemInfo");
	}
	if ($("form[name='ListOrderItemToReceive']")){
		$("form[name='ListOrderItemToReceive']").attr("action", "receiveInventoryItem");
	}
	if ($("form[name='quickShipOrder']")){
		$("form[name='quickShipOrder']").attr("action", "quickShipOrderLogistics");
	}
});