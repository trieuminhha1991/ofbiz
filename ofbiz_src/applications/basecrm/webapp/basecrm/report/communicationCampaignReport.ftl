<style>
	.jqx-grid-group-cell {
		border-right: 1px solid;
	}
</style>

<script type="text/javascript">
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>

<script type="text/javascript" id="communicationCampaignReportGrid">
	var column = [];
	var sumMap;
	$.ajax({url: 'getListColumn',
	    type: 'post',
	    async: false,
	    success: function(data) {
	    	var listDatafield = data.listResultEnumType;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var field = {text: listDatafield[i], datafield:"x_" + listDatafield[i], type: 'string', width: '150', cellsalign: 'right',
	    						aggregatesrenderer: function (aggregates, column, element, summaryData) {
	    							var sum = 0;
	    							if (sumMap[column.datafield]){
	    								sum = sumMap[column.datafield];
	    							}
	                          		var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + 'olbius' + "' style='float: left; width: 100%; height: 100%;'>";
	                          		renderstring += '<div style="color: blue;' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;">${uiLabelMap.Total}: ' + sum + '</div>';
	                          	  	renderstring += "</div>";
	                           		return renderstring; 
	                           	}
	    					};
	    		column.push(field);
	    	}
	    },
	    error: function(data) {
	    	alert('Error !!');
	    }
	});

	column.push({text: "${uiLabelMap.ResultEnumId}", datafield:'result_enum_type_id', cellsalign: 'left', type: 'string', minWidth: '30%', pinned: true});
	column.push({text: "${uiLabelMap.ReasonEnumId}", datafield:'result_enum_id', cellsalign: 'left', type: 'string', minWidth: '30%', pinned: true});
	
	var groupsrenderer = function (text, group, expanded, data) {
          return '<div class="" style="line-height: 25px; font-weight: bold"><span>' + text + '</span>';
  	}
	
	var config = {
		title: ' ${uiLabelMap.CallResultCampaignReport}',
		service: 'communicationEvent',
        showstatusbar: true,
        statusbarheight: 50,
       	showaggregates: true,
       	groupable: true,
       	groups: ['result_enum_type_id'],
       	groupsexpandedbydefault: true,
       	groupsrenderer: groupsrenderer,
       	closeablegroups: false,
       	theme: 'olbius',
       	pagesize: 100,
        columns: column,          
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
     	];
    var communicationCampaignGrid = OLBIUS.oLapGrid('communicationCampaignReportGrid', config, configPopup, 'getCommunicationCampaignReport', true);
    communicationCampaignGrid.funcUpdate(function (oLap) {
        oLap.update({
            'fromDate': oLap.val('from_date_1'),
            'thruDate': oLap.val('thru_date_1'),
            'isChart': false,
            'customTime': oLap.val('customTime'),
        });
	});
        
    communicationCampaignGrid.init(function () {
    	communicationCampaignGrid.runAjax();
    });
    
    communicationCampaignGrid.getRunData(function(data){
    	sumMap = data.sumMap;
    });
</script>