<script type="text/javascript">
	var dataSynTorSalesFilterArr = [
		{'text': "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}", 'value': "PRODUCT_STORE"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelType)}", 'value': "SALES_CHANNEL"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSBranch)}", 'value': "SUBSIDIARY"}
   	];
   	
   	<#assign currentOrgId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator, userLogin)!/>
   	<#if viewPartner?exists && hasOlbPermission("MODULE", "SALES_REPORT_ALLORG", "")>
   		<#if viewPartner == "Y">
   			<#assign listProductStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payToPartyId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, currentOrgId), null, null, null, false)!>
   		<#elseif viewPartner == "A">
   			<#assign listProductStore = delegator.findByAnd("ProductStore", null, null, false)!>
   		</#if>
   	<#else>
		<#assign listProductStore = delegator.findByAnd("ProductStore", {"payToPartyId", currentOrgId}, null, false)!>
   	</#if>

	var dataProductStoreArr = [
	<#if listProductStore?has_content>
		<#list listProductStore as item>
		{"value": "${item.productStoreId}", "text": "${StringUtil.wrapString(item.storeName?default(""))}"},
		</#list>
	</#if>
	];
	var dataProductStoreArrDDL = $.merge([{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': ''}], dataProductStoreArr);
	
	<#assign listSalesChannel = delegator.findByAnd("Enumeration", {"enumTypeId", "SALES_METHOD_CHANNEL"}, null, false)!>
	var dataSalesChannelArr = [
	<#if listSalesChannel?has_content>
		<#list listSalesChannel as item>
		{"enumId": "${item.enumId}", "description": "${StringUtil.wrapString(item.description?default(""))}"},
		</#list>
	</#if>
	];
	
	<#assign listSubsidiaries = Static["com.olbius.basehr.util.SecurityUtil"].getOrganization(userLogin.userLoginId, "${parameters.basePermission?if_exists}", delegator, false)!>
	<#--<#assign listPartySubsidiary = delegator.findByAnd("PartyRegion", {"partyId", currentOrgId}, null, false)!>-->
	<#assign listPartySubsidiary = delegator.findList("PartyFullNameDetailSimple", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listSubsidiaries), null, null, null, false)!>
   	var dataPartySubsidiaryArr = [
	<#if listPartySubsidiary?has_content>
		<#list listPartySubsidiary as item>
		{"partyId": "${item.partyId}", "partyCode": "${item.partyCode?default("")}", "groupName": "${StringUtil.wrapString(item.groupName?default(""))}"},
		</#list>
	</#if>
	];
	
	var dataCctpFilterTopArr = ["5", "10", "15", "20"];
	var dataCctpFilterSortArr = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSTurnoverHighest)}', 'value': 'DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSTurnoverLowest)}', 'value': 'ASC'}
	];
	var dataTopQtyFilterSortArr = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityHighest)}', 'value': 'DESC'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantityLowest)}', 'value': 'ASC'}
	];
	var dataCctpFilterTypeArr = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProduct)}', 'value': 'PRODUCT'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesExecutive)}', 'value': 'SALES_EXECUTIVE'}
	];
   	
   	<#assign listSalesChannel = delegator.findByAnd("Enumeration", {"enumTypeId", "SALES_METHOD_CHANNEL"}, null, false)!>
	var dataCctpSalesChannelArr = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': ''},
	<#if listSalesChannel?has_content>
		<#list listSalesChannel as item>
		{"value": "${item.enumId}", "text": "${StringUtil.wrapString(item.description?default(""))}"},
		</#list>
	</#if>
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
<div id="olbiusChartLineSynTorSales"></div>
<script type="text/javascript">
	$(function(){
		var configOlbiusChartLineSynTorSales = {
			service: "salesOrderNew",
            id: "olbiusChartLineSynTorSales",
            olap: "olapChartLineSynTorSales",
            
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnoverSales)}',
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
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
                valueDecimals: 2 
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
	
		var olbiusChartLineSynTorSalesObj = OlbiusUtil.chart(configOlbiusChartLineSynTorSales);
	});
</script>
