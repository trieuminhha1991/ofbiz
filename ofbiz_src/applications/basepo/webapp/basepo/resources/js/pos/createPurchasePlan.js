var mainGrid;
$(document).ready(function() {
	mainGrid = $("#jqxGridProductList");
	CreatePurchasePlan.init();
});
if (typeof (CreatePurchasePlan) == "undefined") {
	var CreatePurchasePlan = (function() {
		var init = function() {
			$("#fuelux-wizard").ace_wizard()
				.on("change", function(e, info) {
					if ((info.step == 1) && (info.direction == "next")) {
						if (GeneralInfo.validator()) {
							if (GridDetail.validator()) {
								GeneralInfoReview.setValue(GeneralInfo.getValue());
								GridDetailReview.setValue(GridDetail.getValue());
							} else {
								jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
								return false;
							}
						} else {
							return false;
						}
					}
				})
				.on("finished", function(e) {
					jOlbUtil.confirm.dialog( uiLabelMap.AreYouSureCreate, function() {
						CreatePurchasePlan.create();
					});
				});
			
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true });
		};
		var create = function() {
			var generalInfo = GeneralInfo.getValue();
			var param = {};
			param.supplierPartyId = generalInfo.supplierId;
			param.hasMutilPO = generalInfo.multiPO;
			param.mainFacilityId = generalInfo.facilityId;
			param.shipAfterDate = generalInfo.shipAfterDate.getTime();
			param.shipBeforeDate = generalInfo.shipBeforeDate.getTime();
			
			var gridDetails = GridDetail.getValue();
			var productList = [];
			for ( var index in gridDetails) {
				var product = gridDetails[index];
				var productSelected = {};
				productSelected.productId = product.productId;
				var lastPrice = product.lastPrice;
				var productRowDetail = [];
				var dataDetailrows = product.rowDetails;
				for (j = 0; j < dataDetailrows.length; j++) {
					var rowDetail = {};
					var dataDetail = dataDetailrows[j];
					if (dataDetail.quantity > 0) {
						rowDetail.productId = dataDetail.productId;
						rowDetail.facilityId = dataDetail.facilityId;
						rowDetail.quantity = dataDetail.quantity;
						rowDetail.lastPrice = lastPrice;
						productRowDetail.push(rowDetail);
					}
				}
				productSelected.rowDetail = productRowDetail;
				productList.push(productSelected);
			}
			
			param.productList = JSON.stringify(productList);
			
			DataAccess.execute({
				url: "processCreatePurchasePlan",
				data: param
			}, CreatePurchasePlan.notify);
		};
		var notify = function(res) {
			$("#jqxNotification").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotification").jqxNotification({ template: "error"});
				$("#notificationContent").text(uiLabelMap.wgadderror);
				$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#notificationContent").text(uiLabelMap.wgaddsuccess);
				$("#jqxNotification").jqxNotification("open");
				
				setTimeout(function() {
					window.location.href = createPurchasePlanGlobalObject.urlNavigation;
				}, 300);
			}
		};
		return {
			init: function() {
				init();
				GeneralInfo.init();
				GridDetail.init();
				GridDetailReview.init();
			},
			notify: notify,
			create: create
		}
	})();
}