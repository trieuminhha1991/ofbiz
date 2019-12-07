$(document).ready(function(){
	
	if ($("select[name='reasonId']").val() == 'FROM_IMPORT_PLAN'){
		$("form[name='PrepareCreatePurchaseOrder']").attr("action", "editPurchaseOrderFromPlan");
	} else {
		if ($("select[name='reasonId']").val() == 'CREATE_NEW_ORDER'){	
			$("form[name='PrepareCreatePurchaseOrder']").attr("action", "editPurchaseOrder");
		}
	}
	
	$("select[name='reasonId']").change(function(){
		if ($("select[name='reasonId']").val() == 'FROM_IMPORT_PLAN'){
			$("form[name='PrepareCreatePurchaseOrder']").attr("action", "editPurchaseOrderFromPlan");
		} else {
			if ($("select[name='reasonId']").val() == 'CREATE_NEW_ORDER'){	
				$("form[name='PrepareCreatePurchaseOrder']").attr("action", "editPurchaseOrder");
			}
		}
	});
});