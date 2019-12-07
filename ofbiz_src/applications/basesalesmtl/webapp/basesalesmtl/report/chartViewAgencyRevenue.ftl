
<div class="chartC">
	<script type="text/javascript" id="PCAreaChart">
		$(function(){
			var config = {
	            chart: {
	                type: 'column'
	            },
	            title: {
	                text: '',
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
	                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
	            },
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
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'thru_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                    value: OLBIUS.dateToString(cur_date)
	                }],
	                after: 'from_date'
	            },
	        ];
	
	        var gChart1C = OLBIUS.oLapChart('PCAreaChart', config, configPopup, 'evaluateAgencyChart', true, true, OLBIUS.defaultColumnFunc);
			var as = '${parameters.partyId}';
			var series1 = "series1";
	        gChart1C.funcUpdate(function (oLap) {
	            oLap.update({
	            	'fromDate': oLap.val('from_date'),
	                'thruDate': oLap.val('thru_date'),
	                'agencyId': as,
	                'series': series1,
	            });
	        });
	
	        gChart1C.init(function () {
	        	gChart1C.runAjax();
	        });
		});
	</script>
</div> 
<div class="chartC">
	<script type="text/javascript" id="PCAreaChart">
		$(function(){
			var config = {
	            chart: {
	                type: 'line'
	            },
	            title: {
	                text: '',
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
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'thru_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                    value: OLBIUS.dateToString(cur_date)
	                }],
	                after: 'from_date'
	            },
	        ];
	
	        var gChart1C = OLBIUS.oLapChart('PCAreaChart', config, configPopup, 'evaluateAgencyChart', true, true, OLBIUS.defaultLineFunc);
			var as = '${parameters.partyId}';
			var series1 = "series2";
	        gChart1C.funcUpdate(function (oLap) {
	            oLap.update({
	            	'fromDate': oLap.val('from_date'),
	                'thruDate': oLap.val('thru_date'),
	                'agencyId': as,
	                'series': series1,
	            });
	        });
	
	        gChart1C.init(function () {
	        	gChart1C.runAjax();
	        });
		});
	</script>
</div> 
