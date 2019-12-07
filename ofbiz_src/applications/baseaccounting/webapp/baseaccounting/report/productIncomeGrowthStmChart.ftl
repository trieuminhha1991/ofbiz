<script type="text/javascript" id="PIGColumnChart">
	$(function(){
		var config = {
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BACCProductIncomeGrowthColumnChart)}',
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
            },
            legend: {
                enabled: true
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}%</b><br/>',
            }
        };
		
		var dateTypeCheck = "DAY";
		var customTimePeriodYear = null;
		var dateCurrent = new Date();
		var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);			
        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.CommonPeriod)}',
                    data : date_type_source,
                    index: 0
                }],
                event : function(popup) {
                    popup.onEvent('dateType', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('dateType', args.index);
                        var filter = item.value;
                        dateTypeCheck = filter;
                        if(filter != "DAY"){
                        	popup.hide('from_date1');
                        	popup.hide('thru_date1');
                        	popup.hide('from_date2');
                        	popup.hide('thru_date2');
                        	popup.show('customTimePeriodId1');
                        	popup.show('customTimePeriodId2');
                        }
                        if(filter == "DAY"){
                        	popup.show('from_date1');
                        	popup.show('thru_date1');
                        	popup.show('from_date2');
                        	popup.show('thru_date2');
                        	popup.hide('customTimePeriodId1');
                        	popup.hide('dateTypePeriodMonth1');
                        	popup.hide('dateTypePeriodQuater1');
                        	popup.hide('customTimePeriodId2');
                        	popup.hide('dateTypePeriodMonth2');
                        	popup.hide('dateTypePeriodQuater2');
                        }
                        if(filter == "YEAR"){
                        	popup.hide('dateTypePeriodMonth1');
                        	popup.hide('dateTypePeriodQuater1');
                        	popup.hide('dateTypePeriodMonth2');
                        	popup.hide('dateTypePeriodQuater2');
                        }
                        if(filter == "MONTH"){
                    		popup.show('dateTypePeriodMonth1');
                    		popup.hide('dateTypePeriodQuater1');
                    		popup.show('dateTypePeriodMonth2');
                    		popup.hide('dateTypePeriodQuater2');
                    	}
                    	if(filter == "QUARTER"){
                    		popup.show('dateTypePeriodQuater1');
                    		popup.hide('dateTypePeriodMonth1');
                    		popup.show('dateTypePeriodQuater2');
                    		popup.hide('dateTypePeriodMonth2');
                    	}
                    	if(filter == "WEEK"){
                    		popup.show('from_date1');
                        	popup.show('thru_date1');
                        	popup.show('from_date2');
                        	popup.show('thru_date2');
                        	popup.hide('customTimePeriodId1');
                        	popup.hide('dateTypePeriodMonth1');
                        	popup.hide('dateTypePeriodQuater1');
                        	popup.hide('customTimePeriodId2');
                        	popup.hide('dateTypePeriodMonth2');
                        	popup.hide('dateTypePeriodQuater2');
                    	}
	                    popup.resize();
                    });
                }
            },
            {
                action : 'addFilterDropDownList',
                params : [{
                    id : 'productId',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCProduct)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: undefined}].concat(listProductDataSource),
                    index: 0,
                }]
            },
            {
			    action : 'addDateTimeInput',
				params : [{
				    id : 'from_date1',
				    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate1)}',
				    value: OLBIUS.dateToString(currentFirstDay)
				}],
				before: 'thru_date1'
			},
			{
			    action : 'addDateTimeInput',
				params : [{
				    id : 'thru_date1',
				    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate1)}',
				    value: OLBIUS.dateToString(cur_date)
				}],
				after: 'from_date1'
			},
			{
			    action : 'addDateTimeInput',
				params : [{
				    id : 'from_date2',
				    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate2)}',
				    value: OLBIUS.dateToString(currentFirstDay)
				}],
				before: 'thru_date2'
			},
			{
			    action : 'addDateTimeInput',
				params : [{
				    id : 'thru_date2',
				    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate2)}',
				    value: OLBIUS.dateToString(cur_date)
				}],
				after: 'from_date2'
			},
			{
                action : 'addDropDownList',
                params : [{
                    id : 'customTimePeriodId1',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCYear1)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(listCustomTimePeriodsYearDataSource),
                    index: 0,
                    hide: true
                }],
	            event : function(popup) {
	                popup.onEvent('customTimePeriodId1', 'select', function(event) {
	                    var args = event.args;
	                    var item = popup.item('customTimePeriodId1', args.index);
	                    var filter = item.value;
	                    customTimePeriodYear = filter;
	                    if(dateTypeCheck != "YEAR"){
	                    }
	                });
	            }
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateTypePeriodMonth1',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCMonth1)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(listCustomTimePeriodsMonthDataSource),
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateTypePeriodQuater1',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCQuater1)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(listCustomTimePeriodsQuaterDataSource),
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'customTimePeriodId2',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCYear2)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(listCustomTimePeriodsYearDataSource),
                    index: 0,
                    hide: true
                }],
	            event : function(popup) {
	                popup.onEvent('customTimePeriodId2', 'select', function(event) {
	                    var args = event.args;
	                    var item = popup.item('customTimePeriodId2', args.index);
	                    var filter = item.value;
	                    customTimePeriodYear = filter;
	                    if(dateTypeCheck != "YEAR"){
	                    }
	                });
	            }
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateTypePeriodMonth2',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCMonth2)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(listCustomTimePeriodsMonthDataSource),
                    index: 0,
                    hide: true
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateTypePeriodQuater2',
                    label : '${StringUtil.wrapString(uiLabelMap.BACCQuater2)}',
                    data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(listCustomTimePeriodsQuaterDataSource),
                    index: 0,
                    hide: true
                }]
            }
        ];
        var columnChart = OLBIUS.oLapChart('PIGColumnChart', config, configPopup, 'evaluatePIGColumnChart', true, true, OLBIUS.defaultColumnFunc);
        columnChart.funcUpdate(function (oLap) {
        	if(dateTypeCheck == "DAY"){ 
        		var thruDate1 = oLap.val('thru_date1');
        		var fromDate1 = oLap.val('from_date1');
        		var thruDate2 = oLap.val('thru_date2');
        		var fromDate2 = oLap.val('from_date2');
        	}
        	if(dateTypeCheck == "MONTH"){
        		var yearPeriod1 = oLap.val('customTimePeriodId1');
        		var monthPeriod1 = oLap.val('dateTypePeriodMonth1');
        		var yearPeriod2 = oLap.val('customTimePeriodId2');
        		var monthPeriod2 = oLap.val('dateTypePeriodMonth2');
        		if(yearPeriod1 != null){
        			if(monthPeriod1 != null){
            			var thruDate1 = accutils.getThruDate(yearPeriod1, monthPeriod1, null);
            			var fromDate1 = accutils.getFromDate(yearPeriod1, monthPeriod1, null);
        			}else{
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectMonthByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
            	}else{
            		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
            	}
        		if(yearPeriod2 != null){
        			if(monthPeriod2 != null){
            			var thruDate2 = accutils.getThruDate(yearPeriod2, monthPeriod2, null);
            			var fromDate2 = accutils.getFromDate(yearPeriod2, monthPeriod2, null);
        			}else{
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectMonthByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
            	}else{
            		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
            	}
        	}
        	if(dateTypeCheck == "QUARTER"){
        		var yearPeriod1 = oLap.val('customTimePeriodId1');
        		var quaterPeriod1 = oLap.val('dateTypePeriodQuater1');
        		var yearPeriod2 = oLap.val('customTimePeriodId2');
        		var quaterPeriod2 = oLap.val('dateTypePeriodQuater2');
        		if(yearPeriod1 != null){
        			if(quaterPeriod1 != null){
        				var thruDate1 = accutils.getThruDate(yearPeriod1, null, quaterPeriod1);
            			var fromDate1 = accutils.getFromDate(yearPeriod1, null, quaterPeriod1);
        			}else{
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectQuaterByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
        		}else{
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
        		}
        		if(yearPeriod2 != null){
        			if(quaterPeriod2 != null){
        				var thruDate2 = accutils.getThruDate(yearPeriod2, null, quaterPeriod2);
            			var fromDate2 = accutils.getFromDate(yearPeriod2, null, quaterPeriod2);
        			}else{
        				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectQuaterByFilter)}", [{
    		                "label" : "${uiLabelMap.POCommonOK}",
    		                "class" : "btn btn-primary standard-bootbox-bt",
    		                "icon" : "fa fa-check",
    		                }]
    		            );
        				return false;
        			}
        		}else{
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
        		}
        	}
        	if(dateTypeCheck == "WEEK"){
        		var thruDate1 = oLap.val('thru_date1');
        		var fromDate1 = oLap.val('from_date1');
        		var thruDate2 = oLap.val('thru_date2');
        		var fromDate2 = oLap.val('from_date2');
        	}
        	if(dateTypeCheck == "YEAR"){
        		var yearPeriod1 = oLap.val('customTimePeriodId1');
        		var yearPeriod2 = oLap.val('customTimePeriodId2');
        		if(yearPeriod1 != null){
        			var thruDate1 = accutils.getThruDate(yearPeriod1, null, null);
        			var fromDate1 = accutils.getFromDate(yearPeriod1, null, null);
        		}else{
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
        		}
        		if(yearPeriod2 != null){
        			var thruDate2 = accutils.getThruDate(yearPeriod2, null, null);
        			var fromDate2 = accutils.getFromDate(yearPeriod2, null, null);
        		}else{
        			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.POSelectYearByFilter)}", [{
		                "label" : "${uiLabelMap.POCommonOK}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
    				return false;
        		}
        	}
        	
        	var productId = oLap.val('productId');
            oLap.update({
            	'dateType': oLap.val('dateType'),
            	'fromDate1': fromDate1,
                'thruDate1': thruDate1,
                'fromDate2': fromDate2,
                'thruDate2': thruDate2,
                'productId': productId
            }, oLap.val('dateType'));
        });
        columnChart.init(function () {
            columnChart.runAjax();
        });
	});
</script>