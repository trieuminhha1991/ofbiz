if (typeof (ProductType) == "undefined") {
	var ProductType = (function() {
		var initJqxElements = function() {
			$("#colorPicker").slideUp("fast");
			$("#txtProductType").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listProductType, displayMember: "description", valueMember: "productTypeId", placeHolder: multiLang.filterchoosestring});
			$("#txtProductType").jqxDropDownList("val", "FINISHED_GOOD");
			$("#IsVirtual").jqxCheckBox({ width: 10});
			$("#IsVariant").jqxCheckBox({ width: 10});
			
			$("#jqxDisplayColor").jqxColorPicker({ width: "250", height: "250"});
			$("#jqxDisplayColor").jqxColorPicker("setColor", "fff");
			
			$(".jqx-color-picker-preview").css("display", "none");
			$("#displayColor").css("background-color", $("#jqxDisplayColor").val());
			$("#selectColor").css("background-color", $("#jqxDisplayColor").val());
			
			$("#divVariantFromProduct").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listProductVirtual, displayMember: "internalName", valueMember: "productId", placeHolder: multiLang.filterchoosestring, dropDownHeight: 150});
		};
		var handleEvents = function() {
			$("#IsVirtual").on("change", function (event) {
		        var checked = event.args.checked;
		        if (checked) {
		        	if ($("#IsVariant").jqxCheckBox("val")) {
		        		$("#IsVariant").jqxCheckBox("unCheck");
					}
		        	Virtual.activate();
		        	Feature.activate();
				} else {
					Virtual.deactivate();
					Feature.deactivate();
				}
		    });
			$("#IsVariant").on("change", function (event) {
				var checked = event.args.checked;
				if (checked) {
					if ($("#IsVariant").jqxCheckBox("val")) {
						$("#IsVirtual").jqxCheckBox("unCheck");
					}
					Variant.activate();
					Feature.activate();
				} else {
					Variant.deactivate();
					Feature.deactivate();
				}
			});
			
			$("#jqxDisplayColor").on("colorchange", function (event){ 
			    var color = event.args;
			    $("#displayColor").css("background-color", $("#jqxDisplayColor").val());
			    $("#displayColorTotal").css("background-color", $("#jqxDisplayColor").val());
			    $("#selectColor").css("background-color", $("#jqxDisplayColor").val());
			});
			$("#selectColor").click(function() {
				$("#colorPicker").slideUp("slow");
			});
			$("#displayColor").on("click", function() {
				$("#colorPicker").slideDown("slow");
			});
			
			$("#divVariantFromProduct").on("select", function (event){
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    if (value) {
				    	$(".lblProductName").text(label);
				    	Feature.reloadSource(value);
				    	if (!updateMode) {
				    		var data = DataAccess.getData({
								url: "loadProductInfo",
								data: {productId: value},
								source: "product"});
					    	if (data) {
					    		data.productId = null;
					    		data.productCode = null;
								GeneralInfo.setValue(data);
								DetailsInfo.setValue(data);
							}
						}
				    	$(".add-feature").removeClass("hidden");
					}
				}                   
			});
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				Feature.setValue(data);
				
				$("#txtProductType").jqxDropDownList("val", data.productTypeId);
				if (data.isVirtual == "Y") {
					$("#IsVirtual").jqxCheckBox("val", true);
					$("#jqxDisplayColor").jqxColorPicker("setColor", data.displayColor);
				}
				if (data.isVariant == "Y") {
					$("#divVariantFromProduct").jqxDropDownList("val", data.productIdTo);
					$("#divVariantFromProduct").jqxDropDownList({ disabled: true });
					
					$("#IsVariant").jqxCheckBox("val", true);
				}
				$("#IsVirtual").jqxCheckBox({disabled: true });
				$("#IsVariant").jqxCheckBox({disabled: true });
			}
		};
		var getValue = function() {
			var value = new Object();
			var isVirtual = "";
			$("#IsVirtual").jqxCheckBox("val")?isVirtual="Y":isVirtual="N";
			value.isVirtual = isVirtual;
			var isVariant = "";
			$("#IsVariant").jqxCheckBox("val")?isVariant="Y":isVariant="N";
			value.isVariant = isVariant;
			value.productIdTo = $("#divVariantFromProduct").jqxDropDownList("val");
			value.displayColor = $("#jqxDisplayColor").val();
			value.productTypeId = $("#txtProductType").jqxDropDownList("val");
			value.feature = JSON.stringify(Feature.getValue());
			return value;
		};
		var initValidator = function() {
			$("#step1").jqxValidator({
			    rules: [{ input: "#divVariantFromProduct", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (!$("#IsVariant").jqxCheckBox("val")) {
									return true;
								}
								if (input.val()) {
									return true;
								}
								return false;
							}
						}],
		           position: "bottom"
			});
		};
		var validate = function() {
			return $("#step1").jqxValidator("validate");
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
				Feature.init();
			},
			setValue: setValue,
			getValue: getValue,
			validate: validate
		}
	})();
}
if (typeof (Variant) == "undefined") {
	var Variant = (function() {
		var activate = function() {
			$(".variant").removeClass("hidden");
			if ($("#divVariantFromProduct").jqxDropDownList("val")) {
				$(".add-feature").removeClass("hidden");
			}
			Virtual.deactivate();
			Feature.multiSelect(false);
		};
		var deactivate = function() {
			$(".variant").removeClass("hidden");
			$(".variant").addClass("hidden");
			$(".add-feature").removeClass("hidden");
			$(".add-feature").addClass("hidden");
		};
		return {
			activate: activate,
			deactivate: deactivate,
		}
	})();
}
if (typeof (Virtual) == "undefined") {
	var Virtual = (function() {
		var activate = function() {
			$(".virtual").removeClass("hidden");
			Variant.deactivate();
			Feature.multiSelect(true);
		};
		var deactivate = function() {
			$(".virtual").removeClass("hidden");
			$(".virtual").addClass("hidden");
		};
		return {
			activate: activate,
			deactivate: deactivate
		}
	})();
}
