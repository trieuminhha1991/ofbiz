var addPartyToTrainingObj = (function(){
	var _windowWidth = 450;
	var _partyChooseData = {};
	var init = function(){
		initRegistedGrid();
		initExpectedPartyAttGrid();
		initJqxGridSearchEmpl();
		initDropDownGrid();
		initDropDown();
		initSimpleInput();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerAddEmplTraining"));
	};
	var initJqxEditor = function(){
		$("#commentTraining").jqxEditor({ 
    		width: '98%',
            theme: 'olbiuseditor',
            tools: '',
            height: 100,
        });
	};
	var initWindow = function(){
		createJqxWindow($("#" + globalVar.addWindowId), _windowWidth, 435, initJqxEditor);
		createJqxWindow($("#ListRegistedWindow"), 800, 500);
		createJqxWindow($("#ListExpectedWindow"), 750, 500);
		createJqxWindow($('#popupWindowEmplList'), 900, 560, initJqxSplitter);
	};
	var initDropDown = function(){
		createJqxDropDownList(globalVar.trainingResultTypeArr, $("#partyTrainingResult"), "resultTypeId", "description", 25, '98%');
	};
	var initSimpleInput = function(){
		$("#amountMustPaid").jqxNumberInput({ width: '98%', height: 25, min: 0, 
			spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999, decimal: globalVar.estimatedEmplPaid});
		$("#companySupport").jqxNumberInput({ width: '98%', height: 25, min: 0, 
			spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999, decimal: globalVar.amountCompanySupport});
		$("#amountEmplPaid").jqxNumberInput({ width: '98%', height: 25, min: 0, 
			spinButtons: true, decimalDigits: 0, digits: 9, max: 999999999, decimal: globalVar.estimatedEmplPaid});
	};
	var initDropDownGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'}];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyCode', width : '23%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '30%', editable: false},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '47%', editable: false}];
		var grid = $("#jqxGridGroupEmpl");
   		var rendertoolbar = function (toolbar){
   			toolbar.html("");
   			var id = "jqxGridGroupEmpl";
   			var me = this;
   			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmployeeListSelected + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
   			toolbar.append(jqxheader);
   	     	var container = $('#toolbarButtonContainer' + id);
   	        var maincontainer = $("#toolbarcontainer" + id);
	   	    var str = '<button style="margin-left: 20px;" id="deleterowbutton'+id+'"><i class="icon-trash open-sans"></i><span>'+ uiLabelMap.wgdelete + '</span></button>';
	        container.append(str);
	        var obj = $("#deleterowbutton" + id);
	        obj.jqxButton();
	        obj.click(function(){
	        	var selectedrowindexes = grid.jqxGrid('getselectedrowindexes');
	        	var rowIDs = [];
	        	for(var i = 0; i < selectedrowindexes.length; i++){
	        		var rowid = grid.jqxGrid('getrowid', selectedrowindexes[i]);
	        		rowIDs.push(rowid);
	        	}
	        	grid.jqxGrid('deleterow', rowIDs);
	        	var source = $("#jqxGridGroupEmpl").jqxGrid('source');
	    		var records = source.records;
	    		source._source.localdata = records;
	    		setContentDropDownBtn(records.length + " " + uiLabelMap.EmployeeSelected);
	    		grid.jqxGrid('clearselection');
	        });
   		};               
		var config = {
		   		width: 500, 
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: false,
		   		showfilterrow: false,
		   		selectionmode: 'multiplerows',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
	   			showtoolbar: true,
	   			rendertoolbar : rendertoolbar,
	        	source: {pagesize: 5, id: 'partyId', localdata: []}
		 };
		Grid.initGrid(config, datafield, columns, null, $("#jqxGridGroupEmpl"));
		$("#dropDownButtonGroupEmpl").jqxDropDownButton({width: _windowWidth * 9/16, height: 25});
	};
	var initRegistedGrid = function(){
		var grid = $("#listRegistedGrid");
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'trainingCourseId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'dateRegisted', type: 'date'},
		                 {name: 'isJoined', type: 'bool'},
		                 ];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyCode', width : '16%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '18%', editable: false},
		               {text: uiLabelMap.CommonDepartment, datafield: 'groupName', width: '20%'},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '20%', editable: false},
		               {text: uiLabelMap.DateRegistration, datafield: 'dateRegisted', filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "listRegistedGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.ListEmplRegistedTraining + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		
		var config = {
				url: 'JQGetListEmplRegisAccTraining&trainingCourseId=' + globalVar.trainingCourseId,
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				selectionmode: 'checkbox',
				source: {
					id: 'partyId',
					pagesize: 10
				},
				localization: getLocalization(),
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		createGridEvent(grid);
	};
	var initExpectedPartyAttGrid = function(){
		var grid = $("#listExpectedGrid");
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'trainingCourseId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 ];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyCode', width : '20%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '25%', editable: false},
		               {text: uiLabelMap.CommonDepartment, datafield: 'groupName', width: '25%'},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '26%', editable: false},
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "listExpectedGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmplExpectedAttendance + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: 'JQListTrainingPartyExpectedAtt&trainingCourseId=' + globalVar.trainingCourseId,
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				selectionmode: 'checkbox',
				source: {
					id: 'partyId',
					pagesize: 10
				},
				localization: getLocalization(),
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		createGridEvent(grid);
	};
	var createGridEvent = function(grid){
		grid.on('rowselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(rowData){
				_partyChooseData[rowData.partyId] = rowData;
			}else{
				var datainformation = grid.jqxGrid('getdatainformation');
				var paginginformation = datainformation.paginginformation;
				var pagenum = paginginformation.pagenum;
				var pagesize = paginginformation.pagesize;
				var start = pagenum * pagesize;
				var end = start + pagesize;
				for(var rowIndex = start; rowIndex < end; rowIndex++){
					var data = grid.jqxGrid('getrowdata', rowIndex);
					if(data){
						_partyChooseData[data.partyId] = data;
					}
				}
			}
		});
		grid.on('rowunselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(rowData){
				delete _partyChooseData[rowData.partyId];
			}else{
				var datainformation = grid.jqxGrid('getdatainformation');
				var paginginformation = datainformation.paginginformation;
				var pagenum = paginginformation.pagenum;
				var pagesize = paginginformation.pagesize;
				var start = pagenum * pagesize;
				var end = start + pagesize;
				for(var rowIndex = start; rowIndex < end; rowIndex++){
					var data = grid.jqxGrid('getrowdata', rowIndex);
					if(data){
						delete _partyChooseData[data.partyId];
					}
				}
			}
		});
		grid.on("bindingcomplete", function (event) {
			var datainformation = grid.jqxGrid('getdatainformation');
			var paginginformation = datainformation.paginginformation;
			var pagenum = paginginformation.pagenum;
			var pagesize = paginginformation.pagesize;
			var start = pagenum * pagesize;
			var end = start + pagesize;
			for(var rowIndex = start; rowIndex < end; rowIndex++){
				var data = grid.jqxGrid('getrowdata', rowIndex);
				if(data){
					var partyId = data.partyId;
					if(partyId && _partyChooseData.hasOwnProperty(partyId)){
						grid.jqxGrid('selectrow', rowIndex);
					}else{
						grid.jqxGrid('unselectrow', rowIndex);
					}
				}
			}
		}); 
	};
	
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, selectionmode: 'checkbox', sourceId: "partyId"});
		createGridEvent($("#EmplListInOrg"));
	};
	var setContentDropDownBtn = function(content){
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + content + '</div>';
		$("#dropDownButtonGroupEmpl").jqxDropDownButton("setContent", dropDownContent);
	};
	var initEvent = function(){
		$("#ListRegistedWindow").on('close', function(event){
			$("#listRegistedGrid").jqxGrid('clearselection');
			_partyChooseData = {};
		});
		$("#ListExpectedWindow").on('close', function(event){
			$("#listExpectedGrid").jqxGrid('clearselection');
			_partyChooseData = {};
		});
		$('#popupWindowEmplList').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			_partyChooseData = {};
		});
		$("#" + globalVar.addWindowId).on('close', function(event){
			clearData();
		});
		$("#" + globalVar.addWindowId).on('open', function(event){
			initData();
		});
		$("#cancelAddPartyToTraining").click(function(event){
			$("#" + globalVar.addWindowId).jqxWindow('close');
		});
		$("#saveAddPartyToTraining").click(function(event){
			var valid = $("#" + globalVar.addWindowId).jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.AddNewRowConfirm,
					[
					 {
						 "label" : uiLabelMap.CommonSubmit,
						 "class" : "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							 createPartyAttendanceTrainingCourse();	    		
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger icon-remove btn-small open-sans",
					 }
					 ]
				);
		});
	};
	var createPartyAttendanceTrainingCourse = function(){
		var data = getData();
		$("#loadingAddEmplTraining").show();
		$("#chooseEmplBtn").attr("disabled", "disabled");
		$("#cancelAddPartyToTraining").attr("disabled", "disabled");
		$("#saveAddPartyToTraining").attr("disabled", "disabled");
		$.ajax({
			url: 'createTrainingCoursePartyAttendance',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#partyAttdanceGrid").jqxGrid('updatebounddata');
					$("#" + globalVar.addWindowId).jqxWindow('close');
					$("#" + globalVar.addWindowId).trigger("addPartyToTrainingSuccess");
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
				$("#loadingAddEmplTraining").hide();
				$("#chooseEmplBtn").removeAttr("disabled");
				$("#cancelAddPartyToTraining").removeAttr("disabled");
				$("#saveAddPartyToTraining").removeAttr("disabled");
			}
		});
	};
	var getData = function(){
		var data = {};
		var partyIdArr = [];
		var rowsParty = $("#jqxGridGroupEmpl").jqxGrid('getrows');
		for(var i = 0; i < rowsParty.length; i++){
			partyIdArr.push(rowsParty[i].partyId);
		}
		data.partyIds = JSON.stringify(partyIdArr);
		data.trainingCourseId = globalVar.trainingCourseId;
		data.resultTypeId = $("#partyTrainingResult").val();
		data.employeeAmount = $("#amountMustPaid").val();
		data.employerAmount = $("#companySupport").val();
		data.employeePaid = $("#amountEmplPaid").val();
		data.comment = $("#commentTraining").jqxEditor('val');
		return data;
	};
	var selectTypeListEmpl = function(type){
		if(type == "ALL"){
			openJqxWindow($("#popupWindowEmplList"));
		}else if(type == "REGISTED"){
			openJqxWindow($("#ListRegistedWindow"));
		}else if(type == "EXPECTED"){
			openJqxWindow($("#ListExpectedWindow"));
		}
	};
	var closeWindow = function(windowId){
		$("#" + windowId).jqxWindow('close');
	};
	var addPartyToEmplGroup = function(partyArr){
		var source = $("#jqxGridGroupEmpl").jqxGrid('source');
		var localdata = source._source.localdata;
		for(var i = 0; i < partyArr.length; i++){
			var partyId = partyArr[i].partyId;
			var partyIdExists = $("#jqxGridGroupEmpl").jqxGrid('getrowdatabyid', partyId);
			if(!partyIdExists){
				localdata.push(partyArr[i]);
			}
		}
		source._source.localdata = localdata;
		$("#jqxGridGroupEmpl").jqxGrid('source', source);
		setContentDropDownBtn(localdata.length + " " + uiLabelMap.EmployeeSelected);
	};
	var saveChoosEmpl = function(windowId){
		var localdata = [];
		for(var partyId in _partyChooseData){
			if(partyId){
				localdata.push(_partyChooseData[partyId]);
			}
		}
		addPartyToEmplGroup(localdata);
		closeWindow(windowId);
	};
	var initData = function(){
		if(globalVar.actualEmplPaid){
			$("#amountMustPaid").val(globalVar.actualEmplPaid);
			$("#amountEmplPaid").val(globalVar.actualEmplPaid);
		}else{
			$("#amountMustPaid").val(globalVar.estimatedEmplPaid);
			$("#amountEmplPaid").val(globalVar.estimatedEmplPaid);
		}
		if(globalVar.actualAmountCompanySup){
			$("#companySupport").val(globalVar.actualAmountCompanySup);
		}else{
			$("#companySupport").val(globalVar.amountCompanySupport);
		}
	};
	var clearData = function(){
		Grid.clearForm($("#" + globalVar.addWindowId));
		var source = $("#jqxGridGroupEmpl").jqxGrid("source");
		source._source.localdata = [];
		$("#jqxGridGroupEmpl").jqxGrid("source", source);
	};
	var initValidator = function(){
		$("#" + globalVar.addWindowId).jqxValidator({
			rules: [
				{ input: '#chooseEmplBtn', message: uiLabelMap.NoPartyChoose, action: 'none',
					rule : function(input, commit){
						var records = $("#jqxGridGroupEmpl").jqxGrid('source').records;
						if(records.length <= 0){
							return false;
						}
						return true;
					}
				},    
			]
		});
	};
	return{
		init: init,
		selectTypeListEmpl: selectTypeListEmpl,
		closeWindow: closeWindow,
		saveChoosEmpl: saveChoosEmpl
	};
}());
$(document).ready(function(){
	$.jqx.theme = 'olbius';
	addPartyToTrainingObj.init();
});