<script type="text/javascript">
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
	var listPSDataSource = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
  	for(var x in productStore){
    	var productStoreDataSource = {
     		text: productStore[x].storeName,
     		value: productStore[x].productStoreId,
    	}
    listPSDataSource.push(productStoreDataSource);
   	} 
   	
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
			channelData2.push({ 'value': '${salesChannelL.enumId?if_exists}', 'text': '${StringUtil.wrapString(salesChannelL.description)?if_exists}'});
		</#list>
	</#if>
  	
  	var gStore; var gGrid; var gChart; var gChannel; var gType1; var gType2;
  	var gMonth1; var gMonth2; var gQuarter1; var gQuarter2; var gYear1; var gYear2;
  	
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
	
	var salesQuarterData = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSFirstQuarter)}', 'value': '1'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSecondQuarter)}', 'value': '2'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSThirdQuarter)}', 'value': '3'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSFourthQuarter)}', 'value': '4'},
	];
	
	var datee = new Date();
	var getMonth = datee.getMonth();
	var getYear = datee.getFullYear();
	var yearIndex = 0;
    
    for(var i = 0; i <= salesYearData.length; i++){
    	var yearValue = salesYearData[i].value;
    	if(getYear && yearValue && getYear == yearValue){
    		yearIndex = i;
    		break;
    	}
	}
	
	function quarter_of_the_year(date) {
		var month = date.getMonth() + 1;
	    return (Math.ceil(month / 3));
    }
    
	var currentQuarter = quarter_of_the_year(datee) - 1;
	
	var date_type_source_custom= [
    	{text:'${StringUtil.wrapString(uiLabelMap.olap_month_1)}', value: 'MONTH_YEAR'}, 
        {text:'${StringUtil.wrapString(uiLabelMap.olap_month_2)}', value: 'MONTH'}, 
        {text: '${StringUtil.wrapString(uiLabelMap.olap_quarter_1)}', value: 'QUARTER_YEAR'},
        {text: '${StringUtil.wrapString(uiLabelMap.olap_quarter_2)}', value: 'QUARTER'},
        {text: '${StringUtil.wrapString(uiLabelMap.olap_year_1)}', value: 'YEAR'}
	];
	
	var type_source= [
    	{text:'${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', value: 'STORE'}, 
        {text:'${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}', value: 'CHANNEL'}, 
	];
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

 <script id="test">
