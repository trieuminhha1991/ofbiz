$(document).ready(function(){
	$('#alterpopupWindowPaidOut').jqxWindow({
		width: '480', height:'320', resizable: false, draggable: false, isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
	});
	$("#paidOutReason").jqxDropDownList({ source: dataAdapterPaidOut, height: '30', selectedIndex: 0, 
		displayMember: 'description', valueMember: 'invoiceItemTypeId', width: '310',
		});
	$("#amountPaidOut").maskMoney({precision:2,thousands: '.', decimal: ',', allowZero: true});
	$('#PaidOutForm').jqxValidator({
         rules: [
	                { input: '#amountPaidOut', message: BPOSAmountIsRequired, action: 'change',
	                	rule: function (input, commit) {
							if (parseInt(input.val()) > 0) {
								return true;
							}
							return false;
						}
	                },
	                { input: '#paidOutReason', message: BPOSReasonIsRequired, action: 'change', 
						rule: function (input, commit) {
							if (input.val()) {
								return true;
							}
							return false;
						}
					}
                ]
	 });
	
	$('#alterpopupWindowPaidOut').on('open', function (event) {
		flagPopup = false;
		$("#amountPaidOut").focus();
	}); 
	
	$('#alterpopupWindowPaidOut').on('close', function (event) { 
		flagPopup = true;
		$('#PaidOutForm').jqxValidator('hide');
		resetPaidOut();
	}); 
});

function showPaidOut(){
	$('#alterpopupWindowPaidOut').jqxWindow('open'); 
}

function closePaidOut(){
	$('#alterpopupWindowPaidOut').jqxWindow('close'); 
}

function processPaidOut(){
	var amountPaidOut = $("#amountPaidOut").maskMoney('unmasked')[0];
	amountPaidOut = Math.round(amountPaidOut);
	var validate = $('#PaidOutForm').jqxValidator('validate');
	var paidOutReason = $("#paidOutReason").jqxDropDownList('getSelectedItem'); 
	var invoiceItemTypeId = paidOutReason.value;
	var reasonPaidOutComment = $("#commentPaidOut").val();
	if(amountPaidOut && paidOutReason && validate){
		//closePaidOut();
		bootbox.confirm(BPOSAreYouSureWithThisAmountOut, function(result) {
			if(result){
				var param = "amountOut=" + amountPaidOut +"&invoiceItemTypeId=" + invoiceItemTypeId + "&reasonCommentOut=" +  reasonPaidOutComment.trim();
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		$.ajax({url: 'PaidOut',
    					data: param,
    					type: 'post',
    					async: false,
    					success: function(data) {
    					    getResultOfPaidOut(data);
    					},
    					error: function(data) {
    						getResultOfPaidOut(data);
    					}
    				});
            		Loading.hide('loadingMacro');
            	}, 500);
			}else{
				showPaidOut();
				$('#amountPaidOut').focus();
			}
		});
	}
}

function getResultOfPaidOut(data) {
    var serverError = getServerError(data);
    if (serverError != "") {
    	$('#alterpopupWindowPaidOut').jqxWindow('close');
    	flagPopup = false;
    	bootbox.alert(serverError, function() {
    		flagPopup = true;
    		$('#alterpopupWindowPaidOut').jqxWindow('show');
		});
    } else {
    	$('#alterpopupWindowPaidOut').jqxWindow('close');
		resetPaidOut();
		updateCartWebPOS();
    }
}

function resetPaidOut(){
	$("#amountPaidOut").maskMoney('mask', 0.0);
	$("#reasonPaidOut").val("");
	$('#commentPaidOut').val("");
}
