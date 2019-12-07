<#-- ======================================== AREA CHART =========================================== -->
<div class="margin-top30"></div>
<div class="row-fluid" class="margin-top30">
	<div id="olbiusAreaChartOtherTorProduct" class="span12 container-chart-inner-page"></div>
</div>
<script type="text/javascript">
if (typeof OlbChartOtherTorProduct == "undefined") {
	var OlbChartOtherTorProduct = (function(){
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
		    	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
			}
		};
		var initColChart = function(){
			var config = {
				service: "salesOrderNew",
	            id: "olbiusAreaChartOtherTorProduct",
	            olap: "olapOtherTorProduct",
	            
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
	                pointFormat: '<span style="color:{series.color}">${StringUtil.wrapString(uiLabelMap.BSTurnOver)}</span>: <b>{point.y} VND</b><br/>',
	            },
	            plotOptions: {
		            series: {
		                maxPointWidth: 30
		            }
		        },
	            
	            apply: function (grid, popup) {
	                return $.extend({
	                	olapType: 'COLUMNCHART',
	                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
	                }, configDataSync);
	            },
	            chartRender : OlbiusUtil.getChartRender('defaultColumnFunc')
	        };
		
			if (typeof olbiusAreaChartObj == "undefined") olbiusAreaChartObj = OlbiusUtil.chart(config);
		};
		return {
			init: init,
			open: open
		}
	}());
}
</script>

<#-- ======================================== PIE CHART =========================================== -->
<#--
<div class="row-fluid" class="margin-top30">
	<div id="olbiusPieChartProduct" class="span6"></div>
	<div id="olbiusPieChartCategory" class="span6"></div>
</div>
<script type="text/javascript">
	$(function(){
		var config = {
			service: "salesOrderNew",
            id: "olbiusPieChartProduct",
            olap: "olapOtherTorProduct",
            
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
	
		olbiusPieChartProductObj = OlbiusUtil.chart(config);
		
		======================================== PIE CHART 2 ===========================================
		var configPieChartCategory = {
			service: "salesOrderNew",
            id: "olbiusPieChartCategory",
            olap: "olapOtherTorProduct",
            
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnOverChartByCategory)}'
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
                	xAxisName: 'category_name'
                }, configDataSync);
            },
            height: 0.7,
            chartRender : OlbiusUtil.getChartRender('defaultPieFunc')
        };
	
		olbiusPieChartCategoryObj = OlbiusUtil.chart(configPieChartCategory);
	});
</script>
-->