var recruitCandidateProcessObj = (function(){
	var _recruitmentPlanId = null;
	var _partyId = null;
	var init = function(){
		initJqxPannel();
		initJqxWindow();
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'recruitmentPlanId', type: 'string'},
		                 {name: 'roundOrder', type: 'string'},
		                 {name: 'roundName', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'resultTypeId', type: 'string'},
		                 {name: 'dateInterview', type: 'date'},
		                 {name: 'totalPoint', type: 'number'},
		                 {name: 'comment', type: 'string'},
		                 ];
		var columns = [{datafield: 'partyId', hidden: true},
	                   {datafield: 'roundOrder', hidden: true},
	                   {datafield: 'recruitmentPlanId', hidden: true},
	                   {text: uiLabelMap.RecruitmentRound, datafield: 'roundName', width: '16%'},
	                   {text: uiLabelMap.RecruitmentTimeInterview, datafield: 'dateInterview', width: '18%', columntype: 'datetimeinput', filtertype: 'range', cellsformat:'dd/MM/yyyy HH:mm'},
	                   {text: uiLabelMap.HRCommonResults, datafield: 'resultTypeId', width: '10%', columntype: 'dropdownlist', filtertype: 'checkedlist',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.recruitmentResultTypeArr.length; i++){
		            			   if(value == globalVar.recruitmentResultTypeArr[i].resultTypeId){
		            				   return '<span>' + globalVar.recruitmentResultTypeArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.recruitmentResultTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'resultTypeId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
	                   {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '15%', columntype: 'dropdownlist', filtertype: 'checkedlist', 
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.statusCandidateRoundArr.length; i++){
		            			   if(value == globalVar.statusCandidateRoundArr[i].statusId){
		            				   return '<span>' + globalVar.statusCandidateRoundArr[i].description + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   createfilterwidget: function(column, columnElement, widget){
		            		   var source = {
								        localdata: globalVar.statusCandidateRoundArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
							    if(dataSoureList.length < 8){
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }
		            	   },
		               },
		               {text: uiLabelMap.TotalPoint, datafield: 'totalPoint', width: '11%', filterable: false, editable: false},
		               {text: uiLabelMap.HRCommonComment, datafield: 'comment', width: '30%',
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   return defaulthtml;
		            	   }
		               }
	                   ];
		var grid = $("#recruitCandidateProcessGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitCandidateProcessGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentProcess + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
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
				autorowheight: true,
				localization: getLocalization(),
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initJqxPannel = function(){
		$("#recruitCandidateProcessPanel").jqxPanel({width: '99,5%', height: 500, scrollBarSize: 15});
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxGrid();
		};
		createJqxWindow($("#recruitCanidateProcessWindow"), 850, 550, initContent);
		$("#recruitCanidateProcessWindow").on('open', function(event){
			refreshGridData(_recruitmentPlanId, _partyId);
		});
	};
	var openWindow = function(){
		openJqxWindow($("#recruitCanidateProcessWindow"));
	};
	
	var refreshGridData = function(recruitmentPlanId, partyId){
		var tempS = $("#recruitCandidateProcessGrid").jqxGrid('source');
		tempS._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentProcessCandidate&recruitmentPlanId=" + recruitmentPlanId + "&partyId=" + partyId;
		$("#recruitCandidateProcessGrid").jqxGrid('source', tempS);
	};
	
	var setData = function(data){
		_recruitmentPlanId = data.recruitmentPlanId;
		_partyId = data.partyId;
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	recruitCandidateProcessObj.init();
});