<script type="text/javascript">
 	
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)!>
   	
	var productStoreData12 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': ''}];
	<#if productStore?exists>
		<#list productStore as productStoreL >
			productStoreData12.push({'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}',  'value': '${productStoreL.productStoreId?if_exists}'});
		</#list>
	</#if>

	<#assign salesRegion = delegator.findByAnd("PartyAcctgPreference",  {"partyId" : "${ownerPartyId}"}, null, false)>
	var salesRegion = [
	           	    <#list salesRegion as salesRegionL>
	           	    {
	           	    	partyId : "${salesRegionL.partyId}",
	           	    },
	           	    </#list>	
	           	];
	
	var listRegionDataSource = [{'text': '${StringUtil.wrapString(uiLabelMap.BSNullObject)}', 'value': null}];
  	for(var x in salesRegion){
    	var regionDataSource = {
     		text: salesRegion[x].partyId,
     		value: salesRegion[x].partyId,
    	}
    listRegionDataSource.push(regionDataSource);
   	} 
   	
   	
</script>
<script type="text/javascript" id="saleCountOrder">

    $(function () {
    	var dateCurrent = new Date();
		var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth() - 6, 1);	
     	var config = {
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSCountOrderChart)}',
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
            }
        };

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'product_store',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
                    data : productStoreData12,
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'party_from',
                    label : '${StringUtil.wrapString(uiLabelMap.BSParty)}',
                    data : listRegionDataSource,
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_product)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}].concat(OLBIUS.getProduct()),
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'type',
                    label : '${StringUtil.wrapString(uiLabelMap.BSParty2)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}],
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
                    value: OLBIUS.dateToString(currentFirstDay)
                }],
                before: 'thru_date'
            },
            {
                action: 'addDateTimeInput',
                params: [{
                    id: 'thru_date',
                    label: '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
            }
        ];

        var saleOrderOLap = OLBIUS.oLapChart('saleCountOrder', config, configPopup, 'salesOrderCountOlap', true, true, OLBIUS.defaultLineFunc, 0.35);


        saleOrderOLap.funcUpdate(function (oLap) {

            oLap.update({
             'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'dateType':  oLap.val('dateType'),
                'type': ['PRODUCT_STORE', 'PARTY_FROM', 'PARTY_TO', 'CHANNEL', 'PRODUCT', 'PARTY_TYPE'],
                'PRODUCT_STORE': oLap.val('product_store'),
                'PARTY_FROM': oLap.val('party_from'),
                'PARTY_TO': oLap.val('party_to'),
                'CHANNEL': oLap.val('channel'),
                'PRODUCT': oLap.val('productId'),
                'PARTY_TYPE': oLap.val('type')
            }, oLap.val('dateType'));
        });
        saleOrderOLap.init(function () {
            saleOrderOLap.runAjax();
        });

    });

</script>