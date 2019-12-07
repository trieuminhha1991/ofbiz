
<#-- ======================================== PIE CHART =========================================== -->
<div class="margin-top30"></div>
<div class="row-fluid" class="margin-top30">
	<div id="olbiusColChartProdStore" class="span6 container-chart-inner-page"></div>
	<div id="olbiusPieChartProdStore" class="span6 container-chart-inner-page"></div>
</div>
<script type="text/javascript">
if (typeof OlbChartTorProductStore == "undefined") {
	var OlbChartTorProductStore = (function(){
		var initialized = false;
		var init = function(){
			initColChart();
			initPieChart();
			
			initialized = true;
		};
		var open = function(){
			$("#popupViewCharts").jqxWindow("open");
			
			if (!initialized) {
				init();
			} else {
		    	if (olbiusColChartProdStoreObj) olbiusColChartProdStoreObj.update();
		    	if (olbiusPieChartProdStoreObj) olbiusPieChartProdStoreObj.update();
			}
		};
		var initColChart = function(){
			<#-- ======================================== PIE CHART 1 =========================================== -->
			var config = {
				service: "salesOrderNew",
	            id: "olbiusColChartProdStore",
	            olap: "olapChartPieSynTorSales",
	            
	            chart: {
	                type: 'column'
	            },
	            title: {
	                text: '${StringUtil.wrapString(uiLabelMap.BSPSTurnOverChartBySalesChannel)}',
	                x: -20 //center
	            },
	            xAxis: {
	                type: 'category',
	                labels: {
	                    rotation: 0, //-30,
	                    style: {
	                        fontSize: '13px',
	                        fontFamily: 'Verdana, sans-serif'
	                    }
	                },
	                title : {
	                    text: null
	                }
	            },
	            yAxis: {
	                plotLines: [{
	                    value: 0,
	                    width: 1,
	                    color: '#000000'
	                }],
	                title : {
	                    text: null
	                },
	                min: 0
	            },
	            legend: {
	                enabled: false
	            },
	            tooltip: {
	                <#--pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',-->
	                formatter: function () {
	                    return '<i>' + this.x + '</i>' + '<br/>' + '<b>' + this.y.toLocaleString(locale) + '</b>';
	                }
	            },
	            plotOptions: {
		            series: {
		                maxPointWidth: 30,
		                borderWidth: 0,
	                    dataLabels: {
	                        enabled: true
	                    }
		            }
		        },
		        height: 0.6,
	            chartRender: OlbiusUtil.getChartRender('defaultColumnFunc'),
	            
	            apply: function (grid, popup) {
	                return $.extend({
	                	'olapType': 'COLUMNCHART',
	                	'filterTypeId': 'PRODUCT_STORE',
	                	'filterTypeId2': 'SALES_VALUE',
	                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
	                }, configDataSync);
	            }
	        };
		
			if (typeof olbiusColChartProdStoreObj == "undefined") olbiusColChartProdStoreObj = OlbiusUtil.chart(config);
		};
		var initPieChart = function(){
			<#-- ======================================== PIE CHART 2 =========================================== -->
	        var configPieChartProdStore = {
				service: "salesOrderNew",
	            id: "olbiusPieChartProdStore",
	            olap: "olapChartPieSynTorSales",
	            
	            chart: {
	                plotBackgroundColor: null,
	                plotBorderWidth: null,
	                plotShadow: false
	            },
	            title: {
	                text: '${StringUtil.wrapString(uiLabelMap.BSPSTurnOverChartBySalesChannel)}'
	            },
	            tooltip: {
	                pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>'
	            },
	            series: [{
	                type: 'pie'
	            }],
	            plotOptions: {
	                pie: {
	                    allowPointSelect: true,
	                    cursor: 'pointer',
	                    dataLabels: {
	                        enabled: false,
	                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                        style: {
	                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                        }
	                    },
	                    showInLegend: true
	                }
	            },
	            height: 0.6,
	            chartRender: OlbiusUtil.getChartRender('defaultPieFunc'),
	            
	            apply: function (grid, popup) {
	                return $.extend({
	                	'olapType': 'PIECHART',
	                	'filterTypeId': 'PRODUCT_STORE',
	                	'filterTypeId2': 'SALES_VALUE',
	                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
	                }, configDataSync);
	            },
	        };
		
			if (typeof olbiusPieChartProdStoreObj == "undefined") olbiusPieChartProdStoreObj = OlbiusUtil.chart(configPieChartProdStore);
		};
		return {
			init: init,
			open: open
		}
	}());
}
	$(function(){
		
	});
</script>