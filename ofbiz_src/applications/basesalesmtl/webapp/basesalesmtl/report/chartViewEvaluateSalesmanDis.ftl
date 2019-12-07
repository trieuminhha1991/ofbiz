<script type="text/javascript" id="testEvaluateSalesman">
$(function(){
	Highcharts.setOptions({
	    lang: {
	        decimalPoint: ',',
	        thousandsSep: '.'
	    }
	});
		
	var config = {
		chart: {
			type: 'column'
		},
		title: {
			text: '${StringUtil.wrapString(uiLabelMap.BSEffectiveSales)}',
			x: -20 //center
		},
		xAxis: {
			type: 'category',
			labels: {
				rotation: 0,
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
			enabled: true
		},
		tooltip: {
			pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
			valueDecimals: 2
		},
		plotOptions: {
			series: {
				borderWidth: 0,
				dataLabels: {
					enabled: true
				}
			},
			column: {
                showInLegend: false
            }
		}
	};
    
    var columnChart = OLBIUS.oLapChart('testEvaluateSalesman', config, null, 'evaluateSalesman', true, true, function(data, chart, datetype, removeSeries, flagFunc, olap){
        	var tmp = {
                labels: {
                    enabled: true
                },
                categories: data.xAxis
            };

            chart.xAxis[0].update(tmp, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }
            var color = 2;
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

    columnChart.funcUpdate(function (oLap) {
    	var id = 'dis';
        oLap.update({
        	'flag': id,
        });
    });

    columnChart.init(function () {
        columnChart.runAjax();
    });
});
</script>