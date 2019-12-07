var distributorBonusParty = (function(){
	var updateData = function(){
		$("#jqxgrid").jqxGrid({disabled: true});
    	$("#jqxgrid").jqxGrid('showloadelement');
    	$.ajax({
    		url: 'updateSalesBonusSummaryData',
    		data: {salesBonusSummaryId: globalVar.salesBonusSummaryId},
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
		updateData: updateData
	}
}());