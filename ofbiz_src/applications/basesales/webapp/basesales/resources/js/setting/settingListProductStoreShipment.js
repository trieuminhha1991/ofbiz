$(function(){
	OlbSettingProductStoreShipment.init();
});

var OlbSettingProductStoreShipment = (function(){
	var initMenu = (function(){
		$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	});
	
	var eventClickMenu = (function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action"); 
	        if (action == "refresh") {
	        	$("#jqxgrid").jqxGrid('updatebounddata');
	        }
		});
	});
	
	var init = (function(){
		initMenu();
		eventClickMenu();
	});
	
	return{
		init: init,
	}
}());
