$(function(){
	OlbLoyaltyList.init();
});
var OlbLoyaltyList = (function(){
	var init = function(){
		initQuickMenu();
	};
	var initQuickMenu = function(){
		jOlbUtil.contextMenu.create($("#contextMenu_" + contextMenuItemId));
		$("#contextMenu_" + contextMenuItemId).on('itemclick', function (event) {
			var args = event.args;
			// var tmpKey = $.trim($(args).text());
			var tmpId = $(args).attr('id');
			var idGrid = "#jqxLoyalty";
			
	        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
	        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
	        
	        switch(tmpId) {
		        case contextMenuItemId + "_viewdetailnewtab": {
	    			if (rowData) {
	    				var loyaltyId = rowData.loyaltyId;
						var url = 'viewLoyalty?loyaltyId=' + loyaltyId;
						var win = window.open(url, '_blank');
						win.focus();
					}
					break;
				};
	    		case contextMenuItemId + "_viewdetail": { 
	    			if (rowData) {
	    				var loyaltyId = rowData.loyaltyId;
						var url = 'viewLoyalty?loyaltyId=' + loyaltyId;
						var win = window.open(url, '_self');
						win.focus();
					}
					break;
				};
	    		case contextMenuItemId + "_refesh": { 
	    			$(idGrid).jqxGrid('updatebounddata');
	    			break;
	    		};
	    		case contextMenuItemId + "_createnew": {
	    			var win = window.open("newLoyalty", "_self");
		        	window.focus();
		        	break;
	    		};
	    		case contextMenuItemId + "_delete": {
	    			$("#deleterowbuttonjqxLoyalty").click();
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