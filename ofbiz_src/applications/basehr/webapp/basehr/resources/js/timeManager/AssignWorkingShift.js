var assignWSObject = (function(){
	var init = function(){
		initBtnEvent();
	};
	var initBtnEvent = function(){
		$("#assignWorkingShiftParty").click(function(event){
			openJqxWindow($("#assignWorkingWindow"));
		});
		$("#alterSave").click(function(event){			
			var fromDate = $("#assignFromDate").jqxDateTimeInput('val', 'date').getTime();
		    var thruDate = $("#assignThruDate").jqxDateTimeInput('val', 'date').getTime();
		    var item = $('#jqxTree').jqxTree('getSelectedItem');
			var partyId = item.value;
			if($('#assignWorkingWindow').jqxValidator('validate')){
				$("#jqxgrid").jqxGrid('showloadelement');
			    $("#jqxgrid").jqxGrid({disabled: true});
			    $.ajax({
			    	url: 'assignWorkingShiftForParty',
			    	data: {fromDate: fromDate, thruDate: thruDate, partyId: partyId},
			    	type: 'POST',
			    	success: function(response){
			    		$("#jqxNotification").jqxNotification('closeLast');
						if(response.responseMessage == 'success'){
							$("#notificationContent").text(response.successMessage);
							$("#jqxNotification").jqxNotification({template: 'info'});
							$("#jqxNotification").jqxNotification("open");
							$("#jqxgrid").jqxGrid('updatebounddata');
						}else{
							$("#notificationContent").text(response.errorMessage);
							$("#jqxNotification").jqxNotification({template: 'error'});
							$("#jqxNotification").jqxNotification("open");
						}
			    	},
			    	complete: function(jqXHR, textStatus){
						$("#jqxgrid").jqxGrid({disabled: false});
						$("#jqxgrid").jqxGrid('hideloadelement');
			    	}
			    });
			    $("#assignWorkingWindow").jqxWindow('close');
			}else{
				return false;
			}
		});
		$("#alterCancel").click(function(){
			$("#assignWorkingWindow").jqxWindow('close');
		});
		$("#assignWorkingShiftForEmpl").click(function(event){
			assignWSGroupEmpl.openWindow();//assignWSGroupEmpl is defined in AssignWorkingShiftForEmplList.js
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	assignWSObject.init();
});