if (typeof (GeneralInfoReview) == "undefined") {
	var GeneralInfoReview = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#supplierConfirm").text(data.supplierName);
				$("#numberPOConfirm").text(data.multiPO?uiLabelMap.Yes:uiLabelMap.No);
				$("#shipAfterDateConfirm").text(DatetimeUtilObj.formatFullDate(data.shipAfterDate));
				$("#shipBeforeDateConfirm").text(DatetimeUtilObj.formatFullDate(data.shipBeforeDate));
				$("#facilityConfirm").text(data.facilityName);
			}
		};
		return {
			setValue: setValue
		};
	})();
}