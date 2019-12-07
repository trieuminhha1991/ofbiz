//init flag = true, if have a popup show flag = false
var flagPopup = true;
// flag prevent press F3 many times, init = true, when process payment flag =
// false
var flagDupPayCash = true;
var discountPercent = false;

$(document).ready(function() {
	if (disableChangeAfterPaid == 0) {
		bindEventChange();
		$('#discountWholeCartPercent').keypress(function(event) {
			code = event.keyCode ? event.keyCode : event.which;
			if (code.toString() == 27) {
				productToSearchFocus();
				return false;
			}
		});
		$('#discountWholeCartPercent').on('change', function() {
			discountPercent = true;
			discountWholeCart();
		});
	}
});

function bindEventChange() {
	$('#discountWholeCart').on('change', function() {
		var discountWholeCartVal = $("#discountWholeCart").val();
		if (discountWholeCartVal) {
			discountPercent = false;
			discountWholeCart();
		}
	});
}

function discountWholeCart() {
	var flagDiscount = true;
	var grandTotalCart = $('#grandTotalCartHidden').val();
	if (disableChangeAfterPaid == 0) {
		var amount = $('#discountWholeCart').maskMoney('unmasked')[0];
		if (discountPercent) {
			amount = $('#discountWholeCartPercent').val();
		}
		if (isNaN(amount)) {
			flagDiscount = false;
		} else {
			amount = parseFloat(amount);
			grandTotalCart = parseFloat(grandTotalCart);
			if (discountPercent) {
				var totalPercent = parseFloat('100');
				if (!checkCartContainReturnItem()) {
					if (amount >= totalPercent) {
						flagDiscount = false;
					}
				}
			} else {
				if (!checkCartContainReturnItem()) {
					if (amount >= grandTotalCart) {
						flagDiscount = false;
					}
				}
			}
		}

		if (flagDiscount) {
			var param = 'amountDiscount=' + amount + '&percent='
					+ discountPercent;
			jQuery.ajax({
				url : 'DiscountWholeCart',
				data : param,
				type : 'post',
				async : false,
				success : function(data) {
					getResultOfDiscountWholeCart(data);
				},
				error : function(data) {
					getResultOfDiscountWholeCart(data);
				}
			});
		} else {
			if (discountPercent) {
				bootbox.alert(BPOSPercentDiscountNotValid, function() {
					updateCartWebPOS();
				});
			} else {
				bootbox.alert(BPOSMoneyDiscountTotalNotValid, function() {
					updateCartWebPOS();
					bindEventChange();
				});
			}
		}
	}

	return false;
}

function getResultOfDiscountWholeCart(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.alert(serverError);
	} else {
		updateCartWebPOS();
		productToSearchFocus();
		bindEventChange();
	}
	return false;
}

function checkCartContainReturnItem() {
	var rows = $('#showCartJqxgrid').jqxGrid('getrows');
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		var quantity = row.quantityProduct;
		if (quantity < 0) {
			return true;
		}
	}
	return false;
}