$(document).ready(function() {
	$('#promotionCode').bind('keypress', function(event) {
		code = event.keyCode ? event.keyCode : event.which;
		if (code.toString() == 13) {
			processPromotionCode();
			return false;
		}
		if (code.toString() == 27) {
			cancelPromotionCode();
			return false;
		}
	});
	
	$('#alterpopupWindowPromotionCode').jqxValidator({
        rules: [
	                { input: '#promotionCode', message: BPOSValidateRequired, action: 'blur', rule: 'required' },
               ]
	 });
});

function processPromotionCode() {
	var validate = $('#alterpopupWindowPromotionCode').jqxValidator('validate');
	if(validate){
		//$('#alterpopupWindowPromotionCode').jqxWindow('close');
		bootbox.confirm(BPOSVerify, function(result) {
			if(result){
				var promotionCode = $('#promotionCode').val();
				var param = "productPromoCodeId=" + promotionCode;
				$.ajax({
					url : 'ProcessPromotionCode',
					data : param,
					type : 'post',
					async : false,
					success : function(data) {
						getResultOfProcessPromotionCode(data);
					},
					error : function(data) {
						getResultOfProcessPromotionCode(data);
					}
				});
			} else {
				$('#promotionCode').focus();
			}
		});
	}
}

function getResultOfProcessPromotionCode(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.hideAll(serverError);
		$('#alterpopupWindowPromotionCode').jqxWindow('close');
		flagPopup = false;
		bootbox.alert(serverError, function() {
			flagPopup = true;
			$('#alterpopupWindowPromotionCode').jqxWindow('open');
		});
	} else {
		bootbox.hideAll();
		bootbox.alert(data.messageSuccess, function() {
			$('#alterpopupWindowPromotionCode').jqxWindow('close');
		});
		updateCartWebPOS();
		productToSearchFocus();
	}
}

function cancelPromotionCode(){
	bootbox.hideAll();
	$("#promotionCode").val('');
	$('#alterpopupWindowPromotionCode').jqxWindow('close');
    productToSearchFocus();
}