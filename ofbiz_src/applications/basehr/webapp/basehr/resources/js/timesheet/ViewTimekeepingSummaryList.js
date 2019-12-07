var viewTimekeepingSummaryObj = (function(){
	var init = function(){
		initContextMenu();
		initEvent();
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 200);
	};
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function (event) {
			var action = $(args).attr("action");
			var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowIndex);
			var timekeepingSummaryId = rowData.timekeepingSummaryId;
			if(action == "refreshData"){
				updateTimekeepingSummaryParty(timekeepingSummaryId);
			}
		});
	};
	var updateTimekeepingSummaryParty = function(timekeepingSummaryId){
		$("#jqxgrid").jqxGrid({disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'updateTimekeepingSummaryPartyFromTimekeepingDetail',
			data: {timekeepingSummaryId: timekeepingSummaryId},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					Grid.renderMessage('jqxgrid', response._ERROR_MESSAGE_, {autoClose: true,
						template : 'error', appendContainer: "#containerjqxgrid", opacity : 0.9});
				}
			},
			complete: function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
			}
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewTimekeepingSummaryObj.init();
});