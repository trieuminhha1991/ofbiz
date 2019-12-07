$(document).ready(function() {
	ReceiveByUPCA.init();
});
if (typeof (ReceiveByUPCA) == "undefined") {
	var ReceiveByUPCA = (function() {
		var mainGrid, txtUPCACode;
		var initJqxElements = function() {
			
		};
		var handleEvents = function() {
			$("#btnScanUPCA").click(function() {
				focus();
			});
			txtUPCACode.dblclick(function() {
				$(this).val(UPCA.make());
				txtUPCACode.trigger("change");
			});
			txtUPCACode.on("change", function() {
				DataAccess.execute({
				url: "getProductDetailByUPCA",
				data: { idUPCA: $(this).val() }
				}, ReceiveByUPCA.addItem);
				$(this).val("");
			});
		};
		var addItem = function(res) {
			if (res) {
				var data = res["productDetail"];
				if (data) {
					var actualDeliveredQuantity = 1;
					var rowid;
					var rows = mainGrid.jqxGrid("getrows");
					for ( var x in rows) {
						if (rows[x].productId == data.productId && rows[x].quantityUomId == data.quantityUomId && rows[x].UPCACode == data.idUPCA) {
							actualDeliveredQuantity += rows[x].actualDeliveredQuantity;
							rowid = rows[x].uid;
						}
					}
					if (typeof (rowid) != "undefined") {
						mainGrid.jqxGrid("setcellvaluebyid", rowid, "actualDeliveredQuantity", actualDeliveredQuantity);
					} else {
						var datarow = PODlvObj.generaterow(data.productCode);
						datarow.actualDeliveredQuantity = 1;
						datarow.UPCACode = data.idUPCA;
						mainGrid.jqxGrid("addrow", null, datarow, "first");
					}
					Alert.success();
				} else {
					Alert.error();
				}
			} else {
				Alert.error();
			}
		};
		var focus = function() {
			txtUPCACode.animate({width: "toggle"}).focus();
		};
		return {
			init: function() {
				txtUPCACode = $("#txtUPCACode");
				mainGrid = $("#jqxgrid2");
				initJqxElements();
				handleEvents();
			},
			addItem: addItem
		};
	})();
}
if (typeof (UPCA) == "undefined") {
	var UPCA = (function() {
		var make = function() {
			var UPCAs =
			[
				"220000001504",
				"220001001503",
				"220002002004",
				"xxx",
			];
			return UPCAs[_.random(0, UPCAs.length - 1)];
		};
		return {
			make: make
		};
	})();
}