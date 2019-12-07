<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign products = delegator.findByAnd("Product", {"productTypeId", "FINISHED_GOOD"}, null, false) />
	
	<#assign  productsArray = "[" />
	<#assign flag = "N" />
	<#list products as product>
		<#if flag == "Y">
	    	<#assign productsArray = productsArray + ", " />
		</#if>
	    <#assign productsArray = productsArray + "{ value: " + "\'" + product.get("productId") + "\'" + ", text: " + "\'" + product.get("productName") + "\'"  + ", productCode: " + "\'" + product.get("productCode") + "\'" + " }" />
	    <#assign flag = "Y" />
	</#list>
	<#assign productsArray = productsArray + "]" />
	
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
var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
var productsArray = ${StringUtil.wrapString(productsArray)};
	$(function(){
		var column = [];
		var groups = [];
		

		for (var i = 0; i < productsArray.length; i++){
			var full_title = "";
			if (OlbiusConfig.report.show.productName) {
				full_title += productsArray[i].text ? productsArray[i].text : "";
			}
			if (OlbiusConfig.report.show.productCode) {
				if (full_title != "") {
					full_title += "</b><br><b>";
				}
				full_title += productsArray[i].productCode;
			}
			if (full_title) {
				var field2 = {text: full_title, align: 'center', name: productsArray[i].value};
	    		var field = {text: '${StringUtil.wrapString(uiLabelMap.BSActual)}', datafield:productsArray[i].value+"_a", type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: productsArray[i].value, filterable: false};
	    		var field3 = {text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield:productsArray[i].value+"_e", type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: productsArray[i].value, filterable: false};
	    		var field4 = {text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}', datafield:productsArray[i].value+"_p", type: 'number', width: '12%', cellsalign: 'right', cellsformat: 'n2', columngroup: productsArray[i].value, filterable: false,
	    				cellsrenderer: function (row, column, value) {
	    					if (typeof (value) == 'number') {
	    						value = value.toLocaleString(locale) + ' %';
							}
					        return '<div class=\"text-right\">' + value + '</div>';
					    }
	    		};
	    		column.push(field);
	    		column.push(field3);
	    		column.push(field4);
	    		groups.push(field2);
			}
		}
	column.push({ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		datafield: 'stt', columntype: 'number', width: '3%',
		  	cellsrenderer: function (row, column, value) {
			  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		}
 	});
	
	column.push({text: "${StringUtil.wrapString(uiLabelMap.DADistributorId)}", datafield:'se_code', cellsalign: 'left', type: 'string', width: '13%', pinned : true});
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
                    		   index: _.find(_.map(salesYearData, function(obj, key){
            		       		    	if (obj.value == OlbiusConfig.report.time.current.year) {
            		    					return key;
            		    				}
            		    		    }), function(key){ return key })
                    	   }]
                       },
                       {
                        action : 'addDropDownList',
                        params : [{
                            id : 'monthh',
                            label : '${StringUtil.wrapString(uiLabelMap.BSMonth)}',
                            data : salesMonthData,
                            index: _.find(_.map(salesMonthData, function(obj, key){
            		    		    	if (obj.value == (OlbiusConfig.report.time.current.month + 1)) {
            		    					return key;
            		    				}
            		    		    }), function(key){ return key })
	                        }]
	                    },
	                    {
						    action : 'addDropDownListMultil',
						    params : [{
						        id : 'products',
						        label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
						        data : productsArray,
						        index: 0
						    }]
						}
                ];

    var testGrid = OLBIUS.oLapGrid('salesOut', config, configPopup, 'evaluateESIGrid', true);
    testGrid.funcUpdate(function (oLap) {
    	var products = typeof (oLap.val('products')) == 'object'?oLap.val('products'):_.map(productsArray, function(obj, key){
    		return obj.value;
        });
    	if (_.isEmpty(products)) {
    		products = _.map(productsArray, function(obj, key){
        		return obj.value;
            });
		}
        oLap.update({
            'yearr': oLap.val('yearr'),
            'monthh': oLap.val('monthh'),
            'products': products
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
