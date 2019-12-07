<script type="text/javascript" src="/salesresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" id="saleOrderQuantityTotal">
	<#assign products2 = delegator.findList("Product", null, null, null, null, false)>
	var productData2 = [
		<#if products2?exists>
			<#list products2 as item2>
				{
					productId: "${item2.productCode?if_exists}",
					productName: "${StringUtil.wrapString(item2.get("productName", locale)?if_exists)}"
				},
			</#list>
		</#if>
	];
	var mapProducDataById = {
       		<#if products2?exists>
       			<#list products2 as item>
       				<#assign s1 = StringUtil.wrapString(item.get("productName", locale)?if_exists)/>
       				"${item.productId?if_exists}": "${s1}",
       			</#list>
       		</#if>	
       };
	var mapProducData = {
			<#if products2?exists>
			<#list products2 as item>
			<#assign s1 = StringUtil.wrapString(item.get("productName", locale)?if_exists)/>
			"${item.productCode?if_exists}": "${s1}",
			</#list>
			</#if>	
	};
	var listProductDataSource2 = [];	
	for(var x in productData2){
			var productDataSource2 = {
				text: productData2[x].productName,
				value: productData2[x].productId,
			}
			listProductDataSource2.push(productDataSource2);
		}
	
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)>
   	
	var productStoreData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': ''}];
	<#if productStore?exists>
		<#list productStore as productStoreL >
			productStoreData2.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
		</#list>
	</#if>

    $(function () {
    	var dateCurrent = new Date();
		var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth() - 6, 1);	
        var config = {
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnoverSalesByProduct)}',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: false
                },
                title: {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
               	value: 0,
                    width: 1,
                    color: '#808080'
                }],
                title: {
                    text: null
                },
                min: 0
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.x + '</b>' + '<br/>' + '<tspan style="fill:' + this.color + '" x="8" dy="15">●</tspan><i> ' + mapProducDataById[this.series.name] + '</i>: <b>' + this.y.toLocaleString(locale) + '</b>';
                }
            }
        };

        var configPopup = [
    		{
				action : 'addJqxGridMultil',
				params : [{
					id : 'productId',
					title1: '${StringUtil.wrapString(uiLabelMap.ProductCode)}',
					title2: '${StringUtil.wrapString(uiLabelMap.ProductName)}',  
					label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
					data : listProductDataSource2,
					value: []
	        }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'product_store',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
                    data : productStoreData2,
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_dateType)}',
                    data : date_type_source,
                    index: 2
                }]
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'from_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(currentQueryDay)
                }],
                event: function (popup) {
                    popup.onEvent('from_date', 'valueChanged', function (event) {
                        var fromDate = event.args.date;
                        var thruDate = popup.getDate('thru_date');
                        if (thruDate < fromDate) {
                            popup.val('thru_date', fromDate);
                        }
                    });
                }
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'thru_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                event: function (popup) {
                    popup.onEvent('thru_date', 'valueChanged', function (event) {
                        var thruDate = event.args.date;
                        var fromDate = popup.getDate('from_date');
                        if (thruDate < fromDate) {
                            popup.val('from_date', thruDate);
                        }
                    });
                }
            }
        ];

        var saleOrderOLap = OLBIUS.oLapChart('saleOrderQuantityTotal', config, configPopup, 'salesOrderTotalOlap', true, true, OLBIUS.defaultLineFunc);


        saleOrderOLap.funcUpdate(function (oLap) {

            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'dateType':  oLap.val('dateType'),
                'type': ['PRODUCT_STORE', 'PARTY_FROM', 'PARTY_TO', 'CHANNEL', 'PRODUCT[]'],
                'PRODUCT_STORE': oLap.val('product_store'),
                'PARTY_FROM': oLap.val('party_from'),
                'PARTY_TO': oLap.val('party_to'),
                'CHANNEL': oLap.val('channel'),
                'PRODUCT': oLap.val('productId'),
                'promoFlag': oLap.val('promo'),
                'quantity': true,
                'show': 'PRODUCT[]'
            }, oLap.val('dateType'));
        });
        saleOrderOLap.init(function () {
            saleOrderOLap.runAjax();
        });

    });

</script>