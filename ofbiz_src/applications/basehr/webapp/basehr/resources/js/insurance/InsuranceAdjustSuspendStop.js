var insuranceAdjSuspendStopObj = (function(){
	var _isDataInitiated = false;
	var _partyIdSelected = [];
	var init = function(){
		initGrid();
		initDateTime();
		initDropDown();
		initInput();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerSuspendStopParticipate"));
	};
	var initGrid = function(){
		var grid =  $("#listEmplParticipateGrid");
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
							   var rowData = $('#listEmplParticipateGrid').jqxGrid('getrowdata', row);
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
			var id = "listEmplParticipateGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.InsuranceAdjustSuspendStopParticipateEmpl + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
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
	var initDateTime = function(){
		var monthDataRequire = [];
		var monthDataNotRequire = [{month: -1, description: "-----------"}];
		for(var i = 0; i < 12; i++){
			monthDataRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
			monthDataNotRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthDataRequire, $("#monthStartSuspendStop"), "month", "description", 25, 100);
		$("#yearStartSuspendStop").jqxNumberInput({ width: 80, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		createJqxDropDownList(monthDataNotRequire, $("#monthEndSuspendStop"), "month", "description", 25, 100);
		$("#yearEndSuspendStop").jqxNumberInput({ width: 80, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});

		createJqxDropDownList(monthDataRequire, $("#monthStartSupplement"), "month", "description", 25, 100);
		$("#yearStartSupplement").jqxNumberInput({ width: 80, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		createJqxDropDownList(monthDataNotRequire, $("#monthEndSupplement"), "month", "description", 25, 100);
		$("#yearEndSupplement").jqxNumberInput({ width: 80, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.suspendInsReasonTypeArr, $("#suspendReasonList"), "suspendReasonId", "description", 25, 190);
	};
	var initInput = function(){
		$("#isReturnSHICard").jqxRadioButton({ width: 200, height: 25});
		$("#isSupplement").jqxRadioButton({ width: 200, height: 25});
	};
	var initWindow = function(){
		createJqxWindow($("#adjustSuspendStopWindow"), 800, 580);
	};
	var initValidator = function(){
		$("#adjustSuspendStopWindow").jqxValidator({
			rules: [
				{ input: '#yearEndSuspendStop', message: uiLabelMap.SuspendThruGreateThanSuspendFrom, action: 'blur',
					rule : function(input, commit){
						var monthThru = $("#monthEndSuspendStop").val();
						var yearThru = $("#yearEndSuspendStop").val();
						if(monthThru > -1){
							var monthFrom = $("#monthStartSuspendStop").val();
							var yearFrom = $("#yearStartSuspendStop").val();
							var fromDate = new Date(yearFrom, monthFrom, 1);
							var thruDate = new Date(yearThru, monthThru, 1);
							if(fromDate > thruDate){
								return false;
							}
						}
						return true;
					}
				}, 
				{input: '#yearEndSupplement', message: uiLabelMap.ReturnSHIThruGreateThanReturnSHIFrom, action: 'blur',
					rule : function(input, commit){
						var isSupplement = $("#isSupplement").jqxRadioButton('checked');
						if(!isSupplement){
							return true;
						}
						var monthThru = $("#monthEndSupplement").val();
						var yearThru = $("#yearEndSupplement").val();
						if(monthThru > -1){
							var monthFrom = $("#monthStartSupplement").val();
							var yearFrom = $("#yearStartSupplement").val();
							var fromDate = new Date(yearFrom, monthFrom, 1);
							var thruDate = new Date(yearThru, monthThru, 1);
							if(fromDate > thruDate){
								return false;
							}
						}
						return true;
					}
				}, 
				{ input: '#suspendReasonList', message: uiLabelMap.FieldRequired, action: 'none',
					rule : function(input, commit){
						var value = $(input).val();
						if(!value){
							return false;
						}
						return true;
					}
				}, 
			]
		});
	};
	var initEvent = function(){
		$("#suspendReasonList").on('select', function(event){
			var args = event.args;
			var index = args.index;
			var data = globalVar.suspendInsReasonTypeArr[index];
			if("Y" == data.isRequestReturnCard){
				$("#supplementInfo").show();
				$("#isReturnSHICard").jqxRadioButton({checked: true});
			}else{
				$("#supplementInfo").hide();
				$("#isReturnSHICard").jqxRadioButton({checked: false});
				$("#isSupplement").jqxRadioButton({checked: false});
			}
			var value = args.item.value;
			if(value == "TERMINATION"){
				$("#monthEndSuspendStop").jqxDropDownList({selectedIndex: 0, disabled: true});
			}else{
				$("#monthEndSuspendStop").jqxDropDownList({disabled: false});
			}
		});
		$("#isReturnSHICard").on('change', function(event){
			var checked = event.args.checked;
			if(checked){
				$("#monthStartSupplement").jqxDropDownList({disabled: true});
				$("#yearStartSupplement").jqxNumberInput({disabled: true});
				$("#monthEndSupplement").jqxDropDownList({selectedIndex: 0, disabled: true});
			}else{
				$("#monthStartSupplement").jqxDropDownList({disabled: false});
				$("#yearStartSupplement").jqxNumberInput({disabled: false});
				$("#monthEndSupplement").jqxDropDownList({disabled: false});
			}
		});
		$("#monthEndSupplement").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearEndSupplement").jqxNumberInput({disabled: true});
				}else{
					$("#yearEndSupplement").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#monthEndSuspendStop").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearEndSuspendStop").jqxNumberInput({disabled: true});
				}else{
					$("#yearEndSuspendStop").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#monthStartSuspendStop").on('select', function(event){
			var args = event.args;
			if(_isDataInitiated){
				if(args){			
					var args = event.args;
					if (args) {
						var month = args.item.value;
						var year = $("#yearStartSuspendStop").val();
						refreshGridData(month, year);
					}
				}
				_partyIdSelected = [];
			}
		});
		$("#yearStartSuspendStop").on('valueChanged', function(event){
			if(_isDataInitiated){
				var year = event.args.value;
				var month = $("#monthStartSuspendStop").val();
				refreshGridData(month, year);
				_partyIdSelected = [];
			}
		});
		$("#adjustSuspendStopWindow").on('close', function(event){
			resetData();
		});
		$("#adjustSuspendStopWindow").on('open', function(event){
			initData();
		});
		$("#listEmplParticipateGrid").on('bindingcomplete', function(event){
			_partyIdSelected.forEach(function(partyId){
				$("#listEmplParticipateGrid").jqxGrid('setcellvaluebyid', partyId, "isSelected", true);
			});
		});
		$("#cancelAdjSuspendStop").click(function(event){
			$("#adjustSuspendStopWindow").jqxWindow('close');
		});
		$("#saveContinueAdjSuspendStop").click(function(event){
			var valid = $("#adjustSuspendStopWindow").jqxValidator('validate');
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
			bootbox.dialog(uiLabelMap.CreateSuspendOrStopParticipateConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						createEmplSuspendOrStop(false);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
		$("#saveAdjSuspendStop").click(function(event){
			var valid = $("#adjustSuspendStopWindow").jqxValidator('validate');
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
			bootbox.dialog(uiLabelMap.CreateSuspendOrStopParticipateConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						 createEmplSuspendOrStop(true);
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
		var month = $("#monthStartSuspendStop").val();
		var year = $("#yearStartSuspendStop").val();
		var fromDate = new Date(year, month, 1);
		data.fromDate = fromDate.getTime();
		month = $("#monthEndSuspendStop").val();
		year = $("#yearEndSuspendStop").val();
		if(month > -1){
			var thruDate = new Date(year, month, 1);
			data.thruDate = thruDate.getTime();
		}
		data.suspendReasonId = $("#suspendReasonList").val();
		var isReturnSHICard = $("#isReturnSHICard").jqxRadioButton('checked');
		var isSupplement = $("#isSupplement").jqxRadioButton('checked');
		if(isReturnSHICard){
			data.isReturnSHICard = "Y";
		}else if(isSupplement){
			data.isSupplement = "Y";
			month = $("#monthStartSupplement").val();
			year = $("#yearStartSupplement").val();
			data.supplementFromDate = (new Date(year, month, 1)).getTime();
			month = $("#monthEndSupplement").val();
			year = $("#yearEndSupplement").val();
			if(month > -1){
				data.supplementThruDate = (new Date(year, month, 1)).getTime();
			}
		}
		data.partyIds = JSON.stringify(_partyIdSelected);
		return data;
	};
	var createEmplSuspendOrStop = function(isCloseWindow){
		var data = getData();
		disableAll();
		$.ajax({
			url: 'createEmplSuspendOrStopParticipate',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					if(isCloseWindow){
						$("#adjustSuspendStopWindow").jqxWindow('close');
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
	var resetData = function(){
		_isDataInitiated = false;
		Grid.clearForm($("#insuranceSuspendInfo"));
		$("#supplementInfo").hide();
		_partyIdSelected = [];
	};
	var initData = function(){
		var date = new Date();
		var month = date.getMonth();
		var year = date.getFullYear();
		$("#monthStartSuspendStop").val(month);
		$("#monthStartSupplement").val(month);
		$("#monthEndSupplement").jqxDropDownList({selectedIndex: 0});
		$("#monthEndSuspendStop").jqxDropDownList({selectedIndex: 0});
		$("#yearStartSupplement").val(year);
		$("#yearEndSupplement").val(year);
		$("#yearStartSuspendStop").val(year);
		$("#yearEndSuspendStop").val(year);
		refreshGridData(month, year);
		_isDataInitiated = true;
	};
	var refreshGridData = function(month, year){
		var source = $("#listEmplParticipateGrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListInsEmplParticipating&month=" + month + "&year=" + year;;
		$("#listEmplParticipateGrid").jqxGrid('source', source);
	};
	var openWindow = function(){
		openJqxWindow($("#adjustSuspendStopWindow"));
	};
	var disableAll = function(){
		$("#loadingSuspendStopParticipate").show();
		$("#cancelAdjSuspendStop").attr("disabled", "disabled");
		$("#saveContinueAdjSuspendStop").attr("disabled", "disabled");
		$("#saveAdjSuspendStop").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingSuspendStopParticipate").hide();
		$("#cancelAdjSuspendStop").removeAttr("disabled");
		$("#saveContinueAdjSuspendStop").removeAttr("disabled");
		$("#saveAdjSuspendStop").removeAttr("disabled");
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	insuranceAdjSuspendStopObj.init();
});