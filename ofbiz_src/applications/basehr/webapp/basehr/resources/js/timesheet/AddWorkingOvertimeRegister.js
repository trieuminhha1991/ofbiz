var addNewWorkingOvertimeObj = (function(){
	var _partyId = "";
	var init = function(){
		initJqxGridSearchEmpl();
		initJqxInput();
		initJqxDateTime();
		initDropDown();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinner"+ globalVar.addNewWindow));
	};
	var initJqxInput = function(){
		$("#partyId" + globalVar.addNewWindow).jqxInput({width: '86.5%', height: 20, disabled: true});
		$("#emplPosition" + globalVar.addNewWindow).jqxInput({width: '96%', height: 20, disabled: true});
		$("#groupName"+ globalVar.addNewWindow).jqxInput({width: '96%', height: 20, disabled: true});
	};
	var initJqxDateTime = function(){
		$("#fromDate" + globalVar.addNewWindow).jqxDateTimeInput({width: '100%', height: 25});
		$("#thruDate" + globalVar.addNewWindow).jqxDateTimeInput({width: '93%', height: 25});
		$("#dateRegistered" + globalVar.addNewWindow).jqxDateTimeInput({width: '97%', height: 25});
		$("#startTime" + globalVar.addNewWindow).jqxDateTimeInput({width: '100%', height: 25, formatString: 'HH:mm:ss', showTimeButton: true, showCalendarButton: false});
		$("#endTime" + globalVar.addNewWindow).jqxDateTimeInput({width: '93%', height: 25, formatString: 'HH:mm:ss', showTimeButton: true, showCalendarButton: false});
	};
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initDropDown = function(){
		var source = {
				localdata: globalVar.weekdayEnumArr,
	            datatype: "array"
		}
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#dayOfWeekAppl" + globalVar.addNewWindow).jqxComboBox({checkboxes: true, source: dataAdapter, displayMember: "description", 
	   		valueMember: "enumId", height: 25, width: '98%', theme: 'olbius', multiSelect: true});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			$("#reasonRegister" + globalVar.addNewWindow).jqxEditor({            
		        width: '98%',
		        theme: 'olbiuseditor',
		        tools: '',
		        height: 100,
		    });
		};
		createJqxWindow($("#" + globalVar.addNewWindow), 530, 500, initContent);
		createJqxWindow($('#popupWindowEmplList'), 900, 525, initJqxSplitter);
	};
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap});
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
		    $('#popupWindowEmplList').jqxWindow('close');
		    _partyId = data.partyId;
		    $("#partyId" + globalVar.addNewWindow).val(data.fullName);
		    $("#emplPosition" + globalVar.addNewWindow).val(data.emplPositionType);
		    $("#groupName" + globalVar.addNewWindow).val(data.department);
		});
	};
	var getData = function(){
		var data = {};
		data.partyId = _partyId;
		data.dateRegistered = $('#dateRegistered' + globalVar.addNewWindow).jqxDateTimeInput('val', 'date').getTime();
		data.fromDate = $('#fromDate' + globalVar.addNewWindow).jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $('#thruDate' + globalVar.addNewWindow).jqxDateTimeInput('val', 'date').getTime();
		data.startTime = $('#startTime' + globalVar.addNewWindow).jqxDateTimeInput('val', 'date').getTime();
		data.endTime = $('#endTime' + globalVar.addNewWindow).jqxDateTimeInput('val', 'date').getTime();
		data.reasonRegister = $("#reasonRegister" + globalVar.addNewWindow).jqxEditor('val');
		var checkedItem = $("#dayOfWeekAppl" + globalVar.addNewWindow).jqxComboBox('getCheckedItems');
		var enumIdArr = [];
		for(var i = 0; i < checkedItem.length; i++){
			enumIdArr.push(checkedItem[i].value);
		}
		if(enumIdArr.length > 0){
			data.enumIdList = JSON.stringify(enumIdArr);
		}
		return data;
	};
	var clearData = function(){
		Grid.clearForm($("#"+ globalVar.addNewWindow));
		_partyId = "";
		$("#dayOfWeekAppl" + globalVar.addNewWindow).jqxComboBox('uncheckAll');
	};
	var initData = function(){
		var date = new Date();
		$("#dateRegistered" + globalVar.addNewWindow).val(date);
		$("#fromDate" + globalVar.addNewWindow).val(null);
		$("#thruDate" + globalVar.addNewWindow).val(null);
		$("#startTime" + globalVar.addNewWindow).val(null);
		$("#endTime" + globalVar.addNewWindow).val(null);
		$("#dayOfWeekAppl" + globalVar.addNewWindow).jqxComboBox('checkItem', "_NA_");
	};
	var initEvent = function(){
		$("#searchPartyNewBtn").click(function(event){
			openJqxWindow($('#popupWindowEmplList'));
		});
		$('#popupWindowEmplList').on('open', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
		$("#"+ globalVar.addNewWindow).on('close', function(event){
			clearData();
		});
		$("#" + globalVar.addNewWindow).on('open', function(event){
			initData();
		});
		$("#dayOfWeekAppl" + globalVar.addNewWindow).on('checkChange', function (event){
			var args = event.args;
			var item = args.item;
			var value = item.value;
			var checked = item.checked;
			if(value == "_NA_"){
				if(checked){
					for(var i = 1; i < 8; i++){
						$("#dayOfWeekAppl" + globalVar.addNewWindow).jqxComboBox('uncheckIndex', i); 
						$("#dayOfWeekAppl" + globalVar.addNewWindow).jqxComboBox('disableAt', i); 
					}
				}else{
					for(var i = 1; i < 8; i++){
						$("#dayOfWeekAppl" + globalVar.addNewWindow).jqxComboBox('enableAt', i); 
					}
				}
			}
		});
		$("#cancel" + globalVar.addNewWindow).click(function(event){
			$("#"+ globalVar.addNewWindow).jqxWindow('close');
		});
		$("#save" + globalVar.addNewWindow).click(function(event){
			var valid = $("#" + globalVar.addNewWindow).jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmWorkingOvertimeRegister,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createWorkOvertimeRegistration(true);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			var data = getData();
		});
		$("#saveAndContinue" + globalVar.addNewWindow).click(function(event){
			var valid = $("#" + globalVar.addNewWindow).jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmWorkingOvertimeRegister,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createWorkOvertimeRegistration(false);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	var createWorkOvertimeRegistration = function(isCloseWindow){
		var data = getData();
		$("#loading" + globalVar.addNewWindow).show();
		$("#cancel" + globalVar.addNewWindow).attr("disabled", "disabled");
		$("#saveAndContinue" + globalVar.addNewWindow).attr("disabled", "disabled");
		$("#save" + globalVar.addNewWindow).attr("disabled", "disabled");
		$.ajax({
			url: 'createWorkOvertimeRegis',
			type: 'POST',
			data: data,
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					if(isCloseWindow){
						$("#" + globalVar.addNewWindow).jqxWindow('close');
					}else{
						clearData();
						initData();
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
				$("#loading" + globalVar.addNewWindow).hide();
				$("#cancel" + globalVar.addNewWindow).removeAttr("disabled");
				$("#saveAndContinue" + globalVar.addNewWindow).removeAttr("disabled");
				$("#save" + globalVar.addNewWindow).removeAttr("disabled");
			}
		});
	};
	var initJqxValidator = function(){
		$("#"+ globalVar.addNewWindow).jqxValidator({
			rules: [
				{ input: '#searchPartyNewBtn', message: uiLabelMap.FieldRequired, action: 'none',
					   rule: function (input, commit){
						   if(_partyId.length <= 0){
							  return false;
						   }
						   return true;
					   }
				},    
				{ input: '#dateRegistered' + globalVar.addNewWindow, message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},    
				{ input: '#fromDate' + globalVar.addNewWindow, message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},    
				{ input: '#thruDate' + globalVar.addNewWindow, message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},    
				{ input: '#startTime' + globalVar.addNewWindow, message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},    
				{ input: '#endTime' + globalVar.addNewWindow, message: uiLabelMap.FieldRequired, action: 'none',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{ input: '#thruDate' + globalVar.addNewWindow, message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'none',
					rule: function (input, commit){
						var fromDate = $('#fromDate' + globalVar.addNewWindow).jqxDateTimeInput('val', 'date');
						var thruDate = $(input).jqxDateTimeInput('val', 'date');
						if(fromDate && thruDate && thruDate < fromDate){
							return false;
						}
						return true;
					}
				},  
			]
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	addNewWorkingOvertimeObj.init();
});