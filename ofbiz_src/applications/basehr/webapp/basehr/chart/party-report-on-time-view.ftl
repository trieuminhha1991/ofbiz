<script type="text/javascript" id="partyOLapOnTime">

    $(function(){

        var config = {
            chart: {
                type: 'area'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_ontime_chart_title)}'
            },
            subtitle: {
                text: null
            },
            xAxis: {
                tickmarkPlacement: 'on',
                title: {
                    enabled: false
                }
            },
            yAxis: {
                title: {
                    text: null
                }
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.percentage:.1f}%</b><br/>',
                shared: true
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

        var partyOLap = OLBIUS.oLapChart('partyOLapOnTime', config, configPopup, 'onTime', true, true, OLBIUS.defaultLineFunc);

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