<script>
	
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign listProductStore = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, company), null, null, null, false) />
	
	var dataProductStoreArr = [
	<#if listProductStore?has_content>
		<#list listProductStore as item>
		{"value": "${item.facilityId}", "text": "${StringUtil.wrapString(item.facilityName?default(""))}"},
		</#list>
	</#if>
	];
	var dataProductStoreArr = $.merge(dataProductStoreArr, [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'ALL'}]);
	
	<#assign listPrimaryCategory = Static["com.olbius.product.util.ProductUtil"].getAllProductCatalogCategory(delegator)!/>
   	var dataPrimaryCategoryArr = [
	<#if listPrimaryCategory?has_content>
		<#list listPrimaryCategory as item>
		{"value": "${item.productCategoryId}", "text": "${item.categoryName?default("")}"},
		</#list>
	</#if>
	];
	
	var chartRenderTypeColumn = function(config){
		var $color = typeof(config.color) != 'undefined' ? config.color : 0;
		return function(data, obj) {
		        var tmp = {
		            labels: {
		                enabled: true
		            },
		            categories: data.xAxis
		        };
		
		        obj._chart.xAxis[0].update(tmp, false);
		
		        while (obj._chart.series.length > 0) {
		            obj._chart.series[0].remove(false);
		        }
		
		        var color = $color;
		        for (var i in data.yAxis) {
		            obj._chart.addSeries({
		                name: i,
		                data: data.yAxis[i],
		                color: Highcharts.getOptions().colors[color++]
		            }, false);
		        }
		
		        obj._chart.redraw();
		
		        return !!(data.xAxis && data.xAxis.length == 0);
		    };
	};
	var dataCctpFilterTopArr = ["5", "10", "15", "20"];
	var dataTopQtyFilterSortArr = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityHighest)}', 'value': 'DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityLowest)}', 'value': 'ASC'}
	];
</script>
<script type="text/javascript" id="purchaseOrderPieChart">
$(function () {
	var config = {
			chart: {
				plotBackgroundColor: null,
				plotBorderWidth: null,
				plotShadow: false
			},
			title: {
				text: "${StringUtil.wrapString(uiLabelMap.PORatePurchasrOrder)}"
			},
			tooltip: {
				pointFormat: "<b>{point.percentage:.1f}%: {point.y}</b>"
			},
			series: [{
				type: "pie"
			}],
			plotOptions: {
				pie: {
					allowPointSelect: true,
					cursor: "pointer",
					dataLabels: {
						enabled: true,
						format: "<b>{point.name}</b>: {point.percentage:.1f} %",
						style: {
							color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || "black"
						}
					}
				}
			}
	};

	var configPopup = [
	{
		action : "addDateTimeInput",
		params : [{
			id : "from_date",
			label : "${StringUtil.wrapString(uiLabelMap.olap_fromDate)}",
			value: OLBIUS.dateToString(LocalData.date.firstDayOfMonth)
		}],
		before: "thru_date"
	},
	{
		action : "addDateTimeInput",
		params : [{
			id : "thru_date",
			label : "${StringUtil.wrapString(uiLabelMap.olap_thruDate)}",
			value: OLBIUS.dateToString(LocalData.date.currentDate)
		}],
		after: "from_date"
	}];

	var purchaseOrderPieChart = OLBIUS.oLapChart("purchaseOrderPieChart", config, configPopup, "purchaseOrderReportPieChart", true, true, OLBIUS.defaultPieFunc);

	purchaseOrderPieChart.funcUpdate(function(oLap) {
		oLap.update({
			fromDate: oLap.val("from_date"),
			thruDate: oLap.val("thru_date"),
			filterTypeId: "FILTER_CATALOG"
		});
	});

	purchaseOrderPieChart.init(function () {
		purchaseOrderPieChart.runAjax();
	});
});
</script>