var viewEmplLeaveReasonTypeObject = (function(){
	var init = function(){
        if(OlbCore.isNotEmpty(globalVar.editable) && globalVar.editable === "true") {
            initContextMenu();
        }
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 120)
		$('#contextMenu').on('itemclick', function(event){
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			executeEdit(boundIndex);
		});
	};
	var executeEdit = function(boundIndex){
		var rowData = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
		var rowid = $("#jqxgrid").jqxGrid('getrowid', boundIndex);
		rowData.rowid = rowid;
		editEmpleaveReasonTypeObject.openEditWindow();
		editEmpleaveReasonTypeObject.setData(rowData);
	};
	return{
		init: init,
		executeEdit: executeEdit
	}
}());
$(document).ready(function () {
	viewEmplLeaveReasonTypeObject.init();
});
