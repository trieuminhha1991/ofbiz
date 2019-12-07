<script type="text/javascript" id="mostCategoryPurchaseOrderReportOlap">
	$(function() {
		var textView = OLBIUS.textView({
			id :"mostCategoryPurchaseOrderReportOlap",
			url: "getMostCategoryPurchaseOrderReportOlap",
			icon: "fa fa-arrow-up",
			renderTitle: function(data) {
				return "${StringUtil.wrapString(uiLabelMap.POCategoryMostPurchaseOrder)}"
			},
			renderValue: function(data) {
				var listDatafield = data.listValue;
				var categoryName = "";
				var quantity = null;
				var productName = "${StringUtil.wrapString(uiLabelMap.DAProduct)}";
				for (var i in listDatafield) {
					quantity = listDatafield[0];
					categoryName = listDatafield[1];
				}
				if (quantity != null) {
					quantity = quantity * (1);
					return "<b>" + categoryName + "</b><br/> (" + formatnumber(quantity) + " " + productName+")";
				} else {
					return "0<br/> ("+productName+")";
				}
			}
		}).init();
	});
</script>