<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
var sourceTop = [
                  "10",
                  "20",
                  "50"
		        ];
var sourceTop2 = [
                  "3",
                  "5",
                  "10",
                  "20"
		        ];
var sourceStatus2 = [
			{	text: "${StringUtil.wrapString(uiLabelMap.BSSelling)}",
     			value: "F"},
			{	text: "${StringUtil.wrapString(uiLabelMap.BSSlowSelling)}",
     			value: "S"}
];

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

<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)>
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
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.get("description", locale))?if_exists}'});
		</#list>
	</#if>

	<#assign categoryList = delegator.findByAnd("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryTypeId", "CATALOG_CATEGORY"), null, false)!>
	var categoryList = [
	    <#list categoryList as categoryL>
	    {
	    	productCategoryId : "${categoryL.productCategoryId}",
	    	categoryName: "${StringUtil.wrapString(categoryL.get("categoryName", locale))}"
	    },
	    </#list>	
	];
	
	var categoryData= [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if categoryList?exists>
		<#list categoryList as categoryL >
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
		</#list>
	</#if>

 	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

<script id="topProductGrid">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSTopProduct)}',
            columns: [
	            { text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
	    	  		datafield: 'stt', columntype: 'number', width: '3%',
		    	  	cellsrenderer: function (row, column, value) {
		    		  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	 	}
			 	},   
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', datafield: 'category', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '32%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '15%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTotal)}', datafield: 'total1', type: 'number', width: '15%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channel', type: 'string', width: '20%'},
            ]
        };

        var configPopup = [
             {
			    action : 'addDateTimeInput',
			    params : [{
			        id : 'from_date',
			        label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
			        value: OLBIUS.dateToString(currentFirstDay)
			    }],
			    before: 'thru_date'
			},
            {
                action : 'addDropDownList',
                params : [{
                    id : 'statusSales',
                    label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
                    data : sourceStatus2,
                    index: 0
                }]
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
                    id : 'topProduct',
                    label : '${StringUtil.wrapString(uiLabelMap.BSTop)}',
                    data : sourceTop,
                    index: 0
                }]
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
        	 {
                action : 'addDropDownList',
                params : [{
                    id : 'storeChannel',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
                    data : channelData2,
                    index: 0
                }]
            },
            {
                action : 'addDropDownListMultil',
                params : [{
                    id : 'category',
                    label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
                    data : categoryData,
                    index: 0
                }]
            },
        ];

        var testGrid = OLBIUS.oLapGrid('topProductGrid', config, configPopup, 'evaluateTopProductSM', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'orig': oLap.val('organization'),
                'dateType': oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'topProduct': oLap.val('topProduct'),
                'orderStatus': oLap.val('orderStatus'),
                'storeChannel': oLap.val('storeChannel'),
                'category': oLap.val('category'),
                'statusSales': oLap.val('statusSales')
            }, oLap.val('dateType'));
        });

        testGrid.init(function () {
            testGrid.runAjax();
        });
    });
</script>

