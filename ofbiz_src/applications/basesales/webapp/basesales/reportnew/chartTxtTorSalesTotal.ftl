<div id="olbiusChartTxtTorSalesTotal" style="height:100%"></div>
<script type="text/javascript">
	$(function() {
		var textViewTorSalesTotal = OLBIUS.textView({
			id :'olbiusChartTxtTorSalesTotal',
			url: 'olapChartTxtTorSalesTotal',
			icon: 'fa fa-bar-chart',
			data: {
				service: "salesOrderNew",
				olapType: "GRID",
				<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
			},
			renderTitle: function(data) {
				return '${StringUtil.wrapString(uiLabelMap.BSTurnoverThisMonth)}'
			},
			renderValue: function(data) {
				var dataMap = data.data;
		    	if(typeof dataMap == "undefined" || dataMap.length < 0 || dataMap[0] == null){
		    		return "0 VND";
		    	} else {
		    		var firstRow = dataMap[0];
		    		if (firstRow) {
		    			return formatnumber(firstRow.total_amount) + " VND";
		    		} else {
		    			return "0 VND";
		    		}
		    	}
			}
			//renderDescription: function(data) {}
		});
		textViewTorSalesTotal.init();
	});
</script>