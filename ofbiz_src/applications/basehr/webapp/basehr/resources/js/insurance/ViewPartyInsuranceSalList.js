var viewPartyInsObject = (function(){
	var init = function(){
		initJqxDateTimeInput();
		initJqxTreeButton();
		initJqxNotification();
		initBtnEvent();
	};
	
	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};
	
	var initBtnEvent = function(){
		$("#configEmplBaseSalary").click(function(event){
			openJqxWindow($("#settingSalaryByPositionWindow"));
		});
		$("#addNew").click(function(event){
			openJqxWindow($("#partyInsuranceSalaryWindow"));
		});
	};
	
	var initJqxDateTimeInput = function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
		$("#dateTimeInput").on('change', function (event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var item = $('#jqxTree').jqxTree('getSelectedItem');
			if(item){
				refreshGridData(item.value, selection.from.getTime(), selection.to.getTime());
			}
		});
		$("#dateTimeInput").jqxDateTimeInput('setRange', new Date(globalVar.monthStart), new Date(globalVar.monthEnd));
	};
	
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 300, treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#dropDownButton"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTree"), $("#dropDownButton"));
			var partyId = item.value;
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			if(selection){
				var fromDate = selection.from.getTime();
				var thruDate = selection.to.getTime();
				refreshGridData(partyId, fromDate, thruDate);
			}
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
	};
	
	var refreshGridData = function(partyId, fromDate, thruDate){
		if(typeof(partyId) != 'undefined' && typeof(fromDate) != 'undefined' && typeof(thruDate) != 'undefined'){
			var tempS = $("#jqxgrid").jqxGrid('source');
			tempS._source.url = "jqxGeneralServicer?sname=JQListEmplInsuranceSalary&hasrequest=Y&partyId=" + partyId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
			$("#jqxgrid").jqxGrid('source', tempS);
		}
	};
	
	return{
		init: init,
	}
}());

