<script type="text/javascript" id="totalCommunicationBySchedule">
$(function() {
	var textView = OLBIUS.textView({
		id :"totalCommunicationBySchedule",
		url: "getTotalCommunicationBySchedule",
		icon: "fa fa-calendar",
		data: { period: "TODAY" },
		renderTitle: function(data) {
			return "${StringUtil.wrapString(uiLabelMap.BCRMTotalCommunicationBySchedule)}"
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