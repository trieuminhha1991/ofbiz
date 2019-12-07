<script type="text/javascript">
var sourceStatus = [
        			{	text: "${StringUtil.wrapString(uiLabelMap.BSSellingOK)}",
             			value: "Y"},
        			{	text: "${StringUtil.wrapString(uiLabelMap.BSSellingNOK)}",
             			value: "N"}
        ];
</script>

<script type="text/javascript" id="TSColumnChart">
$(function(){
	var dateCurrent = new Date();
	var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
	var config = {
        chart: {
            type: 'column'
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.BSTSColumn)}',
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
        plotOptions: {
            column: {
                stacking: 'normal',
                dataLabels: {
                	rotation: -90,
                    enabled: false,
                    y: 0,
                    color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'yellow',
                    style: {
                    	fontSize: '14px',
                        textShadow: '0 0 3px white'
                    }
                }
            }
        },
    };

    var configPopup = [
		{
		    action : 'addDateTimeInput',
		    params : [{
		        id : 'from_date',
		        label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
		        value: OLBIUS.dateToString(currentQueryDay)
		    }],
		    before: 'thru_date'
		},
		{
            action : 'addDropDownList',
            params : [{
                id : 'status',
                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
                data : sourceStatus,
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
    ];

    var columnChart = OLBIUS.oLapChart('TSColumnChart', config, configPopup, 'evaluateSaexChart', true, true, OLBIUS.defaultColumnFunc, 0.65);

    columnChart.funcUpdate(function (oLap) {
        oLap.update({
        	'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'status': oLap.val('status'),
        });
    });

    columnChart.init(function () {
        columnChart.runAjax();
    });
});
</script>