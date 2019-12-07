<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
</script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)>
	var productStore = [
	    <#list productStore as productStoreL>
	    {
	    	productStoreId : "${productStoreL.productStoreId}",
	    	storeName: "${StringUtil.wrapString(productStoreL.get("storeName", locale))}"
	    },
	    </#list>	
	];
	var listPSDataSource = [];
  	for(var x in productStore){
    	var productStoreDataSource = {
     		text: productStore[x].storeName,
     		value: productStore[x].productStoreId,
    	}
    listPSDataSource.push(productStoreDataSource);
   	} 
   	
	var productStoreData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if productStore?exists>
		<#list productStore as productStoreL >
			productStoreData2.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
		</#list>
	</#if>

	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!>
	var salesChannel = [
	    <#list salesChannel as salesChannelL>
	    {
	    	enumId : "${salesChannelL.enumId}",
	    	description: "${StringUtil.wrapString(salesChannelL.get("description", locale))}"
	    },
	    </#list>	
	];
	
   	var channelData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if salesChannel?exists>
		<#list salesChannel as salesChannelL >
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.description)?if_exists}'});
		</#list>
	</#if>	
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

<script id="test">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSMTopRevenueByDistributor)}',
            service: 'salesOrder',
            columns: [
            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  datafield: 'stt', columntype: 'number', width: '3%',
		    	  cellsrenderer: function (row, column, value) {
		    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  }
			 	},   
                { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'customerId', type: 'string', width: '17%', cellsalign: 'left', hidden: true},
                { text: '${StringUtil.wrapString(uiLabelMap.BSDistributor)}', datafield: 'customerCode', type: 'string', width: '12%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'customerName', type: 'string', width: '35%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channelName', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'storeName', type: 'string', width: '20%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '15%', cellsformat: 'f0', cellsalign: 'right'}
            ]
        };

        var configPopup = [
        	{
	            action : 'addDateTimeInput',
	            params : [{
	                id : 'from_date',
	                label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
	                value: OLBIUS.dateToString(past_date),
	             	disabled: true,
	            }],
	            before: 'thru_date'
	        },
	        {
	            action : 'addDateTimeInput',
	            params : [{
	                id : 'thru_date',
	                label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                value: OLBIUS.dateToString(cur_date),
	                disabled: true,
	            }],
	            after: 'from_date'
	        },
	        {
	            action : 'addDateTimeInput',
	            params : [{
	                id : 'from_date_1',
	                label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
	                value: OLBIUS.dateToString(past_date),
	                hide: true,
	            }],
	            before: 'thru_date_1'
	        },
	        {
	            action : 'addDateTimeInput',
	            params : [{
	                id : 'thru_date_1',
	                label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                value: OLBIUS.dateToString(cur_date),
	                hide: true,
	            }],
	            after: 'from_date_1'
	        },
	        {
		        action : 'addDropDownList',
		        params : [{
		            id : 'customTime',
		            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
		            data : customDate,
		            index: 2,
		        }],
	            event : function(popup) {
	                popup.onEvent('customTime', 'select', function(event) {
	                    var args = event.args;
	                    var item = popup.item('customTime', args.index);
	                    var filter = item.value;
	                    popup.clear('from_date');
	                    popup.clear('thru_date');
	                    if(filter == 'oo') {
	                        popup.show('from_date_1');
	                        popup.show('thru_date_1');
	                        popup.hide('from_date');
	                        popup.hide('thru_date');
	                    } else {
	                    	popup.show('from_date');
	                        popup.show('thru_date');
	                    	popup.hide('from_date_1');
	                        popup.hide('thru_date_1');
	                    }
	                    popup.resize();
	                });
	            }
		    },
        	{
                action : 'addDropDownListMultil',
                params : [{
                    id : 'productStore',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
                    data : productStoreData2,
                    index: 0
                }]
            },
            {
                action : 'addDropDownListMultil',
                params : [{
                    id : 'channel',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
                    data : channelData2,
                    index: 0
                }]
            },
        ];

        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateMPC', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'productStore': oLap.val('productStore'),
                'channel': oLap.val('channel'),
                'customTime': oLap.val('customTime'),
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
        });
    });
</script>