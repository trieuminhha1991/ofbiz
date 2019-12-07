var editEmplTimesheetObject = (function(){
	var init = function(){
		updateAction();
		saveAction();
		cancelAction();
		sendAction();
	};
	
	var saveAction = function(){
		$("#alterSave").click(function(){
	    	if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
	    	$(this).attr("disabled", "disabled");
	    	bootbox.dialog(uiLabelMap.CreateEmpTimesheetRemind,
	    			[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "btn-primary btn-small icon-ok open-sans",
					    "callback": function() {
					    	createEmplTimesheet();
					    	$("#alterSave").removeAttr("disabled");
					    }
					},
					{
					    "label" : uiLabelMap.CommonCancel,
					    "class" : "btn-danger icon-remove btn-small",
					    "callback": function() {
					    	$("#alterSave").removeAttr("disabled");
					    }
					}		
	    			]
	    	);
	    });
	};
	var cancelAction = function(){
		$("#cancelSendTimesheet").click(function(){
			$("#proposalApprvalTimesheet").jqxWindow('close');
		});
		
		$("#cancelUpdateBtn").click(function(){
			$("#jqxWindowEmplTimesheetInDay").jqxWindow('close');
		});
		$("#alterCancel").click(function(){
	    	$("#popupAddRow").jqxWindow('close');
		});
	};	
	
	var sendAction = function(){
		$("#sendTimesheet").click(function(){
			var emplTimesheetId = $("#emplTimesheetId").val();
			if(emplTimesheetId){
				$("#sendTimesheet").attr('disabled','disabled');	
				jqxWindowEmplTimesheetInDay.jqxWindow('close');
				$.ajax({
					url: "proposalApprovalTimesheets",
					data:{emplTimesheetId: emplTimesheetId},
					type: 'POST',
					success: function(data){
						if(data._EVENT_MESSAGE_){
							$("#jqxNotifyEmplTimesheets").text(data._EVENT_MESSAGE_);
							$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'info'})
							$("#jqxNotifyEmplTimesheets").jqxNotification("open");
						}else{
							$("#jqxNotifyEmplTimesheets").text(data._ERROR_MESSAGE_);
							$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'error'})
							$("#jqxNotifyEmplTimesheets").jqxNotification("open");
						}
					},
					complete: function(){
						$("#sendTimesheet").removeAttr("disabled");	
						$("#proposalApprvalTimesheet").jqxWindow('close');
					}
				});
			}
		});
	};
	
	var updateAction = function(){
    	$("#updateEmplTimekeepingSignBtn").click(function(){
    		var rowindexes = $("#jqxGridEmplTimekeepingSign").jqxGrid('getselectedrowindexes');
    		if(rowindexes.length > 0){
    			$("#updateEmplTimekeepingSignBtn").attr("disabled", "disabled");
    			var dataSubmit = new Array();
    			var partyIdSubmit = $("#jqxGridEmplTimekeepingSign").jqxGrid('getrowdata', rowindexes[0]).partyId;
    			var dateAttendanceSubmit = $("#jqxGridEmplTimekeepingSign").jqxGrid('getrowdata', rowindexes[0]).dateAttendance;
    			var emplTimesheetId = $("#jqxGridEmplTimekeepingSign").jqxGrid('getrowdata', rowindexes[0]).emplTimesheetId;
    			for(var i = 0; i < rowindexes.length; i++){
    				var rowData = $("#jqxGridEmplTimekeepingSign").jqxGrid('getrowdata', rowindexes[i]);
    				var timekeepingSignUpdateInfo = {};
    				timekeepingSignUpdateInfo["emplTimekeepingSignId"] = rowData.emplTimekeepingSignId; 
    				if(rowData.hours){
    					timekeepingSignUpdateInfo["hours"] = rowData.hours;	 
    				}
    				if(rowData.workday){
    					timekeepingSignUpdateInfo["workday"] = rowData.workday 
    				}
    				dataSubmit.push(timekeepingSignUpdateInfo);
    			}	
    			
    			$("#jqxGridEmplTimekeepingSign").jqxGrid('showloadelement');
    			$.ajax({
    				url: "updateEmplTimesheetAttendance",
    				data: {partyId: partyIdSubmit, dateAttendance: dateAttendanceSubmit.getTime(), emplTimekeepingSignList: JSON.stringify(dataSubmit), emplTimesheetId: emplTimesheetId},
    				type: 'POST',
    				success: function(data){
    					$("#jqxNotificationTimesheetInDay").jqxNotification('closeLast');
    					if(data._EVENT_MESSAGE_){
    						$("#jqxTimesheetAtt").jqxGrid('updatebounddata');
    						$("#jqxNotificationTimesheetInDayContent").text(data._EVENT_MESSAGE_);
    						$("#jqxNotificationTimesheetInDay").jqxNotification({template: 'info'});
    						$("#jqxNotificationTimesheetInDay").jqxNotification("open");
    						$("#jqxGridEmplTimekeepingSign").jqxGrid('updatebounddata');
    					}else{
    						$("#jqxNotificationTimesheetInDayContent").text(data._ERROR_MESSAGE_);
    						$("#jqxNotificationTimesheetInDay").jqxNotification({template: 'error'});
    						$("#jqxNotificationTimesheetInDay").jqxNotification("open");	
    					}
    				},
    				complete: function(jqXHR, status){
    					$("#updateEmplTimekeepingSignBtn").removeAttr("disabled");
    					$("#jqxGridEmplTimekeepingSign").jqxGrid('hideloadelement');
    				}
    			});
    		}
    	});
    };
    
    var createEmplTimesheet = function(){
    	var check = $("#checkImportData").jqxCheckBox('checked');
    	var importDataTimeRecord = "N";
    	if(check){
    		importDataTimeRecord = "Y";
    	}
    	if(importDataTimeRecord == "N"){
    		bootbox.dialog(uiLabelMap.TimesheetNotImportData + ". " + uiLabelMap.AreYouSure, 
    		[{
    		    "label" : uiLabelMap.CommonSubmit,
    		    "class" : "btn-primary btn-small icon-ok open-sans",
    		    "callback": function() {
    		    	addNewRowTimesheets(importDataTimeRecord);
    		    	$("#popupAddRow").jqxWindow('close');
    		    	$("#alterSave").removeAttr("disabled");
    		    }
    		},
    		{
    		    "label" : uiLabelMap.CommonCancel,
    		    "class" : "btn-danger icon-remove btn-small",
    		    "callback": function() {
    		    	$("#alterSave").removeAttr("disabled");
    		    }
    		}]);
    	}else{
    		addNewRowTimesheets(importDataTimeRecord);
    		$("#popupAddRow").jqxWindow('close');
    	}
    };
    
    var addNewRowTimesheets = function (importDataTimeRecord){
    	var partyIdSelect = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
    	var row = 
    	{
       		emplTimesheetName: $("#emplTimesheetNameAdd").val(),
       		customTimePeriodId: $("#monthCustomTime").val(),
       		partyId: partyIdSelect.value,
       		groupName: partyIdSelect.label,
       		importDataTimeRecord: importDataTimeRecord
       	};
       	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
    };
	    
	return{
		init: init
	}
	
}());

$(document).ready(function(){
	editEmplTimesheetObject.init();
});