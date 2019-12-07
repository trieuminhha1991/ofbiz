<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var flagData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSCompany)}', 'value': 'COMPANY'}, {'text': '${StringUtil.wrapString(uiLabelMap.BSDistributor)}', 'value': 'DISTRIBUTOR'}];
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>


<script type="text/javascript" id="VSPieChart">
	$(function () {
		var config = {
				chart: {
					plotBackgroundColor: null,
					plotBorderWidth: null,
					plotShadow: false
				},
				title: {
					text: '${StringUtil.wrapString(uiLabelMap.BSGLChart)}'
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
		                		   value: OLBIUS.dateToString(past_date)
		                	   }],
		                	   before: 'thru_date'
		                   },
		                   {
		                       action : 'addDropDownList',
		                       params : [{
		                           id : 'flag',
		                           label : '${StringUtil.wrapString(uiLabelMap.BSFilterType)}',
		                           data : flagData2,
		                           index: 0
		                       }],
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
		var TSPC = OLBIUS.oLapChart('VSPieChart', config, configPopup, 'evaluateTSPieChart', true, true, OLBIUS.defaultPieFunc);
		
		TSPC.funcUpdate(function(oLap) {
			
			oLap.update({
				'fromDate': oLap.val('from_date'),
				'thruDate': oLap.val('thru_date'),
				'flag': oLap.val('flag'),
			});
		});
		
		TSPC.init(function () {
			TSPC.runAjax();
		});
	});
</script>