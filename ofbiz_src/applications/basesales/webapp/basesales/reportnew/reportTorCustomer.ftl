<script type="text/javascript">
	var configDataSync = {};
	var olbiusAreaChartObj;
	
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_STATUS"}, null, true)!/>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as item>
		{	value: '${item.statusId}',
			text: '${StringUtil.wrapString(item.get("description", locale))}'
		},
		</#list>
	</#if>
	];
</script>

<div id="olbiusTorCustomer"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSTurnOverCustomer)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusTorCustomer",
            olap: "olapTorCustomer",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.olap_dateType)}",
                    datafield: {name: "dateTime", type: "string"},
                    width: 100
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}",
                    datafield: {name: "store_name", type: "string"},
                    width: 110
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCustomerId)}",
                    datafield: {name: "customer_code", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCustomerName)}",
                    datafield: {name: "customer_name", type: "string"},
                    minwidth: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSQuantity)}",
                    datafield: {name: "total_quantity", type: "number"},
                    width: 160, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSWeight)}",
                    datafield: {name: "total_selected_amount", type: "number"},
                    width: 160, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSValueTotal)}",
                    datafield: {name: "total_amount", type: "number"},
                    width: 200, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
            ],
            popup: [
                {
                    group: "dateTime",
                    id: "dateTime"
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'orderStatusId',
                        label: "${StringUtil.wrapString(uiLabelMap.BSOrderStatus)}",
                        source: orderStatusData,
                        selectedIndex: _.find(_.map(orderStatusData, function(obj, key){
					    	if (obj.value == "ORDER_COMPLETED") {
								return key;
							}
					    }), function(key){ return key })
                    },
                    hide: false
                }
            ],
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	var popupData = $.extend(dateTimeData, {
            		orderStatusId: popup.val("orderStatusId")
            	});
            	configDataSync.fromDate = popupData.fromDate;
            	configDataSync.thruDate = popupData.thruDate;
            	configDataSync.dateType = popupData.dateType;
            	configDataSync.orderStatusId = popupData.orderStatusId;
            	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
                return $.extend({
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, popupData);
            },
            excel: true,
            exportFileName: '[SALES]_DSKHACHHANG_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
        
        $('body').on("runolapservicedone", function(){
        	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
        });
    });
</script>

<#-- ======================================== AREA CHART =========================================== -->
<div id="olbiusAreaChartTorCustomer" class="margin-top30 container-chart-inner-page"></div>
<script type="text/javascript">
	$(function(){
		var config = {
			service: "salesOrderNew",
            id: "olbiusAreaChartTorCustomer",
            olap: "olapTorCustomer",
            
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPPSColumn)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -30,
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
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
            },
            plotOptions: {
	            series: {
	                maxPointWidth: 30
	            }
	        },
	        height: 0.35,
	        chartRender : OlbiusUtil.getChartRender('defaultColumnFunc'),
            
            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'COLUMNCHART',
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, configDataSync);
            }
        };
	
		olbiusAreaChartObj = OlbiusUtil.chart(config);
	});
</script>

<#-- ======================================== PIE CHART =========================================== -->
<#--
<div class="row-fluid" class="margin-top30">
	<div id="olbiusPieChartProdStore" class="span6"></div>
	<div id="olbiusPieChartSalesChannel" class="span6"></div>
</div>
<script type="text/javascript">
	$(function(){
		var configPieChart1 = {
			service: "salesOrderNew",
            id: "olbiusPieChartProdStore",
            olap: "olapTorCustomer",
            
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPSTurnOverChartBySalesChannel)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y} VND</b>'
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
                    },
                    showInLegend: true
                }
            },
            
            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'PIECHART',
                	xAxisName: 'store_name'
                }, configDataSync);
            },
            height: 0.7,
            chartRender : OlbiusUtil.getChartRender('defaultPieFunc')
        };
	
		olbiusPieChartProdStoreObj = OlbiusUtil.chart(configPieChart1);
		
         ================================================= PIE CHART 2 ======================================== 
		var configPieChart2 = {
			service: "salesOrderNew",
            id: "olbiusPieChartSalesChannel",
            olap: "olapTorCustomer",
            
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPSTurnOverChartBySalesChannelType)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y} VND</b>'
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
                    },
                    showInLegend: true
                }
            },
            
            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'PIECHART',
                	xAxisName: 'sales_method_channel_name'
                }, configDataSync);
            },
            height: 0.7,
            chartRender : OlbiusUtil.getChartRender('defaultPieFunc')
        };
	
		olbiusPieChartSalesChannelObj = OlbiusUtil.chart(configPieChart2);
	});
</script>
-->