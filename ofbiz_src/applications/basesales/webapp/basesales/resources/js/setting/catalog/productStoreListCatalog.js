$(function(){
	OlbProductStoreListCatalog.init();
});

var OlbProductStoreListCatalog = (function(){
	var init = (function(){
		initElement();
		initEvent();
	});
	var initElement = function(){
		jOlbUtil.contextMenu.create($("#contextMenu_" + contextMenuItemId));
	};
	var initEvent = function(){
		$("#contextMenu_" + contextMenuItemId).on('itemclick', function (event) {
			var args = event.args;
			var tmpId = $(args).attr('id');
			var idGrid = "#jqxgrid";
			
	        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
	        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
	        
	        switch(tmpId) {
	    		case contextMenuItemId + "_delete": { 
    				$("#deleterowbuttonjqxgrid").click();
					break;
				};
	    		case contextMenuItemId + "_refesh": { 
	    			$(idGrid).jqxGrid('updatebounddata');
	    			break;
	    		};
	    		default: break;
	    	}
	    });
		
		$("#contextMenu_" + contextMenuItemId).on('shown', function () {
			var rowIndexSelected = $("#jqxgrid").jqxGrid('getSelectedRowindex');
			var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowIndexSelected);
			if (rowData != null && rowData.thruDate != null && rowData.thruDate != undefined) {
				var thruDate = new Date(rowData.thruDate).getTime();
				var nowDate = new Date().getTime();
				if (thruDate < nowDate) {
					$("#contextMenu_" + contextMenuItemId).jqxMenu('disable', contextMenuItemId + '_delete', true);
				} else {
					$("#contextMenu_" + contextMenuItemId).jqxMenu('disable', contextMenuItemId + '_delete', false);
				}
			} else {
				$("#contextMenu_" + contextMenuItemId).jqxMenu('disable', contextMenuItemId + '_delete', false);
			}
		});
	};
	
	return {
		init: init,
	}
}());