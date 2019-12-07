<#--<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>-->

<script type="text/javascript" id="partyOLapMember">
    $(function () {

        var config = {
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_member_chart_title)}',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: true
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
                }
            },
            legend: {
                /*layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0*/
                enabled: false
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true
                    }
                }
            }
        };

        var configPopup = {
            'jqxTree': {
                action: 'addJQXTree',
                params: [{
                    id: 'jqxTree',
                    label: '${StringUtil.wrapString(uiLabelMap.party_tree_title)}'
                }]
            }
        };

        var partyOLap = OLBIUS.oLapChart('partyOLapMember', config, configPopup, 'member', true, true, OLBIUS.defaultColumnFunc);

        partyOLap.funcUpdate(function (oLap) {
            var group = oLap.val('jqxTree');
            if (group == null) {
                group = OLBIUS.getCompany();
            }
            group = OLBIUS.getChildGroups(group);
            if(group.length == 0) {
                group = ['?'];
            }
            var date = new Date();
            oLap.update({
                'group': group,
                'child': true,
                'cur': true,
                'fromDate': '1990-01-01',
                'thruDate': date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate()
            });
        });

        partyOLap.init(function () {
            partyOLap.runAjax();
        });

    });

</script>