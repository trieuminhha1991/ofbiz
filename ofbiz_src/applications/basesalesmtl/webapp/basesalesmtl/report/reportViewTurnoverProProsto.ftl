<style>
	.olbiusChartContainer, .olbiusGridContainer{
		margin-bottom: 50px!important;
	}
	.chart1C, .chart2D {
		width: 50%!important;
	}
	.chart1C {
		float: left;
	}
	.chart2D {
		float: right;
	}
</style>

<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)>
	<#assign entityCondition = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "OWNER")>
	<#assign otherCondition = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL , "LEGAL_ORGANIZATION")>
	<#assign productStoreDis = delegator.findList("ProductStoreRoleAndPartyDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondition, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,otherCondition), null, null, null, false)>				
	
	var productStore = [
	    <#list productStore as productStoreL>
	    {
	    	productStoreId : "${productStoreL.productStoreId}",
	    	storeName: "${StringUtil.wrapString(productStoreL.get("storeName", locale))}"
	    },
	    </#list>	
    ];
	
	var productStoreDis = [
        <#list productStoreDis as productStore2L>
        {
        	productStoreId : "${productStore2L.productStoreId}",
        	storeName: "${StringUtil.wrapString(productStore2L.get("storeName", locale))}"
        },
        </#list>	
    ];
	
	var listPSDataSource2 = [];
	for(var x in productStoreDis){
		var productStoreDataSource2 = {
			text: productStoreDis[x].storeName,
			value: productStoreDis[x].productStoreId,
		}
		listPSDataSource2.push(productStoreDataSource2);
	} 
	
	var listPSDataSource = [];
  	for(var x in productStore){
    	var productStoreDataSource = {
     		text: productStore[x].storeName,
     		value: productStore[x].productStoreId,
    	}
    listPSDataSource.push(productStoreDataSource);
   	} 
   	
  	var productStoreData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
  	<#if productStore?exists>
	  	<#list productStore as productStoreL >
	  		productStoreData2.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
	  	</#list>
  	</#if>
  	
	var productStoreData3 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
	<#if productStoreDis?exists>
		<#list productStoreDis as productStoreDisL >
			productStoreData3.push({ 'value': '${productStoreDisL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreDisL.storeName)?if_exists}'});
		</#list>
	</#if>

	var sortByData = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSNo)}', 'value': null},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', 'value': 'stoIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductId)}', 'value': 'proIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductName)}', 'value': 'proNameSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', 'value': 'quaSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', 'value': 'totSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSCategory)}', 'value': 'cateSort'}
	];
	
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
	
	var gFromDateC; var gThruDateC; var gProductStoreC; var gSortIdC; var gCategoryC; var gOrderStatusC; var gGridC; var gChart1C; var gChart2C;
	var gFromDateD; var gThruDateD; var gProductStoreD; var gSortIdD; var gCategoryD; var gOrderStatusD; var gGridD; var gChart1D; var gChart2D;
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
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'productStoreName', type: 'string', width: '15%', cellsalign: 'left'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'category', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPercent)}', datafield: 'percent', type: 'string', width: '10%', hidden: true},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '29%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '13%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
	            ]
	        };
	
	        var configPopup = [
	        	{
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'productStore',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
	                    data : productStoreData2,
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
			            data : sortByData,
			            index: 0
			        }]
			    },
	        ];
	
	        gGridC = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesOlapGrid', true);
	
	        gGridC.funcUpdate(function (oLap) {
	        	gFromDateC = oLap.val('from_date'); 
	        	gThruDateC = oLap.val('thru_date'); 
	        	gProductStoreC = oLap.val('productStore'); 
	        	gSortIdC = oLap.val('sortId'); 
	        	gCategoryC = oLap.val('category'); 
	        	gOrderStatusC = oLap.val('orderStatus');
	            oLap.update({
	                'fromDate': oLap.val('from_date'),
	                'thruDate': oLap.val('thru_date'),
	                'productStore': oLap.val('productStore'),
	                'sortId': oLap.val('sortId'),
	                'category': oLap.val('category'),
	                'orderStatus': oLap.val('orderStatus')
	            });
	        });
	
	        gGridC.init(function () {
	        	gGridC.runAjax();
	        	gChart1C.runAjax();
	        	gChart2C.runAjax();
	        }, false, function(oLap){
	        	var dataAll = oLap.getAllData();s
	        	if(dataAll.length != 0){
	            	var fromDateInput = oLap.val('from_date');
	            	var thruDateInput = oLap.val('thru_date');
	            	var dateFromDate = new Date(fromDateInput);
	            	var dateThruDate = new Date(thruDateInput);
	            	var dateFrom = dateFromDate.getTime();
	            	var thruFrom = dateThruDate.getTime();
	            	var sortIdInput = oLap.val('sortId');
	            	var orderStatus =  oLap.val('orderStatus');
	            	var productStoreInput = oLap.val('productStore');
	            	var categoryInput = oLap.val('category');
	            	
	            	window.location.href = "exportTurnoverProProStoReportToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&productStore=" + productStoreInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
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
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'productStoreName', type: 'string', width: '15%', cellsalign: 'left'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'category', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPercent)}', datafield: 'percent', type: 'string', width: '10%', hidden: true},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '29%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '13%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
	            ]
	        };
	
	        var configPopup = [
	        	{
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'productStore',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
	                    data : productStoreData3,
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
			            label : '${StringUtil.wrapString(uiLabelMap.BSMSortBy)}',
			            data : sortByData,
			            index: 0
			        }]
			    },
	        ];
	
	        gGridD = OLBIUS.oLapGrid('test2', config, configPopup, 'evaluateTurnoverSM', true);
	
	        gGridD.funcUpdate(function (oLap) {
	        	gFromDateD = oLap.val('from_date'); 
	        	gThruDateD = oLap.val('thru_date'); 
	        	gProductStoreD = oLap.val('productStore'); 
	        	gSortIdD = oLap.val('sortId'); 
	        	gCategoryD = oLap.val('category'); 
	        	gOrderStatusD = oLap.val('orderStatus');
	        	
	            oLap.update({
	                'fromDate': oLap.val('from_date'),
	                'thruDate': oLap.val('thru_date'),
	                'productStore': oLap.val('productStore'),
	                'sortId': oLap.val('sortId'),
	                'category': oLap.val('category'),
	                'orderStatus': oLap.val('orderStatus')
	            });
	        });
	
	        gGridD.init(function () {
	        	gGridD.runAjax();
	        	gChart1D;
	        	gChart2D;
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
	            	var orderStatus =  oLap.val('orderStatus');
	            	var productStoreInput = oLap.val('productStore');
	            	var categoryInput = oLap.val('category');
	            	
	            	window.location.href = "exportTurnoverPPSSMToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&productStore=" + productStoreInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
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

<div class="chart1C">
	<script type="text/javascript" id="PPSAreaChart">
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
	            }
	        };
	
	        var configPopup = [
				{
				    action : 'addDropDownListMultil',
				    params : [{
				        id : 'productStore',
				        label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
				        data : productStoreData2,
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
				        data : sortByData,
				        index: 0
				    }]
				},
	        ];
	
	        gChart1C = OLBIUS.oLapChart('PPSAreaChart', config, null, 'evaluateSalesPPSAreaChart', true, true, OLBIUS.defaultColumnFunc, 0.6);
	
	        gChart1C.funcUpdate(function (oLap) {
	            oLap.update({
	            	'fromDate': gFromDateC,
	                'thruDate': gThruDateC,
	                'productStore': gProductStoreC,
	                'sortId': gSortIdC,
	                'category': gCategoryC,
	                'orderStatus': gOrderStatusC
	            });
	        });
	
	        gChart1C.init(function () {
	        	gChart1C.runAjax();
	        });
		});
	</script>
	<script type="text/javascript" id="PPSPieChartTotal">
	    $(function () {
	        var config = {
	            chart: {
	                plotBackgroundColor: null,
	                plotBorderWidth: null,
	                plotShadow: false
	            },
	            title: {
	                text: '${StringUtil.wrapString(uiLabelMap.BSPPSPIeChartTotal)}'
	            },
	            tooltip: {
	                pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>',
	                valueSuffix: ' VND'
	            },
	            series: [{
	                type: 'pie'
	            }],
	            legend: {
	                layout: 'vertical',
	                align: 'right',
	                verticalAlign: 'middle',
	                borderWidth: 1
	            },
	            plotOptions: {
	                pie: {
	                    allowPointSelect: true,
	                    cursor: 'pointer',
	                    dataLabels: {
	                        enabled: false,
	                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                        style: {
	                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                        }
	                    },
	                    showInLegend: true
	                }
	            }
	        };
	
	        var configPopup = [
				{
				    action : 'addDropDownListMultil',
				    params : [{
				        id : 'productStore',
				        label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
				        data : productStoreData2,
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
				        data : sortByData,
				        index: 0
				    }]
				},
	        ];
	        gChart2C = OLBIUS.oLapChart('PPSPieChartTotal', config, null, 'evaluateTurnoverPPSPieChart2', true, true, OLBIUS.defaultPieFunc, 0.6);
	
	        gChart2C.funcUpdate(function(oLap) {
	            oLap.update({
	            	'fromDate': gFromDateC,
	                'thruDate': gThruDateC,
	                'productStore': gProductStoreC,
	                'sortId': gSortIdC,
	                'category': gCategoryC,
	                'orderStatus': gOrderStatusC
	            });
	        });
	
	        gChart2C.init(function () {
	        	gChart2C.runAjax();
	        });
	    });
    </script>