$(function(){
		var dateCurrent = new Date();
		var currentQueryDay3 = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
		var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 7);	
		var currentQueryDay2 = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 8);	
		
        var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BSSalesGrowthReport)}',
            service: 'salesOrder',
            columns: [
	            { text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  	datafield: 'stt', columntype: 'number', width: '3%',
		    	 	 cellsrenderer: function (row, column, value) {
		    		  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  	}
			 	},
                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'productStoreName', type: 'string', width: '12%', cellsalign: 'center'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', minWidth: '30%'},
                { text: '${StringUtil.wrapString(uiLabelMap.CommonPeriod)} 1', datafield: 'quantity1', type: 'number', width: '13%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.CommonPeriod)} 2', datafield: 'quantity2', type: 'number', width: '13%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSPercentPeriod)}', datafield: 'pvp', type: 'number', width: '13%', cellsformat: 'p2', cellsalign: 'right',
                	cellsrenderer: function (row, column, value) {
                		if(parseInt(value) < 100){
			    		  	return '<div class=\"background-important-nd text-right\" style=margin:4px;>' + (value) + ' %</div>';
	                	} else {
	                		return '<div class=\"text-right\" style=margin:4px;>' + (value) + ' %</div>';
	                	}
	    	  		}
                },
                { text: '${StringUtil.wrapString(uiLabelMap.PercentVersus)}', datafield: 'pvs', width: '13%',
                	cellsrenderer: function (row, column, value) {
                		if (value) {
                			if(parseInt(value) < 0){
    			    		  	return '<div class=\"background-important-nd text-right\" style=margin:4px;>' + (value) + ' %</div>';
    	                	} else {
    	                		return '<div class=\"text-right\" style=margin:4px;>' + (value) + ' %</div>';
    	                	}
						} else {
							return '<div class=\"text-right\" style=margin:4px;>NA</div>';
						}
                	}
                }
            ]
        };

        var configPopup = [
            	{
				action : 'addDropDownList',
				params : [{
					id : 'type2',
					label : '${StringUtil.wrapString(uiLabelMap.BSStatistic)}',
					data : type_source,
					index: 0
				}],
				 event : function(popup) {
                    popup.onEvent('type2', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('type2', args.index);
                        var filter = item.value;
                        popup.hide('productStore');
                        popup.hide('channel');
                        if(filter == 'STORE') {
	                   popup.show('productStore');
                        } else if(filter == 'CHANNEL') {
                    	popup.show('channel');
                        } 
                    popup.resize();
                    });
                }
			},
				{
				action : 'addDropDownList',
				params : [{
					id : 'typee',
					label : '${StringUtil.wrapString(uiLabelMap.olap_dateType)}',
					data : date_type_source_custom,
					index: 0
				}],
				 event : function(popup) {
                    popup.onEvent('typee', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('typee', args.index);
                        var filter = item.value;
                        popup.hide('monthh');
                        popup.hide('monthh2');
                        popup.hide('quarterr');
                        popup.hide('quarterr2');
                        popup.hide('yearr');
                        popup.hide('yearr2');
                        if(filter == 'MONTH_YEAR') {
	                   popup.show('monthh');
	                        popup.show('monthh2');
	                        popup.show('yearr');
                        } else if(filter == 'MONTH') {
                    	popup.show('monthh');
	                        popup.show('monthh2');
	                        popup.show('yearr');
	                        popup.show('yearr2');
                        } else if(filter == 'QUARTER_YEAR') {
                   		popup.show('quarterr');
	                        popup.show('quarterr2');
	                        popup.show('yearr');
        				} else if(filter == 'QUARTER') {
        					popup.show('quarterr');
	                        popup.show('quarterr2');
	                        popup.show('yearr');
	                        popup.show('yearr2');
        				}	else if(filter == 'YEAR') {
							popup.show('yearr');
	                        popup.show('yearr2');
        				}	
                    popup.resize();
                    });
                }
			},
			{
                action : 'addDropDownList',
                	params : [{
	                id : 'productStore',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}',
	                    data : listPSDataSource,
	                    index: 0
                	}]
            	},
				{
                action : 'addDropDownList',
                	params : [{
	                id : 'channel',
	                    label : '${StringUtil.wrapString(uiLabelMap.BSSalesChannelType)}',
	                    data : channelData2,
	                    index: 0,
	                    hide: true
                	}]
            	},
			{
				action : 'addDropDownList',
				params : [{
					id : 'yearr',
					label : '${StringUtil.wrapString(uiLabelMap.BSYear)}',
					data : salesYearData,
					index: yearIndex,
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
			{
				action : 'addDropDownList',
				params : [{
					id : 'monthh2',
					label : '${StringUtil.wrapString(uiLabelMap.BSMonth2)}',
					data : salesMonthData,
					index: getMonth,
				}]
			},
			{
				action : 'addDropDownList',
				params : [{
					id : 'quarterr',
					label : '${StringUtil.wrapString(uiLabelMap.BSQuarter)}',
					data : salesQuarterData,
					index: 0,
					hide: true
				}]
			},
			{
				action : 'addDropDownList',
				params : [{
					id : 'quarterr2',
					label : '${StringUtil.wrapString(uiLabelMap.BSQuarter2)}',
					data : salesQuarterData,
					index: currentQuarter,
					hide: true
				}]
			},
			{
				action : 'addDropDownList',
				params : [{
					id : 'yearr2',
					label : '${StringUtil.wrapString(uiLabelMap.BSYear2)}',
					data : salesYearData,
					index: yearIndex,
					hide: true
				}]
			},
        ];

        gGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesGrowthOlapGrid', true);

        gGrid.funcUpdate(function (oLap) {
        	gStore = oLap.val('productStore');
        	gChannel = oLap.val('channel');
        	gMonth1 = oLap.val('monthh');
        	gMonth2 = oLap.val('monthh2');
        	gQuarter1 =  oLap.val('quarterr');
        	gQuarter2 =  oLap.val('quarterr2');
        	gYear1 = oLap.val('yearr');
        	gYear2 = oLap.val('yearr2');
    		gType = oLap.val('typee');
    		gType2 = oLap.val('type2');
	    	
			oLap.update({
				'productStore': oLap.val('productStore'),
				'channel': oLap.val('channel'),
				'monthh': oLap.val('monthh'),
				'monthh2': oLap.val('monthh2'),
				'quarterr': oLap.val('quarterr'),
				'quarterr2': oLap.val('quarterr2'),
				'yearr': oLap.val('yearr'),
				'yearr2': oLap.val('yearr2'),
				'typee': oLap.val('typee'),
				'type2': oLap.val('type2'),
			});
        });

        gGrid.init(function () {
        	gGrid.runAjax();
        	// gChart.runAjax();
        });
    });
</script>
<#--
<script type="text/javascript" id="test2">
	$(function(){
		var config = {
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSSalesGrowthReportChart)}',
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
                    color: '#808080'
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
                shared: true
            },
        };

        gChart = OLBIUS.oLapChart('test2', config, null, 'evaluateSalesGrowthOlapChart', true, true, OLBIUS.defaultColumnFunc);

        gChart.funcUpdate(function (oLap) {
            oLap.update({
                'productStore': gStore,
					'channel': gChannel,
					'monthh': gMonth1,
					'monthh2': gMonth2,
					'quarterr': gQuarter1,
					'quarterr2': gQuarter2,
					'yearr': gYear1,
					'yearr2': gYear2,
					'typee': gType,
					'type2': gType2,
            });
        });

        gChart.init(function () {
        	gChart.runAjax();
        });
	});
</script>
-->