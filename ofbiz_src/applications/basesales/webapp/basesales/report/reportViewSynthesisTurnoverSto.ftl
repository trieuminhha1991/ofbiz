<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

<script type="text/javascript">
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

	var filterData = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', 'value': 'channel'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', 'value': 'productstore'},
	];
	
	var filterData2 = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesVolume)}', 'value': 'salesvolume'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesValue)}', 'value': 'salesvalue'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSOrderVolume)}', 'value': 'ordervolume'},
	];
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
	
</script>

<script id="synthesisByStore">
$(function(){
	var dateCurrent = new Date();
	var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
	var column = [];
	$.ajax({url: 'getStoreListColumn2',
	    type: 'post',
	    async: false,
	    success: function(data) {
	    	var listDatafield = data.listResultStore;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var field = {text: (listDatafield[i].store_name ? listDatafield[i].store_name : ""), datafield:listDatafield[i].product_store_id, type: 'string', width: '9%', cellsalign: 'right', cellsformat: 'n2'};
	    		column.push(field);
	    	}
	    },
	    error: function(data) {
	    	alert('Error !!');
	    }
	});
	column.push({ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		datafield: 'stt', columntype: 'number', width: '3%',
		  	cellsrenderer: function (row, column, value) {
			  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		}
 	});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}", datafield:'productId', cellsalign: 'left', type: 'string', width: '15%', pinned : true});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}", datafield:'productName', cellsalign: 'left', type: 'string', width: '35%', pinned : true });
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSStatus)}", datafield:'status', cellsalign: 'left', type: 'string', width: '15%', pinned : true, hidden: true });
	
	var config = {
    		title: '${StringUtil.wrapString(uiLabelMap.BSMSynthesisSalesChannelReport)}',
    		service: 'salesOrder',
            showstatusbar: true,
            statusbarheight: 50,
           	showaggregates: true,
            columns: column
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
                id : 'orderStatus',
                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
                data : statusItemData,
                index: 5
            }]
        },
    ];


        var testGrid = OLBIUS.oLapGrid('synthesisByStore', config, configPopup, 'evaluateSalesSynthesisReport', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'customTime': oLap.val('customTime'),
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
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
            	
            	window.location.href = "exportSynthesisReportToExcel?&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&orderStatus=" + orderStatus;
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

<script type="text/javascript" id="TurnoverSynthesisPieChart">
    $(function () {
		var dateCurrent = new Date();
		var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
        var config = {
        	service: 'salesOrder',
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSSynthesisPieChart)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>'
            },
            series: [{
                type: 'pie'
            }],
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
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
	                    popup.clear('from_date_1');
	                    popup.clear('thru_date_1');
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
		            id : 'filter1',
		            label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
		            data : filterData,
		            index: 0
		        }]
		    },
		    {
		        action : 'addDropDownList',
		        params : [{
		            id : 'filter2',
		            label : '${StringUtil.wrapString(uiLabelMap.olap_filter)}',
		            data : filterData2,
		            index: 0
		        }]
		    },
            
        ];
        var TSPC = OLBIUS.oLapChart('TurnoverSynthesisPieChart', config, configPopup, 'evaluateTurnoverSynthesisPieChart', true, true, OLBIUS.defaultPieFunc);

        TSPC.funcUpdate(function(oLap) {
            oLap.update({
            	'customTime': oLap.val('customTime'),
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'filter1': oLap.val('filter1'),
                'filter2': oLap.val('filter2'),
            });
        });

        TSPC.init(function () {
            TSPC.runAjax();
        });
    });

</script>