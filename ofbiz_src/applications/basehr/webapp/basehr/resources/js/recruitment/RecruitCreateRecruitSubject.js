var recruitmentSubjectListObj = (function(){
	var init = function(){
		initJqxGrid();
		initJqxInput();
		initJqxValidator();
		initEvent();
		initJqxWindow();
	};
	
	var initJqxValidator = function(){
		$("#addRecruitmentSubjectWindow").jqxValidator({
			rules: [
			        {input : '#recruitmentSubjectNew', message : uiLabelMap.FieldRequired, action : 'blur',
			        	rule : function(input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#recruitmentSubjectNew', message : uiLabelMap.InvalidChar, action : 'blur',
			        	rule : function(input, commit){
			        		var val = input.val();
			        		if(val){
			        			if(validationNameWithoutHtml(val)){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#commentSubjectNew', message : uiLabelMap.InvalidChar, action : 'blur',
			        	rule : function(input, commit){
			        		var val = input.val();
			        		if(val){
			        			if(validationNameWithoutHtml(val)){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        }
	        ]
		});
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'subjectId', type: 'string'},
		                 {name: 'subjectName', type:'string'},
		                 {name: 'comment', type: 'string'}];
		var columns = [{datafield: 'subjectId', hidden: true},
		               {text: uiLabelMap.CommonName, datafield: 'subjectName', width: '40%'},
		               {text: uiLabelMap.HRNotes, datafield: 'comment', width: '60%'}];
		var grid = $("#recruitSubjectListGrid");
		var rendertoolbarSubject = function (toolbar){
			toolbar.html("");
			var id = "recruitSubjectListGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentSubjectList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		grid, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#addRecruitmentSubjectWindow"),
		        	}
		        );
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: 'JQGetListRecruitmentSubject',
				rendertoolbar : rendertoolbarSubject,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: true,
				filterable: true,
				editmode: 'dblclick',
				selectionmode: 'multiplerows',
				localization: getLocalization(),
				source: {
					addColumns: 'subjectName;comment',
					createUrl: 'jqxGeneralServicer?jqaction=C&sname=createRecruitmentRoundSubject',
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateRecruitmentRoundSubject",
					editColumns: "subjectId;subjectName;comment",
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initJqxInput = function(){
		$("#recruitmentSubjectNew").jqxInput({width: '96%', height: 20});
		$("#commentSubjectNew").jqxInput({width: '96%', height: 20});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addRecruitmentSubjectWindow"), 400, 180);
		createJqxWindow($("#recruitmentSubjectListWindow"), 600, 480);
		$("#addRecruitmentSubjectWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#recruitSubjectListGrid").jqxGrid('clearselection');
		});
		$("#recruitmentSubjectListWindow").on('close', function(event){
			$("#recruitSubjectListGrid").jqxGrid('clearselection');
		});
	};
	
	var initEvent = function(){
		$("#cancelCreateSubject").click(function(event){
			$("#addRecruitmentSubjectWindow").jqxWindow('close');
		});
		
		$("#cancelSelectSubject").click(function(event){
			$("#recruitmentSubjectListWindow").jqxWindow('close');
		});
		
		$("#saveCreateSubject").click(function(event){
			var valid = $("#addRecruitmentSubjectWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var row = {
					subjectName: $("#recruitmentSubjectNew").val(),
					comment: $("#commentSubjectNew").val()
			};
			$("#recruitSubjectListGrid").jqxGrid('addrow', null, row, 'first');
			$("#addRecruitmentSubjectWindow").jqxWindow('close');
		});
		
		$("#saveSelectSubject").click(function(event){
			var indexs = $("#recruitSubjectListGrid").jqxGrid('getselectedrowindexes');
			if(indexs.length > 0){
				var rowData = [];
				for(var i = 0; i < indexs.length; i++){
					var dataRecord = $("#recruitSubjectListGrid").jqxGrid('getrowdata', indexs[i]);
					var checkSubjectExists = $("#recruitmentRoundSubjectGrid").jqxGrid('getrowdatabyid', dataRecord.subjectId);
					if(!checkSubjectExists){
						rowData.push({subjectId: dataRecord.subjectId, subjectName: dataRecord.subjectName, ratio: 1});
					}
				}
				$("#recruitmentRoundSubjectGrid").jqxGrid('addrow', null, rowData);
			}
			$("#recruitmentSubjectListWindow").jqxWindow('close');
		});
	};
	var disableGrid = function(){
		$("#recruitmentRoundSubjectGrid").jqxGrid({disabled: true});
		$("#addrowbuttonrecruitmentRoundSubjectGrid").attr("disabled", "disabled");
		$("#deleterowbuttonrecruitmentRoundSubjectGrid").attr("disabled", "disabled");
	};
	var enableGrid = function(){
		$("#recruitmentRoundSubjectGrid").jqxGrid({disabled: false});
		$("#addrowbuttonrecruitmentRoundSubjectGrid").removeAttr("disabled");
		$("#deleterowbuttonrecruitmentRoundSubjectGrid").removeAttr("disabled");
	};
	return{
		init: init,
		enableGrid: enableGrid,
		disableGrid: disableGrid
	}
}());