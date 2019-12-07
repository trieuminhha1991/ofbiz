var selectedWebPOS = 0;
var flagProductSearch = 0;
var productSelectedDiscountPercent = false;
var focusTime = 0;
var flagReturn = false;

$(document).ready(function() {
	$('#productSelectedQuantity').change(function() {
		updateCartItem();
	});
});

function bindEventChangePrice() {
	$('#productSelectedDiscountAmount').change(
			function() {
				var productSelectedDiscountAmount = $(
						"#productSelectedDiscountAmount").val();
				if (productSelectedDiscountAmount) {
					productSelectedDiscountPercent = false;
					itemDiscount();
				}
			});
}

function returnCartItemSelected(rowIndex) {
	if (rowIndex >= 0) {
		var param = 'cartLineIndex=' + rowIndex + '&quantity='
		+ $('#productSelectedQuantity').val();
		$.ajax({
			url : 'ReturnCartItem',
			data : param,
			type : 'post',
			async : false,
			success : function(data) {
				getResultOfReturnCartItem(data);
			},
			error : function(data) {
				getResultOfReturnCartItem(data);
			}
		});
	}
}

function getResultOfReturnCartItem(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.alert(serverError);
	} else {
		updateCartWebPOS();
	}
	productToSearchFocus();
}

function incrementItemQuantityWebPOS() {
	// Get current selected index
	var selectedIndex = $('#showCartJqxgrid').jqxGrid('selectedrowindex');
	if (disableChangeAfterPaid == 0) {
		var qnt = parseInt($('#productSelectedQuantity').val());
		if (!isNaN(qnt)) {
			qnt = qnt + 1;
			$('#productSelectedQuantity').val(qnt);
			updateCartItemWebPOS(selectedWebPOS);
		}
	}
	productToSearchFocus();
	// Set the selected index
	$('#showCartJqxgrid').jqxGrid('selectrow', selectedIndex);
	return false;
}

function decrementItemQuantityWebPOS() {
	// Get current selected index
	var selectedIndex = $('#showCartJqxgrid').jqxGrid('selectedrowindex');
	if (disableChangeAfterPaid == 0) {
		var qnt = parseInt($('#productSelectedQuantity').val());
		if (!isNaN(qnt) && qnt > 1) {
			qnt = qnt - 1;
			$('#productSelectedQuantity').val(qnt);
			updateCartItemWebPOS(selectedWebPOS);
		}
	}
	productToSearchFocus();
	// Set the selected index
	$('#showCartJqxgrid').jqxGrid('selectrow', selectedIndex);
	return false;
}

function itemQuantityFocus() {
	var rows = $("#showCartJqxgrid").jqxGrid('getRows');
	if (rows.length > 0) {
		$('#productSelectedQuantity').focus();
	}
	return false;
}

function checkDiscountFocus() {
	var rows = $("#showCartJqxgrid").jqxGrid('getRows');
	if (rows.length > 0) {
		discountFocus();
	}
}

function discountFocus() {
	focusTime += 1;
	if (focusTime == 1) {
		if ($("#discountWholeCartPercent").prop('disabled')) {
			focusTime += 1;
		} else {
			$('#discountWholeCart').focus();
		}
	}
	if (focusTime == 2) {
		if ($("#productSelectedDiscountAmount").prop('disabled')) {
			focusTime += 1;
		} else {
			$('#productSelectedDiscountAmount').focus();
		}
	}
	if (focusTime == 3) {
		if ($("#discountWholeCartPercent").prop('disabled')) {
			focusTime += 1;
		} else {
			$('#discountWholeCartPercent').focus();
		}
	}
	if (focusTime == 4) {
		if ($("#productSelectedDiscountPercent").prop('disabled')) {
			focusTime += 1;
		} else {
			$('#productSelectedDiscountPercent').focus();
		}
		focusTime = 0;
	}

	return false;
}

function resetCartItemDiscountAndDisable() {
	$("#productSelectedDiscountAmount").maskMoney('mask', 0.0);
	$("#productSelectedDiscountPercent").maskMoney('mask', 0.0);
	$("#productSelectedDiscountAmount").prop('disabled', true);
	$("#productSelectedDiscountPercent").prop('disabled', true);
}

function processItemDiscount() {
	var rowIndex = $("#showCartJqxgrid").jqxGrid('getselectedrowindex');
	if (checkConditionForReturn()) {
		processReturnWithWholeDiscount(rowIndex);
	} else {
		bootbox.confirm(BPOSReturnItemIsNotAllowedWithDiscount,
				function(result) {
					if (result) {
						resetCartItemDiscountAndDisable();
						itemDiscount();
						processReturnWithWholeDiscount(rowIndex);
					}
				});
	}
}

function processReturnWithWholeDiscount(rowIndex) {
	if (checkConditionForReturnWithWholeDiscount()) {
		returnCartItemSelected(rowIndex);
	} else {
		bootbox.confirm(BPOSReturnItemIsNotAllowedWithDiscount,
				function(result) {
					if (result) {
						resetWholeCartAndDisable();
						discountWholeCart();
						returnCartItemSelected(rowIndex);
					}
				});
	}
}

