var apprWorkingovertimeObj = (function(){
	var _workOvertimeRegisId = "";
	var init = function(){
		initJqxInput();
		initJqxDateTime();
		initJqxRadioBtn();
		initDropDown();
		initJqxWindow();
		initJqxPanel();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerAppr"));
	};
	var initJqxInput = function(){
		$("#partyIdAppr").jqxInput({width: '96%', height: 20, disabled: true});
		$("#emplPositionAppr").jqxInput({width: '96%', height: 20, disabled: true});
		$("#groupNameAppr").jqxInput({width: '96%', height: 20, disabled: true});
	};
	var initJqxDateTime = function(){
		$("#fromDateAppr").jqxDateTimeInput({width: '100%', height: 25});
		$("#thruDateAppr").jqxDateTimeInput({width: '93%', height: 25});
		$("#dateRegisteredAppr").jqxDateTimeInput({width: '97%', height: 25});
		$("#startTimeAppr").jqxDateTimeInput({width: '100%', height: 25, formatString: 'HH:mm:ss', showTimeButton: true, showCalendarButton: false});
		$("#endTimeAppr").jqxDateTimeInput({width: '93%', height: 25, formatString: 'HH:mm:ss', showTimeButton: true, showCalendarButton: false});
	};
	var initJqxRadioBtn = function(){
		$("#acceptWorkovertime").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#rejectWorkovertime").jqxRadioButton({ width: '95%', height: 25, checked: false});
	};
	var initDropDown = function(){
		var source = {
				localdata: globalVar.weekdayEnumArr,
	            datatype: "array"
		}
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#dayOfWeekApplAppr").jqxComboBox({checkboxes: true, source: dataAdapter, displayMember: "description", 
	   		valueMember: "enumId", height: 25, width: '98%', theme: 'olbius', multiSelect: true, disabled: true});
		createJqxDropDownList(globalVar.statusWorkOvertimeArr, $("#statusIdAppr"), "statusId", "description", 25, '98%');
		$("#statusIdAppr").jqxDropDownList({disabled: true});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			$("#reasonApproval").jqxEditor({            
		        width: '98%',
		        theme: 'olbiuseditor',
		        tools: '',
		        height: 100,
		    });
		};
		createJqxWindow($("#ApprWorkovertimeRegWindow"), 560, 550, initContent);
	};
	var initJqxPanel = function(){
		$("#jqxPanelApproval").jqxPanel({width: '100%', height: 450, scrollBarSize: 15, autoUpdate: true});
	};
	var openWindow = function(){
		openJqxWindow($("#ApprWorkovertimeRegWindow"));
	};
	var setData = function(data){
		_workOvertimeRegisId = data.workOvertimeRegisId;
		$("#statusIdAppr").val(data.statusId);
		$("#partyIdAppr").val(data.fullName);
		$("#emplPositionAppr").val(data.emplPositionTypeDesc);
		$("#groupNameAppr").val(data.department);
		$("#dateRegisteredAppr").val(data.dateRegistered);
		$("#fromDateAppr").val(data.fromDate);
		$("#thruDateAppr").val(data.thruDate);
		$("#startTimeAppr").val(data.startTime);
		$("#endTimeAppr").val(data.endTime);
		$("#loadingAppr").show();
		$("#cancelAppr").attr("disabled", "disabled");
		$("#saveAppr").attr("disabled", "disabled");
		$.ajax({
			url: 'getWorkOvertimeRegisEnum',
			data: {workOvertimeRegisId: _workOvertimeRegisId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					var enumIdList = response.enumIdList;
					for(var i = 0; i < enumIdList.length; i++){
						$("#dayOfWeekApplAppr").jqxComboBox('checkItem', enumIdList[i]);
					}
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingAppr").hide();
				$("#cancelAppr").removeAttr("disabled");
				$("#saveAppr").removeAttr("disabled");
			}
		});
	};
	var getData = function(){
		var data = {};
		data.workOvertimeRegisId = _workOvertimeRegisId;
		data.dateRegistered = $('#dateRegisteredAppr').jqxDateTimeInput('val', 'date').getTime();
		data.fromDate = $('#fromDateAppr').jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $('#thruDateAppr').jqxDateTimeInput('val', 'date').getTime();
		data.startTime = $('#startTimeAppr').jqxDateTimeInput('val', 'date').getTime();
		data.endTime = $('#endTimeAppr').jqxDateTimeInput('val', 'date').getTime();
		data.reasonApproval = $("#reasonApproval").jqxEditor('val');
		var accept = $("#acceptWorkovertime").jqxRadioButton('checked');
		var reject = $("#rejectWorkovertime").jqxRadioButton('checked');
		if(accept){
			data.statusId = "WOTR_ACCEPTED";
		}else if(reject){
			data.statusId = "WOTR_REJECTED";
		}
		return data;
	};
	var initEvent = function(){
		$("#ApprWorkovertimeRegWindow").on('close', function(event){
			Grid.clearForm($("#ApprWorkovertimeRegWindow"));
			_workOvertimeRegisId = "";
			$("#dayOfWeekApplAppr").jqxComboBox('uncheckAll');
		});
		$("#dayOfWeekApplAppr").on('checkChange', function (event){
			var args = event.args;
			var item = args.item;
			var value = item.value;
			var checked = item.checked;
			if(value == "_NA_"){
				if(checked){
					for(var i = 1; i < 8; i++){
						$("#dayOfWeekApplAppr").jqxComboBox('uncheckIndex', i); 
						$("#dayOfWeekApplAppr").jqxComboBox('disableAt', i); 
					}
				}else{
					for(var i = 1; i < 8; i++){
						$("#dayOfWeekApplAppr").jqxComboBox('enableAt', i); 
					}
				}
			}
		});
		$("#cancelAppr").click(function(event){
			$("#ApprWorkovertimeRegWindow").jqxWindow('close');
		});
		$("#saveAppr").click(function(event){
			var valid = $("#ApprWorkovertimeRegWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getData();
			$("#loadingAppr").show();
			$("#cancelAppr").attr("disabled", "disabled");
			$("#saveAppr").attr("disabled", "disabled");
			$.ajax({
				url: 'approvalWorkingOvertimeRegister',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
							template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
						$("#jqxgrid").jqxGrid('updatebounddata');
						$("#ApprWorkovertimeRegWindow").jqxWindow('close');
					}else{
						bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#loadingAppr").hide();
					$("#cancelAppr").removeAttr("disabled");
					$("#saveAppr").removeAttr("disabled");
				}
			});
		});
	};
	var initJqxValidator = function(){
		$("#ApprWorkovertimeRegWindow").jqxValidator({
			rules:[
				{input : '#rejectWorkovertime', message : uiLabelMap.PleaseSelectOption, action: 'blur', 
					rule : function(input, commit){
						var acceptWorkovertime = $("#acceptWorkovertime").jqxRadioButton('checked');
						var rejectWorkovertime = $("#rejectWorkovertime").jqxRadioButton('checked');
						if(!acceptWorkovertime && !rejectWorkovertime){
							return false;
						}
						return true;
					}
				},      	
	        ]
		});
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());
var contextMenuObj = (function(){
	var init = function(){
		createJqxMenu("contextMenu", 30, 150);
		initEvent();
	};
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "approver"){
            	apprWorkingovertimeObj.openWindow();
            	apprWorkingovertimeObj.setData(dataRecord);
            }
		});
	};
	return{
		init: init
	}
}()); 
$(document).ready(function () {
	 apprWorkingovertimeObj.init();
	 contextMenuObj.init();
});