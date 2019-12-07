<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	

	<#assign salesYear = delegator.findByAnd("CustomTimePeriod", Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", "SALES_YEAR"), null, false)!>
	var salesYear = [
	    <#list salesYear as salesYearL>
	    {
	    	customTimePeriodId : "${salesYearL.customTimePeriodId}",
	    	periodName: "${StringUtil.wrapString(salesYearL.get("periodName", locale))}"
	    },
	    </#list>	
	];
	
   	var salesYearData = [];
	<#if salesYear?exists>
		<#list salesYear as salesYearL >
			salesYearData.push({ 'value': '${salesYearL.periodName?if_exists}', 'text': '${StringUtil.wrapString(salesYearL.periodName)?if_exists}'});
		</#list>
	</#if>
	
	var salesMonthData = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSJanuary)}', 'value': '1'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSFebruary)}', 'value': '2'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMarch)}', 'value': '3'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSApril)}', 'value': '4'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMay)}', 'value': '5'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSJune)}', 'value': '6'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSJuly)}', 'value': '7'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSAugust)}', 'value': '8'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSeptember)}', 'value': '9'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSOctober)}', 'value': '10'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSNovember)}', 'value': '11'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSDecember)}', 'value': '12'},
	];
	
	var datee = new Date();
	var getMonth = datee.getMonth();
	var getYear = datee.getFullYear();
	var yearIndex = 0;
    
    for(var i = 0; i <= salesYearData.length; i++){
    	var yearValue = salesYearData[i].value;
    	if(getYear && yearValue && getYear == yearValue){
    		yearIndex = i;
    		break;
    	}
	}
    
    var sourceTop = [
		{'text': '10', 'value': '10'},
		{'text': '20', 'value': '20'},
		{'text': '30', 'value': '30'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMAll)}', 'value': '1000'},
    ];
</script>

<script type="text/javascript" id="TSColumnChart">
$(function(){
	Highcharts.setOptions({
	    lang: {
	        decimalPoint: ',',
	        thousandsSep: '.'
	    }
	});
	var config = {
		service: 'salesOrder',
		chart: {
			type: 'column'
		},
		title: {
			text: '${StringUtil.wrapString(uiLabelMap.BSMTSColumn)}',
			x: -20 //center
		},
		xAxis: {
			type: 'category',
			labels: {
				rotation: 0,
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
			valueDecimals: 2
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
    
    var configPopup = [
		{
			action : 'addDropDownList',
			params : [{
				id : 'yearr',
				label : '${StringUtil.wrapString(uiLabelMap.BSYear)}',
				data : salesYearData,
				index: yearIndex
			}]
		},
		{
			action : 'addDropDownList',
			params : [{
				id : 'monthh',
				label : '${StringUtil.wrapString(uiLabelMap.BSMonth)}',
				data : salesMonthData,
				index: getMonth,
			}]
		},
		{
			action : 'addDropDownList',
			params : [{
				id : 'topSalesman',
				label : '${StringUtil.wrapString(uiLabelMap.BSTop)}',
				data : sourceTop,
				index: 0
			}]
		},
    ];

    var columnChart = OLBIUS.oLapChart('TSColumnChart', config, configPopup, 'evaluateTopSalesmanChartv2', true, true, OLBIUS.defaultColumnFunc, 0.39);
	var isDistributor = "distributor_true";
	
    columnChart.funcUpdate(function (oLap) {
        oLap.update({
        	'yearr': oLap.val('yearr'),
            'monthh': oLap.val('monthh'),
            'topSalesman': oLap.val('topSalesman'),
            'position': isDistributor,
        });
    });

    columnChart.init(function () {
        columnChart.runAjax();
    });
});
</script>
