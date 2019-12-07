var LocalConfig = new Object();
$(document).ready(function() {
	$('#nav a').each(function (i) { $(this).attr('tabindex', -1); });
	activateHotKeys();
	$('#discountWholeCart').maskMoney({precision:2,thousands: '.', decimal: ',', allowZero: true});
	$("#productSelectedUnitPrice").maskMoney({precision:2,thousands: '.', decimal: ',', allowZero: true});
	$("#productSelectedDiscountAmount").maskMoney({precision:2,thousands: '.', decimal: ',', allowZero: true});
	
	$(document).keydown(function(objEvent) {
		if (objEvent.keyCode == 9) {
			for ( var x in LocalConfig.disabledTab) {
				if ($("#" + LocalConfig.disabledTab[x]).jqxWindow('isOpen')) {
					objEvent.preventDefault();
					break;
				}
			}
		}
	});
	LocalConfig.notHideOverlayDiv = ["jqxwindowHelpCenter"];
});
$.prototype.disableTab = function() {
	if (typeof (LocalConfig.disabledTab) == "undefined") {
		LocalConfig.disabledTab = new Array();
	}
	LocalConfig.disabledTab.push($(this).attr("id"));
};

function getCartInfo(){
	var result = null;
	$.ajax({
		url : 'GetCartInfo',
		type : 'post',
		async : false,
		success : function(data) {
			result =  getResultOfCartInfo(data);
		},
		error : function(data) {
			result = [];
		}
	});
	return result;
}

function getResultOfCartInfo(data){
	var serverError = getServerError(data);
    if (serverError != "") {
    	productToSearchFocus();
        bootbox.alert(serverError);
        return [];
    } else {
        return data.listCartItems;
    }
}

function updateCartWebPOS(){
	var data = getCartInfo();
	sourceItem.localdata = data;
	$("#jqxProductList").jqxComboBox('close');
	$("#jqxProductList").jqxComboBox('focus');
   	$("#showCartJqxgrid").jqxGrid('updatebounddata');

	var listCartItems = $("#showCartJqxgrid").jqxGrid('getrows');
	if(listCartItems && listCartItems.length >0){
		updateCartItemSelected(selectedWebPOS);
	}else{
		resetSelectCartItem();
	}
	
	//update cart header
	updateCartHeader();
	focusTime = 0;	
}

function updateCartHeader(){
	$.ajax({url: 'ShowCartHeaderWebPOS',
        type: 'post',
        async: false,
        success: function(data) {
            getResultOfUpdateCartHeader(data);
        },
        error: function(data) {
        	getResultOfUpdateCartHeader(data);
        }
    });
}

function updateParty() {
	$.ajax({
		url : 'GetPartyInfo',
		type : 'post',
		async : false,
		success : function(data) {
			getResultOfUpdateParty(data);
		},
		error : function(data) {
			getResultOfUpdateParty(data);
		}
	});
}

function getResultOfUpdateParty(data){
	$("#jqxPartyList").jqxComboBox('clearSelection');
	$("#jqxPartyList").jqxComboBox('close');
	$("#jqxPartyList").jqxComboBox('focus');
	var serverError = getServerError(data);
    if (serverError != "") {
		bootbox.alert(serverError);
    } else {
    	var partyInfo = data.partyInfo;
    	if(partyInfo){
    		var partyId = partyInfo.partyId;
    		var partyName = partyInfo.partyName;
    		var partyAddress = partyInfo.partyAddress;
    		var partyTelephone = partyInfo.partyTelephone;
    		if(partyId){
    			$("#partyIdTmp").val(partyId);
    			$("#partyId").html(partyId);
    		}else{
    			$("#partyIdTmp").val("");
    			$("#partyId").html(" ");
    		}
    		if(partyName){
    			$("#partyName").html(partyName);
    		}else{
    			$("#partyName").html(" ");
    		}
    		if(partyAddress){
    			$("#partyAddress").html(partyAddress);
    			if (partyAddress.length > 31){
    				$('#posPartyInfo').css("overflow-y", "auto");
    			}
    		}else{
    			$("#partyAddress").html(" ");
    			$('#posPartyInfo').css("overflow-y", "hidden");
    		}
    		if(partyTelephone){
    			$("#partyMobile").html(partyTelephone);
    		}else{
    			$("#partyMobile").html(" ");
    		}
    	}else{
    		resetParty();
    	}
    }
}

