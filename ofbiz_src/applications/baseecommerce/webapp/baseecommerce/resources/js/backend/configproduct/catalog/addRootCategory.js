if (typeof (RootCatagory) == "undefined") {
	var RootCatagory = (function() {
		var initJqxElements = function() {
			$("#alterpopupAddRootCategory").jqxWindow({
			    width: 650, maxWidth: 1000, theme: "olbius", minHeight: 380, resizable: false, isModal: true, autoOpen: false,
			    cancelButton: $("#cancelAddRootCategory"), modalOpacity: 0.7
			});
		};
		var handleEvents = function() {
			$('#alterpopupAddRootCategory').on('open', function () {
				$("#txtProductCategoryId").val("");
				$("#txtCategoryName").val("");
				$("#tarDescription").val("");
			});
			$('#alterpopupAddRootCategory').on('close', function () {
				$('#alterpopupAddRootCategory').jqxValidator('hide');
			});

			$("#saveRootCategory").click(function () {
				if ($("#alterpopupAddRootCategory").jqxValidator("validate")) {
					var row = {};
				row.productCategoryId = $("#txtProductCategoryId").val();
				row.productCategoryTypeId = "CATALOG_CATEGORY";
				row.categoryName = $("#txtCategoryName").val();
				row.longDescription = $("#tarDescription").val();
				DataAccess.execute({
				url: "addRootCategory",
				data: row},
				AddCategory.notify);
				$("#alterpopupAddRootCategory").jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			$("#alterpopupAddRootCategory").jqxValidator({
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
						{ input: "#txtProductCategoryId", message: multiLang.CategoryIdAlreadyExists, action: "keyup, blur",
							rule: function (input, commit) {
								var value = input.val().toLowerCase();
								if (_.indexOf(listCategoryIds, value) === -1) {
									return true;
								}
								return false;
							}
						},
						{ input: "#txtCategoryName", message: multiLang.DmsFieldRequired, action: "keyup, blur", rule: "required" }]
			});
		};
		var open = function() {
			var wtmp = window;
		var tmpwidth = $('#alterpopupAddRootCategory').jqxWindow('width');
	        $("#alterpopupAddRootCategory").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#alterpopupAddRootCategory").jqxWindow('open');
		};
		var fixme = function() {
			return DataAccess.execute({
		url: "fixRootCategory",
		data: {}});
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			open: open,
			fixme: fixme
		};
	})();
}