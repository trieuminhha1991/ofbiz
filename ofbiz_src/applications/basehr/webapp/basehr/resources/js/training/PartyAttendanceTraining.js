var partyAttendanceTrainingObj = (function(){
	var init = function(){
		initGrid();
	};
	var initGrid = function(){
		var grid = $("#partyAttdanceGrid");
		var datafield = [{name: 'trainingCourseId', type: 'string'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'firstName', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'},
		                 {name: 'isExpectedAttend', type: 'bool'},
		                 {name: 'isRegister', type: 'bool'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'resultTypeId', type: 'string'},
		                 {name: 'comment', type: 'string'},
		                 {name: 'employeeAmount', type: 'number'},
		                 {name: 'employerAmount', type: 'number'},
		                 {name: 'employeePaid', type: 'number'},
		                 {name: 'employeeAmountRemain', type: 'number'},
		                 ];
		var columns = [{text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '11%'},
					   {text: uiLabelMap.EmployeeName, datafield: 'firstName', width: '16%', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var rowData = grid.jqxGrid('getrowdata', row);
								if(rowData){
									return '<span>' + rowData.fullName + '</span>';
								}
							}
		               },
		               {text: uiLabelMap.CommonDepartment, datafield: 'groupName', width: '17%'},
					   {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '16%'},
					   {text: uiLabelMap.HRExpectedAttend, datafield:'isExpectedAttend', width: '12%',  columntype: 'checkbox'},
					   {text: uiLabelMap.HRCommonRegisted, datafield:'isRegister', width: '10%',  columntype: 'checkbox'},
					   {text: uiLabelMap.HRCommonResults, datafield: 'resultTypeId', width: '8%', columntype: 'dropdownlist', filtertype: 'checkedlist',
						   cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.trainingResultTypeArr.length; i++){
									if(value == globalVar.trainingResultTypeArr[i].resultTypeId){
										return '<span>' + globalVar.trainingResultTypeArr[i].description + '</span>'; 
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								   var source = {
									        localdata: globalVar.trainingResultTypeArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'resultTypeId'});
								    if(dataSoureList.length > 8){
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }
							},
					   },
					   {text: uiLabelMap.HRCommonComment, datafield: 'comment', width: '20%',
						   cellsrenderer: function (row, column, value, defaulthtml, columnproperties) {
							   return defaulthtml;
						   }
					   },
					   {text: uiLabelMap.TrainingAmountEmployeeMustPaid, datafield: 'employeeAmount', columntype: 'numberinput', 
						   filtertype: 'number', width: '11%',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
								}
							},
					   },
					   {text: uiLabelMap.AmountCompanySupport, datafield: 'employerAmount', columntype: 'numberinput', 
						   filtertype: 'number', width: '11%',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							   if(typeof(value) == 'number'){
								   return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
							   }
						   },
					   },
					   {text: uiLabelMap.TrainingAmountEmployeePaid, datafield: 'employeePaid', columntype: 'numberinput', 
						   filtertype: 'number', width: '11%',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							   if(typeof(value) == 'number'){
								   return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
							   }
						   },
					   },
					   {text: uiLabelMap.HRCommonRemain, datafield: 'employeeAmountRemain', columntype: 'numberinput', 
						   filtertype: 'number', width: '11%',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							   if(typeof(value) == 'number'){
								   return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
							   }
						   },
					   },
		               ];
		
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "partyAttdanceGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.ListEmplAttendanceTraining + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        if(globalVar.isEditable){
	        	Grid.createAddRowButton(
	        			grid, container, uiLabelMap.CommonAddNew, {
	        				type: "popup",
	        				container: $("#AddNewPartyAttWindow"),
	        			}
	        	);
	        	Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, null, "", uiLabelMap.CannotDeleteRow, 
	        			uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
	        }
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		
		var config = {
				url: 'JQGetListEmployeeAttendanceTraining&trainingCourseId=' + globalVar.trainingCourseId,
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				autorowheight: true,
				selectionmode: 'singlerow',
				localization: getLocalization(),
				source:{
					removeUrl:"deleteTrainingPartyAttendance",
					deleteColumns:"trainingCourseId;partyId",
					deletesuccessfunction: function(){
						$("#" + globalVar.addWindowId).trigger("addPartyToTrainingSuccess");
					}
					
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	}
	return{
		init: init
	}
}());
$(document).ready(function(){
	$.jqx.theme = 'olbius';
	partyAttendanceTrainingObj.init();
});