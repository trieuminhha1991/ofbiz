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

<div id="olbiusOtherTorCategory"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap[titleProperty])}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusOtherTorCategory",
            olap: "olapOtherTorCategory",
            theme: OlbCore.theme,
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSCategoryId)}",
                    datafield: {name: "category_id", type: "string"},
                    width: 200, pinned : true
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCategoryName)}",
                    datafield: {name: "category_name", type: "string"},
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
                    width: 80, filtertype: 'number', 
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
            //hierarchy: {
			//	keyDataField: {name: 'category_id'},
			//	parentDataField: 'parent_category_id'
			//},
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
            	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
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
				window.open("exportReportOtherTorCategoryExcel?" + otherParam, "_blank");
            },
            exportFileName: '[SALES]_TH_DS_DANHMUC_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        //var grid = OlbiusUtil.treeGrid(config);
        var grid = OlbiusUtil.grid(config);
        
        $('body').on("runolapservicedone", function(){
        	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
        });
    });
</script>


