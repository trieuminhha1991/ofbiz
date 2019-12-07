var recruitmentCostItemListObj = (function(){
	var _recruitmentPlanId = null;
	var _editRowId = null;
	var init = function(){
		initJqxGrid();
		initJqxWindow();
		initEvent();
		initJqxNotification();
	};
	
	var initJqxGrid = function(){
		var datafield = recruitmentCostUtilObj.getDataField();//recruitmentCostUtilObj is defined in RecruitmentViewCostItemList.js
		var columns = recruitmentCostUtilObj.getColumns();
		var grid = $("#recruitmentCostItemListGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitmentCostItemListGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentCostList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        if(globalVar.hasPermissionAdmin){
	        	Grid.createAddRowButton(
	        			grid, container, uiLabelMap.CommonAddNew, {
	        				type: "popup",
	        				container: $("#addRecruitmentCostWindow"),
	        			}
	        	);
	        	Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
	        			"", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
	        }
	        /*Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);*/
		};
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				source: {
					pagesize : 10,
					addColumns: 'recruitmentPlanId;recruitCostItemTypeId;amount(java.math.BigDecimal);comment',
					createUrl: 'jqxGeneralServicer?jqaction=C&sname=createRecruitmentCostItem',
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateRecruitmentCostItem",
					editColumns: "recruitmentPlanId;recruitCostItemTypeId;amount(java.math.BigDecimal);comment",
					removeUrl: "jqxGeneralServicer?jqaction=D&sname=deleteRecruitmentCostItem", 
					deleteColumns: "recruitmentPlanId;recruitCostItemTypeId"
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var createRecruitmentCostItem = function(isCloseWindow){
		var recruitCostItemTypeId = $("#recruitCostItemNew").val();
		var row = {
				recruitmentPlanId: _recruitmentPlanId,
				recruitCostItemTypeId: recruitCostItemTypeId,
				recruitCostCatName: $("#recruitCostCatId").val(),
				amount: $("#recruitCostItemAmount").val(),
				comment: $("#recruitCostComment").val()
		};
		$("#recruitmentCostItemListGrid").jqxGrid('addrow', null, row, 'first');
		if(isCloseWindow){
			$("#addRecruitmentCostWindow").jqxWindow('close');
		}
	};
	var updateRecruitmentCostItem = function(){
		var recruitCostItemTypeId = $("#recruitCostItemNew").val();
		var row = {
				recruitmentPlanId: _recruitmentPlanId,
				recruitCostItemTypeId: recruitCostItemTypeId,
				recruitCostCatName: $("#recruitCostCatId").val(),
				amount: $("#recruitCostItemAmount").val(),
				comment: $("#recruitCostComment").val()
		};
		if(_editRowId != null){
			$("#recruitmentCostItemListGrid").jqxGrid('updaterow', _editRowId, row);
		}
		$("#addRecruitmentCostWindow").jqxWindow('close');
	};
	var initJqxWindow = function(){
		createJqxWindow($("#RecruitmentCostItemListWindow"), 850, 530);
		$("#RecruitmentCostItemListWindow").on('close', function(event){
			_recruitmentPlanId = null;
			_editRowId = null;
			if(globalVar.hasPermissionAdmin){
				editRecruitmentCostItemObj.setCreateRecruitmentCostItem(null);
				editRecruitmentCostItemObj.setUpdateRecruitmentCostItem(null);
			}
		});
		$("#RecruitmentCostItemListWindow").on('open', function(event){
			if(globalVar.hasPermissionAdmin){
				editRecruitmentCostItemObj.setCreateRecruitmentCostItem(createRecruitmentCostItem);
				editRecruitmentCostItemObj.setUpdateRecruitmentCostItem(updateRecruitmentCostItem);
			}
			refreshGridData(_recruitmentPlanId);
		});
	};
	var initEvent = function(){
		$("#recruitmentCostItemListGrid").on('rowdoubleclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			//_isEditMode = true;
			if(globalVar.hasPermissionAdmin){
				editRecruitmentCostItemObj.setEditMode(true);
			}
			var data = $("#recruitmentCostItemListGrid").jqxGrid('getrowdata', boundIndex);
			_editRowId = data.uid;
			$("#recruitCostItemNew").val(data.recruitCostItemTypeId);
			$("#recruitCostItemAmount").val(data.amount);
			$("#recruitCostComment").val(data.comment);
			openJqxWindow($("#addRecruitmentCostWindow"));
		});
	};
	var setData = function(data){
		_recruitmentPlanId = data.recruitmentPlanId;
	};
	var openWindow = function(){
		openJqxWindow($("#RecruitmentCostItemListWindow"));
	};
	var initJqxNotification = function(){
		$("#jqxNotificationrecruitmentCostItemListGrid").jqxNotification({ width: "100%", 
			appendContainer: "#containerrecruitmentCostItemListGrid", opacity: 0.9, template: "info" });
	};
	var refreshGridData = function(recruitmentPlanId){
		var tempS = $("#recruitmentCostItemListGrid").jqxGrid('source');
		tempS._source.url = "jqxGeneralServicer?sname=JQGetRecruitmentListCostItem&recruitmentPlanId=" + recruitmentPlanId;
		$("#recruitmentCostItemListGrid").jqxGrid('source', tempS);
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());

var recruitmentCostUtilObj = (function(){
	var getDataField = function(){
		return [{name: 'recruitmentPlanId', type: 'string'},
		        {name: 'recruitCostItemTypeId', type: 'string'},
		         {name: 'recruitCostCatName', type:'string'},
		         {name: 'amount', type:'number'},
		         {name: 'comment', type: 'string'}];
	};
	var getColumns = function(){
		return [
		        	{datafield: 'recruitmentPlanId', hidden: true},
	               {text: uiLabelMap.RecruitmentCostItemName, datafield: 'recruitCostItemTypeId', width: '25%', columntype: 'dropdownlist',
	            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	            		   for(var i = 0; i < globalVar.recruitmentCostItemArr.length; i++){
	            			   if(value == globalVar.recruitmentCostItemArr[i].recruitCostItemTypeId){
	            				   return '<span>' + globalVar.recruitmentCostItemArr[i].recruitCostItemName + '</span>';
	            			   }
	            		   }
	            		   return '<span>' + value + '</span>';
	            	   },
	               },
	               {text: uiLabelMap.RecruitmentCostCategory, datafield: 'recruitCostCatName', width: '25%'},
	               {text: uiLabelMap.HRCommonAmount, datafield: 'amount', width: '25%', columntype: 'numberinput',
	            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	            		   return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
	            	   },
	               },
	               {text: uiLabelMap.HRNotes, datafield: 'comment', width: '25%'},
	               ];
	};
	return{
		getDataField: getDataField,
		getColumns: getColumns
	}
}());

$(document).ready(function(){
	recruitmentCostItemListObj.init();
});