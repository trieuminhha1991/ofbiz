<div id="olbiusChartLineSynQtyProduct"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartLineSynQtyProduct = {
			service: "salesOrderNew",
            id: "olbiusChartLineSynQtyProduct",
            olap: "olapChartLineSynQtyProduct",
            
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSChartQuantityOfProduct)}',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: false
                },
                title: {
                    text: null
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
                },
                min: 0
            },
            tooltip: {
                <#--pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
                valueDecimals: 0-->
                formatter: function () {
                    return '<b>' + this.x + '</b>' + '<br/>' + '<tspan style="fill:' + this.color + '" x="8" dy="15"></tspan><i> ' + this.series.name + '</i>: <b>' + this.y.toLocaleString(locale) + '</b>';
                }
            },
            legend: {
				layout: 'vertical',
				align: 'right',
				verticalAlign: 'middle',
				borderWidth: 0
            },
            chartRender: OlbiusUtil.getChartRender('defaultLineFunc'),
            
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
                        id: 'filter',
                        label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                        source: dataProductStoreArrDDL,
                        selectedIndex: 0
                    }
                },
                <#-- Loc theo san pham -->
            ],
            
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
                return $.extend({
                	'olapType': 'LINECHART',
                	'filterTop': popup.element("filterTop").val(),
                	'filterSort': popup.element("filterSort").val(),
                	'filterTypeId': popup.element("filter").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };
		
		OlbiusUtil.chart(configOlbiusChartLineSynQtyProduct);
	});
</script>
