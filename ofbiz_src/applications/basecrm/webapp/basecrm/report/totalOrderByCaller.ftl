<script type="text/javascript" id="totalOrderByCaller">
$(function() {
	var textView = OLBIUS.textView({
		id :"totalOrderByCaller",
		url: "getTotalOrderByCaller",
		icon: "fa fa-cart-plus",
		data: {period: "TODAY"},
		renderTitle: function(data) {
			return "${StringUtil.wrapString(uiLabelMap.BCRMTotalSalesOrderByCaller)}"
		},
		renderValue: function(data) {
	    	if(data){
	    		return data.value.toLocaleString(locale);
	    	} else {
	    		return "0";
	    	}
		}
	}).init();
});
</script>