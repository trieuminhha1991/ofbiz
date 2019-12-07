$(document).ready(function() {
	AddProductPrice.init();
});
if (typeof (AddProductPrice) == "undefined") {
	var AddProductPrice = (function() {
		var initJqxElements = function() {
			$("#addProductPrice").jqxWindow({
				theme: "olbius", width: 850, maxWidth: 2000, height: 310, maxHeight: 1000, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancel"), modalOpacity: 0.7
			});
			$("#divProductPriceType").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: productPriceTypes, displayMember: "description", valueMember: "productPriceTypeId", placeHolder: multiLang.filterchoosestring});
			$("#divProductCurrencyUom").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: currencyUoms, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#divProductCurrencyUom").jqxDropDownList("val", "VND");
			$("#divQuantityUomId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: quantityUoms, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#divProductPrice").jqxNumberInput({ theme: "olbius", width: 218, height: 30, inputMode: "advanced", spinButtons: true, decimalDigits: 3 });
			$("#divFromDate").jqxDateTimeInput({theme: "olbius", width: 218, height: 30 });
			$("#divThruDate").jqxDateTimeInput({theme: "olbius", width: 218, height: 30 });
			$("#divThruDate").jqxDateTimeInput("setDate", null);
			
			setTimeout(function() {
				if (locale=="vi") {
					$("#divProductPrice").jqxNumberInput({ decimalSeparator: ',', groupSeparator: "." });
				}
			}, 50);
		};
		var handleEvents = function() {
			$("#btnSave").click(function(){
				if ($("#addProductPrice").jqxValidator("validate")) {
					if (gridSelecting) {
						if ($("#addProductPrice").data("update")) {
							var rowIndexSelected = gridSelecting.jqxGrid('getSelectedRowindex');
							var rowData = gridSelecting.jqxGrid('getrowdata', rowIndexSelected);
							gridSelecting.jqxGrid("updaterow", rowData.uid, AddProductPrice.getValue());
						} else {
							gridSelecting.jqxGrid("addrow", null, AddProductPrice.getValue());
						}
						$("#addProductPrice").jqxWindow("close");
					}
				}
			});
			$("#addProductPrice").on("close",function(){
				$("#addProductPrice").jqxValidator("hide");
				$("#divProductPriceType").jqxDropDownList("clearSelection");
				$("#divQuantityUomId").jqxDropDownList("clearSelection");
				$("#divProductPrice").jqxNumberInput("val", 0);
				$("#divThruDate").jqxDateTimeInput("setDate", null);
				enable();
			});
		};
		var initValidator = function() {
			$("#addProductPrice").jqxValidator({
			    rules: [
						{ input: "#divProductPriceType", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								var value = input.jqxDropDownList("val");
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divProductCurrencyUom", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								var value = input.jqxDropDownList("val");
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divQuantityUomId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								var value = input.jqxDropDownList("val");
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divProductPrice", message: multiLang.DmsPriceNotValid, action: "valueChanged", 
							rule: function (input, commit) {
								var value = input.jqxNumberInput("getDecimal");
								if (value >= 0) {
									return true;
								}
								return false;
							}
						},
			            { input: "#divFromDate", message: multiLang.dateNotValid, action: "valueChanged", 
				           	rule: function (input, commit) {
				           		var currentTime = new Date().getTime();
				           		var value = 0;
				           		input.jqxDateTimeInput("getDate")?value=input.jqxDateTimeInput("getDate").getTime():value;
				           		if (value == 0) {
									return true;
								}
				           		if (currentTime > value) {
				           			return true;
				           		}
				           		return false;
				           	}
				        }]
			});
		};
		var getValue = function() {
			var value = new Object();
			value.productId = productIdParam;
			value.productPriceTypeId = $("#divProductPriceType").jqxDropDownList("val");
			value.productPricePurposeId = "PURCHASE";
			value.currencyUomId = $("#divProductCurrencyUom").jqxDropDownList("val");
			value.productStoreGroupId = "_NA_";
			value.price = $("#divProductPrice").jqxNumberInput("getDecimal");
			value.termUomId = $("#divQuantityUomId").jqxDropDownList("val");
			if ($("#divFromDate").jqxDateTimeInput("getDate")) {
				value.fromDate = $("#divFromDate").jqxDateTimeInput("getDate").getTime();
			}
			if ($("#divThruDate").jqxDateTimeInput("getDate")) {
				value.thruDate = $("#divThruDate").jqxDateTimeInput("getDate").getTime();
			}
			return value;
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#divProductPriceType").jqxDropDownList("val", data.productPriceTypeId);
				$("#divProductCurrencyUom").jqxDropDownList("val", data.currencyUomId);
				if (data.price) {
					$("#divProductPrice").jqxNumberInput("setDecimal", data.price.toString());
				}
				$("#divQuantityUomId").jqxDropDownList("val", data.termUomId);
				if (data.fromDate) {
					$("#divFromDate").jqxDateTimeInput("setDate", new Date(data.fromDate.time));
				}
				if (data.thruDate) {
					$("#divThruDate").jqxDateTimeInput("setDate", new Date(data.thruDate.time));
				}
				disable();
			}
		};
		var disable = function() {
			$("#divProductPriceType").jqxDropDownList({ disabled: true });
			$("#divProductCurrencyUom").jqxDropDownList({ disabled: true });
			$("#divQuantityUomId").jqxDropDownList({ disabled: true });
			$("#divFromDate").jqxDateTimeInput({ disabled: true });
		};
		var enable = function() {
			$("#divProductPriceType").jqxDropDownList({ disabled: false });
			$("#divProductCurrencyUom").jqxDropDownList({ disabled: false });
			$("#divQuantityUomId").jqxDropDownList({ disabled: false });
			$("#divFromDate").jqxDateTimeInput({ disabled: false });
		};
		var open = function(productId, grid) {
			if (grid) {
				gridSelecting = grid;
			}
			if (productId) {
				$("#addProductPrice").data("update", false);
			} else {
				$("#addProductPrice").data("update", true);
			}
			var wtmp = window;
			var tmpwidth = $("#addProductPrice").jqxWindow("width");
	        $("#addProductPrice").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#addProductPrice").jqxWindow("open");
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			open: open,
			getValue: getValue,
			setValue: setValue,
			disable: disable
		};
	})();
}