$(document).ready(function(){
	var list = $("form[name='ListReceiptRequirements'] input[type='checkbox']");
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
			case "REQ_APPROVED":
				enableSend();
				break;
			case "REQ_CREATED":
				enableApprove();
				break;
			case "REQ_ACCEPTED":
				enableCreate();
				break;
		}
	};
	function enableApprove(){
		$("button[name='submitButton']").prop("disabled",false);
		$("button[name='submitButton2']").prop("disabled",true);
		$("button[name='submitButton3']").prop("disabled",true);
	}
	function enableSend(){
		$("button[name='submitButton']").prop("disabled",true);
		$("button[name='submitButton2']").prop("disabled",false);
		$("button[name='submitButton3']").prop("disabled",true);
	}
	function enableCreate(){
		$("button[name='submitButton']").prop("disabled",true);
		$("button[name='submitButton2']").prop("disabled",true);
		$("button[name='submitButton3']").prop("disabled",false);
	}
	function disableAll(){
		$("button[name='submitButton']").prop("disabled",true);
		$("button[name='submitButton2']").prop("disabled",true);
		$("button[name='submitButton3']").prop("disabled",true);
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
	$("button[name='submitButton']").click(function(){
		$("form[name='ListReceiptRequirements']").attr("action", "approveReceiptRequirement"); 
	});
	$("button[name='submitButton2']").click(function(){
		$("form[name='ListReceiptRequirements']").attr("action", "sendReceiptRequirement"); 
	});
	$("button[name='submitButton3']").click(function(){
		$("form[name='ListReceiptRequirements']").attr("action", "createReceiptFromRequirement"); 
	});
})