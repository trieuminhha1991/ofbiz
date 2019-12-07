var locale = 'vi';
var amountCreditCardLast = 0;
$(document).ready(function (){
	$("#amountCash").maskMoney({ precision:2,thousands: '.', decimal: ',', allowZero: true });
	$("#amountCreditCard").maskMoney({precision:2,thousands: '.', decimal: ',', allowZero: true});
	$("#paybackCash").maskMoney({precision:2,thousands: '.', decimal: ','});
	$("#amountCash").keypress(function (e) {
	    //if the letter is not digit then display error and don't type anything
	    if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57) && e.which != 13 && e.which != 16 && e.which != 46) {
	   	 	return false;
	    } else {
	    	var amountCash = $("#amountCash").maskMoney('unmasked')[0];
	    	var totalDue = $("#totalDue").val();
	    	amountCash = parseFloat(amountCash);
	    	totalDue = parseFloat(totalDue);
	    	var paybackCash = amountCash - totalDue;
	    	paybackCash = parseFloat(paybackCash);
	    	paybackCash = Math.round(paybackCash);
	    	if(paybackCash >0){
	    		$("#paybackCash").maskMoney('mask', paybackCash);
	    	}else{
	    		$("#paybackCash").maskMoney('mask', 0.0);
	    	}
	    }
	});
	
	$("#amountCreditCard").keypress(function (e) {
		//if the letter is not digit then display error and don't type anything
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57) && e.which != 13 && e.which != 16) {
			return false;
		} else {
			var amountCash = $("#amountCash").maskMoney('unmasked')[0];
	    	var totalDue = $("#totalDue").val();
	    	totalDue = parseFloat(totalDue);
	    	var totalDueOrigin = totalDue;
	    	if(totalDue < 0){
	    		totalDueOrigin = totalDue * (-1);
	    	}
	    	amountCash = parseFloat(amountCash);
	    	var amountCreditCard = $("#amountCreditCard").maskMoney('unmasked')[0];
	    	amountCreditCard = parseFloat(amountCreditCard);
	    	if(amountCreditCard > totalDueOrigin){
	    		$('#alterpopupWindowPayCash').jqxWindow('close');
	    		bootbox.hideAll();
				bootbox.alert(BPOSAmountCreditCardBiggerTotalDue, function(){
					$('#alterpopupWindowPayCash').jqxWindow('open');
				});
	    	} else {
	    		amountCreditCardLast = amountCreditCard;
	    		var totalAmount = amountCash + amountCreditCard;
		    	var paybackCash = totalAmount - totalDue;
		    	paybackCash = parseFloat(paybackCash);
		    	paybackCash = Math.round(paybackCash);
		    	if(paybackCash >0){
		    		$("#paybackCash").maskMoney('mask', paybackCash);
		    	}else{
		    		$("#paybackCash").maskMoney('mask', 0);
		    	}
	    	}	
		}
	});
	
	$('#amountCash').keydown(function(event) {  	
       code = event.keyCode ? event.keyCode : event.which;
       //8 is code of backspace
       if(code == 8){
    	   	var amountCash = $("#amountCash").maskMoney('unmasked')[0];
    	   	var amountCreditCard = $("#amountCreditCard").maskMoney('unmasked')[0];
	    	var totalDue = $("#totalDue").val();
	    	amountCash = parseFloat(amountCash);
	    	amountCreditCard = parseFloat(amountCreditCard);
	    	totalDue = parseFloat(totalDue);
	    	var amountTotal = amountCash + amountCreditCard;
	    	var paybackCash = amountTotal - totalDue;
	    	paybackCash = parseFloat(paybackCash);
	    	paybackCash = Math.round(paybackCash);
	    	if(paybackCash >0){
	    		$("#paybackCash").maskMoney('mask', paybackCash);
	    	} else {
	    		$("#paybackCash").maskMoney('mask', 0.00);
	    	}
       }
	});
	
	$('#amountCreditCard').keydown(function(event) {  	
		code = event.keyCode ? event.keyCode : event.which;
		//8 is code of backspace
		if(code == 8){
			var amountCash = $("#amountCash").maskMoney('unmasked')[0];
			var amountCreditCard = $("#amountCreditCard").maskMoney('unmasked')[0];
			var totalDue = $("#totalDue").val();
			amountCash = parseFloat(amountCash);
			amountCreditCard = parseFloat(amountCreditCard);
			totalDue = parseFloat(totalDue);
			var amountTotal = amountCash + amountCreditCard;
			var paybackCash = amountTotal - totalDue;
			paybackCash = parseFloat(paybackCash);
			paybackCash = Math.round(paybackCash);
			if(paybackCash >0){
				$("#paybackCash").maskMoney('mask', paybackCash);
			} else {
				$("#paybackCash").maskMoney('mask', 0.00);
			}
		}
	});
	
	$('#amountCash').keydown(function(event) {  	
		code = event.keyCode ? event.keyCode : event.which; 	
	    if(code == 13){
	    	var amountCash = $("#amountCash").maskMoney('unmasked')[0];
			var amountCreditCard = $("#amountCreditCard").maskMoney('unmasked')[0];
			amountCreditCard = parseFloat(amountCreditCard);
			amountCash = parseFloat(amountCash);
			var paybackCash = 0;
		    if(amountCash == 0){
		    	var cashTotalDue = $('#totalDue').val();
		      	cashTotalDue = parseFloat(cashTotalDue);
		      	if (cashTotalDue >= 0){
		      		if(cashTotalDue >= amountCreditCard){
		      			amountCash = cashTotalDue - amountCreditCard;
		      			paybackCash = 0;
		      		} else {
		      			amountCash = 0;
		      			paybackCash = amountCreditCard - cashTotalDue;
		      		}
		      		$("#amountCash").maskMoney('mask', amountCash);
			      	$('#paybackCash').maskMoney('mask', paybackCash);
		      	} else {
		      		$("#amountCash").maskMoney('mask', 0);
		      		$('#paybackCash').maskMoney('mask', cashTotalDue);
		      	}
		      	return false;
		    }
	   }
       //F3
       if(code == 114){
    	   payCashConfirm('N');
    	   return false;
       }
       //F4
       if(code == 115){
    	   if (isPrintBeforePayment == 'N'){
    		   payCashConfirm('Y');
    	   }
    	   return false;
       }
       //ESC
       if (code.toString() == 27) {
    	   $('#alterpopupWindowPayCash').jqxWindow('close');
           productToSearchFocus();
           return false;
       }
	});
	
	$('#amountCreditCard').keydown(function(event) {  	
		code = event.keyCode ? event.keyCode : event.which; 	
	    if(code == 13){
	    	var amountCash = $("#amountCash").maskMoney('unmasked')[0];
			var amountCreditCard = $("#amountCreditCard").maskMoney('unmasked')[0];
			amountCreditCard = parseFloat(amountCreditCard);
			amountCash = parseFloat(amountCash);
			var paybackCash = 0;
		    if(amountCreditCard == 0){
		    	var cashTotalDue = $('#totalDue').val();
		      	cashTotalDue = parseFloat(cashTotalDue);
		      	if (cashTotalDue >= 0){
		      		if(cashTotalDue >= amountCash){
		      			amountCreditCard = cashTotalDue - amountCash;
		      			paybackCash = 0;
		      		} else {
		      			amountCreditCard = 0;
		      			paybackCash = amountCash - cashTotalDue;
		      		}
		      		$("#amountCreditCard").maskMoney('mask', amountCreditCard);
			      	$('#paybackCash').maskMoney('mask', paybackCash);
		      	} else {
		      		$("#amountCreditCard").maskMoney('mask', 0);
		      		$('#paybackCash').maskMoney('mask', cashTotalDue);
		      	}
		      	return false;
		    }
	   }
       //F3
       if(code == 114){
    	   payCashConfirm('N');
    	   return false;
       }
       //F4
       if(code == 115){
    	   if (isPrintBeforePayment == 'N'){
    		   payCashConfirm('Y');
    	   }
    	   return false;
       }
       //ESC
       if (code.toString() == 27) {
    	   $('#alterpopupWindowPayCash').jqxWindow('close');
           productToSearchFocus();
           return false;
       }
	});
   
	$('#payCashCancel').click(function(event) {
		$('#alterpopupWindowPayCash').jqxWindow('close');
	    productToSearchFocus();
	    return false;
	});
	
	$('#alterpopupWindowPayCash').on('open', function (event) {
		resetPayment();
	});
});