</div>


<div class="chart2D">
	<script type="text/javascript" id="PPSAreaChartDis">
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
		        }
		    };
		
		    var configPopup = [
				{
				    action : 'addDropDownListMultil',
				    params : [{
				        id : 'productStore',
				        label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
				        data : productStoreData3,
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
				        data : sortByData,
				        index: 0
				    }]
				},
		    ];
		
		    gChart1D = OLBIUS.oLapChart('PPSAreaChartDis', config, null, 'evaluateSalesPPSAreaChartSM', true, true, OLBIUS.defaultColumnFunc, 0.6);
		
		    gChart1D.funcUpdate(function (oLap) {
		        oLap.update({
		        	'fromDate': gFromDateD,
	                'thruDate': gThruDateD,
	                'productStore': gProductStoreD,
	                'sortId': gSortIdD,
	                'category': gCategoryD,
	                'orderStatus': gOrderStatusD
		        });
		    });
		
		    gChart1D.init(function () {
		    	gChart1D.runAjax();
		    });
		});
	</script>
	
	<script type="text/javascript" id="MTLPPSPieChartValue">
	    $(function () {
	        var config = {
	            chart: {
	                plotBackgroundColor: null,
	                plotBorderWidth: null,
	                plotShadow: false
	            },
	            title: {
	                text: '${StringUtil.wrapString(uiLabelMap.BSPPSPIeChartTotal)}'
	            },
	            tooltip: {
	                pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>',
	                valueSuffix: ' VND'
	            },
	            series: [{
	                type: 'pie'
	            }],
	            legend: {
	                layout: 'vertical',
	                align: 'right',
	                verticalAlign: 'middle',
	                borderWidth: 1
	            },
	            plotOptions: {
	                pie: {
	                    allowPointSelect: true,
	                    cursor: 'pointer',
	                    dataLabels: {
	                        enabled: false,
	                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
	                        style: {
	                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
	                        }
	                    },
	                    showInLegend: true
	                }
	            }
	        };
	
	        var configPopup = [
				{
				    action : 'addDropDownListMultil',
				    params : [{
				        id : 'productStore',
				        label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
				        data : productStoreData3,
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
				        data : sortByData,
				        index: 0
				    }]
				},
	        ];
	        gChart2D = OLBIUS.oLapChart('MTLPPSPieChartValue', config, null, 'evaluateTurnoverPPSPCMTL', true, true, OLBIUS.defaultPieFunc, 0.6);
	
	        gChart2D.funcUpdate(function(oLap) {
				
	            oLap.update({
	            	'fromDate': gFromDateD,
	                'thruDate': gThruDateD,
	                'productStore': gProductStoreD,
	                'sortId': gSortIdD,
	                'category': gCategoryD,
	                'orderStatus': gOrderStatusD
	            });
	        });
	
	        gChart2D.init(function () {
	        	gChart2D.runAjax();
	        });
	    });
	</script>
</div>