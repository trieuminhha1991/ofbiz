if (typeof (AddConfigPrintOrder) == "undefined") {
	var AddConfigPrintOrder = (function() {
		var jqxwindow, extendsValue = new Object();
		var initJqxElements = function() {
			$("#txtHeaderFontSize").jqxNumberInput({ width: 250, height: 25, spinButtons: true, min: 0, decimalDigits: 0, digits: 5 });
			$("#txtInfoFontSize").jqxNumberInput({ width: 250, height: 25, spinButtons: true, min: 0, decimalDigits: 0, digits: 5 });
			$("#txtContentFontSize").jqxNumberInput({ width: 250, height: 25, spinButtons: true, min: 0, decimalDigits: 0, digits: 5 });
			$("#txtLogo").ace_file_input({
				no_file: multiLang.BPOSNoFile,
				btn_choose: multiLang.BPOSChoose,
				btn_change: multiLang.BPOSChange,
				droppable: false,
				onchange: null,
				thumbnail: false
			});
			jOlbUtil.windowPopup.create(jqxwindow, {width: "99%", height: 450, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7});
			$("#splitterConfigPrint").jqxSplitter({ width: "100%", height: "95%", panels: [{size: "70%", min: "70%", collapsible: false}, { size: "60%" }], splitBarSize: 0, resizable: false });
			
			var configProductStore = {
				placeHolder: multiLang.filterchoosestring,
				useUrl: false,
				key: "productStoreId",
				value: "storeName",
				autoDropDownHeight: true,
				width: 250
			}
			new OlbDropDownList($("#txtProductStore"), productStoreListData, configProductStore, []);
			
			var configFont = {
				placeHolder: multiLang.filterchoosestring,
				useUrl: false,
				key: "fontTypeId",
				value: "description",
				autoDropDownHeight: true,
				width: 250
			}
			new OlbDropDownList($("#txtFontFamily"), fontTypeData, configFont, []);
			
			var configSelectPrint = {
				placeHolder: multiLang.filterchoosestring,
				useUrl: false,
				key: "value",
				value: "text",
				autoDropDownHeight: true,
				selectedIndex: 0,
				width: 250
			}
			new OlbDropDownList($("#txtTypePrint"), selectPrintData, configSelectPrint, []);
		};
		var handleEvents = function() {
			$("#alterSave").on("click", function() {
				if (validator()) {
					if (jqxwindow.data("action") == "UPDATE") {
						ConfigPrintOrder.updateRow(getValue());
					} else {
						ConfigPrintOrder.addRow(getValue());
					}
					jqxwindow.jqxWindow("close");
				}
			});
			$("#alterPrint").on("click", function() {
				if (validator()) {
					ConfigPrintOrder.print();
				}
			});
			
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				jqxwindow.data("action", "");
				extendsValue = new Object();
				$("#logo").attr("src", "");
				clean();
			});
			
			$("#txtLogo").change(function() {
				PreviewPrintOrder.readURL(this, $("#logo"));
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules :
				[
					{ input: "#txtFontFamily", message: multiLang.DmsFieldRequired, action: "change",
						rule: function (input, commit) {
							var value = $(input).jqxDropDownList("val");
							return !(/^\s*$/.test(value));
						}
					},
					{ input: "#txtTypePrint", message: multiLang.DmsFieldRequired, action: "change",
						rule: function (input, commit) {
							var value = $(input).jqxDropDownList("val");
							return !(/^\s*$/.test(value));
						}
					},
					{ input: "#txtHeaderFontSize", message: multiLang.BPOSValidateGreaterThanZero, action: "valueChanged",
						rule: function (input, commit) {
							var value = $(input).jqxNumberInput("val");
							if (value <= 0 && jqxwindow.jqxWindow("isOpen")) {
								return false;
							}
							return true;
						}
					},
					{ input: "#txtInfoFontSize", message: multiLang.BPOSValidateGreaterThanZero, action: "valueChanged",
						rule: function (input, commit) {
							var value = $(input).jqxNumberInput("val");
							if (value <= 0 && jqxwindow.jqxWindow("isOpen")) {
								return false;
							}
							return true;
						}
					},
					{ input: "#txtContentFontSize", message: multiLang.BPOSValidateGreaterThanZero, action: "valueChanged",
						rule: function (input, commit) {
							var value = $(input).jqxNumberInput("val");
							if (value <= 0 && jqxwindow.jqxWindow("isOpen")) {
								return false;
							}
							return true;
						}
					}
				],
				position : "topcenter"
			});
		};
		var validator = function() {
			return jqxwindow.jqxValidator("validate");
		};
		var getValue = function() {
			var value = {};
			value = {
				productStoreId : $("#txtProductStore").jqxDropDownList("val"),
				fontFamily : $("#txtFontFamily").jqxDropDownList("val"),
				headerFontSize : $("#txtHeaderFontSize").jqxNumberInput("val"),
				infoFontSize : $("#txtInfoFontSize").jqxNumberInput("val"),
				contentFontSize : $("#txtContentFontSize").jqxNumberInput("val"),
				isPrintBeforePayment : $("#txtTypePrint").jqxDropDownList("val")
			};
			if ($("#txtLogo").prop("files")[0]) {
				var url = DataAccess.uploadFile($("#txtLogo").prop("files")[0]);
				if (url) {
					value.logo = url;
				}
			}
			return _.extend(value, extendsValue);
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtProductStore").jqxDropDownList("val", data.productStoreId);
				$("#txtFontFamily").jqxDropDownList("val", data.fontFamily);
				$("#txtHeaderFontSize").jqxNumberInput("val", data.headerFontSize);
				$("#txtInfoFontSize").jqxNumberInput("val", data.infoFontSize);
				$("#txtContentFontSize").jqxNumberInput("val", data.contentFontSize);
				$("#txtTypePrint").jqxDropDownList("val", data.isPrintBeforePayment);
				extendsValue.uid = data.uid;
				jqxwindow.data("action", "UPDATE");
			}
		};
		var clean = function() {
			$("#txtHeaderFontSize").jqxNumberInput("clear");
			$("#txtInfoFontSize").jqxNumberInput("clear");
			$("#txtContentFontSize").jqxNumberInput("clear");
			$("#txtProductStore").jqxDropDownList("clearSelection");
			$("#txtFontFamily").jqxDropDownList("clearSelection");
			$("txtLogo").parent().find("a.remove").trigger("click");
		};
		return {
			init: function() {
				jqxwindow = $("#alterpopupWindow");
				initJqxElements();
				handleEvents();
				initValidator();
				PreviewPrintOrder.handler();
			},
			getValue: getValue,
			setValue: setValue
		};
	})();
}
if (typeof (PreviewPrintOrder) == "undefined") {
	var PreviewPrintOrder = (function() {
		var handler = function() {
			$("#txtProductStore").on("change", function (event) {
				var args = event.args;
				if (args) {
					$("#storeName").html(args.item.label);
				}
			});
			$("#txtFontFamily").on("change", function (event) {
				var args = event.args;
				if (args) {
					$("#PrintOrder").css("font-family", args.item.value);
				}
			});

			$("#txtHeaderFontSize").on("valueChanged", function (event) {
				$(".headerFontSize").css("font-size", event.args.value + "px");
			}); 
			$("#txtInfoFontSize").on("valueChanged", function (event) {
				$(".infoFontSize").css("font-size", event.args.value + "px");
			});
			$("#txtContentFontSize").on("valueChanged", function (event) {
				$(".contentFontSize").css("font-size", event.args.value + "px");
			});
		};
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function (e) {
					img.attr("src", e.target.result);
				}
				reader.readAsDataURL(input.files[0]);
			}
		};
		return {
			handler: handler,
			readURL: readURL
		}
	})();
}