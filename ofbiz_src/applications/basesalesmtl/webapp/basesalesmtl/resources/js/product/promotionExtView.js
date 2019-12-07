$(function(){
	OlbPromoView.init();
});
var OlbPromoView = (function(){
	var validatorVAL;
	
	var init = function(){
		/*jOlbUtil.dateTimeInput.create("#thruDate", {width: '220', allowNullDate: true, value: null, showFooter: true});*/
		validatorVAL = new OlbValidator($('#updatePromoThruDate'), [{input: "#thruDate_i18n", type: 'validInputNotNull'}]);
		/*if (typeof(promotionThruDate) != "undefined") {
			$("#thruDate").jqxDateTimeInput("val", promotionThruDate);
		}*/
	};
	var acceptProductPromo = function(){
		jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToAccept, function() {
            	document.PromoAccept.submit();
            });
		
	};
	var cancelProductPromo = function(){
		jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCancelNotAccept, function() {
            	document.PromoCancel.submit();
            });
	};
	var updatePromoThruDate = function(){
		if(!validatorVAL.validate()) {
			return false;
		} else {
			/*var thruDateLong = null;
			if (typeof($('#thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#thruDate').jqxDateTimeInput('getDate') != null) {
				thruDateLong = $('#thruDate').jqxDateTimeInput('getDate').getTime();
				$("#thruDateHide").val(thruDateLong);
			}
			if (!OlbElementUtil.isNotEmpty(thruDateLong)) {
				jOlbUtil.alert.error(uiLabelMap.BSThruDateMustNotBeEmpty);
				return false;
			}*/
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreateUpdateThruDate, function() {
            	document.getElementById("updatePromoThruDate").submit();
            });
		}
	};
	var enterCancelPromo = function(){
		bootbox.prompt(uiLabelMap.BSPromoReasonCancelPromo, function(result) {
			if(result === null) {
			} else {
				document.getElementById('changeReason').value = "" + result;
				document.PromoCancel.submit();
			}
		});
	};
	return {
		init: init,
		acceptProductPromo: acceptProductPromo,
		cancelProductPromo: cancelProductPromo,
		updatePromoThruDate: updatePromoThruDate,
		enterCancelPromo: enterCancelPromo
	};
}());