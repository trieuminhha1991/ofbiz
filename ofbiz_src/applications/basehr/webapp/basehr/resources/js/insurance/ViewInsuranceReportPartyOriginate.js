var insReportPartyOrigiObj = (function(){
	var refreshData = function(){
		$("#jqxgrid").jqxGrid('showloadelement');
		$("#jqxgrid").jqxGrid({disabled: true});
		$.ajax({
			url: 'updateInsurancePartyOriginateOfReport',
			data: {reportId: globalVar.reportId},
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
	};
	return{
		refreshData: refreshData
	}
}());
$(document).ready(function () {
	
});