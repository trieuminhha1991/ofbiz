<div id="olbiusChartTxtQtySalesOrderTotal" style="height:100%"></div>
<script type="text/javascript">
	$(function() {
		var test = OLBIUS.textView({
			id :'olbiusChartTxtQtySalesOrderTotal',
			url: 'olapChartTxtQtySalesOrderTotal',
			icon: 'fa fa-shopping-cart',
			data: {
				service: "salesOrderNew",
				olapType: "GRID",
				<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
			},
			renderTitle: function(data) {
				return "${StringUtil.wrapString(uiLabelMap.BSOrderThisMonth)}";
			},
			renderValue: function(data) {
				var dataMap = data.data;
		    	if(typeof dataMap == "undefined" || dataMap.length < 0 || dataMap[0] == null){
		    		return "0";
		    	} else {
		    		var firstRow = dataMap[0];
		    		if (firstRow) {
		    			return formatnumber(firstRow.total_qty_order);
		    		} else {
		    			return "0";
		    		}
		    	}
			}
		});
		test.init();
	});
</script>