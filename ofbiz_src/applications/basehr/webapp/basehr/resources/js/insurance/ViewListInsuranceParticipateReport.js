var listInsParticipateReportObj = (function(){
	var init = function(){
		initContextMenu();
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 160);
		$("#contextMenu").on('itemclick', function (event) {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			var args = event.args;
			var action = $(args).attr("action");
			if(action == 'updateData'){
				$("#jqxgrid").jqxGrid('showloadelement');
				$("#jqxgrid").jqxGrid({disabled: true});
				$.ajax({
					url: 'updateInsurancePartyOriginateOfReport',
					data: {reportId: dataRecord.reportId},
					type: 'POST',
					success: function(response){
						if(response._EVENT_MESSAGE_){
							Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose : true,
								template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
							$("#jqxgrid").jqxGrid('updatebounddata');
						}else{
							bootbox.dialog(response._ERROR_MESSAGE_,
									[
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);
						}	
					},
					complete: function(jqXHR, textStatus){
						$("#jqxgrid").jqxGrid('hideloadelement');
						$("#jqxgrid").jqxGrid({disabled: false});
					}
				});
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	listInsParticipateReportObj.init();
});