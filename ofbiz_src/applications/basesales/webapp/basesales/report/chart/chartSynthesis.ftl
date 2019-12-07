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
	
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
</script>
<script type="text/javascript" id="TurnoverSynthesisPieChart">
    $(function () {
        var config = {
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
                        enabled: false,
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
        var TSPC = OLBIUS.oLapChart('TurnoverSynthesisPieChart', config, configPopup, 'evaluateTurnoverSynthesisPieChart', true, true, OLBIUS.defaultPieFunc, 0.75);

        TSPC.funcUpdate(function(oLap) {
			
            oLap.update({
					'fromDate': oLap.val('from_date_1'),
					'thruDate': oLap.val('thru_date_1'),
					'filter1': oLap.val('filter1'),
					'filter2': oLap.val('filter2'),
					'customTime': oLap.val('customTime'),
            });
        });

        TSPC.init(function () {
            TSPC.runAjax();
        });
    });

</script>