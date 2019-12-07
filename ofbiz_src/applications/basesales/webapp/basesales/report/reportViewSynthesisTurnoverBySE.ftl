<style>
	.olbiusGridContainer {
		margin-bottom: 50px!important; 
	}
	.jqx-widget-header-olbius > div > div {
	    white-space: normal;
		text-align: center!important;
	}
</style>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
</script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign statusItem = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "ORDER_STATUS"), null, false)>
	var statusItem = [
	    <#list statusItem as statusItemL>
	    {
	    	statusId : "${statusItemL.statusId}",
	    	description: "${StringUtil.wrapString(statusItemL.get("description", locale))}"
	    },
	    </#list>	
	];
   	
   	var statusItemData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if statusItem?exists>
		<#list statusItem as statusItemL >
			statusItemData2.push({ 'value': '${statusItemL.statusId?if_exists}', 'text': '${StringUtil.wrapString(statusItemL.get("description", locale))?if_exists}'});
		</#list>
	</#if>
	
	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!>
	var salesChannel = [
	    <#list salesChannel as salesChannelL>
	    {
	    	enumId : "${salesChannelL.enumId}",
	    	description: "${StringUtil.wrapString(salesChannelL.get("description", locale))}"
	    },
	    </#list>	
	];
	
   	var channelData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if salesChannel?exists>
		<#list salesChannel as salesChannelL >
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.get("description", locale))?if_exists}'});
		</#list>
	</#if>
	
	var gOrgan;
	var gOrderStatus;
	var gFromDate;
	var gThruDate;
	var gChannel;
	var SynSaExVGrid;
	var SynSaExQGrid;
	var gCustomTime;
	
	$($(".breadcrumb").children()[1]).html("${uiLabelMap.Report} <span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSMRevenueBySalesExecutive)}");
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>

<script id="synthesisBySaExQuantity">
$(function(){
	var dateCurrent = new Date();
	var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
	var column = [];
	var groups = [];
	$.ajax({url: 'getStoreListColumn',
	    type: 'post',
	    async: false,
	    success: function(data) {
	    	var listDatafield = data.listResultStore;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var full_title = "";
	    		if (OlbiusConfig.report.show.productName) {
	    			full_title += listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
				}
	    		if (OlbiusConfig.report.show.productCode) {
	    			if (full_title != "") {
	    				full_title += "</b><br><b>";
					}
	    			full_title += listDatafield[i].product_code;
	    		}
	    		var field = {text: full_title, datafield:listDatafield[i].product_id+"_q", type: 'string', width: '10%', cellsalign: 'right', cellsformat: 'n2', align: 'center', filterable: false};
	    		var field3 = {text: (listDatafield[i].internal_name ? listDatafield[i].internal_name : ""), datafield:listDatafield[i].product_id+"_t", type: 'string', width: '9%', cellsalign: 'right', cellsformat: 'n2', hidden : true, filterable: false};
	    		column.push(field);
	    		column.push(field3);
	    	}
	    },
	    error: function(data) {
	    	alert('Error !!');
	    }
	});
	
	column.push({ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		datafield: 'stt', columntype: 'number', width: '3%',
		  	cellsrenderer: function (row, column, value) {
			  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		}
 	});
	
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSStaffId)}", datafield:'staffId', cellsalign: 'left', type: 'string', width: '13%', pinned : true});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSFullName)}", datafield:'staffName', cellsalign: 'left', type: 'string', width: '18%', pinned : true, });
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSVolumeTotal)}", datafield:'volumeTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', filterable: false});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSValueTotal)}", datafield:'valueTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', hidden: true, filterable: false});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSValueTotalVAT)}", datafield:'valueTotalVAT', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', filterable: false, hidden: true});
	
	var config = {
    		title: '${StringUtil.wrapString(uiLabelMap.BSByVolume)}',
    		service: 'salesOrder',
            columns: column,
            sortable: true,
	    	filterable: true,
	    	showfilterrow: true,
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
	            index: 2,
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
                id : 'orderStatus',
                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
                data : statusItemData2,
                index: 0
            }]
        },
        {
            action : 'addDropDownList',
            params : [{
                id : 'channel',
                label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
                data : channelData2,
                index: 0
            }]
        },
    ];


    SynSaExQGrid = OLBIUS.oLapGrid('synthesisBySaExQuantity', config, configPopup, 'evaluateSalesSynthesisReportBySalesExecutiveGrid', true);

    SynSaExQGrid.funcUpdate(function (oLap) {
    	gOrderStatus = oLap.val('orderStatus');
    	gFromDate = oLap.val('from_date_1');
    	gThruDate = oLap.val('thru_date_1');
    	gChannel = oLap.val('channel');
    	gCustomTime = oLap.val('customTime');
    	
        oLap.update({
            'orderStatus': oLap.val('orderStatus'),
            'fromDate': oLap.val('from_date_1'),
            'thruDate': oLap.val('thru_date_1'),
            'channel': oLap.val('channel'),
            'customTime': oLap.val('customTime'),
        });
    });
        

    SynSaExQGrid.init(function () {
        SynSaExQGrid.runAjax();
        SynSaExVGrid.runAjax();
    },false ,function(oLap){
    	var dataAll = oLap.getAllData();
    	if(dataAll.length != 0){
        	var fromDateInput = oLap.val('from_date');
        	var thruDateInput = oLap.val('thru_date');
        	var dateFromDate = new Date(fromDateInput);
        	var dateThruDate = new Date(thruDateInput);
        	var dateFrom = dateFromDate.getTime();
        	var thruFrom = dateThruDate.getTime();
        	var orderStatus = oLap.val('orderStatus');
        	var channel = oLap.val('channel');
        	
        	window.location.href = "exportSynthesisReportBySaExToExcel?&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&orderStatus=" + orderStatus + "&channel=" + channel;
    	}else{
    		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
    		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
    		    "class" : "btn-small btn-primary width60px",
    		    }]
    		   );
    	}
	});
});
</script>

