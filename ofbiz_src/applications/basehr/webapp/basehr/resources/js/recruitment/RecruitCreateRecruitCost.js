var recruitmentCostGridObj = (function(){
	//var _isEditMode = false;
	var _gridEle = null;
	var init = function(){
		//initJqxGrid();
		initEvent();
	};
	
	var initJqxGrid = function(gridId){
		var datafield = recruitmentCostUtilObj.getDataField();//recruitmentCostUtilObj is defined in RecruitmentViewCostItemList.js
		var columns = recruitmentCostUtilObj.getColumns();
		
		var grid = $("#" + gridId);
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = gridId;
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentCostList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		grid, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#addRecruitmentCostWindow"),
		        	}
		        );
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		
		var config = {
				url: '',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: false,
				editable: false,
				localization: getLocalization(),
				source: {pagesize : 10, id: 'recruitCostItemTypeId'}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		grid.on('rowdoubleclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			//_isEditMode = true;
			editRecruitmentCostItemObj.setEditMode(true);
			var data = grid.jqxGrid('getrowdata', boundIndex);
			$("#recruitCostItemNew").val(data.recruitCostItemTypeId);
			$("#recruitCostItemAmount").val(data.amount);
			$("#recruitCostComment").val(data.comment);
			openJqxWindow($("#addRecruitmentCostWindow"));
		});
	};
	
	var initEvent = function(){
		
	};
	
	var updateRecruitmentCostItem = function(){
		var recruitCostItemTypeId = $("#recruitCostItemNew").val();
		var checkExists = _gridEle.jqxGrid('getrowdatabyid', recruitCostItemTypeId);
		if(!checkExists){
			createRecruitmentCostItem(true);
			return;
		}
		var row = {
				recruitCostItemTypeId: recruitCostItemTypeId,
				recruitCostCatName: $("#recruitCostCatId").val(),
				amount: $("#recruitCostItemAmount").val(),
				comment: $("#recruitCostComment").val()
		};
		_gridEle.jqxGrid('updaterow', recruitCostItemTypeId, row);
		$("#addRecruitmentCostWindow").jqxWindow('close');
	};
	
	var createRecruitmentCostItem = function(isCloseWindow){
		var recruitCostItemTypeId = $("#recruitCostItemNew").val();
		var checkExists = _gridEle.jqxGrid('getrowdatabyid', recruitCostItemTypeId);
		if(checkExists){
			bootbox.dialog(uiLabelMap.RecruitmentCostItemIsAdded,
					[
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
			return;
		}
		var row = {
				recruitCostItemTypeId: recruitCostItemTypeId,
				recruitCostCatName: $("#recruitCostCatId").val(),
				amount: $("#recruitCostItemAmount").val(),
				comment: $("#recruitCostComment").val()
		};
		_gridEle.jqxGrid('addrow', null, row, 'first');
		if(isCloseWindow){
			$("#addRecruitmentCostWindow").jqxWindow('close');
		}
	};
	
	var getData = function(){
		var rows = _gridEle.jqxGrid('getrows');
		var data = [];
		for(var i = 0; i < rows.length; i++){
			data.push({recruitCostItemTypeId: rows[i].recruitCostItemTypeId, amount: rows[i].amount, comment: rows[i].comment});
		}
		return {recruitmentCostList : JSON.stringify(data)}; 
	};
	
	var resetData = function(){
		var source = _gridEle.jqxGrid('source');
		source._source.localdata = [];
		_gridEle.jqxGrid('source', source);
	};
	
	var disable = function(){
		_gridEle.jqxGrid({ disabled: true}); 
		$("#addrowbuttonrecruitmentCostGrid").attr("disabled", "disabled");
		$("#deleterowbuttonrecruitmentCostGrid").attr("disabled", "disabled");
	};
	var enable = function(){
		_gridEle.jqxGrid({ disabled: false}); 
		$("#addrowbuttonrecruitmentCostGrid").removeAttr("disabled");
		$("#deleterowbuttonrecruitmentCostGrid").removeAttr("disabled");
	};
	var setGridEle = function(gridEle){
		_gridEle = gridEle;
	};
	return{
		init: init,
		getData: getData,
		resetData: resetData,
		disable: disable,
		enable: enable,
		createRecruitmentCostItem: createRecruitmentCostItem,
		updateRecruitmentCostItem: updateRecruitmentCostItem,
		initJqxGrid: initJqxGrid,
		setGridEle: setGridEle
	}
}());
