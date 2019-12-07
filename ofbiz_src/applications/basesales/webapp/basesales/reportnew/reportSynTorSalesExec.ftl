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

<div id="olbiusSynTorSalesExec"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSTurnoverSynthesisBySalesExecutive)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusSynTorSalesExec",
            olap: "olapSynTorSalesExec",
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
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderVolume)}",
                    datafield: {name: "order_number", type: "number"},
                    width: 100, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderedQty)}",
                    datafield: {name: "total_quantity", type: "number"},
                    width: 100, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPaidQty)}",
                    datafield: {name: "return_quantity", type: "number"},
                    width: 100, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSWeight)}",
                    datafield: {name: "total_selected_amount", type: "number"},
                    width: 100, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalDiscountAmount)}",
                    datafield: {name: "discount_amount", type: "number"},
                    width: 140, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatcurrency(value, "${locale}", true) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSFinishValueTotal)}",
                    datafield: {name: "total_amount", type: "number"},
                    width: 160, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatcurrency(value, "${locale}", true) + '</div>';
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
            	<#--
            	configDataSync.fromDate = dateTimeData.fromDate;
            	configDataSync.thruDate = dateTimeData.thruDate;
            	configDataSync.dateType = dateTimeData.dateType;
            	if (gridByVolume) gridByVolume.update();
            	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
            	-->
                return $.extend({
                	"orderStatusId": popup.val("orderStatusId"),
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
            excel: function(obj){
            	var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					OlbCore.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}
				
				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value){
						otherParam += "&" + key + "=" + value;
					});
				}
				var filterObject = grid.getFilter();
				if (filterObject && filterObject.filter) {
					var filterData = filterObject.filter;
					for (var i = 0; i < filterData.length; i++) {
						otherParam += "&filter=" + filterData[i];
					}
				}
				window.open("exportReportSyncTorSalesExecExcel?" + otherParam, "_blank");
            },
            exportFileName: '[SALES]_TH_DS_NVCHAMSOC_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
    });
</script>
