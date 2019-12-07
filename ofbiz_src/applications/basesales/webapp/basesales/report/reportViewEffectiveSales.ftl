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
</script>
<script id="test">
	$(function(){
        var config = {
    		sortable: true,
        	filterable: true,
        	showfilterrow: true,
            title: '${StringUtil.wrapString(uiLabelMap.BSEffectiveSales)}',
            service: 'salesOrder',
            columns: [
        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  datafield: 'stt', columntype: 'number', width: '3%',
		    	  cellsrenderer: function (row, column, value) {
		    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    	  		}
		 		},   
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productCode', type: 'string', width: '12%',  align: 'center', pinned: true},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '30%', align: 'center', pinned: true},
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'janr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jan', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'jan', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jan', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'janp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jan', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }	
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'febr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Feb', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'feb', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Feb', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'febp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Feb', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'marr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Mar', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'mar', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Mar', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'marp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Mar', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'aprr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Apr', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'apr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Apr', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'aprp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Apr', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'mayr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'May', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'may', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'May', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'mayp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'May', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'junr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jun', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'jun', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jun', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'junp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jun', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'julr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jul', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'jul', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jul', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'julp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Jul', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'augr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Aug', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'aug', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Aug', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'augp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Aug', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'sepr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Sep', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'sep', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Sep', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'sepp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Sep', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'octr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Oct', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'oct', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Oct', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'octp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Oct', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'novr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Nov', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'nov', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Nov', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'novp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Nov', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
                { text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield: 'decr', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Dec', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'dec', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Dec', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield: 'decp', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', columngroup: 'Dec', filterable: false,
                	cellsrenderer: function (row, column, value) {
                		var cellClass = "";
    					if (typeof (value) == 'number') {
    						if (value < 100) {
        						cellClass = "background-important-nd";
    						}
    						value = value.toLocaleString(locale) + ' %';
						}
				        return '<div class=\"text-right ' + cellClass + '\">' + value + '</div>';
				    }
                },
            ],
            columngroups: 
        	[
				{ text: '${StringUtil.wrapString(uiLabelMap.BSJanuary)}', align: 'center', name: 'Jan'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSFebruary)}', align: 'center', name: 'Feb'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSMarch)}', align: 'center', name: 'Mar'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSApril)}', align: 'center', name: 'Apr'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSMay)}', align: 'center', name: 'May'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSJune)}', align: 'center', name: 'Jun'},
        	 	{ text: '${StringUtil.wrapString(uiLabelMap.BSJuly)}', align: 'center', name: 'Jul'},
        	 	{ text: '${StringUtil.wrapString(uiLabelMap.BSAugust)}', align: 'center', name: 'Aug'},
        	 	{ text: '${StringUtil.wrapString(uiLabelMap.BSSeptember)}', align: 'center', name: 'Sep'},
        	 	{ text: '${StringUtil.wrapString(uiLabelMap.BSOctober)}', align: 'center', name: 'Oct'},
        	 	{ text: '${StringUtil.wrapString(uiLabelMap.BSNovember)}', align: 'center', name: 'Nov'},
    	 		{ text: '${StringUtil.wrapString(uiLabelMap.BSDecember)}', align: 'center', name: 'Dec'},
    	 	]
        };

        var configPopup = [
              {
                action : 'addDropDownList',
                params : [{
                    id : 'yearr',
                    label : '${StringUtil.wrapString(uiLabelMap.BSYear)}',
                    data : salesYearData,
                    index: _.find(_.map(salesYearData, function(obj, key){
	        		    	if (obj.value == OlbiusConfig.report.time.current.year) {
	        					return key;
	        				}
	        		    }), function(key){ return key })
                }]
            },
        ];

        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateESGrid', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'yearr': oLap.val('yearr')
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
