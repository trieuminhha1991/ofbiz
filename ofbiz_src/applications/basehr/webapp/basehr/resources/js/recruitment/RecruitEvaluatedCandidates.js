var evaluatedCandidateObj = (function(){
	var _recruitmentPlanId = null;
	var init = function(){
		initJqxSplitter();
		initJqxListBox();
		initJqxGrid();
		initContextMenu();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerEvaluated"));
		$("#jqxNotificationrecruitEvaluatedCandidateGrid").jqxNotification({ width: "100%", appendContainer: "#containerrecruitEvaluatedCandidateGrid", 
			opacity: 0.9, template: "info" });
	};
	var initJqxSplitter = function(){
		$('#roundMainEvaluatedSplitter').jqxSplitter({ width: 955, height: 530, panels: [{ size: 180}], splitBarSize: 3});
	};
	var initJqxListBox = function(){
		var source = {
			datatype: "json",
			datafields: [
                { name: 'roundOrder' },
                { name: 'roundName' },
                {name: 'enumRoundTypeId'}
            ],
            id: 'id',
            url: "",
            root: "listReturn",
        };
        var dataAdapter = new $.jqx.dataAdapter(source,{
        		beforeSend: function (xhr) {
        			$("#loadingEvaluated").show();
        		},
        		loadComplete: function (records){
        			$("#loadingEvaluated").hide();
        			$("#listBoxRoundRecEngaged").jqxListBox('selectIndex', 0);
        		},
        		beforeLoadComplete: function (records) {
        			
                }
        });
		$("#listBoxRoundRecEngaged").jqxListBox({ source: dataAdapter, displayMember: "roundName", valueMember: "roundOrder", 
			width: '100%', height: '99.5%', itemHeight: 30});
		$("#listBoxRoundRecEngaged").on('select', function(event){
			var args = event.args;
		    if (args) {
		        var roundOrder = args.item.value;
		        if($("#statusEvaluatedCandidateDropDown").length){
		        	var statusSelectedItem = $("#statusEvaluatedCandidateDropDown").jqxDropDownList('getSelectedItem');
		        	if(statusSelectedItem){
		        		refreshGrid(_recruitmentPlanId, roundOrder, statusSelectedItem.value);
		        	}
		        }
		    }
		});
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'recruitmentPlanId', type: 'string'},
		                 {name: 'recruitCandidateId', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'candidateId', type: 'string'},
		                 {name: 'gender', type: 'string'},
		                 {name: 'birthDate', type: 'date'},
		                 {name: 'educationSystemTypeId', type: 'string'},
		                 {name: 'majorDesc', type: 'string'},
		                 {name: 'classificationTypeId', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'roundOrder', type: 'string'},
		                 {name: 'roundName', type: 'string'},
		                 {name: 'dateInterview', type: 'date'},
		                 ];
		
		var columns = [{datafield: 'recruitmentPlanId', hidden: true},
		               {datafield: 'partyId', hidden: true},
		               {datafield: 'dateInterview', hidden: true},
		               {datafield: 'roundOrder', hidden: true},
		               {text: uiLabelMap.RecruitmentCandidateId, datafield: 'recruitCandidateId', width: '14%'},
		               {text: uiLabelMap.HRFullName, datafield: 'fullName', width: '15%'},
		               {text: uiLabelMap.PartyGender, datafield: 'gender', width: '12%', columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.genderArr.length; i++){
		            			   if(value == globalVar.genderArr[i].genderId){
		            				   return '<span>' + globalVar.genderArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.genderArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'genderId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
		               {text: uiLabelMap.PartyBirthDate, datafield: 'birthDate', width: '15%', columntype: 'datetimeinput', filtertype: 'range', cellsformat:'dd/MM/yyyy'},
		               {text: uiLabelMap.DegreeTraining, datafield: 'educationSystemTypeId', width: '17%', columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.educationSystemTypeArr.length; i++){
		            			   if(value == globalVar.educationSystemTypeArr[i].educationSystemTypeId){
		            				   return '<span>' + globalVar.educationSystemTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.educationSystemTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'educationSystemTypeId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
		               {text: uiLabelMap.HRSpecialization, datafield: 'majorDesc', width: '15%'},
		               {text: uiLabelMap.HRCommonClassification, datafield: 'classificationTypeId', width: '12%', columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.degreeClassTypeArr.length; i++){
		            			   if(value == globalVar.degreeClassTypeArr[i].classificationTypeId){
		            				   return '<span>' + globalVar.degreeClassTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.degreeClassTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'classificationTypeId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
		               {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '12%', columntype: 'dropdownlist', filtertype: 'checkedlist', filterable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.statusCandidateRoundArr.length; i++){
		            			   if(value == globalVar.statusCandidateRoundArr[i].statusId){
		            				   return '<span>' + globalVar.statusCandidateRoundArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		               },
		               {text: uiLabelMap.CurrentRecruitmentRound, datafield: 'roundName', width: '20%', filterable: false},
		               ];
		var grid = $("#recruitEvaluatedCandidateGrid");
		var customControlAdvance = "<div id='statusEvaluatedCandidateDropDown'></div>";
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitEvaluatedCandidateGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentCandidatesList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.triggerToolbarEvent(grid, container, customControlAdvance);
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: false,
				filterable: true,
				localization: getLocalization(),
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#contextMenuEvaluation"), false);
	};
	var initContextMenu = function(){
		var liElement = $("#contextMenuEvaluation>ul>li").length;
		var contextMenuHeight = 30 * liElement;
		$("#contextMenuEvaluation").jqxMenu({ width: 170, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup', popupZIndex: 22000});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#recruitmentEvaluatedWindow"), 970, 580);
		$("#recruitmentEvaluatedWindow").on('close', function(event){
			_recruitmentPlanId = null;
			updateSoucreJqxListBox("");
			$("#statusEvaluatedCandidateDropDown").jqxDropDownList('clearSelection');
		});
		$("#recruitmentEvaluatedWindow").on('open', function(event){
			if($("#statusEvaluatedCandidateDropDown").length){
				$("#statusEvaluatedCandidateDropDown").jqxDropDownList({selectedIndex: 0});
			}
		});
	};
	var refreshGrid = function(recruitmentPlanId, roundOrder, statusId){
		var source = $("#recruitEvaluatedCandidateGrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListCandidateEvaluatedInRecruitRound&recruitmentPlanId=" + recruitmentPlanId 
									+ "&roundOrder=" + roundOrder + "&statusEvaludatedId=" + statusId;
		$("#recruitEvaluatedCandidateGrid").jqxGrid('source', source);
	};
	var initEvent = function(){
		$("#recruitEvaluatedCandidateGrid").on('loadCustomControlAdvance', function(){
			createJqxDropDownList(globalVar.statusEvaluatedCandidateArr, $("#statusEvaluatedCandidateDropDown"), "statusId", "description", 25, 160);
			$("#statusEvaluatedCandidateDropDown").on('select', function(event){
				var args = event.args;
				if(args){
					var statusId = args.item.value;
					var roundOrderSelectedItem = $("#listBoxRoundRecEngaged").jqxListBox('getSelectedItem');
					if(roundOrderSelectedItem){
						refreshGrid(_recruitmentPlanId, roundOrderSelectedItem.value, statusId);
					}
				}
			});
			$("#statusEvaluatedCandidateDropDown").jqxDropDownList({selectedIndex: 0});
		});
		$("#contextMenuEvaluation").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#recruitEvaluatedCandidateGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#recruitEvaluatedCandidateGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "evaluation"){
            	var selectedItem = $("#listBoxRoundRecEngaged").jqxListBox('getSelectedItem');
            	if(selectedItem){
            		var originalItem = selectedItem.originalItem;
            		var enumRoundTypeId = originalItem.enumRoundTypeId;
            		if(enumRoundTypeId == "RECRUIT_ROUND_TEST"){
            			evalutedCandidateInTestRoundObj.openWindow();//evalutedCandidateInTestRoundObj is defined in RecruitEvalutedCandidateTestRound.js
            			evalutedCandidateInTestRoundObj.setData(dataRecord);
            		}else{
            			evalutedCandidateInINTWRoundObj.openWindow();//evalutedCandidateInINTWRoundObj is defined in RecruitEvalutedCandidateINTVWRound.js
            			evalutedCandidateInINTWRoundObj.setData(dataRecord);
            		}
            	}
            }
		});
	};
	var openWindow = function(){
		openJqxWindow($("#recruitmentEvaluatedWindow"));
	};
	var setData = function(data){
		_recruitmentPlanId = data.recruitmentPlanId;
		prepareData();
	};
	var updateSoucreJqxListBox = function(url){
		var source = $("#listBoxRoundRecEngaged").jqxListBox('source');
		source._source.url = url; 
		$("#listBoxRoundRecEngaged").jqxListBox('source', source);
		//$("#listBoxRoundRecEngaged").jqxListBox('refresh');
	};
	var prepareData = function(){
		updateSoucreJqxListBox("getListRecruitRoundPartyEngagedBoard?recruitmentPlanId=" + _recruitmentPlanId);
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());
$(document).ready(function(){
	evaluatedCandidateObj.init();
});