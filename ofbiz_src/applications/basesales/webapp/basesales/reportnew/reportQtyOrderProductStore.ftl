<script type="text/javascript">
	var configDataSync = {};
	
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
<#--{text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', type: 'string', width: '15%', cellsalign: 'left',
					datafield: {name: "facility_name", type: "string"},
				},-->
<div id="olbiusQtyOrderProductStore"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSPSQuantityOrderByProductStore)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusQtyOrderProductStore",
            olap: "olapQtyOrderProductStore",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.olap_dateType)}",
                    datafield: {name: "dateTime", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSChannelId)}",
                    datafield: {name: "product_store_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSChannelName)}",
                    datafield: {name: "store_name", type: "string"},
                    minwidth: 260
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderVolume)}",
                    datafield: {name: "num_order", type: "number"},
                    width: 120, filtertype: 'number', 
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
            	var popupData = $.extend(dateTimeData, {orderStatusId: popup.val("orderStatusId")});
            	configDataSync.fromDate = popupData.fromDate;
            	configDataSync.thruDate = popupData.thruDate;
            	configDataSync.dateType = popupData.dateType;
            	configDataSync.orderStatusId = popupData.orderStatusId;
                return $.extend({
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, popupData);
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
				window.open("exportReportQtyOrderProductStoreExcel?" + otherParam, "_blank");
            },
            exportFileName: 'TMP'
        };
        
        
        var grid = OlbiusUtil.grid(config);
        
    });
</script>

<#--
	gFromDate = oLap.val('from_date_1'); 
	gThruDate = oLap.val('thru_date_1'); 
	gStore = oLap.val('productStore'); 
	gCategory = oLap.val('category'); 
	gStatus = oLap.val('orderStatus'); 
	gCustomTime = oLap.val('customTime');
	'fromDate': gFromDate,
	'thruDate': gThruDate,
	'productStore': gStore,
	'category': gCategory,
	'orderStatus': gStatus,
	'customTime': gCustomTime,
-->
