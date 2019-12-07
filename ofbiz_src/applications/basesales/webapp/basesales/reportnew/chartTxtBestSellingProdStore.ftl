<div id="olbiusChartTxtBestSellingProdStore" style="height:100%"></div>
<script type="text/javascript">
	$(function() {
		var test = OLBIUS.textView({
			id :'olbiusChartTxtBestSellingProdStore',
			url: 'olapChartTxtBestSellingProdStore',
			icon: 'fa fa-line-chart',
			data: {
				service: "salesOrderNew",
				olapType: "GRID",
				<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
			},
			renderTitle: function(data) {
				var dataMap = data.data;
		    	if(typeof dataMap == "undefined" || dataMap.length < 0 || dataMap[0] == null){
		    		return "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}";
		    	} else {
		    		var firstRow = dataMap[0];
		    		if (firstRow) {
		    			return firstRow.store_name;
		    		} else {
		    			return "${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}";
		    		}
		    	}
				return "${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}";
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
			},
			renderDescription: function(data) {
				return '${StringUtil.wrapString(uiLabelMap.BSTurnoverByTopStoreThisMonth)}'
			}
		});
		test.init();
	});
</script>