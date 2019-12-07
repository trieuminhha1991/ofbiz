<script type="text/javascript" id="returnChart">
    $(function () {
    	var config = {
    			service: 'salesOrderPOS',
                chart: {
                    type: 'column'
                },
                title: {
                    text: '${StringUtil.wrapString(uiLabelMap.ChartReturnOrder)}',
                    x: -20 //center
                },
                xAxis: {
                    type: 'category',
                    labels: {
                        rotation: -45,
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
                        color: '#808080'
                    }],
                    title : {
                        text: null
                    },
                    min: 0
                },
                legend: {
                    enabled: false
                },
                tooltip: {
                    pointFormat: '{point.y}'
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
	            before: 'thru_date'
	        },
	        {
	            action : 'addDateTimeInput',
	            params : [{
	                id : 'thru_date_1',
	                label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                value: OLBIUS.dateToString(cur_date),
	                hide: true,
	            }],
	            after: 'from_date'
	        },
	        {
		        action : 'addDropDownList',
		        params : [{
		            id : 'customTime',
		            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
		            data : customDate,
		            index: 4,
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
			        id : 'facilityId',
			        label : '${StringUtil.wrapString(uiLabelMap.BPOSFacility)}',
			        data : facilityData,
			        index: 0
			    }]
			},
			{
			    action : 'addDropDownList',
			    params : [{
			        id : 'partyId',
			        label : '${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}',
			        data : customerData,
			        index: 0
			    }]
			},
            {
                action : 'addDropDownList',
                params : [{
                    id : 'limit',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_limit)}',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_unlimit)}', value: '0'}, '5', '10', '15', '20'],
                    index: 3
                }]
            }
		];
    	
    	var returnOLap = OLBIUS.oLapChart('returnChart', config, configPopup, 'returnChartOlapVer2', true, true, OLBIUS.defaultColumnFunc);

    	returnOLap.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'facilityId': oLap.val('facilityId'),
                'partyId': oLap.val('partyId'),
                'limit': oLap.val('limit'),
                'customTime': oLap.val('customTime'),
            });
        });
    	returnOLap.init(function () {
    		returnOLap.runAjax();
        });
    });
    
</script>