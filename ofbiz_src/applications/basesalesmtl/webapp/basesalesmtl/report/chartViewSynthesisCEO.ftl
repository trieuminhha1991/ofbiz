<script type="text/javascript" src="/salesresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" id="saleOrderTotal">
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign productStore = delegator.findList("ProductStore", null, null, null, null, false)>
	<#assign salesRegion9 = delegator.findList("PartyRegion", null, null, null, null, false)>
   	
   	var filterData9 = [
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}", 'value': "PRODUCT_STORE"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}", 'value': "CHANNEL"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSBranch)}", 'value': "REGION"}, 
	   	{'text': "${StringUtil.wrapString(uiLabelMap.BSRanks)}", 'value': "LEVEL"}
   	];
   	
	var productStoreData13 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': ''}];
	<#if productStore?exists>
		<#list productStore as productStoreL >
			productStoreData13.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
		</#list>
	</#if>
	
	var productStoreList = [
		<#if productStore?exists>
			<#list productStore as itemStore>
				{
					productStoreId: "${itemStore.productStoreId?if_exists}",
					storeName: "${StringUtil.wrapString(itemStore.get("storeName", locale)?if_exists)}"
				},
			</#list>
		</#if>
	];
	var listStoreDataSource2 = [];	
	for(var x in productStoreList){
			var storeDataSource2 = {
				text: productStoreList[x].storeName,
				value: productStoreList[x].productStoreId,
			}
			listStoreDataSource2.push(storeDataSource2);
		}
		
	<#assign salesChannel9 = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!>
	var salesChannel9 = [
		<#list salesChannel9 as salesChannelL9>
			{
				enumId : "${salesChannelL9.enumId}",
				description: "${StringUtil.wrapString(salesChannelL9.get("description", locale))}"
			},
		</#list>	
	];
	
	var listChannelDataSource9 = [];
	for(var x in salesChannel9){
		var channelDataSource9 = {
			text: salesChannel9[x].description,
			value: salesChannel9[x].enumId,
		}
	listChannelDataSource9.push(channelDataSource9);
	} 
	
	var salesRegion9 = [
		<#list salesRegion9 as salesRegionL9>
			{
				partyId : "${salesRegionL9.partyId}",
				groupName: "${StringUtil.wrapString(salesRegionL9.get("groupName", locale))}"
			},
		</#list>	
	];
	
	var listRegionDataSource9 = [];
	for(var x in salesRegion9){
		var regionDataSource9 = {
			text: salesRegion9[x].groupName,
			value: salesRegion9[x].partyId,
		}
	listRegionDataSource9.push(regionDataSource9);
	} 

	
    $(function () {
        var config = {
        	service: 'salesOrder',
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
				id : 'filter',
				label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
				data : filterData9,
				index: 0
				}],
			    event : function(popup) {
		       	popup.onEvent('filter', 'select', function(event) {
						var args = event.args;
					    var item = popup.item('filter', args.index);
						var filter = item.value;
						popup.hide('product_store');
						popup.hide('channel');
						popup.hide('region');
						popup.hide('level');
						popup.clear('product_store');
						popup.clear('channel');
						popup.clear('region');
						popup.clear('level')
						if(filter == 'PRODUCT_STORE') {
							popup.show('product_store');
						} else if(filter == 'CHANNEL') {
							popup.show('channel');
						} else if(filter == 'REGION') {
							popup.show('region');
						} else if(filter == 'LEVEL') {
							popup.show('level');
						}
						popup.resize();
			        });
			    }
			},
            {
				action : 'addJqxGridMultil',
				params : [{
				id : 'product_store',
				title1: '${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}',
				title2: '${StringUtil.wrapString(uiLabelMap.BSStoreName)}',  
				label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
				data : listStoreDataSource2,
				value: []
				}]
			},
			{
				action : 'addJqxGridMultil',
				params : [{
				id : 'channel',
				title1: '${StringUtil.wrapString(uiLabelMap.BSChannelId)}',
				title2: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',  
				label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
				data : listChannelDataSource9,
				value: [],
				hide: true
				}]
			},
			{
				action : 'addJqxGridMultil',
				params : [{
				id : 'region',
				title1: '${StringUtil.wrapString(uiLabelMap.BSBranchId)}',
				title2: '${StringUtil.wrapString(uiLabelMap.BSBranch)}',  
				label : '${StringUtil.wrapString(uiLabelMap.BSBranch)}',
				data : listRegionDataSource9,
				value: [],
				hide: true
				}]
			},
			{
				action : 'addDropDownList',
				params : [{
					id : 'level',
					label : '${StringUtil.wrapString(uiLabelMap.BSRanks)}',
					data : [{'text': "${StringUtil.wrapString(uiLabelMap.BSASM)}", 'value': "ASM"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSRSM)}", 'value': "RSM"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSCSM)}", 'value': "CSM"}],
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

        var saleOrderOLap = OLBIUS.oLapChart('saleOrderTotal', config, configPopup, 'salesValueOlapChart', true, true, OLBIUS.defaultLineFunc);

        saleOrderOLap.funcUpdate(function (oLap) {
            oLap.update({
            	'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'dateType':  oLap.val('dateType'),
                'currency':  oLap.val('currency'),
                'store': oLap.val('product_store'),
                'channel': oLap.val('channel'),
                'filter': oLap.val('filter'),
                'region': oLap.val('region'),
                'level': oLap.val('level'),
            }, oLap.val('dateType'));
        });
        saleOrderOLap.init(function () {
            saleOrderOLap.runAjax();
        });
    });

</script>

<#--
{
				action : 'addDropDownList',
				params : [{
				id : 'filter',
				label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
				data : [{'text': "${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}", 'value': "PRODUCT_STORE"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSParty)}", 'value': "PARTY_FROM"}, {'text': "${StringUtil.wrapString(uiLabelMap.BSParty2)}", 'value': "PARTY_TO"} , {'text': "${StringUtil.wrapString(uiLabelMap.BSProduct)}", 'value': "PRODUCT"}],
				index: 0
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


if(filter == 'CHANNEL') {
                            popup.show('channel');
                        } else
                        
{'text': "${StringUtil.wrapString(uiLabelMap.DAChannel)}", 'value': "CHANNEL"},
 -->