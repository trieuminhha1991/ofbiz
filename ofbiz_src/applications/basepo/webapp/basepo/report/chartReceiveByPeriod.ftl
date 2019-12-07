<div id="chartColReceivedByPeriod"></div>
<script type="text/javascript">
	$(function(){
		var configChartColReceivedByPeriod = {
			service: "facilityInventory",
            id: "chartColReceivedByPeriod",
            olap: "olapChartColReceivedByPeriod",
            
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BPChartReceivedByPeriod)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: 0, //-30,
                    style: {
                        fontSize: '10px',
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
                formatter: function () {
                    return '<b> SKU: ' + this.x + '</b>' + '<br/>' + '<b> $: ' + this.y.toLocaleString(locale) + '</b>';
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
            chartRender: chartRenderTypeColumn({color: 2}),
            
            popup: [
                {
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                    	index: 5,
                    	fromDate: past_date,
                    	thruDate: cur_date,
                    	dateTypeIndex: 2,
                    }
                },
            ],
            
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
                return $.extend({
                	'olapType': 'COLUMNCHART',
                }, dateTimeData);
            },
        };
	
		OlbiusUtil.chart(configChartColReceivedByPeriod);
	});
</script>