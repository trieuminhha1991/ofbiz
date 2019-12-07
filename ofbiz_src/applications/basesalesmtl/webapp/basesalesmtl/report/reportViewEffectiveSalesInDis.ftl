<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign salesYear = delegator.findByAnd("CustomTimePeriod", Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", "SALES_YEAR"), null, false)!>
	var salesYear = [
	    <#list salesYear as salesYearL>
	    {
	    	customTimePeriodId : "${salesYearL.customTimePeriodId}",
	    	periodName: "${StringUtil.wrapString(salesYearL.get("periodName", locale))}"
	    },
	    </#list>	
	];
	
   	var salesYearData = [];
	<#if salesYear?exists>
		<#list salesYear as salesYearL >
			salesYearData.push({ 'value': '${salesYearL.periodName?if_exists}', 'text': '${StringUtil.wrapString(salesYearL.periodName)?if_exists}'});
		</#list>
	</#if>
	
	var salesMonthData = [
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSJanuary)}', 'value': '1'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSFebruary)}', 'value': '2'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSMarch)}', 'value': '3'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSApril)}', 'value': '4'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSMay)}', 'value': '5'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSJune)}', 'value': '6'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSJuly)}', 'value': '7'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSAugust)}', 'value': '8'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSSeptember)}', 'value': '9'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSOctober)}', 'value': '10'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSNovember)}', 'value': '11'},
	                      {'text': '${StringUtil.wrapString(uiLabelMap.BSDecember)}', 'value': '12'},
	];
</script>
<script id="salesOut">
	$(function(){
		var column = [];
		var groups = [];
		$.ajax({url: 'getStoreListColumn', type: 'post', async: false, success: function(data) {
	    	// var listDatafield = data.listResultStore;
	    	// var list = [];
	    	// var list2 = [];
	    	// for (var i = 0; i < listDatafield.length; i++){
	    		// var field2 = {text: listDatafield[i], align: 'center', name: listDatafield[i]};
	    		// groups.push(field2);
	    	// }
	    	// for (var i = 0; i < listDatafield.length; i++){
	    		// list.push(listDatafield[i] + 'a');
	    		// list2.push(listDatafield[i] + 'e');
	    	// }
	    	// for (var i = 0; i < list.length; i++){
	    		// var field = {text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield:list[i], type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: listDatafield[i], filterable: false};
	    		// var field3 = {text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield:list2[i], type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: listDatafield[i], filterable: false};
	    		// column.push(field);
	    		// column.push(field3);
	    	// }
	    	var listDatafield = data.listResultStore;
	    	for (var i = 0; i < listDatafield.length; i++){
	    		var name = listDatafield[i].internal_name ? listDatafield[i].internal_name : "";
	    		var code = listDatafield[i].product_code;
	    		var field2 = {text: listDatafield[i].internal_name ? listDatafield[i].internal_name : "", align: 'center', name: listDatafield[i].product_id};
	    		var full_title = name + "</b><br><b>" + code;
	    		var field = {text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield:listDatafield[i].product_id+"_a", type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: listDatafield[i].product_id, filterable: false};
	    		var field3 = {text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield:listDatafield[i].product_id+"_e", type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: listDatafield[i].product_id, filterable: false};
	    		var field4 = {text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield:listDatafield[i].product_id+"_p", type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: listDatafield[i].product_id, filterable: false};
	    		column.push(field);
	    		column.push(field3);
	    		column.push(field4);
	    		groups.push(field2);
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
	
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSStaffId)}", datafield:'se_code', cellsalign: 'left', type: 'string', width: '13%', pinned : true});
	column.push({text: "${StringUtil.wrapString(uiLabelMap.BSFullName)}", datafield:'se_name', cellsalign: 'left', type: 'string', width: '15%', pinned : true});

    var config = {
		sortable: true,
    	filterable: true,
    	showfilterrow: true,
        title: '${StringUtil.wrapString(uiLabelMap.BSEffectiveSales)}',
        service: 'salesOrder',
        columns: column,
        columngroups: groups,
    };

    var configPopup = [
           {
        	   action : 'addDropDownList',
        	   params : [{
        		   id : 'yearr',
        		   label : '${StringUtil.wrapString(uiLabelMap.BSYear)}',
        		   data : salesYearData,
        		   index: 0
        	   }]
           },
          {
            action : 'addDropDownList',
            params : [{
                id : 'monthh',
                label : '${StringUtil.wrapString(uiLabelMap.BSMonth)}',
                data : salesMonthData,
                index: 0,
            }]
        },
    ];

    var testGrid = OLBIUS.oLapGrid('salesOut', config, configPopup, 'evaluateESIDGrid', true);

    testGrid.funcUpdate(function (oLap) {
        oLap.update({
            'yearr': oLap.val('yearr'),
            'monthh': oLap.val('monthh')
        });
    });

    testGrid.init(function () {
        testGrid.runAjax();
    }, false, function(oLap){
    	var dataAll = oLap.getAllData();
    	if(dataAll.length != 0){
        	var fromDateInput = oLap.val('from_date');
        	var thruDateInput = oLap.val('thru_date');
        	var dateFromDate = new Date(fromDateInput);
        	var dateThruDate = new Date(thruDateInput);
        	var dateFrom = dateFromDate.getTime();
        	var thruFrom = dateThruDate.getTime();
        	var sortIdInput = oLap.val('sortId');
        	var orderStatus = oLap.val('orderStatus');
        	var channelInput = oLap.val('storeChannel');
        	var categoryInput = oLap.val('category');
        	
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
<#--
window.location.href = "exportTurnoverProChaReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&channel=" + channelInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
<script type="text/javascript" id="PCColumnChart">
	$(function(){
		var config = {
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPPSColumn)}',
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
            legend: {
                enabled: true
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
            },
        };

        var configPopup = [
            {
	            action : 'addDropDownList',
	            params : [{
	                id : 'storeChannel',
	                label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
	                data : listChannelDataSource,
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
            }
        ];

        var columnChart = OLBIUS.oLapChart('PCColumnChart', config, configPopup, 'evaluateSalesPCColumnChart', true, true, OLBIUS.defaultColumnFunc);

        columnChart.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'orderStatus': oLap.val('orderStatus'),
                'storeChannel': oLap.val('storeChannel')
            }, oLap.val('dateType'));
        });

        columnChart.init(function () {
            columnChart.runAjax();
        });
	});
</script>

<script type="text/javascript" id="PCAreaChart">
	$(function(){
		var config = {
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPPSColumn)}',
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
            legend: {
                enabled: true
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
            },
        };

        var configPopup = [
            {
	            action : 'addDropDownList',
	            params : [{
	                id : 'storeChannel',
	                label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
	                data : listChannelDataSource,
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

        var areaChart = OLBIUS.oLapChart('PCAreaChart', config, configPopup, 'evaluateSalesPCAreaChart', true, true, OLBIUS.defaultColumnFunc);

        areaChart.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'orderStatus': oLap.val('orderStatus'),
                'storeChannel': oLap.val('storeChannel')
            }, oLap.val('dateType'));
        });

        areaChart.init(function () {
            areaChart.runAjax();
        });
	});
</script>
-->
