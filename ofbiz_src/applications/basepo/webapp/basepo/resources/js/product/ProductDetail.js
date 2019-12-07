$(document).ready(function() {
	if (productIdParam) {
		Product.letUpdate(productIdParam);
	}
});
var extendSupplierProductId;
if (typeof (Product) == "undefined") {
	var Product = (function() {
		var letUpdate = function(productId) {
			var data = DataAccess.getData({
						url: "loadProductInfo",
						data: {productId: productId},
						source: "product"});
			extendSupplierProductId = data.extendSupplierProductId;
			ProductType.setValue(data);
			GeneralInfo.setValue(data);
			DetailsInfo.setValue(data);
			SupplierInfo.loadSupplierProduct(data.supplierId);
		};
		return {
			letUpdate: letUpdate
		}
	})();
}
if (typeof (ProductType) == "undefined") {
	var ProductType = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				if (data.isVirtual == "Y") {
					$("#dpcolor").removeClass("hide");
					$(".product-feature").removeClass("hide");
					$("#productStatus").text(DmsIsVirtual);
					$("#DisplayColor").css('background-color', data.displayColor);
				} else if (data.isVariant == "Y") {
					$(".product-feature").removeClass("hide");
					$("#productStatus").text(ProductType.getProductStatusByDepartment(data));
					
				} else {
					$("#productStatus").text(ProductType.getProductStatusByDepartment(data));
				}
				if (data.feature) {
					for ( var x in productFeatureTypes) {
						var thisFeature = data.feature[productFeatureTypes[x]];
						var featureStr = "";
						for ( var z in thisFeature) {
							featureStr += mapProductFeature[thisFeature[z]] + ", ";
						}
						if (featureStr.length > 2) {
							featureStr = featureStr.substring(0, featureStr.length -2);
						}
						$("#txt" + productFeatureTypes[x]).text(featureStr);
					}
					$(".feature-container").removeClass("hide");
				}
				$("#txtProductType").text(mapProductType[data.productTypeId]);
			}
		};
		var getProductStatusByDepartment = function(data) {
			return DataAccess.getData({
				url: "getProductStatusByDepartment",
				data:{productId: data.productId},
				source: "status"});
		};
		return {
			setValue: setValue,
			getProductStatusByDepartment: getProductStatusByDepartment
		}
	})();
}
if (typeof (GeneralInfo) == "undefined") {
	var GeneralInfo = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtProductId").text(data.productCode);
				$("#txtPrimaryProductCategoryId").text(mapProductCategory[data.primaryProductCategoryId]);
				$("#txtTaxCatalogs").text(mapProductCategory[data.productCategoryTaxId]);
				$("#txtInternalName").text(data.internalName);
				$("#txtProductName").text(data.productName);
				if (data.brandName) {
					$("#txtBrandName").text(LocalUtility.getPartyName(data.brandName));
				}
				$("#description1").html(data.longDescription);
				var productCategoryId = "";
				for ( var x in data.productCategories) {
					productCategoryId += mapProductCategory[data.productCategories[x]] + ", ";
				}
				if (productCategoryId.length > 2) {
					productCategoryId = productCategoryId.substring(0, productCategoryId.length -2);
				}
				$("#txtProductCategoryId").text(productCategoryId);
			}
		};
		return {
			setValue: setValue
		}
	})();
}
if (typeof (DetailsInfo) == "undefined") {
	var DetailsInfo = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				if (data.weight) {
					$("#txtWeight").text(data.weight.toLocaleString(locale));
				}
				if (data.productWeight) {
					$("#txtProductWeight").text(data.productWeight.toLocaleString(locale));
				}
				$("#txtWeightUomId").text(mapUom[data.weightUomId]);
				$("#txtQuantityUomId").text(mapUom[data.quantityUomId]);
				if (data.productListPrice) {
					$("#txtCurrencyUomId").text(mapUom[data.productListPrice.currencyUomId]);
					if (data.productListPrice.price) {
						$("#txtProductListPrice").text(data.productListPrice.price.toLocaleString(locale));
					}
					$("#txtTaxInPrice").text(data.productListPrice.taxInPrice);
				}
				if (data.productDefaultPrice) {
					if (data.productDefaultPrice.price) {
						$("#txtProductDefaultPrice").text(data.productDefaultPrice.price.toLocaleString(locale));
					}
				}
			}
		};
		return {
			setValue: setValue
		};
	})();
}
if (typeof (SupplierInfo) == "undefined") {
	var SupplierInfo = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				if (data.partyId) {
					$("#supplierAdd").text(LocalUtility.getPartyName(data.partyId));
				}
				$("#productCurrencyUomId").text(mapUom[data.currencyUomId]);
				if (data.comments) {
					$("#comments").text(data.comments);
				}
				$("#supplierProductId").text(data.supplierProductId);
				$("#canDropShip").text(data.canDropShip);
				if (data.availableFromDate) {
					 $('#availableFromDate').text(new Date(data.availableFromDate.time).toTimeOlbius());
				}
				if (data.availableThruDate) {
					 $('#availableThruDate').text(new Date(data.availableThruDate.time).toTimeOlbius());
				}
				if (data.lastPrice) {
					$("#lastPrice").text(data.lastPrice.toLocaleString(locale));
				}
				if (data.shippingPrice) {
					$("#shippingPrice").text(data.shippingPrice.toLocaleString(locale));
				}
				if (data.minimumOrderQuantity) {
					$("#minimumOrderQuantity").text(data.minimumOrderQuantity.toLocaleString(locale));
				}
			}
		};
		var loadSupplierProduct = function(partyId) {
			if (!_.isEmpty(partyId)) {
				partyId = partyId[0].partyId;
				if (extendSupplierProductId[partyId]) {
					
					extendSupplierProductId[partyId].availableFromDate = extendSupplierProductId[partyId].availableFromDate.time;
					var data = DataAccess.getData({
						url: "loadSupplierProduct",
						data: extendSupplierProductId[partyId],
						source: "supplierProduct"});
					SupplierInfo.setValue(data);
				}
			}
		};
		return {
			setValue: setValue,
			loadSupplierProduct: loadSupplierProduct
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