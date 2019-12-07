<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var flagData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSCompany)}', 'value': 'COMPANY'}, {'text': '${StringUtil.wrapString(uiLabelMap.BSDistributor)}', 'value': 'DISTRIBUTOR'}];
	var TSPC; var gFromDate; var gThruDate; var gFlag; var gCustomTime;
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>

<div class="grid" style="margin-bottom: 50px;">
<script id="test">
	$(function(){
        var config = {
    		sortable: false,
        	filterable: false,
        	showfilterrow: false,
            title: '${StringUtil.wrapString(uiLabelMap.BSRevenueStateGrid)}',
            service: 'salesOrder',
            columns: [
                { text: '${StringUtil.wrapString(uiLabelMap.BSStateProvince)}', datafield: 'state', type: 'string', width: '40%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSMTurnover)}', datafield: 'value1', type: 'string',
                	cellsrenderer: function (row, column, value) {
				        return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
				    }
                },
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
    	           action : 'addDropDownList',
    	           params : [{
    	               id : 'flag',
    	               label : '${StringUtil.wrapString(uiLabelMap.BSClassification)}',
    	               data : flagData2,
    	               index: 0
    	           }],
    	  		},
       ];

        gGridC = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateTSPieGrid', true);

        gGridC.funcUpdate(function (oLap) {
        	gFromDate = oLap.val('from_date_1');
        	gThruDate = oLap.val('thru_date_1');
        	gFlag = oLap.val('flag');
        	gCustomTime = oLap.val('customTime');
            oLap.update({
                'fromDate': gFromDate,
                'thruDate': gThruDate,
                'flag': gFlag,
                'customTime': gCustomTime,
            });
        });

        gGridC.init(function () {
        	gGridC.runAjax();
        	TSPC.runAjax();
        }, false, null);
    });
</script>

</div>

<script type="text/javascript" id="VSPieChart">
	$(function () {
		var config = {
				chart: {
					plotBackgroundColor: null,
					plotBorderWidth: null,
					plotShadow: false
				},
				title: {
					text: '${StringUtil.wrapString(uiLabelMap.BSRevenueStateChart)}'
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
		
		TSPC = OLBIUS.oLapChart('VSPieChart', config, null, 'evaluateTSPieChart', true, true, OLBIUS.defaultPieFunc);
		
		TSPC.funcUpdate(function(oLap) {
			
			oLap.update({
				'fromDate': gFromDate,
				'thruDate': gThruDate,
				'flag': gFlag,
				'customTime': gCustomTime,
			});
		});
		
		TSPC.init(function () {
			TSPC.runAjax();
		});
	});
</script>