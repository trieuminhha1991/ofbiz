<@jqOlbCoreLib />

<div id="olbiusOtherStatisticSalesData"></div>
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
    	var gridObj;
        
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap[titleProperty])}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusOtherStatisticSalesData",
            olap: "olapOtherStatisticSalesData",
            theme: OlbCore.theme,
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderId)}",
                    datafield: {name: "order_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCreatorId)}",
                    datafield: {name: "creator_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCreatorName)}",
                    datafield: {name: "creator_name", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelId)}",
                    datafield: {name: "product_store_id", type: "string"},
                    width: 100
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}",
                    datafield: {name: "store_name", type: "string"},
                    minwidth: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSDayMonthYearSlash)}",
                    datafield: {name: "order_date", type: "string"},
                    width: 100,
                    cellsrenderer: function(row, colum, value) {
						return '<span>' + jOlbUtil.dateTime.formatDate(value) + '</span>';
					}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSHourMinuteSecondSlash)}",
                    datafield: {name: "", type: "date"},
                    width: 90, hidden: false,
                    cellsrenderer: function(row, colum, value) {
                    	var rowData = gridObj._grid.jqxGrid('getrowdata', row);
						if (rowData) return '<span>' + jOlbUtil.dateTime.formatTime(rowData.order_date, '/') + '</span>';
						else return '';
					}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSAmount)} (chua VAT)",
                    datafield: {name: "sub_total_amount", type: "number"},
                    width: 110, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSAmount)} (da bao gom VAT)",
                    datafield: {name: "total_amount", type: "number"},
                    width: 110, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSNumberItem)}",
                    datafield: {name: "num_item", type: "number"},
                    width: 100, filtertype: 'number', 
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
				var dataRow = gridObj._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}
				
				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value){
						otherParam += "&" + key + "=" + value;
					});
				}
				window.location.href = "exportReportStatisticSalesDataExcel?" + otherParam;
				
            	//obj._grid.jqxGrid('exportdata', 'xls', obj._exportFileName, true, null, false, 'olbiusOlapExport', null, $.extend({
                //    serviceName: obj._serviceName,
                //    olapType: 'GRID',
                //    olapTitle: obj._title
                //}, obj._data));
            },
            exportFileName: '[SALES]_DS_DONHANG_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        gridObj = OlbiusUtil.grid(config);
    });
</script>
