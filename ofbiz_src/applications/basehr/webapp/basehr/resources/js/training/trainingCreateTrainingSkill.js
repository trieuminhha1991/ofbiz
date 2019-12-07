var traininingSkillObject = (function(){
	var init = function(){
		initJqxGrid();
		//initBtnEvent();
		skillTypeListObject.init();
	};
	
	var getRowData = function(){
		var retData = $("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('getrows');
		return retData;
	};
	
	var functionAfterSelectSkillType = function(rowData){
		var source = $("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('source');
		source._source.localdata = rowData;
		$("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('source', source);
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'trainingCourseId', type: 'string'},
		                 {name: 'skillTypeId', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'parentTypeDescription', type: 'string'},
		                 {name: 'resultTypeId', type: 'string'}];
		var columns = [
		               {text: uiLabelMap.HRSkillType, datafield: 'description', width: '40%', editable : false},
		               {text: uiLabelMap.HRSkillTypeParent, datafield: 'parentTypeDescription', width: '35%'},
		               {text: uiLabelMap.RequrimentLevelSkillTrainingCourse, datafield:'resultTypeId', width: '25%',columntype: 'dropdownlist',
		            	   cellsrenderer: function (row, column, value){
		            		   for(var i = 0; i< globalVar.trainingResultTypeArr.length; i++){
		            			   if(value == globalVar.trainingResultTypeArr[i].resultTypeId){
		            				   return '<span>' + globalVar.trainingResultTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createEditor: function (row, cellvalue, editor, cellText, width, height) {
		            		   createJqxDropDownList(globalVar.trainingResultTypeArr, editor, "resultTypeId", "description", height, width);
		            	   },
		            	   initEditor: function (row, cellvalue, editor, celltext, width, height) {
		            		   editor.val(cellvalue);
		            	   }
		               }];
		var grid = $("#skillTypeGrid" + globalVar.createNewSuffix);
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "skillTypeGrid" + globalVar.createNewSuffix;
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.TrainingCourseSkillTypeList + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
			var container = $('#toolbarButtonContainer' + id);
			var maincontainer = $("#toolbarcontainer" + id);
			Grid.createAddRowButton(
					grid, container, uiLabelMap.CommonAddNew, {
						type: "popup",
						container: $("#addSkillTrainingWindow"),
					}
			);
		};
		var sourceGrid = {
				updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateTrainingCourseSkillType",
				editColumns: "skillTypeId;trainingCourseId;resultTypeId",
				pagesize: 10,
				id: 'skillTypeId'
		};
		var config = {
				width: '100%', 
				height: 450,
				autoheight: false,
				virtualmode: false,
				showfilterrow: false,
				rendertoolbar: rendertoolbar,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: false,
				filterable: false,
				editable: true,
				rowsheight: 26,
				url: '',    
				showtoolbar: true,
				source: sourceGrid
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var getData = function(){
		var records = getRowData();
		var skillTypeIds = [];
		for(var i = 0; i < records.length; i++){
			var row = {};
			row.skillTypeId = records[i].skillTypeId;
			row.resultTypeId = records[i].resultTypeId;
			skillTypeIds.push(row);
		}
		return {skillTypeIds: JSON.stringify(skillTypeIds)};
	};
	var reset = function(){
		var source = $("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('source');
		source._source.localdata = [];
		$("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('source', source);
	};
	
	return{
		init: init,
		getRowData: getRowData,
		getData: getData,
		functionAfterSelectSkillType: functionAfterSelectSkillType,
		reset: reset
	}
}());

var skillTypeListObject = (function(){
	var init = function(){
		initJqxGrid();
		initWindow();
		initEvent();
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'isSelected', type: 'bool'},
		                 {name: 'skillTypeId', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'parentTypeDescription', type: 'string'},
		                 {name: 'resultTypeId', type: 'string'}
		                 ];
		var columns = [{ text: '', datafield: 'isSelected', columntype: 'checkbox', width: '8%', editable: true},,
		               {text: uiLabelMap.HRSkillType, datafield: 'description', width: '40%', editable: false},
		               {text: uiLabelMap.HRSkillTypeParent, datafield: 'parentTypeDescription', width: '30%', editable: false},
		               {text: uiLabelMap.RequrimentLevelSkillTrainingCourse, datafield: 'resultTypeId', width: '22%', editable: true,
		            	   columntype: 'dropdownlist',
		            	   cellsrenderer: function (row, column, value){
		            		   for(var i = 0; i< globalVar.trainingResultTypeArr.length; i++){
		            			   if(value == globalVar.trainingResultTypeArr[i].resultTypeId){
		            				   return '<span>' + globalVar.trainingResultTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createEditor: function (row, cellvalue, editor, cellText, width, height) {
		            		   createJqxDropDownList(globalVar.trainingResultTypeArr, editor, 'resultTypeId', 'description', height, width);
		            	   },
		            	   initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
		            	   }
		               }
		               ];
		var grid = $("#skillListGrid");
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "skillListGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.HRSkillTypeList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#addNewSkillTypeWindow")});
		};
		
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: false,
				editable: true,
				sortable: true,
				selectionmode: 'singlecell',
				localization: getLocalization(),
				source: {
					localdata: [],
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var loadData = function(){
		$("#skillListGrid").jqxGrid({disabled: true});
		$("#skillListGrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'jqxGeneralServicer?sname=JQListSkillType&pagesize=0',
			success: function(response){
				var localdata = response.results;
				updateLocaldata(localdata);
			},
			complete: function(jqXHR, textStatus){
				$("#skillListGrid").jqxGrid({disabled: false});
				$("#skillListGrid").jqxGrid('hideloadelement');
			}
		});
	};
	var updateLocaldata = function(localdata){
		$("#skillListGrid").jqxGrid({disabled: false});
		var source = $("#skillListGrid").jqxGrid('source');
		source._source.localdata = localdata;
		$("#skillListGrid").jqxGrid('source', source);
	};
	var initWindow = function(){
		createJqxWindow($("#addSkillTrainingWindow"), 600, 340);
	};
	var initEvent = function(){
		$("#addNewSkillTypeWindow").on('addSkillTypeSuccess', function(){
			if ($('#containerEditTraining').lenght > 0){
				Grid.renderMessage('EditTraining', uiLabelMap.wgaddsuccess, 
						{autoClose : true, template : 'info',appendContainer : "#containerEditTraining",opacity : 0.9});
			}
			loadData();
		});
		$("#addSkillTrainingWindow").on('open', function(event){
			loadData();
		});
		$("#addSkillTrainingWindow").on('close', function(event){
			updateLocaldata([]);
		});
		$("#cancelEditTrainingCourseSkill").click(function(event){
			$("#addSkillTrainingWindow").jqxWindow('close');
		});
		$("#saveEditTrainingCourseSkill").click(function(event){
			var rows = $("#skillListGrid").jqxGrid('getrows');
			for(var i = 0; i < rows.length; i++){
				var rowData = rows[i];
				if(rowData.isSelected){
					var newRowData = {skillTypeId: rowData.skillTypeId, resultTypeId: rowData.resultTypeId, parentTypeDescription: rowData.parentTypeDescription, description: rowData.description};
					var rowIndex = $("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('getrowboundindexbyid', rowData.skillTypeId);
					if(rowIndex > -1){
						$("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('updaterow', rowData.skillTypeId, newRowData)
					}else{
						$("#skillTypeGrid" + globalVar.createNewSuffix).jqxGrid('addrow', rowData.skillTypeId, newRowData);
					}
				}
			}
			$("#addSkillTrainingWindow").jqxWindow('close');
		});
	};
	return{
		init: init
	}
}());
