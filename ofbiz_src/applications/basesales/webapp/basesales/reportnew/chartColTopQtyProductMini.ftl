<div id="olbiusChartColTopQtyProductMini"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartColTopQtyProductMini = {
			service: "salesOrderNew", //job
            id: "olbiusChartColTopQtyProductMini",
            olap: "olapChartColTopQtyProductMini", //service
            
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSChartTopProductByQuantity)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: 0, //-30,
                    style: {
                        fontSize: '10px',
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
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterProductStore',
                        label: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}",
                        source: dataProductStoreArrDDL,
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
                	'filterProductStore': popup.element("filterProductStore").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };
	
		OlbiusUtil.chart(configOlbiusChartColTopQtyProductMini);
	});
</script>
