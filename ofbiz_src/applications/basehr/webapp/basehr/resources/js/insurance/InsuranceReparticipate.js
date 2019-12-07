var reparticipateInsObj = (function(){
	var _isDataInitiated = false;
	var _partyIdSelected = [];
	var init = function(){
		initSimpleInput();
		initDateTime();
		initNumberInput();
		initGrid();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerRearticipate"));
	};
	var initSimpleInput = function(){
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			$("#insuranceTypeReParticipate" + insuranceType.insuranceTypeId).jqxCheckBox({ width: 60, height: 25, 
				checked: isCompulsory == "Y", disabled: isCompulsory == "Y"});
			$("#rateReparticipate" + insuranceType.insuranceTypeId).jqxNumberInput({ width: 60, height: 25, digits: 3, 
				symbolPosition: 'right', symbol: '%', spinButtons: false, disabled: true, decimal: insuranceType.employeeRate});
		});
		$("#isInsuranceSalaryUnchange").jqxCheckBox({width: 140, height: 25})
	};
	var initDateTime = function(){
		var monthDataRequire = [];
		var monthDataNotRequire = [{month: -1, description: "-----------"}];
		for(var i = 0; i < 12; i++){
			monthDataRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
			monthDataNotRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthDataRequire, $("#monthStartReparticipate"), "month", "description", 25, 90);
		$("#yearStartReparticipate").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		createJqxDropDownList(monthDataNotRequire, $("#monthEndReparticipate"), "month", "description", 25, 90);
		$("#yearEndReparticipate").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initNumberInput = function(){
		$("#reparticipateInsSal").jqxNumberInput({ width: '100%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#reparticipateAllowancePos").jqxNumberInput({ width: '100%', height: '25px',
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#reparticipateAllowanceSeniority").jqxNumberInput({ width: '100%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#reparticipateAllowanceSeniorityExces").jqxNumberInput({ width: '100%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#reparticipateAllowanceOther").jqxNumberInput({ width: '100%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
	};
	var initGrid = function(){
		var grid =  $("#listEmplSuspendGrid");
		var datafield = [{name: 'isSelected', type: 'bool'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'firstName', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'emplPositionTypeDesc', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'insuranceSocialNbr', type: 'string'},
		                 {name: 'insHealthCard', type: 'string'},
		                 {name: 'insuranceSalary', type: 'number'},
		                 ];
		var columns = [{ text: '', datafield: 'isSelected', columntype: 'checkbox', width: '6%', editable: true, pinned: true, filterable: false,
					cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
						var rowData = grid.jqxGrid('getrowdata', row);
						if(oldvalue){
							if(_partyIdSelected.indexOf(rowData.partyId) < 0){
								_partyIdSelected.push(rowData.partyId);
							}
						}else{
							var index = _partyIdSelected.indexOf(rowData.partyId);
							_partyIdSelected.splice(index, 1);
						}
				    }
			   },
		       {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '13%', editable: false},
		       {text: uiLabelMap.EmployeeName, datafield: 'firstName', width: '17%', editable: false,
				   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
					   var rowData = grid.jqxGrid('getrowdata', row);
					   if(rowData){
						   return '<span>' + rowData.fullName + '</span>';
					   }
				   }
			   },
			   {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionTypeDesc', width: '17%', editable: false},
			   {text: uiLabelMap.CommonDepartment, datafield: 'groupName', editable: false, width: '17%'},
			   {text: uiLabelMap.SocialInsuranceNbrIdentify, datafield: 'insuranceSocialNbr', width: '15%', editable: false},
			   {text: uiLabelMap.HealthInsuranceNbr, datafield: 'insHealthCard', width: '13%', editable: false},
			   {text: uiLabelMap.TotalInsuranceSocialSalary, datafield: 'insuranceSalary', width: '16%', editable: false, filterType : 'number',
				   cellsrenderer: function (row, column, value) {
						if(value){
							return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
						}
					}
			   },
		       ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "listEmplSuspendGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.InsuranceReparticipateForEmpl + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
			var container = $('#toolbarButtonContainer' + id);
			var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
		   		width: '100%', 
		   		autoheight: true,
		   		virtualmode: true,
		   		showfilterrow: true,
		   		showtoolbar: true,
		   		rendertoolbar: rendertoolbar,
		        filterable: true,
		        editable: true,
		        url: '',                
		        source: {
		        	pagesize: 5,
		        	id: 'partyId'
		        },
		        pagesizeoptions: ['5', '10', '15', '20']
	   	};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		createJqxWindow($("#reparticipateWindow"), 870, 580);
	};
	var initEvent = function(){
		$("#reparticipateWindow").on('close', function(event){
			resetData();
		});
		$("#reparticipateWindow").on('open', function(event){
			initData();
		});
		$("#isInsuranceSalaryUnchange").on('change', function(event){
			var checked = event.args.checked;
			$("#reparticipateInsSal, #reparticipateAllowancePos, #reparticipateAllowanceSeniority," +
					" #reparticipateAllowanceSeniorityExces, #reparticipateAllowanceOther").jqxNumberInput({disabled: checked});
		});
		$("#monthEndReparticipate").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearEndReparticipate").jqxNumberInput({disabled: true});
				}else{
					$("#yearEndReparticipate").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#monthStartReparticipate").on('select', function(event){
			var args = event.args;
			if(_isDataInitiated){
				if(args){			
					var args = event.args;
					if (args) {
						var month = args.item.value;
						var year = $("#yearStartReparticipate").val();
						refreshGridData(month, year);
					}
				}
				_partyIdSelected = [];
			}
		});
		$("#yearStartReparticipate").on('valueChanged', function(event){
			if(_isDataInitiated){
				var year = event.args.value;
				var month = $("#monthStartReparticipate").val();
				refreshGridData(month, year);
				_partyIdSelected = [];
			}
		});
		$("#cancelReparticipate").click(function(event){
			$("#reparticipateWindow").jqxWindow('close');
		});
		$("#saveContinueReparticipate").click(function(event){
			var valid = $("#reparticipateWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(_partyIdSelected.length == 0){
				bootbox.dialog(uiLabelMap.NoPartyChoose,
						[{
							"label": uiLabelMap.CommonClose,
							"class": "btn-danger icon-remove btn-small open-sans",
						}]
					);
				return;
			}
			bootbox.dialog(uiLabelMap.CreateReparticipateConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						createEmplReparicipate(false);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
		$("#saveReparticipate").click(function(event){
			var valid = $("#reparticipateWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(_partyIdSelected.length == 0){
				bootbox.dialog(uiLabelMap.NoPartyChoose,
						[{
							"label": uiLabelMap.CommonClose,
							"class": "btn-danger icon-remove btn-small open-sans",
						}]
					);
				return;
			}
			bootbox.dialog(uiLabelMap.CreateReparticipateConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						 createEmplReparicipate(true);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
	};
	var getData = function(){
		var data = {};
		var month = $("#monthStartReparticipate").val();
		var year = $("#yearStartReparticipate").val();
		var fromDate = new Date(year, month, 1);
		data.fromDate = fromDate.getTime();
		month = $("#monthEndReparticipate").val();
		year = $("#yearEndReparticipate").val();
		if(month > -1){
			var thruDate = new Date(year, month, 1);
			data.thruDate = thruDate.getTime();
		}
		var insuranceTypeArr = [];
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			if("Y" != isCompulsory){
				if($("#insuranceType" + insuranceType.insuranceTypeId).jqxCheckBox('checked')){
					insuranceTypeArr.push(insuranceType.insuranceTypeId);
				}
			}
		});
		if(insuranceTypeArr.length > 0){
			data.insuranceTypeNotCompulsory = JSON.stringify(insuranceTypeArr);
		}
		var checked = $("#isInsuranceSalaryUnchange").jqxCheckBox('checked');
		if(!checked){
			data.amount = $("#reparticipateInsSal").val();
			data.allowanceSeniority = $("#reparticipateAllowanceSeniority").val();
			data.allowanceSeniorityExces = $("#reparticipateAllowanceSeniorityExces").val();
			data.allowancePosition = $("#reparticipateAllowancePos").val();
			data.allowanceOther = $("#reparticipateAllowanceOther").val();
		}else{
			data.isInsuranceSalaryUnchange = "Y";
		}
		data.partyIds = JSON.stringify(_partyIdSelected);
		return data;
	};
	var createEmplReparicipate = function(isCloseWindow){
		var data = getData();
		disableAll();
		$.ajax({
			url: 'createEmplReparicipate',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					if(isCloseWindow){
						$("#reparticipateWindow").jqxWindow('close');
					}else{
						resetData();
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
				enableAll();
			}
		});
	};
	var initData = function(){
		var date = new Date();
		var month = date.getMonth();
		var year = date.getFullYear();
		$("#monthStartReparticipate").val(month);
		$("#monthEndReparticipate").jqxDropDownList({selectedIndex: 0});
		$("#yearStartReparticipate").val(year);
		$("#yearEndReparticipate").val(year);
		$("#isInsuranceSalaryUnchange").jqxCheckBox({checked: true});
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			$("#insuranceTypeReParticipate" + insuranceType.insuranceTypeId).jqxCheckBox({checked: isCompulsory == "Y"});
			$("#rateReparticipate" + insuranceType.insuranceTypeId).jqxNumberInput({ decimal: insuranceType.employeeRate});
		});
		refreshGridData(month, year);
		_isDataInitiated = true;
	};
	var resetData = function(){
		_isDataInitiated = false;
		_partyIdSelected = [];
		Grid.clearForm($("#reparticipateInfo"));
		
	};
	var openWindow = function(){
		openJqxWindow($("#reparticipateWindow"));
	};
	var initValidator = function(){
		$("#reparticipateWindow").jqxValidator({
			rules: [
				{ input: '#yearEndReparticipate', message: uiLabelMap.ParticipateThruGreateThanParticipateFrom, action: 'blur',
					rule : function(input, commit){
						var monthThru = $("#monthEndReparticipate").val();
						var yearThru = $("#yearEndReparticipate").val();
						if(monthThru > -1){
							var monthFrom = $("#monthStartReparticipate").val();
							var yearFrom = $("#yearStartReparticipate").val();
							var fromDate = new Date(yearFrom, monthFrom, 1);
							var thruDate = new Date(yearThru, monthThru, 1);
							if(fromDate > thruDate){
								return false;
							}
						}
						return true;
					}
				},     
				{ input: '#reparticipateInsSal', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'blur',
					rule : function(input, commit){
						var checked = $("#isInsuranceSalaryUnchange").jqxCheckBox('checked');
						if(!checked && $(input).val() <= 0){
							return false;
						}
						return true;
					}
				},     
			]
		});
	};
	var refreshGridData = function(month, year){
		var source = $("#listEmplSuspendGrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListInsSuspend&month=" + month + "&year=" + year;;
		$("#listEmplSuspendGrid").jqxGrid('source', source);
	};
	var disableAll = function(){
		$("#loadingRearticipate").show();
		$("#cancelReparticipate").attr("disabled", "disabled");
		$("#saveContinueReparticipate").attr("disabled", "disabled");
		$("#saveReparticipate").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingRearticipate").hide();
		$("#cancelReparticipate").removeAttr("disabled");
		$("#saveContinueReparticipate").removeAttr("disabled");
		$("#saveReparticipate").removeAttr("disabled");
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	reparticipateInsObj.init();
});