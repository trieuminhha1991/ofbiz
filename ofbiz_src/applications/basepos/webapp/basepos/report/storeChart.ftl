<script type="text/javascript" id="storeChart">
    $(function () {
    	var config = {
    		service: 'salesOrderPOS',
                chart: {
                    type: 'column'
                },
                title: {
                    text: '${StringUtil.wrapString(uiLabelMap.BSPSChartSalesChannelPOS)}',
                    x: -20 //center
                },
                xAxis: {
                    type: 'category',
                    labels: {
                        rotation: -45,
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
                        color: '#808080'
                    }],
                    title : {
                        text: null
                    },
                },
                legend: {
                    enabled: false
                },
                tooltip: {
                    pointFormat: '{point.y}'
                },
                plotOptions: {
                    series: {
                        pointPadding: 0
                    }
                }
            };
    	
    	var configPopup = [
			{
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'from_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
	                    value: OLBIUS.dateToString(past_date),
                     	disabled: true,
	                }],
	                before: 'thru_date'
	            },
	            {
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'thru_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                    value: OLBIUS.dateToString(cur_date),
	                    disabled: true,
	                }],
	                after: 'from_date'
	            },
	            {
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'from_date_1',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
	                    value: OLBIUS.dateToString(past_date),
	                    hide: true,
	                }],
	                before: 'thru_date'
	            },
	            {
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'thru_date_1',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                    value: OLBIUS.dateToString(cur_date),
	                    hide: true,
	                }],
	                after: 'from_date'
	            },
	            {
			        action : 'addDropDownList',
			        params : [{
			            id : 'customTime',
			            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
			            data : customDate,
			            index: 4,
			        }],
	                event : function(popup) {
	                    popup.onEvent('customTime', 'select', function(event) {
	                        var args = event.args;
	                        var item = popup.item('customTime', args.index);
	                        var filter = item.value;
	                        popup.clear('from_date');
	                        popup.clear('thru_date');
	                        if(filter == 'oo') {
	                            popup.show('from_date_1');
	                            popup.show('thru_date_1');
	                            popup.hide('from_date');
	                            popup.hide('thru_date');
	                        } else {
	                        	popup.show('from_date');
	                            popup.show('thru_date');
	                        	popup.hide('from_date_1');
	                            popup.hide('thru_date_1');
	                        }
	                        popup.resize();
	                    });
	                }
			    },
		];
    	
    	var storeOLap = OLBIUS.oLapChart('storeChart', config, configPopup, 'storeChartOlapVer2', true, true, function(data, chart, datetype, removeSeries, flagFunc, olap){
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
            var color = 4;
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

    	storeOLap.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'customTime': oLap.val('customTime'),
            });
        });
    	storeOLap.init(function () {
    		storeOLap.runAjax();
        });
    });
</script>