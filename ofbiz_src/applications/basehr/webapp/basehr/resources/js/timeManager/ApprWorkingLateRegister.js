var apprWorkingovertimeObj = (function(){
	var _workingLateRegisterId = "";
	var init = function(){
		initJqxInput();
		initJqxDateTime();
		initJqxRadioBtn();
		initDropDown();
		initNumberInput();
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
	};
	var initJqxRadioBtn = function(){
		$("#acceptWorkingLate").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#rejectWorkingLate").jqxRadioButton({ width: '95%', height: 25, checked: false});
	};
	var initDropDown = function(){
		var source = {
				localdata: globalVar.weekdayEnumArr,
	            datatype: "array"
		}
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#dayOfWeekApplAppr").jqxComboBox({checkboxes: true, source: dataAdapter, displayMember: "description", 
	   		valueMember: "enumId", height: 25, width: '98%', theme: 'olbius', multiSelect: true, disabled: true});
		createJqxDropDownList(globalVar.statusListWorkingLateArr, $("#statusIdAppr"), "statusId", "description", 25, '98%');
		$("#statusIdAppr").jqxDropDownList({disabled: true});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			$("#reasonReject").jqxEditor({            
		        width: '98%',
		        theme: 'olbiuseditor',
		        tools: '',
		        height: 100,
		    });
		};
		createJqxWindow($("#ApprWorkingLateRegWindow"), 560, 550, initContent);
	};
	var initNumberInput = function(){
		$("#lateInStartShiftAppr").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple' });
		$("#earlyOutShiftBreakAppr").jqxNumberInput({ width: '92%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple' });
		$("#lateInShiftBreakAppr").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple' });
		$("#earlyOutEndShiftAppr").jqxNumberInput({ width: '92%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple' });
	};
	var initJqxPanel = function(){
		$("#jqxPanelApproval").jqxPanel({width: '100%', height: 450, scrollBarSize: 15, autoUpdate: true});
	};
	var openWindow = function(){
		openJqxWindow($("#ApprWorkingLateRegWindow"));
	};
	var setData = function(data){
		_workingLateRegisterId = data.workingLateRegisterId;
		$("#statusIdAppr").val(data.statusId);
		$("#partyIdAppr").val(data.fullName);
		$("#emplPositionAppr").val(data.emplPositionTypeDesc);
		$("#groupNameAppr").val(data.department);
		$("#fromDateAppr").val(data.fromDate);
		$("#thruDateAppr").val(data.thruDate);
		$("#lateInStartShiftAppr").val(data.lateInStartShift);
		$("#earlyOutShiftBreakAppr").val(data.earlyOutShiftBreak);
		$("#lateInShiftBreakAppr").val(data.lateInShiftBreak);
		$("#earlyOutEndShiftAppr").val(data.earlyOutEndShift);
		$("#loadingAppr").show();
		$("#cancelAppr").attr("disabled", "disabled");
		$("#saveAppr").attr("disabled", "disabled");
		$.ajax({
			url: 'getWorkingLateRegisEnum',
			data: {workingLateRegisterId: _workingLateRegisterId},
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
		data.workingLateRegisterId = _workingLateRegisterId;
		data.fromDate = $('#fromDateAppr').jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $('#thruDateAppr').jqxDateTimeInput('val', 'date').getTime();
		data.reasonReject = $("#reasonReject").jqxEditor('val');
		data.lateInStartShift = $("#lateInStartShiftAppr").val();
		data.earlyOutShiftBreak = $("#earlyOutShiftBreakAppr").val();
		data.lateInShiftBreak = $("#lateInShiftBreakAppr").val();
		data.earlyOutEndShift = $("#earlyOutEndShiftAppr").val();
		var accept = $("#acceptWorkingLate").jqxRadioButton('checked');
		var reject = $("#rejectWorkingLate").jqxRadioButton('checked');
		if(accept){
			data.statusId = "EMPL_LATE_ACCEPTED";
		}else if(reject){
			data.statusId = "EMPL_LATE_REJECTED";
		}
		return data;
	};
	var initEvent = function(){
		$("#ApprWorkingLateRegWindow").on('close', function(event){
			Grid.clearForm($("#ApprWorkingLateRegWindow"));
			_workingLateRegisterId = "";
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
			$("#ApprWorkingLateRegWindow").jqxWindow('close');
		});
		$("#saveAppr").click(function(event){
			var valid = $("#ApprWorkingLateRegWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getData();
			$("#loadingAppr").show();
			$("#cancelAppr").attr("disabled", "disabled");
			$("#saveAppr").attr("disabled", "disabled");
			$.ajax({
				url: 'approvalWorkingLateRegister',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
							template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
						$("#jqxgrid").jqxGrid('updatebounddata');
						$("#ApprWorkingLateRegWindow").jqxWindow('close');
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
		$("#ApprWorkingLateRegWindow").jqxValidator({
			rules:[
				{input : '#rejectWorkingLate', message : uiLabelMap.PleaseSelectOption, action: 'blur', 
					rule : function(input, commit){
						var acceptWorkingLate = $("#acceptWorkingLate").jqxRadioButton('checked');
						var rejectWorkingLate = $("#rejectWorkingLate").jqxRadioButton('checked');
						if(!acceptWorkingLate && !rejectWorkingLate){
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