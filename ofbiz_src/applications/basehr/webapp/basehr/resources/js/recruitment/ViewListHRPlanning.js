var tmpS;
var hrPlanningListObj = (function(){
	var init = function(){
		initInput();
		initJqxDropDownList();
		initEvent();
		initJqxNotification();
		initJqxTreeButton();
		if(globalVar.createContextMenu){
			initJqxContextMenu();
		}
		var item = $("#jqxTree").jqxTree('getSelectedItem');
		var partyId = item.value;
		var date = new Date();
		emplPositionTypeId=$("#emplPositionTypeDropDown").val();
		refreshGrid($("#yearCustomTimePeriod").val(), partyId, emplPositionTypeId);
	};
	
	var initJqxContextMenu = function(){
		createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var cellSelected = $("#jqxgrid").jqxGrid('getselectedcell');
			var rowindex = cellSelected.rowindex;
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'approver'){
            	approvePlanningObj.openWindow();//approvePlanningObj is defined in ApproveHRPlanning.js
            	approvePlanningObj.setData(dataRecord);
            }else if(action == "edit"){
            	editHRPlanningObj.openWindow();//editHRPlanningObj is defined in RecruitmentEditHRPlanning.js
            	editHRPlanningObj.setData(dataRecord);
            }
		});
	};
	var initInput = function(){
		var date = new Date();
		$("#yearCustomTimePeriod").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, decimal: date.getFullYear()});
		$("#yearCustomTimeNew").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, decimal: date.getFullYear()});

	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#emplPositionTypeDropDown"), "emplPositionTypeId", "description",
				25, '100%', null, uiLabelMap.ChooseEmplPositionType);
		if(globalVar.hasOwnProperty("selectYearCustomTimePeriodId")){
		}
	};
	var initJqxNotification = function(){
		$("#jqxNotificationNtf").jqxNotification({width: '100%', autoClose : true, template : 'info', appendContainer : "#containerNtf", opacity : 0.9});
	};
	
	var initEvent = function(){
		$("#yearCustomTimePeriod").on('valueChanged', function(event){
				var item = $("#jqxTree").jqxTree('getSelectedItem');
				if(item){
					var partyId = item.value;
					refreshGrid($("#yearCustomTimePeriod").val(), partyId, emplPositionTypeId);
				}
		});
		$("#emplPositionTypeDropDown").on('select', function(event){
			var args = event.args;
			if(args){
				var emplPositionTypeId = args.item.value;
				var item = $("#jqxTree").jqxTree('getSelectedItem');
				if(item){
					var partyId = item.value;
					refreshGrid($("#yearCustomTimePeriod").val(), partyId, emplPositionTypeId);
				}
			}
		});
		$("#clearFilter").click(function(event){
			$("#emplPositionTypeDropDown").jqxDropDownList('clearSelection');
			var item = $("#jqxTree").jqxTree('getSelectedItem');
			if(item){
				var partyId = item.value;
				refreshGrid($("#yearCustomTimePeriod").val(), partyId, "");
			}
		});
		$("#addNewHRPlanBtn").click(function(event){
			addHRPlanningObj.openWindow();
		});
		$("#jqxgrid").on("cellclick", function (event) {
			var args = event.args;
			if(args.rightclick) {
				var rowBoundIndex = args.rowindex;
			    var dataField = args.datafield;
			    $('#jqxgrid').jqxGrid('selectcell', rowBoundIndex, dataField);
			}
		});
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 270, treeWidth: 270};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var year = $("#yearCustomTimePeriod").val();
			if(typeof(year) != 'undefined' && year.length > 0){
				refreshGrid(year, partyId, $("#emplPositionTypeDropDown").val());
			}
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var refreshGrid = function(year, partyId, emplPositionTypeId){
		var customTimePeriodId="L";
		for(var i=0;i<globalVar.customTimePeriodArr.length;i++)
		{
			if(globalVar.customTimePeriodArr[i].periodName.includes(year))
			{
				customTimePeriodId=globalVar.customTimePeriodArr[i].customTimePeriodId;
				break;
			}
		}
		refreshBeforeReloadGrid($("#jqxgrid"));
		tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQGetHRPlanning&hasrequest=Y&customTimePeriodId=" + customTimePeriodId + "&partyId=" + partyId + "&emplPositionTypeId=" + emplPositionTypeId;
		$("#jqxgrid").jqxGrid('source', tmpS);
	};
	
	var updateGrid = function(){
		$("#jqxgrid").jqxGrid('updatebounddata');
	};
	
	var createJqxTooltip = function(id, statusId){
		var statusDes = "";
		for(var i = 0; i < globalVar.statusArr.length; i++){
			if(globalVar.statusArr[i].statusId == statusId){
				statusDes = globalVar.statusArr[i].description;
				break;
			}
		}
		$("#" + id).jqxTooltip({ content: '<b>' + statusDes + '</b>', position: 'mouse', name: 'movieTooltip'});
	};
	
	return{
		init: init,
		updateGrid: updateGrid,
		createJqxTooltip: createJqxTooltip
	}
}());

