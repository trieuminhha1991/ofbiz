var viewEmplAllowanceObj = (function(){
	var init = function(){
		for(var i = 0; i < globalVar.allowanceParamPeriodArr.length; i++){
			initEvent(globalVar.allowanceParamPeriodArr[i]);
		}
		$("#jqxNotificationjqxgrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerjqxgrid"});
		$("#jqxNotificationviewEmplAllowanceGrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerviewEmplAllowanceGrid"});
	};
	var initEvent = function(periodTypeId){
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		var date = new Date();
		$("#grid" + periodTypeId).on('loadCustomControlAdvance', function(event){
			var fromDate = null;
			var thruDate = null;
			if(periodTypeId == "YEARLY"){
				$("#year_" + periodTypeId).jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
				$("#year_" + periodTypeId).val(date.getFullYear());
				fromDate = new Date(date.getFullYear(), 0, 1);
				thruDate = new Date(date.getFullYear(), 11, 31);
				$("#year_" + periodTypeId).on('valueChanged', function(event){
					var year = event.args.value;
					var tempFromDate = new Date(year, 0, 1);
					var tempThruDate = new Date(year, 11, 31);
					reloadData(tempFromDate, tempThruDate, "grid" + periodTypeId, periodTypeId);
				});
			}else if(periodTypeId == "DAILY"){
				$("#date_" + periodTypeId).jqxDateTimeInput({width: 200, height: 25, selectionMode: 'range' });
				fromDate = new Date(date.getFullYear(), date.getMonth(), 1);
				thruDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
				$("#date_" + periodTypeId).jqxDateTimeInput('setRange', fromDate, thruDate);
				$("#date_" + periodTypeId).on('change', function(event){
					var selection = $("#date_" + periodTypeId).jqxDateTimeInput('getRange');
					reloadData(selection.from, selection.to, "grid" + periodTypeId, periodTypeId);    
				});
			}else{
				$("#year_" + periodTypeId).jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
				createJqxDropDownList(monthData, $("#month_" + periodTypeId), "month", "description", 25, 90);
				$("#month_" + periodTypeId).jqxDropDownList({selectedIndex: date.getMonth()});
				$("#year_" + periodTypeId).val(date.getFullYear());
				fromDate = new Date(date.getFullYear(), date.getMonth(), 1);
				thruDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
				$("#month_" + periodTypeId).on('select', function(event){
					var args = event.args;
					if (args) {
						var month = args.item.value;
						var year = $("#year_" + periodTypeId).val();
						var tempFromDate = new Date(year, month, 1);
						var tempThruDate = new Date(year, parseInt(month) + 1, 0);
						reloadData(tempFromDate, tempThruDate, "grid" + periodTypeId, periodTypeId);
					}
				});
				$("#year_" + periodTypeId).on('valueChanged', function(event){
					var year = event.args.value;
					var month = $("#month_" + periodTypeId).val();
					var tempFromDate = new Date(year, month, 1);
					var tempThruDate = new Date(year, parseInt(month) + 1, 0);
					reloadData(tempFromDate, tempThruDate, "grid" + periodTypeId, periodTypeId);
				});
			}
			reloadData(fromDate, thruDate, "grid" + periodTypeId, periodTypeId);
		});
	};
	var reloadData = function(fromDate, thruDate, gridId, periodTypeId){
		var source = $("#" + gridId).jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListEmplAllowance&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime() + "&periodTypeId=" + periodTypeId;
		$("#" + gridId).jqxGrid('source', source);
	};
	return{
		init: init
	}
}());
var contextMenuObj = (function(){
	var init = function(){
		createJqxMenu("contextMenu", 30, 120);
		initEvent();
	};
	var initEvent = function(){
		$("#contextMenu").on('itemclick', function(event){
			for(var i = 0; i < globalVar.allowanceParamPeriodArr.length; i++){
				var periodTypeId = globalVar.allowanceParamPeriodArr[i];
				if($("#grid" + periodTypeId)){
					var rowSelectedIndex = $("#grid" + periodTypeId).jqxGrid('getselectedrowindex');
					if(rowSelectedIndex > -1){
						var dataRecord = $("#grid" + periodTypeId).jqxGrid('getrowdata', rowSelectedIndex);
						viewDetailEmplAllowance(dataRecord.partyCode, dataRecord.fullName, periodTypeId);
						return;
					}
				}
			}
		});
	};
	var viewDetailEmplAllowance = function(partyCode, fullName, periodTypeId){
		openJqxWindow($("#viewEmplAllowanceWindow"));
		var fromDate, thruDate = null;
		if(periodTypeId == "DAILY"){
			var selection = $("#date_" + periodTypeId).jqxDateTimeInput('getRange');
			fromDate = selection.from;
			thruDate = selection.to;
		}else if(periodTypeId == "MONTHLY"){
			var month = $("#month_" + periodTypeId).val();
			var year = $("#year_" + periodTypeId).val();
			fromDate = new Date(year, month, 1);
			thruDate = new Date(year, parseInt(month) + 1, 0);
		}else if(periodTypeId == "YEARLY"){
			var year = $("#year_" + periodTypeId).val();
			fromDate = new Date(year, 0, 1);
			thruDate = new Date(year, 11, 31);
		}
		viewEmplAllowanceDetailObj.updateData(partyCode, fullName, fromDate, thruDate);
	};
	return{
		init: init
	}
}());
var viewEmplAllowanceDetailObj = (function(){
	var init = function(){
		initGrid();
		initSimpleInput();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'code', type: 'string'},
		                 {name: 'orgId', type: 'string'},
		                 {name: 'name', type: 'string'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'periodTypeId', type: 'string'},
		                 {name: 'fromDate', type: 'date'},
		                 {name: 'thruDate', type: 'date'},
		                 ];
		var columns = [{text: uiLabelMap.HREmplAllowances, datafield: 'name', width: '25%', editable: false},
		               {text: uiLabelMap.HRCommonAmount, datafield: 'amount', width: '15%', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
								editor.jqxNumberInput({ width: cellwidth, height: cellheight, spinButtons: true, inputMode: 'advanced'});
							},
							geteditorvalue: function (row, cellvalue, editor) {
								return editor.val();
							}
		               },
		               {text: uiLabelMap.CommonPeriodType, datafield: 'periodTypeId', width: '20%', editable: false, columntype: 'dropdownlist',
							filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value) {
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
		               {text: uiLabelMap.EffectiveFromDate, datafield: 'fromDate', width: '20%', cellsformat: 'dd/MM/yyyy', filtertype: 'range',editable: false},
		               {text: uiLabelMap.CommonThruDate, datafield: 'thruDate', width: '20%', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: true, 
		            	   columntype: 'datetimeinput',
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		            		  editor.jqxDateTimeInput({width: cellwidth, height: cellheight, showFooter: true});
		            	   },
		            	   validation: function (cell, value) {
		            		   var rowIndex = cell.row;
		            		   var rowData = $("#viewEmplAllowanceGrid").jqxGrid('getrowdata', rowIndex);
		            		   var fromDate = rowData.fromDate;
		            		   if(value && fromDate > value){
		            				  return { result: false, message: uiLabelMap.ThruDateMustBeAfterFromDate };
		            		   }
		            		   return true;
		            	   }
		               }
		               ];
		
		var grid = $("#viewEmplAllowanceGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "viewEmplAllowanceGrid";
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.ViewListAllowanceOfEmpl + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
		   		width: '100%', 
		   		virtualmode: true,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: false,
		        filterable: true,
		        editable: true,
		        url: '',   
	   			showtoolbar: true,
	   			rendertoolbar: rendertoolbar,
	   			source:{
	   				pagesize: 5,
	   				updateUrl : "jqxGeneralServicer?jqaction=U&sname=updateEmplPayrollParameters",
		   			editColumns : "orgId;partyId;code;amount;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)",
	   			}
		 };
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initSimpleInput = function(){
		$("#partyCodeView").jqxInput({width: '80.5%', height: 20, disabled: true});
		$("#fullNameView").jqxInput({width: '93%', height: 20, disabled: true});
	};
	var initWindow = function(){
		createJqxWindow($("#viewEmplAllowanceWindow"), 700, 420);
	};
	var initEvent = function(){
		$("#viewEmplAllowanceWindow").on('close', function(event){
			refreshBeforeReloadGrid($("#viewEmplAllowanceGrid"));
			$("#partyCodeView").val("");
			$("#fullNameView").val("");
		});
	};
	var updateData = function(partyCode, fullName, fromDate, thruDate){
		$("#partyCodeView").val(partyCode);
		$("#fullNameView").val(fullName);
		var source = $("#viewEmplAllowanceGrid").jqxGrid('source');
		source._source.url = 'jqxGeneralServicer?sname=JQGetListAllowanceOfEmpl&partyCode=' + partyCode;
		$("#viewEmplAllowanceGrid").jqxGrid('source', source);
	}; 
	return{
		init: init,
		updateData: updateData
	}
}());
$(document).ready(function(){
	viewEmplAllowanceObj.init();
	contextMenuObj.init();
	viewEmplAllowanceDetailObj.init();
});