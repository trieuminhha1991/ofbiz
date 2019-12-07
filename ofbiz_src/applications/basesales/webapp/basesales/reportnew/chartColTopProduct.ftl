<div id="olbiusChartColTopProduct"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartColTopProduct = {
			service: "salesOrderNew",
            id: "olbiusChartColTopProduct",
            olap: "olapChartColTopProduct",
            
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTPColumn)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: 0, //-30,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                },
                title : {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#000000'
                }],
                title : {
                    text: null
                },
                min: 0
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
	                maxPointWidth: 30,
	                borderWidth: 0,
                    dataLabels: {
                        enabled: true
                    }
	            }
	        },
            //chartRender: OlbiusUtil.getChartRender('defaultColumnFunc'),
            chartRender: chartRenderTypeColumn({color: 7}),
            
            popup: [
                {
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                    	index: 2,
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
                        source: dataCctpFilterSortArr,
                        selectedIndex: 0
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterSalesChannel',
                        label: "${StringUtil.wrapString(uiLabelMap.BSSalesChannelType2)}",
                        source: dataCctpSalesChannelArr,
                        selectedIndex: 0
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterType',
                        label: "${StringUtil.wrapString(uiLabelMap.BSViewBy)}",
                        source: dataCctpFilterTypeArr,
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
                	'filterSalesChannel': popup.element("filterSalesChannel").val(),
                	'filterType': popup.element("filterType").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };
	
		OlbiusUtil.chart(configOlbiusChartColTopProduct);
	});
</script>
