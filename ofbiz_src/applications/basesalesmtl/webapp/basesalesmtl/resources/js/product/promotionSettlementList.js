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
					var promoSettlementId = data.promoSettlementId;
					var url = 'viewPromoSettle?promoSettlementId=' + promoSettlementId;
					var win = window.open(url, '_self');
					win.focus();
				}
	        } else if (tmpKey == uiLabelMap.BSViewDetailInNewTab) {
	        	var data = $("#jqxPromotion").jqxGrid("getrowdata", rowindex);
				if (data != undefined && data != null) {
					var promoSettlementId = data.promoSettlementId;
					var url = 'viewPromoSettle?promoSettlementId=' + promoSettlementId;
					var win = window.open(url, '_blank');
					win.focus();
				}
	        } else if (tmpKey == uiLabelMap.BSDeleteSelectedRow) {
	        	$("#deleterowbuttonjqxPromotion").click();
	        }
		});
	};
	return {
		init: init
	};
}());