var editPartyInsObject = (function(){
	var edit = false;
	var partyInsSalId;
	var init = function(){
		initJqxInput();
		initJqxGrid();
		initJqxNumberInput();
		initJqxDateTimeInput();
		initJqxDropDownList();
		initBtnEvent();
		initJqxValidator();
		initJqxGridEvent();
		initJqxWindow();
	};
	
	var initJqxGridEvent = function(){
		$('#jqxgrid').on('rowdoubleclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $('#jqxgrid').jqxGrid('getrowdata', boundIndex);
			edit = true;
			partyInsSalId = data.partyInsSalId;
			$("#partyIdNew").jqxInput('val', {value: data.partyId, label: data.fullName});
			$("#insuranceSalaryAmount").val(data.amount);
			$("#fromDate").val(data.fromDate);
			if(data.thruDate){
				$("#thruDate").val(data.thruDate);
			}else{
				$("#thruDate").val(null);
			}
			$("#periodTypeId").val(data.periodTypeId);
			openJqxWindow($("#partyInsuranceSalaryWindow"));
		});
	};
	
	var initBtnEvent = function(){
		$("#searchBtn").click(function(event){
			openJqxWindow($("#popupWindowEmplList"));
		});
		$("#btnCancel").click(function(event){
			$('#partyInsuranceSalaryWindow').jqxWindow('close');
		});
		$("#btnSave").click(function(event){
			if(!validate()){
				return;
			}
			$("#btnSave").attr("disabled", "disabled");
			if(!edit){
				bootbox.dialog(uiLabelMap.HrCreateNewConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 createPartyInsuranceSalary();	    		
								 $("#btnSave").removeAttr("disabled");
								 $('#partyInsuranceSalaryWindow').jqxWindow('close');
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger icon-remove btn-small",
							 "callback": function() {
								 $("#btnSave").removeAttr("disabled");
							 }
						 }
						 ]
				);		 	
			}else{
				bootbox.dialog(uiLabelMap.EditPartyInsuranceConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 updatePartyInsuranceSalary();	    		
								 $("#btnSave").removeAttr("disabled");
								 $('#partyInsuranceSalaryWindow').jqxWindow('close');
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger icon-remove btn-small",
							 "callback": function() {
								 $("#btnSave").removeAttr("disabled");
							 }
						 }
						 ]
				);
			}
		});
	};	
	
	var getData = function(){
		var data = {};
		var party = $("#partyIdNew").val();
		data.partyId = party.value;
		data.amount = $("#insuranceSalaryAmount").val();
		data.fromDate = $("#fromDate").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		data.periodTypeId = $("#periodTypeId").val();
		return data;
	};
	
	var updatePartyInsuranceSalary = function(){
		var data = getData();
		if(partyInsSalId){
			data.partyInsSalId = partyInsSalId;
			$.ajax({
				url: 'updatePartyInsuranceSalary',
				data: data,
				type: 'POST',
				success: function(response){
		    		$("#jqxNtf").jqxNotification('closeLast');
		    		if(response.responseMessage == "success"){
		    			$("#jqxNtfContent").text(response.successMessage);
						$("#jqxNtf").jqxNotification({ template: 'info' });
						$("#jqxNtf").jqxNotification('open');
						$("#jqxgrid").jqxGrid('updatebounddata');
		    		}else{
		    			$("#jqxNtfContent").text(response.errorMessage);
						$("#jqxNtf").jqxNotification({ template: 'error' });
						$("#jqxNtf").jqxNotification('open');
		    		}
		    	},
		    	complete: function(jqXHR, textStatus){
		    		$("#jqxgrid").jqxGrid('hideloadelement');
		    		$("#jqxgrid").jqxGrid({ disabled: false});
		    	}
			});
		}
	};
	
	var createPartyInsuranceSalary = function(){
		var data = getData();
		$("#jqxgrid").jqxGrid('showloadelement');
		$("#jqxgrid").jqxGrid({ disabled: true});
		$.ajax({
			url: 'createPartyInsuranceSalary',
			data: data,
			type: 'POST',
			success: function(response){
	    		$("#jqxNtf").jqxNotification('closeLast');
	    		if(response.responseMessage == "success"){
	    			$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({ template: 'info' });
					$("#jqxNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
	    		}else{
	    			$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({ template: 'error' });
					$("#jqxNtf").jqxNotification('open');
	    		}
	    	},
	    	complete: function(jqXHR, textStatus){
	    		$("#jqxgrid").jqxGrid('hideloadelement');
	    		$("#jqxgrid").jqxGrid({ disabled: false});
	    	}
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(periodTypeArr, $('#periodTypeId'), "periodTypeId", "description", 25, "98%");	
	};
	
	var initJqxNumberInput = function(){
		$("#insuranceSalaryAmount").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, decimalDigits: 0, digits: 10, max: 9999999999});
	};
	
	var initJqxInput = function(){
		$("#partyIdNew").jqxInput({width: '87%', height: 20, disabled: true, valueMember: 'partyId', displayMember:'fullName'});
	};
	
	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#thruDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	};
	
	var initJqxGrid = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap});
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
		    $('#popupWindowEmplList').jqxWindow('close');
		    $("#partyIdNew").jqxInput('val', {value: data.partyId, label: data.partyName});
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($('#partyInsuranceSalaryWindow'), 500, 300);
		$("#partyInsuranceSalaryWindow").on('close', function(event){
			edit = false;
			Grid.clearForm($(this));
		});
		$("#partyInsuranceSalaryWindow").on('open', function(event){
			if(!edit){
				var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
				$("#fromDate").val(selection.from);
				$("#thruDate").val(null);
				$("#partyInsuranceSalaryWindow").jqxWindow('setTitle', uiLabelMap.CommonAddNew);
			}else{
				$("#partyInsuranceSalaryWindow").jqxWindow('setTitle', uiLabelMap.EditPartyInsuranceSalary);
			}
		});
		
		$('#popupWindowEmplList').jqxWindow({
		    showCollapseButton: true, autoOpen: false, maxWidth: "80%", minWidth: "50%", maxHeight: 520, height: 520, width: "80%", isModal: true, 
		    theme:'olbius', collapsed:false,
		    initContent: function () {  
		    	initJqxSplitter();
		    }
		});
	};
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	
	var validate = function(){
		return $('#partyInsuranceSalaryWindow').jqxValidator('validate');
	}
	
	var initJqxValidator = function(){
		$('#partyInsuranceSalaryWindow').jqxValidator({
			rules:[
					{input: '#insuranceSalaryAmount', message: uiLabelMap.AmountValueGreaterThanZero, action: 'blur',
						rule: function (input, commit){
							var value = input.val();
							if(value < 0){
								return false
							}
							return true;
						}	
					},
					{input: '#insuranceSalaryAmount', message: uiLabelMap.FieldRequired, action: 'blur',
						rule: function (input, commit){
							if(!$(input).val()){
								return false
							}
							return true;
						}	
					},
					{input: '#partyIdNew', message: uiLabelMap.FieldRequired, action: 'blur',
						rule: function (input, commit){
							var value = input.val();
							if(!value){
								return false
							}
							return true;
						}	
					},
					{input: '#periodTypeId', message: uiLabelMap.FieldRequired, action: 'blur',
						rule: function (input, commit){
							var value = input.val();
							if(!value){
								return false
							}
							return true;
						}	
					},
					{input: '#fromDate', message: uiLabelMap.FieldRequired, action: 'blur',
						rule: function (input, commit){
							var value = input.val();
							if(!value){
								return false
							}
							return true;
						}	
					},
					{input: '#thruDate', message: uiLabelMap.ThruDateMustGreaterThanFromDate, action: 'blur',
						rule: function (input, commit){
							var value = input.jqxDateTimeInput('val', 'date');
							if(value){
								var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
								if(value.getTime() < fromDate.getTime()){
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

var configInsSalaryByPos = (function(){
	var init = function(){
		initJqxDateTimeInput();
		initJqxDropDownList();
		initBtnEvent();
		initJqxValidator();
		initJqxWindow();
	};
	
	var initBtnEvent = function(){
		$("#cancelConfig").click(function(event){
			$('#settingSalaryByPositionWindow').jqxWindow('close');
		});
		$("#saveConfig").click(function(event){
			if(!validate()){
				return;
			}
			createEmplInsuranceSalByPosition();
			$('#settingSalaryByPositionWindow').jqxWindow('close');
		});
	};
	
	var createEmplInsuranceSalByPosition = function(){
		var data = {};
		var fromDate = $("#configFromDate").jqxDateTimeInput('val', 'date');
		var thruDate = $("#configThruDate").jqxDateTimeInput('val', 'date');
		data.overrideDataWay = $("#configPyrllParamSettingDropdown").val();
		data.fromDate = fromDate.getTime();
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		$("#jqxgrid").jqxGrid('showloadelement');
		$("#jqxgrid").jqxGrid({ disabled: true});
		$.ajax({
	    	url: 'settingEmplInsuranceSalaryByPosType',
	    	data: data,
	    	type: 'POST',
	    	success: function(response){
	    		$("#jqxNtf").jqxNotification('closeLast');
	    		if(response.responseMessage == "success"){
	    			$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({ template: 'info' });
					$("#jqxNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
	    		}else{
	    			$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({ template: 'error' });
					$("#jqxNtf").jqxNotification('open');
	    		}
	    	},
	    	complete: function(jqXHR, textStatus){
	    		$("#jqxgrid").jqxGrid('hideloadelement');
	    		$("#jqxgrid").jqxGrid({ disabled: false});
	    	}
	    });
	};
	
	var validate = function(){
		return $('#settingSalaryByPositionWindow').jqxValidator('validate');
	};
	
	var initJqxValidator = function(){
		$('#settingSalaryByPositionWindow').jqxValidator({
			rules:[
				{input: '#configFromDate', message: uiLabelMap.FieldRequired, action: 'blur',
					rule: function (input, commit){
						var value = input.val();
						if(!value){
							return false
						}
						return true;
					}	
				},
				{input: '#configThruDate', message: uiLabelMap.ThruDateMustGreaterThanFromDate, action: 'blur',
					rule: function (input, commit){
						var value = input.jqxDateTimeInput('val', 'date');
						if(value){
							var fromDate = $("#configFromDate").jqxDateTimeInput('val', 'date');
							if(value.getTime() < fromDate.getTime()){
								return false;
							}
						}
						return true;
					}	
				},
			]
		});
	};
	
	var initJqxDropDownList = function(){
		var configPayrollParamSetting = [
				{value: 'getValueHighest', description: uiLabelMap.PayrollParamPositionHighest},                             
				{value: 'getValueLowest', description: uiLabelMap.PayrollParamPositionLowest},                             		                 		    	                          
		];
		createJqxDropDownList(configPayrollParamSetting, $('#configSettingDropdown'), "value", "description", 25, "98%");	
	};
	
	var initJqxWindow = function(){
		createJqxWindow($('#settingSalaryByPositionWindow'), 420, 240);
		$("#settingSalaryByPositionWindow").on('open', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			$("#configFromDate").val(selection.from);
			$("#configThruDate").val(null);
			$("#configSettingDropdown").jqxDropDownList('selectItem', 'getValueHighest');
			
		});
		
		$("#settingSalaryByPositionWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	
	var initJqxDateTimeInput = function(){
		$("#configFromDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#configThruDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius', showFooter: true});
		restrictFomDateThruDate($("#configFromDate"), $("#configThruDate"));
	};
	return{
		init: init
	}
}());
var removeFilter = (function(){
	var init = function(){
		initBtnEvent();
	};
	var initBtnEvent = function(){
		$("#removeFilter").click(function(){
			$("#jqxgrid").jqxGrid('clearfilters');
		})
	}
	return{
		init : init
	}
}());
$(document).ready(function () {
	viewPartyInsObject.init();
	editPartyInsObject.init();
	configInsSalaryByPos.init();
	removeFilter.init();
});
