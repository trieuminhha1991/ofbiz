var PaymentMethod = (function(){
	var init = function(){
		console.log(11);
		$('input[name="checkOutPaymentId"]').change(function(){
			var obj = $(this);
			var id = obj.attr('id');
			var text = $('label[for="'+id+'"]').text();
			console.log(id, text);
			if(text){
				LocalStorage.setItem(partyId,'userPayment', text);
			}else{
				LocalStorage.setItem(partyId,'userPayment', obj.val());
			}
		});
	};
	$(document).ready(function(){
		init();
	});
})();
