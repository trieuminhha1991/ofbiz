$(document).ready(function(){
	switch($("select[name='shipmentTypeId']").val()) {
	    case "SALES_SHIPMENT":
	        $("input[name='orderTypeId']").val("SALES_ORDER");
	        break;
	    case "PURCHASE_SHIPMENT":
	    	$("input[name='orderTypeId']").val("PURCHASE_ORDER");
	        break;
	    case "PURCHASE_RETURN":
	    	$("input[name='orderTypeId']").val("PURCHASE_ORDER");
	        break;
	    case "SALES_RETURN":
	    	$("input[name='orderTypeId']").val("SALES_ORDER");
	        break; 
	}
	$("select[name='shipmentTypeId']").change(function(){
		switch($("select[name='shipmentTypeId']").val()) {
	    case "SALES_SHIPMENT":
	        $("input[name='orderTypeId']").val("SALES_ORDER");
	        break;
	    case "PURCHASE_SHIPMENT":
	    	$("input[name='orderTypeId']").val("PURCHASE_ORDER");
	        break;
	    case "PURCHASE_RETURN":
	    	$("input[name='orderTypeId']").val("PURCHASE_ORDER");
	        break;
	    case "SALES_RETURN":
	    	$("input[name='orderTypeId']").val("SALES_ORDER");
	        break; 
	}
});