var viewListEmplSalaryBaseObject = (function(){
	var init = function(){
		initEvent();
	};
	
	var initJqxDateTime = function(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 200, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(globalVar.monthStart);
		var thruDate = new Date(globalVar.monthEnd);
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		refreshGridData(fromDate, thruDate);
		$("#dateTimeInput").on('valueChanged', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from;
		    var thruDate = selection.to;
		    refreshGridData(fromDate, thruDate);
		});	
	};
	
	var initEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(event){
			initJqxDateTime();
		});
		
	};
	
	var refreshGridData = function(fromDate, thruDate){	
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=JQListEmplSalaryBaseFlat&hasrequest=Y&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
		$("#jqxgrid").jqxGrid('source', tmpS);
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
			var rowSelectedIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			showPayHistoryDetail(rowSelectedIndex);
		});
	};
	var showPayHistoryDetail = function(rowSelectedIndex){
		if(rowSelectedIndex > -1){
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowSelectedIndex);
			viewDetailEmplSal(dataRecord.partyCode, dataRecord.fullName);
			return;
		}
	};
	var viewDetailEmplSal = function(partyCode, fullName){
		openJqxWindow($("#viewEmplSalWindow"));
		var fromDate, thruDate = null;
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		fromDate = selection.from;
		thruDate = selection.to;
		viewEmplSalaryDetailObj.updateData(partyCode, fullName, fromDate, thruDate);
	};
	
	return{
		init: init,
		showPayHistoryDetail: showPayHistoryDetail
	}
}());

var viewEmplSalaryDetailObj = (function(){
	var init = function(){
		initGrid();
		initSimpleInput();
		initWindow();
		initEvent();
		$("#jqxNotificationviewEmplSalGrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerviewEmplSalGrid"});
	};
	var initGrid = function(){
		var datafield = [{name: 'partyIdTo', type: 'string'},
		                 {name: 'partyIdFrom', type: 'string'},
		                 {name: 'roleTypeIdTo', type: 'string'},
		                 {name: 'roleTypeIdFrom', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'periodTypeId', type: 'string'},
		                 {name: 'fromDate', type: 'date'},
		                 {name: 'thruDate', type: 'date'},
		                 ];
		var columns = [{text: uiLabelMap.SalaryBaseFlat, datafield: 'amount', width: '25%', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
								}
							},
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
								editor.jqxNumberInput({ width: cellwidth, height: cellheight, spinButtons: true, inputMode: 'advanced', min: 0});
							},
							geteditorvalue: function (row, cellvalue, editor) {
								return editor.val();
							}
		               },
		               {text: uiLabelMap.CommonPeriodType, datafield: 'periodTypeId', width: '25%', editable: false, columntype: 'dropdownlist',
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
		               {text: uiLabelMap.EffectiveFromDate, datafield: 'fromDate', width: '25%', cellsformat: 'dd/MM/yyyy', filtertype: 'range',editable: false},
		               {text: uiLabelMap.CommonThruDate, datafield: 'thruDate', width: '25%', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: true, 
		            	   columntype: 'datetimeinput',
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		            		  editor.jqxDateTimeInput({width: cellwidth, height: cellheight, showFooter: true});
		            	   },
		            	   validation: function (cell, value) {
		            		   var rowIndex = cell.row;
		            		   var rowData = $("#viewEmplSalGrid").jqxGrid('getrowdata', rowIndex);
		            		   var fromDate = rowData.fromDate;
		            		   if(value && fromDate > value){
		            				  return { result: false, message: uiLabelMap.ThruDateMustBeAfterFromDate};
		            		   }
		            		   return true;
		            	   }
		               }
		               ];
		
		var grid = $("#viewEmplSalGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "viewEmplSalGrid";
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmployeeSalaryDetails + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
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
	   				updateUrl : "jqxGeneralServicer?jqaction=U&sname=updatePartySalaryBase",
		   			editColumns : "partyIdTo;roleTypeIdTo;partyIdFrom;periodTypeId;roleTypeIdFrom;amount(java.math.BigDecimal);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)",
	   			}
		 };
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initSimpleInput = function(){
		$("#partyCodeView").jqxInput({width: '80.5%', height: 20, disabled: true});
		$("#fullNameView").jqxInput({width: '93%', height: 20, disabled: true});
	};
	var initWindow = function(){
		createJqxWindow($("#viewEmplSalWindow"), 650, 420);
	};
	var initEvent = function(){
		$("#viewEmplSalWindow").on('close', function(event){
			refreshBeforeReloadGrid($("#viewEmplSalGrid"));
			$("#partyCodeView").val("");
			$("#fullNameView").val("");
			$("#jqxgrid").jqxGrid('updatebounddata');
		});
	};
	var updateData = function(partyCode, fullName, fromDate, thruDate){
		$("#partyCodeView").val(partyCode);
		$("#fullNameView").val(fullName);
		var source = $("#viewEmplSalGrid").jqxGrid('source');
		source._source.url = 'jqxGeneralServicer?sname=JQGetListPayHistoryOfEmpl&partyCode=' + partyCode;
		$("#viewEmplSalGrid").jqxGrid('source', source);
	}; 
	return{
		init: init,
		updateData: updateData
	}
}());

$(document).ready(function(){
	viewListEmplSalaryBaseObject.init();
	contextMenuObj.init();
	viewEmplSalaryDetailObj.init();
});

