$(function(){
	OlbPromoCodeList.init();
});
var OlbPromoCodeList = (function(){
	var init = function(){
		initQuickMenu();
	};
	var initQuickMenu = function(){
		jOlbUtil.contextMenu.create($("#contextMenu_" + contextMenuItemId));
		$("#contextMenu_" + contextMenuItemId).on('itemclick', function (event) {
			var args = event.args;
			// var tmpKey = $.trim($(args).text());
			var tmpId = $(args).attr('id');
			var idGrid = "#jqxPromotionCode";
			
	        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
	        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
	        
	        switch(tmpId) {
	    		case contextMenuItemId + "_viewdetail": { 
	    			if (rowData) {
	    				var productPromoCodeId = rowData.productPromoCodeId;
	    				if (productPromoCodeId) {
	    					OlbPromoCodeEdit.loadPromoCodeDetail(productPromoCodeId);
	    				}
					}
					break;
				};
	    		case contextMenuItemId + "_refesh": { 
	    			$(idGrid).jqxGrid('updatebounddata');
	    			break;
	    		};
	    		case contextMenuItemId + "_createnew": {
	    			$("#customcontroljqxPromotionCode1").click();
		        	break;
	    		};
	    		case contextMenuItemId + "_delete": {
	    			$("#deleterowbuttonjqxPromotionCode").click();
	    			break;
	    		};
	    		default: break;
	    	}
		});
	};
	return {
		init: init
	};
}());