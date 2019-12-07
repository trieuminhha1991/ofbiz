var createInvoiceEmplSalaryItemObject = (function(){
	var init = function(){
		initBtnEvent();
	};
	
	var initBtnEvent = function(){
		$("#createInvoice").click(function(event){
			var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
			if(rowindexes.length == 0){
				bootbox.dialog(uiLabelMap.NoEmployeeChoose_createInvoice,
						 [
						 {
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn btn-danger icon-remove form-action-button",
			    		    "callback": function() {
			    		    	
			    		    }
			    		 }
			    		 ]
					 );
				return;
			}
			bootbox.dialog(uiLabelMap.CreateInvoiceForPartyConfirm,
					 [
					 {
		    		    "label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn btn-primary icon-ok form-action-button",
		    		    "callback": function() {
		    		    	createInvoiceForListParty(rowindexes);
		    		    }
		    		 },
		    		 {
		    		    "label" : uiLabelMap.CommonCancel,
		    		    "class" : "btn-danger icon-remove btn form-action-button",
		    		 }
		    		 ]
				 );
		});
	};
	
	var createInvoiceForListParty = function(rowindexes){
		var dataSubmit = {};
		dataSubmit.customTimePeriodId = $("#monthCustomTime").val();
		var partyIdList = new Array()
		for(var i = 0; i < rowindexes.length; i++){
			var tempData = $("#jqxgrid").jqxGrid('getrowdata', rowindexes[i]);
			partyIdList.push(tempData.partyIdTo); 
		}
		dataSubmit.partyIdList = JSON.stringify(partyIdList);
		$("#jqxgrid").jqxGrid({disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'createInvoiceItemSalary',
			type: 'POST',
			data: dataSubmit,
			success: function(response){
				$("#jqxNotify").jqxNotification('closeLast');
				$('#jqxcontainer').empty();
				if(response.responseMessage == 'success'){
					$("#jqxNotify").jqxNotification({template: 'info'});
					$("#jqxNotifyContent").text(response.successMessage);
					$('#jqxgrid').jqxGrid('clearselection');
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#jqxNotify").jqxNotification({template: 'error'})
					$("#jqxNotifyContent").text(response.errorMessage);
				}
				$("#jqxNotify").jqxNotification('open');
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
	createInvoiceEmplSalaryItemObject.init();
});

