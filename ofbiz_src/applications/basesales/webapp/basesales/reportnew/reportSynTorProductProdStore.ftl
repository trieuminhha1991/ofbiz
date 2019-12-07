<script type="text/javascript">
	var configDataSync = {};
	var olbiusAreaChartObj;
	var olbiusColChartProdStoreObj;
	var olbiusPieChartProdStoreObj;
	
	<#--
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_STATUS"}, null, true)!/>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as item>
		{	value: '${item.statusId}',
			text: '${StringUtil.wrapString(item.get("description", locale))}'
		},
		</#list>
	</#if>
	];-->
</script>

<div id="olbiusSynTorProductProdStore"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSPSTurnoverProductSynthesisByChannel)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusSynTorProductProdStore",
            olap: "olapSynTorProductProdStore",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            showstatusbar: true,
            statusbarheight: 30,
           	showaggregates: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}",
                    datafield: {name: "product_code", type: "string"},
                    width: 160, pinned : true
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}",
                    datafield: {name: "product_name", type: "string"},
                    minwidth: 200
                },
                <#if listProductStore?exists>
                <#list listProductStore as prodStore>
	                <#if prodStore.productStoreId?has_content>
	                {text: "${StringUtil.wrapString(prodStore.storeName?default(""))}",
	                    datafield: {name: "${StringUtil.wrapString(prodStore.productStoreId?default(""))}", type: "number"},
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
                }
                <#--,{
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
                }-->
            ],
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	var popupData = $.extend(dateTimeData, {}); //orderStatusId: popup.val("orderStatusId")
            	configDataSync.fromDate = popupData.fromDate;
            	configDataSync.thruDate = popupData.thruDate;
            	configDataSync.dateType = popupData.dateType;
            	//configDataSync.orderStatusId = popupData.orderStatusId;
            	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
            	if (olbiusColChartProdStoreObj) olbiusColChartProdStoreObj.update();
            	if (olbiusPieChartProdStoreObj) olbiusPieChartProdStoreObj.update();
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
				window.open("exportReportSyncTorProductProdStoreExcel?" + otherParam, "_blank");
            },
            exportFileName: '[SALES]_TH_DSSP_KENH_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
        
        /*$('body').on("runolapservicedone", function(){
        	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
        	if (olbiusColChartProdStoreObj) olbiusColChartProdStoreObj.update();
        	if (olbiusPieChartProdStoreObj) olbiusPieChartProdStoreObj.update();
        });*/
        
        // init window popup charts
		$("#popupViewCharts").jqxWindow({maxWidth: 960, width: 960, height: 520, resizable: true, isModal: true, autoOpen: false, modalOpacity: 0.7, theme: "olbius", cancelButton: $("#wn_charts_alterClose")});
		$("#popupViewCharts").on("open", function(){
			var tmpwidth = $("#popupViewCharts").jqxWindow('width');
            $("#popupViewCharts").jqxWindow({position: {x: (window.outerWidth - tmpwidth)/2, y: pageYOffset + 30}});
		});
    });
    
    
    function openCharts(){
    	$("#popupViewCharts").jqxWindow("open");
    	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
    	if (olbiusColChartProdStoreObj) olbiusColChartProdStoreObj.update();
    	if (olbiusPieChartProdStoreObj) olbiusPieChartProdStoreObj.update();
    };
</script>

<div class="pull-right margin-top20">
	<button class="btn btn-small btn-primary" onClick="openCharts()"><i class="fa fa-pie-chart"></i> View charts</button>
</div>

<div id="popupViewCharts" style="display:none">
	<div>${uiLabelMap.Charts}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<#include "reportChartTorProductProdStore.ftl"/>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
				<button id="wn_charts_alterClose" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	   		</div>
		</div>
	</div>
</div>

