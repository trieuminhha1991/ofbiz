
<script type="text/javascript" id="partyOLapPieAge">

    $(function () {

        var config = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_pie_age_chart_title)}'
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

        var partyOLap = OLBIUS.oLapChart('partyOLapPieAge', config, configPopup, 'personBirth', true, true, OLBIUS.defaultPieFunc);

        partyOLap.funcUpdate(function(oLap) {
            var group = oLap.val('jqxTree');
            if(group == null) {
                group = OLBIUS.getCompany();
            }
            oLap.update({
                'group' : group
            });
        });

        partyOLap.init(function () {
            partyOLap.runAjax();
        });

    });

</script>