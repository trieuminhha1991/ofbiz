$(document).ready(function() {
	productToSearchFocus();
});

function setPartyToCart(partyId) {
    var param = 'partyId=' + partyId;
    $.ajax({url: 'SetPartyToCart',
        data: param,
        type: 'post',
        async: false,
        success: function(data) {
        	getResultOfSetPartyToCart(data);
        },
        error: function(data) {
        	getResultOfSetPartyToCart(data);
        }
    });
    // Recalculate Loyalty
    updateCartHeader();
}

function getResultOfSetPartyToCart(data){
	var serverError = getServerError(data);
    if (serverError != "") {
    	bootbox.alert(serverError, function() {
			flagPopup = true;
		});
		$("#jqxPartyList").jqxComboBox('clearSelection');
		$("#jqxPartyList").jqxComboBox('focus');
    } else {
    	flagPopup = true;
    	updateParty();
    }
}

function partyToSearchFocus() {
	if (disableChangeAfterPaid == 0) {
		$("#jqxPartyList").jqxComboBox('focus');
		$("#jqxPartyList").jqxComboBox('clearSelection');
		$("#jqxPartyList").jqxComboBox('close');
	}
	return false;
}

function productToSearchFocus() {
	if (disableChangeAfterPaid == 0) {
		$("#jqxProductList").jqxComboBox('clearSelection');
		$("#jqxProductList").jqxComboBox('close');
		$("#jqxProductList").jqxComboBox('focus');
	} 
	return false;
}