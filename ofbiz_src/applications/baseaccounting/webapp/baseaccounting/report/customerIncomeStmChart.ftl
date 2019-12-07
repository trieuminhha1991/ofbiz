<div class="row-fluid margin-top30"></div>
<div class="row-fluid">
	<div class="span12">
		<div class="span2"></div>
		<div class="span8">
			<div id="custIncomePieChart"></div>
		</div>
		<div class="span2"></div>
	</div>
</div>

<script type="text/javascript">
    $(function () {
        var config = {
    		service: 'acctgTransTotal',
    		id: "custIncomePieChart",
            olap: "evaluateCustIncomePieChart",
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerIncomePieChart)}'
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
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    },
                    showInLegend: true
                }
            },
            height: 0.65,
            chartRender: OlbiusUtil.getChartRender('defaultPieFunc'),
            
            apply: function (grid, popup) {
                return $.extend({
                	'olapType': 'PIECHART',
                }, configDataSync);
            },
        };
        CIPC = OlbiusUtil.chart(config);
    });
</script>