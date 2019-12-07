<script type="text/javascript" id="partyOLapTimeTracker">
    $(function(){

        var time = {};

        var config = {
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_time_tracker_chart_title)}'
            },

            /*xAxis: {
                type: 'datetime',
                dateTimeLabelFormats: {
                    day: '%b %e, %y'
                }
            },*/

            yAxis: {
                title: {
                    text: null
                },
                labels: {
                    formatter: function () {
                        var h = parseInt(this.value);
                        var tmp = (this.value - h)*60;
                        var m = parseInt(tmp);
                        tmp = (tmp - m)*60;
                        var s = parseInt(tmp);
                        return formatNumberLength(h, 2) + ':' + formatNumberLength(m, 2) + ':' + formatNumberLength(s, 2);
                    }
                }
            },

            tooltip: {
                crosshairs: true,
                shared: true,
                formatter: function() {
                    return '<b>' + Highcharts.dateFormat('%A, %B %e, %Y', new Date(this.x)) + '<b><br/><b>' + this.points[0].series.name +' : '+ time['averages'][ this.x] + '<b><br/><b>' +
                            this.points[1].series.name + ' : ' + time['ranges'][this.x][0] + ' - ' + time['ranges'][this.x][1] + '<b>';
                }
            },
            legend: {
                enabled: false
            }
        };

        var _timeTypeSource = [{text: '${StringUtil.wrapString(uiLabelMap.party_timeStart)}', value: 'START'}, {text: '${StringUtil.wrapString(uiLabelMap.party_timeEnd)}', value: 'END'}];

        var configPopup = {
            'jqxTree': {
                action: 'addJQXTree',
                params: [{
                    id: 'jqxTree',
                    label: '${StringUtil.wrapString(uiLabelMap.party_tree_title)}'
                }]
            },
            'timeId' : {
                action : 'addDropDownList',
                params : [{
                    id : 'timeType',
                    label : '${StringUtil.wrapString(uiLabelMap.party_timeType)}',
                    data : _timeTypeSource,
                    index: 0
                }]
            },
            'fromDateId' : {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                event: function(popup) {
                    popup.onEvent('from_date', 'valueChanged', function(event){
                        var fromDate = event.args.date;
                        var thruDate = popup.getDate('thru_date');
                        if(thruDate < fromDate) {
                            popup.val('thru_date', fromDate);
                        }
                    });
                }
            },
            'thruDateId' : {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                event: function(popup) {
                    popup.onEvent('thru_date', 'valueChanged', function(event){
                        var thruDate = event.args.date;
                        var fromDate = popup.getDate('from_date');
                        if(thruDate < fromDate) {
                            popup.val('from_date', thruDate);
                        }
                    });
                }
            }
        };

        var func = function(data, chart, dateType, removeSeries, flagFunc, oLap) {
            time = {};

            chart.xAxis[0].update({
                labels: {
                    enabled: true,
                    formatter: OLBIUS.getFormaterAxisLabel('DAY')
                },
                tickInterval: OLBIUS.getTickIntervalSize(data.xAxis.length, OLBIUS.getTickInterval()),
                categories: data.xAxis
            }, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }

            var tmp = [];
            for(var i in data.xAxis) {
                if(!time['averages']) {
                    time['averages'] = {};
                }
                time['averages'][data.yAxis['averages'][i][0]] = data.yAxis['averages'][i][1];
                var _tmp = [];
                _tmp.push(data.yAxis['averages'][i][0]);
                var _time = data.yAxis['averages'][i][1];
                var __time = _time.split(':');
                _time = 0.0;
                _time += parseInt(__time[0]);
                _time += parseInt(__time[1])/60;
                _time += parseInt(__time[2])/3600;
                _tmp.push(_time);
                tmp.push(_tmp);
            }

            chart.addSeries({
                name : 'Averages',
                data: tmp,
                marker: {
                    fillColor: 'white',
                    lineWidth: 2,
                    lineColor: Highcharts.getOptions().colors[0]
                }
            }, false);

            tmp = [];

            for(var i in data.xAxis) {
                if(!time['ranges']) {
                    time['ranges'] = {};
                }
                time['ranges'][data.yAxis['ranges'][i][0]] = [];
                time['ranges'][data.yAxis['ranges'][i][0]].push(data.yAxis['ranges'][i][1]);
                time['ranges'][data.yAxis['ranges'][i][0]].push(data.yAxis['ranges'][i][2]);
                var _tmp = [];
                _tmp.push(data.yAxis['ranges'][i][0]);
                var _time = data.yAxis['ranges'][i][1];
                var __time = _time.split(':');
                _time = 0.0;
                _time += parseInt(__time[0]);
                _time += parseInt(__time[1])/60;
                _time += parseInt(__time[2])/3600;
                _tmp.push(_time);
                _time = data.yAxis['ranges'][i][2];
                __time = _time.split(':');
                _time = 0.0;
                _time += parseInt(__time[0]);
                _time += parseInt(__time[1])/60;
                _time += parseInt(__time[2])/3600;
                _tmp.push(_time);
                tmp.push(_tmp);
            }

            chart.addSeries({
                name : 'Range',
                data : tmp,
                type: 'arearange',
                lineWidth: 0,
                linkedTo: ':previous',
                color: Highcharts.getOptions().colors[0],
                fillOpacity: 0.3,
                zIndex: 0
            }, false);

            chart.redraw();

            if(data.xAxis.length == 0) {
                flagFunc();
            }
        }

        var partyOLap = OLBIUS.oLapChart('partyOLapTimeTracker', config, configPopup, 'timeTracker', true, true, func);

        partyOLap.funcUpdate(function (oLap) {
            var group = oLap.val('jqxTree');
            if (group == null) {
                group = OLBIUS.getCompany();
            }
            oLap.update({
                'group': group,
                'timeId': oLap.val('timeType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date')
            });
        });

        partyOLap.init(function () {
            partyOLap.runAjax();
        });

    });
</script>