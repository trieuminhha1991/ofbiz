var insAdjSalAndJobObj = (function(){
	var _partyId = null;
	var init = function(){
		initSimpleInput();
		initDateTime();
		initDropDown();
		initGrid();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerAdjustSalJob"));
	};
	var initSimpleInput = function(){
		$("#emplAdjustSalCode").jqxInput({width: '83.5%', height: 20, disabled: true});
		$("#emplAdjustSalSocialInsNbr").jqxInput({width: '96%', height: 20, disabled: true});
		$("#emplAdjustSalDept").jqxInput({width: '96%', height: 20, disabled: true});
		$("#emplAdjustSalName").jqxInput({width: '96%', height: 20, disabled: true});
		$("#oldInsuranceSalary").jqxNumberInput({ width: '98%', height: '25px', spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0, disabled: true});
		$("#newInsuranceSalary").jqxNumberInput({ width: '98%', height: '25px', spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
	};
	var initDateTime = function(){
		var monthDataRequire = [];
		var monthDataNotRequire = [{month: -1, description: "-----------"}];
		for(var i = 0; i < 12; i++){
			monthDataRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
			monthDataNotRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthDataRequire, $("#monthAdjSalOldInfoFrom"), "month", "description", 25, 90);
		$("#monthAdjSalOldInfoFrom").jqxDropDownList({disabled: true});
		$("#yearAdjSalOldInfoFrom").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});
		
		createJqxDropDownList(monthDataNotRequire, $("#monthAdjSalOldInfoTo"), "month", "description", 25, 90);
		$("#monthAdjSalOldInfoTo").jqxDropDownList({disabled: true});
		$("#yearAdjSalOldInfoTo").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});

		createJqxDropDownList(monthDataRequire, $("#monthAdjSalNewInfoFrom"), "month", "description", 25, 90);
		$("#yearAdjSalNewInfoFrom").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});

		createJqxDropDownList(monthDataNotRequire, $("#monthAdjSalNewInfoTo"), "month", "description", 25, 90);
		$("#yearAdjSalNewInfoTo").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#oldEmplPosTypeInfoList"), "emplPositionTypeId", "description", 25, "98%");
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#newEmplPosTypeInfoList"), "emplPositionTypeId", "description", 25, "98%");
		$("#oldEmplPosTypeInfoList").jqxDropDownList({disabled: true});
		
	};
	var initWindow = function(){
		createJqxWindow($("#adjustSalJobWindow"), 800, 420);
		createJqxWindow($("#listEmplParticipateInsWindow"), 750, 480);
	};
	var initData = function(){
		var date = new Date();
		var month = date.getMonth();
		var year = date.getFullYear();
		$("#monthAdjSalNewInfoFrom").val(month);
		$("#monthAdjSalNewInfoTo").jqxDropDownList({selectedIndex: 0});
		$("#yearAdjSalNewInfoFrom").val(year);
		$("#yearAdjSalNewInfoTo").val(year);
	};
	var resetData = function(){
		Grid.clearForm($("#adjustSalJobWindow"));
		_partyId = null;
	};
	var initEvent = function(){
		$("#adjustSalJobWindow").on('open', function(event){
			initData();
		});
		$("#adjustSalJobWindow").on('close', function(event){
			resetData();
		});
		$("#monthAdjSalNewInfoTo").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearAdjSalNewInfoTo").jqxNumberInput({disabled: true});
				}else{
					$("#yearAdjSalNewInfoTo").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#listEmplParticipateInsWindow").on('open', function(event){
			var month = $("#monthAdjSalNewInfoFrom").val();
			var year = $("#yearAdjSalNewInfoTo").val();
			var source = $("#listEmplCurrentParticipating").jqxGrid('source');
			source._source.url = 'jqxGeneralServicer?sname=JQGetListInsEmplParticipating&month=' + month + '&year=' + year;
			$("#listEmplCurrentParticipating").jqxGrid('source', source);
			$("#listEmplCurrentParticipating").jqxGrid('clearselection');
		});
		
		$("#listEmplParticipateInsWindow").on('close', function(event){
			var source = $("#listEmplCurrentParticipating").jqxGrid('source');
			source._source.url = '';
			$("#listEmplCurrentParticipating").jqxGrid('source', source);
		});
		
		$("#chooseEmplAdjustSal").click(function(event){
			openJqxWindow($("#listEmplParticipateInsWindow"));
		});
		
		$("#listEmplCurrentParticipating").on('rowdoubleclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#listEmplCurrentParticipating").jqxGrid('getrowdata', boundIndex);
			_partyId = rowData.partyId;
			$("#emplAdjustSalCode").val(rowData.partyCode);
			$("#emplAdjustSalDept").val(rowData.groupName);
			$("#emplAdjustSalName").val(rowData.fullName);
			$("#emplAdjustSalSocialInsNbr").val(rowData.insuranceSocialNbr);
			$("#oldEmplPosTypeInfoList").val(rowData.emplPositionTypeId);
			$("#oldInsuranceSalary").val(rowData.insuranceSalary);
			var salFromDate = rowData.salFromDate;
			var salThruDate = rowData.salThruDate;
			if(salFromDate){
				$("#monthAdjSalOldInfoFrom").val(salFromDate.getMonth());
				$("#yearAdjSalOldInfoFrom").val(salFromDate.getFullYear());
			}
			if(salThruDate){
				$("#monthAdjSalOldInfoTo").val(salThruDate.getMonth());
				$("#yearAdjSalOldInfoTo").val(salThruDate.getFullYear());
			}else{
				$("#monthAdjSalOldInfoTo").jqxDropDownList({selectedIndex: 0});
				$("#yearAdjSalOldInfoTo").val(null);
			}
			$("#listEmplParticipateInsWindow").jqxWindow('close');
		});
		$("#cancelAdjSalJob").click(function(event){
			$("#adjustSalJobWindow").jqxWindow('close');
		});
		$("#saveContinueAdjSalJob").click(function(event){
			var valid = $("#adjustSalJobWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateAdjustEmplSalaryAndJobConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						createAdjustEmplSalaryAndJob(false);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
		$("#saveAdjSalJob").click(function(event){
			var valid = $("#adjustSalJobWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateAdjustEmplSalaryAndJobConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						createAdjustEmplSalaryAndJob(true);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
	};
	
	var initGrid = function(){
		var grid =  $("#listEmplCurrentParticipating");
		var datafield = [
		                 {name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'firstName', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'emplPositionTypeDesc', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'insuranceSocialNbr', type: 'string'},
		                 {name: 'insHealthCard', type: 'string'},
		                 {name: 'insuranceSalary', type: 'number'},
		                 {name: 'emplPositionTypeId', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'salFromDate', type: 'date'},
		                 {name: 'salThruDate', type: 'date'},
		                 ];
		var columns = [
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
			var id = "listEmplCurrentParticipating";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.AdjustSalAndJobTitleForEmpl + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
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
				pagesize: 10,
				id: 'partyId'
			},
			pagesizeoptions: ['5', '10', '15', '20']
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initValidator = function(){
		$("#adjustSalJobWindow").jqxValidator({
			rules: [
				{ input: '#chooseEmplAdjustSal', message: uiLabelMap.FieldRequired, action: 'none',
					rule : function(input, commit){
						if(!_partyId){
							return false;
						}
						return true;
					}
				},      
				{ input: '#newEmplPosTypeInfoList', message: uiLabelMap.FieldRequired, action: 'none',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#newInsuranceSalary', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'none',
					rule : function(input, commit){
						if(input.val() > 0){
							return true;
						}
						return false;
					}
				},  
				{input: '#yearAdjSalNewInfoTo', message: uiLabelMap.ValueMustGreaterThanFrom, action: 'blur',
					rule : function(input, commit){
						var monthThru = $("#monthAdjSalNewInfoTo").val();
						var yearThru = $("#yearAdjSalNewInfoTo").val();
						if(monthThru > -1){
							var monthFrom = $("#monthAdjSalNewInfoFrom").val();
							var yearFrom = $("#yearAdjSalNewInfoFrom").val();
							var fromDate = new Date(yearFrom, monthFrom, 1);
							var thruDate = new Date(yearThru, monthThru, 1);
							if(fromDate > thruDate){
								return false;
							}
						}
						return true;
					}
				}, 
				{input: '#yearAdjSalNewInfoFrom', message: uiLabelMap.ValueMustBeGreaterThanOldInfoFromDate, action: 'blur',
					rule : function(input, commit){
						var monthFrom = $("#monthAdjSalNewInfoFrom").val();
						var yearFrom = $("#yearAdjSalNewInfoFrom").val();
						var fromDateNew = new Date(yearFrom, monthFrom, 1);
						var monthToOld = $("#monthAdjSalOldInfoTo").val();
						if(monthToOld < 0){
							var monthFromOld = $("#monthAdjSalOldInfoFrom").val();
							var yearFromOld = $("#yearAdjSalOldInfoFrom").val();
							var dateOld = new Date(yearFromOld, monthFromOld, 1);
							if(dateOld >= fromDateNew){
								return false;
							}
						}
						return true;
					}
				}, 
				{input: '#yearAdjSalNewInfoFrom', message: uiLabelMap.ValueMustBeGreaterThanOldInfoThruDate, action: 'blur',
					rule : function(input, commit){
						var monthFrom = $("#monthAdjSalNewInfoFrom").val();
						var yearFrom = $("#yearAdjSalNewInfoFrom").val();
						var fromDateNew = new Date(yearFrom, monthFrom, 1);
						var monthToOld = $("#monthAdjSalOldInfoTo").val();
						if(monthToOld > -1){
							var yearToOld = $("#yearAdjSalOldInfoTo").val();
							var dateOld = new Date(yearToOld, monthToOld, 1);
							if(dateOld >= fromDateNew){
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
		data.partyId = _partyId;
		var month = $("#monthAdjSalNewInfoFrom").val();
		var year = $("#yearAdjSalNewInfoFrom").val();
		var fromDate = new Date(year, month, 1);
		data.fromDate = fromDate.getTime();
		month = $("#monthAdjSalNewInfoTo").val();
		year = $("#yearAdjSalNewInfoTo").val();
		if(month > -1){
			var thruDate = new Date(year, month, 1);
			data.thruDate = thruDate.getTime();
		}
		data.emplPositionTypeId = $("#newEmplPosTypeInfoList").val();
		data.amount = $("#newInsuranceSalary").val();
		return data;
	};
	var createAdjustEmplSalaryAndJob = function(isCloseWindow){
		var data = getData();
		disableAll();
		$.ajax({
			url: 'createInsAdjustEmplSalaryAndJob',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					if(isCloseWindow){
						$("#adjustSalJobWindow").jqxWindow('close');
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
	var disableAll = function(){
		$("#loadingAdjustSalJob").show();
		$("#cancelAdjSalJob").attr("disabled", "disabled");
		$("#saveContinueAdjSalJob").attr("disabled", "disabled");
		$("#saveAdjSalJob").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingAdjustSalJob").hide();
		$("#cancelAdjSalJob").removeAttr("disabled");
		$("#saveContinueAdjSalJob").removeAttr("disabled");
		$("#saveAdjSalJob").removeAttr("disabled");
	};
	var openWindow = function(){
		openJqxWindow($("#adjustSalJobWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	insAdjSalAndJobObj.init();
});