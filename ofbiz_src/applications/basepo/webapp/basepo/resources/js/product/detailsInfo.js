if (typeof (DetailsInfo) == "undefined") {
	var DetailsInfo = (function() {
		var initJqxElements = function() {
			$("#popupConfigPacking").jqxWindow({width: 500, maxWidth: 1000, theme: "olbius", minHeight: 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#configPackingCancel"), modalOpacity: 0.7});
	    	$("#uomFromId1").jqxDropDownList({ source: listUoms, width: "100%", displayMember: "description", valueMember: "uomId", theme: "olbius", placeHolder: filterchoosestring, autoDropDownHeight: true });
//	    	$("#uomFromIdBaseProduct").jqxDropDownList({ source: listUoms, width: "100%", displayMember: "description", valueMember: "uomId", theme: "olbius", placeHolder: "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}", autoDropDownHeight: true });
	    	$("#quantityConvert1").jqxNumberInput({ inputMode: "simple", spinButtons: true, theme: "olbius", width: "100%", decimalDigits: 0, min: 1, decimal: 1});
	    	$("#fromDate1").jqxDateTimeInput({theme: "olbius", width: "100%" });
	    	$("#thruDate1").jqxDateTimeInput({theme: "olbius", width: "100%",allowNullDate: true, value: null });
			
			$("#txtQuantityUomId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listQuantityUom, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#txtWeightUomId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listWeightUom, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#txtTaxInPrice").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: ["Y", "N"], placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
			$("#txtTaxInPrice").jqxDropDownList("val", "N");
			$("#txtCurrencyUomId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listCurrencyUom, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#txtCurrencyUomId").jqxDropDownList("val", "VND");
			$("#txtWeight").jqxNumberInput({ theme: "olbius", width: 218, height: 30, inputMode: "advanced", spinButtons: true, decimalDigits: 3 });
			$("#txtProductWeight").jqxNumberInput({ theme: "olbius", width: 218, height: 30, inputMode: "advanced", spinButtons: true, decimalDigits: 3 });
			$("#txtProductDefaultPrice").jqxNumberInput({ theme: "olbius", width: 218, height: 30, inputMode: "advanced", spinButtons: true, decimalDigits: 1 });
			$("#txtProductListPrice").jqxNumberInput({ theme: "olbius", width: 218, height: 30, inputMode: "advanced", spinButtons: true, decimalDigits: 1 });
			$("#txtDayN").jqxNumberInput({ theme: 'olbius', width: 218, height: 30, inputMode: 'simple', spinButtons: true, decimalDigits: 0 });
			$("#txtShelflife").jqxNumberInput({ theme: 'olbius', width: 218, height: 30, inputMode: 'simple', spinButtons: true, decimalDigits: 0 });
			setTimeout(function() {
				if (locale=="vi") {
					$("#txtWeight").jqxNumberInput({ decimalSeparator: ",", groupSeparator: "." });
					$("#txtProductWeight").jqxNumberInput({ decimalSeparator: ",", groupSeparator: "." });
					$("#txtProductDefaultPrice").jqxNumberInput({ decimalSeparator: ",", groupSeparator: "." });
					$("#txtProductListPrice").jqxNumberInput({ decimalSeparator: ",", groupSeparator: "." });
					$("#txtDayN").jqxNumberInput({ decimalSeparator: ',' });
					$("#txtShelflife").jqxNumberInput({ decimalSeparator: ',' });
				}
			}, 50);
		};
		var handleEvents = function() {
//			$("#txtQuantityUomId").on("select", function(event) {
//				
//				if(!updateMode){
//					var args = event.args;
//				    if (args) {
//					    var index = args.index;
//					    var item = args.item;
//					    var label = item.label;
//					    var value = item.value;
//					    //open window
//					    $("#uomFromIdBaseProduct").text(label);
//					    $("#popupConfigPacking").jqxWindow("open");
//					    $("#addNewConfig").css("display", "block");
//				    }
//				}
//			});
			$("#configPackingSave").on("click", function() {
			    $("#popupConfigPacking").jqxWindow("close");
			});
			$("#configPackingCancel").on("click", function() {
			    $("#popupConfigPacking").jqxWindow("close");
			    $("#uomFromId1").jqxDropDownList("clearSelection");
			});
			$("#addNewConfig").on("click", function() {
			    $("#popupConfigPacking").jqxWindow("open");
			});
			
			$("#txtQuantityUomId").on("change", function(event) {
				if (productIdParameters && $("#txtCurrencyUomId").jqxDropDownList("val") && $("#txtTaxInPrice").jqxDropDownList("val")) {
					var args = event.args;
				    if (args) {
					    var index = args.index;
					    var item = args.item;
					    var label = item.label;
					    var value = item.value;
					    getProductPrice(value, $("#txtCurrencyUomId").jqxDropDownList("val"), $("#txtTaxInPrice").jqxDropDownList("val"));
				    }
				}
			});
			$("#txtCurrencyUomId").on("change", function(event) {
				if (productIdParameters && $("#txtQuantityUomId").jqxDropDownList("val") && $("#txtTaxInPrice").jqxDropDownList("val")) {
					var args = event.args;
					if (args) {
						var index = args.index;
						var item = args.item;
						var label = item.label;
						var value = item.value;
						getProductPrice($("#txtQuantityUomId").jqxDropDownList("val"), value, $("#txtTaxInPrice").jqxDropDownList("val"));
					}
				}
			});
			$("#txtTaxInPrice").on("change", function(event) {
				if (productIdParameters && $("#txtQuantityUomId").jqxDropDownList("val") && $("#txtCurrencyUomId").jqxDropDownList("val")) {
					var args = event.args;
					if (args) {
						var index = args.index;
						var item = args.item;
						var label = item.label;
						var value = item.value;
						getProductPrice($("#txtQuantityUomId").jqxDropDownList("val"), $("#txtCurrencyUomId").jqxDropDownList("val"), value);
					}
				}
			});
		};
		var initValidator = function() {
			$("#step3").jqxValidator({
			    rules: [{ input: "#txtWeight", message: multiLang.DmsWeightNotValid, action: "change", 
							rule: function (input, commit) {
								if (input.jqxNumberInput("getDecimal") > 0) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductWeight", message: multiLang.DmsWeightNotValid, action: "change", 
							rule: function (input, commit) {
								if (input.jqxNumberInput("getDecimal") > 0) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtWeightUomId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtQuantityUomId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtCurrencyUomId", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if ($("#IsVariant").val()) {
									return true;
								}
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtTaxInPrice", message: multiLang.fieldRequired, action: "change", 
							rule: function (input, commit) {
								if ($("#IsVariant").val()) {
									return true;
								}
								if (input.jqxDropDownList("val")) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductDefaultPrice", message: multiLang.DmsPriceNotValid, action: "valueChanged", 
							rule: function (input, commit) {
								if (input.jqxNumberInput("getDecimal") >= 0) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductListPrice", message: multiLang.DmsPriceNotValid, action: "valueChanged", 
							rule: function (input, commit) {
								if (input.jqxNumberInput("getDecimal") >= 0) {
									return true;
								}
								return false;
							}
						}],
		           position: "bottom"
			});
		};
		var validate = function() {
			return $("#step3").jqxValidator("validate");
		};
		var getProductPrice = function(termUomId, currencyUomId, taxInPrice) {
			var data = DataAccess.getData({
						url: "loadProductPrice",
						data: {productId: productIdParameters, termUomId: termUomId, currencyUomId: currencyUomId, taxInPrice: taxInPrice},
						source: "productPrice"});
			if (!_.isEmpty(data)) {
				$("#txtProductListPrice").jqxNumberInput("setDecimal", data.productListPrice);
				$("#txtProductDefaultPrice").jqxNumberInput("setDecimal", data.productDefaultPrice);
			}
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtWeight").jqxNumberInput("setDecimal", data.weight);
				$("#txtProductWeight").jqxNumberInput("setDecimal", data.productWeight);
				if (data.dayN) {
					$("#txtDayN").jqxNumberInput('setDecimal', data.dayN);
				}
				if (data.shelflife) {
					$("#txtShelflife").jqxNumberInput('setDecimal', data.shelflife);
				}
				$("#txtWeightUomId").jqxDropDownList("val", data.weightUomId);
				$("#txtQuantityUomId").jqxDropDownList("val", data.quantityUomId);
				if (data.productListPrice) {
					$("#txtCurrencyUomId").jqxDropDownList("val", data.productListPrice.currencyUomId);
					$("#txtProductListPrice").jqxNumberInput("setDecimal", data.productListPrice.price);
					$("#txtTaxInPrice").jqxDropDownList("val", data.productListPrice.taxInPrice);
				}
				if (data.productDefaultPrice) {
					$("#txtProductDefaultPrice").jqxNumberInput("setDecimal", data.productDefaultPrice.price);
				}
				$("#txtQuantityUomId").jqxDropDownList({ disabled: true });
			}
		};
		var getValue = function() {
			var value = new Object();
			value.quantityUomId = $("#txtQuantityUomId").jqxDropDownList("val");
			value.weightUomId = $("#txtWeightUomId").jqxDropDownList("val");
			value.weight = $("#txtWeight").jqxNumberInput("getDecimal");
			value.dayN = $("#txtDayN").jqxNumberInput('getDecimal');
			value.shelflife = $("#txtShelflife").jqxNumberInput('getDecimal');
			value.productWeight = $("#txtProductWeight").jqxNumberInput("getDecimal");
			value.currencyUomId = $("#txtCurrencyUomId").jqxDropDownList("val");
			value.productDefaultPrice = $("#txtProductDefaultPrice").jqxNumberInput("getDecimal");
			value.productListPrice = $("#txtProductListPrice").jqxNumberInput("getDecimal");
			value.taxInPrice = $("#txtTaxInPrice").jqxDropDownList("val");
			//config packing
			if($("#uomFromId1").val() != null && $("#uomFromId1").val() != ""){
				value.uomFromId = $("#uomFromId1").val();
				value.quantityConvert = $("#quantityConvert1").val();
				value.fromDateConfigPacking = $("#fromDate1").jqxDateTimeInput("getDate").getTime();
				if($("#thruDate1").jqxDateTimeInput("getDate") != null){
					value.thruDateConfigPacking = $("#thruDate1").jqxDateTimeInput("getDate").getTime();
				}
			}
			value.dayN==0?value.dayN=null:value.dayN;
			value.shelflife==0?value.shelflife=null:value.shelflife;
			return value;
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			validate: validate,
			setValue: setValue,
			getValue: getValue
		};
	})();
}