function notEnoughPaid(){
	$('#alterpopupWindowPayCash').jqxWindow('close');
	bootbox.hideAll();
	bootbox.alert(BPOSNotEnoughPay, function() {
		$('#alterpopupWindowPayCash').jqxWindow('open');
	});
	
	return false;
}

function checkPayCash(){
	var cashTotalDue = $('#totalDue').val();
	cashTotalDue = parseFloat(cashTotalDue);
	var amountCash = $("#amountCash").maskMoney('unmasked')[0];
	var amountCreditCard = $("#amountCreditCard").maskMoney('unmasked')[0];
	amountCash = parseFloat(amountCash);
	amountCreditCard = parseFloat(amountCreditCard);
	var amountTotal = amountCash + amountCreditCard;
	if(cashTotalDue < 0){
		cashTotalDue = cashTotalDue * (-1);
	}
	var paybackCash = amountTotal - cashTotalDue;
	var paybackCash = parseFloat(paybackCash);
	if(paybackCash >=0){
		return true;
	} else {
		return false;
	}
}

function payCashConfirm(isPrint) {
	var cashTotalDue = $('#totalDue').val();
	if (cashTotalDue >= 0){
		var boolean = checkPayCash();
	} else {
		var boolean = true;
	}
	if(boolean){
		var amountCash = $("#amountCash").maskMoney('unmasked')[0];
		var amountCreditCard = $("#amountCreditCard").maskMoney('unmasked')[0];
	    disableChangeAfterPaid = 1;
		amountCash = parseFloat(amountCash);
		amountCreditCard = parseFloat(amountCreditCard);
		var totalDue = $('#totalDue').val();
		totalDue = parseFloat(cashTotalDue);
		var originTotalDue = totalDue;
		var isReturn = false;
		if(totalDue < 0){
			originTotalDue = originTotalDue * (-1);
			isReturn = true;
		}
		var totalAmount = amountCash + amountCreditCard;
		if((totalAmount >=originTotalDue && isReturn == false) || (totalAmount < originTotalDue && isReturn == true)){
			var param = new Object();
			if(amountCreditCard >= originTotalDue){
				param.amountCreditCard = originTotalDue;
			}else if(amountCreditCard >0){
				param.amountCreditCard = amountCreditCard;
				param.amountCash = originTotalDue - amountCreditCard;
			}else if(amountCreditCard ==0){
				param.amountCash = originTotalDue;
			}
			if (flagDupPayCash){
				flagDupPayCash = false;
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		$.ajax({url: 'PayCash',
    			        data: param,
    			        type: 'post',
    			        async: false,
    			        success: function(data) {
    			            getResultOfPayCashConfirm(data);
    			        },
    			        error: function(data) {
    			        	getResultOfPayCashConfirm(data);
    			        }
    			    });
            		Loading.hide('loadingMacro');
            		$('#alterpopupWindowPayCash').jqxWindow('close');
            	}, 500);
            	if (isPrint === 'Y'){
            		getDataForPrint();
    				$("#PrintOrder").show();
    				$("#PrintOrder").css({
    					"z-index" : -1,
    					position: "absolute"
    				});
    				setTimeout(function(){
    					$("#PrintOrder").printArea();
    				}, 100);
            	}
			}
		}
	} else {
		notEnoughPaid();
	}
}

function checkShowPayCash(){
	var rows = $("#showCartJqxgrid").jqxGrid('getRows');
	if (rows.length > 0){
		showPayCash();
	} else {
		bootbox.alert(BPOSNoAnyItemInCart);
	}
}

function showPayCash(){
	bootbox.hideAll();
	$('#alterpopupWindowPayCash').jqxWindow('open');
	return false;
}

function getResultOfPayCashConfirm(data) {
    var serverError = getServerError(data);
    if (serverError != "") {
    	bootbox.alert(serverError);
    } else {
    	$('#amountCash').val('');
    	$('#amountCreditCard').val('');
    	bootbox.hideAll();
    	updateParty();
        updateCartWebPOS();
        disableChangeAfterPaid = 0;
        resetPayment();
    }
}

function resetPayment(){
	$('#amountCash').val('');
	$('#amountCreditCard').val('');
    $('#paybackCash').val('');
	flagDupPayCash = true;
}