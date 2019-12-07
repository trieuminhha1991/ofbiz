var approvePlanningObj = (function(){
	var planningObj = {};
	var updateGrid = false;
	var init = function(){
		initJqxDropDownList();
		initJqxRadioButton();
		initJqxInput();
		initJqxNumberInput();
		initEvent();
		initJqxWindow();
		initJqxValidator();
		create_spinner($("#spinnerAjaxApprove"));
	};
	
	var initJqxWindow =  function(){
		var initContent = function(){
		};
		createJqxWindow($("#approveHRPlanningWindow"), 520, 340, initContent);
		$("#approveHRPlanningWindow").on('close', function(event){
			planningObj = {};
			Grid.clearForm($(this));
			if(updateGrid){
				hrPlanningListObj.updateGrid();
			}
		});
		$("#approveHRPlanningWindow").on('open', function(event){
			var year= $("#yearCustomTimePeriod").val();
			var yearCustomTimePeriodId="";
			for(var i=0;i<globalVar.customTimePeriodArr.length;i++)
			{
				if(globalVar.customTimePeriodArr[i].periodName.includes(year))
				{
					yearCustomTimePeriodId=globalVar.customTimePeriodArr[i].customTimePeriodId;
					break;
				}
			}
			$("#yearCustomTimeApprove").val(yearCustomTimePeriodId);
			updateGrid = false;
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#monthCustomTimeApprove"), "customTimePeriodId", "periodName", 25, '95%');
		createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimeApprove"), "customTimePeriodId", "periodName", 25, '94%');
		$("#yearCustomTimeApprove").jqxDropDownList({disabled: true});
		$("#monthCustomTimeApprove").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var dataRecord = planningObj.planningData[index];
				if(dataRecord.comment){
					$("#commentApprove").val(dataRecord.comment);
				}
				for(var i = 0; i < globalVar.statusArr.length; i++){
					if(dataRecord.statusId == globalVar.statusArr[i].statusId){
						$("#statusPlanningApprove").jqxInput('val', {label: globalVar.statusArr[i].description, value: dataRecord.statusId});
					}
				}
				if(dataRecord.statusId == 'HR_PLANNING_ACC'){
					$("#acceptHRPlanning").jqxRadioButton({checked: true});
				}else if(dataRecord.statusId == 'HR_PLANNING_REJ'){
					$("#rejectHRPlanning").jqxRadioButton({checked: true});
				}else if(dataRecord.statusId == 'HR_PLANNING_CAN'){
					$("#cancelHRPlanning").jqxRadioButton({checked: true});
				}
				$("#quantityApprove").val(dataRecord.quantity);
			}
		});
	};
	
	var initJqxValidator = function(){
		$("#approveHRPlanningWindow").jqxValidator({
			rules:[
				{input : '#emplPositionTypeIdApprove', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},      	
				{input : '#monthCustomTimeApprove', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},      	
				{input : '#yearCustomTimeApprove', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},      	
				{input : '#cancelHRPlanning', message : uiLabelMap.PleaseSelectOption, action: 'blur', 
					rule : function(input, commit){
						var acceptHRPlanning = $("#acceptHRPlanning").jqxRadioButton('checked');
						var rejectHRPlanning = $("#rejectHRPlanning").jqxRadioButton('checked');
						var cancelHRPlanning = $("#cancelHRPlanning").jqxRadioButton('checked');
						if(!acceptHRPlanning && !rejectHRPlanning && !cancelHRPlanning){
							return false;
						}
						return true;
					}
				},
				{
					input : '#commentApprove', message : uiLabelMap.InvalidChar, action : 'change',
					rule : function(input, commit){
						var value = input.val();
						var regular =  /[~`!#@$%\^&*+=\\[\]\\{}|\\<>\?]/; 
						if(regular.test(value)){
							return false
						}
						return true;
					}
				}
	        ]
		});
	};
	
	var validate = function(){
		return $("#approveHRPlanningWindow").jqxValidator('validate');
	};
	
	var initJqxRadioButton = function(){
		$("#acceptHRPlanning").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#rejectHRPlanning").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#cancelHRPlanning").jqxRadioButton({ width: '94%', height: 25, checked: false});
	};
	
	var initJqxInput = function(){
		$("#emplPositionTypeIdApprove").jqxInput({width: '96%', height: 20, disabled: true, valueMember: 'emplPositionTypeId', displayMember: 'description'});
		$("#statusPlanningApprove").jqxInput({width: '96%', height: 20, disabled: true, valueMember: 'statusId', displayMember: 'description'});
		$("#commentApprove").jqxInput({width: '96%', height: 20, disabled: false});
	};
	var initJqxNumberInput = function(){
		$("#quantityApprove").jqxNumberInput({ width: '97%', height: '25px', spinButtons: false, decimalDigits: 0, inputMode: 'simple', disabled: true});
	};
	
	var openWindow = function(){
		openJqxWindow($("#approveHRPlanningWindow"));
	};
	
	var setData = function(data){
		if(data.hasOwnProperty("emplPositionTypeId")){
			var planningData = [];
			for(var i = 0; i < 12; i++){
				if(data.hasOwnProperty("customTimePeriodId_" + i)){
					var row = {
							emplPositionTypeId: data.emplPositionTypeId,
							customTimePeriodId: data["customTimePeriodId_" + i], 
							comment: data["comment_" + i], 
							statusId: data["statusId_" + i], 
							quantity: data["quantity_" + i], 
							statusId: data["statusId_" + i],
							periodName: globalVar.monthNames[i]
					};
					planningData.push(row);
				}
			}
			planningObj.emplPositionTypeId = data.emplPositionTypeId;
			planningObj.planningData = planningData;
			updateSourceDropdownlist($("#monthCustomTimeApprove"), planningObj.planningData);
			for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
				if(globalVar.emplPositionTypeArr[i].emplPositionTypeId == planningObj.emplPositionTypeId){
					$("#emplPositionTypeIdApprove").jqxInput('val', {label: globalVar.emplPositionTypeArr[i].description, value: planningObj.emplPositionTypeId});
					break;
				}
			}
		}
	};
	
	var initEvent = function(){
		$("#alterCancelApprove").click(function(event){
			$("#approveHRPlanningWindow").jqxWindow('close');
		});
		$("#alterSaveApprove").click(function(event){
			if(!validate()){
				return;
			}
			bootbox.dialog(uiLabelMap.ApproveHRPlanningConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							approveHRPlanning(true);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]
			);
		});
		$("#saveAndContinueApprove").click(function(event){
			if(!validate()){
				return;
			}
			bootbox.dialog(uiLabelMap.ApproveHRPlanningConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							approveHRPlanning(false);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]
			)
		});
	};
	
	var approveHRPlanning = function(isCloseWindow){
		$("#ajaxLoadingApprove").show();
		disableBtn();
		var dataSubmit = {};
		dataSubmit.emplPositionTypeId = planningObj.emplPositionTypeId;
		dataSubmit.customTimePeriodId = $("#monthCustomTimeApprove").val();
		var accept = $("#acceptHRPlanning").jqxRadioButton('checked');
		var reject = $("#rejectHRPlanning").jqxRadioButton('checked');
		var cancel = $("#cancelHRPlanning").jqxRadioButton('checked');
		if(accept){
			dataSubmit.statusId = "HR_PLANNING_ACC";
		}else if(reject){
			dataSubmit.statusId = "HR_PLANNING_REJ";
		}else if(cancel){
			dataSubmit.statusId = "HR_PLANNING_CAN";
		}
		if($("#commentApprove").val()){
			dataSubmit.comment = $("#commentApprove").val(); 
		}
		$.ajax({
			url: 'ApproveHRPlanning',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					updateGrid = true;
					if(isCloseWindow){
						$("#approveHRPlanningWindow").jqxWindow('close');
					}
					$('#containerNtf').empty();
					$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#notificationContentNtf").text(response._EVENT_MESSAGE_);
					$("#jqxNotificationNtf").jqxNotification('open');
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoadingApprove").hide();
				enableBtn();
			}
		});
	};
	
	var disableBtn = function(){
		$("#alterCancelApprove").attr("disabled", "disabled");
		$("#alterSaveApprove").attr("disabled", "disabled");
		$("#saveAndContinueApprove").attr("disabled", "disabled");
	};
	
	var enableBtn = function(){
		$("#alterCancelApprove").removeAttr("disabled");
		$("#alterSaveApprove").removeAttr("disabled");
		$("#saveAndContinueApprove").removeAttr("disabled");
	};
	
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	approvePlanningObj.init();
});