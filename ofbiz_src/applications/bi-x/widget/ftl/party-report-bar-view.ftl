
<script type="text/javascript" id="partyOLapBar">
    $(function () {
        var config = {
            chart: {
                type: 'bar'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_bar_chart_title)}'
            },
            xAxis: [{
                reversed: false
            }, {
                opposite: true,
                reversed: false,
                linkedTo: 0
            }],
            yAxis: {
                title: {
                    text: null
                },
                labels: {
                    formatter: function () {
                        return Math.abs(this.value);
                    }
                },
                min: -40,
                max: 40
            },

            plotOptions: {
                series: {
                    stacking: 'normal'
                }
            },

            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.name + ', age ' + this.point.category + '</b><br/>' +
                            'Population: ' + Highcharts.numberFormat(Math.abs(this.point.y), 0);
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

        var partyOLap = OLBIUS.oLapChart('partyOLapBar', config, configPopup, 'personBirth', true, true, OLBIUS.defaultBarFunc);

        partyOLap.funcUpdate(function(oLap) {
            oLap.update({
                'gender': true,
                'group' : oLap.val('jqxTree')
            });
        });

        partyOLap.init(function () {
            partyOLap.runAjax();
        });

    });

</script>

