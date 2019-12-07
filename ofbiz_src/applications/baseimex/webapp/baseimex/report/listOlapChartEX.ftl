<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/poresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/initDashboard.js"></script>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>
<script>
	//Prepare for product data
	<#assign products = delegator.findList("Product", null, null, null, null, false)>
	var productData = [
		<#if products?exists>
			<#list products as item>
				{
					productId: "${item.productCode?if_exists}",
					<#assign s = StringUtil.wrapString(item.get("productName", locale)?if_exists)/>
					productName: "${s}"
				},
			</#list>
		</#if>
	];
	
	<#assign customTimePeriodsYeah = delegator.findList("CustomTimePeriod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", "COMMERCIAL_YEAR")), null, null, null, false)>
	var customTimePeriodsYeahData = [
							<#if customTimePeriodsYeah?exists>
								<#list customTimePeriodsYeah as item>
									{
										customTimePeriodId: "${item.customTimePeriodId?if_exists}",
										periodName: "${StringUtil.wrapString(item.get("periodName", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapcustomTimePeriodsYeahData = {
			<#if customTimePeriodsYeah?exists>
				<#list customTimePeriodsYeah as item>
					"${item.customTimePeriodId?if_exists}": "${StringUtil.wrapString(item.get("periodName", locale)?if_exists)}",
				</#list>
			</#if>	
	};
	
	<#assign customTimePeriodsQuater = delegator.findList("CustomTimePeriod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", "SALES_QUARTER", "parentPeriodId", "9100")), null, null, null, false)>
	var customTimePeriodsQuaterData = [
							<#if customTimePeriodsQuater?exists>
								<#list customTimePeriodsQuater as item>
									{
										customTimePeriodId: "${item.customTimePeriodId?if_exists}",
										periodName: "${StringUtil.wrapString(item.get("periodName", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapcustomTimePeriodsQuaterData = {
			<#if customTimePeriodsQuater?exists>
				<#list customTimePeriodsQuater as item>
					"${item.customTimePeriodId?if_exists}": "${StringUtil.wrapString(item.get("periodName", locale)?if_exists)}",
				</#list>
			</#if>	
	};
	
	<#assign customTimePeriodsMonth = delegator.findList("CustomTimePeriod", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("parentPeriodId", "9000", "periodTypeId", "MONTHLY")), null, null, null, false)>
	var customTimePeriodsMonthData = [
							<#if customTimePeriodsMonth?exists>
								<#list customTimePeriodsMonth as item>
									{
										customTimePeriodId: "${item.customTimePeriodId?if_exists}",
										periodName: "${StringUtil.wrapString(item.get("periodName", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapcustomTimePeriodsMonthData = {
			<#if customTimePeriodsMonth?exists>
				<#list customTimePeriodsMonth as item>
					"${item.customTimePeriodId?if_exists}": "${StringUtil.wrapString(item.get("periodName", locale)?if_exists)}",
				</#list>
			</#if>	
	};
	<#assign productCategorys = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"), null, null, null, false)>
</script>
<script type="text/javascript" id="pucharOrderChart">
    $(function () {
    	var listProductCategorys = [
    		<#if productCategorys?exists>
    			<#list productCategorys as item>
    				{
    					productCategoryId: "${item.productCategoryId?if_exists}",
    					categoryName: "${StringUtil.wrapString(item.get("categoryName", locale)?if_exists)}"
    				},
    			</#list>
    		</#if>
        ];
    	var listProductDataSource = [];	
    	var listStatusDataSource = [];
    	var listProductCategoryDataSource = [];
    	for(var x in productData){
			var productDataSource = {
				text: productData[x].productName,
				value: productData[x].productId,
			}
			listProductDataSource.push(productDataSource);
		}
    	for(var i in statusItemData){
			var statusDataSource = {
				text: statusItemData[i].description,
				value: statusItemData[i].statusId,
			}
			listStatusDataSource.push(statusDataSource);
		}
    	for(var i in listProductCategorys){
			var productCategorys = {
				text: listProductCategorys[i].categoryName,
				value: listProductCategorys[i].productCategoryId,
			}
			listProductCategoryDataSource.push(productCategorys); 
		}
    	var optionLimitAcset = [
			 {
				text: "5",
				value: 5
			 },
    	     {
    	    	 text: "10",
    	    	 value: 10
    	     },
    	     {
    	    	 text: "15",
    	    	 value: 15
    	     },
    	     {
    	    	 text: "20",
    	    	 value: 20
    	     },
    	     {
    	    	 text: "25",
    	    	 value: 25
    	     }
    	];
    	var optionFilterData = [
   			 {
   				text: '${StringUtil.wrapString(uiLabelMap.POProductsAreBoughtAtMost)}',
   				value: "FILTER_MAX"
   			 },
       	     {
       	    	 text: '${StringUtil.wrapString(uiLabelMap.POProductArePurchasedAtLeast)}',
       	    	 value: "FILTER_MIN" 
       	     },
       	];
    	var config = {
                chart: {
                    type: 'column'
                },
                title: {
                    text: '${StringUtil.wrapString(uiLabelMap.POChartPurchaseOrder)}',
                    x: -20 //center
                },
                xAxis: {
                    type: 'category',
                    labels: {
                        rotation: -45,
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
                    enabled: false
                },
                tooltip: {
                    pointFormat: '{point.y}'
                }
            };
    	
    	var dateCurrent = new Date();
    	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
    	var configPopup = [
    		   	{
                    action : 'addDateTimeInput',
                    params : [{
                        id : 'from_date',
                        label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                        value: OLBIUS.dateToString(currentFirstDay)
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
        			action : 'addJqxGridMultil',
        	        params : [{
        	            id : 'productId',
        	            title1: '${StringUtil.wrapString(uiLabelMap.ProductCode)}',
        	            title2: '${StringUtil.wrapString(uiLabelMap.ProductName)}',  
        	            label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
        	            data : listProductDataSource,
        	            value: []
        	        }]
                },
                {
	                action : 'addJqxGridMultil',
	    	        params : [{
	    	            id : 'categoryId',
	    	            title1: '${StringUtil.wrapString(uiLabelMap.POCategoryId)}',
	    	            title2: '${StringUtil.wrapString(uiLabelMap.POCategoryName)}',  
	    	            label : '${StringUtil.wrapString(uiLabelMap.DmsProductCatalogs)}',
	    	            data : listProductCategoryDataSource,
	    	            value: []
	    	        }]
                },	
                {
                    action : 'addDropDownList',
                    params : [{
                        id : 'limitId',
                        label : '${StringUtil.wrapString(uiLabelMap.POTopProduct)}',
                        data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(optionLimitAcset),
                        index: 4
                    }]
                },
                {
                    action : 'addDropDownList',
                    params : [{
                        id : 'filterTypeId',
                        label : '${StringUtil.wrapString(uiLabelMap.POTypeFilter)}',
                        data : optionFilterData,
                        index: 1,
                    }]
                },
   			];
    	
    	var purchaseOrderChartOLap = OLBIUS.oLapChart('pucharOrderChart', config, configPopup, 'purchaseOrderChart', true, true, OLBIUS.defaultColumnFunc);

    	purchaseOrderChartOLap.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'productId': oLap.val('productId'),
                'categoryId': oLap.val('categoryId'), 
                'limitId': oLap.val('limitId'),
                'filterTypeId': oLap.val('filterTypeId'),
                'typeChart': 'quantity',
            });
        });
    	purchaseOrderChartOLap.init(function () {
    		purchaseOrderChartOLap.runAjax();
        });
    	
    });
    
</script>