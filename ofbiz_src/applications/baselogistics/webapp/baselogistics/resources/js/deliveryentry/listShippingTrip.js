$(function(){
	ShippingTripObj.init();
});
var ShippingTripObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		var fromDate1 = null;
    	var fromDate2 = null;
    	
    	$("#jqxNotificationSuccess").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
	    });
	};
	
	var initInputs = function initInputs() {
		$("#ShippingTripMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		


	};
	
	var initElementComplex = function initElementComplex() {
		
	}
	var cellClass = function (row, columnfield, value) {
 		var data = $('#jqxGridListShippingTrip').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("TRIP_CANCELLED" == data.statusId) {
 				return "background-cancel";
 			} else if ("TRIP_CREATED" == data.statusId) {
 				return "background-important-nd";
 			} else if ("TRIP_EXPORTED" == data.statusId) {
 				return "background-prepare";
 			}
 		}
    }
	var initEvents = function initEvents() {
		$("#ShippingTripMenu").on('itemclick', function (event) {
            var data = $('#jqxgrid').jqxGrid('getRowData', $("#jqxgrid").jqxGrid('selectedrowindexes'));
            var tmpStr = $.trim($(args).text());
            if(tmpStr == uiLabelMap.ViewDetailInNewPage){

                window.open("shippingTripDetail?shippingTripId=" + data.shippingTripId, '_blank');

            } else if(tmpStr == uiLabelMap.BSViewDetail){

                window.location.href = "shippingTripDetail?shippingTripId=" + data.shippingTripId;

            } else if (tmpStr == uiLabelMap.BSRefresh){
                $('#jqxgrid').jqxGrid('updatebounddata');
            }
	    });
	}
	
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
	var createShippingTrip = function(){
		window.open('prepareCreateShippingTripByOrder','_blank');
	};
	
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
	

	var getDetailShipment = function(shipmentId){
	};
	return {
		init: init,
		getLocalization: getLocalization,
		createShippingTrip: createShippingTrip,
		formatFullDate: formatFullDate,
		cellClass : cellClass,
	};
}());