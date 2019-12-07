$(function() {
	OlbPromoView.init();
});
var OlbPromoView = (function() {
	var validatorVAL;

	var init = function() {
		validatorVAL = new OlbValidator($('#updatePromoThruDate'), [ {
			input : "#thruDate_i18n",
			type : 'validInputNotNull'
		} ]);
		initEvent();
	};
	var initEvent = function() {
		$("#showSupplierViewMore").click(function() {
			var dataViewMore = $("#supplierViewMore").html();
			jOlbUtil.alert.info(dataViewMore);
		});
	};
	var acceptProductPromo = function() {
		jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToAccept,
				function() {
					document.PromoAccept.submit();
				});

	};
	var cancelProductPromo = function() {
		jOlbUtil.confirm.dialog(
				uiLabelMap.BSAreYouSureYouWantToCancelNotAccept, function() {
					document.PromoCancel.submit();
				});
	};
	var updatePromoThruDate = function() {
		if (!validatorVAL.validate()) {
			return false;
		} else {
			jOlbUtil.confirm
					.dialog(
							uiLabelMap.BSAreYouSureYouWantToCreateUpdateThruDate,
							function() {
								document.getElementById("updatePromoThruDate")
										.submit();
							});
		}
	};
	var enterCancelPromo = function() {
		bootbox.prompt(uiLabelMap.BSPromoReasonCancelPromo, function(result) {
			if (result === null) {
			} else {
				document.getElementById('changeReason').value = "" + result;
				document.PromoCancel.submit();
			}
		});
	};
	return {
		init : init,
		acceptProductPromo : acceptProductPromo,
		cancelProductPromo : cancelProductPromo,
		updatePromoThruDate : updatePromoThruDate,
		enterCancelPromo : enterCancelPromo
	};
}());