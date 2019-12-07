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
<div id="partyOLapPieGender"></div>
<script type="text/javascript">
    $(function(){
        var configOlbiusChartPiePartyGender = {
            service: "person",
            id: "partyOLapPieGender",
            olap: "olapChartPiePartyGenderHR",

            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_pie_gender_chart_title)}'
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
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };

        OlbiusUtil.chart(configOlbiusChartPiePartyGender);
    });
</script>
