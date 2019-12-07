<script type="text/javascript" src="/salesresources/js/popup_extend_grid.js"></script>
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
	<#assign distributorId = userLogin.partyId />
	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)>
	<#assign agency = delegator.findList("PartyFromAndNameRelOutletDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("distributorId", distributorId, "roleTypeIdFrom", "CUSTOMER")), null, null, null, false)>
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
    
	var agency = [
		<#if agency?exists>
			<#list agency as itemAgency>
				{
					partyId: "${itemAgency.partyId?if_exists}",
					groupName: "${StringUtil.wrapString(itemAgency.get("groupName", locale)?if_exists)}"
				},
			</#list>
		</#if>
	];
	var listAgencyDataSource2 = [];	
	for(var x in agency){
			var agencyDataSource2 = {
				text: agency[x].groupName,
				value: agency[x].partyId,
			}
			listAgencyDataSource2.push(agencyDataSource2);
		}
    
	var listPSDataSource2 = [];
	for(var x in productStoreDis){
		var productStoreDataSource2 = {
			text: productStoreDis[x].storeName,
			value: productStoreDis[x].productStoreId,
		}
		listPSDataSource2.push(productStoreDataSource2);
	};
	
	var listAgencySource = [];
	for(var x in agency){
		var agencyDataSource = {
			text: agency[x].partyId,
			value: agency[x].groupName,
		}
		listAgencySource.push(agencyDataSource);
	};
	
	var listPSDataSource = [];
  	for(var x in productStore){
    	var productStoreDataSource = {
     		text: productStore[x].storeName,
     		value: productStore[x].productStoreId,
    	}
    listPSDataSource.push(productStoreDataSource);
   	}; 
   	
  	var productStoreData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
  	<#if productStore?exists>
	  	<#list productStore as productStoreL >
	  		productStoreData2.push({ 'value': '${productStoreL.productStoreId?if_exists}', 'text': '${StringUtil.wrapString(productStoreL.storeName)?if_exists}'});
	  	</#list>
  	</#if>
  	
	var productStoreData3 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];

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
	
	var gFromDateD; var gThruDateD; var gProductStoreD; var gSortIdD; var gCategoryD; var gOrderStatusD; var gGridD; var gChart1D; var gChart2D;
	var gOtherTime; var gAgency;
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
		
	var listOrderStatusSource = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMCreated)}', 'value': 'ORDER_CREATED'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMApproved)}', 'value': 'ORDER_APPROVED'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMCompleted)}', 'value': 'ORDER_COMPLETED'},
	];
</script>

<div class="grid">
	<script id="test2">
		$(function(){
	        var config = {
	        	sortable: true,
	        	filterable: true,
	        	showfilterrow: true,
	            title: '${StringUtil.wrapString(uiLabelMap.BSTurnoverReport)}',
	            service: 'salesOrder',
	            columns: [
	            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: 'stt', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    	  }
				 	},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'productStoreName', type: 'string', width: '15%', cellsalign: 'left', hidden: true},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSMAgency)}', datafield: 'agencyId', type: 'string', width: '12%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'agencyName', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'category', type: 'string', width: '15%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPercent)}', datafield: 'percent', type: 'string', width: '10%', hidden: true},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '10%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '30%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '7%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', datafield: 'unit', type: 'string', width: '7%', filterable: false},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '11%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
	            ]
	        };
	
	        var configPopup = [
	        	{
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'from_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
	                    value: OLBIUS.dateToString(currentFirstDay),
	                    hide: true
	                }],
	                before: 'thru_date'
	            },
	            {
	                action : 'addDateTimeInput',
	                params : [{
	                    id : 'thru_date',
	                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
	                    value: OLBIUS.dateToString(cur_date),
	                    hide: true
	                }],
	                after: 'from_date'
	            },
	        	{
	                action : 'addDropDownListMultil',
	                params : [{
	                    id : 'productStore',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
	                    data : productStoreData3,
	                    index: 0,
	                    hide: true
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
		                label : '${StringUtil.wrapString(uiLabelMap.BSOrderStatus)}',
		                data : listOrderStatusSource,
		                index: ['ORDER_COMPLETED'],
		            }]
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
	                            popup.show('from_date');
	                            popup.show('thru_date');
	                        } else {
	                        	popup.hide('from_date');
	                            popup.hide('thru_date');
	                        }
	                        popup.resize();
	                    });
	                }
			    },
			    {
					action : 'addJqxGridMultil',
					params : [{
						id : 'agency',
						title1: '${StringUtil.wrapString(uiLabelMap.BSMAgency)}',
						title2: '${StringUtil.wrapString(uiLabelMap.BSMAgencyName)}',  
						label : '${StringUtil.wrapString(uiLabelMap.BSMAgency)}',
						data : listAgencyDataSource2,
					value: []
				}]
			},
	        ];
	
	        gGridD = OLBIUS.oLapGrid('test2', config, configPopup, 'evaluateTurnoverDistributor', true);
	
	        gGridD.funcUpdate(function (oLap) {
	        	gFromDateD = oLap.val('from_date'); 
	        	gThruDateD = oLap.val('thru_date'); 
	        	gProductStoreD = oLap.val('productStore'); 
	        	gCategoryD = oLap.val('category'); 
	        	gOrderStatusD = oLap.val('orderStatus');
	        	gOtherTime = oLap.val('customTime');
	        	gAgency = oLap.val('agency');
	        	
	            oLap.update({
	                'fromDate': gFromDateD,
	                'thruDate': gThruDateD,
	                'productStore': gProductStoreD,
	                'category': gCategoryD,
	                'orderStatus': gOrderStatusD,
	                'customTime': gOtherTime,
	                'agency': gAgency,
	            });
	        });
	
	        gGridD.init(function () {
	        	gGridD.runAjax();
	        	gChart1D.runAjax();
	        	gChart2D.runAjax();
	        }, false, function(oLap){
	        	var dataAll = oLap.getAllData();
	        	if(dataAll.length != 0){
	            	
	        	}else{
	        	}
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
		
		    gChart1D = OLBIUS.oLapChart('PPSAreaChartDis', config, null, 'evaluateSalesProDisAreaChart', true, true, OLBIUS.defaultColumnFunc);
		
		    gChart1D.funcUpdate(function (oLap) {
		        oLap.update({
		        	'fromDate': gFromDateD,
	                'thruDate': gThruDateD,
	                'productStore': gProductStoreD,
	                'category': gCategoryD,
	                'orderStatus': gOrderStatusD,
	                'customTime': gOtherTime,
	                'agency': gAgency,
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
	
	        gChart2D = OLBIUS.oLapChart('MTLPPSPieChartValue', config, null, 'evaluateTurnoverProDisPCMTL', true, true, OLBIUS.defaultPieFunc);
	
	        gChart2D.funcUpdate(function(oLap) {
	            oLap.update({
	            	'fromDate': gFromDateD,
	                'thruDate': gThruDateD,
	                'productStore': gProductStoreD,
	                'category': gCategoryD,
	                'orderStatus': gOrderStatusD,
	                'customTime': gOtherTime,
	                'agency': gAgency,
	            });
	        });
	
	        gChart2D.init(function () {
	        	gChart2D.runAjax();
	        });
	    });
	</script>
</div>
