<#include "chartLineSynTorSales.ftl"/>
<div id="partyOLapPersonMember"></div>
<script type="text/javascript">
    $(function(){
        var configOlbiusChartColTopQtyProductMini = {
            service: "person", //job
            id: "partyOLapPersonMember",
            olap: "olapChartColPartyMember", //service

            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.party_person_member_chart_title)}',
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
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
            <#--pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',-->
                formatter: function () {
                    return '<i>' + this.x + '</i>' + '<br/>' + '<b>' + this.y.toLocaleString(locale) + '</b>';
                }
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true
                    }
                }
            },
            //chartRender: OlbiusUtil.getChartRender('defaultColumnFunc'),
            chartRender: chartRenderTypeColumn({color: 2}),
            popup: [
                {
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                        index: 4,
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterTop',
                        label: "${StringUtil.wrapString(uiLabelMap.BSTop)}",
                        source: dataCctpFilterTopArr,
                        selectedIndex: 0
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterSort',
                        label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                        source: dataTopQtyFilterSortArr,
                        selectedIndex: 0
                    }
                }
            ],

            apply: function (grid, popup) {
                var dateTimeData = popup.group("dateTime").val();
                return $.extend({
                    'olapType': 'COLUMNCHART',
                    'filterTop': popup.element("filterTop").val(),
                    'filterSort': popup.element("filterSort").val(),
//                    'filterProductStore': popup.element("filterProductStore").val(),
                    <#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };

        OlbiusUtil.chart(configOlbiusChartColTopQtyProductMini);
    });
</script>
