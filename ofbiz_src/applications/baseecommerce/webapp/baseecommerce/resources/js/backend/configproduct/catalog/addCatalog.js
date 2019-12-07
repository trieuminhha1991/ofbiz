var listProdCatalog= new Array();
if (typeof (AddCatalog) == "undefined") {
	var AddCatalog = (function() {
		var initJqxElements = function() {
			$("#alterpopupWindow").jqxWindow({
			    width: 500, maxWidth: 1000, theme: "olbius", minHeight: 200, resizable: false, isModal: true, autoOpen: false,
			    cancelButton: $("#alterCancel"), modalOpacity: 0.7
			});
		};
		var handleEvents = function() {
			$('#alterpopupWindow').on('open', function () {
				$("#txtProdCatalogId").val("");
				$("#txtProdCatalogName").val("");
			});
			$('#alterpopupWindow').on('close', function () {
				$('#alterpopupWindow').jqxValidator('hide');
			});
			$("#alterSave").click(function () {
				if ($('#alterpopupWindow').jqxValidator('validate')) {
					var row = {};
				row.prodCatalogId = $("#txtProdCatalogId").val();
				row.catalogName = $("#txtProdCatalogName").val();
				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			        $("#alterpopupWindow").jqxWindow('close');
				}
			});
		};
		var initValidator = function() {
			$('#alterpopupWindow').jqxValidator({
			    rules: [
							{ input: '#txtProdCatalogId', message: multiLang.fieldRequired, action: 'keyup, blur', rule: 'required' },
							{ input: '#txtProdCatalogId', message: multiLang.ContainSpecialSymbol, action: 'keyup, blur',
								rule: function (input, commit) {
									var value = input.val();
									if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
										return true;
									}
									return false;
								}
							},
							{ input: '#txtProdCatalogId', message: multiLang.CatalogIdAlreadyExists, action: 'keyup, blur',
								rule: function (input, commit) {
									var value = input.val().toLowerCase();
									if (_.indexOf(listProdCatalog, value) === -1) {
										return true;
									}
									return false;
								}
							},
							{ input: '#txtProdCatalogName', message: multiLang.fieldRequired, action: 'keyup, blur', rule: 'required' } ]
			});
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}
function hasWhiteSpace(s) {
	return /\s/g.test(s);
}