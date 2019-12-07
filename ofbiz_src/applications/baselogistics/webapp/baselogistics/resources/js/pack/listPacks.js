$(function(){
	ListPackObj.init();
});
var ListPackObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		if ($("#PackMenu").length > 0){
			$("#PackMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		}
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
		$("#PackMenu").on('itemclick', function (event) {
			var data = $('#jqxgridPack').jqxGrid('getRowData', $("#jqxgridPack").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.ViewDetailInNewPage){

					window.open("viewOrder?orderId=" + data.orderId, '_blank');

			} else if(tmpStr == uiLabelMap.BSViewDetail){

					window.location.href = "viewOrder?orderId=" + data.orderId;

			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridPack').jqxGrid('updatebounddata');
			}
		});
	};
	var initValidateForm = function (){
	};
	var getLocalization = function getLocalization() {
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
	
	var showDetailPack = function showDetailPack(value){
		href = "viewDetailPack?packId="+value;
		window.location.href = href;
	}
	
	var prepareCreateNewPack = function prepareCreateNewPack(value){
		href = "NewPack";
		window.location.href = href;
	}
	
	return {
		init: init,
		showDetailPack: showDetailPack,
		getLocalization: getLocalization,
		prepareCreateNewPack: prepareCreateNewPack,
	};
}());