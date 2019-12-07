<script type="text/javascript">
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)>
   	
	var productStoreData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
	<#if productStore?exists>
		<#list productStore as productStoreL >
			productStoreData2.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
		</#list>
	</#if>
	
	var partyId = "${userLogin.partyId}";
	var currency = "VND";
	var productStore = null;
	var flag = "a";
</script>
<script type="text/javascript" id="salesmanOrderTotal">
    $(function () {
    	Highcharts.setOptions({
		    lang: {
		        decimalPoint: ',',
		        thousandsSep: '.'
		    }
		});
		
        var config = {
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnoverSales)}',
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
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
                valueDecimals: 2
            },
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: false
                    },
                    enableMouseTracking: true
                }
            },
        };

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'filter',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
                    data : [{'text': "${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}", 'value': "PRODUCT_STORE"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSParty2)}", 'value': "PARTY_TO"} , {'text': "${StringUtil.wrapString(uiLabelMap.BSProduct)}", 'value': "PRODUCT"}],
                    index: 0,
                }],
                event : function(popup) {
                    popup.onEvent('filter', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('filter', args.index);
                        var filter = item.value;
                        popup.hide('product_store');
                        popup.hide('party_from');
                        popup.hide('party_to');
                        popup.hide('channel');
                        popup.hide('productId');
                        popup.clear('product_store');
                        popup.clear('party_from');
                        popup.clear('party_to');
                        popup.clear('channel');
                        popup.clear('productId');
                        if(filter == 'PRODUCT_STORE') {
                            popup.show('product_store');
                        } else if(filter == 'PARTY_FROM') {
                            popup.show('party_from');
                        } else if(filter == 'PARTY_TO') {
                            popup.show('party_to');
                        } else if(filter == 'PRODUCT') {
                            popup.show('productId');
                        }
                        popup.resize();
                    });
                }
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'product_store',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
                    data : productStoreData2,
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'party_from',
                    label : '${StringUtil.wrapString(uiLabelMap.BSParty)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}, {'text': '${StringUtil.wrapString(uiLabelMap.BSNorth)}', 'value': 'MB'}, {'text': '${StringUtil.wrapString(uiLabelMap.BSSouth)}', 'value': 'MN'}],
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'party_to',
                    label : '${StringUtil.wrapString(uiLabelMap.BSParty2)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}],
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'channel',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}, {'text': "${StringUtil.wrapString(uiLabelMap.BSFamily)}", 'value': "BHKENH_THUE_BAO"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSSchool)}", 'value': "BHKENH_TRUONG_HOC"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSBusiness)}", 'value': "BHKENH_VAN_PHONG"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSRetail)}", 'value': "BHKENH_BAN_LE"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSActivation)}", 'value': "BHKENH_ACTIVATION"}],
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_product)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}].concat(OLBIUS.getProduct()),
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'currency',
                    label : '${StringUtil.wrapString(uiLabelMap.BSCurrency)}',
                    data : ['VND'],
                    index: 0,
                    hide: true
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
                    value: OLBIUS.dateToString(past_date)
                }],
                before: 'thru_date'
                /*event: function (popup) {
                    popup.onEvent('from_date', 'valueChanged', function (event) {
                        var fromDate = event.args.date;
                        var thruDate = popup.getDate('thru_date');
                        if (thruDate < fromDate) {
                            popup.val('thru_date', fromDate);
                        }
                    });
                }*/
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'thru_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
                /*event: function (popup) {
                    popup.onEvent('thru_date', 'valueChanged', function (event) {
                        var thruDate = event.args.date;
                        var fromDate = popup.getDate('from_date');
                        if (thruDate < fromDate) {
                            popup.val('from_date', thruDate);
                        }
                    });
                }*/
            }
        ];

        var saleOrderOLap = OLBIUS.oLapChart('salesmanOrderTotal', config, configPopup, 'salesmanOrderTotalOlap', true, true, OLBIUS.defaultLineFunc);

        saleOrderOLap.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'dateType':  oLap.val('dateType'),
                'currency':  currency,
                'type': [oLap.val('filter')],
                'show': oLap.val('filter'),
                'PRODUCT_STORE': productStore,
                'PARTY_FROM': oLap.val('party_from'),
                'PARTY_TO': oLap.val('party_to'),
                'CHANNEL': oLap.val('channel'),
                'PRODUCT': oLap.val('productId'),
                'taxFlag' : oLap.val('tax'),
                'partyId': partyId,
                'flag': flag,
            }, oLap.val('dateType'));
        });   
        saleOrderOLap.init(function () {
            saleOrderOLap.runAjax();
        });

    });

</script>

<#--
if(filter == 'CHANNEL') {
                            popup.show('channel');
                        } else
                        
{'text': "${StringUtil.wrapString(uiLabelMap.DAChannel)}", 'value': "CHANNEL"},
 -->