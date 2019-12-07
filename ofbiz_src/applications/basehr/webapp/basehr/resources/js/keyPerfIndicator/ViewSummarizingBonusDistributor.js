var summarizingDistributorObj = (function(){
	var init = function(){
		initSimpleInput();
		initDropDown();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerCreate"));
	};
	var initSimpleInput = function(){
		$("#salesBonusSummaryName").jqxInput({width: '96%', height: 20});
	};
	var quarterCustLoadComplete = function(){
		var today = new Date();
		$("#quarterCustomTime").jqxDropDownList({disabled: false});
		var currentQuarter = Math.floor((today.getMonth() + 3) / 3); 
		$("#quarterCustomTime").jqxDropDownList({selectedIndex: currentQuarter - 1});
	};
	var monthCustLoadComplete = function(){
		$("#monthCustomTime").jqxDropDownList({disabled: false});
		$("#monthCustomTime").jqxDropDownList({selectedIndex: 0});
	};
	var initDropDown = function(){
		var datafield = [{name: 'customTimePeriodId'}, {name: 'periodName'}, {name: 'fromDate'}, {name: 'thruDate'}];
		createJqxDropDownList(globalVar.salesYearCustArr, $("#yearCustomTime"), "customTimePeriodId", "periodName", 25, '93%');
		createJqxDropDownListBinding($("#quarterCustomTime"), datafield, "", "listCustomTimePeriod", "customTimePeriodId", "periodName", "98%", 25, quarterCustLoadComplete);
		createJqxDropDownListBinding($("#monthCustomTime"), datafield, "", "listCustomTimePeriod", "customTimePeriodId", "periodName", "98%", 25, monthCustLoadComplete);
	};
	
	var getSalesBonusPolicy = function(customTimePeriodId){
		$("#saleBonusPolicyGrid").jqxGrid({disabled: true});
		$("#saleBonusPolicyGrid").jqxGrid('showloadelement');
		var localdata = [];
		$.ajax({
			url: 'getDistributorBonusPolicyByCustomTimePeriod',
			data:{customTimePeriodId: customTimePeriodId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					var listReturn = response.listReturn;
					for(var i = 0; i < listReturn.length; i++){
						localdata.push({
							isSelected: false, salesBonusPolicyId: listReturn[i].salesBonusPolicyId, 
							salesBonusPolicyName: listReturn[i].salesBonusPolicyName,
							fromDate: listReturn[i].fromDate.time,
							thruDate: listReturn[i].thruDate? listReturn[i].thruDate.time: null,
									
						}); 
					}
				}else{
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#saleBonusPolicyGrid").jqxGrid({disabled: false});
				$("#saleBonusPolicyGrid").jqxGrid('hideloadelement');
				updateGridLocalData($("#saleBonusPolicyGrid"), localdata)
			}
		});
	};
	var initGrid = function(){
		var datafield = [{name: 'salesBonusPolicyId', type: 'string'},
		                 {name: 'salesBonusPolicyName', type: 'string'},
		                 {name: 'isSelected', type: 'bool'},
		                 {name: 'fromDate', type: 'date'},
		                 {name: 'thruDate', type: 'date'},
		                 ];
		var columns = [{ text: '', datafield: 'isSelected', columntype: 'checkbox', width: '5%', editable: true,
							cellbeginedit: function (row, datafield, columntype) {
								var rows = $("#saleBonusPolicyGrid").jqxGrid('getrows');
								for(var i = 0; i < rows.length; i++){
									$("#saleBonusPolicyGrid").jqxGrid('setcellvalue', i, 'isSelected', false); 
								}
							}
					   },
		               {text: uiLabelMap.BonusPolicyNameShort, datafield: 'salesBonusPolicyName', width: '35%', editable: false},
		               {text: uiLabelMap.EffectiveFromDate, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', width: '30%', editable: false,},
					   {text: uiLabelMap.CommonThruDate, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', width: '30%',editable: false,}
		               ];
		var grid = $("#saleBonusPolicyGrid");
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "saleBonusPolicyGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BonusPolicyNameAppl + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
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
				source: {
					localdata: [],
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initGrid();
		};
		createJqxWindow($("#popupAddRow"), 510, 400, initContent);
	};
	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
			rules: [
					{input : '#salesBonusSummaryName', message : uiLabelMap.FieldRequired, action : 'blur',
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#monthCustomTime', message : uiLabelMap.FieldRequired, action : 'blur',
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#quarterCustomTime', message : uiLabelMap.FieldRequired, action : 'blur',
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#yearCustomTime', message : uiLabelMap.FieldRequired, action : 'blur',
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
				]
		});
	};
	var initEvent = function(){
		$("#yearCustomTime").on('select', function(event){
			var args = event.args;
			if (args) {
				var item = args.item;
				var customTimePeriodId = item.value;
				$("#quarterCustomTime").jqxDropDownList({disabled: true});
				updateJqxDropDownListBinding($("#quarterCustomTime"), "getCustomTimePeriodByParent?parentPeriodId=" + customTimePeriodId);
			}
		});
		$("#quarterCustomTime").on('select', function(event){
			var args = event.args;
			if (args) {
				var item = args.item;
				var customTimePeriodId = item.value;
				$("#monthCustomTime").jqxDropDownList({disabled: true});
				updateJqxDropDownListBinding($("#monthCustomTime"), "getCustomTimePeriodByParent?parentPeriodId=" + customTimePeriodId);
			}
		});
		$("#popupAddRow").on('open', function(event){
			var date = new Date();
			var year = date.getFullYear();
			for(var i = 0; i < globalVar.salesYearCustArr.length; i++){
				if(year == globalVar.salesYearCustArr[i].year){
					$("#yearCustomTime").val(globalVar.salesYearCustArr[i].customTimePeriodId);
					break;
				}
			}
		});
		$("#popupAddRow").on('close', function(event){
			updateGridLocalData($("#saleBonusPolicyGrid"), []);
			$("#monthCustomTime").jqxDropDownList('clearSelection');
			$("#quarterCustomTime").jqxDropDownList('clearSelection');
			$("#yearCustomTime").jqxDropDownList('clearSelection');
			$("#salesBonusSummaryName").val("");
		});
		$("#monthCustomTime").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var label = item.label;
				var yearSelected = $("#yearCustomTime").jqxDropDownList('getSelectedItem');
				setSalesBonusSummaryName(label, yearSelected.label);
				getSalesBonusPolicy(item.value)
			}
		});
		$("#cancelAdd").click(function(event){
			$("#popupAddRow").jqxWindow('close');
		});
		$("#saveAdd").click(function(event){
			var valid = $("#popupAddRow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var rows = $("#saleBonusPolicyGrid").jqxGrid('getrows');
			var isSelected = false;
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
					message = uiLabelMap.SalesBonusPolicyIsNotSelected;
				}else{
					message = uiLabelMap.SalesBonusPolicyIsNotCreated;
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
			bootbox.dialog(uiLabelMap.HrCreateNewConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createDistributorBonusSummary();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	var createDistributorBonusSummary = function(){
		var data = {};
		data.salesBonusSummaryName = $("#salesBonusSummaryName").val();
		data.customTimePeriodId = $("#monthCustomTime").val();
		var rows = $("#saleBonusPolicyGrid").jqxGrid('getrows');
		for(var i = 0; i < rows.length; i++){
			var isSelected = rows[i].isSelected;
			if(isSelected){
				data.salesBonusPolicyId = rows[i].salesBonusPolicyId;
				break;
			}
		}
		$("#loadingCreate").show();
		$("#cancelAdd").attr("disabled", "disabled");
		$("#saveAdd").attr("disabled", "disabled");
		$.ajax({
			url: 'createDistributorBonusSummary',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#popupAddRow").jqxWindow('close');
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
				$("#loadingCreate").hide();
				$("#cancelAdd").removeAttr("disabled");
				$("#saveAdd").removeAttr("disabled");
			}
		});
	};
	var setSalesBonusSummaryName = function(month, year){
		var name = uiLabelMap.ViewSummarizingBonusDistributor + " " + month + " - " + year;
		$("#salesBonusSummaryName").val(name);
	};
	return{
		init: init
	}
}());

var contextMenuObj = (function(){
	var init = function(){
		initContextMenu();
	};
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 210);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "updateData"){
            	$("#jqxgrid").jqxGrid({disabled: true});
            	$("#jqxgrid").jqxGrid('showloadelement');
            	$.ajax({
            		url: 'updateSalesBonusSummaryData',
            		data: {salesBonusSummaryId: dataRecord.salesBonusSummaryId},
            		type: 'POST',
            		success: function(response){
            			if(response._EVENT_MESSAGE_){
        					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose: true,
        						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
        					$("#jqxgrid").jqxGrid('updatebounddata');
        				}else{
        					Grid.renderMessage('jqxgrid', response._ERROR_MESSAGE_, {autoClose: true,
        						template : 'error', appendContainer: "#containerjqxgrid", opacity : 0.9});
        				}
            		},
            		complete: function(jqXHR, textStatus){
        				$("#jqxgrid").jqxGrid({disabled: false});
                    	$("#jqxgrid").jqxGrid('hideloadelement');
        			}
            	});
            }
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	summarizingDistributorObj.init();
	contextMenuObj.init();
});