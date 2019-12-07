var editAllocCostPeriodItemObj = (function(){
	var _allocCostPeriodId;
	var _listParty = [];
	var init = function(){
		initGrid();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var datafield = [];
		var columns = [];
		var grid = $("#editAllocationCostItemGrid");
		
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "editAllocationCostItemGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.CostAllocationDetailShort + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				pageable: true,
				editmode: 'dblclick',
				selectionmode: 'singlecell',
				columngroups: [],
				source:{
					pagesize: 10,
					localdata: []
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initEvent = function(){
		$("#EditAllocCostPeriodWindow").on('open', function(event){
			initOpen();
		});
		$("#EditAllocCostPeriodWindow").on('close', function(event){
			resetData();
		});
		$("#cancelEditAlloc").click(function(event){
			$("#EditAllocCostPeriodWindow").jqxWindow('close');
		});
		$("#saveEditAlloc").click(function(event){
			var rows = $("#editAllocationCostItemGrid").jqxGrid('getrows');
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
					return;
				}
			}
			saveAllocationCostPeriodItem();
		});
	};
	
	var saveAllocationCostPeriodItem = function(){
		Loading.show('loadingMacro');
		$("#cancelEditAlloc").attr("disabled", "disabled");
		$("#saveEditAlloc").attr("disabled", "disabled");
		var data = {allocCostPeriodId: _allocCostPeriodId};
		var costAllocationGlAccData = [];
		var rows = $("#editAllocationCostItemGrid").jqxGrid('getrows');
		rows.forEach(function(rowData){
			for(var i = 0; i < _listParty.length; i++){
				var partyId = _listParty[i].partyId;
				var tempGlAccData = {};
				tempGlAccData.glAccountId = rowData.glAccountId;
				tempGlAccData.partyId = partyId;
				tempGlAccData.allocationCostTypeId = rowData.allocationCostTypeId;
				tempGlAccData.allocationRate = rowData[partyId + "_percent"];
				tempGlAccData.allocCostPeriodSeqId = rowData[partyId + "_seqId"];
				costAllocationGlAccData.push(tempGlAccData);
			}
		});
		data.allocationCostPeriodItem = JSON.stringify(costAllocationGlAccData);
		$.ajax({
			url: 'editAllocationCostPeriodItem',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
					$("#EditAllocCostPeriodWindow").jqxWindow('close');
					//$("#jqxgrid").jqxGrid('updatebounddata');
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
				$("#cancelEditAlloc").removeAttr("disabled");
				$("#saveEditAlloc").removeAttr("disabled");
			}
		});
	};
	
	var initOpen = function(){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getAllocationCostPeriodItemData',
			data: {allocCostPeriodId: _allocCostPeriodId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				renderColumnDatafield(response.listParty, response.listData);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var renderColumnDatafield = function(listParty, listData){
		_listParty = listParty;
		var grid = $("#editAllocationCostItemGrid");
		var disableCellClass = function (row, column, value, data) {
			return "disableCellEditor";
		};
		var datafield = [{name: 'glAccountId', type: 'string'},
		                 {name: 'accountCode', type: 'string'},
		                 {name: 'accountName', type: 'string'},
		                 {name: 'totalCost', type: 'number'},
		                 {name: 'allocationCostTypeId', type: 'string'},
		                 {name: 'totalPercent', type: 'number'},];
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
		for(var i = 0; i < listParty.length; i++){
			var organization = listParty[i];
			datafield.push({name: organization.partyId + "_quantity", type: 'number'});
			datafield.push({name: organization.partyId + "_percent", type: 'number'});
			datafield.push({name: organization.partyId + "_seqId", type: 'string'});
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
			columngroups.push({text: organization.fullName, align: 'center', name: organization.partyId});
		}
		updateGrid(datafield, columns, columngroups, listData);
	};
	
	var updateGrid = function(datafield, columns, columngroups, localdata){
		var grid = $("#editAllocationCostItemGrid");
		var source = grid.jqxGrid('source');
		grid.jqxGrid('columns', columns);
		grid.jqxGrid('columngroups', columngroups);
		
		var source = grid.jqxGrid('source');
		source._source.datafields = datafield;
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
		grid.jqxGrid('refresh');
	};
	
	var resetData = function(){
		updateGrid([], [], [], []);
		_allocCostPeriodId = "";
		_listParty = [];
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#EditAllocCostPeriodWindow"), 880, 500);
	};
	var openWindow = function(allocCostPeriodId){
		_allocCostPeriodId = allocCostPeriodId;
		accutils.openJqxWindow($("#EditAllocCostPeriodWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	editAllocCostPeriodItemObj.init();
});