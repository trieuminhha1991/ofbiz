var editHRPlanningObj = (function(){
	var updateGrid = false;
	var _emplPositionTypeId = null;
	var init = function(){
		initJqxDropDownList();
		initJqxInput();
		initJqxNumberInput();
		initEvent();
		initJqxWindow();
		initJqxValidator();
		initDropDownGrid();
		create_spinner($("#spinnerAjaxEdit"));
	};
	var initDropDownGrid = function(){
		$("#editListDropDownBtn").jqxDropDownButton({width: '97%', height: 25});
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'}];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyId', width : '23%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'partyName', width: '30%', editable: false},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '47%', editable: false}];
		var config = {
		   		width: 500, 
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: true,
		   		showfilterrow: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
		        url: 'JQGetListHRMAdminAuthorization',    
	   			showtoolbar: false,
	        	source: {pagesize: 5, id: 'partyId'}
		 };
		Grid.initGrid(config, datafield, columns, null, $("#jqxGridEdit"));
	};
	var initJqxWindow =  function(){
		var initContent = function(){
		};
		createJqxWindow($("#editHRPlanningWindow"), 520, 300, initContent);
		$("#editHRPlanningWindow").on('close', function(event){
			_emplPositionTypeId = null;
			Grid.clearForm($(this));
			if(updateGrid){
				hrPlanningListObj.updateGrid();//hrPlanningListObj is defined in ViewListHRPlanning.js
			}
		});
		$("#editHRPlanningWindow").on('open', function(event){
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
			$("#yearCustomTimeEdit").val(yearCustomTimePeriodId);
			$("#jqxGridEdit").jqxGrid('clearselection');
			$("#ajaxLoadingEdit").show();
			disableBtn();
			$.ajax({
				url: 'getCustomTimePeriodByParent',
				data: {parentPeriodId: yearCustomTimePeriodId},
				type: 'POST',
				success: function(response){

					if(response.listCustomTimePeriod){
						var currentTime = new Date();
						var listCustomTimePeriod=response.listCustomTimePeriod;
						var sourceMonth=[];
						for(var i=0;i<listCustomTimePeriod.length ;i++)
						{
							if(listCustomTimePeriod[i].thruDate>currentTime)
							{
								sourceMonth.push(listCustomTimePeriod[i]);
							}
						}
						updateSourceDropdownlist($("#monthCustomTimeEdit"), sourceMonth);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#ajaxLoadingEdit").hide();
					enableBtn();
				}
			});
			updateGrid = false;
		});
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#monthCustomTimeEdit"), "customTimePeriodId", "periodName", 25, '95%');
		createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimeEdit"), "customTimePeriodId", "periodName", 25, '94%');
		$("#yearCustomTimeEdit").jqxDropDownList({disabled: true});
		$("#monthCustomTimeEdit").on('select', function(event){
			var args = event.args;
			if (args) {
				var item = args.item;
				var value = item.value;
				$("#ajaxLoadingEdit").show();
				disableBtn();
				$("#jqxGridEdit").jqxGrid('clearselection');
				$("#editListDropDownBtn").jqxDropDownButton('setContent', "");
				$.ajax({
					url: 'getHRPlanningByCustomTimePeriod',
					data: {customTimePeriodId: value, emplPositionTypeId: _emplPositionTypeId},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							if(response.statusId){
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(response.statusId == globalVar.statusArr[i].statusId){
										$("#statusPlanningEdit").jqxInput('val', {label: globalVar.statusArr[i].description, value: response.statusId});
									}
								}
							}else{
								$("#statusPlanningEdit").jqxInput('val', {label: uiLabelMap.HRNotCreated, value: ""});
							}
							if(response.quantity){
								$("#quantityEdit").val(response.quantity);
							}else{
								$("#quantityEdit").val(null);
							}
							if(response.approvedPartyId){
								var index = $('#jqxGridEdit').jqxGrid('getrowboundindexbyid', response.approvedPartyId);
								$('#jqxGridEdit').jqxGrid('selectrow', index);
							}
						}else{
							bootbox.dialog(response.errorMessage,
									[{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]
							);
						}
					},
					complete: function(jqXHR, textStatus){
						$("#ajaxLoadingEdit").hide();
						enableBtn();
					}
				}); 
			}
		});
	};
	var initJqxValidator = function(){
		$("#editHRPlanningWindow").jqxValidator({
			rules:[
				{input : '#emplPositionTypeIdEdit', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},      	
				{input : '#monthCustomTimeEdit', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},      	
				{input : '#yearCustomTimeEdit', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				}, 
				{input : '#quantityEdit', message : uiLabelMap.ValueMustBeGreateThanZero, action: 'blur', 
					rule : function(input, commit){
						if(!input.val() || input.val() < 1){
							return false;
						}
						return true;
					}
				}, 
				{input : '#editListDropDownBtn', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var selectedIndex = $("#jqxGridEdit").jqxGrid('getselectedrowindex');
						if(selectedIndex < 0){
							return false;
						}
						return true;
					}
				},
	        ]
		});
	};
	
	var validate = function(){
		return $("#editHRPlanningWindow").jqxValidator('validate');
	};
	var initJqxNumberInput = function(){
		$("#quantityEdit").jqxNumberInput({ width: '97%', height: '25px', spinButtons: false, decimalDigits: 0, inputMode: 'simple'});
	};
	var initJqxInput = function(){
		$("#emplPositionTypeIdEdit").jqxInput({width: '96%', height: 20, disabled: true, valueMember: 'emplPositionTypeId', displayMember: 'description'});
		$("#statusPlanningEdit").jqxInput({width: '96%', height: 20, disabled: true, valueMember: 'statusId', displayMember: 'description'});
	};
	
	var openWindow = function(){
		openJqxWindow($("#editHRPlanningWindow"));
	};
	var setData = function(data){
		if(data.hasOwnProperty("emplPositionTypeId")){
			_emplPositionTypeId = data.emplPositionTypeId;
			for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
				if(globalVar.emplPositionTypeArr[i].emplPositionTypeId == data.emplPositionTypeId){
					$("#emplPositionTypeIdEdit").jqxInput('val', {label: globalVar.emplPositionTypeArr[i].description, value: data.emplPositionTypeId});
					break;
				}
			}
		}
	};
	var initEvent = function(){
		$("#jqxGridEdit").on('rowselect', function (event) {
            var args = event.args;
            var row = $("#jqxGridEdit").jqxGrid('getrowdata', args.rowindex);
            var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyName'] + '</div>';
            $("#editListDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
        });
		$("#alterCancelEdit").click(function(event){
			$("#editHRPlanningWindow").jqxWindow('close');
		});
		$("#alterSaveEdit").click(function(event){
			if(!validate()){
				return;
			}
			editHRPlanning(true);
		});
		$("#saveAndContinueEdit").click(function(event){
			if(!validate()){
				return;
			}
			editHRPlanning(false);
		});
	};
	var editHRPlanning = function(isCloseWindow){
		$("#ajaxLoadingEdit").show();
		disableBtn();
		var approverRowIndex = $("#jqxGridEdit").jqxGrid('getselectedrowindex');
		var data = $("#jqxGridEdit").jqxGrid('getrowdata', approverRowIndex);
		var dataSubmit = {};
		dataSubmit.emplPositionTypeId = _emplPositionTypeId;
		dataSubmit.customTimePeriodId = $("#monthCustomTimeEdit").val();
		dataSubmit.quantity = $("#quantityEdit").val();
		dataSubmit.approvedPartyId = data.partyId;
		$.ajax({
			url: 'editHRPlanning',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					updateGrid = true;
					if(isCloseWindow){
						$("#editHRPlanningWindow").jqxWindow('close');
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
				$("#ajaxLoadingEdit").hide();
				enableBtn();
			}
		});
	};
	var disableBtn = function(){
		$("#alterCancelEdit").attr("disabled", "disabled");
		$("#alterSaveEdit").attr("disabled", "disabled");
		$("#saveAndContinueEdit").attr("disabled", "disabled");
	};
	
	var enableBtn = function(){
		$("#alterCancelEdit").removeAttr("disabled");
		$("#alterSaveEdit").removeAttr("disabled");
		$("#saveAndContinueEdit").removeAttr("disabled");
	};
	
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	editHRPlanningObj.init();
});