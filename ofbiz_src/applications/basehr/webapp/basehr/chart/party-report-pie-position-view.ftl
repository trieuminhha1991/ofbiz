<script type="text/javascript" id="partyOLapPiePosition">
    $(function () {
        var config = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_pie_position_chart_title)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%</b>'
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
                    }
                }
            }
        };

        var configPopup = {
            'jqxTree' : {
                action : 'addJQXTree',
                params : [{
                    id : 'jqxTree',
                    label : '${StringUtil.wrapString(uiLabelMap.party_tree_title)}'
                }]
            }
        };

        var partyOLap = OLBIUS.oLapChart('partyOLapPiePosition', config, configPopup, 'position', true, true, OLBIUS.defaultPieFunc);

        partyOLap.funcUpdate(function(oLap) {
            oLap.update({
                'group' : oLap.val('jqxTree'),
                'type': 'POSITION_TYPE'
            });
        });

        partyOLap.init(function () {
            partyOLap.runAjax();
        });

    });

</script>