<#-- data filter from chartSynTorSales.ftl file -->
<div id="olbiusChartLineSynQtyOrder"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartLineSynQtyOrder = {
			service: "salesOrderNew",
            id: "olbiusChartLineSynQtyOrder",
            olap: "olapChartLineSynQtyOrder",
            
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSCountOrderChart)}',
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
                valueDecimals: 2-->
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
                        id: 'filter',
                        label: "${StringUtil.wrapString(uiLabelMap.olap_filter)}",
                        source: dataSynTorSalesFilterArr,
                        selectedIndex: 0
                    },
                    event: [
                        {
                            name: 'select',
                            action: function (p, e) {
                                var item = e.element.jqxDropDownList('getSelectedItem');
                                var value = item.value;
                                switch (value) {
                                    case 'PRODUCT_STORE' :
                                    {
                                        p.element('productStore').show();
                                        p.element('salesChannel').hide();
                                        p.element('partySubsidiary').hide();
                                        break;
                                    }
                                    case 'SALES_CHANNEL' :
                                    {
                                        p.element('productStore').hide();
                                        p.element('salesChannel').show();
                                        p.element('partySubsidiary').hide();
                                        break;
                                    }
                                    case 'SUBSIDIARY' :
                                    {
                                        p.element('productStore').hide();
                                        p.element('salesChannel').hide();
                                        p.element('partySubsidiary').show();
                                        break;
                                    }
                                    default :
                                    {
                                        break
                                    }
                                }
                            }
                        }
                    ]
                },
                {
                    action: 'jqxGridMultiple',
                    params: {
                        id: 'productStore',
                        label: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}",
                        grid: {
        	            	source: dataProductStoreArr,
        	            	id: "value",
        	            	width: 550,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelId)}", datafield: 'value', width: 150 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelName)}", datafield: 'text' }
	            	        ]
        	            }
                    },
                    hide: false
                },
                {
                    action: 'jqxGridMultiple',
                    params: {
                        id: 'salesChannel',
                        label: "${StringUtil.wrapString(uiLabelMap.BSPSAbbSalesChannelType)}",
                        grid: {
        	            	source: dataSalesChannelArr,
        	            	id: "enumId",
        	            	width: 550,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSId)}", datafield: 'enumId', width: 150 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSDescription)}", datafield: 'description' }
	            	        ]
        	            }
                    },
                    hide: true
                },
                {
                    action: 'jqxGridMultiple',
                    params: {
                        id: 'partySubsidiary',
                        label: "${StringUtil.wrapString(uiLabelMap.BSBranch)}",
                        grid: {
        	            	source: dataPartySubsidiaryArr,
        	            	id: "partyId",
        	            	width: 550,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSPartyId)}", datafield: 'partyCode', width: 150 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSName)}", datafield: 'groupName' }
	            	        ]
        	            }
                    },
                    hide: true
                },
            ],
            
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
                return $.extend({
                	'olapType': 'LINECHART',
                	'filterTypeId': popup.element("filter").val(),
                	'productStoreIds': popup.element("productStore").val(),
                	'salesChannelIds': popup.element("salesChannel").val(),
                	'partySubsidiaryIds': popup.element("partySubsidiary").val(),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
        };
		
		OlbiusUtil.chart(configOlbiusChartLineSynQtyOrder);
	});
</script>
