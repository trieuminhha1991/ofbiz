var viewTimekeepingDetailPartyObj = (function(){
	var init = function(){
		initEvent();
		initContextMenu();
	};
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function (event) {
			var cellSelected = $("#jqxgrid").jqxGrid('getselectedcell');
			var args = event.args;
			var id = $(args).attr("id");
			if(id == "viewDetailDatekeeping"){
				var rowIndex = cellSelected.rowindex;
				var datafield = cellSelected.datafield;
				var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowIndex);
				var data = rowData;
				data.dateTimekeeping = datafield;
				partyDateTimekeepingDetailObj.openWindow();
				partyDateTimekeepingDetailObj.setData(data);//partyDateTimekeepingDetailObj is defined in ViewTimekeepingDetailPartyDatekeeping.js
			}else if(id == "reloadExcelData"){
				timesheetDetailWizard.openWindow(); //timesheetDetailWizard is defined in ViewTimekeepingDetailPartyReloadData.js
			}else if(id == "updateDataFromRelatedModule"){
				updateDataRelatedObj.openWindow();//updateDataRelatedObj is defined in TimekeepingUpdateDataRelated.js
			}
		});
		$("#contextMenu").on('shown', function (event) {
			var cellSelected = $("#jqxgrid").jqxGrid('getselectedcell');
			if(cellSelected){
				var datafield = cellSelected.datafield;
				if(datafield == "firstName" || datafield == "emplPositionTypeDes" || datafield == "groupName" 
					|| datafield == "totalWorkdayPaid" || datafield == "workdayActual" || datafield == "partyCode"){
					$(this).jqxMenu('disable', "viewDetailDatekeeping", true);
				}else{
					$(this).jqxMenu('disable', "viewDetailDatekeeping", false);
				}
			}
		});
		$("#jqxgrid").on("cellclick", function (event) {
			var args = event.args;
			if(args.rightclick) {
				var rowBoundIndex = args.rowindex;
			    var dataField = args.datafield;
			    $('#jqxgrid').jqxGrid('selectcell', rowBoundIndex, dataField);
			}
		});
		
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 300);
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	viewTimekeepingDetailPartyObj.init();
});