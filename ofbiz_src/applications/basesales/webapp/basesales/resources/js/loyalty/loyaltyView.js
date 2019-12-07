$(function(){
	OlbLoyaltyView.init();
});
var OlbLoyaltyView = (function(){
	var validatorVAL;
	
	var init = function(){
		initValidateForm();
	};
	var initValidateForm = function(){
		validatorVAL = new OlbValidator($('#updateLoyaltyThruDate'), [{input: "#thruDate_i18n", type: 'validInputNotNull'}]);
	};
	var acceptLoyalty = function(){
		jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToAccept, function() {
            	document.LoyaltyAccept.submit();
            });
		
	};
	var cancelLoyalty = function(){
		jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCancelNotAccept, function() {
            	document.LoyaltyCancel.submit();
            });
	};
	var updateLoyaltyThruDate = function(){
		if(!validatorVAL.validate()) {
			return false;
		} else {
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreateUpdateThruDate, function() {
            	document.getElementById("updateLoyaltyThruDate").submit();
            });
		}
	};
	var enterCancelLoyalty = function(){
		bootbox.prompt(uiLabelMap.BSLoyaltyReasonCancelLoyalty, function(result) {
			if(result === null) {
			} else {
				document.getElementById('changeReason').value = "" + result;
				document.LoyaltyCancel.submit();
			}
		});
	};
	return {
		init: init,
		acceptLoyalty: acceptLoyalty,
		cancelLoyalty: cancelLoyalty,
		updateLoyaltyThruDate: updateLoyaltyThruDate,
		enterCancelLoyalty: enterCancelLoyalty
	};
}());