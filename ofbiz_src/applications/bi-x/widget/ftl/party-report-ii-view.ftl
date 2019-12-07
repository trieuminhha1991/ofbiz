
<script type="text/javascript" id="partyOLapPerson">
    $(function(){

        var config = {
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_person_chart_title)}',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: false
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
                min : 0
            },
            legend: {
                enabled: false
            }
        };

        var configPopup = {
            'jqxTree': {
                action: 'addJQXTree',
                params: [{
                    id: 'jqxTree',
                    label: '${StringUtil.wrapString(uiLabelMap.party_tree_title)}'
                }]
            },
            'dateTypeId' : {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.party_dateType)}',
                    data : date_type_source,
                    index: 2
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

        var partyOLap = OLBIUS.oLapChart('partyOLapPerson', config, configPopup, 'personOlap', true, true, OLBIUS.defaultLineFunc);

        partyOLap.funcUpdate(function (oLap) {
            var group = oLap.val('jqxTree');
            if (group == null) {
                group = OLBIUS.getCompany();
            }
            oLap.update({
                'group': group,
                'ft': true,
                'dateType': oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date')
            }, oLap.val('dateType'));
        });

        partyOLap.init(function () {
            partyOLap.runAjax();
        });

    });

</script>