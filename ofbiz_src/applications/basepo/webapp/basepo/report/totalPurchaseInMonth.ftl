<script type="text/javascript" id="totalAmountBought">
$(function() {
	var textView = OLBIUS.textView({
		id :"totalAmountBought",
		url: "getTotalAmountBought",
		icon: "fa fa-cart-plus",
		data: {period: "InMonth"},
		renderTitle: function(data) {
			return "${StringUtil.wrapString(uiLabelMap.POOrderTotalAmountBoughtInMonth)}"
		},
		renderValue: function(data) {
			var orderCount = data.value;
			if(orderCount){
				return formatcurrency(orderCount);
			} else {
				return "0 VND";
			}
		}
	}).init();
});
</script>