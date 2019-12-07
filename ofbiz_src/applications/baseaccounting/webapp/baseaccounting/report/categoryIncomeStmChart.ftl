<script type="text/javascript" id="CategoryIncomePieChart">
    $(function () {
        var config = {
    		service: 'acctgTransTotal',
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BACCCategoryIncomePieChart)}'
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
		var dateCurrent = new Date();
		var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	        
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
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
            },
        ];
        CIPC = OLBIUS.oLapChart('CategoryIncomePieChart', config, configPopup, 'evaluateCatIncomePieChart', true, true, OLBIUS.defaultPieFunc);
        CIPC.funcUpdate(function(oLap) {
            oLap.update(configDataSync);
        });
        CIPC.init(function () {
        	CIPC.runAjax();
        });
    });
</script>