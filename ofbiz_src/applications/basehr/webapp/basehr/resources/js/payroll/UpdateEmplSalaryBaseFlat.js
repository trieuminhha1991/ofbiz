var updateEmplSalaryObject = (function(){
	var _partyIdSelected = {};
	var _isDataInitiated = false;
	var init = function(){
		initSimpleInput();
		initDropDown();
		initGrid();
		initWindow();
		initValidator();
		initEvent();
		create_spinner($("#spinnerUpdate"));
	};
	var initSimpleInput = function(){
		$("#fromDateDailyUpdate").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDateDailyUpdate").jqxDateTimeInput({width: '98%', height: 25, showFooter: true});
		$("#yearFromMonthlyUpdate").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#yearThruMonthlyUpdate").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#yearFromYearlyUpdate").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#yearThruYearlyUpdate").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$("#amountSalUpdate").jqxNumberInput({ width: '98%', height: 25, spinButtons: true, decimalDigits: 0});
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeSalUpdate"), "periodTypeId", "description", 25, "98%");
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthData, $("#monthFromMonthlyUpdate"), "month", "description", 25, 90);
		monthData.unshift({month: -1, description: '--------'});
		createJqxDropDownList(monthData, $("#monthThruMonthlyUpdate"), "month", "description", 25, 90);
	};
	var initGrid = function(){
		var grid = $("#listEmplBaseSalGrid");
		var datafield = [{name: 'isSelected', type: 'bool'},
		                {name: 'partyIdFrom', type: 'string'},
						{name: 'partyIdTo', type: 'string'},
						{name: 'roleTypeIdFrom', type: 'string'},
						{name: 'roleTypeIdTo', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'firstName', type: 'string'},
						{name: 'fullName', type: 'string'},
						{name: 'emplPositionTypeDesc', type: 'string'},
						{name: 'amount', type: 'number'},
						{name: 'periodTypeId', type: 'string'},
						{name: 'fromDate', type: 'date', other: 'Timestamp'},];
		var columns = [{ text: '', datafield: 'isSelected', columntype: 'checkbox', width: '4%', editable: true, pinned: true, filterable: false,
							cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
								var rowData = grid.jqxGrid('getrowdata', row);
								if(oldvalue){
									if(_partyIdSelected.indexOf(rowData.partyIdTo) < 0){
										_partyIdSelected.push(rowData.partyIdTo);
									}
								}else{
									var index = _partyIdSelected.indexOf(rowData.partyIdTo);
									_partyIdSelected.splice(index, 1);
								}
						    }
					   },
		               {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '14%', editable: false},
					    {text: uiLabelMap.EmployeeName, datafield: 'firstName', width: '17%', cellsalign: 'left', editable: false,
				    	 cellsrenderer: function(row, column, value){
				    		 var rowData = grid.jqxGrid('getrowdata', row);
				    		 if(rowData && rowData.fullName){
				    			return '<span>' + rowData.fullName + '</span>';
				    		 }
				    	 }
				      },
					  {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionTypeDesc', width: '19%', editable: false,},
					  {text: uiLabelMap.HRCommonAmount, datafield: 'amount', width: '15%', columntype: 'numberinput', 
						  filtertype: 'number', editable: false,
						  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
								}
							}
					  },
					  {text: uiLabelMap.PeriodTypePayroll, datafield: 'periodTypeId', filtertype: 'checkedlist', 
						  columntype: 'dropdownlist', width:'16%', editable: false,
						  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							 for(var i = 0; i < globalVar.periodTypeArr.length; i++){
								 if(value == globalVar.periodTypeArr[i].periodTypeId){
									return '<span>' + globalVar.periodTypeArr[i].description + '</span>';
								 }
							 }
							 return '<span>' + value + '</span>';
						 },
						 createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.periodTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'periodTypeId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
						 },
					  },
					  {text: uiLabelMap.EffectiveFromDate, datafield: 'fromDate', width: '15%', cellsformat: 'dd/MM/yyyy',  
						 	filtertype : 'range', columntype: 'datetimeinput', editable: false,
						 	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						 		var rowData = grid.jqxGrid('getrowdata', row);
						 		if(rowData){
							 		var periodTypeId = rowData.periodTypeId;
							 		if(periodTypeId == 'MONTHLY'){
							 			return '<span>' + getMonth(value) + '/' + value.getFullYear() + '</span>';
							 		}else if(periodTypeId == 'DAILY'){
							 			return '<span>' + getDate(value) + '/' + getMonth(value) + '/' + value.getFullYear() + '</span>';
							 		}else if(periodTypeId == 'YEARLY'){
							 			return '<span>' + value.getFullYear() + '</span>';
							 		}
						 		}
						 	}
					  },
		              ];
		var config = {
		   		width: '100%', 
		   		autoheight: true,
		   		virtualmode: true,
		   		showfilterrow: true,
		   		showtoolbar: false,
		   		//rendertoolbar: rendertoolbar,
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
	var initWindow = function(){
		createJqxWindow($("#batchUpdateSalaryWindow"), 780, 550);
	};
	var initValidator = function(){
		$("#batchUpdateSalaryWindow").jqxValidator({
			rules: [
			        { input: '#amountSalUpdate', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'blur',
			        	rule : function(input, commit){
			        		if($(input).val() <= 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        }, 
			        { input: '#fromDateDailyUpdate', message: uiLabelMap.FieldRequired, action: 'none',
			        	rule: function (input, commit){
			        		var periodTypeId = $("#periodTypeSalUpdate").val();
			        		if(periodTypeId == "DAILY" && !input.val()){
			        			return false; 
			        		}
			        		return true;
			        	}
			        },  
			        { input: '#monthFromMonthlyUpdate', message: uiLabelMap.FieldRequired, action: 'none',
			        	rule: function (input, commit){
			        		var periodTypeId = $("#periodTypeSalUpdate").val();
			        		if(periodTypeId == "MONTHLY" && !input.val()){
			        			return false; 
			        		}
			        		return true;
			        	}
			        },  
			        { input: '#yearFromYearlyUpdate', message: uiLabelMap.FieldRequired, action: 'none',
			        	rule: function (input, commit){
			        		var periodTypeId = $("#periodTypeSalUpdate").val();
			        		if(periodTypeId == "YEARLY" && !input.val()){
			        			return false; 
			        		}
			        		return true;
			        	}
			        },  
			        
			        { input: '#thruDateDailyUpdate' , message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'none',
			        	rule: function (input, commit){
			        		var fromDate = $('#fromDateDailyUpdate').jqxDateTimeInput('val', 'date');
			        		var thruDate = $(input).jqxDateTimeInput('val', 'date');
			        		var periodTypeId = $("#periodTypeSalUpdate").val();
			        		if(periodTypeId == "DAILY" && fromDate && thruDate && thruDate < fromDate){
			        			return false;
			        		}
			        		return true;
			        	}
			        },  
			        { input: '#yearThruMonthlyUpdate' , message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'none',
			        	rule: function (input, commit){
			        		var periodTypeId = $("#periodTypeSalUpdate").val();
			        		if(periodTypeId == "MONTHLY"){
			        			var monthTo = $('#monthThruMonthlyUpdate').val();
			        			if(monthTo > -1){
			        				var monthFrom = $('#monthFromMonthlyUpdate').val();
			        				var yearFrom = $('#yearFromMonthlyUpdate').val();
			        				var yearTo = $('#yearThruMonthlyUpdate').val();
			        				var dateFrom = new Date();
			        				var dateTo = new Date();
			        				dateFrom.setDate(1);
			        				dateFrom.setMonth(monthFrom);
			        				dateFrom.setFullYear(yearFrom);
			        				dateTo.setDate(2);
			        				dateTo.setMonth(monthTo);
			        				dateTo.setFullYear(yearTo);
			        				if(dateFrom > dateTo){
			        					return false;
			        				}
			        			}
			        		}
			        		return true;
			        	}
			        }, 
			        { input: '#yearThruYearlyUpdate' , message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'none',
			        	rule: function (input, commit){
			        		var periodTypeId = $("#periodTypeSalUpdate").val();
			        		if(periodTypeId == "YEARLY"){
			        			var yearFrom = $("#yearFromYearlyUpdate").val();
			        			var yearTo = $("#yearThruYearlyUpdate").val();
			        			if(yearFrom > yearTo){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        ]
		});
	};
	var initEvent = function(){
		$("#periodTypeSalUpdate").on('select', function(event){
			var args = event.args;
			if(args){
				var periodTypeId = args.item.value;
				var fromDate = null;
				if(periodTypeId == "DAILY"){
					$("#batchUpdateSalaryWindow .periodTypeDaily").show();
					$("#batchUpdateSalaryWindow .periodTypeMonthly").hide();
					$("#batchUpdateSalaryWindow .periodTypeYearly").hide();
					if(_isDataInitiated){
						fromDate = $("#fromDateDailyUpdate").jqxDateTimeInput('val', 'date');
					}
				}else if(periodTypeId == "MONTHLY"){
					$("#batchUpdateSalaryWindow .periodTypeDaily").hide();
					$("#batchUpdateSalaryWindow .periodTypeMonthly").show();
					$("#batchUpdateSalaryWindow .periodTypeYearly").hide();
					if(_isDataInitiated){
						fromDate = new Date($("#yearFromMonthlyUpdate").val(), $("#monthFromMonthlyUpdate").val(), 1);
					}
				}else if(periodTypeId == "YEARLY"){
					$("#batchUpdateSalaryWindow .periodTypeDaily").hide();
					$("#batchUpdateSalaryWindow .periodTypeMonthly").hide();
					$("#batchUpdateSalaryWindow .periodTypeYearly").show();
					if(_isDataInitiated){
						fromDate = new Date($("#yearFromYearlyUpdate").val(), 0, 1);
					}
				}
				refreshGrid(fromDate, periodTypeId);
			}
		});
		$("#monthThruMonthlyUpdate").on('select', function(event){
			var args = event.args;
			if(args){
		    	var item = args.item;
		    	var month = item.value;
		    	if(month < 0){
		    		$("#yearThruMonthlyUpdate").jqxNumberInput({disabled: true});
		    	}else{
		    		$("#yearThruMonthlyUpdate").jqxNumberInput({disabled: false});
		    	}
		    }
		});
		
		$("#batchUpdateSalaryWindow").on('open', function(event){
			initData();
		});
		$("#batchUpdateSalaryWindow").on('close', function(event){
			clearData();
		});
		$("#fromDateDailyUpdate").on('valueChanged', function(event){
			if(_isDataInitiated){
				var fromDate = event.args.date; 
				refreshGrid(fromDate, "DAILY");
			}
		});
		$("#monthFromMonthlyUpdate").on('select', function(event){
			var args = event.args;
			if(_isDataInitiated){
				if(args){			
					var args = event.args;
					if (args) {
						var month = args.item.value;
						var year = $("#yearFromMonthlyUpdate").val();
						var fromDate = new Date(year, month, 1);
						refreshGrid(fromDate, "MONTHLY");
					}
				}
			}
		});
		$("#yearFromMonthlyUpdate").on('valueChanged', function(event){
			if(_isDataInitiated){
				var year = event.args.value;
				var month = $("#monthFromMonthlyUpdate").val();
				var fromDate = new Date(year, month, 1);
				refreshGrid(fromDate, "MONTHLY");
			}
		});
		$("#yearFromYearlyUpdate").on('valueChanged', function(event){
			if(_isDataInitiated){
				var year = event.args.value;
				var fromDate = new Date(year, 0, 1); 
				refreshGrid(fromDate, "YEARLY");
			}
		});
		$("#listEmplBaseSalGrid").on('bindingcomplete', function(event){
			_partyIdSelected.forEach(function(partyId){
				$("#listEmplBaseSalGrid").jqxGrid('setcellvaluebyid', partyId, "isSelected", true);
			});
		});
		$("#cancelUpdate").click(function(event){
			$("#batchUpdateSalaryWindow").jqxWindow('close');
		});
		$("#saveAndContinueUpdate").click(function(event){
			var valid = $("#batchUpdateSalaryWindow").jqxValidator('validate');
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
			bootbox.dialog(uiLabelMap.UpdateEmplSalaryBaseConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						updateEmplSalaryBase(false);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
		$("#saveUpdate").click(function(event){
			var valid = $("#batchUpdateSalaryWindow").jqxValidator('validate');
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
			bootbox.dialog(uiLabelMap.UpdateEmplSalaryBaseConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							updateEmplSalaryBase(true);
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
		var periodTypeId = $("#periodTypeSalUpdate").val();
		data.periodTypeId = periodTypeId;
		var fromDate = null, thruDate = null;
		if(periodTypeId == "DAILY"){
			fromDate = $("#fromDateDailyUpdate").jqxDateTimeInput('val', 'date');
			thruDate = $("#thruDateDailyUpdate").jqxDateTimeInput('val', 'date');
		}else if(periodTypeId == "MONTHLY"){
			var monthFrom = $("#monthFromMonthlyUpdate").val();
			var yearFrom = $("#yearFromMonthlyUpdate").val();
			fromDate = new Date(yearFrom, monthFrom, 1);
			var monthTo = $("#monthThruMonthlyUpdate").val();
			if(monthTo > -1){
				var yearTo = $("#yearThruMonthlyUpdate").val();
				thruDate = new Date(yearTo, parseInt(monthTo) + 1, 0);
			}
		}else if(periodTypeId == "YEARLY"){
			var yearFrom = $("#yearFromYearlyUpdate").val();
			var yearTo = $("#yearThruYearlyUpdate").val();
			fromDate = new Date(yearFrom, 0, 1);
			thruDate = new Date(yearTo, 11, 31);
		}
		data.fromDate = fromDate.getTime();
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		data.amount = $("#amountSalUpdate").val();
		data.partyIds = JSON.stringify(_partyIdSelected);
		return data;
	};
	var updateEmplSalaryBase = function(isCloseWindow){
		var data = getData();
		if(!validateTime(data)){
            bootbox.dialog(uiLabelMap.HRTimeIsNotValid,
                [{
                    "label": uiLabelMap.CommonClose,
                    "class": "btn-danger icon-remove btn-small open-sans",
                }]
            );
            return;
        };
		disableAll();
		$.ajax({
			url: 'editEmployeeSalaryBase',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					if(isCloseWindow){
						$("#batchUpdateSalaryWindow").jqxWindow('close');
					}else{
						clearData();
						initData();
					}
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
								"label": uiLabelMap.CommonClose,
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
	
	var validateTime = function (data) {
        var rows = $("#listEmplBaseSalGrid").jqxGrid("getrows");
        var maxTime = new Date();
        if(OlbCore.isEmpty(_partyIdSelected)){
            return false;
        }
        for(var i = 0;i<rows.length;i++){
            if($.inArray(rows[i].partyIdTo,_partyIdSelected)>-1 && rows[i].fromDate > maxTime){
                maxTime = rows[i].fromDate;
            }
        }
        if(maxTime < data.fromDate){
            return true;
        }
        return false;
    };

	var disableAll = function(){
		$("#loadingUpdate").show();
		$("#cancelUpdate").attr("disabled", "disabled");
		$("#saveAndContinueUpdate").attr("disabled", "disabled");
		$("#saveUpdate").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingUpdate").hide();
		$("#cancelUpdate").removeAttr("disabled");
		$("#saveAndContinueUpdate").removeAttr("disabled");
		$("#saveUpdate").removeAttr("disabled");
	};
	var refreshGrid = function(fromDate, periodTypeId){
		if(periodTypeId && fromDate){
			var source = $("#listEmplBaseSalGrid").jqxGrid('source');
			source._source.url = "jqxGeneralServicer?sname=JQListEmplBaseSalaryMaxDate&fromDate=" + fromDate.getTime() + "&periodTypeId=" + periodTypeId;
			$("#listEmplBaseSalGrid").jqxGrid('source', source);
			_partyIdSelected = [];
		}
	};
	var clearData = function(){
		_isDataInitiated = false;
		$("#batchUpdateSalaryWindow .periodTypeDaily").hide();
		$("#batchUpdateSalaryWindow .periodTypeDaily").hide();
		$("#batchUpdateSalaryWindow .periodTypeMonthly").hide();
		_partyIdSelected = {};
		$("#periodTypeSalUpdate").jqxDropDownList('clearSelection');
	};
	var initData = function(){
		var date = new Date();
		var monthStart = new Date(date.getFullYear(), date.getMonth(), 1);
		var monthEnd = new Date(date.getFullYear(), parseInt(date.getMonth()) + 1, 0);
		$("#monthThruMonthlyUpdate").jqxDropDownList({selectedIndex: 0});
		$("#monthFromMonthlyUpdate").jqxDropDownList({selectedIndex: date.getMonth()});
		$("#yearFromMonthlyUpdate").val(date.getFullYear());
		$("#yearFromYearlyUpdate").val(date.getFullYear());
		$("#yearThruMonthlyUpdate").val(date.getFullYear());
		$("#yearThruYearlyUpdate").val(date.getFullYear());
		$("#fromDateDailyUpdate").val(monthStart);
		$("#thruDateDailyUpdate").val(null);
		$("#periodTypeSalUpdate").val("MONTHLY");
		refreshGrid(monthStart, "MONTHLY");
		_isDataInitiated = true;
	};
	var openWindow = function(){
		openJqxWindow($("#batchUpdateSalaryWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	updateEmplSalaryObject.init();
});