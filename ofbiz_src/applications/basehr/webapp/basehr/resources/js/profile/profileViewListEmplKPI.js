var viewEmplListKPIGeneralObj = (function(){
	var init = function(){
		initBtnEvent();
		initJqxGrid();
		initJqxWindow();
		initJqxValidator();
		create_spinner($("#spinnerAjaxAppr"));
		$("#jqxNotificationApprover").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerApprover"});
	};
	var initJqxGrid = function(){
		createDropDownGridApprover($("#approverListDropDownBtn"), $("#jqxGridApprover"),
				{EmployeeId: uiLabelMap.EmployeeId, EmployeeName: uiLabelMap.EmployeeName, HrCommonPosition: uiLabelMap.HrCommonPosition});
	};
	var initBtnEvent = function(){
		$("#removeFilter").on('click', function(){
			$('#jqxgrid_daily').jqxGrid('clearfilters');
			$('#jqxgrid_weekly').jqxGrid('clearfilters');
			$('#jqxgrid_monthly').jqxGrid('clearfilters');
			$('#jqxgrid_quarterly').jqxGrid('clearfilters');
			$('#jqxgrid_yearly').jqxGrid('clearfilters');
		});
		$("#sendPropsalAppr").click(function(event){
			openJqxWindow($("#ProposalApprWindow"));
		});
		$("#btnCancel").click(function(event){
			$("#ProposalApprWindow").jqxWindow('close');
		});
		$("#btnSave").click(function(event){
			var valid = $("#ProposalApprWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var approverRowIndex = $("#jqxGridApprover").jqxGrid('getselectedrowindex');
			var approvedPartyData = $("#jqxGridApprover").jqxGrid('getrowdata', approverRowIndex);
			var approverPartyId = approvedPartyData.partyId;
			$("#btnSave").attr("disabled", "disabled");
			$("#btnCancel").attr("disabled", "disabled");
			$("#approverListDropDownBtn").jqxDropDownButton({disabled: true});
			$("#ajaxLoadingAppr").show();
			$.ajax({
				url: 'ntfApprovalKPI',
				data: {approverPartyId: approverPartyId},
				type: 'POST',
				success: function(response){
					$("#jqxNotificationApprover").jqxNotification('closeLast');
					if(response._EVENT_MESSAGE_){
						$("#notificationContentApprover").text(response._EVENT_MESSAGE_);
						$("#jqxNotificationApprover").jqxNotification({template: 'info'});
						$("#jqxNotificationApprover").jqxNotification("open");
						$("#ProposalApprWindow").jqxWindow('close');
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
				error: function(jqXHR, textStatus, errorThrown){
					
				},
				complete: function(jqXHR, textStatus){
					$("#btnSave").removeAttr("disabled");
					$("#btnCancel").removeAttr("disabled");
					$("#approverListDropDownBtn").jqxDropDownButton({disabled: false});
					$("#ajaxLoadingAppr").hide();
				}
			});
		});
	};
	var initJqxValidator = function(){
		$("#ProposalApprWindow").jqxValidator({
			rules: [
				{input : '#approverListDropDownBtn', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var selectedIndex = $("#jqxGridApprover").jqxGrid('getselectedrowindex');
						if(selectedIndex < 0){
							return false;
						}
						return true;
					}
				},   
			]
		});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#ProposalApprWindow"), 460, 135);
		$("#ProposalApprWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#ProposalApprWindow").on('open', function(event){
			$("#jqxGridApprover").jqxGrid('clearselection');
			$("#jqxGridApprover").jqxGrid({selectedrowindex: 0});
		});
	};
	return{
		init: init,
	}
}());
$(document).ready(function(){
	viewEmplListKPIGeneralObj.init();
});