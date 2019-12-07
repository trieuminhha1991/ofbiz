<script type="text/javascript">
	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)>
	var salesChannel = [
	    <#list salesChannel as salesChannelL>
	    {
	    	enumId : "${salesChannelL.enumId}",
	    	description: "${StringUtil.wrapString(salesChannelL.get("description", locale))}"
	    },
	    </#list>	
	];
	
	var listChannelDataSource = [];
  	for(var x in salesChannel){
    	var channelDataSource = {
     		text: salesChannel[x].description,
     		value: salesChannel[x].enumId,
    	}
    listChannelDataSource.push(channelDataSource);
   	} 
   	
   	<#assign statusItem = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "ORDER_STATUS"), null, false)>
	var statusItem = [
	    <#list statusItem as statusItemL>
	    {
	    	statusId : "${statusItemL.statusId}",
	    	description: "${StringUtil.wrapString(statusItemL.get("description", locale))}"
	    },
	    </#list>	
	];
   	
   	var statusItemData = [];
	<#if statusItem?exists>
		<#list statusItem as statusItemL >
			statusItemData.push({ 'value': '${statusItemL.statusId?if_exists}', 'text': '${StringUtil.wrapString(statusItemL.get("description", locale))?if_exists}'});
		</#list>
	</#if>
</script>
<script id="test">
	$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSCustomerReport)}',
            columns: [
				{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
					  datafield: 'stt', columntype: 'number', width: '3%',
					  cellsrenderer: function (row, column, value) {
						  return '<div style=margin:4px;>' + (value + 1) + '</div>';
					  }
					},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channelName', type: 'string', width: '10%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'storeName', type: 'string', width: '15%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'customerId', type: 'string', width: '12%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'customerName', type: 'string' },
				  { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'volume', type: 'string', width: '10%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSTotal)}', datafield: 'monetized', type: 'number', width: '15%', cellsformat: 'n0', cellsalign: 'right'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSRevenueTotal)}', datafield: 'percent', type: 'number', width: '15%', cellsformat: 'p2', cellsalign: 'right'},
            ]
        };

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'channel',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
                    data : listChannelDataSource,
                    index: 0
                }]
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                before: 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
            },
            {
	            action : 'addDropDownList',
	            params : [{
	                id : 'orderStatus',
	                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
	                data : statusItemData,
	                index: 5
	            }]
	        },
        ];


        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateCustomerSM', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'orig': oLap.val('organization'),
                'orderStatus': oLap.val('orderStatus'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'channel':oLap.val('channel'),
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
        });
    });
</script>
