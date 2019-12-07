var viewEquipmentListObj = (function(){
	var init = function(){
		initContextMenu();
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 160);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				editEquipmentObj.openWindow(data);//editEquipmentObj is defined in equipmentEdit.js
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	viewEquipmentListObj.init();
});