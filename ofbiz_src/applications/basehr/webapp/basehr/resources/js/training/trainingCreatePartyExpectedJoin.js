var partyExpectedObj = (function(){
	var partyIdAddToTraining = [];
	var rowSelectedObject = {};
	var init = function(){
		initJqxGrid();
		initJqxGridSearchEmpl();
		initBtnEvent();
		initJqxWindow();
	};
	
	var initBtnEvent = function(){
		$("#btnCancelAddParty").click(function(event){
			$("#addPartyExpectedWindow").jqxWindow('close');
			
		});
		$("#btnSaveAddParty").click(function(event){
			var localdata = new Array();
			for(var key in rowSelectedObject){
				var data = rowSelectedObject[key];
				localdata.push({partyId: data.partyId, fullName: data.fullName, emplPositionType: data.emplPositionType, department: data.department, partyCode: data.partyCode});
			}
			var tempS = $("#partyExpectedGrid" + globalVar.createNewSuffix).jqxGrid('source');
			tempS._source.localdata = localdata;
			$("#partyExpectedGrid" + globalVar.createNewSuffix).jqxGrid('source', tempS);
			$("#addPartyExpectedWindow").jqxWindow('close');
		});
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'},
		                 {name: 'department', type: 'string'}];
		var columnlist = [
		                  {text: uiLabelMap.EmployeeId, datafield: 'partyCode' , editable: false, cellsalign: 'left', width: '20%', filterable: false},
		                  {text: uiLabelMap.EmployeeName, datafield: 'fullName', editable: false, cellsalign: 'left', width: '20%', filterable: false},
		                  {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', editable: false, cellsalign: 'left', width: '30%', filterable: false},
		                  {text: uiLabelMap.CommonDepartment, datafield: 'department', editable: false, cellsalign: 'left', filterable: false, width: '30%'},
		                  ];
		var grid = $("#partyExpectedGrid" + globalVar.createNewSuffix);
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "partyExpectedGrid" + globalVar.createNewSuffix;
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.EmplExpectedAttendance + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
			var container = $('#toolbarButtonContainer' + id);
			var maincontainer = $("#toolbarcontainer" + id);
			Grid.createAddRowButton(
					grid, container, uiLabelMap.CommonAddNew, {
						type: "popup",
						container: $("#addPartyExpectedWindow"),
					}
			);
			Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var config = {
				width: '100%', 
				height: 450,
				autoheight: false,
				virtualmode: false,
				showfilterrow: false,
				showtoolbar: true,
				rendertoolbar: rendertoolbar,
				pageable: true,
				sortable: false,
				filterable: false,
				selectionmode: 'multiplerows',
				editable: false,
				url: '',
				source: {pagesize: 10, id: 'partyId'}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var getPartyIdAddToTraining = function(){
		var records = $("#partyExpectedGrid" + globalVar.createNewSuffix).jqxGrid('getrows');
		var retData = [];
		for(var i = 0; i < records.length; i++){
			retData.push(records[i].partyId);
		}
		return retData;
	};
	
	var setPartyIdAddToTraining = function(newArr){
		partyIdAddToTraining = newArr;
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			$("#splitterEmplList").jqxSplitter({width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
		};
		createJqxWindow($("#addPartyExpectedWindow"), 850, 560, initContent);
		$('#addPartyExpectedWindow').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
			partyIdAddToTraining = getPartyIdAddToTraining();
			selectParty();
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
		$('#addPartyExpectedWindow').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			setPartyIdAddToTraining([]);
			rowSelectedObject = {};
		});
	};
	
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, url: 'JQGetEmplListInOrg&hasrequest=Y', selectionmode: 'multiplerows', 
														width: '100%', height: 467, sourceId: 'partyId'});
		$("#EmplListInOrg").on('rowselect', function(event){
			var rowData = event.args.row;
			if(rowData && !rowSelectedObject.hasOwnProperty(rowData.partyId)){
				rowSelectedObject[rowData.partyId] = rowData;
			}
		});
		
		$("#EmplListInOrg").on('rowunselect', function(event){
			var rowData = event.args.row;
			if(rowData && rowSelectedObject.hasOwnProperty(rowData.partyId)){
				delete rowSelectedObject[rowData.partyId];
			}
		});
		$("#EmplListInOrg").on('bindingcomplete', function(event){
			selectParty();
		});
	};
	
	var selectParty = function(){
		var selectedRowIndexes = $("#EmplListInOrg").jqxGrid('getselectedrowindexes');
		for(var i = 0; i < partyIdAddToTraining.length; i++){
			var rowIndex = $("#EmplListInOrg").jqxGrid('getrowboundindexbyid', partyIdAddToTraining[i]);
			if(rowIndex > -1 && selectedRowIndexes.indexOf(rowIndex) == -1){
				$("#EmplListInOrg").jqxGrid('selectrow', rowIndex);
			}
		}
	};
	var getNbrEmplExpected = function(){
		return $("#partyExpectedGrid" + globalVar.createNewSuffix).jqxGrid('getrows').length;
	};
	
	var getData = function(){
		var data = $("#partyExpectedGrid" + globalVar.createNewSuffix).jqxGrid('getrows');
		var partyIds = new Array();
		for(var i = 0; i < data.length; i++){
			partyIds.push(data[i].partyId);
		}
		return {partyIds: JSON.stringify(partyIds)};
	};
	var reset = function(){
		var tempS = $("#partyExpectedGrid" + globalVar.createNewSuffix).jqxGrid('source');
		tempS._source.localdata = [];
		$("#partyExpectedGrid" + globalVar.createNewSuffix).jqxGrid('source', tempS);
	};
	
	return{
		init: init,
		getNbrEmplExpected: getNbrEmplExpected,
		getData: getData,
		reset: reset
	}
}());
