<script type="text/javascript">
	var configDataSync = {};
	var olbiusColChartProdStoreObj;
	var olbiusPieChartProdStoreObj;
	
	<#assign returnStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_RETURN_STTS"}, null, true)!/>
	var returnStatusData = [
	<#if returnStatuses?exists>
		<#list returnStatuses as item>
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
<div id="olbiusTorReturnProdStore"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSPSAbbTurnOverReturnByProductStore)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "returnSalesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusTorReturnProdStore",
            olap: "olapTorReturnProdStore",
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
                {text: "${StringUtil.wrapString(uiLabelMap.BSAbbQuantityProduct)}",
                    datafield: {name: "received_quantity", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSWeight)}",
                    datafield: {name: "received_amount", type: "number"},
                    width: 160, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSValueTotalReturn)}",
                    datafield: {name: "return_total_amount", type: "number"},
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
                        id: 'returnStatusId',
                        label: "${StringUtil.wrapString(uiLabelMap.BSOrderStatus)}",
                        source: returnStatusData,
                        selectedIndex: _.find(_.map(returnStatusData, function(obj, key){
					    	if (obj.value == "RETURN_COMPLETED") {
								return key;
							}
					    }), function(key){ return key })
                    },
                    hide: false
                }
            ],
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	var popupData = $.extend(dateTimeData, {returnStatusId: popup.val("returnStatusId")});
            	configDataSync.fromDate = popupData.fromDate;
            	configDataSync.thruDate = popupData.thruDate;
            	configDataSync.dateType = popupData.dateType;
            	configDataSync.returnStatusId = popupData.returnStatusId;
            	//if (olbiusColChartProdStoreObj) olbiusColChartProdStoreObj.update();
            	//if (olbiusPieChartProdStoreObj) olbiusPieChartProdStoreObj.update();
                return $.extend({
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, popupData);
            },
            excel: true,
            exportFileName: '[SALES]_DSSP_TRALAI_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        
        var grid = OlbiusUtil.grid(config);
        
        //$('body').on("runolapservicedone", function(){
        //	if (olbiusColChartProdStoreObj) olbiusColChartProdStoreObj.update();
        //	if (olbiusPieChartProdStoreObj) olbiusPieChartProdStoreObj.update();
        //});
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

<#--
<#include "reportChartTorReturnProdStore.ftl"/>
-->
