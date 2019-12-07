var StatusOrder = (function(){
	var init = function(){
		var addr = LocalStorage.getItem(partyId,'userFullAddress');
		var payment = LocalStorage.getItem(partyId,'userPayment');
		var phone = LocalStorage.getItem(partyId,'userPhone');
		var phoneObj = $('#phone');
		var addrObj = $('#address');
		var paymentObj = $('#payment');
		var curPayment = paymentObj.text();
		curPayment = curPayment.replace(/[^a-zA-Z ]/g, "").trim();
		var curAddr = addrObj.text();
		curAddr = curAddr.replace(/[^a-zA-Z0-9 ]/g, "").trim();
		var curPhone = phoneObj.text();
		curPhone = curPhone.replace(/[^a-zA-Z0-9 ]/g, "").trim();
		if(!curAddr){
			addrObj.text(addr);
		}
		if(!curPayment && payment){
			paymentObj.text(payment);
		}
		if(!curPhone){
			phoneObj.text(phone);
		}
	};
	$(document).ready(function(){
		init();
	});
	return {

	};
})();
