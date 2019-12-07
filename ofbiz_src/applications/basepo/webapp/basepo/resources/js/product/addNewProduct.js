Loading.show();
$(document).ready(function() {
	Product.init();
	setTimeout(function() {
		if (productIdParameters) {
			updateMode = true;
			Product.letUpdate(productIdParameters);
			$("#btnSaveAndContinue").addClass("hidden");
		} else {
			$("#btnSave").html("<i class='icon-ok'></i>" + multiLang.CommonCreate);
		}
		if (productIdOrg) {
			Product.letClone(productIdOrg);
		}
	}, 100);
	setTimeout(function() {
		Loading.hide();
	}, 2000);
});
var extendSupplierProductId;
if (typeof (Product) == "undefined") {
	var Product = (function() {
		var validateDone = false;
		var initJqxElements = function() {
			$("#jqxNotificationNested").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if (ProductType.validate() && GeneralInfo.validate() && DetailsInfo.validate()) {
					Product.disable();
					Product.save(Product.returnToListProducts);
				}
			});
			$("#btnSaveAndContinue").click(function() {
				if (ProductType.validate() && GeneralInfo.validate() && DetailsInfo.validate()) {
					Product.disable();
					Product.save(Product.continueCreate);
				}
			});
		};
		var letUpdate = function(productId) {
			var data = DataAccess.getData({
						url: "loadProductInfo",
						data: {productId: productId},
						source: "product"});
			extendSupplierProductId = data.extendSupplierProductId;
			ProductType.setValue(data);
			GeneralInfo.setValue(data);
			DetailsInfo.setValue(data);
		};
		var letClone = function(productId) {
			var data = DataAccess.getData({
				url: "loadProductInfo",
				data: {productId: productId},
				source: "product"});
			data.productId = null;
			data.productCode = null;
			extendSupplierProductId = data.extendSupplierProductId;
			ProductType.setValue(data);
			GeneralInfo.setValue(data);
			DetailsInfo.setValue(data);
		};
		var returnToListProducts = function(result) {
			Loading.hide();
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
				validateDone = false;
				$("#btnNext").attr("disabled", false);
				var messageError = "";
				result["_ERROR_MESSAGE_"]?messageError=result["_ERROR_MESSAGE_"]:messageError=result["_ERROR_MESSAGE_LIST_"][0];
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
		      	$("#notificationContentNested").text(messageError);
		      	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				if (result) {
					location.href = "ProductDetail?productId=" + result['productId'];
				}
			}
		};
		var continueCreate = function(result) {
			Loading.hide();
			validateDone = false;
			$("#btnNext").attr("disabled", false);
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
				var messageError = "";
				result["_ERROR_MESSAGE_"]?messageError=result["_ERROR_MESSAGE_"]:messageError=result["_ERROR_MESSAGE_LIST_"][0];
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
		      	$("#notificationContentNested").text(messageError);
		      	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				location.reload();
			}
		};
		var save = function(command) {
			if (!validateDone) {
				validateDone = true;
				Loading.show();
				var url = "createProductDms";
				var data = _.extend(ProductType.getValue(), GeneralInfo.getValue(), DetailsInfo.getValue());
				if (locale=="vi") {
					data.weight = data.weight.toString().replaceAll(".", ",");
					data.productWeight = data.productWeight.toString().replaceAll(".", ",");
					data.productDefaultPrice = data.productDefaultPrice.toString().replaceAll(".", ",");
					data.productListPrice = data.productListPrice.toString().replaceAll(".", ",");
				}
				data.supplierProduct = SupplierProduct.getValue();
				if (updateMode) {
					url = "updateProductDms";
					data.productId = productIdParameters;
				}
				DataAccess.execute({
							url: url,
							data: data},
							command);
			}
		};
		var disable = function() {
			$("#btnNext").attr("disabled", true);
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				GeneralInfo.init();
				DetailsInfo.init();
				ProductType.init();
			},
			letUpdate: letUpdate,
			letClone: letClone,
			save: save,
			returnToListProducts: returnToListProducts,
			continueCreate: continueCreate,
			disable: disable
		}
	})();
}