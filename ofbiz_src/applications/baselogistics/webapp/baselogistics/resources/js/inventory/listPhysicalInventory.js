$(function(){
	PhysicInvObj.init();
});
var PhysicInvObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		
	};
	var initInputs = function(){
		$("#PhysicalInvMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initElementComplex = function(){
		
	};

	var initEvents = function(){
		$("#PhysicalInvMenu").on('itemclick', function (event) {
			var data = $('#jqxgridPhysicalInventory').jqxGrid('getRowData', $("#jqxgridPhysicalInventory").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.ViewDetailInNewPage){
				window.open("viewDetailPhysicalInventory?physicalInventoryId=" + data.physicalInventoryId, '_blank');
			} else if(tmpStr == uiLabelMap.BSViewDetail){
				window.location.href = "viewDetailPhysicalInventory?physicalInventoryId=" + data.physicalInventoryId;
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridPhysicalInventory').jqxGrid('updatebounddata');
			}
		});
	};
	
	var initValidateForm = function(){
		
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
	
	var detailPhysicalInventory = function detailPhysicalInventory(value){
		href = "viewDetailPhysicalInventory?physicalInventoryId="+value;
		window.location.href = href;
	};
	
	var prepareCreatePhysicalInventory = function prepareCreatePhysicalInventory(value){
		href = "prepareCreatePhysicalInventory";
		window.location.href = href;
	};
	
	return {
		init: init,
		detailPhysicalInventory: detailPhysicalInventory,
		prepareCreatePhysicalInventory: prepareCreatePhysicalInventory,
	}
}());