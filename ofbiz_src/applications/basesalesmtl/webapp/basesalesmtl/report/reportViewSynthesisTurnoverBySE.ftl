<style>
	.olbiusGridContainer {
		margin-bottom: 50px!important; 
	}
	.jqx-widget-header-olbius > div > div {
	    white-space: normal;
		text-align: center!important;
	}
</style>

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
   	
   	var statusItemData = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if statusItem?exists>
		<#list statusItem as statusItemL >
			statusItemData.push({ 'value': '${statusItemL.statusId?if_exists}', 'text': '${StringUtil.wrapString(statusItemL.get("description", locale))?if_exists}'});
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
</script>

<script id="synthesisBySaExQuantity">
$(function(){
	var column = [];
	var groups = [];
	$.ajax({url: 'getStoreListColumn',
	    type: 'post',
	    async: false,
	    success: function(data) {
	    	var listDatafield = data.listResultStore;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var name = listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
	    		var code = listDatafield[i].product_code;
	    		var full_title = name + "</b><br><b>" + code;
	    		var field = {text: full_title, datafield:listDatafield[i].product_id+"_q", type: 'string', width: '12.5%', cellsalign: 'right', cellsformat: 'n2', align: 'center'};
	    		var field3 = {text: (listDatafield[i].internal_name ? listDatafield[i].internal_name : ""), datafield:listDatafield[i].product_id+"_t", type: 'string', width: '9%', cellsalign: 'right', cellsformat: 'n2', hidden : true};
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
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSVolumeTotal)}", datafield:'volumeTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', });
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSValueTotal)}", datafield:'valueTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', hidden: true});
	
	var config = {
    		title: '${StringUtil.wrapString(uiLabelMap.BSByVolume)}',
            columns: column,
            columnsheight: 75,
    };
	
    var configPopup = [
        {
            action : 'addDropDownList',
            params : [{
                id : 'organization',
                label : '${StringUtil.wrapString(uiLabelMap.BSOrganization)}',
                data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_true)}', value: 'true'}],
                index: 0
            }]
        },
     	{
            action : 'addDropDownListMultil',
            params : [{
                id : 'orderStatus',
                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
                data : statusItemData,
                index: 0
            }]
        },
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

    SynSaExQGrid = OLBIUS.oLapGrid('synthesisBySaExQuantity', config, configPopup, 'synthesisReportBySalesExecutiveSM', true);

    SynSaExQGrid.funcUpdate(function (oLap) {
    	gOrgan = oLap.val('organization');
    	gOrderStatus = oLap.val('orderStatus');
    	gFromDate = oLap.val('from_date');
    	gThruDate = oLap.val('thru_date');
    	gChannel = oLap.val('channel');
    	
        oLap.update({
            'orig': oLap.val('organization'),
            'orderStatus': oLap.val('orderStatus'),
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'channel': oLap.val('channel'),
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
        	
        	window.location.href = "exportSynthesisReportSMBySaExToExcel?&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&orderStatus=" + orderStatus + "&channel=" + channel;
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
	    		var name = listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
	    		var code = listDatafield[i].product_code;
	    		var full_title = name + "</b><br><b>" + code;
	    		var field = {text: full_title, datafield:listDatafield[i].product_id+"_q", type: 'string', width: '12.5%', cellsalign: 'right', cellsformat: 'n2', align: 'center'};
	    		var field3 = {text: (listDatafield[i].internal_name ? listDatafield[i].internal_name : ""), datafield:listDatafield[i].product_id+"_t", type: 'string', width: '9%', cellsalign: 'right', cellsformat: 'n2', hidden : true};
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
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSVolumeTotal)}", datafield:'volumeTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2', hidden: true});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSValueTotal)}", datafield:'valueTotal', cellsalign: 'right', type: 'number', width: '12%', pinned : true, cellsformat: 'n2'});
	
	var config = {
    		title: '${StringUtil.wrapString(uiLabelMap.BSByValue)}',
            columns: column,
            columnsheight: 75,
    };
	
	SynSaExVGrid = OLBIUS.oLapGrid('synthesisBySaExValue', config, null, 'evaluateSalesSynthesisReportBySalesExecutiveGrid', true);
	        SynSaExVGrid.funcUpdate(function (oLap) {
	            oLap.update({
	                'orig': gOrgan,
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