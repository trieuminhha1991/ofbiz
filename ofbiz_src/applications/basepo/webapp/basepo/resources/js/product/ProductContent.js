$(document).ready(function() {
	Config.init();
});
if (typeof (Config) == "undefined") {
	var Config = (function() {
		var loadConfigOfProduct = function(productId) {
			var data = DataAccess.getData({
						url: "loadConfigOfProduct",
						data: {productId: productId},
						source: "config"});
			if (!_.isEmpty(data)) {
				ProductInfo.setValue(data);
				ProductImage.setValue(data);
			}
		};
		return {
			init: function() {
				if (productIdParam) {
					loadConfigOfProduct(productIdParam);
				}
			},
			loadConfigOfProduct: loadConfigOfProduct
		};
	})();
}

if (typeof (ProductInfo) == "undefined") {
	var ProductInfo = (function() {
		var setValue = function(data) {
			$("#txtEffects").html(data.effects?data.effects:multiLang.DANotData);
			$("#txtComposition").html(data.composition?data.composition:multiLang.DANotData);
			$("#txtShelfLife").html(data.shelfLife?data.shelfLife:multiLang.DANotData);
			$("#txtUsers").html(data.users?data.users:multiLang.DANotData);
			$("#txtInstructions").html(data.instructions?data.instructions:multiLang.DANotData);
			$("#txtLicense").html(data.license?data.license:multiLang.DANotData);
			$("#txtPacking").html(data.packing?data.packing:multiLang.DANotData);
			$("#txtContraindications").html(data.contraindications?data.contraindications:multiLang.DANotData);
		};
		return {
			setValue: setValue
		}
	})();
}


if (typeof (ProductImage) == "undefined") {
	var ProductImage = (function() {
		var setValue = function(data) {
			if (data.largeImageUrl) {
				$('#largeImage').attr('src', encodeURI(data.largeImageUrl));
			}
			if (data.smallImageUrl) {
				$('#smallImage').attr('src', encodeURI(data.smallImageUrl));
			}
			if (data.ADDITIONAL_IMAGE_1) {
				$('#additional1').attr('src', encodeURI(data.ADDITIONAL_IMAGE_1));
			}
			if (data.ADDITIONAL_IMAGE_2) {
				$('#additional2').attr('src', encodeURI(data.ADDITIONAL_IMAGE_2));
			}
			if (data.ADDITIONAL_IMAGE_3) {
				$('#additional3').attr('src', encodeURI(data.ADDITIONAL_IMAGE_3));
			}
			if (data.ADDITIONAL_IMAGE_4) {
				$('#additional4').attr('src', encodeURI(data.ADDITIONAL_IMAGE_4));
			}
		};
		return {
			setValue: setValue
		};
	})();
}