function resetParty(){
	$("#partyIdTmp").val("");
	$("#partyId").html("");
	$("#partyName").html("");
	$("#partyAddress").html("");
	$("#partyMobile").html("");
}

function updateViewHoldCart() {
	$.ajax({
		url : 'ViewHoldCart',
		type : 'post',
		async : false,
		success : function(data) {
			getListHoldCart();
			var allData = $("#showHoldCartList").jqxGrid('getrows');
			if (allData.length == 0){
				var holdCartItemsData = [{}];
				updateSoruceHoldCartItems(holdCartItemsData);
				resetOverViewHoldCart();
			} else{
				$("#showHoldCartList").jqxGrid('selectrow', 0);
				updateHoldCartItems(0);
			}
			$('#showHoldCartWindow').jqxWindow('open');
            $('#showHoldCartList').jqxGrid('focus');
		},
		error : function(data) {
			getResultOfViewHoldCart();
		}
	});
}

var formatValueStrNumber = function(number, locale){
	var numberStr = "" + number;
	if (locale == "vi") {
		if (numberStr.indexOf(",") > -1) {
			numberStr.replace(",", "");
		}
		if (numberStr.indexOf(".") > -1) {
			numberStr = numberStr.replace(".", ",");
		}
	}
	return numberStr;
};

function addItem(productId, qnt, updCart, uomId, amount, idEAN) {
    var param = 'add_product_id=' + productId + "&quantity=" + qnt + "&quantityUomId=" + uomId;
	if (amount != null && typeof(amount) != "undefined") {
		var amountStr = formatValueStrNumber(amount, "vi");
		param += "&add_amount=" + amountStr;
	}
	if (idEAN != null && typeof(idEAN) != "undefined") param += "&idEAN=" + idEAN;
    $.ajax({url: 'AddToCart',
        data: param,
        type: 'post',
        async: false,        
        success: function(data) {
            getResultOfAddItem(data, updCart);
        },
        error: function(data) {
            getResultOfAddItem(data, updCart);
        }
    });
    flagProductSearch = 1;
}

function getResultOfAddItem(data, updCart) {
    var serverError = getServerError(data);
    if (serverError != "") {
        productToSearchFocus();
        bootbox.alert(serverError, function() {
			flagPopup = true;
		});
    } else {
    	flagPopup = true;
        if (updCart == "Y") {
        	selectedWebPOS = data.itemId;
            updateCartWebPOS();
            productToSearchFocus();
        }
    }
}

function hideOverlayDiv() {
    $("#amountCash").maskMoney('mask', 0.0);
    $("#amountCreditCard").maskMoney('mask', 0.0);
    $("#paybackCash").maskMoney('mask', 0.0);
    bootbox.hideAll();
}

function emptyCartWebPOS() {
    $.ajax({url: 'EmptyCart',
        type: 'post',
        async: false,
        success: function(data) {
            getResultOfEmptyCartWebPOS(data);
        },
        error: function(data) {
            getResultOfEmptyCartWebPOS(data);
        }
    });
    return false;
}

function getResultOfEmptyCartWebPOS(data) {
    var serverError = getServerError(data);
    if (serverError != "") {
        bootbox.alert(serverError);
    } else {
        updateParty();
        updateCartWebPOS();
        disableChangeAfterPaid = 0;
        hideOverlayDiv();
    }
    return false;
}

function holdCart(){
    $.ajax({url: 'holdCart',
        type: 'post',
        async: false,
        success: function(data) {
            getResultOfHoldCart(data);
        },
        error: function(data) {
        	getResultOfHoldCart(data);
        }
    });
    return false;
}

