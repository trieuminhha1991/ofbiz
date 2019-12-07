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
<script type="text/javascript" id="TPColumnChart">
	$(function(){
		var config = {
			service: 'salesOrder',
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTPColumn)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -30,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                },
                title : {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#000000'
                }],
                title : {
                    text: null
                },
                min: 0
            },
            legend: {
                enabled: false
            },
            tooltip: {
                pointFormat: '{point.y}'
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true
                    }
                }
            }
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

        var columnChart = OLBIUS.oLapChart('TPColumnChart', config, configPopup, 'topProductColumn', true, true, OLBIUS.defaultColumnFunc, 0.52);

        columnChart.funcUpdate(function (oLap) {
            oLap.update({
           	'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'topProduct': oLap.val('topProduct'),
                'orderStatus': oLap.val('orderStatus'),
                'storeChannel': oLap.val('storeChannel'),
                'category': oLap.val('category'),
                'statusSales': oLap.val('statusSales')
            });
        });

        columnChart.init(function () {
            columnChart.runAjax();
        });
	});
</script>