$(function(){
	ListPicklistBin.init();
});
var ListPicklistBin = (function() {
	var init = function() {
		initInputs();
		initEvents();
	};
	var initInputs = function() {
		$("#contextMenuPicklistBin").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initEvents = function() {
		$("#contextMenuPicklistBin").on('itemclick', function (event) {
			var data = $('#jqxgridPicklistBin').jqxGrid('getRowData', $("#jqxgridPicklistBin").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			var picklistBinId = data.picklistBinId; 
			if (tmpStr == uiLabelMap.BLCreateSalesDelivery){
				createDelivery(picklistBinId);
			}
			if (tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridPicklistBin').jqxGrid('updatebounddata');
			}
		});
		
		$("#contextMenuPicklistBin").on("shown", function () {
			var rowIndexSelected = $('#jqxgridPicklistBin').jqxGrid("getSelectedRowindex");
			var statusId = $('#jqxgridPicklistBin').jqxGrid("getcellvalue", rowIndexSelected, "binStatusId");
			if (statusId === "PICKBIN_APPROVED") {
				$("#contextMenuPicklistBin").jqxMenu("disable", "mnuCreateDelivery", false);
			} else {
				$("#contextMenuPicklistBin").jqxMenu("disable", "mnuCreateDelivery", true);
			}
		});
	}
	
	var createDelivery = function(picklistBinId){
		jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
			Loading.show('loadingMacro');
			setTimeout(function(){
				$.ajax({
					type: 'POST',
					url: 'createDeliveryFromPicklistBin',
					async: false,
					data: {
						picklistBinId: picklistBinId,
					},
					success: function(res){
						if(res._ERROR_MESSAGE_ || !res.deliveryId){
							if(res._ERROR_MESSAGE_){
								jOlbUtil.alert.error(uiLabelMap.UpdateError+ ": "+res._ERROR_MESSAGE_);
								return false;
							}
						} else {
							var deliveryId = res.deliveryId;
							window.location.href = 'getListSalesDeliveries?selectedMenuItem=AR';
						}
					},
				});
				Loading.hide('loadingMacro');
			}, 500);
		});
	}
	return {
		init: init,
	}
}());