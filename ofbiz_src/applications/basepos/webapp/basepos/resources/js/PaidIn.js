$(document).ready(function(){
	$('#alterpopupWindowPaidIn').jqxWindow({
		width: '480', height:'320', resizable: true,draggable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
	});
	
	$("#paidInReason").jqxDropDownList({ source: dataAdapterPaidIn, height: '30', selectedIndex: 0, 
		displayMember: 'description', valueMember: 'invoiceItemTypeId', width: '310',
	});
	$("#amountPaidIn").maskMoney({precision:2,thousands: '.', decimal: ',', allowZero: true});
	$('#PaidInForm').jqxValidator({
         rules: [
	                { input: '#amountPaidIn', message: BPOSAmountIsRequired, action: 'change',
	                	rule: function (input, commit) {
	                		if (parseInt(input.val()) > 0) {
								return true;
							}
							return false;
						}
	                },
	                { input: '#paidInReason', message: BPOSReasonIsRequired, action: 'change', 
						rule: function (input, commit) {	
							if (input.val()) {
								return true;
							}
							return false;
						}	
					}
                ]
	 });
	
	$('#alterpopupWindowPaidIn').on('open', function (event) {
		flagPopup = false;
		$("#amountPaidIn").focus();
	});
	
	$('#alterpopupWindowPaidIn').on('close', function (event) { 
		flagPopup = true;
		$('#PaidInForm').jqxValidator('hide');
		resetPaidIn();
	}); 	
});

function showPaidIn(){
	$('#alterpopupWindowPaidIn').jqxWindow('open'); 
}

function closePaidIn(){
	$('#alterpopupWindowPaidIn').jqxWindow('close'); 
}

function processPaidIn(){
	var amountPaidIn = $("#amountPaidIn").maskMoney('unmasked')[0];
	amountPaidIn = Math.round(amountPaidIn);
	var validate = $('#PaidInForm').jqxValidator('validate');
	var paidInReason = $("#paidInReason").jqxDropDownList('getSelectedItem'); 
	var invoiceItemTypeId = paidInReason.value;
	var reasonPaidInComment = $("#commentPaidIn").val();
	if(amountPaidIn && invoiceItemTypeId && validate){
		//closePaidIn();
		bootbox.confirm(BPOSAreYouSureWithThisAmountIn, function(result) {
			if(result){
				var param = "amountIn=" + amountPaidIn + "&invoiceItemTypeId="+invoiceItemTypeId  + "&reasonCommentIn=" + reasonPaidInComment.trim();
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		$.ajax({url: 'PaidIn',
    					data: param,
    					type: 'post',
    					async: false,
    					success: function(data) {
    					    getResultOfPaidIn(data);
    					},
    					error: function(data) {
    						getResultOfPaidIn(data);
    					}
    				});
            		Loading.hide('loadingMacro');
            	}, 500);
			}else{
				showPaidIn();
				$("#amountPaidIn").focus();
			}
		});
	}
}

function getResultOfPaidIn(data) {
    var serverError = getServerError(data);
    if (serverError != "") {
    	$('#alterpopupWindowPaidIn').jqxWindow('close');
    	flagPopup = false;
    	bootbox.alert(serverError, function() {
    		flagPopup = true;
    		$('#alterpopupWindowPaidIn').jqxWindow('show');
		});
    } else {
    	$('#alterpopupWindowPaidIn').jqxWindow('close');
		resetPaidIn();
		updateCartWebPOS();
    }
}

function resetPaidIn(){
	$("#amountPaidIn").maskMoney('mask', 0.0);
	$("#reasonPaidIn").val("");
	$('#commentPaidIn').val("");
}