var addHRPlanningObj = (function(){
	var updateGrid = false;
	var init = function(){
		initJqxDropDownList();
		initJqxTreeButton();
		initJqxInput();
		initDropDownGrid();
		initJqxValidator();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerAjaxAddNew"));
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#emplPositionTypeAddNew"), "emplPositionTypeId", "description", 25, '99%');
	};
	
	var initDropDownGrid = function(){
		$("#approverListDropDownBtn").jqxDropDownButton({width: '95%', height: 25});
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'partyName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'}];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyId', width : '23%', editable: false, hidden: true,},
		               {text: uiLabelMap.EmployeeId, datafield : 'partyCode', width : '23%', editable: false},
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
		Grid.initGrid(config, datafield, columns, null, $("#jqxGridApprover"));
	};
	
	var initEvent = function(){
		$("#jqxGridApprover").on('rowselect', function (event) {
            var args = event.args;
            var row = $("#jqxGridApprover").jqxGrid('getrowdata', args.rowindex);
            var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyName'] + '</div>';
            $("#approverListDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
        });
		
		$("#yearCustomTimeNew").on('valueChanged', function (event) {
			for (var i = 1; i <= 12; i ++){
				$("#month" + i).jqxInput({disabled: false});
			}
            checkPeriodMonth();
        });
		
		$("#alterCancel").click(function(event){
			$("#addHRPlaningWindow").jqxWindow('close');
		});
		$("#alterSave").click(function(event){
			var valid = $("#addHRPlaningWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreateHRPlanning,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createHRPlanning(true);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		$("#saveAndContinue").click(function(event){
			var valid = $("#addHRPlaningWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreateHRPlanning,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createHRPlanning(false);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	
	var createHRPlanning = function(isCloseWindow){
		var dataSubmit = {};
		var approverRowIndex = $("#jqxGridApprover").jqxGrid('getselectedrowindex');
		var data = $("#jqxGridApprover").jqxGrid('getrowdata', approverRowIndex);
		dataSubmit.emplPositionTypeId = $("#emplPositionTypeAddNew").val();
		dataSubmit.customTimePeriodId = "L";
		for(var i=0;i<globalVar.customTimePeriodArr.length;i++)
		{
			if(globalVar.customTimePeriodArr[i].periodName.includes($("#yearCustomTimeNew").val()))
			{
				dataSubmit.customTimePeriodId =globalVar.customTimePeriodArr[i].customTimePeriodId;
				break;
			}
		}
		dataSubmit.partyId = data.partyId;
		dataSubmit.year = $("#yearCustomTimeNew").val();
		var inputValid = false;
		for(var i = 1; i <= 12; i++){
			if($("#month" + i).val() != undefined && $("#month" + i).val() != null){
				inputValid = true;
				dataSubmit["month" + i] = $("#month" + i).val(); 
			}
		}
		if(!inputValid){
			bootbox.dialog(uiLabelMap.NotInputHRPlanning,
					[{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]
			);
			return;
		};
		$("#ajaxLoadingAddNew").show();
		disabledBtn();
		$.ajax({
			url: 'createHRPlanning',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					var dataCount = $("#jqxgrid").jqxGrid('getrows');
					var len=dataCount.length;
					if(tmpS.cachedrecords.length>0) {
						updateGrid = true;
					}
					if(tmpS.cachedrecords.length==0)
					{
						location.reload();
					}
					if(isCloseWindow){
						$("#addHRPlaningWindow").jqxWindow('close');
					}
					$('#containerNtf').empty();
					$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#notificationContentNtf").text(response.successMessage);
					$("#jqxNotificationNtf").jqxNotification('open');
				}else{
					var mess = null;
					if (response.errorMessage != null && response.errorMessage != undefined && response.errorMessage != ''){
						mess = response.errorMessage;
					} else {
						mess = uiLabelMap.HasErrorWhenProcess;
					}
					bootbox.dialog(mess,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoadingAddNew").hide();
				enableBtn();
			}
		});
	};
	
	var disabledBtn = function(){
		$("#alterCancel").attr("disabled", "disabled");
		$("#alterSave").attr("disabled", "disabled");
		$("#saveAndContinue").attr("disabled", "disabled");
	};
	
	var enableBtn = function(){
		$("#alterCancel").removeAttr("disabled");
		$("#alterSave").removeAttr("disabled");
		$("#saveAndContinue").removeAttr("disabled");
	}
	
	var initJqxValidator = function(){
		$("#addHRPlaningWindow").jqxValidator({
			rules: [
				{input : '#emplPositionTypeAddNew', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
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
	
	var initJqxInput = function(){
		for(var i = 1; i <=12; i++){
			$("#month" + i).jqxInput({width: '98%', height: 20, rtl : true});
		}
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 200, treeWidth: 200};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"), globalVar.rootPartyArr, "treeNew", "treeChildNew", config);
		$('#jqxTreeAddNew').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTreeAddNew').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
			var partyId = item.value;
			var customTimePeriod = $("#yearCustomTimeNew").val();
			$("#emplPositionTypeAddNew").jqxDropDownList({disabled: true});
			$.ajax({
				url: 'getListAllEmplPositionTypeOfParty',
				data: {partyId: partyId, customTimePeriod: customTimePeriod},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						updateSourceDropdownlist($("#emplPositionTypeAddNew"), response.listReturn);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#emplPositionTypeAddNew").jqxDropDownList({disabled: false});
				}
			});
			
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addHRPlaningWindow"), 660, 450);
		$("#addHRPlaningWindow").on('open', function(event){
			checkPeriodMonth();
			if(globalVar.rootPartyArr.length > 0){
				$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeNew")[0]);
			}
			if(globalVar.hasOwnProperty("selectYearCustomTimePeriodId")){
				$("#yearCustomTimeNew").val($("#yearCustomTimePeriod").val());
			}
			updateGrid = false;
		});
		$("#addHRPlaningWindow").on('close', function(event){
			clearDropDownContent($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
			$("#jqxGridApprover").jqxGrid('clearselection');
			Grid.clearForm($(this));
			if(updateGrid){
				hrPlanningListObj.updateGrid();
			}
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#addHRPlaningWindow"));
	};
	
	function checkPeriodMonth(){
		for(var i = 1; i <=12; i++){
			$("#month" + i).jqxInput('val', 0);
		}
		var year = $("#yearCustomTimeNew").val();
		var fromDate = null;
		var thruDate = null;
		var now = new Date();
		var curYear = now.getFullYear();
		var curMonth = now.getMonth() + 1;
		if (year == curYear){
			for (var i = 1; i < curMonth; i ++){
					$("#month" + i).jqxInput({disabled: true});
			}
		}
		if (year < curYear){
			for (var i = 1; i <= 12; i++){
				$("#month" + i).jqxInput({disabled: true});
			}
		}
	}
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	hrPlanningListObj.init();
	addHRPlanningObj.init();
});