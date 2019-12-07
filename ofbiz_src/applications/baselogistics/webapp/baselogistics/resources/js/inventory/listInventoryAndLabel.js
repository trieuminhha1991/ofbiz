$(function(){
	InvAndLabelObj.init();
});
var InvAndLabelObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function(){
		$("#editpopupWindow").jqxWindow({
			maxWidth: 1200, minWidth: 300, width: 550, minHeight: 100, height: 200, maxHeight: 1000, resizable: false, modalZIndex: 10000, isModal: true, autoOpen: false, cancelButton: $("#editCancel"), modalOpacity: 0.7, theme:theme         
	    });
		initLabelList($("#inventoryItemLabelId"), []);
		$("#contextMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initElementComplex = function(){
		
	};
	var initValidateForm = function(){
		
	};
	var initEvents = function(){
		$("#editpopupWindow").on('close', function (){
			$("#inventoryItemLabelId").jqxComboBox('clearSelection');
		});
		
		$("#contextMenu").on('itemclick', function (event) {
//			var data = $('#jqxgridItemAndLabel').jqxGrid('getRowData', $("#jqxgridItemAndLabel").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridItemAndLabel').jqxGrid('updatebounddata');
			}
		});
		
		$("#editSave").click(function (){
			var lables = $("#inventoryItemLabelId").jqxComboBox('getSelectedItems'); 
			var listInventoryItemLabelIds = [];
			if (lables.length > 0){
				for (var i = 0; i < lables.length; i ++){
					var map = {
						inventoryItemLabelId: lables[i].originalItem.inventoryItemLabelId,
					}
					listInventoryItemLabelIds.push(map);
				}
				var list2 = JSON.stringify(listInventoryItemLabelIds);
				bootbox.dialog(uiLabelMap.AreYouSureSave, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	Loading.show('loadingMacro');
				    	setTimeout(function(){
							$.ajax({
					    		url: "createInventoryItemLabelAppls",
					    		type: "POST",
					    		async: false,
					    		data: {
					    			inventoryItemId: $("#inventoryItemId").val(),
					    			listInventoryItemLabelIds: list2,
					    		},
					    		success: function (res){
					    			$("#jqxgridItemAndLabel").jqxGrid('updatebounddata'); 
					    			$("#editpopupWindow").jqxWindow('close');
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
			} else {
    			$("#editpopupWindow").jqxWindow('close');
			}
		});
	};
	
	var initLabelList = function(comboBox, selectArr){
		var configLabelList = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: "inventoryItemLabelId",
    		value: "description",
			width:'100%',
			height: 25,
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=jqGetInventoryItemLabels',
		};
		new OlbComboBox(comboBox, null, configLabelList, selectArr);
	};
	
	var prepareAssignLabelInventory = function (){
		href = "prepareAssignLabelInventory";
		window.location.href = href;
	};
	
	var getLocalization = function () {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	return {
		init: init,
		prepareAssignLabelInventory: prepareAssignLabelInventory,
	}
}());