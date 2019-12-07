$(function(){
	OlbQuotationList.init();
});
var OlbQuotationList = (function(){
	var init = function(){
		initQuickMenu();
	};
	var initQuickMenu = function(){
		jOlbUtil.contextMenu.create($("#contextMenu"));
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxPromotion").jqxGrid('getselectedrowindex');
	        var tmpKey = $.trim($(args).text());
	        if (tmpKey == uiLabelMap.BSRefresh) {
	        	$("#jqxPromotion").jqxGrid('updatebounddata');
	        } else if (tmpKey == uiLabelMap.BSViewDetail) {
	        	var data = $("#jqxPromotion").jqxGrid("getrowdata", rowindex);
				if (data != undefined && data != null) {
					var productPromoId = data.productPromoId;
					var url = 'viewPromotionExt?productPromoId=' + productPromoId;
					var win = window.open(url, '_self');
					win.focus();
				}
	        } else if (tmpKey == uiLabelMap.BSViewDetailInNewTab) {
	        	var data = $("#jqxPromotion").jqxGrid("getrowdata", rowindex);
				if (data != undefined && data != null) {
					var productPromoId = data.productPromoId;
					var url = 'viewPromotionExt?productPromoId=' + productPromoId;
					var win = window.open(url, '_blank');
					win.focus();
				}
	        } else if (tmpKey == uiLabelMap.BSDeleteSelectedRow) {
	        	$("#deleterowbuttonjqxPromotion").click();
	        } else if (tmpKey == uiLabelMap.BSCreateNew) {
	        	var win = window.open("newPromotionExt", "_self");
	        	window.focus();
	        }
		});
	};
	return {
		init: init
	};
}());