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
<div id="olbiusTorProductProdStore"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSPSAbbTurnOverProductBySalesChannel)}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderNew",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusTorProductProdStore",
            olap: "olapTorProductProdStore",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.olap_dateType)}",
                    datafield: {name: "dateTime", type: "string"},
                    width: 100
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}",
                    datafield: {name: "product_code", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}",
                    datafield: {name: "product_name", type: "string"},
                    minwidth: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPrimaryCategory)}",
                    datafield: {name: "category_name", type: "string"},
                    width: 140
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}",
                    datafield: {name: "store_name", type: "string"},
                    width: 140
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSQuantity)}",
                    datafield: {name: "total_quantity", type: "number"},
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
                {text: "${StringUtil.wrapString(uiLabelMap.BSValueTotal)} (chua VAT)",
                    datafield: {name: "sub_total_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSValueTotal)} (da bao gom VAT)",
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
                }
                <#--
                ,{
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
                -->
            ],
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	var popupData = $.extend(dateTimeData, {}); //orderStatusId: popup.val("orderStatusId")
            	configDataSync.fromDate = popupData.fromDate;
            	configDataSync.thruDate = popupData.thruDate;
            	configDataSync.dateType = popupData.dateType;
            	//configDataSync.orderStatusId = popupData.orderStatusId;
            	//if (olbiusAreaChartObj) olbiusAreaChartObj.update();
            	//if (olbiusColChartProdStoreObj) olbiusColChartProdStoreObj.update();
            	//if (olbiusPieChartProdStoreObj) olbiusPieChartProdStoreObj.update();
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
				window.open("exportReportTorProductProdStoreExcel?" + otherParam, "_blank");
            },
            exportFileName: '[SALES]_DSSP_KENH_' + (new Date()).formatDate("ddMMyyyy")
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
