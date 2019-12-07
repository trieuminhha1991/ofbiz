var viewGlAccountTypeListObj = (function(){
	var init = function(){
		initContextMenu();
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				editGlAccountTypeObj.openWindow(data);
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	viewGlAccountTypeListObj.init();
});