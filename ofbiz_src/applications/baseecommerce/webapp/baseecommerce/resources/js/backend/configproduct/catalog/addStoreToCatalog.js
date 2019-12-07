if (typeof (AddStore) == "undefined") {
	var AddStore = (function() {
		var initJqxElements = function() {
			$("#jqxwindowAddStoreToCatalog").jqxWindow({
				theme: 'olbius', width: 550, maxWidth: 1845, minHeight: 200, height: 200, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddStore"), modalOpacity: 0.7
			});

			$("#txtProductStore").jqxDropDownList({ theme: 'olbius', width: 218, height: 30, source: [], displayMember: 'storeName',
				valueMember: 'productStoreId',
				placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
		};
		var handleEvents = function() {
			$('#jqxwindowAddStoreToCatalog').on('open', function () {
				$("#txtProductStore").jqxDropDownList('clearSelection');
			});
			$('#jqxwindowAddStoreToCatalog').on('close', function () {
				$('#jqxwindowAddStoreToCatalog').jqxValidator('hide');
			});

			$("#saveAddStore").click(function () {
				if ($('#jqxwindowAddStoreToCatalog').jqxValidator('validate')) {
					var row = {};
				row.prodCatalogId = $(".lblProdCatalogId").first().text();
				row.productStoreId = $("#txtProductStore").jqxDropDownList('getSelectedItem').value;
				row.storeName = $("#txtProductStore").jqxDropDownList('getSelectedItem').label;
				row.defaultCurrencyUomId = mapProductStoreCurrency[row.productStoreId];
				$("#jqxgridStores").jqxGrid('addRow', null, row, "first");
				$("#jqxwindowAddStoreToCatalog").jqxWindow('close');
				}
			});
		};
		var initValidator = function() {
			$('#jqxwindowAddStoreToCatalog').jqxValidator({
			    rules: [{ input: '#txtProductStore', message: multiLang.fieldRequired, action: 'change',
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
			var rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var prodCatalogId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "prodCatalogId" );
			$("#txtProductStore").jqxDropDownList({source: DataAccess.getData({
																url: "getListProductStoreAvalibleInCatalog",
																data: {prodCatalogId: prodCatalogId},
																source: "listProductStoreAvalible"})});

			var wtmp = window;
		var tmpwidth = $('#jqxwindowAddStoreToCatalog').jqxWindow('width');
	        $("#jqxwindowAddStoreToCatalog").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		$("#jqxwindowAddStoreToCatalog").jqxWindow('open');
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