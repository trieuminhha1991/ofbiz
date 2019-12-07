<script type="text/javascript">
	var configDataSync = {};
	var olbiusAreaChartObj;
	var olbiusColChartRegionObj;
	var olbiusPieChartRegionObj;
	
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

<div id="olbiusTorProductRegion"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSTurnoverProductByRegion)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusTorProductRegion",
            olap: "olapTorProductRegion",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.olap_dateType)}",
                    datafield: {name: "dateTime", type: "string"},
                    width: 100
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSRegion)}",
                    datafield: {name: "region_name", type: "string"},
                    width: 160
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}",
                    datafield: {name: "product_code", type: "string"},
                    width: 160
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}",
                    datafield: {name: "product_name", type: "string"},
                    minwidth: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPrimaryCategory)}",
                    datafield: {name: "category_name", type: "string"},
                    width: 120
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
            	if (olbiusColChartRegionObj) olbiusColChartRegionObj.update();
            	if (olbiusPieChartRegionObj) olbiusPieChartRegionObj.update();
                return $.extend({
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, popupData);
            },
            excel: true,
            exportFileName: '[SALES]_DSSP_VUNGMIEN_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
        
        $('body').on("runolapservicedone", function(){
        	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
        	if (olbiusColChartRegionObj) olbiusColChartRegionObj.update();
        	if (olbiusPieChartRegionObj) olbiusPieChartRegionObj.update();
        });
    });
</script>

<#-- ======================================== AREA CHART =========================================== -->
<div id="olbiusAreaChartTorProductRegion" class="margin-top30 container-chart-inner-page"></div>
<script type="text/javascript">
	$(function(){
		var config = {
			service: "salesOrderNew",
            id: "olbiusAreaChartTorProductRegion",
            olap: "olapTorProductRegion",
            
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
                enabled: true
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
<div class="row-fluid" class="margin-top30">
	<div id="olbiusColChartRegion" class="span6 container-chart-inner-page"></div>
	<div id="olbiusPieChartRegion" class="span6 container-chart-inner-page"></div>
</div>
<script type="text/javascript">
	$(function(){
		var config = {
			service: "salesOrderNew",
            id: "olbiusColChartRegion",
            olap: "olapChartPieSynTorRegion",
            
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnOverChartByProduct)}',
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
	        height: 0.6,
            chartRender: OlbiusUtil.getChartRender('defaultColumnFunc'),
            //chartRender: chartRenderTypeColumn({color: 7}),
            
            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'PIECHART',
                	xAxisName: 'product_code',
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, configDataSync);
            }
        };
	
		olbiusColChartRegionObj = OlbiusUtil.chart(config);
		
        <#-- ================================================= PIE CHART 2 ======================================== -->
		var configPieChartRegion = {
			service: "salesOrderNew",
            id: "olbiusPieChartRegion",
            olap: "olapChartPieSynTorRegion",
            
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnOverChartByRegion)}'
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
                	xAxisName: 'region_name',
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, configDataSync);
            },
            height: 0.6,
            chartRender : OlbiusUtil.getChartRender('defaultPieFunc')
        };
	
		olbiusPieChartRegionObj = OlbiusUtil.chart(configPieChartRegion);
	});
</script>
