<script type="text/javascript">
   	<#if Static["com.olbius.basehr.util.SecurityUtil"].hasRole("ACC_EMPLOYEE", partyId, delegator) || Static["com.olbius.basehr.util.SecurityUtil"].hasRole("LOG_ADMIN", partyId, delegator)>
   		<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
		<#assign listProductStore = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, company), null, null, null, false) />
	<#else>
		<#assign listFacilityIds = Static["com.olbius.util.FacilityUtil"].getFacilityManages(delegator, userLogin)!>
		<#assign listProductStore = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listFacilityIds), null, null, null, false) />
	</#if>
	
	var dataProductStoreArr = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'ALL'}];
	var dataProductStoreTmp = [
	<#if listProductStore?has_content>
		<#list listProductStore as item>
		{"value": "${item.facilityId}", "text": "${StringUtil.wrapString(item.facilityName?default(""))}"},
		</#list>
	</#if>
	];
	var dataProductStoreArr = $.merge(dataProductStoreArr, dataProductStoreTmp);
	
	<#assign listPrimaryCategory = Static["com.olbius.product.util.ProductUtil"].getAllProductCatalogCategory(delegator)!/>
   	var dataPrimaryCategoryArr = [
	<#if listPrimaryCategory?has_content>
		<#list listPrimaryCategory as item>
		{"value": "${item.productCategoryId}", "text": "${item.categoryName?default("")}"},
		</#list>
	</#if>
	];
	
	var dataCctpFilterTopArr = ["5", "10", "15", "20"];
	var dataTopQtyFilterSortArr = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityHighest)}', 'value': 'DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityLowest)}', 'value': 'ASC'}
	];
	
	var chartRenderTypeColumn = function(config){
		var $color = typeof(config.color) != 'undefined' ? config.color : 0;
		return function(data, obj) {
		        var tmp = {
		            labels: {
		                enabled: true
		            },
		            categories: data.xAxis
		        };
		
		        obj._chart.xAxis[0].update(tmp, false);
		
		        while (obj._chart.series.length > 0) {
		            obj._chart.series[0].remove(false);
		        }
		
		        var color = $color;
		        for (var i in data.yAxis) {
		            obj._chart.addSeries({
		                name: i,
		                data: data.yAxis[i],
		                color: Highcharts.getOptions().colors[color++]
		            }, false);
		        }
		
		        obj._chart.redraw();
		
		        return !!(data.xAxis && data.xAxis.length == 0);
		    };
	};
</script>
<div id="olbiusChartColTopExportSales"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartColTopExportSales = {
			service: "facilityInventory",
            id: "olbiusChartColTopExportSales",
            olap: "olapChartColTopExportSales",
            
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BLChartTopExportSales)}',
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
	
		OlbiusUtil.chart(configOlbiusChartColTopExportSales);
	});
</script>