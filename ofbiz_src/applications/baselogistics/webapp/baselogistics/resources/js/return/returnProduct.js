$(function(){
	ReturnObj.init();
});
var ReturnObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		$("#ReturnMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
		$("#ReturnMenu").on('itemclick', function (event) {
			var data = $('#jqxgridProductReturn').jqxGrid('getRowData', $("#jqxgridProductReturn").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.ViewDetailInNewPage){
				var href;
				if ("CUSTOMER_RETURN" == returnHeaderTypeId){
					href = "viewReturnOrder?returnId="+data.returnId;
				}
				if ("VENDOR_RETURN" == returnHeaderTypeId){
					href = "getDetailVendorReturn?returnId="+data.returnId;
				}
				window.open(href, '_blank');
			} else if(tmpStr == uiLabelMap.BSViewDetail){
				var href;
				if ("CUSTOMER_RETURN" == returnHeaderTypeId){
					href = "viewReturnOrder?returnId="+data.returnId;
				}
				if ("VENDOR_RETURN" == returnHeaderTypeId){
					href = "getDetailVendorReturn?returnId="+data.returnId;
				}
				window.location.href = href;
			} else if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridProductReturn').jqxGrid('updatebounddata');
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
	
	var showDetailReturn = function showDetailReturn(value){
		var href = "";
		if ("CUSTOMER_RETURN" == returnHeaderTypeId){
			href = "viewReturnOrder?returnId="+value;
		}
		if ("VENDOR_RETURN" == returnHeaderTypeId){
			href = "getDetailVendorReturn?returnId="+value;
		}
		window.location.href = href;
	}
	
	var addZero = function(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	
	var formatFullDate = function(value) {
		if (value) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	
	return {
		init: init,
		showDetailReturn: showDetailReturn,
		getLocalization: getLocalization,
		formatFullDate: formatFullDate,
	};
}());