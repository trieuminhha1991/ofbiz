var generalInfoObj = (function(){
	var init = function(){
		initJqxDateTimeInput();
		initJqxTreeButton();
		initJqxDropDown();
		initJqxNumberInput();
		initJqxInput();
		initEvent();
		initJqxValidator();
	};
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 430, treeWidth: 430};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$('#jqxTreeAddNew').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTreeAddNew').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
			var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
			if(fromDate && thruDate){
				updateEmplTimesheetDetailName(fromDate, thruDate, item.label);
			}
		});
	};
	var initJqxNumberInput = function(){
		$("#monthTS").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 1, max: 12});
		$("#yearTS").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 0, inputMode: 'simple', min: 1});
	};
	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({ width: '98%', height: '25px' });
		$("#thruDate").jqxDateTimeInput({ width: '98%', height: '25px' });
	};
	var initJqxInput = function(){
		$("#timekeepingDetailName").jqxInput({width: '97%', height: 20});
	};
	var initJqxDropDown = function(){
		createJqxDropDownList(globalVar.enumArr, $("#timesheetDetailEnumId"), "enumId", "description", 25, '98%');
		createJqxDropDownList(globalVar.workingShiftArr, $("#workingShiftDropDown"), "workingShiftId", "workingShiftName", 25, '98%');
	};
	var initEvent = function(){
		$("#monthTS").on('valueChanged', function(event){
			var month = event.args.value;
			var year = $("#yearTS").val();  
			updateDateTimeInput(month - 1, year);
		});
		$("#yearTS").on('valueChanged', function(event){
			var year = event.args.value;
			var month = $("#monthTS").val();
			updateDateTimeInput(month - 1, year);
		});
		$("#fromDate").on('valueChanged', function (event){
			var fromDate = event.args.date; 
			var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
			var partySelectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			updateEmplTimesheetDetailName(fromDate, thruDate, partySelectedItem.label);
		});
		$("#thruDate").on('valueChanged', function (event){
			var thruDate = event.args.date;
			var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			var partySelectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			updateEmplTimesheetDetailName(fromDate, thruDate, partySelectedItem.label);
		});
	};
	var updateEmplTimesheetDetailName = function(fromDate, thruDate, orgName){
		if(fromDate && thruDate && orgName){
			var fromDateDes = getDate(fromDate) + "/" + getMonth(fromDate) + "/" + fromDate.getFullYear();
			var thruDateDes = getDate(thruDate) + "/" + getMonth(thruDate) + "/" + thruDate.getFullYear() ;
			var text = uiLabelMap.EmplTimesheetList + " " + uiLabelMap.CommonFromLowercase + " " + fromDateDes + " " 
						+ uiLabelMap.CommonToLowercase + " " + thruDateDes + " - " + orgName;
			$("#timekeepingDetailName").val(text);
		}
	};
	var updateDateTimeInput = function(month, year){
		var startMonth = new Date(year, month, 1);
		var endMonth = new Date(year, month + 1, 0);
		$("#fromDate").val(startMonth);
		$("#thruDate").val(endMonth);
	};
	var onWindowOpen = function(){
		var nowDate = new Date();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
		$("#monthTS").val(nowDate.getMonth() + 1);
		$("#yearTS").val(nowDate.getFullYear());
		$("#workingShiftDropDown").jqxDropDownList({selectedIndex: 0});
	};
	var resetData = function(){
		Grid.clearForm($("#generalInfo"));
		$("#jqxTreeAddNew").jqxTree('selectItem', null);
	};
	var initJqxValidator = function(){
		$("#generalInfo").jqxValidator({
			rules: [
				{input : '#dropDownButtonAddNew', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var selectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
						if(!selectedItem){
							return false;
						}
						return true;
					}
				},  	
				{input : '#timekeepingDetailName', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  	
				{input : '#timesheetDetailEnumId', message : uiLabelMap.FieldRequired, action: 'blur', 
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
				{input : '#workingShiftDropDown', message : uiLabelMap.FieldRequired, action: 'blur', 
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
	var getData = function(){
		var data = {};
		var partySelectedItem = $("#jqxTreeAddNew").jqxTree("getSelectedItem");
		data.partyId = partySelectedItem.value;
		data.timesheetDetailEnumId = $("#timesheetDetailEnumId").val();
		data.month = $("#monthTS").val() - 1;
		data.year = $("#yearTS").val();
		data.fromDate = $("#fromDate").jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $("#thruDate").jqxDateTimeInput('val', 'date').getTime();
		data.timekeepingDetailName = $("#timekeepingDetailName").val();
		data.workingShiftId = $("#workingShiftDropDown").val();
		return data;
	};
	var validate = function(){
		return $("#generalInfo").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#generalInfo").jqxValidator('hide');
	};
	return{
		init: init,
		onWindowOpen: onWindowOpen,
		resetData: resetData,
		hideValidate: hideValidate,
		validate: validate,
		getData: getData
	}
}());