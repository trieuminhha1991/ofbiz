<div id="olbiusChartColTopRecivePurchase"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartColTopReceivePurchase = {
			service: "facilityInventory",
            id: "olbiusChartColTopRecivePurchase",
            olap: "olapChartColTopReceivePurchase",
            
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BLChartTopReceivePurchase)}',
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
                formatter: function () {
                    return '<b> SKU: ' + this.x + '</b>' + '<br/>' + '<b> ${uiLabelMap.QuantitySum}: ' + this.y.toLocaleString(locale) + '</b>';
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
            chartRender: chartRenderTypeColumn({color: 5}),
            
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
                        label: "${StringUtil.wrapString(uiLabelMap.Facility)}",
                        source: dataProductStoreArr,
                        selectedIndex: 0
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'filterPrimaryCategory',
                        label: "${StringUtil.wrapString(uiLabelMap.BLCategoryProduct)}",
                        source: dataPrimaryCategoryArr,
                        selectedIndex: 0
                    }
                }
            ],
            
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	$('#categoryId').val(popup.element("filterPrimaryCategory").val());
                return $.extend({
                	'olapType': 'COLUMNCHART',
                	'filterTop': popup.element("filterTop").val(),
                	'filterSort': popup.element("filterSort").val(),
                	'filterProductStore': popup.element("filterProductStore").val(),
                	'filterPrimaryCategory': popup.element("filterPrimaryCategory").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };
	
		OlbiusUtil.chart(configOlbiusChartColTopReceivePurchase);
	});
</script>