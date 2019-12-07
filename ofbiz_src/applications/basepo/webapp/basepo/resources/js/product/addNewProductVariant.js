$(document).ready(function() {
	AddProductVariant.init();
});
if (typeof (AddProductVariant) == "undefined") {
	var AddProductVariant = (function() {
		var productId, extendData = new Object(), jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: "olbius", width: 900, maxWidth: 1000, minHeight: 460, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancelProductVariant"), modalOpacity: 0.7
			});
			
			$("#txtDescription").jqxEditor({
			    theme: "olbiuseditor",
			    width:"97.2%"
			});
		};
		var handleEvents = function() {
			jqxwindow.on("close", function() {
				productId = null;
				extendData = new Object();
				$("#txtDescription").jqxEditor("val", "");
				$("#txtProductCode").val("");
				$("#txtInternalName").val("");
				$("#txtProductName").val("");
			});
			$("#btnSaveProductVariant").click(function(){
				if (jqxwindow.jqxValidator("validate")) {
					DataAccess.execute({
						url: "createProductDms",
						data: AddProductVariant.getValue()},
						AddProductVariant.notify);
					$("#jqxgrid").jqxGrid("updatebounddata");
					jqxwindow.jqxWindow("close");
				}
			});
			jqxwindow.on("close",function(){
				jqxwindow.jqxValidator("hide");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
			    rules: [{ input: "#txtProductCode", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtProductCode", message: multiLang.containSpecialSymbol, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductCode", message: multiLang.ProductIdAlreadyExists, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									var check = DataAccess.getData({
										url: "checkProductCode",
										data: {productCode: value, productId: null},
										source: "check"});
									if ("false" == check) {
										 return false;
									}
								}
								return true;
							}
						},
						{ input: "#txtInternalName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtProductName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" }]
			});
		};
		var open = function(product) {
			if (product) {
				productId = product.productId;
		    	
		    	data = DataAccess.getData({
					url: "loadProductInfo",
					data: {productId: productId},
					source: "product"});
		    	if (data) {
		    		extendData.brandName = data.brandName;
		    		extendData.primaryProductCategoryId = data.primaryProductCategoryId;
		    		extendData.productCategoryId = data.productCategories;
		    		extendData.taxCatalogs = data.productCategoryTaxId;
		    		extendData.productCategories = data.productCategories;
		    		extendData.productTypeId = data.productTypeId;
		    		extendData.weight = data.weight;
		    		extendData.productWeight = data.productWeight;
		    		extendData.quantityUomId = data.quantityUomId;
		    		extendData.weightUomId = data.weightUomId;
	    			extendData.currencyUomId = data.productListPrice?data.productListPrice.currencyUomId:"VND";
	    			extendData.taxInPrice = data.productListPrice?data.productListPrice.taxInPrice: "N";
	    			extendData.productDefaultPrice = 0;
	    			extendData.productListPrice = 0;
				}
		    	AddProductVariant.setValue(product);
			}
			var wtmp = window;
	    	var tmpwidth = jqxwindow.jqxWindow("width");
	        jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
	    	jqxwindow.jqxWindow("open");
	    	$("#txtProductCode").focus();
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtProductCode").val(data.productCode);
				$("#txtInternalName").val(data.internalName);
				$("#txtProductName").val(data.productName);
			}
		};
		var getValue = function() {
			var value = new Object();
			value.isVirtual = "N";
			value.isVariant = "Y";
			value.productIdTo = productId;
			value.productCode = $("#txtProductCode").val();
			value.internalName = $("#txtInternalName").val();
			value.productName = $("#txtProductName").val();
			value.longDescription = $("#txtDescription").jqxEditor("val");
			value.feature = JSON.stringify(Feature.getValue());
			return _.extend(value, extendData);
		};
		var notify = function(result) {
			$("#jqxNotificationNested").jqxNotification("closeLast");
			if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
				var messageError = "";
				result["_ERROR_MESSAGE_"]?messageError=result["_ERROR_MESSAGE_"]:messageError=result["_ERROR_MESSAGE_LIST_"][0];
				$("#jqxNotificationNested").jqxNotification({ template: "error"});
		      	$("#notificationContentNested").text(messageError);
		      	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: "info"});
		      	$("#notificationContentNested").text(multiLang.updateSuccess);
		      	$("#jqxNotificationNested").jqxNotification("open");
			}
		};
		return {
			init: function() {
				jqxwindow = $("#addProductVariant");
				initJqxElements();
				handleEvents();
				initValidator();
				Feature.init();
			},
			open: open,
			notify: notify,
			setValue: setValue,
			getValue: getValue
		};
	})();
}