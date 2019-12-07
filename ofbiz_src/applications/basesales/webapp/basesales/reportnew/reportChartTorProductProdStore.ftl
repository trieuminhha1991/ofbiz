<#-- ======================================== AREA CHART =========================================== -->
<div id="olbiusAreaChartTorProductProdStore" class="margin-top30 container-chart-inner-page"></div>
<script type="text/javascript">
	$(function(){
		var config = {
			service: "salesOrderNew",
            id: "olbiusAreaChartTorProductProdStore",
            olap: "olapTorProductProdStore",
            
            chart: {
                type: 'column'
            },
            title: {
            	text: '${StringUtil.wrapString(uiLabelMap.BSPPSColumn)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -30,
                    style: {
                        fontSize: '11px',
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
                enabled: true
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
            },
            plotOptions: {
	            series: {
	                maxPointWidth: 30
	            }
	        },
	        height: 0.35,
	        chartRender : OlbiusUtil.getChartRender('defaultColumnFunc'),
            
            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'COLUMNCHART',
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, configDataSync);
            }
        };
	
		if (typeof olbiusAreaChartObj == "undefined") olbiusAreaChartObj = OlbiusUtil.chart(config);
		
        <#--
        olbiusAreaChartObj = OLBIUS.oLapChart('olbiusAreaChartTorProductProdStore', config, null, 'olapTorPPSAreaChart', true, true, function(data, chart, datetype, removeSeries, flagFunc, olap){
        	var tmp = {
                labels: {
                    enabled: false
                },
                categories: data.xAxis
            };

            chart.xAxis[0].update(tmp, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }
            var color = 8;
            for (var i in data.yAxis) {
                chart.addSeries({
                    name: i,
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++],
                }, false);
            }

            chart.redraw();

            if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
                flagFunc();
            }
		});

        olbiusAreaChartObj.funcUpdate(function (oLap) {
            oLap.update($.extend(configDataSync, {
            	productStore: 'all',
				category: 'all',
				orderStatus: 'ORDER_COMPLETED',
				customTime: 'ww'
            }));
        });

        olbiusAreaChartObj.init(function () {
            olbiusAreaChartObj.runAjax();
        });
        -->
	});
</script>

<#-- ======================================== PIE CHART =========================================== -->
<div class="row-fluid" class="margin-top30">
	<div id="olbiusColChartProdStore" class="span6 container-chart-inner-page"></div>
	<div id="olbiusPieChartProdStore" class="span6 container-chart-inner-page"></div>
</div>
<script type="text/javascript">
	$(function(){
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
		
		<#--
		var config = {
			service: "salesOrderNew",
            id: "olbiusColChartProdStore",
            olap: "olapChartPieSynTorProduct",
            
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnOverChartByProduct)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y} VND</b>'
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
            
            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'PIECHART',
                	xAxisName: 'product_code'
                }, configDataSync);
            },
            height: 0.7,
            chartRender : OlbiusUtil.getChartRender('defaultPieFunc')
        };
	
		olbiusColChartProdStoreObj = OlbiusUtil.chart(config);
		-->
		
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
		
		<#--
        var config = {};
        gPieValue = OLBIUS.oLapChart('PPSPieChartTotal', config, null, 'evaluateTurnoverPPSPieChart2', true, true, OLBIUS.defaultPieFunc);
        gPieValue.funcUpdate(function(oLap) {
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'productStore': gStore,
                'category': gCategory,
                'orderStatus': gStatus,
                'customTime': gCustomTime,
            });
        });
        gPieValue.init(function () {
            gPieValue.runAjax();
        });
        -->

		<#--
		var configPieChartProdStore = {
			service: "salesOrderNew",
            id: "olbiusPieChartProdStore",
            olap: "olapTorProductProdStore",
            
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPSTurnOverChartBySalesChannel)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y} VND</b>'
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
            
            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'PIECHART',
                	xAxisName: 'store_name'
                }, configDataSync);
            },
            height: 0.7,
            chartRender : OlbiusUtil.getChartRender('defaultPieFunc')
        };
	
		olbiusPieChartProdStoreObj = OlbiusUtil.chart(configPieChartProdStore);
		-->
	});
</script>