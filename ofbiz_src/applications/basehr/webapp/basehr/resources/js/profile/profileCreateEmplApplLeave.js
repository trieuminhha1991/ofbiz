var createEmplLeaveObject = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownEvent();
		initJqxDateTimeInput();
		initJqxDateTimeInputEvent();
		initJqxNumerInput();
		initJqxWindow();
		initJqxValidator();
		initJqxBtnEvent();
		initJqxNotification();
		initDropDownGrid();
	};
	
	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};
	var initDropDownGrid = function(){
		createDropDownGridApprover($("#approverListDropDownBtn"), $("#jqxGridApprover"),
				{EmployeeId: uiLabelMap.EmployeeId, EmployeeName: uiLabelMap.EmployeeName, HrCommonPosition: uiLabelMap.HrCommonPosition});
	};
	var initJqxInput = function(){
		$("#employeeName").jqxInput({width: '100%', height: 20, disabled: true});
		$("#employeeId").jqxInput({width: '96%', height: 20, disabled: true});
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
		createJqxDropDownList([], $("#fromDateTime"), "leaveTypeId", "description", 25, '92%');
		createJqxDropDownList([], $("#thruDateTime"), "leaveTypeId", "description", 25, '92%');
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
			}
		});
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		}
		createJqxWindow($("#addNewWindow"), 600, 570, initContent);
		$("#addNewWindow").on('open', function(event){
			initData();
			$("#jqxGridApprover").jqxGrid('clearselection');
		});
		$("#addNewWindow").on('close', function(event){
			Grid.clearForm($(this));
			updateSourceDropdownlist($("#fromDateTime"), []);
	    	updateSourceDropdownlist($("#thruDateTime"), []);
	    	hideValidate();
		});
	};
	
	var initData = function(){
		$("#fromDate").val(new Date(globalVar.nowTimestamp));
		$("#employeeName").val(globalVar.partyName);
		$("#employeeId").val(globalVar.partyCode);
		$("#emplPositionType").val(globalVar.emplPositionType);
		$("#partyIdFrom").val(globalVar.partyIdFrom);
		$("#dateApplication").val(new Date(globalVar.startDate));
		$("#workingShift").jqxDropDownList({selectedIndex: 0});
	};
	
	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({ width: '100%', height: '25px'})
		$("#fromDate").val(null);
		$("#thruDate").jqxDateTimeInput({ width: '100%', height: '25px'})
		$("#thruDate").val(null);
		$("#dateApplication").jqxDateTimeInput({width: '97%', height: '25px', disabled: true})
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
	
	var initJqxValidator = function(){
		$("#addNewWindow").jqxValidator({
			rules:[
			       {
			    	   input: '#emplLeaveReason',
			    	   message: uiLabelMap.FieldRequired,
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input: '#workingShift',
			    	   message: uiLabelMap.FieldRequired,
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input: '#fromDate',
			    	   message: uiLabelMap.FieldRequired,
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input: '#thruDate',
			    	   message: uiLabelMap.FieldRequired,
			    	   rule: function (input, commit){
			    		   if(!input.val()){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input: '#fromDate',
			    	   message: uiLabelMap.FromDateLessThanEqualThruDate,
			    	   rule: function (input, commit){
			    		   var thruDate = $('#thruDate').jqxDateTimeInput('val', 'date');
			    		   if(thruDate){
			    			   if(input.jqxDateTimeInput('val', 'date').getTime() > thruDate.getTime()){
			    				   return false;
			    			   }
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input: '#thruDate',
			    	   message: uiLabelMap.GTDateFieldRequired,
			    	   rule: function (input, commit){
			    		   var fromDate = $('#fromDate').jqxDateTimeInput('val', 'date');
			    		   var thruDate = input.jqxDateTimeInput('val', 'date');
			    		   if(thruDate && thruDate.getTime() < fromDate.getTime()){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input: '#thruDateTime',
			    	   message: uiLabelMap.TimeChooseIsNotValid,
			    	   rule: function (input, commit){
			    		   var fromDate = $('#fromDate').jqxDateTimeInput('val', 'date');
			    		   var thruDate = $('#thruDate').jqxDateTimeInput('val', 'date');
			    		   if(thruDate && thruDate.getTime() == fromDate.getTime()){
			    			   if(input.val() == globalVar.leaveFirstHalf && $("#fromDateTime").val() == globalVar.leaveSecondHalf){
			    				   return false;
			    			   }
			    		   }
			    		   return true;
			    	   }
			       },
			       {
			    	   input : '#fromDate',
			    	   message : uiLabelMap.DayOffCantBeBeforeApply,
			    	   rule : function(input, commit){
			    		   var fromDate = $('#fromDate').jqxDateTimeInput('val', 'date');
			    		   var dateApplication = $('#dateApplication').jqxDateTimeInput('val','date');
			    		   if(fromDate < dateApplication){
			    			   return false;
			    		   }
			    		   return true;
			    	   }
			       },
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
	
	var initJqxBtnEvent = function(){
		$("#btnCancel").click(function(event){
			$("#addNewWindow").jqxWindow('close');
		});
		$("#btnSave").click(function(event){
			if(!validate()){
				return;
			}
			var fromDate = $('#fromDate').jqxDateTimeInput('val', 'date');
			var thruDate = $('#thruDate').jqxDateTimeInput('val', 'date');
			var HolidaySelect = Math.ceil((thruDate.getTime()-fromDate.getTime())/(1000 * 60 * 60 * 24));
			var HolidayRemain=$('#annualLeaveRemain').text()
			if(HolidaySelect>HolidayRemain)
			{
				OlbCore.alert.error(uiLabelMap.GTDateSelect+" "+HolidaySelect+" "+uiLabelMap.GTDateSelectExceeded+" "+HolidayRemain);
				return;
			}
			$("#btnSave").attr("disabled", "disabled");
			bootbox.dialog(uiLabelMap.HrCreateNewConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createNewEmplLeave();
							$("#addNewWindow").jqxWindow('close');
							$("#btnSave").removeAttr("disabled");
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
						"callback": function() {
							$("#addNewWindow").jqxWindow('close');
						}
					}]		
			);
		});
		
	};
	
	var createNewEmplLeave = function(){
		var data = getData();
		$("#jqxgrid").jqxGrid({ disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		emplLeaveObject.disableBtn();
		$.ajax({
			url: 'createEmplLeave',
			data: data,
			type: 'POST', 
			success: function(response){
				$("#jqxNtf").jqxNotification('closeLast');
				if(response.responseMessage == "success"){
					$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({ disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
				emplLeaveObject.enableBtn();
			}
		});
	}
	
	var getData = function(){
		var data = {};
		data.emplLeaveReasonTypeId = $("#emplLeaveReason").val();
		data.workingShiftId = $("#workingShift").val();
		data.fromDate = $("#fromDate").jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $("#thruDate").jqxDateTimeInput('val', 'date').getTime();
		data.description = $("#notes").jqxEditor('val');
		data.fromDateLeaveTypeId = $("#fromDateTime").val();
		data.thruDateLeaveTypeId = $("#thruDateTime").val();
		var approverRowIndex = $("#jqxGridApprover").jqxGrid('getselectedrowindex');
		var approvedPartyData = $("#jqxGridApprover").jqxGrid('getrowdata', approverRowIndex);
		data.approverPartyId = approvedPartyData.partyId;
		return data;
	};
	
	var validate = function(){
		return $("#addNewWindow").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#addNewWindow").jqxValidator('hide');
	};
	
	var initJqxEditor = function(){
		$('#notes').jqxEditor({ 
    		width: '97%',
            theme: 'olbiuseditor',
            tools: '',
            height: 100,
        });	
	};
	
	var openWindow = function(){
		openJqxWindow($("#addNewWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	createEmplLeaveObject.init();
});