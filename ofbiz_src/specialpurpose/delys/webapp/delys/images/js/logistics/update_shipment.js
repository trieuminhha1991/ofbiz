$(document).ready(function(){
	if ($("form[name='findOrderItems']")){
		$("form[name='findOrderItems']").attr("action", "editShipmentPlan");	
	}
	if ($("form[name='addToShipmentPlan']")){
		$("form[name='addToShipmentPlan']").attr("action", "addOrderItemToShipmentPlan");	
	}
	if ($("form[name^='listShipmentPlan'][name$='deleteLink']")){
		$("form[name^='listShipmentPlan'][name$='deleteLink']").attr("action", "removeOrderShipment");	
	}
	if ($("form[name='additemsfromorder']")){
		$("form[name='additemsfromorder']").attr("action", "addItemsFromOrder");	
	}
	if ($("form[name^='createShipmentPackageContentForm']")){
		$("form[name^='createShipmentPackageContentForm']").attr("action", "createShipmentItemPackage");	
	}
	if ($("form[name^='deleteShipmentItemPackageContent']")){
		$("form[name^='deleteShipmentItemPackageContent']").attr("action", "deleteShipmentItemPackage");	
	}
	if ($("form[name^='deleteShipmentItem']")){
		$("form[name^='deleteShipmentItem']").attr("action", "removeShipmentItem");	
	}
	if ($("form[name^='createShipmentPackageContentForm']")){
		$("form[name^='createShipmentPackageContentForm']").attr("action", "createShipmentItemPackage");	
	}
	if ($("#OrderItems")){
		$("#OrderItems").attr("action", "issueOrderItems");	
	}
	if ($("#OrderItemShipGrpInvRes")){
		$("#OrderItemShipGrpInvRes").attr("action", "issueOrderItemShipGrpInvRes");	
	}
	if ($("#ReceiveInventory")){
		var oldHref = $("#ReceiveInventory").attr("href");	
		var newHref = oldHref.replace("ReceiveInventory", "receiveInventory");
		$("#ReceiveInventory").attr("href", newHref);
	}
});