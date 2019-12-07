<style>
	.olbiusChartContainer, .olbiusGridContainer{
		margin-bottom: 50px!important;
	}
</style>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
</script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
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
			<#if categoryL.productCategoryId != "BROWSE_ROOT">
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
			</#if>
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
    
     var gFromDateC; var gThruDateC; var gOrderStatusC; var gCategoryC; var gChannelC; var gGridC; var gChart1C; var gCustomTime;
     
     var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);		
</script>

<div class="grid">
	<script id="test">
		$(function(){
	        var config = {
				sortable: true,
		    	filterable: true,
		    	showfilterrow: true,
	            title: '${StringUtil.wrapString(uiLabelMap.BSReportTurnoverProChaK)}',
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
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '12%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '31%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'Quantity', type: 'number', width: '12%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', datafield: 'unit', type: 'string', width: '10%', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'Total', type: 'number', width: '12%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
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
	
	
	        gGridC = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesOlapGridByChannel', true);
	
	        gGridC.funcUpdate(function (oLap) {
	        	gFromDateC = oLap.val('from_date_1');
	        	gThruDateC = oLap.val('thru_date_1');
	        	gOrderStatusC = oLap.val('orderStatus');
	        	gCategoryC = oLap.val('category');
	        	gChannelC = oLap.val('storeChannel');
	        	gCustomTimeC = oLap.val('customTime');
	        	
	            oLap.update({
	                'fromDate': oLap.val('from_date_1'),
	                'thruDate': oLap.val('thru_date_1'),
	                'orderStatus': oLap.val('orderStatus'),
	                'category': oLap.val('category'),
	                'storeChannel': oLap.val('storeChannel'),
	                'customTime': oLap.val('customTime'),
	            });
	        });
	
	        gGridC.init(function () {
	        	gGridC.runAjax();
	        	gChart1C.runAjax();
	        	gChart2C.runAjax();
	        }, false, function(oLap){
	        	var dataAll = oLap.getAllData();
	        	if(dataAll.length != 0){
	            	var fromDateInput = oLap.val('from_date');
	            	var thruDateInput = oLap.val('thru_date');
	            	var dateFromDate = new Date(fromDateInput);
	            	var dateThruDate = new Date(thruDateInput);
	            	var dateFrom = dateFromDate.getTime();
	            	var thruFrom = dateThruDate.getTime();
	            	var orderStatus = oLap.val('orderStatus');
	            	var channelInput = oLap.val('storeChannel');
	            	var categoryInput = oLap.val('category');
	            	
	            	window.location.href = "exportTurnoverProChaReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&channel=" + channelInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
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
</div>
<div class="chartC">
	<script type="text/javascript" id="PCAreaChart">
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
	            plotOptions: {
	                column: {
	                    stacking: 'normal'
	                }
	            },
	            legend: {
	                enabled: true
	            },
	            tooltip: {
	                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
	            },
	        };
	
	        gChart1C = OLBIUS.oLapChart('PCAreaChart', config, null, 'evaluateSalesPCAreaChart', true, true, OLBIUS.defaultColumnFunc);
	
	        gChart1C.funcUpdate(function (oLap) {
	            oLap.update({
	            	'fromDate': gFromDateC,
	                'thruDate': gThruDateC,
	                'orderStatus': gOrderStatusC,
	                'category': gCategoryC,
	                'storeChannel': gChannelC,
	                'customTime': gCustomTimeC,
	            });
	        });
	
	        gChart1C.init(function () {
	        	gChart1C.runAjax();
	        });
		});
	</script>
	
	<script type="text/javascript" id="PCPieChartTotal">
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
	                pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>',
	                valueSuffix: ' VND'
	            },
	            series: [{
	                type: 'pie'
	            }],
	            legend: {
	                layout: 'vertical',
	                align: 'right',
	                verticalAlign: 'middle',
	                borderWidth: 1
	            },
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
	
	        gChart2C = OLBIUS.oLapChart('PCPieChartTotal', config, null, 'evaluateTurnoverPCPC', true, true, OLBIUS.defaultPieFunc);
	
	        gChart2C.funcUpdate(function(oLap) {
	            oLap.update({
	            	'fromDate': gFromDateC,
	                'thruDate': gThruDateC,
	                'orderStatus': gOrderStatusC,
	                'category': gCategoryC,
	                'storeChannel': gChannelC,
	                'customTime': gCustomTimeC,
	            });
	        });
	
	        gChart2C.init(function () {
	        	gChart2C.runAjax();
	        });
	    });
	</script>
</div>