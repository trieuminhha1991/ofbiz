$(function(){
	OlbProductStoreListRole.init();
});

var OlbProductStoreListRole = (function(){
	var init = (function(){
		initElement();
		initEvent();
	});
	var initElement = function(){
		jOlbUtil.contextMenu.create($("#contextMenu_" + contextMenuItemIdRole));
	};
	var initEvent = function(){
		$("#contextMenu_" + contextMenuItemIdRole).on('itemclick', function (event) {
			var args = event.args;
			var tmpId = $(args).attr('id');
			var idGrid = "#jqxProdStoreRole";
			
	        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
	        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
	        
	        switch(tmpId) {
	    		case contextMenuItemIdRole + "_delete": { 
    				$("#deleterowbuttonjqxProdStoreRole").click();
					break;
				};
	    		case contextMenuItemIdRole + "_refesh": { 
	    			$(idGrid).jqxGrid('updatebounddata');
	    			break;
	    		};
	    		default: break;
	    	}
	    });
		
		$("#contextMenu_" + contextMenuItemIdRole).on('shown', function () {
			var rowIndexSelected = $("#jqxProdStoreRole").jqxGrid('getSelectedRowindex');
			var rowData = $("#jqxProdStoreRole").jqxGrid('getrowdata', rowIndexSelected);
			if (rowData != null && rowData.thruDate != null && rowData.thruDate != undefined) {
				var thruDate = new Date(rowData.thruDate).getTime();
				var nowDate = new Date().getTime();
				if (thruDate < nowDate) {
					$("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_delete', true);
				} else {
					$("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_delete', false);
				}
			} else {
				$("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_delete', false);
			}
		});
	};
	
	return {
		init: init,
	}
}());