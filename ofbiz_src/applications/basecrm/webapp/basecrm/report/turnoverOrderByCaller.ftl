<script type="text/javascript" id="turnoverOrderByCaller">
$(function() {
	var textView = OLBIUS.textView({
		id :"turnoverOrderByCaller",
		url: "getTurnoverOrderByCaller",
		icon: "fa fa-bar-chart",
		data: {period: "TODAY"},
		renderTitle: function(data) {
			return "${StringUtil.wrapString(uiLabelMap.BCRMTurnoverSalesByCaller)}"
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