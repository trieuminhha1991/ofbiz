var approvalEmplLeaveObject = (function(){
	var _commentApproval = "";
	var fromDateLeaveTypeId;
	var thruDateLeaveTypeId;
	var statusId;
	var emplLeaveId
	var init = function(){
			initJqxInput();
			initJqxDropDownList();
			initJqxDropDownEvent();
			initJqxDateTimeInput();
			initJqxDateTimeInputEvent();
			initJqxNumerInput();
			initJqxRadioBtn();
			initJqxWindow();
			initJqxPanel();
			initJqxBtnEvent();
			initJqxNotification();
			initJqxValidator();
	};
	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};
	
	var initJqxInput = function(){
		$("#employeeName").jqxInput({width: '96%', height: 20, disabled: true});
		$("#emplPositionType").jqxInput({width: '96%', height: 20, disabled: true});
		$("#partyIdFrom").jqxInput({width: '96%', height: 20, disabled: true});
	};
	
	var initJqxNumerInput = function(){
		$("#rateBenefit").jqxNumberInput({ width: '100%', decimalDigits: 0, digits: 3, symbol: '%', height: '25px', 
			disabled: true,spinButtons: false, inputMode: 'simple',  symbolPosition: 'right'});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.emplLeaveReasonArr, $("#emplLeaveReason"), "emplLeaveReasonTypeId", "description", 25, '100%');
		createJqxDropDownList(globalVar.workingShiftArr, $("#workingShift"), "workingShiftId", "workingShiftName", 25, '97%');
		createJqxDropDownList(globalVar.statusArr, $("#statusId"), "statusId", "description", 25, '97%');
		createJqxDropDownList([], $("#fromDateTime"), "leaveTypeId", "description", 25, '92%');
		createJqxDropDownList([], $("#thruDateTime"), "leaveTypeId", "description", 25, '92%');
		$("#statusId").jqxDropDownList({disabled: true});
	};
	var initJqxDropDownEvent = function(){
		$("#emplLeaveReason").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var index = args.index;
		    	var dataRecord = globalVar.emplLeaveReasonArr[index];
		    	if(dataRecord.rateBenefit){
		    		$("#rateBenefit").val(dataRecord.rateBenefit);
		    	}else{
		    		$("#rateBenefit").val(0)
		    	}
		    }
		});
		$("#workingShift").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
		    	var dataRecord = globalVar.workingShiftArr[index];
		    	var shiftStartTime = dataRecord.shiftStartTime;
		    	var shiftEndTime = dataRecord.shiftEndTime;
		    	var shiftBreakStart = dataRecord.shiftBreakStart;
		    	var shiftBreakEnd = dataRecord.shiftBreakEnd;
		    	var fromDateTimeArr = [{leaveTypeId: globalVar.leaveFirstHalf, description: getHours(shiftStartTime) + ":" + getMinutes(shiftStartTime)},
		    	                       {leaveTypeId: globalVar.leaveSecondHalf, description: getHours(shiftBreakEnd) + ":" + getMinutes(shiftBreakEnd)}];

		    	var thruDateTimeArr = [{leaveTypeId: globalVar.leaveFirstHalf, description: getHours(shiftBreakStart) + ":" + getMinutes(shiftBreakStart)},
		    	                       {leaveTypeId: globalVar.leaveSecondHalf, description: getHours(shiftEndTime) + ":" + getMinutes(shiftEndTime)}];
		    	updateSourceDropdownlist($("#fromDateTime"), fromDateTimeArr);
		    	updateSourceDropdownlist($("#thruDateTime"), thruDateTimeArr);
		    	$("#fromDateTime").jqxDropDownList({selectedIndex: 0});
		    	$("#thruDateTime").jqxDropDownList({selectedIndex: thruDateTimeArr.length -1});
		    	$("#fromDateTime").jqxDropDownList('selectItem', fromDateLeaveTypeId);
				$("#thruDateTime").jqxDropDownList('selectItem', thruDateLeaveTypeId);
			}
		});
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		}
		createJqxWindow($("#approvalEmplLeaveWindow"), 600, 565, initContent);
		$("#approvalEmplLeaveWindow").on('close', function(event){
			Grid.clearForm($(this));
			updateSourceDropdownlist($("#fromDateTime"), []);
	    	updateSourceDropdownlist($("#thruDateTime"), []);
	    	$("#thruDateTime, #fromDateTime, #workingShift, #emplLeaveReason").jqxDropDownList({disabled: false});
	    	$("#fromDate, #thruDate, #dateApplication").jqxDateTimeInput({disabled: false});
	    	$("#approvalEmplLeaveWindow").jqxValidator('hide');
		});
		$("#approvalEmplLeaveWindow").on('open', function(event){
			$("#notes").val(_commentApproval);	
		});
	};
	
	var initJqxPanel = function(){
		$("#jqxPanelApproval").jqxPanel({width: '100%', height: 470, scrollBarSize: 15});
	};
	
	var setData = function(data){
		fromDateLeaveTypeId = data.fromDateLeaveTypeId;
		thruDateLeaveTypeId = data.thruDateLeaveTypeId;
		$("#emplLeaveReason").jqxDropDownList('selectItem', data.emplLeaveReasonTypeId);
		$("#workingShift").jqxDropDownList('selectItem',data.workingShiftId);
		$("#fromDate").jqxDateTimeInput('setDate', data.fromDate);
		$("#thruDate").jqxDateTimeInput('setDate', data.thruDate);		
		if(data.commentApproval){
			_commentApproval = data.commentApproval;
		}else{
			_commentApproval = "";
		}
		$("#employeeName").val(data.fullName + "-" + data.partyCode);
		if(data.emplPositionTypeDesc){
			$("#emplPositionType").val(data.emplPositionTypeDesc);
		}
		if(data.groupName){
			$("#partyIdFrom").val(data.groupName);
		}
		if(data.description){
			$("#emplLeaveDesc").html(data.description);
		}else{
			$("#emplLeaveDesc").html("________");
		}
		$("#statusId").val(data.statusId);
		statusId = data.statusId;
		$("#dateApplication").jqxDateTimeInput('setDate', data.dateApplication);
		$("#thruDateTime, #fromDateTime, #workingShift, #emplLeaveReason").jqxDropDownList({disabled: true});
		$("#fromDate, #thruDate, #dateApplication").jqxDateTimeInput({disabled: true});
		emplLeaveId = data.emplLeaveId;
		if(statusId == "LEAVE_APPROVED"){
			$("#acceptEmplLeave").jqxRadioButton({checked: true});
		}else if(statusId == "LEAVE_CANCEL"){
			$("#cancelEmplLeave").jqxRadioButton({checked: true});
		}else if(statusId == "LEAVE_REJECTED"){
			$("#rejectEmplLeave").jqxRadioButton({checked: true});
		}
	};
	
	var initJqxRadioBtn = function(){
		$("#acceptEmplLeave").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#rejectEmplLeave").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#cancelEmplLeave").jqxRadioButton({ width: '94%', height: 25, checked: false});
	};
	
	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({ width: '100%', height: '25px'});
		$("#thruDate").jqxDateTimeInput({ width: '100%', height: '25px'});
		$("#dateApplication").jqxDateTimeInput({width: '97%', height: '25px'});
	};
	
	var initJqxDateTimeInputEvent = function(){
		$("#fromDate").on('valueChanged', function(event){
			var date = event.args.date;
			if(date){
				var endYear = new Date(date.getFullYear(), 11, 31);
				$("#thruDate").jqxDateTimeInput('setMaxDate', endYear);
			}
		});
	};
	
	var initJqxBtnEvent = function(){
		$("#btnCancel").click(function(event){
			$("#approvalEmplLeaveWindow").jqxWindow('close');
		});
		$("#btnSave").click(function(event){
			var valid = $("#approvalEmplLeaveWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var warningMesg = "";
			if(statusId != "LEAVE_CREATED"){
				warningMesg = uiLabelMap.ChangeStatusEmplLeaveAfterApporveConfirm;
			}else{
				warningMesg = uiLabelMap.ApprovalEmplLeaveConfirm; 
			}
			bootbox.dialog(warningMesg,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							approvalEmplLeave();
							$("#approvalEmplLeaveWindow").jqxWindow('close');
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
						"callback": function() {
						}
					}]		
			);
		});
	};
	
	var approvalEmplLeave = function(){
		$("#jqxgrid").jqxGrid({ disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		disabledBtn();
		var statusId = "";
		var dataSubmit = {emplLeaveId: emplLeaveId, commentApproval: $("#notes").jqxEditor('val')};
		var accept = $("#acceptEmplLeave").jqxRadioButton('checked');
		var reject = $("#rejectEmplLeave").jqxRadioButton('checked');
		var cancel = $("#cancelEmplLeave").jqxRadioButton('checked');
		if(accept){
			dataSubmit.statusId = "LEAVE_APPROVED";
		}else if(reject){
			dataSubmit.statusId = "LEAVE_REJECTED";
		}else if(cancel){
			dataSubmit.statusId = "LEAVE_CANCEL";
		}
		$.ajax({
			url: 'approvalEmplLeave',
			data: dataSubmit,
			type: 'POST', 
			success: function(response){
				$("#jqxNtf").jqxNotification('closeLast');
				if(response._EVENT_MESSAGE_){
					$("#jqxNtfContent").text(response._EVENT_MESSAGE_);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#jqxNtfContent").text(response._ERROR_MESSAGE_);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({ disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
				enableBtn();
			}
		});
	};
	
	var disabledBtn = function(){
		$("#btnSave").attr("disabled", "disabled");
		$("#btnCancel").attr("disabled", "disabled");
	};
	var enableBtn = function(){
		$("#btnSave").removeAttr("disabled");
		$("#btnCancel").removeAttr("disabled");
	};
	
	var initJqxEditor = function(){
		$('#notes').jqxEditor({ 
    		width: '97%',
            theme: 'olbiuseditor',
            tools: '',
            disabled: false,
            height: 100,
        });	
	};
	
	var initJqxValidator = function(){
		$("#approvalEmplLeaveWindow").jqxValidator({
			rules:[
				{input : '#cancelEmplLeave', message : uiLabelMap.PleaseSelectOption, action: 'blur', 
					rule : function(input, commit){
						var acceptEmplLeave = $("#acceptEmplLeave").jqxRadioButton('checked');
						var rejectEmplLeave = $("#rejectEmplLeave").jqxRadioButton('checked');
						var cancelEmplLeave = $("#cancelEmplLeave").jqxRadioButton('checked');
						if(!acceptEmplLeave && !rejectEmplLeave && !cancelEmplLeave){
							return false;
						}
						return true;
					}
				},      	
	        ]
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#approvalEmplLeaveWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());

$(document).ready(function(){
	approvalEmplLeaveObject.init();
});