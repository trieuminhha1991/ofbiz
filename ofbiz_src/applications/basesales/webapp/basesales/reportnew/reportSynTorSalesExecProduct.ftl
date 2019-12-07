<script type="text/javascript">
	var configDataSync = {};
	var gridByVolume;
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

<div id="olbiusSynTorSalesExecProduct"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSByVolume)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusSynTorSalesExecProduct",
            olap: "olapSynTorSalesExecProduct",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            showstatusbar: true,
            statusbarheight: 30,
           	showaggregates: true,
           	columnsheight: 52,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSEmployeeId)}",
                    datafield: {name: "party_id", type: "string"},
                    width: 160, pinned : true
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSEmployeeName)}",
                    datafield: {name: "name", type: "string"},
                    minwidth: 200
                },
                <#assign listProduct = Static["com.olbius.basesales.reports.ReportSalesUtils"].getProductSalesOrder(delegator)/>
                <#if listProduct?exists>
                <#list listProduct as prod>
	                <#if prod.productId?has_content>
	                {text: "${prod.productCode?default("")}<br/>${StringUtil.wrapString(prod.productName?default(""))}",
	                    datafield: {name: "PROD${StringUtil.wrapString(prod.productId?default(""))}", type: "number"},
	                    width: 120, filtertype: 'number', 
	                    cellsrenderer: function(row, column, value) {
							if (value == 0) {
								return '<div class=\"innerGridCellContent align-right\">-</div>';
							} else {
								return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
							}
					 	},
					 	aggregates: [{'${StringUtil.wrapString(uiLabelMap.BSTotal)}': 
					 		function (aggregatedValue, currentValue) {
					 			return aggregatedValue + currentValue;
					 		}
    				  	}],
    				  	aggregatesrenderer: function (aggregates) {
							var renderstring = "";
    		              	$.each(aggregates, function (key, value) {
    		                  	renderstring += '<div class="innerGridCellContent align-right" style="color:red;font-weight:bold">' + formatnumber(value) + '</div>';
    		              	});
    		              	return renderstring;
    		          	}
	                },
	                </#if>
                </#list>
                </#if>
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
            	<#--
            	configDataSync.fromDate = dateTimeData.fromDate;
            	configDataSync.thruDate = dateTimeData.thruDate;
            	configDataSync.dateType = dateTimeData.dateType;
            	if (gridByVolume) gridByVolume.update();
            	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
            	-->
                return $.extend({
                	"groupByExName": "byQuantity",
                	"orderStatusId": popup.val("orderStatusId"),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
            excel: true,
            exportFileName: '[SALES]_TH_DSSP_NVCHAMSOC_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
    });
</script>

<div id="olbiusSynTorSalesExecProductByVolume" class="margin-top30"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSByValue)}",
            service: "salesOrderNew",
            button: false,
            id: "olbiusSynTorSalesExecProductByVolume",
            olap: "olapSynTorSalesExecProduct",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            showstatusbar: true,
            statusbarheight: 30,
           	showaggregates: true,
           	columnsheight: 52,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSEmployeeId)}",
                    datafield: {name: "party_id", type: "string"},
                    width: 160, pinned : true
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSEmployeeName)}",
                    datafield: {name: "name", type: "string"},
                    width: 200
                },
                <#if listProduct?exists>
                <#list listProduct as prod>
	                <#if prod.productId?has_content>
	                {text: "${prod.productCode?default("")}<br/>${StringUtil.wrapString(prod.productName?default(""))}",
	                    datafield: {name: "PROD${StringUtil.wrapString(prod.productId?default(""))}", type: "number"},
	                    width: 120, filtertype: 'number', 
	                    cellsrenderer: function(row, column, value) {
							if (value == 0) {
								return '<div class=\"innerGridCellContent align-right\">-</div>';
							} else {
								return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
							}
					 	},
					 	aggregates: [{'${StringUtil.wrapString(uiLabelMap.BSTotal)}': 
					 		function (aggregatedValue, currentValue) {
					 			return aggregatedValue + currentValue;
					 		}
    				  	}],
    				  	aggregatesrenderer: function (aggregates) {
							var renderstring = "";
    		              	$.each(aggregates, function (key, value) {
    		                  	renderstring += '<div class="innerGridCellContent align-right" style="color:red;font-weight:bold">' + formatcurrency(value) + '</div>';
    		              	});
    		              	return renderstring;
    		          	}
	                },
	                </#if>
                </#list>
                </#if>
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
                return $.extend({
                	"groupByExName": "byAmount",
                	"orderStatusId": popup.val("orderStatusId"),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
            excel: true,
            exportFileName: '[SALES]_TH_DSSP_NVCHAMSOCSL_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var gridByVolume = OlbiusUtil.grid(config);
    });
</script>

