<script type="text/javascript">
    var dataPieSTSFilterArr = [
        {'text': "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}", 'value': "PRODUCT_STORE"},
        {'text': "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelType)}", 'value': "SALES_CHANNEL"},
    ];

    var dataPieSTSFilter2Arr = [
        {'text': '${StringUtil.wrapString(uiLabelMap.BSSalesValue)}', 'value': 'SALES_VALUE'},
        {'text': '${StringUtil.wrapString(uiLabelMap.BSSalesVolume)}', 'value': 'SALES_VOLUME'},
        {'text': '${StringUtil.wrapString(uiLabelMap.BSOrderVolume)}', 'value': 'ORDER_VOLUME'},
    ];
</script>
<div id="partyOLapPiePosition"></div>
<script type="text/javascript">
    $(function(){
        var configOlbiusChartPieSynTorSales = {
            service: "person",
            id: "partyOLapPiePosition",
            olap: "olapChartPiePositionViewHR",

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
            },
            chartRender: OlbiusUtil.getChartRender('defaultPieFunc'),

            popup: [
                {
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                        index: 5,
                        dateTypeIndex: 2,
                        fromDate: past_date,
                        thruDate: cur_date
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'position_type',
                        label: "${StringUtil.wrapString(uiLabelMap.party_positionType)}",
                        source: ["POSITION_TYPE", "POSITION"],
                        selectedIndex: 0
                    }
                }
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
                }
                console.log("test");
                console.log(group);
                return $.extend({
                    'olapType': 'PIECHART',
                    'group': group,
                    'type': popup.element("position_type").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };

        OlbiusUtil.chart(configOlbiusChartPieSynTorSales);
    });
</script>
