<style>
	.olbiusChartContainer, .olbiusGridContainer{
		margin-bottom: 50px!important;
	}
</style>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
</script>
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
   	
  	var productStoreData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
  	<#if productStore?exists>
	  	<#list productStore as productStoreL >
	  		productStoreData2.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
	  	</#list>
  	</#if>
  	
	var productStoreData3 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if productStoreDis?exists>
		<#list productStoreDis as productStoreDisL >
			productStoreData3.push({ 'value': '${productStoreDisL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreDisL.storeName)?if_exists}'});
		</#list>
	</#if>
	
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
	
	var categoryData= [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if categoryList?exists>
		<#list categoryList as categoryL >
		<#if categoryL.productCategoryId != "BROWSE_ROOT">
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
		</#if>
		</#list>
	</#if>
	
	var gFromDateD; var gThruDateD; var gProductStoreD; var gCategoryD; var gOrderStatusD; var gGridD; var gChart1D; var gChart2D;
	var gCustomTimeD;
	
	var sourceLimit = [
		{'text': '15', 'value': '15'},
		{'text': '30', 'value': '30'},
		{'text': '50', 'value': '50'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}
    ];
    
    var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];

	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);		
</script>

<div class="grid">
	<script id="test2">
		$(function(){
	        var config = {
	        	sortable: true,
	        	filterable: true,
	        	showfilterrow: true,
	        	service: 'salesOrder',
	            title: '${StringUtil.wrapString(uiLabelMap.BSReportTurnoverProProstoK)}',
	            columns: [
	            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: 'stt', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    	  }
				 	},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'productStoreName', type: 'string', width: '15%', cellsalign: 'left'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'category', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'agencyId', type: 'string', width: '15%',hidden: true},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'agencyName', type: 'string', width: '15%', hidden: true},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPercent)}', datafield: 'percent', type: 'string', width: '10%', hidden: true},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '29%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', datafield: 'unit', type: 'string', width: '10%', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '13%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
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
		            action : 'addDropDownListMultil',
		            params : [{
		                id : 'orderStatus',
		                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
		                data : statusItemData,
		                index: ['ORDER_COMPLETED']
		            }]
		        },
	        ];
	
	        gGridD = OLBIUS.oLapGrid('test2', config, configPopup, 'evaluateTurnoverSM', true);
	
	        gGridD.funcUpdate(function (oLap) {
	        	gFromDateD = oLap.val('from_date_1'); 
	        	gThruDateD = oLap.val('thru_date_1'); 
	        	gProductStoreD = oLap.val('productStore'); 
	        	gCategoryD = oLap.val('category'); 
	        	gOrderStatusD = oLap.val('orderStatus');
	        	gCustomTimeD = oLap.val('customTime');
	        	
	            oLap.update({
	                'fromDate': oLap.val('from_date_1'),
	                'thruDate': oLap.val('thru_date_1'),
	                'productStore': oLap.val('productStore'),
	                'category': oLap.val('category'),
	                'orderStatus': oLap.val('orderStatus'),
	                'customTime': oLap.val('customTime'),
	            });
	        });
	
	        gGridD.init(function () {
	        	gGridD.runAjax();
	        	gChart1D.runAjax();
	        	gChart2D.runAjax();
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
	            	
	            	window.location.href = "exportTurnoverPPSSMToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productStore=" + productStoreInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
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

<div class="chart2D">
	<script type="text/javascript" id="PPSAreaChartDis">
		$(function(){
			var config = {
				service: 'salesOrder',
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
		        plotOptions: {
	                column: {
	                    stacking: 'normal'
	                }
	            },
		        legend: {
		            enabled: true
		        },
		        tooltip: {
		            pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
		        }
		    };
		
		    gChart1D = OLBIUS.oLapChart('PPSAreaChartDis', config, null, 'evaluateSalesPPSAreaChartSM', true, true, OLBIUS.defaultColumnFunc);
		
		    gChart1D.funcUpdate(function (oLap) {
		        oLap.update({
		        	'fromDate': gFromDateD,
	                'thruDate': gThruDateD,
	                'productStore': gProductStoreD,
	                'category': gCategoryD,
	                'orderStatus': gOrderStatusD,
	                'customTime': gCustomTimeD,
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
	        	service: 'salesOrder',
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
	
	        gChart2D = OLBIUS.oLapChart('MTLPPSPieChartValue', config, null, 'evaluateTurnoverPPSPCMTL', true, true, OLBIUS.defaultPieFunc);
	
	        gChart2D.funcUpdate(function(oLap) {
	            oLap.update({
	            	'fromDate': gFromDateD,
	                'thruDate': gThruDateD,
	                'productStore': gProductStoreD,
	                'category': gCategoryD,
	                'orderStatus': gOrderStatusD,
	                'customTime': gCustomTimeD,
	            });
	        });
	
	        gChart2D.init(function () {
	        	gChart2D.runAjax();
	        });
	    });
	</script>
</div>