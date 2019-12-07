$(function(){
	OlbSettingProductStoreCatalog.init();
});

var OlbSettingProductStoreCatalog = (function(){
	var initMenu = (function(){
		$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	});
	
	var eventMenu = (function(){
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
		eventMenu();
	});
	
	return{
		init: init,
	}
}());