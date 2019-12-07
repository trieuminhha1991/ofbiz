if (typeof (AddCatagory) == "undefined") {
	var AddCatagory = (function() {
		var initJqxElements = function() {
			$("#alterpopupWindow").jqxWindow({
			    width: 650, maxWidth: 1000, theme: "olbius", minHeight: 450, resizable: false, isModal: true, autoOpen: false,
			    cancelButton: $("#alterCancel"), modalOpacity: 0.7
			});
			$("#txtPrimaryParentCategoryId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: [],
				displayMember: "categoryName", valueMember: "productCategoryId", placeHolder: multiLang.filterchoosestring });
			$("#txtRootCategory").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: rootCategories,
				displayMember: "categoryName", valueMember: "productCategoryId", placeHolder: multiLang.filterchoosestring });
			if (rootCategories.length < 5) {
				$("#txtRootCategory").jqxDropDownList({ autoDropDownHeight: true });
			}
		};
		var handleEvents = function() {
			$('#alterpopupWindow').on('open', function () {
				$("#txtProductCategoryId").val("");
				$("#txtCategoryName").val("");
				$("#tarDescription").val("");
				$("#txtSequenceNumber").val("");
				$("#txtRootCategory").jqxDropDownList('clearSelection');
				$("#txtPrimaryParentCategoryId").jqxDropDownList('clearSelection');
			});
			$('#alterpopupWindow').on('close', function () {
				$('#alterpopupWindow').jqxValidator('hide');
			});

			$('#txtRootCategory').on('change', function (event) {
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    var data = DataAccess.getData({
						url: "getMainCategoriesByPrimaryParentCategoryId",
						data: {productCategoryId: value},
						source: "categories"});
					$("#txtPrimaryParentCategoryId").jqxDropDownList({ source: data });
				    if (data.length < 5) {
						$("#txtPrimaryParentCategoryId").jqxDropDownList({ autoDropDownHeight: true });
					} else {
						$("#txtPrimaryParentCategoryId").jqxDropDownList({ autoDropDownHeight: false });
					}
			    }
			});
			$("#alterSave").click(function () {
				if ($("#alterpopupWindow").jqxValidator("validate")) {
					var row = {};
					row.productCategoryId = $("#txtProductCategoryId").val();
					row.productCategoryTypeId = "CATALOG_CATEGORY";
					row.categoryName = $("#txtCategoryName").val();
					row.sequenceNum = $("#txtSequenceNumber").val();
					if ($("#txtPrimaryParentCategoryId").jqxDropDownList('val')) {
						row.primaryParentCategoryId = $("#txtPrimaryParentCategoryId").jqxDropDownList('val');
						} else {
							row.primaryParentCategoryId = $("#txtRootCategory").jqxDropDownList('val');
						}
					row.longDescription = $("#tarDescription").val();
					DataAccess.execute({
							url: urlCreate,
							data: row},
							Categories.notify);
							setTimeout(function() {
								location.reload();
								}, 500);
					$("#alterpopupWindow").jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			$("#alterpopupWindow").jqxValidator({
			    rules: [{ input: "#txtProductCategoryId", message: multiLang.DmsFieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtProductCategoryId", message: multiLang.ContainSpecialSymbol, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtProductCategoryId", message: multiLang.CategoryIdAlreadyExists, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									var check = DataAccess.getData({
										url: "checkProductCategoryId",
										data: {productCategoryId: value},
										source: "check"});
									if ("false" == check) {
										 return false;
									}
								}
								return true;
							}
						},
						{ input: "#txtCategoryName", message: multiLang.DmsFieldRequired, action: "keyup, blur", rule: "required" },
						{ input: '#txtRootCategory', message: multiLang.fieldRequired, action: 'change',
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						}]
			});
		};
		var open = function() {
			var wtmp = window;
			var tmpwidth = $('#alterpopupWindow').jqxWindow('width');
	        $("#alterpopupWindow").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#alterpopupWindow").jqxWindow('open');
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			open: open
		};
	})();
}