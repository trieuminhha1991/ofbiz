<script type="text/javascript" id="totalCommunication">
$(function() {
	var textView = OLBIUS.textView({
		id :"totalCommunication",
		url: "getTotalCommunication",
		icon: "fa fa-phone-square",
		data: {period: "TODAY"},
		renderTitle: function(data) {
			return "${StringUtil.wrapString(uiLabelMap.BCRMTotalCommunication)}"
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