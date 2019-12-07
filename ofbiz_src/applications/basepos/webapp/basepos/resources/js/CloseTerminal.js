$(document).ready(function() {
	$("#endingDrawerCashAmount").maskMoney({precision:2,thousands: '.', decimal: ','});

	$("#endingDrawerCashAmount").keypress(function (e) {
	    //if the letter is not digit then display error and don't type anything
	    if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57) && e.which != 13 && e.which != 16) {
	    	return false;
	    }else{
	    	$("#endingDrawerCashAmount").maskMoney('unmasked')[0];
	    }
	});
	
	$('#endingDrawerCashAmount').bind('keypress', function(event) {
		code = event.keyCode ? event.keyCode : event.which;
		if (code.toString() == 13) {
			checkAmountCloseTerminal();
			return false;
		}
		if (code.toString() == 27) {
			cancelCloseterminal();
			return false;
		}
	});
});

function checkAmountCloseTerminal(){
	var amount = $('#endingDrawerCashAmount').maskMoney('unmasked')[0];
	if (amount <= 0){
		//$('#alterpopupWindowClose').jqxWindow('close');
		bootbox.alert(BPOSEnterAmountClose, function() {
			//$('#alterpopupWindowClose').jqxWindow('open');
			$('#endingDrawerCashAmount').focus();
		});
	} else {
		closeTerminalConfirm();
	}
}

function closeTerminalConfirm() {
	var param = 'endingDrawerCashAmount=' +$("#endingDrawerCashAmount").maskMoney('unmasked')[0];
	bootbox.hideAll();
	//$('#alterpopupWindowClose').jqxWindow('close');
	bootbox.confirm(BPOSAreYouSureClose, function(result) {
		if(result){
			$.ajax({
				url : 'CloseTerminal',
				data : param,
				type : 'post',
				async : false,
				success : function(data) {
					getResultOfCloseTerminal(data);
				},
				error : function(data) {
					getResultOfCloseTerminal(data);
				}
			});
		} else {
			$("#endingDrawerCashAmount").focus();
		}
	});
}

function getResultOfCloseTerminal(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.hideAll(serverError);
		$('#alterpopupWindowClose').jqxWindow('close');
		bootbox.alert(serverError);
	} else {
		bootbox.hideAll();
		$('#alterpopupWindowClose').jqxWindow('close');
		window.location = "main";
	}
}

function cancelCloseterminal(){
	bootbox.hideAll();
	$("#endingDrawerCashAmount").val('');
	$('#alterpopupWindowClose').jqxWindow('close');
    productToSearchFocus();
}