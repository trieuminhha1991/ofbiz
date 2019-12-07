$(document).ready(function() {
	Config.init();
});
if (typeof (Config) == "undefined") {
	var Config = (function() {
		var initJqxElements = function() {
			$('#fuelux-wizard').ace_wizard();
			$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$('#fuelux-wizard').on('change' , function(e, info) {

			}).on('finished', function(e) {
				Config.disable();
				Config.uploadImages();
			});
		};
		var loadConfigOfProduct = function(productId) {
			var data = DataAccess.getData({
						url: "loadConfigOfProduct",
						data: {productId: productId},
						source: "config"});
			if (!_.isEmpty(data)) {
//				ConfigCategory.setValue(data);
				ProductInfo.setValue(data);
				ProductImage.setValue(data);
			}
		};
		var uploadImages = function() {
			var uploadedImages = ProductImage.getValue();
			if (uploadedImages.largeImageUrl) {
				uploadedImages.largeImageUrl = DataAccess.uploadFile(uploadedImages.largeImageUrl);
			}
			if (uploadedImages.smallImageUrl) {
				uploadedImages.smallImageUrl = DataAccess.uploadFile(uploadedImages.smallImageUrl);
			}

			if (uploadedImages.ADDITIONAL_IMAGE_1) {
				uploadedImages.ADDITIONAL_IMAGE_1 = DataAccess.uploadFile(uploadedImages.ADDITIONAL_IMAGE_1);
			}
			if (uploadedImages.ADDITIONAL_IMAGE_2) {
				uploadedImages.ADDITIONAL_IMAGE_2 = DataAccess.uploadFile(uploadedImages.ADDITIONAL_IMAGE_2);
			}
			if (uploadedImages.ADDITIONAL_IMAGE_3) {
				uploadedImages.ADDITIONAL_IMAGE_3 = DataAccess.uploadFile(uploadedImages.ADDITIONAL_IMAGE_3);
			}
			if (uploadedImages.ADDITIONAL_IMAGE_4) {
				uploadedImages.ADDITIONAL_IMAGE_4 = DataAccess.uploadFile(uploadedImages.ADDITIONAL_IMAGE_4);
			}
			save(uploadedImages);
		};
		var save = function(uploadedImages) {
			var data = _.extend(ProductInfo.getValue(), uploadedImages);
			DataAccess.execute({
						url: "configProduct",
						data: data},
						Config.notify);
		};
		var disable = function() {
			$("#btnNext").attr("disabled", true);
		};
		var notify = function(res) {
			$('body').scrollTop(0);
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
				$("#notificationContentNested").text(errormes);
				$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'});
				$("#notificationContentNested").text(multiLang.updateSuccess);
				$("#jqxNotificationNested").jqxNotification("open");
				setTimeout(function() {
//			      		window.location.href = "ProductCategories";
					location.reload();
				}, 1000);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
//				ConfigCategory.init();
				ProductInfo.init();
				ProductImage.init();
				if (productIdParam) {
					loadConfigOfProduct(productIdParam);
				}
			},
			loadConfigOfProduct: loadConfigOfProduct,
			disable: disable,
			uploadImages: uploadImages,
			notify: notify
		};
	})();
}