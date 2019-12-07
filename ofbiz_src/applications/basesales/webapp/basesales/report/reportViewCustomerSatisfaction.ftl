<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
	
	var customDate = [
		{'text': '${StringUtil.wrapString(uiLabelMap.DayLabel)}', 'value': 'dd'},
		{'text': '${StringUtil.wrapString(uiLabelMap.WeekLabel)}', 'value': 'ww'},
		{'text': '${StringUtil.wrapString(uiLabelMap.MonthLabel)}', 'value': 'mm'},
		{'text': '${StringUtil.wrapString(uiLabelMap.QuarterLabel)}', 'value': 'qq'},
		{'text': '${StringUtil.wrapString(uiLabelMap.YearLabel)}', 'value': 'yy'},
		{'text': '${StringUtil.wrapString(uiLabelMap.OtherLabel)}', 'value': 'oo'}
	];
</script>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	<#assign loyaltyGroup = delegator.findList("PartyClassificationGroup", null, null, null, null, false)!>
	var loyaltyGroup = [
	    <#list loyaltyGroup as loyaltyGroupL>
	    {
	    	partyClassificationGroupId : "${loyaltyGroupL.partyClassificationGroupId}",
	    	description: "${StringUtil.wrapString(loyaltyGroupL.get('description', locale))}"
	    },
	    </#list>	
	];
	var listLGDataSource = [];
  	for(var x in loyaltyGroup){
    	var loyaltyGroupDataSource = {
     		text: loyaltyGroup[x].loyaltyGroupName,
     		value: loyaltyGroup[x].loyaltyGroupId,
    	}
    	listLGDataSource.push(loyaltyGroupDataSource);
   	} 
   	
	var loyaltyGroupData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
	<#if loyaltyGroup?exists>
		<#list loyaltyGroup as loyaltyGroupL >
			loyaltyGroupData2.push({ 'value': '${loyaltyGroupL.partyClassificationGroupId?if_exists}', 'text': '${StringUtil.wrapString(loyaltyGroupL.description)?if_exists}'});
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
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
		</#list>
	</#if>
	
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign productStore = delegator.findList("ProductStore", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("payToPartyId", ownerPartyId)), null, null, null, false)>
	var productStore = [
	    <#list productStore as productStoreL>
	    {
	    	productStoreId : "${productStoreL.productStoreId}",
	    	storeName: "${StringUtil.wrapString(productStoreL.get("storeName", locale))}"
	    },
	    </#list>	
	];
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
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

<script id="test">
$(function(){
        var config = {
    		service: 'salesOrder',
            title: '${StringUtil.wrapString(uiLabelMap.BSCustomerSatisfaction)}',
            columns: [
            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  datafield: 'stt', columntype: 'number', width: '3%',
		    	  cellsrenderer: function (row, column, value) {
		    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  }
			 	},   
			 	{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'channel', type: 'string', width: '12%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSChannelType)}', datafield: 'store', type: 'string', width: '12%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSPartyClassificationGroup2)}', datafield: 'classificationGroup', type: 'string', width: '10%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '12%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', minwidth: '29%', width: 'auto'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'volume', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', datafield: 'unit', type: 'string', width: '10%'},
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
                before: 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                    hide: true,
                }],
                after: 'from_date'
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
                    id : 'loyaltyGroup',
                    label : '${StringUtil.wrapString(uiLabelMap.BSPartyClassificationGroup2)}',
                    data : loyaltyGroupData2,
                    index: 0
                }]
            },
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
                    id : 'productStore',
                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
                    data : productStoreData2,
                    index: 0
                }]
            },
        ];

        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateCSGrid', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'loyaltyGroup': oLap.val('loyaltyGroup'),
                'customTime': oLap.val('customTime'),
                'store': oLap.val('productStore'),
                'channel': oLap.val('storeChannel'),
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
        });
    });
</script>

<script type="text/javascript" id="LGPieChart">
	$(function () {
		var config = {
				chart: {
					plotBackgroundColor: null,
					plotBorderWidth: null,
					plotShadow: false
				},
				title: {
					text: '${StringUtil.wrapString(uiLabelMap.BSGLChart)}'
				},
				tooltip: {
					pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>'
				},
				series: [{
					type: 'pie'
				}],
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
						}
					}
				}
		};
		
		var configPopup = [
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
		var TSPC = OLBIUS.oLapChart('LGPieChart', config, configPopup, 'evaluateLGPieChart', true, true, OLBIUS.defaultPieFunc);
		
		TSPC.funcUpdate(function(oLap) {
			
			oLap.update({
				'fromDate': oLap.val('from_date'),
				'thruDate': oLap.val('thru_date'),
			});
		});
		
		TSPC.init(function () {
			TSPC.runAjax();
		});
	});
</script>

<script type="text/javascript" id="LGVPieChart">
	$(function () {
	    var config = {
	        chart: {
	            plotBackgroundColor: null,
	            plotBorderWidth: null,
	            plotShadow: false
	        },
	        title: {
	            text: '${StringUtil.wrapString(uiLabelMap.BSGLChart)}'
	        },
	        tooltip: {
	            pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>'
	        },
	        series: [{
	            type: 'pie'
	        }],
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
	                }
	            }
	        }
	    };
	
	    var configPopup = [
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
	    var TSPC = OLBIUS.oLapChart('LGVPieChart', config, configPopup, 'evaluateLGVPieChart', true, true, OLBIUS.defaultPieFunc);
	
	    TSPC.funcUpdate(function(oLap) {
			
	        oLap.update({
	            'fromDate': oLap.val('from_date'),
	            'thruDate': oLap.val('thru_date'),
	        });
	    });
	
	    TSPC.init(function () {
	        TSPC.runAjax();
	    });
	});
</script>