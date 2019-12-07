$(function(){
	ShipmentObj.init();
});
var ShipmentObj = (function(){
	var init = function(){
//		initInputs();
//		initElementComplex();
//		initEvents();
//		initValidateForm();
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
	var createShipment = function(){
		window.open('prepareCreateShipment','_blank');
	};
	var getDetailShipment = function(shipmentId){
		var shipmentDT;
		$.ajax({
			type: 'POST',
			url: 'getShipmentDetail',
			async: false,
			data: {
				shipmentId: shipmentId,
			},
			success: function(data){
				shipmentDT = data;
			},
		});
		$("#originFacilityId").text(shipmentDT.originFacilityName);
		$("#destinationFacilityId").text(shipmentDT.destFacilityName);
		$("#originContactMechId").text(shipmentDT.originAddress);
		$("#destinationContactMechId").text(shipmentDT.destAddress);
		$("#estimatedShipDate").text(shipmentDT.estimatedShipDate);
		$("#estimatedArrivalDate").text(shipmentDT.estimatedArrivalDate);
		$("#estimatedShipCost").text(shipmentDT.estimatedShipCost.toLocaleString('${localeStr}'));
		$("#currencyUomId").text(shipmentDT.currencyUom);
	};
	return {
		init: init,
		getDetailShipment: getDetailShipment,
		getLocalization: getLocalization,
		createShipment: createShipment,
	};
}());