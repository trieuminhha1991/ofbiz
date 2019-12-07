$(function() {
	OlbQuotationList.init();
});
var OlbQuotationList = (function() {
	var init = function() {
		initQuickMenu();
	};
	var initQuickMenu = function() {
		jOlbUtil.contextMenu.create($("#contextMenu_" + contextMenuItemId));
		$("#contextMenu_" + contextMenuItemId).on(
				"itemclick",
				function(event) {
					var args = event.args;
					var tmpId = $(args).attr("id");
					var idGrid = "#jqxPromotion";

					var rowindex = $(idGrid).jqxGrid("getselectedrowindex");
					var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);

					switch (tmpId) {
					case contextMenuItemId + "_viewdetailnewtab": {
						if (rowData) {
							var productPromoId = rowData.productPromoId;
							var url = "viewPromotionPO?productPromoId="
									+ productPromoId;
							var win = window.open(url, "_blank");
							win.focus();
						}
						break;
					}
						;
					case contextMenuItemId + "_viewdetail": {
						if (rowData) {
							var productPromoId = rowData.productPromoId;
							var url = "viewPromotionPO?productPromoId="
									+ productPromoId;
							var win = window.open(url, "_self");
							win.focus();
						}
						break;
					}
						;
					case contextMenuItemId + "_refesh": {
						$(idGrid).jqxGrid("updatebounddata");
						break;
					}
						;
					case contextMenuItemId + "_delete": {
						$("#deleterowbuttonjqxPromotion").click();
					}
						;
					default:
						break;
					}
				});
	};
	return {
		init : init
	};
}());