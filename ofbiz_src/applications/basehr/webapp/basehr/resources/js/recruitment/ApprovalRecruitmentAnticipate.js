var apprRecruitAnticipateObj = (function(){
	var _hasPermission = false;
	var _gridRecruitAnticipateHeaderContent = "";
	var _gridRecruitAnticipateHeader = "gridRecruitAnticipateHeader";
	var _gridId = "apprRecruitAnticipateGrid";
	var _groupName = null;
	var _emplPositionTypeDesc = null;
	var _statusListAppr = [];
	var _statusNeedAppr = null;
	var init = function(){
		initGrid();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerApprRecruitAnticipateWindow"));
		initJqxNotification();
	};
	var initJqxNotification = function(){
		$("#jqxNotificationapprRecruitAnticipateGrid").jqxNotification({ width: "100%", appendContainer: "#containerapprRecruitAnticipateGrid", opacity: 0.9, template: "info" });
	};
	var initGrid = function(){
		var datafield = [
		                 {name: 'recruitAnticipateId', type: 'string'},
		                 {name: 'recruitAnticipateSeqId', type: 'string'},
		                 {name: 'month', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'newStatusId', type: 'string'},
		                 {name: 'changeReason', type: 'string'},
		];
		var columns = [
		                  {datafield: 'recruitAnticipateId', hidden: true},
		                  {datafield: 'recruitAnticipateSeqId', hidden: true},
		                  {datafield: 'statusId', hidden: true},
		                  {text: uiLabelMap.HRCommonTime, datafield: 'month', width: '14%', editable: false},
		                  {text : uiLabelMap.HRCommonQuantity, cellsalign: 'right',  width : '12%', dataField : 'quantity',  columntype: 'numberinput', editable: false},
		                  {text: uiLabelMap.CommonStatus, datafield: 'newStatusId', width: '23%', editable: true,
								columntype: 'dropdownlist', filtertype: 'checkedlist',
								cellsrenderer: function (row, column, value) {
									for(var i = 0; i < globalVar.statusArr.length; i++){
										if(value == globalVar.statusArr[i].statusId){
											return '<span>' + globalVar.statusArr[i].description + '</span>'; 
										}
									}
									return '<span>' + value + '</span>';
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									createJqxDropDownList(_statusListAppr, editor, "statusId", "description", cellheight, cellwidth);
								},
								initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
							        editor.val(cellvalue);
							    },
							    cellbeginedit: function (row, datafield, columntype) {
							    	var data = $("#" + _gridId).jqxGrid('getrowdata', row);
							    	var statusId = data.statusId;
							    	if(_statusNeedAppr && statusId == _statusNeedAppr){
							    		return true;
							    	}
							    	return false;
							    }
						  },
						  {text: uiLabelMap.HRNotes, datafield: 'changeReason', editable: true,
							  cellbeginedit: function (row, datafield, columntype) {
								  var data = $("#" + _gridId).jqxGrid('getrowdata', row);
							    	var statusId = data.statusId;
							    	if(_statusNeedAppr && statusId == _statusNeedAppr){
							    		return true;
							    	}
							    	return false;
							  }
						  },
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
		        selectionmode: 'multiplerows',
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
	var initEvent = function(){
		$("#cancelApprRecruitAnticipate").click(function(event){
			$("#ApprRecruitAnticipateWindow").jqxWindow('close');
		});
		$("#acceptRecruitAnticipateItemSelected").click(function(event){
			var rowIndexSelected = $("#" + _gridId).jqxGrid('getselectedrowindexes');
			if(rowIndexSelected.length == 0){
				bootbox.dialog(uiLabelMap.NoRowSelected,
						[
						{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
				return;
			}
			var data = [];
			for(var i = 0; i < rowIndexSelected.length; i++){
				data.push($("#" + _gridId).jqxGrid('getrowdata', rowIndexSelected[i]));
			}
			changeStatusRow(data, "accept");
			$("#" + _gridId).jqxGrid('hidevalidationpopups');
		});
		$("#acceptAllApprRecruitAnticipate").click(function(event){
			var data = $("#" + _gridId).jqxGrid('getrows');
			changeStatusRow(data, "accept");
			$("#" + _gridId).jqxGrid('hidevalidationpopups');
		});
		$("#rejectRecruitAnticipateItemSelected").click(function(event){
			var rowIndexSelected = $("#" + _gridId).jqxGrid('getselectedrowindexes');
			if(rowIndexSelected.length == 0){
				bootbox.dialog(uiLabelMap.NoRowSelected,
						[
						{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
				return;
			}
			var data = [];
			for(var i = 0; i < rowIndexSelected.length; i++){
				data.push($("#" + _gridId).jqxGrid('getrowdata', rowIndexSelected[i]));
			}
			changeStatusRow(data, "reject");
			$("#" + _gridId).jqxGrid('hidevalidationpopups');
		});
		$("#rejectAllRecruitAnticipateItem").click(function(event){
			var data = $("#" + _gridId).jqxGrid('getrows');
			changeStatusRow(data, "reject");
			$("#" + _gridId).jqxGrid('hidevalidationpopups');
		});
		$("#saveApprRecruitAnticipate").click(function(event){
			var valid = true;
			var rowsData = $("#" + _gridId).jqxGrid('getrows');
			for(var i = 0; i < rowsData.length; i++){
				var rowData = rowsData[i];
				if(rowData.newStatusId == _statusNeedAppr){
					valid = false;
					var rowIndexErr = $('#apprRecruitAnticipateGrid').jqxGrid('getrowboundindexbyid', rowData.uid);
					$("#" + _gridId).jqxGrid('showvalidationpopup', rowIndexErr, "newStatusId", uiLabelMap.HRNotApproval);
					break;
				}
			}
			if(!valid){
				return;
			}
			apprRecruitmentAnticipateItem();
		});
		$("#" + _gridId).on('cellbeginedit', function (event){
			$("#" + _gridId).jqxGrid('hidevalidationpopups');
		});
	};
	var changeStatusRow = function(dataArr, statusCode){
		var newStatusId = "";
		for(var i = 0; i < _statusListAppr.length; i++){
			if(_statusListAppr[i].statusCode == statusCode && _statusListAppr[i].statusId != _statusNeedAppr){
				newStatusId = _statusListAppr[i].statusId;
				break;
			}
		}
		for(var i = 0; i < dataArr.length; i++){
			var data = dataArr[i];
			var currStatusId = data.statusId;
			if(currStatusId == _statusNeedAppr){
				data.newStatusId = newStatusId;
				$("#" + _gridId).jqxGrid('updaterow', data.uid, data);
			}
		}
		$("#" + _gridId).jqxGrid('clearselection');
	}
	var apprRecruitmentAnticipateItem = function(){
		var rows = $("#" + _gridId).jqxGrid('getrows');
		var dataSubmit = [];
		for(var i = 0; i < rows.length; i++){
			var tempData = rows[i];
			var row = {
				recruitAnticipateId: tempData.recruitAnticipateId, 
				recruitAnticipateSeqId: tempData.recruitAnticipateSeqId, 
				statusId: tempData.newStatusId, 
			};
			if(tempData.changeReason){
				row.changeReason = tempData.changeReason; 
			}
			dataSubmit.push(row);
		}
		var selectedIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var rowdata = $("#jqxgrid").jqxGrid('getrowdata', selectedIndex);
		$("#loadingApprRecruitAnticipateWindow").show();
		disableAll();
		$.ajax({
			url: 'approvalRecruitmentAnticipate',
			type: 'POST',
			data: {apprRecruitAnticipateItemList: JSON.stringify(dataSubmit), recruitAnticipateId: rowdata.recruitAnticipateId},
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage("jqxgrid", response.successMessage, {autoClose: true,
						template : 'info',
						appendContainer : "#containerjqxgrid",
						opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#ApprRecruitAnticipateWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response.errorMessage,
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
				$("#loadingApprRecruitAnticipateWindow").hide();
				enableAll();
			}
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#ApprRecruitAnticipateWindow"), 750, 570);
		$("#ApprRecruitAnticipateWindow").on("close", function(event){
			$("#apprPartyId").html("");
			$("#apprEmplPositionType").html("");
			_groupName = null;
			_emplPositionTypeDesc = null;
			_hasPermission = false;
			$("#" + _gridId).jqxGrid('clearselection');
			$("#" + _gridId).jqxGrid({editable: false});
			var source = $("#" + _gridId).jqxGrid("source");
			source._source.localdata = [];
			$("#" + _gridId).jqxGrid("source", source);
			_statusListAppr = [];
			_statusNeedAppr = null;
		});
		
		$("#ApprRecruitAnticipateWindow").on("open", function(event){
			var year = $("#year").val();
			_gridRecruitAnticipateHeaderContent = uiLabelMap.RecruitmentAnticipate + " " + year;
			if($("#" + _gridRecruitAnticipateHeader).length > 0){
				$("#" + _gridRecruitAnticipateHeader).html(_gridRecruitAnticipateHeaderContent);
			}
		});
	};
	var setData = function(data){
		//var yearCustomTimeSelected = $("#yearCustomTimePeriod").jqxDropDownList('getSelectedItem');
		_groupName = data.groupName;
		_emplPositionTypeDesc = data.emplPositionTypeDesc;
		$("#apprPartyId").html(data.groupName);
		$("#apprEmplPositionType").html(data.emplPositionTypeDesc);
		refreshGrid(data.recruitAnticipateId);
		if(globalVar.hasPermissionAppr){
			loadDataForApprRecruitmentAnticipate(data);
		}
	};
	var loadDataForApprRecruitmentAnticipate = function(data){
		$("#loadingApprRecruitAnticipateWindow").show();
		disableAll();
		$.when(
				$.ajax({
					url: 'checkPermissionApprRecruitmentAnticipate',
					type: 'POST',
					data: {recruitAnticipateId: data.recruitAnticipateId},
					success: function(response){
						if(response._EVENT_MESSAGE_){
							_hasPermission = response.hasPermission;
						}
					},
					complete: function(jqXHR, textStatus){
						prepareApprRecruitmentAnticipate();
					}
				}),
				$.ajax({
					url: 'getStatusListApprRecruitmentAnticipate',
					type: 'POST',
					data: {recruitAnticipateId: data.recruitAnticipateId},
					success: function(response){
						if(response.statusIdNeedAppr){
							_statusNeedAppr = response.statusIdNeedAppr;
							_statusListAppr = response.statusList;
						}
					},
					complete: function(jqXHR, textStatus){
						
					}
				})
		).done(function(a1, a2){
			$("#loadingApprRecruitAnticipateWindow").hide();
			enableAll();
		});
		
	};
	var prepareApprRecruitmentAnticipate = function(){
		if(_hasPermission){
			$("#" + _gridId).jqxGrid({editable: true, editmode: 'dblclick'});
			$("#apprBtn").show();
			$("#saveApprRecruitAnticipate").show();
		}else{
			$("#" + _gridId).jqxGrid({editable: false});
			$("#apprBtn").hide();
			$("#saveApprRecruitAnticipate").hide();
		}
	};
	var disableAll = function(){
		$("#cancelApprRecruitAnticipate").attr("disabled", "disabled");
		$("#acceptRecruitAnticipateItem").attr("disabled", "disabled");
		$("#acceptAllApprRecruitAnticipate").attr("disabled", "disabled");
		$("#rejectRecruitAnticipateItemSelected").attr("disabled", "disabled");
		$("#rejectAllRecruitAnticipateItem").attr("disabled", "disabled");
		$("#saveApprRecruitAnticipate").attr("disabled", "disabled");
		$("#" + _gridId).jqxGrid({disabled: true});
	};
	var enableAll = function(){
		$("#cancelApprRecruitAnticipate").removeAttr("disabled");
		$("#acceptRecruitAnticipateItem").removeAttr("disabled");
		$("#acceptAllApprRecruitAnticipate").removeAttr("disabled");
		$("#rejectRecruitAnticipateItemSelected").removeAttr("disabled");
		$("#rejectAllRecruitAnticipateItem").removeAttr("disabled");
		$("#saveApprRecruitAnticipate").removeAttr("disabled");
		$("#" + _gridId).jqxGrid({disabled: false});
	};
	var refreshGrid = function(recruitAnticipateId){
		$("#" + _gridId).jqxGrid('showloadelement');
		$("#" + _gridId).jqxGrid({disabled: true});
		var tmpS = $("#" + _gridId).jqxGrid('source');
		$.ajax({
			url: 'getRecruitmentAnticipateItemList',
			type: 'POST',
			data: {recruitAnticipateId: recruitAnticipateId},
			success: function(response){
				if(response.listReturn){
					tmpS._source.localdata = response.listReturn;
				}else{
					tmpS._source.localdata = [];
				}
				$("#" + _gridId).jqxGrid('source', tmpS);
			},
			complete: function(jqXHR, textStatus){
				$("#" + _gridId).jqxGrid('hideloadelement');
				$("#" + _gridId).jqxGrid({disabled: false});
			}
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#ApprRecruitAnticipateWindow"));
	};
	
	return{
		init: init,
		setData: setData,
		apprRecruitmentAnticipateItem: apprRecruitmentAnticipateItem,
		//getGridRowDataById: getGridRowDataById,
		//updateGrid: updateGrid,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	apprRecruitAnticipateObj.init();
});