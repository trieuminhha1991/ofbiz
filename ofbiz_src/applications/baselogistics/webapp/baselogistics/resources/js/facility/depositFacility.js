$(function () {
	DepositFacilityObj.init();
});
var DepositFacilityObj = (function () {

	var facilitySelected = null;
	var btnClick = false;
	var init = function(){
		initInputs();
		initEvents();
		
	};
	var initInputs = function(){
		$("#menuForDepositFacility").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	
	
	var initEvents = function(){
		
		$("#menuForDepositFacility").on('itemclick', function (event) {
			if (facilitySelected){
				var data = facilitySelected;
				var tmpStr = $.trim($(args).text());
				if(tmpStr == uiLabelMap.Location){
					window.location.href = "getDepositLocations?facilityId=" + data.facilityId;
				}else if(tmpStr == uiLabelMap.Inventory){
					window.location.href = "getDepositInventory?facilityId=" + data.facilityId;
				} else if(tmpStr == uiLabelMap.Edit){
					getFacility(data.facilityId,false);
				} else if(tmpStr == uiLabelMap.ViewDetailInNewPage.trim()){
					window.open("detailDepositFacility?facilityId=" + data.facilityId, '_blank');
				} else if(tmpStr == uiLabelMap.BSViewDetail){
					window.location.href = "detailDepositFacility?facilityId=" + data.facilityId;
				} else if (tmpStr == uiLabelMap.CommonDelete){	
					deleteFacility(data.facilityId);
				} else if(tmpStr == uiLabelMap.BSRefresh){
					$('#jqGridDepositFacility').jqxGrid('updatebounddata');
				}
			}
		});
		
		$('#jqGridDepositFacility').on('rowclick', function (event) {
		    var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $('#jqGridDepositFacility').jqxGrid('getrowdata', boundIndex);
		    facilitySelected = data;
		}); 

	};
	
	
	
	return {
		init: init,
	};
}());