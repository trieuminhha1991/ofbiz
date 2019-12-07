<div id="partyOLapPieSchool"></div>
<script type="text/javascript">
    $(function(){
        var configOlbiusChartPieSynTorSales = {
            service: "person",
            id: "partyOLapPieSchool",
            olap: "olapChartPiePartySchol",

            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_pie_school_title)}'
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
                        id: 'school_type',
                        label: "${StringUtil.wrapString(uiLabelMap.party_schoolType)}",
                        source: ["EDU_SYS", "CLASSIFICATION", "SCHOOL", "STUDY_MODE", "MAJOR"],
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
                    'schoolType': popup.element("school_type").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };

        OlbiusUtil.chart(configOlbiusChartPieSynTorSales);
    });
</script>
