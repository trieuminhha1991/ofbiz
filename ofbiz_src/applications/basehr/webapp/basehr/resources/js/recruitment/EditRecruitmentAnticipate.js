var editRecruitmentAnticipateObj = (function(){
	var _gridRecruitAnticipateHeaderContent = "";
	var _gridRecruitAnticipateHeader = "gridEditRecruitAnticipateHeader";
	var _gridId = "editRecruitAnticipateGrid";
	var init = function(){
		//var yearCustomTimeSelected = null;
		initGrid();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerEditRecruitAnticipateWindow"));
	};
	var initGrid = function(){
		var datafield = [
		                 {name: 'recruitAnticipateId', type: 'string'},
		                 {name: 'recruitAnticipateSeqId', type: 'string'},
		                 {name: 'month', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'apprReason', type: 'string'},
		                 {name: 'changeReason', type: 'string'},
		];
		var columns = [
		                  {datafield: 'recruitAnticipateId', hidden: true},
		                  {datafield: 'recruitAnticipateSeqId', hidden: true},
		                  {text: uiLabelMap.HRCommonTime, datafield: 'month', width: '12%', editable: false},
		                  {text : uiLabelMap.HRCommonQuantity, cellsalign: 'right',  width : '10%', dataField : 'quantity',  columntype: 'numberinput', 
		                	  	editable: true,
		                	  	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxNumberInput({width: cellwidth, height: cellheight, inputMode: 'simple', decimalDigits: 0});
								},
							    validation: function(cell, value){
							        if (value < 0) {
							            return { result: false, message: uiLabelMap.ValueMustBeGreateThanZero};
							        }
							        return true;
							    }
		                  },
		                  {text: uiLabelMap.CommonStatus, datafield: 'statusId', width: '18%', editable: false,
								columntype: 'dropdownlist', filtertype: 'checkedlist',
								cellsrenderer: function (row, column, value) {
									for(var i = 0; i < globalVar.statusArr.length; i++){
										if(value == globalVar.statusArr[i].statusId){
											return '<span>' + globalVar.statusArr[i].description + '</span>'; 
										}
									}
									return '<span>' + value + '</span>';
								},
						  },
						  {text: uiLabelMap.ApprovalReason, datafield: 'apprReason', editable: false, width: '30%'},
						  {text: uiLabelMap.HRNotes, datafield: 'changeReason', editable: true, width: '30%'},
		];
		var grid = $("#" + _gridId);
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = _gridId;
			var me = this;
	        var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5 id='" + _gridRecruitAnticipateHeader + "'>" + _gridRecruitAnticipateHeaderContent + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
		   		width: '100%', 
		   		virtualmode: false,
		   		showfilterrow: true,
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: true,
		        url: '',    
		        selectionmode: 'singlerow',
	   			showtoolbar: true,
	   			rendertoolbar: rendertoolbar,
	   			pagesizeoptions: [12],
	   			pagesize: 12,
	        	source: {
		   			id: 'recruitAnticipateSeqId'
	        	}
		 };
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#EditRecruitAnticipateWindow"), 800, 580);
		$("#EditRecruitAnticipateWindow").on('close', function(event){
			$("#editPartyId").html("");
			$("#editEmplPositionType").html("");
			$("#" + _gridId).jqxGrid('clearselection');
			var source = $("#" + _gridId).jqxGrid("source");
			source._source.localdata = [];
			$("#" + _gridId).jqxGrid("source", source);
		});
		$("#EditRecruitAnticipateWindow").on('open', function(event){
			year = $("#year").val();
			_gridRecruitAnticipateHeaderContent = uiLabelMap.RecruitmentAnticipate + " " + year;
			if($("#" + _gridRecruitAnticipateHeader).length > 0){
				$("#" + _gridRecruitAnticipateHeader).html(_gridRecruitAnticipateHeaderContent);
			}
		});
	};
	var initEvent = function(){
		$("#cancelEditRecruitAnticipate").click(function(event){
			$("#EditRecruitAnticipateWindow").jqxWindow('close');
		});
		$("#saveEditRecruitAnticipate").click(function(event){
			var rows = $("#" + _gridId).jqxGrid('getrows');
			if(rows.length == 0){
				return;
			}
			bootbox.dialog(uiLabelMap.UpdateRecruitmentAnticipateConfirm,
					[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "btn-primary btn-small icon-ok open-sans",
					    "callback": function(){
					    	updateRecruitmentAnticipate();
					    }
					},
					{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		}]		
				);
		});
	};
	var updateRecruitmentAnticipate = function(){
		var rows = $("#" + _gridId).jqxGrid('getrows');
		var dataSubmit = [];
		for(var i = 0; i < rows.length; i++){
			var tempData = rows[i];
			var row = {
				recruitAnticipateId: tempData.recruitAnticipateId, 
				recruitAnticipateSeqId: tempData.recruitAnticipateSeqId, 
				quantity: tempData.quantity, 
			};
			if(tempData.changeReason){
				row.changeReason = tempData.changeReason; 
			}
			dataSubmit.push(row);
		}
		var selectedIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var rowdata = $("#jqxgrid").jqxGrid('getrowdata', selectedIndex);
		$("#loadingEditRecruitAnticipateWindow").show();
		disableAll();
		$.ajax({
			url: 'editRecruitmentAnticipate',
			type: 'POST',
			data: {editRecruitAnticipateItemList: JSON.stringify(dataSubmit), recruitAnticipateId: rowdata.recruitAnticipateId},
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage("jqxgrid", response._EVENT_MESSAGE_, {autoClose: true,
						template : 'info',
						appendContainer : "#containerjqxgrid",
						opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#EditRecruitAnticipateWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}
							]		
						);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingEditRecruitAnticipateWindow").hide();
				enableAll();
			}
		});
	};
	var disableAll = function(){
		$("#cancelEditRecruitAnticipate").attr("disabled", "disabled");
		$("#saveEditRecruitAnticipate").attr("disabled", "disabled");
		$("#" + _gridId).jqxGrid({disabled: true});
	};
	var enableAll = function(){
		$("#cancelEditRecruitAnticipate").removeAttr("disabled");
		$("#saveEditRecruitAnticipate").removeAttr("disabled");
		$("#" + _gridId).jqxGrid({disabled: false});
	};
	var openWindow = function(){
		openJqxWindow($("#EditRecruitAnticipateWindow"));
	};
	var setData = function(data){
		//var yearCustomTimeSelected = $("#yearCustomTimePeriod").jqxDropDownList('getSelectedItem');
		$("#editPartyId").html(data.groupName);
		$("#editEmplPositionType").html(data.emplPositionTypeDesc);
		refreshGrid(data.recruitAnticipateId);
	};
	var refreshGrid = function(recruitAnticipateId){
		$("#" + _gridId).jqxGrid('showloadelement');
		$("#" + _gridId).jqxGrid({disabled: true});
		var tmpS = $("#" + _gridId).jqxGrid('source');
		$.ajax({
			url: 'getRecruitAnticipateItemListCanEdit',
			type: 'POST',
			data: {recruitAnticipateId: recruitAnticipateId},
			success: function(response){
				if(response.listReturn){
					tmpS._source.localdata = response.listReturn;
				}else{
					tmpS._source.localdata = [];
				}
				$("#" + _gridId).jqxGrid('source', tmpS);
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
				    		    "label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",
				    		}]		
						);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#" + _gridId).jqxGrid('hideloadelement');
				$("#" + _gridId).jqxGrid({disabled: false});
			}
		});
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	editRecruitmentAnticipateObj.init();
});