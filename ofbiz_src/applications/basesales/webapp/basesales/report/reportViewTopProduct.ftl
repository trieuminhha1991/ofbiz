<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
</script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
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
var sourceStatus = [
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
		<#if categoryL.productCategoryId != "BROWSE_ROOT">
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
		</#if>
		</#list>
	</#if>
	
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
	
	var filterTypeData = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProduct)}', 'value': 'product_'},
		{'text': '${StringUtil.wrapString(uiLabelMap.Employee)}', 'value': 'staff_'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProvince)}', 'value': 'state_'}
	];
	
    var gFromDate; var gThruDate; var gTopProduct; var gOrderStatus; var gStoreChannel; var gCategory; var gStatusSales; var gGrid; var gChart;
	var gFilterType; var gCustomTime;
	
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

<script id="topProductGrid">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSTopProduct)}',
            service: 'salesOrder',
            columns: [
	            { text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
	    	  		datafield: 'stt', columntype: 'number', width: '3%',
		    	  	cellsrenderer: function (row, column, value) {
		    		  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	 	}
			 	},   
                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channel', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', datafield: 'category', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '10%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '35%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSGeoCode)}', datafield: 'geo_code', type: 'string', width: '20%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSGeoName)}', datafield: 'geo_name', type: 'string', width: '40%'},
                { text: '${StringUtil.wrapString(uiLabelMap.Employee)}', datafield: 'staff_code', type: 'string', width: '20%'},
                { text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'staff_name', type: 'string', width: '40%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '12%', cellsformat: 'n2', cellsalign: 'right'},
            ],
            sortable: true,
	    	filterable: true,
	    	showfilterrow: true,
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
                action : 'addDropDownList',
                params : [{
                    id : 'statusSales',
                    label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
                    data : sourceStatus,
                    index: 0
                }]
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
            {
                action : 'addDropDownList',
                params : [{
                    id : 'filterType',
                    label : '${StringUtil.wrapString(uiLabelMap.BSReportOptionK)}',
                    data : filterTypeData,
                    index: 0
                }]
            },
        ];

        gGrid = OLBIUS.oLapGrid('topProductGrid', config, configPopup, 'evaluateSalesTopProductOlapGrid', true);

        gGrid.funcUpdate(function (oLap) {
        	gFromDate = oLap.val('from_date_1');
        	gThruDate = oLap.val('thru_date_1');
        	gTopProduct = oLap.val('topProduct');
        	gOrderStatus = oLap.val('orderStatus');
        	gStoreChannel = oLap.val('storeChannel');
        	gCategory = oLap.val('category');
        	gStatusSales = oLap.val('statusSales');
        	gFilterType = oLap.val("filterType");
        	gCustomTime = oLap.val("customTime");
        	
            oLap.update({
				'fromDate': oLap.val('from_date_1'),
				'thruDate': oLap.val('thru_date_1'),
				'topProduct': oLap.val('topProduct'),
				'orderStatus': oLap.val('orderStatus'),
				'storeChannel': oLap.val('storeChannel'),
				'category': oLap.val('category'),
				'statusSales': oLap.val('statusSales'),
				'filterType': oLap.val('filterType'),
				'customTime': oLap.val('customTime'),
            });
        });

        gGrid.init(function () {
        	gGrid.runAjax();
        	gChart.runAjax();
        });
    });
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
                enabled: true
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

        gChart = OLBIUS.oLapChart('TPColumnChart', config, null, 'topProductColumn', true, true, OLBIUS.defaultColumnFunc);

        gChart.funcUpdate(function (oLap) {
            oLap.update({
            	'fromDate': gFromDate,
                'thruDate': gThruDate,
                'topProduct': gTopProduct,
                'orderStatus': gOrderStatus,
                'storeChannel': gStoreChannel,
                'category': gCategory,
                'statusSales': gStatusSales,
                'filterType': gFilterType,
                'customTime': gCustomTime,
            });
        });

        gChart.init(function () {
        	gChart.runAjax();
        });
	});
</script>