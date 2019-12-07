<div id="partyOLapBar"></div>
<script type="text/javascript">
    $(function(){
        var configOlbiusPartyChartBar = {
            service: "person", //job
            id: "partyOLapBar",
            olap: "olapChartColPartyBarHR", //service

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
            },
            chartRender: OlbiusUtil.getChartRender('defaultBarFunc'),
            popup: [
                {
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                        index: 4,
                    }
                },
            ],

            apply: function (grid, popup) {
                var dateTimeData = popup.group("dateTime").val();
                var group = null;
                if (group == null) {
                    group = OLBIUS.getCompany();
                }
                group = OLBIUS.getChildGroups(group);
                if(group.length == 0) {
                    group = ['?'];
                }0
                return $.extend({
                    'olapType': 'COLUMNCHART',
                    'group': group,
                    'gender': true,
                    <#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };

        OlbiusUtil.chart(configOlbiusPartyChartBar);
    });
</script>
