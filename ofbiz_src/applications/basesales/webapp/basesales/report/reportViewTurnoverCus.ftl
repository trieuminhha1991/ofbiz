<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)>
	var salesChannel = [
	    <#list salesChannel as salesChannelL>
	    {
	    	enumId : "${salesChannelL.enumId}",
	    	description: "${StringUtil.wrapString(salesChannelL.get("description", locale))}"
	    },
	    </#list>	
	];
	
	var listChannelDataSource = [];
  	for(var x in salesChannel){
    	var channelDataSource = {
     		text: salesChannel[x].description,
     		value: salesChannel[x].enumId,
    	}
    listChannelDataSource.push(channelDataSource);
   	} 
   	
   	<#assign statusItem = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "ORDER_STATUS"), null, false)>
	var statusItem = [
	    <#list statusItem as statusItemL>
	    {
	    	statusId : "${statusItemL.statusId}",
	    	description: "${StringUtil.wrapString(statusItemL.get("description", locale))}"
	    },
	    </#list>	
	];
   	
   	var statusItemData = [];
	<#if statusItem?exists>
		<#list statusItem as statusItemL >
			statusItemData.push({ 'value': '${statusItemL.statusId?if_exists}', 'text': '${StringUtil.wrapString(statusItemL.get("description", locale))?if_exists}'});
		</#list>
	</#if>
	
	var channelData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if salesChannel?exists>
		<#list salesChannel as salesChannelL >
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.description)?if_exists}'});
		</#list>
	</#if>
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>
<script id="test">
$(function(){
		var dateCurrent = new Date();
		var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSCustomerReport)}',
            service: 'salesOrder',
            columns: [
				{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
					  datafield: 'stt', columntype: 'number', width: '3%',
					  cellsrenderer: function (row, column, value) {
						  return '<div style=margin:4px;>' + (value + 1) + '</div>';
					  }
					},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channelName', type: 'string', width: '15%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'storeName', type: 'string', width: '15%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSMAgency)}', datafield: 'customerId', type: 'string', width: '12%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'customerName', type: 'string', width: '25%'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'monetized', type: 'number', width: '15%', cellsformat: 'n0', cellsalign: 'right'},
				  { text: '${StringUtil.wrapString(uiLabelMap.BSRevenueTotal)}', datafield: 'percent', type: 'number', width: '15%', cellsformat: 'p2', cellsalign: 'right'},
            ]
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
                before: 'thru_date_1'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                    hide: true,
                }],
                after: 'from_date_1'
            },
            {
		        action : 'addDropDownList',
		        params : [{
		            id : 'customTime',
		            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
		            data : customDate,
		            index: 1,
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
                action : 'addDropDownListMultil',
                params : [{
                    id : 'channel',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
                    data : channelData2,
                    index: 0
                }]
            },
            {
	            action : 'addDropDownList',
	            params : [{
	                id : 'orderStatus',
	                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
	                data : statusItemData,
	                index: 5
	            }]
	        },
        ];


        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesCustomerOlapGrid', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
            	'orig': oLap.val('organization'),
                'orderStatus': oLap.val('orderStatus'),
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'channel':oLap.val('channel'),
                'customTime':oLap.val('customTime')
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
        });
    });
</script>

<#-- 
,function(oLap){
        	if(oLap){
        		var dateTypeInput = oLap.val('dateType');
            	var fromDateInput = oLap.val('from_date');
            	var thruDateInput = oLap.val('thru_date');
            	var channelInput = oLap.val('channel');
            	var dateFromDate = new Date(fromDateInput);
            	var dateThruDate = new Date(thruDateInput);
            	var dateFrom = dateFromDate.getTime();
            	var thruFrom = dateThruDate.getTime();
            	
            	window.location.href = "exportSalesReportCTo.pdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&channel=" + channelInput;
        	}else{
        		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
        		    "class" : "btn-small btn-primary width60px",
        		    }]
        		   );
        	}
        	 -->