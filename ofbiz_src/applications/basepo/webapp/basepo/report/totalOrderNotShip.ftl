<script type="text/javascript" id="totalOrderNotShip">
	$(function() {
		var textView = OLBIUS.textView({
			id :"totalOrderNotShip",
			url: "getNotShipOrder",
			icon: "fa fa-exclamation-triangle",
			renderTitle: function(data) {
				return "${StringUtil.wrapString(uiLabelMap.OrderNotYetShip)}"
			},
			renderValue: function(data) {
				var orderCount = data.orderCount;
				if(orderCount){
					return formatnumber(orderCount);
				} else {
					return "0";
				}
			}
		}).init();
		
		textView.click(function() {
			//Handle onclick
		});
	});
</script>