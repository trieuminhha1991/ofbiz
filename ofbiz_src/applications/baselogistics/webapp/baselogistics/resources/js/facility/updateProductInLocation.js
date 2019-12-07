$(function(){
	UpdateProductInLocation.init();
});

var UpdateProductInLocation = (function(){
	var init = function(){
		initEvents();
	};
	
	//EVENT UPDATE PRODUCT IN LOCATION FACILITY 
	var initEvents = function(){
		$("#updateProductTo").click(function () {
			LocationFacilityObj.updateMode = true;
			LocationFacilityObj.arrayLocationHasProductNeedReposition = _.uniq(LocationFacilityObj.arrayLocationHasProductNeedReposition);
			arrayLocationId = LocationFacilityObj.arrayLocationHasProductNeedReposition;
			$("#moveProductTo").click();
		});
		
		$("#moveProductTo").click(function () {
			if (arrayLocationId.length == 0) {
				bootbox.dialog(uiLabelMap.ChooseLocation, [{
		            "label" : '${uiLabelMap.OK}',
		            "class" : "btn btn-primary standard-bootbox-bt",
		            "icon" : "fa fa-check",
		            }]
		        );
				return;
			}
			arrayLocationIdFrom = arrayLocationId;
			arrayLocationIdFrom = _.uniq(arrayLocationIdFrom);
			for ( var x in arrayLocationIdFrom) {
				if (!mapHasInventoryInLocation[arrayLocationIdFrom[x]]) {
					arrayLocationIdFrom.splice(x, 1);
				}
			}
			if (arrayLocationIdFrom.length == 0) {
				bootbox.dialog(uiLabelMap.LocationNotHasProduct, [{
		            "label" : '${uiLabelMap.OK}',
		            "class" : "btn btn-primary standard-bootbox-bt",
		            "icon" : "fa fa-check",
		            }]
		        );
				return;
			}
			listlocationFacilityTemp = JSON.stringify(LocationFacilityObj.listlocationFacility);
			var listlocationFacilityFrom = [];
			for ( var x in LocationFacilityObj.listlocationFacility) {
				var locationId = LocationFacilityObj.listlocationFacility[x].locationId;
				for ( var y in arrayLocationIdFrom) {
					var locationIdFr = arrayLocationIdFrom[y];
					if (locationId == locationIdFr) {
						listlocationFacilityFrom.push(LocationFacilityObj.listlocationFacility[x]);
					}
				}
			}
			LocationFacilityObj.moveMode = true;
			if (!LocationFacilityObj.updateMode) {
				LocationFacilityObj.LocationFacilityObj.listlocationFacility = _.difference(LocationFacilityObj.listlocationFacility, listlocationFacilityFrom);
				clearLocationWasLootAllChild();
			}
			reset();
			LocationFacilityObj.renderTreeGridLocationFacility();
			$("#divUpdateProductTo").css("display", "none");
			$("#taskBarHiden").css("display", "none");
			$("#taskBarMove").css("display", "block");
			bootbox.dialog(uiLabelMap.ChooseLocationToMove, [{
	            "label" : '${uiLabelMap.OK}',
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
			$("#moveProduct").attr('disabled', true);
			$("#btnReset").attr('disabled', true);
		});
	}
	
	return {
		init: init,
	};
}());