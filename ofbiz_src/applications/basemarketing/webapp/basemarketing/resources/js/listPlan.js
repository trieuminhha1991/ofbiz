$(document).ready(function() {
	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 200, height: 30, autoOpenPopup: false, mode: 'popup'});
	contextMenu.on('itemclick', function (event) {
        var args = event.args;
        var itemId = $(args).attr('id');
        switch (itemId) {
		case "addPlanChild":
			var rowIndexSelected = $('#ListPlan').jqxGrid('getSelectedRowindex');
		var rowData = $('#ListPlan').jqxGrid('getrowdata', rowIndexSelected);
			var marketingPlanId = rowData.marketingPlanId;
			window.location.href = "EditMarketingPlan?parentPlanId=" + marketingPlanId;
			break;
		default:
			break;
		}
	});
	contextMenu.on('shown', function () {

	});
});