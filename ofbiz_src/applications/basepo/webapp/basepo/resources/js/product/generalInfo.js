if (typeof (GeneralInfo) == "undefined") {
	var GeneralInfo = (function() {
		var initJqxElements = function() {
			$("#description1").jqxEditor({
			    theme: "olbiuseditor",
			    width:"96%"
			});
			var source0 = { datatype: "json",
					datafields: [{ name: "partyId" },
					             { name: "groupName" }],
					             url: "getListProductBrands"};
			var dataAdapter0 = new $.jqx.dataAdapter(source0);
			$("#txtBrandName").jqxDropDownList({ theme: "olbius", source: dataAdapter0, width: 218, height: 30, displayMember: "groupName", valueMember: "partyId", placeHolder: multiLang.filterchoosestring});
			
			$("#txtTaxCatalogs").jqxDropDownList({ theme: "olbius", source: listTaxCategory, width: 218, height: 30, displayMember: "categoryName", valueMember: "productCategoryId", placeHolder: multiLang.filterchoosestring});
			$("#txtCatalog").jqxDropDownList({ theme: "olbius", source: prodCatalogs, width: 218, height: 30, displayMember: "catalogName", valueMember: "prodCatalogId", autoDropDownHeight: true, placeHolder: multiLang.filterchoosestring});
			
			var source = { datatype: "json",
					datafields: [{ name: "productCategoryId" },
					             { name: "categoryName" }],
					             url: "getProductCategories"};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#txtPrimaryProductCategoryId").jqxDropDownList({ theme: "olbius", source: dataAdapter, width: 218, height: 30, displayMember: "categoryName", valueMember: "productCategoryId", placeHolder: multiLang.filterchoosestring});
			
			var source2 = { datatype: "json",
					datafields: [{ name: "productCategoryId" },
					             { name: "categoryName" }],
					             url: "getProductCategories"};
			var dataAdapter2 = new $.jqx.dataAdapter(source2);
			$("#txtProductCategoryId").jqxComboBox({ theme: "olbius", source: dataAdapter2, width: 218, height: 30, displayMember: "categoryName", valueMember: "productCategoryId", multiSelect: true, dropDownHeight: 200});
			
			setTimeout(function() {
				$("#txtProductCategoryId").jqxComboBox("refresh");
			}, 1500);
			
			$("#alterpopupWindow").jqxWindow({
			    width: 650, maxWidth: 1000, theme: "olbius", minHeight: 420, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
			});
		};
		var initValidator = function() {
			$("#step2").jqxValidator({
			    rules: [{ input: "#txtProductId", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtProductId", message: multiLang.containSpecialSymbol, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductId", message: multiLang.ProductIdAlreadyExists, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									var check = DataAccess.getData({
										url: "checkProductCode",
										data: {productCode: value, productId: productIdParameters},
										source: "check"});
									if ("false" == check) {
										 return false;
									}
								}
								return true;
							}
						},
						{ input: "#txtInternalName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtProductName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtInternalName", message: multiLang.containSpecialSymbol, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductName", message: multiLang.containSpecialSymbol, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtTaxCatalogs", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								var value = input.jqxDropDownList("val");
								if (value) {
									return true;
								}
								return false;
							}
						}],
			           position: "bottom"
			});
			$("#alterpopupWindow").jqxValidator({
			    rules: [{ input: "#txtProductCategoryIdAdd", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtProductCategoryIdAdd", message: multiLang.containSpecialSymbol, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductCategoryIdAdd", message: multiLang.CategoryIdAlreadyExists, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val().toLowerCase();
								if (_.indexOf(listProductCategorys, value) === -1) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtCategoryNameAdd", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" }]
			});
		};
		var handleEvents = function() {
			$("#addCatagory").on("click", function() {
				$("#alterpopupWindow").jqxWindow("open");
			});
			
			$("#alterpopupWindow").on("open", function () {
				$("#tarDescriptionAdd").jqxEditor({
			        theme: "olbiuseditor",
			        width: "98%",
			        height: 240
			    });
				$("#tarDescriptionAdd").jqxEditor("val", "");
				$("#txtProductCategoryIdAdd").val("");
				$("#txtCategoryNameAdd").val("");
			});
			$("#alterpopupWindow").on("close", function () {
				$("#alterpopupWindow").jqxValidator("hide");
			});
			$("#alterSave").click(function () {
				if ($("#alterpopupWindow").jqxValidator("validate")) {
					var row = {};
			    	row.productCategoryId = $("#txtProductCategoryIdAdd").val();
			    	row.productCategoryTypeId = "CATALOG_CATEGORY";
			    	row.categoryName = $("#txtCategoryNameAdd").val();
			    	row.longDescription = $("#tarDescriptionAdd").jqxEditor("val");
			    	DataAccess.execute({
					    	url: "createProductCategoryAjax",
					    	data: row
						},
						GeneralInfo.reloadProductCategory);
			        $("#alterpopupWindow").jqxWindow("close");
				}
			});
			
			$("#txtBrandName").on("bindingComplete", function (event) {
				if (databrandName) {
					$("#txtBrandName").jqxDropDownList("val", databrandName);
					databrandName = null;
				}
			});
			$("#txtPrimaryProductCategoryId").on("bindingComplete", function (event) {
				if (dataPrimaryProductCategoryId) {
					$("#txtPrimaryProductCategoryId").jqxDropDownList("val", dataPrimaryProductCategoryId);
					dataPrimaryProductCategoryId = null;
				}
			});
			$("#txtProductCategoryId").on("bindingComplete", function (event) {
				if (dataProductCategories) {
					for ( var x in dataProductCategories) {
						$("#txtProductCategoryId").jqxComboBox("selectItem", dataProductCategories[x]);
					}
					dataProductCategories = null;
				}
			});
			
			$("#txtCatalog").on("change", function (event) {     
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    GeneralInfo.reloadProductCategory({prodCatalogId: value});
				}
			});
		};
		var reloadProductCategory = function(res) {
			var url = "getProductCategories";
			if (res.prodCatalogId) {
				url += "?prodCatalogId=" + res.prodCatalogId;
			}
			var source = { datatype: "json",
					datafields: [{ name: "productCategoryId" },
					             { name: "categoryName" }],
					             url: url};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#txtPrimaryProductCategoryId").jqxDropDownList({ source: dataAdapter });
			
			var source2 = { datatype: "json",
					datafields: [{ name: "productCategoryId" },
					             { name: "categoryName" }],
					             url: url};
			var dataAdapter2 = new $.jqx.dataAdapter(source2);
			$("#txtProductCategoryId").jqxComboBox({ source: dataAdapter2 });
		};
		var dataProductCategories, dataPrimaryProductCategoryId, databrandName;
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				if (data.productCode) {
					$("#txtProductId").val(data.productCode);
				}
				$("#txtTaxCatalogs").jqxDropDownList("val", data.productCategoryTaxId);
				$("#txtInternalName").val(data.internalName);
				$("#txtProductName").val(data.productName);
				$("#description1").jqxEditor("val", data.longDescription);
				dataProductCategories = data.productCategories;
				dataPrimaryProductCategoryId = data.primaryProductCategoryId;
				databrandName = data.brandName;
				
				$("#txtBrandName").jqxDropDownList("val", databrandName);
				if (dataPrimaryProductCategoryId) {
					$("#txtPrimaryProductCategoryId").jqxDropDownList("val", dataPrimaryProductCategoryId);
				}
				for ( var x in dataProductCategories) {
					$("#txtProductCategoryId").jqxComboBox("selectItem", dataProductCategories[x]);
				}
			}
		};
		var getValue = function() {
			var value = new Object();
			value.productCode = $("input[name='txtProductId']").val();
			value.primaryProductCategoryId = $("#txtPrimaryProductCategoryId").jqxDropDownList("val");
			value.taxCatalogs = $("#txtTaxCatalogs").jqxDropDownList("val");
			value.internalName = $("input[name='txtInternalName']").val();
			value.brandName = $("#txtBrandName").jqxDropDownList("val")
			value.productName = $("input[name='txtProductName']").val();
			value.longDescription = $("#description1").jqxEditor("val");
			value.productCategoryId = LocalUtil.getValueSelectedJqxComboBox($("#txtProductCategoryId"));
			return value;
		};
		var validate = function() {
			return $("#step2").jqxValidator("validate");
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			reloadProductCategory: reloadProductCategory,
			validate: validate,
			getValue: getValue,
			setValue: setValue
		}
	})();
}
function hasWhiteSpace(s) {
	return /\s/g.test(s);
}