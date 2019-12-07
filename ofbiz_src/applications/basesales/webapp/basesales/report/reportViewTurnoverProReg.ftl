<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
<#assign salesRegion = delegator.findByAnd("PartyAcctgPreference",  {"partyId" : "${ownerPartyId}"}, null, false)>
	var salesRegion = [
	    <#list salesRegion as salesRegionL>
	    {
	    	partyId : "${salesRegionL.partyId}",
	    },
	    </#list>	
	];
	
	var listRegionDataSource = [];
  	for(var x in salesRegion){
    	var regionDataSource = {
     		text: salesRegion[x].partyId,
     		value: salesRegion[x].partyId,
    	}
    listRegionDataSource.push(regionDataSource);
   	} 
   	
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
	
	var gOrderStatus; var gFromDate; var gThruDate; var gRegion; var gGrid; var gChart;
	
	var listOrderStatusSource2 = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMSent)}', 'value': 'ORDER_SENT'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMCreated)}', 'value': 'ORDER_CREATED'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMProcessing)}', 'value': 'ORDER_PROCESSING'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMIntransit)}', 'value': 'ORDER_IN_TRANSIT'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMCompleted)}', 'value': 'ORDER_COMPLETED'},
	];
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

<script type="text/javascript" id="revenueByRegion">
    $(function () {
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSProductReport)}',
            id: 'revenueByRegion',
            olap: 'evaluateRevenueByRegion',
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  	datafield: 'stt', columntype: 'number', width: '3%',
		    	  	cellsrenderer: function (row, column, value) {
		    		  	return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  	}
			 	}, 
               	{
                    text: '${StringUtil.wrapString(uiLabelMap.BSMRegion)}',
                    datafield: {name: 'region', type: 'string'},
                    width: '16%'
                }, 
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}',
                    datafield: {name: 'category', type: 'string'},
                    width: '13%'
                }, 
               	{
                    text: '${StringUtil.wrapString(uiLabelMap.BSMProductCode)}',
                    datafield: {name: 'productCode', type: 'string'},
                    width: '11%'
                }, 
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}',
                    datafield: {name: 'productName', type: 'string'},
                    width: '27%'
                }, 
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}',
                    datafield: {name: 'salesVolume', type: 'string'}, 
                    width: '10%',
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                },
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSMUnit)}',
                    datafield: {name: 'unit', type: 'string'},
                    width: '8%'
                }, 
             	{
                    text: '${StringUtil.wrapString(uiLabelMap.BSSalesValue)}',
                    datafield: {name: 'salesValue', type: 'string'}, 
                    width: '12%',
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                }, 
            ],
            popup: [
            	{
                    group: 'dateTime',
                    id: 'dateTime'
               	},
               	<#if region?if_exists?index_of(",", 0) != -1>
                {
                	action: 'jqxGridMultiple',
                	params: {
                		id : 'region',  
                		label : '${StringUtil.wrapString(uiLabelMap.BSMRegion)}',
                		grid: {
                			source: ${StringUtil.wrapString(region)},
                			id: "partyId",
                			width: 250,
                			sortable: true,
                			pagesize: 5,
                			columnsresize: true,
                			pageable: true,
                			altrows: true,
                			showfilterrow: true,
                			filterable: true,
                			columns: [
                			          { text: "${StringUtil.wrapString(uiLabelMap.BSMRegionCode)}", datafield: 'partyId', width: 150, hidden: true }, 
                			          { text: "${StringUtil.wrapString(uiLabelMap.BSMRegion)}", datafield: 'description' }
    			          	]
                		}
                	}
                },</#if>
               	<#if categories?if_exists?index_of(",", 0) != -1>
                {
                	action: 'jqxGridMultiple',
                	params: {
                		id : 'categories',  
                		label : '${StringUtil.wrapString(uiLabelMap.BSCategory)}',
                		grid: {
                			source: ${StringUtil.wrapString(categories)},
                			id: "productCategoryId",
                			width: 550,
                			sortable: true,
                			pagesize: 5,
                			columnsresize: true,
                			pageable: true,
                			altrows: true,
                			showfilterrow: true,
                			filterable: true,
                			columns: [
                			          { text: "${StringUtil.wrapString(uiLabelMap.DmsProdCatalogId)}", datafield: 'productCategoryId', width: 150 }, 
                			          { text: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}", datafield: 'categoryName' }
                			          ]
                		}
                	}
                },</#if>
            	<#if products?if_exists?index_of(",", 0) != -1>
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'product',  
        	            label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
        	            grid: {
        	            	source: ${StringUtil.wrapString(products)},
        	            	id: "productId",
        	            	width: 500,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductId)}", datafield: 'productCode', width: 150 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductName)}", datafield: 'productName' }
	            	        ]
        	            }
                    }
                },</#if>
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'orderStatus',  
        	            label : '${StringUtil.wrapString(uiLabelMap.BSMOrderStatus)}',
        	            grid: {
        	            	source: ${StringUtil.wrapString(orderStatus1)},
        	            	id: "statusId",
        	            	width: 250,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSStatusId)}", datafield: 'statusId', width: 150, hidden: true }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSMOrderStatus)}", datafield: 'description' }
	            	        ]
        	            }
                    }
                },
            ],
            apply: function (grid, popup) {
                return $.extend({
                    product: popup.val('product'),
                    categories: popup.val('categories'),
                    region: _.isEmpty(popup.val("region"))?_.pluck(${StringUtil.wrapString(region)}, "partyId"):popup.val("region"),
                    group: ['facility', 'product', 'uom'],
                    orderStatus: popup.val('orderStatus'),
                }, popup.group('dateTime').val());
            },
            excel: true
        };

        var grid = OlbiusUtil.grid(config);

    });
</script>