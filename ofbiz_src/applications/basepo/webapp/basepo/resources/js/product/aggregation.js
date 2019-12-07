if (typeof (Aggregation) == "undefined") {
	var Aggregation = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#productIdTotal").text(data.productCode);
				$("#productCategoryIdTotal").text(mapProductCategory[data.primaryProductCategoryId]);
				$("#taxCatalogsTotal").text(mapTaxCategory[data.taxCatalogs]);
				$("#productInternalNameTotal").text(data.internalName);
				$("#productNameTotal").text(data.productName);
				if (data.brandName) {
					$("#productBrandNameTotal").text(LocalUtility.getPartyName(data.brandName));
				}
				$("#weightTotal").text(data.weight.toLocaleString(locale));
				$("#netWeightTotal").text(data.productWeight.toLocaleString(locale));
				$("#weightUomIdTotal").text(mapWeightUom[data.weightUomId]);
				$("#quantityUomIdTotal").text(mapQuantityUom[data.quantityUomId]);
				$("#ProductTasteTotal").text(getlabelSelectedJqxComboBox('ProductTaste'));
				$("#ProductSizeTotal").text(getlabelSelectedJqxComboBox('ProductSize'));
				$("#ProductColorTotal").text(getlabelSelectedJqxComboBox('ProductColor'));
				if ($("#IsVirtual").val()) {
					$("#divdisplayColorTotal").removeClass('hide');
					$("#displayColorTotal").css('background-color', $("#jqxDisplayColor").val());
				}else {
					$("#divdisplayColorTotal").addClass('hide');
				}
				$("#descriptionTotal").html(data.longDescription);
				$("#productDefaultPriceTotal").text(data.productDefaultPrice.toLocaleString(locale));
				$("#productListPriceTotal").text(data.productListPrice.toLocaleString(locale));
				$("#currencyUomIdTotal").text(mapCurrencyUom[data.currencyUomId]);
				$("#TaxInPriceTotal").text(data.taxInPrice);
			}
		};
		return {
			setValue: setValue
		};
	})();
}
if (typeof (LocalUtility) == "undefined") {
	var LocalUtility = (function () {
		var getPartyName = function (partyId) {
			if (partyId) {
				return DataAccess.getData({
					url: "getPartyName",
					data: {partyId: partyId},
					source: "partyName"});
			}
		};
		return {
			getPartyName: getPartyName
		};
	})();
}