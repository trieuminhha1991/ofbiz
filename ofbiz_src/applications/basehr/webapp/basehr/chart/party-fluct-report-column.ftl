<script type="text/javascript" id="partyFluctOlap">
	$(function(){
		var config = {
				chart: {
	                type: 'column'
	            },
	            title: {
	                text: '${StringUtil.wrapString(uiLabelMap.party_fluct_title)}',
	                x: -20 //center
	            },
	            xAxis: {
	                labels: {
	                    enabled: true
	                },
	                tickInterval : 3
	            },
	            yAxis: {
	                plotLines: [{
	                    value: 0,
	                    width: 1,
	                    color: '#808080'
	                }],
	                title: {
	                    text: null
	                }
	            },
	            legend: {
	                enabled: true
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
		
		var configPopup = {
	            'jqxTree': {
	                action: 'addJQXTree',
	                params: [{
	                    id: 'jqxTree',
	                    label: '${StringUtil.wrapString(uiLabelMap.party_tree_title)}'
	                }]
	            },
	            'dateTypeId' : {
	                action : 'addDropDownList',
	                params : [{
	                    id : 'dateType',
	                    label : '${StringUtil.wrapString(uiLabelMap.party_dateType)}',
	                    data : date_type_source,
	                    index: 2
	                }]
	            },
	            'fromDateId' : {
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'from_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
	                    value: OLBIUS.dateToString(past_date)
	                }],
	                event: function(popup) {
	                    popup.onEvent('from_date', 'valueChanged', function(event){
	                        var fromDate = event.args.date;
	                        var thruDate = popup.getDate('thru_date');
	                        if(thruDate < fromDate) {
	                            popup.val('thru_date', fromDate);
	                        }
	                    });
	                }
	            },
	            'thruDateId' : {
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'thru_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                    value: OLBIUS.dateToString(cur_date)
	                }],
	                event: function(popup) {
	                    popup.onEvent('thru_date', 'valueChanged', function(event){
	                        var thruDate = event.args.date;
	                        var fromDate = popup.getDate('from_date');
	                        if(thruDate < fromDate) {
	                            popup.val('from_date', thruDate);
	                        }
	                    });
	                }
	            }
	        };
		var partyOlap = OLBIUS.oLapChart('partyFluctOlap', config, configPopup, 'partyFluct', true, true, OLBIUS.defaultLineFunc);
		partyOlap.funcUpdate(function(oLap){
			var group = oLap.val('jqxTree');
			oLap.update({
				'group': group,
                'dateType': oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date')
			}, oLap.val('dateType'))
		});
		partyOlap.init(function(){
			partyOlap.runAjax();
		});
	})
</script>