<script id="synthesisBySaExValue">
$(function(){
	var column = [];
	var groups = [];
	$.ajax({url: 'getStoreListColumn',
	    type: 'post',
	    async: false,
	    success: function(data) {
	    	var listDatafield = data.listResultStore;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var full_title = "";
	    		if (OlbiusConfig.report.show.productName) {
	    			full_title += listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
				}
	    		if (OlbiusConfig.report.show.productCode) {
	    			if (full_title != "") {
	    				full_title += "</b><br><b>";
					}
	    			full_title += listDatafield[i].product_code;
	    		}
	    		var field = {text: full_title, datafield:listDatafield[i].product_id+"_q", type: 'string', width: '12.5%', cellsalign: 'right', cellsformat: 'n2', align: 'center', hidden : true, filterable: false};
	    		var field3 = {text: full_title, datafield:listDatafield[i].product_id+"_t", type: 'string', width: '10%', cellsalign: 'right', cellsformat: 'n2', filterable: false};
	    		column.push(field);
	    		column.push(field3);
	    	}
	    },
	    error: function(data) {
	    	alert('Error !!');
	    }
	});
	column.push({ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		datafield: 'stt', columntype: 'number', width: '3%',
		  	cellsrenderer: function (row, column, value) {
			  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		}
 	});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSStaffId)}", datafield:'staffId', cellsalign: 'left', type: 'string', width: '13%', pinned : true});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSFullName)}", datafield:'staffName', cellsalign: 'left', type: 'string', width: '18%', pinned : true, });
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSVolumeTotal)}", datafield:'volumeTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', hidden: true, filterable: false});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSValueTotal)}", datafield:'valueTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', filterable: false});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSValueTotalVAT)}", datafield:'valueTotalVAT', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', filterable: false});
	
	var config = {
    		title: '${StringUtil.wrapString(uiLabelMap.BSByValue)}',
            columns: column,
            sortable: true,
	    	filterable: true,
	    	showfilterrow: true,
    };
	
	SynSaExVGrid = OLBIUS.oLapGrid('synthesisBySaExValue', config, null, 'evaluateSalesSynthesisReportBySalesExecutiveGrid', true);
	        SynSaExVGrid.funcUpdate(function (oLap) {
	            oLap.update({
	                'customTime': gCustomTime,
	                'orderStatus': gOrderStatus,
	                'fromDate': gFromDate,
	                'thruDate': gThruDate,
	                'channel': gChannel,
	            });
	        });
		
        	SynSaExVGrid.init(function () {
        		SynSaExVGrid.runAjax();
        	});
    });
</script>