function getResultOfHoldCart(data) {
    var serverError = getServerError(data);
    if (serverError != "") {
    	bootbox.alert(serverError);
    } else {
    	updateCartWebPOS();
    	updateParty();
        productToSearchFocus();
    }
    return false;
}

function loadCart(transactionId){
	var param = "transactionId="+ transactionId;
    $.ajax({url: 'loadCart',
    	data: param,
        type: 'post',
        async: false,
        success: function(data) {
            getResultOfLoadCart(data);
        },
        error: function(data) {
        	getResultOfLoadCart(data);
        }
    });
}

function getResultOfLoadCart(data) {
    var serverError = getServerError(data);
    if (serverError != "") {
    	bootbox.alert(serverError);
    } else {
    	updateCartWebPOS();
    	updateParty();
    	$('#showHoldCartWindow').jqxWindow('hide');
        productToSearchFocus();
    }
}

function confirmRemoveHeldCart(holdCartId){
	//$('#showHoldCartWindow').jqxWindow('close');
	bootbox.confirm(BPOSConfirmRemoveHeldCart, function(result){
		if (result){
			removeHeldCart(holdCartId)
		}
		$("#showHoldCartWindow").focus();
	});
}

function removeHeldCart(holdCartId){
	var param = "transactionId="+ holdCartId;
    $.ajax({url: 'RemoveHoldedCart',
    	data: param,
        type: 'post',
        async: false,
        success: function(data) {
        	getResultOfRemoveHeldCart(data);
        },
        error: function(data) {
        	getResultOfRemoveHeldCart(data);
        }
    });
}

function getResultOfRemoveHeldCart(data) {
    var serverError = getServerError(data);
    if (serverError != "") {
    	bootbox.alert(serverError);
    } else {
    	bootbox.hideAll();
    	updateViewHoldCart();
    }
}

function formatcurrency(num, uom){
	decimalseparator = ",";
 	thousandsseparator = ".";
 	currencysymbol = "đ";
 	if(typeof(uom) == "undefined" || uom == null){
 		uom = "${currencyUomId?if_exists}";
 	}
	if(uom == "USD"){
		currencysymbol = "$";
		decimalseparator = ".";
 		thousandsseparator = ",";
	}else if(uom == "EUR"){
		currencysymbol = "€";
		decimalseparator = ".";
 		thousandsseparator = ",";
	}
	if (num < 0){
		numT = num*(-1);
	} else {
		numT = num;
	}
	if(typeof(numT) == "undefined" || numT == null){
		numT = 0;
	}
    var str = numT.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
    if(str.indexOf(".") > 0) {
        parts = str.split(".");
        str = parts[0];
    }
    str = str.split("").reverse();
    for(var j = 0, len = str.length; j < len; j++) {
        if(str[j] != ",") {
            output.push(str[j]);
            if(i%3 == 0 && j < (len - 1)) {
                output.push(thousandsseparator);
            }
            i++;
        }
    }
    if (num < 0) output.push('-');
    formatted = output.reverse().join("");
    return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
}

//Check server side error
function getServerError(data) {
    var serverErrorHash = [];
    var serverError = "";
    if (data._ERROR_MESSAGE_LIST_ != undefined) {
        serverErrorHash = data._ERROR_MESSAGE_LIST_;
        $.each(serverErrorHash, function(i, error) {
          if (error != undefined) {
              if (error.message != undefined) {
                  serverError += error.message;
              } else {
                  serverError += error;
              }
            }
        });
    }
    if (data._ERROR_MESSAGE_ != undefined) {
        serverError = data._ERROR_MESSAGE_;
    }
    return serverError;
}

function limitByChar(text, maxlength) {
	if (text && maxlength) {
		if (text.length > maxlength) {
			var limited = "";
			for (var int = 0; int < maxlength; int++) {
				limited += text[int];
			}
			return limited + "...";
		}
	}
	return text;
}
