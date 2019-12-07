var viewEmplWorkingLateObject = (function(){
	var init = function(){
		initJqxDateTimeInput();
		initJqxDropDownList();
		initJqxValidator();
		initJqxInput();
		initJqxNumberInput();
		initBtnEvent();
		initJqxWindow();
		initGrid();
		initGridEvent();
	};
	var initGridEvent = function(){
		$('#addEmployeeId').on('rowselect', function(event){
			var partyId = event.args.row.partyId;
			var partyName = event.args.row.partyName;
			contentDropDownButton.push(partyName);
			partyIdDropDown.push(partyId);
			setContentDropDownButton(contentDropDownButton);
		});
		
		$('#addEmployeeId').on('rowunselect', function(event){
			var partyId = event.args.row.partyId;
			var partyName = event.args.row.partyName;
			var index = contentDropDownButton.indexOf(partyName);
			if(index > -1){
				contentDropDownButton.splice(index, 1);
			}
			setContentDropDownButton(contentDropDownButton);
			var index1 = partyIdDropDown.indexOf(partyId);
			if(index1 > -1){
				partyIdDropDown.splice(index1, 1);
			}
		});
	};
	
	var setContentDropDownButton = function(contentDropDownButton){
		var string_content = "";
		for(var i=0;i<contentDropDownButton.length;i++){
			string_content += contentDropDownButton[i].trim() + ";";
		}
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + string_content + '</div>';
		$('#addEmployeeIdButton').jqxDropDownButton('setContent', dropDownContent);
		$('#addEmployeeIdButton').attr('title', string_content);
	};
	
	var initGrid = function(){
		var source = {
			localdata : emplDataArr,
			datafields : [
			             {name : 'partyId', type : 'string'},
		                 {name : 'partyName', type : 'string'}
            ],
            datatype : 'array',
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$('#addEmployeeId').jqxGrid({
			width : 480,
			autoHeight : true,
			sortable : false,
			filterable : true,
			showfilterrow : true,
			source : dataAdapter,
			pageable: true,
			pageSizeOptions: ['5', '10' , '15', '30', '50', '100'],
			localization: getLocalization(),
			columns : [
			           {text : uiLabelMap.EmployeeId, datafield : 'partyId', width : '50%'},
	                   {text : uiLabelMap.EmployeeName, datafield : 'partyName'}
           ],
           selectionmode : 'multiplerows'
		})
	}
	
	var initJqxDateTimeInput = function(){
		var maxDateAddNew = new Date(globalVar.nowTimestamp);
		$("#addDateWorkingLate").jqxDateTimeInput({ width: '98%', height: 25, theme: 'olbius', max: maxDateAddNew});
		$("#jqxDateTime").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		$("#jqxDateTime").jqxDateTimeInput('setRange', new Date(globalVar.fromDate), new Date(globalVar.thruDate));
		$("#jqxDateTime").on('change', function(event){
			var selection = $("#jqxDateTime").jqxDateTimeInput('getRange');
			var fromDate = selection.from;
			var thruDate = selection.to;
			var source = $("#jqxgrid").jqxGrid('source');
			source._source.url = "jqxGeneralServicer?sname=getEmplWorkingLateInPeriod&fromDate="+ fromDate.getTime() +"&thruDate=" + thruDate.getTime();
			$("#jqxgrid").jqxGrid('source', source);
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(statusWorkingLateArr, $("#statusWorkingLateAppr"), 'statusId', 'description', 25, '98%');
		createJqxDropDownList(statusWorkingLateArr, $("#addWorkingLateStatus"), 'statusId', 'description', 25, '98%');
		$('#addEmployeeIdButton').jqxDropDownButton({width : '98%', height : 25})
	};
	
	var initJqxWindow = function(){
		$("#jqxWindowAppr").jqxWindow({
			width: 500, minWidth: 550, height: 375,  maxHeight:375, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
			initContent: function(){
			}
		});
		$("#jqxWindowConfirmDelete").jqxWindow({
			width: 400, minWidth: 400, height: 130,  maxHeight:130, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
		});
		$("#jqxWindowAddrow").jqxWindow({
			width: 470,  height: 300,  resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
//			initContent: function(){
//				$("#addEmployeeId").jqxComboBox({source: emplJqxComboboxAdapter, displayMember: "partyName", valueMember: "partyId", theme: 'olbius',
//		      		itemHeight: 25, height: 25, width: '98%', multiSelect: true,
//		      		renderer: function (index, label, value) {
//						for(var i=0; i < emplDataArr.length; i++){
//							if(emplDataArr[i].partyId == value){
//								return emplDataArr[i].partyName;
//							}
//						}
//				    	return value;
//		      		}	
//				});
//			}
		});
		$("#jqxWindowAddrow").on('open', function (event) {
			$("#addEmployeeId").jqxComboBox('clearSelection');
			$("#addDateWorkingLate").val(new Date());
			$("#addWorkingLateMinutes").val(0);
			$("#addWorkingLateReason").val("");
			$("#addWorkingLateStatus").jqxDropDownList('clearSelection');
		});
		$('#jqxWindowAddrow').bind('close', function(){
			contentDropDownButton = [];
			partyIdDropDown = [];
			$('#addEmployeeIdButton').jqxDropDownButton('setContent', null);
			$('#addEmployeeIdButton').removeAttr('title');
			$('#addEmployeeId').jqxGrid('clearselection');
		})
	};
	
	var initJqxValidator = function(){
		$("#addNewForm").jqxValidator({
			rules:[
//				{input: "#addEmployeeId", message: uiLabelMap.CommonRequired, action: 'blur', 
//					rule: function(input, commit){
//						value = input.val();
//						if(!value){
//							return false;
//						}
//						return true;
//					} 
//				},
				{input: "#addDateWorkingLate", message: uiLabelMap.CommonRequired, action: 'blur', rule: 'required'},
				{input: '#addWorkingLateMinutes', message: uiLabelMap.AmountValueGreaterThanZero, action: 'blur',
					rule: function (input, commit){
						var value = input.val();
						if(value < 0){
							return false
						}
						return true;
					}	
				},
				{input: "#addWorkingLateStatus", message: uiLabelMap.CommonRequired, action: 'blur',
					rule: function(input, commit){
						value = input.val();
						if(!value){
							return false;
						}
						return true;
					}
				}
			]
		});	
	};
	
	var initJqxInput = function(){
		$("#reasonWorkingLateAppr, #addWorkingLateReason").jqxInput({height: 20, width: '96%', theme:'olbius'});
	};
	
	var initJqxNumberInput = function(){
		$("#delayTimeWorkingLateAppr").jqxNumberInput({ width: 200, height: '25px', spinButtons: false, digits: 3, decimalDigits: 0, theme: 'olbius'});
		$("#addWorkingLateMinutes").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, digits: 3, decimalDigits: 0, theme: 'olbius'});
	};
	
	var initBtnEvent = function(){
		saveAction();
		approvalAllAction();
		notApprovalAllAction();
		updateFromRecorderAction();
		btnSaveAddNewAction();
		$("#btnCancel").click(function(){
			$("#jqxWindowAppr").jqxWindow('close');
		});
		
		$("#addNewRow").click(function(event){
			openJqxWindow($("#jqxWindowAddrow"));
		});
		$("#cancelDelete").click(function(){
			$("#jqxWindowConfirmDelete").jqxWindow('close');
		});
		$("#btnCancelAddNew").click(function(){
			$("#jqxWindowAddrow").jqxWindow('close');
		});
		$("#deleteRow").click(function(){
			var selectrow = $("#jqxgrid").jqxGrid("getselectedrowindex");
			if(selectrow > -1){
				openJqxWindow($("#jqxWindowConfirmDelete"));
			}
		});
		$("#acceptDelete").click(function(){
			var selectrowIndex = $("#jqxgrid").jqxGrid("getselectedrowindex");
			if(selectrowIndex > -1){
				var rowId = $('#jqxgrid').jqxGrid('getrowid', selectrowIndex);
				$("#jqxgrid").jqxGrid('deleterow', selectrowIndex);
			}
			$("#jqxWindowConfirmDelete").jqxWindow('close');
		});
		$("#removeFilter").click(function(){
			$('#jqxgrid').jqxGrid('clearfilters');
		})
	};
	
	var saveAction = function(){
		$("#btnSave").click(function(){
			var emplWorkingLateId = $("#emplWorkingLateIdAppr").val();
			if(emplWorkingLateId){
				var dataUpdate = $("#jqxgrid").jqxGrid("getrowdatabyid", emplWorkingLateId);
				var updateRow = {
						emplWorkingLateId: emplWorkingLateId,
						partyId: dataUpdate.partyId,
						partyName: dataUpdate.partyName,
						dateWorkingLate: dataUpdate.dateWorkingLate,
						reason: $("#reasonWorkingLateAppr").val(),
						statusId: $("#statusWorkingLateAppr").val(),
						arrivalTime: dataUpdate.arrivalTime,
						delayTime: $("#delayTimeWorkingLateAppr").val()
				}
				
				$("#jqxgrid").jqxGrid('updaterow', emplWorkingLateId, updateRow);
			}
			$("#jqxWindowAppr").jqxWindow('close');
		});
	};
	
	var approvalAllAction = function(){
		$("#approvalAll").click(function(){
			$("#jqxgrid").jqxGrid("showloadelement");
			var selectionRange = $("#jqxDateTime").jqxDateTimeInput('getRange');
			$.ajax({
				url: "approvalAllEmplWorkingLateInPeriod",
				data: {fromDate: selectionRange.from.getTime(), thruDate: selectionRange.to.getTime(), statusId: "EMPL_LATE_ACCEPTED"},
				type: "POST",
				success: function(data){
					if(data._EVENT_MESSAGE_){
						$("#jqxgrid").jqxGrid('updatebounddata');
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
	                    		"info", data._EVENT_MESSAGE_, "jqxNotifyContainerApprAll");
					}else{
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
								"error", data._ERROR_MESSAGE_, "jqxNotifyContainerApprAll");
					}
				},
				complete: function(){
					$("#jqxgrid").jqxGrid("hideloadelement");
				}
			});
		});
	};
	
	var notApprovalAllAction = function(){
		$("#notApprovalAll").click(function(){
			$("#jqxgrid").jqxGrid("showloadelement");
			var selectionRange = $("#jqxDateTime").jqxDateTimeInput('getRange');
			$.ajax({
				url: "approvalAllEmplWorkingLateInPeriod",
				data: {fromDate: selectionRange.from.getTime(), thruDate: selectionRange.to.getTime(), statusId: "EMPL_LATE_REJECTED"},
				type: "POST",
				success: function(data){
					if(data._EVENT_MESSAGE_){
						$("#jqxgrid").jqxGrid('updatebounddata');
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
								"info", data._EVENT_MESSAGE_, "jqxNotifyContainerApprAll");
					}else{
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
								"error", data._ERROR_MESSAGE_, "jqxNotifyContainerApprAll");
					}
				},
				complete: function(){
					$("#jqxgrid").jqxGrid("hideloadelement");
				}
			});
		});
	};
	var updateFromRecorderAction = function(){
		$("#updateFromRecorder").click(function(){
			$("#jqxgrid").jqxGrid({ disabled: true});
			$('#jqxgrid').jqxGrid('showloadelement');
			$(this).attr("disabled", "disabled");
			var range = $("#jqxDateTime").jqxDateTimeInput('getRange');
			var from = range.from;
			var to = range.to;
			$.ajax({
				url: "updateAllEmplWorkingLateInPeriod",
				data: {fromDate: from.getTime(), thruDate: to.getTime()},
				type: "POST",
				success: function(data){
					if(data.responseMessage == "success"){
						$("#jqxgrid").jqxGrid("updatebounddata");	
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
								"info", data.successMessage, "jqxNotifyContainerApprAll");
					}else{
						openNotification($("#jqxNotificationApprovalAll"), $("#jqxNotificationApprovalContent"), 
								"error", data.errorMessage, "jqxNotifyContainerApprAll");
					}
				},
				complete: function(){
					$("#jqxgrid").jqxGrid({ disabled: false});
					$('#jqxgrid').jqxGrid('hideloadelement');
					$("#updateFromRecorder").removeAttr("disabled");
				}
			});
		});
	};
	var btnSaveAddNewAction = function(){
		$("#btnSaveAddNew").click(function(){
			var valid = $("#addNewForm").jqxValidator('validate');
			if(!valid){
				return false;
			}
//			var partyIds = $("#addEmployeeId").jqxComboBox('getSelectedItems');
//			var partyIdSubmit = new Array();
//			for(var i = 0; i < partyIds.length; i++){
//				partyIdSubmit.push({"partyId" : partyIds[i].value});
//			}
			var partyIdSubmit = new Array();
			for (var i = 0; i < partyIdDropDown.length; i++) {
				partyIdSubmit.push({"partyId" : partyIdDropDown[i]});
			}
			var data = {
					partyId: JSON.stringify(partyIdSubmit),
					dateWorkingLate: $("#addDateWorkingLate").jqxDateTimeInput('val', 'date').getTime(),
					delayTime: $("#addWorkingLateMinutes").val(),
					statusId: $("#addWorkingLateStatus").val(),
					reason: $("#addWorkingLateReason").val(),
			};
			$.ajax({
				url: "createEmplWorkingLateExt",
				type: "POST",
				data: data,
				success: function(data){
					$("#jqxNotificationAddNew").jqxNotification('closeLast');
					if(data._EVENT_MESSAGE_){
						$("#jqxNotificationAddNew").jqxNotification({
							width: "100%", opacity: 0.9,appendContainer: "#jqxNotificationContainer",
							autoOpen: false,  autoClose: true, template: "info"
						});
						$("#jqxNotificationAddNew").text(data._EVENT_MESSAGE_);
						$("#jqxgrid").jqxGrid("updatebounddata");	
					}else{
						$("#jqxNotificationAddNew").jqxNotification({
							width: "100%", opacity: 0.9, appendContainer: "#jqxNotificationContainer",
							autoOpen: false,  autoClose: true, template: "error"
						});
						$("#jqxNotificationAddNew").text(data._ERROR_MESSAGE_);
					}
					$("#jqxNotificationAddNew").jqxNotification('open');
				}, 
				error: function(){
					
				}
			});
			$("#jqxWindowAddrow").jqxWindow('close');
		});
	};
	
	var openNotification = function(notifyDiv, contentDiv, template, text, containerId){
		notifyDiv.jqxNotification('closeLast');
		contentDiv.text(text);
		notifyDiv.jqxNotification({
	        width: "100%", opacity: 0.9, appendContainer: "#" + containerId,
	        autoOpen: false,  autoClose: true, template: template});
		notifyDiv.jqxNotification("open");
	};
	
	var clearDataInWindow = function(){
		$("#partyIdWorkingLateAppr").text("");
		$("#partyNameWorkingLateAppr").text("");
		$("#dateWorkingLateAppr").text("");
		$("#arrivalTimeWorkingLateAppr").text("");
		$("#reasonWorkingLateAppr").val("");
		$("#delayTimeWorkingLateAppr").val(0);
		$("#emplWorkingLateIdAppr").val("");
	};
	
	return{
		init: init
	}
}());

$(document).ready(function () {
	viewEmplWorkingLateObject.init();
});

