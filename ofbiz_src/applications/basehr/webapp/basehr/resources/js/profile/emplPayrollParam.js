$(document).ready(function () {
	$("#jqxgrid").on('loadCustomControlAdvance', function(event){
		$("#dateTimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.monthStart);
		var thruDate = new Date(globalVar.monthEnd);
		var source = $("#jqxgrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListEmplPayrollParameter&partyId=" + globalVar.userLogin_partyId + "&hasrequest=Y&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
		$("#jqxgrid").jqxGrid('source', source);
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from;
		    var thruDate = selection.to;
		    refreshGridData(fromDate, thruDate);
		});
	});
});

function refreshGridData(fromDate, thruDate){
	var source = $("#jqxgrid").jqxGrid('source');
	source._source.url = "jqxGeneralServicer?sname=JQGetListEmplPayrollParameter&partyId=" + globalVar.userLogin_partyId + "&hasrequest=Y&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
	$("#jqxgrid").jqxGrid('source', source);
	
}