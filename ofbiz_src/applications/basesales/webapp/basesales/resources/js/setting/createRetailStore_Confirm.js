if (typeof (RetailStoreConfirm) == "undefined") {
	var RetailStoreConfirm = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#wn_productStoreId_label").text(data.productStoreId);
				$("#wn_storeName_label").text(data.storeName);
				$("#wn_includeOtherCustomer_label").text(data.includeOtherCustomer);
				$("#wn_payToPartyId_label").text(data.payToPartyId);
				$("#wn_salesMethodChannelEnumId_label").text(data.salesMethodChannelEnumId);
				$("#wn_defaultSalesChannelEnumId_label").text(data.defaultSalesChannelEnumId);
				$("#wn_pscata_prodCatalogId_label").text(data.prodCatalogId);
				$("#wn_vatTaxAuthPartyId_label").text(data.vatTaxAuthPartyId);
				$("#wn_vatTaxAuthGeo_label").text(data.vatTaxAuthGeoId);
				$("#wn_reserveOrderEnumId_label").text(data.reserveOrderEnumId);
				$("#wn_defaultCurrencyUomId_label").text(data.defaultCurrencyUomId);
				$("#wn_Manager_label").text(data.managerDetail);
				//$("#wn_Salesman_label").text(data.salesmanDetail);
				$("#countryGeoId_label").text(data.countryGeoName);
				$("#provinceGeoId_label").text(data.provinceGeoName);
				$("#districtGeoId_label").text(data.districtGeoName);
				$("#wardGeoId_label").text(data.wardGeoName);
				$("#phoneNumber_label").text(data.phoneNumber);
				$("#address_label").text(data.address);
			}
		};
		return {
			setValue: setValue
		}
	})();
}