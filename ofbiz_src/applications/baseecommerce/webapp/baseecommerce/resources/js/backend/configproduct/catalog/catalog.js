$(document).ready(function() {
	Catalog.init();
});

if (typeof (Catalog) == "undefined") {
	var Catalog = (function() {
		var initJqxElements = function() {
			$("#contextMenu").jqxMenu({ theme: 'olbius', width: 220, autoOpenPopup: false, mode: 'popup'});
		};
		var handleEvents = function() {
			$("#jqxgrid").on('contextmenu', function () {
			    return false;
			});
			$("#contextMenu").on('itemclick', function (event) {
		        var args = event.args;
		        var itemId = $(args).attr('id');
		        switch (itemId) {
				case "viewListCategory":
					rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
				var prodCatalogId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "prodCatalogId" );
				$(".lblProdCatalogId").text(prodCatalogId);
				Categories.open(prodCatalogId);
					break;
				case "viewProductStoreList":
					rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
				var prodCatalogId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "prodCatalogId" );
				$(".lblProdCatalogId").text(prodCatalogId);
				Stores.open(prodCatalogId);
					break;
				default:
					break;
				}
		    });
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				AddCatalog.init();
				Categories.init();
				Stores.init();
			}
		};
	})();
}