<style>
	.jqx-grid-group-cell {
		border-right: 1px solid;
	}
	.total-content{
		white-space: normal;	
	    padding: 0 5px;
	}
	.total-content .total-content-item{
	    height: 20px;
		margin-bottom: 5px;
	}
	 .total-content-item [class*="span"]{
	 	min-height: 20px;
	 	height:20px;
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

<script>
	<#assign marCam = delegator.findList("MarketingCampaign", null, null, null, null, false)!>
	var marCam = [
	    <#list marCam as marCamL>
	    {
	    	marketingCampaignId : "${marCamL.marketingCampaignId}",
	    	campaignName: "${StringUtil.wrapString(marCamL.get("campaignName", locale))}"
	    },
	    </#list>	
	];
	
	var marketingCampaignData = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
	<#if marCam?exists>
		<#list marCam as marCamL >
			marketingCampaignData.push({ 'value': '${marCamL.marketingCampaignId?if_exists}', 'text': '${StringUtil.wrapString(marCamL.campaignName)?if_exists}' });
		</#list>
	</#if>
	
</script>

<script type="text/javascript" id="communicationEmployeeReportGrid">
	var column = [];
	var sumMap;
	var sumMapContacted;
	var sumMapUncontacted;
	$.ajax({url: 'getListColumnEmployee',
	    type: 'post',
	    async: false,
	    success: function(data) {
	    	var listDatafield = data.listEmployeee;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var field = {text: listDatafield[i], datafield:listDatafield[i], type: 'string', width: 120, cellsalign: 'right',
						aggregatesrenderer: function (aggregates, column, element, summaryData) {
							var sum = 0;
							var sumC = 0;
							var sumU = 0;
							if (sumMap[column.datafield]){
								sum = sumMap[column.datafield];
							}
							if (sumMapContacted[column.datafield]){
								sumC = sumMapContacted[column.datafield];
							}
							if (sumMapUncontacted[column.datafield]){
								sumU = sumMapUncontacted[column.datafield];
							}
                      		var renderstring = '<div class="total-content" style="white-space: normal;">'
                      							+ '<div class="row-fluid total-content-item"><div class="span7" style="color: green;">${uiLabelMap.BCRMContacted}:</div>' 
                      							+  '<div class="span5 pull-right" style="color: green;">'+sumC+'</div></div>'
                      							+ '<div class="row-fluid total-content-item"><div class="span7" style="color: red;">${uiLabelMap.BCRMUnContacted}:</div>' 
                      							+  '<div class="span5 pull-right" style="color: red;">'+sumU+'</div></div>' 
                      							+ '<div class="row-fluid total-content-item"><div class="span7" style="color: blue;">${uiLabelMap.BSTotal}:</div>' 
                      							+  '<div class="span5 pull-right" style="color: blue;">'+sum+'</div></div>' 
                      	  						+ "</div>";
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
  	
	var groupcolumnrenderer = function (text) {
		return '<div class="custom-column" style="line-height: 25px><span>' + text + '</span>';
	}
  	
	var config = {
    		title: ' ${uiLabelMap.CallResultEmployeeReport}',
    		service: 'communicationEvent',
            showstatusbar: true,
            statusbarheight: 50,
           	showaggregates: true,
           	groupable: true,
           	pagesize: 100,
           	statusbarheight: 75,
           	groups: ['result_enum_type_id'],
           	groupsexpandedbydefault: true,
           	groupsrenderer: groupsrenderer,
           	groupcolumnrenderer: groupcolumnrenderer,
           	closeablegroups: false,
            columns: column
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
    
    var communicationEmployeeGrid = OLBIUS.oLapGrid('communicationEmployeeReportGrid', config, configPopup, 'getCommunicationEmployeeReport', true);
    communicationEmployeeGrid.funcUpdate(function (oLap) {
        oLap.update({
            'fromDate': oLap.val('from_date_1'),
            'thruDate': oLap.val('thru_date_1'),
            'isChart': false,
            'customTime': oLap.val('customTime'),
        });
	});
        
    communicationEmployeeGrid.init(function () {
    	communicationEmployeeGrid.runAjax();
    });
    
    communicationEmployeeGrid.getRunData(function(data){
    	sumMap = data.sumMap;
    	sumMapContacted = data.sumMapContacted;
    	sumMapUncontacted = data.sumMapUncontacted;
    	
    });
    
   	$(function(){
		$(window).resize();
	});
</script>

<#--
, function(oLap){
	var dataAll = oLap.getAllData();
	if(dataAll.length != 0){
    	var fromDateInput = oLap.val('from_date');
    	var thruDateInput = oLap.val('thru_date');
    	var dateFromDate = new Date(fromDateInput);
    	var dateThruDate = new Date(thruDateInput);
    	var dateFrom = dateFromDate.getTime();
    	var thruFrom = dateThruDate.getTime();
    	window.location.href = "exportCommunicaitonEmployeeToExcel?&fromDate=" + dateFrom + "&thruDate=" + thruFrom;
	}else{
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
		    "class" : "btn-small btn-primary width60px",
		    }]
		   );
	}
}
-->