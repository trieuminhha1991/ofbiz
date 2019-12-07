$(document).ready(function(){
	var list = $("form[name='ListAllDeliveries'] input[type='checkbox']");
	disableAll();
	for(var x = 0; x < list.length; x++){
		(function(x){
			var obj = $(list[x]);
			obj.change(function(){
				changeStatus($(this));
			});
		})(x);
	}
	function changeButtonStatus(status){
		switch (status){
			case "DLV_CREATED":
				enableExported();
				break;
			case "DLV_CREATED":
				enableExported();
				break;
			case "DLV_EXPORTED":
				enableDelivered();
				break;
			case "DLV_DELIVERED":
				break;
			case "DLV_COMPLETED":
				break;
		}
	};
	function enableExported(){
		$("button[name='exportedButton']").prop("disabled",false);
		$("button[name='deliveredButton']").prop("disabled",true);
		$("input[name^='newStatusId']").val("DLV_EXPORTED");
	}
	function enableDelivered(){
		$("button[name='exportedButton']").prop("disabled",true);
		$("button[name='deliveredButton']").prop("disabled",false);
		$("input[name^='newStatusId']").val("DLV_DELIVERED");
	}
	function disableAll(){
		$("button[name='exportedButton']").prop("disabled",true);
		$("button[name='deliveredButton']").prop("disabled",true);
	}
	function changeStatus(obj){
		var statusId = obj.parent().siblings("input[name^='statusId']").val();
		if(obj.is(":checked")){
			changeButtonStatus(statusId);
		}else{
			disableAll();
		}
		for(var x = 0; x < list.length; x++){
			var tmp = $(list[x]); 
			var curstatus = tmp.parent().siblings("input[name^='statusId']").val();
			if(tmp.is(":checked")){
				changeButtonStatus(statusId);
			}
			if(curstatus != statusId && obj.is(":checked")){
				tmp.attr("checked", false);
			} else if(curstatus == statusId && obj.is(":checked")){
			}
		}
	};
	$("button[name='exportedButton']").click(function(){
		$("form[name='ListAllDeliveries']").attr("action", "exportedDelivery");
	});
	$("button[name='deliveredButton']").click(function(){
		$("form[name='ListAllDeliveries']").attr("action", "deliveredDelivery"); 
	});
})
