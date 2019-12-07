<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var dateCurrent = new Date();
	var partyId = "${userLogin.partyId}";
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
	<#assign salesmanList = Static["com.olbius.basesales.party.PartyWorker"].getSalesmanIdsBySup(delegator, userLogin.partyId)!>

	var salesmanList = [
	<#if salesmanList?exists>
	    <#list salesmanList as salesmanL>
	    {
	    	partyId : "${salesmanL}",
	    },
	    </#list>	
    </#if>
	];
	
	var listSMDataSource = [];	
	for(var x in salesmanList){
		var SMSource = {
			text: salesmanList[x].partyId,
			value: salesmanList[x].partyId,
		}
		listSMDataSource.push(SMSource);
	}
</script>

<#--
<script type="text/javascript" id="testEvaluateSalesman">
    $(function () {
    	Highcharts.setOptions({
		    lang: {
		        decimalPoint: ',',
		        thousandsSep: '.'
		    }
		});
		
        var config = {
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.Test)}',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: false
                },
                title: {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }],
                title: {
                    text: null
                },
                min: 0
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
                valueDecimals: 2
            },
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: false
                    },
                    enableMouseTracking: true
                }
            },
        };

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'salesman',
                    label : '${StringUtil.wrapString(uiLabelMap.Test)}',
                    data : listSMDataSource,
                    index: 0
                }]
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'from_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                before: 'thru_date'
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'thru_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
            }
        ];

        var saleOrderOLap = OLBIUS.oLapChart('testEvaluateSalesman', config, null, 'evaluateSalesman', true, true, OLBIUS.defaultColumnFunc);

        saleOrderOLap.funcUpdate(function (oLap) {
            oLap.update({
            });
        });   
        saleOrderOLap.init(function () {
            saleOrderOLap.runAjax();
        });

    });

</script>
-->
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
        oLap.update({
        	'flag': 'sup',
        });
    });

    columnChart.init(function () {
        columnChart.runAjax();
    });
});
</script>