<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
	
	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!>
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
   	
   	var channelData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if salesChannel?exists>
		<#list salesChannel as salesChannelL >
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.description)?if_exists}'});
		</#list>
	</#if>

	var sortByData2 = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSNo)}', 'value': null},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', 'value': 'chaIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductId)}', 'value': 'proIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductName)}', 'value': 'proNameSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', 'value': 'quaSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSTotal)}', 'value': 'totSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', 'value': 'cateSort'}
	];
	
	<#assign statusItem = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "ORDER_STATUS"), null, false)!>
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
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
	
	var gFromDate; var gThruDate; var gOrderStatus; var gCategory; var gStoreChannel; var gCustomTime; var gColumnChart; var gPieChart;
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

<script id="test">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSRevenueProductChannel)}',
            columns: [
        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  datafield: 'stt', columntype: 'number', width: '3%',
		    	  cellsrenderer: function (row, column, value) {
		    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    	  		}
		 		},   
                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channel', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', datafield: 'category', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '12%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '31%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantityNUnit)}', datafield: 'Quantity', type: 'number', width: '12%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'Total', type: 'number', width: '12%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
            ],
            sortable: true,
            showfilterrow: true,
            filterable: true,
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
		            index: 1,
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
	                id : 'orderStatus',
	                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
	                data : statusItemData,
	                index: 5
	            }]
        	},
        ];


        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesOlapGridByChannel', true);

        testGrid.funcUpdate(function (oLap) {
        	gFromDate = oLap.val('from_date_1'),
            gThruDate = oLap.val('thru_date_1'),
            gOrderStatus = oLap.val('orderStatus'),
            gCategory = oLap.val('category'),
            gStoreChannel = oLap.val('storeChannel'),
            gCustomTime = oLap.val('customTime'),
        	
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'orderStatus': gOrderStatus,
                'category': gCategory,
                'storeChannel': gStoreChannel,
                'customTime': gCustomTime,
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
            gColumnChart.runAjax();
            gPieChart.runAjax();
        }, false, function(oLap){
        	var dataAll = oLap.getAllData();
        	if(dataAll.length != 0){
            	var fromDateInput = oLap.val('from_date_1');
            	var thruDateInput = oLap.val('thru_date_1');
            	var dateFromDate = new Date(fromDateInput);
            	var dateThruDate = new Date(thruDateInput);
            	var dateFrom = dateFromDate.getTime();
            	var thruFrom = dateThruDate.getTime();
            	var sortIdInput = oLap.val('sortId');
            	var orderStatus = oLap.val('orderStatus');
            	var channelInput = oLap.val('storeChannel');
            	var categoryInput = oLap.val('category');
            	
            	window.location.href = "exportTurnoverProChaReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&channel=" + channelInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
        	}else{
        		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
        		    "class" : "btn-small btn-primary width60px",
        		    }]
        		   );
        	}
    	});
    });
</script>
<script type="text/javascript" id="PCColumnChart">
	$(function(){
		var config = {
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPPSColumn)}',
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
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
            },
        };

        gColumnChart = OLBIUS.oLapChart('PCColumnChart', config, null, 'evaluateSalesPCColumnChart', true, true, OLBIUS.defaultColumnFunc);

        gColumnChart.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'orderStatus': gOrderStatus,
                'category': gCategory,
                'storeChannel': gStoreChannel,
                'customTime': gCustomTime,
            });
        });

        gColumnChart.init(function () {
            gColumnChart.runAjax();
        });
	});
</script>
<script type="text/javascript" id="PCAreaChart">
	$(function(){
		var config = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPPSPIeChartQuantity)}'
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{point.y}</span>: <b>{point.percentage:.1f}%</b>'
            },
            series: [{
                type: 'pie'
            }],
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false,
                        format: '<b>{point.name}({point.y})</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    },
                    showInLegend: true
                }
            }
        };

        gPieChart = OLBIUS.oLapChart('PCAreaChart', config, null, 'evaluateTurnoverPCPC', true, true, OLBIUS.defaultPieFunc);
 
        gPieChart.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'orderStatus': gOrderStatus,
                'category': gCategory,
                'storeChannel': gStoreChannel,
                'customTime': gCustomTime,
            });
        });

        gPieChart.init(function () {
            gPieChart.runAjax();
        });
	});
</script>
