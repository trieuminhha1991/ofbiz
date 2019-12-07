
<script type="text/javascript" id="facilityDeliveryOLap">

    $(function () {
        var config = {
            chart: {
                type: 'area'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.facility_delivery_title)}',
                x: -20 //center
            },
            xAxis: {
                tickmarkPlacement: 'on',
                labels: {
                    enabled: false
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
                min: 0
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.1f}%</b><br/>',
                shared: true
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            plotOptions: {
                area: {
                    stacking: 'percent',
                    lineColor: '#ffffff',
                    lineWidth: 1,
                    marker: {
                        lineWidth: 1,
                        lineColor: '#ffffff'
                    }
                }
            }
        };

        var _geoType = ['DISTRICT', 'PROVINCE', 'REGION', 'COUNTRY'];

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.facility_productId)}',
                    data : OLBIUS.getProduct(),
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'geoType',
                    label : 'Geo',
                    data : _geoType,
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'facilityFlag',
                    label : 'Flag',
                    data : [{'text': 'True', 'value': true}, {'text': 'False', 'value': false}],
                    index: 0
                }]
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                before : 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after : 'from_date'
            }
        ];

        var func = function(data, chart, datetype, removeSeries, flagFunc, olap) {
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

            var color = 0;
            var marker = 0;
            for (var i in data.yAxis) {
                chart.addSeries({
                    name: i,
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++],
                    marker: {
                        symbol: Highcharts.getOptions().symbols[marker++]
                    }
                }, false);
            }

            chart.redraw();

            if(data.xAxis.length == 0) {
                flagFunc();
            }
        };

        var facilityOLap = OLBIUS.oLapChart('facilityDeliveryOLap', config, configPopup, 'facilityProductDeliveryOlap', true, true, func);

        facilityOLap.funcUpdate(function(oLap) {
            oLap.update({
                'productId': oLap.val('productId'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'dateType': oLap.val('dateType'),
                'geoType': oLap.val('geoType'),
                'facilityFlag': oLap.val('facilityFlag')
            });
        });

        facilityOLap.init(function () {
            facilityOLap.runAjax();
        });

    });

</script>