function itemDiscount() {
	var flagDiscount = true;
	var amount = $('#productSelectedDiscountAmount').maskMoney('unmasked')[0];
	if (productSelectedDiscountPercent) {
		amount = $('#productSelectedDiscountPercent').val();
	}
	var subTotal = $('#itemSubTotal').val();
	if (isNaN(amount)) {
		flagDiscount = false;
	} else {
		amount = parseFloat(amount);
		subTotal = parseFloat(subTotal);
		if (productSelectedDiscountPercent) {
			if (!flagReturn) {
				var totalPercent = parseFloat('100');
				if (amount >= totalPercent) {
					flagDiscount = false;
				}
			}
		} else {
			if (!flagReturn) {
				if (amount >= subTotal) {
					flagDiscount = false;
				}
			}
		}
	}
	if (flagDiscount) {
		$.ajax({
			url : 'itemDiscount',
			data : {
				cartLineIdx : selectedWebPOS,
				amountItemDiscount : amount,
				percentItemDiscount : productSelectedDiscountPercent
			},
			type : 'post',
			async : false,
			success : function(data) {
				getResultOfItemDiscount(data);
			},
			error : function(data) {
				getResultOfItemDiscount(data);
			}
		});
	} else {
		if (productSelectedDiscountPercent) {
			bootbox.alert(BPOSPercentDiscountNotValid, function() {
				updateCartWebPOS();
			});
		} else {
			bootbox.alert(BPOSMoneyDiscountNotValid, function() {
				updateCartWebPOS();
				bindEventChangePrice();
			});
		}
	}
}

function getResultOfItemDiscount(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.alert(serverError);
	} else {
		updateCartWebPOS();
		productToSearchFocus();
		bindEventChangePrice();
	}
}

function updateCartItem() {
	var quantity = $('#productSelectedQuantity').val();
	var quantityTmp = $("#productSelectedQuantityTmp").val();
	if (quantity != '') {
		quantity = parseInt(quantity, 10);
		if (isNaN(quantity)) {
			bootbox.alert(BPOSQuantityItemNotValid);
			$('#productSelectedQuantity').val(quantityTmp);
		} else {
			var param = 'cartLineIndex=' + selectedWebPOS + '&quantity='
					+ $('#productSelectedQuantity').val();
			$.ajax({
				url : 'UpdateCartItem',
				data : param,
				type : 'post',
				async : false,
				success : function(data) {
					getResultOfUpdateCartItem(data);
				},
				error : function(data) {
					getResultOfUpdateCartItem(data);
				}
			});
		}

	}
}

function getResultOfUpdateCartItem(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.alert(serverError, function() {
			var productSelectedQuantityTmp = $("#productSelectedQuantityTmp")
					.val();
			$("#productSelectedQuantity").val(productSelectedQuantityTmp);
			$("#jqxProductList").jqxComboBox('focus');
		});
	} else {
		updateCartWebPOS();
		productToSearchFocus();
	}
}

function deleteCartItem(cartLineIndex) {
	if (disableChangeAfterPaid == 0) {
		var param = 'cartLineIndex=' + cartLineIndex;
		$.ajax({
			url : 'DeleteCartItem',
			data : param,
			type : 'post',
			async : false,
			success : function(data) {
				getResultOfDeleteCartItem(data);
			},
			error : function(data) {
				getResultOfDeleteCartItem(data);
			}
		});
	}
}

function getResultOfDeleteCartItem(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.alert(serverError);
		$("#jqxProductList").jqxComboBox('focus');
	} else {
		selectedWebPOS = 0;
		updateCartWebPOS();
	}
	return false;
}

function updateCartItemWebPOS(selectedWebPOS) {
	var param = 'cartLineIndex=' + selectedWebPOS + '&quantity='
			+ $('#productSelectedQuantity').val();
	$.ajax({
		url : 'UpdateCartItem',
		data : param,
		type : 'post',
		async : false,
		success : function(data) {
			getResultOfUpdateCartItemWebPOS(data);
		},
		error : function(data) {
			getResultOfUpdateCartItemWebPOS(data);
		}
	});
}

function getResultOfUpdateCartItemWebPOS(data) {
	var serverError = getServerError(data);
	if (serverError != "") {
		bootbox.alert(serverError);
	} else {
		updateCartWebPOS();
	}
}

function checkConditionForReturn() {
	var productSelectedDiscountAmount = $("#productSelectedDiscountAmount")
			.val();
	var productSelectedDiscountPercent = $("#productSelectedDiscountPercent")
			.val();
	productSelectedDiscountAmount = parseFloat(productSelectedDiscountAmount);
	productSelectedDiscountPercent = parseFloat(productSelectedDiscountPercent);
	if (productSelectedDiscountAmount > 0 || productSelectedDiscountPercent > 0) {
		return false;
	} else {
		return true;
	}
}

function resetWholeCartAndDisable() {
	$("#discountWholeCart").maskMoney('mask', 0.0);
	$("#discountWholeCartPercent").maskMoney('mask', 0.0);
	$("#discountWholeCart").prop('disabled', true);
	$("#discountWholeCartPercent").prop('disabled', true);
}

function checkConditionForReturnWithWholeDiscount() {
	var rowIndex = $("#showCartJqxgrid").jqxGrid('getselectedrowindex');
	var discountWholeCart = $("#discountWholeCart").val();
	var discountWholeCartPercent = $("#discountWholeCartPercent").val();
	discountWholeCart = parseFloat(discountWholeCart);
	discountWholeCartPercent = parseFloat(discountWholeCartPercent);
	if (discountWholeCart > 0 || discountWholeCartPercent > 0) {
		var rows = $('#showCartJqxgrid').jqxGrid('getrows');
		var lengthRow = rows.length;
		if (lengthRow < 2) {
			return false;
		} else {
			for (var i = 0; i < lengthRow; i++) {
				var row = rows[i];
				if (row.uid != rowIndex) {
					var quantity = row.quantityProduct;
					if (quantity > 0) {
						return true;
					}
				}
			}
			return false;
		}
	} else {
		return true;
	}
}