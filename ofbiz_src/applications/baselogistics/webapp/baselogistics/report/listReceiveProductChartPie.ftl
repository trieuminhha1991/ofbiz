<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/logresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" id="receiveProductReportPieChart"> 
$(function () {
	var optionFilterDataType = [
	    {
		  text: '${uiLabelMap.StockIn}',
		  value: "RECEIVE"  
        }, 
        {
		  text: '${uiLabelMap.StockOut}',
		  value: "EXPORT"  
        }, 
	]
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
    var config = {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.LOGPieChartReceiveExportByProduct)}'
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
        {
            action : 'addDropDownList',
            params : [{
                id : 'filterTypeId',
                label : '${StringUtil.wrapString(uiLabelMap.KStatistic)}',
                data : optionFilterDataType,
                index: 0
            }],
        },
    ];
    var receiveProductReportPieChart = OLBIUS.oLapChart('receiveProductReportPieChart', config, configPopup, 'receiveProductWarehouseReportPieChart', true, true, OLBIUS.defaultPieFunc);

    receiveProductReportPieChart.funcUpdate(function(oLap) {
        oLap.update({
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'filterTypeId': oLap.val('filterTypeId')
        });
    });

    receiveProductReportPieChart.init(function () {
    	receiveProductReportPieChart.runAjax();
    });
});
</script>