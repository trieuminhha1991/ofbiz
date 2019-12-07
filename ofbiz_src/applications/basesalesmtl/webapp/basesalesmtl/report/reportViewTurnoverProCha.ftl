<style>
	.olbiusChartContainer, .olbiusGridContainer{
		margin-bottom: 50px!important;
	}
	.chartC {
		width: 50%;
		float: left;
	}
	.chartD {
		width: 50%;
		float: right;
	}
</style>

<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign salesChannel = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!>
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
   	
   	var channelData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
	<#if salesChannel?exists>
		<#list salesChannel as salesChannelL >
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.description)?if_exists}'});
		</#list>
	</#if>

	var sortByData2 = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSNo)}', 'value': null},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', 'value': 'chaIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductId)}', 'value': 'proIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductName)}', 'value': 'proNameSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', 'value': 'quaSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSTotal)}', 'value': 'totSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', 'value': 'cateSort'}
	];
	
	<#assign statusItem = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "ORDER_STATUS"), null, false)!>
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


	<#assign categoryList = delegator.findByAnd("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryTypeId", "CATALOG_CATEGORY"), null, false)!>
	var categoryList = [
	    <#list categoryList as categoryL>
	    {
	    	productCategoryId : "${categoryL.productCategoryId}",
	    	categoryName: "${StringUtil.wrapString(categoryL.get("categoryName", locale))}"
	    },
	    </#list>	
	];
	
	var categoryData= [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
	<#if categoryList?exists>
		<#list categoryList as categoryL >
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
		</#list>
	</#if>
	
	
	 'fromDate': oLap.val('from_date'),
     'thruDate': oLap.val('thru_date'),
     'sortId': oLap.val('sortId'),
     'orderStatus': oLap.val('orderStatus'),
     'category': oLap.val('category'),
     'storeChannel': oLap.val('storeChannel')
     
     var gFromDateC; var gThruDateC; var gSortC; var gOrderStatusC; var gCategoryC; var gChannelC; var gGridC; var gChart1C;
     var gFromDateD; var gThruDateD; var gSortD; var gOrderStatusD; var gCategoryD; var gChannelD; var gGridD; var gChart1D;
</script>

<div class="grid">
	<script id="test">
	$(function(){
	        var config = {
				sortable: true,
		    	filterable: true,
		    	showfilterrow: true,
	            title: '${StringUtil.wrapString(uiLabelMap.BSMByCompany)}',
	            service: 'salesOrder',
	            columns: [
	        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: 'stt', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  		}
			 		},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channel', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', datafield: 'category', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '12%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '31%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantityNUnit)}', datafield: 'Quantity', type: 'number', width: '12%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSTotal)}', datafield: 'Total', type: 'number', width: '12%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
	            ]
	        };
	
	        var configPopup = [
	              {
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'storeChannel',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
	                    data : channelData2,
	                    index: 0
	                }]
	            },
	            {
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'category',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
	                    data : categoryData,
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
		                id : 'orderStatus',
		                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
		                data : statusItemData,
		                index: 5
		            }]
	        	},
	        	{
			        action : 'addDropDownList',
			        params : [{
			            id : 'sortId',
			            label : '${StringUtil.wrapString(uiLabelMap.BSSortBy)}',
			            data : sortByData2,
			            index: 0
			        }]
			    },
	        ];
	
	
	        gGridC = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesOlapGridByChannel', true);
	
	        gGridC.funcUpdate(function (oLap) {
	        	gFromDateC = oLap.val('from_date');
	        	gThruDateC = oLap.val('thru_date');
	        	gSortC = oLap.val('sortId');
	        	gOrderStatusC = oLap.val('orderStatus');
	        	gCategoryC = oLap.val('category');
	        	gChannelC = oLap.val('storeChannel');
	        	
	            oLap.update({
	                'fromDate': oLap.val('from_date'),
	                'thruDate': oLap.val('thru_date'),
	                'sortId': oLap.val('sortId'),
	                'orderStatus': oLap.val('orderStatus'),
	                'category': oLap.val('category'),
	                'storeChannel': oLap.val('storeChannel')
	            });
	        });
	
	        gGridC.init(function () {
	        	gGridC.runAjax();
	        	gChart1C.runAjax();
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
	            	
	            	window.location.href = "exportTurnoverProChaReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&channel=" + channelInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
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
	
	<script id="test2">
	$(function(){
	        var config = {
	    		sortable: true,
	        	filterable: true,
	        	showfilterrow: true,
	            title: '${StringUtil.wrapString(uiLabelMap.BSMByDistributor)}',
	            columns: [
	        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: 'stt', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  		}
			 		},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', datafield: 'channel', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', datafield: 'category', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '12%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '31%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantityNUnit)}', datafield: 'Quantity', type: 'number', width: '12%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSTotal)}', datafield: 'Total', type: 'number', width: '12%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
	            ]
	        };
	
	        var configPopup = [
	              {
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'storeChannel',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
	                    data : channelData2,
	                    index: 0
	                }]
	            },
	            {
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'category',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
	                    data : categoryData,
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
		                id : 'orderStatus',
		                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
		                data : statusItemData,
		                index: 5
		            }]
	        	},
	        	{
			        action : 'addDropDownList',
			        params : [{
			            id : 'sortId',
			            label : '${StringUtil.wrapString(uiLabelMap.BSSortBy)}',
			            data : sortByData2,
			            index: 0
			        }]
			    },
	        ];
	
	
	        gGridD = OLBIUS.oLapGrid('test2', config, configPopup, 'evaluateTurnoverByCD', true);
	
	        gGridD.funcUpdate(function (oLap) {
	        	gFromDateD = oLap.val('from_date');
	        	gThruDateD = oLap.val('thru_date');
	        	gSortD = oLap.val('sortId');
	        	gOrderStatusD = oLap.val('orderStatus');
	        	gCategoryD = oLap.val('category');
	        	gChannelD = oLap.val('storeChannel');
	        	
	            oLap.update({
	                'fromDate': oLap.val('from_date'),
	                'thruDate': oLap.val('thru_date'),
	                'sortId': oLap.val('sortId'),
	                'orderStatus': oLap.val('orderStatus'),
	                'category': oLap.val('category'),
	                'storeChannel': oLap.val('storeChannel')
	            });
	        });
	
	        gGridD.init(function () {
	        	gGridD.runAjax();
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
	            	
	            	window.location.href = "exportTurnoverProChaReportDisToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&channel=" + channelInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
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
</div>
<div class="chartC">
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
   	                action : 'addDropDownListMultil',
   	                params : [{
   	                    id : 'storeChannel',
   	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
   	                    data : channelData2,
   	                    index: 0
   	                }]
   	            },
   	            {
   	                action : 'addDropDownListMultil',
   	                params : [{
   	                    id : 'category',
   	                    label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
   	                    data : categoryData,
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
   		                id : 'orderStatus',
   		                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
   		                data : statusItemData,
   		                index: 5
   		            }]
   	        	},
   	        	{
   			        action : 'addDropDownList',
   			        params : [{
   			            id : 'sortId',
   			            label : '${StringUtil.wrapString(uiLabelMap.BSSortBy)}',
   			            data : sortByData2,
   			            index: 0
   			        }]
   			    },
	        ];
	
	        gChart1C = OLBIUS.oLapChart('PCAreaChart', config, null, 'evaluateSalesPCAreaChart', true, true, OLBIUS.defaultColumnFunc);
	
	        gChart1C.funcUpdate(function (oLap) {
	            oLap.update({
	            	'fromDate': gFromDateC,
	                'thruDate': gThruDateC,
	                'sortId': gSortC,
	                'orderStatus': gOrderStatusC,
	                'category': gCategoryC,
	                'storeChannel': gChannelC
	            });
	        });
	
	        gChart1C.init(function () {
	        	gChart1C.runAjax();
	        });
		});
	</script>
</div>
<div class="chartD">
	<script type="text/javascript" id="PCDColumnChart">
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
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'storeChannel',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
	                    data : channelData2,
	                    index: 0
	                }]
	            },
	            {
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'category',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
	                    data : categoryData,
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
		                id : 'orderStatus',
		                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
		                data : statusItemData,
		                index: 5
		            }]
	        	},
	        	{
			        action : 'addDropDownList',
			        params : [{
			            id : 'sortId',
			            label : '${StringUtil.wrapString(uiLabelMap.BSSortBy)}',
			            data : sortByData2,
			            index: 0
			        }]
			    },
        ];

        gChart1D = OLBIUS.oLapChart('PCDColumnChart', config, null, 'evaluateSalesPCDAreaChart', true, true, OLBIUS.defaultColumnFunc);

        gChart1D.funcUpdate(function (oLap) {
            oLap.update({
            	'fromDate': gFromDateD,
                'thruDate': gThruDateD,
                'sortId': gSortD,
                'orderStatus': gOrderStatusD,
                'category': gCategoryD,
                'storeChannel': gChannelD
            });
        });

        gChart1D.init(function () {
        	gChart1D.runAjax();
        });
	});
</script>
</div>