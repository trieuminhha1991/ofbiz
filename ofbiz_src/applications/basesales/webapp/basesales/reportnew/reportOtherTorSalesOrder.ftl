<@jqOlbCoreLib />

<div id="olbiusOtherTorSalesOrder"></div>
<script type="text/javascript">
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
	
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap[titleProperty])}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusOtherTorSalesOrder",
            olap: "olapOtherTorSalesOrder",
            theme: OlbCore.theme,
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderId)}",
                    datafield: {name: "order_id", type: "string"},
                    width: 130
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCreateDate)}",
                    datafield: {name: "order_date", type: "date"},
                    width: 100,
                    cellsrenderer: function(row, colum, value) {
						return '<span>' + jOlbUtil.dateTime.formatFullDate(value) + '</span>';
					}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}",
                    datafield: {name: "store_name", type: "string"},
                    minwidth: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCustomerId)}",
                    datafield: {name: "customer_id", type: "string"},
                    width: 100
                },
                <#--
                {text: "${StringUtil.wrapString(uiLabelMap.BSCustomerName)}",
                    datafield: {name: "customer_name", type: "string"},
                    minwidth: 120
                },
                -->
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderedQty)}",
                    datafield: {name: "total_quantity", type: "number"},
                    width: 90, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPaidQty)}",
                    datafield: {name: "return_quantity", type: "number"},
                    width: 90, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSWeight)}",
                    datafield: {name: "total_selected_amount", type: "number"},
                    width: 90, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalDiscountAmount)}",
                    datafield: {name: "discount_amount", type: "number"},
                    width: 100, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSFinishValueTotal)} (chua VAT)",
                    datafield: {name: "sub_total_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSFinishValueTotal)} (da bao gom VAT)",
                    datafield: {name: "total_amount", type: "number"},
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
				window.open("exportReportOtherTorSalesOrderExcel?" + otherParam, "_blank");
            },
            exportFileName: '[SALES]_DS_DONHANG_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
    });
</script>
