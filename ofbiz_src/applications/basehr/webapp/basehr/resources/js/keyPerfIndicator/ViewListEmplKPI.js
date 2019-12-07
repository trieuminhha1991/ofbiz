var emplListKPICommonObj = (function(){
	var viewEmplKPIList = true;
    var _partyChooseData = {};
	var init = function(){
		//initJqxGridSearchEmpl();
        initEvent();
		initJqxWindow();
	};
	
	var initJqxSplitter = function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	
	var initJqxWindow = function(){
		createJqxWindow($('#popupWindowEmplList'), 900, 560, initJqxSplitter);
		/*$('#popupWindowEmplList').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});*/
        $('#popupWindowEmplList').on('open', function(event){
            $("#addNewEmplKPIListWindow").jqxValidator('hide');
            if(typeof(globalVar.expandTreeId) != 'undefined'){
                $("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
                $('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
            }
        });
        $('#popupWindowEmplList').on('close', function(event){
            $("#EmplListInOrg").jqxGrid('clearselection');
            _partyChooseData = {};
        });

	};
	var initEvent = function() {
        $("#saveChooseEmpl").click(function(event){
            var localdata = [];
            for(var partyId in _partyChooseData){
                if(partyId){
                    localdata.push(_partyChooseData[partyId]);
                }
            }
            addPartyToEmplGroup(localdata);
            $('#popupWindowEmplList').jqxWindow('close');
        });
        $("#cancelChooseEmpl").click(function(event){
            $('#popupWindowEmplList').jqxWindow('close');
        });
        $("#EmplListInOrg").on('rowselect', function (event){
            var args = event.args;
            var rowData = args.row;
            if(rowData){
                _partyChooseData[rowData.partyId] = rowData;
            }else{
                var datainformation = $('#EmplListInOrg').jqxGrid('getdatainformation');
                var paginginformation = datainformation.paginginformation;
                var pagenum = paginginformation.pagenum;
                var pagesize = paginginformation.pagesize;
                var start = pagenum * pagesize;
                var end = start + pagesize;
                for(var rowIndex = start; rowIndex < end; rowIndex++){
                    var data = $('#EmplListInOrg').jqxGrid('getrowdata', rowIndex);
                    if(data){
                        _partyChooseData[data.partyId] = data;
                    }
                }
            }
        });
        $("#EmplListInOrg").on('rowunselect', function (event){
            var args = event.args;
            var rowData = args.row;
            if(rowData){
                delete _partyChooseData[rowData.partyId];
            }else{
                var datainformation = $('#EmplListInOrg').jqxGrid('getdatainformation');
                var paginginformation = datainformation.paginginformation;
                var pagenum = paginginformation.pagenum;
                var pagesize = paginginformation.pagesize;
                var start = pagenum * pagesize;
                var end = start + pagesize;
                for(var rowIndex = start; rowIndex < end; rowIndex++){
                    var data = $('#EmplListInOrg').jqxGrid('getrowdata', rowIndex);
                    if(data){
                        delete _partyChooseData[data.partyId];
                    }
                }
            }
        });
        $("#EmplListInOrg").on("bindingcomplete", function (event) {
            var datainformation = $('#EmplListInOrg').jqxGrid('getdatainformation');
            var paginginformation = datainformation.paginginformation;
            var pagenum = paginginformation.pagenum;
            var pagesize = paginginformation.pagesize;
            var start = pagenum * pagesize;
            var end = start + pagesize;
            for(var rowIndex = start; rowIndex < end; rowIndex++){
                var data = $('#EmplListInOrg').jqxGrid('getrowdata', rowIndex);
                if(data){
                    var partyId = data.partyId;
                    if(partyId && _partyChooseData.hasOwnProperty(partyId)){
                        $("#EmplListInOrg").jqxGrid('selectrow', rowIndex);
                    }else{
                        $("#EmplListInOrg").jqxGrid('unselectrow', rowIndex);
                    }
                }
            }
        });
    };
    var addPartyToEmplGroup = function(partyArr){
        var source = $("#jqxGridGroupEmpl").jqxGrid('source');
        //console.log(source, 'ahihi');
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

	/*var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap});
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
		    if(viewEmplKPIList){
		    	viewEmplListKPIObj.setPartyIdForViewKPI(data);
		    }else{
		    	editEmplKPIObj.setPartyIdForEditKPI(data);
		    }
		    $('#popupWindowEmplList').jqxWindow('close');
		});
	};*/
	
	var setViewEmplKPIList = function(value){
		viewEmplKPIList = value;
	};
	
	var openWindow = function(){
		openJqxWindow($('#popupWindowEmplList'));
	};
    var setContentDropDownBtn = function(content){
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + content + '</div>';
        $("#dropDownButtonGroupEmpl").jqxDropDownButton("setContent", dropDownContent);
    };
	
	return{
		init: init,
		setViewEmplKPIList: setViewEmplKPIList,
        addPartyToEmplGroup: addPartyToEmplGroup,
        setContentDropDownBtn: setContentDropDownBtn,
		openWindow: openWindow
	}
}());

var viewEmplListKPIObj = (function(){
	var _refreshGrid = false; 
	var _rowTooltipData = {};
	var init = function(){
		initJqxGridSearchEmpl();
		initJqxSplitter();
		initJqxWindow1();
		initJqxInput();
		initEvent();
		initJqxWindow2();
	};
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrgSearch"), {uiLabelMap: uiLabelMap, selectionmode: 'singlerow', sourceId: "partyId"});
	};
	var initJqxSplitter = function(){
		$("#splitterEmplListSearch").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initJqxWindow1 = function(){
		createJqxWindow($('#popupWindowEmplListSearch'), 900, 560, initJqxSplitter);
		/*$('#popupWindowEmplList').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});*/
		$('#popupWindowEmplListSearch').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplListSearch").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplListSearch")[0]);
				$('#jqxTreeEmplListSearch').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplListSearch")[0]);
			}
		});
		$('#popupWindowEmplListSearch').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});

	};
	var initJqxInput = function(){
		$("#searchPartyId").jqxInput({width: '90%', height: 26, placeHolder: uiLabelMap.CommonEmployee, 
			disabled: true, valueMember: 'partyId', displayMember: 'partyName'});
	};
	
	var initEvent = function(){
		$("#searchPartyIdBtn").click(function(event){
			$('#popupWindowEmplListSearch').jqxWindow('open');
		});
		$("#EmplListInOrgSearch").on('rowselect', function (event){
			var args = event.args;
			var rowData = args.row;
			if(rowData) {
				setPartyIdForViewKPI2(rowData);
			}
			$('#popupWindowEmplListSearch').jqxWindow('close');
		});
	};
	var setPartyIdForViewKPI2 = function(data){
		$("#searchPartyId").jqxInput('val', {label: data.fullName, value: data.partyId});
		refreshData(data.partyId);
	};
	var setPartyIdForViewKPI = function(data){
		$("#searchPartyId").jqxInput('val', {label: data.partyName, value: data.partyId});
		refreshData(data.partyId);
	};

	var refreshData = function(partyId){
		refreshBeforeReloadGrid($("#jqxgrid" + globalVar.listEmplKPIWindow));
		var tmpS = $("#jqxgrid" + globalVar.listEmplKPIWindow).jqxGrid('source');
		tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListKPI&partyId=' + partyId;
		$("#jqxgrid" + globalVar.listEmplKPIWindow).jqxGrid('source', tmpS);
	};

	var initJqxWindow2 = function(){
		var initContent = function(){
			initJqxGrid();
		};
		createJqxWindow($("#" + globalVar.listEmplKPIWindow), 950, 550, initContent());		
		$("#" + globalVar.listEmplKPIWindow).on('open', function(event){
			emplListKPICommonObj.setViewEmplKPIList(true);
			_refreshGrid = false;
		});
		$("#" + globalVar.listEmplKPIWindow).on('close', function(event){
			$("#searchPartyId").val("");
			refreshBeforeReloadGrid($("#jqxgrid" + globalVar.listEmplKPIWindow));
			//$("#jqxgrid" + globalVar.listEmplKPIWindow).jqxGrid('refreshdata');
			if(_refreshGrid){
				kpiAssignEmplObj.updateGrid();
			}
		});
	};
	
	var getDataField = function(){
		var datafield = [{name: 'partyId', type: 'string'},
						{name: 'criteriaId', type: 'string'},
						{name: 'criteriaName', type: 'string'},
						{name: 'perfCriteriaTypeId', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
						{name: 'periodTypeId', type: 'string'},
						{name: 'target', type: 'number'},
						{name: 'uomId', type: 'string'},
						{name: 'statusId', type: 'string'},
						{name: 'description', type: 'string'},
						{name: 'weight', type: 'number'}];
		return datafield;
	};
	
	var setRefreshGrid = function(value){
		_refreshGrid = value;
	};
	
	var getColumns = function(){
		var columns = [
						{text: uiLabelMap.CommonId, datafield: 'criteriaId', width: '10%', editable: false},
						{text: uiLabelMap.HRCommonFields, datafield: 'perfCriteriaTypeId', width: '20%', editable: false,
							columntype: 'dropdownlist', filtertype: 'checkedlist', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.perfCriteriaTypeArr.length; i++){
									if(value == globalVar.perfCriteriaTypeArr[i].perfCriteriaTypeId){
										return '<span>' + globalVar.perfCriteriaTypeArr[i].description + '</span>'; 
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.perfCriteriaTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'perfCriteriaTypeId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
							}	
						},
						{text: uiLabelMap.HRCommonKPIName, datafield: 'criteriaName', width: '20%', editable: false,
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid' + globalVar.listEmplKPIWindow).jqxGrid('getrowdata', row);
								if(data){
									var description = data.description;
									if(description && description.length > 0){
										 _rowTooltipData["row_" + row] = {row: row, data: description};
									}
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: uiLabelMap.HRFrequency, datafield: 'periodTypeId', width: '15%', columntype: 'dropdownlist',
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
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
							},
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								createJqxDropDownList(globalVar.periodTypeArr, editor, "periodTypeId", "description", height, width);
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
							geteditorvalue: function (row, cellvalue, editor) {
								return editor.val();
							}
						},
						{text : uiLabelMap.HRTarget, width : '18%', dataField : 'target',  columntype: 'numberinput',
							filtertype: 'number', editable: true,
							cellsrenderer: function (row, column, value) {
								if(value){
									value = formatNumber(value, 2,  " ");
								}
								return '<span>' + value + '<span>';
							},
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxNumberInput({width: width, height: height,  spinButtons: true, min: 0, max: 1, decimalDigits: 2, 
									digits: 12, max: 999999999999});
							}
						},
						{text : uiLabelMap.HRCommonUnit, width : '8%', dataField : 'uomId',  columntype: 'dropdownlist',
							filtertype: 'checkedlist', editable: true,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.uomArr.length; i++){
									if(value == globalVar.uomArr[i].uomId){
										return '<span>' + globalVar.uomArr[i].abbreviation + '</span>'; 
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.uomArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'abbreviation', valueMember : 'uomId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
							},
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								createJqxDropDownList(globalVar.uomArr, editor, "uomId", "abbreviation", height, width);
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
							geteditorvalue: function (row, cellvalue, editor) {
								return editor.val();
							}
						},
						{text: uiLabelMap.KPIWeigth, datafield: 'weight', columntype: 'numberinput', width: '10%',
							cellsrenderer: function (row, column, value) {
								if(typeof(value) == 'number'){
									var tempValue = 100 * value;
									return '<span>' + tempValue + '%</span>';
								}
								return '<span>' + value + '</span>';
							},
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxNumberInput({width: width, height: height,  spinButtons: true, min: 0, max: 1, decimalDigits: 0, 
									digits: 3, symbol: '%', symbolPosition: 'right'});
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								if(typeof(cellvalue) == 'number'){
									editor.val(cellvalue * 100);
								}
							},
							geteditorvalue: function (row, cellvalue, editor) {
								return editor.val() / 100;
							}
						},
						{text: uiLabelMap.CommonFromDate, datafield: 'fromDate', filtertype: 'range',columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', 
							editable: false, width: '14%',
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxDateTimeInput({width: width, height: height});
							}
						},
						{text: uiLabelMap.CommonThruDate, datafield: 'thruDate', filtertype: 'range', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', 
							editable: true, width: '14%',
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxDateTimeInput({width: width, height: height, showFooter: true});
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
						},
						{text : uiLabelMap.CommonStatus, width: '13%', dataField : 'statusId', columntype: 'dropdownlist', hidden: true,
							filtertype: 'checkedlist', editable: false,
							cellsrenderer: function (row, column, value) {
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
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
							}
						},
						{datafield: 'description', hidden: true},
						{datafield: 'partyId', hidden: true},
						];
		return columns;
	};
	
	var initJqxGrid = function(){
		var datafield = getDataField();
		var columns = getColumns();
		var grid = $("#jqxgrid" + globalVar.listEmplKPIWindow);
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "jqxgrid" + globalVar.listEmplKPIWindow;
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ListKPIAssignForEmpl + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        if(globalVar.updatePermission){
	        	Grid.createAddRowButton(
	        			grid, container, uiLabelMap.AssignKPIForEmpl, {
	        				type: "popup",
	        				container: $("#addNewEmplKPIListWindow"),
	        			}
	        	);
	        }
	        if(globalVar.deletePermission){ 
	        	Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
	        			"", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
	        }
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		
		var config = {
		   		width: '100%', 
		   		height: 450,
		   		autoheight: false,
		   		virtualmode: true,
		   		showfilterrow: true,
		   		rendertoolbar: rendertoolbar,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: true,
		        editable: true,
		        url: '',    
	   			showtoolbar: true,
	        	source: {
	        		pagesize: 10,
	        		updateUrl : "jqxGeneralServicer?jqaction=U&sname=updatePartyPerfCriteria",
		   			editColumns : "partyId;criteriaId;uomId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);periodTypeId;target(java.math.BigDecimal);weight(java.math.BigDecimal)",
		   			removeUrl: "jqxGeneralServicer?jqaction=D&sname=deletePartyPerfCriteria", 
					deleteColumns: "criteriaId;partyId;fromDate(java.sql.Timestamp)"
	        	}
		 };
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var openWindow = function(){
		openJqxWindow($("#" + globalVar.listEmplKPIWindow));
	};
	return{
		init: init,
		openWindow: openWindow,
		setPartyIdForViewKPI: setPartyIdForViewKPI,
		setRefreshGrid: setRefreshGrid
	}
}());


$(document).ready(function(){
	emplListKPICommonObj.init();
	viewEmplListKPIObj.init();
});