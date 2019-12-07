var emplListInKPIAssessment = (function(){
	var _perfCriteriaAssessmentId = "";
	//var _partySelected = {};
	var init = function(){
		initJqxGrid();
		initContextMenu();
		initJqxWindow();
		initJqxPanel();
		initEvent();
		initJqxNotification();
	};
	var initJqxNotification = function(){
		$("#jqxNotificationemplListInKPIAssessGrid").jqxNotification({ width: "100%", appendContainer: "#containeremplListInKPIAssessGrid", opacity: 0.9, template: "info" });
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'perfCriteriaAssessmentId', type: 'string'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'point', type: 'number'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'perfCriteriaRateGradeId', type: 'string'},
		                 {name: 'perfCriteriaRateGradeName', type: 'string'},
		                 {name: 'salaryRate', type: 'number'},
		                 {name: 'allowanceRate', type: 'number'},
		                 {name: 'bonusAmount', type: 'number'},
		                 {name: 'punishmentAmount', type: 'number'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'customTimePeriodId', type: 'string'},
		                 {name: 'yearCustomTimePeriodId', type: 'string'},
		                 {name: 'periodName', type: 'string'}
		];
		var columns = [{datafield: 'perfCriteriaAssessmentId', hidden: true},
		               {datafield: 'perfCriteriaRateGradeId', hidden: true},
		               {datafield: 'customTimePeriodId', hidden: true},
		               {datafield: 'yearCustomTimePeriodId', hidden: true},
		               {datafield: 'point', hidden: true},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '15%'},
		               {text: uiLabelMap.EmployeeId, datafield: 'partyId', width: '13%'},
		               {text: uiLabelMap.KPIRating, datafield: 'perfCriteriaRateGradeName', width: '13%',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		            		   var data = $("#emplListInKPIAssessGrid").jqxGrid('getrowdata', row);
		            		   if(data && typeof(data.point) == 'number'){
		            			   return '<span>' + value + ' ('+ data.point +')</span>';
		            		   }else{
		            			   return '<span>' + value + '</span>';
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.SalaryRate, datafield: 'salaryRate', width: '15%', cellsalign: 'right', 
		            	   columntype: 'numberinput', filtertype: 'number', editable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   if(typeof(value) == 'number'){
								   return '<span class="align-right">' + value + '%<span>'; 
							   }
							   return '<span class="align-right">' + value + '<span>';
						   },
		               },
		               {text: uiLabelMap.AllowanceRate, datafield: 'allowanceRate', width: '15%', cellsalign: 'right', 
					   		columntype: 'numberinput', filtertype: 'number', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   if(typeof(value) == 'number'){
								   return '<span class="align-right">' + value + '%<span>'; 
							   }
							   return '<span class="align-right">' + value + '<span>';
						   }
					   },
					   {text: uiLabelMap.HRCommonBonus, datafield: 'bonusAmount', width: '12%', cellsalign: 'right', 
					   		columntype: 'numberinput', filtertype: 'number', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
						   }
					   },
					   {text: uiLabelMap.HRPunishmentAmount, datafield: 'punishmentAmount', width: '13%', cellsalign: 'right', 
					   		columntype: 'numberinput', filtertype: 'number', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
						   }
					   },
					   {text: uiLabelMap.PaymentPeriod, datafield: 'periodName', width: '16%'},
					   {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '12%', columntype: 'dropdownlist',
						   filtertype: 'checkedlist', 
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   for(var i = 0; i < globalVar.statusArr.length; i++){
									if(value == globalVar.statusArr[i].statusId){
										return '<span>' + globalVar.statusArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
						   },
						   createfilterwidget: function(column, columnElement, widget){
							   var source = {
								        localdata: globalVar.statusArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
						},
					   }
		];
		var grid = $("#emplListInKPIAssessGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "emplListInKPIAssessGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmplListInPerfCriteriaAssessment + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader); 
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        if(globalVar.updatePermission){
	        	Grid.createAddRowButton(
	        			grid, container, uiLabelMap.CommonAddNew, {
	        				type: "popup",
	        				container: $("#popupWindowEmplList"),
	        			}
	        	);
	        }
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
	        Grid.createContextMenu(grid, $("#contextMenuKPIAssess"), false);
	        Grid.addContextMenuHoverStyle(grid, $("#contextMenuKPIAssess"));
	        if(globalVar.deletePermission){ 
	        	Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
	        			"", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
	        }
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				source: {
					pagesize : 10,
					removeUrl: "jqxGeneralServicer?jqaction=D&sname=deletePerfCriteriaAssessmentParty", 
					deleteColumns: "perfCriteriaAssessmentId;partyId"
				},
				localization: getLocalization(),
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		
	};
	var initContextMenu = function(){
		var liElement = $("#contextMenuKPIAssess>ul>li").length;
		var contextMenuHeight = 30 * liElement;
		$("#contextMenuKPIAssess").jqxMenu({ width: 220, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup', popupZIndex: 22000});
		$("#contextMenuKPIAssess").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#emplListInKPIAssessGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#emplListInKPIAssessGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "detailsOfReviewPoint"){
            	emplKPIAssessmentDetails.setData(dataRecord);//emplKPIAssessmentDetails is defined in ViewEmplKPIAssessmentDetails.js
            	emplKPIAssessmentDetails.openWindow();
            }else if(action == "editReviewPoint"){
            	editEmplKPIAssessmentObj.setData(dataRecord);
            	editEmplKPIAssessmentObj.openWindow();
            }
		});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#emplListInKPIAssessmentWindow"), 950, 580);
		$("#emplListInKPIAssessmentWindow").on('close', function(event){
			refreshBeforeReloadGrid($("#emplListInKPIAssessGrid"));
			_perfCriteriaAssessmentId = "";
			
			$("#jqxNotificationemplListInKPIAssessGrid").jqxNotification('closeLast');
		});
		$("#emplListInKPIAssessmentWindow").on('open', function(event){
			var source = $("#emplListInKPIAssessGrid").jqxGrid('source');
			source._source.url = "jqxGeneralServicer?sname=JQGetPerfCriteriaAssessmentParty&perfCriteriaAssessmentId=" + _perfCriteriaAssessmentId;;
			$("#emplListInKPIAssessGrid").jqxGrid('source', source);
		});
	};
	
	var initJqxPanel = function(){
		$("#emplListInKPIAssessPanel").jqxPanel({width: '100%', height: 525, scrollBarSize: 15})
	};
	var openWindow = function(){
		openJqxWindow($("#emplListInKPIAssessmentWindow"));
	};
	var setData = function(data){
		_perfCriteriaAssessmentId = data.perfCriteriaAssessmentId;
	};
	var initEvent = function(){
		$("#emplListInKPIAssessGrid").on('rowdoubleclick', function (event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#emplListInKPIAssessGrid").jqxGrid('getrowdata', boundIndex);
			editEmplKPIAssessmentObj.setData(data);
			editEmplKPIAssessmentObj.openWindow();
		});
	}; 
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());

$(document).ready(function(){
	emplListInKPIAssessment.init();
	//editEmplKPIAssessmentObj.init();
});