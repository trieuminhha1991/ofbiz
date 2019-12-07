var createAllocCostPeriodObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#addAllocCostPeriodCode").jqxInput({width: '95%', height: 20});
		$("#addAllocCostPeriodName").jqxInput({width: '95%', height: 20});
		$("#addFromDate").jqxDateTimeInput({width: '90%', height: 25});
		$("#addThruDate").jqxDateTimeInput({width: '90%', height: 25});
	};
	
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#addAllocationCostTypeId"), globalVar.allocationCostTypeArr, 
				{width: '97%', height: 25, valueMember: 'allocationCostTypeId', displayMember: 'description'});
	};
	
	var initGrid = function(){
		var grid = $("#allocationCostItemGrid");
		var disableCellClass = function (row, column, value, data) {
			return "disableCellEditor";
		};
		var datafield = [{name: 'glAccountId', type: 'string'},
		                 {name: 'accountCode', type: 'string'},
		                 {name: 'accountName', type: 'string'},
		                 {name: 'totalCost', type: 'number'},
		                 {name: 'allocationCostTypeId', type: 'string'},
		                 {name: 'totalPercent', type: 'number'},
		                 ];
		var columns = [{text: uiLabelMap.BACCGlAccountCode, datafield: 'accountCode', width: '8%', editable: false, cellclassname: disableCellClass},
		               {text: uiLabelMap.BACCGlAccountName, datafield: 'accountName', width: '18%', editable: false, cellclassname: disableCellClass},
		               {text: uiLabelMap.TotalCost, datafield: 'totalCost', columntype: 'numberinput', width: '15%', 
		            	   filtertype  :'number', editable: false, cellclassname: disableCellClass,
		            	   cellsrenderer: function(row, colum, value){
						  		if(typeof(value) == 'number'){
						  			return '<span style="text-align: right">' + formatcurrency(value, globalVar.baseCurrencyUomId) + '</value>';
						  		}
						  	}
		               },
		               {text: uiLabelMap.AllocationCostType, datafield: 'allocationCostTypeId', width: '18%', columntype: 'dropdownlist',
						   cellsrenderer: function(row, columns, value){
							   for(var i = 0; i < globalVar.allocationCostTypeArr.length; i++){
								   if(value == globalVar.allocationCostTypeArr[i].allocationCostTypeId){
									   return '<span>' + globalVar.allocationCostTypeArr[i].description + '</span>';
								   }
							   }
						   },
						   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
							   accutils.createJqxDropDownList(editor, globalVar.allocationCostTypeArr, 
										{width: cellwidth, height: cellheight, valueMember: 'allocationCostTypeId', displayMember: 'description'});
						   },
						   initeditor: function (row, cellvalue, editor, celltext, pressedChar){
							   editor.val(cellvalue);
						   }
					   },
					   {text: uiLabelMap.BACCToalPercent, datafield: 'totalPercent', width: '8%', columntype: 'numberinput',
						   	editable: false, cellclassname: disableCellClass,
							cellsrenderer: function(row, columns, value){
								if(typeof(value) == "number"){
									return '<span style="text-align: right">' + value + '%</value>';
								}
							},
						}
		               ];
		var totalColumnOrgWidth = 100 - (8 + 18 + 15 + 18); 
		var columnWidth = totalColumnOrgWidth / globalVar.organizationArr.length;
		var minWidth = 20;
		if(columnWidth < minWidth){
			columnWidth = minWidth;
		}
		var columngroups = [];
		for(var i = 0; i < globalVar.organizationArr.length; i++){
			var organization = globalVar.organizationArr[i];
			datafield.push({name: organization.partyId + "_quantity", type: 'number'});
			datafield.push({name: organization.partyId + "_percent", type: 'number'});
			columns.push({text: uiLabelMap.BACCQuantity, datafield: organization.partyId + "_quantity", editable: false,
				width: 0.7 * columnWidth + '%', columntype: 'numberinput', columngroup: organization.partyId, cellclassname: disableCellClass,
				cellsrenderer: function(row, columns, value){
					if(typeof(value) == "number"){
						return '<span style="text-align: right">' + value + '</value>';
					}
				}
			});
			columns.push({text: uiLabelMap.BACCPercent, datafield: organization.partyId + "_percent", width: 0.33 * columnWidth + '%', columntype: 'numberinput', columngroup: organization.partyId,
							cellsrenderer: function(row, columns, value){
								if(typeof(value) == "number"){
									return '<span style="text-align: right">' + value + '%</value>';
								}
							},
							cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
								var rowData = grid.jqxGrid('getrowdata', row);
								var totalPercent = newvalue;
								globalVar.organizationArr.forEach(function(organization){
									var tempDatafield = organization.partyId + "_percent";
									if(tempDatafield !== datafield){
										totalPercent += rowData[tempDatafield];
									}
								});
								grid.jqxGrid('setcellvalue', row, 'totalPercent', totalPercent);
							}
						}
			);
			columngroups.push({text: organization.groupName, align: 'center', name: organization.partyId});
		}
		var config = {
				url: '',
				showtoolbar : false,
				width : '100%',
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				pageable: true,
				editmode: 'dblclick',
				selectionmode: 'singlecell',
				columngroups: columngroups,
				source:{
					pagesize: 10,
					localdata: []
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initEvent = function(){
		$("#AddAllocationCostPeriodWindow").on('open', function(event){
			initOpen();
		});
		$("#AddAllocationCostPeriodWindow").on('close', function(event){
			resetData();
		});
		$("#updateGridData").click(function(){
			getGlAccountCostInfo();
		});
		$("#cancelAddAlloc").click(function(){
			$("#AddAllocationCostPeriodWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddAlloc").click(function(){
			var validate = validateFormCreate();
			if(!validate){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateAllocationCostPeriodConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createAllocationCostPeriod(false);
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#saveAddAlloc").click(function(){
			var validate = validateFormCreate();
			if(!validate){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateAllocationCostPeriodConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createAllocationCostPeriod(true);
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
	};
	
	var getData = function(){
		var data = {};
		data.allocCostPeriodCode = $("#addAllocCostPeriodCode").val();
		data.allocCostPeriodName = $("#addAllocCostPeriodName").val();
		data.allocationCostTypeId = $("#addAllocationCostTypeId").val();
		data.fromDate = $("#addFromDate").jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $("#addThruDate").jqxDateTimeInput('val', 'date').getTime();
		var costAllocationGlAccData = [];
		var rows = $("#allocationCostItemGrid").jqxGrid('getrows');
		rows.forEach(function(rowData){
			for(var i = 0; i < globalVar.organizationArr.length; i++){
				var partyId = globalVar.organizationArr[i].partyId;
				var tempGlAccData = {};
				tempGlAccData.glAccountId = rowData.glAccountId;
				tempGlAccData.partyId = partyId;
				tempGlAccData.allocationCostTypeId = rowData.allocationCostTypeId;
				tempGlAccData.allocationRate = rowData[partyId + "_percent"];
				costAllocationGlAccData.push(tempGlAccData);
			}
		});
		data.allocationCostPeriodItem = JSON.stringify(costAllocationGlAccData);
		return data;
	};
	
	var createAllocationCostPeriod = function(isCloseWindow){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'createAllocationCostPeriod',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
					if(isCloseWindow){
						$("#AddAllocationCostPeriodWindow").jqxWindow('close');
					}else{
						resetData();
						initOpen();
					}
					$("#jqxgrid").jqxGrid('updatebounddata');
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
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var validateFormCreate = function(){
		var valid = $("#AddAllocationCostPeriodWindow").jqxValidator('validate');
		if(!valid){
			return false;
		}
		var rows = $("#allocationCostItemGrid").jqxGrid('getrows');
		for(var i = 0; i < rows.length; i++){
			var rowData = rows[i];
			if(rowData.totalPercent != 100){
				bootbox.dialog(uiLabelMap.TotalRateAllocGlAccount + " <b>" + rowData.accountCode + "</b> " + uiLabelMap.BACCNotEqualOneHundredPercent,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);	
				return false;
			}
		}
		return true;
	};
	
	var initValidator = function(){
		$("#AddAllocationCostPeriodWindow").jqxValidator({
			rules: [
			        {input: '#addAllocCostPeriodCode', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
						rule: function (input, commit) {
							if(!$(input).val()){
								return false;
							}
							return true;
						}
			        },
			        {input: '#addAllocCostPeriodName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
			        	rule: function (input, commit) {
			        		if(!$(input).val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {input: '#addAllocationCostTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
			        	rule: function (input, commit) {
			        		if(!$(input).val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {input: '#addThruDate', message: uiLabelMap.BSValidateDate, action: 'keyup, change', 
			        	rule: function (input, commit) {
			        		if(!$(input).val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			]
		});
	};
	
	var initOpen = function(){
		var date = new Date();
		var startMonth = new Date(date.getFullYear(), date.getMonth(), 1);
		var endMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0);
		$("#addFromDate").val(startMonth);
		$("#addThruDate").val(endMonth);
		$("#addAllocationCostTypeId").jqxDropDownList({selectedIndex: 0});
		getGlAccountCostInfo();
	};
	
	var getGlAccountCostInfo = function(){
		disableAll();
		var fromDate = $("#addFromDate").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#addThruDate").jqxDateTimeInput('val', 'date').getTime();
		var allocationCostTypeId = $("#addAllocationCostTypeId").val();
		var data = {fromDate: fromDate, thruDate: thruDate, allocationCostTypeId: allocationCostTypeId};
		
		$.ajax({
			url: 'getGlAccountIdCostInfo',
			type: 'POST',
			data: data,
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);	
					return;
				}
				updateGridLocalData(response.listReturn);
			},
			complete: function(){
				enableAll();
			}
		});
	};
	
	var updateGridLocalData = function(localdata){
		var source = $("#allocationCostItemGrid").jqxGrid('source');
		source._source.localdata = localdata;
		$("#allocationCostItemGrid").jqxGrid('source', source);
	};
	
	var resetData = function(){
		Grid.clearForm($("#AddAllocationCostPeriodWindow .form-legend"));
		updateGridLocalData([]);
	};
	
	var disableAll = function(){
		$("#addAllocCostPeriodCode").jqxInput({disabled: true});
		$("#addAllocCostPeriodName").jqxInput({disabled: true});
		$("#addFromDate").jqxDateTimeInput({disabled: true});
		$("#addThruDate").jqxDateTimeInput({disabled: true});
		$("#addAllocationCostTypeId").jqxDropDownList({disabled: true});
		$("#allocationCostItemGrid").jqxGrid({disabled: true});
		$("#allocationCostItemGrid").jqxGrid('showloadelement');
		$("#cancelAddAlloc").attr("disabled", "disabled");
		$("#saveAndContinueAddAlloc").attr("disabled", "disabled");
		$("#saveAddAlloc").attr("disabled", "disabled");
		$("#updateGridData").attr("disabled", "disabled");
	};
	
	var enableAll = function(){
		$("#addAllocCostPeriodCode").jqxInput({disabled: false});
		$("#addAllocCostPeriodName").jqxInput({disabled: false});
		$("#addFromDate").jqxDateTimeInput({disabled: false});
		$("#addThruDate").jqxDateTimeInput({disabled: false});
		$("#addAllocationCostTypeId").jqxDropDownList({disabled: false});
		$("#allocationCostItemGrid").jqxGrid({disabled: false});
		$("#allocationCostItemGrid").jqxGrid('hideloadelement');
		$("#cancelAddAlloc").removeAttr("disabled");
		$("#saveAndContinueAddAlloc").removeAttr("disabled");
		$("#saveAddAlloc").removeAttr("disabled");
		$("#updateGridData").removeAttr("disabled");
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#AddAllocationCostPeriodWindow"), 880, 600);
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	createAllocCostPeriodObj.init();
});