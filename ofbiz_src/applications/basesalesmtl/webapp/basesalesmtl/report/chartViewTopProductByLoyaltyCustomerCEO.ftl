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
   	
	var loyaltyGroupData2 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
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
	
	var categoryData= [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
	<#if categoryList?exists>
		<#list categoryList as categoryL >
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
		</#list>
	</#if>
</script>

<script type="text/javascript" id="CSColumnChart">
$(function(){
	var dateCurrent = new Date();
	var currentQueryDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
	var config = {
        chart: {
            type: 'column'
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.BSCSColumn)}',
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
        plotOptions: {
            column: {
                stacking: 'normal',
                dataLabels: {
                	rotation: -90,
                    enabled: false,
                    y: 0,
                    color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'yellow',
                    style: {
                    	fontSize: '14px',
                        textShadow: '0 0 3px white'
                    }
                }
            }
        },
    };

    var configPopup = [
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
		        id : 'category',
		        label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
		        data : categoryData,
		        index: 0,
		        hide: true
		    }]
		},
		{
		    action : 'addDateTimeInput',
		    params : [{
		        id : 'from_date',
		        label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
		        value: OLBIUS.dateToString(currentQueryDay)
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

    var columnChart = OLBIUS.oLapChart('CSColumnChart', config, configPopup, 'evaluateCSChart', true, true, OLBIUS.defaultColumnFunc, 0.65);

    columnChart.funcUpdate(function (oLap) {
        oLap.update({
        	'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'loyaltyGroup': oLap.val('loyaltyGroup'),
        });
    });

    columnChart.init(function () {
        columnChart.runAjax();
    });
});
</script>