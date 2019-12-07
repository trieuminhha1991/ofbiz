var addTimekeepingSummaryObj = (function(){
	var _windowWidth = 550;
	var init = function(){
		initJqxDateTimeInput();
		initJqxTreeButton();
		initJqxNumberInput();
		initJqxInput();
		initGrid();
		initDropDown();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerCreateNew"));
	};
	var initJqxTreeButton = function(){
		var treeWidth = _windowWidth * 0.62;
		var config = {dropDownBtnWidth: treeWidth, treeWidth: treeWidth};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$('#jqxTreeAddNew').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTreeAddNew').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
			var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
			if(fromDate && thruDate){
				updateTimekeepingSummaryName(fromDate, thruDate, item.label);
			}
			var month = $("#monthTS").val();
			var year = $("#yearTS").val();
			if(year > 0){
				updateTimekeepingDetailGrid(month - 1, year, item.value);
			}
		});
	};
	var initJqxNumberInput = function(){
		$("#monthTS").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 1, max: 12});
		$("#yearTS").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 1});
	};
	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({ width: '100%', height: '25px' });
		$("#thruDate").jqxDateTimeInput({ width: '96%', height: '25px' });
	};
	var initJqxInput = function(){
		$("#timekeepingSummaryName").jqxInput({width: '97%', height: 20});
	};
	var updateTimekeepingSummaryName = function(fromDate, thruDate, orgName){
		if(fromDate && thruDate && orgName){
			var fromDateDes = getDate(fromDate) + "/" + getMonth(fromDate) + "/" + fromDate.getFullYear();
			var thruDateDes = getDate(thruDate) + "/" + getMonth(thruDate) + "/" + thruDate.getFullYear() ;
			var text = uiLabelMap.EmplTimesheetList + " " + uiLabelMap.CommonFromLowercase + " " + fromDateDes + " " 
						+ uiLabelMap.CommonToLowercase + " " + thruDateDes + " - " + orgName;
			$("#timekeepingSummaryName").val(text);
		}
	};
	var initJqxWindow = function(){
		createJqxWindow($("#AddTimekeepingSummaryWindow"), _windowWidth, 500);
	};
	var onWindowOpen = function(){
		var nowDate = new Date();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
		$("#monthTS").val(nowDate.getMonth() + 1);
		$("#yearTS").val(nowDate.getFullYear());
	};
	var initEvent = function(){
		$("#AddTimekeepingSummaryWindow").on('open', function(event){
			onWindowOpen();
		});
		$("#AddTimekeepingSummaryWindow").on('close', function(event){
			Grid.clearForm($("#AddTimekeepingSummaryWindow"));
			$("#jqxTreeAddNew").jqxTree('selectItem', null);
			$("#AddTimekeepingSummaryWindow").jqxValidator('hide');
			updateLocalDataGrid($("#timekeepingDetailGrid"), []);
		});
		
		$("#monthTS").on('valueChanged', function(event){
			var month = event.args.value;
			var year = $("#yearTS").val();
			var selectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			updateDateTimeInput(month - 1, year);
			if(year > 0 && selectedItem){
				updateTimekeepingDetailGrid(month - 1, year, selectedItem.value);
			}
		});
		$("#yearTS").on('valueChanged', function(event){
			var year = event.args.value;
			var month = $("#monthTS").val();
			updateDateTimeInput(month - 1, year);
			var selectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			if(year > 0 && selectedItem){
				updateTimekeepingDetailGrid(month - 1, year, selectedItem.value);
			}
		});
		$("#fromDate").on('valueChanged', function (event){
			var fromDate = event.args.date; 
			var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
			var partySelectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			updateTimekeepingSummaryName(fromDate, thruDate, partySelectedItem.label);
		});
		$("#thruDate").on('valueChanged', function (event){
			var thruDate = event.args.date;
			var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			var partySelectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			updateTimekeepingSummaryName(fromDate, thruDate, partySelectedItem.label);
		});
		$("#cancelAdd").click(function(event){
			$("#AddTimekeepingSummaryWindow").jqxWindow('close');
		});
		$("#saveAdd").click(function(event){
			var valid = $("#AddTimekeepingSummaryWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var isSelected = false;
			var rows = $("#timekeepingDetailGrid").jqxGrid('getrows');
			var totalRow = rows.length;
			for(var i = 0; i < totalRow; i++){
				isSelected = rows[i].isSelected;
				if(isSelected){
					break;
				}
			}
			if(!isSelected){
				var message = "";
				if(totalRow > 0){
					message = uiLabelMap.TimekeepingDetailIsNotSelected;
				}else{
					message = uiLabelMap.TimekeepingDetailIsNotCreated;
				}
				bootbox.dialog(message,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreateEmplTimesheetSummary,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createEmplTimesheetSummary();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	var getData = function(){
		var data = {};
		var partySelectedItem = $("#jqxTreeAddNew").jqxTree("getSelectedItem");
		data.partyId = partySelectedItem.value;
		data.month = $("#monthTS").val() - 1;
		data.year = $("#yearTS").val();
		data.fromDate = $("#fromDate").jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $("#thruDate").jqxDateTimeInput('val', 'date').getTime();
		data.timekeepingSummaryName = $("#timekeepingSummaryName").val();
		var rows = $("#timekeepingDetailGrid").jqxGrid('getrows');
		var timekeepingDetailArr = [];
		for(var i = 0; i < rows.length; i++){
			var rowData = rows[i];
			if(rowData.isSelected){
				timekeepingDetailArr.push(rowData.timekeepingDetailId);
			}
		}
		data.timekeepingDetailId = JSON.stringify(timekeepingDetailArr);
		return data;
	};
	var createEmplTimesheetSummary = function(){
		var data = getData();
		$("#loadingCreateNew").show();
		disableAll();
		$.ajax({
			url: 'createEmplTimesheetSummary',
			data: data,
			type: 'POST', 
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
    				$("#AddTimekeepingSummaryWindow").jqxWindow('close');
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
				$("#loadingCreateNew").hide();
				enableAll();
			}
		});
	};
	var disableAll = function(){
		$("#timekeepingDetailGrid").jqxGrid({disabled: true});
		$("#dropDownButtonAddNew").jqxDropDownButton({disabled: true});
		$("#monthTS").jqxNumberInput({disabled: true});
		$("#yearTS").jqxNumberInput({disabled: true});
		$("#fromDate").jqxDateTimeInput({disabled: true});
		$("#thruDate").jqxDateTimeInput({disabled: true});
		$("#timekeepingSummaryName").jqxInput({disabled: true});
		$("#cancelAdd").attr("disabled", "disabled");
		$("#saveAdd").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#timekeepingDetailGrid").jqxGrid({disabled: false});
		$("#dropDownButtonAddNew").jqxDropDownButton({disabled: false});
		$("#monthTS").jqxNumberInput({disabled: false});
		$("#yearTS").jqxNumberInput({disabled: false});
		$("#fromDate").jqxDateTimeInput({disabled: false});
		$("#thruDate").jqxDateTimeInput({disabled: false});
		$("#timekeepingSummaryName").jqxInput({disabled: false});
		$("#cancelAdd").removeAttr("disabled");
		$("#saveAdd").removeAttr("disabled");
	};
	var updateTimekeepingDetailGrid = function(month, year, partyId){
		$("#timekeepingDetailGrid").jqxGrid('showloadelement');
		$("#timekeepingDetailGrid").jqxGrid({disabled: true});
		var localdata = [];
		$.ajax({
			url: "getTimekeepingDetailInMonthYear",
			data: {month: month, year: year, partyId: partyId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					var listReturn = response.listReturn;
					for(var i = 0; i < listReturn.length; i++){
						localdata.push({isSelected: false, timekeepingDetailId: listReturn[i].timekeepingDetailId, timekeepingDetailName: listReturn[i].timekeepingDetailName}); 
					}
				}
			},
			complete: function(jqXHR, textStatus){
				$("#timekeepingDetailGrid").jqxGrid('hideloadelement');
				$("#timekeepingDetailGrid").jqxGrid({disabled: false});
				updateLocalDataGrid($("#timekeepingDetailGrid"), localdata);
			}
		});
	};
	var updateLocalDataGrid = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var updateDateTimeInput = function(month, year){
		var startMonth = new Date(year, month, 1);
		var endMonth = new Date(year, month + 1, 0);
		$("#fromDate").val(startMonth);
		$("#thruDate").val(endMonth);
	};
	var initGrid = function(){
		var datafield = [{name: 'isSelected', type: 'bool'},
		                 {name: 'timekeepingDetailId', type: 'string'},
		                 {name: 'timekeepingDetailName', type: 'string'}
		                 ];
		var columns = [{datafield: 'timekeepingDetailId', hidden: true},
		               { text: '', datafield: 'isSelected', columntype: 'checkbox', width: 50, editable: true,
							cellbeginedit: function (row, datafield, columntype) {
								
							}
		               },
		               {text: uiLabelMap.TimekeepingDetailName, datafield: 'timekeepingDetailName', align: 'center', editable: false}
		               ];
		var grid = $("#timekeepingDetailGrid");
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "timekeepingDetailGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h6>" + uiLabelMap.SummaryFromTimekeepingDetail + "</h6><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '99%',
				virtualmode: false,
				editable: true,
				selectionmode: 'singlecell',
				localization: getLocalization(),
				pageable: false,
				theme: 'energyblue',
				source: {
					localdata: []
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initDropDown = function(){
		
	};
	var initJqxValidator = function(){
		$("#AddTimekeepingSummaryWindow").jqxValidator({
			rules: [
				{input : '#dropDownButtonAddNew', message: uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var selectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
						if(!selectedItem){
							return false;
						}
						return true;
					}
				},  	
				{input : '#timekeepingSummaryName', message: uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  	
				{input : '#monthTS', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  	
				{input : '#yearTS', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  	
				{input : '#fromDate', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  	
				{input : '#thruDate', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  	
				{input : '#fromDate', message : uiLabelMap.DateNotValid, action: 'blur', 
					rule : function(input, commit){
						var month = $("#monthTS").val();
						var year = $("#yearTS").val();
						var startMonth = new Date(year, month - 1 , 1);
						var endMonth = new Date(year, month, 0);
						if(input){
							var date = input.jqxDateTimeInput('val', 'date');
							if(date < startMonth || date > endMonth){
								return false;
							}
						}
						return true;
					}
				},  	
				{input : '#thruDate', message : uiLabelMap.DateNotValid, action: 'blur', 
					rule : function(input, commit){
						var month = $("#monthTS").val();
						var year = $("#yearTS").val();
						var startMonth = new Date(year, month - 1, 1);
						var endMonth = new Date(year, month, 0);
						if(input){
							var date = input.jqxDateTimeInput('val', 'date');
							if(date < startMonth || date > endMonth){
								return false;
							}
						}
						return true;
					}
				},  	
				{input : '#thruDate', message : uiLabelMap.ValueMustGreaterOrEqualThanFromDate, action: 'blur', 
					rule : function(input, commit){
						if(input){
							var thruDate = input.jqxDateTimeInput('val', 'date');
							var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
							if(thruDate < fromDate){
								return false;
							}
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

$(document).ready(function(){
	addTimekeepingSummaryObj.init();
});