var skillTypeListObj = (function(){
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
			Grid.renderMessage('EditTraining', uiLabelMap.wgaddsuccess, 
					{autoClose : true, template : 'info',appendContainer : "#containerEditTraining",opacity : 0.9});
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
			var skillData = [];
			for(var i = 0; i < rows.length; i++){
				var rowData = rows[i];
				if(rowData.isSelected){
					skillData.push({skillTypeId: rowData.skillTypeId, resultTypeId: rowData.resultTypeId});
				}
			}
			if(skillData.length == 0){
				bootbox.dialog(uiLabelMap.NotSkillTypeSelected,
					[{
						"label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
					}]		
				);
				return;
			}
			var data = {};
			data.skillTypeId = JSON.stringify(skillData);
			data.trainingCourseId = globalVar.trainingCourseId;
			$("#skillListGrid").jqxGrid({disabled: true});
			$("#skillListGrid").jqxGrid('showloadelement');
			$("#cancelEditTrainingCourseSkill").attr("disabled", "disabled");
			$("#saveEditTrainingCourseSkill").attr("disabled", "disabled");
			$.ajax({
				url: 'editTrainingCourseSkillType',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						Grid.renderMessage('EditTraining', response.successMessage, 
								{autoClose : true, template : 'info', appendContainer : "#containerEditTraining", opacity : 0.9});
						$("#gridSkillTrain").jqxGrid('updatebounddata');
						$("#addSkillTrainingWindow").jqxWindow('close');
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
					$("#skillListGrid").jqxGrid({disabled: false});
					$("#skillListGrid").jqxGrid('hideloadelement');
	    			$("#cancelEditTrainingCourseSkill").removeAttr("disabled");
	    			$("#saveEditTrainingCourseSkill").removeAttr("disabled");
				}
			});
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
	skillTypeListObj.init();
});