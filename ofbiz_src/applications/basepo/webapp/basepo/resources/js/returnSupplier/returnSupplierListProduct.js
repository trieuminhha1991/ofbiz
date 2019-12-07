$(function() {
	OlbOrderProduct.init();
});

var OlbOrderProduct = (function() {
	var init = function() {
		initGrid();
		initEvents();
	};

	var initGrid = function() {
		var orderId = $("#supplierId").val();
		$("#jqxgridProduct").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQListProductByOrder&orderId="
				+ orderId;
		$("#jqxgridProduct").jqxGrid("updatebounddata");
	};

	var initEvents = function() {
		$("#orderHeaderBtn")
				.on(
						"close",
						function() {
							var orderId = $("#orderHeaderId").val();
							$("#jqxgridProduct").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQListProductByOrder&orderId="
									+ orderId;
							$("#jqxgridProduct").jqxGrid("updatebounddata");
							$("#jqxgridProduct").on(
									"bindingcomplete",
									function(event) {
										var data = $("#jqxgridProduct")
												.jqxGrid("getrows");
										for (var i = 0; i < data.length; i++) {
											$("#jqxgridProduct").jqxGrid(
													"unselectrow", i);
										}
									});
						});
		$("#jqxgridProduct").on("rowselect", function(event) {
			var args = event.args;
			var rowBoundIndex = args.rowindex;
			var rowData = args.row;
			if (rowData.returnableQuantity == 0) {
				$("#jqxgridProduct").jqxGrid("unselectrow", rowBoundIndex);
				bootbox.dialog("" + uiLabelMap.BPOProductCanNotReturn, [ {
					"label" : uiLabelMap.wgok,
					"icon" : "fa-check",
					"class" : "btn btn-primary form-action-button pull-right",
					"callback" : function() {

					}
				} ]);
			}
		});
	};

	return {
		init : init,
	}
}());