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
<div id="olbiusChartPieSynTorSales"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartPieSynTorSales = {
			service: "salesOrderNew",
            id: "olbiusChartPieSynTorSales",
            olap: "olapChartPieSynTorSales",
            
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSSynthesisPieChart)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>'
            },
            series: [{
                type: 'pie'
            }],
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false,
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
                        id: 'filter',
                        label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                        source: dataPieSTSFilterArr,
                        selectedIndex: 0
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filter2',
                        label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                        source: dataPieSTSFilter2Arr,
                        selectedIndex: 0
                    }
                }
            ],
            
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
                return $.extend({
                	'olapType': 'PIECHART',
                	'filterTypeId': popup.element("filter").val(),
                	'filterTypeId2': popup.element("filter2").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };
	
		OlbiusUtil.chart(configOlbiusChartPieSynTorSales);
	});
</script>
