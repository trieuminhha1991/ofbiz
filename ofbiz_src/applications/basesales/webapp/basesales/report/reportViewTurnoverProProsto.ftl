<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
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
	
    var gFromDate; var gThruDate; var gStore; var gCategory; var gStatus; var gCustomTime; var gColumnVolume; var gColumnValue;
    var gPieVolume; var gPieValue;
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>
<script id="test">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSRevenueProductProductStore)}',
            service: 'salesOrder',
            columns: [
            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  datafield: 'stt', columntype: 'number', width: '3%',
		    	  cellsrenderer: function (row, column, value) {
		    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  }
			 	},   
                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'productStoreName', type: 'string', width: '15%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'category', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSPercent)}', datafield: 'percent', type: 'string', width: '10%', hidden: true},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '29%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', filterable:false,},
                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '13%', cellsformat: 'f0', cellsalign: 'right', filterable: false,}
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
                    id : 'productStore',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
                    data : productStoreData2,
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

        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesOlapGrid', true);

        testGrid.funcUpdate(function (oLap) {
        	gFromDate = oLap.val('from_date_1'); 
        	gThruDate = oLap.val('thru_date_1'); 
        	gStore = oLap.val('productStore'); 
        	gCategory = oLap.val('category'); 
        	gStatus = oLap.val('orderStatus'); 
        	gCustomTime = oLap.val('customTime');
        	
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'productStore': gStore,
                'category': gCategory,
                'orderStatus': gStatus,
                'customTime': gCustomTime,
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
//            gColumnVolume.runAjax();
            gColumnValue.runAjax();
//            gPieVolume.runAjax();
            gPieValue.runAjax();
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
            	var orderStatus =  oLap.val('orderStatus');
            	var productStoreInput = oLap.val('productStore');
            	var categoryInput = oLap.val('category');
            	
            	window.location.href = "exportTurnoverProProStoReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&productStore=" + productStoreInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
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
<#--
<script type="text/javascript" id="PPSColumnChart">
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
                    color: '#plotLines'
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
            }
        };

        gColumnVolume = OLBIUS.oLapChart('PPSColumnChart', config, null, 'evaluateSalesPPSColumnChart', true, true, function(data, chart, datetype, removeSeries, flagFunc, olap){
        	var tmp = {
                labels: {
                    enabled: false
                },
                categories: data.xAxis
            };

            chart.xAxis[0].update(tmp, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }
            var color = 6;
            for (var i in data.yAxis) {
                chart.addSeries({
                    name: i,
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++],
                }, false);
            }

            chart.redraw();

            if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
                flagFunc();
            }
		});

        gColumnVolume.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'productStore': gStore,
                'category': gCategory,
                'orderStatus': gStatus,
                'customTime': gCustomTime,
            });
        });

        gColumnVolume.init(function () {
            gColumnVolume.runAjax();
        });
	});
</script>
-->
<script type="text/javascript" id="PPSAreaChart">
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
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
            }
        };

        gColumnValue = OLBIUS.oLapChart('PPSAreaChart', config, null, 'evaluateSalesPPSAreaChart', true, true, function(data, chart, datetype, removeSeries, flagFunc, olap){
        	var tmp = {
                labels: {
                    enabled: false
                },
                categories: data.xAxis
            };

            chart.xAxis[0].update(tmp, false);

            if (removeSeries) {
                while (chart.series.length > 0) {
                    chart.series[0].remove(false);
                }
            }
            var color = 8;
            for (var i in data.yAxis) {
                chart.addSeries({
                    name: i,
                    data: data.yAxis[i],
                    color: Highcharts.getOptions().colors[color++],
                }, false);
            }

            chart.redraw();

            if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
                flagFunc();
            }
		});

        gColumnValue.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'productStore': gStore,
                'category': gCategory,
                'orderStatus': gStatus,
                'customTime': gCustomTime,
            });
        });

        gColumnValue.init(function () {
            gColumnValue.runAjax();
        });
	});
</script>
<#--
<script type="text/javascript" id="PPSPieChart">

    $(function () {
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
        gPieVolume = OLBIUS.oLapChart('PPSPieChart', config, null, 'evaluateTurnoverPPSPieChart', true, true, OLBIUS.defaultPieFunc);

        gPieVolume.funcUpdate(function(oLap) {
			
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'productStore': gStore,
                'category': gCategory,
                'orderStatus': gStatus,
                'customTime': gCustomTime,
            });
        });

        gPieVolume.init(function () {
            gPieVolume.runAjax();
        });
    });

</script>
-->
<script type="text/javascript" id="PPSPieChartTotal">

    $(function () {
        var config = {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPPSPIeChartTotal)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y} VND</b>'
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
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    },
                    showInLegend: true
                }
            }
        };

        gPieValue = OLBIUS.oLapChart('PPSPieChartTotal', config, null, 'evaluateTurnoverPPSPieChart2', true, true, OLBIUS.defaultPieFunc);

        gPieValue.funcUpdate(function(oLap) {
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'productStore': gStore,
                'category': gCategory,
                'orderStatus': gStatus,
                'customTime': gCustomTime,
            });
        });

        gPieValue.init(function () {
            gPieValue.runAjax();
        });
    });

</script>
