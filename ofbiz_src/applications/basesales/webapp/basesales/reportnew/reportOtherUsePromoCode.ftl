<script type="text/javascript">
	var configDataSync = {};
	var olbiusAreaChartObj;
	var olbiusPieChartProductObj;
	var olbiusPieChartCategoryObj;
</script>

<div id="olbiusOtherUsePromoCode"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap[titleProperty])}<#if viewPartner?exists && viewPartner == "Y"> (NPP)<#elseif viewPartner?exists && viewPartner == "A"> (DTM)</#if>",
            service: "salesOrderPromoCodeJob",
            button: <#if viewPartner?exists && viewPartner == "Y">false<#elseif viewPartner?exists && viewPartner == "A">false<#else>true</#if>,
            id: "olbiusOtherUsePromoCode",
            olap: "olapOtherUsePromoCode",
            theme: OlbCore.theme,
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.olap_dateType)}",
                    datafield: {name: "dateTime", type: "string"},
                    width: 100
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}",
                    datafield: {name: "product_promo_id", type: "string"},
                    width: 160
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPromoName)}",
                    datafield: {name: "promo_name", type: "string"},
                    minwidth: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCustomerName)}",
                    datafield: {name: "customer_name", type: "string"},
                    width: 160
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSVoucherCode)}",
                    datafield: {name: "product_promo_code_id", type: "string"},
                    width: 160
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderId)}",
                    datafield: {name: "order_id", type: "string"},
                    width: 160
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalDiscountAmount)}",
                    datafield: {name: "total_discount_amount", type: "number"},
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
                }
            ],
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
            	configDataSync.fromDate = dateTimeData.fromDate;
            	configDataSync.thruDate = dateTimeData.thruDate;
            	configDataSync.dateType = dateTimeData.dateType;
            	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
            	if (olbiusPieChartProductObj) olbiusPieChartProductObj.update();
            	if (olbiusPieChartCategoryObj) olbiusPieChartCategoryObj.update();
                return $.extend({
                	<#if viewPartner?exists>'viewPartner': '${viewPartner}',</#if>
                }, dateTimeData);
            },
            excel: true,
            exportFileName: '[SALES]_TH_DS_SANPHAM_' + (new Date()).formatDate("ddMMyyyy")
        };
        
        var grid = OlbiusUtil.grid(config);
        
        $('body').on("runolapservicedone", function(){
        	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
        	if (olbiusPieChartProductObj) olbiusPieChartProductObj.update();
        	if (olbiusPieChartCategoryObj) olbiusPieChartCategoryObj.update();
        });
    });
</script>
