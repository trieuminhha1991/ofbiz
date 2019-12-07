/*maybe delete*/
var trainingPartyAttObj = (function(){
	var trainingCourseId;
	var init = function(){
		initJqxGrid();
		initJqxInput();
		initJqxDropDownList();
		initJqxDateTimeInput();
		initJqxGridSearchEmpl();
		initJqxWindowEvent();
		initBtnEvent();
		initJqxValidator();
		initJqxWindow();
		initJqxNotification();
	};
	var initJqxNotification = function(){
		$("#jqxNotificationpartyAttendance" + globalVar.editPartyAttTraining).jqxNotification({
	        width: "100%", position: "top-left", opacity: 1, appendContainer: "#containerpartyAttendance" + globalVar.editPartyAttTraining,
	        autoOpen: false, autoClose: true
	    });
	};
	var initJqxValidator = function(){
		$("#addPartyAttendanceWindow" + globalVar.editPartyAttTraining).jqxValidator({
			rules:[
			    { input: '#party' + globalVar.editPartyAttTraining, message: uiLabelMap.FieldRequired, action: 'keyup, blur', 
			    	rule: function (input, commit){
			    		if(!input.val()){
			    			return false;
			    		}
			    		return true;
			    	}
			    },
			]
		});
	};
	
	var initJqxInput = function(){
		$("#party" + globalVar.editPartyAttTraining).jqxInput({height: 20, width: '87%', valueMember: 'partyId', displayMember: 'partyName', disabled: true});
	};
	
	var initBtnEvent = function(){
		$("#searchBtn" + globalVar.editPartyAttTraining).click(function(event){
			openJqxWindow($("#windowEmplList" + globalVar.editPartyAttTraining));
		});
		
		$("#alterCancelPartyAtt").click(function(event){
			$("#addPartyAttendanceWindow" + globalVar.editPartyAttTraining).jqxWindow('close');
		});
		$("#alterSavePartyAtt").click(function(event){
			var valid = $("#addPartyAttendanceWindow" + globalVar.editPartyAttTraining).jqxValidator('validate');
			if(!valid){
				return;
			}
			createTrainingCoursePartyAttendance();
			$("#addPartyAttendanceWindow" + globalVar.editPartyAttTraining).jqxWindow('close');
		});
	};
	
	var createTrainingCoursePartyAttendance = function(){
		var row = {};
		row.trainingCourseId = trainingCourseId;
		row.partyId = $("#party" + globalVar.editPartyAttTraining).val().value;
		row.employeeAmount = $("#employeeAmount" + globalVar.editPartyAttTraining).val();
		row.employerAmount = $("#employerAmount" + globalVar.editPartyAttTraining).val();
		row.employeePaid = $("#employeePaid" + globalVar.editPartyAttTraining).val();
		row.resultTypeId = $("#resultTypeId" + globalVar.editPartyAttTraining).val();
		row.statusId = $("#statusId" + globalVar.editPartyAttTraining).val();
		$("#partyAttendance" + globalVar.editPartyAttTraining).jqxGrid('addrow', null, row, 'first');
	};
	
	var initJqxWindowEvent = function(){
		$('#windowEmplList' + globalVar.editPartyAttTraining).on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList" + globalVar.editPartyAttTraining).jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList" + globalVar.editPartyAttTraining)[0]);
				$('#jqxTreeEmplList' + globalVar.editPartyAttTraining).jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList" + globalVar.editPartyAttTraining)[0]);
			}
		});
		$('#windowEmplList' + globalVar.editPartyAttTraining).on('close', function(event){
			$("#EmplListInOrg" + globalVar.editPartyAttTraining).jqxGrid('clearselection');
		});
	};
	
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg" + globalVar.editPartyAttTraining), {uiLabelMap: uiLabelMap});
		$("#EmplListInOrg" + globalVar.editPartyAttTraining).on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#EmplListInOrg" + globalVar.editPartyAttTraining).jqxGrid('getrowdata', boundIndex);
		    $("#party" + globalVar.editPartyAttTraining).jqxInput('val', {value: data.partyId, label: data.partyName});
		    $('#windowEmplList' + globalVar.editPartyAttTraining).jqxWindow('close');
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.trainingResultTypeArr, $("#resultTypeId" + globalVar.editPartyAttTraining), "resultTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.statusRegisterArr, $("#statusId" + globalVar.editPartyAttTraining), "statusId", "description", 25, '98%');
	};
	
	var initJqxDateTimeInput = function(){
		$("#employeeAmount" + globalVar.editPartyAttTraining).jqxNumberInput({ width: '98%', height: 25, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999});
		$("#employerAmount" + globalVar.editPartyAttTraining).jqxNumberInput({ width: '98%', height: 25, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999});
		$("#employeePaid" + globalVar.editPartyAttTraining).jqxNumberInput({ width: '98%', height: 25, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999});
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'trainingCourseId', type: 'string'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'employeeAmount', type: 'number'},
		                 {name: 'employerAmount', type: 'number'},
		                 {name: 'employeePaid', type: 'number'},
		                 {name: 'resultTypeId', type: 'string'}];
		
		var columns = [{datafield: 'trainingCourseId', hidden: true},
		               {datafield: 'partyId', hidden: true},
		               {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '15%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '18%', editable: false},
		               {text: uiLabelMap.TrainingResult, datafield: 'resultTypeId', width: '15%', columntype: 'dropdownlist',
		            	   cellsrenderer: function (row, column, value){
		            		   for(var i = 0; i < globalVar.trainingResultTypeArr.length; i++){
		            			   if(globalVar.trainingResultTypeArr[i].resultTypeId == value){
		            				   return '<span>' + globalVar.trainingResultTypeArr[i].description +'</span>';
		            			   }
		            		   }
		            		   return '<span>' + value +'</span>';
		            	   },
		            	   createEditor: function (row, cellvalue, editor, cellText, width, height) {
		            		   createJqxDropDownList(globalVar.trainingResultTypeArr, editor, "resultTypeId", "description", height, width);
		            	   },
		            	   initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
		               },
		               {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '15%', columntype: 'dropdownlist',
		            	   cellsrenderer: function (row, column, value){
		            		   for(var i = 0; i < globalVar.statusRegisterArr.length; i++){
		            			   if(globalVar.statusRegisterArr[i].statusId == value){
		            				   return '<span>' + globalVar.statusRegisterArr[i].description +'</span>';
		            			   }
		            		   }
		            		   return '<span>' + value +'</span>';
		            	   },
		            	   createEditor: function (row, cellvalue, editor, cellText, width, height) {
		            		   createJqxDropDownList(globalVar.statusRegisterArr, editor, "statusId", "description", height, width);
		            	   },
		            	   initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
		            	   },
		               },
		               {text: uiLabelMap.TrainingAmountEmployeeMustPaid, datafield: 'employeeAmount', width: '15%', columntype: 'numberinput',
			               createEditor: function (row, cellvalue, editor, cellText, width, height) {
			            	   editor.jqxNumberInput({ width: width, height: height, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999});
			               },
			               initEditor: function (row, cellvalue, editor, celltext, width, height) {
								if(typeof(cellvalue) != 'undefined' && cellvalue != null){
									editor.val(cellvalue);
								}
		            	   },
		               },
		               {text: uiLabelMap.AmountCompanySupport, datafield: 'employerAmount', width: '15%', columntype: 'numberinput',
			               createEditor: function (row, cellvalue, editor, cellText, width, height) {
			            	   editor.jqxNumberInput({ width: width, height: height, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999});
			               },    
			               initEditor: function (row, cellvalue, editor, celltext, width, height) {
			            	   if(typeof(cellvalue) != 'undefined' && cellvalue != null){
			            		   editor.val(cellvalue);
			            	   }
		            	   },
		               },
		               {text: uiLabelMap.TrainingAmountEmployeePaid, datafield: 'employeePaid', width: '15%', columntype: 'numberinput',
			               createEditor: function (row, cellvalue, editor, cellText, width, height) {
			            	   editor.jqxNumberInput({ width: width, height: height, min: 0,  spinButtons: false,decimalDigits: 0, digits: 9, max: 999999999});
			               },   
			               initEditor: function (row, cellvalue, editor, celltext, width, height) {
			            	   if(typeof(cellvalue) != 'undefined' && cellvalue != null){
			            		   editor.val(cellvalue);
			            	   }
		            	   },
		               },
		               ];
		var grid = $("#partyAttendance" + globalVar.editPartyAttTraining);
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "partyAttendance" + globalVar.editPartyAttTraining;
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        	grid, container, uiLabelMap.CommonAddNew, {
	        		type: "popup",
	        		container: $("#addPartyAttendanceWindow" + globalVar.editPartyAttTraining),
	        	}
	        );
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, null, "", uiLabelMap.CannotDeleteRow, 
	        		uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var source = {
				pagesize: 15,
				createUrl: 'jqxGeneralServicer?jqaction=C&sname=createTrainingPartyAttendance',
				addColumns: "partyId;trainingCourseId;statusId;resultTypeId;employeeAmount(java.math.BigDecimal);employerAmount(java.math.BigDecimal);employeePaid(java.math.BigDecimal)",
				updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateTrainingPartyAttendance",
				editColumns: "partyId;trainingCourseId;resultTypeId;statusId;employeeAmount(java.math.BigDecimal);employerAmount(java.math.BigDecimal);employeePaid(java.math.BigDecimal)",
				removeUrl: "jqxGeneralServicer?jqaction=D&sname=deleteTrainingPartyAttendance",
				deleteColumns: "partyId;trainingCourseId"
		};
		var config = {
				width: '100%', 
				height: 490,
				autoheight: false,
				virtualmode: true,
				showfilterrow: false,
				showtoolbar: true,
				rendertoolbar: rendertoolbar,
				pageable: true,
				sortable: false,
		        filterable: false,
		        editable: true,
		        url: '',
		        source: source
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var setData = function(data){
		trainingCourseId = data.trainingCourseId;
		var tempS = $("#partyAttendance" + globalVar.editPartyAttTraining).jqxGrid('source');
		tempS._source.url = "jqxGeneralServicer?sname=JQListTrainingCoursePartyAttendance&trainingCourseId=" + trainingCourseId;
		$("#partyAttendance" + globalVar.editPartyAttTraining).jqxGrid('source', tempS);
	};
	var openWindow = function(){
		openJqxWindow($("#" + globalVar.editPartyAttTraining));
	};
	
	var initJqxSplitter =  function(){
		$("#splitterEmplList" + globalVar.editPartyAttTraining).jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#" + globalVar.editPartyAttTraining), 800, 550);
		var initContent = function(){
			initJqxSplitter();
		};
		createJqxWindow($("#addPartyAttendanceWindow" + globalVar.editPartyAttTraining), 500, 340);
		createJqxWindow($("#windowEmplList" + globalVar.editPartyAttTraining), 850, 520, initContent);
		$("#" + globalVar.editPartyAttTraining).on('close', function(event){
			var grid = $("#partyAttendance" + globalVar.editPartyAttTraining);
			var tempS = grid.jqxGrid('source');
			tempS._source.url='';
			grid.jqxGrid('gotopage', 0);
			grid.jqxGrid('source', tempS);
			
		});
		$("#addPartyAttendanceWindow" + globalVar.editPartyAttTraining).on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	trainingPartyAttObj.init();	
});