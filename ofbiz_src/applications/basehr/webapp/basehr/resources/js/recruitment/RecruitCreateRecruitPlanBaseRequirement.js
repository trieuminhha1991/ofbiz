var recruitPlanBaseRequireObj = (function(){
	var init = function(){
		initWizard();
		initJqxWindow();
		create_spinner($("#spinnerReq"));
	};
	var initJqxGrid = function(){
		var grid = $("#recruitmentReqGrid");
		var datafield = [{name: 'recruitmentRequireId', type: 'string'},
						   {name: 'month', type: 'number'},
						   {name: 'year', type: 'number'},
						   {name: 'emplPositionTypeId', type: 'string'},
						   {name: 'partyId', type: 'string'},
						   {name: 'groupName', type: 'string'},
						   {name: 'quantity', type: 'number'},
						   {name: 'quantityUnplanned', type: 'number'},
						   {name: 'recruitmentFormTypeId', type: 'string'},
						   {name: 'fullName', type: 'string'},
						   {name: 'recruitAnticipatePlanCreated', type: 'bool'},
						   {name: 'enumRecruitReqTypeId', type: 'string'},
						   ];
		var columns = [
		       {text: uiLabelMap.RecruitingPosition, datafield: 'emplPositionTypeId', editable: false, width: '20%',
					columntype: 'dropdownlist', filtertype: 'checkedlist',
					cellsrenderer: function (row, column, value) {
						for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
							if(value == globalVar.emplPositionTypeArr[i].emplPositionTypeId){
								return '<span>' + globalVar.emplPositionTypeArr[i].description + '</span>';
							}
						}
						return '<span>' + value + '</span>';
					},
					createfilterwidget: function(column, columnElement, widget){
						var source = {
						        localdata: globalVar.emplPositionTypeArr,
						        datatype: 'array'
						};		
						var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
					    var dataSoureList = filterBoxAdapter.records;
					    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'emplPositionTypeId'});
					    if(dataSoureList.length > 8){
					    	widget.jqxDropDownList({autoDropDownHeight: false});
					    }else{
					    	widget.jqxDropDownList({autoDropDownHeight: true});
					    }
					}
				},
				{text: uiLabelMap.TimeRecruiting, datafield: 'month', editable: false, width: '14%', filterable: false,
					cellsrenderer: function (row, column, value) {
						var data = grid.jqxGrid('getrowdata', row);
						if(data){
							return '<span>' + uiLabelMap.CommonMonth + ' ' + (value + 1) + '/' + data.year  +'</span>';
						}
					}
				},
				{text: uiLabelMap.CommonDepartment, datafield: 'groupName', editable: false, width: '19%', filterable: false},
				{text: uiLabelMap.RecruitmentEnumType, datafield: 'enumRecruitReqTypeId', editable: false, width: '16%', 
					columntype: 'dropdownlist', filtertype: 'checkedlist',
					cellsrenderer: function (row, column, value) {
						for(i = 0; i < globalVar.recruitReqEnumArr.length; i++){
							if(value == globalVar.recruitReqEnumArr[i].enumId){
								return '<span>' + globalVar.recruitReqEnumArr[i].description + '</span>';
							}
						}
						return '<span>' + value + '</span>';
					},	
					createfilterwidget: function(column, columnElement, widget){
						var source = {
						        localdata: globalVar.recruitReqEnumArr,
						        datatype: 'array'
						};		
						var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
					    var dataSoureList = filterBoxAdapter.records;
					    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'enumId'});
					    if(dataSoureList.length > 8){
					    	widget.jqxDropDownList({autoDropDownHeight: false});
					    }else{
					    	widget.jqxDropDownList({autoDropDownHeight: true});
					    }
					}
				},
				{text: uiLabelMap.PlannedRecruitmentShort, datafield: 'quantity', editable: false, width: '14%', 
					columntype: 'numberinput', filtertype: 'number', cellsalign: 'right',},
				{text: uiLabelMap.QuantityUnplannedShort, datafield: 'quantityUnplanned', editable: false, width: '16%', 
						columntype: 'numberinput', filtertype: 'number', cellsalign: 'right',},
				{text: uiLabelMap.RecruitmentFormType, datafield: 'recruitmentFormTypeId', width: '18%', editable: false, 
					columntype: 'dropdownlist', filtertype: 'checkedlist',
					cellsrenderer: function (row, column, value) {
						for(i = 0; i < globalVar.recruitmentFormTypeArr.length; i++){
							if(value == globalVar.recruitmentFormTypeArr[i].recruitmentFormTypeId){
								return '<span>' + globalVar.recruitmentFormTypeArr[i].description + '</span>';
							}
						}
						return '<span>' + value + '</span>';
					},
					createfilterwidget: function(column, columnElement, widget){
						var source = {
						        localdata: globalVar.recruitmentFormTypeArr,
						        datatype: 'array'
						};		
						var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
					    var dataSoureList = filterBoxAdapter.records;
					    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'recruitmentFormTypeId'});
					    if(dataSoureList.length > 8){
					    	widget.jqxDropDownList({autoDropDownHeight: false});
					    }else{
					    	widget.jqxDropDownList({autoDropDownHeight: true});
					    }
					}
				},
		];
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitmentReqGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentRequireIsApproved + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: 'JQGetListRecruitmentRequireApproved',
				rendertoolbar: rendertoolbar,
				showtoolbar: true,
				width: '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxDateTimeInput = function(){
		$("#recruitmentDateTimeReq").jqxDateTimeInput({width: '98%', height: 25,  selectionMode: 'range' , showFooter: true});
		$("#recruitmentApplyDateTimeReq").jqxDateTimeInput({ width: '98%', height: 25,  selectionMode: 'range', showFooter: true});
		$("#recruitmentDateTimeReq").val(null);
		$("#recruitmentApplyDateTimeReq").val(null);
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxGrid();
			initJqxDateTimeInput();
		};
		createJqxWindow($("#editRecruitPlanRequireWindow"), 870, 580, initContent);
		$("#editRecruitPlanRequireWindow").on('open', function(event){
			/**
			 * editRecruitmentCostItemObj is defined in RecruitmentCostUtils.js
			 * recruitmentCostGridObj is defined in RecruitCreateRecruitCost.js
			 */
			editRecruitmentCostItemObj.setCreateRecruitmentCostItem(recruitmentCostGridObj.createRecruitmentCostItem);
			editRecruitmentCostItemObj.setUpdateRecruitmentCostItem(recruitmentCostGridObj.updateRecruitmentCostItem);
			recruitmentBoardObj.setGridEle($("#recruitmentBoardReqGrid"));//recruitmentBoardObj is defined in RecruitCreateRecruitmentPlanBoard.js
			recruitmentRoundObject.setGridEle($("#recruitmentRoundReqGrid"));//recruitmentRoundObject is defined in RecruitCreateRecruitRound.js
			recruitmentCostGridObj.setGridEle($("#recruitmentCostReqGrid"));//recruitmentSubjectListObj is defined in RecruitCreateRecruitCost.js
			//recruitmentRoundObject.initWindowOpen();
		});
		$("#editRecruitPlanRequireWindow").on('close', function(event){
			recruitmentBoardObj.resetData();
			recruitmentRoundObject.resetData();
			recruitmentCostGridObj.resetData();
			editRecruitmentCostItemObj.setCreateRecruitmentCostItem(null);//editRecruitmentCostItemObj is defined in RecruitmentCostUtils.js
			editRecruitmentCostItemObj.setUpdateRecruitmentCostItem(null);
			$("#recruitmentReqGrid").jqxGrid('clearselection');
			resetStep();
		});
	};
	var initWizard = function(){
		$('#wizardRequirement').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	var selectedRowIndex = $("#recruitmentReqGrid").jqxGrid('getselectedrowindex');
	        	if(selectedRowIndex < 0){
	        		bootbox.dialog(uiLabelMap.RecruitmentRequireNotChoose,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
	        		return false;
	        	}
	        }else if(info.step == 2 && (info.direction == "next")){
	        	if(!recruitmentBoardObj.validate()){
	        		return false;
	        	}
	        	recruitmentRoundObject.setInterviewerMakerData(recruitmentBoardObj.getGridRowData());
	        }else if(info.step == 3 && (info.direction == "next")){
	        	
	        }
	    }).on('finished', function(e) {
	    	bootbox.dialog(uiLabelMap.ConfirmCreateRecruitmentPlan,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createRecruitmentPlan();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
	    }).on('stepclick', function(e){
	    	
	    });
	};
	var getData = function(){
		var retData = {};
		var selectedRowIndex = $("#recruitmentReqGrid").jqxGrid('getselectedrowindex');
		var rowData = $("#recruitmentReqGrid").jqxGrid('getrowdata', selectedRowIndex);
		retData.recruitmentRequireId = rowData.recruitmentRequireId;
		var recruitmentDate = $("#recruitmentDateTimeReq").jqxDateTimeInput('getRange');
		if(recruitmentDate.from && recruitmentDate.to){
			retData.recruitmentFromDate = recruitmentDate.from.getTime();
			retData.recruitmentThruDate = recruitmentDate.to.getTime();
		}
		var recruitmentApplyDate = $("#recruitmentApplyDateTimeReq").jqxDateTimeInput('getRange');
		if(recruitmentApplyDate.from && recruitmentApplyDate.to){
			retData.applyFromDate = recruitmentDate.from.getTime();
			retData.applyThruDate = recruitmentDate.to.getTime();
		}
		return retData;
	};
	var createRecruitmentPlan = function(){	
		var info = getData();
		var board = recruitmentBoardObj.getData();
		var round = recruitmentRoundObject.getData();
		var cost = recruitmentCostGridObj.getData();
		var dataSubmit = $.extend({}, info, board, round, cost);
		
		$("#loadingReq").show();
		disableBtn();
		recruitmentCostGridObj.disable();
		$.ajax({
			url: 'createRecruitmentPlan',
			type: 'POST',
			data: dataSubmit,
			success: function(response){
				if(response.responseMessage == "success"){
					$('#containerNtf').empty();
					$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#notificationContentNtf").text(response.successMessage);
					$("#jqxNotificationNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#editRecruitPlanRequireWindow").jqxWindow('close');
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
				$("#loadingReq").hide();
				enableBtn();
				recruitmentCostGridObj.enable();
			}
		});
	};
	var disableBtn = function(){
		$("#btnNextReq").attr("disabled", "disabled");
		$("#btnPrevReq").attr("disabled", "disabled");
	};
	var enableBtn = function(){
		$("#btnNextReq").removeAttr("disabled");
		$("#btnPrevReq").removeAttr("disabled");
	};
	var openWindow = function(){
		openJqxWindow($("#editRecruitPlanRequireWindow"));
	};
	var resetStep = function(){
		$('#wizardRequirement').wizard('previous');
		$('#wizardRequirement').wizard('previous');
		$('#wizardRequirement').wizard('previous');
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	recruitPlanBaseRequireObj.init();
	recruitmentBoardObj.initJqxGrid("recruitmentBoardReqGrid");//recruitmentBoardObj is defined in RecruitCreateRecruitmentPlanBoard.js
	recruitmentRoundObject.initJqxGrid("recruitmentRoundReqGrid");//recruitmentRoundObject is defined in RecruitCreateRecruitRound.js
	recruitmentCostGridObj.initJqxGrid("recruitmentCostReqGrid");//recruitmentCostGridObj is defined in RecruitCreateRecruitCost.js
});